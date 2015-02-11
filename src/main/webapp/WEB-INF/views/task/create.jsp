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
			<li class="active"><a style="color: black" href="#"><i class="fa fa-plus"></i> <s:message code="task.create" text="Create task"/></a></li>
			<li><a style="color: black" href="<c:url value="/task/import"/>"><i class="fa fa-download"></i> <s:message code="task.import"/></a></li>
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
		<c:set var="sprint_error">
			<form:errors path="addToSprint" />
		</c:set>
		
		<c:if test="${not empty name_error}">
			<c:set var="name_class" value="has-error" />
		</c:if>
		<c:if test="${not empty desc_error}">
			<c:set var="desc_class" value="has-error" />
		</c:if>
		<c:if test="${not empty sprint_error}">
			<c:set var="sprint_class" value="has-error" />
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
					<div id="task_type" class="image-combo"></div>
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu" role="menu"
					aria-labelledby="dropdownMenu1">
					<%
						pageContext.setAttribute("types", TaskType.values());
					%>
					<c:forEach items="${types}" var="enum_type">
						<c:if test="${not enum_type.subtask}">
							<li><a class="taskType" tabindex="-1" href="#" id="${enum_type}" data-type="${enum_type}"><t:type
										type="${enum_type}" show_text="true" list="true" /></a></li>
						</c:if>
					</c:forEach>
				</ul>
			</div>
			<span class="help-block"><s:message code="task.type.help" /> <a href="#" style="color:black">&nbsp;<i class="fa fa-question-circle"></i></a></span>
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
		<%-----------SPRINT---------------------------%>
		<div>
			<div class="mod-header">
				<h5 class="mod-header-title">
					Sprint
				</h5>
			</div>
			<div class="form-group ${sprint_class}">
				<select class="form-control ${sprint_class}" id="addToSprint" name="addToSprint" style="width:300px;">
				</select>
				<form:errors path="addToSprint" element="p" class="text-danger"/>
				<div id="sprintWarning" style="color: darkorange;margin-top: 10px;"></div>
			</div>
		</div>
		<%-- Estimate --%>
		<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.estimate" />
				</h5>
		</div>
		<div>
			<div class="form-group ${sprint_class}">
				<div class="input-group"><form:input path="estimate" class="form-control" style="width:150px" />&nbsp;<span id="estimate_optional"><s:message code="main.optional"/></span></div>
				<form:errors path="estimate" element="p" class="text-danger" />
				<span class="help-block"><s:message code="task.estimate.help" /><br>
					<s:message code="task.estimate.help.pattern" /> </span>
			</div>
			<div  id="estimate_div">
				<div class="form-group ${sprint_class}">
					<label><s:message code="task.storyPoints" /></label>
					<form:input path="story_points" class="form-control "
						style="width:150px" />
					<span class="help-block"><s:message
							code="task.storyPoints.help" /></span>
				</div>
			</div>
		</div>
		<label class="checkbox" style="display: inherit; font-weight: normal">
			<input type="checkbox" name="no_estimation" id="no_estimation"
			value="true"> <s:message code="task.withoutEstimation"  />&nbsp;<i class="fa fa-question-circle a-tooltip"
			data-html="true" title="<s:message  code ="task.withoutEstimation.help" />"
			data-placement="right"></i>
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
		checkTaskTypeEstimate(type);
   	 	$("#task_type").html($(this).html());
   		$("#type").val(type);
	});
	
	$(".taskPriority").click(function(){
		var priority = $(this).data('priority');
  	 	$("#task_priority").html($(this).html());
   		$("#priority").val(priority);
	});
	
	
	//Projects
	
	$("#no_estimation").click(function() {
		toggleEstimation();
	});
	
	function toggleEstimation(){
		if($("#no_estimation").prop("checked") == true){
			$('#estimate_div').slideUp("slow");
			$("#story_points").val("");
			$("#estimate_optional").show();
		}else{
			$("#estimate_optional").hide();
			$('#estimate_div').slideDown("slow");
		}
	}
	
	function checkTaskTypeEstimate(type){
		if (type == 'TASK' || type == 'BUG'){
			$("#no_estimation").prop("checked", true);
			toggleEstimation();
		}else{
			$("#no_estimation").prop("checked", false);
			toggleEstimation();
		}
	}

	
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
		getDefaultAssignee();
		getDefaultTaskType();
		getDefaultTaskPriority();
		fillSprints();
	});
	
	$("#addToSprint").change(function(){
		$("#sprintWarning").html('');
		var active = $('#addToSprint option:selected').data('active');
		if(active){
			var message = '<i class="fa fa-exclamation-circle"></i>'
						+ ' <s:message code="task.sprint.add.warning"/>';
			$("#sprintWarning").html(message);
		}
	});
	
	//INIT ALL
	fillSprints();
	getDefaultAssignee();
	getDefaultTaskType();
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
		var url='<c:url value="/project/getDefaultTaskType"/>';
		$.get(url,{id:$("#projects_list").val()},function(result,status){
				var thisType = $("#"+result);
				var type = thisType.data('type');
				checkTaskTypeEstimate(type)
		   	 	$("#task_type").html(thisType.html());
		   		$("#type").val(type);
		});
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
	
	function fillSprints(){
		$.get('<c:url value="/getSprints"/>',{projectID:$("#projects_list").val()},function(result){
			$('#addToSprint').empty();
			$('#addToSprint').append("<option></option>");
			$.each(result, function(key, sprint) {
				var isActive = "";
				if (sprint.active){
					isActive = " (<s:message code="agile.sprint.active"/>)";
				}
			    $('#addToSprint')
			         .append($("<option></option>")
			         .attr("value",sprint.sprintNo)
			         .attr("data-active",sprint.active)
			         .text("Sprint " + sprint.sprintNo + isActive));
			     $('#addToSprint').val('');
			});
		});
	}
});
</script>