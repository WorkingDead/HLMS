<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.ironing.delivery" /></h3>

<div class="body">

	<s:form theme="simple" id="addFrom" namespace="/general" action="ironing-delivery-fixed-reader" method="post" cssClass="ajaxForm">

<!-- ################################################## -->
<!-- Receipt Info Fieldset -->
<!-- ################################################## -->
		<fieldset id="receiptInfoFieldset" name="receiptInfoFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.receipt.info"/></legend>
			
			<div class="actionErrors" id="actionErrorDiv01" ref="addFrom"></div>
			<div class="actionMessages" id="actionMessageDiv01" ref="addFrom"></div>
		
			<div class="alignRight">
				<s:submit theme="simple" type="button" id="ajaxBtnReceiptSave" key="btn.finish" method="create" cssClass="button blue buttonMarginCorner ajaxButton" />
				<s:submit theme="simple" type="button" id="ajaxBtnReceiptReset" key="btn.reset" method="XXXXXXX" cssClass="button rosy buttonMarginCorner" />
			</div>
			
			<ul>
				<li>
					<label for="receipt.code"><s:text name="receipt.code"/>:</label>
					<s:textfield theme="simple" name="receipt.code" cssClass="displayText" readonly="true"/>
				</li>
				
				<li>
					<label for="receiptClothTotal"><s:text name="receipt.cloth.total"/>:</label>
					<s:textfield theme="simple" id="receiptClothTotal" name="receiptClothTotal" cssClass="displayText" readonly="true"/>
				</li>
				
				<li>
					<label for="rreceiptRemark"><s:text name="receipt.remark"/>:</label>
					<s:textarea theme="simple" id="receiptRemark" name="receipt.remark"/>
				</li>
			</ul>
		</fieldset>
	</s:form>

<!-- ################################################## -->
<!-- Roll Container Fieldset -->
<!-- ################################################## -->
	<fieldset id="rollContainerFieldset" name="rollContainerFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.roll.container.info"/></legend>
		
		<%--
		<div class="actionErrors" ref="rollContainerFieldset"></div>
		<div class="actionMessages" ref="rollContainerFieldset"></div>
		--%>
		
		<div class="alignRight">
		</div>
		
		<ul>
			<li>
				<label for="rollContainer.id"><s:text name="roll.container.code"/>:</label>
				<s:select
					theme="simple"
					id="cartSelectionBox"
					name="rollContainer.id"
					list="rollContainerList"
					listKey="id"
					listValue="code"
					emptyOption="true"
					disabled="false"
					cssClass="inputText" />
			</li>
			
			<!--
			<li>
				<label for="captureFor"><s:text name="ironing.delivery.roll.container.rfid.capture"/>:</label>
				<input type="radio" name="captureFor" value="1" class="radioText" checked="checked"><s:text name="ironing.delivery.add.to.roll.container"/>
				<input type="radio" name="captureFor" value="2" class="radioText"><s:text name="ironing.delivery.remove.from.roll.container"/>
			</li>
			-->
		</ul>
	</fieldset>
	
	<div class="buttonArea">
		<s:submit theme="simple" type="button" id="btnCaptureStart" key="btn.capture" method="XXXX" cssClass="button blue buttonMargin" />
		<s:submit theme="simple" type="button" id="btnCaptureStop" key="btn.capture.stop" method="XXXXXX" cssClass="button rosy buttonMargin" />
	</div>
	
<!-- ################################################## -->
<!-- RFID Capture Summary Fieldset -->
<!-- ################################################## -->
	<fieldset id="rfidCaptureSummaryFieldset" name="rfidCaptureSummaryFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.rfid.capture.summary"/></legend>
		
		<div class="RFIDTotal">
			<!-- RFID Summary Table -->
			<table id="RFIDTotal">
				<thead>
					<tr><td><s:text name="label.cloth.total"/></td></tr>
				</thead>
				
				<tbody>
					<tr>
						<td class="bigText">
							<s:property value="rollContainerClothTotal"/>
						</td>
					</tr>
				</tbody>
				
				<tfoot></tfoot>
			</table>
		</div>
				
		<div class="RFIDSummary">
			<table id="RFIDSummary">
				<thead>
					<tr>
						<td><s:text name="clothType.clothType"/></td>
						<td><s:text name="label.type.total"/></td>
					</tr>
				</thead>
				
				<tbody>
					<!-- Auto Generated Area -->
				</tbody>
			
				<tfoot></tfoot>
			</table>	
			<!-- End of RFID Summary Table -->
		</div>
	</fieldset>

