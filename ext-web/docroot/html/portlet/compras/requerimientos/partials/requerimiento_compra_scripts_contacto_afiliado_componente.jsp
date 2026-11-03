<%--
Referencia: requerimiento_compra_scripts_base_componente.jsp y
Autorizaciones/reclamos_prestacionales/view_reclamo.jsp: popup y confirmacion.
Cuerpo JavaScript compartido por requerimientos y consulta de OC.
Incluir estaticamente dentro de un script, fuera de cualquier IIFE.
Requiere comprasActualizaDomicilioURL, jQuery, Liferay.Popup y WebKeysAfiliados.
La identidad fija de OC conserva CUIL e integrante; el servidor valida su token.
--%>
    function <portlet:namespace />valorInputCompra(id) {
        var el = document.getElementById('<portlet:namespace />' + id);

        if (!el || typeof el.value == 'undefined' || el.value == null) {
            return '';
        }

        return String(el.value).replace(/^\s+|\s+$/g, '');
    }

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
        jQuery('#<portlet:namespace />divResultadoActualizarOK').hide();

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

        if (<portlet:namespace />valorInputCompra('contacto_afiliado_identidad_fija') != '1'
                && idPar != '<%= WebKeysAfiliados.PARENTESCO_DEFAULT %>'
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

                respuesta_contacto_json:
                        '1',

                cmd:
                        'save'
            },

            success: function(data) {
                var resultado = null;
                try {
                    resultado = typeof data == 'string' ? jQuery.parseJSON(data) : data;
                } catch (e) {
                    resultado = null;
                }
                if (!resultado || resultado.status !== 'ok') {
                    alert('No se pudieron actualizar los datos de contacto.');
                    return;
                }

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
