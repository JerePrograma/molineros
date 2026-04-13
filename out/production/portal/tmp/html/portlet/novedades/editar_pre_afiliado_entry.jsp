<%@ include file="/html/portlet/novedades/init.jsp"%>
<portlet:defineObjects />
<%
String tabsAMostrar = request.getParameter("tabs_a_mostrar");
if (tabsAMostrar == null || tabsAMostrar.trim().equals("")){
	tabsAMostrar = (String)request.getAttribute("tabs_a_mostrar");
}
String accion = (String)request.getAttribute(Constants.CMD); 

PreAfiliado afiliado = (PreAfiliado)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);	
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

StringBuilder tabsAValues = new StringBuilder("informacion_general");
tabsAValues.append(",imagenes_afiliados");



PortletURL portletURL = renderResponse.createRenderURL();
/* if(null!=afiliado){
	if(null!=opciones && opciones.equals("true") && null!= tabsA && tabsA.equals("informacion_general")){
		request.setAttribute("opciones",opciones);
	}else{
		tabsAValues.append(",informacion_adicional");
		tabsAValues.append(",historico_movimientos");
	}
	portletURL.setParameter("cuil_titular",afiliado.getCuil_titular());
	
	portletURL.setParameter("inte",afiliado.getInteAsString());
} */
portletURL.setParameter("struts_action", "/afiliados/editar_pre_afiliado");
portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("cmd", accion);

if (tabsAMostrar != null && !tabsAMostrar.trim().equals("") && !tabsAMostrar.trim().equals("null")){
	portletURL.setParameter("tabs_a_mostrar", tabsAMostrar);
}

String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH);
if (tabsAMostrar != null && !tabsAMostrar.trim().equals("") && !tabsAMostrar.trim().equals("null") ){
	tabsANames = tabsAMostrar;
	tabsAValues= new StringBuilder(tabsAMostrar);
}

String portlet_name = "afiliados";
%>

