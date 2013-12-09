package uk.bl.wa.interject.type;

public class Interjection {
	
	private String mimeType;
	private String redirectUrl;
	
	public Interjection(String mimeType, String redirectUrl) {
		this.mimeType = mimeType;
		this.redirectUrl = redirectUrl;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
	
