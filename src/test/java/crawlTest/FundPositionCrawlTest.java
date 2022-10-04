package crawlTest;

import crawl.FundPositionCrawl;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class FundPositionCrawlTest {
    @Test
    public void crawlFundPositionTest() throws IOException {
        FundPositionCrawl positionCrawl = new FundPositionCrawl(519697);
        boolean result = positionCrawl.crawlFundPosition();
        Assert.assertTrue(result);
    }
}
