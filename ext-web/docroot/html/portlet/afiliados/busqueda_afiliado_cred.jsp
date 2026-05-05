<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>




<portlet:defineObjects/>
		
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="entidad" />:</label></td>
						<td>
							<select name="<portlet:namespace/>entidad" id="<portlet:namespace/>entidad">
									<%
										for (String entidad : WebKeysGlobal.ENTIDADES_UOMA) {
									%>
										<option value="<%= entidad %>"><%=entidad%></option>
										<%= entidad == WebKeysLiquidaciones.ID_DEFAULT_ENTIDAD ? "selected" : ""  %>
									<%
									}
									%>
							</select>
						</td>
						<td><label><liferay-ui:message key="numero-afi" />:</label></td>
						<td><input id="<portlet:namespace />numero_afi" name="<portlet:namespace />numero_afi" size="6" maxlength="10" type="text" value="" /></td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="cuil" />:</label></td>
						<td><input id="<portlet:namespace />cuil" name="<portlet:namespace />cuil" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="integrante" />:</label></td>
						<td><input id="<portlet:namespace />inte" name="<portlet:namespace />inte" size="2" maxlength="2" type="text" value="" /></td>
						<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
						<td>
							<select name="<portlet:namespace/>tipoDoc" id="<portlet:namespace/>tipoDoc">
									<option value=""></option>
									<%
										for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {
									%>
										<option value="<%= tipoDoc %>"><%=tipoDoc%></option>
									<%
									}
									%>
							</select>
						</td>
						<td><label><liferay-ui:message key="nro-documento" />:</label></td>		
						<td><input id="<portlet:namespace />nroDoc" name="<portlet:namespace />nroDoc" size="9" maxlength="13" type="text" value="" /></td>
						<td><label><liferay-ui:message key="seccional" />:</label></td>		
						<td colspan="2" rowspan="3" style="vertical-align:top" ><jsp:include page='/html/portlet/afiliados/busqueda_seccional.jsp' /></td>
					</tr>
					<tr>
						<td colspan="12">&nbsp;</td>
					</tr>
					<tr>
						<td><label><liferay-ui:message key="apellido" />: </label></td>
						<td colspan="2"><input id="<portlet:namespace />apellido" name="<portlet:namespace />apellido" size="20" maxlength="100" type="text" value="" /></td>
						<td><label><liferay-ui:message key="nombre" />:</label></td>
						<td colspan="2"><input id="<portlet:namespace />nombre" name="<portlet:namespace />nombre" size="20" maxlength="100" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td coslpan="1">							
							<input id="<portlet:namespace />buscarAfiliado" value="<liferay-ui:message key="buscar-afiliado"/>" title="<liferay-ui:message key="buscar-afiliado" />" type="button" onClick="javascript:<portlet:namespace />buscarAfiliados();"/>
							<input id="<portlet:namespace />limpiar-campos" value="<liferay-ui:message key="limpiar-campos"/>" title="<liferay-ui:message key="limpiar-campos" />" type="button" onClick="javascript:resetForm();"/>
							<div id="<portlet:namespace />seleccionarAfiliadoDiv">
								<input id="<portlet:namespace />seleccionarAfiliadoF" value="<liferay-ui:message key="choose"/>" title="<liferay-ui:message key="choose" />" type="button" onClick="javascript:pedirCredencialFromForm();"/>								
							</div>							
						</td>												
					</tr>					
				</table>
				
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
					<div align="center" id="<portlet:namespace />busquedaAfiliadoDiv">						
					</div>
					
				
				<div align="right">
					<input id="<portlet:namespace />imprimirCredencialExentoCopago" value="<liferay-ui:message key="imprimir-credenciales-exento"/>" title="<liferay-ui:message key="imprimir-credenciales" />" type="button" onClick="javascript:imprimirCredencialExentoCopago();"/>
					<input id="<portlet:namespace />imprimirCredenciales" value="<liferay-ui:message key="imprimir-credenciales"/>" title="<liferay-ui:message key="imprimir-credenciales" />" type="button" onClick="javascript:imprimirCredenciales();"/>
					<input id="<portlet:namespace />imprimirCredencialesCES" value="Imprimir Credenciales CES" title="Imprimir Credenciales CES" type="button" onClick="javascript:imprimirCredencialesCES();"/>
					<input id="<portlet:namespace />borrarLista" value="<liferay-ui:message key="borrar-lista"/>" title="<liferay-ui:message key="borrar-lista" />" type="button" onClick="javascript:borraLista();"/>
				</div>	 
			
