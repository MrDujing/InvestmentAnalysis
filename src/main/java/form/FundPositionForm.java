package form;

public class FundPositionForm {
    private int fundCode;
    private int quarter;//quarter count from 2000-01-01, 2000/1/1-2000/3/31 correspond to quarter 0.
    private byte assetProperty;//0:Unknown; 1-Stock; 2-Bond.
    private String assetCode;//
    private String assetName;//Stock or Bond;
    private float assetProportion;//Proportion of Stock or Bond.

    //Forbid default constructor, form must be assigned explicitly.
    private FundPositionForm() {}

    public FundPositionForm(int code, int quarter, byte property, String assetCode, String name, float proportion) {
        fundCode = code;
        this.quarter = quarter;
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

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public int getAssetProperty() {
        return assetProperty;
    }

    public void setAssetProperty(byte assetProperty) {
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
