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
	boolean showAprobacion=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_APROBACION_RECIBOS);
	String ptoVtaAfip="00030";

	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="00030";
	}
	
	
	
	Recibo filtro=(Recibo)session.getAttribute(WebKeysHoteles.RECIBOS_FILTRO);
	
	
	
	Calendar currentDde = CalendarFactoryUtil.getCalendar();
	Calendar currentHta = CalendarFactoryUtil.getCalendar();
	if(filtro!=null && filtro.getFechaDdeFiltro()!=null){
		currentDde.setTime(filtro.getFechaDdeFiltro());
	}
	
	if(filtro!=null && filtro.getFechaHtaFiltro()!=null){
		currentHta.setTime(filtro.getFechaHtaFiltro());
	}
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
    <input name="<portlet:namespace />id_hotel"  id="<portlet:namespace />id_hotel" type="hidden"	value="<%=ptoVtaAfip%>" /> 
	<fieldset class="block-labels">
		<legend>Gestión de Recibos</legend>
		
		<table class="lfr-table">
		  <tr>
		  <td><liferay-ui:message key="fc-suc" /> </td> 
				<td><select name="<portlet:namespace />rc_sucursal_filtro" id="<portlet:namespace />rc_sucursal_filtro" >
				<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
						<%if(ptoVtaAfip.equalsIgnoreCase("9999")){ %>
							<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>"><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
						<%}else if(ptoVtaAfip.equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %>
							<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" ><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
						<%} %>
				<%} %>
			</select></td>
		  
		   <td><label>Desde:</label></td>
		   <td colspan="2">
				<liferay-ui:input-date
						dayParam="fechaDesdeDiaFiltro"
						dayValue="<%=currentDde.get(Calendar.DATE) %>" 
						dayNullable="<%= false %>"
						monthParam="fechaDesdeMesFiltro"
						monthValue="<%= currentDde.get(Calendar.MONTH) %>"	
						monthNullable="<%= false %>"			
						yearParam="fechaDesdeAnioFiltro"
						yearValue="<%= currentDde.get(Calendar.YEAR) %>"
						yearNullable="<%= false %>"
						yearRangeStart="<%= currentDde.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= currentDde.get(Calendar.YEAR)%>"
						firstDayOfWeek="<%= currentDde.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
			</td>
						
			<td><label>Hasta:</label></td>
			<td colspan="2">
				<liferay-ui:input-date
							dayParam="fechaHastaDiaFiltro"
							dayValue="<%=currentHta.get(Calendar.DATE) %>" 
							dayNullable="<%= false %>"
							monthParam="fechaHastaMesFiltro"
							monthValue="<%= currentHta.get(Calendar.MONTH) %>"	
							monthNullable="<%= false %>"			
							yearParam="fechaHastaAnioFiltro"
							yearValue="<%= currentHta.get(Calendar.YEAR) %>"
							yearNullable="<%= false %>"
							yearRangeStart="<%= currentHta.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= currentHta.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= currentHta.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			</td>
		   
		    <td><label>Número:</label></td>
			<td><input id="<portlet:namespace />nroFiltro" 
					name="<portlet:namespace />nroFiltro" size="20"
					maxlength="10" type="text" 
					value='<%=filtro!=null && filtro.getNumero()!=null?filtro.getNumero():"" %>' />
			</td>
		  </tr>
		  <tr><td>&nbsp;</td></tr>
		 </table>
		 <table class="lfr-table"> 
		  <tr>
		    <td><label>Cliente Descripción:</label></td>
			<td colspan="3"><input id="<portlet:namespace />clienteFiltro" 
					name="<portlet:namespace />clienteFiltro" size="50"
					maxlength="50" type="text" 
					value='<%=filtro!=null && filtro.getCliente()!=null && filtro.getCliente().getRazonSocial()!=null ?filtro.getCliente().getRazonSocial():"" %>' />
			</td>
			
			<td><label>Cliente Documento:</label></td>
			<td><input id="<portlet:namespace />nroDocFiltro" 
					name="<portlet:namespace />nroDocFiltro" size="15"
					maxlength="11" type="text" 
					value='<%=filtro!=null && filtro.getCliente()!=null && filtro.getCliente().getCuit()!=null ?filtro.getCliente().getCuit():"" %>' /> </td>
			
		  </tr>
		  <tr>
		    <td>&nbsp;</td>
		  </tr>
		</table>
		
		<div id="<portlet:namespace />divEstado">
		  <table>
		     <tr>
		       <td>
		         Estado:
		       </td>
		       <td>
		         <select name="<portlet:namespace />rc_estado_filtro" id="<portlet:namespace />rc_estado_filtro" >
		            <option value="0">Todos</option>
		            <option value="1">Pendientes</option>
		            <option value="2">Aprobados</option>
		         </select>
		       </td>
		     </tr>
		     <tr>
		       <td>&nbsp;</td>
		     </tr>
		  </table>
		</div>
		
		<table>
		 <tr>
		   <td> 
		     <input type="button" value="Buscar" onClick="<portlet:namespace />buscarRecibos();"/>&nbsp;
		   </td>
		   <td>&nbsp;</td>
		  <td>   
			<input type="button" value="Nuevo" id="<portlet:namespace />newRecibo" 
			name="<portlet:namespace />newRecibo" 
			onClick="<portlet:namespace />nuevoRecibo();"/>&nbsp;
		  </td>
		  <td>   
			<input type="button" value="Aprobar" id="<portlet:namespace />aprobarRecibo" 
			name="<portlet:namespace />aprobarRecibo" onClick="<portlet:namespace />aprobarRecibos();"/>&nbsp;
		  </td>
		  </tr>  	
		</table>
	</fieldset>
	
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
	
	<div id="<portlet:namespace />div_recibos">
			<jsp:include page='/html/portlet/hoteles/recibos_result.jsp' />  	
	</div>
	
	
	<input type="hidden" name="<portlet:namespace />id_hotel"
		id="<portlet:namespace />id_hotel" value="<%=ptoVtaAfip%>" />	
		
