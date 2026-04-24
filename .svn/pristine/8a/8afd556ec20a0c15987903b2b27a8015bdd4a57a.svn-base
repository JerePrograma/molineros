<%@page import="ar.com.ospim.util.DateUtils"%>
<%@ include file="/html/portlet/afiliados/init.jsp" %>

<% 

String portlet_name_space = portletDisplay.getId();
String portlet_name="afiliados";
if (portlet_name_space == null || portlet_name_space.trim().equals("")){
	portlet_name = "afiliados";
}else if(portlet_name != null && portlet_name_space.trim().equals("CAI_1")){
	portlet_name = "cai";
}

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Afiliado afiliado=(Afiliado)row.getObject();
String cuil = afiliado.getCuil_titular();
String inte = afiliado.getInteAsString();
String apellidoNombre = afiliado.getApellidoNombre();

String reincorporarURL="javascript:recuperarAfiliado('"+cuil+"','"+inte+"');";		

String nuevo_CRM_URL="javascript:nuevoCrmContacto('"+cuil+"','"+inte+"');";

String nuevo_CRM_LEGALES_URL="javascript:nuevoCrmDocumentoLegal('"+cuil+"','"+inte+"');";

String confirmar = "¿Estás seguro que desea eliminar el afiliado? " + apellidoNombre + " y no podrá deshacerse ?";

String deleteFisicoURL="javascript:if(confirm('"+confirmar+"')) { eliminarFisicamenteAfiliadoInte('"+cuil+"','"+inte+"');}";

String editarAfiliadoDeBaja="javascript:editarAfiliadoBorrado('"+cuil+"','"+inte+"');";

String cargar_SUSPENCION_COBERTURA_URL="javascript:preGestionCoberturaMed('"+cuil+"','"+inte+"','deactivate');";

String finalizar_SUSPENCION_COBERTURA_URL="javascript:preGestionCoberturaMed('"+cuil+"','"+inte+"','restore');";




//Permiso para cargar llamadas de att. al cliente
boolean showABMCrm = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM); 
boolean showCAI = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_VER_PORTLET_CAI); 
boolean showDel = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ELIMINAR_AFILIADO);
boolean showEditarDeBaja = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_MODIFICA_AFILIADO_DE_BAJA);

//Permiso para cargar documentacion crm legales
boolean showABMCrmLegales = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM_LEGALES); 

boolean showABMAfi = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);

boolean adminCobertura = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ADMIN_SUSPENDE_COBERTURA);		

%>

