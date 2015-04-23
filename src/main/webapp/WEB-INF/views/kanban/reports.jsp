<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jquery.jqplot.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.highlighter.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.enhancedLegendRenderer.min.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.dateAxisRenderer.js"/>"></script>
<%-- <link href="<c:url value="/resources/css/docs.min.css" />" --%>
<!-- 	rel="stylesheet" media="screen" /> -->
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/jquery.jqplot.css"/>" />

<c:set var="tasks_text">
	<s:message code="task.tasks" text="Tasks" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" arguments="" />
</c:set>
<c:if test="${empty param.release}">
	<c:set var="ActiveRelease"><s:message code="agile.release.current"/></c:set>
</c:if>
<c:if test="${not empty param.release}">
	<c:set var="ActiveRelease"><s:message code="agile.release"/> ${param.release}</c:set>
</c:if>
<h3>[${project.projectId}] ${project}</h3>
<div class="white-frame"
	style="display: table; width: 100%; height: 85vh">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/kanban/board"/>"><i
					class="fa fa-list-alt"></i> <s:message code="agile.board" /></a></li>
			<li class="active"><a style="color: black"
				href="<c:url value="/${project.projectId}/kanban/reports"/>"><i
					class="fa fa-line-chart"></i> <s:message code="agile.reports" /></a></li>
		</ul>
	</div>
		<div class="row">
		<a class="anchor" id="sprint"></a>
		<div class="col-lg-2 col-md-3 col-sm-4">
			<div id="menu" style="position: fixed;">
				<nav>
					<ul class="nav nav-pills nav-stacked">
						<li>
							<a href="#" id="releaseNoMenu" class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
								<h4><b>${ActiveRelease}</b> <span class="caret"></h4></span>
    						</a>
							<ul id="releases" class="dropdown-menu" role="menu">
								<li><a href="#" class="releaseMenuNo" data-number=""> <s:message code="agile.release.current"/></a></li>
								<c:forEach var="release" items="${releases}">
									<li><a href="#" class="releaseMenuNo" data-number="${release.release}"> Release ${release.release}</a></li>
								</c:forEach>
   							</ul>
						<li><a href="#chart"><div class="side-bar theme"></div><s:message code="agile.burndown"/></a></li>
						<li><a href="#time_chart"><div class="side-bar theme"></div><s:message code="agile.timelogged"/></a></li>
						<li><a href="#events"><div class="side-bar theme"></div><s:message code="agile.events"/></a></li>
						<li><a href="#releaseTotal"><div class="side-bar theme"></div><s:message code="agile.total"/></a></li>
					</ul>
				</nav>
			</div>
		</div>
	</div>
	<div class="row" style="height: 500px;">
		<a class="anchor" id="chart"></a>
		<div class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4" id="chartdiv"  style="height: 500px;"></div>
	</div>
	<div class="row" style="height: 300px;">
		<a class="anchor" id="time_chart"></a>
		<div id="chartdiv2"	class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4" style="height: 300px;"></div>
	</div>
	<div class="row">
		<a class="anchor" id="events"></a>
		<div class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4">
			<h4 style="text-align: center;"><i class="fa fa-calendar"></i> <s:message code="agile.events.release"/></h4>
			<table id="eventsTable" class="table table-bordered"
				style="width: 100%">
				<thead class="theme">
					<tr>
						<th style="width: 300px"><s:message code="task.task" /></th>
						<th style="width: 100px"><s:message code="main.date" /></th>
						<th id="timeColumn" style="width: 60px; text-align: center"><s:message code="agile.time"/></th>
						<th style="width: 90px"><s:message code="task.activeLog" /></th>
						<th style="width: 100px"><s:message code="agile.user" /></th>
						<!-- 						TODO -->
					</tr>
				</thead>
			</table>
			<a class="anchor" id="sprintTotal"></a>
			<h4 style="text-align: center;"><i class="fa fa-list-ul"></i> <s:message code="agile.release.total"/></h4>
			<table id="summaryTable" class="table table-bordered"
				style="width: 100%">
				<thead id="releaseTotal" class="theme">
				</thead>
			</table>
		</div>
	</div>
</div>


