<%@page import="com.qprogramming.tasq.task.TaskPriority" %>
<%@page import="com.qprogramming.tasq.task.TaskState" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<script src="<c:url value="/resources/js/hopscotch.js" />"></script>
<link href="<c:url value="/resources/css/hopscotch.css" />" rel="stylesheet"
      media="screen"/>
<div id="content" class="white-frame sidepadded" style="overflow: auto;">
    <div>
        <div class="pull-right">
            <a id="task-editmenu" class="btn btn-default btn-sm a-tooltip" href="#"
               data-toggle="dropdown"> <i class="fa fa-lg fa-pencil"></i>
            </a>
            <ul class="dropdown-menu" style="top: inherit;">
                <li><a href="#"> <i
                        class="fa fw fa-pencil"></i> Edit task</a></li>
                <li><a class="linkButton" href="#"> <i
                        class="fa fw fa-link fa-flip-horizontal"></i>&nbsp;Link</a></li>
                <li><a href="#">
                    <i class="fa fw fa-sitemap"></i>&nbsp;Add subtask</a>
                </li>
                <li>
                    <a href="#">
                        <i class="fa fw fa-plus"></i>
                        Create linked task</a>
                </li>
                <li><a class="addFileButton" href="#"> <i
                        class="fa fw fa-file"></i>&nbsp;Attach file</a></li>
            </ul>
            <button id="task-watch" class="btn btn-default btn-sm a-tooltip" title=""
                    data-html="true">
                <i id="watch_icon" class="fa fa-lg fa-eye-slash"></i>
            </button>

            <a id="task-delete" class="btn btn-default btn-sm a-tooltip delete_btn"
               href="#"
               title="Delete task"
               data-lang="en"
               data-msg='Are you sure you want to delete this task?&lt;br&gt;This operation cannot be undone'>
                <i class="fa fa-lg fa-trash-o"></i>
            </a>
        </div>
        <h3 class="marginleft_20">
            <i class="fa fa-lg fa-lightbulb-o a-tooltip fa-border"
               title="User story"></i>
            <a href='#'>TST</a>
            / [TST-1] Sample task<span id="task-name"></span>
            <a href="<c:url value="/tour"/>" id="go-back" class="btn btn-default btn-success"
               style="margin-left:100px; display:none">
                Click here to go back to tours page
            </a>
        </h3>
    </div>
    <div class="row">
        <div class="col-md-9 col-sm-12">
            <div>
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        <i class="fa fa-caret-down toggler" data-tab="detailsToggle"></i>
                        <span class="mod-header-title-txt"> <i
                                class="fa fa-align-left"></i> Details</span>
                    </h5>
                </div>
                <div id="detailsToggle">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="row">
                                <div class="col-md-4 col-sm-6">Status</div>
                                <div class="col-md-8 col-sm-6 paddingleft_20">
                                    <div class="dropdown pointer">
                                        <%
                                            pageContext.setAttribute("states", TaskState.values());
                                        %>
                                        <div class="image-combo nowidth a-tooltip"
                                             data-toggle="dropdown" data-placement="top"
                                             title="Click to change">
                                            <div id="current_state" data-state="ONGOING"
                                                 style="float: left; padding-right: 5px;">
                                            <span class="state_span a-tooltip"><i
                                                    class="fa fa-lg fa-spin fa-repeat"></i>&nbsp;In progress</span>
                                            </div>
                                            <span class="caret" id="task_state"></span>
                                        </div>
                                        <ul class="dropdown-menu" role="menu"
                                            aria-labelledby="dropdownMenu2">
                                            <c:forEach items="${states}" var="enum_state">
                                                <li><a href="#" class="change_state"
                                                       data-state="${enum_state}"> <t:state
                                                        state="${enum_state}"/>
                                                </a></li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-2 col-sm-6">Priority</div>
                        <div class="col-md-4 col-sm-6 paddingleft_20">
                            <div class="dropdown pointer">
                                <%
                                    pageContext.setAttribute("priorities",
                                            TaskPriority.values());
                                    pageContext.setAttribute("critical", TaskPriority.MAJOR);
                                %>
                                <div class="image-combo nowidth a-tooltip"
                                     data-priority="CRITICAL"
                                     data-toggle="dropdown" data-placement=top
                                     title="Click to change">
                                    <t:priority priority="${critical}"/>
                                    <span class="caret" id="task_priority"></span>
                                </div>
                                <ul class="dropdown-menu" role="menu"
                                    aria-labelledby="dropdownMenu2">
                                    <c:forEach items="${priorities}" var="enum_priority">
                                        <li><a tabindex="-1"
                                               href='#'
                                               id="${enum_priority}"> <t:priority
                                                priority="${enum_priority}"/>
                                        </a></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <!--STORY POINTS-->
                    <div class="row">
                        <div class="col-md-2 col-sm-6">Story points</div>
                        <div class="col-md-4 col-sm-6 paddingleft_20"><span
                                class="points badge theme left"><span id="point_value">3</span>
									<input id="point-input" class="point-input">
										<span id="point_approve"
                                              style="display: none; cursor: pointer;"><i
                                                class="fa fa-check"></i></span>
										<span id="point_cancel"
                                              style="display: none; cursor: pointer;"><i
                                                class="fa fa-times"></i></span>
										<span id="point_edit" class="point-edit"><i
                                                class="fa fa-pencil points"></i></span>
                                    </span></div>
                    </div>

                    <!-------------------------	TAGS ------------------->
                    <div class="row">
                        <div class="col-md-2 col-sm-12" style="vertical-align: top;">Tags</div>
                        <div class="col-md-10 col-sm-12 paddingleft_20">
                            <span class="tag label label-info theme"><span class="tagSearch"
                                                                           data-name="subtask"
                                                                           id="task-tag">subtask</span><span
                                    data-role="remove"></span></span>
                        </div>
                    </div>
                </div>
            </div>
            <div>
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        <i class="fa fa-caret-down toggler" data-tab="descriptionToggle"></i>
                        <span class="mod-header-title-txt"> <i
                                class="fa fa-book"></i> Description</span>
                    </h5>
                </div>
                <div id="descriptionToggle">
                    Additional information about task will be stored in here<span
                        id="task-description"></span>
                </div>
            </div>
            <div>
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        <i class="fa fa-caret-down toggler" data-tab="estimatesToggle"></i>
                        <span class="mod-header-title-txt"> <i
                                class="fa fa-lg fa-clock-o"></i> Time tracking</span>
                    </h5>
                </div>
                <!-- logwork trigger modal -->
                <button id="task-logowork" class="btn btn-default btn-sm worklog" data-toggle="modal"
                        data-target="#logWorkform">
                    <i class="fa fa-lg fa-calendar"></i>
                    Log work
                </button>
                <a href="#">
                    <button id="taks-starttime" class="btn btn-default btn-sm">
                        <i class="fa fa-lg fa-clock-o"></i>
                        Start time
                    </button>
                </a>
                <table id="estimatesToggle" style="width: 400px;
                 ">
                    <tr>
                        <td></td>
                        <td style="width: 150px"></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bar_td" style="width: 50px">Estimate</td>
                        <td class="bar_td">
                            <div class="progress"
                                 style="width:100%">
                                <div class="progress-bar" role="progressbar"
                                     aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"
                                     style="width: 100%;"></div>
                            </div>
                        </td>
                        <td class="bar_td">5d</td>
                    </tr>
                    <tr>
                        <td class="bar_td">Logged</td>
                        <td class="bar_td">
                            <div class="progress"
                                 style="width:100%">
                                <div class="progress-bar progress-bar-warning" role="progressbar"
                                     aria-valuenow="41.0" aria-valuemin="0"
                                     aria-valuemax="100" style="width:41.0%"></div>
                            </div>
                        </td>
                        <td class="bar_td">2d 40m</td>
                    </tr>
                    <tr>
                        <td class="bar_td">Remaining</td>
                        <td class="bar_td">
                            <div class="progress"
                                 style="width:100%">
                                <div class="progress-bar progress-bar-success"
                                     role="progressbar" aria-valuenow="58.0"
                                     aria-valuemin="0" aria-valuemax="100"
                                     style="width:58.0% ; float:right"></div>
                            </div>
                        </td>
                        <td class="bar_td">2d 7h 20m<span id="task-estimates"></span></td>
                    </tr>
                </table>
            </div>
            <div>
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        <i class="fa fa-caret-down toggler" data-tab="linksToggle"></i> <span
                            class="mod-header-title-txt"> <i
                            class="fa fa-lg fa-link fa-flip-horizontal"></i> Related Tasks</span>
                    </h5>
                    <a class="btn btn-default btn-xxs a-tooltip pull-right linkButton" style="min-width: 37px;"
                       href="#" title="" data-placement="top"
                       data-original-title="Link"> <i
                            class="fa fa-plus"></i><i
                            class="fa fa-lg fa-link fa-flip-horizontal"></i>
                    </a>
                </div>
                <div class="row marginleft_0">
                    <div id="linkDiv" style="display: none" class="form-group">
                        <form id="linkTask" name="mainForm" method="get"
                              action="#">
                            <div class="form-group col-md-4">
                                <select id="link" name="link" class="form-control input-sm">
                                    <option value="RELATES_TO">relates to</option>
                                    <option value="BLOCKS">blocks</option>
                                    <option value="IS_BLOCKED_BY">is blocked by</option>
                                    <option value="DUPLICATES">duplicates</option>
                                    <option value="IS_DUPLICATED_BY">is duplicated by</option>
                                </select>
                            </div>
                            <div class="form-group col-md-6">
                                <input class="form-control input-sm" id="task_link"
                                       placeholder="Start typing to search for task id or name...">
                                <div id="linkLoader" style="display: none">
                                    <i class="fa fa-cog fa-spin"></i>
                                    Loading...<br>
                                </div>
                            </div>
                            <div class="form-group col-md-4" style="padding-left: 10px">
                                <button type="submit" class="btn btn-default a-tooltip btn-sm"
                                        title="" data-placement="top"
                                        data-original-title="Link task TST-1 with selected">
                                    <i class="fa fa-link fa-flip-horizontal"></i>
                                    Link
                                </button>
                                <a id="linkCancel" class="btn btn-sm linkButton"> Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="row marginleft_0">
                    <div id="linksToggle"
                         style="max-height: 300px; overflow-y: auto; padding-left: 15px">
                        <div style="display: table; width: 100%">
                            <div style="display: table-row">
                                <div style="display: table-cell">
                                    is blocked by
                                </div>
                                <div style="display: table-cell; padding-left: 20px">
                                    <table class="table table-hover table-condensed button-table"
                                           style="border-top-style: hidden;">
                                        <tr>
                                            <td style="width: 30px">
                                                <t:type type="BUG" list="true"/>
                                            </td>
                                            <td style="width: 30px">
                                                <%
                                                    pageContext.setAttribute("blocker", TaskPriority.BLOCKER);
                                                %>
                                                <t:priority priority="${blocker}" list="true"/>
                                            </td>
                                            <td id="task-related"><a href="#"
                                                                     style="color: inherit;
                                                               text-decoration: line-through;">
                                                [TST-2] Sample bug</a></td>
                                            <td style="width: 30px">
                                                <div class="buttons_panel pull-right">
                                                    <a
                                                            href='#'>
                                                        <i class="fa fa-trash-o"
                                                           style="color: gray"></i>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>

                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="task-subtasks">
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        <i class="fa fa-caret-down toggler" data-tab="subtasksToggle"></i>
                        <span class="mod-header-title-txt"> <i
                                class="fa fa-lg fa-sitemap"></i> Subtasks</span>
                    </h5>
                    <a class="btn btn-default btn-xxs a-tooltip pull-right" style="min-width: 37px;"
                       href="#"
                       data-placement="top"
                       data-original-title="Add subtask">
                        <i class="fa fa-plus"></i> <i class="fa fa-lg fa-sitemap"></i>
                    </a>
                </div>
                <div id="subTask" class="form-group togglerContent"
                     style="padding-left: 15px;">
                    <table id="subtasksToggle"
                           class="table table-hover table-condensed button-table">
                        <tr
                                class="">
                            <td style="width: 30px">
                                <t:type type="SUBTASK" list="true"/>
                            </td>
                            <td style="width: 30px">
                                <%
                                    pageContext.setAttribute("major", TaskPriority.MAJOR);
                                %>
                                <t:priority priority="${major}" list="true"/>
                            </td>
                            <td><a
                                    style="color: inherit;"
                                    href="#">[TST-1/1]
                                Sub task </a></td>
                            <td style="width: 100px">
                                <t:state state="TO_DO"/>
                            </td>
                            <td style="width: 50px; padding-top: 14px;">
                                <div class="progress" style="height: 5px;">
                                    <div class="progress-bar  a-tooltip"
                                         title="0%" role="progressbar"
                                         aria-valuenow="0" aria-valuemin="0"
                                         aria-valuemax="100" style="width:0%"></div>
                                </div>
                            </td>
                        </tr>
                        <tr
                                class="">
                            <td style="width: 30px">
                                <t:type type="IDLE" list="true"/>
                            </td>
                            <td style="width: 30px">
                                <%
                                    pageContext.setAttribute("trivial", TaskPriority.TRIVIAL);
                                %>
                                <t:priority priority="${trivial}" list="true"/>
                            </td>
                            <td><a
                                    style="color: inherit;"
                                    href="#">[TST-1/2]
                                Breaks</a></td>
                            <td style="width: 100px">
                                <t:state state="ONGOING"/>
                            </td>
                            <td style="width: 50px; padding-top: 14px;">
                                <div class="progress" style="height: 5px;">
                                    <div class="progress-bar  a-tooltip"
                                         title="42.0%" role="progressbar"
                                         aria-valuenow="42.0" aria-valuemin="0"
                                         aria-valuemax="100" style="width:42.0%"></div>
                                </div>
                            </td>
                        </tr>
                        <tr
                                class="closed">
                            <td style="width: 30px">
                                <t:type type="SUBBUG" list="true"/>
                            </td>
                            <td style="width: 30px">
                                <%
                                    pageContext.setAttribute("minor", TaskPriority.MINOR);
                                %>
                                <t:priority priority="${minor}" list="true"/>
                            </td>
                            <td><a
                                    style="color: inherit;text-decoration: line-through;"
                                    href="#">[TST-1/3]
                                Some minor already finished task bug</a></td>
                            <td style="width: 100px">
                                <t:state state="CLOSED"/>
                            </td>
                            <td style="width: 50px; padding-top: 14px;">
                                <div class="progress" style="height: 5px;">
                                    <div class="progress-bar progress-bar-success a-tooltip"
                                         title="100%" role="progressbar"
                                         aria-valuenow="100" aria-valuemin="0"
                                         aria-valuemax="100" style="width:100%"></div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div>
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        <i class="fa fa-caret-down toggler" data-tab="filesToggle"></i> <span
                            class="mod-header-title-txt"> <i
                            class="fa fa-lg fa-files-o"></i> Files</span>
                    </h5>
                    <a class="btn btn-default btn-xxs a-tooltip pull-right addFileButton" href="#"
                       data-toggle="modal"
                       data-target="#files_task" data-taskID="TST-1">
                        <i class="fa fa-plus"></i> <i class="fa fa-lg fa-file"></i>
                    </a>
                </div>
                <div>
                    <table id="filesToggle"
                           class="table table-hover table-condensed button-table">
                        <tr>
                            <td><i class="fa fa-file-image-o"></i>&nbsp;
                                <a class="clickable">
                                    5-Response Time
                                    Graph.png</a>
                            </td>
                            <td style="width: 30px">
                                <div class="buttons_panel pull-right">
                                    <a
                                            href='#'>
                                        <i class="fa fa-trash-o" style="color: gray"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-md-3 col-sm-12">
            <div id="task-people">
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        <i class="fa fa-user"></i>
                        People</h5>
                </div>
                <div>
                    <div class="row">
                        <div class="col-lg-5 col-md-12">
                            Task owner&nbsp;:
                        </div>
                        <div class="col-lg-7 col-md-12">
                            <img data-src="holder.js/30x30"
                                 class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"/>&nbsp;<a
                                href="#">Jakub Romaniszyn</a>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-5 col-md-12">
                            Assignee&nbsp;:
                        </div>
                        <div class="col-lg-7 col-md-12">
                            <img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"/>&nbsp;<a href="#">Demo User</a>
                        </div>
                    </div>
                    <div id="assign_button_div" class="row">
                        <div class="col-md-12 text-center" >
                            <span class="btn btn-default btn-sm a-tooltip assignToTask" style="width: 150px;margin-top: 5px;"
                                  title="Assign" data-toggle="modal"
                                  data-target="#assign_modal" data-taskID="TST-1"
                                  data-assignee="Demo  User"
                                  data-assigneeID="2"
                                  data-projectID="TST"> <i
                                    class="fa fa-lg fa-user-plus"></i>&nbsp;Assign
                            </span>
                        </div>
                    </div>
                </div>
            </div>
            <div>
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        <i class="fa fa-calendar"></i>
                        Dates</h5>
                </div>
                <div>
                    <div class="row">
                        <div class="col-sm-4">Created</div>
                        <div class="col-sm-8">: 01-04-2015 00:23</div>
                    </div>
                    <div class="row">
                        <div class="col-sm-4">Updated</div>
                        <div class="col-sm-8">: 01-04-2015 00:31</div>
                    </div>
                    <div class="row">
                        <div class="col-sm-4">Due</div>
                        <div class="col-sm-8">: 29-04-2015</div>
                    </div>
                </div>
            </div>
            <div id="sprint_release" style="">
                <div class="mod-header">
                    <h5 class="mod-header-title">
                        Sprints</h5>
                </div>
                <div id="sprints">
                    <div><a href="#">Sprint 18</a></div>
                </div>
            </div>
        </div>
    </div>
    <div>
        <hr>
        <ul class="nav nav-tabs">
            <li id="worklogs-tab"><a id="worklogs" style="color: black" href="#logWork"
                                     data-toggle="tab"><i class="fa fa-newspaper-o"></i> Activity log</a></li>
            <li id="comments-tab" class="active"><a style="color: black" href="#comments"
                                                    data-toggle="tab"><i class="fa fa-comments"></i> Comments</a></li>
        </ul>
        <div id="myTabContent" class="tab-content">
            <div id="comments" class="tab-pane fade in active">
                <table class="table table-hover button-table">
                    <thead>
                    <th style="width:30px"></th>
                    <th></th>
                    <th id="sorter" class="time-header clickable"><span id="indicator"><i class="fa fa-caret-down"></i></span>&nbsp;Date
                    </th>
                    </thead>
                    <tr id="c61" data-date="01-04-2015 00:31">
                        <td>
                            <img data-src="holder.js/30x30"
                                 class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"/>
                        </td>
                        <td colspan="2">
                            <div>

                                <a href="#">Jakub Romaniszyn</a>
                                <div class="time-div">01-04-2015 00:31</div>
                            </div>
                            <div class="buttons_panel" style="float: right">
                                <a href="##c61"
                                   title="Link to this comment"
                                   style="color: #676767"><i class="fa fa-link"></i></a>
                                <a href="#" class="comments_edit" data-toggle="modal"
                                   data-target="#commentModal"
                                   data-comment_id="61"><i class="fa fa-pencil"
                                                           style="color: #676767"></i></a>
                                <a
                                        href='#'><i
                                        class="fa fa-trash-o" style="color: #676767"></i></a>
                            </div>
                            <div class="comment-div">
                                <span class="comment-message">All blockers eliminated. Moving on with work</span>
                            </div>
                            <div></div>
                        </td>
                    </tr>
                </table>
                <div id="comments_div" style="display: none">
                    <form id="commentForm" name="commentForm" method="post"
                          action="#">
                        <input type="hidden" name="task_id" value="TST-1">
                        <textarea id="comment-message" class="form-control comment-message-text max4kchars" rows="3"
                                  name="message"
                                  autofocus></textarea>
                        <span class="remain-span"><span class="remain"></span> chars left</span>
                        <div style="margin-top: 5px">
                            <button class="btn btn-default btn-sm" type="submit">
                                Add
                            </button>
                            <span class="btn btn-sm" id="comments_cancel">Cancel</span>
                        </div>
                    </form>
                </div>
                <button id="comments_add" class="btn btn-default btn-sm">
                    <i class="fa fa-comment"></i>&nbsp;
                    Add comment
                </button>
            </div>
            <div id="logWork" class="tab-pane fade">
                <table id="taskworklogs" class="table table-condensed table-hover button-table">
                    <tbody>
                    <tr>
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td style="font-size: smaller; color: dimgray;width: 100%;"><a href="#">Jakub
                            Romaniszyn</a>&nbsp;changed assignee
                            <div class="time-div">01-04-2015 00:38</div>

                            <div class="quote">Demo User</div>
                        </td>
                    </tr>
                    <tr>
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td style="font-size: smaller; color: dimgray;width: 100%;"><a href="#">Jakub
                            Romaniszyn</a>&nbsp;commented
                            <div class="time-div">01-04-2015 00:31</div>
                            <div class="quote">All blockers eliminated. Moving on with work</div>
                        </td>
                    </tr>
                    <tr>
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td style="font-size: smaller; color: dimgray;width: 100%;"><a href="#">Jakub
                            Romaniszyn</a>&nbsp;changed assignee
                            <div class="time-div">01-04-2015 00:30</div>
                            <div class="quote">Demo User</div>
                        </td>
                    </tr>
                    <tr>
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td style="font-size: smaller; color: dimgray;width: 100%;"><a href="#">Jakub
                            Romaniszyn</a>&nbsp;changed status
                            <div class="time-div">01-04-2015 00:29</div>
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
                    <tr>
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td style="font-size: smaller; color: dimgray;width: 100%;"><a href="#">Jakub
                            Romaniszyn</a>&nbsp;logged work
                            <div class="time-div">01-04-2015 00:24</div>

                            <div class="quote">3h</div>
                        </td>
                    </tr>
                    <tr>
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td style="font-size: smaller; color: dimgray;width: 100%;"><a href="#">Jakub
                            Romaniszyn</a>&nbsp;edited
                            <div class="time-div">01-04-2015 00:24</div>

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
                    <tr>
                        <td><img data-src="holder.js/30x30" class="avatar small"
                                 src="<c:url value="/resources/img/avatar.png"/>"></td>
                        <td style="font-size: smaller; color: dimgray;width: 100%;"><a href="#">Jakub
                            Romaniszyn</a>&nbsp;created task
                            <div class="time-div">01-04-2015 00:23</div>

                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="logWorkform" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">
                    Log work</h4>
            </div>
            <form id="mainForm" name="mainForm" method="post"
                  action="#">
                <div class="modal-body">
                    <input id="modal_taskID" type="hidden" name="taskID">
                    <div class="form-group">
                        <label>Time spent</label> <input
                            id="loggedWork" name="loggedWork"
                            style="width: 150px; height: 25px" class="form-control"
                            type="text" value=""> <span class="help-block">How long you spent on this task? (eg 2w 5d 20h)</span>
                    </div>
                    <div>
                        <div style="float: left; margin-right: 50px;">
                            <label>Date</label> <input
                                id="datepicker" name="date_logged"
                                style="width: 150px; height: 25px"
                                class="form-control datepicker" type="text" value="">
                        </div>
                        <div>
                            <label>Time in 24h format</label> <input
                                id="time_logged" name="time_logged"
                                style="width: 70px; height: 25px" class="form-control"
                                type="text" value="">
                        </div>
                    </div>
                    <span class="help-block">Choose date and time for which time will be logged. Leave empty for current date and time.</span>
                    <div>
                        <label>Remaining</label>
                        <div class="radio">
                            <label> <input type="radio" name="estimate_reduce"
                                           id="estimate_auto" value="auto" checked> Reduce automatically</label>
                        </div>
                        <div class="radio">
                            <label> <input type="radio" name="estimate_reduce"
                                           id="estimate_manual" value="auto"> Set manually </label> <input
                                id="remaining" name="remaining" class="form-control"
                                style="width: 150px; height: 25px" disabled>
                        </div>
                    </div>
                    <span class="help-block">Automatically reduce remaining time left (but not below 0). You can also manually set it.</span>
                </div>
                <div class="modal-footer">
                    <a class="btn" data-dismiss="modal">Cancel</a>
                    <button class="btn btn-default" type="submit">
                        Log
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    $('#logWorkform').on('shown.bs.modal', function () {
        console.log("show!");
        $("#loggedWork").focus();
    });
    $(".worklog").click(function () {
        var taskID = $(this).data('taskid');
        var title = '<i class="fa fa-calendar"></i> Log work ' + taskID;
        $("#myModalLabel").html(title);
        $("#modal_taskID").val(taskID);
        $("#loggedWork").focus();
    });

    $(".datepicker").datepicker({
        maxDate: '0',
        dateFormat: "dd-mm-yy",
        firstDay: 1
    });
    $(".datepicker").change(function () {
        var date = new Date;
        var minutes = (date.getMinutes() < 10 ? '0' : '') + date.getMinutes();
        var hour = date.getHours();
        $("#time_logged").val(hour + ":" + minutes);
    });
    $("#time_logged").mask("Z0:A0", {
        translation: {
            'Z': {
                pattern: /[0-2]/
            },
            'A': {
                pattern: /[0-5]/
            }
        },
        placeholder: "__:__"
    });
    $("#time_logged").change(function () {
        var regex = /([01]\d|2[0-3]):([0-5]\d)/;
        if (!regex.test($(this).val())) {
            $(this).val('');
        }
    });

    $("#estimate_manual").change(function () {
        $('#remaining').attr("disabled", !this.checked);
    });
    $("#estimate_auto").change(function () {
        $('#remaining').val("");
        $('#remaining').attr("disabled", this.checked);
    });
