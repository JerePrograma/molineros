<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@page import="ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	PreAutorizacion preautorizacion=(PreAutorizacion)request.getSession().getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	SimpleDateFormat sdfFinal = new SimpleDateFormat("yyyyMMdd");
	boolean esEdicion = true;
	if(viewStr==null){
		viewStr=ParamUtil.getString(request, "view");
	}
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	int id_preautorizacion=preautorizacion!=null && preautorizacion.getId()!= null ?(int)preautorizacion.getId():0;
	if(preautorizacion==null){
		preautorizacion= new PreAutorizacion();
	}
	
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	if(preautorizacion.getFecha()==null){
		fecha.setTime(new Date());
	}else{
	  fecha.setTime(preautorizacion.getFecha());
	} 
	
	Calendar fechaRespuestaPS = CalendarFactoryUtil.getCalendar();
	if(preautorizacion.getFechaRespuestaPS()!=null){
	   fechaRespuestaPS.setTime(preautorizacion.getFechaRespuestaPS());
	}
	
	Calendar fechaNotificacion = CalendarFactoryUtil.getCalendar();
	if(preautorizacion.getFechaNotificacionAfiliado()!=null){
		fechaNotificacion.setTime(preautorizacion.getFechaNotificacionAfiliado());
	}
	
	Calendar fechaEntrega = CalendarFactoryUtil.getCalendar();
	if(preautorizacion.getFechaEntregaRespuesta() !=null){
		fechaEntrega.setTime(preautorizacion.getFechaEntregaRespuesta());
	}
	
	Calendar fechaEnvioTercerizadora = CalendarFactoryUtil.getCalendar();
	if(preautorizacion.getFechaEnvioTercerizadora() !=null){
		fechaEnvioTercerizadora.setTime(preautorizacion.getFechaEnvioTercerizadora());
	}
	
	Calendar fechaRecepcionTercerizadora = CalendarFactoryUtil.getCalendar();
	if(preautorizacion.getFechaRecepcionTercerizadora() !=null){
		fechaRecepcionTercerizadora.setTime(preautorizacion.getFechaRecepcionTercerizadora());
	}
	
	Calendar fechaAlojamientoDesde = CalendarFactoryUtil.getCalendar();
	if(preautorizacion.getAlojamientoDesde()  !=null){
		fechaAlojamientoDesde.setTime(preautorizacion.getAlojamientoDesde());
	}
	
	Calendar fechaAlojamientoHasta = CalendarFactoryUtil.getCalendar();
	if(preautorizacion.getAlojamientoHasta()  !=null){
		fechaAlojamientoHasta.setTime(preautorizacion.getAlojamientoHasta());
	}
	
	
	String email = "";
	String organizacionId = user.getOrganizations().size()>0?String.valueOf(user.getOrganizations().get(0).getOrganizationId()):"";
    String tabValue = ParamUtil.getString(request, "tab", null); // "datos"

    //boolean incluirBajas = ParamUtil.getBoolean(request, "incluir_bajas", false);

    String prestacionPideTipoOpc=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_DISCAPACIDAD_PIDE_TIPO");
    String marcaReinLiqDiscapacidad=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_DISCAPACIDAD_MARCA_REINLIQ");
    boolean rolAlertaRoja = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_PREAUTORIZACION_ALERTA_ROJA );
	boolean rolGestionOspim = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_PREAUTORIZACION_GESTION_OSPIM);
	String nroreclamo ="Reclamo Nro : " + "000"+  String.valueOf(preautorizacion.getIdReclamoPrestacional());
	/* List<ClaseBase>diagnosticos = TraeListasServiceUtil.getTraeDiagnosticos(); */
	List<ClaseBase>diagnosticos = (List<ClaseBase>)request.getSession().getAttribute(WebKeysAutorizaciones.DIAGNOSTICOS);

	if(diagnosticos == null){
		request.getSession().setAttribute(WebKeysAutorizaciones.DIAGNOSTICOS,TraeListasServiceUtil.getTraeDiagnosticos());
	}
	
%>

<form action="" method="post" name="<portlet:namespace />fmS">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden"
		value="" />
    <input type="hidden" name="<portlet:namespace />tab_seleccionada"  value="<%=tabValue%>" />
  
	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="deleteItemOk"
		message="<%=(String)request.getAttribute(\"msgItemOk\") %>" />
	<liferay-ui:error key="errorAfiliadoNull"
		message="<%=(String)request.getAttribute(\"msgInsertError\") %>" />
	<liferay-ui:error key="errorAfiliadoSinCobertMed"
		message="<%=(String)request.getAttribute(\"msgErrorAfiSinCobMed\") %>" />
		
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
		

	<fieldset class="block-labels"> 
		<legend>Preautorizaci&oacute;n</legend>
		
		<table class="lfr-table">
		   <tr>
		      <td>
				   <div id="divAlertaRojaAlta" <%if(!rolAlertaRoja){%> hidden='hidden' style="visibility: hidden;" <%}%>  >
				      <table style="background-color:#AEB6BF">
				         <tr>
				           <td>
				             &nbsp;&nbsp; <label>Alerta Roja:</label>
				           </td>
				           <td><input type="checkbox" id="<portlet:namespace />alertaRoja" name="<portlet:namespace />alertaRoja" <%=preautorizacion.isAlertaRoja() ?"checked=\"checked\"":"" %>/></td>
				         </tr>
				      </table>
				   </div>
				   
				   <div id="divAlertaRojaMuestra" <%if(rolAlertaRoja || !preautorizacion.isAlertaRoja()){%> hidden='hidden' style="visibility: hidden;" <%}%>  >
				    <table >
				         <tr>
				           <td>
				              <label style="font-size:18px;background-color:#FF0000;color:white" > Alerta Roja </label>
				           </td>
				           
				         </tr>
				    </table>
				   </div>
				   
				  
			    </td>
			    
			    <td>
			        <table style="background-color:#AEB6BF">
				         <tr>
				           <td>
				              &nbsp;&nbsp;<label><liferay-ui:message key="discapacidad" /> :</label>
				           </td>
				           <td><input type="checkbox" id="<portlet:namespace />discapacidadChk" name="<portlet:namespace />discapacidadChk" <%=preautorizacion.isDiscapacidad() ?"checked=\"checked\"":"" %>
				                 onclick="javascript:<portlet:namespace />manejoDiscapacidad();"
				                 <%if(preautorizacion.getId()!=null && preautorizacion.getUltimoEstado()!=null && !"AP".equals(preautorizacion.getUltimoEstado().getId())){%> disabled="disabled" <%}%>/></td>
				         </tr>
				      </table>
			    </td>
			    <!--  <td>&nbsp;</td> -->
			     <td>
			        <table style="background-color:#AEB6BF">
				         <tr>
				           <td>
				              &nbsp;&nbsp; <label><liferay-ui:message key="medicamentos" />:</label>
				           </td>
				           <td><input type="checkbox" id="<portlet:namespace />medicamentoChk" name="<portlet:namespace />medicamentoChk" <%=preautorizacion.isMedicamento() ?"checked=\"checked\"":"" %>
				                 onclick="javascript:<portlet:namespace />manejoMedicamento();"
				                 <%if(preautorizacion.getId()!=null && preautorizacion.getUltimoEstado()!=null && !"AP".equals(preautorizacion.getUltimoEstado().getId())){%> disabled="disabled" <%}%>/></td>
				         </tr>
				      </table>
			    </td>
			    
			     <td>
			        <table style="background-color:#AEB6BF">
				         <tr>
				           <td>
				              &nbsp;&nbsp;<label><liferay-ui:message key="alojamiento" />:</label>
				           </td>
				           <td><input type="checkbox" id="<portlet:namespace />alojamientoChk" name="<portlet:namespace />alojamientoChk" <%=preautorizacion.isAlojamiento() ?"checked=\"checked\"":"" %>
				                 onclick="javascript:<portlet:namespace />manejoAlojamiento();"
				                 <%if(preautorizacion.getId()!=null && preautorizacion.getUltimoEstado()!=null && !"AP".equals(preautorizacion.getUltimoEstado().getId())){%> disabled="disabled" <%}%>/></td>
				         </tr>
				      </table>
			    </td>
			    <td>
			        <table style="background-color:#AEB6BF">
				         <tr>
				           <td>
				              &nbsp;&nbsp;<label><liferay-ui:message key="prot-ort" />:</label>
				           </td>
				           <td><input type="checkbox" id="<portlet:namespace />protesisOrtChk" 
				           		name="<portlet:namespace />protesisOrtChk" <%=preautorizacion.isProtesisOrtesis() ?"checked=\"checked\"":"" %>
				                 <%if(preautorizacion.getId()!=null && preautorizacion.getUltimoEstado()!=null && !"AP".equals(preautorizacion.getUltimoEstado().getId())){%> disabled="disabled" <%}%>/></td>
				         </tr>
				      </table>
			    </td>
			    
			    <td>
			        <table style="background-color:#AEB6BF">
				         <tr>
				           <td>
				              &nbsp;&nbsp;<label>Posible A.R.T:</label>
				           </td>
				           <td><input type="checkbox" id="<portlet:namespace />artChk" 
				           		name="<portlet:namespace />artChk" <%=preautorizacion.isART() ?"checked=\"checked\"":"" %>
				                 <%if(preautorizacion.getId()!=null && preautorizacion.getUltimoEstado()!=null && !"AP".equals(preautorizacion.getUltimoEstado().getId())){%> disabled="disabled" <%}%>/></td>
				         </tr>
				      </table>
			    </td>
			    
