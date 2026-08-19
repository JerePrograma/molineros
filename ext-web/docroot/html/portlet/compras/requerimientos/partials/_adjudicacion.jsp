<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>

<%
RequerimientoCompra reqAdjudicacion =
        (RequerimientoCompra) request.getAttribute(
                "compras.requerimiento.req"
        );

if (reqAdjudicacion == null) {
    reqAdjudicacion =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION
            );
}

if (reqAdjudicacion == null) {
    reqAdjudicacion =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW
            );
}

if (reqAdjudicacion == null) {
    reqAdjudicacion = new RequerimientoCompra();
}

Object soloLecturaAttrAdjudicacion =
        renderRequest.getAttribute(
                WebKeysCompras.SOLO_LECTURA_ATTR
        );

String strutsActionAdjudicacion =
        ParamUtil.getString(
                renderRequest,
                "struts_action",
                ""
        );

String modoAdjudicacion =
        ParamUtil.getString(
                renderRequest,
                "modo",
                ""
        );

boolean soloLecturaAdjudicacion =
        Boolean.TRUE.equals(soloLecturaAttrAdjudicacion)
        || ParamUtil.getBoolean(
                request,
                "solo_lectura",
                false
        )
        || "/compras/ver_requerimiento".equals(
                strutsActionAdjudicacion
        )
        || "ver".equalsIgnoreCase(modoAdjudicacion);

Object puedeEditarCotizacionAttrAdjudicacion =
        request.getAttribute(
                "compras.requerimiento.puedeEditarCotizacion"
        );

boolean puedeCotizarAdjudicacion =
        puedeEditarCotizacionAttrAdjudicacion instanceof Boolean
                ? Boolean.TRUE.equals(
                        puedeEditarCotizacionAttrAdjudicacion
                )
                : user != null
                        && PermissionUtil.userContainsRole(
                                user,
                                WebKeysCompras.ROL_COTIZAR_COMPRAS
                        )
                        && reqAdjudicacion.puedeEditarCotizacion()
                        && !soloLecturaAdjudicacion;

boolean puedeVerCotizacionAdjudicacion =
        reqAdjudicacion.puedeVerCotizacion();

int idRequerimientoAdjudicacion =
        reqAdjudicacion.getIdRequerimientoCompra();

boolean requerimientoPersistidoAdjudicacion =
        idRequerimientoAdjudicacion > 0;

boolean restaurarCotizacionAdjudicacion =
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

String idPrestadorAdjudicadoAdjudicacion =
        restaurarCotizacionAdjudicacion
                ? ParamUtil.getString(
                        renderRequest,
                        WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                        ""
                )
                : reqAdjudicacion.getIdPrestadorAdjudicadoString();

String prestadorAdjudicadoAdjudicacion =
        reqAdjudicacion.getPrestadorAdjudicadoVisible();

List<PrestadorCotizacion> prestadoresEnviadosAdjudicacion =
        (List<PrestadorCotizacion>) request.getAttribute(
                "compras.requerimiento.prestadoresEnviados"
        );

if (prestadoresEnviadosAdjudicacion == null) {
    prestadoresEnviadosAdjudicacion =
            new ArrayList<PrestadorCotizacion>();
}

String errorPrestadoresAdjudicacion =
        (String) request.getAttribute(
                "compras.requerimiento.errorPrestadoresEnviados"
        );

if (errorPrestadoresAdjudicacion == null) {
    errorPrestadoresAdjudicacion = "";
}

boolean hayPrestadoresEnviadosAdjudicacion =
        prestadoresEnviadosAdjudicacion != null
        && !prestadoresEnviadosAdjudicacion.isEmpty();

Set<Integer> idsPrestadoresConPresupuestoAdjudicacion =
        (Set<Integer>) request.getAttribute(
                "compras.requerimiento.idsPrestadoresConPresupuesto"
        );

if (idsPrestadoresConPresupuestoAdjudicacion == null) {
    idsPrestadoresConPresupuestoAdjudicacion =
            new HashSet<Integer>();
}

String errorPresupuestosAdjudicacion =
        (String) request.getAttribute(
                "compras.requerimiento.errorPresupuestos"
        );

if (errorPresupuestosAdjudicacion == null) {
    errorPresupuestosAdjudicacion = "";
}

Set<Integer> idsPrestadoresHabilitadosAdjudicacion =
        new HashSet<Integer>();

for (int i = 0;
        prestadoresEnviadosAdjudicacion != null
        && i < prestadoresEnviadosAdjudicacion.size();
        i++) {

    PrestadorCotizacion prestadorAdjudicacion =
            prestadoresEnviadosAdjudicacion.get(i);

    if (prestadorAdjudicacion != null
            && prestadorAdjudicacion.getIdPrestador() > 0
            && idsPrestadoresConPresupuestoAdjudicacion.contains(
                    Integer.valueOf(
                            prestadorAdjudicacion.getIdPrestador()
                    )
            )) {

        idsPrestadoresHabilitadosAdjudicacion.add(
                Integer.valueOf(
                        prestadorAdjudicacion.getIdPrestador()
                )
        );
    }
}

boolean hayPrestadoresHabilitadosAdjudicacion =
        !idsPrestadoresHabilitadosAdjudicacion.isEmpty();

if (puedeCotizarAdjudicacion
        && !WebKeysCompras.isEmpty(
                idPrestadorAdjudicadoAdjudicacion
        )) {

    try {
        int idPrestadorAdjudicadoActualAdjudicacion =
                Integer.parseInt(
                        idPrestadorAdjudicadoAdjudicacion
                );

        if (!idsPrestadoresHabilitadosAdjudicacion.contains(
                Integer.valueOf(
                        idPrestadorAdjudicadoActualAdjudicacion
                )
        )) {

            idPrestadorAdjudicadoAdjudicacion = "";
            prestadorAdjudicadoAdjudicacion = "";
        }
    } catch (NumberFormatException e) {
        idPrestadorAdjudicadoAdjudicacion = "";
        prestadorAdjudicadoAdjudicacion = "";
    }
}

