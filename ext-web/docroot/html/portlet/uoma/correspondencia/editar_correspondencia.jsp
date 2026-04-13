<%@ include file="/html/portlet/uoma/init.jsp"%>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute(WebKeysUnidadOperativa.VIEW_UNIDAD_OPERATIVA);
	
	Correspondencia correspondencia=(Correspondencia)request.getAttribute(WebKeysCorrespondencia.CORRESPONDENCIA_EN_EDICION);
	
	if(correspondencia!=null){	
		request.setAttribute("domicilio_editremi", correspondencia.getDomicilioRemitente());
		request.setAttribute("domicilio_editdesti", correspondencia.getDomicilioDestinatario());
	}
	
	boolean editar=correspondencia!=null?true:false;
	
	List<TipoCorrespondencia> tiposCorresp= CorrespondenciaServiceImpl.buscarTipoCorrespondencia();		
		
	
	boolean esView = false;
	if (viewStr != null){
		esView = true;
	}
	
	Calendar fechaRecepcion=null;
	if(correspondencia!=null){
		fechaRecepcion=Calendar.getInstance();
		fechaRecepcion.setTime(correspondencia.getFechaEnvioRecepcion());
	}	
	
	Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
	String destino=correspondencia!=null&&correspondencia.getDestino()!=null?correspondencia.getDestino():"";
	String lugarRecep=correspondencia!=null&&correspondencia.getLugarRecepcion()!=null?correspondencia.getLugarRecepcion():"";
	int tipoCorr= correspondencia!=null?correspondencia.getTipo().getIdTipo():0;
	String edificioRemitente=correspondencia!=null&&correspondencia.getEdificioRemitente()!=null?correspondencia.getEdificioRemitente():"";
	String edificioDestinatario=correspondencia!=null&&correspondencia.getEdificioDestinatario()!=null?correspondencia.getEdificioDestinatario():"";
	String observaciones=correspondencia!=null&&correspondencia.getObservaciones()!=null?correspondencia.getObservaciones():"";
%>
	
	

