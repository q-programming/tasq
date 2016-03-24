<%@page import="com.qprogramming.tasq.task.TaskType"%>
<%@page import="com.qprogramming.tasq.task.worklog.LogType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jquery.jqplot.min.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.highlighter.min.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.dateAxisRenderer.min.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.cursor.min.js"/>"></script>
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<security:authentication property="principal" var="user" />
<c:if test="${not empty param.closed}">
	<c:set var="closed_q">closed=${param.closed}&</c:set>
</c:if>
<c:forEach items="${project.administrators}" var="admin">
	<c:if test="${admin.id == user.id || is_admin}">
		<c:set var="can_edit" value="true" />
	</c:if>
</c:forEach>
<div class="white-frame" style="overflow: auto;">
	<c:set var="projectName_text">
		<s:message code="project.name" />
	</c:set>
	<c:set var="projectDesc_text">
		<s:message code="project.description" />
	</c:set>
	<c:if test="${can_edit}">
		<div class="pull-right">
			<a class="btn btn-default a-tooltip pull-right"
				style="padding: 6px 11px;"
				href='<s:url value="/project/${project.projectId}/manage"></s:url>'
				title="<s:message code="project.manage" text="Set as avtive" />"
				data-placement="bottom"><i class="fa fa-wrench"></i></a>
		</div>
	</c:if>
	<div class="pull-right">
		<c:if test="${project.id eq user.active_project}">
			<a class="btn btn-default a-tooltip pull-right"
				style="padding: 6px 11px;" href='#'
				title="<s:message
									code="project.active" text="Active project" />"
				data-placement="bottom"> <i class="fa fa-refresh fa-spin"></i></a>
		</c:if>
		<c:if test="${project.id ne user.active_project}">
			<a class="btn btn-default a-tooltip pull-right"
				href='<s:url value="/project/activate/${project.projectId}"></s:url>'
				title="<s:message
									code="project.activate" text="Set as avtive" />"
				data-placement="bottom"> <i class="fa fa-refresh"></i>
			</a>
		</c:if>
			<a class="show_participants_btn btn btn-default a-tooltip pull-right" 
				title="" data-placement="bottom" data-original-title="<s:message code="project.members"/>">
					<i class="fa fa-users"></i>
			</a>
	</div>
	<h3>[${project.projectId}] ${project.name}</h3>
	${project.description}
	<hr>
	<c:set var="tasks_total">${TO_DO + ONGOING + COMPLETE + CLOSED + BLOCKED}</c:set>
	<c:set var="tasks_todo">${TO_DO * 100 / tasks_total }</c:set>
	<c:set var="tasks_ongoing">${ONGOING * 100 / tasks_total}</c:set>
	<c:set var="tasks_complete">${COMPLETE *100 / tasks_total}</c:set>
	<c:set var="tasks_closed">${CLOSED *100 / tasks_total}</c:set>
	<c:set var="tasks_blocked">${BLOCKED*100 / tasks_total}</c:set>
	<div class="progress">
		<div class="progress-bar progress-bar-warning a-tooltip"
			style="width: ${tasks_todo}%"
			title="${TO_DO}&nbsp;<s:message code="task.state.todo"/>">
			<c:if test="${tasks_todo gt 10.0}">
				<span>${TO_DO}&nbsp;<s:message code="task.state.todo" /></span>
			</c:if>
		</div>
		<div class="progress-bar a-tooltip" style="width: ${tasks_ongoing}%"
			title="${ONGOING}&nbsp;<s:message code="task.state.ongoing"/>">
			<c:if test="${tasks_ongoing gt 10.0}">
				<span>${ONGOING}&nbsp;<s:message code="task.state.ongoing" /></span>
			</c:if>
		</div>
		<div class="progress-bar progress-bar-success a-tooltip" style="width: ${tasks_complete}%"
			title="${COMPLETE}&nbsp;<s:message code="task.state.complete"/>">
			<c:if test="${tasks_complete gt 10.0}">
				<span>${COMPLETE}&nbsp;<s:message code="task.state.complete" /></span>
			</c:if>
		</div>
		
		<div class="progress-bar progress-bar-closed  a-tooltip"
			style="width: ${tasks_closed}%"
			title="${CLOSED}&nbsp;<s:message code="task.state.closed"/>">
			<c:if test="${tasks_closed gt 10.0}">
				<span>${CLOSED}&nbsp;<s:message code="task.state.closed" /></span>
			</c:if>
		</div>
		<div class="progress-bar progress-bar-danger a-tooltip"
			style="width: ${tasks_blocked}%"
			title="${BLOCKED}&nbsp;<s:message code="task.state.blocked"/>">
			<c:if test="${tasks_blocked gt 10.0}">
				<span>${BLOCKED}&nbsp;<s:message code="task.state.blocked" /></span>
			</c:if>
		</div>
	</div>
	<%----------CHART -----------%>
	<div id="chart_divarea" class="row" style="height: 300px; width: 90%; margin: 20px auto">
		<div id="chartdiv"></div>
	</div>
	<div id="no_events" style="text-align: center;padding: 20px;display:none">
		No events
	</div>
		<div style="display: inherit; font-size: small; float: right">
		<span id="moreEvents" class="clickable" data-all="false"><span id="moreEventsCheck"><i 
			class="fa fa-square-o"></i></span> <s:message
				code="project.moreevents.chart" /></span>
	</div>
	
	<div style="display: table; width: 100%">
		<div style="display: table-cell; width: 600px">
			<%------------------------------ EVENTS ------------------------%>
			<h3>
				<s:message code="project.latestEvents" />
			</h3>
			<div class="text-center">
				<ul id="eventsTable_pagination_top"></ul>
			</div>
			<div>
				<table id="eventsTable" class="table table-condensed">
				</table>
			</div>
			<div class="text-center">
				<ul id="eventsTable_pagination_bot"></ul>
			</div>
			
		</div>
		<%------------------------TASKS -------------------------------%>
		<div style="display: table-cell; padding-left: 30px">
			<h3>
				<a href="<c:url value="/tasks"/>" style="color: black"><s:message
						code="task.tasks" /></a>
				<div class="pull-right">
					<c:if test="${empty param.closed}">
						<div>
							<a
								href="<s:url value="/project/${project.projectId}?closed=yes"></s:url>"><span
								style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
									<i class="fa fa-check-square-o"></i> <s:message
										code="project.hideClosed" />
							</span></a>
						</div>
					</c:if>
					<c:if test="${not empty param.closed}">
						<div>
							<a
								href="<s:url value="/project/${project.projectId}"></s:url>"><span
								style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
									<i class="fa fa-square-o"></i> <s:message
										code="project.hideClosed"></s:message>
							</span></a>
						</div>
					</c:if>
					<div
						style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
						<s:message code="tasks.subtasks" />
						&nbsp; <i id="opensubtask"
							class="fa fa-plus-square clickable a-tooltip"
							title="<s:message code="task.subtask.showall"/>"></i> <i
							id="hidesubtask" class="fa fa-minus-square clickable a-tooltip"
							title="<s:message code="task.subtask.hideall"/>"></i>
					</div>
				</div>
			</h3>
			<table class="table table-hover">
				<c:forEach items="${tasks}" var="task">
					<tr>
						<td><t:type type="${task.type}" list="true" /></td>
						<td><t:priority priority="${task.priority}" list="true" /></td>
						<td><c:if test="${task.subtasks gt 0}">
								<i class="subtasks fa fa-plus-square" data-task="${task.id}"
									id="subtasks${task.id}"></i>
							</c:if> <a href="<c:url value="/task/${task.id}"/>"
							style="<c:if test="${task.state eq 'CLOSED' }">
							text-decoration: line-through;
							</c:if>">
								[${task.id}] ${task.name}</a>
						<td>
						<td><c:set var="logged_class"></c:set> <c:set
								var="percentage">${task.percentage_logged}</c:set> <c:if
								test="${task.percentage_logged gt 100 or task.state eq 'BLOCKED'}">
								<c:set var="logged_class">progress-bar-danger</c:set>
							</c:if> <c:if test="${task.state eq 'CLOSED'}">
								<c:set var="logged_class">progress-bar-success</c:set>
								<c:set var="percentage">100</c:set>
							</c:if> <c:if test="${task.state eq 'TO_DO'}">
								<c:set var="logged_class">progress-bar-success</c:set>
								<c:set var="percentage">0</c:set>
							</c:if>
							<div class="progress" style="width: 50px">
								<div class="progress-bar ${logged_class} a-tooltip"
									role="progressbar" aria-valuenow="${percentage}"
									aria-valuemin="0" aria-valuemax="100"
									style="width:${percentage}%" title="${percentage}%"></div>
							</div></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>
