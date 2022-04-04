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

    public int insertFundBaseInfoSingle(FundBaseForm baseInfo) {

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

            }
        } catch (SQLException e) {
            logger.error("Failed, sql is {}", querySql);
            e.printStackTrace();
        } finally {
            conn.close();
            stmt.close();
        }
        return baseInfo;

    }
}
