package util;

/**
 * Define pre-URL for crawl data.
 */
public class ConstantParameter {
    //VALUE_CRAWL_URL_PREFIX: crawl history value, while add fund code.
    public static final String VALUE_CRAWL_URL_PREFIX = "https://fundf10.eastmoney.com/F10DataApi.aspx?type=lsjz&per=49";
    //POSITION_CRAWL_URL_PREFIX: crawl fund position, while add fund code.
    public static final String POSITION_CRAWL_URL_PREFIX = "https://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jjcc&topline=30";
    //Day_Increase_Rate is invalid while it is 999.
    public static final int RATE_INVALID = 999;
    //Quarter invalid is 999.
    public static final int QUARTER_INVALID = 999;
    //Year invalid is 999.
    public static final int YEAR_INVALID = 999;
}