</script>
<!-- CLOSE TASK MODAL -->
<div class="modal fade" id="close_task" role="dialog"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header theme">
                <h4 class="modal-title" id="closeDialogTitle">
                    Finish task</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <span class="help-block">Task will be finished. You can additionally:</span>
                </div>

                <div class="checkbox">
                    <div style="font-weight: bold;">Estimate</div>
                    <label class="checkbox"> <input type="checkbox"
                                                    name="zero_checkbox" id="modal_zero_checkbox"> Set remaining time
                        for this task to zero</label>
                </div>
                <div id="closeSubtask" class="checkbox" style="display:none">
                    <div style="font-weight: bold; margin-left: -22px;">Subtasks</div>
                    <label class="checkbox"> <input type="checkbox"
                                                    name="closesubtasks" id="modal_subtasks" checked>Close all subtasks?
                        (<span id="modal_subtaskCount"></span>)
                    </label>
                </div>
                <div>
                    <label>Add comment</label>
                    <textarea id="modal_comment" name="message" class="form-control" rows="3"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button id="close_task_btn" class="btn btn-default">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>
<script>
    $('#close_task').on('shown.bs.modal', function (e) {
        var subTasks = $("#modal_subtaskCount").html();
        console.log(subTasks);
        if (subTasks > 0) {
            $("#closeSubtask").show();
        }
    });
    $("#close_task_btn").click(function () {
    });