<form name="formulariox" id="formulariox" method="post">

	<fieldset class="block-labels"><legend><liferay-ui:message key="editar-correspondencia" /></legend>
	<table class="lfr-table" width="100%">
		<tr>			
			<td><label><liferay-ui:message key="destino" />:</label></td>
			<td>
				<select name="<portlet:namespace/>destino" id="<portlet:namespace/>destino" onChange="javascript:<portlet:namespace />cambiaDestino();">																
					<option value="ENTRANTE" <%=destino.equals("ENTRANTE")? "selected":""%> default="true">Entrante</option>
					<option value="SALIENTE" <%=destino.equals("SALIENTE")? "selected":""%>>Saliente</option>													
				</select>
			</td>
			<td>
				<div id="<portlet:namespace />fechaRecepcionLabel" name="<portlet:namespace />fechaRecepcionLabel">
					<label><liferay-ui:message key="fecha-recepcion" />:</label>
				</div>
			 	<div id="<portlet:namespace />fechaEnvioLabel" name="<portlet:namespace />fechaEnvioLabel">
			 		<label><liferay-ui:message key="fecha-envio" />:</label>
			 	</div>
			</td>
			<td colspan="6">
				<liferay-ui:input-date dayParam="fechaDiaRecepcion"
				dayValue="<%= correspondencia!=null?fechaRecepcion.get(Calendar.DATE):fechaHoy.get(Calendar.DATE)%>" monthParam="fechaMesRecepcion"
				monthValue="<%= correspondencia!=null?fechaRecepcion.get(Calendar.MONTH):fechaHoy.get(Calendar.MONTH) %>"
				yearParam="fechaAnioRecepcion" yearValue="<%= correspondencia!=null?fechaRecepcion.get(Calendar.YEAR):fechaHoy.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 120 %>"
				yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 120 %>"
				firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
				disabled="<%= esView %>" />
			</td>
		</tr>		
		<tr><td colspan="9">&nbsp;</td></tr>		
		<tr>			
			<td colspan="2">
				<div id="<portlet:namespace />edificioRecepcion" name="<portlet:namespace />edificioRecepcion">				 
					<label><liferay-ui:message key="lugar-recepcion" />:</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;
					<select name="<portlet:namespace/>edificioRecepcion" id="<portlet:namespace/>edificioRecepcion">																						
						<option value="MEXICO" default="true" <%=lugarRecep.equals("MEXICO")? "selected":""%>>Mexico</option>
						<option value="SANJUAN" <%=lugarRecep.equals("SANJUAN")? "selected":""%>>San Juan</option>	
						<option value="">Sin Seleccionar</option>												
					</select>
				</div>				
			</td>							
			<td>
				<div id="<portlet:namespace />tipoCorrLabel" name="<portlet:namespace />tipoCorrLabel"> 
					<label><liferay-ui:message key="tipo-correspondencia" />:</label>
				</div>
				<div id="<portlet:namespace />tipoEnvioLabel" name="<portlet:namespace />tipoEnvioLabel"> 
					<label><liferay-ui:message key="tipo-envio" />:</label>
				</div>
			</td>
			<td>
				<table>
				<tr>
				<td>				
				<div id="<portlet:namespace />tipoEnvioTable" name="<portlet:namespace />tipoEnvioTable">
			    <select name="<portlet:namespace/>tipoEnvio" id="<portlet:namespace/>tipoEnvio" onChange="javascript:<portlet:namespace />cambiaTipoEnvio()">											
					<option value="SIMPLE"  <%=null!=correspondencia&&correspondencia.getTipoEnvio().equals("SIMPLE")? "selected":""%>>Simple</option>							
					<option value="ARGENTINO"  <%=null!=correspondencia&&correspondencia.getTipoEnvio().equals("ARGENTINO")? "selected":""%>>Argentino</option>																		
					<option value="CODIGO" <%=null!=correspondencia&&correspondencia.getTipoEnvio().equals("CODIGO")? "selected":""%>>Código Oblea</option>																							
				</select>
				</div>				
				</td>
				<td>				
				<div id="<portlet:namespace />codigoObleaDiv" name="<portlet:namespace />codigoObleaDiv">
					<label><liferay-ui:message key="cod-seg" />:</label>
					<input id="<portlet:namespace />codigoOblea" name="<portlet:namespace />codigoOblea" size="20" maxlength="100" type="text" value="<%=correspondencia!=null?String.valueOf(correspondencia.getOblea()):""%>" />
				</div>
				</td>
				</tr>
				</table>
				<div id="<portlet:namespace />tipoCorrTable" name="<portlet:namespace />tipoCorrTable">
				<table>
					<tr>
						<td>
							<label><liferay-ui:message key="gastos-seccional" /></label>
						</td>
						<td>
							<input type="checkbox" name="<portlet:namespace />gastos_seccional" id="<portlet:namespace />gastos_seccional" value="true" <% if (correspondencia != null && correspondencia.isGastoSeccional()) {%> checked="checked" <%} %>/>
						</td>
						<td>
							<label><liferay-ui:message key="reintegros" /></label>
						</td>
						<td>
							<input type="checkbox" name="<portlet:namespace />reintegros" id="<portlet:namespace />reintegros" value="true" <% if (correspondencia != null && correspondencia.isReintegro()) {%> checked="checked" <%} %>/>
						</td>
					</tr>
					<tr>
						<td>
							<label><liferay-ui:message key="padrones" /></label>
						</td>
						<td>
							<input type="checkbox" name="<portlet:namespace />padrones" id="<portlet:namespace />padrones" value="true" <% if (correspondencia != null && correspondencia.isPadrones()) {%> checked="checked" <%} %>/>
						</td>
						<td>
							<label><liferay-ui:message key="discapacidad" /></label>
						</td>
						<td>
							<input type="checkbox" name="<portlet:namespace />discapacidad" id="<portlet:namespace />discapacidad" value="true" <% if (correspondencia != null && correspondencia.isDiscapacidad()) {%> checked="checked" <%} %>/>
						</td>
					</tr>
					<tr>
						<td>
							<label><liferay-ui:message key="otros" /></label>
						</td>
						<td>
							<input type="checkbox" name="<portlet:namespace />otros" id="<portlet:namespace />otros" value="true" <% if (correspondencia != null && correspondencia.isOtros()) {%> checked="checked" <%} %>/>
						</td>
						<td>
							<label><liferay-ui:message key="facturacion" /></label>
						</td>
						<td>
							<input type="checkbox" name="<portlet:namespace />facturacion" id="<portlet:namespace />facturacion" value="true" <% if (correspondencia != null && correspondencia.isFacturacion()) {%> checked="checked" <%} %>/>
						</td>							
					</tr>
					<tr>
						<td>
							<label><liferay-ui:message key="documentacion" /></label>
						</td>
						<td>
							<input type="checkbox" name="<portlet:namespace />documentacion" id="<portlet:namespace />documentacion" value="true" <% if (correspondencia != null && correspondencia.isDocumentacion()) {%> checked="checked" <%} %>/>
						</td>
						<td>
							<label><liferay-ui:message key="tesoreria" /></label>
						</td>
						<td>
							<input type="checkbox" name="<portlet:namespace />tesoreria" id="<portlet:namespace />tesoreria" value="true" <% if (correspondencia != null && correspondencia.isTesoreria()) {%> checked="checked" <%} %>/>
						</td>													
					</tr>
					<tr>
						<td>
							<label><liferay-ui:message key="medicamentos" /></label>
						</td>
						<td colspan="3">
							<input type="checkbox" name="<portlet:namespace />medicamentos" id="<portlet:namespace />medicamentos" value="true" <% if (correspondencia != null && correspondencia.isMedicamentos()) {%> checked="checked" <%} %>/>
						</td>
					</tr>
				</table>
				</div>
			</td>
			<!--td colspan="6">
				<select name="<portlet:namespace/>tipoCorresp" id="<portlet:namespace/>tipoCorresp" onChange="javascript:<portlet:namespace />cambiaDestino();">
					<%for(TipoCorrespondencia tipo:tiposCorresp){%>
						<option value="<%=tipo.getIdTipo()%>" <%=tipoCorr==tipo.getIdTipo()?"selected":""%>><%=tipo.getDescripcion()%></option>									
					<%}%>												
				</select>
			</td-->		
		</tr>
		<tr><td colspan="9">&nbsp;</td></tr>		
	</table>
	<div id="<portlet:namespace />grupoRemitente" name="<portlet:namespace />grupoRemitente">
	<fieldset class="block-labels"> 
	<legend>
					<div id="<portlet:namespace />labelRemitente" name="<portlet:namespace />labelRemitente">
						<label><liferay-ui:message key="remitente" /></label>
					</div>	
					<div id="<portlet:namespace />labelDestinatario" name="<portlet:namespace />labelDestinatario">
						<label><liferay-ui:message key="destinatario" /></label>
					</div>		 			
	</legend>
	<table class="lfr-table" width="100%">
		<tr>
			<td width="6%">			
				<liferay-ui:message key="tipo-remitente" />:
			</td>								
			<td  colspan="5">
				<select name="<portlet:namespace/>lugarRemitente" id="<portlet:namespace/>lugarRemitente" onChange="javascript:<portlet:namespace />cambiaLugar()">											
					<option value="Seccional" default="true" <%=edificioRemitente.equals("Seccional")? "selected":""%>>Seccional</option>							
					<option value="PRESTA-RAZON" default="true" <%=edificioRemitente.equals("PRESTA-RAZON")? "selected":""%>>Prestador/Razón Social</option-->																		
					<option value="MEXICO" <%=edificioRemitente.equals("MEXICO")?"selected":""%>>Mexico</option>
					<option value="SANJUAN" <%=edificioRemitente.equals("SANJUAN")?"selected":""%>>San Juan</option>
					<option value="FARMACIA" <%=edificioRemitente.equals("FARMACIA")?"selected":""%>>Farmacia</option>
					<option value="AFILIADO" <%=edificioRemitente.equals("AFILIADO")?"selected":""%>>Afiliado</option>
					<!--option value="Particular" default="true" <%=edificioRemitente.equals("Particular")? "selected":""%>>Particular</option-->													
				</select>
			</td>
		</tr>
		<tr>
			<td colspan="6">&nbsp;</td>			
		</tr>
		<tr>
			<td><label><liferay-ui:message key="razon-prestador" />:</label></td>							
			<td width="6%">
				<!--input id="<portlet:namespace />apellidoRemitente" name="<portlet:namespace />apellidoRemitente" size="20" maxlength="100" type="text" value="<%=correspondencia!=null?correspondencia.getApellidoRemitente():""%>" /></td-->
				<input id="<portlet:namespace />razonPrestadorRemitente" name="<portlet:namespace />razonPrestadorRemitente" size="20" maxlength="100" type="text" value="<%=correspondencia!=null?correspondencia.getRazonPrestadorRemitente():""%>" />
			</td>
			<td width="10%"><label><liferay-ui:message key="datos-factura" />:</label></td>							
			<td  colspan="3">
				<input id="<portlet:namespace />datos_factura" name="<portlet:namespace />datos_factura" size="20" maxlength="100" type="text" value="<%=correspondencia!=null?correspondencia.getDatosFactura():""%>" />
			</td>								
		</tr>
		<tr>
			<td colspan="6">&nbsp;</td>
		</tr>		
		<tr>
			<td><label><liferay-ui:message key="farmacia" />:</label></td>
			<td  colspan="5">
				&nbsp;&nbsp;&nbsp;
				<liferay-util:include page='/html/portlet/uoma/correspondencia/busqueda_farmacia.jsp'>
					<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
					<liferay-util:param value="<%=null!=correspondencia?correspondencia.getCodFarmacia():\"\"%>" name="id_farmacia" />
					<liferay-util:param value="<%=null!=correspondencia?correspondencia.getFarmacia():\"\"%>" name="farmacia" />
				</liferay-util:include>																			
			</td>
		</tr>
		<tr>
			<td  colspan="6">&nbsp;</td>
		</tr>
		
		<tr>
			<td  colspan="6">
				<div id="<portlet:namespace />divRemitenteDir" name="<portlet:namespace />divRemitenteDir">
						<liferay-util:include page='/html/portlet/uoma/editar_domicilio.jsp'>
							<liferay-util:param name="edit_mode" value="true"  />
							<liferay-util:param name="prefijo" value="remi" />
						</liferay-util:include>
				</div>		
			</td>
		</tr>
		<tr>
			<td  colspan="6">&nbsp;</td>
		</tr>
		<tr>
			<td>			
				<label><liferay-ui:message key="seccional" />:</label>
			</td>					
			<td colspan="5">
				<liferay-util:include page='/html/portlet/uoma/busqueda_seccional_incidente.jsp'>
					<liferay-util:param name="prefijo" value="remi" />
					<liferay-util:param name="id_seccional_rremi" value='<%=null!=correspondencia&&null!=correspondencia.getSeccionalRemitente()?String.valueOf(correspondencia.getSeccionalRemitente().getIdSeccional()):""%>' />
					<liferay-util:param name="seccional_rremi" value='<%=null!=correspondencia&&null!=correspondencia.getSeccionalRemitente()?correspondencia.getSeccionalRemitente().getDescripcion():""%>' />
				</liferay-util:include>									
			</td>								
		</tr>
	</table>
	</fieldset>
	</div>	
	<table class="lfr-table" width="100%">
		<tr>
			<td colspan="9">&nbsp;</td>
		</tr>
		<tr>			
			<td>
				<legend><liferay-ui:message key="observaciones" /></legend>
			</td>			
			<td colspan="8">
				<textarea id="observaciones" name="observaciones" cols="140" rows="5"><%=observaciones%></textarea>
			</td>
		</tr>		
		<tr>
			<td colspan="9">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="9" align="center">				
				<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveCorrespondencia();" />
				<%if(null!=correspondencia){%>
					<input type="button" value="<liferay-ui:message key="nueva-correspondencia" />" onClick="<portlet:namespace />nuevaCorrespondencia();" />
				<%}%>				
			</td>		
		</tr>
		
	</table>
	
	<input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>"/>
	<input type="hidden" name="<portlet:namespace />id_correspondencia" id="<portlet:namespace />id_correspondencia" value="<%=correspondencia!=null?correspondencia.getIdCorrespondencia():""%>" />
	<input type="hidden" name="<portlet:namespace />id_domicilio_remitente" id="<portlet:namespace />id_domicilio_remitente" value="<%=correspondencia!=null?correspondencia.getDomicilioRemitente().getId_domicilio():""%>" />
	<input type="hidden" name="<portlet:namespace />id_domicilio_destinatario" id="<portlet:namespace />id_domicilio_destinatario" value="<%=correspondencia!=null?correspondencia.getDomicilioDestinatario().getId_domicilio():""%>" />
	
	</fieldset>
