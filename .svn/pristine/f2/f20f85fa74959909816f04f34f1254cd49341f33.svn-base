<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<portlet:defineObjects/>
<%
 
Integer idLote = ParamUtil.getInteger(request, "nrolote");
List<Motivo> motivosDebito = TraeListasServiceUtil.getMotivosDebito();

%>

<div id="<portlet:namespace />debitosDiv">
   <fieldset class="block-labels">
		<legend>Débitos y Exclusión para Liquidar</legend>			
				
				<table class="lfr-table">
					<tr>
					<td>
					         <table class="lfr-table">
					         <tr><td colspan="14">
					               <liferay-util:include page='/html/portlet/autorizaciones/integracion/integracion_comprobante_search.jsp'>
							         <liferay-util:param value="<%=String.valueOf(idLote)%>" name="nrolote" />
							         <liferay-util:param value="_DEB" name="sufijo" />
							       </liferay-util:include>
							     </td>
							 </tr>   
							 
							 <tr>
							   <td>&nbsp;</td>
							 </tr>  
							    	
				             <tr>
				                <table class="lfr-table">
				                <tr>
				                <td>&nbsp;<label>Débito:</label>
				                <td><input id="<portlet:namespace />debito" name="<portlet:namespace />debito" size="15" maxlength="15" type="text" value=""  /></td>
				  
				                <td><label>Motivo:</label>
				                <td>
				                   <select name="<portlet:namespace/>motivo" id="<portlet:namespace/>motivo">
			                       <%
								      for (Motivo motivoDebito : motivosDebito) {
			                       %>
			                         <option value="<%= motivoDebito.getId_motivo()%>"><%=motivoDebito.getDescripcion()%></option>
			                       <%
								      }
			                       %>
		                           </select>
				                </td>
				  
				                <td><input id="<portlet:namespace />agregar" value="<liferay-ui:message key="agregar"/>" title="Agregar Débito" type="button" onClick="javascript:agregarDebito();"/>
				                
				                <td><input id="<portlet:namespace />excluirLiquidacion" value="Excluir de Liquicación" title="Excluir de Liquidación" type="button" onClick="javascript:excluirLiquidacion();"/>
				                
				                <td><input id="<portlet:namespace />incluirLiquidacion" value="Incluir en Liquicación" title="Incluir en Liquidación" type="button" onClick="javascript:incluirLiquidacion();"/>
				                
				               </td>
				               </tr>
				               <tr>
							     <td>&nbsp;</td>
							   </tr>  
				                
				               </table>
				             </tr> 
				            </table>
					     
					   </td>
				    </tr>
				</table>    
				<table class="lfr-table">
					<tr>
					   <td>
					     <div id="<portlet:namespace />debitosDelLoteDiv">
					            <jsp:include page='/html/portlet/autorizaciones/integracion/integracion_debitos_result.jsp'/>
					     </div>
					   </td>
					   
					   
																		
					</tr>
														
				</table>
				
								
				<div align="center" id="<portlet:namespace />buscandoIMP">
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='buscando'/></td>
							<td align="center">					
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>
	
				
  </fieldset>								
