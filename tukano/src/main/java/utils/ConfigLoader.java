package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
	private static final String CONFIG_FILE = "deployment.properties";
	private static final Properties properties = new Properties();

	private static ConfigLoader instance = null;

	private ConfigLoader() {
		try {
			properties.load(new FileInputStream(CONFIG_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized ConfigLoader getInstance() {
		if (instance == null) {
			instance = new ConfigLoader();
		}
		return instance;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		return properties.getProperty(key).replaceAll("^\"|\"$", "");
	}
}
