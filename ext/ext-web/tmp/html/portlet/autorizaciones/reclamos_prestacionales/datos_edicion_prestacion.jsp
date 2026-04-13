<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%
//prestacion en proceso de edicion 
PrestacionesReclamo  prestacionEnEdicion  = (PrestacionesReclamo) request.getSession().getAttribute(WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION   );
request.getSession().removeAttribute(WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION);
Integer tipoedicion=0;
String ocultarSeccional=null;

Calendar fechaseccional  = Calendar.getInstance();

Calendar fechaPrestacion  = Calendar.getInstance();


if(prestacionEnEdicion != null  ){
	 tipoedicion = (Integer) request.getAttribute("tipoEdicion");
	 if(prestacionEnEdicion.getComprobanteFecha() != null){
		 fechaseccional.setTime(prestacionEnEdicion.getComprobanteFecha());
	 }
	 if(prestacionEnEdicion.getFechaPrestacion() !=null){
		 fechaPrestacion.setTime(prestacionEnEdicion.getFechaPrestacion());
	 }
	 
}

String   captionbotoncancelar="Cancelar Edicion de la Prestacion" ;
String   captionlabelproceso="PRESTACION EN PROCESO DE EDICION";
String   estiloLabel=""; 


if (tipoedicion==1) {
	captionbotoncancelar="Cancelar Autorizacion de la Prestacion";
	captionlabelproceso="PRESTACION EN PROCESO DE AUTORIZACION";
	estiloLabel="style='color:green;'";
}
if (tipoedicion==2) {
	captionbotoncancelar="Cancelar Rechazo de la Prestacion";
	captionlabelproceso="PRESTACION EN PROCESO DE RECHAZO";
	estiloLabel="style='color:red;'";
}

ocultarSeccional = (String) request.getAttribute("ocultar");


