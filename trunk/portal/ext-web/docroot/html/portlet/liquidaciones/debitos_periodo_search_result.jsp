<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%

	String viewStr = (String)request.getAttribute(WebKeysLiquidaciones.VIEW_LIQUIDACION);
	boolean esView = false;
	if (viewStr != null){
		esView = true;

	}

	ArrayList <LiquidacionDebitoTercero> liquidacionDebitoTerceros = (ArrayList<LiquidacionDebitoTercero>)request.getAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACIONES_DEBITOS_TERCEROS);
	String pendientes = (String) request.getAttribute("pendientes");

	PortletURL portletURLLiquidacionDebitos = renderResponse.createRenderURL();
	List<String> headerNamesLiquidacionDebitos = new ArrayList<String>();
	headerNamesLiquidacionDebitos.add("periodo");
	headerNamesLiquidacionDebitos.add("observaciones");
	if (pendientes.equals("1")) {
		headerNamesLiquidacionDebitos.add("Generar");
	} else {
		headerNamesLiquidacionDebitos.add("Editar");
	}

	SearchContainer searchContainerLiquidacionDebitos= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLLiquidacionDebitos, headerNamesLiquidacionDebitos,
			LanguageUtil.get(pageContext, "no-liquidaciones-were-found"));

	if(null!=liquidacionDebitoTerceros){
		int total=liquidacionDebitoTerceros.size();
		searchContainerLiquidacionDebitos.setTotal(total);
		List resultRowsLiquidacionList = searchContainerLiquidacionDebitos.getResultRows();
		for (int i = 0; i < liquidacionDebitoTerceros.size(); i++) {
			LiquidacionDebitoTercero liquidacionDebitoTercero = (LiquidacionDebitoTercero) liquidacionDebitoTerceros.get(i);
			ResultRow rowLiquidacion = new ResultRow(liquidacionDebitoTercero,liquidacionDebitoTercero.getId_liquidacion(), i);
			rowLiquidacion.addText(liquidacionDebitoTercero.getPeriodoString());
			rowLiquidacion.addText(liquidacionDebitoTercero.getObservaciones());
			if (!esView){
				StringBuilder sb= new StringBuilder();
				if (!pendientes.equals("1")) {
					sb.append("<img alt=\"<liferay-ui:message key='action.EDIT'/>\" src=\"");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/edit.png\" onClick=\"javascript:editarLiquidacionDT('");
					sb.append(liquidacionDebitoTercero.getId_liquidacionString());
					sb.append("');\" />");
				} else {
					sb.append("<img alt=\"<liferay-ui:message key='action.COPY'/>\" src=\"");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/copy.png\" onClick=\"javascript:altaLiquidacionDT('");
					sb.append(liquidacionDebitoTercero.getPeriodoString());
					sb.append("');\" />");
				}
				rowLiquidacion.addText(sb.toString());
			}
			resultRowsLiquidacionList.add(rowLiquidacion);
		}
	}
%>

<c:choose>
	<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	</c:when>
</c:choose>
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

<liferay-ui:search-iterator searchContainer="<%=searchContainerLiquidacionDebitos%>" />