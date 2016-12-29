<%@page import="com.qprogramming.tasq.task.TaskPriority" %>
<%@page import="com.qprogramming.tasq.task.TaskState" %>
<%@ page import="com.qprogramming.tasq.task.TaskType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<style>
    .subtaskLink {
        color: inherit;
    }
</style>

<c:set var="tasks_text">
    <s:message code="task.tasks" text="Tasks"/>
</c:set>
<c:set var="taskDesc_text">
    <s:message code="task.description" text="Description"/>
</c:set>
<security:authentication property="principal" var="user"/>
<c:if test="${not empty param.projectID}">
    <c:set var="active_project" value="${param.projectID}"/>
</c:if>
<div class="padding-top5">
    <div class="row margintop_10">
        <%--PROJECT--%>
        <div class="col-sm-12 col-md-4 margintop_10 form-inline">
            <div class="form-group">
                <span>${tasks_text}</span>&nbsp;
                <select id="project" name="project" class="form-control">
                    <c:forEach items="${projects}" var="project">
                        <option
                                <c:if test="${active_project eq project.projectId}">
                                    selected
                                </c:if>
                                value="${project.projectId}">${project}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <%--END PROJECT--%>
        <%-- EXPORT --%>
        <div class="hidden-xs hidden-sm col-md-offset-5 col-md-3 margintop_10">
            <div id="buttDiv" class="pull-right">
                <a class="btn btn-default export_startstop" style="width: 200px;">
                    <i class="fa fa-upload"></i>
                    <s:message code="task.export"/>
                </a>
            </div>
            <div id="fileDiv" style="display:none" class="pull-right">
                <div class="row">
                    <div class="col-md-3">
                        <a class="btn export_startstop"><s:message code="main.cancel"/></a>
                    </div>
                    <div class="col-md-6">
                        <div class="btn-group">
                            <a class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                                <i class="fa fa-long-arrow-down"></i><i class="fa fa-file"></i> <s:message
                                    code="task.export.selected"/>&nbsp;
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu " role="menu">
                                <li>
                                    <a href="#" class="fileExport" data-type="xls"><i
                                            class="fa fa-file-excel-o"></i> <s:message
                                            code="task.export.type.excel"/>
                                    </a>
                                </li>
                                <li>
                                    <a href="#" class="fileExport" data-type="xml"><i
                                            class="fa fa-file-code-o"></i> <s:message code="task.export.type.xml"/>
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%--END EXPORT--%>
    </div>
    <div class="row">
        <%--------------FILTERS ----------------------------%>
        <div style="line-height: 30px;" class="col-sm-12 col-md-10 margintop_10">
            <c:if test="${not empty param.projectID
            || not empty param.state
            || not empty param.query
            || not empty param.priority
            || not empty param.type
            || not empty param.assignee}">
                <c:if test="${not empty param.projectID}">
                    <c:set var="projID_url">
                        projectID=${param.projectID}&
                    </c:set>
                </c:if>
                <c:if test="${not empty param.type}">
                    <c:set var="type_url">
                        type=${param.type}&
                    </c:set>
                </c:if>
                <c:if test="${not empty param.state}">
                    <c:set var="state_url">
                        state=${param.state}&
                    </c:set>
                </c:if>
                <c:if test="${not empty param.query}">
                    <c:set var="query_url">
                        query=${param.query}&
                    </c:set>
                </c:if>
                <c:if test="${not empty param.priority}">
                    <c:set var="priority_url">
                        priority=${param.priority}&
                    </c:set>
                </c:if>
                <c:if test="${not empty param.assignee}">
                    <c:set var="assignee_url">
                        assignee=${param.assignee}&
                    </c:set>
                </c:if>
                <c:if test="${not empty param.projectID}">
                <span class="filter"><s:message code="project.project"/>: <span
                        class="filter_span"> ${param.projectID}<a
                        href="<c:url value="/tasks?${state_url}${query_url}${priority_url}${assignee_url}${type_url}"/>">
                        <i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
                        </a></span></span>
                </c:if>
                <c:if test="${not empty param.type}">
                <span class="filter"><s:message code="task.type"/>:&nbsp;
                    <span
                            class="filter_span">  <t:type type="${param.type}" list="true" show_text="true"/>
                        <a href="<c:url value="/tasks?${projID_url}${query_url}${priority_url}${state_url}${assignee_url}"/>">
                        <i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
                        </a>
                    </span>
                </span>
                </c:if>
                <c:if test="${not empty param.priority}">
                <span class="filter"><s:message code="task.priority"/>:&nbsp;
                    <span class="filter_span"><t:priority priority="${param.priority}"/>
                        <a href="<c:url value="/tasks?${projID_url}${query_url}${state_url}${assignee_url}${type_url}"/>">
                        <i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
                        </a></span></span>
                </c:if>
                <c:if test="${not empty param.state}">
                    <span class="filter"><s:message code="task.state"/>: <span
                            class="filter_span"><t:state state="${param.state}"/> <a
                            href="<c:url value="/tasks?${projID_url}${query_url}${priority_url}${assignee_url}${type_url}"/>">
                        <i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i></a>
                        </span></span>
                </c:if>
                <c:if test="${not empty assignee}">
                    <span class="filter"><s:message code="task.assignee"/>: <span
                            class="filter_span">
                            <c:if test="${user eq assignee}"><s:message code="task.assignee.me"/></c:if>
                            <c:if test="${user ne assignee}">${assignee}</c:if>
                        <a href="<c:url value="/tasks?${projID_url}${query_url}${priority_url}${state_url}${type_url}"/>">
                        <i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
                    </a></span></span>
                </c:if>
                <c:if test="${not empty param.query}">
                    <c:set var="query_url">
                        query=${param.query}&
                    </c:set>
                    <c:if test="${fn:length(param.query) > 40}">
                        <c:set var="searchQueryTag">${fn:substring(param.query, 0, 40)}...</c:set>
                    </c:if>
                    <c:if test="${fn:length(param.query) < 40}">
                        <c:set var="searchQueryTag">${param.query}</c:set>
                    </c:if>
                    &nbsp;<span style="white-space:nowrap;"><s:message code="main.search"/>:&nbsp;<span
                    class="filter_span"> ${searchQueryTag}<a
                        href="<c:url value="/tasks?${projID_url}${state_url}${priority_url}"/>">
                        <i class="fa fa-times" style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
                        </a></span></span>
                </c:if>
            </c:if>
        </div>
        <%--END FILTERS--%>
        <%--SUBTASK--%>
        <div class="col-md-2 margintop_10" style="white-space: nowrap;">
            <div class="pull-right">
                <s:message code="tasks.subtasks"/>&nbsp;
                <i id="opensubtask" class="fa fa-plus-square clickable a-tooltip"
                   title="<s:message code="task.subtask.showall"/>"></i>
                <i id="hidesubtask" class="fa fa-minus-square clickable a-tooltip"
                   title="<s:message code="task.subtask.hideall"/>"></i>
            </div>
        </div>
        <%--END SUBTASK--%>
    </div>
