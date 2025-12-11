<%@ include file="/html/portlet/comprobantes/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%

	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "comprobantes";
	}else if(renderResponse.getNamespace().equals("_COM_1_")){
		portlet_name = "comprobantes";
	}
	List<Sector>sectores = (List<Sector>)ComprobanteServiceUtil.getSectores();
	String usuario=user.getScreenName();
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	fecha.setTime(new Date());

	List<User> users = UserLocalServiceUtil.search(
			themeDisplay.getCompanyId(), null, Boolean.TRUE, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);

 		
 		String esEditableStr = ParamUtil.getString(request, "esEditable");
 		if (esEditableStr == null || esEditableStr.equals("false")){
 			esEditableStr ="false";
 		}
 		boolean esEditable = Boolean.parseBoolean(esEditableStr);
 		
 		session.removeAttribute(WebKeysComprobantes.COMPROBANTE_SECTOR_EN_EDICION);
%>
		<fieldset class="block-labels">
		<legend>Asignación de Usuarios por Sector</legend>
			
		 <table class="lfr-table">	
			<tr><td>
			
	            <table class="lfr-table">
	             <tr>
			      <td><label>Area Liquidación:</label></td>
			      <td>
					<select id="<portlet:namespace />sector" name="<portlet:namespace />sector" onchange="javascript:<portlet:namespace />buscarSector();"">
					   <option value="">Selecciones un Sector</option>
						<%for(Sector sector:sectores) {%>
						<option
							value="<%=sector.getId() %>"><%=sector.getId() %>
						</option>
					    <%}%>					
				    </select>
			      </td>
		         </tr> 
		         <tr><td>&nbsp;</td></tr>	
		         <tr>	
			       <td><label>Usuarios:</label></td>
			       <td>
                     <select name="<portlet:namespace/>usuario" id="<portlet:namespace/>usuario">
		              <option value="">Seleccione un Usuario</option>	       					
							<%for(User u:users) {%>
							   <option value="<%=u.getUserId()%>"><%=u.getFullName()%> </option>
							<%}%>
								
			         </select>
			       </td>
			       <td>     
		             <input type="button" value="<liferay-ui:message key="assign-users" />" 
		                 onClick="<portlet:namespace />agregarUsuario();" />
		           </td>  		
		         </tr>
		         <tr><td>&nbsp;</td></tr>
	            </table>
	     
	     </td>
	     
	     <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	     <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	     
	     <td>
	      <table  class="lfr-table">
	      <tr>
	       <td valign="top" align="right" colspan="15" width="100%">
				<div align="right" id="<portlet:namespace />usuariosDiv">
					<liferay-util:include page="/html/portlet/comprobantes/sector_usuarios_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEditable) %>'/>
					</liferay-util:include>
				</div>
			</td>
           </tr>
	      </table>       	     
    	 </td>
    	 </tr>
    	 </table> 				
		</fieldset>
		
<input type="hidden" name="<portlet:namespace />id_usuario_asignado" id="<portlet:namespace />id_usuario_asignado" value="" />
					
<script type="text/javascript">
function <portlet:namespace />buscarSector(){
	var sector= jQuery('#<portlet:namespace/>sector').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/administracion'
		+	'&<%= Constants.CMD%>=' 
	    + 'findSector'
	    + '&sector=' + encodeURI(sector);
				
		jQuery('#<portlet:namespace />usuariosDiv').load(url, function() {} );
			
}

function <portlet:namespace />agregarUsuario(){
	var idDetalle=jQuery('#<portlet:namespace />id_usuario_asignado').val();
		
	var usuarioId=jQuery('#<portlet:namespace/>usuario').val();
	var usuarioDescripcion=jQuery('#<portlet:namespace/>usuario').find('option:selected').text();
	var sector= jQuery('#<portlet:namespace/>sector').val();
		
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/administracion'
		+	'&<%= Constants.CMD%>=';
			
	if(idDetalle==null || idDetalle =="" || idDetalle==0){
		url += 'adduser';
	}
				
	url += '&usuarioid=' + encodeURI(usuarioId)
			+ '&usuariodescripcion=' + encodeURI(usuarioDescripcion)
			+ '&iddetalle=' + encodeURI(idDetalle)
			+ '&esEdicion=' +"<%=esEditable%>"; 	
				
			jQuery('#<portlet:namespace />usuariosDiv').load(url, function() {
				jQuery('#<portlet:namespace/>usuario').val("");
			} );
}


function borraUsuario(idMod){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/administracion'
		+	'&<%= Constants.CMD%>=' + 'deleteuser'
		+ '&usuarioid=' + encodeURI(idMod)
		+ '&esEdicion=' +"<%=esEditable%>"; 	
		jQuery('#<portlet:namespace />usuariosDiv').load(url, function() {}	 );
}	
</script>
