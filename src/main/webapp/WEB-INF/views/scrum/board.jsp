<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="myfn" uri="/WEB-INF/tags/custom.tld"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
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
<c:if test="${myfn:contains(project.participants,user) && user.isUser || is_admin}">
	<c:set var="can_edit" value="true" />
</c:if>
<h3>[${project.projectId}] ${project}</h3>
<div class="white-frame" style="display: table; width: 100%;height:85vh">
<div style="display:table-caption;margin-left: 10px;">
	<ul class="nav nav-tabs" style="border-bottom:0">
			<li><a style="color: black" href="<c:url value="/${project.projectId}/scrum/backlog"/>"><span class="glyphicon glyphicon-book"></span>
					Backlog</a></li>
			<li class="active"><a style="color: black" href="#"><span class="glyphicon glyphicon-list-alt"></span> <s:message
						code="agile.board" /></a></li>
			<li><a style="color: black" href="<c:url value="/${project.projectId}/scrum/burndown"/>"><span class="glyphicon glyphicon-bullhorn"></span>
			 <s:message code="agile.reports" /></a></li>
	</ul>
</div>
<div style="display:table-header-group;">
	<h4>Sprint ${sprint.sprintNo} <span style="font-size: small;">(${sprint.start_date} - ${sprint.end_date})</span></h4>
</div >
	
	<div class="table_state" data-state="TO_DO" >
		<div><h4><s:message code="task.state.todo"/></h4></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'TO_DO'}">
				<t:card task="${task}" can_edit="${can_edit}"/>
			</c:if>
		</c:forEach>
	</div>
	<div class="table_state" data-state="ONGOING">
		<div><h4><s:message code="task.state.ongoing"/></h4></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'ONGOING'}">
				<t:card task="${task}" can_edit="${can_edit}"/>
			</c:if>
		</c:forEach>
	</div>
	<div class="table_state" data-state="CLOSED">
		<div><h4><s:message code="task.state.closed"/></h4></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'CLOSED'}">
				<t:card task="${task}" can_edit="${can_edit}" />
			</c:if>
		</c:forEach>
	</div>
	<div class="table_state" data-state="BLOCKED">
		<div><h4><s:message code="task.state.blocked"/></h4></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'BLOCKED'}">
				<t:card task="${task}" can_edit="${can_edit}"/>
			</c:if>
		</c:forEach>
	</div>
	
</div>
<%------------------LOG WORK MODAL ---------------------%>
<div class="modal fade" id="logWorkform" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">
					<s:message code="task.logWork" />
				</h4>
			</div>
			<form id="mainForm" name="mainForm" method="post"
				action="<c:url value="/logwork"/>">
				<div class="modal-body">
					<input id="taskID" type="hidden" name="taskID">
					<div class="form-group">
						<label><s:message code="task.logWork.spent" /></label> <input
							id="logged_work" name="logged_work"
							style="width: 150px; height: 25px" class="form-control"
							type="text" value=""> <span class="help-block"><s:message
								code="task.logWork.help"></s:message> </span>
					</div>
					<div>
						<div style="float: left; margin-right: 50px;">
							<label><s:message code="main.date" /></label> <input
								id="datepicker" name="date_logged"
								style="width: 150px; height: 25px"
								class="form-control datepicker" type="text" value="">
						</div>
						<div>
							<label><s:message code="main.time" /></label> <input
								id="time_logged" name="time_logged"
								style="width: 70px; height: 25px" class="form-control"
								type="text" value="">
						</div>
					</div>
					<span class="help-block"><s:message
							code="task.logWork.when.help"></s:message> </span>
					<div>
						<label><s:message code="task.remaining" /></label>
						<div class="radio">
							<label> <input type="radio" name="estimate_reduce"
								id="estimate_auto" value="auto" checked> <s:message
									code="task.logWork.reduceAuto" />
							</label>
						</div>
						<div class="radio">
							<label> <input type="radio" name="estimate_reduce"
								id="estimate_manual" value="auto"> <s:message
									code="task.logWork.reduceManual" />
							</label> <input id="remaining" name="remaining" class="form-control"
								style="width: 150px; height: 25px" disabled>
						</div>
					</div>
					<span class="help-block"><s:message
							code="task.logWork.estimate.help"></s:message> </span>
				</div>
				<div class="modal-footer">
					<a class="btn" data-dismiss="modal"><s:message
							code="main.cancel" /></a>
					<button class="btn btn-default" type="submit">
						<s:message code="main.log" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<jsp:include page="../modals/close.jsp" />
