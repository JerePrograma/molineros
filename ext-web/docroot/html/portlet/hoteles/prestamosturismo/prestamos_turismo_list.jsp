<%@include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/hoteles/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	String ptoVtaAfip="00030";
	String seccional="";

	try{
		
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString();
		
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="9999";
		
	}
	
	try{
		seccional=user.getExpandoBridge().getAttribute("id_seccional").toString();
	}catch(Exception e){
		seccional="";
	}
	
	Calendar current = CalendarFactoryUtil.getCalendar();
	
	boolean showPrestamosSeccional=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_PRESTAMOS_TURISMO_SECCIONAL);
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
    <input name="<portlet:namespace />id_hotel" type="hidden"	value="<%=ptoVtaAfip%>" /> 
	<fieldset class="block-labels">
		<legend>Préstamos Turismo</legend>
		
		 <fieldset class="block-labels">
									<legend>
										<liferay-ui:message key="datos-afiliado" />
									</legend>
        <liferay-util:include page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
					<liferay-util:param name="edit_mode" value="<%=String.valueOf(true) %>" />
					<liferay-util:param name="discapacidad" value="<%= null %>" />
					<liferay-util:param name="pag_reintegro" value="<%= String.valueOf(true) %>" />
					<liferay-util:param name="from_reclamo" value="false" />
					<liferay-util:param name="cuil" value='' />
					<liferay-util:param name="inte" value=''/>
					<liferay-util:param name="origen" value="_filtro" />
		</liferay-util:include>
		</fieldset>
		
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		  <tr>
		      <td><label>Id:</label></td>
			  <td colspan="2"><input id="<portlet:namespace />idPrestamo_filtro" 
				     	name="<portlet:namespace />idPrestamo_filtro" size="20" maxlength="20" 
				     	type="text" value='' onkeydown="allowOnlyDigits(event);" /></td>
				       
			  <td>Hotel:</td> 
		      
		      <td><select name="<portlet:namespace />cod_Hotel_filtro" id="<portlet:namespace />cod_Hotel_filtro">
		          <option value="">Seleccione un Hotel</option>
								<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
									<%if(ptoVtaAfip.equalsIgnoreCase("9999")){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>"><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%}else if(ptoVtaAfip.equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>"><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%} %>
								<%} %>
			</select></td>
			
			 <td><label>Fecha Convenio Desde:</label></td>
						<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaDesdeDiaFiltro"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaDesdeMesFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaDesdeAnioFiltro"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						
			 <td><label>Hasta:</label></td>
						<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaHastaDiaFiltro"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaHastaMesFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaHastaAnioFiltro"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			</td>			
		   </tr>
		   <tr>
		     <td><label>Fecha Cuota Desde:</label></td>
						<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaDesdeDiaCuotaFiltro"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaDesdeMesCuotaFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaDesdeAnioCuotaFiltro"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						
			 <td><label>Hasta:</label></td>
						<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaHastaDiaCuotaFiltro"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaHastaMesCuotaFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaHastaAnioCuotaFiltro"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			</td>
		   </tr>
		   
		   <tr>
		      <td><label><liferay-ui:message key="seccional" />:</label></td>														
			  <td colspan="4" style="vertical-align:top" >
						       <liferay-util:include page='/html/portlet/autorizaciones/busqueda_seccional.jsp'>
						       <liferay-util:param value="_filtro_1" name="prefijo" />
						       </liferay-util:include>
		    </td>
		   </tr>

		</table>

		<fieldset>
			<input type="button" value="Buscar"
				onClick="<portlet:namespace />buscarPrestamos();" />&nbsp; 
				
			<%if(!showPrestamosSeccional){ %>	
			  <input type="button" value="Nuevo"
				onClick="<portlet:namespace />nuevoPrestamo();" />&nbsp;
			<%}%>	
						
			<input type="button" value="Exportar Cuenta Corriente"
				onClick="<portlet:namespace />exportarCC();" />&nbsp; 
				
		   
		   <label>Corte Cta Cte al: </label>	
			<liferay-ui:input-date
							dayParam="fechaDeudaCCDiaFiltro"
							dayValue="<%=current.get(Calendar.DAY_OF_MONTH) %>" 
							dayNullable="<%= false %>"
							monthParam="fechaDeudaCCMesFiltro"
							monthValue="<%=current.get(Calendar.MONTH) %>"	
							monthNullable="<%= false %>"			
							yearParam="fechaDeudaCCAnioFiltro"
							yearValue="<%=current.get(Calendar.YEAR) %>"
							yearNullable="<%= false %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />		       		
				
				
				
				
				
				
			<input type="radio" name="<portlet:namespace />exportConsolidado" value="true"
				checked="checked">Consolidado &nbsp;
			<input type="radio"	name="<portlet:namespace />exportConsolidado" value="false">Detalle &nbsp; &nbsp; &nbsp; &nbsp;

			<input type="checkbox" id="<portlet:namespace />exportSoloSaldo" 
			       name="<portlet:namespace />exportSoloSaldo" 
			       value="false" />Solo con Saldo &nbsp;
			       
		    <input type="button" value="Exportar a Planilla"
				onClick="<portlet:namespace />exportarPlanilla();" />&nbsp; 
				
			<label>Deuda exigible al: </label>	
			<liferay-ui:input-date
							dayParam="fechaDeudaDiaFiltro"
							dayValue="<%=current.get(Calendar.DAY_OF_MONTH) %>" 
							dayNullable="<%= false %>"
							monthParam="fechaDeudaMesFiltro"
							monthValue="<%=current.get(Calendar.MONTH) %>"	
							monthNullable="<%= false %>"			
							yearParam="fechaDeudaAnioFiltro"
							yearValue="<%=current.get(Calendar.YEAR) %>"
							yearNullable="<%= false %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />		       
					
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
		</fieldset>	
	  
		<div id="<portlet:namespace />div_prestamos">
			<liferay-util:include
						page='/html/portlet/hoteles/prestamosturismo/prestamos_turismo_result.jsp'>
				<liferay-util:param value="<%= String.valueOf(true) %>"
									name="edit_mode" />
			</liferay-util:include>
		</div>
		
	</fieldset>
