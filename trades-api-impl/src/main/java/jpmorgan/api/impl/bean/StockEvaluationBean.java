package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.ejb.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Formulas calculations Bean
 * */
@Stateless
public class StockEvaluationBean implements StockEvaluation {

    private static final Logger log = LogManager.getLogger(StockEvaluationBean.class);

    /**
     * DI of RecordTrades Bean for all Market calculations
     */
    @EJB
    private RecordTrades tradeRecords;

    public StockEvaluationBean() {
    }

    /**
     * Get injected trade bean
     * @return RecordTrades bean
     */
    public RecordTrades getTradeRecords() {
        return tradeRecords;
    }

    /**
     * Dividend Yield calculation for COMMON stock type
     *
     * @param lastDividend currently existed dividend for stock
     * @param price new price
     * @return Last Dividend / Price
     * @throws IllegalArgumentException for Null or Zero price
     */
    public Double evalCommonDividend(Double lastDividend, @NotNull Integer price) throws IllegalArgumentException{
        if(price == null || price == 0)
            throw new IllegalArgumentException("Price can't be 0 or null");

        return lastDividend / price;
    }

    /**
     * Dividend Yield calculation for PREFERRED stock type
     *
     * @param fixDividend fixed dividend
     * @param parValue parameter value
     * @param price price
     * @return (Fixed Dividend . Par Value) / Price
     * @throws IllegalArgumentException
     */
    public Double evalPreferredDividend(Integer fixDividend, Integer parValue, @NotNull Integer price) throws IllegalArgumentException{
        if(price == null || price == 0)
            throw new IllegalArgumentException("Price can't be 0 or null");

        return (double) (fixDividend*parValue)/price;
    }

    /**
     * Calculate Dividend for a given Stock Type
     *
     * @see   StockEvaluationBean#evalCommonDividend(java.lang.Double, java.lang.Integer)
     * @see   StockEvaluationBean#evalPreferredDividend(java.lang.Integer, java.lang.Integer, java.lang.Integer)
     * @param type stock type
     * @param div currently existed dividend for stock
     * @param fixDividend  fixed dividend for stock
     * @param parValue given parameter value
     * @param price given price
     * @return evalCommonDividend or evalPreferredDividend for appropriate type
     * @throws IllegalArgumentException if type is Null
     */
    public Double evalDividend(@NotNull TradeRecord.StockType type, @Nullable Double div, @Nullable Integer fixDividend,
                                @Nullable Integer parValue, Integer price) throws IllegalArgumentException{
        if(type == null)
            throw new IllegalArgumentException("Stock Type can't be null for evaluate Dividend");

        return type.equals(TradeRecord.StockType.COMMON) ? evalCommonDividend(div, price) :
                                    evalPreferredDividend(fixDividend, parValue, price);
    }

    /**
     * Evaluate PE Ratio
     * @param price given price
     * @param dividend given dividend
     * @return Price / Dividend
     * @throws IllegalArgumentException forNull Dividend
     */
    public Double evalPERatio(Integer price, @NotNull Double dividend) throws IllegalArgumentException{
        if(dividend == null || dividend == 0)
            throw new IllegalArgumentException("Dividend can't be 0 or null");
        return price/dividend;
    }

    /**
     * Calculate Geometric Mean value
     * @param tFrime for a given time frame in mins
     * @return n-radical of N Volume Weighted Stock Prices multiplication
     * @throws IllegalArgumentException
     */
    public Double evalGeometricMean(@NotNull Double tFrime) throws IllegalArgumentException{
        if(tFrime == null || tFrime == 0)
            throw new IllegalArgumentException("Time frame can't be 0 or null");

        TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> market = tradeRecords.getMarket();
        Double multiVWSPrice = 1.0;
        boolean isOK = false;
        for(TradeRecord.StockSymbol symbol : market.keySet()){
            Double vwsp = evalVolWeightStockPrice(symbol.toString(), tFrime);
            if(vwsp != null && vwsp > 0) {
                multiVWSPrice *= vwsp;
                isOK = true;
            }
        }
        return isOK ? Math.pow(multiVWSPrice, 1.0/market.keySet().size()) : null;
    }

    /**
     * Calculation of Volume Weighted Stock Price
     * @param symbol for a given symbol
     * @param tFrime for a given time frame
     * @return Sum(Price.Qty)/Sum(Qty) for Stock
     * @throws IllegalArgumentException if inputs Null
     */
    public Double evalVolWeightStockPrice(@NotNull String symbol, @NotNull Double tFrime) throws IllegalArgumentException{
        if(tFrime == null || tFrime == 0)
            throw new IllegalArgumentException("Time frame can't be 0 or null");

        if(symbol == null || symbol.equals(""))
            throw new IllegalArgumentException("Time frame can't be empty");

        TreeMap<Date, TradeRecord> stock;
        try {
            stock = tradeRecords.getStock(TradeRecord.StockSymbol.valueOf(symbol));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Undefined Stock symbol: " + symbol);
        }
        Date date = new Date((long) (System.currentTimeMillis() - tFrime * 60 * 1000));
        NavigableMap<Date, TradeRecord> tail = stock.tailMap(date, true);
        Double multiSum = 0.0;
        Double qtySum = 0.0;
        for(TradeRecord record : tail.values()){
            multiSum += (record.getPrise() * record.getQty());
            qtySum += record.getQty();
        }
        return qtySum > 0 ? multiSum/qtySum : null;
    }
}
