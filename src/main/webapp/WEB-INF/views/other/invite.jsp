<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<div>
    <div class="mod-header">
        <h5 class="mod-header-title">
            <i class="fa fa-users"></i>
            <s:message code="panel.invite"/>
        </h5>
    </div>
	<span style="padding-left: 20px" class="help-block"><s:message
            code="panel.invite.help"/></span>
    <form action="<c:url value="/inviteUsers"/>" id="invite">
        <div class="row">
            <div id="invite_div" class="col-sm-9">
                <input name="email" id="inviteField" class="form-control input-italic"
                       type="text" placeholder="<s:message code="panel.invite" />">
            </div>
            <div class="col-sm-2 text-center">
                <button id="inviteBtn" type="submit" class="btn btn-default"
                        disabled="disabled">
                    <i class="fa fa-user-plus"></i>
                    <s:message code="panel.invite"/>
                </button>
            </div>
        </div>
    </form>
</div>
<script>
    $("form#invite :input").each(function () {
        $('#inviteField').blur(function () {
            var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            if (regex.test(this.value)) {
                console.log("valid");
                $("#invite_div").removeClass('has-error').addClass('has-success');
                $("#inviteBtn").removeAttr("disabled");
            } else {
                $("#invite_div").addClass('has-error').removeClass('has-success');
                $("#inviteBtn").attr("disabled", 'disabled');
            }
        });
    });
</script>