</div>
<%--------TASK LIST ----------%>
<div class="white-frame">
    <security:authentication property="principal" var="user"/>
    <table class="table table-hover table-condensed">
        <thead class="theme">
        <tr>
            <th class="export_cell export-hidden" style="width: 30px"><input
                    id="select_all" type="checkbox" class="a-tooltip"
                    title="<s:message code="task.export.clickAll"/>"></th>
            <th style="width: 60px;text-align: center;">
                <span class="dropdown a-tooltip clickable" title="<s:message code="task.type" />">
                    <a class="filter dropdown-toggle theme" type="button" id="dropdownMenuType"
                       data-toggle="dropdown">
                        <s:message code="task.type"/> <span class="caret theme hidden-xs hidden-sm"></span></a>
                        <ul class="dropdown-menu">
                            <%
                                pageContext.setAttribute("types", TaskType.getTypes(false));
                            %>
                            <c:forEach items="${types}" var="type">
                                <li><a href="<c:url value="/tasks?${projID_url}${query_url}${state_url}${priority_url}${assignee_url}type=${type}"/>">
                                        <t:type type="${type}" list="true" show_text="true"/></a></li>
                            </c:forEach>
                        </ul>
                </span>
            </th>
            <th style="width: 30px;"><span class="dropdown a-tooltip"
                                           title="<s:message code="task.priority" />"
                                           style="padding-top: 5px; cursor: pointer;"> <a
                    class="dropdown-toggle theme" type="button" id="dropdownMenu2"
                    data-toggle="dropdown" style="color: black"> <span
                    class="caret theme"></span></a>
							<%
                                pageContext.setAttribute("priorities", TaskPriority.values());
                            %>
						<ul class="dropdown-menu">
							<c:forEach items="${priorities}" var="priority">
								<li><a
                                        href="<c:url value="/tasks?${projID_url}${query_url}${state_url}${type_url}${assignee_url}priority=${priority}"/>">
                                    <t:priority priority="${priority}"/></a></li>
                            </c:forEach>
						</ul>
				</span></th>
            <th><s:message code="task.name"/></th>
            <th class="hidden-xs hidden-sm text-center" style="width: 110px"><s:message code="task.progress"/></th>
            <th class="text-center">
                <div class="dropdown" style="padding-top: 5px; cursor: pointer;">
                    <a class="filter dropdown-toggle theme" type="button" id="dropdownMenu1"
                       data-toggle="dropdown"><s:message code="task.state"/><span
                            class="caret theme hidden-xs hidden-sm"></span></a>
                    <%
                        pageContext.setAttribute("states", TaskState.values());
                    %>
                    <ul class="dropdown-menu">
                        <c:forEach items="${states}" var="state">
                            <li><a
                                    href="<c:url value="/tasks?${projID_url}${query_url}${priority_url}${assignee_url}${type_url}state=${state}"/>">
                                <t:state state="${state}"/></a></li>
                        </c:forEach>
                        <li class="divider"></li>
                        <li><a
                                href="<c:url value="/tasks?${projID_url}${query_url}${priority_url}${assignee_url}${type_url}state=ALL"/>">
                            <t:state state="ALL"/>
                        </a>
                        </li>
                    </ul>
                </div>
            </th>
            <th style="width: 200px">
                <div class="dropdown" style="padding-top: 5px; cursor: pointer;">
                    <a class="filter dropdown-toggle theme" type="button" id="dropdownMenuAssignee"
                       data-toggle="dropdown"><s:message code="task.assignee"/><span
                            class="caret theme hidden-xs hidden-sm"></span></a>
                    <ul class="dropdown-menu" style="padding: 5px;width: 200px;z-index: 1;">
                        <li>
                            <input type="text" class="form-control input-sm"
                                   placeholder="<s:message code="project.participant.hint"/>"
                                   id="assignee_auto">
                            <div id="assignee_autoLoader" style="display: none; color:black">
                                <i class="fa fa-cog fa-spin"></i>
                                <s:message code="main.loading"/>
                                <br>
                            </div>
                        </li>
                    </ul>
                </div>
            </th>
        </tr>
        </thead>
        <%----------------TASKS -----------------------------%>
        <form id="exportTaskForm" method="POST" enctype="multipart/form-data" action="<c:url value="/task/export"/>">
            <input id="exportType" type="hidden" name="type" value="xml">
            <c:forEach items="${tasks}" var="task">
                <c:if test="${task.id eq user.activeTask}">
                    <tr class="bg-color theme">
                    <c:set var="blinker" value="blink"/>
                    <c:set var="link">
                        class="theme"
                    </c:set>
                </c:if>
                <c:if test="${task.id ne user.activeTask}">
                    <c:set var="blinker" value=""/>
                    <c:set var="tr_bg" value=""/>
                    <c:set var="link" value=""/>
                    <c:if test="${task.state eq 'CLOSED'}">
                        <c:set var="tr_bg" value="closed"/>
                    </c:if>
                    <c:if test="${task.state eq 'BLOCKED'}">
                        <c:set var="tr_bg" value="blocked"/>
                    </c:if>
                    <tr class="${tr_bg}">
                </c:if>
                <td class="export_cell export-hidden"><input class="export"
                                                             type="checkbox" name="tasks" value="${task.id}"></td>
                <td style="text-align: center;"><t:type type="${task.type}" list="true"/></td>
                <td><t:priority priority="${task.priority}" list="true"/></td>
                <td>
                    <c:if test="${task.subtasks gt 0}">
                        <i class="subtasks fa fa-plus-square" data-task="${task.id}" id="subtasks${task.id}"></i>
                    </c:if>
                    <a href="<c:url value="/task/${task.id}"/>"
                       style="color: inherit;<c:if test="${task.state eq 'CLOSED' }">
                               text-decoration: line-through;
                               </c:if>">[${task.id}]
                            ${task.name}</a>
                </td>
                <%-- 			<c:if test="${not task.estimated}"> --%>
                <%-- 				<td>${task.loggedWork}</td> --%>
                <%-- 			</c:if> --%>
                <%-- 			<c:if test="${task.estimated}"> --%>
                <td class="hidden-xs hidden-sm">
                    <div class="progress" style="width: 100px;">
                        <c:set var="logged_class"/>
                        <c:set var="percentage">${100-task.percentage_left}</c:set>
                        <c:if
                                test="${task.percentage_logged gt 100 or task.state eq 'BLOCKED'}">
                            <c:set var="logged_class">progress-bar-danger</c:set>
                            <c:set var="percentage">${100-task.overCommited}</c:set>
                        </c:if>
                        <c:if test="${task.estimate eq '0m' && task.remaining ne '0m'}">
                            <c:set var="percentage">${task.percentage_logged}</c:set>
                        </c:if>
                        <c:if test="${task.state eq 'CLOSED'}">
                            <c:set var="logged_class">progress-bar-success</c:set>
                            <c:set var="percentage">100</c:set>
                        </c:if>
                        <c:if test="${task.state eq 'TO_DO'}">
                            <c:set var="percentage">0</c:set>
                        </c:if>
                        <div class="progress-bar ${logged_class} a-tooltip" role="progressbar"
                             aria-valuenow="${percentage}" aria-valuemin="0"
                             aria-valuemax="100" style="width:${percentage}%;" title="${percentage}%"></div>
                    </div>
                </td>
                <td class="${blinker} hidden-xs"><t:state state="${task.state}" list="false"/></td>
                <td class="${blinker} visible-xs text-center"><t:state state="${task.state}" list="true"/></td>
                <td><c:if test="${empty task.assignee }">
                    <i><s:message code="task.unassigned"/></i>
                    <c:if test="${user.getIsUser()}">
                        <span class="btn btn-default btn-xxs a-tooltip assignToTask pull-right"
                              title="<s:message code="task.assign"/>" data-toggle="modal" data-target="#assign_modal"
                              data-taskID="${task.id}" data-assignee="${task.assignee}"
                              data-assigneeID="${task.assignee.id}"
                              data-projectID="${task.project.projectId}">
                                        <i class="fa fa-lg fa-user-plus"></i>
                        </span>
                    </c:if>
                </c:if> <c:if test="${not empty task.assignee}">
                    <img data-src="holder.js/20x20"
                         class="avatar xsmall"
                         src="<c:url value="/../avatar/${task.assignee.id}.png"/>"/>
                    &nbsp;<a ${link} href="<c:url value="/user/${task.assignee.username}"/>">${task.assignee}</a>
                </c:if></td>
                </tr>
            </c:forEach>
        </form>
    </table>