<%
    id_preautorizacion = 0;
    int idPedidoApp = 0;

    if (preautorizacion != null && preautorizacion.getId() != null) {
        id_preautorizacion = preautorizacion.getId();

        try {
            idPedidoApp = PreAutorizacionServiceUtil.obtenerIdPreautorizacionAPP(id_preautorizacion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    if (idPedidoApp != 0) {
%>
        <td>
            <table style="font-weight: bold; color: blue; font-size: 18px">
                <tr>
                    <td>
                        &nbsp;&nbsp;<label>ID APP </label>
                    </td>
                    <td>
                        <input id="<portlet:namespace />nroPreautorizacionApp"
                            name="<portlet:namespace />nroPreautorizacionApp" size="5"
                            maxlength="20" type="text" readonly="readonly" tabindex="-1"
                            value="<%= idPedidoApp %>" />
                    </td>
                </tr>
            </table>
        </td>
<%
    }
%>

		   <tr>
				<td>&nbsp;</td>
			</tr>
		</table>   
		
		<table class="lfr-table">
		   <tr>
			   <td>
			       <label><liferay-ui:message key="fecha" />:</label>
			   </td>
			   <td>  
			         <liferay-ui:input-date
						 dayParam="fechaPreAutorizacionDia"
						 dayValue="<%=preautorizacion.getFecha()!=null?fecha.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						 dayNullable="<%= false %>" monthParam="fechaPreAutorizacionMes"
						 monthValue="<%=preautorizacion.getFecha()!=null?fecha.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						 monthNullable="<%= false %>" yearParam="fechaPreAutorizacionAnio"
						 yearValue="<%=preautorizacion.getFecha()!=null?fecha.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						 yearNullable="<%= false %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"
						 />
						 
			    </td>
			    
				
			    
			    <td><liferay-ui:message key="id" />:</td> 
			    <td><input id="<portlet:namespace />nroPreautorizacion"
					name="<portlet:namespace />nroPreautorizacion" size="5"
					maxlength="20" type="text" readonly="readonly" tabindex="-1"
					value="<%=preautorizacion.getId() ==null?"":preautorizacion.getId() %>" />
				</td>
				<td><liferay-ui:message key="solic-terc" />:</td> 
			    <td><input id="<portlet:namespace />nroSolicitud"
					name="<portlet:namespace />nroSolicitud" size="10"
					maxlength="20" type="text" readonly="readonly" tabindex="-1"
					value="<%=preautorizacion.getIdAutorizacionWS() ==null?"":preautorizacion.getIdAutorizacionWS() %>" />
				</td>
				<%if(preautorizacion!=null && preautorizacion.getPreAutorizOrigen()!=null 
					&& preautorizacion.getPreAutorizOrigen() > 0){ %>
				<td><liferay-ui:message key="preaut-origen" />:</td> 
			    <td><input id="<portlet:namespace />idPreautOrigen"
					name="<portlet:namespace />idPreautOrigen" size="10"
					maxlength="20" type="text" readonly="readonly" tabindex="-1"
					value="<%=preautorizacion.getIdAutorizacionWS() ==null?"":preautorizacion.getPreAutorizOrigen() %>" />
				</td>
				<%} %>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
						                
		</table>

		<table class="lfr-table">
			<tr>
				<td>
					<div id="<portlet:namespace/>divAfiliadosPreautorizacion">
						<fieldset class="block-labels">
							<legend>
								<liferay-ui:message key="datos-afiliado" />
							</legend>
                            <liferay-util:include
                                page='/html/portlet/autorizaciones/busqueda_afiliado_filtro_preautorizaciones.jsp'>
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

                            <label id="<portlet:namespace />discapacidad_1" style="display: none;">
                                <font style="color: red">Discapacitado</font>
                            </label>

                            &nbsp;

                            <label id="<portlet:namespace />discapacidad_vto_1" style="display: none;">
                                Vto. Certificado:
                            </label>
						</fieldset>
					</div>
				</td>
				<td>
				  <fieldset class="block-labels seccionVerificarDomicilio" id="<portlet:namespace />seccionVerificarDomicilio">
				      <table>
				       <tr>
				          <td>&nbsp;</td>
			           </tr>   
				       <tr>		
			             <td><label><liferay-ui:message key="contacto-verif-domi" />:</label></td>
			            </tr>
			            <tr>
				          <td>&nbsp;</td>
			            </tr>
			            <tr> 
			             <td>			             
				            <div id="<portlet:namespace />divBotonActualizar">
				               <%if(esEdicion){ %>
					            <input type="button" value="Actualizar" 
					    	     onclick="javascript:mostrarDomicilioAfiliado();"
					    	    />
					    	    <%} %>
				            </div>
				          </td>  
				        </tr>
				        
				      
				        <tr>   
				         <td> 
				            <div id="<portlet:namespace />divResultadoActualizarOK">
					           <p><b><liferay-ui:message key="crm-actualiza-domicilio"/></b></p>
				            </div>
			             </td>
			            </tr> 
			            <tr>
				          <td>&nbsp;</td>
			            </tr>  
			         </table>
			       </fieldset>
				</td>
			</tr>
		</table>
		
        <table class="lfr-table">
			<tr>
			   <td>
			     <div id="<portlet:namespace />divPrestador" hidden="hidden" >
			       <fieldset class="block-labels">
						<legend style="font-size:12px">
						    Prestador
						</legend>
	               <liferay-util:include page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
					  		<liferay-util:param name="esEditable" value='true'/>
					  		<liferay-util:param name="solo_vigentes" value='true'/>
					  		<liferay-util:param name="search_url" value="/autorizaciones/buscar_prestador" />
					  		<liferay-util:param name="ext" value='_aut'/>
					</liferay-util:include>	
					</fieldset>
			     </div>
			   </td>
			</tr>
		</table>
			      			
		<table class="lfr-table">
			<tr>
			   <td>
			   
			   <div id="<portlet:namespace />divAlojamiento" hidden="hidden" >
					<fieldset class="block-labels">
						<legend style="color:green;font-size:18px">
						    Fechas Probables de Alojamiento
						</legend>
						
						
						<table class="lfr-table">
					     <tr>
		                   <td>
			                  <label>Desde:</label>
			              </td>
			              <td>  
			                 <liferay-ui:input-date
						          dayParam="fechaPreAutorizacionAlojamientoDiaDesde"
						          dayValue="<%=preautorizacion.getAlojamientoDesde()!=null?fechaAlojamientoDesde.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						          dayNullable="<%= false %>" monthParam="fechaPreAutorizacionAlojamientoMesDesde"
						          monthValue="<%=preautorizacion.getAlojamientoDesde()!=null?fechaAlojamientoDesde.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						          monthNullable="<%= false %>" yearParam="fechaPreAutorizacionAlojamientoAnioDesde"
						          yearValue="<%=preautorizacion.getAlojamientoDesde()!=null?fechaAlojamientoDesde.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						          yearNullable="<%= false %>"
						          yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) %>"
						          yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 2 %>"
						          firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						          disabled="<%= false %>"/>
			              </td>
		                    
		                  
		                   <td>
			                  <label>Hasta:</label>
			              </td>
			              <td>  
			                 <liferay-ui:input-date
						          dayParam="fechaPreAutorizacionAlojamientoDiaHasta"
						          dayValue="<%=preautorizacion.getAlojamientoHasta()!=null?fechaAlojamientoHasta.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						          dayNullable="<%= false %>" monthParam="fechaPreAutorizacionAlojamientoMesHasta"
						          monthValue="<%=preautorizacion.getAlojamientoHasta()!=null?fechaAlojamientoHasta.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						          monthNullable="<%= false %>" yearParam="fechaPreAutorizacionAlojamientoAnioHasta"
						          yearValue="<%=preautorizacion.getAlojamientoHasta()!=null?fechaAlojamientoHasta.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						          yearNullable="<%= false %>"
						          yearRangeStart="<%= fechaHasta.get(Calendar.YEAR)%>"
						          yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 2%>"
						          firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						          disabled="<%= false %>"/>
			              </td>
		                 </tr>
                        </table>
						
						
					</fieldset>
					
				</div> 
			   
			    <div id="<portlet:namespace />divCodigoPresentadoSeguimientoSur">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="datos-codigos-seguimientos" />
						</legend>
						<liferay-util:include
							page='/html/portlet/autorizaciones/pre_autorizaciones/preautorizacion_prestaciones.jsp'>
							<liferay-util:param value="<%=String.valueOf(esEdicion)%>" name="esEdicion" />
						</liferay-util:include>
					</fieldset>
					<table class="lfr-table">
					<tr>
		             <td colspan="1">&nbsp;</td>
                    </tr>
                    </table>
				</div>
				
				
				<div id="<portlet:namespace />divTroquelPresentadoSeguimientoSur">
					<fieldset class="block-labels">
						<legend>
							<liferay-ui:message key="datos-medicam-seguimientos" />
						</legend>
						<liferay-util:include page="/html/portlet/autorizaciones/pre_autorizaciones/medicamentos/busqueda_medicamentos.jsp">
				            
				            <liferay-util:param name="search_url" value="/autorizaciones/buscar_medicamento" />
				           
				            <liferay-util:param name="troquel" value='' />
				            <liferay-util:param name="nombre_medicamento" value='' />
				            <liferay-util:param name="id_medicamento" value='' />
				            <liferay-util:param name="esEditable" value='true' />
				            <liferay-util:param name="popup" value='true' />
				        </liferay-util:include>
				    </fieldset>
				</div>
				
				</td>
			</tr>
			 
		  <tr>
		     <td colspan="1">&nbsp;</td>
          </tr>
        </table>   
	</fieldset>
	<%-- <br>
	  <fieldset class="block-labels"> 
		<legend>Diagnóstico:</legend>
		  <table class="lfr-table">
		  <tr>
		  <td>Seleccione: </td>
		  <td> 
	       <select name="<portlet:namespace />diagnostico"
					  id="<portlet:namespace />diagnostico" >
						<%for(ClaseBase diagnostico:diagnosticos) {%>
						<option
							value="<%=diagnostico.getId() %>"
							<%if (preautorizacion != null && preautorizacion.getDiagnostico() !=null &&
								  preautorizacion.getDiagnostico().getId() !=null &&
								  diagnostico.getId().equals(preautorizacion.getDiagnostico().getId())) { %>
							selected="selected" <%} %>>
							<%=diagnostico.getDescripcion() %>
						</option>
						<% } %>
	      </select>
	      </td>
	     </tr> 
	    </table>  
	 </fieldset>   --%>
	 <fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="observaciones-diagnostico" />
		</legend>
		<!-- </br> -->
		<table class="lfr-table">
			<tr>
				<td><label><liferay-ui:message key="observaciones-diagnostico" />:</label></td>
				<td colspan="5"><liferay-util:include
						page="/html/portlet/autorizaciones/busqueda_diagnostico_preaut.jsp">
						<liferay-util:param name="id_diagnostico"
    						value='<%= (preautorizacion != null && preautorizacion.getDiagnostico() != null) ? preautorizacion.getDiagnostico().getId() : "" %>' />	
						<liferay-util:param name="descripDiag"
   							value='<%= (preautorizacion != null && preautorizacion.getDiagnostico() != null) ? preautorizacion.getDiagnostico().getDescripcion() : "" %>' />
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion)%>" />
					</liferay-util:include></td>
			</tr>
		</table>
		<!-- </br> -->
	</fieldset>
	<br>
	<fieldset class="block-labels"> 
		<legend><liferay-ui:message key="obs-internas" />:</legend>
		   	&nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="4" cols="160" maxlength="20000" 
		              id="<portlet:namespace />observacionesPreautorizacion" 
		              name="<portlet:namespace />observacionesPreautorizacion"
		              style="resize:vertical;"><%=preautorizacion.getObservaciones()!=null?preautorizacion.getObservaciones():"" %></textarea>
	</fieldset>		
	<fieldset class="block-labels"> 
		<legend><liferay-ui:message key="obs-compartidas" />:</legend>	
		    &nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="4" cols="160" maxlength="20000" 
		              id="<portlet:namespace />observacionesPreautorizacionTerc" 
		              name="<portlet:namespace />observacionesPreautorizacionTerc"
		              style="resize:vertical;"><%=preautorizacion.getObservacionesTercerizadoras()!=null?preautorizacion.getObservacionesTercerizadoras():"" %></textarea>          			                    
	</fieldset>	
	
	<fieldset class="block-labels" > 
		<legend><font size=4>Estudios que se requieren</font></legend>
		<table>
		   <tr>
		   
		   
		      <td style="color:#0000ff">
		        <div id="<portlet:namespace />divchHC" <%if(!preautorizacion.isHistoriaClinica() && !preautorizacion.getExisteHistoriaClinica()){%> hidden="hidden"<%}%> >
		          <input type="checkbox" id="<portlet:namespace />chHC" name="<portlet:namespace />chHC"
		          <%if(preautorizacion.isHistoriaClinica()){%> checked="checked" <% } %>><font size=3> Resumen Historia Cl&iacute;nica</font>
		        </div>
		        
		       </td>
		       <td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	          <td style="color:#0000ff">
	              <div id="<portlet:namespace />divchEC" <%if(!preautorizacion.isEstudiosComplementarios() && !preautorizacion.getExisteEstudiosComplementarios() ){%> hidden="hidden"<%}%> >
	                <input type="checkbox" id="<portlet:namespace />chEC" name="<portlet:namespace />chEC"
	                <%if(preautorizacion.isEstudiosComplementarios()){%> checked="checked" <% } %>><font size=3> Estudios Complementarios</font>
	              </div>  
	          </td>
	           <td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	          <td style="color:#0000ff">
	            <div id="<portlet:namespace />divchBI" <%if(!preautorizacion.isBiopsia() && !preautorizacion.getExisteBiopsia() ){%> hidden="hidden"<%}%> >
	            <input type="checkbox" id="<portlet:namespace />chBI" name="<portlet:namespace />chBI"
	            <%if(preautorizacion.isBiopsia()){%> checked="checked" <% } %> ><font size=3> Biopsia</font>
	            </div>
	          </td>
	           <td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	          <td style="color:#0000ff">
	            <div id="<portlet:namespace />divchAN" <%if(!preautorizacion.isAnatomiaPatologica() && !preautorizacion.getExisteAnatomiaPatologica() ){%> hidden="hidden"<%}%> >
	             <input type="checkbox" id="<portlet:namespace />chAN" name="<portlet:namespace />chAN"
	             <%if(preautorizacion.isAnatomiaPatologica() ){%> checked="checked" <% } %>><font size=3> Anatom&iacute;a Patol&oacute;gica</font>
	            </div> 
	           </td>
	           <td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<!--  	          <td style="color:#0000ff"><input type="checkbox" id="ch5" checked="checked" ><font size=3> Tratamientos Previos</font></td> -->
	       </tr>
	       <tr>
		     <td colspan="1">&nbsp;</td>
           </tr>
	    </table>      
	</fieldset>	
	
	<br>
	<table>
	<tr><td>
	<div id="<portlet:namespace />divGestionOspim" <%if(!rolGestionOspim ||  !(preautorizacion.isSupra() || preautorizacion.isMedicamento())|| preautorizacion.getFechaEmail()==null || !"GO".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId())){%> hidden='hidden' style="visibility: hidden; height:0px;" <%}%>>
	  <fieldset class="block-labels" > 
		<legend>Gesti&oacute;n OSPIM</legend>
		
		<table class="lfr-table">
		   <tr>
		      <td>Tipo Pedido:</td>
			  <td>
			     <select name="<portlet:namespace />tipoPedidoOSPIM"  id="<portlet:namespace />tipoPedidoOSPIM" >
						<%for(int i = 0; i < WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_PEDIDO.length; i++ ) {%>
						<option
							value="<%= WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_PEDIDO[i][0] %>"
							<%if (preautorizacion != null && preautorizacion.getTipoPedidoGestionOSPIM() !=null && 
					              (WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_PEDIDO[i][0]).equals(preautorizacion.getTipoPedidoGestionOSPIM())) { %>
							selected="selected" <%} %>>
							<%=WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_PEDIDO[i][1] %>
						</option>
						<% } %>
				 </select>
			  </td> 
		   
			  <td><liferay-ui:message key="estado" />:</td>
			  <td><select name="<portlet:namespace />estadoOSPIM"
					  id="<portlet:namespace />estadoOSPIM"  onchange="<portlet:namespace />seteaEstadoGestionOspim()">
					   <option value="">Seleccione Estado</option> 
						<%for(int i = 0; i < WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_ESTADOS.length; i++ ) {%>
						<option
							value="<%=WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_ESTADOS[i][0] %>"
							<%if (preautorizacion != null && preautorizacion.getUltimoEstadoOSPIM() !=null && 
					              (WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_ESTADOS[i][0]).equals(preautorizacion.getUltimoEstadoOSPIM().getId())) { %>
							selected="selected" <%} %>>
							<%=WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_ESTADOS[i][1] %>
						</option>
						<% } %>
				   </select>
			  </td>
			  
			  <td>Tipo Gesti&oacute;n</td>
			  <td><select name="<portlet:namespace />gestionOSPIM"
					  id="<portlet:namespace />gestionOSPIM" >
						<%for(int i = 0; i < WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_TIPOS_GESTION.length; i++ ) {%>
						<option
							value="<%=WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_TIPOS_GESTION[i][0] %>"
							<%if (preautorizacion != null && preautorizacion.getTipoGestionOSPIM() !=null && 
					              (WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_TIPOS_GESTION[i][0]).equals(preautorizacion.getTipoGestionOSPIM())) { %>
							selected="selected" <%} %>>
							<%=WebKeysAutorizaciones.PREAUTORIZACIONES_GESTION_OSPIM_TIPOS_GESTION[i][1] %>
						</option>
						<% } %>
				   </select>
			  </td>
		  </tr>
		  <tr>
		     <td colspan="1">&nbsp;</td>
          </tr>
		  <tr>
		      <td><liferay-ui:message key="observaciones" /></td>
		      <td colspan="5"><textarea rows="5" cols="150"
			   id="<portlet:namespace />observacionesOSPIM"
			   name="<portlet:namespace />observacionesOSPIM" <% if (!esEdicion) { %>
			   <%="readonly='readonly'" %> <%}%>><%= preautorizacion != null && preautorizacion.getObservacionesOSPIM() != null?preautorizacion.getObservacionesOSPIM() : "" %></textarea>
			   </td>
		  </tr>
		   
		  <tr>
		     <td colspan="1">&nbsp;</td>
          </tr>
          <tr>
             <td>
             <%if(esEdicion && rolGestionOspim && preautorizacion.isMedicamento() && preautorizacion.getFechaEmail()!=null &&
		             "GO".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId()) && 
		             (preautorizacion.getUltimoEstadoOSPIM()==null || preautorizacion.getUltimoEstadoOSPIM().getId()==null ||
		             "".equalsIgnoreCase(preautorizacion.getUltimoEstadoOSPIM().getId()) )
		          ){%> 
                  <input id="<portlet:namespace />enviarPS"
		             value="Enviar a PS"
		             title="Enviar a Prevención Salud"
		             onClick="javascript: enviarPS();"
		             type="button" />
		      <%}%>        
             
             </td>
          </tr>
          </table>
          <table>
                <tr>
                  <td colspan="2">
                    <div id="<portlet:namespace />divRecuperaSUR">
                       <span style="font-size: 9pt; color: green; "><label ><b>Recuperable SUR</b></label></span>
                    </div>
                  </td>
                  
                   <td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  
                  <td>
                    <div id="<portlet:namespace />divReclamo">
                        <span style="font-size: 9pt; color: green; "><label><b><%= nroreclamo %></b></label></span>
		            </div> 
                  </td>
                 </tr>
          </table>
          
		
	  </fieldset>	
	</div>
	</td></tr>
	</table>
	
	
	
	<div id="<portlet:namespace />divDocumentacionTercerizadora">
	  <br>
	  <fieldset class="block-labels"> 
		  <legend>Env&iacute;o de documentaci&oacute;n a OSPIM central</legend>
		    <table class="lfr-table">
		      <tr>
		         <td>
			       <label>Env&iacute;o de Documentaci&oacute;n Original:</label>
			     </td>
			     <td>  
			       <liferay-ui:input-date
						 dayParam="fechaEnvioTercerizadoraDia"
						 dayValue='<%=preautorizacion.getFechaEnvioTercerizadora()  !=null?fechaEnvioTercerizadora.get(Calendar.DAY_OF_MONTH ):-1%>'
						 dayNullable="<%= true %>" monthParam="fechaEnvioTercerizadoraMes"
						 monthValue='<%=preautorizacion.getFechaEnvioTercerizadora()!=null?fechaEnvioTercerizadora.get(Calendar.MONTH ):-1%>'
						 monthNullable="<%= true %>" yearParam="fechaEnvioTercerizadoraAnio"
						 yearValue='<%=preautorizacion.getFechaEnvioTercerizadora()!=null?fechaEnvioTercerizadora.get(Calendar.YEAR ):-1%>'
						 yearNullable="<%= true %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/> 
						 
			     </td>
			     
			     <td>
			       <label>Recepci&oacute;n de Documentaci&oacute;n:</label>
			     </td>
			     <td>  
			       <liferay-ui:input-date
						 dayParam="fechaRecepcionTercerizadoraDia"
						 dayValue='<%=preautorizacion.getFechaRecepcionTercerizadora() !=null?fechaRecepcionTercerizadora.get(Calendar.DAY_OF_MONTH ):-1%>'
						 dayNullable="<%= true %>" monthParam="fechaRecepcionTercerizadoraMes"
						 monthValue='<%=preautorizacion.getFechaRecepcionTercerizadora()!=null?fechaRecepcionTercerizadora.get(Calendar.MONTH ):-1%>'
						 monthNullable="<%= true %>" yearParam="fechaRecepcionTercerizadoraAnio"
						 yearValue='<%=preautorizacion.getFechaRecepcionTercerizadora()!=null?fechaRecepcionTercerizadora.get(Calendar.YEAR ):-1%>'
						 yearNullable="<%= true %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/> 
			     </td>
			     
              </tr>
            </table> 		  
	  </fieldset> 
	</div>
	<br>
	<div id="<portlet:namespace />divPrevencion">
	  <fieldset class="block-labels"> 
		<legend>Respuesta de autorizaci&oacute;n</legend>
		
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		   <tr>
		      <td><liferay-ui:message key="estado" />:</td>
				<td><select name="<portlet:namespace />estadoPreautorizacion"
					  id="<portlet:namespace />estadoPreautorizacion" onchange="<portlet:namespace />habilitaComponentesPorEstado()"
					    <% if (!esEdicion || 
					    		(preautorizacion != null 
					    		&& preautorizacion.getUltimoEstado() !=null 
					    		&& preautorizacion.getUltimoEstado().equals(WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[6][0])
					    		&& preautorizacion.getPreAutorizAsociada() == 0)
					    		) { %> disabled="disabled"<%}%> >
						<%for(int i = 0; i < WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES.length-1; i++ ) {%>
						<option
							value="<%=WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[i][0] %>"
							<%if (preautorizacion != null && preautorizacion.getUltimoEstado() !=null && 
					              (WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[i][0]).equals(preautorizacion.getUltimoEstado().getId())) { %>
							selected="selected" <%} %>>
							<%=WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[i][1] %>
						</option>
						<% } %>
				   </select>
				   
				</td> 
		   
			   <td>
			       <label><liferay-ui:message key="fecha-respuesta-terc" />:</label>
			   </td>
			   <td>  
			       <liferay-ui:input-date
						 dayParam="fechaRespuestaPSDia"
						 dayValue='<%=preautorizacion.getFechaRespuestaPS()!=null?fechaRespuestaPS.get(Calendar.DAY_OF_MONTH ):-1%>'
						 dayNullable="<%= true %>" monthParam="fechaRespuestaPSMes"
						 monthValue='<%=preautorizacion.getFechaRespuestaPS()!=null?fechaRespuestaPS.get(Calendar.MONTH ):-1%>'
						 monthNullable="<%= true %>" yearParam="fechaRespuestaPSAnio"
						 yearValue='<%=preautorizacion.getFechaRespuestaPS()!=null?fechaRespuestaPS.get(Calendar.YEAR ):-1%>'
						 yearNullable="<%= true %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/> 
			    </td>
			   <%-- <% if(preautorizacion != null && preautorizacion.getUltimoEstado() != null
		   				&& preautorizacion.getUltimoEstado().getMotivoRechazo() != null){ %>
			   <td>
			       <label><liferay-ui:message key="motivo-rech" />:</label>
			   </td>
			   <td>
			   		<input type="text" style="width: 400px;" name="<portlet:namespace />motivoRechazo" id="<portlet:namespace />motivoRechazo" 
			   		value="<%= preautorizacion != null && preautorizacion.getUltimoEstado() != null
			   				&& preautorizacion.getUltimoEstado().getMotivoRechazo() != null?preautorizacion.getUltimoEstado().getMotivoRechazo()  : "" %>"> 
			   </td> 
			   <% }else{ %>
			   	<td colspan="2">&nbsp;</td>
			   <% } %> --%>
			   <td>
			       <label><liferay-ui:message key="motivo-rech" />:</label>
			   </td>
			   <td>
			   		<input type="text" style="width: 400px;" name="<portlet:namespace />motivoRechazo" id="<portlet:namespace />motivoRechazo" 
			   		value="<%= preautorizacion != null && preautorizacion.getUltimoEstado() != null
			   				&& preautorizacion.getUltimoEstado().getMotivoRechazo() != null?preautorizacion.getUltimoEstado().getMotivoRechazo()  : "" %>"> 
			   </td>
		  </tr>
		  
          <% if(preautorizacion != null && preautorizacion.getUltimoEstado() != null
		   				&& preautorizacion.getUltimoEstado().getObservacionesExternas() != null){ %>
          <tr>
          	 <td>
			       <label><liferay-ui:message key="obs-externa" />:</label>
			</td>
          	<td colspan="5">
			   	<input type="text" style="width: 800px;" name="<portlet:namespace />obsExterna" id="<portlet:namespace />obsExterna" 
			   		value="<%= preautorizacion != null && preautorizacion.getUltimoEstado() != null
			   				&& preautorizacion.getUltimoEstado().getObservacionesExternas() != null?preautorizacion.getUltimoEstado().getObservacionesExternas()  : "" %>"> 
			   </td> 
          </tr>
          <%} %>
		  <tr>
		      <td><liferay-ui:message key="tipo-entrega" />:</td>
			  <td><select name="<portlet:namespace />tipoEntrega"
					  id="<portlet:namespace />tipoEntrega" disabled="disabled" >
					    <option value="0">Seleccione tipo Entrega</option> 
					    <%for(int i = 0; i < WebKeysAutorizaciones.TIPOS_ENTREGA.length; i++ ) {%>
						<option
							value="<%=WebKeysAutorizaciones.TIPOS_ENTREGA[i][0] %>"
							<%if (preautorizacion != null && preautorizacion.getTipoEntrega()!=null && 
					              (WebKeysAutorizaciones.TIPOS_ENTREGA[i][0]).equals(preautorizacion.getTipoEntrega())) { %>
							selected="selected" <%} %>>
							<%=WebKeysAutorizaciones.TIPOS_ENTREGA[i][1] %>
						</option>
						<% } %>
				   </select>
				</td>
				<td>
			       <label><liferay-ui:message key="notificacion-afiliado" />:</label>
			   </td>
			   <td>  
			       <liferay-ui:input-date
						 dayParam="fechaNotificacionDia"
						 dayValue='<%=preautorizacion.getFechaNotificacionAfiliado()!=null?fechaNotificacion.get(Calendar.DAY_OF_MONTH ):-1%>'
						 dayNullable="<%= true %>" monthParam="fechaNotificacionMes"
						 monthValue='<%=preautorizacion.getFechaNotificacionAfiliado()!=null?fechaNotificacion.get(Calendar.MONTH ):-1%>'
						 monthNullable="<%= true %>" yearParam="fechaNotificacionAnio"
						 yearValue='<%=preautorizacion.getFechaNotificacionAfiliado()!=null?fechaNotificacion.get(Calendar.YEAR ):-1%>'
						 yearNullable="<%= true %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/> 
			    </td>
			    
			   <td>
			       <label><liferay-ui:message key="entrega-respuesta" />:</label>
			   </td>
			   <td>  
			       <liferay-ui:input-date
						 dayParam="fechaEntregaDia"
						 dayValue='<%=preautorizacion.getFechaEntregaRespuesta() !=null?fechaEntrega.get(Calendar.DAY_OF_MONTH ):-1%>'
						 dayNullable="<%= true %>" monthParam="fechaEntregaMes"
						 monthValue='<%=preautorizacion.getFechaEntregaRespuesta()!=null?fechaEntrega.get(Calendar.MONTH ):-1%>'
						 monthNullable="<%= true %>" yearParam="fechaEntregaAnio"
						 yearValue='<%=preautorizacion.getFechaEntregaRespuesta()!=null?fechaEntrega.get(Calendar.YEAR ):-1%>'
						 yearNullable="<%= true %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= true %>"/>
						 
					<%if(preautorizacion.getPreAutorizAsociada()!=null && preautorizacion.getPreAutorizAsociada()>0){ %>
						&nbsp;&nbsp;&nbsp;<label><liferay-ui:message key="preaut-asociada" /> : <%=preautorizacion.getPreAutorizAsociada() %> </label>
					<%} %>	  
			    </td>
		   </tr>

		</table>
	  </fieldset>	
	</div>
	

		    
	<br>
	<input type="hidden" name="<portlet:namespace />id_preautorizacion"
		id="<portlet:namespace />id_preautorizacion" value="<%=id_preautorizacion%>" />
		
	<input type="hidden" value='<%=esEdicion?"EDIT":"VIEW"%>' name="view" id="view" />
  	   
	<input
		id="<portlet:namespace />nom_seleccionado"
		name="<portlet:namespace />nom_seleccionado" type="hidden" value="" />
	<input id="<portlet:namespace />tipoNomenclador"
		name="<portlet:namespace />tipoNomenclador" type="hidden" value="" />

  <table>
	 <tr>
	  <td>

	<%if (esEdicion && 
    		(preautorizacion != null 
    		&& preautorizacion.getUltimoEstado() !=null 
    		&& preautorizacion.getUltimoEstado().getId().equalsIgnoreCase(WebKeysAutorizaciones.ESTADOS_PREAUTORIZACIONES[6][0]))
    		&& preautorizacion.getPreAutorizAsociada() == 0
			){ %> 
    <input id="<portlet:namespace />clonar"
		value="<liferay-ui:message key="Reintentar Caso"/>"
		title="<liferay-ui:message key="Reintentar con copia del caso" />"
		onClick="javascript: <portlet:namespace />reintentarCaso();"
		type="button" 
		 />
	<%}%>
   <%if (esEdicion){ %> 
    <input id="<portlet:namespace />guardar"
		value="<liferay-ui:message key="guardar"/>"
		title="<liferay-ui:message key="guardar" />"
		onClick="javascript: <portlet:namespace />salvarEdicion();"
		type="button" 
		 />
	<%}%>
		 
	<%if (preautorizacion != null && preautorizacion.getId() != null){ %>
	<!--  
	    <input id="<portlet:namespace />imagenes"
		value="Imágenes"
		title="<liferay-ui:message key="imagenes" />"
		onClick="javascript: <portlet:namespace />imagenesPreautorizacion(<%=preautorizacion.getId()%>);"
		type="button" 
		 />
	-->	 
		
        <input type="button" value="<liferay-ui:message key="next" />"
	     onClick="<portlet:namespace />siguienteSolapa();" />
	<%}%>
	</td>
	<td>
	<%if (preautorizacion != null && preautorizacion.getId() != null 
		&& (preautorizacion.getFechaEmail() == null ||
		  
		   (  ("OB".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId()) ||
			   "CA".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId()) ||
			   "GO".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId())
			  )	   &&  PreAutorizacionServiceUtil.tieneDocumentacionSinEnviar(preautorizacion.getId())  
				   /*preautorizacion.getFechaEmail2()==null*/
			  )
		  
		   ) 
		&& ( (preautorizacion.getImagenes().size()>0  
		     && (preautorizacion.getCodigosPresentados().size()>0 || preautorizacion.getMedicamentosPresentados().size()>0)) ||
			 preautorizacion.isAlojamiento()	
		   )
		&& esEdicion
		 /* && preautorizacion.getRequiereAutorizacion()  */ 
	){ %>
	   <table>
	      <tr>
	       <td>
<!-- Se comenta la condición subyacente para que aparezca el botï¿½n Enviar Mail para los estados CARGADO,OBSERVADO,GESTION OSPIM -- DS 27/02/2020  -->	       
	            <%/*if (preautorizacion != null && preautorizacion.getId() != null 
	            	      && preautorizacion.getFechaEmail() == null){*/%>
	          
	         <input id="<portlet:namespace />eMail"
		         value="<liferay-ui:message key="email-short"/>"
		         title="<liferay-ui:message key="email-short" />"
		         onClick="javascript: <portlet:namespace />emailPreautorizacion(<%=preautorizacion.getId()%>);"
		         type="button" 
		      />
		        <%/*}*/%> 
		   </td>
		   <td colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		   <td>
		      <%if (preautorizacion != null && preautorizacion.getId() != null 
		               && preautorizacion.getFechaEmail() != null){%>
		            <label><font size=3 color="#0000ff">Email enviado <%=sdf.format(preautorizacion.getFechaEmail())%></font></label>   
		      <%}%>          
		   </td>   
		 </tr>
		</table> 
	<%}else if(preautorizacion != null && preautorizacion.getId() != null && preautorizacion.getFechaEmail() != null){%>
	     <table>
	      <tr>
		     <td colspan="1">&nbsp;</td>
           </tr>
	     <tr>
	     <td colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	     <td>
		 <label><font size=3 color="#0000ff">Email enviado <%=sdf.format(preautorizacion.getFechaEmail())%></font></label>
		 </td>
		 <td colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<!--  		 
		 <td>
         <%if(preautorizacion != null && preautorizacion.getId() != null && preautorizacion.getFechaEmail2() != null){%>
            <label><font size=3 color="#0000ff">2do Email enviado <%=sdf.format(preautorizacion.getFechaEmail2())%></font></label>
         <%}%>
         </td>
-->         
        </tr>
         </table>
	<%}%>
     </td>
   	 </tr>
	</table> 
   <%if(preautorizacion != null && preautorizacion.getId() != null){ %>
       <table>
       <tr>
       	 <td>&nbsp;</td>
       </tr>
       <tr>
        <td> 
             <div id="<portlet:namespace />crm_auditoria">
			 	<table style="font-size: 8">
						<tr>
							<td><label><liferay-ui:message key="crm-contacto-alta-sec-usu" />:</label></td>
							<td><%=preautorizacion.getAlta_usr()!=null?
									(preautorizacion.getSeccionalDescripcionAltaUsr()==null?"CENTRAL":preautorizacion.getSeccionalDescripcionAltaUsr())+"/"+ preautorizacion.getAlta_usr():"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label><liferay-ui:message key="crm-contacto-alta-fec" />:</label></td>
							<td><%=preautorizacion.getAlta_fecha()!=null?sdf.format(preautorizacion.getAlta_fecha()):"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label>Modi.Usuario: </label></td>
							<td><%=preautorizacion.getModi_usr()!=null?preautorizacion.getModi_usr():"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label><liferay-ui:message key="crm-contacto-modi-fec" />:</label></td>
							<td><%=preautorizacion.getModi_fecha()!=null?sdf.format(preautorizacion.getModi_fecha()):"" %></td>
						</tr>
				</table>   
		     </div>
         </td>
         </tr>
       </table>
   <%}%>      
   <input type="hidden" value="" name="view" id="view" />
   <input type="hidden" value="" name="<portlet:namespace />marcaReinLiq" id="<portlet:namespace />marcaReinLiq" />
