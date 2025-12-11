<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>

<%@ include file="/html/portlet/correspondencia/init.jsp"%>
<% 
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);	
	CabeceraCorrespondencia correspondencia=(CabeceraCorrespondencia)request.getSession().getAttribute(WebKeysCorrespondencia.SALIDA_EN_EDICION);
	ItemCorrespondencia itemC = (ItemCorrespondencia) request.getSession().getAttribute(WebKeysCorrespondencia.SALIDA_DETALLE_EN_EDICION);
	RemitenteDestinatario dest = (RemitenteDestinatario) request.getAttribute(WebKeysCorrespondencia.DESTINATARIO);	
	
	List<Organization> organizaciones = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		Organization orgLocal = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId()).get(0);
	
	boolean esView = false;
	if (viewStr != null){
		esView = true;
	}
	
	Calendar fechaEmi = Calendar.getInstance();
	Calendar fechaVen = Calendar.getInstance();
	
	String lugarEmision=correspondencia!=null?correspondencia.getLugar():"";
	int id_correspondencia=correspondencia!=null?(int)correspondencia.getId_correspondencia():0;
	String tipoRegistro=correspondencia!=null?correspondencia.getTipoRegistro():"";
	String tipoEnvio=correspondencia!=null?correspondencia.getTipoEnvio():"";
	
	//Datos de item para Editar Item
	String afil_cuil = (itemC!=null&&itemC.getAfiliado()!=null)?itemC.getAfiliado().getCuil_titular():"";      		
	String afil_inte = (itemC!=null&&itemC.getAfiliado()!=null)?String.valueOf(itemC.getAfiliado().getInte()):"";	    	
	String id_farmacia=(itemC!=null&&itemC.getFarmacia()!=null)?String.valueOf(itemC.getFarmacia().getId_farmacia()):"";   
	String cod_farmacia=(itemC!=null&&itemC.getFarmacia()!=null)?itemC.getFarmacia().getCodigo():"";   
	String desc_farmacia=(itemC!=null&&itemC.getFarmacia()!=null)?itemC.getFarmacia().getFarmacia():"";  
	String descripcion_otros=(itemC!=null&&itemC.getOtro()!=null)?itemC.getOtro():"";	   
 	String cuit_prestador_p = (itemC!=null&&itemC.getPrestador()!=null
 			&&itemC.getPrestador().getCuit()!=null)?itemC.getPrestador().getCuit():""; 	
	String nombre_prestador_p = (itemC!=null&&itemC.getPrestador()!=null
			&&itemC.getPrestador().getCuit()!=null)?itemC.getPrestador().getDescripcion():"";   	
	String id_prestador_p = (itemC!=null&&itemC.getPrestador()!=null
			&&itemC.getPrestador().getId_prestador()>0)?itemC.getPrestador().getId_prestadorString():"";	
	String cuit_prov_p = (itemC!=null&&itemC.getProveedor()!=null)?itemC.getProveedor().getCuit():"";	
	String sucu_prov_p = (itemC!=null&&itemC.getProveedor()!=null)?itemC.getProveedor().getSucursal():"";
	String razon_prov_p = (itemC!=null&&itemC.getProveedor()!=null)?itemC.getProveedor().getRazon_soc():"";
	String id_seccional_prov_p = (itemC!=null&&itemC.getProveedor()!=null)?String.valueOf(itemC.getProveedor().getId_seccional()):"";
	String id_secc_r_p = (itemC!=null&&itemC.getSeccional()!=null)?String.valueOf(itemC.getSeccional().getIdSeccional()):"";
	String secc_r_p = (itemC!=null&&itemC.getSeccional()!=null)?String.valueOf(itemC.getSeccional().getDescripcion()):"";
	String contenido = (itemC!=null&&itemC.getContenido()!=null)?itemC.getContenido():"";
	boolean tieneFC = (itemC!=null&&!itemC.getComprobanteString().isEmpty())?true:false;
	String fc_tipo = (itemC!=null&&!itemC.getComprobanteString().isEmpty())?itemC.getCompro_tipo():"";
	String fc_letra = (itemC!=null&&!itemC.getComprobanteString().isEmpty())?itemC.getCompro_letra():"";
	String fc_sucu = (itemC!=null&&!itemC.getComprobanteString().isEmpty())?String.valueOf(itemC.getCompro_sucu()):"";
	String fc_nro = (itemC!=null&&!itemC.getComprobanteString().isEmpty())?itemC.getCompro_nro():"";
	String fc_importe = (itemC!=null&&!itemC.getComprobanteString().isEmpty())?String.valueOf(itemC.getImporte()):"";
	
	if(itemC==null && dest != null){
		afil_cuil = (dest.getAfiliado()!=null)?dest.getAfiliado().getCuil_titular():"";
		afil_inte = (dest.getAfiliado()!=null)?String.valueOf(dest.getAfiliado().getInte()):"";
		id_farmacia=(dest.getFarmacia()!=null)?String.valueOf(dest.getFarmacia().getId_farmacia()):"";
		cod_farmacia=(dest.getFarmacia()!=null)?dest.getFarmacia().getCodigo():"";
		desc_farmacia=(dest.getFarmacia()!=null)?dest.getFarmacia().getFarmacia():"";
		descripcion_otros=(dest.getOtro()!=null)?dest.getOtro():"";
		cuit_prestador_p = (dest.getPrestador()!=null
		 			&&dest.getPrestador().getCuit()!=null)?dest.getPrestador().getCuit():"";
		nombre_prestador_p = (dest.getPrestador()!=null
							&&dest.getPrestador().getCuit()!=null)?dest.getPrestador().getDescripcion():""; 
		id_prestador_p = (dest.getPrestador()!=null
									&&dest.getPrestador().getId_prestador()>0)?dest.getPrestador().getId_prestadorString():"";	
		cuit_prov_p = (dest.getProveedor()!=null)?dest.getProveedor().getCuit():"";			
		sucu_prov_p = (dest.getProveedor()!=null)?dest.getProveedor().getSucursal():"";
		razon_prov_p = (dest.getProveedor()!=null)?dest.getProveedor().getRazon_soc():"";
		id_seccional_prov_p = (dest.getProveedor()!=null)?String.valueOf(dest.getProveedor().getId_seccional()):"";
		id_secc_r_p = (dest.getSeccional()!=null)?String.valueOf(dest.getSeccional().getIdSeccional()):"";
		secc_r_p = (dest.getSeccional()!=null)?String.valueOf(dest.getSeccional().getDescripcion()):"";
	}
	
	if(itemC!=null&&!itemC.getComprobanteString().isEmpty()){
		fechaEmi.setTime(itemC.getFecha_emision());
		fechaVen.setTime(itemC.getFecha_vencimiento());
	}
 	String edificio="", sector="", usuario="", sectorDesc="";
 	String edificioRemite="", sectorRemite="", usuarioRemite="", sectorRemiteDesc="";
	if(itemC!=null || dest!=null){
		edificio = itemC!=null?itemC.getEdificio():dest.getEmpresa_remite();
		sector = itemC!=null?itemC.getSector():dest.getSector_remite();
		sectorDesc = itemC!=null?itemC.getSectorDescripcion():dest.getEmpresa_rem_Descripcion();
		usuario = itemC!=null?itemC.getUsuario():dest.getUsuario_remite();
		
		edificioRemite = itemC!=null?itemC.getEmpresa_remite():"";
		sectorRemite = itemC!=null?itemC.getSector_remite():"";
		sectorRemiteDesc = itemC!=null?itemC.getSector_rem_Descripcion():"";
		usuarioRemite = itemC!=null?itemC.getUsuario_remite():"";
	} 
	
	/* Para obtener el destinatario  */
	List<EmpresaLiferay> esu = null; /* EmpresaSectorUsuarioServiceUtil.getEmpresasSectoresUsuarios();	*/
 	esu = (ArrayList<EmpresaLiferay>) portletSession
 		   .getAttribute(WebKeysCorrespondencia.EMPRESA_SECTOR_USUARIOS_LIFERAY_EN_SESSION,
 			   		PortletSession.APPLICATION_SCOPE);
	
