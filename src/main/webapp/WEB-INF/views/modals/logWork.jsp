<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%------------------LOG WORK MODAL ---------------------%>
<div class="modal fade" id="logWorkform" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">
                    <s:message code="task.logWork"/>
                </h4>
            </div>
            <form id="mainForm" name="mainForm" method="post"
                  action="<c:url value="/logwork"/>">
                <div class="modal-body">
                    <input id="modal_taskID" type="hidden" name="taskID">
                    <div class="form-group has-feedback">
                        <label><s:message code="task.logWork.spent"/></label> <input
                            id="loggedWork" name="loggedWork"
                            style="width: 150px; height: 25px" class="form-control"
                            type="text" value=""> <span class="help-block"><s:message
                            code="task.logWork.help"/> </span>
                    </div>
                    <div>
                        <div id="log-date-div" style="float: left; margin-right: 50px;">
                            <label><s:message code="main.date" htmlEscape="false"/></label> <input
                                id="datepicker" name="date_logged"
                                style="width: 150px; height: 25px"
                                class="form-control datepicker" type="text" value="">
                        </div>
                        <div>
                            <label><s:message code="main.time"/></label> <input
                                id="time_logged" name="time_logged"
                                style="width: 70px; height: 25px" class="form-control"
                                type="text" value="">
                        </div>
                    </div>
					<span class="help-block"><s:message
                            code="task.logWork.when.help"/> </span>
                    <div>
                        <label><s:message code="task.remaining"/></label>
                        <div class="radio">
                            <label> <input type="radio" name="estimate_reduce"
                                           id="estimate_auto" value="auto" checked> <s:message
                                    code="task.logWork.reduceAuto"/>
                            </label>
                        </div>
                        <div class="radio">
                            <label> <input type="radio" name="estimate_reduce"
                                           id="estimate_manual" value="auto"> <s:message
                                    code="task.logWork.reduceManual"/>
                            </label> <input id="remaining" name="remaining" class="form-control"
                                            style="width: 150px; height: 25px" disabled>
                        </div>
                    </div>
					<span class="help-block"><s:message
                            code="task.logWork.estimate.help"></s:message> </span>
                </div>
                <div class="modal-footer">
                    <a class="btn" data-dismiss="modal"><s:message
                            code="main.cancel"/></a>
                    <button id="log-submit" class="btn btn-default" type="submit">
                        <s:message code="main.log"/>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    $('#logWorkform').on('shown.bs.modal', function () {
        $('#loggedWork').val('');
        $('#datepicker').val('');
        $('#time_logged').val('');
        $('#remaining').val('');
        $('#log-submit').prop('disabled', false);
        $("#log-date-div").removeClass('has-error');
        $("#loggedWork").focus();
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = !inputInProgress;
        }
    }).on('hidden.bs.modal', function () {
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = false;
        }
    });

    $(".worklog").click(function () {
        var taskID = $(this).data('taskid');
        fillLogWorkValues(taskID);
    });

    function fillLogWorkValues(taskID) {
        var title = '<i class="fa fa-calendar-plus-o"></i> <s:message code="task.logWork" /> ' + taskID;
        $("#myModalLabel").html(title);
        $("#modal_taskID").val(taskID);
    }

    $(".datepicker").datepicker({
        maxDate: '0',
        dateFormat: "dd-mm-yy",
        firstDay: 1
    });
    $(".datepicker").change(function () {
        if (!isValidDate($(this).val())) {
            showWarning("<s:message code="warning.date.invalid"/>");
            $('#log-submit').prop('disabled', true);
            $("#log-date-div").addClass('has-error');
        } else {
            $('#log-submit').prop('disabled', false);
            $("#log-date-div").removeClass('has-error');
            var date = new Date;
            var minutes = (date.getMinutes() < 10 ? '0' : '') + date.getMinutes();
            var hour = date.getHours();
            $("#time_logged").val(hour + ":" + minutes);
        }
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
