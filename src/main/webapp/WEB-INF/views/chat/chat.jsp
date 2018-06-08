<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<security:authentication property="principal" var="user"/>
<script src="<c:url value="/resources/js/sockjs.min.js" />"></script>
<script src="<c:url value="/resources/js/stomp.min.js" />"></script>
<div class="row">
    <%--this can be manipulated for smaller chat--%>
    <div class="col-md-offset-1 col-md-10 col-sm-12">
        <h3><a href="<c:url value="/project/${chatProject.projectId}"/>">[${chatProject.projectId}]
            ${chatProject.name}</a> chat</h3>
        <div class="row margintop_5" style="padding-bottom: 10px">
            <div class="col-md-12">
                <div id="messages-tab">
                </div>
                <a class="anchor" id="chat-bottom"></a>
            </div>
        </div>
    </div>
    <%--ACTIVE USERS--%>
    <div class="col-md-1 hidden-xs hidden-sm " style="position: fixed;right: 0">
        <h4><s:message code="chat.active.users"/></h4>
        <div id="chat-users">
            <c:forEach items="${chatProject.participants}" var="participant">
                <div class="chat-participant">
                    <img id="account-${participant.username}" data-src="holder.js/50x50"
                         class="avatar offline a-tooltip"
                         data-username="${participant.username}"
                         title="${participant}"
                         data-placement="bottom"
                         src="<c:url value="/../avatar/${participant.id}.png"/>"/>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
<div class=" chat-input">
    <div class="row" style="width: 100%">
        <form class="form-inline">
            <div class="form-group col-xs-8" style="margin-bottom: auto">
                <input type="text" id="message" class="form-control" placeholder="Message..." style="width: 100%"
                       autocomplete="off">
            </div>
            <button id="send" class="btn btn-default" type="submit"><s:message code="chat.send"/></button>
    </div>
</div>
<script>
    username = "${user.username}";
    var stompClient = null;
    var avatarURL = '<c:url value="/../avatar/"/>';
    var accountURL = '<c:url value="/user/"/>';
    var messagesURL = '<c:url value="/${chatProject.projectId}/chat/projectmessages"/>';
    var validateURL = '<c:url value="/chat/validate"/>';
    var project = "${chatProject.projectId}";
    var loading_indicator = '<div id="loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td>';

    const ERROR = 'ERROR';
    const ONLINE = 'ONLINE';
    const OFFLINE = 'OFFLINE';
    const MESSAGE = 'MESSAGE';

    function connect() {
        var socket = new SockJS('<c:url value="/chat-websocket/"/>');
        stompClient = Stomp.over(socket);
        stompClient.debug = null
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/chat-messages/${chatProject.projectId}', function (data) {
                var response = JSON.parse(data.body);
                if (response.event === ERROR) {
                    $("#messages-tab").append("<div style='font-style: italic;'>" + response.message + "</div>");
                } else {
                    if (response.event === ONLINE) {
                        userOnline(response);
                    } else if (response.event === OFFLINE) {
                        userOffline(response.user.username);
                    } else if (response.event === MESSAGE) {
                        userOnline(response);
                        showMessage(response);
                    }
                }
            });
            sendMessage("", "${user.username}", ONLINE);
        });
    }

    function disconnect() {
        if (stompClient !== null) {
            sendMessage("", "${user.username}", OFFLINE);
            stompClient.disconnect();
        }
    }

    function sendMessage(message, username, event) {
        stompClient.send("/chat/message/${chatProject.projectId}", {}, JSON.stringify({
            'message': message,
            'username': username,
            'project': project,
            'event': event
        }));
    }

    function showMessage(response) {
        if (response.user !== null) {
            var account = '<span class="chat-message-username" data-username="' + response.user.username + '"><a href="' + accountURL + response.user.username + '">' + response.user.name + " " + response.user.surname + '</a></span>';
            var avatar;
            if (response.user.username !== "${user.username}") {
                <%--var online = '<i class="fa fa-user a-tooltip" style="color:mediumseagreen; position: absolute;top: 20px;right: 10px;" title="<s:message code="main.online"/>"></i>';--%>
                avatar = '<img data-src="holder.js/30x30" class="avatar small hidden-xs hidden-sm pull-right" src="' + avatarURL + response.user.id + '.png"/>';
                $("#messages-tab").append("<div class='row margintop_5'>" +
                    "<div class='col-md-1'>" + avatar + "</div>" +
                    "<div class='col-md-10'>" +
                    "<div class='chat-bubble-user'>" + account +
                    "<div class='time-div'>" + response.time + "</div>" +
                    "<div>" + response.message + "</div>" +
                    "</div>" +
                    "</div>" +
                    "</div>");
            } else {
                avatar = '<img data-src="holder.js/30x30" class="avatar small hidden-xs hidden-sm pull-left" src="' + avatarURL + response.user.id + '.png"/>';
                $("#messages-tab").append("<div class='row margintop_5'>" +
                    "<div class='col-md-offset-1 col-md-10'>" +
                    "<div class='chat-bubble'>" + account +
                    "<div class='time-div'>" + response.time + "</div>" +
                    "<div>" + response.message + "</div>" +
                    "</div>" +
                    "</div>" +
                    "<div class='col-md-1'>" + avatar + "</div>" +
                    "</div>");
            }
        } else {
            $("#messages-tab").append("<div style='font-style: italic;'>" + response.message + "</div>");
        }
        $(document.body).scrollTop($('#chat-bottom').offset().top);
    }

    function userOnline(response) {
        var username = response.user.username
        var online_status = "<div class='online-status a-tooltip' title='<s:message code="chat.active"/><br>" + response.time + "' data-html='true' data-placement='bottom'></div>";
        var userIMG = $("#account-" + username);
        userIMG.removeClass("offline");
        if (userIMG.parent().find(".online-status").length === 0) {
            userIMG.parent().prepend(online_status);
        }
        userIMG.parent().hide().prependTo('#chat-users').fadeIn();
        //add mobile online indicators
        $(".mobile-online-status[data-username='" + username + "']").remove();
        $(".chat-message-username[data-username='" + username + "']").each(function () {
            var online = '<i class="fa fa-circle mobile-online-status visible-xs visible-sm" aria-hidden="true" data-username="' + username + '"></i>'
            $(this).append(online)
        });
        $(".a-tooltip").tooltip();
    }
    function userOffline(username) {
        var userIMG = $("#account-" + username);
        userIMG.addClass("offline");
        var onlineStatus = userIMG.parent().find(".online-status");
        if (onlineStatus) {
            onlineStatus.remove();
        }
        $(".mobile-online-status[data-username='" + username + "']").remove();
    }

    function readProjectMessages() {
        $("#messages-tab").append(loading_indicator);
        $.get(messagesURL, function (messages) {
            $("#loading").remove();
            $.each(messages, function (key, message) {
                showMessage(message);
            });
        });
    }

    $(function () {
        $("form").on('submit', function (e) {
            e.preventDefault();
        });
        $("#send").click(function () {
            var message = $("#message").val();
            if (message !== '') {
                if (message.length > 4000) {
                    var warning = "<s:message code="chat.error.lenght"/>";
                    showWarning(warning);
                } else {
                    $.get(validateURL, {message: message}, function (result) {
                        if (result.code === 'ERROR') {
                            showWarning(result.message);
                        } else {
                            sendMessage($("#message").val(), username, MESSAGE);
                            $("#message").val('')
                        }
                    });
                }
            }

        });
    });
    connect();
    readProjectMessages();
    $(window).bind('beforeunload', function () {
        disconnect();
    });

</script>