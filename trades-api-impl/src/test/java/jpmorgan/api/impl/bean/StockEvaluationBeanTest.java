package jpmorgan.api.impl.bean;

import com.bm.testsuite.BaseSessionBeanFixture;
import jpmorgan.api.impl.pojo.TradeRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;

@RunWith( JUnit4.class )
public class StockEvaluationBeanTest extends BaseSessionBeanFixture<StockEvaluationBean> {

    TradeRecord aleCurrent;
    TradeRecord ale3MinBefore;

    TradeRecord ginCurrent;
    TradeRecord gin7MinBefore;

    public StockEvaluationBeanTest() {
        super(StockEvaluationBean.class, new Class[]{});
    }

    @Before
    public void init() throws Exception {
        super.setUp();
         StockEvaluationBean seBean = getBeanToTest();

         seBean.getTradeRecords().setRecords(new TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>>());

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

         seBean.getTradeRecords().getMarket().put(TradeRecord.StockSymbol.ALE, tMap);

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

        seBean.getTradeRecords().getMarket().put(TradeRecord.StockSymbol.GIN, tMap);
    }

    @After
    public void clear() throws Exception {
         StockEvaluationBean seBean = getBeanToTest();
         seBean.getTradeRecords().setRecords(new TreeMap<TradeRecord.StockSymbol, TreeMap<Date, TradeRecord>>());
         aleCurrent = null;
         ale3MinBefore = null;
         ginCurrent = null;
         gin7MinBefore = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvalCommonDividend() throws Exception {
        getBeanToTest().evalCommonDividend(1.0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvalPreferredDividend() throws Exception {
        getBeanToTest().evalPreferredDividend(1, 2, 0);
    }

    @Test
    public void testEvalDividend() throws Exception {
        assertThat(getBeanToTest().evalDividend(TradeRecord.StockType.COMMON, 1.4, 5, 10, 123)).isEqualTo(1.4/123);
    }

    @Test
    public void testEvalPERatio() throws Exception {
        assertThat(getBeanToTest().evalPERatio(5, 1.0)).isEqualTo(5.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvalPERatioExc() throws Exception {
        getBeanToTest().evalPERatio(5, 0.0);
    }

    @Test
    public void testEvalGeometricMean() throws Exception {
        Double vwsprise = 1.0;
        Double mSum = (double) (aleCurrent.getPrise() * aleCurrent.getQty());
        Double qSum = (double) aleCurrent.getQty();
        vwsprise *= mSum/qSum;
        mSum = (double) (ginCurrent.getPrise() * ginCurrent.getQty());
        qSum = (double) ginCurrent.getQty();
        vwsprise *= mSum/qSum;

        assertThat(getBeanToTest().evalGeometricMean(1)).isEqualTo(Math.pow(vwsprise, 1.0/2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvalGeometricMeanExc() throws Exception {
        getBeanToTest().evalGeometricMean(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvalVolWeightStockPriceExc() throws Exception {
        getBeanToTest().evalVolWeightStockPrice("ASD", 1);
    }

    @Test
    public void testEvalVolWeightStockPrice() throws Exception {

        assertThat(getBeanToTest().evalVolWeightStockPrice("GIN", 1)).isEqualTo(ginCurrent.getPrise()*1.0);
    }
}