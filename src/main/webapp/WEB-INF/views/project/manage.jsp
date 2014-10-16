<%@page import="com.qprogramming.tasq.task.TaskPriority"%>
<%@page import="com.qprogramming.tasq.task.TaskType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="myfn" uri="/WEB-INF/tags/custom.tld"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<security:authorize access="hasRole('ROLE_ADMIN')">
	<c:set var="is_admin" value="true" />
</security:authorize>
<security:authentication property="principal" var="user" />
<div class="white-frame" style="overflow: auto;">
	<h3>
		<a href="<c:url value="/project?id=${project.id}"/>">[${project.projectId}]
			${project.name}</a>
	</h3>
	<form id="priority_form"
		action="<c:url value="/project/${project.id}/update"/>" method="post">
		<div>
			<div class="mod-header">
				<h4 class="mod-header-title">
					<s:message code="project.manage.defaults" />
				</h4>
			</div>
				<div style="display:table-cell;padding-left: 40px;padding-bottom: 10px;">
					<h5>
						<b><s:message code="project.manage.timetrack" /></b>
					</h5>
					<select id="timeTracked" name="timeTracked" class="form-control" style="width:200px">
						<option value="true"  <c:if test="${project.timeTracked}">selected</c:if>><s:message code="project.manage.timetrack.time"/></option>
						<option value="false" <c:if test="${not project.timeTracked}">selected</c:if>><s:message code="project.manage.timetrack.sp"/></option>
					</select>
					<span class="help-block"><s:message
							code="project.manage.timetrack.help" /></span>
				</div>
			<div style="padding-left: 40px;">
				<h5 class="mod-header-title">
					<s:message code="project.manage.priority" />
				</h5>
				<div class="dropdown">
					<%
						pageContext.setAttribute("priorities", TaskPriority.values());
					%>
					<button id="priority_button" class="btn btn-default " type="button"
						id="dropdownMenu2" data-toggle="dropdown">
						<div id="task_priority" class="image-combo">
							<t:priority priority="${project.default_priority}" />
						</div>
						<span class="caret"></span>
					</button>
					<ul class="dropdown-menu" role="menu"
						aria-labelledby="dropdownMenu2">
						<c:forEach items="${priorities}" var="enum_priority">
							<li><a tabindex="-1" href="#" id="${enum_priority}"><t:priority
										priority="${enum_priority}" /></a></li>
						</c:forEach>
					</ul>
					<span class="help-block"><s:message
							code="project.manage.priority.help" /></span> <input
						name="default_priority" id="default_priority" type="hidden"
						value="${project.default_priority}">
				</div>
			</div>
			<div style="padding-left: 40px;">
				<h5 class="mod-header-title">
					<s:message code="project.manage.type" />
				</h5>
				<div class="dropdown">
					<button id="type_button" class="btn btn-default "
						style="${type_error}" type="button" id="dropdownMenu1"
						data-toggle="dropdown">
						<div id="task_type" class="image-combo">
							<t:type type="${project.default_type}" show_text="true"
								list="true" />
						</div>
						<span class="caret"></span>
					</button>
					<ul class="dropdown-menu" role="menu"
						aria-labelledby="dropdownMenu1">
						<%
							pageContext.setAttribute("types", TaskType.values());
						%>
						<li>------</li>
						<c:forEach items="${types}" var="enum_type">
							<li><a tabindex="-1" href="#" id="${enum_type.code}"><t:type
										type="${enum_type}" show_text="true" list="true" /></a></li>
						</c:forEach>
					</ul>
					<span class="help-block"><s:message
							code="project.manage.type.help" /></span> <input name="default_type"
						id="default_type" type="hidden" value="${project.default_type}">
				</div>
			</div>
		</div>
	</form>
	<%-----------ADMINS --%>
	<div class="mod-header">
		<h4 class="mod-header-title">
			<s:message code="project.admins" />
		</h4>
	</div>
	<table class="table">
		<c:forEach items="${project.administrators}" var="admin">
			<c:if test="${admin.id == user.id || is_admin}">
				<c:set var="can_edit" value="true" />
			</c:if>
			<tr>
				<td><img data-src="holder.js/30x30"
					style="height: 30px; float: left; padding-right: 10px;"
					src="<c:url value="/userAvatar/${admin.id}"/>" />${admin}</td>
			</tr>
		</c:forEach>
	</table>
	<div class="mod-header">
		<h4 class="mod-header-title">
			<s:message code="project.members" />
		</h4>
	</div>
	<div style="display: table-cell; padding-left: 20px;">
		<div id="add_button_div">
			<span class="btn btn-default btn-sm" id="add_button"><span
				class="glyphicon glyphicon-plus"></span> <s:message
					code="project.participant.new" /></span>
		</div>
		<div style="display: table-cell; padding-left: 20px; display: none"
			id="add_div">
			<div>
				<div class="input-group" style="width: 250px;">
					<input type="text" class="form-control input-sm"
						placeholder="<s:message code="project.participant.hint"/>"
						id="participant"> <span class="input-group-btn">
						<button type="button" id="dismiss_add" class="close"
							style="padding-left: 5px">×</button>
					</span>
				</div>
				<form id="added" action="<c:url value="/project/useradd"/>"
					method="post">
					<input type="hidden" name="id" value="${project.id}">
					<!-- 				<div id="added"></div> -->
				</form>
			</div>
		</div>
	</div>
	<%------------PARTICIPANTS --------------------------%>
	<table class="table">
		<c:forEach items="${project.participants}" var="participant">
			<tr>
				<td><img data-src="holder.js/30x30"
					style="height: 30px; float: left; padding-right: 10px;"
					src="<c:url value="/userAvatar/${participant.id}"/>" />${participant}
				</td>
				<td><c:if test="${can_edit}">
						<div class="pull-right">
							<div class="pull-right">
								<form action="<c:url value="/project/userRemove"/>"
									method="post">
									<input type="hidden" name="project_id" value="${project.id}">
									<input type="hidden" name="account_id"
										value="${participant.id}">
									<button type="submit" class="btn btn-default btn-sm a-tooltip "
										title="<s:message code="project.participant.remove"/>">
										<span class="glyphicon glyphicon-remove"></span>
									</button>
								</form>
							</div>
							<c:if test="${myfn:contains(project.administrators,participant)}">
								<div class="pull-right">
									<form action="<c:url value="/project/removeAdmin"/>"
										method="post">
										<input type="hidden" name="project_id" value="${project.id}">
										<input type="hidden" name="account_id"
											value="${participant.id}">
										<button type="submit"
											class="btn btn-default btn-sm a-tooltip "
											title="<s:message code="project.participant.admin.remove"/>">
											<span class="glyphicon glyphicon-minus"></span><span
												class="glyphicon glyphicon-wrench"></span>
										</button>
									</form>
								</div>
							</c:if>
							<c:if
								test="${not myfn:contains(project.administrators,participant)}">
								<div class="pull-right">
									<form action="<c:url value="/project/grantAdmin"/>"
										method="post">
										<input type="hidden" name="project_id" value="${project.id}">
										<input type="hidden" name="account_id"
											value="${participant.id}">
										<button type="submit"
											class="btn btn-default btn-sm a-tooltip "
											title="<s:message code="project.participant.admin.add"/>">
											<span class="glyphicon glyphicon-plus"></span><span
												class="glyphicon glyphicon-wrench"></span>
										</button>
									</form>
								</div>
							</c:if>
						</div>
					</c:if></td>
			</tr>
		</c:forEach>
	</table>
