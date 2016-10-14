# Trades
Web application for Stock Market Tradings

Please find a short demo by link: 
[Demo](#demo)

##Deployment

WEB-APP deployment available by path:
https://github.com/AMalynskyi/Trades/blob/master/trades-webapp/target/trades-1.0.war

After deployment need to navigate to your APP Server using URI: http://{SERVER_HOME}/trades-1.0/

##Architecture

APP architecture is very simple and consist of

###1) EJB server side
  There are two beans on server side:

  * **RecordTradesBean** - Trade Records Entity Manager implementation for In-Memory storage
     Provides Singleton access for trades records data.
     
  * **StockEvaluationBean** - Formulas calculations Bean
     Provides Stateless evaluation functions for given trades parameters.
     
  * **TradeRecord** - POJO for entity parameters
  
###2) UI\Func JSP representations

  * jsp/traderecords.jsp & jsp/trades.jsp
     For UI implementation
  
  * jsp/updaterecord.jsp & jsp/evaluate.jsp
     Processing requests, making beans calls and responces preparation.
     
  * css/styles.css & js/scripts.js
     Styles and js functions
  
###Demo
<p>
<img src="https://github.com/AMalynskyi/Trades/blob/master/demo/demo.gif"x-width:50%;"/>
</p>

