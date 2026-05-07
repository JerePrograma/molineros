<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>

<%
Prestador prestador  = (Prestador)request.getSession().getAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION); 

PrestadorLugarAtencion lugarAtencion = (PrestadorLugarAtencion)request.getSession().getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
Domicilio domicilio = lugarAtencion!=null?lugarAtencion.getDomicilio():null;

PrestadorLugarAtencion lugarAtIndirecto = (PrestadorLugarAtencion)request.getSession().getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION);

Calendar habDesde = Calendar.getInstance();
Calendar habHasta = Calendar.getInstance();

if(lugarAtencion != null && lugarAtencion.getVigenciaDesdeHabilitacion() != null){
	habDesde.setTime(lugarAtencion.getVigenciaDesdeHabilitacion()); 
}
if(lugarAtencion != null && lugarAtencion.getVigenciaHastaHabilitacion() != null){
	habHasta.setTime(lugarAtencion.getVigenciaHastaHabilitacion()); 
}

String cmd = (String) request.getAttribute(Constants.CMD);

boolean esEdicion = false;
boolean esEdicionLugarAt = true;

boolean esNuevoLugarAt = false;

if (prestador == null  ||
   (  cmd!=null  && cmd.length() > 0  && !request.getAttribute(Constants.CMD).equals(Constants.VIEW)   ) ) {
	esEdicion = true;
}

if(lugarAtIndirecto != null){
	esEdicionLugarAt = false;
}

String tabValue = ParamUtil.getString(request, "tab", null); 
String idPrestadorAtencion = "";
if(lugarAtencion!=null && lugarAtencion.getIdPrestadorAtencion() != null && lugarAtencion.getIdPrestadorAtencion() != 0){
	idPrestadorAtencion = String.valueOf(lugarAtencion.getIdPrestadorAtencion());
}

String domicilioAfip = (String) request.getAttribute(WebKeysLiquidaciones.DOMICILIO_AFIP_PRESTADOR_EN_EDICION);

boolean showABMButtons = PermissionUtil.userContainsRole(user,"ABM_PRESTADOR");

%>
<portlet:defineObjects />

<form action="ListaLugarAtencionAction" name="<portlet:namespace />prestador_lugarat_fm"
		 id="<portlet:namespace />prestador_lugarat_fm" method="post" >
 	<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
 	<input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
 	<input type="hidden" name="<portlet:namespace />id_prestador_prestador"  value="<%=prestador!=null?prestador.getId_prestador():0%>" />
	
<%if(domicilioAfip != null){ %>
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="domicilio-afip" />
	</legend>
	<p><b><%=domicilioAfip%></b></p>
</fieldset>	
<%}%>	
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="lugar-atencion-prestador" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">		
		<tr>
			<td colspan="4">
				<div id="<portlet:namespace />lista_lugares_atencion">
					<jsp:include page='/html/portlet/prestadores/lista_lugares_atencion_prestador.jsp' />
				</div>
			</td>
		</tr>
		<tr><td colspan="4">* Seleccione un lugar de atención para ver el detalle</td></tr>	
	</table>			
</fieldset>	
	
