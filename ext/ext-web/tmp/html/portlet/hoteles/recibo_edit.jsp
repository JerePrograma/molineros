<%@ include file="/html/portlet/hoteles/init.jsp"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
    boolean showAprobacion=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_APROBACION_RECIBOS);
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
%>
<style>
 	input[type=text].resaltado {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 16px;
	color: black;
	background:#C2F9F4;
	text-align: left;  
	}
	
	label.resaltado{
	  font-family: Arial, Helvetica, sans-serif;
	  font-size: 14px;
	  color: black;
	}
 	
 	</style>
<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
		<portlet:param name="struts_action" value="/hoteles/recibos_gestion" />
</portlet:renderURL> 
<p><a href="<%= volver %>">Volver</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>
	
<form action="" method="post" name="<portlet:namespace />fmS">

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
			    <td><liferay-ui:message key="fc-suc" />: </td> 
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
				
				<td>
				   <div id="<portlet:namespace />divAprobacion">
				     <table>
				       <td>Aprobado:</td> 
			           <td><input id="<portlet:namespace />fecha_aprobado" 
					       name="<portlet:namespace />fecha_aprobado" size="20"
					       maxlength="10" type="text" tabindex="1" readonly="readonly"
					       value='<%=recibo.getAprobadoFecha()!=null?(new SimpleDateFormat("dd/MM/yyyy")).format(recibo.getAprobadoFecha()):""%>' />
				       </td>
				       <td>
                         <input  id="<portlet:namespace />aprobar_recibo"   
                         name="<portlet:namespace />aprobar_recibo" type="button" value="Aprobar" onClick="<portlet:namespace />aprobarRecibo();"/>&nbsp;
                         <input id="<portlet:namespace />desaprobar_recibo" 
                         name="<portlet:namespace />desaprobar_recibo" type="button" value="Desaprobar" onClick="<portlet:namespace />desaprobarRecibo();"/>&nbsp;
	                   </td>
				      </table> 
				   </div>
				</td>
				
			  </tr>	
			 </table>
		
		<table class="lfr-table">
		    <tr><td>&nbsp;</td></tr>
            		
			<tr>
			   <td>Observación:</td> 
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
       <fieldset class="block-labels"> 
		<legend>Concepto del Ingreso</legend> 
           <fieldset class="block-labels"> 
		     <legend>Reserva</legend>
		     <input type="radio" id="<portlet:namespace />tipoReserva" name="<portlet:namespace />tipo" 
		            <%=recibo.getReserva()!=null && (recibo.getReserva().getIdReserva()!=null || recibo.getFactura()==null ||
		            		recibo.getFactura().getNumero()==null)?"checked=\"checked\"":"" %>  <%=!esEdicion ?"disabled=\"disabled\"":"" %>
		            onchange="<portlet:namespace />habilitar_controles()"/>
		     <table class="lfr-table">
		        <tr>
		           <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			       <td>Año:</td> 
			       
			       <td><select name="<portlet:namespace/>anio"
					id="<portlet:namespace/>anio" <%=!esEdicion ?"disabled=\"disabled\"":"" %> >
						<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						for (int i = hastaAnio-1; i<=hastaAnio+1; i++){  %>
						<option value="<%=i%>"
							<%if (recibo != null && recibo.getReserva() != null && recibo.getReserva().getAnio() !=null && 
							recibo.getReserva().getAnio() !=0 && i == recibo.getReserva().getAnio()) { %>
							selected="selected" <%} %>>
							<%=i %></option>
						   <%}%>
				</select></td>
				
				  <td>Número:</td> 
				
			       <td><input id="<portlet:namespace />reserva"
					    name="<portlet:namespace />reserva" size="10" 
					    maxlength="10" type="text" tabindex="1" <%=!esEdicion ?"readonly=\"readonly\"":"" %>
					    value='<%=recibo.getReserva()!=null && recibo.getReserva().getIdReserva()!=null?recibo.getReserva().getIdReserva():"" %>' 
					    onkeydown="allowOnlyDigits(event)"/>
				   </td>
 				   
				   <td align="right">
						<input type="button" value="Buscar" onclick="<portlet:namespace />traer_reserva();" > &nbsp;
				   </td>
				   
			    </tr>
			    <tr><td>&nbsp;</td></tr>
			    <tr>
			       <table class="lfr-table">
			         <tr>
			           <td>
			              <label>Cliente:</label>
			           </td>
			           <td>
			              <input id="<portlet:namespace />cliente_reserva"
					       name="<portlet:namespace />cliente_reserva" size="20"
					       maxlength="50" type="text"  readonly="readonly"
					       value="<%=recibo.getReserva()!=null && recibo.getReserva().getApellido()!=null
					           ?recibo.getReserva().getApellido()+" "+ (recibo.getReserva().getNombre()!=null?
					        		   recibo.getReserva().getNombre():""):"" %>" />
				       </td>
				       
				       <td>
			              <label>Del:</label>
			           </td>
			           <td>
			              <input id="<portlet:namespace />desde_reserva"
					       name="<portlet:namespace />desde_reserva" size="10"
					       maxlength="50" type="text"  readonly="readonly"
					       value="<%=recibo.getReserva()!=null && recibo.getReserva().getFechaDesde() !=null
					           ?(new SimpleDateFormat("dd/MM/yyyy")).format(recibo.getReserva().getFechaDesde()) :"" %>" />
				       </td>
				       
				       <td>
			              <label>al:</label>
			           </td>
			           <td>
			              <input id="<portlet:namespace />hasta_reserva"
					       name="<portlet:namespace />hasta_reserva" size="10"
					       maxlength="50" type="text"  readonly="readonly"
					       value="<%=recibo.getReserva()!=null && recibo.getReserva().getFechaHasta() !=null
					           ?(new SimpleDateFormat("dd/MM/yyyy")).format(recibo.getReserva().getFechaHasta()) :"" %>" />
				       </td>
				       
				       <td>
			              <label>Seña:</label>
			           </td>
			           <td>
			              <input id="<portlet:namespace />senia_reserva"
					       name="<portlet:namespace />seniao_reserva" size="10"
					       maxlength="50" type="text"  readonly="readonly"
					       value="<%=recibo.getReserva()!=null && recibo.getReserva().getSenia() !=null
					           ?(new DecimalFormat( "#,###,###,##0.00" )).format(recibo.getReserva().getSenia()):"" %>" />
				       </td>
				       
				       
				       <td>
			              <label>Pagado:</label>
			           </td>
			           <td>
			              <input id="<portlet:namespace />pago_reserva"
					       name="<portlet:namespace />pago_reserva" size="10"
					       maxlength="50" type="text"  readonly="readonly"
					       value="<%=recibo.getReserva()!=null && recibo.getReserva().getPagado() !=null
					           ?(new DecimalFormat( "#,###,###,##0.00" )).format(recibo.getReserva().getPagado()):"" %>" />
				       </td>
			         </tr>
			       </table>
			    </tr>
			    
	         </table>
		
	       </fieldset>	
	       <div id="<portlet:namespace />facturacion"> 
	       <fieldset class="block-labels"> 
		      <legend>Factura</legend>
		      <input type="radio" id="<portlet:namespace />tipoFactura" name="<portlet:namespace />tipo" 
		          <%=recibo.getFactura()!=null && recibo.getFactura().getNumero()!=null ?"checked=\"checked\"":"" %> <%=!esEdicion ?"disabled=\"disabled\"":"" %>
		           onchange="<portlet:namespace />habilitar_controles()"/>
		           
		      <table class="lfr-table">
		        <tr>
		          <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		          <td>
			              <label>Tipo:</label>
			      </td>
		          <td>
		            <select name="<portlet:namespace />fc_tipo" id="<portlet:namespace />fc_tipo" <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%>>
						<option value="FCP" <%if(recibo.getFactura()!=null && recibo.getFactura().getTipo() !=null && 
						recibo.getFactura().getTipo().equalsIgnoreCase("FCP")){ %> selected="selected" <%} %>>FCP</option>	
						
				    </select>
				  </td>
		        
			      <td><liferay-ui:message key="fc-letra" />:</td> 
						<td><select name="<portlet:namespace />fc_letra" id="<portlet:namespace />fc_letra"  disabled='true' <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%>  onchange="<portlet:namespace/>cambiarLetraFC();" >
								<option value="B" <%if(recibo.getFactura()!=null && recibo.getFactura().getLetra()!=null && recibo.getFactura().getLetra().equalsIgnoreCase("B")){ %> selected="selected" <%} %> >B</option>
								<option value="A" <%if(recibo.getFactura()!=null && recibo.getFactura().getLetra()!=null && recibo.getFactura().getLetra().equalsIgnoreCase("A")){ %> selected="selected" <%} %>>A</option>
							</select>  
						</td>
						<td><liferay-ui:message key="fc-suc" />: </td> 
						<td><select name="<portlet:namespace />fc_sucursal" id="<portlet:namespace />fc_sucursal" <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%>>
								<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
									<%if(ptoVtaAfip.equalsIgnoreCase("9999")){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(recibo.getFactura()!=null && recibo.getFactura().getSucursal()!=null && recibo.getFactura().getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%}else if(ptoVtaAfip.equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(recibo.getFactura()!=null && recibo.getFactura().getSucursal()!=null && recibo.getFactura().getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%} %>
								<%} %>
							</select></td>
						<td><liferay-ui:message key="fc-nro" />: </td>
						<td><input type="text" name="<portlet:namespace />fc_numero" <%=!esEdicion ?"readonly=\"readonly\"":"" %>
											id="<portlet:namespace />fc_numero" value="<%=recibo.getFactura()!=null && recibo.getFactura().getNumero()!=null?recibo.getFactura().getNumero():"" %>" 
										onkeydown="allowOnlyDigits(event)"	
										>  
						</td> 
						
						<td align="right">
						    <input type="button" value="Buscar" onclick="<portlet:namespace />traer_factura();" > &nbsp;
				        </td>
						
						
						 
						
			    </tr>
			    
			    <tr><td>&nbsp;</td></tr>
			    
			    <tr>
			     <table  class="lfr-table">
			      <tr>
			      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td> 
			      <td>Cliente:</td>
			      
			      <td>
			        <input id="<portlet:namespace />fc_cliente_nombre"
					       name="<portlet:namespace />fc_cliente_nombre" size="50"
					       maxlength="80" type="text"  readonly="readonly"
					       value="<%=recibo.getFactura()!=null && recibo.getFactura().getCliente() !=null
					           ?recibo.getFactura().getCliente().getDescripcionCliente():"" %>" />
			      </td>
			      
			       
						 <td>Fecha:</td>
						 <td><input type="text" name="<portlet:namespace />fc_fecha" 
							id="<portlet:namespace />fc_fecha" value="<%=recibo.getFactura()!=null && recibo.getFactura().getFecha()!=null?
							(new SimpleDateFormat("dd/MM/yyyy")).format(recibo.getFactura().getFecha()):"" %>" readonly="readonly">  
						  </td>
						  <td>Importe:</td>
						  
						  <td>
			              <input id="<portlet:namespace />fc_total"
					       name="<portlet:namespace />fc_total" size="10"
					       maxlength="50" type="text"  readonly="readonly"
					       value="<%=recibo.getFactura()!=null && recibo.getFactura().getImporteTotal() !=null
					           ?(new DecimalFormat( "#,###,###,##0.00" )).format(recibo.getFactura().getImporteTotal()):"" %>" />
				         </td>
			      
			     </tr>
			    </table>
			    
			    </tr>
			    
	          </table>
	       </fieldset>	
	   </fieldset>	
	</fieldset>
	</div>
	<table>
	    <tr>
			<td>
			<div id="<portlet:namespace />mostrar_ingresos">	
				<liferay-util:include page="/html/portlet/hoteles/recibo_formas_ingreso_agregar.jsp">
					<liferay-util:param name="esEdicion" value="true"/>
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


      <input id="<portlet:namespace />guardar" name="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button"  tabindex="4"
	  />
		 
	</td>
	
	<td>
     <input id="<portlet:namespace />nuevo" name="<portlet:namespace />nuevor" 
       type="button" value="Nuevo" onClick="<portlet:namespace />nuevoRecibo();"/>&nbsp;
	</td>
	
	</tr>
	</table>      
   <input type="hidden" value="" name="view" id="view" />
   
</form>
<div id="helpVolver" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Volver: Seleccionando este link, se vuelve a la pantalla anterior.
</div>

<script type="text/javascript">
<portlet:namespace />inicializar();

function <portlet:namespace />inicializar(){
	var puedeAprobar='<%=showAprobacion%>'==='true';
	var reciboProcesado='<%= recibo.getFechaProceso()!=null%>'==='true';
	
	 jQuery('#<portlet:namespace />facturacion').hide();
	
	if(puedeAprobar){
	  jQuery('#<portlet:namespace />divAprobacion').show();
	  jQuery('#<portlet:namespace />guardar').hide();
	  jQuery('#<portlet:namespace />nuevo').hide();
	  var estaAprobado='<%=recibo.getAprobadoFecha()!=null %>'==='true';
	  
	  if(estaAprobado){
		  jQuery('#<portlet:namespace />desaprobar_recibo').show();
		  jQuery('#<portlet:namespace />aprobar_recibo').hide();
	  }else{
		  jQuery('#<portlet:namespace />aprobar_recibo').show();
		  jQuery('#<portlet:namespace />desaprobar_recibo').hide();
	  }
	  
	}else{
	  jQuery('#<portlet:namespace />divAprobacion').hide();
	  if(reciboProcesado){
		 jQuery('#<portlet:namespace />guardar').hide(); 
	  }else{
	     jQuery('#<portlet:namespace />guardar').show();
	  }   
	  jQuery('#<portlet:namespace />nuevo').show();
	}  
}

function <portlet:namespace />validarCampos(){
	var result = true;
	
	var cliente=jQuery('#<portlet:namespace />cliente_recibo_nombre').val();
	var total=jQuery('#<portlet:namespace />total_recibo').val();
	
	if(cliente==""){
		alert("Debe seleccionar una Reserva o una Factura");
		return false;
	}
	
	if(total=="" || parseFloat(total)==0){
		alert("Debe completar los Ingresos del Recibo");
		return false;
	}
	return true;
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {
		jQuery('#<portlet:namespace />fc_letra').removeAttr('disabled');
		jQuery('#<portlet:namespace />anio').removeAttr('disabled');
		jQuery('#<portlet:namespace />rc_sucursal').removeAttr('disabled');
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
		
		submitForm(document.<portlet:namespace />fmS, url);
	}
	return false;		
}


function <portlet:namespace />nuevoRecibo() {
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" />'+
	'<liferay-portlet:param name="cmd" value="new"/>'+
	'</liferay-portlet:renderURL>';
	
	submitForm(document.<portlet:namespace />fmS, url);
}

//-----------
//-----------

function <portlet:namespace />traer_reserva(){
	
	var ptoVta='<%=ptoVtaAfip%>';
	var anio = jQuery('#<portlet:namespace />anio').val();
	var reserva=jQuery('#<portlet:namespace />reserva').val();
	
	if(reserva!=""){
	
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/recupera_datos_reserva';
       url +='&anio='+anio;
       url +='&reserva='+reserva;
       jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
	       
	       	  if(obj.estado=="ERROR" ){
	       		<portlet:namespace />limpiarReserva();
	       		alert("La Reserva es Inexiste"); 
	       	  }else{
	       		 jQuery("#<portlet:namespace />cliente_reserva").val(obj.cliente);
	       		 jQuery("#<portlet:namespace />desde_reserva").val(obj.fechainicio);
	       		 jQuery("#<portlet:namespace />hasta_reserva").val(obj.fechafin);
	       		 jQuery("#<portlet:namespace />pago_reserva").val(obj.pagado);
	       		 jQuery("#<portlet:namespace />senia_reserva").val(obj.senia);
	       		 jQuery("#<portlet:namespace />cliente_recibo_nombre").val(obj.cliente);
	       		 jQuery("#<portlet:namespace />cliente_recibo_documento").val(obj.documento);
	       		 jQuery("#<portlet:namespace />cliente_reserva_id").val(obj.clienteid);
	       		 jQuery("#<portlet:namespace />f_ini_reserva").val(obj.fechainicioid);
	       		 jQuery("#<portlet:namespace />f_fin_reserva").val(obj.fechafinid);
	       	  } 
		   }
	   });
	
	}
}


