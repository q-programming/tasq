<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<div class="white-frame" style="height:85vh;overflow:auto">
	Hello
	<security:authentication property="principal" />
	Welcome to Q-Tasker<br><br>
</div>