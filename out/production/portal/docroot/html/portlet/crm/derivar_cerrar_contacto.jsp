<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/correspondencia/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

ItemCorrespondencia itemCorr=(ItemCorrespondencia)row.getObject();
String urlRecibir = null;
String urlDerivar = null;

if(itemCorr.getCabecera().getTipoEnvio().equalsIgnoreCase("PAQ_FARMACIA")){
	urlRecibir = "javascript:marcaRecibido('"+String.valueOf(itemCorr.getListaPaquete().getId_paquete())+"','PAQ','0')";
}else{
	urlRecibir = "javascript:marcaRecibido('"+String.valueOf(itemCorr.getId())+"','ITEM','"+String.valueOf(itemCorr.getIdCRMContacto())+"')";
}
if(itemCorr.getIdCRMContacto()!=null && itemCorr.getIdCRMContacto() > 0){
	urlDerivar = "javascript:marcaDerivar('"+String.valueOf(itemCorr.getId())+"','ITEM','"+String.valueOf(itemCorr.getIdCRMContacto())+"')";
}
String img = "/html/images/mail_leido.png";
%>
<!-- themeDisplay.getPathThemeImages().toString()  --> 
<portlet:defineObjects />

<liferay-ui:icon-menu>	

<c:if test="<%= Validator.isNotNull(urlRecibir) %>">
 
	 <liferay-ui:icon 
			src="<%=img%>"
			message="Recibir"
			url="<%=urlRecibir%>"/>
		
</c:if>
<c:if test="<%= Validator.isNotNull(urlDerivar) %>">

	<liferay-ui:icon
			image="assign"
			message="Derivar"
			url="<%=urlDerivar%>"/>
	
</c:if>
</liferay-ui:icon-menu>	