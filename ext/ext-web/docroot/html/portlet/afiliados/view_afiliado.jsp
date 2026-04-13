<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ page import="ar.com.ospim.afiliados.services.TelefonoServiceUtil" %>
<%@ page import="ar.com.ospim.global.beans.Telefono" %>

<%
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);	
Afiliado titular = null;
if (null!=afiliado && afiliado.esTitular()) {
	titular = afiliado;
} else {
	titular = ActionUtil.getAfiliadoInclusoDadoBajaByCuilInte(afiliado.getCuil_titular(), 0);
}

Calendar vigenteFecha = CalendarFactoryUtil.getCalendar();
Date vigenteFechaAfil = afiliado != null ? afiliado.getVigen_fecha() : null; 
if (vigenteFechaAfil == null) {
	vigenteFecha.setTime(new Date());
} else{
	vigenteFecha.setTime(afiliado.getVigen_fecha());
}

Calendar fechaNacimiento = CalendarFactoryUtil.getCalendar();
Date fechaNacimientoAfil = afiliado != null ? afiliado.getNaci_fecha() : null; 
if (fechaNacimientoAfil == null) {
	fechaNacimiento.setTime(new Date());
} else {
	fechaNacimiento.setTime(afiliado.getNaci_fecha());
}

String obsGrpFliar = null;

/* if(afiliado != null) {
	obsGrpFliar = EditarAfiliadoServiceUtil.getObservacionesGrupoFliar(afiliado.getCuil_titular(), afiliado.getInte());
} */

