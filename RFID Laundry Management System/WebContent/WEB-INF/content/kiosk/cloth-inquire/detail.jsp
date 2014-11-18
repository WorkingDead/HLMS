<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<s:set var="SystemLanguageEnUS" value="@web.actions.BaseAction$SystemLanguage@en_US"/>
<s:set var="SystemLanguagezhCN" value="@web.actions.BaseAction$SystemLanguage@zh_CN"/>
<s:set var="currentSystemLanguage" value="getCurrentSystemLanguage()"/>

<%-- upload attachment url --%>
<s:url var="showAttachmentUrl" namespace="/others" action="attachment" method="showKioskAttachment"></s:url>

<div class="actionErrors" ref="infoForm"></div>
<div class="actionMessages" ref="infoForm"></div>

<div class="body">
	<s:form theme="simple" id="infoForm" namespace="/kiosk" action="cloth-inquire" method="post" cssClass="ajaxForm" >
		<fieldset id="infoFieldset" name="infoFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.generalinfo"></s:text></legend>
			<ul>
				<li>
					<label for="cloth.staff.code"><s:text name="staff.code"/>:</label>
					<s:textfield theme="simple" name="cloth.staff.code" readonly="true" cssClass="displayText"/>
				</li>

				<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
					<li>
						<label for="cloth.staff.dept.nameCht"><s:text name="staff.dept"/>: </label>
						<s:textfield theme="simple" name="cloth.staff.dept.nameCht" readonly="true" cssClass="displayText"/>
					</li>
				
					<li>
						<label for="cloth.staff.nameCht"><s:text name="staff.name"/>: </label>
						<s:textfield theme="simple" name="cloth.staff.nameCht" readonly="true" cssClass="displayText"/>
					</li>
				</s:if>
				<s:else>
					<li>
						<label for="cloth.staff.dept.nameEng"><s:text name="staff.dept"/>: </label>
						<s:textfield theme="simple" name="cloth.staff.dept.nameEng" readonly="true" cssClass="displayText"/>
					</li>
				
					<li>
						<label for="cloth.staff.nameEng"><s:text name="staff.name"/>: </label>
						<s:textfield theme="simple" name="cloth.staff.nameEng" readonly="true" cssClass="displayText"/>
					</li>
				</s:else>

				<li>
					<label for="cloth.staff.staffStatus"><s:text name="staff.status"/>:</label>
					<s:textfield theme="simple" name="cloth.staff.staffStatus" readonly="true" cssClass="displayText">
						<s:param name="value">
							<s:property value="%{getText(cloth.staff.staffStatus.value)}" escape="false"/>
						</s:param>
					</s:textfield>
				</li>

				<li>
					<label for="cloth.clothType.name"><s:text name="cloth.type"/>:</label>
					<s:textfield theme="simple" name="cloth.clothType.name" readonly="true" cssClass="displayText"/>
				</li>

				<li>
					<label for="cloth.size"><s:text name="cloth.size"/>:</label>
					<s:textfield theme="simple" name="cloth.size" readonly="true" cssClass="displayText"/>
				</li>
				
				<li>
					<label for="cloth.code"><s:text name="cloth.code"/>:</label>
					<s:textfield theme="simple" name="cloth.code" readonly="true" cssClass="displayText"/>
				</li>
				
				<li>
					<label for="cloth.clothStatus"><s:text name="cloth.status"/>:</label>
					<s:textfield theme="simple" name="cloth.clothStatus" readonly="true" cssClass="displayText">
						<s:param name="value">
							<s:property value="%{getText(cloth.clothStatus.value)}" escape="false"/>
						</s:param>
					</s:textfield>
				</li>
				
				<li>
					<label for="cloth.rfid"><s:text name="label.rfid"/>:</label>
					<s:textfield theme="simple" name="cloth.rfid" readonly="true" cssClass="displayText"/>
				</li>
				
				<li>
					<label for="cloth.zone.code"><s:text name="cloth.location"/>:</label>
					<s:textfield theme="simple" name="cloth.zone.code" readonly="true" cssClass="displayText"/>
				</li>
				
				<li>
					<label for="cloth.description"><s:text name="label.description"/>:</label>
					<s:textarea theme="simple" name="clothDesc" cssClass="displayText height30" readonly="true">
						<s:param name="value">
							<s:property escape="false" value="cloth.clothType.description"/>
						</s:param>
					</s:textarea>
				</li>

				<li>
					<label><s:text name="label.image"/>:</label>
					<img src="<s:property value="#showAttachmentUrl"/>?attachmentId=<s:property value="cloth.clothType.soleImageAttachment.id"/>&thumbnail=true" />
				</li>
			</ul>
		</fieldset>
	</s:form>

	<s:form theme="simple" namespace="/master" action="cloth" method="post" cssClass="ajaxSearchDialogForm" listtable="resultDialogTable">
		<s:hidden theme="simple" name="cloth.id" />
		
		<fieldset id="resultFieldset" name="resultFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.cloth.history" /></legend>
			
			<div id="resultDialogTable" class="tableOfInfoDialog">
				<img alt="loading" class="indicator" src="<s:property value="imagesPath"/>layout/ajax-load.gif" />
				<!-- will auto load from ajax -->
			</div>
		</fieldset>
		
		<div class="buttonArea">
			<s:submit theme="simple" id="searchButtonHidden" key="btn.search" method="getKioskClothHistory" cssClass="btnHidden ajaxSearchDialogButton"></s:submit>
		</div>
	</s:form>
</div>

<script>
$(function(){
	$("#searchButtonHidden").trigger("click");
});
</script>