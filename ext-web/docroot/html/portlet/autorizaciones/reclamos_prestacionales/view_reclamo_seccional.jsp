<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include
	file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%@ page import="ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil"%>
<%
Calendar prestacionFecha = CalendarFactoryUtil.getCalendar();
String prestacionFechaString = prestacionFecha.get(Calendar.DATE)+"/"+(prestacionFecha.get(Calendar.MONTH) + 1)+"/"+prestacionFecha.get(Calendar.YEAR);

String cmd = (String) request.getAttribute(Constants.CMD);	
String caso_vinculado = String.valueOf(request.getAttribute("caso_vinculado")!=null?request.getAttribute("caso_vinculado"):0);
String cuit_titular_vinculado="";
int inte_vinculado=0;
boolean reclamo_vinculado =false;
boolean esEdicion = false;
boolean esAlta = false;
int cantprestacioneslista=0;
ReclamoPrestacional  reclamoprestacional  = (ReclamoPrestacional)request.getSession().getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);

//cantprestacioneslista= reclamoprestacional !=null && reclamoprestacional.getPrestaciones() != null ? reclamoprestacional.getPrestaciones().size() :0;

if (cantprestacioneslista == 0){	
	//List<PrestacionesReclamo> prestaciones = (List<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);				
	if (reclamoprestacional != null && !reclamoprestacional.getPrestaciones().isEmpty() ){
		//cantprestacioneslista = prestaciones.size();
		for (int i = 0; i < reclamoprestacional.getPrestaciones().size(); i++) {	
			PrestacionesReclamo presreclamo  = (PrestacionesReclamo) reclamoprestacional.getPrestaciones().get(i);
			if (presreclamo.getEstado() == null || !presreclamo.getEstado().equals(PrestacionesReclamo.ESTADOS.BAJA)){
					cantprestacioneslista = cantprestacioneslista + 1;
			}
		}
	}
}


Calendar fechadia  =Calendar.getInstance(); 		
Calendar fechaospim  = Calendar.getInstance();	
Calendar fechacierre  = Calendar.getInstance();
Calendar fechaseccional  = Calendar.getInstance();
Calendar fecharevision = Calendar.getInstance();
String tabValue = ParamUtil.getString(request, "tab", null); // "datos"
boolean nofechaseccional=true;

fechadia.setTime(new Date());
//obtengo lista de session
List<CieDiez> cieDiez=(ArrayList<CieDiez>) request.getSession().getAttribute(WebKeysGlobal.DOCUMENTOS_CIE);


String divcheckbox="";
String nroreclamo="Caso Nro 00000";
String opAsignadaalReclamo ="";
ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO resolucionAutorizado=ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINVALOR;
divcheckbox="divheadercheckboxs";
String edit = "";

if (cmd != null && (cmd.equalsIgnoreCase(Constants.EDIT) || cmd.equalsIgnoreCase(Constants.ADD)) ){
    esEdicion = true;	
}
// Si es Alta, fuerza el SET de estado a Pre-Carga
if (cmd.equalsIgnoreCase(Constants.ADD)) {
	esAlta = true;
	//reclamoprestacional.setEstado(0);
}

if(reclamoprestacional != null  ){
	nroreclamo ="Reclamo Nro : " + "000"+  String.valueOf(reclamoprestacional.getId_reclamo());
	Date fechaaux = null;
	divcheckbox="divcheckboxsEdicion";	
	fechaaux = Validator.isNotNull(reclamoprestacional)? reclamoprestacional.getAlta_fecha() : null;
	if (fechaaux != null) {
		fechaospim.setTime(reclamoprestacional.getAlta_fecha());	
	}
	// obtiene el estado de la autorizacion de las prestaciones 
	resolucionAutorizado= reclamoprestacional.getEstadoResolucionAutorizada();	
	
	fechaaux = Validator.isNotNull(reclamoprestacional)? reclamoprestacional.getFecha_cierre()   : null;
	if (fechaaux != null) {
		fechacierre.setTime(reclamoprestacional.getFecha_cierre());
		
	}
	
	fechaaux = Validator.isNotNull(reclamoprestacional)? reclamoprestacional.getSeccional_fecha()  : null;
	if (fechaaux != null) {
		fechaseccional.setTime(reclamoprestacional.getSeccional_fecha());
		nofechaseccional=false; 
	}
	
	
	
}else{
	
if (Integer.parseInt(caso_vinculado)>0 ){// carga datos del afiliado del cas 
		ReclamoPrestacional reclamoprestacional1 = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(Integer.parseInt(caso_vinculado));
		cuit_titular_vinculado = reclamoprestacional1.getCuit_titular();
		inte_vinculado = reclamoprestacional1.getInte();
		reclamo_vinculado=true;
	}
}

Integer idPreautorizacion=0;
try{
	idPreautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorIdReclamo(reclamoprestacional.getId_reclamo(),null);
}catch(Exception e){}
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_REABRIR_RECLAMO_PRESTACIONAL  );				



%>
<style>
div.divheadercheckboxs {
	/*
  position: absolute;
  top: 335px;
  right:30;
  left:1020px;
 */
	background-color: #f2f2f2;
	width: 170px;
	height: 150px;
	border: 1px solid black;
}

div.divcheckboxsEdicion {
	/*
  position: absolute;
  top: 335px;
  right:30;
  left:1050px;
*/
	background-color: #f2f2f2;
	width: 170px;
	height: 150px;
	border: 1px solid black;
}

div.divheaderNroReclamo {
	/*
  position: absolute;
  top: 270px;
  right:30;
  left:1000px;
*/
	background-color: #cccccc;
	width: 200px;
	height: 20px;
	border: 1px solid black;
	font-size: 145%
}

div.divheaderNroOP {
	/*
  position: absolute;
  top: 300px;
  right:30;
  left:1000px;
*/
	width: 200px;
	height: 18px;
	border: 1px solid black;
	font-size: 100%
}

div.divNroRecord_Vinculado {
	/*
  position: absolute;
  top: 238px;
  right:30;
  left:625px;
*/
	/*background-color: #f2f2f2;*/
	width: 200px;
	height: 20px;
	/*  border:1px solid black;*/
	font-size: 120%
}

span-fixed-size {
  display: inline-block;
  width: 20px;
}

</style>

<liferay-ui:error key="errorAfiliadoSinCobertMed"
		message="<%=(String)request.getAttribute(\"msgErrorAfiSinCobMed\") %>" />
		
