<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<div class="row margintop_10">
    <div class="white-frame-nomargin col-md-offset-3 col-md-6 col-sm-12">
        <h3>
            <s:message code="panel.settings"></s:message>
        </h3>
        <security:authentication property="principal" var="user"/>
        <form id="panelForm" name="panelForm" method="post"
              enctype="multipart/form-data">
            <div>
                <%--AVATAR--%>
                <div class="row">
                    <div class="col-md-4 col-sm-12 text-center">
                        <div id="avatar" style="border: 1px dashed; ">
                            <img id="avatar_src"
                                 src="<c:url value="/../avatar/${user.id}.png"/>"
                                 style="padding: 10px;border-radius:50%"/>
                            <div id="avatar_upload" class="hidden" style="margin-top: -30px">
                                <input id="file_upload" name="avatar" type="file" accept=".png"
                                       title="Change avatar" class="inputfiles">
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <input id="firstname" class="form-control" name="firstname" value="${user.name}">
                        </div>
                        <div class="form-group">
                            <input id="surname" class="form-control margintop_20" name="surname"
                                   value="${user.surname}">
                        </div>
                        <input id="current-password" type="hidden" name="password">
                        <%--<c:if test="${not user.confirmed}">--%>
                        <%--<i style="color: red"--%>
                        <%--class="fa fa-exclamation-triangle a-tooltip"--%>
                        <%--title="<s:message code="panel.emails.notconfirmed"/>"></i>--%>
                        <%--</c:if>--%>
                    </div>
                </div>
                <%--EMAIL--%>
                <div>
                    <div class="mod-header">
                        <h5 class="mod-header-title">
                            <i class="fa fa-envelope-o"></i>
                            <s:message code="panel.emails"/>
                        </h5>
                    </div>
                    <div class="row paddingleft_20">
                        <div id="email-group" class="form-group has-feedback col-md-8 col-sm-12">
                            <div class="input-group">
                                <span class="input-group-addon"><i class="fa fa-envelope-o"></i></span>
                                <input id="email" name="email" type="email" class="form-control" placeholder="e-mail"
                                       value="${user.email}">
                                <input id="useremail" type="hidden" value="${user.email}">
                            </div>
                            <c:if test="${not user.confirmed}">
                            <span style="color: red"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i>&nbsp;<s:message
                                    code="panel.emails.notconfirmed"/>
                            </c:if>
                        </div>
                        <div id="notConfirmed" class="col-md-4 col-sm-12" <c:if
                                test="${user.confirmed}"> style="display:none" </c:if>>
                            <a href="<c:url value="/emailResend"/>" class="btn btn-default">
                                <i class="fa fa-reply"></i><i class="fa fa-envelope"></i>&nbsp;
                                <s:message code="panel.emails.resend"/>
                            </a>
                            </span>
                        </div>
                    </div>
                    <div class="paddingleft_20">
                        <span class="help-block"><s:message
                                code="panel.emails.help"/></span>
                        <div class="checkbox padding-left5">
                            <label class="checkbox" style="display: inherit;"> <input
                                    type="checkbox" name="watched" id="watched" value="true"
                                    <c:if test="${user.watchnotification}">checked</c:if>>
                                <i class="fa fa-eye"></i> <s:message
                                        code="panel.emails.watched"/>
                            </label>
                        </div>
                        <div class="checkbox padding-left5">
                            <label class="checkbox" style="display: inherit;"> <input
                                    type="checkbox" name="comments" id="comments" value="true"
                                    <c:if test="${user.commentnotification}">checked</c:if>>
                                <i class="fa fa-comment"></i> <s:message
                                        code="panel.emails.comments"/>
                            </label>
                        </div>
                        <div class="checkbox padding-left5">
                            <label class="checkbox" style="display: inherit;"> <input
                                    type="checkbox" name="system" id="system" value="true"
                                    <c:if test="${user.systemnotification}">checked</c:if>>
                                <i class="fa fa-exclamation-triangle"></i> <s:message
                                        code="panel.emails.system"/>
                            </label>
                        </div>
                    </div>
                </div>
                <%--LANGUAGE--%>
                <div>
                    <div class="mod-header">
                        <h5 class="mod-header-title">
                            <i class="fa fa-globe"></i>
                            <s:message code="panel.language"/>
                        </h5>
                    </div>
                    <div class="row paddingleft_20">
                        <div class="form-group col-md-6">
                            <select class="form-control input-sm" name="language">
                                <option value="en"
                                        <c:if test="${user.language eq 'en'}">selected</c:if>><s:message
                                        code="lang.en" text="English"/></option>
                                <option value="pl"
                                        <c:if test="${user.language eq 'pl'}">selected</c:if>><s:message
                                        code="lang.pl" text="Polish"/></option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-12">
                    <span style="padding-left: 20px" class="help-block"><s:message
                            code="panel.language.help"/></span>
                    </div>
                </div>
                <div>
                    <div class="mod-header">
                        <h5 class="mod-header-title">
                            <i class="fa fa-paint-brush"></i>
                            <s:message code="panel.theme"/>
                        </h5>
                    </div>
                    <div class="row paddingleft_20">
                        <div class="col-md-6">
                            <select class="form-control input-sm" name="theme">
                                <c:forEach items="${themes}" var="theme">
                                    <option value="${theme.id}"
                                            <c:if test="${theme.name eq user.theme.name}">selected</c:if>>
                                            ${theme.name}</option>
                                </c:forEach>
                            </select> <span class="help-block"><s:message
                                code="panel.theme.help"/></span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="text-center">
                <span id="submit-settings" class="btn btn-success">
                    <i class="fa fa-floppy-o"></i>&nbsp;
                    <s:message code="panel.save" text="Save settings"/>
                </span>
            </div>
        </form>
        <jsp:include page="../other/invite.jsp"/>
        <div>
            <div class="mod-header">
                <h5 class="mod-header-title">
                    <i class="fa fa-lock" aria-hidden="true"></i>
                    <s:message code="signup.password"/>
                </h5>
            </div>
            <span style="padding-left: 20px" class="help-block"><s:message
                    code="signin.password.reset.help" htmlEscape="false"/></span>
            <div class="text-center">
                <a href="<c:url value="/sendResetPassword"/>" class="btn btn-default">
                    <i class="fa fa-envelope-o"></i>
                    <s:message code="signin.password.reset"/>
                </a>
            </div>
        </div>
    </div>
