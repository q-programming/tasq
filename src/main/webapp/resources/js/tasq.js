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
}

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
/**
 * @return {boolean}
 */
function IsEmail(email) {
    var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    return regex.test(email);
}

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

$(document).on("click", ".toggler", function () {
    var target = $(this).data('tab');
    $(this).nextAll(".mod-header-title-txt").toggleClass('closed');
    $('#' + target).toggle("blind", 500);
    $(this).toggleClass('closed');
    $(this).toggleClass('fa-caret-down');
    $(this).toggleClass('fa-caret-right');
});

$(document).on("click", ".menu-toggle", function () {
    var targetClass = "." + $(this).data('type');
    $(targetClass).toggle("blind");
    var indicator = $(this).find(".menu-indicator");
    indicator.toggleClass('fa-toggle-down');
    indicator.toggleClass('fa-toggle-right');
});
$(document).on("click", "#small-sidebar-show", function (e) {
    e.preventDefault();
    $("#sidebar-div").hide("slide");
    $(".main").toggleClass("col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2").toggleClass("col-sm-12 col-md-12 big");
    $("#small-sidebar-div").show("slide");
    saveSmallSidebarSize(true);
});
$(document).on("click", "#sidebar-show", function (e) {
    e.preventDefault();
    $("#sidebar-div").show("slide");
    $(".main").toggleClass("col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2").toggleClass("col-sm-12 col-md-12 big");
    $("#small-sidebar-div").hide("slide");
    saveSmallSidebarSize(false);
});


function getTaskType(type) {
    switch (type) {
        case "TASK":
            type = '<i class="fa fa-lg fa-fw fa-check-square"></i> ';
            return type;
        case "USER_STORY":
            type = '<i class="fa fa-lg fa-fw fa-lightbulb-o"></i> ';
            return type;
        case "ISSUE":
            type = '<i class="fa fa-lg fa-fw fa-exclamation-triangle"></i> ';
            return type;
        case "BUG":
            type = '<i class="fa fa-lg fa-fw fa-bug"></i> ';
            return type;
        case "IDLE":
            type = '<i class="fa fa-lg fa-fw fa-coffee"></i> ';
            return type;
        case "SUBTASK":
            type = '<i class="fa fa-lg fa-fw fa-check-circle-o"></i> ';
            return type;
        case "SUBBUG":
            type = '<i class="fa fa-lg fa-fw fa-bug"></i> ';
            return type;
        case "CHANGE_REQUEST":
            type = '<i class="fa fa-lg fa-fw fa-magic"></i> ';
            return type;
        default:
            return 'not yet added ';
    }
    ;
}
var currentAccount = -1;
$(document).on("click", ".avatar.small.member", function () {
    var account = $(this).data('account');
    //first show all
    $(".agile-card,.agile-list").each(function () {
        $(this).show();
    });
    currentTag = "";
    $(".avatar.small.member").each(function () {
        $(this).removeClass("not_selected");
    });
    $(".tag_filter").each(function () {
        $(this).removeClass("not_selected");
    });

    if (account !== currentAccount) {
        $(".agile-card,.agile-list").each(function () {
            var assignee = $(this).data('assignee');
            if (assignee !== account) {
                $(this).hide();
            }
        });
        $('.avatar.small.member').not(this).each(function () {
            $(this).addClass("not_selected");
        });
        currentAccount = account;
    } else {
        currentAccount = "";
    }
})


var currentTag;
$(document).on("click", ".tag_filter", function () {
    //first show all
    $(".agile-card,.agile-list").each(function () {
        $(this).show();
    });
    currentAccount = "";
    //reset SP to original value
    $(".total-points").each(function () {
        $(this).text($(this).attr("data-count"));
    })
    $(".tag_filter").each(function () {
        $(this).removeClass("not_selected");
    });
    $(".avatar.small.member").each(function () {
        $(this).removeClass("not_selected");
    });
    var tag = $(this).data("name");
    if (tag != currentTag) {
        $(".total-points").text("0");
        $(".agile-card,.agile-list").each(function () {
            var sprintId = $(this).attr('sprint-id');
            var tags = $(this).data("tags").split(",");
            if (!($.inArray(tag, tags) >= 0)) {
                $(this).hide();
            } else {
                var points_span = $(this).find(".points-div > .point-value")
                if (points_span) {
                    if (sprintId) {
                        var points = parseInt(points_span.attr('data-points'));
                        if (!isNaN(points)) {
                            var curr_value = parseInt($("#sprint_points_" + sprintId).text());
                            $("#sprint_points_" + sprintId).text(curr_value + points)
                        }
                    }
                }
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

function addMessagesEvents() {
    var minimized_elements = $('div.quote > table.worklog_table > tbody > tr > td');

    minimized_elements.each(function () {
        var t = $(this).text();
        if (t.length < 500) {
            return;
        }

        $(this).html(
            t.slice(0, 500) + '<span>... </span>&nbsp;<a href="#" class="more-info"><i class="fa fa-caret-square-o-down" aria-hidden="true"></i></a>' +
            '<span style="display:none;">' + t.slice(500, t.length) + '&nbsp;<a href="#" class="less-info"><i class="fa fa-caret-square-o-up" aria-hidden="true"></i></a></span>'
        );

    });

    $('a.more-info', minimized_elements).click(function (event) {
        event.preventDefault();
        $(this).hide().prev().hide();
        $(this).next().show();
    });

    $('a.less-info', minimized_elements).click(function (event) {
        event.preventDefault();
        $(this).parent().hide().prev().show().prev().show();
    });
}
function detectmob() {
    return window.innerWidth <= 800;
}

function fixOldTables() {
    var oldTables = $('table.worklog_table');
    oldTables.each(function () {
        $(this).addClass("table");
    })
}

function getURLAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/'));
    pathName = pathName.substring(0, pathName.lastIndexOf('/'));
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}
