<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
	SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
	
	List<DrogaPatologia> patologias=SeguimientoSurServiceUtil.traePatologias(0);
	List<DrogaPatologia> normas=SeguimientoSurServiceUtil.traeNormasSeguimientoSur(0);
	boolean rolExpedienteSUR = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ALTA_EXPEDIENTES_SUR);
	boolean rolExpedienteSURCierre = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CIERRE_EXPEDIENTES_SUR);

	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	int id_seguimiento=seguimiento!=null && seguimiento.getId()!= null ?(int)seguimiento.getId():0;
	if(seguimiento==null){
		seguimiento= new SeguimientoSur();
	} 
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	Calendar fechaPresentacion = CalendarFactoryUtil.getCalendar();
	if(seguimiento.getPresentacion_fecha()==null){
		fechaPresentacion.setTime(new Date());
	}else{
	  fechaPresentacion.setTime(seguimiento.getPresentacion_fecha());
	} 
	
	Calendar fechaMesaEntrada = CalendarFactoryUtil.getCalendar();
	if(seguimiento.getMesaEntrada_fecha()==null){
	  fechaMesaEntrada.setTime(new Date());
	}else{
	  fechaMesaEntrada.setTime(seguimiento.getMesaEntrada_fecha());
	} 
	
	
	Calendar fechaTutelaje = CalendarFactoryUtil.getCalendar();
	if(seguimiento.getTutelaje_fecha()==null){
	  fechaTutelaje.setTime(new Date());
	}else{
	  fechaTutelaje.setTime(seguimiento.getTutelaje_fecha());
	} 
	
	Calendar fechaDiagnostico = CalendarFactoryUtil.getCalendar();
	if(seguimiento.getDiagnostico_fecha()==null){
	  fechaDiagnostico.setTime(new Date());
	}else{
	  fechaDiagnostico.setTime(seguimiento.getDiagnostico_fecha());
	} 
	
	Calendar fechaFinTratamiento = CalendarFactoryUtil.getCalendar();
	if(seguimiento.getFinTratamiento_fecha()==null){
	  fechaFinTratamiento.setTime(new Date());
	}else{
	  fechaFinTratamiento.setTime(seguimiento.getFinTratamiento_fecha());
	} 
	
	Calendar fechaIngresoSur = CalendarFactoryUtil.getCalendar();
	if(seguimiento.getFecha_ingreso_area_sur()==null){
	  fechaIngresoSur.setTime(new Date());
	}else{
	  fechaIngresoSur.setTime(seguimiento.getFecha_ingreso_area_sur());
	} 
	
	String motivoCierre="";
	if(seguimiento.getCierre_motivo()!=null){
	  for(int xi = 0; xi < WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES.length; xi++ ) {
	     String motivo=WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES[xi][0];
         if( motivo.equalsIgnoreCase(seguimiento.getCierre_motivo())){ 
             motivoCierre=WebKeysAutorizaciones.MOTIVOS_CIERRE_EXPEDIENTES[xi][1];
             break;
         }             		   
      }
	}
	
	Calendar fechaPropAdel = CalendarFactoryUtil.getCalendar();
	if(seguimiento.getFechaProporcionalAdelantado()!=null){
		fechaPropAdel.setTime(seguimiento.getFechaProporcionalAdelantado());
	}
	
%>

