<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="myfn" uri="/WEB-INF/tags/custom.tld" %>
<security:authentication property="principal" var="user"/>
<security:authorize access="hasRole('ROLE_ADMIN')">
    <c:set var="is_admin" value="true"/>
</security:authorize>
<c:if test="${(myfn:contains(project.participants,user) && user.isUser) || is_admin}">
    <c:set var="can_edit" value="true"/>
</c:if>
<tr>
    <td class="table_header">
        <i class="fa fa-pencil-square-o"></i>
        <s:message code="task.state.todo"/>
    </td>
    <td class="table_header">
        <i class="fa fa-spin fa-repeat"></i>
        <s:message code="task.state.ongoing"/>
    </td>
    <td class="table_header">
        <i class="fa fa-check"></i>
        <s:message code="task.state.complete"/>
    </td>
    <td class="table_header">
        <i class="fa fa-archive"></i>
        <s:message code="task.state.closed"/>
    </td>

    <td class="table_header">
        <i class="fa fa-ban"></i>
        <s:message code="task.state.blocked"/>
    </td>
</tr>
<tr>
    <td style="vertical-align: top;">
        <div class="well table_state sortable_tasks" data-state="TO_DO">
            <c:forEach items="${tasks}" var="task">
                <c:if test="${task.state eq 'TO_DO' && not task.subtask}">
                    <t:card task="${task}" can_edit="${can_edit}"/>
                </c:if>
            </c:forEach>
        </div>
    </td>
    <td style="vertical-align: top;">
        <div class="well table_state notsortable_tasks" data-state="ONGOING">
            <c:forEach items="${tasks}" var="task">
                <c:if test="${task.state eq 'ONGOING' && not task.subtask}">
                    <t:card task="${task}" can_edit="${can_edit}"/>
                </c:if>
            </c:forEach>
        </div>
    </td>
    <td style="vertical-align: top;">
        <div class="well table_state notsortable_tasks" data-state="COMPLETE">
            <c:forEach items="${tasks}" var="task">
                <c:if test="${task.state eq 'COMPLETE' && not task.subtask}">
                    <t:card task="${task}" can_edit="${can_edit}"/>
                </c:if>
            </c:forEach>
        </div>
    </td>
    <td style="vertical-align: top;">
        <div class="well table_state notsortable_tasks" data-state="CLOSED">
            <c:forEach items="${tasks}" var="task">
                <c:if test="${task.state eq 'CLOSED' && not task.subtask}">
                    <t:card task="${task}" can_edit="${can_edit}"/>
                </c:if>
            </c:forEach>
        </div>
    </td>
    <td style="vertical-align: top;">
        <div class="well table_state notsortable_tasks" data-state="BLOCKED">
            <c:forEach items="${tasks}" var="task">
                <c:if test="${task.state eq 'BLOCKED' && not task.subtask}">
                    <t:card task="${task}" can_edit="${can_edit}"/>
                </c:if>
            </c:forEach>
        </div>
    </td>
</tr>
</table>
<script>
    var currentTag;
    var maxHeight = 0;
    $(window).resize(function () {
        resizeDivs();
    });

    $(document).ready(function ($) {
        resizeDivs();
        showMembers();
        <c:if test="${can_edit}">

        $(".notsortable_tasks").sortable({
            connectWith: '.table_state',
            cursor: 'move',
            items: "div.agile-card",
            helper: 'clone',
            receive: function (ev, ui) {
                ui.item.remove();
            }
        });

        $(".sortable_tasks").sortable({
            connectWith: '.table_state',
            cursor: 'move',
            items: "div.agile-card",
            helper: 'clone',
            receive: function (ev, ui) {
                ui.item.remove();
            },
            update: function (event, ui) {
                $("#save_order").show("highlight", {color: '#5cb85c'}, 1000);
            }
        });
        $("#save_order").click(function () {
            var order = $("div.sortable_tasks").sortable("toArray");
            var url = '<c:url value="/agile/order"/>';
            var project = '${project.id}';
            showWait(true);
            $.post(url, {ids: order, project: project}, function (result) {
                showWait(false);
                $("#save_order").hide("highlight", {color: '#5cb85c'}, 1000);
            });
        });

        $(".table_state").droppable({
            activeClass: "state_default",
            hoverClass: "state_hover",
            drop: function (event, ui) {
                //event on drop
                taskID = ui.draggable.attr("id");
                subTasks = ui.draggable.attr('data-subtasks');
                var text = '<i class="fa fa-check"></i>&nbsp;<s:message code="task.closeTask"/>&nbsp;';
                $('#closeDialogTitle').html(text + taskID);
                $('#modal_subtaskCount').html(subTasks);
                var oldState = ui.draggable.attr("state");
                var state = $(this).data('state');
                if (oldState != state) {
                    var target = $(this);
                    var dragged = ui.draggable;
                    dragged.draggable({
                        connectToSortable: '.table_state',
                        helper: 'clone'
                    });
                    dragged.data('state', state);
                    target.append(dragged.clone(true).show());
                    $("#save_order").hide();
                    if (state == 'CLOSED') {
                        $('#close_task').modal({
                            show: true,
                            keyboard: false,
                            backdrop: 'static'
                        });
                        $('#' + taskID + ' a[href]').toggleClass('closed');
                    }
                    else {
                        showWait(true);
                        $("#save_order").hide();
                        $.post('<c:url value="/task/changeState"/>', {id: taskID, state: state}, function (result) {
                            if (result.code == 'ERROR') {
                                reloadmsg = ' <s:message code="main.pageReload" arguments="5"/>';
                                showError(result.message + reloadmsg);
                                window.setTimeout('location.reload()', 5000);
                            }
                            else {
                                $("#save_order").hide();
                                showSuccess(result.message);
                                if (oldState == 'CLOSED') {
                                    $('#' + taskID + ' a[href]').toggleClass('closed');
                                }
                            }
                            showWait(false);
                        });
                    }
                }
                var dropped = ui.draggable;
                var droppedOn = $(this);
                $(dropped).detach().css({top: 0, left: 0}).appendTo(droppedOn);
            },
            accept: function (dropElem) {
                var taskID = dropElem.attr("id");
                var state = $(this).data('state');
                return $("#state_" + taskID).val() != state;

            }
        });
        </c:if>
    });

    function resizeDivs() {
        var bodyheight = $(document).height();
        $(".table_state").css("min-height", bodyheight * 0.65);
        $(".table_state").each(function () {
            if (maxHeight < $(this).outerHeight()) {
                maxHeight = $(this).outerHeight();
            }
        });
        $(".table_state").css("min-height", maxHeight);
    }

    function showMembers() {
        var url = '<c:url value="/project/getParticipants"/>';
        var term = '';
        var projId = "${project.id}"
        $.get(url, {id: projId, term: term}, function (data) {
            console.log(data);
            var avatarURL = '<c:url value="/../avatar/"/>';
            for (var j = 0; j < data.length; j++) {
                var account = data[j];
                var avatar = '<img data-src="holder.js/30x30" class="avatar small member clickable a-tooltip" src="' + avatarURL + +account.id + '.png" ' +
                        'title="'+account.name+' '+ account.surname+'" data-account="'+account.id+'"/>&nbsp;';
                $("#members").append(avatar);
            }
            $('.a-tooltip').tooltip();
        });
    }


</script>