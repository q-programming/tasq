<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<security:authentication property="principal" var="user" />
<div class="white-frame"
	style="width: 700px; overflow: auto; display: table">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black" href="<c:url value="/task/create"/>"><span
					class="glyphicon glyphicon-plus"></span> <s:message
						code="task.create" text="Create task" /></a></li>
			<li class="active"><a style="color: black" href="#"><span
					class="glyphicon glyphicon-import"></span> <s:message code="task.import"/></a></li>
		</ul>
	</div>
	<div>
		<samp>
			${logger}
		</samp>
		<span class="btn pull-right" onclick="location.href='<c:url value="/"/>';"><s:message
					code="main.cancel" text="Ok" /></span>
	</div>
</div>
	
