package crawl;

import dao.StockBaseInfoDao;
import form.StockBaseInfoForm;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConstantParameter;
import util.StockType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                crawlUrl = ConstantParameter.US_COMPANY_INFO + code;
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
        //Crawl base info by selenium driver.
        System.setProperty("webdriver.chrome.driver","./config/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));//Set time delay.
        driver.manage().window().minimize();
        driver.get(crawlUrl);

        //Judge document is valid.
        if (crawlDocument == null) {
            logger.warn("Can't crawl anything from {}", crawlUrl);
            return false;
        }
        Pattern validPattern = Pattern.compile("公司名称");
        Matcher validMatcher = validPattern.matcher(crawlDocument.text());
        if (validMatcher.find() == false) {
            //crawlDocument is invalid.
            logger.warn("Crawl document is invalid, url is {}, code is {}, stock type is {}", crawlUrl, stockCode, stockType);
            return false;
        }

        //Parse info from document.
        String name, industry, type;
        switch (stockType) {
            case USSTOCK:
                Elements zqzlElementUS = crawlDocument.getElementById("div_zqzl").getElementsByTag("tr");
                type = zqzlElementUS.get(1).getElementsByTag("td").get(1).text();

                Elements gszlElementUS = crawlDocument.getElementById("div_gszl").getElementsByTag("tr");
                name = gszlElementUS.get(0).getElementsByTag("td").get(1).text();
                industry = gszlElementUS.get(2).getElementsByTag("td").get(1).text();
                break;
            case HKSTOCK:
                Elements zqzlElementHK = crawlDocument.getElementById("tlp_data").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                type = zqzlElementHK.get(1).getElementsByTag("td").get(3).text();

                Elements gszlElementHK = crawlDocument.getElementById("tlp_data").getElementsByTag("tbody").get(1).getElementsByTag("tr");
                name = gszlElementHK.get(0).getElementsByTag("td").get(1).text();
                industry = gszlElementHK.get(2).getElementsByTag("td").get(3).text();
                break;
            case HSSTOCK:
                Elements allElementHS = crawlDocument.getElementById("Table0").getElementsByTag("tr");
                type = allElementHS.get(6).getElementsByTag("td").get(1).text();
                name = allElementHS.get(0).getElementsByTag("td").get(1).text();
                industry = allElementHS.get(6).getElementsByTag("td").get(3).text();
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
