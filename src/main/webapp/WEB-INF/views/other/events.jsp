<%@page import="com.qprogramming.tasq.task.TaskType" %>
<%@page import="com.qprogramming.tasq.task.worklog.LogType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%
    pageContext.setAttribute("types", LogType.values());
    pageContext.setAttribute("taskTypes", TaskType.values());
%>
<script>
    taskURL = '<c:url value="/task/"/>';
    apiurl = '<c:url value="/task/getSubTasks"/>';
    small_loading_indicator = '<div id="small_loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br></div>';
    loading_indicator = '<div id="loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></div>';

    function fetchWorkLogData(page, projectID) {
        var urlProj = '<c:url value="/projectEvents"/>';
        var urlAll = '<c:url value="/usersProjectsEvents"/>';
        var url;
        if (!projectID) {
            url = urlAll;
        } else {
            url = urlProj;
        }
        var avatarURL = '<c:url value="/../avatar/"/>';
        var accountURL = '<c:url value="/user/"/>';
        var loading_indicator = '<tr id="loading" class="centerPadded"><td colspan="3"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td></tr>';
        $("#eventsTable .projEvent").remove();
        $("#eventsTable").append(loading_indicator);
        $.get(url, {id: projectID, page: page}, function (data) {
            //console.log(data)
            $("#eventsTable tr").remove();
            if (!data) {
                var row = '<tr class="eventRow centerPadded"><td colspan="3"><i><s:message code="event.noEvents"/></i></td></tr>';
                $("#eventsTable").append(row);
            } else {
                printWorkLogNavigation(page, data);
                var rows = "";
                for (var j = 0; j < data.content.length; j++) {
                    var content = data.content[j];
                    var timeLogged = '<div class="time-div">' + content.time + '</div>';
                    var avatar = '<img data-src="holder.js/30x30" class="avatar small" src="' + avatarURL + content.account.id + '.png"/>';
                    var account = '<a href="' + accountURL + content.account.username + '">' + content.account.name + " " + content.account.surname + '</a>&nbsp;';
                    var event = getEventTypeMsg(content.type);
                    var task = '';
                    var row = '<tr class="projEvent"><td>' + avatar + '</td><td>';
                    if (content.task != null) {
                        task = '<a href="' + taskURL + content.task.id + '">[' + content.task.id + '] ' + content.task.name + '</a>';
                    }
                    var message = '';
                    if (content.message != null && content.message != '') {

                        message = '<div class="quote">' + content.message + '</div>';
                    }
                    row += timeLogged + account + event + task + message;
                    row += '</td></tr>';
                    rows += row;
                }
                $("#eventsTable").append(rows);
                addMessagesEvents();
                fixOldTables();
            }
        });

    }
    function printWorkLogNavigation(page, data) {
        var options = {
            bootstrapMajorVersion: 3,
            currentPage: page + 1,
            totalPages: data.totalPages,
            itemContainerClass: function (type, page, current) {
                return (page === current) ? "active" : "pointer-cursor";
            },
            numberOfPages: 10,
            onPageChanged: function (e, oldPage, newPage) {
                fetchWorkLogData(newPage - 1);
            }
        };
        $("#eventsTable_pagination_top").bootstrapPaginator(options);
        $("#eventsTable_pagination_bot").bootstrapPaginator(options);
    }

    function getEventTypeMsg(type) {
        switch (type) {
                <c:forEach items="${types}" var="enum_type">
            case "${enum_type}":
                return '<s:message code="${enum_type.code}"/> ';
                </c:forEach>
            default:
                return 'not yet added ';
        }
    }



</script>
