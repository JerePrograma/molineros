<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	NumberFormat formatter = new DecimalFormat("#0.00");  
	
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	
	Integer entidad = WebKeysGlobal.OSPIM;
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "uoma";
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	CajaChica cajaChica=(CajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
	
	int id_caja_chica=cajaChica!=null && cajaChica.getId() !=null ?(int)cajaChica.getId():0;
	
	if(cajaChica==null){
		cajaChica= new CajaChica();
	} 
	
	
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
	fechaDesde.setTime(new Date());
	
	 fechaDesde.add(Calendar.DAY_OF_YEAR, -30);
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	%>

<form action="" method="post" name="<portlet:namespace />fmCJCHEJ">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<fieldset class="block-labels">
		<legend>Caja Chica</legend>

		<table class="lfr-table">
			<tr>
			
			   <td><label><liferay-ui:message key="caja-chica-nombre" />:</label></td>
				<td><input id="<portlet:namespace />descripcionCajaChica"
					name="<portlet:namespace />descripcionCajaChica" size="70"
					maxlength="70" type="text"
					value='<%=cajaChica.getDescripcion()==null?"":cajaChica.getDescripcion() %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/></td>
			   <td>
					<liferay-ui:message key="Estado" />
				</td>
				<td>
				   <input id="<portlet:namespace />estadoCajaChica"
					name="<portlet:namespace />estadoCajaChica" size="40"
					maxlength="40" type="text" 
					value='<%=cajaChica.getEstado().getDescripcion()==null?"":cajaChica.getEstado().getDescripcion() %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
				</td>	
					
				<td>
				   <input id="<portlet:namespace />estadoFechaCajaChica"
					name="<portlet:namespace />estadoFechaCajaChica" size="20"
					maxlength="20" type="text"
					value='<%=cajaChica.getEstado().getFecha() ==null?"":sdf.format(cajaChica.getEstado().getFecha()) %>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
				</td>
				<td>
					<liferay-ui:message key="Saldo" />
				</td>
				<td>
				
				   <input id="<portlet:namespace />saldoCajaChica"  style="background-color: #72A4D2;"
					name="<portlet:namespace />saldoCajaChica" size="20" 
					maxlength="20" type="text"
					value='<%=formatter.format(cajaChica.getSaldo())%>' 
					<%if(cajaChica!=null && cajaChica.getId()!=null && cajaChica.getId()!=0){%> disabled="disabled" <%}%>/>
					
				</td>		
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
	</fieldset>
	<br>
	<fieldset class="block-labels">
		<legend>Parámetros de Selección</legend>

		<table class="lfr-table">
		    <tr>
		        
		       <td><label><liferay-ui:message key="Desde" />:</label></td>
		       <td>  
					    <liferay-ui:input-date
					         dayParam="fechaDesdeCajaChicaDia"
					         dayValue="<%=fechaDesde.get(Calendar.DAY_OF_MONTH )%>"
					         dayNullable="<%= true %>" monthParam="fechaDesdeCajaChicaMes"
					         monthValue="<%=fechaDesde.get(Calendar.MONTH )%>"
					         monthNullable="<%= true %>" yearParam="fechaDesdeCajaChicaAnio"
					         yearValue="<%=fechaDesde.get(Calendar.YEAR )%>"
					         yearNullable="<%= true %>"
					         yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 5 %>"
					         yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) %>"
					         firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
					         disabled="<%= false %>"/>
				</td> 
		       
		    
		       <td><label><liferay-ui:message key="Hasta" />:</label></td>
		       <td>  
					    <liferay-ui:input-date
					         dayParam="fechaHastaCajaChicaDia"
					         dayValue="<%=fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
					         dayNullable="<%= true %>" monthParam="fechaHastaCajaChicaMes"
					         monthValue="<%=fechaHasta.get(Calendar.MONTH )%>"
					         monthNullable="<%= true %>" yearParam="fechaHastaCajaChicaAnio"
					         yearValue="<%=fechaHasta.get(Calendar.YEAR )%>"
					         yearNullable="<%= true %>"
					         yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
					         yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
					         firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
					         disabled="<%= false %>"/>
				</td>
		    </tr>
		    <tr>
				<td>&nbsp;</td>
			</tr>
		    	  	
			<tr>
				  <td><input id="<portlet:namespace />reporteCajaChica"
		              value="<liferay-ui:message key="reporte"/>"
		              title="<liferay-ui:message key="reporte" />"
		              onClick="javascript: <portlet:namespace />emitirReporteCajaChica();"
		              type="button"/>
		              <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		                  <a href="javascript:void(0)" onclick="help(event, 'helpReporteCaja')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		              <%}%>
		          </td>
			</tr>
			 
	    </table>
		
	</fieldset>
	
	<input type="hidden" name="<portlet:namespace />id_caja_chica"
		id="<portlet:namespace />id_caja_chica" value="<%=id_caja_chica%>" />
    <input type="hidden" value="" name="view" id="view" /> 

    
   
</form>

<div id="helpReporteCaja" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
 Luego de ingresado el rango de fechas deseado, se podrá emitir un reporte de los comprobantes ingresados a través del botón "Reporte". Luego de un pequeño lapso de tiempo de procesamiento, se descargará a su puesto local, una planilla de cálculo con los resultados obtenidos con el nombre "CajaChica.xls".
</div>

<script type="text/javascript">

var popupCJ;
var auxiliar;

function <portlet:namespace />emitirReporteCajaChica(){
	var idCajaChica=jQuery('#<portlet:namespace />id_caja_chica').val();
	var diaDesde=jQuery('#<portlet:namespace />fechaDesdeCajaChicaDia').val();
	var mesDesde=jQuery('#<portlet:namespace />fechaDesdeCajaChicaMes').val();
	var anioDesde=jQuery('#<portlet:namespace />fechaDesdeCajaChicaAnio').val();
	
	var diaHasta=jQuery('#<portlet:namespace />fechaHastaCajaChicaDia').val();
	var mesHasta=jQuery('#<portlet:namespace />fechaHastaCajaChicaMes').val();
	var anioHasta=jQuery('#<portlet:namespace />fechaHastaCajaChicaAnio').val();
	
	window.location.href ='/xlsservlet/?reporte=REPORTE_CAJA_CHICA'
		         +'&entidad='+<%=entidad%>
	             +'&id_caja_chica='+idCajaChica
	             +'&fechaDesdeCajaChicaDia='+diaDesde
	             +'&fechaDesdeCajaChicaMes='+mesDesde
	             +'&fechaDesdeCajaChicaAnio='+anioDesde
	             +'&fechaHastaCajaChicaDia='+diaHasta
	             +'&fechaHastaCajaChicaMes='+mesHasta
	             +'&fechaHastaCajaChicaAnio='+anioHasta;
}

</script>