<script type="text/javascript">
	var popupAfill;
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />seleccionarAfiliadoDiv').hide();	
	var aux='';
	<portlet:namespace />pedirCredencial(aux);
	function imprimirCredenciales(){		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/genera_credencial&imprimir=true';
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="print" />",modal:true,width:830});
		jQuery(popupAfill).load(url, function() {
			jQuery('#<portlet:namespace />buscando').hide();
			Liferay.Popup.close(popupAfill);		            															
		 }
		 );
	}
	
	function imprimirCredencialExentoCopago(){		
			
			if (validarCredencialExentoCoPago()){
					
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/genera_credencial&imprimir=EXENTO_DE_COPAGO';
				
				popupAfill = Liferay.Popup({title:"<liferay-ui:message key="print" />",modal:true,width:830});
					jQuery(popupAfill).load(url, function() {
						
						
				        jQuery('#<portlet:namespace />buscando').hide();
						Liferay.Popup.close(popupAfill);		            															
				}
				);

			}

	}	


	
	function validarCredencialExentoCoPago(){
		 var respuesta=true;
		<%Map<String, Afiliado> mapCredenciales = (Map<String, Afiliado>) portletSession.getAttribute(WebKeysAfiliados.CREDENCIALES_A_IMPRIMIR,
				PortletSession.APPLICATION_SCOPE);	
				boolean  flag = false;		
		
				if (mapCredenciales != null){
				    
					ArrayList<Afiliado> afiliados = new ArrayList<Afiliado>(mapCredenciales.values());
	
					for(Afiliado afi : afiliados) {
			
						if(afiliados.size()  == 0 || afiliados.size() > 1 ||
									(CredencialesServiceUtil.validarExisteExentoCopago(afi.getCuil_titular(),afi.getInte())==0)){
							flag = true;	
						}
					}	
				}else{
					flag = true;
				}
			  %>
			<%  if (flag){%>
				alert("No existe credencial para la selección ");	
				 respuesta=false;
			<%}%>
		 	return  respuesta;    
	}
	
	
	function <portlet:namespace />pedirCredencial(inputs){		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/genera_credencial'+inputs;
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, function() {																					
																					jQuery('#<portlet:namespace />buscando').hide();																					
																					if(popupAfill!=null){
																						Liferay.Popup.close(popupAfill);
																					}																																
																				 }
																);		
	}

	function pedirCredencialFromForm(){		
		var cuil=jQuery('#<portlet:namespace />cuil').val();		
		var inte=jQuery('#<portlet:namespace />inte').val();		
		var inputs='&credenciales=-'+cuil+'|'+inte;		
		
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/genera_credencial&inputs';
		 url = url.replace("inputs", encodeURI(inputs));

		 
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, function() {																					
																					jQuery('#<portlet:namespace />buscando').hide();
																					jQuery('#<portlet:namespace />seleccionarAfiliadoDiv').hide();
																					jQuery('#<portlet:namespace />cuil').val('');
																					jQuery('#<portlet:namespace />inte').val('');
																					jQuery('#<portlet:namespace />tipoDoc').val('');
																					jQuery('#<portlet:namespace />nroDoc').val('');
																					jQuery('#<portlet:namespace />id_seccional').val('');
																					jQuery('#<portlet:namespace />seccional').val('');		
																					jQuery('#<portlet:namespace />apellido').val('');
																					jQuery('#<portlet:namespace />nombre').val('');																					
																					jQuery('#<portlet:namespace />numero_afi').val('');
																																											
																					if(popupAfill!=null){
																						Liferay.Popup.close(popupAfill);
																					}																																
																				 }
																);		
	}

	function borraLista(){		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/genera_credencial&borrarLista=true';
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, function() {
			jQuery('#<portlet:namespace />buscando').hide();					            															
		 }
		);		
	}

	function borraCredencial(mapid){
		var inputs= mapid;		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/genera_credencial&creden=+inputs+&borrar=true';
		url = url.replace("inputs", encodeURI(inputs));
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, function() {
			jQuery('#<portlet:namespace />buscando').hide();
			Liferay.Popup.close(popupAfill);		            															
		 }
		);		
	}
		
	function <portlet:namespace />buscarAfiliados(){
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional').val();		
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();		
		
		if(!<portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi)){
			return false;
		}
		if(cuil.length>0){
			if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil').focus();
				return false;
			}
		}		
		var credencial='ospim';
		if(entidad=='U.O.M.A.'){
			credencial='uoma';
		}else if(entidad=='A.M.T.I.M.A.'){
			credencial='amtima';
		}
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}		
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="grupo-filtro-busqueda-afiliado" />",modal:true,width:830});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliados_cred&cuil='+cuil+
		'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+nroDoc+'&seccional='+seccional+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+'&entidad='+entidad+'&numero_afi='+numero_afi+'&popup=true&checkbox=true&cred='+credencial;
        jQuery(popupAfill).load(url);
	}
	
	function <portlet:namespace />validarBusqueda(cuil,inte,tipoDoc,nroDoc,seccional,apellido,nombre,entidad,numero_afi){		
		if(trim(cuil.length)==0 && trim(inte.length)==0 && trim(tipoDoc.length)==0 && trim(nroDoc.length)==0 && trim(seccional.length)==0 &&  
		   trim(apellido.length)==0 && trim(nombre.length)==0 && trim(entidad.length)==0 && trim(numero_afi.length)==0){
			alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
			return false;
		}else{
			return true;
		}
	}
	
	function seleccionaAfiliado(cuil,inte,docu_tipo,docu_nro,nombre,apellido,id_secc,desc_secc,ospim,uoma,amtima,bajaFecha){
		jQuery('#<portlet:namespace />cuil').val(cuil);
		jQuery('#<portlet:namespace />inte').val(inte);
		jQuery('#<portlet:namespace />tipoDoc').val(docu_tipo);
		jQuery('#<portlet:namespace />nroDoc').val(docu_nro);
		jQuery('#<portlet:namespace />id_seccional').val(id_secc);
		jQuery('#<portlet:namespace />seccional').val(desc_secc);		
		jQuery('#<portlet:namespace />apellido').val(apellido);
		jQuery('#<portlet:namespace />nombre').val(nombre);
		if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[0] %>') {
			jQuery('#<portlet:namespace />numero_afi').val(ospim);
		}
		if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[1] %>') {
			jQuery('#<portlet:namespace />numero_afi').val(uoma);
		}
		if (jQuery('#<portlet:namespace />entidad').val() == '<%= WebKeysGlobal.ENTIDADES_UOMA[2] %>') {
			jQuery('#<portlet:namespace />numero_afi').val(amtima);
		}
		if (document.getElementById("<portlet:namespace />baja_fecha")!= null){
			document.getElementById("<portlet:namespace />baja_fecha").value = bajaFecha;
		}
		
		jQuery('#<portlet:namespace />seleccionarAfiliadoDiv').show();
		Liferay.Popup.close(popupAfill);
	}

	function <portlet:namespace />resetValid() {
		if (jQuery("#<portlet:namespace />id_seccional").val() != "") {
			jQuery("#<portlet:namespace />secc_seleccionada").val("1")
		}
	}	
	function resetForm(){
		jQuery('#<portlet:namespace />cuil').val('');
		jQuery('#<portlet:namespace />inte').val('');
		jQuery('#<portlet:namespace />tipoDoc').val('');
		jQuery('#<portlet:namespace />nroDoc').val('');
		jQuery('#<portlet:namespace />id_seccional').val('');
		jQuery('#<portlet:namespace />seccional').val('');		
		jQuery('#<portlet:namespace />apellido').val('');
		jQuery('#<portlet:namespace />nombre').val('');																					
		jQuery('#<portlet:namespace />numero_afi').val('');
	}
	
	function imprimirCredencialesCES(){		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/genera_credencial&imprimir=CES';
		popupAfill = Liferay.Popup({title:"<liferay-ui:message key="print" />",modal:true,width:830});
		jQuery(popupAfill).load(url, function() {
			jQuery('#<portlet:namespace />buscando').hide();
			Liferay.Popup.close(popupAfill);		            															
		 }
		 );
	}

	<portlet:namespace />resetValid();
		
</script>
