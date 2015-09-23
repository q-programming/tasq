<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div
	style="padding-top: 20px">
	<div class="mod-header">
		<h5 class="mod-header-title">
			<i class="fa fa-users"></i>
			<s:message code="panel.invite" />
		</h5>
	</div>
	<span style="padding-left: 20px" class="help-block"><s:message
			code="panel.invite.help" /></span>
	<form action="<c:url value="/inviteUsers"></c:url>" id="invite">
		<div style="display: table-row;">
			<div id="invite_div" style="display: table-cell">
				<input name="email" id="inviteField" class="form-control input-italic"
					type="text" placeholder="<s:message
							code="panel.invite" />"
					style="width: 300px; margin-left: 20px;">

			</div>
			<div style="display: table-cell">
				<i id="valid" class="fa fa-check"
					style="display: none; color: green; padding-left: 10px"></i> <i
					id="invalid" class="fa fa-times"
					style="display: none; color: red; padding-left: 10px"></i>
			</div>
			<div style="display: table-cell; padding-left: 20px">
				<button id="inviteBtn" type="submit" class="btn btn-default"
					disabled="disabled">
					<i class="fa fa-user-plus"></i>
					<s:message code="panel.invite" />
				</button>
			</div>
		</div>
	</form>
</div>
<script>
	$("form#invite :input").each(function() {
		$('#inviteField').blur(function() {
			var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
				if (regex.test(this.value)) {
					console.log("valid");
					$("#invite_div").removeClass('has-error');
					$("#invite_div").addClass('has-success');
					$("#valid").show();
					$("#invalid").hide();
					$("#inviteBtn").removeAttr("disabled");
				} else {
					$("#invite_div").addClass('has-error');
					$("#invite_div").removeClass('has-success');
					$("#valid").hide();
					$("#invalid").show();
					$("#inviteBtn").attr("disabled", 'disabled');
				}
		});
	});
</script>