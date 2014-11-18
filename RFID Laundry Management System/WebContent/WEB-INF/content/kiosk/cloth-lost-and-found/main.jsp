<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<s:set var="lost" value="@web.actions.kiosk.ClothLostAndFoundAction$LostFoundWay@lost"/>
<s:set var="found" value="@web.actions.kiosk.ClothLostAndFoundAction$LostFoundWay@found"/>

<div class="body">

	<s:if test="%{ #lost.toString().indexOf( #parameters.way ) == 0 }">
		<s:hidden theme="simple" id="pageName" value="page-cloth-lost"/>
	</s:if>
	<s:else>
		<s:hidden theme="simple" id="pageName" value="page-cloth-found"/>
	</s:else>


	<s:form theme="simple" id="newForm" namespace="/kiosk" action="cloth-lost-and-found" method="post" cssClass="ajaxForm">
		<s:hidden theme="simple" id="hiddenKioskName" name="kioskName"/>
		<s:hidden theme="simple" name="staff.id" id="staffID"/>

		<fieldset id="clothLostFieldset" name="clothLostFieldset" class="kioskFieldset fieldsetStyle01">

			<div class="actionErrors" ref="newForm"></div>
			<div class="actionMessages" ref="newForm"></div>

			<s:if test="%{ #lost.toString().indexOf( #parameters.way ) == 0 }">
				<legend><s:text name="fieldset.legend.cloth.lost"/></legend>
			</s:if>
			<s:else>
				<legend><s:text name="fieldset.legend.cloth.found"/></legend>
			</s:else>

			<div class="alignRight">
				<s:submit type="button" theme="simple" id="staffInquireButton" key="btn.inquire" cssClass="kioskButton gray buttonMarginCorner" />
			</div>
			
			<ul>
				<li>
					<label for="staff.cardNumber"><s:text name="staff.card"/>: </label>
					<s:textfield theme="simple" id="staffCardNumber" name="staff.cardNumber" cssClass="inputText" />
				</li>
				
				<li>
					<label for="staff.code"><s:text name="staff.code"/>: </label>
					<s:textfield theme="simple" id="staffCode" name="staff.code" cssClass="inputText" />
					<img id="staffCodeMicrosoftVirtualKeyboard" src="<s:property value="imagesPath"/>layout/Keyboard.png" style="visibility:hidden" />
				</li>
				
				<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
					<li>
						<label for="staff.nameCht"><s:text name="staff.name"/>: </label>
						<s:textfield theme="simple" name="staff.nameCht" readonly="true" cssClass="displayText" id="staffName"/>
					</li>

					<li>
						<label for="staff.dept.nameCht"><s:text name="staff.dept"/>: </label>
						<s:textfield theme="simple" name="staff.dept.nameCht" readonly="true" cssClass="displayText" id="staffDeptName"/>
					</li>
				</s:if>
				<s:else>
					<li>
						<label for="staff.nameEng"><s:text name="staff.name"/>: </label>
						<s:textfield theme="simple" name="staff.nameEng" readonly="true" cssClass="displayText" id="staffName"/>
					</li>
					
					<li>
						<label for="staff.dept.nameEng"><s:text name="staff.dept"/>: </label>
						<s:textfield theme="simple" name="staff.dept.nameEng" readonly="true" cssClass="displayText" id="staffDeptName"/>
					</li>
				</s:else>

				<li>
					<label for="clothType.id"><s:text name="cloth.type"/>:</label>
					<s:select id="clothTypeSelect"
						theme="simple"
						name="clothType.id" 
						list="emptyList"
						emptyOption="true"
						disabled="true"
						cssClass="inputText"
					 />
				</li>

				<li>
					<label for="cloth.id"><s:text name="cloth.code"/>:</label>
					<s:select id="clothSelect"
						theme="simple"
						name="cloth.id" 
						list="emptyList"
						emptyOption="true"
						disabled="true"
						cssClass="inputText"
					 />
				</li>
				
<!-- 

Try More
http://stackoverflow.com/questions/11660193/struts2-comparing-2-variable-in-if-tag-not-working
http://stackoverflow.com/questions/4996905/comparing-two-valuestack-string-values-in-jsp-struts2

 -->
<!-- 				<li> -->
<%-- 					<s:property value="%{#parameters.way}" /> --%>
<!-- 				</li> -->
				
<!-- 				<li> -->
<%-- 					<s:property value="%{#lost.toString()}" /> --%>
<!-- 				</li> -->
			</ul>
		</fieldset>

		<div class="buttonArea">
			<s:if test="%{ #lost.toString().indexOf( #parameters.way ) == 0 }">
				<s:submit theme="simple" id="btnLost" method="lost" key="btn.lost" cssClass="kioskButton blue buttonMarginCorner ajaxButton"/>
			</s:if>
			<s:else>
				<s:submit theme="simple" id="btnFound" method="found" key="btn.found" cssClass="kioskButton blue buttonMarginCorner ajaxButton"/>
			</s:else>

			<s:reset theme="simple" id="btnReset" method="XXXXXXX" key="btn.reset" cssClass="kioskButton rosy buttonMarginCorner"/>
<%-- 			<s:submit theme="simple" type="button" id="btnReset" key="btn.reset" method="XXXXXXX" cssClass="kioskButton rosy buttonMarginCorner" /> --%>
		</div>
	</s:form>
</div>

<script>

//////////////////////////////////////////////////////////////////////
// dom ready (HTML Document Load Complete)
//////////////////////////////////////////////////////////////////////
$(function(){
	initialPageSetting();
});

function initialPageSetting()
{
	clothTypeListKioskLostFound( $("#clothTypeSelect"), null );

	staffInquireButton();
 	
	twoRadioedTextFieldsDefault(
			$("#staffCardNumber"), 
			function(){ 
				$("#staffCode").val("");
				displayStaffData( null );
				
				$("#clothSelect").attr('disabled','disabled');
				$("#clothTypeSelect").attr('disabled','disabled');
			},
			
			$("#staffCode"), 
			function(){ 
				$("#staffCardNumber").val("");
				displayStaffData( null );
				
				$("#clothSelect").attr('disabled','disabled');
				$("#clothTypeSelect").attr('disabled','disabled');
			} 
	);

 	staffCodeAutoComplete();
 	clothTypeSelectionChange();
	resetFormWhenSuccess();
	resetFormImpl();
	
	$("#staffCardNumber").focus();
	
	listenToSupportMicrosoftVirtualKeyboard( $("#staffCode"), $("#staffCodeMicrosoftVirtualKeyboard") );
}

function staffInquireButton()
{
	<s:url var="getKioskStaffByCardNumber" namespace="/master" action="staff" method="getKioskStaffByCardNumber"/>
	
	$("#staffInquireButton").on('click', function(){

		var dataString = '';
		if ( $('#staffCardNumber').val() && typeof $('#staffCardNumber').val() !== 'undefined' )
		{
			$('#staffCardNumber').val( $.trim( $('#staffCardNumber').val() ) );
			dataString = dataString + 'dummyStaffForAC.cardNumber='+ $('#staffCardNumber').val();
		}
		else
		{
			$('#staffCode').val( $.trim( $('#staffCode').val() ) );
			dataString = dataString + 'dummyStaffForAC.code='+ $('#staffCode').val();
		}

		$.ajax({
				type: "POST",
				url: "<s:property value="#getKioskStaffByCardNumber"/>",
				dataType: "json",
				data: dataString,
				cache: false,
				success: function(data) {
					
					if ( data && typeof data !== 'undefined' ) {
						
						$.each(data, function(key, value) {
							
							if ( value && typeof value !== 'undefined' )
							{
								displayStaffData( value );
								$("#clothTypeSelect").removeAttr('disabled');
								$("#clothSelect").attr('disabled','disabled');
							}
							else
							{
								displayStaffData( null );
								$("#clothTypeSelect").attr('disabled','disabled');
								$("#clothSelect").attr('disabled','disabled');
							}
							
							return false;
						});
					}
					else {
						displayStaffData( null );
					}
				}
		});

		return false;
	});
}

function staffCodeAutoComplete()
{
	<s:url var="getKioskStaffCodeAutoCompleteList" namespace="/master" action="staff" method="getKioskStaffCodeAutoCompleteList"/>
	
	// auto complete
	$("#staffCode").autocomplete({
		source: function(request, response){		
			$.ajax({
				url: "<s:property value="#getKioskStaffCodeAutoCompleteList"/>",
				dataType: "json",
				cache: false,
				data: {
					"dummyStaffForAC.code" : $.trim(request.term)
				},
				success: function(data) {
					response( $.map( data, function( item ) {
						return {
							value: item.code, 
							id: item.id,
							code: item.code,
							nameEng: item.nameEng,
							nameCht: item.nameCht,
							dept: item.dept,
							
							<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
								label: item.code + " | " + item.nameCht
							</s:if>
							<s:else>
								label: item.code + " | " + item.nameEng
							</s:else>
						}
					}));
				}
			});	
		},
		minLength: 2,
		select: function( event, ui ) {
			$(this).val( $.trim( $(this).val() ) );
		}
	});
}

