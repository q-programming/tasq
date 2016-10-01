<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<security:authentication property="principal" var="user"/>
<div class="white-frame col-lg-10 col-md-10"
     style="overflow: auto; display: table">
    <div style="display: table-caption; margin-left: 10px;">
        <ul class="nav nav-tabs" style="border-bottom: 0">
            <li><a style="color: black" href="<c:url value="/task/create"/>"><i class="fa fa-plus"></i> <s:message
                    code="task.create" text="Create task"/></a></li>
            <li class="active"><a style="color: black" href="#"><i class="fa fa-download"></i> <s:message
                    code="task.import"/></a></li>
        </ul>
    </div>
    <div>
        <samp>
            ${logger}
        </samp>
        <span class="btn btn-default pull-right" onclick="location.href='<c:url value="/"/>';">Ok</span>
    </div>
</div>
	