</form>

<script type="text/javascript">
var popupNM;
var popupPAT;
var popupDomicilio;
var popupImg;

<portlet:namespace />initDateFields();
<portlet:namespace />configuraCarga();
<portlet:namespace />manejoDiscapacidad();
<portlet:namespace />sincronizarIncluirBajas();

function <portlet:namespace />initDateFields(){
	jQuery('#<portlet:namespace />divResultadoActualizarOK').hide();
	jQuery('#<portlet:namespace />divReclamo').hide();
	
	if(<%=preautorizacion!=null && preautorizacion.getId()!=null && preautorizacion.getId()>0%> ){
	   jQuery("#<portlet:namespace />cuil").val('<%=preautorizacion.getAfiliado().getCuil_titular()%>');
	   jQuery("#<portlet:namespace />inte").val('<%=preautorizacion.getAfiliado().getInte() %>');
	   var esRecuperableSURStr = ('<%=preautorizacion.isRecuperableSUR()%>');
	   var esReclamoPrestacional = (('<%=preautorizacion.getIdReclamoPrestacional()==null || preautorizacion.getIdReclamoPrestacional()==0?false:true %>')==='true');
	   <portlet:namespace />buscarAfiliados();
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
       
       
       
       var url1 = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_preautorizacion_estudios_requeridos';
	   
	    jQuery.ajax({   
	       url: url1,
	       async:false,
	       success: function(data){
		   var obj = jQuery.parseJSON(data);
		   var recuperaSUR=(obj.recuperasur === 'true');
		   
		   if (!recuperaSUR){
			   jQuery('#<portlet:namespace />divRecuperaSUR').hide();
		   }else{
			   jQuery('#<portlet:namespace />divRecuperaSUR').show();
		   }
		   
	    }				                                                                                                                                                                                                                                                            
	   });								
       
	   if(!esReclamoPrestacional){
		   jQuery('#<portlet:namespace />divReclamo').hide();
	   }else{
		   jQuery('#<portlet:namespace />divReclamo').show();  
	   } 
       
    }
	<%if(preautorizacion!=null && preautorizacion.getId()!=null && preautorizacion.getId()>0 && !"AP".equals(preautorizacion.getUltimoEstado().getId())){%>
	<portlet:namespace />buscarDiagnosticoP();
	<%}%>
     
	try{
	  jQuery('#<portlet:namespace />id_prestador_aut').val('<%=preautorizacion.getPrestador()!=null && preautorizacion.getPrestador().getId_prestador()>0? preautorizacion.getPrestador().getId_prestador():""%>');
	  jQuery('#<portlet:namespace />cuit_prestador_aut').val('<%=preautorizacion.getPrestador()!=null && preautorizacion.getPrestador().getId_prestador()>0? preautorizacion.getPrestador().getCuit():""%>');
	  jQuery("#<portlet:namespace />nombre_prestador_aut").val('<%=preautorizacion.getPrestador()!=null && preautorizacion.getPrestador().getId_prestador()>0? preautorizacion.getPrestador().getDescripcion():""%>');
	}catch(e){
	  jQuery('#<portlet:namespace />id_prestador_aut').val('');
	  jQuery('#<portlet:namespace />cuit_prestador_aut').val('');
	  jQuery("#<portlet:namespace />nombre_prestador_aut").val('');	
	}	  
	
}


