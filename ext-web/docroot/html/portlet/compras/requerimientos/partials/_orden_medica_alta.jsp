<%
final int maxOrdenesMedicasPorCarga = 20;

int cantidadOrdenesMedicasInicial =
        ParamUtil.getInteger(
                renderRequest,
                "orden_medica_count",
                1
        );

if (cantidadOrdenesMedicasInicial <= 0) {
    cantidadOrdenesMedicasInicial = 1;
}

if (cantidadOrdenesMedicasInicial > maxOrdenesMedicasPorCarga) {
    cantidadOrdenesMedicasInicial = maxOrdenesMedicasPorCarga;
}
%>

<c:if test="<%= esNuevo && modoEditable %>">

    <style type="text/css">
        #<portlet:namespace />orden_medica_fieldset {
            position: relative;
        }

        #<portlet:namespace />tabla_ordenes_medicas {
            width: 100%;
            border-collapse: separate;
            border-spacing: 3px;
        }

        #<portlet:namespace />tabla_ordenes_medicas th {
            text-align: left;
            vertical-align: middle;
        }

        #<portlet:namespace />tabla_ordenes_medicas td {
            vertical-align: middle;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        td.orden-medica-campo-archivo {

            width: 30%;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        td.orden-medica-campo-fecha {

            width: 20%;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        td.orden-medica-campo-receta {

            width: 20%;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        td.orden-medica-acciones {

            width: 30%;
            white-space: nowrap;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        input.orden-medica-archivo {

            width: 98%;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        input.orden-medica-fecha-visible {

            width: 95px;
            margin-right: 4px;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        input.orden-medica-numero-receta {

            width: 95%;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        td.orden-medica-acciones input {

            margin-right: 4px;
        }

        #<portlet:namespace />orden_medica_calendario {
            position: absolute;
            display: none;
            z-index: 10000;
            width: 250px;
            padding: 8px;
            border: 1px solid #999999;
            background: #ffffff;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
        }

        #<portlet:namespace />orden_medica_calendario
        .orden-medica-calendario-cabecera {

            text-align: center;
            margin-bottom: 6px;
            white-space: nowrap;
        }

        #<portlet:namespace />orden_medica_calendario
        .orden-medica-calendario-titulo {

            display: inline-block;
            min-width: 130px;
            font-weight: bold;
        }

        #<portlet:namespace />orden_medica_calendario table {
            width: 100%;
            border-collapse: collapse;
        }

        #<portlet:namespace />orden_medica_calendario th {
            padding: 2px;
            text-align: center;
            font-size: 11px;
        }

        #<portlet:namespace />orden_medica_calendario td {
            padding: 1px;
            text-align: center;
        }

        #<portlet:namespace />orden_medica_calendario
        input.orden-medica-calendario-dia {

            width: 28px;
            padding: 2px;
            text-align: center;
        }

        #<portlet:namespace />orden_medica_calendario
        .orden-medica-calendario-pie {

            margin-top: 6px;
            text-align: center;
        }

        #<portlet:namespace />orden_medica_calendario
        .orden-medica-calendario-pie input {

            margin-right: 4px;
        }
    </style>

    <fieldset
            id="<portlet:namespace />orden_medica_fieldset"
            class="block-labels compras-seccion compras-seccion-orden-medica">

        <legend>
            Orden médica

            <a href="javascript:void(0)"
               onclick="return comprasHelp(
                       event,
                       '<portlet:namespace />helpCargaOrdenMedica'
               );">
                <img
                        style="height: 25px; width: 25px; vertical-align: middle;"
                        src="/html/images/help.png"
                        title="Ayuda para carga de órdenes médicas"
                        alt="Ayuda" />
            </a>
        </legend>

        <table
                class="lfr-table compras-resumen-requerimiento"
                id="<portlet:namespace />tabla_ordenes_medicas">

            <colgroup>
                <col style="width: 30%;" />
                <col style="width: 20%;" />
                <col style="width: 20%;" />
                <col style="width: 30%;" />
            </colgroup>

            <thead>
                <tr>
                    <th>Orden médica</th>
                    <th>Fecha de la orden médica</th>
                    <th>Número de receta</th>
                    <th>Acciones</th>
                </tr>
            </thead>

            <tbody id="<portlet:namespace />ordenes_medicas_body">

                <%
                for (int i = 0;
                        i < cantidadOrdenesMedicasInicial;
                        i++) {

                    String parametroFechaOrdenMedica =
                            i == 0
                                    ? "fecha_orden_medica"
                                    : "fecha_orden_medica_" + i;

                    String parametroNumeroRecetaOrdenMedica =
                            i == 0
                                    ? "numero_receta_orden_medica"
                                    : "numero_receta_orden_medica_" + i;

                    String fechaOrdenMedicaGuardada =
                            ParamUtil.getString(
                                    renderRequest,
                                    parametroFechaOrdenMedica,
                                    ""
                            );

                    String numeroRecetaOrdenMedicaGuardado =
                            ParamUtil.getString(
                                    renderRequest,
                                    parametroNumeroRecetaOrdenMedica,
                                    ""
                            );

                    /*
                     * Compatibilidad con cualquier retorno anterior
                     * que todavía utilice exclusivamente
                     * fecha_orden_medica.
                     */
                    if (i == 0
                            && WebKeysCompras.isEmpty(
                                    fechaOrdenMedicaGuardada
                            )) {

                        fechaOrdenMedicaGuardada =
                                ParamUtil.getString(
                                        renderRequest,
                                        "fecha_orden_medica",
                                        ""
                                );
                    }

                    String fechaOrdenMedicaISO = "";
                    String fechaOrdenMedicaVisible = "";

                    if (fechaOrdenMedicaGuardada != null) {
                        fechaOrdenMedicaGuardada =
                                fechaOrdenMedicaGuardada.trim();
                    }

                    if (fechaOrdenMedicaGuardada != null
                            && fechaOrdenMedicaGuardada.matches(
                                    "\\d{4}-\\d{2}-\\d{2}"
                            )) {

                        fechaOrdenMedicaISO =
                                fechaOrdenMedicaGuardada;

                        fechaOrdenMedicaVisible =
                                fechaOrdenMedicaGuardada.substring(8, 10)
                                        + "/"
                                        + fechaOrdenMedicaGuardada.substring(
                                                5,
                                                7
                                        )
                                        + "/"
                                        + fechaOrdenMedicaGuardada.substring(
                                                0,
                                                4
                                        );

                    } else if (fechaOrdenMedicaGuardada != null
                            && fechaOrdenMedicaGuardada.matches(
                                    "\\d{2}/\\d{2}/\\d{4}"
                            )) {

                        fechaOrdenMedicaVisible =
                                fechaOrdenMedicaGuardada;

                        fechaOrdenMedicaISO =
                                fechaOrdenMedicaGuardada.substring(6, 10)
                                        + "-"
                                        + fechaOrdenMedicaGuardada.substring(
                                                3,
                                                5
                                        )
                                        + "-"
                                        + fechaOrdenMedicaGuardada.substring(
                                                0,
                                                2
                                        );
                    }

                    String nombreCampoArchivo =
                            i == 0
                                    ? "orden_medica"
                                    : "orden_medica_" + i;

                    String idCampoArchivo =
                            i == 0
                                    ? "orden_medica"
                                    : "orden_medica_" + i;

                    String nombreCampoFechaVisible =
                            i == 0
                                    ? "<portlet:namespace />fecha_orden_medica_visible"
                                    : "<portlet:namespace />fecha_orden_medica_"
                                            + i
                                            + "_visible";

                    String idCampoFechaVisible =
                            i == 0
                                    ? "fecha_orden_medica"
                                    : "fecha_orden_medica_" + i;

                    String nombreCampoFecha =
                            i == 0
                                    ? "fecha_orden_medica"
                                    : "fecha_orden_medica_" + i;

                    String idCampoFecha =
                            i == 0
                                    ? "fecha_orden_medica_valor"
                                    : "fecha_orden_medica_"
                                            + i
                                            + "_valor";

                    String nombreCampoNumeroReceta =
                            i == 0
                                    ? "numero_receta_orden_medica"
                                    : "numero_receta_orden_medica_" + i;

                    String idCampoNumeroReceta =
                            nombreCampoNumeroReceta;
                %>

                    <tr>
                        <td class="orden-medica-campo-archivo">
                            <input
                                    type="file"
                                    class="orden-medica-archivo"
                                    id="<portlet:namespace /><%= idCampoArchivo %>"
                                    name="<%= nombreCampoArchivo %>"
                                    accept=".jpg,.jpeg,.png,image/jpeg,image/png" />

                            <div class="compras-ayuda-campo">
                                Formatos permitidos: JPG, JPEG o PNG.
                            </div>
                        </td>

                        <td class="orden-medica-campo-fecha">
                            <input
                                    type="text"
                                    readonly="readonly"
                                    class="orden-medica-fecha-visible"
                                    id="<portlet:namespace /><%= idCampoFechaVisible %>"
                                    name="<%= nombreCampoFechaVisible %>"
                                    maxlength="10"
                                    size="12"
                                    value="<%= HtmlUtil.escape(
                                            fechaOrdenMedicaVisible
                                    ) %>" />

                            <input
                                    type="hidden"
                                    class="orden-medica-fecha-valor"
                                    id="<portlet:namespace /><%= idCampoFecha %>"
                                    name="<%= nombreCampoFecha %>"
                                    value="<%= HtmlUtil.escape(
                                            fechaOrdenMedicaISO
                                    ) %>" />

                            <input
                                    type="button"
                                    class="orden-medica-calendario-abrir"
                                    value="Calendario"
                                    title="Seleccionar fecha de la orden médica" />

                            <div class="compras-ayuda-campo">
                                Seleccione la fecha desde el calendario.
                            </div>
                        </td>

                        <td class="orden-medica-campo-receta">
                            <input
                                    type="text"
                                    class="orden-medica-numero-receta"
                                    id="<portlet:namespace /><%= idCampoNumeroReceta %>"
                                    name="<%= nombreCampoNumeroReceta %>"
                                    maxlength="100"
                                    value="<%= HtmlUtil.escape(
                                            numeroRecetaOrdenMedicaGuardado
                                    ) %>" />

                            <div class="compras-ayuda-campo">
                                Opcional.
                            </div>
                        </td>

                        <td class="orden-medica-acciones">
                            <input
                                    type="button"
                                    class="orden-medica-borrar"
                                    value="Borrar"
                                    title="Quitar esta orden médica" />

                            <input
                                    type="button"
                                    class="orden-medica-agregar"
                                    value="Agregar otra orden médica"
                                    title="Agregar otra orden médica"
                                    style="<%= i == 0 ? "" : "display:none;" %>" />
                        </td>
                    </tr>

                <%
                }
                %>

            </tbody>
        </table>

        <input
                type="hidden"
                name="orden_medica_count"
                id="<portlet:namespace />orden_medica_count"
                value="<%= cantidadOrdenesMedicasInicial %>" />

        <div id="<portlet:namespace />orden_medica_calendario">
        </div>
    </fieldset>

    <div
            id="<portlet:namespace />helpCargaOrdenMedica"
            class="containerPlus draggable compras-container-ayuda {buttons:'c', skin:'default', width:'700',title:'Ayuda - Carga de órdenes médicas',closed:'true'}"
            style="top: 500px; left: 200px">

        <strong>Requisitos para cargar órdenes médicas</strong>
        <br /><br />

        - Para dar de alta un requerimiento nuevo debe cargarse
          al menos una Orden médica.
        <br />

        - Los formatos permitidos son JPG, JPEG y PNG.
        <br />

        - El archivo debe contener realmente una imagen JPEG o PNG válida;
          no alcanza con cambiarle la extensión.
        <br />

        - El archivo no puede estar vacío.
        <br />

        - Cada Orden médica debe tener informada su propia fecha.
        <br />

        - El número de receta es opcional.
        <br />

        - La fecha debe seleccionarse utilizando el botón "Calendario".
        <br />

        - Puede utilizar "Agregar otra orden médica" para asociar
          más de una Orden médica al mismo requerimiento.
        <br />

        - Se pueden cargar hasta
          <%= maxOrdenesMedicasPorCarga %>
          órdenes médicas en una misma operación.
        <br />

        - El botón "Borrar" elimina únicamente la fila de la carga actual,
          antes de guardar el requerimiento.
        <br />

        - Los archivos deben respetar el tamaño máximo permitido por
          Document Library.
        <br />

        - Si el guardado falla por una validación, por seguridad del navegador
          deberá volver a seleccionar los archivos. Las fechas y la cantidad
          de filas sí pueden conservarse.
    </div>

    <script type="text/javascript">
        var <portlet:namespace />ordenMedicaCalendarioInputVisible =
                null;

        var <portlet:namespace />ordenMedicaCalendarioInputValor =
                null;

        var <portlet:namespace />ordenMedicaCalendarioAnio =
                0;

        var <portlet:namespace />ordenMedicaCalendarioMes =
                0;

        function <portlet:namespace />ordenMedicaDosDigitos(valor) {
            return valor < 10
                    ? '0' + valor
                    : String(valor);
        }

        function <portlet:namespace />ordenMedicaFechaISO(
                anio,
                mes,
                dia) {

            return String(anio)
                    + '-'
                    + <portlet:namespace />ordenMedicaDosDigitos(
                            mes + 1
                    )
                    + '-'
                    + <portlet:namespace />ordenMedicaDosDigitos(
                            dia
                    );
        }

        function <portlet:namespace />ordenMedicaFechaVisible(
                anio,
                mes,
                dia) {

            return <portlet:namespace />ordenMedicaDosDigitos(
                    dia
            )
                    + '/'
                    + <portlet:namespace />ordenMedicaDosDigitos(
                            mes + 1
                    )
                    + '/'
                    + String(anio);
        }

        function <portlet:namespace />cerrarCalendarioOrdenMedica() {
            jQuery(
                    '#<portlet:namespace />orden_medica_calendario'
            ).hide();

            <portlet:namespace />ordenMedicaCalendarioInputVisible =
                    null;

            <portlet:namespace />ordenMedicaCalendarioInputValor =
                    null;
        }

        function <portlet:namespace />seleccionarFechaOrdenMedica(
                anio,
                mes,
                dia) {

            if (!<portlet:namespace />ordenMedicaCalendarioInputVisible
                    || !<portlet:namespace />ordenMedicaCalendarioInputValor) {

                return false;
            }

            <portlet:namespace />ordenMedicaCalendarioInputVisible.val(
                    <portlet:namespace />ordenMedicaFechaVisible(
                            anio,
                            mes,
                            dia
                    )
            );

            <portlet:namespace />ordenMedicaCalendarioInputValor.val(
                    <portlet:namespace />ordenMedicaFechaISO(
                            anio,
                            mes,
                            dia
                    )
            );

            <portlet:namespace />cerrarCalendarioOrdenMedica();

            return false;
        }

        function <portlet:namespace />renderizarCalendarioOrdenMedica() {
            var calendario =
                    jQuery(
                            '#<portlet:namespace />orden_medica_calendario'
                    );

            var nombresMeses = [
                'Enero',
                'Febrero',
                'Marzo',
                'Abril',
                'Mayo',
                'Junio',
                'Julio',
                'Agosto',
                'Septiembre',
                'Octubre',
                'Noviembre',
                'Diciembre'
            ];

            calendario.empty();

            var cabecera =
                    jQuery(
                            '<div '
                                    + 'class="orden-medica-calendario-cabecera">'
                                    + '</div>'
                    );

            var anterior =
                    jQuery(
                            '<input '
                                    + 'type="button" '
                                    + 'value="<" '
                                    + 'title="Mes anterior" '
                                    + '/>'
                    );

            anterior.click(function() {
                <portlet:namespace />ordenMedicaCalendarioMes--;

                if (<portlet:namespace />ordenMedicaCalendarioMes < 0) {
                    <portlet:namespace />ordenMedicaCalendarioMes = 11;
                    <portlet:namespace />ordenMedicaCalendarioAnio--;
                }

                <portlet:namespace />renderizarCalendarioOrdenMedica();

                return false;
            });

            var titulo =
                    jQuery(
                            '<span '
                                    + 'class="orden-medica-calendario-titulo">'
                                    + '</span>'
                    );

            titulo.text(
                    nombresMeses[
                            <portlet:namespace />ordenMedicaCalendarioMes
                    ]
                            + ' '
                            + <portlet:namespace />ordenMedicaCalendarioAnio
            );

            var siguiente =
                    jQuery(
                            '<input '
                                    + 'type="button" '
                                    + 'value=">" '
                                    + 'title="Mes siguiente" '
                                    + '/>'
                    );

            siguiente.click(function() {
                <portlet:namespace />ordenMedicaCalendarioMes++;

                if (<portlet:namespace />ordenMedicaCalendarioMes > 11) {
                    <portlet:namespace />ordenMedicaCalendarioMes = 0;
                    <portlet:namespace />ordenMedicaCalendarioAnio++;
                }

                <portlet:namespace />renderizarCalendarioOrdenMedica();

                return false;
            });

            cabecera.append(
                    anterior
            );

            cabecera.append(
                    document.createTextNode(' ')
            );

            cabecera.append(
                    titulo
            );

            cabecera.append(
                    document.createTextNode(' ')
            );

            cabecera.append(
                    siguiente
            );

            calendario.append(
                    cabecera
            );

            var tabla =
                    jQuery(
                            '<table></table>'
                    );

            var encabezado =
                    jQuery(
                            '<tr>'
                                    + '<th>Dom</th>'
                                    + '<th>Lun</th>'
                                    + '<th>Mar</th>'
                                    + '<th>Mié</th>'
                                    + '<th>Jue</th>'
                                    + '<th>Vie</th>'
                                    + '<th>Sáb</th>'
                                    + '</tr>'
                    );

            tabla.append(
                    encabezado
            );

            var primerDia =
                    new Date(
                            <portlet:namespace />ordenMedicaCalendarioAnio,
                            <portlet:namespace />ordenMedicaCalendarioMes,
                            1
                    ).getDay();

            var diasMes =
                    new Date(
                            <portlet:namespace />ordenMedicaCalendarioAnio,
                            <portlet:namespace />ordenMedicaCalendarioMes + 1,
                            0
                    ).getDate();

            var dia =
                    1;

            var fila =
                    null;

            for (var celda = 0;
                    celda < 42;
                    celda++) {

                if (celda % 7 == 0) {
                    fila =
                            jQuery(
                                    '<tr></tr>'
                            );

                    tabla.append(
                            fila
                    );
                }

                var td =
                        jQuery(
                                '<td></td>'
                        );

                if (celda < primerDia
                        || dia > diasMes) {

                    td.html(
                            '&nbsp;'
                    );

                } else {
                    var botonDia =
                            jQuery(
                                    '<input '
                                            + 'type="button" '
                                            + 'class="orden-medica-calendario-dia" '
                                            + '/>'
                            );

                    botonDia.val(
                            dia
                    );

                    botonDia.attr(
                            'data-dia',
                            String(dia)
                    );

                    botonDia.click(function() {
                        var diaSeleccionado =
                                parseInt(
                                        jQuery(this).attr(
                                                'data-dia'
                                        ),
                                        10
                                );

                        return <portlet:namespace />seleccionarFechaOrdenMedica(
                                <portlet:namespace />ordenMedicaCalendarioAnio,
                                <portlet:namespace />ordenMedicaCalendarioMes,
                                diaSeleccionado
                        );
                    });

                    td.append(
                            botonDia
                    );

                    dia++;
                }

                fila.append(
                        td
                );

                if (dia > diasMes
                        && celda >= primerDia + diasMes
                        && (celda + 1) % 7 == 0) {

                    break;
                }
            }

            calendario.append(
                    tabla
            );

            var pie =
                    jQuery(
                            '<div '
                                    + 'class="orden-medica-calendario-pie">'
                                    + '</div>'
                    );

            var hoy =
                    jQuery(
                            '<input '
                                    + 'type="button" '
                                    + 'value="Hoy" '
                                    + '/>'
                    );

            hoy.click(function() {
                var fechaHoy =
                        new Date();

                return <portlet:namespace />seleccionarFechaOrdenMedica(
                        fechaHoy.getFullYear(),
                        fechaHoy.getMonth(),
                        fechaHoy.getDate()
                );
            });

            var cerrar =
                    jQuery(
                            '<input '
                                    + 'type="button" '
                                    + 'value="Cerrar" '
                                    + '/>'
                    );

            cerrar.click(function() {
                <portlet:namespace />cerrarCalendarioOrdenMedica();

                return false;
            });

            pie.append(
                    hoy
            );

            pie.append(
                    document.createTextNode(' ')
            );

            pie.append(
                    cerrar
            );

            calendario.append(
                    pie
            );
        }

        function <portlet:namespace />abrirCalendarioOrdenMedica(
                boton) {

            var row =
                    jQuery(boton)
                            .parents(
                                    'tr'
                            )
                            .eq(0);

            var visible =
                    row.find(
                            'input.orden-medica-fecha-visible'
                    );

            var valor =
                    row.find(
                            'input.orden-medica-fecha-valor'
                    );

            if (visible.length == 0
                    || valor.length == 0) {

                return false;
            }

            <portlet:namespace />ordenMedicaCalendarioInputVisible =
                    visible;

            <portlet:namespace />ordenMedicaCalendarioInputValor =
                    valor;

            var fechaBase =
                    new Date();

            var fechaISO =
                    jQuery.trim(
                            valor.val()
                    );

            var match =
                    /^(\d{4})-(\d{2})-(\d{2})$/.exec(
                            fechaISO
                    );

            if (match) {
                var anio =
                        parseInt(
                                match[1],
                                10
                        );

                var mes =
                        parseInt(
                                match[2],
                                10
                        ) - 1;

                var dia =
                        parseInt(
                                match[3],
                                10
                        );

                var fechaParseada =
                        new Date(
                                anio,
                                mes,
                                dia
                        );

                if (fechaParseada.getFullYear() == anio
                        && fechaParseada.getMonth() == mes
                        && fechaParseada.getDate() == dia) {

                    fechaBase =
                            fechaParseada;
                }
            }

            <portlet:namespace />ordenMedicaCalendarioAnio =
                    fechaBase.getFullYear();

            <portlet:namespace />ordenMedicaCalendarioMes =
                    fechaBase.getMonth();

            <portlet:namespace />renderizarCalendarioOrdenMedica();

            var calendario =
                    jQuery(
                            '#<portlet:namespace />orden_medica_calendario'
                    );

            var fieldset =
                    jQuery(
                            '#<portlet:namespace />orden_medica_fieldset'
                    );

            var botonJQuery =
                    jQuery(
                            boton
                    );

            var offsetFieldset =
                    fieldset.offset();

            var offsetBoton =
                    botonJQuery.offset();

            calendario.css(
                    {
                        left:
                                offsetBoton.left
                                        - offsetFieldset.left,
                        top:
                                offsetBoton.top
                                        - offsetFieldset.top
                                        + botonJQuery.outerHeight()
                                        + 3
                    }
            );

            calendario.show();

            return false;
        }

        function <portlet:namespace />reindexarFilasOrdenMedica() {
            var rows =
                    jQuery(
                            '#<portlet:namespace />ordenes_medicas_body tr'
                    );

            rows.each(function(index) {
                var row =
                        jQuery(this);

                var archivo =
                        row.find(
                                'input.orden-medica-archivo'
                        );

                var fechaVisible =
                        row.find(
                                'input.orden-medica-fecha-visible'
                        );

                var fechaValor =
                        row.find(
                                'input.orden-medica-fecha-valor'
                        );

                var numeroReceta =
                        row.find(
                                'input.orden-medica-numero-receta'
                        );

                var botonAgregar =
                        row.find(
                                'input.orden-medica-agregar'
                        );

                if (index == 0) {
                    archivo.attr(
                            'name',
                            'orden_medica'
                    );

                    archivo.attr(
                            'id',
                            '<portlet:namespace />orden_medica'
                    );

                    fechaVisible.attr(
                            'name',
                            '<portlet:namespace />fecha_orden_medica_visible'
                    );

                    fechaVisible.attr(
                            'id',
                            '<portlet:namespace />fecha_orden_medica'
                    );

                    fechaValor.attr(
                            'name',
                            'fecha_orden_medica'
                    );

                    fechaValor.attr(
                            'id',
                            '<portlet:namespace />fecha_orden_medica_valor'
                    );

                    numeroReceta.attr(
                            'name',
                            'numero_receta_orden_medica'
                    );

                    numeroReceta.attr(
                            'id',
                            '<portlet:namespace />numero_receta_orden_medica'
                    );

                } else {
                    archivo.attr(
                            'name',
                            'orden_medica_'
                                    + index
                    );

                    archivo.attr(
                            'id',
                            '<portlet:namespace />orden_medica_'
                                    + index
                    );

                    fechaVisible.attr(
                            'name',
                            '<portlet:namespace />fecha_orden_medica_'
                                    + index
                                    + '_visible'
                    );

                    fechaVisible.attr(
                            'id',
                            '<portlet:namespace />fecha_orden_medica_'
                                    + index
                    );

                    fechaValor.attr(
                            'name',
                            'fecha_orden_medica_'
                                    + index
                    );

                    fechaValor.attr(
                            'id',
                            '<portlet:namespace />fecha_orden_medica_'
                                    + index
                                    + '_valor'
                    );

                    numeroReceta.attr(
                            'name',
                            'numero_receta_orden_medica_'
                                    + index
                    );

                    numeroReceta.attr(
                            'id',
                            '<portlet:namespace />numero_receta_orden_medica_'
                                    + index
                    );
                }

                /*
                 * Igual que en presupuestos:
                 * Agregar solamente se muestra en la primera fila.
                 */
                if (index == 0
                        && rows.length
                                < <%= maxOrdenesMedicasPorCarga %>) {

                    botonAgregar.show();

                } else {
                    botonAgregar.hide();
                }
            });

            jQuery(
                    '#<portlet:namespace />orden_medica_count'
            ).val(
                    rows.length
            );
        }

        function <portlet:namespace />vincularAccionesFilaOrdenMedica(
                row) {

            var botonCalendario =
                    row.find(
                            'input.orden-medica-calendario-abrir'
                    );

            var botonBorrar =
                    row.find(
                            'input.orden-medica-borrar'
                    );

            var botonAgregar =
                    row.find(
                            'input.orden-medica-agregar'
                    );

            botonCalendario.unbind(
                    'click'
            );

            botonCalendario.click(function() {
                return <portlet:namespace />abrirCalendarioOrdenMedica(
                        this
                );
            });

            botonBorrar.unbind(
                    'click'
            );

            botonBorrar.click(function() {
                var tbody =
                        jQuery(
                                '#<portlet:namespace />ordenes_medicas_body'
                        );

                <portlet:namespace />cerrarCalendarioOrdenMedica();

                jQuery(this)
                        .parents(
                                'tr'
                        )
                        .eq(0)
                        .remove();

                if (tbody.find('tr').length == 0) {
                    <portlet:namespace />agregarFilaOrdenMedica();
                } else {
                    <portlet:namespace />reindexarFilasOrdenMedica();
                }

                return false;
            });

            botonAgregar.unbind(
                    'click'
            );

            botonAgregar.click(function() {
                return <portlet:namespace />agregarFilaOrdenMedica();
            });
        }

        function <portlet:namespace />agregarFilaOrdenMedica() {
            var tbody =
                    jQuery(
                            '#<portlet:namespace />ordenes_medicas_body'
                    );

            if (tbody.length == 0) {
                return false;
            }

            var cantidad =
                    tbody.find(
                            'tr'
                    ).length;

            if (cantidad
                    >= <%= maxOrdenesMedicasPorCarga %>) {

                alert(
                        'Se pueden cargar hasta '
                                + '<%= maxOrdenesMedicasPorCarga %>'
                                + ' órdenes médicas por operación.'
                );

                return false;
            }

            var archivo =
                    jQuery(
                            '<input '
                                    + 'type="file" '
                                    + 'class="orden-medica-archivo" '
                                    + 'accept=".jpg,.jpeg,.png,image/jpeg,image/png" '
                                    + '/>'
                    );

            var fechaVisible =
                    jQuery(
                            '<input '
                                    + 'type="text" '
                                    + 'readonly="readonly" '
                                    + 'class="orden-medica-fecha-visible" '
                                    + 'maxlength="10" '
                                    + 'size="12" '
                                    + '/>'
                    );

            var fechaValor =
                    jQuery(
                            '<input '
                                    + 'type="hidden" '
                                    + 'class="orden-medica-fecha-valor" '
                                    + '/>'
                    );

            var numeroReceta =
                    jQuery(
                            '<input '
                                    + 'type="text" '
                                    + 'class="orden-medica-numero-receta" '
                                    + 'maxlength="100" '
                                    + '/>'
                    );

            var botonCalendario =
                    jQuery(
                            '<input '
                                    + 'type="button" '
                                    + 'class="orden-medica-calendario-abrir" '
                                    + 'value="Calendario" '
                                    + 'title="Seleccionar fecha de la orden médica" '
                                    + '/>'
                    );

            var botonBorrar =
                    jQuery(
                            '<input '
                                    + 'type="button" '
                                    + 'class="orden-medica-borrar" '
                                    + 'value="Borrar" '
                                    + 'title="Quitar esta orden médica" '
                                    + '/>'
                    );

            var botonAgregar =
                    jQuery(
                            '<input '
                                    + 'type="button" '
                                    + 'class="orden-medica-agregar" '
                                    + 'value="Agregar otra orden médica" '
                                    + 'title="Agregar otra orden médica" '
                                    + '/>'
                    );

            var row =
                    jQuery(
                            '<tr></tr>'
                    );

            var tdArchivo =
                    jQuery(
                            '<td '
                                    + 'class="orden-medica-campo-archivo">'
                                    + '</td>'
                    );

            tdArchivo.append(
                    archivo
            );

            tdArchivo.append(
                    '<div class="compras-ayuda-campo">'
                            + 'Formatos permitidos: JPG, JPEG o PNG.'
                            + '</div>'
            );

            var tdFecha =
                    jQuery(
                            '<td '
                                    + 'class="orden-medica-campo-fecha">'
                                    + '</td>'
                    );

            tdFecha.append(
                    fechaVisible
            );

            tdFecha.append(
                    document.createTextNode(' ')
            );

            tdFecha.append(
                    fechaValor
            );

            tdFecha.append(
                    botonCalendario
            );

            tdFecha.append(
                    '<div class="compras-ayuda-campo">'
                            + 'Seleccione la fecha desde el calendario.'
                            + '</div>'
            );

            var tdNumeroReceta =
                    jQuery(
                            '<td '
                                    + 'class="orden-medica-campo-receta">'
                                    + '</td>'
                    );

            tdNumeroReceta.append(
                    numeroReceta
            );

            tdNumeroReceta.append(
                    '<div class="compras-ayuda-campo">'
                            + 'Opcional.'
                            + '</div>'
            );

            var tdAcciones =
                    jQuery(
                            '<td '
                                    + 'class="orden-medica-acciones">'
                                    + '</td>'
                    );

            tdAcciones.append(
                    botonBorrar
            );

            tdAcciones.append(
                    document.createTextNode(' ')
            );

            tdAcciones.append(
                    botonAgregar
            );

            row.append(
                    tdArchivo
            );

            row.append(
                    tdFecha
            );

            row.append(
                    tdNumeroReceta
            );

            row.append(
                    tdAcciones
            );

            tbody.append(
                    row
            );

            <portlet:namespace />vincularAccionesFilaOrdenMedica(
                    row
            );

            <portlet:namespace />reindexarFilasOrdenMedica();

            return false;
        }

        jQuery(function() {
            var rows =
                    jQuery(
                            '#<portlet:namespace />ordenes_medicas_body tr'
                    );

            rows.each(function() {
                <portlet:namespace />vincularAccionesFilaOrdenMedica(
                        jQuery(this)
                );
            });

            <portlet:namespace />reindexarFilasOrdenMedica();

            jQuery(
                    '#<portlet:namespace />orden_medica_calendario'
            ).click(function(event) {
                if (event
                        && event.stopPropagation) {

                    event.stopPropagation();
                }
            });

            jQuery(document).click(function() {
                <portlet:namespace />cerrarCalendarioOrdenMedica();
            });
        });
    </script>
</c:if>