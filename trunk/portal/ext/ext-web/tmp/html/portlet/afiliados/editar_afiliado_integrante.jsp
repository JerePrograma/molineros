<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="ar.com.ospim.util.DateUtils"%>
<portlet:defineObjects />

<%
String accion = (String)session.getAttribute(Constants.CMD);
Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

Afiliado preAfiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_SESSION);

Calendar vigenteFecha = CalendarFactoryUtil.getCalendar();
Date vigenteFechaAfil = preAfiliado != null ? preAfiliado.getVigen_fecha() : null; 
String opciones=(String)request.getParameter("opciones");

if (vigenteFechaAfil == null) {
	vigenteFecha.setTime(new Date());
} else {
	vigenteFecha.setTime(preAfiliado.getVigen_fecha());
}

Calendar fechaNacimiento = CalendarFactoryUtil.getCalendar();
Date fechaNacimientoAfil = preAfiliado != null ? preAfiliado.getNaci_fecha() : null; 
if (fechaNacimientoAfil == null) {
	fechaNacimiento.setTime(new Date());
} else {
	fechaNacimiento.setTime(preAfiliado.getNaci_fecha());
}

%>

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
	

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="observaciones" />
	</legend>
	</br>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="observaciones" />:</label></td>
			<td colspan="7"><textarea cols="100"
					name="<portlet:namespace/>obs" id="<portlet:namespace/>obs"><%= afiliado!=null && afiliado.getObservaciones() != null  ? afiliado.getObservaciones() : new String("") %></textarea></td>
		</tr>
	</table>
