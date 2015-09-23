<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div class="white-frame"
	style="height: 480px; overflow: auto; width: 600px">
	<div style="display: table">
		<div id="avatar" style="border: 1px dashed; min-width: 110px; text-align: center;">
			<img src="<c:url value="/../avatar/${account.id}.png"/>"
				style="padding: 10px;"></img>
		</div>
		<div
			style="display: table-cell; margin-left: 10px; padding-left: 20px">
			<h3>
			${account}&nbsp;
			<c:if test="${account.online}">
				<i class="fa fa-user a-tooltip" style="color:mediumseagreen" title="<s:message code="main.online"/>"></i>
			</c:if>
			<c:if test="${not account.online}">
				<i class="fa fa-user a-tooltip" style="color:lightgray" title="<s:message code="main.offline"/>"></i>
			</c:if>
			</h3>
			<i class="fa fa-envelope" style="color:black"></i>
			<i><a href="mailto:${account.email}">${account.email}</a></i>
			<div>
				<i class="fa fa-cog"></i>
				<s:message code="role.role"></s:message>
				:
				<s:message code="${account.role.code}" />
			</div>
			<div>
				<i class="fa fa-globe"></i> <s:message code="panel.language"></s:message>
				:
				<s:message code="lang.${account.language}" text="${account.language}"/>
			</div>
		</div>
	</div>
	<div style="height:300px;overflow: auto;">
		<h4><s:message code="project.projects" /></h4>
		<table class="table table-condensed table-hover">
			<c:forEach items="${projects}" var="project">
				<tr>
					<td><a href="<c:url value="/project/${project.projectId}"/>">[${project.projectId}]
							${project.name}</a></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
