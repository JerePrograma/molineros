<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.tesoreria.beans.AjustePlanSuperador" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.text.DecimalFormatSymbols" %>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
 	//PrecioPlanSuperador precio=(PrecioPlanSuperador)request.getSession().getAttribute(WebKeysTesoreria.PRECIO_EN_SESSION);
	List<AjustePlanSuperador> disponibles = (List<AjustePlanSuperador>)request.getSession().getAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_RESULT);
	Double importeCotizado	= ParamUtil.getDouble(request, "importeCotizado");
	String fechaCotizado  = ParamUtil.getString(request, "fechaCotizado", null);
	
	if(importeCotizado==null) importeCotizado=0D;
	AjustePlanSuperador ajusteSel = (AjustePlanSuperador)request.getSession().getAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_SELECCIONADO);
	if(ajusteSel==null) ajusteSel = new AjustePlanSuperador();
	Calendar fechaVigenciaDde = CalendarFactoryUtil.getCalendar();
	    if(ajusteSel==null || ajusteSel.getFechaDesde() ==null){
		    fechaVigenciaDde.setTime(new Date());
	    }else{
	    	fechaVigenciaDde.setTime(ajusteSel.getFechaDesde());
	    }  
		
	    Calendar fechaVigenciaHta = CalendarFactoryUtil.getCalendar();
	    if(ajusteSel==null || ajusteSel.getFechaHasta() ==null){
		    fechaVigenciaHta.setTime(new Date());
	    }else{
	    	fechaVigenciaHta.setTime(ajusteSel.getFechaHasta());
	    } 
	
	    Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
		fechaHasta.setTime(new Date());
		DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
		simbolos.setGroupingSeparator('.'); // Separador de miles
		simbolos.setDecimalSeparator(',');   // Separador de decimales
		DecimalFormat df = new DecimalFormat("#,##0.00", simbolos);
%>

	             <table class="lfr-table">
		                <thead >
		                <tr>
		                  <th style="background-color: #2c5e77;color:white">
		                   <label>Disponibles</label>
		                  </th>
		                  
		                  <th> </th>
		                  <th style="background-color: #2c5e77;color:white" colspan="4">
		                  <label>Asignados</label>
		                  </th>  
		                </tr>  
		                </thead>
		                <tbody>
		                <tr>
		                 <td style="vertical-align: top;">  <select name="<portlet:namespace />ajustes_disponibles"  id="<portlet:namespace />ajustes_disponibles"  width=300 style="width: 350px; height: 100px"  size="12">
						   <%for(AjustePlanSuperador p:disponibles) {%>
						    <option	value="<%=p.getId() %>"><%=p.getDescripcion() %></option>
						    <% } %>
	                       </select>
	                     </td>
	                     
	                     <td> 
                             <input id="<portlet:namespace />seleccion"  style="color:green" value="   >>   " title="Seleccionar"
		                      onClick="javascript: <portlet:namespace />seleccionarAjuste();" type="button" />
                        </td>
                        
                        
                        <td  colspan="4">
                             <table >
                               <tr>
                                 <td><label>Id: </label></td>
                                 <td>
                                 <input type="text" name="<portlet:namespace/>ajusteId" id="<portlet:namespace/>ajusteId"   value="<%= ajusteSel.getId() != null ? String.valueOf(ajusteSel.getId()) : "" %>"
                                      readonly size="10px"/>
                                 </td>     
                                 <td><label>Descripcin: </label></td>
                                 <td>
                                      <input type="text" name="<portlet:namespace/>ajusteDe" id="<portlet:namespace/>ajusteDe"  size="50px"
                                        value="<%= ajusteSel.getDescripcion() != null ? String.valueOf(ajusteSel.getDescripcion()) : "" %>" readonly/>
                                 </td>   
                                 <td><label>Porcentaje: </label></td>
                                 <td>
                                 <input type="text" name="<portlet:namespace/>ajustePorcentaje" id="<portlet:namespace/>ajustePorcentaje"   value="<%= ajusteSel.getPorcentaje() != null ? String.valueOf(ajusteSel.getPorcentaje()) : "" %>"
                                      readonly size="10px"/>
                                 </td>     
                                 <td><label>Importe: </label></td>
                                 <td>
                                      <input type="text" name="<portlet:namespace/>ajusteImporte"
							                 id="<portlet:namespace/>ajusteimporte" size="10px"
							                 value="<%=ajusteSel.getImporte()!= null ? String.valueOf(ajusteSel.getImporte().toPlainString()) : ""%>"
							                 readonly />
						</td> 
                               </tr>
                              </table> 
                              <table> 
                               <tr>
                               
                                <td><label>Desde:</label></td>
				                <td colspan="2">
							         <liferay-ui:input-date
							         dayParam="fechaDesdeDia"
							         dayValue="<%=ajusteSel !=null && ajusteSel.getFechaDesde() !=null?fechaVigenciaDde.get(Calendar.DAY_OF_MONTH ):-1%>"
							         dayNullable="<%= true %>"
							         monthParam="fechaDesdeMes"
							         monthValue="<%=ajusteSel !=null && ajusteSel.getFechaDesde()!=null?fechaVigenciaDde.get(Calendar.MONTH ):-1%>"
							         monthNullable="<%= true %>"			
							         yearParam="fechaDesdeAnio"
							         yearValue="<%=ajusteSel !=null && ajusteSel.getFechaDesde()!=null?fechaVigenciaDde.get(Calendar.YEAR ):-1 %>"
							         yearNullable="<%= true %>"
							         yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
							         yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)%>"
							         firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							         disabled="false" />
			                    </td>
			
			                    <td><label>Hasta:</label></td>
				                <td colspan="2">
							         <liferay-ui:input-date
							         dayParam="fechaHastaDia"
							         dayValue="<%=ajusteSel !=null && ajusteSel.getFechaHasta() !=null?fechaVigenciaHta.get(Calendar.DAY_OF_MONTH ):-1%>"
							         dayNullable="<%= true %>"
							         monthParam="fechaHastaMes"
							         monthValue="<%=ajusteSel !=null && ajusteSel.getFechaHasta()!=null?fechaVigenciaHta.get(Calendar.MONTH ):-1%>"
							         monthNullable="<%= true %>"			
							         yearParam="fechaHastaAnio"
							         yearValue="<%=ajusteSel !=null && ajusteSel.getFechaHasta()!=null?fechaVigenciaHta.get(Calendar.YEAR ):-1 %>"
							         yearNullable="<%= true %>"
							         yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
							         yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)+50%>"
							         firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
							         disabled="false" />
			                    </td>
			                    <td>
			                       <input id="<portlet:namespace />seleccion"   value="Agregar" title="Agregar"
		                                   onClick="javascript: <portlet:namespace />agregarAjuste();" type="button" />
		                     
			                    </td>
                               </tr>  
                             </table>
                            <table>
                              <tr>
                                 <td colspan="4">
                                 <div id="<portlet:namespace/>divAjusteAsignado">
                                    <liferay-util:include page='/html/portlet/afiliados/formulario_cotizar_ajustes_asignados.jsp'>
                                       <liferay-util:param value="<%= df.format(importeCotizado)%>"  name="importeCotizado" />
                                       <liferay-util:param value="<%= fechaCotizado%>"  name="fechaCotizado" />
                                    </liferay-util:include>
                                 </div>
	                             </td>
                               </tr>   
                            </table>
	                    </td>
	                     
	                    </tr>
	                    </tbody>
		             </table>
