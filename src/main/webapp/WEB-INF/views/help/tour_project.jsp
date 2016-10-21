<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<%@page import="com.qprogramming.tasq.task.TaskPriority" %>
<script src="<c:url value="/resources/js/hopscotch.js" />"></script>
<link href="<c:url value="/resources/css/hopscotch.css" />" rel="stylesheet"
      media="screen"/>
<div class="white-frame" style="overflow: auto;">
    <div class="pull-right">
        <a id="project-manage" class="btn btn-default a-tooltip pull-right" style="padding: 6px 11px;" href="#" title=""
           data-placement="bottom"
           data-original-title="Manage project"><i class="fa fa-wrench"></i></a>
    </div>
    <div class="pull-right">
        <a id="project-active" class="btn btn-default a-tooltip pull-right" href="#" title="" data-placement="bottom"
           data-original-title="Set as active project"> <i class="fa fa-refresh"></i>
        </a>
        <a id="project-members" class="btn btn-default a-tooltip pull-right" title="" data-placement="bottom"
           data-original-title="Project members">
            <i class="fa fa-users"></i>
        </a>
    </div>
    <h3>[TST] Testing Project<span id="project-title"></span>
        <a href="<c:url value="/tour"/>" id="go-back" class="btn btn-default btn-success"
           style="margin-left:100px; display:none">
            Click here to go back to tours page
        </a>
    </h3>
    Project to test application
    <hr>
    <div id="project-open-close" class="progress">
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
    <div id="project-chart" class="row" style="height: 300px; width: 90%; margin: 20px auto;">
        <img class="responsive" src="<c:url value="/resources/img/help/sample_chart.png"/>">
    </div>
    <div style="display: inherit; font-size: small; float: right;margin-top: 20px;">
        <span id="project-30" class="clickable" data-all="false"><span id="moreEventsCheck"><i
                class="fa fa-square-o"></i></span> Show more than 30 days</span>
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
                            <div class="time-div" id="project-event">07-10-2016 15:58</div>
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
                        <a id="project-task-closed" href="#"><span
                                style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
									<i class="fa fa-check-square-o"></i> Hide closed tasks</span></a>
                    </div>
                    <div id="project-subtasks"
                         style="display: inherit; font-size: small; font-weight: normal; color: black; float: right">
                        Subtasks
                        &nbsp; <i id="opensubtask" class="fa fa-plus-square clickable a-tooltip" title=""
                                  data-original-title="Show all subtasks"></i> <i id="hidesubtask"
                                                                                  class="fa fa-minus-square clickable a-tooltip"
                                                                                  title=""
                                                                                  data-original-title="Hide all subtasks"></i>
                    </div>
                </div>
            </h3>
            <table id="project-tasklist" class="table table-hover">
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
                title: "Project details",
                content: "Project ID , project name and description. Description can be edited as rich text adding links, numbering etc.",
                target: "project-title",
                placement: "right"
            },
            {
                title: "Project open closed bar",
                content: 'Shows how many tasks in total are open, in progress or closed',
                target: "project-open-close",
                placement: "top",
                xOffset: "center",
                arrowOffset: "center"
            },
            {
                title: "Events chart",
                content: 'Shows chart with latest project events.<br> Open line is in <strong><span style="color:#f0ad4e">orange</span></strong>, closed <strong><span style="color:#488A48">green</span></strong> and <strong><span style="color:#337ab7">blue in between</span></strong> - tasks in progress',
                target: "project-chart",
                placement: "top",
                xOffset: "center",
                arrowOffset: "center",
                yOffset: 100
            },
            {
                title: "More chart events",
                content: 'By default chart is rendered only from last 30 days.<br>You can show all events from very beginning of project by selecting "Show more than 30 days checkbox"',
                target: "project-30",
                placement: "left",
                width: 400,
                yOffset: -22
            },
            {
                title: "Project latest events",
                content: 'All latest events in project.<br>Use pagination navigation at the top or bottom to show next pages',
                target: "project-event",
                placement: "right",
                yOffset: -20
            },
            {
                title: "Project tasks",
                content: 'List of all tasks in project (sorted by ID). This is just for overview, as better option to view project task is via <a href="<c:url value="/tour?page=tasklist"/>" target="_blank">Task List</a>',
                target: "project-tasklist",
                placement: "top",
                xOffset: "center",
                arrowOffset: "center"
            },
            {
                title: "Show closed tasks",
                content: 'By default closed tasks are hidden in this view. In order to view all task de-select this checkbox.',
                target: "project-task-closed",
                placement: "left",
                yOffset: -24
            },
            {
                title: "Show all/hide subtasks",
                content: "If one of tasks have subtasks <i class='fa fa-plus-square'></i> icon will be shown before it's name.<br> After clicking this button all subtasks for every task will be expanded",
                target: "project-subtasks",
                placement: "left",
                yOffset: -23
            },
            {
                title: "Project members",
                content: 'Show modal with all project participants.',
                target: "project-members",
                placement: "left",
                yOffset: -14
            },
            {
                title: "Activate/Deactivate project",
                content: 'set displayed project as active. It will be used while creating new tasks, listing and searching as first choice. It will also be visible on left side menu',
                target: "project-active",
                placement: "left",
                yOffset: -14
            },
            {
                title: "Project management",
                content: 'This option is only visible for project administrators. It moves to project management view.<br>More info about <a href="<c:url value="/help#proj-edit"/>" target="_blank">configuration of projects</a>',
                target: "project-manage",
                placement: "left",
                yOffset: -14,
                width:400,
                onNext: function () {
                    $('#go-back').show();
                }
            },
            {
                title: "Done",
                content: 'Go back to tour page, or start using ${applicationName}',
                target: "project-title",
                placement: "bottom",
                yOffset: 10,
                xOffset: 40,
                width: 400,
                onPrev: function () {
                    $('#go-back').hide();
                },
                arrowOffset: 'center'
            }

        ]
    };
    hopscotch.startTour(tour);
    $(document).on("click", ".hopscotch-bubble-close", function () {
        $('#go-back').show();
    });
</script>