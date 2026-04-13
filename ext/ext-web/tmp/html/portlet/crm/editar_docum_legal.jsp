<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/crm/init.jsp"%>
<%
    String portlet_name = ParamUtil.getString(request, "portlet_name");
    if(renderResponse.getNamespace().equals("_JUD_1_")){
	   portlet_name = "judicial";
    }else{
	   portlet_name = "afiliados";
    }
    
    
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	PortletURL portletURL = renderResponse.createRenderURL();
	/* String viewStr = (String)request.getAttribute("view"); */
	String accion = (String) request.getAttribute(Constants.CMD);

	Boolean esAfiliado = (Boolean) request.getAttribute(WebKeysCrm.CRM_ES_AFILIADO);

	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM_LEGALES);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	DocumentoLegalCRM documentoLegal = null;
	
	if(esView){
		documentoLegal = (DocumentoLegalCRM) request.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_VIEW);
	}else{
		documentoLegal = (DocumentoLegalCRM) request.getSession().getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
	}
	
	if(esAfiliado == null && documentoLegal != null){
		esAfiliado = (documentoLegal.getAfiliado() != null); //si viene un afiliado, esAfiliado true obvio no?
	}else if(esAfiliado == null && documentoLegal == null){
		esAfiliado = true;
	} 
    
	List<MotivoContacto> motivosCrm = (List<MotivoContacto>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_MOTIVOS);
	List<TipoReclamo> tiposReclamoCrm = (List<TipoReclamo>)request.getSession().getAttribute(WebKeysCrm.CRM_LISTA_TIPOS_RECLAMO);

	String email = (String) request.getAttribute(WebKeysCrm.CRM_AFILIADO_EMAIL);

	Calendar desdeFecha = CalendarFactoryUtil.getCalendar();
	desdeFecha.setTime(new Date());
	desdeFecha.add(Calendar.DATE, -30); 
	Calendar hastaFecha = Calendar.getInstance();
	hastaFecha.setTime(new Date()); 
	
	 Calendar fechaNotificacion=null,fechaVto=null,fechaRta=null,fechaAviso=null,fechaContacto=null  ;
	fechaNotificacion = Calendar.getInstance();
	/* Calendar fechaNotificacion = Calendar.getInstance();
	Calendar fechaVto = Calendar.getInstance();
	Calendar fechaRta = Calendar.getInstance();
	Calendar fechaAviso = Calendar.getInstance();
	Calendar fechaContacto = Calendar.getInstance();  */
	
	if(documentoLegal != null && documentoLegal.getFechaNotificacion() !=null){
		fechaNotificacion = CalendarFactoryUtil.getCalendar();
		fechaNotificacion.setTime(documentoLegal.getFechaNotificacion());
	}
	if(documentoLegal != null && documentoLegal.getFechaVencimiento() !=null){
		fechaVto = CalendarFactoryUtil.getCalendar();
		fechaVto.setTime(documentoLegal.getFechaVencimiento());
	}
	if(documentoLegal != null && documentoLegal.getFechaRespuesta()!=null){
		fechaRta = CalendarFactoryUtil.getCalendar();
		fechaRta.setTime(documentoLegal.getFechaRespuesta());
	}
	if(documentoLegal != null && documentoLegal.getFechaAvisoAlEstudio() !=null){
		fechaAviso = CalendarFactoryUtil.getCalendar();
		fechaAviso.setTime(documentoLegal.getFechaAvisoAlEstudio());
	}
	if(documentoLegal != null && documentoLegal.getFechaContactoPSOM() !=null){
		fechaContacto = CalendarFactoryUtil.getCalendar();
		fechaContacto.setTime(documentoLegal.getFechaContactoPSOM());
	}
%>
<form action="" method="post" name="<portlet:namespace />fm_doc_legal_crm">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%=accion%>" />

<liferay-ui:success key="insertReclamoOk"  message="<%=(String)request.getAttribute(\"msgReclamoOk\")  %>"  />
<liferay-ui:success key="updateReclamoOk"  message="<%=(String)request.getAttribute(\"msgReclamoOk\")  %>"  />
<liferay-ui:success key="deleteReclamoOk"  message="<%=(String)request.getAttribute(\"msgReclamoOk\")  %>"  />