<%-- <tiles:insertTemplate template="modal/close"/> --%>
<script>
	$(document).ready(function($) {
		<c:if test="${can_edit}">
		$(".worklog").click(function(){
			var taskID = $(this).data('taskid');
			var title = "<s:message code="task.logWork" /> " + taskID;
			$("#myModalLabel").html(title);
			$("#taskID").val(taskID);
		});
		
		$(".assign_me").click(function(){
			var taskID = $(this).data('taskid');
			//assignMe
			$.post('<c:url value="/task/assignMe"/>',{id:taskID},function(message){
				if(message!='OK'){
					showError(message);
				}
				else{
					var succes = '<s:message code="task.assinged.me"/> ' + taskID;
					var assignee = '<img data-src="holder.js/20x20"	style="height: 20px; padding-right: 5px;" src="<c:url value="/userAvatar/${user.id}"/>" />'
									+'<a href="<c:url value="/user?id=${user.id}"/>">${user}</a>';
					$("#assignee_"+taskID).html(assignee);
					showSuccess(succes);
				}
			});
		});

		$(".agile-card").draggable ({
		    	revert: 'invalid',
		    	cursor: 'move'
		});
		
       
		$( ".table_state" ).droppable({
		      activeClass: "state_default",
		      hoverClass: "state_hover",
		      drop: function( event, ui ) {
		    	  //event on drop
		    	 taskID = ui.draggable.attr("id");
		    	 var oldState =  ui.draggable.attr("state");
		    	 var state = $(this).data('state');
		    	 if( oldState != state){
			    	 if(state == 'CLOSED'){
			    		 $('#close_task').modal({
			    	            show: true,
			    	            keyboard: false,
			    	            backdrop: 'static'
			    	     });
			    		 console.log('halt?');
		    	 	}
			    	else{
						$.post('<c:url value="/task/changeState"/>',{id:taskID,state:state},function(result){
							if(result.code == 'Error'){
								showError(result.message);
							}
							else{
								showSuccess(result.message);
							}
						});
			    	}
		    	 }
		    	 var dropped = ui.draggable;
		         var droppedOn = $(this);
		         $(dropped).detach().css({top: 0,left: 0}).appendTo(droppedOn);
		      },
		      accept: function(dropElem) {
		    	  	var taskID = dropElem.attr("id");
		    	  	var state = $(this).data('state');
		    	  	return $("#state_" + taskID).val() != state;
		    	  	
		    	  }
		    });
		//------------------------------------Datepickers
		$(".datepicker").datepicker({
			maxDate : '0',
			dateFormat : "dd-mm-yy",
			firstDay: 1
		});
		$(".datepicker").change(function() {
			var date = new Date;
			var minutes = date.getMinutes();
			var hour = date.getHours();
			$("#time_logged").val(hour + ":" + minutes);
		});
		$("#time_logged").mask("Z0:A0", {
			translation : {
				'Z' : {
					pattern : /[0-2]/
				},
				'A' : {
					pattern : /[0-5]/
				}
			},
			placeholder : "__:__"
		});
		$("#estimate_manual").change(function() {
			$('#remaining').attr("disabled", !this.checked);
		});
		$("#estimate_auto").change(function() {
			$('#remaining').val("");
			$('#remaining').attr("disabled", this.checked);
		});
		</c:if>
	});
</script>