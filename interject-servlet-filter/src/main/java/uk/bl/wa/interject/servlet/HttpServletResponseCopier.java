/**
 * 
 */
package uk.bl.wa.interject.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class HttpServletResponseCopier extends HttpServletResponseWrapper {
	
	protected static Logger logger = LogManager.getLogger(HttpServletResponseCopier.class);
	
    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private ServletOutputStreamCopier copier;

    public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
        super(response);
       	logger.info("HttpServletResponseCopier()");
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
    	logger.info("getOutputStream()");
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = getResponse().getOutputStream();
            copier = new ServletOutputStreamCopier(outputStream);
        }

        return copier;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
    	logger.info("getWriter()");
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
            writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
    	logger.info("flushBuffer()");
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            copier.flush();
        }
    }

    public byte[] getCopy() {
    	logger.info("getCopy()");
        if (copier != null) {
            return copier.getCopy();
        } else {
            return new byte[0];
        }
    }
}