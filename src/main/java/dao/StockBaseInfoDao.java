package dao;

import form.StockBaseInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HikariCPDataSource;
import util.StockType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StockBaseInfoDao {
    private Logger logger = LoggerFactory.getLogger(StockBaseInfoDao.class);
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private static final String database = HikariCPDataSource.getDatabaseName();

    /**
     * Get base info of stock.
     */
    public StockBaseInfoForm queryBaseInfo(String code) {
        StockBaseInfoForm baseInfoForm = null;
        String querySql = String.format("SELECT * FROM %s.stock_base_info t WHERE t.stock_code = %s", database, code);

        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(querySql);

            //Parse result
            if (rs.next()) {
                String companyName = rs.getString("company_name");
                String companyIndustry = rs.getString("company_industry");
                String stockType = rs.getString("stock_type");
                baseInfoForm = new StockBaseInfoForm(code, companyName, companyIndustry, stockType);
            } else
                baseInfoForm = null;
        } catch (SQLException e) {
            logger.error("ERROR: query information of {}, which sql is {}", code, querySql);
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                stmt.close();
                rs.close();
            } catch (SQLException e) {
                logger.error("ERROR: close database resources failed");
            }
        }
        return baseInfoForm;
    }

    /**
     * Insert base info to database.
     * @return true: insert succeed, else false.
     */
    public boolean insertStockBaseInfo(String code, StockType type) {
        //TODO.20220502
    }


}
