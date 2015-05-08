<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="myfn" uri="/WEB-INF/tags/custom.tld"%>
<link href="<c:url value="/resources/css/jquery.contextmenu.css" />"
	rel="stylesheet" media="screen" />
<script src="<c:url value="/resources/js/jquery.contextmenu.js" />"></script>
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
<h3>[${project.projectId}] ${project}</h3>
<div class="white-frame" style="display: table; width: 100%">
	<%--MENU --%>
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li class="active"><a style="color: black" href="#"><i class="fa fa-book"></i> Backlog</a></li>
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/board"/>"><i class="fa fa-list-alt"></i> <s:message
						code="agile.board" /></a></li>
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/reports"/>"><i class="fa fa-line-chart"></i> <s:message
						code="agile.reports" /></a></li>
		</ul>
	</div>
	<div style="display: table-cell; width: 55%">
		<h4>
			<s:message code="agile.sprints" />
		</h4>
		<c:if test="${can_edit}">
			<div style="margin: 5px 0px;">
				<s:message code="agile.create.sprint" />&nbsp;
				<button id="create_sprint" class="btn btn-default btn-sm">
					<i class="fa fa-lg fa-plus"></i>
				</button>
			</div>
		</c:if>
		<form id="create_form"
			action="<c:url value="/${project.projectId}/scrum/create"/>"
			method="post"></form>
		<c:forEach var="entry" items="${sprint_result}">
			<c:set var="sprint" value="${entry.key}" />
			<c:set var="count" value="0" />
			<div class="sprint_div">
				<%---Buttons --%>
				<%-- Only print button for sprint at top (active or not_active) --%>
				<c:if test="${can_edit}">
					<div class="buttons_panel" style="float: right">
						<c:if test="${not sprint.active}">
							<c:if test="${not b_rendered}">
								<c:set var="b_rendered" value="true" />
								<button class="btn btn-default btn-sm" id="start-sprint"
									data-sprint="${sprint.id}"  data-sprintNo="${sprint.sprintNo}" data-toggle="modal"
									data-target="#startSprint">
									<i class="fa fa-lg fa-play"></i>&nbsp;
									<s:message code="agile.sprint.start" />
								</button>
							</c:if>
							<a class="btn btn-default btn-sm a-tooltip confirm_action"
								href="<c:url value="/scrum/delete?id=${sprint.id}"/>"
								title="<s:message code="agile.sprint.delete" />"
								data-lang="${pageContext.response.locale}"
								data-msg='<s:message code="agile.sprint.delete.confirm"></s:message>'>
								<i class="fa fa-lg fa-trash-o"></i>
							</a>
						</c:if>
						<c:if test="${sprint.active}">
							<c:set var="b_rendered" value="true" />
							<a class="btn btn-default btn-sm confirm_action"
								href="<c:url value="/scrum/stop?id=${sprint.id}"/>"
								data-lang="${pageContext.response.locale}"
								data-msg='<s:message code="agile.sprint.finish.confirm"></s:message>'>
								<i class="fa fa-lg fa-check"></i>&nbsp;<s:message
									code="agile.sprint.finish" />
							</a>
						</c:if>

					</div>
				</c:if>
				<%--Sprint content --%>
				<div>
					<h4>Sprint ${sprint.sprintNo}</h4>
				</div>
				<c:if test="${sprint.active}">
					<p>
						<i class="fa fa-lg fa-flip-horizontal fa-history"></i>
						${sprint.start_date} - ${sprint.end_date}
					</p>
				</c:if>
				<div id="sprint_${sprint.id}" class="table_sprint" data-id="${sprint.id}">
					<%--Sprint task --%>
					<c:forEach items="${entry.value}" var="task">
						<c:set var="count" value="${count + task.story_points}" />
						<div class="agile-list" data-id="${task.id}" id="${task.id}"
							sprint-id="${sprint.id}"
							<c:if test="${task.state eq 'CLOSED' }">
							style="text-decoration: line-through;"
							</c:if>>
							<div style="display: table-cell; width: 100%;">
								<t:type type="${task.type}" list="true" />
								<a href="<c:url value="/task?id=${task.id}"/>"
									style="color: inherit;">[${task.id}] ${task.name}</a>
								<form id="sprint_remove_${task.id}"
									action="<c:url value="/${project.projectId}/scrum/sprintRemove"/>"
									method="post">
									<input type="hidden" name="taskID" value="${task.id}">
								</form>
							</div>
							<div style="display: table-cell">
								<c:if test="${task.story_points ne 0 && task.estimated}">
								<span
									class="badge theme points-value" data-points="${task.story_points}">
									${task.story_points} </span>
								</c:if>
								<c:if test="${task.story_points eq 0 && task.estimated}">
								<span
									class="badge theme points-value" data-points=0>
									? </span>
								</c:if>
							</div>
						</div>
					</c:forEach>
				</div>
				<div style="text-align: right;">
					<s:message code="agile.storypoints.total" />
					<span id="sprint_points_${sprint.id}" class="badge theme" style="margin: 0px 5px;">${count}</span>
				</div>
			</div>
			<hr>
		</c:forEach>
	</div>
	<!-- 	FREE TASK LIST -->
	<div style="display: table-cell; padding-left: 20px; width: 45%">
		<h4>
			<s:message code="task.tasks" />
			<span class="btn btn-default pull-right" id="save_order" style="display:none"><i class="fa fa-floppy-o"></i>&nbsp;Save order</span>
		</h4>
		<ul id="sortable" style="margin-top: 20px;">
		<c:forEach items="${tasks}" var="task">
			<c:if test="${not task.inSprint && task.state ne 'CLOSED'}">
				<div class="agile-card" data-id="${task.id}" id="${task.id}">
					<div class="side-bar theme"></div>
					<div style="display: table-cell; width: 100%;">
						<t:type type="${task.type}" list="true" />
						<t:priority priority="${task.priority}" list="true" />
						<a href="<c:url value="/task?id=${task.id}"/>"
							style="color: inherit;">[${task.id}] ${task.name}</a>
						<form id="sprint_assign_${task.id}"
 							action=""
 							method="post">
 							<input type="hidden" name="taskID" value="${task.id}"> <input
 								type="hidden" id="sprintID_${task.id}" name="sprintID">
 						</form>
					</div>
					<c:if test="${task.estimated}">
					<div class="pointsdiv" style="display: table-cell">
						<c:if test="${task.story_points == 0 && task.estimated}">
							<c:set var="points">?</c:set>
						</c:if>
						<c:if test="${task.story_points ne 0 && task.estimated}">
							<c:set var="points">${task.story_points}</c:set>
						</c:if>
						<span class="points badge theme">
							<span class="point-value" data-points="${points}">${points}</span>
							<input class="point-input" data-id="${task.id}">
							<span class="point-approve" style="display:none;cursor: pointer;"><i class="fa fa-check" style="vertical-align:text-top"></i></span>
							<span class="point-cancel" style="display:none;cursor: pointer;"><i class="fa fa-times" style="vertical-align:text-top"></i></span>
							<span class="point-edit"><i class="fa fa-pencil points" style="vertical-align:text-top"></i></span>
						</span>
					</div>
					</c:if>
				</div>
			</c:if>
		</c:forEach>
		</ul>
	</div>
