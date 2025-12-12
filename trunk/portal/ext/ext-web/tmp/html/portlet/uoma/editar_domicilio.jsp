<%@ include file="/html/portlet/uoma/init.jsp" %>

<%
/* List<Provincia> provincias = (ArrayList<Provincia>) portletSession
		.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
				PortletSession.APPLICATION_SCOPE);

if (provincias == null) {
	provincias = TraeListasServiceUtil.getProvincias();
	portletSession.setAttribute(
			WebKeysAfiliados.PROVINCIAS_EN_SESSION,
			provincias,
			PortletSession.APPLICATION_SCOPE);	
}

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (localidades == null || localidades.size()==0) {
	localidades = TraeListasServiceUtil.getLocalidades();
	portletSession.setAttribute(
	WebKeysAfiliados.LOCALIDADES_EN_SESSION,
	localidades,
	PortletSession.APPLICATION_SCOPE);	
} */
String prefijo= request.getParameter("prefijo");
StringBuffer aux=new StringBuffer("domicilio_edit");
aux.append(prefijo!=null?prefijo:"");
Domicilio domicilio = (Domicilio) request.getAttribute(aux.toString());

%>

<table width="100%">
<tr>
	<td><label><liferay-ui:message key="provincia" />:</label></td>
	<td colspan="1">
			<select id="<portlet:namespace/>provincia<%=prefijo!=null?prefijo:""%>"
				name="<portlet:namespace/>provincia<%=prefijo!=null?prefijo:""%>" 
				onchange="javascript:filtrarLocalidad<%=prefijo!=null?prefijo:""%>();"
				style="width: 150px;">
					<option value="0">Seleccione una provincia</option>
				<%
					for (Provincia provincia : provincias) {
				%>
					<option value="<%= provincia.getId() %>" <%if(null!=domicilio && domicilio.getProvinciaId()==provincia.getId()){%>selected<%}%>><%=provincia.getDescripcion()%></option>
				<%
					}
				%>
			</select>
	</td>							
	<td><label><liferay-ui:message key="localidad" />:</label></td>			
	<td colspan="6">
		<div class="selector-localidad">
			<select id="<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>"  
				name="<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>" 
				onchange="javascript:filtrarCodPostal<%=prefijo!=null?prefijo:""%>();"
				style="width: 250px;">
				<option selected value="0">Seleccione una localidad</option>			
				<%
					for (Localidad localidad : localidades) {
				%>
				<option value="<%= localidad.getId() %>" <%if(null!=domicilio && domicilio.getLocalidadId()==localidad.getId()){%>selected<%}%> ><%=localidad.getDescripcion()%></option>
				<%
					}
				%>
			</select>
		</div>	
	</td>
