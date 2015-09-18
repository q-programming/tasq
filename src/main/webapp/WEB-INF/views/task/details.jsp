<%@page import="com.qprogramming.tasq.task.worklog.LogType"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@page import="com.qprogramming.tasq.account.Roles"%>
<%@page import="com.qprogramming.tasq.task.link.TaskLinkType"%>
<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="myfn" uri="/WEB-INF/tags/custom.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script src="<c:url value="/resources/js/bootstrap-tagsinput.js" />"></script>
<link href="<c:url value="/resources/css/bootstrap-tagsinput.css" />" rel="stylesheet" media="screen" />
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<security:authentication property="principal" var="user" />
<c:set var="is_user" value="<%=Roles.isUser()%>" />
<c:if test="${(myfn:contains(task.project.administrators,user) || is_admin || task.owner.id == user.id)}">
	<c:set var="can_edit" value="true" />
</c:if>
<c:if test="${task.assignee.id == user.id}">
	<c:set var="is_assignee" value="true" />
</c:if>
<div class="white-frame" style="overflow: auto;">
	<%----------------------TASK NAME-----------------------------%>
	<c:set var="taskName_text">
		<s:message code="task.name" text="Name" />
	</c:set>
	<c:set var="taskDesc_text">
		<s:message code="task.description" />
	</c:set>
	<%----------------------EDIT MENU-----------------------------%>
	<div>
		<div class="pull-right">
			<c:if test="${is_user  && task.state ne'CLOSED'}">
				<a class="btn btn-default btn-sm a-tooltip" href="#"
					data-toggle="dropdown"> <i class="fa fa-lg fa-pencil"></i>
				</a>
				<ul class="dropdown-menu" style="top: inherit; right: inherit;">
					<c:if test="${can_edit}">
						<li><a href="<c:url value="task/edit?id=${task.id}"/>"> <i
								class="fa fw fa-pencil"></i> <s:message code="task.edit" />
						</a></li>
					</c:if>
					<li><a class="linkButton" href="#"> <i
							class="fa fw fa-link fa-flip-horizontal"></i>&nbsp;<s:message
								code="task.link" />
					</a></li>
					<c:if test="${not task.subtask}">
						<li><a href="<c:url value="task/${task.id}/subtask"/>">
								<i class="fa fw fa-sitemap"></i>&nbsp;<s:message
								code="task.subtasks.add" />
							</a>
						</li>
					</c:if>
					<li>
						<a href="<c:url value="/task/create"/>?linked=${task.id}&project=${task.project.id}">
								<i class="fa fw fa-plus"></i>
								<s:message code="task.linked.create" />
						</a>
					</li>
					<li><a class="addFileButton" href="#" data-toggle="modal"
						data-target="#files_task" data-taskID="${task.id}"> <i
							class="fa fw fa-file"></i>&nbsp;<s:message code="task.addFile" />
					</a></li>
					<c:if test="${task.subtask}">
						<li><a href="#" class="convert2task" data-toggle="modal" data-target="#convert2task" 
								data-taskid="${task.id}" data-type="${task.type}" data-project="${task.project.id}" 
								>
								<i class="fa fw fa-level-up"></i>&nbsp;<s:message
								code="task.subtasks.2task" />
							</a>
						</li>
					</c:if>
					

				</ul>
			</c:if>
			<c:if test="${task.state eq'CLOSED'}">
				<a href="<c:url value="/task/create"/>?linked=${task.id}&project=${task.project.id}"
					class="btn btn-default btn-sm a-tooltip" title="<s:message code="task.linked.create" />">
					<i class="fa fw fa-plus"></i>
				</a>
			</c:if>
			<button id="watch" class="btn btn-default btn-sm a-tooltip" title=""
				data-html="true">
				<c:if test="${watching}">
					<i id="watch_icon" class="fa fa-lg fa-eye-slash"></i>
				</c:if>
				<c:if test="${not watching}">
					<i id="watch_icon" class="fa fa-lg fa-eye"></i>
				</c:if>
			</button>

			<c:if test="${can_edit && user.isPowerUser}">
				<a class="btn btn-default btn-sm a-tooltip delete_btn"
					href="<c:url value="/task/delete?id=${task.id}"/>"
					title="<s:message code="task.delete" text="Delete task" />"
					data-lang="${pageContext.response.locale}"
					data-msg='<s:message code="task.delete.confirm"></s:message>'>
					<i class="fa fa-lg fa-trash-o"></i>
				</a>
			</c:if>
		</div>
		<h3>
			<t:type type="${task.type}" />
			<c:if test="${task.subtask}">
				<a href='<c:url value="/task?id=${task.parent}"/>'>[${task.parent}]</a>
			/ [${task.id}] ${task.name}
			</c:if>
			<c:if test="${not task.subtask}">
				<a href='<c:url value="/project?id=${task.project.id}"/>'>${task.project.projectId}</a>
			/ [${task.id}] ${task.name}
			</c:if>
		</h3>
	</div>
	<div style="display: table">
		<%--------------------LEFT SIDE DIV -------------------------------------%>
		<div style="display: table-cell; width: 70%">
			<%-----------------TASK DETAILS ---------------------------------%>
			<div>
				<div class="mod-header">
					<h5 class="mod-header-title">
						<i class="fa fa-caret-down toggler" data-tab="detailsToggle"></i>
						<span class="mod-header-title-txt"> <i
							class="fa fa-align-left"></i> <s:message code="task.details" />
						</span>
					</h5>
				</div>
				<table id="detailsToggle">
					<tr>
						<td style="width: 80px;"><s:message code="task.state" /></td>
						<td class="left-margin"><c:choose>
								<c:when test="${can_edit && user.isPowerUser || is_assignee}">
									<div class="dropdown pointer">
										<%
											pageContext.setAttribute("states", TaskState.values());
										%>
										<div id="task_state" class="image-combo a-tooltip"
											data-toggle="dropdown" data-placement="top"
											title="<s:message code="main.click"/>">
											<div id="current_state"
												style="float: left; padding-right: 5px;">
												<t:state state="${task.state}" />
											</div>
											<span class="caret"></span>
										</div>
										<ul class="dropdown-menu" role="menu"
											aria-labelledby="dropdownMenu2">
											<c:forEach items="${states}" var="enum_state">
												<li><a href="#" class="change_state"
													data-state="${enum_state}"> <t:state
															state="${enum_state}" />
												</a></li>
											</c:forEach>
										</ul>
									</div>
								</c:when>
								<c:otherwise>
									<t:state state="${task.state}" />
								</c:otherwise>
							</c:choose></td>
					</tr>
					<tr>
						<td><s:message code="task.priority" /></td>
						<td class="left-margin"><c:choose>
								<c:when test="${can_edit && user.isPowerUser || is_assignee}">
									<div class="dropdown pointer">
										<%
											pageContext.setAttribute("priorities",
															TaskPriority.values());
										%>
										<div id="task_priority" class="image-combo a-tooltip"
											data-toggle="dropdown" data-placement=top
											title="<s:message code="main.click"/>">
											<t:priority priority="${task.priority}" />
											<span class="caret"></span>
										</div>
										<ul class="dropdown-menu" role="menu"
											aria-labelledby="dropdownMenu2">
											<c:forEach items="${priorities}" var="enum_priority">
												<li><a tabindex="-1"
													href='<c:url value="task/priority?id=${task.id}&priority=${enum_priority}"></c:url>'
													id="${enum_priority}"> <t:priority
															priority="${enum_priority}" />
												</a></li>
											</c:forEach>
										</ul>
									</div>
								</c:when>
								<c:otherwise>
									<t:priority priority="${task.priority}" />
								</c:otherwise>
							</c:choose></td>
					</tr>
					<!-------------------------	TAGS ------------------->
					<c:if test="${not task.subtask}">
					<tr>
						<td style="vertical-align: top;">Tags</td>
						<td class="left-margin">
							<c:if test="${can_edit}">
								<input id="taskTags" type="text" title="<s:message code="task.tags.add"/>"
										value=""/>
									<i id="editTags" class="fa fa-pencil" style="vertical-align: super; display: none"></i>
									<i id="searchFieldHelp" class="fa fa-cog fa-spin" style="vertical-align: super;display: none"></i>
							</c:if>
							<c:if test="${not can_edit}">
								<div id="taskTagslist" style="color: rgb(187, 186, 186);padding: 6px 6px;">
								</div>
							</c:if>
						</td>
					</tr>
					</c:if>
					<tr>
						<td style="vertical-align: top;"><s:message
								code="task.description" /></td>
						<td class="left-margin">${task.description}</td>
					</tr>
					<c:if test="${task.story_points eq 0}">
						<c:set var="points">?</c:set>
					</c:if>
					<c:if test="${task.story_points ne 0}">
						<c:set var="points">${task.story_points}</c:set>
					</c:if>
					<c:if
						test="${(not task.subtask) && (task.estimated) && not task.project.timeTracked}">
						<tr>
							<td><s:message code="task.storyPoints" /></td>
							<td class="left-margin"><span
								class="points badge theme left"><span id="point_value">${points}</span>
									<c:if test="${can_edit  && task.state ne'CLOSED'}">
										<input id="point-input" class="point-input">
										<span id="point_approve"
											style="display: none; cursor: pointer;"><i
											class="fa fa-check"></i></span>
										<span id="point_cancel"
											style="display: none; cursor: pointer;"><i
											class="fa fa-times"></i></span>
										<span id="point_edit" class="point-edit"><i
											class="fa fa-pencil points"></i></span>
									</c:if> </span></td>
						</tr>
					</c:if>
				</table>
			</div>
			<%----------------ESTIMATES DIV -------------------------%>
			<div>
				<div class="mod-header">
					<h5 class="mod-header-title">
						<i class="fa fa-caret-down toggler" data-tab="estimatesToggle"></i>
						<span class="mod-header-title-txt"> <i
							class="fa fa-lg fa-clock-o"></i> <s:message code="task.timetrack" />
						</span>
					</h5>
				</div>
				<!-- logwork trigger modal -->
				<c:if test="${can_edit && user.isPowerUser || is_assignee}">
					<button class="btn btn-default btn-sm worklog" data-toggle="modal"
						data-target="#logWorkform" data-taskID="${task.id}">
						<i class="fa fa-lg fa-calendar"></i>
						<s:message code="task.logWork"></s:message>
					</button>
					<c:if
						test="${not empty user.active_task && user.active_task[0] eq task.id}">
						<a href="#">
							<button class="btn btn-default btn-sm a-tooltip handleTimerBtn"
								title="<s:message code="task.stopTime.description" />">
								<i class="fa fa-lg fa-clock-o"></i>
								<s:message code="task.stopTime"></s:message>
							</button>
						</a>
						<div class="bar_td">
							<s:message code="task.currentTime" />
							: <span class="timer"></span>
						</div>
					</c:if>
					<c:if
						test="${empty user.active_task || user.active_task[0] ne task.id}">
						<a href="<c:url value="/task/time?id=${task.id}&action=start"/>">
							<button class="btn btn-default btn-sm">
								<i class="fa fa-lg fa-clock-o"></i>
								<s:message code="task.startTime"></s:message>
							</button>
						</a>
					</c:if>
				</c:if>
				<%--ESTIMATES TAB	--%>
				<%-- Default values --%>
				<c:set var="estimate_value">100</c:set>
				<c:set var="loggedWork">${task.percentage_logged}</c:set>
				<c:set var="remaining_width">100</c:set>
				<c:set var="remaining_bar">${task.percentage_left}</c:set>
				<%-- Check if it's not lower than estimate --%>
				<c:if test="${task.lowerThanEstimate eq 'true'}">
					<c:set var="remaining_width">${task.percentage_logged + task.percentage_left}</c:set>
					<c:set var="loggedWork">${(task.percentage_logged / (task.percentage_logged + task.percentage_left))*100}</c:set>
					<c:set var="remaining_bar">${(task.percentage_left / (task.percentage_logged + task.percentage_left))*100}</c:set>
				</c:if>
				<%-- logged work is greater than 100% and remaning time is greater than 0 --%>
				<c:if
					test="${task.percentage_logged gt 100 && task.remaining ne '0m' }">
					<c:set var="estimate_width">${task.moreThanEstimate}</c:set>
					<c:set var="remaining_bar">${task.overCommited}</c:set>
					<c:set var="loggedWork">${100-task.overCommited}</c:set>
				</c:if>
				<c:if
					test="${task.percentage_logged + task.percentage_left gt 100 && task.remaining ne '0m' }">
					<c:set var="estimate_width">${task.moreThanEstimate}</c:set>
					<c:set var="remaining_bar">${task.overCommited}</c:set>
					<c:set var="loggedWork">${100-task.overCommited}</c:set>
				</c:if>
				<c:if test="${task.percentage_left gt 100}">
					<c:set var="estimate_width">${task.moreThanEstimate}</c:set>
					<c:set var="remaining_bar">${task.overCommited}</c:set>
					<c:set var="loggedWork">${100-task.overCommited}</c:set>
				</c:if>
				<%-- There was more logged but remaining is 0 --%>
				<c:if
					test="${task.percentage_logged gt 100 && task.remaining eq '0m' }">
					<c:set var="estimate_width">${task.moreThanEstimate}</c:set>
				</c:if>
				<%-- Task without estimate but with remaining time --%>
				<c:if test="${task.estimate eq '0m' && task.remaining ne '0m'}">
					<c:set var="remaining_bar">	${100-task.percentage_logged}</c:set>
				</c:if>
				<table id="estimatesToggle" style="width: 400px">
					<tr>
						<td></td>
						<td style="width: 150px"></td>
						<td></td>
					</tr>
					<%-- Estimate bar --%>
					<c:if test="${task.estimate ne '0m'}">
						<tr>
							<td class="bar_td" style="width: 50px"><s:message
									code="task.estimate" /></td>
							<td class="bar_td"><div class="progress"
									style="width: ${estimate_width}%">
									<div class="progress-bar" role="progressbar"
										aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"
										style="width: 100%;"></div>
								</div></td>
							<td class="bar_td">${task.estimate}</td>
						</tr>
					</c:if>
					<%-- Logged work bar --%>
					<tr>
						<td class="bar_td"><s:message code="task.logged" /></td>
						<td class="bar_td"><div class="progress"
								style="width:${remaining_width}%">
								<c:set var="logged_class">progress-bar-warning</c:set>
								<c:if test="${task.percentage_logged gt 100}">
									<c:set var="logged_class">progress-bar-danger</c:set>
								</c:if>
								<div class="progress-bar ${logged_class}" role="progressbar"
									aria-valuenow="${loggedWork}" aria-valuemin="0"
									aria-valuemax="100" style="width:${loggedWork}%"></div>
							</div></td>
						<td class="bar_td">${task.loggedWork}</td>
					</tr>
					<%-- Remaining work bar --%>
					<tr>
						<td class="bar_td"><s:message code="task.remaining" /></td>
						<td class="bar_td"><div class="progress"
								style="width:${remaining_width}%">
								<div class="progress-bar progress-bar-success"
									role="progressbar" aria-valuenow="${remaining_bar}"
									aria-valuemin="0" aria-valuemax="100"
									style="width:${remaining_bar}% ; float:right"></div>
							</div></td>
						<td class="bar_td">${task.remaining }</td>
					</tr>
					<%-- 					</c:if> --%>
				</table>
			</div>
			<%-------------- RELATED TASKS ------------------%>
			<c:if test="${not task.subtask}">
				<div>
					<div class="mod-header">
						<h5 class="mod-header-title">
							<i class="fa fa-caret-down toggler" data-tab="linksToggle"></i> <span
								class="mod-header-title-txt"> <i
								class="fa fa-lg fa-link fa-flip-horizontal"></i> <s:message
									code="task.related" />
							</span>
						</h5>
						<a class="btn btn-default btn-xxs a-tooltip pull-right linkButton" style="min-width: 37px;"
							href="#" title="" data-placement="top"
							data-original-title="<s:message code="task.link"/>"> <i
							class="fa fa-plus"></i><i
							class="fa fa-lg fa-link fa-flip-horizontal"></i>
						</a>
					</div>
					<div id="linkDiv" style="display: none" class="form-group">
						<form id="linkTask" name="mainForm" method="post"
							action="<c:url value="/task/link"/>">
							<div class="form-group col-md-4">
								<select id="link" name="link" class="form-control input-sm">
									<%
										pageContext.setAttribute("linkTypes", TaskLinkType.values());
									%>
									<c:forEach items="${linkTypes}" var="linkType">
										<option value="${linkType}"><s:message
												code="${linkType.code}" /></option>
									</c:forEach>
								</select>
							</div>
							<div class="form-group col-md-6">
								<input class="form-control input-sm" id="task_link"
									placeholder="<s:message code="task.link.task.help"/>">
								<div id="linkLoader" style="display: none">
									<i class="fa fa-cog fa-spin"></i>
									<s:message code="main.loading" />
									<br>
								</div>
							</div>
							<input type="hidden" name="taskA" value="${task.id}"> <input
								type="hidden" id="taskB" name="taskB">
							<div class="form-group col-md-4" style="padding-left: 10px">
								<button type="submit" class="btn btn-default a-tooltip btn-sm"
									title="" data-placement="top"
									data-original-title="<s:message code="task.link.help" arguments="${task.id}"/>">
									<i class="fa fa-link fa-flip-horizontal"></i>
									<s:message code="task.link" />
								</button>
								<a id="linkCancel" class="btn btn-sm linkButton"> <s:message
										code="main.cancel" />
								</a>
							</div>
						</form>
					</div>
					<div id="linksToggle"
						style="max-height: 300px; overflow-y: auto; padding-left: 15px">
						<div style="display: table; width: 100%">
							<c:forEach var="linkType" items="${links}">
								<div style="display: table-row">
									<div style="display: table-cell">
										<s:message code="${linkType.key.code}" />
									</div>
									<div style="display: table-cell; padding-left: 20px">
										<table class="table table-hover table-condensed button-table">
											<c:forEach var="linkTask" items="${linkType.value}">
												<tr>
													<td style="width: 30px"><t:type
															type="${linkTask.type}" list="true" /></td>
													<td style="width: 30px"><t:priority
															priority="${linkTask.priority}" list="true" /></td>
													<td><a href="<c:url value="task?id=${linkTask.id}"/>"
														style="color: inherit;
												<c:if test="${linkTask.state eq 'CLOSED' }">text-decoration: line-through;</c:if>">
															[${linkTask.id}] ${linkTask.name}</a></td>
													<c:if test="${can_edit && user.isPowerUser || is_assignee}">
														<td style="width: 30px">
															<div class="buttons_panel pull-right">
																<a
																	href='<c:url value="/task/deletelink?taskA=${task.id}&taskB=${linkTask.id}&link=${linkType.key}"/>'>
																	<i class="fa fa-trash-o" style="color: gray"></i>
																</a>
															</div>
														</td>
													</c:if>
												</tr>

											</c:forEach>
										</table>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
				<%---------------------SUBTASKS -------------%>
				<div>
					<div class="mod-header">
						<h5 class="mod-header-title">
							<i class="fa fa-caret-down toggler" data-tab="subtasksToggle"></i>
							<span class="mod-header-title-txt"> <i
								class="fa fa-lg fa-sitemap"></i> <s:message
									code="tasks.subtasks" />
							</span>
						</h5>
						<a class="btn btn-default btn-xxs a-tooltip pull-right" style="min-width: 37px;"
							href="<c:url value="task/${task.id}/subtask"/>"
							data-placement="top"
							data-original-title="<s:message code="task.subtasks.add"/>">
							<i class="fa fa-plus"></i> <i class="fa fa-lg fa-sitemap"></i>
						</a>
					</div>
					<div id="subTask" class="form-group togglerContent"
						style="padding-left: 15px;">
						<table id="subtasksToggle"
							class="table table-hover table-condensed button-table">
							<c:forEach var="subTask" items="${subtasks}">
								<tr
									class="<c:if test="${subTask.state eq 'CLOSED' }">closed</c:if>">
									<td style="width: 30px"><t:type type="${subTask.type}"
											list="true" /></td>
									<td style="width: 30px"><t:priority
											priority="${subTask.priority}" list="true" /></td>
									<td><a
										style="color: inherit;<c:if test="${subTask.state eq 'CLOSED' }">text-decoration: line-through;</c:if>"
										href="<c:url value="subtask?id=${subTask.id}"/>">[${subTask.id}]
											${subTask.name}</a></td>
									<td style="width: 100px"><t:state state="${subTask.state}" /></td>
									<td style="width: 50px; padding-top: 14px;">
										<div class="progress" style="height: 5px;">
											<c:set var="logged_class"></c:set>
											<c:set var="percentage">${subTask.percentage_logged}</c:set>
											<c:if test="${subTask.state eq 'TO_DO'}">
												<c:set var="percentage">0</c:set>
											</c:if>
											<c:if test="${subTask.state eq 'CLOSED'}">
												<c:set var="logged_class">progress-bar-success</c:set>
												<c:set var="percentage">100</c:set>
											</c:if>
											<c:if
												test="${subTask.state eq 'BLOCKED' || subTask.percentage_logged gt 100}">
												<c:set var="logged_class">progress-bar-danger</c:set>
											</c:if>
											<div class="progress-bar ${logged_class} a-tooltip"
												title="${percentage}%" role="progressbar"
												aria-valuenow="${percentage}" aria-valuemin="0"
												aria-valuemax="100" style="width:${percentage}%"></div>
										</div>
									</td>
								</tr>
							</c:forEach>
						</table>
					</div>
				</div>
			</c:if>
			<%---------------------FILES -------------%>
			<c:if test="${fn:length(files) gt 0}">
				<div>
					<div class="mod-header">
						<h5 class="mod-header-title">
							<i class="fa fa-caret-down toggler" data-tab="filesToggle"></i> <span
								class="mod-header-title-txt"> <i
								class="fa fa-lg fa-files-o"></i> <s:message code="task.files" />
							</span>
						</h5>
						<a class="btn btn-default btn-xxs a-tooltip pull-right addFileButton" href="#" data-toggle="modal"
						data-target="#files_task" data-taskID="${task.id}">
							<i class="fa fa-plus"></i> <i class="fa fa-lg fa-file"></i>
						</a>
					</div>
					<div>
						<table id="filesToggle"
							class="table table-hover table-condensed button-table">
							<c:forEach items="${files}" var="file">
								<c:choose>
									<c:when
										test="${fn:endsWith(file,'pptx') || fn:endsWith(file,'ppt') || fn:endsWith(file,'pps')}">
										<c:set var="file_type">fa-file-powerpoint-o</c:set>
									</c:when>
									<c:when
										test="${fn:endsWith(file,'doc') || fn:endsWith(file,'docx') || fn:endsWith(file,'rtf') || fn:endsWith(file,'txt') || fn:endsWith(file,'odt')}">
										<c:set var="file_type">fa-file-word-o</c:set>
									</c:when>
									<c:when
										test="${fn:endsWith(file,'xls') || fn:endsWith(file,'xlsx') || fn:endsWith(file,'ods') || fn:endsWith(file,'csv')}">
										<c:set var="file_type">fa-file-excel-o</c:set>
									</c:when>
									<c:when
										test="${fn:endsWith(file,'jpg') || fn:endsWith(file,'png') || fn:endsWith(file,'gif')}">
										<c:set var="file_type">fa-file-image-o</c:set>
									</c:when>

									<c:otherwise>
										<c:set var="file_type">fa-file-o</c:set>
									</c:otherwise>
								</c:choose>
								<tr>
									<td><i class="fa ${file_type}"></i>&nbsp;
									<c:if test="${file_type eq 'fa-file-image-o'}">
										<img data-src="holder.js/50x50"
											style="height: 50px;" 
											src="<c:url value="task/${task.id}/imgfile?get=${file}"/>" />
									</c:if>
									<a href="<c:url value="task/${task.id}/file?get=${file}"></c:url>">${file}</a>
									</td>
									<c:if test="${can_edit && user.isPowerUser || is_assignee}">
										<td style="width: 30px">
											<div class="buttons_panel pull-right">
												<a
													href='<c:url value="task/removeFile?id=${task.id}&file=${file}"/>'>
													<i class="fa fa-trash-o" style="color: gray"></i>
												</a>
											</div>
										</td>
									</c:if>
								</tr>
							</c:forEach>
						</table>
					</div>
				</div>
			</c:if>
		</div>
		<%--------------------RIGHT SIDE DIV -------------------------------------%>
		<div class="left-margin" style="display: table-cell; width: 400px">
			<%-------------------------PEOPLE ----------------------------------%>
			<div>
				<div class="mod-header">
					<h5 class="mod-header-title">
						<i class="fa fa-user"></i>
						<s:message code="task.people" />
					</h5>
				</div>
				<div>
					<div style="display: table">
						<div style="display: table-cell; min-width: 100px">
							<s:message code="task.owner" />
							:
						</div>
						<div style="display: table-cell">
							<img data-src="holder.js/20x20"
								style="height: 20px; padding-right: 5px;"
								src="<c:url value="/../avatar/${task.owner.id}.png"/>" /><a
								href="<c:url value="/user?id=${task.owner.id}"/>">${task.owner}</a>
						</div>
					</div>
					<div id="assign_button_div" style="display: table">
						<div style="display: table-cell; min-width: 100px">
							<s:message code="task.assignee" />
							:
						</div>
						<div style="display: table-cell;">
							<c:if test="${empty task.assignee}">
								<i><s:message code="task.unassigned" /></i>
							</c:if>
							<c:if test="${not empty task.assignee}">
								<img data-src="holder.js/20x20" style="height: 20px;"
									src="<c:url value="/../avatar/${task.assignee.id}.png"/>" />
								<a href="<c:url value="/user?id=${task.assignee.id}"/>">${task.assignee}</a>
							</c:if>
							<c:if test="${user.isUser}">
								<span class="btn btn-default btn-sm a-tooltip assignToTask"
									title="<s:message code="task.assign"/>" data-toggle="modal"
									data-target="#assign_modal" data-taskID="${task.id}"
									data-assignee="${task.assignee}"
									data-assigneeID="${task.assignee.id}"
									data-projectID="${task.project.projectId}"> <i
									class="fa fa-lg fa-user-plus"></i>
								</span>
							</c:if>
						</div>
					</div>
				</div>
			</div>
			<%----------------------DATES ----------------------------------------%>
			<div>
				<div class="mod-header">
					<h5 class="mod-header-title">
						<i class="fa fa-calendar"></i>
						<s:message code="task.dates" />
					</h5>
				</div>
				<table>
					<tr>
						<td><s:message code="task.created" /></td>
						<td class="left-margin">: ${task.create_date}</td>
					</tr>
					<tr>
						<td><s:message code="task.lastUpdate" /></td>
						<td class="left-margin">: ${task.lastUpdate}</td>
					</tr>
					<c:if test="${not empty task.due_date}">
						<tr>
							<td><s:message code="task.due" /></td>
							<td class="left-margin">: ${task.due_date}</td>
						</tr>
					</c:if>
				</table>
			</div>
			<%----------------SPRITNS/RELEASES ----------------------%>
			<c:set var="hidden">none</c:set>
			<c:if test="${task.project.agile eq 'KANBAN' && not empty task.release }">
				<c:set var="hidden">block</c:set>
			</c:if>
			<c:if test="${not task.subtask}">
				<div id="sprint_release" style="display: ${hidden};">
					<c:if test="${task.project.agile eq 'KANBAN'}">
						<div class="mod-header">
							<h5 class="mod-header-title">
								<s:message code="agile.release" />
							</h5>
						</div>
						<div>
							<a
								href="<c:url value="/${task.project.projectId}/${fn:toLowerCase(task.project.agile)}/reports?release=${task.release.release}"/>">
								${task.release.release}</a>
						</div>
					</c:if>
					<c:if test="${task.project.agile eq 'SCRUM'}">
						<div class="mod-header">
							<h5 class="mod-header-title">
								<s:message code="task.sprints" />
							</h5>
						</div>
						<div id="sprints"></div>
					</c:if>
				</div>
			</c:if>
		</div>
	</div>
	<%--------------------------- BOTTOM TABS------------------------------------%>
	<div>
		<hr>
		<ul class="nav nav-tabs">
			<li><a id="worklogs" style="color: black" href="#logWork"
				data-toggle="tab"><i class="fa fa-newspaper-o"></i> <s:message
						code="task.activeLog" /></a></li>
			<li class="active"><a style="color: black" href="#comments"
				data-toggle="tab"><i class="fa fa-comments"></i> <s:message
						code="comment.comments" /></a></li>
		</ul>
		<div id="myTabContent" class="tab-content">
			<%--------------------------------- Comments -----------------------------%>
			<div id="comments" class="tab-pane fade in active">
				<table class="table table-hover button-table">
					<c:forEach items="${comments}" var="comment">
						<tr id="c${comment.id}">
							<td>
								<div>
									<img data-src="holder.js/30x30"
										style="height: 30px; float: left; padding-right: 10px;"
										src="<c:url value="/../avatar/${comment.author.id}.png"/>" />
									<a href="<c:url value="/user?id=${comment.author.id}"/>">${comment.author}</a>
									<div class="time-div">${comment.date}</div>
								</div> <%-- Comment buttons --%>
								<div class="buttons_panel" style="float: right">
									<a href="<c:url value="/task?id=${task.id}#c${comment.id}"/>"
										title="<s:message code="comment.link" text="Link to this comment" />"
										style="color: gray"><i class="fa fa-link"></i></a>
									<c:if test="${user == comment.author }">
										<c:if test="${not empty comment.message}">
											<a href="#" class="comments_edit" data-toggle="modal"
												data-target="#commentModal"
												data-message="${comment.message}"
												data-comment_id="${comment.id}"><i class="fa fa-pencil"
												style="color: gray"></i></a>
											<a
												href='<c:url value="/task/${task.id}/comment/delete?id=${comment.id}"/>'><i
												class="fa fa-trash-o" style="color: gray"></i></a>
										</c:if>
									</c:if>
								</div>
								<div>
									<c:choose>
										<c:when test="${empty comment.message}">
											<span style="color: gray; font-size: smaller"><s:message
													code="comment.deleted" text="Comment deleted" /></span>
										</c:when>
										<c:otherwise>
						${comment.message}
					</c:otherwise>
									</c:choose>
								</div> <c:if test="${not empty comment.date_edited}">
									<span style="color: gray; font-size: smaller;"><s:message
											code="comment.lastedited" text="Comment last edited" />
										${comment.date_edited}</span>
								</c:if>
								<div></div>
							</td>
						</tr>
					</c:forEach>
				</table>
				<%-- End of comments, comment addition div display --%>
				<div id="comments_div" style="display: none">
					<form id="commentForm" name="commentForm" method="post"
						action="<c:url value="/task/comment"/>">
						<input type="hidden" name="task_id" value="${task.id}">
						<textarea class="form-control" rows="3" name="message"
							id="message" autofocus></textarea>
						<div style="margin-top: 5px">
							<button class="btn btn-default btn-sm" type="submit">
								<s:message code="main.add" text="Add" />
							</button>
							<span class="btn btn-sm" id="comments_cancel"><s:message
									code="main.cancel" text="Cancel" /></span>
						</div>
					</form>
				</div>
				<c:if test="${user.isUser}">
					<button id="comments_add" class="btn btn-default btn-sm">
						<i class="fa fa-comment"></i>&nbsp;
						<s:message code="comment.add" text="Add Comment" />
					</button>
				</c:if>
			</div>
			<%------------------ WORK LOG -------------------------%>
			<div id="logWork" class="tab-pane fade">
				<table id="taskworklogs" class="table table-condensed table-hover button-table">
				</table>
			</div>
		</div>
	</div>
