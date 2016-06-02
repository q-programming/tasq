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
    <h3>Agile Boards</h3>
</div>
<div class="white-frame" style="overflow: auto;">
    <table class="table table-condensed">
        <thead>
        <tr>
            <th style="width: 300px"><s:message code="project.name"/></th>
            <th style="width: 100px">Agile type</th>
            <th><s:message code="project.description"/></th>
            <th><s:message code="project.admin"/></th>
        </tr>
        </thead>
        <c:forEach items="${projects}" var="project">
            <c:if test="${project.id eq user.active_project}">
                <tr class="active-project">
                    <td><a class="black-link" href="<c:url value="${project.projectId}/${project.agile.code}/board"/>"><b>[${project.projectId}]
                            ${project.name}</b></a></td>
                    <td><b>${project.agile}</b></td>
                    <td><c:if test="${fn:length(project.description) > 200}">
                        <b>${fn:substring(myfn:stripHtml(project.description), 0, 200)} ...</b>
                    </c:if>
                        <c:if test="${fn:length(project.description) < 200}">
                            <b>${project.description}</b>
                        </c:if>
                    </td>
                    <td><c:forEach var="admin" items="${project.administrators}"
                                   end="0">
                        <a href="<c:url value="/user/${admin.username}"/>"
                           class="black-link">${admin}</a>
                    </c:forEach></td>
                </tr>
            </c:if>
            <c:if test="${project.id ne user.active_project}">
                <tr>
                    <td><a class="black-link"
                           href="<c:url value="${project.projectId}/${project.agile.code}/board"/>">[${project.projectId}]
                            ${project.name}</a></td>
                    <td>${project.agile}</td>
                    <td><c:if test="${fn:length(project.description) > 200}">
                        ${fn:substring(myfn:stripHtml(project.description), 0, 200)} ...
                    </c:if>
                        <c:if test="${fn:length(project.description) < 200}">
                            ${project.description}
                        </c:if>
                    </td>
                    <td><c:forEach var="admin" items="${project.administrators}"
                                   end="0">
                        <a href="<c:url value="/user/${admin.username}"/>"
                           style="color: black;">${admin}</a>
                    </c:forEach></td>
                </tr>
            </c:if>
        </c:forEach>
    </table>
</div>
<script>
    $(document).ready(function ($) {

    });


</script>