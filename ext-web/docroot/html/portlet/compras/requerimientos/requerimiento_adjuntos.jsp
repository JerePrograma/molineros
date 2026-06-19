<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.servlet.SessionMessages" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.WindowState" %>



<%
RequerimientoCompra reqPresupuestos =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (reqPresupuestos == null) {
    reqPresupuestos =
            (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
}

if (reqPresupuestos == null) {
    reqPresupuestos = new RequerimientoCompra();
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
        renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR);

String modoPresupuestos = ParamUtil.getString(renderRequest, "modo", "");
String strutsActionPresupuestos = ParamUtil.getString(renderRequest, "struts_action", "");

boolean soloLecturaPresupuestos =
        Boolean.TRUE.equals(soloLecturaAttrPresupuestos)
        || "ver".equalsIgnoreCase(modoPresupuestos)
        || "/compras/ver_requerimiento".equals(strutsActionPresupuestos);

boolean puedeCotizarPresupuestos =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS);

boolean puedeEditarPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && puedeCotizarPresupuestos
        && reqPresupuestos.puedeAdministrarPresupuestos()
        && !soloLecturaPresupuestos;

List<TercerizadoraServicio> tercerizadorasPresupuestos =
        TraeListasServiceUtil.getTercerizadoraServicio(
                renderRequest
        );

if (tercerizadorasPresupuestos == null) {
    tercerizadorasPresupuestos =
            new ArrayList<TercerizadoraServicio>();
}

String idTercerizadoraCotizacionPresupuestos =
        ParamUtil.getString(
                renderRequest,
                "cotizacion_id_tercerizadora",
                reqPresupuestos.getIdTercerizadora()
        );

PortletURL uploadPresupuestosURL = renderResponse.createActionURL();
uploadPresupuestosURL.setWindowState(WindowState.MAXIMIZED);
uploadPresupuestosURL.setParameter("struts_action", "/compras/upload_presupuestos_requerimiento");

String modoRetornoPresupuestos = soloLecturaPresupuestos ? "ver" : "";

String msgInsertErrorPresupuestos =
        (String) request.getAttribute("msgInsertError");

if (msgInsertErrorPresupuestos == null) {
    msgInsertErrorPresupuestos = "";
}

