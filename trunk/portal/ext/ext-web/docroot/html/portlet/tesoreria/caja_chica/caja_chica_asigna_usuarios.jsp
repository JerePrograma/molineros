<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@ page import="com.liferay.portal.kernel.util.OrderByComparator"%>
 
<%

String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);

Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
fechaDesde.setTime(new Date());
Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
fechaHasta.setTime(new Date());

List<User> users = UserLocalServiceUtil.search(
		themeDisplay.getCompanyId(), null, Boolean.TRUE, null,
		QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);

%>


<table width="100%">
  <tr>
	<td width="100%" valign="top">
	<table class="lfr-table" width="100%">
	   <tr>
	    <td valign="top" width="30%" >
	     <table>
	       <tr>
	         <td valign="top"><label><liferay-ui:message key="usuario" />:</label></td>
	         <td valign="top">
	              <select name="<portlet:namespace/>usuario_caja_chica" id="<portlet:namespace/>usuario_caja_chica">
		              <option value="">Seleccione un Usuario</option>	       					
							<%for(User u:users) {%>
							   <option value="<%=u.getUserId()%>"><%=u.getFullName()%> </option>
							<%}%>
								
			      </select>
	   
		     </td>
	       </tr>
	       <tr>
		     <td colspan="1">&nbsp;</td>
	       </tr>
	       <tr>
	        <td valign="bottom">
	         <table>
	          <tr>
	            <td>     
		            <input type="button" value="<liferay-ui:message key="assign-users" />" 
		            onClick="<portlet:namespace />agregarCajaChicaUsuario();" />
		        </td>     
		            
		      </tr>       
		     </table>
	        </td>
	       </tr>
	      </table>
	     </td>
	     <td valign="top" colspan="15" width="70%">
				<div align="center" id="<portlet:namespace />usuariosCajaChicaDiv">
					<liferay-util:include page="/html/portlet/tesoreria/caja_chica/caja_chica_asigna_usuarios_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
        </tr>
	</table>
	</td>
  </tr>
</table>

<input type="hidden" name="<portlet:namespace />id_usuario_asignado" id="<portlet:namespace />id_usuario_asignado" value="" />

<script type="text/javascript">
   	function <portlet:namespace />agregarCajaChicaUsuario(){
		var idDetalle=jQuery('#<portlet:namespace />id_usuario_asignado').val();
			
		var usuarioId=jQuery('#<portlet:namespace/>usuario_caja_chica').val();
		var usuarioDescripcion=jQuery('#<portlet:namespace/>usuario_caja_chica').find('option:selected').text();
			
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/caja_chica_agregar_usuario'
			+	'&<%= Constants.CMD%>=' 
				
		if(idDetalle==null || idDetalle =="" || idDetalle==0){
			url += 'adduser'
		}
					
		url += '&usuarioid=' + encodeURI(usuarioId)
				+ '&usuariodescripcion=' + encodeURI(usuarioDescripcion)
				+ '&iddetalle=' + encodeURI(idDetalle)
				+ '&esEdicion=' +"<%=esEdicion%>"; 	
					
				jQuery('#<portlet:namespace />usuariosCajaChicaDiv').load(url, function() {} );
				
	}
	

	function borraCajaChicaUsuario(idMod){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/caja_chica_agregar_usuario'
			+	'&<%= Constants.CMD%>=' + 'deleteuser'
			+ '&usuarioid=' + encodeURI(idMod)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			jQuery('#<portlet:namespace />usuariosCajaChicaDiv').load(url, function() {}	 );
	}
	
	
	
	
	
</script>