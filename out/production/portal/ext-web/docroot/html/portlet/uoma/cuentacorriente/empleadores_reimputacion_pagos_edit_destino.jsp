<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.beans.FichaBoletaPortal"%>
<%@ page import="ar.com.ospim.global.services.EmpresaServiceUtil"%>
<%@ page import="ar.com.ospim.global.beans.Empresa"%>
<%@ page import="ar.com.ospim.global.beans.ConvenioNacion"%>
<%@ page import="java.text.DecimalFormat"%>
<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

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

 		DecimalFormat fm = new DecimalFormat("###0.00");
 		

 		FichaBoletaPortal boletaDest = (FichaBoletaPortal)request.getSession().getAttribute("BOLETA_EMPLEADORES_BOLETA_IMPAGA");
 		FichaBoletaPortal boletaOrig = (FichaBoletaPortal)request.getSession().getAttribute("BOLETA_EMPLEADORES_REIMPUTAR");
 		
 		
 		List<ConvenioNacion> conveniosDest =TraeListasServiceUtil.getConvenioNac();
 		
		ConvenioNacion convDest = null; 
		if(	boletaDest!=null && boletaDest.getTipoBoleta()!=null ){		
		  convDest=new ConvenioNacion();
		  for(ConvenioNacion c:conveniosDest){
        	if(c.getTipo_boleta()==boletaDest.getTipoBoleta().intValue()){
        		convDest=c;
        		break;
        	}
          }
		}  
 		
 		List<String> errores = (List<String>)request.getSession().getAttribute("Errores");
 		if (errores != null && !errores.isEmpty()){
 			%>
 			<table  style="color:red" >
 			<%
 			for (String error : errores){
 				%>
 				<tr><td>
 				<%=error%>
 				</td></tr>
 				<%
 			}
 			%>
 			</table>
 			<%
 	    }
 		BigDecimal boletaTotal=BigDecimal.ZERO;
 		if(boletaDest!=null && boletaDest.getCapital()!=null && boletaDest.getInteres()!=null){
 			boletaTotal =boletaDest.getCapital().add(boletaDest.getInteres());
 			if(boletaDest.getAjusteCapital()!=null){
 				boletaTotal =boletaTotal.add(boletaDest.getAjusteCapital());
 			}	
 			if(boletaDest.getAjusteInteres()!=null){
 			   boletaTotal =boletaTotal.add(boletaDest.getAjusteInteres());
 			}   
 		}
 		
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date());
%>	
		<fieldset class="block-labels">
		    <table class="lfr-table">			
			 <tr><td>
		     	   <label>Importe:</label>&nbsp;
				   <input id="<portlet:namespace />importe_dest" name="<portlet:namespace />importe_dest" size="8" readonly="readonly" 
				          maxlength="20" type="text" value="<%=boletaTotal!=null?fm.format(boletaTotal):""%>"/> 
			     </td>
			     <td>
			       <label>Tipo:</label>&nbsp;
			       <input id="<portlet:namespace />desc_dest" name="<portlet:namespace />desc_dest" size="20" readonly="readonly" 
			       type="text" value="<%=convDest!=null ?convDest.getDescripcion():""%>"/>
			     </td>
			      <td>
			       <label>Período:</label>&nbsp;
			       <input id="<portlet:namespace />periodo_dest" name="<portlet:namespace />periodo_dest" size="8" readonly="readonly" 
			       maxlength="8" type="text" value="<%=boletaDest!=null ?boletaDest.getPeriodoAsString():""%>"/>
			     </td>
			     <td>
			       <label>Diferencia:</label>&nbsp;
			       <input id="<portlet:namespace />dif_dest" name="<portlet:namespace />dif_dest" size="20" readonly="readonly" 
			       type="text" value="<%=boletaOrig.getImporte()!=null?  fm.format(boletaOrig.getImporte().subtract(boletaTotal)):"" %>"
			       style=" color:<%if(boletaOrig.getImporte()!=null && boletaOrig.getImporte().subtract(boletaTotal).compareTo(BigDecimal.ZERO)>0){%>green
			              <%}else{%>  red <%}%>"/>
			     </td>
			 </tr>
			</table>
		</fieldset>	
		
		<fieldset class="block-labels">
		    <table class="lfr-table">			
			 <tr><td>
		     	   <label>Generar Ajuste:</label>&nbsp;
		     	   <input type="checkbox" id="<portlet:namespace />ajusteChk" name="<portlet:namespace />ajusteChk" onclick="javascript:<portlet:namespace />manejoAjuste();" />
		
			</td></tr>
			<tr>
			 <td>
			   <div id="<portlet:namespace />divAjuste">
			        
			       <label>Habilitar a partir de:</label>&nbsp;
			       <liferay-ui:input-date
						 dayParam="fechaAjusteDia"
						 dayValue="<%=fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						 dayNullable="<%= false %>" monthParam="fechaAjusteMes"
						 monthValue="<%=fechaHasta.get(Calendar.MONTH )%>"
						 monthNullable="<%= false %>" yearParam="fechaAjusteAnio"
						 yearValue="<%=fechaHasta.get(Calendar.YEAR ) %>"
						 yearNullable="<%= false %>"
						 yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						 yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						 firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/>
			   
			       <label>Tipo:</label>&nbsp;
			       <select name="<portlet:namespace />tipoAporteAjuste"
					  id="<portlet:namespace />tipoAporteAjuste" >
					     
					    <%for(ConvenioNacion cn:conveniosDest) {%>
						<option
							value="<%=cn.getTipo_boleta() %>"
							<%if (boletaDest != null &&  boletaDest.getTipoBoleta()!=null &&
					              cn.getTipo_boleta()==boletaDest.getTipoBoleta() ){ %>
							selected="selected" <%} %>>
							<%=cn.getDescripcion() %>
						</option>
						<% } %>
				   </select>
			   
			   
			   
			       <label>Importe Ajuste:</label>&nbsp;
				   <input id="<portlet:namespace />importe_ajuste" name="<portlet:namespace />importe_ajuste" size="8" 
				          maxlength="20" type="text" value="<%=boletaOrig.getImporte()!=null?fm.format(boletaOrig.getImporte().subtract(boletaTotal)):""%>"/> 
			     </td>
			   </div>
			 </td>
			</tr>
			</table>
		</fieldset>		
		
<script type="text/javascript">		
		
<portlet:namespace />manejoAjuste();

function <portlet:namespace />manejoAjuste(){
	var esAjuste=jQuery('#<portlet:namespace />ajusteChk').attr('checked');
	if(esAjuste){
		jQuery('#<portlet:namespace/>divAjuste').show();
	}else{
		jQuery('#<portlet:namespace/>divAjuste').hide();
	}
}
	
</script>
