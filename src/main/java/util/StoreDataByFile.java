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
    private static final String databaseName = HikariCPDataSource.getDatabaseName();

    public StoreDataByFile() {

    }

    /**
     * insert multi data into MYSQL by MYSQL LOCAL_INFILE
     * with input stream, which ignore sql.csv.
     *
     * @param insertSql SQL
     * @param builder   data by StringBuilder
     */
    private int fastInsertData(String insertSql, StringBuilder builder) {
        int succeedRows = -1;// Record succeed rows inserted;
        byte[] bytes = builder.toString().getBytes();
        PreparedStatement statement = null;
        try (InputStream insertDataStream = new ByteArrayInputStream(bytes)) {
            statement = conn.prepareStatement(insertSql);
            if (statement.isWrapperFor(com.mysql.jdbc.Statement.class)) {
                com.mysql.jdbc.PreparedStatement mysqlStatement = statement.unwrap(com.mysql.jdbc.PreparedStatement.class);
                mysqlStatement.setLocalInfileInputStream(insertDataStream);
                succeedRows = mysqlStatement.executeUpdate();
            }
        } catch (IOException e) {
            logger.error("Construct to InputStream failed");
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error("Insert data by file failed, insertSql is {}", insertSql);
            e.printStackTrace();
        } finally {
            try {
                if (null != conn)
                    conn.close();
                if (null != statement)
                    statement.close();
            } catch (SQLException e) {
                logger.error("close connection or statement failed");
                e.printStackTrace();
            }
        }
        return succeedRows;
    }

    /**
     * call to insertFundHistoryValue multiple data.
     *
     * @param tableName        table which to insertFundHistoryValue
     * @param tableColumns     columns which to insertFundHistoryValue
     * @param tableColumnValue all data to insertFundHistoryValue
     */
    public int insertMultipleData(String tableName, Vector<String> tableColumns, StringBuilder tableColumnValue) {
        //join insertFundHistoryValue sql
        String insertColumnName = StringUtils.join(tableColumns, ",");
        String insertSql = "LOAD DATA LOCAL INFILE 'sql.csv' IGNORE INTO TABLE " + databaseName + "." + tableName + " (" + insertColumnName + ")";

        return fastInsertData(insertSql, tableColumnValue);
    }
}

