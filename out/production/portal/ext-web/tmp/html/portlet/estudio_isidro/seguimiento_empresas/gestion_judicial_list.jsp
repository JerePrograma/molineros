<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%

	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "estudio_isidro";
	}else if(renderResponse.getNamespace().equals("_EST_1_")){
		portlet_name = "estudio_isidro";
	}
	//List<ClaseBase>sectores = (List<ClaseBase>)ComprobanteServiceUtil.getSectoresByUser(user.getScreenName());
	String usuario=user.getScreenName();
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	fecha.setTime(new Date());


 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysEstudioIsidro.ROL_SEGUIMIENTO_EMPRESAS_JUDICIALES);
 		
 		String esEditableStr = ParamUtil.getString(request, "esEditable");
 		if (esEditableStr == null || esEditableStr.equals("false")){
 			esEditableStr ="false";
 		}
 		boolean esEditable = Boolean.parseBoolean(esEditableStr);
 		
 		String[] tiposStr = TraeListasServiceUtil.getSystemConfig("GESTION_JUDICIAL_TIPOS_DEMANDA").split(";");
 		List<ClaseBase> tipos =new ArrayList<ClaseBase>();
 		for(int i=0;i<=tiposStr.length-1;i++){
 			ClaseBase c =new ClaseBase();
 			String codigo = tiposStr[i].split("=")[0];
 			String descripcion = tiposStr[i].split("=")[1];
 			c.setId(codigo);
 			c.setDescripcion(descripcion);
 			tipos.add(c);
 		}
 		

 		String[] estadosStr = TraeListasServiceUtil.getSystemConfig("GESTION_JUDICIAL_TIPOS_ESTADOS").split(";");
 			List<ClaseBase> estados =new ArrayList<ClaseBase>();
 			for(int i=0;i<=estadosStr.length-1;i++){
 				ClaseBase c =new ClaseBase();
 				String codigo = estadosStr[i].split("=")[0];
 				String descripcion = estadosStr[i].split("=")[1];
 				c.setId(codigo);
 				c.setDescripcion(descripcion);
 				estados.add(c);
 		}
%>
<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
		<fieldset class="block-labels">
		<legend>Gestiones Judiciales</legend>
		<table width="70%" class="lfr-table">
			<tr>
			
			  <td>
				 <label>Id:</label>
			  </td>
			  <td>
				<input type="text" id="<portlet:namespace />id" name="<portlet:namespace />id" value="" 
				  maxlength="20"  size="20"/>
			  </td>
			  
			  <td>
				 <label>Expediente:</label>
			  </td>
			  <td>
				<input type="text" id="<portlet:namespace />nro_expediente" name="<portlet:namespace />nro_expediente" value="" 
				  maxlength="125"  size="50"/>
			  </td>
			  
			  <td>
				 <label>Carátula:</label>
			  </td>
			  <td>
				<input type="text" id="<portlet:namespace />caratula" name="<portlet:namespace />caratula" value="" maxlength="500"  size="90"/>
			  </td>
			</tr>
			
			<tr><td colspan="8">&nbsp;</td></tr>
			
			<tr>
			 <td><label><liferay-ui:message key="tipo" />:</label></td>
			  <td>
					<select id="<portlet:namespace />tipo" name="<portlet:namespace />tipo">
						<option value="">Todos</option>		
						<%for(ClaseBase c:tipos) {%>
						<option	value="<%= c.getId() %>"><%=c.getDescripcion() %>
						</option>
						<% } %>				
					</select>
			  </td>
			  <td><label><liferay-ui:message key="entidad" />:</label></td>
			  <td>
				<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">	
				   <option selected value="">Todas</option>	
				   <option value="U">U.O.M.A.</option>	
				   <option value="O">O.S.P.I.M.</option>
				   <option value="A">A.M.T.I.M.A.</option>													
				</select>						
			  </td>	
			  
			  <td><label>Estado:</label></td>
			  <td>
				<select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
						<option value="">Todos</option>	
						<%for(ClaseBase c:estados) {%>
						<option	value="<%= c.getId() %>"><%=c.getDescripcion() %></option>
						<% } %>	
				</select>
			  </td>
			 
			</tr>
			
			<tr><td colspan="8">&nbsp;</td></tr>
		    <tr><td colspan="8">&nbsp;</td></tr>
				<tr>	
					<td>
						<label><liferay-ui:message key="fecha-alta" /> Desde:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaDiaDde"
						monthParam="fechaMesDde"
						yearParam="fechaAnioDde" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
					<td>
						<label><liferay-ui:message key="fecha-alta" /> Hasta:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date
						monthNullable="true" 
						dayNullable="true"
						yearNullable="true"
						dayParam="fechaDiaHta"
						monthParam="fechaMesHta"
						yearParam="fechaAnioHta" 
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 10 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
		</table>
		
        <table>
		  <tr>
			<td>
				<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
									  		<liferay-util:param name="esEditable" value='true'/>						  		
									  		<liferay-util:param name="cuit" value=''/>
									  		<liferay-util:param name="sucu" value='000'/>
									  		<liferay-util:param name="suf_entidad" value='_dem'/>
									  		<liferay-util:param name="suf" value='_dem'/>
				</liferay-util:include>
			</td>
		  </tr>
		</table>			
		
			
		<table>		
				<tr>
					<td>
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"
						onclick="javascript:<portlet:namespace />buscarDemandas();"/>							
					</td>
					<td>
					&nbsp;
					</td>	
					<td>
						<input id="<portlet:namespace />limpiar" value="Limpiar Filtro" title="<liferay-ui:message key="limpiar" />" type="button"
						onclick="javascript:<portlet:namespace />limpiarFiltro();"/>							
					</td>
					<td>
					&nbsp;
					</td>
					
					
					
					<td colspan="6">&nbsp;</td>	
					<td>
					<input type="button" value="Reporte" onClick="<portlet:namespace />reporteDemandas();"/>&nbsp;
					</td>
					<c:if test="<%= showABMButtons %>">
					<td>
					  <input type="button" value="Nuevo" onClick="<portlet:namespace />nuevaDemanda();"/>&nbsp;
					</td>  
					</c:if>
					
				</tr>
		</table>				
		</fieldset>
		<div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
		</div>	
		<fieldset class="block-labels">
			<legend>
					<label>Demandas:</label>
			</legend>
		
			<div align="center" id="<portlet:namespace />busquedaDemandasDiv">
				<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_search_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEditable)%>" />
				</liferay-util:include>
			</div>	
		</fieldset>

