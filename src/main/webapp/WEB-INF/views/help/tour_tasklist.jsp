<%@ page import="com.qprogramming.tasq.task.TaskPriority" %>
<%@ page import="com.qprogramming.tasq.task.TaskState" %>
<%@ page import="com.qprogramming.tasq.task.TaskType" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
           prefix="security" %>
<script src="<c:url value="/resources/js/hopscotch.js" />"></script>
<link href="<c:url value="/resources/css/hopscotch.css" />" rel="stylesheet"
      media="screen"/>
<div class="padding-top5">
    <div class="row margintop_10">
        <div class="col-sm-12 col-md-4 margintop_10 form-inline">
            <div class="form-group">
                <span>Tasks</span>
                <select id="tasklist-project" name="project" class="form-control">
                    <option value="TST">[TST] Testing Project</option>
                    <option value="TSTII">[TSTII] Test 2</option>
                    <option value="TSTI">[TSTI] Test</option>
                    <option value="DEMO">[DEMO] Demo project :)</option>
                </select>
            </div>
        </div>
        <div class="col-md-5">
            <span id="more-tours"></span>
            <a href="<c:url value="/tour"/>" id="go-back" class="btn btn-default btn-success"
               style="margin-left:20px; display:none">
                Click here to go back to tours page
            </a>
        </div>
        <div class="hidden-xs hidden-sm col-md-3 margintop_10">
            <div id="buttDiv" class="pull-right">
                <a id="tasklist-export" class="btn btn-default export_startstop" style="width: 120px;">
                    <i class="fa fa-upload"></i>
                    Export</a>
            </div>
            <div id="fileDiv" style="display:none">
                <div style="display: table-cell">
                    <a class="btn export_startstop">Cancel</a>
                </div>
                <div style="display: table-cell">
                    <div class="btn-group">
                        <a class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                            <i class="fa fa-long-arrow-down"></i><i class="fa fa-file"></i> Export selected&nbsp;
                            <b class="caret"></b>
                        </a>
                        <ul class="dropdown-menu " role="menu">
                            <li>
                                <a href="#" class="fileExport" data-type="xls"><i class="fa fa-file-excel-o"></i> Excel
                                    file</a>
                            </li>
                            <li>
                                <a href="#" class="fileExport" data-type="xml"><i class="fa fa-file-code-o"></i> XML
                                    file</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div style="line-height: 30px;" class="col-sm-12 col-md-10 margintop_10">
        <span id="tasklist-filters" class="filter">Status:
            <span class="filter_span">
                <span class="state_span a-tooltip" data-original-title="" title="">
                    <i class="fa fa-lg fa-list"></i>&nbsp;All tasks
                </span>
                <a href="#"><i class="fa fa-times"
                               style="font-size: smaller; margin-left: 3px; color: lightgray"></i></a>
            </span>
        </span>
        </div>
        <div class="col-md-2 margintop_10" style="white-space: nowrap;">
            <div id="tasklist-subtasks" class="pull-right">
                Subtasks&nbsp;
                <i id="opensubtask" class="fa fa-plus-square clickable a-tooltip" title=""
                   data-original-title="Show all subtasks"></i>
                <i id="hidesubtask" class="fa fa-minus-square clickable a-tooltip" title=""
                   data-original-title="Hide all subtasks"></i>
            </div>
        </div>
    </div>
</div>
<div class="white-frame">
    <table id="tasklist" class="table table-hover table-condensed">
        <thead class="theme">
        <tr>
            <th class="export_cell export-hidden" style="width: 30px"><input id="select_all" type="checkbox"
                                                                             class="a-tooltip" title=""
                                                                             data-original-title="Click to select all">
            </th>
            <th style="width: 60px;text-align: center;">
                <span id="tasklist-type" class="dropdown a-tooltip clickable" title="" data-original-title="Type">
                    <a class="filter dropdown-toggle theme" type="button" id="dropdownMenuType" data-toggle="dropdown">
                        Type <span class="caret theme hidden-xs hidden-sm"></span></a>
                        <ul id="dropdown-types" class="dropdown-menu">
                            <%
                                pageContext.setAttribute("types", TaskType.getTypes(false));
                            %>
                            <c:forEach items="${types}" var="type">
                                <li>
                                    <a href="#"><t:type type="${type}" list="true" show_text="true"/></a>
                                </li>
                            </c:forEach>
                        </ul>
                    <span>
            </span></span></th>
            <th style="width: 30px;">
                <span id="tasklist-priority" class="dropdown a-tooltip" title=""
                      style="padding-top: 5px; cursor: pointer;" data-original-title="Priority"> <a
                        class="dropdown-toggle theme black-link" type="button" id="dropdownMenu2"
                        data-toggle="dropdown"> <span class="caret theme"></span></a>
                    <%
                        pageContext.setAttribute("priorities", TaskPriority.values());
                    %>
                    <ul id="dropdown-priority" class="dropdown-menu">
                        <c:forEach items="${priorities}" var="priority">
                            <li>
                                <a href="#"><t:priority priority="${priority}"/></a>
                            </li>
                        </c:forEach>
                    </ul>
                </span>
            </th>
            <th style="width: 500px">Summary</th>
            <th id="tasklist-progress" style="width: 1px"></th>
            <th class="hidden-xs hidden-sm">Progress</th>

            <th>
                <div id="tasklist-state" class="dropdown" style="padding-top: 5px; cursor: pointer;">
                    <a class="filter dropdown-toggle theme" type="button" id="dropdownMenu1" data-toggle="dropdown">Status<span
                            class="caret theme hidden-xs hidden-sm"></span></a>
                    <%
                        pageContext.setAttribute("states", TaskState.values());
                    %>
                    <ul id="dropdown-status" class="dropdown-menu">
                        <c:forEach items="${states}" var="state">
                            <li><a href="#"><t:state state="${state}"/></a></li>
                        </c:forEach>
                        <li class="divider"></li>
                        <li><a href="#"><t:state state="ALL"/></a>
                        </li>
                    </ul>
                </div>
            </th>
            <th style="width: 200px">
                <div id="tasklist-assignee" class="dropdown" style="padding-top: 5px; cursor: pointer;">
                    <a class="filter dropdown-toggle theme" type="button" id="dropdownMenuAssignee"
                       data-toggle="dropdown">Assignee<span class="caret theme hidden-xs hidden-sm"></span></a>
                    <ul id="dropdown-assignee" class="dropdown-menu" style="padding: 5px;width: 200px;z-index: 1;">
                        <li>
                            <span role="status" aria-live="polite" class="ui-helper-hidden-accessible"></span><input
                                type="text" class="form-control input-sm ui-autocomplete-input"
                                placeholder="Begin typing to find users" id="assignee_auto" autocomplete="off">
                            <div id="assignee_autoLoader" style="display: none; color:black">
                                <i class="fa fa-cog fa-spin"></i>
                                Loading...<br>
                            </div>
                        </li>
                    </ul>
                </div>
            </th>
        </tr>
        </thead>
        <form id="exportTaskForm" method="POST" enctype="multipart/form-data" action="#"></form>
        <input id="exportType" type="hidden" name="type" value="xml">
        <tbody>
        <tr class="closed">
            <td class="export_cell export-hidden"><input class="export" type="checkbox" name="tasks" value="TST-2"></td>
            <td style="text-align: center;">
                <t:type type="BUG" list="true"/>
            </td>
            <td>
                <%
                    pageContext.setAttribute("major", TaskPriority.MAJOR);
                %>
                <t:priority priority="${major}" list="true"/>
            </td>
            <td>
                <a href="#" style="color: inherit;
                               text-decoration: line-through;
                               ">[TST-2]
                    Sample bug</a>
            </td>
            <td></td>
            <td class="hidden-xs hidden-sm">
                <div class="progress" style="width: 100px;" >
                    <div class="progress-bar progress-bar-success a-tooltip" role="progressbar" aria-valuenow="100"
                         aria-valuemin="0" aria-valuemax="100" style="width:100%;" title=""
                         data-original-title="100%"></div>
                </div>
            </td>
            <td class=""><t:state state="CLOSED"/>
            </td>
            <td><img data-src="holder.js/20x20" class="avatar xsmall" src="<c:url value="/resources/img/avatar.png"/>">
                &nbsp;<a href="#">Jakub Romaniszyn</a>
            </td>
        </tr>
        <tr class="">
            <td class="export_cell export-hidden"><input class="export" type="checkbox" name="tasks" value="TST-1"></td>
            <td style="text-align: center;"><t:type type="USER_STORY" list="true"/>
            </td>
            <td>
                <%
                    pageContext.setAttribute("critical", TaskPriority.CRITICAL);
                %>
                <t:priority priority="${critical}" list="true"/>
            </td>
            <td>
                <i class="subtasks fa fa-minus-square" data-task="TST-1" id="subtasksTST-1"></i>
                <a href="#" style="color: inherit;">[TST-1]
                    Sample task</a>
                <div style="margin-top: 5px;border-top: 1px solid lightgray;" class="subtaskdiv" id="TST-1subtask">
                    <div style="padding:2px;"><t:type type="SUBTASK" list="true"/><a href="#"
                                                                                     class="subtaskLink black-link">[TST-1/1]
                        Sub task </a></div>
                    <div style="padding:2px;"><t:type type="IDLE" list="true"/> <a href="#"
                                                                                   class="subtaskLink black-link">[TST-1/2]
                        Breaks</a></div>
                    <div style="padding:2px;"><t:type type="SUBBUG" list="true"/> <a href="#"
                                                                                     class="subtaskLink closed black-link">[TST-1/3]
                        Some minor already finished task bug</a></div>
                    <div style="padding:2px;"><t:type type="SUBTASK" list="true"/> <a href="#"
                                                                                      class="subtaskLink black-link">[TST-1/4]
                        Completed one</a></div>
                </div>
            </td>
            <td></td>
            <td class="hidden-xs hidden-sm">
                <div class="progress" style="width: 100px;">
                    <div class="progress-bar  a-tooltip" role="progressbar" aria-valuenow="43.0" aria-valuemin="0"
                         aria-valuemax="100" style="width:43.0%;" title="" data-original-title="43.0%"></div>
                </div>
            </td>
            <td class=""><t:state state="ONGOING"/>
            </td>
            <td><img data-src="holder.js/20x20" class="avatar xsmall" src="<c:url value="/resources/img/avatar.png"/>">
                &nbsp;<a href="#">Demo User</a>
            </td>
        </tr>
        </tbody>
    </table>