/* Revisar esto de pegar las seccionales */
List<Seccional> seccionales = (ArrayList<Seccional>) portletSession
.getAttribute(WebKeysAfiliados.SECCIONALES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (seccionales == null) {
	seccionales = TraeListasServiceUtil.getSeccionales();
	portletSession.setAttribute(WebKeysAfiliados.SECCIONALES_EN_SESSION,
	seccionales,PortletSession.APPLICATION_SCOPE);
}

Date fecha =  null;
String fechaRecepcion =  null;

List<Telefono> tels = new ArrayList<Telefono>();
Telefono telCel = null;
Telefono telFijo = null;

if (afiliado != null && afiliado.getCuil_titular() != null && !"".equals(afiliado.getCuil_titular())) {
    tels = TelefonoServiceUtil.getTelefonos(afiliado.getCuil_titular(), afiliado.getInte());

    for (Telefono t : tels) {
        if ("C".equalsIgnoreCase(t.getTipo())) {
            telCel = t;
        } else if ("F".equalsIgnoreCase(t.getTipo())) {
            telFijo = t;
        }
    }
}
%>
<%-- <fieldset class="block-labels">
	<legend><liferay-ui:message key="observaciones" /></legend>
	</br>
	<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="7"><textarea cols="100" <% if (afiliado != null) { %> <%="readonly='readonly'" %> <%}%>
				name="<portlet:namespace/>obs" id="<portlet:namespace/>obs"><%= afiliado!=null && afiliado.getObservaciones() != null  ? afiliado.getObservaciones() : new String("") %></textarea></td>
	</tr>
	<%if(obsGrpFliar != null){%>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td><label><liferay-ui:message key="observaciones"/> grupo familiar:</label></td>
		<td colspan="7"><textarea cols="100" readonly="readonly"
				name="<portlet:namespace/>obsGrpFliar" id="<portlet:namespace/>obsGrpFliar"><%= afiliado!=null && obsGrpFliar != null  ? obsGrpFliar : new String("") %></textarea></td>
	</tr>
	<%}%>
	</table>
</fieldset> --%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key="observaciones-interna" /></legend> 

		<div align="center" id="<portlet:namespace />observaciones_internas" style="height:120px; overflow: scroll; overflow-x: hidden;">
					<liferay-util:include page="/html/portlet/afiliados/afi_observaciones_search_result.jsp">
					</liferay-util:include>
			</div>	
</fieldset> 
</br>

<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos-afiliado" /></legend>
<table class="lfr-table">
	<%if (afiliado != null && afiliado.getBaja_fecha() != null){ %>
	<tr>
		<td>
			<label><liferay-ui:message key="baja-fecha" />:</label>
		</td>
		<td>
			<%= afiliado.getBaja_fechaAsString() %>	
		</td>
		<td>
			<label><liferay-ui:message key="motivo-baja" />:</label>		
		</td>
		<td>
				<%		if (afiliado.getId_motivo_baja() != -1){
							MotivoBaja m = new MotivoBaja(afiliado.getId_motivo_baja(), "");
							int indexOf  = motivos.indexOf(m);
							if (indexOf!= -1){
							%>
								<%=motivos.get(indexOf).getDescripcion()%>
						<%	}
						}
				%>
		</td>
	</tr>
	<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
	<%} %>
	<tr>
		<td><label><liferay-ui:message key="cuil-titular" />:</label></td>
		<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuil_titular"
			name="<portlet:namespace />cuil_titular" size="13" maxlength="13"
			type="text"
			value="<%= afiliado != null ? afiliado.getCuil_titular() : "" %>"
			<% if (afiliado != null) { %> <%="readonly='readonly'" %> <%}%> /></td>
		<td>&nbsp;</td>
		<td><label><liferay-ui:message key="integrante" />:</label></td>
		<td colspan="2"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />inte"
			name="<portlet:namespace />inte" size="2" maxlength="2" type="text"
			value="<%= afiliado != null ? afiliado.getInteAsString() : "" %>"
			<% if (afiliado != null) { %> <%="readonly='readonly'" %> <%}%> /></td>
		</td>
		<td><label><liferay-ui:message key="vigente-desde" />:</label></td>
			<td colspan="1"><!--<input id="<portlet:namespace />fecha_nacimiento" name="<portlet:namespace />fecha_nacimiento" size="13" type="text" value="" />-->
			<liferay-ui:input-date
				dayParam="vigenteFechaDia"
				dayValue="<%= vigenteFecha.get(Calendar.DATE) %>" 
				monthParam="vigenteFechaMes"
				monthValue="<%= vigenteFecha.get(Calendar.MONTH) %>"				
				yearParam="vigenteFechaAnio"
				yearValue="<%= vigenteFecha.get(Calendar.YEAR) %>"
				yearRangeStart="<%= vigenteFecha.get(Calendar.YEAR) - 120 %>"
				yearRangeEnd="<%= vigenteFecha.get(Calendar.YEAR) + 120 %>"
				firstDayOfWeek="<%= vigenteFecha.getFirstDayOfWeek() - 1 %>"
				disabled="<%= true %>" />
			</td>
			<td><label>N° Correspondencia:</label></td>
			<td><input id="<portlet:namespace />numero_correspondencia"
			name="<portlet:namespace />numero_correspondencia" size="10" maxlength="10"
			type="text" onkeydown="allowOnlyDigits(event);" disabled="disabled" readonly="readonly"
			value="<%= afiliado != null ? afiliado.getIdCorrespondencia() : "" %>" /></td>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td colspan="2"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />apellido"
				name="<portlet:namespace />apellido" size="20" maxlength="100"
				type="text"
				value="<%= afiliado != null ? afiliado.getApellido() : "" %>" /></td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td colspan="2"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />nombre"
				name="<portlet:namespace />nombre" maxlength="100" type="text"
				value="<%= afiliado != null ? afiliado.getNombre() : "" %>" /></td>
			<td><label><liferay-ui:message key="sexo" />:</label></td>
			<td colspan="2"><select disabled="disabled" name="<portlet:namespace/>sexo">
				<option
					<%= afiliado != null && afiliado.getSexo().trim().toUpperCase().equals("M") ? "selected" : ""  %>
					value="m">Masculino</option>
				<option
					<%= afiliado != null && afiliado.getSexo().trim().toUpperCase().equals("F") ? "selected" : ""  %>
					value="f">Femenino</option>
			</select></td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
			<td colspan="2"><select disabled="disabled"
				name="<portlet:namespace/>documento_tipo">
				<%
										for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
									%>
				<option
					<%= afiliado != null && afiliado.getDocumento_tipo().equals(tipoDoc) ? "selected" : ""  %>
					value="<%= tipoDoc %>"><%=tipoDoc%></option>
				<%
									}
									%>
			</select></td>
			<td><label><liferay-ui:message key="nro-documento" />:</label></td>
			<td colspan="2"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />nroDoc"
				name="<portlet:namespace />nroDoc" size="9" maxlength="13"
				type="text"
				value="<%= afiliado != null ? afiliado.getDocu_numero() : "" %>" />
			</td>
			<td colspan="1"><label><liferay-ui:message key="cuil" />:</label></td>
			<td><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cuil"
				name="<portlet:namespace />cuil" size="13" maxlength="13"
				type="text"
				value="<%= afiliado != null ? afiliado.getCuil() : "" %>" /></td>

		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="fecha-nacimiento" />:</label></td>
			<td colspan="5"><!--<input disabled="disabled" id="<portlet:namespace />fecha_nacimiento" name="<portlet:namespace />fecha_nacimiento" size="13" type="text" value="" />-->
			<liferay-ui:input-date
				dayParam="fechaNacimientoDia"
				dayValue="<%= fechaNacimiento.get(Calendar.DATE) %>" 
				monthParam="fechaNacimientoMes"
				monthValue="<%= fechaNacimiento.get(Calendar.MONTH) %>"				
				yearParam="fechaNacimientoAnio"
				yearValue="<%= fechaNacimiento.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaNacimiento.get(Calendar.YEAR) - 120 %>"
				yearRangeEnd="<%= fechaNacimiento.get(Calendar.YEAR) + 20 %>"
				firstDayOfWeek="<%= fechaNacimiento.getFirstDayOfWeek() - 1 %>"
				disabled="<%= true %>" /></td>
			<td><label><liferay-ui:message key="estado-civil" />:</label></td>
			<td colspan="2"><select disabled="disabled" name="<portlet:namespace/>estado_civil">
				<!-- <option value="DESCONOCIDO"/>DESCONOCIDO</option> -->
				<%
					for (EstadoCivil estadoCivil : estados_civil) {
				%>
				<option
					<%= afiliado != null && afiliado.getId_civil_esta()== estadoCivil.getCodigo() ? "selected" : ""  %>
					value="<%= estadoCivil.getCodigo() %>"><%=estadoCivil.getDescripcion()%></option>
				<%
					}
				%>

			</select></td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="nacionalidad" />:</label></td>
			<td colspan="2"><select disabled="disabled" name="<portlet:namespace/>nacionalidad">
				<%
										for (Nacionalidad nacion : nacionalidades) {
									%>
				<option
					<%= afiliado != null && afiliado.getNacionalidad() == nacion.getId() ? "selected" : ""  %>
					<%= afiliado == null && nacion.getId() == WebKeysAfiliados.ID_DEFAULT_NACIONALIDAD ? "selected" : ""  %>
					value="<%= nacion.getId() %>"><%=nacion.getDescripcion()%></option>
				<%
									}
									%>
			</select></td>
			<td colspan="3">&nbsp;</td>
			
			<td><label><liferay-ui:message key="parentesco" />:</label></td>
			<td colspan="2"><select disabled="disabled" name="<portlet:namespace/>parentesco">
				<%
					for (Parentesco parentesco : parentescos) {
				%>
				<option
					<%= afiliado != null && afiliado.getId_parentesco()==parentesco.getCodigo() ? "selected" : ""  %>
					value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%></option>
				<%
				  }
				%>
			</select></td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>

		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td colspan="5"><select disabled="disabled" name="<portlet:namespace/>seccional">
				<%
										for (Seccional seccional : seccionales) {
									%>
				<option
					<%= afiliado != null && afiliado.getSeccional().getId() == seccional.getId() ? "selected" : ""  %>
					value="<%= seccional.getId() %>"><%=seccional.getDescripcion()%></option>
				<%
									}
									%>
			</select></td>
			<td><label><liferay-ui:message key="obra-social-ant" />:</label></td>
			<td colspan="4"><select disabled="disabled"
				name="<portlet:namespace/>obra_social_ant">
				<option value=""/>DESCONOCIDA</option>
				<%
										for (ObraSocialCampo os : obrasSocialesAnteriores) {
									%>
				<option
					<%= afiliado != null && afiliado.getAnterior_os() == os.getId() ? "selected" : ""  %>
					value="<%= os.getId() %>"><%=os.getDescripcionShort()%></option>
				<%
									}
									%>
			</select></td>
		</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>		
	<tr>
		<td colspan="11" >
			<div id="<portlet:namespace/>opcionsss">
				<p><label><liferay-ui:message key="Formulario" />:</label>&nbsp;&nbsp;
				<%=afiliado!=null&&afiliado.getDetalleOpcionSs()!=null?afiliado.getDetalleOpcionSs().getNroFormulario():"" %>&nbsp;&nbsp;&nbsp;&nbsp;
				<label><liferay-ui:message key="Tomo" />:</label>&nbsp;&nbsp;
				<%=afiliado!=null&&afiliado.getDetalleOpcionSs()!=null?afiliado.getDetalleOpcionSs().getTomo():"" %>&nbsp;&nbsp;&nbsp;&nbsp;
				<label><liferay-ui:message key="Libro" />:</label>&nbsp;&nbsp;
				<%=afiliado!=null&&afiliado.getDetalleOpcionSs()!=null? afiliado.getDetalleOpcionSs().getLibro():""  %>&nbsp;&nbsp;&nbsp;&nbsp;
				<label><liferay-ui:message key="delegacion" />:</label>&nbsp;&nbsp;
				<%=afiliado!=null&&afiliado.getDetalleOpcionSs()!=null? afiliado.getDetalleOpcionSs().getDelegacion()  :""  %>&nbsp;&nbsp;&nbsp;&nbsp;
				<label><liferay-ui:message key="fecha-eleccion" />:</label>&nbsp;&nbsp;
				<%=afiliado!=null&&afiliado.getDetalleOpcionSs()!=null&&afiliado.getDetalleOpcionSs().getFechaElecc()!=null? afiliado.getDetalleOpcionSs().getFechaElecc()  :""  %>&nbsp;&nbsp;&nbsp;&nbsp;
				<label><liferay-ui:message key="fecha-certif" />:</label>&nbsp;&nbsp;&nbsp;&nbsp;
				<%=afiliado!=null&&afiliado.getDetalleOpcionSs()!=null&&afiliado.getDetalleOpcionSs().getFechaCerti()!=null? afiliado.getDetalleOpcionSs().getFechaCerti()   :""  %>&nbsp;&nbsp;&nbsp;&nbsp;
				<label><liferay-ui:message key="regimen" />:</label>&nbsp;&nbsp;
				<%=afiliado!=null&&afiliado.getDetalleOpcionSs()!=null&&afiliado.getDetalleOpcionSs().getRegimen()!=null? afiliado.getDetalleOpcionSs().getRegimen() :""  %>&nbsp;&nbsp;&nbsp;&nbsp;
				</p>				
			</div>	
		</td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>	
	</table>
	
	<table class="lfr-table">
	<tr>
			<td><label><liferay-ui:message key="discapacitado" />:</label></td>
			<td colspan="1">
				<select disabled="disabled" name="<portlet:namespace/>discapacitado">
					<option value="0" <%= afiliado != null && null!= afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("0") ? "selected" : ""  %>>No</option>
					<option value="1" <%= afiliado != null && null!= afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("1") ? "selected" : ""  %>>Si</option>
				</select>
			</td>
			<td colspan="1"> 
				<% if(afiliado!=null && null!=afiliado.getDiscapacitado() ){ 
					if(afiliado.getDiscapacitado().equals("1") || afiliado.getFechaVtoDocDiscap() != null ){
					%>	
				<label><liferay-ui:message key="fecha-vto-docdisc" />:</label>
				<%}} %>
			</td>
			<td colspan="1">
				<% if(afiliado.getFechaVtoDocDiscap() == null) { 
					if(afiliado.getDiscapacitado().equals("1")  ){  %>
					No tiene documentación.
				<%} } else { 
					Date hoy = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					if (afiliado.getFechaVtoDocDiscap().after(hoy)  ){ %>
					<font color="green"> <%=  sdf.format(afiliado.getFechaVtoDocDiscap()) %> </font>	
				<% } else { %>
					<font color="red"> <%=  sdf.format(afiliado.getFechaVtoDocDiscap()) %> </font>
				<%}}%>
						
			</td>
			<!-- <td colspan="2">&nbsp;</td> -->
			<td><label><liferay-ui:message key="censo" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>censo2013" disabled="disabled">
					<option value="0"
						<%= afiliado != null && afiliado.getCenso2013()==0 ? "selected" : ""  %>>No</option>
					<option value="1"
						<%= afiliado != null && afiliado.getCenso2013()==1 ? "selected" : ""  %>>Si</option>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="email-short" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />email"
				name="<portlet:namespace />email" size="50" maxlength="50"  disabled="disabled"
				type="text" value="<%= afiliado != null && afiliado.getEmail()!=null ? afiliado.getEmail() : "" %>"</td>	
			<td><label><liferay-ui:message key="tieneAntJud" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>tiene_antecedentes_judiciales" disabled="disabled">
					<option value="0"
						<%= afiliado != null && afiliado.getTieneAntecedentesJudiciales()==0 ? "selected" : ""  %>>No</option>
					<option value="1"
						<%= afiliado != null && afiliado.getTieneAntecedentesJudiciales()==1 ? "selected" : ""  %>>Si</option>
			</select>
			</td>	
		</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>		
	<tr>
		<td><label><liferay-ui:message key="cliente_preferencial" />:</label></td>
		<td colspan="1"><select name="<portlet:namespace/>cliente_preferencial" disabled="disabled">
					<option value="0"
						<%= afiliado != null && afiliado.getClientePreferencial()==0 ? "selected" : ""  %>>No</option>
					<option value="1"
						<%= afiliado != null && afiliado.getClientePreferencial()==1 ? "selected" : ""  %>>Si</option>
		</select></td>
		<td colspan="2">&nbsp;</td>
		<td><label>Proyecto:</label></td>
		<td><select name="<portlet:namespace />proyecto" id="<portlet:namespace />proyecto" disabled="disabled"> 
				<option value="" <%if(afiliado != null&&afiliado.getProyecto()==null){ %>selected="selected" <%} %>  ></option>
				<option value="VOLVER2016" <%if(afiliado != null&&afiliado.getProyecto()!=null
							&&afiliado.getProyecto().equalsIgnoreCase("VOLVER2016")){ %>selected="selected" <%} %> >VOLVER 2016</option> 
				<option value="INFOXAFIP" <%if(afiliado != null&&afiliado.getProyecto()!=null
								&&afiliado.getProyecto().equalsIgnoreCase("INFOXAFIP")){ %>selected="selected" <%} %> >Informados por AFIP</option>
					<option value="VIGENEXCEP" <%if(afiliado != null&&afiliado.getProyecto()!=null
								&&afiliado.getProyecto().equalsIgnoreCase("VIGENEXCEP")){ %>selected="selected" <%} %> >VIGENTE POR EXCEPCION</option>
				<option value="INFOXSSS" <%if(afiliado != null&&afiliado.getProyecto()!=null
							&&afiliado.getProyecto().equalsIgnoreCase("INFOXSSS")){ %>selected="selected" <%} %> >Informado por la SSS</option>	
				<option value="MEJORPERT" <%if(afiliado != null && afiliado.getProyecto()!=null
							&&afiliado.getProyecto().equalsIgnoreCase("MEJORPERT")){ %>selected="selected" <%} %> >MEJOR PERTENECER</option>									
			</select>
		</td>
		<td>
			<%if (afiliado != null && afiliado.getIncidentes() != null) {
				fecha = afiliado.getIncidentes().iterator().next().getFechaRecepcion(); 
				SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");		
				fechaRecepcion = sdf.format(fecha);
			%>
				<span style="font-size: 9pt; color: green;  " id="<portlet:namespace />incidente"><label><b>Caso U.O. fecha:  <%=fechaRecepcion %>  </b></label></span>
			<%} %>
		</td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
