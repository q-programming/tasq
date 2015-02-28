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
		$(".alert").alert();
		window.setTimeout(function() { 
			$(".alert-success").alert('close');
			$(".alert-info").alert('close'); }
		,10000);
};

function showWarning(message){
	var warningMsg= '<div class="alert alert-warning fade in"	style="position: fixed; bottom: 0px; width:100%">'
		+'<button type="button" class="close" data-dismiss="alert">&times;</button>'
		+ message
		+'</div>';
		$('#messages_div').append(warningMsg);
		$(".alert").alert();
		window.setTimeout(function() { $(".alert-warning").alert('close'); }, 15000);
};

$(".alert").alert();
window.setTimeout(function() { 
	$(".alert-success").alert('close');
	$(".alert-info").alert('close'); }
,10000);
window.setTimeout(function() { $(".alert-warning").alert('close'); }, 15000);

function showWait(show){
	if(show){
		$("body").css("cursor", "wait");
	}else{
		$("body").css("cursor", "auto");
	}
}
function IsEmail(email) {
	  var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	  return regex.test(email);
}

$(document).on("click",".toggler",function(e) {
	var target = $(this).data('tab');
	$(this).nextAll(".mod-header-title-txt").toggleClass('closed');
	$('#'+target).toggle("blind", 500);
	$(this).toggleClass('closed');
	$(this).toggleClass('fa-caret-down');
	$(this).toggleClass('fa-caret-right');
});