<!-- form name="<portlet:namespace />reclamo_fm" id="<portlet:namespace />reclamo_fm"-->

	<input type="hidden" id="<portlet:namespace />fprest"
		name="<portlet:namespace />fprest" value="<%=prestacionFechaString%>" />
	<input type="hidden" id="<portlet:namespace />posforcie10"
		name="<portlet:namespace />posforcie10" value="0" /> <input
		type="hidden" id="<portlet:namespace />codigoCie10"
		name="<portlet:namespace />codigoCie10"
		value="<%=Validator.isNotNull(reclamoprestacional) && Validator.isNotNull(reclamoprestacional.getCodigoCie10())   ? reclamoprestacional.getCodigoCie10()   : ""  %>" />
	<input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>"
		value="<%=cmd%>" /> <input type="hidden"
		name="<portlet:namespace />cantprestacioneslista"
		id="<portlet:namespace />cantprestacioneslista"
		value="<%=cantprestacioneslista%>" /> <input type="hidden"
		name="<portlet:namespace />cuiltitular"
		id="<portlet:namespace />cuiltitular" /> <input type="hidden"
		name="<portlet:namespace />intetitular"
		id="<portlet:namespace />intetitular" /> <input type="hidden"
		name="<portlet:namespace />cantrevisionesactivas"
		id="<portlet:namespace />cantrevisionesactivas" value="" /> <input
		type="hidden" id="<portlet:namespace />id_reclamosel"
		name="<portlet:namespace />id_reclamosel" size="8"
		value="<%=Validator.isNotNull(reclamoprestacional)  ? reclamoprestacional.getId_reclamo()  : "0"  %>" />
	<input type="hidden" id="<portlet:namespace />tipoaccionprestacion"
		name="<portlet:namespace />tipoaccionprestacion" size="8" value='0' />
	<input type="hidden" id="<portlet:namespace />evaluacionreclamo"
		name="<portlet:namespace />evaluacionreclamo" size="8"
		value="<%=resolucionAutorizado%>" /> <input type="hidden"
		id="<portlet:namespace />auditoriaadministrativa"
		name="<portlet:namespace />auditoriaadministrativa" size="8"
		value="<%=resolucionAutorizado%>" /> <input type="hidden"
		id="<portlet:namespace />montoPsPrestaciones"
		name="<portlet:namespace />montoPsPrestaciones" size="8" value='0' />
	<input type="hidden"
		id="<portlet:namespace />tipoNomencladorSeguimiento_filtro"
		name="<portlet:namespace />tipoNomencladorSeguimiento_filtro" size="8"
		value='0' />
		
		
		
	
	<div id="helpComprobantes" class="containerPlus draggable {buttons:'c', skin:'default', width:'700',title:'Ayuda',closed:'true'}" style="top: 500px; left: 200px">
			Ejemplo de factura: CUIT 30999999999 FCP B 00001 - 00000028 F. Emisión 2/07/2020 <br>
			Comprobante: FCP,  <br>
			Letra: B,  <br>
			Suc.: 00001 ( o sólo el número 1 y el sistema autocompletará los 0 a izquierda al guardar)  <br>
			Nro: 00000028 ( o sólo el número 28 y el sistema autocompletará los 0 a izquierda al guardar)  <br>
			CUIT: 30999999999
	</div>
														

	<fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="Cabecera Caso" />
		</legend>

		<!-- DS -->
		<table>
			<tr>
				<td>
					<!-- DS -->
					<table class="lfr-table"
						style="border-collapse: separate; border-spacing: 3px;">

						<tr>
							<td><label><liferay-ui:message key="Fecha Seccional" />:</label></td>
							<%if(reclamoprestacional == null || nofechaseccional) {%>
							<td colspan="1"><liferay-ui:input-date
									dayParam="fechaseccionalDia"
									dayValue="<%= fechaseccional.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaseccionalMes"
									monthValue="<%= fechaseccional.get(Calendar.MONTH) %>"
									monthNullable="<%= true %>" yearParam="fechaseccionalAnio"
									yearValue="<%= fechaseccional.get(Calendar.YEAR) %>"
									yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-2  %>"
									yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)  %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%}else{ %>
							<td colspan="1"><liferay-ui:input-date
									dayParam="fechaseccionalDia"
									dayValue="<%= fechaseccional.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaseccionalMes"
									monthValue="<%= fechaseccional.get(Calendar.MONTH)%>"
									monthNullable="<%= true %>" yearParam="fechaseccionalAnio"
									yearValue="<%= fechaseccional.get(Calendar.YEAR )%>"
									yearRangeStart="<%= fechaseccional.get(Calendar.YEAR) - 2 %>"
									yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR) + 1 %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%} %>

							<td><label><liferay-ui:message key="Tipo Pedido" />
									:</label></td>
							<td><table>
									<select <% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />tipopedido"
										id="<portlet:namespace />tipopedido">
										<option value="REINTEGRO"
											<%=Validator.isNotNull(reclamoprestacional)  && Validator.isNotNull(reclamoprestacional.getTipoPedido())  &&  reclamoprestacional.getTipoPedido().equals("REINTEGRO") ? "selected" : ""  %>>REINTEGRO</option>
									</select>
								</table></td>

							<td><label><liferay-ui:message key="Sector" /> :</label></td>
							<td><table>
									<select <% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace/>sector"
										id="<portlet:namespace />sector"
										onchange="manejarTipoSector();">
										<option value="SELECCIONAR">SELECCIONAR</option>
										<option value="DISCAPACIDAD"
										    <%=Validator.isNotNull(reclamoprestacional) && "DISCAPACIDAD".equals(reclamoprestacional.getSector()) ? "selected" : ""  %>>DISCAPACIDAD</option>
										<option value="PRESTACIONES MEDICAS"
										    <%=Validator.isNotNull(reclamoprestacional) && "PRESTACIONES MEDICAS".equals(reclamoprestacional.getSector()) ? "selected" : ""  %>>PRESTACIONES MÉDICAS</option>
										<option value="FARMACIA"
										    <%=Validator.isNotNull(reclamoprestacional) && "FARMACIA".equals(reclamoprestacional.getSector()) ? "selected" : ""  %>>FARMACIA</option>
										<option value="ODONTOLOGIA"
										    <%=Validator.isNotNull(reclamoprestacional) && "ODONTOLOGIA".equals(reclamoprestacional.getSector()) ? "selected" : ""  %>>ODONTOLOGIA</option>																				
									</select>
								</table></td>

							<td><label><liferay-ui:message key="Estado" /> :</label></td>
							<td><table>
									<tr>
										<td><select <% if (!esEdicion) { %> disabled='disabled'
											<%}%> name="<portlet:namespace/>estado"
											onchange="controlarEstadoCerrado();"
											id="<portlet:namespace/>estado">

												<% for (EstadosReclamosPrestacionales estados : listaestados) { %>
													<!-- Si es Alta, muestra solo estado PRECARGA -->									
													<%if (esAlta){ %>
													
														<%if ((estados.getId() == 0)){ %>
															<option
																<%= reclamoprestacional != null  && reclamoprestacional.getEstado() == estados.getId() ? "selected" : ""  %>
																value="<%= estados.getId() %>"><%=estados.getDescripcion()%>
															</option>
														<%} %>
																										
													<%} else { %> 
													<!-- Si es Edicion, muestra estado PRECARGA y OBSERVADO -->													
													
														<%if ((estados.getId() == 0) || (estados.getId() == 5) || (estados.getId() == 6)){ %>
															<option
																<%= reclamoprestacional != null  && reclamoprestacional.getEstado() == estados.getId() ? "selected" : ""  %>
																value="<%= estados.getId() %>"><%=estados.getDescripcion()%>
															</option>
														<%} %>
												
													<%} %>
												<%} %>
										</select></td>

									</tr>
								</table></td>
																															
							<td colspan="10">&nbsp;</td>
							<td colspan="10">&nbsp;</td>

							<td>
								<div class="divheaderNroReclamo">
									<label><b><liferay-ui:message
												key="<%= nroreclamo %>" /></b></label>
								</div>
							</td>
						</tr>

					<%
					if (reclamoprestacional != null && reclamoprestacional.getEstadoObservacion() != null && reclamoprestacional.getEstadoObservacion().length() >0 ) {						
					%>										
						<tr>
							<td colspan="12">
								<fieldset class="block-labels">
									<legend>
										<liferay-ui:message key="observacion" />
									</legend>
									<table>
										<tr>
											<td><span class="span-fixed-size"
												id="<portlet:namespace />estadoObservacion"
												style="color: red;"> <%=reclamoprestacional != null && reclamoprestacional.getEstadoObservacion() != null
							? reclamoprestacional.getEstadoObservacion() : ""%>
											</span></td>
										</tr>
									</table>
	
								</fieldset>
							</td>
						</tr>
						<%
						}
						%>
					<table class="lfr-table">	
					<tr>
							<td colspan="12">
								<fieldset class="block-labels">
									<legend>
										<liferay-ui:message key="datos-afiliado" />
									</legend>
									<liferay-util:include
										page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>

										<liferay-util:param name="edit_mode"
											value="<%=String.valueOf(esEdicion) %>" />
										<liferay-util:param name="discapacidad" value="<%= null %>" />
										<liferay-util:param name="pag_reintegro"
											value="<%= String.valueOf(true) %>" />
										<liferay-util:param name="from_reclamo" value="true" />

										<% if ( reclamo_vinculado   ) { %>
										<liferay-util:param name="cuil"
											value="<%= String.valueOf(cuit_titular_vinculado) %>" />
										<liferay-util:param name="inte"
											value="<%= String.valueOf(inte_vinculado) %>" />
										<liferay-util:param name="origen" value="" />

										<%}else{ %>
										<liferay-util:param name="cuil"
											value="<%=reclamoprestacional!=null?reclamoprestacional.getCuit_titular():null%>" />
										<liferay-util:param name="inte"
											value="<%=reclamoprestacional!=null?String.valueOf(reclamoprestacional.getInte()):null%>" />
										<liferay-util:param name="origen" value="" />
										<%} %>

									</liferay-util:include>
								</fieldset>
							</td>	
							
							<td>
				  <fieldset class="block-labels seccionVerificarDomicilio" id="<portlet:namespace />seccionVerificarDomicilio">
				      <table>
				       <tr>
				          <td>&nbsp;</td>
			           </tr>   
				       <tr>		
			             <td><label><liferay-ui:message key="contacto-verif-domi" />:</label></td>
			            </tr>
			            <tr>
				          <td>&nbsp;</td>
			            </tr>
			            <tr> 
			             <td>
				            <div id="<portlet:namespace />divBotonActualizar">
				               <%if(esEdicion){ %>
					            <input type="button" value="Actualizar" 
					    	     onclick="javascript:mostrarDomicilioAfiliado();"
					    	    />
					    	    <%} %>
				            </div>
				          </td>  
				        </tr>
				            
				        <tr>   
				         <td> 
				            <div id="<portlet:namespace />divResultadoActualizarOK">
					           <p><b><liferay-ui:message key="crm-actualiza-domicilio"/></b></p>
				            </div>
			             </td>
			            </tr> 
			            <tr>
				          <td>&nbsp;</td>
			            </tr>  
			         </table>
			       </fieldset>
				</td>
									
						</tr>
						</table>
					</table>

				</td>
				<td></td>

			</tr>
		</table>


	</fieldset>

	<br>

	<fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="Datos de la Prestación" />
		</legend>


		<div id="<portlet:namespace />datos_edicion_prestacion">

			<span><b>Prestaci&oacute;n en Proceso de Edici&oacute;n.</b></span>
			<liferay-util:include
				page="/html/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp">
			</liferay-util:include>
		</div>

		<div id="<portlet:namespace />datos_prestacion_ingreso">


			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 2px; width: 100%;">
				<tr>
					<td colspan="15">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="Datos del Comprobante" />
							</legend>
							<table>
								<tr>
									<td colspan="10">
										<table class="lfr-table"
											style="border-collapse: separate; border-spacing: 3px;">
											<tr>
												<td><label><liferay-ui:message
															key="comprobante" />:</label></td>
												<td><select name="<portlet:namespace/>comprobante_tipo"
													id="<portlet:namespace/>comprobante_tipo"
													<% if (!esEdicion) { %> disabled="disabled" <%} %>>
														<option value="FCP">FCP</option>
														<option value="RCB">RCB</option>
														<option value="OTR">OTRO</option>
														<!-- <option value="AUT">AUTORIZACION</option> -->
												</select></td>

												<td><label><liferay-ui:message key="letra" />:</label></td>
												<td colspan="3"><select
													name="<portlet:namespace/>comprobante_letra"
													id="<portlet:namespace/>comprobante_letra">
														<option value=""></option>
														<option value="A">A</option>
														<option value="B">B</option>
														<option value="C">C</option>
												</select></td>
												<td>Suc:</td>
												<td><input id="<portlet:namespace />comprobante_suc"
													name="<portlet:namespace />comprobante_suc" size="5"
													onkeydown="allowOnlyDigits(event);"
													maxlength="5" type="text" value="" <% if (!esEdicion) { %>
													readonly="readonly" <%} %> /></td>


												<td>Nro:</td>
												<td><input id="<portlet:namespace />comprobante_nro"
													name="<portlet:namespace />comprobante_nro" size="9"
													maxlength="8" type="text" onkeydown="allowOnlyDigits(event);"
													value="" <% if (!esEdicion) { %>
													readonly="readonly" <%} %> /></td>
												<td><label>F. Emisi&oacute;n: </label></td>
												<td colspan="3"><liferay-ui:input-date
														dayParam="fechaComprobanteDia" dayValue=""
														dayNullable="<%=true %>" monthParam="fechaComprobanteMes"
														monthValue="-1" monthNullable="<%= true %>"
														yearParam="fechaComprobanteAnio" yearValue=""
														yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-2 %>"
														yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)  %>"
														yearNullable="<%= true %>"
														firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
														disabled="<%= !esEdicion %>" /></td>
														
													   
										       <td>
										         <a href="javascript:void(0)" onclick="help(event, 'helpComprobantes')"><img style="height: 25px; width: 25px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
										        </td>															
													
											</tr>
											
											 
											
										</table>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<!-- div style="width:75%;"-->
									<td colspan="7"><liferay-util:include
											page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
											<liferay-util:param name="esEditable"
												value='<%= String.valueOf(esEdicion) %>' />
											<liferay-util:param name="cuit" value='' />
											<liferay-util:param name="sucu" value='' />
											<liferay-util:param name="razon" value='' />
											<liferay-util:param name="id_seccional" value='' />
											<liferay-util:param name="esEmpresaPrestador" value='true' />
											<liferay-util:param name="suf_entidad" value='_' />
										</liferay-util:include></td>
									<!-- /div-->
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td colspan="70"><label>Mantener comprobante
											seleccionado:</label></td>
									<td><input type="checkbox"
										name="<portlet:namespace />mantenerComprobante"
										id="<portlet:namespace />mantenerComprobante"></td>

								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td>

										<div style="width: 120%;"
											id="<portlet:namespace />busqueda_prestaciones">
											<table class="lfr-table"
												style="border-collapse: separate; border-spacing: 3px;">
												<tr>
													<td colspan="15"><label>F. Prestación: </label></td>
													<td colspan="13"><liferay-ui:input-date
															dayParam="fechaPrestacionDia" dayValue=""
															dayNullable="<%=true%>" monthParam="fechaPrestacionMes"
															monthValue="-1" monthNullable="<%=true%>"
															yearParam="fechaPrestacionAnio" yearValue=""
															yearNullable="<%=true%>"
															yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 2%>"
															yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)%>"
															firstDayOfWeek="" disabled="<%= !esEdicion %>" /></td>
												
													<td><label><liferay-ui:message
																key="codigo-presentado" />:</label></td>
													<td><input
														id="<portlet:namespace />codigoSeguimiento_filtro"
														name="<portlet:namespace />codigoSeguimiento_filtro"
														size="10" maxlength="20" type="text" value='' /></td>
													<td><input
														id="<portlet:namespace />descripcionSeguimiento_filtro"
														name="<portlet:namespace />descripcionSeguimiento_filtro"
														size="60" maxlength="200" type="text" value='' /></td>
													<td><div style="width: 4%;"
															id="<portlet:namespace />divBtnBusca">
															<a href="javascript: void(0);"
																onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();"
																tabindex="-1">Buscar</a> <a href="javascript: void(0);"
																onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();"
																tabindex="-1">Limpiar</a>
														</div>
												
												</tr>
												<tr>
													<td>&nbsp;</td>
												</tr>
											</table>
										</div>



										<div style="width: 920%;"
											id="<portlet:namespace />busqueda_farmacia">
											<table class="lfr-table"
												style="border-collapse: separate; border-spacing: 3px;">
												<tr>
												
													<td colspan="15"><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;F.
															Prestación: </label></td>
													<td colspan="15"><liferay-ui:input-date
															dayParam="fechaPrestacionDiaFarmacia" dayValue=""
															dayNullable="<%=true%>"
															monthParam="fechaPrestacionMesFarmacia" monthValue="-1"
															monthNullable="<%=true%>"
															yearParam="fechaPrestacionAnioFarmacia" yearValue=""
															yearNullable="<%=true%>"
															yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 2%>"
															yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)%>"
															firstDayOfWeek="" disabled="<%= !esEdicion %>" /></td>
												<td colspan="6">
													<liferay-util:include page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
															<liferay-util:param name="search_url"
																value="/autorizaciones/buscar_medicamentos" />
															<liferay-util:param name="troquel" value='' />
															<liferay-util:param name="nombre_medicamento" value='' />
															<liferay-util:param name="id_medicamento" value='' />
															<liferay-util:param name="esEditable" value='true' />
															<liferay-util:param name="mostrar_con_presentacion" value='true' />
														</liferay-util:include>
													</td>
												</tr>
												<tr>
													<td>&nbsp;</td>
												</tr>
											</table>
										</div>
										<table>
											<tr>
												<td><label><liferay-ui:message key="Cantidad" />:</label>
												</td>
												<td><input id="<portlet:namespace />cantidadFC"
													<% if (!esEdicion) { %> disabled='disabled' <%}%>
													name="<portlet:namespace />cantidadFC" size="8"
													maxlength="9" type="text" value="1"
													onblur="calculatotalFC()" /></td>

												<td><label><liferay-ui:message key="Importe" />:</label></td>
												<td><input id="<portlet:namespace />importeUnitarioFC"
													<% if (!esEdicion) { %> disabled='disabled' <%}%>
													name="<portlet:namespace />importeUnitarioFC" size="8"
													maxlength="13" value='' type="text"
													onkeydown="allowOnlyDigitsAndDecimals(event)"
													onblur="calculatotalFC()" /></td>


												<td><label>Importe prestación:</label></td>
												<td><input id="<portlet:namespace />importeFC"
													<% if (!esEdicion) { %> disabled='disabled' <%}%>
													name="<portlet:namespace />importeFC" size="8"
													maxlength="13" value='' type="text"
													onkeydown="allowOnlyDigitsAndDecimals(event)"
													readonly="readonly" /></td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
										</table>
							</table>
						</fieldset>
			</table>
	</fieldset>

		
		
	<div id="<portlet:namespace />datos_prestacion_ingreso_obs">


	<table>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td colspan="8"><liferay-ui:message key="observacion" />:</td>
			<td><textarea rows="3" cols="100" <% if (!esEdicion) { %>
					disabled='disabled' <%}%>
					id="<portlet:namespace />observacion_prestacion" maxlength="250"
					name="<portlet:namespace />observacion_prestacion"></textarea> <br>
				<b><liferay-ui:message
						key="La observación de 200 caracteres como máximo." /></b></td>
			<td colspan="12">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<%if(esEdicion) {%>
			<td><input type="button"
				value="<liferay-ui:message key="add-prestacion-pre-carga" />"
				onClick="<portlet:namespace />agregarPrestacion();"
				id="<portlet:namespace />buttonaddprestacion"
				name="<portlet:namespace />buttonaddprestacion"
				title="<liferay-ui:message key="add-prestacion-reclamo" />" /> <% }%>
		</tr>
	</table>

	</div>

	<table>
		<tr>
			<td><span
				id="<portlet:namespace />CantidadDePrestacionesDelReclamo"
				style="color: red;"></span></td>
		</tr>
	</table>


	<div id="<portlet:namespace />lista_prestaciones_reclamos"
		align="center"
		style="height: 160px; overflow: scroll; overflow-x: hidden;">
		<table>
			<tr>
				<td colspan="10"><liferay-util:include
						page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamos_seccional.jsp">
					</liferay-util:include></td>
			</tr>
		</table>
	</div>




	<br />


	<%if(reclamoprestacional!=null) {%>
	<div id="<portlet:namespace />botoneditareclamo" align="center"
		style="height: 80px; overflow-x: hidden;">
		<table>
			<tr>
				<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.EDIT)) {%>
				<td><input type="button"
					value="<liferay-ui:message key="Actualizar" />"
					onClick="<portlet:namespace />editaReclamo(false);"
					title="<liferay-ui:message key="Actualiza los Datos Ingresados." />" />
				</td>
				<%} %>
			
				<td><input type="button"
					value="<liferay-ui:message key="next" />"
					onClick="<portlet:namespace />siguienteSolapa();" /></td>
			
				<td>
				
					<input id="<portlet:namespace />eMail"
					value="<liferay-ui:message key="email-large"/>"
					title="<liferay-ui:message key="email-large" />"
					onClick="javascript: <portlet:namespace />emailPreCarga(<%=reclamoprestacional.getId_String()%>);"
					type="button" /> 
					<%if (reclamoprestacional != null && reclamoprestacional.getId_reclamo() != 0 && 
						 reclamoprestacional.getFechaMailSeccional() != null)  { %>
				
							<td><label><font size=3 color="#0000ff">&nbsp;&nbsp;
										Email enviado <%=sdf.format(reclamoprestacional.getFechaMailSeccional())%></font></label>
							</td>
							<td colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					 <%}%>
				</td>

			</tr>
		</table>
	</div>
	<%}%>
	<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.ADD)){ %>
	<div id="<portlet:namespace />botonsavereclamo" align="center"
		style="height: 80px; overflow-x: hidden;">
		<table>
			<tr>
				<td><input type="button"
					value="<liferay-ui:message key="Grabar" />"
					onClick="<portlet:namespace />saveReclamo();"
					title="<liferay-ui:message key="Graba los Datos Ingresados." />" />
				</td>
			</tr>
		</table>
	</div>
	<%} %>
	</fieldset>


	<input type="hidden" name="<portlet:namespace />codigoprestacion"
		id="<portlet:namespace />codigoprestacion"
		value="<%=Validator.isNotNull(reclamoprestacional)  ? reclamoprestacional.getEstado() : "0"  %>" />
	<input type="hidden" name="<portlet:namespace />tipogestion"
		id="<portlet:namespace />tipogestion" value="" /> <input
		type="hidden" name="<portlet:namespace />idreclamoprestacion"
		id="<portlet:namespace />idreclamoprestacion" value="" /> <input
		type="hidden" name="<portlet:namespace />consultareclamo"
		id="<portlet:namespace />consultareclamo"
		value="<%=esEdicion  ? true  : false  %>" /> <input type="hidden"
		id="<portlet:namespace />nom_seleccionado"
		name="<portlet:namespace />nom_seleccionado" value="" /> <input
		type="hidden" name="<portlet:namespace />caso_vinculado"
		id="<portlet:namespace />caso_vinculado" value="" /> <input
		id="<portlet:namespace />tipoNomenclador"
		name="<portlet:namespace />tipoNomenclador" type="hidden" value="" />

	<div id='validarExistenciaCuit' style="float: right;"></div>

