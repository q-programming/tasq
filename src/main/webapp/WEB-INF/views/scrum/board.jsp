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
<c:if test="${myfn:contains(project.participants,user) || is_admin}">
	<c:set var="can_edit" value="true" />
</c:if>
<%-- <h4>SCRUM Board</h4><h4><a href="<c:url value="/${projectID}/scrum/backlog"/>">SCRUM Backlog</a></h4> --%>
<div class="white-frame" style="display: table; width: 100%;height:85vh">
<div style="display:table-caption;margin-left: 10px;">
	<ul class="nav nav-tabs" style="border-bottom:0">
			<li class="active"><a style="color: black" href="#"><span class="glyphicon glyphicon-list-alt"></span> Board</a></li>
			<li><a style="color: black" href="<c:url value="/${projectID}/scrum/backlog"/>"><span class="glyphicon glyphicon-book"></span>
					Backlog</a></li>
			<li><a style="color: black" href="#"><span class="glyphicon glyphicon-bullhorn"></span> Reports</a></li>
	</ul>
</div>
<div style="display:table-header-group;">
	<h4>Sprint ${sprint.sprint_no} <span style="font-size: small;">(<s:message code="agile.sprint.activeEnding"/> ${sprint.end_date})</span></h4>
</div>
	<div class="table_state" data-state="TO_DO" >
		<div><h4><s:message code="task.state.todo"/></h4></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'TO_DO'}">
				<t:card task="${task}" />
			</c:if>
		</c:forEach>
	</div>
	<div class="table_state" data-state="ONGOING">
		<div><h4><s:message code="task.state.ongoing"/></h4></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'ONGOING'}">
				<t:card task="${task}" />
			</c:if>
		</c:forEach>
	</div>
	<div class="table_state" data-state="CLOSED">
		<div><h4><s:message code="task.state.closed"/></h4></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'CLOSED'}">
				<t:card task="${task}" />
			</c:if>
		</c:forEach>
	</div>
	<div class="table_state" data-state="BLOCKED">
		<div><h4><s:message code="task.state.blocked"/></h4></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'BLOCKED'}">
				<t:card task="${task}" />
			</c:if>
		</c:forEach>
	</div>
</div>
<script>
	$(document).ready(function($) {
		<c:if test="${can_edit}">
		$("#assign_me").click(function(){
			var current_email = "${user.email}";
			$("#assign").append('<input type="hidden" name="email" value=' + current_email + '>');
        	$("#assign").submit();
		});

		$(".agile-card").draggable({
		    	revert: 'invalid',
		    	cursor: 'move'
		});
		$( ".table_state" ).droppable({
		      activeClass: "state_default",
		      hoverClass: "state_hover",
		      drop: function( event, ui ) {
		    	  //event on drop
		    	 var taskID = ui.draggable.attr("id");
		    	 var state = $(this).data('state');
		    	 if($("#state_" + taskID).val() != state){
			    	 if(state == 'CLOSED'){
		    		 	alert("closing");
		    	 	}
		    	 	$("#state_" + taskID).val(state);
		    	 	$("#state_form_"+taskID).submit();
		    	 }
		    	 
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