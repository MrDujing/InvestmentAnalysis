import crawl.FundHistoryValueCrawl;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;
import util.JsonConfig;

import java.io.IOException;
import java.util.Map;


public class CrawlInvestmentData {
    public static void main(String[] args) {
        try {
            Map<Integer, Pair<String, Float>> fundGroup = new JsonConfig(".\\src\\main\\resources\\group.json").getGroupTarget();
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

