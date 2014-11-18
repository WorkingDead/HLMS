<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<s:set var="SystemLanguageEnUS" value="@web.actions.BaseAction$SystemLanguage@en_US"/>
<s:set var="SystemLanguagezhCN" value="@web.actions.BaseAction$SystemLanguage@zh_CN"/>
<s:set var="currentSystemLanguage" value="getCurrentSystemLanguage()"/>

<div class="pagingStyle01">
	<s:property escape="false" value="page.pagination"/>
</div>

<table class="dataTable">
	<thead>
		<tr>
			<th><s:checkbox name="cbCheckAll" theme="simple" cssClass="checkall simpleCheckBox" ref="cbCheckAll"></s:checkbox></th>
			
			<th><s:text name="label.date" /></th>
			<th><s:text name="label.time" /></th>
			<th><s:text name="special.event.special.event" /></th>
			<th><s:text name="staff.dept"/></th>
			<th><s:text name="staff.code"/></th>
			<th><s:text name="staff.name.cht"/></th>
			<th><s:text name="staff.name.eng"/></th>
			<th><s:text name="clothType.clothType"/></th>
			<th><s:text name="cloth.size"/></th>
			<th><s:text name="cloth.code"/></th>
			<th><s:text name="special.event.status"/></th>
		</tr>
	</thead>
	
	<tbody>
		<s:if test="specialEventList == null || specialEventList.size == 0">
			<tr>
				<td colspan="11"><s:text name="list.nothing.found"/></td>
			</tr>
		</s:if>

		<s:iterator value="specialEventList">
			<tr>
				<td><s:checkbox name="specialEvent.id" fieldValue="%{id}" theme="simple" cssClass="checkall2 simpleCheckBox selectedSpecialEventMarker" checkall="cbCheckAll"></s:checkbox></td>
				<td><s:date name="creationDate" format="dd-MM-yyyy"/></td>
				<td><s:date name="creationDate" format="HH:mm:ss"/></td>
				
				<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
					<td><s:property value="%{getText(specialEventName.value)}"/></td>
					<td><s:property value="staff.dept.nameCht"/></td>
					<td><s:property value="staff.code"/></td>
					<td><s:property value="staff.nameCht"/></td>
					<td><s:property value="staff.nameEng"/></td>

					<s:if test="cloth == null">
						<td><s:property value="clothType.name"/></td>
					</s:if>
					<s:else>
						<td><s:property value="cloth.clothType.name"/></td>
					</s:else>

					<td><s:property value="cloth.size"/></td>
					<td><s:property value="cloth.code"/></td>
					<td><s:property value="%{getText(specialEventStatus.value)}"/></td>
				</s:if>
				<s:else>
					<td><s:property value="%{getText(specialEventName.value)}"/></td>
					<td><s:property value="staff.dept.nameEng"/></td>
					<td><s:property value="staff.code"/></td>
					<td><s:property value="staff.nameCht"/></td>
					<td><s:property value="staff.nameEng"/></td>

					<s:if test="cloth == null">
						<td><s:property value="clothType.name"/></td>
					</s:if>
					<s:else>
						<td><s:property value="cloth.clothType.name"/></td>
					</s:else>

					<td><s:property value="cloth.size"/></td>
					<td><s:property value="cloth.code"/></td>
					<td><s:property value="%{getText(specialEventStatus.value)}"/></td>
				</s:else>
			</tr>
		</s:iterator>
	</tbody>
	
	<tfoot></tfoot>
</table>