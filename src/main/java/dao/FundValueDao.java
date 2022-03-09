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
     * @return true: all history value inserted succeed, else false.
     */
    public boolean insertFundHistoryValue(ArrayList<FundValueForm> valueArray) {
        int allCount = valueArray.size();
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

        int succeedInsertRows = new StoreDataByFile().insertMultipleData("asset_history_value", tableColumn, insertValue);
        return (succeedInsertRows == allCount) ? true : false;
    }

    /**
     * query database, retrieve history value from start to end.
     * start date is inclusive, and end data is exclusive.
     *
     * @param code  asset code
     * @param start start date, include
     * @param end   end date, exclude, if end <= 0, retrieve all date.
     * @return query result between start and end.
     */
    public ArrayList queryFunHistoryValue(final int code, final int start, final int end) {
        FundValueForm historyValueForm = null;
        ArrayList<FundValueForm> historyValueArray = new ArrayList<>();
        String sql = "";
        //query by asset code.
        if (code > 0 && end > start && start >= 0) {
            sql = "SELECT * FROM asset_history_value h WHERE h.asset_code = " + code + " AND h.day_increase_rate <> 0"
                    + " AND h.value_date BETWEEN " + start + " AND " + (end - 1)
                    + " ORDER BY h.value_date";
        } else if (code > 0 && end <= 0) {
            sql = "SELECT * FROM asset_history_value h WHERE h.asset_code = " + code + " AND h.day_increase_rate <> 0"
                    + " ORDER BY h.value_date";
        } else {
            logger.info("FundValueDao.queryFunHistoryValue failed");
        }

        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            logger.severe("FundValueDao.queryFunHistoryValue, sql connection failed");
            e.printStackTrace();
        }

        try {
            while (rs.next()) {
                historyValueForm = new FundValueForm(rs.getInt(1), rs.getInt(2),rs.getInt(3),
                        rs.getFloat(4), rs.getFloat(5), rs.getFloat(6));
                historyValueArray.add(historyValueForm);
            }
        } catch (SQLException e) {
            logger.severe("FundValueDao.queryFunHistoryValue, ResultSet.next failed");
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.severe("FundValueDao.queryFunHistoryValue, close failed");
                e.printStackTrace();
            }
        }
        return historyValueArray;
    }
}
