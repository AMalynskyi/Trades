package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * User: Oleksandr Malynskyi
 * Date: 9/25/2016
 */
@Stateless
public class StockEvaluationBean implements StockEvaluation {

    private static final Logger log = LogManager.getLogger(StockEvaluationBean.class);

    @EJB
    private RecordTrades tradeRecords;

    public StockEvaluationBean() {
    }

    public Double evalCommonDividend(Double lastDividend, @NotNull Integer price){
        return lastDividend / price;
    }

    public Double evalPreferredDividend(Integer fixDividend, Integer parValue, @NotNull Integer price){
        return (double) (fixDividend*parValue)/price;
    }

    public Double evalDividend(@NotNull TradeRecord.StockType type, Double div, Integer fixDividend,
                                Integer parValue, @NotNull Integer price) {
        return type.equals(TradeRecord.StockType.COMMON) ? evalCommonDividend(div, price) :
                                    evalPreferredDividend(fixDividend, parValue, price);
    }

    public Double evalPERatio(Integer price, Double dividend){
        return price/dividend;
    }

    public Double evalGeometricMean(Integer tFrime){
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

    public Double evalVolWeightStockPrice(String symbol, Integer tFrime){
        TreeMap<Date, TradeRecord> stock = tradeRecords.getStock(TradeRecord.StockSymbol.valueOf(symbol));
        Date date = new Date(System.currentTimeMillis()-tFrime*60*1000);
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
