<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<form action="" method="post" name="<portlet:namespace />rec">

	<input name="<portlet:namespace /><%=Constants.CMD%>" type="hidden"	value="" /> 
	<liferay-util:include page="/html/portlet/tesoreria/recibos/view_recibo.jsp" />
	
</form>
