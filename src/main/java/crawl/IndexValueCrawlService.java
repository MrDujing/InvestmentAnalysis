package crawl;


import java.util.Vector;

public interface IndexValueCrawlService {
    /**
     * read index value from local csv file.
     * @param fileName
     * @return true : read and get data successfully, else false.
     */
    boolean readCSV(String fileName);

    /**
     * store index value into database.
     * @param tableName
     * @param columns
     * @return count which insert into database successfully, -1 means error.
     */
    int storeDatabase(String tableName, Vector columns);
}
