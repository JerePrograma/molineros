<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@include file="/html/portlet/seccional/init.jsp"%>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<portlet:defineObjects />

<%  
    PortletURL portletURL = renderResponse.createRenderURL();
    String portlet_name = ParamUtil.getString(request, "portlet_name");
    if (portlet_name == null || portlet_name.trim().equals("")){
	    /* portlet_name = "seccionales"; */
	    portlet_name = "sec";
    }

    Integer idSeccional = (Integer) request.getAttribute("id_seccional"); 
%>

<form action="" method="post" name="<portlet:namespace />fm_img" enctype="multipart/form-data">	

	
<fieldset class="block-labels"><legend>Imagenes de la Seccional</legend>

<h1>Seccional <%=idSeccional%> </h1>

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
         <input id="<portlet:namespace />uploadIMGSeccional"
				value="<liferay-ui:message key="subir-archivo"/>"
				title="<liferay-ui:message key="subir-archivo" />"
				onClick="javascript: <portlet:namespace />uploadImagenSeccional();"
				type="button" /> 
    
    </td>					
   </tr>
   <tr>
   		<td>&nbsp;</td>
   </tr>
</table>
</fieldset>

<div id="<portlet:namespace />listado_imagenes_seccional  ">
				<jsp:include page='/html/portlet/seccional/gestion_sec_imagenes_search_documentos.jsp' />  
</div>

<input type="hidden" name="<portlet:namespace />id_seccional" id="<portlet:namespace />id_seccional" value="<%=idSeccional%>" />
	
</form>			

<script type="text/javascript" >
function <portlet:namespace />uploadImagenSeccional() {	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_seccional';
	document.<portlet:namespace />fm_img.method = 'post';
	url = url+'&imagen'+'='+'<%=Constants.ADD%>'+'&id_seccional=<%=idSeccional %>';
	submitForm(document.<portlet:namespace />fm_img, url);
}

function verImagenSeccional(folderId,fileName){
   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
   '<liferay-portlet:param name="struts_action" value="/sec/visualizar_img_seccionales"/>'+
   '<liferay-portlet:param name="name" value="__Name"/>'+
   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
   '</liferay-portlet:actionURL>';      
   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function deleteImagenSeccional(folderId,fileName) {	
	var confirmar=false;
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){	
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_seccional';						
		document.<portlet:namespace />fm_img.method = 'post';
		url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
		url += "&folderid="+folderId;
		url += "&filename="+fileName;
		submitForm(document.<portlet:namespace />fm_img, url);
	}else{
		return false;
	}	
}

</script>

