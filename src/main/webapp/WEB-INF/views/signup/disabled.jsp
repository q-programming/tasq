<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>

<div class="hero-unit" style="text-align: center">
    <h3>
        <s:message code="view.index.title" arguments="${applicationName}"/>
    </h3>
    <div class="white-frame">
        <p>
            Unfortunately signup is currently disabled, Tasker will be used in bug contest which will happen in
            October
        </p>
        <p>If you are interested in viewing application and will not participate in mentioned competistion please
            contact me </p>
        <p><a href="mailto:jakub.romaniszyn@gmail.com"><i class="fa fa fa-envelope"></i>&nbsp;jakub.romaniszyn@gmail.com</a>
        </p>
    </div>
</div>