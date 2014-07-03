<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
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
<div class="white-frame" style="display: table; width: 100%">
	<%--MENU --%>
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/board"/>"><span
					class="glyphicon glyphicon-list-alt"></span> <s:message
						code="agile.board" /></a></li>
			<li class="active"><a style="color: black" href="#"><span
					class="glyphicon glyphicon-book"></span> Backlog</a></li>
			<li><a style="color: black" href="#"><span
					class="glyphicon glyphicon-bullhorn"></span> <s:message
						code="agile.reports" /></a></li>
		</ul>
	</div>
	<div style="display: table-cell">
		<h4>
			<s:message code="agile.sprints" />
		</h4>
		<div style="margin: 5px 0px;">
			<s:message code="agile.create.sprint" />
			<button id="create_sprint" class="btn btn-default btn-sm">
				<span class="glyphicon glyphicon-plus"></span>
			</button>
		</div>
		<form id="create_form"
			action="<c:url value="/${project.projectId}/scrum/create"/>"
			method="post"></form>
		<c:forEach items="${sprints}" var="sprint">
			<c:set var="count" value="0" />
			<div class="table_sprint" data-id="${sprint.id}">
				<%---Buttons --%>
				<div class="buttons_panel" style="float: right">
					<c:if test="${not sprint.active}">
					<button class="btn btn-default btn-sm">
						<span class="glyphicon glyphicon-play"></span>
						<s:message code="agile.sprint.start" />
					</button>
					</c:if>
					<c:if test="${sprint.active}">
						<button class="btn btn-default btn-sm">
							<span class="glyphicon glyphicon-ok"></span>
							<s:message code="agile.sprint.finish" />
						</button>
					</c:if>
					<button class="btn btn-default btn-sm">
						<span class="glyphicon glyphicon-remove"></span>
						<s:message code="agile.sprint.delete" />
					</button>
				</div>
				<%--Sprint content --%>
				<div>
					<h4>Sprint ${sprint.sprint_no}</h4>
				</div>
				<div id="sprint_${sprint.sprint_no}">
					<%--Sprint task --%>
					<c:forEach items="${tasks}" var="task">
						<c:if test="${task.sprint eq sprint }">
						<c:set var="count" value="${count + task.story_points}" />
							<div class="agile-list" data-id="${task.id}" id="${task.id}">
								<div>
									<t:type type="${task.type}" list="true" />
									<a href="<c:url value="/task?id=${task.id}"/>"
										style="color: inherit;">[${task.id}] ${task.name}</a> <span
										class="badge theme <c:if test="${task.story_points == 0}">zero</c:if>">
										${task.story_points} </span>
									<form id="sprint_remove_${task.id}"
										action="<c:url value="/${project.projectId}/scrum/sprintRemove"/>"
										method="post">
										<input type="hidden" name="taskID" value="${task.id}">
									</form>
								</div>
							</div>
						</c:if>
					</c:forEach>
				</div>
				<div style="text-align: right;">
					<s:message code="agile.storypoints.total"/><span class="badge theme" style="margin:0px 5px;">${count}</span>
				</div>
			</div>
			<hr>
		</c:forEach>
	</div>
	<div style="display: table-cell; padding-left: 20px">
		<h4>
			<s:message code="task.tasks" />
		</h4>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${empty task.sprint}">
				<div class="agile-card" data-id="${task.id}" id="${task.id}">
					<div>
						<t:type type="${task.type}" list="true" />
						<a href="<c:url value="/task?id=${task.id}"/>"
							style="color: inherit;">[${task.id}] ${task.name}</a> <span
							class="badge theme <c:if test="${task.story_points == 0}">zero</c:if>">
							${task.story_points} </span>
						<form id="sprint_assign_${task.id}"
							action="<c:url value="/${project.projectId}/scrum/sprintAssign"/>"
							method="post">
							<input type="hidden" name="taskID" value="${task.id}"> <input
								type="hidden" id="sprintID_${task.id}" name="sprintID"
								value="${task.sprint.id}">
						</form>

					</div>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>
<script>
	$(document).ready(function($) {
		var assign_txt = "<s:message code="agile.assing"/>";
		var assing_too_txt = "<s:message code="agile.assing2"/>";
		var remove_txt = "<s:message code="agile.sprint.remove"/>";
		$("#create_sprint").click(function() {
			$("#create_form").submit();
		});

		$(".agile-card").draggable({
			revert : 'invalid',
			cursor : 'move'
		});
		$(".table_sprint").droppable({
			activeClass : "state_default",
			hoverClass : "state_hover",
			drop : function(event, ui) {
				//event on drop
				var taskID = ui.draggable.attr("id");
				var sprintID = $(this).data('id');
				$("#sprintID_" + taskID).val(sprintID);
				$("#sprint_assign_" + taskID).submit();
			},
		});
		$('.agile-card').contextPopup({
			title: assign_txt,
			items : 
			[
			 <c:forEach items="${sprints}" var="sprint">
			 {label : assing_too_txt + ' '+ "${sprint.sprint_no}",
					icon : '',
					action : function(event) {
						var taskID = event.currentTarget.id;
						var sprintID = "${sprint.id}";
						$("#sprintID_" + taskID).val(sprintID);
						$("#sprint_assign_" + taskID).submit();
						}
			},
			 </c:forEach>
			]});
		
		$('.agile-list').contextPopup({
			items : 
			[{label : remove_txt,
					icon : '',
					action : function(event) {
						var taskID = event.currentTarget.id;
						$("#sprint_remove_" + taskID).submit();
						}
			}]});
	});

	
</script>