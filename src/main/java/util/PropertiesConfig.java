package util;

import java.io.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class PropertiesConfig {
    private Properties properties = new Properties();
    private String fileName;
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfig.class);

    /**
     * Get properties from .properties file.
     *
     * @param fileName file name, correspond to resources directory.
     * @exception  IOException load file failed, system exit(1).
     */
    public PropertiesConfig(String fileName) {
        this.fileName = fileName;
        try (InputStream in = PropertiesConfig.class.getResourceAsStream(fileName)) {
            properties.load(in);
        } catch (IOException e) {
            logger.error("Load {} failed", fileName);
            e.printStackTrace();
        }

    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * Write properties which store in HashTable to file.
     *
     */
    public void updateProperties(Map<String,String> propertiesPair) {
        for(Map.Entry<String, String> entry : propertiesPair.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            properties.store(fos, LocalDate.now().toString());
        } catch (IOException e) {
            logger.error("Open {} failed", fileName);
            e.printStackTrace();
        }
    }
}
