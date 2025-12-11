<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%

PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	Integer estado = ParamUtil.getInteger(request, "estado");
	int id_tratamiento = ParamUtil.getInteger(request,"id_tratamiento", 0);
	
	List<MotivoExcepcion> lista = TraeListasServiceUtil.getMotivosEstadosAutorizacionPrestacional(estado);
	
%>

<form action="" method="post" name="<portlet:namespace />fmSB">

			
					
<table class="lfr-table">	
	<tr>
	     <td>Motivos </td>
		  
		  <td colspan="5">
		 
		   <select name="<portlet:namespace/>motivoEstado" id="<portlet:namespace/>motivoEstado" >
				<%	for (MotivoExcepcion tnom : lista) { %>
						<option value="<%= tnom.getId() %>"><%=tnom.getDescripcion() %></option>
				<%	} %>
		   </select>
		   
		 </td>  
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>

<input id="<portlet:namespace />save"
				value="<liferay-ui:message key="save"/>"
				title="<liferay-ui:message key="save" />"
				onClick="javascript: <portlet:namespace />agregarMotivoEstado();"
				type="button" /> 
				
</form>

<script type="text/javascript">

function <portlet:namespace />agregarMotivoEstado(){
	var mot = jQuery("#<portlet:namespace/>motivoEstado").val();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/editar_autorizacionprestacional_entry';
	url +='&id_tratamiento='+<%=id_tratamiento%>+'&estado='+<%=estado%>+'&accionOriginal=estado'+'&motivo='+encodeURI(mot);
	
	jQuery.ajax({   
		   url: url,
		   async:false,
		   success: function(data){
			<portlet:namespace />cerrarCambiarEstadoTratamientoConMotivo();
    }});
	return false;
}

</script>

