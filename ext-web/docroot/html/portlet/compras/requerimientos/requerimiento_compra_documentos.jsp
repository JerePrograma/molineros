<%--
Responsabilidad:
    Renderiza la pantalla de documentos del requerimiento.
Incluido desde:
    requerimiento_compra_documentos_componente.jsp
Pantallas o estados de uso:
    Búsqueda, selección o popup según el forward indicado.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    tabla_carga_presupuestos, compra_presupuesto_fm, helpCargaPresupuestos
Efectos secundarios:
    Sólo modifica el DOM o comunica la selección al callback namespaced.
--%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper" %>
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
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Set" %>

<%
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
        Boolean.TRUE.equals(soloLecturaAttrPresupuestos)
        || soloLecturaParamPresupuestos
        || "ver".equalsIgnoreCase(modoPresupuestos)
        || "/compras/ver_requerimiento".equals(
                strutsActionPresupuestos
        );

Object puedeCotizarAttrPresupuestos =
        request.getAttribute(
                "compras.requerimiento.puedeCotizar"
        );

boolean puedeCotizarPresupuestos =
        puedeCotizarAttrPresupuestos instanceof Boolean
                ? Boolean.TRUE.equals(puedeCotizarAttrPresupuestos)
                : user != null
                        && PermissionUtil.userContainsRole(
                                user,
                                WebKeysCompras.ROL_COTIZAR_COMPRAS
                        );

boolean altaEmpresaPresupuestos =
        idRequerimientoCompraPresupuestos == 0
        && Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.esNuevo"))
        && Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.puedeABM"))
        && puedeCotizarPresupuestos
        && !soloLecturaPresupuestos;

boolean puedeEditarPresupuestos =
        altaEmpresaPresupuestos || (idRequerimientoCompraPresupuestos > 0
        && puedeCotizarPresupuestos
        && reqPresupuestos.puedeAdministrarPresupuestos()
        && !soloLecturaPresupuestos);

boolean cotizacionEmpresaPresupuestos =
        altaEmpresaPresupuestos || reqPresupuestos.esSectorSinCotizacionPrestador();

int filasEmpresaRecuperadas = altaEmpresaPresupuestos ? Math.min(
        WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA,
        Math.max(0, ParamUtil.getInteger(renderRequest, "presupuesto_count", 0))) : 0;

boolean puedeVerPrestadoresEnviadosPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && !cotizacionEmpresaPresupuestos
        && reqPresupuestos.puedeVerPresupuestos();

List<PrestadorCotizacion> prestadoresEnviadosPresupuestos =
        (List<PrestadorCotizacion>) request.getAttribute(
                "compras.requerimiento.prestadoresEnviados"
        );

if (prestadoresEnviadosPresupuestos == null) {
    prestadoresEnviadosPresupuestos =
            new ArrayList<PrestadorCotizacion>();
}

String errorPrestadoresPresupuestos =
        (String) request.getAttribute(
                "compras.requerimiento.errorPrestadoresEnviados"
        );

if (errorPrestadoresPresupuestos == null) {
    errorPrestadoresPresupuestos = "";
}

boolean hayPrestadoresEnviadosPresupuestos =
        prestadoresEnviadosPresupuestos != null
        && !prestadoresEnviadosPresupuestos.isEmpty();

List<PrestadorCotizacion> prestadoresDisponiblesPresupuestos =
        (List<PrestadorCotizacion>) request.getAttribute(
                "compras.requerimiento.prestadoresDisponiblesPresupuesto"
        );

if (prestadoresDisponiblesPresupuestos == null) {
    prestadoresDisponiblesPresupuestos =
            new ArrayList<PrestadorCotizacion>();
}

boolean hayPrestadoresDisponiblesPresupuestos =
        !prestadoresDisponiblesPresupuestos.isEmpty();

long maximoTamanoPresupuesto =
        DocumentoLibraryComprasHelper.obtenerMaximoTamanoDocumento();
String maximoTamanoPresupuestoTexto =
        maximoTamanoPresupuesto >= 1000000L
                ? Math.round(maximoTamanoPresupuesto / 1000000.0d) + " MB"
                : maximoTamanoPresupuesto % 1024L == 0L
                        ? (maximoTamanoPresupuesto / 1024L) + " KB"
                        : maximoTamanoPresupuesto + " bytes";

int maxPresupuestosCargaActual =
        cotizacionEmpresaPresupuestos
                ? WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA
                : 1;

PortletURL uploadPresupuestosURL =
        renderResponse.createActionURL();

uploadPresupuestosURL.setWindowState(
        WindowState.MAXIMIZED
);

uploadPresupuestosURL.setParameter(
        "struts_action",
        "/compras/upload_presupuestos_requerimiento"
);

PortletURL comparativaPrestadorURL = renderResponse.createRenderURL();
comparativaPrestadorURL.setWindowState(LiferayWindowState.EXCLUSIVE);
comparativaPrestadorURL.setParameter("struts_action", "/compras/comparativa");
comparativaPrestadorURL.setParameter("id_requerimiento_compra",
        String.valueOf(idRequerimientoCompraPresupuestos));

PortletURL buscarEmpresasCotizacionURL = null;

if (cotizacionEmpresaPresupuestos) {
    buscarEmpresasCotizacionURL =
            renderResponse.createRenderURL();

    buscarEmpresasCotizacionURL.setWindowState(
            LiferayWindowState.EXCLUSIVE
    );

    buscarEmpresasCotizacionURL.setParameter(
            "struts_action",
            "/compras/buscar_empresas_cotizacion"
    );

    buscarEmpresasCotizacionURL.setParameter(
            WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA,
            String.valueOf(
                    idRequerimientoCompraPresupuestos
            )
    );
}

boolean puedeCompletarFiscalPresupuestos = reqPresupuestos.isActivo()
        && reqPresupuestos.esSectorSinCotizacionPrestador()
        && reqPresupuestos.isOrdenCompra() && puedeCotizarPresupuestos
        && !"ver".equalsIgnoreCase(modoPresupuestos)
        && !"/compras/ver_requerimiento".equals(strutsActionPresupuestos);
String modoRetornoPresupuestos =
        soloLecturaPresupuestos && !puedeCompletarFiscalPresupuestos
                ? "ver"
                : "editar";

String msgInsertErrorPresupuestos =
        (String) request.getAttribute(
                "msgInsertError"
        );

