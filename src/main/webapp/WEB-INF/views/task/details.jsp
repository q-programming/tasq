<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="white-frame" style="overflow: auto;">
	<security:authentication property="principal" var="user" />
	<c:set var="taskName_text">
		<s:message code="task.name" text="Name" />
	</c:set>
	<c:set var="taskDesc_text">
		<s:message code="task.description" />
	</c:set>
	<h3>
		<t:type type="${task.type}" />
		[${task.id}] ${task.name}
	</h3>
	<h4>
		<s:message code="task.state" />
		:
		<t:state state="${task.state}" />
	</h4>
	<hr>
	${task.description}
	<hr>
	<!-- Button trigger modal -->
	<button class="btn btn-default btn-sm" data-toggle="modal"
		data-target="#changeState">
		<span class="glyphicon glyphicon-list-alt"></span>
		<s:message code="task.changeState" />
	</button>

	<!-- Button trigger modal -->
	<button class="btn btn-default btn-sm" data-toggle="modal"
		data-target="#logWork">
		<span class="glyphicon glyphicon-time"></span>
		<s:message code="task.logWork"></s:message>
	</button>
	<%--ESTIMATES TAB --%>
	<c:set var="estimate_value">100</c:set>
	<c:if test="${task.percentage_logged gt 100}">
		<c:set var="estimate_value">${100 + task.percentage_left}</c:set>
	</c:if>
	<table style="width: 400px">
		<%-- IF ESTIMATE IS 0 --%>
		<c:if test="${task.estimate eq '0m'}">
			<tr>
				<td class="bar_td"><s:message code="task.logged" /></td>
				<td class="bar_td">${task.logged_work}</td>
				<td class="bar_td"></td>
			</tr>
		</c:if>
		<%-- IF ESTIMATE IS NOT 0 --%>
		<c:if test="${task.estimate ne '0m'}">
			<tr>
				<td class="bar_td" style="width: 50px"><s:message
						code="task.estimate" /></td>
				<td style="width: 150px"><div class="progress">
						<div class="progress-bar" role="progressbar"
							aria-valuenow="${estimate_value}" aria-valuemin="0"
							aria-valuemax="100" style="width: ${estimate_value}%;"></div>
					</div></td>
				<td class="bar_td">${task.estimate}</td>
			</tr>
			<tr>
				<td class="bar_td"><s:message code="task.logged" /></td>
				<td><div class="progress">
						<c:set var="logged_class">progress-bar-warning</c:set>
						<c:if test="${task.percentage_logged gt 100}">
							<c:set var="logged_class">progress-bar-danger</c:set>
						</c:if>
						<div class="progress-bar ${logged_class}" role="progressbar"
							aria-valuenow="${task.percentage_logged}" aria-valuemin="0"
							aria-valuemax="100" style="width:${task.percentage_logged}%"></div>
					</div></td>
				<td class="bar_td">${task.logged_work}</td>
			</tr>
			<tr>
				<td class="bar_td"><s:message code="task.remaining" /></td>
				<td><div class="progress">
						<div class="progress-bar progress-bar-success" role="progressbar"
							aria-valuenow="${task.percentage_left}" aria-valuemin="0"
							aria-valuemax="100"
							style="width:${task.percentage_left}% ; float:right"></div>
					</div></td>
				<td class="bar_td">${task.remaining }</td>
			</tr>
		</c:if>
	</table>
	<%--------------------------- WORKLOG------------------------------------%>
	<hr>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#"><s:message
					code="task.activeLog" /></a></li>
	</ul>

	<table class="table table-condensed">
		<c:forEach items="${task.worklog}" var="worklog">
			<tr>
				<td><div style="font-size: smaller; color: dimgray;">${worklog.account}
						<t:logType logType="${worklog.type}" />
						<div class="pull-right">${worklog.time}</div>
					</div> <c:if test="${not empty worklog.message}">
						${worklog.message}
					</c:if></td>
			</tr>
		</c:forEach>
	</table>
</div>
<!-- LOG WORK MODAL -->
<div class="modal fade" id="logWork" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">
					<s:message code="task.logWork" />
				</h4>
			</div>
			<form id="mainForm" name="mainForm" method="post"
				action="<c:url value="/logwork"/>">
				<div class="modal-body">

					<input type="hidden" name="taskID" value="${task.id}">
					<div class="form-group">
						<label>Log work</label> <input id="logged_work" name="logged_work"
							style="width: 150px" class="form-control" type="text" value="">
						<span class="help-block"><s:message
								code="task.logWork.help"></s:message> </span>
					</div>

				</div>
				<div class="modal-footer">
					<button class="btn btn-default" type="submit">
						<s:message code="main.log" />
					</button>
					<a class="btn" data-dismiss="modal"><s:message
							code="main.cancel" /></a>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- CHANGE STATE MODAL -->
<%
	pageContext.setAttribute("states", TaskState.values());
%>
<div class="modal fade" id="changeState" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">
					<s:message code="task.changeState" />
				</h4>
			</div>
			<form id="mainForm" name="mainForm" method="post"
				action="<c:url value="/task/state"/>">
				<div class="modal-body">
					<input type="hidden" name="taskID" value="${task.id}">
					<div class="form-group">
						<label>Change state</label> <select name="state"
							class="form-control">
							<c:forEach items="${states}" var="state">
								<option value="${state}">${state.description}</option>
							</c:forEach>
						</select> <span class="help-block">Choose new task state</span>
					</div>

				</div>
				<div class="modal-footer">
					<button class="btn btn-default" type="submit">
						<s:message code="main.change" />
					</button>
					<a class="btn" data-dismiss="modal"><s:message
							code="main.cancel" /></a>
				</div>
			</form>
		</div>
	</div>
</div>

<script>
	$(document).ready(function($) {
	});

	
</script>