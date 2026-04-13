<%@ include file="/html/portlet/autorizaciones/init.jsp"%>


<%
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	int id_tratamiento = ParamUtil.getInteger(request,
		"id_tratamiento", 0);
    if(id_tratamiento==0){
	  try{	
	    id_tratamiento= (Integer) request.getSession().getAttribute("id_tratamiento");
	  }catch(Exception e){
		id_tratamiento=0;  
	  }
	}

   boolean esDiscapacitado = false;
   try{
	   ParamUtil.getBoolean(request,"esdiscapacitado");
   }catch(Exception e){
   
   }
    
	AutorizacionPrestacional tratamiento = null;
	tratamiento = AutorizacionPrestacionalServiceUtil
			.getAutorizacionPrestacional(id_tratamiento);
	List<Documento> documentosDiscapacidad = TraeListasServiceUtil
			.getDocumentosDiscapacidad();
	List<TercerizadoraServicio> tercerizadoras = TraeListasServiceUtil
			.getTercerizadorasPorConvenios();
	List<Documento> documentosFaltantes = tratamiento != null ? tratamiento
			.getDocumentosFaltantes()
			: new ArrayList<Documento>();
			
    List<MotivoExcepcion>motivosExcepcion = TraeListasServiceUtil.getMotivoExcepcion();			

	String viewStr = ParamUtil.getString(request, "view", "");

	boolean esView = false;
	if (viewStr != null && viewStr.length() > 0) {
		esView = true;
	}

	Date periodoDesde = null;
	Calendar pediodoDC = CalendarFactoryUtil.getCalendar();
	periodoDesde = Validator.isNotNull(tratamiento) ? tratamiento
			.getPeriodo_desde() : null;
	if (periodoDesde == null) {
		pediodoDC.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
	} else {
		pediodoDC.setTime(tratamiento.getPeriodo_desde());
	}

	Date periodoHasta = null;
	Calendar pediodoHC = CalendarFactoryUtil.getCalendar();
	periodoHasta = Validator.isNotNull(tratamiento) ? tratamiento
			.getPeriodo_hasta() : null;
	if (periodoHasta == null) {
		pediodoHC.setTime(DateUtils.getLastDateOfYear(new Date(), true));
	} else {
		pediodoHC.setTime(tratamiento.getPeriodo_hasta());
	}
	Prestador prestador = Validator.isNotNull(tratamiento) ? tratamiento
			.getPrestador()
			: null;	
			
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
			fechaHasta.setTime(new Date());
					
	Calendar fechaIngresoTratamiento = CalendarFactoryUtil.getCalendar();
	if(tratamiento==null || tratamiento.getAlta_fecha() ==null){
			  fechaIngresoTratamiento.setTime(new Date());
	}else{
			  fechaIngresoTratamiento.setTime(tratamiento.getAlta_fecha());
	}  

	
	 String cuilP = (String)session.getAttribute("cuil_titular"); 
	 String inteP = (String)session.getAttribute("inte");
	 
	 if(cuilP==null || "".equals(cuilP)){
		 cuilP=tratamiento.getAfiliado().getCuil_titular();
		 inteP=String.valueOf(tratamiento.getAfiliado().getInte()) ;
	 }
	 String esDiscapacitadoP = (String)session.getAttribute("esDiscapacitado"); 


	 String codLeche = TraeListasServiceUtil.getSystemConfig("CODIGO_LECHE_MATERNIZADA"); 
	 
	 
	 
	 List<String> errores = (List<String>)request.getAttribute("errores");
	 if (errores != null && !errores.isEmpty()){
	 	%>
	 	<table  style="color:red" >
	 	<%
	 	for (String error : errores){
	 		%>
	 		<tr><td>
	 		<%=error%>
	 		</td></tr>
	 		<%
	 	}
	 	%>
	 	</table>
	 	<%
	 }

	
%>
<form action="" method="post" name="<portlet:namespace />fmSSS" id="<portlet:namespace />fmSSS" >


<fieldset class="block-labels"><legend>Tratamiento</legend>

<table class="lfr-table">
   <tr>
     <td ><label>Nro Autorización:</label></td>
     <td>
        <span style="font-size: 13pt; color: blue; "><label ><%=Validator.isNotNull(tratamiento) && tratamiento.getNroAutorizacion()!=null ? tratamiento.getNroAutorizacion()  : 0 %> </label></span>
        
     
     </td>
     <td ><label>Fecha Emisión:</label></td>
     <td>  
		<liferay-ui:input-date
			dayParam="fechaIngresoTratamientoDia"
			dayValue="<%=tratamiento !=null && tratamiento.getAlta_fecha()!=null?fechaIngresoTratamiento.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
			dayNullable="<%= false %>" monthParam="fechaIngresoTratamientoMes"
			monthValue="<%=tratamiento !=null && tratamiento.getAlta_fecha()!=null?fechaIngresoTratamiento.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
			monthNullable="<%= false %>" yearParam="fechaIngresoTratamientoAnio"
			yearValue="<%=tratamiento !=null && tratamiento.getAlta_fecha()!=null?fechaIngresoTratamiento.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
			yearNullable="<%= false %>"
			yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
			yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
			firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
			disabled="<%= true %>"/>
						 
	 </td>
	 
	  <td >&nbsp;</td>
	  
	  <c:if test="<%=Validator.isNotNull(tratamiento)%>"> 
		<td><label><liferay-ui:message key="estado" />:</label></td>
		<td colspan="1"><select name="<portlet:namespace />estado_" id="<portlet:namespace />estado_" disabled='disabled'>
			<option value="0" <%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 0 ? "selected" : ""%>>PreAutorización
	
			</option>
			
			<option value="7" <%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 7 ? "selected" : ""%>>Monotributo
	
			</option>
			
			<option value="1" <%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 1 ? "selected" : ""%>>En
			Curso</option>
			<option value="2" <%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 2 ? "selected" : ""%>>Documentación Faltante</option>			
			<option value="3"
				<%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 3 ? "selected" : ""%>>Cambio
			Prestador</option>
			<option value="4"
				<%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 4 ? "selected" : ""%>>Finalizado</option>
			<option value="5"
				<%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 5 ? "selected" : ""%>>Abandonado</option>
			<option value="6"
				<%=Validator.isNotNull(tratamiento)
						&& Validator.isNotNull(tratamiento.getEstado())
						&& tratamiento.getEstado() == 6 ? "selected" : ""%>>Rechazado</option>			
		</select></td>
		<td>&nbsp;&nbsp;</td>		
		</c:if>
	   
	   <td >&nbsp;</td>
	   
      <% if (Validator.isNotNull(tratamiento) && tratamiento.getIdPreautorizacion() != 0 ){ %>  	 
	  <td>  
	 		<label>Nro Pre Autorización:</label>
	  </td>
	   <td>
        <span style="font-size: 13pt; color: blue; "><label ><%=Validator.isNotNull(tratamiento) && tratamiento.getIdPreautorizacion()!=null ? tratamiento.getIdPreautorizacion()  : 0 %> </label></span>
       </td>
	  
	  <%}%>
   </tr>

   <tr>
		<td colspan="8">&nbsp;</td>
  </tr>
  
  <tr>
  <td colspan="12">
     <table><tr>
     <td colspan="2">
      <label id="<portlet:namespace/>discapacidad_lb">Discapacidad:</label> &nbsp; &nbsp; &nbsp;
      <input type="checkbox"  id="<portlet:namespace/>es_discapacitado"
			name="<portlet:namespace/>es_discapacitado" 
				  <%if (tratamiento != null && tratamiento.isDiscapacitado()){%> checked="checked"<%}%>
	  >
	 </td>
	 
	 <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	 <td colspan="2">
      <label id="<portlet:namespace/>leche_lb">Leche Maternizada:</label> &nbsp; &nbsp; &nbsp;
      <input type="checkbox"  id="<portlet:namespace/>es_leche"
			name="<portlet:namespace/>es_leche" 
				  <%if (tratamiento != null && tratamiento.isLecheMaternizada()){%> checked="checked"<%}%>
	  >
	 </td>
	 <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	 <td colspan="2">
      <label id="<portlet:namespace/>dependencia_lb">Con Dependencia:</label> &nbsp; &nbsp; &nbsp;
      <input type="checkbox"  id="<portlet:namespace/>es_dependencia"
			name="<portlet:namespace/>es_dependencia" 
				  <%if (tratamiento != null && tratamiento.isConDependencia()){%> checked="checked"<%}%>
	  >
	 </td></tr>
	 <tr>
		<td colspan="8">&nbsp;</td>
	</tr>
	<tr>
	  </table>
	  </td>
    </tr>
	
  <tr><td colspan="12">
