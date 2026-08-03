<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

 
<%

String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}
String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);

Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
fechaDesde.setTime(new Date());
Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
fechaHasta.setTime(new Date());

List<TipoNomenclador> tiposNomencladorAux=TraeListasServiceUtil.getTiposNomenclador();
List<TipoNomenclador> tiposNomenclador=new  ArrayList<TipoNomenclador>();
for(TipoNomenclador t:tiposNomencladorAux){
	if(t.getId_tipo_nomenclador()==2 || t.getId_tipo_nomenclador()==4 || t.getId_tipo_nomenclador()==6 || t.getId_tipo_nomenclador()==8
			|| t.getId_tipo_nomenclador()==3 || t.getId_tipo_nomenclador()==1){
		tiposNomenclador.add(t);
	}
}

String prestacionPideTipo=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_DISCAPACIDAD_PIDE_TIPO");
List<OpcionesPrestacion> listaOpciones = TraeListasServiceUtil.getOpcionesPrestacion(prestacionPideTipo);

%>

<table width="100%">
  <tr>
	<td valign="top">
      <table class="lfr-table" >
	   <tr>
	   
	       <td><liferay-ui:message key="clase-expediente" />:</td>
			<td><select name="<portlet:namespace/>clasePrestacion" id="<portlet:namespace/>clasePrestacion" style="width: 230px;" >
					               <option value="0">Seleccione un Tipo de Nomenclador</option>
					               <%for (TipoNomenclador tnom : tiposNomenclador) {%>
								     <option value="<%= tnom.getId_tipo_nomenclador() %>">
								     <%=tnom.getDescripcion() %></option>
					               <%}%>
			    </select>
			    
		   </td>
	   
           <td><label><liferay-ui:message key="codigo-presentado" />:</label></td>
           
			<td><input id="<portlet:namespace />codigoPrestacion"
								name="<portlet:namespace />codigoPrestacion" size="10"
								maxlength="20" type="text"
								value='' /></td>
			<td><input id="<portlet:namespace />descripcionPrestacion"
				 name="<portlet:namespace />descripcionPrestacion" size="80"
				 maxlength="200" type="text"
				 value=''
				 onKeyUp="javascript:<portlet:namespace />buscarPrestacionOnDiv(event)" /></td>
				 
			<td><div id="<portlet:namespace />divBtnBuscaPrestacion">
									<a href="javascript: void(0);"
										onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();"
										tabindex="-1">Buscar</a> <a href="javascript: void(0);"
										onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();"
										tabindex="-1">Limpiar</a>
								</div>
							</td>
		</tr>
		<tr>
		  <td colspan="6">&nbsp;</td>
	    </tr>
	    
	    
	    <tr>
	    	<td colspan="6">
	      <table>
	        <tr>
	           <td><label><liferay-ui:message key="cantidad" />:</label></td>
				
		       <td><input id="<portlet:namespace />cantidadPrestacion"
					name="<portlet:namespace />cantidadPrestacion" size="20"
					maxlength="20" type="text" onkeydown="allowOnlyDigits(event);"
					value='1' onchange="<portlet:namespace />actualizoTotal()"/>
		       </td>
	    
	           <td><label><liferay-ui:message key="importe" />:</label></td>
	           <td><input id="<portlet:namespace />importePrestacion"
					name="<portlet:namespace />importePrestacion" size="20"
					maxlength="20" type="text" onkeydown="allowOnlyDigitsAndDecimals(event);"
					value='' onchange="<portlet:namespace />actualizoTotal()"/>
		       </td>
	    
	          <td><label><liferay-ui:message key="total" />:</label></td>
	          <td><input id="<portlet:namespace />totalPrestacion"
					name="<portlet:namespace />totalPrestacion" size="20"
					maxlength="20" type="text"
					value='' readonly="readonly" />
		      </td>
	    
	          <td><input type="checkbox"  name="<portlet:namespace />requiereAutorizacion" 
				   id="<portlet:namespace />requiereAutorizacion" disabled="disabled" > Requiere Autorización</td>
				   
			  <td><input type="checkbox"  name="<portlet:namespace />supra" 
				   id="<portlet:namespace />supra" disabled="disabled" > SUPRA</td>	   
	    
	          <td>    
	            <div id="<portlet:namespace />divOpc23101" hidden="true">
	             <table>
	             	<tr>
			            <td><label id="<portlet:namespace />opciones23101label" > <liferay-ui:message key="tipo"/>:</label></td>
			          	
					    <td>
						      <select  name="<portlet:namespace/>opciones23101" id="<portlet:namespace/>opciones23101">
							   <option selected value="0">SELECCIONE</option>
								   <% for (OpcionesPrestacion opcionNomenclador : listaOpciones) { %>
								    <option	 value="<%=opcionNomenclador.getId()%>"><%=opcionNomenclador.getDescripcion()%></option>
								   <%}%>
						      </select>
					
					    </td>
					 </tr>     
			     </table>
			    </div>
			  </td>
			  <td><input type="checkbox"  name="<portlet:namespace />cirugia" 
				   id="<portlet:namespace />cirugia" disabled="disabled" > Cirug&iacute;a</td> 
	          <td>
	              <%if(esEdicion){ %>
				  <input type="button" value="<liferay-ui:message key="agregar-codigo-seguimiento" />" 
		                 onClick="<portlet:namespace />agregarPreautorizacionCodigoNomenclador();" />
		          <%} %>       
		     </td>	
		   </tr>
		 </table> 
		 </td>  
	    </tr>
	    
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoCodigosPreautorizacion">
				<table style="align: center;" width="100%">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
		<tr>
		  <td colspan="1">&nbsp;</td>
	    </tr>
		<tr>
			<td colspan="12">
				<div align="center" id="<portlet:namespace />codigospreautorizaciones">
					<liferay-util:include page="/html/portlet/autorizaciones/pre_autorizaciones/preautorizacion_prestaciones_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
		
	</table>
	
  </td>
 </tr>
