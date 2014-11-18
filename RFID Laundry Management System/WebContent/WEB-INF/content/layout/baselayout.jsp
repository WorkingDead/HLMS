<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="taglibs.jsp"%>
<!DOCTYPE html><!-- html5 -->
<html>
<head>

<!-- meta data -->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="expires" content="0"/>

<title><tiles:insertAttribute name="title" ignore="true" /></title>

<s:head />

<tiles:importAttribute name="scripts" scope="request"/> 
<s:iterator value="#request['scripts']" var="js">
	<script src="<%=request.getContextPath()%><s:property value="#js" />?<%=System.currentTimeMillis() %>"></script>
</s:iterator>

<tiles:importAttribute name="styles" scope="request"/> 
<s:iterator value="#request['styles']" var="css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%><s:property value="#css" />?<%=System.currentTimeMillis() %>" />
</s:iterator>

<!--[if IE]>
<tiles:importAttribute name="styles-ie" scope="request"/> 
<s:iterator value="#request['styles-ie']">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%><s:property />?<%=System.currentTimeMillis() %>" />
</s:iterator>
<![endif]-->

</head>
<body>
	<div class="container">
		<div class="header">
			<tiles:insertAttribute name="header" />
		</div>
		<div class="menu">
			<tiles:insertAttribute name="menu" ignore="true" />
		</div>
		<div class="notice">
			<s:actionerror cssClass="warning"/>
			<s:actionmessage cssClass="message"/>
		</div>
		<div class="body">
			<tiles:insertAttribute name="globalSetting" />
			<tiles:insertAttribute name="body" />
		</div>
		<div class="footer">
			<tiles:insertAttribute name="footer" />
		</div>
	</div>
</body>
</html>