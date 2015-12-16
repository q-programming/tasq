<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ attribute name="state" required="true"%>
<c:choose>
<c:when test="${state eq 'TO_DO'}">
<span class="state_span"><i class="fa fa-lg fa-pencil-square-o"></i> 
<s:message code="task.state.todo"></s:message></span>
</c:when>
<c:when test="${state eq 'ONGOING'}">
<span class="state_span"><i class="fa fa-lg fa-spin fa-repeat"></i> <s:message
code="task.state.ongoing"></s:message></span>
</c:when>
<c:when test="${state eq 'BLOCKED'}">
<span class="state_span"><i class="fa fa-lg fa-ban"></i> <s:message
code="task.state.blocked"></s:message></span>
</c:when>
<c:when test="${state eq 'CLOSED'}">
<span class="state_span"><i class="fa fa-lg fa-archive"></i> 
<s:message code="task.state.closed"></s:message></span>
</c:when>
<c:when test="${state eq 'OPEN'}">
<span class="state_span"><i class="fa fa-lg fa-refresh"></i> 
<s:message code="task.state.open"></s:message></span>
</c:when>
<c:when test="${state eq 'COMPLETE'}">
<span class="state_span"><i class="fa fa-lg fa-check"></i> 
<s:message code="task.state.complete"></s:message></span>
</c:when>
<c:otherwise>
<span class="state_span"><i class="fa fa-lg fa-list"></i> 
<s:message code="task.showAll"></s:message></span>
</c:otherwise>
</c:choose>