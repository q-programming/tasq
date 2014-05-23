<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<div class="white-frame"
	style="height: 470px; overflow: auto; width: 500px">
	<security:authentication property="principal" var="user" />
	<h3>
		<s:message code="panel.settings"></s:message>
	</h3>
	<hr>
	<form id="panelForm" name="panelForm" method="post">
		<div style="overflow: auto; padding: 5px">
			<div id="avatar"
				style="border: 1px dashed; display: inline-block; text-align: center; min-width: 110px; float: left">
				<img id="avatar_src" src="<c:url value="${user.avatar}"/>"
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
							<c:if test="${user.email_notifications}">checked</c:if>
								>
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
								<c:if test="${user.language eq 'en'}">selected</c:if>>English</option>
							<option value="pl"
								<c:if test="${user.language eq 'pl'}">selected</c:if>>Polski</option>
						</select>
					</div>
				</div>
			</div>
		</div>
		<hr>
		<div style="text-align: center;">
			<button class="btn btn-primary" type="submit">
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