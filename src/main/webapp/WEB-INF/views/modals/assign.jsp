<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- ASSIGN TO TASK MODAL -->
<div class="modal fade ui-front" id="assign_modal" role="dialog"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="assignToModalLabel">
                    <s:message code="task.assign"/>
                </h4>
            </div>
            <div class="modal-body">
                <form id="assignModalForm" action="<c:url value="/task/assign"/>" method="post">
                    <input id="assign_taskID" name="taskID" type="hidden">
                    <input name="account" class="form-control input-sm " id="assignee_input"
                           placeholder="<s:message code="project.participant.hint"/>">
                    <div id="assignUsersLoader" style="display: none">
                        <i class="fa fa-cog fa-spin"></i>
                        <s:message code="main.loading"/>
                        <br>
                    </div>
                    <table style="width:100%;margin-top: 20px;">
                        <tr>
                            <td>
                                <s:message code="task.assignee"/>:
                                <span id="displayAssignee" style="  margin: 0 auto;  margin-top: 20px;"></span>
                            </td>
                            <td class="pull-right">
                                <button id="unassign_btn" class="btn btn-default a-tooltip"
                                        title="<s:message code="task.unassign"/>"><i class="fa fa-user-times"></i>
                                </button>

                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td class="pull-right" style="margin-top:20px">
							<span id="assign_me_btn" class="btn btn-default" style="width:150px">
								<i class="fa fa-user"></i> <s:message code="task.assignme"/>
							</span>
                                <button id="assign_btn" class="btn btn-default" disabled="disabled" style="width:150px">
                                    <i class="fa fa-user"></i> <s:message code="task.assign"/>
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
    var avatarURL = '<c:url value="/../avatar/"/>';
    var taskID;
    var projectID;

    $(".assignToTask").click(function () {
        var assignee = $(this).data('assignee');
        var assigneeid = $(this).data('assigneeid');
        fillAssigneeValues($(this).data('projectid'), $(this).data('taskid'), assignee, assigneeid);
    });


    function fillAssigneeValues(project, task, assignee, assigneeid) {
        projectID = project;
        taskID = task;
        var showAssignee;
        var title = '<i class="fa fa-lg fa-user-plus"></i>' + taskID
                + ' - <s:message code="task.assign" /> ... ';
        $("#assignToModalLabel").html(title);
        $("#assign_taskID").val(taskID);
        if (assignee == '') {
            showAssignee = '<i><s:message code="task.unassigned" /></i>';
            $("#unassign_btn").attr("disabled", "disabled");
        } else {
            showAssignee = '<img data-src="holder.js/30x30" class="avatar small" src="' + avatarURL + assigneeid + '.png"/>' + assignee;
        }
        $("#displayAssignee").html(showAssignee);
        $("#assign_btn").attr("disabled", "disabled");
    }
    $('#assign_modal').on('shown.bs.modal', function () {
        $("#assignee_input").focus();
    });
    $('#assign_modal').on('hidden.bs.modal', function () {
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = false;
        }
    });


    $("#assign_me_btn").click(function () {
        var url = '<c:url value="/task/assignMe?id="/>' + taskID;
        window.location.href = url;
    });

    $("#unassign_btn").click(function () {
        $("#assignModalForm").append('<input id="assignEmail" type="hidden" name="email" value="">');
        $("#assignModalForm").submit();
    });


    $("#assignee_input").autocomplete({
        minLength: 1,
        delay: 500,
        //define callback to format results
        source: function (request, response) {
            $(this).closest(".ui-menu").hide();
            var term = request.term;
            if (term in cache) {
                var result = cache[term];
                response($.map(result, function (item) {
                    return {
                        label: item.name + " " + item.surname,
                        value: item.id
                    }
                }));
                return;
            }
            $("#assignUsersLoader").show();
            var url = '<c:url value="/project/getParticipants"/>';
            $.get(url, {id: projectID, term: term, userOnly: true}, function (data) {
                $("#assignUsersLoader").hide();
                var results = [];
                $.each(data, function (i, item) {
                    var itemToAdd = {
                        value: item.email,
                        label: item.name + " " + item.surname,
                        id: item.id
                    };
                    results.push(itemToAdd);
                });
                cache[term] = results;
                $(this).closest(".ui-menu").show();
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
                $("#assignEmail").remove();
                $("#assignee_input").val("");
                var avatar = '<img data-src="holder.js/30x30" class="avatar small" src="' + avatarURL + ui.item.id + '.png"/>';
                var show = avatar + '&nbsp;' + ui.item.label;
                $("#displayAssignee").html(show);
                $("#assignModalForm").append('<input id="assignEmail" type="hidden" name="email" value=' + ui.item.value + '>');
                $("#assign_btn").removeAttr("disabled");
                return false;
            }
        }
    });
</script>