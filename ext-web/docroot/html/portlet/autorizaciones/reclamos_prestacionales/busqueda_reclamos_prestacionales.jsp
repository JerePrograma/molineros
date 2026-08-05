<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", 0); //prevents caching at the proxy server
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_RECLAM_PREST);
	boolean showReadOnlyReclamPrestac=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CONSULTA_RECLAMOS_PRESTACIONALES);
	
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");	
	//verificar los calendars
	Calendar fechaOspim = CalendarFactoryUtil.getCalendar();
	fechaOspim.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
	Calendar fechaOspimHta = CalendarFactoryUtil.getCalendar();
	fechaOspimHta.setTime(DateUtils.getLastDateOfYear(new Date(), true));	
	Calendar fechaCierreReclamo = CalendarFactoryUtil.getCalendar();
	fechaCierreReclamo.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
	Calendar fechaCierreReclamo1 = CalendarFactoryUtil.getCalendar();
	fechaCierreReclamo1.setTime(DateUtils.getLastDateOfYear(new Date(), true));
	Integer nroLote = ReclamosPrestacionesServiceUtil.getLoteVigenteReclamoPrestacional(); 	
	
	Calendar fechaseccional  = Calendar.getInstance();
	


	BusquedaReclamoFiltro filtro = (BusquedaReclamoFiltro) request.getSession().getAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_RECLAMOS);
	Calendar fechaDesde =null; 		
	Calendar fechaHasta = null;
	Calendar fechaCierreDesde =null; 		
	Calendar fechaCierreHasta = null;
	Calendar fechaComprobante = null;
	String nroReclamo = "";
	String tipoPedidoFiltro = "";
	String tipoPedidoSel ="";
	String sectorSel = "";
	String nroAfilido="";
	String cuil="";
	String integrante="";
	String tipoDocumento="";
	String nroDocumento="";
	String idSeccional="";
	String apellido="";
	String nombre="";
	String idSeccionalAfiSel="";
	String descSeccionalAfiSel="";
	String resolucion="";
	String estadoFiltro="";
	String tipoGestionFiltro="";
	String nroLoteFiltro="";
	String idSeccionalCarga="";	
	String descSeccionalCarga="";
	String codigoPrestacionFiltro="";
	String descPrestacionFiltro="";
	String tipoPrestacion="";
	String codigoFarmaciaFiltro="";
	String descFarmaciaFiltro="";
	String entidadAfi="";
	String frequencia="";
	String comprobante="";
	String comprobanteSucu="";
	String comprobanteNumero="";
	String cuitEmpresa="";
	String sucEmpresa="";
	String razonSocialEmpresa="";
	int codIntegracion = 0 ;
	int recuperoSur = 0;


	
	if(filtro != null){
		if(filtro.getfDesde()!=null){
			fechaDesde =  Calendar.getInstance();
			fechaDesde.setTime(filtro.getfDesde());
		}
		if(filtro.getfHasta()!=null){
			fechaHasta =  Calendar.getInstance();
			fechaHasta.setTime(filtro.getfHasta());
		}	
		if(filtro.getCierreDesde()!=null){
			fechaCierreDesde =  Calendar.getInstance();
			fechaCierreDesde.setTime(filtro.getCierreDesde());
		}
		if(filtro.getCierreHasta()!=null){
			fechaCierreHasta =  Calendar.getInstance();
			fechaCierreHasta.setTime(filtro.getCierreHasta());
		}
		if(filtro.getFechaEmision()!=null){
			fechaComprobante =  Calendar.getInstance();
			fechaComprobante.setTime(filtro.getFechaEmision());
		}
		
		nroReclamo=filtro.getNroReclamo();
		tipoPedidoSel=filtro.getTipoPedido();
		sectorSel=filtro.getSector();
		nroAfilido=filtro.getNroAfilido();
		cuil=filtro.getCuil();
		integrante=filtro.getIntegrante();
		tipoDocumento=filtro.getTipoDocumentoSel();
		nroDocumento=filtro.getNroDocumento();
		apellido = filtro.getApellido();
		nombre = filtro.getNombre();
		idSeccionalAfiSel = filtro.getIdSeccionalAfiliado();
		descSeccionalAfiSel = filtro.getDescSeccionalAfiliado();
		tipoPedidoFiltro = filtro.getTipoPedido();
		resolucion = filtro.getResolucion();	
		estadoFiltro = filtro.getEstados();
		tipoGestionFiltro = filtro.getTipoGestion();
		nroLoteFiltro = filtro.getNroLote();
		idSeccionalCarga = filtro.getIdSeccionalCarga();
		descSeccionalCarga =  filtro.getDescSeccionalCarga();
		codigoPrestacionFiltro = filtro.getIdPrestacion();
		descPrestacionFiltro = filtro.getDescPrestacion();
		tipoPrestacion = filtro.getPrestacione();
		codigoFarmaciaFiltro = filtro.getIdFarmacia();
		descFarmaciaFiltro = filtro.getDescFarmacia();
		entidadAfi = filtro.getEndidadAfi();
		frequencia = filtro.getFrecuencia();
		comprobante = filtro.getComprobante();
		comprobanteSucu = filtro.getSucursalComprobate();
		comprobanteNumero = filtro.getNroComprobante();
		cuitEmpresa = filtro.getCuit();
		sucEmpresa = filtro.getSucursalEmpresa();
		razonSocialEmpresa = filtro.getRazonSocia();
		codIntegracion = filtro.getCodIntegracion();
		recuperoSur = filtro.getRecuperoSur();
	}
	
	
%>



<form action="<%=portletURL%>" method="post" 
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;">
	
