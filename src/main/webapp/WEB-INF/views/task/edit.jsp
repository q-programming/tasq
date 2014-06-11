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
	<!-- 	<h3> -->
	<%-- 		<s:message code="task.edit" text="Create task"></s:message> --%>
	<!-- 	</h3> -->
	<form:form modelAttribute="newTaskForm" id="newTaskForm" method="post">
		<div>
			<h3>
				<t:type type="${newTaskForm.type}" />
				[${newTaskForm.id}] - ${newTaskForm.name}
				<%-- 			<form:hidden path="id"/> --%>
				<form:hidden path="type" />
			</h3>
		</div>
		<div>
			<h4>
				<s:message code="project.project" />
				: ${newTaskForm.project}
				<form:hidden path="project" />
			</h4>
		</div>
		<hr>

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
		<%-- Estimate --%>
		<hr>
		<h4>
			<s:message code="task.estimate" />
		</h4>
		<div id="estimate_div">
			<div class="form-group">
				<form:input path="estimate" class="form-control" style="width:150px" />
				<form:errors path="estimate" element="p" class="text-danger" />
				<span class="help-block"><s:message code="task.estimate.help" /><br>
					<s:message code="task.estimate.help.pattern" /> <br>
				<br>
				<s:message code="task.estimate.edit.help " /></span>
			</div>
			<div>
				<label><s:message code="task.storyPoints" /></label>
				<form:input path="story_points" class="form-control"
					style="width:150px" />
				<span class="help-block"><s:message
						code="task.storyPoints.help" /></span>
			</div>
		</div>
		<label class="checkbox" style="display: inherit; font-weight: normal">
			<input type="checkbox" name="no_estimation" id="no_estimation"
			value="true"> <s:message code="task.withoutEstimation" /> <span
			class="glyphicon glyphicon-question-sign a-tooltip"
			title="<s:message code ="task.withoutEstimation.help"/>"
			data-placement="right"></span>
		</label>

		<div style="margin: 10px auto; text-align: right;">
			<button type="submit" class="btn btn-success">
				<s:message code="task.edit" text="Edit"></s:message>
			</button>
			<span class="btn" onclick="location.href='<c:url value="/"/>';"><s:message
					code="main.cancel" text="Cancel" /></span>
		</div>
	</form:form>
</div>
<script>
	$(document)
			.ready(
					function($) {
						<c:forEach items="${types}" var="enum_type">
						$("#${enum_type.code}")
								.click(
										function() {
											console.log("${enum_type.code}");
											var type = '<img src="<c:url value="/resources/img/${enum_type.code}.png"/>"> <s:message code="task.type.${enum_type.code}"/>';
											$("#task_type").html(type);
											$("#type").val("${enum_type}");
										});
						</c:forEach>

						$("#no_estimation").change(function() {
							$("#estimate").val("");
							$('#estimate_div').slideToggle("slow");
						});

					});

	
</script>