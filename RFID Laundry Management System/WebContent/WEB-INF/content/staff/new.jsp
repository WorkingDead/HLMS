<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.staff.new" /></h3>

<div class="body">
<s:form theme="simple" id="addFrom" namespace="/master" action="staff" method="post" cssClass="ajaxForm">
	
<!-- ################################################## -->
<!-- GeneralInfo Fieldset -->
<!-- ################################################## -->
	<fieldset id="generalInfoFieldset" name="generalInfoFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.generalinfo"/></legend>
		
		<!-- This div's ref must set to form id -->
		<div class="actionErrors" ref="addFrom"></div>
		<div class="actionMessages" ref="addFrom"></div>
		
		<ul>
			<li>
				<label for="staff.code"><s:text name="staff.code"/>: </label>
				<s:textfield theme="simple" name="staff.code" cssClass="inputText" />
			</li>
			
			<li>
				<label for="staffPositionSelectionBox"><s:text name="staff.position"/>: </label>
				<s:select
					theme="simple"
					id="staffPositionSelectionBox"
					name="staff.position"
					list="positionList"
					disabled="false"
					cssClass="inputText" />
			</li>
			
			<li>
				<label for="staff.nameCht"><s:text name="staff.name.cht"/>: </label>
				<s:textfield theme="simple" name="staff.nameCht" cssClass="inputText"/>
			</li>
			
			<li>
				<label for="staff.nameEng"><s:text name="staff.name.eng"/>: </label>
				<s:textfield theme="simple" name="staff.nameEng" cssClass="inputText"/>
			</li>
			
			<li>
				<label for="deptSelectionBox"><s:text name="staff.dept"/>: </label>
				<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
					<s:select
						theme="simple"
						id="deptSelectionBox"
						name="staff.dept.id"
						list="deptList"
						listKey="id"
						listValue="nameCht"
						disabled="false"
						cssClass="inputText" />
				</s:if>
				<s:else>
					<s:select
						theme="simple"
						id="deptSelectionBox"
						name="staff.dept.id"
						list="deptList"
						listKey="id"
						listValue="nameEng"
						disabled="false"
						cssClass="inputText" />
				</s:else>
			</li>
			
			<li>
				<label for="staffStatusSelectionBox"><s:text name="staff.status"/>: </label>
				<s:select
					theme="simple"
					id="staffStatusSelectionBox"
					name="staff.staffStatus"
					list="staffStatusList"
					listValue="getText(value)"
					disabled="false"
					cssClass="displayText" />
			</li>
			
			<li>
				<label for="staff.cardNumber"><s:text name="staff.card"/>: </label>
				<s:textfield theme="simple" name="staff.cardNumber" cssClass="inputText" />
			</li>
			
			<li>
				<label for="staff.enable"><s:text name="label.enable"/>: </label>
				<s:checkbox theme="simple" name="staff.enable" value="true" fieldValue="true" cssClass="simpleCheckBox"/>
			</li>
		</ul>
	</fieldset>


