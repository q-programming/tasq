<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<c:set var="email_txt">
	<s:message code="signup.email" />
</c:set>
<c:set var="password_txt">
	<s:message code="signup.password" />
</c:set>
<div class="navbar navbar-fixed-top">
	<div class="container">
		<button type="button" class="navbar-toggle" data-toggle="collapse"
			data-target=".nav-collapse">
			<span class="icon-bar"></span> <span class="icon-bar"></span> <span
				class="icon-bar"></span>
		</button>
		<a class="navbar-brand" href="<c:url value="/"/>"
			style="padding-top: 0px; padding-bottom: 0px"><img
			src="<c:url value="/resources/img/tasQ_logo_small.png"/>"></img></a>
		<div class="nav-collapse collapse">
			<ul class="nav navbar-nav">
				<li><div class="dropdown" style="padding-top: 5px">
						<a class="dropdown-toggle btn btn-default" type="button"
							id="dropdownMenu1" data-toggle="dropdown"><s:message
								code="project.projects" text="Projects" /><span class="caret"></span></a>
						<ul class="dropdown-menu">
							<c:forEach items="${last_projects}" var="project">
								<li><a href="<c:url value="project?id=${project.id}"/>">[${project.id}]
										${project.name}</a></li>
							</c:forEach>
							<li role="presentation" class="divider"></li>
							<li style="margin: 10px;"><a href="#"><s:message
										code="project.showAll" text="Projects" /></a></li>
							<li style="margin: 10px;"><a
								href="<c:url value="project/create"/>"><s:message
										code="project.create" text="Create project" /></a></li>
						</ul></li>
			</ul>
			<!-- 			<ul class="nav navbar-nav"> -->
			<%-- 				<li class="active"><a href="#"><span --%>
			<%-- 						class="glyphicon glyphicon-home"></span> Home</a></li> --%>
			<!-- 				<li><a href="#about">About</a></li> -->
			<!-- 				<li><a href="#contact">Contact</a></li> -->
			<!-- 			</ul> -->
			<ul class="nav navbar-nav pull-right">
				<security:authorize access="!isAuthenticated()">
					<li>
						<div class="dropdown" style="padding-top: 5px">
							<a class="dropdown-toggle" type="button" id="dropdownMenu1"
								data-toggle="dropdown"><s:message code="menu.signin"
									text="Sign in" /></a>
							<ul class="dropdown-menu">
								<li style="margin: 10px;">
									<form action='<s:url value="/j_spring_security_check"/>'
										method="post">
										<div class="form-group">
											<div class="col-xs-2" style="padding-bottom: 5px">
												<input type="text" class="form-control input-sm"
													id="inputEmail" placeholder="${email_txt}"
													name="j_username">
											</div>
											<div class="col-xs-2 signin">
												<input type="password" class="form-control input-sm"
													id="inputPassword" placeholder="${password_txt}"
													name="j_password">
											</div>
											<div class="col-xs-2 signin">
												<div class="checkbox">
													<label> <input type="checkbox"
														name="_spring_security_remember_me"> <s:message
															code="menu.remmember" />
													</label>
												</div>
											</div>
											<div class="col-xs-2 signin">
												<button type="submit" class="btn btn-default btn-sm">
													<s:message code="menu.signin" />
												</button>
											</div>
										</div>
									</form>
								</li>
							</ul>
						</div>
					</li>
				</security:authorize>
				<%-- Logged in user --%>
				<security:authorize access="isAuthenticated()">
					<security:authentication property="principal" var="user" />
					<li>
						<div>
							<a class="btn btn-default btn-xxs a-tooltip"
								href='<s:url value="/settings"></s:url>'
								title="<s:message
									code="menu.settings" text="Settings" />"
								data-placement="bottom"><span
								class="glyphicon glyphicon-cog"></span></a> <a
								class="btn btn-default btn-xxs a-tooltip"
								href='<s:url value="/settings"></s:url>'
								title="<s:message
									code="menu.help" text="Help" />"
								data-placement="bottom"><span
								class="glyphicon glyphicon-question-sign"></span></a> <a
								class="btn btn-default btn-xxs a-tooltip"
								href='<s:url value="/logout"></s:url>'
								title="<s:message
									code="menu.logout" text="Log out" />"
								data-placement="bottom"><span
								class="glyphicon glyphicon-off"></span></a>
						</div>
						<div>${user}</div>
					</li>
					<li>
						<div class="pull-right">
							<img src="<c:url value="${user.avatar}"/>" style="height: 50px"></img>
						</div>
					</li>
				</security:authorize>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
</div>