</script>
<script src="/resources/js/bootstrap.file-input.js"></script>
<!-- ATTACH FILE TO TASK MODAL -->
<div class="modal fade" id="files_task" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header theme">
                <h4 class="modal-title" id="fileModalLabel">
                    Files</h4>
            </div>
            <div class="modal-body">
                <form id="submit_files" action="#" method="POST" enctype="multipart/form-data">
                    <input id="file_taskID" name="taskID" type="hidden">
                    <div class="pull-right">
					<span id="addMoreFiles" class="btn btn-default btn-sm">
						<i class="fa fa-plus"></i><i class="fa fa-file"></i>&nbsp;Add more files
					</span>
                    </div>
                    <table id="fileTable" style="width: 300px;">
                    </table>
                </form>
            </div>
            <div class="modal-footer">
                <a class="btn" data-dismiss="modal">Cancel</a>
                <button id="upload_file_btn" class="btn btn-default">
                    Attach files
                </button>
            </div>
        </div>
    </div>
</div>
<script>
    var fileTypes = '.doc,.docx,.rtf,.txt,.odt,.xls,.xlsx,.ods,.csv,.pps,.ppt,.pptx,.jpg,.png,.gif';
    $(".addFileButton").click(function () {
        var taskID = $(this).data('taskid');
        var title = '<i class="fa fa-file"></i>&nbsp;' + taskID + ' - Attach file ';
        $("#fileModalLabel").html(title);
        $("#file_taskID").val(taskID);
        addFileInput();
    });

    $(document).on("change", ".file_upload", function (e) {
        var td = $(this).closest("td");
        td.find(".removeFile").remove();
        td.append('<span class="btn btn-default pull-right removeFile"><i class="fa fa-trash"></i><span>');
    });

    $("#addMoreFiles").click(function () {
        addFileInput();
    });

    $(document).on("click", ".removeFile", function (e) {
        $(this).closest("tr").remove();
        if ($('#fileTable tr').length == 0) {
            addFileInput();
        }
    });

    function addFileInput() {
        var choose = ' Choose file';
        var title = "<i class='fa fa-file'></i>" + choose;
        var inputField = '<input class="file_upload" name="files" type="file" accept="' + fileTypes + '" title="' + title + '" data-filename-placement="inside">';
        $("#fileTable").append(
                '<tr><td style="width:300px">' + inputField + '</td></tr>');
        $("#fileTable tr:last").find(".file_upload").bootstrapFileInput();
    }

