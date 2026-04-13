<%@page import="ar.com.ospim.util.StringUtils"%>
<%@page import="ar.com.ospim.crm.beans.EdificioSectorUsuarioLiferay"%>
<%@ include file="/html/portlet/crm/init.jsp"%>
<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "afiliados";
	}
	if(renderResponse.getNamespace().equals("_CAI_1_")){
		portlet_name = "cai";
	} 	
	
	if(renderResponse.getNamespace().equals("_JUD_1_")){
		portlet_name = "judicial";
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	PortletURL portletURL = renderResponse.createRenderURL();
	/* String viewStr = (String)request.getAttribute("view"); */
	String accion = (String) request.getAttribute(Constants.CMD);

	Boolean esAfiliado = (Boolean) request.getAttribute(WebKeysCrm.CRM_ES_AFILIADO);
	Boolean esContactoPersonal = (Boolean) request.getAttribute(WebKeysCrm.CRM_ES_PERSONAL_SECCIONAL);
	Boolean esPrestador = (Boolean) request.getAttribute(WebKeysCrm.CRM_ES_PRESTADOR);
	Boolean esEmpresa = (Boolean) request.getAttribute(WebKeysCrm.CRM_ES_EMPRESA);
	Boolean esCompaniero = (Boolean) request.getAttribute(WebKeysCrm.CRM_ES_COMPANIERO);
	if(esAfiliado==null) esAfiliado=false;
	if(esContactoPersonal==null) esContactoPersonal=false;
	if(esPrestador==null) esPrestador=false;
	if(esEmpresa==null) esEmpresa=false;
	if(esCompaniero==null) esCompaniero=false;

	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM);
	boolean esView = false;
	
	boolean esCAI = UserLocalServiceUtil.hasUserGroupUser(98474, user.getUserId() );
	boolean esCRM=UserLocalServiceUtil.hasUserGroupUser(120402, user.getUserId() );   
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	ContactoCRM contacto = null;
	
	if(esView){
		contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
	}else{
		contacto = (ContactoCRM) request.getSession().getAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
	}
	
	List<MotivoContacto> motivosCrm = (List<MotivoContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS);
	List<CategoriaContacto> categoriasCrm = (List<CategoriaContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_CATEGORIAS);
	List<TipoContacto> tiposCrm = (List<TipoContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_TIPOS);
	String email = (String) request.getAttribute(WebKeysCrm.CRM_AFILIADO_EMAIL);

	try{
		categoriasCrm.remove(new CategoriaContacto(6,"PREAUTORIZACION"));	
	}catch(Exception e){}
	
	Calendar desdeFecha = CalendarFactoryUtil.getCalendar();
	desdeFecha.setTime(new Date());
	desdeFecha.add(Calendar.DATE, -120); 
	Calendar hastaFecha = Calendar.getInstance();
	hastaFecha.setTime(new Date()); 
	
	EdificioSectorUsuarioLiferay esu = (EdificioSectorUsuarioLiferay) request.getAttribute(WebKeysCrm.CRM_COMPANIERO); 

%>

<form action="" method="post" name="<portlet:namespace />fm_crm">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%=accion%>" />
    <input name="<portlet:namespace />noAfiliado" type="hidden" value="<%=!esAfiliado%>" />
    <input name="<portlet:namespace />contactoAfiliado"  type="hidden" value="<%=esAfiliado %>" />
    <input name="<portlet:namespace />contactoSeccional"  type="hidden" value="<%=esContactoPersonal %>" />
    <input name="<portlet:namespace />contactoPrestador"  type="hidden" value="<%=esPrestador %>" />
    <input name="<portlet:namespace />contactoEmpresa"  type="hidden" value="<%=esEmpresa %>" />
    <input name="<portlet:namespace />contactoUsuario"  type="hidden" value="<%=esCompaniero %>" />
    
<liferay-ui:success key="insertContactoOk"  message="<%=(String)request.getAttribute(\"msgContactoOk\")  %>"  />
<liferay-ui:success key="updateContactoOk"  message="<%=(String)request.getAttribute(\"msgContactoOk\")  %>"  />
<liferay-ui:success key="deleteContactoOk"  message="<%=(String)request.getAttribute(\"msgContactoOk\")  %>"  />

