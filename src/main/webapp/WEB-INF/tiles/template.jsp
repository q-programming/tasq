<%@ page trimDirectiveWhitespaces="true" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras"
           prefix="tilesx" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
    <title>${applicationName}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="<c:url value="/resources/js/jquery-1.11.1.min.js" />"></script>
    <%-- jQuery UI --%>
    <script src="<c:url value="/resources/js/jquery-ui-1.10.4.custom.js" />"></script>
    <link
            href="<c:url value="/resources/css/jquery-ui-1.10.4.custom.min.css" />"
            rel="stylesheet" media="screen"/>
    <%-- Fix issue jQueryUI vs Bootstrap argue over tooltip --%>
    <script>
        $.widget.bridge('uitooltip', $.ui.tooltip);
    </script>
    <%-- BOOTSTRAP --%>
    <%-- <link href="<c:url value="/resources/css/bootstrap.min.css" />" --%>
    <!-- 	rel="stylesheet" media="screen" /> -->
    <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
            integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS"
            crossorigin="anonymous"></script>
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
          crossorigin="anonymous">
    <link href="<c:url value="/resources/css/core.css" />" rel="stylesheet"
          media="screen"/>
    <%-- FONT AWSOME --%>
    <link rel="stylesheet"
          href="//maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css">
    <%-- <link href="<c:url value="/resources/css/font-awesome.min.css" />" rel="stylesheet" --%>
    <!-- 	media="screen" /> -->
    <%-- CUSTOMS --%>
    <link href="<c:url value="/resources/css/custom.css" />"
          rel="stylesheet" media="screen"/>
    <link rel="icon" type="image/png"
          href="<c:url value="/resources/img/favicon.ico"/>"/>

    <%--CUSTOM SCRIPTS --%>
    <script src="<c:url value="/resources/js/tasq.js" />"></script>
    <script src="<c:url value="/resources/js/eventTimer.js" />"></script>
    <script src="<c:url value="/resources/js/jquery.mask.min.js" />"></script>
    <script src="<c:url value="/resources/js/bootbox.min.js" />"></script>
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
    <%-------THEME ------%>
    <security:authorize access="!isAuthenticated()">
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet" type="text/css">
        <link href="<c:url value="/resources/css/theme.css" />"
              rel="stylesheet" media="screen"/>
    </security:authorize>
    <security:authorize access="isAuthenticated()">
        <security:authentication property="principal" var="user"/>
        <c:if test="${not empty user.theme}">
            <jsp:include page="../views/other/theme.jsp"/>
        </c:if>
        <c:if test="${empty user.theme}">
            <link href="<c:url value="/resources/css/theme.css" />"
                  rel="stylesheet" media="screen"/>
        </c:if>
        <%--Load language based datepickers--%>
        <c:if test="${user.language eq 'pl'}">
            <script src="<c:url value="/resources/js/jquery.ui.datepicker-pl.js" />"></script>
        </c:if>

    </security:authorize>
</head>
<body>
<!-- 	Header -->
<tiles:insertAttribute name="header" defaultValue=""/>
<!-- Page content -->
<div class="container-fluid">
    <div class="row">
        <security:authorize access="isAuthenticated()">
            <!-- Sidebar -->
            <div class="col-sm-3 col-md-2 sidebar" id="sidebar-div"
                 <c:if test="${user.smallSidebar}">style="display: none" </c:if>>
                <tiles:insertAttribute name="sidebar" defaultValue=""/>
            </div>
            <c:if test="${!user.smallSidebar}">
                <c:set var="body_classes">col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2</c:set>
            </c:if>
            <c:if test="${user.smallSidebar}">
                <c:set var="body_classes">col-sm-12 col-md-12 big</c:set>
            </c:if>

            <tiles:insertAttribute name="small-sidebar" defaultValue=""/>
            <div class="${body_classes} main">
                <div class="main-container">
                    <tiles:insertAttribute name="body" defaultValue=""/>
                </div>
                <tiles:insertAttribute name="footer" defaultValue=""/>
            </div>

        </security:authorize>
        <security:authorize access="!isAuthenticated()">
            <tiles:insertAttribute name="body" defaultValue=""/>
        </security:authorize>
        <!-- End of page content -->

        <a href="#" class="back-to-top a-tooltip"
           title="<s:message code="main.backToTop"/>"><i
                class="fa fa-2x fa-angle-up"></i></a>
        <%
            /* Show a message. See support.web package */
        %>
        <div id="messages_div">
            <c:if test="${not empty message}">
                <c:choose>
                    <c:when test="${message.type == 'WARNING'}">
                        <c:set value="" var="alertClass"/>
                    </c:when>
                    <c:otherwise>
                        <c:set value="alert-${fn:toLowerCase(message.type)}"
                               var="alertClass"/>
                    </c:otherwise>
                </c:choose>
                <div class="alert ${alertClass} fade in alert-overlay">
                    <button type="button" class="close theme-close" data-dismiss="alert">&times;</button>
                    <%
                        /* Display a message by its code. If the code was not found, it will be displayed as default text */
                    %>
                    <s:message code="${message.message}" arguments="${message.args}"
                               text="${message.message}"/>
                </div>
            </c:if>
        </div>
    </div>
</div>
</body>
<script>
    $(document).ready(function () {
        $('.back-to-top').hide();
        $(window).scroll(function () {
            if ($(this).scrollTop() > 200) {
                $('.back-to-top').fadeIn();
            } else {
                $('.back-to-top').fadeOut();
            }
        });

        $('.back-to-top').click(function () {
            $('html, body').animate({
                scrollTop: 0
            }, 800);
            return false;
        });
    });
    $(".alert").alert();
    window.setTimeout(function () {
        $(".alert-success").alert('close');
        $(".alert-info").alert('close');
    }, 15000);
    window.setTimeout(function () {
        $(".alert-warning").alert('close');
    }, 20000);

    //E-mail confirmed ?
    var emailConfirmed = "${user.confirmed}";
    if (emailConfirmed === 'false' && !settings) {
        var msg = '<s:message code="signup.notconfirmed" /> ';
        var settings = '<s:message code="menu.settings" /> ';
        var url = '<a href="<c:url value="/settings" />">' + settings + '</a>';
        showWarning(msg + url);
    }
</script>
</html>