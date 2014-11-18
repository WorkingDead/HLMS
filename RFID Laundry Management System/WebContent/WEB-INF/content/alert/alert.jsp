<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>



<h3 class="fnName"><s:text name="page.title.alert.setting" /></h3>

<s:form theme="simple" id="editForm" namespace="/general" action="alert" method="post" cssClass="ajaxForm">
	
	<fieldset id="alertSettingFieldset" name="alertSettingFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.generalinfo"/></legend>
		
		<div class="actionErrors" ref="editForm"></div>
		<div class="actionMessages" ref="editForm"></div>
		
		
		<ul>
			<li>
				<label for="ironingExpiryPeriod"><s:text name="alert.ironing.time.setting" />: </label>
				<s:textfield theme="simple" name="ironingExpiryPeriod" cssClass="inputText"/>
			</li>
		</ul>
	</fieldset>
	
	<div class="buttonArea">
		<s:submit theme="simple" id="btnSave" method="update" key="btn.save" cssClass="button blue buttonMargin ajaxButton" />
		<s:reset key="btn.reset" cssClass="button rosy buttonMargin"/>
	</div>
</s:form>


<script>

$(function() {
	//////////////////////////////////////////////////
	// Go to list page if save success
	//////////////////////////////////////////////////
	<s:url var="getListPageUrl" namespace="/general" action="alert" method="getListPage"/>
	$("body").delegate("#editForm", "editForm.success", function(e, result, result2) {
		msgDialog("", result.actionMessages, "<s:text name="btn.ok"/>",
				function()
				{
// 					$(location).attr('href',"<s:property value="#getListPageUrl"/>");
				}
			);
	});
});

</script>