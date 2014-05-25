<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="projects_text">
	<s:message code="project.projects" />
</c:set>
<c:set var="projectDesc_text">
	<s:message code="project.description" />
</c:set>

<div>
	<h3>${projects_text}</h3>
</div>
<div class="white-frame" style="overflow: auto;">
	<security:authentication property="principal" var="user" />
	<table class="table table-condensed">
		<thead style="font-weight: bold;">
			<tr>
				<td style="width: 300px"><s:message code="project.name" /></td>
				<td><s:message code="project.description" /></td>
				<td style="width: 100px"><s:message code="project.action" /></td>
			</tr>
		</thead>
		<c:forEach items="${projects}" var="project">
			<c:if test="${project.active}">
				<tr style="background: lightblue;">
					<td><a href="<c:url value="project?id=${project.id}"/>"><b>[${project.projectId}] ${project.name}</b></a></td>
					<td><b>${project.description}</b></td>
					<td><a class="btn btn-default a-tooltip pull-right" style="padding: 6px 11px;"
						href='#'
						title="<s:message
									code="project.active" text="Set as avtive" />"
						data-placement="bottom"> <img
							src="<c:url value="/resources/img/active.gif"/>"></img></a></td>
				</tr>
			</c:if>
			<c:if test="${not project.active}">
				<tr>
					<td><a href="<c:url value="project?id=${project.id}"/>">[${project.projectId}] ${project.name}</a></td>
					<td>${project.description}</td>
					<td><a class="btn btn-default a-tooltip pull-right"
						href='<s:url value="/project/activate?id=${project.id}"></s:url>'
						title="<s:message
									code="project.activate" text="Set as avtive" />"
						data-placement="bottom"> <span
							class="glyphicon glyphicon-refresh"></span>
					</a></td>
				</tr>
			</c:if>


		</c:forEach>
	</table>
</div>
<script>
	$(document).ready(function($) {

	});

	
</script>