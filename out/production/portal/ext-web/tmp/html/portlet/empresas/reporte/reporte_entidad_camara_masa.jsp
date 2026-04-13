<%@ include file="/html/portlet/empresas/init.jsp" %>

<%	
	ReporteEntidadCamaraMasaBean reporte = (ReporteEntidadCamaraMasaBean) request.getAttribute(WebKeysEmpresas.REPORTE_ENTIDAD_CAMARA_MASA);
	Empresa empresa = (Empresa)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);
	
	HashMap<String, ReporteEntidadCamaraMasaBean.ItemReporte> itemHM=null;
	if(null!=reporte && null!= reporte.getItems() && reporte.getItems().size()>0 ){
		itemHM=reporte.getItems();		
	}
%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key="empleados-entidad-remuneracion-masa" /></legend>
	<table width="100%">
<%	
	if(null!=itemHM){
%>		
	<tr>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="tipo"/></b>
		</td >
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="periodo"/></b>
		</td>
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="ospim"/></b>
		</td>	
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="uoma"/></b>
		</td>		
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="amtima"/></b>
		</td>
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="total-empresa"/></b>
		</td>												
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="reportes"/></b>
		</td>																	
	</tr>
	<tr>
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			&nbsp;
		</td >
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cantidad"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="remuneracion"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cantidad"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="remuneracion"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cantidad"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="remuneracion"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cantidad"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="remuneracion"/></b>
		</td>		
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="por-aporte"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="nomina"/></b>
		</td>
																															
	</tr>
	<% ReporteEntidadCamaraMasaBean.ItemReporte itemUOMA=itemHM.get(WebKeysGlobal.ENTIDAD_UOMA);
	   ReporteEntidadCamaraMasaBean.ItemReporte itemAMTIMA=itemHM.get(WebKeysGlobal.ENTIDAD_AMTIMA);
	   if(null!=itemUOMA||null!=itemAMTIMA){%>
	<tr>		
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0">
				Portal Empleadores
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<%=reporte.getPeriodoAsString()%>				
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				----------
			</td>	
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				----------
			</td>		
			
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<%=null!=itemUOMA?itemUOMA.getCantidad():""%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				<%=itemUOMA.getRemuneracion()!=null?itemUOMA.getRemuneracion().toString():""%>
			</td>		
		
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<%=null!=itemAMTIMA?itemAMTIMA.getCantidad():""%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				<%=null!=itemAMTIMA &&itemAMTIMA.getRemuneracion()!=null?itemAMTIMA.getRemuneracion().toString():""%>
			</td>
		<%ReporteEntidadCamaraMasaBean.ItemReporte item=itemHM.get("TOTAL_EMPRESA"); %>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<%=null!=item?item.getCantidad():""%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				<%=null!=item && item.getRemuneracion()!=null?item.getRemuneracion().toString():""%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<a href="javascript:reporteCuotasEmple();">Ver</a>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				  <%if(null!=reporte && reporte.getPeriodosEmpleadores()!=null){
			      String[] splittedSting = reporte.getPeriodosEmpleadores().split("-");%>
			      <select id="periodo_emple" name="periodo_emple">
			      <% for (int x=0; x<splittedSting.length; x++) {%>
			      	<option value='<%=splittedSting[x]%>'><%=splittedSting[x]%></option>
			      <%} 
			      }%>  
				  </select>
				<a href="javascript:reporteNominaEmple();">Ver</a>
			</td>					
	</tr>	
	<%}
	   ReporteEntidadCamaraMasaBean.ItemReporte item=itemHM.get(WebKeysGlobal.ENTIDAD_OSPIM); 
	   if(null!=item){%>
	<tr>		
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed">
				AFIP
			</td>
			
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed" >
				<%=item.getPeriodoAsString()%>				
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed" >
				<%=null!=item?item.getCantidad():""%>			
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed" >
				<%=null!=item && null!= item.getRemuneracion()?item.getRemuneracion().toString():""%>				
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed">
				----------
			</td>	
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed">
				----------
			</td>	
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed">
				----------
			</td>	
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed">
				----------
			</td>	
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed" >
				<%=null!=item?item.getCantidad():""%>			
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #ededed" >
				<%=null!=item && null!= item.getRemuneracion()?item.getRemuneracion().toString():""%>				
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				----------
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				  <%if(null!=reporte && reporte.getPeriodosAfip()!=null){
			      String[] splittedSting = reporte.getPeriodosAfip().split("-");%>
			      <select id="periodo_afip" name="periodo_afip">
			      <% for (int x=0; x<splittedSting.length; x++) {%>
			      	<option value='<%=splittedSting[x]%>'><%=splittedSting[x]%></option>
			      <%} 
			      }%>  
				  </select>
				<a href="javascript:reporteNominaAfip();">Ver</a>
			</td>		
	</tr>
	<%} 
	item=itemHM.get("OSPIM_PORTAL"); 
	if(null!=item){%>
	<tr>		
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0">
				Portal Molineros
			</td>
			
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				<%=reporte.getPeriodoAsString()%>				
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				<%=null!=item?item.getCantidad():""%>				
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				----------
			</td>
			<%item=itemHM.get("UOMA_PORTAL"); %>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				<%=null!=item?item.getCantidad():""%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				----------
			</td>
			<%item=itemHM.get("AMTIMA_PORTAL"); %>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				<%=null!=item?item.getCantidad():""%>				
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				----------
			</td>
			
			<%item=itemHM.get("TOTAL_PORTAL"); %>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				<%=null!=item?item.getCantidad():""%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0" >
				----------
			</td>			
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				----------
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				  ----------
			</td>
	</tr>
	<%} %>
				
<%}else{ %>
<tr><td>Empresa sin datos de DDJJ<td></tr>
<%}%>
</table>	
</fieldset>
<%	
	if(null!=itemHM && (null!= itemHM.get(WebKeysGlobal.FAIM) || null!= itemHM.get(WebKeysGlobal.CAENA) || null!=itemHM.get(WebKeysGlobal.CEPA))){
%>	
<fieldset class="block-labels">
	<legend><liferay-ui:message key="empleados-camara" /></legend>
	<table width="100%">	
	<tr>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="periodo"/></b>
		</td >		
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="faim"/></b>
		</td>	
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cepa"/></b>
		</td>		
		<td colspan="2" style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="caena"/></b>
		</td>																															
	</tr>
	<tr>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			&nbsp;
		</td>		
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cantidad"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="remuneracion"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cantidad"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="remuneracion"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cantidad"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="remuneracion"/></b>
		</td>																															
	</tr>
	<% ReporteEntidadCamaraMasaBean.ItemReporte item=itemHM.get(WebKeysGlobal.FAIM);%>
	<tr>	
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<%=reporte.getPeriodoAsString()%>				
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<%=null!=item?item.getCantidad():"--------"%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				<%=null!=item && null!=item.getRemuneracion()?item.getRemuneracion().toString():"--------"%>
			</td>		
	<% item=itemHM.get(WebKeysGlobal.CEPA);%>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<%=null!=item?item.getCantidad():"--------"%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				<%=null!=item && item.getRemuneracion()!=null?item.getRemuneracion().toString():"--------"%>
			</td>
	<% item=itemHM.get(WebKeysGlobal.CAENA);%>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
				<%=null!=item?item.getCantidad():""%>
			</td>
			<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
				<%=null!=item && item.getRemuneracion()!=null?item.getRemuneracion().toString():"--------"%>
			</td>					
	</tr>					
</table>	
</fieldset>
<%}else if(null!=itemHM){ %>
<table>
<tr><td>Empresa sin datos de DDJJ<td></tr>
</table>
<%}%>

