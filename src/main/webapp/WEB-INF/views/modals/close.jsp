<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- CLOSE TASK MODAL -->
<div class="modal fade" id="close_task" role="dialog"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<h4 class="modal-title" id="myModalLabel">
					<s:message code="task.changeState" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="form-group">
					<span class="help-block"><s:message
							code="task.changeState.help" /></span>
				</div>
				<div class="checkbox">
					<label class="checkbox"> <input type="checkbox"
						name="zero_checkbox" id="modal_zero_checkbox"> <s:message
							code="task.setToZero" />
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
	$("#close_task_btn").click(function() {
		var comment = $("#modal_comment").val();
		var zero = $("#modal_zero_checkbox").prop('checked') 
		$.post('<c:url value="/task/changeState"/>', {
			id : taskID,
			state : 'CLOSED',
			message: comment,
			zero_checkbox: zero
		}, function(result) {
			$('#close_task').modal('toggle');
			if (result.code != 'OK') {
				showError(result.message);
			} else {
				showSuccess(result.message);
			}
		});
	});
</script>