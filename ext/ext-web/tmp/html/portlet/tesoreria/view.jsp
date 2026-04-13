<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.uoma.WebKeysUOMA" %>
<%

String tabs1 = ParamUtil.getString(request, "tabs1", null);
if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}
if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}

boolean tesoreria= PermissionUtil.userContainsRole(user,"ABM_Tesoreria") || PermissionUtil.userContainsRole(user,"consulta_tesoreria") ;
boolean reportes=PermissionUtil.userContainsRole(user,"Reportes_Tesoreria_Contaduria_Declaracion_Jurada") || PermissionUtil.userContainsRole(user,WebKeysTesoreria.REPORTE_CHEQUES_PENDIENTE_COBRO);
boolean calcDeudaMasivo=PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_CALCULO_DEUDA_MASIVO);
boolean showInterbanking = PermissionUtil.userContainsRole(user, WebKeysGlobal.ROL_INTERBANKING);

boolean showABMCentroCostoUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_ABM_CENTRO_COSTO_UOMA);
boolean showTABLEROCentroCostoUOMA = PermissionUtil.userContainsRole(user, WebKeysUOMA.ROL_TABLERO_CENTRO_COSTO_UOMA);


if ((tabs1 == null || tabs1.equals("bandeja-de-entrada")) && tesoreria){
	tabs1="calculo-deuda"; 
}else if(tabs1==null && reportes){
	tabs1="reportes";
}
String tabs1Values = null;

if(tesoreria && !calcDeudaMasivo){
 	tabs1Values = "calculo-deuda,actas,convenios,liquidar-actas-convenios,liquidar-desregulados,bancos,ingresos,subir-archivo-afip,contabilidad,reportes,adm-caja-chica,adm-interes";
}else if(tesoreria && calcDeudaMasivo){
	tabs1Values = "calculo-deuda,calculos-deuda-masivo,actas,convenios,liquidar-actas-convenios,liquidar-desregulados,bancos,ingresos,subir-archivo-afip,contabilidad,reportes,adm-caja-chica,adm-interes"; 	
}else if(reportes){
	tabs1Values = "reportes";
}
if(showInterbanking){	
	tabs1Values=tabs1Values!=null?tabs1Values + ",interbanking":"interbanking";
}


if(showABMCentroCostoUOMA){
	tabs1Values=tabs1Values!=null?tabs1Values + ",centro-costo":"centro-costo";
}	

if(tesoreria){
	tabs1Values=tabs1Values!=null?tabs1Values + ",liquidar-jubilados":"liquidar-jubilados";
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/tesoreria/view");
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
<!-- REPRESENTACIÓN DE LOS TABS DE tesoreria -->
<c:choose>	
	<c:when test='<%= tabs1.equals("calculo-deuda") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/actas/busqueda_calculo_deuda.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("calculos-deuda-masivo") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/actas/busqueda_calculo_deuda_masivo.jsp">
		</liferay-util:include>
	</c:when>
		
	<c:when test='<%= tabs1.equals("actas") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/actas/busqueda_actas.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("convenios") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/convenios/busqueda_convenios.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("liquidar-actas-convenios") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/liquida_acta_convenios/liquida_acta_convenios.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("liquidar-desregulados") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/liquida_desregulados/liquida_desregulados.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("bancos") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/bancos/bancos.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("ingresos") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/recibos/busqueda_recibos.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("subir-archivo-afip") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/afip/upload_archivos_afip.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("contabilidad") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/equivalencias/contabilidad.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("reportes") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/reportes/reportes.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("adm-caja-chica") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/caja_chica/caja_chica_adm.jsp"/>	
	</c:when>
		<c:when test='<%= tabs1.equals("adm-interes") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/intereses/interes_adm.jsp"/>	
	</c:when>
	<c:when test='<%= tabs1.equals("interbanking") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/interbanking_menu.jsp"/>	
	</c:when>	
	<c:when test='<%= tabs1.equals("centro-costo") %>'>
		<liferay-util:include page="/html/portlet/uoma/centro_costo/centro_costo_opc.jsp"/>	
	</c:when>
	
	<c:when test='<%= tabs1.equals("liquidar-jubilados") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/liquida_jubilados/liquida_jubilados.jsp">
		</liferay-util:include>
	</c:when>
	
	
</c:choose>
</form>