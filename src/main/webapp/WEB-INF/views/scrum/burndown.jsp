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
			<table class="table" style="width:100%">
				<thead class="theme">
					<tr>
						<th style="width: 30px"><s:message code="task.type" /></th>
						<th style="width: 30px"><s:message code="task.priority" /></th>
						<th style="width: 500px"><s:message code="task.name" /></th>
						<th style="width: 30px"><s:message code="task.logged" /></th>
						<th style="width: 30px"><s:message code="task.remaining" /></th>
						<th style="text-align:center"><s:message code="task.progress" /></th>
					</tr>
				</thead>
				<c:forEach items="${tasksList}" var="task">
					<tr>
						<td><t:type type="${task.type}" list="true" /></td>
						<td style="text-align:center"><t:priority priority="${task.priority}" list="true" /></td>
						<td><a href="<c:url value="/task?id=${task.id}"/>"
							style="color: inherit;<c:if test="${task.state eq 'CLOSED' }">
										text-decoration: line-through;
										</c:if>">[${task.id}] ${task.name}</a></td>
						<td style="text-align: right;">${task.logged_work}</td>
						<td style="text-align: right;">${task.remaining}</td>
						<td style="text-align:center"><t:state state="${task.state}"></t:state></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		var burned = [${burned}];
		var left = [${left}];
		var ideal = [${ideal}];
		var plot = $.jqplot('chartdiv', [ left,burned,ideal ], {
			title : 'Sprint ${sprint.sprintNo}<p style="font-size: xx-small;">${sprint.start_date} - ${sprint.end_date}</p>',
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
						formatString : '%s h',
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
	});
</script>
