<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.ironing.delivery.handheld" /></h3>
<h1><s:text name="warning.page.in.using.by.another.user" /></h1>

<script>

$(function(){
	
	<s:url var="getMainPageUrl" namespace="/general" action="ironing-delivery-handheld" method="getMainPage"/>
	
	var alertMsg = "<s:text name="dialog.msg.page.in.using.by.another.user" /> " + 
					"<s:text name="dialog.msg.enter.page.clear.other.user.data" /> " + 
					"<s:text name="dialog.msg.u.want.to.continue" />";
	
	optionsAvailableMsgDialog("", alertMsg, 
			
			"<s:text name="btn.yes"/>",
			function(){
				var url = "<s:property value="#getMainPageUrl"/>";
				url += "?takePageOwnership=true";
				$(location).attr('href', url);
			},
			
			"<s:text name="btn.no"/>",
			function(){
				// nothing to do
			}
	);
	
});
		
</script>