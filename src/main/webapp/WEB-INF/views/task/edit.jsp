<%@page import="com.qprogramming.tasq.task.TaskType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<script src="<c:url value="/resources/js/trumbowyg.min.js" />"></script>
<script src="<c:url value="/resources/js/trumbowyg.editlink.js" />"></script>
<link href="<c:url value="/resources/css/trumbowyg.min.css" />" rel="stylesheet" media="screen" />
<security:authentication property="principal" var="user" />
<c:if test="${user.language ne 'en' }">
	<script src="<c:url value="/resources/js/trumbowyg.${user.language}.min.js" />"></script>
</c:if>
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
	<form:form modelAttribute="taskForm" id="taskForm" method="post">
		<div>
			<h3>
				<t:type type="${taskForm.type}" />
				[${taskForm.id}] - ${taskForm.name}
				<form:hidden path="id"></form:hidden>
			</h3>
		</div>
		<div>
			<h4>
				<s:message code="project.project" />
				: [${project.projectId}] ${project.name}
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
					<div id="task_type" class="image-combo"></div>
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu" role="menu"
					aria-labelledby="dropdownMenu1">
					<%
						pageContext.setAttribute("types", TaskType.values());
					%>
					<c:if test="${task.subtask}">
						<c:forEach items="${types}" var="enum_type">
							<c:if test="${enum_type.subtask}">
								<li><a class="taskType" tabindex="-1" href="#" id="${enum_type}" data-type="${enum_type}"><t:type
											type="${enum_type}" show_text="true" list="true" /></a></li>
							</c:if>
						</c:forEach>
						<li><a class="taskType clickable" tabindex="-1" id="IDLE" data-type="IDLE"><t:type type="IDLE" show_text="true" list="true"/></a></li>
					</c:if>
					<c:if test="${not task.subtask}">
						<c:forEach items="${types}" var="enum_type">
							<c:if test="${not enum_type.subtask}">
								<li><a class="taskType clickable" tabindex="-1" id="${enum_type}" data-type="${enum_type}"><t:type
											type="${enum_type}" show_text="true" list="true" /></a></li>
							</c:if>
						</c:forEach>
					</c:if>
				</ul>
			</div>
			<span class="help-block"><s:message code="task.type.help" /> <a href="#" style="color:black"><i class="fa fa-question-circle"></i></a></span>
			<input type="hidden" id="changed_taskType" name="type">
<%-- 			<form:hidden path="type" id="changed_taskType"/> --%>
			<form:errors path="type" element="p" class="text-danger" />
		</div>
		<%-- Estimate --%>
		<div id="estimate_div">
			<div class="form-group">
				<c:if test="${task.loggedWork ne '0m' && task.subtasks lt 1}">
					<div>
						<div class="mod-header">
							<h5 class="mod-header-title">
								<s:message code="task.remaining" />
							</h5>
						</div>
						<form:input path="remaining" class="form-control"
							style="width:150px" />
						<form:errors path="remaining" element="p" class="text-danger" />
						<span class="help-block"><s:message
								code="task.edit.remaining.help" /><br> <s:message
								code="task.estimate.help.pattern" /> </span>
					</div>
				</c:if>
				<c:if test="${task.loggedWork eq '0m' }">
					<div class="mod-header">
						<h5 class="mod-header-title">
							<s:message code="task.estimate" />
						</h5>
					</div>
					<form:input path="estimate" class="form-control"
						style="width:150px" />
					<form:errors path="estimate" element="p" class="text-danger" />
					<span class="help-block"><s:message
							code="task.estimate.help" /><br> <s:message
							code="task.estimate.help.pattern" /> </span>
				</c:if>
			</div>
			<c:if test="${not task.subtask}">
				<div id="storyPoints" <c:if test="${not task.estimated}">style="display:none"</c:if>>
					<label><s:message code="task.storyPoints" /></label>
					<form:input path="story_points" class="form-control"
						style="width:150px" />
					<span class="help-block"><s:message
							code="task.storyPoints.help" /></span>
				</div>
			</c:if>
			
		</div>
			<c:if test="${not task.inSprint}">
				<c:if test="${not task.estimated}">
					<c:set var="checked">
						checked="checked" 
					</c:set>
				</c:if>
				<label class="checkbox clickable" style="display: inherit; font-weight: normal;margin-left: 20px;">
					<input type="checkbox" name="estimated" id="estimated" ${checked}/>
					<s:message code="task.withoutEstimation"  />&nbsp;
					<i class="fa fa-question-circle a-tooltip"
						data-html="true" title="<s:message  code ="task.withoutEstimation.help" />"
						data-placement="right">
					</i>
				</label>
			</c:if>
		<div>
			<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.dueDate" />
				</h5>
			</div>
			<form:input path="due_date" class="form-control datepicker"
				id="due_date" style="width:150px" />
			<span class="help-block"><s:message code="task.dueDate.help" /></span>

		</div>

		<div style="margin: 10px auto; text-align: right;">
			<span class="btn"
				onclick="location.href='<c:url value="/task/${task.id}"/>';"><s:message
					code="main.cancel" text="Cancel" /></span>
			<button type="submit" class="btn btn-success">
				<i class="fa fa-pencil"></i>&nbsp;<s:message code="task.edit" text="Edit"></s:message>
			</button>
			
		</div>
	</form:form>
</div>
<script>
	$(document).ready(function($) {
		
		$("#estimated").click(function() {
			toggleEstimation();
		});
		
		function toggleEstimation(){
			if($("#estimated").prop("checked") == true){
				$('#storyPoints').slideUp("slow");
				$("#story_points").val("");
			}else{
				$('#storyPoints').slideDown("slow");
			}
		}
		
		var btnsGrps = jQuery.trumbowyg.btnsGrps;
		$('#description').trumbowyg({
			lang: '${user.language}',
			fullscreenable: false,
			removeformatPasted: true,
			autogrow: true,
			btns: ['formatting',
		           '|', btnsGrps.design,
		           '|', 'link',
		           '|', 'insertImage',
		           '|', btnsGrps.justify,
		           '|', btnsGrps.lists]
		});
		
		//------------------------------------Datepickers
		$(".datepicker").datepicker({
			minDate : '0'
		});
		$(".datepicker").datepicker("option", "dateFormat", "dd-mm-yy");
		$('.datepicker').datepicker("option", "firstDay", 1);
		var currentDue = "${taskForm.due_date}";
		$("#due_date").val(currentDue);
		getDefaultTaskType();
		
		$(".taskType").click(function(){
			$('#changed_taskType').val('');
			var type = $(this).data('type');
	   	 	$("#task_type").html($(this).html());
	   		$("#changed_taskType").val(type);
		});

		function getDefaultTaskType(){
			var taskType = '${task.type}';
			var thisType = $("#"+taskType);
	   	 	$("#task_type").html(thisType.html());
	   		$("#changed_taskType").val(taskType);
		}
	});
	
	
	
</script>