function <portlet:namespace />traer_factura(){
	
	var ptoVta=jQuery('#<portlet:namespace />fc_sucursal').val();;
	var tipo = jQuery('#<portlet:namespace />fc_tipo').val();
	var letra=jQuery('#<portlet:namespace />fc_letra').val();
	var numero=jQuery('#<portlet:namespace />fc_numero').val();
	


	if(ptoVta!="" && tipo !="" && letra!="" && numero !=""){

	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/hoteles/recupera_datos_factura';
	   
       url +='&letra='+letra;
       url +='&sucursal='+ptoVta;
       url +='&numero='+numero;
       url +='&tipo='+tipo;
       jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
	       
	       	  if(obj.estado=="ERROR" ){
	       		<portlet:namespace />limpiarReserva();
	       		alert("La Factura es Inexiste"); 
	       	  }else{
	       		jQuery("#<portlet:namespace />fc_cliente_nombre").val(obj.cliente);
	       		jQuery("#<portlet:namespace />fc_numero").val(obj.numero);
	       		jQuery("#<portlet:namespace />fc_fecha").val(obj.fecha);
	       		jQuery("#<portlet:namespace />fc_total").val(obj.total);
	       		jQuery("#<portlet:namespace />cliente_recibo_nombre").val(obj.clienteNombre);
	       		jQuery("#<portlet:namespace />cliente_recibo_documento").val(obj.clienteDocumento);
	       	  } 
		   }
	   });
	
	}
}



