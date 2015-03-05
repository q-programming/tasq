<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<div class="white-frame" style="width: 500px">
	<h3>
		<s:message code="panel.settings"></s:message>
	</h3>
	<security:authentication property="principal" var="user" />
	<form id="panelForm" name="panelForm" method="post"
		enctype="multipart/form-data">
		<div style="display: table-row; padding: 5px">
			<div>
				<div id="avatar"
					style="border: 1px dashed; display: table-cell; text-align: center; min-width: 110px;">
					<img id="avatar_src" src="<c:url value="/../avatar/${user.id}"/>"
						style="padding: 10px;"></img>
					<div id="avatar_upload" class="hidden" style="margin-top: -30px">
						<input id="file_upload" name="avatar" type="file" accept=".png"
							title="Change avatar" class="inputfiles">
					</div>
				</div>
				<div style="display: table-cell; padding-left: 20px">
					<h3>${user}</h3>
					<i>${user.email}</i>
				</div>
			</div>
			<div style="display: table-row;">
				<div>
					<div class="mod-header">
						<h5 class="mod-header-title">
							<i class="fa fa-envelope-o"></i>
							<s:message code="panel.emails"></s:message>
						</h5>
					</div>
					<div class="checkbox">
						<label class="checkbox" style="display: inherit;"> <input
							type="checkbox" name="emails" id="emails" value="true"
							<c:if test="${user.email_notifications}">checked</c:if>>
							<i class="fa fa-envelope"></i> <s:message
								code="panel.recieveEmails"></s:message>
						</label> <span class="help-block"><s:message
								code="panel.emails.help" /></span>
					</div>
				</div>
				<div>
					<div class="mod-header">
						<h5 class="mod-header-title">
							<i class="fa fa-globe"></i>
							<s:message code="panel.language"></s:message>
						</h5>
					</div>
					<div class="" style="width: 150px; padding-left: 20px">
						<select class="form-control input-sm" name="language">
							<option value="en"
								<c:if test="${user.language eq 'en'}">selected</c:if>><s:message
									code="lang.en" text="English" /></option>
							<option value="pl"
								<c:if test="${user.language eq 'pl'}">selected</c:if>><s:message
									code="lang.pl" text="Polish" /></option>
						</select>
					</div>
					<span style="padding-left: 20px" class="help-block"><s:message
							code="panel.language.help" /></span>
				</div>
				<div>
					<div class="mod-header">
						<h5 class="mod-header-title">
							<i class="fa fa-paint-brush"></i>
							<s:message code="panel.theme"></s:message>
						</h5>
					</div>
					<div style="width: 350px; margin-top: 5px; padding-left: 20px">
						<select class="form-control input-sm" name="theme">
							<option value="" <c:if test="${empty user.theme}">selected</c:if>><s:message
									code="panel.theme.darkblue" /></option>
							<option value="lightblue"
								<c:if test="${user.theme eq 'lightblue'}">selected</c:if>><s:message
									code="panel.theme.lightblue" /></option>
							<option value="green"
								<c:if test="${user.theme eq 'green'}">selected</c:if>><s:message
									code="panel.theme.green" /></option>
							<option value="red"
								<c:if test="${user.theme eq 'red'}">selected</c:if>><s:message
									code="panel.theme.red" /></option>
						</select> <span class="help-block"><s:message
								code="panel.theme.help" /></span>
					</div>
				</div>
			</div>
		</div>
		<div style="text-align: center;">
			<button class="btn btn-success" type="submit">
				<s:message code="panel.save" text="Save settings" />
			</button>
		</div>
	</form>
	<jsp:include page="../other/invite.jsp" />
</div>
<script>
	$(document).ready(function($) {
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
	});

	
</script>