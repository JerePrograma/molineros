<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	
	boolean showABMOpciones= PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_OPCIONES);
	
	DetalleOpcionesSS detOpSss = (DetalleOpcionesSS) request.getSession().getAttribute(WebKeysAfiliados.OPCIONSS_EN_EDICION);
	
	boolean esView = false;
	String esViewStr=(String)request.getAttribute("esView");
	if(esViewStr != null && esViewStr.equalsIgnoreCase("view")){
		esView = true;
	}
		
	/* long id_opcionsss=detOpSss!=null&&detOpSss.getId()!=null?(long)detOpSss.getId():0; */
	
	String tabsA = "informacion_general";
	StringBuilder tabsAValues = new StringBuilder("informacion_general");
/* 	String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH); */

	String tabsAMostrar = request.getParameter("tabs_a_mostrar");
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
%>

<form action="" method="post" name="<portlet:namespace />fm">
	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<table class="lfr-table">

	<c:if test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<table>
			<tr> 
				<%if(showABMOpciones && !esView){ %> 			
					<td align="left">	
						<input type="button" value="<liferay-ui:message key="alta-opcion" />" onClick="<portlet:namespace />altaOpcionAfi();" />
					</td>
				<% } %>
			</tr>
		</table>
	</c:if>	
	
	<liferay-ui-custom:tabs 
		names="<%= tabsANames %>" 
		tabsValues="<%= tabsAValues.toString() %>" 
		portletURL="<%= portletURL %>"  
		value="<%= tabsA%>"/> 
	<c:choose>
		<c:when test='<%= tabsA.equals("informacion_general")%>'>
			<tr>
				<td>
					<liferay-util:include page="/html/portlet/afiliados/editar_opcionAfi.jsp" />
				</td>
			</tr>					
		</c:when>
	</c:choose>
	
</table>
<table>
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>	
</table>
	
	<table>
		<tr>
			<td colspan="10">&nbsp;</td>
		</tr>
		<tr>
			<%if(showABMOpciones && !esView){ %> 			
			<td colspan="3" align="center">			
				<input type="button" value="<liferay-ui:message key="save" />" 
					   onClick="javascript:<portlet:namespace />saveOpcionSss();" />&nbsp;
			</td>
			<td>&nbsp;</td>
			<td colspan="3" align="center">	
				<input type="button" value="<liferay-ui:message key="alta-opcion" />" onClick="<portlet:namespace />altaOpcionAfi();" />
			</td>
			<% } %>
		</tr>		
	</table>

	
	<input type="hidden" name="<portlet:namespace/>unifica_aportes" id="<portlet:namespace/>unifica_aportes" value="NO" />
	<input type="hidden" name="<portlet:namespace/>valida_fechas" id="<portlet:namespace/>valida_fechas" value="OK" />
	 	
</form>

<script type="text/javascript">

/* jQuery('#<portlet:namespace />buscandoDetalles').hide(); */




function <portlet:namespace />saveOpcionSss() {
		
   
	
	if (<portlet:namespace />validarCampos()) {
	 		<%if(null!=detOpSss && null!=detOpSss.getId() && detOpSss.getId() > 0 ){%>				
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE%>";			
		<%}else{%>
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.ADD %>";
		<%}%> 
		
      if(validarRamoMolinero()){
		
		var url = "<portlet:actionURL windowState='<%= LiferayWindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/afiliados/editar_opcion_entry'/></portlet:actionURL>";		
		submitForm(document.<portlet:namespace />fm, url);
      } 	
	}
	return false;
}



function <portlet:namespace />altaOpcionAfi() {
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_opcion_entry" /></portlet:actionURL>';
	<%-- document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = '<%=Constants.ADD%>'; --%>
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}




function validarRamoMolinero() {
    var cuit="";
    var params="";
    var cuitempl = jQuery("#<portlet:namespace/>cuit_empleador").val();
    var respuesta=true;
    var rtaMolinero=false;
    var rtaNoExisteAfip=false;
    params += "&nroCuitEmpresa="+cuitempl;
    
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_ramo_molinero';
	   url = url + params;
	   jQuery.ajax({   
		   url: url,
		   async: false,
		   success: function(data) {
				var obj = jQuery.parseJSON(data);
				var resp = obj.existe;
				var respNoExisteAfip = obj.noexiste;
				rtaMolinero=(resp  === 'true');
				rtaNoExisteAfip=(respNoExisteAfip  === 'true');
	   		}
	   }); 
	   
	   if(rtaMolinero){
		  respuesta=confirm ('Es Ramo Molinero '+'\nDesea continuar?');
		   
	   }
	   
	   if(rtaNoExisteAfip){
	   		respuesta=confirm ('El empleador no está cargado en nuestra base de AFIP '+'\nDesea continuar?');
			   
	   }
	   
       return  respuesta;    
	 
}


