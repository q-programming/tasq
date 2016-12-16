<%@page import="com.qprogramming.tasq.task.TaskType" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<%---------------------CONVERT TO TASK MODAL --%>
<div class="modal fade" id="convert2task" tabindex="-1" role="dialog"
     aria-labelledby="" aria-hidden="true">
    <div class="modal-dialog ">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 id="convertTitle" class="modal-title"></h4>
            </div>
            <form id="mainForm" name="mainForm" method="post"
                  action="<c:url value="/task/conver2task"/>">
                <div class="modal-body">
                    <input id="taskid" type="hidden" name="taskid">
                    <div>
                        <s:message code="task.subtasks.2task.help"/>
                        <div class="mod-header">
                            <h5 class="mod-header-title">
                                <s:message code="task.type"/>
                            </h5>
                        </div>
                        <div class="row">
                            <div class="col-md-5 text-center margintop_5">
                                <dvi id="currentType">
                                </dvi>
                            </div>
                            <div class="col-md-1 text-center margintop_5">
                                <i class="fa fa-lg fa-long-arrow-right hidden-xs"></i>
                                <i class="fa fa-lg fa-arrow-down visible-xs marginbottom_5"></i>
                            </div>
                            <div class="col-md-6">
                                <div class="dropdown">
                                    <button id="type_button" class="btn btn-default "
                                            style="width: 100%;${type_error}" type="button" id="dropdownMenu1"
                                            data-toggle="dropdown">
                                        <div id="task_type" class="image-combo"></div>
                                        <span class="caret"></span>
                                    </button>
                                    <ul class="dropdown-menu" role="menu"
                                        aria-labelledby="dropdownMenu1">
                                        <%
                                            pageContext.setAttribute("types", TaskType.values());
                                        %>
                                        <c:forEach items="${types}" var="enum_type">
                                            <c:if test="${not enum_type.subtask}">
                                                <li><a class="taskType" tabindex="-1" href="#"
                                                       id="${enum_type}" data-type="${enum_type}"><t:type
                                                        type="${enum_type}" show_text="true" list="true"/></a></li>
                                            </c:if>
                                        </c:forEach>
                                    </ul>
                                    <input type="hidden" name="type" id="type"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <a class="btn" data-dismiss="modal"><s:message
                            code="main.cancel"/></a>
                    <button class="btn btn-default" type="submit">
                        <s:message code="task.subtasks.2task.convert"/>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    $(".convert2task").click(function () {
        var taskID = $(this).data('taskid');
        var type = $(this).data('type');
        var project = $(this).data('project');
        var title = '<i class="fa fa-level-up"></i>&nbsp;' + taskID
                + '&nbsp;<s:message code="task.subtasks.2task" /> ';
        $("#convertTitle").html(title);
        $("#currentType").html(getSubtaskType(type));
        $("#taskid").val(taskID);
        getDefaultTaskType(project);
    });
    $(".taskType").click(function () {
        var type = $(this).data('type');
        $("#task_type").html($(this).html());
        $("#type").val(type);
    });

    function getSubtaskType(type) {
        switch (type) {
            case "IDLE":
                var type = '<i class="fa fa-lg fa-fw fa-coffee"></i> <s:message code="task.type.idle" />';
                return type;
            case "SUBTASK":
                var type = '<i class="fa fa-lg fa-fw fa-check-circle-o"></i> <s:message code="task.type.subtask" />';
                return type;
            case "SUBBUG":
                var type = '<i class="fa fa-lg fa-fw fa-bug"></i> <s:message code="task.type.subbug" />';
                return type;
            default:
                return 'not yet added ';
        }
    }
    function getDefaultTaskType(project) {
        var url = '<c:url value="/project/getDefaults"/>';
        $.get(url, {id: project}, function (result, status) {
            project = result;
            var thisType = $("#" + project.default_type);
            var type = thisType.data('type');
            $("#task_type").html(thisType.html());
            $("#type").val(type);
        });
    }
</script>
