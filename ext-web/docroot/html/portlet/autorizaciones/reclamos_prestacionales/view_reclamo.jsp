<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto" %>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include
	file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%
/*
 * Normaliza exclusivamente el handoff vÃ¡lido Compras -> Reclamo
 * Prestacional antes de que el ensamblado legacy resuelva el modo.
 *
 * En una navegaciÃ³n cross-portlet, el parÃ¡metro cmd=add puede convivir con
 * un atributo Constants.CMD distinto. El contexto validado por nonce,
 * usuario y vigencia es la evidencia necesaria para mantener este flujo en
 * alta y permitir que la botonera renderice Grabar.
 */
String cmdParametroCompras = ParamUtil.getString(
        request,
        Constants.CMD,
        ""
);
String origenReclamoCompras = ParamUtil.getString(
        request,
        "origen",
        ""
);
String nonceReclamoCompras = ParamUtil.getString(
        request,
        WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
        ""
);
Object contextoReclamoComprasObj = request.getSession().getAttribute(
        WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
);
ReclamoPrestacionalCompraContexto contextoReclamoCompras =
        contextoReclamoComprasObj instanceof ReclamoPrestacionalCompraContexto
        ? (ReclamoPrestacionalCompraContexto) contextoReclamoComprasObj
        : null;

boolean handoffReclamoComprasValido =
        Constants.ADD.equalsIgnoreCase(cmdParametroCompras)
        && "compras".equalsIgnoreCase(origenReclamoCompras)
        && contextoReclamoCompras != null
        && contextoReclamoCompras.coincideNonce(nonceReclamoCompras)
        && contextoReclamoCompras.perteneceAUsuario(
                user != null ? user.getScreenName() : ""
        )
        && contextoReclamoCompras.estaVigente(
                System.currentTimeMillis()
        );

if (handoffReclamoComprasValido) {
    request.setAttribute(
            Constants.CMD,
            Constants.ADD
    );
}
%>
<<<<<<< .mine
||||||| .r7295
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
	width: 250px;
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
	width: 250px;
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

   .alnright { text-align: right; }

span-fixed-size {
  display: inline-block;
  width: 20px;
}

</style>

<liferay-ui:error key="errorAfiliadoSinCobertMed"
	message="<%=(String)request.getAttribute(\"msgErrorAfiSinCobMed\") %>" />

<liferay-ui:error key="errorPrestacionComprobante"
	message="<%=(String)request.getAttribute(\"msgErrorPrestacionComprobante\") %>" />
	
<form name="<portlet:namespace />reclamo_fm"
	id="<portlet:namespace />reclamo_fm">
	
	
<div id="<portlet:namespace />global"
		align="left"
		style="width:75%;">	
	
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

	<fieldset class="cabeceraCaso"> 
		<legend>
			<liferay-ui:message key="Cabecera Caso" />
		</legend>

		<!-- DS -->
		
		
		<table >
		<tr>
				<td width="10%" ><label><liferay-ui:message key="Fecha Ospim" />
									:&nbsp;</label></td>
							<%if(reclamoprestacional == null) {%>
							<td width="33%"  ><liferay-ui:input-date dayParam="fechaospimDia"
									dayValue="<%= fechadia.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaospimMes"
									monthValue="<%= fechadia.get(Calendar.MONTH )%>"
									monthNullable="<%= true %>" yearParam="fechaospimAnio"
									yearValue="<%= fechadia.get(Calendar.YEAR)%>"
									yearRangeStart="<%= fechaospim.get(Calendar.YEAR)-5  %>"
									yearRangeEnd="<%= fechaospim.get(Calendar.YEAR)  %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaospim.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion  %>" /></td>
							<%}else{ %>
							<td width="33%" ><liferay-ui:input-date dayParam="fechaospimDia"
									dayValue="<%= fechaospim.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaospimMes"
									monthValue="<%= fechaospim.get(Calendar.MONTH )%>"
									monthNullable="<%= true %>" yearParam="fechaospimAnio"
									yearValue="<%= fechaospim.get(Calendar.YEAR)%>"
									yearRangeStart="<%= fechaospim.get(Calendar.YEAR) - 5 %>"
									yearRangeEnd="<%= fechaospim.get(Calendar.YEAR) + 1 %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaospim.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%} %>
							<td colspan="1"><label>&nbsp;&nbsp;&nbsp;</label></td>
							
							<td>
							<td   width="10%"><label><liferay-ui:message key="Fecha Seccional" />:&nbsp;</label></td>
							<%if(reclamoprestacional == null || nofechaseccional) {%>
							<td width="33%  style="text-align: left;"><liferay-ui:input-date
									dayParam="fechaseccionalDia" dayValue=""
									dayNullable="<%=true %>" monthParam="fechaseccionalMes"
									monthValue="-1" monthNullable="<%= true %>"
									yearParam="fechaseccionalAnio" yearValue=""
									yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
									yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)  %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%}else{ %>
							<td c width="33%"><liferay-ui:input-date
									dayParam="fechaseccionalDia"
									dayValue="<%= fechaseccional.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaseccionalMes"
									monthValue="<%= fechaseccional.get(Calendar.MONTH)%>"
									monthNullable="<%= true %>" yearParam="fechaseccionalAnio"
									yearValue="<%= fechaseccional.get(Calendar.YEAR )%>"
									yearRangeStart="<%= fechaseccional.get(Calendar.YEAR) - 5 %>"
									yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR) + 1 %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%} %> 
						
							
							<td>&nbsp;</td>
										
							<td width="3%"><liferay-ui:message key="Amparo"/>:</td>
							<td>&nbsp;</td>
											
							 <td width="3%"><input type="checkbox"
									id="<portlet:namespace />chk_amparo"
									name="<portlet:namespace />chk_amparo"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isAmparo()  ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br>
							 </td> 
							 <td>&nbsp;</td>
							<td width="2%"><label><liferay-ui:message key="Lote" />: </label></td>
							 <td>&nbsp;</td>
							<td width="15%"><input id="<portlet:namespace />nroLote"
								name="<portlet:namespace />nroLote" size="8" maxlength="9"
								type="text"
								value="<%=reclamoprestacional==null || reclamoprestacional.getNroLote()==null
	    						|| reclamoprestacional.getNroLote()==0?"":reclamoprestacional.getNroLote() %>"
								readonly="readonly" /></td>
							</td>
				
							<td width="37%" >&nbsp;&nbsp;&nbsp;</td>
							
						<td>
							<div class="divheaderNroReclamo">
								<label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label>
							</div>
							<div  class="divheaderNroOP">
								<label><b><%= opAsignadaalReclamo %></b></label> 							
							</div>
						</td>
				
				<tr>
				<tr>
					<td></td>
				</tr>
					
		</table>
		<br>
		<table>
				<tr>
							<td colspan="2"><label><liferay-ui:message key="Tipo Pedido" />:&nbsp;&nbsp;</label></td>
							<td><table>
									<select <% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />tipopedido"
										id="<portlet:namespace />tipopedido"
										onchange="cambioTipoPedido();manejarTipoPedidoCierre();"
										onblur="manejarTipoPedido();">
										<option value="SELECCIONAR">SELECCIONAR</option>
										<option value=EXCEPCION
											<%=Validator.isNotNull(reclamoprestacional) && Validator.isNotNull(reclamoprestacional.getTipoPedido())  && reclamoprestacional.getTipoPedido().equals("EXCEPCION") ? "selected" : ""  %>>EXCEPCIÓN</option>
										<option value="REINTEGRO"
											<%=Validator.isNotNull(reclamoprestacional)  && Validator.isNotNull(reclamoprestacional.getTipoPedido())  &&  reclamoprestacional.getTipoPedido().equals("REINTEGRO") ? "selected" : ""  %>>REINTEGRO</option>
										<option value="EXTRACAPITA"
											<%=Validator.isNotNull(reclamoprestacional) && Validator.isNotNull(reclamoprestacional.getTipoPedido())  && reclamoprestacional.getTipoPedido().equals("EXTRACAPITA") ? "selected" : ""  %>>EXTRACÁPITA</option>
									</select>
								</table></td>
							
							<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
							
							<td colspan="2"><label><liferay-ui:message key="Sector" />:&nbsp;&nbsp;</label></td>
							<td><table> 
									<select <% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace/>sector"
										id="<portlet:namespace />sector"
										onchange="manejarTipoSector();">
										
										<option value="" <%= (reclamoprestacional == null || Validator.isNull(reclamoprestacional.getSector())) ? "selected" : "" %>>-- SELECCIONAR --</option>

							            <option value="DISCAPACIDAD"
							                <%= "DISCAPACIDAD".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                DISCAPACIDAD
							            </option>
							
							            <option value="PRESTACIONES MEDICAS"
							                <%= "PRESTACIONES MEDICAS".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                PRESTACIONES MÉDICAS
							            </option>
							
							            <option value="FARMACIA"
							                <%= "FARMACIA".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                FARMACIA
							            </option>
							
							            <option value="LEGALES"
							                <%= "LEGALES".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                LEGALES
							            </option>
							
							            <%-- 
							            <option value="LIQUIDACIONES"
							                <%= "LIQUIDACIONES".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                LIQUIDACIONES
							            </option>
							            --%>
							
							            <option value="ODONTOLOGIA"
							                <%= "ODONTOLOGIA".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                ODONTOLOGIA
							            </option>
            
									</select>
								</table></td>

								<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
								<td><label id="integracion_label"  style="display:none"><liferay-ui:message key="integracion"  />:&nbsp;&nbsp;</label></td>
								<td><select name="<portlet:namespace/>integracion" id="<portlet:namespace/>integracion"
									<% if (!esEdicion) { %> disabled='disabled' <%}%> style15"display:none"  >												
										<option value="0">Seleccione Integración</option>
											<% for (ReclamosPrestacionalesIntegracion integracion : listaIntegracion) { %>
												<option
													<%=reclamoprestacional != null  && reclamoprestacional.getCodigoIntegracion() == integracion.getId() ? "selected" : ""  %>
													value="<%= integracion.getId() %>"><%=integracion.getDescripcion()%>
												</option>
												<% } %>
												
								</select>
								</td>
								<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
								
								<td>								
								<div id="integracion_div"  style="display:none">
									<img id="integracion_desc"   height='16'  width='16'  src='/html/themes/classic/images/common/help.png' title=''   />
									</div>
								</td>
							<td>
							<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td colspan="1"><label><liferay-ui:message key="Estado" />: &nbsp;</label></td>
							<td><table>
									<tr>
										<td><select <% if (!esEdicion) { %> disabled='disabled'
											<%}%> name="<portlet:namespace/>estado"
											onchange="controlarEstadoCerrado();"
											id="<portlet:namespace/>estado">

												<option value="-1">SELECCIONE</option>
												<% for (EstadosReclamosPrestacionales estados : listaestados) { %>
													<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.ADD) &&  estados.getId()==0){ %>
	
													<% } else{%>
													
														<option
															<%= reclamoprestacional != null  && reclamoprestacional.getEstado() == estados.getId() ? "selected" : ""  %>
															value="<%= estados.getId() %>"><%=estados.getDescripcion()%>
														</option>												
													<% } %>
												<% } %>

										</select></td>
										
									</tr>
																	
								</table></td>
						</tr>												
		</table>

		<br>
		<%-- Observacion como Textarea %>
		<%-- 
		<table class="lfr-table"
			style="border-collapse: separate; border-spacing: 0px;">
			<tr>
				<td colspan="8"><liferay-ui:message key="observacion" />:</td>
				<td><textarea rows="2" cols="150"
						disabled='disabled'
						id="<portlet:namespace />estadoObservacion" maxlength="250"
						name="<portlet:namespace />estadoObservacion"><%=reclamoprestacional != null && reclamoprestacional.getEstadoObservacion() != null
					? reclamoprestacional.getEstadoObservacion() : ""%>
														</textarea></td>
			</tr>
		</table>
		--%>
		
		<table >
			<%-- Observacion como Fieldset--%>
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
							<%-- class="span-fixed-size" --%>
								<td><span 
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
		
			<tr>
				<td>
					<!-- DS -->
					<table 
						style="border-collapse: separate; border-spacing: 3px;">
						<tr>
							<!--  													
							    <td>
							    <div class="divheaderNroReclamo">		     
							    <label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label>
							    </div>
							    <div class="divheaderNroOP">		     
							    <label><b><liferay-ui:message key="<%= opAsignadaalReclamo %>" /></b></label>
							    </div>
							    <% if(idPreautorizacion!=null && idPreautorizacion!=0){ %>
							      <br>
							      <div>		     
							       <span style="font-size: 9pt; color: green; "><label><b>Preautorizacion: <%= idPreautorizacion %></b></label></span>
							      </div>
							    <%}%>
							    
							    </td>
							    
							    <td></td>
							-->
							</tr>					
							<tr>			       	
						</tr>
					</table>
				</td>
				
				
				<!-- DS -->
				<td>
<!-- 					<div class="divheaderNroReclamo"> -->
<%-- 						<label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label> --%>
<!-- 					</div> -->
<!-- 					<div  class="divheaderNroOP"> -->
<%-- 						<label><b><%= opAsignadaalReclamo %></b></label> 							 --%>
<!-- 					</div> -->

						<!-- <div class='<%=divcheckbox%>'>-->
						<!--  table class="lfr-table">-->
						<!--     <tr><td>&nbsp;</td></tr>
							<tr>
								<td>&nbsp;</td>
								<%-- <td><input type="checkbox"
									id="<portlet:namespace />chk_amparo"
									name="<portlet:namespace />chk_amparo"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isAmparo()  ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br>
								</td> --%>
								<!-- <td>&nbsp;</td>
								<td><liferay-ui:message key="Amparo" /><br></td> -->
						<!-- 	</tr>-->
<!-- 							<tr><td>&nbsp;</td></tr> -->
<!-- 							<tr> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><input type="checkbox" -->
<%-- 									id="<portlet:namespace />chk_superintendencia" --%>
<%-- 									name="<portlet:namespace />chk_superintendencia" --%>
<%-- 									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isSuperintendencia()   ? "checked" : "Unchecked" %> --%>
<%-- 									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br> --%>
<!-- 								</td> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><liferay-ui:message key="Superintendencia" /><br></td> -->