</table>
<div id='<portlet:namespace/>fechasSSS'>
	<table>
		<tr>
			<td colspan="8">
				<div id='<portlet:namespace/>fechasSSS'>
					<p><b>Fechas Superintendencia de Salud:</b>&nbsp;&nbsp;&nbsp;
					<liferay-ui:message key="fecha-presentacion" />:&nbsp;
					<b><%=afiliado!=null&&afiliado.getDetalleFechasSuperintendencia()!=null&&afiliado.getDetalleFechasSuperintendencia().getFechaPresentacionSuper() !=null? afiliado.getDetalleFechasSuperintendencia().getFechaPresentacionSuper()   :""  %></b>&nbsp;&nbsp;
					<liferay-ui:message key="fecha_modif" />:&nbsp;
					<b><%=afiliado!=null&&afiliado.getDetalleFechasSuperintendencia()!=null&&afiliado.getDetalleFechasSuperintendencia().getFechaModiSuper() !=null? afiliado.getDetalleFechasSuperintendencia().getFechaModiSuper()  :""  %></b>&nbsp;&nbsp;
					<liferay-ui:message key="fecha-baja" />:&nbsp;
					<b><%=afiliado!=null&&afiliado.getDetalleFechasSuperintendencia()!=null&&afiliado.getDetalleFechasSuperintendencia().getFechaBajaSuper() !=null? afiliado.getDetalleFechasSuperintendencia().getFechaBajaSuper()   :""  %></b></p>&nbsp;&nbsp;
				</div>	
			</td>
		</tr>
	
	</table>
</div>	
</fieldset>
<br/>
<fieldset class="block-labels"><legend><liferay-ui:message key="home-address-afi" /></legend>
<table class="lfr-table">
	
	<tr>
		<td><label><liferay-ui:message key="provincia" />:</label></td>
		<td colspan="2"><select disabled="disabled" name="<portlet:namespace/>provincia">
			<%
										for (Provincia provincia : provincias) {
									%>
			<option
				<%= afiliado != null && afiliado.getDomicilioDefault().getProvinciaId() == provincia.getId() ? "selected" : ""  %>
				<%= afiliado == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
				value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
			<%
									}
									%>
		</select></td>
		<td><label><liferay-ui:message key="localidad" />:</label></td>
		<td colspan="2"><select disabled="disabled" name="<portlet:namespace/>localidad">
			<%
										for (Localidad localidad : localidades) {
									%>
			<option
				<%= afiliado != null && afiliado.getDomicilioDefault().getLocalidadId() == localidad.getId() ? "selected" : ""  %>
				<%= afiliado == null && localidad.getId() == WebKeysAfiliados.ID_DEFAULT_LOCALIDAD ? "selected" : ""  %>
				value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
			<%
									}
									%>
		</select></td>
		<td><label><liferay-ui:message key="cod-postal" />:</label></td>
		<td colspan="2"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />cod_postal"
			name="<portlet:namespace />cod_postal" size="5" maxlength="4"
			type="text"
			value="<%= afiliado != null ? afiliado.getDomicilioDefault().getPostal_codi() : "" %>" />
		</td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="calle" />:</label></td>
		<td colspan="5"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />calle"
			name="<portlet:namespace />calle" size="50" maxlength="100"
			type="text"
			value="<%= afiliado != null ? afiliado.getDomicilioDefault().getCalle() : "" %>" />
		</td>
		<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
		<td colspan="1"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />numero"
			name="<portlet:namespace />numero" size="5" maxlength="4" type="text"
			value="<%= afiliado != null ? afiliado.getDomicilioDefault().getNumero() : "" %>" />
		</td>
		<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
		<td colspan="1"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />piso"
			name="<portlet:namespace />piso" size="5" maxlength="5" type="text"
			value="<%= afiliado != null ? afiliado.getDomicilioDefault().getPiso() : "" %>" />
		</td>
		<td colspan="1"><label><liferay-ui:message
			key="departamento" />:</label></td>
		<td colspan="1"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />dpto"
			name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
			value="<%= afiliado != null ? afiliado.getDomicilioDefault().getDepto() : "" %>" />
		</td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
		<td colspan="2"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />barrio"
			name="<portlet:namespace />barrio" size="12" maxlength="50"
			type="text"
			value="<%= afiliado != null ? afiliado.getDomicilioDefault().getBarrio() : "" %>" />
		</td>
		
		<%-- <td colspan="1"><label><liferay-ui:message key="telefono" />:</label></td>
		<td colspan="2"><input disabled="disabled" readonly="readonly" id="<portlet:namespace />telefono"
			name="<portlet:namespace />telefono" size="15" maxlength="15"
			type="text"
			value="<%= afiliado != null ? afiliado.getDomicilioDefault().getTelefono() : "" %>" />
		</td> --%>		
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<tr>
    <!-- Teléfono -->
    <td><label><liferay-ui:message key="telefono" />:</label></td>
    <td colspan="2">
        <input id="<portlet:namespace />cod_area_telefono"
            name="<portlet:namespace />cod_area_telefono" size="5" maxlength="5"
            type="text" value="<%= telFijo != null && telFijo.getCodigoArea()!=null ? telFijo.getCodigoArea() : "" %>"
            disabled="disabled" />
        <input id="<portlet:namespace />telefono"
            name="<portlet:namespace />telefono" size="15" maxlength="15"
            type="text" value="<%= telFijo != null && telFijo.getNumero()!=null ? telFijo.getNumero() : "" %>"
            disabled="disabled" />
    </td>

    <!-- Celular -->
    <td><label><liferay-ui:message key="celular" />:</label></td>
    <td colspan="2">
        <input id="<portlet:namespace />cod_area_celular"
            name="<portlet:namespace />cod_area_celular" size="5" maxlength="5"
            type="text" value="<%= telCel != null && telCel.getCodigoArea()!=null ? telCel.getCodigoArea() : "" %>"
            disabled="disabled" />
        <input id="<portlet:namespace />celular"
            name="<portlet:namespace />celular" size="15" maxlength="15"
            type="text" value="<%= telCel != null && telCel.getNumero()!=null ? telCel.getNumero() : "" %>"
            disabled="disabled" />
    </td>
