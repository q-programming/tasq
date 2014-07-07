<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="myfn" uri="/WEB-INF/tags/custom.tld"%>
<c:set var="tasks_text">
	<s:message code="task.tasks" text="Tasks" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" />
</c:set>
<security:authentication property="principal" var="user" />
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<c:if test="${myfn:contains(project.administrators,user) || is_admin}">
	<c:set var="can_edit" value="true" />
</c:if>
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
	<div style="display: table-cell; width: 55%">
		<h4>
			<s:message code="agile.sprints" />
		</h4>
		<c:if test="${can_edit}">
			<div style="margin: 5px 0px;">
				<s:message code="agile.create.sprint" />
				<button id="create_sprint" class="btn btn-default btn-sm">
					<span class="glyphicon glyphicon-plus"></span>
				</button>
			</div>
		</c:if>
		<form id="create_form"
			action="<c:url value="/${project.projectId}/scrum/create"/>"
			method="post"></form>
		<c:forEach items="${sprints}" var="sprint">
			<c:set var="count" value="0" />
			<div class="table_sprint" data-id="${sprint.id}">
				<%---Buttons --%>
				<%-- Only print button for sprint at top (active or not_active) --%>
				<c:if test="${can_edit}">
					<div class="buttons_panel" style="float: right">
						<c:if test="${not sprint.active}">
							<c:if test="${not b_rendered}">
								<c:set var="b_rendered" value="true" />
								<button class="btn btn-default btn-sm" id="start-sprint"
									data-sprint="${sprint.id}" data-toggle="modal"
									data-target="#startSprint">
									<span class="glyphicon glyphicon-play"></span>
									<s:message code="agile.sprint.start" />
								</button>
							</c:if>
							<a class="btn btn-default btn-sm a-tooltip delete_sprint"
								href="<c:url value="/scrum/delete?id=${sprint.id}"/>"
								title="<s:message code="agile.sprint.delete" />"
								data-lang="${pageContext.response.locale}"
								data-msg='<s:message code="agile.sprint.delete.confirm"></s:message>'>
								<span class="glyphicon glyphicon-trash"></span>
							</a>
						</c:if>
						<c:if test="${sprint.active}">
							<c:set var="b_rendered" value="true" />
							<button class="btn btn-default btn-sm">
								<span class="glyphicon glyphicon-ok"></span>
								<s:message code="agile.sprint.finish" />
							</button>
						</c:if>

					</div>
				</c:if>
				<%--Sprint content --%>
				<div>
					<h4>Sprint ${sprint.sprint_no}</h4>
				</div>
				<c:if test="${sprint.active}">
							<p><span class="glyphicon glyphicon-repeat"></span> <s:message code="agile.sprint.activeEnding"/> ${sprint.end_date} </p>
				</c:if>
				<div id="sprint_${sprint.sprint_no}">
					<%--Sprint task --%>
					<c:forEach items="${tasks}" var="task">
						<c:if test="${task.sprint eq sprint }">
							<c:set var="count" value="${count + task.story_points}" />
							<div class="agile-list" data-id="${task.id}" id="${task.id}"
								<c:if test="${task.state eq 'CLOSED' }">
							style="text-decoration: line-through;"
							</c:if>>
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
					<s:message code="agile.storypoints.total" />
					<span class="badge theme" style="margin: 0px 5px;">${count}</span>
				</div>
			</div>
			<hr>
		</c:forEach>
	</div>
	<div style="display: table-cell; padding-left: 20px; width: 45%">
		<h4>
			<s:message code="task.tasks" />
		</h4>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${empty task.sprint && task.state ne 'CLOSED'}">
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
<div class="modal fade" id="startSprint" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">
					<s:message code="agile.sprint.start.title" />
				</h4>
			</div>
			<form id="startSprintForm" name="startSprintForm" method="post"
				action="<c:url value="/scrum/start"/>">
				<div class="modal-body">
					<input id="project_id" type="hidden" name="project_id" value="${project.id}">
					<input id="sprintID" type="hidden" name="sprintID">
					<div>
						<div style="margin-right: 50px; display: table-cell">
							<label><s:message code="agile.sprint.from" /></label> <input
								id="sprint_start" name="sprint_start"
								style="width: 150px; height: 25px"
								class="form-control datepicker" type="text" value="">
						</div>
						<div style="padding-left: 50px; display: table-cell">
							<label><s:message code="agile.sprint.to" /></label> <input
								id="sprint_end" name="sprint_end"
								style="width: 150px; height: 25px"
								class="form-control datepicker" type="text" value="">
						</div>
						<p id="errors" class="text-danger"></p>
						<span class="help-block"><s:message
								code="agile.sprint.startstop"></s:message></span>
					
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn btn-default" type="submit">
						<s:message code="agile.sprint.start" />
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
	var assign_txt = "<s:message code="agile.assing"/>";
	var assing_too_txt = "<s:message code="agile.assing2"/>";
	var remove_txt = "<s:message code="agile.sprint.remove"/>";
	<c:if test="${can_edit}">
	$("#create_sprint").click(function() {
		$("#create_form").submit();
	});
	$(document).on("click", "#start-sprint", function() {
		var id = $(this).data('sprint');
		$("#sprintID").val(id);
		$("#errors").html("");
		$("#sprint_start").val("");
		$("#sprint_end").val("");
	});
	
	$(".datepicker").datepicker({
		minDate : '0',
		dateFormat : "dd-mm-yy",
		firstDay: 1
	});
	$( "#startSprintForm" ).submit(function( event ) {
		var start = $("#sprint_start").val();
		var end = $("#sprint_end").val();
		var start_date = $.datepicker.parseDate("dd-mm-yy",start);
		var end_date = $.datepicker.parseDate("dd-mm-yy",end);
		if(start_date==null || end_date==null|| start_date > end_date){
			var error_msg = "<s:message code="agile.sprint.startstop.error"/>";
			$("#errors").html(error_msg)
			event.preventDefault();
		}
		else{
			$("#startSprintForm").submit();
			}
		});
	
// 	$("#startSprintForm").submit(function( event ) {
// 		var end_date = $.datepicker.parseDate("dd-mm-yy",$("#sprint_end").val() );
// 		var start_date = $.datepicker.parseDate("dd-mm-yy",$("#sprint_start").val() );
// 		aler("start: " + start_date + " end: " + end_date);
// 		event.preventDefault();
// 	});
// 	$("#sprint_end").datepicker({
// 		maxDate : '+1m',
// 		minDate : '0'
// 	});
// 	$(".datepicker").datepicker("option", "dateFormat", "dd-mm-yy");
		
	$(".delete_sprint").click(function(e) {
		var msg = '<p style="text-align:center">'
			+ $(this).data('msg') + '</p>';
		var lang = $(this).data('lang');
		bootbox.setDefaults({
				locale : lang
		});
		e.preventDefault();
		var $link = $(this);
		bootbox.confirm(msg, function(result) {
			if (result == true) {
				document.location.assign($link.attr('href'));
			}
		});
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
	</c:if>
});	
</script>