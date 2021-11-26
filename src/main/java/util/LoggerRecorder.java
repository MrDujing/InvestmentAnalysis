package util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerRecorder {
    private Logger logRecorder;
    private FileHandler logFile;
    private static String loggerName, fileName;

    static {
        Properties prop = new PropertiesConfig("config.properties").getProperties();
        loggerName = prop.getProperty("LOG_NAME");
        fileName = prop.getProperty("LOG_FILE");
    }

    /**
     * constructor
     *
     * @param loggerName logger name, can be acquired by Logger.getName().
     */
    public LoggerRecorder(String loggerName, String fileName) {
        logRecorder = Logger.getLogger(loggerName);
        //create file
        try {
            File file = new File(fileName);
            file.createNewFile();
            logFile = new FileHandler(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set log format and file handler
        logFile.setFormatter(new SimpleFormatter());
        logRecorder.addHandler(logFile);
    }

    public LoggerRecorder() {
        this(loggerName, fileName);
    }

    public Logger getLogger() {
        return logRecorder;
    }
}
