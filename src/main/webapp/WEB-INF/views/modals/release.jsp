<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%---------------------RELEASE MODAL --%>
<div class="modal fade" id="releaseModal" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header theme">
				<button type="button" class="close theme-close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<i class="fa fa-clipboard"></i>&nbsp;
					<s:message code="agile.newRelease"></s:message>
				</h4>
			</div>
			<form id="mainForm" name="mainForm" method="post"
				action="<c:url value="/kanban/release"/>">
			<div class="modal-body">
					<input id="projectid" type="hidden" name="id"
						value="${project.projectId}">
					<div>
						<h4>
							<s:message code="agile.release.new.help" />
						</h4>
						<div>
							<label><s:message code="agile.release.name" /></label> <input
								id="release" name="release"
								style="width: 150px; height: 25px" class="form-control"
								type="text" value=""> <span class="help-block"><s:message
									code="agile.release.name.help" />&nbsp;<i
								class="fa fa-info-circle a-tooltip"
								title="<s:message
							code="agile.release.help" htmlEscape="false"/>"
								data-html="true"></i> </span>
						</div>
						<p id="errors" class="text-danger"></p>
						<div>
							<label><s:message code="comment.add" /></label>
							<textarea id="modal_comment" name="message" class="form-control"
								rows="3"></textarea>
						</div>
					</div>
			</div>
			<div class="modal-footer">
				<a class="btn" data-dismiss="modal"><s:message
						code="main.cancel" /></a>
				<button class="btn btn-default" type="submit">
					<s:message code="main.create" />
				</button>
			</div>
			</form>
		</div>
	</div>
</div>
<script>
	
</script>
