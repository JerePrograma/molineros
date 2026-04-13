<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.beans.FichaBoletaPortal"%>
<%@ page import="ar.com.ospim.global.services.EmpresaServiceUtil"%>
<%@ page import="ar.com.ospim.global.beans.Empresa"%>
<%@ page import="ar.com.ospim.global.beans.ConvenioNacion"%>
<%@ page import="java.text.DecimalFormat"%>

<portlet:defineObjects/>

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

 		DecimalFormat fm = new DecimalFormat("###0.00");

 		FichaBoletaPortal boleta = (FichaBoletaPortal)request.getSession().getAttribute("BOLETA_EMPLEADORES_REIMPUTAR");
 		
        Empresa empresa =(Empresa)request.getSession().getAttribute("BOLETA_EMPLEADORES_EMPRESA");
 		
        List<ConvenioNacion> conveniosNac =TraeListasServiceUtil.getConvenioNac();
        ConvenioNacion convOriginal = new ConvenioNacion();
        if(boleta!=null && boleta.getTipoBoleta()!=null){
        
          for(ConvenioNacion c:conveniosNac){
        	if(c.getTipo_boleta()==boleta.getTipoBoleta().intValue()){
        		convOriginal=c;
        		break;
        	}
          }
        }
        FichaBoletaPortal boletaDest = (FichaBoletaPortal)request.getSession().getAttribute("BOLETA_EMPLEADORES_BOLETA_IMPAGA");
 		
%>	

<form action="" method="post" name="<portlet:namespace />fm">

 <liferay-ui:success key="insertCabOk" message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
 <liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
		
		<fieldset class="block-labels">
				<legend>
					Reimputación de Pagos de Boletas
				</legend>
			<fieldset class="block-labels">	
			
			    <legend>Origen</legend>
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="empresa" />:</label></td>
						<td colspan="6">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='false'/>
						  		<liferay-util:param name="portlet_name" value='uoma'/>
						  		<liferay-util:param name="cuit" value='<%=boleta.getCuit() %>'/>
						  		<liferay-util:param name="sucu" value='<%=boleta.getEmpresa_sucursal() %>'/>
						  		<liferay-util:param name="buscar_destino" value='true'/>
						  		<liferay-util:param name="razon" value='<%=empresa!=null && boleta.getCuit()!=null?empresa.getRazon_soc():"" %>'/>
							</liferay-util:include>
						</td>
					</tr>
					
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>
					
					<tr>
		             <td>
					      <label>Nro de Boleta:</label>&nbsp;<input
					      id="<portlet:namespace />numero" name="<portlet:namespace />numero"
					      size="8" maxlength="8" type="text" readonly="readonly" value="<%=boleta.getNro_boleta_portal_emple()%>" />
					 </td>
					 <td>
					      <label>Detalle:</label>&nbsp;<input
					      id="<portlet:namespace />descripcion" name="<portlet:namespace />descripcion"
					      size="30" type="text" readonly="readonly" value="<%=convOriginal.getDescripcion()!=null?convOriginal.getDescripcion():""%>" />
					 </td>
					 
					 <td>
					     <label>Período:</label>&nbsp;<input id="<portlet:namespace />periodo" name="<portlet:namespace />periodo"
					      size="10" type="text" readonly="readonly" value="<%=boleta.getPeriodoAsString()%>" />
					 </td>
					 
					 <td>
					     <label>Pago:</label>&nbsp;<input
					      id="<portlet:namespace />pago" name="<portlet:namespace />pago"
					      size="10" type="text" readonly="readonly" value="<%=boleta.getImporte()!=null?fm.format(boleta.getImporte()):""%>" />
					 </td>
					 
					  <td>
					     <label>Medio:</label>&nbsp;<input
					      id="<portlet:namespace />medio" name="<portlet:namespace />medio"
					      size="20" type="text" readonly="readonly" value="<%=boleta.getCodBarras()!=null?boleta.getCodBarras():""%>" />
					 </td>
					 
					 <td>
					     <label>Rendición:</label>&nbsp;<input
					      id="<portlet:namespace />rendicion" name="<portlet:namespace />rendicion"
					      size="15" type="text" readonly="readonly" value="<%=boleta.getFecha_rendicionAsString()%>" />
					 </td>
					 
					 <td>
					     <label>Nro Movimiento:</label>&nbsp;<input
					      id="<portlet:namespace />nroMovimiento" name="<portlet:namespace />nroMovimiento"
					      size="15" type="text" readonly="readonly" value="<%=boleta.getNroMovimiento()!=null?boleta.getNroMovimiento():""%>" />
					 </td>
					</tr>						
				 </table>
				</fieldset>
				
				<fieldset class="block-labels">	
			       <legend>Destino</legend>
				    <table class="lfr-table">			
					  <tr><td>
		            	      <table class="lfr-table">			
					           <tr>
					             <td>
					                <label>Nro de Boleta:</label>&nbsp;
					                 <input id="<portlet:namespace />numero_dest" name="<portlet:namespace />numero_dest" size="8" maxlength="8" type="text" value=""/>
					             </td>
					             <td>
					                <input id="<portlet:namespace />impagasTr" value="Traer Boleta" title="Traer Boleta" type="button" onClick="javascript:<portlet:namespace />traerImpaga();"/>
					             </td>
					           </tr>  
					           <tr>
						         <td colspan="7">&nbsp;</td>
					           </tr>
					           <tr>
					              <td>
					               <div align="left" id="<portlet:namespace />divBoletaDestino">
					                     <jsp:include page='/html/portlet/uoma/cuentacorriente/empleadores_reimputacion_pagos_edit_destino.jsp' />	
					               </div>
					              </td> 
					           </tr>
					          </table>        
					      
					
					  </td>
					  
					  <td align="right">
					      <fieldset>
					        <input id="<portlet:namespace />impagas" value="Ver Impagas" title="Ver Impagas" type="button" onClick="javascript:<portlet:namespace />verImpagas('V');"/>
				            <input id="<portlet:namespace />impagasL" value="Limpiar" title="Ver Impagas" type="button" onClick="javascript:<portlet:namespace />verImpagas('L');"/>
				            
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
				            							
						    <div align="right" id="<portlet:namespace />divBoletasImpagas">
						         <fieldset class="block-labels"><legend>Boletas Impagas</legend>
			                         <jsp:include page='/html/portlet/tesoreria/recibos/recibo_aportes_empleadores_search_result.jsp' />
	                             </fieldset>		
                            </div>
                          </fieldset>  
					  </td>
					  
					  </tr>
					</table>
				</fieldset>		
				<table>	
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>
					<tr>
					   <td>
							<input id="<portlet:namespace />save" value="<liferay-ui:message key="save"/>" title="<liferay-ui:message key="save" />" type="button" onClick="javascript:<portlet:namespace />guardar();"/>							
						</td>
					</tr>
				</table>	      	  
		</fieldset>	
