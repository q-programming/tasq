<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<%@page import="com.qprogramming.tasq.task.TaskPriority" %>
<security:authentication property="principal" var="user"/>
<security:authorize access="hasRole('ROLE_ADMIN')">
    <c:set var="is_admin" value="true"/>
</security:authorize>
<script src="<c:url value="/resources/js/hopscotch.js" />"></script>
<link href="<c:url value="/resources/css/hopscotch.css" />" rel="stylesheet"
      media="screen"/>
<div class="white-frame" style="overflow: auto;">
    <div class="pull-right">
        <a class="btn btn-default a-tooltip pull-right" style="padding: 6px 11px;" href="#" title=""
           data-placement="bottom"
           data-original-title="Manage project"><i class="fa fa-wrench"></i></a>
    </div>
    <div class="pull-right">
        <a class="btn btn-default a-tooltip pull-right" href="#" title="" data-placement="bottom"
           data-original-title="Set as active project"> <i class="fa fa-refresh"></i>
        </a>
        <a class="btn btn-default a-tooltip pull-right" title="" data-placement="bottom"
           data-original-title="Project members">
            <i class="fa fa-users"></i>
        </a>
    </div>
    <h3>[TST] Testing Project</h3>
    Project to test application
    <hr>
    <div class="progress">
        <div class="progress-bar progress-bar-warning a-tooltip" style="width: 42.857142857142854%" title=""
             data-original-title="3&nbsp;To Do">
            <span>3&nbsp;To Do</span>
        </div>
        <div class="progress-bar a-tooltip" style="width: 28.571428571428573%" title=""
             data-original-title="2&nbsp;In progress">
            <span>2&nbsp;In progress</span>
        </div>
        <div class="progress-bar progress-bar-success a-tooltip" style="width: 0.0%" title=""
             data-original-title="0&nbsp;Completed">
        </div>

        <div class="progress-bar progress-bar-closed  a-tooltip" style="width: 28.571428571428573%" title=""
             data-original-title="2&nbsp;Closed ">
            <span>2&nbsp;Closed </span>
        </div>
        <div class="progress-bar progress-bar-danger a-tooltip" style="width: 0.0%" title=""
             data-original-title="0&nbsp;Blocked">
        </div>
    </div>
    <div id="chart_divarea" class="row" style="height: 300px; width: 90%; margin: 20px auto;">
        <img class="responsive" src="<c:url value="/resources/img/help/sample_chart.png"/>">
    </div>
    <div style="display: inherit; font-size: small; float: right">
        <span id="" class="clickable" data-all="false"><span id="moreEventsCheck"><i class="fa fa-square-o"></i></span> Show more than 30 days</span>
    </div>

    <div style="display: table; width: 100%">
        <div style="display: table-cell; width: 600px">
            <h3>
                Latest events</h3>
            <div class="text-center">
                <ul id="eventsTable_pagination_top" class="pagination">
                    <li class="active"><a title="Current page is 1">1</a></li>
                    <li class="pointer-cursor"><a title="Go to page 2">2</a></li>
                    <li class="pointer-cursor"><a title="Go to page 3">3</a></li>
                    <li class="pointer-cursor"><a title="Go to page 4">4</a></li>
                    <li class="pointer-cursor"><a title="Go to next page">&gt;</a></li>
                    <li class="pointer-cursor"><a title="Go to last page">&gt;&gt;</a></li>
                </ul>
            </div>
            <div>
                <table id="eventsTable" class="table table-condensed">
                    <tbody>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">07-10-2016 15:58</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;created task <a href="#">[TST-4] Some sample
                            task</a></td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:38</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;changed assignee <a href="#">[TST-1]
                            Sample task</a>
                            <div class="quote">Demo User</div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:31</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;commented <a href="#">[TST-1] Sample
                            task</a>
                            <div class="quote">All blockers eliminated. Moving on with work</div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:30</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;changed assignee <a href="#">[TST-1]
                            Sample task</a>
                            <div class="quote">Paweł Korga</div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:30</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;closed task <a href="#">[TST-1/3]
                            Some minor already finished task</a></td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:30</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;added subtask <a href="#">[TST-1/3]
                            Some minor already finished task</a></td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:29</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;changed status <a href="#">[TST-1]
                            Sample task</a>
                            <div class="quote">
                                <table class="worklog_table">
                                    <tbody>
                                    <tr>
                                        <td>Blocked</td>
                                        <td>Ongoing</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:29</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;logged work <a href="#">[TST-1/2]
                            Breaks</a>
                            <div class="quote">2h 25m</div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:28</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;edited <a href="#">[TST-1/2]
                            Breaks</a>
                            <div class="quote">
                                <table class="worklog_table">
                                    <tbody>
                                    <tr>
                                        <td colspan="2"><b>Name :</b></td>
                                    </tr>
                                    <tr>
                                        <td>Coffee break</td>
                                        <td>Breaks</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:28</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;logged work <a href="#">[TST-1/2]
                            Breaks</a>
                            <div class="quote">1h</div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:28</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;added subtask <a href="#">[TST-1/2]
                            Breaks</a></td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:27</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;added subtask <a href="#">[TST-1/1]
                            Sub task </a></td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:26</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;linked tasks
                            <div class="quote">TST-1 - TST-2</div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:26</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;closed task <a href="#">[TST-2]
                            Sample bug</a></td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:26</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;logged work <a href="#">[TST-2]
                            Sample bug</a>
                            <div class="quote">45m</div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:26</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;created task <a href="#">[TST-2]
                            Sample bug</a></td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:24</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;logged work <a href="#">[TST-1]
                            Sample task</a>
                            <div class="quote">3h</div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:24</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;edited <a href="#">[TST-1] Sample
                            task</a>
                            <div class="quote">
                                <table class="worklog_table">
                                    <tbody>
                                    <tr>
                                        <td colspan="2"><b>Estimate :</b></td>
                                    </tr>
                                    <tr>
                                        <td>0m</td>
                                        <td>1d</td>
                                    </tr>
                                    <tr>
                                        <td colspan="2"><b>Estimated :</b></td>
                                    </tr>
                                    <tr>
                                        <td>false</td>
                                        <td>true</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                    </tr>
                    <tr class="projEvent">
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td>
                            <div class="time-div">01-04-2015 00:23</div>
                            <a href="#">Jakub Romaniszyn</a>&nbsp;created task <a href="#">[TST-1]
                            Sample task</a></td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="text-center">
                <ul id="eventsTable_pagination_bottom" class="pagination">
                    <li class="active"><a title="Current page is 1">1</a></li>
                    <li class="pointer-cursor"><a title="Go to page 2">2</a></li>
                    <li class="pointer-cursor"><a title="Go to page 3">3</a></li>
                    <li class="pointer-cursor"><a title="Go to page 4">4</a></li>
                    <li class="pointer-cursor"><a title="Go to next page">&gt;</a></li>
                    <li class="pointer-cursor"><a title="Go to last page">&gt;&gt;</a></li>
                </ul>
            </div>

        </div>
        <div style="display: table-cell; padding-left: 30px">
            <h3>
                <a href="#" style="color: black">Tasks</a>
                <div class="pull-right">
                    <div>
                        <a href="#"><span
                                style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
									<i class="fa fa-check-square-o"></i> Hide closed tasks</span></a>
                    </div>
                    <div style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
                        Subtasks
                        &nbsp; <i id="opensubtask" class="fa fa-plus-square clickable a-tooltip" title=""
                                  data-original-title="Show all subtasks"></i> <i id="hidesubtask"
                                                                                  class="fa fa-minus-square clickable a-tooltip"
                                                                                  title=""
                                                                                  data-original-title="Hide all subtasks"></i>
                    </div>
                </div>
            </h3>
            <table class="table table-hover">
                <tbody>
                <tr>
                    <td>
                        <t:type type="USER_STORY" list="true"/>
                    </td>
                    <td>
                        <%
                            pageContext.setAttribute("major", TaskPriority.MAJOR);
                        %>
                        <t:priority priority="${major}" list="true"/>
                    </td>
                    <td><a href="#" style="">
                        [TST-4] Some sample task</a>
                    </td>
                    <td>
                    </td>
                    <td>
                        <div class="progress" style="width: 50px">
                            <div class="progress-bar progress-bar-success a-tooltip" role="progressbar"
                                 aria-valuenow="0"
                                 aria-valuemin="0" aria-valuemax="100" style="width:0%" title=""
                                 data-original-title="0%"></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <t:type type="TASK" list="true"/>
                    </td>
                    <%
                        pageContext.setAttribute("minor", TaskPriority.MINOR);
                    %>

                    <td>
                        <t:priority priority="${minor}" list="true"/>
                    </td>
                    <td><a href="#" style="">
                        [TST-3] New task</a>
                    </td>
                    <td>
                    </td>
                    <td>
                        <div class="progress" style="width: 50px">
                            <div class="progress-bar progress-bar-success a-tooltip" role="progressbar"
                                 aria-valuenow="0"
                                 aria-valuemin="0" aria-valuemax="100" style="width:0%" title=""
                                 data-original-title="0%"></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <t:type type="USER_STORY"/>
                    </td>
                    <td>
                        <%
                            pageContext.setAttribute("critical", TaskPriority.CRITICAL);
                        %>
                        <t:priority priority="${critical}" list="true"/>
                    </td>
                    <td><i class="subtasks fa fa-minus-square" data-task="TST-1" id="subtasksTST-1"></i>
                        <a href="#" style="">
                            [TST-1] Sample task</a>
                        <div style="margin-top: 5px;border-top: 1px solid lightgray;" class="subtaskdiv"
                             id="TST-1subtask">
                            <div style="padding:2px;"><t:type type="SUBTASK" list="true"/><a
                                    href="#" class="subtaskLink ">[TST-1/1] Sub task </a></div>
                            <div style="padding:2px;"><t:type type="IDLE" list="true"/> <a href="#"
                                                                                           class="subtaskLink ">[TST-1/2]
                                Breaks</a></div>
                            <div style="padding:2px;"><t:type type="SUBBUG" list="true"/> <a
                                    href="#" class="subtaskLink closed">[TST-1/3] Some minor already finished
                                task bug</a></div>
                        </div>
                    </td>
                    <td>
                    </td>
                    <td>
                        <div class="progress" style="width: 50px">
                            <div class="progress-bar  a-tooltip" role="progressbar" aria-valuenow="41.0"
                                 aria-valuemin="0"
                                 aria-valuemax="100" style="width:41.0%" title="" data-original-title="41.0%"></div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
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
                onNext: function () {
                    $(".project-menu").show("blind");
                },
                yOffset: -20
            },
            </c:if>
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
                yOffset: -20
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
                placement: "left"
            },
            {
                title: "Personal menu",
                content: 'Personal menu will be shown when clicking on avatar or name. Here we find links to events , watching pages and settings.<br><br> More info can be found <a href="<c:url value="/help#personal"/>" target="_blank">here</a>',
                target: "user-menu",
                placement: "left"
            },
            {
                title: "More tours",
                content: 'Select one of tours to learn more about rest of application',
                target: "more-tours",
                placement: "bottom"
            }
        ]
    };
    $('#start').click(function () {
        hopscotch.startTour(tour);
    });
    // Start the tour!
</script>