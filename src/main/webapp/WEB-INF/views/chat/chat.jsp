<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<security:authentication property="principal" var="user"/>
<script src="<c:url value="/resources/js/sockjs.min.js" />"></script>
<script src="<c:url value="/resources/js/stomp.min.js" />"></script>
<div class="row">
    <div class="col-md-12">
        <h3><a href="<c:url value="/project/${chatProject.projectId}"/>">[${chatProject.projectId}]
            ${chatProject.name}</a> chat</h3>
    </div>
</div>
<div class="row">
    <%--this can be manipulated for smaller chat--%>
    <div class="col-sm-12 col-md-8">
        <div>
            <div class="row">
                <div class="col-sm-12 chat-input">
                    <form class="form-inline">
                        <div class="form-group" style="width: 75%">
                            <input type="text" id="message" class="form-control" placeholder="Message..."
                                   style="width: 90%">
                        </div>
                        <button id="send" class="btn btn-default" type="submit">Send</button>
                    </form>
                </div>
            </div>
            <div class="row margintop_5" style="padding-bottom: 10px">
                <div class="col-md-12">
                    <div id="messages-tab">
                    </div>
                    <a class="anchor" id="chat-bottom"></a>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    username = "${user.username}";
    var stompClient = null;
    var avatarURL = '<c:url value="/../avatar/"/>';
    var accountURL = '<c:url value="/user/"/>';
    var messagesURL = '<c:url value="/${chatProject.projectId}/chat/projectmessages"/>';
    var project = "${chatProject.projectId}";
    var loading_indicator = '<div id="loading" class="centerPadded"><i class="fa fa-cog fa-spin"></i> <s:message code="main.loading"/><br><img src="<c:url value="/resources/img/loading.gif"/>"></img></td>';

    function connect() {
        var socket = new SockJS('/gs-guide-websocket');
        stompClient = Stomp.over(socket);
        stompClient.debug = null
        stompClient.connect({}, function (frame) {
            //console.log('Connected: ' + frame);
            stompClient.subscribe('<c:url value="/chat/${chatProject.projectId}/messages"/>', function (message) {
                showMessage(JSON.parse(message.body));
            });
            sendMessage("${user} connected");
        });
    }

    function disconnect() {
        if (stompClient !== null) {
            sendMessage("${user} disconnected");
            stompClient.disconnect();
        }
    }

    function sendMessage(message, username) {
        stompClient.send("<c:url value="/chat/${chatProject.projectId}/send"/>", {}, JSON.stringify({
            'message': message,
            'username': username
        }));
    }

    function showMessage(message) {
        if (message.account !== null) {
            var avatar = '<img data-src="holder.js/30x30" class="avatar small hidden-xs hidden-sm" src="' + avatarURL + message.account.id + '.png"/>';
            var account = '<a href="' + accountURL + message.account.username + '">' + message.account.name + " " + message.account.surname + '</a>';
            if (message.account.username !== "${user.username}") {
                $("#messages-tab").append("<div class='row margintop_5'>" +
                    "<div class='col-md-9'>" +
                    "<div class='chat-bubble '>" +
                    "<div class='time-div'>" + message.time + "</div>" +
                    "<div>" + message.message + "</div>" +
                    "</div>" +
                    "</div>" +
                    "<div class='col-md-3'>" + avatar + account + "</div>" +
                    "</div>");
            } else {
                $("#messages-tab").append("<div class='row margintop_5'>" +
                    "<div class='col-md-3'>" + avatar + account + "</div>" +
                    "<div class='col-md-9'>" +
                    "<div class='chat-bubble-user'>" +
                    "<div class='time-div'>" + message.time + "</div>" +
                    "<div>" + message.message + "</div>" +
                    "</div>" +
                    "</div></div>");
            }
        } else {
            $("#messages-tab").append("<div style='font-style: italic;'>" + message.message + "</div>");
        }
        $(document.body).scrollTop($('#chat-bottom').offset().top);
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
            if ($("#message").val() !== '') {
                sendMessage($("#message").val(), username);
                $("#message").val('')
            }

        });
    });
    connect();
    readProjectMessages()
</script>