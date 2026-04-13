<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%

boolean showExpedSUR=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_EXPED_SUR);

boolean showReclamPrestac=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_RECLAM_PREST);
boolean showReadOnlyReclamPrestac=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CONSULTA_RECLAMOS_PRESTACIONALES);

boolean showPreAutorizaciones=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_PREAUTORIZACION );
boolean showAutorizaciones=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ALTA_AUTORIZACIONES_PMI );
boolean showReportePreAutorizaciones=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_PREAUTORIZACION_GERENCIAL );
boolean showEquipoInterdisciplinario =PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_EQUIPO_INTERDISCIPLINARIO);
boolean showSituacionMedica =PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_SITUACIONES_MEDICAS);
boolean showIntegracion =PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION);
boolean showPreCargaReintegros=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ABM_RECLAMOS_PRESTACIONALES_PRECARGA);


String tabs1 = ParamUtil.getString(request, "tabs1", null);
if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}
if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1");
}

//tabs1="recetas-pmi";

String tabs1Values = null;

tabs1Values="";

/* if (StringUtils.checkEmpty(tabs1) && showPreAutorizaciones
		&& !showAutorizaciones && !showExpedSUR && !showReclamPrestac){
	tabs1="pre-autorizaciones";
} */
if (showAutorizaciones ) {
    tabs1Values = "nomenclador,cartilla";
	tabs1Values += ",autorizaciones-prestacionales";
}

if (showExpedSUR  ) {
	tabs1Values +=  (tabs1Values.length()>0?",":"")  + "seguimiento-sur";
}

if (showEquipoInterdisciplinario) {
	tabs1Values += (tabs1Values.length()>0?",":"")  +"equipo-interdisciplinario";
}

if ((showReclamPrestac ) || (showReadOnlyReclamPrestac)) {
	tabs1Values += (tabs1Values.length()>0?",":"")  + "reclamos-prestacionales";
}

if (showPreCargaReintegros ) {
	tabs1Values += (tabs1Values.length()>0?",":"")  + "pre-carga-reintegros";
}

if (showPreAutorizaciones ) {
	/* if(!showAutorizaciones && !showExpedSUR && !showReclamPrestac){
	    tabs1="pre-autorizaciones"; 
	}  */  
	tabs1Values +=  (tabs1Values.length()>0?",":"")  + "pre-autorizaciones,ensalud-formularios";
}


if (showSituacionMedica ) {
	tabs1Values += (tabs1Values.length()>0?",":"")  + "situacion-medica";
}


if (showAutorizaciones ||  showReportePreAutorizaciones ) {
	tabs1Values +=  (tabs1Values.length()>0?",":"")  + "reportes";
}

if (showIntegracion ) {
	tabs1Values += (tabs1Values.length()>0?",":"")  + "integracion";
}


String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
	/* tabs1="nomenclador"; */
	if(StringUtils.checkNotEmpty(tabs1Values) && tabs1Values.contains(",")){
		tabs1= tabs1Names.split(",")[0];
	}else{
		tabs1= tabs1Names;
	}
}


/* String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH); */
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/autorizaciones/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>


<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
	value="<%=tabs1%>"
/>
<!-- REPRESENTACIóN DE LOS TABS DE Autorizaciones -->
<c:choose>	

	<c:when test='<%=tabs1.equals("recetas-pmi") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/recetas/autorizaciones_recetas_pmi.jsp"/>	
	</c:when>
	<c:when test='<%=tabs1.equals("nomenclador") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/nomenclador/nomenclador.jsp"/>	
	</c:when>
	<%-- <c:when test='<%=tabs1.equals("conv-prest") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/administracion/convenios_prest/busqueda_convenios_prestacionales.jsp"/>	
	</c:when> --%>
	<c:when test='<%=tabs1.equals("seguimiento-sur") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/seguimiento_sur/seguimiento_sur_list.jsp"/>	
	</c:when>
	<c:when test='<%=tabs1.equals("cartilla") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/cartilla_list.jsp"/>	
	</c:when>
	<c:when test='<%=tabs1.equals("autorizaciones-prestacionales") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/autorizaciones_prestacionales/busqueda_autorizaciones_prestacionales.jsp"/>	
	</c:when>
	<c:when test='<%=tabs1.equals("reclamos-prestacionales") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/busqueda_reclamos_prestacionales.jsp"/>	
	</c:when>
	<c:when test='<%=tabs1.equals("pre-carga-reintegros") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/busqueda_reclamos_prestacionales_seccional.jsp"/>	
	</c:when>
	<c:when test='<%=tabs1.equals("situacion-medica") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/patologias/busqueda_situacion_medica.jsp"/>	
	</c:when>
	<c:when test='<%=tabs1.equals("pre-autorizaciones") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/pre_autorizaciones/preautorizacion_list.jsp"/>	
	</c:when>
	<c:when test='<%=tabs1.equals("equipo-interdisciplinario") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/equipo_interdisciplinario/busqueda_equipo_interdisciplinario.jsp"/>	
	</c:when>					
	<c:when test='<%=tabs1.equals("reportes") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reportes/reportes.jsp"/>	
	</c:when>	
	
	<c:when test='<%=tabs1.equals("integracion") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/integracion/integracion_menu.jsp"/>	
	</c:when>
	
	<c:when test='<%=tabs1.equals("ensalud-formularios") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/acceso_patologias_ensalud.jsp"/>	
	</c:when>	
	
</c:choose>

</form>