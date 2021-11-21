package util;

public class FundCodeTransfer {
    public static String intToString(int fundCode) {
        if (fundCode < 100000)
            return Integer.toString(fundCode + 1000000).substring(1);
        else
            return Integer.toString(fundCode);
    }

    public static int stringToInt(String fundCode) {
        return Integer.parseInt(fundCode);
    }
}
