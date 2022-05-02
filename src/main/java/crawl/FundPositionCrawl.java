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
import java.util.HashMap;
import java.util.Map;
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
    private String recentPositionQuarterStock, recentPositionQuarterBond;
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
        Properties properties = new PropertiesConfig("./config/crawldate.properties", false).getProperties();
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

    public boolean crawlFundPosition() throws IOException {
        positionFormArray.clear();

        //Crawl stock position from crawlStockQuarter to now, which crawlStockQuarter not include.
        boolean stockResult = crawlAssetPosition(crawlStockQuarter, crawlStockUrl, ConstantParameter.STOCK);
        //Crawl bond position from crawlBondQuarter to now, which crawlBondQuarter not include.
        boolean bondResult = crawlAssetPosition(crawlBondQuarter, crawlBondUrl, ConstantParameter.BOND);

        if (stockResult && bondResult && positionFormArray.size() >= 0) {
            if (positionFormArray.size() == 0) {
                logger.info(String.format("Don't have any position need to be crawled, "));
                return true;
            }

            //Store fund position into database.
            int insertRows = new FundPositionDao().insertFundPosition(positionFormArray);
            if (insertRows >= 0) {
                logger.info(String.format("Succeed,Store %d position into database, insert rows are %d", fundCode, insertRows));
            } else {
                logger.warn(String.format("Crawl fund position failed, fund %d", fundCode));
                return false;//Crawl asset position failed.
            }

            //Store crawl quarter into crawldate.properties.
            Map<String, String> crawlDate = new HashMap<>();
            //If don't crawl anything, then recentPositionQuarterStock/Bond will be null,so consider it.
            if (recentPositionQuarterStock != null)
                crawlDate.put(FundCodeTransfer.transferToStr(fundCode) + "StockPosition", recentPositionQuarterStock);
            if (recentPositionQuarterBond != null)
                crawlDate.put(FundCodeTransfer.transferToStr(fundCode) + "BondPosition", recentPositionQuarterBond);
            new PropertiesConfig("./config/crawldate.properties", false).updateProperties(crawlDate);
            return true;
        } else {
            logger.warn(String.format("Crawl fund position failed, fund %d", fundCode));
            return false;//Crawl asset position failed.
        }
    }

    /**
     * Crawl asset position of bond or stock.
     *
     * @param crawlQuarter crawl position from crawlQuarter to now, which crawlQuarter not include.
     * @param crawlUrl     crawl url.
     * @param property     0-unknown, 1-stock, 2-bond
     * @return true: crawl successfully.
     * @throws IOException
     */
    private boolean crawlAssetPosition(int crawlQuarter, String crawlUrl, int property) throws IOException {
        int proportionOffset;//For crawl proportion of asset.
        if (ConstantParameter.STOCK == property)
            proportionOffset = 3;
        else if (ConstantParameter.BOND == property)
            proportionOffset = 2;
        else
            return false;//Just crawl bond or stock.
        //Only can crawl asset position up to last quarter.
        //There are no position date of this quarter.
        if (crawlQuarter + 1 == new DateTransForm().getQuarterCount())
            return true;

        //First crawl,get all years from url.
        String firstUrl = crawlUrl + ConstantParameter.YEAR_INVALID;
        Document firstDocument = Jsoup.connect(firstUrl).timeout(10000).get();
        String yearArrayStr = firstDocument.body().text();

        Pattern yearPattern = Pattern.compile("[0-9]{4}");
        Matcher yearMatcher = yearPattern.matcher(yearArrayStr);
        if (yearMatcher.find() == false)
            return true; //No position, don't need crawl.

        //Crawl position of all year in while loop.
        //endFlag = true, only crawl position from crawlQuarter to now;
        //recentQuarter=true, select most recent quarter of position.
        boolean endFlag = false, recentQuarter = true;
        do {
            if (endFlag)
                break;

            String year = yearMatcher.group();
            String yearUrl = crawlUrl + year;

            Document yearDocument = Jsoup.connect(yearUrl).timeout(10000).get();
            //Crawl quarter position of this year.
            Elements quarterDiv = yearDocument.getElementsByClass("box");
            if (quarterDiv.size() < 1)
                continue;

            //Crawl each quarter.
            for (Element quarter : quarterDiv) {
                //First crawl position date, compare with crawlQuarter.
                Pattern datePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
                Matcher dateMatcher = datePattern.matcher(quarter.text());
                if (dateMatcher.find() == false) {
                    logger.error(String.format("Invalid date format of %s ", yearUrl));
                    return false;
                }
                String positionDate = dateMatcher.group();
                int positionQuarter = new DateTransForm(positionDate).getQuarterCount();
                //Record recent quarter of position, would be write into config file.
                if (recentQuarter) {
                    switch (property) {
                        case ConstantParameter.STOCK:
                            recentPositionQuarterStock = positionDate;
                            break;
                        case ConstantParameter.BOND:
                            recentPositionQuarterBond = positionDate;
                            break;
                        default:
                            break;

                    }
                    recentQuarter = false;
                }
                //Don't crawl if have crawled before.
                //crawlQuarter:last crawl date; positionQuarter: this crawl date.
                if (positionQuarter <= crawlQuarter) {
                    endFlag = true;
                    break;
                }

                //Crawl asset position.
                Element assetTable = quarter.getElementsByTag("table").first().getElementsByTag("tbody").first();
                Elements assetRows = assetTable.getElementsByTag("tr");
                for (Element asset : assetRows) {
                    int count = asset.getElementsByTag("td").size();
                    String assetCodeTemp = asset.getElementsByTag("td").get(1).text();
                    //Cut assetCode by dot.
                    String assetCode = null;
                    if (assetCodeTemp.indexOf(".") > -1) {
                        assetCode = assetCodeTemp.split(".")[0];
                    } else {
                        assetCode = assetCodeTemp;
                    }
                    String assetName = asset.getElementsByTag("td").get(2).text();
                    String assetProportionStr = asset.getElementsByTag("td").get(count - proportionOffset).text();
                    Pattern proportionPattern = Pattern.compile("[0-9]+[\\.]?[0-9]*");
                    Matcher proportionMatcher = proportionPattern.matcher(assetProportionStr);
                    if (proportionMatcher.find() == false) {
                        logger.error(String.format("Invalid format of %s proportion, url is %s, date is %s", assetName, yearUrl, positionDate));
                        return false;
                    }
                    float assetProportion = Float.parseFloat(proportionMatcher.group());
                    //Insert into array.
                    FundPositionForm form = new FundPositionForm(fundCode, positionQuarter, property, assetCode, assetName, assetProportion);
                    positionFormArray.add(form);
                    /**
                     * Crawl info of each asset, which implement later.
                     * Bond position don't have assetUrl.
                     */
                    //TODO: 20220328.
                    //String assetUrl = "https:" + asset.getElementsByTag("td").get(1).getElementsByTag("a").attr("href");
                }
            }
        } while (yearMatcher.find());
        return true;
    }
}