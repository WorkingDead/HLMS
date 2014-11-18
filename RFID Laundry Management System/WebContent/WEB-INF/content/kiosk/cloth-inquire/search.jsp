<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<s:set var="SystemLanguageEnUS" value="@web.actions.BaseAction$SystemLanguage@en_US"/>
<s:set var="SystemLanguagezhCN" value="@web.actions.BaseAction$SystemLanguage@zh_CN"/>
<s:set var="currentSystemLanguage" value="getCurrentSystemLanguage()"/>

<div class="pagingStyle01">
	<s:property escape="false" value="page.pagination"/>
</div>

<table class="kioskRFIDTable">
	<thead>
		<tr>
			<th><s:checkbox name="cbCheckAll" theme="simple" cssClass="checkall simpleCheckBox" ref="cbCheckAll"></s:checkbox></th>
			<th><s:text name="staff.dept"/></th>
			<th><s:text name="staff.code"/></th>
			<th><s:text name="cloth.location"/></th>
			<th><s:text name="staff.name"/></th>
			<th><s:text name="clothType.clothType"/></th>
			<th><s:text name="cloth.code"/></th>
			<th><s:text name="label.rfid"/></th>
			<th><s:text name="label.status"/></th>
		</tr>
	</thead>
	
	<tbody>
		<s:if test="clothList == null || clothList.size == 0">
			<tr>
				<td colspan="8"><s:text name="list.nothing.found"/></td>
			</tr>
		</s:if>

		<s:iterator value="clothList">
			<tr>
				<td><s:checkbox name="cloth.id" fieldValue="%{id}" theme="simple" cssClass="checkall2 simpleCheckBox" checkall="cbCheckAll"></s:checkbox></td>
				
				<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
					<td><s:property value="staff.dept.nameCht"/></td>
					<td><s:property value="staff.code"/></td>
					<td><s:property value="zone.code"/></td>
					<td><s:property value="staff.nameCht"/></td>
					<td><s:property value="clothType.name"/></td>
					<td><s:property value="code"/></td>
					<td class="RFIDDisplay"><s:property value="rfid"/></td>
					<td><s:property value="%{getText(clothStatus.value)}"/></td>
				</s:if>
				<s:else>
					<td><s:property value="staff.dept.nameEng"/></td>
					<td><s:property value="staff.code"/></td>
					<td><s:property value="zone.code"/></td>
					<td><s:property value="staff.nameEng"/></td>
					<td><s:property value="clothType.name"/></td>
					<td><s:property value="code"/></td>
					<td class="RFIDDisplay"><s:property value="rfid"/></td>
					<td><s:property value="%{getText(clothStatus.value)}"/></td>
				</s:else>

			</tr>
		</s:iterator>
	</tbody>
	
	<tfoot></tfoot>
</table>