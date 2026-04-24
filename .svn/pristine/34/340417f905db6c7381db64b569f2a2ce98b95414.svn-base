<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>

<%@ page import="java.util.Comparator"%>
<portlet:defineObjects/>
<%
 
    Integer idLote = ParamUtil.getInteger(request, "nrolote");
    String sufijo =  ParamUtil.getString(request, "sufijo");
    if(idLote==null ||idLote==0){
       idLote = (Integer)request.getAttribute("nrolote");
    }   

    String detalleOP = "Lote: " + idLote;
   
%>
<fieldset class="block-labels">
		<legend>Comprobante</legend>
		
		
<fieldset class="block-labels">		
		  <table class="lfr-table">
				<tr>
				   <td colspan="2">
					      <span style="font-size: 12pt; color:blue;"><label id="lbOp"> <%= detalleOP %> </label></span>
				   </td>
				</tr>
				<tr>
				    <td><label>Cuit Prestador:</label></td>
				    <td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="15" maxlength="11" type="text" value="" /></td>
				    
				    <td><label>Cuil:</label></td>
				    <td><input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="15" maxlength="11" type="text" value="" /></td>
				    
				    <td><label>Tipo:</label></td>
				    <td><input id="<portlet:namespace />tipo" name="<portlet:namespace />tipo" size="3" maxlength="1" type="text" value=""/></td>
				    
				    <td><label>Sucursal:</label></td>
				    <td><input id="<portlet:namespace />sucursal" name="<portlet:namespace />tipo" size="6" maxlength="5" type="text" value=""/></td>
				    
				    <td><label>Nro:</label></td>
				    <td><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="10" maxlength="10" type="text" value=""/></td>
				    
				    <td><label>Prestación:</label></td>
				    <td><input id="<portlet:namespace />prestacion" name="<portlet:namespace />prestacion" size="3" maxlength="5" type="text" value=""/></td>
			    </tr>
			    
			    <tr> <td>&nbsp;</td></tr>
			    
				<tr>
				 <td colspan="14" align="right" ><input id="<portlet:namespace />busquedaCpbte" value="Buscar" title="Buscar Comprobante" type="button" onClick="javascript:buscarComprobanteIntegracion();"/>
				 <td colspan="14" align="right" ><input id="<portlet:namespace />limpiarCpbte" value="Limpiar" title="Limpiar Comprobante" type="button" onClick="javascript:limpiarComprobanteIntegracion();"/>
				 </td>
				</tr>
				
			</table>
			<table class="lfr-table">	
				<tr>
				  <td><label>Fecha Prestación:</label>
				  <td><input id="<portlet:namespace />fecha" name="<portlet:namespace />fecha" size="15" maxlength="11" type="text" value="" readonly="readonly" /></td>
				  
				  <td><label>Importe:</label>
				  <td><input id="<portlet:namespace />importe" name="<portlet:namespace />importe" size="15" maxlength="15" type="text" value="" readonly="readonly" /></td>
				  
				  <td><label>Solicitado:</label>
				  <td><input id="<portlet:namespace />solicitado" name="<portlet:namespace />solicitado" size="15" maxlength="15" type="text" value="" readonly="readonly" /></td>
				  
				  <td><label>Status:</label>
				  <td><input id="<portlet:namespace />estado" name="<portlet:namespace />estado" size="3" maxlength="3" type="text" value="" readonly="readonly" /></td>
				  
				</tr>    
			</table>
</fieldset>


<div align="center" id="<portlet:namespace />buscandoSEARCH">
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='buscando'/></td>
							<td align="center">					
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
</div>


<input id="<portlet:namespace />idLote" name="<portlet:namespace />idLote" type="hidden" value="<%= idLote %>" />	
<input id="<portlet:namespace />idCpte<%=sufijo%>" name="<portlet:namespace />idCpte<%=sufijo%>" type="hidden" value="" />	

<script type="text/javascript">
	var popup;
	jQuery('#<portlet:namespace />buscandoSEARCH').hide();
	
	function buscarComprobanteIntegracion(){
		var cuit=	jQuery("#<portlet:namespace/>cuit").val();
		var cuil=	jQuery("#<portlet:namespace/>cuil").val();
		var tipo=	jQuery("#<portlet:namespace/>tipo").val();
		var sucursal=	jQuery("#<portlet:namespace/>sucursal").val();
		var numero=	jQuery("#<portlet:namespace/>numero").val();
		var prestacion=	jQuery("#<portlet:namespace/>prestacion").val();
		var idLote =	jQuery("#<portlet:namespace/>idLote").val();

		if(cuit!=null && cuit!="" && 
		   cuil!=null && cuil!="" &&
		   tipo!=null && tipo!="" &&
		   sucursal!=null && sucursal!="" &&
		   numero!=null && numero!="" &&
		   prestacion!=null && prestacion!="" ){
		   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_buscar_comprobante';
	   	       url = url+'&cuit='+cuit +
	   	                 '&cuil='+cuil +
	   	                 '&tipo='+tipo +
	   	                 '&sucursal='+sucursal +
	   	                 '&numero='+numero +
	   	                 '&prestacion='+prestacion+
	   	                 '&lote='+idLote;	
			
			jQuery.ajax({   
				url: url,
				success: function(data){
					jQuery('#<portlet:namespace />buscandoSEARCH').hide();
					var obj = jQuery.parseJSON(data);
					jQuery('#<portlet:namespace />idCpte<%=sufijo%>').val(obj.id);
					jQuery('#<portlet:namespace />fecha').val(obj.fecha);
					jQuery('#<portlet:namespace />importe').val(obj.importe);
					jQuery('#<portlet:namespace />solicitado').val(obj.solicitado);
					jQuery('#<portlet:namespace />estado').val(obj.status);
					
					if(obj.status=='OK'){
						jQuery('#<portlet:namespace />excluirLiquidacion').show();
						jQuery('#<portlet:namespace />incluirLiquidacion').hide();
					}else{
						jQuery('#<portlet:namespace />excluirLiquidacion').hide();
						if(obj.id != null){
						  jQuery('#<portlet:namespace />incluirLiquidacion').show();
						}else{
						  jQuery('#<portlet:namespace />incluirLiquidacion').hide();	
						}  
					}
				}
			});		
		}else{
			alert('Debe ingresar todos los datos para la búsqueda')
		}  
	   	
	}

	function limpiarComprobanteIntegracion(){
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
	
</script>
