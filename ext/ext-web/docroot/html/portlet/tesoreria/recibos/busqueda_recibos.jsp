<%@ page import="ar.com.ospim.tesoreria.ReciboDerivadoException" %>
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
boolean showABMButtons = PermissionUtil.userContainsRole(user, WebKeysTesoreria.ROL_ABM_RECIBOS) || portlet_name.equals("farmacia")|| portlet_name.equals("uoma");
%>
<portlet:defineObjects/>
<liferay-ui:error exception="<%= ReciboDerivadoException.class %>" message="recibo-derivado-exception" />
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-recibos" /></legend>
				<table class="lfr-table">
					<tr>
						<!--td><label><liferay-ui:message key="origen" />:</label>
						<td>
							<select name="<portlet:namespace/>origen" id="<portlet:namespace/>origen">
								<option value="teso">Tesorería</option>									
								<option value="estudio">Estudio</option>								
							</select>
						</td-->
						<td><label><liferay-ui:message key="recibo" />:</label></td>
						<td><input id="<portlet:namespace />recibo" name="<portlet:namespace />recibo" size="13" maxlength="20" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="cuit" />:</label></td>
						<td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="empresa" />:</label></td>
						<td><input id="<portlet:namespace />empresa" name="<portlet:namespace />empresa" size="50" maxlength="50" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
						<td>
							<% if(showABMButtons && !soloVer) { %>
								<input type="button" value="<liferay-ui:message key="alta-ingreso" />" onClick="javascript:<portlet:namespace />altaRecibo();" />
							<%} %>
						</td>		
					</tr>
					<tr>
						<td colspan="11">&nbsp;</td>
					</tr>
					<% if(portlet_name.equals("farmacia") || portlet_name.equals("uoma")){ %>
					<tr>						
						<td><label><liferay-ui:message key="cuil-titular" />:</label></td>
						<td><input id="<portlet:namespace />cuil_titular" name="<portlet:namespace />cuil_titular" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="inte" />:</label></td>
						<td><input id="<portlet:namespace />inte" name="<portlet:namespace />inte" size="10" maxlength="2" type="text" value="" /></td>
						
						
						<% if(portlet_name.equals("farmacia")){ %>
						   <td><label>ID. AMTIMA:</label></td>
						   <td><input id="<portlet:namespace />id_amtima" name="<portlet:namespace />id_amtima" size="13" maxlength="11" type="text" value="" /></td>
						<%} %>
					</tr>
					<%} %>
					<tr>
						<td colspan="11">&nbsp;</td>
					</tr>
				</table>	      	  
		</fieldset>	
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
			<div align="center" id="<portlet:namespace />busquedaReciboDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var recibo=jQuery('#<portlet:namespace />recibo').val();
		var empresa=jQuery('#<portlet:namespace />empresa').val();
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var cuil_titu="";
		var inte="";
		var id_amtima="";
		var origen=jQuery('#<portlet:namespace/>origen').val();
		<%
		if(portlet_name.equals("farmacia")|| portlet_name.equals("uoma")){  %> 
			 cuil_titu=jQuery('#<portlet:namespace />cuil_titular').val();
			 inte=jQuery('#<portlet:namespace />inte').val();
		<% } %>		
		
		<%if(portlet_name.equals("farmacia")){  %> 
		   id_amtima=jQuery('#<portlet:namespace />id_amtima').val();
	    <%}%>		
		if(!<portlet:namespace />validarBusqueda(recibo,empresa, cuit, cuil_titu, inte,id_amtima)){
			/*if(!<portlet:namespace />validarBusqueda(recibo,empresa, cuit)){*/	
			return false;
		}		
		jQuery('#<portlet:namespace/>buscando').show();
		
		if(origen=='estudio'){			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_recibos_no_os'
		}else{
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_recibos'
		}
		url=url+'&recibo='+recibo+'&empresa='+empresa+'&cuit='+cuit;
		url += '&cuil_titular='+cuil_titu+'&inte='+inte;
		url += '&origen=recibosTesoreria';
		url += '&id_amtima='+id_amtima;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />busquedaReciboDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusqueda(recibo, empresa, cuit, cuil_titular, inte,id_amtima){		
		if(trim(recibo).length ==0 && trim(empresa).length==0 && trim(cuit).length==0 && trim(cuil_titular).length==0 && trim(inte).length==0
				&& trim(id_amtima).length==0){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}
	/**function <portlet:namespace />validarBusqueda(recibo, empresa, cuit){		
		if(trim(recibo).length ==0 && trim(empresa).length==0 && trim(cuit).length==0 ){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}*/

	function <portlet:namespace />altaRecibo() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_recibos_entry';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     
	
</script>
