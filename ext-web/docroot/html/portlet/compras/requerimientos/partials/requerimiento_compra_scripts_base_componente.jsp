<%--
Responsabilidad:
    Declara utilidades ES5 compartidas por consulta y edición.
Incluido desde:
    requerimiento_compra_consulta_ensamblado.jsp, requerimiento_compra_edicion_ensamblado.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    situacionMedicaSecuencia, situacionMedicaRequest, popupSituacionMedica, ocultarSituacionMedicaAfiliado, btnSituacionMedica, actualizarSituacionMedicaAfiliado
Efectos secundarios:
    Sólo modifica el DOM o el modelo JavaScript; no ejecuta persistencia.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_inicializacion_componente.jsp" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<portlet:renderURL
        var="comprasBuscarAfiliadoDatosURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">

    <portlet:param
            name="struts_action"
            value="/compras/buscar_afiliado_datos" />

</portlet:renderURL>
<portlet:renderURL
        var="comprasActualizaDomicilioURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">

    <portlet:param
            name="struts_action"
            value="/compras/actualiza_domicilio" />

</portlet:renderURL>
<portlet:renderURL
        var="comprasBuscarVencimientoCudURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">

    <portlet:param
            name="struts_action"
            value="/compras/buscar_afiliado_fecha_vto_documentacion" />

</portlet:renderURL>

<portlet:renderURL
        var="comprasTieneSituacionMedicaURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">

    <portlet:param
            name="struts_action"
            value="/compras/tiene_situacion_medica_vigente" />

</portlet:renderURL>

<portlet:renderURL
        var="comprasVerSituacionMedicaURL"
        windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">

    <portlet:param
            name="struts_action"
            value="/compras/ver_situacion_medica_vigente" />