<!-- 							</tr> -->
<!--							<tr><td>&nbsp;</td></tr>-->
<!-- 							<tr> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><input type="checkbox" -->
<%-- 									id="<portlet:namespace />chk_recuperable" --%>
<%-- 									name="<portlet:namespace />chk_recuperable" --%>
<%-- 									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isRecuperable()   ? "checked" : "Unchecked" %> --%>
<%-- 									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br> --%>
<!-- 								</td> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><liferay-ui:message key="Recuperable" /><br></td> -->
<!-- 							</tr> -->
							<!-- tr>
								<td>&nbsp;</td>
								<td><input type="checkbox"
									id="<portlet:namespace />chk_entramite"
									name="<portlet:namespace />chk_entramite"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isEntramite()   ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /></td>
								<td>&nbsp;</td>
								<td><liferay-ui:message key="Beneficiario en trámite" /></td>
							</tr-->
					

						<!-- </table> -->
					<!-- </div> -->
					<%if (reclamo_vinculado) {%>
					<div class="divNroRecord_Vinculado">
						<liferay-ui:message key="Asociado al Reclamo Nro :" /><%=caso_vinculado%>
					</div> <%}%> <% if(idPreautorizacion!=null && idPreautorizacion!=0){ %> <br>
					<div>
						<span style="font-size: 9pt; color: green;"><label><b>Preautorizaci&oacute;n:
									<%= idPreautorizacion %></b></label></span>
					</div> <%}%>

				</td>

				<!-- </td> -->
			</tr>
		</table>
		<!-- DS -->
	</fieldset> <!-- Cabecera del caso -->

	<table class="cabeceraCaso">
	<tr>
							<td>
								<fieldset class="block-labels">
									<legend>
										<liferay-ui:message key="datos-afiliado" />
									</legend>
									<liferay-util:include
										page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>

										<liferay-util:param name="edit_mode" value="<%=String.valueOf(esEdicion) %>" />
										<liferay-util:param name="discapacidad" value="<%= null %>" />
										<liferay-util:param name="pag_reintegro" value="<%= String.valueOf(true) %>" />
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
				          <td>&nbsp;</td>
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
	
	<table class="lfr-table"
		style="border-collapse: separate; border-spacing: 0px;">
		<tr>
			<td>
				<fieldset class="cabeceraCaso">
					<legend>
						<liferay-ui:message key="datos-cie-diez" />
					</legend>
					<liferay-util:include
						page='/html/portlet/autorizaciones/busqueda_ciediez.jsp'>
						<liferay-util:param name="edit_mode" value="<%=String.valueOf(esEdicion) %>" />
						<liferay-util:param name="codigo"
							value="<%=reclamoprestacional!=null?reclamoprestacional.getCodigoCie10():null%>" />
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
	</table>
	<br>
	<table class="lfr-table">
		<tr>
			<td colspan="8"><liferay-ui:message key="Diagnostico" />:</td>
			<td><textarea rows="2" cols="167" <% if (!esEdicion) { %>
					disabled='disabled' <%}%> id="<portlet:namespace />diagnostico"
					maxlength="250" name="<portlet:namespace />diagnostico"><%=reclamoprestacional!=null && reclamoprestacional.getDiagnosticoAfiliado() !=null ?reclamoprestacional.getDiagnosticoAfiliado() :""%></textarea>
			</td>
		</tr>
	</table>


	<fieldset class="lfr-table"> 
	
		<legend>
			<liferay-ui:message key="Datos de la Prestación" />
		</legend>

		<div id="<portlet:namespace />busqueda_farmacia" align="left" width="80%">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 3px;">
				<tr>
					<td colspan="15"><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;F. Prestación: </label></td>
					<td colspan="15"><liferay-ui:input-date
							dayParam="fechaPrestacionDiaFarmacia" dayValue=""
					dayNullable="<%=true%>"
					monthParam="fechaPrestacionMesFarmacia" monthValue="-1"
					monthNullable="<%=true%>"
							yearParam="fechaPrestacionAnioFarmacia" yearValue=""
							yearNullable="<%=true%>"
							yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 5%>"
							yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR) %>"
							firstDayOfWeek="" disabled="<%= !esEdicion %>" /></td>
					<td colspan="4"><liferay-util:include
							page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
							<liferay-util:param name="search_url"
								value="/autorizaciones/buscar_medicamentos" />
							<liferay-util:param name="troquel" value='' />
							<liferay-util:param name="nombre_medicamento" value='' />
							<liferay-util:param name="id_medicamento" value='' />
							<liferay-util:param name="esEditable" value='true' />
							<liferay-util:param name="mostrar_con_presentacion" value='true' />
						</liferay-util:include></td>


				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>

			</table>
		</div>

		<div id="<portlet:namespace />busqueda_prestaciones" align="left" width="80%">
			<table width="75%">
				<tr>
					<td ><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;F. Prestación: </label></td>
					<td ><liferay-ui:input-date dayParam="fechaPrestacionDia" dayValue="" dayNullable="<%=true%>"
					monthParam="fechaPrestacionMes" monthValue="-1" monthNullable="<%=true%>"
					yearParam="fechaPrestacionAnio" yearValue="" yearNullable="<%=true%>"
							yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 5%>"
							yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR)%>"
							firstDayOfWeek="" disabled="<%= !esEdicion %>" /></td>


					<td><label><liferay-ui:message key="codigo-presentado" />:</label></td>
					<td><input id="<portlet:namespace />codigoSeguimiento_filtro"
						name="<portlet:namespace />codigoSeguimiento_filtro" size="10"
						maxlength="20" type="text" value='' /></td>
					<td><input
						id="<portlet:namespace />descripcionSeguimiento_filtro"
						name="<portlet:namespace />descripcionSeguimiento_filtro"
						size="60" maxlength="200" type="text" value='' /></td>
					<td><div id="<portlet:namespace />divBtnBusca">
							<a href="javascript: void(0);"
								onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();"
								tabindex="-1">Buscar</a> <a href="javascript: void(0);"
								onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();"
								tabindex="-1">Limpiar</a>
						</div></td>
				</tr>		
				<tr>
					<td>&nbsp;</td>
				</tr>

				
			</table>
		</div>


		<div id="<portlet:namespace />datos_edicion_prestacion" align="left" width="95%">
          <table width="95%;"><tr><td>
			<span><b>Prestaci&oacute;n en Proceso de Edici&oacute;n.</b></span>
			<liferay-util:include
				page="/html/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp">
			</liferay-util:include>
		  </td></tr></table>	
		</div>

		<div id="<portlet:namespace />datos_prestacion_ingreso">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 2px; width: 85%;">
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
											style="border-collapse: separate; border-spacing: 3px;" >
											<tr>
												<td><label><liferay-ui:message key="Frecuencia" />:</label></td>
												<td><select <% if (!esEdicion) { %> disabled='disabled'
													<%}%> name="<portlet:namespace />frecuencia"
													id="<portlet:namespace />frecuencia">
														<option value="SELECCIONE">SELECCIONE</option>
														<option value="UNICA">UNICA</option>
														<option value="SEMANAL">SEMANAL</option>
														<option value="TRIMESTRAL">TRIMESTRAL</option>
														<option value="MENSUAL">MENSUAL</option>
														<option value="SEMESTRAL">SEMESTRAL</option>
														<option value="ANUAL">ANUAL</option>
												</select></td>

												<td><label><liferay-ui:message
															key="comprobante" />:</label></td>
												<td><select name="<portlet:namespace />comprobante_tipo"
													id="<portlet:namespace />comprobante_tipo"
													<% if (!esEdicion) { %> disabled="disabled" <%} %>>
														<option value="FCP">FCP</option>
														<option value="RCB">RCB</option>
														<option value="OTR">OTRO</option>
														<!--  <option value="AUT">AUTORIZACION</option> -->
												</select></td>

												<td><label><liferay-ui:message key="letra" />:</label></td>
												<td colspan="3"><select
													name="<portlet:namespace />comprobante_letra"
													id="<portlet:namespace />comprobante_letra">	
												</select></td>
												<td>Suc:</td>
												<td><input id="<portlet:namespace />comprobante_suc"
													name="<portlet:namespace />comprobante_suc" size="5"
													maxlength="5" 
													onkeydown="allowOnlyDigits(event);"
													type="text" value="" <% if (!esEdicion) { %>
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
														yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
														yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR)%>"
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
									<td colspan="15"><liferay-util:include
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
								</tr>

								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td><label><liferay-ui:message key="Cantidad" />:</label>
									</td>
									<td><input id="<portlet:namespace />cantidadFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />cantidadFC" size="8" maxlength="9"
										type="text" value="1" onblur="calculatotalFC()" /></td>

									<td><label><liferay-ui:message key="Importe" />:</label></td>
									<td><input id="<portlet:namespace />importeUnitarioFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />importeUnitarioFC" size="8"
										maxlength="20" value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										onblur="calculatotalFC()" /></td>


									<td><label>Total Comprobante:</label></td>
									<td><input id="<portlet:namespace />importeFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />importeFC" size="8" maxlength="20"
										value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										readonly="readonly" /></td>
								</tr>
							</table>
						</fieldset>

					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>

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
									<td><input id="<portlet:namespace />cantidad"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />cantidad" size="8" maxlength="9"
										type="text" value="1" onblur="calculatotal()" /></td>

									<td><label><liferay-ui:message key="Importe" />:</label>
									</td>
									<td><input id="<portlet:namespace />importe"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />importe" size="8" maxlength="20"
										value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										onblur="calculatotal()" /></td>

									<td><label><liferay-ui:message key="Total" />:</label></td>
									<td><input id="<portlet:namespace />total"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />total" size="8" maxlength="20"
										value='' type="text" readonly="readonly" /></td>

									<td><label><liferay-ui:message key="Cargo OSPIM" />:</label>
									</td>
									<td><input id="<portlet:namespace />cargoospim"
										name="<portlet:namespace />cargoospim" size="8" maxlength="20"
										value='' <% if (!esEdicion) { %> disabled='disabled' <%}%>
										type="text" value=""
										onkeydown="allowOnlyDigitsAndDecimals(event)" /></td>
									<td><label><liferay-ui:message key="Cargo Prestadora" />:</label>
									</td>
									<td><input id="<portlet:namespace />cargops"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />cargops" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>
									<td><label>Cargo Monotributo:</label>
									<td><input id="<portlet:namespace />cargoimesa"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />cargoimesa" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>
										
									<td><label>Reconocido SSS:</label>
									</td>
									<td><input id="<portlet:namespace />reconocidoSSS"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />reconocidoSSS" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>	
										
									<td>Recuperable:</label> 
									
									<select name="<portlet:namespace />recuperable_sur" id="<portlet:namespace />recuperable_sur"
									 <% if (!esEdicion) { %> disabled="disabled" <%} %> onchange="cambiorecuperable();">
														<option value="0">Seleccione</option>
														<option value="1">SUR</option>
														<option value="3">Integración</option>
														<option value="2">NO Recuperable</option>
									</select>
									

									</tr>
							</table>
						</fieldset>
					</td>
				</tr>
          </table>
          <table class="lfr-table">
				<tr>
					<td colspan="8"><liferay-ui:message key="observacion" />:</td>
					<td><textarea rows="3" cols="100" <% if (!esEdicion) { %>
							disabled='disabled' <%}%>
							id="<portlet:namespace />observacion_prestacion" maxlength="250"
							name="<portlet:namespace />observacion_prestacion"></textarea> <br>
						<b><liferay-ui:message
								key="La observación de 200 caracteres como máximo." /></b></td>
					<td colspan="12">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td><input type="button"
						value="<liferay-ui:message key="add-prestacion-reclamo" />"
						onClick="<portlet:namespace />agregarPrestacion();"
						id="<portlet:namespace />buttonaddprestacion"
						name="<portlet:namespace />buttonaddprestacion"
						title="<liferay-ui:message key="add-prestacion-reclamo" />" /></td>
					<%if (reclamo_vinculado) {%>
					<td><input type="button"
						value="<liferay-ui:message key="Ver Prestaciones del Caso Asociado." />"
						onClick="<portlet:namespace />verprestacionesasociadas();"
						id="<portlet:namespace />botonprestacionesasociadas"
						name="<portlet:namespace />botonprestacionesasociadas"
						title="<liferay-ui:message key="Ver Prestaciones del Caso Asociado." />" />
					</td>
					<%}%>
				</tr>
			</table>

		</div>

		<table>
			<tr>
				<td><span id="<portlet:namespace />CantidadDePrestacionesDelReclamo" style="color: red;"></span></td>
			</tr>
		</table>

       <table style="align:left; width:75%;">
		<tr>
		<td colspan="10">
	   <div id="<portlet:namespace />lista_prestaciones_reclamos"
			style="
        max-width: 1100px;
        max-height: 180px;
        overflow-x: auto;
        overflow-y: auto;
        border: 1px solid #ccc;
        border-radius: 6px;
        background: #fff;">
			<liferay-util:include
							page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamos.jsp">
			</liferay-util:include>
						
		</div>
		</td>
		</tr>
		</table>

	</fieldset> <!-- Fin Datos de la Prestacion -->
	

	<div id="<portlet:namespace />lista_prestaciones_asociadas"
		align="center"
		style="height: 120px; overflow: scroll; overflow-x: hidden;">
		<span style="background-color: #d4d9d5; font-size: 135%"><b><%= caso_vinculado%>.</b>

		</span>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamoasociado.jsp"></liferay-util:include>
	</div>

	<%-- 	<input type="hidden" name="<portlet:namespace />estadosel"
		id="<portlet:namespace />estadosel"
		value="<%=Validator.isNotNull(reclamoprestacional)  ? reclamoprestacional.getEstado() : "0"  %>" /> --%>
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
		id="<portlet:namespace />caso_vinculado" value="" />


	<fieldset class="block-labels">


		<table class="cabeceraCaso"
			style="border-collapse: separate; border-spacing: 3px;">
			<tr>
				<td colspan="4">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Contactos-Afiliado-Reclamo" />
						</legend>
						<table>
							<tr>
								<td><b><label id="CantidadDeContactosAsociados">
											Ningún Contacto Asociado. </label></b></td>
								<td><input type="button"
									value="Ver Contactos Asociados al Caso."
									onClick="<portlet:namespace />vercontactosdelreclamo();"
									id="<portlet:namespace />botoncontactosreclamo"
									title="<liferay-ui:message key="Ver Contactos Asociados al Caso." />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td style="display: none;">

					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Recuperable" />
						</legend>
						<table>
							<tr>
								<td style="display: none;"><input type="button"
									value="Buscar Solicitud SUR"
									title="<liferay-ui:message key="Buscar Solicitud SUR" />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
				<td style="display: none;">

					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Orden de Pago" />
						</legend>
						<table>
							<tr>
								<td style="display: none;"><input type="button"
									value="Buscar OP Reclamo"
									title="<liferay-ui:message key="Buscar OP Reclamo" />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
		</table>

		<div id="<portlet:namespace />lista_contactos_reclamo" align="center"
			style="height: 160px; overflow: scroll; overflow-x: hidden;">

			<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_contactos_reclamo.jsp">
			</liferay-util:include>
		</div>

		<fieldset class="block-labels">
			<legend>
				<liferay-ui:message key="rev" />
			</legend>
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 3px;">
				<tr>
					<td><label><liferay-ui:message key="fecha-revision" />
							:</label></td>
					<td><liferay-ui:input-date dayParam="fecharevisionDia"
							dayValue="<%= fechadia.get(Calendar.DATE)%>"
							dayNullable="<%=true %>" monthParam="fecharevisionMes"
							monthValue="<%= fechadia.get(Calendar.MONTH )%>"
							monthNullable="<%= true %>" yearParam="fecharevisionAnio"
							yearValue="<%= fechadia.get(Calendar.YEAR)%>"
							yearRangeStart="<%= fecharevision.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= fecharevision.get(Calendar.YEAR)  %>"
							yearNullable="<%= true %>"
							firstDayOfWeek="<%= fecharevision.getFirstDayOfWeek() - 1 %>"
							disabled="<%= !esEdicion %>" /></td>

					<td><label><liferay-ui:message key="responresolucion" />:</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<portlet:namespace/>respresolucion"
						id="<portlet:namespace />respresolucion">
							<option value="SELECCIONE">SELECCIONE</option>
							<option value="AUDITORIA ADMINISTRATIVA">AUDITORIA ADMINISTRATIVA</option>
							<option value="AUDITORIA DE PRESTACIONES MEDICAS">AUDITORIA DE PRESTACIONES MEDICAS</option>
							<option value="AUDITORIA FARMACEUTICA">AUDITORIA FARMACEUTICA</option>
							<option value="AUDITORIA MEDICA">AUDITORIA MEDICA</option>
							<option value="AUTORIZADO O.S.">AUTORIZADO O.S.</option>
							<option value="AUDITORIA ODONTOLOGICA">AUDITORIA ODONTOLOGICA</option>
							<option value="COMISION DIRECTIVA">COMISION DIRECTIVA</option>
							<option value="DIRIGENTES">DIRIGENTES</option>
							<option value="EQUIPO INTERDISCIPLINARIO">EQUIPO INTERDISCIPLINARIO</option>
							<option value="GERENCIADORA">GERENCIADORA</option>
							<option value="LEGALES">LEGALES</option>
					</select></td>
				</tr>
				<tr>
					<td><label><liferay-ui:message key="Presentes" />:</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<portlet:namespace/>presentes"
						id="<portlet:namespace />presentes">
							<option value="SELECCIONE">SELECCIONE</option>
							<option value="AUDITORIA MEDICA">AUDITORIA MEDICA</option>
							<option value="COMISION DIRECTIVA">COMISION DIRECTIVA</option>
							<option value="EQUIPO INTERDISCIPLINARIO">EQUIPO INTERDISCIPLINARIO</option>
							<option value="GERENCIADORA">GERENCIADORA</option>
					</select></td>
					<td><label><liferay-ui:message key="resolucion" /> :</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<portlet:namespace />resolucion"
						id="<portlet:namespace />resolucion"
						onchange="cambioresolucion();">
							<option value="Seleccione">SELECCIONE</option>
							<option value="AUTORIZADO">AUTORIZADO</option>
							<option value="RECHAZADO">RECHAZADO</option>
					</select></td>
				</tr>
					
				<tr>
					<td><liferay-ui:message key="observacion" />:</td>
					<td><textarea rows="3" cols="100"
							id="<portlet:namespace />observacion_revision" maxlength="200"
							name="<portlet:namespace />observacion_revision"></textarea> <br>
						<b><liferay-ui:message
								key="La observación de 200 caracteres como máximo." /></b></td>
					<td>
						<div id="<portlet:namespace />botonrevision">
							<input type="button"
								value="<liferay-ui:message key="agregar-revision"  />"
								onClick="<portlet:namespace />agregarRevision();"
								title="<liferay-ui:message key="agregar-revision" />" />
						</div>
					</td>
				</tr>
			</table>
			<div id="<portlet:namespace />lista_revisiones" align="center"
				style="height: 120px; overflow: scroll; overflow-x: hidden;">
				<table>
					<tr>
						<td colspan="10"><liferay-util:include
								page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_revisiones_reclamo.jsp">
							</liferay-util:include></td>
					</tr>
				</table>
				<label align='center'
					id="<portlet:namespace/>mensajerevisionefectuada"></label>
			</div>
		</fieldset>


		<table align="center" class="lfr-table"
			style="border-collapse: separate; border-spacing: 3px;">
			<tr>
				<td colspan="5">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Justificación Médica" />
						</legend>
						<textarea name="<portlet:namespace />justificacionmedcica_reclamo"
							id="<portlet:namespace />justificacionmedcica_reclamo" rows="4"
							cols="80" onkeyup="convertToUppercase(this)"
							onchange="validarevision();"
							<% if (!esEdicion ||( reclamoprestacional!=null && reclamoprestacional.getTipo_gestion_cierre_reclamo()>0 ) ) { %>
							disabled='disabled' <%}%>
							id="<portlet:namespace />justificacionmedcica_reclamo"
							name="<portlet:namespace />justificacionmedcica_reclamo">
				<%=reclamoprestacional!=null && reclamoprestacional.getJustificaconMedica()!=null ?reclamoprestacional.getJustificaconMedica() :""%></textarea>
					</fieldset>
				</td>
				<td colspan="5">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Dictamen Comisión" />
						</legend>
						<textarea rows="4" cols="70" onkeyup="convertToUppercase(this)"
							<% if (!esEdicion ||( reclamoprestacional!=null && reclamoprestacional.getTipo_gestion_cierre_reclamo()>0 ) )  { %>
							disabled='disabled' <%}%>
							id="<portlet:namespace />dictamencomision_reclamo"
							name="<portlet:namespace />dictamencomision_reclamo">
				<%=reclamoprestacional!=null && reclamoprestacional.getDictamenComision()!=null ?reclamoprestacional.getDictamenComision() :""%></textarea>
					</fieldset>
				</td>
			</tr>
		</table>

		<table align="center" class="lfr-table"
			style="border-collapse: separate; border-spacing: 3px; columns: 3;">
			<tr>
				<td>
					<div id="<portlet:namespace />Cierre_Reclamo_Div">
						<fieldset class="block-labels">

							<legend>
								<liferay-ui:message key="Cierre Reclamo" />
							</legend>
							<table class="lfr-table"
								style="border-collapse: separate; border-spacing: 3px;">
								<tr>
									<%if(reclamoprestacional == null) {%>
									<td colspan="2"><label><liferay-ui:message
												key="Fecha Cierre" />:</label> <liferay-ui:input-date
											dayParam="fechacierreDia"
											dayValue="<%= fechacierre.get(Calendar.DATE)%>"
											dayNullable="<%=true %>" monthParam="fechacierreMes"
											monthValue="<%= fechacierre.get(Calendar.MONTH )%>"
											monthNullable="<%= true %>" yearParam="fechacierreAnio"
											yearValue="<%= fechacierre.get(Calendar.YEAR)%>"
											yearRangeStart="<%= fechacierre.get(Calendar.YEAR) - 5 %>"
											yearRangeEnd="<%= fechacierre.get(Calendar.YEAR)  %>"
											yearNullable="<%= true %>"
											firstDayOfWeek="<%= fechacierre.getFirstDayOfWeek() - 1 %>"
											disabled="<%= !esEdicion %>" /></td>
									<%}else{ %>
									<td colspan="2"><label><liferay-ui:message
												key="Fecha Cierre" />:</label> <liferay-ui:input-date
											dayParam="fechacierreDia"
											dayValue="<%= fechacierre.get(Calendar.DATE)%>"
											dayNullable="<%=true %>" monthParam="fechacierreMes"
											monthValue="<%= fechacierre.get(Calendar.MONTH )%>"
											monthNullable="<%= true %>" yearParam="fechacierreAnio"
											yearValue="<%= fechacierre.get(Calendar.YEAR)%>"
											yearRangeStart="<%= fechacierre.get(Calendar.YEAR) - 5  %>"
											yearRangeEnd="<%= fechacierre.get(Calendar.YEAR)  %>"
											yearNullable="<%= true %>"
											firstDayOfWeek="<%= fechacierre.getFirstDayOfWeek() - 1 %>"
											disabled="<%= !esEdicion %>" /></td>
									<%} %>
									<td colspan="2"><label><liferay-ui:message
												key="Tipo Gestion" />:</label> <select   <% if (!esEdicion) { %>
										disabled='disabled' <%}%>												
										name="<portlet:namespace/>tipo_gestion_cierre_reclamo"
										id="<portlet:namespace/>tipo_gestion_cierre_reclamo"
										onchange="manejartipogestion();">
											<option selected value="0">SELECCIONE LA GESTION</option>
											<% for (TiposDeGestionReclamosPrestacionales tipogestion  : listatipogestionreclamos) { %>
											<option
												value="<%=tipogestion.getId()%>"><%=tipogestion.getDescripcion()%>
											</option>
											<% } %>
									</select></td>	
									<tr id="<portlet:namespace/>observacion_medica_tr" style="display:none;">
									
											<td><label><liferay-ui:message key="observaciones-area-medica"/>:</label></td>
											<td><select name="<portlet:namespace/>observacion_medica" id="<portlet:namespace/>observacion_medica"
												<% if (!esEdicion) { %> disabled='disabled' <%}%>>												
													<option value="0">Seleccione observación</option>
														<% for (ReclamosPrestacionalesRevisionEstado  revisionEstado  : listaRevisionEstado) { %>
															<option
																value="<%=revisionEstado.getId()%>"><%=revisionEstado.getDescripcion()%>
													   		</option>
													   	<%} %>
											</select></td>
									</tr>
														
						
								<tr>
								</tr>
								<tr>
									<td colspan="1"><liferay-ui:message key="observacion" />:
									</td>
									<td colspan="3"><textarea rows="3" cols="50"
											id="<portlet:namespace />reclamo_observacion_cierre"
											maxlength="200"
											name="<portlet:namespace />reclamo_observacion_cierre"><%=Validator.isNotNull(reclamoprestacional) &&   Validator.isNotNull(reclamoprestacional.getReclamo_observacion_cierre()) ? reclamoprestacional.getReclamo_observacion_cierre():"" %></textarea>
									</td>
								</tr>

			


							</table>


							<table align="center" width="600px">

								<tr>
									<td width="600px">&nbsp;</td>
								</tr>

								<tr>
									<td width="170px"><liferay-ui:message
											key="Incluido Convenio con Gerenciadora" />: <input
										type="checkbox"
										id="<portlet:namespace />incluido_convenio_gerenciadora"
										name="<portlet:namespace />incluido_convenio_gerenciadora"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isReclamo_convenio_gerenciadora()  ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>

									<td width="320px"><liferay-ui:message key="2 % UOMA" />:
										<input type="checkbox" id="<portlet:namespace />dosporciento"
										name="<portlet:namespace />dosporciento"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isDosPorciento()   ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>

									<td width="210px"><label>Débito Prestadora:</label> <input
										type="checkbox" id="<portlet:namespace />debitoprestadora"
										name="<portlet:namespace />debitoprestadora"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isDebitoPrestadora()    ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>
								</tr>
							</table>

						</fieldset>
					</div>
				</td>
				<td>

					<fieldset class="block-labels">

						<legend>
							<liferay-ui:message key="Datos de la OP" />
						</legend>

						<table class="lfr-table"
							style="border-collapse: separate; border-spacing: 3px;">
							<% if (opAsignadaalReclamoExiste){ %>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de lista:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getId_lista_reintegro()>0  ? reclamoprestacional.getId_lista_reintegro() : ""  %></td>
							</tr>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de OP:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getIdOP()>0 ? reclamoprestacional.getIdOP() : ""  %></td>
							</tr>
							<%if (reclamoprestacional.getChequeOP()!=null){ %>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de cheque:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getChequeOP()!= null ? reclamoprestacional.getChequeOP(): ""  %></td>
							</tr>
							<%}else{ %>
								<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de Cuenta:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getCtaNro()!= 0 ? reclamoprestacional.getCtaNro(): ""  %></td>
							</tr>
							<%} %>
							
							<tr style="font-size: 110%">
								<td colspan="1"><b>Fecha OP:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getfechaOPAsString()   %></td>
							</tr>
							<%}else{%>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Sin Orden de Pago.</b></td>
							</tr>
							<%}%>

						</table>
				</td>
				</fieldset>
			</tr>
		</table>

		<br />
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
		<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.EDIT)) {%>
		<div id="<portlet:namespace />botoneditareclamo" align="center"
			style="height: 80px; overflow-x: hidden;">
			<table>
				<tr>
					<td><input type="button"
						value="<liferay-ui:message key="Actualizar" />"
						onClick="<portlet:namespace />editaReclamo(false);"
						title="<liferay-ui:message key="Actualiza los Datos Ingresados." />" />
					</td>				
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<%-- Si es Pendiente y NO ES ReadOnly, no muestra las opcion para volver a Recarga --%>
					<% if ((PuedeObservar) && (!showReadOnlyReclamPrestac)) {%>						
						<td><input type="button"
							value="<liferay-ui:message key="Observar" />"
							onClick="<portlet:namespace />volverEstadoObservado();"
							title="<liferay-ui:message key="Cambia a Estado Observado." />" />
						</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<% }%>
					<td><input type="button"
						value="<liferay-ui:message key="Imprimir Datos" />"
						onClick="<portlet:namespace />imprimirReclamo();"
						title="<liferay-ui:message key="Imprimir Registro del Caso." />" />
					</td>
				</tr>
			</table>
		</div>
		<%} %>
		<% if( reclamoprestacional!=null 
		&& showABMButtons == true 
		&& reclamoprestacional.getEstado()== 3  && reclamoprestacional.getIdOP() == 0
		&& !reclamoprestacional.isMarcaReabrirReclamo()) { // cerrado sin OP %>

		<div id="<portlet:namespace />boton_rollback" align="center"
			style="height: 80px; overflow-x: hidden;">
			<table>
				<tr>
					<td><input type="button"
						value="<liferay-ui:message key="rollback-reclamo" />"
						onClick="<portlet:namespace />reabrirReclamo(false);"
						title="<liferay-ui:message key="Reabre Registro del Caso." />" />
					</td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				</tr>
			</table>
		</div>
		<%}%>

		<%if(reclamoprestacional != null){ %>
		<div align="center">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 5px;">
				<tr>
					<td colspan="4"></hr></td>
				</tr>
				<tr>
					<td colspan="4">
						<div align="center" id="<portlet:namespace />rp_auditoria">
							<table style="font-size: 8">
								<tr>
									<td><label><liferay-ui:message key="Alta Usuario" />:</label></td>
									<td><%=reclamoprestacional.getAlta_usr()!=null? reclamoprestacional.getAlta_usr():""%></td>
									<td><label><liferay-ui:message
												key="crm-contacto-alta-fec" />:</label></td>
									<td><%=sdf2.format(reclamoprestacional.getAlta_fecha()) %></td>
									<td><label><liferay-ui:message key="Modi Usuario" />:</label></td>
									<td><%=reclamoprestacional.getModi_usr()!=null?reclamoprestacional.getModi_usr():"" %></td>
									<td><label><liferay-ui:message
												key="crm-contacto-modi-fec" />:</label></td>
									<td><%=reclamoprestacional.getModi_usr()!=null? sdf2.format(reclamoprestacional.getModi_fecha()) :"" %></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<% } %>
		<input id="<portlet:namespace />tipoNomenclador"
			name="<portlet:namespace />tipoNomenclador" type="hidden" value="" />

		<div id='validarExistenciaCuit' style="float: right;"></div>
