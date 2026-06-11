<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>

<%!
private String jsDetalleCompra(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}
%>

<%
String namespaceDetalleCompra = renderResponse.getNamespace();

RequerimientoCompra reqDetalle =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (reqDetalle == null) {
    reqDetalle =
            (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

if (reqDetalle == null) {
    reqDetalle = new RequerimientoCompra();
}

Object soloLecturaAttrDetalle =
        renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String strutsActionDetalle = ParamUtil.getString(renderRequest, "struts_action", "");
String modoDetalle = ParamUtil.getString(renderRequest, "modo", "");

boolean soloLecturaDetalle =
        Boolean.TRUE.equals(soloLecturaAttrDetalle)
        || ParamUtil.getBoolean(request, "solo_lectura", false)
        || "/compras/ver_requerimiento".equals(strutsActionDetalle)
        || "ver".equalsIgnoreCase(modoDetalle);

boolean usuarioPuedeABMDetalle =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)
        && reqDetalle.isEditable();

boolean puedeABMDetalle = usuarioPuedeABMDetalle && !soloLecturaDetalle;

List<RequerimientoCompraDetalle> detalles = reqDetalle.getDetalles();

if (detalles == null) {
    detalles = new ArrayList<RequerimientoCompraDetalle>();
}

Integer idSectorActual = reqDetalle.getSectorId();

int sectorIdParametro = ParamUtil.getInteger(request, "sector_id", 0);

if ((idSectorActual == null || idSectorActual.intValue() <= 0) && sectorIdParametro > 0) {
    idSectorActual = Integer.valueOf(sectorIdParametro);
}

String idSectorActualString =
        idSectorActual != null && idSectorActual.intValue() > 0
                ? String.valueOf(idSectorActual.intValue())
                : "";

Object articulosAttr = renderRequest.getAttribute("ARTICULOS_COMPRA");

if (articulosAttr == null) {
    articulosAttr = request.getAttribute("ARTICULOS_COMPRA");
}

List<CompraArticulo> articulos = null;

if (articulosAttr instanceof List) {
    articulos = (List<CompraArticulo>) articulosAttr;
}

if (articulos == null) {
    articulos = new ArrayList<CompraArticulo>();
}

PortletURL detalleActionURL = renderResponse.createActionURL();
detalleActionURL.setWindowState(WindowState.MAXIMIZED);
detalleActionURL.setParameter("struts_action", "/compras/editar_requerimiento_detalle");

int idRequerimientoCompraDetalle = reqDetalle.getIdRequerimientoCompra();

boolean requerimientoPersistidoDetalle = idRequerimientoCompraDetalle > 0;

int detalleColspan = 4 + (puedeABMDetalle ? 1 : 0);
%>