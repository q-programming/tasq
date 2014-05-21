<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<div class="navbar navbar-fixed-top">
	<div class="container">
		<button type="button" class="navbar-toggle" data-toggle="collapse"
			data-target=".nav-collapse">
			<span class="icon-bar"></span> <span class="icon-bar"></span> <span
				class="icon-bar"></span>
		</button>
		<a class="navbar-brand" href="#" style="padding-top: 0px;padding-bottom:0px"><img src="<c:url value="/resources/img/tasQ_logo_small.png"/>"></img></a>
		<div class="nav-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="active"><a href="#"><span
						class="glyphicon glyphicon-home"></span> Home</a></li>
				<li><a href="#about">About</a></li>
				<li><a href="#contact">Contact</a></li>
			</ul>
			<ul class="nav navbar-nav pull-right">
				<security:authorize access="!isAuthenticated()">
					<li>
						<div class="dropdown" style="padding-top: 5px">
							<a class="dropdown-toggle" type="button" id="dropdownMenu1"
								data-toggle="dropdown">Sign in</a>
							<ul class="dropdown-menu">
								<li style="margin: 10px;">
									<form action='<s:url value="/j_spring_security_check"/>'
										method="post">
										<c:if test="${not empty param['error']}">
											<div class="alert alert-error">Sign in error. Please
												try again.</div>
										</c:if>
										<div class="form-group">
											<div class="col-xs-2" style="padding-bottom:5px">
												<input type="text" class="form-control input-sm"
													id="inputEmail" placeholder="Email" name="j_username">
											</div>
											<div class="col-xs-2 signin">
												<input type="password" class="form-control input-sm"
													id="inputPassword" placeholder="Password" name="j_password">
											</div>
											<div class="col-xs-2 signin">
												<div class="checkbox">
													<label> <input type="checkbox"
														name="_spring_security_remember_me"> Remember me
													</label>
												</div>
											</div>
											<div class="col-xs-2 signin">
												<button type="submit" class="btn btn-default btn-sm">Sign
													in</button>
											</div>
										</div>
									</form>
								</li>
							</ul>
						</div>
					</li>
				</security:authorize>

				<security:authorize access="isAuthenticated()">
					<li><a href='<s:url value="/logout"></s:url>'>Logout (<security:authentication
								property="principal.username" />)
					</a></li>
				</security:authorize>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
</div>