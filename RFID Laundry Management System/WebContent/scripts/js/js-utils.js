///////////////////////////////
//jquery plugin
///////////////////////////////

// check whether a element has attribute "name"
$.fn.hasAttr = function(name) {  
   return this.attr(name) !== undefined;
};

//indexOf
//The following methods should work for all of these browsers:
//For example, 
//var checkedHistory = new Array();
//if ( checkedHistory.indexOf( value ) == -1 ) {	}
if (!Array.indexOf) {
	Array.prototype.indexOf = function (obj, start) {
		for (var i = (start || 0); i < this.length; i++) {
			if (this[i] == obj) {
				return i;
			}
		}
		return -1;
	};
}
//indexOf

//preset all ajax no cache!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
$.ajaxSetup ({
    // Disable caching of AJAX responses
    cache: false
});

///////////////////////////////
// common value
///////////////////////////////

var datetime_format="dd-mm-yy";
var month_format="yyyy-mm";
var time_format = "HH:mm";

///////////////////////////////
//common function
///////////////////////////////

// trim space
function trimAll(sString) 
{ 
	while (sString.substring(0,1) == ' ') 
	{ 
		sString = sString.substring(1, sString.length); 
	} 
	while (sString.substring(sString.length-1, sString.length) == ' ') 
	{ 
		sString = sString.substring(0,sString.length-1); 
	} 
	return sString; 
} 

// replace all "strFind" with "strReplace" in the original string "strOrg"
function replaceAll(strOrg, strFind, strReplace) {
	var index = 0;
	while (strOrg.indexOf(strFind, index) != -1) {
		strOrg = strOrg.replace(strFind, strReplace);
		index = strOrg.indexOf(strFind, index);
	}
	return strOrg;
}

////////////////////////////////////////////
// Checkbox List Control (check/uncheck all) 
////////////////////////////////////////////
/* How to use (by Horace):
 * Example: 
 * Parent Checkbox: <s:checkbox id="aaa" name="bbb" cssClass="checkall" ref="checkAllInputRows" />
 * Children Checkbox: <s:checkbox id="xxx" name="yyy" cssClass="checkall2" checkall="checkAllInputRows" />
 * 
 * To use this util:
 * 1. the parent-checkbox(perform check-all-child function) must:
 * - has a css class "checkall"
 * - has a user-defined attribute 'ref', its value must match the 'checkall' attribute in the children checkbox
 * 
 * 2. the child-checkboxes must:
 * - has a css class "checkall2" (for child uncheck itself, then uncheck the parent)
 * - has a user-defined attribute 'checkall', its value must be same as 'ref' attribute in the parent checkbox
 */
$(function() {
	$(".checkall").live("click", function() {
		var ref = $(this).attr("ref");
		if ($(this).attr("checked"))
		{
			$("input[checkall='" + ref + "']").each(function() {
				$(this).attr("checked", true);
				$(this).trigger("change");
			});
		} 
		else
		{
			$("input[checkall='" + ref + "']").each(function() {
				$(this).attr("checked", false);
				$(this).trigger("change");
			});
		}
	});
	
	$(".checkall2").live("click", function() {
		var checkall = $(this).attr("checkall");
		if ($(this).attr("checked"))
		{
			// nothing to do
		}
		else
		{
			$("input[ref='" + checkall + "']").attr("checked", false);
		}
	});
});

///////////////////////////////
// Tab-Panel (all jquery tab-panel div must use css-class "pageTabPanel")
///////////////////////////////
$(function() {
	// use AJAX tab, only retrieve content of a tab when this tab is selected 
	$( ".pageTabPanel" ).tabs({
		ajaxOptions: {
			cache: false,
			error: function( xhr, status, index, anchor ) {
				$( anchor.hash ).html("Error! Couldn't load this tab." );
			}
		}
	});
	
//	// Refresh the content whenever the tab is selected
//	$('#tabs > ul > li > a').each(function (index, element) {
//		$(element).click(function () {         
//			$('#tabs').tabs('load', index);
//		});
//	});
	
});


///////////////////////////////
// Date Time Picker
///////////////////////////////

$(function() {
	setUpDateTimePicker();
});

function setUpDateTimePicker()
{
	if($( ".dateTimePicker" )[0]){
		$( ".dateTimePicker" ).datepicker({"dateFormat": datetime_format});
	}
}

///////////////////////////////
// Month Picker
///////////////////////////////