<fieldset class="block-labels"><legend><liferay-ui:message
			key="datos-afiliado" /></legend> <liferay-util:include
			page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
			<liferay-util:param value="<%=String.valueOf(false)%>"
				name="edit_mode" />
			<liferay-util:param value="<%=String.valueOf(true)%>"
				name="discapacidad" />
			<liferay-util:param name="pag_reintegro" value='1' />	
			<liferay-util:param value="<%=cuilP%>"
				name="cuil" />	
			<liferay-util:param value="<%=inteP%>"
				name="inte" />			
		</liferay-util:include></fieldset>
</td></tr>

<tr>
		<td colspan="8">&nbsp;</td>
  </tr>
</table>


		

<table class="lfr-table">
<tr>
		<td colspan="8"><label hidden="hidden">Periodicidad:</label>
		<select name="<portlet:namespace/>periodicidad" 
			id="<portlet:namespace/>periodicidad">
			
			<option value="Mensual"
				<%=Validator.isNotNull(tratamiento)
					&& Validator.isNotNull(tratamiento.getPeriodicidad())
					&& tratamiento.getPeriodicidad()
							.equalsIgnoreCase("Mensual") ? "selected" : ""%>>Mensual</option>	
							
			<option value="Trimestral"
				<%=Validator.isNotNull(tratamiento)
					&& Validator.isNotNull(tratamiento.getPeriodicidad())
					&& tratamiento.getPeriodicidad()
							.equalsIgnoreCase("Trimestral") ? "selected" : ""%>>Trimestral</option>
							
			<option value="Semestral"
				<%=Validator.isNotNull(tratamiento)
					&& Validator.isNotNull(tratamiento.getPeriodicidad())
					&& tratamiento.getPeriodicidad()
							.equalsIgnoreCase("Semestral") ? "selected" : ""%>>Semestral</option>
							
			<option value="Unica"
				<%=Validator.isNotNull(tratamiento)
					&& Validator.isNotNull(tratamiento.getPeriodicidad())
					&& tratamiento.getPeriodicidad()
							.equalsIgnoreCase("Unica") ? "selected" : ""%>>Unica</option>															
		</select> 		
		<label>Fecha Desde:</label>&nbsp; &nbsp; &nbsp;			
					
		<liferay-ui:input-date dayParam="periodoDesdeDia"
			dayValue="<%=Validator.isNull(tratamiento) || periodoDesde!=null? pediodoDC.get(Calendar.DATE):-1%>"
			dayNullable="<%= true %>" monthParam="periodoDesdeMes"
			monthValue="<%= Validator.isNull(tratamiento) || periodoDesde!=null? pediodoDC.get(Calendar.MONTH):-1%>"
			monthNullable="<%= true %>" yearParam="periodoDesdeAnio"
			yearValue="<%=Validator.isNull(tratamiento) || periodoDesde!=null? pediodoDC.get(Calendar.YEAR):0%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= pediodoDC.get(Calendar.YEAR) - 50 %>"
			yearRangeEnd="<%= pediodoDC.get(Calendar.YEAR) + 2 %>"
			firstDayOfWeek="<%= pediodoDC.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />
								
		<label>Fecha Hasta:</label>&nbsp; &nbsp; &nbsp;

		<liferay-ui:input-date dayParam="periodoHastaDia"
			dayValue="<%= Validator.isNull(tratamiento) || periodoHasta!=null? pediodoHC.get(Calendar.DATE):-1%>"
			dayNullable="<%= true %>" monthParam="periodoHastaMes"
			monthValue="<%=Validator.isNull(tratamiento) || periodoHasta!=null? pediodoHC.get(Calendar.MONTH):-1%>"
			monthNullable="<%= true %>" yearParam="periodoHastaAnio"
			yearValue="<%= Validator.isNull(tratamiento) || periodoHasta!=null?pediodoHC.get(Calendar.YEAR):0 %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= pediodoHC.get(Calendar.YEAR) - 50 %>"
			yearRangeEnd="<%= pediodoHC.get(Calendar.YEAR) + 2 %>"
			firstDayOfWeek="<%= pediodoHC.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" />	
		&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;	
		<label id="<portlet:namespace/>recupera_ape_lb">Recupera por SUR:</label> &nbsp; &nbsp; &nbsp;		
		<input type="checkbox"  id="<portlet:namespace/>recupera_ape"
			name="<portlet:namespace/>recupera_ape" value="true"  <%=tratamiento != null && tratamiento.isRecupera_ape() ? "checked='checked'"
							: ""%>></input>
			<span style="font-size: 13pt; color:red; "><label ><%=tratamiento != null && tratamiento.getExcepcionContratoPrestador()!=null && tratamiento.getExcepcionContratoPrestador() ?"Excepción Convenio":""%></label></span>			
	    </td>						
	</tr>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
    
    
    
 
    
		<td colspan="8">&nbsp;</td>
	</tr>


