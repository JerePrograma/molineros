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

List<ModalidadAtencion> estadosList=TraeListasServiceUtil.getEstadosSeguimientoSur() ;

%>


<table width="100%">
  <tr>
	<td width="100%" valign="top">
	<table class="lfr-table" width="100%">
	   <tr>
	   <td><label><liferay-ui:message key="estado-sur" />:</label></td>
	   <td colspan="1">
	   
	        <select name="<portlet:namespace/>estadoSeguimientoSur" id="<portlet:namespace/>estadoSeguimientoSur" onChange="javascript=filtrarMotivosEstadosSeguimientoSur();">
				     <option value="0">Seleccione Estado</option>
				     <%	for (ModalidadAtencion terce : estadosList) { %>
						<option value="<%= terce.getId()%>"><%=terce.getDescripcion()%></option>
				     <%	} %>
			         </select>
			         
		</td>
		<td><label><liferay-ui:message key="fecha" />:</label></td>
		<td colspan="2">
			<liferay-ui:input-date
				dayParam="fechaEstadoSurDia"																					
				dayValue="<%= fechaHasta.get(Calendar.DATE) %>"
				dayNullable="<%= true %>"
				monthParam="fechaEstadoSurMes"
				monthValue="<%= fechaHasta.get(Calendar.MONTH ) %>"
				monthNullable="<%= true %>"
				yearParam="fechaEstadoSurAnio"
				yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
				yearNullable="<%= true %>"
				yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
				yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
				firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>" />
		</td>
		
	    <td><label id="<portlet:namespace/>estadoMotivoSeguimientoSurLb"><liferay-ui:message key="motivo" />:</label></td>
	    <td colspan="1">
	        <select name="<portlet:namespace/>estadoMotivoSeguimientoSur" id="<portlet:namespace/>estadoMotivoSeguimientoSur">
				     <option value="0">Seleccione Motivo</option>
			</select>
		 </td>
		
		
		 <tr>
		  <td colspan="1">&nbsp;</td>
	     </tr>
	   <tr>
	   <td colspan="1" valign="top"><liferay-ui:message key="observaciones"/>:</label></td>
	   <td colspan="6"><textarea rows="5" cols="100" maxlength="20000" 
		               id="<portlet:namespace />observacionesestado" 
					   name="<portlet:namespace />observacionesestado"
					   style="resize: none;"></textarea>
		</td>	
	    <td valign="bottom">
	      <table>
	         <tr>
	           <td>
	                <input type="button" value="<liferay-ui:message key="limpiar-campos" />" 
		            onClick="<portlet:namespace />limpiarSeguimientoSurEstado();" />
		       </td>
		       <td>     
		            <input type="button" value="<liferay-ui:message key="agregar-estado-seguimiento" />" 
		            onClick="<portlet:namespace />agregarSeguimientoSurEstado();" />
		       </td>     
		            
		     </tr>       
		  </table>
	    </td>
        </tr>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoEstadosSeguimientos">
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
				<div align="center" id="<portlet:namespace />estadosseguimientossur">
					<liferay-util:include page="/html/portlet/autorizaciones/seguimiento_sur/seguimientosur_estados_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
	</td>
  </tr>
</table>

<input type="hidden" name="<portlet:namespace />id_seguimiento_estado" id="<portlet:namespace />id_seguimiento_estado" value="" />

