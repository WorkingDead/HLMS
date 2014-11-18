<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.receipt.detail" /></h3>

<div class="body">

<!-- ################################################## -->
<!-- Receipt Info Fieldset -->
<!-- ################################################## -->
	<fieldset id="infoFieldset" name="infoFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.generalinfo"/></legend>
		
		<s:hidden theme="simple" id="hiddenReceiptId" name="receipt.id" />
		
		<ul>
			<li>
				<label for="receipt.code"><s:text name="receipt.code"/>:</label>
				<s:textfield theme="simple" name="receipt.code" cssClass="displayText" readonly="true"/>
			</li>
			
			<li>
				<label for="receipt.receiptType"><s:text name="receipt.type"/>:</label>
				<s:textfield theme="simple" name="receipt.receiptType" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:property value="%{getText(receipt.receiptType.value)}" escape="false"/>
					</s:param>
				</s:textfield>
			</li>
			
			<li>
				<label for="tbCreationDate"><s:text name="label.creation.date"/>:</label>
				<s:textfield theme="simple" name="tbCreationDate" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:date name="receipt.creationDate" format="dd-MM-yyyy"/>
					</s:param>
				</s:textfield>
			</li>
			
			<li>
				<label for="tbCreationTime"><s:text name="label.time"/>:</label>
				<s:textfield theme="simple" name="tbCreationTime" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:date name="receipt.creationDate" format="HH:mm:ss"/>
					</s:param>
				</s:textfield>
			</li>
			
			<%--
			<li>
				<label for="receipt.receiptClothTotal"><s:text name="receipt.cloth.total"/>:</label>
				<s:textfield theme="simple" name="receipt.receiptClothTotal" cssClass="displayText" readonly="true"/>
			</li>
			--%>
			
			<li>
				<label for="receipt.receiptStatus"><s:text name="receipt.status"/>:</label>
				<s:textfield theme="simple" name="receipt.receiptStatus" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:property value="%{getText(receipt.receiptStatus.value)}" escape="false"/>
					</s:param>
				</s:textfield>
			</li>
			
			<li>
				<label for="receipt.createdBy.username"><s:text name="label.handled.by.user"/>:</label>
				<s:textfield theme="simple" name="receipt.createdBy.username" cssClass="displayText" readonly="true" />
			</li>
			
			<li>
				<label for="receipt.staffHandledBy.nameEng"><s:text name="label.handled.by.staff"/>:</label>
				<s:textfield theme="simple" name="receipt.staffHandledBy.nameEng" cssClass="displayText" readonly="true" />
			</li>
			
			<li>
				<label for="receipt.staffPickedBy.nameEng"><s:text name="label.picked.by.staff"/>:</label>
				<s:textfield theme="simple" name="receipt.staffPickedBy.nameEng" cssClass="displayText" readonly="true" />
			</li>
		</ul>
	</fieldset>



<!-- ################################################## -->
<!-- Cloth Summary Fieldset -->
<!-- ################################################## -->
	<fieldset id="clothSummaryFieldset" name="clothSummaryFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.cloth.summary"/></legend>
		
		<div class="RFIDTotal">
			<table id="clothTotalTable">
				<thead>
					<tr><td><s:text name="label.cloth.total"/></td></tr>
				</thead>
				<tbody>
					<tr>
						<td class="bigText">
							<s:property value="receipt.receiptClothTotal"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
				
		<div class="RFIDSummary">
			<table id="clothSummaryTable">
				<thead>
					<tr>
						<td><s:text name="clothType.clothType"/></td>
						<td><s:text name="label.type.total"/></td>
					</tr>
				</thead>
				
				<tbody>
				
					<s:iterator value="clothTypeCountList">
						<tr>
							<td><s:property value="type"/></td>
							<td><s:property value="num"/></td>
						</tr>
					</s:iterator>
					
				</tbody>
			</table>	
		</div>
		
	</fieldset>

