<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jquery.jqplot.min.js"/>"></script>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jqplot.highlighter.min.js"/>"></script>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jqplot.dateAxisRenderer.min.js"/>"></script>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jqplot.canvasOverlay.js"/>"></script>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jqplot.cursor.min.js"/>"></script>
<%--DATA CALC--%>
<c:set var="tasks_total">${TO_DO + ONGOING + COMPLETE + CLOSED + BLOCKED}</c:set>
<c:set var="tasks_todo">${TO_DO * 100 / tasks_total }</c:set>
<c:set var="tasks_ongoing">${ONGOING * 100 / tasks_total}</c:set>
<c:set var="tasks_complete">${COMPLETE *100 / tasks_total}</c:set>
<c:set var="tasks_closed">${CLOSED *100 / tasks_total}</c:set>
<c:set var="tasks_blocked">${BLOCKED*100 / tasks_total}</c:set>
<div class="row">
    <div class="col-sm-12 col-md-4 margintop_10 form-inline">
        <div class="form-group">
            <h3><s:message code="project.stats"/>&nbsp;
                <select id="project" name="project" class="form-control">
                    <c:forEach items="${projects}" var="projectList">
                        <option
                                <c:if test="${projectList.projectId eq project.projectId}">
                                    selected
                                </c:if>
                                value="${projectList.projectId}">${projectList}</option>
                    </c:forEach>
                </select>
            </h3>
        </div>
    </div>
