<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@page import="com.qprogramming.tasq.task.worklog.LogType"%>

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
	
<c:if test="${empty param.sprint}">
	<c:set var="ActiveSprint">${lastSprint.sprintNo }</c:set>
</c:if>
<c:if test="${not empty param.sprint}">
	<c:set var="ActiveSprint">${param.sprint}</c:set>
</c:if>
	
<h3>[${project.projectId}] ${project}</h3>
<div class="white-frame" style="display: table; width: 100%;">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/backlog"/>"><i class="fa fa-book"></i> Backlog</a></li>
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/board"/>"><i class="fa fa-list-alt"></i> <s:message
						code="agile.board" /></a></li>
			<li class="active"><a style="color: black" href="#"><i class="fa fa-line-chart"></i> <s:message
						code="agile.reports" /></a></li>
		</ul>
	</div>
	<div class="row">
		<a class="anchor" id="sprint"></a>
		<div class="col-lg-2 col-md-3 col-sm-4">
			<div id="menu" style="position: fixed;">
				<nav>
					<ul class="nav nav-pills nav-stacked">
						<li>
							<a href="#" id="sprintNoMenu" class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-expanded="false">
								<h4><b>Sprint ${ActiveSprint}</b> <span class="caret"></h4></span>
    						</a>
							<ul class="dropdown-menu" role="menu">
								<c:forEach var="i" begin="1" end="${lastSprint.sprintNo }">
									<li><a href="#" class="sprintMenuNo" data-number="${i}"> Sprint ${i}</a></li>
								</c:forEach>
   							</ul>
						<li><a href="#burndown_chart"><div class="side-bar theme"></div><s:message code="agile.burndown"/></a></li>
						<li><a href="#time_chart"><div class="side-bar theme"></div><s:message code="agile.timelogged"/></a></li>
						<li><a href="#events"><div class="side-bar theme"></div><s:message code="agile.events"/></a></li>
						<li><a href="#sprintTotal"><div class="side-bar theme"></div><s:message code="agile.total"/></a></li>
					</ul>
				</nav>
			</div>
		</div>
	</div>
	<div class="row" style="height: 500px;">
		<a class="anchor" id="burndown_chart"></a>
		<div class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4" id="chartdiv"  style="height: 500px;"></div>
	</div>
	<div class="row" style="height: 300px;">
		<a class="anchor" id="time_chart"></a>
		<div id="chartdiv2"	class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4" style="height: 300px;"></div>
	</div>
	<div class="row">
		<a class="anchor" id="events"></a>
		<div class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4">
			<h4 style="text-align: center;"><i class="fa fa-calendar"></i> <s:message code="agile.events.sprint"/></h4>
			<table id="eventsTable" class="table table-bordered"
				style="width: 100%">
				<thead class="theme">
					<tr>
						<th style="width: 300px"><s:message code="task.task" /></th>
						<th style="width: 100px"><s:message code="main.date" /></th>
						<th style="width: 60px; text-align: center" colspan="2"><s:message code="agile.change"/></th>
						<th id="timeColumn" style="width: 60px; text-align: center"><s:message code="agile.time"/></th>
						<th style="width: 90px"><s:message code="task.activeLog" /></th>
						<th style="width: 100px"><s:message code="agile.user" /></th>
						<!-- 						TODO -->
					</tr>
				</thead>
			</table>
			<a class="anchor" id="sprintTotal"></a>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {
	var plot;
	var plot2;
	var lastSprint = "${ActiveSprint}";
	var avatarURL = '<c:url value="/userAvatar/"/>';
	var taskURL = '<c:url value="/task?id="/>';
	var loading_indicator = '<div id="loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td>';
	var timeTracked = ${project.timeTracked};
	$(".sprintMenuNo").click(function(){
		var number = $(this).data('number');
		renderSprintData(number);
	});
	
	$("#sprintNumber").val(lastSprint);
	renderSprintData(lastSprint);
		
	$("#sprintNumber").change(function(){
		renderSprintData(this.value);
	});
		
		
		var labelFormat = '%s SP';
		if (timeTracked){
			labelFormat = '%#.1f h';
		}
		

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
	    $('#total').remove();
	    if(timeTracked){
	    	$('th:nth-child(4)').hide();
	    }else{
	    	$('th:nth-child(4)').show();
	    }
	    $("#sprintNoMenu").html('<h4><b>Sprint '+ sprintNo + '</b> <span class="caret"></span></h4>')
	    $.get('<c:url value="/${project.projectId}/sprint-data"/>',{sprint:sprintNo},function(result){
 	    	//Fill arrays of data
	    	console.log(result);
	    	$.each(result.timeBurned, function(key,val){
	    		time.push([key, val]);
	    	});
	    	$.each(result.left, function(key,val){
	    		left.push([key, val]);
	    	});
	    	$.each(result.burned, function(key,val){
	    		burned.push([key, val]);
	    	});
	    	var startStop ='';
	    	$.each(result.ideal, function(key,val){
	    		ideal.push([key, val]);
	    		startStop+=key;
	    		startStop+=" - ";
	    	});
	    	startStop = startStop.slice(0,-3);
			  	    	
			//Render worklog events
			$.each(result.worklogs, function(key,val){
	    		var task = '<td><a class="a-tooltip" href="'+ taskURL + val.task.id + '"data-html="true" title=\''+ val.task.description+'\'>[' + val.task.id + '] ' + val.task.name + '</a></td>'; 
	    		var date = "<td>" +val.time + "</td>";
	    		var event = "<td>" +getEventTypeMsg(val.type) + "</td>";
	    		var change;
	    		var timeLogged;
	    		if(timeTracked){
	    			if  (val.type=='ESTIMATE' || val.type=='TASKSPRINTADD'){
		    			change = '<td style="width:30px">' + val.message + '</td><td style="width:30px"></td>';
	    			}else if(val.type=='LOG'){
		    			change = '<td style="width:30px"></td><td style="width:30px">' + val.message + '</td>';
	    			}
	    		}else{
		    		if  (val.type=='REOPEN' || val.type=='TASKSPRINTADD'){
		    			change = '<td style="width:30px">' + val.task.story_points + '</td><td style="width:30px"></td>';
	    			}else if (val.type=='ESTIMATE'){
		    			change = '<td style="width:30px">' + val.message + '</td><td style="width:30px"></td>';
		    		}
		    		else if(val.type=='LOG' && timeTracked == false ){
		    			change = '<td style="width:30px"></td><td style="width:30px"></td>';
	    			}
	    			else{
		    			change = '<td style="width:30px"></td><td style="width:30px">' + val.task.story_points + '</td>';
	    			}
		    		var timeLogged = "<td>";
		    		if(val.activity){
	    				timeLogged = "<td>";
	    				timeLogged+=val.message;
		    		}
	    			timeLogged+="</td>";
	    		}
	    		var account = val.account.name +" " + val.account.surname; 
				var accountTd = '<td rel="popover" data-container="body" data-placement="top" data-account="'
									+ account + '" data-account_email="' + val.account.email + '" data-account_img="' + avatarURL + val.account.id + '">'
									+account
								+'</td>';
				var row = task + date + change + timeLogged + event + accountTd;
				$("#eventsTable").append("<tr>"+row+"</tr>");
	    	});
			//Total
			var totalTxt = '<s:message code="agile.sprint.total"/>';
			if(result.totalTime == '0m'){
				result.totalTime = '0h';
			}
			if(timeTracked){
				var totalRow = '<thead id="total" class="theme"><tr><th colspan="2">' + totalTxt + '</th><th colspan="2" style="text-align:center">~ ' + result.totalTime + '</th><th colspan="2"></th><thead>';
				$("#eventsTable").append(totalRow);
			}else{
				var totalRow = '<thead id="total" class="theme"><tr><th colspan="2">' + totalTxt + '</th><th colspan="2" style="text-align:center">'+ result.totalPoints + ' SP</th><th colspan="2">~ ' + result.totalTime + '</th><th></th><thead>';
				$("#eventsTable").append(totalRow);
			}
			//remove loading
			$("#loading").remove();
	    	//render chart
			plot = $.jqplot('chartdiv', [ left,burned,ideal ], {
				title : '<i class="fa fa-line-chart"></i> <s:message code="agile.burndown.chart"/><p style="font-size: x-small;">'+startStop+'</p>',
				animate: true,
				grid: {
	                background: '#ffffff',
	            },
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
			case "ESTIMATE":
				return '<s:message code="task.state.estimatechange"/>';
			default:
				return 'not yet added ';
		};
	}
});
</script>
