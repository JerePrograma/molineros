<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);

boolean showSeguimientoEmpresas = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_SEGUIMIENTO_EMPRESAS_GESTION);
boolean showActasNoOS = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_SEGUIMIENTO_EMPRESAS_ACTAS_NO_OS);
boolean showConveniosNoOS = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_SEGUIMIENTO_EMPRESAS_CONVENIOS_NO_OS);
boolean showIngresosNoOS=true;
boolean showReportes=PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_SEGUIMIENTO_EMPRESAS_REPORTES);
boolean showSubirArchivos = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.SUBIR_ARCHIVOS_SEG_EMPRESAS);
boolean showJudiciales=PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_SEGUIMIENTO_EMPRESAS_JUDICIALES);

if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}

StringBuffer tabs1ValuesBuffer = new StringBuffer();

if(showSeguimientoEmpresas){
	tabs1ValuesBuffer.append("seguimiento-empresas");
}

if(showJudiciales){
	if(tabs1ValuesBuffer.length()==0 || "".equals(tabs1ValuesBuffer)){
	  tabs1ValuesBuffer.append("judicial");	
	}else{
	  tabs1ValuesBuffer.append(",judicial");
	}  
}

if(showActasNoOS){
	if(tabs1ValuesBuffer.length()==0 ||  "".equals(tabs1ValuesBuffer)){
		tabs1ValuesBuffer.append("actas-noOS");
	}else{
	    tabs1ValuesBuffer.append(",actas-noOS");
	}    
}

if(showConveniosNoOS){
	if(tabs1ValuesBuffer.length()==0 || "".equals(tabs1ValuesBuffer)){
	   tabs1ValuesBuffer.append("convenios-noOS");	
	}else{
	   tabs1ValuesBuffer.append(",convenios-noOS");
	}   
}

if(showReportes){
	if(tabs1ValuesBuffer.length()==0 || "".equals(tabs1ValuesBuffer)){
	  tabs1ValuesBuffer.append("reportes");	
	}else{
	  tabs1ValuesBuffer.append(",reportes");
	}  
}

if(showSubirArchivos){
	if(tabs1ValuesBuffer.length()==0 || "".equals(tabs1ValuesBuffer)){
	  tabs1ValuesBuffer.append("subir-archivo");		
	}else{
	  tabs1ValuesBuffer.append(",subir-archivo");
	}  
}

String tabs1Values=tabs1ValuesBuffer.toString();

if (tabs1 == null || 
                   (tabs1 != null && tabs1Values.indexOf(tabs1) < 0) || 
                   !tabs1Values.contains(tabs1) || 
                      tabs1.equals("convenios")|| tabs1.equals("actas")){	
		tabs1="seguimiento-empresas";
		if(!tabs1Values.contains(tabs1)){
			tabs1="judicial";	
		}
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/estudio_isidro/view");
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
	<c:when test='<%= tabs1.equals("seguimiento-empresas") %>'>
		<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/busqueda_seguimiento.jsp">			
		</liferay-util:include>
	</c:when>	
	<%-- <c:when test='<%= tabs1.equals("deudas-noOS") %>'>
		<liferay-util:include page="/html/portlet/uoma/actasNoOS/busqueda_calculo_deuda.jsp">
		</liferay-util:include>
	</c:when> --%>
	<c:when test='<%= tabs1.equals("actas-noOS") %>'>
		<liferay-util:include page="/html/portlet/uoma/actasNoOS/busqueda_actasNoOS.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("convenios-noOS") %>'>
		<liferay-util:include page="/html/portlet/uoma/conveniosNoOS/busqueda_convenios_no_os.jsp">
		</liferay-util:include>
	</c:when>
	<%-- <c:when test='<%= tabs1.equals("ingresos-estudio") %>'>
		<liferay-util:include page="/html/portlet/uoma/recibos/busqueda_recibos_no_os.jsp"/>	
	</c:when> --%>
	<c:when test='<%= tabs1.equals("reportes") %>'>
		<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/reportes.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("judicial") %>'>
		<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_list.jsp"/>	
	</c:when>
</c:choose>

</form>