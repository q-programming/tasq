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
<script src="<c:url value="/resources/js/trumbowyg.min.js" />"></script>
<link href="<c:url value="/resources/css/trumbowyg.min.css" />" rel="stylesheet" media="screen" />
<security:authentication property="principal" var="user" />
<c:if test="${user.language ne 'en' }">
	<script src="<c:url value="/resources/js/trumbowyg.${user.language}.min.js" />"></script>
</c:if>
<div class="white-frame" style="overflow: auto;">
	<h2>
		<a href="<c:url value="/project?id=${project.id}"/>">[${project.projectId}]
			${project.name}</a>
			<a href="" class="showMore a-tooltip" title="<s:message code="project.manage.edit.description"/>" 
			data-toggle="modal" data-target="#editDescription" data-backdrop="static" data-keyboard="false">
				<i class="fa fa-pencil"></i>
			</a>
	</h2>
	
	<form id="priority_form"
		action="<c:url value="/project/${project.id}/update"/>" method="post">
		<div>
			<div class="mod-header">
				<h3 class="mod-header-title">
					<s:message code="project.manage.defaults" />
				</h3>
			</div>
			<div class="paddedleft40">
				<h5>
					<b><s:message code="project.manage.timetrack" /></b>
				</h5>
				<select id="timeTracked" name="timeTracked" class="form-control"
					style="width: 200px">
					<option value="true"
						<c:if test="${project.timeTracked}">selected</c:if>><s:message
							code="project.manage.timetrack.time" /></option>
					<option value="false"
						<c:if test="${not project.timeTracked}">selected</c:if>><s:message
							code="project.manage.timetrack.sp" /></option>
				</select> <span class="help-block"><s:message
						code="project.manage.timetrack.help" /></span>
			</div>
			<div class="paddedleft40">
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
			<div class="paddedleft40">
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
						<c:forEach items="${types}" var="enum_type">
							<c:if test="${not enum_type.subtask}">
								<li><a tabindex="-1" href="#" id="${enum_type.code}"><t:type
											type="${enum_type}" show_text="true" list="true" /></a></li>
							</c:if>
						</c:forEach>
					</ul>
					<span class="help-block"><s:message
							code="project.manage.type.help" /></span> <input name="default_type"
						id="default_type" type="hidden" value="${project.default_type}">
				</div>
			</div>
			<div class="paddedleft40">
				<h5>
					<b><s:message code="project.manage.assignee" /></b>
				</h5>
				<select id="defaultAssignes" name="defaultAssignee"
					class="form-control" style="width: 200px">
					<option value="-1"
						<c:if test="${empty project.defaultAssigneeID}"> selected</c:if>><s:message
							code="task.unassigned" /></option>
					<c:forEach items="${project.participants}" var="participant">
						<option value="${participant.id}"
							<c:if test="${participant.id eq project.defaultAssigneeID}">selected</c:if>>${participant}</option>
					</c:forEach>
				</select> <span class="help-block"><s:message
						code="project.manage.assignee.help" /></span>

			</div>
		</div>
	</form>
	<div class="mod-header">
		<h3 class="mod-header-title">
			<s:message code="task.people" />
		</h3>
	</div>
	<%-----------ADMINS --%>
	<div class="paddedleft40">
		<div class="mod-header">
			<h5 class="mod-header-title">
				<s:message code="project.admins" />
			</h5>
		</div>
		<table class="table">
			<c:forEach items="${project.administrators}" var="admin">
				<c:if test="${admin.id == user.id || is_admin}">
					<c:set var="can_edit" value="true" />
				</c:if>
				<tr>
					<td><img data-src="holder.js/30x30"
						style="height: 30px; float: left; padding-right: 10px;"
						src="<c:url value="/../avatar/${admin.id}.png"/>" />${admin}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div class="paddedleft40">
		<div class="mod-header">
			<h5 class="mod-header-title">
				<s:message code="project.members" />
			</h5>
		</div>
		<div style="display: table-row; padding-left: 20px;">
			<div id="add_button_div"
				style="display: table-cell; padding-left: 20px;">
				<span class="btn btn-default btn-sm a-tooltip"
					title="<s:message
					code="project.participant.new" />"
					id="add_button"><i class="fa fa-lg fa-user-plus"></i></span>
			</div>
			<div style="display: table-cell; padding-left: 20px; display: none"
				id="add_div">
				<div>
					<div class="input-group" style="width: 250px;">
						<input type="text" class="form-control input-sm"
							placeholder="<s:message code="project.participant.hint"/>"
							id="participant">
							<span class="input-group-btn">
							<i id="participantsLoader" class="fa fa-lg fa-cog fa-spin" style="display:none"></i>
							</span>
							 <span class="input-group-btn">
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
			<div style="display: table-cell; padding-left: 5px;">
				<a data-toggle="modal" data-target="#show_users"><span
					class="btn btn-default btn-sm a-tooltip"
					title="<s:message
					code="menu.users" />"><i class="fa fa-lg fa-users"></i></span></a>
			</div>
		</div>
	</div>
	<%------------PARTICIPANTS --------------------------%>
	<div class="paddedleft40">
		<table class="table">
			<c:forEach items="${project.participants}" var="participant">
				<tr>
					<td><img data-src="holder.js/30x30"
						style="height: 30px; float: left; padding-right: 10px;"
						src="<c:url value="/../avatar/${participant.id}"/>" />${participant}
						<span style="color: #737373">(<s:message
								code="${participant.role.code}" />)
					</span></td>
					<td><c:if test="${can_edit}">
							<div class="pull-right">
								<div class="pull-right">
									<form action="<c:url value="/project/userRemove"/>"
										method="post">
										<input type="hidden" name="project_id" value="${project.id}">
										<input type="hidden" name="account_id"
											value="${participant.id}">
										<button type="submit"
											class="btn btn-default btn-sm a-tooltip "
											title="<s:message code="project.participant.remove"/>">
											<i class="fa fa-user-times"></i>
										</button>
									</form>
								</div>
								<c:if
									test="${myfn:contains(project.administrators,participant)}">
									<div class="pull-right">
										<form action="<c:url value="/project/removeAdmin"/>"
											method="post">
											<input type="hidden" name="project_id" value="${project.id}">
											<input type="hidden" name="account_id"
												value="${participant.id}">
											<button type="submit"
												class="btn btn-default btn-sm a-tooltip "
												title="<s:message code="project.participant.admin.remove"/>">
												<i class="fa fa-minus"></i><i class="fa fa-wrench"></i>
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
												<i class="fa fa-plus"></i><i class="fa fa-wrench"></i>
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
</div>
<div class="modal fade" id="editDescription" role="dialog"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<form action="<c:url value="/project/${project.id}/description"/>" method="post">
				<div class="modal-header theme">
					<h4 class="modal-title" id="myModalLabel">
						[${project.projectId}] project.name
					</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<textarea name="description" id="projectDescription">${project.description}</textarea>
					</div>
				</div>
				<div class="modal-footer">
					<a class="btn" data-dismiss="modal"><s:message
								code="main.cancel" /></a>
					<button id="" class="btn btn-default" type="submit">
						<s:message code="main.edit" />
					</button>
					</div>
			</form>
		</div>
	</div>
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
	$("#timeTracked, #defaultAssignes").change(function() {
		$("#priority_form").submit();
	});
	
	var btnsGrps = jQuery.trumbowyg.btnsGrps;
	$('#projectDescription').trumbowyg({
		lang: '${user.language}',
		fullscreenable: false,
		btns: ['formatting',
	           '|', btnsGrps.design,
	           '|', 'link',
	           '|', 'insertImage',
	           '|', btnsGrps.justify,
	           '|', btnsGrps.lists]
	});
	
	$("#participant").autocomplete({
        minLength: 1,
        delay: 500,
        //define callback to format results
        source: function (request, response) {
        	$("#participantsLoader").show();
            $.getJSON("<c:url value="/getAccounts"/>", request, function(result) {
            	$("#participantsLoader").hide();
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