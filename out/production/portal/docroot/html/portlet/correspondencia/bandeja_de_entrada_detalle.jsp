<%@ include file="/html/portlet/correspondencia/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
	// ver si tiene rol Recepcionista
	boolean showItemsRecepcionista = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);
	//obtengo lista del inbox
	List<ItemCorrespondencia> correspondencia = (ArrayList<ItemCorrespondencia>) request.getSession().getAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA_RESULT);
	/* (ArrayList<ItemCorrespondencia>) CorrespondenciaServiceUtil.bandejaEntrada(user, showItemsRecepcionista); */

	BusquedaBandejaCorreoFiltro filtro = (BusquedaBandejaCorreoFiltro) request.getSession().getAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA);
	
	String viewStr = (String) request.getAttribute("view");
	boolean esView = false;
	if (viewStr != null){
		esView = true;
	}
	
	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
	String numerosDeReferencia="";
	
	if(showItemsRecepcionista){ 
		headerNames.add("Paquete");
	} 
	headerNames.add("Nro.Corr. | Nro.Crm.");
	headerNames.add("Recepción");
	headerNames.add("Tipo");
	headerNames.add("Remitente");
	headerNames.add("Contenido");
	headerNames.add("Comprobante");
	headerNames.add("Importe");
	headerNames.add("Fecha Emisión");
	headerNames.add("Destinatario");
	headerNames.add("Fecha Alta"); 
	headerNames.add("Ver");		
	headerNames.add("Marcar Recibido");		

	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-correspondencia-were-found"));

//recupero coincidencias
if (null != correspondencia && correspondencia.size() > 0 ) {
	total = correspondencia.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	for (int i = 0; i < correspondencia.size(); i++) {

		ItemCorrespondencia corr = (ItemCorrespondencia) correspondencia.get(i);
		
		ResultRow row = new ResultRow(corr, String.valueOf(corr.getId()), i);
		PortletURL rowURL = renderResponse.createRenderURL();
			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		
		if(showItemsRecepcionista){ 
			row.addText(String.valueOf(corr.getListaPaquete() != null ? corr.getListaPaquete().getId_paquete() : ""));
		}
		numerosDeReferencia = corr.getId_correspondencia() + (corr.getIdCRMContacto()!=null&&corr.getIdCRMContacto()!=0?" | " + corr.getIdCRMContacto():"");
		row.addText(numerosDeReferencia);
		row.addText( DateUtils.format(corr.getCabecera().getFecha(), DateUtils.SHORT));	
		row.addText(corr.getTipoRemitenteDestinatario());
		row.addText(corr.getRemitente());
		row.addText(corr.getContenido() == null ? "" : corr.getContenido());
		row.addText(corr.getComprobanteString());
		row.addText(corr.getImporte() != null && corr.getImporte().compareTo(BigDecimal.ZERO) == 1 ? corr.getImporte().toPlainString() : "");
		if(corr.getComprobanteString() != null && corr.getComprobanteString().length() >0){
			row.addText(corr.getFecha_emision() != null ? DateUtils.format(corr.getFecha_emision(), DateUtils.SHORT) : "");
		} else { 
			row.addText("");
		}
		row.addText(corr.getDestinatario());
		row.addText( DateUtils.format(corr.getCabecera().getAlta_fecha(), DateUtils.SHORT));
		
		StringBuilder sb = new StringBuilder("");	
		StringBuilder sbVerDetalle = new StringBuilder("");	

	    if(corr.getEstado().equalsIgnoreCase("RECIBIDO") || corr.getEstado().equalsIgnoreCase("ENVIADO") ){
	    	sb.append("");
	    	/*Solo para quienes reciben FCs para liquidar */
			if( (corr.getUsuario().equalsIgnoreCase(user.getScreenName()) || corr.getUsuario().equalsIgnoreCase("TODOS") ) 
					&& !corr.getComprobanteString().isEmpty() 
					&& corr.getTipoRemitenteDestinatario().equalsIgnoreCase("Prestador") 
/* 					&& (corr.getSectorDescripcion().equalsIgnoreCase("liquidaciones") 
							|| corr.getSectorDescripcion().equalsIgnoreCase("Liquidaciones_Reintegros")) */
					&& corr.getSector().equalsIgnoreCase("99214")
					&& !filtro.getEstado().equalsIgnoreCase("RECIBIDO")){  /*Agregue esto porque sino quedaba habilitado para liquidar las correspondencia ya liquidadas*/
				sb.append("<img src=\""); 
				sb.append("/html/images/icon_currency_over.gif\" alt=\"liquidar\" height='16px;' weight='18px;' onClick=\"javascript:liquidarFC('");
				sb.append(String.valueOf(corr.getId() ));
				sb.append("','");
				sb.append(String.valueOf(i));
				sb.append("');\" />"); 
			}
		}else{
			if(corr.getEstado().equalsIgnoreCase("REVISAR")){
				sb.append("<center><b>Revisar</b></center>");
			}else if(corr.getUsuario().equalsIgnoreCase(user.getScreenName()) || 
					 corr.getUsuario().equalsIgnoreCase("TODOS")) {
				
					sb.append("<img src=\""); 
					sb.append("/html/images/mail_leido.png\" alt=\"leido\" height='16px;' weight='18px;' onClick=\"javascript:marcaRecibido('");
					if(corr.getCabecera().getTipoEnvio().equalsIgnoreCase("PAQ_FARMACIA")){ //Paquetes Tittarelli
						sb.append(String.valueOf(corr.getListaPaquete().getId_paquete()));   //getPaquete().getId()
						sb.append("','PAQ','0');\" />"); 
					}else{ // Items comunes
						sb.append(String.valueOf(corr.getId() ));
						sb.append("','ITEM','");
						sb.append(String.valueOf(corr.getIdCRMContacto()));
						sb.append("');\" />");
						
						/* if(corr.getIdCRMContacto()!=null && corr.getIdCRMContacto() > 0){
							sbRederivar.append("<img src=\"");
							sbRederivar.append("/html/themes/classic/images/common/assign.png\" alt=\"derivar\" height='16px;' weight='18px;' onClick=\"javascript:marcaDerivar('");
							sbRederivar.append(String.valueOf(corr.getId() ));
							sbRederivar.append("','ITEM','");
							sbRederivar.append(String.valueOf(corr.getIdCRMContacto()));
							sbRederivar.append("');\" />");
						} */
						
						if(corr.getIdCRMContacto()!=null && corr.getIdCRMContacto() > 0){
							sbVerDetalle.append("<img alt=\"<liferay-ui:message key='ver-contacto'/>\" height='16px;' weight='18px;' src=\"");
							sbVerDetalle.append(themeDisplay.getPathThemeImages());
							sbVerDetalle.append("/common/view.png\" onClick=\"javascript:verCrmContacto('");
							sbVerDetalle.append(String.valueOf(corr.getIdCRMContacto()));
							sbVerDetalle.append("');\" />");
						}
						
					}
					
				}else{
					sb.append("");
				}
		}	
		row.addText(sbVerDetalle.toString());
		if(corr.getIdCRMContacto()!=null && corr.getIdCRMContacto() > 0 && corr.getEstado().equalsIgnoreCase("INGRESADO")){
			row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/crm/derivar_cerrar_contacto.jsp");	
		}else{
		    row.addText(sb.toString());			
		}
		
		resultRows.add(row);
	}
}

