<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="tasks_text">
	<s:message code="task.tasks" text="Tasks" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" />
</c:set>
<c:if test="${not empty param.projectID}">
	<c:set var="active_project" value="${param.projectID}" />
</c:if>
<div>
	<div style="display: table-cell;">
		<h4>${tasks_text}</h4>
	</div>
	<div style="display: table-cell; padding-left: 20px">
		<select id="project" name="project" style="width: 300px;"
			class="form-control">
			<c:forEach items="${projects}" var="project">
				<option
					<c:if test="${active_project eq project.projectId}">
						selected
					</c:if>
					value="${project.projectId}">${project.name}</option>
			</c:forEach>
		</select>
	</div>
</div>
<div class="white-frame" style="overflow: auto;">
	<security:authentication property="principal" var="user" />
	<table class="table table-condensed">
		<thead>
			<tr>
				<th style="width: 30px"><s:message code="task.type" /></th>
				<th style="width: 500px"><s:message code="task.name" /></th>
				<th><s:message code="task.progress" /></th>
				<th><s:message code="task.state" /></th>
				<th style="width: 100px"><s:message code="main.action" /></th>
			</tr>
		</thead>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.id eq user.active_task[0]}">
				<tr style="background: #428bca; color: white">
					<c:set var="blinker" value="blink" />
			</c:if>
			<c:if test="${task.id ne user.active_task[0]}">
				<c:set var="blinker" value="" />
				<c:set var="tr_bg" value="" />
				<c:if test="${task.state eq 'CLOSED'}">
					<c:set var="tr_bg" value="background: rgba(50, 205, 81, 0.12);" />
				</c:if>
				<c:if test="${task.state eq 'BLOCKED'}">
					<c:set var="tr_bg" value="background: rgba(205, 50, 50, 0.12);" />
				</c:if>
				<tr style="${tr_bg}">
			</c:if>
			<td><t:type type="${task.type}" list="true" /></td>
			<td><a href="<c:url value="task?id=${task.id}"/>"
				style="color: inherit;">[${task.id}] ${task.name}</a></td>
			<c:if test="${not task.estimated}">
				<td>${task.logged_work}</td>
			</c:if>
			<c:if test="${task.estimated}">
				<td><div class="progress" style="width: 50px">
						<c:set var="logged_class"></c:set>
						<c:set var="percentage">${100-task.percentage_left}</c:set>
						<c:if
							test="${task.percentage_logged gt 100 or task.state eq 'BLOCKED'}">
							<c:set var="logged_class">progress-bar-danger</c:set>
							<c:set var="percentage">${100-task.overCommited}</c:set>
						</c:if>
						<c:if test="${task.estimate eq '0m' && task.remaining ne '0m'}">
							<c:set var="percentage">${100- task.percentage_logged}</c:set>
						</c:if>
						<c:if test="${task.state eq 'CLOSED'}">
							<c:set var="logged_class">progress-bar-success</c:set>
						</c:if>
						<div class="progress-bar ${logged_class}" role="progressbar"
							aria-valuenow="${percentage}" aria-valuemin="0"
							aria-valuemax="100" style="width:${percentage}%"></div>
					</div></td>
			</c:if>
			<td class="${blinker}"><t:state state="${task.state}"></t:state></td>
			<td></td>
			</tr>
		</c:forEach>
	</table>
</div>
<script>
	$(document).ready(function($) {
		$("#project").change(function(){
			var link = "<c:url value="/tasks?projectID="/>";
			window.location = link + $(this).val();
		});
	});

	
</script>