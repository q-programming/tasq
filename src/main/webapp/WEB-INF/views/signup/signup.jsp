<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="name_txt">
	<s:message code="signup.name" />
</c:set>
<c:set var="surname_txt">
	<s:message code="signup.surname" />
</c:set>
<c:set var="email_txt">
	<s:message code="signup.email" />
</c:set>
<c:set var="password_txt">
	<s:message code="signup.password" />
</c:set>
<c:set var="confirmPassword_txt">
	<s:message code="signup.confirmPassword" />
</c:set>

<form:form class="form-narrow form-horizontal" method="post"
	modelAttribute="signupForm">
	<fieldset>
		<legend>
			<s:message code="signup.header" />
		</legend>
		<form:errors path="" element="p" class="text-danger" />
		<div class="form-group">
			<label for="name" class="col-lg-2 control-label">${name_txt}</label>
			<div class="col-lg-10">
				<form:input path="name" class="form-control"
					cssErrorClass="form-control" id="name" placeholder="${name_txt}" />
				<form:errors path="name" element="p" class="text-danger"  />
			</div>
		</div>
		<div class="form-group">
			<label for="name" class="col-lg-2 control-label">${surname_txt}</label>
			<div class="col-lg-10">
				<form:input path="surname" class="form-control"
					cssErrorClass="form-control" id="surname"
					placeholder="${surname_txt}" />
				<form:errors path="surname" element="p" class="text-danger"  />
			</div>
		</div>

		<div class="form-group">
			<label for="email" class="col-lg-2 control-label">E-mail</label>
			<div class="col-lg-10">
				<form:input path="email" class="form-control"
					cssErrorClass="form-control" id="email" placeholder="${email_txt}" />
				<form:errors path="email" element="p" class="text-danger"  />
			</div>
		</div>
		<div class="form-group">
			<label for="password" class="col-lg-2 control-label">${password_txt}</label>
			<div class="col-lg-10">
				<form:password path="password" class="form-control" id="password"
					placeholder="${password_txt}" />
				<form:errors path="password" element="p" class="text-danger"  />
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
			<div class="col-lg-offset-2 col-lg-10">
				<button type="submit" class="btn btn-default">
					<s:message code="signup.signup" text="Sign up" />
				</button>
			</div>
		</div>
		<div class="form-group">
			<div class="col-lg-offset-2 col-lg-10">
				<p>
					<s:message code="signup.existing" />
					<a href='<s:url value="/signin"/>'><s:message
							code="menu.signin" text="Sign in" /></a>
				</p>
			</div>
		</div>
	</fieldset>
</form:form>
