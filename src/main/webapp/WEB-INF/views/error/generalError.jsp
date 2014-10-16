<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<p>
	<c:out value="${errorMessage}" /> 	
</p>
<h5 class="toggle_stack btn btn-default">
	<span style="font-size: x-small;"
		class="glyphicon glyphicon-chevron-right"></span> Show stacktrace
</h5>
<div
	style="display: none; padding: 5px; border: 1px solid; border-top-left-radius: 5px; border-top-right-radius: 5px; border-bottom-right-radius: 5px; border-bottom-left-radius: 5px; margin-top: 10px; background-color: white;">
	<div>
		<c:forEach items="${errorMessage.stackTrace}" var="element">
			<c:out value="${element}" />
		</c:forEach>
	</div>
</div>
<script>
$(".toggle_stack").click(function() {
	toggle($(this));
});

function toggle(obj) {
	var open = obj.attr('open');
	obj.next('div').slideToggle('slow');
	obj.children('span').toggleClass('glyphicon glyphicon-chevron-down');
	obj.children('span').toggleClass('glyphicon glyphicon-chevron-right');
	obj.attr('open',!open);
}
</script>