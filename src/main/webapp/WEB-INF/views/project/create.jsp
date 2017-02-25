<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>


<c:set var="projectName_text">
    <s:message code="project.name"/>
</c:set>
<c:set var="projectID_text">
    <s:message code="project.id"/>
</c:set>

<c:set var="projectDesc_text">
    <s:message code="project.description"/>
</c:set>
<div class="white-frame col-lg-10 col-md-10" style="display: table">
    <div style="display:table-caption;margin-left: 10px;">
        <ul class="nav nav-tabs" style="border-bottom:0">
            <li class="active">
                <a style="color: black" href="#">
                    <i class="fa fa-plus"></i>&nbsp;<s:message code="project.create"/>
                </a>
            </li>
        </ul>
    </div>
    <%--<h3>--%>
    <%--<s:message code="project.create"/>--%>
    <%--</h3>--%>
    <form:form modelAttribute="newProjectForm" id="newProjectForm"
               method="post">
        <%-- Check all potential errors --%>
        <c:set var="id_error">
            <form:errors path="project_id"/>
        </c:set>
        <c:set var="name_error">
            <form:errors path="name"/>
        </c:set>
        <c:set var="desc_error">
            <form:errors path="description"/>
        </c:set>
        <c:if test="${not empty id_error}">
            <c:set var="id_class" value="has-error"/>
        </c:if>
        <c:if test="${not empty name_error}">
            <c:set var="name_class" value="has-error"/>
        </c:if>
        <c:if test="${not empty desc_error}">
            <c:set var="desc_class" value="trumbowyg-error"/>
        </c:if>
        <div class="row">
            <div class="form-group pull-left ${id_class} col-md-3">
                <form:label path="project_id">${projectID_text}</form:label>
                <form:input path="project_id" class="form-control"
                            placeholder="${projectID_text}" maxlength="5"/>
                <form:errors path="project_id" element="p" class="text-danger"/>
            </div>
            <div class="form-group pull-left ${name_class } col-md-9 paddingleft_20">
                <label for="name"><s:message code="project.name"/></label>
                <form:input path="name" class="form-control"
                            placeholder="${projectName_text}"/>
                <form:errors path="name" element="p" class="text-danger"/>
            </div>
        </div>
        <div class="row">
            <div class="form-group col-md-12">
                <span class="help-block"><s:message code="project.create.name.hint" htmlEscape="false"/></span>
            </div>
        </div>
        <div class="row">
            <div class="form-group col-sm-3">
                <label><s:message code="project.agile.type"/></label>
                <form:select path="agile" class="form-control">
                    <option value="SCRUM" selected>SCRUM</option>
                    <option value="KANBAN">Kanban</option>
                </form:select>
            </div>
        </div>
        <div class="form-group ${desc_class}">
            <label>${projectDesc_text}</label>
            <form:textarea path="description" class="form-control" rows="5"/>
            <form:errors path="description" element="p" class="text-danger"/>
        </div>
        <div class="form-group" style="margin: 0 auto; text-align: center">
            <div>
                <button type="submit" class="btn btn-success">
                    <s:message code="main.create" text="Create"/>
                </button>
            </div>
        </div>
    </form:form>
</div>
<script src="<c:url value="/resources/js/trumbowyg.min.js" />"></script>
<script src="<c:url value="/resources/js/trumbowyg.preformatted.js" />"></script>
<link href="<c:url value="/resources/css/trumbowyg.min.css" />" rel="stylesheet" media="screen"/>

<security:authentication property="principal" var="user"/>
<c:if test="${user.language ne 'en' }">
    <script src="<c:url value="/resources/js/trumbowyg.${user.language}.min.js" />"></script>
</c:if>
<script>
    $(document).ready(function ($) {
        $.trumbowyg.svgPath = '<c:url value="/resources/img/trumbowyg-icons.svg"/>';
        $('#description').trumbowyg({
            lang: '${user.language}',
            removeformatPasted: true,
            autogrow: true,
            btns: ['formatting',
                '|', ['bold', 'italic', 'underline', 'strikethrough', 'preformatted'],
                '|', 'link',
                '|', 'insertImage',
                '|', 'btnGrp-justify',
                '|', 'btnGrp-lists']
        });
    });
</script>