%>

<fieldset class="block-labels">
<legend>Detalle</legend>
	<fieldset class="block-labels">
	<legend>Destinatario</legend>
	
	<table>	
	<tr>
			
		<td colspan="7"><label><liferay-ui:message key="tipo-destinatario"/>:</label>
		&nbsp;&nbsp;
			<select name="<portlet:namespace/>tipo_remitente" id="<portlet:namespace/>tipo_remitente" 
					onchange="javascript:<portlet:namespace />mostrarDetalle();"
					<% if (esView) { %> disabled="disabled" <%} %> >			
				<option value="">Seleccione un destinatario</option>
				<%-- <%for(int i = 0; i < WebKeysCorrespondencia.TIPOS_REMITENTES.length; i++ ) {%>
					<%if(!WebKeysCorrespondencia.TIPOS_REMITENTES[i][0].equalsIgnoreCase("EMAIL")){ %>
					
					<option value="<%=WebKeysCorrespondencia.TIPOS_REMITENTES[i][0] %>" 
						<%=(itemC != null && itemC.getTipoRemitenteDestinatario().equals(WebKeysCorrespondencia.TIPOS_REMITENTES[i][0]))? "selected":""%> > <%=WebKeysCorrespondencia.TIPOS_REMITENTES[i][1] %> </option>
					
					<% } %>
					
				<% } %>
				<option value="USUARIO" <%if(itemC != null && itemC.getTipoRemitenteDestinatario().equalsIgnoreCase("USUARIO")) {%> selected <% } %> >Usuario</option> --%>
				 
				 <%for(TipoRemitente tr : tipoRemitentes){
					 if(tr.getEs().equals(TipoRemitente.ENTRADA_SALIDA.AMBOS) ||
							 tr.getEs().equals(TipoRemitente.ENTRADA_SALIDA.SOLO_SALIDA)){%>
				 	<option value="<%=tr.getIdTipoRemitente()%>" 
				 	<%=((itemC != null && itemC.getTipoRemitenteDestinatario().equals(tr.getIdTipoRemitente()))||
				 			(dest != null && dest.getTipoRemitenteDestinatario().equals(tr.getIdTipoRemitente())) )? "selected":""%> > <%=tr.getDescripcion() %> </option>
				 <%} }%>
			</select>
		</td>
		<td>&nbsp;&nbsp;&nbsp;</td>
		<td><label><liferay-ui:message key="seguim-paq" />:</label></td>
		<td>
			<input type="text" name="<portlet:namespace />seguimiento_paquete" 
							   id="<portlet:namespace />seguimiento_paquete" 
							   <% if (esView) { %> disabled="disabled" <%} %>  
							   value="<%=itemC!=null&&itemC.getSeguimientoPaquete()!=null?itemC.getSeguimientoPaquete():"" %>"
							   maxlength="7" onkeydown="allowOnlyDigits(event);" size="5"> 
		</td>
		<td><input type="checkbox" <%if(dest!=null){ %>checked="checked" <%} %>  name="mantieneDestinatario" id="mantieneDestinatario" >Mantener Destinatario Seleccionado</td>
	</tr>
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>	
	
	<tr>	
		<td colspan="10">
		<div id="<portlet:namespace />divBuscarAfiliado" name="<portlet:namespace />divBuscarAfiliado">
			<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
			<liferay-util:include page='/html/portlet/correspondencia/busqueda_afiliado.jsp'>
				<liferay-util:param value="<%= String.valueOf(true) %>" name="edit_mode" />
				<liferay-util:param name="cuil" value='<%=afil_cuil%>' />
				<liferay-util:param name="inte" value='<%=afil_inte%>' />
			</liferay-util:include>
			</fieldset>						
		</div>	
			
		<div id="<portlet:namespace />divBuscarFarmacia" name="<portlet:namespace />divBuscarFarmacia">			
				<fieldset class="block-labels"><legend>Seleccione Farmacia</legend>				
				<liferay-util:include page='/html/portlet/uoma/correspondencia/busqueda_farmacia.jsp'>
					<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
					<liferay-util:param name="id_farmacia" value="<%=cod_farmacia%>" />
					<liferay-util:param name="farmacia" value="<%=desc_farmacia%>" />
					<liferay-util:param name="id_farmacia_serial" value="<%=id_farmacia%>" />
				</liferay-util:include>
				
				</fieldset>																													
		</div>
		
		<div id="<portlet:namespace />divBuscarOtros" name="<portlet:namespace />divBuscarOtros">		
			<fieldset class="block-labels"><legend>Descripción</legend>	
			<table class="lfr-table">
				<tr>
					<td><label>Nombre:</label></td>
					<td><input id="<portlet:namespace />otros"
						name="<portlet:namespace />otros" size="100" maxlength="1000" type="text" value="<%=descripcion_otros%>" /></td>
				</tr>
				</table>
			</fieldset>			
		</div>
		
		<div id="<portlet:namespace />divBuscarPrestador" name="<portlet:namespace />divBuscarPrestador">												
			<fieldset class="block-labels"><legend>Datos Prestador</legend>				
				<liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
					<liferay-util:param name="search_url" value="/correspondencia/buscar_prestador" />
					<liferay-util:param name="cuit_prestador" value='<%=cuit_prestador_p%>' />
					<liferay-util:param name="nombre_prestador" value='<%=nombre_prestador_p%>' />
					<liferay-util:param name="id_prestador" value='<%=id_prestador_p%>' />
					<liferay-util:param name="solo_vigentes" value='true' />
					<liferay-util:param name="esEditable" value='<%=String.valueOf(true)%>' />
				</liferay-util:include>
			</fieldset>							
		</div>	
			
		<div id="<portlet:namespace />divBuscarProveedor" name="<portlet:namespace />divBuscarProveedor">							
			<fieldset class="block-labels"><legend>Datos Proveedor</legend>
			<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
		  		<liferay-util:param name="esEditable" value='<%= String.valueOf(true) %>'/>
		  		<liferay-util:param name="cuit" value="<%=cuit_prov_p%>"/>
		  		<liferay-util:param name="sucu" value="<%=sucu_prov_p%>"/>
		  		<liferay-util:param name="razon" value="<%=razon_prov_p%>"/>
		  		<%-- <liferay-util:param name="id_seccional" value="<%=id_seccional_prov_p%>"/> --%>
				<liferay-util:param name="esEmpresaPrestador" value='true'/>		  				  		
		  		<liferay-util:param name="suf" value='_cor'/>
		  		<liferay-util:param name="suf_entidad" value='_ent_cor'/>
			</liferay-util:include>
			</fieldset>
			
		</div>
		<div id="<portlet:namespace />divBuscarSeccional" name="<portlet:namespace />divBuscarSeccional">
			<fieldset class="block-labels"><legend>Datos Seccional</legend>									   
				<liferay-util:include page="/html/portlet/liquidaciones/busqueda_seccional_reintegro.jsp" >
					<liferay-util:param name="id_seccional_r" value="<%=id_secc_r_p%>"/>
					<liferay-util:param name="seccional_r" value="<%=secc_r_p%>"/>					
				</liferay-util:include>
			</fieldset>
		</div>
		
		<div id="<portlet:namespace />divBuscarUsuario" name="<portlet:namespace />divBuscarUsuario">		
			<fieldset class="block-labels"><legend>Buscar Usuarios</legend>	
				<table class="lfr-table">
					<tr>
						<td><label>Edificio:</label></td>
						<td><select name="combo_0" id="combo_0"  onChange="change(this)" onfocus="change(this)">
							<% for(EmpresaLiferay e : esu){ %>
							<option value="<%=e.getEmpresa().getOrganizationId()%>"><%=e.getEmpresa().getName() %></option>
							<%} %></select></td>
						<td><label>Sector:</label></td>
						<td><select name="combo_1" id="combo_1"  onChange="change(this)" onfocus="change(this)">
							<option value="sinsel">Seleccione una empresa </option>
							</select></td>
						<td><label>Usuario:</label></td>
						<td><select name="combo_2" id="combo_2"  onChange="change(this)" onfocus="change(this)"> 
							<option value="sinsel">Seleccione un sector</option>
							</select></td>
					</tr>
				</table>
			</fieldset>			
		</div>
		
		<div id="<portlet:namespace />divCargaComprobante">
			<table>
				<tr>
					<td colspan="10">&nbsp;</td>
				</tr>
				<tr><td colspan="10">Carga Comprobante: <input type="checkbox"  name="<portlet:namespace/>cargaFC" 
							 id="<portlet:namespace/>cargaFC" 
					onclick="javascript:<portlet:namespace />activaCargaComprobante();" ></td>
				</tr>
			</table>
		</div>
		<div id="<portlet:namespace />divComprobante" >
			<table>
				<tr>
					<td><label><liferay-ui:message key="comprobante" />:</label></td>
					<td colspan="4">
	
					<select name="<portlet:namespace/>comprobante_tipo" id="<portlet:namespace/>comprobante_tipo">								
						<option value="FCP" <%if(itemC != null && !fc_tipo.isEmpty() && fc_tipo.equalsIgnoreCase("FCP") ) {%> selected <% } %>>FCP</option>
						<option value="NCR" <%if(itemC != null && !fc_tipo.isEmpty() && fc_tipo.equalsIgnoreCase("NCR") ) {%> selected <% } %>>NCR</option>
						<option value="RCB" <%if(itemC != null && !fc_tipo.isEmpty() && fc_tipo.equalsIgnoreCase("RCB") ) {%> selected <% } %>>RCB</option>
					</select> &nbsp;
					<select name="<portlet:namespace/>comprobante_letra" id="<portlet:namespace/>comprobante_letra">													
						<option value="B" <%if(itemC != null && !fc_letra.isEmpty() && fc_letra.equalsIgnoreCase("B") ) {%> selected <% } %>>B</option>
						<option value="C" <%if(itemC != null && !fc_letra.isEmpty() && fc_letra.equalsIgnoreCase("C") ) {%> selected <% } %>>C</option>
						<option value="X" <%if(itemC != null && !fc_letra.isEmpty() && fc_letra.equalsIgnoreCase("X") ) {%> selected <% } %>>X</option>
					</select> &nbsp;							
					<input id="<portlet:namespace />comprobante_sucu"
						name="<portlet:namespace />comprobante_sucu" size="5" maxlength="6"
						type="text"
						value="<%=fc_sucu%>" />&nbsp;							
					<input id="<portlet:namespace />comprobante_nro"
						name="<portlet:namespace />comprobante_nro" size="11" maxlength="8"
						type="text"
						value="<%=fc_nro%>" />
					</td>
					<td>Importe:</td>
					<td>
						<input id="<portlet:namespace />comprobante_importe_total" 
							   name="<portlet:namespace />comprobante_importe_total" size="12" maxlength="12" type="text" value="<%=fc_importe%>" 
							   onkeydown="allowOnlyDigitsAndDecimals(event); 
							   limitDecimals(2,document.getElementById('<portlet:namespace />importe_total'),event);"/>
					</td>
				</tr>
				<tr>
					<td colspan="10">&nbsp;</td>
				</tr>
				<tr>
					<td><label><liferay-ui:message key="fecha-emision" />:</label></td>
					<td><liferay-ui:input-date dayParam="fechaEDia"
						dayValue="<%= fechaEmi.get(Calendar.DATE)%>"
						monthParam="fechaEMes"
						monthValue="<%= fechaEmi.get(Calendar.MONTH) %>"
						yearParam="fechaEAnio"
						yearValue="<%= fechaEmi.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaEmi.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fechaEmi.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fechaEmi.getFirstDayOfWeek() - 1 %>"
						disabled="<%= esView %>" /></td>
					<td>&nbsp;</td>
					<td><label><liferay-ui:message key="fecha-vencimiento" />:</label></td>
					<td colspan="5"><liferay-ui:input-date dayParam="fechaVDia"
						dayValue="<%= fechaVen.get(Calendar.DATE)%>"
						monthParam="fechaVMes"
						monthValue="<%= fechaVen.get(Calendar.MONTH) %>"
						yearParam="fechaVAnio"
						yearValue="<%= fechaVen.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaVen.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fechaVen.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fechaVen.getFirstDayOfWeek() - 1 %>"
						disabled="<%= esView %>" /></td>
				</tr>
			</table>
		</div>
	</tr>		
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>
		
