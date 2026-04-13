<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ page import="ar.com.ospim.global.EmpresaNoExisteConTalCuitException" %>
<%@ page import="ar.com.ospim.afiliados.exceptions.CuilInvalidoException" %>

<%

DetalleOpcionesSS detOpcionSS = (DetalleOpcionesSS)session.getAttribute(WebKeysAfiliados.OPCIONSS_EN_EDICION);

Calendar eleccionFecha = CalendarFactoryUtil.getCalendar();
Calendar certificaFecha = CalendarFactoryUtil.getCalendar();

Date elecFechaAfil = detOpcionSS != null ? detOpcionSS.getFechaElecc() : null; 
Date certFechaAfil = detOpcionSS != null ? detOpcionSS.getFechaCerti() : null;

String opciones=(String)request.getParameter("opciones");
String esViewStr=(String)request.getAttribute("esView");

boolean esView = false;
String esEdicion = "editar";

if(esViewStr != null && esViewStr.equalsIgnoreCase("view")){
	esView = true;
	esViewStr = "true";
	esEdicion = "vista";
}

if (elecFechaAfil != null) {
	eleccionFecha.setTime(detOpcionSS.getFechaElecc());
}
if (certFechaAfil != null) {
	certificaFecha.setTime(detOpcionSS.getFechaCerti());
} else {
	//eleccionFecha.setTime(new Date());
}