</fieldset>
</br>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="datos-integrante" />
	</legend>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="cuil-titular" />:</label></td>
			<td><input id="<portlet:namespace />cuil_titular"
				name="<portlet:namespace />cuil_titular" size="13" maxlength="11"
				type="text"
				value="<%= afiliado != null ? afiliado.getCuil_titular() : "" %>"
				<% if (afiliado != null) { %>
				<%="readonly='readonly' onfocus='moveFocus();'" %> <%} else { %>
				<%="onblur='javascript:<portlet:namespace />validarCuilcvAsync(event);"%>
				<%} %> /></td>
			<td>&nbsp;</td>
			<td><label><liferay-ui:message key="integrante" />:</label></td>
			<td colspan="2"><input id="<portlet:namespace />inte"
				name="<portlet:namespace />inte" size="2" maxlength="2" type="text"
				value="" readonly='readonly' onfocus='moveFocus();'></input></td>
			<td><label><liferay-ui:message key="vigente-desde" />:</label></td>
			<td>
				<!--<input id="<portlet:namespace />fecha_nacimiento" name="<portlet:namespace />fecha_nacimiento" size="13" type="text" value="" />-->
				<liferay-ui:input-date dayParam="vigenteFechaDia"
					dayValue="<%= vigenteFecha.get(Calendar.DATE)%>"
					monthParam="vigenteFechaMes"
					monthValue="<%= vigenteFecha.get(Calendar.MONTH) %>"
					yearParam="vigenteFechaAnio"
					yearValue="<%= vigenteFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= vigenteFecha.get(Calendar.YEAR) - 120 %>"
					yearRangeEnd="<%= vigenteFecha.get(Calendar.YEAR) + 120 %>"
					firstDayOfWeek="<%= vigenteFecha.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" />
			</td>
			<td><label>N° Correspondencia:</label></td>
			<td><input id="<portlet:namespace />numero_correspondencia"
			name="<portlet:namespace />numero_correspondencia" size="10" maxlength="10"
			type="text" onkeydown="allowOnlyDigits(event);"
			value="<%= afiliado != null ? afiliado.getIdCorrespondencia() : "" %>" /></td>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td colspan="2"><input id="<portlet:namespace />apellido"
				name="<portlet:namespace />apellido" size="20" maxlength="100"
				type="text" value="<%= preAfiliado != null ? preAfiliado.getApellido() : "" %>" /></td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td colspan="2"><input id="<portlet:namespace />nombre"
				name="<portlet:namespace />nombre" maxlength="100" type="text"
				value="<%= preAfiliado != null ? preAfiliado.getNombre() : "" %>" /></td>
			<td><label><liferay-ui:message key="sexo" />:</label></td>
			<td colspan="2"><select name="<portlet:namespace/>sexo"  id="<portlet:namespace/>sexo">
					<option value="m" <% if(preAfiliado != null && preAfiliado.getSexo().equalsIgnoreCase("m")){ %> selected="selected" <%} %>>Masculino</option>
					<option value="f" <% if(preAfiliado != null && preAfiliado.getSexo().equalsIgnoreCase("f")){ %> selected="selected" <%} %>>Femenino</option>
			</select></td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="nacionalidad" />:</label></td>
			<td colspan="2"><select name="<portlet:namespace/>nacionalidad" id="<portlet:namespace/>nacionalidad">
					<%	for (Nacionalidad nacion : nacionalidades) { %>
					<option
						<%= preAfiliado != null && preAfiliado.getNacionalidad() == nacion.getId() ? "selected" : ""  %>
						<%= preAfiliado == null && nacion.getId() == WebKeysAfiliados.ID_DEFAULT_NACIONALIDAD ? "selected" : ""  %>
						value="<%= nacion.getId() %>"><%=nacion.getDescripcion()%>
					</option>
					<%	}	%>
				</select>
			</td>
			<td><label><liferay-ui:message key="discapacitado" />:</label></td>
			<td colspan="2"><select name="<portlet:namespace/>discapacitado">
					<option value="0"
						<%= preAfiliado != null && null!= preAfiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("0") ? "selected" : ""  %>>No</option>
					<option value="1"
						<%= preAfiliado != null && null!= preAfiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("1") ? "selected" : ""  %>>Si</option>
			</select></td>
			<td><label><liferay-ui:message key="parentesco" />:</label></td>
			<td colspan="2">
				<select name="<portlet:namespace/>parentesco" id="<portlet:namespace/>parentesco">
					<%	for (Parentesco parentesco : parentescos) { %>
						<% if(parentesco.getCodigo() != WebKeysAfiliados.PARENTESCO_DEFAULT){ %> <!-- sacamos al titular de las opciones -->
							<option
								<%= preAfiliado != null && preAfiliado.getId_parentesco() == parentesco.getCodigo() ? "selected" : ""  %>
								value="<%= parentesco.getCodigo() %>"><%=parentesco.getDescripcion()%>
							</option>
						<% }%>	
					<%	} %>
			</select>
			</td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="cuil" />:</label></td>
			<td><input id="<portlet:namespace />cuil"
				name="<portlet:namespace />cuil" size="13" maxlength="11"
				type="text" value="<%= preAfiliado != null ? preAfiliado.getCuil() : "" %>"
				onblur="javascript:<portlet:namespace />validarCuilcv('cuil');validarPrefijoCuilPorSexo();" />
				<div id='<portlet:namespace />nroDocAfilExistente'
					style="float: inherit;"></div></td>
			<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
			<td colspan="2"><select
				name="<portlet:namespace/>documento_tipo"
				id="<portlet:namespace/>documento_tipo">
					<%	for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {	%>
					<option
						<%= preAfiliado != null && preAfiliado.getDocumento_tipo().equals(tipoDoc) ? "selected" : ""  %>
						<%= preAfiliado == null && tipoDoc.equals(WebKeysAfiliados.TIPO_DOCUMENTO_DEFAULT) ? "selected" : ""  %>
						value="<%= tipoDoc %>"><%=tipoDoc%>
					</option>
					<%	}	%>
			</select></td>
			<td><label><liferay-ui:message key="nro-documento" />:</label></td>
			<td colspan="2"><input id="<portlet:namespace />nroDoc"
				name="<portlet:namespace />nroDoc" size="9" maxlength="8"
				type="text" value="<%= preAfiliado != null ? preAfiliado.getDocu_numero() : "" %>"
				onfocus="javascript:<portlet:namespace />proponerDNI();" />
			</td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="fecha-nacimiento" />:</label></td>
			<td colspan="4">
				<!--<input id="<portlet:namespace />fecha_nacimiento" name="<portlet:namespace />fecha_nacimiento" size="13" type="text" value="" />-->
				<liferay-ui:input-date dayParam="fechaNacimientoDia"
					dayValue="<%= fechaNacimiento.get(Calendar.DATE)%>"
					monthParam="fechaNacimientoMes"
					monthValue="<%= fechaNacimiento.get(Calendar.MONTH) %>"
					yearParam="fechaNacimientoAnio"
					yearValue="<%= fechaNacimiento.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaNacimiento.get(Calendar.YEAR) - 200 %>"
					yearRangeEnd="<%= fechaNacimiento.get(Calendar.YEAR) + 200 %>"
					firstDayOfWeek="<%= fechaNacimiento.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" />
			</td>
			<td><label><liferay-ui:message key="estado-civil" />:</label></td>
			<td colspan="1">
				<select name="<portlet:namespace/>estado_civil"
					id="<portlet:namespace/>estado_civil"
					onBlur="javascript:<portlet:namespace />validarOtrosCampos();">
						<%	for (EstadoCivil estadoCivil : estados_civil) {	%>
					<option
						<%= preAfiliado != null && preAfiliado.getId_civil_esta()==estadoCivil.getCodigo() ? "selected" : 
							preAfiliado == null && WebKeysAfiliados.ESTADO_CIVIL_DEFAULT == estadoCivil.getCodigo() ? "selected" : "" %>
						value="<%= estadoCivil.getCodigo() %>"><%=estadoCivil.getDescripcion()%></option>
					<%	}	%>
				</select>
			</td>
			<td colspan="2" ><label><liferay-ui:message key="tieneAntJud" />:</label>&nbsp;
				<select name="<portlet:namespace/>tiene_antecedentes_judiciales">
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
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td colspan="5"><liferay-util:include
					page="/html/portlet/afiliados/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional"
						value="<%= afiliado != null ? String.valueOf(afiliado.getSeccional().getId()) :  new String() %>" />
					<liferay-util:param name="seccional" value="" />
					<liferay-util:param name="esEdicion" value="false" />
				</liferay-util:include></td>
			<td><label><liferay-ui:message key="obra-social-ant" />:</label></td>
			<td colspan="4"><select disabled="disabled">
					<%	for (ObraSocialCampo os : obrasSocialesAnteriores) {%>
					<option value="<%= os.getId() %>"
						<% if (os.getId() == afiliado.getAnterior_os()){ %>
						selected="selected" <%} %>><%=os.getDescripcionShort()%></option>

					<%	}	%>
			</select> <input type="hidden" name="<portlet:namespace/>obra_social_ant"
				value="<%= String.valueOf(afiliado.getAnterior_os())%>" /></td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<%-- <tr>
			<td><label><liferay-ui:message key="observaciones" />:</label></td>
			<td colspan="10"><textarea cols="100"
				name="<portlet:namespace/>obs" id="<portlet:namespace/>obs"></textarea></td>
		</tr> --%>
	</table>
