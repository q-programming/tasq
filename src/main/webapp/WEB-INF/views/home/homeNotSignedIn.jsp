<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="hero-unit" style="text-align: center">
	<h3>
		<s:message code="view.index.title" />
	</h3>
	<img src="http://37.233.101.125/assets/images/projects/tasq.png">
	<p>
		<s:message code="view.index.desc" htmlEscape="false" />
		<a
			href="https://github.com/q-programming/spring-mvc-quickstart-archetype.git">
			Spring MVC 4 Quickstart Maven Archetype</a> <br>
		<s:message code="view.index.desc.2" />
	</p>
	<p>
		<a href='<s:url value="/sigin" />' class="btn btn-large btn-success"><s:message
				code="menu.signin" /></a>
		<s:message code="view.index.or" />
		&nbsp;<a href='<s:url value="/signup" />'
			class="btn btn-large btn-success"><s:message code="signup.signup" /></a>
	</p>
</div>