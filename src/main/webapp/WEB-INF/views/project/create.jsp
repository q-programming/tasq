<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="white-frame" style="height: 87vh; overflow: auto;">
	<security:authentication property="principal" var="user" />
	<c:set var="projectName_text">
		<s:message code="project.name" />
	</c:set>
	<c:set var="projectDesc_text">
		<s:message code="project.description" />
	</c:set>

	<h3>
		<s:message code="project.create"></s:message>
	</h3>
	<hr>
	<form:form modelAttribute="newProjectForm" id="newProjectForm" method="post">
		<div class="form-group">
			<form:input path="name" class="form-control"
				placeholder="${projectName_text}" />
			<form:errors path="name" element="p" class="text-danger" />
		</div>
		<div class="form-group">
			<form:textarea path="description" class="form-control" rows="5"
				placeholder="${projectDesc_text}" />
			<form:errors path="description" element="p" class="text-danger" />
		</div>
		<div class="form-group" style="margin: 0 auto; text-align:center">
			<div >
				<button type="submit" class="btn btn-default">
					<s:message code="project.create.btn" text="Create" />
				</button>
			</div>
		</div>
	</form:form>
</div>
<script>
	$(document).ready(function($) {
	});

	
</script>