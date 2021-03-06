<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<%@page import="com.qprogramming.tasq.account.Roles"%>
<%
	pageContext.setAttribute("roles", Roles.values());
%>
<script language="javascript" type="text/javascript"
	src="<c:url value="/resources/js/bootstrap-paginator.min.js"/>"></script>
<c:set var="name_text">
	<s:message code="user.name" text="Name" />&nbsp;
	<s:message code="user.surname" text="Surname" />
</c:set>
<c:set var="role_text">
	<s:message code="role.role" text="Role" />
</c:set>
<c:set var="userList_text">
	<s:message code="user.users" text="User List" />
</c:set>
<c:set var="participants_text">
	<s:message code="project.members" text="Project Members" />
</c:set>
<c:set var="action_text">
	<s:message code="main.action" text="Action" />
</c:set>

<div id="show_users" class="modal fade" tabindex="-1" role="dialog"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<button type="button" class="close theme-close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<i id="search_users" class="fa fa-search search-btn a-tooltip" data-html="true" data-placement="left" title="<s:message code="user.search"/>"></i>
				<h4 class="modal-title" id="myModalLabel">
					<i class="fa fa-users"></i>&nbsp;<span id="users_modalLable">${userList_text}</span>
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
			<div class="text-center">
				<ul id="user_nav"></ul>
			</div>
<!-- 		<div class="modal-footer"> -->
<!-- 			<div><button class="btn btn-default">ok</button></div> -->
<!-- 		</div> -->
		</div>
	</div>
</div>
<script>
var user_loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="4"><i class="fa fa-cog fa-spin"></i><s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';
var url;
var projId;
var show;
$('.show_users_btn').click(function() {
	$("#user_table .listeduser").remove();
	url = '<c:url value="/users"/>';
	$("#users_modalLable").html('${userList_text}');
	$('#filterrow').hide();
	$('#search_field').val('');
	$("#show_users").modal('show');
	projId = '';
	if($("#user_table tr").length<3){
		fetchUsers(0);
		}
});

$('.show_participants_btn').click(function() {
	$("#user_table .listeduser").remove();
	url = '<c:url value="/project/participants"/>';
	projId = '${project.projectId}';
	$("#users_modalLable").html('${participants_text}');
	$('#filterrow').hide();
	$('#search_field').val('');
	$("#show_users").modal('show');
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
	$("#user_table").append(user_loading_indicator);
	
	$.get(url, {projId: projId, page: page,term:term}, function(data) {
		$("#loading").remove();
// 		console.log(data);
		var avatarURL = '<c:url value="/../avatar/"/>';
		var userURL = '<c:url value="/user/"/>';
		var email_txt = '<s:message code="user.send.mail"/>';
		for ( var j = 0; j < data.content.length; j++) {
			var content = data.content[j];
			var online;
			if(content.online){
				online = '<i class="fa fa-user a-tooltip" style="color:mediumseagreen" title="<s:message code="main.online"/>"></i>';
			}else{
				online = '<i class="fa fa-user a-tooltip" style="color:lightgray" title="<s:message code="main.offline"/>"></i>';
			}
			var confirmed = '';
			if(!content.confirmed){
				confirmed = '<span><i style="color: red" class="fa fa-exclamation-triangle a-tooltip"	title="<s:message code="panel.emails.notconfirmed"/>"></i></span>';
			}
			var user = userURL + content.username; 
			var avatar = '<img data-src="holder.js/30x30" class="avatar small" src="' + avatarURL + +data.content[j].id +'.png"/>&nbsp;';
			var row = '<tr class="listeduser"><td>'
						+ online
						+ '<a href="' + user + '" class="btn">'
						+ avatar
						+ content.name + " " + content.surname +'</a></td>'
						+ '<td>'+getRoleTypeMsg(content.role)+'</td>'
						+ '<td><a href="mailto:'+content.email+'" title="'+email_txt+' ('+content.email+')"><i class="fa fa-envelope" style="color: black;"></span>'+ confirmed +'</a></td></tr>';
			$("#user_table").append(row);
		}
		//print Nav
		$("#user_nav tr").remove();
		if(data.totalPages > 1){
			printNavigation(page,data);
		}else{
			$("#user_nav").html('');
		}
		$('.a-tooltip').tooltip();
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
		$('#filterrow').toggle();
		fetchUsers(0);
		$(this).removeAttr("shown");
	}
	else{
		$('#filterrow').toggle();
		$('#search_field').focus();
		$(this).attr("shown",true);
	}
	$('#search_field').val('');
});

$('.sorter').click(function() {
		var table = $(this).parents('table').eq(0);
		var rows = table.find("tr:not(:has('th'))").toArray().sort(comparer($(this).index()));
		this.asc = !this.asc;
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
		var valA = getCellValue(a, index), valB = getCellValue(b, index);
		return $.isNumeric(valA) && $.isNumeric(valB) ? valA - valB : valA
				.localeCompare(valB)
	};
}
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
	}
}
function printNavigation(page,data){
	var options = {
			bootstrapMajorVersion: 3,
            currentPage: page+1,
            totalPages: data.totalPages,
            itemContainerClass: function (type, page, current) {
                return (page === current) ? "active" : "pointer-cursor";
            },
            numberOfPages:10,
            onPageChanged: function(e,oldPage,newPage){
            	fetchUsers(newPage-1,$("#search_field").val());
            }
   	};
	$("#user_nav").bootstrapPaginator(options);
}


</script>