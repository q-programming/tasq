<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<div class="white-frame"
	style="height: 570px; overflow: auto; width: 600px">
	<security:authentication property="principal" var="user" />
	<h3>
		<s:message code="panel.settings"></s:message>
	</h3>
	<hr>
	<form id="panelForm" name="panelForm" method="post"
		enctype="multipart/form-data">
		<div style="overflow: auto; padding: 5px">
			<div id="avatar"
				style="border: 1px dashed; display: inline-block; text-align: center; min-width: 110px; float: left">
				<img id="avatar_src" src="<c:url value="/userAvatar"/>"
					style="padding: 10px;"></img>
				<div id="avatar_upload" class="hidden" style="margin-top: -30px">
					<input id="file_upload" name="avatar" type="file"
						title="Change avatar" class="inputfiles">
				</div>
			</div>
			<div style="float: left; margin-left: 10px">
				<h3>${user}</h3>
				<i>${user.email}</i>
				<hr>
				<div>
					<h4>
						<s:message code="panel.emails"></s:message>
					</h4>
					<div class="checkbox">
						<label class="checkbox" style="display: inherit;"> <input
							type="checkbox" name="emails" id="emails" value="true"
							<c:if test="${user.email_notifications}">checked</c:if>>
							<span class="glyphicon glyphicon-envelope"></span> <s:message
								code="panel.recieveEmails"></s:message>
						</label>
					</div>
				</div>
				<hr>
				<div>
					<h4>
						<s:message code="panel.language"></s:message>
					</h4>
					<div style="width: 150px">
						<select class="form-control input-sm" name="language">
							<option value="en"
								<c:if test="${user.language eq 'en'}">selected</c:if>><s:message
									code="lang.en" text="English" /></option>
							<option value="pl"
								<c:if test="${user.language eq 'pl'}">selected</c:if>><s:message
									code="lang.pl" text="Polish" /></option>
						</select>
					</div>
				</div>
				<hr>
				<div>
					<h4>
						<s:message code="panel.theme"></s:message>
					</h4>
					<div style="width: 350px">
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
						</select>
					</div>
				</div>
			</div>
		</div>
		<hr>
		<div style="text-align: center;">
			<button class="btn btn-success" type="submit">
				<s:message code="panel.save" text="Save settings" />
			</button>
		</div>

	</form>
</div>
<script>
	$(document).ready(function($) {
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
				var reader = new FileReader();
				reader.onload = function(e) {
					$('#avatar_src').attr('src', e.target.result);
				}
				reader.readAsDataURL(input.files[0]);
			}
		}

	});

	
</script>