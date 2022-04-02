package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Read database config from config.properties.
 * Create database connection pool by Hikari.
 * Return Connection by getConnection().
 */
public class HikariCPDataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static Properties prop;
    private static final Logger logger = LoggerFactory.getLogger(HikariCPDataSource.class);

    static {
        prop = new PropertiesConfig("../config.properties",true).getProperties();
        config.setJdbcUrl(prop.getProperty("DB_URL"));
        config.setUsername(prop.getProperty("DB_USER"));
        config.setPassword(prop.getProperty("DB_PASSWORD"));
        config.setDriverClassName(prop.getProperty("DB_DRIVE"));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
        if (null == ds)
            logger.error("Create {} failed", ds.toString());
    }

    public static Connection getConnection() {
        Connection con = null;
        try {
            con = ds.getConnection();
        } catch (SQLException e) {
            logger.error("{} get connection failed", ds.toString());
            e.printStackTrace();
        }
        return con;
    }

    public static String getDatabaseName() {
        return prop.getProperty("DB_NAME");
    }

    private HikariCPDataSource() {
    }
}