function <portlet:namespace />habilitar_controles(){
	var esReserva=jQuery('#<portlet:namespace />tipoReserva').attr('checked');
	jQuery("#<portlet:namespace />cliente_recibo_nombre").val("");
	jQuery("#<portlet:namespace />cliente_recibo_documento").val("");
	
	if(esReserva==true){
		<portlet:namespace />limpiarFactura();
		jQuery('#<portlet:namespace />fc_numero').attr('readonly',"readonly");
		jQuery('#<portlet:namespace />fc_letra').attr('disabled',"disabled");
		
		jQuery('#<portlet:namespace />facturaChk').attr('checked',false);
		jQuery('#<portlet:namespace />reserva').removeAttr('readonly');
	}else{
		<portlet:namespace />limpiarReserva();
		jQuery('#<portlet:namespace />reserva').attr('readonly',"readonly");
		jQuery('#<portlet:namespace />fc_numero').removeAttr('readonly');
		jQuery('#<portlet:namespace />fc_letra').removeAttr('disabled');
	}
}

function <portlet:namespace />limpiarReserva(){
	jQuery("#<portlet:namespace />reserva").val("");
	jQuery("#<portlet:namespace />cliente_reserva").val("");
	jQuery("#<portlet:namespace />cliente_reserva_id").val("");
	jQuery("#<portlet:namespace />desde_reserva").val("");
	jQuery("#<portlet:namespace />hasta_reserva").val("");
	jQuery("#<portlet:namespace />pago_reserva").val(""); 
	jQuery("#<portlet:namespace />senia_reserva").val("");
	jQuery("#<portlet:namespace />f_ini_reserva").val("");
	jQuery("#<portlet:namespace />f_fin_reserva").val("");
}

