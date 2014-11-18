<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<!-- Do not know why this codes cannot be replaced by the same codes in globalSetting.jsp -->
<s:set var="SystemLanguageEnUS" value="@web.actions.BaseAction$SystemLanguage@en_US"/>
<s:set var="SystemLanguagezhCN" value="@web.actions.BaseAction$SystemLanguage@zh_CN"/>
<s:set var="currentSystemLanguage" value="getCurrentSystemLanguage()"/>
<!-- Do not know why this codes cannot be replaced by the same codes in globalSetting.jsp -->

<s:url var="showAttachmentUrl" namespace="/others" action="attachment" method="showAttachment"></s:url>

<div class="actionErrors" ref="infoForm"></div>
<div class="actionMessages" ref="infoForm"></div>

<div class="body">
	<s:form theme="simple" id="infoForm" namespace="/general" action="inventory" method="post" cssClass="ajaxForm" >
		<fieldset id="infoFieldset" name="infoFieldset" class="fieldsetStyle02">
			<legend><s:text name="fieldset.legend.cloth.detail" /></legend>
			
			<ul>
				<li>
					<label for="staffCode"><s:text name="staff.code"></s:text>: </label>
					<s:textfield theme="simple" id="staffCode" name="staffCode" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property value="cloth.staff.code" />
						</s:param>
					</s:textfield>
				</li>
				<li>
					<label for="staffDeptName"><s:text name="staff.dept"></s:text>: </label>
					<s:textfield theme="simple" id="staffDeptName" name="staffDeptName" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
								<s:property escape="false" value="cloth.staff.dept.nameCht" />
							</s:if>
							<s:else>
								<s:property escape="false" value="cloth.staff.dept.nameEng" />
							</s:else>
						</s:param>
					</s:textfield>
				</li>
				
				<li>
					<label for="staffNameCht"><s:text name="staff.name.cht"></s:text>: </label>
					<s:textfield theme="simple" id="staffNameCht" name="staffNameCht" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property escape="false" value="cloth.staff.nameCht" />
						</s:param>
					</s:textfield>
				</li>
				<li>
					<label for="staffNameEng"><s:text name="staff.name.eng"></s:text>: </label>
					<s:textfield theme="simple" id="staffNameEng" name="staffNameEng" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property escape="false" value="cloth.staff.nameEng" />
						</s:param>
					</s:textfield>
				</li>
				
				<li>
					<label for="clothType"><s:text name="clothType.clothType"></s:text>: </label>
					<s:textfield theme="simple" name="clothType" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property escape="false" value="cloth.clothType.name"/>
						</s:param>
					</s:textfield>
				</li>
				<li>
					<label for="clothSize"><s:text name="cloth.size"></s:text>: </label>
					<s:textfield theme="simple" name="clothSize" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property value="cloth.size"/>
						</s:param>
					</s:textfield>
				</li>
				
				<li>
					<label for="clothCode"><s:text name="cloth.code"></s:text>: </label>
					<s:textfield theme="simple" name="clothCode" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property value="cloth.code"/>
						</s:param>
					</s:textfield>
				</li>
				<li>
					<label for="clothStatus"><s:text name="cloth.status"></s:text>: </label>
					<s:textfield theme="simple" name="clothStatus" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property value="%{getText(cloth.clothStatus.value)}" escape="false"/>
						</s:param>
					</s:textfield>
				</li>
				
				<li>
					<label for="clothRfid"><s:text name="label.rfid"></s:text>: </label>
					<s:textfield theme="simple" name="clothRfid" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property value="cloth.rfid"/>
						</s:param>
					</s:textfield>
				</li>
				<li>
					<label for="clothLocation"><s:text name="cloth.location"></s:text>: </label>
					<s:textfield theme="simple" name="clothLocation" cssClass="displayText" readonly="true">
						<s:param name="value">
							<s:property value="cloth.zone.code"/>
						</s:param>
					</s:textfield>
				</li>
				
				<li>
					<label for="clothDesc"><s:text name="label.description"/>:</label>
					<s:textarea theme="simple" name="clothDesc" cssClass="displayText" readonly="true">
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
			<s:submit theme="simple" id="searchButtonHidden" key="btn.search" method="getInventoryClothHistory" cssClass="btnHidden ajaxSearchDialogButton"></s:submit>
		</div>
	</s:form>
</div>



<script>
$(function(){
	$("#searchButtonHidden").trigger("click");
});
</script>