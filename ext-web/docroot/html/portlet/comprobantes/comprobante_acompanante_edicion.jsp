<%@ include file="/html/portlet/comprobantes/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<portlet:defineObjects/>

<%
   NumberFormat format2D = new DecimalFormat("#0.00");

	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "comprobantes";
	}else if(renderResponse.getNamespace().equals("_COM_1_")){
		portlet_name = "comprobantes";
	}
	List<ClaseBase>sectores = (List<ClaseBase>)ComprobanteServiceUtil.getSectoresByUser(user.getScreenName());
	String usuario=user.getScreenName();
	
	ComprobanteAcompanante comprobante=(ComprobanteAcompanante)request.getSession().getAttribute(WebKeysComprobantes.COMPROBANTE_ACOMPANANTE_EN_EDICION);
	comprobante.setCuit(comprobante.getAcreedorEmpresa().getCuit());
	
	request.getSession().setAttribute(WebKeysComprobantes.COMPROBANTE_IMAGEN_VIEW,comprobante);
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	fecha.setTime(new Date());


	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	Calendar fechaEmision = CalendarFactoryUtil.getCalendar();
	fechaEmision.setTime(new Date());
	if(comprobante.getFechaEmision()==null){
		fechaEmision.setTime(new Date());
	}else{
	  fechaEmision.setTime(comprobante.getFechaEmision());
	} 
	
	Calendar fechaRecepcion = CalendarFactoryUtil.getCalendar();
	fechaRecepcion.setTime(new Date());
	if(comprobante.getFechaRecepcion()==null){
		fechaRecepcion.setTime(new Date());
	}else{
	  fechaRecepcion.setTime(comprobante.getFechaRecepcion());
	}
	
	Calendar fechaVencimiento = CalendarFactoryUtil.getCalendar();
	fechaVencimiento.setTime(new Date());
	if(comprobante.getFechaVencimiento()==null){
		fechaVencimiento.setTime(new Date());
	}else{
	  fechaVencimiento.setTime(comprobante.getFechaVencimiento());
	} 
	
	Calendar periodoPrestacion = CalendarFactoryUtil.getCalendar();
	periodoPrestacion.setTime(new Date());
	if(comprobante.getPeriodoPrestacion()==null){
		periodoPrestacion.setTime(new Date());
	}else{
		periodoPrestacion.setTime(comprobante.getPeriodoPrestacion());
	} 
	
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
 		
 	String esEditableStr = ParamUtil.getString(request, "esEditable");
 	if (esEditableStr == null || esEditableStr.equals("false")){
 		esEditableStr ="false";
 	}
 		boolean esEditable = Boolean.parseBoolean(esEditableStr);
%>

