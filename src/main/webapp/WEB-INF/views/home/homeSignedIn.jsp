<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="white-frame" style="height: 85vh; overflow: auto">
	<div class="pull-left">
		<s:message code="home.hello"/>
		<security:authentication property="principal" />
	</div>
	<div class="pull-right">
		<span class="glyphicon glyphicon-calendar"></span> <span
			id="date_span"></span> <span class="glyphicon glyphicon-time"></span>

		<span id="time_span"></span>
	</div>
	<div style="display: table; width: 100%">
		<div style="display: table-cell; width: 500px">
			<div>
				<table class="table table-hover table-condensed">
					<thead class="theme">
						<tr>
							<th colspan="3"><span class="glyphicon glyphicon-user"></span>
								<s:message code="home.mineAssigned"/></th>
						</tr>
					</thead>
					<c:forEach items="${myTasks}" var="task">
						<tr>
							<td style="width: 10px;"><t:type type="${task.type}"
									list="true" /></td>
							<td style="width: 10px;"><t:priority
									priority="${task.priority}" list="true" /></td>
							<td><a href="<c:url value="task?id=${task.id}"/>"
								style="color: inherit;">[${task.id}] ${task.name}</a></td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<div>
				<table class="table table-hover table-condensed">
					<thead class="theme">
						<tr>
							<th colspan="3"><span class="glyphicon glyphicon-list-alt"></span>
								<s:message code="home.open"/></th>
						</tr>
					</thead>
					<c:forEach items="${unassignedTasks}" var="open_task">
						<tr>
							<td style="width: 10px;"><t:type type="${open_task.type}"
									list="true" /></td>
							<td style="width: 10px;"><t:priority
									priority="${open_task.priority}" list="true" /></td>
							<td><a href="<c:url value="task?id=${open_task.id}"/>"
								style="color: inherit;">[${open_task.id}] ${open_task.name}</a></td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
		<div style="display: table-cell; width: 500px; padding-left: 30px">
			<%------DUE TASKS -----------%>
			<c:set var="now" value="<%=new java.util.Date()%>" />
			<table class="table table-condensed table table-hover">
				<thead class="theme">
					<tr>
						<th colspan="3"><span class="glyphicon glyphicon-calendar"></span>
							<s:message code="home.due"/></th>
					</tr>
				</thead>
				<c:forEach items="${dueTasks}" var="due_task">
					<tr style="<c:if test="${due_task.rawDue_date lt now}">
									background: rgba(205, 50, 50, 0.12);
								</c:if>">
						<td style="width: 100px;">${due_task.due_date}</td>
						<td><a href="<c:url value="task?id=${due_task.id}"/>"
							style="color: inherit;">[${due_task.id}] ${due_task.name}</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>

<script>
	$(document).ready(
			function($) {
				$("#date_span").text(
						$.datepicker.formatDate('DD,d MM yy', new Date()));
				setTimeout(display, 1000);

			});
	function display() {
		var currentTime = new Date();
		var hours = currentTime.getHours();
		var minutes = currentTime.getMinutes();
		var seconds = currentTime.getSeconds();
		$("#time_span").text(hours + ":" + minutes + ":" + seconds);
		setTimeout(display, 1000);
	}

	
</script>