</table>
</fieldset>

<fieldset class="block-labels">
<legend>Remitente</legend>
	<table class="lfr-table">
		<tr>
			<td><label>Edificio:</label></td>
			<td><select name="<portlet:namespace/>edificio_destino"
				id="<portlet:namespace/>edificio_destino" <% if (esView) { %> disabled="disabled" <%}else{ %> onchange="javascript:filtrarGrupos();" onclick="javascript:filtrarGrupos();" onfocus="javascript:filtrarGrupos();"  <%} %>  >			
				<%
					for (Organization org : organizaciones) {
				%>
						<option value="<%= org.getOrganizationId() %>" 
						<%if(org.getOrganizationId()==orgLocal.getOrganizationId()){ %> selected="selected" <%} %> <%=org.getName() %>><%=org.getName() %>
						</option>
				<%
					}
				%>
			</select></td>
			<td><label>Sector:</label></td>
			<td><select name="<portlet:namespace/>sector_destino"
				id="<portlet:namespace/>sector_destino" <% if (esView) { %> disabled="disabled" <%} else { %> onchange="javascript:filtrarUsuarios(false, 'Entrada');" onclick="javascript:filtrarUsuarios(false, 'Entrada');" onfocus="javascript:filtrarGrupos();" <%} %>>			
				<option value="">Seleccione un sector</option>
				
			</select></td>
			<td><label>Usuario:</label></td>
			<td><select name="<portlet:namespace/>usuario_destino"
				id="<portlet:namespace/>usuario_destino" <% if (esView) { %> disabled="disabled" <%} %>>						
				<option value="">Seleccione un usuario</option>
					
			</select></td>
		</tr>	
		
		<tr>
			<td colspan="10">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="1"><label>Contenido:</label></td>
			<td colspan="5">
			<textarea rows="6" cols="100" maxlength="20000" 
					id="<portlet:namespace />contenido" 
					name="<portlet:namespace />contenido"
					style="resize: none;" 
					<% if (esView) { %> disabled="disabled" <%} %> ><%=contenido%></textarea>
			</td>
			<%-- <input id="<portlet:namespace />contenido"
				name="<portlet:namespace />contenido" size="100" maxlength="2000" type="text" value="<%=contenido%>" <% if (esView) { %> disabled="disabled" <%} %>  /></td> --%>
		</tr>
		<tr>
			<td colspan="10">&nbsp;</td>
		</tr>
		<tr>

		<% if (itemC==null && !esView  /*&& showABMButtons  && correspondencia.getLugar().equals(String.valueOf(orgLocal.getOrganizationId()))*/) { %>			
			<td><input type="button" value="Agregar" id="agregaBtn"
			 	onfocus="<portlet:namespace />verificaFcPrestadorDuplicada();"
				<%-- onClick="<portlet:namespace />agregarDetalle();return false;" /> --%>
				 onClick="javascript:<portlet:namespace />saveItemCorrespondencia();" />
				</td>
			<td>&nbsp;</td>
		<% } %>
		<% if (itemC!=null && !esView && showABMButtons) { %>				
			<td><input type="submit" value="Actualizar" id="actuBtn"
				onClick="actualizaCorrespondenciaDetalle();return false;" /></td>
			<td>&nbsp;</td>	<td><input type="submit" value="Cancelar"
				onClick="cancelarCorrespondenciaDetalle();return false;" /></td>
			<td>&nbsp;</td>
		<% } %>
	</tr>
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>	
	</table>
	<%String id_item_corresp = itemC!=null?Long.toString(itemC.getId()):""; %>
	<input type="hidden" id="<portlet:namespace />id_item_correspondencia" value="<%=id_item_corresp%>" />
	
	<input type="hidden" id="<portlet:namespace />miEdificio" value="<%=edificio%>">
	<input type="hidden" id="<portlet:namespace />miSector" value="<%=sector%>">
	<input type="hidden" id="<portlet:namespace />miSectord" value="<%=sectorDesc%>">
	<input type="hidden" id="<portlet:namespace />miUsuario" value="<%=usuario%>">
	<input type="hidden" id="<portlet:namespace />miCorreoInterno" value="<%=WebKeysCorrespondencia.TIPOS_ENVIOS[1][0] %>">
	<input type="hidden" id="<portlet:namespace />miMensajeria" value="<%=WebKeysCorrespondencia.TIPOS_ENVIOS[0][0] %>">
	<input type="hidden" id="<portlet:namespace />miEdificioRem" value="<%=edificioRemite%>">
	<input type="hidden" id="<portlet:namespace />miSectorRem" value="<%=sectorRemite%>">
	<input type="hidden" id="<portlet:namespace />miSectordRem" value="<%=sectorRemiteDesc%>">
	<input type="hidden" id="<portlet:namespace />miUsuarioRem" value="<%=usuarioRemite%>">
			
