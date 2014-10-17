<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!-- Locale set -->
<c:set var="name_text">
	<s:message code="user.namesurname" text="Name" />
</c:set>
<c:set var="jobtitle_text">
	<s:message code="user.jobtitle" text="Job Title" />
</c:set>
<c:set var="userList_text">
	<s:message code="user.userlist" text="User List" />
</c:set>
<c:set var="admin_text">
	<s:message code="main.adminPanel" text="Admin Panel" />
</c:set>
<c:set var="action_text">
	<s:message code="main.action" text="Action" />
</c:set>
<c:set var="filterClean_text">
	<s:message code="user.filterClean" text="Clear filter" />
</c:set>
<h3>Users</h3>
<div class="white-frame" style="display: table; width: 100%;height:85vh">
	<table class="table table-hover table-condensed">
		<thead>
			<tr style="color: #428bca">
				<th style="cursor: pointer;" class="sorter a-tooltip"
					data-toggle="tooltip" data-placement="left" data-container='body'
					title="<s:message
							code="user.name.sort" text="Sort by name" />">${name_text}</th>
				<th>${jobtitle_text}</th>
				<th>${action_text}</th>
			</tr>
		</thead>
		<c:forEach items="${accountsList}" var="p">
			<tr>
				<td><security:authentication property="principal"
						var="AuthUser" /> <a
					href="<c:url value="/user/details?id=${p.id}"/>" class="btn"
					rel="popover" data-img="<c:url value="/userAvatar/${p.id}"/>">${p.name}
						${p.surname}</a></td>
				<td>${p.role}</td>
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
${user}
role: ${role}
isAdmin: ${is_admin}

<script>
$.expr[":"].contains = $.expr.createPseudo(function(arg) {
    return function( elem ) {
        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});

$('a[rel=popover]').popover({
	  html: true,
	  trigger: 'hover',
	  content: function () {
	    return '<img data-src="holder.js/100x100" style="height: 100px;" src="'+$(this).data('img') + '" />';
	  }
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
	}
};
function getCellValue(row, index) {
	return $(row).children('td').eq(index).html()
}

// additional code to apply a filter
$('table').each(
function() {
	var table = $(this);
	//var headers = table.find('th').length
	var filterrow = $('<tr>').insertAfter($(this).find('th:last()').parent());
	//Add to only first collumns
	filterrow.append($('<th>').append($('<input class="form-control" style="height:25px">')	.attr('type','text').keyup(
		function() {
			table.find('tr').show();
			filterrow.find('input[type=text]').each(
					function() {
						var index = $(this).parent().index() + 1;
						var filter = $(this).val() != '';
						$(this).toggleClass('filtered',filter);
						if (filter) {
							var el = 'td:nth-child('+ index	+ ')';
							var criteria = ":contains('"+ $(this).val()	+ "')";
							table.find(el+ ':not('+ criteria+ ')').parent().hide();
							}
						});
					})));
	filterrow.append($('<th>'));
	filterrow.append($('<th>').append("<button type=\"button\" class=\"btn btn-default btn-sm\"><span class=\"glyphicon glyphicon-remove\"></span> ${filterClean_text} </button>")
						.click(
		function() {
			$(this).parent().parent().find('input[type=text]').val('').toggleClass('filtered',false);
			table.find('tr').show();
			}));
	});
</script>