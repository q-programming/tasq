<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="hero-unit">
    <h1><s:message code="view.index.title" /></h1>
    <p>
        <a href='<s:url value="/signup" />'class="btn btn-large btn-success"><s:message code="signup.signup" /></a>
    </p>
</div>