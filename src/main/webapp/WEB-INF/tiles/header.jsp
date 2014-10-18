<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="email_txt">
	<s:message code="signup.email" />
</c:set>
<c:set var="password_txt">
	<s:message code="signup.password" />
</c:set>
<security:authentication property="principal" var="user" />
<div class="navbar navbar-fixed-top theme">
	<div class="container">
		<button type="button" class="navbar-toggle" data-toggle="collapse"
			data-target=".nav-collapse">
			<span class="icon-bar"></span> <span class="icon-bar"></span> <span
				class="icon-bar"></span>
		</button>
		<a class="navbar-brand" href="<c:url value="/"/>"
			style="padding-top: 0px; padding-bottom: 4px"><img
			src="<c:url value="/resources/img/tasQ_logo_small.png"/>"></img></a>
		<div class="nav-collapse collapse">
			<security:authorize access="isAuthenticated()">
			<ul class="nav navbar-nav">
				<%-- PROJECTS --%>
				<li><div class="dropdown"
						style="padding-top: 5px; padding: 5px">
						<a class="dropdown-toggle btn theme" type="button"
							id="dropdownMenu1" data-toggle="dropdown"><s:message
								code="project.projects" text="Projects" /> <span
							class="caret theme"></span></a>
						<ul class="dropdown-menu">
							<li><strong style="padding: 5px 10px;"><s:message
										code="project.recent" text="Recent projects" /></strong></li>
							<c:forEach items="${last_projects}" var="l_project">
								<c:if test="${l_project.id eq user.active_project}">
									<li><a href="<c:url value="/project?id=${l_project.id}"/>"><b>[${l_project.projectId}]
												${l_project.name}</b></a></li>
								</c:if>
								<c:if test="${l_project.id ne user.active_project}">
									<li><a href="<c:url value="/project?id=${l_project.id}"/>">[${l_project.projectId}]
											${l_project.name}</a></li>
								</c:if>
							</c:forEach>
							<li role="presentation" class="divider"></li>
							<li style="margin: 10px;"><a
								href="<c:url value="/projects"/>"><span
									class="glyphicon glyphicon-list"></span> <s:message
										code="project.showAll" text="Projects" /></a></li>
							<c:if test="${user.isUser == true}">
								<li style="margin: 10px;"><a
									href="<c:url value="/project/create"/>"><span
										class="glyphicon glyphicon-plus"></span> <s:message
											code="project.create" text="Create project" /></a></li>
							</c:if>
						</ul></li>
						
				<%-- TASKS --%>
				<li><div class="dropdown" style="padding-top: 5px">
						<a class="dropdown-toggle btn theme" type="button"
							id="dropdownMenu1" data-toggle="dropdown"><s:message
								code="task.tasks" text="Tasks" /> <span class="caret theme"></span></a>
						<ul class="dropdown-menu">
							<c:forEach items="${last_tasks}" var="l_task">
								<li><a href="<c:url value="/task?id=${l_task.id}"/>">[${l_task.id}]
										${l_task.name}</a></li>
							</c:forEach>
							<li role="presentation" class="divider"></li>
							<li style="margin: 10px;"><a href="<c:url value="/tasks"/>"><span
									class="glyphicon glyphicon-list"></span> <s:message
										code="task.showAll" text="Show all" /></a></li>

							<c:if test="${user.isReporter == true}">
							<li style="margin: 10px;"><a
								href="<c:url value="/task/create"/>"><span
									class="glyphicon glyphicon-plus"></span> <s:message
										code="task.create" text="Create task" /></a></li>
							</c:if>
						</ul></li>
				<%--AGILE --%>
				<li><div class="dropdown" style="padding-top: 5px">
						<a class="dropdown-toggle btn theme" type="button"
							id="dropdownMenu1" data-toggle="dropdown"><s:message
								code="agile.agile" text="Agile" /> <span class="caret theme"></span></a>
						<ul class="dropdown-menu">
							<c:forEach items="${last_projects}" var="l_project">
								<c:if test="${l_project.id eq user.active_project}">
									<li><a href="<c:url value="/agile/${l_project.id}/"/>"><b>${l_project.name}
												(<s:message code="agile.board.${l_project.agile_type}" />)
										</b></a></li>
								</c:if>
								<c:if test="${l_project.id ne user.active_project}">
									<li><a href="<c:url value="/agile/${l_project.id}/"/>">${l_project.name}
											(<s:message code="agile.board.${l_project.agile_type}" />)
									</a></li>
								</c:if>
							</c:forEach>
							<li role="presentation" class="divider"></li>
							<li style="margin: 10px;"><a href="<c:url value="/tasks"/>"><span
									class="glyphicon glyphicon-list"></span> <s:message
										code="agile.showAll" text="Show all" /></a></li>
						</ul></li>
				<%--Create task button --%>
				<c:if test="${user.isReporter == true}">
				<li>
					<div style="padding-top: 8px;">
						<a class="btn btn-xs theme a-tooltip"
							title="<s:message
								code="task.create" text="Create task" />" data-placement="bottom"
							href="<c:url value="/task/create"/>" style="padding: 5px 15px;border:1px solid">
							<span class="glyphicon glyphicon-plus"></span>
							<span class="glyphicon glyphicon-th-list"></span></a>
					</div>
				</li>
				</c:if>
			</ul>
			</security:authorize>
			<ul class="nav navbar-nav pull-right">
				<security:authorize access="!isAuthenticated()">
					<li>
						<div class="dropdown" style="padding-top: 5px">
							<a class="dropdown-toggle btn theme" type="button"
								id="dropdownMenu1" data-toggle="dropdown"><s:message
									code="menu.signin" text="Sign in" /></a>
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
					<li><form class="form-search form-inline"
							action="<s:url value="/tasks"></s:url>">
							<input type="text" name="query"
								class="form-control search-query input-sm"
								placeholder="<s:message code="task.search"/>"
								style="border-radius: 10px" />
						</form></li>
					<li>
						<div>
							<a class="btn btn-default btn-xxs a-tooltip"
								href='<s:url value="/users"></s:url>'
								title="<s:message
									code="menu.users" text="Settings" />"
								data-placement="bottom"><span
								class="glyphicon glyphicon-user"></span></a> 
							<a class="btn btn-default btn-xxs a-tooltip"
								href='<s:url value="/settings"></s:url>'
								title="<s:message
									code="menu.settings" text="Settings" />"
								data-placement="bottom"><span
								class="glyphicon glyphicon-cog"></span></a> 
							<a class="btn btn-default btn-xxs a-tooltip"
								href='<s:url value="/settings"></s:url>'
								title="<s:message
									code="menu.help" text="Help" />"
								data-placement="bottom"><span
								class="glyphicon glyphicon-question-sign"></span></a> 
							<a
								class="btn btn-default btn-xxs a-tooltip"
								href='<s:url value="/logout"></s:url>'
								title="<s:message
									code="menu.logout" text="Log out" />"
								data-placement="bottom"><span
								class="glyphicon glyphicon-off"></span></a>
						</div>
						<div style="color: white">${user}</div>
					</li>
					<li>
						<div class="pull-right">
							<img src="<c:url value="/userAvatar"/>"
								style="height: 50px; padding-left: 5px;"></img>
						</div>
					</li>
				</security:authorize>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
</div>