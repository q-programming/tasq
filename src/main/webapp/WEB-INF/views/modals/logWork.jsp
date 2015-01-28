<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%------------------LOG WORK MODAL ---------------------%>
<div class="modal fade" id="logWorkform" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">
					<s:message code="task.logWork" />
				</h4>
			</div>
			<form id="mainForm" name="mainForm" method="post"
				action="<c:url value="/logwork"/>">
				<div class="modal-body">
					<input id="modal_taskID" type="hidden" name="taskID">
					<div class="form-group">
						<label><s:message code="task.logWork.spent" /></label> <input
							id="loggedWork" name="loggedWork"
							style="width: 150px; height: 25px" class="form-control"
							type="text" value=""> <span class="help-block"><s:message
								code="task.logWork.help"></s:message> </span>
					</div>
					<div>
						<div style="float: left; margin-right: 50px;">
							<label><s:message code="main.date" /></label> <input
								id="datepicker" name="date_logged"
								style="width: 150px; height: 25px"
								class="form-control datepicker" type="text" value="">
						</div>
						<div>
							<label><s:message code="main.time" /></label> <input
								id="time_logged" name="time_logged"
								style="width: 70px; height: 25px" class="form-control"
								type="text" value="">
						</div>
					</div>
					<span class="help-block"><s:message
							code="task.logWork.when.help"></s:message> </span>
					<div>
						<label><s:message code="task.remaining" /></label>
						<div class="radio">
							<label> <input type="radio" name="estimate_reduce"
								id="estimate_auto" value="auto" checked> <s:message
									code="task.logWork.reduceAuto" />
							</label>
						</div>
						<div class="radio">
							<label> <input type="radio" name="estimate_reduce"
								id="estimate_manual" value="auto"> <s:message
									code="task.logWork.reduceManual" />
							</label> <input id="remaining" name="remaining" class="form-control"
								style="width: 150px; height: 25px" disabled>
						</div>
					</div>
					<span class="help-block"><s:message
							code="task.logWork.estimate.help"></s:message> </span>
				</div>
				<div class="modal-footer">
					<a class="btn" data-dismiss="modal"><s:message
							code="main.cancel" /></a>
					<button class="btn btn-default" type="submit">
						<s:message code="main.log" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<script>
	$(".worklog").click(function() {
		var taskID = $(this).data('taskid');
		var title = '<i class="fa fa-calendar"></i> <s:message code="task.logWork" /> ' + taskID;
		$("#myModalLabel").html(title);
		$("#modal_taskID").val(taskID);
	});

	$(".datepicker").datepicker({
		maxDate : '0',
		dateFormat : "dd-mm-yy",
		firstDay : 1
	});
	$(".datepicker").change(function() {
		var date = new Date;
		var minutes = date.getMinutes();
		var hour = date.getHours();
		$("#time_logged").val(hour + ":" + minutes);
	});
	$("#time_logged").mask("Z0:A0", {
		translation : {
			'Z' : {
				pattern : /[0-2]/
			},
			'A' : {
				pattern : /[0-5]/
			}
		},
		placeholder : "__:__"
	});
	$("#estimate_manual").change(function() {
		$('#remaining').attr("disabled", !this.checked);
	});
	$("#estimate_auto").change(function() {
		$('#remaining').val("");
		$('#remaining').attr("disabled", this.checked);
	});
</script>