<script type="text/javascript">
    jQuery('#<portlet:namespace />agregandoEstadosSeguimientos').hide();
    document.getElementById("<portlet:namespace />estadoMotivoSeguimientoSur").style.visibility = "hidden";
	document.getElementById("<portlet:namespace />estadoMotivoSeguimientoSurLb").style.visibility = "hidden";
    
    
	function <portlet:namespace />agregarSeguimientoSurEstado(){
		var idDetalle=jQuery('#<portlet:namespace />id_seguimiento_estado').val();
		if(idDetalle!=null){
			jQuery('#<portlet:namespace />agregandoEstadosSeguimientos').show();
			
			var estadoDia=jQuery('#<portlet:namespace />fechaEstadoSurDia').val();
			var estadoMes=jQuery('#<portlet:namespace />fechaEstadoSurMes').val();
			var estadoAnio=jQuery('#<portlet:namespace />fechaEstadoSurAnio').val();
			var estadoId=jQuery('#<portlet:namespace/>estadoSeguimientoSur').val();
			var estadoDescripcion=jQuery('#<portlet:namespace/>estadoSeguimientoSur').find('option:selected').text();
			var observaciones=jQuery('#<portlet:namespace />observacionesestado').val();
			var motivoId=jQuery('#<portlet:namespace/>estadoMotivoSeguimientoSur').val();
			var motivoDescripcion=jQuery('#<portlet:namespace/>estadoMotivoSeguimientoSur').find('option:selected').text();
			var fechaActual = new Date();
			var fechaCarga = new Date(estadoAnio,estadoMes,estadoDia);

			if(fechaActual>=fechaCarga){
			
				if(estadoDia!=0 && estadoAnio!=0 && estadoId !=0){
					var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_seguimientoSurEstado'
						+	'&<%= Constants.CMD%>=' ;
					
					if(idDetalle==null || idDetalle =="" || idDetalle==0){
						url += '<%=Constants.ADD%>';
					}else{
						url +='<%=Constants.EDIT%>';
					}
					
					url +=
					  '&estadodia=' + encodeURI(estadoDia)
					+ '&estadomes=' + encodeURI(estadoMes)
					+ '&estadoanio=' + encodeURI(estadoAnio)
					+ '&estadoid=' + encodeURI(estadoId)
					+ '&estadodescripcion=' + encodeURI(estadoDescripcion)
					+ '&observaciones=' + encodeURI(observaciones)
					+ '&iddetalle=' + encodeURI(idDetalle)
					+ '&motivoid=' + encodeURI(motivoId)
					+ '&motivodescripcion=' + encodeURI(motivoDescripcion)
					+ '&esEdicion=' +"<%=esEdicion%>"; 	
					
					jQuery('#<portlet:namespace />estadosseguimientossur').load(url, function() {
																jQuery('#<portlet:namespace />agregandoEstadosSeguimientos').hide();
																<portlet:namespace />limpiarSeguimientoSurEstado();
												   }
					 );
				}else{
					jQuery('#<portlet:namespace />agregandoEstadosSeguimientos').hide();
				}
			}else{
				jQuery('#<portlet:namespace />agregandoEstadosSeguimientos').hide();
				alert("La fecha no puede ser superior a la actual");
			}	
			
		}
	}
	

	function borraSeguimientoSurEstado(idMod){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_seguimientoSurEstado'
			+	'&<%= Constants.CMD%>=' + '<%=Constants.DELETE%>'
			+ '&detalleid=' + encodeURI(idMod)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			jQuery('#<portlet:namespace />estadosseguimientossur').load(url, function() {}	 );
	}
	
	function editaSeguimientoSurEstado(idMod,idEstado,fechaEstado,observaciones,idMotivo){
		<portlet:namespace />limpiarSeguimientoSurEstado();
		jQuery('#<portlet:namespace />id_seguimiento_estado').val(idMod);
		jQuery('#<portlet:namespace />observacionesestado').val(decodeURI(observaciones));
		jQuery('#<portlet:namespace/>estadoSeguimientoSur').val(idEstado);
		jQuery('#<portlet:namespace/>estadoMotivoSeguimientoSur').val(idMotivo);
		
		jQuery('#<portlet:namespace />fechaEstadoSurDia').val(parseInt(fechaEstado.substring(0,2)));
		jQuery('#<portlet:namespace />fechaEstadoSurMes').val(parseInt(fechaEstado.substring(3,5))-1);
		jQuery('#<portlet:namespace />fechaEstadoSurAnio').val(parseInt(fechaEstado.substring(6)));
		
		if(fechaEstado!=null && fechaEstado!=""){	
			jQuery('#<portlet:namespace />fechaEstadoSurDia').attr('disabled',true);
			jQuery('#<portlet:namespace />fechaEstadoSurMes').attr('disabled',true);
			jQuery('#<portlet:namespace />fechaEstadoSurAnio').attr('disabled',true);
			jQuery('#<portlet:namespace/>estadoSeguimientoSur').attr('disabled',true);
			jQuery('#<portlet:namespace/>estadoMotivoSeguimientoSur').attr('disabled',true);
		}
		
	}
	
	function <portlet:namespace />limpiarSeguimientoSurEstado(){
		
		var d = new Date();
		var month = d.getMonth(); 
		var day = d.getDate();
		var year = d.getFullYear();

		jQuery('#<portlet:namespace />fechaEstadoSurDia').val(day);
		jQuery('#<portlet:namespace />fechaEstadoSurMes').val(month);
		jQuery('#<portlet:namespace />fechaEstadoSurAnio').val(year);
		
		jQuery('#<portlet:namespace />observacionesestado').val('');
		jQuery('#<portlet:namespace />id_seguimiento_estado').val('');
		jQuery('#<portlet:namespace/>estadoSeguimientoSur').val('');
		jQuery('#<portlet:namespace/>estadoMotivoSeguimientoSur').val('');
		
		jQuery('#<portlet:namespace />fechaEstadoSurDia').attr('disabled',false);
		jQuery('#<portlet:namespace />fechaEstadoSurMes').attr('disabled',false);
		jQuery('#<portlet:namespace />fechaEstadoSurAnio').attr('disabled',false);
		jQuery('#<portlet:namespace/>estadoSeguimientoSur').attr('disabled',false);
		jQuery('#<portlet:namespace/>estadoMotivoSeguimientoSur').attr('disabled',false);
	   		
	}
	
	
	function filtrarMotivosEstadosSeguimientoSur() {
		var idEstado = jQuery('#<portlet:namespace/>estadoSeguimientoSur').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/id_estadoSur_motivo&idEstado='+idEstado;
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				var str='';
				jQuery('#<portlet:namespace />estadoMotivoSeguimientoSur').find('option').remove();
				jQuery('#<portlet:namespace />estadoMotivoSeguimientoSur').append('<option value="0"></option>');
				for(var i =0;i< obj.motivos.length; i++){
					str='<option value="'+obj.motivos[i].id+'"';
					str+='>'+obj.motivos[i].descripcion+'</option>'
					jQuery('#<portlet:namespace />estadoMotivoSeguimientoSur').append(str);
				}  
				
				if(obj.motivos.length<=1){
				   document.getElementById("<portlet:namespace />estadoMotivoSeguimientoSur").style.visibility = "hidden";
				   document.getElementById("<portlet:namespace />estadoMotivoSeguimientoSurLb").style.visibility = "hidden";
						
				}else{
				   document.getElementById("<portlet:namespace />estadoMotivoSeguimientoSur").style.visibility = "visible";
				   document.getElementById("<portlet:namespace />estadoMotivoSeguimientoSurLb").style.visibility = "visible";
				}

				
			}
		});	
	}
	
</script>