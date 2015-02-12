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
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<security:authentication property="principal" var="user" />
<c:if
	test="${(myfn:contains(task.project.administrators,user) || is_admin || task.owner.id == user.id) && task.state ne'CLOSED'}">
	<c:set var="can_edit" value="true" />
</c:if>
<c:if test="${task.assignee.id == user.id}">
	<c:set var="is_assignee" value="true" />
</c:if>
<div class="white-frame" style="overflow: auto;">
	<c:set var="taskName_text">
		<s:message code="task.name" text="Name" />
	</c:set>
	<c:set var="taskDesc_text">
		<s:message code="task.description" />
	</c:set>
	<%----------------------TASK NAME-----------------------------%>
	<div>
		<div class="pull-right">
			<c:if test="${can_edit}">
				<a href="<c:url value="task/edit?id=${task.id}"/>"><button
						class="btn btn-default btn-sm">
						<i class="fa fa-pencil"></i>
						<s:message code="task.edit" />
					</button></a>
			</c:if>
			<c:if test="${can_edit && user.isUser}">
				<a class="btn btn-default btn-sm a-tooltip delete_task"
					href="<c:url value="task/delete?id=${task.id}"/>"
					title="<s:message code="task.delete" text="Delete task" />"
					data-lang="${pageContext.response.locale}"
					data-msg='<s:message code="task.delete.confirm"></s:message>'>
					<i class="fa fa-trash-o"></i>
				</a>
			</c:if>
		</div>
		<h3>
			<t:type type="${task.type}" />
			<c:if test="${task.subtask}">
			<a href='<c:url value="/task?id=${task.parent}"/>'>[${task.parent}]</a>
			\ [${task.id}] ${task.name}
			</c:if>
			<c:if test="${not task.subtask}">
			<a href='<c:url value="/project?id=${task.project.id}"/>'>${task.project.projectId}</a>
			\ [${task.id}] ${task.name}
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
						<a class="toggler" href="#"><i class="fa fa-caret-down"></i></a>
						<span class="mod-header-title-txt">
							<i class="fa fa-align-left"></i>
							<s:message code="task.details" />
						</span>
					</h5>
				</div>
				<table class="togglerContent">
					<tr>
						<td style="width: 80px;"><s:message code="task.state" /></td>
						<td class="left-margin">
							<c:choose>
								<c:when	test="${can_edit && user.isUser || is_assignee}">
									<div class="dropdown pointer">
										<%
											pageContext.setAttribute("states",
															TaskState.values());
										%>
										<div id="task_state" class="image-combo a-tooltip"
											data-toggle="dropdown" data-placement="top"
											title="<s:message code="main.click"/>">
											<div id="current_state" style = "float: left;padding-right: 5px;">
												<t:state state="${task.state}" />
											</div>
											<span class="caret"></span>
										</div>
										<ul class="dropdown-menu" role="menu"
											aria-labelledby="dropdownMenu2">
											<c:forEach items="${states}" var="enum_state">
												<li>
													<a href="#" class="change_state" data-state="${enum_state}">
														<t:state state="${enum_state}" />
													</a>
												</li>
											</c:forEach>
										</ul>
									</div>
								</c:when>
								<c:otherwise>
									<t:state state="${task.state}" />
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td><s:message code="task.priority" /></td>
						<td class="left-margin"><c:choose>
								<c:when test="${can_edit && user.isUser || is_assignee}">
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

					<tr>
						<td style="vertical-align: top;"><s:message
								code="task.description" /></td>
						<td class="left-margin">${task.description}</td>
					</tr>
					<c:if test="${not task.subtask}">
						<tr>
							<td><s:message code="task.storyPoints" /></td>
							<td class="left-margin">
								<span class="badge theme left">${task.story_points}</span>
							</td>
						</tr>
					</c:if>
				</table>
			</div>
			<%----------------ESTIMATES DIV -------------------------%>
			<div>
				<div class="mod-header">
					<h5 class="mod-header-title">
						<a class="toggler" href="#"><i class="fa fa-caret-down"></i></a>
						<span class="mod-header-title-txt">
							<i class="fa fa-lg fa-clock-o"></i>
							<s:message code="task.timetrack" />
						</span>
					</h5>
				</div>
				<!-- logwork trigger modal -->
				<c:if test="${can_edit && user.isUser || is_assignee}">
					<button class="btn btn-default btn-sm worklog" data-toggle="modal"
						data-target="#logWorkform" data-taskID="${task.id}">
						<i class="fa fa-lg fa-calendar"></i>
						<s:message code="task.logWork"></s:message>
					</button>
					<c:if
						test="${not empty user.active_task && user.active_task[0] eq task.id}">
						<a href="<c:url value="/task/time?id=${task.id}&action=stop"/>">
							<button class="btn btn-default btn-sm a-tooltip"
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
				<%-- 	<br>${loggedWork} <br>${task.percentage_left} <br>${task.lowerThanEstimate eq 'true'} --%>

				<%-- Check if it's not lower than estimate --%>
				<c:if test="${task.lowerThanEstimate eq 'true'}">
					<c:set var="remaining_width">${task.percentage_logged + task.percentage_left}</c:set>
					<c:set var="loggedWork">${100- task.percentage_left}</c:set>
				</c:if>
				<%-- logged work is greater than 100% and remaning time is greater than 0 --%>
				<c:if
					test="${task.percentage_logged gt 100 && task.remaining ne '0m' }">
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
				<table class="togglerContent" style="width: 400px">
					<tr>
						<td></td>
						<td style="width: 150px"></td>
						<td></td>
					</tr>
					<%-- TODO add display based on type! --%>
					<%-- if there weas no ESTIMATE at all --%>
					<c:if test="${not task.estimated}">
						<tr>
							<td class="bar_td"><s:message code="task.logged" /></td>
							<td class="bar_td">${task.loggedWork}</td>
							<td class="bar_td"></td>
						</tr>
					</c:if>
					<%-- IF ESTIMATE IS NOT 0 --%>
					<c:if test="${task.estimated}">
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
					</c:if>
				</table>
			</div>
			<%-------------- RELATED TASKS ------------------%>
			<c:if test="${not task.subtask}">
			<div>
				<div class="mod-header">
					<h5 class="mod-header-title">
						<a class="toggler" href="#"><i class="fa fa-caret-down"></i></a>
						<span class="mod-header-title-txt">
						<i class="fa fa-lg fa-link fa-flip-horizontal"></i>
							<s:message code="task.related"/>
						</span>
					</h5>
					<a class="btn btn-default btn-xxs a-tooltip pull-right linkButton" href="#" title="" data-placement="top" data-original-title="<s:message code="task.link"/>">
						<i class="fa fa-plus"></i><i class="fa fa-lg fa-link fa-flip-horizontal"></i>
					</a>
				</div>
				<div id="linkDiv" style="display:none" class="form-group">
					<form id="linkTask" name="mainForm" method="post" action="<c:url value="/task/link"/>">
						<div class="form-group col-md-4">
							<select id="link" name="link" class="form-control input-sm">
								<%
								pageContext.setAttribute("linkTypes",TaskLinkType.values());
								%>
								<c:forEach items="${linkTypes}" var="linkType">
									<option value="${linkType}"><s:message code="${linkType.code}"/></option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group col-md-6">
							<input class="form-control input-sm" id="task_link" placeholder="<s:message code="task.link.task.help"/>">
							<div id="linkLoader" style="display:none"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br></div>
						</div>
						<input type="hidden" name="taskA" value="${task.id}">
						<input type="hidden" id="taskB" name="taskB">
						<div class="form-group col-md-4"  style="padding-left:10px">
							<button type="submit" class="btn btn-default a-tooltip btn-sm" title="" data-placement="top" data-original-title="<s:message code="task.link.help" arguments="${task.id}"/>">
								<i class="fa fa-link fa-flip-horizontal"></i> <s:message code="task.link"/>
							</button>
							<a id="linkCancel" class="btn btn-sm linkButton">
								<s:message code="main.cancel"/>
							</a>
						</div>
					</form>
				</div>
				<div class="togglerContent" style="max-height: 300px; overflow-y:auto;padding-left:15px">
				<div style="display:table;width:100%">
				<c:forEach var="linkType" items="${links}">
					<div style="display:table-row">
						<div style="display:table-cell">
							<s:message code="${linkType.key.code}"/>
						</div>
						<div style="display:table-cell;padding-left:20px">
							<table class="table table-hover table-condensed button-table">
								<c:forEach var="linkTask" items="${linkType.value}">
									<tr>
										<td style="width: 30px"><t:type type="${linkTask.type}" list="true" /></td>
										<td style="width: 30px"><t:priority priority="${linkTask.priority}" list="true" /></td>
										<td>
											<a href="<c:url value="task?id=${linkTask.id}"/>" style="color: inherit;
												<c:if test="${linkTask.state eq 'CLOSED' }">text-decoration: line-through;</c:if>">
													[${linkTask.id}] ${linkTask.name}</a>
										</td>
										<c:if test="${can_edit && user.isUser || is_assignee}">
										<td style="width: 30px">
											<div class="buttons_panel pull-right">
												<a href='<c:url value="/task/deletelink?taskA=${task.id}&taskB=${linkTask.id}&link=${linkType.key}"/>'>
													<i class="fa fa-trash-o" style="color:gray"></i>
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
						<a class="toggler" href="#"><i class="fa fa-caret-down"></i></a>
						<span class="mod-header-title-txt">
							<i class="fa fa-lg fa-sitemap"></i>
							<s:message code="tasks.subtasks" />
						</span>
					</h5>
					<a class="btn btn-default btn-xxs a-tooltip pull-right" href="<c:url value="task/${task.id}/subtask"/>" data-placement="top" data-original-title="<s:message code="task.subtasks.add"/>">
						<i class="fa fa-plus"></i> <i class="fa fa-lg fa-sitemap"></i>
					</a>
				</div>
				<div id="subTask" class="form-group togglerContent" style="padding-left: 15px;">
					<table class="table table-hover table-condensed button-table">
						<c:forEach var="subTask" items="${subtasks}">
							<tr>
								<td style="width:30px"><t:type type="${subTask.type}" list="true" /></td>
								<td style="width: 30px"><t:priority priority="${subTask.priority}" list="true" /></td>
								<td><a style="color: inherit;" href="<c:url value="subtask?id=${subTask.id}"/>">[${subTask.id}] ${subTask.name}</a></td>
								<td style="width: 100px"><t:state state="${subTask.state}" /></td>
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
					<div>
						<s:message code="task.owner" />
						: <img data-src="holder.js/20x20"
							style="height: 20px; padding-right: 5px;"
							src="<c:url value="/userAvatar/${task.owner.id}"/>" /><a
							href="<c:url value="/user?id=${task.owner.id}"/>">${task.owner}</a>
					</div>
					<div style="display: table-cell; padding-left: 20px; display: none"
						id="assign_div">
						<div>
							<form id="assign" action="<c:url value="/task/assign"/>"
								method="post">
								<input type="hidden" name="taskID" value="${task.id}">
								<table>
									<tr style="vertical-align: top;">
										<td style="width: 250px;">
											<input type="text"
												class="form-control input-sm" name="account"
												placeholder="<s:message code="project.participant.hint"/>"
												id="assignee">
											<div id="usersLoader" style="display:none"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br></div>
										</td>
										<td>
											<button class="btn btn-default btn-sm a-tooltip"
												type="button" id="assign_me"
												title="<s:message code="task.assignme"/>">
												<i class="fa fa-lg fa-user"></i>
											</button>
										</td>
										<td>
											<button class="btn btn-default btn-sm a-tooltip"
												type="button" id="unassign"
												title="<s:message code="task.unassign"/>">
												<i class="fa fa-lg fa-user-times"></i>
											</button>
										</td>
										<td>
											<button type="button" id="dismiss_assign"
												class="close a-tooltip"
												title="<s:message code="main.cancel"/>"
												style="padding-left: 5px">×</button>
										</td>
									</tr>
								</table>
							</form>
						</div>
					</div>
					<div id="assign_button_div">
						<div style="display: table-cell;">
							<c:if test="${empty task.assignee}">
								<s:message code="task.assignee" />: <i><s:message
										code="task.unassigned" /></i>
							</c:if>
							<c:if test="${not empty task.assignee}">
								<s:message code="task.assignee" /> : <img
									data-src="holder.js/20x20"
									style="height: 20px; padding-right: 5px;"
									src="<c:url value="/userAvatar/${task.assignee.id}"/>" />
								<a href="<c:url value="/user?id=${task.assignee.id}"/>">${task.assignee}</a>
							</c:if>
						</div>
						<c:if test="${user.isUser}">
							<div style="display: table-cell; padding-left: 5px">
								<span class="btn btn-default btn-sm a-tooltip"
									id="assign_button" title="<s:message code="task.assign"/>">
									<i class="fa fa-lg fa-user-plus"></i>
								</span>
							</div>
						</c:if>
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
						<td><s:message code="task.created"></s:message></td>
						<td class="left-margin">: ${task.create_date}</td>
					</tr>
					<tr>
						<td><s:message code="task.due"></s:message></td>
						<td class="left-margin">: ${task.due_date}</td>
					</tr>
				</table>
			</div>
			<%----------------SPRITNS ----------------------%>
			<c:if test="${not task.subtask}">
			<div>
				<div class="mod-header">
					<h5 class="mod-header-title">
						<s:message code="task.sprints" />
					</h5>
				</div>
				<c:forEach items="${task.sprints}" var="sprint">
					<div>
						<a
							href="<c:url value="/${task.project.projectId}/${fn:toLowerCase(task.project.agile_type)}/reports?sprint=${sprint.sprintNo}"/>">Sprint
							${sprint.sprintNo}</a>
					</div>
				</c:forEach>
			</div>
			</c:if>
		</div>
	</div>
	<%--------------------------- BOTTOM TABS------------------------------------%>
	<div>
		<hr>
		<ul class="nav nav-tabs">
			<li><a style="color: black" href="#logWork" data-toggle="tab"><i class="fa fa-newspaper-o"></i> <s:message
						code="task.activeLog" /></a></li>
			<li class="active"><a style="color: black" href="#comments"
				data-toggle="tab"><i class="fa fa-comments"></i>
					<s:message code="comment.comments" /></a></li>
		</ul>
		<div id="myTabContent" class="tab-content">
			<%--------------------------------- Comments -----------------------------%>
			<div id="comments" class="tab-pane fade in active">
				<table class="table table-hover button-table">
					<c:forEach items="${task.comments}" var="comment">
						<tr id="c${comment.id}">
							<td>
								<div>
									<img data-src="holder.js/30x30"
										style="height: 30px; float: left; padding-right: 10px;"
										src="<c:url value="/userAvatar/${comment.author.id}"/>" /> <a
										href="<c:url value="/user?id=${comment.author.id}"/>">${comment.author}</a>
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
												data-comment_id="${comment.id}"><i class="fa fa-pencil" style="color:gray"></i></a>
											<a
												href='<c:url value="/task/${task.id}/comment/delete?id=${comment.id}"/>'><i class="fa fa-trash-o" style="color:gray"></i></a>
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
				<c:if test="${user.isReporter}">
					<button id="comments_add" class="btn btn-default btn-sm">
						<i class="fa fa-comment"></i>&nbsp;
						<s:message code="comment.add" text="Add Comment" />
					</button>
				</c:if>
			</div>
			<%------------------ WORK LOG -------------------------%>
			<div id="logWork" class="tab-pane fade">
				<table class="table table-condensed table-hover">
					<c:forEach items="${task.worklog}" var="worklog">
						<tr>
							<td><div style="font-size: smaller; color: dimgray;">${worklog.account}
									<t:logType logType="${worklog.type}" />
									<div class="time-div">${worklog.timeLogged}</div>
								</div> <c:if test="${not empty worklog.message}">
									<div>
										<blockquote class="quote">${worklog.message}</blockquote>
									</div>
								</c:if></td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</div>
