<%@page import="com.qprogramming.tasq.task.TaskState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="white-frame" style="overflow: auto;">
	<security:authentication property="principal" var="user" />
	<c:set var="taskName_text">
		<s:message code="task.name" text="Name" />
	</c:set>
	<c:set var="taskDesc_text">
		<s:message code="task.description" />
	</c:set>
	<h3>
		<t:type type="${task.type}" />
		[${task.id}] ${task.name}
	</h3>
	<h4>
		<s:message code="task.state" />
		:
		<t:state state="${task.state}" />
	</h4>
	<hr>
	${task.description}
	<hr>
	<!-- Button trigger modal -->
	<button class="btn btn-default btn-sm" data-toggle="modal"
		data-target="#changeState">
		<span class="glyphicon glyphicon-list-alt"></span>
		<s:message code="task.changeState" />
	</button>

	<!-- Button trigger modal -->
	<button class="btn btn-default btn-sm" data-toggle="modal"
		data-target="#logWorkform">
		<span class="glyphicon glyphicon-calendar"></span>
		<s:message code="task.logWork"></s:message>
	</button>
	<c:if
		test="${not empty user.active_task && user.active_task[0] eq task.id}">
		<a href="<c:url value="/task/time?id=${task.id}&action=stop"/>">
			<button class="btn btn-default btn-sm a-tooltip"
				title="<s:message code="task.stopTime.description" />">
				<span class="glyphicon glyphicon-time"></span>
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
				<span class="glyphicon glyphicon-time"></span>
				<s:message code="task.startTime"></s:message>
			</button>
		</a>
	</c:if>
	<%--ESTIMATES TAB	--%>
	<%-- Default values --%>
	<c:set var="estimate_value">100</c:set>
	<c:set var="logged_work">${task.percentage_logged}</c:set>
	<c:set var="remaining_width">100</c:set>
	<c:set var="remaining_bar">${task.percentage_left}</c:set>
	<%-- 	<br>${logged_work} <br>${task.percentage_left} <br>${task.lowerThanEstimate eq 'true'} --%>

	<%-- Check if it's not lower than estimate --%>
	<c:if test="${task.lowerThanEstimate eq 'true'}">
		<c:set var="remaining_width">${task.percentage_logged + task.percentage_left}</c:set>
		<c:set var="logged_work">${100- task.percentage_left}</c:set>
	</c:if>
	<%-- logged work is greater than 100% and remaning time is greater than 0 --%>
	<c:if
		test="${task.percentage_logged gt 100 && task.remaining ne '0m' }">
		<c:set var="estimate_width">${task.moreThanEstimate}</c:set>
		<c:set var="remaining_bar">${task.overCommited}</c:set>
		<c:set var="logged_work">${100-task.overCommited}</c:set>
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
	<table style="width: 400px">
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
				<td class="bar_td">${task.logged_work}</td>
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
							<div class="progress-bar" role="progressbar" aria-valuenow="100"
								aria-valuemin="0" aria-valuemax="100" style="width: 100%;"></div>
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
							aria-valuenow="${logged_work}" aria-valuemin="0"
							aria-valuemax="100" style="width:${logged_work}%"></div>
					</div></td>
				<td class="bar_td">${task.logged_work}</td>
			</tr>
			<%-- Remaining work bar --%>
			<tr>
				<td class="bar_td"><s:message code="task.remaining" /></td>
				<td class="bar_td"><div class="progress"
						style="width:${remaining_width}%">
						<div class="progress-bar progress-bar-success" role="progressbar"
							aria-valuenow="${remaining_bar}" aria-valuemin="0"
							aria-valuemax="100" style="width:${remaining_bar}% ; float:right"></div>
					</div></td>
				<td class="bar_td">${task.remaining }</td>
			</tr>
		</c:if>
	</table>
	<div>
		<%--------------------------- BOTTOM TABS------------------------------------%>
		<hr>
		<ul class="nav nav-tabs">
			<li><a style="color: black" href="#logWork" data-toggle="tab"><span
					class="glyphicon glyphicon-list-alt"></span> <s:message
						code="task.activeLog" /></a></li>
			<li class="active"><a style="color: black" href="#comments"
				data-toggle="tab"><span class="glyphicon glyphicon-comment"></span>
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
										href="<c:url value="/user/details?id=${comment.author.id}"/>">${comment.author}</a>
									<span style="color: gray; font-size: smaller; float: right;">${comment.date}</span>
								</div> <%-- Comment buttons --%>
								<div class="buttons_panel" style="float: right">
									<a href="<c:url value="/task?id=${task.id}#c${comment.id}"/>"
										title="<s:message code="comment.link" text="Link to this comment" />"
										style="color: gray"><span class="glyphicon glyphicon-link"></span></a>
									<c:if test="${user == comment.author }">
										<c:if test="${not empty comment.message}">
											<a href="#" class="comments_edit" data-toggle="modal"
												data-target="#commentModal"
												data-message="${comment.message}"
												data-comment_id="${comment.id}"><span
												class="glyphicon glyphicon-pencil" style="color: gray"></span></a>
											<a
												href='<c:url value="/task/${task.id}/comment/delete?id=${comment.id}"/>'><span
												class="glyphicon glyphicon-trash" style="color: gray"></span></a>
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
						<textarea type="text" class="form-control" rows="3" name="message"
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
				<button id="comments_add" class="btn btn-default btn-sm">
					<span class="glyphicon glyphicon-comment"></span>
					<s:message code="comment.add" text="Add Comment" />
				</button>
			</div>
			<%------------------ WORK LOG -------------------------%>
			<div id="logWork" class="tab-pane fade">
				<table class="table table-condensed table-hover">
					<c:forEach items="${task.worklog}" var="worklog">
						<tr>
							<td><div style="font-size: smaller; color: dimgray;">${worklog.account}
									<t:logType logType="${worklog.type}" />
									<div class="pull-right">${worklog.timeLogged}</div>
								</div> <c:if test="${not empty worklog.message}">
									<div>
										<blockquote
											style="margin-bottom: 0; font-size: smaller; padding: 10px;">${worklog.message}</blockquote>
									</div>
								</c:if></td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</div>