<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />	
	
	<liferay-portlet:renderURLParams varImpl="portletURL" />
	<fieldset class="block-labels">				
	<legend> <liferay-ui:message key="Datos del Reclamo Prestacional" /> 
	</legend>				
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
		
		<tr>
			<td><label>Nro Reclamo:</label></td>
			<td><input id="<portlet:namespace />nroReclamoFiltro" name="<portlet:namespace />nroReclamoFiltro" size="10" maxlength="10" type="text" value="" /></td>
			<td>&nbsp;</td>
			<td><label><liferay-ui:message key="Tipo Pedido" />:</label></td>
			<td>
				<select name="<portlet:namespace />tipopedido" id="<portlet:namespace />tipopedido" >
					<option value="SELECCIONE" >SELECCIONE</option>
					<option value="EXCEPCION">EXCEPCIÓN</option>
					<option value="REINTEGRO">REINTEGRO</option>
					<option value="EXTRACAPITA">EXTRACÁPITA</option>
				</select>					
			</td>
			<td>&nbsp;</td>
				
			<td><label><liferay-ui:message key="Sector" />:</label></td>	
			<td><table>
					<select name="<portlet:namespace/>sector"
						id="<portlet:namespace/>sector" >
						<option value="SELECCIONE" >SELECCIONE</option>
						<!-- <option value="LIQUIDACIONES">LIQUIDACIONES</option> -->
						<option value="DISCAPACIDAD">DISCAPACIDAD</option>
						<option value="PRESTACIONES MEDICAS">PRESTACIONES MEDICAS</option>
						<option value="FARMACIA">FARMACIA</option>
						<option value="LEGALES">LEGALES</option>
						<option value="ODONTOLOGIA">ODONTOLOGIA</option>
					</select>					
				</table>
			</td>
		
				
			<td><label id="integracion_label"  style="display:none"><liferay-ui:message key="integracion"/>:</label></td>
			<td><select name="<portlet:namespace/>integracion" id="<portlet:namespace/>integracion"
					 style="display:none" >												
				<option value="0">Seleccione Integración</option>
				<% for (ReclamosPrestacionalesIntegracion integracion : listaIntegracion) { %>
				<option value="<%= integracion.getId() %>"><%=integracion.getDescripcion()%>
				</option>
					<% } %>		
				</select>
				</td>
				<td>
				<div id="integracion_div"  style="display:none">
					<img  id="integracion_desc"  height='16'  width='16'  src='/html/themes/classic/images/common/help.png' title=''   />
				</div>
				</td>
			<td>
			<td><label>Resoluci&oacute;n:</label></td>	
			<td>					
			<select name="<portlet:namespace/>resolucion"
						id="<portlet:namespace />resolucion"  > 
						<option value="SELECCIONE">SELECCIONE</option>
						<option value="AUTORIZADO">AUTORIZADO</option>
						<option value="RECHAZADO">RECHAZADO</option>
					</select>
			 </td>
			 <td colspan="20">&nbsp;</td>
			 <td colspan="20">&nbsp;</td>
			 <td>&nbsp;&nbsp;&nbsp;</td>
			
			
		</tr>
		
		<tr>
		<table>
		<tr>
			<td><label><liferay-ui:message key="Estados" />: &nbsp;&nbsp;&nbsp;&nbsp;</label></td>
			<td><select   
				 name="<portlet:namespace/>estado"
				id="<portlet:namespace/>estado">
					<option  value="-1">TODOS</option>
					<% for (EstadosReclamosPrestacionales estados : listaestados) { %>
					<option
						value="<%= estados.getId() %>"><%=estados.getDescripcion()%>
						</option>
					<% } %>					
			</select>	
			 </td> 
			<td colspan="2">&nbsp;&nbsp;&nbsp;</td>
			
			<td><label><liferay-ui:message key="tipo-gestion" />: &nbsp;&nbsp;&nbsp;&nbsp;</label></td>
			<td>					
				<select	 name="<portlet:namespace/>tipo_gestion_cierre_reclamo"
				id="<portlet:namespace/>tipo_gestion_cierre_reclamo"      >
					<option selected value="0">SELECCIONE LA GESTION</option>
					<% for (TiposDeGestionReclamosPrestacionales tipogestion  : listatipogestionreclamos) { %>
					<option value="<%=tipogestion.getId()+"|"+tipogestion.getDescripcion()%>"><%=tipogestion.getDescripcion()%></option>
					<% } %>
			</select>	
			 </td>
			<td colspan="2">&nbsp;&nbsp;&nbsp;</td>
			 <td><label>Nro.Lote: &nbsp;&nbsp;&nbsp;&nbsp;</label></td>
			 <td><input id="<portlet:namespace />nroLote_filtro" name="<portlet:namespace />nroLote_filtro" size="10" maxlength="20" type="text" value=''
			       onKeyPress="return soloNumeros(event)" /></td>
			<%--  <td><label><liferay-ui:message key="Prestaciones" />:</label> </td>
		<td><select name="<portlet:namespace />tipoprestacion" id="<portlet:namespace />tipoprestacion" 
					onchange="manejarTipoPrestacion();">
				<option value="seleccione">TODAS</option>
				<option value="farmacia">FARMACIA</option>
				<option value="clinicas">PRESTACIONES MEDICAS</option>
			</select> 
		</td> --%>
		<!-- <td colspan="6">&nbsp;</td> -->
		<td colspan="2">&nbsp;&nbsp;&nbsp;</td>
		
		<td><label><liferay-ui:message key="seccional" />: &nbsp;&nbsp;&nbsp;&nbsp;</label></td>														
		  <td colspan="3" style="vertical-align:top" >
		  <liferay-util:include page='/html/portlet/autorizaciones/busqueda_seccional.jsp'>
		  <liferay-util:param value="_sec_filtro" name="prefijo" />
		  </liferay-util:include>
	   </td>
	 		
		<td colspan="2">&nbsp;</td>		
		<td colspan="2">&nbsp;</td>	
	<tr>
	</table>				
	   
	</tr>
			</br>
	
	<table> 
		<tr>
	
	<tr>
		<td><liferay-ui:message key="Recuperable" />:</label> 
		<td>
			<select name="<portlet:namespace />recuperable_sur" id="<portlet:namespace />recuperable_sur">
					<option value="0">Seleccione</option>
					<option value="1">SURGE</option>
					<option value="3">Integración</option>
					<option value="2">NO Recuperable</option>
			</select>
		</td>
		<td><label><liferay-ui:message key="Prestaciones" />:</label> </td>
		<td><select name="<portlet:namespace />tipoprestacion" id="<portlet:namespace />tipoprestacion" 
					onchange="manejarTipoPrestacion();">
				<option value="seleccione">TODAS</option>
				<option value="farmacia">FARMACIA</option>
				<option value="clinicas">PRESTACIONES MEDICAS</option>
			</select> 
		</td>	
		<td colspan="10">	
			<div id="<portlet:namespace />busqueda_farmacia">
			
			<!-- <label style="text-decoration: underline;"><liferay-ui:message key="grupo-filtro-busqueda-medicamento"/>:</label>
					<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">			
					<tr>
				      <td colspan="4"> -->	       
							<liferay-util:include page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
								<liferay-util:param name="search_url" value="/autorizaciones/buscar_medicamentos" />
								<liferay-util:param name="troquel" value='' />
								<liferay-util:param name="nombre_medicamento" value='' />
								<liferay-util:param name="id_medicamento" value='' />
								<liferay-util:param name="esEditable" value='true' />
								<liferay-util:param name="mostrar_con_presentacion" value='true' />
							</liferay-util:include>
				     <!--  </td>	
				    </tr>
				    	
				    </table>	 -->
			</div>	
			<div id="<portlet:namespace />busqueda_prestaciones">
			      <!--  <label style="text-decoration: underline;"><liferay-ui:message key="grupo-filtro-busqueda-prestaciones-medicas"/>:</label> -->
					<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">	
						<tr>
					    <td><label><liferay-ui:message key="codigo-presentado"/>:</label></td>
							<td><input id="<portlet:namespace />codigoSeguimiento_filtro" name="<portlet:namespace />codigoSeguimiento_filtro" size="10" maxlength="20" type="text" value=''/></td>
							<td><input id="<portlet:namespace />descripcionSeguimiento_filtro" name="<portlet:namespace />descripcionSeguimiento_filtro" size="80" maxlength="200" type="text" value=''					
							/></td>
							<td><div id="<portlet:namespace />divBtnBusca">
									<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();" tabindex="-1">Buscar</a>
									<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();" tabindex="-1">Limpiar</a>
							</div> </td>
						</tr>
					</table>		
			  </div>
		</td>	
	</tr>
		
	</table> 
	
		

 </table>
<table>		
<tr>
	<td colspan="6">&nbsp;</td>
	
		<tr>			
			<td width="3%"><label><liferay-ui:message key="fecha-desde-ospim" />:</label> </td>
			<td width="22%"><liferay-ui:input-date dayParam="fechaOspimDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaOspimMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaOspimAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaOspim.get(Calendar.YEAR)  -5%>"
			yearRangeEnd="<%= fechaOspim.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaOspim.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /> </td>
		
			
		
					
		
			<td width="3%"><label><liferay-ui:message key="fecha-hasta-ospim" />:</label> </td>
			<td width="22%"> <liferay-ui:input-date dayParam="fechaOspimDiaHta"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaOspimMesHta"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaOspimAnioHta"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaOspimHta.get(Calendar.YEAR) -5%>"
			yearRangeEnd="<%= fechaOspimHta.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaOspimHta.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /></td>
		
				
		
			<td width="3%"><liferay-ui:message key="fecha-desde-CierreReclamo" />:</label> </td>
			<td width="22%"><liferay-ui:input-date dayParam="fechaCierreReclamoDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaCierreReclamoMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaCierreReclamoAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaCierreReclamo.get(Calendar.YEAR)-5  %>"
			yearRangeEnd="<%= fechaCierreReclamo.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaCierreReclamo.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /> </td>
			
			
			
			
			<td width="3%"><liferay-ui:message key="fecha-hasta-CierreReclamo" />:</label> </td>
			<td width="27%"> <liferay-ui:input-date dayParam="fechaCierreReclamoDiaHta"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaCierreReclamoMesHta"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaCierreReclamoAnioHta"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaCierreReclamo1.get(Calendar.YEAR) -5%>"
			yearRangeEnd="<%= fechaCierreReclamo1.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaCierreReclamo1.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /></td>
			
		    
		</tr>	
