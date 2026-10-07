<%--
Responsabilidad:
    Renderiza resultados documentales del requerimiento.
Incluido desde:
    requerimiento_compra_documentos.jsp
Pantallas o estados de uso:
    Búsqueda, selección o popup según el forward indicado.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza presentación; las operaciones se delegan al Action.
--%>
<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/document_library/init.jsp" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.log.Log" %>
<%@ page import="com.liferay.portal.kernel.log.LogFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
Log logPresupuestos =
        LogFactoryUtil.getLog(
                "compras.requerimiento_adjuntos_search_documentos"
        );

String namespaceAdjuntos =
        renderResponse.getNamespace();

RequerimientoCompra reqPresupuestos =
        (RequerimientoCompra) request.getAttribute(
                "compras.requerimiento.req"
        );

if (reqPresupuestos == null) {
    reqPresupuestos =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION
            );
}

if (reqPresupuestos == null) {
    reqPresupuestos =
            (RequerimientoCompra) renderRequest.getAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW
            );
}

boolean cotizacionEmpresaPresupuestos =
        reqPresupuestos != null
        && reqPresupuestos.esSectorSinCotizacionPrestador();

int idRequerimientoCompraPresupuestos =
        reqPresupuestos != null
                ? reqPresupuestos.getIdRequerimientoCompra()
                : 0;

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

boolean soloLecturaPresupuestos =
        Boolean.TRUE.equals(soloLecturaAttrPresupuestos)
        || ParamUtil.getBoolean(
                request,
                "solo_lectura",
                false
        )
        || "ver".equalsIgnoreCase(modoPresupuestos)
        || "/compras/ver_requerimiento".equals(
                strutsActionPresupuestos
        );

Object puedeCotizarAttr =
        request.getAttribute(
                "compras.requerimiento.puedeCotizar"
        );

boolean puedeCotizarPresupuestos =
        puedeCotizarAttr instanceof Boolean
                ? Boolean.TRUE.equals(puedeCotizarAttr)
                : user != null
                        && PermissionUtil.userContainsRole(
                                user,
                                WebKeysCompras.ROL_COTIZAR_COMPRAS
                        );

boolean puedeEditarEmpresaPresupuestos =
        cotizacionEmpresaPresupuestos
        && puedeCotizarPresupuestos
        && reqPresupuestos.isActivo()
        && (reqPresupuestos.isPendiente() || reqPresupuestos.isOrdenCompra())
        && !"ver".equalsIgnoreCase(modoPresupuestos)
        && !"/compras/ver_requerimiento".equals(strutsActionPresupuestos);

boolean puedeEliminarPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && puedeCotizarPresupuestos
        && reqPresupuestos != null
        && reqPresupuestos.puedeAdministrarPresupuestos()
        && !soloLecturaPresupuestos;

PortletURL portletURL =
        renderResponse.createRenderURL();

portletURL.setWindowState(
        LiferayWindowState.MAXIMIZED
);

List<String> headerNames =
        new ArrayList<String>();
headerNames.add("Archivo");
headerNames.add(
        cotizacionEmpresaPresupuestos
                        ? "Empresa"
                        : "Prestador"
);
headerNames.add("Descargar");
headerNames.add("Editar");
headerNames.add("Eliminar");

String mensajeSinResultados =
        idRequerimientoCompraPresupuestos > 0
                ? cotizacionEmpresaPresupuestos
                                ? "No hay cotizaciones de empresas asociadas al requerimiento."
                                : "No hay presupuestos asociados al requerimiento."
                : "No se informó el requerimiento de compra.";

SearchContainer searchContainer =
        new SearchContainer(
                renderRequest,
                null,
                null,
                SearchContainer.DEFAULT_CUR_PARAM,
                SearchContainer.DEFAULT_DELTA,
                portletURL,
                headerNames,
                mensajeSinResultados
        );

List<RequerimientoCompraPresupuesto> presupuestos =
        (List<RequerimientoCompraPresupuesto>) request.getAttribute(
                "compras.requerimiento.presupuestos"
        );

Map<Integer, Boolean> documentosValidosPreparados =
        (Map<Integer, Boolean>) request.getAttribute(
                "compras.requerimiento.presupuestoDocumentoValido"
        );

Map<Integer, String> downloadUrlsPreparadas =
        (Map<Integer, String>) request.getAttribute(
                "compras.requerimiento.presupuestoDownloadURL"
        );

