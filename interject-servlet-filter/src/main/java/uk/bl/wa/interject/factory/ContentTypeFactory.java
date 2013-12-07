package uk.bl.wa.interject.factory;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;

import uk.bl.wa.interject.type.ProblemType;

public enum ContentTypeFactory {
	
	INSTANCE;
	
	private ContentTypeFactory() {}
	
	public ProblemType findProblemType(String url) {
		String mimeType = newTika().detect(url);
		ProblemType problemType = ProblemType.get(mimeType);
		return problemType;
	}
	
	public ProblemType findProblemType(InputStream inputStream) {
		ProblemType problemType = null;
		String mimeType = null;
		try {
			mimeType = newTika().detect(inputStream);
			problemType = ProblemType.get(mimeType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return problemType;
	}
	
	private Tika newTika() {
		return new Tika();
	}
}