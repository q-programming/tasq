<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ attribute name="task" required="true" type="com.qprogramming.tasq.task.Task" %>
<%@ attribute name="taskTime" required="true" %>
<%@ attribute name="subtasks" required="false" type="java.util.List" %>
<%@ attribute name="subtasksTime" required="false" %>
<%@ attribute name="method" required="true" %>

<tr class="time-details-row subtask-${method}" style="border-top: 2px solid lightgray;">
    <td colspan="2"></td>
    <td class="bar_td">
        <div>[${task.id}]</div>
    </td>
    <td class="bar_td">${taskTime}</td>
</tr>
<tr class="time-details-row subtask-${method}" style="border-bottom: 1px solid lightgray;">
    <td></td>
    <td colspan="2" class="bar_td">
        <div><s:message
                code="tasks.subtasks"/></div>
    </td>
    <td class="bar_td">${subtasksTime}</td>
</tr>
<c:forEach var="subTask" items="${subtasks}">
    <tr class="time-details-row subtask-${method}">
        <td colspan="2"></td>
        <td class="bar_td">
            <div>[${subTask.id}]</div>
        </td>
        <c:choose>
            <c:when test="${method eq 'estimate'}">
                <td class="bar_td">${subTask.estimate}</td>
            </c:when>
            <c:when test="${method eq 'logged'}">
                <td class="bar_td">${subTask.loggedWork}</td>
            </c:when>
            <c:when test="${method eq 'remaining'}">
                <td class="bar_td">${subTask.remaining}</td>
            </c:when>
        </c:choose>
    </tr>
</c:forEach>
<tr class="time-details-row subtask-${method}">
    <td colspan="4">&nbsp;</td>
</tr>