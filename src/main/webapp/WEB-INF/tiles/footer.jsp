<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<security:authentication property="principal" var="user"/>
<security:authorize access="isAuthenticated()">
    <c:set var="active_task_seconds">${user.activeTaskSeconds}</c:set>
    <c:if test="${not empty user.activeTask }">
        <div class="row hidden-print footer-time">
            <div>
                <s:message code="task.active"/>
                <a class="a-tooltip"
                   href="<c:url value="/task/${user.activeTask}"/>"
                   data-html="true">${user.activeTask}</a>
                <a class="btn btn-default btn-xxs a-tooltip handleTimerBtn"
                   title="<s:message code="task.stopTime.description"/>">
                    <i class="fa fa-lg fa-clock-o"></i>
                </a> <span id="activeTaskTimer" class="timer"></span>
            </div>
        </div>
    </c:if>
    <jsp:include page="../views/modals/users.jsp"/>
    <c:if test="${not empty user.activeTask }">
        <script>
            $(".handleTimerBtn").click(function () {
                var message = '<s:message code="error.longerThanDay"  htmlEscape="false"/>';
                var lang = "${pageContext.response.locale}";
                bootbox.setDefaults({
                    locale: lang
                });
                var url = '<c:url value="/task/time?id=${user.activeTask}&action="/>';
                //check if greater that 1d
                var days = $("#activeTaskTimer").attr('days');
                if (days > 0) {
                    bootbox.alert(message, function () {
                        url += 'cancel';
                        window.location.href = url;
                    });
                } else {
                    url += 'stop';
                    window.location.href = url;
                }
            });
        </script>
    </c:if>
</security:authorize>
<script>
    var cache = {};
    $(function () {
        $('.a-tooltip').tooltip();
    });
    //add slidedown animation to dropdown menus
    $('.dropdown').on('show.bs.dropdown', function (e) {
        $(this).find('.dropdown-menu').first().stop(true, true).slideDown();
    });

    //add slideup animation to dropdown menus
    $('.dropdown').on('hide.bs.dropdown', function (e) {
        $(this).find('.dropdown-menu').first().stop(true, true).slideUp();
    });

    //In case of session timeout or general server error.
    //But don't catch SyntaxError as most of api replays are not json parsed
    $(document).ajaxError(
            function (event, jqxhr, settings, thrownError) {
                if (thrownError.message != "Unexpected end of input"
                        && thrownError.__proto__.name != 'SyntaxError') {
                    console.log(thrownError);
                    var message = '<s:message code="error.session"/>';
                    showError(message);
                    var url = '<c:url value="/"/>';
//                    window.location.href = url;
                }
            });
    $("#searchField").autocomplete({
        appendTo: ".container",
        source: function (request, response) {
            $(".ui-menu").hide();
            var term = request.term;
            if (term in cache) {
                response(cache[term]);
                return;
            }
            $("#tagsLoading").show();
            var url = '<c:url value="/getTags"/>';
            $.get(url, {
                term: term
            }, function (data) {
                $("#searchField").autocomplete("widget").show();
                //$(".ui-menu").show();
                $("#tagsLoading").hide();
                var results = [];
                $.each(data, function (i, item) {
                    var itemToAdd = {
                        value: item.name,
                        label: item.name
                    };
                    results.push(itemToAdd);
                });
                cache[term] = results;
                return response(results);
            });
        },
        open: function (e, ui) {
            var termTemplate = "<span class='ui-autocomplete-term'>%s</span>";
            var acData = $(this).data('uiAutocomplete');
            var styledTerm = termTemplate.replace('%s', acData.term);
            acData.menu.element.find('a').each(function () {
                var me = $(this);
                var keywords = acData.term.split(' ').join('|');
                me.html(me.text().replace(new RegExp("(" + keywords + ")", "gi"), '<b>$1</b>'));
            });
        },
        select: function (event, ui) {
            $("#searchField").val(ui.item.value);
            $("#searchForm").submit();
        }
    });

    $(document).ready(
            function ($) {
                var task_start_time = "${active_task_seconds}";
                var startTime = new Date(0);
                startTime.setUTCSeconds(task_start_time);
                <%-- only start timer if there is active task--%>
                <c:if test="${active_task_seconds gt 0}">
                setTimeout(display, 1000);
                </c:if>

                function display() {
                    var endTime = new Date();
                    var timeDiff = endTime - startTime;
                    timeDiff /= 1000;
                    var seconds = Math.round(timeDiff % 60);
                    timeDiff = Math.floor(timeDiff / 60);
                    var minutes = Math.round(timeDiff % 60);
                    timeDiff = Math.floor(timeDiff / 60);
                    var hours = Math.round(timeDiff % 8);
                    timeDiff = Math.floor(timeDiff / 8);
                    var days = timeDiff;
                    var currentTime = days + "d " + hours + "h " + minutes + "m "
                            + seconds + "s";
                    $("#activeTaskTimer").text(currentTime);
                    $("#task_timer").html(currentTime);

                    if (days > 0) {
                        $("#activeTaskTimer").attr('days', days);
                    }
                    setTimeout(display, 1000);
                }
            });
    //toggle sidebars
    function saveSmallSidebarSize(enable) {
        var url = '<c:url value="/sidebar"/>';
        $.get(url, {enable: enable}, function (result) {
        });
    }
</script>