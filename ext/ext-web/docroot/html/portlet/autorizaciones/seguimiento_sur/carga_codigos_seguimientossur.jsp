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
	<td valign="top">
      <table class="lfr-table" >
	   <tr>
           <td><label><liferay-ui:message key="codigo-presentado" />:</label></td>
							<td><input id="<portlet:namespace />codigoSeguimiento"
								name="<portlet:namespace />codigoSeguimiento" size="10"
								maxlength="20" type="text"
								value='' /></td>
							<td><input id="<portlet:namespace />descripcionSeguimiento"
								name="<portlet:namespace />descripcionSeguimiento" size="80"
								maxlength="200" type="text"
								value=''
								onKeyUp="javascript:<portlet:namespace />buscarSeguimientoSurOnDiv(event)" /></td>
							<td><div id="<portlet:namespace />divBtnBuscaSeguimientoSur">
									<a href="javascript: void(0);"
										onclick="javascript:<portlet:namespace />buscarNomencladorAutocompletar();"
										tabindex="-1">Buscar</a> <a href="javascript: void(0);"
										onclick="javascript:<portlet:namespace />limpiarNomencladorAutocompletar();"
										tabindex="-1">Limpiar</a>
								</div>
							</td>
							
							
							<td>     
							  <input type="button" value="<liferay-ui:message key="agregar-codigo-seguimiento" />" 
		                             onClick="<portlet:namespace />agregarSeguimientoSurCodigoNomenclador();" />
		                    </td>	
		</tr>
		
		<tr>
		  <td colspan="1">&nbsp;</td>
	    </tr>
	    
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoCodigosSeguimientos">
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
				<div align="center" id="<portlet:namespace />codigosseguimientossur">
					<liferay-util:include page="/html/portlet/autorizaciones/seguimiento_sur/seguimientosur_codigos_nomenclador_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
		
	</table>
	
  </td>
 </tr>
</table>	
	
<input type="hidden" name="<portlet:namespace />id_seguimiento_codigo" id="<portlet:namespace />id_seguimiento_codigo" value="" />
<input type="hidden" name="<portlet:namespace />id_codigo_seguimiento_sur" id="<portlet:namespace />id_codigo_seguimiento_sur" value="" />

<script type="text/javascript">
    jQuery('#<portlet:namespace />agregandoCodigosSeguimientos').hide();
    
	function <portlet:namespace />agregarSeguimientoSurCodigoNomenclador(){
		var idDetalle=jQuery('#<portlet:namespace />id_codigo_seguimiento_sur').val();
		var idSeguimientoCodigo=jQuery('#<portlet:namespace />id_seguimiento_codigo').val();
		if(idDetalle!=null){
			jQuery('#<portlet:namespace />agregandoCodigosSeguimientos').show();
			
			var codigoSeguimiento=jQuery('#<portlet:namespace />codigoSeguimiento').val();
			var descripcionSeguimiento=jQuery('#<portlet:namespace />descripcionSeguimiento').val();
			var tipoNomenclador=jQuery('#<portlet:namespace />tipoNomenclador').val();
			
			if(codigoSeguimiento!=null && codigoSeguimiento!=""){
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_seguimientoSurCodigoNomenclador'
					+	'&<%= Constants.CMD%>=' 
				url += '<%=Constants.ADD%>'
				
				url +=
	              '&codigo=' + encodeURI(codigoSeguimiento)
				+ '&descripcion=' + encodeURI(descripcionSeguimiento)
				+ '&tiponomenclador=' + encodeURI(tipoNomenclador)
				+ '&iddetalle=' + encodeURI(idSeguimientoCodigo)
				+ '&idseguimientocodigo=' + encodeURI(idSeguimientoCodigo)
				+ '&esEdicion=' +"<%=esEdicion%>"; 	
				
				jQuery('#<portlet:namespace />codigosseguimientossur').load(url, function() {
															jQuery('#<portlet:namespace />agregandoCodigosSeguimientos').hide();
															<portlet:namespace />limpiarNomencladorAutocompletar();
											   }
				 );
			}else{
				jQuery('#<portlet:namespace />agregandoCodigosSeguimientos').hide();
				<portlet:namespace />limpiarNomencladorAutocompletar();
			}	
	  }
		
	}

	function borraSeguimientoSurCodigoNomenclador(idMod){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_seguimientoSurCodigoNomenclador'
			+	'&<%= Constants.CMD%>=' + '<%=Constants.DELETE%>'
			+ '&detalleid=' + encodeURI(idMod)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			jQuery('#<portlet:namespace />codigosseguimientossur').load(url, function() {}	 );
	}
	
</script>