<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>
 
<%

String portlet_name=null;
int entidad=0;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "liquidaciones";
}

if(renderResponse.getNamespace().equals("_LIQ_1_")){
	portlet_name = "liquidaciones";
	entidad=WebKeysGlobal.OSPIM;
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
	entidad=WebKeysGlobal.UOMA;
}

if(renderResponse.getNamespace().equals("_COM_1_")){
	portlet_name = "comprobantes";
}

String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEdicion = Boolean.parseBoolean(esEditableStr);
	List<Concepto> conceptos = (List<Concepto>) request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_EGRESOS);
	
	List<CentroCosto> centros = new ArrayList<CentroCosto>();
	try{
	   centros=TraeListasServiceUtil.getCentrosDeCostosVigentes(entidad);
	}catch(Exception e){}		
%>


<table width="70%">
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
						    <%for (CentroCosto centro : centros) {  %>
								<option value="<%=centro.getId()%>"><%=centro.getDescripcion()%></option>
							<%	 } %>
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
					<liferay-util:include page="/html/portlet/liquidaciones/comprobantes/conceptos_search_result.jsp">
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
	<%-- function <portlet:namespace />agregarConcepto(){
			jQuery('#<portlet:namespace />agregandoIngreso').show();	

			var id = jQuery('#<portlet:namespace />id_concepto').val();
			var importe = jQuery('#<portlet:namespace />importe_concepto').val();
			var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad").val();
			var sucursal=parseInt(jQuery("#<portlet:namespace />sucursal_entidad").val());

			var id_centro = jQuery('#<portlet:namespace />id_centroCosto').val();
			var descripcion_centro = jQuery('#<portlet:namespace />id_centroCosto option:selected').text();
			if (typeof id_centro === "undefined") {
			    id_centro=0;
			}
			
			if (trim(importe) == ""){
				alert("Por favor ingrese el importe del concepto");
				return false;
			}
			 
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_concepto' 
			+ '&id_concepto=' + id
			+ '&importe_concepto=' +importe
			+ '&cuit='+cuit_entidad
			+ '&sucursal='+sucursal
			+ '&esEdicion=' +"<%=esEdicion%>"
			+ '&id_centro='+ id_centro
			+ '&descripcion_centro='+encodeURI(descripcion_centro); 	
			url += '&rnd=' + Math.floor(Math.random()*100);

			jQuery('#<portlet:namespace />ingresos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoIngreso').hide();	
														sugerirEnConcepto();
										   }
			 );	
	} --%>
	function <portlet:namespace />agregarConcepto(){
		jQuery('#<portlet:namespace />agregandoIngreso').show();	

		var id = jQuery('#<portlet:namespace />id_concepto').val();
		var importe = jQuery('#<portlet:namespace />importe_concepto').val();
		var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad").val();
		var sucursal=parseInt(jQuery("#<portlet:namespace />sucursal_entidad").val());

		var id_centro = jQuery('#<portlet:namespace />id_centroCosto').val();
		var descripcion_centro = jQuery('#<portlet:namespace />id_centroCosto option:selected').text();
		if (typeof id_centro === "undefined") {
		    id_centro=0;
		}
		
		if (trim(importe) == ""){
			alert("Por favor ingrese el importe del concepto");
			return false;
		}
		
		var xporlet ='<%=portlet_name%>';
		var esEdicion='<%=esEdicion%>';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/__porlet/agregar_concepto" />'+
		'<liferay-portlet:param name="id_concepto" value="__id_concepto"/>'+
		'<liferay-portlet:param name="importe_concepto" value="__importe_concepto"/>'+
		'<liferay-portlet:param name="cuit" value="__cuit"/>'+
		'<liferay-portlet:param name="sucursal" value="__sucursal"/>'+
		'<liferay-portlet:param name="esEdicion" value="__esEdicion"/>'+
		'<liferay-portlet:param name="id_centro" value="__id_centro"/>'+
		'<liferay-portlet:param name="descripcion_centro" value="__descripcion_centro"/>'+
		'<liferay-portlet:param name="rnd" value="__rnd"/>'+
		'</liferay-portlet:renderURL>';

		url = url.replace("__porlet",xporlet);
		url = url.replace("__id_concepto",encodeURI(id));
		url = url.replace("__importe_concepto",importe);
		url = url.replace("__cuit",cuit_entidad);
		url = url.replace("__sucursal",sucursal);
		url = url.replace("__esEdicion",esEdicion);
		url = url.replace("__id_centro",id_centro);
		url = url.replace("__descripcion_centro",encodeURI(descripcion_centro));
		url = url.replace("__rnd",Math.floor(Math.random()*100));
		
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
													jQuery('#<portlet:namespace />agregandoIngreso').hide();	
													sugerirEnConcepto();
									   }
		);	
	}

	function borraConcepto(id){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto'
			+  '&id_concepto=' +id
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoIngreso').hide();
																						sugerirEnConcepto();
																			   }
															   );
	}
	function borraConcepto(id, id_seccional){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto'
			+  '&id_concepto=' +id
			+  '&id_seccional=' +id_seccional
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />ingresos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoIngreso').hide();
																						sugerirEnConcepto();
																			   }
															   );
	}
	
	function borraConcepto(id, id_seccional,id_centro){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto'
		+  '&id_concepto=' +id
		+  '&id_seccional=' +id_seccional
		+  '&id_centro='+id_centro
		+  '&esEdicion=' + "<%=esEdicion%>" ;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
																					jQuery('#<portlet:namespace />agregandoIngreso').hide();
																					sugerirEnConcepto();
																		   }
														   );
}
	
	jQuery('#<portlet:namespace />agregandoIngreso').hide();
</script>