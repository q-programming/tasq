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
<div class="white-frame" style="display: table; width: 100%;height:85vh">
<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li class="active"><a style="color: black" href="#"> <i class="fa fa-users"></i>	
					<s:message code="menu.manage.users " text="Manage users" /></a></li>
			<li><a style="color: black" href="<c:url value="/manage/tasks"/>"> <i
					class="fa fa-lg fa-check-square"></i> <s:message code="menu.manage.tasks" /></a></li>
		</ul>
	</div>
	<table id="manage_user_table" class="table table-hover table-condensed">
		<thead>
			<tr style="color: #428bca">
				<th style="cursor: pointer;" class="sorter a-tooltip"
					data-toggle="tooltip" data-placement="left" data-container='body'
					title="<s:message
							code="user.sort.surname" text="Sort by name" />">${name_text}</th>
				<th>${role_text}</th>
				<th>${action_text}<button id="manage_search" class="btn btn-default btn-xs pull-right"><span class="glyphicon glyphicon-search"></span></button></th>
			</tr>
		</thead>
		<c:forEach items="${accountsList}" var="p">
			<tr>
				<td><security:authentication property="principal"
						var="AuthUser" /> <a
					href="<c:url value="/user?id=${p.id}"/>" class="btn"
					rel="popover" data-img="<c:url value="/userAvatar/${p.id}"/>">${p}</a></td>
				<security:authorize access="hasRole('ROLE_ADMIN')">
					<td>
						<select class="change_role a-tooltip" title="<s:message code="user.role.change"/>" data-user="${p.id}" style="border: 0px;">
							<c:forEach items="${roles}" var="enum_role">
								<option value="${enum_role}" <c:if test="${p.role eq enum_role}">selected</c:if>><s:message code="${enum_role.code}"/></option>
						</c:forEach>
						</select>
					</td>		
				</security:authorize>
				<security:authorize access="!hasRole('ROLE_ADMIN')">										
					<td><s:message code="${p.role.code}"/></td>
				</security:authorize>
				<td><security:authorize access="hasRole('ROLE_ADMIN')">
						<a href="<c:url value="user/details?id=${p.id}"/>" title="Modify"><span
							class="glyphicon glyphicon-pencil" style="color: black;">
						</span></a>
						<c:if test="${AuthUser != p}">
							<a href="<c:url value="admin/user/${p.id}/delete"/>"
								title="Delete"
								data-msg="Are you Sure want to permamently remove this user?<br>This operation cannot be undone!"
								class="confirmDialog"><span
								class="glyphicon glyphicon-trash" style="color: black;">
							</span></a>
						</c:if>
					</security:authorize> <c:if test="${AuthUser != p}">
						<a href="mailto:${p.email}" title="Send Message"><span
							class="glyphicon glyphicon-envelope" style="color: black;">
						</span></a>
					</c:if></td>
			</tr>
		</c:forEach>
	</table>
</div>
<security:authentication property="principal" var="user" />
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<security:authorize access="hasRole('ROLE_ADMIN')" var="role"/>
<script>
$.expr[":"].contains = $.expr.createPseudo(function(arg) {
    return function( elem ) {
        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});
var previous;
$(".change_role")
	.on('focus', function () {
    	previous = this.value;
	})
	.change(function(){
	var selectBox = $(this);
	$.post('<c:url value="/role"/>',{id:selectBox.data('user'),role:selectBox.val()},function(message){
		if(message!='OK'){
			showError(message);
			selectBox.val(previous);
		}
	});
	
});

$('#manage_search').click(function() {
	if($(this).attr("shown")){
		$('#manage_search_field').val('').toggleClass('manage_filtered',false);
	 	$('#manage_user_table').find('tr').show();
	 	$(this).removeAttr("shown");
	}else{
		$(this).attr("shown",true);
	}
	$('#manage_filterrow').toggle();
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
	}
};
function getCellValue(row, index) {
	return $(row).children('td').eq(index).html()
}

// additional code to apply a filter
$('table').each(
function() {
	var placeholder = "<s:message code="user.search.placeholder"/>";
	var table = $(this);
	//var headers = table.find('th').length
	var filterrow = $('<tr  id="manage_filterrow" style="display:none;">').insertAfter($(this).find('th:last()').parent());
	//Add to only first collumns
	filterrow.append($('<th colspan="3">').append($('<input id="manage_search_field" class="form-control" style="height:25px" placeholder="'+placeholder+'">')	.attr('type','text').keyup(
		function() {
			table.find('tr').show();
			filterrow.find('input[type=text]').each(
					function() {
						var index = $(this).parent().index() + 1;
						var filter = $(this).val() != '';
						$(this).toggleClass('manage_filtered',filter);
						if (filter) {
							var el = 'td:nth-child('+ index	+ ')';
							var criteria = ":contains('"+ $(this).val()	+ "')";
							table.find(el+ ':not('+ criteria+ ')').parent().hide();
							}
						});
					})));
	filterrow.append($('<th>'));
	});
</script>