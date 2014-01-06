/**
 * 
 */
package uk.bl.wa.interject.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Starting from:
 *   http://stackoverflow.com/a/8972088/6689
 * 
 * You need to create a Filter wherein you wrap the ServletResponse argument with a custom HttpServletResponseWrapper 
 * implementation wherein you override the getOutputStream() and getWriter() to return a custom ServletOutputStream 
 * implementation wherein you copy the written byte(s) in the base abstract OutputStream#write(int b) method. 
 * Then, you pass the wrapped custom HttpServletResponseWrapper to the FilterChain#doFilter() call instead 
 * and finally you should be able to get the copied response after the the call.
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class BufferedHttpServletResponse extends HttpServletResponseWrapper {
	
	protected static Logger logger = LogManager.getLogger(BufferedHttpServletResponse.class);
	
    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private ByteArrayServletOutputStream copier;

    public BufferedHttpServletResponse(HttpServletResponse response) throws IOException {
        super(response);
       	logger.info("BufferedHttpServletResponse()");
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
    	new Exception().printStackTrace();
    	logger.info("getOutputStream()");
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = getResponse().getOutputStream();
            copier = new ByteArrayServletOutputStream();
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
            copier = new ByteArrayServletOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
    	logger.info("flushBuffer()");
        if (writer != null) {
            writer.flush();
        } else if (copier != null) {
            copier.flush();
        }
    }
    
    /* (non-Javadoc)
	 * @see javax.servlet.ServletResponseWrapper#isCommitted()
	 */
	@Override
	public boolean isCommitted() {
		return false;
	}

	public void reallyFlush() throws IOException {
    	if( outputStream != null ) {
    		outputStream.write(copier.toByteArray());
    		outputStream.flush();
    		outputStream.close();
    	}
    }

    public byte[] getCopy() {
    	logger.info("getCopy()");
        if (copier != null) {
            return copier.toByteArray();
        } else {
            return new byte[0];
        }
    }
    
    public class ByteArrayServletOutputStream extends ServletOutputStream {
        /**
         * Our buffer to hold the stream.
         */
        protected ByteArrayOutputStream buf = null;


        /**
         * Construct a new ServletOutputStream.
         */
        public ByteArrayServletOutputStream() {
            buf = new ByteArrayOutputStream();
        }


        /**
         * @return the byte array.
         */
        public byte[] toByteArray() {
            return buf.toByteArray();
        }


        /**
         * Write to our buffer.
         *
         * @param b The parameter to write
         */
        public void write(int b) {
            buf.write(b);
        }


		@Override
		public boolean isReady() {
			return true;
		}


		@Override
		public void setWriteListener(WriteListener writeListener) {
		}
    }    
}