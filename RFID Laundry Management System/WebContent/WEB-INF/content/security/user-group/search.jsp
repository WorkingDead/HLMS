<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="pagingStyle01">
	<s:property escape="false" value="page.pagination"/>
</div>

<table class="dataTable">
	<thead>
		<tr>
			<th class="simpleCheckBox">
				<s:checkbox name="groupsListCheck" theme="simple" cssClass="checkall simpleCheckBox" ref="groupsListCheck"></s:checkbox>
			</th>
			<th><s:text name="security.groups.name"></s:text></th>
			<th><s:text name="security.groups.status"></s:text></th>
		</tr>
	</thead>
	<tbody>
		<s:if test="groupsList==null || groupsList.size==0">
			<tr>
				<td colspan="14"><s:text name="list.nothing.found"/></td>
			</tr>
		</s:if>
	
		<s:iterator value="groupsList">
			<tr>
				<!-- fieldValue must use %{} for getting the variable -->
				<td><s:checkbox name="groups.id" fieldValue="%{id}" theme="simple" cssClass="checkall2" checkall="groupsListCheck"></s:checkbox></td>
				<td><s:property value="groupName" /></td>
				<td>
					<s:if test="enabled!=null && enabled==true">
						<s:text name="label.enable" />
					</s:if>
					<s:else>
						<s:text name="label.disable" />
					</s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>