<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.staff.list" /></h3>

<div class="body">
<s:form theme="simple" id="searchForm" namespace="/master" action="staff" method="post" cssClass="ajaxSearchForm" listtable="resultTable">
	
	<fieldset id="searchFieldset" name="searchFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.search.criteria"/></legend>
		
		<ul>
			<li>
				<label for="staff.code"><s:text name="staff.code" />: </label>
				<s:textfield theme="simple" name="staff.code" cssClass="inputText" />
			</li>
			
			<li>
				<label for="staffPositionSelectionBox"><s:text name="staff.position"/>: </label>
				<s:select
					theme="simple"
					id="staffPositionSelectionBox"
					name="staff.position"
					list="positionList"
					emptyOption="true"
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
						emptyOption="true"
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
						emptyOption="true"
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
					emptyOption="true"
					cssClass="inputText" />
			</li>
			
			<li>
				<label for="staff.cardNumber"><s:text name="staff.card"/>: </label>
				<s:textfield theme="simple" name="staff.cardNumber" cssClass="inputText"/>
			</li>
			
			<li>
				<label for="staff.enable"><s:text name="label.enable"/>: </label>
<%-- 				<s:checkbox theme="simple" name="staff.enable" value="true" fieldValue="true" cssClass="simpleCheckBox"/> --%>
				<s:select
					theme="simple"
					id="staffEnableSelectionBox"
					name="enableStatus"
					list="enableStatusList"
					listKey="getText(value)"
					listValue="getText(resKey)"
					emptyOption="true"
					disabled="false"
					cssClass="inputText" />
			</li>
		</ul>
	</fieldset>
	
	<div class="buttonArea">
		<s:submit theme="simple" id="searchButton" key="btn.search" method="getSearchResultPage" cssClass="button blue buttonMargin ajaxSearchButton"></s:submit>
		<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="button rosy buttonMargin"/>
	</div>
</s:form>



<s:form theme="simple" id="staffListForm" namespace="/master" action="staff" method="post">
	<fieldset id="resultFieldset" name="resultFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.search.result" /></legend>
		
		<!-- Retrieve Data Table -->
		<div id="resultTable" class="tableOfInfo1">
			<img alt="loading" class="indicator" src="<s:property value="imagesPath"/>layout/ajax-load.gif" />
			<!-- will auto load from ajax -->
		</div>
		<!-- End of Retrieve Data Table -->
	</fieldset>
	
	<div class="buttonArea">
		<s:submit theme="simple" id="btnEdit" method="getEditPage" key="btn.edit" cssClass="button blue buttonMargin"></s:submit>
	</div>
</s:form>

</div>


<script>
//////////////////////////////////////////////////////////////////////
// For selectedStaffArray (by Horace)
//////////////////////////////////////////////////////////////////////
var selectedStaffArrayIndex = 0;

function resetFieldIndex()
{
	selectedStaffArrayIndex = 0;
}
//////////////////////////////////////////////////////////////////////
// For selectedStaffArray (by Horace)
//////////////////////////////////////////////////////////////////////

$(function(){

	$("#searchButton").trigger("click");

	/////////////////////////////////////////////
	// Click "Edit" Button
	/////////////////////////////////////////////
	<s:url var="getEditPageUrl" namespace="/master" action="staff" method="getEditPage"/>
	$("#btnEdit").on("click", function(){
		// var n = $("input[name='staff.id']:checked").length;
		var n = $(".selectedStaffMarker:checked").length;
		// alert("number of staff selected: " + n);
		
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
			/*
			resetFieldIndex();
			
			$(".selectedStaffMarker:checked").each(function() {
				$(this).attr("name", "selectedStaffList[" + selectedStaffArrayIndex + "].id");
				selectedStaffArrayIndex++;
			});
			
			$("#staffListForm").submit();			// this is not by IE9 or Chrome
			*/
			
			var staffId = $(".selectedStaffMarker:checked:first").val();
			var url = "<s:property value="#getEditPageUrl"/>";
			// url += "?selectedStaffList[0].id=" + staffId;
			url += "?staff.id=" + staffId;
			
			$(location).attr('href', url);
			return false;
		}
	});
	
});

</script>