</fieldset>
<br />
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="home-address-afi" />
	</legend>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td colspan="2"><select disabled="disabled">
					<% for (Provincia provincia : provincias) {  %>
					<option
						<%=afiliado != null && afiliado.getDomicilioDefault().getProvinciaId() == provincia.getId() ? "selected" : ""%>
						<%= afiliado == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	}	%>
			</select> <input type="hidden" name="<portlet:namespace/>provincia"
				value="<%= afiliado != null && afiliado.getDomicilioDefault()!=null ? afiliado.getDomicilioDefault().getProvinciaId()  : ""%>" />
			</td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			<td colspan="2"><select disabled="disabled">
					<% for (Localidad localidad : localidades) { 	%>
					<option
						<%=afiliado != null && afiliado.getDomicilioDefault().getLocalidadId() == localidad.getId() ? "selected" : ""%>
						<%= afiliado == null && localidad.getId() == WebKeysAfiliados.ID_DEFAULT_LOCALIDAD ? "selected" : ""  %>
						value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<% 	} 	%>
			</select> <input type="hidden" name="<portlet:namespace/>localidad"
				value="<%= afiliado != null && afiliado.getDomicilioDefault()!=null ? afiliado.getDomicilioDefault().getLocalidadId()  : ""%>"
				readonly="readonly" /></td>
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td colspan="2"><input id="<portlet:namespace />cod_postal"
				readonly="readonly" name="<portlet:namespace />cod_postal" size="5"
				maxlength="4" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getPostal_codi() : "" %>" />
			</td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="calle" />:</label></td>
			<td colspan="5"><input id="<portlet:namespace />calle"
				readonly="readonly" name="<portlet:namespace />calle" size="50"
				maxlength="100" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getCalle() : "" %>" />
			</td>
			<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />numero"
				readonly="readonly" name="<portlet:namespace />numero" size="5"
				maxlength="5" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getNumero() : "" %>" />
			</td>
			<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />piso"
				readonly="readonly" name="<portlet:namespace />piso" size="5"
				maxlength="2" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getPiso() : "" %>" />
			</td>
			<td colspan="1"><label><liferay-ui:message
						key="departamento" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />dpto"
				readonly="readonly" name="<portlet:namespace />dpto" size="5"
				maxlength="4" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getDepto() : "" %>" />
			</td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
			<td colspan="2"><input id="<portlet:namespace />barrio"
				readonly="readonly" name="<portlet:namespace />barrio" size="12"
				maxlength="50" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getBarrio() : "" %>" />
			</td>
			<td colspan="1"><label><liferay-ui:message
						key="telefono" />:</label></td>
			<td colspan="2"><input id="<portlet:namespace />telefono"
				readonly="readonly" name="<portlet:namespace />telefono" size="12"
				maxlength="11" type="text"
				value="<%= afiliado != null ? afiliado.getDomicilioDefault().getTelefono() : "" %>" />
			</td>
		</tr>
		<tr>
			<td colspan="11">&nbsp;</td>
		</tr>
		<tr>
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
		</tr>
	</table>
