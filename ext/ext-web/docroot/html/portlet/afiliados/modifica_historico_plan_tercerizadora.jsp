<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%@page import="ar.com.ospim.util.DateUtils"%>

<%
	List<MotivoBaja> motivosBaja = (ArrayList<MotivoBaja>) portletSession.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION, 
			PortletSession.APPLICATION_SCOPE);


	String accion = (String) session.getAttribute(Constants.CMD);

	Afiliado afiliado = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
	
	int i=0;
%>

<liferay-ui:success key="actualizaOk" message="<%=(String)request.getAttribute(\"msgUpdateOk\") %>"  />
<liferay-ui:error   key="errorHisto1" message="<%=(String)request.getAttribute(\"msgError1\") %>"  />
<liferay-ui:error   key="errorHisto2" message="<%=(String)request.getAttribute(\"msgError2\") %>"  />
<liferay-ui:error   key="errorHisto3" message="<%=(String)request.getAttribute(\"msgError3\") %>"  />
<liferay-ui:error   key="errorHisto4" message="<%=(String)request.getAttribute(\"msgError4\") %>"  />
<liferay-ui:error   key="errorHisto5" message="<%=(String)request.getAttribute(\"msgError5\") %>"  />

<input name="<portlet:namespace /><%=Constants.CMD%>" id="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="<%=accion%>" />

<div style="display: table; vertical-align: top;">
	<div id="<portlet:namespace />divAfiliado" style="display: table-row;">
		<div id="F0_C0" style="display: table-cell;">
			<div style="display: table; vertical-align: top; border-spacing: 2px;">
				<div id="<portlet:namespace />divAfiliadoDet" style="display: table-row;">
					<div id="FA_C1" style="display: table-cell;">	
						<legend><liferay-ui:message	key="grupo-fliar" />:</legend>
					</div>
					<div id="FA_C2" style="display: table-cell;">	
						<%=afiliado.getCuil_titularMasked() %>
					</div>	
					<div id="FA_C3" style="display: table-cell;">	
						<legend><liferay-ui:message	key="apeynom" />:</legend>
					</div>
					<div id="FA_C4" style="display: table-cell;">
						<%=afiliado.getApeNombre() %>
					</div>	
				</div>
				<div id="<portlet:namespace />divAfiliadoDetFechaVig" style="display: table-row;">	
					<div id="FA_C5" style="display: table-cell;">	
						<%Calendar vigencia = Calendar.getInstance() ;
						  vigencia.setTime(afiliado.getVigen_fecha());	
						%>
						<legend><liferay-ui:message	key="vigen-fecha" />:</legend>
					</div>
					<div id="FA_C6" style="display: table-cell;">
						<input type="hidden" name="vigen_fecha_titular" value="<%=afiliado.getVigen_fechaAsString()%>">		
						<liferay-ui:input-date 
										dayParam="fechaVigenDia"
										dayValue="<%= vigencia.get(Calendar.DATE)%>"
										monthParam="fechaVigenMes"
										monthValue="<%= vigencia.get(Calendar.MONTH) %>"
										yearParam="fechaVigenAnio"
										yearValue="<%= vigencia.get(Calendar.YEAR) %>"
										yearRangeStart="<%= vigencia.get(Calendar.YEAR) - 30 %>"
										yearRangeEnd="<%= vigencia.get(Calendar.YEAR)+30%>"
										firstDayOfWeek="<%= vigencia.getFirstDayOfWeek() - 1 %>"
										disabled="false" />
					</div>
				</div>
				<div id="<portlet:namespace />divAfiliadoDetFechaBaja" style="display: table-row;">		
					<%if(afiliado.getBaja_fecha() != null){ %>
						<div id="FA_C7" style="display: table-cell;">	
							<%Calendar bajaFecha = Calendar.getInstance() ;
							  bajaFecha.setTime(afiliado.getBaja_fecha());	
							%>
							<legend><liferay-ui:message	key="baja-fecha" />:</legend>
						</div>
						<div id="FA_C8" style="display: table-cell;">
							<input type="hidden" name="baja_fecha_titular" value="<%=afiliado.getBaja_fechaAsString()%>">
							<input type="hidden" name="motivo_baja_titular" value="<%=afiliado.getId_motivo_baja()%>">			
							<liferay-ui:input-date 
											dayParam="fechaBajaDia"
											dayValue="<%= bajaFecha.get(Calendar.DATE)%>"
											dayNullable="<%= true %>"
											monthParam="fechaBajaMes"
											monthValue="<%= bajaFecha.get(Calendar.MONTH) %>"
											monthNullable="<%= true %>"
											yearParam="fechaBajaAnio"
											yearValue="<%= bajaFecha.get(Calendar.YEAR) %>"
											yearRangeStart="<%= bajaFecha.get(Calendar.YEAR) -30 %>"
											yearRangeEnd="<%= bajaFecha.get(Calendar.YEAR)+30%>"
											yearNullable="<%= true %>"
											firstDayOfWeek="<%= bajaFecha.getFirstDayOfWeek() - 1 %>"
											disabled="false" />
						</div>
						<div id="FA_C9" style="display: table-cell;">	
							<legend><liferay-ui:message	key="motivo-baja" />:</legend>
						</div>
						<div id="FA_C10" style="display: table-cell;">
							<select name="<portlet:namespace/>motivoBajaAfi" id="<portlet:namespace/>motivoBajaAfi" style="width: 200px;" >
								<option value="0" selected="selected"><liferay-ui:message key="seleccione-motivo-baja" /></option>
								<% for (MotivoBaja motivoBaja : motivosBaja) { %>
									<option <%if(afiliado.getId_motivo_baja() == motivoBaja.getId_motivo_baja()){ %> selected="selected" <%} %>
										value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>		
								<%} %>
							</select>
						</div>	
					<%} %>	
			</div>
		</div>	
	</div>
</div>

<liferay-util:include page='/html/portlet/afiliados/modifica_historico_plan.jsp' />

<liferay-util:include page='/html/portlet/afiliados/modifica_historico_tercerizadora.jsp' />

<div style="display: table; vertical-align: bottom;" >
	<div id="<portlet:namespace />div3" style="display: table-row;">
		<div id="F3_C0" style="display: table-cell;">	
				<br></br> 
				<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />validarCambiosHistorico();" />
		</div>
		<div id="F3_C1" style="display: table-cell;">&nbsp;</div>
		<div id="F3_C2" style="display: table-cell;">&nbsp;</div>
		<div id="F3_C3" style="display: table-cell;">&nbsp;</div>
		<div id="F3_C4" style="display: table-cell;">&nbsp;</div>
	</div>
</div>
</div>
<script type="text/javascript">
	
	function <portlet:namespace />validarCambiosHistorico() {
		url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/cambia_historico_cobertura_entry' /></portlet:actionURL>";
		<%-- url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/cambia_historico_cobertura_entry'; --%>
		url = url + "&cmd_histo=guardar";
		submitForm(document.<portlet:namespace />fm, url);
	}
	
</script>