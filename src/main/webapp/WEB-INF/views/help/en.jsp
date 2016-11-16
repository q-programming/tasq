<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="row white-frame">
    <div class="col-md-10 col-sm-10 col-lg-10" role="main">
        <a class="anchor" id="top"></a>
        <div>
            <a class="anchor" id="about"></a>
            <h2 style="height: 50px; background-color: #2c5aa0; color: white">
                <img src="<c:url value="/resources/img/logo.png"/>"
                     style="height: 50px; padding: 5px;">Tasker
            </h2>
            <p style="text-align: center">v.${version}</p>

            <h3>About Tasker</h3>
            <p>
                "Tasker" is application to track progress within project using
                either time estimation or agile style.<br> It was designed and
                implemented from scratch using <a
                    href="https://github.com/q-programming/spring-mvc-quickstart-archetype.git"><i
                    class="fa fa-code-fork"></i> Spring MVC 4 Quickstart Maven
                Archetype</a> <br>While developing it whole code was stored on
                github and can be backtracked in <a class="more-link"
                                                    href="https://github.com/q-programming/tasq" target="_blank"><i
                    class="fa fa-github-alt"></i> GitHub page</a></br> To help build process it
                had it's own <a class="more-link"
                                href="http://q-programming.pl/jenkins.html" target="_blank"
                                style="margin: 0px 0px 0px -4px;"><img
                    src="http://q-programming.pl/assets/images/jenkins.png"
                    alt="Q-Programming" style="margin-bottom: 0px;"> Jenkins page</a>
                <br> <br> Application creation process was started on May
                18 2014 and over a year to have it in stable version
            </p>
            <hr>
            <div>
                <strong>To quickly get to know with application, it's recommended to take <br>
                    <a href="<c:url value="/tour"/>" style="margin-left: 150px;" target="_blank">
                        <i class="fa fa-graduation-cap" aria-hidden="true"></i>Tasker tours
                    </a>
                </strong>
            </div>
            <hr>
            <p>
                If you found a bug, or want to propose an improvement , please go to
                <a href="http://q-programming.pl/tasq/project/TASQ">http://q-programming.pl/tasq/project/TASQ</a> and
                login using following credentials and create new task.<br>
            <ul class="list-unstyled">
                <li><strong>login</strong>: <i>public</i></li>
                <li><strong>password</strong>: <i>iFoundAbugOrImprovement</i></li>
            </ul>
            </p>
            <hr>
            <p>
                Application created by <b>Jakub Romaniszyn</b> <a
                    href="http://q-programming.pl/">http://q-programming.pl/</a> and is
                under GNU GPL License.
            </p>
            <%--------------------------------MAIN MENU ------------------------------------%>
            <hr>
            <a class="anchor" id="main"></a>
            <h2>Application menus</h2>
            <p>There are two menus, one in form of navigation bar at the top and side menu to the left. Navigation bar
                presents basic information about time , event count and gives access to search , and user options.
                Sidebar menu have options to create and view projects and tasks and work with application in general
            </p>
            <p>Go to <strong><a href="<c:url value="/tour"/>" target="_blank">
                <i class="fa fa-graduation-cap" aria-hidden="true"></i>${applicationName} tour
            </a> </strong>for a quick overview of whole application menu options</p>
            <strong>Navigation bar</strong>
            <ul>
                <li class="wider">
                    <img src="<c:url value="/resources/img/help/search_box.png"/>"> Search input box
                    Shows all task with inputted text within task name, its
                    description or tag. Additionally when user starts to type keyword,
                    all similar task tags are shown in drop-down list. After user
                    confirms selection, all tasks matching this criteria are displayed
                    on <b><a href="#task-view">Browse all tasks</a></b> view , with
                    query as one of filters
                </li>
                <li>
                    <a href='#events'> <i
                            class="fa fa-bell"></i>&nbsp;<strong>2</strong></a> Shows count of unread events. Click to
                    navigate to
                    events pages
                </li>
                <li><a href="#personal"><img
                        src="<c:url value="/resources/img/avatar.png" />" class="avatar">
                    <strong> Name Surname</strong><span class="caret"></span></a>
                    Personal menu under user's avatar
                </li>
            </ul>

            <strong>Sidebar menu</strong>
            <ul>
                <li class="wider"><a href="#create"><i class="fa fa-plus">
                </i>&nbsp;<strong><s:message code="task.create" text="Create"/></strong></a> Here task or project can be
                    created ( depends on
                    user role)
                </li>
                <li class="wider">
                    <i class="fa fa-user"></i>&nbsp;
                    <strong><s:message code="task.list.my" text="My project tasks"/></strong> - Click to search within
                    active project for all tasks assigned to your account. Afterwards you can switch to other project,
                    or add more criteria.<br> More info on <a href="#task-view" target="_blank">Browsing all tasks</a>
                </li>
                <li class="wider"><b><a href="#proj">
                    <i class="menu-indicator fa fw fa-toggle-right"></i><i class="fa fa-list"></i>&nbsp;
                    <s:message code="project.projects" text="Projects"/></a></b> Gives access to
                    projects which are created or available for user. First are
                    displayed 5 last visited projects.<br> To show all , choose <b><a
                            href="#proj-view"><i class="fa fa-list"></i> <s:message
                            code="project.showAll" text="Projects"/></a></b><br> Nearly every
                    user can create his own project from here using <b><a
                            href="#proj-create"><i class="fa fa-plus"></i> <s:message
                            code="project.create" text="Create project"/></a></b></li>
                <li class="wider"><a href="#task"><i
                        class="menu-indicator fa fw fa-toggle-right"></i><i
                        class="fa fa-lg fa-check-square"></i>&nbsp;<strong><s:message
                        code="task.tasks" text="Tasks"/></strong></a> - Gives access to tasks.
                    Last 5 visited task are shown first, and all tasks can be shown
                    using <i class="fa fa-list"></i> <s:message code="task.showAll"
                                                                text="Show all"/> option
                </li>
                <li class="wider"><a href="#agile"><i
                        class="menu-indicator fa fw fa-toggle-right"></i><i
                        class="fa fa-lg fa-desktop"></i>&nbsp;<strong><s:message
                        code="agile.agile" text="Agile"/></strong>
                </a> lists agile boards for last visited projects. All boards can be
                    viewed using <i class="fa fa-list"></i> <s:message
                            code="agile.showAll" text="Show all"/></li>
                <li class="wider">
                    <a href="#users"><i class="fa fa-users"></i>&nbsp;<strong><s:message
                            code="menu.users" text="Users"/></strong></a> - shows modal with all application users
                </li>
                <security:authorize access="hasRole('ROLE_ADMIN')">
                    <li class="wider">
                        <a href="#admin">
                            <i class="menu-indicator fa fw fa-toggle-right"></i><i class="fa fa-wrench"></i>
                            &nbsp;<strong><s:message code="menu.manage" text="Settings"/></strong>
                        </a><span class="admin-button">Admin</span>
                        - menu to manage users or execute some additional task
                        related actions
                    </li>
                </security:authorize>
                <li class="wider"><i class="fa fa-question-circle"></i><strong> Help</strong>
                    (this page)
                </li>

            </ul>
            <%------------------------PERSONAL MENU---------------------------------- --%>
            <hr>
            <a class="anchor" id="personal"></a>
            <h3>Personal menu</h3>
            Under user's avatar there is Personal menu where user can basic
            options, notifications and watch screen.<br> If there are some
            unread notification, there will be number displayed next to user's
            avatar.<br> From this menu user can do following:
            <ul>
                <li><strong><a href="#settings"><i class="fa fa-cog"></i> <s:message
                        code="menu.settings" text="Settings"/></a></strong> - Shows users settings
                    like language, theme etc.
                </li>
                <li><strong><a href="#events"> <i class="fa fa-bell-o"></i>&nbsp;<s:message
                        code="events.events"/>
                </a></strong> - Shows all system events
                </li>
                <li><strong><a href="#watch"> <i class="fa fa-eye"></i>&nbsp;<s:message
                        code="events.watching"/>
                </a></strong> - Shows all currently watched tasks for logged user
                </li>
                <li><strong><a href="/tasq/logout"><i class="fa fa-power-off"></i>
                    <s:message code="menu.logout" text="Log out"/></a></strong> - ends current
                    user session and logouts
                </li>
            </ul>
            <%-------------------------MY SETTINGS -------------------------------- --%>
            <hr>
            <a class="anchor" id="settings"></a>
            <h3>
                <s:message code="panel.settings"/>
            </h3>
            <p>Using this view, user can change some basic options regarding
                "Tasker" application.</p>
            <h4>Avatar</h4>
            <p>
                By default avatar is set to lovely hamster. However it's possible to
                overwrite the default avatar with custom one. When user uploads new
                image and saves profile this image will be stored in app home dir<br>
                <security:authorize access="hasRole('ROLE_ADMIN')">
                    <span class="admin-button">Admin</span>
                    <code>${projHome}/avatar</code>
                </security:authorize>
            </p>

            <h4>
                <i class="fa fa-envelope-o"></i>
                <s:message code="panel.emails"/>
            </h4>
            <p>
                Here email notification can be changed. When user sings up into
                "Tasker" for the first time, notifications are marked as true by
                defaults.<br> If somebody don't want to receive any
                notification this checkbox must be de-selected
            </p>
            <h4>
                <i class="fa fa-globe"></i>
                <s:message code="panel.language"/>
            </h4>
            <p>Whole "Tasker" app is "dynamically localized" which means that
                whole application can be easily translated to any language by
                translating messages properties file. On launch date only English
                and Polish languages were released, but any new requested language
                can be easily added with minimal development effort</p>
            <h4>
                <i class="fa fa-paint-brush"></i>
                <s:message code="panel.theme"/>
            </h4>
            <p>
                Here user can change theme for application. By default blue
                "Default" theme is available, but application administrator can
                easily create new ones.<br>
                <security:authorize access="hasRole('ROLE_ADMIN')">
                    <span class="admin-button">Admin</span>
                    To create new theme see <strong><a href="#a_theme">Themes sections</a></strong>
                </security:authorize>
            </p>
            <h4>
                <i class="fa fa-users"></i>
                <s:message code="panel.invite"/>
            </h4>
            <p>
                New users can be invited to use tasker, by using this form. Input
                valid e-mail address and click "<b><i class="fa fa-user-plus"></i>
                <s:message code="panel.invite"/></b> " to send predefined e-mail to
                inputed address
            </p>
            <hr>
            <%--------------------------------EVENTS ----------------------------%>
            <a class="anchor" id="events"></a>
            <h3>
                <s:message code="events.events"/>
            </h3>
            <p>
                "Tasker" has notification system which will signal all important
                events within application. For example, If there will be some
                administration activity related to user's account. A system
                notification will be sent.<br> Each application user can <strong><a
                    href="#watch"><i class="fa fa-eye"></i>&nbsp; Watch project</a></strong>, to
                track all events related to project he finds interesting. If there
                will be something new in project(comment, new file or task status
                update), new notification will be then added to account. After each
                page refresh, application checks if there is new notification and
                adds unread count next to user's avatar.<br>For example, If
                there is one unread notification <i class="fa fa-bell"></i>&nbsp;<strong>1</strong> will be shown next
                to user's avatar.<br>
                In order to view all notification click it, or <b><i class="fa fa-bell"></i>&nbsp;<s:message
                    code="events.events"/>&nbsp;(1)</b> must be chosen from Personal
                Menu<br> In this screen we can view unread ones ( bolded )
                mark all as read or delete all.
            </p>
            <hr>
            <%--------------------WATCH ----------------------------%>
            <a class="anchor" id="watch"></a>
            <h3>
                <s:message code="events.watching"/>
            </h3>
            <p>
                It's possible to sign up for watch notifications about task. If it
                will be edited , some user will add new comment or state of it will
                be changed, all users who are watching this project will receive
                in-system notification.<br> Furthermore if user selected "Mail
                notification" in My settings, he will receive email notification
                about watched event<br> In order to sign up for project
                notification press watch button
                <button i class="btn btn-default btn-sm">
                    <i class="fa fa-lg fa-eye"></i>
                </button>
                while being in task details screen.<br> All currently watched
                tasks are displayed in <b><i class="fa fa-eye"></i>&nbsp;<s:message
                    code="events.watching"/></b> view which can be accessed through <strong><a
                    href="#personal">Personal Menu</a></strong><br>To stop receiving
                notification about project, press stop watching
                <button class="btn btn-default btn-sm">
                    <i class="fa fa-lg fa-eye-slash"></i>
                </button>
                button located inside task details or in Watched screen inside
                Personal menu
            </p>
            <hr>
            <%---------------------------------USERS------------------------------------- --%>
            <h2>Users</h2>
            <a class="anchor" id="users"></a>
            <p>
                Each user that registered into application can be viewed in modal
                User window shown after clicking
                <i class="fa fa-users"></i>&nbsp;<strong><s:message
                    code="menu.users" text="Users"/></strong>
                option in sidebar menu<br> Every user is shown in this view with
                his avatar.<br>Additionally if user is online ( logged into
                Tasker and performed action within 15 min) &nbsp;<i
                    class="fa fa-user a-tooltip" style="color: mediumseagreen" title=""
                    data-original-title="Online"></i> will be shown next to his name.<br>From
                this screen we can sent e-mail message by selecting <i
                    class="fa fa-envelope" style="color: black;"></i> icon message as
                well.
            </p>
            <hr>
            <%--------------------------------PROJECTS ------------------------------------%>
            <a class="anchor" id="proj"></a>
            <h2>Projects</h2>
            <a class="anchor" id="proj-view"></a>
            <h3>Browsing all projects</h3>
            <p>
                Each user can view project to which he is assigned ( or is admin )
                To do so, please select <b><i class="fa fa-list"></i> <s:message
                    code="project.showAll" text="Projects"/></b> from
                <b>
                    <i class="menu-indicator fa fw fa-toggle-right"></i><i class="fa fa-list"></i>&nbsp;
                    <s:message code="project.projects" text="Projects"/>
                </b> menu
            </p>
            <p>On this screen every project into which user is assigned is
                shown here. Basic information like name , description and who is
                main administrator of project is displayed.</p>
            <p>
                Project that are set as active are highlighted and <i
                    class="fa fa-refresh fa-spin"></i> icon is shown in active button.
                When creating new task, active project is always taken first. To
                change active project, please click <span class="btn btn-default "><i
                    class="fa fa-refresh"></i> </span> button in corresponding row
            </p>
            <hr>
            <%-------------------------DETAILS ------------------------------%>
            <a class="anchor" id="proj-details"></a>
            <h3>View projects details</h3>
            <p>
                <strong> Take a quick
                    <a href="<c:url value="/tour?page=project"/>" target="_blank">
                        <i class="fa fa-graduation-cap" aria-hidden="true"></i>&nbsp;<strong>Project details</strong>
                    </a> tour</strong>
            </p>
            <p>
                In order to show project details , please either select it from
                <b>
                    <i class="menu-indicator fa fw fa-toggle-right"></i><i class="fa fa-list"></i>&nbsp;
                    <s:message code="project.projects" text="Projects"/>
                </b>
                menu , or click on Project ID , on task screen.
            </p>
            <p>Project details screen all project information related to this
                project</p>
            <div>
                <img class="responsive" src="<c:url value="/resources/img/help/project_details.png"/>">
                <p>
                <ol>
                    <li><a class="anchor" id="proj-1"></a><b>Project Name</b></li>
                    <li><a class="anchor" id="proj-2"></a><b>Project
                        description</b></li>
                    <li><a class="anchor" id="proj-3"></a><b>Open/Ongoing/Closed
                        tasks</b> - Progress bar showing ratio of open/closed tasks within
                        project
                    </li>
                    <li><a class="anchor" id="proj-4"></a><b>Open/Closed Chart</b>
                        - shows how work was done in project. How many tasks were created
                        and closed
                    </li>
                    <li><a class="anchor" id="proj-5"></a><b>Show more than
                        30 days on chart</b> By default events from last 30 days are shown on
                        create/closed chart. If you would like to view all events, from
                        very beginning of project, click this checkbox. Please be aware
                        that rendering this chart can take some time, depending on how
                        many tasks were within project
                    </li>
                    <li><a class="anchor" id="proj-6"></a><b>Latest events in
                        project</b></li>
                    <li><a class="anchor" id="proj-7"></a><b>Events list
                        pagination</b></li>
                    <li><a class="anchor" id="proj-8"></a><b>Tasks list</b></li>
                    <li><a class="anchor" id="proj-9"></a><b>Hide closed
                        tasks</b> - by default closed tasks are hidden in this view. In order
                        to view all task de-select this checkbox
                    </li>
                    <li><a class="anchor" id="proj-10"></a><b>Show all/hide
                        all Subtasks</b> - if one of tasks have subtasks <i
                            class="fa fa-plus-square"></i> icon will be shown before it's
                        name. After clicking this button all subtasks for every task will
                        be expanded
                    </li>
                    <li><a class="anchor" id="proj-11"></a><b>Project participants</b> - Show modal with all project
                        participants
                    </li>
                    <li><a class="anchor" id="proj-12"></a><b>Activate/Deactivate
                        project</b> - set displayed project as active. It will be used while creating new tasks, listing
                        and searching as first choice. It will also be visible on left side menu
                    </li>
                    <li><a class="anchor" id="proj-13"></a><b>Project
                        management</b> - set up project, add new users, set admins etc.
                    </li>


                </ol>
                </p>
            </div>
            <%----------------------------CREATE  ---------------------------------------%>
            <a class="anchor" id="proj-create"></a>
            <h3>Creating project</h3>
            <p>
                In order to create new project if you have correct role, start by selecting <b><i
                    class="fa fa-plus"></i> <s:message
                    code="project.create" text="Create project"/></b> option from sidebar menu.
                <br>This will show you "New project" creation screen
                on which all details about new project can be filled in
            </p>
            <table class="table">
                <tr>
                    <td class="col-md-2"><b><s:message code="project.id"/></b></td>
                    <td>Unique ID of project. This will be used when creating new
                        task to generate its ids.ID should not contain any numbers and
                        consist of maximum 5 letters. Chose wisely as it won't be possible
                        to change project ID after it has been created
                    </td>
                </tr>
                <tr>
                    <td><b><s:message code="project.name"/></b></td>
                    <td>Name of new project. It must not be empty. It can be
                        changed at anytime
                    </td>
                </tr>
                <tr>
                    <td><b><s:message code="project.agile.type"/></b></td>
                    <td>Choose agile type of newly created project
                        <ul>
                            <li><b><a href="#scrum">SCRUM</a></b> with more focus on
                                creation of new features or even whole new project
                            </li>
                            <li><b><a href="#kanban">Kanban</a></b> good choice for
                                already existing projects with maintenace phase
                            </li>
                        </ul>
                    </td>
                </tr>

                <tr>
                    <td><b><s:message code="project.description"/></b></td>
                    <td>This section should contain description what the project
                        will be about, what's its goal, and how the project will be
                        created. If project will use some technologies or some new
                        solutions it's recommended to describe them as well. When creating
                        new project this field can't be empty
                    </td>
                </tr>
            </table>
            <hr>
            <%--------------------------------EDIT ------------------------------------%>
            <a class="anchor" id="proj-edit"></a>
            <h3>Editing and managing project</h3>
            <p>
                Each project after it has been created can be edited. To manage your
                project click <span class="btn btn-default"><i
                    class="fa fa-wrench"></i></span> on project details screen
            </p>
            <h4>Default project settings</h4>
            After project has been created you can edit it's name or description
            by clicking <i class="fa fa-pencil"></i> icon next to project name.<br>It's
            impossible to change ID of project, once it has been created <b>Task
            progress in project</b>
            <p>Tasker has two option two track progress within project.</p>
            <ul>
                <li><b>Estimated time tracked</b> - Each task before "sprint"
                    is started, should have estimated time set right away. Sprint is in
                    quotes on purpose, as if time tracked it's not completely SCRUM
                    anymore. Regardless this option can still be used to track progress
                    for team<br></li>
                <li><b>Story points</b> recommended for agile projects. Each
                    task then is measured with points - which measures complexity of
                    this task. Each team can freely translate how point is translated
                    to work time. It's important to remember that 1SP is <b>not
                        necessary</b> 1h!
                </li>
            </ul>
            <p>Please note that it's not recommended to switch how progress
                is tracked while project is ongoing or has already some sprint which
                were tracked. This can cause some errors and very odd charts in
                reports</p>
            <b>Default task priority</b>
            <p>Every new task created within this project will have this
                priority. Of course it can be freely changes by user</p>
            <b>Default task type</b>
            <p>New task created for this project will have this task type. It
                can be changed by user before creation is finished</p>
            <b>Default task assignee</b>
            <p>When new task will be created chosen user will be inputed into
                assignee field. If user would like to assign someone else , he can
                choose him/her from autocomplete field on task creation screen</p>
            <h4>People</h4>
            <b>Admins</b>
            <p>
                All project admins are listed in here. Project admin has access to
                this screen, plus additionally can perform any operation on tasks
                within this project to maintain order etc.<br> To add new
                project admin , user first has to be added to project as member.
                Next click
                <button class="btn btn-default btn-sm " title="">
                    <i class="fa fa-plus"></i><i class="fa fa-wrench"></i>
                </button>
                in his row. If user is already admin and you would like to remove
                this privileges from here , click button located in same place but
                with minus symbol :
                <button class="btn btn-default btn-sm " title="">
                    <i class="fa fa-minus"></i><i class="fa fa-wrench"></i>
                </button>
                <b>Project members</b>
            <p>
                Every project member is listed in here ( with Role in parenthesis).
                To add new existing user into project click <span
                    class="btn btn-default btn-sm" title=""><i
                    class="fa fa-lg fa-user-plus"></i></span>. This will show field with
                autocompletion of all application registered users. Choosing one of
                names will automatically start process of adding user to project. To
                browse through all application users click <span
                    class="btn btn-default btn-sm" title=""><i
                    class="fa fa-lg fa-users"></i></span> button either next to add button on
                located in top menu next to avatar. <br> To remove user from
                project , choose
                <button class="btn btn-default btn-sm " title="">
                    <i class="fa fa-user-times"></i>
                </button>
                button in users row
            <hr>
            <a class="anchor" id="task"></a>
            <%-----------------------------------------TASKS---------------------------------------------%>
            <h2>Tasks</h2>
            <a class="anchor" id="task-view"></a>
            <h3>Browsing all tasks</h3>
            <p>
                <strong>Take a quick
                    <a href="<c:url value="/tour?page=tasklist"/>" target="_blank">
                        <i class="fa fa-graduation-cap" aria-hidden="true"></i>&nbsp;<strong>Task list</strong>
                    </a> tour to get see how list can be filtered etc.</strong>
            </p>
            <p>
                To browse all tasks select <b><i class="fa fa-list"></i> All
                tasks</b> option from <i class="menu-indicator fa fw fa-toggle-down"></i><i
                    class="fa fa-lg fa-check-square"></i>&nbsp;<strong>Tasks</strong> menu
            </p>
            <p>By default all open tasks from currently active project are
                shown. To show tasks from other project choose it from dropdown
                list( only your project are listed here )</p>
            <p>
                Displayed tasks can be filtered by priority or status.<br> In
                order to filter by priority select caret(<span class="caret"></span>)
                near Type text in table header<br> To filter by status , select
                it from drop down menu in Status<span class="caret"></span> also in
                table header. <br>Each applied filter is shown next to Task
                Project combobox, and can be removed by clicking <i
                    class="fa fa-times"
                    style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
                symbol
            </p>
            <p>
                If Task has any subtasks <i class="fa fa-plus-square"></i> symbol
                will be shown next to it's ID. Subtasks can be expanded manually for
                each task with subtasks, or by clicking <b>Show all subtasks</b> <i
                    class="fa fa-plus-square"></i> in top right corner, just below
                export button
            <p>
                From this view user can also <b><a href="#task-export">Export
                tasks</a></b> by clicking <a class="btn btn-default"> <i
                    class="fa fa-upload"></i> Export
            </a> button
            <hr>
            <%-------------------------DETAILS ------------------------------%>
            <a class="anchor" id="task-details"></a>
            <h3>View task details</h3>
            <p>
                <strong>Take a quick
                    <a href="<c:url value="/tour?page=task"/>" target="_blank">
                        <i class="fa fa-graduation-cap" aria-hidden="true"></i>&nbsp;<strong>Task detail</strong>
                    </a> tour</strong>
            </p>

            <p>
                <img class="responsive" src="<c:url value="/resources/img/help/task_details.png"/>">
            </p>
            <p>
                <ol>
                    <li><a class="anchor" id="task-1"></a><b>Project ID/ [ Task
                        ID ] Task name</b></li>
                    <li><a class="anchor" id="task-2"></a><b>Current task
                        status</b> - in order to change this status click <span class="caret"></span>
                        and choose new one
                    </li>
                    More about
                    <b><a href="#taskstatus">task statuses</a></b>
                    <li><a class="anchor" id="task-3"></a><b>Task priority</b> - to
                        change click <span class="caret"></span> and choose new
                    </li>
                    <li><a class="anchor" id="task-4"></a><b>Tags</b> - to add new
                        tag , input it. If there is similar tag, autocomplete box will
                        popup. To remove tag , click <i class="fa fa-times"
                                                        style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
                        symbol<br> To view all tasks with this tag, click it. This
                        will move you to <b><a href="#task-view">Browse all tasks</a></b>
                        view with applied tag filter.
                    </li>
                    <li><a class="anchor" id="task-5"></a><b>Task description</b></li>
                    <li><a class="anchor" id="task-6"></a><b>Story points</b> - if
                        task is estimated, story points for it will be shown. If no points
                        were set ,question mark will be shown instead. This value can be
                        changed either from <b><a href="#task-edit">Edit menu</a></b>, or
                        from this view quickly by hovering over <span class="badge theme">?</span>
                        to reveal extra <i class="fa fa-pencil"></i> button .<br>
                        Click it to show input field <span class="badge theme"> <input
                                class="point-input" style="display: inline-block;"> <span
                                style="cursor: pointer;"><i class="fa fa-check"
                                                            style="vertical-align: text-top"></i></span> <span
                                style="cursor: pointer;"><i class="fa fa-times"
                                                            style="vertical-align: text-top"></i></span>
				</span> Confirm it by either enter key, or clicking check-mark sign. Cancel
                        input by clicking x <br>You have to be either task owner or
                        project admin to do so
                    </li>
                    <li><a class="anchor" id="task-7"></a><b>Log work button</b> -
                        use it to log time spent on this task. More information in <b><a
                                href="#worklogging">Working with tasks</a></b> section
            <p><i><i class="fa fa-exclamation-circle" aria-hidden="true"></i>&nbsp;Keyboard shortcut 'l'</i></p>
            </li>
            <li><a class="anchor" id="task-8"></a><b>Start timer button</b>
                - starts/stops active timer on this task
            </li>
            <li><a class="anchor" id="task-9"></a><b>Time bars</b> - shows
                how much time was estimated, how much logged and remaining
            </li>
            <li><a class="anchor" id="task-10"></a><b>Related tasks</b> -
                Shows list of all task that are related/linked to it. In order to
                add new link , press <a class="btn btn-default btn-xxs" href="#"
                                        title=""> <i class="fa fa-plus"></i><i
                        class="fa fa-lg fa-link fa-flip-horizontal"></i>
                </a> button ( this option is also available under edit menu)
                <p><i><i class="fa fa-exclamation-circle" aria-hidden="true"></i>&nbsp;Keyboard shortcut 'r'</i></p>
            </li>
            <li><a class="anchor" id="task-11"></a><b>Subtasks list</b> -
                all subtasks are shown here with type, priority and progress. To
                create new subtask , click <a class="btn btn-default btn-xxs"
                                              href="#"> <i class="fa fa-plus"></i> <i
                        class="fa fa-lg fa-sitemap"></i>
                </a> button ( option available from edit menu as well )
            </li>
            <li><a class="anchor" id="task-12"></a><b>Activity log tab</b>
                - click to show all activities related to this task
            </li>
            <li><a class="anchor" id="task-13"></a><b>Comments tab</b> -
                click to switch to comments tab. All comments related to this task
                are listed ( sorted from newest).<br> New comment can be added
                by clicking
                <button id="comments_add" class="btn btn-default btn-sm">
                    <i class="fa fa-comment"></i>&nbsp; Add comment
                </button>
                button
                <p><i><i class="fa fa-exclamation-circle" aria-hidden="true"></i>&nbsp;Keyboard shortcut 'c'</i></p>
            </li>
            <li><a class="anchor" id="task-14"></a><b>Show edit menu</b> -
                more information in <b><a href="#task-edit">Editing task</a></b>
                section
            </li>
            <li><a class="anchor" id="task-15"></a><b>Start/Stop
                watching task</b></li>
            <li><a class="anchor" id="task-16"></a><b>Delete task</b> -
                deletes this task ( project admin only) More information in <b><a
                        href="#task-remove">Removing tasks</a></b></li>
            <li><a class="anchor" id="task-17"></a><b>Task owner and
                current assignee</b> - you can quickly assign someone to this task by
                clicking <span class="btn btn-default btn-sm "><i
                        class="fa fa-lg fa-user-plus"></i> </span> button.<br>This will show
                modal window. Start typing username to show autocomplete hints with
                all project members.<br>
                <p><i><i class="fa fa-exclamation-circle" aria-hidden="true"></i>&nbsp;Keyboard shortcut 'a'</i></p>
            </li>
            <li><a class="anchor" id="task-18"></a><b>Creation, last
                update and due dates</b></li>
            <li><a class="anchor" id="task-19"></a><b>Sprints</b> - if task
                belongs or belonged to one of sprints, it will be listed here
            </li>
            </ol>
            <hr>
            <%-------------------------CREATING------------------------------%>
            <a class="anchor" id="task-create"></a>
            <h3>Creating tasks</h3>
            <p>
                To create new task, click<b><i class="fa fa-plus"></i> <s:message
                    code="task.create" text="Create task"/></b> in sidebar menu<br>
                This will show you "Create task" creation screen on which all
                details about new task can be filled in.
            </p>
            <table class="table">
                <tr>
                    <td class="col-md-2"><b><s:message code="task.name"
                                                       text="Summary"/></b></td>
                    <td>Brief summary of what this task will be about</td>
                </tr>
                <tr>
                    <td><b><s:message code="task.description"
                                      text="Description"/></b></td>
                    <td>Description of task. All information required to finish it
                        should be placed here. Basic styling is available through built-in
                        rich text editor
                    </td>
                </tr>
                <tr>
                    <td><b><s:message code="project.project"/></b></td>
                    <td>Project for which this task will be created.By default
                        user's active project is always selected<br> After user
                        select project , task type and priority will be automatically
                        updated to default project value ( overriding already selected
                        value)

                    </td>
                </tr>
                <tr>
                    <td><b><s:message code="task.assign"/></b></td>
                    <td>User to whom this task will be assigned. Autocomplete of
                        all project users is added in this field. If input box will be
                        blank , task will be unassigned. Project default assignee is
                        always entered when this screen is viewed. Default assignee can be
                        changed on <b><a href="#proj-edit">Project management</a></b>
                        screen
                    </td>
                </tr>
                <tr>
                    <td><a class="anchor" id="task-types"></a> <b><s:message
                            code="task.type"/></b></td>
                    <td>Type of task. This helps to filter task for whole team.

                        Additionally types like "Task", "Bug" and "Idle" have estimation
                        disabled by default. Project default task type is always selected
                        when this screen is viewed. This can be changed on <b><a
                                href="#proj-edit">Project management</a></b> screen <br>There
                        are following task types available:
                        <ul>
                            <li><t:type type="TASK" show_text="false" list="false"/> -
                                a simple activity. Usually not too big, and most of the time
                                without fixed estimation. It can also be used to group other
                                task ( by linking as related )
                            </li>
                            <li><t:type type="USER_STORY" show_text="false" list="false"/>
                                - feature, case scenario or bigger thing to be done. In most
                                cases before work has been started on it , it should be
                                estimated
                            </li>
                            <li><t:type type="ISSUE" show_text="false" list="false"/>
                                - used mostly report that something is not right within project
                                but not might not be bug at all
                            </li>
                            <li><t:type type="BUG" show_text="false" list="false"/> -
                                if something is broken, task of this type can be created
                            </li>
                            <li><t:type type="CHANGE_REQUEST" show_text="false" list="false"/> -
                                in case customer would like to change something which was not part of initial scope, or
                                there is some feature request to working project
                            </li>
                            <li><t:type type="IDLE" show_text="false" list="false"/>-
                                sometimes it can happen that team is not working, waiting for
                                something to be unblocked, or just waiting idly for new tasks
                            </li>
                        </ul>
                        and types for subtasks:
                        <ul>
                            <li><t:type type="SUBTASK" show_text="false" list="false"/>-
                                most common subtask type
                            </li>
                            <li><t:type type="SUBBUG" show_text="false" list="false"/>-
                                If there is some bug related to task , we can either create new
                                bug and link it or create subtask with this type
                            </li>
                            <li><t:type type="IDLE" show_text="false" list="false"/></li>
                        </ul>

                    </td>
                </tr>

                <tr>
                    <td><b><s:message code="task.priority"/></b></td>
                    <td>How important this task is</td>
                </tr>

                <tr>
                    <td><b>Sprint</b></td>
                    <td>If there is already existing sprint which is not yet
                        finished , newly created task can be added right away after
                        creation by selecting it from combobox. Please note that if sprint
                        is active, its scope can change
                    </td>
                </tr>

                <tr>
                    <td><b><s:message code="task.estimate"/></b></td>
                    <td>Time estimation of how much this task can take. Standard
                        format should be used : *w *d *h *m (weeks, days, hours, minutes ;
                        where * is any number )<br>
                        <i class="fa fa-exclamation-circle"></i>&nbsp;If only number is inputted , default (h) hours will be used.
                    </td>
                </tr>
                <tr>
                    <td><b><s:message code="task.storyPoints"/></b></td>
                    <td>Complexity measurement of newly created task. This value
                        can be changed later on via Edit menu, from Details screen , or
                        from Backlog view
                        <p></p>
                        <p>
                            <i class="fa fa-exclamation-circle"></i>&nbsp;If task is not
                            estimated, have no points , or optional estimate time, please
                            select <input type="checkbox" name="estimated" id="estimated"
                                          value="true"><b>Task without estimation</b> checkbox.<br>If
                            selected this task can be added to sprint ( even active ) and not
                            affecting sprint scope.
                        </p>
                    </td>
                </tr>
                <tr>
                    <td><b><s:message code="task.dueDate"/></b></td>
                    <td>If task should be finished before some date, enter this
                        date here. Tasks from user's projects, with due date soon to
                        present day are shown on home page
                    </td>
                </tr>
                <tr>
                    <td><b><s:message code="task.files"/></b></td>
                    <td>Choose files that you want to add to this task. This can
                        be some documents, excel sheets or image<br> In order to add
                        more than one file, clicking <span id="addMoreFiles"
                                                           class="btn btn-default btn-sm"> <i class="fa fa-plus"></i><i
                                class="fa fa-file"></i>&nbsp;Add more files
					</span> button will add extra slot for it. Attached files are shown on
                        Task detail screen. If file is an image, it will be shown as
                        thumbnail
                    </td>
                </tr>
            </table>

            <hr>
            <%-------------------------EDIT ------------------------------%>
            <a class="anchor" id="task-edit"></a>
            <h3>Editing tasks</h3>
            <p>
                If user has permission to edit task , extra button <span
                    class="btn btn-default btn-sm a-tooltip"> <i
                    class="fa fa-lg fa-pencil"></i>
				</span> will be shown on <b><a href="#task-14"> Task details view</a></b>
                (14).<br> Following actions are available:
            </p>
            <table class="table">
                <tr>
                    <td class="col-md-2"><b><i class="fa fw fa-pencil"></i>&nbsp;Edit
                        task</b></td>
                    <td>allows user to edit already existing task. Each change
                        will be logged and can be reviewed by task "Activity log".<br>
                        <i class="fa fa-exclamation-circle"></i>&nbsp;Please note , that
                        if task is in already started sprint, changing story points value
                        will affect sprint scope
                    </td>
                </tr>
                <tr>
                    <td class="col-md-2"><b><i
                            class="fa fw fa-link fa-flip-horizontal"></i>&nbsp;Link</b></td>
                    <td>starts linking process. If two tasks have something in
                        common, blocks or are duplicating. They can be linked . Every
                        linked task is shown on task details screen.<br> To link task
                        , start by clicking this menu action, or <span
                                class="btn btn-default btn-xxs " style="min-width: 37px;">
							<i class="fa fa-plus"></i><i
                                class="fa fa-lg fa-link fa-flip-horizontal"></i>
					</span> button. Choose link relation, and then start typing in field ID or
                        name of task that should be linked
                    </td>
                </tr>
                <tr>
                    <td class="col-md-2"><b><i class="fa fw fa-sitemap"></i>&nbsp;Add
                        subtask</b></td>
                    <td>shows create subtask screen which is nearly the same as
                        regular task create screen , but without story points section
                    </td>
                </tr>
                <tr>
                    <td class="col-md-2"><b> <i class="fa fw fa-plus"></i>
                        Create linked task
                    </b></td>
                    <td>starts creation of new task but automatically will link
                        newly created task with currently shown one.<br>Additionally
                        new task's project will be locked to current task project.<br>If
                        task is closed this option will be shown as extra <span
                                class="btn btn-sm btn-default"><i class="fa fw fa-plus"></i></span>
                        button
                    </td>
                </tr>
                <tr>
                    <td class="col-md-2"><b><i class="fa fw fa-file"></i>&nbsp;Attach
                        file</b></td>
                    <td>allows to add files ( documentation , pictures , excels
                        ...). The same action is available via <span
                                class="btn btn-default btn-xxs"> <i class="fa fa-plus"></i>
							<i class="fa fa-lg fa-file"></i>
					</span> button ( shown only if there is at least one file attached )
                    </td>
                </tr>
                <tr>
                    <td class="col-md-2"><b><i class="fa fw fa-level-up"></i>&nbsp;Convert
                        to task</b></td>
                    <td>(subtask only) converts one of subtask into regular task.<br>
                        New next free project ID will be assigned to it. Additionally both
                        task will be linked with "relates to"
                    </td>
                </tr>
            </table>
            <hr>
            <%-----------------------------REMOVE------------------------------%>
            <a class="anchor" id="task-remove"></a>
            <h3>Removing tasks</h3>
            <p>Sometimes it can happen that task needs to be deleted. This is
                not recommended course of actions, and in most of the times task
                should be updated, renamed to match desired state. If task needs to
                be deleted, only project administrators can perform this operation.</p>
            <p>Please be aware that removing task should not be used for
                tasks in active sprints etc. and it should be used in very rare
                cases</p>
            <p>
                <u><i class="fa fa-exclamation-circle"></i> Removing task will
                    purge all worklogs, comments, files and subtasks. Only worklog
                    information about task removal will be added for project.<br>
                    All other information will be lost and cannot be restored!</u>
            </p>

            <hr>
            <%-------------------------IMPORTING ------------------------------%>
            <a class="anchor" id="task-import"></a>
            <h3>Importing tasks</h3>
            <p>
                If there is a need to create more than one task, instead of manually
                adding them one by one, there is possibility to import them in one
                bulk.<br> In order to import tasks, navigate to Task create
                screen and choose <b><span style="color: black"><i
                    class="fa fa-download"></i> Import</span></b> tab. <br> Here we can
                either import previously exported file ( either excel or xml) or
                download template and fill task in it.<br> After file will be
                completed, it can be then imported using that screen.<br> Every
                new task will have assigned new ID , based on which project was
                chosen <br> <br> <i class="fa fa-exclamation-circle"></i>&nbsp;Please
                remember to choose project into which new task will be imported. By
                default , user's active project is always selected.


            </p>
            <hr>
            <%-------------------------EXPORTING ------------------------------%>
            <a class="anchor" id="task-export"></a>
            <h3>Exporting tasks</h3>
            <p>
                If there is a need to transfer task from one project to another or
                move it to another server, then can be exported using <a
                    class="btn btn-default"> <i class="fa fa-upload"></i> Export
            </a> button from All tasks screen. <br> Clicking it, will reveal
                checkboxes next to every visible on screen task. All selected task
                then can be exported either to excel or xml file.<br> <br>
                <i class="fa fa-exclamation-circle"></i>&nbsp;Information like: task
                ID, comments, attached files and worklog is not transfered into
                exported file in current version
            </p>
            <hr>
            <%-------------------------WORKING ------------------------------%>
            <a class="anchor" id="task-work"></a>
            <h3>Working with tasks</h3>
            <p>
                There are couple ways to work with task within application. All
                depends on team agreements and there are no stiff rules how to work
                with task. <br> However there are couple recommended things and
                some rules.
            </p>
            <ol>
                <li>Before starting working on task it should be estimated (
                    either time or story points if working in agile)
                </li>
                <li>Tasks can be worked on without sprint, but tracking whole
                    project will be then more difficult
                </li>
                <li>When sprint was started and one of team members would like
                    to start working on it, he should be assigned to it ( either by
                    project admin, or himself using <b><a href="#task-17">Assign
                        button</a></b>)
                </li>
                <li>Add <b><a href="#task-4">tags</a></b> to help categorize
                    project tasks better ( only project administrators, Administrator or task owner)
                </li>
                <li>If project is time tracked, work should be logged. It can
                    be inputed manually by using <b><a href="#worklogging">Log
                        work</a></b> <br>If project is Scrum based, time can still be
                    tracked, which is very helpful especially on earlier stages of
                    project to correctly map how much time is consumed on tasks
                </li>
                <li>Include files , docs or pictures to help developer
                    understand task
                </li>
                <li>Task type is very good to group many other tasks/user
                    stories. After creating it , all tasks can be linked with "relates
                    to" link type
                </li>
                <li>To stay up-to-date with tasks changes , subscribe as Task
                    watcher , using
                    <button class="btn btn-default btn-sm">
                        <i id="watch_icon" class="fa fa-lg fa-eye"></i>
                    </button>
                    button <b><a href="#task-15">Task details view</a></b>(15)
                </li>
                <li>Comments are very helpful to post more information or
                    indicate what has been done on task
                </li>
            </ol>

            <a class="anchor" id="worklogging"></a>
            <h4>Logging work</h4>
            <p>
                To help better track project it's recommended to track time for each
                task, regardless if project is time based or Scrum type.<br>Time
                can be logged either from task details page , or directly from Agile
                board. Click
                <button class="btn btn-default btn-sm worklog">
                    <i class="fa fa-lg fa-calendar"></i> Log work
                </button>
                button to show logging modal window.<br>On it , input how much
                time was spent on this task in format <i>*w *d *h *m</i> (weeks,
                days, hours, minutes ; where * is any number ).<br>If work was
                done in other date ( for ex. yesterday ) , mark correct date and
                time in <b>Date</b> and <b>Time</b> fields.<br> <br>By
                default remaining time will be automatically reduced, or it can be
                manually set by selecting <b><input type="radio"
                                                    name="estimate_reduce" id="estimate_manual" value="auto">
                Set manually</b> radio button and inputing remaining time ( same format
                applies).<br>The remaining time can never be less than 0.<br>If
                more work was logged in task, it will be marked on task detials
                screen accordingly.
            </p>
            <a class="anchor" id="taskstatus"></a>
            <h4>Task status</h4>
            <p>Tasks can be in following statuses:
            <ul>
                <li><t:state state="TO_DO"/> - new task, which can be taken
                    by developer
                </li>
                <li><t:state state="ONGOING"/> - some work has been started
                    on this task.
                </li>
                <li><t:state state="BLOCKED"/> - task on hold, because one
                    some issues are preventing from continuing work on it
                </li>
                <li><t:state state="COMPLETE"/> - task has been completed,
                    only some review or testing left to be done
                </li>
                <li><t:state state="CLOSED"/> - finished task, all work done</li>
            </ul>

            </p>
            <h4>Timer</h4>
            <p>
                If user would like to track time in real time, timer can be set for
                task.To do so , click
                <button class="btn btn-default btn-sm">
                    <i class="fa fa-lg fa-clock-o"></i> Start time
                </button>
                button. This action will start timer ( Information about active task
                will be shown at the bottom of screen). To stop it click <a
                    class="btn btn-default btn-xxs"> <i class="fa fa-lg fa-clock-o"></i>
            </a> button located on page bottom or
                <button class="btn btn-default btn-sm">
                    <i class="fa fa-lg fa-clock-o"></i> Stop time
                </button>
                on task details page. Timer will also be automatically stopped ( and
                work logged ) when task is closed. Please note that it won't be
                possible to close task if there is other user's active timer on this
                task
            </p>
            <p>
                <i class="fa fa-exclamation-circle"></i>&nbsp;If more than one day
                is on timer , user will be asked to confirm if he in fact would like
                to log this time. Please remember that Tasker uses 8h = 1d
                conversion. This can be changed by admin
            <hr>
            <%--------------------------------SUBTASK ------------------------------------%>
            <a class="anchor" id="task-subtask"></a>
            <h3>Subtasks</h3>
            <p>
                Sometimes task can have multiple things to be done within.<br>
                In order to better track to do list , subtask can be created for it.<br>
                Subtask is just like regular task, but without story point estimate
                , and is permanently linked to parent task. <br> It's creation
                process is done via <b><a href="#task-edit">Edit menu</a></b>
            </p>
            <p>Substaks can always be converted to regular task using Edit
                menu. It's impossible to do other way</p>
            <p>
                <i class="fa fa-exclamation-circle"></i>&nbsp; Time logged within
                subtasks will always be added to it's parent task.
            </p>
            <hr>
            <%--------------------------------AGILE ------------------------------------%>
            <a class="anchor" id="agile"></a>
            <h2>Agile</h2>
            <p>Main feature in Tasker is a way to track project progress
                using agile methodology. When used correctly, it can provide very
                good work feedback, help to plan future features and present big
                picture on that project.</p>
            <p>When project is created, it's type is set ( this cannot be
                changed after creation). Base on this type , correct board will be
                always shown for end user.</p>
            <p>
                Under <b>Agile<span class="caret"></span></b> main menu, last
                visited project boards are listed (+ active project's board in bold)
                and additionally all users project's boards are available under <b><i
                    class="fa fa-list"></i> <s:message code="agile.showAll"
                                                       text="Show all"/></b> menu option
            </p>


            <hr>
            <%-----------------------------	SCRUM ---------------------------------%>
            <a class="anchor" id="scrum"></a>
            <h3>SCRUM</h3>
            <p>
            <blockquote>
                SCRUM is an iterative and incremental agile software development
                methodology for managing product development. It defines "a
                flexible, holistic product development strategy where a development
                team works as a unit to reach a common goal", challenges assumptions
                of the "traditional, sequential approach" to product development,
                and enables teams to self-organize by encouraging physical
                co-location or close online collaboration of all team members, as
                well as daily face-to-face communication among all team members and
                disciplines in the project. A key principle of Scrum is its
                recognition that during production processes, the customers can
                change their minds about what they want and need (often called
                "requirements churn"), and that unpredicted challenges cannot be
                easily addressed in a traditional predictive or planned manner. As
                such, Scrum adopts an empirical approach—accepting that the problem
                cannot be fully understood or defined, focusing instead on
                maximizing the team's ability to deliver quickly and respond to
                emerging requirements.<a class="superscript" target="_blank"
                                         href="https://en.wikipedia.org/wiki/Scrum_(software_development)">[source]</a>
            </blockquote>
            SCRUM should be project chosen methodology, if there are many things
            to create from scratch, lots of new features and some bugfixes. It's
            very good in tracking how team progresses within new areas or known
            ones, but with new project
            </p>
            <a class="anchor" id="scrum-backlog"></a>
            <h4>
                <i class="fa fa-book"></i> Backlog
            </h4>
            <p>All tasks to be done, or in progress in selected project are
                displayed here. This is main planning view, on which project
                administrators (for ex. Scrum Master or Product Owner) can plan
                future work , and set its priorities</p>
            <h5>
                <b>Sprints</b>
            </h5>
            <ol>
                <li>In order to create new sprint , click
                    <button class="btn btn-default btn-sm">
                        <i class="fa fa-lg fa-plus"></i>
                    </button>
                    create sprint button. This will create sprint with next number,
                    and allow to add tasks into it.<br>After creation, this sprint
                    will also be shown on task creation screen, allowing to straight
                    away add new task to it.<br>This sprint won't be active until
                    it's started.
                </li>
                <li>To start sprint , click
                    <button class="btn btn-default btn-sm">
                        <i class="fa fa-lg fa-play"></i>&nbsp; Start
                    </button>
                    button , which will be shown, when hover over sprint. This will
                    show Start sprint modal , where sprint start and end date must be
                    inputed.
                </li>
                <li>To end sprint, click <a class="btn btn-default btn-sm ">
                    <i class="fa fa-lg fa-check"></i>&nbsp;Finish
                </a> button , shown on hover of active sprint.<br> All non-finished
                    tasks from that sprint will be moved back to backlog, and then can
                    be added to next sprint.
                </li>
            </ol>
            <p>
                <i class="fa fa-exclamation-circle"></i> Sprint cannot be started if
                within it, there is at least one task which is not estimated (
                unless it has "<b>Task without estimation</b>" marked on <b><a
                    href="#task-create">Create</a></b>/<b><a href="#task-edit">Edit</a></b>
                view)
            </p>
            <p>
                <i class="fa fa-exclamation-circle"></i> Start date is always taken
                when generating reports. End date is for team information, and will
                be overwritten with correct end date and time, when sprint will be
                ended
            </p>
            <p>
                <i class="fa fa-exclamation-circle"></i> There can be only one
                active sprint at the time. Also sprint start date can't overlap
                already finished sprint's end date
            </p>
            <h5>
                <b>Tasks</b>
            </h5>
            <p>
                All task that are not yet done, and not assigned to sprint ,are
                listed in this section of backlog view. When new sprint is created
                drag and drop tasks from this section, to assign them to it.<br>
                You can order tasks based on how tasks are imporant, so that in next
                sprint they can be taken as first.<br> To order tasks drag and
                drop them, and confirm new task order by clicking <span
                    class="btn btn-default"><i class="fa fa-floppy-o"></i>&nbsp;Save
					order </span> button <br>Additionally right clicking on task will
                show context menu , on which we can assign this task to one of
                existing sprints, or move this task to top or bottom of backlog.
                Moving to top/bottom of backlog still requires confirmation of new
                tasks order.
            </p>
            <a class="anchor" id="scrum-board"></a>
            <h4>
                <i class="fa fa-list-alt"></i> Board
            </h4>
            <p>
                This is main agile view for the team. All tasks, that team supposed
                to work now, on are listed here. Each task is presented in form of
                card with information like name, task type, story points progress
                and current assignee.<br> Following actions are available:
            <ul>
                <li><b>Change task state</b> - drag and drop task from one
                    section to another to quickly change it's state, without a need to
                    enter task details
                </li>
                <li><b>Assign task</b> - if tasks is not yet assigned to
                    anyone, you can assign it to one of team members. Click
                    <button class="btn btn-default btn-xxs">
                        <i class="fa fa-lg fa-user"></i>
                    </button>
                    button on task card to show Assign modal window
                </li>
                <li><b>Log work</b> if currently logged user is assigned to one
                    of task, he/she can log time spent on this task from this screen
                    clicking log work button
                    <button class="btn btn-default btn-xxs " style="margin-left: 5px">
                        <i class="fa fa-lg fa-clock-o"></i>
                    </button>
                    in task card.
                </li>
                <li><b>Filter tasks by tags</b> Next to Sprint number and date
                    , all tags for tasks within sprint are presented. You can filter
                    task with this tag , by clicking it . Click it again to reset tag
                    filter ( or select another to filter by it now)
                </li>
                <li><b>Order tasks</b> as on Backlog view , tasks can be
                    ordered to indicate which task is more important. To order, drag
                    and drop task within "To do" column , and confirm new task order by
                    clicking <span class="btn btn-default"><i
                            class="fa fa-floppy-o"></i>&nbsp;Save order </span> button
                </li>
            </ul>
            <a class="anchor" id="scrum-reports"></a>
            <h4>
                <i class="fa fa-line-chart"></i> Reports
            </h4>
            <p>On this view summary of each sprint is presented. By default
                last ( or active ) sprint is shown</p>
            <h5>
                <b><i class="fa fa-line-chart"></i> Burndown chart</b>
            </h5>
            Shows how team was working on assigned tasks. Remaining line should
            always be near "Ideal" . If it stays above, it means that team is
            either not working efficiently, or task was underestimated in story
            points. Staying below means the opposite. In case of bad estimation
            it should be adjusted in next sprints. All in all, at the end of the
            sprint Ideal and Remaining should meet.<br> If project is time
            based, instead of story points , estimated time will be taken to
            count remaining/burned time
            <h5>
                <b><i class="fa fa-bar-chart"></i> Logged time</b>
            </h5>
            This chart shows how time was logged per day on whole project
            <h5>
                <b><i class="fa fa-calendar"></i> Events in sprint</b>
            </h5>
            All sprint events like creation of new tasks added to sprint, work
            logged or closed tasks are listed in here. Events like comments or
            task creation for project are omitted on this view as not relevant to
            sprint
            <h5>
                <b><i class="fa fa-list-ul"></i>Sprint summary</b>
            </h5>
            This tab summarize how many task were completed ( and their story
            points value) and which were not. Also total time spent on tasks in
            this sprint is added and shown here as well
            <hr>
            <%-----------------------------	KANBAN ---------------------------------%>
            <a class="anchor" id="kanban"></a>
            <h3>Kanban</h3>
            <p>
            <blockquote>
                Kanban is a method for managing knowledge work with an emphasis on
                just-in-time delivery while not overloading the team members. In
                this approach, the process, from definition of a task to its
                delivery to the customer, is displayed for participants to see. Team
                members pull work from a queue. Kanban in the context of software
                development can mean a visual process-management system that tells
                what to produce, when to produce it, and how much to produce -
                inspired by the Toyota Production System and by Lean manufacturing.<a
                    class="superscript" target="_blank"
                    href="https://en.wikipedia.org/wiki/Kanban_(development)">[source]</a>
            </blockquote>
            </p>
            <a class="anchor" id="kanban-board"></a>
            <h4>
                <i class="fa fa-list-alt"></i> Board
            </h4>
            <p>
                Main view for Kanban. Most of team work will be done on it. For
                actions that can be performed please refer to <b><a
                    href="#scrum-board"> Scrum board section</a></b> they are generic
            </p>
            <p>
                The only one big difference compared to Scrum is, that Kanban don't
                have sprints. To track milestones we use releases in it.<br>
                Release indicate that some bigger part of changes, features or
                bugfixes has been done , and it should be delivered (for ex. to
                client)
            </p>
            <p>
                To create new release, click <span class="btn btn-default"> <i
                    class="fa fa-clipboard"></i>&nbsp;New Release
				</span> button which is available for project administrators on <strong><a href="#proj-details">Project
                details</a></strong> page.<br>Clicking
                it will show modal window, where name of release and optional
                comment can be inputed. After it , all closed tasks will be moved to
                this release , and new release will be started.<br>From now on,
                closed tasks from that release will be hidden, and can be reviewed
                on reports page only. It can be reopened, but then it will be part
                of new release
            </p>
            <h4>
                <i class="fa fa-line-chart"></i> Reports
            </h4>
            On this view summary of each release is presented. By default last (
            or current ) release is shown
            <h5>
                <b><i class="fa fa-line-chart"></i> Burndown chart</b>
            </h5>
            Shows how team was working on during releases. Main chart shows how
            many tasks were open/in progress/closed in given time frame
            <h5>
                <b><i class="fa fa-bar-chart"></i> Logged time</b>
            </h5>
            Time logged in project in selected release
            <h5>
                <b><i class="fa fa-calendar"></i> Events in release</b>
            </h5>
            All release events like creation of new tasks, work logged or closed
            tasks are listed in here. Events like comments or omitted on this
            view as not relevant to release
            <h5>
                <b><i class="fa fa-list-ul"></i> Release summary</b>
            </h5>
            This tab summarize how many task were completed with time spent.
            Total time spent on all tasks in this sprint is added and shown here
            as well
            <hr>
            <a class="anchor" id="roles"></a>
            <h3>Roles</h3>
            <p>
                In order to not mess up application each user has application role
                assigned to keep things in order. <br> Following roles are
                available
            <table class="table">
                <tr>
                    <td class="col-md-2"><b><s:message code="role.admin"/></b></td>
                    <td>All below and
                        <ul>
                            <li>Edit any project</li>
                            <li>Assign users to any project</li>
                            <li>Add to project administrators</li>
                            <li>Edit any tasks</li>
                            <li>Change state and priority of any task in any project</li>
                            <li>Delete task</li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td class="col-md-2"><b><s:message code="role.poweruser"/></b></td>
                    <td>Power user <br>Can do all below and
                        <ul>
                            <li>Edit project to which he is assigned</li>
                            <li>Assign users to project (if is project admin)</li>
                            <li>Add to project administrators (if is project admin)</li>
                            <li>Edit tasks in his projects</li>
                            <li>Link tasks in his project</li>
                            <li>Start/stop sprint if is project admin</li>
                            <li>Perform release if is project admin</li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td class="col-md-2"><b><s:message code="role.user"/></b></td>
                    <td>Regular user <br>Can do all below and
                        <ul>
                            <li>Create tasks in project in which he is assigned</li>
                            <li>Edit tasks he created</li>
                            <li>Change state or priority of task he is assigned or
                                created
                            </li>
                            <li>Comment on task which is not in Closed state</li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td class="col-md-2"><b><s:message code="role.viewer"/></b></td>
                    <td>Demo user <br>
                        <ul>
                            <li>Login into application</li>
                            <li>Change user settings, add avatar</li>
                            <li>Watch tasks</li>
                            <li>View all users in Tasker</li>
                            <li>View tasks in project he is assigned to</li>
                            <li>View Task activity log</li>
                            <li>View agile boards, reports and backlog</li>
                            <li>Invite new users to application</li>
                        </ul>
                    </td>
                </tr>
            </table>
            <p>Additionally in order to edit task ( change name, description, add tags ) , user have to be either task
                owner, <s:message code="role.admin"/> or project administrator</p>
            <p>To work with task (change state, log hours ) , user have to be task assignee or <s:message
                    code="role.admin"/></p>
            <security:authorize access="hasRole('ROLE_ADMIN')">
                <%------------------------------ADMIN ---------------------------------------%>
                <a class="anchor" id="admin"></a>
                <h2>Administration</h2>
                <span class="admin-button">Admin</span>
                <p>
                    Being administrator is not a hard work, presuming that it will be
                    used by mature users :)<br> There are only couple activities
                    that will be performed, especially at the beginning when users will
                    start using application There are couple things that Tasker
                    administrators can do within application. For full description
                    check <a href="#roles">Roles</a> section
                </p>
                <p>
                    Whenever user with role Administration is logged he will have extra
                    option in main menu<br>(and in fact this help section will be
                    shown)<br>
                </p>
                <p>
                    <i class="menu-indicator fa  fa-lg fw fa-toggle-right"></i>
                    <i class="fa fa-wrench"></i> &nbsp;<strong><s:message code="menu.manage" text="Settings"/></strong>
                    Options available: <a class="anchor" id="a_users"></a>
                </p>
                <ul>
                    <li><i class="fa fa-cogs"></i> Manage application - manage
                        application changing logo , or add/edit themes
                    <li><i class="fa fa-users"></i> <s:message
                            code="menu.manage.users"/> - similar to regular Show users
                        screen but with additional option to assign roles. Please be aware
                        that it's not possible to remove last admin from application (
                        there has to be at least one at any time)
                    </li>
                    <li><i class="fa fa-lg fa-check-square"></i> <s:message
                            code="menu.manage.tasks"/> - Various task related actions,
                        mostly depreciated, used during development phase
                    </li>
                </ul>
                <a class="anchor" id="a_projects"></a>
                <hr>
                <h3>Projects</h3>
                <p>
                    Having administration rights, enables <strong><a href="#proj-edit">Edit
                    Project</a></strong> operation on all projects created in Tasker. <br>Here
                    are some system information if manual operations will be required
                </p>
                <ul>
                    <li>After project was created it's created in `project` db</li>
                    <li>All task attachments are located in <code>${projHome}/ID/TASK_ID</code>
                        where `ID` is unique project id and
                    </li>
                </ul>
                <hr>
                <a class="anchor" id="a_manage"></a>
                <h3>Manage application</h3>
                <p>
                    Tasker comes with some predefines settings that are included in
                    <code>tasq.war</code>
                    which should be overrides while setting application. Application
                    logo and themes and e-mail settings can be changed anytime by any
                    administrator.<br> To enter application management use admin
                    <i class="menu-indicator fa  fa-lg fw fa-toggle-right"></i>
                    <i class="fa fa-wrench"></i> &nbsp;<strong><s:message code="menu.manage" text="Settings"/></strong>
                    sidebar menu option and navigate to <b><i class="fa fa-cogs"></i> Manage
                    application</b> option
                </p>
                <a class="anchor" id="a_logo"></a>
                <h4>Application logo</h4>
                <p>
                    In order to customize application , and adapt it a bit more to team
                    requirements it's possible to change application logo.<br>
                    This option is available <b><i class="fa fa-cogs"></i> Manage
                    application</b> screen. <br> Logo changing is made by uploading
                    new image which will overwrite logo file in
                    <code>${projHome}/avatar/logo.png</code>
                    <br>This can be done manually but it's recommended to do using
                    application to guarantee best look and feel.<br>To upload new
                    logo , hover mouse cursor over dashed area with current logo , to
                    show <span class="btn btn-default"><s:message
                        code="manage.logo.change"/></span> button. <br> Newly uploaded
                    logo should meet following criteria:
                <ul>
                    <li>Not bigger than 100Kb</li>
                    <li>Not more than 150px height and 150px width</li>
                    <li>Should be visible on all themes</li>
                </ul>
                <p>
                    Logo preview on current users theme is shown after upload. If it
                    looks good, save changes.<br> If for some reason the currently
                    selected logo must be discarded, default logo can be restored, by
                    clicking <span class="btn btn-warning btn-sm"><i
                        class="fa fa-exclamation-circle"></i>&nbsp;Restore default</span> button
                </p>
                <p>
                    <i class="fa fa-exclamation-circle"></i>&nbsp; As logo is replaced
                    on server itself it's possible that old logo will still be cached
                    in browser. To view changes , user's should clear cache or force
                    refresh page by pressing Ctrl+F5<br>It's not recommended to
                    change logo too often

                </p>
                <a class="anchor" id="a_url"></a>
                <h4>Application URL</h4>
                <p>
                    URL of application is used in all e-mails sent by application (
                    like events or account approving ). It must be valid url, otherwise
                    e-mails will be sent with wrong url's to task, account confirmation
                    etc. In order to obtain current url from browser click <a
                        class="btn btn-default btn-sm clickable"><i
                        class="fa fa-arrow-down"></i>&nbsp;Get url</a> button
                </p>
                <a class="anchor" id="a_dir"></a>
                <h4>Application Directory</h4>
                <p>Default directory where all files are stored can be changed
                    in this section. When changing to new path, application will try to
                    move all currently existing files to new location Please ensure
                    that application has correct privileges to access new directory</p>
                <p>
                    <i class="fa fa-exclamation-circle"></i>&nbsp; Do not change
                    application directory wheb application is live and used by users,
                    as it might produce some errors for users currently using it
                </p>

                <a class="anchor" id="a_email"></a>
                <h4>Email settings</h4>
                <p>In this section , you can set all Email related settings.
                    SMTP host name, port username and password. Default value points to
                    localhost server on port 25 without user and password.</p>

                <a class="anchor" id="a_theme"></a>
                <h4>Themes</h4>
                <p>
                    Tasker comes with default theme in nice blue color. Application
                    admin can easily add new themes, or edit exisitng ones.<br>
                    Default theme is only one that is blocked for edition.<br>All
                    themes are listed in table with small preview how they look (
                    including application logo)
                </p>
                <p>
                <h5>
                    <b>Create theme</b>
                </h5>
                To add new theme, click <span class="btn btn-default btn-sm"><i
                    class="fa fa-plus"></i><i class="fa fa-paint-brush"></i> Create new
					theme</span> button on <b><i class="fa fa-cogs"></i> Manage
                application</b> view.<br>Modal window will be shown with fields:
                <table class="table">
                    <tbody>
                    <tr>
                        <td class="col-md-2"><b>Name</b></td>
                        <td>Name of theme. This name will be shown in combobox for
                            all users on their settings screen. This name must be unique and
                            preferably not to long
                        </td>
                    </tr>
                    <tr>
                        <td><b>Font</b></td>
                        <td>Font in which whole application will be shown (all
                            texts).Couple fonts are available too chose from. Some more can
                            be added within application code itself.
                        </td>
                    </tr>
                    <tr>
                        <td><b>Color</b></td>
                        <td>Theme color for application. All navigation elements,
                            headers , badges will have this color applied
                        </td>
                    </tr>

                    <tr>
                        <td><b>Inverse color</b></td>
                        <td>Color that will be applied to any text placed on menus,
                            headers etc.<br>This should be contrasting to theme color
                            to improve readability
                        </td>
                    </tr>
                    </tbody>
                </table>
                <h5>
                    <b>Edit theme</b>
                </h5>
                <p>
                    To edit one of existing themes, click <i class="fa fa-pencil"></i>&nbsp;
                    in corresponding theme row. This will show same modal window as in
                    creation of themes, but with theme filled values.
                </p>
                <a class="anchor" id="a_manage_task"></a>
                <h3>Manage tasks</h3>
                <p>It's somtimes possible that some bad error was made while
                    performing operations on tasks. In that case application
                    administrator have a possibility to remove single work log from
                    task</p>
                <p>
                    To remove worklog , enter task details page , and switch to <span
                        class="btn btn-default btn-sm"><i class="fa fa-newspaper-o"></i>
						Activity log</span> tab at the bottom of the page. All activities
                    worklogs related to this task will be shown here. Hover mouse over
                    log that should be deleted and click on <i class="fa fa-trash-o"></i>
                    to remove it.
                </p>
                <p>
                    After successful deletion you will be moved to Manage tasks page to
                    force reload logs of all tasks within that project. <br> This
                    step is mandatory after every work log deletion!
                </p>
                <p>
                    <i class="fa fa-exclamation-circle"></i> <u>Please note this
                    option should'n be used to frequently as it can produce very bad
                    results in sprints, logged work etc. It's reserved only for cases
                    when everything else fails.</u>
                </p>
            </security:authorize>
            <hr>
            <p class="centered">
                Tasker v. ${version}<br>created by <b>Jakub Romaniszyn</b> <a
                    href="http://q-programming.pl/">http://q-programming.pl/</a> and is
                under GNU GPL License.
            </p>
        </div>
    </div>
    <div id="menu" class="col-md-2 col-sm-2 col-lg-2" role="complementary">
        <nav class="bs-docs-sidebar hidden-print hidden-xs hidden-sm affix">
            <ul class="nav bs-docs-sidenav">
                <li><a href="#about">About Tasker</a></li>
                <li><a href="#main">Application menus</a>
                    <ul class="nav">
                        <li><a href="#personal">Personal menu</a></li>
                        <li><a href="#settings">User Settings menu</a></li>
                        <li><a href="#events">Events</a></li>
                        <li><a href="#watch">Watching</a></li>
                    </ul>
                </li>
                <li><a href="#users">Users</a></li>
                <li><a href="#proj"><s:message code="project.projects"
                                               text="Projects"/></a>
                    <ul class="nav">
                        <li><a href="#proj-view">Browsing all projects</a></li>
                        <li><a href="#proj-details">View projects details</a></li>
                        <li><a href="#proj-create">Creating projects</a></li>
                        <li><a href="#proj-edit">Managing projects</a></li>
                    </ul>
                </li>
                <li><a href="#task"><s:message code="task.tasks"
                                               text="Tasks"/></a>
                    <ul class="nav">
                        <li><a href="#task-view">Browsing all tasks</a></li>
                        <li><a href="#task-details">View task details</a></li>
                        <li><a href="#task-create">Creating tasks</a></li>
                        <li><a href="#task-edit">Editing tasks</a></li>
                        <li><a href="#task-remove">Removing Tasks</a>
                        <li><a href="#task-import">Importing tasks</a></li>
                        <li><a href="#task-export">Exporting tasks</a></li>
                        <li><a href="#task-work">Working with tasks</a></li>
                        <li><a href="#task-subtask">Subtasks</a></li>
                    </ul>
                </li>
                <li><a href="#agile"><s:message code="agile.agile"
                                                text="Agile"/></a>
                    <ul class="nav">
                        <li><a href="#scrum">SCRUM</a>
                            <ul class="nav">
                                <li><a href="#scrum-backlog">Backlog</a></li>
                                <li><a href="#scrum-board">Board</a></li>
                                <li><a href="#scrum-reports">Reports</a></li>
                            </ul>
                        </li>
                        <li><a href="#kanban">Kanban</a>
                            <ul class="nav">
                                <li><a href="#kanban-board">Board</a></li>
                                <li><a href="#kanban-reports">Reports</a></li>
                            </ul>
                        </li>
                    </ul>
                </li>
                <li><a href="#roles">Roles</a></li>
                <security:authorize access="hasRole('ROLE_ADMIN')">
                    <li><a href="#admin">Administration</a>
                        <ul class="nav">
                            <li><a href="#a_users">Users</a></li>
                            <li><a href="#a_projects">Projects</a>
                            <li><a href="#a_manage">Manage application</a>
                                <ul class="nav">
                                    <li><a href="#a_logo">Changing logo</a></li>
                                    <li><a href="#a_dir">Changing main directory</a></li>
                                    <li><a href="#a_theme">Themes</a></li>
                                </ul>
                            </li>
                            <li><a href="#a_manage_task">Manage tasks</a></li>
                        </ul>
                    </li>
                </security:authorize>
            </ul>
        </nav>
    </div>
</div>
<script src="<c:url value="/resources/js/imageMapResizer.min.js" />"></script>
<script>
    $(document).ready(function () {
        $('map').imageMapResize();
        $('body').scrollspy({
            target: '#menu'
        })

    });


</script>