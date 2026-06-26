<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.servlet.SessionMessages" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<%
RequerimientoCompra reqPresupuestos =
        (RequerimientoCompra) renderRequest.getAttribute(
                WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION
        );

if (reqPresupuestos == null) {
    reqPresupuestos =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW
            );
}

if (reqPresupuestos == null) {
    reqPresupuestos =
            new RequerimientoCompra();
}

int idRequerimientoCompraPresupuestos =
        reqPresupuestos.getIdRequerimientoCompra();

if (idRequerimientoCompraPresupuestos <= 0) {
    idRequerimientoCompraPresupuestos =
            ParamUtil.getInteger(
                    renderRequest,
                    "id_requerimiento_compra",
                    0
            );
}

Object soloLecturaAttrPresupuestos =
        renderRequest.getAttribute(
                WebKeysCompras.SOLO_LECTURA_ATTR
        );

String modoPresupuestos =
        ParamUtil.getString(
                renderRequest,
                "modo",
                ""
        );

String strutsActionPresupuestos =
        ParamUtil.getString(
                renderRequest,
                "struts_action",
                ""
        );

boolean soloLecturaParamPresupuestos =
        ParamUtil.getBoolean(
                request,
                "solo_lectura",
                false
        );

boolean soloLecturaPresupuestos =
        Boolean.TRUE.equals(
                soloLecturaAttrPresupuestos
        )
        || soloLecturaParamPresupuestos
        || "ver".equalsIgnoreCase(
                modoPresupuestos
        )
        || "/compras/ver_requerimiento".equals(
                strutsActionPresupuestos
        );

boolean puedeCotizarPresupuestos =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        );

boolean puedeEditarPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && puedeCotizarPresupuestos
        && reqPresupuestos
                .puedeAdministrarPresupuestos()
        && !soloLecturaPresupuestos;

boolean puedeVerPrestadoresEnviadosPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && reqPresupuestos
                .puedeVerPresupuestos();}

List<PrestadorCotizacion> prestadoresEnviadosPresupuestos =
        new ArrayList<PrestadorCotizacion>();

String errorPrestadoresPresupuestos =
        "";

if (puedeVerPrestadoresEnviadosPresupuestos) {
    try {
        prestadoresEnviadosPresupuestos =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPrestadoresEnviados(
                                idRequerimientoCompraPresupuestos
                        );
    } catch (Exception e) {
        errorPrestadoresPresupuestos =
                e.getMessage() != null
                        ? e.getMessage()
                        : "No se pudieron cargar los prestadores enviados.";
    }
}

boolean hayPrestadoresEnviadosPresupuestos =
        prestadoresEnviadosPresupuestos != null
        && !prestadoresEnviadosPresupuestos.isEmpty();

PortletURL uploadPresupuestosURL =
        renderResponse.createActionURL();

uploadPresupuestosURL.setWindowState(
        WindowState.MAXIMIZED
);

uploadPresupuestosURL.setParameter(
        "struts_action",
        "/compras/upload_presupuestos_requerimiento"
);

String modoRetornoPresupuestos =
        soloLecturaPresupuestos
                ? "ver"
                : "editar";

String msgInsertErrorPresupuestos =
        (String) request.getAttribute(
                "msgInsertError"
        );

if (msgInsertErrorPresupuestos == null) {
    msgInsertErrorPresupuestos =
            "";
}

boolean msgPresupuestoGuardado =
        SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-presupuesto-guardado"
        );

int presupuestosGuardados =
        ParamUtil.getInteger(
                renderRequest,
                "presupuestos_guardados",
                0
        );

boolean msgPresupuestoBorrado =
        SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-presupuesto-borrado"
        );
%>

