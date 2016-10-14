package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.*;

/**
 * Trade Records Entity Manager implementation for In-Memory storage
 * Singleton Bean with default records initialization on Startup by Rundom data
 */
@Singleton
@Startup
public class RecordTradesBean implements RecordTrades{

    /**
     * In-Memory storage tree map for sorted quick access
     * Structure:
     * - top level of map is stored by key of Stock Symbol: for every Stock Symbol there is a map of states by updating timestamp
     * - bottom level of map is leafs of maps for the same Stock Symbol sorted by key timestamp of updating
     *
     * To get any state for specific Stock Symbol need to get SubMap of record states for given Stock Symbol
     * and extract proper state by timestamp
     */
    static TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> records;

    private static final Logger log = LogManager.getLogger(RecordTradesBean.class);

    /**
     * DI of bean for evaluating formula-values
     */
    @EJB
    private StockEvaluation evalStock;

    public RecordTradesBean() {
    }

    /**
     * To reset records storage
     * @param records entities to reset to
     */
    public void setRecords(TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> records) {
        RecordTradesBean.records = records;
    }

    /**
     * Get map of records by updating date-time for the Stock Symbol
     * @param symbol Stock Symbol as unique ID
     * @return map by Updating TimeStamp
     */
    public TreeMap<Date, TradeRecord> getStock(TradeRecord.StockSymbol symbol) {
        return records.get(symbol);
    }

    /**
     * Get all records from market for all timestamps
     * @return map of all trade records in memory
     */
    public TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> getMarket() {
        if(records == null){
            initialLoadRecords();
        }
        return records;
    }

    /**
     * Get the current state of Trade Record by Symbol
     * @param symb Stock Symbol
     * @return the latest by timestamp (current) record state
     */
    public TradeRecord getLastRecord(TradeRecord.StockSymbol symb){
        return records.get(symb).lastEntry().getValue();
    }

    /**
     * Store the record state by input parameters
     * If Trade Record with given Symbol is already exist new current state by timestamp will be added
     * If there is no record for given Symbol the new map of states will be added with given current state parameters
     *
     * Restrict write concurrent access
     * @param symb Stock Symbol as unique Id
     * @param stockType COMMON or PREFERRED type
     * @param sellIndicator SELL or BY indicator
     * @param timeStamp timestamp of update \ creation
     * @param fDiv fixed Dividend value
     * @param parVal parameter value
     * @param prise prise value
     * @param qty quantity value
     */
    @Lock(LockType.WRITE)
    public void uploadRecord(TradeRecord.StockSymbol symb, TradeRecord.StockType stockType,
                             TradeRecord.SellIndicator sellIndicator, Date timeStamp, Integer fDiv, Integer parVal,
                             Integer prise, Integer qty){

        TradeRecord record = new TradeRecord();

        record.setStockSymbol(symb);
        record.setTimeStamp(timeStamp);
        record.setStockType(stockType);
        record.setSellIndicator(sellIndicator);
        record.setFixDividend(fDiv);
        record.setParValue(parVal);
        record.setPrise(prise);
        record.setQty(qty);
        double div = getStock(record.getStockSymbol()) == null ? 1.0 : getLastRecord(record.getStockSymbol()).getDividend();
        record.setDividend(evalStock.evalDividend(stockType, div,
                        fDiv, parVal, prise));
        record.setPeRatio(evalStock.evalPERatio(prise, record.getDividend()));

        TreeMap<Date, TradeRecord> stock = getStock(symb);
        if(stock == null){
            stock = new TreeMap<Date, TradeRecord>();
        }

        stock.put(timeStamp, record);
        records.put(symb, stock);
    }

    /**
     * Remove Stock from Market if exist
     * @param symbol symbol Id for Stock to remove
     * @throws IllegalArgumentException if Stock doesn't exist for given symbol
     */
    public void removeRecordStock(TradeRecord.StockSymbol symbol) throws IllegalArgumentException{
        if(! getMarket().containsKey(symbol))
            throw new IllegalArgumentException("Unable to find Stock in Market for given Symbol: " + symbol);

        getMarket().remove(symbol);
    }

