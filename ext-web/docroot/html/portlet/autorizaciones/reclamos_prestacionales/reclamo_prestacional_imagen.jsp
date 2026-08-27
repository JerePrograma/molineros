<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>

<%@ page import="ar.com.ospim.global.beans.Comprobante" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes" %>

<%
boolean showReadOnlyReclamPrestac=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CONSULTA_RECLAMOS_PRESTACIONALES);
%>

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
    
    
//Prueba Insercion IMAGENES des comprobantes    
    Comprobante comprobante=new Comprobante();
    try{
       PrestacionesReclamo presta =reclamoprestacional.getPrestaciones().get(0);
    
       comprobante.setCuit(presta.getComprobanteCUIT());
       comprobante.setTipoComprobante(presta.getComprobanteTipo());
       comprobante.setLetraComprobante(presta.getComprobanteLetra());
       comprobante.setPtoVenta(Integer.valueOf(presta.getComprobanteSucursal()));
       comprobante.setNroComprobante(presta.getComprobanteNro());
    }catch(Exception e){} 
    request.getSession().setAttribute(WebKeysComprobantes.COMPROBANTE_IMAGEN_VIEW,comprobante);
//Fin
    
%>

<!-- form -->
<form action="UploadImagenesReclamosAction" method="post" name="<portlet:namespace />reclamo_fm" id="<portlet:namespace />reclamo_fm" enctype="multipart/form-data">	

	
<fieldset class="block-labels"><legend>Imagenes Reclamo Prestacional</legend>

<h1>Reclamo Prestacional Nro <%=cuit_afiliado%></h1>

<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />


<table class="lfr-table">
  <tr>
<%if (modoConsulta!="si") {%>
    <%if (!showReadOnlyReclamPrestac) {%>

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

<input  type = 'hidden' name="<portlet:namespace />cuit_afiliado" id="<portlet:namespace />cuit_afiliado" value="<%=cuit_afiliado%>" />
<input  type = 'hidden'  name="<portlet:namespace />inte_afiliado" id="<portlet:namespace />inte_afiliado" value="<%=inte_afiliado%>" />

<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/documentacion_compras.jsp" %>

<fieldset class="block-labels">
			<legend>
					<label>Imágenes desde Portal de Proveedores:</label>
			</legend>
		
		     <jsp:include page='/html/portlet/comprobantes/comprobante_search_documentos.jsp' />  	
</fieldset>


</form>	
<!-- /form -->			


