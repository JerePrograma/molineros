<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>
<%@ page import="com.liferay.portal.model.User" %>
 
<%

String portlet_name=null;
int entidad=0;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "liquidaciones";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
	entidad=WebKeysGlobal.UOMA;
}

String esEditableStr = ParamUtil.getString(request, "esEdicion");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}

Long organizacionId = user.getOrganizations().size()>0?user.getOrganizations().get(0).getOrganizationId():0;
Integer bsAs = (organizacionId==818513 || organizacionId==1295316)?902 :0;


boolean esEdicion = Boolean.parseBoolean(esEditableStr);
	List<Concepto> conceptos = (List<Concepto>) request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_EGRESOS);
	List<CentroCosto> centros = new ArrayList<CentroCosto>();
	try{
	   centros=TraeListasServiceUtil.getCentrosDeCostosVigentes(entidad);
	}catch(Exception e){}
	
	List<Localidad> jurisdicciones=TraeListasServiceUtil.getPercepcionesIIBB(entidad);
%>


<table width="70%">
  <tr>
	<td width="50%" valign="top">
	
	<%if (esEdicion){ %>
	<table class="lfr-table" width="100%">
		
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
				<%if(entidad==WebKeysGlobal.UOMA){%>
				
				<td colspan="10">
				 
				  <table>
				    <tr>
				       <td colspan="6" >
					      <liferay-ui:message key="centro-costo"/>:&nbsp;
				       </td>
				       <td>
					      <select id="<portlet:namespace />id_centroCosto" name="<portlet:namespace />id_centroCosto">
					    <option value="0">Seleccione Centro Costo</option>
						<% 	for (CentroCosto centro : centros) {  %>
								<option value="<%=centro.getId()%>"><%=centro.getDescripcion()%></option>
						<%	 } %>
					</select>
				  </td>
				  </tr>
				  <tr><td>&nbsp;</td></tr>
				  </table>
				 
				</td>
			    <%}%>
			    
		
			</tr>
      </table>
      <table class="lfr-table" with="100%">
			
			<tr>
			          <td>
					      <label>Gravado</label>
				      </td>
				      <td>
      					<input type="text" value="" name="<portlet:namespace />importe_gravado" id="<portlet:namespace />importe_gravado" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoIva();calculoConcepto();agregarCeros(this);"/>
				      </td>
				      <td style="background-color:#AEB6BF" ><input type="radio" name="<portlet:namespace />tasa_iva" 
				          value="0" checked="checked" onchange="calculoIva();calculoConcepto()">Exento &nbsp;</td>
				      <td>&nbsp;</td>
				      <td style="background-color:#AEB6BF" ><input type="radio" name="<portlet:namespace />tasa_iva" 
				           value="0.27" onchange="calculoIva();calculoConcepto()">Gravado 27% &nbsp;</td>
				      <td>&nbsp;</td>
				      <td style="background-color:#AEB6BF" ><input type="radio" name="<portlet:namespace />tasa_iva" 
				           value="0.21" onchange="calculoIva();calculoConcepto()">Gravado 21% &nbsp;</td>
				      <td>&nbsp;</td>
				      <td style="background-color:#AEB6BF" ><input type="radio" name="<portlet:namespace />tasa_iva" 
				           value="0.105" onchange="calculoIva();calculoConcepto()">Gravado 10.5% &nbsp;</td>
				      <td>&nbsp;</td>
				      <td>I.V.A:</td>
				      <td>
				        <input type="text" value="" name="<portlet:namespace />importe_iva" id="<portlet:namespace />importe_iva" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoConcepto();agregarCeros(this);"/>
				      </td>
			</tr>  
			<tr>
				      <td>&nbsp;</td>
			</tr>
	 </table>
     <table class="lfr-table" with="100%">			    
			<tr>
				      <td>Percep.IVA:</td>
				      <td>
				        <input type="text" value="" name="<portlet:namespace />importe_percep_iva" id="<portlet:namespace />importe_percep_iva" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoConcepto();agregarCeros(this);"/>
				      </td>
				      
				      <td>Percep.IIBB:</td>
				      <td>
				        <input type="text" value="" name="<portlet:namespace />importe_percep_iibb" id="<portlet:namespace />importe_percep_iibb" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoConcepto();agregarCeros(this);"/>
				      </td>
				      <td>Jurisdicción:</td>
				      
				      
				      <td>
			             <select name="<portlet:namespace/>jurisdiccion_iibb" id="<portlet:namespace/>jurisdiccion_iibb"  >
					               <option value="0">Seleccione</option>
					               <%for (Localidad tnom : jurisdicciones) {%>
								     <option value="<%= tnom.getId_provincia() %>"  <%if(tnom.getId_provincia()==bsAs){%> selected="selected"  <% } %> >
								     <%=tnom.getDescripcion() %></option>
					               <%}%>
			              </select>
		              </td>
				      <td>Otros Tributos:</td>
				      <td>
				        <input type="text" value="" name="<portlet:namespace />importe_otros_tributos" id="<portlet:namespace />importe_otros_tributos" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="calculoConcepto();agregarCeros(this);"/>
				      </td>
				       <td>Total Concepto:</td>
			      <td>
				        <input type="text" value="" name="<portlet:namespace />importe_concepto" id="<portlet:namespace />importe_concepto" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
				               style="background:#AEB6BF" readonly="readonly" />
				               
				  </td> 
			      <td>
				      <input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarConcepto();" />
			      </td>
			</tr>
				    
			<tr>  
			     
			</tr>
	</table>		
	<%} %>
	
	<table  class="lfr-table" width="100%">
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
					<liferay-util:include page="/html/portlet/liquidaciones/comprobantes/conceptos_search_result_extendido.jsp">
						<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion) %>'/>
					</liferay-util:include>
				</div>
			</td>
		</tr>
	</table>
	</td>
