<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script src="<c:url value="/resources/js/trumbowyg.min.js" />"></script>
<link href="<c:url value="/resources/css/trumbowyg.min.css" />" rel="stylesheet" media="screen" />
<security:authentication property="principal" var="user" />
<c:if test="${user.language ne 'en' }">
	<script src="<c:url value="/resources/js/trumbowyg.${user.language}.min.js" />"></script>
</c:if>

<c:set var="projectName_text">
	<s:message code="project.name" />
</c:set>
<c:set var="projectID_text">
	<s:message code="project.id" />
</c:set>

<c:set var="projectDesc_text">
	<s:message code="project.description" />
</c:set>
<div class="white-frame" style="width: 700px; overflow: auto;">
<h3>
	<s:message code="project.create"></s:message>
</h3>
	<form:form modelAttribute="newProjectForm" id="newProjectForm"
		method="post">
		<%-- Check all potential errors --%>
		<c:set var="id_error">
			<form:errors path="project_id" />
		</c:set>
		<c:set var="name_error">
			<form:errors path="name" />
		</c:set>
		<c:set var="desc_error">
			<form:errors path="description" />
		</c:set>
		<c:if test="${not empty id_error}">
			<c:set var="id_class" value="has-error" />
		</c:if>
		<c:if test="${not empty name_error}">
			<c:set var="name_class" value="has-error" />
		</c:if>
		<c:if test="${not empty desc_error}">
			<c:set var="desc_class" value="has-error" />
		</c:if>
		<div style="display:table-row">
			<div class="form-group pull-left ${id_class}" style="width: 100px">
				<form:input path="project_id" class="form-control"
					placeholder="${projectID_text}" />
				<form:errors path="project_id" element="p" class="text-danger" />
			</div>
			<div class="form-group pull-left ${name_class }"
				style="width: 500px; padding-left: 20px;">
				<form:input path="name" class="form-control"
					placeholder="${projectName_text}" />
				<form:errors path="name" element="p" class="text-danger" />
			</div>
		</div>
		<div style="display:table-row">
			<div class="form-group">
				<span class="help-block"><s:message code="project.create.name.hint" htmlEscape="false"/></span>
			</div>
		</div>
		<div class="form-group"
			style="width: 300px;">
			<label><s:message code="project.agile.type" /></label>
			<form:select path="agile_type" class="form-control">
				<option value="SCRUM" selected>SCRUM</option>
				<option value="KANBAN">Kanban</option>
			</form:select>
		</div>
		<div class="form-group ${desc_class}">
			<label>${projectDesc_text}</label>
			<form:textarea path="description" class="form-control" rows="5"
				placeholder="${projectDesc_text}" />
			<form:errors path="description" element="p" class="text-danger" />
		</div>
		<div class="form-group" style="margin: 0 auto; text-align: center">
			<div>
				<button type="submit" class="btn btn-success">
					<s:message code="main.create" text="Create" />
				</button>
			</div>
		</div>
	</form:form>
</div>
<script>
	$(document).ready(function($) {
		var btnsGrps = jQuery.trumbowyg.btnsGrps;
		$('#description').trumbowyg({
			lang: '${user.language}',
			fullscreenable: false,
			btns: ['formatting',
		           '|', btnsGrps.design,
		           '|', 'link',
		           '|', 'insertImage',
		           '|', btnsGrps.justify,
		           '|', btnsGrps.lists]
		});
	});

	
</script>