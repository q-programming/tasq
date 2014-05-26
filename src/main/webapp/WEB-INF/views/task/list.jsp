<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="tasks_text">
	<s:message code="tasks.tasks" text="Tasks" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" />
</c:set>

<div>
	<h3>${tasks_text}</h3>
</div>
<div class="white-frame" style="overflow: auto;">
	<security:authentication property="principal" var="user" />
	<table class="table table-condensed">
		<thead style="font-weight: bold;">
			<tr>
				<td style="width: 300px"><s:message code="task.name" /></td>
				<td>${taskDesc_text}</td>
				<td style="width: 100px"><s:message code="task.action" /></td>
			</tr>
		</thead>
		<c:forEach items="${tasks}" var="task">
			<tr>
				<td><a href="<c:url value="project?id=${task.id}"/>">[${task.taskId}]
						${task.name}</a></td>
				<td>${project.description}</td>
			</tr>
		</c:forEach>
	</table>
</div>
<script>
	$(document).ready(function($) {

	});

	
</script>