</div>		
</form>

=======
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
	width: 250px;
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
	width: 250px;
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

   .alnright { text-align: right; }

span-fixed-size {
  display: inline-block;
  width: 20px;
}

</style>

<liferay-ui:error key="errorAfiliadoSinCobertMed"
	message="<%=(String)request.getAttribute(\"msgErrorAfiSinCobMed\") %>" />

<liferay-ui:error key="errorPrestacionComprobante"
	message="<%=(String)request.getAttribute(\"msgErrorPrestacionComprobante\") %>" />
	
<form name="<portlet:namespace />reclamo_fm"
	id="<portlet:namespace />reclamo_fm">
	
	
<div id="<portlet:namespace />global"
		align="left"
		style="width:75%;">	
	
	<input
	    type="hidden"
	    id="<portlet:namespace/>plan_reclamo_bloqueado"
	    name="<portlet:namespace/>plan_reclamo_bloqueado"
	    value="0"
	/>
	
	<input
	    type="hidden"
	    id="<portlet:namespace/>nombre_plan_reclamo_bloqueado"
	    name="<portlet:namespace/>nombre_plan_reclamo_bloqueado"
	    value=""
	/>

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

	<fieldset class="cabeceraCaso"> 
		<legend>
			<liferay-ui:message key="Cabecera Caso" />
		</legend>

		<!-- DS -->
		
		
		<table >
		<tr>
				<td width="10%" ><label><liferay-ui:message key="Fecha Ospim" />
									:&nbsp;</label></td>
							<%if(reclamoprestacional == null) {%>
							<td width="33%"  ><liferay-ui:input-date dayParam="fechaospimDia"
									dayValue="<%= fechadia.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaospimMes"
									monthValue="<%= fechadia.get(Calendar.MONTH )%>"
									monthNullable="<%= true %>" yearParam="fechaospimAnio"
									yearValue="<%= fechadia.get(Calendar.YEAR)%>"
									yearRangeStart="<%= fechaospim.get(Calendar.YEAR)-5  %>"
									yearRangeEnd="<%= fechaospim.get(Calendar.YEAR)  %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaospim.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion  %>" /></td>
							<%}else{ %>
							<td width="33%" ><liferay-ui:input-date dayParam="fechaospimDia"
									dayValue="<%= fechaospim.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaospimMes"
									monthValue="<%= fechaospim.get(Calendar.MONTH )%>"
									monthNullable="<%= true %>" yearParam="fechaospimAnio"
									yearValue="<%= fechaospim.get(Calendar.YEAR)%>"
									yearRangeStart="<%= fechaospim.get(Calendar.YEAR) - 5 %>"
									yearRangeEnd="<%= fechaospim.get(Calendar.YEAR) + 1 %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaospim.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%} %>
							<td colspan="1"><label>&nbsp;&nbsp;&nbsp;</label></td>
							
							<td>
							<td   width="10%"><label><liferay-ui:message key="Fecha Seccional" />:&nbsp;</label></td>
							<%if(reclamoprestacional == null || nofechaseccional) {%>
							<td width="33%  style="text-align: left;"><liferay-ui:input-date
									dayParam="fechaseccionalDia" dayValue=""
									dayNullable="<%=true %>" monthParam="fechaseccionalMes"
									monthValue="-1" monthNullable="<%= true %>"
									yearParam="fechaseccionalAnio" yearValue=""
									yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
									yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)  %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%}else{ %>
							<td c width="33%"><liferay-ui:input-date
									dayParam="fechaseccionalDia"
									dayValue="<%= fechaseccional.get(Calendar.DATE)%>"
									dayNullable="<%=true %>" monthParam="fechaseccionalMes"
									monthValue="<%= fechaseccional.get(Calendar.MONTH)%>"
									monthNullable="<%= true %>" yearParam="fechaseccionalAnio"
									yearValue="<%= fechaseccional.get(Calendar.YEAR )%>"
									yearRangeStart="<%= fechaseccional.get(Calendar.YEAR) - 5 %>"
									yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR) + 1 %>"
									yearNullable="<%= true %>"
									firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
									disabled="<%= !esEdicion %>" /></td>
							<%} %> 
						
							
							<td>&nbsp;</td>
										
							<td width="3%"><liferay-ui:message key="Amparo"/>:</td>
							<td>&nbsp;</td>
											
							 <td width="3%"><input type="checkbox"
									id="<portlet:namespace />chk_amparo"
									name="<portlet:namespace />chk_amparo"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isAmparo()  ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br>
							 </td> 
							 <td>&nbsp;</td>
							<td width="2%"><label><liferay-ui:message key="Lote" />: </label></td>
							 <td>&nbsp;</td>
							<td width="15%"><input id="<portlet:namespace />nroLote"
								name="<portlet:namespace />nroLote" size="8" maxlength="9"
								type="text"
								value="<%=reclamoprestacional==null || reclamoprestacional.getNroLote()==null
	    						|| reclamoprestacional.getNroLote()==0?"":reclamoprestacional.getNroLote() %>"
								readonly="readonly" /></td>
							</td>
				
							<td width="37%" >&nbsp;&nbsp;&nbsp;</td>
							
						<td>
							<div class="divheaderNroReclamo">
								<label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label>
							</div>
							<div  class="divheaderNroOP">
								<label><b><%= opAsignadaalReclamo %></b></label> 							
							</div>
						</td>
				
				<tr>
				<tr>
					<td></td>
				</tr>
					
		</table>
		<br>
		<table>
				<tr>
							<td colspan="2"><label><liferay-ui:message key="Tipo Pedido" />:&nbsp;&nbsp;</label></td>
							<td><table>
									<select <% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />tipopedido"
										id="<portlet:namespace />tipopedido"
										onchange="cambioTipoPedido();manejarTipoPedidoCierre();manejartipogestion();"
										onblur="manejarTipoPedido();">
										<option value="SELECCIONAR">SELECCIONAR</option>
										<option value=EXCEPCION
											<%=Validator.isNotNull(reclamoprestacional) && Validator.isNotNull(reclamoprestacional.getTipoPedido())  && reclamoprestacional.getTipoPedido().equals("EXCEPCION") ? "selected" : ""  %>>EXCEPCIÓN</option>
										<option value="REINTEGRO"
											<%=Validator.isNotNull(reclamoprestacional)  && Validator.isNotNull(reclamoprestacional.getTipoPedido())  &&  reclamoprestacional.getTipoPedido().equals("REINTEGRO") ? "selected" : ""  %>>REINTEGRO</option>
										<option value="EXTRACAPITA"
											<%=Validator.isNotNull(reclamoprestacional) && Validator.isNotNull(reclamoprestacional.getTipoPedido())  && reclamoprestacional.getTipoPedido().equals("EXTRACAPITA") ? "selected" : ""  %>>EXTRACÁPITA</option>
									</select>
								</table></td>
							
							<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
							
							<td colspan="2"><label><liferay-ui:message key="Sector" />:&nbsp;&nbsp;</label></td>
							<td><table> 
									<select <% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace/>sector"
										id="<portlet:namespace />sector"
										onchange="manejarTipoSector();">
										
										<option value="" <%= (reclamoprestacional == null || Validator.isNull(reclamoprestacional.getSector())) ? "selected" : "" %>>-- SELECCIONAR --</option>

							            <option value="DISCAPACIDAD"
							                <%= "DISCAPACIDAD".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                DISCAPACIDAD
							            </option>
							
							            <option value="PRESTACIONES MEDICAS"
							                <%= "PRESTACIONES MEDICAS".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                PRESTACIONES MÉDICAS
							            </option>
							
							            <option value="FARMACIA"
							                <%= "FARMACIA".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                FARMACIA
							            </option>
							
							            <option value="LEGALES"
							                <%= "LEGALES".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                LEGALES
							            </option>
							
							            <%-- 
							            <option value="LIQUIDACIONES"
							                <%= "LIQUIDACIONES".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                LIQUIDACIONES
							            </option>
							            --%>
							
							            <option value="ODONTOLOGIA"
							                <%= "ODONTOLOGIA".equals(reclamoprestacional != null ? reclamoprestacional.getSector() : "") ? "selected" : "" %>>
							                ODONTOLOGIA
							            </option>
            
									</select>
								</table></td>

								<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
								<td><label id="integracion_label"  style="display:none"><liferay-ui:message key="integracion"  />:&nbsp;&nbsp;</label></td>
								<td><select name="<portlet:namespace/>integracion" id="<portlet:namespace/>integracion"
									<% if (!esEdicion) { %> disabled='disabled' <%}%> style15"display:none"  >												
										<option value="0">Seleccione Integración</option>
											<% for (ReclamosPrestacionalesIntegracion integracion : listaIntegracion) { %>
												<option
													<%=reclamoprestacional != null  && reclamoprestacional.getCodigoIntegracion() == integracion.getId() ? "selected" : ""  %>
													value="<%= integracion.getId() %>"><%=integracion.getDescripcion()%>
												</option>
												<% } %>
												
								</select>
								</td>
								<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
								
								<td>								
								<div id="integracion_div"  style="display:none">
									<img id="integracion_desc"   height='16'  width='16'  src='/html/themes/classic/images/common/help.png' title=''   />
									</div>
								</td>
							<td>
							<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td colspan="1"><label><liferay-ui:message key="Estado" />: &nbsp;</label></td>
							<td><table>
									<tr>
										<td><select <% if (!esEdicion) { %> disabled='disabled'
											<%}%> name="<portlet:namespace/>estado"
											onchange="controlarEstadoCerrado();"
											id="<portlet:namespace/>estado">

												<option value="-1">SELECCIONE</option>
												<% for (EstadosReclamosPrestacionales estados : listaestados) { %>
													<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.ADD) &&  estados.getId()==0){ %>
	
													<% } else{%>
													
														<option
															<%= reclamoprestacional != null  && reclamoprestacional.getEstado() == estados.getId() ? "selected" : ""  %>
															value="<%= estados.getId() %>"><%=estados.getDescripcion()%>
														</option>												
													<% } %>
												<% } %>

										</select></td>
										
									</tr>
																	
								</table></td>
						</tr>												
		</table>

		<br>
		<%-- Observacion como Textarea %>
		<%-- 
		<table class="lfr-table"
			style="border-collapse: separate; border-spacing: 0px;">
			<tr>
				<td colspan="8"><liferay-ui:message key="observacion" />:</td>
				<td><textarea rows="2" cols="150"
						disabled='disabled'
						id="<portlet:namespace />estadoObservacion" maxlength="250"
						name="<portlet:namespace />estadoObservacion"><%=reclamoprestacional != null && reclamoprestacional.getEstadoObservacion() != null
					? reclamoprestacional.getEstadoObservacion() : ""%>
														</textarea></td>
			</tr>
		</table>
		--%>
		
		<table >
			<%-- Observacion como Fieldset--%>
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
							<%-- class="span-fixed-size" --%>
								<td><span 
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
		
			<tr>
				<td>
					<!-- DS -->
					<table 
						style="border-collapse: separate; border-spacing: 3px;">
						<tr>
							<!--  													
							    <td>
							    <div class="divheaderNroReclamo">		     
							    <label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label>
							    </div>
							    <div class="divheaderNroOP">		     
							    <label><b><liferay-ui:message key="<%= opAsignadaalReclamo %>" /></b></label>
							    </div>
							    <% if(idPreautorizacion!=null && idPreautorizacion!=0){ %>
							      <br>
							      <div>		     
							       <span style="font-size: 9pt; color: green; "><label><b>Preautorizacion: <%= idPreautorizacion %></b></label></span>
							      </div>
							    <%}%>
							    
							    </td>
							    
							    <td></td>
							-->
							</tr>					
							<tr>			       	
						</tr>
					</table>
				</td>
				
				
				<!-- DS -->
				<td>
<!-- 					<div class="divheaderNroReclamo"> -->
<%-- 						<label><b><liferay-ui:message key="<%= nroreclamo %>" /></b></label> --%>
<!-- 					</div> -->
<!-- 					<div  class="divheaderNroOP"> -->
<%-- 						<label><b><%= opAsignadaalReclamo %></b></label> 							 --%>
<!-- 					</div> -->

						<!-- <div class='<%=divcheckbox%>'>-->
						<!--  table class="lfr-table">-->
						<!--     <tr><td>&nbsp;</td></tr>
							<tr>
								<td>&nbsp;</td>
								<%-- <td><input type="checkbox"
									id="<portlet:namespace />chk_amparo"
									name="<portlet:namespace />chk_amparo"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isAmparo()  ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br>
								</td> --%>
								<!-- <td>&nbsp;</td>
								<td><liferay-ui:message key="Amparo" /><br></td> -->
						<!-- 	</tr>-->
<!-- 							<tr><td>&nbsp;</td></tr> -->
<!-- 							<tr> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><input type="checkbox" -->
<%-- 									id="<portlet:namespace />chk_superintendencia" --%>
<%-- 									name="<portlet:namespace />chk_superintendencia" --%>
<%-- 									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isSuperintendencia()   ? "checked" : "Unchecked" %> --%>
<%-- 									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br> --%>
<!-- 								</td> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><liferay-ui:message key="Superintendencia" /><br></td> -->

