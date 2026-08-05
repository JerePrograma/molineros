<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
 
Calendar fecha = CalendarFactoryUtil.getCalendar();
fecha.setTime(new Date());
int diasDelMes = fecha.getActualMaximum(Calendar.DAY_OF_MONTH);

 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
%>
		<% if(showABMButtons) { %>
		<fieldset class="block-labels">
		<legend>Búsqueda Comprobantes de Transferencia de Interbanking</legend>
		<table width="70%">
				<tr>
					<td><label><liferay-ui:message key="acreedor" />:</label></td>
					<td colspan="7">
						<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					  		<liferay-util:param name="esEditable" value='true'/>
						</liferay-util:include>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
				
				<tr>
				   <td><label>Desde:</label></td>
				   <td colspan="2">
									<liferay-ui:input-date
                                            dayParam="fechaDesdeDiaFiltro"
                                            dayValue="<%= fecha.get(Calendar.DATE) %>"
                                            dayNullable="<%= false %>"
                                            monthParam="fechaDesdeMesFiltro"
                                            monthValue="<%= fecha.get(Calendar.MONTH) %>"
                                            monthNullable="<%= false %>"
                                            yearParam="fechaDesdeAnioFiltro"
                                            yearValue="<%= fecha.get(Calendar.YEAR)%>"
                                            yearNullable="<%= false %>"
                                            yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
                                            yearRangeEnd="<%= fecha.get(Calendar.YEAR)%>"
                                            firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
                                            disabled="false" />
				   </td>
				   
				   <td><label>Hasta:</label></td>
				   <td colspan="2">
									<liferay-ui:input-date
                                            dayParam="fechaHastaDiaFiltro"
                                            dayValue="<%= diasDelMes %>"
                                            dayNullable="<%= false %>"
                                            monthParam="fechaHastaMesFiltro"
                                            monthValue="<%= fecha.get(Calendar.MONTH) %>"
                                            monthNullable="<%= false %>"
                                            yearParam="fechaHastaAnioFiltro"
                                            yearValue="<%= fecha.get(Calendar.YEAR)%>"
                                            yearNullable="<%= false %>"
                                            yearRangeStart="<%= fecha.get(Calendar.YEAR) - 10 %>"
                                            yearRangeEnd="<%= fecha.get(Calendar.YEAR)%>"
                                            firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
                                            disabled="false" />
				   </td>
				
				</tr>
				
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"
						onclick="javascript:<portlet:namespace />buscarCptes();"/>							
					</td>
					<td>
				</tr>
			</table>				
		</fieldset>
		<%} %>
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
			<div align="center" id="<portlet:namespace />comprobantesResultDiv">
			</div>
		</fieldset>
			
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscando').hide();	
	//jQuery('#<portlet:namespace />buscar').click(function(){

    function <portlet:namespace />buscarCptes(){
		var cuit_acreedor = jQuery("#<portlet:namespace />cuit_entidad").val();    
	    var sucu_acreedor = jQuery("#<portlet:namespace />sucursal_entidad").val();
	    var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();	
	    
	    var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");
		//jQuery("#pagina").val(pagina_sel);
	    
		jQuery('#<portlet:namespace />buscando').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/comprobantes_consulta_interbanking';
		url += '&cmd=filter';
		url += '&cuit_entidad='+cuit_acreedor+'&pagina='+pagina_sel;
		url += "&fechadesdedia=" + fechaDesdeDia.value;
		url += "&fechadesdemes=" + fechaDesdeMes.value;
		url += "&fechadesdeanio="+ fechaDesdeAnio.value;
		url += "&fechahastadia=" + fechaHastaDia.value;
		url += "&fechahastames=" + fechaHastaMes.value;
		url += "&fechahastaanio="+ fechaHastaAnio.value;
		url += '&rnd=' + Math.floor(Math.random()*100);
		if((cuit_acreedor !=null && cuit_acreedor != "")){
		   jQuery('#<portlet:namespace />comprobantesResultDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();
        															  }
           );
	    }else{
	    	alert("Debe seleccionar un CUIT como parámetro de búsqueda");
	    	jQuery('#<portlet:namespace />buscando').hide();	
	    }   
	}
    
    function verImagenComprobante(folderId,fileName){
 	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
 	   '<liferay-portlet:param name="struts_action" value="/liquidaciones/documentacion_adjunta_recuperar"/>'+
 	   '<liferay-portlet:param name="name" value="__Name"/>'+
 	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
 	   '</liferay-portlet:actionURL>';      
 	   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
 	   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
  }
</script>
