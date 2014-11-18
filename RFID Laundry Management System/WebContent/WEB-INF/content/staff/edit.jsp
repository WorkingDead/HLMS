<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.staff.edit" /></h3>

<div class="body">
<s:form theme="simple" id="editForm" namespace="/master" action="staff" method="post" cssClass="ajaxForm">
	
	<s:hidden theme="simple" id="hiddenStaffId" name="staff.id" />
	
	<fieldset id="generalInfoFieldset" name="generalInfoFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.generalinfo"/></legend>
		
		<!-- This div's ref must set to form id -->
		<div class="actionErrors" ref="editForm"></div>
		<div class="actionMessages" ref="editForm"></div>
		
		<ul>
			<li>
				<label for="staff.code"><s:text name="staff.code"/>: </label>
				<s:textfield theme="simple" name="staff.code" readonly="true" cssClass="displayText" />
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
				
				<s:if test="staff.staffStatus.toString() == 'Leave'">
					<s:textfield theme="simple" name="dummyStaffStatus" readonly="true" cssClass="displayText">
						<s:param name="value">
							<s:property escape="false" value="%{getText(staff.staffStatus.value)}" />
						</s:param>
					</s:textfield>
					<s:hidden theme="simple" name="staff.staffStatus" />
				</s:if>
				<s:else>
					<s:select
						theme="simple"
						id="staffStatusSelectionBox"
						name="staff.staffStatus"
						list="staffStatusList"
						listValue="getText(value)"
						disabled="false"
						cssClass="inputText" />
				</s:else>
			</li>
			
			<li>
				<label for="staff.cardNumber"><s:text name="staff.card"/>: </label>
				<s:textfield theme="simple" name="staff.cardNumber" cssClass="inputText" />
			</li>
			<li>
				<label for="staff.enable"><s:text name="label.enable"/>: </label>
				<s:checkbox theme="simple" name="staff.enable" cssClass="simpleCheckBox"/>
			</li>
		</ul>
	</fieldset>
	
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
							ref="checkAllInputRows" />
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
				<%-- Dynamic Generation --%>
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


	<fieldset id="clothListFieldset" name="clothListFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.cloth.list"/></legend>
		
		<div class="alignRight">
			
			<s:submit theme="simple" 
				type="button" 
				id="btnEditCloth" 
				key="btn.edit" 
				cssClass="button gray"
				/>
		
			<s:url var="voidClothUrl" action="staff" method="voidCloth"></s:url>
			<s:submit theme="simple" 
				type="button" 
				id="ajaxBtnVoidCloth"
				key="btn.void" 
				cssClass="button rosy" 
				ref="clothListFieldset"
				url="#voidClothUrl" />
		</div>
		
		<div class="actionErrors" ref="clothListFieldset"></div>
		<div class="actionMessages" ref="clothListFieldset"></div>
		
		<!-- Cloth List Table-->
		<table id="clothListTable" class="dataTable">
			<thead>
				<tr>
					<th class="simpleCheckBox">
						<s:checkbox theme="simple" 
							id="checkAllDataRows" 
							name="checkAllDataRows" 
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
			
				<!-- ##################### -->
				<!-- dynamic generate area -->
				<!-- ##################### -->
			
				<s:if test="clothListInDb == null || clothListInDb.size == 0">
					<tr>
						<td colspan="7"><s:text name="list.nothing.found"/></td>
					</tr>
				</s:if>
				<s:iterator value="clothListInDb" status="rowStatus">
					<tr>
						<td>
							<%-- 
							<s:checkbox theme="simple" 
								id="selected_cloth_id_%{id}"
								name="voidClothIdList[%{#rowStatus.index}]" 
								fieldValue="%{id}"
								cssClass="simpleCheckBox checkall2 clothMaybeVoidMarker" 
								checkall="checkAllDataRows" 
								/>
							--%>
							
							<input type="checkbox" 
								id="<s:property value="#rowStatus.index" />" 
								name="cb_cloth_id_<s:property value="#rowStatus.index" />" 
								class="simpleCheckBox checkall2 clothMaybeVoidMarker" 
								checkall="checkAllDataRows" />
							<input type="hidden" 
								id="hidden_cloth_id_<s:property value="#rowStatus.index" />"
								name="hidden_cloth_id_<s:property value="#rowStatus.index" />" 
								value="<s:property value="id" />" />
						</td>
						
						<td><s:property value="clothType.name"/></td>
						<td><s:property value="size"/></td>
						<td><s:property value="code"/></td>
						<td><s:property value="rfid"/></td>
						<td><s:property value="%{getText(clothStatus.value)}"/></td>
						<td><s:property value="remark"/></td>
					</tr>
				</s:iterator>
			
				<!-- ##################### -->
				<!-- dynamic generate area -->
				<!-- ##################### -->
				
			</tbody>
			
			<tfoot></tfoot>
		</table>
	</fieldset>
	
	<div class="buttonArea">
		<s:submit theme="simple" type="button" key="btn.save" method="update" cssClass="button blue buttonMargin ajaxButton" />
		<s:submit theme="simple" type="button" key="btn.cancel" id="cancelButton" cssClass="button rosy buttonMargin" />
	</div>