<!-- 							</tr> -->
<!--							<tr><td>&nbsp;</td></tr>-->
<!-- 							<tr> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><input type="checkbox" -->
<%-- 									id="<portlet:namespace />chk_recuperable" --%>
<%-- 									name="<portlet:namespace />chk_recuperable" --%>
<%-- 									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isRecuperable()   ? "checked" : "Unchecked" %> --%>
<%-- 									<% if (!esEdicion) { %> disabled='disabled' <%}%> /> <br> --%>
<!-- 								</td> -->
<!-- 								<td>&nbsp;</td> -->
<!-- 								<td><liferay-ui:message key="Recuperable" /><br></td> -->
<!-- 							</tr> -->
							<!-- tr>
								<td>&nbsp;</td>
								<td><input type="checkbox"
									id="<portlet:namespace />chk_entramite"
									name="<portlet:namespace />chk_entramite"
									<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isEntramite()   ? "checked" : "Unchecked" %>
									<% if (!esEdicion) { %> disabled='disabled' <%}%> /></td>
								<td>&nbsp;</td>
								<td><liferay-ui:message key="Beneficiario en trámite" /></td>
							</tr-->
					

						<!-- </table> -->
					<!-- </div> -->
					<%if (reclamo_vinculado) {%>
					<div class="divNroRecord_Vinculado">
						<liferay-ui:message key="Asociado al Reclamo Nro :" /><%=caso_vinculado%>
					</div> <%}%> <% if(idPreautorizacion!=null && idPreautorizacion!=0){ %> <br>
					<div>
						<span style="font-size: 9pt; color: green;"><label><b>Preautorizaci&oacute;n:
									<%= idPreautorizacion %></b></label></span>
					</div> <%}%>

				</td>

				<!-- </td> -->
			</tr>
		</table>
		<!-- DS -->
	</fieldset> <!-- Cabecera del caso -->

	<table class="cabeceraCaso">
	<tr>
							<td>
								<fieldset class="block-labels">
									<legend>
										<liferay-ui:message key="datos-afiliado" />
									</legend>
									<liferay-util:include
										page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>

										<liferay-util:param name="edit_mode" value="<%=String.valueOf(esEdicion) %>" />
										<liferay-util:param name="discapacidad" value="<%= null %>" />
										<liferay-util:param name="pag_reintegro" value="<%= String.valueOf(true) %>" />
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
				          <td>&nbsp;</td>
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
	
	<table class="lfr-table"
		style="border-collapse: separate; border-spacing: 0px;">
		<tr>
			<td>
				<fieldset class="cabeceraCaso">
					<legend>
						<liferay-ui:message key="datos-cie-diez" />
					</legend>
					<liferay-util:include
						page='/html/portlet/autorizaciones/busqueda_ciediez.jsp'>
						<liferay-util:param name="edit_mode" value="<%=String.valueOf(esEdicion) %>" />
						<liferay-util:param name="codigo"
							value="<%=reclamoprestacional!=null?reclamoprestacional.getCodigoCie10():null%>" />
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
	</table>
	<br>
	<table class="lfr-table">
		<tr>
			<td colspan="8"><liferay-ui:message key="Diagnostico" />:</td>
			<td><textarea rows="2" cols="167" <% if (!esEdicion) { %>
					disabled='disabled' <%}%> id="<portlet:namespace />diagnostico"
					maxlength="250" name="<portlet:namespace />diagnostico"><%=reclamoprestacional!=null && reclamoprestacional.getDiagnosticoAfiliado() !=null ?reclamoprestacional.getDiagnosticoAfiliado() :""%></textarea>
			</td>
		</tr>
	</table>


	<fieldset class="lfr-table"> 
	
		<legend>
			<liferay-ui:message key="Datos de la Prestación" />
		</legend>

		<div id="<portlet:namespace />busqueda_farmacia" align="left" width="80%">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 3px;">
				<tr>
					<td colspan="15"><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;F. Prestación: </label></td>
					<td colspan="15"><liferay-ui:input-date
							dayParam="fechaPrestacionDiaFarmacia" dayValue=""
					dayNullable="<%=true%>"
					monthParam="fechaPrestacionMesFarmacia" monthValue="-1"
					monthNullable="<%=true%>"
							yearParam="fechaPrestacionAnioFarmacia" yearValue=""
							yearNullable="<%=true%>"
							yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 5%>"
							yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR) %>"
							firstDayOfWeek="" disabled="<%= !esEdicion %>" /></td>
					<td colspan="4"><liferay-util:include
							page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
							<liferay-util:param name="search_url"
								value="/autorizaciones/buscar_medicamentos" />
							<liferay-util:param name="troquel" value='' />
							<liferay-util:param name="nombre_medicamento" value='' />
							<liferay-util:param name="id_medicamento" value='' />
							<liferay-util:param name="esEditable" value='true' />
							<liferay-util:param name="mostrar_con_presentacion" value='true' />
						</liferay-util:include></td>


				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>

			</table>
		</div>

		<div id="<portlet:namespace />busqueda_prestaciones" align="left" width="80%">
			<table width="75%">
				<tr>
					<td ><label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;F. Prestación: </label></td>
					<td ><liferay-ui:input-date dayParam="fechaPrestacionDia" dayValue="" dayNullable="<%=true%>"
					monthParam="fechaPrestacionMes" monthValue="-1" monthNullable="<%=true%>"
					yearParam="fechaPrestacionAnio" yearValue="" yearNullable="<%=true%>"
							yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 5%>"
							yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR)%>"
							firstDayOfWeek="" disabled="<%= !esEdicion %>" /></td>


					<td><label><liferay-ui:message key="codigo-presentado" />:</label></td>
					<td><input id="<portlet:namespace />codigoSeguimiento_filtro"
						name="<portlet:namespace />codigoSeguimiento_filtro" size="10"
						maxlength="20" type="text" value='' /></td>
					<td><input
						id="<portlet:namespace />descripcionSeguimiento_filtro"
						name="<portlet:namespace />descripcionSeguimiento_filtro"
						size="60" maxlength="200" type="text" value='' /></td>
					<td><div id="<portlet:namespace />divBtnBusca">
							<a href="javascript: void(0);"
								onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();"
								tabindex="-1">Buscar</a> <a href="javascript: void(0);"
								onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();"
								tabindex="-1">Limpiar</a>
						</div></td>
				</tr>		
				<tr>
					<td>&nbsp;</td>
				</tr>

				
			</table>
		</div>


		<div id="<portlet:namespace />datos_edicion_prestacion" align="left" width="95%">
          <table width="95%;"><tr><td>
			<span><b>Prestaci&oacute;n en Proceso de Edici&oacute;n.</b></span>
			<liferay-util:include
				page="/html/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp">
			</liferay-util:include>
		  </td></tr></table>	
		</div>

		<div id="<portlet:namespace />datos_prestacion_ingreso">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 2px; width: 85%;">
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
											style="border-collapse: separate; border-spacing: 3px;" >
											<tr>
												<td><label><liferay-ui:message key="Frecuencia" />:</label></td>
												<td><select <% if (!esEdicion) { %> disabled='disabled'
													<%}%> name="<portlet:namespace />frecuencia"
													id="<portlet:namespace />frecuencia">
														<option value="SELECCIONE">SELECCIONE</option>
														<option value="UNICA">UNICA</option>
														<option value="SEMANAL">SEMANAL</option>
														<option value="TRIMESTRAL">TRIMESTRAL</option>
														<option value="MENSUAL">MENSUAL</option>
														<option value="SEMESTRAL">SEMESTRAL</option>
														<option value="ANUAL">ANUAL</option>
												</select></td>

												<td><label><liferay-ui:message
															key="comprobante" />:</label></td>
												<td><select name="<portlet:namespace />comprobante_tipo"
													id="<portlet:namespace />comprobante_tipo"
													<% if (!esEdicion) { %> disabled="disabled" <%} %>>
														<option value="FCP">FCP</option>
														<option value="RCB">RCB</option>
														<option value="OTR">OTRO</option>
														<!--  <option value="AUT">AUTORIZACION</option> -->
												</select></td>

												<td><label><liferay-ui:message key="letra" />:</label></td>
												<td colspan="3"><select
													name="<portlet:namespace />comprobante_letra"
													id="<portlet:namespace />comprobante_letra">	
												</select></td>
												<td>Suc:</td>
												<td><input id="<portlet:namespace />comprobante_suc"
													name="<portlet:namespace />comprobante_suc" size="5"
													maxlength="5" 
													onkeydown="allowOnlyDigits(event);"
													type="text" value="" <% if (!esEdicion) { %>
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
														yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
														yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)<fechadia.get(Calendar.YEAR)?fechadia.get(Calendar.YEAR):fechadia.get(Calendar.YEAR)%>"
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
									<td colspan="15"><liferay-util:include
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
								</tr>

								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td><label><liferay-ui:message key="Cantidad" />:</label>
									</td>
									<td><input id="<portlet:namespace />cantidadFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />cantidadFC" size="8" maxlength="9"
										type="text" value="1" onblur="calculatotalFC()" /></td>

									<td><label><liferay-ui:message key="Importe" />:</label></td>
									<td><input id="<portlet:namespace />importeUnitarioFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />importeUnitarioFC" size="8"
										maxlength="20" value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										onblur="calculatotalFC()" /></td>


									<td><label>Total Comprobante:</label></td>
									<td><input id="<portlet:namespace />importeFC"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />importeFC" size="8" maxlength="20"
										value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										readonly="readonly" /></td>
								</tr>
							</table>
						</fieldset>

					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>

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
									<td><input id="<portlet:namespace />cantidad"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />cantidad" size="8" maxlength="9"
										type="text" value="1" onblur="calculatotal()" /></td>

									<td><label><liferay-ui:message key="Importe" />:</label>
									</td>
									<td><input id="<portlet:namespace />importe"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />importe" size="8" maxlength="20"
										value='' type="text"
										onkeydown="allowOnlyDigitsAndDecimals(event)"
										onblur="calculatotal()" /></td>

									<td><label><liferay-ui:message key="Total" />:</label></td>
									<td><input id="<portlet:namespace />total"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />total" size="8" maxlength="20"
										value='' type="text" readonly="readonly" /></td>

									<td><label><liferay-ui:message key="Cargo OSPIM" />:</label>
									</td>
									<td><input id="<portlet:namespace />cargoospim"
										name="<portlet:namespace />cargoospim" size="8" maxlength="20"
										value='' <% if (!esEdicion) { %> disabled='disabled' <%}%>
										type="text" value=""
										onkeydown="allowOnlyDigitsAndDecimals(event)" /></td>
									<td><label><liferay-ui:message key="Cargo Prestadora" />:</label>
									</td>
									<td><input id="<portlet:namespace />cargops"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />cargops" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>
									<td><label>Cargo Monotributo:</label>
									<td><input id="<portlet:namespace />cargoimesa"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />cargoimesa" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>
										
									<td><label>Reconocido SSS:</label>
									</td>
									<td><input id="<portlet:namespace />reconocidoSSS"
										<% if (!esEdicion) { %> disabled='disabled' <%}%>
										name="<portlet:namespace />reconocidoSSS" size="8" maxlength="20"
										value='' onkeydown="allowOnlyDigitsAndDecimals(event)"
										type="text" value="" /></td>	
										
									<td>Recuperable:</label> 
									
									<select name="<portlet:namespace />recuperable_sur" id="<portlet:namespace />recuperable_sur"
									 <% if (!esEdicion) { %> disabled="disabled" <%} %> onchange="cambiorecuperable();">
														<option value="0">Seleccione</option>
														<option value="1">SURGE</option>
														<option value="3">Integración</option>
														<option value="2">NO Recuperable</option>
									</select>
									

									</tr>
							</table>
						</fieldset>
					</td>
				</tr>
          </table>
          <table class="lfr-table">
				<tr>
					<td colspan="8"><liferay-ui:message key="observacion" />:</td>
					<td><textarea rows="3" cols="100" <% if (!esEdicion) { %>
							disabled='disabled' <%}%>
							id="<portlet:namespace />observacion_prestacion" maxlength="250"
							name="<portlet:namespace />observacion_prestacion"></textarea> <br>
						<b><liferay-ui:message
								key="La observación de 200 caracteres como máximo." /></b></td>
					<td colspan="12">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td><input type="button"
						value="<liferay-ui:message key="add-prestacion-reclamo" />"
						onClick="<portlet:namespace />agregarPrestacion();"
						id="<portlet:namespace />buttonaddprestacion"
						name="<portlet:namespace />buttonaddprestacion"
						title="<liferay-ui:message key="add-prestacion-reclamo" />" /></td>
					<%if (reclamo_vinculado) {%>
					<td><input type="button"
						value="<liferay-ui:message key="Ver Prestaciones del Caso Asociado." />"
						onClick="<portlet:namespace />verprestacionesasociadas();"
						id="<portlet:namespace />botonprestacionesasociadas"
						name="<portlet:namespace />botonprestacionesasociadas"
						title="<liferay-ui:message key="Ver Prestaciones del Caso Asociado." />" />
					</td>
					<%}%>
				</tr>
			</table>

		</div>

		<table>
			<tr>
				<td><span id="<portlet:namespace />CantidadDePrestacionesDelReclamo" style="color: red;"></span></td>
			</tr>
		</table>

       <table style="align:left; width:75%;">
		<tr>
		<td colspan="10">
	   <div id="<portlet:namespace />lista_prestaciones_reclamos"
			style="
        max-width: 1100px;
        max-height: 180px;
        overflow-x: auto;
        overflow-y: auto;
        border: 1px solid #ccc;
        border-radius: 6px;
        background: #fff;">
			<liferay-util:include
							page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamos.jsp">
			</liferay-util:include>
						
		</div>
		</td>
		</tr>
		</table>

	</fieldset> <!-- Fin Datos de la Prestacion -->
	

	<div id="<portlet:namespace />lista_prestaciones_asociadas"
		align="center"
		style="height: 120px; overflow: scroll; overflow-x: hidden;">
		<span style="background-color: #d4d9d5; font-size: 135%"><b><%= caso_vinculado%>.</b>

		</span>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamoasociado.jsp"></liferay-util:include>
	</div>

	<%-- 	<input type="hidden" name="<portlet:namespace />estadosel"
		id="<portlet:namespace />estadosel"
		value="<%=Validator.isNotNull(reclamoprestacional)  ? reclamoprestacional.getEstado() : "0"  %>" /> --%>
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
		id="<portlet:namespace />caso_vinculado" value="" />


	<fieldset class="block-labels">


		<table class="cabeceraCaso"
			style="border-collapse: separate; border-spacing: 3px;">
			<tr>
				<td colspan="4">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Contactos-Afiliado-Reclamo" />
						</legend>
						<table>
							<tr>
								<td><b><label id="CantidadDeContactosAsociados">
											Ningún Contacto Asociado. </label></b></td>
								<td><input type="button"
									value="Ver Contactos Asociados al Caso."
									onClick="<portlet:namespace />vercontactosdelreclamo();"
									id="<portlet:namespace />botoncontactosreclamo"
									title="<liferay-ui:message key="Ver Contactos Asociados al Caso." />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td style="display: none;">

					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Recuperable" />
						</legend>
						<table>
							<tr>
								<td style="display: none;"><input type="button"
									value="Buscar Solicitud SUR"
									title="<liferay-ui:message key="Buscar Solicitud SUR" />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
				<td style="display: none;">

					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Orden de Pago" />
						</legend>
						<table>
							<tr>
								<td style="display: none;"><input type="button"
									value="Buscar OP Reclamo"
									title="<liferay-ui:message key="Buscar OP Reclamo" />">
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
		</table>

		<div id="<portlet:namespace />lista_contactos_reclamo" align="center"
			style="height: 160px; overflow: scroll; overflow-x: hidden;">

			<liferay-util:include page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_contactos_reclamo.jsp">
			</liferay-util:include>
		</div>

		<fieldset class="block-labels">
			<legend>
				<liferay-ui:message key="rev" />
			</legend>
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 3px;">
				<tr>
					<td><label><liferay-ui:message key="fecha-revision" />
							:</label></td>
					<td><liferay-ui:input-date dayParam="fecharevisionDia"
							dayValue="<%= fechadia.get(Calendar.DATE)%>"
							dayNullable="<%=true %>" monthParam="fecharevisionMes"
							monthValue="<%= fechadia.get(Calendar.MONTH )%>"
							monthNullable="<%= true %>" yearParam="fecharevisionAnio"
							yearValue="<%= fechadia.get(Calendar.YEAR)%>"
							yearRangeStart="<%= fecharevision.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= fecharevision.get(Calendar.YEAR)  %>"
							yearNullable="<%= true %>"
							firstDayOfWeek="<%= fecharevision.getFirstDayOfWeek() - 1 %>"
							disabled="<%= !esEdicion %>" /></td>

					<td><label><liferay-ui:message key="responresolucion" />:</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<portlet:namespace/>respresolucion"
						id="<portlet:namespace />respresolucion">
							<option value="SELECCIONE">SELECCIONE</option>
							<option value="AUDITORIA ADMINISTRATIVA">AUDITORIA ADMINISTRATIVA</option>
							<option value="AUDITORIA DE PRESTACIONES MEDICAS">AUDITORIA DE PRESTACIONES MEDICAS</option>
							<option value="AUDITORIA FARMACEUTICA">AUDITORIA FARMACEUTICA</option>
							<option value="AUDITORIA MEDICA">AUDITORIA MEDICA</option>
							<option value="AUTORIZADO O.S.">AUTORIZADO O.S.</option>
							<option value="AUDITORIA ODONTOLOGICA">AUDITORIA ODONTOLOGICA</option>
							<option value="COMISION DIRECTIVA">COMISION DIRECTIVA</option>
							<option value="DIRIGENTES">DIRIGENTES</option>
							<option value="EQUIPO INTERDISCIPLINARIO">EQUIPO INTERDISCIPLINARIO</option>
							<option value="GERENCIADORA">GERENCIADORA</option>
							<option value="LEGALES">LEGALES</option>
					</select></td>
				</tr>
				<tr>
					<td><label><liferay-ui:message key="Presentes" />:</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<portlet:namespace/>presentes"
						id="<portlet:namespace />presentes">
							<option value="SELECCIONE">SELECCIONE</option>
							<option value="AUDITORIA MEDICA">AUDITORIA MEDICA</option>
							<option value="COMISION DIRECTIVA">COMISION DIRECTIVA</option>
							<option value="EQUIPO INTERDISCIPLINARIO">EQUIPO INTERDISCIPLINARIO</option>
							<option value="GERENCIADORA">GERENCIADORA</option>
					</select></td>
					<td><label><liferay-ui:message key="resolucion" /> :</label></td>
					<td><select <% if (!esEdicion) { %> disabled='disabled' <%}%>
						name="<portlet:namespace />resolucion"
						id="<portlet:namespace />resolucion"
						onchange="cambioresolucion();">
							<option value="Seleccione">SELECCIONE</option>
							<option value="AUTORIZADO">AUTORIZADO</option>
							<option value="RECHAZADO">RECHAZADO</option>
					</select></td>
				</tr>
					
				<tr>
					<td><liferay-ui:message key="observacion" />:</td>
					<td><textarea rows="3" cols="100"
							id="<portlet:namespace />observacion_revision" maxlength="200"
							name="<portlet:namespace />observacion_revision"></textarea> <br>
						<b><liferay-ui:message
								key="La observación de 200 caracteres como máximo." /></b></td>
					<td>
						<div id="<portlet:namespace />botonrevision">
							<input type="button"
								value="<liferay-ui:message key="agregar-revision"  />"
								onClick="<portlet:namespace />agregarRevision();"
								title="<liferay-ui:message key="agregar-revision" />" />
						</div>
					</td>
				</tr>
			</table>
			<div id="<portlet:namespace />lista_revisiones" align="center"
				style="height: 120px; overflow: scroll; overflow-x: hidden;">
				<table>
					<tr>
						<td colspan="10"><liferay-util:include
								page="/html/portlet/autorizaciones/reclamos_prestacionales/lista_revisiones_reclamo.jsp">
							</liferay-util:include></td>
					</tr>
				</table>
				<label align='center'
					id="<portlet:namespace/>mensajerevisionefectuada"></label>
			</div>
		</fieldset>


		<table align="center" class="lfr-table"
			style="border-collapse: separate; border-spacing: 3px;">
			<tr>
				<td colspan="5">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Justificación Médica" />
						</legend>
						<textarea name="<portlet:namespace />justificacionmedcica_reclamo"
							id="<portlet:namespace />justificacionmedcica_reclamo" rows="4"
							cols="80" onkeyup="convertToUppercase(this)"
							onchange="validarevision();"
							<% if (!esEdicion ||( reclamoprestacional!=null && reclamoprestacional.getTipo_gestion_cierre_reclamo()>0 ) ) { %>
							disabled='disabled' <%}%>
							id="<portlet:namespace />justificacionmedcica_reclamo"
							name="<portlet:namespace />justificacionmedcica_reclamo">
				<%=reclamoprestacional!=null && reclamoprestacional.getJustificaconMedica()!=null ?reclamoprestacional.getJustificaconMedica() :""%></textarea>
					</fieldset>
				</td>
				<td colspan="5">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="Dictamen Comisión" />
						</legend>
						<textarea rows="4" cols="70" onkeyup="convertToUppercase(this)"
							<% if (!esEdicion ||( reclamoprestacional!=null && reclamoprestacional.getTipo_gestion_cierre_reclamo()>0 ) )  { %>
							disabled='disabled' <%}%>
							id="<portlet:namespace />dictamencomision_reclamo"
							name="<portlet:namespace />dictamencomision_reclamo">
				<%=reclamoprestacional!=null && reclamoprestacional.getDictamenComision()!=null ?reclamoprestacional.getDictamenComision() :""%></textarea>
					</fieldset>
				</td>
			</tr>
		</table>

		<table align="center" class="lfr-table"
			style="border-collapse: separate; border-spacing: 3px; columns: 3;">
			<tr>
				<td>
					<div id="<portlet:namespace />Cierre_Reclamo_Div">
						<fieldset class="block-labels">

							<legend>
								<liferay-ui:message key="Cierre Reclamo" />
							</legend>
							<table class="lfr-table"
								style="border-collapse: separate; border-spacing: 3px;">
								<tr>
									<%if(reclamoprestacional == null) {%>
									<td colspan="2"><label><liferay-ui:message
												key="Fecha Cierre" />:</label> <liferay-ui:input-date
											dayParam="fechacierreDia"
											dayValue="<%= fechacierre.get(Calendar.DATE)%>"
											dayNullable="<%=true %>" monthParam="fechacierreMes"
											monthValue="<%= fechacierre.get(Calendar.MONTH )%>"
											monthNullable="<%= true %>" yearParam="fechacierreAnio"
											yearValue="<%= fechacierre.get(Calendar.YEAR)%>"
											yearRangeStart="<%= fechacierre.get(Calendar.YEAR) - 5 %>"
											yearRangeEnd="<%= fechacierre.get(Calendar.YEAR)  %>"
											yearNullable="<%= true %>"
											firstDayOfWeek="<%= fechacierre.getFirstDayOfWeek() - 1 %>"
											disabled="<%= !esEdicion %>" /></td>
									<%}else{ %>
									<td colspan="2"><label><liferay-ui:message
												key="Fecha Cierre" />:</label> <liferay-ui:input-date
											dayParam="fechacierreDia"
											dayValue="<%= fechacierre.get(Calendar.DATE)%>"
											dayNullable="<%=true %>" monthParam="fechacierreMes"
											monthValue="<%= fechacierre.get(Calendar.MONTH )%>"
											monthNullable="<%= true %>" yearParam="fechacierreAnio"
											yearValue="<%= fechacierre.get(Calendar.YEAR)%>"
											yearRangeStart="<%= fechacierre.get(Calendar.YEAR) - 5  %>"
											yearRangeEnd="<%= fechacierre.get(Calendar.YEAR)  %>"
											yearNullable="<%= true %>"
											firstDayOfWeek="<%= fechacierre.getFirstDayOfWeek() - 1 %>"
											disabled="<%= !esEdicion %>" /></td>
									<%} %>
									<td colspan="2"><label><liferay-ui:message
												key="Tipo Gestion" />:</label> <select   <% if (!esEdicion) { %>
										disabled='disabled' <%}%>												
										name="<portlet:namespace/>tipo_gestion_cierre_reclamo"
										id="<portlet:namespace/>tipo_gestion_cierre_reclamo"
										onchange="manejartipogestion();">
											<option selected value="0">SELECCIONE LA GESTION</option>
											<% for (TiposDeGestionReclamosPrestacionales tipogestion  : listatipogestionreclamos) { %>
											<option
												value="<%=tipogestion.getId()%>"><%=tipogestion.getDescripcion()%>
											</option>
											<% } %>
									</select></td>	
									<tr id="<portlet:namespace/>observacion_medica_tr" style="display:none;">
									
											<td><label><liferay-ui:message key="observaciones-area-medica"/>:</label></td>
											<td><select name="<portlet:namespace/>observacion_medica" id="<portlet:namespace/>observacion_medica"
												    <% if (!esEdicion) { %> disabled="disabled" <% } %>>										
												    <option value="0">Seleccione observación</option>
												</select></td>
									</tr>
														
						
								<tr>
								</tr>
								<tr>
									<td colspan="1"><liferay-ui:message key="observacion" />:
									</td>
									<td colspan="3"><textarea rows="3" cols="50"
											id="<portlet:namespace />reclamo_observacion_cierre"
											maxlength="200"
											name="<portlet:namespace />reclamo_observacion_cierre"><%=Validator.isNotNull(reclamoprestacional) &&   Validator.isNotNull(reclamoprestacional.getReclamo_observacion_cierre()) ? reclamoprestacional.getReclamo_observacion_cierre():"" %></textarea>
									</td>
								</tr>

			


							</table>


							<table align="center" width="600px">

								<tr>
									<td width="600px">&nbsp;</td>
								</tr>

								<tr>
									<td width="170px"><liferay-ui:message
											key="Incluido Convenio con Gerenciadora" />: <input
										type="checkbox"
										id="<portlet:namespace />incluido_convenio_gerenciadora"
										name="<portlet:namespace />incluido_convenio_gerenciadora"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isReclamo_convenio_gerenciadora()  ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>

									<td width="320px"><liferay-ui:message key="2 % UOMA" />:
										<input type="checkbox" id="<portlet:namespace />dosporciento"
										name="<portlet:namespace />dosporciento"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isDosPorciento()   ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>

									<td width="210px"><label>Débito Prestadora:</label> <input
										type="checkbox" id="<portlet:namespace />debitoprestadora"
										name="<portlet:namespace />debitoprestadora"
										<%=Validator.isNotNull(reclamoprestacional) && reclamoprestacional.isDebitoPrestadora()    ? "checked" : "Unchecked"  %>
										<% if (!esEdicion) { %> disabled='disabled' <%}%>></td>
								</tr>
							</table>

						</fieldset>
					</div>
				</td>
				<td>

					<fieldset class="block-labels">

						<legend>
							<liferay-ui:message key="Datos de la OP" />
						</legend>

						<table class="lfr-table"
							style="border-collapse: separate; border-spacing: 3px;">
							<% if (opAsignadaalReclamoExiste){ %>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de lista:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getId_lista_reintegro()>0  ? reclamoprestacional.getId_lista_reintegro() : ""  %></td>
							</tr>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de OP:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getIdOP()>0 ? reclamoprestacional.getIdOP() : ""  %></td>
							</tr>
							<%if (reclamoprestacional.getChequeOP()!=null){ %>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de cheque:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getChequeOP()!= null ? reclamoprestacional.getChequeOP(): ""  %></td>
							</tr>
							<%}else{ %>
								<tr style="font-size: 110%">
								<td colspan="1"><b>Nro de Cuenta:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getCtaNro()!= 0 ? reclamoprestacional.getCtaNro(): ""  %></td>
							</tr>
							<%} %>
							
							<tr style="font-size: 110%">
								<td colspan="1"><b>Fecha OP:</b></td>
								<td colspan="1"
									style="color: #ada397; font-size: 120%; font-weight: bold;"><%= reclamoprestacional.getfechaOPAsString()   %></td>
							</tr>
							<%}else{%>
							<tr style="font-size: 110%">
								<td colspan="1"><b>Sin Orden de Pago.</b></td>
							</tr>
							<%}%>

						</table>
				</td>
				</fieldset>
			</tr>
		</table>

		<br />
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
		<%if(cmd!=null && cmd.equalsIgnoreCase(Constants.EDIT)) {%>
		<div id="<portlet:namespace />botoneditareclamo" align="center"
			style="height: 80px; overflow-x: hidden;">
			<table>
				<tr>
					<td><input type="button"
						value="<liferay-ui:message key="Actualizar" />"
						onClick="<portlet:namespace />editaReclamo(false);"
						title="<liferay-ui:message key="Actualiza los Datos Ingresados." />" />
					</td>				
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<%-- Si es Pendiente y NO ES ReadOnly, no muestra las opcion para volver a Recarga --%>
					<% if ((PuedeObservar) && (!showReadOnlyReclamPrestac)) {%>						
						<td><input type="button"
							value="<liferay-ui:message key="Observar" />"
							onClick="<portlet:namespace />volverEstadoObservado();"
							title="<liferay-ui:message key="Cambia a Estado Observado." />" />
						</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<% }%>
					<td><input type="button"
						value="<liferay-ui:message key="Imprimir Datos" />"
						onClick="<portlet:namespace />imprimirReclamo();"
						title="<liferay-ui:message key="Imprimir Registro del Caso." />" />
					</td>
				</tr>
			</table>
		</div>
		<%} %>
		<% if( reclamoprestacional!=null 
		&& showABMButtons == true 
		&& reclamoprestacional.getEstado()== 3  && reclamoprestacional.getIdOP() == 0
		&& !reclamoprestacional.isMarcaReabrirReclamo()) { // cerrado sin OP %>

		<div id="<portlet:namespace />boton_rollback" align="center"
			style="height: 80px; overflow-x: hidden;">
			<table>
				<tr>
					<td><input type="button"
						value="<liferay-ui:message key="rollback-reclamo" />"
						onClick="<portlet:namespace />reabrirReclamo(false);"
						title="<liferay-ui:message key="Reabre Registro del Caso." />" />
					</td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				</tr>
			</table>
		</div>
		<%}%>

		<%if(reclamoprestacional != null){ %>
		<div align="center">
			<table class="lfr-table"
				style="border-collapse: separate; border-spacing: 5px;">
				<tr>
					<td colspan="4"></hr></td>
				</tr>
				<tr>
					<td colspan="4">
						<div align="center" id="<portlet:namespace />rp_auditoria">
							<table style="font-size: 8">
								<tr>
									<td><label><liferay-ui:message key="Alta Usuario" />:</label></td>
									<td><%=reclamoprestacional.getAlta_usr()!=null? reclamoprestacional.getAlta_usr():""%></td>
									<td><label><liferay-ui:message
												key="crm-contacto-alta-fec" />:</label></td>
									<td><%=sdf2.format(reclamoprestacional.getAlta_fecha()) %></td>
									<td><label><liferay-ui:message key="Modi Usuario" />:</label></td>
									<td><%=reclamoprestacional.getModi_usr()!=null?reclamoprestacional.getModi_usr():"" %></td>
									<td><label><liferay-ui:message
												key="crm-contacto-modi-fec" />:</label></td>
									<td><%=reclamoprestacional.getModi_usr()!=null? sdf2.format(reclamoprestacional.getModi_fecha()) :"" %></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<% } %>
		<input id="<portlet:namespace />tipoNomenclador"
			name="<portlet:namespace />tipoNomenclador" type="hidden" value="" />

		<div id='validarExistenciaCuit' style="float: right;"></div>
