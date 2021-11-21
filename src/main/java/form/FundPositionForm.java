package form;

public class FundPositionForm {
    private int fundCode;
    private int quarterCount;//Quarter count from 1978-01, which 1978-01 correspond to 1.
    private int assetProperty;//0:Unknown; 1-Stock; 2-Bond.
    private String assetCode;//
    private String assetName;//Stock or Bond;
    private float assetProportion;//Proportion of Stock or Bond.

    //Forbid default constructor, form must be assigned explicitly.
    private FundPositionForm() {}

    public FundPositionForm(int code, int date, int property, String assetCode, String name, float proportion) {
        fundCode = code;
        quarterCount = date;
        assetProperty = property;
        this.assetCode = assetCode;
        assetName = name;
        assetProportion = proportion;
    }

    public int getFundCode() {
        return fundCode;
    }

    public void setFundCode(int fundCode) {
        this.fundCode = fundCode;
    }

    public int getQuarterCount() {
        return quarterCount;
    }

    public void setQuarterCount(int quarterCount) {
        this.quarterCount = quarterCount;
    }

    public int getAssetProperty() {
        return assetProperty;
    }

    public void setAssetProperty(int assetProperty) {
        this.assetProperty = assetProperty;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public float getAssetProportion() {
        return assetProportion;
    }

    public void setAssetProportion(float assetProportion) {
        this.assetProportion = assetProportion;
    }
}
