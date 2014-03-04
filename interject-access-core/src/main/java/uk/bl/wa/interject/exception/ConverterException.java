package uk.bl.wa.interject.exception;

public class ConverterException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3027078129662685698L;

	public ConverterException(String message) {
        super(message);
    }
	
	public ConverterException(Throwable exception) {
		super(exception);
	}
}

