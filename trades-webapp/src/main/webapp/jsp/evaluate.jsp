<%@ page import="org.apache.logging.log4j.*" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="jpmorgan.api.impl.bean.StockEvaluation" %>

<%

    final Logger log = LogManager.getLogger("EVALUATE-JSP");

    StockEvaluation bean = InitialContext.doLookup("java:module/StockEvaluationBean");

    String VWACTION = "VWSP";
    String GBCEACTION = "GBCE";

    String action = request.getParameter("action");
    String tFrame = request.getParameter("tFrame");
    String symbol = request.getParameter("symbol");

    log.debug("received evaluate: " + action + " - " + tFrame + " - " + symbol);

    Double result=null;
    if(VWACTION.equals(action)){
        result = bean.evalVolWeightStockPrice(symbol, Integer.valueOf(tFrame));
    }else if(GBCEACTION.equals(action)){
        result = bean.evalGeometricMean(Integer.valueOf(tFrame));
    }

    response.setContentType("text/xml");

    if(result == null){
        response.getWriter().write("<p style=\"color: red; font-style: italic\">" +
                "Unable to evaluate result for chosen parameters" + "</p>");
    }else {
        response.getWriter().write(String.valueOf(result));
    }

%>
