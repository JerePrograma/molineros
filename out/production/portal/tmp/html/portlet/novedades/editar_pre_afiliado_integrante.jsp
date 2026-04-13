<%@ include file="/html/portlet/novedades/init.jsp"%>
<%
String accion = (String)request.getAttribute(Constants.CMD);

PreAfiliado afiliado = (PreAfiliado)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);
Calendar vigenteFecha = CalendarFactoryUtil.getCalendar();
Date vigenteFechaAfil = afiliado != null ? afiliado.getVigen_fecha() : null; 
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

boolean esTitular = afiliado == null ? true : afiliado.getInte() == 0;
/*Integer idSecSugerido = (Integer)request.getAttribute("id_seccional_sugerida");
if(afiliado!=null){
	idSecSugerido = afiliado.getId_seccional();
}*/
String cuil_titular = (String) ParamUtil.getString(request, "cuil_titular_p");;
if(cuil_titular != null){
	esTitular = false;
}

String tipo_nov = "";
tipo_nov = (String) request.getAttribute("tipo_novedad_pre_afi");
if(tipo_nov == null && afiliado != null){
	tipo_nov = afiliado.getTipo_novedad();
}

String tabsAMostrar = request.getParameter("tabs_a_mostrar");
if (tabsAMostrar == null || tabsAMostrar.trim().equals("")){
	tabsAMostrar = (String)request.getAttribute("tabs_a_mostrar");
}
%> 

<%-- <input name="<portlet:namespace /><%= Constants.CMD %>"
	id="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
	value="<%= accion %>" /> --%>
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
		<liferay-ui:message key="seccional" />
	</legend>
	</br>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td colspan="5"><liferay-util:include
					page="/html/portlet/afiliados/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional" value="<%=String.valueOf(seccionalFijada) %>" />
					<liferay-util:param name="seccional" value="<%=seccionalString %>" />
					<liferay-util:param name="esEdicion" value="false" />
				</liferay-util:include></td>
		</tr>
	</table>
	</br>
</fieldset>
</br>

<%-- <% if(idSecSugerido > 0) {%>

<script type="text/javascript" >

jQuery('#<portlet:namespace />id_seccional').val('<%=idSecSugerido%>');
jQuery("#<portlet:namespace />seccional").val("abcde");
jQuery("#<portlet:namespace />secc_seleccionada").val("1");
var popupSecc;
<portlet:namespace />buscarSeccional();	
if(popupSecc){		
	Liferay.Popup.close(popupSecc);
}

</script>

<% } %> --%>

<form action="" method="post" name="<portlet:namespace />fm">	
	<input type="hidden" name="tabs_a_mostrar" value="<%=tabsAMostrar%>"/>
	<input name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%= accion %>" />
		

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="datos-afiliado" />
	</legend>
	</br>
	<table class="lfr-table">
		<tr>
			<td>
			<input name="<portlet:namespace />tipo_novedad" id="<portlet:namespace />tipo_novedad" type="hidden" 
							value="<%=tipo_nov  %>" />
			<label><liferay-ui:message key="codigo" /></label></td>
			<td><input id="<portlet:namespace />id"
				name="<portlet:namespace />id" type="text"
				value='<%= afiliado != null ? afiliado.getId() : "" %>'
				readonly='readonly' /></td>
			<td><label><liferay-ui:message key="cuil-titular" /></label></td>
			<td><input id="<portlet:namespace />cuil_titular"
				name="<portlet:namespace />cuil_titular" size="13" maxlength="11"
				type="text"
				value='<%= afiliado != null ? afiliado.getCuil_titular() : "" %>'
				<% if (afiliado != null || !esTitular) { %> readonly='readonly'
				onfocus='moveFocus();' <%} else { %>
				onblur='javascript:<portlet:namespace />validarExisteCuil("cuil_titular");<portlet:namespace />proponerDNI();<portlet:namespace />completeCuil();'
				<% } %> /></td>
			<td><label><liferay-ui:message key="integrante" />:</label></td>
			<td><input id="<portlet:namespace />inte"
				name="<portlet:namespace />inte" size="2" maxlength="2" type="text"
				value="<%= afiliado != null ? String.valueOf(afiliado.getInte()) : "" %>"
				onfocus='moveFocus();' /></td>
			<td><label><liferay-ui:message key="vigente-desde" />:</label></td>
			<td colspan="2"><liferay-ui:input-date
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
			<td colspan="1"><label><liferay-ui:message key="nacionalidad" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>nacionalidad">
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
				onblur="javascript:<portlet:namespace />buscarAfilExistente(event);">
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
				value="<%= afiliado != null ? afiliado.getDocumento_numero() : "" %>"
				onblur="javascript:<portlet:namespace />buscarAfilExistente(event);"
				onkeydown="allowOnlyDigits(event);" />
				<div id='<portlet:namespace />nroDocAfilExistente'
					style="float: inherit;"></div></td>
			<td colspan="1"><label><liferay-ui:message key="cuil" />:</label></td>
			<td><input id="<portlet:namespace />cuil"
				name="<portlet:namespace />cuil" size="13" maxlength="11"
				type="text"
				value="<%= afiliado != null ? afiliado.getCuil() : "" %>"
				onblur="javascript:<portlet:namespace />validarExisteCuil('cuil');"/></td>
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
						<%= afiliado != null && afiliado.getId_estado_civil_sss()==estadoCivil.getCodigo() ? "selected" : 
							afiliado == null && WebKeysAfiliados.ESTADO_CIVIL_DEFAULT == estadoCivil.getCodigo() ? "selected" : "" %>
						value="<%= estadoCivil.getCodigo() %>"><%=estadoCivil.getDescripcion()%></option>
					<%	}	%>
			</select></td>
			<td colspan="1"><label><liferay-ui:message key="parentesco" />:</label></td>
			<td colspan="3"><select name="<portlet:namespace/>parentesco" id="<portlet:namespace/>parentesco"
					<%	for (Parentesco parentesco : parentescos) { %>
					
						<% if(parentesco.getCodigo() != WebKeysAfiliados.PARENTESCO_DEFAULT){ %> <!-- sacamos al titular de las opciones -->
							<option value="<%= parentesco.getCodigo() %>" 
							<%= afiliado != null && afiliado.getId_parentesco_sss() == parentesco.getCodigo() ? "selected" : ""  %> ><%=parentesco.getDescripcion()%></option>
						<% }%>	
					<%	} %>
			</select></td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="discapacitado" />:</label></td>
			<td colspan="1"><select name="<portlet:namespace/>discapacitado">
					<option value="0"
						<%= afiliado != null && null!= afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("0") ? "selected" : ""  %>>No</option>
					<option value="1"
						<%= afiliado != null && null!= afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("1") ? "selected" : ""  %>>Si</option>
			</select></td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
	</table>
