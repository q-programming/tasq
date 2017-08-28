<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<%--DATA CALC--%>
<c:set var="tasks_total">${TO_DO + ONGOING + COMPLETE + CLOSED + BLOCKED}</c:set>
<c:set var="tasks_todo">${TO_DO * 100 / tasks_total }</c:set>
<c:set var="tasks_ongoing">${ONGOING * 100 / tasks_total}</c:set>
<c:set var="tasks_complete">${COMPLETE *100 / tasks_total}</c:set>
<c:set var="tasks_closed">${CLOSED *100 / tasks_total}</c:set>
<c:set var="tasks_blocked">${BLOCKED*100 / tasks_total}</c:set>
<c:if test="${tasks_total > 0}">
    <div>
        <h3>[${project.projectId}]&nbsp;${project.name}</h3>
    </div>
    <div class="white-frame">
    <div class="row">
            <%--TASK BY STATUS--%>
        <div class="col-md-6 percentage-div">
            <div class="row">
                <div class="col-xs-5 col-sm-3">
                    <t:state state="TO_DO"/>&nbsp;
                    <span class="task-count">${TO_DO}</span>
                </div>
                <div class="col-xs-7 col-sm-9">
                    <div class="progress stats-progress">
                        <div class="progress-bar progress-bar-warning animated-progress"
                             data-value="${tasks_todo}%">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-5 col-sm-3">
                    <t:state state="ONGOING"/>&nbsp;
                    <span class="task-count">${ONGOING}</span>
                </div>
                <div class="col-xs-7 col-sm-9">
                    <div class="progress stats-progress">
                        <div class="progress-bar animated-progress"
                             data-value="${tasks_ongoing}%">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-5 col-sm-3">
                    <t:state state="COMPLETE"/>&nbsp;
                    <span class="task-count">${COMPLETE}</span>
                </div>
                <div class="col-xs-7 col-sm-9">
                    <div class="progress stats-progress">
                        <div class="progress-bar progress-bar-success animated-progress"
                             data-value="${tasks_complete}%">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-5 col-sm-3">
                    <t:state state="CLOSED"/>&nbsp;
                    <span class="task-count">${CLOSED}</span>
                </div>
                <div class="col-xs-7 col-sm-9">
                    <div class="progress stats-progress">
                        <div class="progress-bar  animated-progress progress-bar-closed"
                             data-value="${tasks_closed}%">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-5 col-sm-3">
                    <t:state state="BLOCKED"/>&nbsp;
                    <span class="task-count">${BLOCKED}</span>
                </div>
                <div class="col-xs-7 col-sm-9">
                    <div class="progress stats-progress">
                        <div class="progress-bar progress-bar-danger animated-progress"
                             data-value="${tasks_blocked}%">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>
</div>
<script>
    $(document).ready(function ($) {
        $('.animated-progress').css('width', '0');

        $(window).on('load', function () {//TODO switch to on load REST
            $('.task-count').each(function () {
                $(this).prop('Counter',0).animate({
                    Counter: $(this).text()
                }, {
                    duration: 2000,
                    step: function (now) {
                        $(this).text(Math.ceil(now));
                    }
                });
            });

            $('.animated-progress').each(function () {
                var itemWidth = $(this).data('value');
                $(this).animate({
                    width: itemWidth
                }, 1000);
            });
        });
    });
</script>