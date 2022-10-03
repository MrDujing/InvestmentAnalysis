package form;

public class IndexValueForm {
    private String indexCode;
    private int date;
    private float indexDayValue;
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

    public float getIndexDayValue() {
        return indexDayValue;
    }

    public void setIndexDayValue(float indexDayValue) {
        this.indexDayValue = indexDayValue;
    }

    public float getDayIncreaseRate() {
        return dayIncreaseRate;
    }

    public void setDayIncreaseRate(float dayIncreaseRate) {
        this.dayIncreaseRate = dayIncreaseRate;
    }
}