</fieldset>

<br />

<%if(!esTitular) {%>
<script type="text/javascript">
	jQuery("#<portlet:namespace />cuil_titular").val("<%=cuil_titular%>");
</script>
<%} %>

			
<input type="button" value="<liferay-ui:message key="save" />"
	onClick="<portlet:namespace />savePreAfiliadoIntegrante();" />

&nbsp;&nbsp;
<input type="button" value="<liferay-ui:message key="alta-afiliado" />" 
	onClick="<portlet:namespace />altaPreAfiliado();" />
<%--	
<c:if test="<%= afiliado != null %>">
 	<input type="button"
		value="<liferay-ui:message key="documentacion-adjunta" />"
		onClick="<portlet:namespace />documentacionAdjunta( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" /> 
	<input type="button"
		value="<liferay-ui:message key="cargar-integrante" />"
		onClick="<portlet:namespace />cargarIntegrante();" />
	 <c:if test="<%= afiliado.getTiene_imagen() == 1 %>">
		<input type="button" value="<liferay-ui:message key="ver-imagenes" />"
			onClick="<portlet:namespace />verImagenDirecta();" />
	</c:if>
	<c:if test='<%= null != afiliado.getDiscapacitado() && afiliado.getDiscapacitado().equals("1") %>'>
		<input type="button" value="Detalle Discapacidad"
			onClick="<portlet:namespace />detalleDiscapacidad( <%=afiliado.getCuil_titular()%>, <%=afiliado.getInte() %>);" />
	</c:if> 
</c:if> 
<br /> --%>
<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">		
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />apellido);		
		</script>
</c:if> 

</form>

<script type="text/javascript">
	
	<%-- function <portlet:namespace />validarExisteCuil(cuil){
		if(cuil=="cuil"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil').val();
		}else if(cuil=="cuil_titular"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil_titular').val();
		}		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_existe_pre_afiliado&cuil='+cuil_final;
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.validado=="1"){
					alert("<liferay-ui:message key='cuil-invalido'/>");
				}else if(obj.validado=="2"){
					alert("<liferay-ui:message key='cuil-titular-existente'/>");
				}else if(obj.validado=="3"){
					alert("<liferay-ui:message key='cuil-preafi-existente'/>");
				} 	
			}				                                                                                                                                                                                                                                                            
			
		});
	} --%>
	function filtrarLocalidad() {
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_provincia_localidad&idProvincia='+idProvincia;
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
				jQuery('#<portlet:namespace />cod_area_telefono').val(obj.codAreaTel);
				jQuery('#<portlet:namespace />cod_area_celular').val(obj.codAreaTel);
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
	}

	function <portlet:namespace />validarOtrosCampos() {

		var cod_parentesco=parseInt(jQuery("#<portlet:namespace/>parentesco").val());
		var cod_estado_civil=parseInt(jQuery("#<portlet:namespace/>estado_civil").val());
		
		if ((cod_parentesco==1 || cod_parentesco==2) && cod_estado_civil==1) {
			alert("<liferay-ui:message key='the-conyuge-no-puede-ser-soltero' />");	
			return false;	
		} 	if ((cod_parentesco==3||cod_parentesco==4||cod_parentesco==5||cod_parentesco==6||cod_parentesco==7||cod_parentesco==8)&&(cod_estado_civil==2)) {
			alert("<liferay-ui:message key='the-hijo-no-puede-ser-casado' />");
			return false;	
		}
	}

</script>