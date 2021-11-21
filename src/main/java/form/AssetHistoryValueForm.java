package form;

/**
 * Describe history value of asset, such as fund, index or group.
 */
public class AssetHistoryValueForm {
    private int assetCode;
    private int valueDate;//day number from 1978-01-01, which 1978-01-01 correspond to 0.
    private int assetProperty = 0;//Asset property, 0-Unknown;1-Fund;2-Index;3-Group.
    private float netValue;//Net value of each day, not include bonus.
    private float totalValue;//Total value, include bonus.
    private float dayIncreaseRate = 0;

    //The form must be assigned explicitly.
    private AssetHistoryValueForm() {
    }

    public AssetHistoryValueForm(int code, int date, int property, float net, float total, float rate) {
        assetCode = code;
        valueDate = date;
        assetProperty = property;
        netValue = net;
        totalValue = total;
        dayIncreaseRate = rate;
    }

    public int getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(int assetCode) {
        this.assetCode = assetCode;
    }

    public int getValueDate() {
        return valueDate;
    }

    public void setValueDate(int valueDate) {
        this.valueDate = valueDate;
    }

    public int getAssetProperty() {
        return assetProperty;
    }

    public void setAssetProperty(int assetProperty) {
        this.assetProperty = assetProperty;
    }

    public float getNetValue() {
        return netValue;
    }

    public void setNetValue(float netValue) {
        this.netValue = netValue;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
    }

    public float getDayIncreaseRate() {
        return dayIncreaseRate;
    }

    public void setDayIncreaseRate(float dayIncreaseRate) {
        this.dayIncreaseRate = dayIncreaseRate;
    }
}
