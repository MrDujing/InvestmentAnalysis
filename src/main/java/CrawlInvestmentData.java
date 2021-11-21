import crawl.FundHistoryValueCrawl;
import crawl.FundPositionCrawl;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;
import util.ConfigJson;

import java.io.IOException;
import java.util.Map;


public class CrawlInvestmentData {
    public static void main(String[] args) {
        try {
            Map<Integer, Pair<String, Float>> fundGroup = new ConfigJson(".\\src\\main\\resources\\group.json").getFundMap();
            for (int fundCode : fundGroup.keySet()) {
                new FundHistoryValueCrawl(fundCode).crawlFundHistory();
                System.out.println("Crawl fund history value: " + fundGroup.get(fundCode).getKey() + "-" + fundCode);
                //new FundPositionCrawl(fundCode).crawlFundPosition();
                //System.out.println("Crawl fund position : " + fundGroup.get(fundCode).getKey() + "-" + fundCode);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

