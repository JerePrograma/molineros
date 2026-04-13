<%@ include file="/html/portlet/correspondencia/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0

		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		
		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);		

 		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
		fechaDesde.setTime(new Date());
		
		List<Organization> organizaciones = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
 		Organization orgLocal = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId()).get(0);
		List<Organization> orgAux = new ArrayList<Organization>();
		orgAux.add(orgLocal);
%>

<fieldset class="block-labels"><legend>Correo Interno</legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	<tr>
		<td><label>Fecha Emisión/Recepción Paquete:</label></td>
		<td width="250"><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />
		</td>

		<td><label>Número Correspondencia:</label></td>
		<td><input id="<portlet:namespace />numero_correspondencia"
			name="<portlet:namespace />numero_correspondencia" size="20"
			maxlength="100" type="text" onkeydown="allowOnlyDigits(event);"/></td>
		
		<td><label>Paquete:</label></td>		
		<td><input id="<portlet:namespace />paquete"
			name="<portlet:namespace />paquete" size="20" maxlength="100"
			type="text" onkeydown="allowOnlyDigits(event);"/></td>				
		<td>		
		<input type="hidden" name="<portlet:namespace />tipo_registro"
			id="<portlet:namespace />tipo_registro" value="" />				
		<input type="hidden" name="<portlet:namespace />tipo_envio"
			id="<portlet:namespace />tipo_envio" value=""/>
		<input type="hidden" name="<portlet:namespace />edificio"
			id="<portlet:namespace />edificio" value="" />
		<input type="hidden" name="<portlet:namespace />id_paquete" 
			id="<portlet:namespace />id_paquete" value="" />	
			
		</td>
	</tr>
	<!-- <tr>
		<td colspan="10">&nbsp;</td>
	</tr> -->
	<tr>
		<td><label>Fecha Desempaquetado Desde:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesempDesdeDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesempDesdeMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaDesempDesdeAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />
		</td>
		<td><label>Fecha Desempaquetado Hasta:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesempHastaDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesempHastaMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaDesempHastaAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />
		</td>
	</tr>
	<tr>
		<td><label>Edificio:</label></td>
		<td><select name="<portlet:namespace/>edificio_destino"
			id="<portlet:namespace/>edificio_destino" onchange="javascript:filtrarGrupos();" onclick="javascript:filtrarGrupos();" onfocus="javascript:filtrarGrupos();">
			<option value=""></option>
				<%
					for (Organization org : organizaciones) {
				%>
						<option value="<%= org.getOrganizationId() %>" 
						<%if(org.getOrganizationId()==orgLocal.getOrganizationId()){ %> selected="selected" <%} %> ><%=org.getName() %></option>
				<%
					}
				%>
		</select></td>
				<td><label>Sector:</label></td>
		<td><select name="<portlet:namespace/>sector_destino"
			id="<portlet:namespace/>sector_destino" onchange="javascript:filtrarUsuarios(true,true);" onclick="javascript:filtrarUsuarios(true,true);" >
			<option value="">Seleccione un sector</option>

		</select></td>
		
		<td><label>Usuario:</label></td>
		<td><select name="<portlet:namespace/>usuario_destino"
			id="<portlet:namespace/>usuario_destino">
			<option value="">Seleccione un usuario</option>
	
			</select></td>
	</tr>
<!-- 	<tr>
		<td colspan="10">&nbsp;</td>
	</tr> -->

	<tr>
		<td><label>Tipo Remitente/Destinatario Item:</label></td>
		<td><select name="<portlet:namespace/>tipo_remitente" id="<portlet:namespace/>tipo_remitente" onchange="javascript:<portlet:namespace />mostrarDetalle();">
			<option value=""></option>
			<option value="AFILIADO">Afiliado</option>
			<option value="FARMACIA">Farmacia</option>
			<option value="OTROS">Otros</option>
			<option value="PRESTADOR">Prestador</option>
			<option value="PROVEEDOR">Proveedor</option>
			<option value="SECCIONAL">Seccional</option>
			<option value="SSS">Superintendencia S.S.</option>
			<option value="DRISIDRO">Estudio Dr. Isidro</option>
		</select></td>
	</tr>
