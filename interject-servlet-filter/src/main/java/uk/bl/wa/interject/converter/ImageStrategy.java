package uk.bl.wa.interject.converter;

public interface ImageStrategy {
	byte[] convertFromUrlToPng(String url, String sourceContentType) throws Exception;
}
