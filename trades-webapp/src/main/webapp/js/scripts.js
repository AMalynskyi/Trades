/**
 * Created by malynskyi on 10/2/2016.
 */

function setSelectedOption(sel, byVal){

    for (i = 0; i< sel.options.length; i++){

        if (sel.options[i].value == byVal){
            sel.options[i].selected = true;
            break;
        }

    }
}

function getAjaxRequest(){
    var ajaxRequest;
    try{
        ajaxRequest = new XMLHttpRequest();
    }catch (e){
       try{
          ajaxRequest = new ActiveXObject("Msxml2.XMLHTTP");
       }catch (e) {
          try{
             ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
          }catch (e){
             return null;
          }
       }
    }
    return ajaxRequest;
}
function vwStockPrice(symb, tFrame, resId){

    if(!document.getElementById(tFrame).validity.valid || document.getElementById(symb).value == "-Choose Symbol-"){
        document.getElementById(resId).innerHTML = '<p style="color: red; font-style: italic">' + "Please fill in all parameters" + '</p>';
        return;
    }

    var ajax = getAjaxRequest();

    ajax.onreadystatechange = function(){

       if(ajax.readyState == 4 && ajax.status == 200){
          var result = document.getElementById(resId);
          result.innerHTML = '<b>' + ajax.responseText + '</b>';
       }
    };

    var url = "jsp/evaluate.jsp";
    url += "?action=VWSP";
    url += "&symbol=" + document.getElementById(symb).value;
    url += "&tFrame=" + document.getElementById(tFrame).value;

    ajax.open("GET", url, true);
    ajax.send(null);
 }

function gbceAllShareIndex(tFrame, resId){

    if(!document.getElementById(tFrame).validity.valid){
        document.getElementById(resId).innerHTML = '<p style="color: red; font-style: italic">' + "Please fill in all parameters" + '</p>';
        return;
    }

    var ajax = getAjaxRequest();

    ajax.onreadystatechange = function(){

       if(ajax.readyState == 4 && ajax.status == 200){
          var result = document.getElementById(resId);
          result.innerHTML = '<b>' + ajax.responseText + '</b>';
       }
    };

    var url = "jsp/evaluate.jsp";
    url += "?action=GBCE";
    url += "&tFrame=" + document.getElementById(tFrame).value ;

    ajax.open("GET", url, true);
    ajax.send(null);
 }