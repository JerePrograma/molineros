<%@ include file="/html/portlet/hoteles/init.jsp"%>
<%@page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>
<%@page import="com.liferay.portlet.documentlibrary.model.DLFolder"%>


<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
    NumberFormat nf = new DecimalFormat("#0.00");
    DLFolder f = DLFolderLocalServiceUtil.getFolder(
        10136, 0L, "Prestamos");
    long folderId = f.getFolderId();

	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	Prestamo prestamo=(Prestamo)request.getSession().getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
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
//		ptoVtaAfip="00030";
		ptoVtaAfip="9999";
	}
	
	Long id_prestamo=prestamo!=null && prestamo.getId()!=null?prestamo.getId():0;
	if(prestamo==null){
		prestamo= new Prestamo();
	} 
	
	Calendar estadiaDesde = CalendarFactoryUtil.getCalendar();
		if(prestamo.getEstadiaDesde()!=null){
		estadiaDesde.setTime(prestamo.getEstadiaDesde());
	}else{
		estadiaDesde.setTime(new Date());
	}
		
	Calendar estadiaHasta= CalendarFactoryUtil.getCalendar();
	if(prestamo.getEstadiaHasta()!=null){
		estadiaHasta.setTime(prestamo.getEstadiaHasta());
	}else{
		estadiaHasta.setTime(new Date());
	}	
	
	Calendar acuerdo= CalendarFactoryUtil.getCalendar();
	if(prestamo.getAcuerdoFecha()!=null){
		acuerdo.setTime(prestamo.getAcuerdoFecha());
	}else{
		acuerdo.setTime(new Date());
	}
	
	Calendar cuota= CalendarFactoryUtil.getCalendar();
	if(prestamo.getPrimeraCuota()!=null){
		cuota.setTime(prestamo.getPrimeraCuota());
	}else{
		cuota.setTime(new Date());
	}
	
	Calendar actual= CalendarFactoryUtil.getCalendar();
	actual.setTime(new Date());
	
	boolean showPrestamosSeccional=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_PRESTAMOS_TURISMO_SECCIONAL);