<form action="" method="post" name="<portlet:namespace />fmS">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />

  

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="avisoSeguimientoDuplicado"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
	<liferay-ui:error key="avisoModuloIncompleto"
		message="<%=(String)request.getAttribute(\"msgModError\") %>" />
		

	<fieldset class="block-labels"> 
		<legend><liferay-ui:message key="seguim-sur" /></legend>
		
		<table class="lfr-table">
		   <tr>
			   <td>
			       <label><liferay-ui:message key="fecha-ingreso-sur" />:</label>
			   </td>
			   <td>  
			         <liferay-ui:input-date
						 dayParam="fechaIngresoSurDia"
						 dayValue="<%=seguimiento.getFecha_ingreso_area_sur()!=null?fechaIngresoSur.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						 dayNullable="<%= false %>" monthParam="fechaIngresoSurMes"
						 monthValue="<%=seguimiento.getFecha_ingreso_area_sur()!=null?fechaIngresoSur.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						 monthNullable="<%= false %>" yearParam="fechaIngresoSurAnio"
						 yearValue="<%=seguimiento.getFecha_ingreso_area_sur()!=null?fechaIngresoSur.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						 yearNullable="<%= false %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/>
						 
			    </td>
			    
			    <td><label><liferay-ui:message key="nro-correspondencia" />:</label></td>
				<td><input id="<portlet:namespace />nroCorrespondenciaSUR"
					name="<portlet:namespace />nroCorrespondenciaSUR" size="20"
					maxlength="20" type="text" 
					value='<%=seguimiento.getNro_correspondencia_sur()==null?"":seguimiento.getNro_correspondencia_sur()%>' />
				</td>
				
				<td>
				<label>DDJJ: </label>
				</td>
				<td><input id="<portlet:namespace />ddjjSUR"
					name="<portlet:namespace />ddjjSUR" size="20"
					maxlength="20" type="text" readonly="readonly"
					value='<%=seguimiento.getDdjj() ==null?"":sdf.format(seguimiento.getDdjj())%>' />
				</td>
				
				<td>
				    <div id="<portlet:namespace />divconveniotecerizadora">
				    <table>
				    <label > <liferay-ui:message key="Convenio Tercerizadora." />:</label>
					<select name="<portlet:namespace/>conveniotecerizadora"
						id="<portlet:namespace />conveniotecerizadora" >
						<option value="" ></option>
						<option value="OMINT 2017">OMINT 2017</option>
					</select>					
				    </table>
				    </div>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
						                
		</table>

		<table class="lfr-table">
			<tr>
			
			  <td><liferay-ui:message key="clase-expediente" /></td>
				<td><select name="<portlet:namespace />claseExpediente"
					id="<portlet:namespace />claseExpediente" onchange="javascript:<portlet:namespace />configuraCarga();<portlet:namespace />actualizaBimestres();<portlet:namespace />valoresDefectoPorExpediente();">
						<%for(int i = 0; i < WebKeysAutorizaciones.CLASES_EXPEDIENTES.length; i++ ) {%>
						<option
							value="<%=WebKeysAutorizaciones.CLASES_EXPEDIENTES[i][0] %>"
							<%if (seguimiento != null && seguimiento.getClaseExpediente()  !=null && 
					              (WebKeysAutorizaciones.CLASES_EXPEDIENTES[i][0]).equals(seguimiento.getClaseExpediente())) { %>
							selected="selected" <%} %>>
							<%=WebKeysAutorizaciones.CLASES_EXPEDIENTES[i][1] %>
						</option>
						<% } %>
				</select></td>
			    <td><input id="<portlet:namespace />nroTipoExpedienteSUR"
					name="<portlet:namespace />nroTipoExpedienteSUR" size="5"
					maxlength="20" type="text" readonly="readonly"
					value='<%=seguimiento.getId_tipo_expediente_nro() ==null?"":seguimiento.getId_tipo_expediente_nro()%>' /></td>
			
				<td><liferay-ui:message key="anio" /></td>
				<td><select name="<portlet:namespace/>ejercicio"
					id="<portlet:namespace/>ejercicio"
					onchange="javascript:<portlet:namespace />actualizaBimestres();">
						<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						for (int i = hastaAnio-9; i<=hastaAnio; i++){  %>
						<option value="<%=i%>"
							<%if (seguimiento != null && seguimiento.getAnio()!=0 && i == seguimiento.getAnio()) { %>
							selected="selected" <%} %>>
							<%=i %></option>
						<%}%>
				</select></td>
				<td><liferay-ui:message key="periodicidad" /></td>
				<td>
				  <div id="<portlet:namespace />divPeriodicidad"> 
				   <select name="<portlet:namespace/>bimestre"
					id="<portlet:namespace/>bimestre" onchange="javascript:<portlet:namespace />actualizaFechas();">
						<%	String bimestre[]={"","Primer","Segundo","Tercer","Cuarto","Quinto","Sexto"};
						for (int i = 1; i<=6; i++){  %>
						<option value="<%=i%>">
							<%=bimestre[i]+ " Bimestre" %></option>
						<%}%>
				   </select>
				  </div>
				  <div id="<portlet:namespace />divPeriodicidadHemofilia"> 
				   <input id="<portlet:namespace />periodicidadHemofiliaSUR"
					name="<portlet:namespace />periodicidadHemofiliaSUR" size="50"
					maxlength="70" type="text" 
					value='<%=seguimiento.getPeriodicidadHemofilia() ==null?"":seguimiento.getPeriodicidadHemofilia() %>' />
				  </div>	
				</td>
				
				<td><label><liferay-ui:message key="nroSolicitudSur" />:</label></td>
				<td><input id="<portlet:namespace />nroSolicitudSUR"
					name="<portlet:namespace />nroSolicitudSUR" size="20"
					maxlength="20" type="text"
					value='<%=seguimiento.getNro_solicitud_sur()==null?"":seguimiento.getNro_solicitud_sur()%>' /></td>
                <td><label><liferay-ui:message key="nroExpediente" />:</label></td>
				<td><input id="<portlet:namespace />nroExpedienteSUR"
					name="<portlet:namespace />nroExpedienteSUR" size="20"
					maxlength="20" type="text"
					value='<%=seguimiento.getNro_expediente()==null?"":seguimiento.getNro_expediente()%>' /></td>
					
			   <td align="center"><span style="font-size: 13pt; color: blue; "><label ><%=seguimiento.getUltimoEstado()%></label></span> </td>		
			</tr>

			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>
		
		<table class="lfr-table">
		    <tr>
		      <td><liferay-ui:message key="cobertura-expediente" /></td>
				<td><select name="<portlet:namespace />tipoExpediente"
					id="<portlet:namespace />tipoExpediente" onkeyup="javascript:<portlet:namespace />habilitaTercerizadora();"  onchange="javascript:<portlet:namespace />habilitaTercerizadora();">
						<%for(int i = 0; i < WebKeysAutorizaciones.TIPOS_EXPEDIENTES.length; i++ ) {%>
						  <%if ((seguimiento != null && seguimiento.getId()!=null && seguimiento.getId()!=0)
								|| ((seguimiento == null || seguimiento.getId()==null || seguimiento.getId()==0) && WebKeysAutorizaciones.TIPOS_EXPEDIENTES[i][0]!="3") )
						  { %>
						<option
							value="<%=WebKeysAutorizaciones.TIPOS_EXPEDIENTES[i][0] %>"
							<%if (seguimiento != null && seguimiento.getId_tipo_expediente() !=null && 
					      Integer.parseInt(WebKeysAutorizaciones.TIPOS_EXPEDIENTES[i][0]) == seguimiento.getId_tipo_expediente()) { %>
							selected="selected" <%} %>>
							<%=WebKeysAutorizaciones.TIPOS_EXPEDIENTES[i][1] %>
						</option>
						  <% } %>
						<% } %>
				</select></td>
               <td>
				  <div id="<portlet:namespace />divTipoExpedienteTercerizadora">
				    <table>
				       <tr>
				       <td>
					      <liferay-ui:message key="cobertura-expediente-tercerizadora" />
					   </td>
					   <td>
					    <select name="<portlet:namespace />tipoExpedienteTercerizadora"
						id="<portlet:namespace />tipoExpedienteTercerizadora" onchange="">
							<%for(int i = 0; i < WebKeysAutorizaciones.TIPOS_EXPEDIENTES_TERCERIZADORA.length; i++ ) {%>
							<option
								value="<%=WebKeysAutorizaciones.TIPOS_EXPEDIENTES_TERCERIZADORA[i][0] %>"
								<%if (seguimiento != null && seguimiento.getId_tipo_expediente_tercerizadora() !=null && 
						      Integer.parseInt(WebKeysAutorizaciones.TIPOS_EXPEDIENTES_TERCERIZADORA[i][0]) == seguimiento.getId_tipo_expediente_tercerizadora()) { %>
								selected="selected" <%} %>>
								<%=WebKeysAutorizaciones.TIPOS_EXPEDIENTES_TERCERIZADORA[i][1] %>
							</option>
							<% } %>
					    </select>
					   </td>
					  </tr>  
					</table>   
				  </div>  
				</td>
                <div>
                
                <td id="celdalabelmontoopsim"><liferay-ui:message key="Monto Ospim" /></td>        
				<td id="celdavaluemontoospim">				
				<input id="<portlet:namespace />montoOspim" onkeydown="allowOnlyDigitsAndDecimals(event)"
					name="<portlet:namespace />montoOspim" size="10"	maxlength="10" type="text"
					value='<%=seguimiento.getImporteOspim()  ==null?"":seguimiento.getImporteOspim() %>' />
				</td>
				<td>&nbsp;</td>
    			<td id="celdalabelmontoomint"><liferay-ui:message key="Monto Omint" /></td>				
				<td id="celdavaluemontoomint">
				<input id="<portlet:namespace />montoOmint" onkeydown="allowOnlyDigitsAndDecimals(event)"
					name="<portlet:namespace />montoOmint" size="10"	maxlength="10" type="text"
					value='<%=seguimiento.getImporteOmint()  ==null?"":seguimiento.getImporteOmint() %>' />
				</td>
				<td>&nbsp;</td>
    			<td id="celdalabelmontoprevencion"><liferay-ui:message key="Monto Prevención" /></td>				
				<td id="celdavaluemontoprevencion">
				<input id="<portlet:namespace />montoprevencion" onkeydown="allowOnlyDigitsAndDecimals(event)"
					name="<portlet:namespace />montoprevencion" size="10"	maxlength="10" type="text"
					value='<%=seguimiento.getImportePrevencion() ==null?"":seguimiento.getImportePrevencion() %>' />
				</td>
				<td>&nbsp;</td>
				<td id="celdalabelmontoEnSalud"><liferay-ui:message key="Monto Ensalud" /></td>				
				<td id="celdavaluemontoEnSalud">
				<input id="<portlet:namespace />montoEnSalud" onkeydown="allowOnlyDigitsAndDecimals(event)"
					name="<portlet:namespace />montoEnSalud" size="10"	maxlength="10" type="text"
					value='<%=seguimiento.getImporteEnSalud() ==null?"":seguimiento.getImporteEnSalud() %>' />
				</td>
				<td>&nbsp;</td>
				
				<td id="celdalabelmontoCemic"><liferay-ui:message key="Monto Cemic" /></td>				
				<td id="celdavaluemontoCemic">
				<input id="<portlet:namespace />montoCemic" onkeydown="allowOnlyDigitsAndDecimals(event)"
					name="<portlet:namespace />montoCemic" size="10"	maxlength="10" type="text"
					value='<%=seguimiento.getImporteCemic() ==null?"":seguimiento.getImporteCemic() %>' />
				</td>
				<td>&nbsp;</td>

				<td><liferay-ui:message key="autoriza-omint" /></td>
				<td><select name="<portlet:namespace/>autorizaOmint"
					id="<portlet:namespace/>autorizaOmint">
						<option value="0">Seleccione</option>
						<%for(int i = 0; i < WebKeysAutorizaciones.AUTORIZA_OMINT.length; i++ ) {%>
						<option value="<%=WebKeysAutorizaciones.AUTORIZA_OMINT[i][0] %>"
							<%if (seguimiento != null && seguimiento.getId_autoriza_omint()  !=null && 
							 Integer.parseInt(WebKeysAutorizaciones.AUTORIZA_OMINT[i][0]) == seguimiento.getId_autoriza_omint()) { %>
							selected="selected" <%}%>>
							<%=WebKeysAutorizaciones.AUTORIZA_OMINT[i][1]%>
						</option>
						<% } %>

				</select></td>
				
				<td>&nbsp;</td>				
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>	
		</table>
		<table class="lfr-table">
			<tr>
			   <td>
			   
			   
			    <div id="<portlet:namespace />divCodigoPresentadoSeguimientoSur">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="datos-codigos-seguimientos" />
						</legend>
						<liferay-util:include
							page='/html/portlet/autorizaciones/seguimiento_sur/carga_codigos_seguimientossur.jsp'>
							<liferay-util:param value="<%=String.valueOf(esEdicion)%>"
								name="esEdicion" />
						</liferay-util:include>
					</fieldset>
					<table class="lfr-table">
					<tr>
		             <td colspan="1">&nbsp;</td>
                    </tr>
                    </table>
				  </div>
				  
				  <table class="lfr-table">
					    <tr>
					        <td><label><liferay-ui:message key="norma" />:</label></td>
							
							
							<td>
			                    <select name="<portlet:namespace/>normaSeguimiento" id="<portlet:namespace/>normaSeguimiento"  >
					               <option value="0">Seleccione una norma</option>
					               <%for (DrogaPatologia tnom : normas) {%>
								     <option value="<%= tnom.getId()%>"
								     <%if(tnom.getId().equals(seguimiento.getNorma())){%> selected="selected" <% } %>>
								     <%=tnom.getDrogaDescripcion()%></option>
					               <%}%>
			                    </select>
		                        </td>
				        </tr>	
				        <tr>
		                   <td colspan="1">&nbsp;</td>
                         </tr>
			      </table>
			   
  				  <div id="<portlet:namespace />divNormaSeguimientoSur">
					    <table class="lfr-table">
					    <tr>
					     <td><label><liferay-ui:message key="patologia" />:</label></td>
						 <td colspan="4" style="vertical-align:top" >
                            
						       <liferay-util:include page='/html/portlet/autorizaciones/busqueda_patologia.jsp'>
						       <liferay-util:param value="" name="prefijo" />
						       <liferay-util:param value='<%=seguimiento.getPatologia()!=null? seguimiento.getPatologia().toString():""%>' name="id_patologia" />
						       <liferay-util:param value='<%=seguimiento.getPatologia()!=null? seguimiento.getPatologiaDescripcion():""%>' name="patologia" />
						       </liferay-util:include>
						    
						</td>								
					        
					    <td>
					       <div id="<portlet:namespace />divNuevaPatologia">
					       <a href="javascript:<portlet:namespace />agregarNuevaPatologia();" id="<portlet:namespace />nuevaPatologia" ><liferay-ui:icon image="add" /></a>
					       </div>
					    </td>    
					    
					    <td><label id="<portlet:namespace />codigo_hiv_Lb"><liferay-ui:message key="codigo-hiv" />:</label></td>
					    <td>
					        <input id="<portlet:namespace />codigo_hiv"
					        name="<portlet:namespace />codigo_hiv" size="20" maxlength="20" type="text"
					        value='<%=seguimiento.getCodigoHIV()==null?"":seguimiento.getCodigoHIV()%>' />
					    </td>
					           
					    </tr>
					    <tr>
				          <td>&nbsp;</td>
			            </tr>
			            <tr>
					      <td colspan="5">
					       <table class="lfr-table">
					       <tr>
					          <td><input type="checkbox"  name="<portlet:namespace />tutelajeSeguimiento" 
							               id="<portlet:namespace />tutelajeSeguimiento" <%if(seguimiento.getTutelaje() ){%> checked="checked" <% } %>
							               onchange="javascript:<portlet:namespace />habilitaTutelaje();"><label id="<portlet:namespace />tutelajeSurLb"><liferay-ui:message key="tutelaje" /></label></td>
						      <td colspan="2">
				                   <div id="<portlet:namespace />divTutelajeSeguimientoSur">
				                     <table>
				                      <tr>
				                        <td>
					                      <label><liferay-ui:message key="fecha-tutelaje" />:</label>
					                    </td>
					                    <td>  
					                      <liferay-ui:input-date
						                    dayParam="fechaTutelajeSurDia"
						                    dayValue="<%=seguimiento.getTutelaje_fecha()!=null?fechaTutelaje.get(Calendar.DAY_OF_MONTH ):0%>"
						                    dayNullable="<%= true %>" monthParam="fechaTutelajeSurMes"
						                    monthValue="<%=seguimiento.getTutelaje_fecha()!=null?fechaTutelaje.get(Calendar.MONTH ):0%>"
						                    monthNullable="<%= true %>" yearParam="fechaTutelajeSurAnio"
						                    yearValue="<%=seguimiento.getTutelaje_fecha()!=null?fechaTutelaje.get(Calendar.YEAR ):0 %>"
						                    yearNullable="<%= true %>"
						                    yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						                    yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						                    firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						                    disabled="<%= false %>"/>
						                </td>
						                <td>    
						                  <label><liferay-ui:message key="observaciones" />:</label>
						                </td>
						                <td>  
						                  <textarea rows="2" cols="100" maxlength="20000" 
			                                id="<portlet:namespace />observacionesTutelaje" 
						                    name="<portlet:namespace />observacionesTutelaje"
						                    style="resize: none;"><%=seguimiento.getTutelaje_observaciones()%></textarea>
						                </td>    
						               </tr> 
						              </table>   
					               </div> 
				              </td>
				          </tr>
				          </table>
				         </td>       
					    </tr>
					    </table>
					
					</div>		
                </td> 
				

			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>

		<table class="lfr-table">
			<tr>
			    <td><label><liferay-ui:message key="importe-presentado" />:</label></td>
				
				<td><input id="<portlet:namespace />importeSeguimientoSUR"
					name="<portlet:namespace />importeSeguimientoSUR" size="20"
					maxlength="20" type="text"
					value='<%=seguimiento.getImportePresentado()==null?"":seguimiento.getImportePresentado()%>' /></td>
				<td><label><liferay-ui:message key="tope-recupero" />:</label></td>	
				<td><input id="<portlet:namespace />topeRecuperoSeguimientoSUR"
					name="<portlet:namespace />topeRecuperoSeguimientoSUR" size="20"
					maxlength="20" type="text"  onchange="javascript:<portlet:namespace />calculaImporteDiabetesSUR();"
					value='<%=seguimiento.getTopeRecupero()==null?"":seguimiento.getTopeRecupero()%>' /></td>	
				
			    <td><label id="<portlet:namespace/>valorUnitarioMedicamentoSurLb"><liferay-ui:message key="valor-unitario"/>:</label></td>
			    <td><input id="<portlet:namespace />valorUnitarioMedicamentoSur" 
				  name="<portlet:namespace />valorUnitarioMedicamentoSur" size="20" maxlength="20" type="text"
				  onchange="javascript:<portlet:namespace />calculaImportePresentadoMedicamentoSur();" 
				  value='<%=seguimiento.getValorUnitario()==null?"":seguimiento.getValorUnitario()%>'/></td>		
				  
				<td><label id="<portlet:namespace/>cantidadAfiliadosSeguimientoSURLb"><liferay-ui:message key="cantidad-afiliados" />:</label></td>
				
				<td><input id="<portlet:namespace />cantidadAfiliadosSeguimientoSUR"
					name="<portlet:namespace />cantidadAfiliadosSeguimientoSUR" size="20"
					maxlength="20" type="text" onchange="javascript:<portlet:namespace />calculaImporteDiabetesSUR();"
					value='<%=seguimiento.getCantidadAfiliados()==null?"":seguimiento.getCantidadAfiliados() %>' /></td>  
			</tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
			   <td><label><liferay-ui:message key="importe-reconocido" />:</label></td>
				
				<td><input id="<portlet:namespace />importeReconocidoSeguimientoSUR"
					name="<portlet:namespace />importeReconocidoSeguimientoSUR" size="20"
					maxlength="20" type="text"
					value='<%=seguimiento.getImporteReconocido() ==null?"":seguimiento.getImporteReconocido() %>' /></td>
					
				 <td><label><liferay-ui:message key="importe-proporcional-adelantado" />:</label></td>
				
				<td><input id="<portlet:namespace />importeProporcionalAdelantadoSeguimientoSUR"
					name="<portlet:namespace />importeProporcionalAdelantadoSeguimientoSUR" size="20"
					maxlength="20" type="text"
					value='<%=seguimiento.getProporcionalAdelantado() ==null?"":seguimiento.getProporcionalAdelantado() %>' /></td>	
				
				<td><label><liferay-ui:message key="fecha-proporcional-adelantado" />:</label></td>
				
				<% if(seguimiento.getFechaProporcionalAdelantado()!=null){ %>
				<td colspan="2"><liferay-ui:input-date
						dayParam="fechaPropAdelantadoDia"
						dayValue="<%=fechaPropAdel.get(Calendar.DAY_OF_MONTH )%>"
						monthParam="fechaPropAdelantadoMes"
						monthValue="<%=fechaPropAdel.get(Calendar.MONTH)%>"
						yearParam="fechaPropAdelantadoAnio"
						yearValue="<%=fechaPropAdel.get(Calendar.YEAR ) %>"
						yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" /></td>	
				<%} else{ %>
					<td colspan="2">N/D</td>	
				<%} %>		
			</tr> 
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			
			
			<tr>
			  <td><label><liferay-ui:message key="fecha-presentacion" />:</label></td>
				<td colspan="2"><liferay-ui:input-date
						dayParam="fechaPresentacionSurDia"
						dayValue="<%=seguimiento.getPresentacion_fecha()!=null?fechaPresentacion.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						dayNullable="<%= true %>" monthParam="fechaPresentacionSurMes"
						monthValue="<%=seguimiento.getPresentacion_fecha()!=null?fechaPresentacion.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						monthNullable="<%= true %>" yearParam="fechaPresentacionSurAnio"
						yearValue="<%=seguimiento.getPresentacion_fecha()!=null?fechaPresentacion.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						yearNullable="<%= true %>"
						yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" /></td>
			   <td><label><liferay-ui:message key="fecha-mesaentrada" />:</label></td>
				<td colspan="2"><liferay-ui:input-date
					dayParam="fechaMesaEntradaSurDia"
					dayValue="<%=seguimiento.getMesaEntrada_fecha()!=null?fechaMesaEntrada.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
					dayNullable="<%= true %>" monthParam="fechaMesaEntradaSurMes"
					monthValue="<%=seguimiento.getMesaEntrada_fecha()!=null?fechaMesaEntrada.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
					monthNullable="<%= true %>" yearParam="fechaMesaEntradaSurAnio"
					yearValue="<%=seguimiento.getMesaEntrada_fecha()!=null?fechaMesaEntrada.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
					yearNullable="<%= true %>"
					yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
					yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
					firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" />
				</td>
				
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>


		<table class="lfr-table">
			<tr>
				<td>
					<div id="<portlet:namespace/>divAfiliadosSeguimientoSur">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="datos-afiliado" />
							</legend>
							<liferay-util:include
								page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
								<liferay-util:param value="<%= String.valueOf(true) %>"
									name="edit_mode" />
								<liferay-util:param value="<%= String.valueOf(true) %>"
									name="discapacidad" />
								<liferay-util:param value="<%= String.valueOf(true) %>"
									name="pag_reintegro" />
								<liferay-util:param name="cuil" value='' />
								<liferay-util:param name="inte" value='' />
								<liferay-util:param value="" name="origen" />
							</liferay-util:include>
							&nbsp;
							<label id="<portlet:namespace />discapacidad_1" style="display: none;"><font style="color: red">Discapacitado</font></label>
						    &nbsp;
						    <label id="<portlet:namespace />discapacidad_vto_1" style="display: none;">Vto. Certificado: </font></label>
						</fieldset>
					</div>
				</td>
			</tr>

            <tr>
				<td>
					<div id="<portlet:namespace/>divDiagnosticoSeguimientoSur">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="observaciones-diagnostico" />
							</legend>
							
							
							<table class="lfr-table" width="100%">
                              <tr>
	                            <td>        
	                            <td><label><liferay-ui:message key="unidad-medida" />:</label></td>
	                            <td>
	                                <select name="<portlet:namespace />unidadMedidaSUR"
					                     id="<portlet:namespace />unidadMedidaSUR">
						             <%for(int i = 0; i < WebKeysAutorizaciones.UNIDAD_MEDIDA_DROGADEPENDENCIA.length; i++ ) {%>
						              <option
							             value="<%=WebKeysAutorizaciones.UNIDAD_MEDIDA_DROGADEPENDENCIA[i][0] %>"
							                  <%if (seguimiento != null && seguimiento.getClaseExpediente()  !=null && 
					                                (WebKeysAutorizaciones.UNIDAD_MEDIDA_DROGADEPENDENCIA[i][0]).equals(seguimiento.getClaseExpediente())) { %>
							             selected="selected" <%} %>>
							            <%=WebKeysAutorizaciones.UNIDAD_MEDIDA_DROGADEPENDENCIA[i][1] %>
						              </option>
						             <% } %>
			                        </select>
		                        </td>
		                        
		                        <td><label><liferay-ui:message key="cantidad-meses" />:</label></td>
	                            <td>
	                                <select name="<portlet:namespace />cantidadMesesSUR" id="<portlet:namespace />/>cantidadMesesSUR">
	                                  <option value="1" <%if(seguimiento !=null && seguimiento.getCantidadMesesTratamiento()!=null  && seguimiento.getCantidadMesesTratamiento() ==1){%> selected="selected"  <% } %>>1</option>
	                                  <option value="2" <%if(seguimiento !=null && seguimiento.getCantidadMesesTratamiento()!=null  && seguimiento.getCantidadMesesTratamiento() ==2){%> selected="selected"  <% } %>>2</option>
	                                  <option value="3" <%if(seguimiento !=null && seguimiento.getCantidadMesesTratamiento()!=null  && seguimiento.getCantidadMesesTratamiento() ==3){%> selected="selected"  <% } %>>3</option>
	                                  <option value="4" <%if(seguimiento !=null && seguimiento.getCantidadMesesTratamiento()!=null  && seguimiento.getCantidadMesesTratamiento() ==4){%> selected="selected"  <% } %>>4</option>
	                                  <option value="5" <%if(seguimiento !=null && seguimiento.getCantidadMesesTratamiento()!=null  && seguimiento.getCantidadMesesTratamiento() ==5){%> selected="selected"  <% } %>>5</option>
	                                  <option value="6" <%if(seguimiento !=null && seguimiento.getCantidadMesesTratamiento()!=null  && seguimiento.getCantidadMesesTratamiento() ==6){%> selected="selected"  <% } %>>6</option>
					                </select>
		                        </td>


	                           <td><label><liferay-ui:message key="fecha-diagnostico-sur" />:</label></td>
		                        <td colspan="1">
			                      <liferay-ui:input-date
				                     dayParam="fechaDiagnosticoSurDia"																					
				                     dayValue="<%=seguimiento.getDiagnostico_fecha()!=null?fechaDiagnostico.get(Calendar.DAY_OF_MONTH ):0 %>"
				                     dayNullable="<%= true %>"
				                     monthParam="fechaDiagnosticoSurMes"
				                     monthValue="<%=seguimiento.getDiagnostico_fecha()!=null?fechaDiagnostico.get(Calendar.MONTH ):0%>"
				                     monthNullable="<%= true %>"
				                     yearParam="fechaDiagnosticoSurAnio"
				                     yearValue="<%=seguimiento.getDiagnostico_fecha()!=null?fechaDiagnostico.get(Calendar.YEAR ):0%>"
				                     yearNullable="<%= true %>"
				                     yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
				                     yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
				                     firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
				                     disabled="<%= false %>" />
		                        </td>
	
		                        <td><label><liferay-ui:message key="fin-tratamiento-sur" />:</label></td>
		                        <td colspan="1">
			                      <liferay-ui:input-date
				                     dayParam="fechaFinTratamientoSurDia"																					
				                     dayValue="<%=seguimiento.getFinTratamiento_fecha()!=null?fechaFinTratamiento.get(Calendar.DAY_OF_MONTH ):0 %>"
				                     dayNullable="<%= true %>"
				                     monthParam="fechaFinTratamientoSurMes"
				                     monthValue="<%=seguimiento.getFinTratamiento_fecha()!=null?fechaFinTratamiento.get(Calendar.MONTH ):0%>"
				                     monthNullable="<%= true %>"
				                     yearParam="fechaFinTratamientoSurAnio"
				                     yearValue="<%=seguimiento.getFinTratamiento_fecha()!=null?fechaFinTratamiento.get(Calendar.YEAR ):0%>"
				                     yearNullable="<%= true %>"
				                     yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
				                     yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
				                     firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
				                     disabled="<%= false %>" />
		                        </td>

		
	                          </tr>

							</table>
							
						</fieldset>
					</div>
				</td>
			</tr>

			<tr>
				<td>
				  <div id="<portlet:namespace />divTratamientosSeguimientoSur">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="datos-tratamientos" />
						</legend>
						<liferay-util:include
							page='/html/portlet/autorizaciones/seguimiento_sur/busqueda_tratamientos.jsp'>
						</liferay-util:include>
					</fieldset>
				 </div>	
				 
				 <div id="<portlet:namespace />divMedicamentosSeguimientoSur">
					<fieldset class="block-labels">
					    <legend id="<portlet:namespace />medicamentoProtesisSur">
							<liferay-ui:message key="medicamentos" />
						</legend>
						<liferay-util:include
							page='/html/portlet/autorizaciones/seguimiento_sur/busqueda_liquidaciones_medicamentos.jsp'>
							<liferay-util:param name="clase" value='true'/>
						</liferay-util:include>
					</fieldset>
				 </div>	
				 <div id="<portlet:namespace />divComprobantesLiquidadosSeguimientoSur">
					  <liferay-util:include
							page='/html/portlet/autorizaciones/seguimiento_sur/carga_comprobantes_seguimientossur.jsp'>
							<liferay-util:param name="esEditable" value='true'/>
					  </liferay-util:include>
				 </div>	
				</td>
			</tr>
			
			<tr>
			  <td>
			     <div id="<portlet:namespace />divObservacionesSeguimientoSur">
			       <fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="observaciones-prestacion" />
						</legend>
			            <textarea rows="2" cols="180" maxlength="20000" 
			                                id="<portlet:namespace />observacionesSeguimientoSUR" 
						                    name="<portlet:namespace />observacionesSeguimientoSUR"
						                    style="resize: none;"><%=(seguimiento.getObservaciones()==null?"":seguimiento.getObservaciones())%></textarea>
				   </fieldset>		                    
			     </div>
			  </td>
			</tr>

			<tr>
				<td>
				  <div id="<portlet:namespace />divEdicionEstadosSeguimientoSur">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="datos-estados-seguimientos" />
						</legend>
						<liferay-util:include
							page='/html/portlet/autorizaciones/seguimiento_sur/carga_estados_seguimientossur.jsp'>
							<liferay-util:param value="<%=String.valueOf(esEdicion)%>"
								name="esEdicion" />
						</liferay-util:include>
					</fieldset>
				  </div>	
				</td>
			</tr>
			
			<tr>
				<td>
				  <div id="<portlet:namespace />divEdicionPrestadoresSeguimientoSur">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="datos-prestadores-seguimientos" />
						</legend>
						<liferay-util:include
							page='/html/portlet/autorizaciones/seguimiento_sur/carga_prestadores_seguimientossur.jsp'>
							<liferay-util:param value="<%=String.valueOf(esEdicion)%>"
								name="esEdicion" />
						</liferay-util:include>
					</fieldset>
				  </div>	
				</td>
			</tr>
			
			<tr>
		     <td>
		        <div id="<portlet:namespace />divComprobantesSeguimientoSur">
				<fieldset class="block-labels">
				   <legend>Datos Cierre</legend>
				   <table >
				     <tr>
				       <td><label><liferay-ui:message key="cierre-fecha" />:</label></td>
				       <td><input id="<portlet:namespace />fechaCierreSUR"
							name="<portlet:namespace />fechaCierreSUR" size="10"
							maxlength="20" type="text" readonly="readonly"
							value='<%=seguimiento.getCierre_Fecha_string()==null?"":seguimiento.getCierre_Fecha_string()%>' /></td>
						
					  <td><label>Motivo:</label></td>		
					  <td><input id="<portlet:namespace />motivoCierreSUR"
							name="<portlet:namespace />motivoCierreSUR" size="50"
							type="text"
							value='<%=seguimiento.getCierre_motivo() ==null?"":motivoCierre%>' readonly="readonly"/></td>	
					 </tr>	
					 
					 <tr>
				       <td>&nbsp;</td>
			         </tr>
					 
					 <tr>
					   <td><label>Comprobante:</label></td>		
					   <td>
					      <input id="<portlet:namespace />comprobanteTipoSUR"
							name="<portlet:namespace />comprobanteTipoSUR" size="4"
							type="text"
							value='<%=seguimiento.getComprobanteTipo() ==null?"":seguimiento.getComprobanteTipo()%>' readonly="readonly"/>
							
						  <input id="<portlet:namespace />comprobanteLetraSUR"
							name="<portlet:namespace />comprobanteLetraSUR" size="2"
							type="text"
							value='<%=seguimiento.getComprobanteLetra() ==null?"":seguimiento.getComprobanteLetra()%>' readonly="readonly"/> 
						  <input id="<portlet:namespace />comprobanteSucursalSUR"
							name="<portlet:namespace />comprobanteSucursalSUR" size="10"
							type="text"
							value='<%=seguimiento.getComprobanteSucursal() ==null?"":seguimiento.getComprobanteSucursal()%>' readonly="readonly"/>
						  <input id="<portlet:namespace />comprobanteNumeroSUR"
							name="<portlet:namespace />comprobanteNumeroSUR" size="20"
							type="text"
							value='<%=seguimiento.getComprobanteNumero() ==null?"":seguimiento.getComprobanteNumero()%>' readonly="readonly"/>	 		
					   </td>
					   	
					   <td><label>Fecha:</label></td>
					   <td><input id="<portlet:namespace />comprobanteFechaSUR"
							name="<portlet:namespace />/>comprobanteFechaSUR" size="10"
							maxlength="20" type="text" readonly="readonly"
							value='<%=seguimiento.getComprobante_Fecha_string() ==null?"":seguimiento.getComprobante_Fecha_string()%>' /></td>
					   <td><label>Importe:</label></td>		
					   <td>	
					      <input id="<portlet:namespace />comprobanteImporteSUR"
							name="<portlet:namespace />comprobanteImporteSUR" size="20"
							type="text"
							value='<%=seguimiento.getComprobanteImporte() ==null?"":seguimiento.getComprobanteImporte()%>' readonly="readonly"/>
					   </td>	
					  
					 </tr>	
					</table>		
			    </fieldset>
			    </div>
			    
			 </td>   
		  </tr>
		  
		</table>
	</fieldset>
	<br>
	<input type="hidden" name="<portlet:namespace />id_seguimiento"
		id="<portlet:namespace />id_seguimiento" value="<%=id_seguimiento%>" />
	<input type="hidden" value="" name="view" id="view" /> <input
		id="<portlet:namespace />nom_seleccionado"
		name="<portlet:namespace />nom_seleccionado" type="hidden" value="" />
	<input id="<portlet:namespace />tipoNomenclador"
		name="<portlet:namespace />tipoNomenclador" type="hidden" value="" />

   <c:if test="<%=seguimiento.getCierre_fecha()==null && seguimiento.getBaja_fecha()== null && (rolExpedienteSUR || rolExpedienteSURCierre) %>">
	 <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
		 />
   </c:if>		
  
  <input id="<portlet:namespace />nroCorrespondenciaValidacion" 	name="<portlet:namespace />tipoNomenclador" type="hidden" value="" />
		