try {
    if (presupuestos == null) {
        presupuestos =
                new ArrayList<RequerimientoCompraPresupuesto>();
    }

    if (documentosValidosPreparados == null) {
        documentosValidosPreparados =
                new HashMap<Integer, Boolean>();
    }

    if (downloadUrlsPreparadas == null) {
        downloadUrlsPreparadas =
                new HashMap<Integer, String>();
    }

    int total = presupuestos.size();
    searchContainer.setTotal(total);

    int inicio = searchContainer.getStart();
    int fin = searchContainer.getEnd();

    if (inicio < 0) {
        inicio = 0;
    }

    if (fin > total) {
        fin = total;
    }

    List resultRows =
            searchContainer.getResultRows();

    for (int i = inicio; i < fin; i++) {
        RequerimientoCompraPresupuesto presupuesto =
                presupuestos.get(i);

        if (presupuesto == null
                || presupuesto.getIdRequerimientoPresupuesto() == null
                || presupuesto
                        .getIdRequerimientoPresupuesto()
                        .intValue() <= 0) {
            continue;
        }

        if (cotizacionEmpresaPresupuestos
                != presupuesto.isCotizacionEmpresa()) {

            continue;
        }

        int idRequerimientoPresupuesto =
                presupuesto
                        .getIdRequerimientoPresupuesto()
                        .intValue();

        ResultRow row =
                new ResultRow(
                        presupuesto,
                        idRequerimientoPresupuesto,
                        i
                );

        row.setObject(presupuesto);

        String archivoVisible =
                presupuesto.getNombreOriginal();

        if (WebKeysCompras.isEmpty(archivoVisible)) {
            archivoVisible = presupuesto.getTitulo();
        }

        if (WebKeysCompras.isEmpty(archivoVisible)) {
            archivoVisible = presupuesto.getNombrePersistido();
        }

        row.addText(
                HtmlUtil.escape(archivoVisible)
        );

        if (cotizacionEmpresaPresupuestos) {
            String nombreEmpresa = WebKeysCompras.isEmpty(presupuesto.getDescripcionEmpresa())
                    ? "" : presupuesto.getDescripcionEmpresa();
            String cuitEmpresa = WebKeysCompras.isEmpty(presupuesto.getEmpresaCuit())
                    ? "" : presupuesto.getEmpresaCuit();
            String sucursalEmpresa = WebKeysCompras.isEmpty(presupuesto.getEmpresaSucursal())
                    ? "" : presupuesto.getEmpresaSucursal();
            String idDatosEmpresa = namespaceAdjuntos + "cotizacion_empresa_datos_"
                    + idRequerimientoPresupuesto;
            String idEdicionEmpresa = namespaceAdjuntos + "cotizacion_empresa_edicion_"
                    + idRequerimientoPresupuesto;
            StringBuilder empresaVisible = new StringBuilder();
            empresaVisible.append("<div id=\"").append(HtmlUtil.escape(idDatosEmpresa)).append("\">");
            empresaVisible.append(HtmlUtil.escape(nombreEmpresa));
            if (!WebKeysCompras.isEmpty(cuitEmpresa)) {
                empresaVisible.append("<br />CUIT: ").append(HtmlUtil.escape(cuitEmpresa));
            }
            if (!WebKeysCompras.isEmpty(sucursalEmpresa)) {
                empresaVisible.append(" - Sucursal: ").append(HtmlUtil.escape(sucursalEmpresa));
            }
            empresaVisible.append("</div>");

            if (puedeEditarEmpresaPresupuestos && presupuesto.isActivo()
                    && presupuesto.getIdRequerimiento() != null
                    && presupuesto.getIdRequerimiento().intValue()
                            == idRequerimientoCompraPresupuestos) {
                empresaVisible.append("<div id=\"").append(HtmlUtil.escape(idEdicionEmpresa));
                empresaVisible.append("\" style=\"display:none;\"><table class=\"lfr-table\">");
                empresaVisible.append("<tr><td><label>Nombre (obligatorio):</label></td><td>");
                empresaVisible.append("<input type=\"text\" class=\"cotizacion-empresa-nombre\" maxlength=\"200\" size=\"30\" value=\"");
                empresaVisible.append(HtmlUtil.escape(nombreEmpresa)).append("\" /></td></tr>");
                empresaVisible.append("<tr><td><label>CUIT (opcional):</label></td><td>");
                empresaVisible.append("<input type=\"text\" class=\"cotizacion-empresa-cuit\" maxlength=\"11\" size=\"13\" value=\"");
                empresaVisible.append(HtmlUtil.escape(cuitEmpresa)).append("\"");
                if (!WebKeysCompras.isEmpty(cuitEmpresa)) {
                    empresaVisible.append(" readonly=\"readonly\"");
                }
                empresaVisible.append(" />");
                empresaVisible.append("<input type=\"hidden\" class=\"cotizacion-empresa-sucursal\" value=\"");
                empresaVisible.append(HtmlUtil.escape(sucursalEmpresa)).append("\" /></td></tr>");
                empresaVisible.append("<tr><td></td><td><input type=\"button\" value=\"Guardar\" onclick=\"return ");
                empresaVisible.append(namespaceAdjuntos).append("guardarEmpresaCotizacion(");
                empresaVisible.append(idRequerimientoPresupuesto).append(", this);\" /> ");
                empresaVisible.append("<input type=\"button\" value=\"Cancelar\" onclick=\"return ");
                empresaVisible.append(namespaceAdjuntos).append("cancelarEdicionEmpresaCotizacion(");
                empresaVisible.append(idRequerimientoPresupuesto).append(");\" /></td></tr></table></div>");
            }
            row.addText(empresaVisible.toString());
        } else {
            row.addText(
                    HtmlUtil.escape(
                            presupuesto.getDescripcionPrestador()
                    )
            );
        }

        boolean documentoValido =
                Boolean.TRUE.equals(
                        documentosValidosPreparados.get(
                                Integer.valueOf(
                                        idRequerimientoPresupuesto
                                )
                        )
                );

        String downloadURL =
                downloadUrlsPreparadas.get(
                        Integer.valueOf(
                                idRequerimientoPresupuesto
                        )
                );

        if (downloadURL == null) {
            downloadURL = "";
        }

        StringBuilder descargar =
                new StringBuilder();

        if (documentoValido
                && !WebKeysCompras.isEmpty(downloadURL)) {

            descargar.append("<a href=\"");
            descargar.append(HtmlUtil.escape(downloadURL));
            descargar.append("\" target=\"_blank\">");
            descargar.append(
                    "<img alt=\"Descargar presupuesto\" src=\""
            );
            descargar.append(
                    themeDisplay.getPathThemeImages()
            );
            descargar.append("/common/view.png\" />");
            descargar.append("</a>");
        } else {
            descargar.append(
                    "<span title=\"El documento asociado no está disponible\">No disponible</span>"
            );
        }

        row.addText(descargar.toString());

        if (cotizacionEmpresaPresupuestos) {
            StringBuilder editar = new StringBuilder();
            if (puedeEditarEmpresaPresupuestos && presupuesto.isActivo()
                    && presupuesto.getIdRequerimiento() != null
                    && presupuesto.getIdRequerimiento().intValue()
                            == idRequerimientoCompraPresupuestos) {
                editar.append("<a href=\"#\" title=\"Editar empresa\" onclick=\"return ");
                editar.append(namespaceAdjuntos).append("editarEmpresaCotizacion(");
                editar.append(idRequerimientoPresupuesto).append(");\"><img alt=\"Editar empresa\" src=\"");
                editar.append(themeDisplay.getPathThemeImages()).append("/common/edit.png\" /></a>");
            }
            row.addText(editar.toString());
        } else {
            StringBuilder editar = new StringBuilder();
            if (puedeEliminarPresupuestos && presupuesto.isActivo()
                    && presupuesto.getIdPrestador() != null
                    && presupuesto.getIdPrestador().intValue() > 0) {
                editar.append("<a href=\"#\" title=\"Editar presupuesto\" onclick=\"jQuery('#");
                editar.append(namespaceAdjuntos);
                editar.append("presupuesto_0_id_prestador').val('");
                editar.append(presupuesto.getIdPrestador().intValue());
                editar.append("').change(); return false;\"><img alt=\"Editar presupuesto\" src=\"");
                editar.append(themeDisplay.getPathThemeImages());
                editar.append("/common/edit.png\" /></a>");
            }
            row.addText(editar.toString());
        }

        StringBuilder borrar =
                new StringBuilder();

        if (puedeEliminarPresupuestos
                && documentoValido) {

            borrar.append(
                    "<img alt=\"Eliminar presupuesto\" src=\""
            );
            borrar.append(
                    themeDisplay.getPathThemeImages()
            );
            borrar.append(
                    "/common/delete.png\" style=\"cursor: pointer;\" onclick=\"return "
            );
            borrar.append(namespaceAdjuntos);
            borrar.append(
                    "deletePresupuestoRequerimientoCompra("
            );
            borrar.append(idRequerimientoPresupuesto);
            borrar.append(");\" />");
        }

        row.addText(borrar.toString());
        resultRows.add(row);
    }
%>
<liferay-ui:search-iterator
        searchContainer="<%= searchContainer %>" />

<%
} catch (Exception e) {
    logPresupuestos.error(
            "No se pudieron consultar los presupuestos "
                    + "asociados al requerimiento. "
                    + "idRequerimientoCompra="
                    + idRequerimientoCompraPresupuestos,
            e
    );
%>

    <div class="portlet-msg-error">
        No se pudieron consultar los presupuestos asociados
        al requerimiento.
    </div>

<%
}
%>
