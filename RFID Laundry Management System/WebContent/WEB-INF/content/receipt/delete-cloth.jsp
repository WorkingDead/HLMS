<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="actionErrors" ref="deleteClothForm"></div>
<div class="actionMessages" ref="deleteClothForm"></div>
		
<s:form theme="simple" id="deleteClothForm" namespace="/general" action="receipt" method="post" cssClass="ajaxForm" >
	<fieldset id="deleteClothFieldset" name="deleteClothFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.cloth.detail" /></legend>
		
		<s:hidden theme="simple" id="popupBoxReceiptId" name="receiptId" />
		
		<ul>
			<%--
			<li>
				<label for="receipt.code"><s:text name="receipt.code"/>:</label>
				<s:textfield theme="simple" name="receipt.code" readonly="true" cssClass="displayText"/>
			</li>
			
			<li>
				<label for="receipt.receiptType"><s:text name="receipt.type"/>:</label>
				<s:textfield theme="simple" name="receipt.receiptType" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:property value="%{getText(receipt.receiptType.value)}"/>
					</s:param>
				</s:textfield>
			</li>
			--%>
			
			<li>
				<label for="staff.code"><s:text name="staff.code"/>: </label>
				<s:textfield theme="simple" name="staff.code" readonly="false" cssClass="inputText" />
			</li>
			
			<li>
				<label for="clothType.id"><s:text name="cloth.type"/>: </label>
				<s:select
					theme="simple"
					name="clothType.id" 
					list="clothTypeList"
					listKey="id"
					listValue="name"
					emptyOption="true"
					cssClass="inputText"
				 />
			</li>
			
			<li>
				<label for="cloth.code"><s:text name="cloth.code"/>: </label>
				<s:textfield theme="simple" name="cloth.code" readonly="false" cssClass="inputText" />
			</li>
		</ul>
	</fieldset>
		
	<div class="buttonArea">
		<s:submit theme="simple" id="btnSave" method="removeClothFromRollContainer" key="btn.remove" cssClass="button blue buttonMargin ajaxButton"/>
	</div>
</s:form>
