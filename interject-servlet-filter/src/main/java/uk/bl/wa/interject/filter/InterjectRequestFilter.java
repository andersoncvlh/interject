/**
 * 
 */
package uk.bl.wa.interject.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


/**
 *
 * Redirect specific Mime Types
 *
 * This version just uses the URL.
 * 
 * Want to use URL, server mime type and content sniffing. a la:
 * 
 * http://stackoverflow.com/questions/8933054/how-to-log-response-content-from-a-java-web-server
 * 
 * See also
 * 
 * http://stackoverflow.com/a/8499001/6689
 * 
 * But see below for notes on the complications that ensue.
 * 
 * @author Andrew.Jackson@bl.uk
 * 
 */
public class InterjectRequestFilter implements Filter {

	protected static Logger logger = LogManager.getLogger(InterjectRequestFilter.class);
	public List<String> mimeTypes = new ArrayList<String>();
	public List<String> mimeRedirects = new ArrayList<String>();
	public Properties propertiesConfig;


	/**
	 * 
	 */
	public InterjectRequestFilter() {
		logger.info("Starting up...");
		Config conf = ConfigFactory.load();
		for( Config s : conf.getConfigList("interject")) {
			System.out.println("Service "+s.getString("endpoint"));
			System.out.println("Types: "+s.getStringList("types"));
		}
		
	}


	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) req;
		HttpServletResponse httpResponse = (HttpServletResponse) res;

		byte[] copy = null;
		
		// Perform the rest of the processing, so we have access to the payload:
		//
		// FIXME Doing this here does not work, as the initial response (e.g. status code etc is already 
		// committed by the time we exit this function.
		// To make this work, we need to buffer the response until we can sample the body and then
		// clear/rewrite the response before continuing.
		// A fairly similar example is included in the form of the HttpServletResponseCopier class.
		//

		logger.info("Checking MIME Type for: "+httpRequest.getRequestURL());
		Tika tika = new Tika();
		String strMime = null;
		strMime = tika.detect(httpRequest.getRequestURL().toString());
		logger.info("Determined Content-Type to be: "+strMime);
		

		// Default to NOT returning the original without question:
		boolean originalRequested = false;

		// Grab the Accept header:
		Header accept = new BasicHeader("Accept", httpRequest.getHeader("Accept"));
		// Check the Accept header, if any:
		if( accept != null ) {
			logger.info("Got Accept: "+accept.getValue());
			for( HeaderElement e : accept.getElements() ) {
				if( e.getName().equals(strMime)) {
					logger.info("Accept header matched original format.");
					originalRequested = true;
				}
			}
		}

		// Check for a special no-transform parameter:
		if( httpRequest.getParameter("interject_disabled") != null ) {
			boolean interjectDisabled = Boolean.parseBoolean( httpRequest.getParameter("interject_disabled") );
			if( interjectDisabled )
				originalRequested = true;
		}


		// If we are not just passing through the original:
		if( originalRequested == false ) {
			try {
			// Now decide what to do, based on the determined type:
			for(int i = 0;i < mimeTypes.size();i++){
				String mimeType = mimeTypes.get(i);
				if(mimeType.equalsIgnoreCase(strMime)){
					logger.info("Redirecting: "+httpRequest.getRequestURL() + " to: " +  mimeRedirects.get(i));
					httpResponse.sendRedirect(httpResponse.encodeRedirectURL(
							mimeRedirects.get(i) 
							+ "?url="+httpRequest.getRequestURL().toString() 
							+ "&sourceContentType=" + strMime
							));
					return;
				}
			}
			} catch (Exception e ) {
				logger.error("Failed: "+e);
			}
		}
		
		// Going slightly mad: this seems to work, down here, but does not work if I do it earlier in this method. NO IDEA WHY.
		// It seems the precise timing of when buffers get written to depends on things I can't really control.

		// Set up the response copier:
		HttpServletResponseCopier hsrc = new HttpServletResponseCopier(httpResponse);
		
		// Pass down the chain:
		res.setBufferSize(0);
		chain.doFilter(req, hsrc);
		
		// Flush the original response, and grab a copy:
		String currentType = httpResponse.getContentType();
		res.flushBuffer();
		copy = hsrc.getCopy();

		logger.info("Copier got: "+copy.length);
		logger.info("Copier got: "+new String(copy).substring(0, 5));
		
		// Flush
		hsrc.flushBuffer();
		hsrc.reallyFlush();

		// Sniff the type of the payload:
		if( copy != null ) {
			Metadata md = new Metadata();
			md.add(Metadata.RESOURCE_NAME_KEY, httpRequest.getRequestURL().toString());
			//md.add(Metadata.CONTENT_TYPE, currentType);
			strMime = tika.detect( new ByteArrayInputStream(copy),md);
		}
		logger.info("Determined Content-Type to be: "+strMime);

	}

	@Override
	public void destroy(){}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		logger.info("Loading Properties file: interject-filter.properties" );	      

		propertiesConfig = new Properties();
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("interject-filter.properties");
			propertiesConfig.load(in);
		} catch (Exception e) {
			logger.error("Unable to load properties from file", e);

		}

		mimeTypes = new ArrayList<String>(Arrays.asList(propertiesConfig.getProperty("mimeType").split(";")));
		mimeRedirects = new ArrayList<String>(Arrays.asList(propertiesConfig.getProperty("mimeType.redirect").split(";")));

	}

}