<fieldset class="block-labels"><legend><liferay-ui:message key="crm-contacto" /></legend>

	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		<tr>
			<td colspan="2"><label><liferay-ui:message key="crm-contacto-nro" /></label>&nbsp;&nbsp;
			<input id="<portlet:namespace />numero_contacto"
				name="<portlet:namespace />numero_contacto" size="20"
				maxlength="100" type="text"  
				value="<%=contacto != null ? String.valueOf(contacto.getIdContacto()) : ""%>" 
				readonly="readonly" />
				
				&nbsp; <b><%=contacto != null && contacto.getImportancia() > 0 ? String.valueOf(contacto.getImportanciaDescripcion()) : ""%></b>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div align="center" id="<portlet:namespace />crm_afiliado">
					<%if(esAfiliado){ %>
					<liferay-util:include page="/html/portlet/crm/editar_contacto_afiliado.jsp">
					</liferay-util:include>
					<%}else if(!esAfiliado && esContactoPersonal){ %>
					<liferay-util:include page="/html/portlet/cai/view_contacto_seccional.jsp">
					</liferay-util:include>
					<%}else if(esPrestador){ %>
					<liferay-util:include page="/html/portlet/cai/view_contacto_prestador.jsp">
					</liferay-util:include>
					<%}else if(!esAfiliado && esEmpresa){ %>
					<liferay-util:include page="/html/portlet/cai/view_contacto_empresa.jsp">
					</liferay-util:include>
					<%}else if(!esAfiliado && esCompaniero){ %>
					<liferay-util:include page="/html/portlet/cai/view_contacto_companiero.jsp">
					</liferay-util:include>
					<%}else{%>
					<liferay-util:include page="/html/portlet/crm/editar_contacto_no_afiliado.jsp">
					</liferay-util:include>
					<%} %>
				</div>
			</td>
		</tr>
		<%if(!esView){ %>		
		<tr>
			<td colspan="2">	
				<fieldset class="block-labels">
					<legend><liferay-ui:message key="ultimos-contactos" /></legend>
					
					<label><liferay-ui:message key="fecha-desde" />:</label>
					<liferay-ui:input-date
						dayParam="fechaDesdeDia"
						dayValue="<%= desdeFecha.get(Calendar.DATE)%>"
						monthParam="fechaDesdeMes"
						monthValue="<%= desdeFecha.get(Calendar.MONTH) %>"
						yearParam="fechaDesdeAnio"
						yearValue="<%= desdeFecha.get(Calendar.YEAR) %>"
						yearRangeStart="<%= desdeFecha.get(Calendar.YEAR)-5 %>"
						yearRangeEnd="<%= desdeFecha.get(Calendar.YEAR)  %>"
						firstDayOfWeek="<%= desdeFecha.getFirstDayOfWeek()%>"
						/> 
					<label><liferay-ui:message key="fecha-hasta" />:</label>
					<liferay-ui:input-date
						dayParam="fechaHastaDia"
						dayValue="<%= hastaFecha.get(Calendar.DATE)%>"
						monthParam="fechaHastaMes"
						monthValue="<%= hastaFecha.get(Calendar.MONTH) %>"
						yearParam="fechaHastaAnio"
						yearValue="<%= hastaFecha.get(Calendar.YEAR) %>"
						yearRangeStart="<%= hastaFecha.get(Calendar.YEAR) %>"
						yearRangeEnd="<%= hastaFecha.get(Calendar.YEAR)  %>"
						firstDayOfWeek="<%= hastaFecha.getFirstDayOfWeek()%>"
						/>	
						<input type="button" value="Buscar" onclick="javascript: <portlet:namespace />buscarContactosAnteriores();">
					
					<div align="center" id="<portlet:namespace />ultimos_contactos" style="height:120px; overflow: scroll; overflow-x: hidden;">
						<liferay-util:include page="/html/portlet/crm/ultimos_contactos_search_result.jsp">
						</liferay-util:include>
					</div>
					
				</fieldset>
			</td>
		</tr>
		<%}%>
		<tr>
			<td colspan="2">
			<fieldset class="block-labels">
				<%if(!esView){ %>		
				<legend><liferay-ui:message key='nuevo-contacto' /></legend>
				<%} %>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-importancia" />:</label></td>
						<td><input type="checkbox" name="<portlet:namespace/>importancia"  id="<portlet:namespace/>importancia" 
							<%if(contacto!=null&& contacto.getImportancia()>0){%> checked="checked" <%} %>></td>
						<td><label><liferay-ui:message key="crm-contacto-incumplContrato" />:</label></td>
						<td><input type="checkbox" name="<portlet:namespace/>incumplContrato"  id="<portlet:namespace/>incumplContrato" 
							<%if(contacto!=null&& contacto.getIncumplimientoDelContrato()>0){%> checked="checked" <%} %>></td>	
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-tipo" />:</label></td>
						<td><select name="<portlet:namespace />tipo_contacto"
								id="<portlet:namespace />tipo_contacto" <% if (esView) { %> disabled="disabled" <%} %>>			
								<option value="0">Seleccione un tipo de contacto</option>
								<% for(TipoContacto tc : tiposCrm) {%>
									<option value="<%=tc.getId() %>"  
									<%= (contacto != null && contacto.getTipo().getId() == tc.getId())?"selected":""%>
									<%= (contacto == null && tc.getId() == WebKeysCrm.CRM_TIPO_CONT_DEFAULT)?"selected":""%>
									><%=tc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td><label><liferay-ui:message key="crm-contacto-categoria" />:</label></td>
						<td><select name="<portlet:namespace />categoria_contacto"
								id="<portlet:namespace />categoria_contacto" <% if (esView) { %> disabled="disabled" <%} %>>			
								<option value="0">Seleccione una categoría de contacto</option>
								<% for(CategoriaContacto cc : categoriasCrm) {%>
									<option value="<%=cc.getId() %>"  
									<%= (contacto != null && contacto.getCategoria().getId() == cc.getId())?"selected":"" %>
									<%= (contacto == null && cc.getId() == WebKeysCrm.CRM_CATEGORIA_DEFAULT)?"selected":""%>
									><%=cc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-motivo" />:</label></td>
						<td><select name="<portlet:namespace />motivo_contacto"
								id="<portlet:namespace />motivo_contacto" <% if (esView) { %> disabled="disabled" <%} %>
								onchange="javascript:<portlet:namespace />proponeDescripcionyCierre();">
								<option value="0">Seleccione un motivo de contacto</option>			
								<% for(MotivoContacto mc : motivosCrm) {%>
									<option value="<%=mc.getId() %>"  
									<%= (contacto != null && contacto.getMotivo().getId() == mc.getId())?"selected":""%>
									<%= (contacto == null && mc.getId() == WebKeysCrm.CRM_MOTIVO_DEFAULT)?"selected":""%>
									><%=mc.getDescripcion() %></option>
								<%} %>
							</select>
						</td>
						<td><label><liferay-ui:message key="crm-contacto-estado" />:</label></td>
						<td><select name="<portlet:namespace />estado_contacto"
								id="<portlet:namespace />estado_contacto" 
								<% if (esView) { %>  disabled="disabled" <%} %>
								onchange="javascript:<portlet:namespace />mostrarUsuarioDerivacion();" >
										
								<%if(accion!=null&&accion.equalsIgnoreCase("update")&&contacto.getEstado().equals(ContactoCRM.ESTADOS.DERIVADO) || esCompaniero){%>
										<option value="<%=WebKeysCrm.CRM_ESTADOS[1] %>"><%=WebKeysCrm.CRM_ESTADOS[1]%></option>
								<%}else if(accion!=null&&accion.equalsIgnoreCase("update")&&!contacto.getEstado().equals(ContactoCRM.ESTADOS.DERIVADO)){%>
										<option value="<%=WebKeysCrm.CRM_ESTADOS[0] %>" <%= (contacto != null 
											&& (WebKeysCrm.CRM_ESTADOS[0].equalsIgnoreCase(contacto.getEstado().name()) ) )?"selected":""%>><%=WebKeysCrm.CRM_ESTADOS[0]%></option>
										<option value="<%=WebKeysCrm.CRM_ESTADOS[2] %>" <%= (contacto != null 
											&& (WebKeysCrm.CRM_ESTADOS[2].equalsIgnoreCase(contacto.getEstado().name()) ) )?"selected":""%>><%=WebKeysCrm.CRM_ESTADOS[2]%></option>
								<%}else { %>		
									<option value="0">Seleccione un estado de contacto</option>
									<% for(int i = 0; i < WebKeysCrm.CRM_ESTADOS.length; i++){ %>
										<%if(!esAfiliado && !esContactoPersonal){ %>
												<%if(WebKeysCrm.CRM_ESTADOS[i].equalsIgnoreCase(WebKeysCrm.CRM_ESTADOS[1])){ %>
													<option value="<%=WebKeysCrm.CRM_ESTADOS[i] %>" 
													<%= (contacto != null && (WebKeysCrm.CRM_ESTADOS[i].equalsIgnoreCase(contacto.getEstado().name()) ) )?"selected":""%>
													><%=WebKeysCrm.CRM_ESTADOS[i]%></option>
											    <%} %>
												<%if(WebKeysCrm.CRM_ESTADOS[i].equalsIgnoreCase(WebKeysCrm.CRM_ESTADOS[2])){ %>
													<option value="<%=WebKeysCrm.CRM_ESTADOS[i] %>" 
													<%= (contacto != null && (WebKeysCrm.CRM_ESTADOS[i].equalsIgnoreCase(contacto.getEstado().name()) ) )?"selected":""%>
													><%=WebKeysCrm.CRM_ESTADOS[i]%></option>
											    <%} %>	
										<%}else{ %>
										<option value="<%=WebKeysCrm.CRM_ESTADOS[i] %>" 
											<%= (contacto != null && (WebKeysCrm.CRM_ESTADOS[i].equalsIgnoreCase(contacto.getEstado().name()) ) )?"selected":""%>
											<%= (contacto == null && (WebKeysCrm.CRM_ESTADOS[i].equalsIgnoreCase(WebKeysCrm.CRM_ESTADO_DEFAULT) ) )?"selected":""%> 
											><%=WebKeysCrm.CRM_ESTADOS[i]%></option>
										<%} %>	
									<%} %>
								<%} %>
							</select>
						</td>
					</tr>
					
					<%if(esView){ %>
					<tr>
						<td colspan="4"><a href="javascript:buscaSeguimientoCrmContacto();" title="Historial de Derivaciones">
							<label><liferay-ui:message key="crm-histo-deriv" /></label> 
							<img height='16'  width='16' 
							 src='/html/themes/classic/images/common/view_tasks.png' alt='Historial de derivaciones' />
							</a> 
						</td>
					</tr> 
					<tr>
						<td colspan="4">
						<div align="center" 
							 id="<portlet:namespace />seguimiento_contacto" 
							 style="height:120px; overflow: scroll; overflow-x: hidden;">
								<liferay-util:include page="/html/portlet/crm/seguimiento_search_result.jsp">
								</liferay-util:include>
						</div>
						</td>
					</tr>
					<%} %>
					<%if(!esCompaniero || contacto!=null){ %> 
					<tr>
						<td colspan="4">
							<div align="center" id="<portlet:namespace />divUsuarioDerivacion" >
								<liferay-util:include page="/html/portlet/crm/derivacion_liferay.jsp">
									<liferay-util:param name="view" value="<%=esView?new String(\"true\"):new String(\"false\") %>"/>
									
								</liferay-util:include>
							</div>
						</td>
					</tr>
					<%} %>
					<%if(esCompaniero && contacto ==null){ %> 
						<tr>
							<td colspan="4" align="center">
								<table  class="lfr-table">
						
								<tr>
									<td><label><liferay-ui:message key="edificio" />:</label></td>
									<td><select name="<portlet:namespace/>edificio_destino_compa"
										id="<portlet:namespace/>edificio_destino_compa">
										<option value="1">sel</option>
										</select>
									</td>
									<td><label><liferay-ui:message key="sector" />:</label></td>
									<td><select name="<portlet:namespace/>sector_destino_compa"
										id="<portlet:namespace/>sector_destino_compa">											
										</select>
									</td>
									<td><label><liferay-ui:message key="usuario" />:</label></td>
									<td><select name="<portlet:namespace/>usuario_destino_compa"
										id="<portlet:namespace/>usuario_destino_compa">						
										</select>
									</td>
								</tr>
								</table>
							</td>
						</tr>	
			   
					<%} %> 
					<%if(esCompaniero && contacto==null ){ %>
					<script type="text/javascript">
						jQuery('#<portlet:namespace/>edificio_destino_compa').append(new Option('<%=esu.getEmpresaDescripcion()%>', '<%=esu.getEdificio()%>', true, true));
						jQuery('#<portlet:namespace/>sector_destino_compa').append(new Option('<%=esu.getSectorDescripcion()%>', '<%=esu.getGrupo()%>', true, true));
						jQuery('#<portlet:namespace/>usuario_destino_compa').append(new Option('<%=esu.getUsuarioApeyNom()%>', '<%=esu.getUsuario()%>', true, true));
						
					</script>
					<%} %>
					<%-- <liferay-util:param name="edif" value="<%=contacto.getDerivacion()!=null?contacto.getDerivacion().getEdificio():new String(\"0\") %>"/>
						<liferay-util:param name="sect" value="<%=contacto.getDerivacion()!=null?contacto.getDerivacion().getGrupo():new String(\"0\") %>"/>
						<liferay-util:param name="usu"  value="<%=contacto.getDerivacion()!=null?contacto.getDerivacion().getUsuario():new String(\"0\") %>"/> --%>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-descripcion" />:</label></td>
						<td colspan="3"><textarea rows="6" cols="100"  
									id="<portlet:namespace />descripcion_contacto" 
									name="<portlet:namespace />descripcion_contacto"
									style="resize: none;" 
									<% if (esView) { %> disabled="disabled" <%} %> ><%= contacto!=null?contacto.getDescripcion():""%></textarea>
						</td>			
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-relac" />:</label></td>
						<td><input id="<portlet:namespace />relacionado_con_contacto"
							name="<portlet:namespace />relacionado_con_contacto" size="20"
							maxlength="100" type="text"  
							value="<%=contacto!=null && contacto.getIdCrmRelacionado()!=null ? String.valueOf(contacto.getIdCrmRelacionado()) : ""%>" 
							<% if (esView) { %> disabled="disabled" <%} %> />
						</td>
						<td colspan="2">&nbsp;</td>
					</tr>
					<%if(contacto!=null && StringUtils.checkNotEmpty(contacto.getComentarioAvance())){ %>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-comentario-avance" />:</label></td>
						<td colspan="3"><textarea rows="5" cols="100" 
									id="<portlet:namespace />comentarios_avance" 
									name="<portlet:namespace />comentarios_avance"
									style="resize: none;" 
									disabled="disabled"><%= contacto!=null&&contacto.getComentarioAvance()!=null?contacto.getComentarioAvance() :""%></textarea>
						</td>			
					</tr>
					<%} %>
					<% if(!esView) { %> 
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-comentario-avance" />:</label></td>
						<td colspan="3"><textarea rows="5" cols="100"  
									id="<portlet:namespace />comentarios_avance_nuevo" 
									name="<portlet:namespace />comentarios_avance_nuevo"
									style="resize: none;"></textarea>
						</td>			
					</tr>
					<%} %>
					<%-- <tr>
						<td><label><liferay-ui:message key="crm-contacto-comentario-cierre" />:</label></td>
						<td colspan="3"><textarea rows="5" cols="100" maxlength="90000" 
									id="<portlet:namespace />comentarios_contacto" 
									name="<portlet:namespace />comentarios_contacto"
									style="resize: none;" 
									<% if (esView) { %> disabled="disabled" <%} %> ><%= contacto!=null&&contacto.getComentarioCierre()!=null?contacto.getComentarioCierre() :""%></textarea>
						</td>			
					</tr> --%>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-comentario-cierre" />:</label></td>
						<td colspan="3">
						<%if(contacto!=null || esView) {%>
						
							<div align="left" id="<portlet:namespace />divComentarioCierre" >
								<liferay-util:include page="/html/portlet/crm/editar_contacto_cierre.jsp">
									<liferay-util:param name="esView" value="<%=esView?new String(\"true\"):new String(\"false\") %>"/>
									
								</liferay-util:include>
							</div>
						<%}else{ %>
						
							<div align="left" id="<portlet:namespace />divComentarioCierrePredet" >
								<liferay-util:include page="/html/portlet/crm/editar_contacto_cierre.jsp">
									<liferay-util:param name="esView" value="<%=esView?new String(\"true\"):new String(\"false\") %>"/>
									
								</liferay-util:include>
							</div>
						<%} %>
						</td>

					</tr>
					<tr>
						<%if(!esView){ %>
							<%if(contacto == null){ %>
								<td>
									<input type="button" value="Grabar" onclick="javascript:grabarCrmContacto();">
								</td>
							 <%}else{ %>
								<td>
									<input type="button" value="Modificar" onclick="javascript:modificaCrmContacto();">
								</td> 
							<%} %>	 
						<td>
							<input type="button" value="Nuevo" onclick="javascript:nuevoCrmContactoSeleccion();">
						</td>
						<%} %>
						
					</tr>
					<%if(contacto != null){ %>
					<tr>
						<td colspan="4"></hr></td>
					</tr>
					<tr>
						<td colspan="4">
							<div align="center" id="<portlet:namespace />crm_auditoria">
								<table style="font-size: 8">
									<tr>
										<td><label><liferay-ui:message key="crm-contacto-alta-sec-usu" />:</label></td>
										<td><%=contacto.getAltaSector()+"/"+contacto.getAltaUsr() %></td>
										<td><label><liferay-ui:message key="crm-contacto-alta-fec" />:</label></td>
										<td><%=sdf.format(contacto.getAltaFecha()) %></td>
										<td><label><liferay-ui:message key="crm-contacto-modi-sec-usu" />:</label></td>
										<td><%=contacto.getModiSector()+"/"+contacto.getModiUsr() %></td>
										<td><label><liferay-ui:message key="crm-contacto-modi-fec" />:</label></td>
										<td><%=sdf.format(contacto.getModiFecha()) %></td>
									</tr>
								</table>   
							</div>
						</td>
					</tr>
					<% } %>						
				</table>
			</fieldset>
			</td>
		</tr>
	</table>		
</fieldset>

</form>

<script type="text/javascript" >
jQuery('#<portlet:namespace />divUsuarioDerivacion').hide();		
jQuery('#<portlet:namespace />divResultadoActualizarOK').hide();
jQuery('#<portlet:namespace />seguimiento_contacto').hide();

var popupCRM;

function grabarCrmContacto(){
	if (<portlet:namespace />validarCampos()) {
	var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
			<portlet:param name="struts_action" value="/afiliados/editar_contacto_entry" />
			<portlet:param name="cmd" value="save" />
 	        </portlet:renderURL>';
    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
		 url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
						<portlet:param name="struts_action" value="/cai/editar_contacto_entry" />
						<portlet:param name="cmd" value="save" />
				</portlet:renderURL>';
	</c:if>	
	
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>
	 url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
					<portlet:param name="struts_action" value="/judicial/editar_contacto_entry" />
					<portlet:param name="cmd" value="save" />
			</portlet:renderURL>';
    </c:if>	
	
		document.<portlet:namespace />fm_crm.method = 'post';
		submitForm(document.<portlet:namespace />fm_crm, url);
	}	
}

function modificaCrmContacto(){
	if (<portlet:namespace />validarCampos()) {
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
						<portlet:param name="struts_action" value="/afiliados/editar_contacto_entry" />
						<portlet:param name="cmd" value="update" />
				</portlet:renderURL>';
				
		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
				 url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
								<portlet:param name="struts_action" value="/cai/editar_contacto_entry" />
								<portlet:param name="cmd" value="update" />
						</portlet:renderURL>';
		</c:if>		
		
		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>
		 url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
						<portlet:param name="struts_action" value="/judicial/editar_contacto_entry" />
						<portlet:param name="cmd" value="update" />
				</portlet:renderURL>';
        </c:if>
				
		document.<portlet:namespace />fm_crm.method = 'post';
		submitForm(document.<portlet:namespace />fm_crm, url);
	}	
}

function verCrmContacto(idContSerial) {
	var params = "&<%= Constants.CMD %>=" + "<%= Constants.VIEW%>";
	params = params + '&idContactoSerial='+idContSerial;
	
	popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true, width: 880, position:['center',30]});
	
/* 	popupCRM= Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true,width:880,height:655});*/
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_contacto_entry';   		       	
	
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
	    url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/editar_contacto_entry';
    </c:if>
    
    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>
        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/judicial/editar_contacto_entry';
    </c:if>
    url = url + params;
	jQuery(popupCRM).load(url);	
	
	
	
}