$(function() {
	setUpMonthPicker();
});

function setUpMonthPicker()
{
	if($( ".monthPicker" )[0]){
		$( ".monthPicker" ).monthpicker({pattern : month_format});
	}
}

///////////////////////////////
// Date + Time Picker (Horace)
///////////////////////////////

$(function() {
	setUpTimePicker();
});

function setUpTimePicker()
{
	if($( ".timePicker" )[0]){
		$( ".timePicker" ).timepicker(  );	// {"dateFormat": datetime_format, showSecond: true, timeFormat: time_format}
	}
}


///////////////////////////////
// Form Ajax Submit using Button
///////////////////////////////
var ajaxIgnoreClick = false; 

var methodName = null;
var package = null;
var action = null;
$(document).ready(function() {
	
	$(".ajaxButton").live("click", function(){
		methodName = $(this).attr("name");		// struts2 "method" attribute changed to "name" attribute in html
												// example value of methodName is 'method:create' 
		return true;
	});
	
	$(".ajaxForm").bind("reset", function(){
		
		var form = $(this);
		
		// remove the error msg in this form
		var formId = form.attr("id");
		var inputs = $("#" + formId + " :input");		// for all input element under this form
		inputs.removeClass("fieldErrors");				// remove their field error		// this may be duplicated with the below codes
		resetAllMessage(formId);
		removeActionMessageAndActionError(formId);
	});
		
	$(".ajaxForm").live("submit", function(){
		
		var form = $(this);
		
//package = ${this}.attr("package");
//		action = ${this}.attr("action");		

		// mask when saving
		form.mask("Loading...");
		
		// Find disabled inputs, and remove the "disabled" attribute
		// html form doesn't submit the disabled element value to server, but we want to submit this disabled element value
		var disabled = form.find(':input:disabled').removeAttr('disabled');
		
		// method serialize() collect all input element name/value pair in the form (e.g. ?name=chris&age=3...) 
		var data = form.serialize();
		data += "&struts.enableJSONValidation=true";	// struts2 setting: for json
		
		// re-disabled the set of inputs that you previously enabled
		disabled.attr('disabled','disabled');

		// construct the url string

		var url = form.attr("action").replace(".action", "") + "!" + methodName.replace("method:", "");
		
		// remove the error msg in this form
		var formId = form.attr("id");
		var inputs = $("#" + formId + " :input");		// for all input element under this form
		inputs.removeClass("fieldErrors");				// remove their field error		// this may be duplicated with the below codes
		resetAllMessage(formId);
		removeActionMessageAndActionError(formId);
		
		$.post(url, data, function(result){
			
			// unmask after save
			form.unmask();
			
			if (result.errors != undefined)
			{
				$.each(result.errors, function(key, value) {
					addActionError(formId, value);
				});
				
				$.each(result.errors, function(key, value) {
					showActionError(formId);
					return false;
				});
			}
			if (result.actionMessages != undefined)
			{
				$.each(result.actionMessages, function(key, value) {
					addActionMessage(formId, value);
				});
				
				$.each(result.actionMessages, function(key, value) {
					showActionMessage(formId);
					return false;
				});
			}
			if (result.actionErrors != undefined)
			{
				$.each(result.actionErrors, function(key, value) {
					addActionError(formId, value);
				});
				
				$.each(result.actionErrors, function(key, value) {
					showActionError(formId);
					return false;
				});
			}
			if (result.fieldErrors != undefined)
			{
				$.each(result.fieldErrors, function(key, value) {
					addFieldError(formId, key, value);
				});
			}
			
			if ( result == undefined || ( result.errors == undefined && (result.actionErrors == undefined || result.actionErrors.length==0) && (result.fieldErrors == undefined || getPropertyCount(result.fieldErrors)==0) ) )
			{
				form.trigger(formId + ".success", result);
			}
			else
			{
				form.trigger(formId + ".failure", result);
			}
			
			if(form[0].ajaxCallback){
				eval(form[0].ajaxCallback.value + '.call()');
			}
			
		}, "json");
		
		return false;
	});
});

function getPropertyCount(obj) {
    var count = 0,
        key;

    for (key in obj) {
        if (obj.hasOwnProperty(key)) {
            count++;
        }
    }

    return count;
}

