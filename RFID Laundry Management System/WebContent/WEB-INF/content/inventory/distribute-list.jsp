<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.inventory.distribute" /></h3>

<div class="body">
<s:form theme="simple" id="searchForm" namespace="/general" action="inventory" method="post" cssClass="ajaxSearchForm" listtable="resultTable">
	<fieldset id="searchFieldset" name="searchFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.search.criteria" /></legend>
		
		<ul>
			<li>
				<label for="deptSelectionBox"><s:text name="staff.dept"/>: </label>
				<s:select
					theme="simple"
					id="deptSelectionBox"
					name="staff.dept.id"
					list="deptList"
					listKey="id"
					listValue="nameCht"
					disabled="false"
					emptyOption="true"
					cssClass="inputText" />
			</li>
			<li>
				<label for="staff.code"><s:text name="staff.code" />: </label>
				<s:textfield theme="simple" name="staff.code" cssClass="inputText" />
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
				<label for="clothType.id"><s:text name="cloth.type"/>:</label>
				<s:select
					theme="simple"
					name="clothType.id" 
					list="clothTypeList"
					listKey="id"
					listValue="name"
					emptyOption="true"
					cssClass="inputText"
				 />
			</li>
			
			<li>
				<label for="clothStatus"><s:text name="cloth.status"/>:</label>
				<s:select
					theme="simple"
					name="cloth.clothStatus" 
					list="clothStatusList"
					listValue="getText(value)"
					emptyOption="false"
					cssClass="inputText"
				 />
			</li>
			
			<li>
				<label for="cloth.code"><s:text name="cloth.code"/>: </label>
				<s:textfield theme="simple" name="cloth.code" readonly="false" cssClass="inputText" />
			</li>
			<li>
				<label for="zone.id"><s:text name="cloth.location"/>:</label>
				<s:select
					theme="simple"
					name="zone.id" 
					list="zoneList"
					listKey="id"
					listValue="code"
					emptyOption="true"
					cssClass="inputText"
				 />
			</li>
			
			<li>
				<label for="dateFrom"><s:text name="label.from.custom"><s:param><s:text name="label.process.date"/></s:param></s:text>: </label>
				<s:textfield theme="simple" name="dateFrom" cssClass="inputText dateTimePicker"></s:textfield>
			</li>
			<li>
				<label for="dateTo"><s:text name="label.to" />: </label>
				<s:textfield theme="simple" name="dateTo" cssClass="inputText dateTimePicker"></s:textfield>
			</li>
			
			<li>
				<label for="cloth.rfid"><s:text name="label.rfid"/>: </label>
				<s:textfield theme="simple" name="cloth.rfid" readonly="false" cssClass="inputText" />
			</li>
			<li>
			</li>
			
			
		</ul>
	</fieldset>

	<div class="buttonArea">
		<s:submit theme="simple" id="searchButton" key="btn.search" method="getSearchResultPage" cssClass="button blue buttonMargin ajaxSearchButton"></s:submit>
		<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="button rosy buttonMargin"/>
	</div>
</s:form>


<fieldset id="resultFieldset" name="resultFieldset" class="fieldsetStyle01">
	<legend><s:text name="fieldset.legend.search.result" /></legend>
	
	<div id="resultTable" class="tableOfInfo1">
		<img alt="loading" class="indicator" src="<s:property value="imagesPath"/>layout/ajax-load.gif" />
		<!-- will auto load from ajax -->
	</div>
</fieldset>

<div class="actionErrors" ref="searchForm"></div>
<div class="actionMessages" ref="searchForm"></div>
<div class="buttonArea">
	<label for="staffCode"><s:text name="staff.distribute.code" />: </label>
	<s:textfield theme="simple" id="staffCode" name="staff.code" cssClass="inputText"></s:textfield>

	<label for="dateDistribute"><s:text name="label.distribute.date"></s:text>: </label>
	<s:textfield theme="simple" id="dateDistribute" name="dateDistribute" cssClass="inputText dateTimePicker"></s:textfield>

	<!-- This hidden search button is required to use the Ajax-Form-Submit function -->
	<s:submit theme="simple" id="btnDistribute" key="btn.distribute" cssClass="button blue buttonMargin"></s:submit>
</div>

</div>

<%-- Dialogs --%>
<div class="hidden" id="infoDialog" title="<s:text name="cloth.info"/>"></div>

<script>

var popUpBox = undefined;

//////////////////////////////////////////////////////////////////////
//dom ready (HTML Document Load Complete)
//////////////////////////////////////////////////////////////////////
$(function(){
	
	openInfoForm();
	
	closeInfoForm();
	
	$("#searchButton").trigger("click");
});

function openInfoForm() {

	<s:url var="distributeCloth" namespace="/general" action="cloth-distribute" method="distribute"/>

	$("#btnDistribute").on("click", function(){
		
		var n = $(".selectedClothMarker:checked").length;
		
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
			var clothId = $(".selectedClothMarker:checked:first").val();
			var distributeDate = $('#dateDistribute').val();
			var staffCode = $('#staffCode').val();
			
			if (distributeDate == "") {
				alertDialog(
						"",
						"<s:text name="dialog.errors.distribute.required"/>",
						"<s:text name="btn.ok"/>");
				return false;
			}
			
			if (staffCode == ""){
				alertDialog(
						"",
						"<s:text name="errors.staff.code.required"/>",
						"<s:text name="btn.ok"/>");
				return false;
			}
			
			var data = {
				'staff.code': staffCode,
				'cloth.id': clothId,
				'distributeDate': distributeDate
			};
			

			var formId = 'searchForm';
			$.post('<s:property value="#distributeCloth"/>', data, function(result){
				
				if (result.errors != undefined)
				{
					$.each(result.errors, function(key, value) {
						addActionError(formId, value);
					});
					
					$.each(result.errors, function(key, value) {
						showActionError(formId);
						return false;
					});
				}
				if (result.actionMessages != undefined)
				{
					$.each(result.actionMessages, function(key, value) {
						addActionMessage(formId, value);
					});
					
					$.each(result.actionMessages, function(key, value) {
						showActionMessage(formId);
						return false;
					});
				}
				if (result.actionErrors != undefined)
				{
					$.each(result.actionErrors, function(key, value) {
						addActionError(formId, value);
					});
					
					$.each(result.actionErrors, function(key, value) {
						showActionError(formId);
						return false;
					});
				}
				if ( result == undefined || ( result.errors == undefined && (result.actionErrors == undefined || result.actionErrors.length==0) && (result.fieldErrors == undefined || getPropertyCount(result.fieldErrors)==0) ) )
				{
					removeActionError(formId);
					$("#searchButton").trigger("click");
				}
				
			}, "json");
			
			return false;
		}
	});
}

function closeInfoForm() {

	//Edit Form handler
	$("#infoForm").live("infoForm.success", function(){
	
// 		if( popUpBox != undefined )
//  			popUpBox.dialog( "close" );
		$("#btnView").trigger('click');
	});
}

</script>