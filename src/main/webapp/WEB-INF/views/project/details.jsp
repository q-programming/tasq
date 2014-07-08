<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<security:authentication property="principal" var="user" />
<c:if test="${not empty param.show}">
<c:set var="show_q">show=${param.show}&</c:set>
</c:if>
<c:if test="${not empty param.closed}">
<c:set var="closed_q">closed=${param.closed}&</c:set>
</c:if>
<c:forEach items="${project.administrators}" var="admin">
	<c:if test="${admin.id == user.id || is_admin}">
		<c:set var="can_edit" value="true" />
	</c:if>
</c:forEach>
<div class="white-frame" style="overflow: auto;">
	<c:set var="projectName_text">
		<s:message code="project.name" />
	</c:set>
	<c:set var="projectDesc_text">
		<s:message code="project.description" />
	</c:set>
	<c:if test="${can_edit}">
		<div class="pull-right">
			<a class="btn btn-default a-tooltip pull-right"
				style="padding: 6px 11px;"
				href='<s:url value="/project/manage?id=${project.id}"></s:url>'
				title="<s:message code="project.manage" text="Set as avtive" />"
				data-placement="bottom"><span class="glyphicon glyphicon-wrench"></span></a>
		</div>
	</c:if>
	<div class="pull-right">
		<c:if test="${project.id eq user.active_project}">
			<a class="btn btn-default a-tooltip pull-right"
				style="padding: 6px 11px;" href='#'
				title="<s:message
									code="project.active" text="Set as avtive" />"
				data-placement="bottom"> <img
				src="<c:url value="/resources/img/active.gif"/>"></img></a>
		</c:if>
		<c:if test="${project.id ne user.active_project}">
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
	<c:set var="tasks_total">${TO_DO + ONGOING+ CLOSED+ BLOCKED}</c:set>
	<c:set var="tasks_todo">${TO_DO * 100 / tasks_total }</c:set>
	<c:set var="tasks_ongoing">${ONGOING * 100 / tasks_total}</c:set>
	<c:set var="tasks_closed">${CLOSED *100 / tasks_total}</c:set>
	<c:set var="tasks_blocked">${BLOCKED*100 / tasks_total}</c:set>
	<div class="progress">
		<div class="progress-bar progress-bar-warning a-tooltip"
			style="width: ${tasks_todo}%"
			title="${TO_DO} <s:message code="task.state.todo"/>">
			<c:if test="${tasks_todo gt 10.0}">
				<span>${TO_DO} <s:message code="task.state.todo" /></span>
			</c:if>
		</div>
		<div class="progress-bar a-tooltip" style="width: ${tasks_ongoing}%"
			title="${ONGOING} <s:message code="task.state.ongoing"/>">
			<c:if test="${tasks_ongoing gt 10.0}">
				<span>${ONGOING} <s:message code="task.state.ongoing" /></span>
			</c:if>
		</div>
		<div class="progress-bar progress-bar-success a-tooltip"
			style="width: ${tasks_closed}%"
			title="${CLOSED} <s:message code="task.state.closed"/>">
			<c:if test="${tasks_closed gt 10.0}">
				<span>${CLOSED} <s:message code="task.state.closed" /></span>
			</c:if>
		</div>
		<div class="progress-bar progress-bar-danger a-tooltip"
			style="width: ${tasks_blocked}%"
			title="${BLOCKED} <s:message code="task.state.blocked"/>">
			<c:if test="${tasks_blocked gt 10.0}">
				<span>${BLOCKED} <s:message code="task.state.blocked" /></span>
			</c:if>
		</div>
	</div>
	<div style="display: table; width: 100%">
		<div style="display: table-cell; width: 600px">
			<%------------------------------ EVENTS ------------------------%>
			<h3>
				<s:message code="project.latestEvents" />
			</h3>
			<%-- Next previous div --%>
			<div>
				<c:choose>
					<c:when test="${fn:length(events) eq 25 && empty param['show']}">
						<div class="pull-left"></div>
						<div class="pull-right">
							<a class="btn btn-default btn-xxs"
								href="<c:url value="/project?${closed_q}id=${project.id}&show=1"/>"><s:message
									code="project.next" /></a>
						</div>
					</c:when>
					<c:when test="${fn:length(events) ge 25 && param['show'] gt 1}">
						<div class="pull-left">
							<a class="btn btn-default btn-xxs"
								href="<c:url value="/project?${closed_q}id=${project.id}"/>"><s:message
									code="project.previous" /></a>
						</div>
						<div class="pull-right">
							<a class="btn btn-default btn-xxs"
								href="<c:url value="/project?${closed_q}id=${project.id}&show=${param.show +1}"/>"><s:message
									code="project.next" /></a>
						</div>
					</c:when>
					<c:when test="${fn:length(events) lt 25 && param['show'] eq 1}">
						<div class="pull-left">
							<a class="btn btn-default btn-xxs"
								href="<c:url value="/project?${closed_q}id=${project.id}"/>"><s:message
									code="project.previous" /></a>
						</div>
						<div class="pull-right"></div>
					</c:when>
					<c:when
						test="${fn:length(events) lt 25 && not empty param['show']}">
						<div class="pull-left">
							<a class="btn btn-default btn-xxs"
								href="<c:url value="/project?${closed_q}id=${project.id}&show=${param['show']-1}"/>"><s:message
									code="project.previous" /></a>
						</div>
						<div class="pull-right"></div>
					</c:when>
					<c:when
						test="${fn:length(events) eq 25 && not empty param['show']}">
						<div class="pull-left">
							<a class="btn btn-default btn-xxs"
								href="<c:url value="/project?${closed_q}id=${project.id}&show=${param['show']-1}"/>"><s:message
									code="project.previous" /></a>
						</div>
						<div class="pull-right">
							<a class="btn btn-default btn-xxs"
								href="<c:url value="/project?${closed_q}id=${project.id}&show=1"/>"><s:message
									code="project.next" /></a>
						</div>
					</c:when>
				</c:choose>
			</div>
			<table class="table">
				<c:forEach items="${events}" var="worklog">
					<tr>
						<td><img data-src="holder.js/30x30"
							style="height: 30px; float: left; padding-right: 10px;"
							src="<c:url value="/userAvatar/${worklog.account.id}"/>" />
							${worklog.account} <t:logType logType="${worklog.type}" /> <c:if
								test="${not empty worklog.task}">
								<a href="<c:url value="/task?id=${worklog.task.id}"/>">[${worklog.task.id}]
									${worklog.task.name}</a>
							</c:if>
							<div class="pull-right">${worklog.timeLogged}</div> <c:if
								test="${not empty worklog.message}">
								<div>${worklog.message}</div>
							</c:if></td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<%------------------------TASKS -------------------------------%>
		<div style="display: table-cell; padding-left: 30px">
			<h3>
				<a href="<c:url value="/tasks"/>" style="color: black"><s:message
						code="task.tasks" /></a>
				<c:if test="${not empty param.closed}">
					<a href="<s:url value="/project?${show_q}id=${project.id}"></s:url>"><span
						style="display: inherit; font-size: small; font-weight: normal; color:black;float: right">
							<span class="glyphicon glyphicon-check"></span> <s:message
								code="project.hideClosed"></s:message>
					</span></a>
				</c:if>
				<c:if test="${empty param.closed}">
					<a
						href="<s:url value="/project?${show_q}id=${project.id}&closed=hide"></s:url>"><span
						style="display: inherit; font-size: small; font-weight: normal; color:black; float: right">
							<span class="glyphicon glyphicon-unchecked"></span> <s:message
								code="project.hideClosed"></s:message>
					</span></a>
				</c:if>

			</h3>
			<table class="table">
				<c:forEach items="${tasks}" var="task">
					<tr>
						<td><t:type type="${task.type}" list="true" /></td>
						<td><t:priority priority="${task.priority}" list="true" /></td>
						<td><a href="<c:url value="task?id=${task.id}"/>"
							style="<c:if test="${task.state eq 'CLOSED' }">
							text-decoration: line-through;
							</c:if>">[${task.id}]
								${task.name}</a>
						<td>
						<td><c:set var="logged_class"></c:set> <c:if
								test="${task.percentage_logged gt 100 or task.state eq 'BLOCKED'}">
								<c:set var="logged_class">progress-bar-danger</c:set>
							</c:if> <c:if test="${task.state eq 'CLOSED'}">
								<c:set var="logged_class">progress-bar-success</c:set>
							</c:if> <c:if test="${not task.estimated}">
								<div>${task.logged_work}</div>
							</c:if> <c:if test="${task.estimated}">
								<div class="progress" style="width: 50px">
									<div class="progress-bar ${logged_class}" role="progressbar"
										aria-valuenow="${task.percentage_logged}" aria-valuemin="0"
										aria-valuemax="100" style="width:${task.percentage_logged}%"></div>
								</div>
							</c:if></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>
<script>
	$(document).ready(function($) {
	});

	
</script>