<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ attribute name="logType" required="true" %>
<c:choose>
	<c:when test="${logType eq 'CREATE'}">
		<s:message code="log.type.create"></s:message>
	</c:when>
	<c:when test="${logType eq 'LOG'}">
		<s:message code="log.type.log"></s:message>
	</c:when>
	<c:when test="${logType eq 'CHANGE'}">
		<s:message code="log.type.change"></s:message>
	</c:when>
	<c:when test="${logType eq 'STATUS'}">
		<s:message code="log.type.status"></s:message>
	</c:when>
	<c:when test="${logType eq 'ESTIMATE'}">
		<s:message code="log.type.estimate"></s:message>
	</c:when>
	<c:when test="${logType eq 'CLOSED'}">
		<s:message code="log.type.closed"></s:message>
	</c:when>
</c:choose>