/**
 * 
 */
package uk.bl.wa.interject.servlet;

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
	}


	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) req;
		HttpServletResponse httpResponse = (HttpServletResponse) res;

		logger.info("Checking MIME Type for: "+httpRequest.getRequestURL());

		// Perform the rest of the processing, so we have access to the payload:
		//
		// FIXME Doing this here does not work, as the initial response (e.g. status code etc is already 
		// committed by the time we exit this function.
		// To make this work, we need to buffer the response until we can sample the body and then
		// clear/rewrite the response before continuing.
		// A fairly similar example is included in the form of the HttpServletResponseCopier class.
		//

		//chain.doFilter(req, res);

		// Sniff the type of the payload:
		//BufferedInputStream in = new BufferedInputStream("test");
		Tika tika = new Tika();
		String strMime = tika.detect(httpRequest.getRequestURL().toString());
		logger.info("Determined Content-Type to be: "+strMime);

		
		// Now decide what to do, based on the determined type:
		Header accept = new BasicHeader("Accept", httpRequest.getHeader("Accept"));
		boolean originalRequested = false;
		if( accept != null ) {
			logger.info("Got Accept: "+accept.getValue());
			for( HeaderElement e : accept.getElements() ) {
				if( e.getName().equals(strMime)) {
					logger.info("Accept header matched original format.");
					originalRequested = true;
				}
			}
		}

		if( originalRequested == false ) {
			// Redirect any filtered Mimetype
			for(int i = 0;i < mimeTypes.size();i++){
				String mimeType = mimeTypes.get(i);
				if(mimeType.equalsIgnoreCase(strMime)){
					logger.info("Redirecting: "+httpRequest.getRequestURL() + " to: " +  mimeRedirects.get(i));
					httpResponse.sendRedirect(httpResponse.encodeRedirectURL(
							mimeRedirects.get(i) 
							+ httpRequest.getRequestURL().toString() 
							+ "&sourceContentType=" + strMime
							));
					return;
				}
			}	
		}


		// Set up the response copier:
		HttpServletResponseCopier hsrc = new HttpServletResponseCopier(httpResponse);
		
		// Pass down the chain:
		chain.doFilter(req, hsrc);
		
		// output the result
		byte[] copy = hsrc.getCopy();
		logger.info("Copier got: "+copy.length);
		logger.info("Copier got: "+new String(copy).substring(0, 10));
		logger.info("Type: "+tika.detect(copy));
		
		hsrc.flushBuffer();

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