if (msgInsertErrorPresupuestos == null) {
    msgInsertErrorPresupuestos = "";
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

<style type="text/css">
    #<portlet:namespace />tabla_carga_presupuestos {
        width: 100%;
        border-collapse: separate;
        border-spacing: 3px;
    }

    #<portlet:namespace />tabla_carga_presupuestos th {
        text-align: left;
        vertical-align: top;
    }

    #<portlet:namespace />tabla_carga_presupuestos td {
        vertical-align: top;
    }

    #<portlet:namespace />tabla_carga_presupuestos select.presupuesto-prestador,
    #<portlet:namespace />tabla_carga_presupuestos input.presupuesto-archivo,
    #<portlet:namespace />tabla_carga_presupuestos td.presupuesto-acciones input {
        margin-top: 0;
    }

    #<portlet:namespace />tabla_carga_presupuestos .compras-ayuda-campo {
        white-space: normal;
    }

    #<portlet:namespace />tabla_carga_presupuestos .presupuesto-ayuda-tamano {
        margin-left: 8px;
    }

    #<portlet:namespace />tabla_carga_presupuestos
    td.presupuesto-campo-contraparte {
        width: 40%;
    }

    #<portlet:namespace />tabla_carga_presupuestos
    td.presupuesto-campo-archivo {
        width: 35%;
    }

    #<portlet:namespace />tabla_carga_presupuestos
    td.presupuesto-acciones {
        width: 25%;
        white-space: nowrap;
    }

    #<portlet:namespace />tabla_carga_presupuestos
    select.presupuesto-prestador {
        width: 98%;
    }

    <% if (cotizacionEmpresaPresupuestos) { %>
        #<portlet:namespace />tabla_carga_presupuestos
        .presupuesto-empresa-seleccionada {
            margin-top: 4px;
        }
    <% } %>

    #<portlet:namespace />tabla_carga_presupuestos
    input.presupuesto-archivo {
        width: 98%;
    }

    #<portlet:namespace />tabla_carga_presupuestos
    td.presupuesto-acciones input {
        margin-right: 4px;
    }

    .compras-pdf-pendiente {
        font-size: 18px;
        cursor: help;
    }
</style>