<!-- /form-->

<script type="text/javascript">

jQuery('#<portlet:namespace />divResultadoActualizarOK').hide();

var popupMD;

jQuery('#<portlet:namespace />cantprestacioneslista').val('<%=cantprestacioneslista%>');
jQuery("#<portlet:namespace />busqueda_prestaciones").hide();
jQuery("#<portlet:namespace />busqueda_farmacia").hide();
jQuery("#<portlet:namespace />datos_edicion_prestacion").hide();
jQuery("#<portlet:namespace />Cierre_Reclamo_Div").hide();
/* jQuery("#<portlet:namespace />botoneditareclamo").hide(); */
jQuery("#<portlet:namespace />lista_prestaciones_asociadas").hide();
jQuery("#<portlet:namespace />lista_contactos_reclamo").hide();
jQuery("#<portlet:namespace />justificacion_medica_reclamo").hide();
jQuery("#<portlet:namespace />caso_vinculado").val(<%=caso_vinculado%>);

var addprestacion = false;
var load = false;
var sectorIni = '';

function <portlet:namespace />actualizarAfiliadoPorFecha(diaId, mesId, anioId) {
	var diaPrest = jQuery("#<portlet:namespace />" + diaId).val();
	var mesPrest = jQuery("#<portlet:namespace />" + mesId).val();
	var anioPrest = jQuery("#<portlet:namespace />" + anioId).val();

	if (diaPrest == "" || mesPrest == "" || anioPrest == "" || mesPrest == "-1") {
		return;
	}

	var mesReal = parseInt(mesPrest, 10) + 1;
	var fechaPrestacion = diaPrest + "/" + mesReal + "/" + anioPrest;

	jQuery("#<portlet:namespace />fprest").val(fechaPrestacion);

	var cuil = jQuery("#<portlet:namespace />cuil").val();
	var inte = jQuery("#<portlet:namespace />inte").val();

	if (cuil != "" && inte != "") {
		<portlet:namespace />buscarAfiliados_(fechaPrestacion);
	}
}

function <portlet:namespace />actualizarFechaPrestacionAfiliado() {
	<portlet:namespace />actualizarAfiliadoPorFecha(
		"fechaPrestacionDia",
		"fechaPrestacionMes",
		"fechaPrestacionAnio"
	);
}

function <portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado() {
	<portlet:namespace />actualizarAfiliadoPorFecha(
		"fechaPrestacionDiaFarmacia",
		"fechaPrestacionMesFarmacia",
		"fechaPrestacionAnioFarmacia"
	);
}

function <portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion() {
	<portlet:namespace />actualizarAfiliadoPorFecha(
		"fechaPrestacionDiaEdicion",
		"fechaPrestacionMesEdicion",
		"fechaPrestacionAnioEdicion"
	);
}


// Alta / carga normal
jQuery("#<portlet:namespace />fechaPrestacionDia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionMes").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionAnio").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});


// Alta / farmacia
jQuery("#<portlet:namespace />fechaPrestacionDiaFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionMesFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionAnioFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});


// Edición
jQuery(document).on("change", "#<portlet:namespace />fechaPrestacionDiaEdicion", function(){
	<portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion();
});

jQuery(document).on("change", "#<portlet:namespace />fechaPrestacionMesEdicion", function(){
	<portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion();
});

jQuery(document).on("change", "#<portlet:namespace />fechaPrestacionAnioEdicion", function(){
	<portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion();
});


jQuery(document).ready(function() {
	load = true;
	sectorIni = jQuery("#<portlet:namespace />sector").val();
});

jQuery("#<portlet:namespace />sector").change(function(){
	
	try {	
   		var valor=jQuery('#<portlet:namespace />cantprestacioneslista').val();

   		
		if (valor >= 1 && load == true){
			
	        var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
			
			var confirmar = false;
			confirmar=confirm ('Se eliminaran los ítems por no pertenecer al tipo correspondiente '+'\nDesea hacerlo?');
			if(confirmar){
				 var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/borrar_reclamosprestaciones_todos';
    			 url = url + params;
    			jQuery("#<portlet:namespace />lista_prestaciones_reclamos").load(url);	
			}else{
				jQuery("#<portlet:namespace />sector option[value="+sectorIni+"]").attr("selected",true);
			}	
			
		}
   		
	}
	catch (err) {
		alert('error manejarTipoSector ');
	}

});





/* var data=jQuery('#<portlet:namespace/>estado').val();
document.getElementById("<portlet:namespace />estadosel").value = data; */

jQuery("#<portlet:namespace />idreclamoprestacion").val("0");
<% if(reclamoprestacional != null) {%>
jQuery("#<portlet:namespace />idreclamoprestacion").val(<%=reclamoprestacional.getId_reclamo() %>);
/* jQuery("#<portlet:namespace />botoneditareclamo").show(); */
jQuery("#<portlet:namespace />botonsavereclamo").hide();
      <% if(reclamoprestacional.getEstado()==3 ) {%>            
            jQuery("#<portlet:namespace />Cierre_Reclamo_Div").show();
            jQuery("#<portlet:namespace />botonrevision").hide();
      <%}%>      
manejarTipoPedidoCierre();      
manejarTipoSector();

<%if( resolucionAutorizado!=ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINVALOR && resolucionAutorizado!=ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINEVALUACION) {%> 
	// oculta boton de agregar porque existe una evaluacion de rECHAZO o APROBACION no de baja
	jQuery("#<portlet:namespace />botonrevision").hide();
	jQuery("#<portlet:namespace/>mensajerevisionefectuada").html("Revision Efectuada, el Sistema soporta solo una revision activa (No de baja).");
<%}%> 

<%}%>


<% if(!esEdicion) {%>
    /* jQuery("#<portlet:namespace />botoneditareclamo").hide();   */  
    /* document.getElementById("<portlet:namespace />sector").disabled = "disabled"; */
    
    document.getElementById("<portlet:namespace />reclamo_observacion_cierre").disabled = "disabled";
    
    jQuery("#<portlet:namespace />botonrevision").hide();
    jQuery("#<portlet:namespace />buttonaddprestacion").hide();    
    
    //document.getElementById("<portlet:namespace />buscadorcie10buscador").disabled = "disabled";
     
    
    
<%}%>





aplicaEstiloBordeRojoDatosObligatorio();


// Esta funcion no se utiliza (?)
function <portlet:namespace />buscarNomencladorAutocompletar_edit(){
	var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro_edit").val();
	var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro_edit").val();
    //var tipoNomenclador=jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro_edit").val();
 	// Marca ReinLiq no se utiliza en esta busqueda 
    tipoNomenclador = '';   
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
    	
    	if(tipoNomenclador==8){
    		marcaReinliq=6;
    	}

    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
    	
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    jQuery(popupMD).load(url);
    }
}

function <portlet:namespace />buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val();
	var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val();
    var tipoNomenclador=jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val();
    var marcaReinliq=null;
    
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
    	
    	if(tipoNomenclador==8){
    		marcaReinliq=6;
    	}
    	
    	// Buscador de Nomenclador de ODONTOLOGIA
    	if (tipoNomenclador==1){
    		marcaReinliq=3;
    	}
        	
    	var esPrestMed = 0;
    	sector = jQuery("#<portlet:namespace />sector").val();
    	if (sector == "PRESTACIONES MEDICAS")
    		esPrestMed = 1;
    	
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    url += '&esPrestMed='+esPrestMed;
	    jQuery(popupMD).load(url);
    }
}

function <portlet:namespace />limpiarNomencladorAutocompletar(){	
	jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val('');
	jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val('');
	jQuery("#<portlet:namespace />descripcionSeguimiento_filtro_edit").val('');
	jQuery("#<portlet:namespace />codigoSeguimiento_filtro_edit").val('');
}

 
 


function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
	jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val(codigo);
	jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val(descripcion);
	jQuery("#<portlet:namespace />nom_seleccionado").val("1"); // selecciona el tipo de nomenclador	 
	jQuery('#<portlet:namespace />tipoNomenclador').val(tipoNomenclador);
	
	jQuery('#<portlet:namespace />codigoSeguimiento_filtro_edit').val(codigo);
	jQuery("#<portlet:namespace />descripcionSeguimiento_filtro_edit").val(descripcion);
	jQuery("#<portlet:namespace />nom_seleccionado_edit").val("1"); // selecciona el tipo de nomenclador	 
	jQuery('#<portlet:namespace />tipoNomenclador_edit').val(tipoNomenclador);
	
	Liferay.Popup.close(popupMD);

}


function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
	seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
    <portlet:namespace />cerrarNm();
}


function <portlet:namespace />cerrarNm(){
	<portlet:namespace />cerrarDivNm();
	if(popupMD){
		Liferay.Popup.close(popupMD);
	}
}



function DatosRevisionOk(){
	 
	var dianro  = jQuery("#<portlet:namespace />fecharevisionDia").val();
	var mesnro  = jQuery("#<portlet:namespace />fecharevisionMes").val()  ;
	var anionro   = jQuery("#<portlet:namespace />fecharevisionAnio").val();
	 
	  
	if (dia || mes || anio){
	   alert("Debe ingresar la fecha de Revisi\u00F3n");
		return false ;
	}
	if (dia || mes || anio ||  jQuery('#<portlet:namespace />resolucion').val()=='' ){
		   alert("Debe ingresar la resoluci\u00F3n");
			return false ;
		}
		
	var resolucion   =document.getElementById("<portlet:namespace/>resolucion");
	if (resolucion.selectedIndex==0){
		alert('Debe seleccionar el tipo de resolucion de la lista.');
		return false ;
	}		
	
	var diaExist  = isNaN(parseInt(jQuery("#<portlet:namespace />fecharevisionDia").val()));
	var mesExist  = isNaN(parseInt(jQuery("#<portlet:namespace />fecharevisionMes").val()));
	var anioExist   = isNaN(parseInt(jQuery("#<portlet:namespace />fecharevisionAnio").val()));
	
	var dia  = jQuery('#<portlet:namespace />fechaospimDia').val();
	var mes  = jQuery("#<portlet:namespace />fechaospimMes").val() ;
	var anio   = jQuery("#<portlet:namespace />fechaospimAnio").val();
	
	var fechaOspim  = new Date(anio, mes ,dia);
	var fechaRevision  = new Date(anionro,mesnro, dianro);
	var t = Date.now();
	var hoy = new Date(t);
	    
    diff  = new Date(fechaRevision - fechaOspim);
    days  = diff/1000/60/60/24;     
    
    if (diaExist || mesExist || anioExist) {
    	alert('Error en la fecha de revision ingresada.');
    	return false;
    }
	if(days<0){
		alert('La fecha de revision no puede ser inferior a la fecha de Ingreso del Reclamo (Fecha Ospim).');
		return false;
	}
	
	diff  = new Date(hoy  - fechaRevision);
    days  = diff/1000/60/60/24;
	if(days<0){
		alert('La fecha de revision no puede ser superior a la fecha de hoy.');
		return false;
	}
	
	
	return true ;		
}

