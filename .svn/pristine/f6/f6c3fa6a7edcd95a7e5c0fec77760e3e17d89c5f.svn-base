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

%>


<table width="100%">
  <tr>
	<td width="100%" valign="top">
	<table class="lfr-table" width="100%">
	   <tr>
	   <td><label><liferay-ui:message key="cuit"/>:</label></td>
					<td><input id="<portlet:namespace />cuitPrestadorSeguimientoSur" 
					      name="<portlet:namespace />cuitPrestadorSeguimientoSur" size="10" maxlength="13" type="text" value=''/></td>
					      
					<td><label><liferay-ui:message key="prestador"/>:</label></td>
					<td><input id="<portlet:namespace />prestadorSeguimientoSur" 
					      name="<portlet:namespace />prestadorSeguimientoSur" size="60" maxlength="70" type="text" value=''  /></td> 
					      
					<td>     
					 <a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarPrestadorSeguimiento();" tabindex="-1">Buscar</a>
		<td><label><liferay-ui:message key="fecha" />:</label></td>
		<td colspan="2">
			<liferay-ui:input-date
				dayParam="fechaPrestadorSurDia"																					
				dayValue="<%= fechaHasta.get(Calendar.DATE) %>"
				dayNullable="<%= true %>"
				monthParam="fechaPrestadorSurMes"
				monthValue="<%= fechaHasta.get(Calendar.MONTH ) %>"
				monthNullable="<%= true %>"
				yearParam="fechaPrestadorSurAnio"
				yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
				yearNullable="<%= true %>"
				yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
				yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
				firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>" />
		</td>
		
		<tr>
		  <td colspan="1">&nbsp;</td>
	   </tr>
	   <tr>
	   <td colspan="1" valign="top"><liferay-ui:message key="observaciones"/>:</label></td>
	   <td colspan="6"><textarea rows="5" cols="100" maxlength="20000" 
		               id="<portlet:namespace />observacionesprestador" 
					   name="<portlet:namespace />observacionesprestador"
					   style="resize: none;"></textarea>
		</td>	
	    <td valign="bottom">
	      <table>
	         <tr>
	           <td>
	                <input type="button" value="<liferay-ui:message key="limpiar-campos" />" 
		            onClick="<portlet:namespace />limpiarSeguimientoSurPrestador();" />
		       </td>
		       <td>     
		            <input type="button" value="<liferay-ui:message key="agregar-prestador-seguimiento" />" 
		            onClick="<portlet:namespace />agregarSeguimientoSurPrestador();" />
		       </td>     
		            
		     </tr>       
		  </table>
	    </td>
        </tr>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoPrestadoresSeguimientos">
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
				<div align="center" id="<portlet:namespace />prestadoresseguimientossur">
					<liferay-util:include page="/html/portlet/autorizaciones/seguimiento_sur/seguimientosur_prestadores_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
	</td>
  </tr>
</table>

<input type="hidden" name="<portlet:namespace />id_seguimiento_prestador" id="<portlet:namespace />id_seguimiento_prestador" value="" />
<input type="hidden" name="<portlet:namespace />id_prestador_seguimiento_sur" id="<portlet:namespace />id_prestador_seguimiento_sur" value="" />

