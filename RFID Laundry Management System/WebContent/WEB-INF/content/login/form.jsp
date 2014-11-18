<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<%-- <s:form theme="simple" cssClass="center" action="/j_spring_security_check" method="post"> --%>
<form action="<%= request.getContextPath() %>/j_spring_security_check" method="post" class="center">

	<div class="loginArea">
		<fieldset id="loginFieldset" name="loginFieldset" class="fieldsetStyle03">
		<legend><s:text name="menu.login" /></legend>
		<ul>
		<li>
		<label for="loginID"><s:text name="menu.loginid" /></label>
		<s:textfield theme="simple" name="j_username" label="Username"></s:textfield>
		</li>
		<li>
		<label for="password"><s:text name="menu.login.password" /></label>
		<s:password theme="simple" name="j_password" label="Password"></s:password>
		</li>
		</ul>
		</fieldset>
	</div>
	<div class="buttonArea">
	    
		<s:submit theme="simple" name="submit" key="btn.login" cssClass="button blue buttonMargin" />
		<s:reset theme="simple" name="reset" key="btn.reset" cssClass="button rosy buttonMargin"></s:reset>
	</div>
	
</form>
<%-- </s:form> --%>