%>

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
	<liferay-ui:error key="errorUploadFile" message="<%=(String) request.getAttribute(\"msgInsertError\")%>" />
	<liferay-ui:error key="errorPrestamo"
		message="<%=(String)request.getAttribute(\"msgError\") %>" />
		
    <liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
		

	<fieldset class="block-labels"> 
	   <legend>Préstamo</legend>
	   
	   <table  class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	     <th>
	       
	           Número
	     </th>
	     <th>
	        <input type="text" name="<portlet:namespace />ac_nro" 	id="<portlet:namespace />ac_nro" value="<%=id_prestamo !=0L?id_prestamo:"" %>" 
						readonly="readonly"	>  
	     </th>
    
       </table>						                
	   <fieldset class="block-labels">
									<legend>
										<liferay-ui:message key="datos-afiliado" />
									</legend>
        <liferay-util:include page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
					<liferay-util:param name="edit_mode" value="<%=String.valueOf(esEdicion) %>" />
					<liferay-util:param name="discapacidad" value="<%= null %>" />
					<liferay-util:param name="pag_reintegro" value="<%= String.valueOf(true) %>" />
					<liferay-util:param name="from_reclamo" value="false" />
					<liferay-util:param name="cuil" value='<%= prestamo!=null && prestamo.getAfiliado() != null ? prestamo.getAfiliado().getCuil_titular() :"" %>' />
					<liferay-util:param name="inte" value='<%= prestamo!=null && prestamo.getAfiliado() != null ? String.valueOf(prestamo.getAfiliado().getInte()) :	new String("") %>'/>
					<liferay-util:param name="origen" value="" />
		</liferay-util:include>
		</fieldset>
	    <br>	
	    
	    <fieldset class="block-labels">
		  <legend>Datos de la Estadía	</legend>
		  <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		  <tr>
		  <td>Hotel:</td> 
		  <td><select name="<portlet:namespace />cod_Hotel" id="<portlet:namespace />cod_Hotel" <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%>>
								<!-- 
								<%if(prestamo.getHotel() !=null && prestamo.getHotel().equalsIgnoreCase("00010")){ %> selected="selected" <%} %>>EVA PERON</option>
								<%if(prestamo.getHotel()!=null && prestamo.getHotel().equalsIgnoreCase("00020")){ %> selected="selected" <%} %>>LOS DIQUES</option>
								<%if(prestamo.getHotel()!=null && prestamo.getHotel().equalsIgnoreCase("00030")){ %> selected="selected" <%} %>>MAR DEL PLATA</option>
								 -->
								<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
									<%if(ptoVtaAfip.equalsIgnoreCase("9999")){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(prestamo.getHotel()!=null && prestamo.getHotel().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%}else if(ptoVtaAfip.equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(prestamo.getHotel()!=null && prestamo.getHotel().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%} %>
								<%} %>
			</select></td>
			
			<td>
				<label><liferay-ui:message key="fecha-desde" />:</label>
						<liferay-ui:input-date
						dayParam="fechaEstadiaDdeDia"
						dayValue="<%= estadiaDesde.get(Calendar.DATE)%>"
						monthParam="fechaEstadiaDdeMes"
						monthValue="<%= estadiaDesde.get(Calendar.MONTH) %>"
						yearParam="fechaEstadiaDdeAnio"
						yearValue="<%= estadiaDesde.get(Calendar.YEAR) %>"
						yearRangeStart="<%= estadiaDesde.get(Calendar.YEAR)-1 %>"
						yearRangeEnd="<%= estadiaDesde.get(Calendar.YEAR) +1 %>"
						firstDayOfWeek="<%= estadiaDesde.getFirstDayOfWeek()%>"
						
				/> 
			</td>
			
			<td>
				<label><liferay-ui:message key="fecha-hasta" />:</label>
						<liferay-ui:input-date
						dayParam="fechaEstadiaHtaDia"
						dayValue="<%= estadiaHasta.get(Calendar.DATE)%>"
						monthParam="fechaEstadiaHtaMes"
						monthValue="<%= estadiaHasta.get(Calendar.MONTH) %>"
						yearParam="fechaEstadiaHtaAnio"
						yearValue="<%= estadiaHasta.get(Calendar.YEAR) %>"
						yearRangeStart="<%= estadiaHasta.get(Calendar.YEAR)-1 %>"
						yearRangeEnd="<%= estadiaHasta.get(Calendar.YEAR) +1 %>"
						firstDayOfWeek="<%= estadiaHasta.getFirstDayOfWeek()%>"
						
				/> 
			</td>	
		  </tr>	
		  
		   <tr>
		 	
			<td>
			<label><liferay-ui:message key="observaciones" />:</label>
			</td>
			<td colspan="7"><textarea cols="100"
					name="<portlet:namespace/>observaciones" 
					id="<portlet:namespace/>observaciones"><%= prestamo!=null && prestamo.getObservaciones() != null  ?
							prestamo.getObservaciones() : new String("") %></textarea>
			</td>
		</tr>
		  				
		  </table>
		 </fieldset>  
		 
		 <div id="convenioDiv">
		    <fieldset class="block-labels">
		      <legend>Datos del Convenio	</legend>
		       <fieldset class="block-labels">
		            <legend>Factura	</legend>
		      
		       <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		         <tr>
		          <td><liferay-ui:message key="fc-tipo" /> </td> 
						<td><select name="<portlet:namespace />fc_tipo" id="<portlet:namespace />fc_tipo"   onchange="<portlet:namespace/>cambiar_fc_tipo(); "  <% if (!esEdicion) { %>
							<%="disabled='disabled'" %> <%}%> >
								<option value="<%=WebKeysGlobal.COMPROBANTE_FACTURA%>" <%if(prestamo.getFactura().getTipo()!=null && prestamo.getFactura().getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)){ %> selected="selected" <%} %> ><%=WebKeysGlobal.COMPROBANTE_FACTURA %></option>
								<option value="<%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO%>" <%if(prestamo.getFactura().getTipo()!=null && prestamo.getFactura().getTipo().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_NOTA_CREDITO)){ %> selected="selected" <%} %>><%=WebKeysGlobal.COMPROBANTE_NOTA_CREDITO %></option>
							</select>  
						</td>
						<td><liferay-ui:message key="fc-letra" /> </td> 
						<td><select name="<portlet:namespace />fc_letra" id="<portlet:namespace />fc_letra" >
								<option value="B" <%if(prestamo.getFactura().getLetra()!=null && prestamo.getFactura().getLetra().equalsIgnoreCase("B")){ %> selected="selected" <%} %> >B</option>
								<option value="A" <%if(prestamo.getFactura().getLetra()!=null && prestamo.getFactura().getLetra().equalsIgnoreCase("A")){ %> selected="selected" <%} %>>A</option>
							</select>  
						</td>
						<td><liferay-ui:message key="fc-suc" /> </td> 
						<td><select name="<portlet:namespace />fc_sucursal" id="<portlet:namespace />fc_sucursal" >
								<%for(int i=0; i<WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES.length;i++){ %>
									<%if(ptoVtaAfip.equalsIgnoreCase("9999")){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" <%if(prestamo.getFactura().getSucursal()!=null && 
										prestamo.getFactura().getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%}else if(ptoVtaAfip.equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %>
										<option value="<%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0] %>" 
										  <%if(prestamo.getFactura().getSucursal()!=null && prestamo.getFactura().getSucursal().equalsIgnoreCase(WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][0])){ %> selected="selected" <%} %>><%=WebKeysUOMA.PUNTOS_DE_VENTA_HOTELES[i][1]%></option>
									<%} %>
								<%} %>
							</select></td>
						<td><liferay-ui:message key="fc-nro" /> </td>
						<td><input type="text" name="<portlet:namespace />fc_numero" 
											id="<portlet:namespace />fc_numero" value="<%=prestamo.getFactura().getNumero()!=null?prestamo.getFactura().getNumero():"" %>" 
										onkeydown="allowOnlyDigits(event)"	>  
						</td>
						<td>Importe </td>
						<td><input type="text" name="<portlet:namespace />fc_importe" 
											id="<portlet:namespace />fc_importe" value="<%=prestamo.getFactura().getTotalExento()!=null?nf.format(prestamo.getFactura().getTotalExento()):"" %>" 
										onkeydown="allowOnlyDigits(event)"	
										onchange="actualizarMonto()">  
						</td>
				      </tr>
				      </table>
				      
		      </fieldset>
		      

              <fieldset class="block-labels">
		            <legend>Otros Importes	</legend>
		      
		       <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		         <tr>
		          	<td>Movilidad </td>
						<td><input type="text" name="<portlet:namespace />ac_movilidad" 
							id="<portlet:namespace />ac_movilidad" value="<%=prestamo.getMovilidad()!=null?nf.format(prestamo.getMovilidad()):"" %>" 
							onkeydown="allowOnlyDigits(event)"
							onchange="actualizarMonto()">  
					</td>
				 </tr>
				</table>
				      
		      </fieldset>		      
		      
		      
		      
		      <fieldset class="block-labels">
		            <legend>Acuerdo	</legend>
		         <div id="<portlet:namespace />acuerdoDiv">
		         <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		         <tr>
		            <td>
				       <label>Fecha</label>
						<liferay-ui:input-date
						dayParam="fechaAcuerdoDia"
						dayValue="<%= prestamo.getAcuerdoFecha()!=null?acuerdo.get(Calendar.DATE):-1%>"
						dayNullable="<%= true %>"
						monthParam="fechaAcuerdoMes"
						monthValue="<%= prestamo.getAcuerdoFecha()!=null?acuerdo.get(Calendar.MONTH):-1 %>"
						monthNullable="<%= true %>"	
						yearParam="fechaAcuerdoAnio"
						yearValue="<%=prestamo.getAcuerdoFecha()!=null? acuerdo.get(Calendar.YEAR):-1 %>"
						yearNullable="<%= true %>"
						yearRangeStart="<%= acuerdo.get(Calendar.YEAR)-1 %>"
						yearRangeEnd="<%= acuerdo.get(Calendar.YEAR) +1 %>"
						firstDayOfWeek="<%= acuerdo.getFirstDayOfWeek()%>"
						disabled = ""
				       /> 
			        </td>	
		            <td>Monto </td>
						<td><input type="text" name="<portlet:namespace />ac_importe" 
											id="<portlet:namespace />ac_importe" value="<%=prestamo.getMonto() !=null?nf.format(prestamo.getMonto()):"" %>" 
										onkeydown="allowOnlyDigits(event)" onchange="<portlet:namespace />recalcular()"	>  
					</td>
					<td>Interés % </td>
						<td><input type="text" name="<portlet:namespace />ac_interes_porcentaje" 
											id="<portlet:namespace />ac_interes_porcentaje" value="<%=prestamo.getInteresPorcentaje() !=null?
													nf.format(prestamo.getInteresPorcentaje()):0 %>" 
										onkeydown="allowOnlyDigits(event)"	onchange="<portlet:namespace />recalcular()">  
					</td>
					<td>Interés $ </td>
						<td><input type="text"
								name="<portlet:namespace />ac_interes_importe"
								id="<portlet:namespace />ac_interes_importe"		value="<%=prestamo.getInteresImporte() != null ? nf.format(prestamo.getInteresImporte()) : "" %>"
								onkeydown="allowOnlyDigits(event)" onchange="<portlet:namespace />recalcular()"></td>
								
					<td>Total </td>
						<td><input type="text"
								name="<portlet:namespace />ac_total"
								id="<portlet:namespace />ac_total"
								value="<%=prestamo.getTotal() != null ? nf.format(prestamo.getTotal()) : ""%>"
								onkeydown="allowOnlyDigits(event)"></td>			
		         </tr>
		        </table> 
		        <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		         <tr>
		           <td>Cuotas </td>
						<td><input type="text" name="<portlet:namespace />ac_cuotas" 
											id="<portlet:namespace />ac_cuotas" value="<%=prestamo.getCantidadCuotas() !=null?
													prestamo.getCantidadCuotas():"" %>" 
										onkeydown="allowOnlyDigits(event)"	>  
					</td>
					
					<td>
				       <label>Primera Cuota</label>
						<liferay-ui:input-date
						dayParam="fechaAcuerdoCuotaDia"
						dayValue='<%=prestamo.getPrimeraCuota()!=null? cuota.get(Calendar.DATE):-1%>'
						dayNullable="<%= true %>"
						monthParam="fechaAcuerdoCuotaMes"
						monthValue='<%=prestamo.getPrimeraCuota()!=null? cuota.get(Calendar.MONTH):-1 %>'
						monthNullable="<%= true %>"	
						yearParam="fechaAcuerdoCuotaAnio"
						yearValue='<%= prestamo.getPrimeraCuota()!=null?cuota.get(Calendar.YEAR):-1 %>'
						yearNullable="<%= true %>"
						yearRangeStart="<%= cuota.get(Calendar.YEAR)-1%>"
						yearRangeEnd="<%=cuota.get(Calendar.YEAR) +1 %>"
						firstDayOfWeek="<%=cuota.getFirstDayOfWeek()%>"
						disabled = ""
				       /> 
			        </td>
					<td>
					  <%if(!showPrestamosSeccional){ %>	
	                      <input id="<portlet:namespace />generarCuota"
	                      name="<portlet:namespace />generarCuota"
					      value="Generar Cuotas"
					      title="Generar cuotas"
					      onClick="javascript: <portlet:namespace />generarCuotas();"
					      type="button" />
 	                    </td>
 	                   <%} %> 
 	                 <td>
 	                 <%if(!showPrestamosSeccional){ %>	
	                      <input id="<portlet:namespace />habilitarGenerarCuota"
	                      name="<portlet:namespace />habilitarGenerarCuota"
					      value="Habilitar Generar Cuotas"
					      title="Habilitar Generar cuotas"
					      onClick="javascript: habilitaCargaAcuerdo(true);"
					      type="button" />
					  <%} %>    
 	                  </td>   
		         </tr>
		         
		         <tr>
		           <td>Pagado </td>
				   <td><input type="text" name="<portlet:namespace />ac_pagado" 
											id="<portlet:namespace />ac_pagado" value="<%=prestamo.getPagado() !=null?nf.format(prestamo.getPagado()):"" %>" 
										onkeydown="allowOnlyDigits(event)" readonly="readonly"	>  
					</td>
					
					<td>
				          <input type="button" onclick="javascript:verPagosPrestamo();" value="Ver Pagos"/> 
				    </td>
		         </tr>
		         
		         </table>
		      </div>    
		      </fieldset> 
		       
		    </fieldset>
		    <fieldset class="block-labels">
		            <legend>Cuota	</legend>
		            <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		         <tr>
		           <td>Número </td>
						<td><input type="text" name="<portlet:namespace />cuota_nro" 
											id="<portlet:namespace />cuota_nro" value="" 
										onkeydown="allowOnlyDigits(event)"	>  
					</td>
					
					<td>
				       <label>Vencimiento</label>
						<liferay-ui:input-date
						dayParam="fechaCuotaDia"
						dayValue='-1'
						dayNullable="<%= true %>"
						monthParam="fechaCuotaMes"
						monthValue='-1'
						monthNullable="<%= true %>"	
						yearParam="fechaCuotaAnio"
						yearValue='-1'
						yearNullable="<%= true %>"
						yearRangeStart="<%= cuota.get(Calendar.YEAR)-1%>"
						yearRangeEnd="<%=cuota.get(Calendar.YEAR) +1 %>"
						firstDayOfWeek="<%=cuota.getFirstDayOfWeek()%>"
						disabled = ""
				       /> 
			        </td>
			        <td>Importe </td>
						<td><input type="text"
								name="<portlet:namespace />cuota_importe"
								id="<portlet:namespace />cuota_importe"
								value=""
								onkeydown="allowOnlyDigits(event)"></td>
					<td>
					  <%if(!showPrestamosSeccional){ %>	
	                      <input id="<portlet:namespace />agregarCuota"
					      value="Agregar"
					      title="Agregar"
					      onClick="javascript: <portlet:namespace />agregarCuotas();"
					      type="button" />
					   <%}%>   
 	                    </td>
		         </tr>
		         </table>
		      </fieldset>
		  </div>
	</fieldset>
	
	<div id="<portlet:namespace />div_cuotas">
	                
			<jsp:include page='/html/portlet/hoteles/prestamosturismo/prestamos_cuotas_result.jsp' />  	
	</div>
	
	<br>
	<input type="hidden" name="<portlet:namespace />id_prestamo"
		id="<portlet:namespace />id_prestamo" value="<%=id_prestamo%>" />
		
	<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" /> 

    <table>
	 <tr>
	  <td>
	  
	  <%if(!showPrestamosSeccional){ %>	
      <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button"  tabindex="4"
	    />
	  <%}%>  
	</td>
	<td>&nbsp;&nbsp;</td>
	<td>
	  <%if(!showPrestamosSeccional){ %>	
	  <input id="<portlet:namespace />eliminar"
		value="<liferay-ui:message key="anular"/>"
		title="<liferay-ui:message key="anular" />"
		onClick="javascript: <portlet:namespace />anularPrestamo();"
		type="button"  tabindex="4"
	  />
	  <%}%>
	</td>
	
	
	<td>&nbsp;&nbsp;</td>
	<td>
	  <%if(!showPrestamosSeccional){ %>	
	    <input type="button" value="Nuevo Préstamo" id="<portlet:namespace />nuevo"
				onClick="<portlet:namespace />nuevoPrestamo();" />&nbsp;
	  <%}%>
	</td>
	
	
	</tr>
	</table>     
	
	 
   <input type="hidden" value="" name="view" id="view" />
   
