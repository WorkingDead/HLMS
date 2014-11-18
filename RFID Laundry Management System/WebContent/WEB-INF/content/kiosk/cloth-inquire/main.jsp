<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<div class="body">
	<s:hidden theme="simple" id="pageName" value="page-cloth-inquire"/>

	<s:form theme="simple" namespace="/master" action="cloth" method="post" cssClass="ajaxSearchForm" listtable="resultTable">

		<fieldset id="searchFieldset" name="searchFieldset" class="kioskFieldset fieldsetStyle01">
			<legend><s:text name="fieldset.legend.search.criteria" /></legend>
			<ul>
				<li>
					<label for="staff.dept.id"><s:text name="staff.dept"/>:</label>
					<s:select id="staffDeptSelect"
						theme="simple"
						name="staff.dept.id" 
						list="emptyList"
						emptyOption="true"
						cssClass="inputText"
					/>
				</li>

				<li>
					<label for="staff.code"><s:text name="staff.code"/>:</label>
					<s:textfield theme="simple" id="staffCode" name="staff.code" cssClass="inputText"/>
					<img id="staffCodeMicrosoftVirtualKeyboard" src="<s:property value="imagesPath"/>layout/Keyboard.png" style="visibility:hidden" />
				</li>

				<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
					<li>
						<label for="staff.nameCht"><s:text name="staff.name"/>:</label>
						<s:textfield theme="simple" id="staffNameCht" name="staff.nameCht" cssClass="inputText"/>
						<img id="staffNameChtMicrosoftVirtualKeyboard" src="<s:property value="imagesPath"/>layout/Keyboard.png" style="visibility:hidden" />
					</li>
				</s:if>
				<s:else>
					<li>
						<label for="staff.nameEng"><s:text name="staff.name"/>:</label>
						<s:textfield theme="simple" id="staffNameEng" name="staff.nameEng" cssClass="inputText"/>
						<img id="staffNameEngMicrosoftVirtualKeyboard" src="<s:property value="imagesPath"/>layout/Keyboard.png" style="visibility:hidden" />
					</li>
				</s:else>

				<li>
					<label for="clothType.id"><s:text name="cloth.type"/>:</label>
					<s:select id="clothTypeSelect"
						theme="simple"
						name="clothType.id" 
						list="emptyList"
						emptyOption="true"
						cssClass="inputText"
					 />
				</li>

				<li>
					<label for="cloth.clothStatus"><s:text name="cloth.status"/>:</label>
					<s:select
						theme="simple"
						name="cloth.clothStatus" 
						list="clothStatusList"
						listValue="getText(value)"
						emptyOption="true"
						cssClass="inputText"
					 />
				</li>

				<li>
					<label for="cloth.code"><s:text name="cloth.code"/>:</label>
					<s:textfield theme="simple" id="clothCode" name="cloth.code" cssClass="inputText"/>
					<img id="clothCodeMicrosoftVirtualKeyboard" src="<s:property value="imagesPath"/>layout/Keyboard.png" style="visibility:hidden" />
				</li>
				
				<li>
					<label for="zone.id"><s:text name="cloth.location"/>:</label>
					<s:select
						theme="simple"
						name="zone.id" 
						list="zoneList"
						listKey="id"
						listValue="code"
						emptyOption="true"
						cssClass="inputText"
					 />
				</li>
			</ul>
		</fieldset>
	
		<div class="buttonArea">
			<s:submit theme="simple" id="searchButton" key="btn.search" method="getKioskSearchResultPage" cssClass="kioskButton blue buttonMarginCorner ajaxSearchButton"></s:submit>
			<s:reset theme="simple" id="btnReset" method="" key="btn.reset" cssClass="kioskButton rosy buttonMarginCorner"/>
		</div>
	</s:form>

	<s:form theme="simple" namespace="/kiosk" action="cloth-inquire" method="post">
		<fieldset id="resultFieldset" name="resultFieldset" class="kioskFieldset fieldsetStyle01">
			<legend><s:text name="fieldset.legend.search.result" /></legend>
			
			<!-- Retrieve Data Table -->
			<div id="resultTable" class="tableOfInfo1">
				<img alt="loading" class="indicator" src="<s:property value="imagesPath"/>layout/ajax-load.gif" />
				<!-- will auto load from ajax -->
			</div>
			<!-- End of Retrieve Data Table -->
		</fieldset>
		
		<div class="buttonArea">
			<s:submit theme="simple" id="btnView" key="btn.view" cssClass="kioskButton blue buttonMarginCorner"></s:submit>
		</div>
	</s:form>
</div>

<%-- Dialogs --%>
<div class="hidden" id="infoDialog" title="<s:text name="cloth.info"/>"></div>

<script>

var popUpBox = undefined;

//////////////////////////////////////////////////////////////////////
//dom ready (HTML Document Load Complete)
//////////////////////////////////////////////////////////////////////
$(function(){
	departmentListDefault( $("#staffDeptSelect") );
	clothTypeListDefault( $("#clothTypeSelect") );
	openInfoForm();
	closeInfoForm();
	$("#searchButton").trigger("click");
	
	listenToSupportMicrosoftVirtualKeyboard( $("#staffCode"), $("#staffCodeMicrosoftVirtualKeyboard") );
	
	<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
		listenToSupportMicrosoftVirtualKeyboard( $("#staffNameCht"), $("#staffNameChtMicrosoftVirtualKeyboard") );
	</s:if>
	<s:else>
		listenToSupportMicrosoftVirtualKeyboard( $("#staffNameEng"), $("#staffNameEngMicrosoftVirtualKeyboard") );
	</s:else>

	listenToSupportMicrosoftVirtualKeyboard( $("#clothCode"), $("#clothCodeMicrosoftVirtualKeyboard") );
});


function openInfoForm() {

	<s:url var="getKioskDetailPage" namespace="/master" action="cloth" method="getKioskDetailPage"></s:url>
	
	$("#btnView").on("click", function(){
		var n = $("input[name='cloth.id']:checked").length;

		if (n == 0)
		{
			alertDialog(
					"",
					"<s:text name="dialog.errors.nothing.select"/>",
					"<s:text name="btn.ok"/>");
			return false;
		}
		else if (n > 1)
		{
			alertDialog(
					"",
					"<s:text name="dialog.errors.select.one.only"/>",
					"<s:text name="btn.ok"/>");
			return false;
		}
	
		var id = $("input[name='cloth.id']:checked").val();
		popUpBox = $("#infoDialog").load(
						"<s:property value="#getKioskDetailPage"/>",
						"cloth.id=" + id).dialog({
				modal: true,
				
				width: 600,
				height: 850,

				position: { my: "center", at: "top", of: window },
				
				
				buttons: {
					"close" :{
						text: "<s:text name="btn.close"/>",
						style: "width: 120px; font-size: 12px; padding: 2px",
						click: function(){
							$(this).dialog("close");
							$(this).dialog("destroy");
						}
					}
				},

				close: function() {
				}
		}).css("font-size", "12px");

		return false;
	});
}

function closeInfoForm() {

	//Edit Form handler
	$("#infoForm").live("infoForm.success", function(){

 		if( popUpBox != undefined )
 			popUpBox.dialog( "close" );
	});
}

</script>