</form>

<script type="text/javascript">

jQuery("#<portlet:namespace />nroCorrespondenciaSUR").blur(function(){ validarNroCorrespondencia(); });


<% if (Validator.isNotNull(seguimiento) && Validator.isNotNull(seguimiento.getConvenioTercerizadora()) && !seguimiento.getConvenioTercerizadora().equals(""))   {%>
      jQuery("#<portlet:namespace />conveniotecerizadora").val('<%=seguimiento.getConvenioTercerizadora()%>');
      document.getElementById("<portlet:namespace />conveniotecerizadora").disabled = "disabled";
<%}else{%>
      jQuery("#<portlet:namespace />divconveniotecerizadora").hide();
<%}%>

var popupNM;
var popupPAT;
<portlet:namespace />initDateFields();
function <portlet:namespace />initDateFields(){
	if(<%=seguimiento.getAnio()%>==null || <%=seguimiento.getAnio()%>==0){
		var fecha = new Date();
		var anio = fecha.getFullYear();
		jQuery("#<portlet:namespace />ejercicio").val(anio);
	}
	
	
	ocultaMontosCobertura();
	
	<portlet:namespace />configuraCarga();
	<portlet:namespace />actualizaBimestres();
	<portlet:namespace />actualizaDrogas();
	<portlet:namespace />habilitaTercerizadora();
	<portlet:namespace />habilitaTutelaje();
	
    if(<%=seguimiento!=null && seguimiento.getId()!=null && seguimiento.getId()>0%> ){
	   jQuery("#<portlet:namespace />cuil").val('<%=seguimiento.getCuilTitular()%>');
	   jQuery("#<portlet:namespace />inte").val('<%=seguimiento.getIntegrante()%>');
	   jQuery("#<portlet:namespace />tipoNomenclador").val('<%=seguimiento.getTipoNomencladorId() %>');
	   
//	   document.getElementById("<portlet:namespace/>ejercicio").disabled=true;
//	   document.getElementById("<portlet:namespace/>bimestre").disabled=true;
	   document.getElementById("<portlet:namespace/>claseExpediente").disabled=true;
	   document.getElementById("<portlet:namespace/>patologiaSeguimiento").disabled=true;
	   
	   document.getElementById("<portlet:namespace/>patologia").disabled=true;
	   jQuery("#<portlet:namespace />btnBuscarPatologia").hide();
	   jQuery("#<portlet:namespace />divNuevaPatologia").hide();
	   
	   jQuery("#<portlet:namespace/>divAfiliadosSeguimientoSur").children().attr('disabled','disabled');
	   if(<%=!"DB".equalsIgnoreCase(seguimiento.getClaseExpediente())%>){
		   <portlet:namespace />buscarAfiliados();   
	   }
	   
	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliado_fecha_vto_documentacion&cuil_titular=';
		   url += jQuery("#<portlet:namespace />cuil").val();
		   url +='&inte=';
		   url += jQuery("#<portlet:namespace />inte").val();
			
       jQuery.ajax({   
	   url: url,
	   async:false,
	   success: function(data){
		var obj = jQuery.parseJSON(data);
		var fechaVto = obj.fechaVto;
		var discapacitado =obj.discapacitado;
		 if(discapacitado !=null && discapacitado=='1'){
			jQuery('#<portlet:namespace/>discapacidad_vto_1').html("Vto.Documentación "+fechaVto);
			jQuery('#<portlet:namespace />discapacidad_1').show();
			jQuery('#<portlet:namespace />discapacidad_vto_1').show();
		 }else{
			jQuery('#<portlet:namespace/>discapacidad_vto_1').html('');
			jQuery('#<portlet:namespace />discapacidad_1').hide();
			jQuery('#<portlet:namespace />discapacidad_vto_1').hide();
		 }
		
	   }				                                                                                                                                                                                                                                                            
	
       });



       if(<%=seguimiento.getCierre_fecha()!=null%>) {
           var f = document.forms['<portlet:namespace />fmS'];
           for(var i=0,fLen=f.length;i<fLen;i++){
             f.elements[i].readOnly = true;
           }

		   jQuery('#<portlet:namespace />divEdicionSeguimientoSur').children().attr("disabled",'disabled');
		   jQuery('#<portlet:namespace />divTratamientosSeguimientoSur').children().attr("disabled",'disabled');
		   jQuery("#<portlet:namespace />divMedicamentosSeguimientoSur").children().attr("disabled",'disabled');
	   }
    }
    
}