function ValidarDatosObligatorios(Edicion){
	var date = new Date();
//	var baja_fecha = jQuery('#<portlet:namespace />baja_fecha').val();
	var valor = 0;
	valor=jQuery('#<portlet:namespace />cantprestacioneslista').val();
	
	var dia1  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalDia").val()));
	var mes1  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalMes").val()));
	var anio1   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalAnio").val()));	
	
	
	var dia2  = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreDia").val()));
	var mes2  = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreMes").val()));
	var anio2   = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreAnio").val()));
	
	
	var msgs = ["Debe seleccionar al Afiliado asociado al reclamo.","Complete la Fecha Seccional o dejela en blanco"]; 
	var condiciones =[1];
	var controles  =[1];
		
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();	
	
	var  resp=true;
	
	controles[0]=document.getElementById("<portlet:namespace />cuil");	
	controles[1]=document.getElementById("<portlet:namespace />fechaseccionalDia");
	
	
	condiciones[0]=(cuil=="" || inte=="" );
	condiciones[1]=(dia1 || mes1 || anio1) && (!dia1 || !mes1 || !anio1) ;
	

	if (condiciones[0] && resp){
		resp=false;
		alert (msgs[0] );
		controles[0].focus();
	}
	if (condiciones[1] && resp){
		resp=false;
		alert (msgs[1] );
		controles[1].focus();
	}

	/* DS -- Comentado 01/09/2022 se cambia por criterio nuevo contemplando fecha de prestacion y baja de afiliado
	if(baja_fecha !=null){
    	var valuesStart=baja_fecha.split("/");
    	var dateStart=new Date(valuesStart[2],(valuesStart[1]-1),valuesStart[0]);
    	if(dateStart<date){
     	  alert("Afiliado dado de Baja"); 
     	  return false;
    	}  
    }
    */
	
	
    if (Edicion && addprestacion) {
    	if (valor <1   && resp){
    		alert('Debe tener ingresada por lo menos una prestación');
    		resp=false;
    	}
    }else{
    		if (valor <1  && resp ){
    			alert('Debe tener ingresada por lo menos una prestación');
    			resp=false;
    		}	
    }
    
	
    var codError='';	
	var baja =  jQuery('#<portlet:namespace />baja_fecha').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_reclamo_afiliado_prestaciones';
	url +='&baja='+baja;
	 
	jQuery.ajax({   
		   url: url,
		   async: false,
		   success: function(data) {
			  var obj = jQuery.parseJSON(data);
			  codError = obj.codError;
	   		}
	 }); 
		   
	 if(codError == '6'){
	       alert('La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado');
		   resp=false;	   
	 }
    
	return resp;	
}


function <portlet:namespace />saveReclamo() {
	

	
	if ( ValidarDatosObligatorios(false))  {
				
		var accionEnCurso = document.<portlet:namespace />reclamo_fm.<portlet:namespace /><%= Constants.CMD %>.value;
		document.<portlet:namespace />reclamo_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.SAVE  %>';
		var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";

		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:actionURL>';
		url = url + "&esDatosTab=true";
		url = url + params;
		document.<portlet:namespace />reclamo_fm.method = 'post';
		submitForm(document.<portlet:namespace />reclamo_fm, url);		
	
	}							  	
}


function <portlet:namespace />editaReclamo(fromAutoriza) {
	
	if (fromAutoriza) {
		abreAutorizacion();
	}
	
	if ( ValidarDatosObligatorios(true))  {
	
	  var accionEnCurso = document.<portlet:namespace />reclamo_fm.<portlet:namespace /><%= Constants.CMD %>.value;
	  document.<portlet:namespace />reclamo_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.UPDATE %>';
	  var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";

	
	  var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:actionURL>';
	  url = url + "&esDatosTab=true";
	  url = url + params;
	  document.<portlet:namespace />reclamo_fm.method = 'post';
	
	  submitForm(document.<portlet:namespace />reclamo_fm, url);
		
	}							  	
}


function <portlet:namespace />siguienteSolapa() {

	
	    var accionEnCurso = document.<portlet:namespace />reclamo_fm.<portlet:namespace /><%= Constants.CMD %>.value;
		document.<portlet:namespace />reclamo_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.MOVE %>';
        var params = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";

		
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" /></portlet:actionURL>';
		url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=cta_bancaria' +  '&<%= Constants.CMD %>=<%=Constants.MOVE %>';
        url = url + params;

		document.<portlet:namespace />reclamo_fm.method = 'post';
	
		submitForm(document.<portlet:namespace />reclamo_fm, url);
}




function manejarListaPresentes(){
	var tipoSelect  =document.getElementById("<portlet:namespace />presenteslista");
	jQuery("#<portlet:namespace />presentes").val(tipoSelect.value); // asigna el valor de la lista al control oculto 
}


function cambioresolucion(){
	var tipoSelect  =document.getElementById("<portlet:namespace />resolucion");
	var justificacion=jQuery('#<portlet:namespace />justificacionmedcica_reclamo').val();
	if  (tipoSelect.selectedIndex>0 && justificacion.length ==0  && document.getElementById("<portlet:namespace />respresolucion").selectedIndex!=1){
			jQuery('#<portlet:namespace />justificacionmedcica_reclamo').focus();
			tipoSelect.selectedIndex=0;
			alert('Tiene que ingresar la Justificacion Medica del Caso para ingresar la revision.');			
		}		
}





function manejarTipoPedidoCierre(){
	var tipoPedido  = document.getElementById("<portlet:namespace />tipopedido");
	jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo').html('');  //vacio lista opciones del select
/* 	jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo").append(new Option("SELECCIONE LA GESTION", "0"));
	document.getElementById("<portlet:namespace />tipopedido").selectedIndex==0 */
	if(tipoPedido.value=="EXCEPCION"){
		jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo").append(new Option("FACTURACION DIRECTA", "3"));
		jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo option[value='3']").attr("selected", true); //FACT. DIRECTA
	}
	if(tipoPedido.value=="REINTEGRO"){
		jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo").append(new Option("REINTEGRO", "4"));
		jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo option[value='4']").attr("selected", true); //REINTEGRO
	}
	if(tipoPedido.value=="EXTRACAPITA"){
		jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo").append(new Option("EXTRACAPITA", "1"));
		jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo option[value='1']").attr("selected", true); //EXTRACAPITA
	}
	jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo").append(new Option("RECHAZADO", "5"));
}

function manejarTipoSector(){
	var tipoSector  =document.getElementById("<portlet:namespace />sector");
	var tipopedido  = document.getElementById("<portlet:namespace />tipopedido");
	try {
		jQuery("#<portlet:namespace />busqueda_prestaciones").show();
		jQuery("#<portlet:namespace />busqueda_farmacia").hide();
		jQuery("#<portlet:namespace />nom_seleccionado").val("1"); // se selecciono maestra de prestaciones medicas 
		jQuery('#<portlet:namespace />troquel').val("");  
		jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val("");
		jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val("");
   		
		// 1. Discapacidad
		// 2. Prest Medicas
		// 3. Farmacia
		// 4. Odonto
				
		if (tipoSector.selectedIndex==3){
   			 
   			if (tipopedido.selectedIndex!=1){
				if(tipoSector.selectedIndex == 3){
	   				jQuery("#<portlet:namespace />busqueda_farmacia").show();       
	  				 jQuery("#<portlet:namespace />busqueda_prestaciones").hide();    		
				}
   				
   					       
  		         jQuery("#<portlet:namespace />nom_seleccionado").val("2"); // se selecciono maestra de farmacia
   	   		}else{
   	   		     jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val(9);  // farmacia
   	   		}	 
        }
   		if (tipoSector.selectedIndex==1){  
   	       jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val(8); // discapacidad 
   		}else if (tipoSector.selectedIndex==4){
   			/* ODONTOLOGIA Tipo Nomenclador 1 */
   			jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val(1); // discapacidad	
   		}
   		
	}
	catch (err) {
		alert('error manejarTipoSector() ');
	}
}


function <portlet:namespace />agregarRevision() {		
     
	var  revisionConCierre =false;
	
	if ( DatosRevisionOk())  {
		
		var resolucion = jQuery('#<portlet:namespace />resolucion').val();
		
		var presentes = jQuery('#<portlet:namespace />presentes').val();
		var respresolucion = jQuery('#<portlet:namespace />respresolucion').val();		
		var revisionFechaVtoDia = jQuery('#<portlet:namespace />fecharevisionDia').val(); 
		var revisionFechaVtoMes = jQuery('#<portlet:namespace />fecharevisionMes').val(); 
		var revisionFechaVtoAnio = jQuery('#<portlet:namespace />fecharevisionAnio').val();
		
		var reclamoobservacion  = jQuery('#<portlet:namespace />observacion_revision').val();
		var chk_amparo=jQuery("#<portlet:namespace/>chk_amparo").is(':checked');
		var chk_superintendencia=jQuery("#<portlet:namespace/>chk_superintendencia").is(':checked');
		var chk_recuperable = jQuery("#<portlet:namespace/>chk_recuperable").is(':checked');
		var chk_entramite = jQuery("#portlet:namespace />chk_entramite").is(':checked');
	
	    if (document.getElementById("<portlet:namespace />resolucion").selectedIndex==0 ) {
	    	resolucion="";     
	    }
	    if (document.getElementById("<portlet:namespace />presentes").selectedIndex==0 ) { 
	    	presentes="";     
	    }
	    if (document.getElementById("<portlet:namespace />respresolucion").selectedIndex==0 ) {
	    	respresolucion="";     
	    }
	    jQuery('#<portlet:namespace />auditoriaadministrativa').val('');
	    if (document.getElementById("<portlet:namespace />respresolucion").selectedIndex==1 ) {
	    	jQuery('#<portlet:namespace />auditoriaadministrativa').val('Ok');
	    }
	    
		var params = {"resolucion":resolucion,
							   "presentes":presentes,
							   "respresolucion":respresolucion,
							   "revisionFechaVtoDia":revisionFechaVtoDia,
							   "revisionFechaVtoMes":revisionFechaVtoMes,
							   "revisionFechaVtoAnio":revisionFechaVtoAnio,						   
							   "reclamoobservacion":reclamoobservacion
							   						   
							   };
			
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_revisiones_reclamo" /></portlet:renderURL>';
		
		
		if (resolucion.toUpperCase()!="AUTORIZADO"){
			if(confirm("Confirma el Cierre del Caso con el Rechazo en la revision ?")){
	 			    /* var estadoSelectsector  =document.getElementById("<portlet:namespace/>estado"); */
				    //estadoSelectsector.selectedIndex = 2; // setea el estado en cerrado
				    /* estadoSelectsector.selectedIndex = ubicacionOpcionEstadoCerradoCombo();	 */		    
				    jQuery("#<portlet:namespace/>estado option[value='3']").attr("selected", true); //CERARADO
				    
				    controlarEstadoCerrado(); // hace visible los controles del estado cerrado
				    
				    document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo").disabled = false;
					
					var tipoSelectsector  =document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo");
					
					seteaControlesFacturacionDirecta(true);
					/* tipoSelectsector.selectedIndex= ubicacionOpcionRechazadoenCombo(); */
				    jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo option[value='5']").attr("selected", true); //RECHAZADO
					/* var tipoGestionArray=jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo').val().split("|"); */
					var idgestion=jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo').val()
					
					/* var idgestion =tipoGestionArray [0]; */
					jQuery('#<portlet:namespace />tipogestion').val(idgestion);				
					jQuery('#<portlet:namespace/>reclamo_observacion_cierre').val('RECHAZO DE LA PRESTACION EN LA REVISION.');
					revisionConCierre=true;
					jQuery('#<portlet:namespace/>cantrevisionesactivas').val(1); // para que no valide esto
					desactivaCheckCierre();							
					
	 		}else{
					return false;	
			}	
		}
			
		// oculta boton de agreagr revision porque solo se admite un aprobacion o un rechazo no hay parciales dentro del reclamo
		jQuery("#<portlet:namespace />botonrevision").hide();
		jQuery("#<portlet:namespace/>mensajerevisionefectuada").html("Revisión Efectuada, el Sistema soporta solo una revisión activa (No de baja).");
	
	 	jQuery('#<portlet:namespace />lista_revisiones').load(url,params, function(){
															jQuery('#<portlet:namespace />buscando').hide();            															
														  });
	 	
		 jQuery('#<portlet:namespace />resolucion').val('');
		 jQuery('#<portlet:namespace />presentes').val('');
		 jQuery('#<portlet:namespace />respresolucion').val('');	  	  	  
		 document.getElementById("<portlet:namespace />fecharevisionDia").selectedIndex = 0;	 
		 document.getElementById("<portlet:namespace />fecharevisionMes").selectedIndex = 0;	 
		 document.getElementById("<portlet:namespace />fecharevisionAnio").selectedIndex = 0;
		 document.getElementById("<portlet:namespace />fecharevisionAnio").selectedIndex = 0;
		 jQuery('#<portlet:namespace />observacion_revision').val('');
		 <%if(reclamoprestacional != null  ){%>
		 	if (revisionConCierre==true){			 
		 		<portlet:namespace />editaReclamo(false); 
		 	}
		 <%}else{%>
		 	if (revisionConCierre==true){
			 <portlet:namespace />saveReclamo();
		 	}
		 <%}%>
	}
}       		



