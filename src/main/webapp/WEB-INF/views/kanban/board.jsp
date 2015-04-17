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
			<li class="active"><a style="color: black" href="#"><i
					class="fa fa-list-alt"></i> <s:message code="agile.board" /></a></li>
			<li><a style="color: black"
				href="<c:url value="/${project.projectId}/kanban/reports"/>"><i
					class="fa fa-line-chart"></i> <s:message code="agile.reports" /></a></li>
		</ul>
	</div>
	<div style="display: table-header-group;">
		<div style="display: table-cell; padding-bottom: 30px;">
			<span class="btn btn-default pull-left" id="new release" data-toggle="modal" data-target="#releaseModal">
				<i class="fa fa-clipboard"></i>&nbsp;<s:message code="agile.newRelease"/></span>
		</div>
		<div style="display: table-cell"></div>
		<div style="display: table-cell"></div>
		<div style="display: table-cell"></div>
		<div style="display: table-cell"></div>
		<div style="display: table-cell"></div>
		<div style="display: table-cell">
			<span class="btn btn-default pull-right" id="save_order"
				style="display: none"><i class="fa fa-floppy-o"></i>&nbsp;Save
				order</span>
		</div>
	</div>
	<jsp:include page="../agile/board.jsp" />
</div>
<jsp:include page="../modals/logWork.jsp" />
<jsp:include page="../modals/close.jsp" />
<jsp:include page="../modals/assign.jsp" />
<jsp:include page="../modals/release.jsp" />