if(prestacionEnEdicion != null  ){
%>  
<script type="text/javascript">
jQuery("#<portlet:namespace />datos_edicion_prestacion").show();
jQuery("#<portlet:namespace />codigoprestacion").val('<%=prestacionEnEdicion.getCodigoPrestacion()%>');
jQuery("#<portlet:namespace />idRegistro").val('<%=prestacionEnEdicion.getIdRegistro()%>');

jQuery("#<portlet:namespace />idRegistro").val('<%=prestacionEnEdicion.getIdRegistro()%>');

<%if (prestacionEnEdicion != null && prestacionEnEdicion.getId_prestacion() != 0){ %>
	jQuery("#<portlet:namespace />codigoSeguimiento_filtro_edit").val('<%=prestacionEnEdicion.getCodigoPrestacion()%>');
	jQuery("#<portlet:namespace />descripcionSeguimiento_filtro_edit").val('<%=prestacionEnEdicion.getDescripcion()%>');
	<portlet:namespace />buscarNomencladorAutocompletar_edit();
<%}else{%>
	jQuery("#<portlet:namespace />troquel_edit").val('<%=prestacionEnEdicion.getId_medicamento()%>');
<%}%>

// se oculta parte area media para las seccionales
<%if(ocultarSeccional != null){%>

jQuery("#<portlet:namespace />Autorizado").hide();
	
<%}%>
</script>
<%}%>
	    <input   type="hidden" id="<portlet:namespace />idRegistro" name="<portlet:namespace />idRegistro" size="10" maxlength="10" type="text" value='<%=Validator.isNotNull(prestacionEnEdicion)  ? prestacionEnEdicion.getIdRegistro()      : ""  %>'/></td>

        <label <%=estiloLabel %>"><b><liferay-ui:message key="<%=captionlabelproceso%>"/></b></label>
        		
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">			
		<tr>
		
		 <td>
		<label>F. Prestación: </label>
		<liferay-ui:input-date dayParam="fechaPrestacionDiaEdicion"
			dayValue="<%=prestacionEnEdicion!=null && prestacionEnEdicion.getFechaPrestacion()!=null?fechaPrestacion.get(Calendar.DAY_OF_MONTH ):0%>" dayNullable="<%=true%>"
			monthParam="fechaPrestacionMesEdicion"   monthValue='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getFechaPrestacion()!=null?fechaPrestacion.get(Calendar.MONTH):-1 %>'			
			monthNullable="<%=true%>" yearParam="fechaPrestacionAnioEdicion"
			yearValue='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getFechaPrestacion()!=null?fechaPrestacion.get(Calendar.YEAR):-1 %>'
			 yearNullable="<%=true%>"
			yearRangeStart="<%=fechaseccional.get(Calendar.YEAR) - 5%>"
			yearRangeEnd="<%=fechaseccional.get(Calendar.YEAR)+1%>"
			firstDayOfWeek="" />
		</td>	
		<td>
		
		<%
		Integer idPrest = null;
		Integer idMedic = null;
		
		if (prestacionEnEdicion != null) {
		    idPrest = prestacionEnEdicion.getId_prestacion();
		    idMedic = prestacionEnEdicion.getId_medicamento();
		}
		
		//sin medicamento: no viene nada desde la app
		boolean sinMedicamento = (idMedic == null || idMedic.intValue() == 0);
		
		// hay prestacion cargada distinta de 0
		boolean hayPrestacion = (idPrest != null && idPrest.intValue() != 0);
		
		// mostrar código presentado si hay prestación, o si no hay medicamento
		boolean mostrarCodigoPresentado = hayPrestacion || sinMedicamento;
		%>	
			<%if (mostrarCodigoPresentado){ %>
	   	<td>	<label><liferay-ui:message key="codigo-presentado" />:</label></td>
					<td><input id="<portlet:namespace />codigoSeguimiento_filtro_edit"
						name="<portlet:namespace />codigoSeguimiento_filtro_edit" size="10"
						maxlength="20" type="text" value='' /></td>
					<td><input
						id="<portlet:namespace />descripcionSeguimiento_filtro_edit"
						name="<portlet:namespace />descripcionSeguimiento_filtro_edit"
						size="60" maxlength="200" type="text" value='' /></td>
					<td><div style="width:4%;" id="<portlet:namespace />divBtnBusca">
							<a href="javascript: void(0);"
								onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar_edit();"
								tabindex="-1">Buscar</a> <a href="javascript: void(0);"
								onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();"
								tabindex="-1">Limpiar</a>
						</div>
						<td>
	    	<%}else { %>
	    					
   			<td colspan="6">
   			  
						<liferay-util:include
							page="/html/portlet/utils/medicamentos_edit/busqueda_medicamentos_edit.jsp">
							<liferay-util:param name="search_url_edit"
								value="/autorizaciones/buscar_medicamentos_edit" />
							<liferay-util:param name="troquel" value='' />
							<liferay-util:param name="nombre_medicamento_edit" value='' />
							<liferay-util:param name="id_medicamento_edit" value='' />
							<liferay-util:param name="esEditable" value='true' />
							<liferay-util:param name="mostrar_con_presentacion_edit" value='true' />
						</liferay-util:include> 
				
	    	</td>
	    	<%} %>
		
		<td>&nbsp; </td>
		<td>&nbsp; </td>
		
		
		</tr>
		</table>
		
      
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 3px;">
		<tr>			
			<td colspan="15">
			 <div id="<portlet:namespace />datos_comprobante">
		     <fieldset class="block-labels">
	         <legend>
		         <liferay-ui:message key="Datos del Comprobante" />
	         </legend>	
			    <table>
			    <%if(ocultarSeccional == null){%>
			      <tr>
			      <td><label><liferay-ui:message key="Frecuencia" />:</label></td>	
			      <td>
					<select 
						name="<portlet:namespace />frecuenciaEdicion"
						id="<portlet:namespace />frecuenciaEdicion" >  
						<option value="SELECCIONE" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("SELECCIONE") ? "selected":"" %>>SELECCIONE</option>
						<option value="UNICA" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("UNICA") ? "selected":""%>>UNICA</option>
						<option value="SEMANAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("SEMANAL") ? "selected":""%>>SEMANAL</option>
						<option value="TRIMESTRAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("TRIMESTRAL") ? "selected":""%>>TRIMESTRAL</option>
						<option value="MENSUAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("MENSUAL") ? "selected":""%>>MENSUAL</option>
						<option value="SEMESTRAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("SEMESTRAL") ? "selected" :""%>>SEMESTRAL</option>					
						<option value="ANUAL" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getFrecuencia().equals("ANUAL") ? "selected" :""%>>ANUAL</option>
					</select>
				  </td>	  	
				<%}%>
				  <td><label><liferay-ui:message key="comprobante" />:</label></td>
			      <td>
			         <select name="<portlet:namespace/>comprobante_tipo_edicion" id="<portlet:namespace/>comprobante_tipo_edicion">
				        <option value="FCP"  <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteTipo().equals("FCP") ? "selected":"" %>>FCP</option>
				        <option value="RCB" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteTipo().equals("RCB") ? "selected":""%>>RCB</option>
				        <option value="OTR" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteTipo().equals("OTR") ? "selected":""%>>OTR</option>
				        <option value="AUT" <%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteTipo().equals("AUT") ? "selected":""%>>AUT</option>
			         </select> 
			      </td>
			      
			      	<td><label><liferay-ui:message key="letra" />:</label></td>
					<td>
					<select name="<portlet:namespace/>comprobante_letra_edicion"
					id="<portlet:namespace/>comprobante_letra_edicion">
					</select></td>
			      <td>Suc:</td>
			      <td> 
			        <input id="<portlet:namespace />comprobante_suc_edicion"
				        name="<portlet:namespace />comprobante_suc_edicion" size="8" maxlength="5"
				        type="text"	value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteSucursal()!=null?prestacionEnEdicion.getComprobanteSucursal():""%>'/>
			      </td>  	
			      
			      
			      <td>Nro:</td>
			      <td> 
			        <input id="<portlet:namespace />comprobante_nro_edicion"
				        name="<portlet:namespace />comprobante_nro_edicion" size="11" maxlength="15"
				        type="text"	value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteNro()!=null?prestacionEnEdicion.getComprobanteNro():""%>'/>
			      </td>  	
			      <td><label>F.Emision:</label></td>
			      <td colspan="1"><liferay-ui:input-date dayParam="fechaComprobanteDiaEdicion"
					   dayValue='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteFecha()!=null?fechaseccional.get(Calendar.DAY_OF_MONTH ):0%>' 
					   dayNullable="<%=true %>"
					   monthParam="fechaComprobanteMesEdicion"
					   monthValue='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteFecha()!=null?fechaseccional.get(Calendar.MONTH):-1 %>'					
					   monthNullable="<%= true %>"
					   yearParam="fechaComprobanteAnioEdicion"
					   yearValue='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteFecha()!=null?fechaseccional.get(Calendar.YEAR):-1 %>'
					   yearRangeStart="<%= fechaseccional.get(Calendar.YEAR)-5  %>"
					   yearRangeEnd="<%= fechaseccional.get(Calendar.YEAR) +1 %>"
					   yearNullable="<%= true %>"
					   firstDayOfWeek="<%= fechaseccional.getFirstDayOfWeek() - 1 %>"
					   />
			      </td>
			     </tr>
			  <tr><td>&nbsp;</td></tr>
			  <tr>
			       <td colspan="15"><liferay-util:include
					page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					<liferay-util:param name="esEditable"
							value='<%= String.valueOf( "true" ) %>' />
						<liferay-util:param name="cuit" value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteCUIT()!=null?prestacionEnEdicion.getComprobanteCUIT():""%>' />
						<liferay-util:param name="sucu" value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteCUITSucursal()!=null?prestacionEnEdicion.getComprobanteCUITSucursal():""%>' />
						<liferay-util:param name="razon" value='<%=Validator.isNotNull(prestacionEnEdicion) && prestacionEnEdicion.getComprobanteRazonSocial()!=null?prestacionEnEdicion.getComprobanteRazonSocial():""%>' />
						<liferay-util:param name="id_seccional" value='' />
						<liferay-util:param name="esEmpresaPrestador" value='true' />
						<liferay-util:param name="suf_entidad" value='_edicion'/>		
						<liferay-util:param name="suf" value='_edicion'/>		
					</liferay-util:include>
			  </td>
			</tr>
			
			<tr><td>&nbsp;</td></tr>
		         <tr>
			     <td><label><liferay-ui:message key="Cantidad" />:</label> </td>
			     <td><input id="<portlet:namespace />cantidadFC_edicion"   
				   name="<portlet:namespace />cantidadFC_edicion" size="8" maxlength="20" type="text" value='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteCantidad()!=null?prestacionEnEdicion.getComprobanteCantidad():"" %>'
				   onblur="calculatotalFCEdicion()" /> </td>
						
			     <td><label><liferay-ui:message key="Importe" />:</label> </td>
			     <td><input id="<portlet:namespace />importeUnitarioFC_edicion"   
				    name="<portlet:namespace />importeUnitarioFC_edicion" size="8" maxlength="20" 
				    value ='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteImporte()!=null?prestacionEnEdicion.getComprobanteImporte():"" %>' type="text"  
				    onkeydown="allowOnlyDigitsAndDecimals(event)"	
				    onblur="calculatotalFCEdicion()"/> </td>
			
							
			     <td><label>Total Comprobante:</label> </td>		
			     <td>
			        <input id="<portlet:namespace />importeFC_edicion"   
				    name="<portlet:namespace />importeFC_edicion" size="8" maxlength="20" 
				    value ='<%=prestacionEnEdicion!=null && prestacionEnEdicion.getComprobanteTotal()!=null?prestacionEnEdicion.getComprobanteTotal():"" %>' 
				    type="text" onkeydown="allowOnlyDigitsAndDecimals(event)" readonly="readonly"/>
			     </td>
				  
				 </tr>	
				</table>
				
			</fieldset>
			</div>	
		  </td>
	</tr>
