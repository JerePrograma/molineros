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

<fieldset class="block-labels"><legend>Entradas/Salidas</legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	<tr>
		<td><label>Lugar Emisión/Recepción:</label></td>
		<td><select name="<portlet:namespace/>edificio"
			id="<portlet:namespace/>edificio">
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

		<td><label>Fecha Desde:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesdeDiaBusq"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMesBusq"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnioBusq"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>			
			
		<td><label>Fecha Hasta:</label></td>
		<td><liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>			
	</tr>
	<tr>
		<td><label>Tipo Registro:</label></td>
		<td><select name="<portlet:namespace />tipo_registro"
			id="<portlet:namespace />tipo_registro">
			<option value=""></option>
			<option value="ENTRADA">Entrada</option>
			<option value="SALIDA">Salida</option>
		</select></td>
		<td><label>Tipo Envío:</label></td>
		<td><select name="<portlet:namespace />tipo_envio"
			id="<portlet:namespace />tipo_envio">
			<option value=""></option>
			<%for(int i = 0; i < WebKeysCorrespondencia.TIPOS_ENVIOS.length; i++ ) {%>
			<option value="<%=WebKeysCorrespondencia.TIPOS_ENVIOS[i][0] %>"> <%=WebKeysCorrespondencia.TIPOS_ENVIOS[i][1] %> </option>
			<% } %>
		</select></td>
	<%-- 	<td><label>Paquete:</label></td>
		<td><input id="<portlet:namespace />paquete"
			name="<portlet:namespace />paquete" size="20" maxlength="100"
			type="text" onkeydown="allowOnlyDigits(event);"/></td> --%>
		<td><label>Estado:</label></td>
		<td><select name="<portlet:namespace />estado_item"
			id="<portlet:namespace />estado_item">
			<option value=""></option>
			<%for(int i = 0; i < WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA.length; i++){ %>
			<option value="<%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[i] %>"><%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[i] %> </option>
			<%} %>
		</select></td>			
	</tr>
	<tr>
		<td><label>N° Correspondencia:</label></td>
		<td><input id="<portlet:namespace />numero_correspondencia"
			name="<portlet:namespace />numero_correspondencia" size="10" maxlength="10"
			type="text" onkeydown="allowOnlyDigits(event);"/></td>
		<td><label>N° Paquete:</label></td>
		<td><input id="<portlet:namespace />paquete"
			name="<portlet:namespace />paquete" size="10" maxlength="10"
			type="text" onkeydown="allowOnlyDigits(event);"/></td>
		<td><label>N° Seguim. Paquete:</label></td>
		<td><input id="<portlet:namespace />seguim_paquete"
			name="<portlet:namespace />seguim_paquete" size="10" maxlength="10"
			type="text" onkeydown="allowOnlyDigits(event);"/></td>
	</tr>
	<tr>
		<td><label>Edificio:</label></td>
		<td>
			<select name="<portlet:namespace/>edificio_destino" 
					id="<portlet:namespace/>edificio_destino" onchange="javascript:filtrarGrupos();" onchange="javascript:filtrarGrupos();"  onclick="javascript:filtrarGrupos();">
				<option value="">Seleccione un edificio</option>
				<%
					for (Organization org : organizaciones) {
				%>
						<option value="<%= org.getOrganizationId() %>"><%=org.getName() %></option>
				<%
					}
				%>
			</select>
		</td>
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
	<tr>
		<td><label>Tipo Remitente/Destinatario:</label></td>
		<td><select name="<portlet:namespace/>tipo_remitente" id="<portlet:namespace/>tipo_remitente" onchange="javascript:<portlet:namespace />mostrarDetalle();">
			<option value=""></option>
			<%-- <%for(int i = 0; i < WebKeysCorrespondencia.TIPOS_REMITENTES.length; i++ ) {%>
				<option value="<%=WebKeysCorrespondencia.TIPOS_REMITENTES[i][0] %>"> <%=WebKeysCorrespondencia.TIPOS_REMITENTES[i][1] %> </option>
			<% } %> --%>
			<%for(TipoRemitente tr : tipoRemitentes){%>
				 <option value="<%=tr.getIdTipoRemitente()%>"> <%=tr.getDescripcion() %> </option>
			<%} %>
		</select></td>
		<td><label>Oblea/Mensajería:</label></td>
		<td><input id="<portlet:namespace />nro_oblea"
			name="<portlet:namespace />nro_oblea" size="40"
			maxlength="50" type="text" value="" /></td>
			
		<td colspan = "2">&nbsp;</td>
		
	</tr>
	<tr>	
		<td colspan="6">
		<div id="<portlet:namespace />divBuscarAfiliado" name="<portlet:namespace />divBuscarAfiliado">
			<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
			<liferay-util:include page='/html/portlet/correspondencia/busqueda_afiliado.jsp'>
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
				name="<portlet:namespace />otros" size="100" maxlength="1000"
				type="text" /></td>
				</tr>
				</table>
			</fieldset>			
		</div>
		
		<div id="<portlet:namespace />divBuscarPrestador" name="<portlet:namespace />divBuscarPrestador">
															
				<fieldset class="block-labels"><legend>Datos Prestador</legend>				
				<liferay-util:include
			page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
			<liferay-util:param name="search_url"
				value="/correspondencia/buscar_prestador" />
			<liferay-util:param name="cuit_prestador"
				value='' />
			<liferay-util:param name="nombre_prestador"
				value='' />
			<liferay-util:param name="id_prestador"
				value='' />
			<liferay-util:param name="esEditable"
				value='<%=String.valueOf(true)%>' />
					</liferay-util:include></fieldset>
										
				<table style="border-collapse: separate; border-spacing: 5px;">
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
							<input id="<portlet:namespace />importe_total" name="<portlet:namespace />importe_total" size="12" maxlength="12" type="text" value="" onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_total'),event);"/>
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
											
				<table style="border-collapse: separate; border-spacing: 5px;">
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
	<tr>
		<td align="right" colspan="5">
		
