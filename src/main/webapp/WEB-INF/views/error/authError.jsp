<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<div class="alert alert-danger fade in" style="width: 100%;">
	<button type="button" class="close" data-dismiss="alert">&times;</button>
	${errorMessage}
</div>
<p>
	<a href="<c:url value="/"/>">
		<button class="btn btn-default" style="margin: 20px;"
			type="button">
			<i class="fa fa-chevron-left"></i>&nbsp;
			<s:message code="main.return" />
		</button></a>
</p>
