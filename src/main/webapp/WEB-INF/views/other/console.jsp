<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>

<div class="white-frame"
	style="width: 80%; overflow: auto; display: table">
	<div style="display: table-caption; margin-left: 10px;">
		<ul class="nav nav-tabs" style="border-bottom: 0">
			<li class="active"><a style="color: black" href="#"><span
					class="fa-stack"> <i class="fa fa-square fa-stack-2x"></i>
						<i class="fa fa-terminal fa-stack-1x fa-inverse"></i>
				</span> Console</a></li>
		</ul>
	</div>
	<div>
		<samp> ${console} </samp>
		<span class="btn btn-default pull-right"
			onclick="location.href='<c:url value="/"/>';">Ok</span>
	</div>
</div>
