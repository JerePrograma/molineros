<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/afiliados/init.jsp"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%  
    PortletURL portletURL = renderResponse.createRenderURL();
    String portlet_name = ParamUtil.getString(request, "portlet_name");
    if (portlet_name == null || portlet_name.trim().equals("")){
	    portlet_name = "afiliados";
    }
    Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
    String cuit_afiliado=afiliado!=null && afiliado.getCuil()!= null ?afiliado.getCuil() :"";
    Integer inte_afiliado=afiliado!=null  ?(int)afiliado.getInte() :0;
%>

<!-- form -->

	
<fieldset class="block-labels"><legend>Imagenes Afiliado</legend>

<h1>Afiliado <%=afiliado.getCuil()%></h1>

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
				<jsp:include page='/html/portlet/afiliados/afiliado_imagenes_search_documentos.jsp' />  
</div>

<input type="hidden" name="<portlet:namespace />cuit_afiliado" id="<portlet:namespace />cuit_afiliado" value="<%=cuit_afiliado%>" />
<input type="hidden" name="<portlet:namespace />inte_afiliado" id="<portlet:namespace />inte_afiliado" value="<%=inte_afiliado%>" />
	
<!-- /form -->			


