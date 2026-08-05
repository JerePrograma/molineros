<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%

boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)||PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA_UOMA);

Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

boolean opcionIngreso = false;
if (recibo != null && recibo.getAfiliado() !=null ){
	opcionIngreso = true;
}

List<Concepto> conceptos = (List<Concepto>)request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_INGRESO);

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

if(renderResponse.getNamespace().equals("_EST_1_")){
	portlet_name = "estudio_isidro";
}

Calendar periodo = CalendarFactoryUtil.getCalendar(); 		
periodo.setTime(new Date());

int id_seccional= recibo!= null && recibo.getEmpresa() != null && recibo.getEmpresa().getId_seccional()>0 ? recibo.getEmpresa().getId_seccional() 
	: recibo!= null && null!= recibo.getSeccional() && recibo.getSeccional().getId_seccional()>0?recibo.getSeccional().getId_seccional(): 0;

%>
<script type="text/javascript">	
	function cambiarAEntidad(){
		
		jQuery('#<portlet:namespace />divBusqEntidad').show();
		jQuery('#<portlet:namespace />divBusqAfiliado').hide();
	}
	function cambiarAAfiliado(){
		
		jQuery('#<portlet:namespace />divBusqEntidad').hide();
		jQuery('#<portlet:namespace />divBusqAfiliado').show();
	}
	function debeMostrarDetalleAportes(idconcepto){		
		<%if(portlet_name.equals("farmacia")){%>
			//HACER UNA MARCA EN EL BEAN PARA EVITAR ESTO...		
			if(idconcepto==224){
				jQuery('#<portlet:namespace />divDetalleAportes').show();
			}
		<%}else if(portlet_name.equals("uoma")){%>		
		    //HACER UNA MARCA EN EL BEAN PARA EVITAR ESTO...
			if(idconcepto==891 || idconcepto==962 || idconcepto==964 || idconcepto==972 || idconcepto==976 || idconcepto==894 || idconcepto==892|| idconcepto==988 || idconcepto==82){
				document.getElementById("<portlet:namespace />divDetalleAportes").style.display = 'inline-block';
			}else{
				jQuery('#<portlet:namespace />divDetalleAportes').hide();
			}
		<%}else{%>
			jQuery('#<portlet:namespace />divDetalleAportes').hide();
		<%}%>			
			
	}
</script>

<liferay-ui:error exception="<%= ReciboConceptoSinImporteException.class %>" message="recibo-concepto-sin-importe" />

<fieldset class="block-labels">
<legend><liferay-ui:message	key="conceptos" /></legend>
<input type="hidden" id="ids_actas" value="0"/>
<input type="hidden" id="ids_convenios" value="0"/>
<input type="hidden" id="total_cheques_rechazados" value="0"/>
<input type="hidden" id="total_cheques_no_depositados" value="0"/>
<input type="hidden" id="total_otros" value="0"/>
<input type="hidden" id="total_prestamos" value="0"/>
<input type="hidden" id="<portlet:namespace />id_seccional_ent" name="<portlet:namespace />id_seccional_ent" value="0"/>

<input type="hidden" id="<portlet:namespace />boleta_capital" value="0"/>
<input type="hidden" id="<portlet:namespace />boleta_interes" value="0"/>
<input type="hidden" id="<portlet:namespace />boleta_ajuste" value="0"/>

