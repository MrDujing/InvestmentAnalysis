package dao;

import form.IndexValueForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HikariCPDataSource;
import util.StoreDataByFile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

public class IndexValueDao {
    Logger logger = LoggerFactory.getLogger(IndexValueDao.class);
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private static final String database = HikariCPDataSource.getDatabaseName();
    private String indexCode, tableName;

    public IndexValueDao(String code, String table) {
        indexCode = code;
        tableName = table;
    }

    private IndexValueDao() {
    }

    /**
     * Store index value forms into database.
     * @param forms
     * @return -1: exception, cnt which succeed into database.
     */
    public int storeIntoDatabase(Vector<IndexValueForm> forms) {
        StringBuilder insertValue = new StringBuilder();

        for (IndexValueForm value : forms) {
            insertValue.append(indexCode).append("\t");
            insertValue.append(value.getDate()).append("\t");
            insertValue.append(value.getOpenPrice()).append("\t");
            insertValue.append(value.getClosePrice()).append("\t");
            insertValue.append(value.getHighPrice()).append("\t");
            insertValue.append(value.getLowPrice()).append("\t");
            insertValue.append(value.getTradeVolume()).append("\t");
            insertValue.append(value.getDayIncreaseRate()).append("\n");
        }

        //sql table columns.
        Vector<String> tableColumn = new Vector<>();
        tableColumn.add("index_code");
        tableColumn.add("date");
        tableColumn.add("open_price");
        tableColumn.add("close_price");
        tableColumn.add("high_price");
        tableColumn.add("low_price");
        tableColumn.add("trade_volume");
        tableColumn.add("day_increase_rate");

        int succeedInsertRows = new StoreDataByFile().insertMultipleData(tableName, tableColumn, insertValue);
        return succeedInsertRows;
    }

    //TODO, query database.

}
