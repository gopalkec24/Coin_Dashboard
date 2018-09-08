<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="com.gopal.dao.PresetVO"%>
<<jsp:useBean id="testPrint" class="com.gopal.dao.PresetVO" scope="request" />
<!DOCTYPE html>
<html>
<head>
    <title>Dynamic Drop Down List Demo (AJAX) - CodeJava.net</title>
    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script>
            $(document).on("click", "#cosa", function() {
	 			
                $.get("MyBadLuck",{cosa : $("#cosa option:selected").text()}, function(responseJson) {
                    var $select = $("#preset");
                    $select.find("option").remove();   
                    $.each(responseJson, function(index, category) {
                        $("<option>").val(category).text(category).appendTo($select);
                    }); 
						$("#from").val("");
						$("#select").val("");
						$("#where").val("");
                   
                     
                });
            });

 			$(document).on("click", "#preset", function(responseJson) {
	 			
                $.get("MyBadLuck",{cosa : $("#cosa option:selected").text(),preset : $("#preset option:selected").text()}, function(responseJson) {
                 
					$("#from").val(responseJson.from);
					$("#select").val(responseJson.select);
					$("#where").val(responseJson.where);
					                                    
                     
                });
            });
             
            $(document).on("click", "#buttonSubmit", function() {
                
                $.post("MyBadLuck", {cosa : $("#cosa").val(),preset : $("#preset").val(),select:$("#select").val(),from:$("#from").val(),where:$("#where").val()}, function(responseText) {
                    alert(responseText);                    
                });
            });
        </script> 
</head>
<body>
<div align="center">
     <h2>Dynamic Drop Down List (AJAX) Demo</h2>
    <button id="buttonLoad">Load</button> &nbsp;

	<select id="cosa">
	<option value="0" selected="selected" >Choose cosa</option>
	<option value="ASC">ASC</option>
	<option value="GC">GC</option>
	</select>
    <select id="preset"></select>
    <br/><br/>
	<input id="select" name="select" >
	<input id="from" name="from" >
	<input id="where" name="where" >
    <button id="buttonSubmit">Submit</button> 

<%-- <table>
		
		<tr>
		   <th>  TEst </th>
		   
		    </tr>
		    <tr>
		    <td> 
		    <% out.print(testPrint.getWhere());%>
		    </td>
		    </tr>
		</table> --%>

</div>
</body>
</html>