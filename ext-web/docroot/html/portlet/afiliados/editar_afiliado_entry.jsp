<%@ include file="/html/portlet/afiliados/init.jsp"%>
<portlet:defineObjects />
<%@page import="ar.com.ospim.util.DateUtils"%>

<%
String tabsAMostrar = request.getParameter("tabs_a_mostrar");
if (tabsAMostrar == null || tabsAMostrar.trim().equals("")){
	tabsAMostrar = (String)request.getAttribute("tabs_a_mostrar");
}
String accion = (String)session.getAttribute(Constants.CMD);
String opciones=(String)session.getAttribute("opciones");
String preCarga=(String)session.getAttribute("pre_carga");
String idPreAfi=(String)session.getAttribute("id_pre_afiliado");

Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);	
int inte = 0;
if (afiliado!=null) {
	inte = afiliado.getInte();	
}
String tabsA = ParamUtil.getString(request, "tabs1", "");

if(tabsA.equals("")){
	tabsA = (String)request.getAttribute("tabs1");
}

if((tabsA == null) || (tabsA != null && tabsA.equals(""))) {
	tabsA = "informacion_general";
}
String tabCambiaHistorico = (String)request.getAttribute("mostrar_tab_cambio_historico");

StringBuilder tabsAValues = new StringBuilder("informacion_general");

boolean showPrevencionWS = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_PREVENCION_WS_ADMIN);

PortletURL portletURL = renderResponse.createRenderURL();
if(null!=afiliado){
	if(null!=opciones && opciones.equals("true") && null!= tabsA && tabsA.equals("informacion_general")){
		request.setAttribute("opciones",opciones);
	}else if(null!=preCarga && preCarga.equals("true") && null!= tabsA && tabsA.equals("informacion_general")){
		request.setAttribute("pre_carga",preCarga);
		request.setAttribute("id_pre_afiliado",idPreAfi);
	}else{
/* 		if (afiliado!=null  &&  !DateUtils.esMayor(new Date(), afiliado.getBaja_fecha() != null ? afiliado.getBaja_fecha() : new Date())  ){
			tabsAValues.append(",informacion_adicional");
		} */
		if (afiliado!=null  &&  (afiliado.getBaja_fecha()==null 
									|| DateUtils.esMayor(DateUtils.getMismoDia_23_59hs(afiliado.getBaja_fecha()) , DateUtils.getMismoDia_00_00hs(new Date())))){
			tabsAValues.append(",informacion_adicional");
		}
		if(tabCambiaHistorico !=null){ 
			tabsAValues.append(",cambios_cobertura");
		}
		tabsAValues.append(",historico_movimientos");
		tabsAValues.append(",historico_contactos");
		
		if(afiliado.getAfiPlan()!=null){
		   tabsAValues.append(",imagenes_afiliados");
		}
		if(showPrevencionWS){
			tabsAValues.append(",novedades_a_prevencion");	
		}
	}
	portletURL.setParameter("cuil_titular",afiliado.getCuil_titular());
	
	portletURL.setParameter("inte",afiliado.getInteAsString());
}
portletURL.setParameter("struts_action", "/afiliados/editar_afiliado_entry");
portletURL.setParameter("tabs1", tabsA);
if (tabsAMostrar != null && !tabsAMostrar.trim().equals("") && !tabsAMostrar.trim().equals("null")){
	portletURL.setParameter("tabs_a_mostrar", tabsAMostrar);
}
String ddReinc = (String)request.getAttribute(WebKeysAfiliados.DESDE_REINCORPORAR); 
if (ddReinc != null && !ddReinc.trim().equals("") && !ddReinc.trim().equals("null")){
	portletURL.setParameter(WebKeysAfiliados.DESDE_REINCORPORAR, ddReinc);
}

String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH);
if (tabsAMostrar != null && !tabsAMostrar.trim().equals("") && !tabsAMostrar.trim().equals("null")){
	tabsANames = tabsAMostrar;
	tabsAValues= new StringBuilder(tabsAMostrar);
}

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
    portlet_name = "afiliados";
}
%>

