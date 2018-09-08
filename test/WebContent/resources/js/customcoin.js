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