<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="white-frame" style="overflow: auto;">
	<security:authentication property="principal" var="user" />
	<c:set var="projectName_text">
		<s:message code="project.name" />
	</c:set>
	<c:set var="projectDesc_text">
		<s:message code="project.description" />
	</c:set>
	<div class="pull-right">
		<c:if test="${project.active}">
			<a class="btn btn-default a-tooltip pull-right"
				style="padding: 6px 11px;" href='#'
				title="<s:message
									code="project.active" text="Set as avtive" />"
				data-placement="bottom"> <img
				src="<c:url value="/resources/img/active.gif"/>"></img></a>
		</c:if>
		<c:if test="${not project.active}">
			<a class="btn btn-default a-tooltip pull-right"
				href='<s:url value="/project/activate?id=${project.id}"></s:url>'
				title="<s:message
									code="project.activate" text="Set as avtive" />"
				data-placement="bottom"> <span
				class="glyphicon glyphicon-refresh"></span>
			</a>
		</c:if>
	</div>
	<h3>${project.name}</h3>
	<hr>
	${project.description}
	<table>
	<c:forEach items="${tasks}" var="task">
		<tr><td>${task.name}</tr>
	</c:forEach>
	</table>
</div>
<script>
	$(document).ready(function($) {
	});

	
</script>