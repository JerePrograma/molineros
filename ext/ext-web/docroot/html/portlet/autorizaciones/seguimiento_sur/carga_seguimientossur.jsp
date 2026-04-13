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

List<ModalidadAtencion> estadosOspimList=TraeListasServiceUtil.getEstadosOspimSeguimientoSur() ;

%>


<table width="100%">
  <tr>
	<td width="100%" valign="top">
	<table class="lfr-table" width="100%">
	   <tr>
	   
	   <td><label><liferay-ui:message key="estado-sur" />:</label></td>
	   <td colspan="2">
	   
	        <select name="<portlet:namespace/>estadoOspimSeguimientoSur" id="<portlet:namespace/>estadoOspimSeguimientoSur">
				     <option value="0">Seleccione Estado</option>
				     <%	for (ModalidadAtencion est : estadosOspimList) { %>
						<option value="<%= est.getId()%>"><%=est.getDescripcion()%></option>
				     <%	} %>
			         </select>
		</td>
	   
	   <td><label><liferay-ui:message key="fecha-notificacion-sur" />:</label></td>
		<td colspan="2">
			<liferay-ui:input-date
				dayParam="fechaNotificacionSurDia"																					
				dayValue=""
				dayNullable="<%= true %>"
				monthParam="fechaNotificacionSurMes"
				monthValue=""
				monthNullable="<%= true %>"
				yearParam="fechaNotificacionSurAnio"
				yearValue=""
				yearNullable="<%= true %>"
				yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
				yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
				firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>" />
		</td>
		
	   </tr>
	   <tr>
		  <td colspan="1">&nbsp;</td>
	   </tr>
	   <tr>
	   <td colspan="1" valign="top"><liferay-ui:message key="observaciones"/>:</label></td>
	   <td colspan="6"><textarea rows="5" cols="100" maxlength="20000" 
		               id="<portlet:namespace />observaciones" 
					   name="<portlet:namespace />observaciones"
					   style="resize: none;"></textarea>
		</td>	
	    <td valign="bottom">
	      <table>
	         <tr>
	           <td>
	                <input type="button" value="<liferay-ui:message key="limpiar-campos" />" 
		            onClick="<portlet:namespace />limpiarSeguimientoSurDetalle();" />
		       </td>
		       <td>     
		            <input type="button" value="<liferay-ui:message key="agregar-seguimiento" />" 
		            onClick="<portlet:namespace />agregarSeguimientoSurDetalle();" />
		       </td>     
		            
		     </tr>       
		  </table>
	    </td>
        </tr>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoSeguimientos">
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
			<td colspan="12">
				<div align="center" id="<portlet:namespace />seguimientossur">
					<liferay-util:include page="/html/portlet/autorizaciones/seguimiento_sur/seguimientosur_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
	</td>
  </tr>
</table>

<input type="hidden" name="<portlet:namespace />id_seguimiento_detalle" id="<portlet:namespace />id_seguimiento_detalle" value="" />

