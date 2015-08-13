<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="myfn" uri="/WEB-INF/tags/custom.tld"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<c:set var="tasks_text">
	<s:message code="task.tasks" text="Tasks" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" arguments="" />
</c:set>
<security:authentication property="principal" var="user" />
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<c:if
	test="${myfn:contains(project.participants,user) && user.isPowerUser || is_admin}">
	<c:set var="can_edit" value="true" />
</c:if>
<h3>[${project.projectId}] ${project}</h3>
<div class="white-frame"
	style="display: table; width: 100%; height: 85vh">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/backlog"/>"><i
					class="fa fa-book"></i> Backlog</a></li>
			<li class="active"><a style="color: black" href="#"><i
					class="fa fa-list-alt"></i> <s:message code="agile.board" /></a></li>
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/reports"/>"><i
					class="fa fa-line-chart"></i> <s:message code="agile.reports" /></a></li>
		</ul>
	</div>
	<table style="width:100%">
		<tr>
			<td>
				<h4 style="padding-bottom: 20px">
				Sprint ${sprint.sprintNo} <span
					style="font-size: small; margin-left: 5px">(${sprint.start_date}
					- ${sprint.end_date})</span>
				</h4>
			</td>
			<td colspan="2" style="vertical-align: initial;">
				<i class="fa fa-tags"></i>&nbsp;<s:message code="task.tags"/>: <c:forEach items="${tags}" var="tag">
					<span class="tag label label-info theme tag_filter a-tooltip" title="<s:message code="task.tags.click.filter"/>" data-name="${tag}">${tag}</span>
				</c:forEach>
			</td>
			<td>
				<span class="btn btn-default pull-right" id="save_order"
					style="display: none"><i class="fa fa-floppy-o"></i>&nbsp;Save order
				</span>
			</td>
		</tr>
	<jsp:include page="../agile/board.jsp" />
</div>
<jsp:include page="../modals/logWork.jsp" />
<jsp:include page="../modals/close.jsp" />
<jsp:include page="../modals/assign.jsp" />