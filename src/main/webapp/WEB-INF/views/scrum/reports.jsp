<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@page import="com.qprogramming.tasq.task.worklog.LogType"%>

<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jquery.jqplot.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.highlighter.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.dateAxisRenderer.js"/>"></script>
<%-- <link href="<c:url value="/resources/css/docs.min.css" />" --%>
<!-- 	rel="stylesheet" media="screen" /> -->
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/jquery.jqplot.css"/>" />
<h3>[${project.projectId}] ${project}</h3>
<div class="white-frame" style="display: table; width: 100%;">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/backlog"/>"><span
					class="glyphicon glyphicon-book"></span> Backlog</a></li>
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/board"/>"><span
					class="glyphicon glyphicon-list-alt"></span> <s:message
						code="agile.board" /></a></li>
			<li class="active"><a style="color: black" href="#"><span
					class="glyphicon glyphicon-bullhorn"></span> <s:message
						code="agile.reports" /></a></li>
		</ul>
	</div>
	<div style="display: table-row;">
		<div style="display: table-cell;"></div>
		<div style="display: table-cell;">
			<a class="anchor" id="sprint"></a>
			<c:if test="${empty param.sprint}">
				<c:set var="ActiveSprint">${lastSprint.sprintNo }</c:set>
			</c:if>
			<c:if test="${not empty param.sprint}">
				<c:set var="ActiveSprint">${param.sprint}</c:set>
			</c:if>
			<select id="sprintNumber" class="form-control">
				<c:forEach var="i" begin="1" end="${lastSprint.sprintNo }">
					<option value="${i}">Sprint ${i}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div style="display: table-row;">
		<div id="menu" 
			style="display: table-cell; width: 100px; padding: 10px; padding-left: 0px;padding-right: 10px">
			<nav class="bs-docs-sidebar hidden-print hidden-xs hidden-sm affix">
				<ul class="nav bs-docs-sidenav">
					<li><a href="#sprint">Sprint</a>
					<li><a href="#burndown_chart">Burndown</a></li>
					<li><a href="#time_chart">Time Logged</a></li>
					<li><a href="#events">Events</a></li>
				</ul>
			</nav>
			<a class="anchor" id="burndown_chart"></a>
		</div>
		<div id="chartdiv"
			style="display: table-cell; height: 500px; width: 90%">
			</div>
	</div>
	<div style="display: table-row;">
		<div style="display: table-cell;">
			<a class="anchor" id="time_chart"></a>
		</div>
		<div id="chartdiv2"
			style="display: table-cell; height: 500px; width: 90%">
			
		</div>
	</div>
	<a class="anchor" id="events"></a>
	<div style="display: table-row;">
		<div style="display: table-cell;"></div>
		<div style="display: table-cell;">
			<c:if test="${not empty workLogList}">
				<table id="eventsTable" class="table table-bordered"
					style="width: 100%">
					<thead class="theme">
						<tr>
							<th style="width: 300px"><s:message code="task.name" /></th>
							<th style="width: 100px"><s:message code="main.date" /></th>
							<th style="width: 60px; text-align: center" colspan="2">Change</th>
							<th style="width: 60px; text-align: center">Time</th>
							<th style="width: 90px"><s:message code="task.activeLog" /></th>
							<th style="width: 100px"><s:message code="agile.user" /></th>
							<!-- 						TODO -->
						</tr>
					</thead>
				</table>
			</c:if>
		</div>
	</div>
</div>
<%
	pageContext.setAttribute("types", LogType.values());
%>

