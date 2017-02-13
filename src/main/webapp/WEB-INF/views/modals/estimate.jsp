<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- Image modal -->
<div class="modal fade" id="new-time-modal-dialog" tabindex="-1" role="dialog" aria-labelledby="Image modal">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="new-time-title">
                    <s:message code="task.estimate"/>
                </h4>
            </div>
            <div class="modal-body">
                <label id="new-time-label"><s:message code="task.estimate"/></label>
                <div class="form-group has-feedback row">
                    <input type="hidden" id="new-time-taskid">
                    <input type="hidden" id="new-time-estimate">
                    <div class="col-md-4">
                        <input id="new-time-value" name="new-time-value" class="form-control"
                               type="text" value="">
                    </div>
                </div>
                <div class="row">
                    <span class="help-block col-md-12">
                        <s:message code="task.estimate.help.pattern" htmlEscape="false"/>
                    </span>
                </div>

            </div>
            <div class="modal-footer">
                <a class="btn" data-dismiss="modal"><s:message
                        code="main.cancel"/></a>
                <button id="new-time-submit" class="btn btn-default">
                    <s:message code="main.change"/>
                </button>
            </div>
        </div>
    </div>
</div>
<script>
    $('#estimate-modal-dialog').on('shown.bs.modal', function () {
        $('#new-time-value').focus();
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = !inputInProgress;
        }
    }).on('hidden.bs.modal', function () {
        if (typeof inputInProgress !== 'undefined') {
            inputInProgress = false;
        }
    });
    $(".remaining-modal").click(function () {
        var label = '<s:message code="task.remaining.change.new"/>';
        $("#new-time-estimate").val(false);
        var title = '<i class="fa fa-calendar" aria-hidden="true"></i> <s:message code="task.remaining.change"/>&nbsp;'
        setValues(title, label, $(this));
    });

    $(".estimate-modal").click(function () {
        var label = '<s:message code="task.estimate.change.new"/>';
        $("#new-time-estimate").val(true);
        var title = '<i class="fa fa-calendar-o" aria-hidden="true"></i> <s:message code="task.estimate.change"/>&nbsp;'
        setValues(title, label, $(this));
    });

    function setValues(title, label, that) {
        var taskID = that.data('taskid');
        var val = that.data('val');
        $("#new-time-value").val(val);
        $("#new-time-label").html(label);
        $("#new-time-title").html(title + taskID);
        $("#new-time-taskid").val(taskID);
    }
    $("#new-time-submit").click(function () {
        var id = $("#new-time-taskid").val();
        var newVal = $("#new-time-value").val();
        var estimate = $("#new-time-estimate").val();
        var url = "<c:url value="/task/changeEstimateTime" />";
        $.post(url, {id: id, newValue: newVal, estimate: estimate}, function (result) {
            $("#new-time-modal-dialog").modal("hide");
            if (result.code === 'ERROR') {
                showError(result.message);
            } else {
                showSuccess(result.message);
                setTimeout(function () {
                    window.location.reload(1);
                }, 5000);
            }
        })
    });


</script>