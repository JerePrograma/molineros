<%@page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<portlet:renderURL var="volverIndiceConveniosURL" windowState="<%= LiferayWindowState.NORMAL.toString() %>">
    <portlet:param name="struts_action" value="/prestadores/convenios_prestacionales" />
    <portlet:param name="restoreConvPrest" value="true" />
</portlet:renderURL>
	
	<script type="text/javascript">

	function redirigir(codigo) {
                        var boolcodigo=jQuery('#<portlet:namespace />por_codigo').is(':checked');
                        var vistaView = "por_rango";
                        if (boolcodigo == true) {
                                vistaView = "por_codigo";
                        }

                        var backURL = '<%= volverIndiceConveniosURL.toString() %>'.replace(/&amp;/g, '&');

                        var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/prestadores/editar_convenio_prest_entry';
                        url = url + '&id_convenio='+codigo+'&por_codigo='+vistaView;
                        url = url + '&<%=Constants.CMD%>='+'<%=Constants.VIEW%>';
                        url = url + '&backURL=' + encodeURIComponent(backURL);

                        window.location.href = url;
                        return false;
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
                PortletURL iteratorURL = renderResponse.createRenderURL();
                iteratorURL.setWindowState(LiferayWindowState.NORMAL);
                iteratorURL.setParameter("struts_action", "/prestadores/convenios_prestacionales");
                iteratorURL.setParameter("restoreConvPrest", "true");

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

                SearchContainer searchContainer = new SearchContainer(
                        renderRequest,
                        null,
                        null,
                        SearchContainer.DEFAULT_CUR_PARAM,
                        Integer.MAX_VALUE,
                        iteratorURL,
                        headerNames,
                        LanguageUtil.get(pageContext, "no-convenios-prestac-were-found")
                );
								    
					if(null!=convPresLista){
				 				//Seteo el total de la lista.
					 	int total = convPresLista.size();
					 	searchContainer.setTotal(total);
					 	
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < convPresLista.size(); i++) {
					 		ConvenioPrestacional convPrest = (ConvenioPrestacional) convPresLista.get(i);
				 					ResultRow row = new ResultRow(convPrest,convPrest.getId(), i);
					 				//PortletURL rowURL = renderResponse.createRenderURL();
					 				//portletURL.setParameter("struts_action","/prestadores/view_contrato_entry");
					 				//portletURL.setParameter("por_codigo", vistaView);
					 				row.addText(String.valueOf(convPrest.getId()));
					 				String codPrestadorText = "";
                                                                        String razonSocialText = "";
                                                                        String cuitPrestadorText = "";
                                                                        String tipoPrestadorText = "";

                                                                        if (convPrest.getPrestador() != null) {
                                                                                codPrestadorText = String.valueOf(convPrest.getPrestador().getId_prestador());

                                                                                if ("null".equalsIgnoreCase(codPrestadorText) || "0".equals(codPrestadorText)) {
                                                                                        codPrestadorText = "";
                                                                                }

                                                                                razonSocialText = convPrest.getPrestador().getDescripcion() != null ? convPrest.getPrestador().getDescripcion() : "";
                                                                                cuitPrestadorText = convPrest.getPrestador().getCuit() != null ? convPrest.getPrestador().getCuit() : "";

                                                                                if (convPrest.getPrestador().getTipo() != null && convPrest.getPrestador().getTipo().getDescripcion() != null) {
                                                                                        tipoPrestadorText = convPrest.getPrestador().getTipo().getDescripcion();
                                                                                }
                                                                        }

                                                                        row.addText(codPrestadorText);
                                                                        row.addText(razonSocialText);
                                                                        row.addText(cuitPrestadorText);
                                                                        row.addText(tipoPrestadorText);
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
								row.addJSP("left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/prestadores/convenios_prest/editar_borrar_convenio_prest.jsp");
							}							
							else {
								row.addText("");
							}								
				 			resultRows.add(row);
					 	}
					 }
			%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />