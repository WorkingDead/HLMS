<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<s:set var="SystemLanguageEnUS" value="@web.actions.BaseAction$SystemLanguage@en_US"/>
<s:set var="SystemLanguagezhCN" value="@web.actions.BaseAction$SystemLanguage@zh_CN"/>
<s:set var="currentSystemLanguage" value="getCurrentSystemLanguage()"/>

<script>

	var SystemLanguage = {};
	<s:iterator value="systemLanguageList" var="systemLanguage">
		SystemLanguage.<s:property value="#systemLanguage"/> = {value: "<s:property value="#systemLanguage"/>", systemResourceKey: "<s:property value="#systemLanguage.systemResourceKey"/>", kioskResourceKey: "<s:property value="#systemLanguage.kioskResourceKey"/>"};
		SystemLanguage.<s:property value="#systemLanguage"/>.toString = function() {
		    return "<s:property value="#systemLanguage"/>";
		};
	</s:iterator>

	var currentSystemLanguage = "<s:property value="%{#currentSystemLanguage}"/>";
	
	var uploadFileButton = "<s:property value="%{ getText('btn.uploadFile') }"/>";
	var attachmentErrorTypeError = "<s:property value="%{ getText('attachment.error.typeError') }"/>";
	var attachmentErrorSizeError = "<s:property value="%{ getText('attachment.error.sizeError') }"/>";
	var attachmentErrorMinSizeError = "<s:property value="%{ getText('attachment.error.minSizeError') }"/>";
	var attachmentErrorEmptyError = "<s:property value="%{ getText('attachment.error.emptyError') }"/>";
	var attachmentErrorOnLeave = "<s:property value="%{ getText('attachment.error.onLeave') }"/>";

//  	alert("SystemLanguage.en_US = " + SystemLanguage.en_US);
//  	alert("SystemLanguage.zh_CN = " + SystemLanguage.zh_CN);

//  	alert("currentSystemLanguage = " + currentSystemLanguage);
 	
//  	if ( currentSystemLanguage == SystemLanguage.zh_CN )
//  		alert("Here is zh_CN");
//  	if ( currentSystemLanguage == SystemLanguage.en_US )
//  		alert("Here is en_US");



///////////////////////////////////////////////////////////////
// Two Radioed Text Fields Core
///////////////////////////////////////////////////////////////
//DEFAULT
function twoRadioedTextFieldsDefault(elFirst, elFirstRadioAction, elSecond, elSecondRadioAction) {
	
	var textFieldChangeDetectionDefault = "keyup paste";

	if ( elFirst && typeof elFirst !== 'undefined' && elSecond && typeof elSecond !== 'undefined' ) {
		
		elFirst.on(textFieldChangeDetectionDefault, function(event){
			elFirstRadioAction();
		});
		
		elSecond.on(textFieldChangeDetectionDefault, function(event){
			elSecondRadioAction();
		});
	}
}
//DEFAULT
///////////////////////////////////////////////////////////////
// Two Radioed Text Fields Core
///////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////
// Cloth Type List
///////////////////////////////////////////////////////////////
function clothTypeListDefault(el) {

	<s:url var="getKioskSuggestedClothTypeJsonResultUrl" namespace="/master" action="cloth-type" method="getKioskSuggestedClothTypeJsonResult"/>
	
	var url = "<s:property value="#getKioskSuggestedClothTypeJsonResultUrl"/>";
	var data = "clothType.enable=true";
	
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
				if ( el && typeof el !== 'undefined' ) {
					
					el.html("");
					var opts = '<option value=""></option>';
					$.each(result.clothTypeList, function(key, value) {
						opts += '<option value="' + value.id + '">' + value.name + '</option>';
						});
					el.html(opts);
					
				}
			}
		}
		else {
			alertDialog("", "<s:text name="errors.operationError"/>", "<s:text name="btn.ok"/>");
		}			

	}, "json").error(function(jqXHR, textStatus, errorThrown) {
		alertDialog("", "<s:property value="getText('errors.jQuery.post.json', { jqXHR.responseText, textStatus, errorThrown })"/>", "<s:text name="btn.ok"/>");
	});
}



function clothTypeListKioskLostFound(el, staffEl) {

	<s:url var="getKioskSuggestedClothTypeJsonResultUrl" namespace="/master" action="cloth-type" method="getKioskSuggestedClothTypeJsonResult"/>
	
	var url = "<s:property value="#getKioskSuggestedClothTypeJsonResultUrl"/>";
	var data = "clothType.enable=true";
	if ( staffEl && typeof staffEl !== 'undefined' ) {
		data = data + "&staff.id=" + staffEl.val();
	}

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
				if ( el && typeof el !== 'undefined' ) {
					
					el.html("");
					var opts = '<option value=""></option>';
					$.each(result.clothTypeList, function(key, value) {
						opts += '<option value="' + value.id + '">' + value.name + '</option>';
						});
					el.html(opts);
					
				}
			}
		}
		else {
			alertDialog("", "<s:text name="errors.operationError"/>", "<s:text name="btn.ok"/>");
		}			

	}, "json").error(function(jqXHR, textStatus, errorThrown) {
		alertDialog("", "<s:property value="getText('errors.jQuery.post.json', { jqXHR.responseText, textStatus, errorThrown })"/>", "<s:text name="btn.ok"/>");
	});
}
///////////////////////////////////////////////////////////////
// Cloth Type List
///////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////
// Department List
///////////////////////////////////////////////////////////////
function departmentListDefault(el) {

	<s:url var="getKioskSuggestedDepartmentJsonResultUrl" namespace="/master" action="department" method="getKioskSuggestedDepartmentJsonResult"/>
	
	var url = "<s:property value="#getKioskSuggestedDepartmentJsonResultUrl"/>";
	var data = "department.enable=true";
	
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
				if ( el && typeof el !== 'undefined' ) {
					
					el.html("");
					var opts = '<option value=""></option>';
					$.each(result.departmentList, function(key, value) {

						<s:if test="%{ #currentSystemLanguage == #SystemLanguagezhCN }">
							opts += '<option value="' + value.id + '">' + value.nameCht + '</option>';
						</s:if>
						<s:else>
							opts += '<option value="' + value.id + '">' + value.nameEng + '</option>';
						</s:else>

						});
					el.html(opts);
				}
			}
		}
		else {
			alertDialog("", "<s:text name="errors.operationError"/>", "<s:text name="btn.ok"/>");
		}			

	}, "json").error(function(jqXHR, textStatus, errorThrown) {
		alertDialog("", "<s:property value="getText('errors.jQuery.post.json', { jqXHR.responseText, textStatus, errorThrown })"/>", "<s:text name="btn.ok"/>");
	});
}
///////////////////////////////////////////////////////////////
// Department List
///////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////
// Calling Microsoft Virtual Keyboard
///////////////////////////////////////////////////////////////
//IE8
// Tools -> Internet Options -> Security
// 	Safety Site
// 		ADD "http://localhost"

// ( This Saftey Site )
// Tools -> Internet Options -> Security
// 	Custom Level
// 		ActiveX Control and plug-ins
// 			Initialize and script ActiveX controls not marked as safe
// 				Enable

function listenToSupportMicrosoftVirtualKeyboard( el, elIcon ) {
	
	var iconShowTimeout = null;
	var iconShow = function() {
		elIcon.attr("style", "visibility: visible; width: 50px; height: 18px;");
	}
	
	var iconHideTimeout = null;
	var iconHide = function () {
		elIcon.attr("style", "visibility: hidden");
	}
	
	el.focus(function() {
		iconShowTimeout = setTimeout(iconShow, 200);
	});

	el.blur(function() {
		iconHideTimeout = setTimeout(iconHide, 200);
	});
	
	elIcon.click(function() {
	    clearTimeout(iconHideTimeout);
	    iconHideTimeout = null;
	    
		var oShell = new ActiveXObject("Shell.Application");
		var commandtoRun = "C:\\Program Files\\Common Files\\Microsoft Shared\\ink\\TabTip.exe"; 
		oShell.ShellExecute(commandtoRun,"","","open","1");
		
		el.focus();
	});
}
///////////////////////////////////////////////////////////////
// Calling Microsoft Virtual Keyboard
///////////////////////////////////////////////////////////////

</script>