<!--  		<c:if test="<%=showABMButtons%>"> -->
		<input
			id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar" />"
			onClick="javascript: <portlet:namespace />buscarCorrespondencia();"
			type="button" />&nbsp; <input type="button" value="Nueva Entrada"
			onClick="<portlet:namespace />nuevaEntrada();" />&nbsp; <input
			type="button" value="Nueva Salida"
			onClick="<portlet:namespace />nuevaSalida();" />&nbsp;
<!--  		</c:if>-->&nbsp;
		</td>
		<td>
			<input type="button" id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" onClick="<portlet:namespace />reporteXLS();" />
			<%-- <input type="button" id="<portlet:namespace />reporte" value="re-imprimir-paquete" onClick="<portlet:namespace />imprimirPaquete(994);" /> --%>
		</td>
	</tr>
	<tr>
		<td align="right" colspan="4">	
		<td><label>N° Paquete:</label>&nbsp;
			<input id="<portlet:namespace />nro_paq_reimp"
			name="<portlet:namespace />nro_paq_reimp" size="10" maxlength="10" width="10"
			type="text" onkeydown="allowOnlyDigits(event);"/>
		</td>
		<td>	
			<input type="button" id="<portlet:namespace />reporte" value="re-imprimir-paquete" onClick="<portlet:namespace />reImprimirPaquete();" />
		</td>
	</tr>	
</table>
</fieldset>
<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />busquedaCorrespondenciaDiv">
	<liferay-util:include page="/html/portlet/correspondencia/correspondencia_search_result.jsp">
	</liferay-util:include>
</div>
</fieldset>