<!-- 	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>	 -->
	
	<tr>	
		<td colspan="10">
		<div id="<portlet:namespace />divBuscarAfiliado" name="<portlet:namespace />divBuscarAfiliado">
			<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
			<liferay-util:include page='/html/portlet/liquidaciones/busqueda_afiliado.jsp'>
				<liferay-util:param value="<%= String.valueOf(true) %>" name="edit_mode" />
				<liferay-util:param name="cuil" value='' />
				<liferay-util:param name="inte" value='' />
			</liferay-util:include>
			</fieldset>
						
		</div>
				
		<div id="<portlet:namespace />divBuscarFarmacia" name="<portlet:namespace />divBuscarFarmacia">
			
				<fieldset class="block-labels"><legend>Seleccione Farmacia</legend>
				
				<liferay-util:include page='/html/portlet/uoma/correspondencia/busqueda_farmacia.jsp'>
					<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
					<liferay-util:param value="" name="id_farmacia" />
					<liferay-util:param value="" name="farmacia" />
				</liferay-util:include>
				
				</fieldset>																													
		</div>
		
		<div id="<portlet:namespace />divBuscarOtros" name="<portlet:namespace />divBuscarOtros">		
			<fieldset class="block-labels"><legend>Descripción</legend>	
			<table class="lfr-table">
				<tr>
					<td><label>Otros:</label></td>
					<td><input id="<portlet:namespace />otros"
						name="<portlet:namespace />otros" size="100" maxlength="1000" type="text" /></td>
				</tr>
				</table>
			</fieldset>			
		</div>
		
		<div id="<portlet:namespace />divBuscarPrestador" name="<portlet:namespace />divBuscarPrestador">
															
				<fieldset class="block-labels"><legend>Datos Prestador</legend>				
				<liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
					<liferay-util:param name="search_url" value="/correspondencia/buscar_prestador" />
					<liferay-util:param name="cuit_prestador" value='' />
					<liferay-util:param name="nombre_prestador" value='' />
					<liferay-util:param name="id_prestador" value='' />
					<liferay-util:param name="esEditable" value='<%=String.valueOf(true)%>' />
				</liferay-util:include></fieldset>
										
			<table>
				<tr>
					<td colspan="10">&nbsp;</td>
				</tr>
		
					<tr>
						<td><label><liferay-ui:message key="comprobante" />:</label></td>
						<td colspan="4">
						<select name="<portlet:namespace/>comprobante_tipo" id="<portlet:namespace/>comprobante_tipo">
							<option value=""></option>
							<option value="FCP">FCP</option>
							<option value="NCR">NCR</option>
							<option value="RCB">RCB</option>
						</select> &nbsp;
						<select name="<portlet:namespace/>comprobante_letra" id="<portlet:namespace/>comprobante_letra">
							<option value=""></option>								
							<option value="B">B</option>
							<option value="C">C</option>
							<option value="X">X</option>								
						</select> &nbsp;						
						<input id="<portlet:namespace />sucu"
							name="<portlet:namespace />sucu" size="5" maxlength="6"
							type="text"
							value="" />&nbsp;							
						<input id="<portlet:namespace />comprobante_nro"
							name="<portlet:namespace />comprobante_nro" size="11" maxlength="15"
							type="text"
							value="" />
						</td>
						<td>Importe:</td>
						<td>
							<input id="<portlet:namespace />importe_total" name="<portlet:namespace />importe_total" size="12" maxlength="12" type="text" value="0" 
								onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_total'),event);"/>
						</td>
					</tr>
				</table>						
		</div>		
		<div id="<portlet:namespace />divBuscarProveedor" name="<portlet:namespace />divBuscarProveedor">						
					
			<fieldset class="block-labels"><legend>Datos Proveedor</legend>
					
			<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
		  		<liferay-util:param name="esEditable" value='<%= String.valueOf(true) %>'/>
		  		<liferay-util:param name="cuit" value=''/>
		  		<liferay-util:param name="sucu" value=''/>
		  		<liferay-util:param name="razon" value=''/>
		  		<liferay-util:param name="id_seccional" value=''/>
				<liferay-util:param name="esEmpresaPrestador" value='true'/>		  				  		
		  		<liferay-util:param name="suf" value='_cor'/>
		  		<liferay-util:param name="suf_entidad" value='_ent_cor'/>
			</liferay-util:include>
			</fieldset>
											
		<table>
			<tr>
				<td colspan="10">&nbsp;</td>
			</tr>
	
				<tr>
					<td><label><liferay-ui:message key="comprobante" />:</label></td>
					<td colspan="4">
					<select name="<portlet:namespace/>comprobante_tipo" id="<portlet:namespace/>comprobante_tipo">
						<option value=""></option>
						<option value="FCP">FCP</option>
						<option value="NCR">NCR</option>
						<option value="NDB">NDB</option>
						<option value="RCB">RCB</option>
						<option value="ANT">ANT</option>
						<option value="VAR">VAR</option>
					</select> &nbsp;
					<select name="<portlet:namespace/>comprobante_letra" id="<portlet:namespace/>comprobante_letra">
						<option value=""></option>	
						<option value="A">A</option>
						<option value="B">B</option>
						<option value="C">C</option>
						<option value="M">M</option>
					</select> &nbsp;						
					<input id="<portlet:namespace />sucu"
						name="<portlet:namespace />sucu" size="5" maxlength="6"
						type="text"
						value="" />&nbsp;							
					<input id="<portlet:namespace />comprobante_nro"
						name="<portlet:namespace />comprobante_nro" size="11" maxlength="15"
						type="text"
						value="" />
					</td>
				</tr>
			</table>
																																
		</div>		
		<div id="<portlet:namespace />divBuscarSeccional" name="<portlet:namespace />divBuscarSeccional">
			
			<fieldset class="block-labels"><legend>Datos Seccional</legend>									   
				<jsp:include page="/html/portlet/liquidaciones/busqueda_seccional_reintegro.jsp" />
			</fieldset>
					
		</div>		
				
	</tr>
	
	<!-- <tr>
		<td colspan="10">&nbsp;</td>
	</tr> -->
	
	<c:if test="<%=showABMButtons%>">
		<tr>
			<td align="left" colspan="10">	
					<input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />"
						onClick="<portlet:namespace />buscarPaquetes();"
						type="button" />
				&nbsp;&nbsp;
					<input id="<portlet:namespace />desempaquetar" value="Desempaquetar" 
						title="Desempaquetar"
						onClick="javascript:desempaquetar();"
						type="button" />
				&nbsp;
			</td>
		</tr>
	</c:if>
