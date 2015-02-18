<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div class="white-frame"
	style="width: 80%; overflow: auto; display: table">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black" href="<c:url value="/events" />">
					<i class="fa fa-bell"></i> <s:message code="events.events" />
			</a></li>
			<li class="active"><a style="color: black" href="#"> <i
					class="fa fa-eye"></i> <s:message code="events.watching" /></a></li>
		</ul>
	</div>
	<div>
		<table class="table table-hover table-condensed">
			<thead class="theme">
				<tr>
					<th style="width: 55px;"></th>
					<th><s:message code="task.task"/></th>
					<th style="width: 100px;"><s:message code="events.watchers"/></th>
				</tr>
			</thead>
			<c:forEach items="${watching}" var="task">
				<tr>
					<td><i
						class="btn btn-default fa fa-eye-slash stopWatching a-tooltip"
						data-taskid="${task.id}"
						title="<s:message code="task.watch.stop"/>"></i></td>
					<td><t:type type="${task.type}" list="true" /><a
						href="<c:url value="/task?id=${task.id}"/>">${task}</a></td>
					<td style="text-align:center">${task.count}&nbsp;<i class="fa fa-eye"></i></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
<script>
	$(".stopWatching").click(function() {
		var clicked = $(this);
		var taskID = clicked.data('taskid');
		var url = '<c:url value="/task/watch"/>';
		$.post(url, {
			id : taskID
		}, function(result) {
			if (result.code == 'ERROR') {
				showError(result.message);
			} else {
				showSuccess(result.message);
				clicked.closest("tr").remove();
			}
		});
	});

	
</script>