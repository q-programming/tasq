<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script src="<c:url value="/resources/js/trumbowyg.min.js" />"></script>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
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

<div class="col-lg-1 col-md-2 col-sm-3">
	<div id="menu" class="bs-docs-sidebar hidden-print affix">
<!-- 				<nav> -->
					<ul class="nav bs-docs-sidenav">
						<li>&nbsp;</li>
						<li class=""><a href="#nameA">${taskName_text}</a></li>
						<li class=""><a href="#descA">${taskDesc_text}</a></li>
						<li class=""><a href="#projectA">Other</a></li>
						<li class=""><a href="#createA">Create</a></li>
					</ul>
<!-- 				</nav> -->
	</div>
</div>
<div class="white-frame" style="overflow: auto;display:table">
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
	<form:form modelAttribute="taskForm" id="taskForm" method="post" style="margin-top: 5px;" enctype="multipart/form-data">
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
		
		<a class="anchor" id="nameA"></a>
		<div class="form-group ${name_class }">
			<form:input path="name" class="form-control"
				placeholder="${taskName_text}" />
			<form:errors path="name" element="p" class="text-danger" />
		</div>
		<a class="anchor" id="descA"></a>
		<div class="form-group ${desc_class}">
			<form:textarea path="description" class="form-control" rows="5"
				placeholder="${taskDesc_text}" />
			<form:errors path="description" element="p" class="text-danger" />
		</div>
		<%-------------------------Project ----------------------------------%>
		<a class="anchor" id="projectA"></a>
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
			<a class="anchor" id="assignToA"></a>
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
		<a class="anchor" id="typeA"></a>
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
		<a class="anchor" id="priorityA"></a>
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
		<a class="anchor" id="sprintA"></a>
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
		<%--------------- Estimate ---------------------%>
		<a class="anchor" id="estimateA"></a>
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
			<input type="checkbox" name="estimated" id="estimated"
			value="true"> <s:message code="task.withoutEstimation"  />&nbsp;<i class="fa fa-question-circle a-tooltip"
			data-html="true" title="<s:message  code ="task.withoutEstimation.help" />"
			data-placement="right"></i>
		</label>
		<%----------DUE DATE --------------------------%>
		<a class="anchor" id="dueDateA"></a>
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
		<%----------FILE UPLOAD --------------------------%>
		<a class="anchor" id="filesA"></a>
		<div>
			<div class="mod-header">
				<h5 class="mod-header-title">
					<s:message code="task.files" />
				</h5>
			</div>
			<div class="pull-right">
				<span id="addMoreFiles" class="btn btn-default btn-sm">
					<i class="fa fa-plus"></i><i class="fa fa-file"></i>&nbsp;Add more files
				</span>
			</div>
			<table id="fileTable" style="width: 300px;">
			</table>
		</div>
		
		<%--------------Submit button -----------------%>
		<a class="anchor" id="createA"></a>
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
	var fileTypes='.doc,.docx,.rtf,.txt,.odt,.xls,.xlsx,.ods,.csv,.pps,.ppt,.pptx,.jpg,.png,.gif';
	var btnsGrps = jQuery.trumbowyg.btnsGrps;
	$('#description').trumbowyg({
		lang: '${user.language}',
		fullscreenable: false,
		btns: ['formatting',
	           '|', btnsGrps.design,
	           '|', 'link',
	           '|', 'insertImage',
	           '|', btnsGrps.justify,
	           '|', btnsGrps.lists]
	});
	
	$(".file_upload").bootstrapFileInput();
	
	$(document).on("change",".file_upload",function(e) {
		var td = $(this).closest("td");
		td.find(".removeFile").remove();
		td.append('<span class="btn btn-default pull-right removeFile"><i class="fa fa-trash"></i><span>');
    });
	
	$("#addMoreFiles").click(function(){
		addFileInput();
	});
	
	$(document).on("click",".removeFile",function(e) {
		$(this).closest("tr").remove();
		if($('#fileTable tr').length==0){
			addFileInput();
		}
    });

	
	function addFileInput(){
		var choose = ' <s:message code="task.chooseFile" />';
		var title = "<i class='fa fa-file'></i>"+choose;
		var inputField = '<input class="file_upload" name="files" type="file" accept="'+fileTypes+'" title="'+title+'" data-filename-placement="inside">';
		$("#fileTable").append('<tr><td style="width:300px">'+inputField+'</td></tr>');
		$("#fileTable tr:last").find(".file_upload").bootstrapFileInput();;
	}
	
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
	
	$("#estimated").click(function() {
		toggleEstimation();
	});
	
	function toggleEstimation(){
		if($("#estimated").prop("checked") == true){
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
	addFileInput();
	$('body').scrollspy({
		target : '#menu'
	});
	
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