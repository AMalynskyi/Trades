package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * User: Oleksandr Malynskyi
 * Date: 9/28/2016
 */
@Local
public interface StockEvaluation {

    public Double evalCommonDividend(Double lastDividend, @NotNull Integer price);

    public Double evalPreferredDividend(Integer fixDividend, Integer parValue, @NotNull Integer price);

    public Double evalDividend(@NotNull TradeRecord.StockType type, Double div, Integer fixDividend,
                                Integer parValue, @NotNull Integer price);

    public Double evalPERatio(Integer price, Double dividend);

    public Double evalGeometricMean(Integer tFrime);

    public Double evalVolWeightStockPrice(String symbol, Integer tFrime);
}
