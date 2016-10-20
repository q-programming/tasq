<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>

<div class="hero-unit" style="text-align: center">
    <h3>
        <s:message code="view.index.title" arguments="${applicationName}"/>
    </h3>
    <div class="white-frame" style="width: 700px">
        <p>
            Unfortunately signup is currently disabled.<br>Tasker will be used in bug contest which will happen in
            November, please visit Tasker tour page,
            where you can see how application looks like and can view detailed help page
            <p>
                <a class="more-link" href="http://q-programming.pl/tasker-tour" target="_blank">
                    <i class="fa fa-graduation-cap" aria-hidden="true"></i>Tasker tour
                </a>
            </p>
        </p>
        <p>If you are more interested in application after visiting tour page and will not participate in mentioned
            competition please contact me </p>
        <p><a href="mailto:jakub.romaniszyn@gmail.com"><i class="fa fa fa-envelope"></i>&nbsp;jakub.romaniszyn@gmail.com</a>
        </p>
    </div>
</div>