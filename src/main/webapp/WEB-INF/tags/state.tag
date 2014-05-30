<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ attribute name="state" required="true"%>
<c:choose>
	<c:when test="${state eq 'TO_DO'}">
		<span class="state_span"><span class="glyphicon glyphicon-list"></span>
			<s:message code="task.state.todo"></s:message></span>
	</c:when>
	<c:when test="${state eq 'ONGOING'}">
		<span class="state_span"><span
			class="glyphicon glyphicon-repeat"></span> <s:message
				code="task.state.ongoing"></s:message></span>
	</c:when>
	<c:when test="${state eq 'BLOCKED'}">
		<span class="state_span"><span
			class="glyphicon glyphicon-ban-circle"></span> <s:message
				code="task.state.blocked"></s:message></span>
	</c:when>
	<c:when test="${state eq 'CLOSED'}">
		<span class="state_span"><span class="glyphicon glyphicon-ok"></span>
			<s:message code="task.state.closed"></s:message></span>
	</c:when>
</c:choose>