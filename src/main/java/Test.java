import crawl.FundPositionCrawl;
import crawl.FundValueCrawl;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        FundValueCrawl crawl = new FundValueCrawl(519704);
        crawl.crawlFundHistory();
    }
}