function <portlet:namespace />buscarPrestacionOnDiv(e){
			var evtobj=window.event? event : e
			var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
			var clase=jQuery("#<portlet:namespace />clasePrestacion").val();
			var marcaReinLiq=jQuery("#<portlet:namespace />marcaReinLiq").val();
			if(jQuery("#<portlet:namespace />nom_seleccionado").val() == "1" && (keyPressed==8 || keyPressed==46)){
				jQuery('#<portlet:namespace />codigoPrestacion').val("");
				jQuery("#<portlet:namespace />nom_seleccionado").val("");
				return false;
			}
			
		    var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionPrestacion").val();
		    if (nombre_nomenclador==null){
		    	nombre_nomenclador = '';
		    }    
		    if(jQuery("#<portlet:namespace />nom_seleccionado").val() != "1" && nombre_nomenclador.length>=6 ){
		    	if(popupNM==null)
		    	    popupNM = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupNM = null;}});
		    	
		    	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
			    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador);
			    url += "&tiponomenclador="+clase;	
			    url += "&muestrabaja=false";
			    url += "&soloActivos=true";
			    url += "&from=preautorizaciones";
			    url += "&marcareinliq="+ marcaReinLiq;
		       jQuery(popupNM).load(url);
				
		    }else{}
}

