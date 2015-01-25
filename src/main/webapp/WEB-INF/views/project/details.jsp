<%@page import="com.qprogramming.tasq.task.worklog.LogType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jquery.jqplot.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.highlighter.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.dateAxisRenderer.js"/>"></script>
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<security:authentication property="principal" var="user" />
<c:if test="${not empty param.show}">
	<c:set var="show_q">show=${param.show}&</c:set>
</c:if>
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
				href='<s:url value="/project/manage?id=${project.id}"></s:url>'
				title="<s:message code="project.manage" text="Set as avtive" />"
				data-placement="bottom"><i class="fa fa-wrench"></i></a>
		</div>
	</c:if>
	<div class="pull-right">
		<c:if test="${project.id eq user.active_project}">
			<a class="btn btn-default a-tooltip pull-right"
				style="padding: 6px 11px;" href='#'
				title="<s:message
									code="project.active" text="Set as avtive" />"
				data-placement="bottom"> <i class="fa fa-refresh fa-spin"></i></a>
		</c:if>
		<c:if test="${project.id ne user.active_project}">
			<a class="btn btn-default a-tooltip pull-right"
				href='<s:url value="/project/activate?id=${project.id}"></s:url>'
				title="<s:message
									code="project.activate" text="Set as avtive" />"
				data-placement="bottom"> <i class="fa fa-refresh"></i>
			</a>
		</c:if>
	</div>
	<h3>[${project.projectId}] ${project.name}</h3>
	${project.description}
	<hr>
	<c:set var="tasks_total">${TO_DO + ONGOING+ CLOSED+ BLOCKED}</c:set>
	<c:set var="tasks_todo">${TO_DO * 100 / tasks_total }</c:set>
	<c:set var="tasks_ongoing">${ONGOING * 100 / tasks_total}</c:set>
	<c:set var="tasks_closed">${CLOSED *100 / tasks_total}</c:set>
	<c:set var="tasks_blocked">${BLOCKED*100 / tasks_total}</c:set>
	<div class="progress">
		<div class="progress-bar progress-bar-warning a-tooltip"
			style="width: ${tasks_todo}%"
			title="${TO_DO} <s:message code="task.state.todo"/>">
			<c:if test="${tasks_todo gt 10.0}">
				<span>${TO_DO} <s:message code="task.state.todo" /></span>
			</c:if>
		</div>
		<div class="progress-bar a-tooltip" style="width: ${tasks_ongoing}%"
			title="${ONGOING} <s:message code="task.state.ongoing"/>">
			<c:if test="${tasks_ongoing gt 10.0}">
				<span>${ONGOING} <s:message code="task.state.ongoing" /></span>
			</c:if>
		</div>
		<div class="progress-bar progress-bar-success a-tooltip"
			style="width: ${tasks_closed}%"
			title="${CLOSED} <s:message code="task.state.closed"/>">
			<c:if test="${tasks_closed gt 10.0}">
				<span>${CLOSED} <s:message code="task.state.closed" /></span>
			</c:if>
		</div>
		<div class="progress-bar progress-bar-danger a-tooltip"
			style="width: ${tasks_blocked}%"
			title="${BLOCKED} <s:message code="task.state.blocked"/>">
			<c:if test="${tasks_blocked gt 10.0}">
				<span>${BLOCKED} <s:message code="task.state.blocked" /></span>
			</c:if>
		</div>
	</div>
	<%----------CHART -----------%>
	<div class="row" style="height: 300px;width:90%;margin:0 auto">
		<div id="chartdiv"></div>
	</div>
	<div style="display: table; width: 100%">
		<div style="display: table-cell; width: 600px">
			<%------------------------------ EVENTS ------------------------%>
			<h3>
				<s:message code="project.latestEvents" />
			</h3>
			<div>
				<table id="eventsTable" class="table table-condensed">
				</table>
			</div>
		</div>
		<%------------------------TASKS -------------------------------%>
		<div style="display: table-cell; padding-left: 30px">
			<h3>
				<a href="<c:url value="/tasks"/>" style="color: black"><s:message
						code="task.tasks" /></a>
				<c:if test="${empty param.closed}">
					<a
						href="<s:url value="/project?${show_q}id=${project.id}&closed=yes"></s:url>"><span
						style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
							<i class="fa fa-check-square-o"></i> <s:message
								code="project.hideClosed"></s:message>
					</span></a>
				</c:if>
				<c:if test="${not empty param.closed}">
					<a
						href="<s:url value="/project?${show_q}id=${project.id}"></s:url>"><span
						style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
							<i class="fa fa-square-o"></i> <s:message
								code="project.hideClosed"></s:message>
					</span></a>
				</c:if>

			</h3>
			<table class="table table-hover">
				<c:forEach items="${tasks}" var="task">
					<tr>
						<td><t:type type="${task.type}" list="true" /></td>
						<td><t:priority priority="${task.priority}" list="true" /></td>
						<td><a href="<c:url value="task?id=${task.id}"/>"
							style="<c:if test="${task.state eq 'CLOSED' }">
							text-decoration: line-through;
							</c:if>">[${task.id}]
								${task.name}</a>
						<td>
						<td><c:set var="logged_class"></c:set> <c:if
								test="${task.percentage_logged gt 100 or task.state eq 'BLOCKED'}">
								<c:set var="logged_class">progress-bar-danger</c:set>
							</c:if> <c:if test="${task.state eq 'CLOSED'}">
								<c:set var="logged_class">progress-bar-success</c:set>
							</c:if> <c:if test="${not task.estimated}">
								<div>${task.logged_work}</div>
							</c:if> <c:if test="${task.estimated}">
								<div class="progress" style="width: 50px">
									<div class="progress-bar ${logged_class}" role="progressbar"
										aria-valuenow="${task.percentage_logged}" aria-valuemin="0"
										aria-valuemax="100" style="width:${task.percentage_logged}%"></div>
								</div>
							</c:if></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>
	<%
	pageContext.setAttribute("types",
					LogType.values());
	%>