</script>
<!-- ASSIGN TO TASK MODAL -->
<div class="modal fade ui-front" id="assign_modal" role="dialog"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="assignToModalLabel">
                    Assign</h4>
            </div>
            <div class="modal-body">
                <form id="assignModalForm" action="#" method="post">
                    <input id="assign_taskID" name="taskID" type="hidden">
                    <input name="account" class="form-control input-sm " id="assignee_input"
                           placeholder="Begin typing to find users">
                    <div id="assignUsersLoader" style="display: none">
                        <i class="fa fa-cog fa-spin"></i>
                        Loading...<br>
                    </div>
                    <table style="width:100%;margin-top: 20px;">
                        <tr>
                            <td>
                                Assignee:
                                <span id="displayAssignee" style="  margin: 0 auto;  margin-top: 20px;"></span>
                            </td>
                            <td class="pull-right">
                                <button id="unassign_btn" class="btn btn-default a-tooltip" title="Unassign"><i
                                        class="fa fa-user-times"></i></button>

                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td class="pull-right" style="margin-top:20px">
							<span id="assign_me_btn" class="btn btn-default" style="width:150px">
								<i class="fa fa-user"></i> Assign me</span>
                                <button id="assign_btn" class="btn btn-default" disabled="disabled" style="width:150px">
                                    <i class="fa fa-user"></i> Assign
                                </button>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
    var cache = {};
    var avatarURL = '/../avatar/';
    var taskID;
    var projectID;

    $(".assignToTask").click(function () {
        taskID = $(this).data('taskid');
        var assignee = $(this).data('assignee');
        var assigneeid = $(this).data('assigneeid');
        projectID = $(this).data('projectid');
        var showAssignee;
        var title = '<i class="fa fa-lg fa-user-plus"></i>' + taskID
                + ' - Assign ... ';
        $("#assignToModalLabel").html(title);
        $("#assign_taskID").val(taskID);
        if (assignee == '') {
            showAssignee = '<i>Unassigned</i>';
            $("#unassign_btn").attr("disabled", "disabled");
        } else {
            showAssignee = '<img data-src="holder.js/30x30" class="avatar small" src="<c:url value="/resources/img/avatar.png"/>"/>' + assignee;
        }
        $("#displayAssignee").html(showAssignee);
        $("#assign_btn").attr("disabled", "disabled");

    });

    $("#assign_me_btn").click(function () {
        var url = '#';
        window.location.href = url;
    });

    $("#unassign_btn").click(function () {
        $("#assignModalForm").submit();
    });
