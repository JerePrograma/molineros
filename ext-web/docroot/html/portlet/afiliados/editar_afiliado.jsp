<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="ar.com.ospim.afiliados.services.TelefonoServiceUtil" %>
<%@ page import="ar.com.ospim.global.beans.Telefono" %>

<%
String accion = (String)session.getAttribute(Constants.CMD);
Afiliado afiliado = (Afiliado) request.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
if (afiliado == null) {
    afiliado = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
}
String preCarga=(String)session.getAttribute("pre_carga");

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


boolean esEditarDeBaja = afiliado != null && afiliado.getBaja_fecha() != null && DateUtils.esMayor(DateUtils.getMismoDia_23_59hs(new Date()), DateUtils.getMismoDia_23_59hs(afiliado.getBaja_fecha())); 
/* boolean editable = afiliado != null ?  DateUtils.esMayor(new Date(), afiliado.getBaja_fecha() != null ? afiliado.getBaja_fecha() : new Date() ) : false;
 */Calendar vigenteFecha = CalendarFactoryUtil.getCalendar();
Date vigenteFechaAfil = afiliado != null ? afiliado.getVigen_fecha() : null; 
String opciones=(String)request.getParameter("opciones");
//boolean editable=  (String)request.getParameter("editBaja") != null ? true : false;


if (vigenteFechaAfil == null) {
	vigenteFecha.setTime(new Date());
} else {
	vigenteFecha.setTime(afiliado.getVigen_fecha());
}

Calendar fechaNacimiento = CalendarFactoryUtil.getCalendar();
Date fechaNacimientoAfil = afiliado != null ? afiliado.getNaci_fecha() : null; 
if (fechaNacimientoAfil == null) {
	fechaNacimiento.setTime(new Date());
} else {
	fechaNacimiento.setTime(afiliado.getNaci_fecha());
}

boolean esTitular = afiliado == null ? true : afiliado.esTitular();

boolean puedeEditarContacto = (afiliado == null);
if (afiliado != null) {
    String par = afiliado.getParentesco() != null ? afiliado.getParentesco().toUpperCase() : "";
    puedeEditarContacto = par.equals("TITULAR") || par.equals("CONYUGE") || par.equals("CONCUBINO/A");
}

String ddReinc = (String)request.getAttribute(WebKeysAfiliados.DESDE_REINCORPORAR); 

String obsGrpFliar = null;

if (afiliado != null) {
    try {
        //Si el afiliado no es el titular, siempre tomamos el domicilio del titular
        if (!afiliado.esTitular()) {
            Afiliado titular = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(afiliado.getCuil_titular(), 0);
            if (titular != null && titular.getDomicilioDefault() != null) {
                localidades = localidadesPorProvincia.get(
                    titular.getDomicilioDefault().getProvinciaId()
                );
            }
        } else {
            //Si es el titular, usamos su propio domicilio
            localidades = localidadesPorProvincia.get(
                afiliado.getDomicilioDefault().getProvinciaId()
            );
        }
    } catch (Exception e) {
        System.out.println("No se pudieron cargar localidades del titular: " + e.getMessage());
    }
}




Date fecha =  null;
String fechaRecepcion =  null;
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>
<input name="<portlet:namespace /><%= Constants.CMD %>"
	id="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
	value="<%= accion %>" />
<liferay-ui:error exception="<%= NoSuchAfiliadoEntryException.class %>"
	message="the-afiliado-could-not-be-found" />
<liferay-ui:error exception="<%= DuplicateAfiliadoIdException.class %>"
	message="the-afiliado-key-already-exists" />
<liferay-ui:error
	exception="<%= AfliadoYaTieneConyugeException.class %>"
	message="the-afiliado-ya-tiene-conyuge" />
<liferay-ui:error exception="<%= SystemException.class %>"
	message="sistema-no-disponible" />
<liferay-ui:error exception="<%= HijoNoPuedeSerCasadoException.class %>"
	message="the-hijo-no-puede-ser-casado" />
<liferay-ui:error
	exception="<%= ConyugeNoPuedeSerSolteroException.class %>"
	message="the-conyuge-no-puede-ser-soltero" />
<liferay-ui:error
	exception="<%= TitularNoPuedeSerSolteroException.class %>"
	message="the-titular-no-puede-ser-soltero" />


<%if(preCarga != null && (accion!=null&&accion.equalsIgnoreCase(Constants.UPDATE) )){ %>
<liferay-util:include page='/html/portlet/afiliados/diferencias_info_gral_preafi.jsp'>
</liferay-util:include>
<%} %>
<%if (afiliado != null && afiliado.getSuspencionCobertura()!=null && afiliado.getSuspencionCobertura().size() > 0){ %>
	<div id="divSuspencionCoberturaMed" style="background-color: #FAAC58; font-weight: bold; font: x-large; ">
		<fieldset class="block-labels">
		<legend><liferay-ui:message key="afi-sup-cobertura-medica" /></legend>
		<ul>
		<%for (AfiSuspencionCobertura asc : afiliado.getSuspencionCobertura()){ %>
			<li><%="Desde: " +sdf.format(asc.getVigenDesde()) + (asc.getVigenHasta()!=null?" Hasta: "+sdf.format(asc.getVigenHasta()):" -") %> </li>
		<% }%>
		</ul>
		</fieldset>
	</div>
<%} %>
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="seccional" />
	</legend>
	</br>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td colspan="5"><liferay-util:include
					page="/html/portlet/afiliados/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional"
						value="<%= afiliado != null ? String.valueOf(afiliado.getSeccional().getId()) : new String()  %>" />
					<liferay-util:param name="seccional"
						value="<%= afiliado != null ? afiliado.getSeccional().getDescripcion() : new String()  %>" />
					<% if (!esTitular) { %>
					<liferay-util:param name="esEdicion" value="false" />
					<%} %>
				</liferay-util:include></td>
		</tr>
	</table>
	</br>
