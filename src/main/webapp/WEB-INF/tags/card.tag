<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ attribute name="task" required="true"
	type="com.qprogramming.tasq.task.Task"%>
<div class="agile-card" data-id="${task.id}">
	<div>
		<t:type type="${task.type}" list="true" />
		<a href="<c:url value="task?id=${task.id}"/>" style="color: inherit;">[${task.id}]
			${task.name}</a>
	</div>
	<div style="margin-top: 10px; text-align: right;">

		<c:if test="${empty task.assignee}">
			<i><s:message code="task.unassigned" />
				<button class="btn btn-default btn-xxs a-tooltip" type="button"
					id="assign_me" title="<s:message code="task.assignme"/>">
					<span class="glyphicon glyphicon-user"></span>
				</button></i>
			<form id="state_from" name="state_from" method="post"
				action="<c:url value="/task/state"/>">
				<input type="hidden" name="taskID" value="${task.id}">
				<input type="hidden" name="state" value="${task.state}">
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
			<a ${link} href="<c:url value="/user?id=${task.assignee.id}"/>">${task.assignee}</a>
		</c:if>
	</div>
</div>