</div>
<div id="page-title">
</div>
<div id="main-stats-frame" class="white-frame" style="display: none;">
    <div class="row">
        <div class="col-xs-12">
            <h3>
                <a href="<c:url value="/project/${project.projectId}"/>">
                    [${project.projectId}]&nbsp;${project.name}
                </a>
            </h3>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-12">
            <div id="project-is-active" class="stats-status">
                <span class="a-tooltip" title="<s:message code="project.stats.active.title"/>">
                    <i class="fa fa-refresh fa-spin active a-tooltip project-is-active"></i>
                    <s:message code="project.stats.active"/>
                </span>
            </div>
            <div id="project-is-inactive" class="stats-status">
                <span class="a-tooltip" title="<s:message code="project.stats.inactive.title"/>">
                    <i class="fa fa-bed inactive project-is-inactive"></i>
                    <s:message code="project.stats.inactive"/>
                </span>
            </div>
        </div>
    </div>
    <div class="row">
        <%--SUMMARY--%>
        <div class="col-md-4 col-sm-6">
            <hr>
            <%--DATES--%>
            <div class="row">
                <div class="col-xs-12 col-sm-6">
                    <i class="fa fa-fw fa-calendar-plus-o"></i><s:message code="project.stats.start"/> <span
                        id="start-date"
                        class="stats-status"></span>
                </div>
                <div class="col-xs-6">
                    <i class="fa fa-fw fa-calendar"></i><s:message code="project.stats.days"/> <span id="days"
                                                                                                     class="stats-status counter"></span>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-6">
                    <i class="fa fa-fw fa-check-square"></i><s:message code="project.stats.taskCount"/>
                    <span id="task-count" class="stats-status counter"></span>
                </div>
                <div class="col-xs-6">
                    <i class="fa fa-fw fa-sitemap "></i><s:message code="project.stats.subtaskCount"/>
                    <span id="subtask-count" class="stats-status counter"></span>
                </div>
                <div class="col-xs-6">
                </div>
            </div>
            <hr>
            <%--ESTIMATIONS--%>
            <div class="row">
                <div class="col-xs-12 col-sm-9">
                    <i class="fa fa-fw fa-calendar-o"></i><s:message code="project.stats.totalEstimate"/>
                    <span id="hours-estimated" class="stats-status pull-right">
                    </span>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12 col-sm-9">
                    <i class="fa fa-fw fa-calendar-plus-o"></i><s:message code="project.stats.totalLogged"/>
                    <span id="hours-logged" class="stats-status pull-right">
                    </span>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12 col-sm-9">
                    <i class="fa fa-fw fa-calendar"></i><s:message code="project.stats.totalRemaining"/>
                    <span id="hours-left" class="stats-status pull-right">
                    </span>
                </div>
            </div>
            <hr>
            <%--TASK PER STATUS--%>
            <div class="row">
                <div class="col-xs-6 col-sm-6 col-md-4">
                    <a class="black-link" href="<c:url value="/tasks?projectID=${project.projectId}&state=TO_DO"/>">
                        <t:state state="TO_DO"/>
                    </a>
                    <span class="counter task-count">${TO_DO}</span>
                </div>
                <div class="col-xs-6 col-sm-6 col-md-8">
                    <div class="progress stats-progress">
                        <div class="progress-bar progress-bar-warning animated-progress"
                             data-value="${tasks_todo}%">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-6 col-sm-6 col-md-4">
                    <a class="black-link" href="<c:url value="/tasks?projectID=${project.projectId}&state=ONGOING"/>">
                        <t:state state="ONGOING"/>
                    </a>
                    <span class="counter task-count">${ONGOING}</span>
                </div>
                <div class="col-xs-6 col-sm-6 col-md-8">
                    <div class="progress stats-progress">
                        <div class="progress-bar animated-progress"
                             data-value="${tasks_ongoing}%">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-6 col-sm-6 col-md-4">
                    <a class="black-link" href="<c:url value="/tasks?projectID=${project.projectId}&state=BLOCKED"/>">
                        <t:state state="BLOCKED"/>
                    </a>
                    <span class="counter task-count">${BLOCKED}</span>
                </div>
                <div class="col-xs-6 col-sm-6 col-md-8">
                    <div class="progress stats-progress">
                        <div class="progress-bar progress-bar-danger animated-progress"
                             data-value="${tasks_blocked}%">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-6 col-sm-6 col-md-4">
                    <a class="black-link" href="<c:url value="/tasks?projectID=${project.projectId}&state=COMPLETE"/>">
                        <t:state state="COMPLETE"/>
                    </a>
                    <span class="counter task-count">${COMPLETE}</span>
                </div>
                <div class="col-xs-6 col-sm-6 col-md-8">
                    <div class="progress stats-progress">
                        <div class="progress-bar progress-bar-success animated-progress"
                             data-value="${tasks_complete}%">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-6 col-sm-6 col-md-4">
                    <a class="black-link" href="<c:url value="/tasks?projectID=${project.projectId}&state=CLOSED"/>">
                        <t:state state="CLOSED"/>
                    </a>
                    <span class="counter task-count">${CLOSED}</span>
                </div>
                <div class="col-xs-6 col-sm-6 col-md-8">
                    <div class="progress stats-progress">
                        <div class="progress-bar  animated-progress progress-bar-closed"
                             data-value="${tasks_closed}%">
                        </div>
                    </div>
                </div>
            </div>
            <%--TASK PER ASSIGNEE--%>
            <hr>
            <h4><s:message code="project.stats.topAssignees"/></h4>
            <div id="assignee-div">

            </div>
        </div>
        <%--CHARTS--%>
        <div class="col-md-8 col-sm-6 percentage-div">
            <div id="chart_divarea" class="row" style="height: 300px; width: 90%; margin: 20px auto">
                <div id="closedChart"></div>
            </div>
            <div id="chart_divarea2" class="row" style="height: 200px; width: 90%; margin: 20px auto">
                <div id="loggedChart" style="height: 200px;"></div>
            </div>

        </div>
    </div>
</div>
<script>
    var avatarURL = '<c:url value="/../avatar/"/>';
    var userURL = '<c:url value="/tasks?state=ALL&assignee="/>';
    var loading_indicator = '<div id="loadingoverlay" class="centerPadded"><div class="row"><div class="col-xs-12 full-page-loader"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></div></div></div>';
    $(document).ready(function ($) {
        //reset progress bars
        $('.animated-progress').css('width', '0');
        var projectID = '${project.projectId}';
        var urlProj = '<c:url value="/project/getStats"/>';
        $("#page-title").after(loading_indicator);
        $.get(urlProj, {id: projectID}, function (result) {
            $("#main-stats-frame").show();
            $("#loadingoverlay").remove();
            $("#start-date").html(result.startDate);
            $("#days").html(result.daysCount);
            if (result.active) {
                $("#project-is-inactive").remove();
            } else {
                var inactive = $("#project-is-inactive");
                inactive.append('&nbsp;<span class="time-div">( ' + result.lastEventDate + ' )</span>');
                $("#project-is-active").remove();
            }
            $("#task-count").html(result.taskCount);
            $("#subtask-count").html(result.subTaskCount);
            $("#hours-estimated").html(result.totalEstimate);
            $("#hours-logged").html(result.totalLogged);
            $("#hours-left").html(result.totalRemaining);
            printDataChart(result.closed, "closedChart", '<i class="fa fa-archive"></i>&nbsp;<s:message code="task.state.closed"/>', '<s:message code="task.state.closed"/>', false);
            printDataChart(result.logged, "loggedChart", '<i class="fa fa-bar-chart"></i>&nbsp;<s:message code="agile.timelogged.day"/>', ' h', true);
            //assignees
            $.each(result.topActive, function (key, data) {
                var user = userURL + data.account.username + '&projectID=' + projectID;
                var avatar = '<img data-src="holder.js/30x30" class="avatar small" src="' + avatarURL + +data.account.id + '.png"/>&nbsp;';
                var row =
                    '<div class="row">' +
                    '<div class="col-xs-12 col-sm-6">'
                    + '<a class="a-tooltip" href="' + user
                    + '" title="' +
                    '<s:message code="project.stats.showusers"/>'
                    + '">'
                    + avatar
                    + data.account.name + ' '
                    + data.account.surname
                    + '</a>'
                    + '</div>'
                    + '<div class="col-xs-12 col-sm-6 counter">'
                    + data.count
                    + '</div>'
                    + '</div>';
                $("#assignee-div").append(row);
            });
            animateAll();
        });
    });

    function printDataChart(data, chartID, label, unit, fill) {
        var plot;
        $.jqplot.postDrawHooks.push(function () {
            $(".jqplot-overlayCanvas-canvas").css('z-index', '0'); //send overlay canvas to back
            $(".jqplot-series-canvas").css('z-index', '1'); //send series canvas to front
            $(".jqplot-highlighter-tooltip").css('z-index', '2'); //make sure the tooltip is over the series
            $(".jqplot-event-canvas").css('z-index', '5'); //must be on the very top since it is responsible for event catchin
        });

        var chartData = new Array([]);
        chartData.pop();
        $.each(data, function (key, val) {
            chartData.push([key, val]);
        });

        if (chartData.length > 0) {
            plot = $.jqplot(chartID, [chartData], {
                title: label,
                seriesDefaults: {
                    showMarker: false,
                    shadow: false,
                    rendererOptions: {
                        smooth: true
                    },
                    fill: fill
                },
                cursor: {
                    show: true,
                    zoom: true,
                    showTooltip: false
                },
                grid: {
                    background: '#ffffff'
                },
                animate: true,
                axesDefaults: {
                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer
                },
                axes: {
                    xaxis: {
                        renderer: $.jqplot.DateAxisRenderer,
                        pad: 0,
                        tickOptions: {formatString: '%#d-%m-%Y'}
                    },
                    yaxis: {
                        pad: -2.05,
                        tickOptions: {
                            formatString: '%#d'
                        }
                    }
                },
                highlighter: {
                    show: true,
                    sizeAdjust: 1
                },
                series: [
                    {
                        color: '#5cb85c',
                        highlighter: {formatString: '[%s] %s ' + unit}
                    }],
                legend: {
                    show: false,
                }
            });
        } else {
            $("#chart_divarea").hide('slow');
            $("#no_events").show('slow');
        }
    }

    function animateAll() {
        $('.counter').each(function () {
            $(this).prop('Counter', 0).animate({
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
        $('.a-tooltip').tooltip()
    }

    $("#project").change(function () {
        var link = '<c:url value="/project/"/>' + $(this).val() + "/statistics";
        window.location = link;
    });
</script>