<form action="" method="post" name="<portlet:namespace />fmS">

       <liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	   <liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	   <liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
      <liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />




		<fieldset class="block-labels">
		<legend><liferay-ui:message key="comprobante" /></legend>
		<table width="70%" class="lfr-table">
				<tr>
					<td><label><liferay-ui:message key="tipo" />:</label></td>
					<td>
					<select id="<portlet:namespace />tipo_comprobante" name="<portlet:namespace />tipo_comprobante" disabled="disabled">
						<option value="" >Todos</option>						
						<option value="FCP" <%if (comprobante != null && comprobante.getTipoComprobante() !=null && 
					               "FCP".equals(comprobante.getTipoComprobante()) ) { %>
							selected="selected" <%} %> 
						 >FCP</option>
						<option value="NCR" <%if (comprobante != null && comprobante.getTipoComprobante() !=null && 
					               "NCR".equals(comprobante.getTipoComprobante()) ) { %>
							selected="selected" <%} %>
						 >NCR</option>
					    <option value="NDB" <%if (comprobante != null && comprobante.getTipoComprobante() !=null && 
					               "NDB".equals(comprobante.getTipoComprobante()) ) { %>
							selected="selected" <%} %>
						 >NDB</option>	 
						<option value="RCB" <%if (comprobante != null && comprobante.getTipoComprobante() !=null && 
					               "RCB".equals(comprobante.getTipoComprobante()) ) { %>
							selected="selected" <%} %>
						>RCB</option>
						</select>
					</td>
					
					<td><label><liferay-ui:message key="letra" />:</label></td>
					<td>
					<select id="<portlet:namespace />letra" name="<portlet:namespace />letra" disabled="disabled">
						<option value="">Todos</option>						
						<option value="A"  <%if (comprobante != null && comprobante.getLetraComprobante() !=null && 
					               "A".equals(comprobante.getLetraComprobante()) ) { %>
							selected="selected" <%}%>
						 >A</option>
						<option value="B"  <%if (comprobante != null && comprobante.getLetraComprobante() !=null && 
					               "B".equals(comprobante.getLetraComprobante()) ) { %>
							selected="selected" <%}%>>B</option>
						<option value="C" <%if (comprobante != null && comprobante.getLetraComprobante() !=null && 
					               "C".equals(comprobante.getLetraComprobante()) ) { %>
							selected="selected" <%}%>>C</option>
						<option value="M" <%if (comprobante != null && comprobante.getLetraComprobante() !=null && 
					               "M".equals(comprobante.getLetraComprobante()) ) { %>
							selected="selected" <%}%>>M</option>
						</select>
					</td>
					<td>
						<label><liferay-ui:message key="pto-venta" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />pto_venta" name="<portlet:namespace />pto_venta" onkeydown="allowOnlyDigits(event)" 
						value="<%=String.format("%05d",comprobante.getPtoVenta())  %>" readonly="readonly"/>
					</td>
					<td>
						<label><liferay-ui:message key="numero" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />nro_comprobante" name="<portlet:namespace />nro_comprobante" value="<%=comprobante.getNroComprobante() %>" maxlength="25"/
						readonly="readonly">
					</td>
		</tr>
		</table>			
		<table class="lfr-table">
		   <tr><td colspan="8">&nbsp;</td></tr>
		   <tr>			
					
			 <td><label>Estado:</label></td>
			 <td>
				<select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
						<option value="">Todos</option>						
						<option value="Verificado" <%if (comprobante != null && comprobante.getEstado() !=null && 
					               "Verificado".equals(comprobante.getEstado()) ) { %>
							selected="selected" <%}%>
						>Verificado</option>
						<option value="Rechazado" <%if (comprobante != null && comprobante.getEstado() !=null && 
					               "Rechazado".equals(comprobante.getEstado()) ) { %>
							selected="selected" <%}%>
						>Rechazado</option>
					</select>
			</td>
					
			<td><label>Area Liquidación:</label></td>
			<td>
					<select id="<portlet:namespace />sector" name="<portlet:namespace />sector">
						<option value="">Todos</option>	
						<%for(ClaseBase sector:sectores) {%>
						<option
							value="<%=sector.getId() %>"
							<%if (comprobante != null && comprobante.getSectorDestino() !=null && 
									sector.getId().equals(comprobante.getSectorDestino()) ) { %>
							selected="selected" <%}%>
							><%=sector.getId() %>
						</option>
					    <%}%>					
				    </select>
			</td>
					
					
		 </tr>
	</table>
	<table class="lfr-table">			
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td><label><liferay-ui:message key="emisor" />:</label></td>
					<td colspan="7"><liferay-ui:message key="cuit" />&nbsp;<input type="text" id="<portlet:namespace />cuit_compr_emisor" name="<portlet:namespace />cuit_compr_emisor"
						onkeydown="allowOnlyDigits(event)" size="13" maxlength="11" value ="<%=comprobante.getAcreedorEmpresa().getCuit()%>" readonly="readonly"/>
					</td>
					<td>
						<label><liferay-ui:message key="razon-social" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />razon_social" name="<portlet:namespace />razon_social" 
						     size="50" value="<%=comprobante.getAcreedorEmpresa().getRazon_soc()%>" maxlength="60" readonly="readonly"/>
					</td>
					
				</tr>
	</table>
	<table class="lfr-table">			
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<label><liferay-ui:message key="fecha-emision" /></label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date 
						monthNullable="true"
						dayValue="<%=comprobante.getFechaEmision()!=null?fechaEmision.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaEmisionComprobanteDia"
						monthValue="<%=comprobante.getFechaEmision()!=null?fechaEmision.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						monthParam="fechaEmisionComprobanteMes"
						yearParam="fechaEmisionComprobanteAnio"
						yearValue="<%=comprobante.getFechaEmision()!=null?fechaEmision.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
					<td>
						<label><liferay-ui:message key="fecha-vencimiento" /> </label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaVencimientoComprobanteDia"
						dayValue="<%=comprobante.getFechaVencimiento()!=null?fechaVencimiento.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						monthParam="fechaVencimientoComprobanteMes"
						monthValue="<%=comprobante.getFechaVencimiento()!=null?fechaVencimiento.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						yearParam="fechaVencimientoComprobanteAnio" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearValue="<%=comprobante.getFechaVencimiento()!=null?fechaVencimiento.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
					<td>
						<label><liferay-ui:message key="fecha-alta" /></label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayValue="<%=comprobante.getFechaRecepcion()!=null?fechaRecepcion.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						dayParam="fechaRecepcionComprobanteDia"
						monthValue="<%=comprobante.getFechaRecepcion()!=null?fechaRecepcion.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						monthParam="fechaRecepcionComprobanteMes"
						yearParam="fechaRecepcionComprobanteAnio" 
						yearValue="<%=comprobante.getFechaRecepcion()!=null?fechaRecepcion.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
				</tr>
    </table>
	<table class="lfr-table">				
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
				    <td>
						<label><liferay-ui:message key="cae" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />cae" name="<portlet:namespace />cae" 
						     size="50" value="<%=comprobante.getCae()%>" maxlength="60"/>
					</td>
				  
				    
				  
				     <td>
						<label><liferay-ui:message key="importe" />:</label>
					</td>
					<td>
						<input type="text" id="<portlet:namespace />importe" name="<portlet:namespace />importe" 
						     size="20" value="<%=comprobante.getImporteComprobante()!=null?format2D.format(comprobante.getImporteComprobante()):""%>" maxlength="60"/>
					</td>
				  
					
				</tr>	
				<tr><td colspan="8">&nbsp;</td></tr>
    </table>

    <table class="lfr-table"><tr>    
    <td>
		<label>DNI:</label>
	</td>
	<td>
		<input type="text" id="<portlet:namespace />dni" name="<portlet:namespace />dni" 
						     size="20" value="<%=comprobante.getAfiliado().getDocu_numero()!=null?comprobante.getAfiliado().getDocu_numero():""%>" maxlength="60"/>
	</td>
					
	</tr>
	 <tr><td colspan="8">&nbsp;</td></tr>				
	</table>
    
    <table>	
               <tr><td>
                  <table><tr>
                    <td>
                     <a href="javascript:<portlet:namespace />showHideDivAfiliado();">
		               <legend>
			           <liferay-ui:message	key="afiliado" /> 
			            <img name="arrow_afiliado" id="<portlet:namespace />arrow_afiliado" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
		               </legend>
		             </a>
		            </td>
		            <td>
                     <a href="javascript:<portlet:namespace />showHideDivPrestacion();">
		              <legend>
			           <liferay-ui:message	key="prestacion" /> 
			            <img name="arrow_prestacion" id="<portlet:namespace />arrow_prestacion" alt="<liferay-ui:message key='editar'/>" src="<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png"/>
		              </legend>
		             </a>
		            </td>
		          </tr>
		          </table> 
		        </td></tr>
		        
		        
				<tr>
			     <td>
				  <table class="lfr-table" style="border-collapse: separate; border-spacing:5px;" >
				   <tr>
				      <td>
				         <div id="<portlet:namespace/>divAfiliadosFiltro" >
		                    <fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
					           <liferay-util:include page='/html/portlet/comprobantes/busqueda_afiliado.jsp'>
						       <liferay-util:param value="<%= String.valueOf(true) %>" name="edit_mode" />
						       <liferay-util:param value="<%= null %>" name="discapacidad" />
						       <liferay-util:param value="<%= String.valueOf(true) %>" name="pag_reintegro" />
						       <liferay-util:param name="cuil" value='' />
						       <liferay-util:param name="inte" value='' />
						       <liferay-util:param value="_filtro" name="origen" />
						       </liferay-util:include>
			                </fieldset>
			             </div> 
				      </td> 
				   </tr>
				  </table>
			    </td>
			   </tr>
			</table>
			
			<tables>
			  <tr>
			     <td colspan="3">
		            <div id="<portlet:namespace />div_prestacion">
		               <fieldset class="block-labels">
		                 <legend>
			                <liferay-ui:message	key="prestacion" /> </legend>
		               
		                 <liferay-util:include
			              page="/html/portlet/comprobantes/busqueda_prestacion.jsp">
			              <liferay-util:param name="search_url"  value="/comprobantes/buscar_prestacion" />
			                    <liferay-util:param name="id_prestacion" value='' />
			              <liferay-util:param name="codigo" value='' />
			              <liferay-util:param name="prestacion" value='' />
			              <liferay-util:param name="discapacidad" value='' />
			              <liferay-util:param name="esEditable"   value='true' />
			              <liferay-util:param name="suf" value='_trat'/>
		               </liferay-util:include></fieldset>
		               <table>
		               <tr>
		               <td colspan="4">&nbsp;</td>
		               </tr>
		               <tr>
		               <td>
						<label><liferay-ui:message key="cantidad" />:</label>
					   </td>
					   <td>
						<input type="text" id="<portlet:namespace />cantidad" name="<portlet:namespace />cantidad" 
						     size="20" value="<%=comprobante.getCantidad()!=null?comprobante.getCantidad():""%>" />
					   </td>
					   <td colspan="4">&nbsp;</td>
					   <td><label><liferay-ui:message key="periodo-prestacion" />:</label></td>
					   <td colspan="2"><liferay-ui:input-date dayParam="periodoDia"
						dayNullable="<%= true %>" 
						dayValue="<%=comprobante.getPeriodoPrestacion()!=null?periodoPrestacion.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						monthAndYearParam="periodoMesAnio"
						monthAndYearNullable="<%= true %>"
						monthValue="<%=comprobante.getPeriodoPrestacion()!=null?periodoPrestacion.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						yearValue="<%=comprobante.getPeriodoPrestacion()!=null?periodoPrestacion.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false%>" /></td>
						<td colspan="4">&nbsp;</td>
						
						
						</tr>
					   </table>
		            </div>
		          </td>
		          
		          
		          
			  </tr>
			  <tr><td colspan="8">&nbsp;</td></tr>
			</tables>
			
			<table>
             <tr>
              <td>
               <fieldset class="block-labels"> 
		         <legend><liferay-ui:message key="observaciones" />:</legend>
		   	         &nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="4" cols="160" maxlength="20000" 
		              id="<portlet:namespace />observaciones" 
		              name="<portlet:namespace />observaciones"
		              style="resize:vertical;"><%=comprobante.getObservaciones()!=null?comprobante.getObservaciones():"" %></textarea>
	           </fieldset>		
              </td>
    
              </tr>
              <tr>
                <td>
                 <fieldset class="block-labels"> 
		           <legend><liferay-ui:message key="comentario" />:</legend>
		   	       &nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="4" cols="160" maxlength="20000" 
		              id="<portlet:namespace />comentario" 
		              name="<portlet:namespace />comentario"
		              style="resize:vertical;"><%=comprobante.getComentario()!=null?comprobante.getComentario():"" %></textarea>
	             </fieldset>		
                </td>
    
             </tr>
             <tr><td colspan="8">&nbsp;</td></tr>
             
             <tr>
					<td colspan="15">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="Autorizado por Área Médica:" />
							</legend>
							<table>
								<tr>
									<td><label><liferay-ui:message key="Cantidad" />:</label>
									</td>
									<td><input id="<portlet:namespace />cantidadAM"
										name="<portlet:namespace />cantidadAM" size="8" maxlength="15"
										type="text" value="<%=comprobante.getCantidadAreaMedica()!=null && comprobante.getCantidadAreaMedica()!=0?format2D.format(comprobante.getCantidadAreaMedica()):"1"%>" 
										onblur="calculatotalAM()" /></td>

									<td><label><liferay-ui:message key="Importe" />:</label>
									</td>
									<td><input id="<portlet:namespace />importeAM"
										name="<portlet:namespace />importeAM" size="8" maxlength="30"
										value='<%=comprobante.getImporteAreaMedica()!=null?format2D.format(comprobante.getImporteAreaMedica()):""%>' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										onblur="calculatotalAM()" /></td>

									<td><label><liferay-ui:message key="Total" />:</label></td>
									<td><input id="<portlet:namespace />totalAM"
										name="<portlet:namespace />totalAM" size="8" maxlength="30"
										value='<%=comprobante.getTotalAreaMedica()!=null?format2D.format(comprobante.getTotalAreaMedica()):""%>' 
										type="text" readonly="readonly" /></td>

									<td><label><liferay-ui:message key="Cargo OSPIM" />:</label>
									</td>
									<td><input id="<portlet:namespace />cargoospim"
										name="<portlet:namespace />cargoospim" size="8" maxlength="30"
										type="text" value="<%=comprobante.getCargoOspim()!=null?format2D.format(comprobante.getCargoOspim()):""%>"
										onkeydown="allowOnlyDigitsAndDecimals(event)" /></td>
									<td><label><liferay-ui:message key="cargo-terc" />:</label>
									</td>
									<td><input id="<portlet:namespace />cargops"
										name="<portlet:namespace />cargops" size="8" maxlength="30"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="<%=comprobante.getCargoTercerizadora()!=null?format2D.format(comprobante.getCargoTercerizadora()):""%>" /></td>
									<td><label>Cargo Monotributo:</label>
									<td><input id="<portlet:namespace />cargoimesa"
										name="<portlet:namespace />cargoimesa" size="8" maxlength="30"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="<%=comprobante.getCargoTercerizadoraMonotributistas()!=null?format2D.format(comprobante.getCargoTercerizadoraMonotributistas()):""%>" /></td>
										
									<td><label>Reconocido SSS:</label>
									</td>
									<td><input id="<portlet:namespace />reconocidoSSS"
										name="<portlet:namespace />reconocidoSSS" size="8" maxlength="30"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="<%=comprobante.getReconocidoSSS()!=null?format2D.format(comprobante.getReconocidoSSS()):""%>" /></td>	
										
									<td>Recuperable:</label> 
									<select name="<portlet:namespace />recuperable_sur" id="<portlet:namespace />recuperable_sur"
									  onchange="cambiorecuperable();">
									  
									  <option value="2" <%if (comprobante != null && comprobante.getRecuperable() !=null && 
					                       comprobante.getRecuperable()==2 ) { %>selected="selected" <%}%>>NO Recuperable</option>
					                       
									  <option value="1" <%if (comprobante != null && comprobante.getRecuperable() !=null && 
					                       comprobante.getRecuperable()==1 ) { %>selected="selected" <%}%>>SUR</option>
									  <option value="3" <%if (comprobante != null && comprobante.getRecuperable() !=null && 
					                       comprobante.getRecuperable()==3 ) { %>selected="selected" <%}%>>Integración</option>
									  
									</select>
									

									</tr>
							</table>
						</fieldset>
					</td>
				</tr>
			<tr><td colspan="8">&nbsp;</td></tr>	
             <tr>
             
              <table>
              <tr>
               <td colspan="8"><liferay-ui:message key="observacion" />:</td>
                <td> <textarea rows="4" cols="160" maxlength="20000" 
		              id="<portlet:namespace />observacion_prestacion" 
		              name="<portlet:namespace />observacion_prestacion"
		              style="resize:vertical;"><%=comprobante.getObservacionesPrestacion()!=null?comprobante.getObservacionesPrestacion():"" %></textarea></td>
					<br>
						<b><liferay-ui:message
								key="La observación de 200 caracteres como máximo." /></b></td>
					<td colspan="12">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			  </tr>		
			  </table>		
             
             </tr>
             
             
             
             
             <tr><td colspan="8">&nbsp;</td></tr>
             <tr><td>
                 <input id="<portlet:namespace />guardar"
		            value="<liferay-ui:message key="guardar"/>"
		            title="<liferay-ui:message key="guardar" />"
		            onClick="javascript: <portlet:namespace />salvarEdicion();"
		            type="button"
		            style="color:blue"
		           />
		         </td>    
           </table>
		</fieldset>
			
		 	
			
		<fieldset class="block-labels">
			<legend>
					<label>Imágenes:</label>
			</legend>
		
		     <jsp:include page='/html/portlet/comprobantes/comprobante_search_documentos.jsp' />  	
		</fieldset>