<jsp:include page="../task/subtasks.jsp" />
<jsp:include page="../other/events.jsp" />
<script>
var	plot;
$(document).ready(function($) {
	var currentPage = 0;
	var projectID = '${project.projectId}';
	fetchWorkLogData(currentPage, projectID);
	printChart(false);
	
	$("#moreEvents").click(function() {
		var all = $(this).data('all');
		if(all){
			$("#moreEventsCheck").html('<i class="fa fa-square-o"></i>');
		}else{
			$("#moreEventsCheck").html('<i class="fa fa-check-square-o"></i>');
		}
		printChart(!all);
		$(this).data('all',!all);
	});
});


function printChart(all){
	if(plot){
		plot.destroy();
	}
	$("#chart_divarea").show('slow');
	$("#no_events").hide('slow');
	$("#chartdiv").append(loading_indicator);
	projectId = '${project.projectId}';
	$.get('<c:url value="/project/getChart"/>',{id:projectId,all:all},function(result){
	    	//Fill arrays of data
	    	$("#loading").remove();
	    	//console.log(result);
   		    createdData = new Array([]);
   		 	closedData = new Array([]);
		    createdData.pop();
		    closedData.pop();
	    	$.each(result.created, function(key,val){
	    		createdData.push([key, val]);
	    	});
	    	$.each(result.closed, function(key,val){
	    		closedData.push([key, val]);
	    	});
	    	if(createdData.length > 0 && closedData.length > 0)
	    	{
	    	plot = $.jqplot('chartdiv', [ createdData , closedData ], {
	    		title : '<s:message code="task.created"/>/<s:message code="task.state.closed"/>',
	            seriesDefaults: {
	                rendererOptions: {
	                    smooth: true
	                }
	            },
	            cursor:{ 
	                show: true,
	                zoom:true, 
	                showTooltip:false
	            }, 
	    		fillBetween: {
	                series1: 0,
	                series2: 1,
	                color: "rgba(66, 139, 202, 0.18)",
	                baseSeries: 0,
	                fill: true
	            },
	            grid: {
	                background: '#ffffff',
	            },
	    		animate: true,
	    		axesDefaults : {
	    			labelRenderer : $.jqplot.CanvasAxisLabelRenderer
	    		},
	    		axes : {
	    			xaxis : {
	    				renderer:$.jqplot.DateAxisRenderer, 
	    		        pad : 0,
	    		        tickOptions:{formatString:'%#d-%m'}
	    			},
	    			yaxis : {
	    				pad : -2.05,
	    				tickOptions : {
	    					formatString : '%#d',
	    				}
	    			}
	    		},
	    		highlighter: {
	    		      show: true,
	    		      sizeAdjust: 10
	    		},
	    		series:[
	    		    {
	    			    color: '#f0ad4e',
	    			    highlighter: { formatString: '[%s] %s <s:message code="task.created"/>'}
	    		    },
	    		    {
	    			    color:'#5cb85c',
	    			    highlighter: { formatString: '[%s] %s <s:message code="task.state.closed"/>'}
	    		    }],
	    		legend: {
			        show: false,
			    }
	    	});
		}else{
			$("#chart_divarea").hide('slow');
			$("#no_events").show('slow');
		}
	});
}
</script>