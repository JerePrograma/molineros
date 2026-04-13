<%@ include file="/html/portlet/comprobantes/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);
if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}


StringBuffer tabs1ValuesBuffer = new StringBuffer("gestion-comprobantes");

boolean showAdministracion=PermissionUtil.userContainsRole(user,WebKeysComprobantes.ROL_ADMINISTRACION );
boolean showIntegracion =PermissionUtil.userContainsRole(user,WebKeysComprobantes.ROL_INTEGRACION);
boolean showAcompanante =PermissionUtil.userContainsRole(user,WebKeysComprobantes.ROL_ACOMPANANTE);
boolean showHospitales =PermissionUtil.userContainsRole(user,WebKeysComprobantes.ROL_HOSPITALES);
boolean showFarmacia =PermissionUtil.userContainsRole(user,WebKeysComprobantes.ROL_FARMACIA);
boolean showProveedores =PermissionUtil.userContainsRole(user,WebKeysComprobantes.ROL_PROVEEDORES);
boolean showGerenciadoras =PermissionUtil.userContainsRole(user,WebKeysComprobantes.ROL_GERENCIADORAS);



if (showIntegracion ) {
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",integracion");
	}else{
	  tabs1ValuesBuffer.append("integracion");	
	}
}
	
if (showAcompanante ) {
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",acompanante");
	}else{
	  tabs1ValuesBuffer.append("acompanante");	
	}
}

if (showHospitales ) {
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",hospitales");
	}else{
	  tabs1ValuesBuffer.append("hospitales");	
	}
}

if (showFarmacia ) {
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",farmacia-drogueria");
	}else{
	  tabs1ValuesBuffer.append("farmacia-drogueria");	
	}
}

if (showProveedores ) {
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",proveedores");
	}else{
	  tabs1ValuesBuffer.append("proveedores");	
	}
}

if (showGerenciadoras ) {
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",gerenciadoras");
	}else{
	  tabs1ValuesBuffer.append("gerenciadoras");	
	}
}

if (showAdministracion  ) {
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",administracion-usuarios");
	}else{
	  tabs1ValuesBuffer.append("administracion-usuarios");	
	}
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",download-ws");
	}else{
	  tabs1ValuesBuffer.append("download-ws");	
	}
	
	if(tabs1ValuesBuffer.length()>0){
		  tabs1ValuesBuffer.append(",informar-pagos-ws");
	}else{
		  tabs1ValuesBuffer.append("informar-pagos-ws");	
	}
	
}

String tabs1Values=tabs1ValuesBuffer.toString();

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
		tabs1="gestion-comprobantes";	
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/comprobantes/view");
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
<!-- REPRESENTACIÓN DE LOS TABS DE COMPROBANTES -->
<c:choose>
	<c:when test='<%= tabs1.equals("gestion-comprobantes") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_list.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("administracion-usuarios") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/sector_usuarios.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("download-ws") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_download_ws.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("integracion") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_list_integracion.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("acompanante") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_list_acompanante_terapeutico.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("hospitales") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_list_hospitales.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("farmacia-drogueria") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_list_farmacia.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("gerenciadoras") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_list_gerenciadoras.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("proveedores") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_list_proveedores.jsp"/>
	</c:when>
	
	<c:when test='<%= tabs1.equals("informar-pagos-ws") %>'>	
		<liferay-util:include page="/html/portlet/comprobantes/comprobantes_upload_op_ws.jsp"/>
	</c:when>
	
	
</c:choose>

</form>

<script type="text/javascript">
	
</script>	

<%
request.getSession().removeAttribute("opciones"); 
if (!tabs1.equals("afiliados")) {
	PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(tabs1, StringPool.UNDERLINE, StringPool.DASH)), request);
}
%>