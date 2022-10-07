package crawlTest;

import crawl.IndexValueCrawl;
import org.junit.Test;

import java.io.IOException;

public class IndexValueCrawlTest {
    @Test
    public void testStoreIntoDatabase() throws IOException {
        IndexValueCrawl indexValueCrawl = new IndexValueCrawl("./input/SP500_20000101-20191114.csv");
        indexValueCrawl.storeIntoDatabase();
    }
}