%>
<div class="search-results">
	<form action="/correspondencia/liquidarFC" id = "<portlet:namespace />fm1" name = "<portlet:namespace />fm1" method="post" ></form>
	<c:choose>
		<c:when test="<%= total != 1 %>">
			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
		</c:when>
		<c:otherwise>
			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
		</c:otherwise>
	</c:choose>
	<liferay-util:include page="/html/portlet/correspondencia/paginador_entradas_salidas.jsp">
		<liferay-util:param name="llamada" value="inbox"/>
	</liferay-util:include>
</div>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<div class="search-results">
	<c:choose>
		<c:when test="<%= total != 1 %>">
			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
		</c:when>
		<c:otherwise>
			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
		</c:otherwise>
	</c:choose>
</div>
<script type="text/javascript">
var popupComCierre;
function marcaRecibido(id_item_o_paq, tipo_marca, id_contacto_derivado ) {
	var quiereCerrarContacto;
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/correspondencia/marcar_recibido" /></portlet:renderURL>';
	url= url + "&item_corr="+id_item_o_paq+"&tipo_marca="+tipo_marca+"&id_crm_cont_derivado="+id_contacto_derivado+'&esCierre=true';
	
	if(parseInt(id_contacto_derivado) > 0){
		quiereCerrarContacto = confirm('Desea marcar CERRADO el contacto n° '+id_contacto_derivado);
		if(!quiereCerrarContacto){
			//solo no quiere cerrar contacto, tampoco le dejo marcar el item correspondencia como recibido.
			// esto es solo para aquellos items, que vienen de una derivacion de CRM
			/* return false; */
// NUEVO: si no quiere cerrar contacto entonces le puede cargar un comentario de avance y mantener el estado actual...
			
			popupComCierre= new Liferay.Popup({title:"<liferay-ui:message key="completar-comentarios-avance" />",modal:true, width: 650, position:['center',30]});
			var urlCierre = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/completar_comentarios_cierre';
			urlCierre= urlCierre + "&item_corr="+id_item_o_paq+"&tipo_marca="+tipo_marca+"&id_crm_cont_derivado="+id_contacto_derivado+'&cierre=false';
			
			jQuery(popupComCierre).load(urlCierre); 
			// desde el popupComCierre al cerrar se guarda el comentario
		}else{
			popupComCierre= new Liferay.Popup({title:"<liferay-ui:message key="completar-comentarios-cierre" />",modal:true, width: 650, position:['center',30]});
			/* popupComCierre= Liferay.Popup({title:"<liferay-ui:message key="completar-comentarios-cierre" />",modal:true,width:650}); */
			var urlCierre = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/completar_comentarios_cierre';
			urlCierre= urlCierre + "&item_corr="+id_item_o_paq+"&tipo_marca="+tipo_marca+"&id_crm_cont_derivado="+id_contacto_derivado+'&cierre=true';

			jQuery(popupComCierre).load(urlCierre); 
			// desde el popupComCierre al cerrar se guarda el comentario
		}
	}else{ // si es 0, es Item clasico de correspondencia, debe marcar recibido 
		jQuery('#<portlet:namespace />busquedaCorrespondenciaInboxDiv').load(url);
	}
				
}

