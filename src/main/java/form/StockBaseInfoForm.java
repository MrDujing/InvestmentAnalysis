package form;

public class StockBaseInfoForm {
    private String stockCode;
    private String companyName;
    private String companyIndustry;
    private String stockType;

    private StockBaseInfoForm() {}

    public StockBaseInfoForm(String code, String name, String industry, String type) {
        stockCode = code;
        companyName = name;
        companyIndustry = industry;
        stockType = type;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(String companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }
}
