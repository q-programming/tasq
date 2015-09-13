<%@page import="com.qprogramming.tasq.manage.Font"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%
	pageContext.setAttribute("fonts", Font.values());
%>
<c:forEach items="${fonts}" var="font">
	${font.link}
</c:forEach>
<script src="<c:url value="/resources/js/jquery.minicolors.js" />"></script>
<link href="<c:url value="/resources/css/jquery.minicolors.css" />" rel="stylesheet" media="screen" />

<div class="white-frame"
	style="display: table; width: 100%; height: 80vh">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li class="active"><a style="color: black"
				href="#"> <i class="fa fa-cogs"></i>
					<s:message code="menu.manage" text="Manage application" /></a></li>
			<li><a style="color: black"
				href="<c:url value="/manage/users" />"> <i class="fa fa-users"></i>
					<s:message code="menu.manage.users " text="Manage users" /></a></li>
			<li><a style="color: black" href="#"> <i
					class="fa fa-lg fa-check-square"></i> <s:message
						code="menu.manage.tasks" /></a></li>
		</ul>
	</div>
	<div>
		<h3>Application Avatar</h3>
	</div>
	<hr>
	<div>
		<h3>
			<i class="fa fa-paint-brush"></i> Themes
		</h3>
		<a id="createBtn" href="#" data-toggle="modal" data-target="#theme-create"> <span
			class="btn btn-default pull-right"><i class="fa fa-plus"></i><i
				class="fa fa-paint-brush"></i> <s:message code="theme.create"/></span></a>
		<table class="table table-hover table-condensed">
			<c:forEach items="${themes}" var="theme">
				<tr>
					<td>${theme.name}</td>
					<td style="${theme.font.cssFamily}"><span style="padding: 5px; background-color:${theme.color};color:${theme.invColor}">${theme.font.fontFamily}</span></td> 
					<td style="width:100px"><i class="fa fa-pencil"></i></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
<div class="modal fade" id="theme-create" role="dialog"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<h4 class="modal-title" id="theme-label">
					<i class="fa fa-plus"></i><i class="fa fa-paint-brush"></i><s:message code="theme.create"/>
				</h4>
			</div>
			<form id="theme-new" action="<c:url value="/manage/createTheme"/>" 	method="POST">
			<div class="modal-body">
					<div class="form-group">
						<label><s:message code="main.name" text="Name" /></label> <input
							id="name" name="name" class="form-control">
					</div>
					<div class="form-group">
						<label><s:message code="theme.font" text="Font" /></label>
						<div class="dropdown">
							<button id="font_button" class="btn btn-default " type="button"
								id="fontMenu" data-toggle="dropdown">
								<div id="theme-font">
								</div>
							</button>
							<ul class="dropdown-menu" role="menu" aria-labelledby="fontMenu">
								<c:forEach items="${fonts}" var="font">
									<li><a class="fontType" tabindex="-1" href="#"
										id="${font}" data-font="${font}"> <span
											style="${font.cssFamily}">${font.fontFamily}</span>
									</a></li>
								</c:forEach>
							</ul>
						</div>
						<input name="font" id="font" type="hidden">
					</div>
					<div class="form-group">
						<label><s:message code="theme.color" text="Color" /></label>
						<input
							id="color" name="color" class="form-control mini-colors" value="#000000">
					</div>
					<div class="form-group">
						<label for="invcolor" ><s:message code="theme.invcolor" text="Inverse Color" /></label>
						<input value="#ffffff"
							id="invcolor" name="invcolor" class="form-control mini-colors">
						<span class="help-block"><s:message code="theme.invcolor.help"/></span>
					</div>
				
			</div>
			<div class="modal-footer">
				<a class="btn" data-dismiss="modal"><s:message
						code="main.cancel" /></a>
				<button id="create" class="btn btn-default" type="submit">
					<s:message code="main.create" />
				</button>
			</div>
			</form>
		</div>
	</div>
</div>
<script>
$('input.mini-colors').minicolors();

$("#createBtn").click(function() {
	var msg = '<s:message code="theme.font.choose"/>&nbsp;<span class="caret">';
	$("#theme-font").html(msg);
	$("#name").val("");
	$("#font").val("");
});
$(".fontType").click(function(){
	var font = $(this).data('font');
 	$("#theme-font").html($(this).html() + " <span class='caret'>");
	$("#font").val(font);
});
</script>