function <portlet:namespace />verprestacionesasociadas() {
	
	if (document.getElementById("<portlet:namespace />botonprestacionesasociadas").value=='Ver Prestaciones del Caso Asociado.'){
		jQuery("#<portlet:namespace />lista_prestaciones_asociadas").show();
		document.getElementById("<portlet:namespace />botonprestacionesasociadas").value='Ocultar Prestaciones del Caso Asociado.';
	}else{
		jQuery("#<portlet:namespace />lista_prestaciones_asociadas").hide();
		document.getElementById("<portlet:namespace />botonprestacionesasociadas").value='Ver Prestaciones del Caso Asociado.';
	}
}

function <portlet:namespace />ocultacontactosdelreclamo() {
	jQuery("#<portlet:namespace />lista_contactos_reclamo").hide();
	jQuery("#<portlet:namespace />botoncontactosreclamo").show();
	jQuery("<portlet:namespace />botoncontactosreclamo").value='Ver Contactos Asociados al Caso.';

}


	

function <portlet:namespace />editarPrestacionSeleccionada(tipoAccion) {
	//tipoAccion=1 edicion 
	//tipoAccion=2 Autorizacion prestacion 
	//tipoAccion=3 Rechazo de  prestacion	
	
	var frecuencia= jQuery('#<portlet:namespace />frecuenciaEdicion').val();
	var cantidad =  jQuery('#<portlet:namespace />cantidadEdicion').val();
	var importe = jQuery('#<portlet:namespace />importeEdicion').val();
	var cargoospim= jQuery('#<portlet:namespace />cargoospimEdicion').val();
	var cargops= jQuery('#<portlet:namespace />cargopsEdicion').val();
	var observaciones= jQuery('#<portlet:namespace />observacion_prestacionEdicion').val();
    var prestacion= "Graba Edicion";
    var idprestacion =  jQuery("#<portlet:namespace />codigoprestacion").val();
    var idRegistro=jQuery('#<portlet:namespace />idRegistro').val();

    var estadoAprobacion = tipoAccion;
    var recuperableSur  =  jQuery('#<portlet:namespace />recuperable_surEdicion').attr('checked');  
    
    var cpbteTipo=jQuery('#<portlet:namespace />comprobante_tipo_edicion').val();
    var cpbteNro=jQuery('#<portlet:namespace />comprobante_nro_edicion').val();
    var cpbteDia=jQuery('#<portlet:namespace />fechaComprobanteDiaEdicion').val();
    var cpbteMes=jQuery('#<portlet:namespace />fechaComprobanteMesEdicion').val();
    var cpbteAnio=jQuery('#<portlet:namespace />fechaComprobanteAnioEdicion').val();
    var cpbteCantidad=jQuery('#<portlet:namespace />cantidadFC_edicion').val();
    var cpbteImporte= jQuery('#<portlet:namespace />importeUnitarioFC_edicion').val();
    var importeFC = jQuery('#<portlet:namespace />importeFC_edicion').val();
    var cpbteCuit=jQuery('#<portlet:namespace />cuit_entidad_edicion').val();
    var cpbteSucursal=jQuery('#<portlet:namespace />comprobante_suc_edicion').val();
    var cpbteCuitSucursal=jQuery('#<portlet:namespace />sucursal_entidad_edicion').val();
    var cpbteLetra=jQuery('#<portlet:namespace />comprobante_letra_edicion').val();

    

    
    if (!validaMontosEdicion()){       
   		return false;
	}

    
    var sector=jQuery('#<portlet:namespace />sector').val();

    var fechaPrestacionDia='';
    var fechaPrestacionMes='';
    var fechaPrestacionAnio='';
	
    
    
    fechaPrestacionDia=jQuery('#<portlet:namespace />fechaPrestacionDiaEdicion').val(); 
    fechaPrestacionMes=jQuery('#<portlet:namespace />fechaPrestacionMesEdicion').val();
    fechaPrestacionAnio=jQuery('#<portlet:namespace />fechaPrestacionAnioEdicion').val();
    


    id_medicamento_edit=jQuery('#<portlet:namespace />troquel_edit').val();
	var nombre_medicamento_edit = jQuery('#<portlet:namespace />nombre_medicamento_edit').val();
    
    

	var codigoSeguimiento_filtro_edit = jQuery('#<portlet:namespace />codigoSeguimiento_filtro_edit').val();
	var descripcionSeguimiento_filtro_edit = jQuery("#<portlet:namespace />descripcionSeguimiento_filtro_edit").val();
	var nom_seleccionado_edit = jQuery("#<portlet:namespace />nom_seleccionado").val(); 
	var tipoNomenclador_edit = jQuery('#<portlet:namespace />tipoNomenclador').val();
   
	
	
	
	
	var nombre_medicamento_edit = jQuery('#<portlet:namespace />nombre_medicamento_edit').val();

	

	if (nom_seleccionado_edit ==1){		 
		if (codigoSeguimiento_filtro_edit<1  ) {
		  alert('Debe seleccionar la prestacion');
		  return false;
		} 	
	    if(descripcionSeguimiento_filtro_edit==null || descripcionSeguimiento_filtro_edit==''){
			  alert('Debe seleccionar la prestacion');		  
			  return false;
	    }
		
	}else{		
		if ( id_medicamento_edit <1) {
			alert('Debe seleccionar el medicamento');
			return false;
		}
		if ( nombre_medicamento_edit==null || nombre_medicamento_edit=='') {
			alert('Debe seleccionar el medicamento');
			return false;
		}
		

	}    
	
	/* if (jQuery("#<portlet:namespace />comprobante_letra_edicion").val()==''){
		  alert('Debe seleccionar el una letra comprobate');
		  return false;
	}	
    	
    if(fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ||
    	       fechaPrestacionMes==null || fechaPrestacionMes==-1 || fechaPrestacionMes=='' ||
    	       fechaPrestacionAnio==null || fechaPrestacionAnio==0 || fechaPrestacionAnio==''){
    	       alert('Debe ingresar la fecha del Prestación');
    	return false;	
    } */
    	   
	if (cpbteTipo != 'OTR' && cpbteLetra==''){
		  alert('Debe seleccionar la letra del comprobante');
		  return false;
	}	
	
	if(importeFC==null || importeFC==0){
	  	alert('Debe ingresar el importe de la Factura.');
		return false ;
	}
	
 
  if(cpbteCuit==null || cpbteCuit==''){
  	alert('Debe ingresar el CUIT del Comprobante');
		return false ;
  }
  

  if(cpbteCuitSucursal==null || cpbteCuitSucursal==''){
  	alert('Debe ingresar la sucursal del CUIT del Comprobante');
		return false ;
  }
  
  
  if(cpbteTipo != 'OTR' && (cpbteSucursal==null || cpbteSucursal=='')){
  	alert('Debe ingresar la Sucursal del Comprobante');
		return false ;
  }
  
  if(cpbteTipo != 'OTR' && (cpbteNro==null || cpbteNro=='')){
  	alert('Debe ingresar el Nro del Comprobante');
		return false ;
  }
  
  if (cpbteTipo != 'OTR' && (cpbteDia==null || cpbteDia==0 || cpbteDia=='' ||
	       cpbteMes==null || cpbteMes==-1 || cpbteMes=='' ||
	       cpbteAnio==null || cpbteAnio==0 || cpbteAnio=='')){
	  
	  alert('Debe ingresar la fecha del Comprobante');
	  return false;	
	    
  }
	
  if(cpbteCantidad==null || cpbteCantidad==0 || cpbteCantidad==''){
 		alert('Debe ingresar la cantidad del Comprobante');
      return false;	
  }
 
  if(cpbteImporte==null || cpbteImporte==0 || cpbteImporte==''){
	 	alert('Debe ingresar importe unitario del Comprobante');
      return false;	
  }
 
  if(importeFC==null || importeFC==0 || importeFC==''){
   	alert('Debe ingresar importe total del Comprobante');
    return false;	
  }
    
  if (!ValidaDatosReclamoEditar()){       
   		return false;
   }
    
    
    
    frecuencia="UNICA";    
	
    var params_aux = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";

    
	var params = {"frecuencia":frecuencia,
						   "importe":importe,	
						   "cargoospim":cargoospim,
						   "cargops":cargops,						   
						   "prestacion":prestacion,
						   "idprestacion":idprestacion,
						   "idRegistro":idRegistro,
						   "grabaedicion":true,
						   "estadoAprobacion": estadoAprobacion,
						   "recuperableSur": recuperableSur,
						   "cantidad": cantidad,
						   "observaciones":observaciones,
						   "cpbte_tipo":cpbteTipo,
						   "cpbte_nro":cpbteNro,
						   "cpbte_dia":cpbteDia,
						   "cpbte_mes":cpbteMes,
						   "cpbte_anio":cpbteAnio,
						   "cpbte_cantidad":cpbteCantidad,
						   "cpbte_importe":cpbteImporte,
						   "cpbte_cuit":cpbteCuit,
						   "cpbte_sucursal":cpbteSucursal,
						   "importeFC":importeFC,
						   "cpbte_cuit_sucursal":cpbteCuitSucursal,
						   "cpbte_letra":cpbteLetra,
						   "fecha_prestacion_dia":fechaPrestacionDia,
						   "fecha_prestacion_mes":fechaPrestacionMes,
						   "fecha_prestacion_anio":fechaPrestacionAnio,
						   "id_medicamento_edit":id_medicamento_edit,
						   "nombre_medicamento_edit":nombre_medicamento_edit,
						   "codigoSeguimiento_filtro_edit":codigoSeguimiento_filtro_edit,
						   "descripcionSeguimiento_filtro_edit":descripcionSeguimiento_filtro_edit,
						   "nom_seleccionado_edit":nom_seleccionado_edit,
						   "tipoNomenclador_edit":tipoNomenclador_edit	   
					  };	
	
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones" /></portlet:renderURL>';

 	
 	if(cpbteTipo != 'OTR'){
		if (!validarExisteComprobante(params)){   
		   	return false;
		}
	}    
 	
	url = url + params_aux;
	
 	
	jQuery('#<portlet:namespace />lista_prestaciones_reclamos').load(url,params, function(){
									jQuery('#<portlet:namespace />buscando').hide();            															
													  });			
	jQuery('#<portlet:namespace />cantidadEdicion').val('1');
	jQuery('#<portlet:namespace />importeEdicion').val('');
	jQuery('#<portlet:namespace />totalEdicion').val('');
 	jQuery('#<portlet:namespace />cargoospimEdicion').val('');
 	jQuery('#<portlet:namespace />cargopsEdicion').val('');
 	jQuery('#<portlet:namespace />observacion_prestacionEdicion').val('');
	//document.getElementById("<portlet:namespace />frecuenciaEdicion").selectedIndex = 0;
	jQuery('#<portlet:namespace />troquel').val(""); // farmacia 
	jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val("");// prestaciones medicas 
	jQuery('#<portlet:namespace />recuperable_sur').attr('checked', false);	
	
	
	jQuery('#<portlet:namespace />comprobante_tipo_edicion').val('FCP');
	jQuery('#<portlet:namespace />comprobante_letra_edicion').val('');

	jQuery('#<portlet:namespace />comprobante_nro_edicion').val('');
	jQuery('#<portlet:namespace />comprobante_suc_edicion').val('');
	jQuery('#<portlet:namespace />fechaComprobanteDiaEdicion').val('');
	jQuery('#<portlet:namespace />fechaComprobanteMesEdicion').val('');
	jQuery('#<portlet:namespace />fechaComprobanteAnioEdicion').val('');
	jQuery('#<portlet:namespace />cantidadFC_edicion').val('');
	jQuery('#<portlet:namespace />importeUnitarioFC_edicion').val('');
	jQuery('#<portlet:namespace />importeFC_edicion').val('');
	jQuery('#<portlet:namespace />cuit_entidad_edicion').val('');
    jQuery('#<portlet:namespace />sucursal_entidad_edicion').val('');
    jQuery('#<portlet:namespace />entidad_edicion').val('');

    
	jQuery('#<portlet:namespace />fechaPrestacionDiaFarmacia').val(''); 
    jQuery('#<portlet:namespace />fechaPrestacionMesFarmacia').val('');
    jQuery('#<portlet:namespace />fechaPrestacionAnioFarmacia').val('');
    
	jQuery('#<portlet:namespace />fechaPrestacionDia').val(''); 
    jQuery('#<portlet:namespace />fechaPrestacionMes').val('');
    jQuery('#<portlet:namespace />fechaPrestacionAnio').val('');
    
    jQuery('#<portlet:namespace />fechaPrestacionDiaEdicion').val('');
    jQuery('#<portlet:namespace />fechaPrestacionMesEdicion').val('');
    jQuery('#<portlet:namespace />fechaPrestacionAnioEdicion').val('');

    
    
    jQuery("#<portlet:namespace />nombre_medicamento_edit").val('');
    jQuery("#<portlet:namespace />divBtnBuscaMedicamento_edit").show();
	
	<portlet:namespace />limpiarNomencladorAutocompletar();
	<portlet:namespace />cancelaEdicionPrestacion();
	   
    addprestacion=false;
 
}