</tr>
	
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="8">
			<a href="javascript:mostrarDomicilioHisto();" id="<portlet:namespace />mostrarDomicilioHistoLink" ><liferay-ui:message key="ver-historico" /> domicilios</a>
			<a href="javascript:ocultarDomicilioHisto();" style="display: none;" id="<portlet:namespace />ocultarDomicilioHistoLink" ><liferay-ui:message key="ocultar-historico" /> domicilios</a>
				<div align="left" id="<portlet:namespace />histo_domicilios">
				   <liferay-util:include page='/html/portlet/afiliados/historico_domicilios_search_result.jsp' />
				</div>
		</td>		
	</tr>

</table>
</fieldset>
<br />
<c:if test="<%= afiliado != null %>">
	<input type="button" value="<liferay-ui:message key="documentacion-adjunta" />" onClick="<portlet:namespace />documentacionAdjunta( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" />
<!-- 		
	<c:if test="<%= afiliado.getTiene_imagen() == 1 %>">  
		<input type="button" value="<liferay-ui:message key="ver-imagenes" />" onClick="<portlet:namespace />verImagenDirecta();" />
	</c:if>
	<c:if test="<%= afiliado.getTiene_imagen() > 1 %>">  
		<input type="button" value="<liferay-ui:message key="ver-imagenes" />" onClick="<portlet:namespace />verImagenes();" />
	</c:if>
