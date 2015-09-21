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
<security:authentication property="principal" var="user" />
<div>
	<h3>${projects_text}</h3>
</div>
<div class="white-frame" style="overflow: auto;">
	<table class="table table-condensed">
		<thead>
			<tr>
				<th style="width: 300px"><s:message code="project.name" /></th>
				<th><s:message code="project.description" /></th>
				<th><s:message code="project.admin" /></th>
				<th style="width: 100px"><s:message code="main.action" /></th>
			</tr>
		</thead>
		<c:forEach items="${projects}" var="project">
			<c:if test="${project.id eq user.active_project}">
				<tr style="background: #428bca; color: white">
					<td><a style="color: white"
						href="<c:url value="/project/${project.projectId}"/>"><b>[${project.projectId}]
								${project.name}</b></a></td>
					<td><b>${project.description}</b></td>
					<td><c:forEach var="admin" items="${project.administrators}"
							end="0">
							<a href="<c:url value="/user/${admin.username}"/>" style="color:white;">${admin}</a>
						</c:forEach></td>
					<td><a class="btn btn-default a-tooltip pull-right"
						style="padding: 6px 11px;" href='#'
						title="<s:message
									code="project.active" text="Set as avtive" />"
						data-placement="bottom"> <i class="fa fa-refresh fa-spin"></i></a></td>
				</tr>
			</c:if>
			<c:if test="${project.id ne user.active_project}">
				<tr>
					<td><a href="<c:url value="/project/${project.projectId}"/>">[${project.projectId}]
							${project.name}</a></td>
					<td>${project.description}</td>
					<td><c:forEach var="admin" items="${project.administrators}"
							end="0">
							<a href="<c:url value="/user/${admin.username}"/>" style="color:black;">${admin}</a>
						</c:forEach></td>
					<td><a class="btn btn-default a-tooltip pull-right"
						href='<s:url value="/project/activate/${project.projectId}"></s:url>'
						title="<s:message
									code="project.activate" text="Set as avtive" />"
						data-placement="bottom"> <i class="fa fa-refresh"></i>
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