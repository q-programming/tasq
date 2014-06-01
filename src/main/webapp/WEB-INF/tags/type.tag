<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ attribute name="type" required="false"%>
<%@ attribute name="show_text" required="false"%>
<%@ attribute name="list" required="false"%>
<c:set var="task_txt">
	<s:message code="task.type.task" />
</c:set>
<c:set var="user_story_txt">
	<s:message code="task.type.user_story" />
</c:set>
<c:set var="issue_txt">
	<s:message code="task.type.issue" />
</c:set>
<c:set var="bug_txt">
	<s:message code="task.type.bug" />
</c:set>
<c:set var="idle_txt">
	<s:message code="task.type.idle" />
</c:set>
<c:if test="${empty list}">
	<c:set var="list_view">
		task-type
	</c:set>
</c:if>
<c:if test="${not empty type}">
	<c:choose>
		<c:when test="${type eq 'TASK'}">
			<img class="a-tooltip ${list_view}" title="${task_txt}"
				src="<c:url value="/resources/img/task.png"/>">
		</c:when>
		<c:when test="${type eq 'USER_STORY'}">
			<img class="a-tooltip ${list_view}" title="${user_story_txt}"
				src="<c:url value="/resources/img/user_story.png"/>">
		</c:when>
		<c:when test="${type eq 'ISSUE'}">
			<img class="a-tooltip ${list_view}" title="${issue_txt}"
				src="<c:url value="/resources/img/issue.png"/>">
		</c:when>
		<c:when test="${type eq 'BUG'}">
			<img class="a-tooltip ${list_view}" title="${bug_txt}"
				src="<c:url value="/resources/img/bug.png"/>">
		</c:when>
		<c:when test="${type eq 'IDLE'}">
			<img class="a-tooltip ${list_view}" title="${idle_txt}"
				src="<c:url value="/resources/img/idle.png"/>">
		</c:when>
	</c:choose>
</c:if>
<c:if test="${not empty show_text}">
	<c:choose>
		<c:when test="${type eq 'TASK'}">
			${task_txt}
		</c:when>
		<c:when test="${type eq 'USER_STORY'}">
			${user_story_txt}
		</c:when>
		<c:when test="${type eq 'ISSUE'}">
			${issue_txt}
		</c:when>
		<c:when test="${type eq 'BUG'}">
			${bug_txt}
		</c:when>
		<c:when test="${type eq 'IDLE'}">
			${idle_txt}
		</c:when>
	</c:choose>
</c:if>