</fieldset>
<br />
<input type="hidden" value="<%=afiliado.getId_ospim()%>"
	id="<portlet:namespace />id_ospim" name="<portlet:namespace />id_ospim" />
<input type="hidden" value="<%=afiliado.getId_uoma()%>"
	id="<portlet:namespace />id_uoma" name="<portlet:namespace />id_uoma" />
<input type="hidden" value="<%=afiliado.getId_amtima()%>"
	id="<portlet:namespace />id_amtima"
	name="<portlet:namespace />id_amtima" />
<input type="hidden" value="false"
	id="<portlet:namespace />cuil_validado"
	name="<portlet:namespace />cuil_validado" />

<c:if test="<%= afiliado != null && afiliado.getBaja_fecha() !=null  %>" >
	<input type="hidden" name="baja_fecha_hidden" value="<%= DateUtils.format(afiliado.getBaja_fecha(), DateUtils.SHORT)%>">
	<input type="hidden" name="id_motivo_baja_hidden" value="<%=afiliado.getId_motivo_baja()%>">	
</c:if>
	
<input type="button" value="<liferay-ui:message key="save" />"
	onClick="javascript:<portlet:namespace />saveAfiliadoEntry()" />
<c:if
	test='<%=null != preAfiliado && null != preAfiliado.getDiscapacitado() && preAfiliado.getDiscapacitado().equals("1") %>'>
	&nbsp;&nbsp;
	<input type="button" value="<liferay-ui:message key="det-discap" />"
		onClick="<portlet:namespace />detalleDiscapacidad( <%=preAfiliado.getCuil_titular()%>, <%=preAfiliado.getInte() %>);" />
