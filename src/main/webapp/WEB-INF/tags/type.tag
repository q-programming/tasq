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
<c:set var="subtask_txt">
	<s:message code="task.type.subtask" />
</c:set>
<c:set var="subbug_txt">
	<s:message code="task.type.subbug" />
</c:set>
<c:if test="${empty list}">
	<c:set var="list_view">
		fa-border
	</c:set>
</c:if>
<c:if test="${not empty list}">
	<c:set var="list_view">
		fa-fw
	</c:set>
</c:if>

<c:if test="${not empty type}">
	<c:choose>
		<c:when test="${type eq 'TASK'}">
			<i class="fa fa-lg fa-check-square a-tooltip ${list_view}"  title="${task_txt}"></i>
		</c:when>
		<c:when test="${type eq 'USER_STORY'}">
			<i class="fa fa-lg fa-lightbulb-o a-tooltip ${list_view}"  title="${user_story_txt}"></i>
		</c:when>
		<c:when test="${type eq 'ISSUE'}">
			<i class="fa fa-lg fa-exclamation-triangle a-tooltip ${list_view}"  title="${issue_txt}"></i>
		</c:when>
		<c:when test="${type eq 'BUG'}">
			<i class="fa fa-lg fa-bug a-tooltip ${list_view}"  title="${bug_txt}"></i>
		</c:when>
		<c:when test="${type eq 'SUBBUG'}">
			<i class="fa fa-lg fa-bug a-tooltip ${list_view}"  title="${subbug_txt}"></i>
		</c:when>
		<c:when test="${type eq 'IDLE'}">
			<i class="fa fa-lg fa-coffee a-tooltip ${list_view}"  title="${idle_txt}"></i>
		</c:when>
		<c:when test="${type eq 'SUBTASK'}">
			<i class="fa fa-lg fa-sitemap a-tooltip ${list_view}"  title="${subtask_txt}"></i>
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
		<c:when test="${type eq 'SUBBUG'}">
			${subbug_txt}
		</c:when>
		<c:when test="${type eq 'IDLE'}">
			${idle_txt}
		</c:when>
		<c:when test="${type eq 'SUBTASK'}">
			${subtask_txt}
		</c:when>
	</c:choose>
</c:if>