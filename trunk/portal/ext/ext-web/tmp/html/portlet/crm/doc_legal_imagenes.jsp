<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%-- <%@ include file="/html/portlet/afiliados/init.jsp"%> --%>
<%@ include file="/html/portlet/crm/init.jsp"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%  
	String accion = (String)request.getAttribute(Constants.CMD);

    PortletURL portletURL = renderResponse.createRenderURL();
    
    String portlet_name =  "";
    if(renderResponse.getNamespace().equals("_JUD_1_")){
 	   portlet_name = "judicial";
     }else{
 	   portlet_name = "afiliados";
     }
    
    
    DocumentoLegalCRM docLegal = null;
    
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
    if(esView){
    	docLegal = (DocumentoLegalCRM) request.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_VIEW);
	}else{
		docLegal = (DocumentoLegalCRM) request.getSession().getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
	}

%>
	
<fieldset class="block-labels"><legend><label><liferay-ui:message key="imagenes-afiliados" />:</label></legend>

<form action="" method="post" name="<portlet:namespace />fmUI" id="<portlet:namespace />fmUI" enctype="multipart/form-data">	

<%if(docLegal!=null){ %>
<h1>Afiliado: <%=docLegal.getAfiliado().getApellido().trim() + ", " + docLegal.getAfiliado().getNombre().trim() +" Grupo Familiar: " +docLegal.getAfiliado().getCuil_titular()+"/"+docLegal.getAfiliado().getInte()%></h1>
<%}else{ %>
<h1>Por favor completar los datos del afiliado y guardar primero, luego podrá adjuntar imágenes</h1>
<%} %>
<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
	
<table class="lfr-table">
  <input type="hidden" id="<%=Constants.CMD%>" name="<%=Constants.CMD%>" value="<%=accion%>" />
  <input type="hidden" id="id" name="id" value="<%=docLegal!=null && docLegal.getId()!=null?docLegal.getId():0%>" />	
  <tr>
	    <td><label><liferay-ui:message key="add-imagenes-afiliados" />:</label></td>
		<td><input type="file" name="importa_imagenes" id="importa_imagenes"/></td>
		<td>&nbsp;</td>
	    <td><label><liferay-ui:message key="descripcion" />:</label></td>
					<td><input id="<portlet:namespace />descripcionFile"
						name="<portlet:namespace />descripcionFile" size="90"
						maxlength="120" type="text"
						value='' /></td>
	    <%if(docLegal!=null){ %>
	    <td>
	         <input id="<portlet:namespace />uploadIMGAfiliado"
					value="<liferay-ui:message key="subir-archivo"/>"
					title="<liferay-ui:message key="subir-archivo" />"
					onClick="javascript: <portlet:namespace />uploadDocumLegalAfiliado();"
					type="button" /> 
	    
	    </td>
	    <%}%>					
   </tr>
   <tr>
   		<td>&nbsp;</td>
   </tr>
</table>

   
<div id="<portlet:namespace />listado_imagenes_afiliado">
			<liferay-util:include page="/html/portlet/crm/doc_legal_imagenes_search_documentos.jsp">
	        	<liferay-util:param name="es_view" value="<%=String.valueOf(esView)%>"/>
	        </liferay-util:include>	 
</div>

</form>
</fieldset>

<script type="text/javascript">

<c:if test="<%= docLegal != null %>">
function <portlet:namespace />uploadDocumLegalAfiliado() {	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_doc_legal_afiliado';
	document.<portlet:namespace />fmUI.method = 'post'; 
	url = url+'&imagen='+'<%=Constants.ADD%>'+'&cmd_crm_carga='+'<%=accion%>';
	submitForm(document.<portlet:namespace />fmUI, url);
}

function verImagenAfiliado(folderId,fileName){
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/__portlet/documentacion_adjunta_recuperar"/>'+
	   '<liferay-portlet:param name="name" value="__Name"/>'+
	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	   '</liferay-portlet:actionURL>';      
	   url = url.replace("__Name",fileName).replace("__FolderId",folderId).replace("__portlet","<%=portlet_name%>");
	   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function deleteImagenAfiliado(folderId,fileName) {	
	var confirmar=false;
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){	
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_doc_legal_afiliado';						
		/* document.<portlet:namespace />fmU.method = 'post'; */
		url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		submitForm(document.<portlet:namespace />fmUI, url);
	}else{
		return false;
	}	
}
</c:if>
</script>
