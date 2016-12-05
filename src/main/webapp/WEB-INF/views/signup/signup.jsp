<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script src="<c:url value="/resources/js/pwstrength-bootstrap.js"/>"></script>
<c:set var="name_txt">
    <s:message code="signup.name"/>
</c:set>
<c:set var="surname_txt">
    <s:message code="signup.surname"/>
</c:set>
<c:set var="email_txt">
    <s:message code="signup.email"/>
</c:set>
<c:set var="username_txt">
    <s:message code="signup.username"/>
</c:set>

<c:set var="password_txt">
    <s:message code="signup.password"/>
</c:set>
<c:set var="confirmPassword_txt">
    <s:message code="signup.confirmPassword"/>
</c:set>
<!-- UNCOMMENT IF YOU DON'T HAVE SMTP -->
<!-- <p style="text-align: center;border-radius: 5px;background: papayawhip;padding: 10px;width: 700px; margin: 0 auto 20px auto;"> -->
<!-- <i class="fa fa-lg fa-exclamation-circle"></i>&nbsp;Please note that confirmation mail sending is currently disabled and you will have to be manually confirmed by administrator &nbsp; -->
<!-- </p> -->
<form:form class="form-narrow form-horizontal" method="post"
           modelAttribute="signupForm">
    <fieldset>
        <legend>
            <s:message code="signup.header"/>
        </legend>
        <form:errors path="" element="p" class="text-danger"/>
        <div class="form-group">
            <label for="firstname" class="col-lg-3 control-label">${name_txt}</label>
            <div class="col-lg-9">
                <form:input path="firstname" class="form-control"
                            cssErrorClass="form-control" id="firstname" placeholder="${name_txt}"/>
                <form:errors path="firstname" element="p" class="text-danger"/>
                <i class="fa fa-check inputcheck valid" style="display:none"></i>
                <i class="fa fa-times inputcheck invalid" style="display: none;"></i>
            </div>
        </div>
        <div class="form-group">
            <label for="surname" class="col-lg-3 control-label">${surname_txt}</label>
            <div class="col-lg-9">
                <form:input path="surname" class="form-control"
                            cssErrorClass="form-control" id="surname"
                            placeholder="${surname_txt}"/>
                <form:errors path="surname" element="p" class="text-danger"/>
                <i class="fa fa-check inputcheck valid" style="display:none"></i>
                <i class="fa fa-times inputcheck invalid" style="display: none;"></i>
            </div>
        </div>

        <div class="form-group has-feedback" id="emailDiv">
            <label for="email" class="col-lg-3 control-label">E-mail</label>
            <div class="col-lg-9">
                <form:input path="email" class="form-control"
                            cssErrorClass="form-control" id="email" placeholder="${email_txt}"/>
                <form:errors path="email" element="p" class="text-danger"/>
                <i class="fa fa-check inputcheck valid" style="display:none"></i>
                <i class="fa fa-times inputcheck invalid" style="display: none;"></i>
            </div>
        </div>
        <div class="form-group has-feedback">
            <label for="username" class="col-lg-3 control-label">${username_txt}</label>
            <div class="col-lg-9">
                <form:input path="username" class="form-control"
                            cssErrorClass="form-control" id="username" placeholder="${username_txt}"/>
                <form:errors path="username" element="p" class="text-danger"/>
                <i class="fa fa-check inputcheck valid" style="display:none"></i>
                <i class="fa fa-times inputcheck invalid" style="display: none;"></i>
            </div>
        </div>
        <div class="form-group">
            <label for="password" class="col-lg-3 control-label">${password_txt}</label>
            <div class="col-lg-9">
                <form:password path="password" class="form-control" id="password"
                               placeholder="${password_txt}"/>
                <form:errors path="password" element="p" class="text-danger"/>
                <i class="fa fa-check inputcheck valid" style="display:none"></i>
                <i class="fa fa-times inputcheck invalid" style="display: none;"></i>
            </div>
        </div>
        <div id="pwdContainer" class="form-group">
            <div class="col-lg-offset-3 col-lg-9 ">
                <div class="passwordStrength a-tooltip" title="<s:message code="signup.password.strength"/> "></div>
                <span class="help-block"><s:message code="signup.password.strength.hint"/>&nbsp;</span>
            </div>

        </div>
        <div class="form-group">
            <label for="confirmPassword" class="col-lg-3 control-label">${confirmPassword_txt}</label>
            <div class="col-lg-9">
                <form:password path="confirmPassword" class="form-control"
                               placeholder="${confirmPassword_txt}"/>
                <form:errors path="confirmPassword" element="p" class="text-danger"/>
                <i class="fa fa-check inputcheck valid" style="display:none"></i>
                <i class="fa fa-times inputcheck invalid" style="display: none;"></i>
            </div>
        </div>

        <div class="form-group">
            <div class="col-lg-offset-3 col-lg-9">
                <button id="submitbtn" type="submit" class="btn btn-default" disabled>
                    <s:message code="signup.signup" text="Sign up"/>
                </button>
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-10">
                <p>
                    <s:message code="signup.existing"/>&nbsp;
                    <a href='<s:url value="/signin"/>'><s:message
                            code="menu.signin" text="Sign in"/></a>
                </p>
            </div>
        </div>
    </fieldset>
</form:form>
<script>

    $("form#signupForm :input").each(function () {
        $('#firstname').blur(function () {
            if ($(this).val() == "") {
                invalid($(this));
            } else {
                valid($(this));
            }
        });
        $('#surname').blur(function () {
            if ($(this).val() == "") {
                invalid($(this));
            } else {
                valid($(this));
            }
        });
        $('#username').blur(function () {
            if ($(this).val() == "") {
                invalid($(this));
            } else {
                valid($(this));
            }
        });
        $('#email').blur(function () {
            var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            if (regex.test(this.value)) {
                valid($(this));
                if ($('#username').val() == "") {
                    $('#username').val($(this).val().split("@")[0]);
                }
            } else {
                invalid($(this));
            }
        });
        $('#confirmPassword').blur(function () {
            if ($(this).val() != $('#password').val()) {
                invalid($(this));
            } else {
                valid($(this));
            }
        });
    });
    function invalid(field) {
        field.parent().parent().addClass('has-error');
        field.parent().parent().removeClass('has-success');
        field.siblings(".inputcheck.valid").hide();
        field.siblings(".inputcheck.invalid").show();
    }
    function valid(field) {
        field.parent().parent().addClass('has-success');
        field.parent().parent().removeClass('has-error');
        field.siblings(".inputcheck.valid").show();
        field.siblings(".inputcheck.invalid").hide();
    }
    var options = {};
    options.ui = {
        container: "#pwdContainer",
        showVerdictsInsideProgressBar: false,
        viewports: {
            progress: ".passwordStrength"
        },
        colorClasses: ["danger", "danger", "warning", "warning", "success", "success"]
    };
    options.common = {
        minChar: 8,
        onScore: function (options, word, score) {
            $('.a-tooltip').tooltip();
            if (score > 10) {
                $("#submitbtn").prop('disabled', false);
            } else {
                $("#submitbtn").prop('disabled', true);
            }
            return score;
        }
    };
    $("#password").pwstrength(options).pwstrength("ruleActive", "wordOneSpecialChar", false);
    $('.a-tooltip').tooltip();
</script>