<!-- ################################################## -->
<!-- Pattern Fieldset -->
<!-- ################################################## -->

	<s:form theme="simple" id="patternForm" namespace="/general" action="receipt" method="post">
		
		<fieldset id="patternFieldset" name="patternFieldset" class="fieldsetStyle01">
			<legend><s:text name="fieldset.legend.roll.container.summary"/></legend>
			
			<div class="alignRight">
				<s:if test="receipt.receiptStatus.toString() == 'Finished'">
					<s:submit theme="simple" id="btnEdit" method="getEditPatternPage" key="btn.view" disabled="false" cssClass="button blue"></s:submit>
					<s:submit theme="simple" type="button" id="btnRemoveCloth" key="btn.remove" disabled="true" cssClass="button rosy"/>
				</s:if>
				<s:else>
					<s:submit theme="simple" id="btnEdit" method="getEditPatternPage" key="btn.edit" cssClass="button blue"></s:submit>
					<s:submit theme="simple" type="button" id="btnRemoveCloth" key="btn.remove.cloth" cssClass="button rosy"/>
				</s:else>
			</div>
			
			<table id="patternTable" class="dataTable">
				<thead>
					<tr>
						<th class="simpleCheckBox">
							<s:checkbox theme="simple" 
								id="cbCheckAll" 
								name="cbCheckAll" 
								cssClass="simpleCheckBox checkall" ref="cbCheckAll"></s:checkbox>
						</th>
						<th><s:text name="roll.container.code"/></th>
						<th><s:text name="label.cloth.total"/></th>
						<th><s:text name="label.ironing.delivery.date"/></th>
						<th><s:text name="label.ironing.delivery.time"/></th>
					</tr>
				</thead>
				
				<tbody>
					<s:iterator value="receiptPatternIronList">
						<tr>
							<td>
								<s:checkbox theme="simple" 
									id="selected_pattern_id_[%{id}]"
									name="selectedPatternMarker" 
									fieldValue="%{id}" 
									cssClass="simpleCheckBox checkall2 selectedPatternMarker" checkall="cbCheckAll">
								</s:checkbox>
							</td>
							<td><s:property value="rollContainer.code"/></td>
							<td><s:property value="patternClothTotal"/></td>
							<td><s:date name="ironingDeliveryTime" format="dd-MM-yyyy"/></td>
							<td><s:date name="ironingDeliveryTime" format="HH:mm:ss"/></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
			
		</fieldset>

		<div class="buttonArea">
			
			<%--
			<s:if test="receipt.receiptStatus.toString() == 'Finished'">
				<s:submit theme="simple" id="btnEdit" method="getEditPatternPage" key="btn.edit" disabled="true" cssClass="button blue buttonMargin"></s:submit>
			</s:if>
			<s:else>
				<s:submit theme="simple" id="btnEdit" method="getEditPatternPage" key="btn.edit" cssClass="button blue buttonMargin"></s:submit>
			</s:else>
			--%>
			
			<s:submit theme="simple" type="button" id="btnBack" key="btn.back" cssClass="button rosy buttonMargin"/>
		</div>
	</s:form>
</div>


<%-- Popup Box --%>
<div class="hidden" id="removeClothDialog" title="<s:text name="label.remove.cloth"/>"></div>


<script>
var popUpBox = undefined;

$(function(){
	
	///////////////////////////////////////////////////////////////////
	// Select a roll container (pattern) and get its content page
	///////////////////////////////////////////////////////////////////
	<s:url var="getRemoveClothPageUrl" namespace="/general" action="receipt" method="getRemoveClothPage"/>
	<s:url var="getDetailPageUrl" namespace="/general" action="receipt" method="getDetailPage"/>
	$("#btnRemoveCloth").on("click", function(){
	
		var popupUrl = "<s:property value="#getRemoveClothPageUrl"/>";
		popupUrl += "?selectedReceiptList[0].id=" + $("#hiddenReceiptId").val();
		popUpBox = $("#removeClothDialog").load( popupUrl ).dialog({
					
						modal: true,
						width: 550,
						height: 300,
		
						close: function()
						{
							ajaxIgnoreClick = true; // prevent the action message keep show until user click

							var url = "<s:property value="#getDetailPageUrl"/>";
							url += "?selectedReceiptList[0].id=" + $("#hiddenReceiptId").val();
							
							$(location).attr('href', url);
							return false;
						}
		}).css("font-size", "15px");

		return false;
	});

	// Close the popup box after delete cloth from container
	$("#deleteClothForm").live("deleteClothForm.success", function(){

 		if( popUpBox != undefined )
 			popUpBox.dialog( "close" );
	});
	
	
	///////////////////////////////////////////////////////////////////
	// Select a roll container (pattern) and get its content page
	///////////////////////////////////////////////////////////////////
	<s:url var="getEditPatternPageUrl" namespace="/general" action="receipt" method="getEditPatternPage"/>
	$("#btnEdit").on("click", function(){
		
		var n = $(".selectedPatternMarker:checked").length;
		
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
		else
		{
			// [Warning] This method is not support by IE9 or Chrome
			/*
			$(".selectedPatternMarker:checked").each(function() {
				$(this).attr("name", "selectedPatternList[0].id");	// change the name of checkbox to backend var
			});
			
			$("#patternForm").submit();		// this is not by IE9 or Chrome
			*/
			
			var patternId = $(".selectedPatternMarker:checked:first").val();
			var url = "<s:property value="#getEditPatternPageUrl"/>";
			url += "?selectedPatternList[0].id=" + patternId;
			
			$(location).attr('href', url);
			return false;
			
		}
	});
	
	///////////////////////////////////////////////////////////////////
	// Back to previous page
	///////////////////////////////////////////////////////////////////
	<s:url var="getListPageUrl" namespace="/general" action="receipt" method="getListPage"/>
	$("#btnBack").click(function() {
		$(location).attr('href',"<s:property value="#getListPageUrl"/>");
		return false;
	});
	
});
</script>