</form>		
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();	

function <portlet:namespace />verImpagas(accion){
	jQuery('#<portlet:namespace />buscando').show();
	var cuit=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/empleadores_reimputacion_pagos&cuit='+cuit;
	    url +='&sucursal='+sucursal;
	    url +='&cmd=impagas';
	    url +='&accion='+accion; 
	    jQuery('#<portlet:namespace />divBoletasImpagas').load(url, function() {
	    	jQuery('#<portlet:namespace />buscando').hide();      															
      }
    );		
	
}


function <portlet:namespace />traerImpaga(){
	
	var cuit=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var boleta = jQuery("#<portlet:namespace/>numero_dest").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/empleadores_reimputacion_pagos&cuit='+cuit;
	    url +='&sucursal='+sucursal;
	    url +='&cmd=traerImpaga';
	    url +='&nro_boleta='+boleta;
	    jQuery('#<portlet:namespace />divBoletaDestino').load(url, function() {
    	            															
      }
    );		
}


function <portlet:namespace />validarCampos(){
	var result = true;
	
	var nroDest = jQuery('#<portlet:namespace />numero_dest').val();
	var importeDest =jQuery('#<portlet:namespace />importe_dest').val();
	var esAjuste=jQuery('#<portlet:namespace />ajusteChk').attr('checked');
	var importeAjuste =jQuery('#<portlet:namespace />importe_ajuste').val();
	
	if(nroDest==null || nroDest ==''){
		alert("Debe Seleccionar la boleta Impaga a la cual reimputar el pago");
		return false;
	}else{
		if(importeDest==null || importeDest =='' || parseFloat(importeDest)==0.00){
			alert("Debe Seleccionar la boleta Impaga a la cual reimputar el pago");
			return false;
		}
	}
	
	if(esAjuste && parseFloat(importeAjuste)==0){
		alert("El importe del Ajuste no puede ser CERO");
		return false;
	}
	
	return true;
}


function <portlet:namespace />guardar(){
	
	if (<portlet:namespace />validarCampos()) {
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/uoma/empleadores_reimputacion_pagos" />'+
		'<liferay-portlet:param name="cmd" value="add"/>'+
		'</liferay-portlet:renderURL>';

		submitForm(document.<portlet:namespace />fm, url);
	}
	return false;		
}

</script>