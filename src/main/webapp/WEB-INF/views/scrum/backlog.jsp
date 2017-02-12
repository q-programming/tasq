<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<link href="<c:url value="/resources/css/jquery.contextmenu.css" />"
      rel="stylesheet" media="screen"/>
<script src="<c:url value="/resources/js/jquery.contextmenu.js" />"></script>
<c:set var="tasks_text">
    <s:message code="task.tasks" text="Tasks"/>
</c:set>
<c:set var="taskDesc_text">
    <s:message code="task.description" text="Description"/>
</c:set>
<security:authentication property="principal" var="user"/>
<security:authorize access="hasRole('ROLE_ADMIN')">
    <c:set var="is_admin" value="true"/>
</security:authorize>
<c:if test="${t:contains(project.administrators,user) || is_admin}">
    <c:set var="can_edit" value="true"/>
</c:if>
<div class="row">
    <div class="col-sm12 col-md-6">
        <h3 class="">${project}</h3>
    </div>
    <div class="col-sm12 col-md-6">
        <div class="dropdown margintop_20 pull-right ">
            <a class="btn btn-default " style="width: 120px" data-toggle="dropdown">
                <i class="fa fa-print"></i>&nbsp;<s:message code="agile.print"/></a>
            <ul class="dropdown-menu">
                <li><a class="print_cards clickable"> <i
                        class="fa fw fa-list"></i>&nbsp;<s:message code="agile.print.all"/>
                </a></li>
                <c:forEach var="entry" items="${sprint_result}">
                    <c:set var="sprint" value="${entry.key}"/>
                    <li><a class="print_cards clickable" data-sprintid="${sprint.id}"><i
                            class="fa fw fa-book"></i>&nbsp;<s:message
                            code="agile.print.sprint"/>&nbsp;${sprint.sprintNo}
                    </a></li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>
