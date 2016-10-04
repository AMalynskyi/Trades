package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Remote;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * User: Oleksandr Malynskyi
 * Date: 9/27/2016
 */
@Local
public interface RecordTrades {

    public void updateRecord(TradeRecord.StockSymbol symb, TradeRecord.StockType stockType,
                                 TradeRecord.SellIndicator sellIndicator, Date timeStamp, Integer fDiv, Integer parVal,
                                 Integer prise, Integer qty);

    public List<TradeRecord> getActualRecords();

    public List<String> retrieveStockTypes();

    public List<String> retrieveStockSymbols();

    public List<String> retrieveSellIndicators();

    public TreeMap<Date, TradeRecord> getStock(TradeRecord.StockSymbol symbol);

    public TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> getMarket();
}
