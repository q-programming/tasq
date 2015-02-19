<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="white-frame"
	style="width: 80%; overflow: auto; display: table">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li class="active"><a style="color: black" href="#"> <i
					class="fa fa-bell"></i> <s:message code="events.events" /></a></li>
			<li><a style="color: black" href="<c:url value="/watching" />">
					<i class="fa fa-eye"></i> <s:message code="events.watching" />
			</a></li>
		</ul>
	</div>
	<div>
		<a href="<c:url value="notification/deleteall${sort_link}"/>"
			title="Delete all" class="deleteDialog"
			data-msg="<s:message
							code="notice.deleteAll.confirm" text="Delete all?" />"">
			<button type="button" class="btn btn-default pull-right a-tooltip "
				data-toggle="tooltip" data-placement="bottom" data-container='body'
				title="<s:message
							code="notice.deleteAll" text="Delete All" />">
				<i class="fa fa-trash"></i>
			</button>
		</a> <a href="<c:url value="notification/markall${sort_link}"/>"
			title="Mark all as read" class="deleteDialog"
			data-msg="<s:message
							code="notice.markAll.confirm" text="Mark All read?" />""><button
				type="button" class="btn btn-default pull-right a-tooltip"
				data-toggle="tooltip" data-placement="bottom" data-container='body'
				title="<s:message
							code="notice.markAll" text="Mark All read" />">
				<i class="fa fa-check"></i>
			</button></a>
		<table class="table table-hover table-condensed">
			<thead class="theme">
				<tr>
					<th style="width: 55px;"></th>
					<th></th>
					<th style="width: 100px;">Date</th>
				</tr>
			</thead>

			<c:forEach items="${events}" var="event">
				<tr>
					<!-- 			read/unread -->
					<c:choose>
						<c:when test="${event.unread}">
							<td style="color: black;">
						</c:when>
						<c:otherwise>
							<td style="color: lightgray;">
						</c:otherwise>
					</c:choose>
					<!-- 				choose correct glyph for notification -->
					<c:choose>
						<c:when test="${event.type eq 'COMMENT'}">
							<i class="fa fa-comment"></i>
						</c:when>
						<c:when test="${event.type eq 'WATCH'}">
							<i class="fa fa-eye"></i>
						</c:when>
						<c:when test="${event.type eq 'SYSTEM'}">
							<i class="fa fa-eye"></i>
						</c:when>
					</c:choose>
					</td>
					<td><c:choose>
							<c:when test="${event.unread}">
								<div style="font-weight: bold;">
							</c:when>
							<c:otherwise>
								<div>
							</c:otherwise>
						</c:choose> [${event.task}] ${event.logtype} ${event.message}
						</div></td>
					<td style="color: darkgrey;">${event.date}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>