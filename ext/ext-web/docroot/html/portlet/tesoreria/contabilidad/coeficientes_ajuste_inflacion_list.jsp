<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>


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
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
	
	List<String> errores = (List<String>)request.getAttribute("errores");
	if (errores != null && !errores.isEmpty()){
		%>
		<table  style="color:#F53811">
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
	
%>
<portlet:defineObjects />
<form action="" method="POST" id="<portlet:namespace />busqueda_coeficientes" name="<portlet:namespace />busqueda_coeficientes" enctype="multipart/form-data">
	<table style="width: 40%">
		<tr>
			<td colspan="2"><b>Coeficientes Ajuste por Inflación</b></td>
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
				</select>
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2"><input type="button" id="<portlet:namespace />buscar" value="Buscar" />&nbsp; 
			<input type="button" onclick="altaCoeficiente()" value="Alta Coeficiente" />&nbsp;
			</td>
		</tr>
	</table>
	
	<div align="center" id="<portlet:namespace />uploadDiv">
<fieldset class="block-labels">
			<legend>
					<label>Subir desde Excel:</label>
			</legend>

  <table class="lfr-table">
   <tr>
     <td>
       <label style="color:blue">Seleccione Archivo Formato 2 columnas sin título: 1 Período(AAAAMM) - 2 Coeficiente(xls versión 97-2003) </label>
     </td>
	 <td  align="center">
			<input type="file" name="archivo"/>
	 </td>
	 <td align="center">
		<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
     </td>
  </tr>
</table>
</fieldset>
</div>
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
	<div align="center" id="<portlet:namespace />busquedaCoeficientesDiv">		
		<liferay-util:include page="/html/portlet/tesoreria/contabilidad/coeficientes_ajuste_inflacion_search_result.jsp"/>				
	</div>
</fieldset>


<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
	jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>

function eliminarCoeficiente(entidad, periodo){
	
	if (confirm("¿Esta seguro que desea eliminar el coeficiente: \"" + periodo + "\"?")){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/coeficientes_ajuste_inflacion'
		+  '&cmd=delete';
		url += '&ejercicio=' + escape(ejercicio);
		url += '&periodo=' + escape(periodo);
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />busquedaCoeficientesDiv').load(url, function() {
			jQuery('#<portlet:namespace />buscando').hide();            															
		  }
		);	
	}
}

function altaCoeficiente(){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/coeficientes_ajuste_inflacion';
	url += '&cmd=new';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}

jQuery('#<portlet:namespace />buscando').hide();	

jQuery('#<portlet:namespace />buscar').click(function(){
	var ejercicio=jQuery('#ejercicio').val();
	var periodo=jQuery('#periodo').val();
	
	jQuery('#<portlet:namespace />buscando').show();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/coeficientes_ajuste_inflacion';
	 url += '&cmd=list';
	 url += '&ejercicio=' + escape(ejercicio);
	 url += '&periodo=' + escape(periodo);
	 url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />busquedaCoeficientesDiv').load(url, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );	
});

function <portlet:namespace />uploadArchivo() {	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/coeficientes_ajuste_inflacion';
	url+='&cmd=uploadxls';
	jQuery("#guardando").show();
	document.<portlet:namespace />busqueda_coeficientes.method = 'post';
	submitForm(document.<portlet:namespace />busqueda_coeficientes, url);
}

</script>