</script>
<script>
    var tour = {
        id: "task_tour",
        showPrevButton: true,
        steps: [
            {
                title: "Task name",
                content: "Task type Icon , Project ID ( a link ) and task name with preceding ID",
                target: "task-name",
                placement: "right",
                yOffset: -15
            },
            {
                title: "Task status",
                content: 'Current task status. Click to show available statuses and change it<br><br>More info can be found <a href="<c:url value="/help#task-work"/>" target="_blank">here</a>',
                target: "task_state",
                placement: "right",
                yOffset: -30
            },
            {
                title: "Task priority",
                content: 'Indicate how crucial task is. Click to show and change priority',
                target: "task_priority",
                placement: "right",
                yOffset: -30
            },
            {
                title: "Tags",
                content: 'To add new tag, input it and if there is similar tag, autocomplete box will popup.<br>To remove tag , click <i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>symbol<br> To view all tasks with this tag, click it. This will move you to all tasks list view with applied tag filter.',
                target: "task-tag",
                placement: "right",
                yOffset: -30
            },
            {
                title: "Task description",
                content: 'detialed description what have to be done in this task. Edited in rich text editor, can contain images, lists etc.',
                target: "task-description",
                placement: "right",
                yOffset: -30
            },
            {
                title: "Story points",
                content: 'If task is estimated, story points for it will be shown. If no points were set ,question mark will be shown instead. <br>This value can be changed either from edit menu, or from this view quickly by hovering over <span class="badge theme">?</span> to reveal extra <i class="fa fa-pencil"></i> button .<br> Click it to show input field <span class="badge theme"> <input class="point-input" style="display: inline-block;"> <span style="cursor: pointer;"><i class="fa fa-check" style="vertical-align: text-top"></i></span> <span style="cursor: pointer;"><i class="fa fa-times" style="vertical-align: text-top"></i></span></span> Confirm it by either enter key, or clicking check-mark sign. Cancel input by clicking x <br>You have to be either task owner or project admin to do so',
                target: "point_value",
                placement: "right",
                width: 400,
                yOffset: -25,
                xOffset: 10
            },
            {
                title: "Log work",
                content: 'Use it to log time spent on this task. More information in help page in <a href="<c:url value="/help#task-work"/>" target="_blank">Working with tasks</a> section<p><i><i class="fa fa-exclamation-circle" aria-hidden="true"></i>&nbsp;Keyboard shortcut \'l\'</i></p>',
                target: "task-logowork",
                placement: "right",
                yOffset: -15
            },
            {
                title: "Start/Stop timer",
                content: 'Starts/stops active timer on this task. When timer is stoped, new work log will be automatically added to this task.<br>If logged work will be larger than 8h extra confirmation modal will be shown',
                target: "taks-starttime",
                placement: "right",
                yOffset: -15
            },
            {
                title: "Time bars",
                content: 'Shows how much time was estimated, how much logged and remaining',
                target: "task-estimates",
                placement: "right",
                yOffset: -40
            },
            {
                title: "Related tasks",
                content: 'Shows list of all task that are related/linked to it. In order to add new link , press <a class="btn btn-default btn-xxs" href="#" title=""> <i class="fa fa-plus"></i><i class="fa fa-lg fa-link fa-flip-horizontal"></i></a> button<br>(this option is also available under edit menu)<p><i><i class="fa fa-exclamation-circle" aria-hidden="true"></i>&nbsp;Keyboard shortcut \'r\'</i></p>',
                target: "task-related",
                width: 400,
                placement: "top",
                yOffset: 10
            },
            {
                title: "Subtasks",
                content: 'All subtasks are shown here with type, priority and progress. To create new subtask , click <a class="btn btn-default btn-xxs" href="#"> <i class="fa fa-plus"></i> <i class="fa fa-lg fa-sitemap"></i> </a> button<br>(option available from edit menu as well)',
                target: "task-subtasks",
                placement: "top",
                yOffset: 40,
                xOffset: 'center',
                arrowOffset: 'center',
                onNext: function () {
                    $('.nav-tabs a[href="#logWork"]').tab('show');
                },
            },
            {
                title: "Activity log",
                content: 'Click to show all activities related to this task',
                target: "worklogs",
                placement: "right",
                onNext: function () {
                    $('.nav-tabs a[href="#comments"]').tab('show');
                },
                yOffset: -10
            },
            {
                title: "Comments tab",
                content: 'Click to switch to comments tab. All comments related to this task are listed  (sorted from newest).<br><br> New comment can be added by clicking <button id="comments_add" class="btn btn-default btn-sm"><i class="fa fa-comment"></i>&nbsp; Add comment</button> button<p><i><i class="fa fa-exclamation-circle" aria-hidden="true"></i>&nbsp;Keyboard shortcut \'c\'</i></p>',
                target: "comments-tab",
                placement: "top",
                onPrev: function () {
                    $('.nav-tabs a[href="#logWork"]').tab('show');
                }
            },
            {
                title: "Edit menu",
                content: 'Shows task edit menu. More information in <a href="<c:url value="/help#task-edit"/>" target="_blank">Editing task</a> section',
                target: "task-editmenu",
                placement: "left",
                yOffset: -15,
            },
            {
                title: "Start/Stop watching task",
                content: 'Hover over this button to see how many users (including your account ) watching events in this task',
                target: "task-watch",
                placement: "left",
                yOffset: -15,
            },
            {
                title: "Delete task",
                content: 'Deletes this task ( project admin only) More information in <a href="<c:url value="/help#task-remove"/>" target="_blank">Removing tasks</a>',
                target: "task-delete",
                placement: "left",
                yOffset: -15,
            },
            {
                title: "Task owner and current assignee",
                content: 'You can quickly assign someone to this task by clicking <span class="btn btn-default btn-sm "><i class="fa fa-lg fa-user-plus"></i> </span> button.<br>This will show modal window. Start typing username to show autocomplete hints with all project members<br><p><i><i class="fa fa-exclamation-circle" aria-hidden="true"></i>&nbsp;Keyboard shortcut \'a\'</i></p>',
                target: "task-people",
                placement: "left",
                yOffset: 10
            },
            {
                title: "Sprint/ Releases",
                content: 'If task belongs or belonged to one of sprints/releases, it will be listed here',
                target: "sprints",
                placement: "top",
                onNext: function () {
                    $('#go-back').show();
                },
                yOffset: -15,
            },
            {
                title: "Done",
                content: 'Go back to tour page, or start using ${applicationName}',
                target: "task-name",
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

    // Start the tour!
    hopscotch.startTour(tour);
    $(document).on("click", ".hopscotch-bubble-close", function () {
        $('#go-back').show();
    });
</script>