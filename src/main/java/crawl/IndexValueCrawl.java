package crawl;

import form.IndexValueForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.InputValidate;

import java.io.IOException;
import java.util.Vector;

public class IndexValueCrawl {
    private Logger logger = LoggerFactory.getLogger(IndexValueCrawl.class);
    private Vector<IndexValueForm> indexValueForms = new Vector<>();
    private String fileName;

    /**
     *
     * @param fileName: csv filename, with relative path.
     */
    public IndexValueCrawl(String fileName) {
        this.fileName = fileName;
    }

    private IndexValueCrawl() {
    }

    /**
     * Extract index value from csv file, and store data into database.
     *
     * @return true, if extract and store successfully; else false.
     * @throws IOException
     */
    public boolean storeIntoDatabase() throws IOException {
        String indexCode = InputValidate.analysisIndexFileName(fileName);
        if (null == indexCode)
            return false;
        //TODO
        return true;
    }
}
