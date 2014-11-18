<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="actionErrors" ref="newForm"></div>
<div class="actionMessages" ref="newForm"></div>

<h3 class="fnName"><s:text name="page.title.zone.new" /></h3>

<div class="body">
	<s:form theme="simple" id="newForm" namespace="/master" action="zone" method="post" cssClass="ajaxForm">
		<fieldset id="newClothesFieldset" name="newClothesFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.generalinfo"></s:text></legend>
			<ul>
				<li>
					<label for="zone.code"><s:text name="zone.code"/>:</label>
					<s:textfield theme="simple" name="zone.code" cssClass="inputText"/>
				</li>
				<li>
					<label for="zone.description"><s:text name="label.description"/>:</label>
					<s:textarea theme="simple" name="zone.description"/>
				</li>
				<li>
					<label for="zone.enable"><s:text name="label.enable"/>:</label>
					<s:checkbox theme="simple" name="zone.enable" value="true" fieldValue="true" cssClass="simpleCheckBox"/>
				</li>
			</ul>
		</fieldset>

		<div class="buttonArea">
			<s:submit theme="simple" id="btnCreate" method="create" key="btn.add" cssClass="button blue buttonMargin ajaxButton"/>
			<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="button rosy buttonMargin"/>
		</div>
	</s:form>
</div>

<script>

//////////////////////////////////////////////////////////////////////
//dom ready (HTML Document Load Complete)
//////////////////////////////////////////////////////////////////////
$(function(){
	
	resetNewForm();
});

function resetNewForm() {

	//////////////////////////////////////////////////
	// Go to new or list page when save success
	//////////////////////////////////////////////////
	<s:url var="getNewPageUrl" action="zone" namespace="/master" method="getNewPage"/>
	<s:url var="getListPageUrl" action="zone" namespace="/master" method="getListPage"/>
	
	$("body").delegate("#newForm", "newForm.success", function(e, result, result2) {

		optionsAvailableMsgDialog("", result.actionMessages, 
				"<s:text name="btn.continue"/>",
				function(){
					$(location).attr('href',"<s:property value="#getNewPageUrl"/>");
				},
				"<s:text name="btn.ok"/>",
				function(){
					$(location).attr('href',"<s:property value="#getListPageUrl"/>");
				}
			);
	});
	
}

</script>