</fieldset>
</fieldset>

<script type="text/javascript">

jQuery('#<portlet:namespace />divBuscarAfiliado').hide();
jQuery('#<portlet:namespace />divBuscarFarmacia').hide();
jQuery('#<portlet:namespace />divBuscarOtros').hide();
jQuery('#<portlet:namespace />divBuscarPrestador').hide();
jQuery('#<portlet:namespace />divBuscarProveedor').hide();
jQuery('#<portlet:namespace />divBuscarSeccional').hide();
jQuery('#<portlet:namespace />divBuscarUsuario').hide();
jQuery('#<portlet:namespace />divComprobante').hide();
jQuery('#<portlet:namespace />divCargaComprobante').hide();


function <portlet:namespace />activaCargaComprobante(){
	var check = jQuery("#<portlet:namespace />cargaFC").is(':checked');
    if (check){
    	jQuery('#<portlet:namespace />divComprobante').show();
    }else{
    	jQuery('#<portlet:namespace />divComprobante').hide();
    }
} 

function <portlet:namespace />verificaFcPrestadorDuplicada(){
	var tipo_remitente = jQuery('#<portlet:namespace />tipo_remitente').val();	
	
	if(tipo_remitente == 'PRESTADOR'){
		var liqFC = document.getElementById("<portlet:namespace />cargaFC");
		var validaFC = liqFC.checked ? 'true' : 'false';	
	    
		if(validaFC == 'true'){
			var cuitPrestador = jQuery('#<portlet:namespace />cuit_prestador').val();
			var tipoComp = jQuery('#<portlet:namespace />comprobante_tipo').val();
			var letraComp = jQuery('#<portlet:namespace />comprobante_letra').val(); 
			var nroComp = jQuery('#<portlet:namespace />comprobante_nro').val(); 
			var sucuComp = jQuery('#<portlet:namespace />comprobante_sucu').val();
			var idPtoVenta = sucuComp;
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/verifica_fc_prestador_duplicada&cuitPrestador='+cuitPrestador+'&tipoComp='+tipoComp+'&letraComp='+letraComp+'&nroComp='+nroComp+'&sucuComp='+sucuComp+'&idPtoVenta='+idPtoVenta;
			
				jQuery.ajax({   
					url: url,
					success: function(data){
						var obj = jQuery.parseJSON(data);
						if(obj.verificado=="1"){
							alert('YA EXISTE EL COMPROBANTE TIPO-LETRA-PVTA-NUMERO: ' + tipoComp + ' ' + letraComp + ' ' + idPtoVenta + ' ' + nroComp);
							document.getElementById('agregaBtn').click();
							/* jQuery('#<portlet:namespace />agregaBtn').click(); */
						} 					
					}
				});
			}
	}
	return true;
}

