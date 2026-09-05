<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />
<%
//ReclamoPrestacional  reclamoprestacional  = (ReclamoPrestacional)request.getSession().getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
String tabNames="" ;
String tabValues="" ;
String cmd = (String) request.getAttribute(Constants.CMD);


//Integer idReclamoAux = reclamoprestacional!=null?reclamoprestacional.getId_reclamo():0;
//String  idReclamoString  = reclamoprestacional!=null?reclamoprestacional.getId_String() :"";

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

//if (idReclamoAux == 0 ){
	tabNames="Datos Generales" ;
	 tabValues="datos" ;
/*	
}else{
	 tabNames="Datos Generales,Archivos" ;
	 tabValues="datos,archivos" ;
}
*/
PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/autorizaciones/editar_registro_equipointerdisciplanrio_entry");
portletURL.setParameter("tab", tabValue);
portletURL.setParameter("cmd", cmd);
//portletURL.setParameter("reclamo_id", String.valueOf(idReclamoAux));

%>
<liferay-ui:error key="error-estado-reclamo" message="falta-estado-reclamo-prestacion" />
<liferay-ui:error key="error-fechaseccional-reclamo" message="falta-fechaseccional-reclamo-prestacion" />
<liferay-ui:error key="error-fechaingresoospim-reclamo" message="falta-fechaingresoospim-reclamo-prestacion" />

<%
if (SessionErrors.contains(renderRequest, "dictamen-modificado-concurrentemente")) {

    String usuarioModificacionConcurrente = (String) request.getAttribute("usuarioModificacionConcurrente");
    String mensajeConcurrente;

    if (usuarioModificacionConcurrente == null || usuarioModificacionConcurrente.trim().isEmpty()) {
        mensajeConcurrente = "El dictamen fue modificado por otro usuario. Se cargaron los últimos cambios.";
    } else {
        mensajeConcurrente = "El usuario " + usuarioModificacionConcurrente + " modificó este dictamen. Se cargaron los últimos cambios.";
    }
%>

    <div class="portlet-msg-error">
        <%= HtmlUtil.escape(mensajeConcurrente) %>
    </div>

<%
}
%>
	
<liferay-ui-custom:tabs
	names="<%=tabNames%>"
	tabsValues="<%=tabValues%>"
	value="<%=tabValue%>" 
	url="<%= portletURL.toString() %>"
	param="tab" />
		
<c:choose>
	<c:when test='<%= tabValue.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/equipo_interdisciplinario/view_equiposinter.jsp"/>
	</c:when>
	<c:when test='<%= tabValue.equals("archivos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/equipo_interdisciplinario/reclamo_prestacional_imagen.jsp"/>
	</c:when>	
</c:choose>


