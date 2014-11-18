<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="actionErrors" ref="newForm"></div>
<div class="actionMessages" ref="newForm"></div>

<h3 class="fnName"><s:text name="page.title.clothType.new" /></h3>

<div class="body">
	<s:form theme="simple" id="newForm" namespace="/master" action="cloth-type" method="post" cssClass="ajaxForm">
		<fieldset id="newClothesFieldset" name="newClothesFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.generalinfo"></s:text></legend>
			<ul>
				<li>
					<label for="clothType.name"><s:text name="clothType.name"/>:</label>
					<s:textfield theme="simple" name="clothType.name" cssClass="inputText" />
					<%--
					<s:select theme="simple"
						name="clothType.name" 
						list="clothTypeNameList" 
						cssClass="inputText"
					 />
					 --%>
				</li>
	
				<li>
					<label for="clothType.description"><s:text name="label.description"/>:</label>
					<s:textarea theme="simple" name="clothType.description"/>
				</li>
	
				<li>
					<label for="clothType.enable"><s:text name="label.enable"/>:</label>
					<s:checkbox theme="simple" name="clothType.enable" value="true" fieldValue="true" cssClass="simpleCheckBox"/>
				</li>
	
				<li>
					<s:property escapeHtml="false" escapeJavaScript="false" value="attachmentInput"/>
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
	
	//Create Form handler
	//go to new or list page when save success
	<s:url var="getNewPageUrl" action="cloth-type" namespace="/master" method="getNewPage"/>
	<s:url var="getListPageUrl" action="cloth-type" namespace="/master" method="getListPage"/>

	//Not In Use
	//This statement cannot get the result variable
// 	$("#newForm").live("newForm.success", function(result){
		//$("#btnReset").trigger("click");		//Don't know why this has no effect
		//$("#newForm").trigger("reset");		//This statement cannot reset the file uploader
// 	});
	//Not In Use
	
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
	//Create Form handler
}

</script>

