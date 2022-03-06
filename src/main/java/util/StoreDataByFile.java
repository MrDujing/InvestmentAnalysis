package util;

import com.sun.deploy.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Insert multi data, by MYSQL LOCAL_INFILE.
 */
public class StoreDataByFile {

    private static final Logger logger = LoggerFactory.getLogger(StoreDataByFile.class);
    private static final Connection conn = HikariCPDataSource.getConnection();

    public StoreDataByFile() {

    }

    /**
     * Load InputStream to MYSQL
     *
     * @param loadDataSql SQL
     * @param dataStream  input stream
     * @return row count for succeed insertFundHistoryValue
     */
    private int bulkLoadFromInputStream(String loadDataSql, InputStream dataStream) throws SQLException {
        if (null == dataStream) {
            logger.info("input stream is null");
            return 0;
        }

        PreparedStatement statement = conn.prepareStatement(loadDataSql);
        int succeedCount = 0;
        try {
            if (statement.isWrapperFor(com.mysql.jdbc.Statement.class)) {
                com.mysql.jdbc.PreparedStatement mysqlStatement = statement.unwrap(com.mysql.jdbc.PreparedStatement.class);
                mysqlStatement.setLocalInfileInputStream(dataStream);
                succeedCount = mysqlStatement.executeUpdate();
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return succeedCount;
    }

    /**
     * insert multi data into MYSQL by MYSQL LOCAL_INFILE
     * with input stream, which ignore sql.csv.
     *
     * @param insertSql SQL
     * @param builder   data by StringBuilder
     */
    private int fastInsertData(String insertSql, StringBuilder builder) {
        int succeedRows = 0;
        InputStream inputStream = null;
        try {
            byte[] bytes = builder.toString().getBytes();
            inputStream = new ByteArrayInputStream(bytes);

            //bulk insertFundHistoryValue data
            succeedRows = bulkLoadFromInputStream(insertSql, inputStream);
        } catch (SQLException e) {
            logger.severe("bulkLoadFromInputStream failed");
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != conn) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return succeedRows;
    }

    /**
     * call to insertFundHistoryValue multiple data.
     *
     * @param dataBaseName     database
     * @param tableName        table which to insertFundHistoryValue
     * @param tableColumns     columns which to insertFundHistoryValue
     * @param tableColumnValue all data to insertFundHistoryValue
     */
    public int insertMultipleData(String dataBaseName, String tableName, Vector<String> tableColumns, StringBuilder tableColumnValue) {

        //join insertFundHistoryValue sql
        String insertColumnName = StringUtils.join(tableColumns, ",");
        String insertSql = "LOAD DATA LOCAL INFILE 'sql.csv' INTO TABLE " + dataBaseName + "." + tableName + " (" + insertColumnName + ")";

        int succeedInsertRows = fastInsertData(insertSql, tableColumnValue);
        return succeedInsertRows;
    }
}

