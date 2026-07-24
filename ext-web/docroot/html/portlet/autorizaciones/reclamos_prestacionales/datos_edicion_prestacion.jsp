<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%
//prestacion en proceso de edicion 
PrestacionesReclamo  prestacionEnEdicion  = (PrestacionesReclamo) request.getSession().getAttribute(WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION   );
request.getSession().removeAttribute(WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION);
Integer tipoedicion=0;
String ocultarSeccional=null;

Calendar fechaseccional  = Calendar.getInstance();

Calendar fechaPrestacion  = Calendar.getInstance();


if (prestacionEnEdicion != null) {
	Object tipoEdicionObj = request.getAttribute("tipoEdicion");
	tipoedicion = tipoEdicionObj instanceof Integer
			? (Integer) tipoEdicionObj
			: Integer.valueOf(0);

	if (prestacionEnEdicion.getComprobanteFecha() != null) {
		 fechaseccional.setTime(prestacionEnEdicion.getComprobanteFecha());
	 }
	 if(prestacionEnEdicion.getFechaPrestacion() !=null){
		 fechaPrestacion.setTime(prestacionEnEdicion.getFechaPrestacion());
	 }
}

String captionbotoncancelar =
		"Cancelar Edición de la Prestación";
String captionlabelproceso =
		"PRESTACION EN PROCESO DE EDICION";
String estiloLabel = "";

if (tipoedicion == 1) {
	captionbotoncancelar =
			"Cancelar Autorización de la Prestación";
	captionlabelproceso =
			"PRESTACION EN PROCESO DE AUTORIZACION";
	estiloLabel = "style='color:green;'";
}

if (tipoedicion == 2) {
	captionbotoncancelar =
			"Cancelar Rechazo de la Prestación";
	captionlabelproceso =
			"PRESTACION EN PROCESO DE RECHAZO";
	estiloLabel = "style='color:red;'";
}

ocultarSeccional = (String) request.getAttribute("ocultar");

