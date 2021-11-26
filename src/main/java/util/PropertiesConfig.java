package util;

import java.io.*;
import java.time.LocalDate;
import java.util.Properties;

public class PropertiesConfig {
    private Properties properties = new Properties();
    private String fileName;

    /**
     * Get properties from .properties file.
     *
     * @param file file name, correspond to resources directory.
     * @throws IOException load file failed.
     */
    public PropertiesConfig(String file) throws IOException {
        fileName = file;
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        properties.load(in);
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * Write properties which store in HashTable to file.
     *
     * @param key
     * @param value
     */
    public void updateProperties(String key, String value) {
        properties.setProperty(key, value);
        String tempFileName = "./src/main/resources" + fileName;
        try (FileOutputStream fos = new FileOutputStream(tempFileName)) {
            properties.store(fos, LocalDate.now().toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
