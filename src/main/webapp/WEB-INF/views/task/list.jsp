<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<style>
.subtaskLink{
   color: inherit;
}
</style>

<c:set var="tasks_text">
	<s:message code="task.tasks" text="Tasks" />
</c:set>
<c:set var="taskDesc_text">
	<s:message code="task.description" text="Description" />
</c:set>
<security:authentication property="principal" var="user" />
<c:if test="${not empty param.projectID}">
	<c:set var="active_project" value="${param.projectID}" />
</c:if>
<div>
	<div style="display: table-cell;">
		<h4>${tasks_text}</h4>
	</div>
	<div style="display: table-cell; padding-left: 20px">
		<select id="project" name="project" style="width: 300px;"
			class="form-control">
			<c:forEach items="${projects}" var="project">
				<option
					<c:if test="${active_project eq project.projectId}">
						selected
					</c:if>
					value="${project.projectId}">${project.name}</option>
			</c:forEach>
		</select>
	</div>
	<%--------------FILTERS ----------------------------%>
	<div style="display: table-cell; padding-left: 20px; width: 100%;line-height: 30px;">
		<c:if
			test="${not empty param.projectID || not empty param.state || not empty param.query || not empty param.priority}">
			<c:if test="${not empty param.projectID}">
				<c:set var="projID_url">
								projectID=${param.projectID}&
							</c:set>
			</c:if>
			<c:if test="${not empty param.state}">
				<c:set var="state_url">
								state=${param.state}&
				</c:set>
			</c:if>
			<c:if test="${not empty param.query}">
				<c:set var="query_url">
								query=${param.query}&
				</c:set>
			</c:if>
			<c:if test="${not empty param.priority}">
				<c:set var="priority_url">
								priority=${param.priority}&
				</c:set>
			</c:if>
			<c:if test="${not empty param.projectID}">
				<span><s:message code="project.project" />: <span
					class="filter_span"> ${param.projectID}<a
						href="<c:url value="tasks?${state_url}${query_url}${priority_url}"/>">
						<i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
						</a></span></span>
			</c:if>
			<c:if test="${not empty param.state}">
				<span><s:message code="task.state" />: <span
					class="filter_span"><t:state state="${param.state}" /> <a
						href="<c:url value="tasks?${projID_url}${query_url}${priority_url}"/>">
						<i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i></a>
						</span></span>
			</c:if>
			<c:if test="${not empty param.priority}">
				<span><s:message code="task.priority" />: <span
					class="filter_span"><t:priority priority="${param.priority}" />
						<a
						href="<c:url value="tasks?${projID_url}${query_url}${state_url}"/>">
						<i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
						</a></span></span>
			</c:if>
			<c:if test="${not empty param.query}">
				<c:set var="query_url">
							query=${param.query}&
			</c:set>
				<span style="white-space:nowrap;"><s:message code="main.search" />: <span
					class="filter_span"> ${fn:substring(param.query, 0, 40)}...<a
						href="<c:url value="tasks?${projID_url}${state_url}${priority_url}"/>">
						<i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
						</a></span></span>
			</c:if>
		</c:if>
	</div>
	<div style="display: table-cell; padding-left: 20px;">
		<div style="display:table-row">
			<div id="buttDiv" style="display: table-cell;">
				<a class="btn btn-default export_startstop" style="width: 120px;">
					<i class="fa fa-upload"></i>
					<s:message code="task.export" />
				</a>
			</div>
			<div id="fileDiv" style="display:none">
				<div style="display: table-cell">
					<a class="btn export_startstop"><s:message code="main.cancel"/></a>
				</div>
				<div style="display: table-cell">
					<a id="fileExport" class="btn btn-default">
						<i class="fa fa-long-arrow-down"></i><i class="fa fa-file"></i>  <s:message code="task.export.selected"/>
					</a>
				</div>
			</div>
		</div>
		<div style="margin-top:10px">
						<s:message code="tasks.subtasks" />&nbsp;
						<i id="opensubtask" class="fa fa-plus-square clickable a-tooltip" title="<s:message code="task.subtask.showall"/>"></i> 
						<i id="hidesubtask" class="fa fa-minus-square clickable a-tooltip" title="<s:message code="task.subtask.hideall"/>"></i>
		</div>
	</div>
