package uk.bl.wa.interject.converter;

public class ImageConverter {
	
	private ImageStrategy imageStrategy;

    public ImageConverter(ImageStrategy imageStrategy) {
        this.imageStrategy = imageStrategy;
    }
 
    public byte[] convertFromUrlToPng(String url, String sourceContentType) throws Exception {
        return this.imageStrategy.convertFromUrlToPng(url, sourceContentType);
    }
}