<script>
$(document).ready(function() {
	var plot;
	var plot2;
	var lastSprint = "${lastSprint.sprintNo}";
	var avatarURL = '<c:url value="/userAvatar/"/>';
	var taskURL = '<c:url value="/task?id="/>';
	var loading_indicator = '<div id="loading" class="centerPadded"><img src="<c:url value="/resources/img/loading.gif"/>"></img></td>';

		$("#sprintNumber").val(lastSprint);
		renderSprintData(lastSprint);
		
		$("#sprintNumber").change(function(){
			renderSprintData(this.value);
		});
		
		var timeTracked = ${project.timeTracked};
		var labelFormat = '%s SP';
		if (timeTracked){
			labelFormat = '%s h';
		}
		
		$('td[rel=popover]').popover({
			html : true,
			trigger : 'hover',
			content : function() {
				var account_name = $(this).data('account');
				var account_email = $(this).data('account_email');
				var account_img = $(this).data('account_img');
				var img = '<img data-src="holder.js/30x30"	style="height: 30px; float: left; padding-right: 10px;"	src="'+ account_img +'" />';
				var html = '<div>'+img + account_name+'</div><div><a style="font-size: xx-small;float:right"href="mailto:"'+account_email+'">'+account_email+'</a>';
				return html;
			}});

	function renderSprintData(sprintNo){
		//init arrays and remove first element via pop()
	    time = new Array([]);
	    left = new Array([]);
	    burned = new Array([]);
	    ideal = new Array([]);
	    time.pop();
	    left.pop();
	    burned.pop();
	    ideal.pop();
	    if(plot){
	    	plot.destroy();
	    }
	    if(plot2){
	    	plot2.destroy();
	    }
	    $("#chartdiv").append(loading_indicator);
	    $('#eventsTable tbody').html('');
	    $.get('<c:url value="/${project.projectId}/sprint-data"/>',{sprint:sprintNo},function(result){
 	    	//Fill arrays of data
	    	console.log(result);
	    	$.each(result.timeBurned, function(key,val){
	    		time.push([key, val]);
	    	});
	    	$.each(result.left, function(key,val){
	    		left.push([key, val]);
	    	});
	    	$.each(result.pointsBurned, function(key,val){
	    		burned.push([key, val]);
	    	});
	    	$.each(result.ideal, function(key,val){
	    		ideal.push([key, val]);
	    	});
			//Render worklog events
			console.log("Worklogs");
			$.each(result.worklogs, function(key,val){
	    		var task = '<td><a class="a-tooltip" href="'+ taskURL + val.task.id + '"data-html="true" title=\''+ val.task.description+'\'>[' + val.task.id + '] ' + val.task.name + '</a></td>'; 
	    		var date = "<td>" +val.time + "</td>";
	    		var event = "<td>" +getEventTypeMsg(val.type) + "</td>";
	    		//Points based project
	    		var change;
	    		if  (val.type=='REOPEN' || val.type=='TASKSPRINTADD'){
	    			change = '<td style="width:30px">' + val.task.story_points + '</td><td style="width:30px"></td>';
	    		}else if(val.type=='LOG'){
	    			change = '<td style="width:30px"></td><td style="width:30px"></td>';
	    		}
	    		else{
	    			change = '<td style="width:30px"></td><td style="width:30px">' + val.task.story_points + '</td>';
	    		}
	    		var timeLogged = "<td>"
	    		if(val.activity){
	    			timeLogged+=val.message;
	    		}
	    		timeLogged+="</td>";
	    		var account = val.account.name +" " + val.account.surname; 
				var accountTd = '<td rel="popover" data-container="body" data-placement="top" data-account="'
									+ account + '" data-account_email="' + val.account.email + '" data-account_img="' + avatarURL + val.account.id + '">'
									+account
								+'</td>';
				var row = task + date + change + timeLogged + event + accountTd;
				$("#eventsTable").append("<tr>"+row+"</tr>");
				$('.a-tooltip').tooltip();
				$('body').scrollspy({
					target : '#menu'
				});
	    	});
			//remove loading
			$("#loading").remove();
	    	//render chart
			plot = $.jqplot('chartdiv', [ left,burned,ideal ], {
				title : '<p style="font-size: x-small;">${sprint.start_date} - ${sprint.end_date}</p>',
				animate: true,
				highlighter : {
					show : true,
					sizeAdjust : 7.5
				},
				axesDefaults : {
					labelRenderer : $.jqplot.CanvasAxisLabelRenderer
				},
				axes : {
					xaxis : {
						renderer:$.jqplot.DateAxisRenderer, 
				        tickOptions:{formatString:'%#d-%m'},
						pad : 0
					},
					yaxis : {
						pad : 0,
						tickOptions : {
							formatString : labelFormat,
							show : true
						}
					}
				},
				series:[
				    {
					    label: '<s:message code="agile.remaining"/>',
				    },
				    {
					    label: '<s:message code="agile.burned"/>',
				    },
				    {
					    color: '#B34202',
					    label: '<s:message code="agile.ideal"/>',
					    lineWidth:1, 
					    markerOptions: {
				            show: false,  
				        }
				    }],
				legend: {
				        show: true,
				        location: 'ne',     
				        xoffset: 12,        
				        yoffset: 12,        
				    }
			});
			//	render time chart
			plot2 = $.jqplot('chartdiv2', [ time ], {
				title : 'Time',
				animate: true,
				autoscale: true,
				axesDefaults : {
					labelRenderer : $.jqplot.CanvasAxisLabelRenderer
				},
				axes : {
					xaxis : {
						renderer:$.jqplot.DateAxisRenderer, 
				        pad : 1.05,
				        tickOptions:{formatString:'%#d-%m'}
					},
					yaxis : {
						pad : -1,
						tickOptions : {
							formatString : '%d h',
						}
					}
				},
				series:[
				    {
					    label: 'Time',
					    rendererOptions: {
			                smooth: true
			            },
						fill:true,
					    color:'#5FAB78'
				    }],
				legend: {
				        show: true,
				        location: 'ne',     
				        xoffset: 12,        
				        yoffset: 12,        
				    }
			});
	    });
	};
	function getEventTypeMsg(type){
		switch(type){
			case "CLOSED":
				return '<s:message code="task.state.closed"/>';
			case "REOPEN":
				return '<s:message code="task.state.reopen"/>';
			case "LOG":
				return '<s:message code="task.state.logged"/>';
			case "TASKSPRINTADD":
				return '<s:message code="task.state.tasksprintadd"/>';
			case "TASKSPRINTREMOVE":
				return '<s:message code="task.state.tasksprintremove"/>';
			default:
				return 'not yet added ';
		};
	}
});
</script>