</portlet:renderURL>
<script type="text/javascript">
    var <portlet:namespace />situacionMedicaSecuencia =
            0;

    var <portlet:namespace />situacionMedicaRequest =
            null;

    var <portlet:namespace />popupSituacionMedica =
            null;

    function <portlet:namespace />ocultarSituacionMedicaAfiliado() {

        <portlet:namespace />situacionMedicaSecuencia++;

        if (<portlet:namespace />situacionMedicaRequest
                && <portlet:namespace />situacionMedicaRequest.readyState != 4) {

            try {
                <portlet:namespace />situacionMedicaRequest.abort();
            } catch (e) {
                /*
                 * Un aborto esperado no debe alterar la pantalla.
                 */
            }
        }

        <portlet:namespace />situacionMedicaRequest =
                null;

        jQuery(
                '#<portlet:namespace />btnSituacionMedica'
        ).hide();
    }

    function <portlet:namespace />actualizarSituacionMedicaAfiliado(
            cuil,
            inte) {

        cuil =
                cuil != null
                        ? jQuery.trim(String(cuil))
                        : '';

        inte =
                inte != null
                        ? jQuery.trim(String(inte))
                        : '';

        /*
         * Invalida primero el estado anterior.
         *
         * Esto impide que permanezca visible la situación médica
         * correspondiente al afiliado previamente seleccionado.
         */
        <portlet:namespace />ocultarSituacionMedicaAfiliado();

        if (cuil == ''
                || inte == '') {

            return;
        }

        /*
         * Fail-closed también del lado cliente.
         */
        if (!/^[0-9]{11}$/.test(cuil)
                || !/^[0-9]+$/.test(inte)) {

            return;
        }

        var secuenciaActual =
                <portlet:namespace />situacionMedicaSecuencia;

        <portlet:namespace />situacionMedicaRequest =
                jQuery.ajax({

                    url:
                            '${comprasTieneSituacionMedicaURL}',

                    data: {
                        cuil_titular: cuil,
                        inte: inte
                    },

                    cache: false,

                    success: function(data) {

                        if (secuenciaActual
                                != <portlet:namespace />situacionMedicaSecuencia) {

                            return;
                        }

                        var obj =
                                null;

                        try {
                            obj =
                                    typeof data == 'string'
                                            ? jQuery.parseJSON(data)
                                            : data;

                        } catch (e) {

                            jQuery(
                                    '#<portlet:namespace />btnSituacionMedica'
                            ).hide();

                            return;
                        }

                        var tieneSituacion =
                                obj != null
                                && (
                                        obj.tieneSituacionMedica === true
                                        || obj.tieneSituacionMedica == 'true'
                                );

                        if (tieneSituacion) {

                            jQuery(
                                    '#<portlet:namespace />btnSituacionMedica'
                            ).show();

                        } else {

                            jQuery(
                                    '#<portlet:namespace />btnSituacionMedica'
                            ).hide();
                        }
                    },

                    error: function(xhr, estado) {

                        if (secuenciaActual
                                != <portlet:namespace />situacionMedicaSecuencia) {

                            return;
                        }

                        if (estado == 'abort') {
                            return;
                        }

                        /*
                         * Fail-closed.
                         */
                        jQuery(
                                '#<portlet:namespace />btnSituacionMedica'
                        ).hide();
                    },

                    complete: function() {

                        if (secuenciaActual
                                == <portlet:namespace />situacionMedicaSecuencia) {

                            <portlet:namespace />situacionMedicaRequest =
                                    null;
                        }
                    }
                });
    }

    function <portlet:namespace />abrirSituacionMedicaAfiliado() {

        var cuil =
                <portlet:namespace />valorInputCompra(
                        'cuil'
                );

        var inte =
                <portlet:namespace />valorInputCompra(
                        'inte'
                );

        if (cuil == ''
                || inte == '') {

            return false;
        }

        if (!/^[0-9]{11}$/.test(cuil)
                || !/^[0-9]+$/.test(inte)) {

            return false;
        }

        if (<portlet:namespace />popupSituacionMedica != null) {

            try {
                Liferay.Popup.close(
                        <portlet:namespace />popupSituacionMedica
                );
            } catch (e) {
                /*
                 * El popup puede haber sido cerrado manualmente.
                 */
            }

            <portlet:namespace />popupSituacionMedica =
                    null;
        }

        <portlet:namespace />popupSituacionMedica =
                Liferay.Popup({
                    title: 'Situación Médica',
                    modal: true,
                    width: 950
                });

        var url =
                '${comprasVerSituacionMedicaURL}'
                + '&cuil_titular='
                + encodeURIComponent(cuil)
                + '&inte='
                + encodeURIComponent(inte);

        jQuery(
                <portlet:namespace />popupSituacionMedica
        ).load(
                url
        );

        return false;
    }

    function <portlet:namespace />valorInputCompra(id) {
        var el = document.getElementById('<portlet:namespace />' + id);

        if (!el || typeof el.value == 'undefined' || el.value == null) {
            return '';
        }

        return String(el.value).replace(/^\s+|\s+$/g, '');
    }

    function <portlet:namespace />parsePorcentajeSilencioso(id) {
        var value = <portlet:namespace />valorInputCompra(id);

        if (value == '') {
            return null;
        }

        if (!/^[0-9]+$/.test(value)) {
            return null;
        }

        var parsed = parseInt(value, 10);

        if (isNaN(parsed) || parsed < 0 || parsed > 100) {
            return null;
        }

        return parsed;
    }

    function <portlet:namespace />actualizarRecuperoPorCargoTercerizadora(cargoTercerizadoraForzado) {
        var cargoTercerizadora = null;

        if (typeof cargoTercerizadoraForzado != 'undefined'
                && cargoTercerizadoraForzado != null) {
            cargoTercerizadora = cargoTercerizadoraForzado;
        } else {
            cargoTercerizadora = <portlet:namespace />parsePorcentajeSilencioso('cargo_tercerizadora');
        }

        var recuperoActivo = cargoTercerizadora != null && cargoTercerizadora > 0;

        var recuperoEl = document.getElementById('<portlet:namespace />recupero');

        if (recuperoEl) {
            recuperoEl.checked = recuperoActivo;
            recuperoEl.defaultChecked = recuperoActivo;

            if (recuperoActivo) {
                recuperoEl.setAttribute('checked', 'checked');
            } else {
                recuperoEl.removeAttribute('checked');
            }
        }

        var recuperoHiddenEl = document.getElementById('<portlet:namespace />recupero_hidden');

        if (recuperoHiddenEl) {
            recuperoHiddenEl.value = recuperoActivo ? 'true' : 'false';
        }

        return recuperoActivo;
    }

    function <portlet:namespace />actualizarSurgeCompra() {
        var surgeEl =
                document.getElementById(
                        '<portlet:namespace />surge'
                );

        var surgeHiddenEl =
                document.getElementById(
                        '<portlet:namespace />surge_hidden'
                );

        var surgeValue = '';

        if (surgeEl
                && typeof surgeEl.value != 'undefined'
                && surgeEl.value != null) {

            surgeValue =
                    String(surgeEl.value)
                            .replace(/^\s+|\s+$/g, '');
        }

        /*
         * Sólo 0 y 1 son valores válidos.
         * Cualquier otro valor se normaliza a vacío.
         */
        if (surgeValue != '0'
                && surgeValue != '1') {

            surgeValue = '';
        }

        if (surgeHiddenEl) {
            surgeHiddenEl.value = surgeValue;
        }

        return surgeValue;
    }

    function <portlet:namespace />validarSurgeCompra() {
        var surgeValue =
                <portlet:namespace />actualizarSurgeCompra();

        var surgeEl =
                document.getElementById(
                        '<portlet:namespace />surge'
                );

        if (surgeValue == '0'
                || surgeValue == '1') {

            return true;
        }

        alert('Surge: debe seleccionar Sí o No.');

        if (surgeEl
                && typeof surgeEl.focus == 'function') {

            surgeEl.focus();
        }

        return false;
    }

        /*
         * ==========================================================
         * VENCIMIENTO CUD DEL AFILIADO
         * ==========================================================
         *
         * El componente compartido de afiliado ya contiene:
         *
         *   discapacidad
         *   discapacidad_vto
         *
         * Compras únicamente completa su estado consultando el
         * endpoint existente de Autorizaciones.
         *
         * La secuencia evita que una respuesta AJAX vieja pueda
         * sobrescribir los datos de un afiliado seleccionado después.
         */

        var <portlet:namespace />vencimientoCudSecuencia =
                0;

        var <portlet:namespace />vencimientoCudRequest =
                null;

        function <portlet:namespace />invalidarConsultaVencimientoCud() {

            <portlet:namespace />vencimientoCudSecuencia++;

            if (<portlet:namespace />vencimientoCudRequest
                    && <portlet:namespace />vencimientoCudRequest.readyState != 4) {

                try {
                    <portlet:namespace />vencimientoCudRequest.abort();
                } catch (e) {
                    // No alterar el flujo de Compras por un aborto AJAX.
                }
            }

            <portlet:namespace />vencimientoCudRequest =
                    null;
        }

        function <portlet:namespace />limpiarVisualVencimientoCud() {

            jQuery(
                    '#<portlet:namespace />discapacidad'
            ).hide();

            jQuery(
                    '#<portlet:namespace />discapacidad_vto'
            )
                    .text('')
                    .hide();
        }

        function <portlet:namespace />ocultarVencimientoCudAfiliado() {

            <portlet:namespace />invalidarConsultaVencimientoCud();

            <portlet:namespace />limpiarVisualVencimientoCud();
        }

        function <portlet:namespace />actualizarVencimientoCudAfiliado(
                cuil,
                inte,
                incapacidad) {

            cuil =
                    cuil != null
                            ? jQuery.trim(String(cuil))
                            : '';

            inte =
                    inte != null
                            ? jQuery.trim(String(inte))
                            : '';

            incapacidad =
                    incapacidad != null
                            ? jQuery.trim(String(incapacidad))
                            : '';

            /*
             * Se invalida cualquier consulta anterior antes de
             * comenzar a trabajar con el nuevo afiliado.
             */
            <portlet:namespace />invalidarConsultaVencimientoCud();

            if (incapacidad != '1'
                    || cuil == ''
                    || inte == '') {

                <portlet:namespace />limpiarVisualVencimientoCud();
                return;
            }

            var discapacidad =
                    jQuery(
                            '#<portlet:namespace />discapacidad'
                    );

            var vencimiento =
                    jQuery(
                            '#<portlet:namespace />discapacidad_vto'
                    );

            discapacidad.show();

            vencimiento
                    .text('Vto. CUD: consultando...')
                    .show();

            var secuenciaActual =
                    <portlet:namespace />vencimientoCudSecuencia;

            var url =
                    '${comprasBuscarVencimientoCudURL}';

            <portlet:namespace />vencimientoCudRequest =
                    jQuery.ajax({
                        url: url,
                        data: {
                            cuil_titular: cuil,
                            inte: inte
                        },
                        cache: false,

                        success: function(data) {

                            /*
                             * Ignorar respuestas pertenecientes a una
                             * selección de afiliado anterior.
                             */
                            if (secuenciaActual
                                    != <portlet:namespace />vencimientoCudSecuencia) {

                                return;
                            }

                            var obj =
                                    null;

                            try {
                                obj =
                                        typeof data == 'string'
                                                ? jQuery.parseJSON(data)
                                                : data;
                            } catch (e) {
                                vencimiento
                                        .text('Vto. CUD: no disponible')
                                        .show();

                                return;
                            }

                            var discapacitado =
                                    obj != null
                                    && obj.discapacitado != null
                                            ? jQuery.trim(
                                                    String(
                                                            obj.discapacitado
                                                    )
                                            )
                                            : '';

                            /*
                             * También se respeta el valor canónico devuelto
                             * por el servidor.
                             */
                            if (discapacitado != '1') {
                                <portlet:namespace />limpiarVisualVencimientoCud();
                                return;
                            }

                            var fechaVto =
                                    obj != null
                                    && obj.fechaVto != null
                                            ? jQuery.trim(
                                                    String(
                                                            obj.fechaVto
                                                    )
                                            )
                                            : '';

                            discapacidad.show();

                            if (fechaVto != '') {
                                vencimiento
                                        .text(
                                                'Vto. CUD: '
                                                        + fechaVto
                                        )
                                        .show();
                            } else {
                                vencimiento
                                        .text(
                                                'Vto. CUD: sin fecha informada'
                                        )
                                        .show();
                            }
                        },

                        error: function(xhr, estado) {

                            if (secuenciaActual
                                    != <portlet:namespace />vencimientoCudSecuencia) {

                                return;
                            }

                            /*
                             * abort es esperado cuando se cambia de afiliado.
                             */
                            if (estado == 'abort') {
                                return;
                            }

                            discapacidad.show();

                            vencimiento
                                    .text(
                                            'Vto. CUD: no disponible'
                                    )
                                    .show();
                        },

                        complete: function() {

                            if (secuenciaActual
                                    == <portlet:namespace />vencimientoCudSecuencia) {

                                <portlet:namespace />vencimientoCudRequest =
                                        null;
                            }
                        }
                    });
        }
        var <portlet:namespace />verificacionContactoSecuencia =
                0;

        var <portlet:namespace />verificacionContactoRequest =
                null;

        var <portlet:namespace />vinculacionContactoRequest =
                null;

        function <portlet:namespace />establecerDisponibilidadActualizarContacto(
                habilitado) {

            jQuery(
                    '#<portlet:namespace />seccionVerificarDomicilio'
            ).show();

            jQuery(
                    '#<portlet:namespace />divBotonActualizar'
            ).show();

            var boton =
                    jQuery(
                            '#<portlet:namespace />botonActualizarContactoAfiliado'
                    );

            if (habilitado) {
                boton.removeAttr('disabled');
            } else {
                boton.attr('disabled', 'disabled');
            }
        }

        function <portlet:namespace />ocultarVerificacionContactoAfiliado() {

            <portlet:namespace />verificacionContactoSecuencia++;

            if (<portlet:namespace />verificacionContactoRequest
                    && <portlet:namespace />verificacionContactoRequest.readyState != 4) {

                try {
                    <portlet:namespace />verificacionContactoRequest.abort();
                } catch (e) {
                    /* Aborto esperado. */
                }
            }

            <portlet:namespace />verificacionContactoRequest =
                    null;

            if (<portlet:namespace />vinculacionContactoRequest
                    && <portlet:namespace />vinculacionContactoRequest.readyState != 4) {

                try {
                    <portlet:namespace />vinculacionContactoRequest.abort();
                } catch (e) {
                    /* Aborto esperado. */
                }
            }

            <portlet:namespace />vinculacionContactoRequest =
                    null;

            <portlet:namespace />establecerDisponibilidadActualizarContacto(
                    false
            );

            jQuery(
                    '#<portlet:namespace />divResultadoActualizarOK'
            ).hide();
        }

        function <portlet:namespace />actualizarVerificacionContactoAfiliado(
                cuil,
                inte) {

            var seccion =
                    jQuery(
                            '#<portlet:namespace />seccionVerificarDomicilio'
                    );

            /* El sector o el permiso pueden omitir la sección. */
            if (seccion.length == 0) {
                return;
            }

            <portlet:namespace />ocultarVerificacionContactoAfiliado();

            cuil =
                    cuil != null
                            ? jQuery.trim(String(cuil))
                            : '';

            inte =
                    inte != null
                            ? jQuery.trim(String(inte))
                            : '';

            if (!/^[0-9]{11}$/.test(cuil)
                    || !/^[0-9]+$/.test(inte)) {

                return;
            }

            var secuenciaActual =
                    <portlet:namespace />verificacionContactoSecuencia;

            var tokenContacto =
                    <portlet:namespace />valorInputCompra(
                            'contacto_afiliado_token'
                    );

            if (tokenContacto == '') {
                return;
            }

            <portlet:namespace />vinculacionContactoRequest =
                    jQuery.ajax({

                        url:
                                '${comprasActualizaDomicilioURL}',

                        data: {
                            cuil_titular:
                                    cuil,

                            inte:
                                    inte,

                            contacto_afiliado_token:
                                    tokenContacto,

                            cmd:
                                    'bind'
                        },

                        cache:
                                false,

                        success: function() {
                            if (secuenciaActual
                                    == <portlet:namespace />verificacionContactoSecuencia) {

                                <portlet:namespace />establecerDisponibilidadActualizarContacto(
                                        true
                                );
                            }
                        },

                        error: function() {
                            if (secuenciaActual
                                    == <portlet:namespace />verificacionContactoSecuencia) {

                                <portlet:namespace />establecerDisponibilidadActualizarContacto(
                                        false
                                );
                            }
                        },

                        complete: function() {
                            if (secuenciaActual
                                    == <portlet:namespace />verificacionContactoSecuencia) {

                                <portlet:namespace />vinculacionContactoRequest =
                                        null;
                            }
                        }
                    });

            <portlet:namespace />verificacionContactoRequest =
                    jQuery.ajax({

                        url:
                                '${comprasBuscarAfiliadoDatosURL}',

                        data: {
                            cuil_titular:
                                    cuil,

                            inte:
                                    inte
                        },

                        cache:
                                false,

                        success: function(data) {

                            if (secuenciaActual
                                    != <portlet:namespace />verificacionContactoSecuencia) {

                                return;
                            }

                            var obj =
                                    null;

                            try {
                                obj =
                                        typeof data == 'string'
                                                ? jQuery.parseJSON(data)
                                                : data;
                            } catch (e) {
                                jQuery(
                                        '#<portlet:namespace />divResultadoActualizarOK'
                                ).hide();
                                return;
                            }

                            if (!obj) {
                                jQuery(
                                        '#<portlet:namespace />divResultadoActualizarOK'
                                ).hide();
                                return;
                            }

                            var actualizaDomicilio =
                                    obj.actualizadomicilio;

                            var actualizaTelefono =
                                    obj.actualizatelefono;

                            var requiereActualizar =
                                    actualizaDomicilio === true
                                    || actualizaDomicilio == 'true'
                                    || actualizaTelefono === true
                                    || actualizaTelefono == 'true';

                            seccion.show();

                            if (requiereActualizar) {
                                jQuery(
                                        '#<portlet:namespace />divResultadoActualizarOK'
                                ).hide();

                            } else {
                                jQuery(
                                        '#<portlet:namespace />divResultadoActualizarOK'
                                ).show();
                            }
                        },

                        error: function(xhr, estado) {

                            if (secuenciaActual
                                    != <portlet:namespace />verificacionContactoSecuencia) {

                                return;
                            }

                            if (estado == 'abort') {
                                return;
                            }

                            jQuery(
                                    '#<portlet:namespace />divResultadoActualizarOK'
                            ).hide();
                        },

                        complete: function() {

                            if (secuenciaActual
                                    == <portlet:namespace />verificacionContactoSecuencia) {

                                <portlet:namespace />verificacionContactoRequest =
                                        null;
                            }
                        }
                    });
        }


    var <portlet:namespace />popupDomicilioAfiliado =
            null;


    function <portlet:namespace />mostrarDomicilioAfiliado() {

        var cuilTitular =
                <portlet:namespace />valorInputCompra(
                        'cuil'
                );

        var inte =
                <portlet:namespace />valorInputCompra(
                        'inte'
                );

        var tokenContacto =
                <portlet:namespace />valorInputCompra(
                        'contacto_afiliado_token'
                );

        if (cuilTitular == ''
                || inte == ''
                || tokenContacto == '') {

            alert(
                    'Debe seleccionar al Afiliado.'
            );

            return false;
        }

        if (<portlet:namespace />popupDomicilioAfiliado != null) {

            try {
                Liferay.Popup.close(
                        <portlet:namespace />popupDomicilioAfiliado
                );
            } catch (e) {
                /*
                 * El popup puede haber sido cerrado manualmente.
                 */
            }

            <portlet:namespace />popupDomicilioAfiliado =
                    null;
        }

        <portlet:namespace />popupDomicilioAfiliado =
                Liferay.Popup({
                    title: 'Detalle domicilio',
                    modal: true,
                    width: 950,
                    height: 330,
                    fixedcenter: true
                });

        var url =
                '${comprasActualizaDomicilioURL}'
                + '&cuil_titular='
                + encodeURIComponent(
                        cuilTitular
                )
                + '&inte='
                + encodeURIComponent(
                        inte
                )
                + '&contacto_afiliado_token='
                + encodeURIComponent(
                        tokenContacto
                )
                + '&cmd=view'
                + '&portlet_name=autorizaciones';

        jQuery(
                <portlet:namespace />popupDomicilioAfiliado
        ).load(
                url
        );

        return false;
    }


    function <portlet:namespace />validarEmail() {

        var campoEmail =
                jQuery(
                        '#<portlet:namespace />email'
                );

        if (campoEmail.length == 0) {
            return true;
        }

        var email =
                campoEmail.val() != null
                        ? jQuery.trim(
                                String(
                                        campoEmail.val()
                                )
                        )
                        : '';

        if (email == '') {
            return true;
        }

        var expresion =
                /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;

        if (!expresion.test(email)) {

            alert(
                    'Error: La dirección de correo '
                            + email
                            + ' es incorrecta.'
            );

            campoEmail.focus();

            return false;
        }

        return true;
    }


    function <portlet:namespace />confirmarActualizacionDomicilioAfiliado() {

        var idDomicilio =
                jQuery(
                        '#<portlet:namespace />id_domicilio'
                ).val();

        var idProvincia =
                jQuery(
                        '#<portlet:namespace />provincia'
                ).val();

        var idLocalidad =
                jQuery(
                        '#<portlet:namespace />localidad'
                ).val();

        var calle =
                jQuery(
                        '#<portlet:namespace />calle'
                ).val() || '';

        var numero =
                jQuery(
                        '#<portlet:namespace />numero'
                ).val() || '';

        var piso =
                jQuery(
                        '#<portlet:namespace />piso'
                ).val() || '';

        var departamento =
                jQuery(
                        '#<portlet:namespace />dpto'
                ).val() || '';

        var codigoPostal =
                jQuery(
                        '#<portlet:namespace />cod_postal'
                ).val() || '';

        var barrio =
                jQuery(
                        '#<portlet:namespace />barrio'
                ).val() || '';

        var codigoAreaTelefono =
                jQuery(
                        '#<portlet:namespace />cod_area_telefono'
                ).val() || '';

        var telefono =
                jQuery(
                        '#<portlet:namespace />telefono'
                ).val() || '';

        var codigoAreaCelular =
                jQuery(
                        '#<portlet:namespace />cod_area_celular'
                ).val() || '';

        var celular =
                jQuery(
                        '#<portlet:namespace />celular'
                ).val() || '';

        var email =
                jQuery(
                        '#<portlet:namespace />email'
                ).val() || '';

        var emailOriginal =
                jQuery(
                        '#<portlet:namespace />email_original'
                ).val() || '';

        var cuilTitular =
                <portlet:namespace />valorInputCompra(
                        'cuil'
                );

        var integrante =
                <portlet:namespace />valorInputCompra(
                        'inte'
                );

        var tokenContacto =
                <portlet:namespace />valorInputCompra(
                        'contacto_afiliado_token'
                );

        var idPar =
                jQuery(
                        '#<portlet:namespace />idPar'
                ).val();

        if (idPar != '<%= WebKeysAfiliados.PARENTESCO_DEFAULT %>'
                && idPar != '<%= WebKeysAfiliados.CONYUGE_DEFAULT %>'
                && idPar != '<%= WebKeysAfiliados.CONCUBINO_DEFAULT %>') {

            integrante =
                    '0';
        }

        calle =
                jQuery.trim(
                        String(
                                calle
                        )
                );

        codigoAreaTelefono =
                jQuery.trim(
                        String(
                                codigoAreaTelefono
                        )
                );

        telefono =
                jQuery.trim(
                        String(
                                telefono
                        )
                );

        codigoAreaCelular =
                jQuery.trim(
                        String(
                                codigoAreaCelular
                        )
                );

        celular =
                jQuery.trim(
                        String(
                                celular
                        )
                );

        if (calle == '') {

            alert(
                    'Ingrese la calle del domicilio'
            );

            jQuery(
                    '#<portlet:namespace />calle'
            ).focus();

            return false;
        }

        if ((codigoAreaTelefono == '' && telefono != '')
                || (codigoAreaTelefono != '' && telefono == '')) {

            alert(
                    'El teléfono debe necesariamente tener '
                            + 'el código de área y el número'
            );

            return false;
        }

        if (codigoAreaTelefono.indexOf('0') == 0) {

            alert(
                    'El código de área del teléfono '
                            + 'no debe iniciar con cero'
            );

            return false;
        }

        if (telefono.indexOf('0') == 0) {

            alert(
                    'El número del teléfono no debe iniciar con cero'
            );

            return false;
        }

        if ((codigoAreaTelefono != ''
                || telefono != '')
                && codigoAreaTelefono.length
                        + telefono.length != 10) {

            alert(
                    'La longitud del código de área + teléfono '
                            + 'debe ser de 10 caracteres'
            );

            return false;
        }

        if (codigoAreaCelular.indexOf('0') == 0) {

            alert(
                    'El código de área del celular '
                            + 'no debe iniciar con cero'
            );

            return false;
        }

        if (celular.indexOf('0') == 0) {

            alert(
                    'El número del celular no debe iniciar con cero'
            );

            return false;
        }

        if ((codigoAreaCelular != ''
                || celular != '')
                && codigoAreaCelular.length
                        + celular.length != 10) {

            alert(
                    'La longitud del código de área + celular '
                            + 'debe ser de 10 caracteres'
            );

            return false;
        }

        if (!<portlet:namespace />validarEmail()) {
            return false;
        }

        var url =
                '${comprasActualizaDomicilioURL}'
                + '&id_parentesco='
                + encodeURIComponent(
                        idPar
                )
                + '&portlet_name=autorizaciones';

        jQuery.ajax({

            type:
                    'POST',

            url:
                    url,

            data: {
                cuil_titular:
                        cuilTitular,

                inte:
                        integrante,

                id_domicilio:
                        idDomicilio,

                id_provincia:
                        idProvincia,

                id_localidad:
                        idLocalidad,

                calle:
                        calle,

                numero:
                        numero,

                piso:
                        piso,

                departamento:
                        departamento,

                codigo_postal:
                        codigoPostal,

                barrio:
                        barrio,

                cod_area_telefono:
                        codigoAreaTelefono,

                telefono:
                        telefono,

                cod_area_celular:
                        codigoAreaCelular,

                celular:
                        celular,

                email:
                        email,

                email_original:
                        emailOriginal,

                contacto_afiliado_token:
                        tokenContacto,

                cmd:
                        'save'
            },

            success: function() {

                jQuery(
                        '#<portlet:namespace />divResultadoActualizarOK'
                ).show();

                <portlet:namespace />establecerDisponibilidadActualizarContacto(
                        true
                );

                if (<portlet:namespace />popupDomicilioAfiliado != null) {

                    Liferay.Popup.close(
                            <portlet:namespace />popupDomicilioAfiliado
                    );

                    <portlet:namespace />popupDomicilioAfiliado =
                            null;
                }
            },

            error: function() {

                alert(
                        'No se pudieron actualizar '
                                + 'los datos de contacto.'
                );
            }
        });

        return false;
    }


    /*
     * Compatibilidad requerida por
     * actualiza_domicilio_afiliado.jsp.
     *
     * Ese JSP legacy invoca esta función sin namespace.
     */
    function confirmaActualizacionDomicilioAfiliado() {

        return <portlet:namespace />confirmarActualizacionDomicilioAfiliado();
    }
</script>
