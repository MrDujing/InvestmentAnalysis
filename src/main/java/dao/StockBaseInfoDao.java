package dao;

import form.StockBaseInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HikariCPDataSource;

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
     *
     * @return true: insert succeed, else false.
     */
    public boolean insertStockBaseInfo(StockBaseInfoForm infoForm) {
        String code = infoForm.getStockCode();
        //Query if exist in database or not.
        String querySql = String.format("SELECT * FROM %s.stock_base_info t WHERE t.stock_code = %s", database, code);
        try {
            conn = HikariCPDataSource.getConnection();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(querySql);

            if (rs.next()) {
                String name = rs.getString("company_name");
                String industry = rs.getString("company_industry");
                String type = rs.getString("stock_type");
                if (name == infoForm.getCompanyName() && industry == infoForm.getCompanyIndustry() && type == infoForm.getStockType()) {
                    logger.info("Base info of {} is already exist in database", code);
                    return true;
                }
            }

            //if not exist ,or need to be update.
            String insertSql = String.format("REPLACE INTO %s.stock_base_info (stock_code, company_name, company_industry, stock_type) " +
                    "VALUES (\"%s\", \"%s\", \"%s\", \"%s\")", database, code, infoForm.getCompanyName(), infoForm.getCompanyIndustry(), infoForm.getStockType());

            int insertRows = stmt.executeUpdate(insertSql);
            if (insertRows == 0) {
                logger.error("Failed to insert base info of {} into database. ", code);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Insert base info of {} into database failed", code);
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                stmt.close();
                rs.close();
            } catch (SQLException e) {
                logger.error("Close database resources failed, code is {} ", code);
                e.printStackTrace();
            }
        }
        return true;
    }
}