<td><label>Prestador:</label></td>
	<tr>
	    <td colspan="5">
	     <div id="<portlet:namespace />div_prestador">
		   <liferay-util:include   
			page="/html/portlet/utils/prestadores/busqueda_prestador.jsp">
			<liferay-util:param name="search_url" value="/autorizaciones/buscar_prestador" />
			<liferay-util:param name="cuit_prestador"
			value='<%= Validator.isNotNull(prestador) ? prestador.getCuit() : "" %>' />
			<liferay-util:param name="nombre_prestador"
				value='<%=Validator.isNotNull(prestador) ? prestador.getDescripcion() : "" %>' />
			<liferay-util:param name="id_prestador"
				value='<%=Validator.isNotNull(prestador) && prestador.getId_prestador() != 0 ? String.valueOf(prestador.getId_prestador()) : "" %>' />
			<liferay-util:param name="solo_vigentes"
				value='true' />	
			<liferay-util:param name="esEditable"
				value='<%=String.valueOf( !esView )%>' />
			<liferay-util:param name="ext"
				value='_trat' />	
		   </liferay-util:include>
		   </div>
		</td>
	</tr>
	
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="prestacion" />:</label></td>
		<td colspan="3">
		<div id="<portlet:namespace />div_prestacion">
		<fieldset class="block-labels"><liferay-util:include
			page="/html/portlet/autorizaciones/autorizaciones_prestacionales/busqueda_prestacion.jsp">
			<liferay-util:param name="search_url"
				value="/autorizaciones/buscar_prestacion" />
			<liferay-util:param name="id_prestacion"
				value='<%=tratamiento != null ? String.valueOf(tratamiento.getPrestacion().getId_prestacion()) : "" %>' />
			<liferay-util:param name="codigo"
				value='<%=tratamiento != null ? tratamiento.getPrestacion().getCodigo() : "" %>' />
			<liferay-util:param name="prestacion"
				value='<%=tratamiento != null ? tratamiento.getPrestacion().getDescripcion() : "" %>' />
			<liferay-util:param name="discapacidad"
				value='<%=String.valueOf( true )%>' />
			<liferay-util:param name="esEditable"
				value='<%=String.valueOf( !esView )%>' />
			<liferay-util:param name="suf" value='_trat'/>
		</liferay-util:include></fieldset>
		</div>
		</td>
		
		
		
		<c:if test="<%=Validator.isNotNull(tratamiento)%>"> 
		<td>
		  <label >Cantidad Tickets Copago: </label>
		  <select name="<portlet:namespace/>copago" id="<portlet:namespace/>copago">
		    <%for(int xi=0;xi<=20;xi++){ %>
		     <option value="<%=xi %>"
				<%=Validator.isNotNull(tratamiento)
					&& Validator.isNotNull(tratamiento.getCopago())
					&& tratamiento.getCopago()==xi ? "selected" : ""%>><%=xi%></option>
		   
		    <%} %>
		  </select>	
		</td>
		
		</c:if>
		
		
	</tr>
		
	<tr>
	  <td colspan="8">&nbsp;</td>
	</tr>	
	
	<table class="lfr-table">
		
	<tr>
	  <td colspan="2">
		  <span id="<portlet:namespace />mensajeContrato" style="color: red;display:none;" >No existe convenio para esta Prestación del Prestador Solicitado </span>
	  </td>
	</tr>
	
	<tr>
	  <td colspan="8">&nbsp;</td>
	</tr>
	
	<tr>  
	  <td>
	     <div id='<portlet:namespace />divMotivoExcepcion'
	      <%=tratamiento != null && tratamiento.getExcepcionContratoPrestador()!=null && !tratamiento.getExcepcionContratoPrestador() ? "hidden='hidden'":""%>
	     > 
	       <table>
	        <tr>
	        <td colspan="3">
	        Motivo Excepción
	        </td>
	        <td> 
	        <select name="<portlet:namespace/>motivoExcepcion" id="<portlet:namespace/>motivoExcepcion">
						<%	
						for (MotivoExcepcion m:motivosExcepcion){  %>
						<option value="<%=m.getId() %>"
							<%if (tratamiento != null &&  tratamiento.getMotivoExcepcion()!=null &&
							m.getId().equalsIgnoreCase( tratamiento.getMotivoExcepcion())) { %>
							selected="selected" <%} %>>
							<%=m.getDescripcion() %></option>
						<%}%>
		                </select>
		    </td>            
		    </tr>            
		   </table>              
		  </div>              
	  </td>
	</tr>
	</table>
	
	<table class="lfr-table">	
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
		
		
	<tr>
	<td colspan="8">

	<div align="left" id="<portlet:namespace />divTransporte">
	<table>				
		<tr>
			<td colspan="1"><label>Cantidad Viajes(Mes):</label></td>
			<td colspan="7">
		
		<input id="<portlet:namespace />cantidad_viajes_mensuales"
			name="<portlet:namespace />cantidad_viajes_mensuales" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getCantidad_viajes_mes() : 0%>"
			onchange="sumarTodoTransporte();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />cantidad_viajes_mensuales'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
		
		<label>Cantidad Km.(Día):</label> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
		<input id="<portlet:namespace />cantidad_kilometros_dia"
			name="<portlet:namespace />cantidad_kilometros_dia" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getCantidad_kilometros_dia() : 0%>"
			onchange="sumarTodoTransporte();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />cantidad_kilometros_dia'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;		
		
		<label>Cantidad Km.(Mes):</label> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
		<input id="<portlet:namespace />cantidad_kilometros_mes"
			name="<portlet:namespace />cantidad_kilometros_mes" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getCantidad_kilometros_mes() : 0%>"
			readonly="readonly" /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;

		<label>Importe Km.(Unit):</label> &nbsp;&nbsp;&nbsp; &nbsp;		
		<input id="<portlet:namespace />importe_kilometro_unit"
			name="<portlet:namespace />importe_kilometro_unit" size="12" maxlength="12"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
							.isNotNull(tratamiento.getImporte_kilometro_unit()) ? tratamiento
							.getImporte_kilometro_unit()
							: "0"
							: "0"%>"
			onchange="sumarTodoTransporte();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_kilometro_unit'),event);"
			<%}%> /> 
		
			&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <label>Imp. Total Km.:</label> &nbsp;<input id="<portlet:namespace />importe_total_km"
			name="<portlet:namespace />importe_total_km" size="12" maxlength="20"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
					.isNotNull(tratamiento.getImporte_total_km()) ? tratamiento
					.getImporte_total_km() : "" : ""%>"
			readonly="readonly" />			
			</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>					
		
		<tr>
		<td colspan="1">&nbsp;</td>
		<td colspan="6">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp; <label>Hs. Espera(Día):</label>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;		
		<input id="<portlet:namespace />hs_espera_dia"
			name="<portlet:namespace />hs_espera_dia" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getHs_espera_dia() : 0%>"
			onchange="sumarTodoTransporte();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />hs_espera_dia'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
		
		<label>Hs. espera(Mes):</label> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
		<input id="<portlet:namespace />hs_espera_mes"
			name="<portlet:namespace />hs_espera_mes" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getHs_espera_mes() : 0%>" readonly='readonly' /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;							
		
		<label>Importe Hora(Unit):</label> &nbsp;&nbsp;&nbsp; &nbsp; &nbsp;
		<input id="<portlet:namespace />importe_hs_espera_unit"
			name="<portlet:namespace />importe_hs_espera_unit" size="12" maxlength="12"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
							.isNotNull(tratamiento.getImporte_hs_espera_unit()) ? tratamiento
							.getImporte_hs_espera_unit()
							: "0"
							: "0"%>"
			onchange="sumarTodoTransporte()" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_hs_espera_unit'),event);"
			<%}%> /> 		
			&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;		
		<label><liferay-ui:message
			key="total" /> Hs:</label> &nbsp;<input id="<portlet:namespace />importe_total_hs"
			name="<portlet:namespace />importe_total_hs" size="12" maxlength="20"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
					.isNotNull(tratamiento.getImporte_total_hs()) ? tratamiento
					.getImporte_total_hs() : "" : ""%>"
			readonly="readonly" />
		</td>
		</tr>
		<tr>
			<td colspan="8">&nbsp;</td>
		</tr>
		</table>
	</div>

	</td>
	</tr>
	
	<tr>	    						
		<td colspan="1"><label><liferay-ui:message key="cant" />:</label></td>			
		<td colspan="7">
		
		<input id="<portlet:namespace />cantidad"
			name="<portlet:namespace />cantidad" size="5" maxlength="10"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? tratamiento
					.getCantidad() : 1%>"
			onchange="sumarTodo();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />cantidad'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<label><liferay-ui:message
			key="imp" />:</label> &nbsp;
		<input id="<portlet:namespace />importe"
			name="<portlet:namespace />importe" size="12" maxlength="12"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
					.isNotNull(tratamiento.getImporte_total()) ? tratamiento
					.getImporte_total() : "0" : "0"%>"
			onchange="sumarTodo();" <%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe'),event);"
			<%}%> /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<label><liferay-ui:message
			key="total" />:</label> &nbsp;<input id="<portlet:namespace />total"
			name="<portlet:namespace />total" size="12" maxlength="20"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
					.isNotNull(tratamiento.getImporte_total()) ? tratamiento
					.getImporte_total().multiply(tratamiento.getCantidad())
					: "" : ""%>"
			readonly="readonly" />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<label id="<portlet:namespace />cntTercLb">Cantidad Tercerizada:</label> &nbsp;
			<input id="<portlet:namespace />importe_tercerizado"
			name="<portlet:namespace />importe_tercerizado" size="12" maxlength="12"
			type="text"
			value="<%=Validator.isNotNull(tratamiento) ? Validator
							.isNotNull(tratamiento.getImporte_tercerizado()) ? tratamiento
							.getImporte_tercerizado()
							: "0"
							: "0"%>"
			<%if (esView) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_tercerizado'),event);"
			<%}%> />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		
			</td>
	</tr>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
	
		<tr>
		<td colspan="1"><label>Observaciones:</label></td>
		<td colspan="1"><textarea rows="2" cols="50" id="<portlet:namespace />observaciones"
			name="<portlet:namespace />observaciones" <%if (esView) {%> <%="readonly='readonly'"%> <%}%>><%=tratamiento != null ? tratamiento.getObservaciones()
							: ""%></textarea>
		</td>	
		
		<td colspan="1"><label>Observaciones Internas:</label></td>
		<td colspan="1"><textarea rows="2" cols="50" id="<portlet:namespace />observaciones_int"
			name="<portlet:namespace />observaciones_int" <%if (esView) {%> <%="readonly='readonly'"%> <%}%>><%=tratamiento != null && tratamiento.getObservacionesInternas()!=null ?
					  tratamiento.getObservacionesInternas(): ""%></textarea>
		</td>	
		
		<td colspan="1"><label>Documentación faltante:</label></td>
		<td><select id="<portlet:namespace/>documentacion"
			name="<portlet:namespace/>documentacion" multiple="multiple" size="10">
			<optgroup label="Documentos">
				<%
					for (Documento dd : documentosDiscapacidad) {
				%>
				<option value="<%=dd.getId_documento()%>"
				 <%for (Documento df : documentosFaltantes) {
					if (dd.getId_documento() == df.getId_documento()) {%>
				 	selected="selected"
				 			<%break;
					}
				}%>
				 ><%=dd.getDescripcion()%></option>
				<%
					}
				%>
			</optgroup>
		</select></td>		
	</tr>
	
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
	
	<c:if test="<%=!esView%>">
	
	<tr>				
		<td colspan="8">				
		
		<input type="button"
			value="<liferay-ui:message key="save" />" id="saveTratamientoD"
			onClick="<portlet:namespace />saveTratamientoDiscapacidad(); return false;" />
			
			
		<c:if
			test="<%=Validator.isNotNull(tratamiento) && (tratamiento.getEstado() !=  WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE)
			&& (tratamiento.getEstado() !=  WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_PRE_AUTORIZACION && tratamiento.getEstado() !=6
			    && tratamiento.getEstado() !=  WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_MONOTRIBUTO )%>">
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit"
				value="Autorización PDF"
				onClick="<portlet:namespace />imprimirAT();return false;" />
		</c:if>
			
		<c:if
			test="<%=Validator.isNotNull(tratamiento) && (tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO || tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE)%>">
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit"
				value="Cambio Prestador"
				onClick="<portlet:namespace />cambiarEstadoTratamiento('<%=WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_CAMBIO_PRESTADOR%>>');return false;" />
		</c:if>			
		<c:if
			test="<%=Validator.isNotNull(tratamiento) && (tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO || tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE || tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_ABANDONADO)%>">			
			&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit"
				value="Finalizar Tratamiento"
				onClick="<portlet:namespace />cambiarEstadoTratamiento('<%=WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_FINALIZADO%>>');return false;" />
		</c:if>		
		<c:if test="<%=Validator.isNotNull(tratamiento) && (tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO || tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE || tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_ABANDONADO)%>">			
			&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit"
				value="Abandono de Tratamiento"
				onClick="<portlet:namespace />cambiarEstadoTratamientoConMotivo('<%=WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_ABANDONADO%>>');return false;" />
		</c:if>
		
		<c:if
			test="<%=Validator.isNotNull(tratamiento) && (tratamiento.getEstado() == WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO) %>">
		
		    &nbsp;&nbsp;&nbsp;&nbsp;<input type="button"
			value="Duplicar" id="duplicarTratamientoD"
			onClick="<portlet:namespace />duplicarTratamientoDiscapacidad(); return false;" />
		</c:if>
				
		<input type="hidden"
			value="<%=tratamiento != null ? tratamiento.getId_tratamiento()
						: ""%>"
			name="tratamiento_id" id="tratamiento_id" /> <input type="hidden"
			value="<%=(tratamiento == null ? Constants.ADD
						: Constants.UPDATE)%>"
			name="accionOriginal" id="accionOriginal" />
			
		 <input type="hidden" name="<portlet:namespace />es_excepcion_tratamiento" id="<portlet:namespace />es_excepcion_tratamiento" 
		 value="<%=tratamiento != null && tratamiento.getExcepcionContratoPrestador()? "SI": "NO"%>"/>	
		</td>
	</tr>
	</c:if>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>

	<tr>

	</tr>

	<tr>
		<td colspan="8">
	</tr>
