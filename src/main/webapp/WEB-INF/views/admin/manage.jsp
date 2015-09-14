<%@page import="com.qprogramming.tasq.manage.Font"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%
	pageContext.setAttribute("fonts", Font.values());
%>
<c:forEach items="${fonts}" var="font">
	${font.link}
</c:forEach>
<link href="<c:url value="/resources/css/jquery.minicolors.css" />"
	rel="stylesheet" media="screen" />

<div class="white-frame"
	style="display: table; width: 100%; height: 80vh">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li class="active"><a style="color: black" href="#"> <i
					class="fa fa-cogs"></i> <s:message code="menu.manage"
						text="Manage application" /></a></li>
			<li><a style="color: black"
				href="<c:url value="/manage/users" />"> <i class="fa fa-users"></i>
					<s:message code="menu.manage.users " text="Manage users" /></a></li>
			<li><a style="color: black" href="#"> <i
					class="fa fa-lg fa-check-square"></i> <s:message
						code="menu.manage.tasks" /></a></li>
		</ul>
	</div>
	<form id="avatarUpload" name="avatarUpload" enctype="multipart/form-data" action="<c:url value="/manage/logoUpload"/>" method="POST">
	<div>
		<h3><i class="fa fa-picture-o"></i>&nbsp;<s:message code="manage.logo" text="Application Avatar"/></h3>
		<div id="avatar" class="bg-preview theme"	>
					<img id="avatar_src" src="<c:url value="/../avatar/logo.png"/>"
						style="padding: 10px; text-align:center"></img>
					<div id="avatar_upload" class="hidden" style="margin-top: -30px">
						<input id="file_upload" name="avatar" type="file" accept=".png"
							title="<s:message code="manage.logo.change"/>" class="inputfiles">
					</div>
		</div>
		<div>
			<span class="help-block">
				<s:message code="manage.logo.help" htmlEscape="false" />
			</span>
		</div>
		<div>
			<a href="<c:url value="/manage/logoRestore"/>" class="btn btn-warning"><i class="fa fa-exclamation-circle"></i>&nbsp;<s:message code="manage.logo.restore" /></a>
			<button type="submit" class="btn btn-success"><i class="fa fa-floppy-o"></i>&nbsp;<s:message code="panel.save" /></button>
		</div>
	</div>
	</form>
	<hr>
	<div>
		<h3>
			<i class="fa fa-paint-brush"></i> <s:message code="theme.themes"/>
		</h3>
		<a id="createBtn" href="#" data-toggle="modal"
			data-target="#theme-create"> <span
			class="btn btn-default pull-right"><i class="fa fa-plus"></i><i
				class="fa fa-paint-brush"></i> <s:message code="theme.create" /></span></a>
		<table class="table table-hover table-condensed">
			<thead class="theme">
				<tr>
					<th><s:message code="main.name" text="Name" /></th>
					<th><s:message code="theme.font.color" text="Font & color" /></th>
					<th style="width: 100px"></th>
				</tr>
			</thead>
			<c:forEach items="${themes}" var="theme">
				<tr>
					<td>${theme.name}</td>
					<td style="${theme.font.cssFamily}">
						<span style="display:block;width:200px;padding: 5px; background-color:${theme.color};color:${theme.invColor}">
							<img id="avatar_src" src="<c:url value="/../avatar/logo.png"/>"
							style="height: 25px;"></img>
							${theme.font.fontFamily}
						</span>
					</td>
					<td >
					<c:if test="${theme.name ne 'Default'}">
						<a href="#" style="color: black"
							class="edit-theme a-tooltip" data-name="${theme.name}"
							data-color="${theme.color}" data-invcolor="${theme.invColor}"
							data-font="${theme.font}" data-themeID="${theme.id}" title="<s:message code="main.edit"/>">
							<i class="fa fa-pencil"></i>
						</a>
					</c:if>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
