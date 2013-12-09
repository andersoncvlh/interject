package uk.bl.wa.interject.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

import uk.bl.wa.interject.type.MediaType;

public enum ContentTypeFactory {

	INSTANCE;

	protected static Logger logger = LogManager
			.getLogger(ContentTypeFactory.class);

	private Map<String, MediaType> problemTypes;
	
	private ContentTypeFactory() {
		PropertiesConfiguration propertiesConfig;
		try {
			propertiesConfig = new PropertiesConfiguration(
					"interject-filter.properties");
			List<Object> mimeTypes = propertiesConfig.getList("mimeType");
			problemTypes = new HashMap<String, MediaType>();
			
			for (Object mimeType : mimeTypes) {
				String[] pairs = ((String)mimeType).split(";");
			    String problemMimeType = pairs[0];
			    String problemRedirect = pairs[1];
			    MediaType problemType = newProblemTypeInstance(problemMimeType, problemRedirect);
				problemTypes.put(problemMimeType, problemType);
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public MediaType findProblemType(String url) {
		String mimeType = newTikaInstance().detect(url);
		return problemTypes.get(mimeType);
	}

	public MediaType findProblemType(InputStream inputStream) {
		MediaType problemType = null;
		try {
			String mimeType = newTikaInstance().detect(inputStream);
			problemType = problemTypes.get(mimeType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return problemType;
	}

	private Tika newTikaInstance() {
		return new Tika();
	}
	
	private MediaType newProblemTypeInstance(String mimeType, String redirectUrl) {
		return new MediaType(mimeType, redirectUrl);
	}
}