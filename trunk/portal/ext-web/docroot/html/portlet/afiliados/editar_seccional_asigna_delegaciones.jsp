<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@ page import="com.liferay.portal.kernel.util.OrderByComparator"%>
 
<%
int entidad=WebKeysGlobal.OSPIM;
String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "afiliados";
}

String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);

Seccional seccional=(Seccional)request.getSession().getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);

List<Delegacion> ctas = TraeListasServiceUtil.getDelegacionesSinSeccional();
boolean rolABMSeccionales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_SECCIONALES);

%>


<table width="100%">
  <tr>
	<td width="100%" valign="top">
	<table class="lfr-table" width="100%">
	   <tr>
	    <td valign="top" width="30%" >
	     <table>
	       <tr>
	         <td valign="top">
	         <!--  
	         <label><liferay-ui:message key="delegacion" />:</label>
	         -->
	         </td>
	         <td valign="top">
	           <%if(rolABMSeccionales && !"ED".equalsIgnoreCase(seccional.getModo()) || seccional.getDelegaciones()==null || seccional.getDelegaciones().size()==0){ %>
	              <select name="<portlet:namespace/>delegacion_seccional" id="<portlet:namespace/>delegacion_seccional">
		              <option value="">Seleccione una Delegación</option>	       					
							<%for(Delegacion u:ctas) {%>
							   <option value="<%=u.getId() %>"><%=u.getDescripcion()  %> </option>
							<%}%>
								
			      </select>
	           <%} %>
		     </td>
		     <td>    
		       <%if(rolABMSeccionales && !"ED".equalsIgnoreCase(seccional.getModo()) || seccional.getDelegaciones()==null || seccional.getDelegaciones().size()==0 ){ %>
  		      
		            <input type="button" value="Asignar Delegacion" 
		            onClick="<portlet:namespace />agregarDelegacion();" />
		       <%} %>     
		     </td>     
		     
	       </tr>
	       <tr>
		     <td colspan="1">&nbsp;</td>
	       </tr>
	      </table>
	     </td>
	     </tr>
	     <tr>
	     <td valign="top" colspan="15" width="70%">
				<div align="center" id="<portlet:namespace />delegacionesSeccionalDiv">
					<liferay-util:include page="/html/portlet/afiliados/editar_seccional_asigna_delegaciones_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
        </tr>
	</table>
	</td>
  </tr>
</table>

<input type="hidden" name="<portlet:namespace />id_seccional_asignado" id="<portlet:namespace />id_seccional_asignado" value="" />

<script type="text/javascript">
   	function <portlet:namespace />agregarDelegacion(){
		var idDetalle=jQuery('#<portlet:namespace />id_seccional_asignado').val();
			
		var delegacionId=jQuery('#<portlet:namespace/>delegacion_seccional').val();
		var delegacionDescripcion=jQuery('#<portlet:namespace/>delegacion_seccional').find('option:selected').text();
		
		if(jQuery("#<portlet:namespace />descripcionSeccional").val()==null || jQuery("#<portlet:namespace />descripcionSeccional").val()==""){
		   jQuery("#<portlet:namespace />descripcionSeccional").val(delegacionDescripcion);
		}   
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccional_agregar_delegacion'
			+	'&<%= Constants.CMD%>=' 
				
		if(idDetalle==null || idDetalle =="" || idDetalle==0){
			url += 'asociardelegacion'
		}
					
		url += '&delegacionid=' + encodeURI(delegacionId)
				+ '&delegaciondescripcion=' + encodeURI(delegacionDescripcion)
				+ '&iddetalle=' + encodeURI(idDetalle)
				+ '&esEdicion=' +"<%=esEdicion%>"; 	
				
		if(delegacionId!=null && delegacionId!=""){			
				jQuery('#<portlet:namespace />delegacionesSeccionalDiv').load(url, function() {} );
		}else {
			alert("Debe seleccionar una Delegación");
		}		
				
	}
	

	function borraDelegacionAsociada(idMod){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_seccional_agregar_delegacion'
			+	'&<%= Constants.CMD%>=' + 'desasociardelegacion'
			+ '&delegacionid=' + encodeURI(idMod)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			jQuery('#<portlet:namespace />delegacionesSeccionalDiv').load(url, function() {}	 );
	}
	
	
	
	
	
</script>