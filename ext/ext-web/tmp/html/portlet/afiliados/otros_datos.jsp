<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%@page import="ar.com.ospim.util.DateUtils"%>

<%-- <%@page import="ar.com.ospim.afiliados.action.GuardarOtrosDatosAction"%> --%>

<%
	String accion = (String) session.getAttribute(Constants.CMD);
	String opciones = (String) session.getAttribute("opciones");
	String preCarga = (String) session.getAttribute("pre_carga");
	String idPreAfiliado = (String) session.getAttribute("id_pre_afiliado");
	String ddReinc = (String)request.getAttribute(WebKeysAfiliados.DESDE_REINCORPORAR); 
    String cadenaCuil = TraeListasServiceUtil.getSystemConfig("CUILES_NO_VISIBLES_REMUNERACION");
	Boolean reincorporarRecuperarPlanes = (Boolean) request
			.getSession().getAttribute(
					WebKeysAfiliados.REINCORPORAR_RECUPERAR_PLANES);

	if (reincorporarRecuperarPlanes == null) {
		reincorporarRecuperarPlanes = true;
	}

	Afiliado afiliado = (Afiliado) session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
	AfiPlan afiPlan = afiliado.getAfiPlan();

	Calendar vigenteFecha = null;
	Date vigenteFechaAfil = null;
	vigenteFecha = CalendarFactoryUtil.getCalendar();
	if (null != opciones && opciones.equals("true")) {
		vigenteFecha.setTime(afiliado.getVigen_fecha());
		request.setAttribute("vigenFechaOpciones", vigenteFecha);
	}else if (null != preCarga && preCarga.equals("true")) {
		vigenteFecha.setTime(afiliado.getVigen_fecha());
		request.setAttribute("vigenFechaPreCarga", vigenteFecha);
	}else {
		vigenteFecha.setTime(new Date());
	}

	String cuil_titular = request.getParameter("cuil_titular");
	if (cuil_titular == null) {
		cuil_titular = (String) request.getAttribute("cuil_titular");
	}

	int inte = ParamUtil.getInteger(request, "inte");

	Afiliado afiliado_en_base = null;
	try {
		afiliado_en_base = EditarAfiliadoServiceUtil
				.getAfiliadoEntryInclusoDadoBaja(cuil_titular, inte);
	} catch (Exception e) {

	}

	String fechaEgreso = request.getParameter("fechaEgreso");
	if (fechaEgreso == null || fechaEgreso.equals("")) {
		fechaEgreso = (String) request.getAttribute("fechaEgreso");
	}

	String id_motivo_baja = request.getParameter("id_motivo_baja");
	if (id_motivo_baja == null) {
		id_motivo_baja = (String) request
				.getAttribute("id_motivo_baja");
	}

	boolean isPlusTres = Boolean.getBoolean((String) request
			.getAttribute("isPlusTres"));
	/* boolean baja_cascada = Boolean.getBoolean((String) request.getAttribute("baja_cascada")); */
	Boolean baja_cascada = (Boolean) request.getSession().getAttribute("baja_cascada");

	List<TercerizadoraServicio> tercServList = (List<TercerizadoraServicio>) request.getSession().
													getAttribute(WebKeysAfiliados.TERCERIZADORAS_EN_SESSION);

	List<AfiTercerizadoraServicio> AfiTercServList = (ArrayList<AfiTercerizadoraServicio>) request
			.getSession().getAttribute(
					WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);
	
	/* if (AfiTercServList == null) {
		TercerizadoraServiceUtil
				.buscaTercerizadoras(cuil_titular, inte);
		portletSession.setAttribute(
				WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION,
				AfiTercServList, PortletSession.APPLICATION_SCOPE);
	}
 */
	//obtengo lista de session	
	List<MotivoBaja> motivosBaja = (ArrayList<MotivoBaja>) portletSession
			.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,
					PortletSession.APPLICATION_SCOPE);

%>

<portlet:defineObjects />
<input name="<portlet:namespace /><%=Constants.CMD%>" id="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="<%=accion%>" />

<%if(preCarga != null && (accion!=null&&accion.equalsIgnoreCase(Constants.UPDATE) )){ %>
<liferay-util:include page='/html/portlet/afiliados/diferencias_info_adic_preafi.jsp'>
</liferay-util:include>
<%} %>

<liferay-ui:success key="tercerizadorasOK0"  message="<%=(String)request.getAttribute(\"msgTercerizadoraOk0\")  %>"  />
<liferay-ui:success key="tercerizadorasOK1"  message="<%=(String)request.getAttribute(\"msgTercerizadoraOk1\")  %>"  />

