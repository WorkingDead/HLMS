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
			<th><s:text name="clothType.name" /></th>
			<th><s:text name="label.description" /></th>
			<th class="enable"><s:text name="label.enable" /></th>
		</tr>
	</thead>
	
	<tbody>
		<s:if test="clothTypeList == null || clothTypeList.size == 0">
			<tr>
				<td colspan="4"><s:text name="list.nothing.found"/></td>
			</tr>
		</s:if>
		
		<s:iterator value="clothTypeList">
			<tr>
				<td><s:checkbox name="clothType.id" fieldValue="%{id}" theme="simple" cssClass="checkall2 simpleCheckBox" checkall="cbCheckAll"></s:checkbox></td>
				<td><s:property value="name"/></td>
				<td><s:property value="description"/></td>
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