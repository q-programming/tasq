<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<script>
$(".subtasks").click(function(){
	var taskTD = $(this);
	var task = $(this).data('task');
	showSubTasks(taskTD, task);
});

$("#opensubtask").click(function(){
	$(".subtasks").each(function(i, obj) {
		var targetID = "#"+obj.getAttribute('id');
		var target = $(targetID); 
		var task = obj.getAttribute('data-task');
		if(target.hasClass("fa-plus-square")){
			showSubTasks(target, task);
		}
	});
});
$("#hidesubtask").click(function(){
	$(".subtasks").each(function(i, obj) {
		var targetID = "#"+obj.getAttribute('id');
		var target = $(targetID); 
		target.removeClass('fa-minus-square');
		target.addClass('fa-plus-square');
	});
	$(".subtaskdiv").remove();
});


function showSubTasks(target,parentTask)
{
	var taskTD = target.closest('td');;
	var task = parentTask;
	if(target.hasClass( "fa-minus-square" )){
		var divid = "#"+ task + 'subtask';
		$(divid).remove();
	}else{
		taskTD.append(small_loading_indicator);
		$.get(apiurl,{taskID:task},function(result){
			$("#small_loading").remove();
			var div = '<div style="margin-top: 5px;border-top: 1px solid lightgray;" class="subtaskdiv" id="'+task+'subtask">';
			$.each(result, function(key,val){
				var closed ='';
				if(val.state == 'CLOSED'){
					closed = 'closed';					
				}
				var type = getTaskTypeMsg(val.type);
				var url =  taskURL + val.id;
	    		var row = '<div style="padding:2px;">'+ type + ' <a href="'+ url + '" class="subtaskLink '+closed+'">' + '[' + val.id + '] ' + val.name +'</a></div>';
	    		div+=row;
	    	});
			div+='</div>';
			taskTD.append(div);
		});
	}
	target.toggleClass('fa-minus-square');
	target.toggleClass('fa-plus-square');
}

function getTaskTypeMsg(type){
	switch(type){
		case "SUBTASK":
			var type='<i class="fa fa-lg fa-fw fa-sitemap"></i> ';
			return type;
		case "SUBBUG":
			var type='<i class="fa fa-lg fa-fw fa-bug"></i> ';
			return type;
		case "IDLE":
			var type='<i class="fa fa-lg fa-fw fa-coffee"></i> ';
			return type;
		default:
			return 'not yet added ';
	};
};


</script>