function borraCorrespondenciaDetalle(id_item_correspondencia) {	 
	jQuery('#<portlet:namespace />id_item_correspondencia').val(id_item_correspondencia);
	<%-- var params = "&<%= Constants.CMD %>=" + "<%= Constants.DELETE %>" + "&id_item_correspondencia="+id_item_correspondencia;
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/correspondencia/editar_salida_entry';				
	url = url + params; --%>
	
	var strutsUrl ='/correspondencia/editar_salida_entry';
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
	'<liferay-portlet:param name="cmd" value="delete"/>'+
	'<liferay-portlet:param name="id_item_correspondencia" value="__id_item_correspondencia"/>'+
    '</liferay-portlet:renderURL>';
    url = url.replace("__strutsUrl",strutsUrl);
    url = url.replace("__id_item_correspondencia",id_item_correspondencia);
    
	submitForm(document.<portlet:namespace />fm, url);	
 }

function editaCorrespondenciaDetalle(id_item_correspondencia) {	 
	jQuery('#<portlet:namespace />id_item_correspondencia').val(id_item_correspondencia);
	 var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT %>" + "&id_item_correspondencia="+id_item_correspondencia;
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/editar_salida_entry';				
	url = url + params; 
	<%-- var strutsUrl ='/correspondencia/editar_salida_entry';
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
	'<liferay-portlet:param name="cmd" value="edit"/>'+
	'<liferay-portlet:param name="id_item_correspondencia" value="__id_item_correspondencia"/>'+
    '</liferay-portlet:renderURL>';
    url = url.replace("__strutsUrl",strutsUrl);
    url = url.replace("__id_item_correspondencia",id_item_correspondencia); --%>
    
	jQuery('#<portlet:namespace />destinatario_detalle').load(url);
}

