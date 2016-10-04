<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://logging.apache.org/log4j/tld/log" prefix="log" %>
<html>
<head>
    <link rel="stylesheet" href="css/styles.css">
    <script type="text/javascript" src="js/scripts.js" ></script>
    <title>Stock Trading</title>
</head>
<body>
<%! int rownum=0; %>
<jsp:useBean id="recordsBean" class="jpmorgan.api.impl.bean.RecordTradesBean" scope="application"/>

<c:catch var="exception">
    <div class="top">
        <table class="trades" title="Stock trading evaluations">
            <caption class="trades">Current Trades</caption>
            <tr class="live">
                <th>Stock Symbol</th>
                <th>Type</th>
                <th>Dividend Yield</th>
                <th>Fixed Dividend(%)</th>
                <th>P/E Ratio</th>
                <th>Sell Indicator</th>
                <th>Quantity</th>
                <th>Prise</th>
                <th>Par Value</th>
                <th>Action</th>
            </tr>

            <c:forEach var="record" items="${recordsBean.actualRecords}" >
                <% rownum++; %>
               <tr>
                    <form name="rowForm" action="jsp/updaterecord.jsp" method="post">
                        <td><b>
                            <input id="stockSymbol" name="StockSymbol" type="text" readonly value="${record.stockSymbol}"/>
                        </b></td>
                        <td>
                            <select name="StockType" id="StockType<%=rownum%>">
                            <c:forEach var="type" items="${recordsBean.retrieveStockTypes()}">
                                <option name="${type}">${type}</option>
                            </c:forEach>
                            </select>
                            <script>
                                setSelectedOption(document.getElementById("StockType<%=rownum%>"),"${record.stockType}");
                            </script>
                        </td>
                        <td>
                            <input id="div" name="DividendYield" type="number" min="1" readonly value="${record.SDividend}"/>
                        </td>
                        <td>
                            <input id="fdiv" name="FixedDividend" type="number" min="1" required value="${record.fixDividend}"/>
                        </td>
                        <td>
                            <input id="peratio" name="PERatio" type="number" min="1" readonly value="${record.SPeRatio}"/>
                        </td>
                        <td>
                            <select name="SellInd" id="SellInd<%=rownum%>">
                            <c:forEach var="ind" items="${recordsBean.retrieveSellIndicators()}">
                                <option name="${ind}">${ind}</option>
                            </c:forEach>

                            </select>
                            <script>
                                setSelectedOption(document.getElementById("SellInd<%=rownum%>"),"${record.sellIndicator}");
                            </script>
                        </td>
                        <td>
                            <input id="qty" name="Quantity" required type="number" min="1" value="${record.qty}"/>
                        </td>
                        <td>
                            <input id="price" name="Price" required type="number" min="1" value="${record.prise}"/>
                        </td>
                        <td>
                            <input id="parVal" name="ParValue" required type="number" min="1" value="${record.parValue}"/>
                        </td>
                        <td>
                            <input id="updbtn" name="Update" type="submit" value="Update"/>
                        </td>
                    </form>
                </tr>
            </c:forEach>

        </table>
    </div>
</c:catch>
<c:if test="${exception != null}">
    <log:catching exception="${exception}" logger="jpmorgan.tradesjsp" level="error"/>
</c:if>

    <div class="bottom">
        <jsp:include page="traderecords.jsp"/>
    </div>
</body>
</html>