</form>
<script type="text/javascript">

<%if(correspondencia!=null){
	if(null!=correspondencia.getDestino() && correspondencia.getDestino().equals("ENTRANTE")){%>
		jQuery('#<portlet:namespace />fechaRecepcionLabel').show();
		jQuery('#<portlet:namespace />fechaEnvioLabel').hide();
		//jQuery('#<portlet:namespace />grupoDestinatario').hide();
		jQuery('#<portlet:namespace />grupoRemitente').show();
		jQuery('#<portlet:namespace />codigoObleaDiv').hide();
		jQuery('#<portlet:namespace />tipoEnvioTable').hide();
		jQuery('#<portlet:namespace />tipoEnvioLabel').hide();
		jQuery('#<portlet:namespace />labelRemitente').show();
		jQuery('#<portlet:namespace />labelDestinatario').hide();
	<%}else if(null!=correspondencia.getDestino() && correspondencia.getDestino().equals("SALIENTE")){%>
		jQuery('#<portlet:namespace />fechaRecepcionLabel').hide();
		jQuery('#<portlet:namespace />fechaEnvioLabel').show();
		jQuery('#<portlet:namespace />grupoDestinatario').show();
		//jQuery('#<portlet:namespace />grupoRemitente').hide();
		//jQuery('#<portlet:namespace />codigoObleaDiv').hide();
		jQuery('#<portlet:namespace />tipoCorrLabel').hide();
		jQuery('#<portlet:namespace />tipoCorrTable').hide();
		jQuery('#<portlet:namespace />edificioRecepcion').hide();
		jQuery('#<portlet:namespace />labelRemitente').hide();
		jQuery('#<portlet:namespace />labelDestinatario').show();	
	<%  if(null!=correspondencia.getTipoEnvio() && correspondencia.getTipoEnvio().equals("CODIGO")){%>
			jQuery('#<portlet:namespace />codigoObleaDiv').show();		
	<%  }
	}
	if(edificioRemitente.equals("Seccional")){%>		
		jQuery('#<portlet:namespace />seccRemitenteDiv').show();
		//jQuery('#<portlet:namespace />divRemitenteDir').hide();
	<%}else if(edificioRemitente.equals("Particular") || edificioRemitente==null){%>		
		jQuery('#<portlet:namespace />seccRemitenteDiv').hide();
		jQuery('#<portlet:namespace />divRemitenteDir').show();
	<%}else if(edificioRemitente.equals("MEXICO")||edificioRemitente.equals("SANJUAN")){%>		
		//jQuery('#<portlet:namespace />seccRemitenteDiv').hide();
		//jQuery('#<portlet:namespace />divRemitenteDir').hide();
	<%}
	if(edificioDestinatario.equals("Seccional")){%>		
		jQuery('#<portlet:namespace />seccDestinaDiv').show();
		jQuery('#<portlet:namespace />divDestinoDir').hide();
	<%}else if(edificioDestinatario.equals("Particular")){%>		
		jQuery('#<portlet:namespace />seccDestinaDiv').hide();
		jQuery('#<portlet:namespace />divDestinoDir').show();
	<%}else if(edificioDestinatario.equals("MEXICO")||edificioDestinatario.equals("SANJUAN")){%>		
		jQuery('#<portlet:namespace />seccDestinaDiv').hide();
		jQuery('#<portlet:namespace />divDestinoDir').hide();
	<%}%>
		
<%}else{%>	
	jQuery('#<portlet:namespace />divRemitenteDir').show();
	jQuery('#<portlet:namespace />fechaEnvioLabel').hide();
	jQuery('#<portlet:namespace />fechaEnvioLabel').hide();
	jQuery('#<portlet:namespace />divDestinoDir').hide();
	jQuery('#<portlet:namespace />seccRemitenteDiv').show();
	jQuery('#<portlet:namespace />seccDestinaDiv').hide();
	//jQuery('#<portlet:namespace />grupoDestinatario').hide();
	jQuery('#<portlet:namespace />grupoRemitente').show();	
	jQuery('#<portlet:namespace />tipoEnvioTable').hide();
	jQuery('#<portlet:namespace />tipoEnvioLabel').hide();
	jQuery('#<portlet:namespace />tipoCorrLabel').hide();
	jQuery('#<portlet:namespace />codigoObleaDiv').hide();
	jQuery('#<portlet:namespace />labelRemitente').show();
	jQuery('#<portlet:namespace />labelDestinatario').hide();
<%}%>