function <portlet:namespace />actualizaBimestres(){
	var ejercicio=jQuery("#<portlet:namespace />ejercicio").val();	
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_bimestres_para_anio'
	    + '&ejercicio=' +ejercicio;
	url += '&clase=' + clase;
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			var str='';
			jQuery('#<portlet:namespace />bimestre').find('option').remove();
			for(var i =0;i< obj.bimestres.length; i++){
				str='<option value="'+obj.bimestres[i].id+'"';
				if(<%=seguimiento.getId_bimestre()%>==obj.bimestres[i].id){
				   str += ' selected ';	
				}
				str+='>'+obj.bimestres[i].descripcion +		
				'</option>'
				jQuery('#<portlet:namespace />bimestre').append(str);
			}
			
			<portlet:namespace />actualizaFechas();
		}
	});		
}

function <portlet:namespace />buscarSeguimientoSurOnDiv(e){
			var evtobj=window.event? event : e
			var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
			var clase=jQuery("#<portlet:namespace />claseExpediente").val();
			
			if(jQuery("#<portlet:namespace />nom_seleccionado").val() == "1" && (keyPressed==8 || keyPressed==46)){
				jQuery('#<portlet:namespace />codigoSeguimiento').val("");
				jQuery("#<portlet:namespace />nom_seleccionado").val("");
				return false;
			}
			
		    var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento").val();
		    if (nombre_nomenclador==null){
		    	nombre_nomenclador = '';
		    }    
		    if(jQuery("#<portlet:namespace />nom_seleccionado").val() != "1" && nombre_nomenclador.length>=6 ){
		    	if(popupNM==null)
		    	    popupNM = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupNM = null;}});
		    	
		    	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
			    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador);
			    
			    if("DI"==clase ){
		        	url += "&tiponomenclador=";	
			    }else if("CO"==clase ){
			    	url += "&tiponomenclador=13";
			    }else{
	            	url += "&tiponomenclador=11";	
	            }	
			    
				jQuery(popupNM).load(url);
				
		    }else{}
}

