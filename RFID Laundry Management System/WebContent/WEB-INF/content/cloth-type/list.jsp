<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.clothType.list" /></h3>

<div class="body">
	<s:form theme="simple" namespace="/master" action="cloth-type" method="post" cssClass="ajaxSearchForm" listtable="resultTable">
	
		<fieldset id="searchFieldset" name="searchFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.search.criteria" /></legend>
			<ul>
				<li>
					<label for="clothType.name"><s:text name="clothType.name"/>:</label>
					<s:textfield theme="simple" name="clothType.name" cssClass="inputText"/>
				</li>
				
				<li>
					<label for="clothType.description"><s:text name="label.description"/>:</label>
					<s:textfield theme="simple" name="clothType.description"/>
				</li>
			</ul>
		</fieldset>
	
		<div class="buttonArea">
			<s:submit theme="simple" id="searchButton" key="btn.search" method="getSearchResultPage" cssClass="button blue buttonMargin ajaxSearchButton"></s:submit>
			<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="button rosy buttonMargin"/>
		</div>
	</s:form>

	<s:form theme="simple" namespace="/master" action="cloth-type" method="post">
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

<%-- Dialogs --%>
<div class="hidden" id="editDialog" title="<s:text name="label.update"/>"></div>

<%-- URLS --%>
<s:url var="getEditPageUrl" namespace="/master" action="cloth-type" method="getEditPage"/>

<script>

var popUpBox = undefined;

//////////////////////////////////////////////////////////////////////
//dom ready (HTML Document Load Complete)
//////////////////////////////////////////////////////////////////////
$(function(){

	openEditForm();

	closeEditForm();
	
	$("#searchButton").trigger("click");
});

function openEditForm() {

	$("#btnEdit").on("click", function(){
		var n = $("input[name='clothType.id']:checked").length;

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
	
		var id = $("input[name='clothType.id']:checked").val();
		popUpBox = $("#editDialog").load(
						"<s:property value="#getEditPageUrl"/>",
						"clothType.id=" + id).dialog({
				modal: true,
				
				width: 800,
				height: 600,

				close: function() {
					ajaxIgnoreClick = true; // prevent the action message keep show until user click
					$("#searchButton").trigger("click");
				}
		}).css("font-size", "15px");

		return false;
	});
}

function closeEditForm() {
	
	//Edit Form handler
	$("#editForm").live("editForm.success", function(){

 		if( popUpBox != undefined )
 			popUpBox.dialog( "close" );
	});
}

</script>