function <portlet:namespace />buscarNomencladorAutocompletar(){
	var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionPrestacion").val();
	var codigo_nomenclador=jQuery("#<portlet:namespace />codigoPrestacion").val();
	var clase=jQuery("#<portlet:namespace />clasePrestacion").val();
	var marcaReinLiq=jQuery("#<portlet:namespace />marcaReinLiq").val();
	if(clase==0 && nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
	        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
	}else {
	    	if(popupNM==null)
	    		popupNM = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupNM = null;}});
	    	
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
		    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&codigonomenclador='+encodeURI(codigo_nomenclador);
		    url += "&tiponomenclador="+clase;	
		    url += "&muestrabaja=false";
		    url += "&marcareinliq="+ marcaReinLiq;
		    url += "&from=preautorizaciones";
            jQuery(popupNM).load(url);
	}
		
}

function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
	seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
	if(popupNM){
		Liferay.Popup.close(popupNM);
	}
	
	var esDiscapacidad=jQuery('#<portlet:namespace />discapacidadChk').attr('checked');
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_nomenclador_importe&clase=';
	   url += tipoNomenclador;
	   url +='&codigo=';
	   url += codigo;
	   url +='&esdiscapacidad=';
	   url += esDiscapacidad;	
	
	
    jQuery.ajax({   
       url: url,
       async:false,
       success: function(data){
	   var obj = jQuery.parseJSON(data);
	   var requiereAutorizacion=(obj.requiereautorizacion === 'true');
	   var supra=(obj.supra === 'true');
	   var cirugia=(obj.cirugia === 'true');
//	   jQuery('#<portlet:namespace />importePrestacion').val(obj.importe);
       if(obj.idprestacion!=null && obj.idprestacion!=0 && "0"!=obj.idprestacion ){
	     jQuery('#<portlet:namespace />id_preautorizacion_codigo').val(obj.idprestacion);
       }
 
	   if (requiereAutorizacion)
	   {
		   jQuery('#<portlet:namespace />requiereAutorizacion').attr('checked', true);
	   }
	   else
	   {
		   jQuery('#<portlet:namespace />requiereAutorizacion').attr('checked', false);
	   }
	   
	   if (supra)
	   {
		   jQuery('#<portlet:namespace />supra').attr('checked', true);
	   }
	   else
	   {
		   jQuery('#<portlet:namespace />supra').attr('checked', false);
	   }

	   if (cirugia)
	   {
		   jQuery('#<portlet:namespace />cirugia').attr('checked', true);
	   }
	   else
	   {
		   jQuery('#<portlet:namespace />cirugia').attr('checked', false);
	   }
	   	
    }				                                                                                                                                                                                                                                                            

});

}

function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
	jQuery('#<portlet:namespace />codigoPrestacion').val(codigo);
	jQuery("#<portlet:namespace />descripcionPrestacion").val(descripcion);
	jQuery('#<portlet:namespace />clasePrestacion').val(tipoNomenclador);
	jQuery('#<portlet:namespace />tipoNomenclador').val(tipoNomenclador);
	jQuery("#<portlet:namespace />nom_seleccionado").val("1");
	
	if( '<%=prestacionPideTipoOpc%>'==codigo){
		jQuery('#<portlet:namespace />divOpc23101').show();
	}else{
		jQuery('#<portlet:namespace />divOpc23101').hide();
	}
}


function <portlet:namespace />validarCampos(){
	var result = true;
	var estado=jQuery("#<portlet:namespace />estadoPreautorizacion").val();
	var fechaRespuestaPSDia=jQuery("#<portlet:namespace />fechaRespuestaPSDia").val();
	var fechaRespuestaPSMes=jQuery("#<portlet:namespace />fechaRespuestaPSMes").val();
	var fechaRespuestaPSAnio=jQuery("#<portlet:namespace />fechaRespuestaPSAnio").val();
	var cuil_titu= jQuery('#<portlet:namespace />cuil').val();
	var inte  = jQuery('#<portlet:namespace />inte').val();
	var baja_fecha=jQuery('#<portlet:namespace />baja_fecha').val();
	var date = new Date();
	var fechaEntregaDia=jQuery("#<portlet:namespace />fechaEntregaDia").val();
	var fechaEntregaMes=jQuery("#<portlet:namespace />fechaEntregaMes").val();
	var fechaEntregaAnio=jQuery("#<portlet:namespace />fechaEntregaAnio").val();
	
	var fechaIncidente = jQuery('#<portlet:namespace />fechaIncidente').text();
	
	var diagnosticoId=jQuery("#<portlet:namespace />id_diagnostico").val();
	if (fechaIncidente.trim().length != '0' ){
		   var rpa = false; 
		   rpa = confirm ('El afiliado posee un caso de Unidad Operativa Desea Continuar?');
		   if (rpa != true){
			   return false;
		   }
	}
	
	var esDiscapacidad=jQuery('#<portlet:namespace />discapacidadChk').attr('checked');
	var isSupra=('<%=preautorizacion.isSupra()%>'==='true');
	var esMedicamento=jQuery('#<portlet:namespace />medicamentoChk').attr('checked');
	var esAlojamiento=jQuery('#<portlet:namespace />alojamientoChk').attr('checked');
	var fechaEmail=('<%=preautorizacion.getFechaEnvioMail_string()%>');
	
	if(baja_fecha !=null){
    	var valuesStart=baja_fecha.split("/");
    	var dateStart=new Date(valuesStart[2],(valuesStart[1]-1),valuesStart[0]);
    	if(dateStart<date){
     	  alert("Afiliado dado de Baja"); 
     	  return false;
    	}  
    }
	
	/*
    <portlet:namespace />sincronizarIncluirBajas();

    var incluirBajas = jQuery("#<portlet:namespace />incluir_bajas").is(":checked");

    if(baja_fecha != null && baja_fecha != "" && baja_fecha != "null"){
        var valuesStart = baja_fecha.split("/");

        if(valuesStart.length == 3){
            var dateStart = new Date(valuesStart[2], (valuesStart[1] - 1), valuesStart[0]);

            if(dateStart < date && !incluirBajas){
                alert("Afiliado dado de baja. Para continuar, marque 'Incluir bajas'.");
                return false;
            }
        }
    }
    */
	if (cuil_titu==null || cuil_titu=="" || cuil_titu=="null" || cuil_titu.length==0){
		
		alert("Debe ingresar el Nro de Cuil.");
		return false;
	}
	if (inte==null || inte=="" || inte=="null" || inte.length==0 ){
		
		alert("Debe ingresar el Nro de Integrante.");
		return false;
	}
	
	
	var esAfiliadoDiscapacitado=false;
	var fechaVtoDiscapacidad=null;
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
		esAfiliadoDiscapacitado=true;
		fechaVtoDiscapacidad=new Date( obj.fechaVto.replace( /(\d{2})-(\d{2})-(\d{4})/, "$2/$1/$3") );
	 }}});

	if(diagnosticoId==null || diagnosticoId=="" || diagnosticoId=="null" || diagnosticoId.length==0 ){
		alert("Debe cargar un diagnóstico o elegir la opción OTROS si lo desconoce"); 
   	    return false;
	}
	
	if(esDiscapacidad && !esAfiliadoDiscapacitado){
		alert("El Afiliado debe ser discapacitado"); 
   	    return false;
	}else if(esDiscapacidad && esAfiliadoDiscapacitado && date>fechaVtoDiscapacidad ){
		alert("El Afiliado tiene el certificado de Discapacidad Vencido");
		return false;
	}
	
	/*  Sacado a pedido de Carolina Doumerc 206-02-23 para cuando pidan ponerla de vuelta
	if(esDiscapacidad && estado!=null && "AU"== estado){
		var idPrestador=jQuery('#<portlet:namespace />id_prestador_aut').val();
		if(idPrestador==null || idPrestador=="" || idPrestador=="null"){
			alert("Debe seleccionar un prestador");
			return false;
		} 
	}
    */
	
    var esAfiliadoPrevencion=true;
    if( (jQuery("#<portlet:namespace />afi_tercerizadora").val() != "MOLINEROS POR ENSALUD" &&
    		jQuery("#<portlet:namespace />afi_tercerizadora").val() != "MOLINEROS POR CES") &&
    	(esAfiliadoDiscapacitado && jQuery("#<portlet:namespace />afi_tercerizadora").val() != "MONOTRIBUTO")	
    ){
    	esAfiliadoPrevencion=false;
    }
    
    if(esAfiliadoPrevencion){
    	
    	if( jQuery("#<portlet:namespace />nombre_plan").val() == "USUFRUCTO" ||
    			jQuery("#<portlet:namespace />nombre_plan").val() == "AMTIMA - SINDICATO" ||
    			jQuery("#<portlet:namespace />nombre_plan").val() == "SINDICATO"){
    	
    		esAfiliadoPrevencion=false;
        }    
    }
	
	if(!esAfiliadoPrevencion ){
		alert("El Afiliado no pertenece a ENSALUD o a CES"); 
   	    return false;
	}
	
	if(!esAlojamiento){
	  var prestacionesInconsistentes=false;
	  var msg=""
	  var url = "";

	  if(!esMedicamento){ 
		url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/verificar_prestaciones_preautorizacion&discapacidad=';
	  }else{
		url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/verificar_medicamentos_preautorizacion&discapacidad=';
	  }
	  url += esDiscapacidad;
	  url +="&estado="+estado;
      jQuery.ajax({   
      url: url,
      async:false,
      success: function(data){
	    var obj = jQuery.parseJSON(data);
	    var inconsistencia = obj.inconsistencia;
	    msg =obj.mensaje;
	    prestacionesInconsistentes= (inconsistencia === 'true');
	  }});

      if(prestacionesInconsistentes ){
		alert(msg); 
   	    return false;
	  }
    }
      
	var habilitaGestionOspimMedicamentos="NO";
    if(esMedicamento){
    	var nrodoc=jQuery("#<portlet:namespace />nroDoc").val();
    	url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/verificar_medicamentos_preautorizacion_especiales&nrodoc=';
    	url += nrodoc;
    	jQuery.ajax({   
    	    url: url,
    	    async:false,
    	    success: function(data){
    		  var obj = jQuery.parseJSON(data);
    		  habilitaGestionOspimMedicamentos= obj.gestionospim;
    		  if("SI"==habilitaGestionOspimMedicamentos && estado=='CA'){
    			  jQuery("#<portlet:namespace />estadoPreautorizacion").val("GO");
    			  estado="GO";
    		  };
    	}});
    }
		
	if(estado!="CA" && estado!="NR" && estado!="GO" && estado!="AP" && (fechaRespuestaPSDia=="" || fechaRespuestaPSMes=="" || fechaRespuestaPSAnio=="") && !esAlojamiento ){
		alert("Debe ingresar la fecha de Respuesta Tercerizadora");
		return false;
	}else if(estado=="CA" && ((fechaRespuestaPSDia!="" && fechaRespuestaPSMes!="" && fechaRespuestaPSAnio!="") ||
			                  (fechaEntregaDia!="" && fechaEntregaMes!="" && fechaEntregaAnio!="")
	                          )){
		alert("Debe cambiar el Estado de la Preautorización");
		return false;
		
	}else if(estado=="GO" && !(isSupra || "SI"==habilitaGestionOspimMedicamentos)  ){
            alert("No corresponde el Estado para esta Preautorización");
            return false;
	}else{
		var actualizaDomicilio;
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliado_datos&cuil_titular=';
		   url += cuil_titu;
		   url +='&inte=';
		   url += inte;
		var rta=true; 	
	    jQuery.ajax({   
	    url: url,
	    async:false,
	    success: function(data){
		   var obj = jQuery.parseJSON(data);
		   actualizaDomicilio=obj.actualizadomicilio;
		   if(actualizaDomicilio=='true' ){
			   alert("Debe actualizar Datos de Contacto");
			   rta=false;
//			   return false;
		   }
		   
		}});
	    
		if(!rta) return false;	
	}
	

	var fechaPreDia=jQuery("#<portlet:namespace />fechaPreAutorizacionDia").val();
	var fechaPreMes=jQuery("#<portlet:namespace />fechaPreAutorizacionMes").val();
	var fechaPreAnio=jQuery("#<portlet:namespace />fechaPreAutorizacionAnio").val();
	const inputDate = new Date(fechaPreAnio,fechaPreMes,fechaPreDia); 
	const currentDate = new Date();

	if (inputDate > currentDate) {
		alert("La fecha de la preautorizacion no puede ser una fecha futura");
		return false;
	}
	
		
	
