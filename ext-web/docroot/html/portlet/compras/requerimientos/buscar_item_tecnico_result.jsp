<%@ include file="/html/portlet/compras/init.jsp" %>

<%
String errorBusqueda = (String) request.getAttribute("COMPRAS_ERROR_BUSQUEDA");
String callbackBusqueda = (String) request.getAttribute("COMPRAS_CALLBACK_BUSQUEDA");
String codigoBusqueda = (String) request.getAttribute("COMPRAS_CODIGO_NOMENCLADOR");
String descripcionBusqueda = (String) request.getAttribute("COMPRAS_DESCRIPCION_NOMENCLADOR");
String idTipoBusqueda = (String) request.getAttribute("COMPRAS_ID_TIPO_NOMENCLADOR");
String esPrestMedBusqueda = (String) request.getAttribute("COMPRAS_ES_PREST_MED");
%>

<% if (errorBusqueda != null && errorBusqueda.length() > 0) { %>
    <div class="portlet-msg-error"><%= HtmlUtil.escape(errorBusqueda) %></div>
<% } else { %>
    <liferay-util:include
            page="/html/portlet/autorizaciones/nomenclador/nomenclador_search_result.jsp">
        <liferay-util:param name="descripcionnomenclador" value="<%= descripcionBusqueda %>" />
        <liferay-util:param name="codigonomenclador" value="<%= codigoBusqueda %>" />
        <liferay-util:param name="tiponomenclador" value="<%= idTipoBusqueda %>" />
        <liferay-util:param name="soloActivos" value="true" />
        <liferay-util:param name="muestrabaja" value="false" />
        <liferay-util:param name="esPrestMed" value="<%= esPrestMedBusqueda %>" />
        <liferay-util:param name="callback_seleccion" value="<%= callbackBusqueda %>" />
        <liferay-util:param name="devolver_ids" value="true" />
    </liferay-util:include>
<% } %>