function <portlet:namespace />buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento").val();
	var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento").val();
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();
	if("DI"==clase || "DR"==clase || "FE"==clase || "CO"==clase){
		if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
	        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
	    }else {
	    	if(popupNM==null)
	    		popupNM = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupNM = null;}});
	    	
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
		    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&codigonomenclador='+encodeURI(codigo_nomenclador);
            if("DI"==clase ){
              url += "&tiponomenclador=";	
            }else if("FE"==clase ){
                url += "&tiponomenclador=12";
            }else if("CO"==clase ){
                url += "&tiponomenclador=13";    
            }else{
              url += "&tiponomenclador=11";	
            }		    
			jQuery(popupNM).load(url);
	    }
	}	
}

function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
	seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
	if(popupNM){
		Liferay.Popup.close(popupNM);
	}
}

function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
	jQuery('#<portlet:namespace />codigoSeguimiento').val(codigo);
	jQuery("#<portlet:namespace />descripcionSeguimiento").val(descripcion);
	jQuery('#<portlet:namespace />tipoNomenclador').val(tipoNomenclador);
	jQuery("#<portlet:namespace />nom_seleccionado").val("1");
}


function <portlet:namespace />validarCampos(){
	var result = true;
	
	if (jQuery("#<portlet:namespace/>ejercicio").val==""){
		result=false;
		alert("Debe Seleccionar un Ejercicio");
	}else{
		if (jQuery('#<portlet:namespace />bimestre').val()=="" ){
			result=false;
			alert("Debe Seleccionar un Período");
		}else{	
			if (jQuery("#<portlet:namespace />tipoExpediente").val()==""){
				result=false;
				alert("Debe ingresar un Tipo de Expediente.");
			}else{
//				if (jQuery("#<portlet:namespace />nroSolicitudSUR").val()==""){
//					result=false;
//					alert("Debe ingresar Nro de Solicitud.");
//				}else{
					if (jQuery("#<portlet:namespace />cuil").val()=="" && jQuery("#<portlet:namespace />claseExpediente").val()!="DB"){
						result=false;
						alert("Debe ingresar el Nro de Cuil.");
					}else{
						if (jQuery("#<portlet:namespace />inte").val()=="" && jQuery("#<portlet:namespace />claseExpediente").val()!="DB"){
							result=false;
							alert("Debe ingresar el Nro de Integrante.");
						}	
					}
//				}
			}    
		}   
	}
	
	return result;
}

