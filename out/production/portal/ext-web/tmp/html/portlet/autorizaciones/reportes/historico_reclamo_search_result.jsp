<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ page import="ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.MovimientoReclamoHistorico" %>

<%
List<MovimientoReclamoHistorico> historico =
    (List<MovimientoReclamoHistorico>) request.getAttribute(
        WebKeysAutorizaciones.HISTORICO_RECLAMO);

PortletURL portletURL = renderResponse.createRenderURL();

List<String> headerNames = new ArrayList<String>();
headerNames.add("Estado");
headerNames.add("Observación");
headerNames.add("Fecha de alta");
headerNames.add("Fecha de baja");
headerNames.add("Usuario");

SearchContainer searchContainer = new SearchContainer(
    renderRequest, null, null,
    SearchContainer.DEFAULT_CUR_PARAM,
    SearchContainer.DEFAULT_DELTA, portletURL,
    headerNames, LanguageUtil.get(pageContext, "no-histo-mov-were-found"));

if (historico != null) {
    searchContainer.setTotal(historico.size());
    List resultRows = searchContainer.getResultRows();

    for (int i = 0; i < historico.size(); i++) {
        MovimientoReclamoHistorico item = historico.get(i);
        ResultRow row = new ResultRow(item, String.valueOf(i), i);
        row.addText(item.getEstado() != null ? item.getEstado() : "");
        row.addText(item.getObservacion() != null ? item.getObservacion() : "");
        row.addText(item.getAltaFecha() != null ? item.getAltaFecha().toString() : "");
        row.addText(item.getBajaFecha() != null ? item.getBajaFecha().toString() : "");
        row.addText(item.getAltaUsuario() != null ? item.getAltaUsuario() : "");

        resultRows.add(row);
    }
}
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />