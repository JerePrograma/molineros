<%@ include file="/html/portlet/crm/init.jsp" %>

<portlet:defineObjects/>

<%
		response.setHeader("Cache-Control","no-store"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
 		
 		/* boolean showABMCrm = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM); */
 		boolean showCrmAuditoria = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_CRM_Auditoria);

 		session.removeAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
 		session.removeAttribute("cmd");
		
		Calendar fechaDesde = Calendar.getInstance(); 		
		Calendar fechaHasta = Calendar.getInstance();
		fechaDesde.setTime(new Date());
		fechaHasta.setTime(new Date());
		
		
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="grupo-filtro-busqueda-contacto-crm" /></legend>				
				<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
					<tr>
						<td><label>Fecha Desde:</label></td>
						<td><liferay-ui:input-date dayParam="fechaDesdeDiaEstad"
							dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
							dayNullable="<%= true %>" monthParam="fechaDesdeMesEstad"
							monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>" yearParam="fechaDesdeAnioEstad"
							yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" /></td>			
							
						<td><label>Fecha Hasta:</label></td>
						<td><liferay-ui:input-date dayParam="fechaHastaDiaEstad"
							dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
							dayNullable="<%= true %>" monthParam="fechaHastaMesEstad"
							monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>" yearParam="fechaHastaAnioEstad"
							yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 10 %>"
							firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" /></td>			
					</tr>
					<tr>
						<td colspan="1">
							<liferay-ui:message key="crm-contactos-estadisticas" />			
						</td>
						<td colspan="1">	
							 <select id="<portlet:namespace />tipo_estadistica">
								  <!-- <optgroup label="Swedish Cars"> -->
								    <option value="agrupado"><liferay-ui:message key="crm-estadistica-categ" /></option>
								    <option value="abiertoCerrado"><liferay-ui:message key="crm-estadistica-rendim" /></option>
								    <option value="cierre"><liferay-ui:message key="crm-estadistica-cierre" /></option>
								  <!-- </optgroup> -->
							</select> 
						
						</td>
						<td colspan="1">&nbsp;</td>
						<td colspan="1">
							<input type="button" id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" 
									onClick="<portlet:namespace />estadisticaXLS();" />
						</td>			 		
					</tr>				
			</table>	      	  
		</fieldset>

			
<script type="text/javascript">
	
	function <portlet:namespace />estadisticaXLS(){

		var tipo_estad=jQuery('#<portlet:namespace />tipo_estadistica').val();

		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaEstad').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesEstad').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioEstad').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDiaEstad').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMesEstad').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnioEstad').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;

	    if(tipo_estad == 'agrupado' ){
			window.location.href ='/xlsservlet/?reporte=ESTADISTICA_AGRUPADO_CONTACTOSCRM'
			  				+'&fechaDesdeFinal='+fechaDesdeFinal
			  				+'&fechaHastaFinal='+fechaHastaFinal;
	    } else if(tipo_estad == 'abiertoCerrado' ){	
	    	window.location.href ='/xlsservlet/?reporte=ESTADISTICA_RENDIMIENTO_CONTACTOSCRM'
  				+'&fechaDesdeFinal='+fechaDesdeFinal
  				+'&fechaHastaFinal='+fechaHastaFinal;
	    } else {
	    	window.location.href ='/xlsservlet/?reporte=ESTADISTICA_CIERRES_CONTACTOSCRM'
  				+'&fechaDesdeFinal='+fechaDesdeFinal
  				+'&fechaHastaFinal='+fechaHastaFinal;
	    }
		
	}	

</script>