</form>

<form action="" method="post" name="<portlet:namespace />fmImgS" enctype="multipart/form-data">
 <div id="convenioIMG">
 <label style="color:blue;font-size:12px;font-weight: bold;">Si ha agregado Datos nuevos  a la pantalla. Presione GUARDAR antes de subir las imágenes.</label>
 
 <fieldset>
   <table  class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >
				      <tr>
				        <td>Imágen Factura:</td>  
				        <td>
				             <input type="text" name="<portlet:namespace />fc_nombre" 
										id="<portlet:namespace />fc_nombre" value="<%=prestamo.getImgFactura() !=null?prestamo.getImgFactura():"" %>" 
						 				disabled='disabled'	 sytle="color:red">
						 				  
						</td>		
						<td>
						  <%if(!showPrestamosSeccional){ %>	
						   <input type="file" name="fc_imagen" id="fc_imagen" />
						   <%}%>
						 </td>
						<td>&nbsp;</td>
	                    
		                <td>
		                <%if(!showPrestamosSeccional){ %>	
	                      <input id="<portlet:namespace />uploadIMGFactura"
					      value="<liferay-ui:message key="upload-file"/>"
					      title="<liferay-ui:message key="upload-file" />"
					      onClick="javascript: <portlet:namespace />uploadImagen('FCP');"
					      type="button" />
					     <%}%> 
 	                    </td>
 	                    
 	                    <td>
	                      <input id="<portlet:namespace />verIMGFactura"
					      value="Ver Imágen"
					      title="Ver Imágen"
					      onClick="javascript: <portlet:namespace />verImagen('<%=folderId%>','<%=prestamo.getImgFactura() !=null?prestamo.getImgFactura():"" %>');"
					      type="button" />
 	                    </td>
 	                    <td>
 	                      <%if(!showPrestamosSeccional){ %>	
	                      <input id="<portlet:namespace />delIMGFactura"
					      value="Eliminar Imágen"
					      title="Eliminar Imágen"
					      onClick="javascript: <portlet:namespace />deleteImagen('<%=folderId%>','<%=prestamo.getImgFactura() !=null?prestamo.getImgFactura():"" %>','FCP');"
					      type="button" />
					      <%}%>
 	                    </td>
		              </tr>
		              <tr><td>&nbsp;</td></tr>
		              <tr>
				        <td>Imágen Convenio:</td>  
				        <td>
				           <p style="color:red;">
				             <input type="text" name="<portlet:namespace />cv_nombre" 
											id="<portlet:namespace />cv_nombre" value="<%=prestamo.getImgConvenio() !=null?prestamo.getImgConvenio():"" %>" 
						 			disabled='disabled'
						 			>
						   </p>				  
						</td>		
						<td>
						  <%if(!showPrestamosSeccional){ %>	
						   <input type="file" name="cv_imagen" id="cv_imagen" />
						  <%}%> 
						 </td>
						<td>&nbsp;</td>
	                    
		                <td>
		                 <%if(!showPrestamosSeccional){ %>	
	                      <input id="<portlet:namespace />uploadIMGConvenio"
					      value="<liferay-ui:message key="upload-file"/>"
					      title="<liferay-ui:message key="upload-file" />"
					      onClick="javascript: <portlet:namespace />uploadImagen('CNV');"
					      type="button" />
					     <%} %> 
 	                    </td>
 	                    
 	                    <td>
	                      <input id="<portlet:namespace />verIMGConvenio"
					      value="Ver Imágen"
					      title="Ver Imágen"
					      onClick="javascript: <portlet:namespace />verImagen('<%=folderId%>','<%=prestamo.getImgConvenio() !=null?prestamo.getImgConvenio():"" %>');"
					      type="button" />
 	                    </td>
 	                    <td>
 	                    <%if(!showPrestamosSeccional){ %>	
	                      <input id="<portlet:namespace />delIMGConvenio"
					      value="Eliminar Imágen"
					      title="Eliminar Imágen"
					      onClick="javascript: <portlet:namespace />deleteImagen('<%=folderId%>','<%=prestamo.getImgConvenio() !=null?prestamo.getImgConvenio():"" %>','CNV');"
					      type="button" />
					     <%} %> 
 	                    </td>
		              </tr>
	</table>