<script type="text/javascript">
function reporteCuotasEmple(){	
	jQuery('#<portlet:namespace />buscando').show();
    window.location.href ='/xlsservlet/?reporte=REPORTE_BOLETA_PORTAL_EMPLEADORES'+'&periodoDesdeDia=01&periodoDesdeMes=01&periodoDesdeAnio=1900'+
    '&periodoHastaDia=01&periodoHastaMes=01&periodoHastaAnio=2999&fechaRecDesdeDia=01&fechaRecDesdeMes=01&fechaRecDesdeAnio=1900'+
    '&fechaRecHastaDia=01&fechaRecHastaMes=01&fechaRecHastaAnio=2999&fechaRenDesdeMes=01&fechaRenDesdeAnio=1999&fechaRenHastaDia=01'+
	'&fechaRenHastaMes=01&fechaRenDesdeDia=01&cuit_entidad=<%=empresa.getCuit()%>&consolidado=true&cruce_ddjj_os=false&solo_ddjj=true';
}
function reporteNominaEmple(){	
	var periodo=jQuery("#periodo_emple").val().trim();
	var mes=periodo.substring(0,2)-1;
	var anio=periodo.substring(3,7);	
	jQuery('#<portlet:namespace />buscando').show();
    window.location.href ='/xlsservlet/?reporte=REPORTE_BOLETA_PORTAL_EMPLEADORES'+'&periodoDesdeDia=01&periodoDesdeMes='+mes+'&periodoDesdeAnio='+anio+
    '&periodoHastaDia=01&periodoHastaMes='+mes+'&periodoHastaAnio='+anio+'&fechaRecDesdeDia=01&fechaRecDesdeMes=01&fechaRecDesdeAnio=1900'+
    '&fechaRecHastaDia=01&fechaRecHastaMes=01&fechaRecHastaAnio=2999&fechaRenDesdeMes=01&fechaRenDesdeAnio=1999&fechaRenHastaDia=01'+
	'&fechaRenHastaMes=01&fechaRenDesdeDia=01&cuit_entidad=<%=empresa.getCuit()%>&consolidado=false&cruce_ddjj_os=false&solo_ddjj=true';
}
function reporteNominaAfip(){
		var periodo=jQuery("#periodo_afip").val().trim();
		var mes=periodo.substring(0,2)-1;
		var anio=periodo.substring(3,7);
		var fechaDesdeDia  = '01'
		var fechaDesdeMes= periodo.substring(0,2)-1;
		var fechaDesdeAnio = periodo.substring(3,7);

		var fechaHastaDia = fechaDesdeDia;
		var fechaHastaMes = fechaDesdeMes;
		var fechaHastaAnio = fechaDesdeAnio;
		
		var cuit='<%=empresa.getCuit()%>';
		var cuil='';
				
		jQuery('#<portlet:namespace />buscando').show();
		window.location.href ='/xlsservlet/?reporte=APORTES_CONTRIBUCIONES_EMP'
			+'&cuit='+cuit
			+'&cuil='+cuil
			+'&fechaDesdeDia='+fechaDesdeDia
			+'&fechaDesdeMes='+fechaDesdeMes
			+'&fechaDesdeAnio='+fechaDesdeAnio
			+'&fechaHastaDia='+fechaHastaDia
			+'&fechaHastaMes='+fechaHastaMes
			+'&fechaHastaAnio='+fechaHastaAnio
			+'&formato_procesar=true';
	
}
</script>
