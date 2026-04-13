<%@ include file="/html/portlet/crm/init.jsp" %>
<%
	
	PortletURL portletURL = renderResponse.createRenderURL();
	boolean esView = false;
	String accion = (String) request.getAttribute(Constants.CMD);

	Boolean esAfiliado = (Boolean) request.getAttribute(WebKeysCrm.CRM_ES_AFILIADO);

	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	DocumentoLegalCRM documentoLegal = null;
	
	if(esView){
		documentoLegal = (DocumentoLegalCRM) request.getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_VIEW);
	}else{
		documentoLegal = (DocumentoLegalCRM) request.getSession().getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
	}
%>
	<div style="display: table; vertical-align: top;">
		<div id="<portlet:namespace />divRadicacionEimporteReclamado" style="display: table-row;">
			<div id="F1_C1" style="display: table-cell;">
				<label><liferay-ui:message key="crm-doc-legal-radic" />:</label>
			</div>
			<div id="F1_C2" style="display: table-cell;">
				<input type="text" id="<portlet:namespace />radicacion_reclamo" name="<portlet:namespace />radicacion_reclamo"
							value="<%= documentoLegal!=null&&documentoLegal.getRadicacion()!=null?documentoLegal.getRadicacion():""%>"	
							<% if (esView) { %> disabled="disabled" <%} %> />
			</div>		
			<div id="F1_C3" style="display: table-cell;">	
				<label><liferay-ui:message key="crm-doc-legal-importe" />:</label>
			</div>
			<div id="F1_C4" style="display: table-cell;">	
				<input type="text" id="<portlet:namespace />importe_reclamo" name="<portlet:namespace />importe_reclamo" 
							value="<%= documentoLegal!=null&&documentoLegal.getImporteReclamado()!=null?documentoLegal.getImporteReclamado() :""%>"	
							onkeydown="allowOnlyDigitsAndDecimals(event)"
							<% if (esView) { %> disabled="disabled" <%} %> />
			</div>	
		</div>
		<div id="<portlet:namespace />divRadicacion" style="display: table-row;">
			<div id="F2_C1" style="display: table-cell;">
				<label><liferay-ui:message key="crm-doc-legal-radic" />:</label>
			</div>
			<div id="F2_C2" style="display: table-cell;">		
				<input type="text" id="<portlet:namespace />radicacion_reclamo" name="<portlet:namespace />radicacion_reclamo"
							value="<%= documentoLegal!=null&&documentoLegal.getRadicacion()!=null?documentoLegal.getRadicacion():""%>"	
							<% if (esView) { %> disabled="disabled" <%} %> style="width: 200px;" />
			</div>
			<div id="F2_C3" style="display: table-cell;">&nbsp;</div>
			<div id="F2_C4" style="display: table-cell;">&nbsp;</div>		
		</div>
		<div id="<portlet:namespace />divNroTramite" style="display: table-row;">
			<div id="F3_C1" style="display: table-cell;">	
				<label><liferay-ui:message key="crm-doc-legal-tramite" />:</label>
			</div>
			<div id="F3_C2" style="display: table-cell;">	
				<input type="text" id="<portlet:namespace />tramite_reclamo" name="<portlet:namespace />tramite_reclamo"
								value="<%= documentoLegal!=null&&documentoLegal.getTramiteNumero()!=null?documentoLegal.getTramiteNumero():""%>"	
								<% if (esView) { %> disabled="disabled" <%} %> />
			</div>
			<div id="F3_C3" style="display: table-cell;">&nbsp;</div>
			<div id="F3_C4" style="display: table-cell;">&nbsp;</div>			
		</div>	
		<div id="<portlet:namespace />div1" style="display: table-row;">	
			<div id="F4_C1" style="display: table-cell;">	
				<label><liferay-ui:message key="crm-doc-legal-expediente" />:</label>
			</div>
			<div id="F4_C2" style="display: table-cell;">
				<input type="text" id="<portlet:namespace />expediente_reclamo" name="<portlet:namespace />expediente_reclamo"
								value="<%= documentoLegal!=null&&documentoLegal.getExpediente()!=null?documentoLegal.getExpediente():""%>"	
								<% if (esView) { %> disabled="disabled" <%} %> style="width: 200px;" />
			</div>
			<div id="F4_C3" style="display: table-cell;">
				<label><liferay-ui:message key="crm-doc-legal-resolucion" />:</label>
			</div>
			<div id="F4_C4" style="display: table-cell;">	
				<input type="text" id="<portlet:namespace />resolucion_reclamo" name="<portlet:namespace />resolucion_reclamo"
								value="<%= documentoLegal!=null&&documentoLegal.getResolucion()!=null?documentoLegal.getResolucion() :""%>"	
								<% if (esView) { %> disabled="disabled" <%} %> />
			</div>				
		</div>		
	</div>
	<div style="display: table; vertical-align: top;"> 
		<div id="<portlet:namespace />div2" style="display: table-row;">
			<div id="F5_C1" style="display: table-cell; vertical-align: top;">
				<label><liferay-ui:message key="crm-doc-legal-comentario-cierre" />:</label>
			</div> 
			<div id="F5_C2" style="display: table-cell; width: 300px;"> 	
				<textarea rows="5" cols="100" maxlength="20000"
								id="<portlet:namespace />comentarios_cierre" 
								name="<portlet:namespace />comentarios_cierre"
								style="resize: none;" 
								<% if (esView) { %> disabled="disabled" <%} %> ><%= documentoLegal!=null&&documentoLegal.getDescripcionSolucion()!=null?documentoLegal.getDescripcionSolucion() :""%></textarea>
			</div>
		</div>
		<div id="<portlet:namespace />div3" style="display: table-row;">
			<div id="F6_C1" style="display: table-cell; vertical-align: top;">
				<label><liferay-ui:message key="crm-doc-legal-comentario-estudio" />:</label>
			</div> 
			<div id="F6_C2" style="display: table-cell; width: 300px;"> 	
				<textarea rows="5" cols="100" maxlength="20000"
								id="<portlet:namespace />comentarios_estudio" 
								name="<portlet:namespace />comentarios_estudio"
								style="resize: none;" 
								<% if (esView) { %> disabled="disabled" <%} %> ><%= documentoLegal!=null&&documentoLegal.getDescripcionEstudio()!=null?documentoLegal.getDescripcionEstudio() :""%></textarea>
			</div>
		</div>
		<div id="<portlet:namespace />div4" style="display: table-row;">
			<div id="F7_C1" style="display: table-cell; vertical-align: top;">
				<label><liferay-ui:message key="crm-doc-legal-concluido" />:</label>
			</div> 
			<div id="F7_C2" style="display: table-cell; width: 300px;"> 	
				<input type="checkbox" name="<portlet:namespace/>concluido"  id="<portlet:namespace/>concluido" 
					<% if (esView) { %> disabled="disabled" <%} %>
							<%if(documentoLegal!=null&& documentoLegal.isConcluido()){%> checked="checked" <%} %>>
			</div>
			
		</div>
		
	</div>		