</fieldset>	
</div>	
</form>

<script type="text/javascript">

<portlet:namespace />initDateFields();

function <portlet:namespace />initDateFields(){
  var showConvenio="<%=id_prestamo!=0 %>";
  var bloqueaGeneracionCuotas ="<%=!prestamo.getCuotas().isEmpty()%>";

  jQuery('#convenioDiv').hide();
  jQuery('#convenioIMG').hide();
  jQuery('#<portlet:namespace />div_prestamos').hide();
  jQuery('#<portlet:namespace />div_cuotas').hide();
  jQuery('#<portlet:namespace />eliminar').hide();
  jQuery('#<portlet:namespace />nuevo').hide();
  
  if(showConvenio=='true'){
     jQuery('#convenioDiv').show();
     jQuery('#convenioIMG').show();
     jQuery('#<portlet:namespace />div_cuotas').show();
     jQuery('#<portlet:namespace />eliminar').show();
     jQuery('#<portlet:namespace />nuevo').show();
     jQuery('#<portlet:namespace />cv_nombre').css("color","red");
     if(jQuery('#<portlet:namespace />cv_nombre').val()!=""){
        jQuery('#<portlet:namespace />cv_nombre').css("background","MediumSeaGreen");
     }
     jQuery('#<portlet:namespace />fc_nombre').css("color","red");
     if(jQuery('#<portlet:namespace />fc_nombre').val()!=""){
         jQuery('#<portlet:namespace />fc_nombre').css("background","MediumSeaGreen");
     }
     
  }
  var habilita=true;
  if(bloqueaGeneracionCuotas=='true'){
	 habilita=false;  
  }
  habilitaCargaAcuerdo(habilita);
}


