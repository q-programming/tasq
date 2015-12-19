<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<security:authentication property="principal" var="user" />
${user.theme.font.link}
<c:set var="color" value="${user.theme.color}"></c:set>
<c:set var="invcolor" value="${user.theme.invColor}"></c:set>
<c:set var="rgbcolor">${user.theme.rgbColor.r},${user.theme.rgbColor.g},${user.theme.rgbColor.b}</c:set>
<c:set var="rgbcolor10">${user.theme.rgbColor.r + 60},${user.theme.rgbColor.g + 60},${user.theme.rgbColor.b + 60}</c:set>
<c:set var="rgbinvcolor">${user.theme.rgbInvColor.r},${user.theme.rgbInvColor.g},${user.theme.rgbInvColor.b}</c:set>
<style>
body {
	${user.theme.font.cssFamily}	
}
.theme-text{
	color: ${invcolor} !important;
}

a.theme {
	color: ${invcolor} !important;
}

.caret.theme {
	border-top-color: ${invcolor};
	border-bottom-color: ${invcolor};
}
.btn.caret.theme:hover {
	border-top-color: rgba(${rgbinvcolor}, 0.5);
	border-bottom-color: rgba(${rgbinvcolor}, 0.5);
}
.bg-color.theme {
	color: ${invcolor};
	background-color: ${color};
}
.bg-preview.theme {
    border: 1px dashed;
    display: table-cell;
    text-align: center;
    min-width: 110px;
    max-width: 200px;
    margin: 0 auto;
	background-color: ${color};
	border-color: ${invcolor};
}

nav.navbar.navbar-fixed-top.theme {
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
	border-color: rgba(${rgbcolor}, 0.5);
	padding: 5px;
	/* 	border-radius: 5px; */
}
.state_hover {
	border-style: dashed;
	border-width: 2px;
	border-color: ${color};
	/* 	border-radius: 5px; */
}

span.badge.theme {
	background-color: rgb(${rgbcolor10});
}

span.badge.theme.zero {
	color: ${color};
}

.side-bar.theme {
	background-color: ${color};
}

.nav-pills>li.active>a, .nav-pills>li.active>a:hover, .nav-pills>li.active>a:focus
	{
	color: ${invcolor};
	background-color: ${color};
}
.nav-sidebar>.active>a, .nav-sidebar>.active>a:hover, .nav-sidebar>.active>a:focus
	{
	color: ${invcolor};
	background-color: ${color};
}
.nav>li>a:focus, .nav>li>a:hover {
    text-decoration: none;
	color: ${invcolor};
	background-color: ${color};
}
.navbar-toggle {
	border-color:  ${invcolor};
}
.navbar-toggle .icon-bar {
	background:  ${invcolor};
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
.dropdown-menu > li > a:hover, .dropdown-menu > li > a:focus {
  color:  ${invcolor};
  background-color: ${color};
  background-image: none;
  filter: none;
}
.btn.theme :focus, .btn.theme:hover {
    color: rgba(${rgbinvcolor}, 0.5);
}
.back-to-top {
	display: none;
	position: fixed;
	bottom: 20px;
	right: 20px;
	color: ${invcolor};
	text-decoration: none;
	padding: 10px 15px 10px 15px;
	background-color: rgba(${rgbcolor}, 0.4);
	text-align: center;
}
.back-to-top:hover {
	text-decoration: none;
	color: ${invcolor};
	background-color: rgba(${rgbcolor}, 0.7);
}
.nav .open>a, .nav .open>a:focus, .nav .open>a:hover {
    background-color: transparent;
    
}
.header-time{
	color: ${invcolor};
	font-size: xx-large;
}

.form-control::-webkit-input-placeholder { color: ${invcolor}; }
.form-control:-moz-placeholder { color: ${invcolor}; }
.form-control::-moz-placeholder { color: ${invcolor}; }
.form-control:-ms-input-placeholder { color: ${invcolor}; }

.nav-search{
	height: 52px;
	background: ${color};
	color: ${invcolor};
	border-color: transparent;
}
.nav-search:FOCUS{
	border-color: transparent;
}
.ui-state-default, .ui-widget-content .ui-state-default, .ui-widget-header .ui-state-default {
    border: 1px solid rgb(${rgbcolor10});
    background: ${color} url("images/ui-bg_flat_75_428bca_40x100.png") 50% 50% repeat-x;
    font-weight: normal;
    color: ${invcolor};
}
</style>