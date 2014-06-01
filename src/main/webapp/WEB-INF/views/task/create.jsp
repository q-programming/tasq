<%@page import="com.qprogramming.tasq.task.TaskType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<security:authentication property="principal" var="user" />
<c:set var="taskName_text">
	<s:message code="task.name" text="Summary" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" />
</c:set>
<div class="white-frame" style="width: 700px; overflow: auto;">
	<h3>
		<s:message code="task.create" text="Create task"></s:message>
	</h3>
	<hr>
	<form:form modelAttribute="newTaskForm" id="newTaskForm" method="post">
		<%-- Check all potential errors --%>
		<c:set var="name_error">
			<form:errors path="name" />
		</c:set>
		<c:set var="desc_error">
			<form:errors path="description" />
		</c:set>
		<c:if test="${not empty name_error}">
			<c:set var="name_class" value="has-error" />
		</c:if>
		<c:if test="${not empty desc_error}">
			<c:set var="desc_class" value="has-error" />
		</c:if>
		<div class="form-group ${name_class }">
			<form:input path="name" class="form-control"
				placeholder="${taskName_text}" />
			<form:errors path="name" element="p" class="text-danger" />
		</div>
		<div class="form-group ${desc_class}">
			<form:textarea path="description" class="form-control" rows="5"
				placeholder="${taskDesc_text}" />
			<form:errors path="description" element="p" class="text-danger" />
		</div>
		<hr>
		<div class="form-group">
			<label><s:message code="project.project" /></label>
			<form:select path="project" id="project" style="width:300px;"
				class="form-control">
				<c:forEach items="${projects}" var="project">
					<option
						<c:if test="${project.active}"> selected style="font-weight:bold"</c:if>
						value="${project.projectId}">${project.name}</option>
				</c:forEach>
			</form:select>
			<span class="help-block"><s:message code="task.project.help" /></span>
		</div>
		<c:set var="type_error">
			<form:errors path="type" />
		</c:set>
		<c:if test="${not empty type_error}">
			<c:set var="type_error" value="border-color: #b94a48;" />
		</c:if>
		<div class="form-group">
			<label><s:message code="task.type" /></label>
			<div class="dropdown">
				<button id="type_button" class="btn btn-default " style="${type_error}" type="button" id="dropdownMenu1"
					data-toggle="dropdown">
					<div id="task_type" class="image-combo">Choose type</div>
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu" role="menu"
					aria-labelledby="dropdownMenu1">
					<%
						pageContext.setAttribute("types", TaskType.values());
					%>
					<c:forEach items="${types}" var="enum_type">
						<li><a tabindex="-1" href="#" id="${enum_type.code}"><t:type
								type="${enum_type}" show_text="true" list="true" /></a></li>
					</c:forEach>
				</ul>
			</div>
			<span class="help-block"><s:message code="task.type.help" /></span>
			<form:input path="type" type="hidden" id="type"/>
			<form:errors path="type" element="p" class="text-danger" />
		</div>
		<div class="form-group">
			<label><s:message code="task.estimate" /></label>
			<form:input path="estimate" class="form-control" style="width:150px" />
			<form:errors path="estimate" element="p" class="text-danger" />
			<span class="help-block"><s:message code="task.estimate.help" /><br>
				<s:message code="task.estimate.help.pattern" /> </span>
		</div>
		<div>
			<label><s:message code="task.storyPoints" /></label>
			<form:input path="story_points" class="form-control"
				style="width:150px" />
			<span class="help-block"><s:message
					code="task.storyPoints.help" /></span>
		</div>
		<div style="margin: 10px auto; text-align: right;">
			<button type="submit" class="btn btn-success">
				<s:message code="main.create" text="Create" />
			</button>
			<span class="btn" onclick="location.href='<c:url value="/"/>';"><s:message
					code="main.cancel" text="Cancel" /></span>
		</div>
	</form:form>
</div>
<script>
$(document).ready(function($) {
	<c:forEach items="${types}" var="enum_type">
	$("#${enum_type.code}").click(function() {
		console.log("${enum_type.code}");
		var type = '<img src="<c:url value="/resources/img/${enum_type.code}.png"/>"> <s:message code="task.type.${enum_type.code}"/>';
		$("#task_type").html(type);
		$("#type").val("${enum_type}");
	});
	</c:forEach>
});

	
</script>