<script type="text/javascript">
	function <portlet:namespace />saveAfiliadoEntry() {	

		if (<portlet:namespace />validarCampos()) {
			url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/editar_afiliado_entry' /></portlet:actionURL>";
			<%if(null!=opciones && opciones.trim().equals("true")){%>
				url=url+'&opciones=true&cuit=<%=afiliado.getCuitSituLaboral(0)%>&razon_soc=<%=afiliado.getRazonSocSituLaboral(0).replace("'","\\'")%>';
			<%}%>
			<%if(null!=preCarga && preCarga.trim().equals("true")){%>
				url=url+'&pre_carga=true'+'&id_pre_afiliado='+<%=idPreAfi%>;
			<%}%>	
			submitForm(document.<portlet:namespace />fm, url);
		} 
	}

	function <portlet:namespace />irUnificarAportes(cuil, inte) {
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_afiliado_entry&cuil_titular='+cuil+'&inte='+inte+'&tabs1='+"informacion_adicional";
		submitForm(document.<portlet:namespace />fm, url);
	}

	function <portlet:namespace />cargarIntegrante() {
		var cuil_titular=jQuery('#<portlet:namespace />cuil_titular').val();		
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/cargar_integrante_entry&cuil_titular='+cuil_titular;
		<%if(null!=preCarga && preCarga.trim().equals("true")){%>
			url=url+'&pre_carga=true'+'&id_pre_afiliado='+<%=idPreAfi%>;
		<%}%>
		submitForm(document.<portlet:namespace />fm, url);
	}

	function <portlet:namespace />imprimirExcentoCoPago(cuil_titular, inte) {
	
		window.location.href ="/pdfservlet/?accion=credencialExentoCoPago&cuil="+cuil_titular+"&inte="+inte;


	}
	
	function editarIntegrante(cuil,inte) {
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_afiliado_entry&cuil_titular='+cuil+'&inte='+inte;	
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function <portlet:namespace />validarCampos() {
		var cuil_titular=jQuery('#<portlet:namespace />cuil_titular').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=<%=inte%>;
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
		var calle=jQuery('#<portlet:namespace />calle').val();
		var nroCalle=jQuery('#<portlet:namespace />numero').val();
		var tipoDoc=jQuery('#<portlet:namespace />documento_tipo').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var diaNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoDia').val());
		var mesNac= parseInt(jQuery('#<portlet:namespace />fechaNacimientoMes').val())+1;
		var anioNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoAnio').val());		
/*		var fecha_nac = new Date(anioNac,mesNac,diaNac); */
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());	
		var nro_correspondencia=jQuery('#<portlet:namespace />numero_correspondencia').val();	
/* 		var fecha_vig = new Date(anioVig,mesVig,diaVig); */		
		var codAreaTel=jQuery('#<portlet:namespace />cod_area_telefono').val();
		var nroTel=jQuery('#<portlet:namespace />telefono').val();
		var codAreaTelLabo=jQuery('#<portlet:namespace />cod_area_tel_laboral').val();
		var nroTelLabo=jQuery('#<portlet:namespace />tel_laboral').val();
		var codAreaCel=jQuery('#<portlet:namespace />cod_area_celular').val();
		var nroCel=jQuery('#<portlet:namespace />celular').val();
		var nacionalidad = jQuery('#<portlet:namespace/>nacionalidad').val();
		
		try {
			if(jQuery('#<portlet:namespace/>localidad').val() == "" || jQuery('#<portlet:namespace/>localidad').val()==0){
				alert("<liferay-ui:message key='localidad-obligatoria' />");
				jQuery("#<portlet:namespace />localidad").focus();
				return false;
			}
			
			if(jQuery('#<portlet:namespace/>provincia').val() == "" || jQuery('#<portlet:namespace/>provincia').val()==0){
				alert("<liferay-ui:message key='provincia-obligatoria' />");
				jQuery("#<portlet:namespace />provincia").focus();
				return false;
			}
			
			//Tel Fijo
			if (trim(codAreaTel).length > 0 || trim(nroTel).length > 0) {
			    // Si cargo algo, ambos son obligatorios
			    if (trim(codAreaTel).length == 0 || trim(nroTel).length == 0) {
			        alert("Debe completar cod. área y número de teléfono");
			        jQuery("#<portlet:namespace />telefono").focus();
			        return false;
			    }
			    if (trim(codAreaTel).startsWith('0')) {
			        alert("El código de área del teléfono no debe iniciar con cero");
			        jQuery("#<portlet:namespace />cod_area_telefono").focus();
			        return false;
			    }
			    if (trim(nroTel).startsWith('0')) {
			        alert("El número del teléfono no debe iniciar con cero");
			        jQuery("#<portlet:namespace />telefono").focus();
			        return false;
			    }
			    if (trim(codAreaTel).length + trim(nroTel).length != 10) {
			        alert("La longitud del código de área + teléfono debe ser de 10 caracteres");
			        jQuery("#<portlet:namespace />cod_area_telefono").focus();
			        return false;
			    }
			}

			//Celular
			if (trim(codAreaCel).length > 0 || trim(nroCel).length > 0) {
			    // Si cargo algo, ambos son obligatorios
			    if (trim(codAreaCel).length == 0 || trim(nroCel).length == 0) {
			        alert("Debe completar cod. área y número de celular");
			        jQuery("#<portlet:namespace />celular").focus();
			        return false;
			    }
			    if (trim(codAreaCel).startsWith('0')) {
			        alert("El código de área del celular no debe iniciar con cero");
			        jQuery("#<portlet:namespace />cod_area_celular").focus();
			        return false;
			    }
			    if (trim(nroCel).startsWith('0')) {
			        alert("El número del celular no debe iniciar con cero");
			        jQuery("#<portlet:namespace />celular").focus();
			        return false;
			    }
			    if (trim(codAreaCel).length + trim(nroCel).length != 10) {
			        alert("La longitud del código de área + celular debe ser de 10 caracteres");
			        jQuery("#<portlet:namespace />cod_area_celular").focus();
			        return false;
			    }
			}

			//Fin Telefono

			if(jQuery("#<portlet:namespace />id_seccional").val() == ""){
				alert("<liferay-ui:message key='seccional_invalida' />");
				jQuery("#<portlet:namespace />id_seccional").focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />id_seccional").val() != "" && jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
				alert("<liferay-ui:message key='seccional_invalida' />");
				jQuery("#<portlet:namespace />id_seccional").focus();
				return false;
			}
			if (trim(cuil_titular).length == 0) {
				alert("<liferay-ui:message key='cuil-titular-obligatorio' />");
				jQuery('#<portlet:namespace />cuil_titular').focus();
				return false;
			}
			if(!validarCuil(cuil_titular,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil_titular').focus();
				return false;
			}
			
			if(!esCUITValida(cuil_titular)){
				alert("CUIL inválido");
				jQuery('#<portlet:namespace />cuil_titular').focus();
				return false;
			}
				
			if (trim(apellido).length == 0) {
				alert("<liferay-ui:message key='apellido-obligatorio' />");
				jQuery('#<portlet:namespace />apellido').focus();
				return false;
			}
			if (trim(nombre).length == 0) {
				alert("<liferay-ui:message key='nombre-obligatorio' />");
				jQuery('#<portlet:namespace />nombre').focus();
				return false;
			}
			if (trim(calle).length == 0) {
				alert("<liferay-ui:message key='calle-obligatorio' />");
				jQuery('#<portlet:namespace />calle').focus();
				return false;
			}
			if (trim(nroCalle).length > 0 && !isPositiveInteger(trim(nroCalle))){
				alert("<liferay-ui:message key='calle-altura-invalido' />");
				jQuery('#<portlet:namespace />numero').focus();
				return false;
			}
			if (!isPositiveInteger(trim(cod_postal))){
				alert("<liferay-ui:message key='codigo-postal-invalido' />");
				jQuery('#<portlet:namespace />cod_postal').focus();
				return false;
			}	
			/* || trim(nro_correspondencia) == "0" */
			if ( trim(nro_correspondencia).length == 0){ 
				alert("Debe ingresar un numero de correspondencia");
				jQuery("#<portlet:namespace />numero_correspondencia").focus();
				return false;
			}
			if ((trim(inte) == "" || trim(inte) == "0") && (tipoDoc == "ET")) {
				alert("El titular no puede tener documento en tramite");
				jQuery('#<portlet:namespace />documento_tipo').focus();
				return false;
			}
			if (tipoDoc != "ET"){
				if (trim(nroDoc).length == 0) {
					alert("<liferay-ui:message key='nrodoc-obligatorio' />");
					jQuery('#<portlet:namespace />nroDoc').focus();
					return false;
				}
			}
			if (trim(inte) != "" && trim(inte) != "0" && tipoDoc != "ET") {
				if (trim(inte).length > 0 && inte > 0) {
					if (trim(cuil).length == 0) {
						alert("<liferay-ui:message key='cuil-obligatorio' />");
						jQuery('#<portlet:namespace />cuil').focus();
						return false;
					}
					if(!validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
						jQuery('#<portlet:namespace />cuil').focus();
						return false;
					}
				}
			}
			
			/* mini trucha validacion de fecha de nacimiento del titular  */
			if(inte == 0){ 
			
				if(anioVig < (anioNac + 16) ){
					alert("El titular debe tener al menos 16 años");
					jQuery('#<portlet:namespace />fechaNacimientoDia').focus();
					return false;
				}else if(anioVig == (anioNac + 16) && mesNac > mesVig  ){
					alert("El titular debe tener al menos 16 años");
					jQuery('#<portlet:namespace />fechaNacimientoDia').focus();
					return false;
				}else if(anioVig == (anioNac + 16) && mesNac == mesVig && diaNac > diaVig  ){
					alert("El titular debe tener al menos 16 años");
					jQuery('#<portlet:namespace />fechaNacimientoDia').focus();
					return false;
				}
				
				if(anioVig > (anioNac + 100) ){
					alert("El titular tiene más de 100 años");
					jQuery('#<portlet:namespace />fechaNacimientoDia').focus();
					return false;
				}
			}
			if (nacionalidad == 10 && ((nroDoc > 60000000 && nroDoc < 70000000) || (nroDoc > 90000000 )  )) {
				var opcion = confirm('Este afiliado tiene documento de extranjero, revise la nacionalidad por favor');
				if (opcion == true) {
				 return true;
				}else{   
     			 return false;
				}	 
			}
		} catch (err) {
			return false;
		}
		jQuery('#<portlet:namespace />parentesco').attr("disabled",false);			
		return true;
	}

</script>

<form action="" method="post" name="<portlet:namespace />fm" enctype="multipart/form-data">	
	<input type="hidden" name="tabs_a_mostrar" value="<%=tabsAMostrar%>"/>
	<input type="hidden" name="<%=WebKeysAfiliados.DESDE_REINCORPORAR%>" value="<%=ddReinc%>"/>
	<input name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%= accion %>" />
	<div id="<portlet:namespace />grupoFliar" style="position: fixed; left: 53%; width: 40%; height: 5%; align: right; display: none;">
		<c:choose>
			<c:when test='<%=afiliado!=null %>'>
				<liferay-util:include
					page="/html/portlet/afiliados/grupo_fliar_search_result.jsp" />
			</c:when>
		</c:choose>
	</div>
	<div id="<portlet:namespace />grupoFliarExistente" style="position: fixed; left: 53%; width: 40%; height: 5%; align: right; display: none;">
		<label><liferay-ui:message key="grupo-familiar-existente" />:</label>
	</div>
	
	<%	if (ddReinc == null || !ddReinc.equals(WebKeysAfiliados.DESDE_REINCORPORAR)){ %>
		<div id="<portlet:namespace />grupoFliarShow" style="position: fixed; left: 50%; width: 40%; height: 5%; align: right;">
			<c:choose>
				<c:when test='<%=afiliado!=null %>'>
					<table align="right">
						<tr>
							<c:choose>
								<c:when test='<%=afiliado!=null %>'>
									<td><label><%=afiliado.getApellido()%>,&nbsp;<%=afiliado.getNombre()%>&nbsp;(<%=afiliado.getParentesco()%>)&nbsp;&nbsp;&nbsp;</label>
									</td>
								</c:when>
							</c:choose>
							<td><a align="right" href="javascript:showGrupoFliar();"> <liferay-ui:message
								key='grupo-fliar' />&nbsp; </a></td>
							<td><img alt="<liferay-ui:message key='mostrar-grupo-fliar'/>"
								align="right"
								src="<%=themeDisplay.getPathThemeImages()%>/common/group.png"
								onClick="javascript:showGrupoFliar();" /></td>
						</tr>
					</table>
				</c:when>
			</c:choose>
		</div>
	<%} %>
	
	<liferay-ui-custom:tabs 
		names="<%= tabsANames %>" 
		tabsValues="<%= tabsAValues.toString() %>" 
		portletURL="<%= portletURL %>"  
		value="<%= tabsA%>"/> 
	<c:choose>
		<c:when test='<%= tabsA.equals("informacion_general")%>'>
			<liferay-util:include page="/html/portlet/afiliados/editar_afiliado.jsp" >			
			</liferay-util:include>	
		</c:when>
		<c:when test='<%= tabsA.equals("informacion_adicional")%>'>
			<liferay-util:include page="/html/portlet/afiliados/otros_datos.jsp" >			
			</liferay-util:include>
		</c:when>
		<c:when test='<%= tabsA.equals("cambios_cobertura")%>'>
			<liferay-util:include page="/html/portlet/afiliados/modifica_historico_plan_tercerizadora.jsp" >			
			</liferay-util:include>
		</c:when>
		<c:when test='<%= tabsA.equals("historico_movimientos")%>'>
			<liferay-util:include page="/html/portlet/afiliados/historico_movimientos.jsp" >			
			</liferay-util:include>	
		</c:when>
		<c:when test='<%= tabsA.equals("historico_contactos")%>'>
			<liferay-util:include page="/html/portlet/crm/historico_contactos.jsp" >			
			</liferay-util:include>	
		</c:when>
		<c:when test='<%= tabsA.equals("imagenes_afiliados")%>'>
	        <liferay-util:include page="/html/portlet/afiliados/afiliado_imagenes.jsp"/>
	    </c:when>
	    <c:when test='<%= tabsA.equals("novedades_a_prevencion")%>'>
	        <liferay-util:include page="/html/portlet/afiliados/historico_prevencion_ws.jsp"/>
	    </c:when>				
	</c:choose>
</form>

<c:if test="<%= windowState.equals(LiferayWindowState.MAXIMIZED) %>">
	<script type="text/javascript">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />name);
	</script>