</div>

<script>
    var tour = {
        id: "task_tour",
        showPrevButton: true,
        steps: [
            {
                title: "Projects",
                content: "Choose from which project, tasks should be shown. By default user's active project is selected",
                target: "tasklist-project",
                placement: "bottom",
                xOffset: 'center',
                arrowOffset: 'center'
            },
            {
                title: "Filters",
                content: 'All active filters will be shown here. Either if searched by term, status or assignee.<br>To widen search/display click <i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i> icon on filter tab',
                target: "tasklist-filters",
                placement: "bottom",
                onNext: function () {
                    $("#dropdown-types").addClass("tour-dropdown");
                }
            },
//                SIDE MENU
            {
                title: "Task Type",
                content: 'Click on column to filter tasks by their type',
                target: "tasklist-type",
                placement: "top",
                onNext: function () {
                    $("#dropdown-types").removeClass("tour-dropdown");
                    $("#dropdown-priority").addClass("tour-dropdown");
                },
                onPrev: function () {
                    $("#dropdown-types").removeClass("tour-dropdown");
                },
                width: 300
            },
            {
                title: "Priority",
                content: 'Click on column to filter tasks by priority',
                target: "tasklist-priority",
                placement: "top",
                onNext: function () {
                    $("#dropdown-priority").removeClass("tour-dropdown");
                },
                onPrev: function () {
                    $("#dropdown-types").addClass("tour-dropdown");
                    $("#dropdown-priority").removeClass("tour-dropdown");
                },
                width: 300,
                xOffset: -30
            },
            {
                title: "Progress",
                content: 'Tasks progress bars.<br><span style="color: #337ab7"><strong>Blue</strong></span> bar means task is in progress and <span style="color: #5cb85c"><strong>Green</strong></span> task is closed. If bar is in <span style="color: #d9534f"><strong>red</strong></span>, that means that more time that it was originally estimated, was already spent',
                target: "tasklist-progress",
                placement: "top",
                onNext: function () {
                    $("#dropdown-status").addClass("tour-dropdown");
                },
                onPrev: function () {
                    $("#dropdown-priority").addClass("tour-dropdown");
                },

                yOffset:30,
                width: 300
            },

            {
                title: "Task status",
                content: 'Filter by task status. By default all not closed tasks are shown.<br><span class="tour-bubble-button state_span a-tooltip"><i class="fa fa-lg fa-list"></i>&nbsp;All tasks</span> option can be chosen to view closed as well',
                target: "tasklist-state",
                placement: "left",
                onNext: function () {
                    $("#dropdown-status").removeClass("tour-dropdown");
                    $("#dropdown-assignee").addClass("tour-dropdown");
                },
                onPrev: function () {
                    $("#dropdown-status").removeClass("tour-dropdown")
                },
                yOffset: -20
            },
            {
                title: "Assignee",
                content: 'You can also filter tasks by assignee. Autocomplete will automatically search for all users based on inputted term',
                target: "tasklist-assignee",
                placement: "left",
                onNext: function () {
                    $("#dropdown-assignee").removeClass("tour-dropdown");
                },
                onPrev: function () {
                    $("#dropdown-status").addClass("tour-dropdown");
                    $("#dropdown-assignee").removeClass("tour-dropdown");
                },
                yOffset: -20
            },
            {
                title: "Show all/hide subtasks",
                content: "If one of tasks have subtasks <i class='fa fa-plus-square'></i> icon will be shown before it's name.<br>You can click each task individually, or this buttons to expand/hide all tasks",
                target: "tasklist-subtasks",
                placement: "left",
                yOffset: -23,
                onPrev: function () {
                    $("#dropdown-assignee").addClass("tour-dropdown");
                },
                width: 400
            },
            {
                title: "Export",
                content: "You can export visible tasks into Microsoft Excel format, or XML. Afterwards they can imported into another app instance, edited etc.<br>Please note comments and worklogs won't be exported together with tasks",
                target: "tasklist-export",
                placement: "left",
                yOffset: -10,
                onNext: function () {
                    $('#go-back').show();
                }
            },
            {
                title: "Done",
                content: 'Go back to tour page, or start using ${applicationName}',
                target: "more-tours",
                placement: "bottom",
                yOffset: 40,
                width: 400,
                onPrev: function () {
                    $('#go-back').hide();
                },
                arrowOffset: 'center'
            }

        ]
    };
    hopscotch.startTour(tour);

    $(".export_startstop").click(function () {
        $(".export_cell").toggleClass('export-hidden');
        $("#buttDiv").toggle();
        $("#fileDiv").toggle();
    });
    $("#select_all").click(function () {
        if (this.checked) {
            $('.export').each(function () {
                this.checked = true;
            });
        } else {
            $('.export').each(function () {
                this.checked = false;
            });
        }
    });
    $(document).on("click", ".hopscotch-bubble-close", function () {
        $(".dropdown-menu").removeClass("tour-dropdown");
        $('#go-back').show();
    });


</script>