<%@ page import="org.apache.logging.log4j.*" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="jpmorgan.api.impl.bean.StockEvaluation" %>

<%

    StockEvaluation bean = InitialContext.doLookup("java:module/StockEvaluationBean");

    String VWACTION = "VWSP";
    String GBCEACTION = "GBCE";

    String action = request.getParameter("action");
    String tFrame = request.getParameter("tFrame");
    String symbol = request.getParameter("symbol");

    response.setContentType("text/xml");

    Double result=null;
    try {
        if(VWACTION.equals(action)){
            result = bean.evalVolWeightStockPrice(symbol, Integer.valueOf(tFrame));
        }else if(GBCEACTION.equals(action)){
            result = bean.evalGeometricMean(Integer.valueOf(tFrame));
        }
    } catch (IllegalArgumentException e) {
        response.getWriter().write("<p style=\"color: red; font-style: italic\">" +
                "Incorrect input parameters: " + e.getMessage() + "</p>");
        return;
    }

    if(result == null){
        response.getWriter().write("<p style=\"color: red; font-style: italic\">" +
                "Unable to evaluate result for chosen parameters" + "</p>");
    }else {
        response.getWriter().write(String.valueOf(result));
    }

%>
