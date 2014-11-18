<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<%-- The URL use by going back to list define here --%>
<s:url var="backToListUrl" action="user-management" namespace="/system" method="usersList"/>

<%-- The URL use by going back to create define here --%>
<s:url var="backToCreateUrl" action="user-management" namespace="/system" method="usersCreate"/>

<script>

	$(function() {

		//When saving success, user can choose to go to where
		$("body").delegate("#usersConfirmForm", "usersConfirmForm.success", function(e, result, result2) {
			optionsAvailableMsgDialog("", result.actionMessages, 
					"<s:text name="btn.continue"/>",
					function(){
						$(location).attr('href',"<s:property value="#backToCreateUrl"/>");
					},
					"<s:text name="btn.ok"/>",
					function(){
						$(location).attr('href',"<s:property value="#backToListUrl"/>");
					}
				);
		});
		$("body").delegate("#usersConfirmForm", "usersConfirmForm.failure", function(e, result, result2) {
// 			alertDialog("", result.errors, "<s:text name="btn.ok"/>");
		});	
		//When saving success, user can choose to go to where
	})


</script>

<!-- Modal Window: Popup Box for editing Currency-->
<div id="popUpBox1" class="popUpBoxDiv hidden">
</div>

<h3 class="fnName"><s:text name="security.users.page.title.new" /></h3>

<div class="body">
<s:form theme="simple" id="usersConfirmForm" namespace="/system" action="user-management" method="post" cssClass="ajaxForm">

	<div class="actionErrors" ref="usersConfirmForm"></div>
	<div class="actionMessages" ref="usersConfirmForm"></div>

	<fieldset class="fieldsetStyle01" name="newUserFieldset" id="newUserFieldset">
		
		<legend>
			<s:text name="security.users.page.title.new"></s:text>
		</legend>

		<ul>

			<li>
				<label for="users.username" class="width180"><s:text name="security.users.username"></s:text>: </label>
				<s:textfield theme="simple" name="users.username" cssClass="inputText" />
			</li>
			
			<li>
				<label for="users.password" class="width180"><s:text name="security.users.password"></s:text>: </label>
				<s:password theme="simple" name="users.password" cssClass="inputText" />
			</li>
			
			<li>
				<label for="users.confirmPassword" class="width180"><s:text name="security.users.confirmPassword"></s:text>: </label>
				<s:password theme="simple" name="users.confirmPassword" cssClass="inputText" />
			</li>

			<li>
				<label for="userForm.groups.id" class="width180"><s:text name="security.users.group"></s:text>: </label>
				<s:select
					theme="simple"
					name="userForm.groups.id"
					list="userGroupList"
					listKey="id"
					listValue="groupName"
					cssClass="inputText" />
			</li>
			
			<li>
				<label for="users.lang" class="width180"><s:text name="security.users.lang"></s:text>: </label>
				<s:select
					theme="simple"
					name="users.lang"
					list="@web.actions.BaseAction$SystemLanguage@values()"
					listKey="name()"
					listValue="getText(systemResourceKey)"
					emptyOption="false"
					cssClass="inputText" />
			</li>

			<li>
				<label for="users.enabledString" class="width180"><s:text name="security.users.status"></s:text>: </label>
				<s:select
					theme="simple"
					name="users.enabledString"
					list="enableDisableStatusList"
					listKey="value"
					listValue="getText(resKey)"
					emptyOption="false"
					cssClass="inputText" />
			</li>

		</ul>
	</fieldset>

	<div class="buttonArea">
		<!-- the button event-handling is in utils.js -->
		<s:submit theme="simple" method="create" key="btn.add" cssClass="button blue buttonMargin ajaxButton" />
		<s:reset theme="simple" method="" key="btn.reset" cssClass="button rosy buttonMargin" />
	</div>
</s:form>
</div>