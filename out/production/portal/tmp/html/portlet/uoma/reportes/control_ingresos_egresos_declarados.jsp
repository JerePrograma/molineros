<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.beans.ConvenioNacion" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
		String portlet_name = ParamUtil.getString(request, "portlet_name");
	
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "tesoreria";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		}
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}  
		
		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
		
		List<ConvenioNacion> convenioNac = (List<ConvenioNacion>) TraeListasServiceUtil.getConvenioNac();
%>
<form action="" method="get" name="<portlet:namespace />fmD" enctype="multipart/form-data"> 
		<fieldset class="block-labels">
				<legend>Panel de Control EMPRESAS</legend>
				<table class="lfr-table">
				  <tr>
				   <td>
				     <table class="lfr-table">
					  <tr>
						<td><label><liferay-ui:message key="periodo" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaDesdeDiaD"
							dayValue="1" 
							monthParam="fechaDesdeMesD"
							monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
							yearParam="fechaInicioAnioD"
							yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 50 %>"
							yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 50%>"
							firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
				   
					     <td>
						   	&nbsp;
					     </td>
					     
					     <td><label><liferay-ui:message key="tipo-boleta" />:</label></td>
						 <td>
							<select  name="<portlet:namespace/>tipoBoleta" id="<portlet:namespace/>tipoBoleta" type="text" value=''>
							    <option value="0" selected>Seleccione</option>
	 							<option value="2">Cuota Social UOMA</option>
	 							<option value="3">Cuota Usufructo</option>
	 							<option value="4">Art.46</option>
	 							<option value="5">Aporte Solidario UOMA</option>
							</select>
						</td>
					     
					     
					     
					     <tr>
					        <td>&nbsp;</td>
					     </tr>
					     <tr>							
						   <td colspan="4">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
					  		    <liferay-util:param name="esEditable" value='true'/>
					  		    <liferay-util:param name="portlet_name" value='uoma'/>
						   </liferay-util:include>
						   </td>
					     </tr>
						 <tr>
					        <td>&nbsp;</td>
					      </tr>	
					 	  <td>
							     <input id="<portlet:namespace />reporte_declarados" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
						  </td>
					  </tr>
				  </table>
				 </td>
				</tr> 
			  </table>	  	      	  
		</fieldset>	
		
		
		<fieldset class="block-labels">
			<div align="center" id="<portlet:namespace />buscando_">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
			</div>	
		</fieldset>
		
		<table>
		  <tr>
		     <td valign="top">
		        <div align="center" id="<portlet:namespace />divControlIngresosEgresosDeclarados" style="border: 1px solid"> 
		          <jsp:include page='/html/portlet/uoma/reportes/control_ingresos_egresos_declarados_result.jsp' />  
		        </div>
		     </td>
		      <td valign="top">
		          <div align="center" id="<portlet:namespace />divControlIngresosEgresosCtaCte" style="border: 1px solid"> 
		           
		          </div>
		      </td>
		  </tr>
		</table>
</form>			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando_').hide();	
jQuery('#<portlet:namespace />fechaDesdeDiaD').hide();

jQuery('#<portlet:namespace />reporte_declarados').click(function(){
	jQuery('#<portlet:namespace />buscando_').show();
	
	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaD");
	var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesD");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnioD");
	var tipoBoleta=document.getElementById("<portlet:namespace/>tipoBoleta");
	var cuit=jQuery("#<portlet:namespace />cuit_entidad").val();
	
 	var busquedaNom = {"cmd":"declarados_nivel_0","fechaDesdeDia":fechaDesdeDia.value,"fechaDesdeMes":fechaDesdeMes.value,"fechaDesdeAnio":fechaDesdeAnio.value,"cuit":cuit,"tipoBoleta":tipoBoleta.value};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/uoma/controlIngresosEgresosExplosion" /></portlet:renderURL>';

	jQuery('#<portlet:namespace />divControlIngresosEgresosDeclarados').load(url,busquedaNom, function(){
								jQuery('#<portlet:namespace />buscando_').hide();
	});	
				
	
});




</script>
