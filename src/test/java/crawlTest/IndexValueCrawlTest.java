package crawlTest;

import crawl.IndexValueCrawl;
import org.junit.Test;

import java.io.IOException;

public class IndexValueCrawlTest {
    @Test
    public void testStoreIntoDatabase() throws IOException {
        IndexValueCrawl indexValueCrawl = new IndexValueCrawl("./input/SP500_20191115-20221004.csv");
        indexValueCrawl.storeIntoDatabase();
    }
}
