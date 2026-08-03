<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>	
<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_LIQ_1_")){
		portlet_name = "liquidaciones";
	}
	
	String edit_mode = ParamUtil.getString(request, "edit_mode", null);
	String con_reclamo_prestacional = ParamUtil.getString(request, "con_reclamo_prestacional", null);
	
	Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
	fechaHoy.setTime(new Date());
	String viewStr = (String)request.getAttribute(WebKeysFarmacia.VIEW_REINTEGRO);
	boolean esView = false;
	if (viewStr != null){
		esView = true;
	}
	
	boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ENTIDAD_OSPIM);
	boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ENTIDAD_AMTIMA);
	boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysFarmacia.ROL_ENTIDAD_UOMA);
	
	List <ReintegroMedicamentoItem> medicamentos = (ArrayList<ReintegroMedicamentoItem>)request.getSession().getAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
	int nro_receta = 0;
	Calendar fechaRece = CalendarFactoryUtil.getCalendar();
	Date fechaMedicReceta = new Date();
	

	
	if(null!=medicamentos && medicamentos.size() > 0){
	 	 if (medicamentos.get(0) != null) {
			nro_receta = medicamentos.get(0).getNumeroReceta();
			fechaMedicReceta = medicamentos.get(0).getFechaReceta();
			if(fechaMedicReceta!=null){
				fechaRece.setTime(fechaMedicReceta);
			}	
	 	 }
	}
	
	Calendar comproFecha = CalendarFactoryUtil.getCalendar();

	
	comproFecha.setTime(new Date());
	
	

	Calendar fechaPrestacion = CalendarFactoryUtil.getCalendar();

	
	fechaPrestacion.setTime(new Date());
	
	
	
