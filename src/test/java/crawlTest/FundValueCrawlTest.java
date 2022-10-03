package crawlTest;

import crawl.FundValueCrawl;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class FundValueCrawlTest {

    @Test
    public void crawlFundHistoryTest() throws IOException {
        FundValueCrawl fundValueCrawl = new FundValueCrawl(519697);
        boolean result = fundValueCrawl.crawlFundHistory();
        Assert.assertTrue(result);
    }
}
