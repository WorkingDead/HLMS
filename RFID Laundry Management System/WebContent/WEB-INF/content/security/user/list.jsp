<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<s:url var="usersExportUrl" action="user-management" namespace="/system" method="usersExport"/>

<script>

	$(function() {
		/* Horace: DO NOT remove the following lines 
		$( "#popUpBox1" ).live( "dialogclose", function() {
			// $(".tableOfInfo1").load( "<s:property value='#searchUrl1' />" );
			
			$(".tableOfInfo1").fadeOut('slow').load("<s:property value='#searchUrl1' />").fadeIn("slow");
		});
		*/

		$("#usersExportButton").on("click", function(){

			var url = "<s:property value="#usersExportUrl"/>";
			url += "?users.username=" + $("#userNameId").val() + "&userForm.groups.id=" + $("#userGroupId").val() + "&users.enabledString=" + $("#enabledStringId").val();
			$(location).attr( 'href', url );
			
			return false;
		});
		
		
		$("#usersSearchForm").on("reset", function(){
		});
		
		$("#usersUpdateButton").on("click", function(){
			var n = $("input[name='users.username']:checked").length;
			if(n==0){
				alertDialog("", "<s:text name="dialog.errors.nothing.select"/>", "<s:text name="btn.ok"/>");
				return false;
			}
			if(n>1){
				alertDialog("", "<s:text name="dialog.errors.select.one.only"/>", "<s:text name="btn.ok"/>");
				return false;
			}
			return true;
		});
		
		//trigger auto search
		$("#usersSearchButton").trigger("click");
	});

</script>

<h3 class="fnName"><s:text name="security.users.page.title.list" /></h3>

<div class="body">
<s:form theme="simple" id="usersSearchForm" action="user-management" listtable="listTable" namespace="/system" cssClass="ajaxSearchForm">

	<fieldset class="fieldsetStyle01" name="returnFieldset" id="returnFieldset">
	
		<legend>
			<s:text name="fieldset.legend.search.criteria"></s:text>
		</legend>

		<ul>

			<li>
				<label for="users.username" class="width180"><s:text name="security.users.username"></s:text>: </label>
				<s:textfield theme="simple" name="users.username" id="userNameId" cssClass="inputText" />
			</li>

			<li>
				<label for="" class="width180"><s:text name="security.users.group"></s:text>: </label>
				<s:select
					id="userGroupId"
					theme="simple"
					name="userForm.groups.id"
					list="userGroupList"
					listKey="id"
					listValue="groupName"
					emptyOption="true"
					cssClass="inputText" />
			</li>

			<li>
				<label for="users.enabledString" class="width180"><s:text name="security.users.status"></s:text>: </label>
				<s:select
					id="enabledStringId"
					theme="simple"
					name="users.enabledString"
					list="enableDisableStatusList"
					listKey="value"
					listValue="getText(resKey)"
					emptyOption="true"
					cssClass="inputText" />
			</li>

		</ul>
	</fieldset>

	<div class="buttonArea">
		<s:submit theme="simple" id="usersSearchButton" method="usersSearch" key="btn.search" cssClass="button blue buttonMargin ajaxSearchButton"/>
		<s:reset theme="simple" name="reset" id="btnReset"  key="btn.reset" cssClass="button rosy buttonMargin"  />
	</div>

</s:form>

<s:form theme="simple" name="listForm" action="user-management" namespace="/system" method="GET"><!-- to make the edit page can refresh -->

	<fieldset class="fieldsetStyle01" name="resultFieldset" id="resultFieldset">
	
		<legend><s:text name="fieldset.legend.search.result"></s:text></legend>
		
		<div id="listTable" class="tableOfInfo1">
			<img alt="loading" class="indicator"
						src="<%=request.getContextPath()%>/images/layout/ajax-load.gif" />
			<!-- will auto load from ajax -->
		</div>
		
	</fieldset>

	<div class="buttonArea">
		<s:submit type="button" theme="simple" id="usersUpdateButton" key="btn.update" method="usersUpdate" cssClass="button blue buttonMargin"></s:submit>
<%-- 		<s:submit type="button" theme="simple" id="usersExportButton" cssClass="button gray buttonMargin"><s:property value="%{ getText('btn.export') }"/></s:submit> --%>
	</div>

</s:form>
</div>