function <portlet:namespace />salvarEdicion(){
	 
	if (validaDatosMontosCobertura()) {
			document.getElementById("<portlet:namespace/>ejercicio").disabled=false;
			document.getElementById("<portlet:namespace/>bimestre").disabled=false;
			document.getElementById("<portlet:namespace/>claseExpediente").disabled=false;
			document.getElementById("<portlet:namespace/>patologiaSeguimiento").disabled=false;
			document.getElementById("<portlet:namespace/>patologia").disabled=false;
			jQuery("#<portlet:namespace/>divAfiliadosSeguimientoSur").children().removeAttr("disabled");
			window.onbeforeunload = null;
			if (<portlet:namespace />validarCampos()) {
		<%-- 		var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
				url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/autorizaciones/editar_seguimientosur';
				url = url + params; --%>
				var xporlet ='/autorizaciones/editar_seguimientosur';
				var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
				'<liferay-portlet:param name="struts_action" value="__xporlet" />'+
				'<liferay-portlet:param name="cmd" value="update"/>'+
			    '</liferay-portlet:renderURL>';
			    url = url.replace("__xporlet",xporlet);
				submitForm(document.<portlet:namespace />fmS, url);	
			}
	}
	return false;		
}

jQuery("#<portlet:namespace />bimestre" ).change(function() {
	var clase =jQuery("#<portlet:namespace />claseExpediente").val();
	if(clase=="DI" || clase=="ME"){
	   verificaDuplicado();
	}   
});

jQuery("#<portlet:namespace />cuil" ).change(function() {
	var clase =jQuery("#<portlet:namespace />claseExpediente").val();
	if(clase=="DI" || clase=="ME"){
		   verificaDuplicado();
	}	   
});

jQuery("#<portlet:namespace />inte" ).change(function() {
	var clase =jQuery("#<portlet:namespace />claseExpediente").val();
	if(clase=="DI" || clase=="ME"){
		   verificaDuplicado();
	}	   
});

function verificaDuplicado(){
	if(<%=id_seguimiento==0%> && jQuery("#<portlet:namespace />bimestre" ).val()!=0 &&
			jQuery("#<portlet:namespace />cuil").val()!="" && 	
			jQuery("#<portlet:namespace />inte").val()!=""
	){
		
		 var cuil=jQuery("#<portlet:namespace />cuil").val();
		 var inte=jQuery("#<portlet:namespace />inte").val();
		 var bimestre=jQuery("#<portlet:namespace />bimestre").val();
		 
		 <%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_duplicado_seguimientosur&cuil='+cuil+'&inte='+encodeURI(inte)+'&bimestre='+encodeURI(bimestre); --%>
	
		 var xporletUrl ='/autorizaciones/validar_duplicado_seguimientosur';
		 var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
			'<liferay-portlet:param name="struts_action" value="__xporletUrl" />'+
			'<liferay-portlet:param name="cuil" value="__cuil"/>'+
			'<liferay-portlet:param name="inte" value="__inte"/>'+
			'<liferay-portlet:param name="bimestre" value="__bimestre"/>'+
		    '</liferay-portlet:renderURL>';
		    
		    url = url.replace("__xporletUrl",xporletUrl);
		    url = url.replace("__cuil",cuil);
		    url = url.replace("__inte",inte);
		    url = url.replace("__bimestre",bimestre);
		 
			jQuery.ajax({   
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					if(obj.validado=="1"){
						alert("<liferay-ui:message key='seguimientosur-duplicado-bimestre'/>");
					} 					
				}
			}); 
	}
}
function <portlet:namespace />limpiarNomencladorAutocompletar(){
	jQuery("#<portlet:namespace />descripcionSeguimiento").val('');
	jQuery("#<portlet:namespace />codigoSeguimiento").val('');
	jQuery("#<portlet:namespace />tipoNomenclador").val('')
}

<c:if test='<%="N".equalsIgnoreCase((String)request.getSession().getAttribute("esPopUp"))%>'>
	window.onbeforeunload = function(){return "Esta seguro de abandonar la página?";};
</c:if>

function <portlet:namespace />configuraCarga(){
	
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();
	var valor = jQuery("#<portlet:namespace />tipoExpediente").val();	

	
	jQuery('#<portlet:namespace />fechaMesaEntradaSurDia').attr("disabled",false);
	jQuery('#<portlet:namespace />fechaMesaEntradaSurMes').attr("disabled",false);
	jQuery('#<portlet:namespace />fechaMesaEntradaSurAnio').attr("disabled",false);
	jQuery("#<portlet:namespace/>divAfiliadosSeguimientoSur").show();
	
	jQuery("#<portlet:namespace />divPeriodicidad").show();
	jQuery("#<portlet:namespace />divPeriodicidadHemofilia").hide();
	document.getElementById("<portlet:namespace />periodicidadHemofiliaSUR").style.visibility = "hidden";
	
	jQuery("#<portlet:namespace />divObservacionesSeguimientoSur").hide();
	
	var idSeguimiento=jQuery("#<portlet:namespace />id_seguimiento").val();
	if(idSeguimiento==null || idSeguimiento==0){
	   jQuery("#<portlet:namespace />tutelajeSeguimiento").attr('checked', false);
	   jQuery("#<portlet:namespace />cantidadAfiliadosSeguimientoSUR").val('');
	   jQuery("#<portlet:namespace />topeRecuperoSeguimientoSUR").val('');	
	   jQuery("#<portlet:namespace />importeSeguimientoSUR").val('');
	   jQuery("#<portlet:namespace />importeReconocidoSeguimientoSUR").val('');
	}   
	
	jQuery("#<portlet:namespace />divTipoExpedienteTercerizadora").hide();
	
	jQuery("#<portlet:namespace />divDiagnosticoSeguimientoSur").hide();
	
	jQuery("#<portlet:namespace />observacionesSeguimientoSur").val('');
	
	document.getElementById("<portlet:namespace/>cantidadAfiliadosSeguimientoSURLb").style.visibility = "hidden";
	document.getElementById("<portlet:namespace />cantidadAfiliadosSeguimientoSUR").style.visibility = "hidden";
	
	
	
	if(clase=="DI"){
//		jQuery("#<portlet:namespace />normaSeguimiento").val('');
		jQuery("#<portlet:namespace />patologiaSeguimiento").val('');
		jQuery("#<portlet:namespace />tutelajeSeguimiento").attr('checked', false);
		
//		jQuery('#<portlet:namespace />divTratamientosSeguimientoSur').show();
        jQuery('#<portlet:namespace />divTratamientosSeguimientoSur').hide();
		jQuery('#<portlet:namespace />divCodigoPresentadoSeguimientoSur').show();
		jQuery("#<portlet:namespace />divNormaSeguimientoSur").hide();
		jQuery("#<portlet:namespace />divMedicamentosSeguimientoSur").hide();
        jQuery("#<portlet:namespace />divComprobantesLiquidadosSeguimientoSur").show();
		
		document.getElementById("<portlet:namespace/>valorUnitarioMedicamentoSurLb").style.visibility = "hidden";
		document.getElementById("<portlet:namespace />valorUnitarioMedicamentoSur").style.visibility = "hidden";
		document.getElementById("<portlet:namespace />valorUnitarioMedicamentoSur").value=0;
		
		
		//Agregado por cambio de comportamiento de Discapacidad
		document.getElementById("<portlet:namespace />medicamentoProtesisSur").innerHTML="Prestación";
		document.getElementById("<portlet:namespace />medicamentoProtesisSurBusqueda").innerHTML="Prestación";
		document.getElementById("<portlet:namespace/>drogaMedicamento").style.visibility = "hidden";
		document.getElementById("<portlet:namespace/>drogaMedicamentoLb").style.visibility = "hidden";
		document.getElementById("<portlet:namespace />troquelProtesisSurBusqueda").innerHTML="Código";
		document.getElementById("<portlet:namespace/>drogaMedicamento").value=0;
			
		document.getElementById("<portlet:namespace/>valorUnitarioMedicamentoSurLb").style.visibility = "hidden";
		document.getElementById("<portlet:namespace />valorUnitarioMedicamentoSur").style.visibility = "hidden";
		//Fin Agregado
		
		
	}else if(clase=="ME"|| clase=="PR" || clase=="OT" || 
			 clase=="HI" || clase=="HE" || clase=="DB" ||
			 clase=="DR" || clase=="FE" || clase=="CO"){
		
		jQuery("#<portlet:namespace />codigoSeguimiento").val('');
		jQuery("#<portlet:namespace />descripcionSeguimiento").val('');
		
		jQuery("#<portlet:namespace />divTratamientosSeguimientoSur").hide();
		jQuery('#<portlet:namespace />divCodigoPresentadoSeguimientoSur').hide();
		jQuery("#<portlet:namespace />divNormaSeguimientoSur").show();
		jQuery("#<portlet:namespace />divMedicamentosSeguimientoSur").hide();
		jQuery("#<portlet:namespace />divComprobantesLiquidadosSeguimientoSur").show();
		
		jQuery('#<portlet:namespace />divTutelajeSeguimientoSur').hide();
		
		if(clase=="OT" || clase=="PR"){
		  jQuery("#<portlet:namespace />divObservacionesSeguimientoSur").show();
		}  
		
		if(clase=="ME"){
			document.getElementById("<portlet:namespace />medicamentoProtesisSur").innerHTML="Medicamentos";
			document.getElementById("<portlet:namespace />medicamentoProtesisSurBusqueda").innerHTML="Medicamentos";
			document.getElementById("<portlet:namespace/>drogaMedicamento").style.visibility = "visible";
			document.getElementById("<portlet:namespace/>drogaMedicamentoLb").style.visibility = "visible";
			document.getElementById("<portlet:namespace />troquelProtesisSurBusqueda").innerHTML="Troquel";
			
			document.getElementById("<portlet:namespace/>valorUnitarioMedicamentoSurLb").style.visibility = "visible";
			document.getElementById("<portlet:namespace />valorUnitarioMedicamentoSur").style.visibility = "visible";
		}else{
			document.getElementById("<portlet:namespace />medicamentoProtesisSur").innerHTML="Prestación";
			document.getElementById("<portlet:namespace />medicamentoProtesisSurBusqueda").innerHTML="Prestación";
			document.getElementById("<portlet:namespace/>drogaMedicamento").style.visibility = "hidden";
			document.getElementById("<portlet:namespace/>drogaMedicamentoLb").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />troquelProtesisSurBusqueda").innerHTML="Código";
			document.getElementById("<portlet:namespace/>drogaMedicamento").value=0;
			
			document.getElementById("<portlet:namespace/>valorUnitarioMedicamentoSurLb").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />valorUnitarioMedicamentoSur").style.visibility = "hidden";
			
		}
		
		if(clase=="DR" || clase=="FE" || clase=="CO"){
			jQuery('#<portlet:namespace />divCodigoPresentadoSeguimientoSur').show();
			if(clase=="DR"){
			  jQuery("#<portlet:namespace />divDiagnosticoSeguimientoSur").show();
			}  
			
			document.getElementById("<portlet:namespace/>valorUnitarioMedicamentoSurLb").style.visibility = "visible";
			document.getElementById("<portlet:namespace />valorUnitarioMedicamentoSur").style.visibility = "visible";
		}else{
			jQuery('#<portlet:namespace />divCodigoPresentadoSeguimientoSur').hide();
		}
		
		
		if(clase=="DR" || clase=="HI" || clase=="HE" || clase=="DB"){
			document.getElementById("<portlet:namespace />tutelajeSeguimiento").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />tutelajeSurLb").style.visibility = "hidden";
			
		}else{
			document.getElementById("<portlet:namespace />tutelajeSeguimiento").style.visibility = "visible";
			document.getElementById("<portlet:namespace />tutelajeSurLb").style.visibility = "visible";
		}
		
		if(clase=="DB"){
		   document.getElementById("<portlet:namespace/>cantidadAfiliadosSeguimientoSURLb").style.visibility = "visible";
		   document.getElementById("<portlet:namespace />cantidadAfiliadosSeguimientoSUR").style.visibility = "visible";
		   jQuery("#<portlet:namespace />divMedicamentosSeguimientoSur").hide();
		   jQuery("#<portlet:namespace/>divAfiliadosSeguimientoSur").hide();
		   if(valor == 7){
			   verMontoOspimEnSalud();	   
		   }
		   if(valor == 8){
			   verMontoOspimPrevencionOmint();	   
		   }
		   if(valor == 11){
			   verMontoOmintPrevencionEnSaludCemic();	   
		   }
		}
		if(clase=="HE" || clase=="HI"){
		   jQuery("#<portlet:namespace />divPeriodicidad").hide();
		   jQuery("#<portlet:namespace />divPeriodicidadHemofilia").show();
           document.getElementById("<portlet:namespace />periodicidadHemofiliaSUR").style.visibility = "visible";
		} 
		
		if(clase=="HI"){
			document.getElementById("<portlet:namespace />codigo_hiv").style.visibility = "visible";
			document.getElementById("<portlet:namespace/>codigo_hiv_Lb").style.visibility = "visible";
		}else{
			document.getElementById("<portlet:namespace/>codigo_hiv_Lb").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />codigo_hiv").style.visibility = "hidden";
		}	
	}
	
}

