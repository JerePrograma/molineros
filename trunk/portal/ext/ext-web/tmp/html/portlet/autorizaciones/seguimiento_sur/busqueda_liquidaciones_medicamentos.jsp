<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
 
<%

String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}
String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);

Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
fechaDesde.setTime(new Date());
Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
fechaHasta.setTime(new Date());

%>


<table width="100%">
  <tr>
	<td width="50%" valign="top">
	<table class="lfr-table" width="100%">
	   <tr>
	     <td>
	        <table>
	         <tr>
		     <td>
		        <liferay-util:include page="/html/portlet/autorizaciones/seguimiento_sur/busqueda_medicamentos.jsp">
							<liferay-util:param name="esEditable" value='true'/>
							<liferay-util:param name="search_url"	value="/autorizaciones/buscar_medicamentos_seguimientosur" />
							<liferay-util:param name="troquel" value=''/>
				</liferay-util:include>			   
		     </td>
		     <td><label id="<portlet:namespace/>drogaMedicamentoLb"><liferay-ui:message key="droga"/>:</label></td>
		     <td>      
			    <select name="<portlet:namespace/>drogaMedicamento" id="<portlet:namespace/>drogaMedicamento"  >
				    <option value="0">Seleccione una droga</option>
				</select>      
			 </td>
			 
			 
			 </tr>
		    </table>
		  </td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr> 
	    
	    <tr> 
	    <td colspan="5">
		    <table class="lfr-table">
			    <tr>
				    <td><label><liferay-ui:message key="cuit"/>:</label></td>
					<td><input id="<portlet:namespace />cuitLiquidacion" 
					      name="<portlet:namespace />cuitLiquidacion" size="10" maxlength="13" type="text" value=''/></td>
					      
					<td><label><liferay-ui:message key="prestador"/>:</label></td>
					<td><input id="<portlet:namespace />prestadorSur" 
					      name="<portlet:namespace />prestadorSur" size="60" maxlength="70" type="text" value=''  /></td> 
					      
					<td>     
					 <a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarPrestador();" tabindex="-1">Buscar</a>
					</td> 
					<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
					<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
					<td>&nbsp;</td><td>&nbsp;</td>
					<td>
			            <input type="button" value="<liferay-ui:message key="buscar-liquidaciones" />" 
			            onClick="<portlet:namespace />busquedaLiq();" />
		            </td>           
				</tr>      
			</table>      
         </td>
        </tr>
        <tr>
			<td>&nbsp;</td>
		 </tr> 
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoLiquidaciones">
				</div>
			</td>
		</tr>
		
		<tr>
			<td colspan="12">
				<div align="center" id="<portlet:namespace />liquidacionesDiv">
					<liferay-util:include page="/html/portlet/autorizaciones/seguimiento_sur/medicamentosliquidaciones_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
		
		
	</table>
	</td>
  </tr>
</table>

