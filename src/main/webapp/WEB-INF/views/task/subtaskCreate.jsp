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
<div class="white-frame" style="width: 800px; overflow: auto;display:table">
<h4>[${task.id}] - ${task.name}</h4>
</div>
<div class="white-frame" style="width: 700px; overflow: auto;display:table">
 	<form:form modelAttribute="taskForm" id="taskForm" method="post" style="margin-top: 5px;">
 		<input type="hidden" id="projects_list" name="project" value="${project.id}">
		<%-- Check all potential errors --%>
		<c:set var="name_error">
			<form:errors path="name" />
		</c:set>
		<c:set var="desc_error">
			<form:errors path="description" />
		</c:set>
		<c:set var="sprint_error">
			<form:errors path="addToSprint" />
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
		<%--------------------	Assign to -------------------------------%>
		<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.assign" />
				</h5>
		</div>
			<div class="form-group">
				<input id="assignee_auto" class="form-control" type="text" value="" style="width:300px;">
				<input id="assignee" type="hidden" name="assignee">
			</div>
			<span class="help-block"><s:message code="task.assign.help" /></span>
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
					<div id="task_type" class="image-combo"></div>
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu" role="menu"
					aria-labelledby="dropdownMenu1">
					<%
						pageContext.setAttribute("types", TaskType.values());
					%>
					<c:forEach items="${types}" var="enum_type">
						<c:if test="${enum_type.subtask}">
							<li><a class="taskType" tabindex="-1" href="#" id="${enum_type}" data-type="${enum_type}"><t:type type="${enum_type}" show_text="true" list="true"/></a></li>
						</c:if>
					</c:forEach>
					<li><a class="taskType" tabindex="-1" href="#" id="IDLE" data-type="IDLE"><t:type type="IDLE" show_text="true" list="true"/></a></li>
				</ul>
			</div>
			<span class="help-block"><s:message code="task.type.help" /> <a href="#" style="color:black"><span class="glyphicon glyphicon-question-sign"></span></a></span>
			<form:hidden path="type" id="type"/>
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
					<div id="task_priority" class="image-combo"></div>
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu" role="menu"
					aria-labelledby="dropdownMenu2">
					<c:forEach items="${priorities}" var="enum_priority">
						<li><a class="taskPriority" tabindex="-1" href="#" data-priority="${enum_priority}" id="${enum_priority}">
							<t:priority priority="${enum_priority}"/></a>
						</li>
					</c:forEach>
				</ul>
			</div>
			<form:hidden path="priority" id="priority"/>
			<form:errors path="priority" element="p"/>
		</div>
		<%-- Estimate --%>
		<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.estimate" />
				</h5>
		</div>
		<div id="estimate_div">
			<div class="form-group ${sprint_class}">
				<form:input path="estimate" class="form-control" style="width:150px" />
				<form:errors path="estimate" element="p" class="text-danger" />
				<span class="help-block"><s:message code="task.estimate.help" /><br>
					<s:message code="task.estimate.help.pattern" /> </span>
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
	$(".taskType").click(function(){
		var type = $(this).data('type');
   	 	$("#task_type").html($(this).html());
   		$("#type").val(type);
	});
	
	$(".taskPriority").click(function(){
		var priority = $(this).data('priority');
   	 	$("#task_priority").html($(this).html());
   		$("#priority").val(priority);
	});
	
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

	//INIT ALL
	getDefaultTaskType();
	getDefaultAssignee();
	getDefaultTaskPriority();
	
	$("#assignee_auto").click(function(){
		 $(this).select();
	});
	$("#assignee_auto").change(function(){
		 if(!$("#assignee_auto").val()){
			 $("#assignee").val(null);
		 }
		 checkIfEmpty();
	});
	var cache = {};
	//Assignee
	$("#assignee_auto").autocomplete({
		minLength : 1,
		delay : 500,
		//define callback to format results
		source : function(request, response) {
			var term = request.term;
			if ( term in cache ) {
		          response( cache[ term ] );
		          return;
		    }
			var url='<c:url value="/project/getParticipants"/>';
			var projectID = $("#projects_list").val();
			$.get(url,{id:projectID,term:term},function(result) {
					cache[ term ] = result;
					response($.map(result,function(item) {
						return {
							// following property gets displayed in drop down
							label : item.name+ " "+ item.surname,
							value : item.id,
						}
					}));
				});
			},
			//define select handler
			select : function(event, ui) {
				if (ui.item) {
					event.preventDefault();
					$("#assignee").val(ui.item.value);
					$("#assignee_auto").val(ui.item.label);
					$("#assignee_auto").removeClass("input-italic");
					checkIfEmpty();
				return false;
			}
		}
	});
	function getDefaultTaskType(){
		var thisType = $("#SUBTASK");
		var type = thisType.data('type');
   	 	$("#task_type").html(thisType.html());
   		$("#type").val(type);
	}
	function getDefaultTaskPriority(){
		var url='<c:url value="/project/getDefaultTaskPriority"/>';
		$.get(url,{id:$("#projects_list").val()},function(result,status){
				var thisPriority = $("#"+result);
				var priority = thisPriority.data('priority');
		   	 	$("#task_priority").html(thisPriority.html());
		   		$("#priority").val(priority);
		});
	}
	
	function getDefaultAssignee(){
		$("#assignee").val(null);
		$("#assignee_auto").val(null);
		var url='<c:url value="/project/getDefaultAssignee"/>';
		$.get(url,{id:$("#projects_list").val()},function(result,status){
			if(!result){
				$("#assignee").val(null);
			}
			else{
				$("#assignee_auto").val(result.name + " " + result.surname);
				$("#assignee").val(result.id);
				$("#assignee_auto").removeClass("input-italic");
				
			}
		});
		checkIfEmpty();
	}
	
	function checkIfEmpty(){
		if(!$("#assignee").val()){
			var unassign = '<s:message code="task.unassigned" />';
			$("#assignee_auto").val(unassign);
			$("#assignee_auto").addClass("input-italic");
		}
	}
});
</script>