function clothTypeSelectionChange() {

	$("#clothTypeSelect").bind('change', function() {

		if ( $("#clothTypeSelect").val() == null || $("#clothTypeSelect").val() =="" )
		{
			$("#clothSelect").html("");
			$("#clothSelect").attr('disabled','disabled');
		}
		else
		{
			suggestedClothList( $("#clothTypeSelect").val() );
			$("#clothSelect").removeAttr('disabled');
		}
	});
}

//Get Cloth Type Suggestion Data
function suggestedClothList( val ) {

	<s:url var="getKioskSuggestedClothJsonResultUrl" namespace="/master" action="cloth" method="getKioskSuggestedClothJsonResult"/>
	
	var url = "<s:property value="#getKioskSuggestedClothJsonResultUrl"/>";
	var data = "clothType.id=" + val;
	if ( $("#staffID") && typeof $("#staffID") !== 'undefined' )
		data = data + "&staff.id=" + $("#staffID").val();
	
	$.post(url, data, function(result) {

		if( result != undefined ) {

			if (result.actionErrors != undefined) {
				alertDialog("", result.actionErrors, "<s:text name="btn.ok"/>");
			}
			else if (result.fieldErrors != undefined) {
				alertDialog("", result.fieldErrors, "<s:text name="btn.ok"/>");
			}
			else if (result.errors != undefined) {
				alertDialog("", result.errors, "<s:text name="btn.ok"/>");
			}
			else if (result.actionMessages != undefined) {
				alertDialog("", result.actionMessages, "<s:text name="btn.ok"/>");
			}
			else {

				$("#clothSelect").html("");
				var opts = '<option value=""></option>';
				$.each(result.clothList, function(key, value) {
					opts += '<option value="' + value.id + '">' + value.code + '</option>';
					});
				$("#clothSelect").html(opts);
			}
		}
		else {
			alertDialog("", "Error!", "<s:text name="btn.ok"/>");
		}			

	}, "json").error(function(jqXHR, textStatus, errorThrown) {
		alertDialog("", "could not delete record.", "<s:text name="btn.ok"/>");
	});
}
//Get Cloth Type Suggestion Data

function displayStaffData(value) {
	
	if ( value && typeof value !== 'undefined' )
	{
		$("#staffID").val(value.id);
		<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
			$("#staffName").val(value.nameCht);
			$("#staffDeptName").val(value.dept.nameCht);
		</s:if>
		<s:else>
			$("#staffName").val(value.nameEng);
			$("#staffDeptName").val(value.dept.nameEng);
		</s:else>
		
		clothTypeListKioskLostFound( $("#clothTypeSelect"), $("#staffID") );
	}
	else
	{
		$("#staffID").val('');
		<s:if test="%{ #currentSystemLanguage.toString().equals( #SystemLanguagezhCN.toString() ) }">
			$("#staffName").val('');
			$("#staffDeptName").val('');
		</s:if>
		<s:else>
			$("#staffName").val('');
			$("#staffDeptName").val('');
		</s:else>
		
		clothTypeListKioskLostFound( $("#clothTypeSelect"), null );
	}

	$('#clothSelect').val('').trigger('liszt:updated');
}

function resetFormWhenSuccess() {
	
	//Create Form handler
	//This statement cannot get the result variable
 	$("#newForm").live("newForm.success", function(){

		//$("#btnReset").trigger("click");		//Don't know why this has no effect
		$("#newForm").trigger("reset");			//This statement cannot reset the file uploader
		
		//Prevent re-submit by clicking the submit button again
// 		$("#staffID").val('');
 	});
	//Create Form handler
}

function resetFormImpl()
{
	$("#newForm").bind("reset", function(){
		$("#staffID").val('');
		clothTypeListKioskLostFound( $("#clothTypeSelect"), null );
		$("#clothSelect").html("");
		
		$("#clothSelect").attr('disabled','disabled');
		$("#clothTypeSelect").attr('disabled','disabled');
		
		$("#staffCardNumber").focus();
	});
}

</script>