<script type="text/javascript">
	function <portlet:namespace />savePreAfiliadoEntry() {		
		if (<portlet:namespace />validarCampos()) {
			url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/editar_pre_afiliado' /></portlet:actionURL>";		
			var params = '&tabs1=informacion_general'+'&<%= Constants.CMD %>='+'<%= accion %>'; 
			url = url + params;
			submitForm(document.<portlet:namespace />fm, url);
		} 
	}

	function <portlet:namespace />savePreAfiliadoIntegrante() {	
		if (<portlet:namespace />validarCamposIntegrante()) {
			url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/editar_pre_afiliado' /></portlet:actionURL>";		
			var params = '&tabs1=informacion_general'+'&<%= Constants.CMD %>='+'<%= accion %>'; 
			url = url + params;
			submitForm(document.<portlet:namespace />fm, url);
		} 
	}
	
	<%-- function <portlet:namespace />cargarIntegrante() {
		var cuil_titular=jQuery('#<portlet:namespace />cuil_titular').val();		
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/cargar_integrante_entry&cuil_titular='+cuil_titular;		
		submitForm(document.<portlet:namespace />fm, url);
	} --%>

	<%-- function editarIntegrante(cuil,inte) {
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_afiliado_entry&cuil_titular='+cuil+'&inte='+inte;	
		submitForm(document.<portlet:namespace />fm, url);		
	} --%>
	
	function <portlet:namespace />validarCampos() {
		var cuil_titular=jQuery('#<portlet:namespace />cuil_titular').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=<%=inte%>;
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
		var calle=jQuery('#<portlet:namespace />calle').val();
		var tipoDoc=jQuery('#<portlet:namespace />documento_tipo').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var diaNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoDia').val());
		var mesNac= parseInt(jQuery('#<portlet:namespace />fechaNacimientoMes').val())+1;
		var anioNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoAnio').val());		
/*		var fecha_nac = new Date(anioNac,mesNac,diaNac); */
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());		
/* 		var fecha_vig = new Date(anioVig,mesVig,diaVig); */		

		try {
			if(jQuery('#<portlet:namespace/>localidad').val() == "" || jQuery('#<portlet:namespace/>localidad').val()==0){
				alert("<liferay-ui:message key='localidad-obligatoria' />");
				jQuery("#<portlet:namespace />localidad").focus();
				return false;
			}
			
			if(jQuery('#<portlet:namespace/>provincia').val() == ""|| jQuery('#<portlet:namespace/>provincia').val()==0){
				alert("<liferay-ui:message key='provincia-obligatoria' />");
				jQuery("#<portlet:namespace />provincia").focus();
				return false;
			}
			
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
/* 			function <portlet:namespace />odtRtf(cuil, inte, tipo) {
				window.location.href ="/odtservlet/?accion=certificadoAfiliacion&cuil="+cuil+"&inte="+inte+"&tipo="+tipo;
			} */
			if (trim(cuil_titular).length == 0) {
				alert("<liferay-ui:message key='cuil-titular-obligatorio' />");
				jQuery('#<portlet:namespace />cuil_titular').focus();
				return false;
			}
			if(!validarCuil(cuil_titular,"<liferay-ui:message key='valida-cuil'/>")){
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
			if (!isPositiveInteger(trim(cod_postal))){
				alert("<liferay-ui:message key='codigo-postal-invalido' />");
				jQuery('#<portlet:namespace />cod_postal').focus();
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
			}
			
			var cuit_empleador=jQuery("#<portlet:namespace />cuit_empleador").val();
			var categoria=jQuery("#<portlet:namespace/>categoria").val();
			var situRevista=jQuery("#<portlet:namespace/>situRevista").val();
			
			// valida monotributista
			if(cuit_empleador==cuil_titular && parseInt(categoria) == 11 ){
				alert("El monotributista no puede tener categoria: RELACION DE DEPENDENCIA");
				return false;
			}
			if(cuit_empleador==cuil_titular && parseInt(situRevista) == 6 ){
				alert("El monotributista no puede tener situacion revista: RECIBE HABERES REGULARMENTE");
				return false;
			}

			if((cuit_empleador != cuil_titular) && (parseInt(categoria) == 8 || parseInt(categoria) == 10 )){
				alert("El cuit monotributista debe ser igual al cuil titular");
				return false;
			}

			var planNuevo=parseInt(jQuery("#<portlet:namespace/>nuevoPlan").val());
			var tercerizadoraNuevo=jQuery("#<portlet:namespace/>tercerizadora").val();
			var mot_baja=jQuery("#<portlet:namespace/>motivoBajaPlan").val();
			var bajaDia = jQuery('#<portlet:namespace />fechaVigenHastaDia').val();
			var bajaMes = parseInt(jQuery('#<portlet:namespace />fechaVigenHastaMes').val())+1;  
			var bajaAnio = jQuery('#<portlet:namespace />fechaVigenHastaAnio').val();
			
			if(planNuevo > 0 && tercerizadoraNuevo==""){
				alert("Debe seleccionar una tercerizadora para el plan");
				return false;
			}
			/* if(planNuevo == 0 && tercerizadoraNuevo != ""){
				alert("Debe seleccionar una plan para la tercerizadora");
				return false;
			} */
			if(trim(mot_baja).length > 0 && (trim(bajaDia).length == 0 || trim(bajaMes).length == 0 || trim(bajaAnio).length == 0) ){
				alert("Debe completar la fecha de baja para el plan");
				return false;
			}
			if((trim(mot_baja).length == 0 || mot_baja == "") && (bajaDia != "") && (bajaMes != "" ) && (bajaAnio != "") ){
				alert("Debe seleccionar un motivo de baja para el plan");
				return false;
			}
			var validaCuil = <portlet:namespace />validarExisteCuil("cuil_titular");
		} catch (err) {
			return false;
		}
		jQuery('#<portlet:namespace />parentesco').attr("disabled",false);			
		return true;
	}

	function <portlet:namespace />validarCamposIntegrante() {
		var cuil_titular=jQuery('#<portlet:namespace />cuil_titular').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=<%=inte%>;
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var tipoDoc=jQuery('#<portlet:namespace />documento_tipo').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var diaNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoDia').val());
		var mesNac= parseInt(jQuery('#<portlet:namespace />fechaNacimientoMes').val())+1;
		var anioNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoAnio').val());		
/*		var fecha_nac = new Date(anioNac,mesNac,diaNac); */
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());		
/* 		var fecha_vig = new Date(anioVig,mesVig,diaVig); */		

		try {
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
			var validaCuil = <portlet:namespace />validarExisteCuil("cuil");
		} catch (err) {
			return false;
		}
		jQuery('#<portlet:namespace />parentesco').attr("disabled",false);			
		return true;
	}

