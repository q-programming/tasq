<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="white-frame">
<!-- 	<div class="pull-left"> -->
<%-- 		<s:message code="home.hello" /> --%>
<!-- 		&nbsp; -->
<%-- 		<security:authentication property="principal" /> --%>
<!-- 	</div> -->
	<div class="row">
		<div class="primary col-md-6 col-sm-12 col-xs-12">
			<div>
				<table class="table table-hover table-condensed">
					<thead class="theme">
						<tr>
							<th colspan="4"><i class="fa fa-user"></i> <s:message
									code="home.mineAssigned" /></th>
						</tr>
					</thead>
					<c:forEach items="${myTasks}" var="task">
						<tr>
							<td style="width: 10px;"><t:type type="${task.type}"
									list="true" /></td>
							<td style="width: 10px;"><t:priority
									priority="${task.priority}" list="true" /></td>
							<td style="width: 10px;"><t:state state="${task.state}" list="true"/></td>
							<td><a href="<c:url value="/task/${task.id}"/>"
								style="color: inherit;">[${task.id}] ${task.name}</a></td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<div>
				<table class="table table-hover table-condensed">
					<thead class="theme">
						<tr>
							<th colspan="3"><i class="fa fa-list-alt"></i> <s:message
									code="home.open" /></th>
						</tr>
					</thead>
					<c:forEach items="${unassignedTasks}" var="open_task">
						<tr>
							<td style="width: 10px;"><t:type type="${open_task.type}"
									list="true" /></td>
							<td style="width: 10px;"><t:priority
									priority="${open_task.priority}" list="true" /></td>
							<td><a href="<c:url value="/task/${open_task.id}"/>"
								style="color: inherit;">[${open_task.id}] ${open_task.name}</a></td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
		<div class="secondary col-md-6 col-sm-12 col-xs-12">
			<table class="table table-condensed table" style="margin-bottom: 0px;">
				<thead class="theme">
					<tr>
						<th colspan="3"><i class="fa fa-calendar"></i> <s:message code="project.latestEvents" /></th>
					</tr>
				</thead>
			</table>

			<%------EVENTS IN USERS PROJECTS-----------%>
<!-- 			<div class="text-center"> -->
<!-- 				<ul id="eventsTable_pagination_top"></ul> -->
<!-- 			</div> -->
			<div>
				<table id="eventsTable" class="table table-condensed">
				</table>
			</div>
			<div class="text-center">
				<ul id="eventsTable_pagination_bot"></ul>
			</div>
		</div>
	</div>
</div>
<jsp:include page="../other/events.jsp" />
<script>
	$(document).ready(function($) {
		var currentPage = 0;
		fetchWorkLogData(currentPage);
	});

	
</script>