<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<security:authentication property="principal" var="user" />
<nav class="navbar navbar-fixed-top theme">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="<c:url value="/"/>"
				style="padding-top: 0px; padding-bottom: 4px;"><img
				src="<c:url value="/../avatar/logo.png"/>"
				style="height: 50px; margin-top: 1px;"></img></a> <span
				class="theme-text" style="font-size: xx-large">Tasker</span>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right hidden-xs">
				<security:authorize access="isAuthenticated()">
					<li class="hidden-sm"><a href="#" class="header-date"
						data-toggle="dropdown"><span id="header_time_span"
							class="header-time"><span class="theme-inv-text">00:00</span></span></a>
						<ul class="dropdown-menu" style="margin-top: 3px;">
							<li><div id="header-date"></div></li>
						</ul></li>
					<!-- 				Events -->
					<li><c:if test="${eventCount eq 0}">
							<a class="theme a-tooltip" href='<c:url value="/events"/>'
								title="<s:message
									code="events.events" />"> <i
								class="fa fa-bell-o"></i>
							</a>
						</c:if> <c:if test="${eventCount gt 0}">
							<a href='<c:url value="/events"/>' class="theme a-tooltip"
								title="<s:message
									code="events.events" />"><i
								class="fa fa-bell"></i>&nbsp;${eventCount}</a>
						</c:if></li>
					<li>
						<div class="pull-right" style="margin-right: 40px">
							<a href="#" class="theme nav-dropdown" data-toggle="dropdown">
								<img src="<c:url value="/../avatar/${user.id}.png"/>"
								style="height: 50px; padding-left: 5px; border-radius: 50%">&nbsp;<strong>${user}</strong><span
								class="caret theme"></span>
							</a>
							<ul class="dropdown-menu" style="margin-top: 3px;">
								<li><a href='<c:url value="/settings"/>'><i
										class="fa fa-cog"></i> <s:message code="menu.settings"
											text="Settings" /></a></li>
								<li><c:if test="${eventCount eq 0}">
										<a href='<c:url value="/events"/>'> <i
											class="fa fa-bell-o"></i>&nbsp;<s:message
												code="events.events" />
										</a>
									</c:if> <c:if test="${eventCount gt 0}">
										<a href='<c:url value="/events"/>'> <i class="fa fa-bell"></i>&nbsp;<s:message
												code="events.events" />&nbsp;(${eventCount})
										</a>
									</c:if></li>
								<li><a href='<c:url value="/watching"/>'> <i
										class="fa fa-eye"></i>&nbsp;<s:message code="events.watching" />
								</a></li>
								<li class="divider"></li>
								<li><a href='<s:url value="/logout"></s:url>'><i
										class="fa fa-power-off"></i> <s:message code="menu.logout"
											text="Log out" /></a></li>
							</ul>
							<%-- Logged in user --%>
						</div>
					</li>
				</security:authorize>
				<security:authorize access="!isAuthenticated()">
					<li><div style="padding-top: 5px; padding: 5px">
							<a class="btn login theme" href="<c:url value="/signin"></c:url>"><s:message
									code="menu.signin" /></a>
						</div></li>
				</security:authorize>
			</ul>
			<security:authorize access="isAuthenticated()">
				<form class="navbar-form navbar-right custom-search hidden-xs" id="searchForm"
					action="<s:url value="/tasks"></s:url>">
					<div class="form-group has-feedback">
						<input id="searchField" type="text" name="query"
							class="form-control nav-search search-query input-sm"
							placeholder="<s:message code="task.search"/>" /> <span
							class="fa fa-2x fa-search theme-text form-control-feedback"
							style="margin-top: 10px"></span>
					</div>
					<div id="tagsLoading" class="ui-widget-content ui-corner-all"
						style="display: none; position: absolute; width: 208px;">
						<i class="fa fa-cog fa-spin"></i>&nbsp;
						<s:message code="main.loading" />
					</div>
				</form>
			</security:authorize>
			<!-- 			MOBILE MENU -->
			<ul class="nav navbar-nav navbar-right visible-xs">
				<li><a href="<c:url value="/task/create"/>" class="theme"><i
						class="fa fa-plus"></i>&nbsp;<s:message code="task.create"
							text="Create task" /></a></li>
				<li><a href="<c:url value="/projects"/>" class="theme"><i
						class="fa fa-list"></i>&nbsp;<s:message code="project.projects"
							text="Projects" /></a></li>
				<li><a href="<c:url value="/tasks"/>" class="theme"><i
						class="fa fa-lg fa-check-square"></i>&nbsp;<s:message
							code="task.tasks" text="Tasks" /></a></li>
				<li><a href="<c:url value="/boards"/>" class="theme"><i
						class="fa fa-lg fa-desktop"></i>&nbsp;<s:message
							code="agile.agile" text="Agile" /></a></li>
				<li role="presentation" class="divider"></li>
				<li><a href='<c:url value="/settings"/>' class="theme"><i
						class="fa fa-cog"></i> <s:message code="menu.settings"
							text="Settings" /></a></li>
				<li><c:if test="${eventCount eq 0}">
						<a class="theme a-tooltip" href='<c:url value="/events"/>'
							title=""> <i class="fa fa-bell-o">&nbsp;<s:message
									code="events.events" /></i>
						</a>
					</c:if> <c:if test="${eventCount gt 0}">
						<a href='<c:url value="/events"/>' class="theme a-tooltip"
							title=""><i class="fa fa-bell"></i>&nbsp;<s:message
								code="events.events" />&nbsp;(${eventCount})</a>
					</c:if></li>
				<li role="presentation" class="divider"></li>
				<li><a href='<c:url value="/logout"></c:url>' class="theme"><i
						class="fa fa-power-off"></i> <s:message code="menu.logout"
							text="Log out" /></a></li>
			</ul>
		</div>
	</div>
</nav>
<script>
	$(document).ready(function($) {
		$("#header-date").datepicker();
		$(".header-time").click(function() {
			$('#header-date').toggle("blind");
		});

		// 			$("#header_date_span").text(
		// 					$.datepicker.formatDate('d m yy', new Date()));
		setTimeout(display_time, 1000);

	});
	function display_time() {
		var currentTime = new Date();
		var hours = currentTime.getHours();
		var minutes = currentTime.getMinutes();
		// 		var seconds = currentTime.getSeconds();
		var time = ('0' + hours).slice(-2) + ':' + ('0' + minutes).slice(-2);
		$("#header_time_span").text(time);
		setTimeout(display_time, 1000);
	}

	
</script>