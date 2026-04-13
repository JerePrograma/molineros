<%@ include file="/html/portlet/correspondencia/init.jsp" %>
<%
	//obtengo lista de session	
	List<ItemCorrespondencia> correspondencia = null;
	correspondencia = (List<ItemCorrespondencia>) request.getSession().getAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA);
	boolean showABMButtons = PermissionUtil.userContainsRole(user, WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);
	
	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("Recep.");
	headerNames.add("Fecha");
	headerNames.add("Nro.");
	headerNames.add("T.Reg/T.Envío");
	headerNames.add("Paq.");
	headerNames.add("Remitente");
	headerNames.add("Tipo");
	headerNames.add("Destinatario");
	headerNames.add("Comprobante");
	headerNames.add("Importe");
	headerNames.add("Estado");
	if (showABMButtons) {
		headerNames.add("editar-borrar");
		headerNames.add("Incluir en paquete");
	}
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-correspondencia-were-found"));
	
	boolean hayEmpaquetables = false;
	
	//recupero coincidencias		
	if (null != correspondencia && correspondencia.size() > 0) {
		total = correspondencia.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < correspondencia.size(); i++) {
			ItemCorrespondencia corr = (ItemCorrespondencia) correspondencia.get(i);
			ResultRow row = new ResultRow(corr, String.valueOf(corr.getId()), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(WindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/correspondencia/view_correspondencia_entry");
 			rowURL.setParameter("id_correspondencia", String.valueOf(corr.getId_correspondencia()));
 			rowURL.setParameter("id_item_correspondencia", String.valueOf(corr.getId()));
 			rowURL.setParameter("tipo_registro", corr.getCabecera().getTipoRegistro());
 			rowURL.setParameter("view", "true");
			row.addText(corr.getCabecera().getAlta_usr()); 
			row.addText(corr.getCabecera().getFechaAsString());
			row.addText(String.valueOf(corr.getCabecera().getId_correspondencia()),rowURL);
			String tipoRegTipoEnvio="";
			tipoRegTipoEnvio = corr.getCabecera().getTipoRegistro().substring(0, 1);
			for(int k=0; k < WebKeysCorrespondencia.TIPOS_ENVIOS.length ;k++){
				if(WebKeysCorrespondencia.TIPOS_ENVIOS[k][0].equals(corr.getCabecera().getTipoEnvio() )){
					tipoRegTipoEnvio = tipoRegTipoEnvio + " / " + WebKeysCorrespondencia.TIPOS_ENVIOS[k][2]; 
					k = WebKeysCorrespondencia.TIPOS_ENVIOS.length;
				}
			}
			row.addText(tipoRegTipoEnvio);
			row.addText(String.valueOf(corr.getListaPaquete().getId_paquete()));
			row.addText(corr.getRemitente());
 			row.addText(corr.getTipoRemitenteDestinatario());
			row.addText(corr.getDestinatario());
			row.addText(corr.getComprobanteString());
			row.addText(corr.getImporte() != null && corr.getImporte().compareTo(BigDecimal.ZERO) == 1 ? corr.getImporte().toPlainString() : "");
			row.addText(corr.getEstado());

			if (showABMButtons) {
				row.addJSP("right", SearchEntry.DEFAULT_VALIGN,
								"/html/portlet/correspondencia/editar_borrar_correspondencia.jsp");
			}
			
			StringBuffer sb = new StringBuffer("");
			// si no está de baja, si no está en paquete y si es mensajería y es entrada en estado ingresabe o salida en estado ingresado
			// que el ABM_correspondencia que lo recibe, lo empaqueta solo si no es su lugar de recepcion...
			if(
				  Validator.isNull(corr.getBaja_fecha()) && 
				  (corr.getCabecera().getTipoEnvio().equalsIgnoreCase("CORREOINTERNO") || corr.getCabecera().getTipoEnvio().equalsIgnoreCase("PAQ_FARMACIA"))&& 
			      ( (corr.getEstado().equals("INGRESADO") || corr.getEstado().equals("REVISAR") ) && corr.getListaPaquete() != null && corr.getListaPaquete().getId_paquete() ==0 ) &&
     	     	  ( Long.parseLong(corr.getCabecera().getLugar()) == Long.valueOf(user.getOrganizations().get(0).getOrganizationId()) )
			)  {
				   
					sb.append("<input type=\"checkbox\"");
					sb.append("name=\"");
					sb.append("empaquetar" + corr.getId());
					sb.append("\" id=\"");
					sb.append("empaquetar" + corr.getId());
					sb.append("\" value=\""); //checked='checked'
					sb.append(corr.getId());
					sb.append("\"/>");				
			
					hayEmpaquetables = true;
					
			}
			row.addText(sb.toString());
			resultRows.add(row);
		}
	}
%>
<div class="search-results">
	<c:choose>
		<c:when test="<%= total != 1 %>">
			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
		</c:when>
		<c:otherwise>
			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
		</c:otherwise>
	</c:choose>
	<liferay-util:include page="/html/portlet/correspondencia/paginador_entradas_salidas.jsp">
		<liferay-util:param name="llamada" value="entradas_salidas"/>
	</liferay-util:include>

</div>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<br/>

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

<c:if test="<%= hayEmpaquetables %>">		
	<div align="center">
		<input type="hidden" value="" id="<portlet:namespace />paq_descripcion" name="<portlet:namespace />paq_descripcion" />
		<input type="button" value="Generar Paquete" onClick="<portlet:namespace />pre_empaquetar();" />
	</div>											
</c:if>

<script type="text/javascript"> 
 
function validarSeleccionados() {
	valido=false;
	form1 = document.<portlet:namespace />fm;
	var obj;								
	for(a=0;a<form1.elements.length;a++){
		obj = form1.elements[a];
		if(obj.type=="checkbox" && obj.checked==true){
			valido=true;
			break;
		}
	}
	if(!valido){ 
		return false; 
	}
	return true;
}
var popup;
function <portlet:namespace />pre_empaquetar() {
	
	popup= Liferay.Popup({title:"<liferay-ui:message key="pre-empaquetado" />",modal:true,width:300});
	var urlpre = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/pre_paquete';   		       	
	jQuery(popup).load(urlpre);
}
function <portlet:namespace />empaquetar() {
	if (validarSeleccionados()) {
		var urlpaq = '<portlet:actionURL windowState="<%= windowState.MAXIMIZED.toString() %>"/>&struts_action=/correspondencia/crear_paquete';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, urlpaq);
	}else {
		alert ('No es posible generar la lista');
	}
}
function <portlet:namespace />cerrarPopUp(){
	Liferay.Popup.close(popup);
	<portlet:namespace />empaquetar();
}

</script>