</div>
<jsp:include page="../modals/sprint.jsp" />
<script>
$(document).ready(function($) {
	var assign_txt = '<s:message code="agile.assing"/>';
	var assing_too_txt = '<s:message code="agile.assing2"/>';
	var remove_txt = '<s:message code="agile.sprint.remove"/>';
	var lang = "${pageContext.response.locale}";
	bootbox.setDefaults({
		locale : lang
	});
	<c:if test="${can_edit}">
	reloadEvents();
	$("#create_sprint").click(function() {
		$("#create_form").submit();
	});
	$("#start-sprint").click(function(e) {
		var title = '<i class="fa fa-play"></i>&nbsp;<s:message code="agile.sprint.start.title" />';
		var id = $(this).data('sprint');
		var no = $(this).data('sprintno');
		var title = '<i class="fa fa-play"></i>&nbsp;<s:message code="agile.sprint.start.title" /> ' + no ;
		$("#sprintID").val(id);
		$("#sprintStartModal").html(title);
		$("#errors").html("");
		$("#sprintStart").val("");
		$("#sprintStartTime").val("");
		$("#sprintEnd").val("");
		$("#sprintEndTime").val("");
	});
	
	$(".datepicker").datepicker({
// 		minDate : '0',
		dateFormat : "dd-mm-yy",
		firstDay: 1
	});
	$("#sortable").sortable({
		cursor : 'move',
		update: function(event,ui){
			$("#save_order").show("highlight",{color: '#5cb85c'}, 1000);
		}
	});
	$("#save_order").click(function(){
		var order = $("ul#sortable").sortable("toArray");
		var url = '<c:url value="/agile/order"/>';
		var project = '${project.id}';
		showWait(true);
		$.post(url ,{ids:order,project:project},function(result){
			showWait(false);
			$("#save_order").hide("highlight",{color: '#5cb85c'}, 1000);
		});
	});

	$(".table_sprint").droppable({
		activeClass : "state_default",
		hoverClass : "state_hover",
		drop : function(event, ui) {
			var target = $(this);
			var dragged = ui.draggable;
			var taskID = ui.draggable.attr("id");
			var sprintID = $(this).data('id');
			checkIfActiveAndSend(taskID,sprintID,dragged);
		}
	});

	function reloadEvents(){
	$('.agile-card').unbind('contextmenu');	
	$('.agile-list').unbind('contextmenu');
	$('.agile-card').contextPopup({
		title: assign_txt,
		items : 
		[
		 <c:forEach items="${sprints}" var="sprint">
		 {label : assing_too_txt + ' '+ "${sprint.sprintNo}",
				icon : '',
				action : function(event) {
					var taskID = event.currentTarget.id;
					var sprintID = "${sprint.id}";
					var task = event.currentTarget
					checkIfActiveAndSend(taskID,sprintID,task);
					}
		},
		 </c:forEach>
		,
		null,
		{
			label: 'Move to top of backlog',
			icon: '',
			action : function(event) {
				console.log("Top");
				var task = event.currentTarget;
				$("#sortable").prepend(task);
				$("#save_order").show("highlight",{color: '#5cb85c'}, 1000);
			}
		}

		,
		{
			label: 'Move to bottom of backlog',
			icon: '',
			action : function(event) {
				console.log("Bottom");
				var task = event.currentTarget;
				$("#sortable").append(task);
				$("#save_order").show("highlight",{color: '#5cb85c'}, 1000);
			}
		}
		]});
	
	$('.agile-list').contextPopup({
		items : 
		[{label : remove_txt,
				icon : '',
				action : function(event) {
					var taskID = event.currentTarget.id;
					var sprintID = event.currentTarget.getAttribute('sprint-id');
					var url = '<c:url value="/scrum/isActive"/>';
					var message = '<s:message code="agile.sprint.remove.confirm"/>';
					var task = event.currentTarget
					console.log(task)
					$.get(url ,{id:sprintID},function(active){
						if(active){
							bootbox.confirm(message, function(result) {
								if(result){
									removeFromSprint(taskID,sprintID,task)
								}
							});	
						}else{
							removeFromSprint(taskID,sprintID,task)
						}
					});
				}
		}]});
	}
	
	function checkIfActiveAndSend(taskID,sprintID,task){
		var url = '<c:url value="/scrum/isActive"/>';
		$.get(url ,{id:sprintID},function(active){
			if(active){
				var message = '<s:message code="agile.sprint.add.confirm"/>';
				bootbox.confirm(message, function(result) {
					if(result){
						addToSprint(taskID,sprintID,task)
						console.log("Added to sprint" + sprintID);
					}
					else{
						location.reload();
					}
	 			}); 					
			}else{
				addToSprint(taskID,sprintID,task)
			}
		});
	}
	
	//EVENTS
	function addToSprint(taskID,sprintID,task){
		var url = '<c:url value="/task/sprintAssign"/>';
		$("#save_order").hide();
		$.post(url,{taskID:taskID,sprintID:sprintID},function(result){
			if(result.code == 'OK'){
				$(task).removeClass("agile-card");
				$(task).addClass("agile-list");
				$(task).attr("sprint-id",sprintID);
				$(task).detach().prependTo("#sprint_" + sprintID);
				reloadEvents();
				showSuccess(result.message);
			}else{
				showWarning(result.message);
			}
		});
	}
	function removeFromSprint(taskID,sprintID,task){
		var url = '<c:url value="/task/sprintRemove"/>';
		$("#save_order").hide();
		$.post(url,{taskID:taskID,sprintID:sprintID},function(result){
			if(result.code == 'OK'){
				$(task).addClass("agile-card");
				$(task).removeClass("agile-list");
				$(task).attr("sprint-id","");
				$(task).detach().prependTo("#sortable");
				reloadEvents();
				showSuccess(result.message);
			}else{
				showWarning(result.message);
			}
		});
	}
	//points
	$('.point-edit').click(function(){
		togglePoints($(this));
		$('#point_value').focus();
		
	});
	$('.point-approve').click(function(){
		changePoints($(this));
	});
	
	$('.point-input').keypress(function (e) {
		 var key = e.which;
		 if(key == 13)  // the enter key code
		  {
			changePoints($(this));
		  }
	});
	
	$('.point-cancel').click(function(){
		togglePoints($(this));
	});

	function togglePoints(clicked){
		var parent = clicked.closest('.points');
		parent.find('.point-input').toggle();
		parent.find('.point-input').val('');
		parent.find('.point-value').toggle();
		parent.find('.point-approve').toggle();
		parent.find('.point-cancel').toggle();
		parent.find('.point-edit').toggleClass('hidden');
	}
	function changePoints(edited){
		var parent = edited.closest('.points');
		var input = parent.find('.point-input');
		var taskID = input.data('id');
		var points = input.val();
		if(isNumber(points) && points < 40){
			showWait(true);
			$.post('<c:url value="/task/changePoints"/>',{id:taskID,points:points},function(result){
				if(result.code == 'Error'){
					showError(result.message);
				}
				else{
					parent.find('.point-value').html(points);
					showSuccess(result.message);
					showWait(false);
				}
			});
		}
		togglePoints(edited);
	}

	
	</c:if>
});	
</script>