function habilitaCargaAcuerdo(habilita){

	if(habilita){
		jQuery("#<portlet:namespace />ac_importe").attr("disabled",false);
		jQuery('#<portlet:namespace />ac_interes_porcentaje').attr("disabled",false);
		jQuery("#<portlet:namespace />ac_interes_importe").attr("disabled",false);
		
		jQuery("#<portlet:namespace />ac_total").attr("disabled",false);
		jQuery("#<portlet:namespace />ac_cuotas").attr("disabled",false);
		
		document.getElementById("<portlet:namespace />fechaAcuerdoCuotaDia").disabled=false;
		document.getElementById("<portlet:namespace />fechaAcuerdoCuotaMes").disabled=false;
		document.getElementById("<portlet:namespace />fechaAcuerdoCuotaAnio").disabled=false;
		
		
		document.getElementById("<portlet:namespace />fechaAcuerdoDia").disabled=false;
		document.getElementById("<portlet:namespace />fechaAcuerdoMes").disabled=false;
		document.getElementById("<portlet:namespace />fechaAcuerdoAnio").disabled=false;
		
		
		jQuery("#<portlet:namespace />generarCuota").show();
		jQuery("#<portlet:namespace />habilitarGenerarCuota").hide();
		
			
	}else{
		
		jQuery("#<portlet:namespace />generarCuota").hide();
		jQuery("#<portlet:namespace />ac_importe").attr("disabled",true);
		jQuery('#<portlet:namespace />ac_interes_porcentaje').attr("disabled",true);
		jQuery("#<portlet:namespace />ac_interes_importe").attr("disabled",true);
		
		jQuery("#<portlet:namespace />ac_total").attr("disabled",true);
		jQuery("#<portlet:namespace />ac_cuotas").attr("disabled",true);
		
		document.getElementById("<portlet:namespace />fechaAcuerdoCuotaDia").disabled=true;
		document.getElementById("<portlet:namespace />fechaAcuerdoCuotaMes").disabled=true;
		document.getElementById("<portlet:namespace />fechaAcuerdoCuotaAnio").disabled=true;
		
		document.getElementById("<portlet:namespace />fechaAcuerdoDia").disabled=true;
		document.getElementById("<portlet:namespace />fechaAcuerdoMes").disabled=true;
		document.getElementById("<portlet:namespace />fechaAcuerdoAnio").disabled=true;
		
		jQuery("#<portlet:namespace />habilitarGenerarCuota").show();
	}
}