</table>
<div align="center" id="<portlet:namespace />buscandoPrestadorTD"
	name="<portlet:namespace />buscandoPrestadorTD">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />prestadores_resultadoTD">
	<jsp:include
		page='autorizacion_prestacional_result.jsp' />
		
</div>

<input type="hidden" value="<%=cuilP%>" name="<portlet:namespace />cuil_titular" id="<portlet:namespace />cuil_titular" />
<input type="hidden" value="<%=inteP%>" name="<portlet:namespace />inte" id="<portlet:namespace />inte" />
<input type="hidden" value="<%=esDiscapacitadoP%>" name="esDiscapacitado" id="esDiscapacitado" />
<input type="hidden" value="<%=tratamiento!=null  && tratamiento.getIdPreautorizacion() != 0 ? tratamiento.getIdPreautorizacion()  : 0%>" name="idPreautorizacionAux" id="esDiscapacitado" />


<%if(tratamiento != null && tratamiento.getId_tratamiento() !=0){ %>
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
							<td><%=tratamiento.getAlta_usr()!=null?tratamiento.getAlta_usr():"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label><liferay-ui:message key="crm-contacto-alta-fec" />:</label></td>
							<td><%=tratamiento.getAlta_fecha()!=null?sdf.format(tratamiento.getAlta_fecha()):"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label>Modi.Usuario: </label></td>
							<td><%=tratamiento.getModi_usr()!=null?tratamiento.getModi_usr():"" %></td>
							<td>&nbsp;</td><td>&nbsp;</td>
							<td><label><liferay-ui:message key="crm-contacto-modi-fec" />:</label></td>
							<td><%=tratamiento.getModi_fecha()!=null?sdf.format(tratamiento.getModi_fecha()):"" %></td>
						</tr>
				</table>   
		     </div>
         </td>
         </tr>
       </table>
   <%}%>    

</form>

