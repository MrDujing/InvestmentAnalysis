package crawl;

import form.FundValueForm;
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


public class FundValueCrawl {
    private Logger logger = LoggerFactory.getLogger(FundValueCrawl.class);
    private ArrayList<FundValueForm> fundHistoryValueArray = new ArrayList<>();
    private int crawlValueDate;//Crawl fund history value: (crawlValueDate, now].
    private String historyValueDate;
    private int fundCode;
    private String crawlValueUrlPrefix;

    public FundValueCrawl(int fundCode) {
        this.fundCode = fundCode;

        //acquire crawlValueDate.
        Properties properties = new PropertiesConfig("../crawldate.properties").getProperties();
        String crawlValueDateStr = properties.getProperty(FundCodeTransfer.transferToStr(fundCode) + "HistoryValue");

        if (crawlValueDateStr != null)
            crawlValueDate = new DateTransForm(crawlValueDateStr).getDateCount();
        else
            crawlValueDate = ConstantParameter.DATE_BASE + 1;
        //Crawl date :(crawlValueDate, Today].
        String code = FundCodeTransfer.transferToStr(fundCode);
        String sdate = new DateTransForm(crawlValueDate - 1).getDateStr();
        crawlValueUrlPrefix = ConstantParameter.VALUE_CRAWL_URL_PREFIX + String.format("&code=%s&sdate=%s", code, sdate);
    }


    /**
     * Crawl fund history value between (crawlValueDate, today].
     *
     * @throws IOException
     */
    public boolean crawlFundHistory() throws IOException {
        fundHistoryValueArray.clear();
        int totalPages = -1;
        /**
         * Retrieve total pages of fund history value.
         */
        String firstUrl = crawlValueUrlPrefix + String.format("&page=%d", ConstantParameter.DATE_INVALID);
        Document firstDocument = Jsoup.connect(firstUrl).timeout(10000).get();
        String firstText = firstDocument.body().text();
        Pattern pagePattern = Pattern.compile("pages:([0-9]+)");
        Matcher pageMatcher = pagePattern.matcher(firstText);
        if (pageMatcher.find()) {
            totalPages = Integer.parseInt(pageMatcher.group(1));//Retrieve total pages.
            if (totalPages <= 0) {
                logger.info("Don't have history value need to crawl, fund is {}, url is {}", fundCode, firstUrl);
                return true;
            }
        } else {
            logger.error("No total page, fund is {}, url is {}", fundCode, firstUrl);
            return false;
        }

        /**
         * Crawl history value from [1, totalPages].
         */
        //historyFlag=true, record recent date of history value, which would be wrote into config file.

        boolean historyFlag = true;
        for (int page = 1; page <= totalPages; page++) {
            String crawlUrl = crawlValueUrlPrefix + String.format("&page=%d", page);
            Document pageDocument = Jsoup.connect(crawlUrl).timeout(10000).get();

            //Crawl fund history value.
            Element pageTable = pageDocument.body().getElementsByTag("table").first().getElementsByTag("tbody").first();
            Elements pageRows = pageTable.getElementsByTag("tr");
            //Extract each row.
            for (Element row : pageRows) {
                //Crawl each row.
                String valueDate = row.getElementsByTag("td").get(0).text();
                String netValueStr = row.getElementsByTag("td").get(1).text();
                String totalValueStr = row.getElementsByTag("td").get(2).text();
                String increaseRateStr = row.getElementsByTag("td").get(3).text();
                //Judge each data.
                String valueMatch = "^\\d*\\.?\\d+$", rateMatch = "^-?\\d*\\.?\\d+%$";
                if (netValueStr.matches(valueMatch) && totalValueStr.matches(valueMatch) && increaseRateStr.matches(rateMatch)) {
                    //Parse crawl data.
                    float netValue = Float.parseFloat(netValueStr);
                    float totalValue = Float.parseFloat(totalValueStr);
                    Pattern ratePattern = Pattern.compile("(-?\\d*\\.?\\d+)%");
                    Matcher rateMatcher = ratePattern.matcher(increaseRateStr);
                    float dayIncreaseRate;
                    if (rateMatcher.find()) {
                        dayIncreaseRate = Float.parseFloat(rateMatcher.group(1));
                    } else {
                        logger.info("Don't crawl invalid data, fund is {}, date is {}", fundCode, valueDate);
                        continue;
                    }
                    //Record the most recent date, write into config file.
                    if (historyFlag) {
                        historyValueDate = valueDate;
                        historyFlag = false;
                    }
                    //Store into array.
                    FundValueForm form = new FundValueForm(fundCode, new DateTransForm(valueDate).getDateCount(), netValue, totalValue, dayIncreaseRate);
                    fundHistoryValueArray.add(form);
                } else {
                    logger.info("Don't crawl invalid data, fund is {}, date is {}", fundCode, valueDate);
                    continue;
                }
            }
        }
        //TODO 20220330, store into database, store into crawldate.properties.
        return true;
    }
}