</table>
</fieldset>

	
<table class="tabla-afiliado">
<!-- 	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>	 -->
	<tr>
		<td>
		<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend> 
			
			<liferay-util:include page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
				<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
				<liferay-util:param value="<%=String.valueOf(true)%>" name="discapacidad" />
				<liferay-util:param name="pag_reintegro" value='1' />				
			</liferay-util:include>
		</fieldset>
		</td>
	</tr>
</table>



<div id="<portlet:namespace />datos_prestacion_ingreso">



   <table  class="tabla-afiliado">
	 <tr>
	    <td colspan="15">
		     <fieldset class="block-labels">
	         <legend>
		         <liferay-ui:message key="Datos del Comprobante" />
	         </legend>
	         <table >
	           <tr> 	
			    <td  colspan="15">    
			     <table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;"> 
			      <tr>
			        <td> <label><liferay-ui:message key="Frecuencia" />:</label></td>
			        <td> <select 
						name="<portlet:namespace />frecuencia"
						id="<portlet:namespace />frecuencia" >
								<option value="SELECCIONE">SELECCIONE</option>
								<option value="UNICA">UNICA</option>
								<option value="SEMANAL">SEMANAL</option>
								<option value="TRIMESTRAL">TRIMESTRAL</option>
								<option value="MENSUAL">MENSUAL</option>
								<option value="SEMESTRAL">SEMESTRAL</option>
								<option value="ANUAL">ANUAL</option>					
					  </select>
			        </td>
			
                    <td><label><liferay-ui:message key="comprobante" />:</label></td>
			        <td>
			           <select name="<portlet:namespace/>comprobante_tipo" id="<portlet:namespace/>comprobante_tipo">
				      	<option value="Seleccione">SELECCIONE</option>
				        <option value="FCP">FCP</option>
				        <option value="RCB">RCB</option>
				        <option value="OTR">OTRO</option>
				        <option value="AUT">AUTORIZACION</option>
			           </select> 
			        </td>
			
			        <td>Suc:</td>
			        <td> 
			          <input id="<portlet:namespace />comprobante_suc"
				       name="<portlet:namespace />comprobante_suc" size="8" maxlength="5"
				       type="text"	value=""  />
			        </td>  	
			
			
			        <td>Nro:</td>
			        <td> 
			          <input id="<portlet:namespace />comprobante_nro"
				      name="<portlet:namespace />comprobante_nro" size="11" maxlength="15"
				      type="text"	value="" />
			        </td>  	
			        <td><label>F.Emision: </label></td>
			        <td colspan="1"><liferay-ui:input-date dayParam="fechaComprobanteDia"
					   dayValue="" 
					   dayNullable="<%=true %>"
					   monthParam="fechaComprobanteMes"
					   monthValue="-1"					
					   monthNullable="<%= true %>"
					   yearParam="fechaComprobanteAnio"
					   yearValue=""
					   yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
					   yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR)  %>"
					   yearNullable="<%= true %>"
					   firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>" />
			        </td>
			      </tr> 
			    </table>  
			  </td> 
			</tr>
			<tr><td>&nbsp;</td></tr>
			<tr>
			  <td colspan="15"><liferay-util:include
					page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					<liferay-util:param name="esEditable" value='true'/>
						<liferay-util:param name="cuit" value='' />
						<liferay-util:param name="sucu" value='' />
						<liferay-util:param name="razon" value='' />
						<liferay-util:param name="id_seccional" value='' />
						<liferay-util:param name="esEmpresaPrestador" value='true' />
						<liferay-util:param name="suf_entidad" value='_'/>				
					</liferay-util:include>
			  </td>
			</tr>
		   </table>
		   </fieldset>
			
	  	</td>
	  </tr>
	


   </table>

</div>

<%-- <table>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="Prestaciones" />:</label> </td>
		<td><select name="<portlet:namespace />tipoprestacion" id="<portlet:namespace />tipoprestacion" 
					onchange="manejarTipoPrestacion();">
				<option value="seleccione">TODAS</option>
				<option value="farmacia">FARMACIA</option>
				<option value="clinicas">PRESTACIONES MEDICAS</option>
			</select> 
		</td>

		<td>	
			<div id="<portlet:namespace />busqueda_farmacia">
			<label style="text-decoration: underline;"><liferay-ui:message key="grupo-filtro-busqueda-medicamento"/>:</label>
					<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">			
					<tr>
				      <td colspan="4">	       
							<liferay-util:include page="/html/portlet/utils/medicamentos/busqueda_medicamentos.jsp">
								<liferay-util:param name="search_url" value="/autorizaciones/buscar_medicamentos" />
								<liferay-util:param name="troquel" value='' />
								<liferay-util:param name="nombre_medicamento" value='' />
								<liferay-util:param name="id_medicamento" value='' />
								<liferay-util:param name="esEditable" value='true' />
								<liferay-util:param name="mostrar_con_presentacion" value='true' />
							</liferay-util:include>
				      </td>	
				    </tr>
				    	
				    </table>	
			</div>
		</td>

		<td>		
			<div id="<portlet:namespace />busqueda_prestaciones">
			       <label style="text-decoration: underline;"><liferay-ui:message key="grupo-filtro-busqueda-prestaciones-medicas"/>:</label>
					<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">	
						<tr>
					    <td><label><liferay-ui:message key="codigo-presentado"/>:</label></td>
							<td><input id="<portlet:namespace />codigoSeguimiento_filtro" name="<portlet:namespace />codigoSeguimiento_filtro" size="10" maxlength="20" type="text" value=''/></td>
							<td><input id="<portlet:namespace />descripcionSeguimiento_filtro" name="<portlet:namespace />descripcionSeguimiento_filtro" size="80" maxlength="200" type="text" value=''					
							/></td>
							<td><div id="<portlet:namespace />divBtnBusca">
									<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();" tabindex="-1">Buscar</a>
									<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();" tabindex="-1">Limpiar</a>
							</div> </td>
						</tr>
					</table>		
			  </div>
		</td>
	</tr>