</div>
<jsp:include page="../modals/logWork.jsp" />
<jsp:include page="../modals/close.jsp" />
<jsp:include page="../modals/file.jsp" />
<jsp:include page="../modals/assign.jsp" />
<c:if test="${task.subtask}">
	<jsp:include page="../modals/convert2task.jsp" />
</c:if>
<!-- Edit Comment Modal -->
<div class="modal fade" id="commentModal" tabindex="-1" role="dialog"
	aria-labelledby="role" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4>
					<s:message code="comment.edit" text="Edit comment"></s:message>
				</h4>
			</div>
			<div class="modal-body">
				<form id="mainForm" name="mainForm" method="post"
					action="<c:url value="/task/comment/edit"/>">
					<div class="form-group">
						<div class="form-group">
							<input type="hidden" name="task_id" name="task_id"
								value="${task.id}"> <input type="hidden"
								name="comment_id" id="comment_id">
							<textarea type="text" class="form-control" rows="3"
								name="message" id="message" autofocus></textarea>
						</div>
					</div>
					<div class="form-group">
						<button class="btn btn-default pull-right" type="submit">
							<i class="fa fa-pencil"></i>
							<s:message code="main.edit" text="Edit"></s:message>
						</button>
					</div>
				</form>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<%
	pageContext.setAttribute("types", LogType.values());
