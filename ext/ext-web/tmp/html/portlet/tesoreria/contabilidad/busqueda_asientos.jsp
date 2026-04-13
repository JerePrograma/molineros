<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Concepto"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<liferay-ui:error exception="<%= ConceptoUtilizadoException.class %>"
	message="concepto-utilizado" />

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
	boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
	//boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
	boolean soloVer = PermissionUtil.userContainsRole(user,"CONTABILIDAD_SOLO_VER");
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
%>
<portlet:defineObjects />
<form action="" method="POST" id="busqueda_asientos" name="busqueda_asientos">
	<table style="width: 40%">
		<tr>
			<td colspan="2"><b>Asientos</b></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>Ejercicio:&nbsp;<select name="ejercicio" id="ejercicio">
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						if(portlet_name.equals("farmacia")){
							if (cal.get(Calendar.MONTH) < Calendar.JULY){
								hastaAnio--;
							}
						}else{
							if (cal.get(Calendar.MONTH) < Calendar.AUGUST){
								hastaAnio--;
							}
						}
						for (int i = 2000; i<=hastaAnio; i++){  %>
					<option value="<%=i%>-<%=i+1%>" <%if (i == hastaAnio) { %>
						selected="selected" <%} %>>
						<% if(portlet_name.equals("farmacia")){%>
							Julio&nbsp;<%=i %>&nbsp;-&nbsp;Junio&nbsp;<%= i+1 %></option>
						<%}else{%>						
							Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
						<%}%>
					<%} %>
				</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicioHeader')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</td>
			<td>Periodo:&nbsp;
				<% if(portlet_name.equals("farmacia")){%>
					<select name="periodo" id="periodo">
					<option value="-1"></option>
					<option value="07">Julio</option>
					<option value="08">Agosto</option>
					<option value="09">Septiembre</option>
					<option value="10">Octubre</option>
					<option value="11">Noviembre</option>
					<option value="12">Diciembre</option>
					<option value="01">Enero</option>
					<option value="02">Febrero</option>
					<option value="03">Marzo</option>
					<option value="04">Abril</option>
					<option value="05">Mayo</option>
					<option value="06">Junio</option>					
					</select>
				<%}else{%>
				<select name="periodo" id="periodo">
					<option value="-1"></option>
					<option value="08">Agosto</option>
					<option value="09">Septiembre</option>
					<option value="10">Octubre</option>
					<option value="11">Noviembre</option>
					<option value="12">Diciembre</option>
					<option value="01">Enero</option>
					<option value="02">Febrero</option>
					<option value="03">Marzo</option>
					<option value="04">Abril</option>
					<option value="05">Mayo</option>
					<option value="06">Junio</option>
					<option value="07">Julio</option>
				</select><%}%><a href="javascript:void(0)" onclick="help(event, 'helpPeriodoHeader')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	</table>
	<table>	
		<tr>
			<td colspan="2"><input type="button" id="<portlet:namespace />buscar" value="Buscar" /><a href="javascript:void(0)" onclick="help(event, 'helpBuscarHeader')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;</td> 
			<% if (rolABM && !soloVer) {%>
			     <td>
				    <input type="button" onclick="altaAsiento()" value="Alta Asiento" /><a href="javascript:void(0)" onclick="help(event, 'helpAltaHeader')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
                 </td>
                 <td>				    
				    <input type="button" onclick="generarAsientosAutomaticos()" value="Generar Asientos Automaticos" /><a href="javascript:void(0)" onclick="help(event, 'helpAutomaticosHeader')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
                 </td>
                 <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                 <td>				    
				    <input type="button" onclick="importarSueldos()" value="Alta Asiento Sueldos" />&nbsp;
				 </td> 
				 <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				 <td>				    
				    <input type="button" onclick="aperturaCierre()" value="Asientos Especiales" />&nbsp;
				 </td>  
			<% } %>
			
		</tr>
	</table>
</form>
<hr />
<br />
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
	<div align="center" id="<portlet:namespace />busquedaAsientosDiv">		
		<liferay-util:include page="/html/portlet/tesoreria/contabilidad/asientos_search_result.jsp"/>				
	</div>
</fieldset>

<div id="helpEjercicioHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Se completa sólo cuando se quiere efectuar una búsqueda de asientos del ejercicio indicado. Luego, se deberá seleccionar el botón "Buscar" para visualizar el resultado.
</div>
<div id="helpPeriodoHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Período: Se completa sólo cuando se quiere efectuar una búsqueda que filtre por un período en particular del ejercicio que se indique. Se deberá seleccionar el botón "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpBuscarHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Buscar: De acuerdo a los parámetros previos, seleccionando este botón, se ejecuta la búsqueda de registros coincidentes. El resultado se visualiza en el cuadro inferior de esta pantalla.
</div>
<div id="helpAltaHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Alta Asiento: Seleccionando este botón, se abrirá la pantalla de alta de un nuevo asiento manual.
</div>
<div id="helpAutomaticosHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Asientos Automáticos: Seleccionando este botón, se abrirá la pantalla de generación de asientos automáticos. Estos son los que se corresponden a todas las transacciones efectuadas en el sistema para el período que se trate. Pueden generarse en varias oportunidades para un mismo período; en ese caso, reemplaza los anteriores.
</div>

<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
	jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>
function eliminarAsiento(id, desc){
	var ejercicio=jQuery('#ejercicio').val();
	var periodo=jQuery('#periodo').val();
	
	if (confirm("¿Esta seguro que desea eliminar el asiento: \"" + desc + "\"?")){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/eliminar_asiento'
		+  '&id=' +id;
		 url += '&ejercicio=' + escape(ejercicio);
		 url += '&periodo=' + escape(periodo);
		 <%if(portlet_name.equals("farmacia")){%>
			url += '&amtima=true';
		 <%}%>
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />busquedaAsientosDiv').load(url, function() {
			jQuery('#<portlet:namespace />buscando').hide();            															
		  }
		);	
	}
}
function altaAsiento(){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_asiento';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function generarAsientosAutomaticos(){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/generar_asientos_automaticos_init';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
jQuery('#<portlet:namespace />buscando').hide();	

jQuery('#<portlet:namespace />buscar').click(function(){
	var ejercicio=jQuery('#ejercicio').val();
	var periodo=jQuery('#periodo').val();
	
	jQuery('#<portlet:namespace />buscando').show();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/busqueda_asientos';
	 url += '&ejercicio=' + escape(ejercicio);
	 url += '&periodo=' + escape(periodo);
	 url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />busquedaAsientosDiv').load(url, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );	
});


function importarSueldos(){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/importar_asiento_sueldos';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}

function aperturaCierre(){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/asiento_apertura_cierre';
	url += '&cmd=newApertura'
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}

</script>
