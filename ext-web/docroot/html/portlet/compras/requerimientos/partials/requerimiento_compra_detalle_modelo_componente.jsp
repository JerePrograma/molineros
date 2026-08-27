<%--
Responsabilidad:
    Prepara variables de presentación del detalle embebido.
Incluido desde:
    requerimiento_compra_detalle_embebido.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra" %>

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
        (RequerimientoCompra) request.getAttribute(
                "compras.requerimiento.req"
        );

if (reqDetalle == null) {
    reqDetalle =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION
            );
}

if (reqDetalle == null) {
    reqDetalle =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW
            );
}

if (reqDetalle == null) {
    reqDetalle = new RequerimientoCompra();
}

Object soloLecturaAttrDetalle =
        renderRequest.getAttribute(
                WebKeysCompras.SOLO_LECTURA_ATTR
        );

String strutsActionDetalle =
        ParamUtil.getString(
                renderRequest,
                "struts_action",
                ""
        );

String modoDetalle =
        ParamUtil.getString(
                renderRequest,
                "modo",
                ""
        );

boolean soloLecturaDetalle =
        Boolean.TRUE.equals(soloLecturaAttrDetalle)
        || ParamUtil.getBoolean(
                request,
                "solo_lectura",
                false
        )
        || "/compras/ver_requerimiento".equals(strutsActionDetalle)
        || "ver".equalsIgnoreCase(modoDetalle);

Object puedeEditarEstructuraAttr =
        request.getAttribute(
                "compras.requerimiento.puedeEditarEstructura"
        );

Object puedeEditarCotizacionAttr =
        request.getAttribute(
                "compras.requerimiento.puedeEditarCotizacion"
        );

boolean usuarioPuedeABMDetalle =
        puedeEditarEstructuraAttr instanceof Boolean
                ? Boolean.TRUE.equals(puedeEditarEstructuraAttr)
                : user != null
                        && PermissionUtil.userContainsRole(
                                user,
                                WebKeysCompras.ROL_ABM_COMPRAS
                        )
                        && reqDetalle.puedeEditarEstructura();

boolean usuarioPuedeEliminarDetalle =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        )
        && reqDetalle.puedeEliminarDetalle();

boolean puedeEliminarDetalle =
        usuarioPuedeEliminarDetalle
        && !soloLecturaDetalle;

boolean usuarioPuedeCotizarDetalle =
        puedeEditarCotizacionAttr instanceof Boolean
                ? Boolean.TRUE.equals(puedeEditarCotizacionAttr)
                : user != null
                        && PermissionUtil.userContainsRole(
                                user,
                                WebKeysCompras.ROL_COTIZAR_COMPRAS
                        )
                        && reqDetalle.puedeEditarCotizacion();

boolean puedeABMDetalle =
        usuarioPuedeABMDetalle
        && !soloLecturaDetalle;

boolean puedeCotizarDetalle =
        usuarioPuedeCotizarDetalle
        && !soloLecturaDetalle;

boolean puedeVerCotizacionDetalle =
        reqDetalle.puedeVerCotizacion();

List<RequerimientoCompraDetalle> detalles =
        reqDetalle.getDetalles();

if (detalles == null) {
    detalles = new ArrayList<RequerimientoCompraDetalle>();
}

List<TipoPrestacionCompra> tiposPrestacionDetalle =
        (List<TipoPrestacionCompra>) request.getAttribute(
                WebKeysCompras.TIPOS_PRESTACION_REQUERIMIENTO_COMPRA
        );