<form action="<%= uploadPresupuestosURL.toString() %>"
      method="post"
      name="<portlet:namespace />compra_presupuesto_fm"
      id="<portlet:namespace />compra_presupuesto_fm"
      class="compras-adjuntos-formulario"
      enctype="multipart/form-data">

    <fieldset class="block-labels compras-adjuntos-pedidos">
        <legend>
            <%= cotizacionEmpresaPresupuestos
                    ? "Cotizaciones de empresas"
                    : "Pedidos de presupuestos" %>

            <a href="javascript:void(0)"
               onclick="return comprasHelp(
                       event,
                       '<portlet:namespace />helpCargaPresupuestos'
               );">
                <img
                        style="height: 25px; width: 25px; vertical-align: middle;"
                        src="/html/images/help.png"
                        title="Ayuda para carga de cotizaciones"
                        alt="Ayuda" />
            </a>
        </legend>

        <liferay-ui:error
                key="errorUploadFile"
                message="<%= HtmlUtil.escape(msgInsertErrorPresupuestos) %>" />

        <c:if test="<%= msgPresupuestoGuardado %>">
            <div class="portlet-msg-success">
                Se cargaron
                <%= presupuestosGuardados %>
                <%= cotizacionEmpresaPresupuestos
                        ? (presupuestosGuardados == 1
                                ? "cotización"
                                : "cotizaciones")
                        : (presupuestosGuardados == 1
                                ? "presupuesto"
                                : "presupuestos") %>
                correctamente.
            </div>
        </c:if>

        <c:if test="<%= msgPresupuestoBorrado %>">
            <div class="portlet-msg-success">
                <%= cotizacionEmpresaPresupuestos
                        ? "Cotización de empresa eliminada correctamente."
                        : "Presupuesto eliminado correctamente." %>
            </div>
        </c:if>

        <c:if test="<%= idRequerimientoCompraPresupuestos <= 0 && !altaEmpresaPresupuestos %>">
            <div class="portlet-msg-info">
                <%= cotizacionEmpresaPresupuestos
                        ? "Debe guardar el requerimiento antes de subir cotizaciones de empresas."
                        : "Debe guardar y enviar a cotizar el requerimiento antes de subir presupuestos." %>
            </div>
        </c:if>

        <c:if test="<%= puedeVerPrestadoresEnviadosPresupuestos %>">
            <c:choose>
                <c:when test="<%=
                        !WebKeysCompras.isEmpty(
                                errorPrestadoresPresupuestos
                        )
                %>">
                    <div class="portlet-msg-error">
                        <%= HtmlUtil.escape(
                                errorPrestadoresPresupuestos
                        ) %>
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
                                <th>Email registrado</th>
                                <th>Email destino</th>
                                <th>Estado de notificación</th>
                                <th>PDF</th>
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

                                String emailRegistradoVisible =
                                        prestadorEnviado.getEmailVisible();

                                String emailDestinoVisible =
                                        prestadorEnviado.getEmailDestinoVisible();

                                String[] emailsRegistradosSeparados =
                                        WebKeysCompras.isEmpty(
                                                emailRegistradoVisible
                                        )
                                                ? new String[0]
                                                : emailRegistradoVisible.split(";");

                                String[] emailsDestinoSeparados =
                                        WebKeysCompras.isEmpty(
                                                emailDestinoVisible
                                        )
                                                ? new String[0]
                                                : emailDestinoVisible.split(";");

                                List<String> emailsRegistradosVisibles =
                                        new ArrayList<String>();

                                List<String> emailsDestinoVisibles =
                                        new ArrayList<String>();

                                Set<String> emailsActuales =
                                        new HashSet<String>();

                                Set<String> emailsHistoricos =
                                        new HashSet<String>();

                                for (int j = 0;
                                        j < emailsRegistradosSeparados.length;
                                        j++) {

                                    String emailRegistradoItem =
                                            emailsRegistradosSeparados[j];

                                    if (emailRegistradoItem != null) {
                                        emailRegistradoItem =
                                                emailRegistradoItem.trim();
                                    }

                                    if (WebKeysCompras.isEmpty(
                                            emailRegistradoItem
                                    )) {
                                        continue;
                                    }

                                    String emailRegistradoNormalizado =
                                            emailRegistradoItem.toLowerCase(
                                                    Locale.ROOT
                                            );

                                    if (emailsActuales.add(
                                            emailRegistradoNormalizado
                                    )) {
                                        emailsRegistradosVisibles.add(
                                                emailRegistradoItem
                                        );
                                    }
                                }

                                for (int j = 0;
                                        j < emailsDestinoSeparados.length;
                                        j++) {

                                    String emailDestinoItem =
                                            emailsDestinoSeparados[j];

                                    if (emailDestinoItem != null) {
                                        emailDestinoItem =
                                                emailDestinoItem.trim();
                                    }

                                    if (WebKeysCompras.isEmpty(
                                            emailDestinoItem
                                    )) {
                                        continue;
                                    }

                                    String emailDestinoNormalizado =
                                            emailDestinoItem.toLowerCase(
                                                    Locale.ROOT
                                            );

                                    if (emailsHistoricos.add(
                                            emailDestinoNormalizado
                                    )) {
                                        emailsDestinoVisibles.add(
                                                emailDestinoItem
                                        );
                                    }
                                }

                                boolean emailDestinoDifiere =
                                        !emailsActuales.equals(
                                                emailsHistoricos
                                        );

                                boolean pdfPendiente =
                                        reqPresupuestos
                                                .puedeAdministrarPresupuestos()
                                        && WebKeysCompras.ENVIO_ENVIADO.equals(
                                                prestadorEnviado
                                                        .getEstadoEnvio()
                                        );
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
                                        <% if (emailsRegistradosVisibles.isEmpty()) { %>
                                            No informado
                                        <% } else { %>
                                            <%
                                            for (int j = 0;
                                                    j < emailsRegistradosVisibles.size();
                                                    j++) {

                                                if (j > 0) {
                                            %>
                                                    <br />
                                            <%
                                                }
                                            %>
                                                <%= HtmlUtil.escape(
                                                        emailsRegistradosVisibles.get(j)
                                                ) %>
                                            <%
                                            }
                                            %>
                                        <% } %>
                                    </td>

                                    <td>
                                        <% if (emailsDestinoVisibles.isEmpty()) { %>
                                            No informado
                                        <% } else { %>
                                            <%
                                            for (int j = 0;
                                                    j < emailsDestinoVisibles.size();
                                                    j++) {

                                                if (j > 0) {
                                            %>
                                                    <br />
                                            <%
                                                }
                                            %>
                                                <%= HtmlUtil.escape(
                                                        emailsDestinoVisibles.get(j)
                                                ) %>
                                            <%
                                            }
                                            %>
                                        <% } %>

                                        <% if (emailDestinoDifiere) { %>
                                            <br />
                                            <em>
                                                Difiere del email registrado actual
                                            </em>
                                        <% } %>
                                    </td>

                                    <td>
                                        <%= HtmlUtil.escape(
                                                prestadorEnviado
                                                        .getEstadoEnvioVisible()
                                        ) %>
                                    </td>

                                    <td style="text-align:center;">
                                        <% if (pdfPendiente) { %>
                                            <span class="compras-pdf-pendiente"
                                                  title="PDF pendiente de carga">
                                                &#128161;
                                            </span>
                                        <% } %>
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
                !cotizacionEmpresaPresupuestos
                &&
                puedeEditarPresupuestos
                && WebKeysCompras.isEmpty(
                        errorPrestadoresPresupuestos
                )
                && hayPrestadoresDisponiblesPresupuestos
        %>">

            <table class="lfr-table taglib-search-iterator"
                   id="<portlet:namespace />tabla_carga_presupuestos">

                <colgroup>
                    <col style="width: 40%;" />
                    <col style="width: 35%;" />
                </colgroup>

                <thead>
                    <tr>
                        <th>Prestador enviado</th>
                        <th>Archivo</th>
                    </tr>
                </thead>

                <tbody id="<portlet:namespace />presupuestos_body">
                </tbody>
            </table>

            <select id="<portlet:namespace />prestador_presupuesto_template"
                    style="display: none;">

                <option value="">Seleccione...</option>

                <%
                for (int i = 0;
                        i < prestadoresDisponiblesPresupuestos.size();
                        i++) {

                    PrestadorCotizacion prestadorPresupuesto =
                            prestadoresDisponiblesPresupuestos.get(i);

                    if (prestadorPresupuesto == null
                            || prestadorPresupuesto
                                    .getIdPrestador() <= 0) {
                        continue;
                    }
                %>
                    <option value="<%=
                            prestadorPresupuesto.getIdPrestador()
                    %>">
                        <%= HtmlUtil.escape(
                                prestadorPresupuesto.getEtiquetaVisible()
                        ) %>
                    </option>
                <%
                }
                %>
            </select>
            <div id="<portlet:namespace />comparativaPrestadorSeccion"></div>
        </c:if>

        <c:if test="<%=
                cotizacionEmpresaPresupuestos
                && puedeEditarPresupuestos
        %>">
            <table class="lfr-table taglib-search-iterator"
                   id="<portlet:namespace />tabla_carga_presupuestos">

                <colgroup>
                    <col style="width: 40%;" />
                    <col style="width: 35%;" />
                    <col style="width: 25%;" />
                </colgroup>

                <thead>
                    <tr>
                        <th>Empresa</th>
                        <th>Archivo</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody id="<portlet:namespace />presupuestos_body">
                </tbody>
            </table>
        </c:if>

        <% if (altaEmpresaPresupuestos) { %>
            <input type="button" value="Agregar cotizaci&#243;n de Empresa"
                   onclick="return <portlet:namespace />agregarFilaPresupuesto();" />
            <div class="portlet-msg-info">
                Las cotizaciones se guardan junto al requerimiento. Puede guardar sin adjudicar.
                Si vuelve a cargar la pantalla despu&#233;s de un error, seleccione nuevamente los archivos.
            </div>
            <div id="<portlet:namespace />empresas_recuperadas" style="display:none;">
                <% for (int filaEmpresa = 0; filaEmpresa < filasEmpresaRecuperadas; filaEmpresa++) { %>
                    <span data-cuit="<%= HtmlUtil.escape(ParamUtil.getString(renderRequest, "presupuesto_" + filaEmpresa + "_empresa_cuit", "")) %>"
                          data-sucursal="<%= HtmlUtil.escape(ParamUtil.getString(renderRequest, "presupuesto_" + filaEmpresa + "_empresa_sucursal", "")) %>"
                          data-descripcion="<%= HtmlUtil.escape(ParamUtil.getString(renderRequest, "presupuesto_" + filaEmpresa + "_descripcion_empresa", "")) %>"></span>
                <% } %>
            </div>
        <% } %>

        <c:if test="<%=
                idRequerimientoCompraPresupuestos > 0
                && !puedeEditarPresupuestos
                && !soloLecturaPresupuestos
        %>">
            <div class="portlet-msg-info">
                <%= cotizacionEmpresaPresupuestos
                        ? "La carga de cotizaciones de empresas requiere estado PENDIENTE y rol de cotización. Los datos de Empresa también pueden editarse en ORDEN DE COMPRA."
                        : "Los presupuestos solo pueden administrarse en estado A COTIZAR y con rol de cotización." %>
            </div>
        </c:if>
    </fieldset>

    <div
            id="<portlet:namespace />helpCargaPresupuestos"
            class="containerPlus draggable compras-container-ayuda {buttons:'c', skin:'default', width:'700',title:'Ayuda - Carga de cotizaciones',closed:'true'}"
            style="top: 500px; left: 200px">

        <strong>Requisitos para cargar cotizaciones</strong>
        <br /><br />

        <% if (cotizacionEmpresaPresupuestos) { %>
        - El requerimiento debe pertenecer a RRHH o SISTEMAS. Puede preparar
          las cotizaciones durante el alta y cargarlas mientras permanezca PENDIENTE.
        <br />

        - Debe informar el nombre de la Empresa. El CUIT es opcional y puede
          completarlo después desde Editar en la cotización guardada, también en
          ORDEN DE COMPRA. Buscar permite seleccionar
          una Empresa del padrón de Empleadores.
        <br />

        - Una misma identidad fiscal (CUIT y sucursal) no puede tener otra
          cotización activa para el requerimiento. El nombre no identifica fiscalmente una Empresa.
        <br />

        - Puede utilizar "Agregar otra cotización" para cargar documentos de
          varias Empresas en una misma operación.
        <br />

        <% } else { %>
        - El requerimiento debe estar guardado y enviado a cotizar.
        <br />

        - La carga de presupuestos se encuentra disponible en estado
          A COTIZAR y para usuarios con permiso de cotización.
        <br />

        - Sólo se pueden cargar presupuestos para prestadores cuya
          notificación haya finalizado correctamente.
        <br />

        - Debe seleccionar el prestador al que corresponde cada presupuesto.
        <br />

        - El presupuesto debe presentarse en formato PDF.
        <br />

        - Seleccione un prestador y complete sus datos y prestaciones.
          Guardar registra el archivo y los datos del presupuesto.
        <br />

        - En la primera carga debe seleccionar un PDF no vacío.
          Al editar, puede conservar el archivo actual o seleccionar uno para reemplazarlo.
        <br />

        - Sólo puede existir un presupuesto activo por prestador.
          Eliminar limpia la carga actual; la tabla Cotizaciones permite eliminar un presupuesto guardado.
        <br />

        <% if (maximoTamanoPresupuesto != Long.MAX_VALUE) { %>
            - Cada archivo no debe superar los
              <%= maximoTamanoPresupuestoTexto %>.
        <% } %>
        <br />

        - La lamparita de la columna PDF indica que el prestador fue
          notificado y todavía tiene pendiente la carga de su presupuesto.
        <% } %>

        <% if (cotizacionEmpresaPresupuestos) { %>
        <br />

        - El archivo debe presentarse en formato PDF, no estar vacío y
          respetar el tamaño máximo permitido por Document Library.
        <% } %>
    </div>

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

    <% if (!altaEmpresaPresupuestos) { %>
    <fieldset class="block-labels cotizaciones-fieldset compras-adjuntos-cotizaciones">
        <legend>
            <%= cotizacionEmpresaPresupuestos
                    ? "Cotizaciones de empresas cargadas"
                    : "Cotizaciones" %>
        </legend>

        <div id="<portlet:namespace />listado_presupuestos_requerimiento">
            <jsp:include
                    page="/html/portlet/compras/requerimientos/requerimiento_compra_documentos_busqueda_resultado.jsp" />
        </div>
    </fieldset>
    <% } %>
