<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
	String nroSolicitud=ParamUtil.getString(request, "nroSolicitud");;
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	int id_seguimiento=seguimiento!=null && seguimiento.getId()!= null ?(int)seguimiento.getId():0;
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
%>

<form action="" method="post" name="<portlet:namespace />fmSI" enctype="multipart/form-data">
	
<fieldset class="block-labels"><legend>Imagenes Expediente S.U.R</legend>

<h1>Seguimiento Nro <%=seguimiento.getId_tipo_expediente_nro().toString() + "-" + seguimiento.getClaseExpediente().toUpperCase() %></h1>

<table class="lfr-table">
  <tr>

    <td> Añadir Imagenes:</td>
	<td><input type="file" name="importa_imagenes" id="importa_imagenes"/></td>
	<td>&nbsp;</td>
    <td><label><liferay-ui:message key="descripcion" />:</label></td>
				<td><input id="<portlet:namespace />descripcionFile"
					name="<portlet:namespace />descripcionFile" size="90"
					maxlength="120" type="text"
					value='' /></td>
    <td>
         <input id="<portlet:namespace />uploadSUR"
				value="<liferay-ui:message key="subir-archivo"/>"
				title="<liferay-ui:message key="subir-archivo" />"
				onClick="javascript: <portlet:namespace />uploadImagenSeguimiento();"
				type="button" /> 
    
    </td>					
   </tr>
   <td>&nbsp;</td>
</table>
</fieldset>

 
<div id="<portlet:namespace />listado_imagenes_seguimientoSur">
				<jsp:include page='/html/portlet/autorizaciones/seguimiento_sur/seguimiento_sur_search_documentos.jsp' />  
</div>

<input type="hidden" name="<portlet:namespace />id_seguimiento" id="<portlet:namespace />id_seguimiento" value="<%=id_seguimiento%>" />

	
			
</form>

<script type="text/javascript">

function <portlet:namespace />uploadImagenSeguimiento() {	
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_seguimientosur';						
	document.<portlet:namespace />fmSI.method = 'post';
	url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.ADD%>'+'&nrsolicitud=<%=seguimiento.getId_tipo_expediente_nro().toString() + "-" + seguimiento.getClaseExpediente().toUpperCase()%>'
	submitForm(document.<portlet:namespace />fmSI, url);
}

function verImagenSeguimiento(folderId,fileName){
   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
   '<liferay-portlet:param name="struts_action" value="/autorizaciones/documentacion_adjunta_recuperar"/>'+
   '<liferay-portlet:param name="name" value="__Name"/>'+
   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
   '</liferay-portlet:actionURL>';      
   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
   
    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function deleteImagenSeguimiento(folderId,fileName) {	
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_seguimientosur';						
	document.<portlet:namespace />fmSI.method = 'post';
	url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE %>'
	url += "&folderid="+folderId;
	url += "&filename="+fileName;
	submitForm(document.<portlet:namespace />fmSI, url);
}

</script>
