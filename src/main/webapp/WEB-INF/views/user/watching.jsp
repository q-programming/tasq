<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div class="white-frame"
	style="width: 80%; overflow: auto; display: table">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black" href="<c:url value="/events" />">
					<i class="fa fa-bell"></i> <s:message code="events.events" />
			</a></li>
			<li class="active"><a style="color: black" href="#"> <i
					class="fa fa-eye"></i> <s:message code="events.watching" /></a></li>
		</ul>
	</div>
	<div>
		<table id="watchesTable" class="table table-hover table-condensed">
			<thead class="theme">
				<tr>
					<th style="width: 55px;"></th>
					<th><s:message code="task.task"/></th>
					<th style="width: 100px;"><s:message code="events.watchers"/></th>
				</tr>
			</thead>
		</table>
		<div class="text-center">
				<ul id="watchesNavigation"></ul>
		</div>
		
	</div>
</div>
<script>
var loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="3"><i class="fa fa-cog fa-spin"></i><s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';
fetchWatches(0);

$(document).on("click",".stopWatching",function(e) {
		var clicked = $(this);
		var taskID = clicked.data('taskid');
		var url = '<c:url value="/task/watch"/>';
		$.post(url, {
			id : taskID
		}, function(result) {
			if (result.code == 'ERROR') {
				showError(result.message);
			} else {
				showSuccess(result.message);
				clicked.closest("tr").remove();
			}
		});
});

function fetchWatches(page){
		$("#watchesTable .watchRow").remove();
		$("#watchesTable").append(loading_indicator);
		var url = '<c:url value="/listWatches"/>';
		$.get(url, {page: page}, function(data) {
			$("#loading").remove();
			if(data.content.length == 0){
				var row = '<tr class="watchRow centerPadded"><td colspan="3"><i><s:message code="event.noWatches"/></i></td></tr>';
				$("#watchesTable").append(row);
			}
			for ( var j = 0; j < data.content.length; j++) {
				var task = data.content[j];
				var row = '<tr class="watchRow">';
				var button = '<td><i class="btn btn-default fa fa-eye-slash stopWatching a-tooltip"	'
							+'data-taskid="'+ task.id + '"title="<s:message code="task.watch.stop"/>"></i></td>';
				var taskUrl = '<c:url value="/task/"/>' + task.id;
				var taskStr = '<td>' + getTaskType(task.type) + '&nbsp;<a href="'+taskUrl + '">[' + task.id +'] ' 
							+ task.name + '</a></td>';
				var watches = '<td style="text-align:center">'+ task.watchCount + '&nbsp;<i class="fa fa-eye"></i></td>'
				row+=button + taskStr + watches + '</tr>'
				$("#watchesTable").append(row);
			}
			printWatchesNavigation(page,data)
			$('.a-tooltip').tooltip();
		});
		
	}
	
function printWatchesNavigation(page,data){
	var options = {
			bootstrapMajorVersion: 3,
            currentPage: page+1,
            totalPages: data.totalPages,
            itemContainerClass: function (type, page, current) {
                return (page === current) ? "active" : "pointer-cursor";
            },
            numberOfPages:10,
            onPageChanged: function(e,oldPage,newPage){
            	fetchWatches(newPage-1,'');
            }
   	}
	$("#watchesNavigation").bootstrapPaginator(options);
}

</script>