function <portlet:namespace />limpiarFactura(){
	jQuery("#<portlet:namespace />fc_numero").val("");
	jQuery("#<portlet:namespace />fc_fecha").val("");
	jQuery("#<portlet:namespace />fc_total").val("");
	jQuery("#<portlet:namespace />fc_cliente_nombre").val("");
}


function <portlet:namespace />aprobarRecibo(){
	jQuery('#<portlet:namespace />rc_sucursal').removeAttr('disabled');
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" />'+
	'<liferay-portlet:param name="cmd" value="aprobar_recibo_individual"/>'+
	'<liferay-portlet:param name="aprobar" value="1"/>'+
	'</liferay-portlet:renderURL>';
	submitForm(document.<portlet:namespace />fmS, url);
	return false;		
}


function <portlet:namespace />desaprobarRecibo(){
	jQuery('#<portlet:namespace />rc_sucursal').removeAttr('disabled');
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_gestion_recibos" />'+
	'<liferay-portlet:param name="cmd" value="aprobar_recibo_individual"/>'+
	'<liferay-portlet:param name="aprobar" value="0"/>'+
	'</liferay-portlet:renderURL>';
	submitForm(document.<portlet:namespace />fmS, url);
	return false;		
}


<% if (esEdicion) { %>
<portlet:namespace />habilitar_controles();
<%}%>
</script>