function <portlet:namespace />nuevaCorrespondencia() {		
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/editar_correspondencia" /></portlet:renderURL>';
		url=url+'&nueva=true';
		document.formulariox.method = 'post';
		submitForm(document.formulariox, url);
}

function <portlet:namespace />saveCorrespondencia() {
			<%if(null!=correspondencia){%>				
				document.formulariox.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE%>";
			<%}else{%>
				document.formulariox.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.ADD %>";
			<%}%>						
				
			url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/uoma/editar_correspondencia'/></portlet:actionURL>";
									
			document.formulariox.action=url;
			submitForm(document.formulariox, url);
}
function <portlet:namespace />cambiaDestino(){		
		var destino=jQuery('#<portlet:namespace/>destino').val();
		if(destino=="ENTRANTE"){			
			jQuery('#<portlet:namespace />fechaEnvioLabel').hide();			
			jQuery('#<portlet:namespace />fechaRecepcionLabel').show();
			jQuery('#<portlet:namespace />edificioRecepcion').show();			
			//jQuery('#<portlet:namespace />grupoDestinatario').hide();
			jQuery('#<portlet:namespace />grupoRemitente').show();		
			jQuery('#<portlet:namespace />tipoCorrLabel').show();
			jQuery('#<portlet:namespace />tipoCorrTable').show();
			jQuery('#<portlet:namespace />tipoEnvioLabel').hide();	
			jQuery('#<portlet:namespace />tipoEnvioTable').hide();
			jQuery('#<portlet:namespace />codigoObleaDiv').hide();
			jQuery('#<portlet:namespace />labelRemitente').show();
			jQuery('#<portlet:namespace />labelDestinatario').hide();
		}else{
			jQuery('#<portlet:namespace />fechaRecepcionLabel').hide();			
			jQuery('#<portlet:namespace />fechaEnvioLabel').show();		
			jQuery('#<portlet:namespace />edificioRecepcion').hide();
			//jQuery('#<portlet:namespace />grupoDestinatario').show();
			//jQuery('#<portlet:namespace />grupoRemitente').hide();
			jQuery('#<portlet:namespace />tipoCorrLabel').hide();
			jQuery('#<portlet:namespace />tipoCorrTable').hide();
			jQuery('#<portlet:namespace />tipoEnvioLabel').show();
			jQuery('#<portlet:namespace />tipoEnvioTable').show();
			jQuery('#<portlet:namespace />divDestinoDir').show();
			jQuery('#<portlet:namespace />codigoObleaDiv').show();
			jQuery('#<portlet:namespace />labelRemitente').hide();
			jQuery('#<portlet:namespace />labelDestinatario').show();			
		}		
			
}
function <portlet:namespace />cambiaLugar(){		
		var remitente=jQuery('#<portlet:namespace/>lugarRemitente').val();
		var destinatario=jQuery('#<portlet:namespace/>lugarDestinatario').val();
		if(remitente=="Particular"){
			jQuery('#<portlet:namespace />divRemitenteDir').show();
			jQuery('#<portlet:namespace />seccRemitenteDiv').hide();									
		}else if(remitente=="Seccional"){			
			//jQuery('#<portlet:namespace />divRemitenteDir').hide();
			jQuery('#<portlet:namespace />seccRemitenteDiv').show();
		}else {
			//jQuery('#<portlet:namespace />divRemitenteDir').hide();
			//jQuery('#<portlet:namespace />seccRemitenteDiv').hide();
		}
		if(destinatario=="Particular"){						
			jQuery('#<portlet:namespace />divDestinoDir').show();
			jQuery('#<portlet:namespace />seccDestinaDiv').hide();						
		}else if(destinatario=="Seccional"){			
			jQuery('#<portlet:namespace />divDestinoDir').hide();
			jQuery('#<portlet:namespace />seccDestinaDiv').show();
		}else{
			jQuery('#<portlet:namespace />divDestinoDir').hide();
			jQuery('#<portlet:namespace />seccDestinaDiv').hide();
		}				
			
}
function <portlet:namespace />cambiaTipoEnvio(){		
		/*var tipoEnvio=jQuery('#<portlet:namespace/>tipoEnvio').val();		
		if(tipoEnvio=="CODIGO"){
			jQuery('#<portlet:namespace />codigoObleaDiv').show();												
		}else{			
			jQuery('#<portlet:namespace />codigoObleaDiv').hide();
		}*/	
}
</script>