<!-- ################################################## -->
<!-- RFID Capture Detail Fieldset -->
<!-- ################################################## -->
	<fieldset id="rfidCaptureDetailFieldset" name="rfidCaptureDetailFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.rfid.capture.detail"/></legend>
		
		<div class="alignRight">
			<s:url var="removeClothUrl" action="ironing-delivery-fixed-reader" method="ajaxRemoveRfid"></s:url>
			<s:submit theme="simple" 
				type="button" 
				id="ajaxBtnRemoveCloth" 
				key="btn.remove" 
				cssClass="button rosy buttonMarginCorner" 
				ref="rfidCaptureDetailFieldset" 
				url="#removeClothUrl" />
		</div>
		
		<div class="actionErrors" ref="rfidCaptureDetailFieldset"></div>
		<div class="actionMessages" ref="rfidCaptureDetailFieldset"></div>
		
		<div class="RFIDTable">
			<!-- Retrieve Data Table -->
			<table id="RFIDTable" class="dataTable">
				<thead>
					<tr>
					
						<th class="simpleCheckBox">
							<s:checkbox theme="simple" 
								id="checkAllDataRows" 
								name="checkAllDataRows" 
								cssClass="simpleCheckBox checkall" 
								ref="checkAllDataRows">
							</s:checkbox>
						</th>
						<th><s:text name="staff.dept"/></th>
						<th><s:text name="staff.code"/></th>
						<th><s:text name="staff.name"/></th>
						<th><s:text name="clothType.clothType"/></th>
						<th><s:text name="cloth.code"/></th>
						<th><s:text name="label.rfid"/></th>
					</tr>
				</thead>
				
				<tbody>
					<!-- Auto Generated Area -->
				</tbody>
				
				<tfoot></tfoot>
			</table>
			<!-- End of Retrieve Data Table -->
		</div>
	</fieldset>
</div>



<script>

