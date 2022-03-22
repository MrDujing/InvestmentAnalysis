import crawl.FundPositionCrawl;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        FundPositionCrawl crawl = new FundPositionCrawl(519704);
        crawl.crawlFundPosition();
    }
}