<tr>		
	
<td colspan="8">
	  <div id="<portlet:namespace />Autorizado">
	  	
	  	  <fieldset class="block-labels">
	         <legend>
		         <liferay-ui:message key="Autorizado por Área Médica:" />
	         </legend> 
	  		
			<table>					
	         <tr>		
	            <td><label><liferay-ui:message key="Cantidad" />:</label></td>
	            <td><input id="<portlet:namespace />cantidadEdicion"
				     name="<portlet:namespace />cantidadEdicion" size="2" maxlength="20" type="text" value='<%=Validator.isNotNull(prestacionEnEdicion)  ? prestacionEnEdicion.getCantidad()    : ""  %>'
				     onkeypress="return validaMonto(event,this)" onblur="calculatotal()" /></td>
			
			    <td><label><liferay-ui:message key="Importe" />:</label></td>
			    <td><input id="<portlet:namespace />importeEdicion"
				     name="<portlet:namespace />importeEdicion" size="8" maxlength="20" type="text" value='<%=Validator.isNotNull(prestacionEnEdicion)  ? prestacionEnEdicion.getImporte()   : ""  %>'
				     onkeypress="return validaMonto(event,this)" onblur="calculatotal()" /></td>
		
			    <td><label><liferay-ui:message key="Total" />:</label></td>
				<td><input id="<portlet:namespace />totalEdicion"
				    name="<portlet:namespace />totalEdicion" size="8" maxlength="20" readonly="readonly" type="text" value='<%=Validator.isNotNull(prestacionEnEdicion)  ? prestacionEnEdicion.getTotalString() : ""  %>' /></td>
				
                <td>
                   <label><liferay-ui:message key="Cargo OSPIM" />:</label>
                </td>
			    <td><input id="<portlet:namespace />cargoospimEdicion"
				     name="<portlet:namespace />cargoospimEdicion" size="8" maxlength="20" value ='<%=Validator.isNotNull(prestacionEnEdicion)  ? prestacionEnEdicion.getCargo_ospim() : ""  %>' 
				    type="text" value=""  onkeypress="return validaMonto(event,this)" onkeydown="allowOnlyDigitsAndDecimals(event)"/></td>			
			    <td><label><liferay-ui:message key="cargo-terc" />:</label></td>
			    <td><input id="<portlet:namespace />cargopsEdicion"
				    name="<portlet:namespace />cargopsEdicion" size="8" maxlength="20" value ='<%=Validator.isNotNull(prestacionEnEdicion)  ? prestacionEnEdicion.getCargo_ps()  : ""  %>'     
				    type="text" value=""  onkeypress="return validaMonto(event,this)" /></td>
				    
				 <td>
                   <label><liferay-ui:message key="Cargo Monotributo" />:</label>
                </td>
			    <td><input id="<portlet:namespace />cargoimesaEdicion"
				     name="<portlet:namespace />cargoimesaEdicion" size="8" maxlength="20" value ='<%=Validator.isNotNull(prestacionEnEdicion)  ? prestacionEnEdicion.getCargo_imesa() : ""  %>' 
				    type="text" value=""  onkeypress="return validaMonto(event,this)" onkeydown="allowOnlyDigitsAndDecimals(event)"/></td>	
				 
				    
				<td><label>Reconocido SSS:</label></td>
			    <td><input id="<portlet:namespace />reconocidoSSSEdicion"
				    name="<portlet:namespace />reconocidoSSSEdicion" size="8" maxlength="20" 
				    value ='<%=Validator.isNotNull(prestacionEnEdicion)  ? prestacionEnEdicion.getReconocidoSSS()  : ""  %>'     
				    type="text" value=""  onkeypress="return validaMonto(event,this)" /></td>    
				
			    <td><label><liferay-ui:message key="Recuperable SUR" />:</label></td>
				<td>
				<select name="<portlet:namespace />recuperable_surEdicion" id="<portlet:namespace />recuperable_surEdicion" onchange="cambiorecuperableEdicion();">
						<option value="0">Seleccione Integración</option>
						<option value="1" <%=Validator.isNotNull(prestacionEnEdicion) &&  prestacionEnEdicion.getRecuperable() != null &&  prestacionEnEdicion.getRecuperable()==1 ? "selected" : ""  %>>SUR</option>
						<option value="3" <%=Validator.isNotNull(prestacionEnEdicion) &&  prestacionEnEdicion.getRecuperable() != null &&  prestacionEnEdicion.getRecuperable()==3 ? "selected" : ""  %>>Integración</option>
						<option value="2" <%=Validator.isNotNull(prestacionEnEdicion) &&  prestacionEnEdicion.getRecuperable() != null &&  prestacionEnEdicion.getRecuperable()==2 ? "selected" : ""  %>>NO Recuperable</option>
				</select>
				</td>
			 </tr>   
           </table>	
           </fieldset>
       	  </div>
           
