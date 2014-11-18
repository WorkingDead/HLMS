<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.inventory.view" /></h3>

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
					emptyOption="true"
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

<div class="buttonArea">
	<!-- This hidden search button is required to use the Ajax-Form-Submit function -->
	<s:submit theme="simple" id="btnView" key="btn.view" cssClass="button blue buttonMargin"></s:submit>
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

	<s:url var="getInventoryDetailPage" namespace="/master" action="cloth" method="getInventoryDetailPage"/>

	$("#btnView").on("click", function(){
		
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
			popUpBox = $("#infoDialog").load(
							"<s:property value="#getInventoryDetailPage"/>",
							"cloth.id=" + clothId).dialog({
					modal: true,
					
					width: 1100,
					height: 1024,
		
					buttons: {
						"close" :{
							text: "<s:text name="btn.close"/>",
							click: function(){
								$(this).dialog("close");
								$(this).dialog("destroy");
							}
						}
					},
		
					close: function() {
					}
			}).css("font-size", "15px");
			
			return false;
		}
	});
}

function closeInfoForm() {

	//Edit Form handler
	$("#infoForm").live("infoForm.success", function(){

 		if( popUpBox != undefined )
 			popUpBox.dialog( "close" );
	});
}

</script>