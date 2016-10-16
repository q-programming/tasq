<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<security:authentication property="principal" var="user"/>
<security:authorize access="hasRole('ROLE_ADMIN')">
    <c:set var="is_admin" value="true"/>
</security:authorize>

<script src="<c:url value="/resources/js/hopscotch.js" />"></script>
<link href="<c:url value="/resources/css/hopscotch.css" />" rel="stylesheet"
      media="screen"/>
<div id="content" class="white-frame" style="overflow: auto;text-align: center;width:700px">
    <h3>
        <i class="fa fa-graduation-cap" aria-hidden="true"></i> Welcome to ${applicationName} tours page

    </h3>
    <p>This tours will show basics, how to use, and move within application.<br>Following tours are available:</p>
    <p><span id="start" class="btn btn-default tour-button"><span class="badge theme"
                                                                  aria-hidden="true">1</span> Start ${applicationName} tour</span>
    </p>
    <p>
        <a href="<c:url value="/tour?page=project"/>" class="btn btn-default tour-button">
            <span class="badge theme" aria-hidden="true">2</span> Project details
        </a>
    </p>
    <p>
        <a href="<c:url value="/tour?page=tasklist"/>" class="btn btn-default tour-button">
            <span class="badge theme" aria-hidden="true">3</span> Tasks list
        </a>
    </p>
    <p>
        <a id="task-tour" href="<c:url value="/tour?page=task"/>" class="btn btn-default tour-button">
            <span class="badge theme" aria-hidden="true">4</span> Task details
        </a>
    </p>
    <br>
    <hr>
    <p>You can alwyas come back to tours from <a href="<c:url value="/help"/>">help page</a></p>
    <br>
</div>

<script>
    var tour = {
        id: "task_tour",
        showPrevButton: true,
        steps: [
            {
                title: "Navigation bar",
                content: "Contains main user related actions.<br> Clicking logo navigates to home page",
                target: "navbar",
                placement: "bottom",
                xOffset: 'center',
                arrowOffset: 'center'
            },
            {
                title: "Side menu",
                content: 'Side menu with project and task related options <c:if test="is_admin"> and contains application management</c:if>',
                target: "side-menu",
                placement: "right"
            },
//                SIDE MENU
            <c:if test="${user.isUser == true}">
            {
                title: "Create",
                content: '<span class="tour-bubble-button"><i class="fa fa-plus"></i> <s:message code="task.create" text="Create"/></span> starts new task creation (by default active project will be selected)<c:if test="${user.isPowerUser == true}"><br><br>You can also create new projects by selecting <span class="tour-bubble-button"><i class="fa fa-plus"></i> <s:message code="project.create" text="Create project"/></span></c:if>',
                target: "create-menu",
                placement: "right",
                yOffset: -20
            },
            </c:if>
            {
                title: "My (active) project tasks",
                content: 'Click to search within active project for all tasks assigned to your account. Afterwards you can switch to other project, or add more criteria.<br> More info on <a href="<c:url value="/tour?page=tasklist"/>" target="_blank">Task List</a>',
                target: "mytasks-menu",
                placement: "right",
                onNext: function () {
                    $(".project-menu").show("blind");
                },
                yOffset: -20
            },
            {
                title: "Projects menu",
                content: 'Here you can find recently visited projects. Your active project will be always visible. To view all project you can access, select <span class="tour-bubble-button"><i class="fa fa-list"></i> <s:message code="project.showAll" text="Projects"/></span>',
                target: "projects-menu",
                placement: "right",
                onNext: function () {
                    $(".task-menu").show("blind");
                    $(".project-menu").hide("blind");
                },
                onPrev: function () {
                    $(".project-menu").hide("blind");
                },
                yOffset: -20
            },
            {
                title: "Tasks menu",
                content: '4 last visited task are shown here. You can also view all task from currently active project by clicking <span class="tour-bubble-button"><i class="fa fa-list"></i> <s:message code="task.showAll" text="Show all"/></span><br><br>More on task list later on',
                target: "tasks-menu",
                placement: "right",
                onNext: function () {
                    $(".agile-menu").show("blind");
                    $(".task-menu").hide("blind");
                },
                onPrev: function () {
                    $(".project-menu").show("blind");
                    $(".task-menu").hide("blind");
                },

                yOffset: 0
            },
            {
                title: "Agile section",
                content: 'Last 4 visited project agile views are gathered here. You can also view all agile boards from your available project by selecting <span class="tour-bubble-button"><i class="fa fa-list"></i> <s:message code="agile.showAll" text="Show all"/></span>',
                target: "agile-menu",
                placement: "right",
                onNext: function () {
                    $(".agile-menu").hide("blind");
                    <c:if test="${user.isAdmin == true}">
                    $(".manage-menu").show("blind");
                    </c:if>
                },
                onPrev: function () {
                    $(".task-menu").show("blind");
                    $(".agile-menu").hide("blind");
                },
                yOffset: -40
            },
            <c:if test="${user.isAdmin == true}">
            {
                title: "Manage application",
                content: 'This section is reserved only for application administrator. Here you can go to general application management, manage user, or perform some tasks technical related activities.<br><br>You can read more in help page <a href="<c:url value="/help#admin"/>" target="_blank">here</a>',
                target: "admin-menu",
                placement: "right",
                onNext: function () {
                    $(".manage-menu").hide("blind");
                },
                onPrev: function () {
                    $(".agile-menu").show("blind");
                    $(".manage-menu").hide("blind");
                },
                yOffset: -20
            },
            </c:if>
            {
                title: "Help",
                content: 'Help page, a recommended read for some free time :)',
                target: "help-menu",
                placement: "bottom",
                <c:if test="${user.isAdmin}">
                onPrev: function () {
                    $(".manage-menu").show("blind");
                }
                </c:if>
                <c:if test="${not user.isAdmin}">
                onPrev: function () {
                    $(".agile-menu").show("blind");
                }
                </c:if>
            },

//                END OF SIDE MENU
            {
                title: "Search",
                content: 'Search field. Start typing to autocomplete with available tags. Active project will be searched for tasks with term in title or description (or tags)',
                target: "searchField",
                placement: "bottom"
            },
            {
                title: "Events",
                content: 'Shows count of unread events. Click to navigate to events pages ',
                target: "event-menu-icon",
                placement: "left",
                onNext: function () {
                    $('#user-menu-dropdown').addClass("tour-dropdown");
                }
            },
            {
                title: "Personal menu",
                content: 'Personal menu will be shown when clicking on avatar or name. Here we find links to events , watching pages and settings.<br><br> More info can be found <a href="<c:url value="/help#personal"/>" target="_blank">here</a>',
                target: "user-menu",
                placement: "left",
                onNext: function () {
                    $('#user-menu-dropdown').removeClass("tour-dropdown");
                },
                onPrev: function () {
                    $('#user-menu-dropdown').removeClass("tour-dropdown");
                }
            },
            {
                title: "More tours",
                content: 'Select one of tours to learn more about rest of application',
                target: "task-tour",
                placement: "bottom",
                xOffset: 'center',
                onPrev: function () {
                    $('#user-menu-dropdown').addClass("tour-dropdown");
                },
                arrowOffset: 'center'

            }
        ]
    };
    $('#start').click(function () {
        hopscotch.startTour(tour);
    });
    // Start the tour!
</script>