<div class="white-frame" style="display: table; width: 100%">
    <%--MENU --%>
    <div style="display: table-caption; margin-left: 10px;">
        <ul class="nav nav-tabs" style="border-bottom: 0">
            <li class="active"><a><i class="fa fa-book"></i> Backlog</a></li>
            <li><a
                    href="<c:url value="/${project.projectId}/scrum/board"/>"><i class="fa fa-list-alt"></i> <s:message
                    code="agile.board"/></a></li>
            <li><a
                    href="<c:url value="/${project.projectId}/scrum/reports"/>"><i class="fa fa-line-chart"></i>
                <s:message
                        code="agile.reports"/></a></li>
        </ul>
    </div>
    <div class="row marginleft_0">
        <c:if test="${not empty tags}">
            <div>
                <i class="fa fa-tags"></i>&nbsp;<s:message code="task.tags"/>:
                <c:forEach items="${tags}" var="tag"><span class="tag label label-info theme tag_filter a-tooltip"
                                                           title="<s:message code="task.tags.click.filter"/>"
                                                           data-name="${tag}">${tag}</span></c:forEach>
            </div>
        </c:if>
    </div>
    <div class="row">
        <div class="col-sm-12 col-md-7">
            <h4>
                <s:message code="agile.sprints"/>
            </h4>
            <c:if test="${can_edit}">
                <div style="margin: 5px 0px;">
                    <s:message code="agile.create.sprint"/>&nbsp;
                    <button id="create_sprint" class="btn btn-default btn-sm">
                        <i class="fa fa-lg fa-plus"></i>
                    </button>
                </div>
            </c:if>
            <form id="create_form"
                  action="<c:url value="/${project.projectId}/scrum/create"/>"
                  method="post"></form>
            <c:forEach var="entry" items="${sprint_result}">
                <c:set var="sprint" value="${entry.key}"/>
                <c:set var="count" value="0"/>
                <div class="sprint_div">
                        <%---Buttons --%>
                        <%-- Only print button for sprint at top (active or not_active) --%>
                    <c:if test="${can_edit}">
                        <div class="buttons_panel" style="float: right">
                            <c:if test="${not sprint.active}">
                                <c:if test="${not b_rendered}">
                                    <c:set var="b_rendered" value="true"/>
                                    <button class="btn btn-default btn-sm" id="start-sprint"
                                            data-sprint="${sprint.id}" data-sprintNo="${sprint.sprintNo}"
                                            data-toggle="modal"
                                            data-target="#startSprint">
                                        <i class="fa fa-lg fa-play"></i>&nbsp;
                                        <s:message code="agile.sprint.start"/>
                                    </button>
                                </c:if>
                                <a class="btn btn-default btn-sm a-tooltip confirm_action"
                                   href="<c:url value="/scrum/delete?id=${sprint.id}"/>"
                                   title="<s:message code="main.delete" />"
                                   data-lang="${pageContext.response.locale}"
                                   data-msg='<s:message code="agile.sprint.delete.confirm"></s:message>'>
                                    <i class="fa fa-lg fa-trash-o"></i>
                                </a>
                            </c:if>
                            <c:if test="${sprint.active}">
                                <c:set var="b_rendered" value="true"/>
                                <a class="btn btn-default btn-sm confirm_action"
                                   href="<c:url value="/scrum/stop?id=${sprint.id}"/>"
                                   data-lang="${pageContext.response.locale}"
                                   data-msg='<s:message code="agile.sprint.finish.confirm"></s:message>'>
                                    <i class="fa fa-lg fa-check"></i>&nbsp;<s:message
                                        code="agile.sprint.finish"/>
                                </a>
                            </c:if>

                        </div>
                    </c:if>
                        <%--Sprint content --%>
                    <div>
                        <h4>Sprint ${sprint.sprintNo}</h4>
                    </div>
                    <c:if test="${sprint.active}">
                        <p>
                            <i class="fa fa-lg fa-flip-horizontal fa-history"></i>
                                ${sprint.start_date} - ${sprint.end_date}
                        </p>
                    </c:if>
                    <div id="sprint_${sprint.id}" class="table_sprint" data-id="${sprint.id}">
                            <%--Sprint task --%>
                        <c:forEach items="${entry.value}" var="task">
                            <c:set var="count" value="${count + task.story_points}"/>
                            <div class="agile-list"
                                 data-id="${task.id}" id="${task.id}" data-tags="${task.getTagsList()}"
                                 sprint-id="${sprint.id}"
                                    <c:if test="${task.state eq 'CLOSED' }">
                                        style="text-decoration: line-through;"
                                    </c:if>>
                                <div style="display: table-cell; width: 100%;">
                                    <i class="fa fa-caret-right toggler theme show-more-details a-tooltip"
                                       data-tab="sprint-moredetails-${task.id}" data-quick="true"
                                       data-task="${task.id}" title="<s:message code="agile.card.showmore"/>"></i>
                                    <span style="margin-right: -5px">
                                        <t:type type="${task.type}" list="true"/>
                                    </span>
                                    <a href="<c:url value="/task/${task.id}"/>"
                                       style="color: inherit;">[${task.id}] ${task.name}</a>
                                    <form id="sprint_remove_${task.id}"
                                          action="<c:url value="/${project.projectId}/scrum/sprintRemove"/>"
                                          method="post">
                                        <input type="hidden" name="taskID" value="${task.id}">
                                    </form>
                                </div>
                                <div class="pointsdiv pull-right">
                                    <c:if test="${!sprint.active}">
                                        <%--STORY POINTS--%>
                                        <c:if test="${task.estimated}">
                                            <c:if test="${task.story_points == 0 && task.estimated}">
                                                <c:set var="points_txt">?</c:set>
                                            </c:if>
                                            <c:if test="${task.story_points ne 0 && task.estimated}">
                                                <c:set var="points_txt">${task.story_points}</c:set>
                                            </c:if>
                                            <span class="points badge theme">
                                                <span class="point-value"
                                                      data-points="${task.story_points}">${points_txt}</span>
                                                <input class="point-input" data-id="${task.id}">
                                                <span class="point-approve" style="display:none;cursor: pointer;"><i
                                                        class="fa fa-check"></i></span>
                                                <span class="point-cancel" style="display:none;cursor: pointer;"><i
                                                        class="fa fa-times"></i></span>
                                                <span class="point-edit"><i class="fa fa-pencil points"></i></span>
                                            </span>
                                        </c:if>
                                    </c:if>
                                    <c:if test="${sprint.active}">
                                        <c:if test="${task.story_points ne 0 && task.estimated}">
                                            <span class="badge theme point-value"
                                                  data-points="${task.story_points}">${task.story_points} </span>
                                        </c:if>
                                        <c:if test="${task.story_points eq 0 && task.estimated}">
                                            <span class="badge theme point-value" data-points=0>? </span>
                                        </c:if>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${can_edit || task.owner.id == user.id || task.assignee.id == user.id }">
                                            <c:set var="can_work" value="true"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="can_work" value="false"/>
                                        </c:otherwise>
                                    </c:choose>
                                        <%--ESTIMATE--%>
                                    <c:if test="${can_work && task.loggedWork eq '0m' }">
                                    <span class="estimate-modal a-tooltip clickable" data-toggle="modal"
                                          title="<s:message code="task.estimate.change"/>"
                                          data-target="#new-time-modal-dialog" data-taskID="${task.id}"
                                          data-val="${task.estimate}">
                                            <i class="fa fa-calendar-o" aria-hidden="true"></i>
                                     </span>
                                    </c:if>
                                        <%--REMAINING--%>
                                    <c:if test="${can_work && task.loggedWork ne '0m' }">
                                    <span class="remaining-modal a-tooltip clickable" data-toggle="modal"
                                          title="<s:message code="task.remaining.change"/>"
                                          data-target="#new-time-modal-dialog" data-taskID="${task.id}"
                                          data-val="${task.remaining}">
                                        <i class="fa fa-calendar"></i>
                                    </span>
                                    </c:if>
                                </div>
                                <div class="more-details-div" id="sprint-moredetails-${task.id}" style="display: none;">
                                    <div class="mod-header-bg">
                                    <span class="mod-header-title theme">
                                        <i class="fa fa-lg fa-clock-o"></i> <s:message code="task.timetrack"/>
                                    </span>
                                    </div>
                                    <div>
                                        <s:message code="task.closed"/>: <span
                                            class="pull-right">${task.percentage}%</span><br>
                                        <s:message code="task.estimate"/>: <span
                                            class="pull-right">${task.estimate}</span><br>
                                        <s:message code="task.logged"/>: <span
                                            class="pull-right">${task.loggedWork}</span><br>
                                        <s:message code="task.remaining"/>: <span
                                            class="pull-right">${task.remaining}</span>
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
                                                  title="<s:message code="task.tags.click.filter"/>"
                                                  data-name="${tag}">${tag}</span>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                    <div class="agile-card-subtasks">
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <div style="text-align: right;margin-top:5px">
                        <s:message code="agile.storypoints.total"/>
                        <span id="sprint_points_${sprint.id}" class="badge theme total-points" sprint-id="${sprint.id}"
                              style="margin: 0px 5px;" data-count="${count}">${count}</span>
                    </div>
                </div>
                <hr>
            </c:forEach>
        </div>
        <!-- 	FREE TASK LIST -->
        <div class="col-sm-12 col-md-5">
            <h4>
                <s:message code="task.tasks"/>
                <span class="btn btn-default pull-right" id="save_order" style="display:none;width: 120px"><i
                        class="fa fa-floppy-o"></i>&nbsp;<s:message code="main.save.order"/> </span>
                <span class="a-tooltip btn btn-default pull-right toggler-colapse" id="colapse_all" title="<s:message code="main.collapse.all"/>"
                      style="display: none"><i
                        class="fa fa-minus-square-o"></i></span>
            </h4>
            <ul id="sortable" style="margin-top: 20px;margin-left: -40px;">
                <c:forEach items="${tasks}" var="task">
                    <c:if test="${not task.inSprint && task.state ne 'CLOSED'}">
                        <div class="agile-card" data-id="${task.id}" id="${task.id}" data-tags="${task.getTagsList()}">
                            <div style="display: table-cell; width: 100%;">
                                <i class="fa fa-caret-right toggler theme show-more-details a-tooltip"
                                   data-tab="free-moredetails-${task.id}" data-quick="true"
                                   data-task="${task.id}" title="<s:message code="agile.card.showmore"/>"></i>
                                <span style="margin-right: -5px">
                                    <t:type type="${task.type}" list="true"/>
                                </span>
                                <span style="margin-right: -5px">
                                    <t:priority priority="${task.priority}" list="true"/>
                                </span>
                                <a href="<c:url value="/task/${task.id}"/>"
                                   style="color: inherit;">[${task.id}] ${task.name}</a>
                                <form id="sprint_assign_${task.id}"
                                      action=""
                                      method="post">
                                    <input type="hidden" name="taskID" value="${task.id}"> <input
                                        type="hidden" id="sprintID_${task.id}" name="sprintID">
                                </form>
                            </div>
                            <div class="pointsdiv pull-right">
                                    <%--STORY POINTS--%>
                                <c:if test="${task.estimated}">
                                    <c:if test="${task.story_points == 0 && task.estimated}">
                                        <c:set var="points_txt">?</c:set>
                                    </c:if>
                                    <c:if test="${task.story_points ne 0 && task.estimated}">
                                        <c:set var="points_txt">${task.story_points}</c:set>
                                    </c:if>
                                    <span class="points badge theme">
                                    <span class="point-value" data-points="${task.story_points}">${points_txt}</span>
                                    <input class="point-input" data-id="${task.id}">
                                    <span class="point-approve" style="display:none;cursor: pointer;"><i
                                            class="fa fa-check"></i></span>
                                    <span class="point-cancel" style="display:none;cursor: pointer;"><i
                                            class="fa fa-times"></i></span>
                                    <span class="point-edit"><i class="fa fa-pencil points"></i></span>
                                    </span>
                                </c:if>
                                <c:choose>
                                    <c:when test="${can_edit || task.owner.id == user.id || task.assignee.id == user.id }">
                                        <c:set var="can_work" value="true"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="can_work" value="false"/>
                                    </c:otherwise>
                                </c:choose>
                                    <%--ESTIMATE--%>
                                <c:if test="${can_work && task.loggedWork eq '0m' }">
                                    <span class="estimate-modal a-tooltip clickable" data-toggle="modal"
                                          title="<s:message code="task.estimate.change"/>"
                                          data-target="#new-time-modal-dialog" data-taskID="${task.id}"
                                          data-val="${task.estimate}">
                                            <i class="fa fa-calendar-o" aria-hidden="true"></i>
                                     </span>
                                </c:if>
                                    <%--REMAINING--%>
                                <c:if test="${can_work && task.loggedWork ne '0m' }">
                                    <span class="remaining-modal a-tooltip clickable" data-toggle="modal"
                                          title="<s:message code="task.remaining.change"/>"
                                          data-target="#new-time-modal-dialog" data-taskID="${task.id}"
                                          data-val="${task.remaining}">
                                        <i class="fa fa-calendar"></i>
                                    </span>
                                </c:if>
                            </div>
                            <div class="more-details-div" id="free-moredetails-${task.id}" style="display: none;">
                                <div class="mod-header-bg">
                                    <span class="mod-header-title theme">
                                        <i class="fa fa-lg fa-clock-o"></i> <s:message code="task.timetrack"/>
                                    </span>
                                </div>
                                <div>
                                    <s:message code="task.closed"/>: <span class="pull-right">${task.percentage}%</span><br>
                                    <s:message code="task.estimate"/>: <span
                                        class="pull-right">${task.estimate}</span><br>
                                    <s:message code="task.logged"/>: <span
                                        class="pull-right">${task.loggedWork}</span><br>
                                    <s:message code="task.remaining"/>: <span
                                        class="pull-right">${task.remaining}</span>
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
                                                  title="<s:message code="task.tags.click.filter"/>"
                                                  data-name="${tag}">${tag}</span>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <div class="agile-card-subtasks">
                                </div>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>
