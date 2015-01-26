<%@ page trimDirectiveWhitespaces="true" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras"
	prefix="tilesx"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Tasqr</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script src="<c:url value="/resources/js/jquery-1.11.1.min.js" />"></script>
<%-- jQuery UI --%>
<script src="<c:url value="/resources/js/jquery-ui-1.10.4.custom.js" />"></script>
<script src="<c:url value="/resources/js/jquery.ui.datepicker-pl.js" />"></script>
<link
	href="<c:url value="/resources/css/jquery-ui-1.10.4.custom.min.css" />"
	rel="stylesheet" media="screen" />
<%-- Fix issue jQueryUI vs Bootstrap argue over tooltip --%>
<script>
$.widget.bridge('uitooltip', $.ui.tooltip);
</script>
<%-- BOOTSTRAP --%>
<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>
<link href="<c:url value="/resources/css/bootstrap.min.css" />"
	rel="stylesheet" media="screen" />
<link href="<c:url value="/resources/css/core.css" />" rel="stylesheet"
	media="screen" />
<%-- FONT AWSOME --%>
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
<%-- <link href="<c:url value="/resources/css/font-awesome.min.css" />" rel="stylesheet" --%>
<!-- 	media="screen" /> -->
<%-------THEME ------%>
<security:authorize access="!isAuthenticated()">
	<link href="<c:url value="/resources/css/theme.css" />"
		rel="stylesheet" media="screen" />
</security:authorize>
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal" var="user" />
	<c:if test="${not empty user.theme}">
			<link href="<c:url value="/resources/css/theme-${user.theme}.css" />"
		rel="stylesheet" media="screen" />
	</c:if>
	<c:if test="${empty user.theme}">
		<link href="<c:url value="/resources/css/theme.css" />"
			rel="stylesheet" media="screen" />
	</c:if>
</security:authorize>
<%-- CUSTOMS --%>
<link href="<c:url value="/resources/css/custom.css" />"
	rel="stylesheet" media="screen" />
<link href="<c:url value="/resources/css/jquery.contextmenu.css" />"
	rel="stylesheet" media="screen" />
<link rel="icon" type="image/png"
	href="<c:url value="/resources/img/favicon.ico"/>" />
<%--CUSTOM SCRIPTS --%>
<script src="<c:url value="/resources/js/tasq.js" />"></script>
<script src="<c:url value="/resources/js/jquery.mask.min.js" />"></script>
<script src="<c:url value="/resources/js/bootbox.min.js" />"></script>
<script src="<c:url value="/resources/js/jquery.contextmenu.js" />"></script>
<%-- <tilesx:useAttribute id="styles" name="styles" --%>
<%-- 	classname="java.util.List" ignore="true" /> --%>
<%-- <c:forEach var="cssName" items="${styles}"> --%>
<%-- 	<link type="text/css" href="<c:url value="/resources/css/${cssName}"/>" --%>
<!-- 		rel="stylesheet" media="screen" /> -->
<%-- </c:forEach> --%>
<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
</head>
<body>
	<tiles:insertAttribute name="header" defaultValue="" />
	<!-- Page content -->
	<div class="container">
		<tiles:insertAttribute name="body" defaultValue="" />
	</div>
	<!-- End of page content -->
	<tiles:insertAttribute name="footer" defaultValue="" />
	<%
		/* Show a message. See support.web package */
	%>
	<div id="messages_div" >
		<c:if test="${not empty message}">
			<c:choose>
				<c:when test="${message.type == 'WARNING'}">
					<c:set value="" var="alertClass" />
				</c:when>
				<c:otherwise>
					<c:set value="alert-${fn:toLowerCase(message.type)}"
						var="alertClass" />
				</c:otherwise>
			</c:choose>
			<div class="alert ${alertClass} fade in"
				style="position: fixed; bottom: 0px; width: 100%;">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				<%
					/* Display a message by its code. If the code was not found, it will be displayed as default text */
				%>
				<s:message code="${message.message}" arguments="${message.args}"
					text="${message.message}" />
			</div>
		</c:if>
	</div>
</body>
<script>
	$(".alert").alert();
	window.setTimeout(function() { 
		$(".alert-success").alert('close');
		$(".alert-info").alert('close'); }
	,15000);
	window.setTimeout(function() { $(".alert-warning").alert('close'); }, 20000);
</script>
</html>