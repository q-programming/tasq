<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<div class="white-frame"
     style="display: table; width: 100%; height: 80vh">
    <div style="display: table-caption; margin-left: 10px;">
        <ul class="nav nav-tabs" style="border-bottom: 0">
            <li><a style="color: black" href="<c:url value="/manage/app" />">
                <i class="fa fa-cogs"></i> <s:message code="menu.manage"
                                                      text="Manage application"/>
            </a></li>
            <li><a style="color: black"
                   href="<c:url value="/manage/users" />"> <i class="fa fa-users"></i>
                <s:message code="menu.manage.users " text="Manage users"/></a></li>
            <li class="active"><a style="color: black" href="#"> <i
                    class="fa fa-lg fa-check-square"></i> <s:message
                    code="menu.manage.tasks"/></a></li>
        </ul>
    </div>
    <div class="row" style="padding: 30px;">
        <div class="col-lg-4 col-md-4">
            <div class="form-group" style="margin-top: 3px">
                <label for="project"><s:message code="project.project"/></label>
                <select id="project" style="width: 300px;" name="project"
                        class="form-control"
                        <c:if test="${not empty param.project}">disabled</c:if>>
                    <option></option>
                    <c:forEach items="${projects}" var="project">
                        <option id="${project.projectId}"
                                <c:if test="${project.id eq param.project}">selected style="font-weight:bold"
                        </c:if>
                                value="${project.id}">${project}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="col-lg-8 col-md-8">
            <div>
                <a class="btn btn-default" onclick="addURL(this)"
                   href="<c:url value="/task/updatelogs"></c:url>"> <span
                        class="fa-stack"> <i class="fa fa-repeat fa-stack-2x"></i> <i
                        class="fa fa-gear fa-stack-1x"></i>
				</span></a> Reload all logged work on all tasks within application
            </div>
            <div>
                <a class="btn btn-default" onclick="addURL(this)"
                   href="<c:url value="/task/updateFinish"></c:url>"> <span
                        class="fa-stack"> <i class="fa fa-repeat fa-stack-2x"></i> <i
                        class="fa fa-check fa-stack-1x"></i>
				</span></a> Check all tasks and set correct finish date
            </div>
            <div>
                <a class="btn btn-default" onclick="addURL(this)"
                   href="<c:url value="/task/updateClosed"></c:url>"> <span
                        class="fa-stack"> <i class="fa fa-repeat fa-stack-2x"></i> <i
                        class="fa fa-archive fa-stack-1x"></i>
				</span></a> Check all tasks and add missing close event
            </div>
            <div>
                <a class="btn btn-default" onclick="addURL(this)"
                   href="<c:url value="/task/kanban-fix"></c:url>"> <span
                        class="fa-stack"> <i class="fa fa-repeat fa-stack-2x"></i> <i
                        class="fa fa-calendar fa-stack-1x"></i>
				</span></a> Fix all release dates
            </div>
        </div>
    </div>
</div>
<script>
    function addURL(element) {
        $(element).attr('href', function () {
            return this.href + '?project=' + $("#project").val();
        });
    }
</script>
