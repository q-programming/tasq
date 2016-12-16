<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<security:authentication property="principal" var="user"/>
<div class="pull-left sidebar smaller hidden-xs" id="small-sidebar-div"
     <c:if test="${user.smallSidebar}">style="display: block" </c:if>>
    <ul class="nav nav-sidebar hidden-print">
        <li class="padding-top5">
            <a href="<c:url value="/task/create"/>" class="a-tooltip" data-placement="right" data-container="body"
               title="<s:message code="task.create" text="Create task"/>">
                <i class="fa fa-lg fw fa-plus"></i></a>
        </li>
        <li role="presentation" class="divider"></li>
        <li class="padding-top5">
            <a href='<c:url value="/tasks?assignee="/>${user.username}' class="a-tooltip" data-placement="right"
               data-container="body" title="<s:message code="task.list.my" text="Create"/>">
                <i class="fa fa-lg fw fa-user"></i></a></li>
        <li role="presentation" class="divider"></li>
        <li class="padding-top5">
            <a href="<c:url value="/projects"/>" class="a-tooltip" data-placement="right" data-container="body"
               title="<s:message code="project.projects" text="Projects"/>">
                <i class="fa fa-lg fw fa-list"></i></a></li>
        <li class="padding-top5">
            <a href="<c:url value="/tasks"/>" class="a-tooltip" data-placement="right" data-container="body"
               title="<s:message code="task.tasks" text="Tasks"/>">
                <i class="fa fa-lg fw fa-check-square"></i></a>
        </li>
        <li class="padding-top5">
            <a href="<c:url value="/boards"/>" class="a-tooltip" data-placement="right" data-container="body"
               title="<s:message code="agile.agile" text="Agile"/>">
                <i class="fa fa-lg fw fa-desktop"></i></a>
        </li>
        <li role="presentation" class="divider"></li>
        <li class="padding-top5">
            <a class="show_users_btn clickable a-tooltip" data-toggle="modal"
               title="<s:message code="menu.users" text="Users"/>" data-placement="right" data-container="body">
                <i class="fa fa-lg fw  fa-users"></i></a></li>
        <li role="presentation" class="divider"></li>
        <c:if test="${user.isAdmin == true}">
            <li class="padding-top5">
                <a href="<s:url value="/manage/app"/>" class="a-tooltip" data-placement="right" data-container="body"
                   title="<s:message code="menu.manage" text="Settings"/>">
                    <i class="fa fa-lg fw fa-cogs"></i>
                </a>
            </li>
            <li role="presentation" class="divider"></li>
        </c:if>
        <!-- 	HELP -->
        <li class="padding-top5">
            <a href='<s:url value="/help"/>' data-placement="right" class="a-tooltip" data-container="body"
               title="<s:message code="menu.help" text="Help"/>">
                <i class="fa fa-lg fw fa-question-circle"></i>
            </a>
        </li>
        <li class="nav-bottom">
            <a href="#" id="sidebar-show" class="a-tooltip" data-container="body" data-placement="right"
               title="<s:message code="menu.small.sidebar.hide"/>">
                <i class="fa fa-lg fa-fw fa-angle-double-right" aria-hidden="true"></i></a>
        </li>
    </ul>
</div>