package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Remote;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Interface for Trade Records entities managing
 *
 */
@Local
public interface RecordTrades {

    public void uploadRecord(TradeRecord.StockSymbol symb, TradeRecord.StockType stockType,
                             TradeRecord.SellIndicator sellIndicator, Date timeStamp, Integer fDiv, Integer parVal,
                             Integer prise, Integer qty);

    void setRecords(TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> records);

    public TradeRecord getLastRecord(TradeRecord.StockSymbol symb);

    public List<TradeRecord> getHistoryRecords();

    public List<TradeRecord> getActualRecords();

    public List<String> retrieveStockTypes();

    public List<String> retrieveStockSymbols();

    public List<String> retrieveSellIndicators();

    public TreeMap<Date, TradeRecord> getStock(TradeRecord.StockSymbol symbol);

    public TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> getMarket();

    public void removeRecordStock(TradeRecord.StockSymbol symbol) throws IllegalArgumentException;
}