boolean msgPresupuestoGuardado =
        SessionMessages.contains(
                renderRequest,
                "requerimiento-compra-presupuesto-guardado"
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

        <liferay-ui:error key="errorUploadFile"
                          message="<%= HtmlUtil.escape(msgInsertErrorPresupuestos) %>" />

        <c:if test="<%= msgPresupuestoGuardado %>">
            <div class="portlet-msg-success">
                Presupuesto guardado correctamente.
            </div>
        </c:if>

        <c:if test="<%= msgPresupuestoBorrado %>">
            <div class="portlet-msg-success">
                Presupuesto eliminado correctamente.
            </div>
        </c:if>

        <c:if test="<%= idRequerimientoCompraPresupuestos <= 0 %>">
            <div class="portlet-msg-info">
                Debe guardar y enviar a cotizar el requerimiento antes de subir presupuestos.
            </div>
        </c:if>

        <c:if test="<%= puedeEditarPresupuestos %>">
            <table class="lfr-table">
                <tr>
                    <td>
                        <label for="<portlet:namespace />cotizacion_id_tercerizadora">
                            Tercerizadora:
                        </label>
                    </td>

                    <td colspan="5">
                        <select id="<portlet:namespace />cotizacion_id_tercerizadora"
                                name="<portlet:namespace />cotizacion_id_tercerizadora">
                            <option value="">Seleccione...</option>
                            <%
                            for (int i = 0; i < tercerizadorasPresupuestos.size(); i++) {
                                TercerizadoraServicio tercerizadoraPresupuesto =
                                        tercerizadorasPresupuestos.get(i);

                                String idTercerizadoraPresupuesto =
                                        tercerizadoraPresupuesto != null
                                                ? tercerizadoraPresupuesto.getId_tercerizadora()
                                                : "";

                                String descripcionTercerizadoraPresupuesto =
                                        tercerizadoraPresupuesto != null
                                                ? tercerizadoraPresupuesto.getDescripcion()
                                                : "";

                                if (WebKeysCompras.isEmpty(
                                        idTercerizadoraPresupuesto
                                )) {
                                    continue;
                                }
                            %>
                                <option value="<%= HtmlUtil.escape(
                                        idTercerizadoraPresupuesto
                                ) %>"
                                        <%= idTercerizadoraPresupuesto.equalsIgnoreCase(
                                                idTercerizadoraCotizacionPresupuestos
                                        ) ? "selected" : "" %>>
                                    <%= HtmlUtil.escape(
                                            descripcionTercerizadoraPresupuesto
                                    ) %>
                                </option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>

                <tr>
                    <td>Añadir presupuesto:</td>
                    <td>
                        <input type="file"
                               name="presupuesto"
                               id="<portlet:namespace />presupuesto" />
                    </td>

                    <td>&nbsp;</td>

                    <td>
                        <label>Descripción:</label>
                    </td>

                    <td>
                        <input id="<portlet:namespace />descripcionFile"
                               name="<portlet:namespace />descripcionFile"
                               size="90"
                               maxlength="120"
                               type="text"
                               value="" />
                    </td>

                    <td>
                        <input id="<portlet:namespace />uploadPresupuestoCompra"
                               value="Subir presupuesto"
                               title="Subir presupuesto"
                               onclick="return <portlet:namespace />uploadPresupuestoRequerimientoCompra();"
                               type="button" />
                    </td>
                </tr>
            </table>
        </c:if>

        <c:if test="<%= idRequerimientoCompraPresupuestos > 0 && !puedeEditarPresupuestos && !soloLecturaPresupuestos %>">
            <div class="portlet-msg-info">
                Los presupuestos solo pueden administrarse en estado A cotizar.
            </div>
        </c:if>
    </fieldset>

    <input type="hidden"
           name="<portlet:namespace />presupuesto_accion"
           id="<portlet:namespace />presupuesto_accion"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />id_requerimiento_compra"
           id="<portlet:namespace />id_requerimiento_compra_presupuesto"
           value="<%= idRequerimientoCompraPresupuestos %>" />

    <input type="hidden"
           name="<portlet:namespace />folderid"
           id="<portlet:namespace />folderid"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />filename"
           id="<portlet:namespace />filename"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />filetitle"
           id="<portlet:namespace />filetitle"
           value="" />

    <input type="hidden"
           name="<portlet:namespace />modo"
           id="<portlet:namespace />modo_presupuesto"
           value="<%= HtmlUtil.escape(modoRetornoPresupuestos) %>" />

    <div id="<portlet:namespace />listado_presupuestos_requerimiento">
        <jsp:include page="/html/portlet/compras/requerimientos/requerimiento_adjuntos_search_documentos.jsp" />
    </div>
</form>

<script type="text/javascript">
    function <portlet:namespace />uploadPresupuestoRequerimientoCompra() {
        var form =
                document.getElementById(
                        '<portlet:namespace />compra_presupuesto_fm'
                );

        var file =
                document.getElementById(
                        '<portlet:namespace />presupuesto'
                );

        var tercerizadora =
                document.getElementById(
                        '<portlet:namespace />cotizacion_id_tercerizadora'
                );

        var accion =
                document.getElementById(
                        '<portlet:namespace />presupuesto_accion'
                );

        var folderId =
                document.getElementById(
                        '<portlet:namespace />folderid'
                );

        var filename =
                document.getElementById(
                        '<portlet:namespace />filename'
                );

        var filetitle =
                document.getElementById(
                        '<portlet:namespace />filetitle'
                );

        if (!form) {
            alert(
                    'No se encontró el formulario de presupuestos.'
            );

            return false;
        }

        if (!file || file.value == '') {
            alert(
                    'Debe seleccionar un presupuesto.'
            );

            return false;
        }

        if (!tercerizadora
                || jQuery.trim(tercerizadora.value) == '') {

            alert(
                    'Debe seleccionar la tercerizadora de la cotizacion.'
            );

            return false;
        }

        if (!accion
                || !folderId
                || !filename
                || !filetitle) {

            alert(
                    'No se pudo preparar la subida del presupuesto.'
            );

            return false;
        }

        accion.value =
                '<%= Constants.ADD %>';

        folderId.value = '';
        filename.value = '';
        filetitle.value = '';

        form.submit();

        return false;
    }

    function <portlet:namespace />deletePresupuestoRequerimientoCompra(
            folderIdValue,
            filenameValue,
            filetitleValue) {

        var form =
                document.getElementById(
                        '<portlet:namespace />compra_presupuesto_fm'
                );

        var accion =
                document.getElementById(
                        '<portlet:namespace />presupuesto_accion'
                );

        var folderId =
                document.getElementById(
                        '<portlet:namespace />folderid'
                );

        var filename =
                document.getElementById(
                        '<portlet:namespace />filename'
                );

        var filetitle =
                document.getElementById(
                        '<portlet:namespace />filetitle'
                );

        if (!form) {
            alert(
                    'No se encontró el formulario de presupuestos.'
            );

            return false;
        }

        if (!accion
                || !folderId
                || !filename
                || !filetitle) {

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

        folderId.value =
                folderIdValue;

        filename.value =
                filenameValue;

        filetitle.value =
                filetitleValue;

        form.submit();

        return false;
    }
</script>
