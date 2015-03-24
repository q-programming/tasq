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
	<s:message code="task.description" text="Description" arguments=""/>
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
			<li><a style="color: black" href="<c:url value="/${project.projectId}/scrum/backlog"/>"><i class="fa fa-book"></i>
					Backlog</a></li>
			<li class="active"><a style="color: black" href="#"><i class="fa fa-list-alt"></i> <s:message
						code="agile.board" /></a></li>
			<li><a style="color: black" href="<c:url value="/${project.projectId}/scrum/reports"/>"><i class="fa fa-line-chart"></i>
			 <s:message code="agile.reports" /></a></li>
	</ul>
</div>
<div style="display:table-header-group;">
		<div style="display:table-cell">
			<h4 style="padding-bottom:20px">Sprint ${sprint.sprintNo} <span style="font-size: small;margin-left:5px">(${sprint.start_date} - ${sprint.end_date})</span></h4>
		</div>
		<div style="display:table-cell"></div>
		<div style="display:table-cell"></div>
		<div style="display:table-cell"></div>
		<div style="display:table-cell"></div>
		<div style="display:table-cell"></div>
		<div style="display:table-cell"><span class="btn btn-default pull-right" id="save_order" style="display:none"><i class="fa fa-floppy-o"></i>&nbsp;Save order</span></div>
</div >
	<div class="well table_state sortable_tasks" data-state="TO_DO" >
		<div class="table_header"><i class="fa fa-pencil-square-o"></i> <s:message code="task.state.todo"/></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'TO_DO'}">
				<t:card task="${task}" can_edit="${can_edit}"/>
			</c:if>
		</c:forEach>
	</div>
	<div style="display: table-cell;width:1px"></div>
	<div class="well table_state notsortable_tasks" data-state="ONGOING">
		<div class="table_header"><i class="fa fa-spin fa-repeat"></i> <s:message code="task.state.ongoing"/></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'ONGOING'}">
				<t:card task="${task}" can_edit="${can_edit}"/>
			</c:if>
		</c:forEach>
	</div>
	<div style="display: table-cell;width:1px"></div>
	<div class="well table_state notsortable_tasks" data-state="CLOSED">
		<div class="table_header"><i class="fa fa-check"></i> <s:message code="task.state.closed"/></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'CLOSED'}">
				<t:card task="${task}" can_edit="${can_edit}" />
			</c:if>
		</c:forEach>
	</div>
	<div style="display: table-cell;width:1px"></div>
	<div class="well table_state notsortable_tasks" data-state="BLOCKED">
		<div class="table_header"><i class="fa fa-ban"></i> <s:message code="task.state.blocked"/></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'BLOCKED'}">
				<t:card task="${task}" can_edit="${can_edit}"/>
			</c:if>
		</c:forEach>
	</div>
</div>

<jsp:include page="../modals/logWork.jsp" />
<jsp:include page="../modals/close.jsp" />
<jsp:include page="../modals/assign.jsp" />
<%-- <tiles:insertTemplate template="modal/close"/> --%>
<script>
	$(document).ready(function($) {
		<c:if test="${can_edit}">

// 		$(".agile-card").draggable ({
// 	    	revert: 'invalid',
// 	    	cursor: 'move'
// 		});
		
		$(".sortable_tasks").sortable({
			cursor : 'move',
			items: "div.agile-card",
			helper: "clone",
			update: function(event,ui){
				$("#save_order").show("highlight",{color: '#5cb85c'}, 1000);
				console.log("sort me!");
			}
		});
		
		$(".notsortable_tasks").sortable({
			cursor : 'move',
			helper: "clone",
			items: "div.agile-card"
		});
		
		$("#save_order").click(function(){
			var order = $("div.sortable_tasks").sortable("toArray");
			var url = '<c:url value="/agile/order"/>';
			var project = '${project.id}';
			console.log(order);
			showWait(true);
			$.post(url ,{ids:order,project:project},function(result){
				showWait(false);
				$("#save_order").hide("highlight",{color: '#5cb85c'}, 1000);
			});
		});
		
		$( ".table_state" ).droppable({
		      activeClass: "state_default",
		      hoverClass: "state_hover",
		      drop: function( event, ui ) {
		    	  //event on drop
		    	 taskID = ui.draggable.attr("id");
		    	 subTasks = ui.draggable.attr('data-subtasks');
		    	 var text = '<i class="fa fa-check"></i>&nbsp;<s:message code="task.closeTask"/>&nbsp;';
		    	 $('#closeDialogTitle').html(text + taskID);
		    	 $('#modal_subtaskCount').html(subTasks);
		    	 var oldState =  ui.draggable.attr("state");
		    	 var state = $(this).data('state');
		    	 if( oldState != state){
			    	var target = $(this);
			    	var dragged = ui.draggable;
			    	dragged.css("opacity","0.3");
			    	if(state == 'CLOSED'){
			    		 $('#close_task').modal({
			    	            show: true,
			    	            keyboard: false,
			    	            backdrop: 'static'
			    	     });
			    		 $('#'+taskID + ' a[href]').toggleClass('closed');
		    	 	}
			    	else{
			    		showWait(true);
						$.post('<c:url value="/task/changeState"/>',{id:taskID,state:state},function(result){
							if(result.code == 'Error'){
								dragged.css("opacity","1");
								reloadmsg = ' <s:message code="main.pageReload" arguments="5"/>';
								showError(result.message + reloadmsg);
								window.setTimeout('location.reload()', 5000);
							}
							else{
								dragged.css("opacity","1");
					    		target.append(dragged.clone(true).show());
					    		dragged.remove();
								showSuccess(result.message);
								if(oldState== 'CLOSED'){
									$('#'+taskID + ' a[href]').toggleClass('closed');
								}
							}
							showWait(false);
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
		</c:if>
	});
</script>