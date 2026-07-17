<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%
String accion = (String)session.getAttribute(Constants.CMD);
Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
String tabsA = ParamUtil.getString(request, "tabs1", "informacion_general");
StringBuilder tabsAValues = new StringBuilder("informacion_general");
PortletURL portletURL = renderResponse.createRenderURL();

if(null!=afiliado){
	portletURL.setParameter("cuil_titular",afiliado.getCuil_titular());
	portletURL.setParameter("inte",afiliado.getInteAsString());
}

portletURL.setParameter("struts_action", "/afiliados/cargar_integrante_entry");
portletURL.setParameter("tabs1", tabsA);

String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH); 
%>
<script type="text/javascript">
	<% String exito = (String)request.getAttribute("Exito");%>
	<% if (exito != null && !exito.equals("") && request.getAttribute("FaltaTercerizadora") == null && request.getAttribute("FaltaSituLaboral") == null && request.getAttribute("FaltaAporte") == null){%>
		<%String[] tipo_bono_array = exito.split("\\|");		
		String cadenaOspim = tipo_bono_array[0];
		String cadenaUoma = tipo_bono_array[1];
		String cadenaAmtima = tipo_bono_array[2];%>
		alert("Los cambios se guardaron exitosamente! \r\n" <%if (!cadenaOspim.equals("0")){ %> + "id_ospim= " + <%=cadenaOspim%> <%}%> + " \r\n" <%if (!cadenaUoma.equals("0")){ %> + "id_uoma= " + <%=cadenaUoma%> <%}%> + " \r\n" <%if (!cadenaAmtima.equals("0")){ %> + "id_amtima= " + <%=cadenaAmtima%> <%}%>);		
	<%}%>
	function <portlet:namespace />saveAfiliadoEntry() {			
		if (<portlet:namespace />validarCampos()!=false) {				
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.ADD %>";
			url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/cargar_integrante_entry' /></portlet:actionURL>";		
			submitForm(document.<portlet:namespace />fm, url);
		}
	}

	function <portlet:namespace />cargarIntegrante() {
		url = "<portlet:renderURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/cargar_integrante_entry' /></portlet:renderURL>";		
		submitForm(document.<portlet:namespace />fm, url);
	}
	


	
	function <portlet:namespace />validarCampos() {	
		var cuil_titular=jQuery('#<portlet:namespace />cuil_titular').val();		
		var inte=jQuery('#<portlet:namespace />inte').val();	
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
		var calle=jQuery('#<portlet:namespace />calle').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var tipoDoc=jQuery('#<portlet:namespace />documento_tipo').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var cuil_validado=jQuery('#<portlet:namespace />cuil_validado').val();
		var nro_correspondencia=jQuery('#<portlet:namespace />numero_correspondencia').val();	
		
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());	
		
		var diaNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoDia').val());
		var mesNac= parseInt(jQuery('#<portlet:namespace />fechaNacimientoMes').val())+1;
		var anioNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoAnio').val());
		
		var idParent=jQuery('#<portlet:namespace />parentesco').val();
		var nacionalidad = jQuery('#<portlet:namespace/>nacionalidad').val();
		
		try {
			if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
				alert("<liferay-ui:message key='seccional_invalida' />");
				jQuery("#<portlet:namespace />secc_seleccionada").focus();
				return false;
			}
			if (trim(cuil_titular).length == 0) {
				alert("<liferay-ui:message key='cuil-titular-obligatorio' />");
				jQuery('#<portlet:namespace />cuil_titular').focus();				
				return false;
			}
			/*este viene x defecto del titular y es solo lectura*/
			/* if(!validarCuil(cuil_titular,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuil_titular').focus();
				return false;
			} */

			if(cuil_validado == ''){
				jQuery('#<portlet:namespace />cuil').focus();
				alert("<liferay-ui:message key='cuil-invalido'/>");
				return false;
			}
			
			if (trim(nombre).length == 0) {
				alert("<liferay-ui:message key='nombre-obligatorio' />");
				jQuery('#<portlet:namespace />nombre').focus();
				return false;
			}
			if (trim(apellido).length == 0) {
				alert("<liferay-ui:message key='apellido-obligatorio' />");
				jQuery('#<portlet:namespace />apellido').focus();
				return false;
			}
			/* || trim(nro_correspondencia) == "0" */
			if ( trim(nro_correspondencia).length == 0 ){
				alert("Debe ingresar un numero de correspondencia");
				jQuery("#<portlet:namespace />numero_correspondencia").focus();
				return false;
			}
			if (tipoDoc != "ET"){
				if (trim(nroDoc).length == 0) {
					alert("<liferay-ui:message key='nrodoc-obligatorio' />");
					jQuery('#<portlet:namespace />nroDoc').focus();
					return false;
				}
			}
			
			if (tipoDoc != "ET"){
				if (trim(cuil).length == 0) {
					alert("<liferay-ui:message key='cuil-obligatorio' />");
					jQuery('#<portlet:namespace />cuil').focus();
					return false;
				}				
			}
			if(cuil=="ET"){
				cuil_validado="true";
			}

			if(cuil!="ET" && !validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>") ){
					alert("<liferay-ui:message key='cuil-invalido'/>");
					jQuery('#<portlet:namespace />cuil').focus();
					return false;
			} 
		
			if (!isPositiveInteger(trim(cod_postal))){
				alert("<liferay-ui:message key='codigo-postal-invalido' />");
				jQuery('#<portlet:namespace />cod_postal').focus();
				return false;
			}			
			if (trim(calle).length == 0) {
				alert("<liferay-ui:message key='calle-obligatorio' />");
				jQuery('#<portlet:namespace />calle').focus();
				return false;			
			}	
			
			if (nacionalidad == 10 && ((nroDoc > 60000000 && nroDoc < 70000000) || (nroDoc > 90000000 )  ) ){
				
				  var opcion = confirm('Este afiliado tiene documento de extranjero, revise la nacionalidad por favor');
				  if (opcion == true) {
				     return true;
				  }else{   
       				 return false;
				  }	 
			}

			/* mini trucha validacion de fecha de nacimiento del concubino/conyugue  */
			if(idParent == 1 || idParent == 2){ 
			
				if(anioVig < (anioNac + 16) ){
					alert("Con parentesco cónguye o concubino debe tener al menos 16 años");
					jQuery('#<portlet:namespace />fechaNacimientoDia').focus();
					return false;
				}else if(anioVig == (anioNac + 16) && mesNac > mesVig  ){
					alert("Con parentesco cónguye o concubino debe tener al menos 16 años");
					jQuery('#<portlet:namespace />fechaNacimientoDia').focus();
					return false;
				}else if(anioVig == (anioNac + 16) && mesNac == mesVig && diaNac > diaVig  ){
					alert("Con parentesco cónguye o concubino debe tener al menos 16 años");
					jQuery('#<portlet:namespace />fechaNacimientoDia').focus();
					return false;
				}
				
				if(anioVig > (anioNac + 100) ){
					alert("El integrante tiene más de 100 años");
					jQuery('#<portlet:namespace />fechaNacimientoDia').focus();
					return false;
				}	
			}
			
			if (!<portlet:namespace />validarCuilcvAsync('cuil')){
				
			   	return false;
			}
			
			if (!<portlet:namespace />validarFechaVigenAteriorTitularAsync()){
				
			   	return false;
			}
			
			var result = <portlet:namespace />validarOtrosCampos();
			
			if (result == false){
				return false;
			}
		} catch (err) {
			return false;
		}
		jQuery('#<portlet:namespace />parentesco').attr("disabled",false);
		 
		return true; 
	}