<table class="lfr-table">
	<tr>
		<td>
			<c:if test="<%= Validator.isNotNull(afiliado.getBaja_fecha()) && afiliado.getBaja_fecha().getTime()<System.currentTimeMillis() && !afiliado.esTitular()%>">
				<liferay-ui:icon
						image="../message_boards/ban_user"
						message="afiliado-dado-de-baja"/>
			</c:if>
			<%if(showABMAfi) { %>
			<c:if test="<%= Validator.isNotNull(afiliado.getBaja_fecha()) && afiliado.getBaja_fecha().getTime()<System.currentTimeMillis() && afiliado.esTitular()%>">
				<liferay-ui:icon
						image="../message_boards/ban_user"
						message="recuperar"
						url="<%=reincorporarURL%>"/>
			</c:if>
			<%} %>
		</td>
		<td>
		<liferay-ui:icon-menu>
		<%if(showABMAfi && (
				Validator.isNull(afiliado.getBaja_fecha()) 
				|| (Validator.isNotNull(afiliado.getBaja_fecha()) 
						&& afiliado.getBaja_fecha().getTime() >= DateUtils.getMismoDia_00_00hs(new Date()).getTime() ))) { %>
			<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
				<portlet:param name="struts_action" value="/afiliados/editar_afiliado_entry" />		
				<portlet:param name="cuil_titular" value="<%= cuil %>" />
				<portlet:param name="inte" value="<%= inte %>" />
			</portlet:renderURL>
			<liferay-ui:icon image="edit" url="<%= editURL %>" />
			<%	
				String deleteURL="javascript:if(confirm('Estás seguro que lo deseas dar de baja?')) { eliminaAfiliado('"+cuil+"','"+inte+"');}";		
			%>
			<liferay-ui:icon image="delete" url="<%= deleteURL %>"/>
			
		<%} %>		
			
		
		<%if(afiliado != null && showABMAfi && showDel && 
				afiliado.getDetalleFechasSuperintendencia() !=null &&
				Validator.isNull(afiliado.getDetalleFechasSuperintendencia().getFechaBajaSuper()) &&
				Validator.isNull(afiliado.getDetalleFechasSuperintendencia().getFechaModiSuper()) &&
				Validator.isNull(afiliado.getDetalleFechasSuperintendencia().getFechaPresentacionSuper()) &&
				(  afiliado.getInte() != 0)) { %>
			<liferay-ui:icon 
					image="../common/close"
					message="Borrado Definitivo"
					url="<%=deleteFisicoURL%>"/>
		<%} %>
		
		<%if(showEditarDeBaja && afiliado != null 
							&& afiliado.getBaja_fecha() != null 
							&& DateUtils.esMayor(DateUtils.getMismoDia_23_59hs(new Date()), DateUtils.getMismoDia_23_59hs(afiliado.getBaja_fecha() )) )  { %>
			<liferay-ui:icon 
					image="../common/edit"
					message="Editar de Baja"
					url="<%=editarAfiliadoDeBaja%>"/>
		<%} %>
			
				
		<%if(showABMCrm || showCAI) { %>
			<liferay-ui:icon 
					image="../common/telephone"
					message="contacto"
					url="<%=nuevo_CRM_URL%>"/>
		<%} %>
		<%if(showABMCrmLegales) { %>
			<!-- image="../../../../../html/images/ico-martillo" -->		
			<liferay-ui:icon
					image="../../../../../html/images/ico-martillo18x18"
					message="Legal"
					url="<%=nuevo_CRM_LEGALES_URL%>"/>
		<%} %>		 
		<%if(adminCobertura && (afiliado.getSuspencionCobertura()==null ||
		    ( afiliado.getSuspencionCobertura().get(0).getVigenHasta()!=null && 
		    		DateUtils.esMayor(DateUtils.getMismoDia_23_59hs(new Date()), DateUtils.getMismoDia_23_59hs(afiliado.getSuspencionCobertura().get(0).getVigenHasta() ) 
		    			)
		))) { %>
			<liferay-ui:icon
					image="../../../../../html/images/prohibeCobMedica"
					message="Suspender cobertura"
					url="<%=cargar_SUSPENCION_COBERTURA_URL%>"/>
		<%} %>
		<%if(adminCobertura && afiliado.getSuspencionCobertura()!=null && afiliado.getSuspencionCobertura().get(0).getVigenHasta()==null) { %>
			<liferay-ui:icon
					image="../../../../../html/images/coberturaMedica"
					message="Reactivar cobertura"
					url="<%=finalizar_SUSPENCION_COBERTURA_URL%>"/>
		<%} %>		
		</liferay-ui:icon-menu>
		</td>
	</tr>
</table>				
<script type="text/javascript">



var popup;
function eliminaAfiliado(cuil, inte){
	popup= Liferay.Popup({title:"<liferay-ui:message key="seleccione-motivo-baja" />",modal:true,width:700});
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/motivo_baja&cuil_titular='+cuil+
	  '&inte='+inte;   
	jQuery(popup).load(url); 
}

function recuperarAfiliado(cuil, inte){
	popup= Liferay.Popup({title:"<liferay-ui:message key="recuperar" />",modal:true,width:700});
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/reincorporacion&cuil_titular='+cuil+
	  '&inte='+inte;
	jQuery(popup).load(url); 
}

function ejecutarBaja(cuil,inte){
	var $btn = jQuery('#<portlet:namespace/>btnGuardarBaja');
	var $guardando = jQuery('#<portlet:namespace/>guardandoBaja');

	var motivoBaja=jQuery("#<portlet:namespace/>tipo_aporte").val();
	var diaBaja=jQuery('#<portlet:namespace />fechaBajaDia').val();
	var mesBaja=parseInt(jQuery('#<portlet:namespace />fechaBajaMes').val());
	var anioBaja=jQuery('#<portlet:namespace />fechaBajaAnio').val();
	var borrar="borrar";
	
	verificaInteUnificaAportes(cuil);
	if(inte==0){
		if(!confirm("<liferay-ui:message key='desea-propagar-baja-planes-grupo-fliar'/>")){
			return false;
		}
	}
	
	$btn.hide();
	$guardando.show();
	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_afiliado_entry&<%=Constants.CMD%>=<%=Constants.DELETE%>';
	jQuery.post(url,{cuil_titular:cuil,inte:inte,motivo_baja:motivoBaja,baja_dia:diaBaja,baja_mes:mesBaja,baja_anio:anioBaja, borrar:borrar}, function() {																																											
																						Liferay.Popup.close(popup);
																						<portlet:namespace/>buscaGrupo(cuil);
	  																	  }).fail(function() {
	  																		$guardando.hide();
	  																		$btn.show();
	  																		alert('Ocurrió un error al procesar la baja.');
	  																	});
}
function verificaInteUnificaAportes(cuil_titular){
	 
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verificar_unifica_aportes&cuil='+cuil_titular;		 
		jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if(obj.verificado=="1"){
					alert("<liferay-ui:message key='cuil-titular-unifica'/>");
					document.getElementById(chkbox).checked=false; 
				} 					
			}
		}); 
	}
	