</div>		
</form>

>>>>>>> .r7305
<script type="text/javascript">
<<<<<<< .mine
window.ReclamoPrestacionalNamespace = '<portlet:namespace />';
window.ReclamoPrestacionalAssetError = function(nombre) {
    if (window.console && window.console.error) {
        window.console.error("RECLAMO_PRESTACIONAL_ASSET_ERROR: " + nombre);
||||||| .r7295

var popupMD;

var popupDomicilio;

jQuery('#<portlet:namespace />divResultadoActualizarOK').hide();

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
jQuery('#<portlet:namespace/>reconocidoSSS').attr('readonly', true);


var addprestacion=false;
var load =false;
var sectorIni='';
var estadoIni='';




jQuery(document).ready(function() {
	load = true;
	sectorIni = jQuery("#<portlet:namespace />sector").val();
	estadoIni = jQuery("#<portlet:namespace />estado").val();

	//jQuery('#<portlet:namespace />observacion_medica_div').hide();
	if ('EXCEPCION' ==  jQuery("#<portlet:namespace />tipopedido").val()){
		traerDescripcion();
	}			 
	
	
	
	
	<% if(reclamoprestacional != null  && reclamoprestacional.getEstado()==3 ) {%>            
	
		    jQuery("#<portlet:namespace />tipo_gestion_cierre_reclamo option[value="+<%=reclamoprestacional.getTipo_gestion_cierre_reclamo()%> +"]").attr("selected",true);
	
		    jQuery("#<portlet:namespace />observacion_medica option[value="+<%=reclamoprestacional.getIdObservacionMedica()%> +"]").attr("selected",true);
		    

	<%}%>
    tipoGestionCierreReclamo();
    filtrarLetraComprobante();
	integracionReclamo();


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






jQuery("#<portlet:namespace />integracion").change(function(){
	
	try {	

		traerDescripcion();
   		
	}
	catch (err) {
		alert('error integracion ');
	}

});

 
jQuery("#<portlet:namespace />estado").change(function(){
	
	try {	
   		var estado =jQuery('#<portlet:namespace />estado').val();

   		var chk_amparo =jQuery("#<portlet:namespace/>chk_amparo").is(':checked');
   		
   		if (estado == 4 && chk_amparo == false ){
   			alert('Debe seleccionar la marca de Amparo ')	;
		
			jQuery("#<portlet:namespace />estado option[value=1]").attr("selected",true);

   		}
	}
	catch (err) {
		alert('error estado ');
	}

});



jQuery("#<portlet:namespace />tipopedido").change(function(){
	
	try {	
		 filtrarLetraComprobante();
		 
		 integracionReclamo();
	}
	catch (err) {
		alert('error tipopedido ');
	}

});


jQuery("#<portlet:namespace />chk_amparo").change(function(){
	
	try {	
   		var estado =jQuery('#<portlet:namespace />estado').val();

   		var chk_amparo =jQuery("#<portlet:namespace/>chk_amparo").is(':checked');
   		
   		if (estado == 4 && chk_amparo == false){
   			alert ('No puede sacar la marca de aparo si el estado es Incompleto ');
   			jQuery("#<portlet:namespace/>chk_amparo").attr('checked', true);
   		}
			
	}catch (err) {
		alert('error chk_amparo ');
	}

});



jQuery("#<portlet:namespace />tipo_gestion_cierre_reclamo").change(function(){
	tipoGestionCierreReclamo();
	
});

jQuery("#<portlet:namespace />observacion_medica").change(function(){
	
	try {	   		
   		jQuery("#<portlet:namespace />reclamo_observacion_cierre").text('');
   		
	}
	catch (err) {
		alert('error observacion_medica text');
	}
});


function tipoGestionCierreReclamo(){
	try {	
   		var tipo_presentes = jQuery('#<portlet:namespace />presentes').val();
   		var tipo_resolucion = jQuery('#<portlet:namespace />tipo_gestion_cierre_reclamo').val();
		
		if ( tipo_resolucion == 5){   
			jQuery('#<portlet:namespace />observacion_medica_tr').show();
   		}else{
   			jQuery('#<portlet:namespace />observacion_medica_tr').hide();
   		}
	}
	catch (err) {
		alert('error observacion_medica ');
	}
}


function integracionReclamo(){
	try {	
		 if ('EXCEPCION' ==  jQuery("#<portlet:namespace />tipopedido").val()){
			 jQuery('#integracion_label').show();
			 jQuery('#<portlet:namespace />integracion').show();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').show();
		 }else {
			 jQuery('#integracion_label').hide();
			 jQuery('#<portlet:namespace />integracion').hide();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').hide();


		 }	
	}
	catch (err) {
		alert('error integracion ');
	}
}


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



function filtrarLetraComprobante() {
	var tipoPedido = jQuery("#<portlet:namespace/>tipopedido").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/filtrarLetraComprobante&tipo_pedido='+tipoPedido;
	jQuery("#<portlet:namespace/>comprobante_letra").attr('disabled', 'disabled');
	
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("<portlet:namespace/>comprobante_letra").length = 0;
			jQuery("#<portlet:namespace/>comprobante_letra").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			jQuery('#<portlet:namespace />comprobante_letra').html(data).fadeIn();

		}
	});
}



<%-- <% if(esEdicion) {%>
AcomodarControlesEdicion();
<%}%> --%>


aplicaEstiloBordeRojoDatosObligatorio();

<%-- function  AcomodarControlesEdicion() {
	// HEADER DATOS INHABILITADOS   
	                         
	                         document.getElementById("<portlet:namespace/>sector").disabled = "disabled";
	                         <%if (Validator.isNotNull(reclamoprestacional) &&   Validator.isNotNull(reclamoprestacional.getTipoPedido()) ) {   %>
	                         if ( document.getElementById("<portlet:namespace />tipopedido").selectedIndex!=0) {
	                        	 document.getElementById("<portlet:namespace />tipopedido").disabled = "disabled"; 
	                         }	                            
	                         <%}%>
						 	 document.getElementById("<portlet:namespace />fechaospimDia").disabled = true;
							 document.getElementById("<portlet:namespace />fechaospimMes").disabled = true;
							 document.getElementById("<portlet:namespace />fechaospimAnio").disabled = true;
	// DATOS DE REVISION
	                         jQuery("#<portlet:namespace />botoneditareclamo").show();
	                         document.getElementById("<portlet:namespace />estado").disabled = "";
                        	 document.getElementById("<portlet:namespace />fecharevisionDia").disabled = "";
							 document.getElementById("<portlet:namespace />fecharevisionMes").disabled = "";
							 document.getElementById("<portlet:namespace />fecharevisionAnio").disabled = "";
							 document.getElementById("<portlet:namespace />observacion_revision").disabled = "";
							 document.getElementById("<portlet:namespace />chk_amparo").disabled = "";
							 document.getElementById("<portlet:namespace />chk_superintendencia").disabled = "";
							 document.getElementById("<portlet:namespace />chk_recuperable").disabled = "";						 	 						 
							 document.getElementById("<portlet:namespace />chk_entramite").disabled = "";
							 document.getElementById("<portlet:namespace />resolucion").disabled = "";
							 document.getElementById("<portlet:namespace />respresolucion").disabled = "";
							 document.getElementById("<portlet:namespace />presentes").disabled = "";
		// DATOS DE CIERRE NO ES NECESARIO
		 					/*  document.getElementById("<portlet:namespace />fechacierreDia").disabled = false;
							 document.getElementById("<portlet:namespace />fechacierreMes").disabled = false;
							 document.getElementById("<portlet:namespace />fechacierreAnio").disabled = false;
							
							
							 document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo").disabled = "";							
							 
							 document.getElementById("<portlet:namespace />reclamo_observacion_cierre").disabled = false;
							 document.getElementById("<portlet:namespace />reclamo_ps_factura_ospim").disabled = "";
							 document.getElementById("<portlet:namespace />reclamo_a_negociar").disabled = ""; */
							 
	} --%>



function <portlet:namespace />buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val();
	var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val();
    var tipoNomenclador=jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val();
    
    // Marca ReinLiq no se utiliza en esta busqueda
    var marcaReinliq=null;
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
    	
    	
    	if(tipoNomenclador==8){
    		marcaReinliq=6;
    	}    

    	var esPrestMed = 0;
    	sector = jQuery("#<portlet:namespace />sector").val();
    	if (sector == "PRESTACIONES MEDICAS")
    		esPrestMed = 1;
    		    	
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    url += '&marcareinliq='+marcaReinliq+'&esPrestMed='+esPrestMed;
	    	   
	    jQuery(popupMD).load(url);
=======

var popupMD;

var popupDomicilio;

jQuery('#<portlet:namespace />divResultadoActualizarOK').hide();

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
jQuery('#<portlet:namespace/>reconocidoSSS').attr('readonly', true);


var addprestacion=false;
var load =false;
var sectorIni='';
var estadoIni='';


var observacionesRechazado = [];

<%
for (ReclamosPrestacionalesRevisionEstado revisionEstado : listaRevisionEstado) {
%>
    observacionesRechazado.push({
        id: "<%=revisionEstado.getId()%>",
        descripcion: "<%=UnicodeFormatter.toString(revisionEstado.getDescripcion())%>"
    });
<%
}
%>


var observacionesAutorizado = [];

<%
for (
    ReclamosPrestacionalesRevisionEstado revisionEstado :
    listaRevisionEstadoAutorizado
) {
%>
    observacionesAutorizado.push({
        id: "<%=revisionEstado.getId()%>",
        descripcion: "<%=UnicodeFormatter.toString(revisionEstado.getDescripcion())%>"
    });
<%
}
%>


function cargarObservacionesMedicas(lista,observacionSeleccionada) {

	    var combo = jQuery("#<portlet:namespace/>observacion_medica");

	    combo.empty();

	    combo.append(new Option("Seleccione observación","0"));

	    for (var i = 0; i < lista.length; i++) {

	        combo.append(new Option(lista[i].descripcion,String(lista[i].id)));
	    }

	    combo.val("0");

	    if (combo.length > 0) {
	        combo[0].selectedIndex = 0;
	    }

	    // Solo restaura una observación previamente guardada
	    if (observacionSeleccionada != null && String(observacionSeleccionada) != "" && String(observacionSeleccionada) != "0") {

	        var valorGuardado = String(observacionSeleccionada);

	        if (combo.find("option[value='" + valorGuardado + "']").length > 0) {
	            combo.val(valorGuardado);
	        }
	    }
	}

function normalizarNombrePlan(nombrePlan) {

    if (nombrePlan == null) {
        return "";
    }

    return String(nombrePlan)
        .toUpperCase()
        .replace(/^\s+|\s+$/g, "")
        .replace(/\s+/g, " ");
}


function esPlanBloqueadoParaReclamo(nombrePlan) {

    var planNormalizado =
        normalizarNombrePlan(nombrePlan);

    return planNormalizado == "COBERTURA" ||
           planNormalizado == "COBERTURA TOTAL O" ||
           planNormalizado == "COBERTURA TOTAL M";
}


function <portlet:namespace/>validarPlanParaReclamo(nombrePlan,mostrarMensaje) {

    var bloqueado = esPlanBloqueadoParaReclamo(nombrePlan);

    jQuery("#<portlet:namespace/>plan_reclamo_bloqueado").val(bloqueado ? "1" : "0");

    jQuery("#<portlet:namespace/>nombre_plan_reclamo_bloqueado").val(nombrePlan || "");

    if (bloqueado) {

        if (mostrarMensaje) {
            alert('Afiliado con plan "' + nombrePlan + '" no puede cargar un reclamo.');
        }

        return false;
    }

    return true;
}

var ultimoPlanAfiliadoDetectado = null;

function verificarPlanAfiliadoDelReclamo() {

    var campoPlan = jQuery("#<portlet:namespace/>plan");

    if (campoPlan.length == 0) {
        return;
    }

    var nombrePlan = campoPlan.val();

    if (nombrePlan == null) {
        nombrePlan = "";
    }

    nombrePlan = String(nombrePlan);
    
    if (nombrePlan == ultimoPlanAfiliadoDetectado) {
        return;
    }

    ultimoPlanAfiliadoDetectado = nombrePlan;

    <portlet:namespace/>validarPlanParaReclamo(nombrePlan,true);
}

jQuery(document).ready(function() {
	load = true;
	sectorIni = jQuery("#<portlet:namespace />sector").val();
	estadoIni = jQuery("#<portlet:namespace />estado").val();

	//jQuery('#<portlet:namespace />observacion_medica_div').hide();
	if ('EXCEPCION' ==  jQuery("#<portlet:namespace />tipopedido").val()){
		traerDescripcion();
	}			 
	
	
	var observacionMedicaInicial = null;

	<%
	if (
	    reclamoprestacional != null &&
	    reclamoprestacional.getEstado() == 3
	) {
	%>

	    jQuery(
	        "#<portlet:namespace/>tipo_gestion_cierre_reclamo"
	    ).val(
	        "<%=reclamoprestacional.getTipo_gestion_cierre_reclamo()%>"
	    );

	    <%
	    if (reclamoprestacional.getIdObservacionMedica() > 0) {
	    %>

	        observacionMedicaInicial =
	            "<%=reclamoprestacional.getIdObservacionMedica()%>";

	    <%
	    }
	    %>

	<%
	}
	%>

	tipoGestionCierreReclamo(observacionMedicaInicial);

	filtrarLetraComprobante();
	integracionReclamo();

	//Revisa el afiliado que ya vino cargado, por ejemplo desde la aplicación.
	verificarPlanAfiliadoDelReclamo();

	window.setInterval(verificarPlanAfiliadoDelReclamo,500);

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

jQuery("#<portlet:namespace />integracion").change(function(){
	
	try {	
		traerDescripcion();	
	}
	catch (err) {
		alert('error integracion ');
	}
});
 
jQuery("#<portlet:namespace />estado").change(function(){
	
	try {	
   		var estado =jQuery('#<portlet:namespace />estado').val();

   		var chk_amparo =jQuery("#<portlet:namespace/>chk_amparo").is(':checked');
   		
   		if (estado == 4 && chk_amparo == false ){
   			alert('Debe seleccionar la marca de Amparo ')	;
		
			jQuery("#<portlet:namespace />estado option[value=1]").attr("selected",true);

   		}
	}
	catch (err) {
		alert('error estado ');
	}

});



jQuery("#<portlet:namespace />tipopedido").change(function(){

    try {

        filtrarLetraComprobante();
        integracionReclamo();

        tipoGestionCierreReclamo();

    } catch (err) {

        alert("Error al cambiar el tipo de pedido");
    }
});


jQuery("#<portlet:namespace />chk_amparo").change(function(){
	
	try {	
   		var estado =jQuery('#<portlet:namespace />estado').val();

   		var chk_amparo =jQuery("#<portlet:namespace/>chk_amparo").is(':checked');
   		
   		if (estado == 4 && chk_amparo == false){
   			alert ('No puede sacar la marca de aparo si el estado es Incompleto ');
   			jQuery("#<portlet:namespace/>chk_amparo").attr('checked', true);
   		}
			
	}catch (err) {
		alert('error chk_amparo ');
	}

});

jQuery("#<portlet:namespace />tipo_gestion_cierre_reclamo").change(function(){
	tipoGestionCierreReclamo();
	
});

jQuery("#<portlet:namespace />observacion_medica").change(function(){	
	try {	   		
   		jQuery("#<portlet:namespace />reclamo_observacion_cierre").text(''); 		
	}
	catch (err) {
		alert('error observacion_medica text');
	}
});

function tipoGestionCierreReclamo(observacionSeleccionada) {

    try {

        var tipoPedido = jQuery("#<portlet:namespace/>tipopedido").val();
        var idGestion = String(jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo").val() || "0");           
        var filaObservacion = jQuery("#<portlet:namespace/>observacion_medica_tr");
        var comboObservacion = jQuery("#<portlet:namespace/>observacion_medica");
        var esRechazado = idGestion == "5";
        var esReintegro =  tipoPedido == "REINTEGRO" && idGestion == "4";
        var esExcepcionFacturacionDirecta =tipoPedido == "EXCEPCION" &&idGestion == "3";

        if (esRechazado) {

            cargarObservacionesMedicas(observacionesRechazado, observacionSeleccionada);

            filaObservacion.show();

            comboObservacion.attr("required","required");

        } else if (esReintegro || esExcepcionFacturacionDirecta) {

            cargarObservacionesMedicas(observacionesAutorizado,observacionSeleccionada);
            filaObservacion.show();
            comboObservacion.attr("required","required");

        } else {

            cargarObservacionesMedicas([], "0");
            filaObservacion.hide();
            comboObservacion.removeAttr("required");
        }

    } catch (err) {

        alert(
            "Error al manejar las observaciones del área médica: " +
            err.message
        );
    }
}

function integracionReclamo(){
	try {	
		 if ('EXCEPCION' ==  jQuery("#<portlet:namespace />tipopedido").val()){
			 jQuery('#integracion_label').show();
			 jQuery('#<portlet:namespace />integracion').show();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').show();
		 }else {
			 jQuery('#integracion_label').hide();
			 jQuery('#<portlet:namespace />integracion').hide();
			 jQuery('#integracion_desc').show();
			 jQuery('#integracion_div').hide();
		 }	
	}
	catch (err) {
		alert('error integracion ');
	}
}


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



function filtrarLetraComprobante() {
	var tipoPedido = jQuery("#<portlet:namespace/>tipopedido").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/filtrarLetraComprobante&tipo_pedido='+tipoPedido;
	jQuery("#<portlet:namespace/>comprobante_letra").attr('disabled', 'disabled');
	
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("<portlet:namespace/>comprobante_letra").length = 0;
			jQuery("#<portlet:namespace/>comprobante_letra").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			jQuery('#<portlet:namespace />comprobante_letra').html(data).fadeIn();

		}
	});
}



<%-- <% if(esEdicion) {%>
AcomodarControlesEdicion();
<%}%> --%>


aplicaEstiloBordeRojoDatosObligatorio();

<%-- function  AcomodarControlesEdicion() {
	// HEADER DATOS INHABILITADOS   
	                         
	                         document.getElementById("<portlet:namespace/>sector").disabled = "disabled";
	                         <%if (Validator.isNotNull(reclamoprestacional) &&   Validator.isNotNull(reclamoprestacional.getTipoPedido()) ) {   %>
	                         if ( document.getElementById("<portlet:namespace />tipopedido").selectedIndex!=0) {
	                        	 document.getElementById("<portlet:namespace />tipopedido").disabled = "disabled"; 
	                         }	                            
	                         <%}%>
						 	 document.getElementById("<portlet:namespace />fechaospimDia").disabled = true;
							 document.getElementById("<portlet:namespace />fechaospimMes").disabled = true;
							 document.getElementById("<portlet:namespace />fechaospimAnio").disabled = true;
	// DATOS DE REVISION
	                         jQuery("#<portlet:namespace />botoneditareclamo").show();
	                         document.getElementById("<portlet:namespace />estado").disabled = "";
                        	 document.getElementById("<portlet:namespace />fecharevisionDia").disabled = "";
							 document.getElementById("<portlet:namespace />fecharevisionMes").disabled = "";
							 document.getElementById("<portlet:namespace />fecharevisionAnio").disabled = "";
							 document.getElementById("<portlet:namespace />observacion_revision").disabled = "";
							 document.getElementById("<portlet:namespace />chk_amparo").disabled = "";
							 document.getElementById("<portlet:namespace />chk_superintendencia").disabled = "";
							 document.getElementById("<portlet:namespace />chk_recuperable").disabled = "";						 	 						 
							 document.getElementById("<portlet:namespace />chk_entramite").disabled = "";
							 document.getElementById("<portlet:namespace />resolucion").disabled = "";
							 document.getElementById("<portlet:namespace />respresolucion").disabled = "";
							 document.getElementById("<portlet:namespace />presentes").disabled = "";
		// DATOS DE CIERRE NO ES NECESARIO
		 					/*  document.getElementById("<portlet:namespace />fechacierreDia").disabled = false;
							 document.getElementById("<portlet:namespace />fechacierreMes").disabled = false;
							 document.getElementById("<portlet:namespace />fechacierreAnio").disabled = false;
							
							
							 document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo").disabled = "";							
							 
							 document.getElementById("<portlet:namespace />reclamo_observacion_cierre").disabled = false;
							 document.getElementById("<portlet:namespace />reclamo_ps_factura_ospim").disabled = "";
							 document.getElementById("<portlet:namespace />reclamo_a_negociar").disabled = ""; */
							 
	} --%>



function <portlet:namespace />buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val();
	var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val();
    var tipoNomenclador=jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val();
    
    // Marca ReinLiq no se utiliza en esta busqueda
    var marcaReinliq=null;
	if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
    	if(popupMD==null)
    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
    	
    	
    	if(tipoNomenclador==8){
    		marcaReinliq=6;
    	}    

    	var esPrestMed = 0;
    	sector = jQuery("#<portlet:namespace />sector").val();
    	if (sector == "PRESTACIONES MEDICAS")
    		esPrestMed = 1;
    		    	
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
	    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&tiponomenclador='+tipoNomenclador +'&codigonomenclador='+encodeURI(codigo_nomenclador)+'&soloActivos=true';
	    url += '&marcareinliq='+marcaReinliq+'&esPrestMed='+esPrestMed;
	    	   
	    jQuery(popupMD).load(url);
>>>>>>> .r7305
    }
};
</script>
<script type="text/javascript">
(function(window, jQuery) {
    if (!jQuery || typeof jQuery.ajax !== "function"
            || jQuery.ajax.__rpFiltroLetraNoBloqueante) {
        return;
    }

    var ajaxOriginal = jQuery.ajax;
    var ajaxNoBloqueante = function(opciones) {
        if (arguments.length === 1
                && opciones
                && typeof opciones === "object"
                && opciones.async === false
                && String(opciones.url || "")
                        .indexOf("filtrarLetraComprobante") >= 0) {

            opciones = jQuery.extend({}, opciones);
            opciones.async = true;

            if (window.console && window.console.warn) {
                window.console.warn(
                        "RECLAMO_PRESTACIONAL_FILTRO_LETRA_ASYNC"
                );
            }

            return ajaxOriginal.call(this, opciones);
        }

        return ajaxOriginal.apply(this, arguments);
    };

    ajaxNoBloqueante.__rpFiltroLetraNoBloqueante = true;
    ajaxNoBloqueante.__rpAjaxOriginal = ajaxOriginal;
    jQuery.ajax = ajaxNoBloqueante;
})(window, window.jQuery);
</script>
<script type="text/javascript">
(function(window, jQuery) {
    if (!jQuery || typeof jQuery.ajax !== "function") {
        return;
    }

    var TIMEOUT_AFILIADO_MS = 15000;

<<<<<<< .mine
    function esEndpointAfiliadoNoBloqueante(url) {
        return url.indexOf("evalua_permanencia_afiliado") >= 0
                || url.indexOf("tiene_observaciones_afiliado") >= 0
                || url.indexOf("buscar_afiliado_datos") >= 0;
||||||| .r7295
	var valor = 0;
	valor=jQuery('#<portlet:namespace />cantprestacioneslista').val();
	
	
	var dia  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaospimDia").val()));
	var mes  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaospimMes").val()));
	var anio   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaospimAnio").val()));
	
	var dia1  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalDia").val()));
	var mes1  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalMes").val()));
	var anio1   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalAnio").val()));	
	
	
	var dia2  = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreDia").val()));
	var mes2  = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreMes").val()));
	var anio2   = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreAnio").val()));
	
	
	var msgs = ["Error en la fecha Ospim.", "Debe seleccionar el sector que inicia  el reclamo.", "Debe seleccionar el estado del reclamo.","Debe seleccionar al Afiliado asociado al reclamo.","Complete la Fecha Seccional o dejela en blanco","Debe seleccionar el tipo de Pedido"]; 
	var condiciones =[5];
	var controles  =[5];
		
	var tipoSelectsector  =document.getElementById("<portlet:namespace />sector");
	var tipoSelectestado  =document.getElementById("<portlet:namespace/>estado");
	var tipoSelecttipopedido =document.getElementById("<portlet:namespace />tipopedido");
	/* document.getElementById("<portlet:namespace />tipopedido").selectedIndex==0 */
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();	
	
	
	
	var  resp=true;
	
	controles[0]=document.getElementById("<portlet:namespace />fechaospimDia"); 	
	controles[1]=tipoSelectsector;
	controles[2]=tipoSelectestado; 	
	controles[3]=document.getElementById("<portlet:namespace />cuil");	
	controles[4]=document.getElementById("<portlet:namespace />fechaseccionalDia");
	controles[5]=tipoSelecttipopedido;
	
	condiciones[0]=dia || mes || anio;	
	
	condiciones[1]=(tipoSelectsector.selectedIndex==0);
	condiciones[2]=(tipoSelectestado.selectedIndex==0);	
	condiciones[3]=(cuil=="" || inte=="" );
	condiciones[4]=(dia1 || mes1 || anio1) && (!dia1 || !mes1 || !anio1) ;
	condiciones[5]=(tipoSelecttipopedido.selectedIndex==0);
	
	if (condiciones[0]){
		resp=false;
		alert (msgs[0] );
		controles[0].focus();
	}
	if (condiciones[1] && resp){
		resp=false;
		alert (msgs[1] );
		controles[1].focus();
	}
	if (condiciones[2] && resp){
		resp=false;
		alert (msgs[2] );
		controles[2].focus();
	}
	if (condiciones[3] && resp){
		resp=false;
		alert (msgs[3] );
		controles[3].focus();
	}
	if (condiciones[4] && resp){
		resp=false;
		alert (msgs[4] );
		controles[4].focus();
	}

	if (condiciones[5] && resp){
		resp=false;
		alert (msgs[5] );
		controles[5].focus();
	}

	// valida datos del cierre del reclamo
	var idgestion = jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo').val();
	
	var justificacion=jQuery('#<portlet:namespace />justificacionmedcica_reclamo').val();

	
	if (idgestion == 0  && jQuery('#<portlet:namespace/>estado option:selected').text().trim() == 'CERRADO' ){
		alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
		document.getElementById("<portlet:namespace />tipo_gestion_cierre_reclamo").focus();
		return false;
	}
	
	/* if (idgestion==5){ */
	if (idgestion==5){
	/* 	var isDisabled = jQuery('#<portlet:namespace />dosporciento').is(':disabled');			
	    if (!isDisabled) { */
			if(! confirm("Al seleccionar la opción RECHAZADO el sistema rechazará todas las prestaciones del caso, no podrá asociarlas a reintegros. Está seguro ?")){
				return false;	
			/* } */
	    }	
	}
		var respResolucion = document.getElementById("<portlet:namespace />respresolucion");
		
		if ( jQuery('#<portlet:namespace />auditoriaadministrativa').val()!="Ok" ){ // auditoria administrativa 

			if (justificacion.length ==0  && resp ){ // no hay revisiones activas 
				alert('Tiene que ingresar la justificación médica del Caso para efectuar el Cierre del Caso.');
				jQuery('#<portlet:namespace />justificacionmedcica_reclamo').focus();
				resp=false;
			}		
		}
		// validar si 
		if (idgestion<1  && resp && jQuery('#<portlet:namespace/>estado option:selected').text() == 'CERRADO' ){
			alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
			document.getElementById("<portlet:namespace />tipo_gestion_cierre_reclamo").focus();
			resp=false;
		}
		
			if ((dia2 || mes2 || anio2)  && resp )  {
				alert('Debe ingresar la fecha de Cierre del Reclamo');
				document.getElementById("<portlet:namespace />fechacierreDia").focus();
				resp=false;
			}
		
		if(tipoSelecttipopedido == 3){ //si estado = cerrado
			if (jQuery('#<portlet:namespace/>cantrevisionesactivas').val()<1  && resp ){ // no hay revisiones activas 
				alert('Recuerde, debe tener registrada por lo menos una revisión activa para el cierre del caso!!!!.');			
				resp=false;
			}
		}
		
		
// SI ES CIERRE DEL CASO NO SE CONTROLA SI SE DIERON DE BAJA TODAS LAS PRESTACIONES

	valor=jQuery('#<portlet:namespace />cantprestacioneslista').val();
	

    if (Edicion && addprestacion) {
    	if (valor <1   && resp){
    		alert('Debe tener ingresada por lo menos una prestación');
    		resp=false;
    	}
    }else{
    		if (valor <1  && resp ){
    			
    		}	
=======
	var planBloqueado = jQuery("#<portlet:namespace/>plan_reclamo_bloqueado").val();

		if (planBloqueado == "1") {

		    var nombrePlan = jQuery("#<portlet:namespace/>nombre_plan_reclamo_bloqueado").val();

		    alert('Afiliado con plan "' +nombrePlan +'" no puede cargar un reclamo.');

		    return false;
		}
		
	var valor = 0;
	valor=jQuery('#<portlet:namespace />cantprestacioneslista').val();
	
	
	var dia  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaospimDia").val()));
	var mes  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaospimMes").val()));
	var anio   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaospimAnio").val()));
	
	var dia1  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalDia").val()));
	var mes1  = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalMes").val()));
	var anio1   = isNaN(parseInt(jQuery("#<portlet:namespace />fechaseccionalAnio").val()));	
	
	
	var dia2  = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreDia").val()));
	var mes2  = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreMes").val()));
	var anio2   = isNaN(parseInt(jQuery("#<portlet:namespace />fechacierreAnio").val()));
	
	
	var msgs = ["Error en la fecha Ospim.", "Debe seleccionar el sector que inicia  el reclamo.", "Debe seleccionar el estado del reclamo.","Debe seleccionar al Afiliado asociado al reclamo.","Complete la Fecha Seccional o dejela en blanco","Debe seleccionar el tipo de Pedido"]; 
	var condiciones =[5];
	var controles  =[5];
		
	var tipoSelectsector  =document.getElementById("<portlet:namespace />sector");
	var tipoSelectestado  =document.getElementById("<portlet:namespace/>estado");
	var tipoSelecttipopedido =document.getElementById("<portlet:namespace />tipopedido");
	/* document.getElementById("<portlet:namespace />tipopedido").selectedIndex==0 */
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();	
	
	
	
	var  resp=true;
	
	controles[0]=document.getElementById("<portlet:namespace />fechaospimDia"); 	
	controles[1]=tipoSelectsector;
	controles[2]=tipoSelectestado; 	
	controles[3]=document.getElementById("<portlet:namespace />cuil");	
	controles[4]=document.getElementById("<portlet:namespace />fechaseccionalDia");
	controles[5]=tipoSelecttipopedido;
	
	condiciones[0]=dia || mes || anio;	
	
	condiciones[1]=(tipoSelectsector.selectedIndex==0);
	condiciones[2]=(tipoSelectestado.selectedIndex==0);	
	condiciones[3]=(cuil=="" || inte=="" );
	condiciones[4]=(dia1 || mes1 || anio1) && (!dia1 || !mes1 || !anio1) ;
	condiciones[5]=(tipoSelecttipopedido.selectedIndex==0);
	
	if (condiciones[0]){
		resp=false;
		alert (msgs[0] );
		controles[0].focus();
	}
	if (condiciones[1] && resp){
		resp=false;
		alert (msgs[1] );
		controles[1].focus();
	}
	if (condiciones[2] && resp){
		resp=false;
		alert (msgs[2] );
		controles[2].focus();
	}
	if (condiciones[3] && resp){
		resp=false;
		alert (msgs[3] );
		controles[3].focus();
	}
	if (condiciones[4] && resp){
		resp=false;
		alert (msgs[4] );
		controles[4].focus();
	}

	if (condiciones[5] && resp){
		resp=false;
		alert (msgs[5] );
		controles[5].focus();
	}

	// valida datos del cierre del reclamo
	var idgestion = jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo').val();
	
	var justificacion=jQuery('#<portlet:namespace />justificacionmedcica_reclamo').val();

	var tipoPedidoCierre = jQuery("#<portlet:namespace/>tipopedido").val();

		var observacionMedica = jQuery("#<portlet:namespace/>observacion_medica").val();

		var requiereObservacionMedica =
		    idgestion == "5" ||
		    (
		        tipoPedidoCierre == "REINTEGRO" &&
		        idgestion == "4"
		    ) ||
		    (
		        tipoPedidoCierre == "EXCEPCION" &&
		        idgestion == "3"
		    );

		if (
		    requiereObservacionMedica &&
		    (
		        observacionMedica == null ||
		        observacionMedica == "" ||
		        observacionMedica == "0"
		    )
		) {

		    alert(
		        "Debe seleccionar una observación del área médica."
		    );

		    jQuery(
		        "#<portlet:namespace/>observacion_medica"
		    ).focus();

		    return false;
		}
	
	if (idgestion == 0  && jQuery('#<portlet:namespace/>estado option:selected').text().trim() == 'CERRADO' ){
		alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
		document.getElementById("<portlet:namespace />tipo_gestion_cierre_reclamo").focus();
		return false;
	}
	
	/* if (idgestion==5){ */
	if (idgestion==5){
	/* 	var isDisabled = jQuery('#<portlet:namespace />dosporciento').is(':disabled');			
	    if (!isDisabled) { */
			if(! confirm("Al seleccionar la opción RECHAZADO el sistema rechazará todas las prestaciones del caso, no podrá asociarlas a reintegros. Está seguro ?")){
				return false;	
			/* } */
	    }	
	}
		var respResolucion = document.getElementById("<portlet:namespace />respresolucion");
		
		if ( jQuery('#<portlet:namespace />auditoriaadministrativa').val()!="Ok" ){ // auditoria administrativa 

			if (justificacion.length ==0  && resp ){ // no hay revisiones activas 
				alert('Tiene que ingresar la justificación médica del Caso para efectuar el Cierre del Caso.');
				jQuery('#<portlet:namespace />justificacionmedcica_reclamo').focus();
				resp=false;
			}		
		}
		// validar si 
		if (idgestion<1  && resp && jQuery('#<portlet:namespace/>estado option:selected').text() == 'CERRADO' ){
			alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
			document.getElementById("<portlet:namespace />tipo_gestion_cierre_reclamo").focus();
			resp=false;
		}
		
			if ((dia2 || mes2 || anio2)  && resp )  {
				alert('Debe ingresar la fecha de Cierre del Reclamo');
				document.getElementById("<portlet:namespace />fechacierreDia").focus();
				resp=false;
			}
		
		if(tipoSelecttipopedido == 3){ //si estado = cerrado
			if (jQuery('#<portlet:namespace/>cantrevisionesactivas').val()<1  && resp ){ // no hay revisiones activas 
				alert('Recuerde, debe tener registrada por lo menos una revisión activa para el cierre del caso!!!!.');			
				resp=false;
			}
		}
		
		
// SI ES CIERRE DEL CASO NO SE CONTROLA SI SE DIERON DE BAJA TODAS LAS PRESTACIONES

	valor=jQuery('#<portlet:namespace />cantprestacioneslista').val();
	

    if (Edicion && addprestacion) {
    	if (valor <1   && resp){
    		alert('Debe tener ingresada por lo menos una prestación');
    		resp=false;
    	}
    }else{
    		if (valor <1  && resp ){
    			
    		}	
>>>>>>> .r7305
    }

    function diagnosticar(codigo, detalle) {
        if (window.console && window.console.warn) {
            window.console.warn(
                    codigo + (detalle ? ": " + detalle : "")
            );
        }
<<<<<<< .mine
||||||| .r7295
   		if (tipoSector.selectedIndex==1){     	       
   			jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val(8); // discapacidad 
   		} else if (tipoSector.selectedIndex==6){
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
		
		var observacionMedica = jQuery('#<portlet:namespace />observacion_medica').val();

		
		
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
							   "reclamoobservacion":reclamoobservacion,
							   "observacionMedica":observacionMedica					   
							   };
			
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_revisiones_reclamo" /></portlet:renderURL>';
		
		
		if (resolucion.toUpperCase()!="AUTORIZADO"){
			if(confirm("Confirma el Cierre del Caso con el Rechazo en la revision ?")){
	 			    /* var estadoSelectsector  =document.getElementById("<portlet:namespace/>estado"); */
				    //estadoSelectsector.selectedIndex = 2; // setea el estado en cerrado
				    /* estadoSelectsector.selectedIndex = ubicacionOpcionEstadoCerradoCombo();	 */		    
				    /* jQuery("#<portlet:namespace/>estado option[value='3']").attr("selected", true); //CERARADO */
				    jQuery("#<portlet:namespace/>estado option[value='CERRADO']").attr("selected",true);
				    controlarEstadoCerrado(); // hace visible los controles del estado cerrado
				    
				    document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo").disabled = false;
					
					var tipoSelectsector  =document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo");
					
					seteaControlesFacturacionDirecta(true);
					/* tipoSelectsector.selectedIndex= ubicacionOpcionRechazadoenCombo(); */
				    /* jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo option[value='5']").attr("selected", true); //RECHAZADO */
				    jQuery("#<portlet:namespace />tipo_gestion_cierre_reclamo option[value='RECHAZADO']").attr("selected",true);

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

/* function ubicacionOpcionRechazadoenCombo(){
	var idselect;
	var pos=0;
	var posicion=0;
		jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo option').each(function(){
        	tipoGestionArray = jQuery(this).val().split("|");
        	idselect =tipoGestionArray [0];         
        	if (idselect == 5){
        	 	posicion=pos;
	        }
        	pos=pos+1;
        });
	return posicion;
} */

/* function ubicacionOpcionEstadoCerradoCombo(){
	var idselect;
	var tipoGestionArray;
	var pos=0;
	var posicion=0;
		jQuery('#<portlet:namespace/>estado option').each(function(){
        	tipoGestionArray = jQuery(this).val().split("|");
        	idselect =tipoGestionArray [0];         
        	if (idselect == 3){
        	 	posicion=pos;
	        }
        	pos=pos+1;
        });
	return posicion;
} */

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


function <portlet:namespace />vercontactosdelreclamo() {
		
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();
	var idreclamoprestacion=jQuery('#<portlet:namespace />idreclamoprestacion').val();
	var modoconsulta=jQuery('#<portlet:namespace />consultareclamo').val();
	
	    if ((cuil=="" || inte=="" )){		
			alert ('Debe seleccionar al Afiliado para ver sus contactos.');
			document.getElementById("<portlet:namespace />cuil").focus();
			return false;
		}	    
			
	    if (document.getElementById("<portlet:namespace />botoncontactosreclamo").value=='Ver Contactos Asociados al Caso.'){
		jQuery("#<portlet:namespace />lista_contactos_reclamo").show();
		jQuery("#<portlet:namespace />botoncontactosreclamo").hide();
		jQuery("#<portlet:namespace />justificacion_medica_reclamo").hide();
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var idreclamoprestacion=jQuery('#<portlet:namespace />idreclamoprestacion').val();		
		
		if ( jQuery("#<portlet:namespace />idreclamoprestacion").val()<1 
				&&  ((cuil==jQuery("#<portlet:namespace />cuiltitular").val()  
						&& inte==jQuery("#<portlet:namespace />intetitular").val() ))  ){			
			return false; // es el mismo afiliado 
		}		
		
		jQuery("#<portlet:namespace />cuiltitular").val(cuil);
		jQuery("#<portlet:namespace />intetitular").val(inte);
		
		var params = {"cuil_contacto":cuil,"inte_contacto":inte,"idreclamoprestacion":idreclamoprestacion,"modoconsulta":modoconsulta};

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_contactos_reclamo" /></portlet:renderURL>';
		
		jQuery('#<portlet:namespace />lista_contactos_reclamo').load(url,params, function(){
										jQuery('#<portlet:namespace />buscando').hide();          															
															  });			 	 
		}					
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
	var cargoimesa= jQuery('#<portlet:namespace />cargoimesaEdicion').val();
	var reconocidoSSS= jQuery('#<portlet:namespace />reconocidoSSSEdicion').val();
	var observaciones= jQuery('#<portlet:namespace />observacion_prestacionEdicion').val();
    var prestacion= "Graba Edicion";
    var idprestacion =  jQuery("#<portlet:namespace />codigoprestacion").val();
    var idRegistro=jQuery('#<portlet:namespace />idRegistro').val();

    var estadoAprobacion = tipoAccion;
    var recuperableSur  =  jQuery('#<portlet:namespace />recuperable_surEdicion').val();  
    
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


    var flagAmparo = false; 
    var estado=jQuery('#<portlet:namespace />estado').val();
	var chk_amparo=jQuery("#<portlet:namespace/>chk_amparo").is(':checked');

	if (estado == 4 && chk_amparo == true ){
		//Si esta en estado inconsistente y es amparo permitimos grabar sin datos de comprobante
		flagAmparo = true;
	}


	// Solo validar montos si completó algo del área médica
	var tieneDatosAreaMedica = (
	    (importe != null && importe != '' && importe != 0) ||
	    (cargoospim != null && cargoospim != '' && cargoospim != 0) ||
	    (cargops != null && cargops != '' && cargops != 0) ||
	    (cargoimesa != null && cargoimesa != '' && cargoimesa != 0) ||
	    (reconocidoSSS != null && reconocidoSSS != '' && reconocidoSSS != 0)
	);

	if (tieneDatosAreaMedica) {
	    if (recuperableSur == 0) {
	        alert('Debe seleccionar el campo Recuperable');
	        return false;
	    }

	    //validación de montos
	    if (!validaMontosEdicion()) {       
	        return false;
	    }
	}
    
	/*
    if (!validaMontosEdicion()){       
   		return false;
	}*/

/*    
        importe=importe.replace(',','.');
        cargoospim=cargoospim.replace(',','.');
        cargops=cargops.replace(',','.');
*/

    if (frecuencia=="SELECCIONE"){
    	frecuencia="";    
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

    if (flagAmparo == false  && (frecuencia ==null ||  frecuencia=='')){
    		alert('Debe seleccionar la frecuencia correspondiente.');
    		return false ;
=======
   		if (tipoSector.selectedIndex==1){     	       
   			jQuery("#<portlet:namespace />tipoNomencladorSeguimiento_filtro").val(8); // discapacidad 
   		} else if (tipoSector.selectedIndex==6){
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
		
		var observacionMedica = jQuery('#<portlet:namespace />observacion_medica').val();

		
		
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
							   "reclamoobservacion":reclamoobservacion,
							   "observacionMedica":observacionMedica					   
							   };
			
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_revisiones_reclamo" /></portlet:renderURL>';
		
		
		if (resolucion.toUpperCase()!="AUTORIZADO"){
			if(confirm("Confirma el Cierre del Caso con el Rechazo en la revision ?")){
	 			    /* var estadoSelectsector  =document.getElementById("<portlet:namespace/>estado"); */
				    //estadoSelectsector.selectedIndex = 2; // setea el estado en cerrado
				    /* estadoSelectsector.selectedIndex = ubicacionOpcionEstadoCerradoCombo();	 */		    
				    /* jQuery("#<portlet:namespace/>estado option[value='3']").attr("selected", true); //CERARADO */
				    jQuery("#<portlet:namespace/>estado option[value='CERRADO']").attr("selected",true);
				    controlarEstadoCerrado(); // hace visible los controles del estado cerrado
				    
				    document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo").disabled = false;
					
					var tipoSelectsector  =document.getElementById("<portlet:namespace/>tipo_gestion_cierre_reclamo");
					
					seteaControlesFacturacionDirecta(true);
					/* tipoSelectsector.selectedIndex= ubicacionOpcionRechazadoenCombo(); */
				    /* jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo option[value='5']").attr("selected", true); //RECHAZADO */
				    /*jQuery("#<portlet:namespace />tipo_gestion_cierre_reclamo option[value='RECHAZADO']").attr("selected",true);*/
				    
				    
				    jQuery("#<portlet:namespace/>tipo_gestion_cierre_reclamo").val("5");
				    tipoGestionCierreReclamo();
				    
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

/* function ubicacionOpcionRechazadoenCombo(){
	var idselect;
	var pos=0;
	var posicion=0;
		jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo option').each(function(){
        	tipoGestionArray = jQuery(this).val().split("|");
        	idselect =tipoGestionArray [0];         
        	if (idselect == 5){
        	 	posicion=pos;
	        }
        	pos=pos+1;
        });
	return posicion;
} */

/* function ubicacionOpcionEstadoCerradoCombo(){
	var idselect;
	var tipoGestionArray;
	var pos=0;
	var posicion=0;
		jQuery('#<portlet:namespace/>estado option').each(function(){
        	tipoGestionArray = jQuery(this).val().split("|");
        	idselect =tipoGestionArray [0];         
        	if (idselect == 3){
        	 	posicion=pos;
	        }
        	pos=pos+1;
        });
	return posicion;
} */

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


function <portlet:namespace />vercontactosdelreclamo() {
		
	var cuil=jQuery('#<portlet:namespace />cuil').val();
	var inte=jQuery('#<portlet:namespace />inte').val();
	var idreclamoprestacion=jQuery('#<portlet:namespace />idreclamoprestacion').val();
	var modoconsulta=jQuery('#<portlet:namespace />consultareclamo').val();
	
	    if ((cuil=="" || inte=="" )){		
			alert ('Debe seleccionar al Afiliado para ver sus contactos.');
			document.getElementById("<portlet:namespace />cuil").focus();
			return false;
		}	    
			
	    if (document.getElementById("<portlet:namespace />botoncontactosreclamo").value=='Ver Contactos Asociados al Caso.'){
		jQuery("#<portlet:namespace />lista_contactos_reclamo").show();
		jQuery("#<portlet:namespace />botoncontactosreclamo").hide();
		jQuery("#<portlet:namespace />justificacion_medica_reclamo").hide();
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var idreclamoprestacion=jQuery('#<portlet:namespace />idreclamoprestacion').val();		
		
		if ( jQuery("#<portlet:namespace />idreclamoprestacion").val()<1 
				&&  ((cuil==jQuery("#<portlet:namespace />cuiltitular").val()  
						&& inte==jQuery("#<portlet:namespace />intetitular").val() ))  ){			
			return false; // es el mismo afiliado 
		}		
		
		jQuery("#<portlet:namespace />cuiltitular").val(cuil);
		jQuery("#<portlet:namespace />intetitular").val(inte);
		
		var params = {"cuil_contacto":cuil,"inte_contacto":inte,"idreclamoprestacion":idreclamoprestacion,"modoconsulta":modoconsulta};

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/lista_contactos_reclamo" /></portlet:renderURL>';
		
		jQuery('#<portlet:namespace />lista_contactos_reclamo').load(url,params, function(){
										jQuery('#<portlet:namespace />buscando').hide();          															
															  });			 	 
		}					
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
	var cargoimesa= jQuery('#<portlet:namespace />cargoimesaEdicion').val();
	var reconocidoSSS= jQuery('#<portlet:namespace />reconocidoSSSEdicion').val();
	var observaciones= jQuery('#<portlet:namespace />observacion_prestacionEdicion').val();
    var prestacion= "Graba Edicion";
    var idprestacion =  jQuery("#<portlet:namespace />codigoprestacion").val();
    var idRegistro=jQuery('#<portlet:namespace />idRegistro').val();

    var estadoAprobacion = tipoAccion;
    var recuperableSur  =  jQuery('#<portlet:namespace />recuperable_surEdicion').val();  
    
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


    var flagAmparo = false; 
    var estado=jQuery('#<portlet:namespace />estado').val();
	var chk_amparo=jQuery("#<portlet:namespace/>chk_amparo").is(':checked');

	if (estado == 4 && chk_amparo == true ){
		//Si esta en estado inconsistente y es amparo permitimos grabar sin datos de comprobante
		flagAmparo = true;
	}


	// Solo validar montos si completó algo del área médica
	var tieneDatosAreaMedica = (
	    (importe != null && importe != '' && importe != 0) ||
	    (cargoospim != null && cargoospim != '' && cargoospim != 0) ||
	    (cargops != null && cargops != '' && cargops != 0) ||
	    (cargoimesa != null && cargoimesa != '' && cargoimesa != 0) ||
	    (reconocidoSSS != null && reconocidoSSS != '' && reconocidoSSS != 0)
	);

	if (tieneDatosAreaMedica) {
	    if (recuperableSur == 0) {
	        alert('Debe seleccionar el campo Recuperable');
	        return false;
	    }

	    //validación de montos
	    if (!validaMontosEdicion()) {       
	        return false;
	    }
	}
    
	/*
    if (!validaMontosEdicion()){       
   		return false;
	}*/

/*    
        importe=importe.replace(',','.');
        cargoospim=cargoospim.replace(',','.');
        cargops=cargops.replace(',','.');
*/

    if (frecuencia=="SELECCIONE"){
    	frecuencia="";    
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

    if (flagAmparo == false  && (frecuencia ==null ||  frecuencia=='')){
    		alert('Debe seleccionar la frecuencia correspondiente.');
    		return false ;
>>>>>>> .r7305
    }

    if (!jQuery.ajax.__rpAfiliadoNoBloqueante) {
        var ajaxAnterior = jQuery.ajax;
        var ajaxAfiliadoNoBloqueante = function(opciones) {
            if (arguments.length === 1
                    && opciones
                    && typeof opciones === "object"
                    && opciones.async === false) {

                var url = String(opciones.url || "");

                if (esEndpointAfiliadoNoBloqueante(url)) {
                    opciones = jQuery.extend({}, opciones);
                    opciones.async = true;

                    if (opciones.timeout == null) {
                        opciones.timeout = TIMEOUT_AFILIADO_MS;
                    }

                    diagnosticar(
                            "RECLAMO_PRESTACIONAL_AFILIADO_ASYNC",
                            url
                    );

                    return ajaxAnterior.call(this, opciones);
                }
            }

            return ajaxAnterior.apply(this, arguments);
        };

        ajaxAfiliadoNoBloqueante.__rpAfiliadoNoBloqueante = true;
        ajaxAfiliadoNoBloqueante.__rpAjaxAnterior = ajaxAnterior;
        jQuery.ajax = ajaxAfiliadoNoBloqueante;
    }

    if (!jQuery.fn
            || typeof jQuery.fn.load !== "function"
            || jQuery.fn.load.__rpAfiliadoTimeout) {
        return;
    }
<<<<<<< .mine
||||||| .r7295
    
	if (flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && cpbteLetra==''){
		  alert('Debe seleccionar la letra del comprobante');
		  return false;
	}	
    
    if(flagAmparo == false && (importeFC==null || importeFC==0)){
    	alert('Debe ingresar el importe de la Factura.');
		return false ;
    }
    
    if(flagAmparo == false && (cpbteCuit==null || cpbteCuit=='')){
    	alert('Debe ingresar el CUIT del Comprobante');
		return false ;
    }
    
    
    if(flagAmparo == false && (cpbteCuitSucursal==null || cpbteCuitSucursal=='')){
    	alert('Debe ingresar la sucursal del CUIT del Comprobante');
		return false ;
    }
    
    
    if(flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && (cpbteSucursal==null || cpbteSucursal=='')){
    	alert('Debe ingresar la Sucursal del Comprobante');
		return false ;
    }
    
    if(flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && (cpbteNro==null || cpbteNro=='')){
    	alert('Debe ingresar el Nro del Comprobante');
		return false ;
    }
    
    if (flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT') ){
	    if(cpbteDia==null || cpbteDia==0 || cpbteDia=='' ||
	       cpbteMes==null || cpbteMes==-1 || cpbteMes=='' ||
	       cpbteAnio==null || cpbteAnio==0 || cpbteAnio==''){
	       alert('Debe ingresar la fecha del Comprobante');
	       return false;	
	    }
    }
    if(fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ||
    	fechaPrestacionMes==null || fechaPrestacionMes==-1 || fechaPrestacionMes=='' ||
    	fechaPrestacionAnio==null || fechaPrestacionAnio==0 || fechaPrestacionAnio==''){
    	alert('Debe ingresar la fecha de la Prestación');
    	return false;	
    }
    	    
=======
    
	if (flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && cpbteLetra==''){
		  alert('Debe seleccionar la letra del comprobante');
		  return false;
	}	
    
    if(flagAmparo == false && (importeFC==null || importeFC==0)){
    	alert('Debe ingresar el importe de la Factura.');
		return false ;
    }
    
    if(flagAmparo == false && (cpbteCuit==null || cpbteCuit=='')){
    	alert('Debe ingresar el CUIT del Comprobante');
		return false ;
    }
    
    
    if(flagAmparo == false && (cpbteCuitSucursal==null || cpbteCuitSucursal=='')){
    	alert('Debe ingresar la sucursal del CUIT del Comprobante');
		return false ;
    }
    
    
    if(flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && (cpbteSucursal==null || cpbteSucursal=='')){
    	alert('Debe ingresar la Sucursal del Comprobante');
		return false ;
    }
    
    if(flagAmparo == false && (cpbteTipo != 'OTR' && cpbteTipo != 'AUT')  && (cpbteNro==null || cpbteNro=='')){
    	alert('Debe ingresar el Nro del Comprobante');
		return false ;
    }
    
    if (flagAmparo == false){
	    if(cpbteDia==null || cpbteDia==0 || cpbteDia=='' ||
	       cpbteMes==null || cpbteMes==-1 || cpbteMes=='' ||
	       cpbteAnio==null || cpbteAnio==0 || cpbteAnio==''){
	       alert('Debe ingresar la fecha del Comprobante');
	       return false;	
	    }
    }
    if(fechaPrestacionDia==null || fechaPrestacionDia==0 || fechaPrestacionDia=='' ||
    	fechaPrestacionMes==null || fechaPrestacionMes==-1 || fechaPrestacionMes=='' ||
    	fechaPrestacionAnio==null || fechaPrestacionAnio==0 || fechaPrestacionAnio==''){
    	alert('Debe ingresar la fecha de la Prestación');
    	return false;	
    }
    	    
>>>>>>> .r7305

    var loadOriginal = jQuery.fn.load;
    var xhrBusquedaAfiliado = null;

    var loadAfiliadoSeguro = function(url, parametros, callback) {
        if (typeof url !== "string"
                || url.indexOf(
                        "struts_action=/autorizaciones/buscar_afiliados"
                ) < 0) {

            return loadOriginal.apply(this, arguments);
        }

        var destino = this;
        var datos = parametros;
        var completar = callback;
        var metodo = "GET";

        if (typeof parametros === "function") {
            completar = parametros;
            datos = undefined;
        } else if (parametros && typeof parametros === "object") {
            metodo = "POST";
        }

        if (xhrBusquedaAfiliado
                && xhrBusquedaAfiliado.readyState !== 4) {
            xhrBusquedaAfiliado.abort();
        }

        destino.html(
                '<div class="portlet-msg-info">'
                        + 'Buscando afiliados...'
                        + '</div>'
        );

        xhrBusquedaAfiliado = jQuery.ajax({
            url: url,
            type: metodo,
            data: datos,
            dataType: "html",
            timeout: TIMEOUT_AFILIADO_MS,
            success: function(respuesta, estado, xhr) {
                destino.html(respuesta);

                if (typeof completar === "function") {
                    completar.call(
                            destino.length ? destino[0] : destino,
                            respuesta,
                            estado,
                            xhr
                    );
                }
            },
            error: function(xhr, estado) {
                if (estado === "abort") {
                    return;
                }

                destino.html(
                        '<div class="portlet-msg-error">'
                                + 'No se pudo completar la bÃºsqueda de '
                                + 'afiliados. Cierre esta ventana e '
                                + 'intente nuevamente.'
                                + '</div>'
                );

                diagnosticar(
                        "RECLAMO_PRESTACIONAL_AFILIADO_ERROR",
                        estado || "error"
                );

                if (typeof completar === "function") {
                    completar.call(
                            destino.length ? destino[0] : destino,
                            xhr && xhr.responseText
                                    ? xhr.responseText
                                    : "",
                            estado,
                            xhr
                    );
                }
            }
        });

        return destino;
    };

    loadAfiliadoSeguro.__rpAfiliadoTimeout = true;
    loadAfiliadoSeguro.__rpLoadOriginal = loadOriginal;
    jQuery.fn.load = loadAfiliadoSeguro;

    window.ReclamoPrestacionalAfiliadoSearchPatch = {
        timeoutMs: TIMEOUT_AFILIADO_MS,
        esEndpointNoBloqueante: esEndpointAfiliadoNoBloqueante
    };
})(window, window.jQuery);
</script>
<script type="text/javascript">
window.ReclamoPrestacionalJQueryLoadOriginal =
        window.jQuery && window.jQuery.fn ? window.jQuery.fn.load : null;
</script>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf" %>

<script type="text/javascript"
	src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js?v=20260717-legacy-flows-1"
	onerror="window.ReclamoPrestacionalAssetError('view_reclamo.js');"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_tab_guard.js?v=20260717-initial-state-1"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js?v=20260717-initial-state-1"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js?v=20260717-legacy-flows-1"></script>
<script type="text/javascript"
	src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_prestacion_rules_patch.js?v=20260720-recuperable-neutro-2"
	onerror="window.ReclamoPrestacionalAssetError('view_reclamo_prestacion_rules_patch.js');"></script>

<<<<<<< .mine
<script type="text/javascript">
(function(window, jQuery) {
    var config = window.ReclamoPrestacionalViewConfig || {};
    var namespace = config.namespace || "";
||||||| .r7295
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
	 url +='&cpbteCuit='+cpbteCuit;
	 url +='&tipopedido='+tipopedido;
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
		if(codError == '3'){
		   	alert('Prestador CUIT ' +  cpbteCuit  + ' no se encuentra cargado para poder liquidar');
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
=======
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
	 url +='&cpbteCuit='+cpbteCuit;
	 url +='&tipopedido='+tipopedido;
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
		if(codError == '3'){
		   	alert('Prestador CUIT ' +  cpbteCuit  + ' no se encuentra cargado para poder liquidar');
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
		
		if (codError == '7') {
		    alert('La fecha de prestación no puede ser posterior a la fecha de emisión');
		    respuesta = false;
		}
		
	return  respuesta;    
		
}
>>>>>>> .r7305

<<<<<<< .mine
    function esVacio(valor) {
        return valor == null || valor === "" || valor === "0" || valor === "-1";
||||||| .r7295



function ValidaDatosReclamoEditar(){
		
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#<portlet:namespace />fechaComprobanteDiaEdicion').val();
	var cpbte_mes =  jQuery('#<portlet:namespace />fechaComprobanteMesEdicion').val();
	var cpbte_anio = jQuery('#<portlet:namespace />fechaComprobanteAnioEdicion').val();

	    

	fecha_prestacion_dia=jQuery('#<portlet:namespace />fechaPrestacionDiaEdicion').val(); 
	fecha_prestacion_mes=jQuery('#<portlet:namespace />fechaPrestacionMesEdicion').val();
	fecha_prestacion_anio=jQuery('#<portlet:namespace />fechaPrestacionAnioEdicion').val();
	
	var sector=jQuery('#<portlet:namespace />sector').val();
	
    var tipopedido=jQuery('#<portlet:namespace />tipopedido').val();

    var cpbteCuit=jQuery('#<portlet:namespace />cuit_entidad_edicion').val();
	
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
	 url +='&fecha_presacion_anio='+fecha_prestacion_anio;
	 url +='&cpbteCuit='+cpbteCuit;
	 url +='&tipopedido='+tipopedido;	 
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
		if(codError == '3'){
		   	alert('Prestador CUIT ' +  cpbteCuit  + ' no se encuentra cargado para poder liquidar');
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
	    url +='&cuil='+params.cuil;
	    url +='&inte='+params.inte;
		   
    
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
	
	
<%-- <%if (!esEdicion){%>
	document.getElementById("<portlet:namespace/>sector").disabled = "";
<%}%> --%>

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
    



function verCrmContacto(idContSerial) {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.VIEW%>";
		params = params + '&idContactoSerial='+idContSerial;
		
		popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true, width: 880, position:['center',30]});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_contacto_entry';
		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/editar_contacto_entry';
		</c:if>
		url = url + params;
		jQuery(popupCRM).load(url);	
	}
	


function validaMontosEdicion(){	
	
	/* var strimporte =   jQuery('#<portlet:namespace />totalEdicion').val();

    var strcargoospim = jQuery('#<portlet:namespace />cargoospimEdicion').val();
    var strcargops =   jQuery('#<portlet:namespace />cargopsEdicion').val(); */

    //var importedouble = parseFloat(jQuery('#<portlet:namespace />totalEdicion').val());
    var importedouble = parseFloat(jQuery('#<portlet:namespace />totalEdicion').val().replace(",","."));
    
    var cargoospimdouble = parseFloat(jQuery('#<portlet:namespace />cargoospimEdicion').val());
    var cargopsdouble = parseFloat(jQuery('#<portlet:namespace />cargopsEdicion').val());
    var cargoimesadouble = parseFloat(jQuery('#<portlet:namespace />cargoimesaEdicion').val());
    var reconocidoSSS = parseFloat(jQuery('#<portlet:namespace />reconocidoSSSEdicion').val());
    var estado =jQuery("#<portlet:namespace/>estado").val();
    

    var importeFC = parseFloat(jQuery('#<portlet:namespace />importeFC').val());
    var importeFCEdicion = parseFloat(jQuery('#<portlet:namespace />importeFC_edicion').val());
    if(isNaN(importeFC)) {
//	jQuery('#<portlet:namespace />importeFC').val();
	   importeFC=0;
=======



function ValidaDatosReclamoEditar(){
		
	var respuesta=true;
	var codError='';	
	var cpbte_dia =  jQuery('#<portlet:namespace />fechaComprobanteDiaEdicion').val();
	var cpbte_mes =  jQuery('#<portlet:namespace />fechaComprobanteMesEdicion').val();
	var cpbte_anio = jQuery('#<portlet:namespace />fechaComprobanteAnioEdicion').val();

	    

	fecha_prestacion_dia=jQuery('#<portlet:namespace />fechaPrestacionDiaEdicion').val(); 
	fecha_prestacion_mes=jQuery('#<portlet:namespace />fechaPrestacionMesEdicion').val();
	fecha_prestacion_anio=jQuery('#<portlet:namespace />fechaPrestacionAnioEdicion').val();
	
	var sector=jQuery('#<portlet:namespace />sector').val();
	
    var tipopedido=jQuery('#<portlet:namespace />tipopedido').val();

    var cpbteCuit=jQuery('#<portlet:namespace />cuit_entidad_edicion').val();
	
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
	 url +='&cpbteCuit='+cpbteCuit;
	 url +='&tipopedido='+tipopedido;	 
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
		if(codError == '3'){
		   	alert('Prestador CUIT ' +  cpbteCuit  + ' no se encuentra cargado para poder liquidar');
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
		
		if (codError == '7') {
		    alert('La fecha de prestación no puede ser posterior a la fecha de emisión');
		    respuesta = false;
		}
		
	return  respuesta;    
		
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
	    url +='&cuil='+params.cuil;
	    url +='&inte='+params.inte;
		   
    
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
	
	
<%-- <%if (!esEdicion){%>
	document.getElementById("<portlet:namespace/>sector").disabled = "";
<%}%> --%>

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
    



function verCrmContacto(idContSerial) {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.VIEW%>";
		params = params + '&idContactoSerial='+idContSerial;
		
		popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true, width: 880, position:['center',30]});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_contacto_entry';
		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/editar_contacto_entry';
		</c:if>
		url = url + params;
		jQuery(popupCRM).load(url);	
	}
	


function validaMontosEdicion(){	
	
	/* var strimporte =   jQuery('#<portlet:namespace />totalEdicion').val();

    var strcargoospim = jQuery('#<portlet:namespace />cargoospimEdicion').val();
    var strcargops =   jQuery('#<portlet:namespace />cargopsEdicion').val(); */

    //var importedouble = parseFloat(jQuery('#<portlet:namespace />totalEdicion').val());
    var importedouble = parseFloat(jQuery('#<portlet:namespace />totalEdicion').val().replace(",","."));
    
    var cargoospimdouble = parseFloat(jQuery('#<portlet:namespace />cargoospimEdicion').val());
    var cargopsdouble = parseFloat(jQuery('#<portlet:namespace />cargopsEdicion').val());
    var cargoimesadouble = parseFloat(jQuery('#<portlet:namespace />cargoimesaEdicion').val());
    var reconocidoSSS = parseFloat(jQuery('#<portlet:namespace />reconocidoSSSEdicion').val());
    var estado =jQuery("#<portlet:namespace/>estado").val();
    

    var importeFC = parseFloat(jQuery('#<portlet:namespace />importeFC').val());
    var importeFCEdicion = parseFloat(jQuery('#<portlet:namespace />importeFC_edicion').val());
    if(isNaN(importeFC)) {
//	jQuery('#<portlet:namespace />importeFC').val();
	   importeFC=0;
>>>>>>> .r7305
    }

    function normalizarFechaOpcional(prefijo) {
        var dia = jQuery("#" + namespace + prefijo + "Dia");
        var mes = jQuery("#" + namespace + prefijo + "Mes");
        var anio = jQuery("#" + namespace + prefijo + "Anio");

        if (!dia.length || !mes.length || !anio.length) {
            return;
        }

        if (esVacio(dia.val()) && esVacio(mes.val()) && esVacio(anio.val())) {
            dia.val("");
            mes.val("");
            anio.val("");
        }
    }

    function normalizarFechasOpcionales() {
        normalizarFechaOpcional("fechaseccional");
        normalizarFechaOpcional("fechacierre");
    }

    var submitFormOriginal = window.submitForm;
    if (typeof submitFormOriginal === "function" && !submitFormOriginal.__rpP0Normalizado) {
        var submitFormNormalizado = function(formulario) {
            if (formulario && formulario.name === namespace + "reclamo_fm") {
                normalizarFechasOpcionales();
            }
            return submitFormOriginal.apply(this, arguments);
        };
        submitFormNormalizado.__rpP0Normalizado = true;
        window.submitForm = submitFormNormalizado;
    }

    jQuery("#" + namespace + "reclamo_fm").submit(normalizarFechasOpcionales);
})(window, jQuery);

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

function <portlet:namespace />vincularFechasPrestacionEdicion() {
	var handler = <portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion;
	var dia = jQuery("#<portlet:namespace />fechaPrestacionDiaEdicion");
	var mes = jQuery("#<portlet:namespace />fechaPrestacionMesEdicion");
	var anio = jQuery("#<portlet:namespace />fechaPrestacionAnioEdicion");

	dia.unbind("change", handler).bind("change", handler);
	mes.unbind("change", handler).bind("change", handler);
	anio.unbind("change", handler).bind("change", handler);
}

jQuery("#<portlet:namespace />fechaPrestacionDia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionMes").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionAnio").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionDiaFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionMesFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionAnioFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery(document).ready(function() {
	<portlet:namespace />vincularFechasPrestacionEdicion();
});

jQuery(document).ajaxComplete(function(evento, xhr, opciones) {
	var url = opciones && opciones.url ? String(opciones.url) : "";
	if (url.indexOf("editar_reclamosprestaciones") >= 0) {
		<portlet:namespace />vincularFechasPrestacionEdicion();
	}
});


</script>
