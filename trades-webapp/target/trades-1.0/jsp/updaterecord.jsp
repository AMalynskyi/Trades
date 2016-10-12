<%@ page import="jpmorgan.api.impl.bean.RecordTrades" %>
<%@ page import="jpmorgan.api.impl.pojo.TradeRecord" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Updating record</title>
</head>
<body>
<jsp:useBean id="recordsBean" class="jpmorgan.api.impl.bean.RecordTradesBean" scope="application"/>

<div style="text-align: center;">
<h1>Updating record...</h1>
</div>
<%

    RecordTrades bean = InitialContext.doLookup("java:module/RecordTradesBean");

    bean.uploadRecord(TradeRecord.StockSymbol.valueOf(request.getParameter("StockSymbol")),
            TradeRecord.StockType.valueOf(request.getParameter("StockType")),
            TradeRecord.SellIndicator.valueOf(request.getParameter("SellInd")),
            new Date(),
            Integer.valueOf(request.getParameter("FixedDividend")),
            Integer.valueOf(request.getParameter("ParValue")),
            Integer.valueOf(request.getParameter("Price")),
            Integer.valueOf(request.getParameter("Quantity"))
    );
    response.sendRedirect(request.getHeader("Referer"));
%>


</body>
</html>
