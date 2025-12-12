<%@page import="ar.com.ospim.util.DateUtils"%>
<%@page import="ar.com.ospim.global.beans.AportesMonotributo" %>
<%@page import="ar.com.ospim.global.beans.AportesMonotributoClase" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="ar.com.ospim.global.beans.ClaseBase" %>
<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("") || renderResponse.getNamespace().equals("_AFI_1_")){
	portlet_name = "afiliados";
}


String[] tiposStr = TraeListasServiceUtil.getSystemConfig("MONOTRIBUTO_CLASES").split(";");
List<ClaseBase> tipos =new ArrayList<ClaseBase>();
for(int i=0;i<=tiposStr.length-1;i++){
	ClaseBase c =new ClaseBase();
	String codigo = tiposStr[i].split("=")[0];
	String descripcion = tiposStr[i].split("=")[1];
	c.setId(codigo);
	c.setDescripcion(descripcion);
	tipos.add(c);
	
	AportesMonotributo aporte = (AportesMonotributo)session.getAttribute(WebKeysAfiliados.APORTE_EN_EDICION);	

}
%>
<form action="" method="post" name="<portlet:namespace />fmDet"> 		
<fieldset>
  <table class="lfr-table">
  <tr>
    <td>Tipo Demanda:</td>
	<td>
	   <select id="<portlet:namespace />claseM" name="<portlet:namespace />claseM" >
	   		<%for(ClaseBase c:tipos) {%>
						<option	value="<%= c.getId() %>">
							<%=c.getDescripcion() %>
						</option>
			<% } %>
	   </select>
	</td>
	
	<td>Aporte:</td> 
	<td><input id="<portlet:namespace />aporteClase" name="<portlet:namespace />aporteClase" size="15"
					maxlength="20" type="text"
					value='0' />
	</td>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td> <input type="button" value="Agregar"
				onClick="<portlet:namespace />agregarAporteClase();" />
	</td>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td> <input type="button" value="Propagar Fecha Categoría"
				onClick="<portlet:namespace />propagarFechas();" />
	</td>
				
  </tr>
 </table>  
 <br>
 <div align="center" id="<portlet:namespace />clasesMonotributo">
	<jsp:include page='aportemonotributo_clases_result.jsp' />
</div>
 

  
</fieldset>
		
</form>	
	
<script type="text/javascript">



function <portlet:namespace />agregarAporteClase(){
	
	var clase=jQuery('#<portlet:namespace />claseM').val();
	var aporte = jQuery('#<portlet:namespace />aporteClase').val();
	
	if (trim(aporte) == "" || !IsNumeric(aporte) ){
		alert("Debe completar el aporte");
		jQuery('#<portlet:namespace />aporteClase').focus();
		return false;
	}

	var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDia");
	var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMes");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnio");
	
	
	var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia");
	var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes");
	var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio");
		
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_categorias_monotributo_action';
	url = url + '&cmd=agregarClase' 
	+'&clase='+clase
	+'&aporte=' + aporte
	+'&fechaDesdeDia='+fechaDesdeDia.value
	+'&fechaDesdeMes='+fechaDesdeMes.value
	+'&fechaDesdeAnio='+fechaDesdeAnio.value
	+'&fechaHastaDia='+fechaHastaDia.value
	+'&fechaHastaMes='+fechaHastaMes.value
	+'&fechaHastaAnio='+fechaHastaAnio.value
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />clasesMonotributo').load(url, function() {  });
	
}
	
function eliminarAporteClase(id){
	 if(confirm("Esta seguro de Eliminar la Clase?")){		
	 	var busquedaNom = {"id":id,"cmd":"eliminarClase"};
	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/afiliados/abm_categorias_monotributo_action" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />clasesMonotributo').load(url,busquedaNom, function(){
		});	
	 }	
}	
	

function <portlet:namespace />propagarFechas(){
	var fechaDesdeDia = document.getElementById("<portlet:namespace />fechaDesdeDia");
	var fechaDesdeMes = document.getElementById("<portlet:namespace />fechaDesdeMes");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnio");
	
	var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia");
	var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes");
	var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio");
		
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_categorias_monotributo_action';
	url = url + '&cmd=propagarFecha' 
	+'&fechaDesdeDia='+fechaDesdeDia.value
	+'&fechaDesdeMes='+fechaDesdeMes.value
	+'&fechaDesdeAnio='+fechaDesdeAnio.value
	+'&fechaHastaDia='+fechaHastaDia.value
	+'&fechaHastaMes='+fechaHastaMes.value
	+'&fechaHastaAnio='+fechaHastaAnio.value
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />clasesMonotributo').load(url, function() {  });
	
}
	
	 
</script>	
