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
	   <td><label><liferay-ui:message key="periodo-desde" />:</label></td>
	   <td>
		 	<liferay-ui:input-date
				dayParam="fechaDesdeDia"
				dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
				dayNullable="<%= true %>" 
				monthParam="fechaDesdeMes"
				monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
				monthNullable="<%= true %>"				
			    yearParam="fechaDesdeAnio"
				yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
				yearNullable="<%= true %>"
				yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 5 %>"
				yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
				firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>" />
		</td>
		<td><label><liferay-ui:message key="periodo-hasta" />:</label></td>
		<td>
			<liferay-ui:input-date
				dayParam="fechaHastaDia"																					
				dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
				dayNullable="<%= true %>"
				monthParam="fechaHastaMes"
				monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
				monthNullable="<%= true %>"
				yearParam="fechaHastaAnio"
				yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
				yearNullable="<%= true %>"
				yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
				yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)+3 %>"
				firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>" />
		</td>
	   
	    <td><label><liferay-ui:message key="codigo"/>:</label></td>
		<td><input id="<portlet:namespace />codigoTratamiento" 
		      name="<portlet:namespace />codigoTratamiento" size="20" maxlength="20" type="text" value=''/></td>
		      
        <td><label><liferay-ui:message key="estado" />:</label></td>
        <td colspan="1">
             <select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado">
			             <option value="0"></option>
			             <option value="1">En Curso</option>
			             <option value="2">Documentación Faltante</option>
			             <option value="3">Cambio Prestador</option>
			             <option value="4">Finalizado</option>
			             <option value="5">Abandonado</option>
             </select>
        </td>	
		           
	    <td>
		       <input type="button" value="<liferay-ui:message key="buscar-tratamientos" />" 
		            onClick="<portlet:namespace />busquedaTD();" />
	    </td>
        </tr>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoTratamientos">
				</div>
			</td>
		</tr>
		
		<tr>
			<td colspan="12">
				<div align="center" id="<portlet:namespace />tratamientosDiv">
					<liferay-util:include page="/html/portlet/autorizaciones/seguimiento_sur/tratamientos_search_result.jsp">
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
    var popupMD;
  
	jQuery('#<portlet:namespace />agregandoTratamientos').hide();
	
	function <portlet:namespace />busquedaTD(){

		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();

		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();

		var codPrestad=jQuery('#<portlet:namespace />cuit_prestador').val();
		var prestador=jQuery('#<portlet:namespace />nombre_prestador').val();

		var estado=jQuery('#<portlet:namespace />estado').val();

		var codPrestaci=jQuery('#<portlet:namespace />codigoTratamiento').val();
		
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
		
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		jQuery('#<portlet:namespace />agregandoTratamientos').show();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_tratamientos_discapacidad&entidad='+entidad+		
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&codPrest='+codPrestad+'&prestador='+encodeURI(prestador)+'&numero=0'+'&estado='+estado+'&codPrestaci='+codPrestaci;
		
		if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Tratamientos",modal:true,width:700,onClose: function() { popupMD = null;}});
		
		jQuery(popupMD).load(url);

	}
	
	function <portlet:namespace />seleccionarTratamientos(inputs){	
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/selecciontratamientodiscapacidad';
        url += "&tratamientos=" + encodeURI(inputs);
        
		jQuery('#<portlet:namespace />tratamientosDiv').load(url,
				function() {
								Liferay.Popup.close(popupMD);
						   }
		);		
	}

	function verComprobantes(idTrat){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_comprobantes_tratamientos_discapacidad&idtratamiento='+idTrat;
		
        var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		
		url += '&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		       '&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;

		if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Comprobantes",modal:true,width:700,onClose: function() { popupMD = null;}});
		
		jQuery(popupMD).load(url);
	}
	
	function borraTratamiento(idTrat){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/eliminatratamientodiscapacidad';
        url += "&tratamientoid=" + encodeURI(idTrat);
        
		jQuery('#<portlet:namespace />tratamientosDiv').load(url);	
	}
	
	function <portlet:namespace />seleccionarComprobantes(inputs){	
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/seleccioncomprobantestratamientodiscapacidad';
        url += "&comprobantes=" + encodeURI(inputs);
        
		jQuery('#<portlet:namespace />selecciontratamientodiscapacidaddiv').load(url,
				function() {
			                	Liferay.Popup.close(popupMD);
						   }
		);
	}
</script>