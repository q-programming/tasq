<%@page import="com.qprogramming.tasq.task.TaskState"%>
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
<security:authentication property="principal" var="user" />
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
	<%--------------FILTERS ----------------------------%>
	<c:if
		test="${not empty param.projectID || not empty param.state || not empty param.query}">
		<c:if test="${not empty param.projectID}">
			<c:set var="projID_url">
								projectID=${param.projectID}&
							</c:set>
		</c:if>
		<c:if test="${not empty param.state}">
			<c:set var="state_url">
								state=${param.state}&
				</c:set>
		</c:if>
		<c:if test="${not empty param.query}">
			<c:set var="query_url">
								query=${param.query}&
				</c:set>
		</c:if>

		<div style="display: table-cell; padding-left: 20px; width: 100%">
			<c:if test="${not empty param.projectID}">
				<span><s:message code="project.project" />: <span
					class="filter_span"> ${param.projectID}<a
						href="<c:url value="tasks?${state_url}${query_url}"/>"><span
							class="glyphicon glyphicon-remove"
							style="font-size: smaller; margin-left: 3px; color: lightgray"></span></a></span></span>
			</c:if>
			<c:if test="${not empty param.state}">
				<span><s:message code="task.state" />: <span
					class="filter_span"><t:state state="${param.state}" /> <a
						href="<c:url value="tasks?${projID_url}${query_url}"/>"><span
							class="glyphicon glyphicon-remove"
							style="font-size: smaller; margin-left: 3px; color: lightgray"></span></a></span></span>
			</c:if>
			<c:if test="${not empty param.query}">
				<c:set var="query_url">
								query=${param.query}&
				</c:set>
				<span><s:message code="main.search" />: <span
					class="filter_span"> ${param.query}<a
						href="<c:url value="tasks?${projID_url}${state_url}"/>"><span
							class="glyphicon glyphicon-remove"
							style="font-size: smaller; margin-left: 3px; color: lightgray"></span></a></span></span>
			</c:if>
		</div>
	</c:if>
</div>
<div class="white-frame">
	<security:authentication property="principal" var="user" />
	<table class="table table-condensed">
		<thead>
			<tr>
				<th style="width: 30px"><s:message code="task.type" /></th>
				<th style="width: 500px"><s:message code="task.name" /></th>
				<th><s:message code="task.progress" /></th>
				<th>
					<div class="dropdown" style="padding-top: 5px; cursor: pointer;">
						<a class="dropdown-toggle" type="button" id="dropdownMenu1"
							data-toggle="dropdown" style="color: black"><s:message
								code="task.state" /><span class="caret"></span></a>
						<%
							pageContext.setAttribute("states", TaskState.values());
						%>
						<ul class="dropdown-menu">
							<c:forEach items="${states}" var="state">
								<li><a
									href="<c:url value="tasks?${projID_url}${query_url}state=${state}"/>"><t:state
											state="${state}"></t:state></a></li>
							</c:forEach>
						</ul>
					</div>

				</th>
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
			var query = "${query_url}";
			var state = "${state_url}";
			var link = "<c:url value="/tasks?projectID="/>" + $(this).val()+"&" + query + state;
			window.location = link + $(this).val();
		});
	});

	
</script>