<!-- ################################################## -->
<!-- Add Cloth Fieldset -->
<!-- ################################################## -->
	<fieldset id="newClothFieldset" name="newClothFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.cloth.add"/></legend>
		
		<div class="actionErrors" ref="newClothFieldset"></div>
		<div class="actionMessages" ref="newClothFieldset"></div>
		
		<div class="alignRight">
			<s:submit theme="simple" type="button" id="insertNewRowOfInputFields" key="btn.insert" cssClass="button white" />
			<s:submit theme="simple" type="button" id="removeRowsOfInputFields" key="btn.remove" cssClass="button white" />
		</div>

		<!-- Input Data Table -->
		<table id="tableForAddRow" class="dataTable">
			<thead>
				<tr>
					<th class="simpleCheckBox">
						<s:checkbox theme="simple" 
							id="checkAllInputRows" 
							name="checkAllInputRows" 
							cssClass="simpleCheckBox checkall" 
							ref="checkAllInputRows">
						</s:checkbox>
					</th>
					<th><s:text name="cloth.type" /></th>
					<th><s:text name="cloth.size" /></th>
					<th><s:text name="cloth.code" /></th>
					<th><s:text name="label.rfid" /></th>
					<th><s:text name="cloth.status" /></th>
					<th><s:text name="cloth.remark" /></th>
				</tr>
			</thead>
			
			<tbody>
				<tr id="tmpRow0">
					<td>
						<s:checkbox theme="simple"
							id="0" 
							name="rowIndex" 
							cssClass="checkall2" 
							checkall="checkAllInputRows">
						</s:checkbox>
					</td>
					
					<td>
						<%--
 						<s:textfield theme="simple" name="clothList[0].clothType.id" cssClass="tblUniformNo" />
						--%>
						
						<s:select theme="simple" 
							id="clothInfo_0_typeId" 
							name="clothList[0].clothType.id" 
							list="clothTypeList" 
							listKey="id" 
							listValue="name" 
							emptyOption="false"
							cssClass="tblUniformType" />
					</td>
					
					<td>
						<%--
 						<s:textfield theme="simple" name="clothList[0].size" cssClass="tblUniformSize" />
 						--%>
						<s:select theme="simple" 
							id="clothInfo_0_size" 
							name="clothList[0].size"
							list="clothSizeList"
							cssClass="tblUniformSize" />
					</td>
					
					
					<td><s:textfield theme="simple" id="clothInfo_0_code" name="clothList[0].code" cssClass="tblUniformNo" /></td>
					<td><s:textfield theme="simple" id="clothInfo_0_rfid" name="clothList[0].rfid" cssClass="tblRFID" /></td>
					
					<td>
						<%--
 						<s:textfield theme="simple" name="clothList[0].clothStatus" cssClass="tblUniformStatus" />
 						--%>
													
						<s:select theme="simple" 
							id="clothInfo_0_status" 
							name="clothList[0].clothStatus" 
							list="clothStatusList" 
							listValue="getText(value)"
							emptyOption="false"
							disabled="false"
							cssClass="displayText tblUniformStatus" />
					</td>
					
					<td><s:textfield theme="simple" id="clothInfo_0_remark" name="clothList[0].remark" cssClass="tblRemark" /></td>
				</tr>
			</tbody>
		</table>
		
		<div class="buttonArea">
			<s:url var="validatePatternUrl" action="staff" method="checkPattern"></s:url>
			<s:submit theme="simple" 
				type="button" 
				id="ajaxBtnAddToListTable"
				key="btn.add" 
				cssClass="button gray buttonMargin" 
				ref="newClothFieldset"
				url="#validatePatternUrl" />
		</div>
	</fieldset>

<!-- ################################################## -->
<!-- ClothList Fieldset -->
<!-- ################################################## -->
	<fieldset id="clothListFieldset" name="clothListFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.cloth.list"/></legend>
		
		<!-- Cloth List Table-->
		<table id="clothListTable" class="dataTable">
			<thead>
				<tr>
					<th class="simpleCheckBox">
						<s:checkbox theme="simple" 
							id="checkAllDataRows" 
							name="checkAllDataRows" 
							disabled="true"
							cssClass="simpleCheckBox checkall" 
							ref="checkAllDataRows"></s:checkbox>
					</th>
					<th><s:text name="cloth.type" /></th>
					<th><s:text name="cloth.size" /></th>
					<th><s:text name="cloth.code" /></th>
					<th><s:text name="label.rfid" /></th>
					<th><s:text name="cloth.status" /></th>
					<th><s:text name="cloth.remark" /></th>
				</tr>
			</thead>
			
			<tbody>
				<!-- dynamic generate -->
			</tbody>
			
			<tfoot></tfoot>
		</table>
	</fieldset>
	
	<div class="buttonArea">
		<s:submit theme="simple" type="button" key="btn.save" method="create" cssClass="button blue buttonMargin ajaxButton" />
		<s:submit theme="simple" type="button" key="btn.cancel" id="cancelButton" cssClass="button rosy buttonMargin" />
	</div>
</s:form>

</div>



<script>

//////////////////////////////////////////////////////////////////////
// For generating the input field
//////////////////////////////////////////////////////////////////////
var fieldIndex = 1;

function resetFieldIndex()
{
	fieldIndex = 0;
}

//////////////////////////////////////////////////////////////////////
// DOM ready (HTML Document Load Complete)
//////////////////////////////////////////////////////////////////////

