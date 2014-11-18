<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.receipt.summary" /></h3>

<div class="body">
<s:form theme="simple" id="searchForm" namespace="/general" action="receipt" method="post" cssClass="ajaxSearchForm" listtable="resultTable">
	<fieldset id="searchFieldset" name="searchFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.search.criteria" /></legend>
		<ul>
			<li>
				<label for="receiptType"><s:text name="receipt.type"/>:</label>
				<s:select
					theme="simple"
					name="receiptType"
					list="receiptTypeList"
					listValue="getText(value)"
					emptyOption="true"
					cssClass="inputText" />
			</li>
			<li>
				<label for="receipt.code"><s:text name="receipt.code"/>:</label>
				<s:textfield theme="simple" name="receipt.code" cssClass="inputText"/>
			</li>
			
			<li>
				<label for="dateFrom"><s:text name="label.from.custom"><s:param><s:text name="label.creation.date"/></s:param></s:text>:</label>
				<s:textfield theme="simple" name="dateFrom" cssClass="inputText dateTimePicker"></s:textfield>
			</li>
			<li>
				<label for="dateTo"><s:text name="label.to" />:</label>
				<s:textfield theme="simple" name="dateTo" cssClass="inputText dateTimePicker"></s:textfield>
			</li>
			
			<li>
				<label for="receiptStatus"><s:text name="receipt.status"/>:</label>
				<s:select
					theme="simple"
					name="receiptStatus"
					list="receiptStatusList"
					listValue="getText(value)"
					emptyOption="true"
					cssClass="inputText" />
			</li>
			<li>
				<label for="receipt.createdBy.username"><s:text name="label.handled.by.user" />:</label>
				<s:textfield theme="simple" name="receipt.createdBy.username" cssClass="inputText"></s:textfield>
			</li>
			
			<li>
				<label for="receipt.staffHandledBy.code"><s:text name="staff.code.handled.by" />:</label>
				<s:textfield theme="simple" name="receipt.staffHandledBy.code" cssClass="inputText"></s:textfield>
			</li>
			<li>
				<label for="receipt.staffPickedBy.code"><s:text name="staff.code.picked.by" />:</label>
				<s:textfield theme="simple" name="receipt.staffPickedBy.code" cssClass="inputText"></s:textfield>
			</li>
		</ul>
	</fieldset>

	<div class="buttonArea">
		<s:submit theme="simple" id="searchButton" key="btn.search" method="getSearchResultPage" cssClass="button blue buttonMargin ajaxSearchButton"></s:submit>
		<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="button rosy buttonMargin"/>
	</div>
</s:form>


<s:form theme="simple" id="receiptListForm" namespace="/general" action="receipt" method="post">
	<fieldset id="resultFieldset" name="resultFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.search.result" /></legend>
		
		<div id="resultTable" class="tableOfInfo1">
			<img alt="loading" class="indicator" src="<s:property value="imagesPath"/>layout/ajax-load.gif" />
			<!-- will auto load from ajax -->
		</div>
	</fieldset>
	
	<div class="buttonArea">
		<s:submit theme="simple" id="btnDetail" method="getDetailPage" key="btn.view" cssClass="button blue buttonMargin"></s:submit>
		<s:submit theme="simple" id="btnExport" method="XXXX" key="btn.export" cssClass="button gray buttonMargin"></s:submit>
		<%--
		<s:submit theme="simple" id="btnPrint" method="printReceipt" key="btn.print" cssClass="button gray buttonMargin"></s:submit>
		--%>
	</div>
</s:form>
</div>



<script>


