<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<security:authentication property="principal" var="user"/>
<ul class="nav nav-sidebar">
    <!-- CREATE -->
    <li>
        <c:choose>
            <c:when test="${user.isPowerUser == true}">
                <a class="secondaryPanelButton menu-toggle" type="button"
                   data-type="create-menu" style="margin: 0 auto;"><i
                        class="fa fa-plus"></i> Create <span class="caret"></span></a>
                <ul class="menu-items create-menu" style="margin-left: 25px;">
                    <li><a href="<c:url value="/task/create"/>"><i
                            class="fa fa-plus"></i> <s:message code="task.create"
                                                               text="Create task"/></a></li>

                    <li><a href="<c:url value="/project/create"/>"><i
                            class="fa fa-plus"></i> <s:message code="project.create"
                                                               text="Create project"/></a></li>
                </ul>
            </c:when>
            <c:when test="${user.isUser == true}">
                <a class="secondaryPanelButton menu-toggle" type="button" href="<c:url value="/task/create"/>"
                   style="margin: 0 auto;"><i class="fa fa-plus">
                </i> <s:message code="task.create" text="Create"/></a>
            </c:when>
        </c:choose>
    </li>
    <li role="presentation" class="divider"></li>
    <!-- 		PROJECTS -->
    <li><a href="#" class="menu-toggle" data-type="project-menu"><i
            class="menu-indicator fa fw fa-toggle-right"></i><i
            class="fa fa-list"></i>&nbsp;<strong><s:message
            code="project.projects" text="Projects"/></strong></a>
        <ul class="menu-items">
            <c:forEach items="${last_projects}" var="l_project">
                <c:if test="${l_project.id eq user.active_project}">
                    <li><a href="<c:url value="/project/${l_project.projectId}"/>">[${l_project.projectId}]
                            ${l_project.name}</a></li>
                </c:if>
                <c:if test="${l_project.id ne user.active_project}">
                    <li class="project-menu"><a
                            href="<c:url value="/project/${l_project.projectId}"/>">[${l_project.projectId}]
                            ${l_project.name}</a></li>
                </c:if>
            </c:forEach>
            <li role="presentation" class="divider project-menu"></li>
            <li class="project-menu"><a href="<c:url value="/projects"/>"><i
                    class="fa fa-list"></i> <s:message code="project.showAll"
                                                       text="Projects"/></a></li>
        </ul>
    </li>
    <li role="presentation" class="divider"></li>
    <!-- 		TASKS -->
    <li><a href="#" class="menu-toggle" data-type="task-menu"><i
            class="menu-indicator fa fw fa-toggle-down"></i><i
            class="fa fa-lg fa-check-square"></i>&nbsp;<strong><s:message
            code="task.tasks" text="Tasks"/></strong></a>
        <ul class="menu-items">
            <c:forEach items="${last_tasks}" var="l_task">
                <c:if test="${l_task.id eq user.active_task[0]}">
                    <li><span class="active-task"><i
                            class="fa fa-lg fa-spin fa-repeat"></i></span> <t:type
                            type="${l_task.type}" list="true"/><a
                            href="<c:url value="/task/${l_task.id}"/>">[${l_task.id}]
                            ${l_task.name}</a></li>
                </c:if>
                <c:if test="${l_task.id ne user.active_task[0]}">
                    <li class="task-menu"><t:type type="${l_task.type}"
                                                  list="true"/><a
                            href="<c:url value="/task/${l_task.id}"/>">[${l_task.id}]
                            ${l_task.name}</a></li>
                </c:if>
            </c:forEach>
            <li role="presentation" class="divider task-menu"></li>
            <li class="task-menu"><a href="<c:url value="/tasks"/>"><i
                    class="fa fa-list"></i> <s:message code="task.showAll"
                                                       text="Show all"/></a></li>
        </ul>
    </li>
    </li>
    <li role="presentation" class="divider"></li>
    <!-- 		AGILE -->
    <li><a href="#" class="menu-toggle" data-type="agile-menu"><i
            class="menu-indicator fa fw fa-toggle-right"></i><i
            class="fa fa-lg fa-desktop"></i>&nbsp;<strong><s:message
            code="agile.agile" text="Agile"/></strong></a>
        <ul class="menu-items">
            <c:forEach items="${last_projects}" var="l_project">
                <c:if test="${l_project.id eq user.active_project}">
                    <li>
                        <a href="<c:url value="/${l_project.projectId}/${l_project.agile.code}/board"/>">${l_project.name}
                            (<s:message code="agile.board.${l_project.agile}"/>)
                        </a></li>
                </c:if>
                <c:if test="${l_project.id ne user.active_project}">
                    <li class="agile-menu"><a
                            href="<c:url value="/${l_project.projectId}/${l_project.agile.code}/board"/>">${l_project.name}
                        (<s:message code="agile.board.${l_project.agile}"/>)
                    </a></li>
                </c:if>
            </c:forEach>
            <li role="presentation" class="divider agile-menu"></li>
            <li class="agile-menu"><a href="<c:url value="/boards"/>"><i
                    class="fa fa-list"></i><strong> <s:message
                    code="agile.showAll" text="Show all"/></strong></a></li>
        </ul>
    </li>
    </li>
    <li role="presentation" class="divider"></li>
    <!-- 	USERS -->
    <li><a class="show_users_btn clickable" data-toggle="modal"
           title="" data-placement="bottom"><i class="fa fa-lg fa-users"></i>&nbsp;<strong><s:message
            code="menu.users" text="Users"/></strong></a></li>
    <li role="presentation" class="divider"></li>
    <!-- 	MANAGE -->
    <c:if test="${user.isAdmin == true}">
        <li><a href="#" class="menu-toggle" data-type="manage-menu">
            <i class="menu-indicator fa  fa-lg fw fa-toggle-right"></i> <i
                class="fa fa-wrench"></i> &nbsp;<strong><s:message code="menu.manage"
                                                                   text="Settings"/></strong>
        </a>
            <ul class="menu-items">
                <li class="manage-menu"><a
                        href="<s:url value="/manage/app"></s:url>"><i
                        class="fa fa-cogs"></i> &nbsp;<s:message code="menu.manage"
                                                                 text="Settings"/></a></li>
                <li class="manage-menu"><a
                        href="<s:url value="/manage/users"></s:url>"><i
                        class="fa fa-users"></i>&nbsp;<s:message code="menu.manage.users"/></a></li>
                <li class="manage-menu"><a
                        href="<s:url value="/manage/tasks"></s:url>"><i
                        class="fa fa-lg fa-check-square"></i>&nbsp;<s:message
                        code="menu.manage.tasks"/></a></li>

            </ul>
        </li>
        <li role="presentation" class="divider"></li>
    </c:if>
    <!-- 	HELP -->
    <li><a href='<s:url value="/help"></s:url>' title=""
           data-placement="bottom"><i class="fa fa-lg fa-question-circle"></i>&nbsp;<strong><s:message
            code="menu.help" text="Help"/></strong></a></li>
</ul>
<script>
    var linksRequested = $('a[href="${requestedLink}"]');
    var menu_item = linksRequested.parent();
    menu_item.addClass("bg-color theme");
    linksRequested.addClass("theme");
    menu_item.show();
    //try to find parent menu-with class
    var menu_toggle = menu_item.parent().parent();
    if (menu_toggle.children("a")) {
        var type = menu_toggle.children("a").data("type");
        var targetClass = "." + type
        $(targetClass).show();
    }
    //find toggler
    var indicator = menu_toggle.find(".menu-indicator");
    indicator.addClass('fa-toggle-down');
    indicator.removeClass('fa-toggle-right');


</script>