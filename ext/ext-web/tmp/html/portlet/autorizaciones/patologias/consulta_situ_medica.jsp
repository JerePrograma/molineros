<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/patologias/init.jsp"%>

<%
Calendar prestacionFecha = CalendarFactoryUtil.getCalendar();
String prestacionFechaString = prestacionFecha.get(Calendar.DATE)+"/"+(prestacionFecha.get(Calendar.MONTH) + 1)+"/"+prestacionFecha.get(Calendar.YEAR);
String cmd = (String) request.getAttribute(Constants.CMD);

int cantPrestacionesLista=0;
String nroSitMedica = "";
boolean esEdicion = false;
boolean inHabilitar = false;

Calendar fechaDia  =Calendar.getInstance(); 		
fechaDia.setTime(new Date());

Date fechaReg = null;


Calendar fechaVigenDesde = CalendarFactoryUtil.getCalendar();
fechaVigenDesde.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
Calendar fechaVigenHasta = CalendarFactoryUtil.getCalendar();
fechaVigenHasta.setTime(DateUtils.getLastDateOfYear(new Date(), true));

SituacionMedica  situacionMedica  = (SituacionMedica)request.getSession().getAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_POPUP_EN_EDICION);

inHabilitar= true;


if(situacionMedica != null  ){
	nroSitMedica ="Nro Registro : " + "000"+  String.valueOf(situacionMedica.getId_Situacion() );
}else{
	nroSitMedica ="No Tiene Situación Médica";
}
boolean fechaDesdeOk=false;
boolean fechaHastaOk=false;
fechaReg = Validator.isNotNull(situacionMedica)? situacionMedica.getFechaVigen_Desde()    : null;
if (fechaReg != null) {
	fechaVigenDesde.setTime(situacionMedica.getFechaVigen_Desde());
	fechaDesdeOk=true ;
}
fechaReg = Validator.isNotNull(situacionMedica)? situacionMedica.getFechaVigen_Hasta()    : null;
if (fechaReg != null) {
	fechaVigenHasta.setTime(situacionMedica.getFechaVigen_Hasta());
	fechaHastaOk=true;
}

List<TipoDiscapacidad> tiposDisc=(ArrayList<TipoDiscapacidad>) portletSession.getAttribute(WebKeysGlobal.TIPOS_DISCAPACIDAD,PortletSession.APPLICATION_SCOPE);
	if (tiposDisc == null) {
		tiposDisc=TraeListasServiceUtil.getTiposDiscapacidad();
		portletSession.setAttribute(WebKeysGlobal.TIPOS_DISCAPACIDAD,tiposDisc,PortletSession.APPLICATION_SCOPE);	
	}
	
    Calendar vigenteFecha = CalendarFactoryUtil.getCalendar();
	Calendar bajaFecha = CalendarFactoryUtil.getCalendar();
	
	
	
%>

<style>

div.divHeaderNro {
  position: absolute;
  top: 100px;
  right:30;
  left:900px;
  background-color: #cccccc;
  width:200px;
  height:20px;
  border:1px solid black;
  font-size:145%
}

</style>

<form action="EditarPrestadoresEntryAction" name="<portlet:namespace />sitmedica_fm" id="<portlet:namespace />sitmedica_fm" >
		
