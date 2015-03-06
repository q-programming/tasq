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
	<h4 style="padding-bottom:20px">Sprint ${sprint.sprintNo} <span style="font-size: small;margin-left:5px">(${sprint.start_date} - ${sprint.end_date})</span></h4>
</div >
	<div class="well table_state" data-state="TO_DO" >
		<div class="table_header"><i class="fa fa-pencil-square-o"></i> <s:message code="task.state.todo"/></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'TO_DO'}">
				<t:card task="${task}" can_edit="${can_edit}"/>
			</c:if>
		</c:forEach>
	</div>
	<div style="display: table-cell;width:1px"></div>
	<div class="well table_state" data-state="ONGOING">
		<div class="table_header"><i class="fa fa-spin fa-repeat"></i> <s:message code="task.state.ongoing"/></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'ONGOING'}">
				<t:card task="${task}" can_edit="${can_edit}"/>
			</c:if>
		</c:forEach>
	</div>
	<div style="display: table-cell;width:1px"></div>
	<div class="well table_state" data-state="CLOSED">
		<div class="table_header"><i class="fa fa-check"></i> <s:message code="task.state.closed"/></div>
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.state eq 'CLOSED'}">
				<t:card task="${task}" can_edit="${can_edit}" />
			</c:if>
		</c:forEach>
	</div>
	<div style="display: table-cell;width:1px"></div>
	<div class="well table_state" data-state="BLOCKED">
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
			    		showWait(true);
						$.post('<c:url value="/task/changeState"/>',{id:taskID,state:state},function(result){
							if(result.code == 'Error'){
								reloadmsg = ' <s:message code="main.pageReload" arguments="5"/>';
								showError(result.message + reloadmsg);
								window.setTimeout('location.reload()', 5000);
							}
							else{
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