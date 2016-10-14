package jpmorgan.api.impl.bean;

import jpmorgan.api.impl.pojo.TradeRecord;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.ejb.embeddable.EJBContainer;
import java.util.Date;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;

@RunWith( JUnit4.class )
public class RecordTradesBeanTest{

    private static EJBContainer ejbContainer;
    static RecordTrades rtBean;

    static TradeRecord aleCurrent;
    static TradeRecord ale3MinBefore;

    static TradeRecord ginCurrent;
    static TradeRecord gin7MinBefore;

    @BeforeClass
    public static void init() throws Exception {
        ejbContainer = EJBContainer.createEJBContainer();

        rtBean = (RecordTrades) ejbContainer.getContext().lookup("java:global/trades-api-impl/RecordTradesBean");

        rtBean.setRecords(new TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>>());

        TreeMap<Date, TradeRecord> tMap = new TreeMap<Date, TradeRecord>();

        Date timeStamp = new Date();

        aleCurrent = new TradeRecord();
        aleCurrent.setStockSymbol(TradeRecord.StockSymbol.ALE);
        aleCurrent.setTimeStamp(timeStamp);
        aleCurrent.setStockType(TradeRecord.StockType.COMMON);
        aleCurrent.setSellIndicator(TradeRecord.SellIndicator.SELL);
        aleCurrent.setDividend(6.0);
        aleCurrent.setFixDividend(4);
        aleCurrent.setParValue(23);
        aleCurrent.setPrise(111);
        aleCurrent.setQty(34);
        aleCurrent.setPeRatio(aleCurrent.getPrise()/aleCurrent.getDividend());

        tMap.put(aleCurrent.getTimeStamp(), aleCurrent);

        ale3MinBefore = new TradeRecord();
        ale3MinBefore.setStockSymbol(TradeRecord.StockSymbol.ALE);
        ale3MinBefore.setTimeStamp(new Date(timeStamp.getTime()-3*60*1000));
        ale3MinBefore.setStockType(TradeRecord.StockType.PREFERRED);
        ale3MinBefore.setSellIndicator(TradeRecord.SellIndicator.BUY);
        ale3MinBefore.setDividend(3.0);
        ale3MinBefore.setFixDividend(8);
        ale3MinBefore.setParValue(10);
        ale3MinBefore.setPrise(50);
        ale3MinBefore.setQty(10);
        ale3MinBefore.setPeRatio(ale3MinBefore.getPrise()/ale3MinBefore.getDividend());

        tMap.put(ale3MinBefore.getTimeStamp(), ale3MinBefore);

        rtBean.getMarket().put(TradeRecord.StockSymbol.ALE, tMap);

        tMap = new TreeMap<Date, TradeRecord>();

        ginCurrent = new TradeRecord();
        ginCurrent.setStockSymbol(TradeRecord.StockSymbol.GIN);
        ginCurrent.setTimeStamp(timeStamp);
        ginCurrent.setStockType(TradeRecord.StockType.COMMON);
        ginCurrent.setSellIndicator(TradeRecord.SellIndicator.SELL);
        ginCurrent.setDividend(6.0);
        ginCurrent.setFixDividend(4);
        ginCurrent.setParValue(23);
        ginCurrent.setPrise(111);
        ginCurrent.setQty(34);
        ginCurrent.setPeRatio(ginCurrent.getPrise()/ginCurrent.getDividend());

        tMap.put(ginCurrent.getTimeStamp(), ginCurrent);

        gin7MinBefore = new TradeRecord();
        gin7MinBefore.setStockSymbol(TradeRecord.StockSymbol.GIN);
        gin7MinBefore.setTimeStamp(new Date(timeStamp.getTime()-7*60*1000));
        gin7MinBefore.setStockType(TradeRecord.StockType.COMMON);
        gin7MinBefore.setSellIndicator(TradeRecord.SellIndicator.SELL);
        gin7MinBefore.setDividend(6.0);
        gin7MinBefore.setFixDividend(4);
        gin7MinBefore.setParValue(23);
        gin7MinBefore.setPrise(111);
        gin7MinBefore.setQty(34);
        gin7MinBefore.setPeRatio(gin7MinBefore.getPrise()/gin7MinBefore.getDividend());

        tMap.put(gin7MinBefore.getTimeStamp(), gin7MinBefore);

        rtBean.getMarket().put(TradeRecord.StockSymbol.GIN, tMap);
    }

    @AfterClass
    public static void clear() throws Exception {
        rtBean.setRecords(new TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>>());
        aleCurrent = null;
        ale3MinBefore = null;
        ginCurrent = null;
        gin7MinBefore = null;
        ejbContainer.close();
    }

    @Test
    public void testGetStock() throws Exception {
        assertThat(rtBean.getStock(TradeRecord.StockSymbol.GIN).size()).isEqualTo(2);
        assertThat(rtBean.getStock(TradeRecord.StockSymbol.ALE).lastKey()).isEqualTo(aleCurrent.getTimeStamp());
        assertThat(rtBean.getStock(TradeRecord.StockSymbol.JOE)).isNull();
        assertThat(rtBean.getStock(TradeRecord.StockSymbol.GIN).firstKey()).isBefore(ale3MinBefore.getTimeStamp());
    }

    @Test
    public void testGetMarket() throws Exception {
        assertThat(rtBean.getMarket().size()).isEqualTo(2);
    }

    @Test
    public void testGetLastRecord() throws Exception {
        assertThat(rtBean.getLastRecord(TradeRecord.StockSymbol.ALE).getPeRatio()).isEqualTo(aleCurrent.getPeRatio());
    }

    @Test
    public void testUpdateRecord() throws Exception {
        rtBean.uploadRecord(TradeRecord.StockSymbol.JOE, TradeRecord.StockType.COMMON,
                TradeRecord.SellIndicator.SELL, new Date(), 15, 13, 45, 21);
        assertThat(rtBean.getActualRecords().size()).isEqualTo(3);
        assertThat(rtBean.getStock(TradeRecord.StockSymbol.JOE).size()).isEqualTo(1);

        rtBean.uploadRecord(TradeRecord.StockSymbol.ALE, TradeRecord.StockType.COMMON,
                TradeRecord.SellIndicator.SELL, new Date(), 15, 13, 45, 21);

        assertThat(rtBean.getLastRecord(TradeRecord.StockSymbol.ALE).getTimeStamp()).isAfterOrEqualsTo(aleCurrent.getTimeStamp());
    }

    @Test
    public void testGetActualRecords() throws Exception {
        assertThat(rtBean.getActualRecords()).doesNotContain(ale3MinBefore, gin7MinBefore);
    }

    @Test
    public void testGetHistoryRecords() throws Exception {
        assertThat(rtBean.getHistoryRecords()).contains(aleCurrent, ale3MinBefore, ginCurrent, gin7MinBefore);
    }
}