<fieldset class="block-labels">
	<legend>		
		<liferay-ui:message key="Cabecera Registro de Situación Medica del Afiliado" /> 
	</legend>
	<div class="divHeaderNro">		     
		  <label align='center' ><b> <%=nroSitMedica%> </b>  </label>   
    </div>		

	<legend><liferay-ui:message key='afiliado' /></legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >	
		<tr>
			<td><label><liferay-ui:message key="cuil-titular" /></label></td>
			<td><input type="text"
					   id="<portlet:namespace />cuil_titular"
					   name="<portlet:namespace />cuil_titular" size="13" maxlength="11"
					   value='<%=situacionMedica.getAfiliado().getCuil_titular()%>'
					   readonly='readonly'/>
			</td>
			<td><label><liferay-ui:message key="integrante" />:</label></td>
			<td><input id="<portlet:namespace />integ"
				name="<portlet:namespace />integ" size="2" maxlength="2" type="text"
				value="<%=situacionMedica.getAfiliado().getInteAsString()%>" 
				readonly="readonly" />
			</td>
			<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
			<td>
				<select id="<portlet:namespace/>documento_tipo"
					    name="<portlet:namespace/>documento_tipo" 
					    disabled="disabled">
					<%	for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {	%>
					<option
						
						value="<%= tipoDoc %>"><%=tipoDoc%>
					</option>
					<%	}	%>
				</select>
			</td>
			<td><label><liferay-ui:message key="nro-documento" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />nroDoc"
				name="<portlet:namespace />nroDoc" size="9" maxlength="8"
				value="<%=situacionMedica.getAfiliado().getDocu_numero()%>" 
				readonly="readonly" /></td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td><input id="<portlet:namespace />apellido"
				name="<portlet:namespace />apellido" size="20" maxlength="100"
				type="text"
				value="<%=situacionMedica.getAfiliado().getApellido()%>" 
				readonly="readonly" />
			</td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td><input id="<portlet:namespace />nombre"
				name="<portlet:namespace />nombre" maxlength="100" type="text"
				value="<%=situacionMedica.getAfiliado().getNombre()%>" 
				readonly="readonly" />
			</td>			
			<td colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td colspan="3"><liferay-util:include
					page="/html/portlet/afiliados/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional"
						value="<%=String.valueOf(situacionMedica.getAfiliado().getSeccional().getId())%>" />
					<liferay-util:param name="seccional"
						value="<%=situacionMedica.getAfiliado().getSeccional().getDescripcion()%>" />
					<liferay-util:param name="esEdicion" value="false" />
				</liferay-util:include>
			</td>
			<%-- <td><label><liferay-ui:message key="vigente-desde" />:</label></td>
			<td colspan="3"><liferay-ui:input-date
					dayParam="vigenteFechaDia"
					dayValue="<%= vigenteFecha.get(Calendar.DATE)%>"
					monthParam="vigenteFechaMes"
					monthValue="<%= vigenteFecha.get(Calendar.MONTH) %>"
					yearParam="vigenteFechaAnio"
					yearValue="<%= vigenteFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= vigenteFecha.get(Calendar.YEAR) %>"
					yearRangeEnd="<%= vigenteFecha.get(Calendar.YEAR)  %>"
					firstDayOfWeek="<%= vigenteFecha.getFirstDayOfWeek()%>"
					disabled="<%= true %>" />
			</td> --%>
		</tr>
		<%-- <tr>
			<td><label><liferay-ui:message key="plan" />:</label></td>
			<td colspan="2"><input type="text" id="<portlet:namespace />plan_vig_desc" name="<portlet:namespace />plan_vig_desc"  
			value="" readonly="readonly" style="width: 240px; " /></td>
			<td>&nbsp;</td>
			<%if(situacionMedica.getAfiliado().getBaja_fecha()!=null) {%>
				<td><label><liferay-ui:message key="fecha-baja" />:</label></td>
				<td colspan="3"><liferay-ui:input-date
					dayParam="bajaFechaDia"
					dayValue="<%= bajaFecha.get(Calendar.DATE)%>"
					monthParam="bajaFechaMes"
					monthValue="<%= bajaFecha.get(Calendar.MONTH) %>"
					yearParam="bajaFechaAnio"
					yearValue="<%= bajaFecha.get(Calendar.YEAR) %>"
					yearRangeStart="<%= bajaFecha.get(Calendar.YEAR) %>"
					yearRangeEnd="<%= bajaFecha.get(Calendar.YEAR)  %>"
					firstDayOfWeek="<%= bajaFecha.getFirstDayOfWeek()%>"
					disabled="<%= true %>" />
			<%}else{ %>
				<td colspan="3">&nbsp;</td>
			<%} %>
		</tr> --%>
		<tr>
			<td colspan="4">
				<table class="lfr-table">
					<tr>
						<%if(situacionMedica.getAfiliado().getId_ospim_baja_fecha() == null && situacionMedica.getAfiliado().getId_ospim() > 0){ %>
							<td><label><liferay-ui:message key="id-ospim" />:</label></td>
							<td><%=situacionMedica.getAfiliado().getId_ospim() %></td>
						<%} %>
						<%if(situacionMedica.getAfiliado().getId_amtima_baja_fecha() == null && situacionMedica.getAfiliado().getId_amtima() > 0){ %>
							<td><label><liferay-ui:message key="id-amtima" />:</label></td>
							<td><%=situacionMedica.getAfiliado().getId_amtima() %></td>
						<%} %>
						<%if(situacionMedica.getAfiliado().getId_uoma_baja_fecha() == null && situacionMedica.getAfiliado().getId_uoma() > 0){ %>
							<td><label><liferay-ui:message key="id-uoma" />:</label></td>
							<td><%=situacionMedica.getAfiliado().getId_uoma()%></td>
						<%} %>
					</tr>
				</table>
			</td>			
			
		</tr>	
	</table>
	
		
	<!--  	************************* BLOQUE DE DATOS PARA DISCAPACITADO  *************************	-->


