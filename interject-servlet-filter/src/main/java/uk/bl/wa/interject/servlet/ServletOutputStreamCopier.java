/**
 * 
 */
package uk.bl.wa.interject.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

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
    private int bufsize = 100;
    private boolean written;

    public ServletOutputStreamCopier(OutputStream outputStream) {
    	logger.info("ServletOutputStreamCopier()");
        this.outputStream = outputStream;
        this.copy = new ByteArrayOutputStream(bufsize);
        this.counter = 0;
        written = false;
    }

    @Override
    public void write(int b) throws IOException {
        copy.write(b);
    	if( counter == bufsize) {
    		outputStream.write(copy.toByteArray());
    		written = true;
    	}
        if( counter > bufsize ) {
        	outputStream.write(b);
        }
        counter++;
    }

    /* (non-Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
    	logger.info("close()");
    	if( ! written ) { 
    		write(copy.toByteArray()); 
    		written = true;
    	}
		super.close();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
    	logger.info("flush()");
    	if( ! written ) { 
    		write(copy.toByteArray()); 
    		written = true;
    	}
		super.flush();
	}

	public byte[] getCopy() {
    	logger.info("getCopy() INNERMOST ");
    	byte[] b = copy.toByteArray();
    	logger.info("b = "+b.length);
        return b;
    }

}