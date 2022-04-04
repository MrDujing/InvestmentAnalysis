package form;

public class FundBaseForm {
    private int fundCode;
    private String namePinyinAbbr;//Pinyin of fund name ,which is abbreviation.
    private String fundName;//fund name in chinese.
    private int fundProperty;//fund property, which reference to table reference_index.
    private String namePinyinFull;//Pinyin of fund name ,which is full name.

    public FundBaseForm(int code, String abbrName, String name, int property, String fullName) {
        fundCode = code;
        namePinyinAbbr = abbrName;
        fundName = name;
        fundProperty = property;
        namePinyinFull = fullName;
    }

    private FundBaseForm() {
    }

    public int getFundCode() {
        return fundCode;
    }

    public void setFundCode(int fundCode) {
        this.fundCode = fundCode;
    }

    public String getNamePinyinAbbr() {
        return namePinyinAbbr;
    }

    public void setNamePinyinAbbr(String namePinyinAbbr) {
        this.namePinyinAbbr = namePinyinAbbr;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public int getFundProperty() {
        return fundProperty;
    }

    public void setFundProperty(int fundProperty) {
        this.fundProperty = fundProperty;
    }

    public String getNamePinyinFull() {
        return namePinyinFull;
    }

    public void setNamePinyinFull(String namePinyinFull) {
        this.namePinyinFull = namePinyinFull;
    }
}
