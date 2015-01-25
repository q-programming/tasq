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
	<div class="help-block">
		<s:message code="task.import.hint" />
		<a href="<c:url value="/task/getTemplateFile"/>"><i class="fa fa-file-excel-o"></i> <s:message
				code="task.import.template" /></a>
	</div>
	<div class="form-group ">
		<form id="importForm" name="importForm" method="post"
			enctype="multipart/form-data">
			<div class="form-group">
				<div class="mod-header">
					<h5 class="mod-header-title">
						<s:message code="task.import.file" />
					</h5>
				</div>
			
				<input id="file_upload" name="file" type="file" accept=".xls,.xml"
					title='<i class="fa fa-file-excel-o"></i> <s:message code="task.import.selectFile" />'
					class="inputfiles" data-filename-placement="inside">
			</div>
			<div class="form-group">
				<div class="mod-header">
					<h5 class="mod-header-title">
						<s:message code="project.project" />
					</h5>
				</div>
				<select id="projects_list" style="width: 300px;" name="project"
					class="form-control">
					<c:forEach items="${projects}" var="project">
						<option id="${project.projectId}"
							<c:if test="${project.id eq user.active_project}">selected style="font-weight:bold"
 						</c:if>
							value="${project.projectId}">${project.name}</option>
					</c:forEach>
				</select>
			</div>
			<hr>
			<button class="btn btn-success pull-right" type="submit"><span class="glyphicon glyphicon-import"></span> <s:message code="task.import"/></button>
			<span class="btn pull-right" onclick="location.href='<c:url value="/"/>';"><s:message
					code="main.cancel" text="Cancel" /></span>
		</form>
	</div>
</div>
<script>
	$(document).ready(function($) {
		$("#file_upload").bootstrapFileInput();
	});

	
</script>