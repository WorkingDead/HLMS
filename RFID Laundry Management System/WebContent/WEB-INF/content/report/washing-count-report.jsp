<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.washing.count.report" /></h3>

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
	<s:form theme="simple" namespace="/report" action="washing-count-report" method="post" target="_blank">
		<fieldset id="searchFieldset" name="searchFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.export.criteria" /></legend>
			<ul>
				<li>
					<label for=dept.id><s:text name="staff.dept"/>:</label>
					<s:select theme="simple" 
						id="deptId"
						name="dept.id"
						list="deptList"
						listKey="id"
						listValue="nameCht"
						emptyOption="true" />
				</li>
				
				<li>
					<label for="washCountRange"><s:text name="wash.count"/>:</label>
					<s:select theme="simple" 
						id="washCountRangeId"
						name="washCountRange"
						list="washCountRangeList"
						listKey="value"
						listValue="getText(txt)"
						emptyOption="false" />
				</li>
				
			</ul>
		</fieldset>
	
		<div class="buttonArea">
				<s:submit theme="simple" id="btnExport01" key="btn.export" method="exportReport" cssClass="button blue buttonMargin"></s:submit>
<%-- 			<s:submit theme="simple" id="btnExport" key="btn.export" method="XXXXXX" cssClass="button blue buttonMargin"></s:submit> --%>
<%-- 			<s:submit theme="simple" id="btnRealExport" key="btn.export" method="exportReport" cssClass="btnHidden"></s:submit> --%>
			<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="button rosy buttonMargin"/>
		</div>
	</s:form>
</div>




<script>

$(function() {

	<s:url var="getClothCountUrl" namespace="/report" action="washing-count-report" method="getClothCount"></s:url>
	$("#btnExport").on("click", function() {
			var url = "<s:property value="#getClothCountUrl"/>";
			var data = "dept.id=" + $("#deptId").val();
			data += "&washCountRange=" + $("#washCountRangeId").val();
			
			$.post(
					url,
					data,
					function(result)
					{
						if (result.errors == undefined && result.fieldErrors == undefined)
						{
							var numOfFound = result;
							if (numOfFound >= 5000)
							{
								var msgStr = "<s:text name="dialog.msg.cloth.found"/>" + ": " + numOfFound + "<br><br>\n";
								msgStr += "<s:text name="dialog.msg.too.many.cloth.report.export.problem"/>"
								
								optionsAvailableMsgDialog(
										"", 
										msgStr, 
										
										"<s:text name="btn.yes"/>",
										function()
										{
											$("#btnRealExport").trigger("click");
										},
										
										"<s:text name="btn.no"/>",
										function()
										{
											// nothing to do
										}
								);
							}
							else
							{
								$("#btnRealExport").trigger("click");
							}
						}
					}, 
					"json"
			).error(
					function(jqXHR, textStatus, errorThrown)
					{
						alert("WashingCountReport cloth count error~!!");
					}
			);
			
		return false;
	});
});

</script>