<table>
	<tr><td>
	<fieldset class="block-labels">
	<legend><liferay-ui:message	key="situacion-laboral" /></legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>
			<td colspan="7" valign="top">		
				<liferay-util:include page="/html/portlet/afiliados/busqueda_empleador.jsp" >
				   	<%
				   		if (null != opciones && opciones.trim().equals("true")) {
				   	%>		
						<liferay-util:param name="cuit" value="<%=afiliado.getCuit()%>"/>
						<liferay-util:param name="razon_soc" value="<%=afiliado.getRazonSoc()%>"/>
					<%
						}
					%>
					<%
				   		if (null != preCarga && preCarga.trim().equals("true") 
				   			&& afiliado.getLista_situ_laboral() != null) {
				   	%>		
						<liferay-util:param name="cuit" value="<%=afiliado.getCuitSituLaboral(0) %>"/>
						<liferay-util:param name="razon_soc" value="<%=afiliado.getRazonSocSituLaboral(0) %>"/>
						<liferay-util:param name="sucu" value="<%=afiliado.getSucursalSituLaboral(0)%>"/>
						<liferay-util:param name="categoria" value="<%=String.valueOf(afiliado.getIdCategoriaSituLaboral(0))%>"/>
						<liferay-util:param name="situ_revista" value="<%=String.valueOf(afiliado.getIdRevistaSituLaboral(0))%>"/>
					<%
						/* Parche para que no me duplique el mismo laboral de Precarga */
						afiliado.setLista_situ_laboral(null);
						}
					%>
				</liferay-util:include>
				<input type="hidden" id="<portlet:namespace />cuil_titular" name="<portlet:namespace />cuil_titular" value="<%=cuil_titular%>" />
				<input type="hidden" id="<portlet:namespace />editar" name="<portlet:namespace />editar" value="" /> 
				<input type="hidden" id="<portlet:namespace />inteSituLaboral" name="<portlet:namespace />inteSituLaboral" value="" /> 
				<input type="hidden" id="<portlet:namespace />fecha_ingreso_vieja" name="<portlet:namespace />fecha_ingreso_vieja" value="<%=vigenteFecha.get(Calendar.DATE) + "/"
						+ vigenteFecha.get(Calendar.MONTH) + "/"
						+ vigenteFecha.get(Calendar.YEAR)%>" />
				<input type="hidden" id="<portlet:namespace />idSituLaboral" name="<portlet:namespace />idSituLaboral" value="" />
				<input type="hidden" id="<portlet:namespace />estabaDeBajaSituLaboral" name="<portlet:namespace />estabaDeBajaSituLaboral" value="" />
	
				<input type="hidden" name="baja_fecha_hidden" value="<%=DateUtils.format(afiliado.getBaja_fecha(), DateUtils.SHORT)%>">
				<input type="hidden" name="id_motivo_baja_hidden" value="<%=afiliado.getId_motivo_baja()%>">
	
			</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="ingre-fecha" /></label></td>
			<td><liferay-ui:input-date dayParam="fechaIngresoEmpresaDia"
				dayValue="<%=vigenteFecha.get(Calendar.DATE)%>"
				monthParam="fechaIngresoEmpresaMes"
				monthValue="<%=vigenteFecha.get(Calendar.MONTH)%>"
				yearParam="fechaIngresoEmpresaAnio"
				yearValue="<%=vigenteFecha.get(Calendar.YEAR)%>"
				yearRangeStart="<%=vigenteFecha.get(Calendar.YEAR) - 40%>"
				yearRangeEnd="<%=vigenteFecha.get(Calendar.YEAR) + 1%>"
				firstDayOfWeek="<%=vigenteFecha.getFirstDayOfWeek() - 1%>"
				disabled="<%=false%>" /></td>
			<td><label><liferay-ui:message key="egreso-fecha" /></label></td>
			<td><liferay-ui:input-date monthParam="fechaEgresoEmpresaMes"
				monthNullable="true" dayParam="fechaEgresoEmpresaDia"
				dayNullable="true" yearParam="fechaEgresoEmpresaAnio"
				yearNullable="true"
				yearRangeStart="<%=vigenteFecha.get(Calendar.YEAR) - 40%>"
				yearRangeEnd="<%=vigenteFecha.get(Calendar.YEAR) + 20%>"
				firstDayOfWeek="<%=vigenteFecha.getFirstDayOfWeek() - 1%>"
				disabled="<%=false%>" /></td>
			<td>
				<label><liferay-ui:message key="motivo-baja" />&nbsp;</label>
			</td>
			<td>
				<div id="<portlet:namespace />bajaSituLaborales">
					<select name="<portlet:namespace/>motivo_baja_laboral" id="<portlet:namespace/>motivo_baja_laboral">
						<option value=""><liferay-ui:message key="seleccione-motivo-baja" /></option>
						<%
							for (MotivoBaja motivoBaja : motivosBaja) {
						%>
							<option value="<%=motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>						
						<%
							}
						%>
					</select>
				</div>
			</td>
			<%
				String agregaSituLabo = "javascript:grabarSituacionLaboral();"; 
				String limpiarDatos = "javascript:limpiarCamposSituLaboral();";
				String cargaCuitDesempleo = "javascript:cargaCUITdesempleoAnses();";
				String verAportesAnses = "javascript:verAportes(false);";
			%>
			<td><table style="border-collapse:separate; border-spacing: 5px; background-color: silver;">
				<tr>
					<%--
						<td><liferay-ui:icon image="add" url="<%= agregaSituLabo %>" /></td> 
					    <td><liferay-ui:icon image="undo" message="Limpiar campos" url="<%=limpiarDatos %>"></liferay-ui:icon></td>
					    <td><liferay-ui:icon image="anses" message="Ver Aportes" src="../../../../../html/images/anses.ico" url="<%=verAportesAnses %>" /></td>
					    <td><liferay-ui:icon image="anses" message="Completa con CUIT Desempleo Anses" src="../../../../../html/images/ANSES-18x19.jpg" url="<%=cargaCuitDesempleo %>" /></td>
					--%>
				    <td><a href="<%= agregaSituLabo %>" title="Agregar" onkeyup="javascript:<portlet:namespace />pressTeclaComoClickAgregar(event);">
				    		<img src="<%=themeDisplay.getPathThemeImages() %>/common/add.png" 
				    		border="0" alt="Agregar"></a> </td>
				     <td><a href="<%= limpiarDatos %>" title="Limpiar campos" onkeyup="javascript:<portlet:namespace />pressTeclaComoClickLimpiar(event);">
				    		<img src="<%=themeDisplay.getPathThemeImages() %>/common/undo.png" 
				    		border="0" alt="Limpiar campos"></a> </td>		 
				   	 <td>
				   	    <%if (cadenaCuil.indexOf (cuil_titular) == -1){ %>
				   	     <a href="<%= verAportesAnses %>" title="Ver Aportes" onkeyup="javascript:<portlet:namespace />pressTeclaComoClickVerAport(event);">
				    		<img src="<%="../../../../../html/images/anses.ico"%>" 
				    		border="0" alt="Ver Aportes"></a>
				    	<%}%>	 
				     </td>
				    		
				    			
				     <td><a href="<%= cargaCuitDesempleo %>" title="Completa con CUIT Desempleo Anses" onkeyup="javascript:<portlet:namespace />pressTeclaComoClickCuitDesAns(event);">
				    		<img src="<%="../../../../../html/images/ANSES-18x19.jpg"%>" 
				    		border="0" alt="Completa con CUIT Desempleo Anses"></a> </td>				 
				</tr>
			</table> </td>
		</tr>
	</table>
	<div align="center" id="<portlet:namespace />buscandoSituLaborales">
		<table style="align: center;">
			<tr>
				<td><liferay-ui:message key='buscando' /></td>
				<td align="center">
					<img alt="<liferay-ui:message key='buscando'/>" src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
				</td>
			</tr>
		</table>
	</div>
	<div align="center" id="<portlet:namespace />situLaborales">
	    <liferay-util:include page='/html/portlet/afiliados/situlaboral_search_result.jsp' />
	</div>

	   <%--  <%if(seccionalFijada==0 ||(seccionalFijada>0 && (null== afiliado.getBaja_fecha() || afiliado.getBaja_fecha().compareTo(new Date())>1))){%>
			<table class="lfr-table">
				<tr>
					<td>
						<input type="button" value="<liferay-ui:message key="ver-aportes" />" onClick="<portlet:namespace />verAportes(false);" />
					</td>
				</tr>
			</table>
		<%}%> --%>
	</fieldset>
</td></tr>

<%
		if (ddReinc == null
			|| !ddReinc.equals(WebKeysAfiliados.DESDE_REINCORPORAR)
			|| (ddReinc != null
				&& ddReinc.equals(WebKeysAfiliados.DESDE_REINCORPORAR) && !reincorporarRecuperarPlanes)
			
		) {
%>
<tr><td style="width: 100%">
	<div id="<portlet:namespace />planAportes">
		<liferay-util:include page='/html/portlet/afiliados/afiliado_plan.jsp'/>
	</div>	
</td></tr>
<tr><td>
	<fieldset class="block-labels">
	<legend><liferay-ui:message	key="tercerizadora-servicio" /></legend>
		<table class="lfr-table"  style="width: 100%">
			<%-- <tr>
				<td><label><liferay-ui:message key="tercerizadora-servicio" /></label></td>
				<td colspan="5">
					<select  <%if (inte != 0) {%> <%="disabled='disabled'"%>	<%}%> name="<portlet:namespace/>tercerizadora"	id="<portlet:namespace/>tercerizadora">
						<option value="0">Seleccione una tercerizadora</option>
						<%
							for (TercerizadoraServicio terce : tercServList) {
								/* if(!terce.getId_tercerizadora().trim().equals("CSA")){ */
						%>
							<option value="<%=terce.getId_tercerizadora()%>"  
							<% if (null != preCarga && preCarga.trim().equals("true") 
				   			   && afiliado.getId_tercerizadora() != null
				   			   && afiliado.getId_tercerizadora()
				   			   .equalsIgnoreCase(terce.getId_tercerizadora())) {%> selected="selected" <%} %> ><%=terce.getDescripcion()%></option>
						<%
								/* } */
							}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<td><label><liferay-ui:message key="ingre-fecha" /></label></td>
				<td><liferay-ui:input-date monthParam="fechaIngresoTercMes"
					monthValue="<%=vigenteFecha.get(Calendar.MONTH)%>"
					dayParam="fechaIngresoTercDia"
					dayValue="<%=vigenteFecha.get(Calendar.DATE)%>"
					yearParam="fechaIngresoTercAnio"
					yearValue="<%=vigenteFecha.get(Calendar.YEAR)%>"
					yearRangeStart="<%=vigenteFecha.get(Calendar.YEAR) - 40%>"
					yearRangeEnd="<%=vigenteFecha.get(Calendar.YEAR) + 2%>"
					firstDayOfWeek="<%=vigenteFecha.getFirstDayOfWeek() - 1%>"
					disabled="<%=false%>" /></td>
				<td><label><liferay-ui:message key="egreso-fecha" /></label></td>
				<td><liferay-ui:input-date 
					monthParam="fechaEgresoTercMes"
					monthNullable="true" 
					dayParam="fechaEgresoTercDia" 
					dayNullable="true"
					yearParam="fechaEgresoTercAnio" 
					yearNullable="true"
					yearRangeStart="<%=vigenteFecha.get(Calendar.YEAR) - 10%>"
					yearRangeEnd="<%=vigenteFecha.get(Calendar.YEAR) + 20%>"
					firstDayOfWeek="<%=vigenteFecha.getFirstDayOfWeek() - 1%>"
					disabled="<%=false%>" /></td>
				<td colspan="1">
					<input type="hidden" id="<portlet:namespace />fechaIngresoOriginal"	name="<portlet:namespace />fechaIngresoOriginal" value="" />				
				</td>
				<td colspan="1">
					<input type="button" value="<liferay-ui:message key="limpiar-campos" />" onClick="<portlet:namespace />limpiarCamposTercerizadora();" />
					<input type="button" value="Agregar" onClick="<portlet:namespace />agregarTercerizadora();" /><br/><i>*Recuerde presionar "Agregar" para agregar/editar la tercerizadora</i>
				</td>
			</tr> --%>
			<tr><td colspan="6">
				<a href="javascript:mostrarTercerizadoraHisto();" id="<portlet:namespace />mostrarTercHistoLink" ><liferay-ui:message key="ver-historico" /></a>
				<a href="javascript:ocultarTercerizadoraHisto();" id="<portlet:namespace />ocultarTercHistoLink" ><liferay-ui:message key="ocultar-historico" /></a>
					<div align="left" id="<portlet:namespace />histo_tercerizadoras">
					   <liferay-util:include page='/html/portlet/afiliados/historico_tercerizadoras_search_result.jsp' />
					</div>
				</td>	
			</tr>
			<tr><td colspan="6">
				<div align="center" id="<portlet:namespace />buscandoTercerizadoras">
					<table style="align: center;">
						<tr>
							<td><liferay-ui:message key='buscando' /></td>
							<td align="center">
								<img alt="<liferay-ui:message key='buscando'/>" src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>
				</div>
				</td>
			</tr>
			<tr>
				<td colspan="6">
					<div align="center" id="<portlet:namespace />tercerizadoras">
						<liferay-util:include page='/html/portlet/afiliados/tercerizadoras_search_result.jsp' />
					</div>
				</td>
			</tr>		
		</table>
	</fieldset>
</td></tr>	
<%
	}
