package uk.bl.wa.interject.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.formats.png.PngConstants;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class ImageIOConversionServlet
 * 
 * An example image conversion service: 
 *  http://stackoverflow.com/a/14619218/6689 
 *  ...reading and writing JPEG, PNG, BMP, WBMP and GIF.
 * 
 */
public class CommonsImagingConversionServlet extends HttpServlet {
       
	private static final long serialVersionUID = -8969018766035165227L;
	
	protected static Logger logger = LogManager.getLogger(CommonsImagingConversionServlet.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommonsImagingConversionServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getParameter("url");
		String sourceContentType = request.getParameter("sourceContentType");
	    logger.info("Attempting to convert: "+url+" from "+sourceContentType);		
		if (url != null) {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			try {
				HttpGet httpGet = new HttpGet(url);
				if( sourceContentType != null ) {
					httpGet.addHeader("Accept", sourceContentType);
				}
				CloseableHttpResponse res = httpclient.execute(httpGet);
				InputStream is = res.getEntity().getContent();
		        // read image
				Map<String, Object> readParams = new HashMap<String, Object>();
				readParams.put(ImagingConstants.PARAM_KEY_FILENAME, url);
				// Note that current version assumes TIFFs are not transparent.
		        final BufferedImage image = Imaging.getBufferedImage(is,readParams);
				// Now convert and respond:
				response.setContentType("image/png");
				ServletOutputStream out = response.getOutputStream();
				Map<String, Object> writeParams = new HashMap<String, Object>();
				writeParams.put(PngConstants.PARAM_KEY_PNG_FORCE_TRUE_COLOR, Boolean.TRUE);
				Imaging.writeImage(image, out, ImageFormat.IMAGE_FORMAT_PNG, writeParams );
				out.flush();
			} catch (Exception exp) {
				exp.printStackTrace();
			} finally {
				httpclient.close();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