</div>
<div id="password-confirm-modal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-sm" role="document">
        <div class="modal-content">
            <div class="modal-header theme">
                <h4 class="modal-title" id="closeDialogTitle">
                    <i class="fa fa-lock" aria-hidden="true"></i>&nbsp;<s:message code="signup.confirmPassword"/>
                </h4>
            </div>
            <div class="modal-body">
                <div style="padding: 20px">
                    <p><s:message code="panel.vitals.changed"/></p>
                </div>
                <div class="row marginleft_0 marginright_0">
                    <div class="form-group col-md-7 col-sm-12">
                        <input class="form-control"
                               type="password" id="current-password-modal">
                    </div>
                    <div class="col-md-2 col-sm-12">
                        <span id="password-confirmed-btn" class="btn btn-success"><s:message code="signup.confirm"/></span>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>
<script>
    var settings = true;
    function passwordConfirmed() {
        $("#current-password").val($("#current-password-modal").val());
        $("#panelForm").submit();
    }
    $(document).ready(function ($) {
        $("#email").change(function () {
            var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            if (!regex.test(this.value)) {
                $("#email-group").removeClass('has-success');
                $("#email-group").addClass('has-error');
            } else {
                $("#email-group").removeClass('has-error');
                $("#email-group").addClass('has-success');
            }
        });


        var imageWRN = '<s:message code="error.file100kb"/>';
        $("#file_upload").bootstrapFileInput();

        $("#avatar").hover(function () {
            $('#avatar_upload').removeClass('hidden');
        }, function () {
            $('#avatar_upload').addClass('hidden');
        });

        $("#file_upload").change(function () {
            readURL(this);
        });
        //Validate all vital input and submit
        $("#submit-settings").click(function () {
            $(".form-group").removeClass("has-error");
            var firstname = $("#firstname").val();
            var surname = $("#surname").val();
            var email = $("#email").val();
            if (firstname == null || firstname == '') {
                invalid($("#firstname"));
            } else if (surname == null || surname == '') {
                invalid($("#surname"));
            } else if (email == null || email == '') {
                invalid($("#email"));
            } else {
                if (firstname != "${user.name}" || surname != "${user.surname}" || email != "${user.email}") {
                    $("#password-confirm-modal").modal({
                        show: true,
                        keyboard: false,
                        backdrop: 'static'
                    });
                } else {
                    //just submit form
                    $("#panelForm").submit();
                }
            }
        });

        function invalid(obj) {
            obj.parent().addClass('has-error');
        }

        $("#current-password-modal").keyup(function (e) {
            if (e.keyCode == 13) {
                passwordConfirmed();
            }
        });

        $("#password-confirmed-btn").click(function () {
            passwordConfirmed();
        });


        function readURL(input) {
            if (input.files && input.files[0]) {
                if (input.files[0].size > 100000) {
                    showError(imageWRN);
                    return false;
                }
                //check before uploading
                var _URL = window.URL || window.webkitURL;
                file = input.files[0];
                img = new Image();
                img.onload = function () {
                    if (this.width > 500 || this.height > 500) {
                        showError(imageWRN);
                    } else {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            $('#avatar_src').attr('src', e.target.result);
                        };
                        reader.readAsDataURL(file);
                    }
                };
                img.onerror = function () {
                    alert("not a valid file: " + file.type);
                };
                img.src = _URL.createObjectURL(file);

            }
        }
    });


</script>