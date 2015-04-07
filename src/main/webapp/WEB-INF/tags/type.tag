<%@ tag language="java" pageEncoding="UTF-8" 
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib uri="http://www.springframework.org/tags" prefix="s"
%><%@ attribute name="type" required="false"
%><%@ attribute name="show_text" required="false"
%><%@ attribute name="list" required="false"%>
<c:if test="${empty list}">
<c:set var="list_view">fa-border</c:set>
</c:if>
<c:if test="${not empty list}">
<c:set var="list_view">fa-fw</c:set>
</c:if>
<c:if test="${not empty type}">
<c:choose>
<c:when test="${type eq 'TASK'}">
<i class="fa fa-lg fa-check-square a-tooltip ${list_view}"
title="<s:message code="task.type.task" />"></i>
</c:when>
<c:when test="${type eq 'USER_STORY'}">
<i class="fa fa-lg fa-lightbulb-o a-tooltip ${list_view}"
title="<s:message code="task.type.user_story" />"></i>
</c:when>
<c:when test="${type eq 'ISSUE'}">
<i class="fa fa-lg fa-exclamation-triangle a-tooltip ${list_view}"
title="<s:message code="task.type.issue" />"></i>
</c:when>
<c:when test="${type eq 'BUG'}">
<i class="fa fa-lg fa-bug a-tooltip ${list_view}"
title="<s:message code="task.type.bug" />"></i>
</c:when>
<c:when test="${type eq 'SUBBUG'}">
<i class="fa fa-lg fa-bug a-tooltip ${list_view}"
title="<s:message code="task.type.subbug" />"></i>
</c:when>
<c:when test="${type eq 'IDLE'}">
<i class="fa fa-lg fa-coffee a-tooltip ${list_view}"
title="<s:message code="task.type.idle" />"></i>
</c:when>
<c:when test="${type eq 'SUBTASK'}">
<i class="fa fa-lg fa-check-circle-o a-tooltip ${list_view}"
title="<s:message code="task.type.subtask" />"></i>
</c:when>
</c:choose>
</c:if>
<c:if test="${not empty show_text}">
<c:choose>
<c:when test="${type eq 'TASK'}">
<s:message code="task.type.task" />
</c:when>
<c:when test="${type eq 'USER_STORY'}">
<s:message code="task.type.user_story" />
</c:when>
<c:when test="${type eq 'ISSUE'}">
<s:message code="task.type.issue" />
</c:when>
<c:when test="${type eq 'BUG'}">
<s:message code="task.type.bug" />
</c:when>
<c:when test="${type eq 'SUBBUG'}">
<s:message code="task.type.subbug" />
</c:when>
<c:when test="${type eq 'IDLE'}">
<s:message code="task.type.idle" />
</c:when>
<c:when test="${type eq 'SUBTASK'}">
<s:message code="task.type.subtask" />
</c:when>
</c:choose>
</c:if>