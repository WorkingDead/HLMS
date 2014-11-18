<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.monthly.reported.missing.report" /></h3>

<!-- all action/error message here -->
<s:if test="hasActionMessages() ">
	<div class="actionMessages">
		<s:actionmessage />
	</div>
</s:if>

<s:if test="hasActionErrors()">
	<div class="actionErrors">
		<s:actionerror />
	</div>
</s:if>

<div class="body">
	<s:form theme="simple" namespace="/report" action="monthly-reported-missing-report" method="post" target="_blank">
		<fieldset id="searchFieldset" name="searchFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.export.criteria" /></legend>
			<ul>
				<li>
					<label for="monthStr"><s:text name="label.month"/>:</label>
					<s:textfield theme="simple" name="yearMonthStr" cssClass="inputText monthPicker"/>
				</li>
				
				<li>
					<label for="format"><s:text name="label.format" />: </label>
					<s:select theme="simple"
							name="format" 
							list="formatList" 
							listKey="exportName"
							listValue="displayName"
							emptyOption="false" 
							cssClass="inputText" />
				</li>
				<%--
				--%>
			</ul>
		</fieldset>
	
		<div class="buttonArea">
			<s:submit theme="simple" id="btnExport" key="btn.export" method="exportReport" cssClass="button blue buttonMargin"></s:submit>
			<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="button rosy buttonMargin"/>
		</div>
	</s:form>
</div>