</table>	
	
<input type="hidden" name="<portlet:namespace />id_preautorizacion_codigo" id="<portlet:namespace />id_preautorizacion_codigo" value="" />
<input type="hidden" name="<portlet:namespace />id_detalle" id="<portlet:namespace />id_detalle" value="" />
<input type="hidden" name="<portlet:namespace />id_detalle_aux" id="<portlet:namespace />id_detalle_aux" value="" />

<script type="text/javascript">
    jQuery('#<portlet:namespace />agregandoCodigosPreautorizacion').hide();
    
	function <portlet:namespace />actualizoTotal(){
		
		var cantidad=jQuery('#<portlet:namespace />cantidadPrestacion').val();
		var importe=jQuery('#<portlet:namespace />importePrestacion').val();
		if(cantidad!=null && importe !=null){
			jQuery('#<portlet:namespace />totalPrestacion').val(cantidad*importe);
		}
		
	}
	
	function editarPreautorizacionCodigoNomenclador(idMod,idModAux,idTipoNomenclador,idPrestacion,codigo,descripcion,requiereAutorizacion,idApoyo,cantidad,importe,supra,cirugia){
		jQuery('#<portlet:namespace />id_detalle').val(idMod);
		jQuery('#<portlet:namespace />id_detalle_aux').val(idModAux);
		jQuery('#<portlet:namespace />cantidadPrestacion').val(cantidad);
		jQuery('#<portlet:namespace />importePrestacion').val(importe);	
		jQuery('#<portlet:namespace />codigoPrestacion').val(codigo);
		if(requiereAutorizacion=='true'){
		   jQuery('#<portlet:namespace />requiereAutorizacion').attr('checked', true);
	    }else{
	       jQuery('#<portlet:namespace />requiereAutorizacion').attr('checked', false);	
	    }
		if(supra=='true'){
			jQuery('#<portlet:namespace />supra').attr('checked', true);
		}else{
		    jQuery('#<portlet:namespace />supra').attr('checked', false);	
		}
		if(cirugia=='true'){
			jQuery('#<portlet:namespace />cirugia').attr('checked', true);
		}else{
		    jQuery('#<portlet:namespace />cirugia').attr('checked', false);	
		}
		
		jQuery('#<portlet:namespace />descripcionPrestacion').val(descripcion);
		jQuery('#<portlet:namespace/>clasePrestacion').val(idTipoNomenclador);
		jQuery('#<portlet:namespace />id_preautorizacion_codigo').val(idPrestacion);
		if(cantidad!=null && importe!=null){
		  jQuery('#<portlet:namespace />totalPrestacion').val(cantidad * importe);
		} 
		jQuery('#<portlet:namespace />opciones23101').val(idApoyo);
		if('<%=prestacionPideTipo%>' ==codigo){
			jQuery('#<portlet:namespace />divOpc23101').show();
		}else{
			jQuery('#<portlet:namespace />divOpc23101').hide();
		}
	}
	
</script>