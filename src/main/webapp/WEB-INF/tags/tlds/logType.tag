<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ attribute name="logType" required="true" type="com.qprogramming.tasq.task.worklog.LogType"%>
<s:message code="${logType.code}"></s:message>