-->	
	<input type="button"
		value="<liferay-ui:message key="certificado-afiliacion" />"
		onClick="<portlet:namespace />certificadoAfiliacion( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" />
</c:if>

<c:if test="<%= showABMButtons && afiliado != null && afiliado.esBaja() && !afiliado.esTitular() && !titular.esBaja() %>">
	<input type="button" value="<liferay-ui:message key="documentacion-adjunta-recuperar" />" onClick="<portlet:namespace />documentacionAdjuntaRecuperar( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" />	
</c:if>


<script>

<% if (afiliado==null){%>
	jQuery('#<portlet:namespace/>fechasSSS').hide();
<%}%>


<% if (afiliado==null || (afiliado!=null && afiliado.getDetalleOpcionSs()==null) ){%>
	jQuery('#<portlet:namespace/>opcionsss').hide();
<%}%>

function mostrarDomicilioHisto(){
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/historico_domicilios&cuil_titular=<%=afiliado.getCuil_titular()%>';
	jQuery('#<portlet:namespace />histo_domicilios').load(url, function() {
		jQuery('#<portlet:namespace />histo_domicilios').show();
		jQuery('#<portlet:namespace />ocultarDomicilioHistoLink').show();
		jQuery('#<portlet:namespace />mostrarDomicilioHistoLink').hide();	            															
	});
}

function ocultarDomicilioHisto(){
	jQuery('#<portlet:namespace />histo_domicilios').hide();
	jQuery('#<portlet:namespace />mostrarDomicilioHistoLink').show();
	jQuery('#<portlet:namespace />ocultarDomicilioHistoLink').hide();
}
</script>