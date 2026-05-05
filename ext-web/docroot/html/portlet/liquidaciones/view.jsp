<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%
boolean rolVEROP = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_VER_OP);
boolean rolABMOP = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_OP);

if (rolABMOP) {
	rolVEROP = true;
}
	
boolean showCheques = true;
boolean showOrdenPagoAmtima = true;
boolean showOspim = true;
boolean showOpcionesFarma=PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_FARMACIA);
boolean showOpcionesOdo = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA) || PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO);
boolean showOpcionesAuditor = !PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA) && PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO);

boolean showComprobantesGral=PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_BUSQUEDA_GENERAL_COMPROBANTES);
boolean esLiquidadorExterno = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_LIQUIDACIONES_HOSPITALES);
boolean esLiquidadorOSPIM = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);
boolean showPrestador = PermissionUtil.userContainsRole(user,"ABM_PRESTADOR") || PermissionUtil.userContainsRole(user,"VIEW_PRESTADOR");

String tabs1 = ParamUtil.getString(request, "tabs1", null);
if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}
if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}
/*
if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){
	tabs1="liquidaciones";
}
*/
String tabs1Values =null;
if(esLiquidadorExterno){
  tabs1Values = "liquidaciones";
  if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){
	    tabs1="liquidaciones";
  } 
  
}

if(esLiquidadorOSPIM){
  tabs1Values = "liquidaciones,reintegros,reintegro-farmacia";
  tabs1Values += ",cheques,liquidacion-debitos-terceros,consulta-lista-reintegro,consulta-lista-reintegro-farmacia,reportes,administracion-tablas";
  if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){
    tabs1="liquidaciones";
  }  
}else{
	if(showPrestador){
		if(tabs1Values!=null){
		   tabs1Values += (tabs1Values.length()>0?",":"") +"administracion-tablas";
		} else{
		   tabs1Values="administracion-tablas";
		   if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){
			    tabs1="administracion-tablas";
		   }  
		}
	}
}

if (showOpcionesOdo || showOpcionesAuditor) {
	if(tabs1Values ==null){
	   tabs1Values="";	
	   if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){	
	      tabs1="protesis";
	   }   
	} else{
	   tabs1Values += ",";
	}
	tabs1Values += "protesis,ortodoncia-ortopedia";
}
if (showOspim && rolVEROP){
	if(tabs1Values ==null){
		tabs1Values="";
		if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){	
	      tabs1="comprobantes";
		}  
	}else{
	   tabs1Values += ",";
	}
	tabs1Values += "comprobantes";
	if(showComprobantesGral){
		tabs1Values += ",comprobantes-consulta-general";
	}
	tabs1Values += ",ordenes-pago-ospim";	
}else if(showOspim && showComprobantesGral){
	if(tabs1Values ==null){
		tabs1Values="";
		if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){
		   tabs1="comprobantes-consulta-general";
		}   
	}else{
		 tabs1Values += ",";
	}
	tabs1Values += "comprobantes-consulta-general";
}

/*
if(showPrestador){
	if(tabs1Values!=null){
	   tabs1Values += (tabs1Values.length()>0?",":"") +"prestador";
	} else{
	   tabs1Values="prestador";
	   //if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){
	      tabs1 = "prestador";
	   //}   
	}
}
*/
if (showOpcionesAuditor) {
	if (tabs1.equals("reintegros")) {
		if (tabs1 == null || tabs1.equals("bandeja-de-entrada")){
		   tabs1 = "protesis";
		}   
	}
}

String[] vTab=tabs1Values.split(",");
if(vTab.length>0){
  if (tabs1 == null){ 	
	tabs1=vTab[0];
  }else{
	  if(!tabs1Values.contains(tabs1) ){
		 tabs1=vTab[0];
	  }
  }
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/liquidaciones/view");
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
<!-- REPRESENTACI�N DE LOS TABS DE Liquidaciones -->
<c:choose>
	<c:when test='<%= tabs1.equals("liquidaciones") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/busqueda_liquidaciones.jsp">
			<liferay-util:param name="tipo_liquidacion" value="<%=WebKeysLiquidaciones.LIQUIDACION_PRE %>"/>
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("reintegros") %>'>
			<liferay-util:include page="/html/portlet/liquidaciones/reintegros/busqueda_reintegro.jsp">
			<liferay-util:param name="tipo_reintegro" value="<%=WebKeysLiquidaciones.REINTEGRO_PRE %>"/>
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("reintegro-farmacia") %>'>
		<liferay-util:include page="/html/portlet/farmacia/reintegros/busqueda_reintegro.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("protesis") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/reintegros/busqueda_reintegro.jsp">
		  	<liferay-util:param name="tipo_reintegro" value="<%=WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS %>"/>
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("ortodoncia-ortopedia") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/reintegros/busqueda_reintegro.jsp">
		  	<liferay-util:param name="tipo_reintegro" value="<%=WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA %>"/>
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("comprobantes") %>'>	
		<liferay-util:include page="/html/portlet/liquidaciones/comprobantes/busqueda_comprobantes.jsp"/>
	</c:when>	
	<c:when test='<%= tabs1.equals("comprobantes-consulta-general") %>'>	
		<liferay-util:include page="/html/portlet/liquidaciones/comprobantes/comprobantes_consulta_general.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("ordenes-pago-ospim") %>'>	
		<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_ordenes_pago_ospim.jsp"/>
	</c:when>		
	<c:when test='<%= tabs1.equals("cheques") %>'>	
		<liferay-util:include page="/html/portlet/liquidaciones/cheques/busqueda_cheques.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("liquidacion-debitos-terceros") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/busqueda_nota_debito.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("prestador") %>'>	
		<liferay-util:include page="/html/portlet/prestadores/busqueda_prestadores.jsp"/>
	</c:when>	
	<c:when test='<%= tabs1.equals("consulta-lista-reintegro") %>'>	
		<liferay-util:include page="/html/portlet/liquidaciones/consulta_listas_reintegros/reporte_listas_reintegros.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("consulta-lista-reintegro-farmacia") %>'>	
		<liferay-util:include page="/html/portlet/farmacia/consulta_listas_reintegros/reporte_listas_reintegros.jsp"/>
	</c:when>	
	<c:when test='<%= tabs1.equals("reportes") %>'>
		<liferay-util:include page="/html/portlet/liquidaciones/reportes/reportes.jsp"/>	
	</c:when>	
	<c:when test='<%= tabs1.equals("administracion-tablas") %>'>	
		<liferay-util:include page="/html/portlet/prestadoresadministracionTablas.jsp"/>
	</c:when>

</c:choose>

</form>

<script type="text/javascript">

	
	function <portlet:namespace />altaReintegroFarmacia() {		
		<portlet:namespace />limpiarCamposAfiliado(); 
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_reintegro_farmacia_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

	function <portlet:namespace />altaLiquidacion() {
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_liquidacion_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

	function <portlet:namespace />altaReintegro() {
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_reintegro_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>
<%-- <%
if (!tabs1.equals("liquidaciones")) {
	PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(tabs1, StringPool.UNDERLINE, StringPool.DASH)), request);
}
%> --%>