$(function(){
	$("#searchButton").trigger("click");
	
	/////////////////////////////////////////////
	// Click "View" Button
	/////////////////////////////////////////////
	<s:url var="getDetailPageUrl" namespace="/general" action="receipt" method="getDetailPage"/>
	$("#btnDetail").on("click", function(){
		
		var n = $(".selectedReceiptMarker:checked").length;
		
		if (n == 0)
		{
			alertDialog(
					"",
					"<s:text name="dialog.errors.nothing.select"/>",
					"<s:text name="btn.ok"/>");
			return false;
		}
		else if (n > 1)
		{
			alertDialog(
					"",
					"<s:text name="dialog.errors.select.one.only"/>",
					"<s:text name="btn.ok"/>");
			return false;
		}
		else
		{
		
			
			// [Warning] This method is not support by IE9 or Chrome
// 			// 1. Change the name of checkbox to backend var (jump to another page so no problem)
// 			$(".selectedReceiptMarker:checked").each(function() {
// 				$(this).attr("name", "selectedReceiptList[0].id");
// 			});
// 			// 2. Submit the form
// 			$("#receiptListForm").submit();		// this is not by IE9 or Chrome
			
			
			
			var receiptId = $(".selectedReceiptMarker:checked:first").val();
			var url = "<s:property value="#getDetailPageUrl"/>";
			url += "?selectedReceiptList[0].id=" + receiptId;
			
			$(location).attr('href', url);
			return false;
		}
	});
	
	/////////////////////////////////////////////
	// Click "Export" Button
	/////////////////////////////////////////////
	<s:url var="exportReceiptToPdfUrl" namespace="/general" action="receipt" method="exportReceiptToPdf"/>
	$("#btnExport").on("click", function(){
		
		var n = $(".selectedReceiptMarker:checked").length;
		
		if (n == 0)
		{
			alertDialog(
					"",
					"<s:text name="dialog.errors.nothing.select"/>",
					"<s:text name="btn.ok"/>");
			return false;
		}
		else if (n > 1)
		{
			alertDialog(
					"",
					"<s:text name="dialog.errors.select.one.only"/>",
					"<s:text name="btn.ok"/>");
			return false;
		}
		else
		{
			// Export to PDF
			$("#receiptListForm").attr("target", "_blank");
			var originalAction = $("#receiptListForm").attr("action");					// 2. bkup the original action url
			$("#receiptListForm").attr("action", "<s:property value="#exportReceiptToPdfUrl"/>"); 	// 3. change to new action url
			$("#receiptListForm").submit();
			$("#receiptListForm").attr("action", originalAction);					// 4. recover the original action url (prevent error)
			$("#receiptListForm").attr("target", "");
			return false;
		}
	});
	
	
	/////////////////////////////////////////////
	// Click "Print" Button
	/////////////////////////////////////////////
	/*
	<s:url var="printReceiptUrl" namespace="/general" action="receipt" method="printReceipt"/>
	$("#btnPrint").on("click", function(){
		
		var n = $(".selectedReceiptMarker:checked").length;
		
		if (n == 0)
		{
			alertDialog(
					"",
					"<s:text name="dialog.errors.nothing.select"/>",
					"<s:text name="btn.ok"/>");
			return false;
		}
		else if (n > 1)
		{
			alertDialog(
					"",
					"<s:text name="dialog.errors.select.one.only"/>",
					"<s:text name="btn.ok"/>");
			return false;
		}
		else
		{
			// [Warning] This method is not support by IE9 or Chrome
// 			// 1. Change the name of checkbox to backend var (jump to another page so no problem)
// 			$(".selectedReceiptMarker:checked").each(function() {
// 				$(this).attr("name", "selectedReceiptList[0].id");
// 			});
// 			// 2. Submit the form
// 			$("#receiptListForm").submit();		

			// Printer printing
			var receiptId = $(".selectedReceiptMarker:checked:first").val();
			var url = "<s:property value="#printReceiptUrl"/>";
			var data = "selectedReceiptList[0].id=" + receiptId;
			data += "&struts.enableJSONValidation=true";
// 			url += "?selectedReceiptList[0].id=" + receiptId;
// 			url += "&struts.enableJSONValidation=true";
// 			$(location).attr('href', url);

			$.post(url, data, function(result){
				
				if( result != undefined )
				{
					if (result.actionErrors != undefined && result.actionErrors != "") 
					{
						alertDialog("", result.actionErrors, "<s:text name="btn.ok"/>");
					}
					else if (result.actionMessages != undefined && result.actionMessages != "")
					{
						alertDialog("", result.actionMessages, "<s:text name="btn.ok"/>");
					}
					else
					{
						alert("Error!");
					}
				}
				else
				{
					alertDialog("", "Error!", "<s:text name="btn.ok"/>");
				}
				
			}).error(function(){
				alertDialog("", "Error!", "<s:text name="btn.ok"/>");
			});
			return false;
		}
	});
	*/
});

</script>