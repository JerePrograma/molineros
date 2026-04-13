<%@page import="ar.com.uoma.facturacion.services.FacturacionServiceUtil"%>
<%@ include file="/html/portlet/uoma/init.jsp" %>

<script type="text/javascript">

<%String prefijo = request.getParameter("prefijo");%>

function pasarParametrosAParent(nroDoc, apellido,nombre,tipo,estado) {	
	
    jQuery("#<portlet:namespace />cliente_nro_doc<%=prefijo!=null?prefijo:""%>").val(nroDoc);
    jQuery("#<portlet:namespace />cliente_apellido<%=prefijo!=null?prefijo:""%>").val(apellido);
    jQuery("#<portlet:namespace />cliente_nombre<%=prefijo!=null?prefijo:""%>").val(nombre);
    jQuery("#<portlet:namespace />persfisica_tipo<%=prefijo!=null?prefijo:""%>").val(tipo);
    jQuery("#<portlet:namespace />persfisica_estado<%=prefijo!=null?prefijo:""%>").val(estado);
    jQuery("#<portlet:namespace />persfisica_seleccionada<%=prefijo!=null?prefijo:""%>").val("1");
    jQuery("#<portlet:namespace />btnBuscarCliente").hide();
    <portlet:namespace />cerrarPFisi<%=prefijo!=null?prefijo:""%>();
//    <portlet:namespace />cerrar();    
 }

</script>
<%

	/* List<Cliente> persFisicas = (ArrayList<Cliente>) portletSession.getAttribute(WebKeysUOMA.CLIENTES_EN_SESSION,
			PortletSession.APPLICATION_SCOPE); */
	
	List<Cliente> persFisicas = new ArrayList<Cliente>();
			
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("nro-documento");
	headerNames.add("apellido");
	headerNames.add("nombre");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-psifiscas-were-found"));
	//recupero coincidencias		
	if(null!=persFisicas){
		String cliente_nro_doc=(String)renderRequest.getParameter("cliente_nro_doc");
		String clienteString=(String)renderRequest.getParameter("cliente_apellido");

		String clienteNomString =  null, clienteApeString  = null;
		try {
			String[] values = clienteString.split(",");
			clienteApeString  = values[0];
			 clienteNomString =  values[1];
		}catch(Exception e) {
	    	
	    }


		/* persFisicas=ListUtils.traeCoincidenciasDeLista(persFisicas,clienteApeString,cliente_nro_doc); */
		persFisicas=FacturacionServiceUtil.getClientes(cliente_nro_doc, clienteApeString);
		//Seteo el total de la lista.
	 	int total = persFisicas.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total == 0){
			%>
			<script type="text/javascript">
				pasarParametrosAParent("<%=cliente_nro_doc%>", "<%=clienteApeString%>", "<%=clienteNomString%>", 
			      "<%=ar.com.uoma.facturacion.Cliente.TIPOS_CLIENTE.VISITA%>", "<%=ar.com.uoma.facturacion.Cliente.ESTADOS.SELECCIONADO%>");
			</script>				
			<%
		}else{
			if(total==1){
				Cliente clieUnico=(Cliente) persFisicas.get(0);
				%>
					<script type="text/javascript">
						pasarParametrosAParent("<%=clieUnico.getDocumentoNro()%>", "<%=clieUnico.getApellido()%>", "<%=clieUnico.getNombre()%>"
								, "<%=clieUnico.getTipo().name()%>", "<%=clieUnico.getEstado().name()%>");
					</script>				
				<%
			//More de una coincidencia	
			}else {
			 	searchContainer.setTotal(total);
			 	//persFisicas = ListUtil.subList(persFisicas, searchContainer.getStart(),searchContainer.getEnd());
				List resultRows = searchContainer.getResultRows();
			 	for (int i = 0; i < persFisicas.size(); i++) {
			 		Cliente cliente = (Cliente) persFisicas.get(i);
					ResultRow row = new ResultRow(cliente.getDocumentoNro(),cliente.getApellido(), i);			
					// Name and short description
					StringBuilder sb = new StringBuilder();
					sb.append("<a href='javascript:pasarParametrosAParent(\"");
					sb.append(cliente.getDocumentoNro());
					sb.append("\",\"");
					sb.append(cliente.getApellido());
					sb.append("\",\"");
					sb.append(cliente.getNombre());
					sb.append("\",\"");
					sb.append(cliente.getTipo().name());
					sb.append("\",\"");
					sb.append(cliente.getEstado().name());
					sb.append("\")'>");			
					sb.append(cliente.getDocumentoNro());
					sb.append("</a>");
					row.addText(sb.toString());
					StringBuilder sb2 = new StringBuilder();
					sb2.append("<a href='javascript:pasarParametrosAParent(\"");
					sb2.append(cliente.getDocumentoNro());
					sb2.append("\",\"");
					sb2.append(cliente.getApellido());
					sb2.append("\",\"");
					sb2.append(cliente.getNombre());
					sb2.append("\",\"");
					sb2.append(cliente.getTipo().name());
					sb2.append("\",\"");
					sb2.append(cliente.getEstado().name());
					sb2.append("\")'>");
					sb2.append(cliente.getApellido());
					sb2.append("</a>");
					row.addText(sb2.toString());
					StringBuilder sb3 = new StringBuilder();
					sb3.append("<a href='javascript:pasarParametrosAParent(\"");
					sb3.append(cliente.getDocumentoNro());
					sb3.append("\",\"");
					sb3.append(cliente.getApellido());
					sb3.append("\",\"");
					sb3.append(cliente.getNombre());
					sb3.append("\",\"");
					sb3.append(cliente.getTipo().name());
					sb3.append("\",\"");
					sb3.append(cliente.getEstado().name());
					sb3.append("\")'>");
					sb3.append(cliente.getNombre());
					sb3.append("</a>");
					row.addText(sb3.toString());
					resultRows.add(row);
			 	}
		}
		
		
		%>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
		}
	}
%>

