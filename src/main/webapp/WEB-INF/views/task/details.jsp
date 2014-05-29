<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="white-frame" style="overflow: auto;">
	<security:authentication property="principal" var="user" />
	<c:set var="taskName_text">
		<s:message code="task.name" text="Name" />
	</c:set>
	<c:set var="taskDesc_text">
		<s:message code="task.description" />
	</c:set>
	<h3>[${task.id}] ${task.name}</h3>
	<hr>
	${task.description}
	<hr>
	Estimate: ${task.estimate}
	<!-- Button trigger modal -->
	<button class="btn btn-default btn-sm" data-toggle="modal"
		data-target="#myModal">
		<span class="glyphicon glyphicon-time"></span> Log work
	</button>
	<br> Logged : ${task.logged_work}
	<hr>
	WorkLog
	<table class="table table-condensed">
		<c:forEach items="${task.worklog}" var="worklog">
			<tr>
				<td>${worklog.account}${worklog.type}-${worklog.time}</td>
			</tr>
		</c:forEach>
	</table>
</div>
<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">Log work</h4>
			</div>
			<form id="mainForm" name="mainForm" method="post"
				action="<c:url value="/logwork"/>">
				<div class="modal-body">

					<input type="hidden" name="taskID" value="${task.id}">
					<div class="form-group">
						<label>Log work</label> <input id="logged_work" name="logged_work"
							style="width: 150px" class="form-control" type="text" value="">
						<span class="help-block">How long you spent on this task?
							(eg 2w 5d 20h) </span>
					</div>

				</div>
				<div class="modal-footer">
					<button class="btn btn-default" type="submit">Log</button>
					<a class="btn" data-dismiss="modal">Cancel</a>
				</div>
			</form>
		</div>
	</div>
</div>

<script>
	$(document).ready(function($) {
	});

	
</script>