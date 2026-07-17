<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
String tabNames = "";
String tabValues = "";
String cmd = (String) request.getAttribute(Constants.CMD);

String tabValue = ParamUtil.getString(request, "tab", null);

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}

if(tabValue == null || StringUtils.checkEmpty(tabValue)){	
	tabValue = (String) request.getAttribute("tab");
}

if(tabValue == null || StringUtils.checkEmpty(tabValue)){
	tabValue = "datos";
}

SituacionMedica situacionMedicaEnEdicion = 
	(SituacionMedica) request.getSession().getAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION);

String idRegistroSitMed = "";

if (situacionMedicaEnEdicion != null) {
	idRegistroSitMed = String.valueOf(situacionMedicaEnEdicion.getId_Situacion());
}

if ((cmd == null || cmd.trim().equals("")) && situacionMedicaEnEdicion != null) {
	cmd = Constants.EDIT;
}

tabNames = "Datos Generales,Archivos";
tabValues = "datos,archivos";

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/autorizaciones/editar_registro_situacionmedica_entry");
portletURL.setParameter("tab", tabValue);
portletURL.setParameter("cmd", cmd);

portletURL.setParameter("id_registro_sitmed", idRegistroSitMed);
portletURL.setParameter("idSituacionMedica", idRegistroSitMed);
%>

<liferay-ui:error key="error-estado-reclamo" message="falta-estado-reclamo-prestacion" />
<liferay-ui:error key="error-fechaseccional-reclamo" message="falta-fechaseccional-reclamo-prestacion" />
<liferay-ui:error key="error-fechaingresoospim-reclamo" message="falta-fechaingresoospim-reclamo-prestacion" />

<liferay-ui-custom:tabs
	names="<%=tabNames%>"
	tabsValues="<%=tabValues%>"
	value="<%=tabValue%>" 
	url="<%= portletURL.toString() %>"
	param="tab" />

<c:choose>
	<c:when test='<%= tabValue.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/patologias/view_situacionmedica.jsp"/>
	</c:when>

	<c:when test='<%= tabValue.equals("archivos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/patologias/sitmedica_imagen.jsp"/>
	</c:when>	
</c:choose>