</table>
</fieldset>
<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando'/></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />busquedaCorrespondenciaDiv">
</div>
</fieldset>

<script type="text/javascript">
<% if(seccionalFijada!=0){ %>
jQuery('#<portlet:namespace/>fechaDesdeDia').val('');
jQuery('#<portlet:namespace/>fechaDesdeMes').val('');
jQuery('#<portlet:namespace/>fechaDesdeAnio').val('');

<%}%>
jQuery('#<portlet:namespace/>fechaDesempDesdeDia').val('');
jQuery('#<portlet:namespace/>fechaDesempDesdeMes').val('');
jQuery('#<portlet:namespace/>fechaDesempDesdeAnio').val('');

jQuery('#<portlet:namespace/>fechaDesempHastaDia').val('');
jQuery('#<portlet:namespace/>fechaDesempHastaMes').val('');
jQuery('#<portlet:namespace/>fechaDesempHastaAnio').val('');

	jQuery('#<portlet:namespace />divBuscarAfiliado').hide();
	jQuery('#<portlet:namespace />divBuscarFarmacia').hide();
	jQuery('#<portlet:namespace />divBuscarOtros').hide();
	jQuery('#<portlet:namespace />divBuscarPrestador').hide();
	jQuery('#<portlet:namespace />divBuscarProveedor').hide();
	jQuery('#<portlet:namespace />divBuscarSeccional').hide();
	
	function <portlet:namespace />mostrarDetalle() {
		jQuery('#<portlet:namespace />divBuscarAfiliado').hide();
		jQuery('#<portlet:namespace />divBuscarFarmacia').hide();
		jQuery('#<portlet:namespace />divBuscarOtros').hide();
		jQuery('#<portlet:namespace />divBuscarPrestador').hide();
		jQuery('#<portlet:namespace />divBuscarProveedor').hide();
		jQuery('#<portlet:namespace />divBuscarSeccional').hide();
		
		var remitente=jQuery('#<portlet:namespace/>tipo_remitente').val();
		
		if (remitente == 'AFILIADO') {
			jQuery('#<portlet:namespace />divBuscarAfiliado').show();			
		}
		if (remitente == 'FARMACIA') {
			jQuery('#<portlet:namespace />divBuscarFarmacia').show();
		}
		if (remitente == 'OTROS' || tipo_remitente == 'PROVEEDOR' || tipo_remitente == 'FARMACIA') {
			jQuery('#<portlet:namespace />divBuscarOtros').show();
		}		
		if (remitente == 'PRESTADOR') {
			jQuery('#<portlet:namespace />divBuscarPrestador').show();
		}		
		if (remitente == 'PROVEEDOR') {
			jQuery('#<portlet:namespace />divBuscarProveedor').show();
		}		
		if (remitente == 'SECCIONAL') {
			jQuery('#<portlet:namespace />divBuscarSeccional').show();
		}				
	}

	jQuery('#<portlet:namespace />buscando').hide();
	
	function desempaquetar() {		
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/correspondencia/borrar_paquete" /></portlet:actionURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);				
	}
	
	function <portlet:namespace />buscarPaquetes(){

		jQuery('#<portlet:namespace />buscando').show();
		var edificio=jQuery('#<portlet:namespace/>edificio').val();
		
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
	    var diaDesempDesde=jQuery('#<portlet:namespace />fechaDesempDesdeDia').val();	    
	    var mesDesempDesde=parseInt(jQuery('#<portlet:namespace />fechaDesempDesdeMes').val())+1;	    
	    var anioDesempDesde=jQuery('#<portlet:namespace />fechaDesempDesdeAnio').val();
	    var fechaDesempDesdeFinal = diaDesempDesde+'/'+mesDesempDesde+'/'+anioDesempDesde;
	    var diaDesempHasta=jQuery('#<portlet:namespace />fechaDesempHastaAnio').val();	    
	    var mesDesempHasta=parseInt(jQuery('#<portlet:namespace />fechaDesempHastaAnio').val())+1;	    
	    var anioDesempHasta=jQuery('#<portlet:namespace />fechaDesempHastaAnio').val();
	    var fechaDesempHastaFinal = diaDesempHasta+'/'+mesDesempHasta+'/'+anioDesempHasta;

	    
	    var numero_correspondencia=jQuery('#<portlet:namespace />numero_correspondencia').val();  	
		var tipo_registro=jQuery('#<portlet:namespace />tipo_registro').val();
		var tipo_envio=jQuery('#<portlet:namespace />tipo_envio').val();
		var paquete=jQuery('#<portlet:namespace />paquete').val();
		var tipo_remitente=jQuery('#<portlet:namespace />tipo_remitente').val();
		var edificio_destino=jQuery('#<portlet:namespace />edificio_destino').val();
		var sector_destino=jQuery('#<portlet:namespace />sector_destino').val();
		var usuario_destino=jQuery('#<portlet:namespace />usuario_destino').val();
				
		try {
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
		}
		catch (err) {
		}

		try {
			var id_farmacia=jQuery('#<portlet:namespace />id_farmacia').val();
		}
		catch (err) {			
		}

		try {
			var otros=jQuery('#<portlet:namespace />otros').val();
		}
		catch (err) {			
		}

		try {
			var id_prestador=jQuery('#<portlet:namespace />id_prestador').val();
		}
		catch (err) {
		}

		try {
			var comprobante_tipo=jQuery('#<portlet:namespace />comprobante_tipo').val();
			var comprobante_letra=jQuery('#<portlet:namespace />comprobante_letra').val();
			var sucu=jQuery('#<portlet:namespace />sucu').val();			
			var comprobante_nro=jQuery('#<portlet:namespace />comprobante_nro').val();
			var importe_total=jQuery('#<portlet:namespace />importe_total').val();
		}
		catch (err) {
		}

		try {
			var cuit_entidad = document.getElementById("<portlet:namespace />cuit_entidad_cor").value;
			var sucursal_entidad = document.getElementById("<portlet:namespace />sucursal_entidad_cor").value;				
		}
		catch (err) {
		}

		try {
			var id_seccional = document.getElementById("<portlet:namespace />id_seccional_r").value;
		}
		catch (err) {			
		}

		/* var total_reg = jQuery('#<portlet:namespace />total_registros').val(); */
		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
		var viene_de = jQuery('#<portlet:namespace />viene_de').val();
		
		var busquedaCorr = { "edificio": edificio, "fechaDesdeFinal": fechaDesdeFinal,
							 "fechaDesempDesdeFinal": fechaDesempDesdeFinal, "fechaDesempHastaFinal": fechaDesempHastaFinal,
							 "numero_correspondencia": numero_correspondencia, "tipo_registro": tipo_registro, 
							 "tipo_envio": tipo_envio, "paquete": paquete, "tipo_remitente": tipo_remitente,
							 "cuil": cuil, "inte": inte, "id_farmacia": id_farmacia,
							 "otros": otros, "id_prestador": id_prestador, "cuit_entidad": cuit_entidad,
							 "sucursal_entidad": sucursal_entidad, "id_seccional": id_seccional,	 
							 "comprobante_tipo": comprobante_tipo, "comprobante_letra": comprobante_letra, "sucu": sucu,
							 "comprobante_nro": comprobante_nro, "importe_total": importe_total,
							 "edificio_destino" : edificio_destino, "sector_destino" : sector_destino, 
							 "usuario_destino" : usuario_destino , "pagina" : offset_reg, "viene_de" : viene_de};

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/correspondencia/buscar_paquetes" /></portlet:renderURL>';
		
        jQuery('#<portlet:namespace />busquedaCorrespondenciaDiv').load(url,busquedaCorr, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}

	<% if (request.getAttribute("paquete_id")!= null){ %>
		alert('Se ha generado exitosamente el paquete número: ' + '<%=request.getAttribute("paquete_id")%>');
	<%}%>

</script>