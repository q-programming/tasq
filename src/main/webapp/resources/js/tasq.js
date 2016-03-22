$(document).on("click", "area , a", function (e) {
    if ($($.attr(this, 'href')).offset()) {
        $('html, body').animate({
            scrollTop: $($.attr(this, 'href')).offset().top
        }, 500);
        return false;
    }
});

function showError(message) {
    var errorMsg = '<div class="alert alert-danger fade in alert-overlay">'
        + '<button type="button" class="close" data-dismiss="alert">&times;</button>'
        + message
        + '</div>';
    $('#messages_div').append(errorMsg);
};

function showSuccess(message) {
    var successMsg = '<div class="alert alert-success fade in alert-overlay">'
        + '<button type="button" class="close" data-dismiss="alert">&times;</button>'
        + message
        + '</div>';
    $('#messages_div').append(successMsg);
    $(".alert").alert();
    window.setTimeout(function () {
            $(".alert-success").alert('close');
            $(".alert-info").alert('close');
        }
        , 10000);
};

function showWarning(message) {
    var warningMsg = '<div class="alert alert-warning fade in alert-overlay">'
        + '<button type="button" class="close" data-dismiss="alert">&times;</button>'
        + message
        + '</div>';
    $('#messages_div').append(warningMsg);
    $(".alert").alert();
    window.setTimeout(function () {
        $(".alert-warning").alert('close');
    }, 15000);
};

$(".alert").alert();
window.setTimeout(function () {
        $(".alert-success").alert('close');
        $(".alert-info").alert('close');
    }
    , 10000);
window.setTimeout(function () {
    $(".alert-warning").alert('close');
}, 15000);

function showWait(show) {
    if (show) {
        $("body").css("cursor", "wait");
    } else {
        $("body").css("cursor", "auto");
    }
}
function IsEmail(email) {
    var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    return regex.test(email);
}

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

$(document).on("click", ".toggler", function (e) {
    var target = $(this).data('tab');
    $(this).nextAll(".mod-header-title-txt").toggleClass('closed');
    $('#' + target).toggle("blind", 500);
    $(this).toggleClass('closed');
    $(this).toggleClass('fa-caret-down');
    $(this).toggleClass('fa-caret-right');
});

$(document).on("click", ".menu-toggle", function (e) {
    var targetClass = "." + $(this).data('type');
    $(targetClass).toggle("blind");
    var indicator = $(this).find(".menu-indicator");
    indicator.toggleClass('fa-toggle-down');
    indicator.toggleClass('fa-toggle-right');
});

function getTaskType(type) {
    switch (type) {
        case "TASK":
            var type = '<i class="fa fa-lg fa-fw fa-check-square"></i> ';
            return type;
        case "USER_STORY":
            var type = '<i class="fa fa-lg fa-fw fa-lightbulb-o"></i> ';
            return type;
        case "ISSUE":
            var type = '<i class="fa fa-lg fa-fw fa-exclamation-triangle"></i> ';
            return type;
        case "BUG":
            var type = '<i class="fa fa-lg fa-fw fa-bug"></i> ';
            return type;
        case "IDLE":
            var type = '<i class="fa fa-lg fa-fw fa-coffee"></i> ';
            return type;
        case "SUBTASK":
            var type = '<i class="fa fa-lg fa-fw fa-check-circle-o"></i> ';
            return type;
        case "SUBBUG":
            var type = '<i class="fa fa-lg fa-fw fa-bug"></i> ';
            return type;
        default:
            return 'not yet added ';
    }
    ;
}
var currentTag;
$(document).on("click",".tag_filter" ,function () {
    //first show all
    $(".agile-card,.agile-list").each(function () {
        $(this).show();
    });
    $(".tag_filter").each(function () {
        $(this).removeClass("not_selected");
    });
    var tag = $(this).data("name");
    if (tag != currentTag) {
        $(".agile-card,.agile-list").each(function () {
            var ids = $(this).data("id");
            var tags = $(this).data("tags").split(",");
            if (!($.inArray(tag, tags) >= 0)) {
                $(this).hide();
            }
        });
        $('.tag_filter').not(this).each(function () {
            $(this).addClass("not_selected");
        });
        currentTag = tag;
    } else {
        currentTag = "";
    }
});