///////////////////////////////////
// Form Ajax Search using Button
///////////////////////////////////
$(document).ready(function() {
	
	//For Normal Page Search
	$(".ajaxSearchButton").live("click", function(){
		window.ajaxSearchButton = $(this).attr("name");		// method name, e.g. "method:getNewPage"
		window.nonAjaxSearchButtonClick = false;
		return true; //return true for submitting the ajaxSearchForm
	});
	
	$(".nonAjaxSearchButton").live("click", function(){
		window.ajaxSearchButton = $(this).attr("name");		// method name, e.g. "method:getNewPage"
		window.nonAjaxSearchButtonClick = true;
		return true; //return true for submitting the ajaxSearchForm
	});

	$(".ajaxSearchForm").live("submit", function(){
		if( !window.nonAjaxSearchButtonClick )
		{
			var form = $(this);
			ajaxSearchFormPost(form, 0, null);
		}
		else
		{
			return true; // not submitted by ajaxSearchButton
		}
		return false;
	});
	
	$(".ajaxSearchFormPage").live("click", function(){
		
		var offset = $(this).attr("ref");
		var listTableDiv = $(this).closest(".tableOfInfo1");
		var listTableRef = listTableDiv.attr("id");
		var form = $("form[listtable='" + listTableRef + "']");
		ajaxSearchFormPost(form, offset, null);
		
		return false;
	});
	
	$(".ajaxSearchJumpFormPage").live("keypress", function(event){
		
		var code=event.charCode || event.keyCode;
		if(code && code == 13) {// if enter is pressed
			var page = $(this).val();
			var listTableDiv = $(this).closest(".tableOfInfo1");
			var listTableRef = listTableDiv.attr("id");
			var form = $("form[listtable='" + listTableRef + "']");
			ajaxSearchFormPost(form, null, page);
			
			return false;
		};
	});
	//For Normal Page Search



	//For Dialog Page Search
	$(".ajaxSearchDialogButton").live("click", function(){
		window.ajaxSearchDialogButton = $(this).attr("name");		// method name, e.g. "method:getNewPage"
		window.nonAjaxSearchDialogButtonClick = false;
		return true; //return true for submitting the ajaxSearchForm
	});
	
	$(".nonAjaxSearchDialogButton").live("click", function(){
		window.ajaxSearchDialogButton = $(this).attr("name");		// method name, e.g. "method:getNewPage"
		window.nonAjaxSearchDialogButtonClick = true;
		return true; //return true for submitting the ajaxSearchForm
	});
	
	$(".ajaxSearchDialogForm").live("submit", function(){
		if( !window.nonAjaxSearchButtonClick )
		{
			var form = $(this);
			ajaxSearchDialogFormPost(form, 0, null);
		}
		else
		{
			return true; // not submitted by ajaxSearchButton
		}
		return false;
	});
	
	$(".ajaxSearchDialogFormPage").live("click", function(){
		
		var offset = $(this).attr("ref");
		var listTableDiv = $(this).closest(".tableOfInfoDialog");
		var listTableRef = listTableDiv.attr("id");
		var form = $("form[listtable='" + listTableRef + "']");
		ajaxSearchDialogFormPost(form, offset, null);
		
		return false;
	});
	
	$(".ajaxSearchJumpDialogFormPage").live("keypress", function(event){
		
		var code=event.charCode || event.keyCode;
		if(code && code == 13) {// if enter is pressed
			var page = $(this).val();
			var listTableDiv = $(this).closest(".tableOfInfoDialog");
			var listTableRef = listTableDiv.attr("id");
			var form = $("form[listtable='" + listTableRef + "']");
			ajaxSearchDialogFormPost(form, null, page);
			
			return false;
		};
	});
	//For Dialog Page Search
});

function ajaxSearchFormPost(form, offset, page)
{
	var data = form.serialize();
	
	var url = form.attr("action").replace(".action", "") + "!" + window.ajaxSearchButton.replace("method:", "");
	var id = form.attr("id");
	var listTable = form.attr("listtable");	// the result will apply to this DIV
											// listtable is a user-defined attribute of a form, its value is the ID of a DIV
											// the resultant content will fill this DIV
	
	if ( offset != null && offset >= 0 ) {
		data += "&page.offset=" + offset;
		data += "&page.page=-1";
	}
	else if ( page != null ) {
		data += "&page.offset=-1";
		data += "&page.page=" + page;
	}
	else {
	}
	
	$("#" + listTable).mask("Loading...");
	
	$.post(url, data, function(result){
		$("#" + listTable).html(result);
		$("#" + listTable).unmask();
		
		// trigger finish event
		$("body").trigger(id + ".finishload");
		
	}, "text");
}

