package util;

import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonConfig {

    private Map<Integer, String> SingleTarget = new HashMap<>();
    private Set<Pair<Integer, Integer>> RelativeTarget = new HashSet<>();
    private Map<Integer, Pair<String, Float>> GroupTarget = new HashMap<>();

    /**
     * Read GroupAssetAnalysis.json, set GroupTarget.
     * @param file inputFile, e.g. GroupAssetAnalysis.json.
     * @throws ParseException parse error or proportionSum != 1, exit program.
     * @throws IOException access file failed, exit program.
     */
    protected boolean acquireGroupTarget(String file) {

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new FileReader(file));
        float proportionSum = 0;
        for (Object obj : jsonArray) {
            JSONObject fund = (JSONObject) obj;
            int fundCode = Integer.parseInt((String) fund.get("fundCode"));
            String name = (String) fund.get("name");
            float proportion = new Float((Double)fund.get("proportion"));
            GroupTarget.put(fundCode, new Pair(name, proportion));
            proportionSum += proportion;
        }

        if (Math.round(proportionSum) != 1)
            throw new ParseException(ParseException.ERROR_UNEXPECTED_CHAR);
    }

    public Map<Integer, Pair<String, Float>> getGroupTarget() {
        return GroupTarget;
    }
}
