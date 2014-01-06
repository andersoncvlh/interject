package uk.bl.wa.interject.converter;

import org.apache.http.client.HttpClient;

public interface ImageStrategy {
	
	byte[] convertFromUrlToPng(String url, String sourceContentType) throws Exception;
}