<script type="text/javascript">
    jQuery('#<portlet:namespace />agregandoPrestadoresSeguimientos').hide();
	function <portlet:namespace />agregarSeguimientoSurPrestador(){
		var idDetalle=jQuery('#<portlet:namespace />id_prestador_seguimiento_sur').val();
		var idSeguimientoPrestador=jQuery('#<portlet:namespace />id_seguimiento_prestador').val();
		if(idDetalle!=null){
			jQuery('#<portlet:namespace />agregandoPrestadoresSeguimientos').show();
			var prestadorDia=jQuery('#<portlet:namespace />fechaPrestadorSurDia').val();
			var prestadorMes=jQuery('#<portlet:namespace />fechaPrestadorSurMes').val();
			var prestadorAnio=jQuery('#<portlet:namespace />fechaPrestadorSurAnio').val();
			var prestadorCuit=jQuery('#<portlet:namespace/>cuitPrestadorSeguimientoSur').val();
			var prestadorId=jQuery('#<portlet:namespace/>id_prestador_seguimiento_sur').val();
			var prestadorDescripcion=jQuery('#<portlet:namespace/>prestadorSeguimientoSur').val();
			var observaciones=jQuery('#<portlet:namespace />observacionesprestador').val();
			var fechaActual = new Date();
			var fechaCarga = new Date(prestadorAnio,prestadorMes,prestadorDia);

			if(fechaActual>=fechaCarga){
				if(prestadorDia!=0 && prestadorAnio!=0 && prestadorCuit !=0){
					var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_seguimientoSurPrestador'
						+	'&<%= Constants.CMD%>=' 
					
					if(idSeguimientoPrestador==null || idSeguimientoPrestador =="" || idSeguimientoPrestador==0){
						url += '<%=Constants.ADD%>'
					}else{
						url +='<%=Constants.EDIT%>'
					}
					url +=
					  '&prestadordia=' + encodeURI(prestadorDia)
					+ '&prestadormes=' + encodeURI(prestadorMes)
					+ '&prestadoranio=' + encodeURI(prestadorAnio)
					+ '&prestadorid=' + encodeURI(prestadorId)
					+ '&prestadorcuit=' + encodeURI(prestadorCuit)
					+ '&prestadordescripcion=' + encodeURI(prestadorDescripcion)
					+ '&observaciones=' + encodeURI(observaciones)
					+ '&iddetalle=' + encodeURI(idSeguimientoPrestador)
					+ '&esEdicion=' +"<%=esEdicion%>"; 	
					
					jQuery('#<portlet:namespace />prestadoresseguimientossur').load(url, function() {
																jQuery('#<portlet:namespace />agregandoPrestadoresSeguimientos').hide();
																<portlet:namespace />limpiarSeguimientoSurPrestador();
												   }
					 );
				}else{
					jQuery('#<portlet:namespace />agregandoPrestadoresSeguimientos').hide();
				}
			
		}else{
			jQuery('#<portlet:namespace />agregandoPrestadoresSeguimientos').hide();
			alert("La fecha no puede ser superior a la actual");
		}
	}
	}

	function borraSeguimientoSurPrestador(idMod){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_seguimientoSurPrestador'
			+	'&<%= Constants.CMD%>=' + '<%=Constants.DELETE%>'
			+ '&detalleid=' + encodeURI(idMod)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			jQuery('#<portlet:namespace />prestadoresseguimientossur').load(url, function() {}	 );
	}
	
	function editaSeguimientoSurPrestador(idMod,idPrestador,cuitPrestador,descripcionPrestador,fechaEstado,observaciones){
		<portlet:namespace />limpiarSeguimientoSurPrestador();
		
		jQuery('#<portlet:namespace />id_seguimiento_prestador').val(idMod);
		jQuery('#<portlet:namespace />id_prestador_seguimiento_sur').val(idPrestador);
		jQuery('#<portlet:namespace />cuitPrestadorSeguimientoSur').val(cuitPrestador);
		jQuery('#<portlet:namespace/>prestadorSeguimientoSur').val(descripcionPrestador);
		jQuery('#<portlet:namespace />observacionesprestador').val(observaciones);
		
		
		jQuery('#<portlet:namespace />fechaPrestadorSurDia').val(parseInt(fechaEstado.substring(0,2)));
		jQuery('#<portlet:namespace />fechaPrestadorSurMes').val(parseInt(fechaEstado.substring(3,5))-1);
		jQuery('#<portlet:namespace />fechaPrestadorSurAnio').val(parseInt(fechaEstado.substring(6)));
		
		if(fechaEstado!=null && fechaEstado!=""){	
			jQuery('#<portlet:namespace />fechaPrestadorSurDia').attr('disabled',true);
			jQuery('#<portlet:namespace />fechaPrestadorSurMes').attr('disabled',true);
			jQuery('#<portlet:namespace />fechaPrestadorSurAnio').attr('disabled',true);
			jQuery('#<portlet:namespace/>prestadorSeguimientoSur').attr('disabled',true);
			jQuery('#<portlet:namespace/>cuitPrestadorSeguimientoSur').attr('disabled',true);
		}
		
	}
	
	function <portlet:namespace />limpiarSeguimientoSurPrestador(){
		
		var d = new Date();
		var month = d.getMonth(); 
		var day = d.getDate();
		var year = d.getFullYear();

		jQuery('#<portlet:namespace />fechaPrestadorSurDia').val(day);
		jQuery('#<portlet:namespace />fechaPrestadorSurMes').val(month);
		jQuery('#<portlet:namespace />fechaPrestadorSurAnio').val(year);
		
		jQuery('#<portlet:namespace />observacionesprestador').val('');
		jQuery('#<portlet:namespace />id_seguimiento_prestador').val('');
		jQuery('#<portlet:namespace/>cuitPrestadorSeguimientoSur').val('');
		jQuery('#<portlet:namespace/>prestadorSeguimientoSur').val('');
		jQuery('#<portlet:namespace />id_prestador_seguimiento_sur').val('');
		
		jQuery('#<portlet:namespace />fechaPrestadorSurDia').attr('disabled',false);
		jQuery('#<portlet:namespace />fechaPrestadorSurMes').attr('disabled',false);
		jQuery('#<portlet:namespace />fechaPrestadorSurAnio').attr('disabled',false);
		jQuery('#<portlet:namespace/>cuitPrestadorSeguimientoSur').attr('disabled',false);
		jQuery('#<portlet:namespace/>prestadorSeguimientoSur').attr('disabled',false);
	   		
	}
	
	
	
	function <portlet:namespace />buscarPrestadorSeguimiento() {
		var cuit=jQuery("#<portlet:namespace />cuitPrestadorSeguimientoSur").val();
		var prestador=jQuery("#<portlet:namespace />prestadorSeguimientoSur").val();    
		if (cuit == null){
			cuit = "";
		}    
		if (prestador==null){
			prestador = "";
		}
		if(cuit.length == 0 && prestador.length==0) {
		   alert("Debe ingresar algún parámetro");
		}else {
			popupPD = Liferay.Popup({title:"Búsqueda Prestador",modal:true,width:420});
		    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_prestador';
		    url+='&cuit_prestador='+cuit+'&nombre_prestador='+encodeURI(prestador)+'&ext=CP';
			jQuery(popupPD).load(url);
			
		}
	}
	
	
	function seleccionaCamposPrestadCP(cuit, descripcion, id) {
	    jQuery("#<portlet:namespace />cuitPrestadorSeguimientoSur").val(cuit);
	    jQuery("#<portlet:namespace />prestadorSeguimientoSur").val(descripcion);
	    jQuery("#<portlet:namespace />id_prestador_seguimiento_sur").val(id);
	}

	function pasarParametrosAParentPdCP(cuit, descripcion, id) {
  	    seleccionaCamposPrestadCP(cuit,descripcion,id);
	    <portlet:namespace />cerrarPd();
	}
	
	function <portlet:namespace />cerrarPd(){
		if(popupPD){
			Liferay.Popup.close(popupPD);
		}
	}
	
</script>