<script>
$(document).ready(function() {
	var plot;
	var plot2;
	var lastSprint = "${ActiveRelease}";
	var avatarURL = '<c:url value="/../avatar/"/>';
	var taskURL = '<c:url value="/task?id="/>';
	var loading_indicator = '<div id="loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td>';
	
	$(".releaseMenuNo").click(function(){
		var number = $(this).data('number');
		renderSprintData(number);
	});

	

function renderSprintData(releaseNo){
	//init arrays and remove first element via pop()
    time = new Array([]);
    openData = new Array([]);
    closedData = new Array([]);
    progressData = new Array([]);
    time.pop();
    openData.pop();
    closedData.pop();
    progressData.pop();
    if(plot){
    	plot.destroy();
    }
    if(plot2){
    	plot2.destroy();
    }
    $("#chartdiv").append(loading_indicator);
    $('#eventsTable tbody').html('');
    $('#summaryTable tbody').html('');
    $('#totalSummary tr').remove()
    $("#releaseNoMenu").html('<h4><b>Release '+ releaseNo + '</b> <span class="caret"></span></h4>')
    $.get('<c:url value="/${project.projectId}/release-data"/>',{release:releaseNo},function(result){
	    	//Fill arrays of data
    	//console.log(result);
    	$.each(result.timeBurned, function(key,val){
    		time.push([key, val]);
    	});
    	$.each(result.open, function(key,val){
    		openData.push([key, val]);
    	});
    	$.each(result.closed, function(key,val){
    		closedData.push([key, val]);
    	});
    	$.each(result.inProgress, function(key,val){
    		progressData.push([key, val]);
    	});
    	var startStop ='';
		//Render worklog events
		$.each(result.worklogs, function(key,val){
    		var task = '<td><a class="a-tooltip" href="'+ taskURL + val.task.id + '" \'>[' + val.task.id + '] ' + val.task.name + '</a></td>'; 
    		var date = "<td>" +val.time + "</td>";
    		var event = "<td>" +getEventTypeMsg(val.type) + "</td>";
    		var change;
    		var timeLogged = "<td>";
			timeLogged+=val.message;
   			timeLogged+="</td>";
    		var account = val.account.name +" " + val.account.surname; 
			var accountTd = '<td rel="popover" data-container="body" data-placement="top" data-account="'
								+ account + '" data-account_email="' + val.account.email + '" data-account_img="' + avatarURL + val.account.id + '.png">'
								+account
							+'</td>';
			var row = task + date + timeLogged + event + accountTd;
			$("#eventsTable").append("<tr>"+row+"</tr>");
    	});
		//Summary
		//All tasks within sprint
		var thead;
		thead = '<tr><th colspan=2><s:message code="task.task" /></th><th><s:message code="agile.time"/></th></tr>';
		$("#totalSummary").append(thead);
		//Total
		if(result.totalTime == '0m'){
			result.totalTime = '0h';
		}
		var totalRow = '<tr class="theme"><th>Completed</th><th style="text-align:center">~ ' + result.totalTime + ' h</th>';
		$("#summaryTable").append(totalRow);
		$.each(result.tasks.CLOSED, function(key,task){
			if(!task.subtask){
				var type = getTaskType(task.type);
				var taskCell = '<td>'+type+'<a href="'+taskURL + task.id + '">['+task.id+'] ' + task.name + '</a></td>';
				var metter = '<td class="centerd">' + task.loggedWork + '</td>';
				row = '<tr>' + taskCell + metter + '</tr>';
				$("#summaryTable").append(row);
			}
		})
		//remove loading
		$("#loading").remove();
    	//render chart
    	var startStop = result.startStop;
    	var labelFormat = '%d';
		plot = $.jqplot('chartdiv', [ openData,progressData,closedData], {
			title : '<i class="fa fa-line-chart"></i> <s:message code="agile.release.chart"/><p style="font-size: x-small;">'+startStop+'</p>',
			animate: true,
			grid: {
                background: '#ffffff',
            },
			highlighter : {
				show : true,
				sizeAdjust : 7.5
			},
            seriesDefaults: {
                rendererOptions: {
                    smooth: true
                }
            },
    		fillBetween: {
                series1: 1,
                series2: 2,
                color: "rgba(66, 139, 202, 0.18)",
                baseSeries: 2,
                fill: true
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
			    	color: '#f0ad4e',
				    label: '<s:message code="task.created"/>',
				    highlighter: { formatString: '[%s] %s <s:message code="task.created"/>'}
				    
			    },
			    {
			    	color:'#428bca',
				    label: '<s:message code="task.state.ongoing"/>',
				    highlighter: { formatString: '[%s] %s <s:message code="task.state.ongoing"/>'}
			    },
			    {
			    	color:'#5cb85c',
				    label: '<s:message code="task.state.closed"/>',
				    highlighter: { formatString: '[%s] %s <s:message code="task.state.closed"/>'}
			    }],
			legend: {
					renderer: jQuery.jqplot.EnhancedLegendRenderer,
					rendererOptions: {
				          numberRows: '1',
				    },
			        show: true,
		        	location: 's',
		        	placement: 'outsideGrid'
			    }
		});
		//	render time chart
		labelFormat = '%#.1f h';
		plot2 = $.jqplot('chartdiv2', [ time ], {
			title : '<i class="fa fa-bar-chart"></i> <s:message code="agile.timelogged.day"/><p style="font-size: x-small;">'+startStop+'</p>',
			animate: true,
			grid: {
                background: '#ffffff',
            },
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
						formatString : '%#.1f h',
					}
				}
			},
			highlighter: {
			      show: true,
			      sizeAdjust: 10
			},
			series:[
			    {
				    label: '<s:message code="agile.timelogged"/>',
				    rendererOptions: {
		                smooth: true
		            },
					fill:true,
				    color:'#5FAB78'
			    }],
			legend: {
			        show: true,
			        location: 's',
			        placement: 'outsideGrid'
			    }
		});
		
		//-----------------------------Add effects etc-------------------------------
		$('.a-tooltip').tooltip();
		$('body').scrollspy({
			target : '#menu'
		});
		$('td[rel=popover]').popover({
			html : true,
			trigger : 'hover',
			content : function() {
				var account_name = $(this).data('account');
				var account_email = $(this).data('account_email');
				var account_img = $(this).data('account_img');
				var img = '<img data-src="holder.js/30x30"	style="height: 30px; float: left; padding-right: 10px;"	src="'+ account_img +'" />';
				var html = '<div>'+img + account_name+'</div><div><a style="font-size: xx-small;float:right"href="mailto:"'+ account_email +'">'+account_email+'</a>';
				return html;
			}});
    });
}


	function getEventTypeMsg(type){
		switch(type){
			case "CREATE":
				return '<s:message code="task.created"/>';
			case "CLOSED":
				return '<s:message code="task.state.closed"/>';
			case "REOPEN":
				return '<s:message code="task.state.reopen"/>';
			case "LOG":
				return '<s:message code="task.state.logged"/>';
			case "STATUS":
				return 'changed status';
			case "TASKSPRINTREMOVE":
				return '<s:message code="task.state.tasksprintremove"/>';
			case "ESTIMATE":
				return '<s:message code="task.state.estimatechange"/>';
			default:
				return 'not yet added ';
		};
	}
	function getTaskType(type){
		switch(type){
			case "TASK":
				var type='<i class="fa fa-lg fa-fw fa-check-square"></i> ';
				return type;
			case "USER_STORY":
				var type='<i class="fa fa-lg fa-fw fa-lightbulb-o"></i> ';
				return type;
			case "ISSUE":
				var type='<i class="fa fa-lg fa-fw fa-exclamation-triangle"></i> ';
				return type;
			case "BUG":
				var type='<i class="fa fa-lg fa-fw fa-bug"></i> ';
				return type;
			case "IDLE":
				var type='<i class="fa fa-lg fa-fw fa-coffee"></i> ';
				return type;
			case "SUBTASK":
				var type='<i class="fa fa-lg fa-fw fa-check-circle-o"></i> ';
				return type;
			case "SUBBUG":
				var type='<i class="fa fa-lg fa-fw fa-bug"></i> ';
				return type;
			default:
				return 'not yet added ';
		};
	}
});
</script>
