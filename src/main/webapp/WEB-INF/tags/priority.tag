<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ attribute name="priority" required="true"
	type="com.qprogramming.tasq.task.TaskPriority"%>
<%@ attribute name="list" required="false"%>
<c:if test="${not empty priority && empty list}">
	<span class="state_span"><img
		src="<c:url value="/resources/img/${priority.imgcode}.png"/>"> <s:message
			code="${priority.code}"></s:message></span>
</c:if>
<c:if test="${not empty priority && list}">
	<span class="state_span a-tooltip"
		title="<s:message code="${priority.code}"></s:message>"><img
		src="<c:url value="/resources/img/${priority.imgcode}.png"/>"></span>
</c:if>
