<%@ include file="/html/portlet/afiliados/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);
String altaContactoTab = (String) request.getAttribute(WebKeysCrm.PORTLET_TAB_CRM_CONTACTO);

boolean showEmpleadores = false;
boolean showBonos = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_BONOS);
boolean showCredenciales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_CREDENCIALES);
boolean showReporteAfiliaciones = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_REPORTE_AFILIACIONES);
boolean showABMCrm = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM); 
boolean showCrmAuditoria = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_CRM_Auditoria); 
boolean showCrmBusqueda = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_CRM_Busqueda); 
//boolean showABMCrmLegal = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM_LEGALES); 
//boolean showCrmLegalBusqueda = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_CONSULTA_CRM_LEGALES); 
boolean showABMSeccional = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_SECCIONALES);
boolean showVIEWSeccional = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_VIEW_SECCIONALES);
boolean showABMAfiliado = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
boolean showCuentasBancariasAfiliado = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_REPORTE_AFILIACIONES);
boolean showReportePadronConsultaExterna =PermissionUtil.userContainsRole(user,"padron_afiliados_consulta_externa");


boolean showComercialAdministrador =
PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_ADMINISTRADOR);

boolean showComercialSeguimientoMolineros =
PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_MOLINEROS);

boolean showComercialSeguimientoNoMolineros =
PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_NO_MOLINEROS);

boolean showComercialConsulta =
PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_CONSULTA);

boolean showSeguimientoFormulario =
showComercialAdministrador ||
showComercialSeguimientoMolineros ||
showComercialSeguimientoNoMolineros ||
showComercialConsulta;

boolean showVendedores = showComercialAdministrador;

if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}


StringBuffer tabs1ValuesBuffer = new StringBuffer(!showReportePadronConsultaExterna?"afiliados":"");

if(showABMAfiliado){
	tabs1ValuesBuffer.append(",subir-archivo-afip");
}

if(showEmpleadores){
	tabs1ValuesBuffer.append(",empleadores");
}
if(showBonos){
	tabs1ValuesBuffer.append(",bonos");
}

if(showCredenciales){
	tabs1ValuesBuffer.append(",credenciales");
}

if(showReporteAfiliaciones || showReportePadronConsultaExterna ){
	tabs1ValuesBuffer.append(  (tabs1ValuesBuffer.length()>0?",":"")  + "reportes");
	//tabs1ValuesBuffer.append(",reportes");
}
if(showABMCrm && altaContactoTab != null){
	tabs1ValuesBuffer.append(",contactos");
}
if(showABMCrm || showCrmBusqueda){
	tabs1ValuesBuffer.append(",reportes_contactos");
}
if(showCrmAuditoria){
	tabs1ValuesBuffer.append(",estadisticas_contactos");
}

/*
if(showABMCrmLegal || showCrmLegalBusqueda){
	tabs1ValuesBuffer.append(",reportes_reclamos");
}
*/

if(showABMSeccional || showVIEWSeccional){
	tabs1ValuesBuffer.append(",seccionales");
}

if(showCuentasBancariasAfiliado){
	tabs1ValuesBuffer.append(",cuentas_bancarias");
}

if (showSeguimientoFormulario) {
    tabs1ValuesBuffer.append(",seguimiento-formulario");
}

if (showVendedores) {
    tabs1ValuesBuffer.append(",vendedores");
}

String tabs1Values=tabs1ValuesBuffer.toString();

if(!showReportePadronConsultaExterna){
  if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
		tabs1="afiliados";	
  }
}else{
	tabs1="reportes";
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/afiliados/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>

<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm">
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
/>
<!-- REPRESENTACIÓN DE LOS TABS DE AFILIACIONES -->
<c:choose>
	<c:when test='<%= tabs1.equals("afiliados") %>'>	
		<liferay-util:include page="/html/portlet/afiliados/busqueda_afiliado.jsp"/>		
	</c:when>
	<c:when test='<%= tabs1.equals("empleadores") %>'>
		<liferay-util:include page="/html/portlet/afiliados/empleadores/busqueda_empleadores.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("bonos") %>'>
		<liferay-util:include page="/html/portlet/afiliados/busqueda_bonos_seccional.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("credenciales") %>'>	
		<liferay-util:include page="/html/portlet/afiliados/busqueda_afiliado_cred.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("reportes") %>'>	
		<liferay-util:include page="/html/portlet/afiliados/reportes/reportes.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("contactos") %>'>	
		<liferay-util:include page="/html/portlet/crm/editar_contacto.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("reportes_contactos") %>'>
		<liferay-util:include page="/html/portlet/crm/busqueda_contacto.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("estadisticas_contactos") %>'>
		<liferay-util:include page="/html/portlet/crm/estadistica_contacto.jsp"/>
	</c:when>

	<c:when test='<%= tabs1.equals("seccionales") %>'>	
		<liferay-util:include page="/html/portlet/afiliados/busqueda_abm_seccionales.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("subir-archivo-afip") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/afip/upload_archivos_afip.jsp"/>	
	</c:when>
	
	<c:when test='<%= tabs1.equals("cuentas_bancarias") %>'>
   		<liferay-util:include page="/html/portlet/afiliados/busqueda_afiliado_cuenta_bancaria.jsp"/> 
	</c:when>
	
	<c:when test='<%= tabs1.equals("seguimiento-formulario") %>'>
		<liferay-util:include page="/html/portlet/afiliados/seguimiento_form.jsp"/>
  	</c:when>
  	
  	<c:when test='<%= tabs1.equals("vendedores") %>'>
		<liferay-util:include page="/html/portlet/afiliados/vendedores_list.jsp"/>
  	</c:when>
</c:choose>

</form>

<script type="text/javascript">
	function <portlet:namespace />altaAfiliado() {
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_afiliado_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = '<%=Constants.ADD%>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>	

<%
request.getSession().removeAttribute("opciones"); 
if (!tabs1.equals("afiliados")) {
	PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(tabs1, StringPool.UNDERLINE, StringPool.DASH)), request);
}
%>