package dao;

import form.FundPositionForm;
import util.DateTransForm;
import util.HikariCPDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

public class FundPositionDao {
    private Logger logger = new LoggerRecorder().getLogger();
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    /**
     * Insert fund position to database.
     *
     * @param positionArray stored all position of fund.
     * @return true while insert successfully, otherwise false.
     */
    public boolean insertFundPosition(ArrayList<FundPositionForm> positionArray) {
        String insertSql = "INSERT IGNORE INTO investment_data.fund_position (fund_code, quarter_count, asset_property, asset_code,asset_name, asset_proportion) VALUES ";
        for (int i = 0; i < positionArray.size(); i++) {
            insertSql += "(" + positionArray.get(i).getFundCode() + ","
                    + positionArray.get(i).getQuarterCount() + ","
                    + positionArray.get(i).getAssetProperty() + ","
                    + "\"" + positionArray.get(i).getAssetCode() + "\"" + ","
                    + "\"" + positionArray.get(i).getAssetName() + "\"" + ","
                    + positionArray.get(i).getAssetProportion() + ")";
            if (i == positionArray.size() - 1)
                insertSql += ";";
            else
                insertSql += ",";
        }

        int insertCount = 0;
        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            insertCount = stmt.executeUpdate(insertSql);
        } catch (SQLException e) {
            logger.severe("FundPositionDao.insertFundPosition, sql connection failed");
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.severe("FundPositionDao.insertFundPosition, close failed");
                e.printStackTrace();
            }
        }

        return insertCount == positionArray.size();
    }

    /**
     * Query fund position, in current quarter.
     *
     * @return ArrtyList stored fund position.
     */
    public ArrayList queryFundPosition(final int fundCode) {
        FundPositionForm positionForm = null;
        ArrayList<FundPositionForm> positionArray = new ArrayList<>();
        int currentQuarterCount = new DateTransForm().getQuarterCount();
        String querySql = "SELECT * FROM fund_position p WHERE p.fund_code = " + fundCode + " AND p.quarter_count = " + currentQuarterCount;

        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(querySql);
        } catch (SQLException e) {
            logger.severe("FundPositionDao.queryFundPosition, sql connection failed");
            e.printStackTrace();
        }

        try {
            while (rs.next()) {
                positionForm = new FundPositionForm(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5), rs.getFloat(6));
                positionArray.add(positionForm);
            }
        } catch (SQLException e) {
            logger.severe("FundPositionDao.queryFundPosition, ResultSet.next failed");
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.severe("FundPositionDao.queryFundPosition, close failed");
                e.printStackTrace();
            }
        }
        return positionArray;
    }
}
