<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<c:set var="tasks_text">
	<s:message code="task.tasks" text="Tasks" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" arguments="" />
</c:set>
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
	<div style="display: table-header-group;">
		<c:forEach items="${releases}" var="release">
			<div>${release.id} ${release.release} ${release.comment}</div>
		</c:forEach>		
	</div>
</div>
