<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.special.event.view" /></h3>

<div class="body">
<s:form theme="simple" id="searchForm" namespace="/general" action="special-event" method="post" cssClass="ajaxSearchForm" listtable="resultTable">
	<fieldset id="searchFieldset" name="searchFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.search.criteria" /></legend>
		
		<ul>
			<li>
				<label for="eventNameSelectionbox"><s:text name="special.event.special.event"/>: </label>
				<s:select
					theme="simple"
					id="eventNameSelectionbox"
					name="specialEvent.specialEventName" 
					list="specialEventNameList"
					listValue="getText(value)"
					emptyOption="true"
					cssClass="inputText"
				 />
			</li>
			<li>
				<label for="eventStatusSelectionbox"><s:text name="special.event.status"/>: </label>
				<s:select
					theme="simple"
					id="eventStatusSelectionbox"
					name="specialEvent.specialEventStatus" 
					list="specialEventStatusList"
					listValue="getText(value)"
					emptyOption="true"
					cssClass="inputText"
				 />
			</li>
			
			<li>
				<label for="dateFrom"><s:text name="label.from.custom"><s:param><s:text name="label.process.date"/></s:param></s:text>:</label>
				<s:textfield theme="simple" name="dateFrom" cssClass="inputText dateTimePicker"></s:textfield>
			</li>
			<li>
				<label for="dateTo"><s:text name="label.to" />:</label>
				<s:textfield theme="simple" name="dateTo" cssClass="inputText dateTimePicker"></s:textfield>
			</li>
			
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
				<label for="cloth.clothType.id"><s:text name="cloth.type"/>:</label>
				<s:select
					theme="simple"
					name="cloth.clothType.id" 
					list="clothTypeList"
					listKey="id"
					listValue="name"
					emptyOption="true"
					cssClass="inputText"
				 />
			</li>
			<li>
				<label for="cloth.code"><s:text name="cloth.code"/>: </label>
				<s:textfield theme="simple" name="cloth.code" readonly="false" cssClass="inputText" />
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
	<s:submit theme="simple" id="btnEdit" method="getEditPage" key="btn.edit" cssClass="button blue buttonMargin"></s:submit>
</div>

</div>


<%-- Popup Box for Edit --%>
<%-- <div class="hidden" id="editDialog" title="<s:text name="label.update"/>"></div> --%>


<script>

// var popUpBox = undefined;

$(function(){
	$("#searchButton").trigger("click");
	
	/////////////////////////////////////////////
	// Click "View" Button
	/////////////////////////////////////////////
	<s:url var="getEditPageUrl" namespace="/general" action="special-event" method="getEditPage"/>
	$("#btnEdit").on("click", function(){
		
		var n = $(".selectedSpecialEventMarker:checked").length;
		
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
			var eventId = $(".selectedSpecialEventMarker:checked:first").val();
			var url = "<s:property value="#getEditPageUrl"/>";
			url += "?specialEvent.id=" + eventId;
			$(location).attr('href', url);

			
// 			popUpBox = $("#editDialog").load(url, "specialEvent.id=" + eventId).dialog({
// 					modal: true,
// 					width: 1100,
// 					height: 350,
// 					close: function() {
// 						ajaxIgnoreClick = true; // prevent the action message keep show until user click
// 						$("#searchButton").trigger("click");
// 					}
// 			});
// 			return false;
		}
	});
	
// 	$("#editForm").live("editForm.success", function(){
//  		if( popUpBox != undefined )
//  			popUpBox.dialog( "close" );
// 	});
});

</script>