</form>		

<script type="text/javascript">
    jQuery('#<portlet:namespace />buscando').hide();	
	
    <portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
		var esSeccional='<%=showPrestamosSeccional %>';

		if(esSeccional!='true'){
		  jQuery('#<portlet:namespace />id_seccional_filtro').val("");
		  jQuery('#<portlet:namespace />seccional_filtro').val("");
		  
		  jQuery('#<portlet:namespace />id_seccional_filtro_1').attr("disabled",false);
		  jQuery('#<portlet:namespace />seccional_filtro_1').attr("disabled",false);
		  
		  
		}else{
			
		  jQuery('#<portlet:namespace />id_seccional_filtro_1').val("<%=seccional%>");
		  <portlet:namespace />buscarSeccional_filtro_1();
		  jQuery('#<portlet:namespace />id_seccional_filtro_1').attr("disabled",true);
		  jQuery('#<portlet:namespace />seccional_filtro_1').attr("disabled",true);
		}  
	}
	
	function <portlet:namespace />nuevoPrestamo() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function ejecutaConsulta(esBusqueda) {				
		
		var cuil=jQuery('#<portlet:namespace />cuil_filtro').val();
		var inte=jQuery('#<portlet:namespace />inte_filtro').val();
		var id=jQuery('#<portlet:namespace />idPrestamo_filtro').val();
		var hotel=jQuery('#<portlet:namespace />cod_Hotel_filtro').val();
		
		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");
		
		var fechaHastaDia  = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
		var fechaHastaMes= document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");
	
		var fechaDesdeCuotaDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaCuotaFiltro");
		var fechaDesdeCuotaMes= document.getElementById("<portlet:namespace />fechaDesdeMesCuotaFiltro");
		var fechaDesdeCuotaAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioCuotaFiltro");
	
		var fechaHastaCuotaDia  = document.getElementById("<portlet:namespace />fechaHastaDiaCuotaFiltro");
		var fechaHastaCuotaMes= document.getElementById("<portlet:namespace />fechaHastaMesCuotaFiltro");
		var fechaHastaCuotaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioCuotaFiltro");
		
		var seccional = document.getElementById("<portlet:namespace />id_seccional_filtro_1");
		
		var fechaDeudaDia  = document.getElementById("<portlet:namespace />fechaDeudaDiaFiltro");
		var fechaDeudaMes= document.getElementById("<portlet:namespace />fechaDeudaMesFiltro");
		var fechaDeudaAnio = document.getElementById("<portlet:namespace />fechaDeudaAnioFiltro");
		
		
		var fechaDeudaCCDia  = document.getElementById("<portlet:namespace />fechaDeudaCCDiaFiltro");
		var fechaDeudaCCMes= document.getElementById("<portlet:namespace />fechaDeudaCCMesFiltro");
		var fechaDeudaCCAnio = document.getElementById("<portlet:namespace />fechaDeudaCCAnioFiltro");

		if (esBusqueda) {

	 		jQuery('#<portlet:namespace />buscando').show();
		 	var busquedaNom = {"cuil":cuil,"inte":inte,
		 			"fechadesdedia":fechaDesdeDia.value,
		 			"fechadesdemes":fechaDesdeMes.value,
			        "fechadesdeanio":fechaDesdeAnio.value,
			        "hotel":hotel,
			        "id":id,
			        "cmd":"filterPrestamo",
			        "fechahastadia":fechaHastaDia.value,
			        "fechahastames":fechaHastaMes.value,
			        "fechahastaanio":fechaHastaAnio.value,
			        "fechadesdecuotadia":fechaDesdeCuotaDia.value,
			        "fechadesdecuotames":fechaDesdeCuotaMes.value,
			        "fechadesdecuotaanio":fechaDesdeCuotaAnio.value,
			        "fechahastacuotadia":fechaHastaCuotaDia.value,
			        "fechahastacuotames":fechaHastaCuotaMes.value,
			        "fechahastacuotaanio":fechaHastaCuotaAnio.value,
			        "seccional":seccional.value,
			        "fechadeudadia" : fechaDeudaDia.value,
		 			"fechadeudames" : fechaDeudaMes.value,
		 			"fechadeudaanio": fechaDeudaAnio.value,
		 			"fechaccdia" : fechaDeudaCCDia.value,
		 			"fechaccmes" : fechaDeudaCCMes.value,
		 			"fechaccanio": fechaDeudaCCAnio.value};

			
		 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" /></portlet:renderURL>';
		 	jQuery('#<portlet:namespace />div_prestamos').load(url,busquedaNom, function(){
																jQuery('#<portlet:namespace />buscando').hide();      
			});

		} else {
					
			var reporteConsolidado = jQuery("input[name='<portlet:namespace />exportConsolidado']:checked").val();
			
			var chkSaldo = document.getElementById("<portlet:namespace />exportSoloSaldo");
			var reporteSoloSaldo = chkSaldo.checked ? 'true' : 'false';			
			
	 		//alert ('Reporte XLS ' + ((reporteConsolidado == 'true') ? ' Consolidado' : 'Detallado'));
	 		var reportKey = ((reporteConsolidado == 'true') ? 'CUENTAS_CORRIENTES_TUR_PRE_HD' : 'CUENTAS_CORRIENTES_TUR_PRE_IT');
	 		var url = '/xlsservlet/?reporte=' + reportKey
 			+ '&cuil=' + cuil
 			+ '&inte=' + inte
 			+ '&soloConSaldo='   + reporteSoloSaldo
 			+ '&fechadesdedia='  + fechaDesdeDia.value
 			+ '&fechadesdemes='  + fechaDesdeMes.value
 			+ '&fechadesdeanio=' + fechaDesdeAnio.value
 			+ '&hotel='          + hotel
 			+ '&id='             + id
 			+ '&cmd='            + "filterPrestamo"	        
 			+ '&fechahastadia='  + fechaHastaDia.value
 			+ '&fechahastames='  + fechaHastaMes.value
 			+ '&fechahastaanio=' + fechaHastaAnio.value
 			+ '&fechadesdecuotadia='  + fechaDesdeCuotaDia.value
 			+ '&fechadesdecuotames='  + fechaDesdeCuotaMes.value
 			+ '&fechadesdecuotaanio=' + fechaDesdeCuotaAnio.value
 			+ '&fechahastacuotadia='  + fechaHastaCuotaDia.value
 			+ '&fechahastacuotames='  + fechaHastaCuotaMes.value
 			+ '&fechahastacuotaanio=' + fechaHastaCuotaAnio.value
 			
 			+ '&fechaccdia='  + fechaDeudaCCDia.value
 			+ '&fechaccmes='  + fechaDeudaCCMes.value
 			+ '&fechaccanio=' + fechaDeudaCCAnio.value
 			
 			+ '&seccional=' + seccional.value;
 			
	 		url += '&rnd=' + Math.floor(Math.random()*100);
	 		window.location.href =url;

		}						
	}
	
	function <portlet:namespace />exportarCC() {	
		/* Mismos parametros json pero llamando al reporte */
		ejecutaConsulta(false);
	}
	
	function <portlet:namespace />buscarPrestamos() {		
		/* Mismos parametros json pero llamando a la busqueda */
		ejecutaConsulta(true);
	}
	
	
	function <portlet:namespace />exportarPlanilla() {	
		var cuil=jQuery('#<portlet:namespace />cuil_filtro').val();
		var inte=jQuery('#<portlet:namespace />inte_filtro').val();
		var id=jQuery('#<portlet:namespace />idPrestamo_filtro').val();
		var hotel=jQuery('#<portlet:namespace />cod_Hotel_filtro').val();
		
		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");
		
		var fechaHastaDia  = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
		var fechaHastaMes= document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");
	
		var fechaDesdeCuotaDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaCuotaFiltro");
		var fechaDesdeCuotaMes= document.getElementById("<portlet:namespace />fechaDesdeMesCuotaFiltro");
		var fechaDesdeCuotaAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioCuotaFiltro");
	
		var fechaHastaCuotaDia  = document.getElementById("<portlet:namespace />fechaHastaDiaCuotaFiltro");
		var fechaHastaCuotaMes= document.getElementById("<portlet:namespace />fechaHastaMesCuotaFiltro");
		var fechaHastaCuotaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioCuotaFiltro");
		
		var seccional = document.getElementById("<portlet:namespace />id_seccional_filtro_1");
		
		
		var reporteConsolidado = jQuery("input[name='<portlet:namespace />exportConsolidado']:checked").val();
		
		var chkSaldo = document.getElementById("<portlet:namespace />exportSoloSaldo");
		var reporteSoloSaldo = chkSaldo.checked ? 'true' : 'false';		
		
		
		var fechaDeudaDia  = document.getElementById("<portlet:namespace />fechaDeudaDiaFiltro");
		var fechaDeudaMes= document.getElementById("<portlet:namespace />fechaDeudaMesFiltro");
		var fechaDeudaAnio = document.getElementById("<portlet:namespace />fechaDeudaAnioFiltro");
		
		var fechaDeudaCCDia  = document.getElementById("<portlet:namespace />fechaDeudaCCDiaFiltro");
		var fechaDeudaCCMes= document.getElementById("<portlet:namespace />fechaDeudaCCMesFiltro");
		var fechaDeudaCCAnio = document.getElementById("<portlet:namespace />fechaDeudaCCAnioFiltro");

							
		var reportKey = 'REPORTE_PRESTAMOS_TURISMO';
	 		var url = '/xlsservlet/?reporte=' + reportKey
 			+ '&cuil=' + cuil
 			+ '&inte=' + inte
 			
 			+ '&soloConSaldo='   + reporteSoloSaldo
 			+ '&fechadesdedia='  + fechaDesdeDia.value
 			+ '&fechadesdemes='  + fechaDesdeMes.value
 			+ '&fechadesdeanio=' + fechaDesdeAnio.value
 			+ '&hotel='          + hotel
 			+ '&id='             + id
 			+ '&fechahastadia='  + fechaHastaDia.value
 			+ '&fechahastames='  + fechaHastaMes.value
 			+ '&fechahastaanio=' + fechaHastaAnio.value
 			+ '&fechadesdecuotadia='  + fechaDesdeCuotaDia.value
 			+ '&fechadesdecuotames='  + fechaDesdeCuotaMes.value
 			+ '&fechadesdecuotaanio=' + fechaDesdeCuotaAnio.value
 			+ '&fechahastacuotadia='  + fechaHastaCuotaDia.value
 			+ '&fechahastacuotames='  + fechaHastaCuotaMes.value
 			+ '&fechahastacuotaanio=' + fechaHastaCuotaAnio.value
 			+ '&seccional=' + seccional.value
 			+ '&fechadeudadia='  + fechaDeudaDia.value
 			+ '&fechadeudames='  + fechaDeudaMes.value
 			+ '&fechadeudaanio=' + fechaDeudaAnio.value
 			+ '&fechaccdia='  + fechaDeudaCCDia.value
 			+ '&fechaccmes='  + fechaDeudaCCMes.value
 			+ '&fechaccanio=' + fechaDeudaCCAnio.value
 			;
 			
	 		url += '&rnd=' + Math.floor(Math.random()*100);
	 		window.location.href =url;
	}

</script>