</c:if>

<script type="text/javascript">	
	jQuery("#<portlet:namespace />grupoFliar").hide();
	function hideGrupoFliar(){		
		jQuery('#<portlet:namespace/>grupoFliar').hide();
		jQuery("#<portlet:namespace />grupoFliarShow").show();
		
	}
	
	function showGrupoFliar(){		
		jQuery("#<portlet:namespace />grupoFliarShow").hide();
		jQuery('#<portlet:namespace/>grupoFliar').show();		
		
	}

	function <portlet:namespace />buscarAfilExistenteCuilTitular(event) {
		var cuil_titular=jQuery("#<portlet:namespace />cuil_titular").val();
        if(!cuil_titular.length > 0){        	
        	return;
        }
        var tipoValidacion = 'cuil_titular';
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_XCuil&cuil_titular='+cuil_titular+
		  '&tipoValidacion='+tipoValidacion;
		jQuery("#<portlet:namespace />nroDocAfilExistente").load(url);

	}

	function <portlet:namespace />buscarAfilExistenteCuil(event) {
		var cuil=jQuery("#<portlet:namespace />cuil").val();
        if(!cuil.length > 0){        	
        	return;
        }
        var tipoValidacion = 'cuil';
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_XCuil&cuil='+cuil+
		  '&tipoValidacion='+tipoValidacion;		
		jQuery("#<portlet:namespace />nroDocAfilExistente").load(url);

	}

	function <portlet:namespace />buscarAfilExistente(e){
		var cuil_titular=jQuery("#<portlet:namespace />cuil_titular").val();
		var nroDoc=trim(jQuery("#<portlet:namespace />nroDoc").val());
		var documento_tipo=jQuery("#<portlet:namespace />documento_tipo").val();
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val());
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());
		
		var cuil_original = <%=Validator.isNotNull(afiliado) ? "'" + afiliado.getCuil_titular() + "'" : "''" %>;	
		var inte_original = <%=Validator.isNotNull(afiliado) ? afiliado.getInte() : "''" %>;					    	    
        if(!nroDoc.length > 0){        	
        	return;
        }
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_existente&nroDoc='+nroDoc+
		  '&documento_tipo='+documento_tipo+'&cuil_titular='+cuil_titular+'&cuil_original='+cuil_original+'&inte_original='+inte_original+'&diaVig='+diaVig+'&mesVig='+mesVig+'&anioVig='+anioVig;		
		jQuery("#<portlet:namespace />nroDocAfilExistente").load(url);
	}

	var popupda;
	var popupda2;
	var popupdac;
	var popupdd;
	function <portlet:namespace />documentacionAdjunta(cuil, inte) {
		popupda = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta" />",modal:true,width:900});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/documentacion_adjunta&cuil_titular='+cuil+'&inte='+inte;
		jQuery(popupda).load(url);
	}
			
	function <portlet:namespace />certificadoAfiliacion(cuil, inte) {
		popupdac = Liferay.Popup({title:"<liferay-ui:message key="certificado-afiliacion" />",modal:true,width:900});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/odt_rtf&cuil='+cuil+'&inte='+inte;
		jQuery(popupdac).load(url);		
	}

	function <portlet:namespace />odtRtf(cuil, inte, tipo) {
		window.location.href ="/odtservlet/?accion=certificadoAfiliacion&cuil="+cuil+"&inte="+inte+"&tipo="+tipo;
	}

	function <portlet:namespace />detalleDiscapacidad(cuil, inte, disca) {
		popupdd = Liferay.Popup({title:"<liferay-ui:message key="det-discap" />",modal:true,width:900});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/detalle_discapacidad&cuil_titular='+cuil+'&inte='+inte+'&path=/afiliados/grabar_detalle_discapacidad';
		jQuery(popupdd).load(url);
	}

	function <portlet:namespace />reloadPopupDetalle() {
		Liferay.Popup.close(popupdd);
		<portlet:namespace />detalleDiscapacidad();
	}
			
	<c:if test="<%= afiliado != null %>">
		function <portlet:namespace />verImagenes() {
			//popupda = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta" />",modal:true,width:900});
			
			<%String todoAfiliado=afiliado.getCuil_titular()+"*";%>
			var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString()%>">'+
			    			'<liferay-portlet:param name="struts_action" value="/afiliados/buscar_documentacion"/>'+
			    			'<liferay-portlet:param name="keywords" value="<%=todoAfiliado%>"/>'+
		             '</liferay-portlet:renderURL>';      
		    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 	             
			//jQuery(popupda).load(url);
		}
	
		function <portlet:namespace />verImagenDirecta() {
			//popupda = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta" />",modal:true,width:900});		
			var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
			    			'<liferay-portlet:param name="struts_action" value="/afiliados/documentacion_adjunta_recuperar"/>'+
			    			'<liferay-portlet:param name="name" value="<%=afiliado.getTitle()%>"/>'+
			    			'<liferay-portlet:param name="folderId" value="<%=String.valueOf(afiliado.getFolderid())%>"/>'+
		             '</liferay-portlet:actionURL>';      
		    window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 	             
			//jQuery(popupda).load(url);
		}
	</c:if>
	
	function <portlet:namespace />buscarAfilTitBajaExistente(e){
		var cuil_titular=jQuery("#<portlet:namespace />cuil_titular").val();
		var inte=0;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_titular_existente&cuil_titular='+cuil_titular+
		  '&inte='+inte;
		jQuery("#<portlet:namespace />nroDocAfilExistente").load(url);
	}

	function moveFocus() {
		jQuery("#<portlet:namespace />vigenteFechaMes").focus();
	}

	function moveFocusNaci() {
		jQuery("#<portlet:namespace />fechaNacimientoMes").focus();
	}	