function <portlet:namespace />validarCampos(){
	
	var delegId=jQuery('#<portlet:namespace />id_delegacion').val();
	if(delegId.length==0){
		alert('Ingrese la delegacion');
		jQuery('#<portlet:namespace />id_delegacion').focus();
		return false;
	}
	
	
	/* libro y tomo salen de la delegacion seleccionada */
	/* var libro=jQuery('#<portlet:namespace />libro').val();
	if(libro.length==0){
		alert('Ingrese el libro');
		jQuery('#<portlet:namespace />libro').focus();
		return false;
	}
	
	var tomo=jQuery('#<portlet:namespace />tomo').val();
	if(tomo.length==0){
		alert('Ingrese el tomo');
		jQuery('#<portlet:namespace />tomo').focus();
		return false;
	} */
	
	/* var apeynom=jQuery('#<portlet:namespace />apeynom').val(); */
	var apellido=jQuery('#<portlet:namespace />apellido').val();
	var nombre=jQuery('#<portlet:namespace />nombre').val();
	var reg=jQuery('#<portlet:namespace />regimen').val();
	var nroform = jQuery("#<portlet:namespace/>formNro").val();	
	var cuiltit = jQuery("#<portlet:namespace/>cuil_titular").val();
	var email = jQuery("#<portlet:namespace/>email").val();
	var celular = jQuery("#<portlet:namespace/>celular").val();
	var cuitempl = jQuery("#<portlet:namespace/>cuit_empleador").val();
	
	

	
	
	
	if(nroform.length==0){
		alert('Ingrese el número de formulario');
		jQuery('#<portlet:namespace />formNro').focus();
		return false;
	}
	if(parseInt(nroform)< 10000001 && reg=='RG' ){
		alert('El número de formulario debe ser mayor a 10000001');
		jQuery('#<portlet:namespace />formNro').focus();
		return false;
	}
	
	/* if(apeynom.length==0){
		alert('Ingrese el apellido y el nombre');
		jQuery('#<portlet:namespace />apeynom').focus();
		return false;
	} */
	
	if(apellido.length==0){
		alert('Ingrese el apellido');
		jQuery('#<portlet:namespace />apellido').focus();
		return false;
	}
	if(nombre.length==0){
		alert('Ingrese el nombre');
		jQuery('#<portlet:namespace />nombre').focus();
		return false;
	}
	
	if(cuiltit.length==0){
		alert('Ingrese el cuil del titular');
		jQuery('#<portlet:namespace />cuil_titular').focus();
		return false;
	}
	
	if(cuitempl.length==0 && reg=='RG'){
		alert('Ingrese el CUIT del Empleador');
		jQuery('#<portlet:namespace />cuit_empleador').focus();
		return false;
	}
	
	if(reg=='RG' && cuiltit == cuitempl){
		alert('Para régimen general el CUIT no puede ser igual Cuil');
		jQuery('#<portlet:namespace />regimen').focus();
		return false;
	}
	
/* 	if(reg=='RG'){
		if(parseInt(nroform)< 10000001 ){
			alert('El numero de formulario debe ser mayor a 10000001');
			jQuery('#<portlet:namespace />formNro').focus();
			return false;
		}
		if(cuitempl.length==0){
			alert('Ingrese el CUIT del Empleador');
			jQuery('#<portlet:namespace />cuit_empleador').focus();
			return false;
		}
		if(celular.length==0){
			alert('Ingrese un teléfono celular');
			jQuery('#<portlet:namespace />celular').focus();
			return false;
		}
		 if(email.length==0){
			alert('Ingrese un correo electrónico');
			jQuery('#<portlet:namespace />email').focus();
			return false;
		} 
	} */
	
	var loc=jQuery('#<portlet:namespace />localidad').val();
	var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
	
	if(loc.length==0 || parseInt(loc)==0 ){
		alert('Seleccione una localidad');
		jQuery('#<portlet:namespace />localidad').focus();
		return false;
	}
	if(cod_postal.length==0 || parseInt(cod_postal)==0 ){
		alert('Ingrese el Código postal');
		jQuery('#<portlet:namespace />cod_postal').focus();
		return false;
	}
	
	var calle=jQuery('#<portlet:namespace />calle').val();
	if(calle.length==0){
		alert('Ingrese la calle');
		jQuery('#<portlet:namespace />calle').focus();
		return false;
	}else if(calle.length > 20){
		alert('La calle no debe exceder los 20 caracteres');
		jQuery('#<portlet:namespace />calle').focus();
		return false;
	}
	var numero=jQuery('#<portlet:namespace />numero').val();
	if(numero.length==0){
		alert('Ingrese el Numero');
		jQuery('#<portlet:namespace />numero').focus();
		return false;
	}
	
	var telefpart=jQuery('#<portlet:namespace />telefono').val();
	var teleflab=jQuery('#<portlet:namespace />telefonoLab').val();
	var cod_area_celu = jQuery('#<portlet:namespace />cod_area_celular').val();
	var cod_area_tel = jQuery('#<portlet:namespace />cod_area_telefono').val();
	var cod_area_labo = jQuery('#<portlet:namespace />cod_area_tel_laboral').val();
	
	if(telefpart.length==0 && teleflab.length==0 && celular.length==0){
		alert('Ingrese al menos un teléfono');
		jQuery('#<portlet:namespace />telefono').focus();
		return false;
	}

	if(telefpart.length != 0 && cod_area_tel.length==0){
		alert('Ingrese el código de área del teléfono');
		jQuery('#<portlet:namespace />cod_area_telefono').focus();
		return false;
	}
	
	if(teleflab.length != 0 && cod_area_labo.length==0){
		alert('Ingrese el código de área del teléfono laboral');
		jQuery('#<portlet:namespace />cod_area_tel_laboral').focus();
		return false;
	}
	
	if(celular.length != 0 && cod_area_celu.length==0){
		alert('Ingrese el código de área del celular');
		jQuery('#<portlet:namespace />cod_area_celular').focus();
		return false;
	}
	
	if(telefpart.length == 0 && cod_area_tel.length!=0){
		alert('Ingrese el número del teléfono');
		jQuery('#<portlet:namespace />telefono').focus();
		return false;
	}
	
	if(teleflab.length == 0 && cod_area_labo.length!=0){
		alert('Ingrese el número del teléfono laboral');
		jQuery('#<portlet:namespace />telefonoLab').focus();
		return false;
	}
	
	if(celular.length == 0 && cod_area_celu.length!=0){
		alert('Ingrese el número del celular');
		jQuery('#<portlet:namespace />celular').focus();
		return false;
	}
	
	var unifica = jQuery("#<portlet:namespace/>unifica_aportes").val(); 
/* 	var unifica = document.getElementById("<portlet:namespace/>unifica_aportes").value;  */
	var ayncony = jQuery("#<portlet:namespace/>apeyNomConyuge").val();
	var cuilcony = jQuery("#<portlet:namespace/>cuilConyuge").val();

	if(reg=='MT'){
		
		if(unifica=='SI'){
			if(ayncony.length==0){
				alert('Ingrese el Apellido y Nombre del Cónyuge');
				jQuery('#<portlet:namespace />apeyNomConyuge').focus();
				return false;
			}
			if(cuilcony.length==0){
				alert('Ingrese el Cuil del Cónyuge');
				jQuery('#<portlet:namespace />cuilConyuge').focus();
				return false;
			}
		}
	}
	/* if(nroform==0){
		alert('ingrese el numero de formulario');
		jQuery('#<portlet:namespace />formNro').focus();
	}
	
	var apeyNom=jQuery('#<portlet:namespace />apeynom').val();
	if(apeynom.length==0){
		alert('Ingrese el Apellido y Nombre');
		jQuery('#<portlet:namespace />apeynom').focus();
		return false;
	} */

	/*
	var diaElec=jQuery('#<portlet:namespace />fechaEleccionDia').val();
	var mesElec= parseInt(jQuery('#<portlet:namespace />fechaEleccionMes').val())+1;
	var anioElec=jQuery('#<portlet:namespace />fechaEleccionAnio').val();		
	var fecha_eleccion = new Date(anioElec,mesElec,diaElec);
	var diaCert=jQuery('#<portlet:namespace />fechaCertiDia').val();
	var mesCert= parseInt(jQuery('#<portlet:namespace />fechaCertiMes').val())+1;
	var anioCert=jQuery('#<portlet:namespace />fechaCertiAnio').val();		
	var fecha_eleccion = new Date(anioElec,mesElec,diaElec);
	var fecha_certif = new Date(anioCert,mesCert,diaCert);
    
	 var fecha_eleccion3 = new Date(anioElec,mesElec,diaElec+3); 
    
	if(fecha_certif < fecha_eleccion) {
		 alert('La fecha de certificación debe ser como maximo 3 dias antes de la fecha de elección');
		 return false;		
	}*/
	
	/* verificaFechasElecyCertif(); */
	var fechas=jQuery('#<portlet:namespace />valida_fechas').val();
	if(fechas=="FAIL"){
		jQuery('#<portlet:namespace />fechaEleccionDia').focus();
		return false;
	}
	
	var osant=jQuery('#<portlet:namespace />obra_social_ant').val();
	if( (osant.length==0 || parseInt(osant)==0) && reg=='RG' ){
		alert('Seleccione la O.S. anterior');
		jQuery('#<portlet:namespace />obra_social_ant').focus();
		return false;
	}else if(osant=='112608'){
		alert('La O.S. anterior no debe ser igual a OSPIM');
		jQuery('#<portlet:namespace />obra_social_ant').focus();
		return false;
	/* }else if(osant=='128102'){
		alert('La O.S. anterior no es reconocida por la SSS');
		jQuery('#<portlet:namespace />obra_social_ant').focus();
		return false; */	
	}else if( osant != 402707 && osant != 402805 && osant != 402905 &&
			parseInt(osant)>=400000 && parseInt(osant)<500000){
		alert('O.S. de Dirección no permite traspaso a O.S. Sindical');
		return false;
	}
	
	
	
	return true; 
}



