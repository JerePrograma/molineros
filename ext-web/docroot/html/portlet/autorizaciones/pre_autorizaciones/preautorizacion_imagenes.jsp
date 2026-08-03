<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	PreAutorizacion preautorizacion=(PreAutorizacion)request.getSession().getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
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
		portlet_name = "autorizaciones";
	}
	
	int id_preautorizacion=preautorizacion!=null && preautorizacion.getId()!= null ?(int)preautorizacion.getId():0;
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	String tabValue = ParamUtil.getString(request, "tab", null); 
	String cmd = (String) request.getAttribute(Constants.CMD);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	String desdeResult = (String) request.getSession().getAttribute("desde_result");
%>

<form action="" method="post" name="<portlet:namespace />fmSI" enctype="multipart/form-data">
	
<fieldset class="block-labels"><legend>Imagenes Preautorización</legend>

<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
<liferay-ui:error key="errorAfiliadoNull" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />

<h1>Preautorización Nro. <%=preautorizacion!=null && preautorizacion.getId()!=null? preautorizacion.getId().toString() + "-" + preautorizacion.getAfiliado().getApeNombre().toUpperCase() :""%></h1>

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
    
    <%if (preautorizacion != null && preautorizacion.getId() != null && esEdicion 
    		&& !((preautorizacion.getUltimoEstado().getId().equalsIgnoreCase("AU") 
    						|| preautorizacion.getUltimoEstado().getId().equalsIgnoreCase("RE")))){ %>
         <input id="<portlet:namespace />uploadPreautorizacion"
				value="<liferay-ui:message key="subir-archivo"/>"
				title="<liferay-ui:message key="subir-archivo" />"
				onClick="javascript: <portlet:namespace />uploadImagenPreautorizacion();"
				type="button" />
	<%}%>			 
    
    </td>	
    <td>
     <%if (preautorizacion != null && preautorizacion.getId() != null && !"SI".equalsIgnoreCase(desdeResult)){ %>
       <input type="button" value="<liferay-ui:message key="back" />"
	   onClick="<portlet:namespace />anteriorSolapa();" />
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

 
<div id="<portlet:namespace />listado_imagenes_preautorizacion">
 
				<jsp:include page='/html/portlet/autorizaciones/pre_autorizaciones/preautorizacion_search_documentos.jsp' />
				  
</div>

<%
    boolean esDesdeApp = "AppMobile".equalsIgnoreCase(preautorizacion.getAlta_usr());

    boolean imagenesVacias = preautorizacion != null &&
                             (preautorizacion.getImagenes() == null || preautorizacion.getImagenes().isEmpty());

    if (esDesdeApp && imagenesVacias) {
%>
    <br/>
    <input type="button" value="Descargar imágenes APP"
       onclick="javascript:descargarImagenesApp(<%= preautorizacion.getId() %>)"
       style="background-color: #cce5ff; font-weight: bold;" />

        
<%
    }
%>


<%if (preautorizacion != null && preautorizacion.getId() != null 
		&& (preautorizacion.getFechaEmail() == null  ||
		  
		   (  ("OB".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId()) ||
			   "CA".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId())	||
			   "GO".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId())
			  )	   &&  PreAutorizacionServiceUtil.tieneDocumentacionSinEnviar(preautorizacion.getId()) 
				
			  )
		  
		  ) 
		&& ( (preautorizacion.getImagenes().size()>0  
		     && (preautorizacion.getCodigosPresentados().size()>0 || preautorizacion.getMedicamentosPresentados().size()>0)) ||
			 preautorizacion.isAlojamiento()	
		   )
		&& esEdicion
		 /* && preautorizacion.getRequiereAutorizacion()  */ 
	){ %>
	
	    <input id="<portlet:namespace />eMail"
		value="<liferay-ui:message key="email-short"/>"
		title="<liferay-ui:message key="email-short" />"
		onClick="javascript: <portlet:namespace />emailPreautorizacion(<%=preautorizacion.getId()%>);"
		type="button" 
		 />
		     
<%}else if(preautorizacion != null && preautorizacion.getId() != null && preautorizacion.getFechaEmail() != null){%>

         <table>
	     <tr>
		     <td colspan="1">&nbsp;</td>
         </tr>
	     <tr>
	     <td>
		 <label><font size=3 color="#0000ff">Email enviado <%=sdf.format(preautorizacion.getFechaEmail())%></font></label>
		 </td>
		 <td colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		 <td>
<!--  		 
         <%if(preautorizacion != null && preautorizacion.getId() != null && preautorizacion.getFechaEmail2() != null){%>
            <label><font size=3 color="#0000ff">2do Email enviado <%=sdf.format(preautorizacion.getFechaEmail2())%></font></label>
         <%}%>
-->         
         </td>
         </tr>		 
		 </table>

<%}%>