</div>
<!-- LOG WORK MODAL -->
<div class="modal fade" id="logWorkform" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">
					<s:message code="task.logWork" />
				</h4>
			</div>
			<form id="mainForm" name="mainForm" method="post"
				action="<c:url value="/logwork"/>">
				<div class="modal-body">

					<input type="hidden" name="taskID" value="${task.id}">
					<div class="form-group">
						<label><s:message code="task.logWork.spent" /></label> <input
							id="logged_work" name="logged_work"
							style="width: 150px; height: 25px" class="form-control"
							type="text" value=""> <span class="help-block"><s:message
								code="task.logWork.help"></s:message> </span>
					</div>
					<div>
						<div style="float: left; margin-right: 50px;">
							<label><s:message code="main.date" /></label> <input
								id="datepicker" name="date_logged"
								style="width: 150px; height: 25px"
								class="form-control datepicker" type="text" value="">
						</div>
						<div>
							<label><s:message code="main.time" /></label> <input
								id="time_logged" name="time_logged"
								style="width: 60px; height: 25px" class="form-control"
								type="text" value="">
						</div>
					</div>
					<span class="help-block"><s:message
							code="task.logWork.when.help"></s:message> </span>
					<div>
						<label><s:message code="task.remaining" /></label>
						<div class="radio">
							<label> <input type="radio" name="estimate_reduce"
								id="estimate_auto" value="auto" checked> <s:message
									code="task.logWork.reduceAuto" />
							</label>
						</div>
						<div class="radio">
							<label> <input type="radio" name="estimate_reduce"
								id="estimate_manual" value="auto"> <s:message
									code="task.logWork.reduceManual" />
							</label> <input id="remaining" name="remaining" class="form-control"
								style="width: 150px; height: 25px" disabled>
						</div>
					</div>
					<span class="help-block"><s:message
							code="task.logWork.estimate.help"></s:message> </span>
				</div>
				<div class="modal-footer">
					<button class="btn btn-default" type="submit">
						<s:message code="main.log" />
					</button>
					<a class="btn" data-dismiss="modal"><s:message
							code="main.cancel" /></a>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- CHANGE STATE MODAL -->
<%
	pageContext.setAttribute("states", TaskState.values());
%>
<div class="modal fade" id="changeState" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">
					<s:message code="task.changeState" />
				</h4>
			</div>
			<form id="mainForm" name="mainForm" method="post"
				action="<c:url value="/task/state"/>">
				<div class="modal-body">
					<input type="hidden" name="taskID" value="${task.id}">
					<div class="form-group">
						<label>Change state</label> <select name="state"
							class="form-control">
							<c:forEach items="${states}" var="state">
								<option value="${state}">${state.description}</option>
							</c:forEach>
						</select> <span class="help-block">Choose new task state</span>
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn btn-default" type="submit">
						<s:message code="main.change" />
					</button>
					<a class="btn" data-dismiss="modal"><s:message
							code="main.cancel" /></a>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- Edit Comment Modal -->
<div class="modal fade" id="commentModal" tabindex="-1" role="dialog"
	aria-labelledby="role" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
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
							<span class="glyphicon glyphicon-pencil"></span>
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
		//------------------------------------Datepickers
		$(".datepicker").datepicker({
			maxDate : '0'
		});
		$(".datepicker").datepicker("option", "dateFormat", "dd-mm-yy");
		$(".datepicker").change(function() {
			var date = new Date;
			var minutes = date.getMinutes();
			var hour = date.getHours();
			$("#time_logged").val(hour + ":" + minutes);
		});
		$("#time_logged").mask("Z0:A0", {
			translation : {
				'Z' : {
					pattern : /[0-2]/
				},
				'A' : {
					pattern : /[0-5]/
				}
			},
			placeholder : "__:__"
		});
		$("#estimate_manual").change(function() {
			$('#remaining').attr("disabled", !this.checked);
		});
		$("#estimate_auto").change(function() {
			$('#remaining').val("");
			$('#remaining').attr("disabled", this.checked);
		});
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
	});

	
</script>