function actualizaCorrespondenciaDetalle() {

	if (<portlet:namespace />validarCamposDetalle()) {
		var id_item_correspondencia = jQuery('#<portlet:namespace />id_item_correspondencia').val();
		<%-- var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>" + "&id_item_correspondencia="+id_item_correspondencia;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/correspondencia/editar_salida_entry';				
		url = url + params; --%>
		var strutsUrl ='/correspondencia/editar_salida_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'<liferay-portlet:param name="id_item_correspondencia" value="__id_item_correspondencia"/>'+
	    '</liferay-portlet:renderURL>';
	    url = url.replace("__strutsUrl",strutsUrl);
	    url = url.replace("__id_item_correspondencia",id_item_correspondencia);
	    
		submitForm(document.<portlet:namespace />fm, url);			
	}
	return false;
}


function cancelarCorrespondenciaDetalle() {
	var params = "&<%= Constants.CMD %>=" + "<%= Constants.CANCEL %>"
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/editar_salida_entry';
	url = url + params;
	<%-- var strutsUrl ='/correspondencia/editar_salida_entry';
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
	'<liferay-portlet:param name="cmd" value="delete"/>'+
    '</liferay-portlet:renderURL>';
    url = url.replace("__strutsUrl",strutsUrl); --%>
    
	jQuery('#<portlet:namespace />destinatario_detalle').load(url);
}

