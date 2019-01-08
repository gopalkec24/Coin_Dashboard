/**
 * Custom javascript for Coin Dashboard
 */

function replaceUndefinedData(data)
{
	var returnData = data;
	if(typeof returnData == "undefined")
	{
		returnData ="-";
	}
	return returnData;
}

function filterTransactionData($) {
	var coin = $("#coinName").val().toLowerCase();
    var exchange = $("#exchangeName").val().toLowerCase();
    $("#transactionsummary tr").filter(function() {
    	var toggle= ($(this).text().toLowerCase().indexOf(exchange) > -1) && ($(this).text().toLowerCase().indexOf(coin) > -1);
    	console.log(toggle);
      $(this).toggle(toggle);
    });
    
    
}
function getStatusCSS(value){
	if(value ===0){
	 return "neutral";
	 }
	 else if(value >0){
	  return "profit";
	 }
	 else{
	  return "loss";
	 }
	}