List<Delegacion> delegaciones = (ArrayList<Delegacion>) portletSession
.getAttribute(WebKeysAfiliados.DELEGACIONES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (delegaciones == null) {
delegaciones = TraeListasServiceUtil.getDelegaciones();
portletSession.setAttribute(
	WebKeysAfiliados.DELEGACIONES_EN_SESSION,
	delegaciones,
	PortletSession.APPLICATION_SCOPE);
}

String locDesc="";
int locId=0;
if(detOpcionSS != null && detOpcionSS.getLocalidad() != null){
	locId = Integer.parseInt(detOpcionSS.getLocalidad());
	for(int i=0; i < localidades.size(); i++){
		if(locId == localidades.get(i).getId_localidadesss() ){
			/* locId=localidades.get(i).getId_localidadesss(); */
			locDesc=localidades.get(i).getDescripcion();
		}
		if(locDesc != ""){
			break;
		}
	}
}
%>
<liferay-ui:error exception="<%= FormOpcionSSSDuplicadoException.class %>"
	message="the-opcionsss-key-already-exists" />
<liferay-ui:error exception="<%= FormOpcionSSSNoEnviadoException.class %>"
	message="the-opcionsss-form-sin-enviar" />	
<liferay-ui:error exception="<%= FormOpcionSSSValidacionFechasException.class %>"
	message="the-opcionsss-form-fechas" />	
<liferay-ui:error exception="<%= EmpresaNoExisteConTalCuitException.class %>"
	message="empresa-inexistente-cuit" />	
<liferay-ui:error exception="<%= NoExisteFechaConfiguradaPressSuper.class %>"
	message="no_hay_fechas_press_sss" />	
<liferay-ui:error exception="<%= FormOpcionSSSFechaIgualPressException.class %>"
	message="es_fecha_press_super" />		
		
<liferay-ui:error exception="<%= FormOpcionSSSInvalidoException.class %>" message="<%=(String)request.getAttribute(\"msgOpcionSSSfail\")  %>"/>
<liferay-ui:error exception="<%= CuilInvalidoException.class %>" message="<%=(String)request.getAttribute(\"msgOpcionSSSfail\")  %>"/>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="delegacion" />
	</legend>
	</br>
	<div>
	<table class="lfr-table">
		<tr>
			<input type="hidden" name="<portlet:namespace />id_opcionsss" id="<portlet:namespace />id_opcionsss" value="<%=detOpcionSS != null ? detOpcionSS.getId() : 0%>" />
			
			<td><label><liferay-ui:message key="delegacion" />:</label></td>
			<td colspan="5"><liferay-util:include page="/html/portlet/afiliados/busqueda_delegacion.jsp">
					<liferay-util:param name="id_delegacion" value="<%= detOpcionSS != null ? String.valueOf(detOpcionSS.getDelegacionId()) : new String()  %>" />
					<liferay-util:param name="delegacion"
						value="<%= detOpcionSS != null ? detOpcionSS.getDelegacion() : new String()  %>" />
					<liferay-util:param name="esEdicion" value="<%=esEdicion%>" />
				</liferay-util:include></td>
		
			<td><label><liferay-ui:message key="libro" />:</label></td>
			<td><input id="<portlet:namespace />libro"
				name="<portlet:namespace />libro" size="5" maxlength="5" readonly="readonly"
				type="text" value="<%=detOpcionSS!=null ? detOpcionSS.getLibro():"" %>" /></td>
			<td><label><liferay-ui:message key="tomo" />:</label></td>
			<td><input id="<portlet:namespace />tomo"
				name="<portlet:namespace />tomo" size="5" maxlength="5" readonly="readonly"
				type="text" value="<%=detOpcionSS!=null ? detOpcionSS.getTomo():"" %>"   /></td>	
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
	</table>
	</div>
</fieldset>
	</br>
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="datos-afiliado" />
	</legend>
	</br>

	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="formNro" />:</label></td>
			<td width="210px;"><input type="text" id="<portlet:namespace />formNro" maxlength="9" name="<portlet:namespace />formNro" 
					   value="<%=detOpcionSS!=null ? detOpcionSS.getNroFormulario():"" %>" 
					   size="10" maxlength="10" onkeydown="allowOnlyDigits(event);"  <%if(esView){ %> readonly="readonly" <% } %> /></td>
			<%-- <td><label><liferay-ui:message key="apeynom" /> </label></td>
			<td><input type="text" id="<portlet:namespace />apeynom" name="<portlet:namespace />apeynom" value="<%=detOpcionSS!=null ? detOpcionSS.getApeNom():"" %>" size="30" maxlength="30" /></td>
			 --%>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td><input type="text" id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" value="<%=detOpcionSS!=null ? detOpcionSS.getApellido():"" %>" 
					   size="20" maxlength="50" <%if(esView){ %> readonly="readonly" <% } %> /></td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td><input type="text" id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" value="<%=detOpcionSS!=null ? detOpcionSS.getNombre():"" %>" 
			           size="20" maxlength="50" <%if(esView){ %> readonly="readonly" <% } %> /></td> 		
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="sexo" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>sexo"  id="<portlet:namespace/>sexo" <%if(esView){ %> disabled="disabled" <% } %>>
					<option
						<%= detOpcionSS != null && detOpcionSS.getSexo().trim().toUpperCase().equals("M") ? "selected" : ""  %>
						value="m">Masculino</option>
					<option
						<%= detOpcionSS != null && detOpcionSS.getSexo().trim().toUpperCase().equals("F") ? "selected" : ""  %>
						value="f">Femenino</option>
			</select></td>
			<td><label><liferay-ui:message key="cuil-titular" />:</label></td>
			<td><input id="<portlet:namespace />cuil_titular" name="<portlet:namespace />cuil_titular" 
				       type="text"  size="13" maxlength="11"
				       onblur='javascript:<portlet:namespace />validarCuilcv("cuil_titular");'
				       value="<%= detOpcionSS != null ? detOpcionSS.getCuil() : "" %>" 
				       <%if(esView){ %> readonly="readonly" <% } %> /></td> 
			<td><label><liferay-ui:message key="regimen" />:</label></td>
			<td><select name="<portlet:namespace />regimen" id="<portlet:namespace />regimen" 
						onchange="javascript:<portlet:namespace />ocultarMonotrib();" 
						<%if(esView){ %> disabled="disabled" <% } %> >
			<!-- onchange='javascript:<portlet:namespace />mostrarCargaUnificaConyuge();' -->
				<%for(int i = 0; i < WebKeysAfiliados.OPCION_REGIMENES.length; i++ ) {%>
				<option value="<%=WebKeysAfiliados.OPCION_REGIMENES[i][0] %>" 
					<%=(detOpcionSS!=null 
					&& detOpcionSS.getRegimen().equals(WebKeysAfiliados.OPCION_REGIMENES[i][0]))? "selected":""%> > <%=WebKeysAfiliados.OPCION_REGIMENES[i][1] %> </option>
				<% } %>
		</select></td>
			<td colspan="2">
			<div id="<portlet:namespace />divCuitEmpleador" name="<portlet:namespace />divCuitEmpleador" >
				<table>
				<tr><td>
				<label><liferay-ui:message key="cuit" /> Empleador:</label></td>
				<td>&nbsp;&nbsp;&nbsp;</td>
				<td>
				<input id="<portlet:namespace />cuit_empleador"
					name="<portlet:namespace />cuit_empleador" 
					size="10" maxlength="11" type="text" 
					onblur="javascript:<portlet:namespace />validaCuit(this);"
					value="<%=detOpcionSS!=null ? detOpcionSS.getCuit():"" %>" 
					<%if(esView){ %> readonly="readonly" <% } %> />
				</td>
				</tr>
				</table>	
			</div>	 
			</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>		
		<tr>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td colspan="1"><select id="<portlet:namespace/>provincia" <%if(esView){ %> disabled="disabled" <% } %>
				name="<portlet:namespace/>provincia" 
				onchange="javascript:filtrarLocalidad();" onclick="javascript:filtrarLocalidad();" onblur="javascript:filtrarLocalidad();"
				style="width: 150px;">
					<%	for (Provincia provincia : provincias) { %>
					<option
						<%=detOpcionSS != null && Integer.parseInt(detOpcionSS.getProvincia()) == provincia. getIdSss() ? "selected" : ""%>
						<%= detOpcionSS == null && provincia.getIdSss() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
						value="<%= provincia.getIdSss() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>
			</select></td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td colspan="1"><select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <%if(esView){ %> disabled="disabled" <% } %> style="width: 250px;">
					<%if(detOpcionSS!=null){ %>
						<option selected value="<%=locId%>"><%=locDesc%></option>
					<%}else{ %>
						<option selected value="0">Seleccione una localidad</option> 
					<%} %>
					<%-- <%	for (Localidad localidad : localidades) {	%>
					<option
						<%=detOpcionSS != null && Integer.parseInt(detOpcionSS.getLocalidad()) == localidad.getId_localidadesss() ? "selected" : ""%>
						value="<%= localidad.getId_localidadesss() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%> --%>
			</select></td>
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_postal"
				name="<portlet:namespace />cod_postal" size="5" maxlength="4" type="text" onblur="javascript:filtrarLocalidadxCodPostal();"
				value="<%=detOpcionSS!=null ? detOpcionSS.getCod_postal():"" %>" onkeydown="allowOnlyDigits(event);"
				<%if(esView){ %> readonly="readonly" <% } %> /></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="calle" />:</label></td>
			<td colspan="1" style="vertical-align: top" value="<%= detOpcionSS != null ? detOpcionSS.getCalle() : "" %>" ><jsp:include
					page='/html/portlet/afiliados/busqueda_calle_opc.jsp' /></td>
			<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />numero"
				name="<portlet:namespace />numero" size="5" maxlength="5" type="text"
				value="<%= detOpcionSS != null ? detOpcionSS.getNumero() : "" %>" 
				<%if(esView){ %> readonly="readonly" <% } %> onkeydown="allowOnlyDigits(event);" />
			</td>
			<%-- <div id='<portlet:namespace />divCodPostal' style="float: right;"></div> --%>
			<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />piso"
				name="<portlet:namespace />piso" size="5" maxlength="4" type="text"
				value="<%= detOpcionSS != null ? detOpcionSS.getPiso() : "" %>" 
				<%if(esView){ %> readonly="readonly" <% } %> onkeydown="allowOnlyDigits(event);" /></td>
			<td colspan="1"><label><liferay-ui:message key="departamento" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />dpto"
				name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
				value="<%= detOpcionSS != null ? detOpcionSS.getDepartamento() : "" %>" 
				<%if(esView){ %> readonly="readonly" <% } %> /></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_telefono"
				name="<portlet:namespace />cod_area_telefono" size="5" maxlength="5" onkeydown="allowOnlyDigits(event);"
				type="text" value="<%= detOpcionSS != null ? detOpcionSS.getCodAreaTelParticular() : "" %>" 
				<%if(esView){ %> readonly="readonly" <% } %> /></td>	
			<td colspan="1"><label><liferay-ui:message key="telefono" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />telefono" onkeydown="allowOnlyDigitsConSuprimir(event);"
				name="<portlet:namespace />telefono" size="15" maxlength="15" type="text" 
				value="<%= detOpcionSS != null ? detOpcionSS.getTelParticular() : "" %>"
				<%if(esView){ %> readonly="readonly" <% } %> /></td>
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_tel_laboral" onkeydown="allowOnlyDigits(event);"
				name="<portlet:namespace />cod_area_tel_laboral" size="5" maxlength="5"
				type="text" value="<%= detOpcionSS != null ? detOpcionSS.getCodAreaTelLaboral() : "" %>" 
				<%if(esView){ %> readonly="readonly" <% } %> /></td>		
			<td colspan="1"><label><liferay-ui:message key="telefono" /> Laboral:</label></td>
			<td colspan="1"><input id="<portlet:namespace />telefonoLab" onkeydown="allowOnlyDigitsConSuprimir(event);"
				name="<portlet:namespace />telefonoLab" size="15" maxlength="15" type="text" 
				value="<%= detOpcionSS != null ? detOpcionSS.getTelLaboral() : "" %>" 
				<%if(esView){ %> readonly="readonly" <% } %> /></td>	
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>	
			<td colspan="1"><label><liferay-ui:message key="cod-area" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_area_celular" onkeydown="allowOnlyDigits(event);"
				name="<portlet:namespace />cod_area_celular" size="5" maxlength="5"
				type="text" value="<%= detOpcionSS != null ? detOpcionSS.getCodAreaCelular() : "" %>" 
				<%if(esView){ %> readonly="readonly" <% } %> /></td>
			<td colspan="1"><label><liferay-ui:message key="celular" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />celular" onkeydown="allowOnlyDigitsConSuprimir(event);"
				name="<portlet:namespace />celular" size="15" maxlength="15" type="text" 
				value="<%= detOpcionSS != null ? detOpcionSS.getTelCelular() : "" %>" 
				<%if(esView){ %> readonly="readonly" <% } %> /></td>
			<td colspan="1"><label><liferay-ui:message key="email-short" />:</label></td>
			<td colspan="3"><input id="<portlet:namespace />email"
				name="<portlet:namespace />email" size="50" maxlength="50" type="text" onblur="javascript:<portlet:namespace />validarEmail();"
				value="<%= detOpcionSS != null ? detOpcionSS.getEmail() : "" %>"
				<%if(esView){ %> readonly="readonly" <% } %> /></td>
			<!-- <td colspan="1">&nbsp;</td>	 -->	
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label>Fecha Certificación:</label></td>
			<td colspan="1">
				<liferay-ui:input-date dayParam="fechaCertiDia"
					dayValue="<%= certificaFecha.get(Calendar.DATE) %>"
					monthParam="fechaCertiMes"
					monthValue="<%= certificaFecha.get(Calendar.MONTH) %>"
					yearParam="fechaCertiAnio"
					yearValue="<%= certificaFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= certificaFecha.get(Calendar.YEAR) - 20 %>"
					yearRangeEnd="<%= certificaFecha.get(Calendar.YEAR) + 20 %>"
					firstDayOfWeek="<%= certificaFecha.getFirstDayOfWeek()%>"
					disabled="<%=esView%>" />
			</td>
			<td><label>Fecha Elección:</label></td>
			<td colspan="1">
				<liferay-ui:input-date dayParam="fechaEleccionDia"
					dayValue="<%= eleccionFecha.get(Calendar.DATE) %>"
					monthParam="fechaEleccionMes"
					monthValue="<%= eleccionFecha.get(Calendar.MONTH) %>"
					yearParam="fechaEleccionAnio"
					yearValue="<%= eleccionFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= eleccionFecha.get(Calendar.YEAR) - 20 %>"
					yearRangeEnd="<%= eleccionFecha.get(Calendar.YEAR) + 20 %>"
					firstDayOfWeek="<%= eleccionFecha.getFirstDayOfWeek()%>"
					disabled="<%=esView%>" />
			</td>
			<td><label>Fecha Exportación:</label></td>
			<td colspan="1">
				<input id="<portlet:namespace />fechaExportacion" name="<portlet:namespace />fechaExportacion" size="13" type="text" value="<%=detOpcionSS!=null&&detOpcionSS.getFechaExportacion()!=null?detOpcionSS.getFechaExportacion():"" %>" readonly="readonly" />
			</td>
			<td><label>Fecha Entrega:</label></td>
			<td colspan="1">
				<input id="<portlet:namespace />fechaEntrega" name="<portlet:namespace />fechaEntrega" size="13" type="text" value="<%=detOpcionSS!=null&&detOpcionSS.getFechaExportacion()!=null?detOpcionSS.getFechaEntrega():"" %>" readonly="readonly" />
			</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="4">
				<div id="<portlet:namespace />divOSanterior" name="<portlet:namespace />divOSanterior" >
					<table class="lfr-table">
					<tr>	
						<td colspan="1"><label><liferay-ui:message key="os-ant" />:</label></td>
						<td colspan="4">
							<% if(esViewStr != null && esViewStr.equalsIgnoreCase("true")){
									esEdicion = "false";
							   }else{
								    esEdicion = "true";
							   }
							%>
							<liferay-util:include page="/html/portlet/utils/obrassociales/busqueda_obrasocial.jsp">
								<liferay-util:param name="search_url" value="/afiliados/buscar_obrasocial" />
								<liferay-util:param name="id_obrasocial" value="<%= detOpcionSS != null && detOpcionSS.getOsAnterior() != 0 ? String.valueOf(detOpcionSS.getOsAnterior()) : \"\"%>" />
								<liferay-util:param name="esEdicion" value="<%=esEdicion%>" />
							</liferay-util:include>
						</td>
					</tr>
					</table>
				</div>
			</td>
			<td><label>Proyecto:</label></td>
			<td><select name="<portlet:namespace />proyecto" id="<portlet:namespace />proyecto" 
				<%if(esView){ %> disabled="disabled" <% } %>> 
					<option value="" <%if(detOpcionSS != null&&detOpcionSS.getProyecto()==null){ %>selected="selected" <%} %>  ></option>
					<option value="VOLVER2016" <%if(detOpcionSS != null&&detOpcionSS.getProyecto()!=null
								&&detOpcionSS.getProyecto().equalsIgnoreCase("VOLVER2016")){ %>selected="selected" <%} %> >VOLVER 2016</option> 
					<option value="INFOXAFIP" <%if(detOpcionSS != null&&detOpcionSS.getProyecto()!=null
								&&detOpcionSS.getProyecto().equalsIgnoreCase("INFOXAFIP")){ %>selected="selected" <%} %> >Informados por AFIP</option>
					<option value="VIGENEXCEP" <%if(detOpcionSS != null&&detOpcionSS.getProyecto()!=null
								&&detOpcionSS.getProyecto().equalsIgnoreCase("VIGENEXCEP")){ %>selected="selected" <%} %> >VIGENTE POR EXCEPCION</option>
					<option value="INFOXSSS" <%if(detOpcionSS != null&& detOpcionSS.getProyecto()!=null
							&&detOpcionSS.getProyecto().equalsIgnoreCase("INFOXSSS")){ %>selected="selected" <%} %> >Informado por la SSS</option>
					<option value="MEJORPERT" <%if(detOpcionSS != null&& detOpcionSS.getProyecto()!=null
							&& detOpcionSS.getProyecto().equalsIgnoreCase("MEJORPERT")){ %>selected="selected" <%} %> >MEJOR PERTENECER</option>											
								
				</select>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>		
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr><td>
		<div id='<portlet:namespace />divConyugeUnifica'>
			<table class="lfr-table">
			<tr>
				<td><label>Unifica aportes:</label></td>
				<td>
						<select id="<portlet:namespace/>unifica_aportes" name="<portlet:namespace/>unifica_aportes">
							<option <%= detOpcionSS != null && detOpcionSS.getUnificaApo().trim().toUpperCase().equals("NO") ? "selected" : ""  %>
								value="NO">NO</option>
							<option <%= detOpcionSS != null && detOpcionSS.getUnificaApo().trim().toUpperCase().equals("SI") ? "selected" : ""  %>
								value="SI">SI</option>
						</select>
				</td>
				<td><label>Apellido y Nombre C&oacute;nyuge:</label></td>
				<td><input id="<portlet:namespace />apeyNomConyuge" name="<portlet:namespace />apeyNomConyuge" size="30" type="text" maxlength="30"
							value="<%=detOpcionSS!=null?detOpcionSS.getApeNomConyuge():"" %>" />
				</td>			
				<td><label>Cuil C&oacute;nyuge:</label></td>
				<td><input id="<portlet:namespace />cuilConyuge" name="<portlet:namespace />cuilConyuge" 
						   size="13" type="text" maxlength="11"
						   onblur="javascript:<portlet:namespace />validaCuit(this);"
						   value="<%=detOpcionSS!=null ? detOpcionSS.getCuilConyuge():"" %>" />
				</td>
			</tr>
			</table>
		</div>	
		</td></tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
	</table>
</fieldset>
<%-- <br />
<table>
	<tr>
		<td>
			<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveAfiliadoEntry();" />
		</td>
	</tr>
</table> --%>
		
<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">		
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />id_delegacion);				
	</script>
</c:if>

<script type="text/javascript">
jQuery('#<portlet:namespace />divConyugeUnifica').hide();

<%-- <% if(detOpcionSS!=null && detOpcionSS.getUnificaApo().equalsIgnoreCase("SI")){ %>
	jQuery('#<portlet:namespace />divConyugeUnifica').show();
<% } %>  --%>


jQuery('#<portlet:namespace />sexo').change(function(){validarPrefijoCuilPorSexo();});	
jQuery('#<portlet:namespace />cuil_titular').change(function(){validarPrefijoCuilPorSexo();});

jQuery(window).load(function () {
	<%if(detOpcionSS != null) {%>
		validarPrefijoCuilPorSexo();
	<%}%>
});


function validarPrefijoCuilPorSexo(){
	
	var cuil = jQuery('#<portlet:namespace/>cuil_titular').val();
    var sexo = jQuery('#<portlet:namespace/>sexo').val();
    if (cuil.length == 11){
		 var prefijo_cuil = cuil.substring(0, 2);
		 
		 //valido prefijo masculino
		 if (sexo == 'm' && prefijo_cuil != '20'){
			 alert('El cuil ingresado puede ser que no corresponda con el sexo masculino');
		 }else if(sexo == 'f' && prefijo_cuil != '27'){
			 alert('El cuil ingresado puede ser que no corresponda con el sexo femenino');
		 }
	 }
}



function <portlet:namespace />mostrarCargaUnificaConyuge() {
	var esMonotrib = jQuery('#<portlet:namespace/>regimen').val();
	
	if(esMonotrib=='MT'){
		jQuery('#<portlet:namespace />divConyugeUnifica').show();
		jQuery('#<portlet:namespace />divOSanterior').hide();
		jQuery('#<portlet:namespace />divCuitEmpleador').hide();
	}else{
		jQuery('#<portlet:namespace />divConyugeUnifica').hide();
		jQuery('#<portlet:namespace />divCuitEmpleador').show();
		jQuery('#<portlet:namespace />divOSanterior').show();
	}
}
function <portlet:namespace />ocultarMonotrib() {
	var esMonotrib = jQuery('#<portlet:namespace/>regimen').val();
	
	if(esMonotrib=='MT' || esMonotrib=='ESD'){
		jQuery('#<portlet:namespace />divOSanterior').hide();
		jQuery('#<portlet:namespace />divCuitEmpleador').hide();
	}else{
		jQuery('#<portlet:namespace />divCuitEmpleador').show();
		jQuery('#<portlet:namespace />divOSanterior').show();
	}
}

function filtrarLocalidad() {
	var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_provincia_localidad_sss&idProvincia='+idProvincia;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace/>localidad").length = 0;						
			var obj = jQuery.parseJSON(data);
			addElementToSelect("<portlet:namespace/>localidad", "Seleccione una localidad", 0);
			for(var i =0;i< obj.listaFiltrada.length; i++){					
				var value = obj.listaFiltrada[i].split('|')[0];
				var text = obj.listaFiltrada[i].split('|')[1];
				addElementToSelect("<portlet:namespace/>localidad", text, value);					
			}				                                                                                                                                                                                                                                                            
		}
	});
}

