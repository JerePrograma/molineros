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

boolean puedeEditarPresupuestos =
        idRequerimientoCompraPresupuestos > 0
        && puedeCotizarPresupuestos
        && reqPresupuestos.puedeAdministrarPresupuestos()
        && !soloLecturaPresupuestos;

boolean cotizacionEmpresaPresupuestos =
        reqPresupuestos.esSectorSinCotizacionPrestador();

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

int maxPresupuestosCargaActual =
        cotizacionEmpresaPresupuestos
                ? WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA
                : Math.min(
                        WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA,
                        prestadoresDisponiblesPresupuestos.size()
                );

PortletURL uploadPresupuestosURL =
        renderResponse.createActionURL();

uploadPresupuestosURL.setWindowState(
        WindowState.MAXIMIZED
);

uploadPresupuestosURL.setParameter(
        "struts_action",
        "/compras/upload_presupuestos_requerimiento"
);

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

String modoRetornoPresupuestos =
        soloLecturaPresupuestos
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
        vertical-align: middle;
    }

    #<portlet:namespace />tabla_carga_presupuestos td {
        vertical-align: middle;
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

        <c:if test="<%= idRequerimientoCompraPresupuestos <= 0 %>">
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
                && hayPrestadoresEnviadosPresupuestos
                && !hayPrestadoresDisponiblesPresupuestos
                && WebKeysCompras.isEmpty(
                        errorPrestadoresPresupuestos
                )
        %>">
            <div class="portlet-msg-info">
                Todos los prestadores notificados ya tienen un presupuesto
                cargado. Para reemplazar uno, primero debe eliminar el archivo
                existente.
            </div>
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
                    <col style="width: 25%;" />
                </colgroup>

                <thead>
                    <tr>
                        <th>Prestador enviado</th>
                        <th>Archivo</th>
                        <th>Acciones</th>
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

        <c:if test="<%=
                idRequerimientoCompraPresupuestos > 0
                && !puedeEditarPresupuestos
                && !soloLecturaPresupuestos
        %>">
            <div class="portlet-msg-info">
                <%= cotizacionEmpresaPresupuestos
                        ? "Las cotizaciones de empresas solo pueden administrarse mientras el requerimiento está PENDIENTE y con rol de cotización."
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
        - El requerimiento debe estar guardado, pertenecer a RRHH o SISTEMAS
          y permanecer PENDIENTE.
        <br />

        - Debe buscar y seleccionar una Empresa activa del padrón de
          Empleadores antes de elegir el PDF.
        <br />

        - Una misma Empresa no puede repetirse dentro de la carga ni tener
          otra cotización activa para el requerimiento.
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

        - Debe seleccionar un archivo no vacío.
        <br />

        - Un mismo prestador no puede repetirse dentro de la misma carga.
        <br />

        - Sólo puede existir un presupuesto activo por prestador.
          Para reemplazarlo, primero debe eliminar el presupuesto existente.
        <br />

        - Puede utilizar "Agregar otro presupuesto" para cargar varios
          presupuestos en una misma operación.
        <br />

        - La cantidad máxima de presupuestos de una operación depende
          de los prestadores disponibles y del máximo configurado por el sistema.
        <br />

        - El archivo debe respetar el tamaño máximo permitido por
          Document Library.
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
</form>

<script type="text/javascript">
    <% if (cotizacionEmpresaPresupuestos) { %>
    var <portlet:namespace />popupEmpresaCotizacion = null;
    var <portlet:namespace />filaEmpresaCotizacion = null;

    function <portlet:namespace />abrirBusquedaEmpresaCotizacion(row) {
        <portlet:namespace />filaEmpresaCotizacion = row;

        <portlet:namespace />popupEmpresaCotizacion = Liferay.Popup({
            title: 'Búsqueda de Empresas',
            modal: true,
            width: 700
        });

        jQuery(<portlet:namespace />popupEmpresaCotizacion).load(
                '<%= buscarEmpresasCotizacionURL.toString() %>'
        );

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

        row.find('input.presupuesto-empresa-cuit').val(cuit);
        row.find('input.presupuesto-empresa-sucursal').val(sucursal);
        row.find('.presupuesto-empresa-seleccionada').text(
                razonSocial
                        + ' - CUIT: '
                        + cuit
                        + ' - Sucursal: '
                        + sucursal
        );

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
                        '#<portlet:namespace />presupuestos_body tr'
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
                botonSubir.show();

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
                tbody.find('tr').length;

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
                        '<input type="hidden" '
                                + 'class="presupuesto-empresa-cuit" />'
                );

        var empresaSucursal =
                jQuery(
                        '<input type="hidden" '
                                + 'class="presupuesto-empresa-sucursal" />'
                );

        var empresaSeleccionada =
                jQuery(
                        '<div class="presupuesto-empresa-seleccionada">'
                                + 'Sin Empresa seleccionada.'
                                + '</div>'
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

        contraparte = jQuery('<div></div>');
        contraparte.append(buscarEmpresa);
        contraparte.append(empresaSeleccionada);
        contraparte.append(empresaCuit);
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

        var ayudaArchivo =
                jQuery(
                        '<div class="compras-ayuda-campo">'
                                + 'Formatos permitidos: PDF'
                                + '</div>'
                );

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
            jQuery(this)
                    .parents('tr')
                    .eq(0)
                    .remove();

            if (tbody.find('tr').length == 0) {
                <portlet:namespace />agregarFilaPresupuesto();
            } else {
                <portlet:namespace />reindexarFilasPresupuesto();
            }

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

        acciones.append(subir);
        acciones.append(document.createTextNode(' '));
        acciones.append(borrar);
        acciones.append(document.createTextNode(' '));
        acciones.append(agregar);

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

        row.append(acciones);
        tbody.append(row);

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

        if (!form || !accion || !idPresupuesto) {
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

            var claveContraparte =
                    empresaCuit + '|' + empresaSucursal;
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
            if (empresaCuit == '' || empresaSucursal == '') {
                alert(
                        'Debe buscar y seleccionar la Empresa de la cotización '
                                + (index + 1)
                                + '.'
                );
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
        });

        if (!valido) {
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

            <portlet:namespace />agregarFilaPresupuesto();
        <% } %>
    });
</script>
