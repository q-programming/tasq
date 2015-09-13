<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<security:authentication property="principal" var="user" />
${user.theme.font.link}
<c:set var="color" value="${user.theme.color}"></c:set>
<c:set var="invcolor" value="${user.theme.invColor}"></c:set>
<c:set var="rgbcolor">${user.theme.rgbColor.r},${user.theme.rgbColor.g},${user.theme.rgbColor.b}</c:set>
<style>
body {
	${user.theme.font.cssFamily}	
}

a.theme {
	color: ${invcolor};
}

.caret.theme {
	border-top-color: white;
	border-bottom-color: white;
}

div.navbar.navbar-fixed-top.theme {
	background-color: ${color};
}

div.modal-header.theme {
	background-color: ${color};
	color: ${invcolor};
	border-top-left-radius: 5px;
	border-top-right-radius: 5px;
}

thead.theme {
	background: ${color};
	color: ${invcolor};
}

.agile-card {
	color: inherit;
	border: 1px solid;
	padding: 5px;
	margin-bottom: 5px;
	border-color: lightgray;
	background-color: rgba(249, 253, 255, 1);
/* 	font-family: "Trebuchet MS", Arial, Helvetica, sans-serif; */
}

.agile-list {
	color: inherit;
	border: 1px solid;
	padding: 5px;
	margin-bottom: 5px;
	border-color: lightgray;
	background-color: rgba(249, 253, 255, 1)
}

.table_sprint {
	border-style: dashed;
	border-width: 2px;
	border-color: rgba(205, 236, 252, 1);
	padding: 5px;
	/* 	border-radius: 5px; */
}

span.badge.theme {
	background-color: ${color};
}

span.badge.theme.zero {
	color: ${color};
}

.side-bar.theme {
	background-color: ${color};
}

.nav-pills>li.active>a, .nav-pills>li.active>a:hover, .nav-pills>li.active>a:focus
	{
	color: #fff;
	background-color: ${color};
}

.table-hover>tbody>tr:hover>td, .table-hover>tbody>tr:hover>th {
	background-color: rgba(${rgbcolor}, 0.1);
}

span.tag.label.label-info.theme {
	background-color: rgba(${rgbcolor}, 0.8);
}

.pagination>.active>a, .pagination>.active>span, .pagination>li>a:focus,
	.pagination>li>a:hover {
	background-color: rgba(${rgbcolor}, 0.1);
}

.bs-docs-sidebar .nav>.active:focus>a, .bs-docs-sidebar .nav>.active:hover>a,
	.bs-docs-sidebar .nav>.active>a {
	padding-left: 18px;
	font-weight: 700;
	color: ${color};
	background-color: transparent;
	border-left: 2px solid ${color};
}

.bs-docs-sidebar .nav>li>a:focus, .bs-docs-sidebar .nav>li>a:hover {
	padding-left: 19px;
	color: ${color};
	text-decoration: none;
	background-color: transparent;
	border-left: 1px solid ${color};
}
</style>