<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="email_txt">
	<s:message code="signup.email" />
</c:set>
<c:set var="password_txt">
	<s:message code="signup.password" />
</c:set>
<c:set var="password_txt">
	<s:message code="signup.password" />
</c:set>
<c:set var="confirmPassword_txt">
	<s:message code="signup.confirmPassword" />
</c:set>


<form:form class="form-narrow form-horizontal" method="post"
	modelAttribute="passwordResetForm" style="margin-top: 40px;">
	<c:if test="${not empty param['error']}">
		<div class="alert alert-error">
			<s:message code="error.signin" />
		</div>
	</c:if>
	<fieldset>
		<legend><s:message code="signin.password.new"/></legend>
		<div class="form-group">
			<label for="password" class="col-lg-2 control-label">${password_txt}</label>
			<div class="col-lg-10">
				<form:password path="password" class="form-control" id="password"
					placeholder="${password_txt}" />
				<form:errors path="password" element="p" class="text-danger" />
			</div>
		</div>
		<div class="form-group">
			<label for="password" class="col-lg-2 control-label">${confirmPassword_txt}</label>
			<div class="col-lg-10">
				<form:password path="confirmPassword" class="form-control"
					placeholder="${confirmPassword_txt}" />
				<form:errors path="confirmPassword" element="p" class="text-danger" />
			</div>
		</div>
		<div class="form-group">
			<label for="id" class="col-lg-2 control-label"><s:message code="singin.password.token"/></label>
			<div class="col-lg-10">
				<form:input path="id" class="form-control " disabled="true" />
			</div>
		</div>
		
		<div class="form-group">
			<div class="col-lg-offset-2 col-lg-10">
				<button type="submit" class="btn btn-default">
					<s:message code="main.resetPassword" />
				</button>
			</div>
		</div>
	</fieldset>
</form:form>