<script type="text/javascript">


	jQuery(document).ready(function() {
		var estado  = jQuery("#<portlet:namespace />estado_").val();

		if (estado == 0 || estado == 7 ){// si viene de pre autorizacion limpiamos los datos basura
			if(estado == 0){ 
			   jQuery("#<portlet:namespace/>es_discapacitado").attr('checked', true);
			}   
			try {
			     jQuery("#<portlet:namespace />cantidad_viajes_mensuales").val('');
			     jQuery("#<portlet:namespace />cantidad_kilometros_dia").val('');
			     jQuery("#<portlet:namespace />cantidad_kilometros_mes").val('');
			     jQuery("#<portlet:namespace />importe_kilometro_unit").val('');
			     jQuery("#<portlet:namespace />importe_total_km").val('');
			     jQuery("#<portlet:namespace />hs_espera_dia").val('');
			     jQuery("#<portlet:namespace />importe_hs_espera_unit").val('');
			     jQuery("#<portlet:namespace />hs_espera_mes").val('');
			     jQuery("#<portlet:namespace />importe_total_hs").val('');

			} catch (err) {}

			
			
			
		}
		
		<portlet:namespace />setearControlesLecheMaternizada();
	});


    var popup;

	jQuery('#<portlet:namespace />buscandoPrestadorTD').hide();
	jQuery('#<portlet:namespace/>recupera_ape').hide();
	jQuery('#<portlet:namespace/>recupera_ape_lb').hide();
	jQuery('#<portlet:namespace />cntTercLb').hide();
	jQuery('#<portlet:namespace />importe_tercerizado').hide();
	try {
		sumarTodo();
	} catch (err) {}

	try {
		sumarTodoTransporte();
	} catch (err) {}

	
	function sumarTodoTransporte(){
		var cantidad_viajes_mensuales = 0;
		var cantidad_kilometros_dia = 0;
		var cantidad_kilometros_mes = 0;
		var importe_kilometro_unit = 0;		
		var importe_total_km = 0;

		var hs_espera_dia = 0;
		var hs_espera_mes = 0;
		var importe_hs_espera_unit = 0;
		var importe_total_hs = 0;

		if (document.getElementById("<portlet:namespace />cantidad_viajes_mensuales")!=null && trim(document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value) != ""){
			cantidad_viajes_mensuales = document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value;
		}
		if (document.getElementById("<portlet:namespace />cantidad_kilometros_dia") != null && trim(document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value)!= ""){
			cantidad_kilometros_dia = document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value;
		}	
		try {
			cantidad_kilometros_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(cantidad_kilometros_dia);
			document.getElementById("<portlet:namespace />cantidad_kilometros_mes").value = Math.round(cantidad_kilometros_mes * 100)/100;
		} catch (err) {}
				
		if (document.getElementById("<portlet:namespace />importe_kilometro_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_kilometro_unit").value) != ""){
			importe_kilometro_unit = document.getElementById("<portlet:namespace />importe_kilometro_unit").value;
		}
		try {
			importe_total_km = parseFloat(cantidad_kilometros_mes) * parseFloat(importe_kilometro_unit);
			document.getElementById("<portlet:namespace />importe_total_km").value = Math.round(importe_total_km * 100)/100;
		} catch (err) {}
		
		
		if (document.getElementById("<portlet:namespace />hs_espera_dia")!=null && trim(document.getElementById("<portlet:namespace />hs_espera_dia").value) != ""){
			hs_espera_dia = document.getElementById("<portlet:namespace />hs_espera_dia").value;
		}
		try {
			hs_espera_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(hs_espera_dia);
			document.getElementById("<portlet:namespace />hs_espera_mes").value = Math.round(hs_espera_mes * 100)/100;
		} catch (err) {}
		
		if (document.getElementById("<portlet:namespace />importe_hs_espera_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_hs_espera_unit").value) != ""){
			importe_hs_espera_unit = document.getElementById("<portlet:namespace />importe_hs_espera_unit").value;
		}
		try {
			importe_total_hs = parseFloat(hs_espera_mes) * parseFloat(importe_hs_espera_unit);
			document.getElementById("<portlet:namespace />importe_total_hs").value = Math.round(importe_total_hs * 100)/100;
		} catch (err) {}

	    var codigoo = jQuery("#<portlet:namespace />codigo_trat").val();
		
	    if (codigoo == '<%=WebKeysAutorizaciones.PRESTACION_TRANSPORTE%>') {
	    	var cant = 1;
			var imp = 0;
			var imp = parseFloat(importe_total_km) + parseFloat(importe_total_hs);  
			try {
				var x = parseFloat(cant) * parseFloat(imp);
				document.getElementById("<portlet:namespace />cantidad").value = cant;
				document.getElementById("<portlet:namespace />importe").value = imp;
				document.getElementById("<portlet:namespace />total").value = Math.round(x * 100)/100;				
			}
			catch (err) {}
	    }
	}

	function sumarTodo(){
		var cant = 0;
		var imp = 0;
		if (document.getElementById("<portlet:namespace />cantidad")!=null && trim(document.getElementById("<portlet:namespace />cantidad").value) != ""){
			cant = 	document.getElementById("<portlet:namespace />cantidad").value;
		}
		if (document.getElementById("<portlet:namespace />importe") != null && trim(document.getElementById("<portlet:namespace />importe").value)!= ""){
			imp = document.getElementById("<portlet:namespace />importe").value;
		}
		try {
			var x = parseFloat(cant) * parseFloat(imp);
			document.getElementById("<portlet:namespace />total").value = Math.round(x * 100)/100;
		}
		catch (err) {}
	}
	
	function <portlet:namespace />saveTratamientoDiscapacidad() {
		
		jQuery('#<portlet:namespace/>motivoExcepcion').attr('disabled', false);
		jQuery('#<portlet:namespace />div_prestacion :input').attr('disabled', false);
		
		var cuil, inte = ""; 
		var esDiscapacitado = false;
		var id_tratamiento =   document.getElementById("tratamiento_id").value;		
		var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();		
		
	    cuil = jQuery("#<portlet:namespace/>cuil_titular").val();	
		inte = jQuery("#<portlet:namespace/>inte").val();	
		
		
		var cantidad=jQuery('#<portlet:namespace />cantidad').val();
		var importe_total = jQuery("#<portlet:namespace />importe").val();
		var periodicidad = jQuery("#<portlet:namespace />periodicidad").val();

		
		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();		

		var recupera_ape = "false";
		if (document.getElementById("<portlet:namespace />recupera_ape").checked) { recupera_ape = true; } else { recupera_ape = false; } 
		var observaciones = jQuery("#<portlet:namespace />observaciones").val();
		var observaciones_int = jQuery("#<portlet:namespace />observaciones_int").val();
		var documentacion = jQuery("#<portlet:namespace />documentacion").val();
		var accionOriginal = document.getElementById("accionOriginal").value;

        var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
		var sucursal_entidad = 0;
		var id_prestador=document.getElementById("<portlet:namespace />id_prestador_trat").value;

		
		var id_seccional = 0;//document.getElementById("id_seccional").value;

		var id_tercerizadora = '';
		var importe_tercerizado = document.getElementById("<portlet:namespace />importe_tercerizado").value;

		var cantidad_viajes_mensuales = 0;
		var cantidad_kilometros_dia = 0;
		var cantidad_kilometros_mes = 0;
		var importe_kilometro_unit = 0;		
		var importe_total_km = 0;

		var hs_espera_dia = 0;
		var hs_espera_mes = 0;
		var importe_hs_espera_unit = 0;
		var importe_total_hs = 0;

		if (document.getElementById("<portlet:namespace />cantidad_viajes_mensuales")!=null && trim(document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value) != ""){
			cantidad_viajes_mensuales = document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value;
		}
		if (document.getElementById("<portlet:namespace />cantidad_kilometros_dia") != null && trim(document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value)!= ""){
			cantidad_kilometros_dia = document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value;
		}							
		try {
			cantidad_kilometros_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(cantidad_kilometros_dia);			
		} catch (err) {}			
		if (document.getElementById("<portlet:namespace />importe_kilometro_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_kilometro_unit").value) != ""){
			importe_kilometro_unit = document.getElementById("<portlet:namespace />importe_kilometro_unit").value;
		}		
		if (document.getElementById("<portlet:namespace />hs_espera_dia")!=null && trim(document.getElementById("<portlet:namespace />hs_espera_dia").value) != ""){
			hs_espera_dia = document.getElementById("<portlet:namespace />hs_espera_dia").value;
		}
		try {
			hs_espera_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(hs_espera_dia);
		} catch (err) {}
		
		if (document.getElementById("<portlet:namespace />importe_hs_espera_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_hs_espera_unit").value) != ""){
			importe_hs_espera_unit = document.getElementById("<portlet:namespace />importe_hs_espera_unit").value;
		}	
		
		var esExcepcion = document.getElementById("<portlet:namespace />es_excepcion_tratamiento").value;
		
		esDiscapacitado = jQuery('#esDiscapacitado').val(); 
		if (document.getElementById("<portlet:namespace />es_discapacitado").checked) { esDiscapacitado = true; } else { esDiscapacitado = false; } 

		
		var motivoExcepcion= jQuery("#<portlet:namespace/>motivoExcepcion").val();
		
		var esLeche =jQuery('#<portlet:namespace />es_leche').attr('checked');
		var esDependencia =jQuery('#<portlet:namespace />es_dependencia').attr('checked');
		if (<portlet:namespace />validarCamposTD()) {
			jQuery('#<portlet:namespace />estado').removeAttr('disabled');
			var estado = jQuery("#<portlet:namespace />estado_").val();		
			var estado_anterior  = jQuery("#<portlet:namespace />estado_").val();
			if (estado == 0 || estado == 7) {// si estado es Preautorizacion o Monotributo lo pasamos a En curso
				estado = 1;	
			}
		//	jQuery('#<portlet:namespace />buscandoPrestadorTD').show();

			var url = '<portlet:actionURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/autorizaciones/editar_autorizacionprestacional_entry"  /></portlet:actionURL>';
			
			url = url +  '&id_tratamiento='+id_tratamiento+
			'&id_prestacion='+id_prestacion+'&cuil='+cuil+'&inte='+inte+'&cantidad='+cantidad+'&importe_total='+importe_total+
			'&fechaDesdeDia='+periodoDesdeDia+'&fechaDesdeMes='+periodoDesdeMes+'&fechaDesdeAnio='+periodoDesdeAnio+
			'&fechaHastaDia='+periodoHastaDia+'&fechaHastaMes='+periodoHastaMes+'&fechaHastaAnio='+periodoHastaAnio+
			'&accionOriginal='+accionOriginal+'&periodicidad='+periodicidad+'&observaciones='+encodeURI(observaciones)+'&recupera_ape='+recupera_ape+
			'&estado='+estado+'&documentacion='+ encodeURI(documentacion)+
			'&cuit_entidad='+cuit_entidad+'&sucursal_entidad='+sucursal_entidad+'&id_seccional='+id_seccional+
			'&cantidad_viajes_mes='+cantidad_viajes_mensuales+'&cantidad_kilometros_dia='+cantidad_kilometros_dia+
			'&cantidad_kilometros_mes='+cantidad_kilometros_mes+'&importe_kilometro_unit='+importe_kilometro_unit+
			'&hs_espera_dia='+hs_espera_dia+'&hs_espera_mes='+hs_espera_mes+'&importe_hs_espera_unit='+importe_hs_espera_unit+
			'&id_tercerizadora='+id_tercerizadora+'&importe_tercerizado='+importe_tercerizado+'&id_prestador='+id_prestador+
			'&es_excepcion='+esExcepcion+
			'&es_discapacitado='+esDiscapacitado+
			'&es_leche='+esLeche+
			'&motivo_excepcion='+motivoExcepcion+'&estado_anterior='+estado_anterior+'&es_dependencia='+esDependencia+'&observaciones_int='+encodeURI(observaciones_int);
			submitForm(document.<portlet:namespace />fmSSS, url);
	
		}
	}
	
	function <portlet:namespace />validarCamposTD() {
		var cantidad=jQuery('#<portlet:namespace />cantidad').val();
		var importe=jQuery('#<portlet:namespace />importe').val();
		var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();
	
		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();

        var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
        var sucursal_entidad = 0;

		var id_seccional = 0;//document.getElementById("id_seccional").value;
		
		var esLeche = jQuery('#<portlet:namespace />es_leche').attr('checked'); 

		if (trim(cuit_entidad) == "" && !esLeche) {
			alert("Prestador Obligatorio");
			jQuery("#<portlet:namespace />cuit_entidad_trat").focus();
			return false;
		}
		
		if(periodoDesdeDia!="" &&  periodoDesdeMes!="" && periodoDesdeAnio!="" &&
				periodoHastaDia!="" &&  periodoHastaMes!="" && periodoHastaAnio!=""){
			if (parseInt(periodoDesdeAnio,10) != parseInt(periodoHastaAnio,10)) {
				alert ("Las fechas deben pertenecer al mismo año lectivo");
				<portlet:namespace />setearControlesLecheMaternizada();
				return false;
			}
			
			if ((parseInt(periodoDesdeAnio,10) > parseInt(periodoHastaAnio,10))
					|| (parseInt(periodoDesdeAnio,10) == parseInt(periodoHastaAnio,10) && parseInt(periodoDesdeMes,10) > parseInt(periodoHastaMes,10))
					|| (parseInt(periodoDesdeAnio,10) == parseInt(periodoHastaAnio,10) && parseInt(periodoDesdeMes,10) == parseInt(periodoHastaMes,10) && parseInt(periodoDesdeDia,10) > parseInt(periodoHastaDia,10))){
					alert("La Fecha Desde corresponde a una fecha posterior a la Fecha Hasta.");
					<portlet:namespace />setearControlesLecheMaternizada();
					return false;
			}
		}
		
		if(trim(id_prestacion) == ""){
			alert("<liferay-ui:message key='prestacion-obligatoria' />");
			<portlet:namespace />setearControlesLecheMaternizada();
			jQuery("#<portlet:namespace />id_prestacion_trat").focus();
			return false;
		}
		if(trim(id_prestacion) != "" && jQuery("#<portlet:namespace />pres_seleccionada_trat").val()!="1"){
			alert("<liferay-ui:message key='prestacion-invalida' />");
			<portlet:namespace />setearControlesLecheMaternizada();
			jQuery("#<portlet:namespace />id_prestacion_trat").focus();
			return false;
		}
		if (cantidad.length == 0 || cantidad == '0.0' || cantidad == '0') {
			alert("<liferay-ui:message key='cantidad-obligatoria' />");
			<portlet:namespace />setearControlesLecheMaternizada();
			jQuery('#<portlet:namespace />cantidad').focus();
			return false;
		}
		if (importe.length == 0 || importe == '0.0' || importe == '0') {
			alert("<liferay-ui:message key='importe-obligatorio' />");
			<portlet:namespace />setearControlesLecheMaternizada();
			jQuery('#<portlet:namespace />importe').focus();
			return false;
		}
		var codigoo = jQuery("#<portlet:namespace />codigo_trat").val();
	    if (codigoo == '<%=WebKeysAutorizaciones.PRESTACION_TRANSPORTE%>') {			
	    	var importe_kms = document.getElementById("<portlet:namespace />importe_total_km").value;
	    	var importe_hs = document.getElementById("<portlet:namespace />importe_total_hs").value;
			var importe_total = parseFloat (importe_kms) + parseFloat (importe_hs);	

	    	if (importe_total.length == 0 || importe_total == '0.0' || importe_total == '0' || isNaN(importe_total) ) {
				alert("Debe ingresar valores para kilómetros o para horas de espera");				
				return false;
			}
	    		    	
	    }
	    
		return true;
	}

	function <portlet:namespace />cambiarEstadoTratamiento(estado) {
		if(!confirm("Seguro de querer cambiar el estado del tratamiento?")){
			return false;
		}else{
			var id_tratamiento = document.getElementById("tratamiento_id").value;
			var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();		
			var cuil = jQuery("#<portlet:namespace />cuil_titular").val();
			var inte = jQuery("#<portlet:namespace />inte").val();
			var cantidad=jQuery('#<portlet:namespace />cantidad').val();
			var importe_total = jQuery("#<portlet:namespace />importe").val();
			var periodicidad = jQuery("#<portlet:namespace />periodicidad").val();

			var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
			var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
			var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

			var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
			var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
			var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();		

			var recupera_ape = "false";
			if (document.getElementById("<portlet:namespace />recupera_ape").checked) { recupera_ape = true; } else { recupera_ape = false; } 
			var observaciones = jQuery("#<portlet:namespace />observaciones").val();		
			var documentacion = jQuery("#<portlet:namespace />documentacion").val();
			var accionOriginal = document.getElementById("accionOriginal").value;

			var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
			var sucursal_entidad = 0;
			
			var id_seccional = 0;//document.getElementById("id_seccional").value;

			var id_tercerizadora = '';
			var importe_tercerizado = document.getElementById("<portlet:namespace />importe_tercerizado").value;
			
			

			var url = '<portlet:actionURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/autorizaciones/editar_autorizacionprestacional_entry"  /></portlet:actionURL>';
			
			url = url + '&id_tratamiento='+id_tratamiento+'&estado='+estado+'&accionOriginal=estado'+
			'&cuil='+cuil+'&inte='+inte+'&cantidad='+cantidad+'&importe_total='+importe_total+
			'&fechaDesdeDia='+periodoDesdeDia+'&fechaDesdeMes='+periodoDesdeMes+'&fechaDesdeAnio='+periodoDesdeAnio+
			'&fechaHastaDia='+periodoHastaDia+'&fechaHastaMes='+periodoHastaMes+'&fechaHastaAnio='+periodoHastaAnio;
		
			submitForm(document.<portlet:namespace />fmSSS, url);

			return true;
		}
		return false;		
	}

	function <portlet:namespace />imprimirAT(){
		window.location.href ='/pdfservlet/?accion=<%="autorizacionTratamiento"%>&id_tratamiento=<%=tratamiento != null ? tratamiento
							.getId_String() : "0"%>';
		enviarEmailPestador();
	}

	function <portlet:namespace />imprimirATO(){
		window.location.href ='/odtservlet/?accion=<%="autorizacionTratamientoOdt"%>&id_tratamiento=<%=tratamiento != null ? tratamiento
							.getId_String() : "0"%>';		
	}

	function <portlet:namespace />imprimirATR(){
		window.location.href ='/odtservlet/?accion=<%="autorizacionTratamientoRtf"%>&id_tratamiento=<%=tratamiento != null ? tratamiento
							.getId_String() : "0"%>';		
	}
	
	function cambiaCuit_trat(){
	}

	function <portlet:namespace />mostrarDivTransporte(){
		jQuery('#<portlet:namespace />divTransporte').show();
	}

	function <portlet:namespace />ocultarDivTransporte(){
		jQuery('#<portlet:namespace />divTransporte').hide();
	}

	
	function enviarEmailPestador(){
		
		var id_tratamiento = document.getElementById("tratamiento_id").value;
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/autorizacion_email_prestador';		 
		url += '&tratamiento_id=' + id_tratamiento;			

		jQuery.ajax({   
				url: url,
				async:true,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					//nroLoteVigente = obj.lote;
				}
		}); 
	}
	
	<portlet:namespace />ocultarDivTransporte();
	