</form>
			
<script type="text/javascript">
//jQuery("#<portlet:namespace />guardar").css( "background-color", "green");

cambiorecuperable();

jQuery("#<portlet:namespace />periodoDia").hide();
jQuery('#<portlet:namespace />buscando').hide();
    
jQuery('#<portlet:namespace/>divAfiliadosFiltro').hide();
jQuery('#<portlet:namespace/>div_prestacion').hide();

document.getElementById("<portlet:namespace />tipo_comprobante").disabled=true;
document.getElementById("<portlet:namespace />letra").disabled=true;
jQuery('#<portlet:namespace />pto_venta').attr('readonly', true);
jQuery('#<portlet:namespace />nro_comprobante').attr('readonly', true);
jQuery('#<portlet:namespace />cuit_compr_emisor').attr('readonly', true);
jQuery('#<portlet:namespace />razon_social').attr('readonly', true);
    
    
   
jQuery('#<portlet:namespace/>nroDoc_filtro').val('<%=comprobante.getAfiliado().getDocu_numero()!=null?comprobante.getAfiliado().getDocu_numero():""%>');
jQuery('#<portlet:namespace/>codigo_trat').val('<%=comprobante.getCodigoPrestacion()!=null && !"0".equals(comprobante.getCodigoPrestacion())?comprobante.getCodigoPrestacion():""%>');
jQuery('#<portlet:namespace/>prestacion_trat').val('<%=comprobante.getDescripcionPrestacion()!=null && !"null".equals(comprobante.getDescripcionPrestacion())?comprobante.getDescripcionPrestacion():"" %>');