function filtrarLocalidadxCodPostal() {
	/*Si la lista de localidades ya fue seleccionada el id de localidad es distinto de 0, */
	/*si se paso y quedo elegido 'Seleccione una localidad' podemos disparar la busqueda de localidad con el CP */
	var localid = jQuery('#<portlet:namespace/>localidad').val();
	if(parseInt(localid)!=0){
		return true;
	}
	var cp = jQuery('#<portlet:namespace/>cod_postal').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_localidadsss_cp&codigopostal='+cp;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace/>localidad").length = 0;						
			var obj = jQuery.parseJSON(data);
			/* addElementToSelect("<portlet:namespace/>localidad", "Seleccione una localidad", 0); */
			for(var i =0;i< obj.listaFiltrada.length; i++){					
				var value = obj.listaFiltrada[i].split('|')[0];
				var text = obj.listaFiltrada[i].split('|')[1];
				addElementToSelect("<portlet:namespace/>localidad", text, value);					
			}				                                                                                                                                                                                                                                                            
		}
	});
}

<%-- function verificaFechasElecyCertif(){
	
	var diaEleccion=jQuery('#<portlet:namespace />fechaEleccionDia').val();	    
    var mesEleccion=parseInt(jQuery('#<portlet:namespace />fechaEleccionMes').val())+1;	    
    var anioEleccion=jQuery('#<portlet:namespace />fechaEleccionAnio').val();
    var fechaEleccionFinal = diaEleccion+'/'+mesEleccion+'/'+anioEleccion;
    var diaCerti=jQuery('#<portlet:namespace />fechaCertiDia').val();	    
    var mesCerti=parseInt(jQuery('#<portlet:namespace />fechaCertiMes').val())+1;	    
    var anioCerti=jQuery('#<portlet:namespace />fechaCertiAnio').val();
    var fechaCertificaFinal = diaCerti+'/'+mesCerti+'/'+anioCerti;

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_fecha_elec_certif&fecha_eleccion='+fechaEleccionFinal+'&fecha_certi='+fechaCertificaFinal;		 
	
	jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.validado=="0" || obj.validado=="1" || obj.validado=="2" || obj.validado=="3"){
	 				//return true;
					jQuery('#<portlet:namespace />valida_fechas').val("OK");
				}else if(obj.validado=="999"){
					alert('La fecha de certificación no puede ser posterior a la fecha de elección');
					jQuery('#<portlet:namespace />valida_fechas').val("FAIL");
				}else if(obj.validado=="998"){
					alert('La fecha de certificación o de elección no puede ser posterior a la fecha del dia');
					jQuery('#<portlet:namespace />valida_fechas').val("FAIL");	
				}else{	
					alert('La fecha de certificación debe ser como máximo 3 dias hábiles antes de la fecha de elección');
					jQuery('#<portlet:namespace />valida_fechas').val("FAIL");
				} 					
			}
		}); 
} --%>


</script>

