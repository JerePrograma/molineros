<%@ include file="/html/portlet/empresas/init.jsp"%>
<%
EntidadPadronUnificado empresa = (EntidadPadronUnificado)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);				
if(empresa==null){
	LlamadosEstudio llest=(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
	empresa=(llest!=null?llest.getEmpresa():null);
}

List<Provincia> provincias = (ArrayList<Provincia>) portletSession
.getAttribute(WebKeysEmpresas.PROVINCIAS_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysEmpresas.LOCALIDADES_EN_SESSION,
PortletSession.APPLICATION_SCOPE);

List<RamoEmpresa> ramos = (ArrayList<RamoEmpresa>) portletSession
.getAttribute(WebKeysEmpresas.RAMOS_EMPRESA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

boolean esEdicion = false;
esEdicion = true;


//DS - Manejo Localidades por Provincia
Map<Integer,List<Localidad>> localidadesPorProvincia = (Map<Integer,List<Localidad>>) portletSession
.getAttribute(WebKeysEmpresas.LOCALIDADES_EN_SESSION_POR_PROVINCIA, PortletSession.APPLICATION_SCOPE);

if (localidadesPorProvincia == null || localidadesPorProvincia.size()==0) {
	localidadesPorProvincia = new HashMap<Integer,List<Localidad>>();	 
		
	for(Localidad l:localidades){
		if(l!=null && l.getId_provincia()>0 && l.getDescripcion()!=null && !"".equalsIgnoreCase(l.getDescripcion().trim() )){
			List<Localidad> lst =  new ArrayList<Localidad>();
			try{
			  lst = localidadesPorProvincia.get(l.getId_provincia());
			  if(lst==null) lst =  new ArrayList<Localidad>();
			}catch(Exception e){
			  lst =  new ArrayList<Localidad>();
			}
			lst.add(l);
			localidadesPorProvincia.put(l.getId_provincia(), lst);
	   }
	}
	
	portletSession.setAttribute(WebKeysEmpresas.LOCALIDADES_EN_SESSION_POR_PROVINCIA,
			localidadesPorProvincia,PortletSession.APPLICATION_SCOPE);

}

//DS



%>

<%if(empresa==null || empresa instanceof Empresa){%>
<div id="<portlet:namespace/>ocultarDomicilios">
<fieldset class="block-labels">

<legend>
<liferay-ui:message	key="domicilios" /> 
</legend>

<table class="lfr-table" style="border-collapse: separate; border-spacing: 1px;">
	<tr>		
			<!-- <td><label><liferay-ui:message key="tipo" />:</label></td> -->
			<%-- <td>
				<select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>tipo_domicilio_empre" id="<portlet:namespace/>tipo_domicilio_empre">
				<option selected value="PORTAL">PORTAL</option>				
				<option selected value="FISCAL">FISCAL</option>
				<option selected value="SEDE">SEDE ADM.</option>
				<option selected value="SUCURSAL">SUCURSAL</option>
				</select>
			</td> --%>
			<td>
				<input type="hidden" name="<portlet:namespace/>tipo_domicilio_empre" id="<portlet:namespace/>tipo_domicilio_empre" value="PORTAL" >
				<input type="hidden" name="<portlet:namespace/>id_domicilio" id="<portlet:namespace/>id_domicilio" value="" >
			</td>
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td>
				<select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
				<%}%> name="<portlet:namespace/>provincia" id="<portlet:namespace/>provincia" 
				onchange="javascript:filtrarLocalidad();" style="width: 100px">
				<option selected value="0">Seleccione una provincia</option>
				<% for (Provincia provincia : provincias) { %>
				<option value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
				<% } %>
				</select>
			</td>
			<td><label><liferay-ui:message key="localidad" />:</label></td>
            <td> 
            <div class="selector-localidad">
			   <%if(empresa != null) {%>
			    <select id="<portlet:namespace/>localidad"
				  name="<portlet:namespace/>localidad" <% if (!esEdicion) { %>
				  disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();"
				  style="width: 100px;" >
					<option selected value="0">Seleccione una localidad</option>
					<%	for (Localidad localidad : localidades) {	%>
					<option	value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
					<%	}	%>
			    </select>
			  <%} else{%>
			  	<select id="<portlet:namespace/>localidad"
				name="<portlet:namespace/>localidad" <% if (!esEdicion) { %>
				disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();" style="width: 100px;">
					<option selected value="0">Seleccione una localidad</option>
				 </select>	
			  <%} %>		
			 </div>			
			</td>
			
			<td><label><liferay-ui:message key="cod-postal" />:</label></td>
			<td>
				<input id="<portlet:namespace />cod_postal"
				name="<portlet:namespace />cod_postal" size="5" type="text"			
				<% if (!esEdicion) { %> readonly="readonly" %> <%}%> />
			</td>
			<td><label><liferay-ui:message key="calle" />:</label></td>
			<td>
			<liferay-util:include page='/html/portlet/empresas/busqueda_calle_empleador.jsp'>			
			</liferay-util:include>	
			</td>
			<td><label><liferay-ui:message key="numero" />:</label></td>
			<td><input id="<portlet:namespace />numero"
				name="<portlet:namespace />numero" size="5" maxlength="5" type="text"			
				onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" /></td>
			<div id='divCodPostal' style="float: right;"></div>	
			<td><label><liferay-ui:message key="piso" />:</label></td>
			<td><input id="<portlet:namespace />piso"
				name="<portlet:namespace />piso" size="5" maxlength="5" type="text" /></td>
	
			<td><label><liferay-ui:message key="departamento" />:</label></td>
			<td><input id="<portlet:namespace />departamento"
				name="<portlet:namespace />departamento" size="5" maxlength="5" type="text"/></td>	
			<td><label><liferay-ui:message key="observaciones" />:</label></td>
			<td><input id="<portlet:namespace />observa_domi" name="<portlet:namespace />observa_domi" size="10" type="text"/></td>		
			<td>
			<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarDomicilio();" />
			</td>		
	</tr>	
<%} %>		
	<tr style="width:100%;">
		<td colspan="19" >
			<div align="center" id="<portlet:namespace />domicilios">
				<liferay-util:include page="/html/portlet/empresas/domicilios_search_result.jsp">			
				</liferay-util:include>
			</div>
		</td>
	</tr>
</table>
</fieldset>
</div>	
	
<script type="text/javascript">	
<%if(portlet_name.equals("estudio_isidro")){%>	
jQuery('#<portlet:namespace />ocultarDomicilios').css('display','none')		
<%}%>
function <portlet:namespace />showHideDivDomicilios(){		
	if (jQuery("#<portlet:namespace />ocultarDomicilios").css('display') === 'none') {
		jQuery('#<portlet:namespace />ocultarDomicilios').css('display','block')
		jQuery('#<portlet:namespace />arrow_domicilios').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
	}else{
		jQuery('#<portlet:namespace />ocultarDomicilios').css('display','none')
		jQuery('#<portlet:namespace />arrow_domicilios').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
	}
}
	function <portlet:namespace />agregarDomicilio(){				
			
			var provincia=jQuery('#<portlet:namespace />provincia').val();			
			var localidad=jQuery('#<portlet:namespace />localidad').val();			
			var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
			var calle=jQuery('#<portlet:namespace />calle').val();				
			var numero=jQuery('#<portlet:namespace />numero').val();
			var piso=jQuery('#<portlet:namespace />piso').val();
			var departamento=jQuery('#<portlet:namespace />departamento').val();
			var tipo_domi_empre=jQuery('#<portlet:namespace />tipo_domicilio_empre').val();
			var observaciones=jQuery('#<portlet:namespace />observa_domi').val();
			var id_domi=jQuery('#<portlet:namespace />id_domicilio').val();
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_domicilio';			
			url=url+'&provincia=' + provincia
						+'&localidad=' + localidad 
						+'&cod_postal=' + cod_postal
						+'&calle='+ encodeURI(calle)
						+'&numero='+ numero
						+'&piso='+piso
						+'&departamento='+departamento
						+'&tipo_domi_empre='+tipo_domi_empre
						+'&observaciones='+encodeURI(observaciones)
						+'&id_domicilio='+id_domi
						+'&accion=ADD';
								
			jQuery('#<portlet:namespace />domicilios').load(url, function() {																
														jQuery('#<portlet:namespace />provincia').val("0");			
														jQuery('#<portlet:namespace />localidad').val("0");
														jQuery('#<portlet:namespace />cod_postal').val("");
														jQuery('#<portlet:namespace />calle').val("");				
														jQuery('#<portlet:namespace />numero').val("");
														jQuery('#<portlet:namespace />piso').val("");
														jQuery('#<portlet:namespace />departamento').val("");
														jQuery('#<portlet:namespace />observa_domi').val("");
														jQuery('#<portlet:namespace />id_domicilio').val("");
														<%if(portlet_name.equals("estudio_isidro")){%>	
															jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block');		
														<%}%>														
										   }
			 );	
	}
	

	function borraDomicilio(domi_tipo, provincia, localidad, cod_postal, calle, numero, piso, departamento, id_domicilio){	
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_domicilio';		
			url=url+'&tipo_domi_empre=' + domi_tipo
			+'&provincia=' + provincia
			+'&localidad=' + localidad 
			+'&cod_postal=' + cod_postal
			+'&calle='+ encodeURI(calle)
			+'&numero='+ numero
			+'&piso='+piso
			+'&departamento='+departamento
			+'&id_domicilio='+id_domicilio	
			+'&accion=DELETE';			
			jQuery('#<portlet:namespace />domicilios').load(url, function() {
				<%if(portlet_name.equals("estudio_isidro")){%>	
					jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block');		
				<%}%>				
			});
		}	
	}
	
	function editaDomicilio(domi_tipo, provincia, localidad, cod_postal, calle, numero, piso, departamento, id_domicilio, observaciones){
		/* filtrarLocalidad(); */
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_domicilio';		
		url=url+'&tipo_domi_empre=' + domi_tipo
		+'&provincia=' + provincia
		+'&localidad=' + localidad 
		+'&cod_postal=' + cod_postal
		+'&calle='+ encodeURI(calle)
		+'&numero='+ numero
		+'&piso='+piso
		+'&departamento='+departamento
		+'&id_domicilio='+id_domicilio	
		/* +'&accion=DELETE'; */
		+'&accion=EDIT';	
		jQuery('#<portlet:namespace />domicilios').load(url, function() {
			jQuery('#<portlet:namespace />tipo_domicilio_empre').val(domi_tipo);
			jQuery('#<portlet:namespace />provincia').val(provincia);
			filtrarLocalidad();
			jQuery('#<portlet:namespace />localidad').val(localidad);
 			jQuery('#<portlet:namespace />cod_postal').val(cod_postal);
			jQuery('#<portlet:namespace />calle').val(calle);				
			jQuery('#<portlet:namespace />numero').val(numero);
			jQuery('#<portlet:namespace />piso').val(piso);
			jQuery('#<portlet:namespace />departamento').val(departamento);
			jQuery('#<portlet:namespace />observa_domi').val(observaciones);
			jQuery('#<portlet:namespace />id_domicilio').val(id_domicilio);
			<%if(portlet_name.equals("estudio_isidro")){%>	
				jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block');		
			<%}%>	
		});
			
	}
</script>