function <portlet:namespace />cancelaEdicionPrestacion() {
	
	// oculta div de datos de edicion
	jQuery("#<portlet:namespace />datos_edicion_prestacion").hide();
	// habilita el buscador segun el sector
	manejarTipoSector();	
	jQuery("#<portlet:namespace />datos_prestacion_ingreso").show();
	
	<portlet:namespace />limpiarNomencladorAutocompletar();

	
	onOffcombosestadosprestaciones(true);	
	// mover el combo a la posicion de cargado porque no se confirmo el rechazo o la autorizacion
	//var datos = document.getElementById("<portlet:namespace />tipoaccionprestacion").value;	
	//var datasplit =datos.split('-');
	//var idPrestacion = datasplit[1];	
	//document.getElementById('comboestadosreclamo'+ idPrestacion ).selectedIndex = "0";	
	//document.getElementById("<portlet:namespace />tipoaccionprestacion").value="";
		
	jQuery("#<portlet:namespace />buttonaddprestacion").show();    
	jQuery("#<portlet:namespace />datos_prestacion_ingreso_obs").show(); 

	
}

function <portlet:namespace />agregarPrestacion() {	

	var frecuencia= 'UNICA';		
	var importe = jQuery('#<portlet:namespace />importe').val();
	var cantidad  = jQuery('#<portlet:namespace />cantidad').val();
	var observaciones= jQuery('#<portlet:namespace />observacion_prestacion').val();		
    var troquel= jQuery('#<portlet:namespace />troquel').val();
    var prestacion= jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val();    
    var tiponomenclador =jQuery('#<portlet:namespace />nom_seleccionado').val();
    var tiponomencladorprestacion =jQuery('#<portlet:namespace />tiponomenclador').val();
    var nombre_medicamento=jQuery("#<portlet:namespace />nombre_medicamento").val();
    var nombre_prestacion = jQuery('#<portlet:namespace />descripcionSeguimiento_filtro').val();
    var tiponomnecladorprestacion =  jQuery("#<portlet:namespace />tipoNomenclador").val();     
    var cpbteTipo=jQuery('#<portlet:namespace />comprobante_tipo').val();
    var cpbteNro=jQuery('#<portlet:namespace />comprobante_nro').val();
    var cpbteDia=jQuery('#<portlet:namespace />fechaComprobanteDia').val();
    var cpbteMes=jQuery('#<portlet:namespace />fechaComprobanteMes').val();
    var cpbteAnio=jQuery('#<portlet:namespace />fechaComprobanteAnio').val();
    var cpbteCantidad=jQuery('#<portlet:namespace />cantidadFC').val();
    var cpbteImporte= jQuery('#<portlet:namespace />importeUnitarioFC').val();
    var importeFC = jQuery('#<portlet:namespace />importeFC').val();
    var cpbteCuit=jQuery('#<portlet:namespace />cuit_entidad').val();
    var cpbteCuitSucursal=jQuery('#<portlet:namespace />sucursal_entidad').val();
    var cpbteSucursal=jQuery('#<portlet:namespace />comprobante_suc').val();
    var cpbteLetra=jQuery('#<portlet:namespace />comprobante_letra').val();
    
    var sector=jQuery('#<portlet:namespace />sector').val();

    var fechaPrestacionDia='';
    var fechaPrestacionMes='';
    var fechaPrestacionAnio='';
	
    
    if (sector == 'FARMACIA'){
    	 fechaPrestacionDia=jQuery('#<portlet:namespace />fechaPrestacionDiaFarmacia').val(); 
         fechaPrestacionMes=jQuery('#<portlet:namespace />fechaPrestacionMesFarmacia').val();
         fechaPrestacionAnio=jQuery('#<portlet:namespace />fechaPrestacionAnioFarmacia').val();
    }else{
        fechaPrestacionDia=jQuery('#<portlet:namespace />fechaPrestacionDia').val(); 
        fechaPrestacionMes=jQuery('#<portlet:namespace />fechaPrestacionMes').val();
        fechaPrestacionAnio=jQuery('#<portlet:namespace />fechaPrestacionAnio').val();
    }

    
 	if (jQuery("#<portlet:namespace />nom_seleccionado").val()==''){
		  alert('Debe seleccionar el sector');
		  return false;
	}	
	    
	if (jQuery("#<portlet:namespace />nom_seleccionado").val()==1){		 
		if (jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val()<1  ) {
		 	alert('Debe seleccionar la prestacion');
		 	return false;
		} 	
	    if(nombre_prestacion==null || nombre_prestacion==''){
			  alert('Debe seleccionar la prestacion');		
			  return false;
		}
			
	}else{		
		if (jQuery('#<portlet:namespace />troquel').val()<1) {
			alert('Debe seleccionar el medicamento');
			return false;
		}	
		if ( nombre_medicamento==null || nombre_medicamento=='') {
			alert('Debe seleccionar el medicamento');
			return false;
		}
	}    
	

   
    
    if(importeFC==null || importeFC==0){
    	alert('Debe ingresar el importe de la Factura.');
		return false ;
    }
    
    if(cpbteCuit==null || cpbteCuit==''){
    	alert('Debe ingresar el CUIT del Comprobante');
		return false ;
    }
    
    
    if(cpbteCuitSucursal==null || cpbteCuitSucursal==''){
    	alert('Debe ingresar la sucursal del CUIT del Comprobante');
		return false ;
    }
    
    
    if((cpbteSucursal==null || cpbteSucursal=='') && cpbteTipo != 'OTR'){
    	alert('Debe ingresar la Sucursal del Comprobante');
		return false ;
    }
    
    if (cpbteTipo != 'OTR' && cpbteLetra==''){
		  alert('Debe seleccionar la letra del comprobante');
		  return false;
	}	
    
    if((cpbteNro==null || cpbteNro=='') && cpbteTipo != 'OTR'){
    	alert('Debe ingresar el Nro del Comprobante');
		return false ;
    }
    
    if((cpbteDia==null || cpbteDia==0 || cpbteDia=='' ||
       cpbteMes==null || cpbteMes==-1 || cpbteMes=='' ||
       cpbteAnio==null || cpbteAnio==0 || cpbteAnio=='') && cpbteTipo != 'OTR'){
       alert('Debe ingresar la fecha del Comprobante');
       return false;	
    }
    
    if(fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ||
       fechaPrestacionMes==null || fechaPrestacionMes==-1 || fechaPrestacionMes=='' ||
       fechaPrestacionAnio==null || fechaPrestacionAnio==0 || fechaPrestacionAnio==''){
       alert('Debe ingresar la fecha del Prestación');
        return false;	
   	}
    

    if(cpbteCantidad==null || cpbteCantidad==0 || cpbteCantidad==''){
    	 alert('Debe ingresar la cantidad del Comprobante');
         return false;	
    }
    
    if(cpbteImporte==null || cpbteImporte==0 || cpbteImporte==''){
   	 alert('Debe ingresar importe unitario del Comprobante');
        return false;	
    }
    
    if(importeFC==null || importeFC==0 || importeFC==''){
      	 alert('Debe ingresar importe total del Comprobante');
           return false;	
    }
    
    
    if (!ValidaMontos()){       
   		return false;
	}
    
    if (!ValidaDatosReclamo()){       
   		return false;
	}
    
   
   
	var accion = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";

    
	var params = {"frecuencia":frecuencia,
			   "importe":importe,	
			   "troquel":troquel,
			   "prestacion":prestacion,
			   "tiponomenclador":tiponomenclador,
			   "nombre_medicamento":nombre_medicamento,
			   "nombre_prestacion":nombre_prestacion,
			   "tiponomnecladorprestacion":tiponomnecladorprestacion,
			   "cantidad":cantidad,
			   "observaciones":observaciones,
			   "cpbte_tipo":cpbteTipo,
			   "cpbte_nro":cpbteNro,
			   "cpbte_dia":cpbteDia,
			   "cpbte_mes":cpbteMes,
			   "cpbte_anio":cpbteAnio,
			   "cpbte_cantidad":cpbteCantidad,
			   "cpbte_importe":cpbteImporte,
			   "cpbte_cuit":cpbteCuit,
			   "cpbte_sucursal":cpbteSucursal,
			   "importeFC":importeFC,
			   "cpbte_cuit_sucursal":cpbteCuitSucursal,
			   "cpbte_letra":cpbteLetra,
			   "fecha_prestacion_dia":fechaPrestacionDia,
			   "fecha_prestacion_mes":fechaPrestacionMes,
			   "fecha_prestacion_anio":fechaPrestacionAnio
			   };
	
	if(cpbteTipo != 'OTR'){
		if (!validarExisteComprobante(params)){   
	   		return false;
		}
	}   

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_prestaciones_reclamos_seccional" /></portlet:renderURL>';

	url = url + accion;
	jQuery('#<portlet:namespace />lista_prestaciones_reclamos').load(url,params, function(){
									jQuery('#<portlet:namespace />buscando').hide();            															
													  });			
 	
	var mantenerComprobante=jQuery("#<portlet:namespace/>mantenerComprobante").is(':checked');

	if (mantenerComprobante == false){
		jQuery('#<portlet:namespace />comprobante_tipo').val('FCP');
		jQuery('#<portlet:namespace />comprobante_nro').val('');
		jQuery('#<portlet:namespace />fechaComprobanteDia').val('');
		jQuery('#<portlet:namespace />fechaComprobanteMes').val('');
		jQuery('#<portlet:namespace />fechaComprobanteAnio').val('');
	
		jQuery('#<portlet:namespace />cuit_entidad').val('');
	    jQuery('#<portlet:namespace />sucursal_entidad').val('');
	    jQuery('#<portlet:namespace />entidad_').val('');
	    jQuery('#<portlet:namespace />comprobante_suc').val('');
	}

	jQuery("#<portlet:namespace />divBtnBuscaEntidad").show();
	jQuery('#<portlet:namespace />importe').val('');
 	jQuery('#<portlet:namespace />total').val('');
 	jQuery('#<portlet:namespace />cantidad').val('1');
 	jQuery('#<portlet:namespace />observacion_prestacion').val('');
	jQuery('#<portlet:namespace />troquel').val(""); // farmacia 
	jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val("");// prestaciones medicas
	jQuery('#<portlet:namespace />cantidadFC').val('');
	jQuery('#<portlet:namespace />importeUnitarioFC').val('');
	jQuery('#<portlet:namespace />importeFC').val('');
	
	jQuery('#<portlet:namespace />fechaPrestacionDiaFarmacia').val(''); 
    jQuery('#<portlet:namespace />fechaPrestacionMesFarmacia').val('');
    jQuery('#<portlet:namespace />fechaPrestacionAnioFarmacia').val('');
    
	jQuery('#<portlet:namespace />fechaPrestacionDia').val(''); 
    jQuery('#<portlet:namespace />fechaPrestacionMes').val('');
    jQuery('#<portlet:namespace />fechaPrestacionAnio').val('');



    jQuery("#<portlet:namespace />nombre_medicamento").val('');
    jQuery("#<portlet:namespace />divBtnBuscaMedicamento").show();
    
	<portlet:namespace />limpiarNomencladorAutocompletar();
	
    addprestacion=true;
    /* document.getElementById("<portlet:namespace />tipopedido").disabled = true;  */    
    if (jQuery('#<portlet:namespace/>estado').val()==3){   // cerrado
    	jQuery('#<portlet:namespace />montoPsPrestaciones').val(cargops); 
		validaFacturacionDirectayReintegro();    	
    }	                            
}   

function controlarEstadoCerrado() {

	// VERIFICAR SI EXISTE POR LO MENOS UN REGISTRO DE REVISION ACTIVO 	
	if (jQuery('#<portlet:namespace/>estado').val()==3){			
		jQuery("#<portlet:namespace />Cierre_Reclamo_Div").show();		
		validaFacturacionDirectayReintegro();		
	} else {
		jQuery("#<portlet:namespace />Cierre_Reclamo_Div").hide();
		jQuery('#<portlet:namespace/>nroLote').val("");
	}	
}




