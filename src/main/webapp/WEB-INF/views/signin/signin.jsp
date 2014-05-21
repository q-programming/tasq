<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<c:set var="email_txt">
	<s:message code="signup.email" />
</c:set>
<c:set var="password_txt">
	<s:message code="signup.password" />
</c:set>

<form class="form-narrow form-horizontal" action='<s:url value="/j_spring_security_check"/>' method="post">
    <c:if test="${not empty param['error']}">
        <div class="alert alert-error">
            <s:message code="error.signin" />
        </div>
    </c:if>
    <fieldset>
        <legend>Please Sign In</legend>
        <div class="form-group">
            <label for="inputEmail" class="col-lg-2 control-label">Email</label>
            <div class="col-lg-10">
                <input type="text" class="form-control" id="inputEmail" placeholder="${email_txt}" name="j_username">
            </div>
        </div>
        <div class="form-group">
            <label for="inputPassword" class="col-lg-2 control-label">Password</label>
            <div class="col-lg-10">
                <input type="password" class="form-control" id="inputPassword" placeholder="${password_txt}" name="j_password">
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-10">
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="_spring_security_remember_me"><s:message code="menu.remmember" />
                    </label>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-10">
                <button type="submit" class="btn btn-default"><s:message code="menu.signin" /></button>
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-10">
                <p><s:message code="signup.new" /> <a href='<s:url value="/signup"/>'><s:message code="signup.signup" /></a></p>
            </div>
        </div>
    </fieldset>
</form>
