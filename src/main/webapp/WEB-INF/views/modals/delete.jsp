<%@ page import="com.qprogramming.tasq.account.Roles" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<c:set var="is_poweruser" value="<%=Roles.isPowerUser()%>"/>
<security:authentication property="principal" var="user"/>
<!-- Delete task modal -->
<div class="modal fade" id="delete-task-modal-dialog" tabindex="-1" role="dialog" aria-labelledby="Delete task modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header theme">
                <span id="delete-task-header"></span>
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
            </div>
            <div class="modal-body">
                <p class="text-center" id="modal-delete-confirm-msg"><i class="fa fa-lg fa-exclamation-triangle"
                                                                        style="display: initial;"></i>&nbsp<s:message
                        code="task.delete.confirm" htmlEscape="false"/></p>
                <div style="display: none;">
                    Other users working on this task:
                    <ul id="modal-delete-active-users" class="list-unstyled">

                    </ul>
                    <c:if test="${is_poweruser}">
                        <label>
                            <input id="modal-force-delete" type="checkbox" value="false"> Force delete task
                        </label>
                    </c:if>
                </div>
            </div>
            <div class="modal-footer">
                <a class="btn" data-dismiss="modal"><s:message
                        code="main.cancel"/></a>
                <input type="hidden" id="delete-taskID">
                <button id="modal-task-delete-confirm" class="btn btn-default">
                    <i class="fa fa-lg fa-trash-o"></i>&nbsp;
                    <s:message code="main.delete"/>
                </button>
            </div>
        </div>
    </div>
</div>
<script>
    function fetchActiveUsers() {
        var avatarURL = '<c:url value="/../avatar/"/>';
        var accountURL = '<c:url value="/user/"/>';
        var url = '<c:url value="/activeTaskAccounts"/>';
        var currentAccount = "${user.id}";
        //initial state
        $("#modal-force-delete").removeClass("checked").attr('checked', false);
        $('#modal-task-delete-confirm').prop("disabled", false);
        $("#modal-delete-confirm-msg").show();
        $("#modal-delete-active-users").html('');
        showWait(true);
        $.get(url, {taskID: taskID}, function (data) {
            var list = new Array();
            $.each(data, function (idx, account) {
                if (currentAccount != account.id) {
                    var avatar = '<img data-src="holder.js/30x30" class="avatar small" src="' + avatarURL + account.id + '.png"/>';
                    var accountLink = '&nbsp;<a href="' + accountURL + account.username + '" target="_blank">' + account.name + " " + account.surname + '</a>&nbsp;';
                    var row = '<li style="height: 31px;">' + avatar + accountLink + '</li>';
                    list.push(row);
                    $("#modal-delete-active-users").append(row);
                }
            });
            if (list.length > 0) {
                <c:if test="${not is_poweruser}">
                $('#modal-task-delete-confirm').prop("disabled", true);
                $("#modal-delete-confirm-msg").hide();
                </c:if>
                $.each(data, function (idx, row) {
                    $("#modal-delete-active-users").append(row);
                });
                $("#modal-delete-active-users").parent().show();
            } else {
                $("#modal-delete-active-users").parent().hide;
            }
            showWait(false);
        });
    }
    $(".delete-task-modal").click(function () {
        var id = $(this).data('taskid');
        var title = '<i class="fa fa-lg fa-trash-o"></i>&nbsp;<s:message code="task.delete" text="Delete task" /> [' + id + ']';
        $("#delete-task-header").html(title);
        $("#delete-taskID").val(id);
        fetchActiveUsers();
    });


    $('#modal-task-delete-confirm').click(function () {
        var force = $("#modal-force-delete");
        var forceProp = '';
        if (force) {
            if (force.prop("checked")) {
                forceProp = "&force=true";
            }
        }
        var url = "<c:url value="/task/delete?id="/>" + $("#delete-taskID").val() + forceProp;
        window.location = url;
    });

    $('#delete-task-modal-dialog').on('shown.bs.modal', function (e) {
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = !inputInProgress;
        }
    }).on('hidden.bs.modal', function () {
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = false;
        }
    });
</script>