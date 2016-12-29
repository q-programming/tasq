<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ attribute name="state" required="true" %>
<%@ attribute name="list" required="false" %>
<c:choose>
    <c:when test="${state eq 'TO_DO'}">
        <c:set var="message"><s:message code="task.state.todo"/></c:set>
        <span class="state_span a-tooltip" <c:if test="${list}">title="${message}"</c:if>><i
                class="fa fa-lg fa-pencil-square-o"></i><c:if test="${not list}">&nbsp;${message}</c:if></span>
    </c:when>
    <c:when test="${state eq 'ONGOING'}">
        <c:set var="message"><s:message code="task.state.ongoing"/></c:set>
        <span class="state_span a-tooltip" <c:if test="${list}">title="${message}"</c:if>><i
                class="fa fa-lg fa-spin fa-repeat"></i><c:if test="${not list}">&nbsp;${message}</c:if></span>
    </c:when>
    <c:when test="${state eq 'BLOCKED'}">
        <c:set var="message"><s:message code="task.state.blocked"/></c:set>
        <span class="state_span a-tooltip" <c:if test="${list}">title="${message}"</c:if>><i
                class="fa fa-lg fa-ban"></i><c:if test="${not list}">&nbsp;${message}</c:if></span>
    </c:when>
    <c:when test="${state eq 'CLOSED'}">
        <c:set var="message"><s:message code="task.state.closed"/></c:set>
        <span class="state_span a-tooltip" <c:if test="${list}">title="${message}"</c:if>><i
                class="fa fa-lg fa-archive"></i><c:if test="${not list}">&nbsp;${message}</c:if></span>
    </c:when>
    <c:when test="${state eq 'OPEN'}">
        <c:set var="message"><s:message code="task.state.open"/></c:set>
        <span class="state_span a-tooltip" <c:if test="${list}">title="${message}"</c:if>><i
                class="fa fa-lg fa-refresh"></i><c:if test="${not list}">&nbsp;;${message}</c:if></span>
    </c:when>
    <c:when test="${state eq 'COMPLETE'}">
        <c:set var="message"><s:message code="task.state.complete"/></c:set>
        <span class="state_span a-tooltip" <c:if test="${list}">title="${message}"</c:if>><i
                class="fa fa-lg fa-check"></i><c:if test="${not list}">&nbsp;${message}</c:if></span>
    </c:when>
    <c:otherwise>
        <c:set var="message"><s:message code="task.showAll"/></c:set>
        <span class="state_span a-tooltip" <c:if test="${list}">title="${message}"</c:if>><i
                class="fa fa-lg fa-list"></i><c:if test="${not list}">&nbsp;${message}</c:if></span>
    </c:otherwise>
</c:choose>