function validarExisteComprobante( params ) {
    var resp=true;
	var respuesta=true;
    var rtaExisteCompro=false;
    var mensajeErrorOut='';
      
    
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_existe_comprobante';
	
	    url +='&frecuencia='+params.frecuencia;
	    url +='&troquel='+params.troquel;
	    url +='&prestacion='+params.prestacion;
	    url +='&cpbte_tipo='+params.cpbte_tipo;
	    url +='&cpbte_nro='+params.cpbte_nro;
	    url +='&cpbte_dia='+params.cpbte_dia;
	    url +='&cpbte_mes='+params.cpbte_mes;
	    url +='&cpbte_anio='+params.cpbte_anio;
	    url +='&cpbte_cuit='+params.cpbte_cuit;  
	    url +='&cpbte_sucursal='+params.cpbte_sucursal;
		url +='&cpbte_cuit_sucursal='+params.cpbte_cuit_sucursal;
	    url +='&cpbte_letra='+params.cpbte_letra;
	    url +='&fecha_prestacion_dia='+params.fecha_prestacion_dia;
	    url +='&fecha_prestacion_mes='+params.fecha_prestacion_mes;
	    url +='&fecha_prestacion_anio='+params.fecha_prestacion_anio;
	    url +='&tiponomnecladorprestacion='+params.tiponomnecladorprestacion;
	    url +='&tiponomenclador='+params.tiponomenclador;
	    url +='&idRegistro='+params.idRegistro;
	    url +='&id_medicamento_edit='+params.id_medicamento_edit;
	    url +='&nombre_medicamento_edit='+params.nombre_medicamento_edit;
	    url +='&codigoSeguimiento_filtro_edit='+params.codigoSeguimiento_filtro_edit;
	    url +='&descripcionSeguimiento_filtro_edit='+params.descripcionSeguimiento_filtro_edit;
	    url +='&nom_seleccionado_edit='+params.nom_seleccionado_edit;
	    url +='&tipoNomenclador_edit='+params.tipoNomenclador_edit;
		   
    
	   jQuery.ajax({   
		   url: url,
		   async: false,
		   success: function(data) {
			  var obj = jQuery.parseJSON(data);
				resp = obj.existe;
				mensajeErrorOut = obj.mensajeError;
				rtaExisteCompro=(resp  === 'true');
	   		}
	   }); 
	   if(rtaExisteCompro){
		  alert('Ya existe una prestación en esa fecha para el mismo comprobante');
		  respuesta=false;
		   
	   }
	   
	   if(mensajeErrorOut != ''){
		   alert(mensajeErrorOut);
		   respuesta=false;
	   }
	   
       return  respuesta;    
	 
}



function evaluarOnSectorListaEnCero() { 

	jQuery('#<portlet:namespace />cantprestacioneslista').val('0');
	document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo").selectedIndex=0;
	seteaControlesFacturacionDirecta(false);
	
	


}

function validarSiNumero(numero){	
	
	if (!/^([0-9])*$/.test(numero)  ){  //  Backspace, Delete keys
		return false 
	}else{
		return true 
	}	
}

function validaMonto(e, cantidad ){
	 
	tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla	
	patron= new RegExp("^[0-9]+(\.)?[\d{1,2}]$","gi");
	    
		te = String.fromCharCode(tecla);//convertimos el codigo ascii a string
		if (tecla==8 || tecla==46 || tecla==0) return true;
		return validarSiNumero(te);	
}
    

function ValidaDatosReclamo(){
	
	
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#<portlet:namespace />fechaComprobanteDia').val();
	var cpbte_mes =  jQuery('#<portlet:namespace />fechaComprobanteMes').val();
	var cpbte_anio = jQuery('#<portlet:namespace />fechaComprobanteAnio').val();

	var sector=jQuery('#<portlet:namespace />sector').val();

	var fecha_prestacion_dia='';
	var fecha_prestacion_mes='';
	var fecha_prestacion_anio='';
		
	    
	if (sector == 'FARMACIA'){
		 fecha_prestacion_dia=jQuery('#<portlet:namespace />fechaPrestacionDiaFarmacia').val(); 
		 fecha_prestacion_mes=jQuery('#<portlet:namespace />fechaPrestacionMesFarmacia').val();
		 fecha_prestacion_anio=jQuery('#<portlet:namespace />fechaPrestacionAnioFarmacia').val();
	}else{
		 fecha_prestacion_dia=jQuery('#<portlet:namespace />fechaPrestacionDia').val(); 
		 fecha_prestacion_mes=jQuery('#<portlet:namespace />fechaPrestacionMes').val();
		 fecha_prestacion_anio=jQuery('#<portlet:namespace />fechaPrestacionAnio').val();
	}
	
	var troquel= jQuery('#<portlet:namespace />troquel').val();
	var prestacion= jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val();    
	var tipoNomenclador =jQuery('#<portlet:namespace />nom_seleccionado').val();
	var tipoNomencladorPrestacion =jQuery('#<portlet:namespace />tiponomenclador').val();
	var baja =  jQuery('#<portlet:namespace />baja_fecha').val();

	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_reclamo';
		
	 url +='&cpbte_dia='+cpbte_dia;
	 url +='&cpbte_mes='+cpbte_mes;
	 url +='&cpbte_anio='+cpbte_anio;
	 url +='&fecha_prestacion_dia='+fecha_prestacion_dia;
	 url +='&fecha_prestacion_mes='+fecha_prestacion_mes;
	 url +='&fecha_prestacion_anio='+fecha_prestacion_anio;
	 url +='&troquel='+troquel;
	 url +='&prestacion='+prestacion;
	 url +='&tiponomenclador='+tipoNomenclador;
	 url +='&tiponomencladorprestacion='+tipoNomencladorPrestacion;
	 url +='&baja='+baja;
	    
	 jQuery.ajax({   
		   url: url,
		   async: false,
		   success: function(data) {
			  var obj = jQuery.parseJSON(data);
			  codError = obj.codError;
	   		}
	   }); 
		   
	   if(codError == '1'){
	       alert('La fecha de la prestación no puede ser posterior');
		   respuesta=false;	   
		}
		   
		if(codError == '2'){
		   	alert('La fecha del comprobante no puede ser posterior');
			respuesta=false;
		}
		if(codError == '4'){
		   	alert('No existe Prestación en el nomenclador');
			respuesta=false;
		 }
		if(codError == '5'){
		   	alert('No existe medicamento en el nomenclador');
			respuesta=false;
		 }	   
	    
		if(codError == '6'){
		   	alert('La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado');
			respuesta=false;
		}
	
	return  respuesta;    
		
}




function ValidaDatosReclamoEditar(){
	
	
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#<portlet:namespace />fechaComprobanteDiaEdicion').val();
	var cpbte_mes =  jQuery('#<portlet:namespace />fechaComprobanteMesEdicion').val();
	var cpbte_anio = jQuery('#<portlet:namespace />fechaComprobanteAnioEdicion').val();

	    

	fecha_prestacion_dia=jQuery('#<portlet:namespace />fechaPrestacionDiaEdicion').val(); 
	fecha_prestacion_mes=jQuery('#<portlet:namespace />fechaPrestacionMesEdicion').val();
	fecha_prestacion_anio=jQuery('#<portlet:namespace />fechaPrestacionAnioEdicion').val();
	
	
    var troquel= jQuery('#<portlet:namespace />troquel_edit').val();
    var prestacion= jQuery('#<portlet:namespace />codigoSeguimiento_filtro_edit').val();    
    var tipoNomenclador =jQuery('#<portlet:namespace />nom_seleccionado').val();
    var tipoNomencladorPrestacion =jQuery('#<portlet:namespace />tiponomenclador').val();
    var baja =  jQuery('#<portlet:namespace />baja_fecha').val();
    
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_reclamo';
		
	 url +='&cpbte_dia='+cpbte_dia;
	 url +='&cpbte_mes='+cpbte_mes;
	 url +='&cpbte_anio='+cpbte_anio;
	 url +='&fecha_prestacion_dia='+fecha_prestacion_dia;
	 url +='&fecha_prestacion_mes='+fecha_prestacion_mes;
	 url +='&fecha_prestacion_anio='+fecha_prestacion_anio;
	 url +='&troquel='+troquel;
	 url +='&prestacion='+prestacion;
	 url +='&tiponomenclador='+tipoNomenclador;
	 url +='&tiponomencladorprestacion='+tipoNomencladorPrestacion;
	 url +='&baja='+baja;
	    
	 jQuery.ajax({   
		   url: url,
		   async: false,
		   success: function(data) {
			  var obj = jQuery.parseJSON(data);
			  codError = obj.codError;
	   		}
	   }); 
		   
	   if(codError == '1'){
	       alert('La fecha de la prestación no puede ser posterior');
		   respuesta=false;	   
		}
		   
		if(codError == '2'){
		   	alert('La fecha del comprobante no puede ser posterior');
			respuesta=false;
		 }
		if(codError == '4'){
		   	alert('No existe Prestación en el nomenclador');
			respuesta=false;
		 }
		if(codError == '5'){
		   	alert('No existe medicamento en el nomenclador');
			respuesta=false;
		 }	   
	 
		if(codError == '6'){
		   	alert('La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado');
			respuesta=false;
		}
	
	return  respuesta;    
		
}




function validaMontosEdicion()
{	var strimporte =   jQuery('#<portlet:namespace />totalEdicion').val();
    var strcargoospim = jQuery('#<portlet:namespace />cargoospimEdicion').val();
    var strcargops =   jQuery('#<portlet:namespace />cargopsEdicion').val();

    var importedouble = parseFloat(jQuery('#<portlet:namespace />totalEdicion').val());
    var cargoospimdouble = parseFloat(jQuery('#<portlet:namespace />cargoospimEdicion').val());
    var cargopsdouble = parseFloat(jQuery('#<portlet:namespace />cargopsEdicion').val());
    var estado =jQuery("#<portlet:namespace/>estado").val();

    var importeFC = parseFloat(jQuery('#<portlet:namespace />importeFC').val());
    var importeFCEdicion = parseFloat(jQuery('#<portlet:namespace />importeFC_edicion').val());
    if(isNaN(importeFC)) {
//	jQuery('#<portlet:namespace />importeFC').val();
	   importeFC=0;
    }
    if(isNaN(importeFCEdicion)) {
 //	jQuery('#<portlet:namespace />importeFC_edicion').val();
	   importeFCEdicion=0;
    }



    if(isNaN(importedouble)) {		jQuery('#<portlet:namespace />totalEdicion').val()  ; importedouble=0; 	}
    if(isNaN(cargoospimdouble)) {	jQuery('#<portlet:namespace />cargoospimEdicion').val()  ; cargoospimdouble=0; 	}
    if(isNaN(cargopsdouble)) {		jQuery('#<portlet:namespace />cargopsEdicion').val()  ; cargopsdouble=0; 	}

    total= Math.round((cargoospimdouble + cargopsdouble) * 100) / 100 ;

//  valida la suma de los importes no debe superar el importe ingresado 

    if(total==0 && (importeFC>0 ||importeFCEdicion>0) && estado==3){
	   alert('Debe ingresar los importes en el Área Médica');
	   return false; 
    }

    if ( total > importedouble && estado==3){
	    alert('La suma de los importes ( OSPIM y PS ) no puede superar el monto en el importe ingresado.');
	    return false; 
    }

    if ( (total >importedouble || total<importedouble) && estado==3){
	    alert('La suma de los importes ( OSPIM y PS ) no puede diferir del monto en el total ingresado.');
	    return false; 
    }



    if ( document.getElementById("<portlet:namespace />tipopedido").selectedIndex==1) { // tipo de pedido excepcion 
	  if (total!=importedouble && estado==3){
		alert('El importe total de la prestación debe coincidir con la suma de Cargo Ospim mas a Cargo Ps');
		return false;
	  }
    }
	

	
	return true;
}
	
function ValidaMontos()
{
	var importeFC = parseFloat(jQuery('#<portlet:namespace />importeFC').val());
	var importedouble = parseFloat(jQuery('#<portlet:namespace />total').val());
	var cargoospimdouble = parseFloat(jQuery('#<portlet:namespace />cargoospim').val());
	var cargopsdouble = parseFloat(jQuery('#<portlet:namespace />cargops').val());
	var estado =jQuery("#<portlet:namespace/>estado").val();
	
	if(isNaN(importedouble)) {		jQuery('#<portlet:namespace />total').val()  ; importedouble=0; 	}
	if(isNaN(cargoospimdouble)) {	jQuery('#<portlet:namespace />cargoospim').val()  ; cargoospimdouble=0; 	}
	if(isNaN(cargopsdouble)) {		jQuery('#<portlet:namespace />cargops').val()  ; cargopsdouble=0; 	}
	if(isNaN(importeFC)) {		jQuery('#<portlet:namespace />importeFC').val()  ; importeFC=0; 	}
	
//	totalCargos= cargoospimdouble + cargopsdouble;
	totalCargos= Math.round((cargoospimdouble + cargopsdouble) * 100) / 100 ;
	
	if ( totalCargos >importeFC && estado=='3' ){
    	alert('La suma de los importes ( OSPIM y PS ) no puede superar el Importe de la Factura.');
    	return false; 
    }
	
	
	if ( (totalCargos >importedouble || totalCargos<importedouble) && estado=='3'){
    	alert('La suma de los importes ( OSPIM y PS ) no puede diferir del monto en el total ingresado.');
    	return false; 
    }
	
	if ( document.getElementById("<portlet:namespace />tipopedido").selectedIndex==1) { // tipo de pedido excepcion 
		if (totalCargos!=importedouble && estado=='3'){
			alert('El importe total de la prestación debe coincidir con la suma de Cargo Ospim mas Cargo Ps.');
			return false;
		}
	}
 
   if ( document.getElementById("<portlet:namespace />tipopedido").selectedIndex==2) { // tipo de pedido reintegro
	    if (importedouble <totalCargos && estado=='3'){
			alert('El importe total de prestación debe coincidir con la suma de a Cargo Ospim mas Cargo Ps');
			return false;
		}   
		if (totalCargos==0 && estado=='3'){
			alert(' la suma de a Cargo Ospim mas a Cargo Ps debe ser mayor que cero.');
			return false;
		}
   }
   return true;
}


