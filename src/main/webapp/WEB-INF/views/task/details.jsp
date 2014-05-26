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
	<h3>[${task.taskId}] ${task.name}</h3>
	<hr>
	${task.description}
</div>
<script>
	$(document).ready(function($) {
	});

	
</script>