<script>
$(document).ready(function($) {
				var currentPage = 0
				fetchWorkLogData(currentPage);
				printChart();
});

$(document).on("click",".navBtn",function(e) {
	var page =  $(this).data('page');
	//clear everything
	$("#navigation").html('');
	$("#topNavigation").html('');
	$("#eventsTable .projEvent").remove();
	fetchWorkLogData(page); 
});

function printChart(){
	var loading_indicator = '<div id="loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td>';
	$("#chartdiv").append(loading_indicator);
	projectId = '${project.id}';
	$.get('<c:url value="/project/getChart"/>',{id:projectId},function(result){
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
	    	plot = $.jqplot('chartdiv', [ createdData , closedData ], {
	    		title : '<s:message code="task.state.todo"/>/<s:message code="task.state.closed"/>',
	            seriesDefaults: {
	                rendererOptions: {
	                    smooth: true
	                }
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
	    			    highlighter: { formatString: '[%s] %s <s:message code="task.state.todo"/>'}
	    		    },
	    		    {
	    			    color:'#5cb85c',
	    			    highlighter: { formatString: '[%s] %s <s:message code="task.state.closed"/>'}
	    		    }],
	    		legend: {
			        show: false,
			    }
	    	});
	});
}

function fetchWorkLogData(page) {
	var projectID = '${project.id}';
	var url = '<c:url value="/projectEvents"/>';
	var avatarURL = '<c:url value="/userAvatar/"/>';
	var taskURL = '<c:url value="/task?id="/>';
	var loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="3"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';
	$("#eventsTable").append(loading_indicator);
	$.get(url, {id : projectID,	page: page}, function(data) {
		console.log(data)
		$("#eventsTable tr").remove();
		printWorkLogNavigation(page, data);
		var rows = "";
		for ( var j = 0; j < data.content.length; j++) {
			var row = '<tr class="projEvent"><td colspan="3">';
			var content = data.content[j];
			var timeLogged = '<div class="time-div">'+ content.time +'</div>';
			var avatar = '<img data-src="holder.js/30x30" style="height: 30px; float: left; padding-right: 10px;" src="' + avatarURL + content.account.id +'"/>';
			var account = content.account.name + " " + content.account.surname + " ";
			var event = getEventTypeMsg(content.type);
			var task = '';
			if(content.task!=null){
				task = '<a href="'+taskURL+content.task.id + '">[' + content.task.id +'] '+ content.task.name + '</a>';
			}
			var message = '';
			if(content.message!=null && content.message!=''){
				message ='<blockquote class="quote">' + content.message + '</blockquote>';
			}
			row+=timeLogged + avatar + account + event + task + message;
			row+='</td></tr>';
			rows+=row;
		}
		$(rows).insertAfter("#topNavigation");
	});

}
function printWorkLogNavigation(page,data){
	var topRow='<tr id="topNavigation">';
	var bottomRow='<tr>';
	var prev = '<td style="width:30px"></td>';
	if(!data.firstPage){
		prev = '<td style="width:30px"><a class="navBtn btn" data-page="'+ (page -1)+'"><i class="fa fa-arrow-left"></i></a></td>';
	}
	topRow+=prev;
	bottomRow+=prev;
	var numbers = '<td style="text-align:center">';
	//print numbers
	for (var i = 0; i < data.totalPages; i++) {
		var btnClass = "navBtn btn";
		//active btn
		if (i == data.number) {
			btnClass += " btn-default";
		}
		var button = '<a class="'+btnClass+'" data-page="'+ i +'">'
				+ (i + 1) + '</a>';
				numbers+=button;
	}
	topRow+=numbers;
	bottomRow+=numbers;
	
	var next = '<td style="width:30px"></td>';
	if(!data.lastPage){
		next = '<td style="width:30px"><a class="navBtn btn" data-page="'+ (page +1) +'"><i class="fa fa-arrow-right"></i></a></td>';
	}
	topRow+=next+'</tr>';
	bottomRow+=next+'</tr>';
	$("#eventsTable").append(topRow);
	$("#eventsTable").append(bottomRow);
}

function getEventTypeMsg(type){
	switch(type){
		<c:forEach items="${types}" var="enum_type">
		case "${enum_type}":
			return '<s:message code="${enum_type.code}"/> ';
		</c:forEach>
		default:
			return 'not yet added ';
	};
};
</script>