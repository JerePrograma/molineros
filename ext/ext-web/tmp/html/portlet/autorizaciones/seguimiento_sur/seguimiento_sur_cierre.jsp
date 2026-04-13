<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
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
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
%>

<form action="" method="post" name="<portlet:namespace />fmSC">
	
<fieldset class="block-labels"><legend>Cierre Expediente S.U.R</legend>


<table class="lfr-table">		 
	<tr>
		<td><label><liferay-ui:message key="cierre-fecha" />:</label></td>
		 <td colspan="2">
			<liferay-ui:input-date
				dayParam="fechaCierreSurDia"																					
				dayValue="<%=fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
				dayNullable="<%= true %>"
				monthParam="fechaCierreSurMes"
				monthValue="<%=fechaHasta.get(Calendar.MONTH )%>"
				monthNullable="<%= true %>"
				yearParam="fechaCierreSurAnio"
				yearValue="<%=fechaHasta.get(Calendar.YEAR ) %>"
				yearNullable="<%= true %>"
				yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
				yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
				firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>" />
		 </td>      
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>

<table class="lfr-table">	
	<tr>
	     <td>Motivo <select name="<portlet:namespace />motivoCierreSur"
			                   id="<portlet:namespace />motivoCierreSur" 
			                   onchange="">
			                
			                <%for(int i = 0; i < WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES.length; i++ ) {%>
				            <option value="<%=WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES[i][0] %>" 
					          > <%=WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES[i][1] %> </option>
				            <% } %>
		             </select>
		  </td>
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>


<input type="hidden" name="<portlet:namespace />id_seguimiento" id="<portlet:namespace />id_seguimiento" value="<%=id_seguimiento%>" />

<input id="<portlet:namespace />close"
				value="<liferay-ui:message key="close"/>"
				title="<liferay-ui:message key="close" />"
				onClick="javascript: <portlet:namespace />cerrarSeguimiento();"
				type="button" /> 
				
<input type="hidden" name="<portlet:namespace/>validado" id="<portlet:namespace/>validado" value="" />
</form>

<script type="text/javascript">
function <portlet:namespace />cerrarSeguimiento(){
  var motivo =	jQuery('#<portlet:namespace />motivoCierreSur').val();
	  if (("PG"==motivo && <portlet:namespace />validarCierreSeguimientoSur()) || "PG"!=motivo) {
			var params = "&<%= Constants.CMD %>=" + "<%= Constants.EXPIRE %>";
			params += "&id_seguimiento="+<%=id_seguimiento%>;
			params += "&fechaCierreSurDia="+jQuery('#<portlet:namespace />fechaCierreSurDia').val();
			params += "&fechaCierreSurMes="+jQuery('#<portlet:namespace />fechaCierreSurMes').val();
			params += "&fechaCierreSurAnio="+jQuery('#<portlet:namespace />fechaCierreSurAnio').val();
			params += "&motivoCierreSur="+motivo;
			
			url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/editar_seguimientosur';
			url = url + params;
			jQuery('#<portlet:namespace />listado_seguimientoSur').load(url);
			<portlet:namespace />cerrarSeguimientoSur();
	  }
  return false;		
}


function <portlet:namespace />verificaCierreSeguimientoSur(){
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/verificar_cierre_seguimientosur&expediente=<%=seguimiento.getNro_expediente()%>';		 
	 jQuery.ajax({   
				url: url,
				async: false,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					if(obj.validado=="1"){
						jQuery('#<portlet:namespace/>validado').val("OK");
					
					}else{
						alert("<liferay-ui:message key='seguimientosur-movbanco-inexistente'/>");
						jQuery('#<portlet:namespace/>validado').val("NO");
					}
				}
	 }); 
}

function <portlet:namespace />validarCierreSeguimientoSur(){
	<portlet:namespace />verificaCierreSeguimientoSur();
	var esValido=jQuery('#<portlet:namespace/>validado').val();
	if(esValido=="OK"){
	   return true;	
	}else{
	   return false;	
	}
}

</script>

