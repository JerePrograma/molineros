<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		String portlet_name = "tesoreria";		
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
				portlet_name = "farmacia";
		}		
		if(renderResponse.getNamespace().equals("_EST_1_")){
				portlet_name = "estudio_isidro";
		}
		if(renderResponse.getNamespace().equals("_LIQ_1_")){
				portlet_name = "liquidaciones";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
				portlet_name = "uoma";
		}
		
		List<CuentaBancaria> ctas=null;
		ctas = (ArrayList<CuentaBancaria>) portletSession.getAttribute(WebKeysTesoreria.CUENTAS_BCRIAS,PortletSession.APPLICATION_SCOPE);
		if(null==ctas){
			ctas=TraeListasServiceUtil.getCtasBcrias();
			portletSession.setAttribute(WebKeysTesoreria.CUENTAS_BCRIAS, ctas, PortletSession.APPLICATION_SCOPE);
		}
		
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_CHEQUES);
%>
		<fieldset class="block-labels">
						<legend><liferay-ui:message key="alta-chequera" /></legend>
						<table class="lfr-table">
							<tr>
								<td><label><liferay-ui:message key="cuenta-bancaria" />:</label></td>
								<td>							
									<select id="<portlet:namespace />id_cta_bcria" name="<portlet:namespace />id_cta_bcria" onchange="sugerirNroCheque()">
										<% 	for (CuentaBancaria cta : ctas) { 
												if (portlet_name.equals("liquidaciones") && cta.getEntidad().equals("O")) {%>
													<option value="<%=cta.getId_cuenta_bcria()%>" <% if (cta.getId_cuenta_bcria() == 2) {%> selected="selected" <%} %>><%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
											<%  }else if(portlet_name.equals("farmacia") && cta.getEntidad().equals("A")){%>
													<option value="<%=cta.getId_cuenta_bcria()%>"> <%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
											<%  }else if(portlet_name.equals("uoma") && cta.getEntidad().equals("U")){%>
		 											<option value="<%=cta.getId_cuenta_bcria()%>"> <%=cta.getDescripcion()%>&nbsp;<%=cta.getNro_cuenta()%>/<%=String.valueOf(cta.getSucursal())%></option>
		    								  <%}
										    } %>
									</select>
								</td>
								
								<td>&nbsp;</td>
								<td><label><liferay-ui:message key="cheque-desde" />:</label></td>
								<td><input id="<portlet:namespace />numeroDesde" name="<portlet:namespace />numeroDesde" size="10" maxlength="10" type="text" value="" /></td>
								<td><label><liferay-ui:message key="cheque-hasta" />:</label></td>
								<td>							
									<input id="<portlet:namespace />numeroHasta" name="<portlet:namespace />numeroHasta" size="10" maxlength="10" type="text" value="" 
									 />&nbsp;&nbsp;&nbsp;&nbsp;
									<% if(showABMButtons){%>
									<input id="<portlet:namespace />agregar" value="<liferay-ui:message key="agregar"/>" title="<liferay-ui:message key="agregar" />" type="button"/>
									<%} %>							
								</td>
								<td>
									<div align="center" id="<portlet:namespace />busquedaChequerasDiv">
										<liferay-util:include page="/html/portlet/liquidaciones/cheques/chequeras_result.jsp"/>
									</div>
								</td>								
							</tr>	
						</table>	      	  
		</fieldset>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="busqueda-cheques" /></legend>
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="cuit" />:</label></td>
						<td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="numero" />:</label></td>
						<td><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="10" maxlength="10" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
					</tr>
					<tr>
						<td colspan="11">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="11">
							&nbsp;(<liferay-ui:message key="refine-busqueda" />)
						</td>
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
			<div align="center" id="<portlet:namespace />busquedaChequeDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var numero=jQuery('#<portlet:namespace />numero').val();
		
		if(!<portlet:namespace />validarBusqueda(cuit,numero)){
			return false;
		}		
		if(cuit.length>0){
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuit'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
		}		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_cheques&cuit='+cuit+'&numero='+numero;

		jQuery('#<portlet:namespace />busquedaChequeDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusqueda(cuit,descripcion){		
		if(trim(cuit.length)==0 && trim(descripcion.length)==0){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}

	function <portlet:namespace />altaCheque() {
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/<%=portlet_name%>/alta_cheque_entry" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     
	
	jQuery('#<portlet:namespace />agregar').click(function(){
		var ctaBcria=jQuery('#<portlet:namespace />id_cta_bcria').val();
		var numeroDesde=jQuery('#<portlet:namespace />numeroDesde').val()||0;
		var numeroHasta=jQuery('#<portlet:namespace />numeroHasta').val()||0;		
		if(parseInt(numeroDesde)==parseInt(numeroHasta) || parseInt(numeroDesde)>parseInt(numeroHasta) || parseInt(numeroDesde)==0 || parseInt(numeroHasta)==0){
			alert('<liferay-ui:message key="error-numeracion-chequera"/>');
			return false;
		}	
		
		var qCheques=(parseInt(numeroHasta)-parseInt(numeroDesde)+1).toString();
		if(!confirm(" Se van a dar de alta " + qCheques + " Cheques" )){
			return false;
		}
		
		jQuery('#<portlet:namespace />buscando').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_chequera&ctaBcria='+ctaBcria+'&numeroDesde='+numeroDesde+'&numeroHasta='+numeroHasta;

		jQuery('#<portlet:namespace />busquedaChequerasDiv').load(url, function() {        																
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	});
	
	function borrarChequera(id_chequera){		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_chequera&id_chequera='+id_chequera;
		url+='&borrar=true';

		jQuery('#<portlet:namespace />busquedaChequerasDiv').load(url, function() {        																
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	
	}
	
	function exportarChequeraExcel(cta_bcria, desde, hasta){
		var url = '/xlsservlet/?reporte=LISTADO_CHEQUES'
					+ '&id_banco=-1'
					+ '&depositados=-1'
					+ '&rechazados=-1'
					+ '&reemplazados=-1'
					+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'					
					+ '&nro_cheque=' + cta_bcria;		
					+ '&id_cta_bcria=' + cta_bcria;		
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
	}
	
	function sugerirNroCheque(){
		var id = document.getElementById("<portlet:namespace />id_cta_bcria").value;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_proximo_nro_cheque_nueva_chequera&id_cta_bcria='+id;
			url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
			url += '&rnd=' + Math.floor(Math.random()*100);
			url += '&rnd=' + Math.floor(Math.random()*100);
				
	
		jQuery.ajax({   
				url: url,
				success: function(data){	
					var obj = jQuery.parseJSON(data);
					jQuery("#<portlet:namespace />numeroDesde").val(parseInt(obj.numero) +1); 
					jQuery("#<portlet:namespace />numeroHasta").val(""); 
				}
		});
		
     }
	
	sugerirNroCheque();
	
</script>
