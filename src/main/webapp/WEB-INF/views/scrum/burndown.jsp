<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/jquery.jqplot.js"/>"></script>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/jquery.jqplot.css"/>" />

<div class="white-frame"
	style="display: table; width: 100%; height: 85vh">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black" href="<c:url value="/${project.projectId}/scrum/board"/>"><span
					class="glyphicon glyphicon-list-alt"></span> Board</a></li>
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/scrum/backlog"/>"><span
					class="glyphicon glyphicon-book"></span> Backlog</a></li>
			<li class="active"><a style="color: black" href="#"><span
					class="glyphicon glyphicon-bullhorn"></span> Reports</a></li>
		</ul>
	</div>
	<div id="chartdiv" style="height: 400px;"></div>
	${burndown}
</div>
<script>
	$(document).ready(function() {
		var burned = ${burned};
		var left = ${left};
		var plot = $.jqplot('chartdiv', [left,burned], {
			title : "Sprint ${sprint.sprintNo}",
			axesDefaults : {
				labelRenderer : $.jqplot.CanvasAxisLabelRenderer
			},
			axes : {
				xaxis : {
					label : '<s:message code="agile.sprintDays"/>',
					pad : 0
				},
				yaxis: {
			        pad : 0,
			        tickOptions: {
			            show: false
			        }
			    }
			}
		});
	});
</script>
