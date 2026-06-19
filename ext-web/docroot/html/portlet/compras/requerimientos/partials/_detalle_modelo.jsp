<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion" %>

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

Map<String, String> preciosCotizacionRestaurados =
        new HashMap<String, String>();
Map<String, String> prestadoresCotizacionRestaurados =
        new HashMap<String, String>();
Map<String, String> labelsPrestadorCotizacionRestaurados =
        new HashMap<String, String>();

boolean restaurarCotizacion =
        ParamUtil.getBoolean(
                renderRequest,
                "compras_error",
                false
        )
        && ("saveCotizacion".equals(
                ParamUtil.getString(
                        renderRequest,
                        "compras_operacion",
                        ""
                )
        ) || "cerrarCotizacion".equals(
                ParamUtil.getString(
                        renderRequest,
                        "compras_operacion",
                        ""
                )
        ));

if (restaurarCotizacion) {
    int cantidadDetallesRestaurados =
            ParamUtil.getInteger(
                    renderRequest,
                    "detalle_count",
                    0
            );

    for (int i = 0;
            i < cantidadDetallesRestaurados;
            i++) {

        String prefix =
                "detalle_"
                        + i
                        + "_";

        String idDetalleRestaurado =
                ParamUtil.getString(
                        renderRequest,
                        prefix + "id",
                        ""
                );

        if (WebKeysCompras.isEmpty(
                idDetalleRestaurado
        )) {
            continue;
        }

        preciosCotizacionRestaurados.put(
                idDetalleRestaurado,
                ParamUtil.getString(
                        renderRequest,
                        prefix
                                + "precio_unitario_estimado",
                        ""
                )
        );

        prestadoresCotizacionRestaurados.put(
                idDetalleRestaurado,
                ParamUtil.getString(
                        renderRequest,
                        prefix + "id_prestador",
                        ""
                )
        );

        labelsPrestadorCotizacionRestaurados.put(
                idDetalleRestaurado,
                ParamUtil.getString(
                        renderRequest,
                        prefix + "prestador_label",
                        ""
                )
        );
    }
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

int idRequerimientoCompraDetalle =
        reqDetalle.getIdRequerimientoCompra();

boolean requerimientoPersistidoDetalle =
        idRequerimientoCompraDetalle > 0;

List<PrestadorCotizacion> prestadoresEnviadosDetalle =
        new ArrayList<PrestadorCotizacion>();

String errorPrestadoresEnviadosDetalle = "";

if (puedeCotizarDetalle && requerimientoPersistidoDetalle) {
    try {
        prestadoresEnviadosDetalle =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPrestadoresEnviados(
                                idRequerimientoCompraDetalle
                        );
    } catch (Exception e) {
        errorPrestadoresEnviadosDetalle =
                e.getMessage() != null
                        ? e.getMessage()
                        : "No se pudieron cargar los prestadores enviados.";
    }
}

boolean hayPrestadoresEnviadosDetalle =
        prestadoresEnviadosDetalle != null
        && !prestadoresEnviadosDetalle.isEmpty();

int detalleColspan =
        4
        + (puedeVerCotizacionDetalle ? 3 : 0)
        + (puedeABMDetalle ? 1 : 0);
%>
