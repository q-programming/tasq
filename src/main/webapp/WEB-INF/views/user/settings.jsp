<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script src="<c:url value="/resources/js/bootstrap.file-input.js"/>"></script>
<div class="row">
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
                    <div class="col-md-8">
                        <h3>
                            <c:if test="${not user.confirmed}">
                                <i style="color: red"
                                   class="fa fa-exclamation-triangle a-tooltip"
                                   title="<s:message code="panel.emails.notconfirmed"/>"></i>
                            </c:if>
                            ${user}
                        </h3>
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
                        </div>
                        <div id="notConfirmed" class="col-md-4 col-sm-12" <c:if
                                test="${user.confirmed}"> style="display:none" </c:if>>
                            <span style="color: red"> <s:message code="panel.emails.notconfirmed"/>&nbsp;
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
            <div style="text-align: center;">
                <button class="btn btn-success" type="submit">
                    <i class="fa fa-floppy-o"></i>&nbsp;
                    <s:message code="panel.save" text="Save settings"/>
                </button>
            </div>
        </form>
        <jsp:include page="../other/invite.jsp"/>
    </div>
</div>
<script>
    var settings = true;
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
                    if (this.width > 150 || this.height > 150) {
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