<fieldset class="block-labels"><legend><liferay-ui:message key="crm-doc-legal" /></legend>

	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		<tr>
			<td colspan="2"><label><liferay-ui:message key="crm-doc-legal-nro" /></label>&nbsp;&nbsp;
			<input id="<portlet:namespace />id"
				name="<portlet:namespace />id" size="20"
				maxlength="100" type="text"  
				value="<%=documentoLegal != null ? String.valueOf(documentoLegal.getId()) : ""%>" 
				readonly="readonly" />
			</td>
			
		</tr>
		<tr>
			<td colspan="2">
				<div id="<portlet:namespace />crm_afiliado">
					<%if(esAfiliado){ %>
					<liferay-util:include page="/html/portlet/crm/editar_reclamo_afiliado.jsp">
					</liferay-util:include>
					<%}else{%>
					<liferay-util:include page="/html/portlet/crm/editar_reclamo_no_afiliado.jsp">
					</liferay-util:include>
					<%} %>
				</div>
			</td>
		</tr>
		<%if(!esView && esAfiliado){ %>		
		<tr>
			<td colspan="2">	
				<fieldset class="block-labels">
					<legend><liferay-ui:message key="ultimos-documentos-legal" /></legend>
					
					<label><liferay-ui:message key="fecha-desde" />:</label>
					<liferay-ui:input-date
						dayParam="fechaDesdeDia"
						dayValue="<%= desdeFecha.get(Calendar.DATE)%>"
						monthParam="fechaDesdeMes"
						monthValue="<%= desdeFecha.get(Calendar.MONTH) %>"
						yearParam="fechaDesdeAnio"
						yearValue="<%= desdeFecha.get(Calendar.YEAR) %>"
						yearRangeStart="<%= desdeFecha.get(Calendar.YEAR)-10 %>"
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
						yearRangeStart="<%= hastaFecha.get(Calendar.YEAR)-1 %>"
						yearRangeEnd="<%= hastaFecha.get(Calendar.YEAR)  %>"
						firstDayOfWeek="<%= hastaFecha.getFirstDayOfWeek()%>"
						/>	
						<input type="button" value="Buscar" onclick="javascript: <portlet:namespace />buscarReclamosAnterioresAfiliado();">
					
					<div align="center" id="<portlet:namespace />ultimos_reclamos" style="height:120px; overflow: scroll; overflow-x: hidden;">
						<liferay-util:include page="/html/portlet/crm/ultimos_reclamos_search_result.jsp">
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
				<legend><liferay-ui:message key='nuevo-docum-legal' /></legend>
				<%} %>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><label><liferay-ui:message key="crm-doc-legal-tipo" />:</label></td>
						<td><select name="<portlet:namespace />tipo_reclamo"
								id="<portlet:namespace />tipo_reclamo" <% if (esView) { %> disabled="disabled" <%} %>
								onchange="javascript:<portlet:namespace />activaOtrosDatos();" >			
								<option value="0">Seleccione un tipo de reclamo</option>
								<% for(TipoReclamo tr : tiposReclamoCrm) {%>
									<option value="<%=tr.getId() %>"  
									<%= (documentoLegal != null && documentoLegal.getTipo().getId() == tr.getId())?"selected":""%>
									<%-- <%= (documentoLegal == null && tr.getId() == WebKeysCrm.CRM_TIPO_RECLAMO_DEFAULT)?"selected":""%> --%>
									><%=tr.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td><label><liferay-ui:message key="crm-doc-legal-notif" />:</label></td>
						<td><liferay-ui:input-date
								dayParam="notificacionFechaDia"
								dayValue="<%= fechaNotificacion.get(Calendar.DATE)%>"
								monthParam="notificacionFechaMes"
								monthValue="<%= fechaNotificacion.get(Calendar.MONTH) %>"
								yearParam="notificacionFechaAnio"
								yearValue="<%= fechaNotificacion.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaNotificacion.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaNotificacion.get(Calendar.YEAR) + 25 %>"
								firstDayOfWeek="<%= fechaNotificacion.getFirstDayOfWeek()%>"
								disabled="<%= esView %>" /></td>
						<td colspan="2">&nbsp;</td>	
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-doc-legal-motivo" />:</label></td>
						<td colspan="3"><select name="<portlet:namespace />motivo_reclamo"
								id="<portlet:namespace />motivo_reclamo" <% if (esView) { %> disabled="disabled" <%} %>>
								<option value="0">Seleccione un objeto de reclamo</option>			
								<% for(MotivoContacto mc : motivosCrm) {%>
									<option value="<%=mc.getId() %>"  
									<%if (documentoLegal != null && documentoLegal.getMotivo().getId() == mc.getId()) { %> selected="selected" <%}%>><%=mc.getDescripcion() %></option> 
								<%} %>
							</select>
						</td>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="crm-contacto-descripcion" />:</label></td>
						<td colspan="3"><textarea rows="6" cols="100"  
									id="<portlet:namespace />descripcion_reclamo" 
									name="<portlet:namespace />descripcion_reclamo"
									style="resize: none;" 
									<% if (esView) { %> disabled="disabled" <%} %> ><%= documentoLegal!=null?documentoLegal.getDescripcion():""%></textarea>
						</td>
						<td><label><liferay-ui:message key="crm-doc-legal-antec" />:</label></td>
						<td><input type="checkbox" name="<portlet:namespace/>antecedente"  id="<portlet:namespace/>antecedente"
							<% if (esView) { %> disabled="disabled" <%} %> 
							<%if(documentoLegal!=null&& documentoLegal.isTieneAntecedentes()){%> checked="checked" <%} %>></td>			
					</tr>
				</table>	
			</fieldset>
			</td>
		</tr>	
		<tr>
			<td colspan="1" valign="top">
			<fieldset class="block-labels">
				<%if(!esView){ %>		
				<legend><liferay-ui:message key='notifi-docum-legal' /></legend>
				<%} %>
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px; width: 340px; vertical-align: top;">
					<tr>
						<td><label><liferay-ui:message key="crm-doc-legal-vto" />:</label></td>
								<%if(fechaVto != null) {%>
								<td><liferay-ui:input-date
										dayParam="vtoFechaDia"
										dayValue="<%= fechaVto.get(Calendar.DATE)%>"
										dayNullable="<%= true %>"
										monthParam="vtoFechaMes"
										monthValue="<%= fechaVto.get(Calendar.MONTH) %>"
										monthNullable="<%= true %>"
										yearParam="vtoFechaAnio"
										yearValue="<%= fechaVto.get(Calendar.YEAR) %>"
										yearRangeStart="<%= fechaVto.get(Calendar.YEAR) - 25 %>"
										yearRangeEnd="<%= fechaVto.get(Calendar.YEAR) + 25 %>"
										yearNullable="<%= true %>"
										firstDayOfWeek="<%= fechaVto.getFirstDayOfWeek()%>"
										disabled="<%= esView %>" /></td>
								<%}else{ %>
								<td><liferay-ui:input-date
										dayParam="vtoFechaDia"
										dayNullable="<%= true %>"
										monthParam="vtoFechaMes"
										monthNullable="<%= true %>"
										yearParam="vtoFechaAnio"
										yearRangeStart="<%= fechaNotificacion.get(Calendar.YEAR) - 25 %>"
										yearRangeEnd="<%= fechaNotificacion.get(Calendar.YEAR) + 25 %>"
										yearNullable="<%= true %>"
										firstDayOfWeek="<%= fechaNotificacion.getFirstDayOfWeek()%>"
										disabled="<%= esView %>" /></td>
								<%} %>		
					</tr>					
					<tr>					
						<td><label><liferay-ui:message key="crm-doc-legal-resp" />:</label></td>
								<%if(fechaRta != null){ %>
								<td><liferay-ui:input-date
										dayParam="rtaFechaDia"
										dayValue="<%= fechaRta.get(Calendar.DATE)%>"
										dayNullable="<%= true %>"
										monthParam="rtaFechaMes"
										monthValue="<%= fechaRta.get(Calendar.MONTH) %>"
										monthNullable="<%= true %>"
										yearParam="rtaFechaAnio"
										yearValue="<%= fechaRta.get(Calendar.YEAR) %>"
										yearRangeStart="<%= fechaRta.get(Calendar.YEAR) - 25 %>"
										yearRangeEnd="<%= fechaRta.get(Calendar.YEAR) + 25 %>"
										yearNullable="<%= true %>"
										firstDayOfWeek="<%= fechaRta.getFirstDayOfWeek()%>"
										disabled="<%= esView %>" /></td>
								<%}else{ %>
								<td><liferay-ui:input-date
										dayParam="rtaFechaDia"
										dayNullable="<%= true %>"
										monthParam="rtaFechaMes"
										monthNullable="<%= true %>"
										yearParam="rtaFechaAnio"
										yearRangeStart="<%= fechaNotificacion.get(Calendar.YEAR) - 25 %>"
										yearRangeEnd="<%= fechaNotificacion.get(Calendar.YEAR) + 25 %>"
										yearNullable="<%= true %>"
										firstDayOfWeek="<%= fechaNotificacion.getFirstDayOfWeek()%>"
										disabled="<%= esView %>" /></td>
								<%} %>		
					</tr>					
					<tr>					
						<td><label><liferay-ui:message key="crm-doc-legal-aviso" />:</label></td>
								<%if(fechaAviso != null){ %>
								<td><liferay-ui:input-date
										dayParam="avisoFechaDia"
										dayValue="<%= fechaAviso.get(Calendar.DATE)%>"
										dayNullable="<%= true %>"
										monthParam="avisoFechaMes"
										monthValue="<%= fechaAviso.get(Calendar.MONTH) %>"
										monthNullable="<%= true %>"
										yearParam="avisoFechaAnio"
										yearValue="<%= fechaAviso.get(Calendar.YEAR) %>"
										yearRangeStart="<%= fechaAviso.get(Calendar.YEAR) - 25 %>"
										yearRangeEnd="<%= fechaAviso.get(Calendar.YEAR) + 25 %>"
										yearNullable="<%= true %>"
										firstDayOfWeek="<%= fechaAviso.getFirstDayOfWeek()%>"
										disabled="<%= esView %>" /></td>
								<%}else{ %>
									<td><liferay-ui:input-date
										dayParam="avisoFechaDia"
										dayNullable="<%= true %>"
										monthParam="avisoFechaMes"
										monthNullable="<%= true %>"
										yearParam="avisoFechaAnio"
										yearRangeStart="<%= fechaNotificacion.get(Calendar.YEAR) - 25 %>"
										yearRangeEnd="<%= fechaNotificacion.get(Calendar.YEAR) + 25 %>"
										yearNullable="<%= true %>"
										firstDayOfWeek="<%= fechaNotificacion.getFirstDayOfWeek()%>"
										disabled="<%= esView %>" /></td>
								<%} %>		
					</tr>					
					<tr>					
						<td><label><liferay-ui:message key="crm-doc-legal-psom" />:</label></td>
						<%if(fechaContacto != null) {%>
						<td><liferay-ui:input-date
								dayParam="contactoFechaDia"
								dayValue="<%= fechaContacto.get(Calendar.DATE)%>"
								dayNullable="<%= true %>"
								monthParam="contactoFechaMes"
								monthValue="<%= fechaContacto.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"
								yearParam="contactoFechaAnio"
								yearValue="<%= fechaContacto.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaContacto.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaContacto.get(Calendar.YEAR) + 25 %>"
								firstDayOfWeek="<%= fechaContacto.getFirstDayOfWeek()%>"
								yearNullable="<%= true %>"
								disabled="<%= esView %>" /></td>
						<%}else{ %>
							<td><liferay-ui:input-date
								dayParam="contactoFechaDia"
								dayNullable="<%= true %>"
								monthParam="contactoFechaMes"
								monthNullable="<%= true %>"
								yearParam="contactoFechaAnio"
								yearRangeStart="<%= fechaNotificacion.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaNotificacion.get(Calendar.YEAR) + 25 %>"
								firstDayOfWeek="<%= fechaNotificacion.getFirstDayOfWeek()%>"
								yearNullable="<%= true %>"
								disabled="<%= esView %>" /></td>
						<%} %>						
					</tr>
				</table>										
				</fieldset>
				</td>
				<td colspan="1" valign="top">
					<fieldset class="block-labels">
						<%if(!esView){ %>		
						<legend><liferay-ui:message key='otros-docum-legal' /></legend>
						<%} %>
						<div id="<portlet:namespace />otrosDatos">
							<liferay-util:include page="/html/portlet/crm/docum_legal_otros_datos.jsp">
							</liferay-util:include>
						</div>
					</fieldset>
				</td>	
			</tr>				

				<tr>
					<%if(!esView){ %>
					<td>
						<%if(documentoLegal == null){ %>
							<!-- <td> -->
								<input type="button" value="Grabar" onclick="javascript:grabarCrmDocumentoLegal();">
							<!-- </td> -->
						 <%}else{ %>
							<!-- <td> -->
								<input type="button" value="Modificar" onclick="javascript:modificaCrmDocumentoLegal();">
							<!-- </td>  -->
						<%} %>	 
						&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" value="Nuevo" onclick="javascript:nuevoCrmContactoSeleccion();">
					</td>
					<%} %>
						
				</tr>
					
				<%if(documentoLegal != null){ %>
				<tr>
					<td colspan="4"></hr></td>
				</tr>
				<tr>
					<td colspan="4">
						<div align="center" id="<portlet:namespace />crm_auditoria">
							<table style="font-size: 8">
								<tr>
									<td><label><liferay-ui:message key="crm-contacto-alta-sec-usu" />:</label></td>
									<td><%=documentoLegal.getAltaSector()+"/"+documentoLegal.getAltaUsr() %></td>
									<td><label><liferay-ui:message key="crm-contacto-alta-fec" />:</label></td>
									<td><%=sdf.format(documentoLegal.getAltaFecha()) %></td>
									<td><label><liferay-ui:message key="crm-contacto-modi-sec-usu" />:</label></td>
									<td><%=documentoLegal.getModiSector()+"/"+documentoLegal.getModiUsr() %></td>
									<td><label><liferay-ui:message key="crm-contacto-modi-fec" />:</label></td>
									<td><%=sdf.format(documentoLegal.getModiFecha()) %></td>
								</tr>
							</table>   
						</div>
					</td>
				</tr>
				<% } %>						
				</table>
			</fieldset>
			<!-- </td>
		</tr>
	</table>		
</fieldset> -->

</form>

<script type="text/javascript" >
jQuery('#<portlet:namespace />divResultadoActualizarOK').hide();
jQuery('#<portlet:namespace />divRadicacionEimporteReclamado').hide();
jQuery('#<portlet:namespace />divNroTramite').hide();
jQuery('#<portlet:namespace />divRadicacion').hide();

var popupCRM;

function grabarCrmDocumentoLegal(){
	if (<portlet:namespace />validarCampos()) { 
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
					<portlet:param name="struts_action" value="/__portlet/editar_crm_legales_entry" />
					<portlet:param name="cmd" value="save" />
			</portlet:renderURL>';		
	    url=url.replace("__portlet","<%=portlet_name%>");		
				
		document.<portlet:namespace />fm_doc_legal_crm.method = 'post';
		submitForm(document.<portlet:namespace />fm_doc_legal_crm, url);
 	} 	
}

function modificaCrmDocumentoLegal(){
	if (<portlet:namespace />validarCampos()) {
		var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
						<portlet:param name="struts_action" value="/__portlet/editar_crm_legales_entry" />
						<portlet:param name="cmd" value="update" />
				</portlet:renderURL>';
		url=url.replace("__portlet","<%=portlet_name%>");	
				
				
		document.<portlet:namespace />fm_doc_legal_crm.method = 'post';
		submitForm(document.<portlet:namespace />fm_doc_legal_crm, url);
	}	
}

function verCrmDocumentoLegal(idSerial) {
	var params = "&<%= Constants.CMD %>=" + "<%= Constants.VIEW%>";
	params = params + '&id='+idSerial;
	
	popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-doc-legal" />",modal:true, width: 1150, position:['center',10]});
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/__portlet/editar_crm_legales_entry';   		       	
	url = url + params;
	url=url.replace("__portlet","<%=portlet_name%>");	
	
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

	var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/__portlet/editar_crm_legales_entry" />
		<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.ADD %>" />
	</portlet:renderURL>';			
	url=url.replace("__portlet","<%=portlet_name%>");	
			
	url=url+'&cuil_titular='+cuil_titu+'&inte='+integ;
	url=url+'&cambiarAfiliado='+cambiarAfiliado;
	document.<portlet:namespace />fm_doc_legal_crm.method = 'post';
	submitForm(document.<portlet:namespace />fm_doc_legal_crm, url);

}

