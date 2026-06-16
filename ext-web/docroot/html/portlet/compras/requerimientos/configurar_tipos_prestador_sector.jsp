<%@ include file="/html/portlet/compras/init.jsp" %>



<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) renderRequest.getAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO
        );

if (sectores == null) {
    sectores = new ArrayList<RequerimientoCompraSector>();
}

Integer idSectorAttribute =
        (Integer) renderRequest.getAttribute(
                WebKeysCompras.ID_SECTOR_CONFIGURACION_COTIZACION
        );

int idSectorSeleccionado =
        idSectorAttribute != null
                ? idSectorAttribute.intValue()
                : 0;

List<TipoPrestadorSector> tiposPrestador =
        (List<TipoPrestadorSector>) renderRequest.getAttribute(
                WebKeysCompras.TIPOS_PRESTADOR_SECTOR
        );

if (tiposPrestador == null) {
    tiposPrestador = new ArrayList<TipoPrestadorSector>();
}

boolean configuracionActualizada =
        com.liferay.portal.kernel.servlet.SessionMessages.contains(
                renderRequest,
                "configuracion-cotizaciones-actualizada"
        );

boolean configuracionError =
        com.liferay.portal.kernel.servlet.SessionErrors.contains(
                renderRequest,
                "configuracion-cotizaciones-error"
        );

Object errorConfiguracionObject =
        com.liferay.portal.kernel.servlet.SessionErrors.get(
                renderRequest,
                "configuracion-cotizaciones-error"
        );

String errorConfiguracion =
        errorConfiguracionObject != null
                ? String.valueOf(errorConfiguracionObject)
                : null;

PortletURL guardarConfiguracionURL =
        renderResponse.createActionURL();

guardarConfiguracionURL.setWindowState(
        LiferayWindowState.MAXIMIZED
);

guardarConfiguracionURL.setParameter(
        "struts_action",
        "/compras/configurar_tipos_prestador_sector"
);

PortletURL volverURL =
        renderResponse.createRenderURL();

volverURL.setWindowState(
        LiferayWindowState.MAXIMIZED
);

volverURL.setParameter(
        "struts_action",
        "/compras/view"
);

String namespaceConfiguracion =
        PortalUtil.getPortletNamespace(
                portletDisplay.getId()
        );

int cantidadActivos = 0;

for (int i = 0; i < tiposPrestador.size(); i++) {
    TipoPrestadorSector tipo = tiposPrestador.get(i);

    if (tipo != null && tipo.isActivo()) {
        cantidadActivos++;
    }
}
%>

<style type="text/css">
    .compras-configuracion-cotizaciones {
        max-width: 950px;
    }

    .compras-configuracion-cotizaciones .configuracion-resumen {
        margin: 10px 0 15px 0;
        padding: 10px;
        background-color: #f5f5f5;
        border: 1px solid #d5d5d5;
    }

    .compras-configuracion-cotizaciones .configuracion-tipos {
        width: 100%;
        border-collapse: collapse;
        margin-top: 10px;
    }

    .compras-configuracion-cotizaciones .configuracion-tipos th,
    .compras-configuracion-cotizaciones .configuracion-tipos td {
        padding: 7px 10px;
        border-bottom: 1px solid #dddddd;
        text-align: left;
        vertical-align: middle;
    }

    .compras-configuracion-cotizaciones .configuracion-tipos th {
        background-color: #eeeeee;
    }

    .compras-configuracion-cotizaciones .configuracion-check {
        width: 90px;
        text-align: center !important;
    }

    .compras-configuracion-cotizaciones .configuracion-botonera {
        margin-top: 18px;
    }

    .compras-configuracion-cotizaciones .configuracion-ayuda {
        margin: 8px 0 15px 0;
        color: #555555;
    }
</style>