</fieldset>
</br>

<fieldset class="block-labels">
	<legend><liferay-ui:message key="observaciones-internas" /></legend>
	<!-- </br> -->
	<table class="lfr-table" style="width: 100%;">
<%-- 	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="7"><textarea cols="100"
				name="<portlet:namespace/>obs" id="<portlet:namespace/>obs"><%= afiliado!=null && afiliado.getObservaciones() != null  ? afiliado.getObservaciones() : new String("") %></textarea></td>
	</tr>
	<%if(obsGrpFliar != null){%>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td><label><liferay-ui:message key="observaciones"/> grupo familiar:</label></td>
		<td colspan="7"><textarea cols="100" readonly="readonly"
			name="<portlet:namespace/>obsGrpFliar" 
			id="<portlet:namespace/>obsGrpFliar"><%= afiliado!=null && obsGrpFliar != null  ? obsGrpFliar : new String("") %></textarea></td>
	</tr>
	<%}%> --%>
	<tr>
		<td width="100%;">
	<!-- 	<fieldset class="block-labels">
		<legend><liferay-ui:message key="observaciones" /></legend> -->

			<table class="lfr-table" style="width: 100%; ">
					<tr>
						<td><label><liferay-ui:message key="observacion-interna" />:</label></td>
						<td colspan="8"><textarea cols="100"
								name="<portlet:namespace/>obs" id="<portlet:namespace/>obs"></textarea>
						</td>
						<td align="left">
						<img alt="<liferay-ui:message key='dar-alta'/>" 
								src="<%= themeDisplay.getPathThemeImages() + "/common/add.png"%>"
	 							onClick="javascript:nuevaObservacion();" />
	 					</td>		
					</tr>
			</table>
			<!-- </fieldset> -->
		</td>
	</tr>	
	<tr>
		<td width="100%;">
			<div align="center" id="<portlet:namespace />observaciones_internas" style="height:120px; overflow: scroll; overflow-x: hidden;">
					<liferay-util:include page="/html/portlet/afiliados/afi_observaciones_search_result.jsp">
					</liferay-util:include>
			</div>	
		</td>
	
	</tr>
	</table>