function ajaxSearchDialogFormPost(form, offset, page)
{
	var data = form.serialize();
	
	var url = form.attr("action").replace(".action", "") + "!" + window.ajaxSearchDialogButton.replace("method:", "");
	var id = form.attr("id");
	var listTable = form.attr("listtable");	// the result will apply to this DIV
											// listtable is a user-defined attribute of a form, its value is the ID of a DIV
											// the resultant content will fill this DIV
	
	if ( offset != null && offset >= 0 ) {
		data += "&page.offset=" + offset;
		data += "&page.page=-1";
	}
	else if ( page != null ) {
		data += "&page.offset=-1";
		data += "&page.page=" + page;
	}
	else {
	}
	
	$("#" + listTable).mask("Loading...");
	
	$.post(url, data, function(result){
		$("#" + listTable).html(result);
		$("#" + listTable).unmask();
		
		// trigger finish event
		$("body").trigger(id + ".finishload");
		
	}, "text");
}
///////////////////////////////
//End Form Ajax Search using Button
///////////////////////////////

function addFieldError(formid, key, msg)
{
	$('#' + formid + ' :input[name="'+key+'"]').addClass("fieldErrors");
	$('<div class="fieldError" ref="' + formid + '">'+msg+'</div>').insertAfter('#' + formid + ' :input[name="'+key+'"]');
}
function removeFieldError(formid)
{
	clearAllFieldError(formid);
}

function addActionMessage(formid, msg)
{
	$('<div class="actionMessage">'+msg+'</div>').appendTo('div.actionMessages[ref="' + formid + '"]');
}
function showActionMessage(formid)
{
	$('div.actionMessages[ref="' + formid + '"]').hide().fadeIn();
}
function removeActionMessage(formid)
{
	$('div.actionMessages[ref="' + formid + '"]').hide();
}

function addActionError(formid, msg)
{
	$('<div class="actionError">'+msg+'</div>').appendTo('div.actionErrors[ref="' + formid + '"]');
}
function showActionError(formid)
{
	$('div.actionErrors[ref="' + formid + '"]').hide().fadeIn();
}
function removeActionError(formid)
{
	$('div.actionErrors[ref="' + formid + '"]').hide();
}

function removeActionMessageAndActionError(formid) {
	removeActionMessage(formid);
	removeActionError(formid);
}

function resetAllMessage(formId)
{
	clearAllActionMessage(formId);
	clearAllFieldError(formId);
	clearAllActionError(formId);
}

function clearAllActionMessage(formId)
{
	$('div.actionMessages[ref="' + formId + '"]').text("");
}

function clearAllActionError(formId)
{
	$('div.actionErrors[ref="' + formId + '"]').text("");
}

function clearAllFieldError(formId)
{
	$('div.fieldError[ref="' + formId + '"]').remove();
}

function clearAllActionMessage()
{
	$('div.actionMessages').text("");
}

function clearAllActionMessageDelay()
{
	$('div.actionMessages').fadeOut();
}

//when click anywhere, all action message will disappear
$('body').live("click", function(){
	if(ajaxIgnoreClick){
		ajaxIgnoreClick = false;
	}else{
		clearAllActionMessageDelay();
	}
});

///////////////////////////////
//number calculator
///////////////////////////////

//only work for +/-
$(document).ready(function() {
	
	function calTotal(obj, name)
	{
		var total = 0;

		$("[calTotal='" + name + "']").each(function(){
			
			if ( isNaN( $(this).val() ) || trimAll( $(this).val() ) == "" ) {		
				$(this).val("0.00");
			}
			else {
				if($(this).hasAttr("calOption")){
					
					if ( $(this).val() < 0 ) {
						
						var temp = Math.abs( eval( trimAll( $(this).val() ) ) );
						total = total + temp;
					}
					else {
						var str = "total = total " + $(this).attr("calOption") + eval( trimAll( $(this).val() ) );
						eval(str);
					}

				}
				else{
					total += Number($(this).val());
				}
			}
		});

		obj.val(total);
		obj.trigger('change');
	}
	
	$(".calTotal").each(function(){
		var obj = $(this);
		var name = $(this).attr("name");
		$("[calTotal='" + name + "']").each(function(){
			$(this).bind('change', function(){
				calTotal(obj, name);
			});
		});
	});
});

