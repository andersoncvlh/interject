package uk.bl.wa.interject.services;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.bl.wa.interject.converter.ImageConverter;
import uk.bl.wa.interject.converter.ImageIOStrategy;

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
		    ImageConverter imageConverter = new ImageConverter(ImageIOStrategy.INSTANCE);
			try {
				byte[] imageBytes = imageConverter.convertFromUrlToPng(url, sourceContentType);
				response.setContentType("image/png");
				ServletOutputStream out = response.getOutputStream();
				out.write(imageBytes, 0, imageBytes.length);
				out.flush();			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
