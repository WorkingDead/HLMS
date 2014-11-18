<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="actionErrors" ref="editForm"></div>
<div class="actionMessages" ref="editForm"></div>

<div class="body">
	<s:form theme="simple" id="editForm" namespace="/master" action="cloth-type" method="post" cssClass="ajaxForm" >
		<fieldset id="infoFieldset" name="infoFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.generalinfo"></s:text></legend>
			<ul>
				<li>
					<label for="clothType.name"><s:text name="clothType.name" /></label>
					<s:textfield theme="simple" name="clothType.name" readonly="false" cssClass="inputText"/>
				</li>

				<li>
					<label for="clothType.description"><s:text name="label.description" /></label>
					<s:textarea theme="simple" name="clothType.description" />
				</li>

				<li>
					<label for="clothType.enable"><s:text name="label.enable" /></label>
					<s:checkbox theme="simple" name="clothType.enable" cssClass="simpleCheckBox"/>
				</li>
				
				<li>
					<s:property escapeHtml="false" escapeJavaScript="false" value="attachmentInput"/>
				</li>
			</ul>
		</fieldset>

		<div class="buttonArea">
			<s:submit theme="simple" id="btnSave" method="update" key="btn.save" cssClass="button blue buttonMargin ajaxButton"/>
		</div>
	</s:form>
</div>