function <portlet:namespace />mostrarDetalle() {
	jQuery('#<portlet:namespace />divBuscarAfiliado').hide();
	jQuery('#<portlet:namespace />divBuscarFarmacia').hide();
	jQuery('#<portlet:namespace />divBuscarOtros').hide();
	jQuery('#<portlet:namespace />divBuscarPrestador').hide();
	jQuery('#<portlet:namespace />divBuscarProveedor').hide();
	jQuery('#<portlet:namespace />divBuscarSeccional').hide();
	jQuery('#<portlet:namespace />divBuscarUsuario').hide();
	jQuery('#<portlet:namespace />divComprobante').hide();
	jQuery('#<portlet:namespace />divCargaComprobante').hide();
	
	
	var remitente=jQuery('#<portlet:namespace/>tipo_remitente').val();
	/* if (remitente == 'AFILIADO') {
		jQuery('#<portlet:namespace />divBuscarAfiliado').show();			
	}
	if (remitente == 'FARMACIA') {
		jQuery('#<portlet:namespace />divBuscarFarmacia').show();
		jQuery('#<portlet:namespace />divCargaComprobante').show();
	}
	if (remitente == 'OTROS' || remitente == 'SSS' || remitente == 'DRISIDRO' || remitente == 'OMINT' 
			|| remitente == 'PREVENCION' || remitente == 'HTALALEMAN'  ) {
		jQuery('#<portlet:namespace />divBuscarOtros').show();
	}		
	if (remitente == 'PRESTADOR') {
		jQuery('#<portlet:namespace />divBuscarPrestador').show();
		jQuery('#<portlet:namespace />divCargaComprobante').show();
	}		
	if (remitente == 'PROVEEDOR' || remitente == 'APORTEMPL') {
		jQuery('#<portlet:namespace />divBuscarProveedor').show();
		jQuery('#<portlet:namespace />divCargaComprobante').show();
	}		
	if (remitente == 'SECCIONAL') {
		jQuery('#<portlet:namespace />divBuscarSeccional').show();
	}
	if (remitente == 'USUARIO') {
		jQuery('#<portlet:namespace />divBuscarUsuario').show();
	} */
	 <%for(TipoRemitente tr : tipoRemitentes){%>
	 	if (remitente == '<%=tr.getIdTipoRemitente()%>'){
	 		jQuery('#<portlet:namespace /><%=tr.getDiv()%>').show();
	 		
	 		if (<%=tr.getDivFactura()!=null%>){
	 			jQuery('#<portlet:namespace /><%=tr.getDivFactura()%>').show();
	 		}
	 		if (<%=tr.getValorPorDefecto() !=null%>){
	 			jQuery('#<portlet:namespace />otros').val('<%=tr.getValorPorDefecto()%>');
	 		}
	 	}
	 <%}%>
	
}

function <portlet:namespace />validarCamposDetalle(){
	var result = true;
	<%if(null!=correspondencia){%>
		var tipo_remitente = jQuery('#<portlet:namespace />tipo_remitente').val();	
		if(tipo_remitente.length == 0){
			alert("Tipo de Destinatario Obligatorio");
			jQuery("#<portlet:namespace />tipo_remitente").focus();
			return false;
		} else {
			if(tipo_remitente == 'AFILIADO') {
				var cuil=jQuery('#<portlet:namespace />cuil').val();
				var inte=jQuery('#<portlet:namespace />inte').val();
				if(cuil!=null && cuil.length==0){
					alert('Cuil Obligatorio');
					jQuery('#<portlet:namespace />cuil').focus();
					return false;
				}
				if(trim(inte).length == 0){
					alert("<liferay-ui:message key='inte-obligatorio' />");
					jQuery('#<portlet:namespace />inte').focus();
					return false;
				}													
			} else if(tipo_remitente == 'FARMACIA') {				
				var id_farmacia=jQuery('#<portlet:namespace />id_farmacia').val();
				if(trim(id_farmacia).length == 0){
					alert("Farmacia obligatoria");
					jQuery('#<portlet:namespace />id_farmacia').focus();
					return false;
				}				
			} else if(tipo_remitente == 'PRESTADOR') {				
				var id_prestador=jQuery('#<portlet:namespace />id_prestador').val();				
				if(trim(id_prestador).length == 0){
					alert("Prestador obligatorio");
					jQuery('#<portlet:namespace />id_prestador').focus();
					return false;
				}
				
			} else if(tipo_remitente == 'PROVEEDOR' || tipo_remitente == 'APORTEMPL') {
				var cuit_entidad = document.getElementById("<portlet:namespace />cuit_entidad_cor").value;
				var sucursal_entidad = document.getElementById("<portlet:namespace />sucursal_entidad_cor").value;							
				if(trim(cuit_entidad).length == 0){
					alert("Cuit Obligatorio");
					jQuery('#<portlet:namespace />cuit_entidad_cor').focus();
					return false;
				}
				if(trim(sucursal_entidad).length == 0){
					alert("Sucursal Obligatorio");
					jQuery('#<portlet:namespace />sucursal_entidad_cor').focus();
					return false;
				}
				
			} else if(tipo_remitente == 'SECCIONAL') {
				var id_seccional_r=jQuery('#<portlet:namespace />id_seccional_r').val();
				if(trim(id_seccional_r).length == 0){
					alert("Seccional Obligatoria");
					jQuery('#<portlet:namespace />id_seccional_r').focus();
					return false;
				}
			} else if(tipo_remitente == 'USUARIO') {
		
				var grp_liferay = document.getElementById("combo_1").value; /*jQuery('#<portlet:namespace/>combo_1').val();*/
				var usr_liferay = document.getElementById("combo_2").value; /*jQuery('#<portlet:namespace/>combo_2').val();*/
				
				if(trim(grp_liferay).length == 0 || grp_liferay == 'sinsel' ){
					alert("Sector Destinatario Obligatorio");
					return false;
				} 
				if(trim(usr_liferay).length == 0 || usr_liferay == 'sinsel') {
					alert("Usuario Destinatario Obligatorio");
					return false;
				} 
			} else{ /*if(tipo_remitente == 'OTROS' || tipo_remitente == 'SSS' || tipo_remitente == 'DRISIDRO' || tipo_remitente == 'OMINT' || tipo_remitente == 'HTALALEMAN') {*/
				var otros=jQuery('#<portlet:namespace />otros').val();
				if(trim(otros).length == 0){
					alert("Otros obligatorio");
					jQuery('#<portlet:namespace />otros').focus();
					return  false;
				}
			}	
			if(tipo_remitente == 'FARMACIA' || tipo_remitente == 'PROVEEDOR' || tipo_remitente == 'PRESTADOR' || tipo_remitente == 'APORTEMPL') {
				var liqFC = document.getElementById("<portlet:namespace />cargaFC");
				var validaFC = liqFC.checked ? 'true' : 'false';
				
				if( validaFC == 'true'){
					var comprobante=jQuery('#<portlet:namespace />comprobante_nro').val();
					var sucu = jQuery('#<portlet:namespace />comprobante_sucu').val();	
					var importeTot = jQuery('#<portlet:namespace />comprobante_importe_total').val();	
					if (trim(sucu).length == 0){
						alert("<liferay-ui:message key='valida-sucu' />");
						jQuery('#<portlet:namespace />comprobante_sucu').focus();
						return false;
					}
					if(trim(comprobante).length == 0){
						alert("<liferay-ui:message key='comprobante-obligatorio' />");
						jQuery('#<portlet:namespace />comprobante_nro').focus();
						return false;
					}
					if (importeTot < 0){
						alert("<liferay-ui:message key='importe-numerico' />");
						jQuery('#<portlet:namespace />comprobante_importe_total').focus();
						return false;
					}
				}	
			}
			
		}
		var sec_dest = jQuery('#<portlet:namespace />sector_destino').val();
		var usu_dest = jQuery('#<portlet:namespace />usuario_destino').val();
		if(sec_dest==''){
			alert("Debe seleccionar el Sector remitente");
			return false;
		}
		if(usu_dest==''){
			alert("Debe seleccionar el Usuario remitente");
			return false;
		}
		// validar que la empresa remitente sea diferente al lugar de emision
		var tipoEnv = jQuery('#<portlet:namespace/>tipo_envio').val();
		var recep = jQuery('#<portlet:namespace/>edificio').val();
		/* var dest = jQuery('#<portlet:namespace/>edificio_destino').val(); */
		var dest = document.getElementById("combo_0").value;
		var tipoCorreoInt = jQuery('#<portlet:namespace />miCorreoInterno').val();
		var tipoMensajeria = jQuery('#<portlet:namespace />miMensajeria').val();
		
		if(tipo_remitente == 'USUARIO' && tipoEnv == tipoCorreoInt && recep == dest){
			alert("El lugar de Destino debe ser diferente del lugar de Emisión");
			return false;
		} 
		if(tipo_remitente == 'USUARIO' && tipoEnv == tipoMensajeria && recep != dest && dest != 119803 ){ /*119803=tittarelli*/
			alert("El lugar de Destino debe ser el mismo del lugar de Emisión");
			return false;
		}  
		
	<%}%>
	return result;	
}