</td>           								
</tr>
<tr>		
			
       <td> 
       	<c:choose>		
		<c:when test='<%= tipoedicion==1 %>'>
			<liferay-ui:message key="Observacion Edicion" />:
		</c:when>		
		<c:when test='<%= tipoedicion==2 %>'>
			<liferay-ui:message key="Observacion Autorizacion" />:
		</c:when>
		<c:when test='<%= tipoedicion==3 %>'>
			<liferay-ui:message key="Observacion Rechazo" />:
		</c:when>
		<c:otherwise>
        	<liferay-ui:message key="Observacion" />:
        </c:otherwise>
		</c:choose>	
        
       </td>				
       	<td>
       	<c:choose>		
		<c:when test='<%= tipoedicion==1 %>'>
			<textarea rows="3" cols="70" id="<portlet:namespace />observacion_prestacionEdicion" maxlength="250"
	    	name="<portlet:namespace />observacion_prestacionEdicion"><%=Validator.isNotNull(prestacionEnEdicion) && Validator.isNotNull(prestacionEnEdicion.getObservaciones() ) ? prestacionEnEdicion.getObservaciones():"" %></textarea>		
		</c:when>
       	<c:otherwise>
		 	<textarea rows="3" cols="70" 	id="<portlet:namespace />observacion_prestacionEdicion" maxlength="250"
	    	name="<portlet:namespace />observacion_prestacionEdicion"><%=Validator.isNotNull(prestacionEnEdicion) && Validator.isNotNull(prestacionEnEdicion.getObservaciones() ) ? prestacionEnEdicion.getObservaciones():"" %> </textarea>
        </c:otherwise>
       </c:choose>
       
       				
       </td>		
		<td></td><td></td>
		
		<div id="<portlet:namespace />botones_edicion_prestacion">
		<td>
		<%	if(tipoedicion ==0) { 	%>		
		<input type="button" name="<portlet:namespace />btnedita_prestacion" id="<portlet:namespace />btnedita_prestacion" value="<liferay-ui:message key="Editar Prestación" />"  onClick="<portlet:namespace />editarPrestacionSeleccionada(<%=tipoedicion%>);"  title="<liferay-ui:message key="Edita la prestacion" />" />
		<%}%>		
		<%	if(tipoedicion ==1) { 	%>		
		<input type="button" name="<portlet:namespace />btnautoriza_prestacion" id="<portlet:namespace />btnautoriza_prestacion" value="<liferay-ui:message key="Autoriza  Prestación" />"  onClick="<portlet:namespace />editarPrestacionSeleccionada(<%=tipoedicion%>);"  title="<liferay-ui:message key="Autoriza la prestacion" />"/>
		<%}%>
		<%	if(tipoedicion ==2) { 	%>		
		<input type="button" name="<portlet:namespace />btnrechaza_prestacion" id="<portlet:namespace />btnrechaza_prestacion" value="<liferay-ui:message key="Rechaza Prestación" />"  onClick="<portlet:namespace />editarPrestacionSeleccionada(<%=tipoedicion%>);"  title="<liferay-ui:message key="Rechaza la Prestacion" />" />
		<%}%>
		
		</td>
		<td></td><td></td>		
		<td><input type="button" value="<liferay-ui:message key="<%=captionbotoncancelar %>" />"  onClick="<portlet:namespace />cancelaEdicionPrestacion();"  /></td>
		</div>	      					
 </tr>
