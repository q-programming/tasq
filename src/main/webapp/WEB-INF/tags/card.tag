<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ attribute name="task" required="true"
	type="com.qprogramming.tasq.task.Task"%>
<div class="agile-card theme" data-id="${task.id}" id="${task.id}">
	<div>
		<t:type type="${task.type}" list="true" />
		<a href="<c:url value="/task?id=${task.id}"/>" style="color: inherit;<c:if test="${task.state eq 'CLOSED' }">
							text-decoration: line-through;
							</c:if>">[${task.id}]
			${task.name}</a> <span
			class="badge theme <c:if test="${task.story_points == 0}">zero</c:if>">${task.story_points}
		</span>
	</div>
	<div style="display: table; width: 100%; margin-top: 5px;">
		<div style="display: table-cell; vertical-align: bottom;">
			<button class="btn btn-default btn-xxs a-tooltip" type="button"
				id="log_time" title="<s:message code="task.logWork"/>">
				<span class="glyphicon glyphicon-time"></span>
			</button>
			<span class="a-tooltip" title="<s:message code="task.remaining"/>">${task.remaining}</span>
		</div>
		<%---Assignee--%>
		<div style="margin-top: 10px; text-align: right; display: table-cell;">
			<c:if test="${empty task.assignee}">
				<i><s:message code="task.unassigned" />
					<button class="btn btn-default btn-xxs a-tooltip" type="button"
						id="assign_me" title="<s:message code="task.assignme"/>">
						<span class="glyphicon glyphicon-user"></span>
					</button></i>
				<form id="state_form_${task.id}" name="state_form" method="post"
					action="<c:url value="/task/state"/>">
					<input type="hidden" name="taskID" value="${task.id}"> <input
						id="state_${task.id}" type="hidden" name="state"
						value="${task.state}">
				</form>
				<form id="assign" action="<c:url value="/task/assign"/>"
					method="post">
					<input type="hidden" name="taskID" value="${task.id}">
				</form>

			</c:if>
			<c:if test="${not empty task.assignee}">
				<img data-src="holder.js/20x20"
					style="height: 20px; padding-right: 5px;"
					src="<c:url value="/userAvatar/${task.assignee.id}"/>" />
				<form id="state_form_${task.id}" name="state_form" method="post"
					action="<c:url value="/task/state"/>">
					<input type="hidden" name="taskID" value="${task.id}"> <input
						id="state_${task.id}" type="hidden" name="state"
						value="${task.state}">
				</form>
				<a ${link} href="<c:url value="/user?id=${task.assignee.id}"/>">${task.assignee}</a>
			</c:if>
		</div>
	</div>
</div>
