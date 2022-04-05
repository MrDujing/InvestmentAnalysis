package dao;

import form.FundBaseForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HikariCPDataSource;
import util.StoreDataByFile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class FundBaseDao {
    private Logger logger = LoggerFactory.getLogger(FundValueDao.class);
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private static final String database = HikariCPDataSource.getDatabaseName();

    /**
     * insert fund base info into database, which apply to big data.
     *
     * @param baseInfoArray
     * @return -1: insert failed, else succeed, and return succeed rows.
     */
    public int insertFundBaseInfoArray(Vector<FundBaseForm> baseInfoArray) {
        //Construct insertData for all data.
        StringBuilder insertData = new StringBuilder();
        for (FundBaseForm baseInfo : baseInfoArray) {
            insertData.append(baseInfo.getFundCode()).append("\t");
            insertData.append(baseInfo.getNamePinyinAbbr()).append("\t");
            insertData.append(baseInfo.getFundName()).append("\t");
            insertData.append(baseInfo.getFundProperty()).append("\t");
            insertData.append(baseInfo.getNamePinyinFull()).append("\n");
        }

        //Construct table column, which insert into.
        Vector<String> tableColumn = new Vector<>();
        tableColumn.add("fund_code");
        tableColumn.add("name_pinyin_abbr");
        tableColumn.add("fund_name");
        tableColumn.add("fund_property");
        tableColumn.add("name_pinyin_full");

        int succeedInsertRows = new StoreDataByFile().insertMultipleData("fund_base_info", tableColumn, insertData);
        return succeedInsertRows;//May be smaller than baseInfoArray.size();
    }

    /**
     * Insert one data into database.
     *
     * @param baseInfo base info of fund.
     * @return succeed rows, -1 : failed insert.
     */
    public int insertFundBaseInfoSingle(FundBaseForm baseInfo) {
        //Retrieve info
        int fundCode = baseInfo.getFundCode();
        String namePinyinAbbr = baseInfo.getNamePinyinAbbr();
        String fundName = baseInfo.getFundName();
        int property = baseInfo.getFundProperty();
        String namePinyinFull = baseInfo.getNamePinyinFull();
        //Check if baseInfo exist in database.
        FundBaseForm queryResult = queryFundBaseInfo(fundCode);
        //if exist
        String insertSql = String.format("INSERT REPLACE INTO %s.fund_base_info (fund_code, name_pinyin_abbr, fund_name, fund_property, name_pinyin_full) " +
                "VALUES (%d, %s, %s, %d ,%s)", database, fundCode, namePinyinAbbr, fundName, property, namePinyinFull);
        if (queryResult != null)
            logger.info("%d info exist, name is %s, property is %d, all the info will be replaced", fundCode, fundName, property);
        //insert into database, replaced old info if exist.
        int insertRows = -1;
        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            insertRows = stmt.executeUpdate(insertSql);
        } catch (SQLException e) {
            logger.error("Failed, construct connection, sql is {}", insertSql);
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                stmt.close();
            } catch (SQLException e) {
                logger.error("Failed, close database resources. sql is {}", insertSql);
            }
        }
        return insertRows;
    }

    /**
     * Acquire base info of fund.
     *
     * @param code
     * @return null: con't query anything, else query succeed.
     */
    public FundBaseForm queryFundBaseInfo(int code) {
        FundBaseForm baseInfo = null;
        String querySql = String.format("SELECT * FROM %s.fund_base_info t WHERE t.fund_code = %d", database, code);

        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(querySql);
            //Parse query result.
            if (rs.next()) {
                int fundCode = rs.getInt("fund_code");
                String namePinyinAbbr = rs.getString("name_pinyin_abbr");
                String fundName = rs.getString("fund_name");
                int property = rs.getInt("fund_property");
                String namePinyinFull = rs.getString("name_pinyin_full");
                baseInfo = new FundBaseForm(fundCode, namePinyinAbbr, fundName, property, namePinyinFull);
            } else
                baseInfo = null;
        } catch (SQLException e) {
            logger.error("Failed, sql is {}", querySql);
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                stmt.close();
                rs.close();
            } catch (SQLException e) {
                logger.error("Failed, close database resources, sql is {}", querySql);
                e.printStackTrace();
            }
        }
        return baseInfo;
    }

    /**
     * Get reference of fund property, by query table reference_index.
     *
     * @param property property of fund.
     * @return reference of property.
     */
    public int getPropertyReference(String property) {
        int result = 0;
        String querySql = String.format("SELECT reference_id FROM %s.reference_index t WHERE t.property = %s", database, property);
        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(querySql);
            if (rs.next()) {
                result = rs.getInt("reference_id");
                logger.info("reference of {} is {}", property, result);
            } else {
                logger.info("Don't crawl reference for {}", property);
            }
        } catch (SQLException e) {
            logger.error("Failed crawl reference for {}", property);
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                stmt.close();
                rs.close();
            } catch (SQLException e) {
                logger.error("Failed close database resources");
                e.printStackTrace();
            }
        }
        return result;
    }


    public Map<String, Integer> getPropertyReferenceArray() {
        Map<String, Integer> referenceTable = new HashMap<>();
        String querySql = String.format("SELECT * FROM %s.reference_index", database);
        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(querySql);
            while (rs.next()) {
                //TODO,20220405
                referenceTable.put(rs.getString("property"), rs.getInt());
            }

        } catch (SQLException e) {
            logger.error("Failed crawl reference for {}", property);
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                stmt.close();
                rs.close();
            } catch (SQLException e) {
                logger.error("Failed close database resources");
                e.printStackTrace();
            }
        }
    }
}
