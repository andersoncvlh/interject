package uk.bl.wa.interject.converter;

import uk.bl.wa.interject.exception.ConverterException;

public interface ImageStrategy {
	
	byte[] convertFromUrlToPng(String url, String sourceContentType) throws ConverterException;
}
