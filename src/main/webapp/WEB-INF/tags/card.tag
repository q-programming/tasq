<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ attribute name="task" required="true"
	type="com.qprogramming.tasq.task.DisplayTask"%>
<%@ attribute name="can_edit" required="true"%>
<security:authentication property="principal" var="user" />
<div class="agile-card theme" data-id="${task.id}" state="${task.state}"
	id="${task.id}">
	<div class="side-bar theme"></div>
	<div style="padding-left: 5px; min-height: 50px;">
		<t:type type="${task.type}" list="true" />
		<a href="<c:url value="/task?id=${task.id}"/>" style="color: inherit;"
			class="<c:if test="${task.state eq 'CLOSED' }">
							closed
							</c:if>">[${task.id}]
			${task.name}</a> 
			<c:if test="${task.story_points ne 0}">
				<span class="badge theme">${task.story_points}</span>
			</c:if>
	</div>
	<div
		style="display: table; width: 100%; margin-top: 5px; min-height: 30px;">
		<div style="display: table-row">
			<div style="display: table-cell; vertical-align: bottom;">
				<c:if test="${can_edit}">
					<c:if test="${task.assignee.id eq user.id}">
						<button class="btn btn-default btn-xxs a-tooltip worklog"
							style="margin-left: 5px" type="button" data-toggle="modal"
							data-target="#logWorkform" data-taskID="${task.id}" id="log_time"
							title="<s:message code="task.logWork"/>">
							<i class="fa fa-lg fa-clock-o"></i>
						</button>
					</c:if>
				</c:if>
				<%-- 			<span class="a-tooltip" title="<s:message code="task.remaining"/>">${task.percentage}</span> --%>
			</div>
			<%---Assignee--%>
			<div id="assignee_${task.id}"
				style="margin-top: 10px; text-align: right; display: table-cell; vertical-align: bottom;">
				<c:if test="${empty task.assignee}">
					<i><s:message code="task.unassigned" /> <c:if
							test="${can_edit}">
						&nbsp;<button class="btn btn-default assignToTask btn-xxs a-tooltip assign_me"
										title="<s:message code="task.assign"/>" data-toggle="modal" data-target="#assign_modal" 
										data-taskID="${task.id}" data-assignee="${task.assignee}" 
										data-assigneeID="${task.assignee.id}"
										data-projectID="${task.projectID}"
								type="button" data-taskID="${task.id}"
								title="<s:message code="task.assignme"/>">
								<i class="fa fa-lg fa-user"></i>
							</button>
						</c:if> </i>
				</c:if>
				<c:if test="${not empty task.assignee}">
					<a ${link} class="a-tooltip"
						href="<c:url value="/user?id=${task.assignee.id}"/>"
						title="${task.assignee}"><img data-src="holder.js/30x30"
						style="height: 30px; padding-right: 5px;"
						src="<c:url value="/../avatar/${task.assignee.id}.png"/>" />
					</a><c:if test="${task.state ne 'CLOSED'}}"><button class="btn btn-default assignToTask btn-xxs a-tooltip assign_me"
										title="<s:message code="task.assign"/>" data-toggle="modal" data-target="#assign_modal" 
										data-taskID="${task.id}" data-assignee="${task.assignee}" 
										data-assigneeID="${task.assignee.id}"
										data-projectID="${task.projectID}"
										type="button" data-taskID="${task.id}"
										title="<s:message code="task.assignme"/>">
								<i class="fa fa-lg fa-user"></i>
						</button>
					</c:if>
				</c:if>
			</div>
		</div>
		</div>
		<div style="display: table">
			<c:if test="${task.estimated}">
				<div class="progress" style="height: 5px; width: 150px;padding-left:5px">
					<c:set var="logged_class"></c:set>
					<c:set var="percentage">${task.percentage}</c:set>
					<c:if test="${task.state eq 'TO_DO'}">
						<c:set var="percentage">0</c:set>
					</c:if>
					<c:if test="${task.state eq 'CLOSED'}">
						<c:set var="logged_class">progress-bar-success</c:set>
						<c:set var="percentage">100</c:set>
					</c:if>
					<c:if test="${task.state eq 'BLOCKED'}">
						<c:set var="logged_class">progress-bar-danger</c:set>
					</c:if>
					<div class="progress-bar ${logged_class} a-tooltip" title="${percentage}%" role="progressbar"
						aria-valuenow="${percentage}" aria-valuemin="0"
						aria-valuemax="100" style="width:${percentage}%"></div>
				</div>
			</c:if>
		</div>
	
</div>