function nuevoCrmContactoSeleccion() {
	var cambiarAfiliado = true;
	var cuil_titu='0';
	var integ='0';
	
	var debePreguntar = <%=esAfiliado%>;
		
	if(debePreguntar==true){
		if (confirm('<liferay-ui:message key="crm-contacto-rapido" />')) {
			cuil_titu=jQuery('#<portlet:namespace />cuil_titular').val();
			integ=jQuery('#<portlet:namespace />inte').val();
			cambiarAfiliado = false;
		} else {
			cambiarAfiliado = true;
		}
    }
    
	<%-- var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/afiliados/editar_contacto_entry" /></portlet:renderURL>';
	url=url+'&cuil_titular='+cuil_titu+'&inte='+integ+'&<%=Constants.CMD%>='+'<%=Constants.ADD %>' ;
	url=url+'&cambiarAfiliado='+cambiarAfiliado;
	document.<portlet:namespace />fm_crm.method = 'post';
	submitForm(document.<portlet:namespace />fm_crm, url); --%>
	
	var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
				<portlet:param name="struts_action" value="/afiliados/editar_contacto_entry" />
				<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.ADD %>" />
			</portlet:renderURL>';
			url=url+'&cuil_titular='+cuil_titu+'&inte='+integ;
			url=url+'&cambiarAfiliado='+cambiarAfiliado;			
	
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
	
			 url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
							<portlet:param name="struts_action" value="/cai/editar_contacto_entry" />
							<portlet:param name="cmd" value="<%=Constants.ADD %>" />
					</portlet:renderURL>';
					url=url+'&cuil_titular='+cuil_titu+'&inte='+integ;
	</c:if>		
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>
	
	 url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
					<portlet:param name="struts_action" value="/judicial/editar_contacto_entry" />
					<portlet:param name="cmd" value="<%=Constants.ADD %>" />
			</portlet:renderURL>';
			url=url+'&cuil_titular='+cuil_titu+'&inte='+integ;
    </c:if>	
	
	document.<portlet:namespace />fm_crm.method = 'post';
	submitForm(document.<portlet:namespace />fm_crm, url);

}