if (tiposPrestacionDetalle == null) {
    tiposPrestacionDetalle = new ArrayList<TipoPrestacionCompra>();
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
        && (
                "saveCotizacion".equals(
                        ParamUtil.getString(
                                renderRequest,
                                "compras_operacion",
                                ""
                        )
                )
                || "cerrarCotizacion".equals(
                        ParamUtil.getString(
                                renderRequest,
                                "compras_operacion",
                                ""
                        )
                )
        );

String idPrestadorAdjudicadoDetalle =
        restaurarCotizacion
                ? ParamUtil.getString(
                        renderRequest,
                        WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                        ""
                )
                : reqDetalle.getIdPrestadorAdjudicadoString();

String prestadorAdjudicadoDetalle =
        reqDetalle.getPrestadorAdjudicadoVisible();

if (restaurarCotizacion) {
    int cantidadDetallesRestaurados =
            ParamUtil.getInteger(
                    renderRequest,
                    "detalle_count",
                    0
            );

    for (int i = 0; i < cantidadDetallesRestaurados; i++) {
        String prefix =
                "detalle_" + i + "_";

        String idDetalleRestaurado =
                ParamUtil.getString(
                        renderRequest,
                        prefix + "id",
                        ""
                );

        if (WebKeysCompras.isEmpty(idDetalleRestaurado)) {
            continue;
        }

        preciosCotizacionRestaurados.put(
                idDetalleRestaurado,
                ParamUtil.getString(
                        renderRequest,
                        prefix + "precio_unitario_estimado",
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

    if (WebKeysCompras.isEmpty(idPrestadorAdjudicadoDetalle)) {
        String prestadorLegacyUnico = "";

        for (String prestadorLegacy :
                prestadoresCotizacionRestaurados.values()) {

            if (WebKeysCompras.isEmpty(prestadorLegacy)) {
                continue;
            }

            if (WebKeysCompras.isEmpty(prestadorLegacyUnico)) {
                prestadorLegacyUnico = prestadorLegacy;
            } else if (!prestadorLegacyUnico.equals(prestadorLegacy)) {
                prestadorLegacyUnico = "";
                break;
            }
        }

        idPrestadorAdjudicadoDetalle = prestadorLegacyUnico;
    }

    if (WebKeysCompras.isEmpty(prestadorAdjudicadoDetalle)
            && !WebKeysCompras.isEmpty(idPrestadorAdjudicadoDetalle)) {

        for (Map.Entry<String, String> entry :
                prestadoresCotizacionRestaurados.entrySet()) {

            if (idPrestadorAdjudicadoDetalle.equals(entry.getValue())) {
                String label =
                        labelsPrestadorCotizacionRestaurados.get(
                                entry.getKey()
                        );

                if (!WebKeysCompras.isEmpty(label)) {
                    prestadorAdjudicadoDetalle = label;
                    break;
                }
            }
        }
    }
}

Integer idSectorActual =
        reqDetalle.getSectorId();

int sectorIdParametro =
        ParamUtil.getInteger(
                request,
                "sector_id",
                0
        );

if ((idSectorActual == null || idSectorActual.intValue() <= 0)
        && sectorIdParametro > 0) {

    idSectorActual = Integer.valueOf(sectorIdParametro);
}

String idSectorActualString =
        idSectorActual != null
        && idSectorActual.intValue() > 0
                ? String.valueOf(idSectorActual.intValue())
                : "";

String sectorDescripcionActualString =
        reqDetalle.getSectorDescripcionVisible();

PortletURL detalleActionURL =
        renderResponse.createActionURL();
detalleActionURL.setWindowState(WindowState.MAXIMIZED);
detalleActionURL.setParameter(
        "struts_action",
        "/compras/editar_requerimiento_detalle"
);

PortletURL buscarItemTecnicoURL =
        renderResponse.createRenderURL();
buscarItemTecnicoURL.setWindowState(LiferayWindowState.EXCLUSIVE);
buscarItemTecnicoURL.setParameter(
        "struts_action",
        "/compras/buscar_item_tecnico"
);

int idRequerimientoCompraDetalle =
        reqDetalle.getIdRequerimientoCompra();

boolean requerimientoPersistidoDetalle =
        idRequerimientoCompraDetalle > 0;

boolean mostrarTipoCotizacionDetalle =
        !requerimientoPersistidoDetalle
        || !reqDetalle.esSectorSinCotizacionPrestador();

List<PrestadorCotizacion> prestadoresEnviadosDetalle =
        (List<PrestadorCotizacion>) request.getAttribute(
                "compras.requerimiento.prestadoresEnviados"
        );

if (prestadoresEnviadosDetalle == null) {
    prestadoresEnviadosDetalle =
            new ArrayList<PrestadorCotizacion>();
}

String errorPrestadoresEnviadosDetalle =
        (String) request.getAttribute(
                "compras.requerimiento.errorPrestadoresEnviados"
        );

if (errorPrestadoresEnviadosDetalle == null) {
    errorPrestadoresEnviadosDetalle = "";
}

boolean hayPrestadoresEnviadosDetalle =
        prestadoresEnviadosDetalle != null
        && !prestadoresEnviadosDetalle.isEmpty();

if (WebKeysCompras.isEmpty(prestadorAdjudicadoDetalle)
        && !WebKeysCompras.isEmpty(idPrestadorAdjudicadoDetalle)) {

    for (int i = 0;
            i < prestadoresEnviadosDetalle.size();
            i++) {

        PrestadorCotizacion prestador =
                prestadoresEnviadosDetalle.get(i);

        if (prestador != null
                && String.valueOf(
                        prestador.getIdPrestador()
                ).equals(idPrestadorAdjudicadoDetalle)) {

            prestadorAdjudicadoDetalle =
                    prestador.getEtiquetaVisible();
            break;
        }
    }
}

boolean prestadoresAdjudicadosMixtosDetalle =
        reqDetalle.tienePrestadoresAdjudicadosMixtos();

int detalleColspan =
        4
        + (mostrarTipoCotizacionDetalle ? 1 : 0)
        + (puedeVerCotizacionDetalle ? 2 : 0)
        + ((puedeABMDetalle || puedeEliminarDetalle) ? 1 : 0);
%>