<% if (situacionMedica!=null && situacionMedica.isDiscapacitado() ) {%>
 	<div  id="<portlet:namespace />datosdiscapacitado" >
<%}else{ %>
	<div  id="<portlet:namespace />datosdiscapacitado" style="display:none;">
<%}%>

<fieldset class="block-labels"><legend><liferay-ui:message key="det-discap" /></legend> 		
<table class="lfr-table">
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="observaciones-diagnostico"/>:</label></td>
		<td colspan="9"><textarea rows="2" cols="100" maxlength="250" id="<portlet:namespace />diagnostico" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			name="<portlet:namespace />diagnostico"   ><%=situacionMedica!=null && situacionMedica.getDetalleDiscapacidad().getDiagnostico() !=null ? situacionMedica.getDetalleDiscapacidad().getDiagnostico()  :""%></textarea>
		</td>
		
		<td><label><liferay-ui:message key="tipo-discapacidad"/>:</label></td>
		<td>
		    
		    <select name="<portlet:namespace/>tipo_discapacidad" id="<portlet:namespace/>tipo_discapacidad" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			   multiple="multiple" style="visibility:hidden" >
				<option value=""></option>
				<%for (TipoDiscapacidad td : tiposDisc) {%>				
					<option value="<%=td.getId()%>"> <%=td.getDescripcion()%></option>
				<%}%>
			</select>			 
		</td>
		<td>
			<label align='left' id="<portlet:namespace/>mensaje"></label>
		</td>	
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="dependencia"/>:</label></td>
		<td><select name="<portlet:namespace/>dependencia" id="<portlet:namespace/>dependencia" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			 size="1">
			<option value="false" <%=Validator.isNotNull(situacionMedica) && Validator.isNotNull(situacionMedica.getDetalleDiscapacidad().isDependencia() && !situacionMedica.getDetalleDiscapacidad().isDependencia() )   ? "selected" : ""  %> "">No</option>
			<option value="true" <%=Validator.isNotNull(situacionMedica) && Validator.isNotNull(situacionMedica.getDetalleDiscapacidad().isDependencia() && situacionMedica.getDetalleDiscapacidad().isDependencia() )   ? "selected" : ""  %> "">Si</option>			
			</select>			 
			&nbsp;&nbsp;&nbsp;
			</td>
			<td>
			<label><liferay-ui:message key="telef-contacto"/>:</label>&nbsp;&nbsp;
			<input id="<portlet:namespace />telefono_contacto"
				name="<portlet:namespace />telefono_contacto" size="60" maxlength="60" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
				type="text"
				value=<%=situacionMedica!=null && situacionMedica.getDetalleDiscapacidad().getTelefono_contacto()    !=null ?situacionMedica.getDetalleDiscapacidad().getTelefono_contacto() :""%> 
				>
		</td>
	</tr>
	<tr>
		
	</tr>
</table>
</fieldset>
</div>		   
	
<% if (situacionMedica!=null && situacionMedica.isDiscapacitado() ) {%>
 	<div  id="<portlet:namespace />datosNodiscapacitado" style="display:none;">
<%}else{ %>
<div  id="<portlet:namespace />datosNodiscapacitado">
<%}%>
<br>
<table class="lfr-table">
		<tr>
<td colspan="1"><label><liferay-ui:message key="observaciones-diagnostico"/>:</label></td>
		<td colspan="9"><textarea rows="1" cols="150" id="<portlet:namespace />diagnosticonodiscapacitado" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			name="<portlet:namespace />diagnosticonodiscapacitado"   ><%=situacionMedica!=null && situacionMedica.getdiagnostico() !=null ?situacionMedica.getdiagnostico() :""%></textarea>
		</td>
		</tr>
</table>
<br>		
</div>

