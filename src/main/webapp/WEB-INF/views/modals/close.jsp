<%@ page import="com.qprogramming.tasq.task.worklog.TaskResolution" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<!-- CLOSE TASK MODAL -->
<div class="modal fade" id="close_task" role="dialog"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header theme">
                <h4 class="modal-title" id="closeDialogTitle">
                    <i class="fa fa-lg fa-archive"></i>&nbsp;<s:message code="task.closeTask"/>
                </h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <span class="help-block"><s:message
                            code="task.closeTask.help" htmlEscape="false"/></span>
                    <%
                        pageContext.setAttribute("resolution", TaskResolution.values());
                    %>
                    <label><s:message code="task.resolution.choose"/></label>
                    <select id="modal_resolution" class="form-control" style="width: 200px" name="resolution">
                        <c:forEach items="${resolution}" var="enum_resolution">
                            <option value="${enum_resolution}"><s:message code="${enum_resolution.code}"/></option>
                        </c:forEach>
                    </select>
                </div>

                <div class="checkbox">
                    <div style="font-weight: bold;"><s:message code="task.estimate"/></div>
                    <label class="checkbox"> <input type="checkbox"
                                                    name="zero_checkbox" id="modal_zero_checkbox"> <s:message
                            code="task.setToZero"/>
                    </label>
                </div>
                <div id="closeSubtask" class="checkbox" style="display:none">
                    <div style="font-weight: bold;"><s:message code="tasks.subtasks"/></div>
                    <label class="checkbox"> <input type="checkbox"
                                                    name="closesubtasks" id="modal_subtasks" checked><s:message
                            code="task.subtask.closeall"/> (<span id="modal_subtaskCount"></span>)
                    </label>
                </div>
                <%--ADD comment--%>
                <div class="modal-footer" style="padding-right: 0; padding-bottom: 0">
                    <span id="modal-comment-button" class="btn btn-default">
                        <i class="fa fa-comments">&nbsp;</i><s:message code="comment.add"/>
                    </span>
                </div>
                <div id="modal-comment-div" style="display: none">
                    <label><s:message code="comment.add"/></label>
                    <button id="modal-dismiss-comment" type="button" class="close a-tooltip" style="opacity: 1"
                            aria-hidden="true" title="<s:message code="main.cancel" text="Cancel"/>">&times;</button>
                    <textarea id="modal_comment" name="message" class="form-control" rows="3"></textarea>
                    <span class="remain-span"><span class="remain"></span> <s:message
                            code="comment.charsLeft"/></span>
                </div>
            </div>
            <div class="modal-footer">
                <button id="close_task_btn" class="btn btn-default">
                    <s:message code="task.state.close"/>
                </button>
            </div>
        </div>
    </div>
</div>
<script src="<c:url value="/resources/js/trumbowyg.min.js" />"></script>
<script src="<c:url value="/resources/js/trumbowyg.preformatted.js" />"></script>
<link href="<c:url value="/resources/css/trumbowyg.min.css" />" rel="stylesheet" media="screen"/>

<security:authentication property="principal" var="user"/>
<c:if test="${user.language ne 'en' }">
    <script src="<c:url value="/resources/js/trumbowyg.${user.language}.min.js" />"></script>
</c:if>
<script>
    $(document).ready(function ($) {
        $.trumbowyg.svgPath = '<c:url value="/resources/img/trumbowyg-icons.svg"/>';
        $('#modal_comment').trumbowyg({
            lang: '${user.language}',
            removeformatPasted: true,
            autogrow: true,
            btns: ['formatting',
                '|', ['bold', 'italic', 'underline', 'strikethrough', 'preformatted'],
                '|', 'link',
                '|', 'btnGrp-justify',
                '|', 'btnGrp-lists']
        }).on('tbwchange ', function () {
            var tlength = $(this).val().length;
            remain = maxchars - parseInt(tlength);
            if (tlength > 3500) {
                $(".remain-span").show();
                $('.remain').text(remain);
                if (remain < 0) {
                    $('.remain-span').addClass("invalid");
                    $('#close_task_btn').prop('disabled', true);
                } else {
                    $('.remain-span').removeClass("invalid");
                    $('#close_task_btn').prop('disabled', false);
                }
            } else {
                $(".remain-span").hide();
            }
        });
    });

    $('#close_task').on('shown.bs.modal', function (e) {
        var subTasks = $("#modal_subtaskCount").html();
        if (subTasks > 0) {
            $("#closeSubtask").show();
        }
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = !inputInProgress;
        }
    }).on('hidden.bs.modal', function () {
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = false;
        }
    });

    $("#modal-comment-button").click(function () {
        $(this).hide();
        $("#modal-comment-div").show("blind");
    });

    $("#modal-dismiss-comment").click(function () {
        $("#modal_comment").trumbowyg('empty');
        $("#modal-comment-button").show();
        $("#modal-comment-div").hide("blind");
    });

    $("#close_task_btn").click(function () {
        $(this).attr("disabled", true);
        showWait(true);
        var comment = $("#modal_comment").val();
        var zero = $("#modal_zero_checkbox").prop('checked');
        var closeSubtasks = $("#modal_subtasks").prop('checked');
        var resolution = $("#modal_resolution").val();
        $.post('<c:url value="/task/changeState"/>', {
            id: taskID,
            state: 'CLOSED',
            message: comment,
            zero_checkbox: zero,
            closesubtasks: closeSubtasks,
            resolution: resolution
        }, function (result) {
            showWait(false);
            $('#close_task').modal('toggle');
            if (result.code != 'OK') {
                showError(result.message);
            } else {
                showSuccess(result.message);
                location.reload();
            }
        });
    });
</script>