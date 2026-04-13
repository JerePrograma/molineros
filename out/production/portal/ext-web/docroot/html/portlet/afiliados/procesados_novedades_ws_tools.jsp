<%@ include file="/html/portlet/afiliados/init.jsp" %>

<% 

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
AfiliadoOpe afiliado=(AfiliadoOpe)row.getObject();


String procesarUndoURL="javascript:procesar('"+afiliado.getIdTransaccion()+"','"+afiliado.getOperacion()+"','"+true+"');";

String procesarAddURL="javascript:procesar('"+afiliado.getIdTransaccion()+"','"+afiliado.getOperacion()+"','"+false+"');";

String enviarEmailURL="javascript:enviarMailHomologacionPS('"+afiliado.getCuil_titular()+"','"+afiliado.getInte()+"');";


%>

<table class="lfr-table">
	<tr>
		<td>
		<liferay-ui:icon-menu>
		
		<%	if (afiliado.getProcesada() == null  
			&& (( afiliado.getInte() ==0 && 
				afiliado.getOperacion() == 0 || 
				afiliado.getOperacion() == 3 ||
				afiliado.getOperacion() == 5) 
				|| (afiliado.getInte() > 0 && (afiliado.getOperacion() == 1 || afiliado.getOperacion() == 4)) )
		){ %>
			<liferay-ui:icon 
					image="../common/add"
					message="Marcar Procesado"
					url="<%=procesarAddURL%>"/>
					
       <% }%>
       <%	if (afiliado.getProcesada() != null
    		   && (( afiliado.getInte() ==0 && 
				afiliado.getOperacion() == 0 || 
				afiliado.getOperacion() == 3 ||
				afiliado.getOperacion() == 5) 
				|| (afiliado.getInte() > 0 && (afiliado.getOperacion() == 1 || afiliado.getOperacion() == 4)))
    		){ %>
			<liferay-ui:icon 
					image="../common/undo"
					message="Volver a Enviar"
					url="<%=procesarUndoURL%>"/>

		<% }%>
		<%if (afiliado.isInfoDatoHomologacionPS()){ %>
			<liferay-ui:icon 
					image="../message_boards/email"
					message="Enviar E-Mail Homologación"
					url="<%=enviarEmailURL%>"/>
	    <%} %>
	

	
		</liferay-ui:icon-menu>
		</td>
	</tr>
</table>				
<script type="text/javascript">



function enviarMailHomologacionPS(cuil_titular_p, inte){
	var confirmar = false;
	confirmar=confirm ('¿Desea enviar el alta a homologación PS?	'+'\nEnviar Alta?');
	if(confirmar){
		
		var tipoOperacion =  'ENVIAR_MAIL_HOMOLOGACION_PS';
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/historico_prevencion_ws&cmd='+tipoOperacion;
		jQuery('#<portlet:namespace />buscando').show();		
		jQuery("#<portlet:namespace/>buscarHistoricosPrevencionWS").load(url,{cuil_titular:cuil_titular_p, inte_select:inte}, function(){jQuery('#<portlet:namespace />buscando').hide();});
	}	
}




function procesar(idTransaccion, tipoOpe, accion){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	var tipoOperacion =  'PROCESAR';
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/historico_prevencion_ws&cmd='+tipoOperacion;
	jQuery('#<portlet:namespace />buscando').show();		
	jQuery("#<portlet:namespace/>buscarHistoricosPrevencionWS").load(url,{idTransaccion:idTransaccion,operacion:tipoOpe, accion:accion}, function(){jQuery('#<portlet:namespace />buscando').hide();});
}






</script>