function <portlet:namespace />validarCampos() {
	
	var esAfi = '<%=esAfiliado %>';
	var esPersonalSeccional = '<%=esContactoPersonal%>';
	var esPrestador = '<%=esPrestador%>';
	var esEmpresa = '<%=esEmpresa%>';
	var esCompa = '<%=esCompaniero%>';
	var cod_motivo=parseInt(jQuery("#<portlet:namespace />motivo_contacto").val());
	var cod_categoria=parseInt(jQuery("#<portlet:namespace />categoria_contacto").val());
	var cod_tipo=parseInt(jQuery("#<portlet:namespace />tipo_contacto").val());
	var cod_estado=parseInt(jQuery("#<portlet:namespace />estado_contacto").val());
	var descrip=jQuery('#<portlet:namespace />descripcion_contacto').val();
	var comentario_cierre=jQuery('#<portlet:namespace />comentarios_contacto').val();
	var cuil_titular=jQuery('#<portlet:namespace />cuil_titular').val();
	/* var inte=jQuery('#<portlet:namespace />inte').val(); */
	var noa_doc_tipo=jQuery('#<portlet:namespace/>noafi_documento_tipo').val();
	var noa_doc_nro=jQuery('#<portlet:namespace />noafi_documento_nro').val();
	var noa_apel=jQuery('#<portlet:namespace />noafi_apellido').val();
	var noa_nom=jQuery('#<portlet:namespace />noafi_nombre').val();
 	if (esAfi=='true' && trim(cuil_titular).length == 0){
		alert("Seleccione un afiliado, desde los resultados de la búsqueda");
		return false;
	}
 	
 	if (esAfi=='false' && esPersonalSeccional=='false'  && esCompa=='false' 
 			&& esPrestador=='false'  && esEmpresa=='false' && trim(noa_doc_tipo).length == 0){
		alert("Ingrese el tipo de documento del contacto");
		jQuery('#<portlet:namespace/>noafi_documento_tipo').focus();
		return false;
	}

	if (esAfi=='false' && esPersonalSeccional=='false' && esCompa=='false' 
			&& esPrestador=='false'  && esEmpresa=='false' && trim(noa_doc_nro).length == 0){
		alert("Ingrese el numero de documento del contacto");
		jQuery('#<portlet:namespace/>noafi_documento_nro').focus();
		return false;
	}
	if (esAfi=='false' && esPersonalSeccional=='false' && esCompa=='false' 
			&& esPrestador=='false'  && esEmpresa=='false' && trim(noa_apel).length == 0){
		alert("Ingrese el apellido del contacto");
		jQuery('#<portlet:namespace/>noafi_apellido').focus();
		return false;
	}
	if (esAfi=='false' && esPersonalSeccional=='false' && esCompa=='false' 
			&& esPrestador=='false'  && esEmpresa=='false' && trim(noa_nom).length == 0){
		alert("Ingrese el nombre del contacto");
		jQuery('#<portlet:namespace/>noafi_nombre').focus();
		return false;
	}
	
	if(cod_motivo == 0){
		alert("Ingrese un motivo de contacto");
		jQuery('#<portlet:namespace />motivo_contacto').focus();
		return false;
	}
	if(cod_categoria == 0){
		alert("Ingrese una categoría de contacto");	
		jQuery('#<portlet:namespace />categoria_contacto').focus();
		return false;
	}
	if(cod_tipo == 0){
		alert("Ingrese un tipo de contacto");	
		jQuery('#<portlet:namespace />tipo_contacto').focus();
		return false;
	}
	if(cod_estado == 0){
		alert("Ingrese un estado del contacto");	
		jQuery('#<portlet:namespace />estado_contacto').focus();
		return false;
	}
	if (trim(descrip).length == 0){
		alert("Ingrese la descripción del contacto");
		jQuery('#<portlet:namespace />descripcion_contacto').focus();
		return false;
	}
	var estado=jQuery('#<portlet:namespace />estado_contacto').val();
	
	
	<%if(accion==null || (accion!=null && !accion.equalsIgnoreCase(Constants.UPDATE))){ %>
	if (estado == '<%= ContactoCRM.ESTADOS.DERIVADO%>' && <%=!esCompaniero%>) {
		var sec_dest = jQuery('#<portlet:namespace />sector_destino').val();
		var usu_dest = jQuery('#<portlet:namespace />usuario_destino').val();
		
		if(typeof sec_dest == 'undefined' || sec_dest == null || sec_dest == ''){
			alert("Debe seleccionar el Sector derivación");
			return false;
		}
		if(typeof usu_dest == 'undefined' || usu_dest == null || usu_dest == ''){
			alert("Debe seleccionar el Usuario derivación");
			return false;
		}
	}
	<%}%>
	if (estado == '<%= ContactoCRM.ESTADOS.CERRADO%>') {
		if(comentario_cierre==''){
			alert("Debe ingresar un comentario de cierre");
			jQuery('#<portlet:namespace />comentarios_contacto').focus();
			return false;
		}
	}
	
	if (estado == '<%= ContactoCRM.ESTADOS.DERIVADO%>') {
		if(trim(comentario_cierre).length > 0){
			alert("El comentario de cierre debe estar vacío");
			jQuery('#<portlet:namespace />comentarios_contacto').focus();
			return false;
		}
	}
	
	return true;
}

