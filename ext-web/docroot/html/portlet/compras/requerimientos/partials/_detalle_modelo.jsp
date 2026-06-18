<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
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
        && reqDetalle.puedeEditarEstructura();

boolean usuarioPuedeCotizarDetalle =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)
        && reqDetalle.puedeEditarCotizacion();

boolean puedeABMDetalle = usuarioPuedeABMDetalle && !soloLecturaDetalle;
boolean puedeCotizarDetalle = usuarioPuedeCotizarDetalle && !soloLecturaDetalle;
boolean puedeVerCotizacionDetalle =
        reqDetalle.puedeEditarCotizacion()
        || reqDetalle.isCotizado();

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

PortletURL prestadoresEnviadosURL = renderResponse.createRenderURL();
prestadoresEnviadosURL.setWindowState(LiferayWindowState.EXCLUSIVE);
prestadoresEnviadosURL.setParameter("struts_action", "/compras/buscar_prestadores_enviados");
prestadoresEnviadosURL.setParameter("limite", "20");

int idRequerimientoCompraDetalle = reqDetalle.getIdRequerimientoCompra();

if (idRequerimientoCompraDetalle > 0) {
    prestadoresEnviadosURL.setParameter(
            "id_requerimiento_compra",
            String.valueOf(idRequerimientoCompraDetalle)
    );
}

boolean requerimientoPersistidoDetalle = idRequerimientoCompraDetalle > 0;

int detalleColspan =
        4
        + (puedeVerCotizacionDetalle ? 3 : 0)
        + (puedeABMDetalle ? 1 : 0);
%>