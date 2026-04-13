<%@ include file="/html/portlet/empresas/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
Empresa empresa = (Empresa)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);
LlamadosEstudio llest= null;
try{
	llest =(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
}catch(Exception e){
	//nada, esto me pasaba en portlet Empresas...
}
List<RamoEmpresa> ramos = (ArrayList<RamoEmpresa>) portletSession
.getAttribute(WebKeysEmpresas.RAMOS_EMPRESA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

List<Regimen> regimenes = (ArrayList<Regimen>) portletSession
.getAttribute(WebKeysEmpresas.REGIMENES_RET_GANANCIAS_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

List<EstadoGestion> estadosGestion = (ArrayList<EstadoGestion>) request.getSession().getAttribute(WebKeysEstudioIsidro.ESTADOS_GESTION);
/* if(estadosGestion==null){
	estadosGestion = EmpresaServiceUtil.getEstadosEmpresa();
	request.getSession().setAttribute(WebKeysEstudioIsidro.ESTADOS_GESTION, estadosGestion);
} */

Integer idEstado = empresa!=null&&empresa.getEstado()!=null?empresa.getEstado().getId():0;

String esEdicionStr=ParamUtil.getString(request,"esEdicion");
boolean esEdicion = true;
/* if (esEdicionStr != null && !esEdicionStr.trim().equals("")) {
	if (esEdicionStr.equals("true")){
		esEdicion = true;
	} else {
		esEdicion = false;
	}
} */
String idOp=(String)renderRequest.getAttribute("idOp");
String prefijo="empre_";

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>

<c:choose>
	<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	</c:when>
</c:choose>
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

<div id="<portlet:namespace />empresa_div" name="<portlet:namespace />empresa_div" >
<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos-empresa" /></legend>
<table class="lfr-table">
	<tr>		
		<td><b><label><liferay-ui:message key="cuit" />:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label></b></td>
		<td><input id="<portlet:namespace />cuit"
			name="<portlet:namespace />cuit" size="9" maxlength="11" type="text"
			value="<%= empresa != null ? empresa.getCuit() : "" %>"
			<% if (empresa != null) { %> <%="readonly='readonly'" %> <%}%> 
			onblur="<portlet:namespace />validoCuit()"/></td>
		<td><b><label><liferay-ui:message key="sucursal" />:</label></b></td>
		<td><input id="<portlet:namespace />sucursal"
			name="<portlet:namespace />sucursal" size="2" maxlength="5"
			type="text"
			value="<%= empresa != null ? empresa.getSucursal() : "000" %>"
			<% if (empresa != null) { %> <%="readonly='readonly'" %> <%}%> /></td>
		<td><b><label>&nbsp;<liferay-ui:message key="razon-social" />:</label></b></td>
		<td><input id="<portlet:namespace />desc"
			name="<portlet:namespace />desc" size="30" type="text"
			value="<%= empresa != null ? empresa.getRazon_soc() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />
		</td>
		<% if (empresa != null && empresa.getBaja_fecha() != null) { %> 
			<td><span style="font-size: 9pt; color: red;  " id="<portlet:namespace />empresaActiva"><label><b> INACTIVO  <%=sdf.format(empresa.getBaja_fecha()) %>  </b></label></span></td>
		<%}else if (empresa != null) { %>
			<td><span style="font-size: 9pt; color: green;  " id="<portlet:namespace />empresaInactiva"><label><b> ACTIVO  </b></label></span></td>
		<%} %>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>	
	<tr>
	 <%if (empresa!=null && empresa.getObservaciones() != null &&  empresa.getObservaciones().length() > 1){%>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
			<td colspan="7"><textarea cols="100" <% if (empresa != null) { %> <%="readonly='readonly'" %> <%}%>
			name="<portlet:namespace/>obs" id="<portlet:namespace/>obs"><%=empresa.getObservaciones() %></textarea>
		</td>
	<%} %>
	</tr>
</table>

<div align="center" id="<portlet:namespace />verificando">
				<table style="align:center;">
					<tr>
						<td>Verificando CUIT</td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
</div>	


<div>&nbsp;</div>
<%if(portlet_name.equals("empresas")||portlet_name.equals("liquidaciones")){ %>	
<table class="lfr-table">	
	<tr>			
		<td><label><liferay-ui:message key="seccional" />:</label></td>
		<td colspan="2">
				<liferay-util:include
					page="/html/portlet/empresas/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional"
						value="<%= empresa!=null ? String.valueOf(empresa.getId_seccional()) : new String() %>" />
					<liferay-util:param name="seccional" value="" />
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					<liferay-util:param name="prefijo" value="<%=prefijo%>"/>
				</liferay-util:include>
		</td>		
		<td><label><liferay-ui:message key="ramo" />:</label></td>
		<td><select  name="<portlet:namespace/>ramo" id="<portlet:namespace/>ramo" 
			<% if (!esEdicion) { %> disabled="disabled" <%} %> >
			<option value=""></option>
			<% for (RamoEmpresa ramo: ramos) { %>
			<option
				<%= empresa != null && empresa.getRamoEmpresa()!= null && empresa.getRamoEmpresa().equals(ramo) ? "selected" : ""  %>
				value="<%= ramo.getId_ramo_empresa() %>"><%=ramo.getDescripcion()%></option>
			<% } %>
		</select></td>		

		<td colspan="2">
				<liferay-util:include
					page="/html/portlet/empresas/busqueda_actividad.jsp">
					<liferay-util:param name="cod_actividad"
						value="<%= empresa!=null &&  null!=empresa.getActividadPrincipal() && empresa.getActividadPrincipal().getCodigo()!=0 ? String.valueOf(empresa.getActividadPrincipal().getCodigo()) : new String() %>" />
					<liferay-util:param name="cod_actividad_sec"
						value="<%= empresa!=null &&  null!=empresa.getActividadSecundaria() && empresa.getActividadSecundaria().getCodigo()!=0 ? String.valueOf(empresa.getActividadSecundaria().getCodigo()) : new String() %>" />	
					<liferay-util:param name="actividad" value="" />
					<liferay-util:param name="actividad_sec" value="" />
					<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					<liferay-util:param name="prefijo" value="<%=prefijo%>"/>
				</liferay-util:include></td>
		</td>
		<td><%=empresa != null && empresa.getRamoEmpresa()!= null && (empresa.getRamoEmpresa().getId_ramo_empresa()>0 &&
								  empresa.getRamoEmpresa().getId_ramo_empresa()!=99 ||
								  empresa.getRamoEmpresa().getId_ramo_empresa()!=90)?"ENCUADRADA":"SIN ENCUADRAR"%>
		</td>
		<td><label></label><liferay-ui:message key="F.U.M."/></label></td>
		<td><%=null!=empresa && null!=empresa.getModi_fechaAsString()?empresa.getModi_fechaAsString():""%> &nbsp; por &nbsp;<%=null!=empresa && null!=empresa.getModi_usr()?empresa.getModi_usr():""%></td>		
	</tr>
	<tr>			
		<td><label><liferay-ui:message key="cod-regimen" />:</label></td>
		
		<td colspan="10"><select  name="<portlet:namespace/>regimen" id="<portlet:namespace/>regimen" 
			<% if (!esEdicion) { %> disabled="disabled" <%} %> >
			<option value=""></option>
			<% for (Regimen reg: regimenes) { %>
			<option
				<%= empresa != null && empresa.getRegimen()!= null && empresa.getRegimen().getCodigoRegimen().equals(reg.getCodigoRegimen()) ? "selected" : ""  %>
				value="<%= reg.getCodigoRegimen() %>"><%=reg.getRegimenDescripcion()%></option>
			<% } %>
		</select></td>	
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>			
		<td><label><liferay-ui:message key="cbu-alias-transf" />:</label></td>
		<td colspan="10"><%=empresa != null && empresa.getCBU() != null ? empresa.getCBU() : "- NO CARGADO -"  %> </td>	
	</tr>		
</table>
<%} %>

<div>&nbsp;</div>


<div>&nbsp;</div>
<%if(portlet_name.equals("estudio_isidro")){ %>
<div id="<portlet:namespace />tabla_resumen">
<liferay-util:include page="/html/portlet/empresas/tabla_resumen_empleador.jsp">	
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>			
</liferay-util:include>
</div>
<%}%>
<table class="lfr-table" style="width:100%;" >
	<tr>
		<td >
			<liferay-util:include page="/html/portlet/empresas/cuentas_bancarias.jsp">	
				<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>			
			</liferay-util:include>
		</td>
	</tr>	
</table>
	
	<table style="width:100%;">
		<tr>
			<td>
				<liferay-util:include page="/html/portlet/empresas/view_datos_encuadramiento.jsp">	
					<liferay-util:param name="esEdicion" value="true"/>
				</liferay-util:include>
			</td>		
		</tr>
		<tr>
			<td>
				<liferay-util:include page="/html/portlet/empresas/view_datos_domi_y_contactos.jsp">	
					<liferay-util:param name="esEdicion" value="true"/>
				</liferay-util:include>
			</td>		
		</tr>
	</table>
	<table style="width:100%;">
		<tr>
			<td>
				<liferay-util:include page="/html/portlet/empresas/agregar_domicilio.jsp">
					<liferay-util:param name="esEdicion" value="true"/>
				</liferay-util:include>
			</td>		
		</tr>
	</table>
	
	<table style="width:100%;">
	<tr>
		<td>
			<liferay-util:include page="/html/portlet/empresas/agregar_contacto.jsp">
				<liferay-util:param name="esEdicion" value="true"/>
			</liferay-util:include>
		</td>		
	</tr>
	<tr>
		<td>
			<liferay-util:include page="/html/portlet/empresas/agregar_contacto_personalizados.jsp">
				<liferay-util:param name="esEdicion" value="true"/>
			</liferay-util:include>
		</td>		
	</tr>		
	</table>
	<div>&nbsp;</div>		
	
	
</fieldset>
<% if (esEdicion) { %>
<br />

<div align="center" id="<portlet:namespace />procesando_save_empresa">
	<table style="align:center;">
		<tr>
			<td><liferay-ui:message key="Grabando..."/></td>
			<td align="center">
				<img alt="<liferay-ui:message key='grabando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
			</td>
		</tr>
	</table>		
</div>	

<div id="<portlet:namespace />ocultarSaveEmpresa" align="center">

<input type="submit" value="<liferay-ui:message key="save" />"
	<%if(portlet_name.equals("estudio_isidro")) { %>
		onClick="<portlet:namespace />saveEmpleadorDiv();return false;"
	<%}else if (!portlet_name.equals("liquidaciones") ) { %>
		onClick="<portlet:namespace />saveEmpleador();return false;"	 
	<%}else{ %>
		onClick="<portlet:namespace />saveEmpleadorPopUp();return false;"
	<%} %> />
</div>	
	
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
<input type="hidden" value="" name="tabs1" id="tabs1" />
<input type="hidden" value="" name="view" id="view" />
<input type="hidden" value="" name="flag" id="flag" />
<input type="hidden" name="idOp" id="idOp" value="<%=idOp%>" />
<input type="hidden"
	value="<%= request.getAttribute("accionOriginal") != null && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (empresa == null ? Constants.ADD : Constants.UPDATE)%>"
	name="accionOriginal" id="accionOriginal" />
<%} %>
			<div align="center" id="<portlet:namespace />saveEmpleadorDiv">						
			</div>
</div>			
<script type="text/javascript">
jQuery('#<portlet:namespace />procesando_save_empresa').hide();
<%if(portlet_name.equals("estudio_isidro")){%>	
//jQuery('#<portlet:namespace />ocultar_obs').css('display','none')
jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','none');
jQuery('#<portlet:namespace />procesando_save_empresa').hide();
<%}%>
<%if(portlet_name.equals("empresas")){%>	
//jQuery('#<portlet:namespace />ocultar_obs').css('display','none')
jQuery('#<portlet:namespace />ocultarDatosDomiyContac').css('display','none');
jQuery('#<portlet:namespace />ocultarDatosGral').css('display','none');
jQuery('#<portlet:namespace />procesando_save_empresa').hide();

<%}%>
<%if(empresa != null && empresa.getBaja_fecha() != null){%>	
alert ("Empresa de baja, contacte a sistemas!");
jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','none');
jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','none');
jQuery('#<portlet:namespace />ocultarSaveEmpresa').hide();

<%}%>


	<%-- function <portlet:namespace />showHideDivObservaciones(){		
		if (jQuery("#<portlet:namespace />ocultar_obs").css('display') === 'none') {
			jQuery('#<portlet:namespace />ocultar_obs').css('display','block')
			jQuery('#<portlet:namespace />arrow_observaciones').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_x.png');
		}else{
			jQuery('#<portlet:namespace />ocultar_obs').css('display','none')
			jQuery('#<portlet:namespace />arrow_observaciones').attr('src','<%=themeDisplay.getPathThemeImages()%>/arrows/02_plus.png');
		}
	} --%>
	
	function <portlet:namespace />showHideDivSaveEmpresa(){		
		if (jQuery("#<portlet:namespace />ocultarSaveEmpresa").css('display') === 'none') {
			jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block')
		}else{
			jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','none')
		}
	}

	function <portlet:namespace />saveEmpleador() {
		
		if (<portlet:namespace />validarCampos()) {			
			
			document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (empresa == null ? Constants.ADD : Constants.UPDATE) %>";
			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry';
			<% if(portlet_name.equals("estudio_isidro")) {%>
				url=url+'&flagEstudio=true';
			<%}%>			
			document.<portlet:namespace />emple.method = 'post';			
			document.getElementById("cambioSolapa").name = "xx";			
			document.getElementById("tabs1").name = "xx2";			
			submitForm(document.<portlet:namespace />emple, url);
		} 
	}
	function <portlet:namespace />saveEmpleadorDiv() {
		if (<portlet:namespace />validarCampos()) {			
			var form = jQuery(document.<portlet:namespace />emple);
			var url = '<portlet:actionURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry';
			document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (empresa == null ? Constants.ADD : Constants.UPDATE) %>";		
			var cuit=jQuery('#<portlet:namespace />cuit').val();
			form.ajaxForm(
				{
					url: url,
			    	//target: tar,//".ui-dialog-content",//poopup
			        type: "POST",
			        beforeSubmit: function() {
			        	<% 
		        		if (portlet_name.equals("estudio_isidro")) { %>	
			        	jQuery('#<portlet:namespace />procesando_save_empresa').show();
			        	<%}%>
			        },
			        success: function(data) {			        	
			        	jQuery('#<portlet:namespace />empresa_div').html(data);
			        		<% 
			        		if (portlet_name.equals("liquidaciones")) { %>			        		
			        		sugerirRazonSocialChequeYDestino(id_op);
			        	<%}%>
			        	
			        	<% 
		        		if (portlet_name.equals("estudio_isidro")) { %>	
		        		   refrescarDatosEmpresa(cuit);
		        	    <%}%>
			        }
			    }
			);	
			jQuery('#<portlet:namespace />procesando_save_empresa').hide();			
			form.submit();
		}
	}
	
	
	function <portlet:namespace />saveEmpleadorPopUp(){		
		if (<portlet:namespace />validarCampos()) {
			document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
			document.getElementById("cambioSolapa").value="cambioSolapa";
			document.getElementById("cambioSolapa").name = "xx";			
			document.getElementById("tabs1").name = "xx2";
			
			var form = jQuery(document.<portlet:namespace />emple);
			var url = '<portlet:actionURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry';
			var id_op= document.getElementById("idOp").value;			
			document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (empresa == null ? Constants.ADD : Constants.UPDATE) %>";		
			var cuit=jQuery('#<portlet:namespace />cuit').val();
			
			<% if(portlet_name.equals("estudio_isidro")) {%>
			url=url+'&popupSeguimiento=true';
		    <%}%>
			
			form.ajaxForm(
				{
					url: url,
			    	target: popup,//".ui-dialog-content",//poopup
			        type: "POST",
			        beforeSubmit: function() {			        
			        },
			        success: function() {
			        		<% 
			        		if (portlet_name.equals("liquidaciones")) { %>			        		
			        		sugerirRazonSocialChequeYDestino(id_op);
			        	<%}%>
			        	
			        	<% 
		        		if (portlet_name.equals("estudio_isidro")) { %>			        		
		        		   refrescarDatosEmpresa(cuit);
		        	    <%}%>
			        }
			    }
			);	
						
			form.submit();    
		}
	}

	
	
	function <portlet:namespace />validarCampos() {		
		var tiene_dom=jQuery('#<portlet:namespace />tiene_domicilios').val();
		var tiene_email=jQuery('#<portlet:namespace />tiene_email').val();
		
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
		var calle=jQuery('#<portlet:namespace />calle').val();
		var cbu=jQuery('#<portlet:namespace />cbu').val();
		try {
			if (trim(jQuery("#<portlet:namespace />desc").val()).length == 0) {
				alert("<liferay-ui:message key='nombre-obligatorio' />");
				jQuery('#<portlet:namespace />desc').focus();
				return false;
			}

			if (trim(cuit).length == 0){
				alert("<liferay-ui:message key='valida-cuit' />");
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
							
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
			
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;			
			}
			
			
			if (!isInteger(jQuery('#<portlet:namespace />sucursal').val())){
				alert("<liferay-ui:message key='valida-sucu' />");
				jQuery('#<portlet:namespace />sucursal').focus();
				return false;
			}
						
			
<%-- 			if(jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val() == ""){				
				alert("<liferay-ui:message key='seccional_invalida' />");
				jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val() != "" && jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val()!="1"){				
				alert("<liferay-ui:message key='seccional_invalida' />");
				jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").focus();
				return false;
			} --%>			
			if(document.getElementById('<portlet:namespace/>ramo').selectedIndex == 0){
				alert("<liferay-ui:message key='ramo-obligatorio' />");
				jQuery('#<portlet:namespace />ramo').focus();
				return false;
			}
			
			<%if(portlet_name.equals("empresas")){ %>
				if(cbu.trim().length>0 && !validarCBU(cbu, "<liferay-ui:message key='valida-cbu'/>")){
					jQuery('#<portlet:namespace />cbu').focus();
					return false;
				}
			<%}%>
			
			if(tiene_dom=='false'){
				alert('Debe ingresar al menos un domicilio');
				return false;
			}
			
			/*if(cbu.trim().length>0 && tiene_email=='false'){
				alert('Debe ingresar al menos un email si ingresa CBU');
				return false;
			}*/
			
		} catch (err) {
			return false;
		}
		return true;
	}

	function submitFormNotSave(){
		
		if (<portlet:namespace />validarCampos()) {
			document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
			document.getElementById("cambioSolapa").value="cambioSolapa";
			document.getElementById("tabs1").value="datos-fiscales";
			document.getElementById("view").value="true";
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry';
			document.<portlet:namespace />emple.method = 'post';
			submitForm(document.<portlet:namespace />emple, url);
			
		}
	}

	function filtrarLocalidad() {		
		var idProvincia = jQuery('#<portlet:namespace/>provincia').val();		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_provincia_localidad&idProvincia='+idProvincia;
		jQuery("#<portlet:namespace/>localidad").attr('disabled', 'disabled');
		jQuery.ajax({   
			url: url,
			async: false,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;	
				jQuery("#<portlet:namespace/>localidad").removeAttr('disabled');
				var obj = jQuery.parseJSON(data);
				jQuery('.selector-localidad select').html(data).fadeIn();
				/*
				addElementToSelect("<portlet:namespace/>localidad", "Seleccione una localidad", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>localidad", text, value);
				}
				*/
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

	function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/id_localidad_codpostal&idLocalidad='+idLocalidad;		
		
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
	}

	function <portlet:namespace />buscarCodPostalOnDiv(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad").val() == "265" && jQuery("#<portlet:namespace />calle").val() != "" && jQuery("#<portlet:namespace />numero").val() > 0) {
			var calle = jQuery("#<portlet:namespace />calle").val();
			var numero = jQuery("#<portlet:namespace />numero").val();
			if (calle.length > 0 && numero > 0) {				
				
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				
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
	
	jQuery('#<portlet:namespace />observaciones').bind('input propertychange', function() {	      
	      if(trim(this.value).length){
	    	  jQuery('#<portlet:namespace />ocultarSaveEmpresa').css('display','block');
	      }
	});
	
	function <portlet:namespace />validoCuit(){
		var accion = jQuery("#accionOriginal").val();
		var cuit = jQuery("#<portlet:namespace />cuit").val();
		if("add"==accion && cuit !=null){
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/existe_cuit_empresa'
				+'&cuit='+cuit;
			jQuery('#<portlet:namespace />verificando').show();
			jQuery.ajax({   
				url: url,
				async: false,
				success: function(data){
					var obj = jQuery.parseJSON(data);
					var existe=obj.existe;
					jQuery('#<portlet:namespace />verificando').hide();
					if(existe=="true"){
						
					   alert("El cuit ya existe en la Base de Datos");
					}   
				}
			});	
		}
		
	}
	
	
	jQuery('#<portlet:namespace />verificando').hide();	
</script>