</script>

<%-- <form action="" method="post" name="<portlet:namespace />fm">	
	<input type="hidden" name="tabs_a_mostrar" value="<%=tabsAMostrar%>"/>
	<input name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%= accion %>" />
	 --%>
	<liferay-ui:success key="insertOk"  message="<%=(String)request.getAttribute(\"msgOk\")  %>"  />
	<liferay-ui:success key="updateOk"  message="<%=(String)request.getAttribute(\"msgOk\")  %>"  />

	<%-- <div id="<portlet:namespace />grupoFliar" style="position: fixed; left: 53%; width: 40%; height: 5%; align: right; display: none;">
		<c:choose>
			<c:when test='<%=afiliado!=null %>'>
				<liferay-util:include
					page="/html/portlet/afiliados/grupo_fliar_search_result.jsp" />
			</c:when>
		</c:choose>
	</div> --%>
	<%-- <div id="<portlet:namespace />grupoFliarExistente" style="position: fixed; left: 53%; width: 40%; height: 5%; align: right; display: none;">
		<label><liferay-ui:message key="grupo-familiar-existente" />:</label>
	</div> --%>
	
	<%-- <div id="<portlet:namespace />grupoFliarShow" style="position: fixed; left: 50%; width: 40%; height: 5%; align: right;">
		<c:choose>
			<c:when test='<%=afiliado!=null %>'>
				<table align="right">
					<tr>
						<c:choose>
							<c:when test='<%=afiliado!=null %>'>
								<td><label><%=afiliado.getApellido()%>,&nbsp;<%=afiliado.getNombre()%>&nbsp;(<%=afiliado.getId_parentesco_sss()%>)&nbsp;&nbsp;&nbsp;</label>
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
	</div> --%>

<%
String cuil_titular = (String) request.getAttribute("esIntegranteDelCuilTitular");
boolean esTitular = true;
if(cuil_titular != null){
	esTitular = false;
}
%>
	<liferay-ui-custom:tabs 
		names="<%= tabsANames %>" 
		tabsValues="<%= tabsAValues.toString() %>" 
		portletURL="<%= portletURL %>"  
		value="<%= tabsA%>"/> 
	<c:choose>
		<c:when test='<%= tabsA.equals("informacion_general")%>'>
			<%if(esTitular){%>
			<liferay-util:include page="/html/portlet/novedades/editar_pre_afiliado.jsp" >
				<liferay-util:param name="es_titular_p" value="<%=String.valueOf(esTitular)%>"/>			
			</liferay-util:include>	
			<%}else{ %>
			<liferay-util:include page="/html/portlet/novedades/editar_pre_afiliado_integrante.jsp" >
				<liferay-util:param name="cuil_titular_p" value="<%=cuil_titular%>"/>
			</liferay-util:include>
			<%} %>			
		</c:when>
		
		<c:when test='<%= tabsA.equals("imagenes_afiliados")%>'>
	        <liferay-util:include page="/html/portlet/novedades/pre_afiliado_imagenes.jsp">
	        	<!-- <liferay-util:param name="portlet_name" value="novedades"/> -->  
	        </liferay-util:include>
	    </c:when>	
		
		
		<%-- <c:when test='<%= tabsA.equals("informacion_adicional")%>'>
			<liferay-util:include page="/html/portlet/afiliados/otros_datos.jsp" >			
			</liferay-util:include>
		</c:when>
		<c:when test='<%= tabsA.equals("historico_movimientos")%>'>
			<liferay-util:include page="/html/portlet/afiliados/historico_movimientos.jsp" >			
			</liferay-util:include>	
		</c:when>	 --%>
	</c:choose>
