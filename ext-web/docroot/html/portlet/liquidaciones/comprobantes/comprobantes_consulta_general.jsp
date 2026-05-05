<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException.class %>" message="lista-reintegros-no-encontrada" />
<%
 
Calendar fecha = CalendarFactoryUtil.getCalendar();
fecha.setTime(new Date());


 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
%>
		<% if(showABMButtons) { %>
		<fieldset class="block-labels">
		<legend><liferay-ui:message key="busqueda-comprobantes" /></legend>
		<table width="70%">
				<tr>
					<td><label><liferay-ui:message key="tipo" />:</label></td>
					<td>
					<select id="<portlet:namespace />tipo_comprobante" name="<portlet:namespace />tipo_comprobante">
						<option value="">Todos</option>						
						<option value="FCP">FCP</option>
						<option value="NCR">NCR</option>
						<option value="NDB">NDB</option>
						<option value="RCB">RCB</option>
						<option value="ANT">ANT</option>
						<option value="REI">REI</option>
						<option value="VAR">VAR</option>
						<option value="OTR">OTR</option>
						</select>
					</td>
					<td><label><liferay-ui:message key="letra" />:</label></td>
					<td>
					<select id="<portlet:namespace />letra" name="<portlet:namespace />letra">
						<option value="">Todos</option>						
						<option value="A">A</option>
						<option value="B">B</option>
						<option value="C">C</option>
						<option value="M">M</option>
						</select>
					</td>
					<td>
						<label><liferay-ui:message key="pto-venta" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />pto_venta" name="<portlet:namespace />pto_venta" onkeydown="allowOnlyDigits(event)" />
					</td>
					<td>
						<label><liferay-ui:message key="numero" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />nro_comprobante" name="<portlet:namespace />nro_comprobante" value="" maxlength="25"/>
					</td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td><label><liferay-ui:message key="acreedor" />:</label></td>
					<td colspan="7">
						<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					  		<liferay-util:param name="esEditable" value='true'/>
						</liferay-util:include>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				
				<tr>
					<td>
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"
						onclick="javascript:<portlet:namespace />buscarCptes();"/>							
					</td>
					<td>
<!--  					
						<% if(showABMButtons) { %>
                             <input type="button" value="<liferay-ui:message key="alta-comprobante" />" onClick="<portlet:namespace />altaComprobanteGlobal();" />
						<%} %>
-->						
					</td>	
					<td colspan="6">&nbsp;</td>	
				</tr>
			</table>				
		</fieldset>
		<%} %>
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
			<div align="center" id="<portlet:namespace />comprobantesResultDiv">
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	//jQuery('#<portlet:namespace />buscar').click(function(){

    function <portlet:namespace />buscarCptes(){
		//var cuit=jQuery('#<portlet:namespace />cuit_compr_emisor').val();
		var pto_venta=jQuery('#<portlet:namespace />pto_venta').val();
		var tipo_comprobante=jQuery('#<portlet:namespace />tipo_comprobante').val();
		var nro_comprobante=jQuery('#<portlet:namespace />nro_comprobante').val();	
		var letra=document.getElementById("<portlet:namespace />letra").value;

		var cuit_acreedor = jQuery("#<portlet:namespace />cuit_entidad").val();    
	    var sucu_acreedor = jQuery("#<portlet:namespace />sucursal_entidad").val();
	    var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();			
		//jQuery("#pagina").val(pagina_sel);
	    
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/comprobantes_consulta_general';
		url += '&pto_venta='+pto_venta+'&tipo_comprobante='+tipo_comprobante+'&nro_comprobante='+nro_comprobante+
		 	  '&letra=' + letra;
		url += '&cuit_entidad='+cuit_acreedor+'&pagina='+pagina_sel;;
		url += '&rnd=' + Math.floor(Math.random()*100);
		
		if((cuit_acreedor !=null && cuit_acreedor != "") ||
				(pto_venta !=null && pto_venta != "")	||
				(tipo_comprobante!=null && tipo_comprobante != "") ||
				(nro_comprobante !=null && nro_comprobante != "") ||
				(letra != null && letra !="")){
		   jQuery('#<portlet:namespace />comprobantesResultDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();
        															  }
           );
	    }else{
	    	alert("Debe seleccionar algún parámetro de búsqueda");
	    	jQuery('#<portlet:namespace />buscando').hide();	
	    }   
	}
	
    function <portlet:namespace />altaComprobanteGlobal() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_comprobante_general" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}		
</script>
