<%@ include file="/html/portlet/hoteles/init.jsp"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	Recibo recibo=(Recibo)request.getSession().getAttribute(WebKeysHoteles.RECIBO_EN_EDICION);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	String ptoVtaAfip="00030";

	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
	}catch(Exception e){
		ptoVtaAfip="00030";
	}
	
	Long id_recibo=recibo!=null && recibo.getNumero()!=null ?recibo.getNumero():0;
	if(recibo==null){
		recibo= new Recibo();
		recibo.setSucursal(ptoVtaAfip);
	} 
	Calendar current = CalendarFactoryUtil.getCalendar();
%>
<style>
 	input[type=text].resaltado {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 16px;
	color: black;
	background:#A2D6C0;
	text-align: left;  
	}
	
	label.resaltado{
	  font-family: Arial, Helvetica, sans-serif;
	  font-size: 14px;
	  color: black;
	}
 	
 	</style>

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />
  
	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="errorAfiliadoNull"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
		
    <liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
		

	<fieldset class="block-labels"> 
		<legend>Recibo</legend>
		
		 <table class="lfr-table"> 
		       <tr>
			    <td><liferay-ui:message key="fc-suc" /> </td> 
				<td><select name="<portlet:namespace />rc_sucursal" id="<portlet:namespace />rc_sucursal" <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%>>
				<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
						<%if(ptoVtaAfip.equalsIgnoreCase("9999")){ %>
							<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(recibo.getSucursal()!=null && recibo.getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
						<%}else if(ptoVtaAfip.equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %>
							<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(recibo.getSucursal()!=null && recibo.getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
						<%} %>
				<%} %>
				</select></td>
			    
			    <td>Número:</td> 
			    <td><input id="<portlet:namespace />nro" class="resaltado"
					name="<portlet:namespace />nro" size="20"
					maxlength="10" type="text" tabindex="1" readonly="readonly"
					value='<%=recibo.getNumero()!=null?recibo.getNumero():""%>' />
				</td>
				
				<td>Fecha:</td> 
			    <td><input id="<portlet:namespace />fecha_recibo" 
					name="<portlet:namespace />fecha_recibo" size="20"
					maxlength="10" type="text" tabindex="1" readonly="readonly"
					value='<%=recibo.getFecha()!=null?(new SimpleDateFormat("dd/MM/yyyy")).format(recibo.getFecha()):""%>' />
				</td>
				 <td><label>Fecha Anulación:</label></td>
		         <td colspan="2">
				<liferay-ui:input-date
						dayParam="fechaAnulaDia"
						dayValue="<%=current.get(Calendar.DATE) %>" 
						dayNullable="<%= false %>"
						monthParam="fechaAnulaMes"
						monthValue="<%= current.get(Calendar.MONTH) %>"	
						monthNullable="<%= false %>"			
						yearParam="fechaAnulaAnio"
						yearValue="<%= current.get(Calendar.YEAR) %>"
						yearNullable="<%= false %>"
						yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
						firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
			</td>
			  </tr>	
			 </table>
		
		<table class="lfr-table">
		    <tr><td>&nbsp;</td></tr>
            		
			<tr>
			   <td>Descripción:</td> 
			    <td><input id="<portlet:namespace />descripcion"
					name="<portlet:namespace />descripcion" size="100"
					maxlength="100" type="text" tabindex="2" 
					value="<%=recibo.getDescripcion()==null?"":recibo.getDescripcion() %>" />
				</td>
			</tr>
			
			<tr><td>&nbsp;</td></tr>
			
			<tr>
			  <td><label class="cliente">Cliente:</label></td> 
			    <td><input id="<portlet:namespace />cliente_recibo_nombre" class="resaltado"
					name="<portlet:namespace />cliente_recibo_nombre" size="50"
					maxlength="50" type="text"  readonly=\"readonly\"
					value='<%=recibo.getCliente()!=null && recibo.getCliente().getClienteNombre()!=null?recibo.getCliente().getClienteNombre():""%>' />
				</td>
			  <td><label class="cliente">Documento/CUIT:</label></td> 
			  <td><input id="<portlet:namespace />cliente_recibo_documento" class="resaltado"
					name="<portlet:namespace />cliente_recibo_documento" size="15"
					maxlength="15" type="text"  readonly=\"readonly\"
					value='<%=recibo.getCliente()!=null && recibo.getCliente().getClienteDocumento()!=null?recibo.getCliente().getClienteDocumento():""%>' />
			  </td> 
			</tr>
						                
	    </table>
 	</fieldset>
	
	<table>
	    <tr>
			<td>
			<div id="<portlet:namespace />mostrar_ingresos">	
				<liferay-util:include page="/html/portlet/hoteles/recibo_formas_ingreso_agregar.jsp">
					<liferay-util:param name="esEdicion" value="false"/>
					<liferay-util:param name="ptoVtaAfip" value="<%=String.valueOf(ptoVtaAfip) %>"/>
				</liferay-util:include>			
			</div>
			</td>
			
		</tr>
	</table>
	
	
	
	<br>
	<input type="hidden" name="<portlet:namespace />id_recibo"
		id="<portlet:namespace />id_recibo" value="<%=id_recibo%>" />
		
	<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 
	
	<input type="hidden" name="<portlet:namespace />f_ini_reserva"
		id="<portlet:namespace />f_ini_reserva" value="<%=recibo.getReserva()!=null?recibo.getReserva().getFechaDesdeId():"" %>" />
		
	<input type="hidden" name="<portlet:namespace />f_fin_reserva"
		id="<portlet:namespace />f_fin_reserva" value="<%=recibo.getReserva()!=null?recibo.getReserva().getFechaHastaId():"" %>" />	
		
    <input type="hidden" name="<portlet:namespace />cliente_reserva_id"
		id="<portlet:namespace />cliente_reserva_id" value="<%=recibo.getReserva()!=null?recibo.getReserva().getIdCliente():"" %>" />		

    <table>
     <tr>
	  <td>

     <%if(recibo.getFechaBaja()==null){ %>

      <input id="<portlet:namespace />anular"
		value="<liferay-ui:message key="anular"/>"
		title="<liferay-ui:message key="anular" />"
		onClick="javascript: <portlet:namespace />salvarAnulacion();"
		type="button"  tabindex="4"
	  />
	  
	  <%}%>
	  
	  <span align="center" id="<portlet:namespace />guardando_anulacion">
				<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
	  </span>	
	
		 
	</td>
	</tr>
	</table>      
   <input type="hidden" value="" name="view" id="view" />
   

<script type="text/javascript">
jQuery('#<portlet:namespace />guardando_anulacion').hide();

function <portlet:namespace />salvarAnulacion(){
	
	var confirmar = false;
	confirmar=confirm ('Esta seguro de anular el Recibo?');
	if(confirmar){	
	   jQuery('#<portlet:namespace />guardando_anulacion').show();
	   var fechaAnulaDia  = document.getElementById("<portlet:namespace />fechaAnulaDia");
	   var fechaAnulaMes= document.getElementById("<portlet:namespace />fechaAnulaMes");
	   var fechaAnulaAnio = document.getElementById("<portlet:namespace />fechaAnulaAnio");
       var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/hoteles_gestion_recibos'
			+'&fechaAnulaDia='+fechaAnulaDia.value
			+'&fechaAnulaMes='+fechaAnulaMes.value
			+'&fechaAnulaAnio='+fechaAnulaAnio.value;
	   url += '&id_recibo=' + document.getElementById("<portlet:namespace />nro").value;
	   url += '&id_hotel=' + document.getElementById("<portlet:namespace />rc_sucursal").value;
	   url += '&cmd=anular_recibo';
	   url += '&rnd=' + Math.floor(Math.random()*100);
	
	   jQuery(popupMD).load(url);
	}	
}



</script>

