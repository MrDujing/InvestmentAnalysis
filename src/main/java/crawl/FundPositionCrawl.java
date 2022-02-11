package crawl;

import dao.FundPositionDao;
import form.FundPositionForm;
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

public class FundPositionCrawl {
    private Logger logger = new LoggerRecorder().getLogger();
    private ArrayList<FundPositionForm> positionFormArray = new ArrayList<>();
    private boolean isCurrentQuarter = false;//Crawl fund position only if last crawl quarter is not current quarter.
    private String crawlUrl;
    private int fundCode;

    public FundPositionCrawl(String url, int code) {
        fundCode = code;
        crawlUrl = url + FundCodeTransfer.intToString(fundCode) + ".html";

        //acquire lastCrawlQuarter.
        Properties properties = new PropertiesConfig("crawldate.properties").getProperties();
        String crawlDateStr = properties.getProperty(FundCodeTransfer.intToString(fundCode) + "FundPosition");
        if (null != crawlDateStr && new DateTransForm().getQuarterCount() == new DateTransForm(crawlDateStr).getQuarterCount())
            isCurrentQuarter = true;
    }

    public FundPositionCrawl(int code) {
        this(ConstantParameter.FundPositionCrawlURL, code);
    }

    public void crawlFundPosition() throws IOException {
        if (isCurrentQuarter == true)
            return;
        positionFormArray.clear();
        Document fundDocument = Jsoup.connect(crawlUrl).timeout(5000).get();
        /**
         * Crawl stock position.
         */
        Element shareTable = fundDocument.getElementsByClass("position_shares").first();
        Elements shareRow = shareTable.getElementsByTag("tr");

        if (shareRow.size() >= 2 && shareRow.get(1).getElementsByTag("td").size() == 4) {
            //Acquire crawl quarter.
            Element endDate = shareTable.getElementsByClass("end_date").first();
            Pattern endDatePattern = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
            Matcher endDateMatcher = endDatePattern.matcher(endDate.text());
            int lastCrawlQuarter = 0;
            if (endDateMatcher.find())
                lastCrawlQuarter = new DateTransForm(endDateMatcher.group(0)).getQuarterCount();
            else
                lastCrawlQuarter = new DateTransForm(endDate.text().split(" ")[1]).getQuarterCount();
            //Initialize.
            int assertProperty = 0;
            String assertCode = "", assertName = "";
            float assetProportion = 0.0f;
            //Acquire each property of position.
            for (int i = 1; i < shareRow.size(); i++) {
                Elements shareTad = shareRow.get(i).getElementsByTag("td");
                String stockCode = shareTad.get(2).attributes().get("stockcode");
                if ("stock".equals(stockCode.split("_")[0]))
                    assertProperty = 1;
                assertCode = stockCode.split("_")[1].split(" ")[0];
                assertName = shareTad.get(0).getElementsByAttribute("title").get(0).text();
                assetProportion = Float.parseFloat(shareTad.get(1).text().split("%")[0]) / 100;
                positionFormArray.add(new FundPositionForm(fundCode, lastCrawlQuarter, assertProperty, assertCode, assertName, assetProportion));
            }
        }

        /**
         * Crawl bond position.
         */
        Element bondTable = fundDocument.getElementsByClass("position_bonds").first();
        Elements bondRow = bondTable.getElementsByTag("tr");

        if (bondRow.size() >= 2 && bondRow.get(1).getElementsByTag("td").size() == 3) {
            //Acquire crawl quarter.
            Element endDate = bondTable.getElementsByClass("end_date").first();
            Pattern endDatePattern = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
            Matcher endDateMatcher = endDatePattern.matcher(endDate.text());
            int lastCrawlQuarter = 0;
            if (endDateMatcher.find())
                lastCrawlQuarter = new DateTransForm(endDateMatcher.group(0)).getQuarterCount();
            else
                lastCrawlQuarter = new DateTransForm(endDate.text().split(" ")[1]).getQuarterCount();
            //Initialize.
            int assertProperty = 0;
            String assertCode = "", assertName = "";
            float assetProportion = 0.0f;
            //Acquire each property of position.
            for (int i = 1; i < bondRow.size(); i++) {
                Elements bondTad = bondRow.get(i).getElementsByTag("td");
                String bondCode = bondTad.get(2).attributes().get("newcode");
                if ("ZQ".equals(bondCode.split("_")[0]))
                    assertProperty = 2;
                assertCode = bondCode.split("_")[1].split(" ")[0];
                assertName = bondTad.get(0).text();
                assetProportion = Float.parseFloat(bondTad.get(1).text().split("%")[0]) / 100;
                positionFormArray.add(new FundPositionForm(fundCode, lastCrawlQuarter, assertProperty, assertCode, assertName, assetProportion));
            }
        }
        /**
         * Store fund position to database.
         */
        boolean insertFlag = false;
        if (positionFormArray.size() > 0)
            insertFlag = new FundPositionDao().insertFundPosition(positionFormArray);
        else
            logger.info(String.format("%d fund position is zero", fundCode));
        //Store lastCrawlDate to crawldate.properties.
        if (insertFlag)
            new PropertiesConfig("crawldate.properties").updateProperties(FundCodeTransfer.intToString(fundCode) + "FundPosition", new DateTransForm().getDateStr());
        else
            logger.info(String.format("Don't Store %d fund position to database.", fundCode));
    }
}