function <portlet:namespace />validarCampos() {
	
	var esAfi = '<%=esAfiliado %>';
	var cod_motivo=parseInt(jQuery("#<portlet:namespace />motivo_reclamo").val());
	var cod_tipo=parseInt(jQuery("#<portlet:namespace />tipo_reclamo").val());
	var descrip=jQuery('#<portlet:namespace />descripcion_reclamo').val();
	var comentario_cierre=jQuery('#<portlet:namespace />comentarios_reclamo').val();
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
	if (esAfi=='false' && trim(noa_doc_tipo).length == 0){
		alert("Ingrese el tipo de documento del contacto");
		jQuery('#<portlet:namespace/>noafi_documento_tipo').focus();
		return false;
	}
	if (esAfi=='false' && trim(noa_doc_nro).length == 0){
		alert("Ingrese el numero de documento del contacto");
		jQuery('#<portlet:namespace/>noafi_documento_nro').focus();
		return false;
	}
	if (esAfi=='false' && trim(noa_apel).length == 0){
		alert("Ingrese el apellido del contacto");
		jQuery('#<portlet:namespace/>noafi_apellido').focus();
		return false;
	}
	if (esAfi=='false' && trim(noa_nom).length == 0){
		alert("Ingrese el nombre del contacto");
		jQuery('#<portlet:namespace/>noafi_nombre').focus();
		return false;
	}

	if(cod_tipo == 0){
		alert("Ingrese un tipo de contacto");	
		jQuery('#<portlet:namespace />tipo_reclamo').focus();
		return false;
	}
	
	if(cod_motivo == 0){
		alert("Ingrese un motivo de contacto");
		jQuery('#<portlet:namespace />motivo_reclamo').focus();
		return false;
	}

	if (trim(descrip).length == 0){
		alert("Ingrese la descripción del reclamo");
		jQuery('#<portlet:namespace />descripcion_reclamo').focus();
		return false;
	}
	
	return true;
}

