<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>


<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%  
    PortletURL portletURL = renderResponse.createRenderURL();
    String portlet_name = ParamUtil.getString(request, "portlet_name");
    if (portlet_name == null || portlet_name.trim().equals("")){
	    portlet_name = "autorizaciones";
    }
    ReclamoPrestacional reclamoprestacional = (ReclamoPrestacional)session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
      
    String cuit_afiliado=reclamoprestacional!=null && String.valueOf(reclamoprestacional.getId_reclamo()) != null ?String.valueOf(reclamoprestacional.getId_reclamo()) :"ZZZZZZZZZZZZ";
    Integer inte_afiliado=reclamoprestacional!=null  ?(int)reclamoprestacional.getId_reclamo()  :0;
    
    String modoConsulta = (String) request.getAttribute("ModoConsulta");
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	request.setAttribute("solapa_cuenta", "archivos"); 
	

%>

<!-- form -->
<form action="UploadImagenesReclamosAction" method="post" name="<portlet:namespace />reclamo_fm" id="<portlet:namespace />reclamo_fm" enctype="multipart/form-data">	

	
<fieldset class="block-labels"><legend>Imagenes Reclamo Prestacional</legend>

<h1>Reclamo Prestacional Nro <%=cuit_afiliado%></h1>

<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />


<table class="lfr-table">
  <tr>
<%if (modoConsulta!="si") {%>
    <td> Añadir Imagenes:</td>
	<td><input type="file" name="importa_imagenes" id="importa_imagenes"/></td>
	<td>&nbsp;</td>
    <td><label><liferay-ui:message key="descripcion" />:</label></td>
				<td><input id="<portlet:namespace />descripcionFile"
					name="<portlet:namespace />descripcionFile" size="90"
					maxlength="120" type="text"
					value='' /></td>
    <td>
         <input id="<portlet:namespace />uploadIMGAfiliado"
				value="<liferay-ui:message key="subir-archivo"/>"
				title="<liferay-ui:message key="subir-archivo" />"
				onClick="javascript: <portlet:namespace />uploadImagenReclamoPrestacional('archivos');"
				type="button" />
    </td>					
     <td>
     <%if (reclamoprestacional != null  ){ %>
       <input type="button" value="<liferay-ui:message key="back" />"
	   onClick="<portlet:namespace />anteriorSolapa();" />
	 <%}%>  
    </td>	
   
<%}else{%>
    <td> <b>Solo Consulta</b></td>
<%}%>
   </tr>
   <tr>
   <td>&nbsp;</td>
   </tr>
</table>

</fieldset>
   
<div id="<portlet:namespace />listado_imagenes_afiliado">
				<jsp:include page='/html/portlet/autorizaciones/reclamos_prestacionales/reclamo_prestacional_imagenes_search_documentos.jsp' />  
</div>



	
	    <input id="<portlet:namespace />eMail"
		value="<liferay-ui:message key="email-large"/>"
		title="<liferay-ui:message key="email-large" />"
		onClick="javascript: <portlet:namespace />emailPreCarga(<%=reclamoprestacional.getId_String()%>);"
		type="button" 
		 />
		     
<% if(reclamoprestacional != null &&  reclamoprestacional.getId_reclamo() != 0 && reclamoprestacional.getFechaMailSeccional()  != null){%>

         <table>
	     <tr>
		     <td colspan="1">&nbsp;</td> 
         </tr>
	     <tr>
	     <td>
		 <label><font size=3 color="#0000ff">Email enviado <%=sdf.format(reclamoprestacional.getFechaMailSeccional())%></font></label>
		 </td>
		 <td colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
         </tr>		 
		 </table>

<%}%>

<input  type = 'hidden' name="<portlet:namespace />cuit_afiliado" id="<portlet:namespace />cuit_afiliado" value="<%=cuit_afiliado%>" />
<input  type = 'hidden'  name="<portlet:namespace />inte_afiliado" id="<portlet:namespace />inte_afiliado" value="<%=inte_afiliado%>" />

</form>	
<!-- /form -->			


<script type="text/javascript">

	
	function <portlet:namespace />anteriorSolapa() {	
		
		var accion = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";

		var accionEnCurso ="";
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=cta_bancaria';
			url += '&<%= Constants.CMD %>'+'='+'<%=Constants.MOVE %>'
			url = url + accion;
			document.<portlet:namespace />reclamo_fm.method = 'post';
			submitForm(document.<portlet:namespace />reclamo_fm, url);
	}
	
	
	

	function <portlet:namespace />emailPreCarga(id_reclamo){	

		var accion = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
		
		
		 var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" />'+
			'<liferay-portlet:param name="cmd" value="email"/>'+
			'<liferay-portlet:param name="id_reclamosel" value="__id_reclamosel"/>'+
			'<liferay-portlet:param name="tab_seleccionada" value="archivos"/>'+

			'<liferay-portlet:param name="view" value="__view"/>'+
			'</liferay-portlet:renderURL>';
			
			
			 url = url.replace("__id_reclamosel",id_reclamo);
			 url = url + accion;

			 document.<portlet:namespace />reclamo_fm.method = 'post';
			 submitForm(document.<portlet:namespace />reclamo_fm, url); 
    }
		    
		
		
   


</script>



