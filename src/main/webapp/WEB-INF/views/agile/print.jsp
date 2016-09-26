<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<div class="row">
    <c:forEach items="${tasks}" var="task" varStatus="count">
        <t:cardprint task="${task}" can_edit="false"/>
        <c:if test="${count.count % 2 eq 0 }">
            <div class="clearfix"></div>
        </c:if>
    </c:forEach>
</div>
<script>
    $(document).ready(function () {
        window.print();
        setTimeout(function () {
            window.open('', '_self', '');
            window.close();
        }, 2000);
    });
</script>