$(function() {
	
	// Press 'Cancel' button will direct to the List Page
	<s:url var="backToListUrl" namespace="/master" action="staff"  method="getListPage"/>
	$("#cancelButton").on('click', function(){
		var url = "<s:property value="#backToListUrl"/>";
		$(location).attr('href', url);
		
		return false;
	});
	
	
	// INSERT BUTTON: create a row of input fields
	$("#insertNewRowOfInputFields").click(function() {
		appendOneRowOfInputFields();
		return false;
	});
	// INSERT BUTTON: create a row of input fields
	
	// CLEAR ALL BUTTON: reset input fields area
	function removeAllRowsOfInputFields()
	{
		$("table#tableForAddRow tbody tr").remove();
	}
	
	$("#clearAllRowsOfInputFields").click(function() {
		optionsAvailableMsgDialog(
				"", 
				"<s:text name="dialog.msg.confirm.ask"/>", 
				"<s:text name="btn.yes"/>",
				function(){
					removeAllRowsOfInputFields();	// clear ALL rows
					resetFieldIndex();
					appendOneRowOfInputFields();	// one row must be left, so create it again
				},
				"<s:text name="btn.no"/>",
				function(){
				}
			);
		return false;
	});
	// CLEAR ALL BUTTON: reset input fields area
	
	// REMOVE BUTTON: remove selected rows of input fields
	function removeRowsOfInputFields() 
	{
		// delete the checked rows
		$("input[name='rowIndex']").each(function() {
			if ($(this).attr("checked"))
			{
				var i = $(this).attr("id");
				$("tr#tmpRow" + i).remove();
			}
		});
		
		// Re-arrange the row index of remained rows
		// alert("Size: " + $("input[name='rowIndex']").size() );
		$("#checkAllInputRows").removeAttr("checked");
		resetFieldIndex();
		$("input[name='rowIndex']").each(function() {
			var i = $(this).attr("id");
			
			$("tr#tmpRow" + i).attr("id", "tmpRow" + fieldIndex);
			
			$("#clothInfo_" + i + "_typeId")
				.attr("id", "clothInfo_" + fieldIndex + "_typeId")
				.attr("name", "clothList[" + fieldIndex + "].clothType.id");
			
			$("#clothInfo_" + i + "_size")
				.attr("id", "clothInfo_" + fieldIndex + "_size")
				.attr("name", "clothList[" + fieldIndex + "].size");
			
			$("#clothInfo_" + i + "_code")
				.attr("id", "clothInfo_" + fieldIndex + "_code")
				.attr("name", "clothList[" + fieldIndex + "].code");
			
			$("#clothInfo_" + i + "_rfid")
				.attr("id", "clothInfo_" + fieldIndex + "_rfid")
				.attr("name", "clothList[" + fieldIndex + "].rfid");
			
			$("#clothInfo_" + i + "_status")
				.attr("id", "clothInfo_" + fieldIndex + "_status")
				.attr("name", "clothList[" + fieldIndex + "].clothStatus");
			
			$("#clothInfo_" + i + "_remark")
				.attr("id", "clothInfo_" + fieldIndex + "_remark")
				.attr("name", "clothList[" + fieldIndex + "].remark")
			
			$(this).attr("id", fieldIndex);
			
			fieldIndex++;
		});
		
		var n = $("input[name='rowIndex']").length;
		if (n == 0)
		{
			resetFieldIndex();
			appendOneRowOfInputFields();
		}
		return false;
	}
	
	$("#removeRowsOfInputFields").on('click', function(){
		
		var n = $("input[name='rowIndex']:checked").length;
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
			function(){
				removeRowsOfInputFields();
			},
			"<s:text name="btn.no"/>",
			function(){
			}
		);
		return false;
	});
	// REMOVE BUTTON: remove selected rows of input fields
	
	
	
	
	//////////////////////////////////////////////////////////////////////
	// Insert one row of input fields
	//////////////////////////////////////////////////////////////////////
	function appendOneRowOfInputFields()
	{
		var temp = $("<tr/>");
		temp.attr("id", "tmpRow" + fieldIndex );
		
		$("#tableForAddRow").append(

			temp.append(
				$("<td/>").append(
						$("<input/>")
						.attr("type", "checkbox")
						.attr("name", "rowIndex")
						.attr("id", fieldIndex)
						.attr("class", "checkall2")
						.attr("checkall", "checkAllInputRows")
				)
			).append(
				$("<td/>").append(
					$("<select/>")
						.attr("id", "clothInfo_" + fieldIndex + "_typeId")
						.attr("name", "clothList[" + fieldIndex + "].clothType.id")
						.attr("class", "tblUniformType")
						<s:iterator value="clothTypeList">
							.append($("<option/>")
								.attr("value", "<s:property value="id"/>")
								.text("<s:property escape="false" value="name"/>")
							)
						</s:iterator>
				)
			).append(
					$("<td/>").append(
						$("<select/>")
							.attr("id", "clothInfo_" + fieldIndex + "_size")
							.attr("name", "clothList[" + fieldIndex + "].size")
							.attr("class", "tblUniformSize")
							<s:iterator value="clothSizeList">
								.append($("<option/>")
										.attr("value", "<s:property />")
										.text("<s:property escape="false" />")
								)
							</s:iterator>
					)
			).append(
				$("<td/>").append(
					$("<input/>")
						.attr("type", "text")
						.attr("id", "clothInfo_" + fieldIndex + "_code")
						.attr("name", "clothList[" + fieldIndex + "].code")
						.attr("class", "tblUniformNo")
				)
			).append(
				$("<td/>").append(
					$("<input/>")
						.attr("type", "text")
						.attr("id", "clothInfo_" + fieldIndex + "_rfid")
						.attr("name", "clothList[" + fieldIndex + "].rfid")
						.attr("class", "tblRFID")
				)
			).append(
					$("<td/>").append(
						$("<select/>")
							.attr("id", "clothInfo_" + fieldIndex + "_status")
							.attr("name", "clothList[" + fieldIndex + "].clothStatus")
							// .attr("disabled", "disabled")
							.attr("class", "displayText tblUniformStatus")
							<s:iterator value="clothStatusList">
								.append($("<option/>")
									.attr("value", "<s:property />")
									.text("<s:property escape="false" value="getText(value)"/>")
								)
							</s:iterator>
					)
			).append(
				$("<td/>").append(
					$("<input/>")
						.attr("type", "text")
						.attr("id", "clothInfo_" + fieldIndex + "_remark")
						.attr("name", "clothList[" + fieldIndex + "].remark")
						.attr("class", "tblRemark")
				)
			)
		);
		
		fieldIndex++;
	}
	
	
	// To server: Submit the current tmp user input rows
	$("#ajaxBtnAddToListTable").live(
			'click',
			function() {
				var elementId = $(this).attr("ref");			// ref: fieldset id
				var triggerTarget = $("#" + elementId);
				var div = $("#" + elementId + " :input");		// Kan: Wrong! more than required var are sent
				
				div.removeClass("fieldErrors");
				var url = $(this).attr("url");
				var data = div.serialize();
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
								
								removeAllRowsOfInputFields();	// clear ALL rows
								resetFieldIndex();
								appendOneRowOfInputFields();	// one row must be left, so create it again
							}
							
							if (result.actionMessages != undefined)
							{
								$.each(result.actionMessages, function(key, value) {
									addActionMessage(elementId, value);
								});
								
								$.each(result.actionMessages, function(key, value) {
									showActionMessage(elementId);
									return false;
								});
							}
							if (result.actionErrors != undefined) {
								$.each(result.actionErrors, function(key, value) {
									addActionError(elementId, value);
								});
								
								$.each(result.actionErrors, function(key, value) {
									showActionError(elementId, value);
									return false;
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
				return false;
			});
	// To server: Submit the current tmp user input rows
	
	
	
	
	// Cloth List Table: Display Added Pattern
	$("#newClothFieldset").on(
			"newClothFieldset.success",
			function(e, result, result2) {

				$("#clothListTable tbody").text("");

				// Newly added clothes
				// $.each(result.d, function(key, value) {
				$.each(result.clothListAdded, function(key, value) {
					
					var temp = $("<tr/>");
					temp.attr("id", "pattern" + key );
					temp.append(
						$("<td/>").append(
							$("<input/>")
								.attr("type", "checkbox")
								.attr("name", "XXXXXXXXXXXXXXX111")
								.attr("disabled", "disabled")
								.attr("checkall", "XXXXXXXXXXXXXXX222")
								// .attr("checkall", "checkAllDataRows")
								.addClass("checkall2")								
						)
					)
					.append(
						$("<td>").text(value.clothType.name)
					)
					.append(
						$("<td/>").text(value.size)
					)
					.append(
						$("<td/>").text(value.code)
					)
					.append(
						$("<td/>").text(value.rfid)
					)
					.append(
						$("<td/>").text(value.displayField )
					)
					.append(
						$("<td/>").text(value.remark)
					);

					$("#clothListTable tbody").append(temp);
					
					
				});
				
				
				
				// clear ALL rows
				removeAllRowsOfInputFields();
				resetFieldIndex();
				appendOneRowOfInputFields();	// one row must be left, so create it again
	});
	// Cloth List Table: Display Added Pattern

	
	//////////////////////////////////////////////////
	// Go to new or list page when save success
	//////////////////////////////////////////////////
	<s:url var="getNewPageUrl" namespace="/master" action="staff" method="getNewPage"/>
	<s:url var="getListPageUrl" namespace="/master" action="staff" method="getListPage"/>
	
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