<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="hero-unit" style="text-align: center">
	<h3>
		<s:message code="view.index.title" />
	</h3>
	<div id="carousel" class="carousel slide" data-ride="carousel"
		data-interval="5000">
		<!-- Indicators -->
		<ol class="carousel-indicators">
			<li data-target="#carousel" data-slide-to="0" class="active"></li>
			<li data-target="#carousel" data-slide-to="1"></li>
			<li data-target="#carousel" data-slide-to="2"></li>
			<li data-target="#carousel" data-slide-to="3"></li>
		</ol>
		<!-- Wrapper for slides -->
		<div class="carousel-inner" role="listbox">
			<div class="item active centered">
				<img class="centered"
					src="<c:url value="/resources/img/tasq.png"/>">
			</div>
			<div class="item">
				<img class="centered"
					src="<c:url value="/resources/img/tasq_1.png"/>">
			</div>
			<div class="item">
				<img class="centered"
					src="<c:url value="/resources/img/tasq_2.png"/>">
			</div>
			<div class="item">
				<img class="centered"
					src="<c:url value="/resources/img/tasq_3.png"/>">
			</div>
		</div>
		<!-- Controls -->
		<a class="left carousel-control" href="#carousel" role="button"
			data-slide="prev"> <i class="fa fa-2x fa-chevron-left"
			aria-hidden="true" style="color: black"></i>
		</a> <a class="right carousel-control" href="#carousel" role="button"
			data-slide="next"> <i class="fa fa-2x fa-chevron-right"
			aria-hidden="true" style="color: black"></i>
		</a>
	</div>
	<p>
		<s:message code="view.index.desc" htmlEscape="false" />
		<a
			href="https://github.com/q-programming/spring-mvc-quickstart-archetype.git">
			Spring MVC 4 Quickstart Maven Archetype</a> <br>
<%-- 		<s:message code="view.index.desc.2" /> --%>
	</p>
	<p>
		<a href='<s:url value="/sigin" />' class="btn btn-large btn-success"><s:message
				code="menu.signin" /></a>
		<s:message code="view.index.or" />
		&nbsp;<a href='<s:url value="/signup" />'
			class="btn btn-large btn-success"><s:message code="signup.signup" /></a>
	</p>
</div>