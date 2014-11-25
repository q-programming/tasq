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
<jsp:include page="../modals/logWork.jsp" />
<jsp:include page="../modals/close.jsp" />
<%-- <tiles:insertTemplate template="modal/close"/> --%>
<script>
	$(document).ready(function($) {
		<c:if test="${can_edit}">
		$(".assign_me").click(function(){
			var taskID = $(this).data('taskid');
			//assignMe
			$.post('<c:url value="/task/assignMe"/>',{id:taskID},function(result){
				console.log(result);
				if(result.code!='OK'){
					showError(result.message);
				}
				else{
					var assignee = '<img data-src="holder.js/20x20"	style="height: 20px; padding-right: 5px;" src="<c:url value="/userAvatar/${user.id}"/>" />'
									+'<a href="<c:url value="/user?id=${user.id}"/>">${user}</a>';
					$("#assignee_"+taskID).html(assignee);
					showSuccess(result.message);
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
			    		 $('#'+taskID + ' a[href]').toggleClass('closed');
		    	 	}
			    	else{
						$.post('<c:url value="/task/changeState"/>',{id:taskID,state:state},function(result){
							if(result.code == 'Error'){
								showError(result.message);
							}
							else{
								showSuccess(result.message);
								if(oldState== 'CLOSED'){
									$('#'+taskID + ' a[href]').toggleClass('closed');
								}
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
		</c:if>
	});
</script>