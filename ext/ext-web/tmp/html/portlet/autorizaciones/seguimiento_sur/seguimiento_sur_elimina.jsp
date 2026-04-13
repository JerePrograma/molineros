<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	int id_seguimiento=seguimiento!=null && seguimiento.getId()!= null ?(int)seguimiento.getId():0;
	
%>

<form action="" method="post" name="<portlet:namespace />fmSB">
	
<table class="lfr-table">	
	<tr>
	     <td>Motivo </td>
		  
		  <td colspan="5"><textarea rows="3" cols="50" maxlength="20000" 
						id="<portlet:namespace />motivoBajaSur" 
						name="<portlet:namespace />motivoBajaSur"
						style="resize: none;"></textarea>
		 </td>	
		  
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>


<input type="hidden" name="<portlet:namespace />id_seguimiento" id="<portlet:namespace />id_seguimiento" value="<%=id_seguimiento%>" />

<input id="<portlet:namespace />save"
				value="<liferay-ui:message key="save"/>"
				title="<liferay-ui:message key="save" />"
				onClick="javascript: <portlet:namespace />eliminarSeguimiento();"
				type="button" /> 
				
</form>

<script type="text/javascript">
function <portlet:namespace />eliminarSeguimiento(){
	  var motivo =	jQuery('#<portlet:namespace />motivoBajaSur').val();
	  var params = "&<%= Constants.CMD %>=" + "eliminaSeguimiento";
	  params += "&id_seguimiento="+<%=id_seguimiento%>;
	  params += "&motivobajasur="+encodeURI(motivo);
				
	  url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/editar_seguimientosur';
	  url = url + params;
	  jQuery('#<portlet:namespace />listado_seguimientoSur').load(url);
	  <portlet:namespace />cerrarSeguimientoSur();
	  return false;		
	}

</script>