%>
				<table class="lfr-table">
					<tr>
						<%-- <td>
							<label><liferay-ui:message key="numero-receta" />:</label>
						</td>
						<td>
							<input id="<portlet:namespace />receta" name="<portlet:namespace />receta" size="6" maxlength="8" type="text" value="<%= nro_receta == 0 ? "" : nro_receta %>" <%= nro_receta == 0 ? "" : "readonly='readonly'" %>" 
							onkeydown="allowOnlyDigits(event);" onBlur="<portlet:namespace />validarNumeroReceta();"/>
						</td>
						<td><label><liferay-ui:message key="fecha-receta" />:</label></td>
						<td><liferay-ui:input-date dayParam="fechaRecetaDia"
							dayValue="<%= fechaRece.get(Calendar.DATE)%>"
							monthParam="fechaRecetaMes"
							monthValue="<%= fechaRece.get(Calendar.MONTH) %>"
							yearParam="fechaRecetaAnio"
							yearValue="<%= fechaRece.get(Calendar.YEAR) %>"
							yearRangeStart="<%= fechaRece.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaRece.get(Calendar.YEAR) + 10 %>"
							firstDayOfWeek="<%= fechaRece.getFirstDayOfWeek() - 1 %>"
							disabled="<%= esView %>" /></td>
						<td colspan="2"> --%>
							<input id="<portlet:namespace />accion_t" name="<portlet:namespace />accion_t" size="40" maxlength="40" type="hidden" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/>
						</td>
					</tr>
				
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>
				
				
					<tr>	
						<tr>
						<td><label>F. Prestación: </label></td>
						<td colspan="2"><liferay-ui:input-date dayParam="fechaPrestacionDia"
							dayValue="" dayNullable="<%=true%>"
							monthParam="fechaPrestacionMes"
							monthValue="-1" monthNullable="<%=true%>"
							yearParam="fechaPrestacionAnio"
 							yearValue="" yearNullable="<%=true%>"	
 						    yearRangeStart="<%= fechaPrestacion.get(Calendar.YEAR) - 6 %>"
							yearRangeEnd="<%= fechaPrestacion.get(Calendar.YEAR) + 1 %>"
							firstDayOfWeek="<%= fechaPrestacion.getFirstDayOfWeek() - 1 %>"
							disabled="<%=esView %>" />
						</td>	
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
								
						<td><label><liferay-ui:message key="troquel" />:</label></td>
						<td>
							<input id="<portlet:namespace />troquel" name="<portlet:namespace />troquel" onkeydown="allowOnlyDigits(event);" size="7" maxlength="7" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/>
						</td>
					<td>
						<label><liferay-ui:message key="cantidad" />: </label></td>
					<td><input id="<portlet:namespace />cantidad" name="<portlet:namespace />cantidad" size="3" maxlength="3" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>
					<td><label>Código de Barras:</label></td>
					<td>
						<input id="<portlet:namespace />codBarras" name="<portlet:namespace />codBarras" onkeydown="allowOnlyDigits(event);" size="13" maxlength="14" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/>
					</td>					
						<td colspan="5" >
							<div style="visibility:hidden;" id="<portlet:namespace />div_pmo">
								<p>MEDICAMENTO INCLUIDO EN EL PMO</p>
								<input id="<portlet:namespace />pmo" value="" type="hidden" name="<portlet:namespace />C"/>
							</div>
						</td>						
					</tr>
					<tr>
						<td colspan="13">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="nombre" />:</label></td>
						<td><input id="<portlet:namespace />nombre_med" name="<portlet:namespace />nombre_med" size="20" maxlength="20" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>						
						<td><label><liferay-ui:message key="presentacion" />:</label></td>
						<td><input id="<portlet:namespace />presentacion" name="<portlet:namespace />presentacion" size="30" maxlength="30" type="text" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/></td>											
						<td><label><liferay-ui:message key="porcentaje" />:</label></td>
						<td colspan="4">
						<input id="<portlet:namespace />laboratorio" name="<portlet:namespace />laboratorio" size="30" maxlength="30" type="hidden" value="" <%= !Boolean.parseBoolean(edit_mode) ? " readonly='readonly'" : ""  %>/>
						<input id="<portlet:namespace />porcentaje" name="<portlet:namespace />porcentaje" size="5" maxlength="5" type="text" value="" <%= !Boolean.parseBoolean(edit_mode)? " readonly='readonly'" : ""  %>/>						
						<td><label>Dividir %:</label>&nbsp;<input type="checkbox"
						id="<portlet:namespace />dividir"
						name="<portlet:namespace />dividir" value="false"
						/></td>
						<!-- </td> -->
					</tr>
					<tr>
					    <td><input id="<portlet:namespace />porc_anterior" name="<portlet:namespace />porc_anterior" size="5" maxlength="5" type="hidden" value="0" readonly="readonly"/></td>
						<td><input id="<portlet:namespace />porc_sss" name="<portlet:namespace />porc_sss" size="5" maxlength="5" type="hidden" value="0" readonly="readonly"/></td>						
						<td><input id="<portlet:namespace />porc_ospim" name="<portlet:namespace />porc_ospim" size="5" maxlength="5" type="hidden" value="0" readonly="readonly"/></td>
						<td><input id="<portlet:namespace />porc_amtima" name="<portlet:namespace />porc_amtima" size="5" maxlength="5" type="hidden" value="0" readonly="readonly"/></td>						
					</tr>
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>					
					<tr>
						<td><label><liferay-ui:message key="precio-publico" />: $</label></td>
						<td><input id="<portlet:namespace />precio" name="<portlet:namespace />precio" size="10" maxlength="10" type="text" value=""/></td>
						<td><label><liferay-ui:message key="monto-ospim" />: $</label></td>
						<td><input id="<portlet:namespace />monto_cober_ospim" name="<portlet:namespace />monto_cober_ospim" size="10" maxlength="10" type="text" value=""/></td>																		
						<td><label><liferay-ui:message key="monto-amtima" />: $</label>&nbsp;</td>
						<td><input id="<portlet:namespace />monto_cober_amtima" name="<portlet:namespace />monto_cober_amtima" size="10" maxlength="10" type="text" value=""/></td>						
						<td><label><liferay-ui:message key="monto-prestadora" />: $</label>&nbsp;</td>
						<td><input id="<portlet:namespace />monto_cober_prestadora" name="<portlet:namespace />monto_cober_prestadora" size="10" maxlength="10" type="text" value=""/></td>
						<td><label>Monto IMESA: $</label>&nbsp;</td>
						<td><input id="<portlet:namespace />monto_cober_imesa" name="<portlet:namespace />monto_cober_imesa" size="10" maxlength="10" type="text" value=""/></td>
					</tr>										
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>					
					<tr>
						<input id="<portlet:namespace />precio_ospim" name="<portlet:namespace />precio_ospim" size="10" maxlength="10" type="hidden" value="" readonly="readonly"/>
						<td><label><b>Total Precio al Público: $</b></label></td>
						<td><input id="<portlet:namespace />total_med" name="<portlet:namespace />total_med" size="10" maxlength="10" type="text" value="" readonly="readonly"/></td>
						<td><label><b><liferay-ui:message key="total-cobertura" />: $</b></label></td>
						<td ><input id="<portlet:namespace />total_cob" name="<portlet:namespace />total_cob" size="10" maxlength="10" type="text" value="" readonly="readonly"/></td>
					</tr>
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>
				
				
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>
							
 					</table>
				
			       <fieldset >
			       <legend>Comprobante</legend>
				
					<table class="lfr-table">
					
						
					<tr>
					
					
					<td colspan="6"><liferay-util:include
						page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						<liferay-util:param name="esEditable" value='<%= String.valueOf( !esView ) %>' />
						<liferay-util:param name="cuit" value='' />
						<liferay-util:param name="sucu" value='' />
						<liferay-util:param name="razon" value='' />
						<liferay-util:param name="id_seccional" value='0' />
						<liferay-util:param name="esEmpresaPrestador" value='true' />
						<liferay-util:param name="suf_entidad" value='_razon'/>				
					</liferay-util:include></td>
					
					</tr>
					 
				 <tr>
						<td colspan="9">&nbsp;</td>
					</tr>
					 
					</table>
					<table  class="lfr-table">
					<tr>	
					
							<td><label><liferay-ui:message key="comprobante" />&nbsp;&nbsp;</label></td>
							<td><select name="<portlet:namespace/>comprobante_tipo" id="<portlet:namespace/>comprobante_tipo"
									<% if (esView) { %> disabled="disabled" <%} %>>
									<option value="FCP">FCP</option>
									<option value="RCB">RCB</option>
									<option value="OTR">OTRO</option>
								</select> &nbsp;
							<select name="<portlet:namespace />comprobante_letra" id="<portlet:namespace />comprobante_letra"
										<% if (esView) { %> disabled="disabled" <%} %>>
											<option value=""></option>
											<option value="A">A</option>
											<option value="B">B</option>
											<option value="C">C</option>	
							</select>&nbsp;&nbsp;&nbsp;
							<td>&nbsp;</td><td>&nbsp;</td>
								
							<td>										
							    <input id="<portlet:namespace />comprobante_suc"
								name="<portlet:namespace />comprobante_suc" size="8" maxlength="5"
								type="text" 
								value=""
								<% if (esView) { %> readonly="readonly" <%} %> />
							
							</td>
							<td>&nbsp;</td>
							
							<td>		
							    <input id="<portlet:namespace />comprobante_nro"
								name="<portlet:namespace />comprobante_nro" size="11" maxlength="8"
								type="text" 
								value=""
								<% if (esView) { %> readonly="readonly" <%} %> />
							</td>
							<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	
							<td colspan="2"><label>Fecha Comprobante:&nbsp;&nbsp;</label></td>
							<td colspan="10"><liferay-ui:input-date dayParam="comproFechaDia"
								dayValue="" dayNullable="<%=true%>"
								monthParam="comproFechaMes"
								monthValue="-1" monthNullable="<%=true%>"
								yearParam="comproFechaAnio"
								yearValue="" yearNullable="<%=true%>"
								yearRangeStart="<%= comproFecha.get(Calendar.YEAR) - 6 %>"
								yearRangeEnd="<%= comproFecha.get(Calendar.YEAR) + 1 %>"
								firstDayOfWeek="<%= comproFecha.getFirstDayOfWeek() - 1 %>"
								disabled="<%=esView %>" />
							</td>
							<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	
							<td>
						        <label>Importe Comprobante:</label>&nbsp;&nbsp;&nbsp;<input id="<portlet:namespace />importeCompro"
								name="<portlet:namespace />importeCompro" size="12" maxlength="12"
								type="text" 
								value="<%=""%>" 
								<% if (esView) { %>
								<%="readonly='readonly'" %> <%} else {%>
								onkeydown="allowOnlyDigitsAndDecimals(event) ;  limitDecimals(2,document.getElementById('<portlet:namespace />importeCompro'),event)"
								<%} %> />
						    </td>
					
					</tr>
				
					<tr>
						<td colspan="9">&nbsp;</td>
					</tr>
					 
		
										
				</table>
				</fieldset>		
				
				<table>
					<tr>
						<td>
						<c:if test="<%= Boolean.parseBoolean(edit_mode) %>">
							<input id="<portlet:namespace />buscarMedicamento" value="<liferay-ui:message key="buscar-medicamento"/>" title="<liferay-ui:message key="buscar-medicamento" />" type="button" onClick="javascript:<portlet:namespace />buscarMedicamentoFarmacia();"/>
							<input id="<portlet:namespace />agregarMedicamento" value="<liferay-ui:message key="agregar"/>" title="<liferay-ui:message key="agregar" />" type="button" onClick="javascript:<portlet:namespace />agregarMedicamentoFarmacia();"/>
						</c:if>
						</td>
						<td coslpan="8">
						<c:if test="<%= Boolean.parseBoolean(edit_mode) %>">
							<input id="<portlet:namespace />limpiarCampos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />limpiarCamposMedicamento();"/>
						</c:if>
						</td>
					</tr>
				</table>
				
				<input id="<portlet:namespace />fecha_alta_af" value="" type="hidden" name="<portlet:namespace />fecha_alta_af"/>
				<input id="<portlet:namespace />incapacidad_af" value="" type="hidden" name="<portlet:namespace />incapacidad_af"/>
				<input id="<portlet:namespace />registro" value="" type="hidden" name="#<portlet:namespace />registro"/>
				<input id="<portlet:namespace />id_medicamento" value="" type="hidden" name="<portlet:namespace />id_medicamento"/>				
				<input id="<portlet:namespace />id_prestacion" value="0" type="hidden" name="<portlet:namespace />id_prestacion"/>
				<div id="listado_medicamentos_result">
					<liferay-util:include page='/html/portlet/farmacia/medicamentos_lista_result.jsp'>
					</liferay-util:include>
				</div>