</fieldset> 
</br>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="datos-afiliado" />
	</legend>
	</br>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="cuil-titular" /></label></td>
			<td><input id="<portlet:namespace />cuil_titular"
				name="<portlet:namespace />cuil_titular" size="13" maxlength="11"
				type="text"
				value='<%= afiliado != null ? afiliado.getCuil_titular() : "" %>'
				<% if (afiliado != null) { %> readonly='readonly'
				onfocus='moveFocus();' <%} else { %>
				onblur='javascript:<portlet:namespace />validarCuilcv("cuil_titular");<portlet:namespace />proponerDNI();<portlet:namespace />completeCuil(); <portlet:namespace />buscarAfilExistenteCuilTitular(event);'
				<% } %> /></td>
			<td><label><liferay-ui:message key="integrante" />:</label></td>
			<td><input id="<portlet:namespace />inte"
				name="<portlet:namespace />inte" size="2" maxlength="2" type="text"
				value="<%= afiliado != null ? afiliado.getInteAsString() : '0' %>"
				onfocus='moveFocus();' /></td>
			<td><label><liferay-ui:message key="vigente-desde" />:</label></td>
			<%-- <td colspan="2"><liferay-ui:input-date
					dayParam="vigenteFechaDia"
					dayValue="<%= vigenteFecha.get(Calendar.DATE)%>"
					monthParam="vigenteFechaMes"
					monthValue="<%= vigenteFecha.get(Calendar.MONTH) %>"
					yearParam="vigenteFechaAnio"
					yearValue="<%= vigenteFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= vigenteFecha.get(Calendar.YEAR) - 120 %>"
					yearRangeEnd="<%= vigenteFecha.get(Calendar.YEAR) + 220 %>"
					firstDayOfWeek="<%= vigenteFecha.getFirstDayOfWeek()%>"
					disabled="<%= false %>" /></td>
			<td>&nbsp;</td> --%>
			<td><liferay-ui:input-date
					dayParam="vigenteFechaDia"
					dayValue="<%= vigenteFecha.get(Calendar.DATE)%>"
					monthParam="vigenteFechaMes"
					monthValue="<%= vigenteFecha.get(Calendar.MONTH) %>"
					yearParam="vigenteFechaAnio"
					yearValue="<%= vigenteFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= vigenteFecha.get(Calendar.YEAR) - 120 %>"
					yearRangeEnd="<%= vigenteFecha.get(Calendar.YEAR) + 220 %>"
					firstDayOfWeek="<%= vigenteFecha.getFirstDayOfWeek()%>"
					disabled="<%= (afiliado == null || !esEditarDeBaja || accion.equalsIgnoreCase(Constants.ADD)) ? false : true %>" /></td>
			<td><label>N° Correspondencia:</label></td>
			<td><input id="<portlet:namespace />numero_correspondencia"
			name="<portlet:namespace />numero_correspondencia" size="10" maxlength="10"
			type="text" onkeydown="allowOnlyDigits(event);"
			value="<%= afiliado != null ? afiliado.getIdCorrespondencia() : "" %>" /></td>		
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />apellido"
				name="<portlet:namespace />apellido" size="20" maxlength="100"
				type="text"
				value="<%= afiliado != null ? afiliado.getApellido() : "" %>" /></td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />nombre"
				name="<portlet:namespace />nombre" maxlength="100" type="text"
				value="<%= afiliado != null ? afiliado.getNombre() : "" %>" /></td>
			<td><label><liferay-ui:message key="sexo" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>sexo" id="<portlet:namespace/>sexo">
					<option
						<%= afiliado != null && afiliado.getSexo().trim().toUpperCase().equals("M") ? "selected" : ""  %>
						value="m">Masculino</option>
					<option
						<%= afiliado != null && afiliado.getSexo().trim().toUpperCase().equals("F") ? "selected" : ""  %>
						value="f">Femenino</option>
			</select></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td colspan="1"><select id="<portlet:namespace/>provincia"
				name="<portlet:namespace/>provincia" <% if (!esTitular) { %>
				disabled="disabled" <%} %> onchange="javascript:filtrarLocalidad();" style="width: 150px;">
					<%	for (Provincia provincia : provincias) { %>
					<option
						<%=afiliado != null && afiliado.getDomicilioDefault().getProvinciaId() == provincia.getId() ? "selected" : ""%>
						<%= afiliado == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>
			</select></td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td colspan="1">
			 <div class="selector-localidad">
			   <%if(afiliado != null) {%>
			   <select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <% if (!esTitular) { %>
				disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();javascript:filtrarCodAreaTel();"
				style="width: 250px;">
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option
						<%=afiliado != null && afiliado.getDomicilioDefault().getLocalidadId() == localidad.getId() ? "selected" : ""%>
						value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%>
			  </select>
			  <%} else{%>
			  	<select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <% if (!esTitular) { %>
				disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();javascript:filtrarCodAreaTel();"
				style="width: 250px;">
					<option selected value="0">Seleccione una localidad</option>
				 </select>	
			<%} %>		
			 </div>
			</td> 
			<td><label><liferay-ui:message key="calle" />:</label></td>
			<td colspan="1" style="vertical-align: top"><jsp:include
					page='/html/portlet/afiliados/busqueda_calle.jsp' /></td>
			<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />numero"
				name="<portlet:namespace />numero" size="5" maxlength="5"
				type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getNumero() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %>
				onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" />
			</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<div id='divCodPostal' style="float: right;"></div>
			<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />piso"
				name="<portlet:namespace />piso" size="5" maxlength="2" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getPiso() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="departamento" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />dpto"
				name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getDepto() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
				
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_postal"
				name="<portlet:namespace />cod_postal" size="5" maxlength="4"
				type="text" value="<%= afiliado != null ? afiliado.getDomicilioDefault().getPostal_codi() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %>></td>
			<td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />barrio"
				name="<portlet:namespace />barrio" size="12" maxlength="50"
				type="text" value="<%= afiliado != null ? afiliado.getDomicilioDefault().getBarrio() : "" %>"
				<% if (!esTitular) { %> readonly="readonly" <%} %> /></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>	
		<tr>		
		    <td colspan="1"><label>Cód.Area Cel:</label></td>
		    <td colspan="1"><input id="<portlet:namespace />cod_area_celular"
		        name="<portlet:namespace />cod_area_celular" size="5" maxlength="5" onkeydown="allowOnlyDigits(event);"
		        type="text" value="<%= telCel != null && telCel.getCodigoArea()!=null ? telCel.getCodigoArea() : "" %>"
		        <%= !puedeEditarContacto ? "readonly" : "" %>/></td>	
		    <td colspan="1"><label><liferay-ui:message key="celular" />:</label></td>
		    <td colspan="1"><input id="<portlet:namespace />celular" 
		        name="<portlet:namespace />celular" size="15" maxlength="15" onkeydown="allowOnlyDigitsConSuprimir(event);"
		        type="text" value="<%= telCel != null && telCel.getNumero()!=null ? telCel.getNumero() : "" %>"
		        <%= !puedeEditarContacto ? "readonly" : "" %>/></td>	
		    <td colspan="1"><label><liferay-ui:message key="email-short" />:</label></td>
		    <td colspan="2"><input id="<portlet:namespace />email"
		        name="<portlet:namespace />email" size="50" maxlength="50" onblur="javascript:<portlet:namespace />validarEmail();"
		        type="text" value="<%= (afiliado != null && afiliado.getEmail() != null ) ? afiliado.getEmail() : "" %>"
		        <%= !puedeEditarContacto ? "readonly" : "" %>/></td>
		    <td colspan="1">&nbsp;</td>		
		</tr>

		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
		    <td colspan="1"><label>Cód.Area Tel:</label></td>
		    <td colspan="1"><input id="<portlet:namespace />cod_area_telefono"
		        name="<portlet:namespace />cod_area_telefono" size="5" maxlength="5" onkeydown="allowOnlyDigits(event);"
		        type="text" value="<%= telFijo != null && telFijo.getCodigoArea()!=null ? telFijo.getCodigoArea() : "" %>"			
		        <%= !puedeEditarContacto ? "readonly" : "" %>/></td>
		    <td colspan="1"><label><liferay-ui:message key="telefono" />:</label></td>
		    <td colspan="1"><input id="<portlet:namespace />telefono"
		        name="<portlet:namespace />telefono" size="15" maxlength="15" onkeydown="allowOnlyDigitsConSuprimir(event);"
		        type="text" value="<%= telFijo != null && telFijo.getNumero()!=null ? telFijo.getNumero() : "" %>"
		        <%= !puedeEditarContacto ? "readonly" : "" %>/></td>
		</tr>

		<tr>
			<td colspan="8">
				<a href="javascript:mostrarDomicilioHisto();" id="<portlet:namespace />mostrarDomicilioHistoLink"  tabindex="-1"   ><liferay-ui:message key="ver-historico" /> domicilios</a>
				<a href="javascript:ocultarDomicilioHisto();" style="display: none;" id="<portlet:namespace />ocultarDomicilioHistoLink" ><liferay-ui:message key="ocultar-historico" /> domicilios</a>
					<div align="left" id="<portlet:namespace />histo_domicilios">
					   <liferay-util:include page='/html/portlet/afiliados/historico_domicilios_search_result.jsp' />
					</div>
			</td>		
		</tr>	
		
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="nacionalidad" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>nacionalidad" id="<portlet:namespace/>nacionalidad">
					<%	for (Nacionalidad nacion : nacionalidades) { %>
					<option
						<%= afiliado != null && afiliado.getNacionalidad() == nacion.getId() ? "selected" : ""  %>
						<%= afiliado == null && nacion.getId() == WebKeysAfiliados.ID_DEFAULT_NACIONALIDAD ? "selected" : ""  %>
						value="<%= nacion.getId() %>"><%=nacion.getDescripcion()%>
					</option>
					<%	}	%>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="tipo-documento" />:</label></td>
			<td colspan="1"><select
				name="<portlet:namespace/>documento_tipo"
				id="<portlet:namespace/>documento_tipo"
				onblur="javascript:<portlet:namespace />buscarAfilExistenteConVigenFecha(event);">
					<%	for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {	%>
					<option
						<%= afiliado != null && afiliado.getDocumento_tipo().equals(tipoDoc) ? "selected" : ""  %>
						<%= afiliado == null && tipoDoc.equals(WebKeysAfiliados.TIPO_DOCUMENTO_DEFAULT) ? "selected" : ""  %>
						value="<%= tipoDoc %>"><%=tipoDoc%>
					</option>
					<%	}	%>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="nro-documento" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />nroDoc"
				name="<portlet:namespace />nroDoc" size="9" maxlength="8"
				type="text"
				value="<%= afiliado != null ? afiliado.getDocu_numero() : "" %>"
				onblur="javascript:<portlet:namespace />buscarAfilExistente(event);"
				onkeydown="allowOnlyDigits(event);" />
				<div id='<portlet:namespace />nroDocAfilExistente'
					style="float: inherit;"></div></td>
			<td colspan="1"><label><liferay-ui:message key="cuil" />:</label></td>
			<td><input id="<portlet:namespace />cuil"
				name="<portlet:namespace />cuil" size="13" maxlength="11"
				type="text"
				value="<%= afiliado != null ? afiliado.getCuil() : "" %>"
				<% if (afiliado == null || (afiliado != null && afiliado.getId_parentesco()==WebKeysAfiliados.PARENTESCO_DEFAULT)) { %>
				readonly="readonly" onfocus="moveFocusNaci();" <%} else { %>
				onblur="javascript:<portlet:namespace />buscarAfilExistenteCuil(event);<portlet:namespace />validarCuilcv('cuil');"
				<%}%> /></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="fecha-nacimiento" />:</label></td>
			<td colspan="1">
				<!--<input id="<portlet:namespace />fecha_nacimiento" name="<portlet:namespace />fecha_nacimiento" size="13" type="text" value="" />-->
				<liferay-ui:input-date dayParam="fechaNacimientoDia"
					dayValue="<%= fechaNacimiento.get(Calendar.DATE) %>"
					monthParam="fechaNacimientoMes"
					monthValue="<%= fechaNacimiento.get(Calendar.MONTH) %>"
					yearParam="fechaNacimientoAnio"
					yearValue="<%= fechaNacimiento.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaNacimiento.get(Calendar.YEAR) - 200 %>"
					yearRangeEnd="<%= fechaNacimiento.get(Calendar.YEAR) + 200 %>"
					firstDayOfWeek="<%= fechaNacimiento.getFirstDayOfWeek()%>"
					disabled="<%= false %>" />
			</td>
			<td><label><liferay-ui:message key="estado-civil" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>estado_civil"
				id="<portlet:namespace/>estado_civil"
				onBlur="javascript:<portlet:namespace />validarOtrosCampos();">
					<%	for (EstadoCivil estadoCivil : estados_civil) {	%>
					<option
						<%= afiliado != null && afiliado.getId_civil_esta()==estadoCivil.getCodigo() ? "selected" : 
							afiliado == null && WebKeysAfiliados.ESTADO_CIVIL_DEFAULT == estadoCivil.getCodigo() ? "selected" : "" %>
						value="<%= estadoCivil.getCodigo() %>"><%=estadoCivil.getDescripcion()%></option>
					<%	}	%>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="parentesco" />:</label></td>
			<td colspan="3"><select name="<portlet:namespace/>parentesco" id="<portlet:namespace/>parentesco"
				<% if (afiliado == null || (afiliado != null && afiliado.getId_parentesco()==WebKeysAfiliados.PARENTESCO_DEFAULT)) { %>
				disabled="true" <%}%>>
					<%	for (Parentesco parentesco : parentescos) { %>
					
					    <% if(afiliado == null ){ %>
					    	<option
								<%= afiliado == null && parentesco.getCodigo() == WebKeysAfiliados.PARENTESCO_DEFAULT ? "selected" : ""  %>
								value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%>
							</option>
					    <% }else if(afiliado != null && afiliado.getInte() == 0){ %>
							<option
								<%= afiliado != null && afiliado.getId_parentesco() == parentesco.getCodigo() ? "selected" : ""  %>
								<%= afiliado == null && parentesco.getCodigo() == WebKeysAfiliados.PARENTESCO_DEFAULT ? "selected" : ""  %>
								value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%>
							</option>
						<%}else if(afiliado != null && afiliado.getInte() > 0 && parentesco.getCodigo() != WebKeysAfiliados.PARENTESCO_DEFAULT){ %>
							<option
								<%= afiliado != null && afiliado.getId_parentesco() == parentesco.getCodigo() ? "selected" : ""  %>
								value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%>
							</option>
													
						<%	} %>
					<%	} %>
			</select></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="os-ant" />:</label></td>
			<td colspan="5"><liferay-util:include
					page="/html/portlet/utils/obrassociales/busqueda_obrasocial.jsp">
					<liferay-util:param name="search_url" value="/afiliados/buscar_obrasocial" />
					<liferay-util:param name="id_obrasocial"
						value="<%= afiliado != null && afiliado.getAnterior_os() != 0 ? String.valueOf(afiliado.getAnterior_os()) : \"\"%>" />
					<% if (!esTitular) { %>
					<liferay-util:param name="esEdicion" value="false" />
					<%} %>
				</liferay-util:include></td>
		</tr>		
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		
	<tr>
		<td colspan="8" >
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
	</table>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="discapacitado" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>discapacitado" id="<portlet:namespace/>discapacitado">
					<option value="0"
						<%= afiliado != null && null!= afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("0") ? "selected" : ""  %>>No</option>
					<option value="1"
						<%= afiliado != null && null!= afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("1") ? "selected" : ""  %>>Si</option>
			</select></td>
			<td colspan="1">
				<% if(afiliado!=null && null!=afiliado.getDiscapacitado() ){ 
					if(afiliado.getDiscapacitado().equals("1") || afiliado.getFechaVtoDocDiscap() != null ){
					%> <label><liferay-ui:message key="fecha-vto-docdisc" />:</label>
				<%}} %>
			</td>
			<td colspan="1"><c:if test="<%=afiliado!=null %>">
					<% if(afiliado.getFechaVtoDocDiscap() == null) { 
					if(null!=afiliado.getDiscapacitado()&& afiliado.getDiscapacitado().equals("1")  ){  %>
					No tiene documentación.
				<%} } else { 
					Date hoy = new Date();
					
					if ( afiliado.getFechaVtoDocDiscap().after(hoy)  ){ %>
					<font color="green"> <%=  sdf.format(afiliado.getFechaVtoDocDiscap()) %>
					</font>
					<% } else { %>
					<font color="red"> <%=  sdf.format(afiliado.getFechaVtoDocDiscap()) %>
					</font>
					<%}}%>
				</c:if></td>
				<td><label><liferay-ui:message key="censo" />:</label></td>
				<td colspan="1"><select name="<portlet:namespace/>censo2013">
						<option value="0"
							<%= afiliado != null && afiliado.getCenso2013()==0 ? "selected" : ""  %>>No</option>
						<option value="1"
							<%= afiliado != null && afiliado.getCenso2013()==1 ? "selected" : ""  %>>Si</option>
				</select></td>
				<td><label><liferay-ui:message key="tieneAntJud" />:</label></td>
				<td colspan="1"><select name="<portlet:namespace/>tiene_antecedentes_judiciales">
						<option value="0"
							<%= afiliado != null && afiliado.getTieneAntecedentesJudiciales()==0 ? "selected" : ""  %>>No</option>
						<option value="1"
							<%= afiliado != null && afiliado.getTieneAntecedentesJudiciales()==1 ? "selected" : ""  %>>Si</option>
				</select></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="cliente_preferencial" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>cliente_preferencial">
						<option value="0"
							<%= afiliado != null && afiliado.getClientePreferencial()==0 ? "selected" : ""  %>>No</option>
						<option value="1"
							<%= afiliado != null && afiliado.getClientePreferencial()==1 ? "selected" : ""  %>>Si</option>
			</select></td>
			<td colspan="2">&nbsp;</td>
			<td><label>Proyecto:</label></td>
			<td><select name="<portlet:namespace />proyecto" id="<portlet:namespace />proyecto"> 
					<option value="" <%if(afiliado != null&&afiliado.getProyecto()==null){ %>selected="selected" <%} %>  ></option>
					<option value="VOLVER2016" <%if(afiliado != null&&afiliado.getProyecto()!=null
								&&afiliado.getProyecto().equalsIgnoreCase("VOLVER2016")){ %>selected="selected" <%} %> >VOLVER 2016</option> 
					<option value="INFOXAFIP" <%if(afiliado != null&&afiliado.getProyecto()!=null
								&&afiliado.getProyecto().equalsIgnoreCase("INFOXAFIP")){ %>selected="selected" <%} %> >Informados por AFIP</option>
					<option value="VIGENEXCEP" <%if(afiliado != null&&afiliado.getProyecto()!=null
								&&afiliado.getProyecto().equalsIgnoreCase("VIGENEXCEP")){ %>selected="selected" <%} %> >VIGENTE POR EXCEPCION</option>
					<option value="INFOXSSS" <%if(afiliado != null&&afiliado.getProyecto()!=null
							&&afiliado.getProyecto().equalsIgnoreCase("INFOXSSS")){ %>selected="selected" <%} %> >Informado por la SSS</option>
					<option value="MEJORPERT" <%if(afiliado != null&&afiliado.getProyecto()!=null
							&&afiliado.getProyecto().equalsIgnoreCase("MEJORPERT")){ %>selected="selected" <%} %> >MEJOR PERTENECER</option>											
				</select>
			</td>
			<td>
			<%if (afiliado != null && afiliado.getIncidentes() != null) {
				fecha = afiliado.getIncidentes().iterator().next().getFechaRecepcion(); 
				/* SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy"); */		
				fechaRecepcion = sdf.format(fecha);
			%>
				<span style="font-size: 9pt; color: green;  " id="<portlet:namespace />incidente"><label><b>Caso U.O. fecha:  <%=fechaRecepcion %>  </b></label></span>
			<%} %>
			</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
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
	
	<table class="lfr-table">
	<tr>
		<td>&nbsp;</td>
		<td>
		   <label style="color:red">*Cód.Area sin el 0</label>
		</td>
		<td>
		   <label style="color:red">*Celular sin el 15</label>
		</td>
		<td>
		   <label style="color:red">*Teléfono sin espacios ni guiones</label>
		</td>
	</tr>
	<tr>
	    <td>&nbsp;</td>
	    <td>
		   <label style="color:red">ejemplo (011) ingresar solo 11</label>
		</td>
		<td>
		   <label style="color:red">ejemplo (15609999) ingresar 609999</label>
		</td>
		<td>&nbsp;</td>
	</tr>
  </table>
	
	
</fieldset>
<br />

<input type="hidden" name="parentescoDesc" value="<%=afiliado != null && afiliado.getParentesco() != null ? afiliado.getParentesco() : ""%>">

<c:if test="<%= afiliado != null%>">
	<input type="hidden" name="baja_fecha_hidden" value="<%=DateUtils.format(afiliado.getBaja_fecha(), DateUtils.SHORT)%>">
	<input type="hidden" name="id_motivo_baja_hidden" value="<%=afiliado.getId_motivo_baja()%>">
</c:if>

<c:if test="<%= afiliado != null && esEditarDeBaja %>" >

	<input type="hidden" name="provincia" value="<%=afiliado.getDomicilioDefault().getProvinciaId()%>">
	<input type="hidden" name="localidad" value="<%=afiliado.getDomicilioDefault().getLocalidadId()%>">		
	<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />guardarDatos();" />
</c:if>

<%-- <%if( afiliado == null ? false : true ){ %>
	<input type="hidden" name="vigenteFechaDia" value="<%=vigenteFecha.get(Calendar.DATE)%>">
	<input type="hidden" name="vigenteFechaMes" value="<%=vigenteFecha.get(Calendar.MONTH)%>">
	<input type="hidden" name="vigenteFechaAnio" value="<%=vigenteFecha.get(Calendar.YEAR)%>">
 <%}%> 
 --%>
 <%-- <%if( afiliado != null && !editable && accion.equalsIgnoreCase(Constants.UPDATE) ? true : false ){ %>
	<input type="hidden" name="vigenteFechaDia" value="<%=vigenteFecha.get(Calendar.DATE)%>">
	<input type="hidden" name="vigenteFechaMes" value="<%=vigenteFecha.get(Calendar.MONTH)%>">
	<input type="hidden" name="vigenteFechaAnio" value="<%=vigenteFecha.get(Calendar.YEAR)%>">
 <%}%> --%>
<input type="hidden" name="vigenteFechaDiaAux" value="<%=vigenteFecha.get(Calendar.DATE)%>">
<input type="hidden" name="vigenteFechaMesAux" value="<%=vigenteFecha.get(Calendar.MONTH)%>">
<input type="hidden" name="vigenteFechaAnioAux" value="<%=vigenteFecha.get(Calendar.YEAR)%>">
	 
<%if(afiliado != null && afiliado.esTitular() && esEditarDeBaja){ %>
	<td colspan="6" align="right">
		<input type="button" value="<liferay-ui:message key="cambios-cobertura" />"	onClick="<portlet:namespace />verCambioHistorico();" />
	</td>	
<%}%>

<c:if test="<%=!esEditarDeBaja %>" > 
<input type="button" value="<liferay-ui:message key="next" />"
	onClick="<portlet:namespace />saveAfiliadoEntry();" />
 </c:if> 
<%	if (ddReinc == null || !ddReinc.equals(WebKeysAfiliados.DESDE_REINCORPORAR)){ %>
<c:if test="<%= afiliado != null %>">
<%-- 	 <%if(editable)   { %>
		<input type="button"
			value="<liferay-ui:message key="documentacion-adjunta" />"
			onClick="<portlet:namespace />documentacionAdjunta( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" />
	 <%} %>	 --%>	
	 <%if(!esEditarDeBaja )   { %>
 		<input type="button"
			value="<liferay-ui:message key="documentacion-adjunta" />"
			onClick="<portlet:namespace />documentacionAdjunta( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" /> 
		 <%if(afiliado.getInte() == 0 )   { %>
		<input type="button"
			value="<liferay-ui:message key="cargar-integrante" />"
			onClick="<portlet:namespace />cargarIntegrante();" />
		<%}%>
	<%}%>
	<c:if test="<%= afiliado.getTiene_imagen() == 1 %>">
<!--  	
		<input type="button" value="<liferay-ui:message key="ver-imagenes" />"
			onClick="<portlet:namespace />verImagenDirecta();" />
-->			
	</c:if>
	<c:if test="<%= afiliado.getTiene_imagen() > 1 %>">
<!--	
		<input type="button" value="<liferay-ui:message key="ver-imagenes" />"
			onClick="<portlet:namespace />verImagenes();" />
-->			
	</c:if>
	<c:if
		test='<%= null != afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("1") %>'>
		<input type="button" value="<liferay-ui:message key="det-discap" />"
			onClick="<portlet:namespace />detalleDiscapacidad( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" />
	</c:if>
		<c:if	test='<%= null != afiliado && (CredencialesServiceUtil.validarExisteExentoCopago(afiliado.getCuil_titular(), afiliado.getInte()) == 1 || DateUtils.getEdad(afiliado.getNaci_fecha()) == 0)  %>'>

				<input type="button" value="<liferay-ui:message key="imprimir-credenciales-exento" />" onClick="<portlet:namespace />imprimirExcentoCoPago(<%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" />
			
			
	</c:if>
	
</c:if>
<br />
<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">		
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />id_seccional);		
		</script>
</c:if>
<%} %>

<script type="text/javascript">


	
<% if (afiliado==null){%>
	jQuery('#<portlet:namespace/>fechasSSS').hide();
<%}%>


<% if (afiliado==null || (afiliado!=null && afiliado.getDetalleOpcionSs()==null) ){%>
	jQuery('#<portlet:namespace/>opcionsss').hide();
<%}%>



	jQuery('#<portlet:namespace />sexo').change(function(){validarPrefijoCuilPorSexo();});	
	jQuery('#<portlet:namespace />cuil').change(function(){validarPrefijoCuilPorSexo();});

	jQuery(window).load(function () {
		<%if(afiliado != null) {%>
			validarPrefijoCuilPorSexo();
		<%}%>
	});
	
	
	function validarPrefijoCuilPorSexo(){
		
		var cuil = jQuery('#<portlet:namespace/>cuil').val();
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
	

	function mostrarDomicilioHisto(){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/historico_domicilios&cuil_titular=<%=afiliado != null?afiliado.getCuil_titular():""%>';
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
	
	function <portlet:namespace />validarCuilcv(cuil){
		if(cuil=="cuil"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil').val();
		}else if(cuil=="cuil_titular"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil_titular').val();
		}else{
			return;
		}
	    
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_cuil&cuil='+cuil_final+'&vigenteFechaDia='+diaVig+'&vigenteFechaMes='+mesVig+'&vigenteFechaAnio='+anioVig+'&inte=0';
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
	

	function <portlet:namespace />validarCuilcvAsync(cuil){

		var respuesta=true;
		if(cuil=="cuil"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil').val();
		}else if(cuil=="cuil_titular"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil_titular').val();
		}
	
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());
		
		

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_cuil&cuil='+cuil_final+'&vigenteFechaDia='+diaVig+'&vigenteFechaMes='+mesVig+'&vigenteFechaAnio='+anioVig+'&inte=0';
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.validado=="1"){
					alert("<liferay-ui:message key='cuil-invalido'/>");
					respuesta = false;
				}else if(obj.validado=="2"){
					alert("<liferay-ui:message key='cuil-titular-existente'/>");
					return respuesta = false;
				}			 					
			}				                                                                                                                                                                                                                                                            
			
		});
		return respuesta;
	}
	
	
	
	function filtrarLocalidad() {
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;
				jQuery("#<portlet:namespace/>localidad").removeAttr('disabled');
				var obj = jQuery.parseJSON(data);
//				for(var i =0;i< obj.listaFiltrada.length; i++){	
//					jQuery("#<portlet:namespace/>localidad").append(obj.listaFiltrada[i]);
//				}
				
				jQuery('.selector-localidad select').html(data).fadeIn();

			}
		});
	}

	function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_localidad_codpostal&idLocalidad='+idLocalidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
	}

	function filtrarCodAreaTel() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_localidad_cod_area_tel&idLocalidad='+idLocalidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_area_telefono").length = 0;
				document.getElementById("<portlet:namespace />cod_area_celular").length = 0;
				var obj = jQuery.parseJSON(data);						
				if(obj.codAreaTel != null && obj.codAreaTel != 'null' ){
					jQuery('#<portlet:namespace />cod_area_telefono').val(obj.codAreaTel);
					jQuery('#<portlet:namespace />cod_area_celular').val(obj.codAreaTel);
				}else{
					jQuery('#<portlet:namespace />cod_area_telefono').val("");
					jQuery('#<portlet:namespace />cod_area_celular').val("");
				}
			}
		});	
	}

	function validarEmail() {
		var email = jQuery('#<portlet:namespace/>email').val();
		var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
		
		if(trim(email).length > 0){	
			if( !emailReg.test( email ) ) {
				return false;
			} else {
				return true;
			}
		}else{
			return true;
		}	
	}
	
	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	function <portlet:namespace />buscarCodPostalOnDiv(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad").val() == "265" && jQuery("#<portlet:namespace />calle").val() != "" && jQuery("#<portlet:namespace />numero").val() > 0) {
			var calle = jQuery("#<portlet:namespace />calle").val();
			var numero = jQuery("#<portlet:namespace />numero").val();
			if (calle.length > 0 && numero > 0) {				
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				jQuery("#divCodPostal").load(url);		
				jQuery("#divCodPostal").show();
			} else {        
	    		jQuery("#divCodPostal").hide("slow");
	   		}
		}
	}
 
	function <portlet:namespace />cerrarCodPostal() {	
		jQuery("#divCodPostal").hide("slow");
	}

	function <portlet:namespace />proponerDNI() {
		var tipoDoc = jQuery('#<portlet:namespace/>documento_tipo').val();
		if (tipoDoc=='DU') {
			var dni = jQuery('#<portlet:namespace />cuil_titular').val().substring(2,10);
			jQuery('#<portlet:namespace />nroDoc').val(dni);
		} else {
			jQuery('#<portlet:namespace />nroDoc').val("");
		}
	}

	function <portlet:namespace />completeCuil() {
		var cuil = jQuery('#<portlet:namespace />cuil_titular').val();
		jQuery('#<portlet:namespace />cuil').val(cuil);
		validarPrefijoCuilPorSexo();
	}

	function <portlet:namespace />validarOtrosCampos() {
		/*var parentesco = jQuery('#<portlet:namespace/>parentesco').val();
		var estado_civil = jQuery('#<portlet:namespace/>estado_civil').val();
		if ((parentesco=='Conyuge' || parentesco=='Concubino/a') && estado_civil=='Soltero') {
			alert("<liferay-ui:message key='the-conyuge-no-puede-ser-soltero' />");	
			return false;	
		} 	if ((parentesco=='Hijo soltero menor de 21 años'||parentesco=='Hijo del conyuge soltero de 21 a 25 años cursando estudios regulares'||parentesco=='Hijo del conyuge soltero menor de 21 años'||parentesco=='Hijo soltero de 21 a 25 años cursando estudios regulares'||parentesco=='Familiar a cargo'||parentesco=='Menor bajo guarda o tutela')&&(estado_civil=='Casado')) {
			alert("<liferay-ui:message key='the-hijo-no-puede-ser-casado' />");
			return false;	
		}*/
		var cod_parentesco=parseInt(jQuery('#<portlet:namespace/>parentesco').val());
		var cod_estado_civil=parseInt(jQuery("#<portlet:namespace/>estado_civil").val());
		
		if ((parentesco==1 || parentesco==2) && (estado_civil==1)) {
			alert("<liferay-ui:message key='the-conyuge-no-puede-ser-soltero' />");	
			return false;	
		} 	if ((parentesco==3||parentesco==4||parentesco==5||parentesco==6||parentesco==7||parentesco==8)
				&&(estado_civil==2 || estado_civil==7)) {
			alert("<liferay-ui:message key='the-hijo-no-puede-ser-casado' />");
			return false;	
		}

	}

	
	function <portlet:namespace />guardarDatos() {
	
		var url = "<portlet:actionURL ><portlet:param name='struts_action' value='/afiliados/guardar_otros_datos' /></portlet:actionURL>";
		
	    url=url+'&opciones=false&';
	    url=url+"<%= Constants.EDIT %>" + "=" + "<%= Constants.UPDATE %>";
			
		submitForm(document.<portlet:namespace />fm, url);
		
	}
	
	function <portlet:namespace />verCambioHistorico(){	
		
		var url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/cambia_historico_cobertura_entry' /></portlet:actionURL>";
		

		submitForm(document.<portlet:namespace />fm, url);
	}


	function nuevaObservacion() {
		var cuil_titu=jQuery('#<portlet:namespace />cuil_titular').val();
		var integ=jQuery('#<portlet:namespace />inte').val();
		var obserInterna = jQuery('#<portlet:namespace/>obs').val();


		if(trim(obserInterna).length == 0){
			
			alert("Complete la observación interna");
			return false;
			
		}
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>">
					<portlet:param name="struts_action" value="/afiliados/buscar_observaciones_internas" />
					<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.SAVE %>" />
				</portlet:renderURL>';
				
		var params = {
						"cuil_titular":cuil_titu,
						"inte":integ,
						"observacion_interna":obserInterna
					 }
		
		<%-- <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
		
				 url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
								<portlet:param name="struts_action" value="/cai/buscar_observaciones_internas" />
								<portlet:param name="cmd" value="<%=Constants.SAVE %>" />
						</portlet:renderURL>';
		</c:if> --%>
		
		jQuery("#<portlet:namespace />observaciones_internas").load(url,params);
		jQuery('#<portlet:namespace/>obs').val('');
		
	}

  
</script>