function <portlet:namespace />actualizaDrogas(){
	var patologia=jQuery("#<portlet:namespace />patologiaSeguimiento").val();	
	//var patologia=jQuery("#<portlet:namespace />id_patologia").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_drogas_para_patologia'
	    + '&patologia=' +patologia;
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			var str='';
			jQuery('#<portlet:namespace />drogaMedicamento').find('option').remove();
			jQuery('#<portlet:namespace />drogaMedicamento').append('<option value="0">Selecciona una Droga</option>');
			for(var i =0;i< obj.drogas.length; i++){
				str='<option value="'+obj.drogas[i].id+'"';
				
				str+='>'+obj.drogas[i].descripcion+'</option>'
				jQuery('#<portlet:namespace />drogaMedicamento').append(str);
			}                                                                                                                                                                                                                                                            
		}
	});		
}

function <portlet:namespace />actualizaFechas(){
	var bimestre=jQuery("#<portlet:namespace/>bimestre").val();	
	
	
	var ejercicio=jQuery("#<portlet:namespace />ejercicio").val();	
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_bimestres_para_anio_por_id'
	    + '&bimestreid=' +bimestre;
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			var str='';
			for(var i =0;i< obj.bimestres.length; i++){
			   var fechainicio=	obj.bimestres[0].fechainicio.split("-");
			   var fechafin=obj.bimestres[0].fechafin.split("-");
			   jQuery("#<portlet:namespace />fechaDesdeDia").val(parseInt(fechainicio[2]));
			   jQuery("#<portlet:namespace />fechaDesdeMes").val(parseInt(fechainicio[1])-1);
			   jQuery("#<portlet:namespace />fechaDesdeAnio").val(fechainicio[0]);
			   
			   jQuery("#<portlet:namespace />fechaHastaDia").val(parseInt(fechafin[2]));
			   jQuery("#<portlet:namespace />fechaHastaMes").val(parseInt(fechafin[1])-1);
			   jQuery("#<portlet:namespace />fechaHastaAnio").val(fechafin[0]);
			}
		}
	});		
	
}

function <portlet:namespace />habilitaTercerizadora(){	
	jQuery("#<portlet:namespace />divTipoExpedienteTercerizadora").hide();
	jQuery("#<portlet:namespace />divconveniotecerizadora").hide();
		
	var opcion =jQuery("#<portlet:namespace />tipoExpediente").val();
	if(opcion==2){
		jQuery("#<portlet:namespace />divTipoExpedienteTercerizadora").show();
	}
	valor = jQuery("#<portlet:namespace />tipoExpediente").val();
	
	ocultaMontosCobertura();
	
	if(valor ==4){
		verMontoOspimPrevencion();	
	}	    
	if(valor ==5){
		verMontoOmintPrevencion();	
	}		
	if(valor ==6){
		verMontoOspimOmint();
	}
	
	if(valor == 7){
		verMontoOspimEnSalud();
	}	
	
	if(valor == 8){
		verMontoOspimPrevencionOmint();
	}	
	
	if(valor == 9){
		verMontoPrevencionEnSalud();
	}	
	
	if(valor == 10){
		verMontoOmintPrevencionEnSalud();
	}	
	
	if(valor == 11){
		verMontoOmintPrevencionEnSaludCemic();
	}
}

function <portlet:namespace />habilitaTutelaje(){
	var opcion =jQuery("#<portlet:namespace />tutelajeSeguimiento").is(':checked');
	if(opcion){
		jQuery("#<portlet:namespace />divTutelajeSeguimientoSur").show();
		
		if(<%=id_seguimiento==0%>){
			jQuery('#<portlet:namespace />fechaMesaEntradaSurDia').attr("disabled",true);
			jQuery('#<portlet:namespace />fechaMesaEntradaSurMes').attr("disabled",true);
			jQuery('#<portlet:namespace />fechaMesaEntradaSurAnio').attr("disabled",true);
		}
		
	}else{
		jQuery("#<portlet:namespace />divTutelajeSeguimientoSur").hide();
		
		jQuery("#<portlet:namespace />fechaTutelajeSurDia").val('');
		jQuery("#<portlet:namespace />fechaTutelajeSurMes").val('');
		jQuery("#<portlet:namespace />fechaTutelajeSurAnio").val('');
		jQuery("#<portlet:namespace />observacionesTutelaje").val('');
		
		jQuery('#<portlet:namespace />fechaMesaEntradaSurDia').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaMesaEntradaSurMes').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaMesaEntradaSurAnio').attr("disabled",false);
		
	}
}


function <portlet:namespace />valoresDefectoPorExpediente(){
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();

	jQuery('#<portlet:namespace/>patologiaSeguimiento').val('');
	jQuery('#<portlet:namespace/>patologia').val('');
	jQuery("#<portlet:namespace />patologia_seleccionada").val("");
	jQuery("#<portlet:namespace />btnBuscarPatologia").show();
	jQuery("#<portlet:namespace />divNuevaPatologia").show();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_valores_defecto_por_expediente';
	url += '&clase=' + clase;
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
		
            for(var i =0;i< obj.norma.length; i++){
            	jQuery('#<portlet:namespace/>normaSeguimiento').val(obj.norma[i].id);
            }
            
            for(var i =0;i< obj.patologia.length; i++){
            	jQuery('#<portlet:namespace/>patologiaSeguimiento').val(obj.patologia[i].id);
            	jQuery('#<portlet:namespace/>patologia').val(obj.patologia[i].descripcion);
            	jQuery("#<portlet:namespace />patologia_seleccionada").val("1");
            	jQuery("#<portlet:namespace />btnBuscarPatologia").hide();
            }
		}
	});		
}

function <portlet:namespace />calculaImporteDiabetesSUR(){
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();
	if(clase=="DB"){
		var cantidad =jQuery("#<portlet:namespace />cantidadAfiliadosSeguimientoSUR").val();
		var tope = jQuery("#<portlet:namespace />topeRecuperoSeguimientoSUR").val();
		var importe= cantidad * tope;
		jQuery("#<portlet:namespace />importeSeguimientoSUR").val(importe);
	}
}


function <portlet:namespace />agregarNuevaPatologia(){
   	
   //if(popupPAT==null)
	   popupPAT = Liferay.Popup({title:"Nueva Patología",modal:true,width:700});
	   
	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/nueva_patologia_sur';
	   jQuery(popupPAT).load(url);
   
}


