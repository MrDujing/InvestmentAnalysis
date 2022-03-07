package form;

public class FundValueForm {
    private int fundCode;
    private int date;//day number from 2000-01-01, which 2000-01-01 correspond to 0.
    private float netValue;//Net value of each day, not include bonus.
    private float totalValue;//Total value, include bonus.
    private float dayIncreaseRate = Float.MIN_EXPONENT;//Default value if don't have value in fund day increase.

    //The form must be assigned explicitly.
    private FundValueForm() {
    }

    public FundValueForm(int code, int date, float net, float total, float rate) {
        fundCode = code;
        this.date = date;
        netValue = net;
        totalValue = total;
        dayIncreaseRate = rate;
    }

    public int getFundCode() {
        return fundCode;
    }

    public void setFundCode(int fundCode) {
        this.fundCode = fundCode;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
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