</s:form>
</div>


<%-- Popup Box --%>
<div class="hidden" id="editPopupBox" title="<s:text name="label.update"/>"></div>


<script>
//////////////////////////////////////////////////////////////////////
// For generating the input field
//////////////////////////////////////////////////////////////////////
var fieldIndex = 0;

function resetFieldIndex()
{
	fieldIndex = 0;
}
//////////////////////////////////////////////////////////////////////
// For generating the input field
//////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////
// DOM ready (HTML Document Load Complete)
//////////////////////////////////////////////////////////////////////
$(function() {
	
	resetFieldIndex();
	appendOneRowOfInputFields();
	
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
	
	
	// To server 1: Submit the current tmp user input rows
	$("#ajaxBtnAddToListTable").live(
			'click',
			function() {
				var elementId = $(this).attr("ref");
				var triggerTarget = $("#" + elementId);
				var div = $("#" + elementId + " :input");	// Kan: more then need are submit
				
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
	
	
	// To server 3: VOID selected clothes
	$("#ajaxBtnVoidCloth").live('click', 
			
			function() {
				// check that at least one row must be selected 
				var n = $(".clothMaybeVoidMarker:checked").length;
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
						// only checked-checkbox row will submit
						var j = 0;
						$(".clothMaybeVoidMarker").each(function() {
							if ($(this).attr("checked"))
							{
								var curRowIndex = $(this).attr("id");
								$(this).attr("disabled", "disabled");
								$("#hidden_cloth_id_" + curRowIndex).attr("name", "voidClothIdList[" + j + "]");
								j++;
							}
							else
							{
								var curRowIndex = $(this).attr("id");
								$(this).attr("disabled", "disabled");
								$("#hidden_cloth_id_" + curRowIndex).attr("disabled", "disabled");
							}
						});
						
						var ajaxBtnVoidClothVar = $("#ajaxBtnVoidCloth");
						var elementId = ajaxBtnVoidClothVar.attr("ref");		// ref: fieldset id
						var triggerTarget = $("#" + elementId);
						var div = $("#" + elementId + " :input");
						
						div.removeClass("fieldErrors");
						var url = ajaxBtnVoidClothVar.attr("url");				// url: the method of ActionClass to be called
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
						return false;
						
					},
					
					"<s:text name="btn.no"/>",
					function()
					{
					}
				);
				
				return false;
			});
	// To server: VOID selected clothes
	
	
	function refreshClothListTableByJsonResult(e, result, result2)
	{
		$("#checkAllDataRows").attr("checked", false);
		$("#clothListTable tbody").text("");
		
		///////////////////////////////////////////////////////////////////////
		// Cloth in DB
		///////////////////////////////////////////////////////////////////////
		// $.each(result.d, function(key, value) {
		var i = 0;
		$.each(result.clothListInDb, function(key, value) {
			var temp = $("<tr/>");
			temp.attr("id", "tr_db_pattern_" + key );
			// temp.attr("class", "tableRowLightBlue");
			temp.append(
				$("<td/>").append(
					$("<input/>")
						.attr("id", key)
						.attr("type", "checkbox")
						.attr("name", "cb_cloth_id_" + key)
						.attr("checkall", "checkAllDataRows")
						.addClass("simpleCheckBox checkall2 clothMaybeVoidMarker")
				).append(
					$("<input/>")
						.attr("id", "hidden_cloth_id_" + key)
						.attr("type", "hidden")
						.attr("name", "hidden_cloth_id_" + key)
						.val("" + value.id)
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
				$("<td/>").text(value.displayField)
			)
			.append(
				$("<td/>").text(value.remark)
			);

			$("#clothListTable tbody").append(temp);
			
			i++;
		});
		
		///////////////////////////////////////////////////////////////////////
		// Newly added clothes (NOT ALLOW to VOID this newly added clothes)
		///////////////////////////////////////////////////////////////////////
		$.each(result.clothListAdded, function(key, value) {
			
			var temp = $("<tr/>");
			temp.attr("id", "tr_new_pattern_" + key );
			temp.attr("class", "tableRowLightBlue" );
			temp.append(
				$("<td/>").append(
					$("<input/>")
						.attr("id", "XXXXX111_"+key)
						.attr("type", "checkbox")
						.attr("name", "XXXXX222_"+key)
						.attr("value", "XXXXX333_"+key)
						.attr("disabled", "disabled")
						.attr("checkall", "XXXXX444_"+key)
						.addClass("simpleCheckBox")
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
				$("<td/>").text( value.displayField )
			)
			.append(
				$("<td/>").text(value.remark)
			);

			$("#clothListTable tbody").append(temp);
		});
	}
	
	
	// Cloth List Table: Display Added Pattern
	$("#newClothFieldset").on(
			"newClothFieldset.success",
			function(e, result, result2) {
				
				refreshClothListTableByJsonResult(e, result, result2);
				
				// clear ALL rows
				removeAllRowsOfInputFields();
				resetFieldIndex();
				appendOneRowOfInputFields();	// one row must be left, so create it again
	});
	// Cloth List Table: Display Added Pattern
	
	
	// Cloth List Table: Refresh after void some clothes
	$("#clothListFieldset").on(
			"clothListFieldset.success",
			function(e, result, result2) {
				refreshClothListTableByJsonResult(e, result, result2);
	});
	// Cloth List Table: Refresh after void some clothes
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////
	// Edit a cloth
	///////////////////////////////////////////////////////////////////
	
	// To server 2: Edit a cloth
	<s:url var="getEditClothPageUrl" namespace="/master" action="staff" method="getEditClothPage"></s:url>
	<%--
	<s:url var="getEditStaffPageUrl" namespace="/master" action="staff" method="getEditPage"/>
	--%>
	var editRow;  
	$("#btnEditCloth").on("click", function(){
		
		// check that at least one row must be selected 
		var n = $(".clothMaybeVoidMarker:checked").length;
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
		
		var staffId = $("#hiddenStaffId").val();
		var editClothId = -1;
		$(".clothMaybeVoidMarker:checked").each(function() {
			
			editRow = $(this).parents("tr");	// get this row
			// $(this).closest('tr')[0];
			
			var curRowIndex = $(this).attr("id");
			// $(this).attr("disabled", "disabled");
			editClothId = $("#hidden_cloth_id_" + curRowIndex).val();
		});
		
		// alert("staffId: " + staffId + "\n\neditClothId: " + editClothId);
		
		var popupUrl = "<s:property value="#getEditClothPageUrl"/>";
		popupUrl += "?cloth.id=" + editClothId;
		popupUrl += "&staff.id=" + staffId;
		popUpBox = $("#editPopupBox").load( popupUrl ).dialog({
					
						modal: true,
						width: 550,
						height: 350,
						
						buttons: {
							
							"save" : {
								text: "<s:text name="btn.save"/>",
								click: function(){
									$("#btnSave").trigger("click");
								}
							},
							
							"close" :{
								text: "<s:text name="btn.close"/>",
								click: function(){
									$(this).dialog("close");
									$(this).dialog("destroy");
								}
							}
						},

						
						close: function()
						{
							ajaxIgnoreClick = true; // prevent the action message keep show until user click
							return false;
						}
		}).css("font-size", "15px");;

		return false;
	});
	// To server 2: Edit a cloth
	
	// Close the popup box after delete cloth from container
	$("#editClothForm").live("editClothForm.success", function(){

 		if( popUpBox != undefined )
 		{
 			var clothTypeNameNew = $("#popupClothType option::selected").text();
 			var clothSizeNew = $("#popupClothSize").val();
 			var clothCodeNew = $("#popupClothCode").val();
 			var clothRfidNew = $("#popupClothRfid").val();
 			var clothRemarkNew = $("#popupClothRemark").val();
 			
 			editRow.children('td:eq(1)').text(clothTypeNameNew);
 			editRow.children('td:eq(2)').text(clothSizeNew);
 			editRow.children('td:eq(3)').text(clothCodeNew);
 			editRow.children('td:eq(4)').text(clothRfidNew);
			editRow.children('td:eq(6)').text(clothRemarkNew);
			
 			popUpBox.dialog( "close" );
 		}
	});
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////////////
	// Go to list page if save success
	//////////////////////////////////////////////////
	<s:url var="getListPageUrl" namespace="/master" action="staff" method="getListPage"/>
	
	$("body").delegate("#editForm", "editForm.success", function(e, result, result2) {
		
		msgDialog("", result.actionMessages, "<s:text name="btn.ok"/>",
				
				function()
				{
					$(location).attr('href',"<s:property value="#getListPageUrl"/>");
				}
			);
	});
	
	// Press 'Cancel' button will direct to the List Page
	<s:url var="backToListUrl" namespace="/master" action="staff"  method="getListPage"/>
	$("#cancelButton").on('click', function(){
		var url = "<s:property value="#backToListUrl"/>";
		$(location).attr('href', url);
		
		return false;
	});
});
		
</script>