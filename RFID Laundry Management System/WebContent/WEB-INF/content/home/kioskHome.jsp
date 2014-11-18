<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<script>
	var mainPage = "<%= request.getParameter("mainPage") %>";
	
	if ( mainPage && typeof mainPage !== 'undefined' && mainPage != "null" ) {
		
		if ( mainPage == 'cloth-collection' ) {
			window.location.href = '<s:url namespace="/kiosk" action="cloth-collection" method="getMainPage"><s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param></s:url>';
		}
		
		<%-- Handheld --%>
		else if ( mainPage == 'cloth-distribute' ) {
			window.location.href = '<s:url namespace="/kiosk" action="cloth-distribute" method="getMainPage"><s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param></s:url>';
		}
		<%-- Handheld --%>
		
		<%-- Fix Reader --%>
// 		else if ( mainPage == 'cloth-distribute' ) {
<%-- 			window.location.href = '<s:url namespace="/kiosk" action="cloth-distribute-fixed-reader" method="getMainPage"><s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param></s:url>'; --%>
// 		}
		<%-- Fix Reader --%>
	}
	else {
	}
</script>

<br />
<br />
<br />
<%-- <s:text name="page.title.home" /> --%>

<h1><s:text name="welcome.msg"/></h1>
