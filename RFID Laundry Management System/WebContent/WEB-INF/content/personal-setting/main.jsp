<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.personal.setting" /></h3>

<div class="body">
<s:form theme="simple" id="personalSettingForm" namespace="/system" action="personal-setting" method="post" cssClass="ajaxForm" >

	<div class="actionErrors" ref="personalSettingForm"></div>
	<div class="actionMessages" ref="personalSettingForm"></div>

	<fieldset class="fieldsetStyle01" name="infoFieldset" id="infoFieldset">
	
		<legend>
			<s:text name="fieldset.legend.personal.setting"></s:text>
		</legend>

		<ul>
		
			<li>
				<label for="users.username" class="width180"><s:text name="security.users.username"></s:text>: </label>
				<s:textfield theme="simple" name="users.username" readonly="true" cssClass="displayText" />
			</li>

			<li>
				<label for="oldPassword" class="width180"><s:text name="security.users.oldPassword"></s:text>: </label>
				<s:password theme="simple" name="oldPassword" cssClass="inputText" />
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
		</ul>

	</fieldset>

	<div class="buttonArea">
		<!-- the button event-handling is in utils.js -->
		<s:submit theme="simple" method="update" key="btn.update" cssClass="button blue buttonMargin ajaxButton" />
		<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="button rosy buttonMargin"/>
	</div>

</s:form>
</div>

<script>

$(function() {

	$("body").delegate("#personalSettingForm", "personalSettingForm.success", function(e, result, result2) {
		msgDialog("", result.actionMessages, "<s:text name="btn.ok"/>", function(){
			location.reload();
		});
	});

})

</script>