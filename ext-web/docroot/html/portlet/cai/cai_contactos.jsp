<%@ include file="/html/portlet/cai/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
 		boolean showABMCrm = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM);
 		boolean showABMCrmLegales = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM_LEGALES);
	
 		session.removeAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
 		session.removeAttribute("cmd");	
		
 		List<Organization> organizaciones = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		Organization orgLocal = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId()).get(0);
		/* Para obtener el remitente  */
	 	List<EmpresaLiferay> esu = null; /* EmpresaSectorUsuarioServiceUtil.getEmpresasSectoresUsuarios();	*/
	 	esu = (ArrayList<EmpresaLiferay>) portletSession
	 		   .getAttribute(WebKeysCorrespondencia.EMPRESA_SECTOR_USUARIOS_LIFERAY_EN_SESSION,
	 			   		PortletSession.APPLICATION_SCOPE);
	 	String portlet_name = ParamUtil.getString(request, "portlet_name");
		if (portlet_name == null || portlet_name.trim().equals("")){
		    portlet_name = "CAI";
		}
%>


<div style="display: table; vertical-align: top;">
		<div id="<portlet:namespace />divContenedor" style="display: table-row;">
			<div id="F1_C1" style="display: table-cell;">
				<fieldset>
			        <legend><liferay-ui:message key="crm-selec-ingreso" /></legend>
			        <label>
			            <input type="radio" name="<portlet:namespace />tipoContactoClase"  id="<portlet:namespace />tipoContactoClase"
			            	value="afi" checked="checked" onclick="<portlet:namespace />manejarTipoContactoClase();"> Afiliado
			        </label>
			        <label>
			            <input type="radio" name="<portlet:namespace />tipoContactoClase" id="<portlet:namespace />tipoContactoClase"
			            	value="sec" onclick="<portlet:namespace />manejarTipoContactoClase();"> Seccional
			        </label>
			        <label>
			            <input type="radio" name="<portlet:namespace />tipoContactoClase" id="<portlet:namespace />tipoContactoClase" 
			            	value="pre" onclick="<portlet:namespace />manejarTipoContactoClase();"> Prestador
			        </label>
			        <label>
			            <input type="radio" name="<portlet:namespace />tipoContactoClase" id="<portlet:namespace />tipoContactoClase" 
			            	value="emp" onclick="<portlet:namespace />manejarTipoContactoClase();"> Empresa
			        </label>
			        <label>
			            <input type="radio" name="<portlet:namespace />tipoContactoClase" id="<portlet:namespace />tipoContactoClase" 
			            	value="com" onclick="<portlet:namespace />manejarTipoContactoClase();"> Compañero
			        </label>
			    </fieldset>
			</div>
		</div>
		<div id="<portlet:namespace />divAfiliado" style="display: table-row;">
			<div id="F2_C1" style="display: table-cell;">