function validarevision()
{
	if (jQuery('#<portlet:namespace/>cantrevisionesactivas').val()<1){ // no hay revisiones activas 
		alert('Debe tener registrada por lo menos una revision activa.');			
		resp=false;
	}
}

function convertToUppercase(el) {
	  if(!el || !el.value) return;
	  el.value = el.value.toUpperCase();
	}
	
function myXOR(a,b) {
	var resp;
	respa= (a>0 && b>0);
	return ( respa );
	}


function enterTecla(e){
	tecla = (document.all) ? e.keyCode : e.which;//obtenemos el codigo ascii de la tecla	
	if (tecla==13) {
		crit_busqueda();
	}else{
		jQuery('#<portlet:namespace />posforcie10').val(0);
	} 

}

function aplicaEstiloBordeRojoDatosObligatorio() { 
	// borde rojo en datos obligatorios
	color="#ff9999"
	jQuery("#<portlet:namespace />fechaospimMes").css("borderColor",color);
	jQuery("#<portlet:namespace />fechaospimAnio").css("borderColor",color);
	jQuery("#<portlet:namespace />fechaospimDia").css("borderColor",color);
	jQuery("#<portlet:namespace/>estado").css("borderColor",color);
	jQuery("#<portlet:namespace/>sector").css("borderColor",color);
	jQuery("#<portlet:namespace />tipopedido").css("borderColor",color);
	jQuery("#<portlet:namespace />fecharevisionMes").css("borderColor",color);
	jQuery("#<portlet:namespace />fecharevisionAnio").css("borderColor",color);
	jQuery("#<portlet:namespace />fecharevisionDia").css("borderColor",color);
	jQuery("#<portlet:namespace />resolucion").css("borderColor",color);
	jQuery("#<portlet:namespace />justificacionmedica").css("borderColor",color);
	jQuery("#<portlet:namespace />frecuencia").css("borderColor",color);
	jQuery("#<portlet:namespace />importe").css("borderColor",color);
	jQuery("#<portlet:namespace />mensajerevisionefectuada").css("borderColor",color);

}

function calculatotal(){

	importe=jQuery("#<portlet:namespace />importe").val();
	cantidad=jQuery("#<portlet:namespace />cantidad").val();
	total= importe * cantidad  ;
	jQuery("#<portlet:namespace />total").val(Math.round(total.toFixed(2) * 100)/100);

}

function seleccionaCamposCieDiez(codigo,descripcion ){
	jQuery('#<portlet:namespace />codigoCie').val(codigo);
	jQuery('#<portlet:namespace />detalleCie').val(descripcion);
	jQuery('#<portlet:namespace />codigoCie10').val(codigo);
}	

<%if (reclamoprestacional != null  &&   reclamoprestacional.getCodigoCie10()!=null &&  ! reclamoprestacional.getCodigoCie10().equals("")  ) {%>
<portlet:namespace />buscarCieCodigo(); 
<%}%>

function limpiaCamposBusquedaCieDiez(){
	jQuery('#<portlet:namespace />codigoCie10').val("");
}

function validaFacturacionDirectayReintegro(){
	document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo").selectedIndex=0;	
	jQuery('#<portlet:namespace />tipogestion').val(0);
	seteaControlesFacturacionDirecta(false);
	if (jQuery('#<portlet:namespace />montoPsPrestaciones').val()>0 && jQuery('#<portlet:namespace />montoPsPrestaciones').val()!="" ){// forzar facturacion directa o reintegro
		
		if (document.getElementById("<portlet:namespace />tipopedido").selectedIndex==1){ // excepcion 
			document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo").selectedIndex=2;
			jQuery('#<portlet:namespace />tipogestion').val(3); // facturacion directa 
			seteaControlesFacturacionDirecta(true);
		}	
	
	}
}




function seteaControlesFacturacionDirecta(estadoTrueFalse){
	document.getElementById("<portlet:namespace />incluido_convenio_gerenciadora").checked = estadoTrueFalse;
	document.getElementById("<portlet:namespace />debitoprestadora").checked =estadoTrueFalse;

}
function desactivaCheckCierre(){
	seteaControlesFacturacionDirecta(false);
	document.getElementById("<portlet:namespace />dosporciento").checked =false;
	document.getElementById("<portlet:namespace />dosporciento").disabled = true;
}




function abreAutorizacion(){
	
	 window.open('<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="tabs1" value="autorizaciones-prestacionales"/><portlet:param name="redirect" value="#"/></portlet:renderURL>',
	         'Autorizaciones', 'height=800, menubar=no, resizable=yes,scrollbars=yes, status=no, toolbar=no, width=1200');  
}

function calculatotalFC(){

	importe=jQuery("#<portlet:namespace />importeUnitarioFC").val();
	cantidad=jQuery("#<portlet:namespace />cantidadFC").val();
	total= importe * cantidad  ;
	jQuery("#<portlet:namespace />importeFC").val(Math.round(total.toFixed(2) * 100)/100);

}



function <portlet:namespace />emailPreCarga(id_reclamo){	

	var accion = "&<%= Constants.ACTION %>=" + "<%= WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL %>";
	
	
	 var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_seccional_entry" />'+
		'<liferay-portlet:param name="cmd" value="email"/>'+
		'<liferay-portlet:param name="id_reclamosel" value="__id_reclamosel"/>'+
		'<liferay-portlet:param name="tab_seleccionada" value="datos"/>'+

		'<liferay-portlet:param name="view" value="__view"/>'+
		'</liferay-portlet:renderURL>';
		
		
		 url = url.replace("__id_reclamosel",id_reclamo);
		 url = url + accion;

		 document.<portlet:namespace />reclamo_fm.method = 'post';
		 submitForm(document.<portlet:namespace />reclamo_fm, url); 
}

function <portlet:namespace />validarEmail() {
	var email = jQuery('#<portlet:namespace/>email').val();
/* 	var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
 */	
 
/*  Se solicito quitar el 24/05/2016
	if(trim(email).length == 0){
		alert("El campo Email es Obligatorio");
		jQuery("#<portlet:namespace />email").focus();
		return false;
	} */
	if(trim(email).length == 0){
		return true;
	}
	var expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	if ( !expr.test(email) ){
	    alert("Error: La dirección de correo " + email + " es incorrecta.");
	    jQuery("#<portlet:namespace />email").focus();
		return false;
	}
	    
	/* if(trim(email).length > 0){	
		if( !emailReg.test( email ) ) {
			jQuery("#<portlet:namespace />email").focus();
			return false;
		} else {
			return true;
		}
	}else{
		return false;
	} */
	return true;
}

function confirmaActualizacionDomicilioAfiliado(){

	var d_id_domicilio=jQuery("#<portlet:namespace/>id_domicilio").val();
    var d_id_provincia = jQuery("#<portlet:namespace/>provincia").val();
	var d_id_localidad = jQuery("#<portlet:namespace/>localidad").val();
	var d_calle = jQuery("#<portlet:namespace />calle").val();
	var d_numero = jQuery("#<portlet:namespace />numero").val();
	var d_piso = jQuery("#<portlet:namespace />piso").val();
	var d_dpto = jQuery("#<portlet:namespace />dpto").val();
	var d_cod_pos = jQuery("#<portlet:namespace />cod_postal").val();
	var d_barrio = jQuery("#<portlet:namespace />barrio").val();
	var d_cod_area_tel = jQuery("#<portlet:namespace />cod_area_telefono").val();
	var d_telefono = jQuery("#<portlet:namespace />telefono").val();
	//var d_cod_area_laboral = jQuery("#<portlet:namespace />cod_area_tel_laboral").val();
	//var d_laboral = jQuery("#<portlet:namespace />tel_laboral").val();
	var d_cod_area_celu = jQuery("#<portlet:namespace />cod_area_celular").val();
	var d_celular = jQuery("#<portlet:namespace />celular").val();
	
	var d_email = jQuery("#<portlet:namespace />email").val();
	var d_email_original = jQuery("#<portlet:namespace />email_original").val();
	
//	var cuiltitular= jQuery('#<portlet:namespace />cuil_titular').val();
	var cuiltitular= jQuery('#<portlet:namespace />cuil').val();
	var integrante = jQuery("#<portlet:namespace />inte").val();
	

	var idPar = jQuery("#<portlet:namespace />idPar").val();
	if (idPar != "<%= WebKeysAfiliados.PARENTESCO_DEFAULT %>" &&
	    idPar != "<%= WebKeysAfiliados.CONYUGE_DEFAULT %>" &&
	    idPar != "<%= WebKeysAfiliados.CONCUBINO_DEFAULT %>") {
	  integrante = 0;
	}
	
	/*validamos los campos obligatorios*/
	if (trim(d_calle).length == 0){
		alert("Ingrese la calle del domicilio");
		jQuery('#<portlet:namespace/>calle').focus();
		return false;
	}
	
	if (
		 (trim(d_cod_area_tel) == '' && trim(d_telefono) != '') ||
		 (trim(d_cod_area_tel) != '' && trim(d_telefono) == '')
		){
		alert("El teléfono debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />telefono').focus();
		return false;
	}
	
	if(trim(d_cod_area_tel).startsWith('0')){
		alert("El código de area del teléfono no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_telefono").focus();
		return false;
	}
	if(trim(d_telefono).startsWith('0')){
		alert("El número del teléfono no debe iniciar con cero");
		jQuery("#<portlet:namespace />telefono").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_tel).length>0 || trim(d_telefono).length>0){
		if(trim(d_cod_area_tel).length+trim(d_telefono).length!=10){
			alert("La longitud del código de área + teléfono debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_telefono").focus();
			return false;
		}
	}
/*	
	if ((trim(d_cod_area_laboral) == '' && trim(d_laboral) != '') ||
		(trim(d_cod_area_laboral) != '' && trim(d_laboral) == '')
		){
		alert("El teléfono laboral debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />tel_laboral').focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).startsWith('0')){
		alert("El código de area laboral no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_tel_laboral").focus();
		return false;
	}
	if(trim(d_laboral).startsWith('0')){
		alert("El número del teléfono laboral no debe iniciar con cero");
		jQuery("#<portlet:namespace />tel_laboral").focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).length>0 || trim(d_laboral).length>0){
		if(trim(d_cod_area_laboral).length+trim(d_laboral).length!=10){
			alert("La longitud del código de área + teléfono laboral debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_tel_laboral").focus();
			return false;
		}
	}
	
	*/
	
	if(trim(d_cod_area_celu).startsWith('0')){
		alert("El código de area del celular no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_celular").focus();
		return false;
	}
	if(trim(d_celular).startsWith('0')){
		alert("El número del celular no debe iniciar con cero");
		jQuery("#<portlet:namespace />celular").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_celu).length>0 || trim(d_celular).length>0){
		if(trim(d_cod_area_celu).length+trim(d_celular).length!=10){
			alert("La longitud del código de área + celular debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_celular").focus();
			return false;
		}
	}
	
	
	
	if(!<portlet:namespace />validarEmail()){
		return false;
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/actualiza_domicilio&id_parentesco=' + idPar;
	jQuery.post(url,{
					 cuil_titular:cuiltitular,
					 inte:integrante,	 
					 id_domicilio:d_id_domicilio,
					 id_provincia:d_id_provincia,
					 id_localidad:d_id_localidad,
					 calle:d_calle,
					 numero:d_numero,
					 piso:d_piso,
					 departamento:d_dpto,
					 codigo_postal:d_cod_pos,
					 barrio:d_barrio,
					 cod_area_telefono:d_cod_area_tel,
					 telefono:d_telefono,
					 //cod_area_laboral:d_cod_area_laboral,
					 //telefono_laboral:d_laboral,
					 cod_area_celular:d_cod_area_celu,
					 celular:d_celular,
					 email:d_email,
					 email_original:d_email_original,
					 cmd:'save'}, function() {																																											
			if(popupDomicilio!=null){
				jQuery("#<portlet:namespace />divResultadoActualizarOK").show();
				jQuery("#<portlet:namespace />divBotonActualizar").hide();
				Liferay.Popup.close(popupDomicilio); 
			}	 
		});
} 

function mostrarDomicilioAfiliado(){
	var cuil_titu= jQuery("#<portlet:namespace />cuil").val();
	var inte= jQuery("#<portlet:namespace />inte").val();
	var email;
	var actualizaDomicilio;
	
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliado_datos&cuil_titular=';
	   url += cuil_titu;
	   url += '&inte=' + inte;
		
 jQuery.ajax({   
 url: url,
 async:false,
 success: function(data){
	   var obj = jQuery.parseJSON(data);
	   email=obj.email;
	}});
	popupDomicilio= Liferay.Popup({title:"<liferay-ui:message key="detalle-domicilio" />",modal:true,width:950,height:330,fixedcenter:true});
	var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/actualiza_domicilio&cuil_titular='+cuil_titu+'&inte='+inte+'&cmd=view' +'&email='+encodeURI(email);
	jQuery(popupDomicilio).load(url1);
	
}
		
</script>

<style>
.seccionVerificarDomicilio {
  vertical-align: top;
  text-align: center;
  padding: 10px 5px;  
  border: none;
  box-shadow: none;
  background: transparent;
}


</style>
