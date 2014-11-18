<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="actionErrors" ref="editForm"></div>
<div class="actionMessages" ref="editForm"></div>

<div class="body">
	<s:form theme="simple" id="editForm" namespace="/master" action="zone" method="post" cssClass="ajaxForm" >
		<fieldset id="infoFieldset" name="infoFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.generalinfo"></s:text></legend>
			<ul>
				<li>
					<label for="zone.code"><s:text name="zone.code"/>:</label>
					<s:textfield theme="simple" name="zone.code" readonly="true" cssClass="displayText"/>
				</li>
				
				<li>
					<label for="zone.description"><s:text name="label.description"/>:</label>
					<s:textarea theme="simple" name="zone.description" />
				</li>
				
				<li>
					<label for="zone.enable"><s:text name="label.enable"/>:</label>
					<s:checkbox theme="simple" name="zone.enable" cssClass="simpleCheckBox"/>
				</li>
			</ul>
		</fieldset>

		<div class="buttonArea">
			<s:submit theme="simple" id="btnSave" method="update" key="btn.save" cssClass="button blue buttonMargin ajaxButton"/>
		</div>
	</s:form>
</div>