function <portlet:namespace />validarCampos(){
	var result = true;
	var cuil=jQuery("#<portlet:namespace />cuil").val();
	var inte=jQuery("#<portlet:namespace />inte").val();
	if(cuil==null || ""==cuil){
		alert("Debe seleccionar un afiliado");
		return false;
	}
	
	if(inte==null || "0"!=inte){
		alert("Debe seleccionar un afiliando titular");
		return false;
	}
	return true;
}

function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {
		document.getElementById("<portlet:namespace />cuil").disabled=false;
		document.getElementById("<portlet:namespace />inte").disabled=false;
		document.getElementById("<portlet:namespace />fc_tipo").disabled=false;
		document.getElementById("<portlet:namespace />cod_Hotel").disabled=false;
		habilitaCargaAcuerdo(true);
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
		document.<portlet:namespace />fmS.method = 'post';
		submitForm(document.<portlet:namespace />fmS, url);
	}
	return false;		
}

function <portlet:namespace />uploadImagen(tipo){
	document.getElementById("<portlet:namespace />fc_tipo").disabled=false;
	var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" />'+
	'<liferay-portlet:param name="cmd" value="addImagen"/>'+
	'<liferay-portlet:param name="tipo" value="__tipo"/>'+
	'</liferay-portlet:actionURL>';	
	 url = url.replace("__tipo",tipo);
	 
    submitForm(document.<portlet:namespace />fmImgS, url);
}