</form>

<script type="text/javascript">
    function <portlet:namespace />editarEmpresaCotizacion(idPresupuesto) {
        jQuery('#<portlet:namespace />cotizacion_empresa_datos_' + idPresupuesto).hide();
        var editor = jQuery('#<portlet:namespace />cotizacion_empresa_edicion_' + idPresupuesto);
        editor.show();
        editor.find('input.cotizacion-empresa-nombre').focus();
        return false;
    }

    function <portlet:namespace />cancelarEdicionEmpresaCotizacion(idPresupuesto) {
        var editor = jQuery('#<portlet:namespace />cotizacion_empresa_edicion_' + idPresupuesto);
        editor.find('input[type="text"]').each(function() {
            this.value = this.defaultValue;
        });
        editor.hide();
        jQuery('#<portlet:namespace />cotizacion_empresa_datos_' + idPresupuesto).show();
        return false;
    }

    function <portlet:namespace />guardarEmpresaCotizacion(idPresupuesto, boton) {
        var editor = jQuery('#<portlet:namespace />cotizacion_empresa_edicion_' + idPresupuesto);
        var nombre = jQuery.trim(editor.find('input.cotizacion-empresa-nombre').val() || '');
        var cuit = jQuery.trim(editor.find('input.cotizacion-empresa-cuit').val() || '');
        var sucursal = jQuery.trim(editor.find('input.cotizacion-empresa-sucursal').val() || '');
        if (nombre == '' || nombre.length > 200) {
            alert('Debe informar el nombre de la Empresa (hasta 200 caracteres).');
            editor.find('input.cotizacion-empresa-nombre').focus();
            return false;
        }
        if (cuit != '' && (!/^[0-9]{11}$/.test(cuit)
                || (typeof validarCuil == 'function' && !validarCuil(cuit, 'El CUIT no es válido.')))) {
            alert('El CUIT informado no es válido.');
            editor.find('input.cotizacion-empresa-cuit').focus();
            return false;
        }
        var form = document.getElementById('<portlet:namespace />compra_presupuesto_fm');
        if (!form || boton.disabled) { return false; }
        jQuery(form).find('input.empresa-fiscal-envio').remove();
        jQuery('<input type="hidden" class="empresa-fiscal-envio" />')
                .attr('name', '<portlet:namespace />descripcion_empresa').val(nombre).appendTo(form);
        jQuery('<input type="hidden" class="empresa-fiscal-envio" />')
                .attr('name', '<portlet:namespace />empresa_cuit').val(cuit).appendTo(form);
        jQuery('<input type="hidden" class="empresa-fiscal-envio" />')
                .attr('name', '<portlet:namespace />empresa_sucursal').val(sucursal).appendTo(form);
        jQuery('#<portlet:namespace />id_requerimiento_presupuesto').val(idPresupuesto);
        jQuery('#<portlet:namespace />presupuesto_accion').val('editarEmpresa');
        boton.disabled = true;
        form.submit();
        return false;
    }

    function <portlet:namespace />validarTamanoArchivoPresupuesto(archivo) {
        var maximo = <%= maximoTamanoPresupuesto == Long.MAX_VALUE
                ? 0L : maximoTamanoPresupuesto %>;

        if (maximo <= 0 || !archivo
                || !archivo.files || archivo.files.length == 0) {
            return true;
        }

        if (archivo.files[0].size > maximo) {
            alert(
                    'El archivo "' + archivo.files[0].name
                            + '" supera el tamaño máximo permitido de '
                            + '<%= maximoTamanoPresupuestoTexto %>. '
                            + 'Seleccione un archivo de menor tamaño.'
            );
            archivo.focus();
            return false;
        }

        return true;
    }

    <% if (cotizacionEmpresaPresupuestos) { %>
    function <portlet:namespace />actualizarCotizacionesEmpresaSector(limpiar) {
        <% if (altaEmpresaPresupuestos) { %>
        var habilitado = <portlet:namespace />esSectorSinCotizacionPrestadorCompra();
        var panel = jQuery('#<portlet:namespace />cotizaciones_empresa_panel, #<portlet:namespace />adjudicacion_empresa_panel');
        if (!habilitado && limpiar) {
            jQuery('#<portlet:namespace />presupuestos_body').empty();
            <portlet:namespace />reindexarFilasPresupuesto();
            if (<portlet:namespace />popupEmpresaCotizacion && typeof Liferay.Popup.close == 'function') {
                Liferay.Popup.close(<portlet:namespace />popupEmpresaCotizacion);
            }
            <portlet:namespace />popupEmpresaCotizacion = null;
            <portlet:namespace />filaEmpresaCotizacion = null;
        }
        if (habilitado) {
            panel.find(':input').removeAttr('disabled');
            panel.show();
        } else {
            panel.find(':input').attr('disabled', 'disabled');
            panel.hide();
        }
        <portlet:namespace />actualizarEmpresasAdjudicacion();
        <% } %>
    }

    function <portlet:namespace />validarCotizacionesEmpresaGuardado() {
        <% if (altaEmpresaPresupuestos) { %>
        return !<portlet:namespace />esSectorSinCotizacionPrestadorCompra()
                || <portlet:namespace />validarFilasPresupuesto(true);
        <% } else { %>
        return true;
        <% } %>
    }

    function <portlet:namespace />incorporarCotizacionesEmpresa(form) {
        var contextos = [];
        <% if (puedeEditarPresupuestos) { %>
        <% if (altaEmpresaPresupuestos) { %>
        if (!<portlet:namespace />esSectorSinCotizacionPrestadorCompra()) { return contextos; }
        <portlet:namespace />reindexarFilasPresupuesto();
        <% } %>
        <portlet:namespace />capturarEmpresaAdjudicada();
        var nodos = jQuery('#<portlet:namespace />empresa_adjudicada_cuit, '
                + '#<portlet:namespace />empresa_adjudicada_sucursal, '
                + '#<portlet:namespace />empresa_adjudicacion_informada, '
                + '#<portlet:namespace />empresa_adjudicada_id, '
                + '#<portlet:namespace />empresa_adjudicada_indice');
        <% if (altaEmpresaPresupuestos) { %>
        nodos = nodos.add('#<portlet:namespace />presupuesto_count')
                .add('#<portlet:namespace />presupuestos_body input[type="hidden"], '
                        + '#<portlet:namespace />presupuestos_body input[type="file"], '
                        + '#<portlet:namespace />presupuestos_body input[type="text"]');
        <% } %>
        nodos.each(function() {
            contextos.push({nodo: this, padre: this.parentNode, siguiente: this.nextSibling});
            form.appendChild(this);
        });
        <% } %>
        return contextos;
    }

    function <portlet:namespace />restaurarCotizacionesEmpresa(contextos) {
        if (!contextos) { return; }
        for (var i = contextos.length - 1; i >= 0; i--) {
            var contexto = contextos[i];
            if (contexto.siguiente && contexto.siguiente.parentNode == contexto.padre) {
                contexto.padre.insertBefore(contexto.nodo, contexto.siguiente);
            } else {
                contexto.padre.appendChild(contexto.nodo);
            }
        }
    }

    function <portlet:namespace />guardarEmpresaAdjudicada(boton) {
        var form = document.getElementById('<portlet:namespace />empresa_adjudicacion_fm');
        if (!form || boton.disabled) { return false; }
        var token = jQuery(form).find('input[name="<portlet:namespace />compras_save_token"]');
        var tokenPrincipal = jQuery('#<portlet:namespace />compras_save_token');
        if (tokenPrincipal.length > 0 && tokenPrincipal.val()) { token.val(tokenPrincipal.val()); }
        if (!token.val()) {
            alert('Debe volver a abrir el requerimiento antes de guardar la adjudicacion.');
            return false;
        }
        <portlet:namespace />capturarEmpresaAdjudicada();
        jQuery(form).find('input.empresa-adjudicacion-envio').remove();
        jQuery('#<portlet:namespace />empresa_adjudicada_cuit, '
                + '#<portlet:namespace />empresa_adjudicada_sucursal, '
                + '#<portlet:namespace />empresa_adjudicacion_informada, '
                + '#<portlet:namespace />empresa_adjudicada_id, '
                + '#<portlet:namespace />empresa_adjudicada_indice').each(function() {
            jQuery(this).clone().removeAttr('id').addClass('empresa-adjudicacion-envio').appendTo(form);
        });
        boton.disabled = true;
        form.submit();
        return false;
    }

    var <portlet:namespace />popupEmpresaCotizacion = null;
    var <portlet:namespace />filaEmpresaCotizacion = null;

    function <portlet:namespace />abrirBusquedaEmpresaCotizacion(row) {
        <portlet:namespace />filaEmpresaCotizacion = row;

        <portlet:namespace />popupEmpresaCotizacion = Liferay.Popup({
            title: 'Búsqueda de Empresas',
            modal: true,
            width: 700
        });

        var url = '<%= buscarEmpresasCotizacionURL.toString() %>';
        <% if (altaEmpresaPresupuestos) { %>
        if (!<portlet:namespace />esSectorSinCotizacionPrestadorCompra()) {
            return false;
        }
        url += '&modo=alta&id_sector=' + encodeURIComponent(jQuery('#<portlet:namespace />sector_id').val());
        <% } %>
        jQuery(<portlet:namespace />popupEmpresaCotizacion).load(url);

        return false;
    }

    function <portlet:namespace />seleccionarEmpresaCotizacionCompra(
            cuit,
            sucursal,
            razonSocial) {

        var row = <portlet:namespace />filaEmpresaCotizacion;

        cuit = jQuery.trim(String(cuit || ''));
        sucursal = jQuery.trim(String(sucursal || ''));
        razonSocial = jQuery.trim(String(razonSocial || ''));

        if (!row || cuit == '' || sucursal == '' || razonSocial == '') {
            alert('No se pudo seleccionar la Empresa informada.');
            return false;
        }

        var repetida = false;
        jQuery('#<portlet:namespace />presupuestos_body > tr').each(function() {
            var otra = jQuery(this);
            if (otra.get(0) != row.get(0)
                    && otra.find('input.presupuesto-empresa-cuit').val() == cuit
                    && otra.find('input.presupuesto-empresa-sucursal').val() == sucursal) {
                repetida = true;
            }
        });
        <% if (!altaEmpresaPresupuestos) { %>
        jQuery('#<portlet:namespace />empresa_adjudicada_selector option').each(function() {
            if (jQuery(this).attr('data-cuit') == cuit
                    && jQuery(this).attr('data-sucursal') == sucursal) { repetida = true; }
        });
        <% } %>
        if (repetida) {
            alert('La Empresa ya tiene una cotizacion cargada o seleccionada.');
            return false;
        }
        row.find('input.presupuesto-empresa-descripcion').val(razonSocial);
        row.find('input.presupuesto-empresa-cuit').val(cuit);
        row.find('input.presupuesto-empresa-sucursal').val(sucursal);
        row.find('.presupuesto-empresa-seleccionada').text('Sucursal: ' + sucursal);

        <portlet:namespace />actualizarEmpresasAdjudicacion();

        if (<portlet:namespace />popupEmpresaCotizacion
                && typeof Liferay.Popup.close == 'function') {

            Liferay.Popup.close(
                    <portlet:namespace />popupEmpresaCotizacion
            );
        }

        <portlet:namespace />popupEmpresaCotizacion = null;
        <portlet:namespace />filaEmpresaCotizacion = null;

        return false;
    }
    <% } %>

    function <portlet:namespace />reindexarFilasPresupuesto() {
        var rows =
                jQuery(
                        '#<portlet:namespace />presupuestos_body > tr'
                );

        rows.each(function(index) {
            var row =
                    jQuery(this);

            <% if (cotizacionEmpresaPresupuestos) { %>
            var empresaCuit =
                    row.find('input.presupuesto-empresa-cuit');

            var empresaSucursal =
                    row.find('input.presupuesto-empresa-sucursal');
            <% } else { %>
            var prestador =
                    row.find('select.presupuesto-prestador');
            <% } %>

            var archivo =
                    row.find(
                            'input.presupuesto-archivo'
                    );

            var botonSubir =
                    row.find(
                            'input.presupuesto-subir'
                    );

            var botonAgregar =
                    row.find(
                            'input.presupuesto-agregar'
                    );

            <% if (cotizacionEmpresaPresupuestos) { %>
            row.find('input.presupuesto-empresa-descripcion').attr(
                    'name', '<portlet:namespace />presupuesto_' + index + '_descripcion_empresa');
            empresaCuit.attr(
                    'name',
                    '<portlet:namespace />presupuesto_'
                            + index
                            + '_empresa_cuit'
            );

            empresaCuit.attr(
                    'id',
                    '<portlet:namespace />presupuesto_'
                            + index
                            + '_empresa_cuit'
            );

            empresaSucursal.attr(
                    'name',
                    '<portlet:namespace />presupuesto_'
                            + index
                            + '_empresa_sucursal'
            );

            empresaSucursal.attr(
                    'id',
                    '<portlet:namespace />presupuesto_'
                            + index
                            + '_empresa_sucursal'
            );
            <% } else { %>
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
            <% } %>

            archivo.attr(
                    'name',
                    'presupuesto_' + index
            );

            archivo.attr(
                    'id',
                    '<portlet:namespace />presupuesto_' + index
            );

            if (index == 0) {
                <% if (!altaEmpresaPresupuestos) { %>botonSubir.show();<% } %>

                if (rows.length
                        < <%= maxPresupuestosCargaActual %>) {
                    botonAgregar.show();
                } else {
                    botonAgregar.hide();
                }
            } else {
                botonSubir.hide();
                botonAgregar.hide();
            }
        });

        jQuery(
                '#<portlet:namespace />presupuesto_count'
        ).val(rows.length);
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
                tbody.children('tr').length;

        if (cantidad >= <%= maxPresupuestosCargaActual %>) {
            alert(
                    'Se pueden cargar hasta '
                            + '<%= maxPresupuestosCargaActual %>'
                            + ' <%= cotizacionEmpresaPresupuestos
                                    ? "cotizaciones"
                                    : "presupuestos" %> por operación.'
            );
            return false;
        }

        var row = jQuery('<tr></tr>');
        var contraparte = null;

        <% if (cotizacionEmpresaPresupuestos) { %>
        var empresaCuit =
                jQuery(
                        '<input type="text" maxlength="11" size="13" '
                                + 'class="presupuesto-empresa-cuit" />'
                );

        var empresaSucursal =
                jQuery(
                        '<input type="hidden" '
                                + 'class="presupuesto-empresa-sucursal" />'
                );

        var empresaSeleccionada =
                jQuery(
                        '<div class="presupuesto-empresa-seleccionada"></div>'
                );

        var buscarEmpresa =
                jQuery(
                        '<input type="button" '
                                + 'value="Buscar" '
                                + 'title="Buscar Empresa" />'
                );

        buscarEmpresa.click(function() {
            return <portlet:namespace />abrirBusquedaEmpresaCotizacion(row);
        });

        var empresaDescripcion = jQuery('<input type="text" maxlength="200" size="30" class="presupuesto-empresa-descripcion" />');
        empresaDescripcion.change(function() {
            <portlet:namespace />actualizarEmpresasAdjudicacion();
        });
        empresaCuit.change(function() {
            empresaSucursal.val('');
            empresaSeleccionada.text('');
            <portlet:namespace />actualizarEmpresasAdjudicacion();
        });
        var datosEmpresa = jQuery('<table class="lfr-table"></table>');
        var filaNombre = jQuery('<tr><td><label>Nombre (obligatorio):</label></td><td></td></tr>');
        filaNombre.children('td').eq(1).append(empresaDescripcion);
        datosEmpresa.append(filaNombre);
        var filaCuit = jQuery('<tr><td><label>CUIT (opcional):</label></td><td></td></tr>');
        filaCuit.children('td').eq(1).append(empresaCuit).append(' ').append(buscarEmpresa);
        datosEmpresa.append(filaCuit);
        contraparte = jQuery('<div></div>');
        contraparte.append(datosEmpresa);
        contraparte.append(empresaSeleccionada);
        contraparte.append(empresaSucursal);
        <% } else { %>
        var prestador =
                jQuery(
                        '#<portlet:namespace />prestador_presupuesto_template'
                ).clone();

        prestador.removeAttr('id');
        prestador.removeAttr('style');
        prestador.addClass('presupuesto-prestador');
        contraparte = prestador;
        <% } %>

        var archivo =
                jQuery(
                        '<input '
                                + 'type="file" '
                                + 'class="presupuesto-archivo" '
                                + 'accept=".pdf,application/pdf" '
                                + '/>'
                );

        archivo.change(function() {
            <portlet:namespace />validarTamanoArchivoPresupuesto(this);
            <% if (altaEmpresaPresupuestos) { %>
            <portlet:namespace />actualizarEmpresasAdjudicacion();
            <% } %>
        });

        var ayudaArchivo =
                jQuery(
                        '<div class="compras-ayuda-campo">'
                                + 'Formatos permitidos: PDF.'
                                <% if (maximoTamanoPresupuesto != Long.MAX_VALUE) { %>
                                + ' <span class="presupuesto-ayuda-tamano">'
                                + 'Tamaño máximo por archivo: '
                                + '<%= maximoTamanoPresupuestoTexto %>.</span>'
                                <% } %>
                                + '</div>'
                );

        <% if (cotizacionEmpresaPresupuestos) { %>
        var subir =
                jQuery(
                        '<input '
                                + 'type="button" '
                                + 'class="presupuesto-subir" '
                                + 'value="Subir" '
                                + 'title="Subir <%= cotizacionEmpresaPresupuestos
                                        ? "cotizaciones"
                                        : "presupuestos" %>" '
                                + '/>'
                );

        subir.click(function() {
            return <portlet:namespace />uploadPresupuestoRequerimientoCompra();
        });

        var borrar =
                jQuery(
                        '<input '
                                + 'type="button" '
                                + 'class="presupuesto-borrar" '
                                + 'value="Borrar" '
                                + 'title="Quitar esta fila de presupuesto" '
                                + '/>'
                );

        borrar.click(function() {
            <% if (altaEmpresaPresupuestos) { %>
            jQuery('#<portlet:namespace />empresa_adjudicada_indice').val('-1');
            <% } %>
            jQuery(this)
                    .parents('tr')
                    .eq(0)
                    .remove();

            if (tbody.children('tr').length == 0 && !<%= altaEmpresaPresupuestos ? "true" : "false" %>) {
                <portlet:namespace />agregarFilaPresupuesto();
            } else {
                <portlet:namespace />reindexarFilasPresupuesto();
            }
            <portlet:namespace />actualizarEmpresasAdjudicacion();

            return false;
        });

        var agregar =
                jQuery(
                        '<input '
                                + 'type="button" '
                                + 'class="presupuesto-agregar" '
                                + 'value="Agregar otr<%= cotizacionEmpresaPresupuestos
                                        ? "a cotización"
                                        : "o presupuesto" %>" '
                                + 'title="Agregar otra fila de <%=
                                        cotizacionEmpresaPresupuestos
                                                ? "cotización"
                                                : "presupuesto" %>" '
                                + '/>'
                );

        agregar.click(function() {
            return <portlet:namespace />agregarFilaPresupuesto();
        });

        var acciones =
                jQuery(
                        '<td class="presupuesto-acciones"></td>'
                );

        <% if (!altaEmpresaPresupuestos) { %>acciones.append(subir);<% } %>
        acciones.append(document.createTextNode(' '));
        acciones.append(borrar);
        acciones.append(document.createTextNode(' '));
        acciones.append(agregar);
        <% } %>

        row.append(
                jQuery(
                        '<td class="presupuesto-campo-contraparte"></td>'
                ).append(contraparte)
        );

        row.append(
                jQuery(
                        '<td class="presupuesto-campo-archivo"></td>'
                ).append(archivo).append(ayudaArchivo)
        );

        <% if (cotizacionEmpresaPresupuestos) { %>
        row.append(acciones);
        <% } else { %>
        prestador.change(function() {
            archivo.val('');
            <portlet:namespace />cargarComparativaPrestador(prestador.val());
        });
        <% } %>
        tbody.append(row);

        <portlet:namespace />reindexarFilasPresupuesto();
        return false;
    }

    function <portlet:namespace />validarFilasPresupuesto(permitirVacio) {
        var rows =
                jQuery(
                        '#<portlet:namespace />presupuestos_body > tr'
                );

        if (rows.length < (permitirVacio ? 0 : 1)
                || rows.length > <%= maxPresupuestosCargaActual %>) {
            alert('La cantidad de presupuestos no es válida.');
            return false;
        }

        var valido = true;
        var contrapartesSeleccionadas = {};

        rows.each(function(index) {
            var row = jQuery(this);

            <% if (cotizacionEmpresaPresupuestos) { %>
            var empresaCuit =
                    jQuery.trim(
                            row.find('input.presupuesto-empresa-cuit').val()
                    );

            var empresaSucursal =
                    jQuery.trim(
                            row.find(
                                    'input.presupuesto-empresa-sucursal'
                            ).val()
                    );

            var empresaDescripcion = jQuery.trim(row.find('input.presupuesto-empresa-descripcion').val() || '');
            var claveContraparte = empresaCuit != ''
                    ? empresaCuit + '|' + empresaSucursal : 'fila|' + index;
            <% } else { %>
            var prestador =
                    jQuery.trim(
                            row.find('select.presupuesto-prestador').val()
                    );

            var claveContraparte = prestador;
            <% } %>

            var archivo =
                    row.find(
                            'input.presupuesto-archivo'
                    );

            <% if (cotizacionEmpresaPresupuestos) { %>
            if (empresaDescripcion == '') {
                alert(
                        'Debe informar el nombre de la Empresa de la cotización '
                                + (index + 1)
                                + '.'
                );
                valido = false;
                return false;
            }
            if (empresaCuit != '' && (!/^[0-9]{11}$/.test(empresaCuit)
                    || (typeof validarCuil == 'function'
                        && !validarCuil(empresaCuit, 'El CUIT no es válido.')))) {
                alert('El CUIT de la cotización ' + (index + 1) + ' no es válido.');
                valido = false;
                return false;
            }
            <% } else { %>
            if (prestador == '') {
                alert(
                        'Debe seleccionar el prestador del presupuesto '
                                + (index + 1)
                                + '.'
                );
                valido = false;
                return false;
            }
            <% } %>

            if (contrapartesSeleccionadas[claveContraparte]) {
                alert(
                        '<%= cotizacionEmpresaPresupuestos
                                ? "La Empresa de la cotización "
                                : "El prestador del presupuesto " %>'
                                + (index + 1)
                                + ' está repetido. Sólo puede cargarse '
                                + 'un archivo por <%= cotizacionEmpresaPresupuestos
                                        ? "Empresa"
                                        : "prestador" %>.'
                );
                valido = false;
                return false;
            }

            contrapartesSeleccionadas[claveContraparte] = true;

            if (archivo.length == 0 || archivo.val() == '') {
                alert(
                        '<%= cotizacionEmpresaPresupuestos
                                ? "Debe seleccionar el archivo de la cotización "
                                : "Debe seleccionar el archivo del presupuesto " %>'
                                + (index + 1)
                                + '.'
                );
                valido = false;
                return false;
            }

            var nombreArchivo =
                    jQuery.trim(archivo.val());

            if (!/\.pdf$/i.test(nombreArchivo)) {
                alert(
                        '<%= cotizacionEmpresaPresupuestos
                                ? "El archivo de la cotización "
                                : "El archivo del presupuesto " %>'
                                + (index + 1)
                                + ' debe estar en formato PDF.'
                );
                valido = false;
                return false;
            }

            if (!<portlet:namespace />validarTamanoArchivoPresupuesto(
                    archivo.get(0))) {
                valido = false;
                return false;
            }
        });

        if (!valido) {
            return false;
        }

        return true;
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

        if (!form || !accion || !idPresupuesto) {
            alert(
                    'No se pudo preparar la subida del presupuesto.'
            );
            return false;
        }

        <% if (!cotizacionEmpresaPresupuestos) { %>
        var idPrestador = jQuery('#<portlet:namespace />presupuesto_0_id_prestador').val();
        var seccion = jQuery('#<portlet:namespace />comparativaPrestadorSeccion');
        var prestadorCargado = seccion.find(
                'input[name="<portlet:namespace />prestador_' + idPrestador + '"]').val();
        if (!idPrestador || prestadorCargado != idPrestador) {
            alert('Seleccione un prestador y espere a que se carguen sus datos.');
            return false;
        }
        var archivo = jQuery('#<portlet:namespace />presupuesto_0').val();
        if (!archivo && seccion.find('#<portlet:namespace />comparativaTieneArchivo').val() != 'true') {
            alert('Debe seleccionar el PDF del primer presupuesto.');
            return false;
        }
        if (archivo && !/\.pdf$/i.test(archivo)) {
            alert('El archivo debe estar en formato PDF.');
            return false;
        }
        if (!<portlet:namespace />validarTamanoArchivoPresupuesto(
                document.getElementById('<portlet:namespace />presupuesto_0'))) {
            return false;
        }
        var boton = seccion.find('#<portlet:namespace />guardarComparativaBoton');
        if (boton.attr('disabled')) { return false; }
        boton.attr('disabled', 'disabled');
        accion.value = 'guardarComparativa';
        idPresupuesto.value = '';
        <portlet:namespace />reindexarFilasPresupuesto();
        form.submit();
        return false;
        <% } %>

        if (!<portlet:namespace />validarFilasPresupuesto(false)) {
            return false;
        }

        accion.value = '<%= Constants.ADD %>';
        idPresupuesto.value = '';

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
                    '<%= cotizacionEmpresaPresupuestos
                            ? "No se pudo preparar la eliminación de la cotización."
                            : "No se pudo preparar la eliminación del presupuesto." %>'
            );
            return false;
        }

        if (!confirm(
                '<%= cotizacionEmpresaPresupuestos
                        ? "¿Está seguro de eliminar esta cotización?"
                        : "¿Está seguro de eliminar este presupuesto?" %>'
        )) {
            return false;
        }

        accion.value = '<%= Constants.DELETE %>';
        idPresupuesto.value = String(idNumerico);
        form.submit();

        return false;
    }

    <% if (!cotizacionEmpresaPresupuestos) { %>
    function <portlet:namespace />cargarComparativaPrestador(idPrestador) {
        var seccion = jQuery('#<portlet:namespace />comparativaPrestadorSeccion');
        seccion.empty();
        if (!idPrestador) { return; }
        seccion.html('<div class="portlet-msg-info">Cargando presupuesto...</div>');
        jQuery.ajax({
            url: '<%= comparativaPrestadorURL.toString() %>',
            data: { '<portlet:namespace />id_prestador': idPrestador },
            success: function(html) {
                if (jQuery('#<portlet:namespace />presupuesto_0_id_prestador').val() == idPrestador) {
                    seccion.html(html);
                }
            },
            error: function() {
                if (jQuery('#<portlet:namespace />presupuesto_0_id_prestador').val() == idPrestador) {
                    seccion.html('<div class="portlet-msg-error">No se pudo cargar el presupuesto.</div>');
                }
            }
        });
    }

    function <portlet:namespace />limpiarPresupuestoComparativa() {
        jQuery('#<portlet:namespace />presupuesto_0_id_prestador').val('');
        jQuery('#<portlet:namespace />presupuesto_0').val('');
        jQuery('#<portlet:namespace />comparativaPrestadorSeccion').empty();
        return false;
    }
    <% } %>

    jQuery(function() {
        <% if (puedeEditarPresupuestos
                && (
                        cotizacionEmpresaPresupuestos
                        || (
                                hayPrestadoresDisponiblesPresupuestos
                                && WebKeysCompras.isEmpty(
                                        errorPrestadoresPresupuestos
                                )
                        )
                )) { %>

            <% if (!altaEmpresaPresupuestos) { %>
            <portlet:namespace />agregarFilaPresupuesto();
            <% } else { %>
            jQuery('#<portlet:namespace />empresas_recuperadas span').each(function() {
                <portlet:namespace />agregarFilaPresupuesto();
                var origen = jQuery(this);
                var fila = jQuery('#<portlet:namespace />presupuestos_body > tr:last');
                fila.find('input.presupuesto-empresa-cuit').val(origen.attr('data-cuit'));
                fila.find('input.presupuesto-empresa-sucursal').val(origen.attr('data-sucursal'));
                fila.find('input.presupuesto-empresa-descripcion').val(origen.attr('data-descripcion'));
                var sucursalRecuperada = origen.attr('data-sucursal') || '';
                fila.find('.presupuesto-empresa-seleccionada').text(
                        sucursalRecuperada ? 'Sucursal: ' + sucursalRecuperada : '');
            });
            <portlet:namespace />actualizarCotizacionesEmpresaSector(false);
            <% } %>
            <% if (!cotizacionEmpresaPresupuestos
                    && ParamUtil.getInteger(renderRequest, "prestador_comparativa") > 0) { %>
            jQuery('#<portlet:namespace />presupuesto_0_id_prestador')
                    .val('<%= ParamUtil.getInteger(renderRequest, "prestador_comparativa") %>').change();
            <% } %>
        <% } %>
    });
</script>
