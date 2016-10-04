package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.*;

/**
 * User: Oleksandr Malynskyi
 * Date: 9/25/2016
 */
@Singleton
@Startup
public class RecordTradesBean implements RecordTrades{

    private static TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> records;

    private static final Logger log = LogManager.getLogger(RecordTradesBean.class);

    @EJB
    private StockEvaluation evalStock;

    public RecordTradesBean() {
    }

    public TreeMap<Date, TradeRecord> getStock(TradeRecord.StockSymbol symbol) {
        return records.get(symbol);
    }

    public TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> getMarket() {
        return records;
    }

    public TradeRecord getLastRecord(TradeRecord.StockSymbol symb){
        return records.get(symb).lastEntry().getValue();
    }

    @Lock(LockType.WRITE)
    public void updateRecord(TradeRecord.StockSymbol symb, TradeRecord.StockType stockType,
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
        record.setDividend(evalStock.evalDividend(stockType, getLastRecord(record.getStockSymbol()).getDividend(),
                        fDiv, parVal, prise));
        record.setPeRatio(evalStock.evalPERatio(prise, record.getDividend()));

        records.get(symb).put(timeStamp, record);
    }

    @Lock(LockType.READ)
    public List<TradeRecord> getActualRecords(){
        ArrayList<TradeRecord> list = new ArrayList<TradeRecord>();
        for(TreeMap<Date, TradeRecord> map : getRecords().values()){
            list.add(map.lastEntry().getValue());
        }
        return list;
    }

    public List<TradeRecord> getHistoryRecords(){
        ArrayList<TradeRecord> list = new ArrayList<TradeRecord>();
        for(TreeMap<Date, TradeRecord> map : getRecords().values()){
            for(TradeRecord rec : map.values()) {
                list.add(rec);
            }
        }
        return list;
    }

    public List<String> retrieveStockTypes(){
        ArrayList<String> list = new ArrayList<String>();
        for(TradeRecord.StockType type : TradeRecord.StockType.values()){
            list.add(type.toString());
        }
        return list;
    }

    public List<String> retrieveStockSymbols(){
        ArrayList<String> list = new ArrayList<String>();
        for(TradeRecord.StockSymbol symb : TradeRecord.StockSymbol.values()){
            list.add(symb.toString());
        }
        return list;

    }

    public List<String> retrieveSellIndicators(){
        ArrayList<String> list = new ArrayList<String>();
        for(TradeRecord.SellIndicator ind : TradeRecord.SellIndicator.values()){
            list.add(ind.toString());
        }
        return list;

    }


    public TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>> getRecords(){
        if(records == null){
            initialLoadRecords();
        }
        return records;
    }

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

    @PostConstruct
    public void init(){
        try {
            initialLoadRecords();
        }catch(Throwable e){
                log.error("ERROR in RecordTradesBean.init:", e.getMessage(), e);
        }

    }

}
