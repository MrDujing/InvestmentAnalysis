package crawl;

import dao.StockBaseInfoDao;
import form.StockBaseInfoForm;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConstantParameter;
import util.StockType;

import java.io.IOException;

public class StockBaseInfoCrawl {
    private Logger logger = LoggerFactory.getLogger(StockBaseInfoCrawl.class);
    private String stockCode;
    private StockType stockType;
    private String crawlUrl = null;

    private StockBaseInfoCrawl() {
    }

    public StockBaseInfoCrawl(String code, StockType type) {
        stockCode = code;
        stockType = type;
        switch (type) {
            case USSTOCK:
                crawlUrl = ConstantParameter.US_COMPANY_INFO + code + ".O";
                break;
            case HKSTOCK:
                crawlUrl = ConstantParameter.HK_COMPANY_PROFILE + code;
                break;
            case HSSTOCK:
                char firstChar = code.charAt(0);
                if ('0' == firstChar || '1' == firstChar || '2' == firstChar || '3' == firstChar)
                    crawlUrl = ConstantParameter.HS_COMPANY_SURVEY + "SZ" + code;
                else if ('6' == firstChar || '7' == firstChar || '9' == firstChar)
                    crawlUrl = ConstantParameter.HS_COMPANY_SURVEY + "SH" + code;
                else
                    logger.error("Can't judge which type {} belongs to, and type is {}", code, type);
                break;
            default:
                logger.error("Can't judge which type {} belongs to, it't type is {}", code, type);
        }

        if (crawlUrl == null) {
            logger.error("crawl url is null ,can't crawl anything");
        }

    }

    public boolean crawlBaseInfo() throws IOException {
        if (crawlUrl == null) {
            logger.warn("crawl url is null ,code is {}, stock type is {}", stockCode, stockType);
            return false;
        }
        //Set crawl client
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(crawlUrl);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3573.0 Safari/537.36");
        //Crawl stock base info.
        String stockBaseInfoStr = null;
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                //Succeed.
                stockBaseInfoStr = EntityUtils.toString(response.getEntity(), "utf-8");
            } else {
                //Failed.
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

        if (stockBaseInfoStr == null)
            return false;
        //Parse json object.
        JSONObject stockInfoJson = new JSONObject(stockBaseInfoStr);
        //Parse info from document.
        String name, industry, type;
        switch (stockType) {
            case USSTOCK:
                JSONObject zqzlUS = stockInfoJson.getJSONObject("data").getJSONArray("zqzl").getJSONObject(0);
                type = zqzlUS.getString("SECURITYTYPE");

                JSONObject gszlUS = stockInfoJson.getJSONObject("data").getJSONArray("gszl").getJSONObject(0);
                name = gszlUS.getString("COMPNAME");
                industry = gszlUS.getString("INDUSTRY");
                break;
            case HKSTOCK:
                JSONObject zqzlHK = stockInfoJson.getJSONObject("zqzl");
                type = zqzlHK.getString("zqlx");

                JSONObject gszlHK = stockInfoJson.getJSONObject("gszl");
                name = gszlHK.getString("gsmc");
                industry = gszlHK.getString("sshy");
                break;
            case HSSTOCK:
                JSONObject jbzlHS = stockInfoJson.getJSONArray("jbzl").getJSONObject(0);
                type = jbzlHS.getString("SECURITY_TYPE");
                name = jbzlHS.getString("ORG_NAME");
                industry = jbzlHS.getString("EM2016");
                break;
            default:
                type = null;
                name = null;
                industry = null;
        }

        if (type == null || name == null || industry == null) {
            logger.warn("Can't crawl info from web, code is {}, url is {}, type is {}", stockCode, crawlUrl, stockType);
            return false;
        }

        StockBaseInfoForm infoForm = new StockBaseInfoForm(stockCode, name, industry, type);
        boolean insertFlag = new StockBaseInfoDao().insertStockBaseInfo(infoForm);
        return insertFlag;
    }
}
