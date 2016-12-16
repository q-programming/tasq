<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%---------------------START SPRINT MODAL --%>
<div class="modal fade" id="startSprint" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header theme">
                <button type="button" class="close theme-close" data-dismiss="modal"
                        aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="sprintStartModal">
                </h4>
            </div>
            <div class="modal-body">
                <input id="project_id" type="hidden" name="project_id"
                       value="${project.id}"> <input id="sprintID" type="hidden"
                                                     name="sprintID">
                <div>
                    <div class="form-inline">
                        <label>
                            <s:message code="agile.sprint.from"/>
                        </label>
                        <div>
                            <i class="fa fa-calendar a-tooltip" title="<s:message code="main.date"/>"
                               data-html="true"></i>&nbsp;
                            <input id="sprintStart"
                                   name="sprintStart" style="width: 150px; height: 25px"
                                   class="form-control datepicker sprintDates" type="text" value="">
                            <i class="fa fa-clock-o a-tooltip" title="<s:message code="main.time"/>"></i>&nbsp;
                            <input id="sprintStartTime"
                                   name="sprintStartTime" style="width: 70px; height: 25px"
                                   class="form-control sprint_time" type="text" value="">
                        </div>
                    </div>
                    <div class="form-inline">
                        <label>
                            <s:message code="agile.sprint.to"/>
                        </label>
                        <div>
                            <i class="fa fa-calendar a-tooltip" title="<s:message code="main.date"/>"
                               data-html="true"></i>&nbsp;
                            <input id="sprintEnd"
                                   name="sprintEnd" style="width: 150px; height: 25px"
                                   class="form-control datepicker sprintDates" type="text" value="">
                            <i class="fa fa-clock-o a-tooltip" title="<s:message code="main.time"/>"></i>
                            <input id="sprintEndTime"
                                   name="sprintEndTime" style="width: 70px; height: 25px"
                                   class="form-control sprint_time" type="text" value="">
                        </div>
                    </div>
                    <p id="errors" class="text-danger"></p>
					<span class="help-block"><s:message
                            code="agile.sprint.startstop"/></span>

                </div>
            </div>
            <div class="modal-footer">
                <a class="btn" data-dismiss="modal"><s:message
                        code="main.cancel"/></a>
                <button class="btn btn-default" id="sprint_start_btn" disabled>
                    <s:message code="agile.sprint.start"/>
                </button>
            </div>
        </div>
    </div>
</div>
<script>
    $('#startSprint').on('shown.bs.modal', function () {
        $("#sprintStart").val('');
        $("#sprintStartTime").val('');
        $("#sprintEnd").val('');
        $("#sprintEndTime").val('');
        $("#sprint_start_btn").prop('disabled', true);
    });

    $(".datepicker").datepicker({
        dateFormat: "dd-mm-yy",
        firstDay: 1
    }).change(function () {
        if (!isValidDate($(this).val())) {
            showWarning("<s:message code="warning.date.invalid"/>");
            $("#sprint_start_btn").prop('disabled', true);
        } else {
//            $("#sprint_start_btn").prop('disabled', false);
            var date = new Date;
            var minutes = (date.getMinutes() < 10 ? '0' : '') + date.getMinutes();
            var hour = date.getHours();
            $(this).nextAll(".sprint_time").val(hour + ":" + minutes);
            checkDates();
        }
    });
    $(".sprint_time").mask("Z0:A0", {
        translation: {
            'Z': {
                pattern: /[0-2]/
            },
            'A': {
                pattern: /[0-5]/
            }
        },
        placeholder: "__:__"
    }).change(function () {
        var regex = /([01]\d|2[0-3]):([0-5]\d)/;
        if (!regex.test($(this).val())) {
            $(this).val('');
        }
    });

    $("#sprint_start_btn").click(function (event) {
        var start = $("#sprintStart").val();
        var end = $("#sprintEnd").val();
        var startTime = $("#sprintStartTime").val();
        var endTime = $("#sprintEndTime").val();
        var projectID = $("#project_id").val();
        var sprintID = $("#sprintID").val();

        var start_date = $.datepicker.parseDate("dd-mm-yy", start);
        var end_date = $.datepicker.parseDate("dd-mm-yy", end);
        if (start_date == null || end_date == null || start_date > end_date || startTime == "" || endTime == "") {
            var error_msg = "<s:message code="agile.sprint.startstop.error"/>";
            $("#errors").html(error_msg);
            event.preventDefault();
        }
        else {
            $.post('<c:url value="/scrum/start"/>', {
                sprintID: sprintID,
                projectID: projectID,
                sprintStart: start,
                sprintEnd: end,
                sprintStartTime: startTime,
                sprintEndTime: endTime
            }, function (result) {
                if (result.code == 'ERROR') {
                    showError(result.message);
                } else if (result.code == 'WARNING') {
                    showWarning(result.message)
                }
                else {
                    var backlogPage = '<c:url value="/${project.projectId}/scrum/backlog"/>';
                    var url = '<c:url value="/redirect"/>' + "?page=" + backlogPage + "&type=OK&message=" + result.message;
                    window.location = url;
                }
                $("#startSprint").modal('toggle');
            });
        }
    });

    function checkDates() {
        var start = $("#sprintStart").val();
        var end = $("#sprintEnd").val();
        if (start && end) {
            showWait(true);
            $.get('<c:url value="/validateSprint"/>', {
                startDate: start,
                endDate: end
            }, function (result) {
                if (result.code == 'WARNING') {
                    showWarning(result.message);
                    $("#sprint_start_btn").prop('disabled', true);
                }
                else {
                    $("#sprint_start_btn").prop('disabled', false);
                }
                showWait(false);
            });
        }
    }
</script>
