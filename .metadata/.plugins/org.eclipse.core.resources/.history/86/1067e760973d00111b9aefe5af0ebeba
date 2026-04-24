<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
	
	<script type="text/javascript">

	function redirigir(codigo) {		
			var boolcodigo=jQuery('#<portlet:namespace />por_codigo').is(':checked');
			var vistaView = "por_rango";
			if (boolcodigo == true) {
				vistaView = "por_codigo";				
			}			
			
			var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/liquidaciones/editar_convenio_prest_entry';
			url = url + '&id_convenio='+codigo+'&por_codigo='+vistaView;
			url = url + '&<%=Constants.CMD%>='+'<%=Constants.VIEW%>'
			document.<portlet:namespace />fm.method = 'post';
			submitForm(document.<portlet:namespace />fm, url);
		}     										
	</script>

			<%
				//Si debe mostrarse el btn de agregar afiliado
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_CONVENIO_PREST);
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			
					List<ConvenioPrestacional> convPresLista = (ArrayList<ConvenioPrestacional>)request.getSession().getAttribute(WebKeysLiquidaciones.BUSQUEDA_CONVENIOS_PRESTAC_RESULTS);
					if (convPresLista == null || convPresLista.size() == 0) {
							convPresLista = new ArrayList<ConvenioPrestacional>();
					}				
					PortletURL portletURL = renderResponse.createRenderURL();
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					 		List<String> headerNames = new ArrayList<String>();		 	
					 		headerNames.add("conv-prest-numero");
					 		headerNames.add("cod-prestador");
					 		headerNames.add("razon-social");
				 			headerNames.add("cuit");
				 			headerNames.add("tipo-prestador");
				 			headerNames.add("estado");
					 		headerNames.add("fecha-alta");
					 		headerNames.add("usuario-alta");
					 		headerNames.add("Ver");
					if(showABMButtons) {
						headerNames.add("editar-borrar");
					}
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE,portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-convenios-prestac-were-found"));
								    
					if(null!=convPresLista){
				 				//Seteo el total de la lista.
					 	int total = convPresLista.size();
					 	searchContainer.setTotal(total);
					 	
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < convPresLista.size(); i++) {
					 		ConvenioPrestacional convPrest = (ConvenioPrestacional) convPresLista.get(i);
				 					ResultRow row = new ResultRow(convPrest,convPrest.getId(), i);
					 				//PortletURL rowURL = renderResponse.createRenderURL();
					 				portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
					 				//portletURL.setParameter("struts_action","/liquidaciones/view_contrato_entry");
					 				portletURL.setParameter("struts_action","/liquidaciones/editar_convenio_entry");
					 				portletURL.setParameter("id_contrato", String.valueOf(convPrest.getId()));	
					 				portletURL.setParameter("cmd", "view");	
					 				//portletURL.setParameter("por_codigo", vistaView);
					 				row.addText(String.valueOf(convPrest.getId()));
					 				row.addText(String.valueOf(convPrest.getPrestador().getId_prestador()));
					 				row.addText(convPrest.getPrestador().getDescripcion());
					 				row.addText(convPrest.getPrestador().getCuit());
					 				row.addText(convPrest.getPrestador().getTipo().getDescripcion());
					 				row.addText(convPrest.getEstado().toString());
					 				row.addText(sdf.format(convPrest.getAltaFecha()));
					 				row.addText(convPrest.getAltaUsr());
					 				
					 				StringBuilder sbVerDetalle = new StringBuilder("");	
					 				sbVerDetalle.append("<img alt=\"<liferay-ui:message key='ver-contrato'/>\" height='16px;' weight='18px;' src=\"");
							sbVerDetalle.append(themeDisplay.getPathThemeImages());
							sbVerDetalle.append("/common/view.png\" onClick=\"javascript:redirigir('");
							sbVerDetalle.append(String.valueOf(convPrest.getId()));
							sbVerDetalle.append("');\" />");
							row.addText(sbVerDetalle.toString());
					 				/* row.addText("<a href='#' onclick='redirigir(" +contrato.getId_contratoAsString()+ ");'>Ver</a>"); */
							// Action
							if (showABMButtons) {
								row.addJSP("left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/prestadores/editar_borrar_convenio_prest.jsp");
							}							
							else {
								row.addText("");
							}								
				 			resultRows.add(row);
					 	}
					 }
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />