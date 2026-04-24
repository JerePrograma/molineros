<%@ include file="/html/portlet/farmacia/init.jsp" %>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>

<%
String portlet_name = null;
String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);
	List<Concepto> conceptos = (List<Concepto>) request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_EGRESOS_AMTIMA);
int entidad = 0;	

if(portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
	entidad = WebKeysGlobal.AMTIMA;
}



List<CentroCosto> centros = new ArrayList<CentroCosto>();
try{
  centros = TraeListasServiceUtil.getCentrosDeCostosVigentes(entidad);
}catch(Exception e){}
%>


<table width="60%">
  <tr>
	<td width="50%" valign="top">
	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
			<tr>
				<td>
					<liferay-ui:message key="conceptos"/>:&nbsp;
				</td>
				<td>
				<select id="<portlet:namespace />id_concepto" name="<portlet:namespace />id_concepto">
					<% 	for (Concepto concepto : conceptos) {  %>
							<option value="<%=concepto.getId()%>"><%=concepto.getDescripcion()%></option>
						<%	 } %>
				</select>
				</td>
				
				<td colspan="6">
					<table>
					    <tr>
					       <td colspan="4" >
						      <liferay-ui:message key="centro-costo"/>:&nbsp;
					       </td>
					       <td>
						      <select id="<portlet:namespace />id_centroCosto" name="<portlet:namespace />id_centroCosto">
						    	<option value="0">Seleccione Centro Costo</option>
								<% 	for (CentroCosto centro : centros) {  %>
									<option value="<%=centro.getId()%>"><%=centro.getDescripcion()%></option>
								<%	} %>
								</select>
					  		</td>
					  	</tr>
				  	</table>
				</td>
				
				
				<td>
					<input type="text" value="" name="<portlet:namespace />importe_concepto" id="<portlet:namespace />importe_concepto" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"/>
				</td>
				<td>
					<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarConcepto();" />
				</td>
			</tr>
		<%} %>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />agregandoIngreso">
				<table style="align: center;" width="100%">
					<tr>
						<td><liferay-ui:message key='buscando' /></td>
						<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				<div align="center" id="<portlet:namespace />ingresos">
					<liferay-util:include page="/html/portlet/farmacia/comprobantes/conceptos_amtima_search_result.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
	</td>
  </tr>
</table>

<script type="text/javascript">
	function <portlet:namespace />agregarConcepto(){
			jQuery('#<portlet:namespace />agregandoIngreso').show();	

			var id = jQuery('#<portlet:namespace />id_concepto').val();
			var importe = jQuery('#<portlet:namespace />importe_concepto').val();
			
			var id_centro = jQuery('#<portlet:namespace />id_centroCosto').val();
			var descripcion_centro = jQuery('#<portlet:namespace />id_centroCosto option:selected').text();
			if (typeof id_centro === "undefined") {
			    id_centro=0;
			}
			

			if (trim(importe) == ""){
				alert("Por favor ingrese el importe del concepto");
				return false;
			}
			 
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmacia/agregar_concepto_amtima' 
			+ '&id_concepto=' + id
			+ '&importe_concepto=' +importe
			+ '&esAmtima=esAmtima'
			+ '&id_centro=' + id_centro
			+ '&descripcion_centro=' + encodeURI(descripcion_centro)
			+ '&esEdicion=' +"<%=esEdicion%>"; 	
			url += '&rnd=' + Math.floor(Math.random()*100);
			
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoIngreso').hide();	
														sugerirEnConcepto();
										   }
			 );	
	}

	function borraConcepto(id){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmacia/sacar_concepto_amtima'
			+  '&id_concepto=' +id
			+ '&esAmtima=esAmtima'
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoIngreso').hide();
																						sugerirEnConcepto();
																			   }
															   );
	}
	
	function borraConcepto(id, id_seccional){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto_amtima'
		+  '&id_concepto=' +id
		+  '&id_seccional=' +id_seccional
		+  '&esEdicion=' + "<%=esEdicion%>" ;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
			jQuery('#<portlet:namespace />agregandoIngreso').hide();
			sugerirEnConcepto();
		});
	}
	
	function borraConcepto(id, id_seccional,id_centro){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto_amtima'
		+  '&id_concepto=' +id
		+  '&id_seccional=' +id_seccional
		+  '&id_centro='+id_centro
		+  '&esEdicion=' + "<%=esEdicion%>" ;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
			jQuery('#<portlet:namespace />agregandoIngreso').hide();
			sugerirEnConcepto();
		});
	}
	
	
	jQuery('#<portlet:namespace />agregandoIngreso').hide();
</script>