///////////////////////////////
//dialog
///////////////////////////////
var alertDialogDiv = $('<div id="alertDialog"/>');
var msgDialogDiv = $('<div id="msgDialog"/>');

function alertDialog(dialogTitle, msg, okButton, callback){
	alertDialogDiv.html('<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>' + msg + '</p>')
	.dialog({
		modal: true,
		title: dialogTitle,
		buttons:{
			'ok': {
				text: okButton,
				click: function(){
					$(this).dialog('close');
					$(this).dialog('destroy');
					$(this).text("");
					if(callback!=undefined && typeof(callback) == 'function'){
						callback();
					}
				}
			}
		}
	});
}

function msgDialog(dialogTitle, msg, okButton, callback){
	msgDialogDiv.html('<p>' + msg + '</p>')
	.dialog({
		modal: true,
		title: dialogTitle,
		buttons:{
			'ok': {
				text: okButton,
				click: function(){
					$(this).dialog('close');
					$(this).dialog('destroy');
					$(this).text("");
					if(callback!=undefined && typeof(callback) == 'function'){
						callback();
					}
				}
			}
		}
	});
}

function optionsAvailableMsgDialog(dialogTitle, msg, callback1Button, callback1, callback2Button, callback2){
	msgDialogDiv.html('<p>' + msg + '</p>')
	.dialog({
		modal: true,
		title: dialogTitle,
		open: function(event, ui) { 
			$("a.ui-dialog-titlebar-close").remove();
		},
		buttons:{
			'callback1': {
				text: callback1Button,
				click: function(){
					$(this).dialog('close');
					$(this).dialog('destroy');
					$(this).text("");
					if(callback1!=undefined && typeof(callback1) == 'function'){
						callback1();
					}
				}
			},
			"callback2": {
				text: callback2Button,
				click: function(){
					$(this).dialog('close');
					$(this).dialog('destroy');
					$(this).text("");
					if(callback2!=undefined && typeof(callback2) == 'function'){
						callback2();
					}
				}
			}
		}
	});
}

///////////////////////////////
//User Group Resource checkbox list control
///////////////////////////////
$(function() {
	$(".groupCheckall").live("click", function() {
		var ref = $(this).attr("groupRef");
		if ($(this).attr("checked")) {
			$("input[groupCheckallTarget='" + ref + "']").each(function() {
				$(this).attr("checked", true);
				$(this).trigger("change.subGroupCheckall");
			});
		} else {
			$("input[groupCheckallTarget='" + ref + "']").each(function() {
				$(this).attr("checked", false);
				$(this).trigger("change.subGroupCheckall");
			});
		}
	});
	
	$(".subGroupCheckall").live("click change.subGroupCheckall", function() {
		var ref = $(this).attr("subGroupRef");
		if ($(this).attr("checked")) {
			$("input[subGroupCheckallTarget='" + ref + "']").each(function() {
				$(this).attr("checked", true);
				//$(this).trigger("change.groupMemberCheck");
			});
		} else {
			$("input[subGroupCheckallTarget='" + ref + "']").each(function() {
				$(this).attr("checked", false);
				//$(this).trigger("change.groupMemberCheck");
			});
		}
	});

	$(".groupMemberCheck").live("click change.groupMemberCheck", function() {
		
		var checkall = $(this).attr("groupCheckallTarget");
		
		var n = $("input[groupCheckallTarget='" + checkall + "']:checked").length;
		if( n == 0 ) {
			$("input[groupRef='" + checkall + "']").attr("checked", false);
		}
		else {
			$("input[groupRef='" + checkall + "']").attr("checked", true);
		}
	});

	$(".subGroupMemberCheck").live("click", function() {
		
		var checkall = $(this).attr("subGroupCheckallTarget");
		
		var n = $("input[subGroupCheckallTarget='" + checkall + "']:checked").length;
		if( n == 0 ) {
			$("input[subGroupRef='" + checkall + "']").attr("checked", false);
			$("input[subGroupRef='" + checkall + "']").trigger("change.groupMemberCheck");
		}
		else {
			$("input[subGroupRef='" + checkall + "']").attr("checked", true);
			$("input[subGroupRef='" + checkall + "']").trigger("change.groupMemberCheck");
		}
	});
});
