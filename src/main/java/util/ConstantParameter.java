package util;

/**
 * Define pre-URL for crawl data.
 */
public class ConstantParameter {
    //VALUE_CRAWL_URL_PREFIX: crawl history value, while add fund code.
    public static final String VALUE_CRAWL_URL_PREFIX = "https://fundf10.eastmoney.com/F10DataApi.aspx?type=lsjz&per=49";
    //STOCK_POSITION_CRAWL_URL_PREFIX: crawl stock position, while add fund code.
    public static final String STOCK_POSITION_CRAWL_URL_PREFIX = "https://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jjcc&topline=30";
    //BOND_POSITION_CRAWL_URL_PREFIX: crawl bond position, while add fund code.
    public static final String BOND_POSITION_CRAWL_URL_PREFIX = "https://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=zqcc&topline=10";
    //Day_Increase_Rate is invalid while it is 999.
    public static final int RATE_INVALID = 999;
    //Quarter invalid is 999.
    public static final int QUARTER_BASE = 0;
    //Year invalid is 999.
    public static final int YEAR_INVALID = 999;
    //Asset property
    public static final int STOCK = 1;
    public static final int BOND = 2;
}
