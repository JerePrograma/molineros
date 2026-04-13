<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.beans.Empresa"%>
<portlet:defineObjects/>

<%

		String portlet_name = "uoma";
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 
 		
 		Calendar fechamesDDJJ= CalendarFactoryUtil.getCalendar();
 		fechamesDDJJ.setTime(new Date()); 
 		fechamesDDJJ.add(Calendar.MONTH,-1);
 		
 		Empresa empresa =(Empresa)request.getSession().getAttribute("BOLETA_EMPLEADORES_EMPRESA");
 		
%>	
		<fieldset class="block-labels">
				<legend>
					Reimputación de Pagos de Boletas
				</legend>
				<table class="lfr-table">			
					<tr>	
						<td><label><liferay-ui:message key="empresa" />:</label></td>
						<td colspan="6">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
						  		<liferay-util:param name="esEditable" value='true'/>
						  		<liferay-util:param name="portlet_name" value='<%=portlet_name%>'/>
						  		<liferay-util:param name="cuit" value='<%=empresa!=null?empresa.getCuit():"" %>'/>
						  		<liferay-util:param name="sucu" value='<%=empresa!=null?empresa.getSucursal():"" %>'/>
						  		<liferay-util:param name="buscar_destino" value='true'/>
						  		<liferay-util:param name="razon" value='<%=empresa!=null?empresa.getRazon_soc():"" %>'/>
							</liferay-util:include>
						</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="fecha-recaudacion-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue=""
								dayNullable="<%= true %>"
								monthParam="fechaDesdeMes"
								monthValue="-1"
								monthNullable="<%= true %>"
								yearParam="fechaDesdeAnio"
								yearValue=""
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-recaudacion-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaHastaDia"																					
								dayValue=""
								dayNullable="<%= true %>"
								monthParam="fechaHastaMes"
								monthValue="-1"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnio"
								yearValue=""
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					
					    <td><label>Período DDJJ</label></td>
		                
			            
			            <td>
							<liferay-ui:input-date
								dayParam="periodoDesdeDia"																					
								dayValue=""
								dayNullable="<%= true %>"
								monthAndYearNullable="<%= true %>"
								monthAndYearParam="periodoDesdeMesAnio"
								monthValue="<%= fechamesDDJJ.get(Calendar.MONTH) %>"
								yearValue="<%= fechamesDDJJ.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechamesDDJJ.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechamesDDJJ.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechamesDDJJ.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
			            
			            <td>
					      <label>Nro de Boleta:</label>&nbsp;<input
					      id="<portlet:namespace />numero" name="<portlet:namespace />numero"
					      size="8" maxlength="8" type="text"
					      value="" />
					 </td>   
					</tr>						
					
					<tr>
						<td colspan="7">&nbsp;</td>
					</tr>
					<tr>
					   <td>
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="javascript:<portlet:namespace />filtrar();"/>							
					   </td>
						
						<td>
							<input id="<portlet:namespace />limpiar" value="Limpiar" title="Limpiar Filtros" type="button" onClick="javascript:<portlet:namespace />limpiarFiltro();"/>							
						</td>
						
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
						
			<div align="center" id="<portlet:namespace />busquedaCtaCteDiv">	
			    <jsp:include page='/html/portlet/uoma/cuentacorriente/empleadores_reimputacion_pagos_search_result.jsp' />					
			</div>
			
		</fieldset>
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
jQuery("#<portlet:namespace />periodoDesdeDia").hide();


function <portlet:namespace />filtrar(){
	
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
	var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
	var periodoDesdeMesAnio=jQuery("#<portlet:namespace />periodoDesdeMesAnio").val();
	var nro_boleta=jQuery("#<portlet:namespace/>numero").val();
	
	if(cuit_entidad==null || cuit_entidad=="" || sucursal_entidad==null || sucursal_entidad==""){
		alert("Debe seleccionar una EMPRESA");
		return false;
	}
	
	
	jQuery('#<portlet:namespace />buscando').show();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/empleadores_reimputacion_pagos&cuit_entidad='+cuit_entidad;
	 url +='&sucursal='+sucursal_entidad;
	 
	 url +='&desde_mes=' + desde_mes + '&desde_anio=' + desde_anio ; 
	 url += '&hasta_mes=' + hasta_mes + '&hasta_anio=' + hasta_anio ;
	 url += '&periodo='+periodoDesdeMesAnio + '&nro_boleta=' + nro_boleta;
	 url +='&cmd=filter';
	
	jQuery('#<portlet:namespace />busquedaCtaCteDiv').load(url, function() {
    	jQuery('#<portlet:namespace />buscando').hide();            															
      }
    );		
		
	
}

function <portlet:namespace />limpiarFiltro(){
	jQuery("#<portlet:namespace/>fechaDesdeDia").val('');	
	jQuery("#<portlet:namespace/>fechaDesdeMes").val('');
	jQuery("#<portlet:namespace/>fechaDesdeAnio").val('');
	jQuery("#<portlet:namespace/>fechaHastaDia").val('');	
	jQuery("#<portlet:namespace/>fechaHastaMes").val('');
	jQuery("#<portlet:namespace/>fechaHastaAnio").val('');
	jQuery("#<portlet:namespace/>cuit_entidad").val('');	
	jQuery("#<portlet:namespace/>sucursal_entidad").val('');
	jQuery("#<portlet:namespace/>numero").val('');
	jQuery("#<portlet:namespace/>entidad").val('');
	return true;
	
}


</script>