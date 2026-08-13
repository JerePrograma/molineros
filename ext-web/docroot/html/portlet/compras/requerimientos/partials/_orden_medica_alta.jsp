<%@ page import="java.util.Calendar" %>
<%
final int maxOrdenesMedicasPorCarga = 20;

Calendar fechaOrdenMedicaReferencia =
        Calendar.getInstance();

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
        input.orden-medica-numero-receta {

            width: 95%;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        td.orden-medica-acciones input {

            margin-right: 4px;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        td.orden-medica-campo-fecha select {

            margin-right: 2px;
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

                    } else if (fechaOrdenMedicaGuardada != null
                            && fechaOrdenMedicaGuardada.matches(
                                    "\\d{2}/\\d{2}/\\d{4}"
                            )) {

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

                            <span class="orden-medica-fecha-liferay">
                            </span>

                            <input
                                    type="hidden"
                                    class="orden-medica-fecha-valor"
                                    id="<portlet:namespace /><%= idCampoFecha %>"
                                    name="<%= nombreCampoFecha %>"
                                    value="<%= HtmlUtil.escape(
                                            fechaOrdenMedicaISO
                                    ) %>" />
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

        <div
                id="<portlet:namespace />orden_medica_fecha_template"
                style="display:none;">

            <liferay-ui:input-date
                    dayParam="ordenMedicaTemplateDia"
                    dayValue=""
                    dayNullable="<%= true %>"

                    monthParam="ordenMedicaTemplateMes"
                    monthValue="-1"
                    monthNullable="<%= true %>"

                    yearParam="ordenMedicaTemplateAnio"
                    yearValue=""
                    yearNullable="<%= true %>"

                    yearRangeStart="<%= fechaOrdenMedicaReferencia.get(Calendar.YEAR) - 5 %>"
                    yearRangeEnd="<%= fechaOrdenMedicaReferencia.get(Calendar.YEAR) %>"

                    firstDayOfWeek="<%= fechaOrdenMedicaReferencia.getFirstDayOfWeek() %>"

                    disabled="<%= false %>"
            />
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
        function <portlet:namespace />parametroFechaOrdenMedica(
                base,
                index) {

            if (index == 0) {
                return base;
            }

            return base
                    + '_'
                    + index;
        }

        function <portlet:namespace />inicializarInputDateOrdenMedica(
                row,
                index) {

            var contenedor =
                    row.find(
                            '.orden-medica-fecha-liferay'
                    );

            if (contenedor.length == 0) {
                return false;
            }

            var template =
                    jQuery(
                            '#<portlet:namespace />orden_medica_fecha_template'
                    ).html();

            if (!template) {
                return false;
            }

            var parametroDia =
                    <portlet:namespace />parametroFechaOrdenMedica(
                            'fechaOrdenMedicaDia',
                            index
                    );

            var parametroMes =
                    <portlet:namespace />parametroFechaOrdenMedica(
                            'fechaOrdenMedicaMes',
                            index
                    );

            var parametroAnio =
                    <portlet:namespace />parametroFechaOrdenMedica(
                            'fechaOrdenMedicaAnio',
                            index
                    );

            /*
             * Clonar el markup generado por liferay-ui:input-date,
             * reemplazando los parámetros utilizados exclusivamente
             * en la plantilla.
             */
            template =
                    template.replace(
                            /ordenMedicaTemplateDia/g,
                            parametroDia
                    );

            template =
                    template.replace(
                            /ordenMedicaTemplateMes/g,
                            parametroMes
                    );

            template =
                    template.replace(
                            /ordenMedicaTemplateAnio/g,
                            parametroAnio
                    );

            contenedor.html(
                    template
            );

            var dia =
                    document.getElementById(
                            '<portlet:namespace />'
                                    + parametroDia
                    );

            var mes =
                    document.getElementById(
                            '<portlet:namespace />'
                                    + parametroMes
                    );

            var anio =
                    document.getElementById(
                            '<portlet:namespace />'
                                    + parametroAnio
                    );

            if (!dia
                    || !mes
                    || !anio) {

                contenedor.empty();

                return false;
            }

            jQuery(dia).addClass(
                    'orden-medica-fecha-dia'
            );

            jQuery(mes).addClass(
                    'orden-medica-fecha-mes'
            );

            jQuery(anio).addClass(
                    'orden-medica-fecha-anio'
            );

            var fechaValor =
                    row.find(
                            'input.orden-medica-fecha-valor'
                    );

            var fechaISO =
                    fechaValor.length > 0
                            ? jQuery.trim(
                                    fechaValor.val() || ''
                            )
                            : '';

            var match =
                    /^(\d{4})-(\d{2})-(\d{2})$/.exec(
                            fechaISO
                    );

            if (match) {
                var valorAnio =
                        parseInt(
                                match[1],
                                10
                        );

                var valorMes =
                        parseInt(
                                match[2],
                                10
                        ) - 1;

                var valorDia =
                        parseInt(
                                match[3],
                                10
                        );

                jQuery(dia).val(
                        String(valorDia)
                );

                jQuery(mes).val(
                        String(valorMes)
                );

                jQuery(anio).val(
                        String(valorAnio)
                );

            } else {
                jQuery(dia).val(
                        '-1'
                );

                jQuery(mes).val(
                        '-1'
                );

                jQuery(anio).val(
                        '-1'
                );
            }

            jQuery(
                    dia
            ).change(function() {
                <portlet:namespace />sincronizarFechaOrdenMedicaFila(
                        row
                );
            });

            jQuery(
                    mes
            ).change(function() {
                <portlet:namespace />sincronizarFechaOrdenMedicaFila(
                        row
                );
            });

            jQuery(
                    anio
            ).change(function() {
                <portlet:namespace />sincronizarFechaOrdenMedicaFila(
                        row
                );
            });

            return true;
        }

        function <portlet:namespace />sincronizarFechaOrdenMedicaFila(
                row) {

            var dia =
                    row.find(
                            'select.orden-medica-fecha-dia'
                    ).get(0);

            var mes =
                    row.find(
                            'select.orden-medica-fecha-mes'
                    ).get(0);

            var anio =
                    row.find(
                            'select.orden-medica-fecha-anio'
                    ).get(0);

            var fechaValor =
                    row.find(
                            'input.orden-medica-fecha-valor'
                    ).get(0);

            if (!fechaValor) {
                return false;
            }

            fechaValor.value =
                    '';

            if (!dia
                    || !mes
                    || !anio) {

                return false;
            }

            var diaNumero =
                    parseInt(
                            dia.value,
                            10
                    );

            var mesNumero =
                    parseInt(
                            mes.value,
                            10
                    );

            var anioNumero =
                    parseInt(
                            anio.value,
                            10
                    );

            if (isNaN(diaNumero)
                    || isNaN(mesNumero)
                    || isNaN(anioNumero)
                    || diaNumero < 1
                    || mesNumero < 0
                    || anioNumero < 1) {

                return false;
            }

            var fecha =
                    new Date(
                            anioNumero,
                            mesNumero,
                            diaNumero
                    );

            if (fecha.getFullYear() != anioNumero
                    || fecha.getMonth() != mesNumero
                    || fecha.getDate() != diaNumero) {

                return false;
            }

            fechaValor.value =
                    String(anioNumero)
                            + '-'
                            + <portlet:namespace />ordenMedicaDosDigitos(
                                    mesNumero + 1
                            )
                            + '-'
                            + <portlet:namespace />ordenMedicaDosDigitos(
                                    diaNumero
                            );

            return true;
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

                var fechaValor =
                        row.find(
                                'input.orden-medica-fecha-valor'
                        );

                var fechaDia =
                        row.find(
                                'select.orden-medica-fecha-dia'
                        );

                var fechaMes =
                        row.find(
                                'select.orden-medica-fecha-mes'
                        );

                var fechaAnio =
                        row.find(
                                'select.orden-medica-fecha-anio'
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

                    fechaDia.attr(
                            'name',
                            '<portlet:namespace />fechaOrdenMedicaDia'
                    );

                    fechaDia.attr(
                            'id',
                            '<portlet:namespace />fechaOrdenMedicaDia'
                    );

                    fechaMes.attr(
                            'name',
                            '<portlet:namespace />fechaOrdenMedicaMes'
                    );

                    fechaMes.attr(
                            'id',
                            '<portlet:namespace />fechaOrdenMedicaMes'
                    );

                    fechaAnio.attr(
                            'name',
                            '<portlet:namespace />fechaOrdenMedicaAnio'
                    );

                    fechaAnio.attr(
                            'id',
                            '<portlet:namespace />fechaOrdenMedicaAnio'
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

                    fechaDia.attr(
                            'name',
                            '<portlet:namespace />fechaOrdenMedicaDia_'
                                    + index
                    );

                    fechaDia.attr(
                            'id',
                            '<portlet:namespace />fechaOrdenMedicaDia_'
                                    + index
                    );

                    fechaMes.attr(
                            'name',
                            '<portlet:namespace />fechaOrdenMedicaMes_'
                                    + index
                    );

                    fechaMes.attr(
                            'id',
                            '<portlet:namespace />fechaOrdenMedicaMes_'
                                    + index
                    );

                    fechaAnio.attr(
                            'name',
                            '<portlet:namespace />fechaOrdenMedicaAnio_'
                                    + index
                    );

                    fechaAnio.attr(
                            'id',
                            '<portlet:namespace />fechaOrdenMedicaAnio_'
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

            var botonBorrar =
                    row.find(
                            'input.orden-medica-borrar'
                    );

            var botonAgregar =
                    row.find(
                            'input.orden-medica-agregar'
                    );

            botonBorrar.unbind(
                    'click'
            );

            botonBorrar.click(function() {
                var tbody =
                        jQuery(
                                '#<portlet:namespace />ordenes_medicas_body'
                        );

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

            var fechaLiferay =
                    jQuery(
                            '<span '
                                    + 'class="orden-medica-fecha-liferay">'
                                    + '</span>'
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
                    fechaLiferay
            );

            tdFecha.append(
                    fechaValor
            );

            tdFecha.append(
                    '<div class="compras-ayuda-campo">'
                            + 'Seleccione día, mes y año.'
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

            if (!<portlet:namespace />inicializarInputDateOrdenMedica(
                    row,
                    cantidad
            )) {

                row.remove();

                alert(
                        'No se pudo preparar la fecha de la nueva Orden médica.'
                );

                return false;
            }

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

            rows.each(function(index) {
                var row =
                        jQuery(this);

                <portlet:namespace />inicializarInputDateOrdenMedica(
                        row,
                        index
                );

                <portlet:namespace />vincularAccionesFilaOrdenMedica(
                        row
                );
            });

            <portlet:namespace />reindexarFilasOrdenMedica();
        });
    </script>
</c:if>