<table align="center">		
	<tr>
	<td colspan="10">&nbsp;</td>
	</tr>
		<tr>
		<td><label><liferay-ui:message key="vigen-fecha-desde" />&nbsp;&nbsp;:&nbsp;</label> </td>
	<% if (fechaDesdeOk) {%>
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%=fechaVigenDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%=fechaVigenDesde.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%=fechaVigenDesde.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%}else{ %>
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%} %>
						
		<td><label>&nbsp;&nbsp;&nbsp;&nbsp;<liferay-ui:message key="vigen-fecha-hasta" />&nbsp;&nbsp;:&nbsp;</label> </td>
			<% if (fechaHastaOk) {%>
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%=fechaVigenHasta.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%=fechaVigenHasta.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%=fechaVigenHasta.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)  -1%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /> </td>
			<%}else{ %>
			<td> <liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaDia.get(Calendar.YEAR)%>"
			yearRangeEnd="<%= fechaDia.get(Calendar.YEAR)  +1 %>"
			firstDayOfWeek="<%= fechaDia.getFirstDayOfWeek()  %>"
			disabled="<%= inHabilitar %>" /></td>
			<%} %>
		</tr>	
</table>

	
<fieldset class="block-labels"><legend><liferay-ui:message	key="datos-cie-diez" /></legend>

<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="codigo" />: </label></td>
						<td colspan="2"><input id="<portlet:namespace />codigoCie" name="<portlet:namespace />codigoCie" size="7" maxlength="100" type="text" value='<%=situacionMedica!=null && situacionMedica.getDetalleDiscapacidad().getCie_diez()  !=null ? situacionMedica.getDetalleDiscapacidad().getCie_diez()   :""%>'   readonly='readonly' disabled='disabled'  /></td>
						<td><label><liferay-ui:message key="descripcion" />:</label></td>
						<td colspan="2"><input id="<portlet:namespace />detalleCie" name="<portlet:namespace />detalleCie" size="125" maxlength="100" type="text"  value='<%=situacionMedica!=null && situacionMedica.getDetalleDiscapacidad().getCie_diezDescripcion()  !=null ? situacionMedica.getDetalleDiscapacidad().getCie_diezDescripcion()  :""%>'   readonly='readonly' disabled='disabled'  /></td>
					</tr>
</table>
</fieldset>

	
	
	
	<table class="lfr-table">
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="tipo-situacion-medica" />:</label> </td>
		<td>
			<select name="<portlet:namespace/>situacionMedica"  <% if (esEdicion) { %><%="disabled='disabled'" %><%}%> onchange="javascript:cambiacaption();"  <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%> 				
				id="<portlet:namespace/>situacionMedica">
					<option  value="0">SELECCIONE</option>
					<% for (TiposDeSituacionesMedicas situaciones  : listatipodesituacionesmedicas) { %>
					<option
					    <%= situacionMedica != null  && situacionMedica.getIdTipoSituMedica()   == situaciones.getId() ? "selected" : ""  %>
						value="<%= situaciones.getId() %>"><%=situaciones.getDescripcion()%>
						</option>
					<% } %>								
			</select>
		<td colspan="1"><label align='center' id="<portlet:namespace/>captionsituacionmedicasel">Detalle</label></td>
		<td colspan="9">
			<textarea rows="2" cols="80" id="<portlet:namespace />detalleSituMedica" <% if (inHabilitar) { %><%="disabled='disabled'" %><%}%>
			name="<portlet:namespace />detalleSituMedica"   ><%=situacionMedica!=null && situacionMedica.getDetalleSituMedica() !=null ? situacionMedica.getDetalleSituMedica()  :""%></textarea>
		</td>	
	 </td>
	</tr>
</table>
		
</fieldset>	


</form>

<script type="text/javascript">
	jQuery("#<portlet:namespace />datosdiscapacitado").hide();
	jQuery("#<portlet:namespace />datosNodiscapacitado").hide();
	
	
	<%if(situacionMedica!=null && StringUtils.checkNotEmpty(situacionMedica.getDetalleDiscapacidad().getTiposDiscapacidadDelAfiliado()) ){ %>
	var values='<%=situacionMedica.getDetalleDiscapacidad().getTiposDiscapacidadDelAfiliado()%>';
	var valor;
	jQuery.each(values.split(","), function(i,e){
		jQuery("#<portlet:namespace/>tipo_discapacidad option[value='" + e + "']").attr("selected", true);
	});
	
	 var texto = "";
	 jQuery("#<portlet:namespace/>tipo_discapacidad option:selected").each(function() {	
	 texto += jQuery(this).text() + " - ";
	 });
	 
	jQuery("#<portlet:namespace/>mensaje").html(texto);
	jQuery("#<portlet:namespace/>tipo_discapacidad").hide();
	
	
	
	<%}%>
	
	
</script>