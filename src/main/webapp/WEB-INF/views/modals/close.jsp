<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- CLOSE TASK MODAL -->
<div class="modal fade" id="close_task" role="dialog"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<h4 class="modal-title" id="closeDialogTitle">
					<s:message code="task.closeTask" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="form-group">
					<span class="help-block"><s:message
							code="task.closeTask.help" /></span>
				</div>
				
				<div class="checkbox">
					<div style="font-weight: bold;"><s:message code="task.estimate"/></div>
					<label class="checkbox"> <input type="checkbox"
						name="zero_checkbox" id="modal_zero_checkbox"> <s:message
							code="task.setToZero" />
					</label>
				</div>
				<div id="closeSubtask" class="checkbox" style="display:none">
				<div style="font-weight: bold; margin-left: -22px;"><s:message code="tasks.subtasks"/></div>
					<label class="checkbox"> <input type="checkbox"
						name="closesubtasks" id="modal_subtasks" checked><s:message code="task.subtask.closeall"/> (<span id="modal_subtaskCount"></span>)
					</label>
				</div>
				<div>
					<label><s:message code="comment.add" /></label>
					<textarea id="modal_comment" name="message" class="form-control" rows="3"></textarea>
				</div>
			</div>
			<div class="modal-footer">
				<button id="close_task_btn" class="btn btn-default">
					<s:message code="task.state.close" />
				</button>
			</div>
		</div>
	</div>
</div>
<script>
	$('#close_task').on('shown.bs.modal', function (e) {
		var subTasks = $("#modal_subtaskCount").html();
		console.log(subTasks);
		if(subTasks > 0 ){
			$("#closeSubtask").show();
		}
	});
	$("#close_task_btn").click(function() {
		$(this).attr("disabled", true);
		showWait(true);
		var comment = $("#modal_comment").val();
		var zero = $("#modal_zero_checkbox").prop('checked');
		var closeSubtasks = $("#modal_subtasks").prop('checked');
		$.post('<c:url value="/task/changeState"/>', {
			id : taskID,
			state : 'CLOSED',
			message: comment,
			zero_checkbox: zero,
			closesubtasks: closeSubtasks
		}, function(result) {
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