<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="pagingStyle01">
	<s:property escape="false" value="page.pagination"/>
</div>

<table class="dataTable">
	<thead>
		<tr>
			<th class="simpleCheckBox">
				<s:checkbox theme="simple" 
					id="cbCheckAll" 
					name="cbCheckAll" 
					cssClass="simpleCheckBox checkall" ref="cbCheckAll"></s:checkbox>
			</th>
			<th><s:text name="staff.code" /></th>
			<th><s:text name="staff.position" /></th>
			<th><s:text name="staff.name.cht" /></th>
			<th><s:text name="staff.name.eng" /></th>
			<th><s:text name="staff.dept" /></th>
			<th><s:text name="staff.status" /></th>
			<th><s:text name="staff.card" /></th>
			<th class="enable"><s:text name="label.enable" /></th>
		</tr>
	</thead>
	
	<tbody>
		<s:if test="staffList == null || staffList.size == 0">
			<tr>
				<td colspan="9"><s:text name="list.nothing.found"/></td>
			</tr>
		</s:if>
		
		<s:iterator value="staffList">
			<tr>
				<%--
				<td>
					<s:checkbox theme="simple" 
						name="staff.id" 
						fieldValue="%{id}" 
						cssClass="simpleCheckBox checkall2" checkall="cbCheckAll"></s:checkbox>
				</td>
				
				<td>
					<s:checkbox theme="simple" 
						name="selectedStaffList[%{id}].id" 
						fieldValue="%{id}" 
						cssClass="simpleCheckBox checkall2 selectedStaffMarker" checkall="cbCheckAll"></s:checkbox>
				</td>
				--%>
				
				<td>
					<s:checkbox theme="simple" 
						id="selected_staff_id_[%{id}]"
						name="selectedStaffMarker" 
						fieldValue="%{id}" 
						cssClass="simpleCheckBox checkall2 selectedStaffMarker" checkall="cbCheckAll">
					</s:checkbox>
				</td>
				
				<td><s:property value="code"/></td>
				<td><s:property value="position"/></td>
				<td><s:property value="nameCht"/></td>
				<td><s:property value="nameEng"/></td>
				<td><s:property value="dept.nameCht"/></td>
				<td><s:property value="%{getText(staffStatus.value)}"/></td>
				<td><s:property value="cardNumber"/></td>
				<td>
					<s:if test="enable">
						<img alt="yes" src="<%=request.getContextPath()%>/images/boolean/true.gif" />
					</s:if>
					<s:else>
						<img alt="no" src="<%=request.getContextPath()%>/images/boolean/false.gif" />
					</s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
	
	<tfoot></tfoot>
</table>