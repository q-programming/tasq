<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
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
						<c:if test="${project.active}"> selected style="font-weight:bold"</c:if>>${project.name}
					</option>
				</c:forEach>
			</form:select>
			<span class="help-block"><s:message code ="task.project.help" /></span>
		</div>
		<div class="form-group">
			<label><s:message code ="task.type" /></label>
			<form:select path="type" id="type" style="width:300px;"
				class="form-control">
				<option>Task</option>
				<option>Issue</option>
				<option>Bug</option>
				<option>User story</option>
			</form:select>
			<span class="help-block"><s:message code ="task.type.help" /></span>
		</div>
		<div class="form-group">
			<label><s:message code ="task.estimate" /></label>
			<form:input path="estimate" class="form-control" style="width:150px" />
			<span class="help-block"><s:message code ="task.estimate.help" /><br><s:message code ="task.estimate.help.pattern" />
			</span>
		</div>
		<div>
			<label><s:message code ="task.storyPoints" /></label>
			<form:input path="estimate" class="form-control" style="width:150px" />
			<span class="help-block"><s:message code ="task.storyPoints.help" /></span>
		</div>
		<div style="margin: 10px auto; text-align: right;">
				<button type="submit" class="btn btn-success">
					<s:message code="main.create" text="Create" />
				</button>
				<span class="btn" onclick="location.href='<c:url value="/"/>';"><s:message code="main.cancel" text="Cancel" /></span>
		</div>
	</form:form>
</div>
<script>
	$(document).ready(function($) {
	});

	
</script>