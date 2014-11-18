<%@ page language="java" import="java.text.SimpleDateFormat" contentType="text/html; charset=utf-8"%>
<%@ include file="taglibs.jsp"%>

<s:set var="lost" value="@web.actions.kiosk.ClothLostAndFoundAction$LostFoundWay@lost"/>
<s:set var="found" value="@web.actions.kiosk.ClothLostAndFoundAction$LostFoundWay@found"/>

<hr />

<div class="alignLeft">
			<s:a namespace="/kiosk" id="page-cloth-collection" action="cloth-collection" method="getMainPage" cssClass="kioskButton white kioskNavBtnMarker" >
				<s:text name="kiosk.menu.level1.cloth.collection" />
				<s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param>
			</s:a>

<%-- Handheld --%>
			<s:a namespace="/kiosk" id="page-cloth-distribute" action="cloth-distribute" method="getMainPage" cssClass="kioskButton white kioskNavBtnMarker">
				<s:text name="kiosk.menu.level1.cloth.distribute" />
				<s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param>
			</s:a>
<%-- Handheld --%>
			
<%-- Fix Reader --%>
<%-- 			<s:a namespace="/kiosk" id="page-cloth-distribute" action="cloth-distribute-fixed-reader" method="getMainPage" cssClass="kioskButton white kioskNavBtnMarker"> --%>
<%-- 				<s:text name="kiosk.menu.level1.cloth.distribute" /> --%>
<%-- 				<s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param> --%>
<%-- 			</s:a> --%>
<%-- Fix Reader --%>

			<s:a namespace="/kiosk" id="page-cloth-lost" action="cloth-lost-and-found" method="getMainPage" cssClass="kioskButton white kioskNavBtnMarker">
				<s:text name="kiosk.menu.level1.cloth.lost" />
				<s:param name="way"><s:property value="%{#lost}"/></s:param>
				<s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param>
			</s:a>
		
			<s:a namespace="/kiosk" id="page-cloth-found" action="cloth-lost-and-found" method="getMainPage" cssClass="kioskButton white kioskNavBtnMarker">
				<s:text name="kiosk.menu.level1.cloth.found" />
				<s:param name="way"><s:property value="%{#found}"/></s:param>
				<s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param>
			</s:a>

			<s:a namespace="/kiosk" id="page-cloth-inquire" action="cloth-inquire" method="getMainPage" cssClass="kioskButton white kioskNavBtnMarker">
				<s:text name="kiosk.menu.level1.cloth.inquire" />
				<s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param>
			</s:a>
</div>

<div class="alignRight">
	<s:iterator value="systemLanguageList" var="systemLanguage">
		<s:url var="link" includeParams="get" encode="true">
			<s:param name="request_locale" value="#systemLanguage"/>
			<s:param name="kioskName"><%= request.getParameter("kioskName") %></s:param>
		</s:url>
		<s:a href="%{link}" cssClass="languageButton white"><s:text name="%{#systemLanguage.systemResourceKey}"/></s:a>
	</s:iterator>		
</div>

<script>
$(document).ready(function(){
	
	var pageName = $("#pageName").val();	// every page has a hidden field "pageName"
	$(".kioskNavBtnMarker").each(function(index){
		var linkId = $(this).attr("id");
		if (linkId == pageName)
		{
			$(this).removeClass("white");
			$(this).addClass("gray");
		}
	});
	
});

</script>