</script>

<form action="" method="post" name="<portlet:namespace />fm">
<input name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%= accion %>" />
<div id="<portlet:namespace />grupoFliar" style="position:fixed; left:53%; width:40%; height:5%; align:right; display:none;">
	<c:choose>	
		<c:when test='<%=afiliado!=null %>'>		
			<liferay-util:include page="/html/portlet/afiliados/grupo_fliar_search_result.jsp"/>			
		</c:when>
	</c:choose>
</div>

<div id="<portlet:namespace />grupoFliarShow" style="position:fixed; left:50%; width:40%; height:5%; align:right;">
	<c:choose>	
		<c:when test='<%=afiliado!=null %>'>
			<table align="right">	
				<tr>
					<td>	
						<a align="right" href="javascript:showGrupoFliar();">
					 		<liferay-ui:message key='grupo-fliar'/>&nbsp; 
						</a>						
					</td>
					<td>
						<img alt="<liferay-ui:message key='mostrar-grupo-fliar'/>" align="right" src="<%=themeDisplay.getPathThemeImages()%>/common/group.png" onClick="javascript:showGrupoFliar();"/>
					</td>
				</tr>
			</table>
		</c:when>
	</c:choose>
</div>

<liferay-ui:tabs		
	names="<%= tabsANames %>"
	tabsValues="<%= tabsAValues.toString() %>"		
	portletURL="<%= portletURL %>"