%>
<script>
$(document).ready(function($) {
	taskID = "${task.id}";
	updateWatchers();
	<c:if test="${not task.subtask}">
		getSprints();
	</c:if>
	
	//--------------------------------------Coments----------------------------
	function toggle_comment() {
		$('#comments_add').toggle();
		$('#comments_div').slideToggle("slow");
		$(document.body).animate({
			'scrollTop' : $('#comments_div').offset().top
		}, 2000);
	}
			
	$('#comments_scroll').click(function() {
		$(document.body).animate({
			'scrollTop' : $('#comments').offset().top
		}, 2000);
	});

	$('#comments_add').click(function() {
		toggle_comment();
	});
	
	$('#worklogs').click(function() {
		if($("#taskworklogs tr").length<1){
			getWorklogs();
		}
	});

	$('.comments_edit').click(function() {
		var message = $(this).data('message');
		var comment_id = $(this).data('comment_id');
		$(".modal-body #message").val(message);
		$(".modal-body #comment_id").val(comment_id);
	});

	$('#comments_cancel').click(function() {
		toggle_comment();
	});
	
	$("#task_link").autocomplete({
				minLength : 1,
				delay : 500,
				//define callback to format results
				source : function(request, response) {
					$("#linkLoader").show();
					var url = '<c:url value="/getTasks?taskID=${task.id}&projectID=${task.project.id}"/>';
					$.getJSON(url,request,function(result) {
							$("#linkLoader").hide();
							response($.map(result,function(item) {
								return {
									// following property gets displayed in drop down
									label : item.id+ " "+ item.name,
									value : item.id,
									}
								}));
							});
					},
					//define select handler
				select : function(event, ui) {
					if (ui.item) {
						event.preventDefault();
						$("#task_link").val(ui.item.label);
						$("#taskB").val(ui.item.value);
						//$("#task_link").submit();
						return false;
					}
				}
			});

	$("#change_state").change(function() {
		if ($(this).val() == 'CLOSED') {
			$("#zero_remaining").toggle("blind");
		} else {
			$("#zero_remaining").hide("blind");
			$("#zero_checkbox").attr('checked', false);
		}
	});
	
// 			change state
	$(".change_state").click(function() {
	  	 var state = $(this).data('state');
	  	 var newState = $(this).html();
		 var subTasks = "${task.subtasks}";
	   	 if(state == 'CLOSED'){
	   		 	 $('#modal_subtaskCount').html(subTasks);
	    		 $('#close_task').modal({
	    	            show: true,
	    	            keyboard: false,
	    	            backdrop: 'static'
	    	     });
	   	 	}
	    	else{
				$.post('<c:url value="/task/changeState"/>',{id:taskID,state:state},function(result){
					if(result.code == 'ERROR'){
						showError(result.message);
					}
					else{
						$("#current_state").html(newState);
						showSuccess(result.message);
					}
					});
		    	}
			});
			
			$(".linkButton").click(function() {
				//clean regardles what is pressed
				$("#task_link").val('');
				$("#taskB").val('');
				$("#task_link").parent().removeClass("has-error");
				$("#linkDiv").slideToggle("slow");
				
			});
			
			$("#linkTask").submit(function(e) {
			    if($("#taskB").val()==''){
			    	$("#task_link").parent().addClass("has-error");
			    	e.preventDefault();
			    }
			});
			
			$("#watch").click(function(){
				var url = '<c:url value="/task/watch"/>';
				$.post(url,{id:taskID},function(result){
					if(result.code == 'ERROR'){
						showError(result.message);
					}
					else{
						showSuccess(result.message);
						$("#watch_icon").toggleClass("fa-eye");
						$("#watch_icon").toggleClass("fa-eye-slash");
						updateWatchers();
					}
				});
			});
			
			//points
			$('.point-edit').click(function(){
				togglePoints();
				$('#point_value').focus();
				
			});
			
			$('#point_approve').click(function(){
				changePoints();
			});
			
			$('#point-input').keypress(function (e) {
				 var key = e.which;
				 if(key == 13)  // the enter key code
				  {
					changePoints();
				  }
			});
			
			$('#point_cancel').click(function(){
				togglePoints();
			});

			function togglePoints(){
				$('#point-input').val('');
				$('#point-input').toggle();
				$('#point_value').toggle();
				$('#point_approve').toggle();
				$('#point_cancel').toggle();
				$('#point_edit').toggleClass('hidden');
			}
			function changePoints(){
				var points = $('#point-input').val();
				if(isNumber(points) && points < 40){
					showWait(true);
					$.post('<c:url value="/task/changePoints"/>',{id:taskID,points:points},function(result){
						if(result.code == 'Error'){
							showError(result.message);
						}
						else{
							$("#point_value").html(points);
							showSuccess(result.message);
							showWait(false);
						}
					});
				}
				togglePoints();
			}
			function getSprints(){
				projectId = '${project.id}';
				var url ='<c:url value="/task/getSprints"/>' 
				$.get(url ,{taskID:taskID},function(result){
					$.each(result, function(key,sprint){
						var url = '<c:url value="/${task.project.projectId}/${fn:toLowerCase(task.project.agile)}/reports?sprint="/>' + sprint.sprintNo;
						var row = '<div><a href="'+url+'">Sprint ' + sprint.sprintNo+ '</a></div>';
						$("#sprints").append(row);
						$('#sprint_release').show();
			    	});	
				});
			}
			
			function getWorklogs(){
				var loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="3"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';
				var url ='<c:url value="/task/getWorklogs"/>'
				$("#taskworklogs").append(loading_indicator);
				$.get(url ,{taskID:taskID},function(result){
					$.each(result, function(key,worklog){
						$("#loading").remove();
						var account = worklog.account.name + ' ' +worklog.account.surname;
						var type = getEventTypeMsg(worklog.type);
						var message = "";
						if (worklog.message != ""){
							message = '<div><blockquote class="quote">' + worklog.message + '</blockquote></div>'
						}
						var delbtn = '';
						<security:authorize access="hasRole('ROLE_ADMIN')">
							var delurl ='<c:url value="/task/delWorklog?id="/>';
							delbtn = '<div class="buttons_panel" style="float: right;">'
									+'<a class="a-tooltip delete_btn" style="color:gray" href="' +delurl + worklog.id + '"'
									+' title = "<s:message code="task.worklog.delete"/>"'
									+' data-lang="${pageContext.response.locale}"'
									+' data-msg="<s:message code="task.delete.confirm"/>" >' 
									+'<i class="fa fa-trash-o"></i></a></div>';
						</security:authorize>
						var row = '<tr><td><div style="font-size: smaller; color: dimgray;">' + account + ' ' + type + '<div class="time-div">' + worklog.timeLogged + '</div> ' + delbtn + message 
								  + '</td></tr>';
						$("#taskworklogs").append(row);	  
						$(".a-tooltip").tooltip();
			    	});	
				});
			}
			
			
			function updateWatchers(){
				var startwatch = "<s:message code="task.watch.start" htmlEscape="false"/>";
				var stopwatch = "<s:message code="task.watch.stop" htmlEscape="false"/>";
				var current = '<s:message code="task.watching"/>';
				var url = '<c:url value="/task/watchersCount"/>';
				var msg;
				if($("#watch_icon").hasClass("fa-eye")){
					msg = startwatch + '<br>';
				}else{
					msg = stopwatch + '<br>'; 
				}
				
				$.get(url,{id:taskID},function(result){
					var watchers = result;
					$("#watch").attr('data-original-title',msg + current + watchers);
				});
			}
			
$(document).on("click",".delete_btn",function(e) {
		var msg = '<p style="text-align:center"><i class="fa fa-lg fa-exclamation-triangle" style="display: initial;"></i>&nbsp'
					+ $(this).data('msg') + '</p>';
		var lang = $(this).data('lang');
		bootbox.setDefaults({
				locale : lang
		});
		e.preventDefault();
		var $link = $(this);
		bootbox.confirm(msg, function(result) {
		if (result == true) {
			document.location.assign($link.attr('href'));
			}
		});
	});

	//TAGS
	<c:if test="${not task.subtask}">
	var init = true;
	var noTags = '<s:message code="task.tags.noTags" htmlEscape="false"/>';
	$('#taskTags').tagsinput(
			{
	  		maxChars: 12,
	  		maxTags: 6,
	  		trimValue: true
			}
	);
	loadTags();
// 	checkIfEmptyTags()
	
	$(".bootstrap-tagsinput").hover(
		  function() {
		    $( this ).addClass( "inputHover" );
		    $("#editTags").show();
		  }, function() {
		    $( this ).removeClass( "inputHover" );
		    $("#editTags").hide();
		  }
	);
	$('.bootstrap-tagsinput').focusin(function() {
	    $(this).addClass('focus');
	});
	$('.bootstrap-tagsinput').focusout(function() {
	    $(this).removeClass('focus');
	});

	$( "#tagsinput" ).autocomplete({
    	source: function(request, response) {
    		$("#searchFieldHelp").show();
    		var term = request.term;
    		if ( term in cache ) {
              response( cache[ term ] );
              return;
    	    }
    		var url='<c:url value="/getTags"/>';
    		$.get(url,{term:term},function(data) {
    			$("#searchFieldHelp").hide();
    			var results = [];
                $.each(data, function(i, item) {
                    var itemToAdd = {
                        value : item.name,
                        label : item.name
                    };
                    results.push(itemToAdd);
                });
                cache[ term ] = results;
                return response(results);
    		});
    	},
    	select : function(event, ui) {
			$("#tagsinput").val(ui.item.value);
    	}
    });
	
	$('#taskTags').on('beforeItemAdd', function(event) {
		if(!init){
			showWait(true);
			var url='<c:url value="/addTaskTag"/>';
			$.get(url,{name:event.item,taskID:taskID},function(data) {
				if(data == 'ERROR'){
					event.cancel = true;
					var warning = '<s:message code="task.tags.notAdded"/>';
					showWarning(warning);
				}
				showWait(false);
				checkIfEmptyTags();
			});
		}
	});
	
	$('#taskTags').on('itemRemoved', function(event) {
		showWait(true);
		var url='<c:url value="/removeTaskTag"/>';
		$.get(url,{name:event.item,taskID:taskID},function(data) {
			showWait(false);
			checkIfEmptyTags();
		});
	});
	$(document).on("click",".tagSearch",function(e) {
		var url = '<c:url value="/tasks"/>?query=' + $(this).data("name");
		window.location.href = url;
	});
	
	function loadTags(){
		var url='<c:url value="/getTaskTags"/>';
		$.get(url,{taskID:taskID},function(data) {
			var delimiter = '';
			$.each(data, function(i, item) {
				if('${can_edit}'){
					$('#taskTags').tagsinput('add', item.name);
				}else{
					var tag = '<span class="tag label label-info theme"><span class="tagSearch" data-name="'+item.name+'">'+item.name+'</span></span>'
					$('#taskTagslist').append(tag);
					$('#taskTagslist').attr('tags','true');
				}
			});
			checkIfEmptyTags();
			init = false;
		});
		
	}
	
	function checkIfEmptyTags(){
		if('${can_edit}'){
			if($("#taskTags").val()==""){
				$("#tagsinput").attr("placeholder", noTags);
			}else{
				$("#tagsinput").attr("placeholder", "");
			}
		}else{
			if(!$('#taskTagslist').attr('tags')){
				$('#taskTagslist').append(noTags);
			}			
		}
	}
	</c:if>
});
function getEventTypeMsg(type){
	switch(type){
		<c:forEach items="${types}" var="enum_type">
		case "${enum_type}":
			return '<s:message code="${enum_type.code}"/> ';
		</c:forEach>
		default:
			return 'not yet added ';
	};
};
</script>