<script type="text/javascript">
	var popupMedicamento;
	
	function <portlet:namespace />agregarMedicamentoFarmacia(){

		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var idReclamoPrestacional=0;
		var idPrestacionReclamo=0;
		
		
		
		if (jQuery('#<portlet:namespace />id_tercerizadora').val() != "MPS" &&
			  jQuery('#<portlet:namespace />id_tercerizadora').val() != "MEN" &&
			  jQuery('#<portlet:namespace />id_tercerizadora').val() != "MIM" &&
			  jQuery('#<portlet:namespace />id_tercerizadora').val() != "MON" &&
			  jQuery('#<portlet:namespace />id_tercerizadora').val() != "MCE" &&
				(jQuery('#<portlet:namespace />monto_cober_prestadora').val() != "0" ||
						jQuery('#<portlet:namespace />monto_cober_imesa').val() != "0"))		
			 {
			alert("El afiliado debe tener una tercerizadora MOLINEROS POR ENSALUD para ingresarle un monto a la Tercerizadora");
			return false;
		}
		var periodo = jQuery('#<portlet:namespace />periodoMesAnio').val();	
		if (periodo == null || periodo == '') {
			alert('Debe seleccionar un periodo antes de cargar una receta');
			return false;
		}
		if(cuil==null || cuil=='' || inte==null || inte==''){
			alert('<liferay-ui:message key="debe-seleccionar-un-afiliado-antes-cargar-receta"/>');
			return false;
		}
		
		if (jQuery('#<portlet:namespace />monto_cober_prestadora').val() == ''){
			alert('El cargo prestadora debe cero o mayor');
			return false;
		}
		
		if (jQuery('#<portlet:namespace />monto_cober_imesa').val() == ''){
			alert('El cargo Monotributo debe cero o mayor');
			return false;
		}
		
		var cantidad=jQuery('#<portlet:namespace />cantidad	').val().trim();

		
		
		try {
			if( jQuery('#<portlet:namespace />total_cob').val()!=''){ 
				   var monto_cober_ospim =jQuery('#<portlet:namespace />monto_cober_ospim').val() *1 ;
				   var monto_cober_amtima =jQuery('#<portlet:namespace />monto_cober_amtima').val() *1;
				   var monto_cober_prestadora =jQuery('#<portlet:namespace />monto_cober_prestadora').val() *1;
				   var monto_cober_imesa =jQuery('#<portlet:namespace />monto_cober_imesa').val() *1;


				   var total = monto_cober_ospim + monto_cober_amtima + monto_cober_prestadora + monto_cober_imesa; 

		    	   if ( Math.round(total) >Math.round(jQuery('#<portlet:namespace />precio').val()*cantidad)  ){
		    		   alert('El monto ingresado no debe superar a Precio al público ' + jQuery('#<portlet:namespace />precio').val());		    		   
		    		   return false;		    		
		    	   }    	   	
			}			
		}
		catch (err) {}
		

		try {
			if( jQuery('#<portlet:namespace />importeoriginalreclamo').val()!=''){ // validar monto total reclamo				
				   importe1 =jQuery('#<portlet:namespace />monto_cober_ospim').val() ;
	    		   cantidad1 =jQuery('#<portlet:namespace />cantidad').val() ;	    		   
//	    		   total = cantidad1 * importe1  ;
	    		   monto_cober_ospim =jQuery('#<portlet:namespace />monto_cober_ospim').val() *1 ;
				   monto_cober_amtima =jQuery('#<portlet:namespace />monto_cober_amtima').val() *1;
				   monto_cober_prestadora =jQuery('#<portlet:namespace />monto_cober_prestadora').val() *1;
				   monto_cober_imesa =jQuery('#<portlet:namespace />monto_cober_imesa').val() *1;

				   total = monto_cober_ospim + monto_cober_amtima + monto_cober_prestadora + monto_cober_imesa; 
				   
	    		   totalOriginalReclamo=jQuery('#<portlet:namespace />importeoriginalreclamo').val() ;
	    		   
		    	   if ( Math.round(total) >Math.round(totalOriginalReclamo )  ){
		    		   alert('El monto ingresado no debe superar ' + jQuery('#<portlet:namespace />importeoriginalreclamo').val() + ' que es el original autorizado para esta prestación en el reclamo.');
		    		   return false;		    		
		    	   }    	   	
			}			
		}
		catch (err) {}
		
		

		
		var bajafecha=jQuery('#<portlet:namespace />baja_fecha').val();
		
		if(bajafecha!=null && bajafecha!=''){
			var diaBaja=parseInt(bajafecha.split("/")[0], 10);
			var mesBaja=parseInt(bajafecha.split("/")[1], 10);
			var anioBaja=parseInt(bajafecha.split("/")[2], 10);			
			var mesReceta=parseInt(periodo.split("_")[0], 10)+parseInt(1, 10);			
			var anioReceta=parseInt(periodo.split("_")[1], 10);
			if(mesReceta==1 || mesReceta==3 || mesReceta==5 || mesReceta==7 || mesReceta==8 || mesReceta==10 || mesReceta==12){			
				var diaReceta='31';
			}else if(mesReceta==4 || mesReceta==6 || mesReceta==9 || mesReceta==11 ){
				var diaReceta='30';
			}else if(mesReceta==2){
			    var diaReceta='2';
			}			
			if ((parseInt(anioReceta,10) > parseInt(anioBaja,10))
					|| (parseInt(anioReceta,10) == parseInt(anioBaja,10) && parseInt(mesReceta,10) > parseInt(mesBaja,10))
					|| (parseInt(anioReceta,10) == parseInt(anioBaja,10) && parseInt(mesReceta,10) == parseInt(mesBaja,10) && parseInt(diaReceta,10) > parseInt(diaBaja,10))){
					alert("La fecha de la prestación corresponde a una fecha posterior a la baja del afiliado.");
					//return false; A pedido de ASANMARTIN 30082013
				}
			}		
		var fechaReceta='';
		//var diaRece=jQuery('#<portlet:namespace />fechaRecetaDia').val();	    
	    //var mesRece=parseInt(jQuery('#<portlet:namespace />fechaRecetaMes').val())+1;	    
	    //var anioRece=jQuery('#<portlet:namespace />fechaRecetaAnio').val();
	    //var fechaMedicReceta = diaRece+'/'+mesRece+'/'+anioRece;
	    
		//var nroReceta=jQuery('#<portlet:namespace />receta').val().trim();
		

		var troquel=jQuery('#<portlet:namespace />troquel').val().trim();	
		var nombre=jQuery('#<portlet:namespace />nombre_med').val().trim();
		var registro=jQuery('#<portlet:namespace />registro').val().trim();
		var presentacion=jQuery('#<portlet:namespace />presentacion').val().trim();
		var laboratorio=jQuery('#<portlet:namespace />laboratorio').val().trim();
		var cuil=jQuery('#<portlet:namespace />cuil').val().trim();
		var inte=jQuery('#<portlet:namespace />inte').val().trim();
		var accion_t=jQuery('#<portlet:namespace />accion_t').val().trim();
		var pmo=jQuery('#<portlet:namespace />pmo').val().trim();
		var cober_ospim=jQuery('#<portlet:namespace />monto_cober_ospim').val().trim();		
		var cober_amtima=jQuery('#<portlet:namespace />monto_cober_amtima').val().trim();
		var cober_prestadora=jQuery('#<portlet:namespace />monto_cober_prestadora').val().trim();
		var cober_imesa=jQuery('#<portlet:namespace />monto_cober_imesa').val().trim();


		
		var porc_sss=jQuery('#<portlet:namespace />porc_sss').val().trim();
		var porc_ospim=jQuery('#<portlet:namespace />porc_ospim').val().trim();
		var porc_amtima=jQuery('#<portlet:namespace />porc_amtima').val().trim();
		var precio_pub=jQuery('#<portlet:namespace />precio').val().trim();
		var precio_ospim=jQuery('#<portlet:namespace />precio_ospim').val().trim();
		var id_medicamento=jQuery('#<portlet:namespace />id_medicamento').val().trim();
		var id_prestacion=jQuery('#<portlet:namespace />id_prestacion').val().trim();
		var cod_barras=jQuery('#<portlet:namespace />codBarras').val().trim();
		var porcentaje=jQuery('#<portlet:namespace />porcentaje').val().trim();
		
		var diaComprobante=jQuery('#<portlet:namespace />comproFechaDia').val();	    
	    var mesComprobante=jQuery('#<portlet:namespace />comproFechaMes').val();	    
	    var anioComprobante=jQuery('#<portlet:namespace />comproFechaAnio').val();
	    
	    
	    var comprobanteTipo=jQuery('#<portlet:namespace />comprobante_tipo').val();
	    var comprobanteSuc=jQuery('#<portlet:namespace />comprobante_suc').val();  
	    var comprobanteLetra=jQuery('#<portlet:namespace />comprobante_letra').val();
	    var comprobanteNro=jQuery('#<portlet:namespace />comprobante_nro').val();
	    var cuit_entidad=jQuery('#<portlet:namespace />cuit_entidad').val();
	    var sucursal_entidad=jQuery('#<portlet:namespace />sucursal_entidad').val();
		
		var fechaPrestacionDia=jQuery('#<portlet:namespace />fechaPrestacionDia').val();	    
	    var fechaPrestacionMes=jQuery('#<portlet:namespace />fechaPrestacionMes').val();	    
	    var fechaPrestacionAnio=jQuery('#<portlet:namespace />fechaPrestacionAnio').val();
	    
	    
	 
	    
	    
	    
	    
		if(!<portlet:namespace />validarBusqueda(troquel,nombre,presentacion,laboratorio,cod_barras)){
			return false;
		}
		if(!<portlet:namespace />validarMedicamento()){
			return false;
		}

		try {
			
			 idReclamoPrestacional =jQuery('#<portlet:namespace />id_reclamo_prestacional').val();
			 idPrestacionReclamo=jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val();
			 
			 jQuery('#<portlet:namespace />id_reclamo_prestacional').val(0);
			 jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val(0);
			 
			// habilita   controles de importes de la pretacion 
			<portlet:namespace />desactivaControlesPrestacionDesdeReclamo(false);
				
			}
		catch (err) {}
		
		
		if(!validarCuitPrestador()){
			return false;
		}
		
		
		if(comprobanteTipo != 'OTR' && (trim(comprobanteSuc).length == 0 || trim(comprobanteSuc) == 0)){
			alert("<liferay-ui:message key='comprobante-obligatorio-sucursal' />");
			jQuery('#<portlet:namespace />comprobante_suc').focus();
			return false;
		}

	
		if(comprobanteTipo != 'OTR' && (trim(comprobanteNro).length == 0 || trim(comprobanteNro) == 0)){
			alert("<liferay-ui:message key='comprobante-obligatorio' />");
			jQuery('#<portlet:namespace />comprobante_nro').focus();
			return false;
		}
	
		var importeCompro = jQuery('#<portlet:namespace />importeCompro').val(); 	
		if(trim(importeCompro).length == 0 || trim(importeCompro) == 0){
			alert("Importe del Comprobante es obligatorio");
			jQuery('#<portlet:namespace />importeCompro').focus();
			return false;
		}
		
		importe1 =jQuery('#<portlet:namespace />total_med').val() ;
		var m =  parseFloat(trim(importe1),10);
		var n = parseFloat(parseFloat(trim(importeCompro)));
		
		m = Math.round (m * 100) / 100;
		n = Math.round (n * 100) / 100;
			

		if (m > n){
			alert("El importe comprobante debe ser mayor o igual al importe total");
			jQuery('#<portlet:namespace />importeCompro').focus();
			return false;
		}	
		
		
		if(comprobanteTipo != 'OTR' && ( trim(comprobanteLetra).length == 0 || trim(comprobanteLetra) == 0)){
			alert("<liferay-ui:message key='comprobante-obligatorio-letra' />");
			jQuery('#<portlet:namespace />comprobante_letra').focus();
			return false;
		}
		
		
		
	    
	
		if(diaComprobante==null || diaComprobante==0 || diaComprobante=='' ||
				mesComprobante==null || mesComprobante==-1 || mesComprobante=='' ||
				anioComprobante==null || anioComprobante==0 || anioComprobante==''){
	    	    alert('Debe ingresar la fecha de la Comprobante');
	    	return false;	
	    }
		
		if(fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ||
				fechaPrestacionMes==null || fechaPrestacionMes==-1 || fechaPrestacionMes=='' ||
				fechaPrestacionAnio==null || fechaPrestacionAnio==0 || fechaPrestacionAnio==''){
	    	    alert('Debe ingresar la fecha de la Prestación');
	    	return false;	
	    }
	    
	 
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_medicamento_farmacia';
		
	    jQuery.post(url,{id_medicamento:id_medicamento,
		    			 cantidad:cantidad,
		    			 troquel:troquel, 
		    			 nombre:nombre,
		    			 idReclamoPrestacional:idReclamoPrestacional, 
		    			 idPrestacionReclamo:idPrestacionReclamo, 
		    			 laboratorio:laboratorio,
		    			 pmo:pmo,
		    			 cober_ospim:cober_ospim,
		    			 cober_amtima:cober_amtima,
		    			 cober_prestadora:cober_prestadora,
		    			 cober_imesa:cober_imesa,
		    			 porc_sss:porc_sss,
		    			 porc_ospim:porc_ospim,
		    			 porc_amtima:porc_amtima,
		    			 precio_pub:precio_pub,
		    			 precio_ospim:precio_ospim,
		    			 accion_t:accion_t,
		    			 presentacion:presentacion,
		    			 fecha_receta:fechaReceta,
		    		//	 receta:nroReceta,
		    		//	 fecha_medic_receta: fechaMedicReceta,
		    			 id_prestacion:id_prestacion,
		    			 cod_barras:cod_barras,
		    			 porcentaje:porcentaje,
		    			 diaComprobante:diaComprobante,
		    			 mesComprobante:mesComprobante,
		    			 anioComprobante:anioComprobante,
		    			 comprobanteTipo:comprobanteTipo,
		    			 comprobanteSuc:comprobanteSuc,
		    			 comprobanteLetra:comprobanteLetra,
		    			 comprobanteNro:comprobanteNro,
		    			 importeCompro:importeCompro,
		    			 cuit_entidad:cuit_entidad,
		    			 sucursal_entidad:sucursal_entidad,
		    			 fechaPrestacionDia:fechaPrestacionDia,
		    			 fechaPrestacionMes:fechaPrestacionMes,
		    			 fechaPrestacionAnio:fechaPrestacionAnio
		    			 },
	    	    							function( data ) {
 												jQuery("#listado_medicamentos_result").empty().append(jQuery(data));
 												<portlet:namespace />limpiarCamposMedicamento();
	    	    							}
				    );
	    
	    try {
	    <portlet:namespace />desactivaControlesPrestacionDesdeReclamo(false); // activa los controles de medicacion
	    <portlet:namespace />cancelar_prestaciones_reclamos();
	    }
	    catch (err) {}	    
	}

	
	
	
	
	
	function borraMedicamento(id,idReclamo,idPrestacionReclamo){
		var accion='borrar';
		if (id=='all'){
			accion='borrarAll';
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_medicamento_farmacia&id_prestacion='+id+
		'&accion='+accion+'&url='+ Math.floor(Math.random()*100);
		jQuery("#listado_medicamentos_result").load(url);
	}
	
	function <portlet:namespace />buscarMedicamentoFarmacia(){

		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var periodo = jQuery('#<portlet:namespace />periodoMesAnio').val();	
		if (periodo == null || periodo == '') {
			alert('Debe seleccionar un periodo antes de cargar una receta');
			return false;
		}
		if(cuil==null || cuil=='' || inte==null || inte==''){
			alert('<liferay-ui:message key="debe-seleccionar-un-afiliado-antes-cargar-receta"/>');
			return false;
		}
		
		var bajafecha=jQuery('#<portlet:namespace />baja_fecha').val();
		
		if(bajafecha!=null && bajafecha!=''){
			var diaBaja=parseInt(bajafecha.split("/")[0],10);
			var mesBaja=parseInt(bajafecha.split("/")[1],10);
			var anioBaja=parseInt(bajafecha.split("/")[2],10);
			var mesReceta=parseInt(periodo.split("_")[0],10);+parseInt('1');
			var anioReceta=parseInt(periodo.split("_")[1],10);
			if(mesReceta==1 || mesReceta==3 || mesReceta==5 || mesReceta==7 || mesReceta==8 || mesReceta==10 || mesReceta==12){			
				var diaReceta='31';
			}else if(mesReceta==4 || mesReceta==6 || mesReceta==9 || mesReceta==11 ){
				var diaReceta='30';
			}else if(mesReceta==2){
			    var diaReceta='2';
			}

			if ((parseInt(anioReceta) > parseInt(anioBaja))					
					|| (parseInt(anioReceta) == parseInt(anioBaja) && parseInt(mesReceta,10) > parseInt(mesBaja,10))
					|| (parseInt(anioReceta) == parseInt(anioBaja) && parseInt(mesReceta,10) == parseInt(mesBaja,10) && parseInt(diaReceta,10) > parseInt(diaBaja,10))){
					alert("El periodo corresponde a una fecha posterior a la baja del afiliado.");
					return false;
			}
		}
		if (jQuery("#<portlet:namespace />nombre_plan").val() == null || jQuery("#<portlet:namespace />nombre_plan").val() == '') {
			alert("El afiliado no tiene plan.");
			return false;
		}
		
		var troquel=jQuery('#<portlet:namespace />troquel').val();		
		var nombre=jQuery('#<portlet:namespace />nombre_med').val();
		var registro=jQuery('#<portlet:namespace />registro').val();
		var presentacion=jQuery('#<portlet:namespace />presentacion').val();
		var laboratorio=jQuery('#<portlet:namespace />laboratorio').val();
		var cod_barras=jQuery('#<portlet:namespace />codBarras').val();
		if(!<portlet:namespace />validarBusqueda(troquel,nombre,presentacion,laboratorio,cod_barras)){		
			return false;
		}
		popupMedicamento = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-medicamento" />",modal:true,width:800});

        var fecha_prestacion = jQuery("#<portlet:namespace />fprest").val();        	
        
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_medicamento&troquel='+troquel+
	        '&registro='+registro+'&nombre='+encodeURI(nombre)+'&presentacion='+encodeURI(presentacion)+'&laboratorio='+encodeURI(laboratorio)+'&popup=true&cuil='+cuil+'&inte='+inte+'&cod_barras='+cod_barras;	    	
        jQuery(popupMedicamento).load(url);
        
        

	}

	
	

	
