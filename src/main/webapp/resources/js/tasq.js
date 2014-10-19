function showError(message){
	var errorMsg= '<div class="alert alert-danger fade in"	style="position: fixed; bottom: 0px; width:100%">'
		+'<button type="button" class="close" data-dismiss="alert">&times;</button>'
		+ message
		+'</div>';
		$('#messages_div').append(errorMsg);
};

function showSuccess(message){
	var errorMsg= '<div class="alert alert-success fade in"	style="position: fixed; bottom: 0px; width:100%">'
		+'<button type="button" class="close" data-dismiss="alert">&times;</button>'
		+ message
		+'</div>';
		$('#messages_div').append(errorMsg);
};

