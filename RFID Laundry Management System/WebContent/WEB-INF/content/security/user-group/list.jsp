<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<script>

	$(function() {
		/* Horace: DO NOT remove the following lines 
		$( "#popUpBox1" ).live( "dialogclose", function() {
			// $(".tableOfInfo1").load( "<s:property value='#searchUrl1' />" );
			
			$(".tableOfInfo1").fadeOut('slow').load("<s:property value='#searchUrl1' />").fadeIn("slow");
		});
		*/

		$("#groupsSearchForm").on("reset", function(){
		});
		
		$("#groupsUpdateButton").on("click", function(){
			var n = $("input[name='groups.id']:checked").length;
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
		$("#groupsSearchButton").trigger("click");
	});

</script>

<h3 class="fnName"><s:text name="security.groups.page.title.list" /></h3>

<div class="body">
<s:form theme="simple" id="groupsSearchForm" action="user-group-management" listtable="listTable" namespace="/system" cssClass="ajaxSearchForm">

	<fieldset class="fieldsetStyle01" id="returnFieldset" name="returnFieldset">
	
		<legend>
			<s:text name="fieldset.legend.search.criteria"></s:text>
		</legend>

		<ul>

			<li>
				<label for="groups.groupName" class="width180"><s:text name="security.groups.name"></s:text>: </label>
				<s:textfield theme="simple" name="groups.groupName" cssClass="fieldStyle1" />
			</li>

			<li>
				<label for="groups.enabledString" class="width180"><s:text name="security.groups.status"></s:text>: </label>
				<s:select
					theme="simple"
					name="groups.enabledString"
					list="enableDisableStatusList"
					listKey="value"
					listValue="getText(resKey)"
					emptyOption="true"
					cssClass="inputText" />
			</li>			

		</ul>
	</fieldset>

	<div class="buttonArea">
		<s:submit theme="simple" id="groupsSearchButton" method="groupsSearch" key="btn.search" cssClass="button blue buttonMargin ajaxSearchButton"/>
		<s:reset theme="simple" name="reset" id="btnReset"  key="btn.reset" cssClass="button rosy buttonMargin"  />
	</div>

</s:form>

<s:form theme="simple" name="listForm" action="user-group-management" namespace="/system" method="GET"><!-- to make the edit page can refresh -->

	<fieldset class="fieldsetStyle01" id="resultFieldset" name="resultFieldset">
	
		<legend><s:text name="fieldset.legend.search.result"></s:text></legend>
		
		<div id="listTable" class="tableOfInfo1">
			<img alt="loading" class="indicator"
						src="<%=request.getContextPath()%>/images/layout/ajax-load.gif" />
			<!-- will auto load from ajax -->
		</div>
		
	</fieldset>

	<div class="buttonArea">
		<s:submit type="button" theme="simple" id="groupsUpdateButton" key="btn.update" method="groupsUpdate" cssClass="button blue buttonMargin"></s:submit>
	</div>

</s:form>
</div>
