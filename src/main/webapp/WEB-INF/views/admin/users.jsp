<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@page import="com.qprogramming.tasq.account.Roles"%>
<%pageContext.setAttribute("roles", Roles.values());	%>
<c:set var="name_text">
	<s:message code="user.name" text="Name" /> <s:message code="user.surname" text="Surname" />
</c:set>
<c:set var="role_text">
	<s:message code="role.role" text="Role" />
</c:set>
<c:set var="userList_text">
	<s:message code="menu.manage.users " text="Manage users" />
</c:set>
<c:set var="action_text">
	<s:message code="main.action" text="Action" />
</c:set>
<!-- <div id="messages_div"></div> -->
<div class="white-frame" style="display: table; width: 100%;height:80vh">
<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black"
				href="<c:url value="/manage/app" />"> <i class="fa fa-cogs"></i>
					<s:message code="menu.manage" text="Manage application" /></a></li>
			<li class="active"><a style="color: black" href="#"> <i class="fa fa-users"></i>	
					<s:message code="menu.manage.users " text="Manage users" /></a></li>
			<li><a style="color: black" href="<c:url value="/manage/tasks"/>"> <i
					class="fa fa-lg fa-check-square"></i> <s:message code="menu.manage.tasks" /></a></li>
		</ul>
	</div>
	<i id="managed_users_search" class="fa fa-search search-btn a-tooltip" data-html="true" data-placement="left" title="<s:message code="user.search"/>"></i>
	<div style="min-height: 75vh;">
		<table id="managed_user_table" class="table table-hover table-condensed">
				<thead>
					<tr style="color: #428bca">
						<th style="cursor: pointer;" class="sorter"
							>${name_text}</th>
						<th>${role_text}</th>
						<th>${action_text}</th>
					</tr>
					<tr id="manage_filterrow" style="display:none">
						<th colspan="3">
								<input id="manage_search" class="form-control input-sm" placeholder="<s:message code="user.search.placeholder"/>">
						</th>
					</tr>
				</thead>
		</table>
	</div>
	<div>
				<table id="manage_nav" style="width: 100%;"></table>
	</div>
</div>
<security:authentication property="principal" var="user" />
<security:authorize access="hasRole('ROLE_ADMIN')" var="role"/>
<script>
roles = new Array();;
userID = '${user.id}';
<c:forEach items="${roles}" var="enum_role">
	roles.push('${enum_role}');
</c:forEach>
var loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="3"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';
role_change_txt = '<s:message code="user.role.change"/>';
fetchManagedUsers(0);
$.expr[":"].contains = $.expr.createPseudo(function(arg) {
    return function( elem ) {
        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});

var previous;
$(document).on("focus",".change_role",function(e) {
	previous = this.value;
});

$(document).on("change",".change_role",function(e) {
	var selectBox = $(this);
	$.post('<c:url value="/role"/>',{id:selectBox.data('user'),role:selectBox.val()},function(result){
		if(result.code!='OK'){
			showError(result.message);
			selectBox.val(previous);
		}else{
			showSuccess(result.message);
		}
	});
});

$("#manage_search").change(function() {
	fetchManagedUsers(0,$("#manage_search").val());
});
$('#managed_users_search').click(function() {
	if($(this).attr("shown") && $('#manage_search').val()!=''){
		fetchManagedUsers(0);
		$(this).removeAttr("shown");
	}
	else{
		$(this).attr("shown",true);
	}
	$('#manage_filterrow').toggle();
	$('#manage_search').val('');
});

$('a[rel=popover]').popover({
	  html: true,
	  trigger: 'hover',
	  content: function () {
	    return '<img data-src="holder.js/100x100" style="height: 100px;" src="'+$(this).data('img') + '" />';
	  }
	});

$('.manage_sorter').click(function() {
		var table = $(this).parents('table').eq(0)
		var rows = table.find("tr:not(:has('th'))").toArray().sort(comparer($(this).index()))
		this.asc = !this.asc
		if (!this.asc) {
			rows = rows.reverse();
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
	}
};
function getCellValue(row, index) {
	return $(row).children('td').eq(index).html()
}

function printManageNavigation(page,data){
	$("#manage_nav tr").remove();
	var topRow='<tr id="topNavigation">';
	var prev = '<td style="width:30px"></td>';
	if(!data.firstPage){
		prev = '<td style="width:30px"><a class="mgrNavBtn btn" data-page="'+ (page -1)+'"><i class="fa fa-arrow-left"></i></a></td>';
	}
	topRow+=prev;
	var numbers = '<td style="text-align:center">';
	//print numbers
	for (var i = 0; i < data.totalPages; i++) {
		var btnClass = "mgrNavBtn btn";
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
		next = '<td style="width:30px"><a class="mgrNavBtn btn" data-page="'+ (page +1) +'"><i class="fa fa-arrow-right"></i></a></td>';
	}
	topRow+=next+'</tr>';
	$("#manage_nav").append(topRow);
}

$(document).on("click",".mgrNavBtn",function(e) {
	var page =  $(this).data('page');
	fetchManagedUsers(page,$("#manage_nav").val());
});

function fetchManagedUsers(page,term){
	$("#managed_user_table .listeduser").remove();
	$("#managed_user_table").append(loading_indicator);
	var url = '<c:url value="/users"/>';
	$.get(url, {page: page,term:term,size:20}, function(data) {
		$("#loading").remove();
// 		console.log(data);
		var avatarURL = '<c:url value="/../avatar/"/>';
		var userURL = '<c:url value="/user?id="/>';
		var email_txt = '<s:message code="user.send.mail"/>';
		for ( var j = 0; j < data.content.length; j++) {
			var content = data.content[j];
			if(content.online){
				online = '<i class="fa fa-user a-tooltip" style="color:mediumseagreen" title="<s:message code="main.online"/>"></i>';
			}else{
				online = '<i class="fa fa-user a-tooltip" style="color:lightgray" title="<s:message code="main.offline"/>"></i>';
			}
			var username = content.name + " " + content.surname;
			var user = userURL + content.id; 
			var avatar = '<img data-src="holder.js/30x30" style="height: 30px; float: left; padding-right: 10px;" src="' + avatarURL + +data.content[j].id +'.png"/>';
			var action;
			if(content.id == userID){
				action='';
			}else{
				action='<a href="mailto:'+content.email+'" title="'+email_txt+' ('+content.email+')"><i class="fa fa-envelope" style="color: black;"></span></a>';
			}
			var row = '<tr class="listeduser"><td>'
						+ online
						+ '<a href="' + user + '" class="btn">'
						+ avatar + username + '</a></td>'
						+ '<td>'+printRoleSelect(content.id,content.role) + '</td>'
						+ '<td>'+action+'</td></tr>';
			$("#managed_user_table").append(row);
		}
		//print Nav
		$("#manage_nav tr").remove();
		if(data.totalPages > 1){
			printManageNavigation(page,data);
		}
		$('.a-tooltip').tooltip();
	});
}
function printRoleSelect(id,role){
	var select = '<select class="change_role a-tooltip" title="'+role_change_txt+'" data-user="'+id+'" style="border: 0px;">';
	$.each(roles, function(key,val){
		var selected = '';
		if(role === val){
			selected = "selected";
		}
		select+='<option value="'+val+'"'+selected+'>'+ getRoleTypeMsg(val) +'</option>';
	});
	select +='</select>';
	return select;
}

</script>