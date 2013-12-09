package uk.bl.wa.interject.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.bl.wa.interject.type.Interjection;

public enum InterjectionFactory {

	INSTANCE;

	protected static Logger logger = LogManager
			.getLogger(InterjectionFactory.class);

	private Map<String, Interjection> problemTypes;
	
	private InterjectionFactory() {
		PropertiesConfiguration propertiesConfig;
		try {
			propertiesConfig = new PropertiesConfiguration(
					"interject-filter.properties");
			List<Object> mimeTypes = propertiesConfig.getList("mimeType");
			problemTypes = new HashMap<String, Interjection>();
			
			for (Object mimeType : mimeTypes) {
				String[] pairs = ((String)mimeType).split(";");
			    String problemMimeType = pairs[0];
			    String problemRedirect = pairs[1];
			    Interjection problemType = newProblemTypeInstance(problemMimeType, problemRedirect);
				problemTypes.put(problemMimeType, problemType);
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Interjection findProblemType(String mimeType) {
		Interjection problemType = null;
		problemType = problemTypes.get(mimeType);
		return problemType;
	}

	private Interjection newProblemTypeInstance(String mimeType, String redirectUrl) {
		return new Interjection(mimeType, redirectUrl);
	}
}