/*	
	var estadoOspim=jQuery("#<portlet:namespace />estadoOSPIM").val();
	
	if(estado=="GO" && "" ==estadoOspim && fechaEmail!=null && ""!=fechaEmail){
		alert("Debe Seleccionar un Estado de Gestión OSPIM");
		return false;
	}
*/
	return true;
}

    function <portlet:namespace />salvarEdicion(){
        window.onbeforeunload = null;

        //<portlet:namespace />sincronizarIncluirBajas();

        document.getElementById("<portlet:namespace />estadoPreautorizacion").disabled=false;

        if (<portlet:namespace />validarCampos()) {
		document.getElementById("<portlet:namespace/>discapacidadChk").disabled=false;
		document.getElementById("<portlet:namespace/>medicamentoChk").disabled=false;
		document.getElementById("<portlet:namespace/>alojamientoChk").disabled=false;
		document.getElementById("<portlet:namespace/>protesisOrtChk").disabled=false;
		document.getElementById("<portlet:namespace/>artChk").disabled=false;
		jQuery("#<portlet:namespace/>divPrevencion").children().removeAttr("disabled");
		jQuery("#<portlet:namespace/>divGestionOspim").children().removeAttr("disabled");
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
		
		submitForm(document.<portlet:namespace />fmS, url);
	}
	return false;		
}

function <portlet:namespace />limpiarNomencladorAutocompletar(){
	jQuery("#<portlet:namespace />descripcionPrestacion").val('');
	jQuery("#<portlet:namespace />codigoPrestacion").val('');
	jQuery("#<portlet:namespace />clasePrestacion").val('');
	jQuery("#<portlet:namespace />tipoNomenclador").val('');
	jQuery("#<portlet:namespace />cantidadPrestacion").val('1');
	jQuery("#<portlet:namespace />importePrestacion").val('');
	jQuery("#<portlet:namespace />totalPrestacion").val('');
	jQuery("#<portlet:namespace />opciones23101").val('0');
	jQuery('#<portlet:namespace />requiereAutorizacion').attr("checked",false);
	jQuery('#<portlet:namespace />divOpc23101').hide();
	jQuery('#<portlet:namespace />supra').attr("checked",false);
}

<c:if test='<%="N".equalsIgnoreCase((String)request.getSession().getAttribute("esPopUp"))%>'>
//	window.onbeforeunload = function(){return "Esta seguro de abandonar la pï¿½gina?";};
</c:if>

function <portlet:namespace />configuraCarga(){
	var estado=jQuery("#<portlet:namespace />estadoPreautorizacion").val();
	if(estado=="OB"){
		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='CA']").remove();  
	}else if(estado=="RE"){
		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='CA']").remove();
		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='OB']").remove();
		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='AU']").remove();
//		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='DE']").remove(); 
	}else if(estado=="DE"){
		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='CA']").remove();
		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='OB']").remove();
		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='GO']").remove();
		jQuery("#<portlet:namespace />estadoPreautorizacion").find("option[value='NR']").remove();
	}
	/*
    if(estado=="GO"){
		document.getElementById("<portlet:namespace />estadoPreautorizacion").disabled=true;
	}
*/

	<portlet:namespace />habilitaComponentesPorEstado();
	<portlet:namespace />manejoDiscapacidad();
	<portlet:namespace />manejoMedicamento();
	<portlet:namespace />manejoAlojamiento();
	
	if(estado=="GO" || estado=="AU" ||  estado=="RE" ){ 
	/*if(estado=="GO" || estado=="AU" ||  estado=="RE" ){ //DS - Se saca Desestimado como estado Final 03/05/2018 */
		document.getElementById("<portlet:namespace />estadoPreautorizacion").disabled=true;
	}
	
}

function <portlet:namespace />imagenesPreautorizacion(id_Preautorizacion){
	var editarNom = {'<%= Constants.CMD %>':'imagenes',"id_preautorizacion":id_Preautorizacion};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:renderURL>';
    url = url+'&<%= Constants.CMD %>'+'='+'imagenes'+'&id_preautorizacion='+id_Preautorizacion;
    
    document.<portlet:namespace />fmS.method = 'post';
	submitForm(document.<portlet:namespace />fmS, url);
}


function <portlet:namespace />emailPreautorizacion(id_Preautorizacion){
	jQuery('#<portlet:namespace />eMail').hide();
	var noRequiere=false;
	var confirmarEnvio=false;
	var esMedicamento=jQuery('#<portlet:namespace />medicamentoChk').attr('checked');
	var esAlojamiento=jQuery('#<portlet:namespace />alojamientoChk').attr('checked');
	if(!esMedicamento && !esAlojamiento){
	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/verificar_prestaciones_preautorizacion_no_requiere_autorizacion';
	 jQuery.ajax({   
      url: url,
      async:false,
      success: function(data){
	    var obj = jQuery.parseJSON(data);
	    var norequiere = obj.norequiere;
	    noRequiere = (norequiere === 'true');
	  }});	
	}
    if(noRequiere){
      confirmarEnvio=confirm('Las prestaciones no requieren ser autorizadas. Desea enviarlas IGUALMENTE?');	  
    }
    
    
    if(!noRequiere || confirmarEnvio){
	 var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" />'+
	'<liferay-portlet:param name="cmd" value="email"/>'+
	'<liferay-portlet:param name="id_preautorizacion" value="__id_preautorizacion"/>'+
	'</liferay-portlet:renderURL>';
	 url = url.replace("__id_preautorizacion",id_Preautorizacion);
	 document.<portlet:namespace />fmS.method = 'post';
	 submitForm(document.<portlet:namespace />fmS, url);
    }
	
    if(!confirmarEnvio){
    	jQuery('#<portlet:namespace />eMail').show();
    }
}

