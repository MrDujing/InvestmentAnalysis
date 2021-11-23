package util;

import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigJson {
    private String jsonFileName;
    private Map<Integer, Pair<String, Float>> fundMap = new HashMap<>();

    public Map<Integer, Pair<String, Float>> getFundMap() {
        return fundMap;
    }

    /**
     * Read group.json, acquire fund properties.
     * @param fileName group.json.
     * @throws ParseException parse error or proportionSum != 1;
     */
    public ConfigJson(String fileName) throws ParseException, IOException {
        jsonFileName = fileName;
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new FileReader(fileName));
        float proportionSum = 0;
        for (Object obj : jsonArray) {
            JSONObject fund = (JSONObject) obj;
            int fundCode = Integer.parseInt((String) fund.get("fundCode"));
            String name = (String) fund.get("name");
            float proportion = new Float((Double)fund.get("proportion"));
            fundMap.put(fundCode, new Pair(name, proportion));
            proportionSum += proportion;
        }

        if (Math.round(proportionSum) != 1)
            throw new ParseException(ParseException.ERROR_UNEXPECTED_CHAR);
    }
}