/>

<c:choose>
	<c:when test='<%= tabsA.equals("informacion_general") %>'>
		<liferay-util:include page="/html/portlet/afiliados/editar_afiliado_integrante.jsp"/>
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

	function editarIntegrante(cuil,inte){		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_afiliado_entry&cuil_titular='+cuil+
		'&inte='+inte;		
		window.location=url;			
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

	function <portlet:namespace />buscarAfilExistente(e) {
		var cuil_titular=jQuery("#<portlet:namespace />cuil_titular").val();
		var nroDoc=trim(jQuery("#<portlet:namespace />nroDoc").val());
		var documento_tipo=jQuery("#<portlet:namespace />documento_tipo").val();
		var cuil_original = <%=Validator.isNotNull(afiliado) ? "'" + afiliado.getCuil_titular() + "'" : "''" %>;	
		var inte_original = <%=Validator.isNotNull(afiliado) ? afiliado.getInte() : "''" %>;					    	    

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_existente&nroDoc='+nroDoc+
		  '&documento_tipo='+documento_tipo+'&cuil_titular='+cuil_titular+'&cuil_original='+cuil_original+'&inte_original='+inte_original;		
		jQuery("#<portlet:namespace />nroDocAfilExistente").load(url);
	}

	function <portlet:namespace />buscarAfilTitBajaExistente(e) {
		var cuil_titular=jQuery("#<portlet:namespace />cuil_titular").val();
		var inte=0;
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_afiliado_titular_existente&cuil_titular='+cuil_titular+
		  '&inte='+inte;
		jQuery("#<portlet:namespace />nroDocAfilExistente").load(url);		
	}

	function moveFocus()
	{
		jQuery("#<portlet:namespace />vigenteFechaMes").focus();
	}

	function <portlet:namespace />irUnificarAportes(nroDoc, documento_tipo) {		
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/editar_afiliado_entry&nroDoc='+nroDoc+'&documento_tipo='+documento_tipo+'&tabs1='+"informacion_adicional"+'&unif_aportes='+"true";		
		submitForm(document.<portlet:namespace />fm, url);
	}	
	
	
	function <portlet:namespace />validarCuilcvAsync(cuil){

		var respuesta=true;
		if(cuil=="cuil"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil').val();
		}else if(cuil=="cuil_titular"){
			var cuil_final = jQuery('#<portlet:namespace/>cuil_titular').val();
		}
	
		var diaVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaDia').val());
		var mesVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaMes').val())+1;
		var anioVig=parseInt(jQuery('#<portlet:namespace />vigenteFechaAnio').val());
		
		
		var diaNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoDia').val());
		var mesNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoMes').val())+1;
		var anioNac=parseInt(jQuery('#<portlet:namespace />fechaNacimientoAnio').val());
		
		var parentesco = jQuery('#<portlet:namespace/>parentesco').val();
		
		

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_cuil&cuil='+cuil_final+'&vigenteFechaDia='+diaVig+'&vigenteFechaMes='+mesVig+'&vigenteFechaAnio='+anioVig+'&inte=1';
		url = url   +'&diaNac='+diaNac+'&mesNac='+mesNac+'&anioNac='+anioNac+'&parentesco='+parentesco;
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.validado=="1"){
					alert("<liferay-ui:message key='cuil-invalido'/>");
					respuesta = false;
				}else if(obj.validado=="2"){
					alert("<liferay-ui:message key='cuil-titular-existente'/>");
					return respuesta = false;
				}			 					
			}				                                                                                                                                                                                                                                                            
			
		});
		return respuesta;
	}
	
	
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
	
</script>