if (prestacionEnEdicion != null) {
	String codigoPrestacionEdicion = Validator.isNotNull(prestacionEnEdicion.getCodigoPrestacion())
			? prestacionEnEdicion.getCodigoPrestacion()
			: "";
	String descripcionPrestacionEdicion = Validator.isNotNull(prestacionEnEdicion.getDescripcion())
			? prestacionEnEdicion.getDescripcion()
			: "";
%>
<input type="hidden"
	id="<portlet:namespace />idRegistro"
	name="<portlet:namespace />idRegistro"
	value="<%= prestacionEnEdicion.getIdRegistro() %>" />

<label <%= estiloLabel %>><b><liferay-ui:message key="<%= captionlabelproceso %>" /></b></label>
        		
		<table class="lfr-table"
			style="border-collapse: separate; border-spacing: 3px;">
			<tr>
				<td>
					<label>F. Prestación: </label>
					<liferay-ui:input-date
						dayParam="fechaPrestacionDiaEdicion"
						dayValue="<%= prestacionEnEdicion.getFechaPrestacion() != null
								? fechaPrestacion.get(Calendar.DAY_OF_MONTH)
								: 0 %>"
						dayNullable="<%= true %>"
						monthParam="fechaPrestacionMesEdicion"
						monthValue="<%= prestacionEnEdicion.getFechaPrestacion() != null
								? fechaPrestacion.get(Calendar.MONTH)
								: -1 %>"
						monthNullable="<%= true %>"
						yearParam="fechaPrestacionAnioEdicion"
						yearValue="<%= prestacionEnEdicion.getFechaPrestacion() != null
								? fechaPrestacion.get(Calendar.YEAR)
								: -1 %>"
						yearNullable="<%= true %>"
						yearRangeStart="<%= fechaseccional.get(Calendar.YEAR) - 5 %>"
						yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR) + 1 %>"
						firstDayOfWeek="" />
				</td>

				<%
				int idPrest =
						prestacionEnEdicion.getId_prestacion();
				int idMedic =
						prestacionEnEdicion.getId_medicamento();

				boolean sinMedicamento =
						idMedic == 0;
				boolean hayPrestacion =
						idPrest != 0;
				boolean mostrarCodigoPresentado =
						hayPrestacion || sinMedicamento;
				%>

				<% if (mostrarCodigoPresentado) { %>
					<td>
						<label><liferay-ui:message key="codigo-presentado" />:</label>
					</td>
					<td>
						<input
							id="<portlet:namespace />codigoSeguimiento_filtro_edit"
							name="<portlet:namespace />codigoSeguimiento_filtro_edit"
							size="10"
							maxlength="20"
							type="text"
							value="<%= HtmlUtil.escape(
        Validator.isNotNull(
                prestacionEnEdicion.getCodigoPrestacion()
        )
        ? prestacionEnEdicion.getCodigoPrestacion()
        : ""
) %>" />
					</td>
					<td>
						<input
							id="<portlet:namespace />descripcionSeguimiento_filtro_edit"
							name="<portlet:namespace />descripcionSeguimiento_filtro_edit"
							size="60"
							maxlength="200"
							type="text"
							value="<%= HtmlUtil.escape(
									Validator.isNotNull(
											prestacionEnEdicion.getDescripcion()
									)
									? prestacionEnEdicion.getDescripcion()
									: ""
							) %>" />
					</td>
					<td>
						<div
							id="<portlet:namespace />divBtnBuscaEdicion"
							style="width: 4%;">
							<a
								href="javascript:void(0);"
								onclick="<portlet:namespace />buscarNomencladorAutocompletar_edit();"
								tabindex="-1">Buscar</a>
							<a
								href="javascript:void(0);"
								onclick="<portlet:namespace />limpiarNomencladorAutocompletar();"
								tabindex="-1">Limpiar</a>
						</div>
					</td>
				<% } else { %>
					<td colspan="4">
						<liferay-util:include
							page="/html/portlet/utils/medicamentos_edit/busqueda_medicamentos_edit.jsp">
							<liferay-util:param
								name="search_url_edit"
								value="/autorizaciones/buscar_medicamentos_edit" />
							<liferay-util:param name="troquel" value="" />
							<liferay-util:param
								name="nombre_medicamento_edit"
								value="" />
							<liferay-util:param
								name="id_medicamento_edit"
								value="" />
							<liferay-util:param
								name="esEditable"
								value="true" />
							<liferay-util:param
								name="mostrar_con_presentacion_edit"
								value="true" />
						</liferay-util:include>
					</td>
				<% } %>
			</tr>
		</table>
		
      
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>			
			<td colspan="15">
			 <div id="<portlet:namespace />datos_comprobante">
		     <fieldset class="block-labels">
	         <legend>
		         <liferay-ui:message key="Datos del Comprobante" />
	         </legend>	
			    <table>
			    <%if(ocultarSeccional == null){%>
			      <tr>
			      <td><label><liferay-ui:message key="Frecuencia" />:</label></td>	
			      <td>
					<select 
						name="<portlet:namespace />frecuenciaEdicion"
						id="<portlet:namespace />frecuenciaEdicion" >  
						<option value="SELECCIONE" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("SELECCIONE") ? "selected":"" %>>SELECCIONE</option>
						<option value="UNICA" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("UNICA") ? "selected":""%>>UNICA</option>
						<option value="SEMANAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("SEMANAL") ? "selected":""%>>SEMANAL</option>
						<option value="TRIMESTRAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("TRIMESTRAL") ? "selected":""%>>TRIMESTRAL</option>
						<option value="MENSUAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("MENSUAL") ? "selected":""%>>MENSUAL</option>
						<option value="SEMESTRAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("SEMESTRAL") ? "selected" :""%>>SEMESTRAL</option>					
						<option value="ANUAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("ANUAL") ? "selected" :""%>>ANUAL</option>
					</select>
				  </td>	  	
				<%}%>
				  <td><label><liferay-ui:message key="comprobante" />:</label></td>
			      <td>
			         <select name="<portlet:namespace/>comprobante_tipo_edicion" id="<portlet:namespace/>comprobante_tipo_edicion">
				        <option value="FCP"  <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteTipo().equals("FCP") ? "selected":"" %>>FCP</option>
				        <option value="RCB" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteTipo().equals("RCB") ? "selected":""%>>RCB</option>
				        <option value="OTR" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteTipo().equals("OTR") ? "selected":""%>>OTR</option>
				        <option value="AUT" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteTipo().equals("AUT") ? "selected":""%>>AUT</option>
			         </select> 
			      </td>
			      
			      	<td><label><liferay-ui:message key="letra" />:</label></td>
					<td>
					<select name="<portlet:namespace/>comprobante_letra_edicion"
					id="<portlet:namespace/>comprobante_letra_edicion">
					</select></td>
			      <td>Suc:</td>
			      <td> 
			        <input id="<portlet:namespace />comprobante_suc_edicion"
				        name="<portlet:namespace />comprobante_suc_edicion" size="8" maxlength="5"
				        type="text"	
				        onblur="this.value = completarConCeros(this.value,5);"				        
				        value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteSucursal()!=null?prestacionEnEdicion.getComprobanteSucursal():""%>'/>
			      </td>  	
			      
			      
			      <td>Nro:</td>
			      <td> 
			        <input id="<portlet:namespace />comprobante_nro_edicion"
				        name="<portlet:namespace />comprobante_nro_edicion" size="11" maxlength="15"
				        type="text"	
				        onblur="this.value = completarConCeros(this.value,8);"				        				        
				        value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteNro()!=null?prestacionEnEdicion.getComprobanteNro():""%>'/>
			      </td>  	
			      <td><label>F.Emision:</label></td>
			      <td colspan="1"><liferay-ui:input-date dayParam="fechaComprobanteDiaEdicion"
					   dayValue='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteFecha()!=null?fechaseccional.get(Calendar.DAY_OF_MONTH ):0%>' 
					   dayNullable="<%=true %>"
					   monthParam="fechaComprobanteMesEdicion"
					   monthValue='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteFecha()!=null?fechaseccional.get(Calendar.MONTH):-1 %>'					
					   monthNullable="<%= true %>"
					   yearParam="fechaComprobanteAnioEdicion"
					   yearValue='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteFecha()!=null?fechaseccional.get(Calendar.YEAR):-1 %>'
					   yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
					   yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR) +5 %>"
					   yearNullable="<%= true %>"
					   firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
					   />
			      </td>
			     </tr>
			  <tr>
			       <td colspan="15"><liferay-util:include
					page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					<liferay-util:param name="esEditable"
							value='<%= String.valueOf( "true" ) %>' />
						<liferay-util:param name="cuit" value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteCUIT()!=null?prestacionEnEdicion.getComprobanteCUIT():""%>' />
						<liferay-util:param name="sucu" value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteCUITSucursal()!=null?prestacionEnEdicion.getComprobanteCUITSucursal():""%>' />
						<liferay-util:param name="razon" value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteRazonSocial()!=null?prestacionEnEdicion.getComprobanteRazonSocial():""%>' />
						<liferay-util:param name="id_seccional" value='' />
						<liferay-util:param name="esEmpresaPrestador" value='true' />
						<liferay-util:param name="suf_entidad" value='_edicion'/>		
						<liferay-util:param name="suf" value='_edicion'/>		
					</liferay-util:include>
			  </td>
			</tr>
			
		         <tr>
			     <td><label><liferay-ui:message key="Cantidad" />:</label> </td>
			     <td><input id="<portlet:namespace />cantidadFC_edicion"   
				   name="<portlet:namespace />cantidadFC_edicion" size="8" maxlength="20" type="text" value='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteCantidad()!=null?prestacionEnEdicion.getComprobanteCantidad():"" %>'
				   onblur="calculatotalFCEdicion()" /> </td>
						
			     <td><label><liferay-ui:message key="Importe" />:</label> </td>
			     <td><input id="<portlet:namespace />importeUnitarioFC_edicion"   
				    name="<portlet:namespace />importeUnitarioFC_edicion" size="12" maxlength="20" 
				    value='<%= prestacionEnEdicion != null && prestacionEnEdicion.getComprobanteImporte() != null ?
            				new BigDecimal(prestacionEnEdicion.getComprobanteImporte().toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() : "" %>' type="text"  
				    onkeydown="allowOnlyDigitsAndDecimals(event)"	
				    onblur="calculatotalFCEdicion()"/> </td>
			
							
			     <td><label>Total Comprobante:</label> </td>		
			     <td>
			        <input id="<portlet:namespace />importeFC_edicion"   
				    name="<portlet:namespace />importeFC_edicion" size="12" maxlength="20" 
				    value ='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteTotal()!=null?
				    		new BigDecimal(prestacionEnEdicion.getComprobanteTotal().toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString():"" %>' 
				    type="text" onkeydown="allowOnlyDigitsAndDecimals(event)" readonly="readonly"/>
			     </td>
				  
				 </tr>	
				</table>
				
			</fieldset>
			</div>	
		  </td>
	</tr>
<tr>		
	
<td colspan="8">
	<div id="<portlet:namespace />Autorizado">
		<fieldset class="block-labels">
			<legend>
				<liferay-ui:message key="Autorizado por Área Médica:" />
			</legend>

			<table>
				<tr>
					<td>
						<label><liferay-ui:message key="Cantidad" />:</label>
					</td>
					<td>
						<input
							id="<portlet:namespace />cantidadEdicion"
							name="<portlet:namespace />cantidadEdicion"
							size="2"
							maxlength="20"
							type="text"
							value='<%= Validator.isNotNull(prestacionEnEdicion)
									? prestacionEnEdicion.getCantidad()
									: "" %>'
							onkeypress="return validaMonto(event,this)"
							onblur="calculatotal()" />
					</td>

					<td>
						<label><liferay-ui:message key="Importe" />:</label>
					</td>
					<td>
						<input
							id="<portlet:namespace />importeEdicion"
							name="<portlet:namespace />importeEdicion"
							size="12"
							maxlength="20"
							type="text"
							value='<%= Validator.isNotNull(prestacionEnEdicion)
									? new BigDecimal(
											prestacionEnEdicion.getImporte()
									).setScale(
											2,
											RoundingMode.HALF_UP
									).toPlainString()
									: "" %>'
							onkeypress="return validaMonto(event,this)"
							onblur="calculatotal()" />
					</td>

					<td>
						<label><liferay-ui:message key="Total" />:</label>
					</td>
					<td>
						<input
							id="<portlet:namespace />totalEdicion"
							name="<portlet:namespace />totalEdicion"
							size="12"
							maxlength="20"
							readonly="readonly"
							type="text"
							value='<%= Validator.isNotNull(prestacionEnEdicion)
									? prestacionEnEdicion.getTotalString()
									: "" %>' />
					</td>

					<td>
						<label><liferay-ui:message key="Cargo OSPIM" />:</label>
					</td>
					<td>
						<input
							id="<portlet:namespace />cargoospimEdicion"
							name="<portlet:namespace />cargoospimEdicion"
							size="12"
							maxlength="20"
							type="text"
							value='<%= Validator.isNotNull(prestacionEnEdicion)
									? new BigDecimal(
											prestacionEnEdicion.getCargo_ospim()
									).setScale(
											2,
											RoundingMode.HALF_UP
									).toPlainString()
									: "" %>'
							onkeypress="return validaMonto(event,this)"
							onkeydown="allowOnlyDigitsAndDecimals(event)" />
					</td>

					<td>
						<label><liferay-ui:message key="Cargo Prestadora" />:</label>
					</td>
					<td>
						<input
							id="<portlet:namespace />cargopsEdicion"
							name="<portlet:namespace />cargopsEdicion"
							size="12"
							maxlength="20"
							type="text"
							value='<%= Validator.isNotNull(prestacionEnEdicion)
									? new BigDecimal(
											prestacionEnEdicion.getCargo_ps()
									).setScale(
											2,
											RoundingMode.HALF_UP
									).toPlainString()
									: "" %>'
							onkeypress="return validaMonto(event,this)" />
					</td>

					<td>
						<label><liferay-ui:message key="Cargo Monotributo" />:</label>
					</td>
					<td>
						<input
							id="<portlet:namespace />cargoimesaEdicion"
							name="<portlet:namespace />cargoimesaEdicion"
							size="12"
							maxlength="20"
							type="text"
							value='<%= Validator.isNotNull(prestacionEnEdicion)
									? new BigDecimal(
											prestacionEnEdicion.getCargo_imesa()
									).setScale(
											2,
											RoundingMode.HALF_UP
									).toPlainString()
									: "" %>'
							onkeypress="return validaMonto(event,this)"
							onkeydown="allowOnlyDigitsAndDecimals(event)" />
					</td>

					<td>
						<label>Reconocido SSS:</label>
					</td>
					<td>
						<input
							id="<portlet:namespace />reconocidoSSSEdicion"
							name="<portlet:namespace />reconocidoSSSEdicion"
							size="12"
							maxlength="20"
							type="text"
							value='<%= Validator.isNotNull(prestacionEnEdicion)
									? prestacionEnEdicion.getReconocidoSSS()
									: "" %>'
							onkeypress="return validaMonto(event,this)" />
					</td>

					<td>
						<label><liferay-ui:message key="Recuperable SUR" />:</label>
					</td>
					<td>
						<select
							name="<portlet:namespace />recuperable_surEdicion"
							id="<portlet:namespace />recuperable_surEdicion"
							onchange="cambiorecuperableEdicion();">
							<option value="0">
								Seleccione Integración
							</option>
							<option
								value="1"
								<%= Validator.isNotNull(prestacionEnEdicion)
										&& prestacionEnEdicion.getRecuperable() != null
										&& prestacionEnEdicion.getRecuperable() == 1
										? "selected"
										: "" %>>
								SUR
							</option>
							<option
								value="3"
								<%= Validator.isNotNull(prestacionEnEdicion)
										&& prestacionEnEdicion.getRecuperable() != null
										&& prestacionEnEdicion.getRecuperable() == 3
										? "selected"
										: "" %>>
								Integración
							</option>
							<option
								value="2"
								<%= Validator.isNotNull(prestacionEnEdicion)
										&& prestacionEnEdicion.getRecuperable() != null
										&& prestacionEnEdicion.getRecuperable() == 2
										? "selected"
										: "" %>>
								NO Recuperable
							</option>
						</select>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</td>
</tr>
<tr>
	<td id="<portlet:namespace />observacion_prestacionEdicion_label">
		<c:choose>
			<c:when test="<%= tipoedicion.intValue() == 1 %>">
				<liferay-ui:message key="Observacion Autorizacion" />:
			</c:when>
			<c:when test="<%= tipoedicion.intValue() == 2 %>">
				<liferay-ui:message key="Observacion Rechazo" />:
			</c:when>
			<c:otherwise>
				<liferay-ui:message key="Observacion Edicion" />:
			</c:otherwise>
		</c:choose>
	</td>

	<td>
		<textarea
			rows="3"
			cols="70"
			id="<portlet:namespace />observacion_prestacionEdicion"
			maxlength="250"
			name="<portlet:namespace />observacion_prestacionEdicion"><%= Validator.isNotNull(
					prestacionEnEdicion.getObservaciones()
			) ? prestacionEnEdicion.getObservaciones() : "" %></textarea>
	</td>

	<td>
		<div id="<portlet:namespace />botones_edicion_prestacion">
			<% if (tipoedicion.intValue() == 0) { %>
				<input
					type="button"
					name="<portlet:namespace />btnedita_prestacion"
					id="<portlet:namespace />btnedita_prestacion"
					value="Editar Prestaci&oacute;n"
					onclick="<portlet:namespace />editarPrestacionSeleccionada(0);"
					title="Edita la prestaci&oacute;n" />
			<% } %>

			<% if (tipoedicion.intValue() == 1) { %>
				<input
					type="button"
					name="<portlet:namespace />btnautoriza_prestacion"
					id="<portlet:namespace />btnautoriza_prestacion"
					value="Autoriza Prestaci&oacute;n"
					onclick="<portlet:namespace />editarPrestacionSeleccionada(1);"
					title="Autoriza la prestaci&oacute;n" />
			<% } %>

			<% if (tipoedicion.intValue() == 2) { %>
				<input
					type="button"
					name="<portlet:namespace />btnrechaza_prestacion"
					id="<portlet:namespace />btnrechaza_prestacion"
					value="Rechaza Prestaci&oacute;n"
					onclick="<portlet:namespace />editarPrestacionSeleccionada(2);"
					title="Rechaza la prestaci&oacute;n" />
			<% } %>

			<input
				type="button"
				name="<portlet:namespace />btncancelar_prestacion"
				id="<portlet:namespace />btncancelar_prestacion"
				value="<%= HtmlUtil.escape(captionbotoncancelar) %>"
				onclick="<portlet:namespace />cancelaEdicionPrestacion();" />
		</div>
	</td>
</tr>
</table>

<script type="text/javascript">
filtrarLetraComprobanteEdicion();
cambiorecuperableEdicion();

function calculatotal() {
	var importe = String(
			jQuery("#<portlet:namespace />importeEdicion").val() || ""
	).replace(",", ".");
	var cantidad = String(
			jQuery("#<portlet:namespace />cantidadEdicion").val() || ""
	).replace(",", ".");
	var total = parseFloat(importe) * parseFloat(cantidad);

	if (isNaN(total)) {
		total = 0;
	}

	jQuery("#<portlet:namespace />totalEdicion").val(total.toFixed(2));
}

function calculatotalFCEdicion() {
	var importe = String(
			jQuery("#<portlet:namespace />importeUnitarioFC_edicion").val() || ""
	).replace(",", ".");
	var cantidad = String(
			jQuery("#<portlet:namespace />cantidadFC_edicion").val() || ""
	).replace(",", ".");
	var total = parseFloat(importe) * parseFloat(cantidad);

	if (isNaN(total)) {
		total = 0;
	}

	jQuery("#<portlet:namespace />importeFC_edicion").val(
			Math.round(total * 100) / 100
	);
}

function filtrarLetraComprobanteEdicion() {
	var tipoPedido =
			jQuery("#<portlet:namespace />tipopedido").val() || "";
	var url =
			'<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>'
			+ '&struts_action=/autorizaciones/filtrarLetraComprobante'
			+ '&tipo_pedido=' + tipoPedido;
	var select =
			jQuery("#<portlet:namespace />comprobante_letra_edicion");

	if (!select.length) {
		return;
	}

	select.attr("disabled", "disabled");

	jQuery.ajax({
		url: url,
		type: "GET",
		dataType: "html",
		cache: false,
		success: function(data) {
			select.html(data);
			jQuery(
					"#<portlet:namespace />comprobante_letra_edicion"
			).val(
					"<%= prestacionEnEdicion.getComprobanteLetra() != null
							? prestacionEnEdicion.getComprobanteLetra()
							: "" %>"
			);
		},
		error: function() {
			select.empty().append(
					jQuery("<option/>", {
						value: "",
						text: "No se pudo cargar"
					})
			);
		},
		complete: function() {
			select.removeAttr("disabled");
		}
	});
}

function cambiorecuperableEdicion() {
	var recuperable = String(
			jQuery(
					"#<portlet:namespace />recuperable_surEdicion"
			).val() || ""
	);
	var reconocido =
			jQuery("#<portlet:namespace />reconocidoSSSEdicion");

	if (!reconocido.length) {
		return;
	}

	if (recuperable === "1" || recuperable === "3") {
		reconocido.removeAttr("readonly");
	} else {
		reconocido.val("0").attr("readonly", "readonly");
	}
}

function completarConCeros(value, longitud) {
	var digitos = String(value || "").replace(/\D/g, "");
	var ceros = "";
	var i;

	if (!digitos) {
		return "";
	}

	for (i = 0; i < longitud; i++) {
		ceros += "0";
	}

	return (ceros + digitos).slice(-longitud);
}
</script>

<script type="text/javascript">
jQuery(function() {
    var namespace = "<portlet:namespace />";
    var codigo = jQuery("#" + namespace + "codigoSeguimiento_filtro_edit").val() || "";

    jQuery("#" + namespace + "datos_edicion_prestacion").show();
    jQuery("#" + namespace + "codigoprestacion").val(codigo);

    <% if (prestacionEnEdicion.getId_prestacion() == 0
            && prestacionEnEdicion.getId_medicamento() != 0) { %>
    jQuery("#" + namespace + "troquel_edit").val(
            "<%= prestacionEnEdicion.getId_medicamento() %>"
    );
    <% } %>

    <% if (ocultarSeccional != null) { %>
    jQuery("#" + namespace + "Autorizado").hide();
    <% } %>
});
</script>

<%
}
%>