<script type="text/javascript">

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
		
		/* if (remitente == 'AFILIADO') {
			jQuery('#<portlet:namespace />divBuscarAfiliado').show();			
		}
		if (remitente == 'FARMACIA') {
			jQuery('#<portlet:namespace />divBuscarFarmacia').show();
		}
		if (remitente == 'OTROS' || remitente == 'SSS' || remitente == 'DRISIDRO' || remitente == 'OMINT' || remitente == 'PREVENCION' || remitente == 'EMAIL') {
			jQuery('#<portlet:namespace />divBuscarOtros').show();
		}		
		if (remitente == 'PRESTADOR') {
			jQuery('#<portlet:namespace />divBuscarPrestador').show();
		}		
		if (remitente == 'PROVEEDOR' || remitente == 'APORTEMPL') {
			jQuery('#<portlet:namespace />divBuscarProveedor').show();
		}		
		if (remitente == 'SECCIONAL') {
			jQuery('#<portlet:namespace />divBuscarSeccional').show();
		}				 */
		<%for(TipoRemitente tr : tipoRemitentes){%>
		 	if (remitente == '<%=tr.getIdTipoRemitente()%>'){
		 		jQuery('#<portlet:namespace /><%=tr.getDiv()%>').show();
		 	}
		 <%}%>
	}

	jQuery('#<portlet:namespace />buscando').hide();
	
	function <portlet:namespace />nuevaEntrada() {
<%-- 		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/correspondencia/editar_entrada_entry" /></portlet:renderURL>';
		url = url + params; --%>
		var strutsUrl ='/correspondencia/editar_entrada_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="cmd" value="write"/>'+
	    '</liferay-portlet:renderURL>';
	    url = url.replace("__strutsUrl",strutsUrl);
	    
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

	function <portlet:namespace />nuevaSalida() {
<%-- 		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/correspondencia/editar_salida_entry" /></portlet:renderURL>';
		url = url + params; --%>
		var strutsUrl ='/correspondencia/editar_salida_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="cmd" value="write"/>'+
	    '</liferay-portlet:renderURL>';
	    url = url.replace("__strutsUrl",strutsUrl);
	    
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function <portlet:namespace />buscarCorrespondencia(){

		jQuery('#<portlet:namespace />buscando').show();
		var edificio=jQuery('#<portlet:namespace/>edificio').val();
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;	    
	    var numero_correspondencia=jQuery('#<portlet:namespace />numero_correspondencia').val();
		var tipo_registro=jQuery('#<portlet:namespace />tipo_registro').val();
		var tipo_envio=jQuery('#<portlet:namespace />tipo_envio').val();
		var paquete=jQuery('#<portlet:namespace />paquete').val();
		var seguimiento_paquete=jQuery('#<portlet:namespace />seguim_paquete').val();
		var tipo_remitente=jQuery('#<portlet:namespace />tipo_remitente').val();
		var edificio_destino=jQuery('#<portlet:namespace />edificio_destino').val();
		var sector_destino=jQuery('#<portlet:namespace />sector_destino').val();
		var usuario_destino=jQuery('#<portlet:namespace />usuario_destino').val();
		var oblea=jQuery('#<portlet:namespace />nro_oblea').val();
		var estado_item=jQuery('#<portlet:namespace />estado_item').val();
		
		try {
			var cuil=jQuery('#<portlet:namespace />cuil').val();
			var inte=jQuery('#<portlet:namespace />inte').val();
		}
		catch (err) {			
		}

		try {
			/* var id_farmacia=jQuery('#<portlet:namespace />id_farmacia').val(); */
			var id_farmacia=jQuery('#<portlet:namespace />id_farmacia_serial').val(); 
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

		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
		var viene_de = jQuery('#<portlet:namespace />viene_de').val();
		
		var busquedaCorr = { "edificio": edificio, "fechaDesdeFinal": fechaDesdeFinal, "fechaHastaFinal": fechaHastaFinal,
							 "numero_correspondencia": numero_correspondencia, "tipo_registro": tipo_registro, "tipo_envio": tipo_envio, 
							 "paquete": paquete, "seguim_paquete": seguimiento_paquete,
							 "tipo_remitente": tipo_remitente,
							 "cuil": cuil, "inte": inte, "id_farmacia": id_farmacia,
							 "otros": otros, "id_prestador": id_prestador, "cuit_entidad": cuit_entidad,
							 "sucursal_entidad": sucursal_entidad, "id_seccional": id_seccional,	 
							 "comprobante_tipo": comprobante_tipo, "comprobante_letra": comprobante_letra, "sucu": sucu,
							 "comprobante_nro": comprobante_nro, "importe_total": importe_total,
							 "edificio_destino" : edificio_destino, "sector_destino" : sector_destino, 
							 "usuario_destino" : usuario_destino,
							 "nro_oblea" : oblea, "estado_item": estado_item, "pagina" : offset_reg, "viene_de" : viene_de };

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/correspondencia/buscar_correspondencia" /></portlet:renderURL>';
		
        jQuery('#<portlet:namespace />busquedaCorrespondenciaDiv').load(url,busquedaCorr, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  });
	}
	
	function <portlet:namespace />reporteXLS(){
		var edificio=jQuery('#<portlet:namespace/>edificio').val();
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;	    
	    var numero_correspondencia=0;
		var tipo_registro=jQuery('#<portlet:namespace />tipo_registro').val();
		var tipo_envio=jQuery('#<portlet:namespace />tipo_envio').val();
		var paquete=jQuery('#<portlet:namespace />paquete').val();
		var seguimiento_paquete=jQuery('#<portlet:namespace />seguim_paquete').val();
		var tipo_remitente=jQuery('#<portlet:namespace />tipo_remitente').val();
		var edificio_destino=jQuery('#<portlet:namespace />edificio_destino').val();
		var sector_destino=jQuery('#<portlet:namespace />sector_destino').val();
		var usuario_destino=jQuery('#<portlet:namespace />usuario_destino').val();
		var oblea=jQuery('#<portlet:namespace />nro_oblea').val();
		var estado_item=jQuery('#<portlet:namespace />estado_item').val();
	
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
		
		 window.location.href ='/xlsservlet/?reporte=REPORTE_BUSQUEDA_CORRESPONDENCIA'
			 +'&edificio='+edificio 
			 +'&fechaDesdeFinal='+fechaDesdeFinal
			 +'&fechaHastaFinal='+fechaHastaFinal
			 +'&numero_correspondencia='+numero_correspondencia
			 +'&tipo_registro='+tipo_registro
			 +'&tipo_envio='+tipo_envio
			 +'&paquete='+paquete
			 +'&seguim_paquete='+seguimiento_paquete
			 +'&tipo_remitente='+tipo_remitente
			 +'&cuil='+cuil
			 +'&inte='+inte
			 +'&id_farmacia='+id_farmacia
			 +'&otros='+otros
			 +'&id_prestador='+id_prestador
			 +'&cuit_entidad='+cuit_entidad
			 +'&sucursal_entidad='+sucursal_entidad
			 +'&id_seccional='+id_seccional	 
			 +'&comprobante_tipo='+comprobante_tipo
			 +'&comprobante_letra='+comprobante_letra
			 +'&sucu='+sucu
			 +'&comprobante_nro='+comprobante_nro
			 +'&importe_total='+importe_total
			 +'&edificio_destino='+edificio_destino
			 +'&sector_destino='+sector_destino
			 +'&usuario_destino='+usuario_destino
			 +'&nro_oblea='+oblea
			 +'&estado_item='+estado_item; 
	}
	
	function <portlet:namespace />imprimirPaquete(paq_nro){
		/* var tipo_envio=jQuery('#<portlet:namespace />tipo_envio').val(); */
	
		if(!confirm("<liferay-ui:message key='desea-exportar-paquete'/>")){
			return false;
		}
		window.location.href ='/xlsservlet/?reporte=REPORTE_CORRESPONDENCIA_EMPAQUETADA'
	    	 +'&tipo_envio='+'PAQ_FARMACIA'
			 +'&paquete='+paq_nro
			 +'&id_farmacia='+0
			 +'&tipo_remitente=FARMACIA'
			 +'&importe_total='+''
			 +'&edificio_destino=119803'
			 +'&sector_destino=119809'
			 +'&usuario_destino=atittarelli';	
		
	}
	
	function <portlet:namespace />reImprimirPaquete(){
		var paq_nro=jQuery('#<portlet:namespace />nro_paq_reimp').val(); 
	
		if(!confirm("<liferay-ui:message key='desea-exportar-paquete'/>")){
			return false;
		}
		window.location.href ='/xlsservlet/?reporte=REPORTE_CORRESPONDENCIA_EMPAQUETADA'
	    	 +'&tipo_envio='+'PAQ_FARMACIA'
			 +'&paquete='+paq_nro
			 +'&id_farmacia='+0
			 +'&tipo_remitente=FARMACIA'
			 +'&importe_total='+''
			 +'&edificio_destino=119803'
			 +'&sector_destino=119809'
			 +'&usuario_destino=atittarelli';	
		
	}
	
	<% if (request.getAttribute("paquete_id")!= null){ %>
		var nro_paquete = '<%=request.getAttribute("paquete_id")%>';
		
		alert('Se ha generado exitosamente el paquete número: ' + nro_paquete);

		<portlet:namespace />imprimirPaquete(nro_paquete);		
	<%}%>
	
</script>