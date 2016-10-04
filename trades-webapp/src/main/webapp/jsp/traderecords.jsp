<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://logging.apache.org/log4j/tld/log" prefix="log" %>

<html>
<head>
    <link rel="stylesheet" href="css/styles.css">
    <script type="text/javascript" src="js/scripts.js" ></script>
</head>
<body>
<jsp:useBean id="recordsBean" class="jpmorgan.api.impl.bean.RecordTradesBean" scope="application"/>

<c:catch var="exception">

  <div class="top">
          <table class="trades" title="Evaluation Options">
              <caption class="trades">Evaluation Options</caption>
              <tr class="action">
                  <th width="20%">Option</th>
                  <th width="65%">Parameters</th>
                  <th width="15%">Result</th>
              </tr>
              <tr>
                  <td width="20%"><p class="param">Volume Weighted Stock Price</p></td>
                  <td width="65%">
                      <table class="inline">
                          <tr class="param">
                              <td width="50%">
                                  <label class="eval" for="VWStockSymbol">Stock Symbol:</label>
                                  <select class="param" name="StockSymbol" id="VWStockSymbol" required>
                                      <option name="default" disabled selected>-Choose Symbol-</option>
                                  <c:forEach var="symb" items="${recordsBean.retrieveStockSymbols()}">
                                      <option name="${symb}">${symb}</option>
                                  </c:forEach>
                                  </select>
                              </td>
                              <td width="30%">
                                  <label class="eval" for="VWTimeFrame">Time Frame (last mins)</label>
                                  <input class="eval" name="TimeFrame" id="VWTimeFrame" required type="number" min="0"
                                         step="0.1" value="5"/>
                              </td>
                              <td width="20%">
                                  <input name="Eval" type="button" value="Evaluate"
                                         onclick="vwStockPrice('VWStockSymbol', 'VWTimeFrame', 'VWResult')"/>
                              </td>
                          </tr>
                      </table>
                  </td>
                  <td width="15%">
                      <div class="result" id="VWResult"></div>
                  </td>
              </tr>
              <tr>
                  <td width="20%"><p class="param">GBCE All Share Index</p></td>
                  <td width="65%">
                      <table class="inline">
                          <tr class="param">
                              <td width="50%">
                                  <p class="skip">For all stocks</p>
                              </td>
                              <td width="30%">
                                  <label class="eval" for="GBCETimeFrame">Time Frame (last mins)</label>
                                  <input class="eval" width="10%" name="TimeFrame" id="GBCETimeFrame" required
                                         type="number" min="0" step="0.1" value="5"/>
                              </td>
                              <td width="20%">
                                  <input name="Eval" type="button" value="Evaluate" required
                                         onclick="gbceAllShareIndex('GBCETimeFrame', 'GBCEResult')"/>
                              </td>
                          </tr>
                      </table>
                  </td>
                  <td width="15%">
                      <div class="result" id="GBCEResult"></div>
                  </td>
              </tr>

          </table>
  </div>

  <div class="bottom-scroll">
  <table class="trades" title="Stock trading history">
      <caption class="trades">Stock trading history</caption>
      <tr class="hist">
          <th>Time Stamp</th>
          <th>Stock Symbol</th>
          <th>Type</th>
          <th>Dividend Yield</th>
          <th>Fixed Dividend(%)</th>
          <th>P/E Ratio</th>
          <th>Quantity</th>
          <th>Prise</th>
      </tr>

      <c:forEach var="record" items="${recordsBean.historyRecords}" >
          <tr>
            <td>
                ${record.timeStamp}
            </td>
            <td><b>
                ${record.stockSymbol}
            </b></td>
            <td>
              ${record.stockType}
            </td>
            <td><b>
                ${record.SDividend}
            </b></td>
            <td>
                ${record.fixDividend}
            </td>
            <td>
                ${record.SPeRatio}
            </td>
            <td>
                ${record.qty}
            </td>
            <td>
                ${record.prise}
            </td>
          </tr>
      </c:forEach>

  </table>
  </div>

</c:catch>
<c:if test="${exception != null}">
    <log:catching exception="${exception}" logger="jpmorgan.traderecordsjsp" level="error"/>
</c:if>

</body>
</html>