<script type="text/javascript">
    jQuery('#<portlet:namespace />agregandoSeguimientos').hide();
	function <portlet:namespace />agregarSeguimientoSurDetalle(){
		var idDetalle=jQuery('#<portlet:namespace />id_seguimiento_detalle').val();
//		if(habilitaCargaDetalle(idDetalle)){
			jQuery('#<portlet:namespace />agregandoSeguimientos').show();
			
			var notificacionDia=jQuery('#<portlet:namespace />fechaNotificacionSurDia').val();
			var notificacionMes=jQuery('#<portlet:namespace />fechaNotificacionSurMes').val();
			var notificacionAnio=jQuery('#<portlet:namespace />fechaNotificacionSurAnio').val();
	
			var estadoOspim=jQuery('#<portlet:namespace/>estadoOspimSeguimientoSur').val();
			var estadoOspimDescripcion=jQuery('#<portlet:namespace/>estadoOspimSeguimientoSur').find('option:selected').text();
			
			var observaciones=jQuery('#<portlet:namespace />observaciones').val();
			
			var fechaActual = new Date();
			var fechaCarga = new Date(notificacionAnio,notificacionMes,notificacionDia);

			if(fechaActual>=fechaCarga){

			
				if(notificacionDia!=0 && notificacionAnio!=0){
					var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_seguimientoSurDetalle'
						+	'&<%= Constants.CMD%>=' 
					
					if(idDetalle==null || idDetalle =="" || idDetalle==0){
						url += '<%=Constants.ADD%>'
					}else{
						url +='<%=Constants.EDIT%>'
					}
					
					url +=
					  '&notificaciondia=' + encodeURI(notificacionDia)
					+ '&notificacionmes=' + encodeURI(notificacionMes)
					+ '&notificacionanio=' + encodeURI(notificacionAnio)
					+ '&estadoospim=' + encodeURI(estadoOspim)
					+ '&estadoospimdescripcion=' + encodeURI(estadoOspimDescripcion)
					+ '&observaciones=' + encodeURI(observaciones)
					+ '&iddetalle=' + encodeURI(idDetalle)
					+ '&esEdicion=' +"<%=esEdicion%>"; 	
					
					jQuery('#<portlet:namespace />seguimientossur').load(url, function() {
																jQuery('#<portlet:namespace />agregandoSeguimientos').hide();
																<portlet:namespace />limpiarSeguimientoSurDetalle();
												   }
					 );
				}else{
					jQuery('#<portlet:namespace />agregandoSeguimientos').hide();
				}
			}else{
				jQuery('#<portlet:namespace />agregandoSeguimientos').hide();
				alert("La fecha no puede ser superior a la actual");
			}
//		}
	}
	

	function borraSeguimientoSurDetalle(idMod){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_seguimientoSurDetalle'
			+	'&<%= Constants.CMD%>=' + '<%=Constants.DELETE%>'
			+ '&detalleid=' + encodeURI(idMod)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			jQuery('#<portlet:namespace />seguimientossur').load(url, function() {}	 );
	}
	
	function editaSeguimientoSurDetalle(idMod,estadoOspim,fechaNotificacion,observaciones){
		<portlet:namespace />limpiarSeguimientoSurDetalle();
		
		jQuery('#<portlet:namespace />id_seguimiento_detalle').val(idMod);
		jQuery('#<portlet:namespace />observaciones').val(observaciones);
		
		jQuery('#<portlet:namespace />fechaNotificacionSurDia').val(parseInt(fechaNotificacion.substring(0,2)));
		jQuery('#<portlet:namespace />fechaNotificacionSurMes').val(parseInt(fechaNotificacion.substring(3,5))-1);
		jQuery('#<portlet:namespace />fechaNotificacionSurAnio').val(parseInt(fechaNotificacion.substring(6)));
		jQuery('#<portlet:namespace />estadoOspimSeguimientoSur').val(parseInt(estadoOspim));
		
		if(fechaNotificacion!=null && fechaNotificacion!=""){
			jQuery('#<portlet:namespace />fechaNotificacionSurDia').attr('disabled',true);
			jQuery('#<portlet:namespace />fechaNotificacionSurMes').attr('disabled',true);
			jQuery('#<portlet:namespace />fechaNotificacionSurAnio').attr('disabled',true);
		}
	}
	
	function <portlet:namespace />limpiarSeguimientoSurDetalle(){
		jQuery('#<portlet:namespace />fechaNotificacionSurDia').val('');
		jQuery('#<portlet:namespace />fechaNotificacionSurMes').val('');
		jQuery('#<portlet:namespace />fechaNotificacionSurAnio').val('');
		
		jQuery('#<portlet:namespace />fechaRespuestaSurDia').val('');
		jQuery('#<portlet:namespace />fechaRespuestaSurMes').val('');
		jQuery('#<portlet:namespace />fechaRespuestaSurAnio').val('');
		
		jQuery('#<portlet:namespace />estadoOspimSeguimientoSur').val('');
		
		jQuery('#<portlet:namespace />observaciones').val('');
		jQuery('#<portlet:namespace />id_seguimiento_detalle').val('');
		
		jQuery('#<portlet:namespace />fechaNotificacionSurDia').attr('disabled',false);
		jQuery('#<portlet:namespace />fechaNotificacionSurMes').attr('disabled',false);
		jQuery('#<portlet:namespace />fechaNotificacionSurAnio').attr('disabled',false);
		jQuery('#<portlet:namespace />estadoOspimSeguimientoSur').attr('disabled',false);
		
			
	}
	
	
	function habilitaCargaDetalle(idDetalle){
		if(idDetalle==null || idDetalle =="" || idDetalle==0){
			
			 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/habilita_carga_detalleseguimientosur';
			 var ret = true;
			 jQuery.ajax({   
					url: url,
					async: false,
					success: function(data){
						var obj = jQuery.parseJSON(data);
						if(obj.validado=="1"){
							ret= true;
						}else{
							alert("Existe un seguimiento incompleto. No será posible agregar un nuevo seguimiento hasta que se complete el anterior");
							ret= false;
						} 					
					}
				});
			 return ret;
		}else{
		   return true;
		}   
	}
	
</script>