<table class="lfr-table" width="100%">	
	
	<% if(!portlet_name.equals("tesoreria")){ %>
	<tr>
		<td colspan="4" align="left">Elija una opci&oacute;n &nbsp;
			<input type="radio" name="<portlet:namespace />entidadIngreso"  value="Entidad" <% if(!opcionIngreso){ %> checked="checked" <%} %>  onselect="cambiarAEntidad()" onchange="cambiarAEntidad()">Empresa &nbsp;
			<input type="radio" name="<portlet:namespace />entidadIngreso"  value="Afiliado"<% if(opcionIngreso){ %> checked="checked" <%} %>  onselect="cambiarAAfiliado()" onchange="cambiarAAfiliado()">Afiliado &nbsp;
		</td>
		
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<% } %>
	<tr>
		<td colspan="4"> 
		
		<div id="<portlet:namespace />divBusqEntidad" <% if(!opcionIngreso){%> style="display: inline;" <% } else { %> style="display: none;" <% } %> > 
			<fieldset class="block-labels"><legend><liferay-ui:message
				key="datos-empresa" /></legend>
				<liferay-util:include page="/html/portlet/farmacia/busqueda_padron_entidades.jsp">
					<liferay-util:param name="suf_entidad" value="_otr_concep"/>
		  			<liferay-util:param name="cuit" value='<%= recibo!=null && recibo.getEmpresa() != null ? recibo.getEmpresa().getCuit() :	"" %>'/>
		  			<liferay-util:param name="sucu" value='<%=recibo!=null && recibo.getEmpresa() != null ? recibo.getEmpresa().getSucursal() :"" %>'/>
		  			<liferay-util:param name="razon" value='<%=recibo!=null && recibo.getEmpresa() != null ? recibo.getEmpresa().getRazon_soc() :"" %>'/>
		  			<liferay-util:param name="id_seccional_ent" value='<%=String.valueOf(id_seccional) %>'/> 
		  			<liferay-util:param name="esEditable" value='<%=String.valueOf(showABMButtons) %>'/>
			  		<liferay-util:param name="portlet_name" value='tesoreria'/>
				</liferay-util:include>
			</fieldset> 
		</div>		
		</td>
	</tr>
	
	<tr>
		<td colspan="4">
			<div id="<portlet:namespace />divBusqAfiliado" <% if(opcionIngreso){%> style="display: inline;" <% } else { %> style="display: none;" <% } %> >
			<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
			<%if(portlet_name.equals("uoma")){%>
				<liferay-util:include page='/html/portlet/uoma/busqueda_afiliado.jsp'>
					<liferay-util:param name="cuil" value='<%= recibo!=null && recibo.getAfiliado() != null ? recibo.getAfiliado().getCuil_titular() :	"" %>'/>
					<liferay-util:param name="inte" value='<%= recibo!=null && recibo.getAfiliado() != null ? String.valueOf(recibo.getAfiliado().getInte()) :	new String("") %>'/>
					<liferay-util:param name="apellido" value='<%= recibo!=null && recibo.getAfiliado() != null ? recibo.getAfiliado().getApellido() :	"" %>'/>
					<liferay-util:param name="nombre" value='<%= recibo!=null && recibo.getAfiliado() != null ? recibo.getAfiliado().getNombre() :	"" %>'/>					
					<liferay-util:param value="<%=String.valueOf(showABMButtons) %>" name="edit_mode" />
				</liferay-util:include>
			<%}else{%>
				<liferay-util:include page='/html/portlet/farmacia/busqueda_afiliado.jsp'>
					<liferay-util:param name="cuil" value='<%= recibo!=null && recibo.getAfiliado() != null ? recibo.getAfiliado().getCuil_titular() :	"" %>'/>
					<liferay-util:param name="inte" value='<%= recibo!=null && recibo.getAfiliado() != null ? String.valueOf(recibo.getAfiliado().getInte()) :	new String("") %>'/>
					<liferay-util:param name="apellido" value='<%= recibo!=null && recibo.getAfiliado() != null ? recibo.getAfiliado().getApellido() :	"" %>'/>
					<liferay-util:param name="nombre" value='<%= recibo!=null && recibo.getAfiliado() != null ? recibo.getAfiliado().getNombre() :	"" %>'/>
					<liferay-util:param name="entiAfi" value='<%= String.valueOf(WebKeysGlobal.ENTIDAD_AMTIMA)  %>'/>
					<liferay-util:param name="nroAfi"  value='<%= recibo!=null && recibo.getAfiliado() != null ? String.valueOf(recibo.getAfiliado().getId_amtima()) :	"" %>'/>					
					<liferay-util:param value="<%=String.valueOf(showABMButtons) %>" name="edit_mode" />
				</liferay-util:include>
			<%}%>
			
			
			</fieldset>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>

	
	<tr>
		<td valign="top" colspan="4" width="50%">
			Actas&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divActas'));" />
			<div id="<portlet:namespace />divActas">
				<%if (showABMButtons){%>
				<input type="button"  value="<liferay-ui:message key="buscar-actas"/>" onClick="<portlet:namespace />buscarActas();" ><br/>
				<%} %>
				<div align="center" id="<portlet:namespace />agregandoActas">
				<table style="align: center;">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
				<div align="center" id="<portlet:namespace />actas">					
						<liferay-util:include page="/html/portlet/tesoreria/recibos/recibo_actas.jsp">
							<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
						</liferay-util:include>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td valign="top" colspan="4" width="50%">
		Convenios&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divConvenios'));" />
		  <div id="<portlet:namespace />divConvenios">
			<%if (showABMButtons){%>
			<input type="button"  value="<liferay-ui:message key="buscar-convenios" />" onClick="<portlet:namespace />buscarConvenios();" ><br/>
			<%} %>
			<div align="center" id="<portlet:namespace />agregandoConvenios">
				<table style="align: center;">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
				<div align="center" id="<portlet:namespace />convenios">
					<liferay-util:include page="/html/portlet/tesoreria/recibos/recibo_convenios.jsp">
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					</liferay-util:include>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td valign="top" colspan="4">
		Cheques No Depositados&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divChqNoDepo'));" />
		  <div id="<portlet:namespace />divChqNoDepo">
			<%if (showABMButtons){%>
				<input type="button"  value="<liferay-ui:message key="buscar-cheques-a-sustituir" />" onClick="<portlet:namespace />buscarChequeASustituir();" ><br/>
			<%} %>
				<div align="center" id="<portlet:namespace />agregandoChequeSust">
				<table style="align: center;">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
				<div align="center" id="<portlet:namespace />cheques_a_sust"> 
				<liferay-util:include page="/html/portlet/tesoreria/recibos/recibo_cheques_a_sustituir.jsp"/>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td valign="top" colspan="4">
			Cheques Rechazados&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divChqRhz'));" />
		  <div id="<portlet:namespace />divChqRhz">
			<%if (showABMButtons){%>
				<input type="button"  value="<liferay-ui:message key="buscar-cheques-rechazados" />" onClick="<portlet:namespace />buscarChequeRechazado();" ><br/>
			<%} %>
				<div align="center" id="<portlet:namespace />agregandoChequeRech">
				<table style="align: center;">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
				<div align="center" id="<portlet:namespace />cheques_rechazados">
				<liferay-util:include page="/html/portlet/tesoreria/recibos/recibo_cheques_rechazados.jsp"/>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			&nbsp;
		</td>
	</tr>
	
	<tr>
		<td colspan="4">
		  Otros&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divOtros'));" />
		  	<div id="<portlet:namespace />divOtros">
				<table>	
				<tr>
				<td>
				<div id="<portlet:namespace />divOtrosConc">
				</div>			
		  		</td>
		  		<td>
				<liferay-ui:message key="importe"/>:&nbsp;
				</td>
				<td>
				<input type="text" id="<portlet:namespace />otro_importe" name="<portlet:namespace />otro_importe" size="10" maxlength="12" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/> &nbsp;
                </td>				
                <td>
				<input type="button" onclick="javascript:agregarOtroConcepto();" value="Agregar"/>
				</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
				<td colspan="12">
				<div id="<portlet:namespace />divDetalleAportes" style="display: none;">
				  <table class="lfr-table">
				    <tr>
				       <td>
					      <label>Nro Boleta:</label>
					   </td>
					   <td>   
					      <input type="text" name="<portlet:namespace />nroBoleta" id="<portlet:namespace />nroBoleta" value="" maxlength="20" size="7" onkeydown="allowOnlyDigits(event)"
					      onchange="javascript:traeDatosEmpleadores();"/> &nbsp;
					   </td> 
					   <td>
				          <input type="button" onclick="javascript:verEmpleadores();" value="Ver Empleadores"/> 
				       </td>
				       
				       <td>Capital:</td>
				       <td>
				        <input type="text" id="<portlet:namespace />boleta_capital_view" name="<portlet:namespace />boleta_capital_view"  value="0" readonly="readonly"/>
                       </td>
                       
                       <td>Ajustes:</td>
                       <td>  
                         <input type="text" id="<portlet:namespace />boleta_ajuste_view" name="<portlet:namespace />boleta_ajuste_view" value="0" readonly="readonly"/>
                       </td>
                       
                       <td>Interés:</td>
                       <td>				        
                         <input type="text" id="<portlet:namespace />boleta_interes_view" name="<portlet:namespace />boleta_interes_view" value="0"  readonly="readonly"/>
                       </td>
                     </tr>
                     <tr><td>&nbsp;</td></tr>
                     <tr>                           
				      <td>
						<liferay-ui:message	key="cantidad-empleados" />:&nbsp;<input type="text" name="<portlet:namespace />cantidadEmpleados" id="<portlet:namespace />cantidadEmpleados" value="1" maxlength="6" size="5" onkeydown="allowOnlyDigits(event)"/> &nbsp; 
					  </td>
					  <td>	
						<liferay-ui:message	key="remuneracionTotal" />:&nbsp;<input type="text" name="<portlet:namespace />remuneracionTotal" id="<portlet:namespace />remuneracionTotal"  value="0" size="10" maxlength="12" onkeydown="allowOnlyDigitsAndDecimals(event)"/> &nbsp;
					  </td>
					  <td>
						<liferay-ui:message	key="periodo" />:&nbsp; 
					  </td>
					  <td>	
						<liferay-ui:input-date dayParam="periodoDia"
							dayNullable="<%= true %>" 
							dayValue=""
							monthAndYearParam="periodoMesAnio"
							monthValue="<%= periodo.get(Calendar.MONTH) %>"
							monthAndYearNullable="<%= false %>"
							yearValue="<%= periodo.get(Calendar.YEAR) %>"
							yearRangeStart="<%= periodo.get(Calendar.YEAR) - 5 %>"
							yearRangeEnd="<%= periodo.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= periodo.getFirstDayOfWeek() - 1 %>"
							disabled="<%= !showABMButtons%>" /> &nbsp;&nbsp;
					   </td>
					   
					 </tr>  
					 
					 <tr><td>&nbsp;</td></tr>		
					</table>
					
				</div>				
						
				
				</td>
				</tr>				
				</table>
				
				<div align="center" id="<portlet:namespace />agregandoOtros">
					<table style="align: center;">
						<tr>
							<td><liferay-ui:message key='buscando' /></td>
							<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>
				</div>
				<div align="center" id="<portlet:namespace />otros_result">
					<liferay-util:include page="/html/portlet/tesoreria/recibos/otros_search_result.jsp">
						<liferay-util:param name="portlet_name" value="<%=portlet_name%>"/>
					</liferay-util:include>
				</div>
			</div>	
		</td>
	</tr>
	
	<tr>
		<td colspan="4">&nbsp;
		</td>
	</tr>
	<tr>
	   <%if(portlet_name.equals("farmacia")){%>
	
		<td valign="top" colspan="4" width="50%">
		     Beneficio AMTIMA&nbsp;<img src="/html/themes/classic/images/application/handle_se.png" title="Mostrar/Ocultar" onclick="cambiarEstado(jQuery('#<portlet:namespace />divPrestamos'));" />
		     <div id="<portlet:namespace />divPrestamos">
		       <table class="lfr-table">
				    <tr>
				       <td>
					      <label>Nro Préstamo:</label>
					   </td>
					   
					   <td>   
					      <input type="text" name="<portlet:namespace />prestamo_nro" id="<portlet:namespace />prestamo_nro" value="" maxlength="20" size="7" onkeydown="allowOnlyDigits(event)"
					      onblur="traerDatosPrestamo()"/> &nbsp;
					   </td>
					   
					   <td>Total:</td>
				       <td>
				        <input type="text" id="<portlet:namespace />prestamo_total" name="<portlet:namespace />prestamo_total"  value="0" readonly="readonly"/>
                       </td>
					    
					   <td>Pago:</td>
				       <td>
				        <input type="text" id="<portlet:namespace />prestamo_importe" name="<portlet:namespace />prestamo_importe"  value="0" />
                       </td>
                       <td>Fecha Pago:</td>
                       <td>	
						<liferay-ui:input-date
				           dayParam="fechaPrestamoDia"
				           dayValue="<%= periodo.get(Calendar.DATE) %>" 
				           dayNullable="<%= true %>" 
				           monthParam="fechaPrestamoMes"
				           monthValue="<%= periodo.get(Calendar.MONTH) %>"
				           monthNullable="<%= true %>"				
				           yearParam="fechaPrestamoAnio"
				           yearValue="<%= periodo.get(Calendar.YEAR) %>"
  				           yearRangeStart="<%= periodo.get(Calendar.YEAR) - 5 %>"
				           yearRangeEnd="<%= periodo.get(Calendar.YEAR) + 5%>"
				           yearNullable="<%= true %>"
				           firstDayOfWeek="<%= periodo.getFirstDayOfWeek() - 1 %>"
				           disabled="<%= false%>" />
				           
					   </td>
                       <td>
				          <input type="button" onclick="javascript:verPrestamos();" value="Ver Prestamos"/> 
				       </td>
				       
				       <td>
				          <input type="button" onclick="javascript:verPagos();" value="Ver Pagos"/> 
				       </td>
				       
				       <td>
				           <input type="button" onclick="javascript:agregarPrestamo();" value="Agregar"/>
				       </td>
                     </tr>
                </table>       
		     
   	             <div align="center" id="<portlet:namespace />agregandoPrestamos">
				               <table style="align: center;">
					                <tr>
						               <td><liferay-ui:message key='buscando' /></td>
						               <td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						               </td>
					                </tr>
				               </table>
		         </div>
		         <div align="center" id="<portlet:namespace />prestamos_result">					
						     <liferay-util:include page="/html/portlet/tesoreria/recibos/recibo_prestamos_search_result.jsp">
							 <liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
						     </liferay-util:include>
		         </div>
			   </div>
		    </td>
	<%}%>	    
	</tr>
	<tr>
		<td colspan="4">&nbsp;
		</td>
	</tr>