<div class="compras-configuracion-cotizaciones">

    <h2>Configuraci&oacute;n de cotizaciones por sector</h2>

    <p class="configuracion-ayuda">
        Seleccione un sector y marque los tipos de prestador que podr&aacute;n
        recibir solicitudes de cotizaci&oacute;n para sus requerimientos.
    </p>

    <% if (configuracionActualizada) { %>
        <div class="portlet-msg-success">
            La configuraci&oacute;n de cotizaciones fue actualizada correctamente.
        </div>
    <% } %>

    <% if (configuracionError) { %>
        <div class="portlet-msg-error">
            <strong>No se pudo guardar la configuraci&oacute;n.</strong>

            <% if (!WebKeysCompras.isEmpty(errorConfiguracion)) { %>
                <br />
                <%= HtmlUtil.escape(errorConfiguracion) %>
            <% } %>
        </div>
    <% } %>

    <fieldset class="block-labels">
        <legend>Sector de compras</legend>

        <table class="lfr-table">
            <tr>
                <td>
                    <label for="<portlet:namespace />id_sector_selector">
                        Sector:
                    </label>
                </td>

                <td>
                    <select id="<portlet:namespace />id_sector_selector"
                            onchange="<%= namespaceConfiguracion %>cambiarSector(this);">

                        <% if (sectores.isEmpty()) { %>
                            <option value="">
                                No existen sectores configurados
                            </option>
                        <% } %>

                        <%
                        for (int i = 0; i < sectores.size(); i++) {
                            RequerimientoCompraSector sector =
                                    sectores.get(i);

                            if (sector == null
                                    || sector.getIdSector() <= 0) {

                                continue;
                            }

                            int idSector = sector.getIdSector();

                            PortletURL sectorURL =
                                    renderResponse.createRenderURL();

                            sectorURL.setWindowState(
                                    LiferayWindowState.MAXIMIZED
                            );

                            sectorURL.setParameter(
                                    "struts_action",
                                    "/compras/configurar_tipos_prestador_sector"
                            );

                            sectorURL.setParameter(
                                    "id_sector",
                                    String.valueOf(idSector)
                            );
                        %>
                            <option
                                    value="<%= HtmlUtil.escape(sectorURL.toString()) %>"
                                    <%= idSector == idSectorSeleccionado
                                            ? "selected=\"selected\""
                                            : "" %>>

                                <%= HtmlUtil.escape(
                                        sector.getDescripcionVisible()
                                ) %>
                            </option>
                        <%
                        }
                        %>
                    </select>
                </td>
            </tr>
        </table>
    </fieldset>

    <% if (idSectorSeleccionado > 0) { %>

        <form action="<%= guardarConfiguracionURL.toString() %>"
              method="post"
              id="<portlet:namespace />guardarConfiguracionCotizacionesForm"
              onsubmit="return <%= namespaceConfiguracion %>confirmarGuardado();">

            <input type="hidden"
                   name="<portlet:namespace />id_sector"
                   value="<%= idSectorSeleccionado %>" />

            <fieldset class="block-labels">
                <legend>Tipos de prestador habilitados</legend>

                <div class="configuracion-resumen">
                    Tipos disponibles:
                    <strong><%= tiposPrestador.size() %></strong>

                    &nbsp;&nbsp;|&nbsp;&nbsp;

                    Tipos habilitados:
                    <strong id="<portlet:namespace />cantidadTiposActivos">
                        <%= cantidadActivos %>
                    </strong>
                </div>

                <% if (tiposPrestador.isEmpty()) { %>

                    <div class="portlet-msg-info">
                        No existen tipos de prestador disponibles para configurar.
                    </div>

                <% } else { %>

                    <div>
                        <input type="button"
                               value="Marcar todos"
                               onclick="<%= namespaceConfiguracion %>marcarTodos(true);" />

                        &nbsp;

                        <input type="button"
                               value="Desmarcar todos"
                               onclick="<%= namespaceConfiguracion %>marcarTodos(false);" />
                    </div>

                    <table class="configuracion-tipos">
                        <thead>
                            <tr>
                                <th class="configuracion-check">
                                    Habilitado
                                </th>

                                <th>
                                    Tipo de prestador
                                </th>

                                <th>
                                    ID
                                </th>
                            </tr>
                        </thead>

                        <tbody>
                            <%
                            for (int i = 0; i < tiposPrestador.size(); i++) {
                                TipoPrestadorSector tipo =
                                        tiposPrestador.get(i);

                                if (tipo == null) {
                                    continue;
                                }
                            %>
                                <tr>
                                    <td class="configuracion-check">
                                        <input type="checkbox"
                                               class="<portlet:namespace />tipoPrestadorCheckbox"
                                               id="<portlet:namespace />tipo_prestador_<%= tipo.getIdTipoPrestador() %>"
                                               name="<portlet:namespace />id_tipo_prestador"
                                               value="<%= tipo.getIdTipoPrestador() %>"
                                               <%= tipo.isActivo()
                                                       ? "checked=\"checked\""
                                                       : "" %>
                                               onclick="<%= namespaceConfiguracion %>actualizarCantidad();" />
                                    </td>

                                    <td>
                                        <label for="<portlet:namespace />tipo_prestador_<%= tipo.getIdTipoPrestador() %>">
                                            <%= HtmlUtil.escape(
                                                    tipo.getDescripcionVisible()
                                            ) %>
                                        </label>
                                    </td>

                                    <td>
                                        <%= tipo.getIdTipoPrestador() %>
                                    </td>
                                </tr>
                            <%
                            }
                            %>
                        </tbody>
                    </table>

                <% } %>

                <div class="configuracion-botonera">

                    <% if (!tiposPrestador.isEmpty()) { %>
                        <input type="submit"
                               id="<portlet:namespace />guardarConfiguracionCotizaciones"
                               value="Guardar configuraci&oacute;n" />

                        &nbsp;&nbsp;
                    <% } %>

                    <input type="button"
                           value="Volver"
                           onclick="window.location.href='<%= volverURL.toString() %>';" />
                </div>
            </fieldset>
        </form>

    <% } else { %>

        <div class="portlet-msg-info">
            Debe seleccionar un sector para configurar sus tipos de prestador.
        </div>

        <input type="button"
               value="Volver"
               onclick="window.location.href='<%= volverURL.toString() %>';" />

    <% } %>
