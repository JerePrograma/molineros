<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%

PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	Cartilla cartilla=(Cartilla)request.getSession().getAttribute(WebKeysAutorizaciones.CARTILLA_EN_EDICION);
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	int id_cartilla=cartilla!=null && cartilla.getId()!= null ?(int)cartilla.getId():0;
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	
%>

<form action="" method="post" name="<portlet:namespace />fmSB">
	
<table class="lfr-table">	
	<tr>
	     <td>Fecha Baja </td>
		  
		  <td colspan="5">
		 
		   <liferay-ui:input-date
					         dayParam="fechaBajaCartillaDia"
					         dayValue="<%=fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
					         dayNullable="<%= false %>" monthParam="fechaBajaCartillaMes"
					         monthValue="<%=fechaHasta.get(Calendar.MONTH )%>"
					         monthNullable="<%= false %>" yearParam="fechaBajaCartillaAnio"
					         yearValue="<%=fechaHasta.get(Calendar.YEAR )%>"
					         yearNullable="<%= false %>"
					         yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
					         yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
					         firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
					         disabled="<%= false %>"/>
		 </td>  
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>


<input type="hidden" name="<portlet:namespace />id_cartilla" id="<portlet:namespace />id_cartilla" value="<%=id_cartilla%>" />

<input id="<portlet:namespace />save"
				value="<liferay-ui:message key="save"/>"
				title="<liferay-ui:message key="save" />"
				onClick="javascript: <portlet:namespace />eliminarCartilla();"
				type="button" /> 
				
</form>

<script type="text/javascript">
function <portlet:namespace />eliminarCartilla(){
	
	  var params = "&<%= Constants.CMD %>=" + "eliminaCartilla";
	  params += "&id_cartilla="+<%=id_cartilla%>;
	  
	  var dia=jQuery('#<portlet:namespace />fechaBajaCartillaDia').val();
	  var mes=jQuery('#<portlet:namespace />fechaBajaCartillaMes').val();
	  var anio=jQuery('#<portlet:namespace />fechaBajaCartillaAnio').val();
	  
	  params += "&dia="+dia;
	  params += "&mes="+mes;
	  params += "&anio="+anio;
	  
	  url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/editar_cartilla';
	  url = url + params;
	  
	  jQuery('#<portlet:namespace />divCartilla').load(url);
	  <portlet:namespace />cerrarCartilla();
	  return false;		
	}

</script>