function nuevoCrmContacto(cuil_titu,integ) {
	<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_contacto_entry';  
	url=url+'&cuil_titular='+cuil_titu+'&integ='+integ;
	url=url+'&cmd=ADD&noAfiliado=false';
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url); --%>
	var xporlet ='<%=portlet_name%>';
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/__porlet/editar_contacto_entry" />'+
	'<liferay-portlet:param name="cuil_titular" value="__cuil_titu"/>'+
	'<liferay-portlet:param name="integ" value="__inte"/>'+
	'<liferay-portlet:param name="cmd" value="add"/>'+
	/* '<liferay-portlet:param name="noAfiliado" value="false"/>'+ */
	'<liferay-portlet:param name="contactoAfiliado" value="true"/>'+
	'</liferay-portlet:renderURL>';
	url = url.replace("__porlet",xporlet);
	url = url.replace("__cuil_titu",cuil_titu);
	url = url.replace("__inte",integ);

	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

function nuevoCrmDocumentoLegal(cuil_titu,integ) {
	<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_crm_legales_entry';  
	url=url+'&cuil_titular='+cuil_titu+'&inte='+integ;
	url=url+'&cmd=ADD&noAfiliado=false';
	document.<portlet:namespace />fm.method = 'post';
	
	submitForm(document.<portlet:namespace />fm, url); --%>
	var xporlet ='<%=portlet_name%>';
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="/__porlet/editar_crm_legales_entry" />'+
	'<liferay-portlet:param name="cuil_titular" value="__cuil_titu"/>'+
	'<liferay-portlet:param name="integ" value="__inte"/>'+
	'<liferay-portlet:param name="cmd" value="add"/>'+
	'<liferay-portlet:param name="noAfiliado" value="false"/>'+
	'</liferay-portlet:renderURL>';
	url = url.replace("__porlet",xporlet);
	url = url.replace("__cuil_titu",cuil_titu);
	url = url.replace("__inte",integ);

	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
	
}

function eliminarFisicamenteAfiliadoInte(cuil_titular,inte) {
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>">
					<portlet:param name="struts_action" value="/afiliados/borrar_afiliado_entry" />
					<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.DELETE%>" />
				</portlet:renderURL>';		
	
	url=url+'&cuil_titular='+cuil_titular+'&inteAux='+inte;
	
	jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
	
}



function editarAfiliadoBorrado(cuil_titular,inte) {
	
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
					<portlet:param name="struts_action" value="/afiliados/editar_afiliado_entry" />
				</portlet:renderURL>';		
	
	url=url+'&cuil_titular='+cuil_titular+'&inteAux='+inte;
	 
	
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url); 
	
}

function preGestionCoberturaMed(cuil_titular,inte, accion) {
	
	popup= Liferay.Popup({title:"<liferay-ui:message key="afi-sup-cob-med" />",modal:true,width:500});
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/pre_cargar_suspension_cobertura';
	url=url+'&cuil_titular='+cuil_titular+'&inte='+inte+'&accion='+accion;

	jQuery(popup).load(url); 
	
}

function ejecutarSuspencionCobertura(cuil,inte,accion){
	
	var diaSC=jQuery('#<portlet:namespace />fechaSupCobDia').val();
	var mesSC=parseInt(jQuery('#<portlet:namespace />fechaSupCobMes').val());
	var anioSC=jQuery('#<portlet:namespace />fechaSupCobAnio').val();
	
	if(accion =='<%=Constants.DEACTIVATE%>'){
		if(!confirm("<liferay-ui:message key='desea-suspender-cob-med'/>")){
			return false;
		}
	}else{
		if(!confirm("<liferay-ui:message key='desea-recuperar-cob-med'/>")){
			return false;
		}
	}
	
<%--  	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/aplicar_suspension_cobertura';
	
	var params = {"cuil_titular":cuil,"inte":inte,"accion":accion,"diaSuspCob":diaSC,"mesSuspCob":mesSC,"anioSuspCob":anioSC} 
	jQuery.post(url, params, function() {																																											
										Liferay.Popup.close(popup);
										/* <portlet:namespace/>buscaGrupo(cuil); */
										
								  });  --%>
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/aplicar_suspension_cobertura';
	
	var params = {"cuil_titular":cuil,"inte":inte,"accion":accion,"diaSuspCob":diaSC,"mesSuspCob":mesSC,"anioSuspCob":anioSC} 
	
	jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, params, function() {																																											
										Liferay.Popup.close(popup);
									     /* <portlet:namespace/>buscaGrupo(cuil);  */
								  }); 
}


</script>