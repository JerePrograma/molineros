<%--
Responsabilidad:
    Renderiza controles para agregar Órdenes Médicas sin perder las existentes.
Incluido desde:
    requerimiento_compra_orden_medica_carga_runtime_componente.jsp
Pantallas o estados de uso:
    Alta y PENDIENTE; ENVIADO A COTIZAR sólo donde la capacidad publicada lo permite.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    orden_medica_fieldset, tabla_ordenes_medicas, helpCargaOrdenMedica
Efectos secundarios:
    Sólo modifica el DOM o el modelo JavaScript; no ejecuta persistencia.
--%>
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

<c:if test="<%= modoEditable
        && puedeEditarEstructuraPantalla %>">

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

            width: 40%;
        }

        #<portlet:namespace />tabla_ordenes_medicas
        td.orden-medica-campo-fecha {

            width: 30%;
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
            <%= esNuevo
                    ? "Orden médica"
                    : "Agregar Orden médica" %>

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
                <col style="width: 40%;" />
                <col style="width: 30%;" />
                <col style="width: 30%;" />
            </colgroup>

            <thead>
                <tr>
                    <th>Orden médica</th>
                    <th>Fecha de la orden médica</th>
                    <th>Acciones</th>
                </tr>
            </thead>

            <tbody id="<portlet:namespace />ordenes_medicas_body">

                <%
                for (int i = 0;
                        i < maxOrdenesMedicasPorCarga;
                        i++) {

                    boolean ordenMedicaActiva =
                            i < cantidadOrdenesMedicasInicial;

                    String parametroFechaOrdenMedica =
                            i == 0
                                    ? "fecha_orden_medica"
                                    : "fecha_orden_medica_" + i;

                    String fechaOrdenMedicaGuardada =
                            ordenMedicaActiva
                                    ? ParamUtil.getString(
                                            renderRequest,
                                            parametroFechaOrdenMedica,
                                            ""
                                    )
                                    : "";

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

                    int diaOrdenMedica = 0;
                    int mesOrdenMedica = -1;
                    int anioOrdenMedica = 0;

                    if (!WebKeysCompras.isEmpty(
                            fechaOrdenMedicaISO
                    )) {

                        try {
                            anioOrdenMedica = Integer.parseInt(
                                    fechaOrdenMedicaISO.substring(0, 4)
                            );

                            mesOrdenMedica = Integer.parseInt(
                                    fechaOrdenMedicaISO.substring(5, 7)
                            ) - 1;

                            diaOrdenMedica = Integer.parseInt(
                                    fechaOrdenMedicaISO.substring(8, 10)
                            );

                        } catch (Exception e) {
                            diaOrdenMedica = 0;
                            mesOrdenMedica = -1;
                            anioOrdenMedica = 0;
                            fechaOrdenMedicaISO = "";
                        }
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

                    String parametroFechaDia =
                            i == 0
                                    ? "fechaOrdenMedicaDia"
                                    : "fechaOrdenMedicaDia_" + i;

                    String parametroFechaMes =
                            i == 0
                                    ? "fechaOrdenMedicaMes"
                                    : "fechaOrdenMedicaMes_" + i;

                    String parametroFechaAnio =
                            i == 0
                                    ? "fechaOrdenMedicaAnio"
                                    : "fechaOrdenMedicaAnio_" + i;

                    String idCalendarioFecha =
                            "ordenMedicaFechaCalendario_" + i;
                %>

                    <tr
                            class="orden-medica-fila <%= ordenMedicaActiva
                                    ? "orden-medica-activa"
                                    : "orden-medica-inactiva" %>"
                            data-orden-medica-indice="<%= i %>"
                            style="<%= ordenMedicaActiva
                                    ? ""
                                    : "display:none;" %>">

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
                                <liferay-ui:input-date
                                        dayParam="<%= parametroFechaDia %>"
                                        dayValue="<%= diaOrdenMedica %>"
                                        dayNullable="<%= true %>"

                                        monthParam="<%= parametroFechaMes %>"
                                        monthValue="<%= mesOrdenMedica %>"
                                        monthNullable="<%= true %>"

                                        yearParam="<%= parametroFechaAnio %>"
                                        yearValue="<%= anioOrdenMedica %>"
                                        yearNullable="<%= true %>"

                                        yearRangeStart="<%= fechaOrdenMedicaReferencia.get(Calendar.YEAR) - 5 %>"
                                        yearRangeEnd="<%= fechaOrdenMedicaReferencia.get(Calendar.YEAR) %>"

                                        firstDayOfWeek="<%= fechaOrdenMedicaReferencia.getFirstDayOfWeek() - 1 %>"
                                        imageInputId="<%= idCalendarioFecha %>"
                                        disabled="<%= false %>"
                                />
                            </span>

                            <input
                                    type="hidden"
                                    class="orden-medica-fecha-valor"
                                    id="<portlet:namespace /><%= idCampoFecha %>"
                                    name="<%= nombreCampoFecha %>"
                                    value="<%= HtmlUtil.escape(
                                            fechaOrdenMedicaISO
                                    ) %>" />

                            <div class="compras-ayuda-campo">
                                Seleccione mes, día y año.
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
                                    style="display:none;" />
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
    </fieldset>

    <div
            id="<portlet:namespace />helpCargaOrdenMedica"
            class="containerPlus draggable compras-container-ayuda {buttons:'c', skin:'default', width:'700',title:'Ayuda - Carga de órdenes médicas',closed:'true'}"
            style="top: 500px; left: 200px">

        <strong>Requisitos para cargar órdenes médicas</strong>
        <br /><br />

        <% if (esNuevo) { %>

            - Para dar de alta un requerimiento nuevo debe cargarse
              al menos una Orden médica.
            <br />

        <% } else { %>

            - Mientras el requerimiento se encuentre PENDIENTE puede
              incorporar nuevas Órdenes médicas.
            <br />

            - Agregar una nueva Orden médica durante la edición es opcional.
            <br />

            - Las Órdenes médicas ya registradas no se modifican ni eliminan
              desde este componente.
            <br />

        <% } %>
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

        - La fecha debe seleccionarse mediante Día, Mes y Año.
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
        function <portlet:namespace />filasActivasOrdenMedica() {
            return jQuery(
                    '#<portlet:namespace />ordenes_medicas_body '
                            + 'tr.orden-medica-activa'
            );
        }

        function <portlet:namespace />prepararControlesFechaOrdenMedica(
                row) {

            var fechaDia =
                    row.find(
                            'select[id*="fechaOrdenMedicaDia"]'
                    );

            var fechaMes =
                    row.find(
                            'select[id*="fechaOrdenMedicaMes"]'
                    );

            var fechaAnio =
                    row.find(
                            'select[id*="fechaOrdenMedicaAnio"]'
                    );

            if (fechaDia.length != 1
                    || fechaMes.length != 1
                    || fechaAnio.length != 1) {

                return false;
            }

            fechaDia.addClass(
                    'orden-medica-fecha-dia'
            );

            fechaMes.addClass(
                    'orden-medica-fecha-mes'
            );

            fechaAnio.addClass(
                    'orden-medica-fecha-anio'
            );

            fechaDia.unbind(
                    'change.ordenMedica'
            );

            fechaMes.unbind(
                    'change.ordenMedica'
            );

            fechaAnio.unbind(
                    'change.ordenMedica'
            );

            fechaDia.bind(
                    'change.ordenMedica',
                    function() {
                        <portlet:namespace />sincronizarFechaOrdenMedicaFila(
                                row
                        );
                    }
            );

            fechaMes.bind(
                    'change.ordenMedica',
                    function() {
                        <portlet:namespace />sincronizarFechaOrdenMedicaFila(
                                row
                        );
                    }
            );

            fechaAnio.bind(
                    'change.ordenMedica',
                    function() {
                        <portlet:namespace />sincronizarFechaOrdenMedicaFila(
                                row
                        );
                    }
            );

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

            fechaValor.value = '';

            if (!dia
                    || !mes
                    || !anio) {

                return false;
            }

            var diaNumero = parseInt(
                    dia.value,
                    10
            );

            var mesNumero = parseInt(
                    mes.value,
                    10
            );

            var anioNumero = parseInt(
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

            var fecha = new Date(
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

        function <portlet:namespace />limpiarFilaOrdenMedica(row) {
            var archivo =
                    row.find(
                            'input.orden-medica-archivo'
                    ).get(0);

            if (!archivo) {
                return false;
            }

            try {
                archivo.value = '';
            } catch (e) {
                return false;
            }

            if (jQuery.trim(
                    archivo.value || ''
            ) != '') {

                return false;
            }

            row.find(
                    'select.orden-medica-fecha-dia, '
                            + 'select.orden-medica-fecha-mes, '
                            + 'select.orden-medica-fecha-anio'
            ).val('');

            row.find(
                    'input.orden-medica-fecha-valor'
            ).val('');

            row.find(
                    '.orden-medica-fecha-liferay input[type="hidden"]'
            ).val('');

            return true;
        }

        function <portlet:namespace />actualizarContratoFilasOrdenMedica() {
            var todasLasFilas =
                    jQuery(
                            '#<portlet:namespace />ordenes_medicas_body '
                                    + 'tr.orden-medica-fila'
                    );

            var filasActivas =
                    <portlet:namespace />filasActivasOrdenMedica();

            filasActivas.each(function(index) {
                var row = jQuery(this);
                var sufijo = index == 0
                        ? ''
                        : '_' + index;

                row.find(
                        'input.orden-medica-archivo'
                ).attr(
                        'name',
                        'orden_medica' + sufijo
                );

                row.find(
                        'input.orden-medica-fecha-valor'
                ).attr(
                        'name',
                        'fecha_orden_medica' + sufijo
                );

                row.css(
                        'display',
                        ''
                );
            });

            todasLasFilas.filter(
                    '.orden-medica-inactiva'
            ).each(function() {
                var row = jQuery(this);
                var indice = row.attr(
                        'data-orden-medica-indice'
                );

                row.find(
                        'input.orden-medica-archivo'
                ).attr(
                        'name',
                        'orden_medica_slot_' + indice
                );

                row.find(
                        'input.orden-medica-fecha-valor'
                ).attr(
                        'name',
                        'fecha_orden_medica_slot_' + indice
                );

                row.hide();
            });

            todasLasFilas.find(
                    'input.orden-medica-agregar'
            ).hide();

            if (filasActivas.length > 0
                    && filasActivas.length
                            < <%= maxOrdenesMedicasPorCarga %>) {

                filasActivas.eq(0).find(
                        'input.orden-medica-agregar'
                ).show();
            }

            jQuery(
                    '#<portlet:namespace />orden_medica_count'
            ).val(
                    filasActivas.length
            );

            var fechaHistorica = document.getElementById(
                    '<portlet:namespace />fecha_orden_medica_hidden'
            );

            if (fechaHistorica) {
                fechaHistorica.value =
                        filasActivas.length > 0
                                ? filasActivas.eq(0).find(
                                        'input.orden-medica-fecha-valor'
                                ).val() || ''
                                : '';
            }

            return filasActivas.length > 0;
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
                    'click.ordenMedica'
            );

            botonBorrar.bind(
                    'click.ordenMedica',
                    function() {
                        var fila = jQuery(this).parents(
                                'tr.orden-medica-fila'
                        ).eq(0);

                        if (!<portlet:namespace />limpiarFilaOrdenMedica(
                                fila
                        )) {

                            alert(
                                    'No se pudo limpiar la Orden médica seleccionada.'
                            );

                            return false;
                        }

                        if (<portlet:namespace />filasActivasOrdenMedica().length
                                > 1) {

                            fila.removeClass(
                                    'orden-medica-activa'
                            );

                            fila.addClass(
                                    'orden-medica-inactiva'
                            );

                            fila.hide();
                        }

                        <portlet:namespace />actualizarContratoFilasOrdenMedica();

                        return false;
                    }
            );

            botonAgregar.unbind(
                    'click.ordenMedica'
            );

            botonAgregar.bind(
                    'click.ordenMedica',
                    function() {
                        return <portlet:namespace />agregarFilaOrdenMedica();
                    }
            );
        }

        function <portlet:namespace />agregarFilaOrdenMedica() {
            var filasActivas =
                    <portlet:namespace />filasActivasOrdenMedica();

            if (filasActivas.length
                    >= <%= maxOrdenesMedicasPorCarga %>) {

                alert(
                        'Se pueden cargar hasta '
                                + '<%= maxOrdenesMedicasPorCarga %>'
                                + ' órdenes médicas por operación.'
                );

                return false;
            }

            var fila =
                    jQuery(
                            '#<portlet:namespace />ordenes_medicas_body '
                                    + 'tr.orden-medica-inactiva'
                    ).eq(0);

            if (fila.length == 0
                    || !<portlet:namespace />limpiarFilaOrdenMedica(
                            fila
                    )) {

                alert(
                        'No se pudo preparar una nueva Orden médica.'
                );

                return false;
            }

            fila.removeClass(
                    'orden-medica-inactiva'
            );

            fila.addClass(
                    'orden-medica-activa'
            );

            fila.css(
                    'display',
                    ''
            );

            <portlet:namespace />actualizarContratoFilasOrdenMedica();

            return false;
        }

        jQuery(function() {
            var filas =
                    jQuery(
                            '#<portlet:namespace />ordenes_medicas_body '
                                    + 'tr.orden-medica-fila'
                    );

            filas.each(function() {
                var row = jQuery(this);

                <portlet:namespace />prepararControlesFechaOrdenMedica(
                        row
                );

                <portlet:namespace />vincularAccionesFilaOrdenMedica(
                        row
                );
            });

            <portlet:namespace />filasActivasOrdenMedica().each(function() {
                <portlet:namespace />sincronizarFechaOrdenMedicaFila(
                        jQuery(this)
                );
            });

            <portlet:namespace />actualizarContratoFilasOrdenMedica();
        });
    </script>
</c:if>