<script type="text/javascript">
function <portlet:namespace />agregarAjuste(){
	var ajusteId = jQuery("#<portlet:namespace />ajusteId").val();
	var ajusteDe = jQuery("#<portlet:namespace />ajusteDe").val(); 
	var ajustePorcentaje =jQuery("#<portlet:namespace />ajustePorcentaje").val();
	var ajusteImporte =jQuery("#<portlet:namespace />ajusteImporte").val();
	var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDia");
	var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMes");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnio");

	var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia");
	var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes");
	var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio");
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/solicitud_afiliacion'
		+	'&<%= Constants.CMD%>=' + 'agregarAjuste'
		+ '&tabs1=seguimiento-formulario'
		+ '&ajusteid=' + ajusteId
		+ '&ajustede=' +encodeURI(ajusteDe)
		+ '&ajusteporcentaje='+ajustePorcentaje
		+ '&ajusteimporte='+ajusteImporte
		+ '&fechadesdedia=' + fechaDesdeDia.value
		+ '&fechadesdemes=' + fechaDesdeMes.value
		+ '&fechadesdeanio=' + fechaDesdeAnio.value
		+ '&fechahastadia=' + fechaHastaDia.value
		+ '&fechahastames=' + fechaHastaMes.value
		+ '&fechahastaanio=' + fechaHastaAnio.value
		; 	
		jQuery('#<portlet:namespace/>divAjusteAsignado').load(url, function() {
			jQuery("#<portlet:namespace />ajusteId").val("");
			jQuery("#<portlet:namespace />ajusteDe").val("");
			jQuery("#<portlet:namespace />ajustePorcentaje").val("");
			jQuery("#<portlet:namespace />ajusteImporte").val("");
			jQuery("#<portlet:namespace />fechaDesdeDia").val("");
			jQuery("#<portlet:namespace />fechaDesdeMes").val("");
			jQuery("#<portlet:namespace />fechaDesdeAnio").val("");
			jQuery("#<portlet:namespace />fechaHastaDia").val("");
			jQuery("#<portlet:namespace />fechaHastaMes").val("");
			jQuery("#<portlet:namespace />fechaHastaAnio").val("");
		});
	
    return false;	
	
}

	
</script>		             
		             