function <portlet:namespace />verImagen(folderId,fileName){
	
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/hoteles/documentacion_adjunta_recuperar"/>'+
	   '<liferay-portlet:param name="name" value="__Name"/>'+
	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	   '</liferay-portlet:actionURL>';      
	   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
	   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
}

function <portlet:namespace />deleteImagen(folderId,fileName,tipo) {
	var confirmar=false;
	confirmar = confirm ('Está seguro de eliminar este documento');
	if(confirmar){
		
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" />'+
		'<liferay-portlet:param name="cmd" value="deleteImagen"/>'+
		'<liferay-portlet:param name="name" value="__Name"/>'+
		'<liferay-portlet:param name="folderId" value="__FolderId"/>'+
		'<liferay-portlet:param name="tipo" value="__tipo"/>'+
		'</liferay-portlet:actionURL>';	
		
		
		url = url.replace("__Name",fileName).replace("__FolderId",folderId).replace("__tipo",tipo);
		
		submitForm(document.<portlet:namespace />fmImgS, url);
	}else{
		return false;
	}	
}

function <portlet:namespace />recalcular(){
	var acuerdo=jQuery("#<portlet:namespace />ac_importe").val();
	var porcentaje=jQuery('#<portlet:namespace />ac_interes_porcentaje').val();
	var interes= parseFloat(acuerdo) * parseFloat(porcentaje)/100;
    var total= parseFloat(acuerdo)+parseFloat(interes);	
	jQuery("#<portlet:namespace />ac_total").val(Math.round((total) * 100)/100);
	jQuery("#<portlet:namespace />ac_interes_importe").val(Math.round((interes) * 100)/100);
}

function <portlet:namespace />generarCuotas(){

	var total=jQuery("#<portlet:namespace />ac_total").val();
	var cantidad=jQuery("#<portlet:namespace />ac_cuotas").val();
	
	var dia  = document.getElementById("<portlet:namespace />fechaAcuerdoCuotaDia");
	var mes= document.getElementById("<portlet:namespace />fechaAcuerdoCuotaMes");
	var anio = document.getElementById("<portlet:namespace />fechaAcuerdoCuotaAnio");
	
	
	if(total==null || total<=0 || total==''){
		alert("Debe ingresar los importes");
		return false;
	}
	
	if(cantidad==null || cantidad<=0 || cantidad==''){
		alert("Debe ingresar la cantidad de Cuotas");
		return false;
	}

	if(dia==null || dia=='' || dia<=0){
		alert("Debe ingresar la Fecha de la Primera Cuota");
		return false;
	}

	if(mes==null || mes=='' || mes<0){
		alert("Debe ingresar la Fecha de la Primera Cuota");
		return false;
	}
	
	if(anio==null || anio=='' || anio<=0){
		alert("Debe ingresar la Fecha de la Primera Cuota");
		return false;
	}

	
//	jQuery('#<portlet:namespace />buscando').show();

 	var busquedaNom = {"total":total,"cantidad":cantidad,"fechadia":dia.value,"fechames":mes.value,
 			           "fechaanio":anio.value,"cmd":"generarCuotas"};
 	
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" /></portlet:renderURL>';
	jQuery('#<portlet:namespace />div_cuotas').load(url,busquedaNom, function(){
														//jQuery('#<portlet:namespace />buscando').hide();      
	});	
}


function editarPrestamoCuota(cuota,vencimiento,importe){
	var vFecha=vencimiento.split("/");
	jQuery("#<portlet:namespace />cuota_nro").val(cuota);
	jQuery("#<portlet:namespace />fechaCuotaDia").val(vFecha[0]);
	jQuery("#<portlet:namespace />fechaCuotaMes").val(parseFloat(vFecha[1])-1);
	jQuery("#<portlet:namespace />fechaCuotaAnio").val(vFecha[2]);
	jQuery("#<portlet:namespace />cuota_importe").val(parseFloat(importe).toFixed(2));
	
}


