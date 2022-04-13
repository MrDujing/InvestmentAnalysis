import crawl.FundBaseInfoCrawl;
import crawl.FundPositionCrawl;
import crawl.FundValueCrawl;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
/*        int fundCode = 1993;

        FundValueCrawl crawlValue = new FundValueCrawl(fundCode);
        crawlValue.crawlFundHistory();

        FundPositionCrawl crawlPosition = new FundPositionCrawl(fundCode);
        crawlPosition.crawlFundPosition();*/
new FundBaseInfoCrawl().crawlFundBaseInfoEntire();
    }
}
