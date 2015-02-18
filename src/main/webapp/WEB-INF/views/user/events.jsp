<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="white-frame"
	style="width: 80%; overflow: auto; display: table">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li class="active"><a style="color: black" href="#"> <i
					class="fa fa-bell"></i> <s:message code="events.events" /></a></li>
			<li><a style="color: black"
				href="<c:url value="/watching" />"> <i class="fa fa-eye"></i>
					<s:message code="events.watching"/></a></li>
		</ul>
	</div>
	<div>
		EVENTS
	</div>
</div>