<script type="text/javascript">
    var popupMDL;
    var popupPD;
	jQuery('#<portlet:namespace />agregandoLiquidaciones').hide();
	
	function <portlet:namespace />busquedaLiq(){
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var idPrestacion= jQuery('#<portlet:namespace />id_medicamento').val();
		var cuit=jQuery('#<portlet:namespace />cuitLiquidacion').val();
		var importe=jQuery('#<portlet:namespace />importeLiquidacion').val();
		var cantidad=jQuery('#<portlet:namespace />cantidadLiquidacion').val();
        var periodicidad=jQuery('#<portlet:namespace/>bimestre').val();
        var ejercicio=jQuery('#<portlet:namespace/>ejercicio').val();
        var droga=jQuery('#<portlet:namespace/>drogaMedicamento').val();
        var prestador=jQuery('#<portlet:namespace/>prestadorSur').val();
        var clase=jQuery("#<portlet:namespace />claseExpediente").val();
        
		if(trim(cuil).length != 0 && !validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			alert("Cuil inválido");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		if(trim(cuil).length == 0){
			alert("Debe ingresar un cuil");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		jQuery('#<portlet:namespace />agregandoTratamientos').show();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/traer_liquidaciones_medicamentos&entidad='+entidad+		
		'&cuil_titular='+cuil+'&inte='+inte+'&id_prestacion='+idPrestacion+
		'&cuit_prestador='+cuit+'&desc_prestador='+encodeURI(prestador)+'&droga='+encodeURI(droga)+
		'&periodicidad='+periodicidad+'&ejercicio='+ejercicio+'&clase='+clase;
		if(popupMDL==null)
    		popupMDL = Liferay.Popup({title:"Búsqueda Liquidaciones",modal:true,width:900,onClose: function() { popupMDL = null;}});
		
		jQuery(popupMDL).load(url);

	}
	

   function verComprobantesME(idLiquidacion,idPrestacion,orden){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/muestracomprobantemedicamentoliquidacion';
		url += "&idLiquidacion=" + encodeURI(idLiquidacion);
	    url += "&idPrestacion=" + encodeURI(idPrestacion);
	    url += "&orden=" + encodeURI(orden);
		if(popupMDL==null)
    		popupMDL = Liferay.Popup({title:"Comprobante",modal:true,width:400,onClose: function() { popupMDL = null;}});
		
		jQuery(popupMDL).load(url);
	}
	
	function borraComprobanteME(idLiquidacion,idPrestacion,orden){
		var valorUnitario=jQuery("#<portlet:namespace />valorUnitarioMedicamentoSur").val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/eliminamedicamentoliquidacion';
        url += "&idLiquidacion=" + encodeURI(idLiquidacion);
        url += "&idPrestacion=" + encodeURI(idPrestacion);
        url += "&orden=" + encodeURI(orden);
        url += "&valorunitario="+encodeURI(valorUnitario);
        
		jQuery('#<portlet:namespace />liquidacionesDiv').load(url);	
	}
	
	function <portlet:namespace />seleccionarComprobantesME(inputs){	
		var valorUnitario=jQuery("#<portlet:namespace />valorUnitarioMedicamentoSur").val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/seleccioncomprobantesmedicamentos';
        url += "&comprobantes=" + encodeURI(inputs);
        url += "&valorunitario="+ encodeURI(valorUnitario);
		jQuery('#<portlet:namespace />liquidacionesDiv').load(url,
				function() {
			                	Liferay.Popup.close(popupMDL);
						   }
		);		
	}
	
	function <portlet:namespace />buscarPrestador() {
		var cuit=jQuery("#<portlet:namespace />cuitLiquidacion").val();
		var prestador=jQuery("#<portlet:namespace />prestadorSur").val();    
		if (cuit == null){
			cuit = "";
		}    
		if (prestador==null){
			prestador = "";
		}
		if(cuit.length == 0 && prestador.length==0) {
		   alert("Debe ingresar algún parámetro");
		}else {
			popupPD = Liferay.Popup({title:"Búsqueda Prestador",modal:true,width:420});
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_prestador';
		    url+='&cuit_prestador='+cuit+'&nombre_prestador='+encodeURI(prestador);
			jQuery(popupPD).load(url);
			
		}
	}
	
	function seleccionaCamposPrestad(cuit, descripcion, id) {
	    jQuery("#<portlet:namespace />cuitLiquidacion").val(cuit);
	    jQuery("#<portlet:namespace />prestadorSur").val(descripcion);
	}

	function pasarParametrosAParentPd(cuit, descripcion, id) {
		seleccionaCamposPrestad(cuit,descripcion,id);
	    <portlet:namespace />cerrarPd();
	}
	
	function <portlet:namespace />cerrarPd(){
		if(popupPD){
			Liferay.Popup.close(popupPD);
		}
	}
	
	function <portlet:namespace />calculaImportePresentadoMedicamentoSur(){
		var valorUnitario=jQuery("#<portlet:namespace />valorUnitarioMedicamentoSur").val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/calculaimportepresentado';
        url += "&valorunitario="+ encodeURI(valorUnitario);
		jQuery('#<portlet:namespace />liquidacionesDiv').load(url,
				function() {}
		);
	}
</script>