function <portlet:namespace />agregarCuotas(){
	var total=jQuery("#<portlet:namespace />cuota_importe").val();
	var numero=jQuery("#<portlet:namespace />cuota_nro").val();
	
	var dia  = document.getElementById("<portlet:namespace />fechaCuotaDia");
	var mes= document.getElementById("<portlet:namespace />fechaCuotaMes");
	var anio = document.getElementById("<portlet:namespace />fechaCuotaAnio");
	
	
	if(total==null || total<=0 || total==''){
		alert("Debe ingresar el importe");
		return false;
	}
	
	if(numero==null || numero<=0 || numero==''){
		alert("Debe ingresar el Número");
		return false;
	}

	if(dia==null || dia=='' || dia<=0){
		alert("Debe ingresar la Fecha de Vencimiento de  Primera Cuota");
		return false;
	}

	if(mes==null || mes=='' || mes<0){
		alert("Debe ingresar la Fecha de Vencimiento de  Primera Cuota");
		return false;
	}
	
	if(anio==null || anio=='' || anio<=0){
		alert("Debe ingresar la Fecha de Vencimiento de  Primera Cuota");
		return false;
	}

	
 	var busquedaNom = {"total":total,"numero":numero,"fechadia":dia.value,"fechames":mes.value,
 			           "fechaanio":anio.value,"cmd":"agregarCuotas"};
 	
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" /></portlet:renderURL>';
	jQuery('#<portlet:namespace />div_cuotas').load(url,busquedaNom, function(){
		
		jQuery("#<portlet:namespace />cuota_nro").val("");
		jQuery("#<portlet:namespace />fechaCuotaDia").val("-1");
		jQuery("#<portlet:namespace />fechaCuotaMes").val("-1");
		jQuery("#<portlet:namespace />fechaCuotaAnio").val("-1");
		jQuery("#<portlet:namespace />cuota_importe").val("");
		
														//jQuery('#<portlet:namespace />buscando').hide();      
	});	
}




function eliminarPrestamoCuota(numero){
 if(confirm("Esta seguro de Eliminar la CUOTA?")){		
 	var busquedaNom = {"numero":numero,"cmd":"eliminarCuotas"};
 	
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" /></portlet:renderURL>';
	jQuery('#<portlet:namespace />div_cuotas').load(url,busquedaNom, function(){
	});	
 }	
}

function <portlet:namespace />anularPrestamo(){
	if (<portlet:namespace />validarCampos()) {
		document.getElementById("<portlet:namespace />cuil").disabled=false;
		document.getElementById("<portlet:namespace />inte").disabled=false;
		document.getElementById("<portlet:namespace />fc_tipo").disabled=false;
		document.getElementById("<portlet:namespace />cod_Hotel").disabled=false;
		habilitaCargaAcuerdo(true);
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" />'+
		'<liferay-portlet:param name="cmd" value="delete"/>'+
		'</liferay-portlet:renderURL>';
		document.<portlet:namespace />fmS.method = 'post';
		submitForm(document.<portlet:namespace />fmS, url);
	}
	return false;		
}

function verPagosPrestamo(){
	var prestamo=jQuery("#<portlet:namespace />ac_nro").val();
 	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" />'+
	'<liferay-portlet:param name="cmd" value="verPagos"/>'+
	'</liferay-portlet:renderURL>';
	
	
	document.<portlet:namespace />fmS.method = 'post';
	submitForm(document.<portlet:namespace />fmS, url);	
 	
}

function actualizarMonto(){
	var factura=jQuery("#<portlet:namespace />fc_importe").val();
	var movilidad=jQuery("#<portlet:namespace />ac_movilidad").val();
	var acuerdoImporte=parseFloat(factura)+parseFloat(movilidad);
	jQuery("#<portlet:namespace />ac_importe").val(acuerdoImporte);
	
	var acuerdo=jQuery("#<portlet:namespace />ac_importe").val();
	var porcentaje=jQuery('#<portlet:namespace />ac_interes_porcentaje').val();
	var interes= parseFloat(acuerdo) * parseFloat(porcentaje)/100;
    var total= parseFloat(acuerdo)+parseFloat(interes);	
	jQuery("#<portlet:namespace />ac_total").val(Math.round((total) * 100)/100);
	jQuery("#<portlet:namespace />ac_interes_importe").val(Math.round((interes) * 100)/100);
}


function <portlet:namespace />nuevoPrestamo() {
	jQuery("#<portlet:namespace />id_prestamo").val('0');
	jQuery("#<portlet:namespace />id_seccional").val('');
	jQuery("#<portlet:namespace />seccional").val('');
	
	var codHotel = jQuery("#<portlet:namespace />cod_Hotel").val();
	//jQuery("#<portlet:namespace />id_hotel").val(codHotel);
	
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/hoteles/hoteles_prestamos_abm" />'+
	'<liferay-portlet:param name="cmd" value="write"/>'+
	'<liferay-portlet:param name="id_hotel" value="__idHotel"/>'+
	'</liferay-portlet:renderURL>';
	
	url = url.replace("__idHotel",codHotel);
	
	document.<portlet:namespace />fmS.method = 'post';
	submitForm(document.<portlet:namespace />fmS, url);
}
</script>

