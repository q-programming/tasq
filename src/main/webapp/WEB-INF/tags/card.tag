<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ attribute name="task" required="true"
	type="com.qprogramming.tasq.task.Task"%>
<%@ attribute name="can_edit" required="true"%>
<div class="agile-card theme" data-id="${task.id}" state="${task.state}" id="${task.id}">
	<div class="side-bar theme"></div>
	<div style="padding-left: 5px;">
		<t:type type="${task.type}" list="true" />
		<a href="<c:url value="/task?id=${task.id}"/>"
			style="color: inherit;<c:if test="${task.state eq 'CLOSED' }">
							text-decoration: line-through;
							</c:if>">[${task.id}]
			${task.name}</a> <span
			class="badge theme <c:if test="${task.story_points == 0}">zero</c:if>">${task.story_points}
		</span>
	</div>
	<div style="display: table; width: 100%; margin-top: 5px;">
		<div style="display: table-cell; vertical-align: bottom;">
			<c:if test="${can_edit}">
				<button class="btn btn-default btn-xxs a-tooltip worklog" type="button"
					data-toggle="modal" data-target="#logWorkform" data-taskID="${task.id}" id="log_time"
					title="<s:message code="task.logWork"/>">
					<span class="glyphicon glyphicon-time"></span>
				</button>
			</c:if>
			<span class="a-tooltip" title="<s:message code="task.remaining"/>">${task.remaining}</span>
		</div>
		<%---Assignee--%>
		<div id="assignee_${task.id}" style="margin-top: 10px; text-align: right; display: table-cell;">
			<c:if test="${empty task.assignee}">
				<i><s:message code="task.unassigned" />
					<c:if test="${can_edit}">
						<button class="btn btn-default btn-xxs a-tooltip assign_me" type="button"
							data-taskID="${task.id}" title="<s:message code="task.assignme"/>">
							<span class="glyphicon glyphicon-user"></span>
						</button>
					</c:if>
				</i>
			</c:if>
			<c:if test="${not empty task.assignee}">
				<img data-src="holder.js/20x20"
					style="height: 20px; padding-right: 5px;"
					src="<c:url value="/userAvatar/${task.assignee.id}"/>" />
				<a ${link} href="<c:url value="/user?id=${task.assignee.id}"/>">${task.assignee}</a>
			</c:if>
		</div>
	</div>
</div>
