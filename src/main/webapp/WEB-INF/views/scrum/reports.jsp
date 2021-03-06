<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="t" uri="/WEB-INF/tasq.tld" %>

<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jquery.jqplot.min.js"/>"></script>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jqplot.highlighter.min.js"/>"></script>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jqplot.enhancedLegendRenderer.min.js"/>"></script>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jqplot.dateAxisRenderer.min.js"/>"></script>
<link rel="stylesheet" type="text/css"
      href="<c:url value="/resources/css/jquery.jqplot.min.css"/>"/>
<script language="javascript" type="text/javascript"
        src="<c:url value="/resources/js/jqplot.cursor.min.js"/>"></script>


<c:if test="${empty param.sprint}">
    <c:set var="ActiveSprint">${lastSprint.sprintNo }</c:set>
</c:if>
<c:if test="${not empty param.sprint}">
    <c:set var="ActiveSprint">${param.sprint}</c:set>
</c:if>

<h3>${project}</h3>
<div class="white-frame" style="display: table; width: 100%;">
    <div style="display: table-caption; margin-left: 10px;">
        <ul class="nav nav-tabs" style="border-bottom: 0">
            <li><a
                    href="<c:url value="/${project.projectId}/scrum/backlog"/>"><i class="fa fa-book"></i> Backlog</a>
            </li>
            <li><a
                    href="<c:url value="/${project.projectId}/scrum/board"/>"><i class="fa fa-list-alt"></i> <s:message
                    code="agile.board"/></a></li>
            <li class="active"><a><i class="fa fa-line-chart"></i> <s:message
                    code="agile.reports"/></a></li>
        </ul>
    </div>
    <div class="row">
        <a class="anchor" id="sprint"></a>
        <div class="col-lg-2 col-md-3 col-sm-4 hidden-xs hidden-sm">
            <div id="menu" class="bs-docs-sidebar hidden-print affix">
                <nav>
                    <ul class="nav bs-docs-sidenav">
                        <li>
                            <a href="#" id="sprintNoMenu" class="dropdown-toggle" data-toggle="dropdown" href="#"
                               role="button" aria-expanded="false">
                                <h4><b>Sprint ${ActiveSprint}</b> <span class="caret"></h4></span>
                            </a>
                            <ul class="dropdown-menu" role="menu">
                                <c:forEach var="i" begin="1" end="${lastSprint.sprintNo }">
                                    <li><a href="#" class="sprintMenuNo" data-number="${i}"> Sprint ${i}</a></li>
                                </c:forEach>
                            </ul>
                        <li><a href="#burndown_chart"><s:message code="agile.burndown"/></a></li>
                        <li><a href="#time_chart"><s:message code="agile.timelogged"/></a></li>
                        <li><a href="#events"><s:message code="agile.events"/></a></li>
                        <li><a href="#sprintTotal"><s:message code="agile.total"/></a></li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
    <div class="row" style="height: 500px;">
        <a class="anchor" id="burndown_chart"></a>
        <div class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4" id="chartdiv"
             style="height: 500px;"></div>
    </div>
    <div class="row" id="timechart-div" style="height: 300px;">
        <a class="anchor" id="time_chart"></a>
        <div id="chartdiv2" class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4"
             style="height: 300px;"></div>
    </div>
    <div class="row">
        <a class="anchor" id="events"></a>
        <div class="col-lg-10 col-md-9 col-sm-8 col-lg-offset-2 col-md-offset-3 col-sm-offset-4">
            <h4 style="text-align: center;"><i class="fa fa-calendar"></i> <s:message code="agile.events.sprint"/></h4>
            <table id="eventsTable" class="table table-bordered"
                   style="width: 100%">
                <thead class="theme">
                <tr>
                    <th style="width: 300px"><s:message code="task.task"/></th>
                    <th style="width: 100px"><s:message code="main.date"/></th>
                    <th style="width: 60px; text-align: center" colspan="2"><s:message code="agile.change"/></th>
                    <th id="timeColumn" style="width: 60px; text-align: center"><s:message code="agile.time"/></th>
                    <th style="width: 90px"><s:message code="task.activeLog"/></th>
                    <th style="width: 100px"><s:message code="agile.user"/></th>
                </tr>
                </thead>
            </table>
            <a class="anchor" id="sprintTotal"></a>
            <h4 style="text-align: center;"><i class="fa fa-list-ul"></i> <s:message code="agile.sprint.total"/></h4>
            <table id="summaryTable" class="table table-bordered"
                   style="width: 100%">
                <thead id="totalSummary" class="theme">
                </thead>
            </table>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {
        var plot;
        var plot2;
        var timePresent = false;
        var lastSprint = "${ActiveSprint}";
        var avatarURL = '<c:url value="/../avatar/"/>';
        var taskURL = '<c:url value="/task/"/>';
        var loading_indicator = '<div id="loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td>';
        $(".sprintMenuNo").click(function () {
            var number = $(this).data('number');
            renderSprintData(number);
        });

        //Initial sprint render ( from param )
        renderSprintData(lastSprint);
        var labelFormat = '%s SP';

        function renderSprintData(sprintNo) {
            //init arrays and remove first element via pop()
            time = new Array([]);
            left = new Array([]);
            burned = new Array([]);
            ideal = new Array([]);
            time.pop();
            left.pop();
            burned.pop();
            ideal.pop();
            if (plot) {
                plot.destroy();
            }
            if (plot2) {
                plot2.destroy();
            }
            $("#chartdiv").append(loading_indicator);
            $('#eventsTable tbody').html('');
            $('#summaryTable tbody').html('');
            $('#totalSummary tr').remove();
            $('th:nth-child(4)').show();
            $("#sprintNoMenu").html('<h4><b>Sprint ' + sprintNo + '</b> <span class="caret"></span></h4>');
            $.get('<c:url value="/${project.projectId}/sprint-data"/>', {sprint: sprintNo}, function (result) {
                //Fill arrays of data
                //console.log(result);
                $.each(result.timeBurned, function (key, val) {
                    if (val != 0) {
                        timePresent = true;
                    }
                    time.push([key, val]);
                });
                $.each(result.left, function (key, val) {
                    left.push([key, val]);
                });
                $.each(result.burned, function (key, val) {
                    burned.push([key, val]);
                });
                $.each(result.ideal, function (key, val) {
                    ideal.push([key, val]);
                });
                var startStop = result.start + " - " + result.stop;

                //Render worklog events
                $.each(result.worklogs, function (key, val) {
                    var task = '<td><a class="a-tooltip" href="' + taskURL + val.task.id + '" \'>[' + val.task.id + '] ' + val.task.name + '</a></td>';
                    var date = "<td>" + val.time + "</td>";
                    var event = "<td>" + getEventTypeMsg(val.type) + "</td>";
                    var change;
                    var timeLogged;
                    var storyPoints = val.task.story_points;
                    if (storyPoints === 0) {
                        storyPoints = "";
                    }
                    if (val.type === 'REOPEN' || val.type === 'TASKSPRINTADD') {
                        change = '<td style="width:30px">' + storyPoints + '</td><td style="width:30px"></td>';
                    } else if (val.type === 'ESTIMATE') {
                        change = '<td style="width:30px">' + val.message + '</td><td style="width:30px"></td>';
                    }
                    else if (val.type === 'LOG') {
                        change = '<td style="width:30px"></td><td style="width:30px"></td>';
                    }
                    else {
                        change = '<td style="width:30px"></td><td style="width:30px">' + storyPoints + '</td>';
                    }
                    var timeLogged = "<td>";
                    if (val.activity) {
                        timeLogged = "<td>";
                        timeLogged += val.message;
                    }
                    timeLogged += "</td>";
                    var account = val.account.name + " " + val.account.surname;
                    var accountTd = '<td rel="popover" data-container="body" data-placement="top" data-account="'
                        + account + '" data-account_email="' + val.account.email + '" data-account_img="' + avatarURL + val.account.id + '.png">'
                        + account
                        + '</td>';
                    var row = task + date + change + timeLogged + event + accountTd;
                    $("#eventsTable").append("<tr>" + row + "</tr>");
                });
                //Summary
                //All tasks within sprint
                var thead = '<tr><th colspan=1><s:message code="task.task" /></th><th><s:message code="task.storyPoints" /></th><th><s:message code="agile.time"/></th></tr>';
                $("#totalSummary").append(thead);
                //Total
                if (result.totalTime === '0m') {
                    result.totalTime = '0h';
                }
                var totalRow = '<tr class="theme"><th>Completed</th><th style="text-align:center">' + result.totalPoints + ' SP</th><th style="text-align:center">~ ' + result.totalTime + ' h</th>';
                $("#summaryTable").append(totalRow);
                $.each(result.tasks.CLOSED, function (key, task) {
                    if (!task.subtask) {
                        var type = getTaskType(task.type);
                        var taskCell = '<td>' + type + '<a href="' + taskURL + task.id + '">[' + task.id + '] ' + task.name + '</a></td>';
                        var metter;
                        if (task.story_points === 0) {
                            task.story_points = '-';
                        }
                        metter = '<td class="centerd">' + task.story_points + '</td><td class="centerd">' + task.loggedWork + '</td>';
                        row = '<tr>' + taskCell + metter + '</tr>';
                        $("#summaryTable").append(row);
                    }
                });
                row = '<tr><th colspan=3>Not Completed</th></tr>';
                $("#summaryTable").append(row);
                $.each(result.tasks.ALL, function (key, task) {
                    if (!task.subtask) {
                        var type = getTaskType(task.type);
                        var taskCell = '<td>' + type + '<a href="' + taskURL + task.id + '">[' + task.id + '] ' + task.name + '</a></td>';
                        var metter;
                        metter = '<td class="centerd">' + task.story_points + '</td><td class="centerd"></td>';
                        row = '<tr>' + taskCell + metter + '</tr>';
                        $("#summaryTable").append(row);
                    }
                });
                //remove loading
                $("#loading").remove();
                //render chart
                plot = $.jqplot('chartdiv', [left, burned, ideal], {
                    title: '<i class="fa fa-line-chart"></i> <s:message code="agile.burndown.chart"/><p style="font-size: x-small;">' + startStop + '</p>',
                    animate: true,
                    grid: {
                        background: '#ffffff',
                    },
                    highlighter: {
                        show: true,
                        sizeAdjust: 7.5
                    },
                    axesDefaults: {
                        labelRenderer: $.jqplot.CanvasAxisLabelRenderer
                    },
                    axes: {
                        xaxis: {
                            renderer: $.jqplot.DateAxisRenderer,
//				        tickOptions:{formatString:'%#d-%m'},
                            pad: 0,
                            min: left[0][0]
                        },
                        yaxis: {
                            pad: 0,
                            tickOptions: {
                                formatString: labelFormat,
                                show: true
                            }
                        }
                    },
                    cursor: {
                        show: true,
                        zoom: true,
                        showTooltip: false
                    },
                    series: [
                        {
                            label: '<s:message code="agile.remaining"/>',
                            rendererOptions: {
//							smooth: true
                            }
                        },
                        {
                            label: '<s:message code="agile.burned"/>',
                            rendererOptions: {
//							smooth: true
                            }
                        },
                        {
                            color: '#B34202',
                            label: '<s:message code="agile.ideal"/>',
                            lineWidth: 1,
                            markerOptions: {
                                show: false,
                            }
                        }],
                    legend: {
                        renderer: jQuery.jqplot.EnhancedLegendRenderer,
                        rendererOptions: {
                            numberRows: '1',
                        },
                        show: true,
                        location: 's',
                        placement: 'outsideGrid'
                    }
                });
                //	render time chart
                if (timePresent) {
                    $("#timechart-div").show();
                    plot2 = $.jqplot('chartdiv2', [time], {
                        title: '<i class="fa fa-bar-chart"></i> <s:message code="agile.timelogged.day"/><p style="font-size: x-small;">' + startStop + '</p>',
                        animate: true,
                        grid: {
                            background: '#ffffff',
                        },
                        axesDefaults: {
                            labelRenderer: $.jqplot.CanvasAxisLabelRenderer
                        },
                        axes: {
                            xaxis: {
                                renderer: $.jqplot.DateAxisRenderer,
                                pad: 1.05,
                                tickOptions: {formatString: '%#d-%m'}
                            },
                            yaxis: {
                                pad: -1,
                                tickOptions: {
                                    formatString: '%#.1f h',
                                }
                            }
                        },
                        cursor: {
                            show: true,
                            zoom: true,
                            showTooltip: false
                        },
                        highlighter: {
                            show: true,
                            sizeAdjust: 10
                        },
                        series: [
                            {
                                label: '<s:message code="agile.timelogged"/>',
                                rendererOptions: {
                                    smooth: true
                                },
                                fill: true,
                                color: '#5FAB78'
                            }],
                        legend: {
                            show: true,
                            location: 's',
                            placement: 'outsideGrid'
                        }
                    });
                } else {
                    $("#timechart-div").hide();
                }

                //-----------------------------Add effects etc-------------------------------
                $('.a-tooltip').tooltip();
                $('body').scrollspy({
                    target: '#menu'
                });
                $('td[rel=popover]').popover({
                    html: true,
                    trigger: 'hover',
                    content: function () {
                        var account_name = $(this).data('account');
                        var account_email = $(this).data('account_email');
                        var account_img = $(this).data('account_img');
                        var img = '<img data-src="holder.js/30x30"	style="height: 30px; float: left; padding-right: 10px;"	src="' + account_img + '" />';
                        var html = '<div>' + img + account_name + '</div><div><a style="font-size: xx-small;float:right"href="mailto:"' + account_email + '">' + account_email + '</a>';
                        return html;
                    }
                });
            });
        }

        function getEventTypeMsg(type) {
            switch (type) {
                case "CLOSED":
                    return '<s:message code="task.state.closed"/>';
                case "REOPEN":
                    return '<s:message code="task.state.reopen"/>';
                case "LOG":
                    return '<s:message code="task.state.logged"/>';
                case "TASKSPRINTADD":
                    return '<s:message code="task.state.tasksprintadd"/>';
                case "TASKSPRINTREMOVE":
                    return '<s:message code="task.state.tasksprintremove"/>';
                case "ESTIMATE":
                    return '<s:message code="task.state.estimatechange"/>';
                default:
                    return 'not yet added ';
            }
        }
    });
</script>
