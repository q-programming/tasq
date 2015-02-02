<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<div class="hero-unit" style="text-align: center">
	<h3>
		<s:message code="view.index.title" />
	</h3>
	<div class="white-frame">
		<p>
			<security:authentication property="principal" var="user" />
			<s:message code="view.index.new" htmlEscape="false"
				arguments="${user}" />
			<br>
			<s:message code="view.index.new.help" />
			<a class="btn btn-default btn-xxs a-tooltip"
				href='<s:url value="/users"></s:url>'
				title="<s:message
									code="menu.users" text="Settings" />"
				data-placement="bottom"><i class="fa fa-user"></i></a>
		</p>
	</div>
</div>