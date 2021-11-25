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

    /**
     * Read GroupAssetAnalysis.json.
     *
     * @param file inputFile, e.g. GroupAssetAnalysis.json.
     * @throws ParseException parse error or proportionSum != 1, exit program.
     * @throws IOException    access file failed, exit program.
     * @Return GroupTarget, first: proportion; second: fund name; third: fund code.
     */
    public Map<Float, Pair<String, String>> getGroupTarget(String file) {
        Map<Float, Pair<String, String>> groupTarget = new HashMap<>();
        float proportionSum = 0;
        try {
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(new FileReader(file));
            for (Object obj : jsonArray) {
                JSONObject fund = (JSONObject) obj;
                float proportion = Float.parseFloat(((String) fund.get("proportion")).split("%")[0]) / 100;
                String code = (String) fund.get("code");
                String name = (String) fund.get("name");

                groupTarget.put(proportion, new Pair(name, code));
                proportionSum += proportion;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (Math.round(proportionSum) != 1) {
            System.out.println("Sum of proportion != 100%");
            System.exit(1);
        }
        return groupTarget;
    }

    /**
     * Read SingleFeatureAnalysis.json, acquire single target.
     *
     * @param file input file, e.g. SingleFeatureAnalysis.json.
     * @return first parameter: fund name; second: fund code.
     * @Exception IOException, read file failed, exit program.
     */
    public Map<String, String> getSingleTarget(String file) {
        Map<String, String> singleTarget = new HashMap<>();
        try {
            JSONArray array = (JSONArray) new JSONParser().parse(new FileReader(file));
            for (Object obj : array) {
                JSONObject fund = (JSONObject) obj;
                String name = (String) fund.get("name");
                String code = (String) fund.get("code");
                singleTarget.put(name, code);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return singleTarget;
    }

    /**
     * Read RelativeAnalysis.json file, get relativeTarget.
     *
     * @param file filename, e.g. RelativeAnalysis.json.
     * @return Set, first parameter: assetOne code; second parameter: assetTwo code.
     * @Exception Read file failed, exit program; parse file failed , exit program.
     */
    public Set<Pair<String, String>> getRelativeTarget(String file) {
        Set<Pair<String, String>> relativeTarget = new HashSet<>();
        try {
            JSONArray array = (JSONArray) new JSONParser().parse(new FileReader(file));
            for (Object obj : array) {
                JSONObject fund = (JSONObject) obj;
                String assetOne = (String)fund.get("assetOne");
                String assetTwo = (String)fund.get("assetTwo");

                relativeTarget.add(new Pair<>(assetOne, assetTwo));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return relativeTarget;
    }

}