<%-- 				<fieldset class="block-labels">
					<legend>
						<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />
					</legend>	
					<div id="F2_C1_1" style="display: table-row;">
						<div id="F2_C1_1_1" style="display: table-cell;">
							<label><liferay-ui:message key="entidad" />:</label>
						</div>
						<div id="F2_C1_1_2" style="display: table-cell;">
							<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">
									<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
										<c:if test="<%=((entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_OSPIM)) ||
														(entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)) ||
														(entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)))%>">									
											<option value="<%= entidad %>"><%=entidad%></option>
										</c:if>
									<%}%>
							</select>
						</div>
						<div id="F2_C1_1_3" style="display: table-cell;">
							<label><liferay-ui:message key="numero-afi" />:</label>
						</div>
						<div id="F2_C1_1_4" style="display: table-cell;">
							<input id="<portlet:namespace />numero_afi" name="<portlet:namespace />numero_afi" size="6" maxlength="10" type="text" value=""/>
							<input id="<portlet:namespace />numero_socio_prev" name="<portlet:namespace />numero_socio_prev" type="hidden" value=""/>
							<input id="<portlet:namespace />numero_credencial_prev" name="<portlet:namespace />numero_credencial_prev" type="hidden" value=""/>
						</div>
					</div>
					<div id="F2_C1_2" style="display: table-row;">
						<div id="F2_C1_2_1" style="display: table-cell;">
							<label><liferay-ui:message key="cuil" />:</label>
						</div>
						<div id="F2_C1_2_2" style="display: table-cell;">
							<input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="13" maxlength="11" type="text" value="" />
						</div>
						<div id="F2_C1_2_3" style="display: table-cell;">
							<label><liferay-ui:message key="integrante" />:</label>
						</div>
						<div id="F2_C1_2_4" style="display: table-cell;">
							<input id="<portlet:namespace />inte" name="<portlet:namespace />inte" size="2" maxlength="2" type="text" value="" />
						</div>
						<div id="F2_C1_2_5" style="display: table-cell;">
							<label><liferay-ui:message key="tipo-documento" />:</label>
						</div>
						<div id="F2_C1_2_6" style="display: table-cell;">
							<select name="<portlet:namespace/>tipoDoc" id="<portlet:namespace/>tipoDoc">
										<option value=""></option>
										<% for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) { %>
											<option value="<%= tipoDoc %>"><%=tipoDoc%></option>
										<%}%>
							</select>
						</div>
						<div id="F2_C1_2_7" style="display: table-cell;">
							<label><liferay-ui:message key="nro-documento" />:</label>
						</div>
						<div id="F2_C1_2_8" style="display: table-cell;">
							<input id="<portlet:namespace />nroDoc" name="<portlet:namespace />nroDoc" size="9" maxlength="8" type="text" value="" />
						</div>
						<div id="F2_C1_2_9" style="display: table-cell;">
							<label><liferay-ui:message key="seccional" />:</label>
						</div>
						<div id="F2_C1_2_10" style="display: table-cell;">
							<liferay-util:include page="/html/portlet/autorizaciones/busqueda_seccional.jsp"/>
						</div>
					</div>
					<div id="F2_C1_3" style="display: table-row;">
						<div id="F2_C1_3_1" style="display: table-cell;">
							<label><liferay-ui:message key="apellido" />:</label>
						</div>
						<div id="F2_C1_3_2" style="display: table-cell;">
							<input id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" size="20" maxlength="100" type="text" value="" />
						</div>
						<div id="F2_C1_3_3" style="display: table-cell;">
							<label><liferay-ui:message key="nombre" />:</label>
						</div>
						<div id="F2_C1_3_4" style="display: table-cell;">
							<input id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" size="20" maxlength="100" type="text" value="" />
							<input id="<portlet:namespace />libro" name="<portlet:namespace />libro" type="hidden" value="" />
						</div>
						<div id="F2_C1_3_5" style="display: table-cell;">
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						</div>
						<div id="F2_C1_3_6" style="display: table-cell;">
							<input type="button" value="<liferay-ui:message key="nuevo-contacto-noafi" />" onClick="javascript:nuevoContactoNoAfi();" />
						</div>
						<div id="F2_C1_3_7" style="display: table-cell;">						
							<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" type="button" onClick='javascript:<portlet:namespace />limpiarCampos()'/>							
						</div>						
					</div>
					<div id="F2_C1_4" style="display: table-row; width: 100%;">
						<div id="F2_C1_4_1" style="display: table-cell;">
							(<liferay-ui:message key="refine-busqueda" />)
						</div>
					</div>
				</fieldset>	 --%>
				<fieldset class="block-labels">
					<legend>
						<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />
					</legend>				
					<table class="lfr-table"> 
						<tr>
							<td><label><liferay-ui:message key="entidad" />:</label></td>
							<td>
								<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">
										<%for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {	%>
											<c:if test="<%=((entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_OSPIM)) ||
															(entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_AMTIMA)) ||
															(entidad.equalsIgnoreCase(WebKeysGlobal.ENTIDAD_UOMA)))%>">									
												<option value="<%= entidad %>"><%=entidad%></option>
											</c:if>
										<%}%>
								</select>
							</td>
							<td>&nbsp;</td>
							<td><label><liferay-ui:message key="numero-afi" />:</label></td>
							<td><input id="<portlet:namespace />numero_afi" name="<portlet:namespace />numero_afi" size="6" maxlength="10" type="text" value=""/></td>
							<%-- <td><label><liferay-ui:message key="nro-socio-prevencion" />:</label></td>
							<td><input id="<portlet:namespace />numero_socio_prev" name="<portlet:namespace />numero_socio_prev" size="6" maxlength="5" type="text" value=""/></td>
							<td><label><liferay-ui:message key="nro-credencial-prevencion" />:</label></td>
							<td><input id="<portlet:namespace />numero_credencial_prev" name="<portlet:namespace />numero_credencial_prev" size="8" maxlength="10" type="text" value=""/></td> --%>
							<td colspan="4">
								<input id="<portlet:namespace />numero_socio_prev" name="<portlet:namespace />numero_socio_prev" type="hidden" value=""/>
								<input id="<portlet:namespace />numero_credencial_prev" name="<portlet:namespace />numero_credencial_prev" type="hidden" value=""/>
							</td>
						</tr>
						<tr>
							<td colspan="12">&nbsp;</td>
						</tr>
						<tr>
							<td><label><liferay-ui:message key="cuil" />:</label></td>
							<td><input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="13" maxlength="11" type="text" value="" /></td>
							<td>&nbsp;</td>
								<td><label><liferay-ui:message key="integrante" />:</label></td>
								<td><input id="<portlet:namespace />inte" name="<portlet:namespace />inte" size="2" maxlength="2" type="text" value="" /></td>
								<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
								<td>
									<select name="<portlet:namespace/>tipoDoc" id="<portlet:namespace/>tipoDoc">
											<option value=""></option>
											<%
												for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
											%>
												<option value="<%= tipoDoc %>"><%=tipoDoc%></option>
											<%
											}
											%>
									</select>
								</td>
								<td><label><liferay-ui:message key="nro-documento" />:</label></td>
								<td><input id="<portlet:namespace />nroDoc" name="<portlet:namespace />nroDoc" size="9" maxlength="8" type="text" value="" /></td>
								<td><label><liferay-ui:message key="seccional" />:</label></td>
								<td colspan="2" rowspan="3" style="vertical-align:top" >
									<liferay-util:include page="/html/portlet/autorizaciones/busqueda_seccional.jsp"/>
								</td>
						</tr>
						<tr>
							<td colspan="12">&nbsp;</td>
						</tr>
						<tr>
								<td><label><liferay-ui:message key="apellido" />:</label></td>
								<td colspan="2"><input id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" size="20" maxlength="100" type="text" value="" /></td>
								<td><label><liferay-ui:message key="nombre" />:</label></td>
								<td colspan="2"><input id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" size="20" maxlength="100" type="text" value="" /></td>
								<td><input id="<portlet:namespace />libro" name="<portlet:namespace />libro" type="hidden" value="" /></td>
							<td>&nbsp;</td>
							
							<td colspan="4">							
								<input id="<portlet:namespace />buscarAfi" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
							    &nbsp;&nbsp;
								<input type="button" value="<liferay-ui:message key="nuevo-contacto-noafi" />" onClick="javascript:nuevoContactoNoAfi();" />
								
								&nbsp;&nbsp;						
								<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" type="button" onClick='javascript:<portlet:namespace />limpiarCamposAfi()'/>							
							</td>						
						</tr>
						<tr>
							<td colspan="12">
								&nbsp;(<liferay-ui:message key="refine-busqueda" />)
							</td>
						</tr>
					</table>	      	  
			</fieldset>
				<fieldset class="block-labels">
					<div align="center" id="<portlet:namespace />buscandoAfi" style="display: table-row;">
						<div id="F2_C1_5_1" style="display: table-cell;">
							<liferay-ui:message key="buscando"/>
						</div>
						<div id="F2_C1_5_2" style="display: table-cell;">
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</div>		
					</div>	
					
					<div align="center" id="<portlet:namespace />busquedaAfiliadoDiv" style="display: table-row;"></div>
				</fieldset> 	
			</div>
		</div>
		<div id="<portlet:namespace />divSeccional" style="display: table-row;">
			<div id="F3_C1" style="display: table-cell;">
				<!-- Contactos Seccional  -->			
				
					<div id="F3_C1_1" style="display: table-row;">
						<div id="F3_C1_1_1" style="display: table-cell;">
							<fieldset class="block-labels">
							<legend>
								<label><liferay-ui:message key="crm-ingreso-secc" /></label>
							</legend>	 		
							<table class="lfr-table"> 
								
								<tr>
									<td><label><liferay-ui:message key="seccional" />:</label></td>
									<td colspan="2" rowspan="3" style="vertical-align:top" >
										<liferay-util:include page="/html/portlet/autorizaciones/busqueda_seccional.jsp">
										   <liferay-util:param name="prefijo" value='_sec'/>
										</liferay-util:include>
									</td>
									<td><label><liferay-ui:message key="nombre" />:</label></td>
									<td colspan="2"><input id="<portlet:namespace />nombre_s" name="<portlet:namespace />nombre_s" size="60" maxlength="100" type="text" value="" /></td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td colspan="7">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="7">							
										<input id="<portlet:namespace />buscar_s" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
									    &nbsp;&nbsp;
										<input id="<portlet:namespace />limpiar-campos-s" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" type="button" onClick='javascript:<portlet:namespace />limpiarCamposSeccional()'/>							
									</td>						
								</tr>
							</table>
							</fieldset>
						</div>
					</div>	
				</div>
			</div>
			<div align="center" id="<portlet:namespace />busquedaPersonalSeccionalDiv" style="display: table-row;">
				<!-- <fieldset class="block-labels">
					<liferay-util:include page="/html/portlet/cai/personal_seccional_search_result.jsp"/>
				</fieldset> -->
			</div>	      	  
			
	    <div id="<portlet:namespace />divPrestador" style="display: table-row;">
			<div id="F4_C1" style="display: table-cell; padding: 5px;">
				<div id="F4_C1_" style="display: table-row;">
					<div id="F4_C1_1" style="display: table-cell;">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="busqueda-prestadores" />
							</legend>
							 <div id="F4_C1_1_2" style="display: table-row; ">
								<div id="F4_C1_1_2_1" style="display: table-cell; padding: 5px;">
									<label><liferay-ui:message key="cod-prestador" />:</label>
								</div>
								<div id="F4_C1_1_2_2" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />id_prestador"
										name="<portlet:namespace />id_prestador" type="text" maxlength="6" size="6" value="" />
								</div>
								<div id="F4_C1_1_2_3" style="display: table-cell; padding: 5px;">
									<label><liferay-ui:message key="cuit" />:</label>
								</div>
								<div id="F4_C1_1_2_4" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />cuit"
										name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" />
								</div>
								<div id="F4_C1_1_2_5" style="display: table-cell; padding: 5px;">
									<label><liferay-ui:message key="descripcion" />:</label>
								</div>
								<div id="F4_C1_1_2_6" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />descripcion"
										name="<portlet:namespace />descripcion" size="50" maxlength="50" type="text" value="" />
								</div>
								<div id="F4_C1_1_2_7" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />buscarPrest" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
								</div>
								<div id="F4_C1_1_2_8" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />limpiar-campos-prest" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" 
									type="button" onClick='javascript:<portlet:namespace />limpiarCamposPrest()'/>
								</div>
							 </div>
						</fieldset>
					</div>
			</div>
					<div align="center" id="<portlet:namespace />buscandoPrest" style="display: table-row;">
						
							<div id="F4_C1_2_1_1" style="display: table-cell;">
								<liferay-ui:message key='buscando' />
							</div>
							<div id="F4_C1_2_1_2" style="display: table-cell;">
								<img alt="<liferay-ui:message key='buscando'/>"
										src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</div>
					</div>
					<fieldset class="block-labels">
							<div align="center" id="<portlet:namespace />busquedaPrestadorDiv" style="display: table-row;">
							</div>
					</fieldset>
				
			</div>
		</div>		
		<div id="<portlet:namespace />divEmpresa" style="display: table-row;">
			<div id="F5_C1" style="display: table-cell; padding: 5px;">
				<div id="F5_C1_" style="display: table-row;">
					<div id="F5_C1_1" style="display: table-cell;">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="grupo-filtro-busqueda-empleadores" />
							</legend>
							 <div id="F5_C1_1_2" style="display: table-row; ">
								<div id="F5_C1_1_2_1" style="display: table-cell; padding: 5px;">
									<label><liferay-ui:message key="cuit" />:</label>
								</div>
								<div id="F5_C1_1_2_2" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />cuit_e" name="<portlet:namespace />cuit_e" size="13" maxlength="11" type="text" value="" />
								</div>
								<div id="F5_C1_1_2_3" style="display: table-cell; padding: 5px;">
									<label><liferay-ui:message key="sucursal" />:</label>
								</div>
								<div id="F5_C1_1_2_4" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />sucursal_e" name="<portlet:namespace />sucursal_e" size="5" maxlength="6" type="text" value="" />
								</div>
								<div id="F5_C1_1_2_5" style="display: table-cell; padding: 5px;">
									<label><liferay-ui:message key="descripcion" />:</label>
								</div>
								<div id="F5_C1_1_2_6" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />descripcion_e" name="<portlet:namespace />descripcion_e" size="50" maxlength="60" type="text" value="" />
								</div>
								<div id="F5_C1_1_2_7" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />buscarEmp" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
								</div>
								<div id="F5_C1_1_2_8" style="display: table-cell; padding: 5px;">
									<input id="<portlet:namespace />limpiar-campos-emp" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" 
									type="button" onClick='javascript:<portlet:namespace />limpiarCamposEmp()'/>
								</div>
							 </div>
						</fieldset>
					</div>
				</div>
				<div align="center" id="<portlet:namespace />buscandoEmp" style="display: table-row;">
						<div id="F5_C1_2_1_1" style="display: table-cell;">
							<liferay-ui:message key='buscando' />
						</div>
						<div id="F5_C1_2_1_2" style="display: table-cell;">
							<img alt="<liferay-ui:message key='buscando'/>"
									src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</div>
				</div>
				<fieldset class="block-labels">
						<div align="center" id="<portlet:namespace />busquedaEmpleadorDiv" style="display: table-row;">
						</div>
				</fieldset>
			</div>
		</div>		
		<div id="<portlet:namespace />divCompaniero" style="display: table-row;">
			<!-- <div id="F6_C1" style="display: table-cell;">
			</div> -->
			<%-- <div id="<portlet:namespace />divBuscarUsuario"> --%>		
			<fieldset class="block-labels"><legend><liferay-ui:message key='crm-ingreso-usu' /></legend>	
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key='edificio' />:</label></td>
						<td><select name="<portlet:namespace/>edificio_destino"
							id="<portlet:namespace/>edificio_destino"  onchange="javascript:filtrarGrupos();" 
							onclick="javascript:filtrarGrupos();" onfocus="javascript:filtrarGrupos();" >			
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
						<td><label><liferay-ui:message key='sector' />:</label></td>
						<td><select name="<portlet:namespace/>sector_destino"
							id="<portlet:namespace/>sector_destino" 
							onchange="javascript:filtrarUsuarios(false, 'Entrada');" 
							onclick="javascript:filtrarUsuarios(false, 'Entrada');" 
							onfocus="javascript:filtrarGrupos();">			
							<option value="">Seleccione un sector</option>
							
						</select></td>
						<td><label><liferay-ui:message key='usuario' />:</label></td>
						<td><select name="<portlet:namespace/>usuario_destino"
							id="<portlet:namespace/>usuario_destino">						
							<option value="">Seleccione un usuario</option>
								
						</select></td>
						<td>&nbsp;</td>
						<td><input type="button" value="<liferay-ui:message key="Crear contacto" />" onClick="javascript:nuevoContactoUsuario();" /></td>
					</tr>
				</table>
			</fieldset>			
		</div>