%>	
<tr><td>
	<div align="left">
		<br></br> 
		<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />guardarOtrosDatos();" />
		<%
			if (ddReinc == null
					|| !ddReinc.equals(WebKeysAfiliados.DESDE_REINCORPORAR)) {
		%>
			<c:if test="<%=afiliado_en_base != null%>">
				<%if(afiliado.getInte() == 0){ %>
					<input type="button" value="<liferay-ui:message key="cargar-integrante" />" onClick="<portlet:namespace />cargarIntegrante();" />
				<%} %>	
			</c:if>
		<%
			}
		%>
	</div>
</td></tr>
</table>
<script type="text/javascript">

	jQuery('#<portlet:namespace />histo_planes').hide();
	jQuery('#<portlet:namespace />histo_tercerizadoras').hide();
	jQuery('#<portlet:namespace />ocultarPlanHistoLink').hide();
	jQuery('#<portlet:namespace />ocultarTercHistoLink').hide();
	
	jQuery('#<portlet:namespace />buscandoSituLaborales').hide();
	jQuery('#<portlet:namespace />buscandoTercerizadoras').hide();
	jQuery('#<portlet:namespace />validando_reglas_baja_plan').hide(); 
	
	function mostrarPlanHisto(){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/historico_aporte&cuil_titular=<%=cuil_titular%>';
		jQuery('#<portlet:namespace />histo_planes').load(url, function() {
			jQuery('#<portlet:namespace />histo_planes').show();
			jQuery('#<portlet:namespace />ocultarPlanHistoLink').show();
			jQuery('#<portlet:namespace />mostrarPlanHistoLink').hide();	            															
		});
	}
	
	function ocultarPlanHisto(){
		jQuery('#<portlet:namespace />histo_planes').hide();
		jQuery('#<portlet:namespace />mostrarPlanHistoLink').show();
		jQuery('#<portlet:namespace />ocultarPlanHistoLink').hide();
	}
	
	function mostrarTercerizadoraHisto(){
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/historico_tercerizadora&cuil_titular=<%=cuil_titular%>';
		jQuery('#<portlet:namespace />histo_tercerizadoras').load(url, function() {
			jQuery('#<portlet:namespace />histo_tercerizadoras').show();
			jQuery('#<portlet:namespace />ocultarTercHistoLink').show();
			jQuery('#<portlet:namespace />mostrarTercHistoLink').hide();	            															
		});
	}
	
	function ocultarTercerizadoraHisto(){
		jQuery('#<portlet:namespace />histo_tercerizadoras').hide();
		jQuery('#<portlet:namespace />mostrarTercHistoLink').show();
		jQuery('#<portlet:namespace />ocultarTercHistoLink').hide();
	}

	<%
	if (null != preCarga && preCarga.trim().equals("true") 
		&& afiliado.getId_tercerizadora() != null 
		&& afiliado.getAfiPlan().getVigenHasta() !=null) {
		
		Calendar fechFinPres = Calendar.getInstance();
		fechFinPres.setTime(afiliado.getAfiPlan().getVigenHasta());
	%>	
		jQuery('#<portlet:namespace />fechaEgresoTercDia').val(<%=fechFinPres.get(Calendar.DAY_OF_MONTH) %>);
		jQuery('#<portlet:namespace />fechaEgresoTercMes').val(<%=fechFinPres.get(Calendar.MONTH) %>);
		jQuery('#<portlet:namespace />fechaEgresoTercAnio').val(<%=fechFinPres.get(Calendar.YEAR) %>);
	<%}%>
		
	var popupAfill;	
	<%String integrante_aporta = (String) request.getAttribute("integrante_aporta");
			String cuilS = (String) request.getAttribute("cuilS");
			if (integrante_aporta != null) {%>
	   	alert('<%=integrante_aporta%>'+"\r\n"+'<%=cuilS%>');
	 <%}
			String faltante = (String) request.getAttribute("FaltanDatos");
			if (faltante != null) {%>
	   	alert('<%=faltante%>');
	<%}

			String situFaltante = (String) request.getAttribute("FaltanSitus");
			String planFaltante = (String) request.getAttribute("FaltanPlanes");
			String tercFaltante = (String) request
					.getAttribute("FaltanTercerizadoras");
			String tercNoCorrespPlan = (String) request
					.getAttribute("TercNoCorrespPlan");
			if (situFaltante != null || planFaltante != null
					|| tercFaltante != null || tercNoCorrespPlan != null) {%>
		alert('<%=situFaltante == null ? "" : situFaltante%>' + "\r\n" + '<%=planFaltante == null ? "" : planFaltante%>' + "\r\n" 
		+ '<%=tercFaltante == null ? "" : tercFaltante%>'+ '<%=tercNoCorrespPlan == null ? "" : tercNoCorrespPlan%>');		
	<%}%>
	
	<%String exito = (String) request.getAttribute("Exito");%>
	<%if (exito != null && !exito.equals("")
					&& request.getAttribute("FaltaTercerizadora") == null
					&& request.getAttribute("FaltaSituLaboral") == null
					&& request.getAttribute("FaltaAporte") == null
					&& request.getAttribute("TercNoCorrespPlan") == null) {%>
		<%String[] tipo_bono_array = exito.split("\\|");
				String cadenaOspim = tipo_bono_array[0];
				String cadenaUoma = tipo_bono_array[1];
				String cadenaAmtima = tipo_bono_array[2];%>
		alert("Los cambios se guardaron exitosamente! \r\n" <%if (!cadenaOspim.equals("0")) {%> + "id_ospim= " + <%=cadenaOspim%> <%}%> + " \r\n" <%if (!cadenaUoma.equals("0")) {%> + "id_uoma= " + <%=cadenaUoma%> <%}%> + " \r\n" <%if (!cadenaAmtima.equals("0")) {%> + "id_amtima= " + <%=cadenaAmtima%> <%}%>);		
	<%}%>

	<%String exitoBaja = (String) request.getAttribute("ExitoBaja");%>
	<%if (exitoBaja != null) {%>
		alert('<%=exitoBaja%>');		
	<%}%>
	
	<%String no_valido = (String) request.getAttribute("NoValido");%>
	<%if (no_valido != null) {%>
		alert("El Afiliado debe tener un" + "<%=no_valido%>" + " vigente: \r\nPor favor, ingrese un" + "<%=no_valido%>");		
	<%}%>
	<%String invalido = (String) request.getAttribute("Invalido");%>
	<%if (invalido != null) {%>
		alert('<%=invalido%>');		
	<%}%>
	
	<%-- function <portlet:namespace />verHistorico(){
		if (popupAfill!=null) {
			Liferay.Popup.close(popupAfill);
		}
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/historico_aporte&cuil_titular=<%=cuil_titular%>';
		popupAfill = Liferay.Popup({title:"Historico de Planes y Aportes",modal:true,width:800});
		jQuery(popupAfill).load(url);
	} --%>

	function <portlet:namespace />exportarExcel(){
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();		
		if(periodoDesdeMesAnio==null){
			periodoDesdeMesAnio='1_2011';
		}
		var solo_deriva=document.getElementById("<portlet:namespace />solo_derivacion").checked		
		var cuota_amtima=document.getElementById('<portlet:namespace />cuota_amtima').checked;		
		var cuota_usufructo=document.getElementById("<portlet:namespace />cuota_usufructo").checked;		
		var art_46=document.getElementById("<portlet:namespace />art_46").checked;		
		var cuota_social_uoma=document.getElementById("<portlet:namespace />cuota_social_uoma").checked;		
		var aporte_solidario_uoma=document.getElementById("<portlet:namespace />aporte_solidario_uoma").checked;		
		var aporte_afip_ospim=document.getElementById("<portlet:namespace />aporte_afip_ospim").checked;
				
		/*var boleta_blanca_ospim=document.getElementById("<portlet:namespace />boleta_blanca_ospim").checked;
		var boleta_blanca_uoma=document.getElementById("<portlet:namespace />boleta_blanca_uoma").checked;
		var boleta_blanca_amtima=document.getElementById("<portlet:namespace />boleta_blanca_amtima").checked;*/
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_APORTES'
			+'&cuil='+<%=cuil_titular%>
			+'&periodoDesdeMesAnio='+periodoDesdeMesAnio	
			+'&solo_derivacion='+solo_deriva
			+'&cuota_amtima='+cuota_amtima
			+'&cuota_usufructo='+cuota_usufructo
			+'&art_46='+art_46
			+'&cuota_social_uoma='+cuota_social_uoma
			+'&aporte_solidario_uoma='+aporte_solidario_uoma
			+'&aporte_afip_ospim='+aporte_afip_ospim;
			/*+'&boleta_blanca_ospim='+boleta_blanca_ospim
			+'&boleta_blanca_uoma='+boleta_blanca_uoma
			+'&boleta_blanca_amtima='+boleta_blanca_amtima;*/		
	}
	
	/*La duplico paora disparar desde el icono...*/
	function verAportes(cerrarAnterior){
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();

		if(periodoDesdeMesAnio==null){			
			periodoDesdeMesAnio='012011';
		}
				
		if(cerrarAnterior=='true'){
			Liferay.Popup.close(popupAfill);
		}
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/ver_aportes&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuil='+<%=cuil_titular%>;		
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="aportes" />",modal:true,width:1300});
		jQuery(popupAfill).load(url);
		
	}
	/* No borrar */
	function <portlet:namespace />verAportes(cerrarAnterior){
		var periodoDesdeMesAnio=jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();

		if(periodoDesdeMesAnio==null){			
			periodoDesdeMesAnio='012011';
		}
				
		if(cerrarAnterior=='true'){
			Liferay.Popup.close(popupAfill);
		}
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/ver_aportes&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuil='+<%=cuil_titular%>;		
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="aportes" />",modal:true,width:1300});
		jQuery(popupAfill).load(url);
		
	}
			
	function grabarSituacionLaboral(){
		if (!<portlet:namespace />validarDatosSituLaboral()) {
			/* return false; */
		} else {
			jQuery('#<portlet:namespace />buscandoSituLaborales').show();
			var inte=<%=inte%>;
			var cuit_empleador=jQuery('#<portlet:namespace />cuit_empleador').val();
			var diaIngreso=	jQuery('#<portlet:namespace />fechaIngresoEmpresaDia').val();
			var mesIngreso= parseInt(jQuery('#<portlet:namespace />fechaIngresoEmpresaMes').val())+1;
			var anioIngreso=jQuery('#<portlet:namespace />fechaIngresoEmpresaAnio').val();		
			var fechaIngreso=diaIngreso+'/'+mesIngreso+'/'+anioIngreso;	
			var diaIngresoModificar=jQuery('#<portlet:namespace />fechaIngresoEmpresaDia').val();
			var mesIngresoModificar=parseInt(jQuery('#<portlet:namespace />fechaIngresoEmpresaMes').val())+1;
			var anioIngresoModificar=jQuery('#<portlet:namespace />fechaIngresoEmpresaAnio').val();			
			var fecha_ingreso_vieja=jQuery('#<portlet:namespace />fecha_ingreso_vieja').val();	
			var diaEgreso=jQuery('#<portlet:namespace />fechaEgresoEmpresaDia').val();
			var mesEgreso=parseInt(jQuery('#<portlet:namespace />fechaEgresoEmpresaMes').val())+1;			
			var anioEgreso=jQuery('#<portlet:namespace />fechaEgresoEmpresaAnio').val();
			var fechaEgreso=diaEgreso+'/'+mesEgreso+'/'+anioEgreso;
			var diaEgresoFormateado = null;
			var mesEgresoFormateado = null;
			if(diaEgreso < 10) {
				diaEgresoFormateado = '0' + diaEgreso;
			} else {
				diaEgresoFormateado = diaEgreso;
			}
			if (mesEgreso < 10) {
				mesEgresoFormateado = '0' + mesEgreso;
			} else {
				mesEgresoFormateado = mesEgreso;
			}
			var fechaEgresoFormateada = diaEgresoFormateado+'/'+mesEgresoFormateado+'/'+anioEgreso;
			var situ_revista=jQuery("#<portlet:namespace/>situRevista").val();
		    var categoria=jQuery("#<portlet:namespace/>categoria").val();
			var editar=jQuery('#<portlet:namespace />editar').val();
			var motivo_baja=jQuery('#<portlet:namespace/>motivo_baja_laboral').val();
			var escala_salarial=jQuery('#<portlet:namespace/>escala_salarial').val();
			var nombre_empresa = jQuery('#<portlet:namespace />empleador').val();
			nombre_empresa = nombre_empresa.replace("'", " ");	
			var sucursal = jQuery('#<portlet:namespace />sucur').val();
			var nombre_categoria = jQuery('#<portlet:namespace />categoria :selected').text();
			var nombre_situ_revista = jQuery('#<portlet:namespace/>situRevista :selected').text();
			var descrp_motivo_baja = jQuery('#<portlet:namespace/>motivo_baja_laboral :selected').text();
			var idLaboralSitu = jQuery('#<portlet:namespace />idSituLaboral').val();
			var inteSituLaboral = jQuery('#<portlet:namespace />inteSituLaboral').val();
			var baja_cascada=null;			
			if(trim(motivo_baja)!=0 && trim(anioEgreso)!='' && inte==0 && inteSituLaboral==0){
				baja_cascada=confirm("<liferay-ui:message key='desea-propagar-baja-planes-grupo-fliar'/>");
			} else if ((trim(motivo_baja)!=0 && trim(anioEgreso)=='') || (trim(motivo_baja)==0 && trim(anioEgreso)!='')) {
				baja_cascada=null;
				alert("Complete Fecha de Egreso y Motivo de Baja");
				jQuery('#<portlet:namespace />buscandoSituLaborales').hide();
//				return false;
				return;
			}
			/* var id_plan = jQuery('#<portlet:namespace/>plan').val();			 */
			var isPlusTres = 'false';	
			if (motivo_baja!=null && trim(motivo_baja)!=0) {
				if(((motivo_baja == 1 && categoria == 11) 
						|| (motivo_baja == 3 && categoria == 11) 
						|| (motivo_baja == 21 && categoria == 11) 
						|| (motivo_baja == 2 && categoria == 11)
						|| (motivo_baja == 17 && categoria == 2)) /*FIN FDO. DESEMPLEO - BENEF DE SEG DESEMPLEO(LEY24013)*/ 
				/* && (id_plan != 16 && id_plan != 7 && id_plan != 8 && id_plan != 18 && id_plan != 26 && id_plan != 27) */) {
				
					<%Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					Date fechaActualDate = calendar.getTime();
					String fechaActualString = DateUtils.format(fechaActualDate,
					DateUtils.SHORT);
					String fechaActualSub = fechaActualString.substring(0, 10);%>
					var fechaActual = '<%=fechaActualSub%>';
	
					var Anio = fechaEgresoFormateada.substr(6,4);
					var Mes = fechaEgresoFormateada.substr(3,2)*1 -1;
					var Dia = fechaEgresoFormateada.substr(0,2);
				    var fecha_egreso = new Date(Anio,Mes,Dia);
	
				    var Anio1 = fechaActual.substr(6,4);
				    var Mes1 = fechaActual.substr(3,2)*1 -1;
				    var Dia1 = fechaActual.substr(0,2);
				    var fecha_actual = new Date(Anio1,Mes1,Dia1);
				   /*  if(fecha_egreso < fecha_actual) {				    	
				    	jQuery('#<portlet:namespace/>plan').val(id_plan);	    							
				    } */
				    isPlusTres = 'true';
					if(inte==0 && baja_cascada!=false) {
						/* <portlet:namespace />buscarAportes(id_plan, fechaEgreso, motivo_baja, isPlusTres); */
						<portlet:namespace />aplicarReglasBajaPlan(fechaEgresoFormateada, motivo_baja);
						
						<portlet:namespace />aplicarReglasBajaTercerizadora(fechaEgreso, isPlusTres, baja_cascada, motivo_baja);	
					}			
				} else if(inte==0) {
					isPlusTres = 'false';
					if(baja_cascada!=false) {
						
						<portlet:namespace />aplicarReglasBajaPlan(fechaEgresoFormateada, motivo_baja);
						
						/* <portlet:namespace />buscarAportes(id_plan, fechaEgreso, motivo_baja, isPlusTres); */
						<portlet:namespace />aplicarReglasBajaTercerizadora(fechaEgreso, isPlusTres, baja_cascada, motivo_baja);
					}
				}
			}else{ // si no cargan la situacion laboral c/ fecha de baja, es alta solamente 
				   //y puedo validar si es reincorporacion c alta de situ laboral
				var estabaDeBaja = jQuery('#<portlet:namespace />estabaDeBajaSituLaboral').val();

				if(estabaDeBaja==''){
					
					<portlet:namespace />aplicarReglasReincorporaXNuevaSituLabo(fechaIngreso, cuit_empleador);
				}else{
					jQuery('#<portlet:namespace />estabaDeBajaSituLaboral').val('');
				}	
			}
			
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/grabar_situ_laboral&cuit_empleador='+cuit_empleador+
			'&fechaIngreso='+fechaIngreso+'&fechaEgreso='+fechaEgreso+'&cuil_titular='+<%=cuil_titular%>+'&inte='+<%=inte%>+'&editar='+editar+'&situ_revista='+situ_revista+
			'&categoria='+categoria+'&motivo_baja='+motivo_baja+'&baja_cascada='+baja_cascada+'&escala_salarial='+escala_salarial+'&fecha_ingreso_vieja='+fecha_ingreso_vieja+
			'&nombre_empresa='+escape(nombre_empresa)+'&nombre_categoria='+escape(nombre_categoria)+'&nombre_situ_revista='+escape(nombre_situ_revista)+
			'&descrp_motivo_baja='+escape(descrp_motivo_baja)+'&idSituLaboral='+idLaboralSitu+'&sucursal='+sucursal;
			 url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />situLaborales').load(url, function() {
				jQuery('#<portlet:namespace />buscandoSituLaborales').hide();          															
			});	
			limpiarCamposSituLaboral();
		}				
	}

	function <portlet:namespace />aplicarReglasBajaTercerizadora(fechaEgreso, isPlusTres, baja_cascada, motivo_baja){
		jQuery('#<portlet:namespace />buscandoTercerizadoras').show();	
		var idTercerizadora=jQuery('#<portlet:namespace />tercerizadora').val();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/grabar_tercerizadora&id_tercerizadora='+idTercerizadora+
		'&fechaEgreso='+fechaEgreso+'&cuil_titular='+<%=cuil_titular%>+'&inte='+<%=inte%>+'&isPlusTres='+isPlusTres+'&baja_cascada='+baja_cascada+'&idMotivoBaja='+motivo_baja+'&accion=bajaTercerizadoraPorSituLaboral';
		jQuery('#<portlet:namespace />tercerizadoras').load(url, function() {
																					jQuery('#<portlet:namespace />buscandoTercerizadoras').hide();            															
																			   }
															   );	
		/* <portlet:namespace />limpiarCamposTercerizadora(); */			
	}
		
	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	function serializaInputs(inputText) {
		var i=0;
		var text='';
		var regExpAporte = /Aporte/;
		var regExpAporte2 = /aporte/;				
		for(i=0;i<inputText.length;i++){
			if(inputText[i].id.search(regExpAporte)!=-1 || inputText[i].id.search(regExpAporte2)!=-1) {							
				text=text+'&'+inputText[i].id+'='+inputText[i].value;
			} 
		}
		return text;
	}

	function <portlet:namespace />guardarOtrosDatos() {
		
		var idNuevoPlan = jQuery('#<portlet:namespace/>nuevoPlan').val();
		<%-- var esBajaCasc = <%=baja_cascada%>; --%>
		var queAccion = '<%=accion%>';

		if(queAccion == 'add' && idNuevoPlan == '0' ){
			alert('Debe seleccionar un plan nuevo');
			jQuery('#<portlet:namespace/>nuevoPlan').focus();
			return false;
		}
		
		/* if(queAccion == 'update'){ 
			if(!<portlet:namespace />sinModificarPlanActual()){ //Salimos sin hacer nada del plan
				if(<portlet:namespace />validarBajaPlanActual()){ //Editamos algo del plan?
				//Intentamos cambiar plan y no seleccionamos el nuevo plan? Pero, si puede que la baja cascada de baja de plan actual sin crear plan nuevo
						if(idNuevoPlan == '0' && !esBajaCasc ){  
							alert('Debe seleccionar un plan nuevo para realizar el cambio');
							jQuery('#<portlet:namespace/>nuevoPlan').focus();
							return false;
						}
				}else{
					return false;
				}
			} 
		} */
		
		/* jQuery('#<portlet:namespace />buscandoTercerizadoras').show(); */
		/* var flag = 'true'; */
		var url = "<portlet:actionURL ><portlet:param name='struts_action' value='/afiliados/guardar_otros_datos' /></portlet:actionURL>";
		<%if (null != opciones && opciones.trim().equals("true")) {%>
				url=url+'&opciones=true';
		<%}%>
		<%if (null != preCarga && preCarga.trim().equals("true")) {%>
			url=url+'&pre_carga=true&id_pre_afiliado='+<%=idPreAfiliado%>;
		<%}%>
		
		submitForm(document.<portlet:namespace />fm, url);
		
		<%-- if (jQuery('#<portlet:namespace/>tercerizadora').val() != "0"){
			<%if (ddReinc == null
					|| !ddReinc.equals(WebKeysAfiliados.DESDE_REINCORPORAR)
					|| (ddReinc != null
							&& ddReinc.equals(WebKeysAfiliados.DESDE_REINCORPORAR) && !reincorporarRecuperarPlanes)) {%>
			if (confirm("Ha seleccionado una tercerizadora que no fue agregada a la lista. ¿Desea continuar de todos modos?")){
				submitForm(document.<portlet:namespace />fm, url);				
			} else {		
				jQuery('#<portlet:namespace />buscandoTercerizadoras').hide();
				var flag = 'false';
			}
			<%} else {%>
				submitForm(document.<portlet:namespace />fm, url);
			<%}%>
			} else {
				jQuery('#<portlet:namespace/>tercerizadora').attr("disabled",false);
				submitForm(document.<portlet:namespace />fm, url);
			} --%>
		
		
	}
	
	function borraSituacionLaboral(cuit_empleador, sucursal, razonSoc, ingre_fecha) {		
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")) {
			return false;
		} else {	
			jQuery('#<portlet:namespace />fecha_ingreso_vieja').val(ingre_fecha);	
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/grabar_situ_laboral&cuit_empleador='+cuit_empleador+
			'&fechaIngreso='+ingre_fecha+'&cuil_titular='+<%=cuil_titular%>+'&inte='+<%=inte%>+'&borrar=true'+'&fecha_ingreso_vieja='+ingre_fecha+'&sucursal='+sucursal;
			jQuery('#<portlet:namespace />situLaborales').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoSituLaborales').hide();            															
																			   }
															   );
		}	
	}

	function borraTercerizadora(id_tercerizadora, ingre_fecha, baja_fecha){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/grabar_tercerizadora&id_tercerizadora='+id_tercerizadora+
			'&fechaIngreso='+ingre_fecha+'&fechaEgreso='+baja_fecha+'&cuil_titular='+<%=cuil_titular%>+'&inte='+<%=inte%>+'&accion=bajaTercerizadora';
			jQuery('#<portlet:namespace />tercerizadoras').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoTercerizadoras').hide();            															
																			   }
															   );
		}	
	}

	function editaSituacionLaboral(cuit_empleador, sucur, razonSoc, ingre_fecha, baja_fecha, categoria, revista, escala_salarial, idLaboralSitu, motivo_baja, inteSituLaboral){
		//Marco que es edit
		jQuery('#<portlet:namespace />editar').val(true);				
		//deshabilito los campos
		jQuery('#<portlet:namespace />cuit_empleador').attr("disabled",true);
		jQuery('#<portlet:namespace />empleador').attr("disabled",true);
		jQuery('#<portlet:namespace />categoria').attr("disabled",false);
		jQuery('#<portlet:namespace />situRevista').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoEmpresaDia').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoEmpresaMes').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoEmpresaAnio').attr("disabled",false);

		//lleno los campos
		jQuery('#<portlet:namespace />cuit_empleador').val(cuit_empleador);
		jQuery('#<portlet:namespace />empleador').val(razonSoc);
		jQuery('#<portlet:namespace />categoria').val(categoria);
		jQuery('#<portlet:namespace />situRevista').val(revista);
		jQuery('#<portlet:namespace />idSituLaboral').val(idLaboralSitu);
		jQuery("#<portlet:namespace />sucur").val(sucur);		
		jQuery('#<portlet:namespace/>motivo_baja_laboral').val(motivo_baja);
		
		if(escala_salarial!="null"){			
			jQuery('#<portlet:namespace/>escala_salarial').val(escala_salarial);
		}else{			
			jQuery('#<portlet:namespace/>escala_salarial').val("");
		}

		var diaIngreso=ingre_fecha.substring(0,2);
		var mesIngreso=ingre_fecha.substring(3,5);		
		if(mesIngreso.substring(0,1)==0){			
			mesIngreso=mesIngreso.substring(1,2);
		}		
		var anioIngreso=ingre_fecha.substring(6,10);		
		jQuery('#<portlet:namespace />fechaIngresoEmpresaDia').val(parseInt(diaIngreso));
		jQuery('#<portlet:namespace />fechaIngresoEmpresaMes').val(parseInt(mesIngreso)-1);		
		jQuery('#<portlet:namespace />fechaIngresoEmpresaAnio').val(anioIngreso);

		jQuery('#<portlet:namespace />fecha_ingreso_vieja').val(ingre_fecha);		
		
		var diaEgreso=baja_fecha.substring(0,2);		
		var mesEgreso=baja_fecha.substring(3,5);		
		if(mesEgreso.substring(0,1)==0){			
			mesEgreso=mesEgreso.substring(1,2);
		}		
		var anioEgreso=baja_fecha.substring(6,10);		
		jQuery('#<portlet:namespace />cuit_empleador').val(cuit_empleador);
		jQuery('#<portlet:namespace />fechaEgresoEmpresaDia').val(parseInt(diaEgreso));
		jQuery('#<portlet:namespace />fechaEgresoEmpresaMes').val(parseInt(mesEgreso)-1);		
		jQuery('#<portlet:namespace />fechaEgresoEmpresaAnio').val(anioEgreso);
		
		jQuery('#<portlet:namespace />estabaDeBajaSituLaboral').val(baja_fecha);
		
		jQuery('#<portlet:namespace />inteSituLaboral').val(inteSituLaboral);	
	}

	/* function editaTercerizadora(id_tercerizadora, ingre_fecha, baja_fecha, ingre_original){
		//deshabilito los campos
		jQuery('#<portlet:namespace/>tercerizadora').attr("disabled",true);			

		//lleno los campos
		jQuery('#<portlet:namespace/>tercerizadora').val(id_tercerizadora);		
		
		var diaIngreso=ingre_fecha.substring(0,2);		
		var mesIngreso=ingre_fecha.substring(3,5);		
		if(mesIngreso.substring(0,1)==0){			
			mesIngreso=mesIngreso.substring(1,2);
		}		
		var anioIngreso=ingre_fecha.substring(6,10);
		jQuery('#<portlet:namespace />fechaIngresoTercDia').val(parseInt(diaIngreso));
		jQuery('#<portlet:namespace />fechaIngresoTercMes').val(parseInt(mesIngreso)-1);		
		jQuery('#<portlet:namespace />fechaIngresoTercAnio').val(anioIngreso);

		jQuery('#<portlet:namespace />fechaIngresoOriginal').val(ingre_original);
		
		var diaEgreso=baja_fecha.substring(0,2);		
		var mesEgreso=baja_fecha.substring(3,5);		
		if(mesEgreso.substring(0,1)==0){			
			mesEgreso=mesEgreso.substring(1,2);
		}		
		var anioEgreso=baja_fecha.substring(6,10);
		jQuery('#<portlet:namespace />fechaEgresoTercDia').val(parseInt(diaEgreso));
		jQuery('#<portlet:namespace />fechaEgresoTercMes').val(parseInt(mesEgreso)-1);		
		jQuery('#<portlet:namespace />fechaEgresoTercAnio').val(anioEgreso);				
		
	} */
	
	function <portlet:namespace />validarDatosSituLaboral(){
		var cuit_empleador=jQuery('#<portlet:namespace />cuit_empleador').val();
		var suc_empleador=jQuery('#<portlet:namespace />sucur').val();
		var categoria=jQuery("#<portlet:namespace />categoria").val();
		var situRevista=jQuery("#<portlet:namespace />situRevista").val();
		var fechaIngresoDia=jQuery('#<portlet:namespace />fechaIngresoEmpresaDia').val();		 
		var fechaIngresoMes=jQuery('#<portlet:namespace />fechaIngresoEmpresaMes').val();
		var fechaIngresoAnio=jQuery('#<portlet:namespace />fechaIngresoEmpresaAnio').val();

		var fechaEgresoDiaTrim=jQuery('#<portlet:namespace />fechaEgresoEmpresaDia').val().replace(/^\s+/g,'');
		var fechaEgresoMesTrim=jQuery('#<portlet:namespace />fechaEgresoEmpresaMes').val().replace(/^\s+/g,'');
		var fechaEgresoAnioTrim=jQuery('#<portlet:namespace />fechaEgresoEmpresaAnio').val().replace(/^\s+/g,'');
		
		var cuil_titular = jQuery('#<portlet:namespace />cuil_titular').val();
		
		var mensaje="Debe completar los campos de la Situación Laboral";
		var sinError=true;
		if(cuit_empleador.length==0){
			mensaje=mensaje+" *<liferay-ui:message key="cuit_empleador" />";
			sinError=false;
		}
		if(fechaIngresoDia.length==0){
			mensaje=mensaje+" *<liferay-ui:message key="dia" />";
			sinError=false;
		}
		if(fechaIngresoMes.length==0){
			mensaje=mensaje+" *<liferay-ui:message key="mes" />";
			sinError=false;
		}
		if(fechaIngresoAnio.length==0){
			mensaje=mensaje+" *<liferay-ui:message key="anio" />";
			sinError=false;
		}
		
		if(fechaEgresoDiaTrim!="" && fechaEgresoMesTrim !="" && fechaEgresoAnioTrim!=""){
			if(parseInt(fechaIngresoAnio)==parseInt(fechaEgresoAnioTrim)){
				if(parseInt(fechaIngresoMes)>parseInt(fechaEgresoMesTrim)){					
					mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaVto'/>";
					sinError=false;
				}else if(parseInt(fechaIngresoMes)==parseInt(fechaEgresoMesTrim) && parseInt(fechaIngresoDia)>parseInt(fechaEgresoDiaTrim)){					
					mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaEgreso'/>";
					sinError=false;
				}
			}else if(parseInt(fechaIngresoAnio)>parseInt(fechaEgresoAnioTrim)){				
				mensaje="<liferay-ui:message key='fechaIngreso-mayor-fechaEgreso'/>";
				sinError=false;				
			}			
		}
		
		if(!esCUITValida(cuit_empleador)){
			/* jQuery('#<portlet:namespace />cuit').focus(); */
			mensaje="<liferay-ui:message key='valida-cuit'/>";
			sinError=false;	
		}
		
		// valida monotributista
		if(cuit_empleador==cuil_titular && parseInt(categoria) == 11 ){
			mensaje="El monotributista no puede tener categoria: RELACION DE DEPENDENCIA";
			sinError=false;		
		}
		if(cuit_empleador==cuil_titular && parseInt(situRevista) == 6 ){
			mensaje="El monotributista no puede tener situacion revista: RECIBE HABERES REGULARMENTE";
			sinError=false;		
		}

		if(cuit_empleador=='33637617449' && suc_empleador=='000' && (parseInt(situRevista) != 1 || parseInt(categoria) != 2) ){
			mensaje="El CUIT Desempleo Anses no cumple con la categoria y situacion laboral necesarias";
			sinError=false;		
		}
		
		if(!sinError){		
			alert(mensaje);
		}		
		return sinError;
	}
	
	function limpiarCamposSituLaboral(){
		//enable
		jQuery('#<portlet:namespace />cuit_empleador').attr("disabled",false);
		jQuery('#<portlet:namespace />empleador').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoEmpresaDia').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoEmpresaMes').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoEmpresaAnio').attr("disabled",false);
		jQuery('#<portlet:namespace />categoria').attr("disabled",false);
		jQuery('#<portlet:namespace />situRevista').attr("disabled",false);

		//no more update
		jQuery('#<portlet:namespace />editar').val(false);

		//limpio los datos
		jQuery('#<portlet:namespace />cuit_empleador').val("")		
		jQuery('#<portlet:namespace />empleador').val("");
		jQuery('#<portlet:namespace />fechaIngresoEmpresaDia').val("");
		jQuery('#<portlet:namespace />fechaIngresoEmpresaMes').val("");		
		jQuery('#<portlet:namespace />fechaIngresoEmpresaAnio').val("");
		jQuery('#<portlet:namespace />fechaEgresoEmpresaDia').val("");
		jQuery('#<portlet:namespace />fechaEgresoEmpresaMes').val("");		
		jQuery('#<portlet:namespace />fechaEgresoEmpresaAnio').val("");
		jQuery("#<portlet:namespace />categoria option[value=11]").attr("selected",true);
		jQuery("#<portlet:namespace />situRevista option[value=6]").attr("selected",true);
		jQuery("#<portlet:namespace />motivo_baja_laboral option[value='']").attr("selected",true);


		
	}
	
	/* function <portlet:namespace />limpiarCamposTercerizadora(){
		//enable	
		jQuery('#<portlet:namespace/>tercerizadora').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoTercDia').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoTercMes').attr("disabled",false);
		jQuery('#<portlet:namespace />fechaIngresoTercAnio').attr("disabled",false);

		//limpio los datos		
		jQuery('#<portlet:namespace />fechaIngresoTercDia').val("");
		jQuery('#<portlet:namespace />fechaIngresoTercMes').val("");		
		jQuery('#<portlet:namespace />fechaIngresoTercAnio').val("");
		jQuery('#<portlet:namespace />fechaEgresoTercDia').val("");
		jQuery('#<portlet:namespace />fechaEgresoTercMes').val("");		
		jQuery('#<portlet:namespace />fechaEgresoTercAnio').val("");
		jQuery('#<portlet:namespace />fechaIngresoOriginal').val("");
		jQuery('#<portlet:namespace/>tercerizadora').val("0");
	} */

	function <portlet:namespace />cargarIntegrante() {
		var cuil_titular=jQuery('#<portlet:namespace />cuil_titular').val();
		url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/afiliados/cargar_integrante_entry&cuil_titular='+cuil_titular;		
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	<%-- function <portlet:namespace />agregarTercerizadora(){
		jQuery('#<portlet:namespace />buscandoTercerizadoras').show();
		var tercerizadora = jQuery('#<portlet:namespace/>tercerizadora').val();
		var ingreDia = jQuery('#<portlet:namespace />fechaIngresoTercDia').val();
		var ingreMes = jQuery('#<portlet:namespace />fechaIngresoTercMes').val();		
		var ingreAnio = jQuery('#<portlet:namespace />fechaIngresoTercAnio').val();
		var egreDia = jQuery('#<portlet:namespace />fechaEgresoTercDia').val();
		var egreMes = jQuery('#<portlet:namespace />fechaEgresoTercMes').val();		
		var egreAnio = jQuery('#<portlet:namespace />fechaEgresoTercAnio').val();
		var fechaIngresoOriginal = jQuery('#<portlet:namespace />fechaIngresoOriginal').val();

		if(egreDia!="" && egreMes!="" && egreAnio!=""){
			verificaInteUnificaAportes('<%=cuil_titular%>');
		}

		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/grabar_tercerizadora';
		url +='&fechaDesdeDia='+ingreDia;
		url +='&fechaDesdeMes='+ingreMes;
		url +='&fechaDesdeAnio='+ingreAnio;
		url +='&fechaHastaDia='+egreDia;
		url +='&fechaHastaMes='+egreMes;
		url +='&fechaHastaAnio='+egreAnio;
		url +='&id_tercerizadora='+tercerizadora;
		url +='&accion=altaTercerizadora';
		url +='&ingre_original='+fechaIngresoOriginal;
		url += '&rnd=' + Math.floor(Math.random()*100);
		 
		jQuery('#<portlet:namespace />tercerizadoras').load(url, function() {
				jQuery('#<portlet:namespace />buscandoTercerizadoras').hide();
				<portlet:namespace />limpiarCamposTercerizadora();        															
		});	
	} --%>
	
	function verificaInteUnificaAportes(cuil_titular){
		 var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/verificar_unifica_aportes&cuil='+cuil_titular;		 
			jQuery.ajax({   
				url: url,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					if(obj.verificado=="1"){
						alert("<liferay-ui:message key='cuil-titular-unifica'/>");
						document.getElementById(chkbox).checked=false; 
					} 					
				}
			}); 
	}
		
	function <portlet:namespace />filtrarBoletas(){
		var periodoDesdeMesAnio = jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		
		 if(periodoDesdeMesAnio==null){
			periodoDesdeMesAnio='012011';
		} 
		 
		var cuota_amtima=document.getElementById("<portlet:namespace />cuota_amtima").checked;
		var cuota_usufructo=document.getElementById("<portlet:namespace />cuota_usufructo").checked;
		var art_46=document.getElementById("<portlet:namespace />art_46").checked;
		var cuota_social_uoma=document.getElementById("<portlet:namespace />cuota_social_uoma").checked;
		var aporte_solidario_uoma=document.getElementById("<portlet:namespace />aporte_solidario_uoma").checked;
		var aporte_afip_ospim=document.getElementById("<portlet:namespace />aporte_afip_ospim").checked;
		var boleta_blanca_ospim=document.getElementById("<portlet:namespace />boleta_blanca_ospim").checked;
		var boleta_blanca_uoma=document.getElementById("<portlet:namespace />boleta_blanca_uoma").checked;
		var boleta_blanca_amtima=document.getElementById("<portlet:namespace />boleta_blanca_amtima").checked;
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/filtrar_boletas&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuota_amtima='+cuota_amtima+'&cuota_usufructo='+cuota_usufructo+'&art_46='+art_46+'&cuota_social_uoma='+cuota_social_uoma+'&aporte_solidario_uoma='+aporte_solidario_uoma+'&aporte_afip_ospim='+aporte_afip_ospim+'&boleta_blanca_ospim='+boleta_blanca_ospim+'&boleta_blanca_uoma='+boleta_blanca_uoma+'&boleta_blanca_amtima='+boleta_blanca_amtima+'&cuil='+<%=cuil_titular%>;
 		jQuery("#<portlet:namespace />aportes_externos").load(url);   
	}
	
	function <portlet:namespace />setearMeses(cerrarAnterior){
		var periodoDesdeMesAnio = jQuery('#<portlet:namespace />periodoDesdeMesAnio').val();
		var cuota_amtima=document.getElementById("<portlet:namespace />cuota_amtima").checked;
		var cuota_usufructo=document.getElementById("<portlet:namespace />cuota_usufructo").checked;
		var art_46=document.getElementById("<portlet:namespace />art_46").checked;
		var cuota_social_uoma=document.getElementById("<portlet:namespace />cuota_social_uoma").checked;
		var aporte_solidario_uoma=document.getElementById("<portlet:namespace />aporte_solidario_uoma").checked;
		var aporte_afip_ospim=document.getElementById("<portlet:namespace />aporte_afip_ospim").checked;
		var boleta_blanca_ospim=document.getElementById("<portlet:namespace />boleta_blanca_ospim").checked;
		var boleta_blanca_uoma=document.getElementById("<portlet:namespace />boleta_blanca_uoma").checked;
		var boleta_blanca_amtima=document.getElementById("<portlet:namespace />boleta_blanca_amtima").checked;
		
		if(periodoDesdeMesAnio==null){
			periodoDesdeMesAnio='012011';
		}
		
		if(cerrarAnterior=='true') {			
			Liferay.Popup.close(popupAfill);
		}
		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/ver_aportes&periodoDesdeMesAnio='+periodoDesdeMesAnio+'&cuota_amtima='+cuota_amtima+'&cuota_usufructo='+cuota_usufructo+'&art_46='+art_46+'&cuota_social_uoma='+cuota_social_uoma+'&aporte_solidario_uoma='+aporte_solidario_uoma+'&aporte_afip_ospim='+aporte_afip_ospim+'&boleta_blanca_ospim='+boleta_blanca_ospim+'&boleta_blanca_uoma='+boleta_blanca_uoma+'&boleta_blanca_amtima='+boleta_blanca_amtima+'&cuil='+<%=cuil_titular%>;		
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="aportes" />",modal:true,width:1300});
		jQuery(popupAfill).load(url);
	}
	
	/*SVA */
	function <portlet:namespace />aplicarReglasBajaPlan(fechaBajaSituLaboral, motivoBajaSituLaboral){

		var esBajaCascada = false;
		
		if(fechaBajaSituLaboral == "" || fechaBajaSituLaboral.length==0){
			if(!<portlet:namespace />validarBajaPlanActual()){
				return false;
			}
		}else{
			esBajaCascada = true;
		}
		
		var cuil = <%=afiliado.getCuil_titular()%> ;
		var idPlan = <%=afiPlan != null ? afiPlan.getPlan().getId() : 0%>;
		
		var bajaDia = jQuery('#<portlet:namespace />fechaVigenHastaDia').val();
		/* var bajaMes = jQuery('#<portlet:namespace />fechaVigenHastaMes').val();  */ 
		var bajaMes = parseInt(jQuery('#<portlet:namespace />fechaVigenHastaMes').val())+1;  
		var bajaAnio = jQuery('#<portlet:namespace />fechaVigenHastaAnio').val();
		var bajaFecha = bajaDia+'/'+bajaMes+'/'+bajaAnio;	
		var idMotBaja = jQuery('#<portlet:namespace />motivoBajaPlan').val();
		
		if(fechaBajaSituLaboral != "" && fechaBajaSituLaboral.length > 0){ /*seteo la fecha que manda la situacion laboral en cascada */
			bajaFecha = fechaBajaSituLaboral;
			idMotBaja = motivoBajaSituLaboral;
		}
		
		jQuery('#<portlet:namespace />validando_reglas_baja_plan').show();

	/*	var inte=jQuery('#<portlet:namespace />inte').val();*/

		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/baja_plan_reglas';
		url = url+'&cuil='+cuil+'&bajaFecha='+bajaFecha+'&idMotivoBaja='+idMotBaja+'&idPlanActual='+idPlan+'&esBajaCascada='+esBajaCascada;

		
		
		if(esBajaCascada){
			jQuery('#<portlet:namespace />planAportes').load(url, function() {
				jQuery('#<portlet:namespace />validando_reglas_baja_plan').hide();            															
			});
		}else{	
			jQuery('#<portlet:namespace />divNuevoPlan').show();
			
			jQuery('#<portlet:namespace />plan_estado').val('<%=AfiPlan.ESTADOS.MODIFICADO%>');
			
			jQuery('#<portlet:namespace />divNuevoPlan').load(url, function() {
				jQuery('#<portlet:namespace />validando_reglas_baja_plan').hide();            															
			});
		}	
			
	}	

	/*SVA */
	function <portlet:namespace />aplicarReglasReincorporaXNuevaSituLabo(fechaInicioSituLaboral, cuit_situ_laboral){

		var cuil = '<%=afiliado.getCuil_titular()%>' ;
		var afiBajaFecha = '<%= afiliado.getBaja_fechaAsString()!=null?afiliado.getBaja_fechaAsString():"" %>'; //SHORT = "dd/MM/yyyy";
		var desdeReincorporar = '<%=ddReinc != null?"DESDE_REINCORPORAR":"" %>';
		var afiPlanCoberturaVig = '<%=afiPlan!=null?afiPlan.getVigenDesde():"" %>'; // yyyy-MM-dd
	
		if(afiBajaFecha == "" || afiBajaFecha.length == 0 ){ // afi vigente
			//alert('nada que hacer en especial, porque el afiliado esta vigente...');
			return false;
		}
		
		if(desdeReincorporar == "DESDE_REINCORPORAR"){ /* es desde reincorporar
			nada que hacer en especial, porque el afiliado esta vigente o se trata desde reincorporar...*/
			//alert('nada que hacer en especial, es reincorporar...');
			return false;
		}

		if(cuit_situ_laboral == '<%=WebKeysEmpleadores.CUIT_DESEMPLEO_ANSES %>'){
			//alert('nada que hacer en especial, porque es cuit empleo Anses');
			return false;			
		}
		
		jQuery('#<portlet:namespace />validando_reglas_baja_plan').show();

	/*	var inte=jQuery('#<portlet:namespace />inte').val();*/

		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/reinc_x_nueva_situlaboral_reglas';
		url = url+'&cuil='+cuil;
		
		if(fechaInicioSituLaboral != ''){
			url = url +'&fecInicSituLabo='+fechaInicioSituLaboral;
		}
		
		if(afiBajaFecha != ''){
			url = url +'&fechaBajaAfiliado='+afiBajaFecha;
		}
		
		if(afiPlanCoberturaVig != ''){
			url = url +'&afiPlanCoberturaVigDesde='+afiPlanCoberturaVig ;
		}
		
		/* if(esBajaCascada){
			jQuery('#<portlet:namespace />planAportes').load(url, function() {
				jQuery('#<portlet:namespace />validando_reglas_baja_plan').hide();            															
			});
		}else{	 
			jQuery('#<portlet:namespace />divNuevoPlan').show();
			
			jQuery('#<portlet:namespace />divNuevoPlan').load(url, function() {
				jQuery('#<portlet:namespace />validando_reglas_baja_plan').hide();            															
			});
		}	 */
		jQuery('#<portlet:namespace />divNuevoPlan').show();
		
		/* jQuery('#<portlet:namespace />divNuevoPlan').load(url, function() {
			jQuery('#<portlet:namespace />validando_reglas_baja_plan').hide();            															
		}); */
		jQuery('#<portlet:namespace />planAportes').load(url, function() {
			jQuery('#<portlet:namespace />divNuevoPlan').show();
			jQuery('#<portlet:namespace />validando_reglas_baja_plan').hide();            															
		});
	
	}
	function cargaCUITdesempleoAnses(){
		jQuery('#<portlet:namespace />cuit_empleador').val("33637617449");
		jQuery('#<portlet:namespace />sucur').val("000");
		jQuery('#<portlet:namespace />empleador').val("ADMINISTRACION NACIONAL DE LA SEGURIDAD SOCIAL ANS");
		jQuery("#<portlet:namespace />categoria option[value=2]").attr("selected",true);
		jQuery("#<portlet:namespace />situRevista option[value=1]").attr("selected",true);
		
		/* <portlet:namespace />buscarEmpleador(); */


	}
	function <portlet:namespace />pressTeclaComoClickAgregar(event){
		/* backspace y enter*/
		if(event.keyCode == 32 || event.keyCode == 13){
	        <%= agregaSituLabo %>;
	    }
	}
	function <portlet:namespace />pressTeclaComoClickLimpiar(event){
		/* backspace y enter*/
		if(event.keyCode == 32 || event.keyCode == 13){
	        <%= limpiarDatos %>;
	    }
	}
	function <portlet:namespace />pressTeclaComoClickVerAport(event){
		/* backspace y enter*/
		if(event.keyCode == 32 || event.keyCode == 13){
	        <%= verAportesAnses %>;
	    }
	}
	function <portlet:namespace />pressTeclaComoClickCuitDesAns(event){
		/* backspace y enter*/
		if(event.keyCode == 32 || event.keyCode == 13){
	        <%= cargaCuitDesempleo %>;
	    }
	}
	
</script>