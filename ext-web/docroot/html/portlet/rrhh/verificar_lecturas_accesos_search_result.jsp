<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="/html/portlet/rrhh/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_CONSULTA_RRHH);				
			    
				List<RegistroAcceso> registrosList= new ArrayList<RegistroAcceso> ();
				registrosList= (ArrayList<RegistroAcceso>)renderRequest.getAttribute("FICHADAS_LOS_DIQUES");
				int i = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				
			%>
			<form name="<portlet:namespace />fi" id="<portlet:namespace />fi" action="/rrhh/upload_archivo_horarios_con_verificacion">
			<input type="hidden" name="<%=Constants.CMD %>" value="<%=Constants.UPDATE%>">
			<input type="hidden" name="cantidadFichadas" value="<%=registrosList.size()%>">
			<div style="display: table; vertical-align: top; border-spacing: 5px; padding: 5px;">
				<div id="<portlet:namespace />divFichadasCab" style="display: table-row;">
					<div id="F_C0" style="display: table-cell;">&nbsp;</div>
					<div id="F_C1" style="display: table-cell;"><liferay-ui:message key="Apellido y Nombre" /></div>
					<div id="F_C2" style="display: table-cell;"><liferay-ui:message key="Fecha Registro" /></div>
				   	<div id="F_C3" style="display: table-cell;"><liferay-ui:message key="Tipo Registro" /></div>
				</div>
				
				<%	
					 for(i=0; i < registrosList.size(); i++){	
					   
						 RegistroAcceso ra = registrosList.get(i);
				%>
				<div id="<portlet:namespace />divFichadas<%=i%>" style="display: table-row;">
				<div id="F<%=i%>_C0" style="display: table-cell;">
					<input type="hidden" name="id_<%=i%>" value="<%=ra.getId()%>">
					<input type="hidden" name="<portlet:namespace />modificado_<%=i%>" 
						id="<portlet:namespace />modificado_<%=i%>" value="false">
				</div>
				<div id="F<%=i%>_C1" style="display: table-cell;">
				<%=ra.getTarjetaAcceso()!=null?(
						(ra.getTarjetaAcceso().getApellido()!=null?ra.getTarjetaAcceso().getApellido():"sin apellido para DNI:"+ra.getId_tarjeta_acceso())
						+", " +
				        (ra.getTarjetaAcceso().getNombre()!=null?ra.getTarjetaAcceso().getNombre():"sin nombre")
				        ):("sin apellido ni nombre para DNI:"+ra.getId_tarjeta_acceso()) 
				        %>
				</div>
				<div id="F<%=i%>_C2" style="display: table-cell;"><%=sdf.format(ra.getFecha_registro())  %></div>
				<div id="F<%=i%>_C3" style="display: table-cell;">
						<select name="<portlet:namespace/>tipo_registro_<%=i%>" id="<portlet:namespace/>tipo_registro_<%=i%>" 
						style="width: 75px;"
						onchange="<portlet:namespace />modificarEstado('<%=i%>');">
							<option value="E" <%if(ra.getTipo_registro().equalsIgnoreCase("E")) { %>selected="selected" <%} %>>Entrada</option>
							<option value="S" <%if(ra.getTipo_registro().equalsIgnoreCase("S")) { %>selected="selected" <%} %>>Salida</option>		
						</select>
					</div>
				</div>	
				<%} %>	   
					
			 </div>
			 <div style="display: table; vertical-align: top; border-spacing: 5px; padding: 5px;">
				<div id="<portlet:namespace />divAceptar" style="display: table-row;">
					<div id="F_B0" style="display: table-cell;"><input type="submit" value="Corregir" onclick="javascript:corregirUltimasFichadasLosDiques();"></div>
				</div>
			</div>
			</form>		
<script>
function <portlet:namespace />modificarEstado(orden) {
	
	<% for(int l=0; l < i; l++){ %>
	if(orden == <%=l%>){
		jQuery('#<portlet:namespace />modificado_<%=l%>').val('true');
	}
	<%}%>
}

function corregirUltimasFichadasLosDiques(){
	
<%-- 	jQuery('#<portlet:namespace />buscando').show();
	var params = {"<%=Constants.CMD%>":"<%=Constants.UPDATE%>"};
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/rrhh/upload_archivo_horarios_con_verificacion" /></portlet:renderURL>';
	
	jQuery('#<portlet:namespace />busquedaFichadasDiv').load(url, params, function() {
																jQuery('#<portlet:namespace />buscando').hide();            															
															  }
	); --%>
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/rrhh/upload_archivo_horarios_con_verificacion"/></portlet:actionURL>';			
	document.<portlet:namespace />fi.method = 'post';
	submitForm(document.<portlet:namespace />fi, url);
}	

</script>			   