</tr>
<% if (null==prefijo || (!prefijo.trim().equals("remi") && !prefijo.trim().equals("desti")) ){%>
	<tr>
		<td colspan="9">&nbsp;</td>
	</tr>		
	<tr>
		<td><label><liferay-ui:message key="calle" />:</label></td>
		<td colspan="1" style="vertical-align:top" >
			<liferay-util:include page='/html/portlet/uoma/busqueda_calle.jsp' >
				<liferay-util:param name='calle_edit' value='<%=null!=domicilio?domicilio.getCalle():""%>' />
				<liferay-util:param name='prefi' value='<%=null!=prefijo?prefijo:""%>' />
			</liferay-util:include>
		</td>
		<td colspan="1"><label><liferay-ui:message key="numero" />:</label></td>
		<td colspan="1">
			<input id="<portlet:namespace />numero<%=prefijo!=null?prefijo:""%>" name="<portlet:namespace />numero<%=prefijo!=null?prefijo:""%>" size="5" maxlength="5" type="text"
				onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" value="<%=null!=domicilio?domicilio.getNumero():""%>"/>
		</td>
		<div id='divCodPostal' style="float: right;"></div>	
			<td colspan="1"><label><liferay-ui:message key="piso" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />piso<%=prefijo!=null?prefijo:""%>"
				name="<portlet:namespace />piso<%=prefijo!=null?prefijo:""%>" size="5" maxlength="2" type="text" value="<%=null!=domicilio?domicilio.getPiso():""%>"/>
			</td>
			<td colspan="1">
				<label><liferay-ui:message key="departamento" />:</label>
			</td>
			<td colspan="1">
				<input id="<portlet:namespace />dpto<%=prefijo!=null?prefijo:""%>" name="<portlet:namespace />dpto<%=prefijo!=null?prefijo:""%>" size="5" maxlength="4" type="text" value="<%=null!=domicilio?domicilio.getDepto():""%>" />
			</td>
		</tr>
		<tr>
			<td colspan="1">&nbsp;</td>
		</tr>		
		<tr>
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td>
				<input id="<portlet:namespace />cod_postal<%=prefijo!=null?prefijo:""%>" name="<portlet:namespace />cod_postal<%=prefijo!=null?prefijo:""%>" size="5" maxlength="4" type="text" value="<%=null!=domicilio?domicilio.getPostal_codi():""%>"/>
			</td>
			<td>
				<label><liferay-ui:message key="observacion" />:</label>				
			</td>
			<td colspan="6">
				<input id="<portlet:namespace />obserDomicilio<%=prefijo!=null?prefijo:""%>" name="<portlet:namespace />obserDomicilio<%=prefijo!=null?prefijo:""%>" size="40" maxlength="200" type="text" value="<%=null!=domicilio?domicilio.getObservaciones():""%>"/>
			</td>
		</tr>
<%}%>		
</table>		
<script type="text/javascript">	
	<%-- function filtrarLocalidad<%=prefijo!=null?prefijo:""%>() {		
		var idProvincia = jQuery('#<portlet:namespace/>provincia<%=prefijo!=null?prefijo:""%>').val();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>").length = 0;						
				var obj = jQuery.parseJSON(data);
				addElementToSelect("<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>", "Seleccione una localidad", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>", text, value);					
				}				                                                                                                                                                                                                                                                            
			}
		});
	} --%>
	function filtrarLocalidad<%=prefijo!=null?prefijo:""%>() {
		var idProvincia = jQuery('#<portlet:namespace/>provincia<%=prefijo!=null?prefijo:""%>').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery("#<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>").attr('disabled', 'disabled');
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>").length = 0;
				jQuery("#<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>").removeAttr('disabled');
				var obj = jQuery.parseJSON(data);
//				for(var i =0;i< obj.listaFiltrada.length; i++){	
//					jQuery("#<portlet:namespace/>localidad").append(obj.listaFiltrada[i]);
//				}
				
				jQuery('.selector-localidad select').html(data).fadeIn();

			}
		});
	}

	function filtrarCodPostal<%=prefijo!=null?prefijo:""%>() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>').val();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/id_localidad_codpostal&idLocalidad='+idLocalidad;
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal<%=prefijo!=null?prefijo:""%>").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal<%=prefijo!=null?prefijo:""%>').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
	}

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	function <portlet:namespace />buscarCodPostalOnDiv(e) {
	    
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad<%=prefijo!=null?prefijo:""%>").val() == "265" && jQuery("#<portlet:namespace />calle<%=prefijo!=null?prefijo:""%>").val() != "" && jQuery("#<portlet:namespace />numero<%=prefijo!=null?prefijo:""%>").val() > 0) {
			var calle = jQuery("#<portlet:namespace />calle<%=prefijo!=null?prefijo:""%>").val();
			var numero = jQuery("#<portlet:namespace />numero<%=prefijo!=null?prefijo:""%>").val();
			if (calle.length > 0 && numero > 0) {				
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				jQuery("#divCodPostal").load(url);		
				jQuery("#divCodPostal").show();
			} else {        
	    		jQuery("#divCodPostal").hide("slow");
	   		}
		}
	}

	function <portlet:namespace />cerrarCodPostal() {	
		jQuery("#divCodPostal").hide("slow");
	}
</script>		