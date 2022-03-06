package dao;

import form.AssetHistoryValueForm;
import util.HikariCPDataSource;
import util.StoreDataByFile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

public class AssetHistoryValueDao {
    private Logger logger = new LoggerRecorder().getLogger();
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    /**
     * insert history value of asset.
     *
     * @param formArray ArrayList, stored all history value form.
     * @return true: all history value inserted succeed, else false.
     */
    public boolean insertFundHistoryValue(ArrayList<AssetHistoryValueForm> formArray) {
        int allRows = formArray.size();
        StringBuilder insertSql = new StringBuilder();
        //All history value, need to be insert.
        for (AssetHistoryValueForm form : formArray) {
            insertSql.append(form.getAssetCode()).append("\t");
            insertSql.append(form.getValueDate()).append("\t");
            insertSql.append(form.getAssetProperty()).append("\t");
            insertSql.append(form.getNetValue()).append("\t");
            insertSql.append(form.getTotalValue()).append("\t");
            insertSql.append(form.getDayIncreaseRate()).append("\n");
        }

        //sql table columns.
        Vector<String> tableColumn = new Vector<>();
        tableColumn.add("asset_code");
        tableColumn.add("value_date");
        tableColumn.add("asset_property");
        tableColumn.add("net_value");
        tableColumn.add("total_value");
        tableColumn.add("day_increase_rate");

        int succeedInsertRows = new StoreDataByFile().insertMultipleData("investment_data", "asset_history_value", tableColumn, insertSql);
        return (succeedInsertRows == allRows) ? true : false;
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
        AssetHistoryValueForm historyValueForm = null;
        ArrayList<AssetHistoryValueForm> historyValueArray = new ArrayList<>();
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
            logger.info("AssetHistoryValueDao.queryFunHistoryValue failed");
        }

        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            logger.severe("AssetHistoryValueDao.queryFunHistoryValue, sql connection failed");
            e.printStackTrace();
        }

        try {
            while (rs.next()) {
                historyValueForm = new AssetHistoryValueForm(rs.getInt(1), rs.getInt(2),rs.getInt(3),
                        rs.getFloat(4), rs.getFloat(5), rs.getFloat(6));
                historyValueArray.add(historyValueForm);
            }
        } catch (SQLException e) {
            logger.severe("AssetHistoryValueDao.queryFunHistoryValue, ResultSet.next failed");
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.severe("AssetHistoryValueDao.queryFunHistoryValue, close failed");
                e.printStackTrace();
            }
        }
        return historyValueArray;
    }
}