</div>

<script type="text/javascript">

        function <%= namespaceConfiguracion %>cambiarSector(selector) {
            if (selector == null) {
                return;
            }

            var url = selector.value;

            if (url == null || url.length == 0) {
                return;
            }

            window.location.href = url;
        }

        function <%= namespaceConfiguracion %>obtenerCheckboxes() {
            var elementos = document.getElementsByTagName('input');
            var resultado = [];

            for (var i = 0; i < elementos.length; i++) {
                var elemento = elementos[i];

                if (elemento.type == 'checkbox'
                        && elemento.className.indexOf(
                                '<portlet:namespace />tipoPrestadorCheckbox'
                        ) >= 0) {

                    resultado.push(elemento);
                }
            }

            return resultado;
        }

    function <%= namespaceConfiguracion %>obtenerCheckboxes() {
        var elementos = document.getElementsByTagName('input');
        var resultado = [];

        for (var i = 0; i < elementos.length; i++) {
            var elemento = elementos[i];

            if (elemento.type == 'checkbox'
                    && elemento.className.indexOf(
                            '<portlet:namespace />tipoPrestadorCheckbox'
                    ) >= 0) {

                resultado.push(elemento);
            }
        }

        return resultado;
    }

    function <%= namespaceConfiguracion %>marcarTodos(marcar) {
        var checkboxes =
                <%= namespaceConfiguracion %>obtenerCheckboxes();

        for (var i = 0; i < checkboxes.length; i++) {
            checkboxes[i].checked = marcar;
        }

        <%= namespaceConfiguracion %>actualizarCantidad();
    }

    function <%= namespaceConfiguracion %>actualizarCantidad() {
        var checkboxes =
                <%= namespaceConfiguracion %>obtenerCheckboxes();

        var cantidad = 0;

        for (var i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].checked) {
                cantidad++;
            }
        }

        var contador = document.getElementById(
                '<portlet:namespace />cantidadTiposActivos'
        );

        if (contador != null) {
            contador.innerHTML = cantidad;
        }
    }

    function <%= namespaceConfiguracion %>confirmarGuardado() {
        var checkboxes =
                <%= namespaceConfiguracion %>obtenerCheckboxes();

        var cantidadSeleccionada = 0;

        for (var i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].checked) {
                cantidadSeleccionada++;
            }
        }

        var mensaje =
                'Confirma guardar la configuracion de cotizaciones para el sector seleccionado?';

        if (cantidadSeleccionada == 0) {
            mensaje =
                    'No selecciono ningun tipo de prestador. '
                    + 'Esto deshabilitara todos los tipos para el sector. '
                    + 'Confirma continuar?';
        }

        return confirm(mensaje);
    }

</script>