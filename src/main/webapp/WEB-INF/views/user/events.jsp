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
		</a> <a href="#"
			id="readAll" title="Mark all as read" class="deleteDialog"
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
					<th style="width: 20px;"></th>
					<th></th>
					<th style="width: 200px;">Date</th>
				</tr>
			</thead>

			<c:forEach items="${events}" var="event">
					<!-- 			read/unread -->
					<c:choose>
						<c:when test="${event.unread}">
							<tr class="eventRow unread">
						</c:when>
						<c:otherwise>
						<tr class="eventRow read">
						</c:otherwise>
					</c:choose>
					<!-- 				choose correct glyph for notification -->
					<td>
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
								<div  class="eventSummary">
							</c:when>
							<c:otherwise>
								<div  class="eventSummary">
							</c:otherwise>
						</c:choose>
						<a href="#" class="showMore" data-event="${event.id}">[${event.task}] - ${event.who}&nbsp; <s:message
							code="${event.logtype.code}" /></a>
						<blockquote class="eventMore quote">${event.message}<div
								class="pull-right buttons_panel">
								<a style="color:gray" href="<c:url value="/task?id=${event.task}"/>"><i class="fa fa-lg fa-link fa-flip-horizontal"></i></a>
								<a style="color:gray" href="#"><i class="fa fa-lg fa-trash"></i></a>
							</div>
						</blockquote>
						</div></td>
					<td style="color: darkgrey;">${event.date}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
<script>
	$(".showMore").click(function(){
		var eventID = $(this).data('event');
		var event = $(this);
		if(event.closest("tr").hasClass("unread")){
			var url = '<c:url value="/events/read"/>';
			$.post(url, {
				id : eventID
			}, function(result) {
				if (result.code == 'ERROR') {
					showError(result.message);
				} else {
					event.closest("tr").addClass("read");
					event.closest("tr").removeClass("unread");
				}
			});

		}
		$(this).nextAll(".eventMore").toggle();
	});
	
	$("#readAll").click(function(){
		var url = '<c:url value="/events/readAll"/>';
		$.post(url, {}, function(result) {
			if (result.code == 'ERROR') {
				showError(result.message);
			} else {
				$('tr.eventRow.unread').each(function(i, obj) {
					obj.classList.add("read");
					obj.classList.remove("unread");

				});
			}
		});
	});
</script>