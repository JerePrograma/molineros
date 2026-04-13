<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	DemandaJudicial demanda=(DemandaJudicial)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
//	String nroSolicitud=ParamUtil.getString(request, "nroSolicitud");;
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "estudio_isidro";
	}
	
	int id_demanda=demanda!=null && demanda.getId()!= null ?(int)demanda.getId():0;
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	String tabValue = ParamUtil.getString(request, "tab", null); 
	String cmd = (String) request.getAttribute(Constants.CMD);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	String titulo=demanda!=null && demanda.getId()!=null? 
			       demanda.getId().toString() + "-" + demanda.getCuit() + 
			       " -(" +(demanda.getSucursal()!=null?demanda.getSucursal() :"") +") " +
			       (demanda.getRazonSocial()!=null?demanda.getRazonSocial().toUpperCase():"")
			       :"";
	
	//String desdeResult = (String) request.getSession().getAttribute("desde_result");
%>

<form action="" method="post" name="<portlet:namespace />fmSI" enctype="multipart/form-data">
	
<fieldset class="block-labels"><legend>Imagenes Demanda</legend>

<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
<liferay-ui:error key="errorAfiliadoNull" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />

<h1>Demanda Nro. <%=titulo%></h1>

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
    
     <%if(demanda!=null && demanda.getId()!=null){%>
         <input id="<portlet:namespace />uploadImagenDemanda"
				value="<liferay-ui:message key="subir-archivo"/>"
				title="<liferay-ui:message key="subir-archivo" />"
				onClick="javascript:uploadImagenDemanda();"
				type="button" />
	  <%}%>			
    
    </td>	
    <td>&nbsp;</td>			
   </tr>
   <tr>
     <td colspan="4">
       <label style="color:blue"> La imagen no debe superar los 5 MB de tamaño</label>
       <br>
       <label style="color:blue"> Los nombres de los archivos no deben contener caracteres especiales ( / : @ vocales acentuadas ) </label>
     </td>
   </tr>
</table>
</fieldset>

 
<div id="<portlet:namespace />listado_imagenes_demandas">
 
	<jsp:include page='/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_search_documentos.jsp' />		  	
				  
</div>

<input type="hidden" name="<portlet:namespace />id_demanda" id="<portlet:namespace />id_demanda" value="<%=id_demanda%>" />
<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
<input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 
</form>

<script type="text/javascript">



function uploadImagenDemanda() {	
	var descripcionImagen =jQuery('#<portlet:namespace />descripcionFile').val();
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_demandas';						
	document.<portlet:namespace />fmSI.method = 'post';

	url = url+'&<%= Constants.CMD %>'+'=add'+'&id_demanda=<%=demanda!=null &&  demanda.getId()!=null? demanda.getId().toString():"" %>'
	if(!descripcionImagen.length > 0){        	
		alert("Debe ingresar una descripción");
		return;
    }
	submitForm(document.<portlet:namespace />fmSI, url);
}

function verImagenDemanda(folderId,fileName){
   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
   '<liferay-portlet:param name="struts_action" value="/estudio_isidro/documentacion_adjunta_recuperar"/>'+
   '<liferay-portlet:param name="name" value="__Name"/>'+
   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
   '</liferay-portlet:actionURL>';      
   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
   
    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function deleteImagenDemanda(folderId,fileName) {
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_demandas';						
	document.<portlet:namespace />fmSI.method = 'post';
	url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE %>';
	url += "&folderid="+folderId;
	url += "&accion=delete";
	url += "&filename="+fileName;
	submitForm(document.<portlet:namespace />fmSI, url);
}



</script>
