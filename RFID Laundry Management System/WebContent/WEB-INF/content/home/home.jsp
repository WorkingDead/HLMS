<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<script>
	var loc = "<%= request.getParameter("request_locale") %>";
	
	if ( loc && typeof loc !== 'undefined' && loc != "null" ) {
	}
	else {
		window.location.href = '<s:url namespace="/system" action="home"><s:param name="request_locale" value="user.lang"/></s:url>';
	}
</script>

<s:text name="page.title.home" />

<h1><s:text name="welcome.msg"/></h1>
