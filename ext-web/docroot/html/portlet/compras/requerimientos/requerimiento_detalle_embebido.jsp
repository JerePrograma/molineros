<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>

<portlet:defineObjects />

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
    int idReq = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);

    if (idReq > 0) {
        reqDetalle = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idReq);
    }
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
        && reqDetalle.isEditable();

boolean layoutEdicionDetalle = usuarioPuedeABMDetalle;

boolean puedeABMDetalle =
        layoutEdicionDetalle
        && !soloLecturaDetalle;

String readonlyDetalleVista = soloLecturaDetalle
        ? " readonly=\"readonly\""
        : "";

String bloqueoDetalleVista = soloLecturaDetalle
        ? " class=\"compras-bloqueado-sin-estilo\" tabindex=\"-1\" onmousedown=\"return false;\" onkeydown=\"return false;\" onclick=\"return false;\""
        : "";

List<RequerimientoCompraDetalle> detalles = reqDetalle.getDetalles();

if (detalles == null) {
    detalles = new ArrayList<RequerimientoCompraDetalle>();
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

int detalleColspan = layoutEdicionDetalle ? 7 : 6;
%>

<style type="text/css">
    .compras-bloqueado-sin-estilo {
        pointer-events: none;
    }
</style>

<fieldset class="block-labels">
    <legend>Detalle del requerimiento</legend>

    <% if (layoutEdicionDetalle) { %>
        <fieldset class="block-labels">
            <legend>Agregar / editar detalle</legend>

            <input type="hidden"
                   id="<portlet:namespace />detalle_edit_index"
                   value="-1" />

            <table class="lfr-table" width="100%">
                <tr>
                    <td>
                        <label for="<portlet:namespace />detalle_id_articulo">
                            Art&iacute;culo:
                        </label>
                    </td>
                    <td colspan="3">
                        <select id="<portlet:namespace />detalle_id_articulo"
                                style="min-width: 420px;"
                                <%= bloqueoDetalleVista %>>
                            <option value="">Seleccione...</option>
                        </select>

                        &nbsp;

                        <img alt="Nuevo art&iacute;culo"
                             title="Nuevo art&iacute;culo"
                             align="absmiddle"
                             src="<%= themeDisplay.getPathThemeImages() %>/common/add.png"
                             style="cursor:pointer;"
                             onClick="<%= puedeABMDetalle ? namespaceDetalleCompra + "abrirAltaArticuloCompra();" : "return false;" %>" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td>
                        <label for="<portlet:namespace />detalle_cantidad">
                            Cantidad:
                        </label>
                    </td>
                    <td>
                        <input type="text"
                               id="<portlet:namespace />detalle_cantidad"
                               size="8"
                               value="1"
                               <%= readonlyDetalleVista %> />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td>
                        <label for="<portlet:namespace />detalle_observaciones">
                            Observaciones:
                        </label>
                    </td>
                    <td>
                        <input type="text"
                               id="<portlet:namespace />detalle_observaciones"
                               size="60"
                               maxlength="500"
                               value=""
                               <%= readonlyDetalleVista %> />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td colspan="4" align="center">
                        <input type="button"
                               id="<portlet:namespace />detalle_submit"
                               value="Agregar detalle"
                               onClick="<%= puedeABMDetalle ? "return " + namespaceDetalleCompra + "agregarOActualizarDetalle();" : "return false;" %>" />

                        &nbsp;&nbsp;

                        <input type="button"
                               id="<portlet:namespace />detalle_cancelar"
                               value="Cancelar edici&oacute;n"
                               style="display:none;"
                               onClick="<%= puedeABMDetalle ? "return " + namespaceDetalleCompra + "cancelarEdicionDetalle();" : "return false;" %>" />
                    </td>
                </tr>
            </table>
        </fieldset>

        <br />
    <% } %>

    <table class="lfr-table taglib-search-iterator" width="100%">
        <tr class="portlet-section-header results-header">
            <th>ID</th>
            <th>Art&iacute;culo</th>
            <th>Cantidad</th>
            <th>Observaciones</th>

            <% if (layoutEdicionDetalle) { %>
                <th>Acciones</th>
            <% } %>
        </tr>

        <tbody id="<portlet:namespace />detalle_body">
            <tr class="portlet-section-body results-row">
                <td colspan="<%= detalleColspan %>">
                    Cargando detalles...
                </td>
            </tr>
        </tbody>
    </table>

    <% if (puedeABMDetalle) { %>
        <div id="<portlet:namespace />detalle_payload"></div>
    <% } %>
</fieldset>

<script type="text/javascript">
    var <portlet:namespace />detallesCompra = [];
    var <portlet:namespace />detalleDeletedIds = [];
    var <portlet:namespace />articulosCompraCache = [];

    var <portlet:namespace />detalleAccionEnCurso = false;
    var <portlet:namespace />popupArticuloCompraAbriendo = false;

    <%
    for (int i = 0; i < articulos.size(); i++) {
        CompraArticulo articulo = articulos.get(i);

        String idArticulo = articulo.getId() != null
                ? String.valueOf(articulo.getId().intValue())
                : "";

        String idSectorArticulo = articulo.getIdSector() != null
                ? String.valueOf(articulo.getIdSector().intValue())
                : "";

        String descripcionArticulo = articulo.getDescripcion() != null
                ? articulo.getDescripcion()
                : "";

        if (idArticulo.length() > 0 && descripcionArticulo.length() > 0) {
    %>
            <portlet:namespace />articulosCompraCache.push({
                id: '<%= jsDetalleCompra(idArticulo) %>',
                sector: '<%= jsDetalleCompra(idSectorArticulo) %>',
                descripcion: '<%= jsDetalleCompra(descripcionArticulo) %>'
            });
    <%
        }
    }
    %>

    <%
    for (int i = 0; i < detalles.size(); i++) {
        RequerimientoCompraDetalle detalle = detalles.get(i);

        String idArticuloDetalle = "";

        if (detalle.getIdArticulo() != null) {
            idArticuloDetalle = String.valueOf(detalle.getIdArticulo().intValue());
        }
    %>
        <portlet:namespace />detallesCompra.push({
            id: '<%= jsDetalleCompra(detalle.getIdString()) %>',
            idArticulo: '<%= jsDetalleCompra(idArticuloDetalle) %>',
            articulo: '<%= jsDetalleCompra(detalle.getArticuloVisible()) %>',
            cantidad: '<%= jsDetalleCompra(detalle.getCantidadString()) %>',
            observaciones: '<%= jsDetalleCompra(detalle.getObservacionesVisible()) %>'
        });
    <%
    }
    %>

    function <portlet:namespace />detalleEscapeHtml(value) {
        if (value == null) {
            return '';
        }

        return jQuery('<div/>').text(value).html();
    }

    function <portlet:namespace />detalleValue(value) {
        return value == null ? '' : value;
    }

    function <portlet:namespace />getSectorSeleccionadoCompra() {
        var sector = '';

        var bySectorId = jQuery('#<portlet:namespace />sector_id');

        if (bySectorId.length > 0) {
            sector = jQuery.trim(bySectorId.val());
        }

        if (sector == '' || sector == '0') {
            var byIdSector = jQuery('#<portlet:namespace />id_sector');

            if (byIdSector.length > 0) {
                sector = jQuery.trim(byIdSector.val());
            }
        }

        if (sector == '' || sector == '0') {
            sector = '<%= HtmlUtil.escape(idSectorActualString) %>';
        }

        return sector;
    }

    function <portlet:namespace />agregarOActualizarArticuloCache(idArticulo, descripcion, idSector) {
        idArticulo = idArticulo == null ? '' : String(idArticulo);
        descripcion = descripcion == null ? '' : String(descripcion);
        idSector = idSector == null ? '' : String(idSector);

        if (idArticulo == '') {
            return;
        }

        for (var i = 0; i < <portlet:namespace />articulosCompraCache.length; i++) {
            if (<portlet:namespace />articulosCompraCache[i].id == idArticulo) {
                <portlet:namespace />articulosCompraCache[i].sector = idSector;
                <portlet:namespace />articulosCompraCache[i].descripcion = descripcion;
                return;
            }
        }

        <portlet:namespace />articulosCompraCache.push({
            id: idArticulo,
            sector: idSector,
            descripcion: descripcion
        });
    }

    function <portlet:namespace />filtrarArticulosPorSector() {
        var select = jQuery('#<portlet:namespace />detalle_id_articulo');

        if (select.length == 0) {
            return;
        }

        var sectorSeleccionado = <portlet:namespace />getSectorSeleccionadoCompra();
        var sectorSeleccionadoNum = parseInt(sectorSeleccionado, 10);
        var valorActual = select.val();
        var valorActualPermitido = false;

        select.empty();
        select.append('<option value="">Seleccione...</option>');

        for (var i = 0; i < <portlet:namespace />articulosCompraCache.length; i++) {
            var articulo = <portlet:namespace />articulosCompraCache[i];

            var sectorArticuloNum = parseInt(articulo.sector, 10);

            var mostrar =
                    isNaN(sectorSeleccionadoNum)
                    || sectorSeleccionadoNum <= 0
                    || articulo.sector == ''
                    || (!isNaN(sectorArticuloNum) && sectorArticuloNum == sectorSeleccionadoNum);

            if (mostrar) {
                var option = jQuery('<option></option>');

                option.val(articulo.id);
                option.attr('data-sector', articulo.sector);
                option.text(articulo.descripcion);

                select.append(option);

                if (articulo.id == valorActual) {
                    valorActualPermitido = true;
                }
            }
        }

        if (valorActual != '' && valorActualPermitido) {
            select.val(valorActual);
        } else {
            select.val('');
        }
    }

    window['<portlet:namespace />filtrarArticulosPorSector'] =
            <portlet:namespace />filtrarArticulosPorSector;

    function <portlet:namespace />normalizarImporte(value) {
        value = jQuery.trim(value);

        if (value == '') {
            return null;
        }

        if (value.indexOf(',') >= 0) {
            value = value.replace(/\./g, '').replace(',', '.');
        }

        var parsed = parseFloat(value);

        if (isNaN(parsed)) {
            return null;
        }

        return parsed;
    }

    function <portlet:namespace />renderDetallesCompra() {
        var tbody = jQuery('#<portlet:namespace />detalle_body');

        tbody.empty();

        if (<portlet:namespace />detallesCompra.length == 0) {
            tbody.append(
                '<tr class="portlet-section-body results-row">' +
                    '<td colspan="<%= detalleColspan %>">No hay detalles cargados.</td>' +
                '</tr>'
            );

            return;
        }

        for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
            var detalle = <portlet:namespace />detallesCompra[i];

            var rowClass = (i % 2 == 0)
                    ? 'portlet-section-body results-row'
                    : 'portlet-section-alternate results-row alt';

            var html = '';

            html += '<tr class="' + rowClass + '">';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.id) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.articulo) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.cantidad) + '</td>';
            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.observaciones) + '</td>';

            <% if (layoutEdicionDetalle) { %>
                html += '<td>';

                <% if (puedeABMDetalle) { %>
                    html += '<input type="button" value="Editar" onclick="<portlet:namespace />editarDetalleEnPantalla(' + i + ');" />';
                    html += '&nbsp;';
                    html += '<input type="button" value="Quitar" onclick="<portlet:namespace />quitarDetalleEnPantalla(' + i + ');" />';
                <% } else { %>
                    html += '<input type="button" value="Editar" onclick="return false;" />';
                    html += '&nbsp;';
                    html += '<input type="button" value="Quitar" onclick="return false;" />';
                <% } %>

                html += '</td>';
            <% } %>

            html += '</tr>';

            tbody.append(html);
        }
    }

    <% if (puedeABMDetalle) { %>
        var <portlet:namespace />popupArticuloCompra = null;

        function <portlet:namespace />abrirAltaArticuloCompra() {
            if (<portlet:namespace />popupArticuloCompraAbriendo) {
                return false;
            }

            <portlet:namespace />popupArticuloCompraAbriendo = true;

            var idSector = <portlet:namespace />getSectorSeleccionadoCompra();

            if (idSector == '' || !/^[0-9]+$/.test(idSector) || parseInt(idSector, 10) <= 0) {
                alert('Debe seleccionar un sector antes de cargar un articulo.');
                <portlet:namespace />popupArticuloCompraAbriendo = false;
                return false;
            }

            <portlet:namespace />popupArticuloCompra = Liferay.Popup({
                title: 'Alta de articulo',
                modal: true,
                width: 700
            });

            var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>' +
                    '&struts_action=/compras/alta_articulo_popup' +
                    '&id_sector=' + encodeURIComponent(idSector) +
                    '&callback=' + encodeURIComponent('<portlet:namespace />seleccionarArticuloCompra');

            jQuery(<portlet:namespace />popupArticuloCompra).load(url, function() {
                <portlet:namespace />popupArticuloCompraAbriendo = false;
            });

            return false;
        }

        function <portlet:namespace />seleccionarArticuloCompra(idArticulo, descripcion, idSector) {
            <portlet:namespace />agregarOActualizarArticuloCache(
                    idArticulo,
                    descripcion,
                    idSector
            );

            <portlet:namespace />filtrarArticulosPorSector();

            var select = jQuery('#<portlet:namespace />detalle_id_articulo');

            select.val(idArticulo);

            <portlet:namespace />cerrarAltaArticuloCompra();

            jQuery('#<portlet:namespace />detalle_cantidad').focus();
        }

        function <portlet:namespace />seleccionarArticuloCompraCerrar() {
            <portlet:namespace />cerrarAltaArticuloCompra();
        }

        function <portlet:namespace />cerrarAltaArticuloCompra() {
            <portlet:namespace />popupArticuloCompraAbriendo = false;

            if (<portlet:namespace />popupArticuloCompra) {
                Liferay.Popup.close(<portlet:namespace />popupArticuloCompra);
                <portlet:namespace />popupArticuloCompra = null;
            }
        }

        function <portlet:namespace />limpiarEditorDetalle() {
            jQuery('#<portlet:namespace />detalle_edit_index').val('-1');
            jQuery('#<portlet:namespace />detalle_id_articulo').val('');
            jQuery('#<portlet:namespace />detalle_cantidad').val('1');
            jQuery('#<portlet:namespace />detalle_observaciones').val('');
            jQuery('#<portlet:namespace />detalle_submit').val('Agregar detalle');
            jQuery('#<portlet:namespace />detalle_cancelar').hide();

            <portlet:namespace />filtrarArticulosPorSector();
        }

        function <portlet:namespace />editarDetalleEnPantalla(index) {
            var detalle = <portlet:namespace />detallesCompra[index];

            if (!detalle) {
                return;
            }

            jQuery('#<portlet:namespace />detalle_edit_index').val(index);

            <portlet:namespace />filtrarArticulosPorSector();

            jQuery('#<portlet:namespace />detalle_id_articulo').val(<portlet:namespace />detalleValue(detalle.idArticulo));
            jQuery('#<portlet:namespace />detalle_cantidad').val(<portlet:namespace />detalleValue(detalle.cantidad));
            jQuery('#<portlet:namespace />detalle_observaciones').val(<portlet:namespace />detalleValue(detalle.observaciones));

            jQuery('#<portlet:namespace />detalle_submit').val('Guardar detalle');
            jQuery('#<portlet:namespace />detalle_cancelar').show();

            jQuery('#<portlet:namespace />detalle_id_articulo').focus();
        }

        function <portlet:namespace />cancelarEdicionDetalle() {
            if (<portlet:namespace />detalleAccionEnCurso) {
                return false;
            }

            <portlet:namespace />limpiarEditorDetalle();

            return false;
        }

        function <portlet:namespace />setDetalleAccionEnCurso(activo) {
            <portlet:namespace />detalleAccionEnCurso = activo;

            var botonAgregar = jQuery('#<portlet:namespace />detalle_submit');
            var botonCancelar = jQuery('#<portlet:namespace />detalle_cancelar');

            if (botonAgregar.length > 0) {
                if (activo) {
                    if (botonAgregar.attr('data-texto-original') == null
                            || botonAgregar.attr('data-texto-original') == '') {
                        botonAgregar.attr('data-texto-original', botonAgregar.val());
                    }

                    botonAgregar.attr('disabled', 'disabled');
                    botonAgregar.val('Procesando...');
                } else {
                    botonAgregar.removeAttr('disabled');

                    var textoOriginal = botonAgregar.attr('data-texto-original');

                    if (textoOriginal != null && textoOriginal != '') {
                        botonAgregar.val(textoOriginal);
                    }

                    botonAgregar.removeAttr('data-texto-original');
                }
            }

            if (botonCancelar.length > 0) {
                if (activo) {
                    botonCancelar.attr('disabled', 'disabled');
                } else {
                    botonCancelar.removeAttr('disabled');
                }
            }
        }

        function <portlet:namespace />liberarDetalleAccion(delay) {
            if (typeof delay == 'undefined' || delay == null) {
                delay = 0;
            }

            window.setTimeout(function() {
                <portlet:namespace />setDetalleAccionEnCurso(false);
            }, delay);

            return false;
        }

        function <portlet:namespace />existeDetalleConArticulo(idArticulo, ignorarIndex) {
            idArticulo = idArticulo == null ? '' : String(idArticulo);

            for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
                if (typeof ignorarIndex != 'undefined'
                        && ignorarIndex != null
                        && i == ignorarIndex) {
                    continue;
                }

                var detalle = <portlet:namespace />detallesCompra[i];

                if (detalle != null && String(detalle.idArticulo) == idArticulo) {
                    return true;
                }
            }

            return false;
        }

        function <portlet:namespace />agregarOActualizarDetalle() {
            if (<portlet:namespace />detalleAccionEnCurso) {
                return false;
            }

            var idArticulo = jQuery.trim(jQuery('#<portlet:namespace />detalle_id_articulo').val());
            var articulo = jQuery.trim(jQuery('#<portlet:namespace />detalle_id_articulo option:selected').text());
            var cantidad = jQuery.trim(jQuery('#<portlet:namespace />detalle_cantidad').val());
            var observaciones = jQuery.trim(jQuery('#<portlet:namespace />detalle_observaciones').val());

            if (idArticulo == '' || !/^[0-9]+$/.test(idArticulo) || parseInt(idArticulo, 10) <= 0) {
                alert('Debe seleccionar un articulo.');
                jQuery('#<portlet:namespace />detalle_id_articulo').focus();
                return false;
            }

            if (cantidad == '' || !/^[0-9]+$/.test(cantidad) || parseInt(cantidad, 10) <= 0) {
                alert('La cantidad debe ser entera y mayor a cero.');
                jQuery('#<portlet:namespace />detalle_cantidad').focus();
                return false;
            }

            var editIndex = parseInt(
                    jQuery('#<portlet:namespace />detalle_edit_index').val(),
                    10
            );

            var esEdicion =
                    !isNaN(editIndex)
                    && editIndex >= 0
                    && <portlet:namespace />detallesCompra[editIndex];

            if (<portlet:namespace />existeDetalleConArticulo(
                    idArticulo,
                    esEdicion ? editIndex : -1
            )) {
                alert('Ya existe un detalle cargado para este articulo. Edite la fila existente en lugar de agregar otra.');
                jQuery('#<portlet:namespace />detalle_id_articulo').focus();
                return false;
            }

            <portlet:namespace />setDetalleAccionEnCurso(true);

            var detalle = {
                id: '',
                idArticulo: idArticulo,
                articulo: articulo,
                cantidad: cantidad,
                observaciones: observaciones
            };

            if (esEdicion) {
                detalle.id = <portlet:namespace />detallesCompra[editIndex].id;
                <portlet:namespace />detallesCompra[editIndex] = detalle;
            } else {
                <portlet:namespace />detallesCompra.push(detalle);
            }

            <portlet:namespace />limpiarEditorDetalle();
            <portlet:namespace />renderDetallesCompra();

            return <portlet:namespace />liberarDetalleAccion(300);
        }

        function <portlet:namespace />quitarDetalleEnPantalla(index) {
            if (<portlet:namespace />detalleAccionEnCurso) {
                return false;
            }

            <portlet:namespace />setDetalleAccionEnCurso(true);

            var detalle = <portlet:namespace />detallesCompra[index];

            if (!detalle) {
                return <portlet:namespace />liberarDetalleAccion(0);
            }

            if (!confirm('Confirma quitar el detalle?')) {
                return <portlet:namespace />liberarDetalleAccion(0);
            }

            if (detalle.id != null && detalle.id != '' && parseInt(detalle.id, 10) > 0) {
                <portlet:namespace />detalleDeletedIds.push(detalle.id);
            }

            <portlet:namespace />detallesCompra.splice(index, 1);
            <portlet:namespace />limpiarEditorDetalle();
            <portlet:namespace />renderDetallesCompra();

            return <portlet:namespace />liberarDetalleAccion(700);
        }

        function <portlet:namespace />crearHiddenDetalle(name, value) {
            var form = document.getElementById('<portlet:namespace />fmCompras');

            if (!form) {
                alert('No se encontro el formulario principal para serializar detalles.');
                return false;
            }

            var payload = document.getElementById('<portlet:namespace />detalle_payload');

            if (!payload) {
                payload = form;
            }

            var input = document.createElement('input');

            input.type = 'hidden';
            input.name = '<portlet:namespace />' + name;
            input.id = '<portlet:namespace />serializado_' + name;
            input.value = value == null ? '' : value;
            input.className = '<portlet:namespace />detalle_serializado';

            payload.appendChild(input);

            return true;
        }

        function <portlet:namespace />limpiarPayloadDetallesCompra() {
            var form = jQuery('#<portlet:namespace />fmCompras');

            /*
             * Limpieza agresiva e intencional:
             * borra cualquier detalle_count viejo, incluido el hidden fijo value="0"
             * que estaba rompiendo el guardado.
             */
            form.find('input[name="<portlet:namespace />detalle_count"]').remove();
            form.find('input[name="<portlet:namespace />detalle_deleted_ids"]').remove();
            form.find('input[name^="<portlet:namespace />detalle_"]').remove();

            jQuery('#<portlet:namespace />detalle_payload').empty();
        }

        function <portlet:namespace />serializarDetallesCompras() {
            var form = document.getElementById('<portlet:namespace />fmCompras');

            if (!form) {
                alert('No se encontro el formulario principal de Compras.');
                return false;
            }

            <portlet:namespace />limpiarPayloadDetallesCompra();

            if (<portlet:namespace />detallesCompra.length <= 0) {
                alert('Debe cargar al menos un detalle antes de guardar el requerimiento.');
                return false;
            }

            var articulosSerializados = {};

            for (var d = 0; d < <portlet:namespace />detallesCompra.length; d++) {
                var detalleValidacion = <portlet:namespace />detallesCompra[d];

                if (detalleValidacion == null) {
                    continue;
                }

                var idArticuloValidacion = jQuery.trim(detalleValidacion.idArticulo);

                if (idArticuloValidacion == '') {
                    continue;
                }

                if (articulosSerializados[idArticuloValidacion]) {
                    alert(
                        'Detalle #' + (d + 1) +
                        ': el articulo ya fue cargado en otro detalle. ' +
                        'Edite la fila existente en lugar de duplicarlo.'
                    );

                    return false;
                }

                articulosSerializados[idArticuloValidacion] = true;
            }

            if (!<portlet:namespace />crearHiddenDetalle('detalle_count', <portlet:namespace />detallesCompra.length)) {
                return false;
            }

            if (!<portlet:namespace />crearHiddenDetalle('detalle_deleted_ids', <portlet:namespace />detalleDeletedIds.join(','))) {
                return false;
            }

            for (var i = 0; i < <portlet:namespace />detallesCompra.length; i++) {
                var detalle = <portlet:namespace />detallesCompra[i];
                var prefix = 'detalle_' + i + '_';

                var idArticulo = jQuery.trim(detalle.idArticulo);
                var cantidad = jQuery.trim(detalle.cantidad);

                if (idArticulo == ''
                        || !/^[0-9]+$/.test(idArticulo)
                        || parseInt(idArticulo, 10) <= 0) {

                    alert('Detalle #' + (i + 1) + ': debe seleccionar un artículo.');
                    return false;
                }

                if (cantidad == ''
                        || !/^[0-9]+$/.test(cantidad)
                        || parseInt(cantidad, 10) <= 0) {

                    alert('Detalle #' + (i + 1) + ': la cantidad debe ser entera y mayor a cero.');
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'id', detalle.id)) {
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'id_articulo', detalle.idArticulo)) {
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'cantidad', detalle.cantidad)) {
                    return false;
                }

                if (!<portlet:namespace />crearHiddenDetalle(prefix + 'observaciones', detalle.observaciones)) {
                    return false;
                }
            }

            return true;
        }

        window['<portlet:namespace />serializarDetallesCompras'] =
                <portlet:namespace />serializarDetallesCompras;

        jQuery(function() {
            jQuery('#<portlet:namespace />id_sector, #<portlet:namespace />sector_id').change(function() {
                <portlet:namespace />filtrarArticulosPorSector();
            });

            <portlet:namespace />filtrarArticulosPorSector();
        });
    <% } %>

    jQuery(function() {
        <portlet:namespace />renderDetallesCompra();

        <% if (layoutEdicionDetalle) { %>
            <portlet:namespace />filtrarArticulosPorSector();
        <% } %>
    });
</script>