</div>
<jsp:include page="../modals/logWork.jsp" />
<jsp:include page="../modals/close.jsp" />
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
<script>
$(document).ready(function($) {
	taskID = "${task.id}";
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

			$('.comments_edit').click(function() {
						var message = $(this).data('message');
						var comment_id = $(this).data('comment_id');
						$(".modal-body #message").val(message);
						$(".modal-body #comment_id").val(comment_id);
			});

			$('#comments_cancel').click(function() {
						toggle_comment();
			});
			var cache = {};
			$("#assignee").autocomplete({
						minLength : 1,
						delay : 500,
						//define callback to format results
						source : function(request, response) {
							$("#usersLoader").show();
							var term = request.term;
							if ( term in cache ) {
						          response( cache[ term ] );
						          return;
						    }
							var url='<c:url value="/project/getParticipants"/>';
							$.get(url,{id:'${task.project.id}',term:term},function(result) {
									$("#usersLoader").hide();
									cache[ term ] = result;
									response($.map(result,function(item) {
										return {
											// following property gets displayed in drop down
											label : item.name+ " "+ item.surname,
											value : item.email,
											}
										}));
									});
							},
							//define select handler
						select : function(event, ui) {
							if (ui.item) {
								event.preventDefault();
								$("#assignee").val(ui.item.label);
								$("#assign").append('<input type="hidden" name="email" value=' + ui.item.value + '>');
								$("#assign").submit();
								return false;
							}
						}
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

			$("#assign_me").click(function() {
						var current_email = "${user.email}";
						$("#assign").append('<input type="hidden" name="email" value=' + current_email + '>');
						$("#assign").submit();
					});
			
			$("#unassign").click(function() {
						var current_email = "";
						$("#assign").append('<input type="hidden" name="email" value=' + current_email + '>');
						$("#assign").submit();
					});

			$("#assign_button").click(function() {
						$('#assign_div').toggle("blind");
						$('#assign_button_div').toggle("blind");
						$('#assignee').focus();
					});
			$("#dismiss_assign").click(function() {
						$('#assign_div').toggle("blind");
						$('#assign_button_div').toggle("blind");
					});
			$("#change_state").change(function() {
						if ($(this).val() == 'CLOSED') {
							$("#zero_remaining").toggle("blind");
						} else {
							$("#zero_remaining").hide("blind");
							$("#zero_checkbox").attr('checked', false);
						}
					});
			});
// 			change state
			$(".change_state").click(function() {
	    	 var state = $(this).data('state');
	    	 if(state == 'CLOSED'){
		    		 $('#close_task').modal({
		    	            show: true,
		    	            keyboard: false,
		    	            backdrop: 'static'
		    	     });
	    	 	}
		    	else{
					$.post('<c:url value="/task/changeState"/>',{id:taskID,state:state},function(result){
						if(result.code == 'Error'){
							showError(result.message);
						}
						else{
							$("#current_state").html($(this).html());
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
			
$(document).on("click",".delete_task",function(e) {
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
</script>