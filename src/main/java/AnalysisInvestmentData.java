import analysis.AssetSingleProperty;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;
import util.ConfigJson;
import util.DateTransForm;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

public class AnalysisInvestmentData {
    public static void main(String[] args) {
        try {
            Map<Integer, Pair<String, Float>> fundGroup = new ConfigJson(".\\src\\main\\resources\\group.json").getFundMap();
            ArrayList<Pair<Integer, Float>> calculateMap = new ArrayList<>();
            for (int fundCode : fundGroup.keySet()) {

                String fundName = fundGroup.get(fundCode).getKey();
                int startDate = new DateTransForm("2018-01-01").getDateCount();
                int endDate = new DateTransForm().getDateCount();
                AssetSingleProperty singleProperty = new AssetSingleProperty(fundCode, fundName, startDate, endDate);
                System.out.println(String.format("************%d****%s********************", fundCode, fundName));

                //Pie fig
                singleProperty.drawDayIncreasePieFig();

                NumberFormat percentFormat =NumberFormat.getPercentInstance();
                percentFormat.setMaximumFractionDigits(2);

                //MDD
                calculateMap.clear();
                calculateMap = singleProperty.calculateMaxDrawDown();
                String MDDStartDate = new DateTransForm(calculateMap.get(0).getKey()).getDateStr();
                String MDDEndDate = new DateTransForm(calculateMap.get(1).getKey()).getDateStr();
                int MDDDays = calculateMap.get(1).getKey() - calculateMap.get(0).getKey();
                Float MDDStartValue = calculateMap.get(0).getValue();
                Float MDDEndValue = calculateMap.get(1).getValue();
                System.out.println(String.format("MDD: %s:%g -> %s:%g : %d days, %s ", MDDStartDate, MDDStartValue, MDDEndDate, MDDEndValue, MDDDays, percentFormat.format((MDDEndValue - MDDStartValue) / MDDStartValue)));

                //MPF
                calculateMap.clear();
                calculateMap = singleProperty.calculateMaxProfit();
                String MPFStartDate = new DateTransForm(calculateMap.get(0).getKey()).getDateStr();
                String MPFEndDate = new DateTransForm(calculateMap.get(1).getKey()).getDateStr();
                int MPFDays = calculateMap.get(1).getKey() - calculateMap.get(0).getKey();
                Float MPFStartValue = calculateMap.get(0).getValue();
                Float MPFEndValue = calculateMap.get(1).getValue();
                System.out.println(String.format("MPF: %s:%g -> %s:%g : %d days, %s ", MPFStartDate, MPFStartValue, MPFEndDate, MPFEndValue, MPFDays, percentFormat.format((MPFEndValue - MPFStartValue) / MPFStartValue)));

                //MTF
                float mtfIncrease = singleProperty.calculateMaxTotalProfit();
                System.out.println("MTF is " + percentFormat.format(mtfIncrease));

                //STDEV
                double popSTDEV = singleProperty.calculatePopSTDEV();
                System.out.printf("STDEV: %g \n", popSTDEV);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
