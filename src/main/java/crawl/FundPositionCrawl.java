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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class FundPositionCrawl {

    private Logger logger = LoggerFactory.getLogger(FundPositionCrawl.class);
    private ArrayList<FundPositionForm> positionFormArray = new ArrayList<>();
    private String crawlUrl;
    private int crawlPositionQuarter;
    private int fundCode;

    /**
     * Prefix of crawl url is https://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jjcc&topline=30.
     *
     * @param code
     */
    public FundPositionCrawl(int code) {
        fundCode = code;
        crawlUrl = ConstantParameter.POSITION_CRAWL_URL_PREFIX + String.format("&code=%s&year=", FundCodeTransfer.transferToStr(fundCode));

        //acquire lastCrawlQuarter.
        Properties properties = new PropertiesConfig("../crawldate.properties").getProperties();
        String crawlPositionDateStr = properties.getProperty(FundCodeTransfer.transferToStr(fundCode) + "FundPosition");
        if (crawlPositionDateStr == null)
            crawlPositionQuarter = ConstantParameter.QUARTER_INVALID;
        else
            crawlPositionQuarter = new DateTransForm(crawlPositionDateStr).getQuarterCount();
    }

    public boolean crawlFundPosition() throws IOException {
        positionFormArray.clear();

        //Don't crawl if last crawl quarter equal to this quarter.
        if (crawlPositionQuarter == new DateTransForm().getQuarterCount())
            return true;

        /**
         * First crawl,get all years from url.
         */
        String firstUrl = crawlUrl + ConstantParameter.YEAR_INVALID;
        Document firstDocument = Jsoup.connect(firstUrl).timeout(3000).get();
        String yearArrayStr = firstDocument.body().text();

        Pattern yearPattern = Pattern.compile("[0-9]{4}");
        Matcher yearMatcher = yearPattern.matcher(yearArrayStr);
        if (yearMatcher.find() == false)
            return true; //No position, don't need crawl.

        //Crawl position of all year in while loop.
        while (yearMatcher.find()) {
            String year = yearMatcher.group();
            String yearUrl = crawlUrl + year;

            Document yearDocument = Jsoup.connect(yearUrl).timeout(4000).get();

        }


        /**
         * Crawl stock position.
         */
        Element shareTable = firstDocument.getElementsByClass("position_shares").first();
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
            byte assertProperty = 0;
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
        Element bondTable = firstDocument.getElementsByClass("position_bonds").first();
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
            byte assertProperty = 0;
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
        int insertFlag = 0;
        if (positionFormArray.size() > 0)
            insertFlag = new FundPositionDao().insertFundPosition(positionFormArray);
        else
            logger.info(String.format("%d fund position is zero", fundCode));
        //Store lastCrawlDate to crawldate.properties.
        if (insertFlag == 0)
            ;
            // new PropertiesConfig("crawldate.properties").updateProperties(FundCodeTransfer.transferToStr(fundCode) + "FundPosition", new DateTransForm().getDateStr());
        else
            logger.info(String.format("Don't Store %d fund position to database.", fundCode));
        return false;
    }
}