$(function() {
	
	pageIntialSetting();	// by Kitz's requirements
// 	validatePageScheduler();
	
	// Initial setting when entering this page (by Kitz's requirement)
	
	
	
	<s:url var="startCaptureUrl" namespace="/general" action="ironing-delivery-fixed-reader" method="startCapture"></s:url>
	$("#btnCaptureStart").on("click", function() {
		
		$("#btnCaptureStart").attr("disabled", true);
		$("#btnCaptureStart").addClass("disableButton");
		$("#btnCaptureStart").removeClass("blue");
		
		$("#btnCaptureStop").attr("disabled", false);
		$("#btnCaptureStop").addClass("rosy");
 		$("#btnCaptureStop").removeClass("disableButton");
		
		$("#ajaxBtnReceiptSave").attr("disabled", true);
 		$("#ajaxBtnReceiptSave").addClass("disableButton");
 		$("#ajaxBtnReceiptSave").removeClass("blue");

		$("#ajaxBtnReceiptReset").attr("disabled", true);
		$("#ajaxBtnReceiptReset").addClass("disableButton");
		$("#ajaxBtnReceiptReset").removeClass("rosy");
		
		$("#ajaxBtnRemoveCloth").attr("disabled", true);
 		$("#ajaxBtnRemoveCloth").addClass("disableButton");
 		$("#ajaxBtnRemoveCloth").removeClass("rosy");
		
 		
		$("#ajaxBtnRemoveRowsOfRfid").attr("disabled", true);
		$("#cartSelectionBox").attr("disabled", true);
		
		$.ajax({
			url: '<s:property value="#startCaptureUrl"/>'
		});
		
		// Very import to call worker() here because worker() check disabled status of btn #btnCaptureStart
		worker();
		
		return false;
	});
	
	<s:url var="stopCaptureUrl" namespace="/general" action="ironing-delivery-fixed-reader" method="stopCapture"></s:url>
	$("#btnCaptureStop").on("click", function() {
		
		$.ajax({
			url : '<s:property value="#stopCaptureUrl"/>'
		});
		
		
		$("#btnCaptureStart").attr("disabled", false);
 		$("#btnCaptureStart").removeClass("disableButton");
 		$("#btnCaptureStart").addClass("blue");
		
		$("#btnCaptureStop").attr("disabled", true);
 		$("#btnCaptureStop").addClass("disableButton");
 		$("#btnCaptureStop").removeClass("rosy");
		
		$("#ajaxBtnReceiptSave").attr("disabled", false);
		$("#ajaxBtnReceiptSave").addClass("blue");
 		$("#ajaxBtnReceiptSave").removeClass("disableButton");

		$("#ajaxBtnReceiptReset").attr("disabled", false);
		$("#ajaxBtnReceiptReset").addClass("rosy");
 		$("#ajaxBtnReceiptReset").removeClass("disableButton");
		
		$("#ajaxBtnRemoveCloth").attr("disabled", false);
		$("#ajaxBtnRemoveCloth").addClass("rosy");
 		$("#ajaxBtnRemoveCloth").removeClass("disableButton");
		
		
		$("#ajaxBtnRemoveRowsOfRfid").attr("disabled", false);
		$("#cartSelectionBox").attr("disabled", false);
		
		return false;
	});
	
	<s:url var="resetReceiptUrl" namespace="/general" action="ironing-delivery-fixed-reader" method="resetReceipt"></s:url>
	$("#ajaxBtnReceiptReset").on("click", function() {
		
		optionsAvailableMsgDialog(
				"", 
				"<s:text name="dialog.msg.confirm.ask"/>", 
				
				"<s:text name="btn.yes"/>",
				function()
				{
					$.ajax({
						url : '<s:property value="#resetReceiptUrl"/>',
						success: function()
						{
							$("#receiptInfoFieldset .actionErrors").empty();
							$("#receiptInfoFieldset .actionMessages").empty();
							$("#receiptInfoFieldset .actionErrors").hide();
							$("#receiptInfoFieldset .actionMessages").hide();
							
							$("#rfidCaptureDetailFieldset .actionErrors").empty();
							$("#rfidCaptureDetailFieldset .actionMessages").empty();
							$("#rfidCaptureDetailFieldset .actionErrors").hide();
							$("#rfidCaptureDetailFieldset .actionMessages").hide();
							
							$("#receiptRemark").val("");
							$("#receiptClothTotal").val( 0 );
							$("#RFIDTotal tbody tr td").text( 0 );
							$("#RFIDSummary tbody").text("");
							$("#RFIDTable tbody").text("");
							
							$("#cartSelectionBox option:eq(0)").attr('selected', 'selected');
							$("#cartSelectionBox").trigger("change");
						},
						complete: function()
						{
						}
					});
				},
				
				"<s:text name="btn.no"/>",
				function()
				{
					// nothing to do
				}
		);
			
		return false;
	});
	
	
	
	
	// To server: select a container and get its cloth info
	<s:url var="getSavedRfidJsonUrl" namespace="/general" action="ironing-delivery-fixed-reader" method="getSavedRfidJson"></s:url>
	$("#cartSelectionBox").live("change", function(){
		
		var containerId = $("#cartSelectionBox").val();
		if (containerId == "")
		{
			$("#btnCaptureStart").attr("disabled", true);
			$("#btnCaptureStart").addClass("disableButton");
			$("#btnCaptureStart").removeClass("blue");
			
			$("#btnCaptureStop").attr("disabled", true);
	 		$("#btnCaptureStop").addClass("disableButton");
	 		$("#btnCaptureStop").removeClass("rosy");
			
			$("#RFIDTotal tbody tr td").text( "" );
			$("#RFIDSummary tbody tr td").text( "" );
			$("#RFIDTable tbody").text("");
		}
		else
		{
			$.ajax({
				url: '<s:property value="#getSavedRfidJsonUrl"/>',
				data: { "rollContainer.id": containerId, "struts.enableJSONValidation": true },
				success: function(data) {
					updateDisplayTables(data, true);
				},
				complete: function() {
				}
			});
			
			$("#btnCaptureStart").attr("disabled", false);
	 		$("#btnCaptureStart").removeClass("disableButton");
	 		$("#btnCaptureStart").addClass("blue");
		}
	});
		
	
	<s:url var="getCapturedRfidJsonUrl" namespace="/general" action="ironing-delivery-fixed-reader" method="getCapturedRfidJson"></s:url>
	function worker()
	{
		var captureBtnDisable = $("#btnCaptureStart").attr("disabled");
		if (captureBtnDisable == "disabled")	// disabled means capturing
		{
			$.ajax({
				url: '<s:property value="#getCapturedRfidJsonUrl"/>',
				data: { "rollContainer.id": $("#cartSelectionBox").val(), "struts.enableJSONValidation": true },
				success: function(result) {
					// 2.1. update receipt-cloth-total
					$("#receiptClothTotal").val(result.receiptClothTotal);
					
					// 2.2. update tables
					updateDisplayTables(result, false);
				},
				complete: function() {
					setTimeout(worker, 1000);
				}
			});
		}
	}
	
	function pageIntialSetting()
	{
		$("#btnCaptureStart").attr("disabled", true);
		$("#btnCaptureStart").addClass("disableButton");
		$("#btnCaptureStart").removeClass("blue");
		
		$("#btnCaptureStop").attr("disabled", true);
 		$("#btnCaptureStop").addClass("disableButton");
 		$("#btnCaptureStop").removeClass("rosy");
	}
	
	function disableAllButton()
	{
		$("#btnCaptureStart").attr("disabled", true);
		$("#btnCaptureStart").addClass("disableButton");
		$("#btnCaptureStart").removeClass("blue");
		
		$("#btnCaptureStop").attr("disabled", true);
 		$("#btnCaptureStop").addClass("disableButton");
 		$("#btnCaptureStop").removeClass("rosy");
 		
		$("#ajaxBtnReceiptSave").attr("disabled", true);
 		$("#ajaxBtnReceiptSave").addClass("disableButton");
 		$("#ajaxBtnReceiptSave").removeClass("blue");
 		
		$("#ajaxBtnReceiptReset").attr("disabled", true);
		$("#ajaxBtnReceiptReset").addClass("disableButton");
		$("#ajaxBtnReceiptReset").removeClass("rosy");
		
		$("#ajaxBtnRemoveCloth").attr("disabled", true);
 		$("#ajaxBtnRemoveCloth").addClass("disableButton");
 		$("#ajaxBtnRemoveCloth").removeClass("rosy");
		
		$("#ajaxBtnRemoveRowsOfRfid").attr("disabled", true);
		$("#cartSelectionBox").attr("disabled", true);
	}
	
	
// 	<s:url var="validatePageSchedulerUrl" namespace="/general" action="ironing-delivery-fixed-reader" method="checkPageMaster"></s:url>
// 	function validatePageScheduler()
// 	{
// 		$.ajax({
// 			url: '<s:property value="#validatePageSchedulerUrl"/>',
// 			success: function(result) {
				
// 				var masterIsMe = result.masterIsMe;
// 				// alert("masterIsMe: " + masterIsMe);
				
// 				if (masterIsMe)
// 				{
// 					setTimeout(validatePageScheduler, 10000);
// 				}
// 				else
// 				{
// 					disableAllButton();
					
// 					alertDialog(
// 							"", 
// 							"<s:text name="errors.page.no.longer.valid"/>", 
// 							"<s:text name="btn.ok"/>"
// 					);
// 				}
				
// 			},
// 			complete: function() {
// 				// nothing to do
// 			}
// 		});
// 	}
	
	
	
	
	function updateDisplayTables(data, clearRfidTable)
	{
		// clear RFID Detail table tbody
		if (clearRfidTable == true)
		{
			$("#RFIDTable tbody").text("");
		}
      	
		// 1. append rows to RFIDTable
		$.each(data.clothList, function(key, value) {
			
			var row = $("<tr/>").attr("id", "RFIDTable_row_" + value.rfid);
// 			if (value.clothStatus != 'Washing')
// 			{
// 				row.attr("class", "highlightWarning");
// 			}
			
			row.append(
				$("<td/>")
					.append(
						$("<input/>")
						.attr("id", value.rfid)
						.attr("type", "checkbox")
						.attr("name", "cb_rfid_" + value.rfid)
						.attr("checkall", "checkAllDataRows")
						.addClass("simpleCheckBox checkall2 rfidMaybeRemoveMarker") 
					).append(
						$("<input/>")
						.attr("id", "hidden_rfid_" + value.rfid)
						.attr("type", "hidden")
						.attr("name", "hidden_rfid_" + value.rfid)
						.val("" + value.rfid) ) 
			);

			row.append($("<td/>").text(value.staff.dept.nameCht));
			row.append($("<td/>").text(value.staff.code));
			row.append($("<td/>").text(value.staff.nameCht));
			row.append($("<td/>").text(value.clothType.name));
			row.append($("<td/>").text(value.code));
			row.append($("<td/>").text(value.rfid));
			
			$("#RFIDTable tbody").append(row);
		});
		
		// 2 update container-cloth-total
		$("#RFIDTotal tbody tr td").text( data.rollContainerClothTotal );
		
		
		// 3. refresh RFIDSummary table
		$("#RFIDSummary tbody").text("");
		$.each(data.clothTypeCountList, function(key, value) {
			
			var row = $("<tr/>");
			row.append($("<td/>").text(value.type));
			row.append($("<td/>").text(value.num));
			
			$("#RFIDSummary tbody").append(row);
		});
	}
	
	
	// To server: delete selected cloth from rfidFromMapRfidClothAll
	$("#ajaxBtnRemoveCloth").live('click', function() {
				// check that at least one row must be selected 
				var n = $(".rfidMaybeRemoveMarker:checked").length;
				if (n == 0)
				{
					alertDialog(
							"", 
							"<s:text name="dialog.errors.nothing.select"/>", 
							"<s:text name="btn.ok"/>"
					);
					return false;
				}
				
				optionsAvailableMsgDialog(
					"", 
					"<s:text name="dialog.msg.confirm.ask"/>", 
					"<s:text name="btn.yes"/>",
					function()
					{
						//////////////////////////////////////////////////////////////////////////
						// 1. Preparing the data submit to Server, only checked row will submit
						//////////////////////////////////////////////////////////////////////////
						var j = 0;
						$(".rfidMaybeRemoveMarker").each(function() {
							if ($(this).attr("checked"))
							{
								var curRowIndex = $(this).attr("id");	// RFID as the row index
								$(this).attr("disabled", true);			// checkbox's value will not be submitted
								$("#hidden_rfid_" + curRowIndex).attr("disabled", false);	// ensure this hidden field's value will be submitted
								$("#hidden_rfid_" + curRowIndex).attr("name", "rfidToBeRemovedList[" + j + "]");
								j++;
							}
							else
							{
								var curRowIndex = $(this).attr("id");
								$(this).attr("disabled", true);
								$("#hidden_rfid_" + curRowIndex).attr("disabled", true);	// also disable this hidden field
							}
						});
						
						
						//////////////////////////////////////////////////////////////
						// 2. Request Server to remove the selected RFID at back end
						//////////////////////////////////////////////////////////////
						var ajaxBtnRemoveClothVar = $("#ajaxBtnRemoveCloth");
						var elementId = ajaxBtnRemoveClothVar.attr("ref");		// ref: fieldset id
						var triggerTarget = $("#" + elementId);
						var div = $("#" + elementId + " :input");	// unchecked checkboxs and their hidden field are disabled and won't be submitted 
						
						div.removeClass("fieldErrors");
						var url = ajaxBtnRemoveClothVar.attr("url");				// url: the method of ActionClass to be called
						var data = div.serialize();
						data += "&rollContainer.id=" + $("#cartSelectionBox").val();	// [Important] provide the container id
						data += "&struts.enableJSONValidation=true";
						resetAllMessage(elementId);
						
						
						$.post(
								url,
								data,
								function(result) {
									
									if (result.errors == undefined && result.fieldErrors == undefined)
									{
										triggerTarget.trigger(elementId + ".success", result);
										removeActionMessageAndActionError(elementId);
										
										// 2.1. update receipt-cloth-total
										$("#receiptClothTotal").val(result.receiptClothTotal);
										
										// 2.2. update container-cloth-total
										$("#RFIDTotal tbody tr td").text( result.rollContainerClothTotal );
										
										// 2.3. refresh RFIDSummary table
										$("#RFIDSummary tbody").text("");
										$.each(result.clothTypeCountList, function(key, value) {
											
											var row = $("<tr/>");
											row.append($("<td/>").text(value.type));
											row.append($("<td/>").text(value.num));
											
											$("#RFIDSummary tbody").append(row);
										});
										
										
									}
									
									if (result.actionMessages != undefined)
									{
										$.each(result.actionMessages, function(key, value) {
											addActionMessage(elementId, value);
										});
									}
									if (result.actionErrors != undefined) {
										$.each(result.actionErrors, function(key, value) {
											addActionError(elementId, value);
										});
									}
									if (result.fieldErrors != undefined){
										$.each(result.fieldErrors, function(key, value) {
											$('input[name="'+key+'"]').addClass("fieldErrors");
										});
									}
								}, "json").error(
								function(jqXHR, textStatus, errorThrown) {
									$('<div class="actionError">' + textStatus + '</div>')
											.appendTo('div.actionErrors[ref="' + elementId + '"]');
								});
						
						/////////////////////////////////////////////////////////////////////////
						// 3. Remove row from HTML and enable the checkbox in rest rows
						/////////////////////////////////////////////////////////////////////////
						$(".rfidMaybeRemoveMarker").each(function() {
							if ($(this).attr("checked"))
							{
								var curRowIndex = $(this).attr("id");
								$("#RFIDTable_row_" + curRowIndex).remove();
							}
							else
							{
								var curRowIndex = $(this).attr("id");
								$(this).attr("disabled", false);
								$("#hidden_rfid_" + curRowIndex).attr("disabled", false);
							}
						});
						
						$("#checkAllDataRows").attr("checked", false);
						
						return false;
					},
					
					"<s:text name="btn.no"/>",
					function()
					{
					}
				);
				
				return false;
			});
	// To server: delete selected cloth from rfidFromMapRfidClothAll
	
	
	
	//////////////////////////////////////////////////
	// Go to new page when save success
	//////////////////////////////////////////////////
	<s:url var="getNewPageUrl" namespace="/general" action="ironing-delivery-fixed-reader" method="getMainPage" />
	$("body").delegate("#addFrom", "addFrom.success", function(e, result, result2) {
		
		msgDialog("", result.actionMessages, 
				
				"<s:text name="btn.ok"/>",
				function()
				{
					$(location).attr('href',"<s:property value="#getNewPageUrl"/>");
				}
			);
	});
	
	/////////////////////////////////////////////////////////////////
	// Go to new page or Receipt Summary Page after save success
	/////////////////////////////////////////////////////////////////
	<s:url var="getNewPageUrl" namespace="/general" action="ironing-delivery-fixed-reader" method="getMainPage" />
	<s:url var="getListPageUrl" namespace="/general" action="receipt" method="getListPage"/>
	
	$("body").delegate("#addFrom", "addFrom.success", function(e, result, result2) {
		
		optionsAvailableMsgDialog("", result.actionMessages, 
				"<s:text name="btn.continue"/>",
				function()
				{
					$(location).attr('href',"<s:property value="#getNewPageUrl"/>");
				},
				
				"<s:text name="btn.ok"/>",
				function()
				{
					$(location).attr('href',"<s:property value="#getListPageUrl"/>");
				}
			);
	});
	
});
	
</script>