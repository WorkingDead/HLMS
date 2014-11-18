<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="actionErrors" ref="editClothForm"></div>
<div class="actionMessages" ref="editClothForm"></div>
		
<s:form theme="simple" id="editClothForm" namespace="/master" action="staff" method="post" cssClass="ajaxForm" >
	<fieldset id="editClothFieldset" name="editClothFieldset" class="fieldsetStyle01">
		<legend><s:text name="fieldset.legend.cloth.detail" /></legend>
		
		<s:hidden theme="simple" id="popupBoxStaffId" name="staff.id" />
		<s:hidden theme="simple" id="popupBoxClothId" name="cloth.id" />
		
		<ul>
			<li>
				<label for="popupClothCode"><s:text name="cloth.code"/>: </label>
				<s:textfield theme="simple" id="popupClothCode" name="cloth.code" readonly="false" cssClass="inputText" />
			</li>
			
			<li>
				<label for="popupClothType"><s:text name="cloth.type"/>: </label>
				<s:select theme="simple" 
					id="popupClothType" 
					name="cloth.clothType.id" 
					list="clothTypeList" 
					listKey="id" 
					listValue="name" 
					emptyOption="false"
					cssClass="tblUniformType" />
			</li>

			<li>
				<label for="popupClothSize"><s:text name="cloth.size"/>: </label>
				<s:select theme="simple" 
					id="popupClothSize" 
					name="cloth.size"
					list="clothSizeList"
					cssClass="tblUniformSize" />
			</li>


			<li>
				<label for="popupClothRfid"><s:text name="label.rfid"/>: </label>
				<s:textfield theme="simple" id="popupClothRfid" name="cloth.rfid" readonly="false" cssClass="inputText" />
			</li>
			
			<li>
				<label for="popupClothRemark"><s:text name="label.remark"/>: </label>
				<s:textfield theme="simple" id="popupClothRemark" name="cloth.remark" readonly="false" cssClass="inputText" />
			</li>
		</ul>
	</fieldset>
	
	<div class="buttonArea">
		<s:submit theme="simple" id="btnSave" method="updateClothOfStaff" key="btn.save" cssClass="btnHidden ajaxButton"/>
	</div>
</s:form>
