<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/uoma/init.jsp"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%  
 
	String accion = (String)request.getAttribute(Constants.CMD);

	String portlet_name =  "uoma";

	Incidente incidente=(Incidente)request.getAttribute(WebKeysUnidadOperativa.INCIDENTE_EN_EDICION);

    String cuilAfiliado=incidente!=null && incidente.getAfiliado().getCuil_titular()  !=null ?incidente.getAfiliado().getCuil_titular() :"";	
%>


<!-- form -->
<form action="" method="post" name="<portlet:namespace />fmU" id="<portlet:namespace />fmU" enctype="multipart/form-data">
	
<fieldset class="block-labels"><legend>Imágenes caso</legend>

<h1>Afiliado <%=cuilAfiliado%></h1>

<liferay-ui:error key="errorUploadFile" message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
	
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
         <input id="<portlet:namespace />uploadIMGAfiliado"
				value="<liferay-ui:message key="subir-archivo"/>"
				title="<liferay-ui:message key="subir-archivo" />"
				onClick="javascript: <portlet:namespace />uploadImagenAfiliado();"
				type="button" /> 
   </td>					
   </tr>
   <tr>
   <td>&nbsp;</td>
   </tr>
</table>
</fieldset>

   
<div id="<portlet:namespace />listado_imagenes_afiliado">
				<jsp:include page='/html/portlet/uoma/incidente_afiliado_imagenes_search_documentos.jsp' />  
</div>
</form>	

<script type="text/javascript">

<c:if test="<%= incidente != null %>">
function <portlet:namespace />uploadImagenAfiliado() {	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_incidente';
	document.<portlet:namespace />fmU.method = 'post'; 
	url = url+'&imagen='+'<%=Constants.ADD%>'+'&id_incidente='+'<%=incidente.getIdIncidente()%>';
	submitForm(document.<portlet:namespace />fmU, url);
}

function verImagenAfiliado(folderId,fileName){
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/uoma/documentacion_adjunta_recuperar"/>'+
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
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_incidente';						
		/* document.<portlet:namespace />fmU.method = 'post'; */
		url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		url += "&id_incidente="+'<%=incidente.getIdIncidente()%>';
		submitForm(document.<portlet:namespace />fmU, url);
	}else{
		return false;
	}	
}
</c:if>
</script>

<!-- /form -->			