//DS - Nuevo para busqueda de importe en contrato detalle	

	jQuery("#<portlet:namespace />codigo_trat").change(function(){
      
	});
	
	jQuery('#<portlet:namespace />periodoDesdeDia').change(function(){
       <portlet:namespace />buscarContratoPrestadorAut('periododesdedia');
	});
	
	jQuery('#<portlet:namespace />periodoDesdeMes').change(function(){
       <portlet:namespace />buscarContratoPrestadorAut('periododesdemes');
	});
	
	jQuery('#<portlet:namespace />periodoDesdeAnio').change(function(){
       <portlet:namespace />buscarContratoPrestadorAut('periododedanio');
	});

	jQuery('#<portlet:namespace />periodoHastaDia').change(function(){
       <portlet:namespace />buscarContratoPrestadorAut('periodohastadia');
	});
	
	jQuery('#<portlet:namespace />periodoHastaMes').change(function(){
      <portlet:namespace />buscarContratoPrestadorAut('periodohastames');
	});
	
	jQuery('#<portlet:namespace />periodoHastaAnio').change(function(){
      <portlet:namespace />buscarContratoPrestadorAut('periodohastaanio');
	});	
	
	
//	jQuery('#<portlet:namespace />divMotivoExcepcion').hide();
	
//DS - Fin	
	
	
	
	function <portlet:namespace />limpiarPrestacion(){
		
	     jQuery("#<portlet:namespace />id_prestacion_trat").val('');
	     jQuery("#<portlet:namespace />codigo_trat").val('');
	     jQuery("#<portlet:namespace />prestacion_trat").val('');
	     jQuery("#<portlet:namespace />pres_seleccionada_trat").val('')
	     jQuery("#<portlet:namespace />btnBuscarPrestacion_trat").show();
	     
	}
	
	
	function <portlet:namespace />cambiarEstadoTratamientoConMotivo(estado) {
		var mot = prompt("Ingrese Motivo de Cambio de Estado","");
		if(mot==null){
			return false;
		}else{
			var id_tratamiento = document.getElementById("tratamiento_id").value;
			var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();		
			var cuil = jQuery("#<portlet:namespace />cuil").val();
			var inte = jQuery("#<portlet:namespace />inte").val();
			var cantidad=jQuery('#<portlet:namespace />cantidad').val();
			var importe_total = jQuery("#<portlet:namespace />importe").val();
			var periodicidad = jQuery("#<portlet:namespace />periodicidad").val();

			var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
			var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
			var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

			var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
			var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
			var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();		

			var recupera_ape = "false";
			if (document.getElementById("<portlet:namespace />recupera_ape").checked) { recupera_ape = true; } else { recupera_ape = false; } 
			var observaciones = jQuery("#<portlet:namespace />observaciones").val();		
			var documentacion = jQuery("#<portlet:namespace />documentacion").val();
			var accionOriginal = document.getElementById("accionOriginal").value;

			var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
			var sucursal_entidad = 0;
			
			var id_seccional = 0;//document.getElementById("id_seccional").value;

			var id_tercerizadora = '';
			var importe_tercerizado = document.getElementById("<portlet:namespace />importe_tercerizado").value;

			var url = '<portlet:actionURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/autorizaciones/editar_autorizacionprestacional_entry"  /></portlet:actionURL>';
			url = url + '&id_tratamiento='+ id_tratamiento+'&estado='+estado+'&accionOriginal=estado'+
			'&cuil='+cuil+'&inte='+inte+'&cantidad='+cantidad+'&importe_total='+importe_total+
			'&fechaDesdeDia='+periodoDesdeDia+'&fechaDesdeMes='+periodoDesdeMes+'&fechaDesdeAnio='+periodoDesdeAnio+
			'&fechaHastaDia='+periodoHastaDia+'&fechaHastaMes='+periodoHastaMes+'&fechaHastaAnio='+periodoHastaAnio+
			'&motivo='+encodeURI(mot);
	
			submitForm(document.<portlet:namespace />fmSSS, url);

			
			return true;
		}
		return false;		
	}

	
	function <portlet:namespace />cambiarEstadoTratamientoConMotivo(estado){
	   	
		var id_tratamiento = document.getElementById("tratamiento_id").value;
		var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();		
		var cuil = jQuery("#<portlet:namespace />cuil").val();
		var inte = jQuery("#<portlet:namespace />inte").val();
		var cantidad=jQuery('#<portlet:namespace />cantidad').val();
		var importe_total = jQuery("#<portlet:namespace />importe").val();
		var periodicidad = jQuery("#<portlet:namespace />periodicidad").val();

		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();		

		var recupera_ape = "false";
		if (document.getElementById("<portlet:namespace />recupera_ape").checked) { recupera_ape = true; } else { recupera_ape = false; } 
		var observaciones = jQuery("#<portlet:namespace />observaciones").val();		
		var documentacion = jQuery("#<portlet:namespace />documentacion").val();
		var accionOriginal = document.getElementById("accionOriginal").value;

		var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
		var sucursal_entidad = 0;
		
		var id_seccional = 0;//document.getElementById("id_seccional").value;

		var id_tercerizadora = '';
		var importe_tercerizado = document.getElementById("<portlet:namespace />importe_tercerizado").value;
	
		popup = Liferay.Popup({title:"Motivo Cambio Estado",modal:true,width:350});
			   
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/motivo_cambio_estado_autorizacion_prestacional';
		url += "&id_tratamiento=" + id_tratamiento;
		url += "&estado=" + estado;
		jQuery(popup).load(url);
		   
	}


	function <portlet:namespace />cerrarCambiarEstadoTratamientoConMotivo(){
		if(popup){		
				Liferay.Popup.close(popup);
		}
	} 
	
	
	jQuery('#<portlet:namespace/>es_discapacitado').change(function(){
        var esDiscapacitado =jQuery('#<portlet:namespace/>es_discapacitado').attr('checked');
		
		if (esDiscapacitado){
			jQuery('#<portlet:namespace />es_leche').attr('checked',false);
			jQuery('#<portlet:namespace />es_leche').change();
		}
	});
	
	jQuery('#<portlet:namespace />es_leche').change(function(){
		var esLeche =jQuery('#<portlet:namespace />es_leche').attr('checked');
		
		if (esLeche){
			jQuery('#<portlet:namespace/>motivoExcepcion').val('CSI');
			jQuery('#<portlet:namespace />codigo_trat').val('<%=codLeche%>');
			<portlet:namespace />buscarPrestacion();
			jQuery('#<portlet:namespace />importe').val('0');
			jQuery('#<portlet:namespace />total').val('0');
			jQuery('#<portlet:namespace />id_prestador_trat').val('');
			jQuery('#<portlet:namespace />cuit_prestador_trat').val('');
			jQuery('#<portlet:namespace />nombre_prestador_trat').val('');
			<portlet:namespace />setearControlesLecheMaternizada();
		    
		}else{
		   jQuery('#<portlet:namespace />codigo_trat').val('');	
		   jQuery('#<portlet:namespace />prestacion_trat').val('');
		   jQuery('#<portlet:namespace/>motivoExcepcion').val('-1');
		   
		   <portlet:namespace />setearControlesLecheMaternizada();
		   
		}   
	      
	});
	
	
	function <portlet:namespace />setearControlesLecheMaternizada(){
        var esLeche =jQuery('#<portlet:namespace />es_leche').attr('checked');
		if (esLeche){
		   jQuery('#<portlet:namespace />div_prestador :input').attr('disabled', true);
		   jQuery('#<portlet:namespace />div_prestacion :input').attr('disabled', true);
		   jQuery('#<portlet:namespace />importe').attr('readonly', true);
		   jQuery('#<portlet:namespace/>motivoExcepcion').attr('disabled', true);
		   jQuery('#<portlet:namespace/>es_discapacitado').attr('checked',false);
		}else{
		   jQuery('#<portlet:namespace/>motivoExcepcion').attr('disabled', false);
		   jQuery('#<portlet:namespace />div_prestacion :input').attr('disabled', false);
		   jQuery('#<portlet:namespace />div_prestador :input').attr('disabled', false);
		   jQuery('#<portlet:namespace />importe').attr('readonly', false);
		}   
	}
	
	
