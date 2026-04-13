<%@ include file="/html/portlet/novedades/init.jsp" %>

<% 
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
boolean showDELETEButtons = true;  //PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
boolean showALTAButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
PreAfiliado afiliado=(PreAfiliado)row.getObject();
String cuil = afiliado.getCuil_titular();
String inte = String.valueOf(afiliado.getInte());
String id_pre_afi = String.valueOf(afiliado.getId());
String tipo_nov = String.valueOf(afiliado.getTipo_novedad());
String strutsUrl = "";
String accion = afiliado.getTipo_novedad();
boolean enPadron = afiliado.isDe_alta_portal();
boolean existeAfi = false;

int resultInte = PreAfiliadoServiceUtil.existePreAfiliado(afiliado.getCuil()); 

if(resultInte == 1 || resultInte == 2) {// existe integrante en otro grupo familiar 
	existeAfi = true;
}

if(Integer.parseInt(inte) == 0 ){
	strutsUrl = "/afiliados/editar_afiliado_entry";
}else if(Integer.parseInt(inte) > 0 && tipo_nov.equalsIgnoreCase(Constants.ADD)) {
	strutsUrl = "/afiliados/cargar_integrante_entry";
}else if(Integer.parseInt(inte) > 0 && tipo_nov.equalsIgnoreCase(Constants.UPDATE)) {
	strutsUrl =  "/afiliados/editar_afiliado_entry";
}

String deleteURL="javascript:if(confirm('Estás seguro que lo deseas dar de baja?')) { ejecutarBaja('"+cuil+"','"+inte+"','"+id_pre_afi+"');}";

Afiliado afiliadoEntry = null;
if(!inte.equalsIgnoreCase("0")){
	try{	
		afiliadoEntry = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil, 0);
	}catch(Exception e){}
}else{
	afiliadoEntry = new Afiliado();
}
%>
<!-- Si no esta de baja, tenemos el boton editar novedad-pre-afi, el boton eliminar novedad-pre-afi 
y si tenemos permisos de ROL_ABM_AFILIADO podemos dar con el boton alta afiliado (titular e integrante) -->

<c:if test="<%=!enPadron && Validator.isNull(afiliado.getBaja_fecha()) 
|| (Validator.isNotNull(afiliado.getBaja_fecha()) && afiliado.getBaja_fecha().after(new Date())) %>">

<liferay-ui:icon-menu>
	<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="editURL">
		<portlet:param name="struts_action" value="/afiliados/editar_pre_afiliado" />		
		<portlet:param name="cuil_titular" value="<%= cuil %>" />
		<portlet:param name="inte" value="<%= inte %>" />
		<portlet:param name="id" value="<%= id_pre_afi %>" />
		<portlet:param name="<%=Constants.CMD%>" value="<%= Constants.EDIT %>" />
	</portlet:renderURL>
	<liferay-ui:icon image="edit" url="<%= editURL %>" />
	<%	
		if(showDELETEButtons && afiliado.getId() > 0 && !enPadron){
	%>
			<liferay-ui:icon image="delete" url="<%= deleteURL %>"/>
		
		<%	    /*debe estar el titular pre-cargado en afiliado para que pueda incorporar al grupo fliar...*/
				
			if(!existeAfi  && afiliadoEntry != null && showALTAButtons){
		%>	
				<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="cargaURL">
					<portlet:param name="struts_action" value="<%=strutsUrl %>" />		
					<portlet:param name="cuil_titular" value="<%= cuil %>" />
					<portlet:param name="inte" value="<%= inte %>" />
					<portlet:param name="id_pre_afiliado" value="<%= id_pre_afi %>" />
					<portlet:param name="pre_carga" value="true" />
					<portlet:param name="<%=Constants.CMD%>" value="<%= accion %>" />
				</portlet:renderURL>
				<liferay-ui:icon image="add" url="<%= cargaURL %>"/>
		<% 	}else if(afiliado.getInte() > 0 && afiliadoEntry == null){ %>
				<%String mensaje1 = "Primero se debe dar de alta al titular del grupo familiar"; %>
		<liferay-ui:icon
				image="../message_boards/priority_sticky"
				message="<%=mensaje1 %>" />
		<%  } %>
	<%	} %>
		
</liferay-ui:icon-menu>
</c:if>

<c:if test="<%= Validator.isNotNull(afiliado.getBaja_fecha()) && afiliado.getBaja_fecha().before(new Date()) %>">
<%String mensaje2 =  "UsrBaja: " + afiliado.getBaja_usr()+" el " + sdf.format(afiliado.getBaja_fecha()); %>
	<liferay-ui:icon
			image="../message_boards/ban_user"
			message="<%=mensaje2 %>" />
</c:if> 


<script type="text/javascript">

function ejecutarBaja(cuil,inte,id){
	
	var cascada = false;
	
	if(inte==0){
		cascada = confirm("<liferay-ui:message key='desea-propagar-baja-planes-grupo-fliar'/>");
		/* if(!confirm("<liferay-ui:message key='desea-propagar-baja-planes-grupo-fliar'/>")){
			return false;
		} */
	}
	var url ='<portlet:renderURL><portlet:param name="struts_action" value="/afiliados/editar_pre_afiliado"/>
								 <portlet:param name="<%=Constants.CMD%>" value="<%=Constants.DELETE%>"/>
			  </portlet:renderURL>';
			  
	/* var params = '&cuil_titular='+cuil+'&inte='+inte;
	url = url + params; */
	
/* 	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url); */
/* 	jQuery.post(url,{cuil_titular:cuil,inte:inte}); */
	jQuery('#<portlet:namespace />busquedaPreAfiliadoDiv').load(url,{cuil_titular:cuil,inte:inte,idPreAfi:id,esCascada:cascada});
	
}
</script>