</form>			
<script type="text/javascript">
    
jQuery('#<portlet:namespace />buscando').hide();
        
function <portlet:namespace />nuevaDemanda() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>" +"&accion=" + "<%= Constants.WRITE %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/demandas_editar" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
 		submitForm(document.<portlet:namespace />fm, url);	
}    
	
function <portlet:namespace />buscarDemandas(){
	
	            var id=jQuery('#<portlet:namespace />id').val();
				var cuit=jQuery('#<portlet:namespace />cuit_entidad_dem').val();
				var sucursal=jQuery('#<portlet:namespace />sucursal_entidad_dem').val();
				var expediente=jQuery('#<portlet:namespace />nro_expediente').val();
				var caratula=jQuery('#<portlet:namespace />caratula').val();
				var tipo=jQuery('#<portlet:namespace />tipo').val();
				var entidad=jQuery('#<portlet:namespace />entidad').val();
				var fechaDiaDde=jQuery('#<portlet:namespace />fechaDiaDde').val();
				var fechaMesDde=jQuery('#<portlet:namespace />fechaMesDde').val();
				var fechaAnioDde=jQuery('#<portlet:namespace />fechaAnioDde').val();
				var fechaDiaHta=jQuery('#<portlet:namespace />fechaDiaHta').val();
				var fechaMesHta=jQuery('#<portlet:namespace />fechaMesHta').val();
				var fechaAnioHta=jQuery('#<portlet:namespace />fechaAnioHta').val();
				var estado=jQuery('#<portlet:namespace />estado').val();
				
				if (fechaDiaDde != "" || fechaMesDde != "" || fechaAnioDde != ""){
					if (fechaDiaDde == "" || fechaMesDde == "" || fechaAnioDde == ""){
						alert("Por favor seleccione todos los campos de la fecha Desde.");
						return false;
					}
				}
				
				if (fechaDiaHta != "" || fechaMesHta != "" || fechaAnioHta != ""){
					if (fechaDiaHta == "" || fechaMesHta == "" || fechaAnioHta == ""){
						alert("Por favor seleccione todos los campos de la fecha Hasta.");
						return false;
					}
				}
				
								
				var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();			
				jQuery("#pagina").val(pagina_sel);
				
				jQuery('#<portlet:namespace />buscando').show();
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
				url += '&cmd=buscar&id='+id+'&expediente='+expediente+'&caratula='+caratula+
				    '&tipo='+tipo+'&entidad='+entidad + 
				    '&fechaDiaDde='+fechaDiaDde+'&fechaMesDde='+fechaMesDde+
				    '&fechaAnioDde='+fechaAnioDde;
				url+='&fechaDiaHta='+fechaDiaHta+'&fechaMesHta='+fechaMesHta+
				    '&fechaAnioHta='+fechaAnioHta+   
					'&cuit=' + cuit + '&sucursal=' + sucursal;
				url += '&estado='+estado;
				url += '&pagina='+pagina_sel;
				
				url += '&rnd=' + Math.floor(Math.random()*100);
				
				jQuery('#<portlet:namespace />busquedaDemandasDiv').load(url, function() {
		        		jQuery('#<portlet:namespace />buscando').hide();
					}
		        );
};
			
			


