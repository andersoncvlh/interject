package uk.bl.wa.interject.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class ImageIOConversionServlet extends HttpServlet {
       
	private static final long serialVersionUID = -8969018766035165227L;
	
	protected static Logger logger = LogManager.getLogger(ImageIOConversionServlet.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageIOConversionServlet() {
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
				ImageInputStream iis = ImageIO.createImageInputStream(is);
				BufferedImage image = ImageIO.read(iis);
				//res.close();
				// Now convert and respond:
				response.setContentType("image/png");
				ServletOutputStream out = response.getOutputStream();
				ImageIO.write(image, "png", out);
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