<input type="hidden" name="<portlet:namespace />id_preautorizacion" id="<portlet:namespace />id_preautorizacion" value="<%=id_preautorizacion%>" />
<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
<input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 
</form>

<script type="text/javascript">

function <portlet:namespace />uploadImagenPreautorizacion() {	
	var descripcionImagen =jQuery('#<portlet:namespace />descripcionFile').val();
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_preautorizacion';						
	document.<portlet:namespace />fmSI.method = 'post';
	url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.ADD%>'+'&id_preautorizacion=<%=preautorizacion!=null &&  preautorizacion.getId()!=null? preautorizacion.getId().toString():"" %>'
	if(!descripcionImagen.length > 0){        	
		alert("Debe ingresar una descripción");
		return;
    }
	submitForm(document.<portlet:namespace />fmSI, url);
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

function deleteImagenPreautorizacion(folderId,fileName) {	
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_preautorizacion';						
	document.<portlet:namespace />fmSI.method = 'post';
	url = url+'&<%= Constants.CMD %>'+'='+'<%=Constants.DELETE %>';
	url += "&folderid="+folderId;
	url += "&filename="+fileName;
	submitForm(document.<portlet:namespace />fmSI, url);
}

function <portlet:namespace />anteriorSolapa() {	
	var accionEnCurso ="";
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:actionURL>';
		url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=datos';
		url += '&<%= Constants.CMD %>'+'='+'<%=Constants.MOVE %>'
		url += '&view=<%=esEdicion?"EDIT":"VIEW"%>'
		document.<portlet:namespace />fmSI.method = 'post';
		submitForm(document.<portlet:namespace />fmSI, url);
}

function <portlet:namespace />emailPreautorizacion(id_Preautorizacion){
	jQuery('#<portlet:namespace />eMail').hide();
	
	var noRequiere=false;
	var confirmarEnvio=false;
	var debeConsultar= "<%=!preautorizacion.getCodigosPresentados().isEmpty() %>";
	
	if(debeConsultar){
	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/verificar_prestaciones_preautorizacion_no_requiere_autorizacion';
	   jQuery.ajax({   
      url: url,
      async:false,
      success: function(data){
	    var obj = jQuery.parseJSON(data);
	    var norequiere = obj.norequiere;
	    noRequiere = (norequiere === 'true');
	   }});	
	}
	
    if(noRequiere){
      confirmarEnvio=confirm('Las prestaciones no requieren ser autorizadas. Desea enviarlas IGUALMENTE?');	  
    }
    
    
    if(!noRequiere || confirmarEnvio){
	
	 var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" />'+
	'<liferay-portlet:param name="cmd" value="email"/>'+
	'<liferay-portlet:param name="id_preautorizacion" value="__id_preautorizacion"/>'+
	'<liferay-portlet:param name="tab_seleccionada" value="datos-imagenes"/>'+
	'<liferay-portlet:param name="view" value="__view"/>'+
	'</liferay-portlet:renderURL>';
	
	 var xview ='<%=esEdicion?"EDIT":"VIEW"%>';
	 url = url.replace("__id_preautorizacion",id_Preautorizacion);
	 url = url.replace("__view",xview);
	 document.<portlet:namespace />fmSI.method = 'post';
	 submitForm(document.<portlet:namespace />fmSI, url); 
    }
    
    if(!confirmarEnvio){
    	jQuery('#<portlet:namespace />eMail').show();
    }
}

function descargarImagenesApp(idPreautorizacion) {
    if (confirm("¿Desea descargar las imágenes desde la APP?")) {
        var titulo = "PREAUT_" + idPreautorizacion;

        var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>' +
                  '&struts_action=/<%= portlet_name %>/upload_imagenes_preautorizacion' +
                  '&cmd=descargarImagenes' +
                  '&titulo=' + titulo +
                  '&rnd=' + Math.random();
                  
        window.location.href = url;
    }
}

</script>