if (WebKeysCompras.isEmpty(prestadorAdjudicadoAdjudicacion)
        && !WebKeysCompras.isEmpty(
                idPrestadorAdjudicadoAdjudicacion
        )) {

    for (int i = 0;
            i < prestadoresEnviadosAdjudicacion.size();
            i++) {

        PrestadorCotizacion prestadorAdjudicacion =
                prestadoresEnviadosAdjudicacion.get(i);

        if (prestadorAdjudicacion != null
                && String.valueOf(
                        prestadorAdjudicacion.getIdPrestador()
                ).equals(idPrestadorAdjudicadoAdjudicacion)) {

            prestadorAdjudicadoAdjudicacion =
                    prestadorAdjudicacion.getEtiquetaVisible();
            break;
        }
    }
}

boolean prestadoresAdjudicadosMixtosAdjudicacion =
        reqAdjudicacion.tienePrestadoresAdjudicadosMixtos();
%>

<% if (puedeVerCotizacionAdjudicacion) { %>
<div class="compras-seccion compras-seccion-adjudicacion">

    <% if (puedeCotizarAdjudicacion
            && !WebKeysCompras.isEmpty(
                    errorPrestadoresAdjudicacion
            )) { %>

        <div class="portlet-msg-error">
            <%= HtmlUtil.escape(errorPrestadoresAdjudicacion) %>
        </div>

    <% } else if (puedeCotizarAdjudicacion
            && !hayPrestadoresEnviadosAdjudicacion) { %>

        <div class="portlet-msg-info">
            No hay prestadores notificados correctamente
            para este requerimiento.
        </div>

    <% } else if (puedeCotizarAdjudicacion
            && !WebKeysCompras.isEmpty(
                    errorPresupuestosAdjudicacion
            )) { %>

        <div class="portlet-msg-error">
            <%= HtmlUtil.escape(errorPresupuestosAdjudicacion) %>
        </div>

    <% } else if (puedeCotizarAdjudicacion
            && !hayPrestadoresHabilitadosAdjudicacion) { %>

        <div class="portlet-msg-info">
            No hay cotizaciones cargadas para poder seleccionar
            un prestador adjudicado.
        </div>

    <% } %>

    <% if (prestadoresAdjudicadosMixtosAdjudicacion) { %>
        <div class="portlet-msg-error">
            El requerimiento contiene prestadores adjudicados diferentes.
            Seleccione un único prestador antes de guardar la cotización.
        </div>
    <% } %>

    <fieldset class="block-labels compras-adjudicacion">
        <legend>Adjudicación</legend>

        <table class="lfr-table">
            <tr>
                <td>
                    <label for="<portlet:namespace />id_prestador_adjudicado">
                        Prestador adjudicado:
                    </label>
                </td>

                <td>
                    <% if (puedeCotizarAdjudicacion) { %>
                        <select
                                id="<portlet:namespace />id_prestador_adjudicado"
                                style="max-width: 520px; width: 100%;"
                                onchange="<portlet:namespace />capturarPrestadorAdjudicado();"
                                <%= hayPrestadoresHabilitadosAdjudicacion
                                        ? ""
                                        : "disabled=\"disabled\"" %>>

                            <option value="">Seleccione...</option>

                            <%
                            for (int i = 0;
                                    i < prestadoresEnviadosAdjudicacion.size();
                                    i++) {

                                PrestadorCotizacion prestadorAdjudicacion =
                                        prestadoresEnviadosAdjudicacion.get(i);

                                if (prestadorAdjudicacion == null
                                        || prestadorAdjudicacion
                                                .getIdPrestador() <= 0) {
                                    continue;
                                }

                                String idPrestadorAdjudicacion =
                                        String.valueOf(
                                                prestadorAdjudicacion
                                                        .getIdPrestador()
                                        );

                                boolean prestadorHabilitadoAdjudicacion =
                                        idsPrestadoresHabilitadosAdjudicacion
                                                .contains(
                                                        Integer.valueOf(
                                                                prestadorAdjudicacion
                                                                        .getIdPrestador()
                                                        )
                                                );
                            %>
                                <option
                                        value="<%= idPrestadorAdjudicacion %>"
                                        <%= prestadorHabilitadoAdjudicacion
                                                && idPrestadorAdjudicacion.equals(
                                                        idPrestadorAdjudicadoAdjudicacion
                                                )
                                                        ? "selected=\"selected\""
                                                        : "" %>
                                        <%= prestadorHabilitadoAdjudicacion
                                                ? ""
                                                : "disabled=\"disabled\"" %>>

                                    <%= HtmlUtil.escape(
                                            prestadorAdjudicacion
                                                    .getEtiquetaVisible()
                                    ) %><%= prestadorHabilitadoAdjudicacion
                                            ? ""
                                            : " (sin presupuesto cargado)" %>
                                </option>
                            <%
                            }
                            %>
                        </select>
                    <% } else { %>
                        <strong>
                            <%= WebKeysCompras.isEmpty(
                                    prestadorAdjudicadoAdjudicacion
                            )
                                    ? "Sin adjudicar"
                                    : HtmlUtil.escape(
                                            prestadorAdjudicadoAdjudicacion
                                    ) %>
                        </strong>
                    <% } %>
                </td>
            </tr>
        </table>
    </fieldset>
</div>
<% } %>
