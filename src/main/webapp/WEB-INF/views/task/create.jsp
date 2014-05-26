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
<div class="white-frame" style="width: 600px; overflow: auto;">
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
			<label>Project</label>
			<form:select path="project" id="project" style="width:300px;"
				class="form-control">
				<c:forEach items="${projects}" var="project">
					<option
						<c:if test="${project.active}"> selected style="font-weight:bold"</c:if>>${project.name}
					</option>
				</c:forEach>
			</form:select>
			<span class="help-block">Project help</span>
		</div>
		<div class="form-group">
			<label>Type</label>
			<form:select path="type" id="type" style="width:300px;"
				class="form-control">
				<option>Task</option>
				<option>Issue</option>
				<option>Bug</option>
				<option>User story</option>
			</form:select>
			<span class="help-block">Task type help</span>
		</div>
		<div class="form-group">
			<label>Estimate</label>
			<form:input path="estimate" class="form-control" style="width:100px" />
			<span class="help-block">An estimate of how much work remains
				until this issue will be resolved.<br>The format of this is '
				*w *d *h *m ' (representing weeks, days, hours and minutes - where *
				can be any number)<br>Examples: 4d, 5h 30m, 60m and 3w.
			</span>
		</div>
		<div class="form-group">
			<label>Story points</label>
			<form:input path="estimate" class="form-control" style="width:100px" />
			<span class="help-block">Measurement of complexity and/or size
				of a requirement.</span>
		</div>


		<div class="form-group" style="margin: 0 auto; text-align: center">
			<div>
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