</div>
<script>
$(document).ready(function($) {
	<c:forEach items="${types}" var="enum_type">
		$("#${enum_type.code}").click(function() {
			$("#default_type").val("${enum_type}");
			$("#priority_form").submit();
		});
	</c:forEach>

	<c:forEach items="${priorities}" var="enum_priority">
	$("#${enum_priority}").click(function() {
		$("#default_priority").val("${enum_priority}");
		$("#priority_form").submit();
	});	
	</c:forEach>
	$("#timeTracked").change(function() {
		$("#priority_form").submit();
	});	
	
	$("#participant").autocomplete({
        minLength: 1,
        delay: 500,
        //define callback to format results
        source: function (request, response) {
        	
            $.getJSON("<c:url value="/getAccounts"/>", request, function(result) {
                response($.map(result, function(item) {
                    return {
                        // following property gets displayed in drop down
                        label: item.name + " " + item.surname,
                        value: item.email,
                    }
                }));
            });
        },
        //define select handler
        select : function(event, ui) {
            if (ui.item) {
                event.preventDefault();
                $("#participant").val(ui.item.label);
                $("#added").append('<input type="hidden" name="email" value=' + ui.item.value + '>');
                $("#added").submit();
                return false;
            }
        }
    });
	$("#add_button").click(function(){
		$('#add_button_div').toggle();
		$('#add_div').toggle("slide");
		$('#participant').focus();
		});
	$("#dismiss_add").click(function(){
		$('#add_div').toggle();
		$('#add_button_div').toggle("slide");
		});
	
});
</script>