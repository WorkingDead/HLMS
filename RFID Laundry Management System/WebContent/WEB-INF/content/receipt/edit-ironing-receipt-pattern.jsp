<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.roll.container.detail" /></h3>

<div class="body">

<!-- ################################################## -->
<!-- Roll Container Info Fieldset -->
<!-- ################################################## -->

<s:form theme="simple" id="editForm" namespace="/general" action="receipt" method="post" cssClass="ajaxForm">
	<fieldset id="infoFieldset" name="infoFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.roll.container.info"/></legend>
		
		<!-- This div's ref must set to form id -->
		<div class="actionErrors" ref="editForm"></div>
		<div class="actionMessages" ref="editForm"></div>
		
		<s:hidden theme="simple" id="hiddenReceiptId" name="receiptPatternIron.receipt.id" />
		<s:hidden theme="simple" id="hiddenPatternId" name="receiptPatternIron.id" />
		
		<ul>
			<li>
				<label for="deliveryDateStr"><s:text name="label.ironing.delivery.date"/>:</label>
				<s:if test="receiptPatternIron.receipt.receiptStatus.toString() == 'Finished' || receiptPatternIron.receipt.receiptStatus.toString() == 'Followup'">
					<s:textfield theme="simple" name="deliveryDateStr" cssClass="displayText" readonly="true" >
						<s:param name="value">
							<s:date name="receiptPatternIron.ironingDeliveryTime" format="dd-MM-yyyy"/>
						</s:param>
					</s:textfield>
				</s:if>
				<s:else>
					<s:textfield theme="simple" name="deliveryDateStr" cssClass="inputText dateTimePicker" readonly="false" >
						<s:param name="value">
							<s:date name="receiptPatternIron.ironingDeliveryTime" format="dd-MM-yyyy"/>
						</s:param>
					</s:textfield>
				</s:else>
			</li>
			
			<li>
				<label for="deliveryTimeStr"><s:text name="label.ironing.delivery.time"/>:</label>
				
				<s:if test="receiptPatternIron.receipt.receiptStatus.toString() == 'Finished' || receiptPatternIron.receipt.receiptStatus.toString() == 'Followup'">
					<s:textfield theme="simple" name="deliveryTimeStr" cssClass="displayText" readonly="true" >
						<s:param name="value">
							<s:date name="receiptPatternIron.ironingDeliveryTime" format="HH:mm"/>
						</s:param>
					</s:textfield>
				</s:if>
				<s:else>
					<s:textfield theme="simple" name="deliveryTimeStr" cssClass="inputText timePicker" readonly="false" >
						<s:param name="value">
							<s:date name="receiptPatternIron.ironingDeliveryTime" format="HH:mm"/>
						</s:param>
					</s:textfield>
				</s:else>
			</li>
			
			<li>
				<label for="receiptPatternIron.rollContainer.code"><s:text name="roll.container.code"/>:</label>
				<s:textfield theme="simple" name="receiptPatternIron.rollContainer.code" cssClass="displayText" readonly="true"/>
			</li>
			
			<%--
			<li>
				<label for="receiptPatternIron.patternClothTotal"><s:text name="label.cloth.total"/>:</label>
				<s:textfield theme="simple" name="receiptPatternIron.patternClothTotal" cssClass="displayText" readonly="true"/>
			</li>
			--%>
		</ul>
	</fieldset>



<!-- ################################################## -->
<!-- Cloth Summary Fieldset -->
<!-- ################################################## -->
	<fieldset id="clothSummaryFieldset" name="clothSummaryFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.cloth.summary"/></legend>
		
		<div class="RFIDTotal">
			<table id="clothTotalTable">
				<thead>
					<tr><td><s:text name="label.cloth.total"/></td></tr>
				</thead>
				<tbody>
					<tr>
						<td class="bigText">
							<s:property value="receiptPatternIron.patternClothTotal"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
				
		<div class="RFIDSummary">
			<table id="clothSummaryTable">
				<thead>
					<tr>
						<td><s:text name="clothType.clothType"/></td>
						<td><s:text name="label.type.total"/></td>
					</tr>
				</thead>
				
				<tbody>
				
					<s:iterator value="clothTypeCountList">
						<tr>
							<td><s:property value="type"/></td>
							<td><s:property value="num"/></td>
						</tr>
					</s:iterator>
					
				</tbody>
			</table>	
		</div>
		
	</fieldset>


<!-- ################################################## -->
<!-- Cloth Detail Fieldset -->
<!-- ################################################## -->

		<fieldset id="clothDetailFieldset" name="clothDetailFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.cloth.detail"/></legend>
			
			<div class="RFIDTable">
				<table id="clothDetailTable" class="dataTable">
					<thead>
						<tr>
							<th><s:text name="staff.dept"/></th>
							<th><s:text name="staff.code"/></th>
							<th><s:text name="staff.name"/></th>
							<th><s:text name="clothType.clothType"/></th>
							<th><s:text name="cloth.code"/></th>
							<th><s:text name="label.rfid"/></th>
							<th><s:text name="cloth.status"/></th>
						</tr>
					</thead>
					
					<tbody>
						<s:iterator value="clothList">
							<tr>
								<td><s:property value="staff.dept.nameCht"/></td>
								<td><s:property value="staff.code"/></td>
								<td><s:property value="staff.nameCht"/></td>
								<td><s:property value="clothType.name"/></td>
								<td><s:property value="code"/></td>
								<td><s:property value="rfid"/></td>
								<td><s:property value="%{getText(clothStatus.value)}" escape="false"/></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
		</fieldset>

		<div class="buttonArea">
		
			<s:if test="receiptPatternIron.receipt.receiptStatus.toString() == 'Finished' || receiptPatternIron.receipt.receiptStatus.toString() == 'Followup'">
				<s:submit theme="simple" type="button" key="btn.save" method="update" disabled="true" cssClass="button blue buttonMargin ajaxButton" />
			</s:if>
			<s:else>
				<s:submit theme="simple" type="button" key="btn.save" method="update" cssClass="button blue buttonMargin ajaxButton" />
			</s:else>
			
			<s:submit theme="simple" type="button" id="btnBack" key="btn.back" cssClass="button rosy buttonMargin"/>
		</div>
</s:form>
</div>



<script>
$(function(){
	
	$("body").delegate("#editForm", "editForm.success", function(e, result, result2) {
		
		msgDialog("", result.actionMessages, "<s:text name="btn.ok"/>",
				
				function()
				{
					$("#btnBack").trigger("click");
				}
			);
	});
	
	<s:url var="getDetailPageUrl" namespace="/general" action="receipt" method="getDetailPage"/>
	$("#btnBack").click(function() {
		var url = "<s:property value="#getDetailPageUrl"/>";
		url += "?selectedReceiptList[0].id=" + $("#hiddenReceiptId").val();
		
		$(location).attr('href', url);
		return false;
	});
});
</script>



