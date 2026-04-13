<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%-- <%@ include file="/html/portlet/afiliados/init.jsp"%> --%>
<%@ include file="/html/portlet/novedades/init.jsp"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%  
	String accion = (String)request.getAttribute(Constants.CMD);

    PortletURL portletURL = renderResponse.createRenderURL();
    /* String portlet_name = ParamUtil.getString(request, "portlet_name");
    if (portlet_name == null || portlet_name.trim().equals("")){
	    portlet_name = "afiliados";
    } */
    String portlet_name =  "afiliados";
    PreAfiliadoTotal preAfiliado = (PreAfiliadoTotal) session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);
    
    /* portletURL.setParameter("struts_action", "/afiliados/editar_pre_afiliado");
    portletURL.setParameter(Constants.CMD, accion); */
%>
	
<fieldset class="block-labels"><legend><label><liferay-ui:message key="imagenes-afiliados" />:</label></legend>

<form action="" method="post" name="<portlet:namespace />fmU" id="<portlet:namespace />fmU" enctype="multipart/form-data">	

<%if(preAfiliado!=null){ %>
<h1>Afiliado: <%=preAfiliado.getApellido().trim() + ", " + preAfiliado.getNombre().trim() +" CUIL:" +preAfiliado.getCuil()%></h1>
<%}else{ %>
<h1>Por favor completar los datos del afiliado primero, luego podrá adjuntar imágenes</h1>
<%} %>
<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
	
<table class="lfr-table">
  <input type="hidden" id="<%=Constants.CMD%>" name="<%=Constants.CMD%>" value="<%=accion%>" />
  	
  <tr>
	    <td><label><liferay-ui:message key="add-imagenes-afiliados" />:</label></td>
		<td><input type="file" name="importa_imagenes" id="importa_imagenes"/></td>
		<td>&nbsp;</td>
	    <td><label><liferay-ui:message key="descripcion" />:</label></td>
					<td><input id="<portlet:namespace />descripcionFile"
						name="<portlet:namespace />descripcionFile" size="90"
						maxlength="120" type="text"
						value='' /></td>
	    <%if(preAfiliado!=null){ %>
	    <td>
	         <input id="<portlet:namespace />uploadIMGAfiliado"
					value="<liferay-ui:message key="subir-archivo"/>"
					title="<liferay-ui:message key="subir-archivo" />"
					onClick="javascript: <portlet:namespace />uploadImagenAfiliado();"
					type="button" /> 
	    
	    </td>
	    <%}%>					
   </tr>
   <tr>
   		<td>&nbsp;</td>
   </tr>
</table>

   
<div id="<portlet:namespace />listado_imagenes_afiliado">
				<jsp:include page='/html/portlet/novedades/pre_afiliado_imagenes_search_documentos.jsp' />  
</div>

</form>
</fieldset>

<script type="text/javascript">

<c:if test="<%= preAfiliado != null %>">
function <portlet:namespace />uploadImagenAfiliado() {	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_afiliado';
	document.<portlet:namespace />fmU.method = 'post'; 
	url = url+'&imagen='+'<%=Constants.ADD%>'+'&cmd_pre_carga='+'<%=accion%>';
	submitForm(document.<portlet:namespace />fmU, url);
}

function verImagenAfiliado(folderId,fileName){
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/afiliados/documentacion_adjunta_recuperar"/>'+
	   '<liferay-portlet:param name="name" value="__Name"/>'+
	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	   '</liferay-portlet:actionURL>';      
	   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
	   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function deleteImagenAfiliado(folderId,fileName) {	
	var confirmar=false;
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){	
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_afiliado';						
		/* document.<portlet:namespace />fmU.method = 'post'; */
		url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		submitForm(document.<portlet:namespace />fmU, url);
	}else{
		return false;
	}	
}
</c:if>
</script>
