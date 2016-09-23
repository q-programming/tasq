<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="task" required="true"
              type="com.qprogramming.tasq.task.DisplayTask" %>
<%@ attribute name="can_edit" required="true" %>
<security:authentication property="principal" var="user"/>
<div class="agile-card theme col-xs-6 col-sm-6 col-md-6"
     style="display: table; page-break-inside: avoid;margin-bottom: 0px;">
    <div style="padding: 10px; min-height: 30px; border-bottom: 1px solid;">
        <c:if test="${task.story_points ne 0}">
            <span class="badge theme pull-right" style="border: 1px solid #000;">${task.story_points}</span>
        </c:if>
        <t:type type="${task.type}" list="true"/>
        [${task.id}] ${task.name}
    </div>
    <div style="width: 100%; padding:5px; margin-top: 5px; min-height: 150px;max-height: 150px;">
        <c:if test="${fn:length(task.description) > 200}">
            ${fn:substring(task.description, 0, 200)}...
        </c:if>
        <c:if test="${fn:length(task.description) <= 200}">
            ${task.description}
        </c:if>
    </div>
    <c:if test="${not empty task.getTagsList()}">
        <div>
            <c:forEach items="${task.getTagsList()}" var="tag">
                <span class="tag label label-info theme">${tag}</span>
            </c:forEach>
        </div>
    </c:if>
</div>