</form>		

<script type="text/javascript">
		
	var popupMD;
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
		var puedeAprobar='<%=showAprobacion%>'==='true';
		
		jQuery('#<portlet:namespace />buscando').hide();
		if(puedeAprobar){
		  jQuery('#<portlet:namespace />divEstado').show();	
		  jQuery('#<portlet:namespace />aprobarRecibo').show();	
		  jQuery('#<portlet:namespace />newRecibo').hide();	
		}else{
		  jQuery('#<portlet:namespace />divEstado').hide();
		  jQuery('#<portlet:namespace />aprobarRecibo').hide();
		  jQuery('#<portlet:namespace />newRecibo').show();
		}  
	}
	
	function <portlet:namespace />nuevoRecibo() {
		var params = "&<%= Constants.CMD %>=" + "new"+"&id_hotel=<%=ptoVtaAfip %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
    function <portlet:namespace />buscarRecibos(){
		
    	var idHotel=jQuery('#<portlet:namespace />rc_sucursal_filtro').val();
    	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");

		var fechaHastaDia  = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
		var fechaHastaMes= document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");
		var id=jQuery('#<portlet:namespace />nroFiltro').val();
		var cliente=jQuery('#<portlet:namespace />clienteFiltro').val();
		var clienteDoc=jQuery('#<portlet:namespace />nroDocFiltro').val();
		var idEstado=jQuery('#<portlet:namespace />rc_estado_filtro').val();
    	
		jQuery('#<portlet:namespace />buscando').show();
	 	var busquedaNom = {"sucursal":idHotel,
	 			           "cliente":cliente,
	 			           "fechadesdedia":fechaDesdeDia.value,"fechadesdemes":fechaDesdeMes.value, "fechadesdeanio":fechaDesdeAnio.value,
	 			           "id":id,"cmd":"filterRecibos","fechahastadia":fechaHastaDia.value,"fechahastames":fechaHastaMes.value,
	 			           "fechahastaanio":fechaHastaAnio.value,
	 			           "cliente_doc":clienteDoc,
	 			           "estado":idEstado};
	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" /></portlet:renderURL>';
	
		jQuery('#<portlet:namespace />div_recibos').load(url,busquedaNom, function(){
															jQuery('#<portlet:namespace />buscando').hide();      
		});	
		
	}
    
    function <portlet:namespace />aprobarRecibos(){
    	var inputs=jQuery('input:checkbox');
		var aprobados="";
		
		for(i=0;i<inputs.length;i++){
			if(inputs[i].checked){
				aprobados += inputs[i].value + ";";
			}
		}
	
		if(aprobados.length>1){
			
			var idHotel=jQuery('#<portlet:namespace />rc_sucursal_filtro').val();
	    	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
			var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
			var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");
			var fechaHastaDia  = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
			var fechaHastaMes= document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
			var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");
			var id=jQuery('#<portlet:namespace />nroFiltro').val();
			var cliente=jQuery('#<portlet:namespace />clienteFiltro').val();
			var clienteDoc=jQuery('#<portlet:namespace />nroDocFiltro').val();
			var idEstado=jQuery('#<portlet:namespace />rc_estado_filtro').val();
			jQuery('#<portlet:namespace />buscando').show();
		 	var busquedaNom = {"sucursal":idHotel,
		 			           "cliente":cliente,
		 			           "fechadesdedia":fechaDesdeDia.value,"fechadesdemes":fechaDesdeMes.value, "fechadesdeanio":fechaDesdeAnio.value,
		 			           "id":id,"cmd":"aprobacion","fechahastadia":fechaHastaDia.value,"fechahastames":fechaHastaMes.value,
		 			           "fechahastaanio":fechaHastaAnio.value,
		 			           "cliente_doc":clienteDoc,
		 			           "estado":idEstado,
		 			           "aprobados":encodeURI(aprobados)};
		 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" /></portlet:renderURL>';
			jQuery('#<portlet:namespace />div_recibos').load(url,busquedaNom, function(){
																jQuery('#<portlet:namespace />buscando').hide();      
			});
		}
    	
    }

</script>