</c:if>


<br />
<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />cuil_titular);
	</script>
</c:if>

<script type="text/javascript">
	function <portlet:namespace />validarCuilcv(cuil){		
		if(cuil=="cuil"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil').val();
		}else if(cuil=="cuil_titular"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil_titular').val();
		}
		
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());
		
		
		
		var diaNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoDia').val());
		var mesNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoMes').val())+1;
		var anioNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoAnio').val());
		
		var parentesco = jQuery('#<portlet:namespace/>parentesco').val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_cuil&cuil='+cuil_final+'&inte=1'+'&vigenteFechaDia='+diaVig+'&vigenteFechaMes='+mesVig+'&vigenteFechaAnio='+anioVig;
		url = url   +'&diaNac='+diaNac+'&mesNac='+mesNac+'&anioNac='+anioNac+'&parentesco='+parentesco;
		if(cuil_final!="ET"){			
			jQuery.ajax({   
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					if(obj.validado=="1"){
						alert("<liferay-ui:message key='cuil-invalido'/>");
						jQuery('#<portlet:namespace />cuil_validado').val('false');
					}else if(obj.validado=="2"){
						alert("<liferay-ui:message key='cuil-titular-existente'/>");
						jQuery('#<portlet:namespace />cuil_validado').val('false');
					}else{
						jQuery('#<portlet:namespace />cuil_validado').val('true');
					}					
				}				                                                                                                                                                                                                                                                            
				
			});
		}
	}

	function <portlet:namespace />validarOtrosCampos() {
		var parentesco = jQuery('#<portlet:namespace/>parentesco').val();
		var estado_civil = jQuery('#<portlet:namespace/>estado_civil').val();
		if ((parentesco==3 || parentesco==4 || parentesco==5 || parentesco==6 || parentesco==7 ||parentesco==8)
				&&(estado_civil==2 || estado_civil==7)) {
			alert("<liferay-ui:message key='the-hijo-no-puede-ser-casado' />");
			return false;	
		} if ((parentesco==1 || parentesco==2) && (estado_civil==1)) {
			alert("<liferay-ui:message key='the-conyuge-no-puede-ser-soltero' />");	
			return false;	
		}
	}

	function <portlet:namespace />proponerDNI() {
		var tipoDoc = jQuery('#<portlet:namespace/>documento_tipo').val();
		if (tipoDoc=='DU') {
			var dni = jQuery('#<portlet:namespace />cuil').val().substring(2,10);
			jQuery('#<portlet:namespace />nroDoc').val(dni);
		} else {
			jQuery('#<portlet:namespace />nroDoc').val("");
		}		
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
	

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_cuil&cuil='+cuil_final+'&vigenteFechaDia='+diaVig+'&vigenteFechaMes='+mesVig+'&vigenteFechaAnio='+anioVig+'&inte=1';
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
	
	function <portlet:namespace />validarFechaVigenAteriorTitularAsync(){
		var respuesta=true;
		var cuil_titular = jQuery('#<portlet:namespace/>cuil_titular').val();
		

	
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validarFechaVigenAteriorTitular&cuil_titular='+cuil_titular+'&vigenteFechaDia='+diaVig+'&vigenteFechaMes='+mesVig+'&vigenteFechaAnio='+anioVig;
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.validado=="1"){
					alert("<liferay-ui:message key='vigen-fecha-integrante-menor-titular'/>");
					respuesta = false;
				}		 					
			}				                                                                                                                                                                                                                                                            
			
		});
		return respuesta;
	}
	
</script>