<c:if test="${can_edit}">
    <jsp:include page="../modals/sprint.jsp"/>
</c:if>
<jsp:include page="../modals/estimate.jsp"/>
<script>
    $(document).ready(function ($) {
        var assign_txt = '<s:message code="agile.assing"/>';
        var assing_too_txt = '<s:message code="agile.assing2"/>';
        var remove_txt = '<s:message code="agile.sprint.remove"/>';
        var empty_txt = '<s:message code="agile.sprint.empty"/>';
        var lang = "${pageContext.response.locale}";
        bootbox.setDefaults({
            locale: lang
        });
        <c:if test="${can_edit}">
        reloadEvents();
        $("#create_sprint").click(function () {
            $("#create_form").submit();
        });
        $("#start-sprint").click(function (e) {
            var title = '<i class="fa fa-play"></i>&nbsp;<s:message code="agile.sprint.start.title" />';
            var id = $(this).data('sprint');
            var no = $(this).data('sprintno');
            var title = '<i class="fa fa-play"></i>&nbsp;<s:message code="agile.sprint.start.title" /> ' + no;
            $("#sprintID").val(id);
            if ($(".agile-list[sprint-id=" + id + "]").size() == 0) {
                showWarning(empty_txt);
            }
            $("#sprintStartModal").html(title);
            $("#errors").html("");
            $("#sprintStart").val("");
            $("#sprintStartTime").val("");
            $("#sprintEnd").val("");
            $("#sprintEndTime").val("");

        });

        $("#sortable").sortable({
            cursor: 'move',
            update: function (event, ui) {
                $("#save_order").show("highlight", {color: '#5cb85c'}, 1000);
            }
        });
        $("#save_order").click(function () {
            var order = $("ul#sortable").sortable("toArray");
            var url = '<c:url value="/agile/order"/>';
            var project = '${project.id}';
            showWait(true);
            $.post(url, {ids: order, project: project}, function (result) {
                showWait(false);
                $("#save_order").hide("highlight", {color: '#5cb85c'}, 1000);
            });
        });

        $(".print_cards").click(function () {
            var url = '<c:url value="/${project.projectId}/agile/cardsprint"/>';
            var sprintid = $(this).data("sprintid");
            if (sprintid) {
                url += "?sprint=" + sprintid;
            }
            window.open(url, "Cards Print");
        });

        $(".table_sprint").droppable({
            activeClass: "state_default",
            hoverClass: "state_hover",
            drop: function (event, ui) {
                var target = $(this);
                var dragged = ui.draggable;
                var taskID = ui.draggable.attr("id");
                var sprintID = $(this).data('id');
                checkIfActiveAndSend(taskID, sprintID, dragged);
            }
        });

        function reloadEvents() {
            $('.agile-card').unbind('contextmenu');
            $('.agile-list').unbind('contextmenu');
            $('.agile-card').contextPopup({
                title: assign_txt,
                items: [
                    <c:forEach items="${sprints}" var="sprint">
                    {
                        label: assing_too_txt + ' ' + "${sprint.sprintNo}",
                        icon: '',
                        action: function (event) {
                            var taskID = event.currentTarget.id;
                            var sprintID = "${sprint.id}";
                            var task = event.currentTarget;
                            checkIfActiveAndSend(taskID, sprintID, task);
                        }
                    },
                    </c:forEach>
                    ,
                    null,
                    {
                        label: 'Move to top of backlog',
                        icon: '',
                        action: function (event) {
                            console.log("Top");
                            var task = event.currentTarget;
                            $("#sortable").prepend(task);
                            $("#save_order").show("highlight", {color: '#5cb85c'}, 1000);
                        }
                    }

                    ,
                    {
                        label: 'Move to bottom of backlog',
                        icon: '',
                        action: function (event) {
                            console.log("Bottom");
                            var task = event.currentTarget;
                            $("#sortable").append(task);
                            $("#save_order").show("highlight", {color: '#5cb85c'}, 1000);
                        }
                    }
                ]
            });

            $('.agile-list').contextPopup({
                items: [{
                    label: remove_txt,
                    icon: '',
                    action: function (event) {
                        var taskID = event.currentTarget.id;
                        var sprintID = event.currentTarget.getAttribute('sprint-id');
                        var url = '<c:url value="/scrum/isActive"/>';
                        var message = '<s:message code="agile.sprint.remove.confirm"/>';
                        var task = event.currentTarget;
                        console.log(task);
                        $.get(url, {id: sprintID}, function (active) {
                            if (active) {
                                bootbox.confirm(message, function (result) {
                                    if (result) {
                                        removeFromSprint(taskID, sprintID, task)
                                    }
                                });
                            } else {
                                removeFromSprint(taskID, sprintID, task)
                            }
                        });
                    }
                }]
            });
        }

        function checkIfActiveAndSend(taskID, sprintID, task) {
            showWait(true);
            var url = '<c:url value="/scrum/isActive"/>';
            $.get(url, {id: sprintID}, function (active) {
                if (active) {
                    var message = '<s:message code="agile.sprint.add.confirm"/>';
                    bootbox.confirm(message, function (result) {
                        if (result) {
                            addToSprint(taskID, sprintID, task);
                            console.log("Added to sprint" + sprintID);
                        }
                        else {
                            location.reload();
                        }
                    });
                } else {
                    addToSprint(taskID, sprintID, task);
                }
            });
        }

        //EVENTS
        function addToSprint(taskID, sprintID, task) {
            var url = '<c:url value="/task/sprintAssign"/>';
            $("#save_order").hide();
            $.post(url, {taskID: taskID, sprintID: sprintID}, function (result) {
                if (result.code == 'OK') {
                    $(task).removeClass("agile-card");
                    $(task).addClass("agile-list");
                    $(task).attr("sprint-id", sprintID);
                    $(task).detach().prependTo("#sprint_" + sprintID);
                    reloadEvents();
                    showSuccess(result.message);
                    reloadPoints(sprintID);
                } else {
                    showWarning(result.message);
                }
                showWait(false);
            });
        }

        function removeFromSprint(taskID, sprintID, task) {
            showWait(true);
            var url = '<c:url value="/task/sprintRemove"/>';
            $("#save_order").hide();
            $.post(url, {taskID: taskID, sprintID: sprintID}, function (result) {
                if (result.code == 'OK') {
                    $(task).addClass("agile-card");
                    $(task).removeClass("agile-list");
                    $(task).attr("sprint-id", "");
                    $(task).detach().prependTo("#sortable");
                    reloadEvents();
                    showSuccess(result.message);
                    reloadPoints(sprintID);
                } else {
                    showWarning(result.message);
                }
                showWait(false);
            });
        }

        function reloadPoints(sprintID) {
            $("#sprint_points_" + sprintID).html(0);
            $(".agile-list[sprint-id=" + sprintID + "]").each(function () {
                var task_SP = parseInt($(this).find(".point-value").data("points"));
                if (!$.isNumeric(task_SP)) {
                    task_SP = 0;
                }
                var current_SP = parseInt($("#sprint_points_" + sprintID).html()) + task_SP;
                $("#sprint_points_" + sprintID).html(current_SP);
            })
        }

        //points
        $('.point-edit').click(function () {
            togglePoints($(this));
            $('#point_value').focus();

        });
        $('.point-approve').click(function () {
            changePoints($(this));
        });

        $('.point-input').keypress(function (e) {
            var key = e.which;
            if (key == 13)  // the enter key code
            {
                changePoints($(this));
            }
        });

        $('.point-cancel').click(function () {
            togglePoints($(this));
        });

        function togglePoints(clicked) {
            var parent = clicked.closest('.points');
            parent.find('.point-input').toggle();
            parent.find('.point-input').val('');
            parent.find('.point-value').toggle();
            parent.find('.point-approve').toggle();
            parent.find('.point-cancel').toggle();
            parent.find('.point-edit').toggleClass('hidden');
        }

        function changePoints(edited) {
            var message = "<s:message code="task.storyPoints.invalid"/>";
            var parent = edited.closest('.points');
            var input = parent.find('.point-input');
            var taskID = input.data('id');
            var points = input.val();
            if (isNumber(points) && (points >= 0 && points <= 100)) {
                showWait(true);
                $.post('<c:url value="/task/changePoints"/>', {id: taskID, points: points}, function (result) {
                    if (result.code == 'ERROR') {
                        showError(result.message);
                    }
                    else {
                        parent.find('.point-value').html(points);
                        parent.find('.point-value').data("points", points);
                        //reload points
                        var sprintID = parent.closest(".agile-list").attr("sprint-id");
                        if (sprintID) {
                            reloadPoints(sprintID);
                        }
                        showSuccess(result.message);
                    }
                    showWait(false);
                });
            } else {
                showError(message);
            }
            togglePoints(edited);
        }


        </c:if>
        small_loading_indicator = '<div id="small_loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br></div>';
        small_loading_indicator = '<div id="small_loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br></div>';
        subTaskurl = '<c:url value="/task/getSubTasks"/>';
        relatedurl = '<c:url value="/task/getRelated"/>';
        taskURL = '<c:url value="/task/"/>';
        //TODO put in one place with agile\board code
        $(".show-more-details").click(function () {
            if (!$(this).hasClass('expanded')) {
                var moreDetailsDiv = $(this).parent().nextAll('.more-details-div')
                var taskID = $(this).data('task');
                //fill in subtasks
                var targetSubtaskDiv = moreDetailsDiv.find('.agile-card-subtasks');
                targetSubtaskDiv.append(small_loading_indicator);
                $.get(subTaskurl, {taskID: taskID}, function (result) {
                    $("#small_loading").remove();
                    if (result.length > 0) {
                        var subtaskDiv = ' <div class="mod-header-bg"><span class="mod-header-title theme"><i class="fa fa-lg fa-sitemap"></i>&nbsp;<s:message code="tasks.subtasks"/></span></div><div>';
                        $.each(result, function (key, val) {
                            var closed = '';
                            if (val.state === 'CLOSED') {
                                closed = 'closed';
                            }
                            var type = getTaskType(val.type);
                            var url = taskURL + val.id;
                            subtaskDiv += '<div style="padding:2px;">' + type + ' <a href="' + url + '" class="subtaskLink ' + closed + ' black-link">' + '[' + val.id + '] ' + val.name + '</a></div>';
                        });
                        subtaskDiv += '</div>';
                        targetSubtaskDiv.html(subtaskDiv);
                    }
                });
                $(this).addClass('expanded');

            }
            $(this).toggleClass("expanded-toggler");
            if ($(".expanded-toggler").size() > 0) {
                $("#colapse_all").show()
            } else {
                $("#colapse_all").hide()
            }
        });
    });
</script>