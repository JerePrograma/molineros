<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");


	int id_tratamiento = ParamUtil.getInteger(request,
		"id_tratamiento", 0);

	AutorizacionPrestacional tratamiento = null;
	tratamiento = AutorizacionPrestacionalServiceUtil
			.getAutorizacionPrestacional(id_tratamiento);
	
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	String tabValue = ParamUtil.getString(request, "tab", null); 
	String cmd = (String) request.getAttribute(Constants.CMD);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	String desdeResult = (String) request.getSession().getAttribute("desde_result");
%>

<form action="" method="post" name="<portlet:namespace />fm" enctype="multipart/form-data">
	
<fieldset class="block-labels"><legend>Imagenes Autorización</legend>

<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
<liferay-ui:error key="errorAfiliadoNull" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />

<h1> Nro. Autorización  <%=tratamiento.getNroAutorizacion()%></h1>

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
    

         <input id="<portlet:namespace />uploadPreautorizacion"
				value="<liferay-ui:message key="subir-archivo"/>"
				title="<liferay-ui:message key="subir-archivo" />"
				onClick="javascript: <portlet:namespace />uploadImagenAutorizacion();"
				type="button" />
			 
    
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

 
<div id="<portlet:namespace />listado_imagenes_autorizaciones">
 
				<jsp:include page='/html/portlet/autorizaciones/autorizaciones_prestacionales/autorizacion_search_documentos.jsp' />
				  
</div>
<br>
<br>
<br>
<h1> Lista de imagenes de pre autorizaciones</h1>


<div id="<portlet:namespace />listado_imagenes_pre_autorizaciones">
 
				<jsp:include page='/html/portlet/autorizaciones/autorizaciones_prestacionales/preautorizacion_search_documentos.jsp' />
				  
</div>






<input type="hidden" name="<portlet:namespace />id_preautorizacion" id="<portlet:namespace />id_preautorizacion" value="<%="A"%>" />
<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
<input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 
</form>

<script type="text/javascript">

function <portlet:namespace />uploadImagenAutorizacion() {	
	var descripcionImagen =jQuery('#<portlet:namespace />descripcionFile').val();
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_autorizaciones';						
	document.<portlet:namespace />fm.method = 'post';

	url = url+'&<%=Constants.UPDATE %>'+'='+'<%=Constants.ADD%>'+'&id_tratamiento=<%=id_tratamiento%>'
	if(!descripcionImagen.length > 0){        	
		alert("Debe ingresar una descripción");
		return;
    }
	submitForm(document.<portlet:namespace />fm, url);

}

function verImagenAutorizacion(folderId,fileName){
	var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/autorizaciones/documentacion_adjunta_recuperar"/>'+
	'<liferay-portlet:param name="name" value="__Name"/>'+
	'<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	'</liferay-portlet:actionURL>';      
	 url = url.replace("__Name",fileName).replace("__FolderId",folderId);
	
	 window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function deleteImagenAutorizacion(folderId,fileName) {	
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_autorizaciones';						
	document.<portlet:namespace />fm.method = 'post';
	url = url+'&<%= Constants.UPDATE %>'+'='+'<%=Constants.DELETE %>';
	url += '&id_tratamiento'+'='+'<%=id_tratamiento%>'
	url += "&folderid="+folderId;
	url += "&filename="+fileName;
	submitForm(document.<portlet:namespace />fm, url);
}


function verImagenPreautorizacion(folderId,fileName){
	
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/autorizaciones/documentacion_adjunta_recuperar"/>'+
	   '<liferay-portlet:param name="name" value="__Name"/>'+
	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	   '</liferay-portlet:actionURL>';      
	    url = url.replace("__Name",fileName).replace("__FolderId",folderId);
	    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
	}


</script>