<!-- </form> -->

<%-- <c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
	<script type="text/javascript">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />name);
	</script>
</c:if> --%>

<script type="text/javascript">	

	jQuery('#<portlet:namespace />sexo').change(function(){validarPrefijoCuilPorSexo();});	
	jQuery('#<portlet:namespace />cuil').change(function(){validarPrefijoCuilPorSexo();});
		
	jQuery(window).load(function () {
		<%if(afiliado != null) {%>
			validarPrefijoCuilPorSexo();
		<%}%>
	});
		
		
	function validarPrefijoCuilPorSexo(){
		
		var cuil = jQuery('#<portlet:namespace/>cuil').val();
	    var sexo = jQuery('#<portlet:namespace/>sexo').val();
	     
		 if (cuil.length == 11){
			 var prefijo_cuil = cuil.substring(0, 2);
			 
			 //valido prefijo masculino
			 if (sexo == 'm' && prefijo_cuil != '20'){
				 alert('El cuil ingresado puede ser que no corresponda con el sexo masculino');
			 }else if(sexo == 'f' && prefijo_cuil != '27'){
				 alert('El cuil ingresado puede ser que no corresponda con el sexo femenino');
			 }
		 }
	}
	



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
		var cuil_original = <%=Validator.isNotNull(afiliado) ? "'" + afiliado.getCuil_titular() + "'" : "''" %>;	
		var inte_original = <%=Validator.isNotNull(afiliado) ? afiliado.getInte() : "''" %>;					    	    
        if(!nroDoc.length > 0){        	
        	return;
        }
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_existente&nroDoc='+nroDoc+
		  '&documento_tipo='+documento_tipo+'&cuil_titular='+cuil_titular+'&cuil_original='+cuil_original+'&inte_original='+inte_original;		
		jQuery("#<portlet:namespace />nroDocAfilExistente").load(url);
	}

	var popupda;
	var popupda2;
	var popupdac;
	var popupdd;
	<%-- function <portlet:namespace />documentacionAdjunta(cuil, inte) {
		popupda = Liferay.Popup({title:"<liferay-ui:message key="documentacion-adjunta" />",modal:true,width:900});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/documentacion_adjunta&cuil_titular='+cuil+'&inte='+inte;
		jQuery(popupda).load(url);
	}
			
	function <portlet:namespace />certificadoAfiliacion(cuil, inte) {
		popupdac = Liferay.Popup({title:"<liferay-ui:message key="certificado-afiliacion" />",modal:true,width:900});
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/odt_rtf&cuil='+cuil+'&inte='+inte;
		jQuery(popupdac).load(url);		
	}

	function <portlet:namespace />detalleDiscapacidad(cuil, inte, disca) {
		popupdd = Liferay.Popup({title:"Detalle Discapacidad",modal:true,width:900});
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/detalle_discapacidad&cuil_titular='+cuil+'&inte='+inte+'&path=/afiliados/grabar_detalle_discapacidad';
		jQuery(popupdd).load(url);
	} --%>

	function <portlet:namespace />reloadPopupDetalle() {
		Liferay.Popup.close(popupdd);
		<portlet:namespace />detalleDiscapacidad();
	}
			
	
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

	function <portlet:namespace />validarExisteCuil(cuil){
		var result = 'true';
		if(cuil=="cuil"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil').val();
		}else if(cuil=="cuil_titular"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil_titular').val();
		}		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_existe_pre_afiliado&cuil='+cuil_final;
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.validado=="1"){
					result = 'false';
					alert("<liferay-ui:message key='cuil-invalido'/>");
				}else if(obj.validado=="2"){
					result = 'false';
					alert("<liferay-ui:message key='cuil-titular-existente'/>");
				}else if(obj.validado=="3"){
					result = 'false';
					alert("<liferay-ui:message key='cuil-preafi-existente'/>");
				} 	
			}				                                                                                                                                                                                                                                                            
			
		});
		
		return result;
	}
</script>