<form action="<%= uploadPresupuestosURL.toString() %>"
      method="post"
      name="<portlet:namespace />compra_presupuesto_fm"
      id="<portlet:namespace />compra_presupuesto_fm"
      enctype="multipart/form-data">

    <fieldset class="block-labels">
        <legend>Presupuestos</legend>

        <liferay-ui:error
                key="errorUploadFile"
                message="<%= HtmlUtil.escape(msgInsertErrorPresupuestos) %>" />

        <c:if test="<%= msgPresupuestoGuardado %>">
            <div class="portlet-msg-success">
                Se cargaron
                <%= presupuestosGuardados %>
                presupuesto<%= presupuestosGuardados == 1 ? "" : "s" %>
                correctamente.
            </div>
        </c:if>

        <c:if test="<%= msgPresupuestoBorrado %>">
            <div class="portlet-msg-success">
                Presupuesto eliminado correctamente.
            </div>
        </c:if>

        <c:if test="<%= idRequerimientoCompraPresupuestos <= 0 %>">
            <div class="portlet-msg-info">
                Debe guardar y enviar a cotizar el requerimiento
                antes de subir presupuestos.
            </div>
        </c:if>

        <c:if test="<%= puedeVerPrestadoresEnviadosPresupuestos %>">
            <c:choose>
                <c:when test="<%= !WebKeysCompras.isEmpty(errorPrestadoresPresupuestos) %>">
                    <div class="portlet-msg-error">
                        <%= HtmlUtil.escape(errorPrestadoresPresupuestos) %>
                    </div>
                </c:when>

                <c:when test="<%= !hayPrestadoresEnviadosPresupuestos %>">
                    <div class="portlet-msg-info">
                        No hay prestadores notificados correctamente
                        para este requerimiento.
                    </div>
                </c:when>

                <c:otherwise>
                    <table class="lfr-table taglib-search-iterator"
                           style="margin-bottom: 12px; width: 100%;">
                        <thead>
                            <tr>
                                <th>Razón social</th>
                                <th>CUIT</th>
                                <th>Estado de notificación</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                            for (int i = 0;
                                    i < prestadoresEnviadosPresupuestos.size();
                                    i++) {

                                PrestadorCotizacion prestadorEnviado =
                                        prestadoresEnviadosPresupuestos.get(i);

                                if (prestadorEnviado == null) {
                                    continue;
                                }
                            %>
                                <tr>
                                    <td>
                                        <%= HtmlUtil.escape(
                                                prestadorEnviado
                                                        .getDescripcionVisible()
                                        ) %>
                                    </td>
                                    <td>
                                        <%= HtmlUtil.escape(
                                                prestadorEnviado
                                                        .getCuitVisible()
                                        ) %>
                                    </td>
                                    <td>
                                        <%= HtmlUtil.escape(
                                                prestadorEnviado
                                                        .getEstadoEnvioVisible()
                                        ) %>
                                    </td>
                                </tr>
                            <%
                            }
                            %>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </c:if>

        <c:if test="<%=
                puedeEditarPresupuestos
                && WebKeysCompras.isEmpty(
                        errorPrestadoresPresupuestos
                )
                && hayPrestadoresEnviadosPresupuestos
        %>">

            <!-- tabla de archivos -->
            <!-- select prestador_presupuesto_template -->
            <!-- botón Subir -->
            <!-- botón Agregar otro presupuesto -->

        </c:if>
    </fieldset>

    <input type="hidden"
           name="<portlet:namespace />presupuesto_accion"
           id="<portlet:namespace />presupuesto_accion"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />presupuesto_count"
           id="<portlet:namespace />presupuesto_count"
           value="0" />

    <input type="hidden"
           name="<portlet:namespace />id_requerimiento_compra"
           id="<portlet:namespace />id_requerimiento_compra_presupuesto"
           value="<%= idRequerimientoCompraPresupuestos %>" />

    <input type="hidden"
           name="<portlet:namespace />id_requerimiento_presupuesto"
           id="<portlet:namespace />id_requerimiento_presupuesto"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />modo"
           id="<portlet:namespace />modo_presupuesto"
           value="<%= HtmlUtil.escape(modoRetornoPresupuestos) %>" />

    <div id="<portlet:namespace />listado_presupuestos_requerimiento">
        <jsp:include
                page="/html/portlet/compras/requerimientos/requerimiento_adjuntos_search_documentos.jsp" />
    </div>
</form>

