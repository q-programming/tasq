<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<c:set var="tasks_text">
    <s:message code="task.tasks" text="Tasks"/>
</c:set>
<c:set var="taskDesc_text">
    <s:message code="task.description" text="Description" arguments=""/>
</c:set>
<div class="row">
    <div class="col-sm12 col-md-6">
        <h3 class="">${project}</h3>
    </div>
    <div class="col-sm12 col-md-6">
        <div class="margintop_20 pull-right ">
            <a class="btn btn-default print_cards" style="width: 120px">
                <i class="fa fa-print"></i>&nbsp;<s:message code="agile.print"/></a>
        </div>
    </div>
</div>
<div class="white-frame"
     style="display: table; width: 100%; height: 85vh">
    <div style="display: table-caption; margin-left: 10px;">
        <ul class="nav nav-tabs" style="border-bottom: 0">
            <li class="active"><a style="color: black" href="#"><i
                    class="fa fa-list-alt"></i> <s:message code="agile.board"/></a></li>
            <li><a style="color: black"
                   href="<c:url value="/${project.projectId}/kanban/reports"/>"><i
                    class="fa fa-line-chart"></i> <s:message code="agile.reports"/></a></li>
        </ul>
    </div>
    <div class="row marginleft_0 marginright_0 margintop_20">
        <div class="col-sm-12 col-md-6">
            <i class="fa fa-users"></i>&nbsp;<s:message code="project.members"/>
        </div>
        <div class="col-sm-12 col-md-6 text-right">
            <i class="fa fa-tags"></i>&nbsp;<s:message code="task.tags"/>
        </div>
    </div>
    <div class="row marginbottom_20 marginleft_0 marginright_0">
        <div class="col-sm-12 col-md-6" id="members">
            <span class="avatar small member clickable a-tooltip unassigned button" data-account=""
                  title="<s:message code="task.unassigned" />"><i class="fa fa-2x fa-user v-middle"></i></span>
        </div>
        <div class="col-sm-12 col-md-6 pull-right text-right">
            <c:forEach items="${tags}" var="tag">
                <span class="tag label label-info theme tag_filter a-tooltip"
                      title="<s:message code="task.tags.click.filter"/>" data-name="${tag}">${tag}</span>
            </c:forEach>
        </div>
    </div>
    <span class="btn btn-default pull-right" id="save_order"
          style="display: none"><i class="fa fa-floppy-o"></i>&nbsp;Save order
    </span>
    <table style="width:100%">
        <jsp:include page="../agile/board.jsp"/>
</div>
<jsp:include page="../modals/logWork.jsp"/>
<jsp:include page="../modals/close.jsp"/>
<jsp:include page="../modals/assign.jsp"/>


