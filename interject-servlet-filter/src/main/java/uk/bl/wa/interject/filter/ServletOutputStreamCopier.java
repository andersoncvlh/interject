/**
 * 
 */
package uk.bl.wa.interject.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * TODO Modify to avoid copying more that a few hundred KB.
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class ServletOutputStreamCopier extends ServletOutputStream {
	protected static Logger logger = LogManager.getLogger(ServletOutputStreamCopier.class);

    private OutputStream outputStream;
    private ByteArrayOutputStream copy;
    private long counter;
    private int bufsize = 10;
    private boolean written;

    public ServletOutputStreamCopier(OutputStream outputStream) {
    	logger.info("ServletOutputStreamCopier()");
        this.outputStream = outputStream;
        this.copy = new ByteArrayOutputStream();
        this.counter = 0;
        written = false;
    }
    
    private void writeBuf() throws IOException {
    	outputStream.write(copy.toByteArray());
    	written = true;    	
    }

    @Override
    public void write(int b) throws IOException {
        if( counter > bufsize ) {
        	outputStream.write(b);
        } else {
            copy.write(b);
            if( counter == bufsize) writeBuf();
            counter++;
        }
    }

    /* (non-Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
    	logger.info("close()");
		super.close();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
    	logger.info("flush()");
		super.flush();
	}
	
	public void reallyFlush() throws IOException {
    	if( ! written ) writeBuf();
	}
	
	public boolean isCommitted() {
		return written;
	}

	public byte[] getCopy() {
    	return copy.toByteArray();
    }

}