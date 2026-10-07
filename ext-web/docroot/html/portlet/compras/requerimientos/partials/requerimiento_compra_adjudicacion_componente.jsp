<%--
Responsabilidad:
    Renderiza la selección y resumen del prestador o Empresa adjudicada.
Incluido desde:
    requerimiento_compra_adjudicacion_runtime_componente.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    id_prestador_adjudicado, capturarPrestadorAdjudicado, empresa_adjudicada_selector,
    capturarEmpresaAdjudicada, actualizarEmpresasAdjudicacion
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
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

Object puedeCotizarEmpresasAttrAdjudicacion =
        request.getAttribute("compras.requerimiento.puedeCotizar");
boolean rolCotizarEmpresasAdjudicacion =
        puedeCotizarEmpresasAttrAdjudicacion instanceof Boolean
                ? Boolean.TRUE.equals(puedeCotizarEmpresasAttrAdjudicacion)
                : user != null && PermissionUtil.userContainsRole(
                        user, WebKeysCompras.ROL_COTIZAR_COMPRAS);
boolean altaEmpresaAdjudicacion =
        !requerimientoPersistidoAdjudicacion
        && Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.esNuevo"))
        && Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.puedeABM"))
        && rolCotizarEmpresasAdjudicacion && !soloLecturaAdjudicacion;
boolean cotizacionEmpresaAdjudicacion =
        altaEmpresaAdjudicacion || reqAdjudicacion.esSectorSinCotizacionPrestador();
boolean puedeEditarEmpresaAdjudicacion = cotizacionEmpresaAdjudicacion
        && (altaEmpresaAdjudicacion || (requerimientoPersistidoAdjudicacion
                && rolCotizarEmpresasAdjudicacion
                && reqAdjudicacion.puedeAdministrarPresupuestos()
                && !soloLecturaAdjudicacion));
boolean restaurarEmpresaAdjudicacion = puedeEditarEmpresaAdjudicacion
        && ParamUtil.getBoolean(renderRequest, "compras_error", false)
        && "1".equals(ParamUtil.getString(renderRequest, "empresa_adjudicacion_informada", ""));
String empresaAdjudicadaCuit = restaurarEmpresaAdjudicacion
        ? ParamUtil.getString(renderRequest, "empresa_adjudicada_cuit", "") : "";
String empresaAdjudicadaSucursal = restaurarEmpresaAdjudicacion
        ? ParamUtil.getString(renderRequest, "empresa_adjudicada_sucursal", "") : "";
int empresaAdjudicadaId = restaurarEmpresaAdjudicacion
        ? ParamUtil.getInteger(renderRequest, "empresa_adjudicada_id", 0) : 0;
int empresaAdjudicadaIndice = restaurarEmpresaAdjudicacion
        ? ParamUtil.getInteger(renderRequest, "empresa_adjudicada_indice", -1) : -1;
String empresaAdjudicadaVisible = "";
List<RequerimientoCompraPresupuesto> empresasAdjudicacion =
        new ArrayList<RequerimientoCompraPresupuesto>();
List<RequerimientoCompraPresupuesto> presupuestosAdjudicacion =
        (List<RequerimientoCompraPresupuesto>) request.getAttribute("compras.requerimiento.presupuestos");
if (cotizacionEmpresaAdjudicacion && requerimientoPersistidoAdjudicacion
        && presupuestosAdjudicacion != null) {
    for (RequerimientoCompraPresupuesto presupuestoAdjudicacion : presupuestosAdjudicacion) {
        if (presupuestoAdjudicacion == null || !presupuestoAdjudicacion.isActivo()
                || !presupuestoAdjudicacion.isCotizacionEmpresa()
                || WebKeysCompras.isEmpty(presupuestoAdjudicacion.getDescripcionEmpresa())
                || presupuestoAdjudicacion.getIdRequerimiento() == null
                || presupuestoAdjudicacion.getIdRequerimiento().intValue() != idRequerimientoAdjudicacion) {
            continue;
        }
        empresasAdjudicacion.add(presupuestoAdjudicacion);
        if (!restaurarEmpresaAdjudicacion && presupuestoAdjudicacion.isEmpresaAdjudicada()) {
            empresaAdjudicadaId = presupuestoAdjudicacion.getIdRequerimientoPresupuesto();
            empresaAdjudicadaCuit = presupuestoAdjudicacion.getEmpresaCuit();
            empresaAdjudicadaSucursal = presupuestoAdjudicacion.getEmpresaSucursal();
        }
    }
    for (RequerimientoCompraPresupuesto empresaAdjudicacion : empresasAdjudicacion) {
        if (empresaAdjudicacion.getIdRequerimientoPresupuesto().intValue() == empresaAdjudicadaId) {
            empresaAdjudicadaVisible = empresaAdjudicacion.getDescripcionEmpresa()
                    + (WebKeysCompras.isEmpty(empresaAdjudicacion.getEmpresaCuit()) ? ""
                        : " - CUIT: " + empresaAdjudicacion.getEmpresaCuit())
                    + (WebKeysCompras.isEmpty(empresaAdjudicacion.getEmpresaSucursal()) ? ""
                        : " - Sucursal: " + empresaAdjudicacion.getEmpresaSucursal());
            break;
        }
    }
    if (WebKeysCompras.isEmpty(empresaAdjudicadaVisible)) {
        empresaAdjudicadaId = 0;
        empresaAdjudicadaCuit = "";
        empresaAdjudicadaSucursal = "";
    }
}
PortletURL guardarEmpresaAdjudicadaURL = renderResponse.createActionURL();
guardarEmpresaAdjudicadaURL.setWindowState(WindowState.MAXIMIZED);
guardarEmpresaAdjudicadaURL.setParameter("struts_action", "/compras/editar_requerimiento");

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

