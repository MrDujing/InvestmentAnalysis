package crawl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class AbstractIndexValueCrawl implements IndexValueCrawlService {
    private Logger logger = LoggerFactory.getLogger(AbstractIndexValueCrawl.class);

    @Override
    public boolean readCSV(String fileName) {return true;}

    @Override
    public int storeDatabase(String table, Vector columns) {return 1;}

}