<br/>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="home-address" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<%if(domicilio!=null){ %>
	    <input name="<portlet:namespace />id_domicilio" id="<portlet:namespace />id_domicilio" 
	    	type="hidden" value="<%=domicilio.getId_domicilio() %>" />
	    <%} %>
	    <tr>
	    	<td style="width: 85px"><label><liferay-ui:message key="tipo-factura-prest" />:</label></td>
	    	<td colspan="1" style="width: 210px"><select id="<portlet:namespace/>lugarat_factura"
				name="<portlet:namespace/>lugarat_factura" <% if (!esEdicion || !esEdicionLugarAt) { %> <%="disabled='disabled'" %> <%} %>
				onchange="javascript:mostrarIndirecto();">
					<option
						<%=lugarAtencion != null && lugarAtencion.getFactura().equalsIgnoreCase("DIRECTO") ? "selected" : ""%>
						value="DIRECTO">DIRECTO</option>
					<option
						<%=lugarAtencion != null && lugarAtencion.getFactura().equalsIgnoreCase("INDIRECTO") ? "selected" : ""%>
						value="INDIRECTO">INDIRECTO</option>	
			</select></td>
		    <td ><label><liferay-ui:message key="nombre-lugarat" />:</label></td>
	    	<td colspan="1"><input id="<portlet:namespace />lugarat_nombre"
				name="<portlet:namespace />lugarat_nombre"  size="50" maxlength="250"
				type="text"
				value="<%= lugarAtencion != null ? lugarAtencion.getNombre() : "" %>"
				<% if (!esEdicion || !esEdicionLugarAt) { %> readonly="readonly" <%} %>/>
			<%-- <td colspan="4">
				<div id="<portlet:namespace />divBuscarPrestador" name="<portlet:namespace />divBuscarPrestador">										
					<liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador_lugar_at.jsp">
							<liferay-util:param name="search_url" value="/prestadores/buscar_prestador" />
							<liferay-util:param name="cuit_prestador" value='' />
							<liferay-util:param name="nombre_prestador" value='' />
							<liferay-util:param name="id_prestador" value='<%=idPrestadorAtencion%>' />
							<liferay-util:param name="esEditable" value='<%=String.valueOf(true)%>' />
					</liferay-util:include>
				</div>	
			</td> --%>
			<td colspan="4">&nbsp;</td>
			<td>&nbsp;</td>	
	    </tr>
	    <tr>
	    	<td colspan="8">
				<div id="<portlet:namespace />divBuscarPrestador" name="<portlet:namespace />divBuscarPrestador">										
					<liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador_lugar_at.jsp">
							<liferay-util:param name="search_url" value="/prestadores/buscar_prestador" />
							<liferay-util:param name="cuit_prestador" value='' />
							<liferay-util:param name="nombre_prestador" value='' />
							<liferay-util:param name="id_prestador" value='<%=idPrestadorAtencion%>' />
							<liferay-util:param name="esEditable" value='<%=String.valueOf(true)%>' />
					</liferay-util:include>
				</div>
			</td>	
	    </tr>
	    <tr>
	    	<td><label><liferay-ui:message key="nro-hab-lugarat" />:</label></td>
	    	<td colspan="1"><input id="<portlet:namespace />lugarat_nro_habilitacion"
				name="<portlet:namespace />lugarat_nro_habilitacion" size="5" maxlength="8"
				type="text" onkeydown="allowOnlyDigits(event);"
				value="<%= lugarAtencion != null ? lugarAtencion.getNumeroHabilitacion() : "" %>"
				<% if (!esEdicion || !esEdicionLugarAt) { %> readonly="readonly" <%} %>/>
			<td><label><liferay-ui:message key="aut-hab-lugarat" />:</label></td>
	    	<td colspan="1"><input id="<portlet:namespace />lugarat_aut_habilitacion"
				name="<portlet:namespace />lugarat_aut_habilitacion" size="50" maxlength="250"
				type="text"
				value="<%= lugarAtencion != null ? lugarAtencion.getAutoridadHabilitacion() : "" %>"
				<% if (!esEdicion || !esEdicionLugarAt) { %> readonly="readonly" <%} %>/>	
	   		</td>
	   		<td colspan="4">&nbsp;</td>
	   	</tr>
	   	<tr>	
	   		<td><label><liferay-ui:message key="vigente-desde" />:</label></td>
	   		<td><liferay-ui:input-date
					dayParam="laVigenteDesdeFechaDia"
					dayValue="<%= habDesde.get(Calendar.DATE)%>"
					dayNullable="<%=true %>"
					monthNullable="<%= true %>"
					monthParam="laVigenteDesdeFechaMes"
					monthValue="<%= habDesde.get(Calendar.MONTH) %>"
					yearParam="laVigenteDesdeFechaAnio"
					yearValue="<%= habDesde.get(Calendar.YEAR) %>"
					yearRangeStart="<%= habDesde.get(Calendar.YEAR) - 20 %>"
					yearRangeEnd="<%= habDesde.get(Calendar.YEAR) + 120 %>"
					yearNullable="<%= true %>"
					firstDayOfWeek="<%= habDesde.getFirstDayOfWeek()%>"
					disabled="<%= !esEdicion || !esEdicionLugarAt %>" /></td>
	   		<td><label><liferay-ui:message key="vigente-hasta" />:</label></td>
	   		<td><liferay-ui:input-date
	   				dayNullable="<%= true %>"
					dayParam="laVigenteHastaFechaDia"
					dayValue="<%= habHasta.get(Calendar.DATE)%>"
					monthNullable="<%= true %>"
					monthParam="laVigenteHastaFechaMes"
					monthValue="<%= habHasta.get(Calendar.MONTH) %>"
					yearNullable="<%= true %>"
					yearParam="laVigenteHastaFechaAnio"
					yearValue="<%= habHasta.get(Calendar.YEAR) %>"
					yearRangeStart="<%= habHasta.get(Calendar.YEAR) - 20 %>"
					yearRangeEnd="<%= habHasta.get(Calendar.YEAR) + 120 %>"
					firstDayOfWeek="<%= habHasta.getFirstDayOfWeek()%>"
					disabled="<%= !esEdicion || !esEdicionLugarAt %>" /></td>
			<td colspan="2"><label><liferay-ui:message key="pres-copia-hab-lugarat" />:</label><input type="checkbox" 
						name="<portlet:namespace />lugarat_pres_copia_habilitacion"
						id="<portlet:namespace />lugarat_pres_copia_habilitacion" ></td>
			<td colspan="2">&nbsp;</td>				
	   	</tr>	
	   	
		<tr>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td colspan="1"><select id="<portlet:namespace/>provincia"
				name="<portlet:namespace/>provincia" <% if (!esEdicion || !esEdicionLugarAt) { %> <%="disabled='disabled'" %> <%} %>
				onchange="javascript:filtrarLocalidad();">
					<%	for (Provincia provincia : provincias) { %>
					<option
						<%=domicilio != null && domicilio.getProvinciaId() == provincia.getId() ? "selected" : ""%>
						<%=domicilio == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>
			</select></td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
			
			<td> 
            <div class="selector-localidad">
			   <%if(lugarAtencion != null) {%>
			    <select id="<portlet:namespace/>localidad"
				  name="<portlet:namespace/>localidad" <% if (!esEdicion || !esEdicionLugarAt) { %>
				  disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();">
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option	
					  <%=domicilio != null && domicilio.getLocalidadId() == localidad.getId() ? "selected" : ""%>
					 value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%>
			    </select>
			  <%} else{%>
			  	<select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <% if (!esEdicion || !esEdicionLugarAt) { %>
				disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();">
					<option selected value="0">Seleccione una localidad</option>
				 </select>	
			  <%} %>		
			 </div>			
			</td>
			
			
			
			<td><label><liferay-ui:message key="calle" />:</label></td>
			<td colspan="1" style="vertical-align: top">
				<liferay-util:include page='/html/portlet/prestadores/busqueda_calle.jsp'>
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEdicion && esEdicionLugarAt)%>" />
				</liferay-util:include></td>
			<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />numero"
				name="<portlet:namespace />numero" size="5" maxlength="5"
				type="text"
				value="<%= domicilio != null ? domicilio.getNumero() : "" %>"
				<% if (!esEdicion || !esEdicionLugarAt) { %> readonly="readonly" <%} %>
				onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" />
			</td>
		</tr>
		<tr>
			<div id='divCodPostal' style="float: right;"></div>
			<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />piso"
				name="<portlet:namespace />piso" size="5" maxlength="2" type="text"
				value="<%= domicilio != null ? domicilio.getPiso() : "" %>"
				<% if (!esEdicion || !esEdicionLugarAt) { %> readonly="readonly" <%} %> /></td>
			<td colspan="1"><label><liferay-ui:message key="departamento" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />dpto"
				name="<portlet:namespace />dpto" size="5" maxlength="4" type="text"
				value="<%= domicilio != null ? domicilio.getDepto() : "" %>"
				<% if (!esEdicion || !esEdicionLugarAt) { %> readonly="readonly" <%} %> /></td>
				
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />cod_postal"
				name="<portlet:namespace />cod_postal" size="5" maxlength="4"
				type="text" value="<%= domicilio != null ? domicilio.getPostal_codi() : "" %>"
				<% if (!esEdicion || !esEdicionLugarAt) { %> readonly="readonly" <%} %>></td>
			<td colspan="1"><label><liferay-ui:message key="barrio" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />barrio"
				name="<portlet:namespace />barrio" size="12" maxlength="50"
				type="text" value="<%= domicilio != null ? domicilio.getBarrio() : "" %>"
				<% if (!esEdicion || !esEdicionLugarAt) { %> readonly="readonly" <%} %> /></td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="cat-prof" />:</label></td>
			<td><select <% if (!esEdicion || !esEdicionLugarAt) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>cat_prof" id="<portlet:namespace/>cat_prof">
					<option value="" <%if(lugarAtencion!=null && lugarAtencion.getCategoriaProfesional().equals("")){ %> selected="selected" <%} %>></option>
					<option value="X" <%if(lugarAtencion!=null && lugarAtencion.getCategoriaProfesional().equals("X")){ %> selected="selected" <%} %> >X</option>
			</select></td>
			<td><label><liferay-ui:message key="reg-histo-clinica" />:</label></td>
			<td><select <% if (!esEdicion || !esEdicionLugarAt) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>reg_histo_clinica" id="<portlet:namespace/>reg_histo_clinica">
					<option value=""></option>
					<option value="WEB" <%if(lugarAtencion!=null && lugarAtencion.getRegistroHistoriaClinica().equals("WEB")){ %> selected="selected" <%} %>>WEB</option>
					<option value="SOPORTE_MAGNETICO" <%if(lugarAtencion!=null && lugarAtencion.getRegistroHistoriaClinica().equals("SOPORTE_MAGNETICO")){ %> selected="selected" <%} %>>SOPORTE MAGNÉTICO</option>
			</select></td>
			<td colspan="6">&nbsp;</td>
		</tr>	
	</table>	
</fieldset>
<br/>
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="lugar-atencion-contactos-telefonos" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>
			<td colspan="1">
				<select id="<portlet:namespace/>tipo_telefono" 
					  name="<portlet:namespace/>tipo_telefono" 
					  <% if (!esEdicion) { %> <%="disabled='disabled'" %> <%} %> >
					<option value="P">PARTICULAR</option>
					<option value="C">CELULAR</option>
					<option value="F">FAX</option>
				</select>
			</td>
			<td><label><liferay-ui:message key="telefono" /></label></td>
			<td colspan="1">+<input id="<portlet:namespace />telefono_pais"
				name="<portlet:namespace />telefono_pais" size="3" maxlength="3"
				type="text" onkeydown="allowOnlyDigits(event);" value="54"
				<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> -(<input
				id="<portlet:namespace />telefono_area"
				name="<portlet:namespace />telefono_area" size="4" maxlength="4"
				type="text" onkeydown="allowOnlyDigits(event);" value="011"
				<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />)- <input
				id="<portlet:namespace />telefono_numero"
				name="<portlet:namespace />telefono_numero" size="15"
				maxlength="15" type="text" onkeydown="allowOnlyDigits(event);" value=""
				<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />
				&nbsp;Ext.&nbsp; <input id="<portlet:namespace />telefono_ext"
				name="<portlet:namespace />telefono_ext" size="4" maxlength="4"
				type="text" onkeydown="allowOnlyDigits(event);"
				value=""
				<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> <input
				type="hidden" name="<portlet:namespace />telefono_id" value="0" />
			</td>
			<td><label><liferay-ui:message key="observaciones" /></label></td>
			<%-- <td><input type="text" name="<portlet:namespace />telefono_obs" 
			    id="<portlet:namespace />telefono_obs" value=""
				<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> </td>	 --%>
			<td><textarea rows="3" cols="50"
					id="<portlet:namespace />telefono_obs"
					name="<portlet:namespace />telefono_obs" <% if (!esEdicion) { %>
					<%="readonly='readonly'" %> <%}%>></textarea>
			</td>		
			<td align="right">
			    <input type="button"
			           value="Agregar Teléfono"
			           <% if (!esEdicion) { %> disabled="disabled" <% } %>
			           onClick="<portlet:namespace />agregarLugarAtTelefono();" />
			</td>
		</tr>
		<tr>
			<td colspan="5">
				<div id="<portlet:namespace />lista_telefonos">
					<liferay-util:include page="/html/portlet/prestadores/lista_telefonos_prestador_lugar_atencion.jsp">
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>			
</fieldset>

<br/>

<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="lugar-atencion-contactos-electronicos" />
	</legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>
			<td colspan="1">
				<select id="<portlet:namespace/>tipo_contacto" 
				        name="<portlet:namespace/>tipo_contacto" 
				        <% if (!esEdicion) { %> <%="disabled='disabled'" %> <%}%>  >
					<option value="<%=ContactoElectronico.Tipo.EMAIL%>">CORREO ELECTRONICO</option>
					<option value="<%=ContactoElectronico.Tipo.SITIOWEB%>">SITIO WEB</option>
					<option value="<%=ContactoElectronico.Tipo.FAX%>">FACTURA</option>
				</select>
			</td>
			<td><label><liferay-ui:message key="contacto-e" /></label></td>
			<td colspan="1"><input
				id="<portlet:namespace />contactoe_descripcion"
				name="<portlet:namespace />contactoe_descripcion" size="75"
				maxlength="200" type="text"
				value=""
				<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />
			</td>
			<td><label><liferay-ui:message key="observaciones" /></label></td>
			<%-- <td><input type="text" name="<portlet:namespace />contactoe_obs"
			    id="<portlet:namespace />contactoe_obs"
				value="" />
				<input type="hidden" name="<portlet:namespace />contactoe_id" 
					   value="" 
						<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%>/> </td>  --%>
			<td><textarea rows="3" cols="50"
					id="<portlet:namespace />contactoe_obs"
					name="<portlet:namespace />contactoe_obs" <% if (!esEdicion) { %>
					<%="readonly='readonly'" %> <%}%>></textarea>
				<input type="hidden" name="<portlet:namespace />contactoe_id"  value="" />					
			</td>
			<td align="right">
				<input type="button" value="Agregar Contacto Elect." <% if(!esEdicion){%> disabled="disabled" <% } %>
					onClick="<portlet:namespace />agregarLugarAtContactoE();"></td>
		</tr>
		<tr>
			<td colspan="5">
				<div id="<portlet:namespace />lista_contactoes">
						<liferay-util:include page="/html/portlet/prestadores/lista_contactoes_prestador_lugar_atencion.jsp">
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
</fieldset>	
<br/>	
<table>
	<tr>
<% if(showABMButtons) { %>	
<%if(cmd.equalsIgnoreCase(Constants.ADD) || lugarAtencion == null) {%>
	<td colspan="1">
		<input type="button" value="<liferay-ui:message key="save-lugarat" />" <% if(!esEdicion){%> disabled="disabled" <% } %>
			onClick="<portlet:namespace />saveLugarAtencionCompleto();return false;" />
	</td>
	<%}
	if(cmd.equalsIgnoreCase(Constants.EDIT) && lugarAtencion != null) {%>
	<td colspan="1">
		<input type="button" value="<liferay-ui:message key="update-lugarat" />" <% if(!esEdicion){%> disabled="disabled" <% } %>
			onClick="<portlet:namespace />updateLugarAtencionCompleto();return false;" />
	</td>
<%} %>
<%}%>

	<%if(esEdicion){%>
	<td>&nbsp;</td>
	<td>
		<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" 
			   title="<liferay-ui:message key="limpiar-campos" />" type="button" 
			   onClick='javascript:<portlet:namespace />limpiarCamposLugarAt()'/>
	</td>
	<%} %>
	</tr>
</table>			
<% if (esEdicion) { %>
<br />
<div align="left" style="vertical-align: bottom;" >
<input type="button" value="<liferay-ui:message key="back" />"
	onClick="<portlet:namespace />anteriorSolapa();" />
&nbsp;&nbsp;

<%if(showABMButtons) { %>
<input type="submit" value="<liferay-ui:message key="save-prestador" />"
	onClick="<portlet:namespace />savePrestador();return false;" />
<%}%>	
</div>
<%} %>

<div id='validarExistenciaCuit' style="float: right;"></div>

</form>

<% if(lugarAtencion != null && lugarAtencion.getVigenciaDesdeHabilitacion() == null){ %>
<script>
	jQuery('#<portlet:namespace/>laVigenteDesdeFechaDia').val('');
	jQuery('#<portlet:namespace/>laVigenteDesdeFechaMes').val('');
	jQuery('#<portlet:namespace/>laVigenteDesdeFechaAnio').val('');
	jQuery('#<portlet:namespace/>laVigenteHastaFechaDia').val(0);
	jQuery('#<portlet:namespace/>laVigenteHastaFechaMes').val(-1);
	jQuery('#<portlet:namespace/>laVigenteHastaFechaAnio').val(0);
	</script>
<%} %>	 

<script type="text/javascript">

jQuery("#<portlet:namespace />lugarat_nombre").focus();

<%if(lugarAtencion!=null && lugarAtencion.isPresentaCopiaHabilitacion()) {%>
jQuery("#<portlet:namespace/>lugarat_pres_copia_habilitacion").attr('checked', 'checked');  
<%} %>


	function validarExistencia(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode				
		var cuit = jQuery("#<portlet:namespace />cuit").val();
		
		if (cuit.length > 0) {									
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/buscar_cuit_existente&cuit='+cuit;
			jQuery("#validarExistenciaCuit").load(url);		
			jQuery("#validarExistenciaCuit").show();				
		}
	}

	function <portlet:namespace />savePrestador() {
		
		if(<%=lugarAtencion != null %> ){
			if (!confirm("Esta modificando un Lugar de Atención, se perderán su cambios recientes. ¿Desea continuar de todos modos?")){
				return false;
			}
		} 

<%-- 		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/prestadores/editar_prestadores_entry';
		<% if(prestador==null || (prestador != null && prestador.getId_prestador() <= 0 ) ) {%>
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.ADD%>';
		<% }else{ %>
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.UPDATE%>';
		<% } %> --%>
		
		var xportletUrl = '/prestadores/editar_prestadores_entry';
		var cmdA = '<%=Constants.ADD%>';
		var cmdU = '<%=Constants.UPDATE%>';
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		<% if(prestador==null || (prestador != null && prestador.getId_prestador() <= 0 ) ) {%>
		'<liferay-portlet:param name="cmd" value="__cmdA" />'+
		<% }else{ %>
		'<liferay-portlet:param name="cmd" value="__cmdU" />'+
		<% } %> 
	    '</liferay-portlet:renderURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__cmdA", cmdA);
  	    url = url.replace("__cmdU", cmdU);

		
		document.<portlet:namespace />prestador_lugarat_fm.action = url;
 		submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
	}     

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	function filtrarLocalidad() {
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/prestadores/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery.ajax({   
			url: url,
			async: false,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;						
				var obj = jQuery.parseJSON(data);
				jQuery('.selector-localidad select').html(data).fadeIn();
				/*
				addElementToSelect("<portlet:namespace/>localidad", "Seleccione una localidad", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>localidad", text, value);					
				}
				*/
			}
		});
	}
	
	function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/prestadores/id_localidad_codpostal&idLocalidad='+idLocalidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
	}
	
	function <portlet:namespace />buscarCodPostalOnDiv(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad").val() == "265" && jQuery("#<portlet:namespace />calle").val() != "" && jQuery("#<portlet:namespace />numero").val() > 0) {
			var calle = jQuery("#<portlet:namespace />calle").val();
			var numero = jQuery("#<portlet:namespace />numero").val();
			if (calle.length > 0 && numero > 0) {				
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
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
	
	function <portlet:namespace />agregarLugarAtTelefono(){
		
		var tipoTel =jQuery('#<portlet:namespace/>tipo_telefono').val();
		var codPais =jQuery('#<portlet:namespace/>telefono_pais').val(); 
		var codArea =jQuery('#<portlet:namespace/>telefono_area').val();
		var numero  =jQuery('#<portlet:namespace/>telefono_numero').val();	
		var exten   =jQuery('#<portlet:namespace/>telefono_ext').val();
		var obs   =jQuery('#<portlet:namespace/>telefono_obs').val();
		var idTel =jQuery('#<portlet:namespace/>telefono_id').val();
	    var propio = "P";
	    if(jQuery('#<portlet:namespace/>lugarat_factura').val() == 'DIRECTO' ){
	    	propio = "D";
	    }
		if(<portlet:namespace />validaLugarAtTelefono()){
			<%-- var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/lista_telefonos_lugar_at_prestador';
			url = url+'&idTelefono='+idTel+
			'&tipoTel='+tipoTel+
			'&codPais='+codPais+
			'&codArea='+codArea+
			'&numero='+numero+
			'&exten='+exten+
			'&obs='+encodeURI(obs)+
			'&propio='+propio; --%>
			
			var xportletUrl = '/prestadores/lista_telefonos_lugar_at_prestador';
			
			var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="idTelefono" value="__idTelefono"/>'+
			'<liferay-portlet:param name="tipoTel" value="__tipoTel"/>'+
			'<liferay-portlet:param name="codPais" value="__codPais"/>'+
			'<liferay-portlet:param name="codArea" value="__codArea"/>'+
			'<liferay-portlet:param name="numero" value="__numero"/>'+
			'<liferay-portlet:param name="exten" value="__exten"/>'+
			'<liferay-portlet:param name="obs" value="__obs"/>'+
			'<liferay-portlet:param name="propio" value="__propio"/>'+
		    '</liferay-portlet:renderURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__idTelefono", idTel);
	  	    url = url.replace("__tipoTel", tipoTel);
	  	    url = url.replace("__codPais", codPais);
	  	    url = url.replace("__codArea", codArea);
	  	    url = url.replace("__numero", numero);
	  	    url = url.replace("__exten", exten);
	  	    url = url.replace("__obs", encodeURI(obs));
	  	    url = url.replace("__propio", propio);
	  	  
	  	  
	  	    
			jQuery("#<portlet:namespace />lista_telefonos").load(url); 
			
			/* Limpiamos campos */
			jQuery('#<portlet:namespace/>telefono_pais').val('54'); 
			jQuery('#<portlet:namespace/>telefono_area').val('011');
			jQuery('#<portlet:namespace/>telefono_numero').val('');	
			jQuery('#<portlet:namespace/>telefono_ext').val('');
			jQuery('#<portlet:namespace/>telefono_obs').val('');
		}
		
	}
	
	function <portlet:namespace />validaLugarAtTelefono(){
	
		if (trim(jQuery('#<portlet:namespace />telefono_pais').val()) == '' ||
			trim(jQuery('#<portlet:namespace />telefono_area').val()) == '' ||
			trim(jQuery('#<portlet:namespace />telefono_numero').val()) == ''){
				alert("El teléfono debe necesariamente tener el código de país, de area y el número");
				jQuery('#<portlet:namespace />telefono_numero').focus();
				return false;
		}
		return true;
	}	
	
	function <portlet:namespace />agregarLugarAtContactoE(){
		
		var tipoContE =jQuery('#<portlet:namespace/>tipo_contacto').val();
		var descripcion =jQuery('#<portlet:namespace/>contactoe_descripcion').val(); 
		var obs   =jQuery('#<portlet:namespace/>contactoe_obs').val();
		var idContE =jQuery('#<portlet:namespace/>contactoe_id').val();
		var propio = "P";
	    if(jQuery('#<portlet:namespace/>lugarat_factura').val() == 'DIRECTO' ){
	    	propio = "D";
	    }
		
		if(<portlet:namespace />validaLugarAtContacto()){
			<%-- var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/lista_contactos_lugar_at_prestador';
			url = url+'&idContactoE='+idContE+
			'&tipoContacto='+tipoContE+
			'&descripcion='+descripcion+
			'&obs='+encodeURI(obs)+
			'&propio='+propio; --%>
			
			var xportletUrl = '/prestadores/lista_contactos_lugar_at_prestador';
			
			var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="idContactoE" value="__idContactoE"/>'+
			'<liferay-portlet:param name="tipoContacto" value="__tipoContacto"/>'+
			'<liferay-portlet:param name="descripcion" value="__descripcion"/>'+
			'<liferay-portlet:param name="obs" value="__obs"/>'+
			'<liferay-portlet:param name="propio" value="__propio"/>'+
		    '</liferay-portlet:renderURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__idContactoE", idContE);
	  	  	url = url.replace("__tipoContacto", tipoContE);
	  	    url = url.replace("__descripcion", descripcion);
	  	    url = url.replace("__propio", propio);
	  	    url = url.replace("__obs", encodeURI(obs));
			
			jQuery("#<portlet:namespace />lista_contactoes").load(url); 
			
			/* Limpiamos campos */
			jQuery('#<portlet:namespace/>contactoe_descripcion').val(''); 
			jQuery('#<portlet:namespace/>contactoe_obs').val('');
		}
		
	}
	
	function <portlet:namespace />validaLugarAtContacto(){
	
		var tipoContE =jQuery('#<portlet:namespace/>tipo_contacto').val();
		
		if(tipoContE == '<%=ContactoElectronico.Tipo.EMAIL%>' || tipoContE == '<%=ContactoElectronico.Tipo.FAX%>'){
			return <portlet:namespace />validarEmail();
		}	
		if(tipoContE == '<%=ContactoElectronico.Tipo.SITIOWEB%>'){
			return <portlet:namespace />validarSitioWeb();
		}		
		return false;
	}
	
	function <portlet:namespace />saveLugarAtencionCompleto(){
	 	if(<portlet:namespace />validaLugarAtDomicilio()){
	 
			<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/prestadores/lista_lugares_atencion_prestador" /></portlet:actionURL>';
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.ADD%>'; --%>
			
			var xportletUrl = '/prestadores/lista_lugares_atencion_prestador';
			var cmd_ = '<%=Constants.ADD%>';
			
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="cmd" value="__cmd"/>'+
		    '</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__cmd", cmd_);
			
			document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
		} 
	}
	
	function <portlet:namespace />validaLugarAtDomicilio(){
		
		if(jQuery('#<portlet:namespace/>lugarat_nombre').val() == ""|| jQuery('#<portlet:namespace/>lugarat_nombre').val()==0){
			alert("<liferay-ui:message key='nombre-lugarat-obligatorio' />");
			jQuery("#<portlet:namespace />lugarat_nombre").focus();
			return false;
		}
		
		if(jQuery('#<portlet:namespace/>provincia').val() == ""|| jQuery('#<portlet:namespace/>provincia').val()==0){
			alert("<liferay-ui:message key='provincia-obligatoria' />");
			jQuery("#<portlet:namespace />provincia").focus();
			return false;
		}
		
		if(jQuery('#<portlet:namespace/>localidad').val() == "" || jQuery('#<portlet:namespace/>localidad').val()==0){
			alert("<liferay-ui:message key='localidad-obligatoria' />");
			jQuery("#<portlet:namespace />localidad").focus();
			return false;
		}
	
		if (trim(jQuery('#<portlet:namespace/>calle').val()).length == 0) {
			alert("<liferay-ui:message key='calle-obligatorio' />");
			jQuery('#<portlet:namespace />calle').focus();
			return false;
		}
		
		if (!isPositiveInteger(trim(jQuery('#<portlet:namespace/>cod_postal').val()))){
			alert("<liferay-ui:message key='codigo-postal-invalido' />");
			jQuery('#<portlet:namespace />cod_postal').focus();
			return false;
		}	
	
		return true;
	}	
	
	function <portlet:namespace />validarEmail() {
		var email = jQuery('#<portlet:namespace/>contactoe_descripcion').val();
	
		if(trim(email).length == 0){
			alert("El campo descripción del Email es Obligatorio");
			jQuery("#<portlet:namespace />contactoe_descripcion").focus();
			return false;
		}
		var expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		
		if (!expr.test(email) ){
		    alert("Error: La dirección de correo " + email + " es incorrecta.");
		    jQuery("#<portlet:namespace />contactoe_descripcion").focus();
			return false;
		}
		return true;
	}
	
	function <portlet:namespace />validarSitioWeb() {
		var sitioWeb = jQuery('#<portlet:namespace/>contactoe_descripcion').val();
	
		if(trim(sitioWeb).length == 0){
			alert("El campo descripción del Sitio WEb es Obligatorio");
			jQuery("#<portlet:namespace />contactoe_descripcion").focus();
			return false;
		}
	
		var regexp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/
		
		/* return regexp.test(sitioWeb); */
		if (!regexp.test(sitioWeb) ){
		    alert("Error: La url " + sitioWeb + " es incorrecta.");
		    jQuery("#<portlet:namespace />contactoe_descripcion").focus();
			return false;
		}
		return true;
	}
	
	function <portlet:namespace />limpiarLugarAtDomicilio(){
		
		jQuery('#<portlet:namespace/>lugarat_factura').val('DIRECTO');
		jQuery('#<portlet:namespace/>lugarat_nombre').val('');
		jQuery('#<portlet:namespace/>lugarat_nro_habilitacion').val('');
		jQuery('#<portlet:namespace/>lugarat_aut_habilitacion').val('');
		jQuery('#<portlet:namespace/>id_prestador').val('');
	/* 	var presCopia = document.getElementById("<portlet:namespace />lugarat_pres_copia_habilitacion");
		var presCopiaHabil = presCopia.checked ? 'true' : 'false'; */
		jQuery('#<portlet:namespace/>provincia').val(0);
		jQuery('#<portlet:namespace/>localidad').val(0);
		jQuery('#<portlet:namespace/>calle').val('');
		jQuery('#<portlet:namespace/>numero').val('');
		jQuery('#<portlet:namespace/>piso').val('');
		jQuery('#<portlet:namespace/>dpto').val('');
		jQuery('#<portlet:namespace/>cod_postal').val('');
		jQuery('#<portlet:namespace/>barrio').val('');
	    jQuery('#<portlet:namespace/>cat_prof').val('');
	    jQuery('#<portlet:namespace/>reg_histo_clinica').val('');
	    jQuery("#<portlet:namespace/>lugarat_pres_copia_habilitacion").attr('checked', ''); //checked
	    
	    /* recargar las listas de telefonos y contactoes desp de agregar un lugar de atencion completo */
	    
	    mostrarIndirecto();
	}    
	
	function mostrarIndirecto() {
		jQuery('#<portlet:namespace />divBuscarPrestador').hide();
		var tipo=jQuery('#<portlet:namespace/>lugarat_factura').val();
		
		if (tipo == 'INDIRECTO') {
			jQuery('#<portlet:namespace />divBuscarPrestador').show();			
		}else{
			jQuery('#<portlet:namespace />divBuscarPrestador').hide();
		}
	}
	mostrarIndirecto();
		
	function <portlet:namespace />updateLugarAtencionCompleto(){
	
	 	if(<portlet:namespace />validaLugarAtDomicilio()){
			
			<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/prestadores/lista_lugares_atencion_prestador" /></portlet:actionURL>';
			url = url + '&<%=Constants.CMD %>='+'<%=Constants.UPDATE%>'; --%>

			var xportletUrl = '/prestadores/lista_lugares_atencion_prestador';
			var cmd_ = '<%=Constants.UPDATE%>';
			
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="cmd" value="__cmd"/>'+
		    '</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__cmd", cmd_);

			document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
		}
	}
	
	function <portlet:namespace />limpiarCamposLugarAt(){
			
		<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/prestadores/lista_lugares_atencion_prestador" /></portlet:actionURL>';
		url = url + '&<%=Constants.CMD %>='+'<%=Constants.RESET%>'; --%>
		var xportletUrl = '/prestadores/lista_lugares_atencion_prestador';
		var cmd_ = '<%=Constants.RESET%>';
		
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="cmd" value="__cmd"/>'+
	    '</liferay-portlet:actionURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__cmd", cmd_);
  	    
		document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
		submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);	
	}
	
	function <portlet:namespace />anteriorSolapa() {		
			var accionEnCurso = document.<portlet:namespace />prestador_lugarat_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />prestador_lugarat_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.MOVE %>';
			
<%-- 		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/prestadores/editar_prestadores_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=plan_prest'; --%>
			
			var xportletUrl = '/prestadores/editar_prestadores_entry';
			var cmd_ = '<%=Constants.MOVE%>';
			
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
			'<liferay-portlet:param name="cmd" value="__cmd"/>'+
			'<liferay-portlet:param name="accionEnCurso" value="__accionEnCurso"/>'+
			'<liferay-portlet:param name="moverATab" value="plan_prest"/>'+
			
		    '</liferay-portlet:actionURL>';
		
		    url = url.replace("__xportletUrl",xportletUrl); 
	  	    url = url.replace("__cmd", cmd_);
	  	    url = url.replace("__accionEnCurso", accionEnCurso); 
	  	    
			document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
			submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
	}
	
	
	if(jQuery('#<portlet:namespace/>provincia').val()!=null){
		var idLocal = jQuery('#<portlet:namespace/>localidad').val();
		filtrarLocalidad();
		jQuery('#<portlet:namespace/>localidad').val(idLocal);
	};
	
</script>