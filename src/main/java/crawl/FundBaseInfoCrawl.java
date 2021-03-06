package crawl;

import dao.FundBaseDao;
import form.FundBaseForm;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConstantParameter;
import util.FundCodeTransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FundBaseInfoCrawl {
    private Logger logger = LoggerFactory.getLogger(FundBaseInfoCrawl.class);
    private int fundCode;
    private String crawlUrl;
    private CloseableHttpClient httpClient;
    private HttpGet httpGet;
    private FundBaseDao fundBaseDao;

    /**
     * Crawl base info for code.
     *
     * @param code fund code.
     */
    public FundBaseInfoCrawl(int code) {
        this();
        fundCode = code;
    }

    /**
     * Default constructor.
     */
    public FundBaseInfoCrawl() {
        crawlUrl = ConstantParameter.FUND_BASE_INFO_URL;
        //Construct http client.
        httpClient = HttpClients.createDefault();
        //Construct http get.
        httpGet = new HttpGet(ConstantParameter.FUND_BASE_INFO_URL);
        //Add header, pretend to be website browser.
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3573.0 Safari/537.36");
        fundBaseDao = new FundBaseDao();
    }

    /**
     * Crawl base info of all fund from website.
     */
    public boolean crawlFundBaseInfoEntire() {
        String fundBaseInfoStrRaw = null, fundBaseInfoStr = null;
        //Crawl data from website.
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                //Succeed crawl fund data.
                fundBaseInfoStrRaw = EntityUtils.toString(response.getEntity(), "utf-8");
            } else {
                //Failed crawl fund data.
                logger.error("Failed, con't get response from {}", crawlUrl);
                return false;
            }

        } catch (IOException e) {
            logger.error("Failed, con't get response from website");
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("Failed to close httpClient");
                e.printStackTrace();
            }
        }

        //Parse string crawled from website.
        Pattern pattern = Pattern.compile("(\\[\\[.+\\]\\])");
        Matcher matcher = pattern.matcher(fundBaseInfoStrRaw);
        if (matcher.find()) {
            fundBaseInfoStr = matcher.group(1);
            logger.info("Succeed crawl all fund base info from website");
        } else {
            logger.info("Failed crawl all base info from website, raw data is {}", fundBaseInfoStrRaw);
            return false;
        }

        //Acquire all property from database.
        Map<String, Integer> propertyReferences = fundBaseDao.getPropertyReferenceArray();
        //Parse data string by json.
        final JSONArray baseInfoJsonArray = new JSONArray(fundBaseInfoStr);
        ArrayList<FundBaseForm> baseInfoArray = new ArrayList<>();
        for (int i = 0; i < baseInfoJsonArray.length(); i++) {
            JSONArray baseInfo = baseInfoJsonArray.getJSONArray(i);
            //Info of each fund.
            int fundCode = Integer.parseInt(baseInfo.getString(0));
            String pinyinAbbr = baseInfo.getString(1);
            String fundName = baseInfo.getString(2);
            int property = propertyReferences.get(baseInfo.getString(3));
            String pinyinFull = baseInfo.getString(4);

            FundBaseForm baseForm = new FundBaseForm(fundCode, pinyinAbbr, fundName, property, pinyinFull);
            baseInfoArray.add(baseForm);
        }

        //Store into database
        int insertRows = fundBaseDao.insertFundBaseInfoArray(baseInfoArray);
        if (insertRows == -1) {
            logger.error("Insert fund base info into database failed");
            return false;
        } else {
            logger.info("Crawl fund {}, insert into database {}", baseInfoArray.size(), insertRows);
            return true;
        }
    }

    /**
     * Crawl base info of code, just crawl one info.
     * crawl data while don't exist in database.
     *
     * @param code fund code.
     * @return boolean:crawl succeed; else, crawl failed.
     */
    public boolean crawlFundBaseInfo(int code) {
        String fundBaseInfoStrRaw = null;
        //Crawl data from website.
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                //Succeed crawl fund data.
                fundBaseInfoStrRaw = EntityUtils.toString(response.getEntity(), "utf-8");
            } else {
                //Failed crawl fund data.
                logger.error("Failed,can't crawl base info of {} from {}", code, crawlUrl);
                return false;
            }

        } catch (IOException e) {
            logger.error("Failed, con't get response from website while crawl base info of {}", code);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("Failed to close httpClient while crawl base info of {}", code);
                e.printStackTrace();
            }
        }

        if (fundBaseInfoStrRaw == null) {
            logger.error("Don't have info in website {}", crawlUrl);
            return false;
        }

        //parse base info of code from response.
        Pattern pattern = Pattern.compile(String.format(",(\\[\"(%s)\",\"([A-Z]+)\",\"([\\u4E00-\\u9FA5\\(\\)A-Z]+)\",\"([\\u4E00-\\u9FA5\\(\\)\\-A-Z]+)\",\"([A-Z]+)\"\\]),", FundCodeTransfer.transferToStr(code)));
        Matcher matcher = pattern.matcher(fundBaseInfoStrRaw);
        if (matcher.find()) {
            String findResult = matcher.group(1);
            if (findResult == null) {
                logger.error("Failed, regex parse failed ,can't find base info of {}", code);
                return false;
            }

            //Acquire all property from database.
            Map<String, Integer> propertyReferences = fundBaseDao.getPropertyReferenceArray();

            //Parse json from findResult.
            JSONArray baseInfoObject = new JSONArray(findResult);
            int fundCode = Integer.parseInt(baseInfoObject.getString(0));
            String pinyinAbbr = baseInfoObject.getString(1);
            String fundName = baseInfoObject.getString(2);
            int property = propertyReferences.get(baseInfoObject.getString(3));
            String pinyinFull = baseInfoObject.getString(4);

            FundBaseForm baseForm = new FundBaseForm(fundCode, pinyinAbbr, fundName, property, pinyinFull);
            int insertResult = fundBaseDao.insertFundBaseInfoSingle(baseForm);
            if (insertResult != -1) {
                logger.info("Succeed, crawl {} base info, and insert database succeed", code);
            } else {
                logger.error("Failed, insert into database failed, code is {}, insert row is {}", code, insertResult);
                return false;
            }
        }
        return true;
    }
}
