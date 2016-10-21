<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="myfn" uri="/WEB-INF/tags/custom.tld" %>
<c:set var="projects_text">
    <s:message code="project.projects"/>
</c:set>
<c:set var="projectDesc_text">
    <s:message code="project.description"/>
</c:set>
<security:authentication property="principal" var="user"/>
<div>
    <h3>${projects_text}</h3>
</div>
<div class="white-frame" style="overflow: auto;">
    <table class="table table-condensed">
        <thead>
        <tr>
            <th style="width: 300px"><s:message code="project.name"/></th>
            <th><s:message code="project.description"/></th>
            <th><s:message code="project.admin"/></th>
            <th style="width: 100px"><s:message code="main.action"/></th>
        </tr>
        </thead>
        <c:forEach items="${projects}" var="project">
            <c:if test="${project.projectId eq user.activeProject}">
                <%--<tr class="bg-color-light theme">--%>
                <tr class="active-project">
                    <td><a class="black-link"
                            href="<c:url value="/project/${project.projectId}"/>"><b>[${project.projectId}]
                            ${project.name}</b></a></td>
                    <td>
                        <c:if test="${fn:length(project.description) > 200}">
                            <b>${fn:substring(myfn:stripHtml(project.description), 0, 200)} ...</b>
                        </c:if>
                        <c:if test="${fn:length(project.description) < 200}">
                            <b>${project.description}</b>
                        </c:if>
                    </td>
                    <td><c:forEach var="admin" items="${project.administrators}"
                                   end="0">
                        <a class="black-link" href="<c:url value="/user/${admin.username}"/>" >${admin}</a>
                    </c:forEach></td>
                    <td><a class="btn btn-default a-tooltip pull-right"
                           style="padding: 6px 11px;" href='#'
                           title="<s:message
									code="project.active" text="Set as avtive" />"
                           data-placement="bottom"> <i class="fa fa-refresh fa-spin"></i></a></td>
                </tr>
            </c:if>
            <c:if test="${project.projectId ne user.activeProject}">
                <tr>
                    <td><a class="black-link" href="<c:url value="/project/${project.projectId}"/>">[${project.projectId}]
                            ${project.name}</a></td>
                    <td><c:if test="${fn:length(project.description) > 200}">
                        ${fn:substring(myfn:stripHtml(project.description), 0, 200)} ...
                    </c:if>
                        <c:if test="${fn:length(project.description) < 200}">
                            ${project.description}
                        </c:if>
                    </td>
                    <td><c:forEach var="admin" items="${project.administrators}"
                                   end="0">
                        <a href="<c:url value="/user/${admin.username}"/>" class="black-link">${admin}</a>
                    </c:forEach></td>
                    <td><a class="btn btn-default a-tooltip pull-right"
                           href='<s:url value="/project/activate/${project.projectId}"></s:url>'
                           title="<s:message
									code="project.activate" text="Set as avtive" />"
                           data-placement="bottom"> <i class="fa fa-refresh"></i>
                    </a></td>
                </tr>
            </c:if>
        </c:forEach>
    </table>
</div>
<script>
    $(document).ready(function ($) {
    });


</script>