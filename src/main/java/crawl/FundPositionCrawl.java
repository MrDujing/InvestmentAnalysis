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
    private String crawlStockUrl, crawlBondUrl;
    private int crawlStockQuarter, crawlBondQuarter;
    private int fundCode;

    /**
     * Prefix of crawl stock url is https://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jjcc&topline=30.
     * Prefix of crawl bond url is https://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=zqcc&topline=10.
     *
     * @param code
     */
    public FundPositionCrawl(int code) {
        fundCode = code;
        crawlStockUrl = ConstantParameter.STOCK_POSITION_CRAWL_URL_PREFIX + String.format("&code=%s&year=", FundCodeTransfer.transferToStr(fundCode));
        crawlBondUrl = ConstantParameter.BOND_POSITION_CRAWL_URL_PREFIX + String.format("&code=%s&year=", FundCodeTransfer.transferToStr(fundCode));

        //acquire lastCrawlQuarter.
        Properties properties = new PropertiesConfig("../crawldate.properties").getProperties();
        String crawlStockDateStr = properties.getProperty(FundCodeTransfer.transferToStr(fundCode) + "StockPosition");
        if (crawlStockDateStr == null)
            crawlStockQuarter = ConstantParameter.QUARTER_BASE;
        else
            crawlStockQuarter = new DateTransForm(crawlStockDateStr).getQuarterCount();

        String crawlBondDateStr = properties.getProperty(FundCodeTransfer.transferToStr(fundCode) + "BondPosition");
        if (crawlBondDateStr == null)
            crawlBondQuarter = ConstantParameter.QUARTER_BASE;
        else
            crawlBondQuarter = new DateTransForm(crawlBondDateStr).getQuarterCount();
    }

    public boolean crawlFundPosition() throws IOException{
        positionFormArray.clear();

        //Crawl stock position from crawlStockQuarter to now, which crawlStockQuarter not include.
        crawlStockPosition();
        //Crawl bond position from crawlBondQuarter to now, which crawlBondQuarter not include.
        crawlBondPosition();

        /**
         * Store fund position to database, and config file.
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

    /**
     * Crawl stock position of fund.
     */
    private boolean crawlStockPosition() throws IOException{
        //Only can crawl stock position up to last quarter.
        //There are no position date of this quarter.
        if (crawlStockQuarter +1 == new DateTransForm().getQuarterCount())
            return true;

        //First crawl,get all years from url.
        String firstUrl = crawlStockUrl + ConstantParameter.YEAR_INVALID;
        Document firstDocument = Jsoup.connect(firstUrl).timeout(3000).get();
        String yearArrayStr = firstDocument.body().text();

        Pattern yearPattern = Pattern.compile("[0-9]{4}");
        Matcher yearMatcher = yearPattern.matcher(yearArrayStr);
        if (yearMatcher.find() == false)
            return true; //No stock position, don't need crawl.

        //Crawl position of all year in while loop.
        //endFlag = true, only crawl stock position from crawlStockQuarter to now;
        boolean endFlag = false;
        do {
            if (endFlag)
                break;

            String year = yearMatcher.group();
            String yearUrl = crawlStockUrl + year;

            Document yearDocument = Jsoup.connect(yearUrl).timeout(4000).get();
            //Crawl quarter position of this year.
            Elements quarterDiv = yearDocument.getElementsByClass("box");
            if (quarterDiv.size() < 1)
                continue;

            //Crawl each quarter.
            for (Element quarter : quarterDiv) {
                //First crawl position date, compare with crawlStockQuarter.
                Pattern datePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
                Matcher dateMatcher = datePattern.matcher(quarter.text());
                if (dateMatcher.find() == false) {
                    logger.error(String.format("Invalid date format of %s ", yearUrl));
                    return false;
                }
                String positionDate = dateMatcher.group();
                int positionQuarter = new DateTransForm(positionDate).getQuarterCount();
                if (positionQuarter <= crawlStockQuarter) {
                    endFlag = true;
                    break;
                }

                //Crawl stock position.
                Element stockTable = quarter.getElementsByTag("table").first().getElementsByTag("tbody").first();
                Elements stockRows = stockTable.getElementsByTag("tr");
                for (Element stock : stockRows) {
                    int count = stock.getElementsByTag("td").size();
                    String stockCode = stock.getElementsByTag("td").get(1).text();
                    String stockName = stock.getElementsByTag("td").get(2).text();
                    String stockProportionStr = stock.getElementsByTag("td").get(count-3).text();
                    Pattern proportionPattern = Pattern.compile("[0-9]+[\\.]?[0-9]*");
                    Matcher proportionMatcher = proportionPattern.matcher(stockProportionStr);
                    if (proportionMatcher.find() == false) {
                        logger.error(String.format("Invalid format of %s proportion, url is %s, date is %s", stockName, yearUrl, positionDate));
                        return false;
                    }
                    float stockProportion = Float.parseFloat(proportionMatcher.group());
                    //Insert into array.
                    FundPositionForm form = new FundPositionForm(fundCode,positionQuarter,1,stockCode,stockName,stockProportion);
                    positionFormArray.add(form);
                    /**
                     * Crawl info of each stock, which implement later.
                     */
                    //TODO: 20220328.
                    //String stockUrl = "https:" + stock.getElementsByTag("td").get(1).getElementsByTag("a").attr("href");
                }
            }
        } while (yearMatcher.find());
        return true;
    }

    private void crawlBondPosition() throws IOException{

    }
}