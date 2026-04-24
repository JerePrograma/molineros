<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />
<%
ReclamoPrestacional  reclamoprestacional  = (ReclamoPrestacional)request.getSession().getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
String tabNames="" ;

StringBuilder tabValues = new StringBuilder("datos");


String cmd = (String) request.getAttribute(Constants.CMD);


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

if (idReclamoAux == 0 ){
	tabNames="Datos Generales" ;
	tabValues.append("datos");
	
}else{
	 tabNames="Datos Generales,CTA Bancaria, Archivos" ;
	 tabValues.append(",cta_bancaria");
	 tabValues.append(",archivos");
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/autorizaciones/editar_reclamosprestaciones_seccional_entry");
portletURL.setParameter("tab", tabValue);
portletURL.setParameter("cmd", cmd);
portletURL.setParameter("reclamo_id", String.valueOf(idReclamoAux));
portletURL.setParameter(Constants.ACTION, WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL);





%>
<liferay-ui:error key="error-estado-reclamo" message="falta-estado-reclamo-prestacion" />
<liferay-ui:error key="error-fechaseccional-reclamo" message="falta-fechaseccional-reclamo-prestacion" />
<liferay-ui:error key="error-fechaingresoospim-reclamo" message="falta-fechaingresoospim-reclamo-prestacion" />
<liferay-ui:error key="error-cargos-reclamo" message="completar-area-medica" />
<liferay-ui:error key="error-comprobante-invalido" message="tipo-comprobante-invalido" />
<liferay-ui:error key="error-comprobante-duplicado" message="comprobante-duplicado" />
<liferay-ui:error key="error-afi-baja-reclamo" message="afiliado-dado-de-baja" />


<liferay-ui:error key="error-enviar-mail"
	message="<%=(String)request.getAttribute(\"msg-error-enviar-mail\") %>" />
	
<liferay-ui:error key="error-enviar-mail_1"
	message="<%=(String)request.getAttribute(\"msg-error-enviar-mail_1\") %>" />
	
<liferay-ui:error key="error-enviar-mail_2"
	message="<%=(String)request.getAttribute(\"msg-error-enviar-mail_2\") %>" />
	
<liferay-ui:error key="error-enviar-mail_3"
	message="<%=(String)request.getAttribute(\"msg-error-enviar-mail_3\") %>" />


<form action="" method="post" method="post" name="<portlet:namespace />reclamo_fm" id="<portlet:namespace />reclamo_fm"  enctype="multipart/form-data">		
	
	
<liferay-ui-custom:tabs
	names="<%=tabNames%>"
	tabsValues="<%=tabValues.toString()%>"
	value="<%=tabValue%>" 
	url="<%= portletURL.toString() %>"
	param="tab"
	  />
		
<c:choose>
	<c:when test='<%= tabValue.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_seccional.jsp"/>
	</c:when>
	<c:when test='<%= tabValue.equals("cta_bancaria") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/cta_bancaria_reclamo_seccional.jsp"/>
	</c:when>
	<c:when test='<%= tabValue.equals("archivos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/reclamo_prestacional_seccional_imagen.jsp"/>
	</c:when>	                                                                         
</c:choose>

</form>


<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">
		Liferay.Util.focusFormField(document.<portlet:namespace />reclamo_fm.<portlet:namespace />name);
	</script>
</c:if>

<script type="text/javascript">



function <portlet:namespace />uploadImagenReclamoPrestacional(solapa) {
	
	var accion = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_reclamosprestacionales_seccional';
	
	document.<portlet:namespace />reclamo_fm.method = 'post';
	url = url+'&imagen=<%=Constants.ADD%>';
	url = url+'&nrsolicitud=<%=idReclamoString%>';
	url = url+'&solapa=' + solapa;
	url = url + accion;
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
	var accion = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
	<%
	if (cmd != null && cmd.equalsIgnoreCase(Constants.VIEW) ){%>
		alert ( 'Se encuentra en modo consulta, no puede eliminar este archivo.');
		return false;
	<%}	%>
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_reclamosprestacionales_seccional';					
		document.<portlet:namespace />reclamo_fm.method = 'post';
        url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		url = url+'&solapa=' + solapa;
		url = url + accion;
		submitForm(document.<portlet:namespace />reclamo_fm, url);
	}else{
		return false;
	}	
}



</script>

