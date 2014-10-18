<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="alert alert-danger fade in" style="width: 100%;">
	<button type="button" class="close" data-dismiss="alert">&times;</button>
	<c:out value="${errorMessage}" />
</div>
<p></p>