function mostrarDomicilioAfiliado(){
	var cuil_titu= jQuery("#<portlet:namespace />cuil").val();
	var inte= jQuery("#<portlet:namespace />inte").val();
	var email;
	var actualizaDomicilio;
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_afiliado_datos&cuil_titular=';
	   url += cuil_titu;
	   url += '&inte=' + inte;
		
 jQuery.ajax({   
 url: url,
 async:false,
 success: function(data){
	   var obj = jQuery.parseJSON(data);
	   email=obj.email;
	}});
	popupDomicilio= Liferay.Popup({title:"<liferay-ui:message key="detalle-domicilio" />",modal:true,width:950,height:330,fixedcenter:true});
	var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/actualiza_domicilio&cuil_titular='+cuil_titu+'&inte='+inte+'&cmd=view' +'&email='+encodeURI(email);
	jQuery(popupDomicilio).load(url1);
	
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
	//var d_cod_area_laboral = jQuery("#<portlet:namespace />cod_area_tel_laboral").val();
	//var d_laboral = jQuery("#<portlet:namespace />tel_laboral").val();
	var d_cod_area_celu = jQuery("#<portlet:namespace />cod_area_celular").val();
	var d_celular = jQuery("#<portlet:namespace />celular").val();
	
	var d_email = jQuery("#<portlet:namespace />email").val();
	var d_email_original = jQuery("#<portlet:namespace />email_original").val();

//	var cuiltitular= jQuery('#<portlet:namespace />cuil_titular').val();
	var cuiltitular= jQuery('#<portlet:namespace />cuil').val();
	var integrante = jQuery("#<portlet:namespace />inte").val();
	
	var idPar = jQuery("#<portlet:namespace />idPar").val();
	if (idPar != "<%= WebKeysAfiliados.PARENTESCO_DEFAULT %>" &&
	    idPar != "<%= WebKeysAfiliados.CONYUGE_DEFAULT %>" &&
	    idPar != "<%= WebKeysAfiliados.CONCUBINO_DEFAULT %>") {
	  integrante = 0;
	}
	
	/*validamos los campos obligatorios*/
	if (trim(d_calle).length == 0){
		alert("Ingrese la calle del domicilio");
		jQuery('#<portlet:namespace/>calle').focus();
		return false;
	}
	
	if (
		 (trim(d_cod_area_tel) == '' && trim(d_telefono) != '') ||
		 (trim(d_cod_area_tel) != '' && trim(d_telefono) == '')
		){
		alert("El teléfono debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />telefono').focus();
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
	
/*if ((trim(d_cod_area_laboral) == '' && trim(d_laboral) != '') ||
		(trim(d_cod_area_laboral) != '' && trim(d_laboral) == '')
		){
		alert("El teléfono laboral debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />tel_laboral').focus();
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
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/actualiza_domicilio&id_parentesco=' + idPar;
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
					 //cod_area_laboral:d_cod_area_laboral,
					 //telefono_laboral:d_laboral,
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


function <portlet:namespace />agregarPreautorizacionCodigoNomenclador(){
	
	var idDetalle=jQuery('#<portlet:namespace />id_detalle').val();
	var idDetalleAux=jQuery('#<portlet:namespace />id_detalle_aux').val();
	var codigo=jQuery('#<portlet:namespace />codigoPrestacion').val();
	var descripcion=jQuery('#<portlet:namespace />descripcionPrestacion').val();
	var idPreautorizacionCodigo=jQuery('#<portlet:namespace />id_preautorizacion_codigo').val();
	var tipoNomenclador=jQuery('#<portlet:namespace/>clasePrestacion').val();
	var tipoApoyo=jQuery('#<portlet:namespace/>opciones23101').val();
	var tipoApoyoDescripcion=jQuery('#<portlet:namespace/>opciones23101').find('option:selected').text();
	
    var cuil_titu= jQuery('#<portlet:namespace />cuil').val();
    

	
	if(idDetalle!=null && codigo!=null && codigo !="" && descripcion!=null && descripcion !="" && tipoNomenclador!=null && tipoNomenclador !="" && tipoNomenclador !="0") {
		jQuery('#<portlet:namespace />agregandoCodigosPreautorizacion').show();
		
		var tipoNomencladorDescripcion=jQuery('#<portlet:namespace/>clasePrestacion').find('option:selected').text();
		var requiereAutorizacion=jQuery('#<portlet:namespace />requiereAutorizacion').attr('checked');
		var cantidad=jQuery('#<portlet:namespace />cantidadPrestacion').val();
		var importe=jQuery('#<portlet:namespace />importePrestacion').val();
		var supra=jQuery('#<portlet:namespace />supra').attr('checked');
		var cirugia=jQuery('#<portlet:namespace />cirugia').attr('checked');
		
		if(codigo!=null && codigo!=""){
			
//Validacion	
          if( <portlet:namespace />validaPrestacion()){

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_preautorizacionCodigoNomenclador'
				+	'&<%= Constants.CMD%>=' 
			url += 'addPrestacion'
			
			url +=
              '&codigo=' + encodeURI(codigo)
			+ '&descripcion=' + encodeURI(descripcion)
			+ '&tiponomenclador=' + encodeURI(tipoNomenclador)
			+ '&tiponomencladordescripcion=' + encodeURI(tipoNomencladorDescripcion)
			+ '&iddetalle=' + encodeURI(idDetalle)
			+ '&iddetalleaux=' + encodeURI(idDetalleAux)
			+ '&idpreautorizacioncodigo=' + encodeURI(idPreautorizacionCodigo)
			+ '&cantidad=' + cantidad
			+ '&importe=' + importe
			+ '&requiereautorizacion=' + requiereAutorizacion
			+ '&tipoapoyo=' + tipoApoyo
			+ '&tipoapoyodescripcion=' + tipoApoyoDescripcion
			+ '&supra=' + supra
			+ '&cirugia=' + cirugia
			+ '&cuil_titu=' + cuil_titu
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
	
			jQuery('#<portlet:namespace />codigospreautorizaciones').load(url, function() {
														jQuery('#<portlet:namespace />agregandoCodigosPreautorizacion').hide();
														<portlet:namespace />limpiarNomencladorAutocompletar();
														jQuery('#<portlet:namespace />id_detalle_aux').val('');
														jQuery('#<portlet:namespace />id_detalle').val('');


													    var url1 = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_preautorizacion_estudios_requeridos';
														   
													    jQuery.ajax({   
													       url: url1,
													       async:false,
													       success: function(data){
														   var obj = jQuery.parseJSON(data);
														   var requiereHistoriaClinica=(obj.requierehistoriaclinica === 'true');
														   var requiereEstudiosComplementarios=(obj.requiereestudioscomplementarios==='true');
														   var requiereBiopsia=(obj.requierebiopsia=== 'true');
														   var requiereAnatomiaPatologica=(obj.requiereanatomiapatologica === 'true');
														   var recuperaSUR=(obj.recuperasur === 'true');
														   if (!requiereHistoriaClinica){
//															   jQuery('#<portlet:namespace />chHC').attr('checked', false);
															   jQuery('#<portlet:namespace />divchHC').hide();
														   }else{
															   jQuery('#<portlet:namespace />divchHC').show();
//															   jQuery('#<portlet:namespace />chHC').attr('checked', true);
														   }
													   
														   if (!requiereEstudiosComplementarios){
//															   jQuery('#<portlet:namespace />chEC').attr('checked', false);
															   jQuery('#<portlet:namespace />divchEC').hide();
														   }else{
															   jQuery('#<portlet:namespace />divchEC').show();
//															   jQuery('#<portlet:namespace />chEC').attr('checked', true);
														   }	
														   
														   if (!requiereBiopsia){
//															   jQuery('#<portlet:namespace />chBI').attr('checked', false);
														   }else{
//															   jQuery('#<portlet:namespace />chBI').attr('checked', true);
														   }
														   
														   if (!requiereAnatomiaPatologica){
//															   jQuery('#<portlet:namespace />chAN').attr('checked', false);
														   }else{
//															   jQuery('#<portlet:namespace />chAN').attr('checked', false);
														   }
														   
														   if (!recuperaSUR){
															   jQuery('#<portlet:namespace />divRecuperaSUR').hide();
														   }else{
															   jQuery('#<portlet:namespace />divRecuperaSUR').show();
														   }
														   
													    }				                                                                                                                                                                                                                                                            
													});								
    
														
				 }
			 );
         }	
//Cierre validacion			
		}else{
			jQuery('#<portlet:namespace />agregandoCodigosPreautorizacion').hide();
			<portlet:namespace />limpiarNomencladorAutocompletar();
		}	
  }
	
}

function borraPreautorizacionCodigoNomenclador(idMod){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_preautorizacionCodigoNomenclador'
		+	'&<%= Constants.CMD%>=' + 'deletePrestacion'
		+ '&detalleid=' + encodeURI(idMod)
		+ '&esEdicion=' +"<%=esEdicion%>"; 	
		jQuery('#<portlet:namespace />codigospreautorizaciones').load(url, function() {
			
			
			var url1 = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_preautorizacion_estudios_requeridos';
			   
		    jQuery.ajax({   
		       url: url1,
		       async:false,
		       success: function(data){
			   var obj = jQuery.parseJSON(data);
			   var requiereHistoriaClinica=(obj.requierehistoriaclinica === 'true');
			   var requiereEstudiosComplementarios=(obj.requiereestudioscomplementarios==='true');
			   var requiereBiopsia=(obj.requierebiopsia=== 'true');
			   var requiereAnatomiaPatologica=(obj.requiereanatomiapatologica === 'true');
			   var recuperaSUR=(obj.recuperasur === 'true');
			   
			   if (!requiereHistoriaClinica){
				   jQuery('#<portlet:namespace />chHC').attr('checked', false);
				   jQuery('#<portlet:namespace />divchHC').hide();
			   }
		   
			   if (!requiereEstudiosComplementarios){
				   jQuery('#<portlet:namespace />chEC').attr('checked', false);
				   jQuery('#<portlet:namespace />divchEC').hide();
			   }	
			   
			   if (!requiereBiopsia){
				   jQuery('#<portlet:namespace />chBI').attr('checked', false);
			   }
			   
			   if (!requiereAnatomiaPatologica){
				   jQuery('#<portlet:namespace />chAN').attr('checked', false);
			   }
			   
			   if (!recuperaSUR){
				   jQuery('#<portlet:namespace />divRecuperaSUR').hide();
			   }else{
				   jQuery('#<portlet:namespace />divRecuperaSUR').show();
			   }
			   
			   
		    }				                                                                                                                                                                                                                                                            
		});								
			
		}	 );
}


function <portlet:namespace />siguienteSolapa() {
	//<portlet:namespace />sincronizarIncluirBajas();

	document.getElementById("<portlet:namespace/>discapacidadChk").disabled=false;
	document.getElementById("<portlet:namespace />estadoPreautorizacion").disabled=false;
	document.getElementById("<portlet:namespace/>medicamentoChk").disabled=false;
	document.getElementById("<portlet:namespace/>alojamientoChk").disabled=false;
	document.getElementById("<portlet:namespace/>protesisOrtChk").disabled=false;
	document.getElementById("<portlet:namespace/>artChk").disabled=false;
//	jQuery("#<portlet:namespace/>divPrevencion").children().removeAttr("disabled");
	jQuery("#<portlet:namespace/>divGestionOspim").children().removeAttr("disabled");
	
	    var accionEnCurso = document.<portlet:namespace />fmS.<portlet:namespace /><%= Constants.CMD %>.value;
		document.<portlet:namespace />fmS.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.MOVE %>';
		
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" /></portlet:actionURL>';
		url = url + '&accionEnCurso=' + accionEnCurso + '&moverATab=datos-imagenes' + "&esDatosTab=true"+ '&<%= Constants.CMD %>=<%=Constants.MOVE %>';
		
		document.<portlet:namespace />fmS.method = 'post';
	
		submitForm(document.<portlet:namespace />fmS, url);
}

function <portlet:namespace />habilitaComponentesPorEstado(){
	var estado=jQuery("#<portlet:namespace />estadoPreautorizacion").val();
	if(estado=="AU" || estado=="RE" || estado=="GO" || estado=="DE"){
//	if(estado=="AU" || estado=="RE" || estado=="GO"){ //DS - Se saco Desestimado como estado final 03/05/2018
	  document.getElementById("<portlet:namespace/>tipoEntrega").disabled=false;
	  document.getElementById("<portlet:namespace />fechaEntregaDia").disabled=false;
	  document.getElementById("<portlet:namespace />fechaEntregaMes").disabled=false;
	  document.getElementById("<portlet:namespace />fechaEntregaAnio").disabled=false;
	  
	  if(estado=="GO"){
//		  jQuery('#<portlet:namespace />divPrevencion').children().attr("disabled",'disabled');
		  jQuery('#<portlet:namespace />divGestionOspim').show();
		  
		  var estadoOSPIM=jQuery("#<portlet:namespace />estadoOSPIM").val();
		  if(estadoOSPIM=="AU" || estadoOSPIM=="RE" || estadoOSPIM=="DE"){
			  jQuery('#<portlet:namespace />divGestionOspim').children().attr("disabled",'disabled'); 
		  }
		  
	  }else{
//		  jQuery("#<portlet:namespace/>divPrevencion").children().removeAttr("disabled");
		  jQuery('#<portlet:namespace />divGestionOspim').hide();
	  }	
	}else{
		
	  document.getElementById("<portlet:namespace/>tipoEntrega").disabled=true;	
	  document.getElementById("<portlet:namespace />fechaEntregaDia").disabled=true;
	  document.getElementById("<portlet:namespace />fechaEntregaMes").disabled=true;
	  document.getElementById("<portlet:namespace />fechaEntregaAnio").disabled=true;
	}
	if(estado=="RE"){
		document.getElementById("<portlet:namespace />motivoRechazo").disabled=false;	 
	}else{
		document.getElementById("<portlet:namespace />motivoRechazo").disabled=true;
		jQuery('#<portlet:namespace />motivoRechazo').val("");
	}
	
}


function <portlet:namespace />validaPrestacion(){
	var idPreautorizacionCodigo=jQuery('#<portlet:namespace />id_preautorizacion_codigo').val();
    var cuil_titu= jQuery('#<portlet:namespace />cuil').val();
    var inte  = jQuery('#<portlet:namespace />inte').val();
    var idPreautorizacion=jQuery('#<portlet:namespace />nroPreautorizacion').val();
    
    jQuery('#<portlet:namespace />agregandoCodigosPreautorizacion').hide();
    var ret =true;
	if(cuil_titu== null || ""==cuil_titu){
		  alert("Debe seleccionar un afiliado");
		  return false;
	 }	
		
	 if(inte== null || ""==inte){
		  alert("Debe seleccionar un integrante");
		  return false;
	 }
	 
	 var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_preautorizacion_prestaciones_pendientes';
	 url+= "&cuil_titu="+cuil_titu;
	 url+= "&inte="+inte;
	 url+= "&idprestacion="+idPreautorizacionCodigo;
	 url+= "&idpreautorizacion="+idPreautorizacion;
	 jQuery.ajax({   
	       url: url,
	       async:false,
	       success: function(data){
	    	   var obj = jQuery.parseJSON(data);
	    	   var existePendiente=(obj.existeprestacionpendiente === 'true');
	    	   var mensaje=obj.mensaje;
	    	   if(existePendiente){
	    		   alert(mensaje);
	    	   }
	           ret= !existePendiente;
		   
	    }				                                                                                                                                                                                                                                                            
	 });								

	  
  return ret;
}


//-- Manejo Discapacidad --//
function <portlet:namespace />manejoDiscapacidad(){
	var esDiscapacidad=jQuery('#<portlet:namespace />discapacidadChk').attr('checked');
	
	jQuery('#<portlet:namespace />codigoPrestacion').val('');
	jQuery('#<portlet:namespace />descripcionPrestacion').val('');
	jQuery('#<portlet:namespace />clasePrestacion').val('');
	
	
	if(esDiscapacidad){
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="2"]').hide();
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="4"]').hide();
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="6"]').hide();
		
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="8"]').show();
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="3"]').show();
		jQuery('#<portlet:namespace />marcaReinLiq').val('<%=marcaReinLiqDiscapacidad%>');
		jQuery('#<portlet:namespace/>divDocumentacionTercerizadora').show();
		
		jQuery('#<portlet:namespace />alojamientoChk').attr('checked', false);
		jQuery('#<portlet:namespace />divAlojamiento').hide();
		jQuery('#<portlet:namespace/>divPrestador').show();
		
	}else{
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="8"]').hide();
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="3"]').show();
		
//		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="3"]').hide();
		
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="2"]').show();
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="4"]').show();
		jQuery('select[name*="<portlet:namespace/>clasePrestacion"] > option[value="6"]').show();
		jQuery('#<portlet:namespace />marcaReinLiq').val('');
		jQuery('#<portlet:namespace/>divDocumentacionTercerizadora').hide();
		
		jQuery('#<portlet:namespace />id_prestador_aut').val('');
		jQuery('#<portlet:namespace />cuit_prestador_aut').val('');
		jQuery("#<portlet:namespace />nombre_prestador_aut").val(''); 
		jQuery('#<portlet:namespace/>divPrestador').hide();
	}
}



//-- Fin Manejo Discapacidad --//

//-- Manejo Medicamentos --//
function <portlet:namespace />manejoMedicamento(){
	var esMedicamento=jQuery('#<portlet:namespace />medicamentoChk').attr('checked');
		
	jQuery('#<portlet:namespace />codigoPrestacion').val('');
	jQuery('#<portlet:namespace />descripcionPrestacion').val('');
	jQuery('#<portlet:namespace />clasePrestacion').val('');
	
	jQuery('#<portlet:namespace />id_medicamento').val('');
	jQuery('#<portlet:namespace />nombre_medicamento').val('');
	jQuery('#<portlet:namespace />troquel').val('');
	
	if(esMedicamento){
		jQuery('#<portlet:namespace />divTroquelPresentadoSeguimientoSur').show();
		jQuery('#<portlet:namespace/>divDocumentacionTercerizadora').hide();
		jQuery('#<portlet:namespace />divCodigoPresentadoSeguimientoSur').hide();
		
		jQuery('#<portlet:namespace />alojamientoChk').attr('checked', false);
		jQuery('#<portlet:namespace />divAlojamiento').hide();
		
	}else{
		jQuery('#<portlet:namespace />divTroquelPresentadoSeguimientoSur').hide();
		jQuery('#<portlet:namespace/>divDocumentacionTercerizadora').hide();
		jQuery('#<portlet:namespace />divCodigoPresentadoSeguimientoSur').show();
	}
}



//-- Fin Manejo Medicamento --//


function <portlet:namespace />seteaEstadoGestionOspim(){
	if(jQuery('#<portlet:namespace />estadoOSPIM').val()=="RE"){
		jQuery('#<portlet:namespace />gestionOSPIM').val("RC");
	}else{
		jQuery('#<portlet:namespace />gestionOSPIM').val("EX");
	}
}

function <portlet:namespace />agregarPreautorizacionMedicamento(){
	var idDetalle=jQuery('#<portlet:namespace />id_detalle_m').val();
	var idDetalleAux=jQuery('#<portlet:namespace />id_detalle_aux_m').val();
	var codigo=jQuery('#<portlet:namespace />id_medicamento').val();
	var troquel=jQuery('#<portlet:namespace />troquel').val();
	var descripcion=jQuery('#<portlet:namespace />nombre_medicamento').val();
	var idPreautorizacionMedicamento=jQuery('#<portlet:namespace />id_preautorizacion_medicamento').val();
	var cantidad=jQuery('#<portlet:namespace />cantidadMedicamento').val();
	var importe=jQuery('#<portlet:namespace />importeMedicamento').val();	
	
	if(idDetalle!=null && codigo!=null && codigo !="" && descripcion!=null && descripcion !="") {
		
		
		if(codigo!=null && codigo!=""){
			
//Validacion	
          /* if( <portlet:namespace />validaPrestacion()){ */

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_preautorizacionMedicamento'
				+	'&<%= Constants.CMD%>=' 
			url += 'addMedicamento'
			
			url +=
              '&codigo=' + encodeURI(codigo)
            + '&troquel=' + encodeURI(troquel)  
			+ '&descripcion=' + encodeURIComponent(descripcion)
			+ '&iddetalle=' + encodeURI(idDetalle)
			+ '&iddetalleaux=' + encodeURI(idDetalleAux)
			+ '&idpreautorizacionmedic=' + encodeURI(idPreautorizacionMedicamento)
			+ '&cantidad=' + cantidad
			+ '&importe=' + importe
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
	
			jQuery('#<portlet:namespace />troquelespreautorizaciones').load(url, function() {	});
         /* } */	
//Cierre validacion			
		/* }else{
			jQuery('#<portlet:namespace />agregandoCodigosPreautorizacion').hide();
			<portlet:namespace />limpiarNomencladorAutocompletar(); */
			<portlet:namespace />limpiarMedicamentoAutocompletar();
			jQuery('#<portlet:namespace />id_detalle_aux_m').val('');
			jQuery('#<portlet:namespace />id_detalle_m').val('');
		}	
  }
	
}

