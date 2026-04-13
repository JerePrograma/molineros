<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
	String paramMes=null;
	String paramAnio=null;				
	Calendar periodoDesde = Calendar.getInstance(); 		
	int anioDesde=periodoDesde.get(Calendar.YEAR);
%>


<portlet:defineObjects/>
	
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="titulo_subir_paritarias" /></legend>
				<table class="lfr-table">
					<tr>
					<td>
					<td><liferay-ui:message key="fecha_inicio_paritaria" />:&nbsp;</td>
					<td><liferay-ui:input-date
						dayParam="fechaDesdeDia"
						dayNullable="<%= true %>" 
						dayValue="01" 
						monthNullable="<%= true %>" 
						monthParam="fechaDesdeMes"
						monthValue="<%= periodoDesde.get(Calendar.MONTH) %>"
						yearNullable="<%= true %>" 
						yearParam="fechaDesdeAnio"
						yearValue="<%= periodoDesde.get(Calendar.YEAR) %>"
						yearRangeStart="<%= periodoDesde.get(Calendar.YEAR)-5 %>"
						yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) +1 %>"
						firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek()%>"
						/> 			
				</td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="camara" />:</label></td>
						<td><select name="<portlet:namespace/>nombre_camara" id="<portlet:namespace/>nombre_camara">
						<option value="0">Seleccione Camara</option>
						<%for(int i = 0; i < WebKeysUOMA.CAMARA.length; i++ ) {%>
			               <option value="<%=WebKeysUOMA.CAMARA[i][0] %>" 
					      > <%=WebKeysUOMA.CAMARA[i][1] %> </option>
				        <%}%>
						</select></td>		
						<td>&nbsp;</td>
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>							
						</td>
						<td>
							
						    <input type="button" value="<liferay-ui:message key="alta-paritaria" />" onClick="<portlet:namespace />altaParitaria();" />
						
						</td>		
					</tr>
					<tr>
						<td colspan="14">&nbsp;</td>
					</tr>
				</table>	      	  
		</fieldset>	
		<fieldset class="block-labels">
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
			<div align="center" id="<portlet:namespace />busquedaParitariaDiv">						
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />fechaDesdeDia').hide();	
	jQuery('#<portlet:namespace />buscando').hide();	
	jQuery('#<portlet:namespace />buscar').click(function(){
		
		var fechaDesdeMes =jQuery('#<portlet:namespace />fechaDesdeMes').val(); 
		var fechaDesdeAnio =jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var camara_select =jQuery('#<portlet:namespace />nombre_camara').val();
	
		jQuery('#<portlet:namespace />buscando').show();
		if (<portlet:namespace />validarCampos(fechaDesdeMes, fechaDesdeAnio,camara_select)) {
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_paritarias&nombre_camara='+camara_select+
			'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio;
		}
		jQuery('#<portlet:namespace />buscando').hide()
		jQuery('#<portlet:namespace />busquedaParitariaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
	});

	function <portlet:namespace />altaParitaria() {
		
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/alta_ver_paritaria" />
		<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.ADD%>" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}     
	
	function <portlet:namespace />validarCampos(fechaDesdeMes, fechaDesdeAnio,camara_select) {
		if((fechaDesdeMes.length == 0 || fechaDesdeAnio.length == 0) &&   camara_select == 0  ){
			   alert("Debe seleccionar por lo menos un filtro");
			   return false;
			}
		return true;
	}
	
</script>
