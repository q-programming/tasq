<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jquery.jqplot.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.highlighter.js"/>"></script>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jqplot.dateAxisRenderer.js"/>"></script>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/jquery.jqplot.css"/>" />
<h3>[${project.projectId}] ${project}</h3>
<div class="white-frame"
	style="display: table; width: 100%;">
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
	<div style="display:table-cell;width:100px;padding: 10px;padding-left: 0px;">
	<ul>
		<c:if test="${empty param.sprint}">
			<c:set var="ActiveSprint">${lastSprint.sprintNo }</c:set>
		</c:if>
		<c:if test="${not empty param.sprint}">
			<c:set var="ActiveSprint">${param.sprint}</c:set>
		</c:if>
		<c:forEach var="i" begin="1" end="${lastSprint.sprintNo }">
   			<a href="?sprint=${i}"><li class="btn btn-default 
   				<c:if test="${i eq ActiveSprint}">active</c:if>">
   				Sprint ${i}
   			</li>
   			</a>
		</c:forEach>
	</ul>
	</div>
	<div id="chartdiv" style="display:table-cell;height:500px;width:90%"></div>
	<div style="display:table-row;">
		<div style="display:table-cell;"></div>
		<div style="display:table-cell;">
			<c:if test="${not empty workLogList}">
				<table class="table table-bordered" style="width:100%">
					<thead class="theme">
						<tr>
							<th style="width: 300px"><s:message code="task.name" /></th>
							<th style="width: 100px"><s:message code="main.date" /></th>
							<th style="width: 60px;text-align:center" colspan="2">Change</th>
							<th style="width: 90px"><s:message code="task.activeLog" /></th>
							<th style="width: 100px"><s:message code="agile.user"/></th>
	<!-- 						TODO -->
						</tr>
					</thead>
					<c:forEach items="${workLogList}" var="worklog">
						<tr>
							<td><a class="a-tooltip" href="<c:url value="/task?id=${worklog.task.id}"/>" title="
								[${worklog.task.id}] ${worklog.task.name}">
									[${worklog.task.id}] ${worklog.task.name}
								</a></td>
							<td>${worklog.time}</td>
							<c:if test="${not empty worklog.activity}">
								<td style="text-align:center">${worklog.formatedActivity}</td>
							</c:if>
							<c:if test="${empty worklog.activity}">
								<c:choose>
									<c:when test="${worklog.type.string eq 'tasksprintadd' || worklog.type.string eq 'reopen' || worklog.type.string eq 'reopen'}">
										<td style="width: 30px"></td><td style="width: 30px;text-align:center">${worklog.task.story_points}</td>
									</c:when>
									<c:otherwise>
										<td style="width: 30px;text-align:center">${worklog.task.story_points}</td><td style="width: 30px"></td>
									</c:otherwise>
								</c:choose>														
							</c:if>
							<td style="text-align:center"><s:message code="task.state.${worklog.type.string}"/></td>
							<td rel="popover" data-container="body" data-placement="top" data-account="${worklog.account}" data-account_email="${worklog.account.email}" data-account_img="<c:url value="/userAvatar/${worklog.account.id}"/>">${worklog.account}</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
		</div>
	</div>
</div>
<div id="chartdiv2" style="height:500px;width:90%"></div>
<div id="chartdiv3" style="height:500px;width:90%"></div>
<script>
$(document).ready(function() {
		var timeTracked = ${project.timeTracked};
		var labelFormat = '%s SP';
		if (timeTracked){
			labelFormat = '%s h';
		}
		//init arrays and remove first element via pop()
	    time = new Array([]);
	    left = new Array([]);
	    burned = new Array([]);
	    ideal = new Array([]);
	    time.pop();
	    left.pop();
	    burned.pop();
	    ideal.pop();
	    $.get('<c:url value="/${project.projectId}/sprint/burndown"/>',{sprint:4},function(result){
// 	    	console.log(result);
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
	    	console.log(time);
	    	console.log("Left:");
	    	console.log(left);
	    	console.log("Burned:");
	    	console.log(burned);
	    	console.log("Ideal:");
	    	console.log(ideal);
			var plot = $.jqplot('chartdiv', [ left,burned,ideal ], {
				title : 'Sprint ${sprint.sprintNo}<p style="font-size: xx-small;">${sprint.start_date} - ${sprint.end_date}</p>',
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
			var plot2 = $.jqplot('chartdiv2', [ time ], {
				title : 'Time',
				highlighter : {
					show : true,
					sizeAdjust : 7.5
				},
				animate: true,
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
							formatString : '%s h',
							show : true
						}
					}
				},
				seriesDefaults: {
			           fill: true,
			    },
				series:[
				    {
					    label: 'Time',
					    rendererOptions: {
			                smooth: true
			            },
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
// 		jqPlot
		
// 		var burned = [${burned}];
// 		var left = [${left}];
// 		var ideal = [${ideal}];
// 		var plot = $.jqplot('chartdiv', [ left,burned,ideal ], {
// 			title : 'Sprint ${sprint.sprintNo}<p style="font-size: xx-small;">${sprint.start_date} - ${sprint.end_date}</p>',
// 			highlighter : {
// 				show : true,
// 				sizeAdjust : 7.5
// 			},
// 			axesDefaults : {
// 				labelRenderer : $.jqplot.CanvasAxisLabelRenderer
// 			},
// 			axes : {
// 				xaxis : {
// 					renderer:$.jqplot.DateAxisRenderer, 
// 			        tickOptions:{formatString:'%#d-%m'},
// 					pad : 0
// 				},
// 				yaxis : {
// 					pad : 0,
// 					tickOptions : {
// 						formatString : labelFormat,
// 						show : true
// 					}
// 				}
// 			},
// 			series:[
// 			    {
// 				    label: '<s:message code="agile.remaining"/>',
// 			    },
// 			    {
// 				    label: '<s:message code="agile.burned"/>',
// 			    },
// 			    {
// 				    color: '#B34202',
// 				    label: '<s:message code="agile.ideal"/>',
// 				    lineWidth:1, 
// 				    markerOptions: {
// 			            show: false,  
// 			        }
// 			    }],
// 			legend: {
// 			        show: true,
// 			        location: 'ne',     
// 			        xoffset: 12,        
// 			        yoffset: 12,        
// 			    }
// 		});
	});
</script>
