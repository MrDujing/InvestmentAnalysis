package util;

/**
 * Transfer fund code between int and String. Pay attention to fund code which prefixed with 0.
 */
public class FundCodeTransfer {
    public static String transferToStr(int code) {
        if (code < 100000)
            return Integer.toString(code + 1000000).substring(1);
        else
            return Integer.toString(code);
    }

    public static int transferToInt(String code) {
        return Integer.parseInt(code);
    }
}