    /**
     * Get list of all trade symbols range with current state
     *
     * Restrict concurrent read access
     * @return list of Trade Records with latest timestamps
     */
    @Lock(LockType.READ)
    public List<TradeRecord> getActualRecords(){
        ArrayList<TradeRecord> list = new ArrayList<TradeRecord>();
        for(TreeMap<Date, TradeRecord> map : getMarket().values()){
            list.add(map.lastEntry().getValue());
        }
        return list;
    }

    /**
     * Get list of all records states for history representation
     * @return list of all records states sorted by symbol and timestamp
     */
    public List<TradeRecord> getHistoryRecords(){
        ArrayList<TradeRecord> list = new ArrayList<TradeRecord>();
        for(TreeMap<Date, TradeRecord> map : getMarket().values()){
            for(TradeRecord rec : map.values()) {
                list.add(rec);
            }
        }
        return list;
    }

    /**
     * Get all possible stock types for input selection representations
     * @return list of types
     */
    public List<String> retrieveStockTypes(){
        ArrayList<String> list = new ArrayList<String>();
        for(TradeRecord.StockType type : TradeRecord.StockType.values()){
            list.add(type.toString());
        }
        return list;
    }

    /**
     * Get all possible stock symbols for input selection representations
     * @return list of symbols
     */
    public List<String> retrieveStockSymbols(){
        ArrayList<String> list = new ArrayList<String>();
        for(TradeRecord.StockSymbol symb : TradeRecord.StockSymbol.values()){
            list.add(symb.toString());
        }
        return list;

    }

    /**
     * Get all possible sell indicators for input selection representations
     * @return list of sell indicators
     */
    public List<String> retrieveSellIndicators(){
        ArrayList<String> list = new ArrayList<String>();
        for(TradeRecord.SellIndicator ind : TradeRecord.SellIndicator.values()){
            list.add(ind.toString());
        }
        return list;

    }

    /**
     * Service method for initial records list Random filling
     * Need to be overridden for existed data storage
     */
    private void initialLoadRecords(){
        Date timeStamp = new Date();
        int i = 0;
        Random rdm = new Random(1L);

        records = new TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>>();

        for (TradeRecord.StockSymbol symb : TradeRecord.StockSymbol.values()) {
            i++;
            TreeMap<Date, TradeRecord> tMap = new TreeMap<Date, TradeRecord>();

            TradeRecord.StockType type = TradeRecord.StockType.values()[i % 2 > 0 ? 0 : 1];
            TradeRecord.SellIndicator ind = TradeRecord.SellIndicator.values()[i % 2 > 0 ? 0 : 1];

            TradeRecord record = new TradeRecord();

            record.setStockSymbol(symb);
            record.setTimeStamp(timeStamp);
            record.setStockType(type);
            record.setSellIndicator(ind);
            record.setDividend((double) (rdm.nextInt(100) + 1));
            record.setFixDividend(rdm.nextInt(100) + 1);
            record.setParValue(rdm.nextInt(100) + 1);
            record.setPrise(rdm.nextInt(100) + 1);
            record.setQty(rdm.nextInt(100) + 1);

            record.setDividend(evalStock.evalDividend(record.getStockType(), record.getDividend(),
                    record.getFixDividend(), record.getParValue(), record.getPrise()));
            record.setPeRatio(evalStock.evalPERatio(record.getPrise(), record.getDividend()));

            tMap.put(record.getTimeStamp(), record);
            records.put(record.getStockSymbol(), tMap);
        }
    }

    /**
     * Market loading to Memory method
     * Need to be everridden into connection configuration for storage implementation
     */
    @PostConstruct
    public void init(){
        initialLoadRecords();
    }

}
