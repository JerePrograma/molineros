<%@ include file="/html/portlet/crm/init.jsp"%>
<%@page import="ar.com.ospim.util.StringUtils"%>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	PortletURL portletURL = renderResponse.createRenderURL();
	/* String viewStr = (String)request.getAttribute("view"); */
	String accion = (String) request.getAttribute(Constants.CMD);

	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	ContactoCRM contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
	
	List<MotivoContacto> motivosCrm = (List<MotivoContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS);
	List<CategoriaContacto> categoriasCrm = (List<CategoriaContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_CATEGORIAS);
	List<TipoContacto> tiposCrm = (List<TipoContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_TIPOS);

	Calendar desdeFecha = Calendar.getInstance();
	Calendar hastaFecha = Calendar.getInstance();
	/*desdeFecha.set(Calendar.MONTH, desdeFecha.get(Calendar.MONTH) -1);*/
	desdeFecha.set(Calendar.DATE, 1);
	
%>

<fieldset class="block-labels"><legend><liferay-ui:message key="crm-contacto" /></legend>

	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		<tr>
			<td colspan="1"><label><liferay-ui:message key="crm-contacto-nro" /></label>&nbsp;&nbsp;
			<input id="<portlet:namespace />numero_contacto"
				name="<portlet:namespace />numero_contacto" size="20"
				maxlength="100" type="text"  
				value="<%=contacto != null ? String.valueOf(contacto.getIdContacto()) : ""%>" 
				readonly="readonly" />
				&nbsp;&nbsp;<b><%=contacto != null && contacto.getImportancia() > 0 ? String.valueOf(contacto.getImportanciaDescripcion()) : ""%></b>	
			</td>
			<td colspan="1"><label><liferay-ui:message key="crm-contacto-relac" />:</label>
			<input id="<portlet:namespace />relacionado_con_contacto"
				name="<portlet:namespace />relacionado_con_contacto" size="20"
				maxlength="100" type="text"  
				value="<%=contacto!=null && contacto.getIdCrmRelacionado()!=null ? String.valueOf(contacto.getIdCrmRelacionado()) : ""%>" 
				<% if (esView) { %> disabled="disabled" <%} %> />
			</td>
		</tr>
		<tr>
			<td colspan="2">
			   <%if(contacto.getAfiliado()!=null){ %>
				<fieldset class="block-labels"><legend><liferay-ui:message key="Datos para Contacto del Afiliado" /></legend>
				   
					<table class="lfr-table">
						<tr>
							<td><label><liferay-ui:message key="cuil-titular" />:</label>&nbsp;<%=contacto.getAfiliado().getCuil_titular()%></td>
							<td><label><liferay-ui:message key="integrante"   />:</label>&nbsp;<%=contacto.getAfiliado().getInteAsString()%></td>
						</tr>
						<tr>
							<td><label><liferay-ui:message key="apellido" />:</label>&nbsp;<%=contacto.getAfiliado().getApellido()%> </td>
							<td><label><liferay-ui:message key="nombre"   />:</label>&nbsp;<%=contacto.getAfiliado().getNombre()%> </td>
						</tr>
						<tr>
							<td><label><liferay-ui:message key="contacto-e" />:</label>&nbsp;<%=contacto.getAfiliado().getEmail()!=null?contacto.getAfiliado().getEmail():""%> </td> 
							<td><label><liferay-ui:message key="contactos-telefonicos" />:</label>&nbsp;<%=contacto.getAfiliado().getDomicilioDefault()!=null
																									&&contacto.getAfiliado().getDomicilioDefault().getTelefono()!=null?
																											contacto.getAfiliado().getDomicilioDefault().getTelefono():""%> </td>			
						</tr>
					</table>
				</fieldset>
			  	<%} %>	
			  	<%if(contacto.getContactoSeccional() !=null){ %>
			  	  <fieldset class="block-labels"><legend><liferay-ui:message key="Datos para Contacto Seccional" /></legend>
			  	      <table class="lfr-table">
						<tr>
						    <td><label><liferay-ui:message key="seccional" />:</label>&nbsp;<%=contacto.getContactoSeccional().getSeccional().getDescripcion() %> </td>
							<td><label><liferay-ui:message key="Cargo" />:</label>&nbsp;<%=contacto.getContactoSeccional().getCargoDescripcion() %> </td>
							<td><label><liferay-ui:message key="nombre"   />:</label>&nbsp;<%=contacto.getContactoSeccional().getNombreApe() %> </td>
						</tr>
					  </table>	
			  	  </fieldset>
			  	<%} %>
			  	<%if(contacto.getNoAfiliado()!=null){ %>
				<fieldset class="block-labels"><legend><liferay-ui:message key="Datos para Contacto de la Persona" /></legend>
				   
					<table class="lfr-table">
						<tr>
							<td><label><liferay-ui:message key="tipo-documento" />:</label>&nbsp;<%=contacto.getNoAfiliado().getDocumentoTipo()%></td>
							<td><label><liferay-ui:message key="nro-documento"   />:</label>&nbsp;<%=contacto.getNoAfiliado().getDocumentoNumero()%></td>
						</tr>
						<tr>
							<td><label><liferay-ui:message key="apellido" />:</label>&nbsp;<%=contacto.getNoAfiliado().getApellido()%> </td>
							<td><label><liferay-ui:message key="nombre"   />:</label>&nbsp;<%=contacto.getNoAfiliado().getNombre()%> </td>
						</tr>
						<tr>
							<td><label><liferay-ui:message key="contacto-e" />:</label>&nbsp;<%=contacto.getNoAfiliado().getEmail()!=null?contacto.getNoAfiliado().getEmail():""%> </td> 
							<td><label><liferay-ui:message key="contactos-telefonicos" />:</label>&nbsp;<%=contacto.getNoAfiliado().getTelefono()!=null?
																									contacto.getNoAfiliado().getTelefono():""%> </td>			
						</tr>
					</table>
				</fieldset>
			  	<%} %>	
			</td>			
		<tr>
			<td colspan="2">
			<fieldset class="block-labels">
				<%if(!esView){ %>		
				<legend><liferay-ui:message key='nuevo-contacto' /></legend>
				<%} %>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-tipo" />:</label></td>
						<td><select name="<portlet:namespace />tipo_contacto"
								id="<portlet:namespace />tipo_contacto" <% if (esView) { %> disabled="disabled" <%} %>>			
								<option value="0">Seleccione un tipo de contacto</option>
								<% for(TipoContacto tc : tiposCrm) {%>
									<option value="<%=tc.getId() %>"  
									<%if (contacto != null && contacto.getTipo().getId() == tc.getId()) { %> selected="selected" <%}%>><%=tc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td><label><liferay-ui:message key="crm-contacto-categoria" />:</label></td>
						<td><select name="<portlet:namespace />categoria_contacto"
								id="<portlet:namespace />categoria_contacto" <% if (esView) { %> disabled="disabled" <%} %>>			
								<option value="0">Seleccione una categoría de contacto</option>
								<% for(CategoriaContacto cc : categoriasCrm) {%>
									<option value="<%=cc.getId() %>"  
									<%if (contacto != null && contacto.getCategoria().getId() == cc.getId()) { %> selected="selected" <%}%>><%=cc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-motivo" />:</label></td>
						<td><select name="<portlet:namespace />motivo_contacto"
								id="<portlet:namespace />motivo_contacto" <% if (esView) { %> disabled="disabled" <%} %>>
								<option value="0">Seleccione un motivo de contacto</option>			
								<% for(MotivoContacto mc : motivosCrm) {%>
									<option value="<%=mc.getId() %>"  
									<%if (contacto != null && contacto.getMotivo().getId() == mc.getId()) { %> selected="selected" <%}%>><%=mc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td><label><liferay-ui:message key="crm-contacto-estado" />:</label></td>
						<td><select name="<portlet:namespace />estado_contacto"
								id="<portlet:namespace />estado_contacto" <% if (esView) { %> disabled="disabled" <%} %>
								onchange="javascript:<portlet:namespace />mostrarUsuarioDerivacion();" >
								<option value="0">Seleccione un estado de contacto</option>			
								<% for(int i = 0; i < WebKeysCrm.CRM_ESTADOS.length; i++){ %>
									<option value="<%=WebKeysCrm.CRM_ESTADOS[i] %>" 
										<% if (contacto != null && (WebKeysCrm.CRM_ESTADOS[i].equalsIgnoreCase(contacto.getEstado().name()) ) ) { %> selected="selected" <%}%> ><%=WebKeysCrm.CRM_ESTADOS[i]%></option>
								<%} %>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="4">
							<div align="center" id="<portlet:namespace />divUsuarioDerivacion" >
								<liferay-util:include page="/html/portlet/crm/derivacion_liferay.jsp">
									<liferay-util:param name="view" value="<%=esView?new String(\"true\"):new String(\"false\") %>"/>
								</liferay-util:include>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-descripcion" />:</label></td>
						<td colspan="3"><textarea rows="6" cols="100" maxlength="20000" 
									id="<portlet:namespace />descripcion_contacto" 
									name="<portlet:namespace />descripcion_contacto"
									style="resize: none;" 
									<% if (esView) { %> disabled="disabled" <%} %> ><%= contacto!=null?contacto.getDescripcion():""%></textarea>
						</td>			
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
		<%if(esView && contacto.getSeguimiento() != null && contacto.getSeguimiento().size() > 0 ){ %>		
		<tr>
			<td colspan="2">
				<div style="height:100px; overflow: scroll; overflow-x: hidden;"> 
				<fieldset class="block-labels">
					<legend><liferay-ui:message key='seguimiento-contacto' /></legend>
					<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
						<%for(DerivacionSeguimiento seguim: contacto.getSeguimiento()) {%>
							<tr>
								<td colspan="2"><label><liferay-ui:message key="crm-seguim-contacto-alta-edi-sec-usu" />:</label>
								<%=(seguim.getDerivacionEdificio()+"/"+seguim.getDerivacionSector()+"/"+seguim.getDerivacionUsr()).trim() %></td>
								<td colspan="2"><label><liferay-ui:message key="crm-seguim-contacto-alta-fec" />:</label>
								<%=sdf2.format(seguim.getAltaFecha())%></td>
							</tr>
							<tr>
								<td colspan="4">
									<textarea rows="1" cols="100" 
									id="<portlet:namespace />descripcion_contacto" 
									name="<portlet:namespace />descripcion_contacto"
									style="resize: none;" 
									<% if (esView) { %> disabled="disabled" <%} %> ><%= seguim!=null?seguim.getObservaciones():""%></textarea>
								</td>
							</tr>	
						<%} %>
					</table>	
				</fieldset>
				</div>	
			</td>
		</tr>
		<%} %>	
	</table>		
</fieldset>

<script type="text/javascript" >
<%if(contacto!= null && contacto.getImportancia()>0){%>
jQuery("#<portlet:namespace/>importancia").attr('checked', 'checked');  
<%}%>
</script>	
