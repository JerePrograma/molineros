<%@page import="ar.com.uoma.facturacion.Cliente"%>
<%@ include file="/html/portlet/afiliados/init.jsp" %>

<%
String portlet_name = ParamUtil.getString(request, "portlet_name");
String prefijo = ParamUtil.getString(request, "prefijo","");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

String nroDocNuevo=ParamUtil.getString(request,"nroDocumento");

String esEdicionStr=ParamUtil.getString(request,"esEdicion");
boolean esEdicion = true;
if (esEdicionStr != null && !esEdicionStr.trim().equals("")) {
	if (esEdicionStr.equals("true")){
		esEdicion = true;
	} else {
		esEdicion = false;
	}
}
String cliente_nro_doc=ParamUtil.getString(request,"cliente_nro_doc");
if(cliente_nro_doc==null || cliente_nro_doc.trim().length() == 0){
	cliente_nro_doc = nroDocNuevo;
}
String cliente_apellido=ParamUtil.getString(request,"cliente_apellido");
String cliente_nombre=ParamUtil.getString(request,"cliente_nombre");
%>
<div style="float:left;">

<label><liferay-ui:message key="nro-documento" />:</label>
<input  id="<portlet:namespace />cliente_nro_doc_popup" name="<portlet:namespace />cliente_nro_doc_popup" maxlength="8" size="15" type="text" value="<%=cliente_nro_doc%>" 
		<% if (esEdicion) { %> onBlur="javascript:<portlet:namespace />pierdeFoco();" onKeyUp="javascript:<portlet:namespace />buscarPersFisicaOnDiv(event)" 
		onkeydown="allowOnlyDigits(event);" <%} %>
		<% if (!esEdicion) { %> readonly='readonly'<%} %>/>
		
<label><liferay-ui:message key="apellido" />:</label>
<input id="<portlet:namespace />cliente_apellido_popup" name="<portlet:namespace />cliente_apellido_popup" maxlength="50" size="15" type="text" 
	   value="<%=cliente_apellido%>" 
	   <% if (esEdicion) { %> onKeyUp="javascript:<portlet:namespace />buscarPersFisicaOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFoco();" <%} %>
	   <% if (!esEdicion) { %> readonly='readonly'<%} %>/>
	   
<label><liferay-ui:message key="nombre" />:</label>	   
<input id="<portlet:namespace />cliente_nombre_popup" name="<portlet:namespace />cliente_nombre_popup" maxlength="50" size="15" type="text" 
	   value="<%=cliente_nombre%>" 
	   <% if (esEdicion) { %> onKeyUp="javascript:<portlet:namespace />buscarPersFisicaOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFoco();" <%} %>
	   <% if (!esEdicion) { %> readonly='readonly'<%} %>/>
	   

	<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />grabarCliente();" />	
   	   
</div>

<input id="<portlet:namespace />persfisica_seleccionada_popup" name="<portlet:namespace />persfisica_seleccionada_popup" type="hidden" value=""/>
<input id="<portlet:namespace />persfisica_tipo_popup" name="<portlet:namespace />persfisica_tipo_popup" type="hidden" value="<%=Cliente.TIPOS_CLIENTE.VISITA %>"/>
<input id="<portlet:namespace />persfisica_estado_popup" name="<portlet:namespace />persfisica_estado_popup" type="hidden" value="<%=Cliente.ESTADOS.ALTA%>"/>



</script>