</table> --%>	    
<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
	<tr>
		<td colspan="4" align="left"><input id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar-reclamo-prestacion" />" type="button" />
		</td>	
	
		<td colspan="4" align="left">
			<input type="button" value="<liferay-ui:message key="limpiar"/>"
 			title="<liferay-ui:message key="limpiar" />" 
			onClick="<portlet:namespace />limpiarFiltros();" />
		</td>
		
		
		<%if (showABMButtons) { %>
			<td colspan="4" align="left">
				<input type="button" value="<liferay-ui:message key="Nuevo"/>"
					title="<liferay-ui:message key="nuevo-reclamo-prestacion" />" 
					onClick="<portlet:namespace />altaReclamoPrestacional();" />
			</td>
			<td colspan="4" align="center">
			
				<input id="<portlet:namespace />exportar-busqueda" value="<liferay-ui:message key="exportar-busqueda"/>" 
				title="<liferay-ui:message key="exportar-busqueda" />" type="button" />
			</td>
			<td colspan="4" align="center">
				<input id="<portlet:namespace />cerrarlote" value="Cerrar Lote <%=nroLote%>" 
				title="Cerrar Lote <%=nroLote%>" type="button" onclick="<portlet:namespace />cerrarLote()" />
			</td>
			<td><input id="<portlet:namespace />imprimirLote" name="<portlet:namespace />imprimirLote" type="button" value="Imprimir Lote"  onclick="imprimirLote()"/></td>
		<%}else{ %>
			<%if (showReadOnlyReclamPrestac) { %>
				<td colspan="4" align="center">
				
					<input id="<portlet:namespace />exportar-busqueda" value="<liferay-ui:message key="exportar-busqueda"/>" 
					title="<liferay-ui:message key="exportar-busqueda" />" type="button" />
				</td>
			<%} %>
			<td colspan="8">&nbsp;</td>
			
		<%} %>			
	</tr>

</table>

<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
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
<!--  
<fieldset class="block-labels">				
	<legend> Exportar Imágenes</legend>		
	
	
   <table style="align: center;">
	<tr>
		<td>Reclamos a Exportar</td>
		<td> 
		   <input id="<portlet:namespace />rps"
				       name="<portlet:namespace />rps" size="70" maxlength="5000"
				       type="text"	value=""  />
		</td>  	
		<td align="center">
		
		 <input id="<portlet:namespace />exportar-imagenes" value="Exportar Imágenes" 
					title="Exportar Imágenes" type="button" />
		
		</td>
	</tr>
	<tr>
	<table><tr>
	<td><label style="color:blue" >Los números de Reclamos deben estar separados por punto y coma</label></td>
	</tr></table>
	</tr>
   </table>	
			
  

</fieldset>
-->
<div align="center" id="<portlet:namespace />busquedaReclamoPrestaDiv">
</div>
</fieldset>
</form>

