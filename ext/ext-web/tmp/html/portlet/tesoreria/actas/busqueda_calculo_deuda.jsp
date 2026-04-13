<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.ImposibleBorrarActaException" %>

<portlet:defineObjects/>

<liferay-ui:error exception="<%= ImposibleBorrarActaException.class %>" message="imposible-borrar-acta" />
<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,"ABM_CalculoDeuda");

	    boolean isActa = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_ACTAS);
	    if (isActa) {
	    	showABMButtons = true;
	    }
%>
		<fieldset class="block-labels">
		<input type="hidden" name="fromBusquedaDeuda" id="fromBusquedaDeuda" value="fromBusquedaDeuda"/>
				<legend><liferay-ui:message key="busqueda-deudas" /></legend>
				<table class="lfr-table">
					<tr>
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
							<% if(showABMButtons) { %>
								<input type="button" value="<liferay-ui:message key="alta-deuda" />" onClick="<portlet:namespace />altaActa();" />
							<%} %>
						</td>		
					</tr>
					<tr>
						<td colspan="14">&nbsp;</td>
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
			<div align="center" id="<portlet:namespace />busquedaActaDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var empresa=jQuery('#<portlet:namespace />empresa').val();
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		
		if(!<portlet:namespace />validarBusqueda(empresa, cuit)){
			return false;
		}		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/buscar_actas' +
		'&empresa='+empresa+'&cuit='+cuit+'&fromBusquedaDeuda=fromBusquedaDeuda';
		 url += '&rnd=' + Math.floor(Math.random()*100);	
		jQuery('#<portlet:namespace />busquedaActaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusqueda( empresa, cuit){		
		if( trim(empresa).length==0 && trim(cuit).length==0 ){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaActa() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/editar_actas_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     
	
</script>