function <portlet:namespace />validaMedicamento(){
	var idPreautorizacionCodigo=jQuery('#<portlet:namespace />id_preautorizacion_medicamento').val();
    var cuil_titu= jQuery('#<portlet:namespace />cuil').val();
    var inte  = jQuery('#<portlet:namespace />inte').val();
    var idPreautorizacion=jQuery('#<portlet:namespace />nroPreautorizacion').val();
    
    var ret =true;
	if(cuil_titu== null || ""==cuil_titu){
		  alert("Debe seleccionar un afiliado");
		  return false;
	 }	
		
	 if(inte== null || ""==inte){
		  alert("Debe seleccionar un integrante");
		  return false;
	 }
	 
	 var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_preautorizacion_medicamentos_pendientes';
	 url+= "&cuil_titu="+cuil_titu;
	 url+= "&inte="+inte;
	 url+= "&idmedicamento="+idPreautorizacionCodigo;
	 url+= "&idpreautorizacion="+idPreautorizacion;
	 jQuery.ajax({   
	       url: url,
	       async:false,
	       success: function(data){
	    	   var obj = jQuery.parseJSON(data);
	    	   var existePendiente=(obj.existemedicamentopendiente === 'true');
	    	   var mensaje=obj.mensaje;
	    	   if(existePendiente){
	    		   alert(mensaje);
	    	   }
	           ret= !existePendiente;
		   
	    }				                                                                                                                                                                                                                                                            
	 });								

	  
  return ret;
}

function borraPreautorizacionMedicamento(idMod){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_preautorizacionMedicamento'
		+	'&<%= Constants.CMD%>=' + 'deleteMedicamento'
		+ '&detalleid=' + encodeURI(idMod)
		+ '&esEdicion=' +"<%=esEdicion%>"; 	
		
		jQuery('#<portlet:namespace />troquelespreautorizaciones').load(url, function() {});
}

function <portlet:namespace />limpiarMedicamentoAutocompletar(){
	jQuery('#<portlet:namespace />id_medicamento').val('');
	jQuery('#<portlet:namespace />nombre_medicamento').val("");
	jQuery('#<portlet:namespace />troquel').val('');
	jQuery("#<portlet:namespace />cantidadMedicamento").val('1');
	jQuery("#<portlet:namespace />importeMedicamento").val('');
	jQuery("#<portlet:namespace />totalMedicamento").val('');
	jQuery("#<portlet:namespace />divBtnBuscaMedicamento").show();
	/* jQuery("#<portlet:namespace />med_seleccionado").val(''); */

}

function editarPreautorizacionMedicamento(idMod,idModAux,idMedicamento,descripcion,troquel,cantidad,importe){
	jQuery('#<portlet:namespace />id_detalle_m').val(idMod);
	jQuery('#<portlet:namespace />id_detalle_aux_m').val(idModAux);
	jQuery('#<portlet:namespace />cantidadMedicamento').val(cantidad);
	jQuery('#<portlet:namespace />importeMedicamento').val(importe);	
	
	jQuery('#<portlet:namespace />nombre_medicamento').val(descripcion);
	jQuery('#<portlet:namespace/>id_medicamento').val(idMedicamento);
	jQuery('#<portlet:namespace/>troquel').val(troquel);
	
	jQuery('#<portlet:namespace />id_preautorizacion_medicamento').val(idMedicamento);
	
	if(cantidad!=null && importe!=null){
	  jQuery('#<portlet:namespace />totalMedicamento').val(cantidad * importe);
	} 

}


function <portlet:namespace />reintentarCaso() {
    
    var respuesta=confirm ('Está seguro que desea generar una nueva preautorizaciï¿½n copiando los datos de ï¿½sta misma '+'\nDesea continuar?');
		   
	if (respuesta) {
		
		window.onbeforeunload = null;

		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" />'+
		'<liferay-portlet:param name="cmd" value="copy"/>'+
		'</liferay-portlet:renderURL>';
		
		submitForm(document.<portlet:namespace />fmS, url);
		
	}
 
}


function enviarPS(){
	window.onbeforeunload = null;
	document.getElementById("<portlet:namespace />estadoPreautorizacion").disabled=false;

	
//	if (<portlet:namespace />validarCampos()) {
//		document.getElementById("<portlet:namespace/>discapacidadChk").disabled=false;
//		document.getElementById("<portlet:namespace/>medicamentoChk").disabled=false;
//		jQuery("#<portlet:namespace/>divPrevencion").children().removeAttr("disabled");
//		jQuery("#<portlet:namespace/>divGestionOspim").children().removeAttr("disabled");
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/autorizaciones/preautorizacion_editar" />'+
		'<liferay-portlet:param name="cmd" value="enviarPS"/>'+
		'</liferay-portlet:renderURL>';
		
		submitForm(document.<portlet:namespace />fmS, url);
//	}
	return false;		
}


function <portlet:namespace />manejoAlojamiento(){
	var esAlojamiento=jQuery('#<portlet:namespace />alojamientoChk').attr('checked');
	
	if(esAlojamiento){
		jQuery('#<portlet:namespace />divAlojamiento').show();
		jQuery('#<portlet:namespace />medicamentoChk').attr('checked', false);
		jQuery('#<portlet:namespace />discapacidadChk').attr('checked', false);
		jQuery('#<portlet:namespace />protesisOrtChk').attr('checked', false);
		jQuery('#<portlet:namespace />artChk').attr('checked', false);
		
		jQuery('#<portlet:namespace />divCodigoPresentadoSeguimientoSur').hide();
		jQuery('#<portlet:namespace />divTroquelPresentadoSeguimientoSur').hide();
		
	}else{
		jQuery('#<portlet:namespace />divAlojamiento').hide();
	}
}

/*
    function <portlet:namespace />sincronizarIncluirBajas(){
        var incluirBajas = jQuery("#<portlet:namespace />incluir_bajas").is(":checked");

        jQuery("#<portlet:namespace />baja").val(incluirBajas ? "true" : "false");

        jQuery("#<portlet:namespace />baja_filtro").prop("checked", incluirBajas);
        jQuery("#<portlet:namespace />baja_filtro").val(incluirBajas ? "true" : "false");

        jQuery("#<portlet:namespace />incluir_bajas_filtro").prop("checked", incluirBajas);
        jQuery("#<portlet:namespace />incluir_bajas_filtro").val(incluirBajas ? "true" : "false");
    }
 */   
</script>


<style>
.seccionVerificarDomicilio {
  vertical-align: top;
  text-align: center;
  padding: 10px 5px;  
  border: none;
  box-shadow: none;
  background: transparent;
}

.seccionVerificarDomicilio table {
  width: 100%;
  height: 100%;
}

.seccionVerificarDomicilio input[type="button"] {
  width: 100px;
  height: 30px;
  font-size: 12px;
  padding: 3px 0;
}

.seccionVerificarDomicilio label {
  display: block;
  margin-bottom: 8px;
}

fieldset.block-labels {
  width: 98%;
  margin-left: 1%;
  margin-right: 1%;
}

fieldset.block-labels table.lfr-table {
  width: 100%;
}

</style>
