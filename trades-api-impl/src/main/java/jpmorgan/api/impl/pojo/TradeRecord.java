package jpmorgan.api.impl.pojo;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * User: Oleksandr Malynskyi
 * Date: 9/25/2016
 */
public class TradeRecord implements Serializable{

    /**
     * All number values in pennies
     */

    private StockSymbol stockSymbol;

    private StockType stockType;

    private Double dividend;

    private Integer fixDividend;

    private Integer parValue;

    private Integer prise;

    private Double peRatio;

    private Date timeStamp;

    private Integer qty;

    private SellIndicator sellIndicator;

    public TradeRecord() {
    }

    public TradeRecord(StockSymbol stockSymbol, StockType stockType, Double dividend, Integer fixDividend, Integer parValue,
                       Integer prise, Double peRatio, Date timeStamp, Integer qty, SellIndicator sellIndicator) {
        this.stockSymbol = stockSymbol;
        this.stockType = stockType;
        this.dividend = dividend;
        this.fixDividend = fixDividend;
        this.parValue = parValue;
        this.prise = prise;
        this.peRatio = peRatio;
        this.timeStamp = timeStamp;
        this.qty = qty;
        this.sellIndicator = sellIndicator;
    }

    @Override
    public String toString() {
        String str = "";
        try {
            for(Method method : this.getClass().getMethods())
                str += method.getName().toLowerCase().contains("get") ? method.getName() + " : " + method.invoke(this) + " " : "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return str;
    }

    public Integer getParValue() {
        return parValue;
    }

    public void setParValue(Integer parValue) {
        this.parValue = parValue;
    }

    public StockSymbol getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(StockSymbol stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public StockType getStockType() {
        return stockType;
    }

    public void setStockType(StockType stockType) {
        this.stockType = stockType;
    }

    public Double getDividend() {
        return dividend;
    }

    public String getSDividend(){
        return String.format("%.2f", dividend);
    }

    public void setDividend(Double dividend) {
        this.dividend = dividend;
    }

    public Integer getFixDividend() {
        return fixDividend;
    }

    public void setFixDividend(Integer fixDividend) {
        this.fixDividend = fixDividend;
    }

    public Integer getPrise() {
        return prise;
    }

    public void setPrise(Integer prise) {
        this.prise = prise;
    }

    public Double getPeRatio() {
        return peRatio;
    }

    public String getSPeRatio(){
        return String.format("%.2f", peRatio);
    }

    public void setPeRatio(Double peRatio) {
        this.peRatio = peRatio;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public SellIndicator getSellIndicator() {
        return sellIndicator;
    }

    public void setSellIndicator(SellIndicator sellIndicator) {
        this.sellIndicator = sellIndicator;
    }

    public enum SellIndicator{
        BUY,
        SELL
    }

    public enum StockType {
        COMMON,
        PREFERRED
    }

    public enum StockSymbol {
        TEA,
        POP,
        ALE,
        GIN,
        JOE
    }
}
