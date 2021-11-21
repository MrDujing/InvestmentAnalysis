package util;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigProperties {
    private Properties properties = new Properties();
    private boolean loadPropertiesFile = false;
    private String fileName;

    /**
     * Get properties from .properties file.
     *
     * @param propertiesName file name, correspond to resources directory.
     */
    public ConfigProperties(String propertiesName) {
        fileName = propertiesName;
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (null == in)
            loadPropertiesFile = false;
        try {
            properties.load(in);
            loadPropertiesFile = true;
        } catch (IOException e) {
            loadPropertiesFile = false;
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                loadPropertiesFile = false;
                e.printStackTrace();
            }
        }
    }

    public Properties getProperties() {
        if (loadPropertiesFile)
            return properties;
        else
            return null;
    }

    /**
     * Write properties which store in Hashtable to propertiesName file.
     *
     * @param key
     * @param value
     */
    public boolean updateProperties(String key, String value) {
        if (!loadPropertiesFile)
            return false;
        properties.setProperty(key, value);
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            properties.store(fos, LocalDate.now().toString());
            fos.close();// 关闭流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Read group.properties, retrieve fund code and group proportion.
     */
    public Map<Integer, Float> ReadGroupConfig() {
        Map<Integer, Float> fundMap = new HashMap<>();
        float proportionSum = 0;
        for (Object key : properties.keySet()) {
            Float proportion = Float.parseFloat(properties.getProperty(key.toString()).split("%")[0]) / 100;
            proportionSum += proportion;
            fundMap.put(Integer.parseInt(key.toString()), proportion);
        }
        if (Math.round(proportionSum) != 1) {
            System.out.println("proportion sum don't equal 1");
            fundMap.clear();
            System.exit(0);
        }
        return fundMap;
    }
}
