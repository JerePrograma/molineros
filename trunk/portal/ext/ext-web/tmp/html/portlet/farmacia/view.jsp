<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ page import="ar.com.uoma.WebKeysUOMA" %>
<%

boolean showOpcionesFarmacia = PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ABM_FARMACIA);
boolean showContabilidad = PermissionUtil.userContainsRole(user,"ABM_Contabilidad_AMTIMA");
boolean showCalculoDeuda = PermissionUtil.userContainsRole(user,"ABM_CalculoDeuda");
boolean showSubirArchivos = PermissionUtil.userContainsRole(user,"ABM_AMTIMA_ARCHIVOS");
boolean showInterbanking = PermissionUtil.userContainsRole(user, WebKeysGlobal.ROL_INTERBANKING);
boolean showABMCentroCostoUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_ABM_CENTRO_COSTO_UOMA);

boolean showABMProveedores = PermissionUtil.userContainsRole(user,"ABM_Proveedores");


String tabs1 = ParamUtil.getString(request, "tabs1", null);
if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}
if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1");
}
if (tabs1 == null){
	tabs1="comprobantes";
}

//String tabs1Values = "reintegros,vademecum,correo,importar-archivos,consulta-lista-reintegro,actas,convenios";
String tabs1Values = "comprobantes,ordenes-pago,ingresos,bancos";
/* if(tabs1Values!=null && showCalculoDeuda){
	tabs1Values=tabs1Values+",calculo-deuda";
}else{
	tabs1Values=tabs1Values+",actas,convenios,reportes,cheques";
} */
if(tabs1Values!=null && showCalculoDeuda){
	tabs1Values=tabs1Values+",calculo-deuda,actas,convenios";
}
tabs1Values=tabs1Values+",reportes,cheques";

if(tabs1Values!=null && showSubirArchivos){
	tabs1Values=tabs1Values+",subir-archivo";
}
if(tabs1Values!=null && showContabilidad){
	tabs1Values=tabs1Values+",contabilidad";
}else if(tabs1Values==null && showContabilidad){
	tabs1Values="contabilidad";
}
if(tabs1Values!=null && showABMProveedores){
	tabs1Values=tabs1Values+",proveedores";
}else{
	tabs1Values=tabs1Values+",proveedores";
}

if(showInterbanking){	
	tabs1Values=tabs1Values!=null?tabs1Values + ",interbanking":"interbanking";
}

if(showABMCentroCostoUOMA){
	tabs1Values=tabs1Values!=null?tabs1Values + ",centro-costo":"centro-costo";
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(WindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/farmacia/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>

<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
/>
<!-- TODO: VER POR QUE LLEGA BANDEJAD DE ENTRADA? -->
<%if(tabs1.equals("bandeja-de-entrada")){
	tabs1="comprobantes";	
}%>
<c:choose>	
	<c:when test='<%= tabs1.equals("comprobantes") %>'>	
		<liferay-util:include page="/html/portlet/farmacia/comprobantes/busqueda_comprobantes_amtima.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("ordenes-pago") %>'>	
		<liferay-util:include page="/html/portlet/uoma/busqueda_ordenes_pago.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("ingresos") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/recibos/busqueda_recibos.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("bancos") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/bancos/bancos.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("cheques") %>'>	
		<liferay-util:include page="/html/portlet/liquidaciones/cheques/busqueda_cheques.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("calculo-deuda") %>'>
		<liferay-util:include page="/html/portlet/uoma/actasNoOS/busqueda_calculo_deuda.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("actas") %>'>
		<liferay-util:include page="/html/portlet/uoma/actasNoOS/busqueda_actasNoOS.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("convenios") %>'>
		<liferay-util:include page="/html/portlet/uoma/conveniosNoOS/busqueda_convenios_no_os.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("reportes") %>'>	
		<liferay-util:include page="/html/portlet/farmacia/reportes/reportes.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("contabilidad") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/equivalencias/contabilidad.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("subir-archivo") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/afip/upload_archivos_afip.jsp"/>	
	</c:when>	
	<c:when test='<%= tabs1.equals("proveedores") %>'>
		<liferay-util:include page="/html/portlet/uoma/proveedores/proveedores_list.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("interbanking") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/interbanking_menu.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("centro-costo") %>'>
		<liferay-util:include page="/html/portlet/uoma/centro_costo/centro_costo_opc.jsp"/>	
	</c:when>
		
</c:choose>

</form>

<script type="text/javascript">	
	function <portlet:namespace />altaReintegroFarmacia() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/farmacia/editar_reintegro_farmacia_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>
<%
if (!tabs1.equals("vademecum")) {
	PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(tabs1, StringPool.UNDERLINE, StringPool.DASH)), request);
}
%>