function <portlet:namespace />duplicarTratamientoDiscapacidad() {
		
		jQuery('#<portlet:namespace/>motivoExcepcion').attr('disabled', false);
		jQuery('#<portlet:namespace />div_prestacion :input').attr('disabled', false);
		
		var cuil, inte = ""; 
		var esDiscapacitado = false;
		var id_tratamiento = 0;//  document.getElementById("tratamiento_id").value;		
		var id_prestacion = jQuery("#<portlet:namespace />id_prestacion_trat").val();		
		
	    cuil = jQuery("#<portlet:namespace/>cuil_titular").val();	
		inte = jQuery("#<portlet:namespace/>inte").val();	
		
		
		var cantidad=jQuery('#<portlet:namespace />cantidad').val();
		var importe_total = jQuery("#<portlet:namespace />importe").val();
		var periodicidad = jQuery("#<portlet:namespace />periodicidad").val();

		
		var periodoDesdeDia=jQuery('#<portlet:namespace />periodoDesdeDia').val();
		var periodoDesdeMes=jQuery('#<portlet:namespace />periodoDesdeMes').val();
		var periodoDesdeAnio=jQuery('#<portlet:namespace />periodoDesdeAnio').val();

		var periodoHastaDia=jQuery('#<portlet:namespace />periodoHastaDia').val();
		var periodoHastaMes=jQuery('#<portlet:namespace />periodoHastaMes').val();
		var periodoHastaAnio=jQuery('#<portlet:namespace />periodoHastaAnio').val();		

		var recupera_ape = "false";
		if (document.getElementById("<portlet:namespace />recupera_ape").checked) { recupera_ape = true; } else { recupera_ape = false; } 
		var observaciones = jQuery("#<portlet:namespace />observaciones").val();
		var observaciones_int = jQuery("#<portlet:namespace />observaciones_int").val();
		var documentacion = jQuery("#<portlet:namespace />documentacion").val();
		var accionOriginal = "add";

        var cuit_entidad = document.getElementById("<portlet:namespace />cuit_prestador_trat").value;
		var sucursal_entidad = 0;
		var id_prestador=document.getElementById("<portlet:namespace />id_prestador_trat").value;

		
		var id_seccional = 0;//document.getElementById("id_seccional").value;

		var id_tercerizadora = '';
		var importe_tercerizado = document.getElementById("<portlet:namespace />importe_tercerizado").value;

		var cantidad_viajes_mensuales = 0;
		var cantidad_kilometros_dia = 0;
		var cantidad_kilometros_mes = 0;
		var importe_kilometro_unit = 0;		
		var importe_total_km = 0;

		var hs_espera_dia = 0;
		var hs_espera_mes = 0;
		var importe_hs_espera_unit = 0;
		var importe_total_hs = 0;

		if (document.getElementById("<portlet:namespace />cantidad_viajes_mensuales")!=null && trim(document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value) != ""){
			cantidad_viajes_mensuales = document.getElementById("<portlet:namespace />cantidad_viajes_mensuales").value;
		}
		if (document.getElementById("<portlet:namespace />cantidad_kilometros_dia") != null && trim(document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value)!= ""){
			cantidad_kilometros_dia = document.getElementById("<portlet:namespace />cantidad_kilometros_dia").value;
		}							
		try {
			cantidad_kilometros_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(cantidad_kilometros_dia);			
		} catch (err) {}			
		if (document.getElementById("<portlet:namespace />importe_kilometro_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_kilometro_unit").value) != ""){
			importe_kilometro_unit = document.getElementById("<portlet:namespace />importe_kilometro_unit").value;
		}		
		if (document.getElementById("<portlet:namespace />hs_espera_dia")!=null && trim(document.getElementById("<portlet:namespace />hs_espera_dia").value) != ""){
			hs_espera_dia = document.getElementById("<portlet:namespace />hs_espera_dia").value;
		}
		try {
			hs_espera_mes = parseFloat(cantidad_viajes_mensuales) * parseFloat(hs_espera_dia);
		} catch (err) {}
		
		if (document.getElementById("<portlet:namespace />importe_hs_espera_unit")!=null && trim(document.getElementById("<portlet:namespace />importe_hs_espera_unit").value) != ""){
			importe_hs_espera_unit = document.getElementById("<portlet:namespace />importe_hs_espera_unit").value;
		}	
		
		var esExcepcion = document.getElementById("<portlet:namespace />es_excepcion_tratamiento").value;
		
		esDiscapacitado = jQuery('#esDiscapacitado').val(); 
		if (document.getElementById("<portlet:namespace />es_discapacitado").checked) { esDiscapacitado = true; } else { esDiscapacitado = false; } 

		
		var motivoExcepcion= jQuery("#<portlet:namespace/>motivoExcepcion").val();
		
		var esLeche =jQuery('#<portlet:namespace />es_leche').attr('checked');
		var esDependencia =jQuery('#<portlet:namespace />es_dependencia').attr('checked');
		if (<portlet:namespace />validarCamposTD()) {
			jQuery('#<portlet:namespace />estado').removeAttr('disabled');
			var estado = jQuery("#<portlet:namespace />estado_").val();		
			var estado_anterior  = jQuery("#<portlet:namespace />estado_").val();
			if (estado == 0 || estado==7) {// si estado es Preautorizacion lo pasamos a En curso
				estado = 1;	
			}
		
			var url = '<portlet:actionURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/autorizaciones/editar_autorizacionprestacional_entry"  /></portlet:actionURL>';
			
			url = url +  '&id_tratamiento='+id_tratamiento+
			'&id_prestacion='+id_prestacion+'&cuil='+cuil+'&inte='+inte+'&cantidad='+cantidad+'&importe_total='+importe_total+
			'&fechaDesdeDia='+periodoDesdeDia+'&fechaDesdeMes='+periodoDesdeMes+'&fechaDesdeAnio='+periodoDesdeAnio+
			'&fechaHastaDia='+periodoHastaDia+'&fechaHastaMes='+periodoHastaMes+'&fechaHastaAnio='+periodoHastaAnio+
			'&accionOriginal='+accionOriginal+'&periodicidad='+periodicidad+'&observaciones='+encodeURI(observaciones)+'&recupera_ape='+recupera_ape+
			'&estado='+estado+'&documentacion='+ encodeURI(documentacion)+
			'&cuit_entidad='+cuit_entidad+'&sucursal_entidad='+sucursal_entidad+'&id_seccional='+id_seccional+
			'&cantidad_viajes_mes='+cantidad_viajes_mensuales+'&cantidad_kilometros_dia='+cantidad_kilometros_dia+
			'&cantidad_kilometros_mes='+cantidad_kilometros_mes+'&importe_kilometro_unit='+importe_kilometro_unit+
			'&hs_espera_dia='+hs_espera_dia+'&hs_espera_mes='+hs_espera_mes+'&importe_hs_espera_unit='+importe_hs_espera_unit+
			'&id_tercerizadora='+id_tercerizadora+'&importe_tercerizado='+importe_tercerizado+'&id_prestador='+id_prestador+
			'&es_excepcion='+esExcepcion+
			'&es_discapacitado='+esDiscapacitado+
			'&es_leche='+esLeche+
			'&motivo_excepcion='+motivoExcepcion+'&estado_anterior='+estado_anterior+'&es_dependencia='+esDependencia+'&observaciones_int='+encodeURI(observaciones_int);
			submitForm(document.<portlet:namespace />fmSSS, url);
	
		}
	}

</script>