</script>

<script type="text/javascript">
<c:if test="<%= afiliado != null %>">
	function <portlet:namespace />uploadImagenAfiliado() {	
		var descripcionImagen =jQuery('#<portlet:namespace />descripcionFile').val();
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_afiliado';
		document.<portlet:namespace />fm.method = 'post';
		url = url+'&imagen'+'='+'<%=Constants.ADD%>'+'&nrsolicitud=<%=afiliado.getCuil() %>';
		if(!descripcionImagen.length > 0){        	
			alert("Debe ingresar una descripción");
			return;
	    }
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function verImagenAfiliado(folderId,fileName){
	   var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
	   '<liferay-portlet:param name="struts_action" value="/afiliados/documentacion_adjunta_recuperar"/>'+
	   '<liferay-portlet:param name="name" value="__Name"/>'+
	   '<liferay-portlet:param name="folderId" value="__FolderId"/>'+
	   '</liferay-portlet:actionURL>';      
	   url = url.replace("__Name",fileName).replace("__FolderId",folderId);
	   window.open(url,'mywindow','width=800,height=800,toolbar=no,resizable=yes') 
	}
	
	function deleteImagenAfiliado(folderId,fileName) {	
		var confirmar=false;
		confirmar = confirm ('Está seguro de eliminar este documento');
		if(confirmar){	
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_imagenes_afiliado';						
			document.<portlet:namespace />fm.method = 'post';
			url = url+'&imagen'+'='+'<%=Constants.DELETE %>';
			url += "&folderid="+folderId;
			url += "&filename="+fileName;
			submitForm(document.<portlet:namespace />fm, url);
		}else{
			return false;
		}	
	}
</c:if>


</script>