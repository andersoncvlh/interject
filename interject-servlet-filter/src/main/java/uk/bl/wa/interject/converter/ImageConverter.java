package uk.bl.wa.interject.converter;

import uk.bl.wa.interject.exception.ConverterException;

public class ImageConverter {
	
	private ImageStrategy imageStrategy;

    public ImageConverter(ImageStrategy imageStrategy) {
        this.imageStrategy = imageStrategy;
    }
 
    public byte[] convertFromUrlToPng(String url, String sourceContentType) throws ConverterException {
        return this.imageStrategy.convertFromUrlToPng(url, sourceContentType);
    }
}
