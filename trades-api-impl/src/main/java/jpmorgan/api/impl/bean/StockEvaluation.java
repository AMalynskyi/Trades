package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;

/**
 * Interface for formula calculations Bean
 * */
@Local
public interface StockEvaluation {

    public RecordTrades getTradeRecords();

    public Double evalCommonDividend(Double lastDividend, @NotNull Integer price) throws IllegalArgumentException;

    public Double evalPreferredDividend(Integer fixDividend, Integer parValue, @NotNull Integer price) throws IllegalArgumentException;

    public Double evalDividend(@NotNull TradeRecord.StockType type, Double div, Integer fixDividend,
                                Integer parValue, @NotNull Integer price) throws IllegalArgumentException;

    public Double evalPERatio(Integer price, Double dividend) throws IllegalArgumentException;

    public Double evalGeometricMean(Double tFrime) throws IllegalArgumentException;

    public Double evalVolWeightStockPrice(String symbol, Double tFrime) throws IllegalArgumentException;
}