<% if(comprobante.getAfiliado()!=null && comprobante.getAfiliado().getCuil_titular()!=null){%>
   <portlet:namespace />buscarAfiliados_filtro();
   jQuery('#<portlet:namespace />divAfiliadosFiltro').css('display','block');
   jQuery('#<portlet:namespace />arrow_afiliado').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
<%}%>

<% if(comprobante.getCodigoPrestacion()!=null && !"0".equals(comprobante.getCodigoPrestacion()) && 
		!"".equals(comprobante.getCodigoPrestacion())){%>
   jQuery('#<portlet:namespace />div_prestacion').css('display','block');
   jQuery('#<portlet:namespace />arrow_prestacion').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
<%}%> 
			
function <portlet:namespace />showHideDivAfiliado(){
    if (jQuery("#<portlet:namespace />divAfiliadosFiltro").css('display') === 'none') {
		jQuery('#<portlet:namespace />divAfiliadosFiltro').css('display','block');
		jQuery('#<portlet:namespace />arrow_afiliado').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />divAfiliadosFiltro').css('display','none');
		jQuery('#<portlet:namespace />arrow_afiliado').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}			
	
function <portlet:namespace />showHideDivPrestacion(){
    if (jQuery("#<portlet:namespace />div_prestacion").css('display') === 'none') {
		jQuery('#<portlet:namespace />div_prestacion').css('display','block');
		jQuery('#<portlet:namespace />arrow_prestacion').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />div_prestacion').css('display','none');
		jQuery('#<portlet:namespace />arrow_prestacion').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}

function <portlet:namespace />salvarEdicion(){
	window.onbeforeunload = null;
	document.getElementById("<portlet:namespace />tipo_comprobante").disabled=false;
	document.getElementById("<portlet:namespace />letra").disabled=false;
	jQuery('#<portlet:namespace />pto_venta').attr('readonly', false);
	jQuery('#<portlet:namespace />nro_comprobante').attr('readonly', false);
	jQuery('#<portlet:namespace />cuit_compr_emisor').attr('readonly', false);
	jQuery('#<portlet:namespace />razon_social').attr('readonly', false);
	
	if(ValidaMontos()){       
	  if (<portlet:namespace />validarCampos()) {
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/comprobantes/comprobantes_portal_proveedores_acompanantes" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
		
		submitForm(document.<portlet:namespace />fmS, url);
	  }
	}  
	return false;		
}


function <portlet:namespace />validarCampos(){
	var result = true;
	var importe =jQuery('#<portlet:namespace />importe').val();
	
	var prestacion =jQuery('#<portlet:namespace />codigo_trat').val();
	
	if(importe.includes(",")){
		importe=importe.replace(".","").replace(",",".");
	}
	
	if(parseFloat(importe) == "0"){
		alert("El Importe del Comprobante no puede ser cero")
		return false;
	}
	
	if(prestacion==null || prestacion==""){
		alert("El código de prestación no puede estar vacío");
		return false;
	}
	
	return result;	
}	

function calculatotalAM(){
	importe=parseFloat(jQuery("#<portlet:namespace />importeAM").val());
	cantidad=parseFloat(jQuery("#<portlet:namespace />cantidadAM").val());
	total= importe * cantidad  ;
	jQuery("#<portlet:namespace />totalAM").val(Math.round(total.toFixed(2) * 100)/100);
}

function cambiorecuperable(){
	try{
		var recuperable=jQuery('#<portlet:namespace />recuperable_sur').val();
		if(recuperable==3 || recuperable==1){
			jQuery('#<portlet:namespace/>reconocidoSSS').attr('readonly', false);
		}else{
			jQuery('#<portlet:namespace/>reconocidoSSS').val(0);
			jQuery('#<portlet:namespace/>reconocidoSSS').attr('readonly', true);
		}
	}catch (err) {}	
}


function ValidaMontos(){

	var importeFC = jQuery('#<portlet:namespace />importe').val();
	if(importeFC.includes(",")){
		importeFC=importeFC.replace(".","").replace(",",".");
	}
	importeFC=parseFloat(importeFC);
	
	var importedouble = jQuery('#<portlet:namespace />totalAM').val();
	if(importedouble.includes(",")){
		importedouble=importedouble.replace(".","").replace(",",".");
	}
	importedouble=parseFloat(importedouble);
	
	
	var cargoospimdouble = jQuery('#<portlet:namespace />cargoospim').val();
	if(cargoospimdouble.includes(",")){
		cargoospimdouble=cargoospimdouble.replace(".","").replace(",",".");
	}
	cargoospimdouble=parseFloat(cargoospimdouble);

	
	var cargopsdouble = jQuery('#<portlet:namespace />cargops').val();
	if(cargopsdouble.includes(",")){
		cargopsdouble=cargopsdouble.replace(".","").replace(",",".");
	}
	cargopsdouble=parseFloat(cargopsdouble);
	
	var cargoimesadouble = jQuery('#<portlet:namespace />cargoimesa').val();
	if(cargoimesadouble.includes(",")){
		cargoimesadouble=cargoimesadouble.replace(".","").replace(",",".");
	}
	cargoimesadouble=parseFloat(cargoimesadouble);
	
	var reconocidoSSS = jQuery('#<portlet:namespace />reconocidoSSS').val();
	if(reconocidoSSS.includes(",")){
		reconocidoSSS=reconocidoSSS.replace(".","").replace(",",".");
	}
	reconocidoSSS=parseFloat(reconocidoSSS);

	if(isNaN(importedouble)) {		jQuery('#<portlet:namespace />totalAM').val()  ; importedouble=0; 	}
	if(isNaN(cargoospimdouble)) {	jQuery('#<portlet:namespace />cargoospim').val()  ; cargoospimdouble=0; 	}
	if(isNaN(cargopsdouble)) {		jQuery('#<portlet:namespace />cargops').val()  ; cargopsdouble=0; 	}
	if(isNaN(cargoimesadouble)) {		jQuery('#<portlet:namespace />cargoimesa').val()  ; cargoimesadouble=0; 	}
	if(isNaN(reconocidoSSS)) {		jQuery('#<portlet:namespace />reconocidoSSS').val()  ; reconocidoSSS=0; 	}
	if(isNaN(importeFC)) {		jQuery('#<portlet:namespace />importeFC').val()  ; importeFC=0; 	}
	
	totalCargos= Math.round((cargoospimdouble + cargopsdouble+cargoimesadouble + reconocidoSSS) * 100) / 100 ;

	if ( totalCargos >importeFC){
    	alert('La suma de los importes ( OSPIM y Tercerizadoras) no puede superar el Importe de la Factura.');
    	return false; 
    }
	
	
	if ( (totalCargos >importedouble || totalCargos<importedouble)){
    	alert('La suma de los importes ( OSPIM y Tercerizadoras ) no puede diferir del monto en el total ingresado.');
    	return false; 
    }
	

	if (totalCargos!=importedouble){
			alert('El importe total de la prestación debe coincidir con la suma de Cargo Ospim más Cargo Tercerizadora.');
			return false;
    }
 
   
   
   var recuperable  =  jQuery('#<portlet:namespace />recuperable_sur').val();
   if(recuperable==2){
	   if(reconocidoSSS>0){
	   	   alert('El importe reconocido debe estar vacio');
	   	   jQuery('#<portlet:namespace />reconocidoSSS').val('');
    	   return false;
	  } 
   }else  if(recuperable==1 || recuperable==3){
	   if(reconocidoSSS==0){
   		   alert('El importe reconocido debe ser mayor a cero');
       	   return false;
   	  }else if ( reconocidoSSS > importedouble){	
   	      alert('El importe Reconocido no puede superar el monto en el importe ingresado.');
   	      return false; 
   	  }
   }
   return true;
}

</script>