<% if (puedeVerCotizacionAdjudicacion || cotizacionEmpresaAdjudicacion) { %>
<div class="compras-seccion compras-seccion-adjudicacion"
        <% if (cotizacionEmpresaAdjudicacion) { %>
        id="<portlet:namespace />adjudicacion_empresa_panel"
        <%= altaEmpresaAdjudicacion && !reqAdjudicacion.esSectorSinCotizacionPrestador()
                ? "style=\"display: none;\"" : "" %>
        <% } %>>

    <% if (!cotizacionEmpresaAdjudicacion) { %>

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

    <% } %>

    <fieldset class="block-labels compras-adjudicacion">
        <legend>Adjudicación</legend>

        <table class="lfr-table">
            <tr>
                <td>
                    <label for="<portlet:namespace /><%= cotizacionEmpresaAdjudicacion
                            ? "empresa_adjudicada_selector" : "id_prestador_adjudicado" %>">
                        <%= cotizacionEmpresaAdjudicacion ? "Empresa adjudicada:" : "Prestador adjudicado:" %>
                    </label>
                </td>

                <td>
                    <% if (cotizacionEmpresaAdjudicacion) { %>
                        <% if (puedeEditarEmpresaAdjudicacion) { %>
                            <select id="<portlet:namespace />empresa_adjudicada_selector"
                                    style="max-width: 520px; width: 100%;"
                                    onchange="<portlet:namespace />capturarEmpresaAdjudicada();"
                                    <%= empresasAdjudicacion.isEmpty() ? "disabled=\"disabled\"" : "" %>>
                                <option value="">Seleccione...</option>
                                <% for (int i = 0; i < empresasAdjudicacion.size(); i++) {
                                    RequerimientoCompraPresupuesto empresaAdjudicacion = empresasAdjudicacion.get(i);
                                %>
                                    <option value="<%= empresaAdjudicacion.getIdRequerimientoPresupuesto() %>"
                                            data-cuit="<%= HtmlUtil.escape(empresaAdjudicacion.getEmpresaCuit()) %>"
                                            data-sucursal="<%= HtmlUtil.escape(empresaAdjudicacion.getEmpresaSucursal()) %>"
                                            <%= empresaAdjudicacion.getIdRequerimientoPresupuesto().intValue() == empresaAdjudicadaId
                                                    ? "selected=\"selected\"" : "" %>>
                                        <%= HtmlUtil.escape(empresaAdjudicacion.getDescripcionEmpresa()
                                                + (WebKeysCompras.isEmpty(empresaAdjudicacion.getEmpresaCuit()) ? ""
                                                    : " - CUIT: " + empresaAdjudicacion.getEmpresaCuit())
                                                + (WebKeysCompras.isEmpty(empresaAdjudicacion.getEmpresaSucursal()) ? ""
                                                    : " - Sucursal: " + empresaAdjudicacion.getEmpresaSucursal())) %>
                                    </option>
                                <% } %>
                            </select>
                            <input type="hidden" name="<portlet:namespace />empresa_adjudicada_id"
                                   id="<portlet:namespace />empresa_adjudicada_id" value="<%= empresaAdjudicadaId %>" />
                            <input type="hidden" name="<portlet:namespace />empresa_adjudicada_indice"
                                   id="<portlet:namespace />empresa_adjudicada_indice" value="<%= empresaAdjudicadaIndice %>" />
                            <input type="hidden" name="<portlet:namespace />empresa_adjudicada_cuit"
                                   id="<portlet:namespace />empresa_adjudicada_cuit" value="<%= HtmlUtil.escape(empresaAdjudicadaCuit) %>" />
                            <input type="hidden" name="<portlet:namespace />empresa_adjudicada_sucursal"
                                   id="<portlet:namespace />empresa_adjudicada_sucursal" value="<%= HtmlUtil.escape(empresaAdjudicadaSucursal) %>" />
                            <input type="hidden" name="<portlet:namespace />empresa_adjudicacion_informada"
                                   id="<portlet:namespace />empresa_adjudicacion_informada" value="1" />
                        <% } else { %>
                            <strong><%= WebKeysCompras.isEmpty(empresaAdjudicadaVisible)
                                    ? "Sin adjudicar" : HtmlUtil.escape(empresaAdjudicadaVisible) %></strong>
                        <% } %>
                    <% } else if (puedeCotizarAdjudicacion) { %>
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
        <% if (puedeEditarEmpresaAdjudicacion && requerimientoPersistidoAdjudicacion) { %>
            <form action="<%= guardarEmpresaAdjudicadaURL.toString() %>" method="post"
                  id="<portlet:namespace />empresa_adjudicacion_fm">
                <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="saveEmpresaAdjudicada" />
                <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= idRequerimientoAdjudicacion %>" />
                <input type="hidden" name="<portlet:namespace />compras_save_token"
                       value="<%= HtmlUtil.escape((String) renderRequest.getAttribute("COMPRAS_SAVE_TOKEN")) %>" />
                <input type="button" value="Guardar adjudicaci&#243;n"
                       onclick="return <portlet:namespace />guardarEmpresaAdjudicada(this);" />
            </form>
        <% } %>
    </fieldset>
</div>
<% } %>

<% if (puedeEditarEmpresaAdjudicacion) { %>
<script type="text/javascript">
    function <portlet:namespace />capturarEmpresaAdjudicada() {
        var opcion = jQuery('#<portlet:namespace />empresa_adjudicada_selector option:selected');
        var habilitada = opcion.length > 0 && !opcion.attr('disabled') && opcion.val() != '';
        <% if (altaEmpresaAdjudicacion) { %>
        jQuery('#<portlet:namespace />empresa_adjudicada_indice').val(habilitada ? opcion.val() : '-1');
        <% } else { %>
        jQuery('#<portlet:namespace />empresa_adjudicada_id').val(habilitada ? opcion.val() : '0');
        <% } %>
        jQuery('#<portlet:namespace />empresa_adjudicada_cuit').val(habilitada ? (opcion.attr('data-cuit') || '') : '');
        jQuery('#<portlet:namespace />empresa_adjudicada_sucursal').val(habilitada ? (opcion.attr('data-sucursal') || '') : '');
    }

    function <portlet:namespace />actualizarEmpresasAdjudicacion() {
        <% if (altaEmpresaAdjudicacion) { %>
        var selector = jQuery('#<portlet:namespace />empresa_adjudicada_selector');
        var indice = jQuery('#<portlet:namespace />empresa_adjudicada_indice').val();
        var encontrada = false;
        selector.empty().append(jQuery('<option></option>').val('').text('Seleccione...'));
        jQuery('#<portlet:namespace />presupuestos_body tr').each(function(i) {
            var fila = jQuery(this);
            var cuitFila = fila.find('input.presupuesto-empresa-cuit').val() || '';
            var sucursalFila = fila.find('input.presupuesto-empresa-sucursal').val() || '';
            var nombreFila = jQuery.trim(fila.find('input.presupuesto-empresa-descripcion').val() || '');
            if (!nombreFila) { return; }
            var tieneArchivo = !!fila.find('input.presupuesto-archivo').val();
            var opcion = jQuery('<option></option>').val(String(i))
                    .attr('data-cuit', cuitFila).attr('data-sucursal', sucursalFila)
                    .text(nombreFila + (cuitFila ? ' - CUIT: ' + cuitFila : '')
                            + (tieneArchivo ? '' : ' (sin presupuesto cargado)'));
            if (!tieneArchivo) { opcion.attr('disabled', 'disabled'); }
            selector.append(opcion);
            if (String(i) == indice) {
                // Conservar la eleccion tras un error; el PDF debe volver a seleccionarse.
                opcion.attr('selected', 'selected');
                encontrada = true;
            }
        });
        if (!encontrada) {
            selector.val('');
            jQuery('#<portlet:namespace />empresa_adjudicada_indice').val('-1');
            jQuery('#<portlet:namespace />empresa_adjudicada_cuit').val('');
            jQuery('#<portlet:namespace />empresa_adjudicada_sucursal').val('');
        }
        if (selector.find('option').length > 1
                && <portlet:namespace />esSectorSinCotizacionPrestadorCompra()) {
            selector.removeAttr('disabled');
        } else {
            selector.attr('disabled', 'disabled');
        }
        <% } %>
    }
</script>
<% } %>
