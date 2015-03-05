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
		<a href="#" title="Delete all" id="deleteAll"
			data-msg="<s:message code="events.deleteAll.confirm" text="Delete All" />">
			<button type="button" class="btn btn-default pull-right a-tooltip "
				data-toggle="tooltip" data-placement="bottom" data-container='body'
				title="<s:message code="events.deleteAll"/>">
				<i class="fa fa-trash"></i>
			</button>
		</a> <a href="#" id="readAll" title="Mark all as read"
			class="deleteDialog"
			data-msg="<s:message
							code="notice.markAll.confirm" text="Mark All read?" />"><button
				type="button" class="btn btn-default pull-right a-tooltip"
				data-toggle="tooltip" data-placement="bottom" data-container='body'
				title="<s:message
							code="notice.markAll" text="Mark All read" />">
				<i class="fa fa-check"></i>
			</button></a>
		<table id="eventsTable" class="table table-hover table-condensed">
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
				<td><c:choose>
						<c:when test="${event.type eq 'COMMENT'}">
							<i class="fa fa-comment"></i>
						</c:when>
						<c:when test="${event.type eq 'WATCH'}">
							<i class="fa fa-eye"></i>
						</c:when>
						<c:when test="${event.type eq 'SYSTEM'}">
							<i class="fa fa-eye"></i>
						</c:when>
					</c:choose></td>
				<td><c:choose>
						<c:when test="${event.unread}">
							<div class="eventSummary">
						</c:when>
						<c:otherwise>
							<div class="eventSummary">
						</c:otherwise>
					</c:choose> <a href="#" class="showMore" data-event="${event.id}">[${event.task}]
						- ${event.who}&nbsp; <s:message code="${event.logtype.code}" />
				</a>
					<blockquote class="eventMore quote">${event.message}<div
							class="pull-right buttons_panel">
							<a style="color: gray"
								href="<c:url value="/task?id=${event.task}"/>"><i
								class="fa fa-lg fa-link fa-flip-horizontal a-tooltip" title="<s:message code="event.task"/>"></i></a> <a
								style="color: gray" href="#" data-event="${event.id}"
								class="delete-event a-tooltip"
								title="<s:message code="event.delete"/>"> <i
								class="fa fa-lg fa-trash"></i></a>
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
var loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="3"><i class="fa fa-cog fa-spin"></i><s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';
//fetchEvents(0);
$(document).on("click",".eventNavBtn",function(e) {
	var page =  $(this).data('page');
	fetchEvents(page,'');
});

// $("#search_field").change(function() {
// 	fetchUsers(0,$("#search_field").val());
// 	});
	
function fetchEvents(page,term){
	$("#eventsTable .listedEvent").remove();
	$("#eventsTable").append(loading_indicator);
	var url = '<c:url value="/listEvents"/>';
	$.get(url, {page: page,term:term}, function(data) {
		$("#loading").remove();
 		console.log(data);

// 		var avatarURL = '<c:url value="/userAvatar/"/>';
// 		var userURL = '<c:url value="/user?id="/>';
// 		var email_txt = '<s:message code="user.send.mail"/>';
// 		for ( var j = 0; j < data.content.length; j++) {
// 			var content = data.content[j];
// 			var user = userURL + content.id; 
// 			var avatar = '<img data-src="holder.js/30x30" style="height: 30px; float: left; padding-right: 10px;" src="' + avatarURL + +data.content[j].id +'"/>';
// 			var row = '<tr class="listeduser"><td>'
// 						+ '<a href="' + user + '" class="btn">'
// 						+ avatar
// 						+ content.name + " " + content.surname +'</a></td>'
// 						+ '<td>'+getRoleTypeMsg(content.role)+'</td>'
// 						+ '<td><a href="mailto:'+content.email+'" title="'+email_txt+' ('+content.email+')"><i class="fa fa-envelope" style="color: black;"></span></a></td></tr>';
// 			$("#user_table").append(row);
// 		}
		//print Nav
		$("#events_nav tr").remove();
		if(data.totalPages > 1){
			printNavigation(page,data);
		}
	});
}
function printNavigation(page,data){
	$("#events_nav tr").remove();
	var topRow='<tr id="topNavigation">';
	var prev = '<td style="width:30px"></td>';
	if(!data.firstPage){
		prev = '<td style="width:30px"><a class="eventNavBtn btn" data-page="'+ (page -1)+'"><i class="fa fa-arrow-left"></i></a></td>';
	}
	topRow+=prev;
	var numbers = '<td style="text-align:center">';
	//print numbers
	for (var i = 0; i < data.totalPages; i++) {
		var btnClass = "eventNavBtn btn";
		//active btn
		if (i == data.number) {
			btnClass += " btn-default";
		}
		var button = '<a class="'+btnClass+'" data-page="'+ i +'">'
				+ (i + 1) + '</a>';
				numbers+=button;
	}
	topRow+=numbers;
	var next = '<td style="width:30px"></td>';
	if(!data.lastPage){
		next = '<td style="width:30px"><a class="navBtn btn" data-page="'+ (page +1) +'"><i class="fa fa-arrow-right"></i></a></td>';
	}
	topRow+=next+'</tr>';
	$("#events_nav").append(topRow);
}



	$(".showMore").click(function() {
		var eventID = $(this).data('event');
		var event = $(this);
		if (event.closest("tr").hasClass("unread")) {
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

	$(".delete-event").click(function() {
		var eventID = $(this).data('event');
		var event = $(this);
		var url = '<c:url value="/events/delete"/>';
		$.post(url, {
			id : eventID
		}, function(result) {
			if (result.code == 'ERROR') {
				showError(result.message);
			} else {
				event.closest("tr").remove();
			}
		});
	});

	$("#readAll").click(function() {
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

	$("#deleteAll").click(
			function() {
				var url = '<c:url value="/events/deleteAll"/>';
				var msg = $(this).data('msg');
				var lang = $(this).data('lang');
				bootbox.setDefaults({
					locale : lang
				});
				bootbox.confirm("<p align=\"center\">" + msg + "</p>",
						function(confirmation) {
							if (confirmation) {
								$.post(url, {}, function(result) {
									if (result.code == 'ERROR') {
										showError(result.message);
									} else {
										$('tr.eventRow').each(function(i, obj) {
											obj.remove();
										});
									}
								});
							}
						});
			});

	
</script>