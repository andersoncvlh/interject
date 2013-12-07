package uk.bl.wa.interject.type;

import java.util.HashMap;
import java.util.Map;

import org.apache.tika.mime.MediaType;

public enum ProblemType {
	
	BMP(MediaType.image("bmp")), TIFF(MediaType.image("tiff")), MS_BMP(MediaType.image("x-ms-bmp"));
	
    private static final Map<String, ProblemType> lookup = new HashMap<String, ProblemType>();
    
    static {
        for (ProblemType problemType : ProblemType.values())
            lookup.put(problemType.getMimeType(), problemType);
    }
    
	private MediaType mediaType;
    
    public static ProblemType get(String mimeType) {
        return lookup.get(mimeType);
    }
	
	private ProblemType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public MediaType getMediaType() {
		return mediaType;
	}
	
	public String getMimeType() {
		return getMediaType().toString();
	}
}
	
