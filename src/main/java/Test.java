import crawl.FundBaseInfoCrawl;
import crawl.FundPositionCrawl;
import crawl.FundValueCrawl;
import crawl.StockBaseInfoCrawl;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConstantParameter;
import util.StockType;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        new StockBaseInfoCrawl("000568",StockType.HSSTOCK).crawlBaseInfo();
        new StockBaseInfoCrawl("02196",StockType.HKSTOCK).crawlBaseInfo();
        new StockBaseInfoCrawl("FB",StockType.USSTOCK).crawlBaseInfo();
    }
}
