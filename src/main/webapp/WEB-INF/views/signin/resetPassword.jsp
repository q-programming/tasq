<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<c:set var="email_txt">
	<s:message code="signup.email" />
</c:set>
<c:set var="password_txt">
	<s:message code="signup.password" />
</c:set>

<form class="form-narrow form-horizontal" action='<s:url value="/resetPassword"/>' method="post" style="margin-top: 40px;">
    <c:if test="${not empty param['error']}">
        <div class="alert alert-error">
            <s:message code="error.signin" />
        </div>
    </c:if>
    <fieldset>
        <legend><s:message code="signin.password.reset"/></legend>
        <div class="form-group">
            <label for="inputEmail" class="col-lg-2 control-label">Email</label>
            <div class="col-lg-10">
                <input type="text" class="form-control" id="inputEmail" placeholder="${email_txt}" name="email">
            </div>
        </div>
        <div class="form-group">
            <div class="pull-right">
                <button type="submit" class="btn btn-default"><s:message code="main.resetPassword" /></button>
            </div>
        </div>
    </fieldset>
</form>
