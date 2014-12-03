<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
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
	<div style="display: table-cell; padding-left: 20px; width: 100%">
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
						href="<c:url value="tasks?${state_url}${query_url}${priority_url}"/>"><span
							class="glyphicon glyphicon-remove"
							style="font-size: smaller; margin-left: 3px; color: lightgray"></span></a></span></span>
			</c:if>
			<c:if test="${not empty param.state}">
				<span><s:message code="task.state" />: <span
					class="filter_span"><t:state state="${param.state}" /> <a
						href="<c:url value="tasks?${projID_url}${query_url}${priority_url}"/>"><span
							class="glyphicon glyphicon-remove"
							style="font-size: smaller; margin-left: 3px; color: lightgray"></span></a></span></span>
			</c:if>
			<c:if test="${not empty param.priority}">
				<span><s:message code="task.priority" />: <span
					class="filter_span"><t:priority priority="${param.priority}" />
						<a
						href="<c:url value="tasks?${projID_url}${query_url}${state_url}"/>"><span
							class="glyphicon glyphicon-remove"
							style="font-size: smaller; margin-left: 3px; color: lightgray"></span></a></span></span>
			</c:if>
			<c:if test="${not empty param.query}">
				<c:set var="query_url">
							query=${param.query}&
			</c:set>
				<span><s:message code="main.search" />: <span
					class="filter_span"> ${param.query}<a
						href="<c:url value="tasks?${projID_url}${state_url}${priority_url}"/>"><span
							class="glyphicon glyphicon-remove"
							style="font-size: smaller; margin-left: 3px; color: lightgray"></span></a></span></span>
			</c:if>
		</c:if>
	</div>
	<div style="display: table-cell; padding-left: 20px;">
		<button id="export_start" class="btn btn-default">
			<span class="glyphicon glyphicon-export"></span>
			<s:message code="task.export" />
		</button>
	</div>
</div>
<div class="white-frame">
	<security:authentication property="principal" var="user" />
	<table class="table table-condensed">
		<thead class="theme">
			<tr>
				<th class="export_cell export-hidden" style="width: 30px"><input
					id="select_all" type="checkbox" class="a-tooltip"
					title="<s:message code="task.export.clickAll"/>">
				</th>
				<th style="width: 30px"><s:message code="task.type" /></th>
				<th style="width: 30px"><span class="dropdown a-tooltip"
					title="<s:message code="task.priority" />"
					style="padding-top: 5px; cursor: pointer;"> <a
						class="dropdown-toggle theme" type="button" id="dropdownMenu2"
						data-toggle="dropdown" style="color: black"> <span
							class="caret theme"></span></a> <%
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
								href="<c:url value="tasks?${projID_url}${query_url}${priority_url}state=OPEN"/>"
								class="a-tooltip"
								title="<s:message code="task.state.open.hint"/>"><t:state
										state="OPEN"></t:state> </a></li>
						</ul>
					</div>

				</th>
				<th style="width: 200px"><s:message code="task.assignee" /></th>
			</tr>
		</thead>
		<%----------------TASKS -----------------------------%>
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
				type="checkbox"></td>
			<td><t:type type="${task.type}" list="true" /></td>
			<td><t:priority priority="${task.priority}" list="true" /></td>
			<td><a href="<c:url value="task?id=${task.id}"/>"
				style="color: inherit;<c:if test="${task.state eq 'CLOSED' }">
							text-decoration: line-through;
							</c:if>">[${task.id}]
					${task.name}</a></td>
			<c:if test="${not task.estimated}">
				<td>${task.logged_work}</td>
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
							<c:set var="percentage">${100- task.percentage_logged}</c:set>
						</c:if>
						<c:if test="${task.state eq 'CLOSED'}">
							<c:set var="logged_class">progress-bar-success</c:set>
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
	</table>
</div>
<script>
	$(document).ready(function($) {
		$("#project").change(function(){
			var query = "${query_url}";
			var state = "${state_url}";
			var link = "<c:url value="/tasks?projectID="/>" + $(this).val()+"&" + query + state;
			window.location = link + $(this).val();
		});
		$("#export_start").click(function(){
			if($("#export_start").attr( "operation")){
				alert("go");
			} else{
				var title="<s:message code="task.export.selected"/>";
				$(".export_cell").toggle('slow', function() {
				    $(this).toggleClass('export-hidden');
				});
				$("#export_start").html(title);
				$("#export_start").attr( "operation","finish");
			}
		});
		$("#select_all").click(function(){
	        if(this.checked) { // check select status
	            $('.export').each(function() { //loop through each checkbox
	                this.checked = true;  //select all checkboxes with class "checkbox1"               
	            });
	        }else{
	            $('.export').each(function() { //loop through each checkbox
	                this.checked = false; //deselect all checkboxes with class "checkbox1"                       
	            });         
	        }
		});
	});

	
</script>