<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@page import="com.qprogramming.tasq.account.Roles"%>
<%
	pageContext.setAttribute("roles", Roles.values());
%>
<c:set var="name_text">
	<s:message code="user.name" text="Name" />
	<s:message code="user.surname" text="Surname" />
</c:set>
<c:set var="role_text">
	<s:message code="role.role" text="Role" />
</c:set>
<c:set var="userList_text">
	<s:message code="user.users" text="User List" />
</c:set>
<c:set var="action_text">
	<s:message code="main.action" text="Action" />
</c:set>

<div id="show_users" class="modal fade" tabindex="-1" role="dialog"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<i id="search_users" class="fa fa-search search-btn a-tooltip" data-html="true" data-placement="left" title="<s:message code="user.search"/>"></i>
				<h4 class="modal-title" id="myModalLabel">
					<i class="fa fa-users"></i> ${userList_text}
				</h4>
			</div>
			<table id="user_table" class="table table-hover table-condensed" style="width: 95%;margin: 15px;">
				<thead>
					<tr style="color: #428bca">
						<th style="cursor: pointer;" class="sorter"
							>${name_text}</th>
						<th>${role_text}</th>
						<th>${action_text}</th>
					</tr>
					<tr id="filterrow" style="display:none">
						<th colspan="3">
								<input id="search_field" class="form-control input-sm" placeholder="<s:message code="user.search.placeholder"/>">
						</th>
					</tr>
				</thead>
			</table>
			<div>
				<table id="user_nav" style="width: 100%;"></table>
			</div>
<!-- 		<div class="modal-footer"> -->
<!-- 			<div><button class="btn btn-default">ok</button></div> -->
<!-- 		</div> -->
		</div>
	</div>
</div>
<script>
var loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="3"><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';

$('#show_users').on('shown.bs.modal', function (e) {
	if($("#user_table tr").length<3){
		fetchUsers(0);
		}
	});

$(document).on("click",".navBtn",function(e) {
	var page =  $(this).data('page');
	fetchUsers(page,$("#search_field").val());
});

$("#search_field").change(function() {
	fetchUsers(0,$("#search_field").val());
	});
	
function fetchUsers(page,term){
	$("#user_table .listeduser").remove();
	$("#user_table").append(loading_indicator);
	var url = '<c:url value="/users"/>';
	$.get(url, {page: page,term:term}, function(data) {
		$("#loading").remove();
// 		console.log(data);
		var avatarURL = '<c:url value="/userAvatar/"/>';
		var userURL = '<c:url value="/user?id="/>';
		var email_txt = '<s:message code="user.send.mail"/>';
		for ( var j = 0; j < data.content.length; j++) {
			var content = data.content[j];
			var user = userURL + content.id; 
			var avatar = '<img data-src="holder.js/30x30" style="height: 30px; float: left; padding-right: 10px;" src="' + avatarURL + +data.content[j].id +'"/>';
			var row = '<tr class="listeduser"><td>'
						+ '<a href="' + user + '" class="btn">'
						+ avatar
						+ content.name + " " + content.surname +'</a></td>'
						+ '<td>'+getRoleTypeMsg(content.role)+'</td>'
						+ '<td><a href="mailto:'+content.email+'" title="'+email_txt+' ('+content.email+')"><i class="fa fa-envelope" style="color: black;"></span></a></td></tr>';
			$("#user_table").append(row);
		}
		//print Nav
		$("#user_nav tr").remove();
		if(data.totalPages > 1){
			printNavigation(page,data);
		}
	});
}


$.expr[":"].contains = $.expr.createPseudo(function(arg) {
    return function( elem ) {
        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});
var previous;
$('#search_users').click(function() {
	if($(this).attr("shown") && $('#search_field').val()!=''){
		fetchUsers(0);
		$(this).removeAttr("shown");
	}
	else{
		$(this).attr("shown",true);
	}
	$('#filterrow').toggle();
	$('#search_field').val('');
});

$('.sorter').click(function() {
		var table = $(this).parents('table').eq(0)
		var rows = table.find("tr:not(:has('th'))").toArray().sort(comparer($(this).index()))
		this.asc = !this.asc
		if (!this.asc) {
			rows = rows.reverse();
			//TODO Add some indicator about sorting ?
			//$(this).append('<span class="glyphicon glyphicon-sort-by-alphabet-alt"></span>');
		} else {
			//$(this).append('<span class="glyphicon glyphicon-sort-by-alphabet"></span>');
		}
		for (var i = 0; i < rows.length; i++) {
			table.append(rows[i])
		}
});
function comparer(index) {
	return function(a, b) {
		var valA = getCellValue(a, index), valB = getCellValue(b, index)
		return $.isNumeric(valA) && $.isNumeric(valB) ? valA - valB : valA
				.localeCompare(valB)
	};
};
function getCellValue(row, index) {
	return $(row).children('td').eq(index).html()
}

function getRoleTypeMsg(role){
	switch(role){
		<c:forEach items="${roles}" var="enum_role">
		case "${enum_role}":
			return '<s:message code="${enum_role.code}"/>';
		</c:forEach>
		default:
			return 'not yet added ';
	};
};

function printNavigation(page,data){
	$("#user_nav tr").remove();
	var topRow='<tr id="topNavigation">';
	var prev = '<td style="width:30px"></td>';
	if(!data.firstPage){
		prev = '<td style="width:30px"><a class="navBtn btn" data-page="'+ (page -1)+'"><i class="fa fa-arrow-left"></i></a></td>';
	}
	topRow+=prev;
	var numbers = '<td style="text-align:center">';
	//print numbers
	for (var i = 0; i < data.totalPages; i++) {
		var btnClass = "navBtn btn";
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
	$("#user_nav").append(topRow);
}


</script>