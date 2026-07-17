<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/afiliados/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.tesoreria.beans.AjustePlanSuperador" %>
<%@ page import="ar.com.ospim.tesoreria.beans.PrecioPlanSuperador" %>
<%@ page import="ar.com.ospim.tesoreria.service.LiquidacionPlanesSuperadoresServiceUtil" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormatSymbols" %>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<portlet:defineObjects/>
			<%
			
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			
			DecimalFormat df = new DecimalFormat("#,##0.00");
			String portlet_name = ParamUtil.getString(request, "portlet_name");
			Integer entidad = WebKeysGlobal.OSPIM;
			
			List<AjustePlanSuperador> asignados = (List<AjustePlanSuperador>)request.getSession().getAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_ASIGNADOS);
			String fechaCotizadoStr = (String)request.getSession().getAttribute(WebKeysTesoreria.AJUSTE_COTIZACION_FECHA);
			
			Date fechaCotizado = null;
			try {
				fechaCotizado =   formato.parse(fechaCotizadoStr);
			} catch (Exception e) {
					fechaCotizado = null;
			}
			
			String importeCotizado	= ParamUtil.getString(request, "importeCotizado");
			Double importeDelGrupo  = (Double)request.getSession().getAttribute(WebKeysTesoreria.PRECIO_COTIZACION); 
			Double importeAjuste=0D;
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);
		    
			PortletURL portletURLPreAutMed = renderResponse.createRenderURL();
	 		List<String> headerNamesPreAutMed = new ArrayList<String>();
	 		headerNamesPreAutMed.add("Orden");
	 		headerNamesPreAutMed.add("Id");
	 		headerNamesPreAutMed.add("Descripción");
	 		headerNamesPreAutMed.add("Desde");
	 		headerNamesPreAutMed.add("Hasta");
	 		headerNamesPreAutMed.add("Porcentaje");
	 		headerNamesPreAutMed.add("Importe");
	 		headerNamesPreAutMed.add("");
	 		SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLPreAutMed, headerNamesPreAutMed,
			LanguageUtil.get(pageContext, "no hay valores cargados"));
			if(null!=asignados){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < asignados.size(); i++) {
 			 		AjustePlanSuperador h = asignados.get(i);
 			 		    
 			 		  if((h.getFechaDesde()==null || fechaCotizado.compareTo(h.getFechaDesde())>=0) && (
 			 				h.getFechaHasta()==null ||	  fechaCotizado.compareTo(h.getFechaHasta())<=0)){
 			 		
 			 		    if(h.getPorcentaje()!=null && h.getPorcentaje()!=0D){
 			 		    	importeAjuste += importeDelGrupo * h.getPorcentaje()/100;
 			 		    } else{
 			 		    	 if(h.getImporte()!=null){
 	 			 		    	importeAjuste += importeDelGrupo * h.getImporte().doubleValue();
 	 			 		    }
 			 		    }
 			 		  }  
 			 		     
	 				  ResultRow row = new ResultRow(h, h.getId() , i);
	 				  row.addText(String.valueOf(i));
	 				  row.addText(String.valueOf(h.getId()));
	 				  row.addText(h.getDescripcion());
	 				  row.addText(h.getFechaDesdeAsString());
	 				  row.addText(h.getFechaHastaAsString());
	 				  row.addText("right", "middle",String.valueOf(df.format(h.getPorcentaje()) ));
	 				  row.addText("right", "middle",String.valueOf(df.format(h.getImporte()) ));
	 				    
	 				   StringBuilder sb= new StringBuilder();
	 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
	 					sb.append(themeDisplay.getPathThemeImages());
	 					sb.append("/common/delete.png\" onClick=\"javascript:sacarAjuste('");
	 					sb.append(i);
	 					sb.append("');\" />");
	 					row.addText(sb.toString());
	 				    
	 					resultRowsInspector.add(row);
 			 		}
	 		}
		%>
	<table>
	<th> <tr>
	
	  <td colspan="14" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	
	 <td style="font-weight: bold; background-color: #f0f0f0; border-top: 2px solid #ccc;text-align:right;color:blue;font-size: 15px;" >
	    <label>Bonificación/Recargos:  <%=df.format(importeAjuste) %></label> 
	 </td>
	 
	 <td>&nbsp;&nbsp;</td>
	 <td style="font-weight: bold; background-color: #f0f0f0; border-top: 2px solid #ccc;text-align:right;color:green;font-size: 15px;" >
	    <span style="background-color: yellow; color: black;">
	       Total:  <%=df.format(importeDelGrupo+importeAjuste) %>
        </span>
	 
	       <label></label> 
	 </td>
	 </tr>
	 </th> 	
    </table>	 
 	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	<table style="width: 100%; table-layout: fixed; border-collapse: collapse;">
     <tr>
      <td style="width: 100%; vertical-align: top;">   
	     <liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	  </td>
	  
	 </tr>
	</table>
	
		
<script type="text/javascript">
function sacarAjuste(orden){
	var ajusteId = jQuery("#<portlet:namespace />ajusteId").val();
	var ajusteDe = jQuery("#<portlet:namespace />ajusteDe").val(); 
	var ajustePorcentaje =jQuery("#<portlet:namespace />ajustePorcentaje").val();
	var ajusteImporte =jQuery("#<portlet:namespace />ajusteImporte").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/solicitud_afiliacion'
		+	'&<%= Constants.CMD%>=' + 'sacarAjuste'
		+ '&tabs1=seguimiento-formulario'
		+ '&orden=' + orden	; 	
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



