<%@page import="com.qprogramming.tasq.task.worklog.LogType"%>
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
		</a> <a href="#" id="readAll" title="<s:message
							code="events.markAll" text="Mark All read?" />"
			class="deleteDialog"
			data-msg="<s:message
							code="events.markAll.confirm" text="Mark All read?" />"><button
				type="button" class="btn btn-default pull-right a-tooltip"
				data-toggle="tooltip" data-placement="bottom" data-container='body'
				title="<s:message
							code="events.markAll" text="Mark All read" />">
				<i class="fa fa-check"></i>
			</button></a>
		<table id="eventsTable" class="table table-hover table-condensed">
			<thead class="theme">
				<tr>
					<th style="width: 20px;"></th>
					<th></th>
					<th style="width: 200px;"><s:message code="main.date"/></th>
				</tr>
			</thead>
		</table>
		<div class="text-center">
				<ul id="eventsNavigation"></ul>
		</div>
	</div>
</div>
<script>
var loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="3"><i class="fa fa-cog fa-spin"></i><s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';
fetchEvents(0);
$(document).on("click",".eventNavBtn",function(e) {
	var page =  $(this).data('page');
	fetchEvents(page,'');
});

// $("#search_field").change(function() {
// 	fetchUsers(0,$("#search_field").val());
// 	});
	
function fetchEvents(page,term){
	$("#events_nav").remove();
	$("#eventsTable .eventRow").remove();
	$("#eventsTable").append(loading_indicator);
	var url = '<c:url value="/listEvents"/>';
	$.get(url, {page: page,term:term}, function(data) {
		$("#loading").remove();
		console.log(data.content.length);
		if(data.content.length == 0){
			var row = '<tr class="eventRow centerPadded"><td colspan="3"><i><s:message code="event.noEvents"/></i></td></tr>';
			$("#eventsTable").append(row);
		}
		for ( var j = 0; j < data.content.length; j++) {
			var event = data.content[j];
			var row = '<tr class="eventRow ';
			//read unread
			if(event.unread){
				row += 'unread">';
			}else{
				row += 'read">';
			}
			row+='<td>'
			//Type
			if(event.type == 'COMMENT'){
				row+='<i class="fa fa-comment"></i>';
			}else if(event.type == 'WATCH'){
				row+='<i class="fa fa-eye"></i>';
			}else if(event.type == 'SYSTEM'){
				row+='<i class="fa fa-exclamation-triangle"></i>';
			}
			row+='</td><td><div class="eventSummary">';
			//title
			if(event.task!=null){
				var link = '<a href="#" class="showMore" data-event="'+event.id+'">['+event.task+'] - '+event.who+'&nbsp; '+getEventTypeMsg(event.logtype)+'</a>';
			}
			else{
				var link = '<a href="#" class="showMore" data-event="'+event.id+'">'+event.who+'&nbsp; '+getEventTypeMsg(event.logtype)+'</a>';
			}
			row+=link;
			//more
			var taskurl = '<c:url value="/task/"/>'+event.task;
			var eventtask = '<s:message code="event.task"/>';
			var deleteevent= '<s:message code="event.delete"/>';
			var content = '<blockquote class="eventMore quote">'+event.message+'<div class="pull-right buttons_panel">'
							+'<a style="color: gray" href="'+taskurl+'"><i class="fa fa-lg fa-link fa-flip-horizontal a-tooltip" title="'+eventtask+'"></i></a>'
							+'<a style="color: gray" href="#" data-event="'+event.id+'" class="delete-event a-tooltip"	title="'+deleteevent+'"> <i class="fa fa-lg fa-trash"></i></a>'
							+'</div></blockquote>';							
			row+=content;			
			//date
			row+='</div></td><td style="color: darkgrey;">'+event.date+'</tr>';
			$("#eventsTable").append(row);
		}	
		//print Nav
		if(data.totalPages > 1){
			printEventsNavigation(page,data);
		}
	});
}
<%
pageContext.setAttribute("types",
		LogType.values());
%>
function printEventsNavigation(page,data){
	var options = {
			bootstrapMajorVersion: 3,
            currentPage: page+1,
            totalPages: data.totalPages,
            itemContainerClass: function (type, page, current) {
                return (page === current) ? "active" : "pointer-cursor";
            },
            numberOfPages:10,
            onPageChanged: function(e,oldPage,newPage){
            	fetchEvents(newPage-1,'');
            }
   	}
	$("#eventsNavigation").bootstrapPaginator(options);
}

	$(document).on("click",".showMore",function(e) {
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

	$(document).on("click",".delete-even",function(e) {
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
				showSuccess(result.message);
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
										var row = '<tr class="eventRow centerPadded"><td colspan="3"><i><s:message code="event.noEvents"/></i></td></tr>';
										$("#eventsTable").append(row);
										showSuccess(result.message);
									}
								});
							}
						});
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