<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />
<input id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value=""/>
<input id="<portlet:namespace />codigogestion" name="<portlet:namespace />codigogestion" type="hidden" value=""/>
<!-- <input type="hidden"   name="pagina" id="pagina" value="5"/> -->
<script type="text/javascript">
  
    jQuery("#<portlet:namespace />busqueda_prestaciones").hide();        
    jQuery("#<portlet:namespace />busqueda_farmacia").hide();  
    jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_reclamos_prestaciones_sesion';
	jQuery('#<portlet:namespace />busquedaReclamoPrestaDiv').load(url);
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busquedaReclamosPrestacionales();});	
	
	jQuery(document).ready(function() {

		<portlet:namespace />loadFiltros();
		//jQuery('#<portlet:namespace />observacion_medica_div').hide();
		if ('EXCEPCION' ==  jQuery("#<portlet:namespace />tipopedido").val()){
			traerDescripcion();
		}			 
		integracionReclamo();		
	
	});
	

	jQuery("#<portlet:namespace />tipopedido").change(function(){
		
		try {	
			 integracionReclamo();
		}
		catch (err) {
			alert('error tipopedido ');
		}

	});
	
	
	jQuery("#<portlet:namespace />integracion").change(function(){
		
		try {	

			traerDescripcion();
	   		
		}
		catch (err) {
			alert('error integracion ');
		}

	});
	
	function <portlet:namespace />loadFiltros() {
		
		jQuery('#<portlet:namespace />nroReclamoFiltro').val(<%=nroReclamo%>);
		var cmbSipopedido = "<%=tipoPedidoFiltro%>";
		jQuery('#<portlet:namespace />tipopedido').val(cmbSipopedido);
		var cmbSector = "<%=sectorSel%>";
		jQuery('#<portlet:namespace />sector').val(cmbSector);
		jQuery('#<portlet:namespace />numero_afi').val(<%=nroAfilido%>);
		var cmbTipoDoc = "<%=tipoDocumento%>";
		jQuery('#<portlet:namespace />tipoDoc').val(cmbTipoDoc);
		jQuery('#<portlet:namespace />nroDoc').val(<%=nroDocumento%>);
		var txtApellido  = "<%=apellido%>";
		jQuery('#<portlet:namespace />apellido').val(txtApellido);
		var txtNombre  = "<%=nombre%>";
		jQuery('#<portlet:namespace />nombre').val(txtNombre);

		jQuery('#<portlet:namespace />cuil').val(<%=cuil%>);
		jQuery('#<portlet:namespace />inte').val(<%=integrante%>);
		
		jQuery('#<portlet:namespace />id_seccional').val(<%=idSeccionalCarga%>);
		jQuery('#<portlet:namespace />seccional').val("<%=descSeccionalCarga%>");
				
		jQuery('#<portlet:namespace />id_seccional_sec_filtro').val(<%=idSeccionalAfiSel%>);
		jQuery('#<portlet:namespace />seccional_sec_filtro').val("<%=descSeccionalAfiSel%>");
		
		jQuery('#<portlet:namespace />entidad').val("<%=entidadAfi%>");

		
		jQuery('#<portlet:namespace />fechaOspimDia').val(<%=fechaDesde != null ? fechaDesde.get(Calendar.DAY_OF_MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaOspimMes').val(<%=fechaDesde != null ? fechaDesde.get(Calendar.MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaOspimAnio').val(<%=fechaDesde != null ? fechaDesde.get(Calendar.YEAR) :""%>);

		jQuery('#<portlet:namespace />fechaOspimDiaHta').val(<%=fechaHasta != null ? fechaHasta.get(Calendar.DAY_OF_MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaOspimMesHta').val(<%=fechaHasta != null ? fechaHasta.get(Calendar.MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaOspimAnioHta').val(<%=fechaHasta != null ? fechaHasta.get(Calendar.YEAR) :""%>);
		
		jQuery('#<portlet:namespace />fechaCierreReclamoDia').val(<%=fechaCierreDesde != null ? fechaCierreDesde.get(Calendar.DAY_OF_MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaCierreReclamoMes').val(<%=fechaCierreDesde != null ? fechaCierreDesde.get(Calendar.MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaCierreReclamoAnio').val(<%=fechaCierreDesde != null ? fechaCierreDesde.get(Calendar.YEAR) :""%>);
		
		jQuery('#<portlet:namespace />fechaCierreReclamoDiaHta').val(<%=fechaCierreHasta != null ? fechaCierreHasta.get(Calendar.DAY_OF_MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaCierreReclamoMesHta').val(<%=fechaCierreHasta != null ? fechaCierreHasta.get(Calendar.MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaCierreReclamoAnioHta').val(<%=fechaCierreHasta != null ? fechaCierreHasta.get(Calendar.YEAR) :""%>);
		
		var resolucion = "<%=resolucion%>";
		jQuery('#<portlet:namespace />resolucion').val(resolucion);
		
		var estadoFiltro = "<%=estadoFiltro%>";
		jQuery('#<portlet:namespace />estado').val(estadoFiltro);
		
		var tipoGestionFiltro = "<%=tipoGestionFiltro%>";
		jQuery('#<portlet:namespace />tipo_gestion_cierre_reclamo').val(tipoGestionFiltro);
		
		

		jQuery('#<portlet:namespace />nroLote_filtro').val(<%=nroLoteFiltro%>);

		var tipoPrestacionTxt = "<%=tipoPrestacion%>";

		jQuery('#<portlet:namespace />tipoprestacion').val(tipoPrestacionTxt);
		
		manejarTipoPrestacion();
				
		jQuery('#<portlet:namespace />troquel').val("<%=codigoFarmaciaFiltro%>");
		jQuery('#<portlet:namespace />nombre_medicamento').val("<%=descFarmaciaFiltro%>");

		var codigoSeguimiento_filtro = "<%=codigoPrestacionFiltro%>";
		jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val(codigoSeguimiento_filtro);

		jQuery('#<portlet:namespace />descripcionSeguimiento_filtro').val("<%=descPrestacionFiltro%>");
		
		jQuery('#<portlet:namespace />frecuencia').val("<%=frequencia%>");
		jQuery('#<portlet:namespace />comprobante_tipo').val("<%=comprobante%>");

		jQuery('#<portlet:namespace />comprobante_suc').val("<%=comprobanteSucu%>");
		jQuery('#<portlet:namespace />comprobante_nro').val("<%=comprobanteNumero%>");

		
		jQuery('#<portlet:namespace />fechaComprobanteDia').val(<%=fechaComprobante != null ? fechaComprobante.get(Calendar.DAY_OF_MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaComprobanteMes').val(<%=fechaComprobante != null ? fechaComprobante.get(Calendar.MONTH) :""%>);
		jQuery('#<portlet:namespace />fechaComprobanteAnio').val(<%=fechaComprobante != null ? fechaComprobante.get(Calendar.YEAR) :""%>);
		
		jQuery('#<portlet:namespace />cuit_entidad').val("<%=cuitEmpresa%>");
		jQuery('#<portlet:namespace />sucursal_entidad').val("<%=sucEmpresa%>");
		jQuery('#<portlet:namespace />entidad_').val("<%=razonSocialEmpresa%>");

		jQuery('#<portlet:namespace />integracion').val("<%=codIntegracion%>");
		jQuery('#<portlet:namespace />recuperable_sur').val("<%=recuperoSur%>");
		
	}
	
	
	
	
	function <portlet:namespace />limpiarFiltros(){

		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_reclamos_prestacionales_seccional'
		url = url + "&<%= Constants.CMD %>=<%=Constants.EXPIRE %>";
		

		jQuery('#<portlet:namespace />busquedaReclamoPrestaDiv').load(url, function() {
				jQuery('#<portlet:namespace />buscando').hide();            															
			  }
		);
        
		jQuery('#<portlet:namespace />nroReclamoFiltro').val('');
		jQuery('#<portlet:namespace />sector').val('');
		jQuery('#<portlet:namespace />numero_afi').val('');
		jQuery('#<portlet:namespace />tipoDoc').val('');
		jQuery('#<portlet:namespace />nroDoc').val('');
		jQuery('#<portlet:namespace />apellido').val('');
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		jQuery('#<portlet:namespace />id_seccional').val('');
		jQuery('#<portlet:namespace />seccional').val('');	
		jQuery('#<portlet:namespace />tipopedido').val('');	

		jQuery('#<portlet:namespace />fechaOspimDia').val('');
		jQuery('#<portlet:namespace />fechaOspimMes').val('');
		jQuery('#<portlet:namespace />fechaOspimAnio').val('');

		jQuery('#<portlet:namespace />fechaOspimDiaHta').val('');
		jQuery('#<portlet:namespace />fechaOspimMesHta').val('');
		jQuery('#<portlet:namespace />fechaOspimAnioHta').val('');
		
		jQuery('#<portlet:namespace />fechaCierreReclamoDia').val('');
		jQuery('#<portlet:namespace />fechaCierreReclamoMes').val('');
		jQuery('#<portlet:namespace />fechaCierreReclamoAnio').val('');
		
		jQuery('#<portlet:namespace />fechaCierreReclamoDiaHta').val('');
		jQuery('#<portlet:namespace />fechaCierreReclamoMesHta').val('');
		jQuery('#<portlet:namespace />fechaCierreReclamoAnioHta').val('');
		
		jQuery('#<portlet:namespace />resolucion').val('');

		jQuery('#<portlet:namespace />estado').val('');
		jQuery('#<portlet:namespace />tipo_gestion_cierre_reclamo').val('');
		jQuery('#<portlet:namespace />nroLote_filtro').val('');

		jQuery('#<portlet:namespace />id_seccional_sec_filtro').val('');
		jQuery('#<portlet:namespace />seccional_sec_filtro').val('');


		jQuery('#<portlet:namespace />tipoprestacion').val('');
		
		manejarTipoPrestacion();
				
		jQuery('#<portlet:namespace />troquel').val('');

		jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val('');
		jQuery('#<portlet:namespace />nombre_medicamento').val('');

		
		jQuery('#<portlet:namespace />descripcionSeguimiento_filtro').val('');
		
		jQuery('#<portlet:namespace />frecuencia').val('');
		jQuery('#<portlet:namespace />comprobante_tipo').val('');

		jQuery('#<portlet:namespace />comprobante_suc').val('');
		jQuery('#<portlet:namespace />comprobante_nro').val('');

		
		jQuery('#<portlet:namespace />fechaComprobanteDia').val('');
		jQuery('#<portlet:namespace />fechaComprobanteMes').val('');
		jQuery('#<portlet:namespace />fechaComprobanteAnio').val('');
		
		jQuery('#<portlet:namespace />cuit_entidad').val('');
		jQuery('#<portlet:namespace />sucursal_entidad').val('');
		jQuery('#<portlet:namespace />entidad_').val('');
		
		jQuery('#<portlet:namespace />integracion').val('');
		jQuery('#<portlet:namespace />recuperable_sur').val('');
		
		integracionReclamo();

		
		<portlet:namespace />limpiarCamposAfiliado();

	}

	
	
// rutinas de botones de la grilla de busqueda 
function borrarReclamoPrestacional(id_reclamo) {
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-reclamoprestacional'/>")){
			return false;
		}else{			
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE
				.toString()%>"/>&struts_action=/autorizaciones/editar_reclamosprestaciones_entry&id_reclamosel='+id_reclamo;						
	jQuery('#<portlet:namespace />busquedaReclamoPrestaDiv').load(url, function() {				
		<portlet:namespace />busquedaReclamosPrestacionales();
			});
		}
	}

function verReclamosPrestacionales(id_reclamo) {
	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:actionURL>';

	url = url + '&id_reclamosel=' +  id_reclamo+'&consulta=1'; 

	document. <portlet:namespace />fm.method = 'post';

	submitForm(document. <portlet:namespace />fm, url);
	
	}

function editarReclamoPrestacional(id_reclamo) {
		
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:actionURL>';

		url = url + '&id_reclamosel=' + id_reclamo +'&edicion=1';;

		document. <portlet:namespace />fm.method = 'post';

		submitForm(document. <portlet:namespace />fm, url);
			
	}
	


	function <portlet:namespace />busquedaReclamosPrestacionales(){

		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();

		var fechaOspimDia=jQuery('#<portlet:namespace />fechaOspimDia').val();
		var fechaOspimMes=jQuery('#<portlet:namespace />fechaOspimMes').val();
		var fechaOspimAnio=jQuery('#<portlet:namespace />fechaOspimAnio').val();
		
		var fechaOspimDiaHta=jQuery('#<portlet:namespace />fechaOspimDiaHta').val();
		var fechaOspimMesHta=jQuery('#<portlet:namespace />fechaOspimMesHta').val();
		var fechaOspimAnioHta=jQuery('#<portlet:namespace />fechaOspimAnioHta').val();
		
		
		var fechaCierreReclamoDia=jQuery('#<portlet:namespace />fechaCierreReclamoDia').val();
		var fechaCierreReclamoMes=jQuery('#<portlet:namespace />fechaCierreReclamoMes').val();
		var fechaCierreReclamoAnio=jQuery('#<portlet:namespace />fechaCierreReclamoAnio').val();
		
		var fechaCierreReclamoDiaHta=jQuery('#<portlet:namespace />fechaCierreReclamoDiaHta').val();
		var fechaCierreReclamoMesHta=jQuery('#<portlet:namespace />fechaCierreReclamoMesHta').val();
		var fechaCierreReclamoAnioHta=jQuery('#<portlet:namespace />fechaCierreReclamoAnioHta').val();
		

		var codPrestad=jQuery('#<portlet:namespace />cuit_prestador').val();
		var prestador=jQuery('#<portlet:namespace />nombre_prestador').val();

		var estado=jQuery('#<portlet:namespace/>estado').val();
		
		var resolucion=jQuery('#<portlet:namespace/>resolucion').val();
		var tipogestion =jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo').val();
		var tipoPedido = jQuery("#<portlet:namespace />tipopedido").val();
		var codigogestionvalor  =tipogestion.split('|')[0];
		var sectorSeleccionado =jQuery("#<portlet:namespace/>sector").val();   
		
		jQuery('#<portlet:namespace />codigogestion').val(codigogestionvalor);
		
		if (resolucion=="SELECCIONE")
		{	
			resolucion="";
		}
		
		if (tipoPedido=="SELECCIONE"){
			tipoPedido="";
		}
		if (sectorSeleccionado=="SELECCIONE"){
			sectorSeleccionado="";
		}

		var codPrestaci=jQuery('#<portlet:namespace />codPrestaci').val();
		var nroAutorizacion=jQuery('#<portlet:namespace />nroAutorizacionFiltro').val();
		var nroReclamo=jQuery('#<portlet:namespace />nroReclamoFiltro').val();	
	     
		if(trim(cuil).length != 0 && !validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			alert("Cuil inválido");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		
	    var code_medicamento=jQuery('#<portlet:namespace />troquel').val(); 	// farmacia
	    var code_prestacion = jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val();// prestaciones medicas
	    var tiponomnecladorprestacion ;
	    
		var tipoprestacion  = jQuery('#<portlet:namespace />tipoprestacion').val();


		
	    if (tipoprestacion == 'seleccione'){
		    jQuery("#<portlet:namespace />nom_seleccionado").val('0');
	    }
	    
	    if (tipoprestacion == 'clinicas'){  
	    	jQuery("#<portlet:namespace />nom_seleccionado").val('1'); 			// prestacion clinica	
	    }
	    
	    if (tipoprestacion == 'farmacia'){  
	    	jQuery("#<portlet:namespace />nom_seleccionado").val('2'); 			// prestacion clinica
	    	code_prestacion=code_medicamento; 									// asigna el codigo del medicamento
	    }	 
	    if (code_medicamento=='' && code_prestacion=='' ){
	    	code_prestacion="0";
	    }
	    tiponomnecladorprestacion= jQuery("#<portlet:namespace />nom_seleccionado").val();
	    	    
	    var tiponomencladorbuscado  =jQuery('#<portlet:namespace />tiponomenclador').val();
	    
	    var nroLote=jQuery('#<portlet:namespace />nroLote_filtro').val();

	    var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();			
		jQuery("#pagina").val(pagina_sel);
		
		//inicio Datos del Comprobante  
	    var frecuencia=jQuery("#<portlet:namespace/>frecuencia").val();			
	    var comprobante_tipo=jQuery("#<portlet:namespace/>comprobante_tipo").val();			
	
		if (frecuencia=="SELECCIONE"){	
			frecuencia="";
		}
		
		if (comprobante_tipo=="SELECCIONE"){	
			comprobante_tipo="";
		}
	
		var comprobante_suc = jQuery("#<portlet:namespace />comprobante_suc").val();
		
		var comprobante_nro = jQuery("#<portlet:namespace />comprobante_nro").val();	
		var fechaComprobanteDia=jQuery('#<portlet:namespace />fechaComprobanteDia').val();
		var fechaComprobanteMes=jQuery('#<portlet:namespace />fechaComprobanteMes').val();
		var fechaComprobanteAnio=jQuery('#<portlet:namespace />fechaComprobanteAnio').val();
		
		var cuit_entidad=jQuery('#<portlet:namespace />cuit_entidad').val();
		//fin Datos del Comprobante 
        
	    
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();	
		
		var seccional=jQuery('#<portlet:namespace />id_seccional_sec_filtro').val();
		var seccionalDesc=jQuery('#<portlet:namespace />seccional_sec_filtro').val();
		
		
		var sectorSeleccionado =jQuery("#<portlet:namespace/>sector").val();   

		var seccionalSelAfi = jQuery('#<portlet:namespace />id_seccional').val();
		var descSeccionalSelAfi = jQuery('#<portlet:namespace />seccional').val();	
		var tipoDoc = jQuery('#<portlet:namespace />tipoDoc').val();
		var nroDoc = jQuery('#<portlet:namespace />nroDoc').val();

		
		
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		//var seccionalSelAfi = jQuery('#<portlet:namespace />id_seccional').val();
		//var descSeccionalSelAfi = jQuery('#<portlet:namespace />seccional').val();	
		
		var sucursal_entidad = jQuery('#<portlet:namespace />sucursal_entidad').val();	
		var entidad_empresa = jQuery('#<portlet:namespace />entidad_').val();	
		
		
		var nombre_medicamento  = jQuery('#<portlet:namespace />nombre_medicamento').val();
		var descripcionSeguimiento_filtro  = jQuery('#<portlet:namespace />descripcionSeguimiento_filtro').val();
		
		var apellido = jQuery('#<portlet:namespace />apellido').val();
		var nombre = jQuery('#<portlet:namespace />nombre').val();
		var entidadAfi = jQuery('#<portlet:namespace />entidad').val();
		var integracion = jQuery('#<portlet:namespace />integracion').val();
		var recuperable = jQuery('#<portlet:namespace />recuperable_sur').val();

		
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_reclamos_prestacionales&entidad='+entidad+		
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&fechaOspimDia='+fechaOspimDia+'&fechaOspimMes='+fechaOspimMes+'&fechaOspimAnio='+fechaOspimAnio+
		'&fechaOspimDiaHta='+fechaOspimDiaHta+'&fechaOspimMesHta='+fechaOspimMesHta+'&fechaOspimAnioHta='+fechaOspimAnioHta+
		'&fechaCierreReclamoDia='+fechaCierreReclamoDia+'&fechaCierreReclamoMes='+fechaCierreReclamoMes+'&fechaCierreReclamoAnio='+fechaCierreReclamoAnio+
		'&fechaCierreReclamoDiaHta='+fechaCierreReclamoDiaHta+'&fechaCierreReclamoMesHta='+fechaCierreReclamoMesHta+'&fechaCierreReclamoAnioHta='+fechaCierreReclamoAnioHta+
		'&codPrest='+codPrestad+'&prestador='+encodeURI(prestador)+'&numero=0'+'&estado='+estado+'&codPrestaci='+codPrestaci+
		'&nroautorizacion='+nroAutorizacion+'&nroReclamo='+nroReclamo+'&code_prestacion='+code_prestacion+ 
		'&tipoprestacion='+tiponomnecladorprestacion+'&tiponomencladorbuscado='+tiponomencladorbuscado+'&resolucion='+resolucion+'&sectorSel='+encodeURI(sectorSeleccionado)+'&tipoPedido='+tipoPedido+'&pagina='+pagina_sel+'&codigotipogestion='+codigogestionvalor+
		'&nrolote='+nroLote+'&frecuencia='+frecuencia+'&comprobante_tipo='+comprobante_tipo+'&frecuencia='+frecuencia+'&comprobante_tipo='+comprobante_tipo+'&comprobante_suc='+comprobante_suc+
		'&comprobante_nro='+comprobante_nro+'&fechaComprobanteDia='+fechaComprobanteDia+'&fechaComprobanteMes='+fechaComprobanteMes+'&fechaComprobanteAnio='+fechaComprobanteAnio+
		'&cuit_entidad='+cuit_entidad+'&seccional='+seccional+'&nroReclamoFiltro='+nroReclamo+
		'&sectorSel='+encodeURI(sectorSeleccionado)+'&seccional='+encodeURI(seccionalSelAfi)+'&descSeccionalSelAfi='+encodeURI(descSeccionalSelAfi)+
		'&inteFiltro='+inte+'&tipoDoc='+tipoDoc+'&numero_afi='+numero_afi+'&descSeccionalSelAfi='+encodeURI(descSeccionalSelAfi)+
		'&seccionalSelAfi='+seccionalSelAfi+'&sucursal_entidad='+sucursal_entidad+'&entidadEmpresa='+encodeURI(entidad_empresa)+
		'&tipogestionFiltro='+encodeURI(tipogestion)+'&seccionalDesc='+encodeURI(seccionalDesc)+'&tipoPrestacionFiltro='+tipoprestacion+
		'&nombre_medicamento='+encodeURI(nombre_medicamento)+'&descripcionSeguimiento_filtro='+encodeURI(descripcionSeguimiento_filtro)+
		'&nroDoc='+encodeURI(nroDoc)+'&apellido='+encodeURI(apellido)+'&nombre='+encodeURI(nombre)+'&entidadAfi='+encodeURI(entidadAfi)+
		'&integracion='+integracion+'&recuperable_sur='+recuperable;    
		
        jQuery('#<portlet:namespace />busquedaReclamoPrestaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	
	function <portlet:namespace />initDateFields(){
		//capaz que seleccionar el afiliado del componente de búsqueda de afiliados		
	}
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />altaReclamoPrestacional() {		
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:renderURL>';		
		document.<portlet:namespace />fm.method = 'post';
		
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');

		
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	
	function manejarTipoPrestacion ()
	{
	var tipoSelect  =document.getElementById("<portlet:namespace />tipoprestacion");
     
	jQuery("#<portlet:namespace />busqueda_prestaciones").hide();
	jQuery("#<portlet:namespace />busqueda_farmacia").hide();
	
	   				if ( tipoSelect.selectedIndex==1 )
	  				        {  					       					      
	   							jQuery("#<portlet:namespace />busqueda_farmacia").show();
	   				        }
	   				if ( tipoSelect.selectedIndex==2 )
				            {  	       
							     jQuery("#<portlet:namespace />busqueda_prestaciones").show();
 				            }	  
	   				
	   				jQuery('#<portlet:namespace />troquel').val(""); // farmacia
	   				jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val('');
	   				jQuery('#<portlet:namespace />nombre_medicamento').val('');
	   				jQuery('#<portlet:namespace />descripcionSeguimiento_filtro').val('');

	   			    
	}


	
	
	function <portlet:namespace />buscarNomencladorAutocompletar(){
		var nombre_nomenclador=jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val();
		var codigo_nomenclador=jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val();

		 
		
		if(nombre_nomenclador.length==0 && codigo_nomenclador.length==0){
	        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
	    }else {
	    
	    	if(popupMD==null)
	    		popupMD = Liferay.Popup({title:"Búsqueda Nomenclador",modal:true,width:700,onClose: function() { popupMD = null;}});
	    
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_nomenclador';
		    url += '&descripcionnomenclador='+encodeURI(nombre_nomenclador)+'&codigonomenclador='+encodeURI(codigo_nomenclador);
			jQuery(popupMD).load(url);
	    }
		
	}



	function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
		
		jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val(codigo);
		jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val(descripcion);
		jQuery("#<portlet:namespace />nom_seleccionado").val("1"); // selecciona el tipo de nomenclador	 
		jQuery('#<portlet:namespace />tipoNomenclador').val(tipoNomenclador);
		
		
	}

	function pasarParametrosAParentNm(tipoNomenclador,codigo,descripcion) {	
		seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
	    <portlet:namespace />cerrarNm();
	}


	function <portlet:namespace />cerrarDivNm(){
		jQuery("#divSeguimientoSur").hide("slow");
	}

	function <portlet:namespace />cerrarNm(){
		<portlet:namespace />cerrarDivNm();
		if(popupMD){
			Liferay.Popup.close(popupMD);
		}
	}
	

	function <portlet:namespace />limpiarNomencladorAutocompletar(){	
		jQuery("#<portlet:namespace />descripcionSeguimiento_filtro").val('');
		jQuery("#<portlet:namespace />codigoSeguimiento_filtro").val('');
	}
	
	
	
	jQuery('#<portlet:namespace />exportar-busqueda').click(function exportarBusqueda(){
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var fechaOspimDia=jQuery('#<portlet:namespace />fechaOspimDia').val();
		var fechaOspimMes=jQuery('#<portlet:namespace />fechaOspimMes').val();
		var fechaOspimAnio=jQuery('#<portlet:namespace />fechaOspimAnio').val();		
		var fechaOspimDiaHta=jQuery('#<portlet:namespace />fechaOspimDiaHta').val();
		var fechaOspimMesHta=jQuery('#<portlet:namespace />fechaOspimMesHta').val();
		var fechaOspimAnioHta=jQuery('#<portlet:namespace />fechaOspimAnioHta').val();
		var fechaCierreReclamoDia=jQuery('#<portlet:namespace />fechaCierreReclamoDia').val();
		var fechaCierreReclamoMes=jQuery('#<portlet:namespace />fechaCierreReclamoMes').val();
		var fechaCierreReclamoAnio=jQuery('#<portlet:namespace />fechaCierreReclamoAnio').val();		
		var fechaCierreReclamoDiaHta=jQuery('#<portlet:namespace />fechaCierreReclamoDiaHta').val();
		var fechaCierreReclamoMesHta=jQuery('#<portlet:namespace />fechaCierreReclamoMesHta').val();
		var fechaCierreReclamoAnioHta=jQuery('#<portlet:namespace />fechaCierreReclamoAnioHta').val();
		var codPrestad=jQuery('#<portlet:namespace />cuit_prestador').val();
		var prestador=jQuery('#<portlet:namespace />nombre_prestador').val();
		var estado=jQuery('#<portlet:namespace/>estado').val();
		var resolucion=jQuery('#<portlet:namespace/>resolucion').val();
		var tipogestion =jQuery('#<portlet:namespace/>tipo_gestion_cierre_reclamo').val();
		var codigogestionvalor  =tipogestion.split('|')[0];
		jQuery('#<portlet:namespace />codigogestion').val(codigogestionvalor);		
		if (resolucion=="SELECCIONE")
		{	
			resolucion="";
		}		
		var codPrestaci=jQuery('#<portlet:namespace />codPrestaci').val();
		var nroAutorizacion=jQuery('#<portlet:namespace />nroAutorizacionFiltro').val();
		var nroReclamo=jQuery('#<portlet:namespace />nroReclamoFiltro').val();	
	     
		if(trim(cuil).length != 0 && !validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			alert("Cuil inválido");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		
	    var code_medicamento=jQuery('#<portlet:namespace />troquel').val(); 	// farmacia
	    var code_prestacion = jQuery('#<portlet:namespace />codigoSeguimiento_filtro').val();// prestaciones medicas
	    var tiponomnecladorprestacion ;
			    
	    jQuery("#<portlet:namespace />nom_seleccionado").val('0')
	    
   		var tipoprestacion  = jQuery('#<portlet:namespace />tipoprestacion').val();

	    
	    if (tipoprestacion =='clinicas'){  
	    	jQuery("#<portlet:namespace />nom_seleccionado").val('1') 			// prestacion clinica	
	    }
	    
	    if (tipoprestacion =='farmacia'){  
	    	jQuery("#<portlet:namespace />nom_seleccionado").val('2') 			// prestacion clinica
	    	code_prestacion=code_medicamento 									// asigna el codigo del medicamento
	    }	 
	    if (tipoprestacion== 'seleccione'){
	    	code_prestacion="0";
	    }
	    tiponomnecladorprestacion= jQuery("#<portlet:namespace />nom_seleccionado").val();
	    
	    var tiponomencladorbuscado  =jQuery('#<portlet:namespace />tiponomenclador').val();
	    		
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var sectorSeleccionado =jQuery("#<portlet:namespace/>sector").val();
		var tipoPedido = jQuery("#<portlet:namespace />tipopedido").val();
	
		if (tipoPedido=="SELECCIONE"){
			tipoPedido="";
		}
		if (sectorSeleccionado=="SELECCIONE"){
			sectorSeleccionado="";
		}
		
		
		//inicio Datos del Comprobante  
	    var frecuencia=jQuery("#<portlet:namespace/>frecuencia").val();			
	    var comprobante_tipo=jQuery("#<portlet:namespace/>comprobante_tipo").val();			
	
	
		
		if (comprobante_tipo=="SELECCIONE"){	
			comprobante_tipo="";
		}
	
		var comprobante_suc = jQuery("#<portlet:namespace />comprobante_suc").val();
		
		var comprobante_nro = jQuery("#<portlet:namespace />comprobante_nro").val();	
		var fechaComprobanteDia=jQuery('#<portlet:namespace />fechaComprobanteDia').val();
		var fechaComprobanteMes=jQuery('#<portlet:namespace />fechaComprobanteMes').val();
		var fechaComprobanteAnio=jQuery('#<portlet:namespace />fechaComprobanteAnio').val();
		
		var cuit_entidad=jQuery('#<portlet:namespace />cuit_entidad').val();
		//fin Datos del Comprobante 
		var seccional=jQuery('#<portlet:namespace />id_seccional_sec_filtro').val();

		
		var nroLote=jQuery('#<portlet:namespace/>nroLote_filtro').val();
		
		var integracion = jQuery('#<portlet:namespace />integracion').val();
		var recuperable = jQuery('#<portlet:namespace />recuperable_sur').val();

		
		window.location.href ='/xlsservlet/?reporte=REPORTE_RESULT_BUSQUEDA_RECLAMOS_PRESTACIONALES'
		+'&entidad='+entidad+
		'&cuil_titular='+cuil+'&inte='+inte+'&numero_afi='+numero_afi+
		'&fechaOspimDia='+fechaOspimDia+'&fechaOspimMes='+fechaOspimMes+'&fechaOspimAnio='+fechaOspimAnio+
		'&fechaOspimDiaHta='+fechaOspimDiaHta+'&fechaOspimMesHta='+fechaOspimMesHta+'&fechaOspimAnioHta='+fechaOspimAnioHta+
		'&fechaCierreReclamoDia='+fechaCierreReclamoDia+'&fechaCierreReclamoMes='+fechaCierreReclamoMes+'&fechaCierreReclamoAnio='+fechaCierreReclamoAnio+
		'&fechaCierreReclamoDiaHta='+fechaCierreReclamoDiaHta+'&fechaCierreReclamoMesHta='+fechaCierreReclamoMesHta+'&fechaCierreReclamoAnioHta='+fechaCierreReclamoAnioHta+
		'&codPrest='+codPrestad+'&prestador='+encodeURI(prestador)+'&numero=0'+'&estado='+estado+'&codPrestaci='+codPrestaci+
		'&nroautorizacion='+nroAutorizacion+'&nroReclamo='+nroReclamo+'&code_prestacion='+code_prestacion+
		'&tipoprestacion='+tiponomnecladorprestacion+'&tiponomencladorbuscado='+tiponomencladorbuscado+
		'&resolucion='+resolucion+'&codigotipogestion='+codigogestionvalor+'&sectorSeleccionado='+sectorSeleccionado+'&tipoPedido='+tipoPedido+
		'&nrolote='+nroLote+'&frecuencia='+frecuencia+'&comprobante_tipo='+comprobante_tipo+'&frecuencia='+frecuencia+'&comprobante_tipo='+comprobante_tipo+'&comprobante_suc='+comprobante_suc+
		'&comprobante_nro='+comprobante_nro+'&fechaComprobanteDia='+fechaComprobanteDia+'&fechaComprobanteMes='+fechaComprobanteMes+
		'&fechaComprobanteAnio='+fechaComprobanteAnio+'&cuit_entidad='+cuit_entidad+'&seccional='+seccional+
		'&integracion='+integracion+'&recuperable_sur='+recuperable;       
	
	});

	
	
	function <portlet:namespace />cerrarLote() {
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:actionURL>';
		url = url + '&cmd=cerrar'; 
		var confirmar = false;
		confirmar=confirm ('Esta seguro de cerrar el Lote?');
		if(confirmar){
		   document. <portlet:namespace />fm.method = 'post';
		   submitForm(document. <portlet:namespace />fm, url);
		}
	}
	
	function imprimirLote(){
		var idLote=jQuery("#<portlet:namespace />nroLote_filtro").val();
		if(idLote!=null && ""!=idLote){
			var nroLote=jQuery('#<portlet:namespace/>nroLote_filtro').val();
			window.location.href ="/pdfservlet/?accion=reclamoprestacionallote&nrolote="+idLote;
	    } else {
	       alert("Debe Ingresar un Nro. de Lote");	
	    }  
	}
	
	function soloNumeros(e) 
	{ 
	var key = window.Event ? e.which : e.keyCode 
	return ((key >= 48 && key <= 57) || (key==8)) 
	}
	

	function integracionReclamo(){
		try {	
			 if ('EXCEPCION' ==  jQuery("#<portlet:namespace />tipopedido").val()){
				 jQuery('#integracion_label').show();
				 jQuery('#<portlet:namespace />integracion').show();
				 jQuery('#integracion_div').show();
			 }else {
				 jQuery('#integracion_label').hide();
				 jQuery('#<portlet:namespace />integracion').hide();
				 jQuery('#integracion_div').hide();
				 jQuery('#<portlet:namespace />integracion').val("0");


			 }	
		}
		catch (err) {
			alert('error integracion ');
		}
	}
	


	function traerDescripcion() {
		var idIntegracion = jQuery('#<portlet:namespace/>integracion').val();
		var descripcionLarga;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/getIntegracionDetalle&id_integracion='+idIntegracion;
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);						
				descripcionLarga = obj.DescripcionLarga;	
				jQuery("#integracion_desc").attr({alt: descripcionLarga,title: descripcionLarga});

			}
		});	
	}
	
	
jQuery('#<portlet:namespace />exportar-imagenes').click(function exportarImagenes(){
		
		var rps=jQuery('#<portlet:namespace />rps').val();
		 
		if(trim(rps).length == 0 ){
			alert("Debe ingresar lista de Reclamos separados por punto y coma");
			jQuery('#<portlet:namespace />in').focus();
			return false;
		}
		
		window.location.href ='/txtservlet/?reporte=RECLAMOS_EXPORTAR_IMAGENES'
			+'&in='+rps ;	
	});

	
	
</script>

<style>
  .tabla-afiliado{ 
  	width: 100%;
    box-sizing: border-box;
    padding: 10px;
    margin: 0 auto 10px auto;
    }
</style>