function <portlet:namespace />limpiarFiltro(){		
	jQuery('#<portlet:namespace />cuit_entidad_dem').val('');
	jQuery('#<portlet:namespace />sucursal_entidad_dem').val('');
	jQuery('#<portlet:namespace />entidad_dem').val('');
	
	jQuery('#<portlet:namespace />id').val('');
	jQuery('#<portlet:namespace />nro_expediente').val('');		
	jQuery('#<portlet:namespace />caratula').val('');
	jQuery('#<portlet:namespace />tipo').val('');
	jQuery('#<portlet:namespace />entidad').val('');
	jQuery('#<portlet:namespace />estado').val('');
	
	jQuery('#<portlet:namespace />fechaDiaDde').val('');
	jQuery('#<portlet:namespace />fechaMesDde').val('');
	jQuery('#<portlet:namespace />fechaAnioDde').val('');
	
	jQuery('#<portlet:namespace />fechaDiaHta').val('');
	jQuery('#<portlet:namespace />fechaMesHta').val('');
	jQuery('#<portlet:namespace />fechaAnioHta').val('');

		
	
};



function <portlet:namespace />reporteDemandas(){
	
    var id=jQuery('#<portlet:namespace />id').val();
	var cuit=jQuery('#<portlet:namespace />cuit_entidad_dem').val();
	var sucursal=jQuery('#<portlet:namespace />sucursal_entidad_dem').val();
	var expediente=jQuery('#<portlet:namespace />nro_expediente').val();
	var caratula=jQuery('#<portlet:namespace />caratula').val();
	var tipo=jQuery('#<portlet:namespace />tipo').val();
	var entidad=jQuery('#<portlet:namespace />entidad').val();
	var fechaDiaDde=jQuery('#<portlet:namespace />fechaDiaDde').val();
	var fechaMesDde=jQuery('#<portlet:namespace />fechaMesDde').val();
	var fechaAnioDde=jQuery('#<portlet:namespace />fechaAnioDde').val();
	var fechaDiaHta=jQuery('#<portlet:namespace />fechaDiaHta').val();
	var fechaMesHta=jQuery('#<portlet:namespace />fechaMesHta').val();
	var fechaAnioHta=jQuery('#<portlet:namespace />fechaAnioHta').val();
	var estado=jQuery('#<portlet:namespace />estado').val();
	
	if (fechaDiaDde != "" || fechaMesDde != "" || fechaAnioDde != ""){
		if (fechaDiaDde == "" || fechaMesDde == "" || fechaAnioDde == ""){
			alert("Por favor seleccione todos los campos de la fecha Desde.");
			return false;
		}
	}
	
	if (fechaDiaHta != "" || fechaMesHta != "" || fechaAnioHta != ""){
		if (fechaDiaHta == "" || fechaMesHta == "" || fechaAnioHta == ""){
			alert("Por favor seleccione todos los campos de la fecha Hasta.");
			return false;
		}
	}
	
	var url = '/xlsservlet/?reporte=REPORTE_DEMANDAS';				
	url += '&id='+id+'&expediente='+expediente+'&caratula='+caratula+
	    '&tipo='+tipo+'&entidad='+entidad + 
	    '&fechaDiaDde='+fechaDiaDde+'&fechaMesDde='+fechaMesDde+
	    '&fechaAnioDde='+fechaAnioDde;
	url+='&fechaDiaHta='+fechaDiaHta+'&fechaMesHta='+fechaMesHta+
	    '&fechaAnioHta='+fechaAnioHta+   
		'&cuit=' + cuit + '&sucursal=' + sucursal;
	url += '&estado='+estado;
	window.location.href =url;
	
};

	
</script>