<div class="modal fade" id="theme-modal" role="dialog"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<h4 class="modal-title" id="theme-label"></h4>
			</div>
			<form id="theme-form" action="<c:url value="/manage/manageTheme"/>"
				method="POST">
				<input id="themeID" name="themeID" type="hidden">
				<div class="modal-body">
					<div class="form-group">
						<label><s:message code="main.name" text="Name" /></label> <input
							id="name" name="name" class="form-control required">
					</div>
					<div class="form-group">
						<label><s:message code="theme.font" text="Font" /></label>
						<div class="dropdown">
							<button id="font_button" class="btn btn-default" type="button"
								id="fontMenu" data-toggle="dropdown">
								<div id="theme-font"></div>
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
						<input name="font" id="font" type="hidden" class="required">
					</div>
					<div class="form-group">
						<label><s:message code="theme.color" text="Color" /></label> <input
							id="color" name="color" class="form-control mini-colors required"
							value="">
					</div>
					<div class="form-group">
						<label for="invcolor"><s:message code="theme.invcolor"
								text="Inverse Color" /></label> <input value="#ffffff" id="invcolor"
							name="invcolor" class="form-control mini-colors required">
						<span class="help-block"><s:message
								code="theme.invcolor.help" /></span>
					</div>

				</div>
				<div>
					<p id="error" style="color:red;text-align: center;"></p>
				</div>
				</form>
				<div class="modal-footer">
					<a class="btn" data-dismiss="modal"><s:message
							code="main.cancel" /></a>
					<button id="theme-action" class="btn btn-default"></button>
				</div>
		</div>
	</div>
</div>
<script src="<c:url value="/resources/js/jquery.minicolors.js" />"></script>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<script>
	$("#createBtn")
			.click(
					function() {
						var msg = '<s:message code="theme.font.choose"/>&nbsp;<span class="caret">';
						var label = '<i class="fa fa-plus"></i><i class="fa fa-paint-brush"></i><s:message code="theme.create" />';
						$("#theme-font").html(msg);
						$("#theme-label").html(label)
						$("#name").val("");
						$("#font").val("");
						$("#color").val("#000000");
						$("#invcolor").val("#FFFFFF");
						$("#themeID").val("");
						$('input.mini-colors').minicolors();
						var btnMsg = '<s:message code="main.create" />';
						$("#theme-action").html(btnMsg);
						$("#error").html("");
						$("#theme-modal").modal('show');

					});
	$(".edit-theme")
			.click(
					function() {
						var name = $(this).data('name');
						var font = $(this).data('font');
						var color = $(this).data('color');
						var invcolor = $(this).data('invcolor');
						var themeID = $(this).data('themeid');

						var label = '<i class="fa fa-paint-brush"></i><s:message code="theme.edit" /> '
								+ name;
						$("#theme-label").html(label)
						$("#theme-font").html(
								$('#' + font).html() + " <span class='caret'>");
						$("#font").val(font);
						$("#name").val(name);
						$("#color").val(color);
						$("#invcolor").val(invcolor);
						$("#themeID").val(themeID);
						$('input.mini-colors').minicolors();
						var btnMsg = '<s:message code="main.edit" />';
						$("#theme-action").html(btnMsg);
						$("#error").html("");
						$("#theme-modal").modal('show');
					});

	$(".fontType").click(function() {
		var font = $(this).data('font');
		$("#theme-font").html($(this).html() + " <span class='caret'>");
		$("#font").val(font);
	});
	$(".fontType").click(function() {
		var font = $(this).data('font');
		$("#theme-font").html($(this).html() + " <span class='caret'>");
		$("#font").val(font);
	});
	
	$("#theme-action").click(function() {
		var valid = true;
		var fields = '<i class="fa fa-exclamation-circle"></i>&nbsp;<s:message code="theme.required"/>&nbsp;';
		var delimiter = "";
		$(".required").each(function() {
			if ($(this).val()==""){
				valid = false;
				fields+= delimiter + $(this).attr('name');
				delimiter = ","
			}
		});
		if(valid){
			$("#theme-form").submit();
		}
		else{
			$("#error").html(fields);
		}
		
	});
	//AVATAR
		var imageWRN = '<s:message code="error.file100kb"/>';
		$("#file_upload").bootstrapFileInput();

		$("#avatar").hover(function() {
			$('#avatar_upload').removeClass('hidden');
		}, function() {
			$('#avatar_upload').addClass('hidden');
		});

		$("#file_upload").change(function() {
			readURL(this);
		});

		function readURL(input) {
			if (input.files && input.files[0]) {
				if(input.files[0].size > 100000){
					showError(imageWRN);
					return false;
				}
				//check before uploading
				var _URL = window.URL || window.webkitURL;
				file = input.files[0];
				img = new Image();
				img.onload = function() {
					if (this.width > 150 || this.height > 150) {
						showError(imageWRN);
					} else {
						var reader = new FileReader();
						reader.onload = function(e) {
							$('#avatar_src').attr('src', e.target.result);
						}
						reader.readAsDataURL(file);
					}
				};
				img.onerror = function() {
					alert("not a valid file: " + file.type);
				};
				img.src = _URL.createObjectURL(file);

			}
		}

	
</script>

