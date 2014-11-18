<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.receipt.detail" /></h3>

<div class="body">

<!-- ################################################## -->
<!-- Receipt Info Fieldset -->
<!-- ################################################## -->
	<fieldset id="infoFieldset" name="infoFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.generalinfo"/></legend>
		<ul>
		
			<li>
				<label for="receipt.code"><s:text name="receipt.code"/>:</label>
				<s:textfield theme="simple" name="receipt.code" cssClass="displayText" readonly="true"/>
			</li>
			
			<li>
				<label for="receipt.receiptType"><s:text name="receipt.type"/>:</label>
				<s:textfield theme="simple" name="receipt.receiptType" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:property value="%{getText(receipt.receiptType.value)}" escape="false"/>
					</s:param>
				</s:textfield>
			</li>
			
			<li>
				<label for="tbCreationDate"><s:text name="label.creation.date"/>:</label>
				<s:textfield theme="simple" name="tbCreationDate" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:date name="receipt.creationDate" format="dd-MM-yyyy"/>
					</s:param>
				</s:textfield>
			</li>
			
			<li>
				<label for="tbCreationTime"><s:text name="label.time"/>:</label>
				<s:textfield theme="simple" name="tbCreationTime" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:date name="receipt.creationDate" format="HH:mm:ss"/>
					</s:param>
				</s:textfield>
			</li>
			
			<%--
			<li>
				<label for="receiptClothTotal"><s:text name="receipt.cloth.total"/>:</label>
				<s:textfield theme="simple" id="receiptClothTotal" name="receiptClothTotal" cssClass="displayText" readonly="true"/>
			</li>
			--%>
			
			<li>
				<label for="receipt.receiptStatus"><s:text name="receipt.status"/>:</label>
				<s:textfield theme="simple" name="receipt.receiptStatus" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:property value="%{getText(receipt.receiptStatus.value)}" escape="false"/>
					</s:param>
				</s:textfield>
			</li>
			
			<li>
				<label for="receipt.createdBy.username"><s:text name="label.handled.by.user"/>:</label>
				<s:textfield theme="simple" name="receipt.createdBy.username" cssClass="displayText" readonly="true" />
			</li>
			
			<li>
				<label for="receipt.staffHandledBy.code"><s:text name="staff.code.handled.by"/>:</label>
				<s:textfield theme="simple" name="receipt.staffHandledBy.code" cssClass="displayText" readonly="true" />
			</li>
			
			<li>
				<label for="receipt.staffPickedBy.code"><s:text name="staff.code.picked.by"/>:</label>
				<s:textfield theme="simple" name="receipt.staffPickedBy.code" cssClass="displayText" readonly="true" />
			</li>
		</ul>
	</fieldset>



<!-- ################################################## -->
<!-- Cloth Summary Fieldset -->
<!-- ################################################## -->
	<fieldset id="clothSummaryFieldset" name="clothSummaryFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.cloth.summary"/></legend>
		
		<div class="RFIDTotal">
			<table id="RFIDTotal">
				<thead>
					<tr><td><s:text name="label.cloth.total"/></td></tr>
				</thead>
				<tbody>
					<tr>
						<td class="bigText">
							<s:property value="receipt.receiptClothTotal"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
				
		<div class="RFIDSummary">
			<table id="RFIDSummary">
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
		<s:submit theme="simple" type="button" id="btnBack" key="btn.back" cssClass="button rosy buttonMargin"/>
	</div>

</div>



<script>
$(function(){
	<s:url var="getListPageUrl" namespace="/general" action="receipt" method="getListPage"/>
	$("#btnBack").click(function() {
		$(location).attr('href',"<s:property value="#getListPageUrl"/>");
		return false;
	});
});
</script>



