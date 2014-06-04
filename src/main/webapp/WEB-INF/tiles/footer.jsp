<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<security:authentication property="principal" var="user" />
<security:authorize access="isAuthenticated()">
	<c:set var="active_task_seconds">${user.active_task_seconds}</c:set>
<c:if test="${not empty user.active_task }">
	<div class="footer-time">
		<div>
			<s:message code ="task.active"/> <a
				href="<c:url value="task?id=${user.active_task[0]}"/>">${user.active_task[0]}</a>
			<a class="btn btn-default btn-xxs a-tooltip" title="<s:message code="task.stopTime.description"></s:message>"
				href='<c:url value="task/time?id=${user.active_task[0]}&action=stop"/>'> <span
				class="glyphicon glyphicon-time"></span></a> <span class="timer"></span>
		</div>
	</div>
</c:if>
</security:authorize>
<script>
	$(function() {
		$('.a-tooltip').tooltip();
	});
	//add slidedown animation to dropdown menus
	$('.dropdown').on('show.bs.dropdown', function(e) {
		$(this).find('.dropdown-menu').first().stop(true, true).slideDown();
	});

	//add slideup animation to dropdown menus
	$('.dropdown').on('hide.bs.dropdown', function(e) {
		$(this).find('.dropdown-menu').first().stop(true, true).slideUp();
	});
	
	$(document).ready(function($) {
				var task_start_time = "${active_task_seconds}";
				var startTime = new Date(0);
				startTime.setUTCSeconds(task_start_time);
				<%-- only start timer if there is active task--%>
				<c:if test="${active_task_seconds gt 0}">
					setTimeout(display, 1000);
				</c:if>
				

				function display() {
					var endTime = new Date();
					var timeDiff = endTime - startTime;
					timeDiff /= 1000;
					var seconds = Math.round(timeDiff % 60);
					timeDiff = Math.floor(timeDiff / 60);
					var minutes = Math.round(timeDiff % 60);
					timeDiff = Math.floor(timeDiff / 60);
					var hours = Math.round(timeDiff % 24);
					timeDiff = Math.floor(timeDiff / 24);
					var days = timeDiff;
					$(".timer").text(
							days + "d " + hours + "h " + minutes + "m "
									+ seconds + "s");
					setTimeout(display, 1000);
				}
			});
</script>