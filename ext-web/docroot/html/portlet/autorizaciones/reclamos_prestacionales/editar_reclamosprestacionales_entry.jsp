<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />
<%
ReclamoPrestacional  reclamoprestacional  = (ReclamoPrestacional)request.getSession().getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
String contextoCompraNonceRequest =
        ParamUtil.getString(
                request,
                WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
                ""
        );
ReclamoPrestacionalCompraContexto contextoCompraSesion =
        (ReclamoPrestacionalCompraContexto) request.getSession().getAttribute(
                WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
        );
ReclamoPrestacionalCompraContexto contextoCompra =
        contextoCompraSesion != null
                && contextoCompraSesion.coincideNonce(
                        contextoCompraNonceRequest
                )
                && contextoCompraSesion.perteneceAUsuario(
                        user != null ? user.getScreenName() : ""
                )
                && contextoCompraSesion.estaVigente(
                        System.currentTimeMillis()
                )
                ? contextoCompraSesion
                : null;
String contextoCompraNonce =
        contextoCompra != null ? contextoCompra.getNonce() : "";
String tabNames = "";
StringBuilder tabValues = new StringBuilder();

String cmd = (String) request.getAttribute(Constants.CMD);

if (contextoCompra != null) {
    cmd = Constants.ADD;
    request.setAttribute(Constants.CMD, Constants.ADD);
}

boolean showReadOnlyReclamPrestac=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CONSULTA_RECLAMOS_PRESTACIONALES);

Integer idReclamoAux = reclamoprestacional!=null?reclamoprestacional.getId_reclamo():0;
String  idReclamoString  = reclamoprestacional!=null?reclamoprestacional.getId_String() :"";

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

if (idReclamoAux == 0) {
    tabNames = "Datos Generales";
    tabValues.append("datos");

} else if ("REINTEGRO".equalsIgnoreCase(
        reclamoprestacional.getTipoPedido())) {

    tabNames =
            "Datos Generales,CTA Bancaria, Archivos, "
                    + "Histórico de Movimientos";

    tabValues.append("datos");
    tabValues.append(",cta_bancaria");
    tabValues.append(",archivos");
    tabValues.append(",historico_movimientos");

} else {
    tabNames =
            "Datos Generales,Archivos, "
                    + "Histórico de Movimientos";

    tabValues.append("datos");
    tabValues.append(",archivos");
    tabValues.append(",historico_movimientos");
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/autorizaciones/editar_reclamosprestaciones_entry");
portletURL.setParameter("tab", tabValue);
if (cmd != null) {
    portletURL.setParameter("cmd", cmd);
}
portletURL.setParameter("reclamo_id", String.valueOf(idReclamoAux));

if (contextoCompraNonce != null
        && contextoCompraNonce.trim().length() > 0) {

    portletURL.setParameter(
            WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
            contextoCompraNonce
    );
    portletURL.setParameter("origen", "compras");
    portletURL.setParameter(Constants.CMD, Constants.ADD);
}

%>
<liferay-ui:error key="error-estado-reclamo" message="falta-estado-reclamo-prestacion" />
<liferay-ui:error key="error-fechaseccional-reclamo" message="falta-fechaseccional-reclamo-prestacion" />
<liferay-ui:error key="error-fechaingresoospim-reclamo" message="falta-fechaingresoospim-reclamo-prestacion" />
<liferay-ui:error key="error-cargos-reclamo" message="completar-area-medica" />
<liferay-ui:error key="error-comprobante-invalido" message="tipo-comprobante-invalido" />
<liferay-ui:error key="error-comprobante-duplicado" message="comprobante-duplicado" />
<liferay-ui:error key="errorPlanNoPermiteReclamo" message="<%=(String) request.getAttribute(\"msgErrorPlanNoPermiteReclamo\")%>"/>

<liferay-ui:error key="errorCuentaReclamo" message="<%=(String)request.getAttribute(\"msgErrorCuentaReclamo\") %>" />

	
<liferay-ui:error
    key="error-reclamo-modificado"
    message="<%=(String)request.getAttribute(\"msgErrorReclamoModificado\") %>" />

<liferay-ui:error
    key="error-reclamo-ya-cerrado"
    message="<%=(String)request.getAttribute(\"msgErrorReclamoYaCerrado\") %>" />


<liferay-ui:error
    key="error-reclamo-compras"
    message="<%=(String)request.getAttribute(\"msgErrorReclamoCompras\") %>" />
    
<!--  form action="" method="post" method="post" name="<portlet:namespace />reclamo_fm" id="<portlet:namespace />reclamo_fm"  enctype="multipart/form-data"-->		

	
<liferay-ui-custom:tabs
	names="<%=tabNames%>"
	tabsValues="<%=tabValues.toString()%>"
	value="<%=tabValue%>" 
	url="<%= portletURL.toString() %>"
	param="tab"
 />
		
<c:choose>
	<c:when test='<%= tabValue.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp"/>
	</c:when>
	<c:when test='<%= tabValue.equals("cta_bancaria") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/cta_bancaria_reclamo.jsp"/>
	</c:when>
	<c:when test='<%= tabValue.equals("archivos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/reclamo_prestacional_imagen.jsp"/>
	</c:when>
	<c:when test='<%= tabValue.equals("historico_movimientos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/historico_movimientos.jsp"/>
	</c:when>	
</c:choose>
<!--  /form-->

<script type="text/javascript">

function <portlet:namespace />uploadImagenReclamoPrestacional(solapa) {
	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_reclamosprestacionales';
	
	document.<portlet:namespace />reclamo_fm.method = 'post';
	url = url+'&imagen=<%=Constants.ADD%>';
	url = url+'&nrsolicitud=<%=idReclamoString%>';
	url = url+'&solapa=' + solapa;
 	submitForm(document.<portlet:namespace />reclamo_fm, url);
}

function verImagenReclamoPrestacional(folderId,fileName){
	
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/autorizaciones/documentacion_adjunta_recuperar"/>'+
	   '<liferay-portlet:param name="name" value="__Name"/>'+
	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	   '</liferay-portlet:actionURL>';      
	   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
	   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
	}

function deleteImagenReclamoPrestacional(folderId,fileName,solapa) {
	var confirmar=false;
	<%
	if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW) ){%>
		alert ( 'Se encuentra en modo consulta, no puede eliminar este archivo.');
		return false;
	<%}	%>
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_reclamosprestacionales';					
		document.<portlet:namespace />reclamo_fm.method = 'post';
        url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		url = url + '&solapa=' + solapa;
		submitForm(document.<portlet:namespace />reclamo_fm, url);
	}else{
		return false;
	}	
}

</script>