function <portlet:namespace />buscarReclamosAnterioresAfiliado(){

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
			    
	var busquedaReclamos = { "fechaDesdeFinal": desde_final, "fechaHastaFinal": hasta_final, "cuil_titular": cuiltitular, "inte": integrante};
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/__portlet/buscar_crm_legales" /></portlet:renderURL>';
	url=url.replace("__portlet","<%=portlet_name%>");	

    jQuery('#<portlet:namespace />ultimos_reclamos').load(url,busquedaReclamos, function() {
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
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/__portlet/actualiza_domicilio&cuil_titular='+cuil_titu+'&inte='+integ+'&cmd=view'+'&email='+encodeURI('<%=email%>');
	url=url.replace("__portlet","<%=portlet_name%>");	
	jQuery(popupDomicilio).load(url);
	
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
	var d_cod_area_laboral = jQuery("#<portlet:namespace />cod_area_tel_laboral").val();
	var d_laboral = jQuery("#<portlet:namespace />tel_laboral").val();
	var d_cod_area_celu = jQuery("#<portlet:namespace />cod_area_celular").val();
	var d_celular = jQuery("#<portlet:namespace />celular").val();
	
	var d_email = jQuery("#<portlet:namespace />email").val();

	var cuiltitular= jQuery('#<portlet:namespace />cuil_titular').val();
	var integrante  = jQuery('#<portlet:namespace />inte').val();
	
	/*validamos los campos obligatorios*/
	if (trim(d_calle).length == 0){
		alert("Ingrese la calle del domicilio");
		jQuery('#<portlet:namespace/>calle').focus();
		return false;
	}
	/* if (trim(d_numero).length == 0){
		alert("Ingrese la altura de la calle del domicilio");
		jQuery('#<portlet:namespace/>numero').focus();
		return false;
	} */
	if (trim(d_cod_area_celu).length == 0 && trim(d_celular).length == 0){
		alert("Ingrese un cod. de area y número de celular");
		jQuery('#<portlet:namespace/>celular').focus();
		return false;
	}
	
	if (trim(d_email).length == 0){
		alert("Ingrese un correo electrónico");
		jQuery('#<portlet:namespace/>email').focus();
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
	
	if ((trim(d_cod_area_laboral) == '' && trim(d_laboral) != '') ||
		(trim(d_cod_area_laboral) != '' && trim(d_laboral) == '')
		){
		alert("El teléfono laboral debe necesariamente tener el código de area y el número");
		jQuery('#<portlet:namespace />tel_laboral').focus();
		return false;
	}	 
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/__portlet/actualiza_domicilio';
	url=url.replace("__portlet","<%=portlet_name%>");	

	

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
					 cod_area_laboral:d_cod_area_laboral,
					 telefono_laboral:d_laboral,
					 cod_area_celular:d_cod_area_celu,
					 celular:d_celular,
					 email:d_email,
					 cmd:'save'}, function() {																																											
			if(popupDomicilio!=null){
				jQuery("#<portlet:namespace />divResultadoActualizarOK").show();
				jQuery("#<portlet:namespace />divBotonActualizar").hide();
				Liferay.Popup.close(popupDomicilio); 
			}	 
		});
} 

function <portlet:namespace />activaOtrosDatos(){
	
   	var tipoReclamo = jQuery('#<portlet:namespace/>tipo_reclamo').val();
    
	/* 3;"DEFENSA CONSUMIDOR" 5;"CARTA DOCUMENTO / NOTA" */
	if(tipoReclamo == 3 || tipoReclamo == 5 ) {
		jQuery('#<portlet:namespace />divRadicacionEimporteReclamado').hide();
		jQuery('#<portlet:namespace />divRadicacion').hide();
		jQuery('#<portlet:namespace />divNroTramite').hide();
	} 
	/* 2;"RES 75/98" 6;"EXPEDIENTE SSS" */
	if(tipoReclamo == 2 || tipoReclamo == 6 ) {
		jQuery('#<portlet:namespace />divRadicacionEimporteReclamado').hide();
		jQuery('#<portlet:namespace />divRadicacion').hide();
		jQuery('#<portlet:namespace />divNroTramite').show();
	} 
	/* 1;"AMPARO" */
	if(tipoReclamo == 1) {
		jQuery('#<portlet:namespace />divRadicacionEimporteReclamado').hide();
		jQuery('#<portlet:namespace />divRadicacion').show();
		jQuery('#<portlet:namespace />divNroTramite').hide();
	}
	/* 4;"DAÑOS Y PERJUICIOS" 7;"OTROS" */
	if(tipoReclamo == 4 || tipoReclamo == 7) {
		jQuery('#<portlet:namespace />divRadicacionEimporteReclamado').show();
		jQuery('#<portlet:namespace />divRadicacion').hide();
		jQuery('#<portlet:namespace />divNroTramite').hide();
	} 
    
} 

<% if(accion.equalsIgnoreCase(Constants.UPDATE) || esView){ %>
	<portlet:namespace />activaOtrosDatos();
<%}%>

</script>	
