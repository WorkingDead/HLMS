<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<br /><br /><br />
<center>
	<s:hidden theme="simple" id="hiddenKioskName" name="kioskName"/>
	<h1><s:text name="warning.page.in.using.by.another.user" /></h1>
</center>

<script>

$(function(){
	var kioskName = $("#hiddenKioskName").val();
	
	<s:url var="getMainPageUrl" namespace="/kiosk" action="cloth-collection" method="getMainPage"/>
	var alertMsg = "<s:text name="dialog.msg.page.in.using.by.another.user" /> " + 
					"<s:text name="dialog.msg.enter.page.clear.other.user.data" /> " + 
					"<s:text name="dialog.msg.u.want.to.continue" />";
	
	optionsAvailableMsgDialog("", alertMsg, 
			
			"<s:text name="btn.yes"/>",
			function(){
				var url = "<s:property value="#getMainPageUrl"/>";
				url += "?kioskName=" + kioskName;
				url += "&takePageOwnership=true";
				$(location).attr('href', url);
			},
			
			"<s:text name="btn.no"/>",
			function(){
				// nothing to do
			}
	);
	
});
		
</script>