<!-- 
aca estaba la columna de totales  -->	
		
  </tr>
</table>

<script type="text/javascript">

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
		
		var gravado = jQuery('#<portlet:namespace />importe_gravado').val();
		var tasaIva = jQuery("input:radio[name='<portlet:namespace />tasa_iva']:checked").val();
		var importeIva = jQuery('#<portlet:namespace />importe_iva').val();
		var importePercepIva = jQuery('#<portlet:namespace />importe_percep_iva').val();
		var importePercepIIBB = jQuery('#<portlet:namespace />importe_percep_iibb').val();
		var jurisdiccionIIBB = jQuery('#<portlet:namespace />jurisdiccion_iibb').val();
		var importeOtrosTributos = jQuery('#<portlet:namespace />importe_otros_tributos').val();
		
		if (trim(id) == "" ){
			alert("Debe ingresar el concepto");
			jQuery('#<portlet:namespace />agregandoIngreso').hide();	
			return false;
		}
		
		if (trim(importe) == "" || parseFloat(importe)==0){
			alert("Por favor ingrese el importe del concepto");
			jQuery('#<portlet:namespace />agregandoIngreso').hide();	
			return false;
		}

		if(parseFloat(tasaIva)>0 &&  Math.round(parseFloat(gravado)*parseFloat(tasaIva)*100)/100 +0.05
				< Math.round(parseFloat(importeIva)*100)/100 ){
			alert("El importe del IVA no corresponde a la tasa seleccionada");
			jQuery('#<portlet:namespace />agregandoIngreso').hide();	
			return false;
		}

		if(parseFloat(importePercepIIBB)>0 && (jurisdiccionIIBB==null || jurisdiccionIIBB=='' || jurisdiccionIIBB=='0')){
			alert("Debe seleccionar una jurisdicción de IIBB");
			jQuery('#<portlet:namespace />agregandoIngreso').hide();	
			return false;
		}
		
		var xporlet ='<%=portlet_name%>';
		var esEdicion='<%=esEdicion%>';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/__porlet/agregar_concepto_extendido" />'+
		'<liferay-portlet:param name="id_concepto" value="__id_concepto"/>'+
		'<liferay-portlet:param name="importe_concepto" value="__importe_concepto"/>'+
		'<liferay-portlet:param name="cuit" value="__cuit"/>'+
		'<liferay-portlet:param name="sucursal" value="__sucursal"/>'+
		'<liferay-portlet:param name="esEdicion" value="__esEdicion"/>'+
		'<liferay-portlet:param name="id_centro" value="__id_centro"/>'+
		'<liferay-portlet:param name="descripcion_centro" value="__descripcion_centro"/>'+
		'<liferay-portlet:param name="rnd" value="__rnd"/>'+
		'<liferay-portlet:param name="gravado" value="__gravado"/>'+
		'<liferay-portlet:param name="tasa_iva" value="__tasa_iva"/>'+
		'<liferay-portlet:param name="importe_iva" value="__importe_iva"/>'+
		'<liferay-portlet:param name="importe_percep_iva" value="__importe_percep_iva"/>'+
		'<liferay-portlet:param name="importe_percep_iibb" value="__importe_percep_iibb"/>'+
		'<liferay-portlet:param name="jurisdiccion_iibb" value="__jurisdiccion_iibb"/>'+
		'<liferay-portlet:param name="importe_percep_iva" value="__importe_percep_iva"/>'+
		'<liferay-portlet:param name="importe_percep_iibb" value="__importe_percep_iibb"/>'+
		'<liferay-portlet:param name="jurisdiccion_iibb" value="__jurisdiccion_iibb"/>'+
		'<liferay-portlet:param name="importe_otros_tributos" value="__importe_otros_tributos"/>'+
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
		url = url.replace("__gravado",gravado);
		url = url.replace("__tasa_iva",tasaIva);
		url = url.replace("__importe_iva",importeIva);
		url = url.replace("__importe_percep_iva",importePercepIva);
		url = url.replace("__importe_percep_iibb",importePercepIIBB);
		url = url.replace("__jurisdiccion_iibb",jurisdiccionIIBB);
		url = url.replace("__importe_otros_tributos",importeOtrosTributos);
				
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
													jQuery('#<portlet:namespace />agregandoIngreso').hide();	
													sugerirEnConcepto();
													jQuery('#<portlet:namespace />importe_gravado').val('');
													jQuery('#<portlet:namespace />importe_iva').val('');
													jQuery('#<portlet:namespace />importe_percep_iva').val('');
													jQuery('#<portlet:namespace />importe_percep_iibb').val('');
													jQuery('#<portlet:namespace />jurisdiccion_iibb').val('');
													jQuery('#<portlet:namespace />importe_otros_tributos').val('');
													jQuery('input:radio[name="<portlet:namespace />tasa_iva"][value="0"]').attr('checked',true);
													calculoConcepto();
									   }
		);	
	}

	function borraConcepto(id){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto_extendido'
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
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto_extendido'
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
	
	function borraConcepto(id, id_seccional,id_centro,tasa_iva){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto_extendido'
		+  '&id_concepto=' +id
		+  '&id_seccional=' +id_seccional
		+  '&id_centro='+id_centro
		+  '&tasa_iva='+tasa_iva
		+  '&esEdicion=' + "<%=esEdicion%>" ;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
																					jQuery('#<portlet:namespace />agregandoIngreso').hide();
																					sugerirEnConcepto();
																					calculoConcepto();
																		   }
														   );
    }
	
	
	function borraConcepto(id, id_seccional,id_centro,tasa_iva,importe){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_concepto_extendido'
		+  '&id_concepto=' +id
		+  '&id_seccional=' +id_seccional
		+  '&id_centro='+id_centro
		+  '&tasa_iva='+tasa_iva
		+  '&importe='+importe
		+  '&esEdicion=' + "<%=esEdicion%>" ;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />ingresos').load(url, function() {
																					jQuery('#<portlet:namespace />agregandoIngreso').hide();
																					sugerirEnConcepto();
																					calculoConcepto();
																		   }
														   );
    }
	
	function calculoIva(){
	  var pIva = jQuery("input:radio[name='<portlet:namespace />tasa_iva']:checked").val();
	  var gravado = jQuery('#<portlet:namespace />importe_gravado').val();
	  jQuery('#<portlet:namespace />importe_iva').val(Math.round(gravado*pIva*100)/100);
	  
    }
	
	
	
	function calculoConcepto(){
		  var iIva = jQuery('#<portlet:namespace />importe_iva').val();
		  var iGravado = jQuery('#<portlet:namespace />importe_gravado').val();
		  var iPercIva =jQuery('#<portlet:namespace />importe_percep_iva').val();
		  var iPercIIBB =jQuery('#<portlet:namespace />importe_percep_iibb').val();
		  var iOtrosTributos =jQuery('#<portlet:namespace />importe_otros_tributos').val();
		  
		  
		  iGravado = (iGravado == null || iGravado == undefined || iGravado == "") ? 0 : iGravado;
		  iIva = (iIva == null || iIva == undefined || iIva == "") ? 0 : iIva;
		  iPercIva = (iPercIva == null || iPercIva == undefined || iPercIva == "") ? 0 : iPercIva;
		  iPercIIBB = (iPercIIBB == null || iPercIIBB == undefined || iPercIIBB == "") ? 0 : iPercIIBB;
		  iOtrosTributos = (iOtrosTributos == null || iOtrosTributos == undefined || iOtrosTributos == "") ? 0 : iOtrosTributos;
		  
	      var iTotal=(parseFloat(iGravado)+parseFloat(iIva)+parseFloat(iPercIva)+parseFloat(iPercIIBB)+parseFloat(iOtrosTributos)).toFixed(2);

		  jQuery('#<portlet:namespace />importe_concepto').val(Math.round(iTotal*100)/100);
		  
	}
	
	
	
	jQuery('#<portlet:namespace />agregandoIngreso').hide();
</script>