function addElementToSelect(id_combo, texto, valor) {
	var combo = document.getElementById(id_combo);
	var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
	combo.options[idxElemento] = new Option();
	combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
	combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
}

function <portlet:namespace />validarCuilcv(cuil){
	if(cuil=="cuil"){
		var cuil_final = jQuery('#<portlet:namespace/>cuil').val();
	}else if(cuil=="cuil_titular"){
		var cuil_final = jQuery('#<portlet:namespace/>cuil_titular').val();
	}		
	<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_cuil&cuil='+cuil_final+'&inte=0'; --%>

	var xportletUrl = '/afiliados/validar_cuil';
	
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
	'<liferay-portlet:param name="cuil" value="__cuil"/>'+
	'<liferay-portlet:param name="inte" value="0"/>'+
    '</liferay-portlet:renderURL>';

    url = url.replace("__xportletUrl",xportletUrl); 
    url = url.replace("__cuil",cuil_final);
	
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			if(obj.validado=="1"){
				alert("<liferay-ui:message key='cuil-invalido'/>");
			}else if(obj.validado=="2"){
				alert("<liferay-ui:message key='cuil-titular-existente'/>");
			} 					
		}				                                                                                                                                                                                                                                                            
		
	});
}

function <portlet:namespace />validaCuit(cuit){
	var cuil_final = cuit.value ;
	<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_cuil&cuil='+cuil_final+'&inte=0'; --%>
	
	var xportletUrl = '/afiliados/validar_cuil';
	
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
	'<liferay-portlet:param name="cuil" value="__cuil"/>'+
	'<liferay-portlet:param name="inte" value="0"/>'+
    '</liferay-portlet:renderURL>';

    url = url.replace("__xportletUrl",xportletUrl); 
    url = url.replace("__cuil",cuil_final);
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			if(obj.validado=="1"){
				alert("<liferay-ui:message key='cuil-invalido'/>");
				return false;
			}else if(obj.validado=="2"){
				alert("<liferay-ui:message key='cuil-titular-existente'/>");
			}else{
				return true;
			} 					
		}				                                                                                                                                                                                                                                                            
		
	});
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

</script>