<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<!-- Image modal -->
<div class="modal fade" id="estimate-modal-dialog" tabindex="-1" role="dialog" aria-labelledby="Image modal">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="estimate-modal-label">
                    <s:message code="task.estimate"/>
                </h4>
            </div>
            <div class="modal-body">
                <label id="new-time-label"><s:message code="task.estimate"/></label>
                <div class="form-group has-feedback row">
                    <input type="hidden" id="new-estimate-taskID">
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
                <button id="estimate-submit" class="btn btn-default">
                    <s:message code="task.estimate.change"/>
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
        var title = '<i class="fa fa-calendar" aria-hidden="true"></i> <s:message code="task.remaining.change"/>&nbsp;' + taskID;
        var label = '<s:message code="task.remaining.change.new"/>';
        setValues(title, label, $(this));
    });

    $(".estimate-modal").click(function () {
        var title = '<i class="fa fa-calendar-o" aria-hidden="true"></i> <s:message code="task.estimate.change"/>&nbsp;' + taskID;
        var label = '<s:message code="task.estimate.change.new"/>';

        setValues(title, label, $(this));
    });

    function setValues(title, label, that) {
        var taskID = that.data('taskid');
        var val = that.data('val');
        $("#new-time-value").val(val);
        $("#new-time-label").html(label);
        $("#estimate-modal-label").html(title);
        $("#new-estimate-taskID").val(taskID);
    }


</script>