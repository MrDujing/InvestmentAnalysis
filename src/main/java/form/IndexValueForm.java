package form;

public class IndexValueForm {
    private String indexCode;
    private int date;
    private float openPrice;
    private float closePrice;
    private float highPrice;
    private float lowPrice;
    private float tradeVolume;
    private float dayIncreaseRate;

    public String getIndexCode() {
        return indexCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getDayIncreaseRate() {
        return dayIncreaseRate;
    }

    public void setDayIncreaseRate(float dayIncreaseRate) {
        this.dayIncreaseRate = dayIncreaseRate;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public float getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(float highPrice) {
        this.highPrice = highPrice;
    }

    public float getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(float lowPrice) {
        this.lowPrice = lowPrice;
    }

    public float getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(float tradeVolume) {
        this.tradeVolume = tradeVolume;
    }
}