</div>			

	
<script type="text/javascript">
	var popupAfill;
	jQuery('#<portlet:namespace />buscandoIMP').hide();
	jQuery('#<portlet:namespace />excluirLiquidacion').hide();
	jQuery('#<portlet:namespace />incluirLiquidacion').hide();
	
	function agregarDebito() {
		var debito =	jQuery("#<portlet:namespace/>debito").val();
		var motivo =	jQuery("#<portlet:namespace/>motivo").val();
		var idCpte =	jQuery("#<portlet:namespace/>idCpte_DEB").val();
		
		if(debito==null || debito=="" ||
		   motivo==null || motivo=="" ||
		   idCpte==null || idCpte==""  ){
		   alert("Debe llenar todos los campos solicitados");	
		}else{
			var params = "&<%= Constants.CMD %>=debito_add";
			params += "&idcpte="+idCpte;
			params += "&debito="+debito;
			params += "&motivo="+motivo;
			params += "&motivo="+motivo;
			params += "&idLote="+"<%=idLote%>";
			url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/integracion_editar';
			url = url + params;
			
		    jQuery('#<portlet:namespace />debitosDelLoteDiv').load(url, function() {

		    	jQuery("#<portlet:namespace/>debito").val("");
				jQuery("#<portlet:namespace/>motivo").val("");
				jQuery("#<portlet:namespace/>idCpte_DEB").val("");
				
				jQuery("#<portlet:namespace/>cuit").val("");
				jQuery("#<portlet:namespace/>cuil").val("");
				jQuery("#<portlet:namespace/>tipo").val("");
				jQuery("#<portlet:namespace/>sucursal").val("");
				jQuery("#<portlet:namespace/>numero").val("");
				jQuery("#<portlet:namespace/>prestacion").val("");
				
				jQuery('#<portlet:namespace />fecha').val("");
				jQuery('#<portlet:namespace />importe').val("");
				jQuery('#<portlet:namespace />solicitado').val("");
				jQuery('#<portlet:namespace />estado').val("");
			}
           );
		    
		}		
	}
	
	
	function borrarDebito(id,idlote){
		var confirmar = false;
		confirmar=confirm ('Esta seguro de eliminar el Débito?');
		if(confirmar){
		  var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
	   	  url = url+'&<%= Constants.CMD %>'+'='+'debito_delete'+'&idcpte='+id +'&idLote='+idlote;
	   	  jQuery('#<portlet:namespace />debitosDelLoteDiv').load(url);	
		}
	}
	
	function excluirLiquidacion() {
		var idCpte =	jQuery("#<portlet:namespace/>idCpte_DEB").val();
			var params = "&<%= Constants.CMD %>=excluir_liquidacion";
			params += "&idcpte="+idCpte;
			params += "&idLote="+"<%=idLote%>";
			url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/integracion_editar';
			url = url + params;
			if(confirm ('Esta seguro de excluir de la liquidación?')){
		      jQuery('#<portlet:namespace />debitosDelLoteDiv').load(url, function() {

		    	jQuery("#<portlet:namespace/>debito").val("");
				jQuery("#<portlet:namespace/>motivo").val("");
				jQuery("#<portlet:namespace/>idCpte_DEB").val("");
				
				jQuery("#<portlet:namespace/>cuit").val("");
				jQuery("#<portlet:namespace/>cuil").val("");
				jQuery("#<portlet:namespace/>tipo").val("");
				jQuery("#<portlet:namespace/>sucursal").val("");
				jQuery("#<portlet:namespace/>numero").val("");
				jQuery("#<portlet:namespace/>prestacion").val("");
				
				jQuery('#<portlet:namespace />fecha').val("");
				jQuery('#<portlet:namespace />importe').val("");
				jQuery('#<portlet:namespace />solicitado').val("");
				jQuery('#<portlet:namespace />estado').val("");
				
				alert("Se ha excluído el comprobante de la liquidación");
			  }
             );
			}   
	}
	
	
	function incluirLiquidacion() {
		var idCpte =	jQuery("#<portlet:namespace/>idCpte_DEB").val();
			var params = "&<%= Constants.CMD %>=incluir_liquidacion";
			params += "&idcpte="+idCpte;
			params += "&idLote="+"<%=idLote%>";
			url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/integracion_editar';
			url = url + params;
			if(confirm ('Esta seguro de incluir en la liquidación?')){	
		      jQuery('#<portlet:namespace />debitosDelLoteDiv').load(url, function() {

		    	jQuery("#<portlet:namespace/>debito").val("");
				jQuery("#<portlet:namespace/>motivo").val("");
				jQuery("#<portlet:namespace/>idCpte_DEB").val("");
				
				jQuery("#<portlet:namespace/>cuit").val("");
				jQuery("#<portlet:namespace/>cuil").val("");
				jQuery("#<portlet:namespace/>tipo").val("");
				jQuery("#<portlet:namespace/>sucursal").val("");
				jQuery("#<portlet:namespace/>numero").val("");
				jQuery("#<portlet:namespace/>prestacion").val("");
				
				jQuery('#<portlet:namespace />fecha').val("");
				jQuery('#<portlet:namespace />importe').val("");
				jQuery('#<portlet:namespace />solicitado').val("");
				jQuery('#<portlet:namespace />estado').val("");
				
				alert("Se ha incluído el comprobante de la liquidación");
			}
            );
		  }    
	}
</script>
