<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="task" required="true"
              type="com.qprogramming.tasq.task.DisplayTask" %>
<%@ attribute name="can_edit" required="true" %>
<security:authentication property="principal" var="user"/>
<c:set var="percentage">${task.percentage}</c:set>
<div class="agile-card theme" data-id="${task.id}" state="${task.state}" data-subtasks="${task.subtasks}"
     id="${task.id}" data-tags="${task.getTagsList()}" data-assignee="${task.assignee.id}">
    <div style="padding-left: 5px;min-height: 50px">
        <div class="pull-right">
            <c:if test="${task.story_points ne 0}">
                <div>
                    <span class="badge theme pull-right">${task.story_points}</span>
                </div>
            </c:if>
        </div>
        <i class="fa fa-caret-right toggler theme show-more-details a-tooltip" data-tab="moredetails-${task.id}" data-quick="true"
           data-task="${task.id}" title="<s:message code="agile.card.showmore"/>"></i><t:type
            type="${task.type}" list="true"/>
        <a href="<c:url value="/task/${task.id}"/>" style="color: inherit;"
           class="<c:if test="${task.state eq 'CLOSED' }">closed</c:if>">
            [${task.id}] ${task.name}
        </a>
    </div>
    <div class="more-details-div" id="moredetails-${task.id}" style="display: none;">
        <div class="mod-header-bg">
            <span class="mod-header-title theme">
                <i class="fa fa-lg fa-clock-o"></i> <s:message code="task.timetrack"/>
            </span>
        </div>
        <div>
            <s:message code="task.closed"/>: <span class="pull-right">${percentage}%</span><br>
            <s:message code="task.estimate"/>: <span class="pull-right">${task.estimate}</span><br>
            <s:message code="task.logged"/>: <span class="pull-right">${task.loggedWork}</span><br>
            <s:message code="task.remaining"/>: <span class="pull-right">${task.remaining}</span>
        </div>
        <c:if test="${not empty task.getTagsList()}">
            <div class="mod-header-bg">
                <span class="mod-header-title theme">
                    <i class="fa fa-tags"></i>&nbsp;<s:message code="task.tags"/>
                </span>
            </div>
            <div>
                <c:forEach items="${task.getTagsList()}" var="tag">
                    <span class="tag label label-info theme tag_filter a-tooltip"
                          title="<s:message code="task.tags.click.filter"/>" data-name="${tag}">${tag}</span>
                </c:forEach>
            </div>
        </c:if>
        <div class="agile-card-subtasks">
        </div>
    </div>
    <div class="bottom-tab">
        <div
                style="display: table; width: 100%; margin-top: 5px; min-height: 30px;">
            <div style="display: table-row">
                <div style="display: table-cell; vertical-align: bottom;">
                    <c:if test="${can_edit && task.state ne 'CLOSED'}">
                        <c:if test="${task.assignee.id eq user.id}">
                            <button class="btn btn-default btn-xxs a-tooltip worklog"
                                    style="margin-left: 5px" type="button" data-toggle="modal"
                                    data-target="#logWorkform" data-taskID="${task.id}" id="log_time"
                                    title="<s:message code="task.logWork"/>">
                                <i class="fa fa-lg fa-calendar-plus-o"></i>
                            </button>
                        </c:if>
                    </c:if>
                    <%-- <span class="a-tooltip" title="<s:message code="task.remaining"/>">${task.percentage}</span> --%>
                </div>
                <%---Assignee--%>
                <div id="assignee_${task.id}"
                     style="margin-top: 10px; text-align: right; display: table-cell; vertical-align: bottom;">
                    <c:if test="${empty task.assignee && task.state ne 'CLOSED'}">
                        <i><s:message code="task.unassigned"/> <c:if
                                test="${can_edit}">
                            &nbsp;<button class="btn btn-default assignToTask btn-xxs a-tooltip assign_me"
                            title="<s:message code="task.assign"/>" data-toggle="modal" data-target="#assign_modal"
                            data-taskID="${task.id}" data-assignee="${task.assignee}"
                            data-assigneeID="${task.assignee.id}"
                            data-projectID="${task.projectID}"
                            type="button" data-taskID="${task.id}"
                            title="<s:message code="task.assignme"/>">
                            <i class="fa fa-lg fa-user"></i>
                            </button>
                        </c:if> </i>
                    </c:if>
                    <c:if test="${not empty task.assignee}">
                        <a ${link} class="a-tooltip pull-right"
                                   href="<c:url value="/user/${task.assignee.username}"/>"
                                   title="${task.assignee}"><img data-src="holder.js/30x30"
                                                                 class="avatar small"
                                                                 src="<c:url value="/../avatar/${task.assignee.id}.png"/>"/>
                        </a><c:if test="${task.state ne 'CLOSED'}}">
                        <button class="btn btn-default assignToTask btn-xxs a-tooltip assign_me"
                                title="<s:message code="task.assign"/>" data-toggle="modal" data-target="#assign_modal"
                                data-taskID="${task.id}" data-assignee="${task.assignee}"
                                data-assigneeID="${task.assignee.id}"
                                data-projectID="${task.projectID}"
                                type="button" data-taskID="${task.id}"
                                title="<s:message code="task.assignme"/>">
                            <i class="fa fa-lg fa-user"></i>
                        </button>
                    </c:if>
                    </c:if>
                </div>
            </div>
        </div>
        <div style="display: table;padding-left:10px;padding-right:10px;width: 100%;">
            <c:if test="${task.estimated}">
                <c:set var="logged_class"></c:set>
                <c:if test="${task.state eq 'TO_DO'}">
                    <c:set var="percentage">0</c:set>
                </c:if>
                <c:if test="${task.state eq 'CLOSED'}">
                    <c:set var="logged_class">progress-bar-success</c:set>
                    <c:set var="percentage">100</c:set>
                </c:if>
                <c:if test="${task.state eq 'BLOCKED'}">
                    <c:set var="logged_class">progress-bar-danger</c:set>
                </c:if>
                <div class="progress a-tooltip" style="height: 5px;cursor: help" data-html="true"
                     title="<s:message code="task.closed"/>: ${percentage}%<br><s:message code="task.estimate"/>: ${task.estimate}<br><s:message code="task.logged"/>: ${task.loggedWork}<br><s:message code="task.remaining"/>: ${task.remaining}"
                >
                    <div class="progress-bar ${logged_class} a-tooltip" title="${percentage}%" role="progressbar"
                         aria-valuenow="${percentage}" aria-valuemin="0"
                         aria-valuemax="100" style="width:${percentage}%; margin-top:1px"></div>
                </div>
            </c:if>
        </div>
    </div>
</div>