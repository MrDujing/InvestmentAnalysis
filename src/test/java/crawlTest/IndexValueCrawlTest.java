package crawlTest;

import crawl.IndexValueCrawl;
import org.junit.Test;

import java.io.IOException;

public class IndexValueCrawlTest {
    @Test
    public void testStoreIntoDatabase() throws IOException {
        IndexValueCrawl indexValueCrawl = new IndexValueCrawl("./input/CNT_20100101-20220930.csv");
        indexValueCrawl.storeIntoDatabase();
    }
}
