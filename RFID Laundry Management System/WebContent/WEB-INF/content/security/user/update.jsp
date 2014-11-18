<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<%-- The URL use by going back to list define here --%>
<s:url var="backToListUrl" action="user-management" namespace="/system" method="usersList"/>

<script>

	$(function() {

		//go to list page when save success
		$("body").delegate("#usersUpdateForm", "usersUpdateForm.success", function(e, result, result2) {
			msgDialog("", result.actionMessages, "<s:text name="btn.ok"/>", function(){
				var url = "<s:property value="#backToListUrl"/>";
				$(location).attr('href',url);
			});
		});
		$("body").delegate("#usersUpdateForm", "usersUpdateForm.failure", function(e, result, result2) {
			//alertDialog("", result.actionErrors, "<s:text name="btn.ok"/>");
		});		
		//go to list page when save success

		//Cancel Button
		$("#cancelUsersButton").on('click', function(){
			var url = "<s:property value="#backToListUrl"/>";
			$(location).attr('href',url);
			return false;
		});
		//Cancel Button
	})

</script>

<h3 class="fnName"><s:text name="security.users.page.title.update" /></h3>

<div class="body">
<s:form theme="simple" id="usersUpdateForm" namespace="/system" action="user-management" method="post" cssClass="ajaxForm" >

	<div class="actionErrors" ref="usersUpdateForm"></div>
	<div class="actionMessages" ref="usersUpdateForm"></div>

	<fieldset class="fieldsetStyle01" name="infoFieldset" id="infoFieldset">
	
		<legend>
			<s:text name="security.users.page.title.update"></s:text>
		</legend>

		<ul>
		
			<li>
				<label for="users.username" class="width180"><s:text name="security.users.username"></s:text>: </label>
				<s:textfield theme="simple" name="users.username" readonly="true" cssClass="displayText" />
			</li>

			<li>
				<label for="users.password" class="width180"><s:text name="security.users.newPassword"></s:text>: </label>
				<s:password theme="simple" name="users.password" cssClass="inputText" />
			</li>
			
			<li>
				<label for="users.confirmPassword" class="width180"><s:text name="security.users.newConfirmPassword"></s:text>: </label>
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
				<label for="users.enabled" class="width180"><s:text name="security.users.status"></s:text>: </label>
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
		<s:submit theme="simple" method="update" key="btn.update" cssClass="button blue buttonMargin ajaxButton" />
		<s:submit type="button" theme="simple" key="btn.cancel" id="cancelUsersButton" cssClass="button rosy buttonMargin" />
	</div>

</s:form>
</div>