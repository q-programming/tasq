<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="white-frame" style="display: table; width: 100%;height:80vh">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li><a style="color: black"
				href="<c:url value="/manage/app" />"> <i class="fa fa-cogs"></i>
					<s:message code="menu.manage" text="Manage application" /></a></li>
			<li><a style="color: black"
				href="<c:url value="/manage/users" />"> <i class="fa fa-users"></i>
					<s:message code="menu.manage.users " text="Manage users" /></a></li>
			<li class="active"><a style="color: black" href="#"> <i
					class="fa fa-lg fa-check-square"></i> <s:message
						code="menu.manage.tasks" /></a></li>
		</ul>
	</div>
	<div>
		<a class="btn btn-default"
			href="<c:url value="/task/updatelogs"></c:url>"> <span
			class="fa-stack"> <i class="fa fa-repeat fa-stack-2x"></i> <i
				class="fa fa-gear fa-stack-1x"></i>
		</span></a> Reload all logged work on all tasks within application
	</div>
	<hr>
	<div>
		<a class="btn btn-default"
			href="<c:url value="task/updateFinish"></c:url>"> <span
			class="fa-stack"> <i class="fa fa-repeat fa-stack-2x"></i> <i
				class="fa fa-check fa-stack-1x"></i>
		</span></a> Check all tasks and set correct finish date
	</div>
</div>