function ejecutarCierreRecibido(id_item_o_paq, tipo_marca, id_contacto_derivado, es_cierre){
	
	if(id_contacto_derivado == '0'){
		Liferay.Popup.close(popupComCierre);
		return false; 
	}
	if(!validarCierreRecibido() ){ //esta funcion estaen la pagina de seleccion
		return false; 
	}

	var obser = jQuery('#<portlet:namespace />obs_derivacion').val();
	
	var params = {"item_corr":id_item_o_paq, "tipo_marca":tipo_marca, "id_crm_cont_derivado":id_contacto_derivado,
			/* "deriva_edificio_destino":edi_dest, "deriva_sector_destino":sec_dest, "deriva_usuario_destino":usu_dest, */
			"deriva_observaciones":obser, "esCierre":es_cierre};	

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/correspondencia/marcar_recibido" /></portlet:renderURL>';
	/* url= url + "&item_corr="+id_item_o_paq+"&tipo_marca="+tipo_marca+"&id_crm_cont_derivado="+id_contacto_derivado; */

	jQuery('#<portlet:namespace />busquedaCorrespondenciaInboxDiv').load(url,params, function() {
    		Liferay.Popup.close(popupComCierre);          															
    		 });	
    
}


function liquidarFC(id_item_corr, posicionItem) {
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/correspondencia/liquidar_FC" /></portlet:renderURL>';
	url= url + "&item_corr="+id_item_corr+"&item_posicion="+posicionItem;
	document.<portlet:namespace />fm1.method = 'post';
	submitForm(document.<portlet:namespace />fm1, url);				
}

var popupDeriva;
function marcaDerivar(id_item_o_paq, tipo_marca, id_contacto_derivado ) {
	popupDeriva = new Liferay.Popup({title:"<liferay-ui:message key="seleccione-a-quien-derivar" />",modal:true, width: 700, position:['center',30]});
	/* popupDeriva= Liferay.Popup({title:"<liferay-ui:message key="seleccione-a-quien-derivar" />",modal:true,width:700}); */
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/seleccionar_derivacion';
		url= url + "&item_corr="+id_item_o_paq+"&tipo_marca="+tipo_marca+"&id_crm_cont_derivado="+id_contacto_derivado;
	jQuery(popupDeriva).load(url); 
}
	

function ejecutarDerivacion(id_item_o_paq, tipo_marca, id_contacto_derivado){
	
	if(id_contacto_derivado == '0'){
		Liferay.Popup.close(popupDeriva);
		return false; 
	}
	
	if(!validarUsuDeriva() ){ //esta funcion estaen la pagina de seleccion
		return false; 
	}
	var edi_dest = jQuery('#<portlet:namespace />edificio_destino').val();
	var sec_dest = jQuery('#<portlet:namespace />sector_destino').val();
	var usu_dest = jQuery('#<portlet:namespace />usuario_destino').val();
	var obs = jQuery('#<portlet:namespace />obs_derivacion').val();
	var obser = obs.trim();
	
	jQuery('#<portlet:namespace />ejecutaDerivacion').hide(); 
	
	<%-- var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/correspondencia/marcar_recibido_derivar"/></portlet:actionURL>'; --%>
	
	var params = {"item_corr":id_item_o_paq, "tipo_marca":tipo_marca, "id_crm_cont_derivado":id_contacto_derivado,
			"deriva_edificio_destino":edi_dest, "deriva_sector_destino":sec_dest, "deriva_usuario_destino":usu_dest,
			"deriva_observaciones":obser};	
	<!-- encodeURIComponent(obser)-->
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/marcar_recibido_derivar';
	
    jQuery('#<portlet:namespace />busquedaCorrespondenciaInboxDiv').load(url,params, function() {
    		Liferay.Popup.close(popupDeriva);          															
    		 }
    );	
    
	/* jQuery.post(url, params, function() {																																											
										Liferay.Popup.close(popupDeriva);
										jQuery('#<portlet:namespace />busquedaCorrespondenciaInboxDiv').load(url);
								  }); */
}

var popupCRM;

function verCrmContacto(idCont) {
	var params = "&<%= Constants.CMD %>=" + "<%= Constants.VIEW%>";
	params = params + '&idContacto='+idCont;
	
	popupCRM = new Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true, width: 880, position:['center',30]});
	
	/* popupCRM= Liferay.Popup({title:"<liferay-ui:message key="detalle-contacto" />",modal:true,width:880,height:455}); */
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/editar_contacto_entry';   		       	
	url = url + params;
	jQuery(popupCRM).load(url);	
}
</script>	