function <portlet:namespace />buscarContactosAnteriores(){

	var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
    var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();
    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;
    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
    var desde_final=diaDesde+'/'+mesDesde+'/'+anioDesde;		
	var hasta_final=diaHasta+'/'+mesHasta+'/'+anioHasta;		
	var cuiltitular= jQuery('#<portlet:namespace />cuil_titular').val();
	var integrante  = jQuery('#<portlet:namespace />inte').val();
    var contactoseccional = jQuery('#<portlet:namespace />id_contactoSeccional_1').val();
    
	var busquedaContactos = { "fechaDesdeFinal": desde_final, "fechaHastaFinal": hasta_final, "cuil_titular": cuiltitular, "inte": integrante,"contactoseccional":contactoseccional};
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/afiliados/buscar_contactos" /></portlet:renderURL>';
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/cai/buscar_contactos" /></portlet:renderURL>';
    </c:if>
    
    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>
        url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/judicial/buscar_contactos" /></portlet:renderURL>';
    </c:if>
	
    jQuery('#<portlet:namespace />ultimos_contactos').load(url,busquedaContactos, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );	
}

function <portlet:namespace />mostrarUsuarioDerivacion() {
	jQuery('#<portlet:namespace />divUsuarioDerivacion').hide();
	
	var estado=jQuery('#<portlet:namespace />estado_contacto').val();
	if (estado == '<%= ContactoCRM.ESTADOS.DERIVADO%>') {
		jQuery('#<portlet:namespace />divUsuarioDerivacion').show();			
	}else{
		jQuery('#<portlet:namespace />divUsuarioDerivacion').hide();
	}
}	
var popupDomicilio;

