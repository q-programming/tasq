<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div class="white-frame" style="overflow: auto;">
	<security:authentication property="principal" var="user" />
	<c:set var="projectName_text">
		<s:message code="project.name" />
	</c:set>
	<c:set var="projectDesc_text">
		<s:message code="project.description" />
	</c:set>
	<div class="pull-right">
		<c:if test="${project.active}">
			<a class="btn btn-default a-tooltip pull-right"
				style="padding: 6px 11px;" href='#'
				title="<s:message
									code="project.active" text="Set as avtive" />"
				data-placement="bottom"> <img
				src="<c:url value="/resources/img/active.gif"/>"></img></a>
		</c:if>
		<c:if test="${not project.active}">
			<a class="btn btn-default a-tooltip pull-right"
				href='<s:url value="/project/activate?id=${project.id}"></s:url>'
				title="<s:message
									code="project.activate" text="Set as avtive" />"
				data-placement="bottom"> <span
				class="glyphicon glyphicon-refresh"></span>
			</a>
		</c:if>
	</div>
	<h3>[${project.projectId}] ${project.name}</h3>
	${project.description}
	<hr>
	<div><s:message code="task.state.todo"/> ${TO_DO}</div>
	<div><s:message code="task.state.ongoing"/> ${ONGOING}</div>
	<div><s:message code="task.state.closed"/> ${CLOSED}</div>
	<div><s:message code="task.state.blocked"/> ${BLOCKED}</div>
	
	<div style="display: table-cell; width: 65%">
		<h3>
			<s:message code="project.latestEvents" />
		</h3>
		<table class="table">
			<c:forEach items="${events}" var="worklog">
				<tr>
					<td><img data-src="holder.js/30x30"
						style="height: 30px; float: left; padding-right: 10px;"
						src="<c:url value="/userAvatar/${worklog.account.id}"/>" />
						${worklog.account} <t:logType logType="${worklog.type}" /> <a
						href="<c:url value="/task&id=${worklog.task.id}"/>">[${worklog.task.id}]
							${worklog.task.name}</a>
						<div class="pull-right">${worklog.timeLogged}</div>
						</div> <c:if test="${not empty worklog.message}">
							<div>${worklog.message}</div>
						</c:if></td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div style="display: table-cell; padding-left: 30px">
		<h3>
			<s:message code="task.tasks" />
		</h3>
		<table class="table">
			<c:forEach items="${project.tasks}" var="task">
				<tr>
					<td><t:type type="${task.type}" list="true" /></td>
					<td><a href="<c:url value="task?id=${task.id}"/>">[${task.id}]
							${task.name}</a>
					<td>
					<td><div class="progress" style="width: 50px">
							<c:set var="logged_class"></c:set>
							<c:if
								test="${task.percentage_logged gt 100 or task.state eq 'BLOCKED'}">
								<c:set var="logged_class">progress-bar-danger</c:set>
							</c:if>
							<c:if test="${task.state eq 'CLOSED'}">
								<c:set var="logged_class">progress-bar-success</c:set>
							</c:if>
							<div class="progress-bar ${logged_class}" role="progressbar"
								aria-valuenow="${task.percentage_logged}" aria-valuemin="0"
								aria-valuemax="100" style="width:${task.percentage_logged}%"></div>
						</div></td>
				</tr>
			</c:forEach>
		</table>
	</div>

</div>
<script>
	$(document).ready(function($) {
	});

	
</script>