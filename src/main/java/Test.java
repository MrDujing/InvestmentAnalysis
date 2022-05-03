import crawl.FundBaseInfoCrawl;
import crawl.FundPositionCrawl;
import crawl.FundValueCrawl;
import crawl.StockBaseInfoCrawl;
import util.StockType;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        new StockBaseInfoCrawl("AMZN", StockType.USSTOCK).crawlBaseInfo();
        new StockBaseInfoCrawl("03690", StockType.HKSTOCK).crawlBaseInfo();
        new StockBaseInfoCrawl("300035", StockType.HSSTOCK).crawlBaseInfo();
        new StockBaseInfoCrawl("600600", StockType.HSSTOCK).crawlBaseInfo();
    }
}