</div>
<div id="loading" class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="centerPadded">
                <img src="<c:url value="/resources/img/loading.gif"/>">
                <br><s:message code="task.export.prepareFile"/>
            </div>
        </div>
    </div>
</div>
<jsp:include page="subtasks.jsp"/>
<jsp:include page="../modals/assign.jsp"/>
<script>
    $(document).ready(function ($) {
        taskURL = '<c:url value="/task/"/>';
        apiurl = '<c:url value="/task/getSubTasks"/>';
        small_loading_indicator = '<div id="small_loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br></div>';

        $("#project").change(function () {
            var query = "${state_url}${query_url}${priority_url}${assignee_url}${type_url}";
            var link = '<c:url value="/tasks?projectID="/>' + $(this).val() + "&" + query;
            window.location = link + $(this).val();
        });
        $(".export_startstop").click(function () {
            $(".export_cell").toggleClass('export-hidden');
            $("#buttDiv").toggle();
            $("#fileDiv").toggle();
        });
        $(".fileExport").click(function () {
            var atLeastOnechecked = false;
            $('.export').each(function () {
                if (this.checked) {
                    atLeastOnechecked = true;
                    return false;
                }
            });
            if (atLeastOnechecked) {
                $('#loading').modal({
                    show: true,
                    keyboard: false,
                    backdrop: 'static'
                });
                var type = $(this).data('type');
                $("#exportType").val(type);
                var params = "?type=" + type
                $(".export:checked").each(function () {
                    params += "&tasks=" + this.value;
                })
                var url = '<c:url value="/task/export"/>' + params;
                window.open(url);
                location.reload();
            }
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
    });
    $("#assignee_auto").autocomplete({
        minLength: 1,
        delay: 500,
        //define callback to format results
        source: function (request, response) {
            $("#assignee_autoLoader").show();
            $("#assignee_auto").autocomplete("widget").hide();
            var term = request.term;
            if (term in cache) {
                response(cache[term]);
                return;
            }
            var url = '<c:url value="/getAccounts"/>';
            $.get(url, {id: projectID, term: term}, function (data) {
                $("#assignee_autoLoader").hide();
                var results = [];
                $.each(data, function (i, item) {
                    var itemToAdd = {
                        value: item.username,
                        label: item.name + " " + item.surname,
                        id: item.id
                    };
                    results.push(itemToAdd);
                });
                cache[term] = results;
                $("#assignee_auto").autocomplete("widget").show();
                return response(results);
            });
        },
        open: function (e, ui) {
            var termTemplate = "<span class='ui-autocomplete-term'>%s</span>";
            var acData = $(this).data('uiAutocomplete');
            var styledTerm = termTemplate.replace('%s', acData.term);
            acData.menu.element.find('a').each(function () {
                var me = $(this);
                var keywords = acData.term.split(' ').join('|');
                me.html(me.text().replace(new RegExp("(" + keywords + ")", "gi"), '<b>$1</b>'));
            });
        },
        //define select handler
        select: function (event, ui) {
            if (ui.item) {
                event.preventDefault();
                var query = "${projID_url}${state_url}${query_url}${priority_url}${type_url}&assignee=" + ui.item.value;
                var link = '<c:url value="/tasks?"/>' + query;
                window.location = link;
                return false;
            }
        }
    });
</script>