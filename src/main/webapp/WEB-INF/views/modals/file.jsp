<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<!-- ATTACH FILE TO TASK MODAL -->
<div class="modal fade" id="files_task" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header theme">
				<h4 class="modal-title" id="fileModalLabel">
					<s:message code="task.files" />
				</h4>
			</div>
			<div class="modal-body">
				<form id="submit_files" action="<c:url value="/task/attachFiles"/>" method="POST" enctype="multipart/form-data">
				<input id="file_taskID" name="taskID" type="hidden">
				<div class="pull-right">
					<span id="addMoreFiles" class="btn btn-default btn-sm">
						<i class="fa fa-plus"></i><i class="fa fa-file"></i>&nbsp;Add more files
					</span>
				</div>
				<table id="fileTable" style="width: 300px;">
				</table>
				</form>
			</div>
			<div class="modal-footer">
				<a class="btn" data-dismiss="modal"><s:message
							code="main.cancel" /></a>
				<button id="upload_file_btn" class="btn btn-default">
					<s:message code="task.addFiles" />
				</button>
			</div>
		</div>
	</div>
</div>
<script>
var fileTypes='.doc,.docx,.rtf,.txt,.odt,.xls,.xlsx,.ods,.csv,.pps,.ppt,.pptx,.jpg,.png,.gif';
$(".addFileButton").click(function() {
	var taskID = $(this).data('taskid');
	var title = '<i class="fa fa-file"></i>' + taskID+' - <s:message code="task.addFile" /> ';
	$("#fileModalLabel").html(title);
	$("#file_taskID").val(taskID);
	addFileInput();
});

$(document).on("change",".file_upload",	function(e) {
	var td = $(this).closest("td");
	td.find(".removeFile").remove();
	td.append('<span class="btn btn-default pull-right removeFile"><i class="fa fa-trash"></i><span>');
});

$("#addMoreFiles").click(function() {
	addFileInput();
});

$(document).on("click", ".removeFile", function(e) {
	$(this).closest("tr").remove();
	if ($('#fileTable tr').length == 0) {
		addFileInput();
	}
});

function addFileInput() {
	var choose = ' <s:message code="task.chooseFile" />';
	var title = "<i class='fa fa-file'></i>" + choose;
	var inputField = '<input class="file_upload" name="files" type="file" accept="'+fileTypes+'" title="'+title+'" data-filename-placement="inside">';
	$("#fileTable").append(
			'<tr><td style="width:300px">' + inputField + '</td></tr>');
	$("#fileTable tr:last").find(".file_upload").bootstrapFileInput();
}

$("#upload_file_btn").click(function() {
	$('#submit_files').submit();
});
</script>