function mostrarDomicilioAfiliado(cuil_titu, integ){   
	popupDomicilio= Liferay.Popup({title:"<liferay-ui:message key="detalle-domicilio" />",modal:true,width:950,height:330,fixedcenter:true});
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/actualiza_domicilio&cuil_titular='+cuil_titu+'&inte='+integ+'&cmd=view'+'&email='+encodeURI('<%=email%>');
	
	jQuery(popupDomicilio).load(url);
	
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

function confirmaActualizacionDomicilioAfiliado(){
	 
	jQuery("#<portlet:namespace />divBotonActualizar").hide();
	
	var d_id_domicilio=jQuery("#<portlet:namespace/>id_domicilio").val();
    var d_id_provincia = jQuery("#<portlet:namespace/>provincia").val();
	var d_id_localidad = jQuery("#<portlet:namespace/>localidad").val();
	var d_calle = jQuery("#<portlet:namespace />calle").val();
	var d_numero = jQuery("#<portlet:namespace />numero").val();
	var d_piso = jQuery("#<portlet:namespace />piso").val();
	var d_dpto = jQuery("#<portlet:namespace />dpto").val();
	var d_cod_pos = jQuery("#<portlet:namespace />cod_postal").val();
	var d_barrio = jQuery("#<portlet:namespace />barrio").val();
	var d_cod_area_tel = jQuery("#<portlet:namespace />cod_area_telefono").val();
	var d_telefono = jQuery("#<portlet:namespace />telefono").val();
	var d_cod_area_celu = jQuery("#<portlet:namespace />cod_area_celular").val();
	var d_celular = jQuery("#<portlet:namespace />celular").val();
	var d_email = jQuery("#<portlet:namespace />email").val();
	var d_email_original = jQuery("#<portlet:namespace />email_original").val();
	var cuiltitular= jQuery('#<portlet:namespace />cuil_titular').val();
	var integrante  = jQuery('#<portlet:namespace />inte').val();
	
	/*validamos los campos obligatorios*/
	if (trim(d_calle).length == 0){
		alert("Ingrese la calle del domicilio");
		jQuery('#<portlet:namespace/>calle').focus();
		jQuery("#<portlet:namespace />divBotonActualizar").show();
		return false;
	}
	/* if (trim(d_numero).length == 0){
		alert("Ingrese la altura de la calle del domicilio");
		jQuery('#<portlet:namespace/>numero').focus();
		return false;
	} */
	
	/* Se solicito quitar el 24/05/2016
		if (trim(d_cod_area_celu).length == 0 && trim(d_celular).length == 0){
		alert("Ingrese un cod. de area y número de celular");
		jQuery('#<portlet:namespace/>celular').focus();
		return false;
	}
	
	if (trim(d_email).length == 0){
		alert("Ingrese un correo electrónico");
		jQuery('#<portlet:namespace/>email').focus();
		return false;
	} */
	
	if (
		 (trim(d_cod_area_tel) == '' && trim(d_telefono) != '') ||
		 (trim(d_cod_area_tel) != '' && trim(d_telefono) == '')
		){
		alert("El teléfono debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />telefono').focus();
		jQuery("#<portlet:namespace />divBotonActualizar").show();
		return false;
	}
	
	
	if(trim(d_cod_area_tel).startsWith('0')){
		alert("El código de area del teléfono no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_telefono").focus();
		return false;
	}
	if(trim(d_telefono).startsWith('0')){
		alert("El número del teléfono no debe iniciar con cero");
		jQuery("#<portlet:namespace />telefono").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_tel).length>0 || trim(d_telefono).length>0){
		if(trim(d_cod_area_tel).length+trim(d_telefono).length!=10){
			alert("La longitud del código de área + teléfono debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_telefono").focus();
			return false;
		}
	}
	
	/*
	if ((trim(d_cod_area_laboral) == '' && trim(d_laboral) != '') ||
		(trim(d_cod_area_laboral) != '' && trim(d_laboral) == '')
		){
		alert("El teléfono laboral debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />tel_laboral').focus();
		jQuery("#<portlet:namespace />divBotonActualizar").show();
		return false;
	}
	
	
	if(trim(d_cod_area_laboral).startsWith('0')){
		alert("El código de area laboral no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_tel_laboral").focus();
		return false;
	}
	if(trim(d_laboral).startsWith('0')){
		alert("El número del teléfono laboral no debe iniciar con cero");
		jQuery("#<portlet:namespace />tel_laboral").focus();
		return false;
	}
	
	if(trim(d_cod_area_laboral).length>0 || trim(d_laboral).length>0){
		if(trim(d_cod_area_laboral).length+trim(d_laboral).length!=10){
			alert("La longitud del código de área + teléfono laboral debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_tel_laboral").focus();
			return false;
		}
	}
	*/
	if(trim(d_cod_area_celu).startsWith('0')){
		alert("El código de area del celular no debe iniciar con cero");
		jQuery("#<portlet:namespace />cod_area_celular").focus();
		return false;
	}
	if(trim(d_celular).startsWith('0')){
		alert("El número del celular no debe iniciar con cero");
		jQuery("#<portlet:namespace />celular").focus();
		return false;
	}
	
	
	if(trim(d_cod_area_celu).length>0 || trim(d_celular).length>0){
		if(trim(d_cod_area_celu).length+trim(d_celular).length!=10){
			alert("La longitud del código de área + celular debe de ser de 10 caracteres");
			jQuery("#<portlet:namespace />cod_area_celular").focus();
			return false;
		}
	}
	
	
	if(!<portlet:namespace />validarEmail()){
		return false;
	}
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/actualiza_domicilio';   		       	

	jQuery.post(url,{
					 cuil_titular:cuiltitular,
					 inte:integrante,	 
					 id_domicilio:d_id_domicilio,
					 id_provincia:d_id_provincia,
					 id_localidad:d_id_localidad,
					 calle:d_calle,
					 numero:d_numero,
					 piso:d_piso,
					 departamento:d_dpto,
					 codigo_postal:d_cod_pos,
					 barrio:d_barrio,
					 cod_area_telefono:d_cod_area_tel,
					 telefono:d_telefono,
					 cod_area_celular:d_cod_area_celu,
					 celular:d_celular,
					 email:d_email,
					 email_original:d_email_original,
					 cmd:'save'}, function() {																																											
			if(popupDomicilio!=null){
				jQuery("#<portlet:namespace />divResultadoActualizarOK").show();
				jQuery("#<portlet:namespace />divBotonActualizar").hide();
				Liferay.Popup.close(popupDomicilio); 
			}	 
		});
} 

function buscaSeguimientoCrmContacto(){
	var nro_contacto= jQuery('#<portlet:namespace />numero_contacto').val();
	if(nro_contacto == 0){
		nro_contacto = <%=contacto!=null?contacto.getIdContacto():0 %>	
	}
	
	var params = { "id_contacto": nro_contacto };
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/afiliados/buscar_seguimiento_contacto_crm" /></portlet:renderURL>';
	
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/cai/buscar_seguimiento_contacto_crm" /></portlet:renderURL>';
    </c:if>
    
    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>
	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/judicial/buscar_seguimiento_contacto_crm" /></portlet:renderURL>';
    </c:if>
	
	jQuery('#<portlet:namespace />seguimiento_contacto').show(); 
	jQuery('#<portlet:namespace />seguimiento_contacto').load(url, params, function() {
    							/* jQuery('#<portlet:namespace />buscando').hide();  */           															
    					  }
    );	
}


function <portlet:namespace />proponeDescripcionyCierre(){
	var tipoContacto = jQuery('#<portlet:namespace />tipo_contacto').val();
	var idMotivo = jQuery('#<portlet:namespace />motivo_contacto').val();
	var estado = jQuery('#<portlet:namespace />estado_contacto').val();
/* 	1	"LLAMADO ENTRANTE"
2	"LLAMADO SALIENTE" */

	if (<%=esCAI || esCRM%> && (tipoContacto == 1 || tipoContacto == 2) && estado != '<%= ContactoCRM.ESTADOS.DERIVADO%>'){
	<% for (MotivoContacto mc : motivosCrm) { %>
	
		if(idMotivo == <%=mc.getId()%>) {
			
			jQuery('#<portlet:namespace />descripcion_contacto').val("<%=StringUtils.checkNotEmpty(mc.getDescripcionPredeterminada())?mc.getDescripcionPredeterminada():""%>");
			<%-- jQuery('#<portlet:namespace />comentarios_contacto').val("<%=StringUtils.checkNotEmpty(mc.getCierrePredeterminado())?mc.getCierrePredeterminado().substring(0, 150):""%>"); --%>
			
		}
	<%}%>
	
	var params = { "idMotivo": idMotivo };
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/afiliados/buscar_comentario_cierre_predet" /></portlet:renderURL>';
	
	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/cai/buscar_comentario_cierre_predet" /></portlet:renderURL>';
    </c:if>
    
    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_JUD_1_"))%>'>
	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/judicial/buscar_comentario_cierre_predet" /></portlet:renderURL>';
    </c:if>
	
/*     jQuery('#<portlet:namespace />divComentarioCierrePredet').show(); */
	jQuery('#<portlet:namespace />divComentarioCierrePredet').load(url, params, function() {
		/* jQuery('#<portlet:namespace />divComentarioCierre').hide();  */ 
     	}
    );		
	}
	
	
	
}

</script>	
