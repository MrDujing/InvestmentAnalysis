package crawl;

import dao.AssetHistoryValueDao;
import form.AssetHistoryValueForm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FundHistoryValueCrawl {
    private Logger logger = new LoggerRecorder().getLogger();
    public ArrayList<AssetHistoryValueForm> fundHistoryValueArray = new ArrayList<>();
    private int lastCrawlDate;//Crawl fund history value: [lastCrawlDate, now).
    private String preCrawlUrl;
    private int fundCode;

    public FundHistoryValueCrawl(String url, int code) {
        fundCode = code;

        //acquire lastCrawlDate.
        Properties properties = new PropertiesConfig("crawldate.properties").getProperties();
        String crawlDateStr = properties.getProperty(FundCodeTransfer.intToString(code) + "HistoryValue");

        //Crawl date :[lastCrawlDate, Today).
        if (null == crawlDateStr) {
            lastCrawlDate = 0;
            preCrawlUrl = url + "&code=" + FundCodeTransfer.intToString(fundCode) +
                    "&sdate=" +
                    "&edate=" + new DateTransForm().getYesterdayStr();
        } else {
            lastCrawlDate = new DateTransForm(crawlDateStr).getDateCount();
            preCrawlUrl = url + "&code=" + FundCodeTransfer.intToString(fundCode) +
                    "&sdate=" + new DateTransForm(crawlDateStr).getDateStr() +
                    "&edate=" + new DateTransForm().getYesterdayStr();
        }
    }

    public FundHistoryValueCrawl(int code) {
        this(ConstantParameter.FundValueCrawlURL, code);
    }

    /**
     * Crawl fund history value between [lastCrawlDate, today) by preCrawlUrl.
     *
     * @throws IOException
     */
    public void crawlFundHistory() throws IOException {
        fundHistoryValueArray.clear();
        int totalPages = 0, currentPage = 1;

        do {
            String crawlUrl = preCrawlUrl + "&page=" + currentPage;
            Document pageDocument = Jsoup.connect(crawlUrl).timeout(5000).get();
            //Acquire total pages, need to be crawled.
            if (currentPage == 1) {
                String pageText = pageDocument.body().text();
                Pattern pagePattern = Pattern.compile(",pages:(\\d+),");
                Matcher pageMatcher = pagePattern.matcher(pageText);
                if (pageMatcher.find())
                    totalPages = Integer.parseInt(pageMatcher.group(1));
                else
                    //50years * 366days / 49 + 1= totalPages.
                    totalPages = 50 * 366 / 49 + 1;
            }
            if (totalPages == 0)
                return;

            //Crawl fund history value.
            Element pageTable = pageDocument.getElementsByClass("w782 comm lsjz").first();
            Elements pageRows = pageTable.select("tr");
            for (int i = 1; i < pageRows.size(); i++) {
                Element row = pageRows.get(i);
                String historyValueDate = row.child(0).text();
                int assetProperty = 1;//Fund.
                float netValue = Float.parseFloat(row.child(1).text());
                float totalValue;
                if (row.child(2).text().equals(""))
                    totalValue = netValue;
                else
                    totalValue = Float.parseFloat(row.child(2).text());
                String dayIncreaseRateStr = row.child(3).text();
                float dayIncreaseRate = (!dayIncreaseRateStr.contains("%")) ? 0 : Float.parseFloat(dayIncreaseRateStr.split("%")[0]) / 100;
                fundHistoryValueArray.add(new AssetHistoryValueForm(fundCode, new DateTransForm(historyValueDate).getDateCount(), assetProperty, netValue, totalValue, dayIncreaseRate));
            }
        } while (totalPages > currentPage++);

        //Store history value to database.
        boolean insertFlag = new AssetHistoryValueDao().insertFundHistoryValue(fundHistoryValueArray);
        //Store lastCrawlDate to crawldate.properties.
        if (insertFlag)
            new PropertiesConfig("crawldate.properties").updateProperties(FundCodeTransfer.intToString(fundCode) + "HistoryValue", new DateTransForm().getDateStr());
        else
            logger.info(String.format("Store %d history value to database failed", fundCode));
    }
}
