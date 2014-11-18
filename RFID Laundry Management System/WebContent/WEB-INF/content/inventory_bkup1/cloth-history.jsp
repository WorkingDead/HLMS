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
			<th><s:text name="label.date" /></th>
			<th><s:text name="label.time" /></th>
			<th><s:text name="label.event" /></th>
			<th><s:text name="label.system.user" /></th>
			<th><s:text name="label.handled.by.staff" /></th>
			<th><s:text name="label.picked.by.staff" /></th>
		</tr>
	</thead>
	
	<tbody>
		<s:if test="transactionList == null || transactionList.size == 0">
			<tr>
				<td colspan="6"><s:text name="list.nothing.found"/></td>
			</tr>
		</s:if>
		
		<s:iterator value="transactionList">
			<tr>
				<td><s:date name="creationDate" format="dd-MM-yyyy"/></td>
				<td><s:date name="creationDate" format="HH:mm:ss"/></td>
				<td><s:property value="%{getText(transactionName)}"/></td>
				<td><s:property value="createdBy.username"/></td>
				
				<td>
					<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
						<s:property escape="false" value="transHandledByStaff.nameCht" />
					</s:if>
					<s:else>
						<s:property escape="false" value="transHandledByStaff.nameEng" />
					</s:else>
				</td>
				
				<td>
					<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
						<s:property escape="false" value="transPickedByStaff.nameCht" />
					</s:if>
					<s:else>
						<s:property escape="false" value="transPickedByStaff.nameEng" />
					</s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
	
	<tfoot></tfoot>
</table>