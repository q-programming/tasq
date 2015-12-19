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
					<img id="avatar_src"
						src="<c:url value="/../avatar/${user.id}.png"/>"
						style="padding: 10px;border-radius:50%"></img>
					<div id="avatar_upload" class="hidden" style="margin-top: -30px">
						<input id="file_upload" name="avatar" type="file" accept=".png"
							title="Change avatar" class="inputfiles">
					</div>
				</div>
				<div style="display: table-cell; padding-left: 20px">
					<h3>
						<c:if test="${not user.confirmed}">
							<i style="color: red"
								class="fa fa-exclamation-triangle a-tooltip"
								title="<s:message code="panel.emails.notconfirmed"/>"></i>
						</c:if>
						${user}
					</h3>
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
					<div class="" style="padding-left: 20px">
						<div id="email-group" class="form-inline">
							<div class="form-group">
								<div class="input-group">
									<div class="input-group-addon">
										<i class="fa fa-envelope-o"></i>
									</div>
									<input id ="email" name="email" type="text" class="form-control"
										placeholder="e-mail" value="${user.email}" style="width:400px">
									<input
										id="useremail" type="hidden" value="${user.email}">
								</div>

							</div>
						</div>
						<div id="notConfirmed" <c:if test="${user.confirmed}"> style="display:none" </c:if>>
							<span style="color: red"> <s:message
									code="panel.emails.notconfirmed" />
							&nbsp;
							<a href="<c:url value="/emailResend"/>" class="btn btn-default"><i class="fa fa-reply"></i><i
									class="fa fa-envelope"></i>&nbsp;<s:message
										code="panel.emails.resend" /></a>
							</span>
						</div>
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
							<c:forEach items="${themes}" var="theme">
								<option value="${theme.id}"
									<c:if test="${theme.name eq user.theme.name}">selected</c:if>>
									${theme.name}</option>
							</c:forEach>
						</select> <span class="help-block"><s:message
								code="panel.theme.help" /></span>
					</div>
				</div>
			</div>
		</div>
		<div style="text-align: center;">
			<button class="btn btn-success" type="submit">
				<i class="fa fa-floppy-o"></i>&nbsp;
				<s:message code="panel.save" text="Save settings" />
			</button>
		</div>
	</form>
	<jsp:include page="../other/invite.jsp" />
</div>
<script>
	var settings = true;
	$(document).ready(function($) {
		$( "#email" ).change(function() {
				var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
				if (!regex.test(this.value)) {
					$("#email-group").removeClass('has-success');
					$("#email-group").addClass('has-error');
				}else{
					$("#email-group").removeClass('has-error');
					$("#email-group").addClass('has-success');
				}
		});
		
		
		
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
				if (input.files[0].size > 100000) {
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