</table>

</fieldset>
<script type="text/javascript">

     

	function <portlet:namespace />buscarActas(){		
		jQuery('#<portlet:namespace />agregandoActas').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_actas'
			+'&cuit=' +trim(document.getElementById("<portlet:namespace />cuit_entidad").value);

		var inputs = document.getElementsByTagName("input");
		for (var i=0; i<inputs.length; i++){
			if (inputs[i].name.indexOf("acta_") == 0){
				url += '&' + inputs[i].name + '=' + document.getElementById(inputs[i].name).value;
			}
		}		
		url += '&rnd=' + Math.floor(Math.random()*100);		
		jQuery('#<portlet:namespace />actas').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoActas').hide();
					sumarConceptos();
		}); 
		//alert('antes de formas 2');
<%-- 		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/actualizar_formas_ingreso'; --%>
				
		//jQuery('#<portlet:namespace />formas_ingreso').load(url,function() {alert('vuelve de recargar formas')});
	}
	
	function borraActa(id){
		jQuery('#<portlet:namespace />agregandoActas').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_actas'
			+'&borrar=borrar'
			+'&acta_id=' + id;

		var inputs = document.getElementsByTagName("input");
		for (var i=0; i<inputs.length; i++){
			if (inputs[i].name.indexOf("acta_") == 0){
				url += '&' + inputs[i].name + '=' + document.getElementById(inputs[i].name).value;
			}
		}
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />actas').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoActas').hide();
					sumarConceptos();
		}); 
	}
	
	function <portlet:namespace />buscarConvenios(){
		jQuery('#<portlet:namespace />agregandoConvenios').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_convenios'
			+'&cuit=' +trim(document.getElementById("<portlet:namespace />cuit_entidad").value);

		var inputs = document.getElementsByTagName("input");
		for (var i=0; i<inputs.length; i++){
			if (inputs[i].name.indexOf("convenio_") == 0){
				url += '&' + inputs[i].name + '=' + document.getElementById(inputs[i].name).value;
			}
		}
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />convenios').load(url, function() {
			recargarIngresos();
			jQuery('#<portlet:namespace />agregandoConvenios').hide();
			sumarConceptos();
			});
	}
	
	function borraConvenio(id){
		jQuery('#<portlet:namespace />agregandoConvenios').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_convenios'
			+'&borrar=borrar'
			+'&convenio_id=' + id;

		var inputs = document.getElementsByTagName("input");
		for (var i=0; i<inputs.length; i++){
			if (inputs[i].name.indexOf("convenio_") == 0){
				url += '&' + inputs[i].name + '=' + document.getElementById(inputs[i].name).value;
			}
		}
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />convenios').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoConvenios').hide();
					sumarConceptos();
		}); 
	}
	
	function <portlet:namespace />buscarChequeASustituir(){
		jQuery('#<portlet:namespace />agregandoChequeSust').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_cheques_a_sustituir'
			+'&cuit=' +trim(document.getElementById("<portlet:namespace />cuit_entidad").value);
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_a_sust').load(url, function() {
			recargarIngresos();
			jQuery('#<portlet:namespace />agregandoChequeSust').hide();
			sumarConceptos();
			});
	}
	
	function borraChequeADepositar(cheque_nro, id_banco, id_cta_bcria, cuit){
		jQuery('#<portlet:namespace />agregandoChequeSust').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_cheques_a_sustituir'
			+'&borrar=borrar'
			+'&cheque_nro=' + cheque_nro
			+'&id_banco=' + id_banco
			+'&id_cta_bcria=' + id_cta_bcria
			+'&cuit_emisor=' + cuit;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_a_sust').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoChequeSust').hide();
					sumarConceptos();
		}); 
	}
	
	function <portlet:namespace />buscarChequeRechazado(){
		jQuery('#<portlet:namespace />agregandoChequeRech').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_cheques_rechazados'
			+'&cuit=' +trim(document.getElementById("<portlet:namespace />cuit_entidad").value);
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_rechazados').load(url, function() {
			recargarIngresos();
			jQuery('#<portlet:namespace />agregandoChequeRech').hide();
			sumarConceptos();
			});
	}
	
	function borraChequeChequeRechazado(cheque_nro, id_banco, id_cta_bcria, cuit){
		jQuery('#<portlet:namespace />agregandoChequeRech').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_cheques_rechazados'
			+'&borrar=borrar'
			+'&cheque_nro=' + cheque_nro
			+'&id_banco=' + id_banco
			+'&id_cta_bcria=' + id_cta_bcria
			+'&cuit_emisor=' + cuit;
		
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_rechazados').load(url, function() {
					recargarIngresos();
					jQuery('#<portlet:namespace />agregandoChequeRech').hide();
					sumarConceptos();
		}); 
	}

	function sumarActa(cheques, limite, actaId, nro){		
		var adicional = parseFloat(document.getElementById("acta_" + actaId).value);		
		document.getElementById("total_acta_"+ actaId).value = Math.round((Math.round(cheques *100) / 100 
				+ Math.round(adicional *100) / 100 )*100)/100;		
		if ( parseFloat(limite) < parseFloat(document.getElementById("acta_" + actaId).value) ){
			document.getElementById("acta_" + actaId).value= "0";
			document.getElementById("total_acta_" + actaId).value= cheques;
			alert("El valor total a ingresar para el acta " + nro + " no puede superar: " + limite + " (deuda - ingreso por cheques)");
		} 
		sumarConceptos();
	}

	function sumarConvenio(cheques, limite, convId, nro){
		var adicional = parseFloat(document.getElementById("convenio_" + convId).value);
		document.getElementById("total_convenio_"+ convId).value = Math.round((Math.round(cheques *100) / 100 
				+ Math.round(adicional *100) / 100 )*100)/100;
		if ( parseFloat(limite) < parseFloat(document.getElementById("convenio_" + convId).value) ){
			document.getElementById("convenio_" + convId).value= "0";
			document.getElementById("total_convenio_" + convId).value= cheques;
			alert("El valor total a ingresar para el convenio " + nro + " no puede superar: " + limite + " (deuda - ingreso por cheques)");
		} 
		sumarConceptos();
	}

	function agregarOtroConcepto(){		
		if (trim(document.getElementById("<portlet:namespace />otro_concepto").value) == "") {
			alert("Debe seleccionar un concepto");
			return false;
		}

		var importe = trim(document.getElementById("<portlet:namespace />otro_importe").value);
		if (importe == "") {
			alert("Debe ingresar un importe");
			document.getElementById("<portlet:namespace />otro_importe").focus();
			return false;
		}

		
		//Agregar Ajax Validacion boleta portal Empleadores	
		var idconcepto =trim(document.getElementById("<portlet:namespace />otro_concepto").value);
		var nroBoleta=jQuery('#<portlet:namespace />nroBoleta').val();
		var nroSecuenciaDDJJ=0;
		if(idconcepto==224  ||
				idconcepto==891 || idconcepto==962 || idconcepto==964 || 
				idconcepto==972 || idconcepto==976 || idconcepto==894 || 
				idconcepto==892|| idconcepto==988 || idconcepto==82){
			
			var boletaInexistente=false;
			var boletaPagada = false;
			var periodoCorrecto=true;
			
			if(nroBoleta==null || nroBoleta==""){
				alert("Debe Llenar el Nro de Boleta.");
				   return false;
			}else{
				
			   var cuit=document.getElementById("<portlet:namespace />cuit_entidad").value;
			   var sucursal=document.getElementById("<portlet:namespace />sucursal_entidad").value;
			   var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/trae_boleta_portal_empleadores';
			   url1 +='&cuit='+cuit;
			   url1 +='&sucursal=' + sucursal;
			   url1	+='&concepto_id=' + trim(document.getElementById("<portlet:namespace />otro_concepto").value)
			   url1	+='&importe=' + importe
			   url1 +='&nroBoleta=' + nroBoleta
			   url1 +='&periodo=' + trim(document.getElementById("<portlet:namespace />periodoMesAnio").value);    
			   url1+= '&rnd=' + Math.floor(Math.random()*100);
	           jQuery.ajax({   
		        url: url1,
		        async:false,
		        success: function(data){
			    var obj = jQuery.parseJSON(data);
			    boletaInexistente = (obj.inexistente === 'true');
			    boletaPagada = (obj.pagado === 'true');
			    nroSecuenciaDDJJ=obj.secuenciaddjj;
			    periodoCorrecto=(obj.periodocorrecto==='true');
			    
			    jQuery("#<portlet:namespace />boleta_capital").val(Math.round(obj.capital)/100 );
		        jQuery("#<portlet:namespace />boleta_interes").val(Math.round(obj.interes)/100);
		        jQuery("#<portlet:namespace />boleta_ajuste").val(Math.round(obj.ajustes)/100);
			    
		       }});
			}
			
			if(boletaInexistente){
			   alert("Boleta Inexistente en Portal Empleadores");
			   return false;
			}
			  
			if(boletaPagada){
			   alert("Boleta ya pagada en Portal Empleadores");
			   return false;
			}
			
			if(!periodoCorrecto){
			  alert("Período seleccionado no corresponde con el de la Boleta");
			  return false;
			}
		}
		
//Fin Ajax		
		
		jQuery('#<portlet:namespace />agregandoOtros').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_otros_conceptos'
			+'&concepto_id=' + trim(document.getElementById("<portlet:namespace />otro_concepto").value)
			+'&importe=' + importe
			+'&impoRemunTotal=' + trim(document.getElementById("<portlet:namespace />remuneracionTotal").value)
			+'&cantEmpleados=' + trim(document.getElementById("<portlet:namespace />cantidadEmpleados").value)
			+'&periodo=' + trim(document.getElementById("<portlet:namespace />periodoMesAnio").value);
		url +='&nroBoleta=' + nroBoleta;
		url +='&nroSecuenciaDDJJ=' + nroSecuenciaDDJJ;
		url +='&boleta_capital='+ trim(document.getElementById("<portlet:namespace />boleta_capital").value);
		url +='&boleta_interes='+ trim(document.getElementById("<portlet:namespace />boleta_interes").value);
		url +='&boleta_ajuste='+ trim(document.getElementById("<portlet:namespace />boleta_ajuste").value);
		url += '&rnd=' + Math.floor(Math.random()*100);
		
		jQuery('#<portlet:namespace />otros_result').load(url, function() {
					jQuery('#<portlet:namespace />agregandoOtros').hide();
					jQuery("#<portlet:namespace />nroBoleta").val('');
					jQuery("#<portlet:namespace />boleta_capital").val("0");
			        jQuery("#<portlet:namespace />boleta_interes").val("0");
			        jQuery("#<portlet:namespace />boleta_ajuste").val("0");
			        
			        jQuery("#<portlet:namespace />boleta_capital_view").val("0");
			        jQuery("#<portlet:namespace />boleta_interes_view").val("0");
			        jQuery("#<portlet:namespace />boleta_ajuste_view").val("0");
			        
					sumarConceptos();
		}); 

	}
	
	function borrarOtroConcepto( id) {
		jQuery('#<portlet:namespace />agregandoOtros').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_otros_conceptos'
			+'&borrar=borrar'
			+'&oc_id=' + id;
			url += '&rnd=' + Math.floor(Math.random()*100);

		jQuery('#<portlet:namespace />otros_result').load(url, function() {
					jQuery('#<portlet:namespace />agregandoOtros').hide();
					sumarConceptos();
		}); 
	}


	function cambiarEstado(divElement){		
		divElement.slideToggle('slow');		
		busca_conceptos();
	}

	<%if (recibo != null && recibo.getActas() != null && recibo.getActas().size()>0){%>
		jQuery('#<portlet:namespace />divActas').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divActas').hide();
	<%}%>

	<%if (recibo != null && recibo.getConvenios() != null && recibo.getConvenios().size()>0){%>
		jQuery('#<portlet:namespace />divConvenios').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divConvenios').hide();
	<%}%>

	<%if (recibo != null && recibo.getChequesNoDepositados() != null && recibo.getChequesNoDepositados().size()>0){%>
		jQuery('#<portlet:namespace />divChqNoDepo').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divChqNoDepo').hide();
	<%}%>

	<%if (recibo != null && recibo.getChequesRechazados() != null && recibo.getChequesRechazados().size()>0){%>
		jQuery('#<portlet:namespace />divChqRhz').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divChqRhz').hide();
	<%}%>

	<%if (recibo != null && recibo.getOtrosConceptos() != null && recibo.getOtrosConceptos().size()>0){%>
	jQuery('#<portlet:namespace />divOtros').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divOtros').hide();
	<%}%>
	
	<%if (recibo != null && recibo.getReciboPrestamos() != null && recibo.getReciboPrestamos().size()>0){%>
	jQuery('#<portlet:namespace />divPrestamos').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divPrestamos').hide();
	<%}%>
	
	jQuery('#<portlet:namespace />agregandoActas').hide();
	jQuery('#<portlet:namespace />agregandoConvenios').hide();
	jQuery('#<portlet:namespace />agregandoChequeSust').hide();
	jQuery('#<portlet:namespace />agregandoChequeRech').hide();
	jQuery('#<portlet:namespace />agregandoOtros').hide();
	jQuery("#<portlet:namespace />periodoDia").hide();
	jQuery('#<portlet:namespace />agregandoPrestamos').hide();

	function busca_conceptos(){
		var cuit=document.getElementById("<portlet:namespace />cuit_entidad").value;
		var sucursal=document.getElementById("<portlet:namespace />sucursal_entidad").value;			
		var id_seccional='<%=String.valueOf(id_seccional)%>';
		var id_seccional_conc=jQuery("#<portlet:namespace />id_seccional_ent_otr_concep").val();
		
		if(id_seccional==0 || (parseInt(id_seccional_conc)>0 && id_seccional!=id_seccional_conc)){
			id_seccional=id_seccional_conc;		
		}
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_recibos_entry';
		url+='&cuit='+cuit;
		url+='&sucursal=' + sucursal;
		url+='&id_seccional=' + id_seccional;
		url+='&recargarConcepto=true'
		url+= '&rnd=' + Math.floor(Math.random()*100);

		jQuery('#<portlet:namespace />divOtrosConc').load(url); 
	}
	<%if(id_seccional>0){%>
		busca_conceptos();
	<%}%>

	function cambiaCuit(){				
		busca_conceptos();
	}
	
	function verEmpleadores(){
		var cuit=document.getElementById("<portlet:namespace />cuit_entidad").value;
		var sucursal=document.getElementById("<portlet:namespace />sucursal_entidad").value;
		var nroBoleta=jQuery("#<portlet:namespace />nroBoleta").val();
		var periodo=trim(document.getElementById("<portlet:namespace />periodoMesAnio").value);
		var concepto = trim(document.getElementById("<portlet:namespace />otro_concepto").value);
		
		if (concepto == "") {
			alert("Debe seleccionar un concepto");
			return false;
		}

		if(cuit==""){
			alert("Debe seleccionar un cuit");
			return false;
		}
		
		if(sucursal==""){
			alert("Debe seleccionar una sucursal");
			return false;
		}
			
		var popup = Liferay.Popup({title:"Portal Empleadores",modal:true,width:900});
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_otros_conceptos';
		url+='&empleadores=empleadores'
		url+='&concepto_id='+concepto
		url+='&cuit='+cuit;
		url+='&sucursal=' + sucursal;
		url+='&nroBoleta='+nroBoleta;
		url+= '&rnd=' + Math.floor(Math.random()*100);
		
		jQuery(popup).load(url);
		
	}
	
	function traeDatosEmpleadores(){
		var idconcepto =trim(document.getElementById("<portlet:namespace />otro_concepto").value);
		var nroBoleta=jQuery('#<portlet:namespace />nroBoleta').val();
			
		if(nroBoleta!=null && nroBoleta!=""){
			   var cuit=document.getElementById("<portlet:namespace />cuit_entidad").value;
			   var sucursal=document.getElementById("<portlet:namespace />sucursal_entidad").value;
			   var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/trae_boleta_portal_empleadores';
			   url1 +='&cuit='+cuit;
			   url1 +='&sucursal=' + sucursal;
			   url1	+='&concepto_id=' + trim(document.getElementById("<portlet:namespace />otro_concepto").value)
			   url1 +='&nroBoleta=' + nroBoleta
			   url1 +='&periodo=' + trim(document.getElementById("<portlet:namespace />periodoMesAnio").value);    
			   url1+= '&rnd=' + Math.floor(Math.random()*100);
	           jQuery.ajax({   
		        url: url1,
		        async:false,
		        success: function(data){
			    var obj = jQuery.parseJSON(data);
			    var res = obj.periodo.split("-");
                   if(obj.periodo!=""){			    
			          jQuery("#<portlet:namespace />periodoMesAnio").val(res[1]-1+"_"+res[0]);
			          jQuery("#<portlet:namespace />otro_importe").val(  Math.round(obj.totalboleta)/100 );
			          
			          jQuery("#<portlet:namespace />cantidadEmpleados").val(obj.empleados);
			          jQuery("#<portlet:namespace />remuneracionTotal").val(Math.round(obj.remuneracion)/100);
			          
			          
			          jQuery("#<portlet:namespace />boleta_capital").val(Math.round(obj.capital)/100 );
			          jQuery("#<portlet:namespace />boleta_interes").val(Math.round(obj.interes)/100 );
			          jQuery("#<portlet:namespace />boleta_ajuste").val(Math.round(obj.ajustes)/100 );
			          
			          jQuery("#<portlet:namespace />boleta_capital_view").val((Math.round(obj.capital)/100).toFixed(2) );
			          jQuery("#<portlet:namespace />boleta_interes_view").val((Math.round(obj.interes)/100).toFixed(2) );
			          jQuery("#<portlet:namespace />boleta_ajuste_view").val((Math.round(obj.ajustes)/100).toFixed(2) );
                   }   
		       }});
		}
	}

	
	function agregarPrestamo(){
		var nro=trim(document.getElementById("<portlet:namespace />prestamo_nro").value);
		if (nro == "") {
			alert("Debe seleccionar el nro de préstamo");
			return false;
		}

		var importe = trim(document.getElementById("<portlet:namespace />prestamo_importe").value);
		if (importe == "") {
			alert("Debe ingresar el importe del préstamo");
			document.getElementById("<portlet:namespace />prestamo_importe").focus();
			return false;
		}

		var dia=jQuery('#<portlet:namespace />fechaPrestamoDia').val();
		var mes=parseInt(jQuery('#<portlet:namespace />fechaPrestamoMes').val())+1;			
		var anio=jQuery('#<portlet:namespace />fechaPrestamoAnio').val();
		
		if(dia=="" || mes=="" || anio==""){
			alert("Debe ingresar la fecha del préstamo");
			return false;
		}
		
		var total = trim(document.getElementById("<portlet:namespace />prestamo_total").value);
		
		jQuery('#<portlet:namespace />agregandoPrestamos').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_prestamos'
			+'&prestamo_id=' + nro
			+'&importe=' + importe
			+'&importeTotal=' + total
			+'&dia=' + dia
			+'&mes=' + mes
			+'&anio='+ anio;
		url += '&rnd=' + Math.floor(Math.random()*100);
		
		jQuery('#<portlet:namespace />prestamos_result').load(url, function() {
					jQuery('#<portlet:namespace />agregandoPrestamos').hide();
					limpiarPrestamo();
					sumarConceptos();
					
					setearIngresoPrestamos();
		}); 

	}
	
	
	function limpiarPrestamo(){
		jQuery("#<portlet:namespace />prestamo_nro").val('');
		jQuery("#<portlet:namespace />prestamo_total").val("");
        jQuery("#<portlet:namespace />prestamo_importe").val("");
        jQuery("#<portlet:namespace />fechaPrestamoDia").val("<%= periodo.get(Calendar.DATE) %>");
        jQuery("#<portlet:namespace />fechaPrestamoMes").val("<%=periodo.get(Calendar.MONTH)%>");
        jQuery("#<portlet:namespace />fechaPrestamoAnio").val("<%=periodo.get(Calendar.YEAR)%>");
		
	}
	function borrarPrestamo( id) {
		jQuery('#<portlet:namespace />agregandoPrestamos').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_prestamos'
			+'&borrar=borrar'
			+'&prestamo_id=' + id;
			url += '&rnd=' + Math.floor(Math.random()*100);

		jQuery('#<portlet:namespace />prestamos_result').load(url, function() {
					jQuery('#<portlet:namespace />agregandoPrestamos').hide();
					sumarConceptos();
		}); 
	}
	
	function traerDatosPrestamo(){
		var cuil=null;
		var idPrestamo=jQuery("#<portlet:namespace />prestamo_nro").val();
		try{
		   cuil=jQuery('#<portlet:namespace />cuil').val();
		}catch(e){}
		
		if(cuil==null || ""==cuil){
			cuil=jQuery('#<portlet:namespace />cuilrecibosTesoreria').val();
			if(cuil==null || ""==cuil){
		      alert("Debe seleccionar un Afiliado");
		      return false
			}  
		}
		
		if(idPrestamo==null || ""==idPrestamo){
		     alert("Debe ingresar un Número de Beneficio");
		     return false
		}
		
		var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/trae_datos_prestamos';
		    url1 +='&cuil='+cuil;
			url1 +='&prestamo_id=' + idPrestamo
			url1+= '&rnd=' + Math.floor(Math.random()*100);
	
	        jQuery.ajax({   
		        url: url1,
		        async:false,
		        success: function(data){
			    var obj = jQuery.parseJSON(data);
		         
			    if(obj.inexistente=="true"){
			    	alert("No se ha encontrado el Beneficio para el Afiliado");
			    	limpiarPrestamo();
			    }else if(obj.otroAfiliado=="true"){
			    	alert("No corresponde el Beneficio para el Afiliado");
			    	limpiarPrestamo();
			    }else if(obj.total!=0){
			      jQuery("#<portlet:namespace />prestamo_total").val(Math.round(obj.total*100)/100);
			    }  
			    
			}});
		
	}

	
	function verPrestamos(){
		var cuil=null;
		try{
		  cuil=document.getElementById("<portlet:namespace />cuil").value;
		}catch(e){}
		if (cuil==null || cuil == "") {
			cuil=jQuery('#<portlet:namespace />cuilrecibosTesoreria').val();
			if(cuil==null || ""==cuil){
		      alert("Debe seleccionar un Afiliado");
		      return false
			}  
		}
		var popup = Liferay.Popup({title:"Beneficios AMTIMA",modal:true,width:900});
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_recibo_prestamos';
		url+='&prestamos=prestamos'
		url+='&cuil='+cuil;
		url+= '&rnd=' + Math.floor(Math.random()*100);
		
		jQuery(popup).load(url);
		
	}
	

	function verPagos(){
		var cuil=null;
		var idPrestamo=null;
		try{
		  cuil=document.getElementById("<portlet:namespace />cuil").value;
		  idPrestamo=jQuery("#<portlet:namespace />prestamo_nro").val();
		}catch(e){}
		if (cuil==null || cuil == "") {
			cuil=jQuery('#<portlet:namespace />cuilrecibosTesoreria').val();
			if(cuil==null || ""==cuil){
		      alert("Debe seleccionar un Afiliado");
		      return false
			}  
		}
		if (idPrestamo==null || idPrestamo == "") {
			alert("Debe seleccionar el nro de préstamo");
			return false;
		}
		var popup = Liferay.Popup({title:"Pagos Beneficios AMTIMA",modal:true,width:900});
		
		var params = "&cmd=" + "verPagos";
	 	params+="&id_prestamo=" + idPrestamo;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/hoteles_prestamos_abm';
		url = url + params;
		
		
		jQuery(popup).load(url);
		
	}
	
</script>