</table>
	
	
	
			

<script type="text/javascript">

filtrarLetraComprobanteEdicion();
cambiorecuperableEdicion();

function calculatotal(){
	importe=jQuery("#<portlet:namespace />importeEdicion").val();
	importe1 = importe.replace(",",".");
	cantidad=jQuery("#<portlet:namespace />cantidadEdicion").val();
	total= importe1 * cantidad  ;
	jQuery("#<portlet:namespace />totalEdicion").val(total.toFixed(2));
}  	

function calculatotalFCEdicion(){

	importe=jQuery("#<portlet:namespace />importeUnitarioFC_edicion").val();
	cantidad=jQuery("#<portlet:namespace />cantidadFC_edicion").val();
	total= importe * cantidad  ;
	jQuery("#<portlet:namespace />importeFC_edicion").val(Math.round(total.toFixed(2) * 100)/100);

}

function filtrarLetraComprobanteEdicion() {
	var tipoPedido = jQuery("#<portlet:namespace />tipopedido").val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/filtrarLetraComprobante&tipo_pedido='+tipoPedido;
	jQuery("#<portlet:namespace/>comprobante_letra_edicion").attr('disabled', 'disabled');
	
	jQuery.ajax({   
		url: url,
		async:false,
		success: function(data){
			document.getElementById("<portlet:namespace/>comprobante_letra_edicion").length = 0;
			jQuery("#<portlet:namespace/>comprobante_letra_edicion").removeAttr('disabled');
			var obj = jQuery.parseJSON(data);
			jQuery('#<portlet:namespace />comprobante_letra_edicion').html(data).fadeIn();

		}
	});
	
	jQuery("#<portlet:namespace />comprobante_letra_edicion").val("<%=prestacionEnEdicion != null ? prestacionEnEdicion.getComprobanteLetra() : ""%>");
}


function cambiorecuperableEdicion(){
	
	try{
		var recuperable=jQuery('#<portlet:namespace />recuperable_surEdicion').val();
		if(recuperable==3 || recuperable==1){
			jQuery('#<portlet:namespace/>reconocidoSSSEdicion').attr('readonly', false);
		}else{
			jQuery('#<portlet:namespace/>reconocidoSSSEdicion').val(0);
			jQuery('#<portlet:namespace/>reconocidoSSSEdicion').attr('readonly', true);
		}
		
			

	}catch (err) {}	
	
}

</script>	    