</div>

	
			
<script type="text/javascript">

<portlet:namespace />manejarTipoContactoClase();

	function <portlet:namespace />manejarTipoContactoClase(){
		
		/* var tipoClaseSel = jQuery('#<portlet:namespace />tipoContactoClase').val(); */
		var tipoClaseSel = jQuery("input[name='<portlet:namespace />tipoContactoClase']:checked").val();

		if(tipoClaseSel == "afi"){

			jQuery('#<portlet:namespace />divAfiliado').show();
			jQuery('#<portlet:namespace />divSeccional').hide();
			jQuery('#<portlet:namespace />busquedaPersonalSeccionalDiv').hide();
			jQuery('#<portlet:namespace />divPrestador').hide();
			jQuery('#<portlet:namespace />divEmpresa').hide();
			jQuery('#<portlet:namespace />divCompaniero').hide();
			
		}
		
		if(tipoClaseSel == "sec"){

			jQuery('#<portlet:namespace />divAfiliado').hide();
			jQuery('#<portlet:namespace />divSeccional').show();
			jQuery('#<portlet:namespace />busquedaPersonalSeccionalDiv').show();
			jQuery('#<portlet:namespace />divPrestador').hide();
			jQuery('#<portlet:namespace />divEmpresa').hide();
			jQuery('#<portlet:namespace />divCompaniero').hide();
			
		}
		
		if(tipoClaseSel == "pre"){

			jQuery('#<portlet:namespace />divAfiliado').hide();
			jQuery('#<portlet:namespace />divSeccional').hide();
			jQuery('#<portlet:namespace />busquedaPersonalSeccionalDiv').hide();
			jQuery('#<portlet:namespace />divPrestador').show();
			jQuery('#<portlet:namespace />divEmpresa').hide();
			jQuery('#<portlet:namespace />divCompaniero').hide();
			
		}
		
		if(tipoClaseSel == "emp"){

			jQuery('#<portlet:namespace />divAfiliado').hide();
			jQuery('#<portlet:namespace />divSeccional').hide();
			jQuery('#<portlet:namespace />busquedaPersonalSeccionalDiv').hide();
			jQuery('#<portlet:namespace />divPrestador').hide();
			jQuery('#<portlet:namespace />divEmpresa').show();
			jQuery('#<portlet:namespace />divCompaniero').hide();
			
		}
		
		if(tipoClaseSel == "com"){

			jQuery('#<portlet:namespace />divAfiliado').hide();
			jQuery('#<portlet:namespace />divSeccional').hide();
			jQuery('#<portlet:namespace />busquedaPersonalSeccionalDiv').hide();
			jQuery('#<portlet:namespace />divPrestador').hide();
			jQuery('#<portlet:namespace />divEmpresa').hide();
			jQuery('#<portlet:namespace />divCompaniero').show();
			
		}
	}

	jQuery('#<portlet:namespace />buscandoAfi').hide();	
	var url = "<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/buscar_afiliados_sesion";
	url += "&portlet_name=CAI";
	
	jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
	
	jQuery('#<portlet:namespace />buscarAfi').click(function(){		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();		
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();		
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();		
		var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
		var seccional_nombre=jQuery('#<portlet:namespace />seccional').val();
		var apellido=jQuery('#<portlet:namespace />apellido').val();		
		var nombre=jQuery('#<portlet:namespace />nombre').val();		
		var entidad=jQuery('#<portlet:namespace />entidad').val();		
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		var libro=jQuery('#<portlet:namespace />libro').val();
		var formulario=jQuery('#<portlet:namespace />formNro').val();
		var nroSocioPrev =jQuery('#<portlet:namespace />numero_socio_prev').val();
		var nroCredencialPrev =jQuery('#<portlet:namespace />numero_credencial_prev').val();

			if(!<portlet:namespace />validarBusquedaAfi(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi,nroSocioPrev,nroCredencialPrev)){
				return false;
			}
		
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil').focus();
				return false;
			}
		}
		
		jQuery('#<portlet:namespace />buscandoAfi').show();
		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/buscar_afiliados';
		    
		var paramsAfi = {"cuil" : cuil, "inte" : inte, "tipoDoc" : tipoDoc, "nroDoc" : escape(nroDoc), "seccional" : seccional, 
						"nombre" : nombre, "apellido" : apellido, "entidad" : entidad, "numero_afi" : numero_afi,
						"nroSocioPrevencion" : nroSocioPrev, "nroCredencialPrevencion" : nroCredencialPrev,"portlet_name":"CAI"};

		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, paramsAfi, function() {
			jQuery('#<portlet:namespace />buscandoAfi').hide();            															
		  });
	});
	
	function <portlet:namespace/>buscaGrupo(cuil){
		jQuery('#<portlet:namespace />buscandoAfi').show();				
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/buscar_afiliados&cuil='+cuil;		
		//jQuery('#userlist').remove();
        jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscandoAfi').hide();            															
        															  }
        );			
	}
	
	function <portlet:namespace />validarBusquedaAfi(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi, nroSocioPrev, nroCredPrev){
		if(trim(cuil.length)==0 && trim(inte.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 && trim(seccional.length)==0 &&  
		   trim(apellido.length)==0 && trim(nombre.length)==0 && trim(entidad.length)==0 && trim(numero_afi.length)==0
		   && trim(nroSocioPrev.length)==0 && trim(nroCredPrev.length)==0 ){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}
	
	function <portlet:namespace />limpiarCamposAfi(){
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		jQuery('#<portlet:namespace />tipoDoc').val('');
		jQuery('#<portlet:namespace />nroDoc').val('');
		<% if(seccionalFijada==0){%>
			jQuery('#<portlet:namespace />id_seccional').val('');		
			jQuery('#<portlet:namespace />seccional').val('');
		<%}%>
		jQuery('#<portlet:namespace />apellido').val('');
		jQuery('#<portlet:namespace />nombre').val('');
		jQuery('#<portlet:namespace />entidad').val('');
		jQuery('#<portlet:namespace />numero_afi').val('');
		jQuery('#<portlet:namespace />numero_socio_prev').val('');
		jQuery('#<portlet:namespace />numero_credencial_prev').val('');
	}

		
	var popupCRM;
	
	function editarCrmContacto(idContSerial, mensaje) {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.EDIT%>";
		params = params + '&idContactoSerial='+idContSerial+'&msgReturn='+mensaje;
		
		popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true, width: 950, position:['center',30]});

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/editar_contacto_entry';   		       	
		url = url + params;
		jQuery(popupCRM).load(url);	
	}

	function nuevoContactoNoAfi() {
 		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
						<portlet:param name="struts_action" value="/cai/editar_contacto_entry" />
						<portlet:param name="cmd" value="add" />
						<portlet:param name="noAfiliado" value="true" />
					</portlet:renderURL>';		

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url); 
		
	}
	
	
	jQuery('#<portlet:namespace />buscar_s').click(function(){	
		var seccional=jQuery('#<portlet:namespace />id_seccional_sec').val();		
		var seccional_nombre=jQuery('#<portlet:namespace />seccional_sec').val();
		var nombre=jQuery('#<portlet:namespace />nombre_s').val();		
		
		if(!<portlet:namespace />validarBusqueda_s(seccional,nombre)){
				return false;
		}
		
		jQuery('#<portlet:namespace />buscando').show();
		
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada_sec").val()!="1"){
			jQuery("#<portlet:namespace />seccional_sec").val("");
			jQuery("#<portlet:namespace />id_seccional_sec").val("");
		}
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/buscar_plantel_seccional';
		    
		var paramsAfi = {"seccional" : seccional,"nombre" : nombre}
		
			
	        jQuery('#<portlet:namespace />busquedaPersonalSeccionalDiv').load(url, paramsAfi, function() {
	        																jQuery('#<portlet:namespace />buscando').hide();            															
	        															  }
	        );
	});

	function <portlet:namespace />validarBusqueda_s(seccional,nombre){
		if(trim(seccional.length)==0 &&  
		   trim(nombre.length)==0){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}
	
	function <portlet:namespace />limpiarCamposSeccional(){
		jQuery('#<portlet:namespace />id_seccional_sec').val("");		
		jQuery('#<portlet:namespace />seccional_sec').val("");
		jQuery('#<portlet:namespace />nombre_s').val("");		
	}

	jQuery('#<portlet:namespace />buscandoPrest').hide();	
	jQuery('#<portlet:namespace />buscarPrest').click(function(){
		var id_prestador=jQuery('#<portlet:namespace />id_prestador').val();
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var descripcion=jQuery('#<portlet:namespace />descripcion').val();
		var provincia=""; /* jQuery('#<portlet:namespace />provincia').val(); */
		var localidad="";/*  jQuery('#<portlet:namespace />localidad').val(); */
		var profesion=""; /* jQuery('#<portlet:namespace />profesion').val(); */
		var especialidad=""; /* jQuery('#<portlet:namespace />especialidad').val(); */
		var subEspecialidad=""; /* jQuery('#<portlet:namespace />sub-especialidad').val(); */
		var tipoPrestador="";/*  jQuery('#<portlet:namespace />tipo_prestador').val(); */
		
 		if(!<portlet:namespace />validarBusquedaPrest(id_prestador,cuit,descripcion)){
			return false;
		} 
		if(cuit.length>0){
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuit'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
		}		
		jQuery('#<portlet:namespace />buscandoPrest').show();		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/cai/buscar_prestadores" /></portlet:renderURL>';
		var busquedaPrest = { "id_prestador": id_prestador, "cuit":cuit, "descripcion": encodeURI(descripcion), "provincia":provincia,
								"localidad":localidad, "profesion":profesion, "especialidad":especialidad, "subEspecialidad":subEspecialidad,
								"tipoPrestador":tipoPrestador};
		
		jQuery('#<portlet:namespace />busquedaPrestadorDiv').load(url, busquedaPrest, function() {
        																jQuery('#<portlet:namespace />buscandoPrest').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />validarBusquedaPrest(id_prestador,cuit,descripcion){
		if(trim(id_prestador).length==0 && trim(cuit).length==0 && trim(descripcion).length==0){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
	}

	function <portlet:namespace />limpiarCamposPrest(){
		jQuery('#<portlet:namespace />id_prestador').val("");		
		jQuery('#<portlet:namespace />cuit').val("");
		jQuery('#<portlet:namespace />descripcion').val("");		
	}
	
	jQuery('#<portlet:namespace />buscandoEmp').hide();	
	jQuery('#<portlet:namespace />buscarEmp').click(function(){
		var cuit=jQuery('#<portlet:namespace />cuit_e').val();
		var sucu=jQuery('#<portlet:namespace />sucursal_e').val();
		var descripcion=jQuery('#<portlet:namespace />descripcion_e').val();
		var id_seccional=""; /* jQuery('#<portlet:namespace />id_seccional').val(); */
		
		if(!<portlet:namespace />validarBusquedaEmp(cuit,sucu,descripcion,id_seccional)){
			return false;
		}		
		if(cuit.length>0){
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuit'/>")){
				jQuery('#<portlet:namespace />cuit_e').focus();
				return false;
			}
		}		
		jQuery('#<portlet:namespace />buscandoEmp').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/buscar_padron_entidad&cuit_entidad='+cuit+
		'&sucursal='+sucu+'&entidad='+encodeURI(descripcion)+'&id_seccional='+id_seccional;

		jQuery('#<portlet:namespace />busquedaEmpleadorDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscandoEmp').hide();            															
        															  }
        );	
	});
	
	function <portlet:namespace />limpiarCamposEmp(){		
		jQuery('#<portlet:namespace />cuit_e').val("");
		jQuery('#<portlet:namespace />sucursal_e').val("");
		jQuery('#<portlet:namespace />descripcion_e').val("");		
	}
	
	function <portlet:namespace />validarBusquedaEmp(cuit,sucu,descripcion,id_seccional){	
		
		if(trim(cuit.length)==0 && trim(sucu.length)==0 && trim(descripcion.length)==0 && id_seccional==0){			
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}		
		
	}
	
	
	<!-- Script para JSON de Organizacion, Grupo y Usuarios -->

	function filtrarGrupos() {
		var idOrganiz = jQuery('#<portlet:namespace/>edificio_destino').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/organization_groups&organizatioId='+idOrganiz;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />sector_destino").length = 0;	
				//document.getElementById("<portlet:namespace />usuario_destino").length = 0;	//resetea el 3er combo
				//addElementToSelect("<portlet:namespace/>usuario_destino", "Seleccione un usuario", ""); 
				var obj = jQuery.parseJSON(data);
				//addElementToSelect("<portlet:namespace/>sector_destino", "Seleccione un sector", ""); 
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>sector_destino", text, value);					
				}	                                                                                                                                                                                                                                                            
			}
		});	
	}

	function filtrarUsuarios(esBusq, esEntrada) {
		
		var idugrp = jQuery('#<portlet:namespace/>sector_destino').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/groups_users&usergroupId='+idugrp+'&esBusqueda='+esBusq+'&esEntrada='+esEntrada;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />usuario_destino").length = 0;						
				var obj = jQuery.parseJSON(data);
				//addElementToSelect("<portlet:namespace/>usuario_destino", "Seleccione un usuario", ""); 
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>usuario_destino", text, value);					
				}	                                                                                                                                                                                                                                                            
			}
		});	
	}
	

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}
	
	function nuevoContactoUsuario() {
		
		var emp_dest = jQuery('#<portlet:namespace />edificio_destino').val();
		var sec_dest = jQuery('#<portlet:namespace />sector_destino').val();
		var usu_dest = jQuery('#<portlet:namespace />usuario_destino').val();
		if(sec_dest==''){
			alert("Debe seleccionar el Sector");
			return false;
		}
		if(usu_dest==''){
			alert("Debe seleccionar el Usuario");
			return false;
		}
		
 		<%-- var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
						<portlet:param name="struts_action" value="/cai/editar_contacto_entry" />
						<portlet:param name="cmd" value="add" />
						<portlet:param name="contactoUsuario" value="true" />
						<portlet:param name="empresa" value="emp_dest" />
						<portlet:param name="sector" value="sec_dest" />
						<portlet:param name="usuario" value="usu_dest" />
					</portlet:renderURL>';		

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);  --%>
		
	    var strutsUrl = '/cai/editar_contacto_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="cmd" value="add"/>'+
		'<liferay-portlet:param name="contactoUsuario" value="true"/>'+
		'<liferay-portlet:param name="edificio" value="__empresa"/>'+
		'<liferay-portlet:param name="sector" value="__sector"/>'+
		'<liferay-portlet:param name="usuario" value="__usuario"/>'+
	    '</liferay-portlet:renderURL>';
	    
	    url = url.replace("__strutsUrl",strutsUrl);
	    url = url.replace("__empresa",emp_dest);
	    url = url.replace("__sector",sec_dest); 
	    url = url.replace("__usuario",usu_dest); 
		
	    document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
</script>
