<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="pagingStyle01">
	<s:property escape="false" value="page.pagination"/>
</div>

<table class="dataTable">
	<thead>
		<tr>
			<th class="simpleCheckBox">
				<s:checkbox name="cbCheckAll" theme="simple" cssClass="checkall simpleCheckBox" ref="cbCheckAll"></s:checkbox>
			</th>
			<th><s:text name="label.creation.date" /></th>
			<th><s:text name="receipt.type" /></th>
			<th><s:text name="receipt.code" /></th>
			<th><s:text name="receipt.status" /></th>
			<th><s:text name="receipt.cloth.total" /></th>
			<th><s:text name="label.handled.by.user" /></th>
			<th><s:text name="staff.code.handled.by" /></th>
			<th><s:text name="staff.code.picked.by" /></th>
		</tr>
	</thead>
	
	<tbody>
		<s:if test="receiptList == null || receiptList.size == 0">
			<tr>
				<td colspan="9"><s:text name="list.nothing.found"/></td>
			</tr>
		</s:if>
		
		<s:iterator value="receiptList">
			<tr>
				<td><s:checkbox name="receipt.id" fieldValue="%{id}" theme="simple" cssClass="checkall2 simpleCheckBox selectedReceiptMarker" checkall="cbCheckAll"></s:checkbox></td>
				<td>
					<s:date name="creationDate" format="dd-MM-yyyy"/>
				</td>
				
				<td><s:property value="%{getText(receiptType.value)}"/></td>
				<td><s:property value="code"/></td>
				<td><s:property value="%{getText(receiptStatus.value)}"/></td>
				<td><s:property value="receiptClothTotal"/></td>
				<td><s:property value="createdBy.username"/></td>
				<td><s:property value="staffHandledBy.code"/></td>
				<td><s:property value="staffPickedBy.code"/></td>
			</tr>
		</s:iterator>
	</tbody>
	
	<tfoot></tfoot>
</table>