function <portlet:namespace />cerrarPatologiaNueva(id,des){
		if(popupPAT){		
		Liferay.Popup.close(popupPAT);
	}
	

	if(id>0){
	   jQuery('#<portlet:namespace/>patologiaSeguimiento').val(id);
	   jQuery('#<portlet:namespace/>patologia').val(des);
	   jQuery("#<portlet:namespace />patologia_seleccionada").val("1");
	   jQuery("#<portlet:namespace />btnBuscarPatologia").hide();
   }   
	
} 

function ocultaMontosCobertura(){
	jQuery("#celdalabelmontoopsim").hide();
	jQuery("#celdavaluemontoospim").hide();	
	jQuery("#celdalabelmontoomint").hide();
	jQuery("#celdavaluemontoomint").hide();	
	jQuery("#celdalabelmontoprevencion").hide();
	jQuery("#celdavaluemontoprevencion").hide();
	jQuery("#celdalabelmontoEnSalud").hide();
	jQuery("#celdavaluemontoEnSalud").hide();
	jQuery("#celdalabelmontoCemic").hide();
	jQuery("#celdavaluemontoCemic").hide();

}

function verMontoOspimPrevencion (){
	jQuery("#celdalabelmontoomint").hide();
	jQuery("#celdavaluemontoomint").hide();
	jQuery("#celdalabelmontoopsim").show();
	jQuery("#celdavaluemontoospim").show();
	jQuery("#celdalabelmontoprevencion").show();
	jQuery("#celdavaluemontoprevencion").show();	
	jQuery("#celdalabelmontoEnSalud").hide();
	jQuery("#celdavaluemontoEnSalud").hide();
	jQuery("#celdalabelmontoCemic").hide();
	jQuery("#celdavaluemontoCemic").hide();
}

function verMontoOspimOmint(){
	jQuery("#celdalabelmontoprevencion").hide();
	jQuery("#celdavaluemontoprevencion").hide();
	jQuery("#celdalabelmontoopsim").show();
	jQuery("#celdavaluemontoospim").show();
	jQuery("#celdalabelmontoomint").show();
	jQuery("#celdavaluemontoomint").show();
	jQuery("#celdalabelmontoEnSalud").hide();
	jQuery("#celdavaluemontoEnSalud").hide();
	jQuery("#celdalabelmontoCemic").hide();
	jQuery("#celdavaluemontoCemic").hide();


}

function verMontoOmintPrevencion (){
	jQuery("#celdalabelmontoopsim").hide();
	jQuery("#celdavaluemontoospim").hide();
	jQuery("#celdalabelmontoprevencion").show();
	jQuery("#celdavaluemontoprevencion").show();
	jQuery("#celdalabelmontoomint").show();
	jQuery("#celdavaluemontoomint").show();
	jQuery("#celdalabelmontoEnSalud").hide();
	jQuery("#celdavaluemontoEnSalud").hide();
	jQuery("#celdalabelmontoCemic").hide();
	jQuery("#celdavaluemontoCemic").hide();


}

function verMontoOspimEnSalud (){
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();
	
	jQuery("#celdalabelmontoprevencion").hide();
	jQuery("#celdavaluemontoprevencion").hide();
	jQuery("#celdalabelmontoomint").hide();
	jQuery("#celdavaluemontoomint").hide();
	jQuery("#celdalabelmontoCemic").hide();
	jQuery("#celdavaluemontoCemic").hide();
	
	if(clase=="DB"){
		jQuery("#celdalabelmontoopsim").show();
		jQuery("#celdavaluemontoospim").show();
		jQuery("#celdalabelmontoEnSalud").show();
		jQuery("#celdavaluemontoEnSalud").show();	
	}


}

function verMontoOspimPrevencionOmint (){
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();
	
	jQuery("#celdalabelmontoEnSalud").hide();
	jQuery("#celdavaluemontoEnSalud").hide();	
	jQuery("#celdalabelmontoCemic").hide();
	jQuery("#celdavaluemontoCemic").hide();
	
	if(clase=="DB"){
		jQuery("#celdalabelmontoopsim").show();
		jQuery("#celdavaluemontoospim").show();
		jQuery("#celdalabelmontoprevencion").show();
		jQuery("#celdavaluemontoprevencion").show();
		jQuery("#celdalabelmontoomint").show();
		jQuery("#celdavaluemontoomint").show();
		
	}

}


function verMontoPrevencionEnSalud (){
	
	jQuery("#celdalabelmontoomint").hide();
	jQuery("#celdavaluemontoomint").hide();
	jQuery("#celdalabelmontoopsim").hide();
	jQuery("#celdavaluemontoospim").hide();
	jQuery("#celdalabelmontoEnSalud").show();
	jQuery("#celdavaluemontoEnSalud").show();	
	jQuery("#celdalabelmontoprevencion").show();
	jQuery("#celdavaluemontoprevencion").show();
	jQuery("#celdalabelmontoCemic").hide();
	jQuery("#celdavaluemontoCemic").hide();
	

}

function verMontoOmintPrevencionEnSalud (){
	
	jQuery("#celdalabelmontoopsim").hide();
	jQuery("#celdavaluemontoospim").hide();
	jQuery("#celdalabelmontoomint").show();
	jQuery("#celdavaluemontoomint").show();
	jQuery("#celdalabelmontoEnSalud").show();
	jQuery("#celdavaluemontoEnSalud").show();	
	jQuery("#celdalabelmontoprevencion").show();
	jQuery("#celdavaluemontoprevencion").show();
	jQuery("#celdalabelmontoCemic").hide();
	jQuery("#celdavaluemontoCemic").hide();
	

}

function verMontoOmintPrevencionEnSaludCemic (){
	var clase=jQuery("#<portlet:namespace />claseExpediente").val();
	
	jQuery("#celdalabelmontoopsim").hide();
	jQuery("#celdavaluemontoospim").hide();
	jQuery("#celdalabelmontoomint").show();
	jQuery("#celdavaluemontoomint").show();
	jQuery("#celdalabelmontoEnSalud").show();
	jQuery("#celdavaluemontoEnSalud").show();	
	jQuery("#celdalabelmontoprevencion").show();
	jQuery("#celdavaluemontoprevencion").show();
	if(clase=="DB"){
	  jQuery("#celdalabelmontoCemic").show();
	  jQuery("#celdavaluemontoCemic").show();
	}else{
	  jQuery("#celdalabelmontoCemic").hide();
	  jQuery("#celdavaluemontoCemic").hide();
	}

}


function validaDatosMontosCobertura(){
    var respuesta=true;
	var importePresentado  = jQuery('#<portlet:namespace />importeSeguimientoSUR').val();
	var montoOspim  = jQuery('#<portlet:namespace />montoOspim').val();
	var montoOmint  = jQuery('#<portlet:namespace />montoOmint').val();
	var montoPrevencion  = jQuery('#<portlet:namespace />montoprevencion').val();
	var total =0;
	
	importePresentadoDouble= parseFloat(importePresentado.replace(',','.'));
	montoOspimDouble= parseFloat(montoOspim.replace(',','.'));
	montoOmintDouble= parseFloat(montoOmint.replace(',','.'));
	montoPrevencionDouble= parseFloat(montoPrevencion.replace(',','.'));
	
	if(isNaN(importePresentadoDouble)) {jQuery('#<portlet:namespace />totalEdicion').val()  ; importePresentadoDouble=0; 	}
	if(isNaN(montoOspimDouble)) { jQuery('#<portlet:namespace />montoOspim').val()  ; montoOspimDouble=0; 	}
	if(isNaN(montoOmintDouble)) { jQuery('#<portlet:namespace />montoOmint').val()  ; montoOmintDouble=0; 	}
	if(isNaN(montoPrevencion)) { jQuery('#<portlet:namespace />montoprevencion').val()  ; montoPrevencion=0; 	}
	cobertura = jQuery("#<portlet:namespace />tipoExpediente").val();	
	if(cobertura==4) {
    	total= montoOspimDouble + montoPrevencionDouble;
    	msgCustom="Ospim / Prevención";
    	jQuery('#<portlet:namespace />montoOmint').val(0);
	}
	if (cobertura==5) {
		total= montoPrevencionDouble + montoOmintDouble;
    	msgCustom="Prevención / Omint";	
    	jQuery('#<portlet:namespace />montoOspim').val(0);
	}
    if (cobertura==6) {
    	total= montoOspimDouble + montoOmintDouble;
    	msgCustom="Ospim / Omint";	
    	jQuery('#<portlet:namespace />montoprevencion').val(0);
    }    
    
	if ( total >importePresentadoDouble){
    	alert('Error, la suma de los importes ( ' + msgCustom + ' ) no puede superar el monto del importe presentado.');
    	return false; 
    }
	
	return  respuesta;
}

function validarNroCorrespondencia() {
    var cuit="";
    var params="";
    nroCorrespondencia =jQuery("#<portlet:namespace />nroCorrespondenciaSUR").val();
    if(nroCorrespondencia!=""){
    
    params += "&nrocorrespondencia="+nroCorrespondencia;
    
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/validar_nro_correspondencia';
	   url = url + params;
	   jQuery.ajax({   
	   url: url,
		success: function(data) {
			var obj = jQuery.parseJSON(data);
			var respuesta = obj.nroCorrespondenciaExiste;
			jQuery('#<portlet:namespace />nroCorrespondenciaValidacion').val(respuesta);
				if (respuesta=="false"){
					alert("El nro de correspondencia no existe.");
					jQuery("#<portlet:namespace />nroCorrespondenciaSUR").focus();
				}
			}
				}); 
    }
    
    return  true;    
	 
}


	
</script>

