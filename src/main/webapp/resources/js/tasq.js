function showError(message){
	var errorMsg= '<div class="alert alert-danger fade in"	style="position: fixed; bottom: 0px; width:100%">'
		+'<button type="button" class="close" data-dismiss="alert">&times;</button>'
		+ message
		+'</div>';
		$('#messages_div').append(errorMsg);
};

function showSuccess(message){
	var successMsg= '<div class="alert alert-success fade in"	style="position: fixed; bottom: 0px; width:100%">'
		+'<button type="button" class="close" data-dismiss="alert">&times;</button>'
		+ message
		+'</div>';
		$('#messages_div').append(successMsg);
};

function showWarning(message){
	var warningMsg= '<div class="alert alert-warning fade in"	style="position: fixed; bottom: 0px; width:100%">'
		+'<button type="button" class="close" data-dismiss="alert">&times;</button>'
		+ message
		+'</div>';
		$('#messages_div').append(warningMsg);
};

$(".alert").alert();
window.setTimeout(function() { 
	$(".alert-success").alert('close');
	$(".alert-info").alert('close'); }
,15000);
window.setTimeout(function() { $(".alert-warning").alert('close'); }, 20000);

function showWait(show){
	if(show){
		$("body").css("cursor", "wait");
	}else{
		$("body").css("cursor", "auto");
	}
}


