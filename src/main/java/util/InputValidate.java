package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidate {

    /**
     * Validate index file in Input directory.
     * @param fileName index file, which directory is ./input
     * @return index code, if matchered; else , null.
     */
    public static String analysisIndexFileName(String fileName) {
        Pattern pattern = Pattern.compile("^(./input/)([0-9A-Z]+)_([0-9]{8}-[0-9]{8})(?!_read).csv$");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find())
            return matcher.group(2);
        else
            return null;
    }
}