<%if(itemC != null || dest!=null){ %>
	
	<portlet:namespace />mostrarDetalle();
	
	/*Setea al Destinatario de la Edicion de  Item*/
	var edi=jQuery('#<portlet:namespace/>miEdificio').val();
	var sect=jQuery('#<portlet:namespace/>miSector').val();
	var sectd=jQuery('#<portlet:namespace/>miSectord').val();
	var usua=jQuery('#<portlet:namespace/>miUsuario').val();
	var ediRem=jQuery('#<portlet:namespace/>miEdificioRem').val();
	var sectRem=jQuery('#<portlet:namespace/>miSectorRem').val();
	var sectdRem=jQuery('#<portlet:namespace/>miSectordRem').val();
	var usuaRem=jQuery('#<portlet:namespace/>miUsuarioRem').val();
	
/* 	jQuery("#<portlet:namespace />edificio_destino option[value="+edi+"]").attr("selected",true);
	jQuery('#<portlet:namespace />sector_destino').append('<option value='+sect+' selected="selected">'+sectd+'</option>');
	jQuery('#<portlet:namespace />usuario_destino').append('<option value='+usua+' selected="selected">'+usua+'</option>'); */
	jQuery("#<portlet:namespace />edificio_destino option[value="+ediRem+"]").attr("selected",true);
	jQuery('#<portlet:namespace />sector_destino').append('<option value='+sectRem+' selected="selected">'+sectdRem+'</option>');
	jQuery('#<portlet:namespace />usuario_destino').append('<option value='+usuaRem+' selected="selected">'+usuaRem+'</option>');

	 jQuery("#combo_0 option[value="+ edi +"]").attr("selected",true);
	 jQuery("#combo_0").change();
	 jQuery("#combo_1 option[value="+ sect +"]").attr("selected",true);
	 jQuery("#combo_1").change();
	 jQuery("#combo_2 option[value="+ usua +"]").attr("selected",true);

<%} %>

<%if(tieneFC){ %>
jQuery("#<portlet:namespace/>cargaFC").attr('checked', 'checked');  
jQuery('#<portlet:namespace />divCargaComprobante').show();
jQuery('#<portlet:namespace />divComprobante').show();
<% } %>

</script>