<script type="text/javascript">
    function <portlet:namespace />reindexarFilasPresupuesto() {
        var rows =
                jQuery(
                        '#<portlet:namespace />presupuestos_body tr'
                );

        rows.each(function(index) {
            var row =
                    jQuery(this);

            var prestador =
                    row.find(
                            'select.presupuesto-prestador'
                    );

            var archivo =
                    row.find(
                            'input.presupuesto-archivo'
                    );

            prestador.attr(
                    'name',
                    '<portlet:namespace />presupuesto_'
                            + index
                            + '_id_prestador'
            );

            prestador.attr(
                    'id',
                    '<portlet:namespace />presupuesto_'
                            + index
                            + '_id_prestador'
            );

            archivo.attr(
                    'name',
                    'presupuesto_'
                            + index
            );

            archivo.attr(
                    'id',
                    '<portlet:namespace />presupuesto_'
                            + index
            );
        });

        jQuery(
                '#<portlet:namespace />presupuesto_count'
        ).val(
                rows.length
        );
    }

    function <portlet:namespace />agregarFilaPresupuesto() {
        var tbody =
                jQuery(
                        '#<portlet:namespace />presupuestos_body'
                );

        if (tbody.length == 0) {
            return false;
        }

        var cantidad =
                tbody.find(
                        'tr'
                ).length;

        if (cantidad >= <%= WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA %>) {
            alert(
                    'Se pueden cargar hasta '
                            + '<%= WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA %>'
                            + ' presupuestos por operación.'
            );

            return false;
        }

        var prestador =
                jQuery(
                        '#<portlet:namespace />prestador_presupuesto_template'
                ).clone();

        prestador.removeAttr(
                'id'
        );

        prestador.removeAttr(
                'style'
        );

        prestador.addClass(
                'presupuesto-prestador'
        );

        var archivo =
                jQuery(
                        '<input type="file" class="presupuesto-archivo" />'
                );

        var borrar =
                jQuery(
                        '<input type="button" value="Borrar" />'
                );

        borrar.click(function() {
            jQuery(this)
                    .closest(
                            'tr'
                    )
                    .remove();

            <portlet:namespace />reindexarFilasPresupuesto();
        });

        var row =
                jQuery(
                        '<tr></tr>'
                );

        row.append(
                jQuery(
                        '<td></td>'
                ).append(
                        prestador
                )
        );

        row.append(
                jQuery(
                        '<td></td>'
                ).append(
                        archivo
                )
        );

        row.append(
                jQuery(
                        '<td></td>'
                ).append(
                        borrar
                )
        );

        tbody.append(
                row
        );

        <portlet:namespace />reindexarFilasPresupuesto();

        return false;
    }

    function <portlet:namespace />uploadPresupuestoRequerimientoCompra() {
        var form =
                document.getElementById(
                        '<portlet:namespace />compra_presupuesto_fm'
                );

        var accion =
                document.getElementById(
                        '<portlet:namespace />presupuesto_accion'
                );

        var idPresupuesto =
                document.getElementById(
                        '<portlet:namespace />id_requerimiento_presupuesto'
                );

        if (!form
                || !accion
                || !idPresupuesto) {

            alert(
                    'No se pudo preparar la subida del presupuesto.'
            );

            return false;
        }

        var rows =
                jQuery(
                        '#<portlet:namespace />presupuestos_body tr'
                );

        if (rows.length <= 0
                || rows.length
                        > <%= WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA %>) {

            alert(
                    'La cantidad de presupuestos no es válida.'
            );

            return false;
        }

        var valido =
                true;

        rows.each(function(index) {
            var row =
                    jQuery(this);

            var prestador =
                    jQuery.trim(
                            row.find(
                                    'select.presupuesto-prestador'
                            ).val()
                    );

            var archivo =
                    row.find(
                            'input.presupuesto-archivo'
                    );

            if (prestador == '') {
                alert(
                        'Debe seleccionar el prestador del presupuesto '
                                + (index + 1)
                                + '.'
                );

                valido =
                        false;

                return false;
            }

            if (archivo.length == 0
                    || archivo.val() == '') {

                alert(
                        'Debe seleccionar el archivo del presupuesto '
                                + (index + 1)
                                + '.'
                );

                valido =
                        false;

                return false;
            }
        });

        if (!valido) {
            return false;
        }

        accion.value =
                '<%= Constants.ADD %>';

        idPresupuesto.value =
                '';

        <portlet:namespace />reindexarFilasPresupuesto();

        form.submit();

        return false;
    }

    function <portlet:namespace />deletePresupuestoRequerimientoCompra(
            idRequerimientoPresupuestoValue) {

        var form =
                document.getElementById(
                        '<portlet:namespace />compra_presupuesto_fm'
                );

        var accion =
                document.getElementById(
                        '<portlet:namespace />presupuesto_accion'
                );

        var idPresupuesto =
                document.getElementById(
                        '<portlet:namespace />id_requerimiento_presupuesto'
                );

        var idNumerico =
                parseInt(
                        idRequerimientoPresupuestoValue,
                        10
                );

        if (!form
                || !accion
                || !idPresupuesto
                || isNaN(idNumerico)
                || idNumerico <= 0) {

            alert(
                    'No se pudo preparar la eliminación del presupuesto.'
            );

            return false;
        }

        if (!confirm(
                '¿Está seguro de eliminar este presupuesto?'
        )) {
            return false;
        }

        accion.value =
                '<%= Constants.DELETE %>';

        idPresupuesto.value =
                String(
                        idNumerico
                );

        form.submit();

        return false;
    }

    jQuery(function() {
        <% if (puedeEditarPresupuestos
                && hayPrestadoresEnviadosPresupuestos
                && WebKeysCompras.isEmpty(
                        errorPrestadoresPresupuestos
                )) { %>

            <portlet:namespace />agregarFilaPresupuesto();
        <% } %>
    });
</script>
