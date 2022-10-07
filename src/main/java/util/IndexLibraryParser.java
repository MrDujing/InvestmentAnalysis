package util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexLibraryParser {
    private Logger logger = LoggerFactory.getLogger(IndexLibraryParser.class);
    private String fileName;
    private static JSONArray jsonArray;

    public IndexLibraryParser(String file) {
        fileName = file;
    }

    public IndexLibraryParser() {
        this("../indexLibrary.json");
    }

    public IndexLibraryParser parseJson() {
        if (null != jsonArray)
            return this;
        JSONTokener jsonTokener = new JSONTokener(IndexLibraryParser.class.getResourceAsStream(fileName));
        jsonArray = new JSONArray(jsonTokener);
        return this;
    }

    public String getTableName(String code) {
        String result = null;
        for (Object obj : jsonArray) {
            JSONObject js = (JSONObject) obj;
            if (js.get("prefix").toString().equals(code)) {
                result = js.get("lib").toString();
                break;
            }
        }
        return result;
    }
}