</div>
<%--------TASK LIST ----------%>
<div class="white-frame">
	<security:authentication property="principal" var="user" />
	<table class="table table-hover table-condensed">
		<thead class="theme">
			<tr>
				<th class="export_cell export-hidden" style="width: 30px"><input
					id="select_all" type="checkbox" class="a-tooltip"
					title="<s:message code="task.export.clickAll"/>"></th>
				<th style="width: 30px;text-align: center;"><s:message code="task.type" /></th>
				<th style="width: 30px;"><span class="dropdown a-tooltip"
					title="<s:message code="task.priority" />"
					style="padding-top: 5px; cursor: pointer;"> <a
						class="dropdown-toggle theme" type="button" id="dropdownMenu2"
						data-toggle="dropdown" style="color: black"> <span
							class="caret theme"></span></a> 
							<%
 								pageContext.setAttribute("priorities", TaskPriority.values());
 							%>
						<ul class="dropdown-menu">
							<c:forEach items="${priorities}" var="priority">
								<li><a
									href="<c:url value="tasks?${projID_url}${query_url}${state_url}priority=${priority}"/>"><t:priority
											priority="${priority}"></t:priority></a></li>
							</c:forEach>
						</ul>
				</span></th>
				<th style="width: 500px"><s:message code="task.name" /></th>
				<th><s:message code="task.progress" /></th>
				<th>
					<div class="dropdown" style="padding-top: 5px; cursor: pointer;">
						<a class="dropdown-toggle theme" type="button" id="dropdownMenu1"
							data-toggle="dropdown"><s:message code="task.state" /><span
							class="caret theme"></span></a>
						<%
							pageContext.setAttribute("states", TaskState.values());
						%>
						<ul class="dropdown-menu">
							<c:forEach items="${states}" var="state">
								<li><a
									href="<c:url value="tasks?${projID_url}${query_url}${priority_url}state=${state}"/>"><t:state
											state="${state}"></t:state></a></li>
							</c:forEach>
							<li class="divider"></li>
							<li><a
								href="<c:url value="tasks?${projID_url}${query_url}${priority_url}state=ALL"/>">
									<t:state state="ALL"></t:state>
								</a>
							</li>
						</ul>
					</div>

				</th>
				<th style="width: 200px"><s:message code="task.assignee" /></th>
			</tr>
		</thead>
		<%----------------TASKS -----------------------------%>
		<form id="exportTaskForm" method="POST"	enctype="multipart/form-data" action="<c:url value="/task/export"/>">
		<c:forEach items="${tasks}" var="task">
			<c:if test="${task.id eq user.active_task[0]}">
				<tr style="background: #428bca; color: white">
					<c:set var="blinker" value="blink" />
					<c:set var="link">
						style="color:white"
					</c:set>
			</c:if>
			<c:if test="${task.id ne user.active_task[0]}">
				<c:set var="blinker" value="" />
				<c:set var="tr_bg" value="" />
				<c:set var="link" value="" />
				<c:if test="${task.state eq 'CLOSED'}">
					<c:set var="tr_bg" value="background: rgba(50, 205, 81, 0.12);" />
				</c:if>
				<c:if test="${task.state eq 'BLOCKED'}">
					<c:set var="tr_bg" value="background: rgba(205, 50, 50, 0.12);" />
				</c:if>
				<tr style="${tr_bg}">
			</c:if>
			<td class="export_cell export-hidden"><input class="export"
				type="checkbox" name="tasks" value="${task.id}"></td>
			<td style="text-align: center;"><t:type type="${task.type}" list="true" /></td>
			<td><t:priority priority="${task.priority}" list="true" /></td>
			<td>
				<c:if test="${task.subtasks gt 0}">
					<i class="subtasks fa fa-plus-square" data-task="${task.id}" id="subtasks${task.id}"></i>
				</c:if>
				<a href="<c:url value="task?id=${task.id}"/>"
				style="color: inherit;<c:if test="${task.state eq 'CLOSED' }">
							text-decoration: line-through;
							</c:if>">[${task.id}]
					${task.name}</a>
			</td>
			<c:if test="${not task.estimated}">
				<td>${task.loggedWork}</td>
			</c:if>
			<c:if test="${task.estimated}">
				<td><div class="progress" style="width: 50px">
						<c:set var="logged_class"></c:set>
						<c:set var="percentage">${100-task.percentage_left}</c:set>
						<c:if
							test="${task.percentage_logged gt 100 or task.state eq 'BLOCKED'}">
							<c:set var="logged_class">progress-bar-danger</c:set>
							<c:set var="percentage">${100-task.overCommited}</c:set>
						</c:if>
						<c:if test="${task.estimate eq '0m' && task.remaining ne '0m'}">
							<c:set var="percentage">${task.percentage_logged}</c:set>
						</c:if>
						<c:if test="${task.state eq 'CLOSED'}">
							<c:set var="logged_class">progress-bar-success</c:set>
							<c:set var="percentage">100</c:set>
						</c:if>
						<c:if test="${task.state eq 'TO_DO'}">
							<c:set var="percentage">0</c:set>
						</c:if>
						<div class="progress-bar ${logged_class}" role="progressbar"
							aria-valuenow="${percentage}" aria-valuemin="0"
							aria-valuemax="100" style="width:${percentage}%"></div>
					</div></td>
			</c:if>
			<td class="${blinker}"><t:state state="${task.state}"></t:state></td>
			<td><c:if test="${empty task.assignee}">
					<i><s:message code="task.unassigned" /></i>
				</c:if> <c:if test="${not empty task.assignee}">
					<img data-src="holder.js/20x20"
						style="height: 20px; padding-right: 5px;"
						src="<c:url value="/userAvatar/${task.assignee.id}"/>" />
					<a ${link} href="<c:url value="/user?id=${task.assignee.id}"/>">${task.assignee}</a>
				</c:if></td>
			</tr>
		</c:forEach>
		</form>
	</table>
