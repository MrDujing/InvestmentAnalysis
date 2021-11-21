package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Read database config from config.properties.
 * Create database connection pool by Hikari.
 * Return Connection by getConnection().
 */
public class HikariCPDataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static Properties prop;

    static {
        prop = new ConfigProperties("config.properties").getProperties();
        config.setJdbcUrl(prop.getProperty("DB_URL"));
        config.setUsername(prop.getProperty("DB_USER"));
        config.setPassword(prop.getProperty("DB_PASSWORD"));
        config.setDriverClassName(prop.getProperty("DB_DRIVE"));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private HikariCPDataSource() {
    }
}
