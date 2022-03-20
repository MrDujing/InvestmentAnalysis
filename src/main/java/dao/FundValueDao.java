package dao;

import form.FundValueForm;
import util.HikariCPDataSource;
import util.StoreDataByFile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class FundValueDao {
    private Logger logger = LoggerFactory.getLogger(FundValueDao.class);
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    /**
     * insert history value of asset.
     *
     * @param valueArray ArrayList, stored all history value form.
     * @return insert rows.
     */
    public int insertFundHistoryValue(ArrayList<FundValueForm> valueArray) {
        StringBuilder insertValue = new StringBuilder();
        //All history value, need to be insert.
        for (FundValueForm value : valueArray) {
            insertValue.append(value.getFundCode()).append("\t");
            insertValue.append(value.getDate()).append("\t");
            insertValue.append(value.getNetValue()).append("\t");
            insertValue.append(value.getTotalValue()).append("\t");
            insertValue.append(value.getDayIncreaseRate()).append("\n");
        }

        //sql table columns.
        Vector<String> tableColumn = new Vector<>();
        tableColumn.add("fund_code");
        tableColumn.add("date");
        tableColumn.add("net_value");
        tableColumn.add("total_value");
        tableColumn.add("day_increase_rate");

        int succeedInsertRows = new StoreDataByFile().insertMultipleData("fund_value", tableColumn, insertValue);
        return succeedInsertRows;
    }

    /**
     * query database, retrieve history value from start to end.
     * start date is inclusive, and end data is exclusive.
     * It means no day_increase_rate if day_increase_rate equal to 999,
     * so the sql cull it.
     *
     * @param code  asset code
     * @param start start date, include
     * @param end   end date, exclude.
     * @return query result between start and end.
     */
    public ArrayList queryFunHistoryValue(final int code, final int start, final int end) {
        FundValueForm fundValueForm = null;
        ArrayList<FundValueForm> fundValueArray = new ArrayList<>();
        String sql = "";
        //query by asset code.
        if (code > 0 && end > start && start >= 0) {
            sql = "SELECT * FROM investment_data.fund_value h WHERE h.fund_code = " + code + " AND h.day_increase_rate <> 999"
                    + " AND h.date BETWEEN " + start + " AND " + (end - 1)
                    + " ORDER BY h.date";
        } else return null;

        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            logger.error("FundValueDao.queryFunHistoryValue, sql connection failed");
            e.printStackTrace();
        }

        try {
            while (rs.next()) {
                fundValueForm = new FundValueForm(rs.getInt("fund_code"), rs.getInt("date"), rs.getFloat("net_value"),
                        rs.getFloat("total_value"), rs.getFloat("day_increase_rate"));
                fundValueArray.add(fundValueForm);
            }
        } catch (SQLException e) {
            logger.error("FundValueDao.queryFunHistoryValue, ResultSet.next failed");
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.error("FundValueDao.queryFunHistoryValue, close failed");
                e.printStackTrace();
            }
        }
        return fundValueArray;
    }
}