</div>
<div id="loading" class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
    	<div class="centerPadded">
      		<img src="<c:url value="/resources/img/loading.gif"/>"></img>
      		<br><s:message code="task.export.prepareFile"/>
  	  	</div>
  	  	<div class="centerPadded">
  	  		<a href="<c:url value="/tasks"/>"><i class="fa fa-lg fa-check-circle-o"></i></span> <s:message code="task.export.goBack"/></a>
  	  	</div>
    </div>
  </div>
</div>
<jsp:include page="subtasks.jsp" />
<script>
	$(document).ready(function($) {
		taskURL = '<c:url value="/task?id="/>';
		apiurl = '<c:url value="/task/getSubTasks"/>';
		small_loading_indicator = '<div id="small_loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br></div>';

		$("#project").change(function(){
			var query = "${query_url}";
			var state = "${state_url}";
			var link = '<c:url value="/tasks?projectID="/>' + $(this).val()+"&" + query + state;
			window.location = link + $(this).val();
		});
		$(".export_startstop").click(function(){
				$(".export_cell").toggleClass('export-hidden');
				$("#buttDiv").toggle();
				$("#fileDiv").toggle();
		});
		$("#fileExport").click(function(){
			var atLeastOnechecked = false;
			$('.export').each(function() {
                if(this.checked){
                	atLeastOnechecked = true;
                	return false;
                }           
            });
		    if (atLeastOnechecked){
				$("#exportTaskForm").submit();
				$('#loading').modal({
	 	            show: true,
	 	            keyboard: false,
	 	            backdrop: 'static'
	 	     });
		    }
		});
		
		$("#select_all").click(function(){
	        if(this.checked) {
	            $('.export').each(function() {
	                this.checked = true;               
	            });
	        }else{
	            $('.export').each(function() {
	                this.checked = false;                       
	            });         
	        }
		});
	});
</script>