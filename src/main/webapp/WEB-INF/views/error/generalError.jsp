<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div style="padding: 40px">
<p>
    <c:out value="${errorMessage}"/>
</p>

<c:if test="${errorMessage['class'] ne 'class java.lang.String'}">
    <h5 class="toggle_stack btn btn-default">
        <i class="fa fa-caret-square-o-right"></i> Show stacktrace
    </h5>
    <div
            style="display: none; padding: 5px; border: 1px solid; border-radius: 5px; margin-top: 10px; background-color: white; word-wrap: break-word; ">
        <div>
            <!-- it's a String! -->
            <c:forEach items="${errorMessage.stackTrace}" var="element">
                <c:out value="${element}"/>
            </c:forEach>
        </div>
    </div>
</div>
</c:if>
<script>
    $(".toggle_stack").click(function () {
        toggle($(this));
    });

    function toggle(obj) {
        var open = obj.attr('open');
        obj.next('div').slideToggle('slow');
        obj.children('i').toggleClass('fa-caret-square-o-down');
        obj.children('i').toggleClass('fa-caret-square-o-right');
        obj.attr('open', !open);
    }
</script>