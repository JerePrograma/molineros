<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ page import="com.liferay.portal.security.auth.AuthException" %>
<%@ page import="com.liferay.portal.kernel.servlet.SessionErrors" %>


<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);
String opciones= (String) request.getSession().getAttribute("opciones"); 

boolean showNovedades = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_NOVEDADES_SSS);
boolean showOpciones = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_OPCIONES);
boolean showSubirArchivoAfiliaciones=PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_SUBIR_ARCHIVO);
boolean showPreCargaAfi = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_PRE_CARGA_AFI);

if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}


/* StringBuffer tabs1ValuesBuffer = new StringBuffer("novedades-sss"); */
StringBuffer tabs1ValuesBuffer = null;

if(showOpciones){
	if(tabs1ValuesBuffer==null){
		tabs1ValuesBuffer = new StringBuffer("opciones");
	}else{
		tabs1ValuesBuffer.append(",opciones");
	}
	if (tabs1 == null){	
		tabs1="opciones";	
	} 
}

if(showNovedades){
	if(tabs1ValuesBuffer==null){
		tabs1ValuesBuffer = new StringBuffer("novedades-sss");
	}else{ 
		tabs1ValuesBuffer.append(",novedades-sss");
	}
	if (tabs1 == null){	
		tabs1="novedades-sss";	
	}
} 
if(showSubirArchivoAfiliaciones){
	if(tabs1ValuesBuffer==null){
		tabs1ValuesBuffer = new StringBuffer("subir-archivo");
	}else{
		tabs1ValuesBuffer.append(",subir-archivo");
	}	
}

if(showPreCargaAfi){
	if(tabs1ValuesBuffer==null){
		tabs1ValuesBuffer = new StringBuffer("pre-carga-afi");
	}else{
		tabs1ValuesBuffer.append(",pre-carga-afi");
	}
	if (tabs1 == null){	
		tabs1="pre-carga-afi";	
	} 
}


String tabs1Values=null;
if(tabs1ValuesBuffer != null){
	tabs1Values=tabs1ValuesBuffer.toString();
}

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
	if(showOpciones){
		tabs1="opciones";
	}
	if(showNovedades){
		tabs1="novedades-sss";
	}
	if(showSubirArchivoAfiliaciones){
		tabs1 = "subir-archivo";
	}
	if(showPreCargaAfi){
		tabs1="pre-carga-afi";	
	}
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
	value="<%=tabs1%>"
/>
<!-- REPRESENTACIÓN DE LOS TABS DE AFILIACIONES -->
<c:choose>
	<c:when test='<%= tabs1.equals("opciones") || (null!=opciones && opciones.equals("true"))%>'>	
		<liferay-util:include page="/html/portlet/afiliados/busqueda_afiliado.jsp">
			<liferay-util:param name="opciones" value="true"/>
		</liferay-util:include>			
	</c:when>
	<c:when test='<%= tabs1.equals("novedades-sss") %>'>
		<liferay-util:include page="/html/portlet/novedades/busqueda_novedades_sss.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("subir-archivo") %>'>
		<liferay-util:include page="/html/portlet/novedades/upload_archivos_sss.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("pre-carga-afi") %>'>
		<liferay-util:include page="/html/portlet/novedades/busqueda_pre_afiliado.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1Values==null %>'>
		<liferay-util:include page="/html/portlet/novedades/busqueda_novedades_sss_rol_error.jsp" />
	</c:when>
</c:choose>

</form>

<script type="text/javascript">
	function <portlet:namespace />altaOpcionAfi() {
		var url = '<portlet:actionURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_opcion_entry" /></portlet:actionURL>';
		<%-- document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = '<%=Constants.ADD%>'; --%>
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>	

<%
//request.getSession().removeAttribute("opciones"); 
/* if (!tabs1.equals("novedades-sss")) { */
if (!tabs1.equals("opciones")) {	
	PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(tabs1, StringPool.UNDERLINE, StringPool.DASH)), request);
}
%>