<%-- 	function <portlet:namespace />validarNumeroReceta(){
		var numReceta = jQuery("#<portlet:namespace />receta").val();
		var id_reintegro = jQuery("#<portlet:namespace />numero").val();
		if (trim(numReceta) == '') {
			return;
		}
		if (id_reintegro == ''){
			id_reintegro = 0;
		}
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_numero_receta';
			url += '&numero_receta=' + numReceta + '&id_reintegro=' + id_reintegro;			
		jQuery.ajax({
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);				
				if ( parseInt(obj.receta) > 0 ){
					alert('El número de receta: ' + numReceta + ', no está permitido porque fue cargado en el sistema previamente, verifíquelo.');
					jQuery('#<portlet:namespace />receta').val('');
				}
			}
		});		
	} --%>
		
	function <portlet:namespace />validarBusqueda(troquel,nombre,presentacion,laboratorio,cod_barras){
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();		
		var periodo = jQuery('#<portlet:namespace />periodoMesAnio').val();
		
		if(trim(troquel.length)==0 && trim(nombre.length)==0 && trim(presentacion.length)==0 && trim(laboratorio.length)==0 && trim(cod_barras.length)==0) {
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}

	//desde la busqueda
	
	function seleccionaMedicamento(id_medicamento, troquel,registro,nombre,droga,presentacion,laboratorio,precio,cober_sss, cober_ospim,cober_amtima,precio_ospim, monto_ospim, monto_amtima,pmo, cod_barras, cober_total, accion_t){
		seleccionaCamposMedicamento(id_medicamento, troquel,registro,nombre,droga,presentacion,laboratorio,precio,cober_sss, cober_ospim,cober_amtima,precio_ospim, monto_ospim, monto_amtima, pmo, cod_barras, cober_total, accion_t);
		Liferay.Popup.close(popupMedicamento);
	}			

	//desde la busqueda
	function seleccionaCamposMedicamento(id_medicamento, troquel,registro,nombre,droga,presentacion,laboratorio,precio,cober_sss, cober_ospim,cober_amtima,precio_ospim, monto_ospim, monto_amtima, pmo, cod_barras, cober_total, accion_t){		
		jQuery('#<portlet:namespace />id_medicamento').val(id_medicamento);
		jQuery('#<portlet:namespace />troquel').val(troquel);
		jQuery('#<portlet:namespace />registro').val(registro);
		jQuery('#<portlet:namespace />nombre_med').val(nombre);
		jQuery('#<portlet:namespace />droga').val(droga);
		jQuery('#<portlet:namespace />presentacion').val(presentacion);
		jQuery('#<portlet:namespace />laboratorio').val(laboratorio);
		jQuery('#<portlet:namespace />precio').val(precio);
		jQuery('#<portlet:namespace />porc_ospim').val(cober_ospim);
		jQuery('#<portlet:namespace />porc_amtima').val(cober_amtima);
		jQuery('#<portlet:namespace />porc_sss').val(cober_sss);
		jQuery('#<portlet:namespace />precio_ospim').val(precio_ospim);
		jQuery('#<portlet:namespace />monto_cober_ospim').val(monto_ospim);
		jQuery('#<portlet:namespace />monto_cober_amtima').val(monto_amtima);	
		jQuery('#<portlet:namespace />pmo').val(pmo);
		jQuery('#<portlet:namespace />codBarras').val(cod_barras);
		jQuery('#<portlet:namespace />porcentaje').val(cober_total);
		jQuery('#<portlet:namespace />porc_anterior').val(cober_total);
		jQuery('#<portlet:namespace />accion_t').val(accion_t);
		if(pmo=='true'){
			jQuery('#<portlet:namespace />div_pmo').css('visibility','visible');
		}
		
		//jQuery('#<portlet:namespace />pmo').val(pmo);
		var cantidad=jQuery('#<portlet:namespace />cantidad').val().trim();
		
		if(cantidad==null || cantidad==''){
			jQuery('#<portlet:namespace />cantidad').val(1);
			cantidad=1;
		}
		try {
			if( jQuery('#<portlet:namespace />importeoriginalreclamo').val()!=''){ // prestacion del reclamo pretacional
				jQuery('#<portlet:namespace />porc_anterior').val('100'); 
				jQuery('#<portlet:namespace />porcentaje').val('100');
				jQuery('#<portlet:namespace />monto_cober_ospim').val(jQuery('#<portlet:namespace />importereclamo').val());
				jQuery('#<portlet:namespace />precio').val(jQuery('#<portlet:namespace />importereclamo').val());
			}	
		}
		catch (err) {}
        jQuery('#<portlet:namespace />monto_cober_prestadora').val(jQuery('#<portlet:namespace />monto_cober_prestadora_aux').val());
        jQuery('#<portlet:namespace />monto_cober_imesa').val(jQuery('#<portlet:namespace />cargo_imesa').val());
		
		var x = parseFloat(cantidad) * parseFloat(jQuery('#<portlet:namespace />precio').val());
		var y = (parseFloat(jQuery('#<portlet:namespace />monto_cober_amtima').val())+
				parseFloat(jQuery('#<portlet:namespace />monto_cober_ospim').val())) * parseFloat(cantidad);
		
		jQuery("#<portlet:namespace />total_med").val(Math.round(x * 100)/100);
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);

	}

	function <portlet:namespace />resetValid() {
		if (jQuery("#<portlet:namespace />id_seccional").val() != "") {
			jQuery("#<portlet:namespace />secc_seleccionada").val("1")
		}
	}
	
	<portlet:namespace />resetValid();

	function <portlet:namespace />limpiarCamposMedicamento() {
	
		if (document.getElementById('<portlet:namespace />troquel').disabled = false) {
			alert('Operacion Invalida');
			return false;
		}		
		
		
	 <%SimpleDateFormat	sdf = new SimpleDateFormat("dd-MM-yyyy");
	
		String diaCompro="", anioCompro = "";
		int mesCompro = 0;
		
		String fecha_compro_formatteada = sdf.format(new Date());
		String[] fecha_compro_spliteada = fecha_compro_formatteada.split("-");
		diaCompro = fecha_compro_spliteada[0];
		mesCompro = Integer.parseInt(fecha_compro_spliteada[1])-1;
		anioCompro = fecha_compro_spliteada[2];
		
		%>
		
		jQuery('#<portlet:namespace />troquel').val('');
		jQuery('#<portlet:namespace />registro').val('');
		jQuery('#<portlet:namespace />nombre_med').val('');
		jQuery('#<portlet:namespace />presentacion').val('');
		jQuery('#<portlet:namespace />laboratorio').val('');	
		jQuery('#<portlet:namespace />precio').val('');
		jQuery('#<portlet:namespace />cober_ospim').val('');		
		jQuery('#<portlet:namespace />cober_amtima').val('');
		jQuery('#<portlet:namespace />precio_ospim').val('');
		jQuery('#<portlet:namespace />pmo').val('');	
		jQuery('#<portlet:namespace />div_pmo').css('visibility','hidden');		
		jQuery('#<portlet:namespace />cantidad').val('');
		jQuery('#<portlet:namespace />porc_sss').val('');
		jQuery('#<portlet:namespace />porc_ospim').val('');
		jQuery('#<portlet:namespace />porc_amtima').val('');
		jQuery('#<portlet:namespace />total_med').val('');
		jQuery('#<portlet:namespace />total_cob').val('');
		jQuery('#<portlet:namespace />monto_cober_ospim').val('');
		jQuery('#<portlet:namespace />monto_cober_amtima').val('');
		jQuery('#<portlet:namespace />monto_cober_prestadora').val('');
		jQuery('#<portlet:namespace />monto_cober_imesa').val('');
		
		//jQuery('#<portlet:namespace />total').val('');
		jQuery('#<portlet:namespace />codBarras').val('');		
		jQuery('#<portlet:namespace />porcentaje').val('');
		jQuery("#<portlet:namespace />porcentaje").attr("readonly", false);
		jQuery('#<portlet:namespace />porc_anterior').val('');
		jQuery("#<portlet:namespace />total_med").val('');
		jQuery("#<portlet:namespace />total_cob").val('');
		jQuery("#<portlet:namespace />cantidad").val('');
		jQuery("#<portlet:namespace />id_prestacion").val('0');
		jQuery("#<portlet:namespace />id_medicamento").val('');
		document.getElementById("<portlet:namespace />dividir").checked = false;
		
		
		jQuery('#<portlet:namespace />monto_cober_ospim').attr('readonly', false);
		jQuery('#<portlet:namespace />monto_cober_prestadora').attr('readonly', false);
		jQuery('#<portlet:namespace />monto_cober_amtima').attr('readonly', false);
		jQuery('#<portlet:namespace />monto_cober_imesa').attr('readonly', false);
		jQuery("#<portlet:namespace />cantidad").attr('readonly', false);
		
		try {
			jQuery('#<portlet:namespace />id_reclamo_prestacional').val(0);
			jQuery('#<portlet:namespace />id_prestacion_reclamo_prestacional').val(0);
			<portlet:namespace />cancelar_prestaciones_reclamos();
		}
		
		catch (err) {}

	    
	    jQuery('#<portlet:namespace />comprobante_tipo').val('FCP');
	    jQuery('#<portlet:namespace />comprobante_suc').val('');  
	    jQuery('#<portlet:namespace />comprobante_letra').val('');
	    jQuery('#<portlet:namespace />comprobante_nro').val('');
	    jQuery('#<portlet:namespace />importeCompro').val('');
	    jQuery('#<portlet:namespace />cuit_entidad').val('');
	    jQuery('#<portlet:namespace />sucursal_entidad').val('');

	    jQuery('#<portlet:namespace />entidad_razon').val('');

	    
	    jQuery('#<portlet:namespace />comproFechaDia').val(<%=diaCompro%>);	    
	    jQuery('#<portlet:namespace />comproFechaMes').val(<%=mesCompro%>);	    
	    jQuery('#<portlet:namespace />comproFechaAnio').val(<%=anioCompro%>);
	    
	    
	    
	    jQuery('#<portlet:namespace />fechaPrestacionDia').val(<%=diaCompro%>);	    
	    jQuery('#<portlet:namespace />fechaPrestacionMes').val(<%=mesCompro%>);	    
	 	jQuery('#<portlet:namespace />fechaPrestacionAnio').val(<%=anioCompro%>);
	    
	    
	    
	}

	function <portlet:namespace />validarMedicamento() {
		if(jQuery('#<portlet:namespace />troquel').val().trim()==''){
			alert('<liferay-ui:message key="ingrese-troquel"/>');
			return false;
		}		
		if(jQuery('#<portlet:namespace />nombre_med').val().trim()==''){
			alert('<liferay-ui:message key="ingrese-nombre-med"/>');
			return false;
		}
		if(jQuery('#<portlet:namespace />presentacion').val().trim()==''){
			alert('<liferay-ui:message key="ingrese-presentacion"/>');
			return false;
		}	
		if(jQuery('#<portlet:namespace />laboratorio').val().trim()==''){
			alert('<liferay-ui:message key="ingrese-laboratorio"/>');
			return false;
		}
		if(jQuery('#<portlet:namespace />porcentaje').val().trim()==''){
			alert('Porcentaje?');
			return false;
		}				
		if(jQuery('#<portlet:namespace />precio').val().trim()==''){
			alert('<liferay-ui:message key="ingrese-precio"/>');
			return false;
		}
		if(jQuery('#<portlet:namespace />monto_cober_ospim').val().trim()==''){
			alert('<liferay-ui:message key="ingrese-cober-ospim"/>');
			return false;
		}
		if(jQuery('#<portlet:namespace />monto_cober_amtima').val().trim()==''){
			alert('<liferay-ui:message key="ingrese-cober-amtima"/>');
			return false;
		}
		if(jQuery('#<portlet:namespace />cantidad').val().trim()==''){
			alert('<liferay-ui:message key="ingrese-cantidad"/>');
			return false;
		}
		
		/*
		if(jQuery('#<portlet:namespace />codBarras').val().trim()==''){
			alert('Ingrese Código de Barras');
			return false;
		}
		*/
		
// 		if(jQuery('#<portlet:namespace />receta').val().trim()==''){
// 			alert('<liferay-ui:message key="ingrese-nro-receta"/>');
// 			return false;
// 		}
		return true;
	}
	
	jQuery('#<portlet:namespace />precio').change(function() {		
		var troquel = jQuery('#<portlet:namespace />troquel').val();
		var precio  = jQuery('#<portlet:namespace />precio').val();		
		var id_plan = jQuery('#<portlet:namespace />afi_id_plan').val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/calcular_valor_medicamento';
			url += '&troquel=' + troquel + '&precio=' + precio+'&id_plan='+id_plan;			
		jQuery.ajax({
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);				
				
				if ( parseInt(obj.precio_amtima) > 0 ){					
					jQuery('#<portlet:namespace />monto_cober_amtima').val(obj.precio_amtima);
					jQuery('#<portlet:namespace />monto_cober_ospim').val(obj.precio_ospim);
					jQuery('#<portlet:namespace />precio').val(obj.precio_publico);
					jQuery('#<portlet:namespace />total_cob').val(obj.total_cobertura);
					jQuery('#<portlet:namespace />total_med').val(obj.precio_publico);
				}
			}
		});				
	});
	
	
	/*
	jQuery('#<portlet:namespace />monto_cober_prestadora').change(function() {
		var cantidad=parseFloat(jQuery('#<portlet:namespace />cantidad').val());
		var amtima = jQuery('#<portlet:namespace />monto_cober_amtima').val();
		var ospim = jQuery('#<portlet:namespace />monto_cober_ospim').val();
		var monto_cober_prest = jQuery('#<portlet:namespace />monto_cober_prestadora').val();

		if (cantidad==null || trim(cantidad)=='') {cantidad=0;}		
		if (amtima==null || trim(amtima)=='') {amtima = 0;}
		if (ospim==null || trim(ospim)=='') {ospim = 0;}
		if (monto_cober_prest==null || trim(monto_cober_prest)=='') {monto_cober_prest = 0;}
		var y = (parseFloat(amtima)+parseFloat(ospim)+parseFloat(monto_cober_prest)) * parseFloat(cantidad);
		
		if(isNaN(y)) y=0;
		
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);				
	});
	jQuery('#<portlet:namespace />monto_cober_ospim').change(function() {
		var cantidad=parseFloat(jQuery('#<portlet:namespace />cantidad').val());
		var amtima = jQuery('#<portlet:namespace />monto_cober_amtima').val();
		var ospim = jQuery('#<portlet:namespace />monto_cober_ospim').val();
		var monto_cober_prest = jQuery('#<portlet:namespace />monto_cober_prestadora').val();

		if (cantidad==null || trim(cantidad)=='') {cantidad=0;}		
		if (amtima==null || trim(amtima)=='') {amtima = 0;}
		if (ospim==null || trim(ospim)=='') {ospim = 0;}
		if (monto_cober_prest==null || trim(monto_cober_prest)=='') {monto_cober_prest = 0;}
		var y = (parseFloat(amtima)+parseFloat(ospim)+parseFloat(monto_cober_prest) * parseFloat(cantidad);
		
		if(isNaN(y)) y=0;
		
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);				
	});
	jQuery('#<portlet:namespace />monto_cober_amtima').change(function() {
		var cantidad=parseFloat(jQuery('#<portlet:namespace />cantidad').val());
		var amtima = jQuery('#<portlet:namespace />monto_cober_amtima').val();
		var ospim = jQuery('#<portlet:namespace />monto_cober_ospim').val();
		var monto_cober_prest = jQuery('#<portlet:namespace />monto_cober_prestadora').val();

		if (cantidad==null || trim(cantidad)=='') {cantidad=0;}		
		if (amtima==null || trim(amtima)=='') {amtima = 0;}
		if (ospim==null || trim(ospim)=='') {ospim = 0;}
		if (monto_cober_prest==null || trim(monto_cober_prest)=='') {monto_cober_prest = 0;}

		var y = (parseFloat(amtima)+parseFloat(ospim)+parseFloat(monto_cober_prest)) * parseFloat(cantidad);
		
		if(isNaN(y)) y=0;
		
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);				
	});
	jQuery('#<portlet:namespace />cantidad').change(function() {
		var cantidad=parseFloat(jQuery('#<portlet:namespace />cantidad').val());
		var amtima = jQuery('#<portlet:namespace />monto_cober_amtima').val();
		var ospim = jQuery('#<portlet:namespace />monto_cober_ospim').val();
		var precio = jQuery('#<portlet:namespace />precio').val();
		if (cantidad==null || trim(cantidad)=='') {cantidad=0;}
		if (amtima==null || trim(amtima)=='') {amtima = 0;}
		if (ospim==null || trim(ospim)=='') {ospim = 0;}
		if (precio== null || trim(precio)=='') {precio = 0;}
		var x = parseFloat(cantidad) * parseFloat(precio);
		var y = (parseFloat(amtima)+parseFloat(ospim)) * parseFloat(cantidad);		
		
		if(isNaN(y)) y=0;
		if(isNaN(x)) x=0;
		
		jQuery("#<portlet:namespace />total_med").val(Math.round(x * 100)/100);
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);		
	});*/
	
	jQuery('#<portlet:namespace />cantidad, #<portlet:namespace />monto_cober_amtima, #<portlet:namespace />monto_cober_ospim,#<portlet:namespace />monto_cober_prestadora,#<portlet:namespace />monto_cober_imesa').change(function() {
		
		//alert('test');
		var cantidad=parseFloat(jQuery('#<portlet:namespace />cantidad').val());
		var amtima = jQuery('#<portlet:namespace />monto_cober_amtima').val();
		var ospim = jQuery('#<portlet:namespace />monto_cober_ospim').val();
		var precio = jQuery('#<portlet:namespace />precio').val();
		var monto_cober_prest = jQuery('#<portlet:namespace />monto_cober_prestadora').val();
		var monto_cober_imesa = jQuery('#<portlet:namespace />monto_cober_imesa').val();

		if (cantidad==null || trim(cantidad)=='') {cantidad=0;}
		if (amtima==null || trim(amtima)=='') {amtima = 0;}
		if (ospim==null || trim(ospim)=='') {ospim = 0;}
		if (precio== null || trim(precio)=='') {precio = 0;}
		if (monto_cober_prest== null || trim(monto_cober_prest)=='') {monto_cober_prest = 0;}
		if (monto_cober_imesa== null || trim(monto_cober_imesa)=='') {monto_cober_imesa = 0;}

		var x = parseFloat(cantidad) * parseFloat(precio);
		var y = (parseFloat(amtima)+parseFloat(ospim)+parseFloat(monto_cober_prest)+parseFloat(monto_cober_imesa)) ;		
		
		if(isNaN(y)) y=0;
		if(isNaN(x)) x=0;
		
		jQuery("#<portlet:namespace />total_med").val(Math.round(x * 100)/100);
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);		
	});
	
	
	
	
	jQuery('#<portlet:namespace />porcentaje').change(function() {
		//multiplico porcentaje por precio al publico y muestro en el campo monto ospim
		var pmo = jQuery('#<portlet:namespace />pmo').val();
		<portlet:namespace />calcularMontoOspim(pmo);
	});

	jQuery('#<portlet:namespace />dividir').change(function() {		
		//multiplico porcentaje por precio al publico y muestro en el campo monto ospim
		var pmo = jQuery('#<portlet:namespace />pmo').val();
		<portlet:namespace />calcularMontoOspim(pmo);		
	});
	
	function <portlet:namespace />calcularMontoOspim(pmo) {
		var porcentaje=parseFloat(jQuery('#<portlet:namespace />porcentaje').val());
		var precio = jQuery('#<portlet:namespace />precio').val();
		if (precio==null || trim(precio)=='') {precio=0;}
		if (porcentaje== null || trim(porcentaje)=='') {porcentaje = 0;}
		var dividir = document.getElementById("<portlet:namespace />dividir");	
		var divido = dividir.checked ? 1 : 0;
		var x = 0;
		if (divido == 0) {
			x = parseFloat(precio) * parseFloat(porcentaje);
			jQuery("#<portlet:namespace />monto_cober_ospim").val(Math.round((x/100)*100)/100);			
			jQuery("#<portlet:namespace />monto_cober_amtima").val(0);
		} else {
			x = parseFloat(precio) * parseFloat(porcentaje/2);
			jQuery("#<portlet:namespace />monto_cober_ospim").val(Math.round((x/100)*100)/100);			
			jQuery("#<portlet:namespace />monto_cober_amtima").val(Math.round((x/100)*100)/100);
		}				
		var cantidad=parseFloat(jQuery('#<portlet:namespace />cantidad').val());
		var amtima = jQuery('#<portlet:namespace />monto_cober_amtima').val();
		var ospim = jQuery('#<portlet:namespace />monto_cober_ospim').val();		
		if (cantidad==null || trim(cantidad)=='') {cantidad=0;}		
		if (amtima==null || trim(amtima)=='') {amtima = 0;}
		if (ospim==null || trim(ospim)=='') {ospim = 0;}
		var y = (parseFloat(amtima)+parseFloat(ospim)) * parseFloat(cantidad);		
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);		
	}

	function <portlet:namespace />calcularMontoAmtima(pmo) {
		var porcentaje=0;
		if (pmo == 'true') {
			porcentaje = parseFloat(jQuery('#<portlet:namespace />porc_sss').val());
		} else {
			porcentaje = parseFloat(jQuery('#<portlet:namespace />porc_amtima').val());
			//alert (porcentaje);
		}
		var precio = jQuery('#<portlet:namespace />precio').val();
		if (precio==null || trim(precio)=='') {precio=0;}
		if (porcentaje== null || trim(porcentaje)=='') {porcentaje = 0;}
		if (porcentaje == '40.00' || porcentaje == '40') {
			var	x = parseFloat(precio) * parseFloat(porcentaje);
			jQuery("#<portlet:namespace />monto_cober_amtima").val(Math.round((x/100)*100)/100);	
		}		
		var cantidad=parseFloat(jQuery('#<portlet:namespace />cantidad').val());
		var amtima = jQuery('#<portlet:namespace />monto_cober_amtima').val();
		var ospim = jQuery('#<portlet:namespace />monto_cober_ospim').val();		
		if (cantidad==null || trim(cantidad)=='') {cantidad=0;}		
		if (amtima==null || trim(amtima)=='') {amtima = 0;}
		if (ospim==null || trim(ospim)=='') {ospim = 0;}
		var y = (parseFloat(amtima)+parseFloat(ospim)) * parseFloat(cantidad);		
		jQuery("#<portlet:namespace />total_cob").val(Math.round(y * 100)/100);		
	}

	function esCobertura(id_plan) {		
		if (id_plan == 2 || id_plan == 3 || id_plan == 4 || id_plan == 5 || 
				id_plan == 19 || id_plan == 20 || id_plan == 9) {
			return true;
		}
		return false;
	}
	
	function esIntegral(id_plan) {		
		if (id_plan == 1 || id_plan == 5 || id_plan == 7 || id_plan == 8 || 
				id_plan == 12 || id_plan == 21 || id_plan == 22) {
			return true;
		}
		return false;
	}
	
	 

	function <portlet:namespace />desactivaControlesPrestacionDesdeReclamo(valor) {
		
 	   if (valor) {
 		   document.getElementById("<portlet:namespace />nombre_med").disabled = "disabled";
    		   document.getElementById("<portlet:namespace />presentacion").disabled = "disabled";
    		   //document.getElementById("<portlet:namespace />codBarras").disabled = "disabled";
    		   document.getElementById("<portlet:namespace />troquel").disabled = "disabled";
    		   document.getElementById("<portlet:namespace />buscarMedicamento").disabled = "disabled";
    		   document.getElementById("<portlet:namespace />limpiarCampos").disabled = "disabled";
 	   }
 	   else{
 		   document.getElementById("<portlet:namespace />nombre_med").disabled = "";
    		   document.getElementById("<portlet:namespace />presentacion").disabled = "";
    		   //document.getElementById("<portlet:namespace />codBarras").disabled = "";
    		   document.getElementById("<portlet:namespace />troquel").disabled = "";
    		   document.getElementById("<portlet:namespace />buscarMedicamento").disabled = "";
    		   document.getElementById("<portlet:namespace />limpiarCampos").disabled = "";
    		   document.getElementById("<portlet:namespace />troquel").disabled = "";
 	   }
 	   
    }
	
	
	function validarCuitPrestador() {
	       var cuit="";
	       var params="";
	       cuit=jQuery("#<portlet:namespace />cuit_entidad").val();
	       params += "&nroCuitEmpresa="+cuit;
	       if(cuit.trim().length == 0  ){
	    	   alert('Complete la empresa');
	    	   return false;
	       }
	       
	       
	       var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/validar_cuit_prestador';
	  	   url = url + params;
	  	   jQuery.ajax({   
	  	   url: url,
	  		success: function(data) {
				var obj = jQuery.parseJSON(data);
	  			var respuesta = obj.nroCuitExisteEnAfip;
	  			jQuery('#<portlet:namespace />cuitvalidoprestador').val(respuesta);
	  			if (respuesta=="false"){
	  				alert('Cuit de Prestador no presente en informacion de AFIP.');
					return false;
	  			}
	  		}
		   }); 
	  	   
	  	   
	  	   if(jQuery("#<portlet:namespace />sucursal_entidad").val() == ""){
			  alert("<liferay-ui:message key='sucursal-obligatorio' />");
			  jQuery("#<portlet:namespace />sucursal_entidad").focus();
			  return false;
	  	   }

        return  true;    
	  	 
	}
	
	
	

	
	
	
	
	
	
	
</script>

