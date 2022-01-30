package util;


public class FundCodeTransfer {
    private int codeInt;
    private String codeStr;

    private FundCodeTransfer() { }

    public FundCodeTransfer(int code) {
        codeInt = code;
        if (code < 100000)
            codeStr = Integer.toString(code + 1000000).substring(1);
        else
            codeStr = Integer.toString(code);
    }

    public FundCodeTransfer(String code) {
        codeStr = code;
        codeInt = Integer.parseInt(code);
    }

    public int getCodeInt() {
        return codeInt;
    }

    public String getCodeStr() {
        return codeStr;
    }
}
