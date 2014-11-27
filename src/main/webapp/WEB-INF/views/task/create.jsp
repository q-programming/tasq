<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<security:authentication property="principal" var="user" />
<c:set var="taskName_text">
	<s:message code="task.name" text="Summary" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" />
</c:set>

<div class="white-frame" style="width: 700px; overflow: auto;display:table">
<div style="display:table-caption;margin-left: 10px;">
	<ul class="nav nav-tabs" style="border-bottom:0">
			<li class="active"><a style="color: black" href="#"><span class="glyphicon glyphicon-plus"></span> <s:message code="task.create" text="Create task"/></a></li>
			<li><a style="color: black" href="<c:url value="/task/import"/>"><span class="glyphicon glyphicon-import"></span> <s:message code="task.import"/></a></li>
	</ul>
</div>
<!-- 	<div class="mod-header"> -->
<!-- 		<h3 class="mod-header-title"> -->
<%-- 				<s:message code="task.create" text="Create task"/> --%>
<!-- 		</h3> -->
<!-- 	</div> -->
	<form:form modelAttribute="taskForm" id="taskForm" method="post" style="margin-top: 5px;">
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
		<%-------------------------Project ----------------------------------%>
		<div class="form-group">
			<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="project.project" />
				</h5>
			</div>
			<form:select id="projects_list" style="width:300px;" path="project"	class="form-control">
 				<c:forEach items="${projects_list}" var="list_project">
					<option id="${list_project.projectId}"
 						<c:if test="${list_project.id eq user.active_project}">selected style="font-weight:bold"
 						</c:if>
 						value="${list_project.id}">${list_project.name}</option>
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
		<%-----------------TASK TYPE ---------------%>
		<div class="form-group">
			<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.type" />
				</h5>
			</div>
			<div class="dropdown">
				<button id="type_button" class="btn btn-default "
					style="${type_error}" type="button" id="dropdownMenu1"
					data-toggle="dropdown">
					<div id="task_type" class="image-combo">
						<t:type	type="${project.default_type}" show_text="true" list="true" /></div>
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
			<span class="help-block"><s:message code="task.type.help" /> <a href="#"><span class="glyphicon glyphicon-question-sign"></span></a></span>
			<form:hidden path="type" id="type" value="${fn:toUpperCase(project.default_type)}"/>
			<form:errors path="type" element="p" class="text-danger" />
		</div>
		<%------------PRIORITY --------------------%>
		<div class="form-group">
			<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.priority" />
				</h5>
			</div>
			<div class="dropdown">
			<%
				pageContext.setAttribute("priorities", TaskPriority.values());
			%>
			
				<button id="priority_button" class="btn btn-default "
					type="button" id="dropdownMenu2"
					data-toggle="dropdown">
					<div id="task_priority" class="image-combo"><t:priority	priority="${project.default_priority}"/></div>
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu" role="menu"
					aria-labelledby="dropdownMenu2">
					<c:forEach items="${priorities}" var="enum_priority">
						<li><a tabindex="-1" href="#" id="${enum_priority}"><t:priority	priority="${enum_priority}"/></a></li>
					</c:forEach>
				</ul>
			</div>
			<form:hidden path="priority" id="priority" value="${fn:toUpperCase(project.default_priority)}" />
			<form:errors path="priority" element="p"/>
		</div>
		<%-----------SPRINT---------------------------%>
		<div>
			<div class="mod-header">
				<h5 class="mod-header-title">
					Sprint
				</h5>
			</div>
		<select class="form-control" id="addToSprint" name="addToSprint" style="width:300px;">
		</select>
		</div>
		<%-- Estimate --%>
		<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.estimate" />
				</h5>
		</div>
		<div id="estimate_div">
			<div class="form-group">
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
		</div>
		<label class="checkbox" style="display: inherit; font-weight: normal">
			<input type="checkbox" name="no_estimation" id="no_estimation"
			value="true"> <s:message code="task.withoutEstimation" /> <span
			class="glyphicon glyphicon-question-sign a-tooltip"
			title="<s:message code ="task.withoutEstimation.help"/>"
			data-placement="right"></span>
		</label>
		<%----------DUE DATE --------------------------%>
		<div>
			<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.dueDate" />
				</h5>
			</div>
			<form:input path="due_date" class="form-control datepicker"
				id="due_date" style="width:150px" />
				<span class="help-block"><s:message
						code="task.dueDate.help" /></span>
		</div>
		<%--------------Submit button -----------------%>
		<div style="margin: 10px auto; text-align: right;">
			<span class="btn" onclick="location.href='<c:url value="/"/>';"><s:message
					code="main.cancel" text="Cancel" /></span>
			<button type="submit" class="btn btn-success">
				<s:message code="main.create" text="Create" />
			</button>
		</div>
	</form:form>
</div>
<script>
$(document).ready(function($) {
	<c:forEach items="${types}" var="enum_type">
		$("#${enum_type.code}").click(function() {
			var type = '<img src="<c:url value="/resources/img/${enum_type.code}.png"/>"> <s:message code="task.type.${enum_type.code}"/>';
			$("#task_type").html(type);
			$("#type").val("${enum_type}");
		});
	</c:forEach>

	<c:forEach items="${priorities}" var="enum_priority">
	$("#${enum_priority}").click(function() {
		var priority = '<img src="<c:url value="/resources/img/${enum_priority.imgcode}.png"/>"> <s:message code="${enum_priority.code}"></s:message>';
		$("#task_priority").html(priority);
		$("#priority").val("${enum_priority}");
	});
	</c:forEach>
	//Projects
	
	$("#no_estimation").change(function() {
			$("#estimate").val("");
			$('#estimate_div').slideToggle("slow");
	});
	//------------------------------------Datepickers
	$(".datepicker").datepicker({
		minDate : '0'
	});
	$(".datepicker").datepicker("option", "dateFormat", "dd-mm-yy");
	$('.datepicker').datepicker("option", "firstDay", 1);
	$('.datepicker').datepicker($.datepicker.regional['${user.language}']);
	var currentDue = "${taskForm.due_date}";
	$("#due_date").val(currentDue);
	$("#projects_list").change(function(){
		fillSprints();
	});
	fillSprints();
	
	function fillSprints(){
		$.get('<c:url value="/getSprints"/>',{projectID:$("#projects_list").val()},function(result){
			$('#addToSprint').empty();
			$.each(result, function(key, sprint) {
				var isActive = "";
				if (sprint.active){
					isActive = " (<s:message code="agile.sprint.active"/>)";
				}
			    $('#addToSprint')
			         .append($("<option></option>")
			         .attr("value",sprint.sprintNo)
			         .text("Sprint " + sprint.sprintNo + isActive));
			     $('#addToSprint').val('');
			});
		});
	}
});

	
</script>