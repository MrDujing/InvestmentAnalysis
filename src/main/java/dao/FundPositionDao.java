package dao;

import form.FundPositionForm;
import util.DateTransForm;
import util.HikariCPDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class FundPositionDao {
    private Logger logger = LoggerFactory.getLogger(FundPositionDao.class);
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private static final String database = HikariCPDataSource.getDatabaseName();

    /**
     * Insert fund position to database.
     *
     * @param positionArray stored all position of fund.
     * @return insert rows, -1 : insert failed; else insert right. With statement IGNORE INTO, insert row may less than array size;
     */
    public int insertFundPosition(ArrayList<FundPositionForm> positionArray) {
        String insertSql = "INSERT IGNORE INTO " + database + ".fund_position (fund_code, quarter, asset_property, asset_code,asset_name, asset_proportion) VALUES ";
        for (int i = 0; i < positionArray.size(); i++) {
            insertSql += "(" + positionArray.get(i).getFundCode() + ","
                    + positionArray.get(i).getQuarter() + ","
                    + positionArray.get(i).getAssetProperty() + ","
                    + "\"" + positionArray.get(i).getAssetCode() + "\"" + ","
                    + "\"" + positionArray.get(i).getAssetName() + "\"" + ","
                    + positionArray.get(i).getAssetProportion() + ")";
            if (i == positionArray.size() - 1)
                insertSql += ";";
            else
                insertSql += ",";
        }

        int insertCount = -1;
        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            insertCount = stmt.executeUpdate(insertSql);
        } catch (SQLException e) {
            logger.error("FundPositionDao.insertFundPosition, sql connection failed");
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.error("FundPositionDao.insertFundPosition, close failed");
                e.printStackTrace();
            }
        }

        return insertCount;
    }

    /**
     * Query fund position, in current quarter.
     *
     * @return ArrayList stored fund position.
     */
    public ArrayList queryFundPosition(final int fundCode, final int quarter) {
        FundPositionForm positionForm = null;
        ArrayList<FundPositionForm> positionArray = new ArrayList<>();
        String querySql = "SELECT * FROM " + database + ".fund_position p WHERE p.fund_code = " + fundCode + " AND p.quarter = " + quarter;

        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(querySql);
        } catch (SQLException e) {
            logger.error("FundPositionDao.queryFundPosition, sql connection failed");
            e.printStackTrace();
        }

        try {
            while (rs.next()) {
                positionForm = new FundPositionForm(rs.getInt("fund_code"), rs.getInt("quarter"), rs.getInt("asset_property"),
                        rs.getString("asset_code"), rs.getString("asset_name"), rs.getFloat("asset_proportion"));
                positionArray.add(positionForm);
            }
        } catch (SQLException e) {
            logger.error("FundPositionDao.queryFundPosition, ResultSet.next failed");
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.error("FundPositionDao.queryFundPosition, close failed");
                e.printStackTrace();
            }
        }
        return positionArray;
    }
}
