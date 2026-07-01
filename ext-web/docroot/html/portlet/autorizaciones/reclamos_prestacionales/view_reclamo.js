(function (window, document, jQuery) {
    var config = null;
    var namespace = '';
    var popupMD = null;
    var popupDomicilio = null;
    var popupCRM = null;
    var addprestacion = false;
    var load = false;
    var sectorIni = '';
    var estadoIni = '';

    function selector(nombre) {
        return '#' + namespace + nombre;
    }

    function elemento(nombre) {
        return document.getElementById(namespace + nombre);
    }

    function formulario(nombre) {
        return document.forms[namespace + nombre] || document[namespace + nombre];
    }

    function valor(nombre) {
        return jQuery(selector(nombre)).val();
    }

    function url(nombre) {
        return config.urls[nombre];
    }

    function mensaje(nombre, valorPorDefecto) {
        return config.messages && config.messages[nombre]
            ? config.messages[nombre]
            : valorPorDefecto;
    }

    function agregarParametros(baseUrl, parametros) {
        var query = jQuery.param(parametros || {});
        if (!query) {
            return baseUrl;
        }
        return baseUrl + (baseUrl.indexOf('?') >= 0 ? '&' : '?') + query;
    }

    function seleccionarValor(nombre, valorSeleccionado) {
        jQuery(selector(nombre)).val(String(valorSeleccionado));
    }

    function estaSeleccionado(nombre) {
        return jQuery(selector(nombre)).is(':checked');
    }

    function establecerComando(form, comando) {
        var control = form && form.elements
            ? form.elements[namespace + config.commands.cmdParam]
            : null;

        if (!control) {
            throw new Error('No se encontró el campo de comando del formulario: ' + namespace + config.commands.cmdParam);
        }
        control.value = comando;
    }

    function obtenerComando(form) {
        var control = form && form.elements
            ? form.elements[namespace + config.commands.cmdParam]
            : null;
        return control ? control.value : '';
    }

    function exponer(nombre, funcion, conNamespace) {
        window[conNamespace ? namespace + nombre : nombre] = funcion;
    }

    function exponerFuncionesPublicas() {
        exponer('buscarNomencladorAutocompletar', buscarNomencladorAutocompletar, true);
        exponer('buscarNomencladorAutocompletar_edit', buscarNomencladorAutocompletarEdit, true);
        exponer('limpiarNomencladorAutocompletar', limpiarNomencladorAutocompletar, true);
        exponer('cerrarDivNm', cerrarDivNm, true);
        exponer('cerrarNm', cerrarNm, true);
        exponer('saveReclamo', saveReclamo, true);
        exponer('volverEstadoObservado', volverEstadoObservado, true);
        exponer('editaReclamo', editaReclamo, true);
        exponer('reabrirReclamo', reabrirReclamo, true);
        exponer('agregarRevision', agregarRevision, true);
        exponer('verprestacionesasociadas', verPrestacionesAsociadas, true);
        exponer('ocultacontactosdelreclamo', ocultaContactosDelReclamo, true);
        exponer('vercontactosdelreclamo', verContactosDelReclamo, true);
        exponer('editarPrestacionSeleccionada', editarPrestacionSeleccionada, true);
        exponer('cancelaEdicionPrestacion', cancelaEdicionPrestacion, true);
        exponer('agregarPrestacion', agregarPrestacion, true);
        exponer('imprimirReclamo', imprimirReclamo, true);
        exponer('validarEmail', validarEmail, true);

        exponer('tipoGestionCierreReclamo', tipoGestionCierreReclamo, false);
        exponer('integracionReclamo', integracionReclamo, false);
        exponer('filtrarLetraComprobante', filtrarLetraComprobante, false);
        exponer('seleccionaCamposNm', seleccionaCamposNm, false);
        exponer('pasarParametrosAParentNm', pasarParametrosAParentNm, false);
        exponer('DatosRevisionOk', DatosRevisionOk, false);
        exponer('ValidarDatosObligatorios', ValidarDatosObligatorios, false);
        exponer('manejartipogestion', manejarTipoGestion, false);
        exponer('manejarListaPresentes', manejarListaPresentes, false);
        exponer('cambioresolucion', cambioResolucion, false);
        exponer('manejarTipoPedido', manejarTipoPedido, false);
        exponer('cambioTipoPedido', cambioTipoPedido, false);
        exponer('manejarTipoPedidoCierre', manejarTipoPedidoCierre, false);
        exponer('manejarTipoSector', manejarTipoSector, false);
        exponer('controlarEstadoCerrado', controlarEstadoCerrado, false);
        exponer('ValidaDatosReclamo', ValidaDatosReclamo, false);
        exponer('ValidaDatosReclamoEditar', ValidaDatosReclamoEditar, false);
        exponer('validarExisteComprobante', validarExisteComprobante, false);
        exponer('evaluarOnSectorListaEnCero', evaluarOnSectorListaEnCero, false);
        exponer('validarSiNumero', validarSiNumero, false);
        exponer('validaMonto', validaMonto, false);
        exponer('verCrmContacto', verCrmContacto, false);
        exponer('validaMontosEdicion', validaMontosEdicion, false);
        exponer('ValidaMontos', ValidaMontos, false);
        exponer('validarevision', validaRevision, false);
        exponer('convertToUppercase', convertToUppercase, false);
        exponer('myXOR', myXOR, false);
        exponer('enterTecla', enterTecla, false);
        exponer('aplicaEstiloBordeRojoDatosObligatorio', aplicaEstiloBordeRojoDatosObligatorio, false);
        exponer('calculatotal', calculaTotal, false);
        exponer('seleccionaCamposCieDiez', seleccionaCamposCieDiez, false);
        exponer('limpiaCamposBusquedaCieDiez', limpiaCamposBusquedaCieDiez, false);
        exponer('seteaControlesFacturacionDirecta', seteaControlesFacturacionDirecta, false);
        exponer('desactivaCheckCierre', desactivaCheckCierre, false);
        exponer('abreAutorizacion', abreAutorizacion, false);
        exponer('calculatotalFC', calculaTotalFC, false);
        exponer('traerDescripcion', traerDescripcion, false);
        exponer('cambiorecuperable', cambioRecuperable, false);
        exponer('confirmaActualizacionDomicilioAfiliado', confirmaActualizacionDomicilioAfiliado, false);
        exponer('mostrarDomicilioAfiliado', mostrarDomicilioAfiliado, false);
    }

    function inicializarPantalla() {
        jQuery(selector('divResultadoActualizarOK')).hide();

        jQuery(selector('cantprestacioneslista')).val(
            config.initial.cantPrestacionesLista
        );

        jQuery(selector('busqueda_prestaciones')).hide();
        jQuery(selector('busqueda_farmacia')).hide();
        jQuery(selector('datos_edicion_prestacion')).hide();
        jQuery(selector('Cierre_Reclamo_Div')).hide();
        jQuery(selector('lista_prestaciones_asociadas')).hide();
        jQuery(selector('lista_contactos_reclamo')).hide();
        jQuery(selector('justificacion_medica_reclamo')).hide();

        jQuery(selector('caso_vinculado')).val(
            config.initial.casoVinculado
        );

        jQuery(selector('reconocidoSSS')).prop(
            'readonly',
            true
        );

        /*
         * El ID permanece en cero para un borrador proveniente
         * de Compras.
         */
        jQuery(selector('idreclamoprestacion')).val('0');

        load = true;

        sectorIni =
            valor('sector');

        estadoIni =
            valor('estado');

        /*
         * Tanto un RP persistido como un borrador de Compras tienen
         * valores iniciales de cabecera que deben reflejarse en la UI.
         *
         * El borrador no es un RP existente, pero sí contiene:
         *
         * - tipo de pedido;
         * - sector;
         * - prestaciones precargadas.
         */
        var tieneModeloInicial =
            config.initial.reclamoExiste
            || config.initial.borradorDesdeCompras;

        if (tieneModeloInicial) {
            manejarTipoPedidoCierre();
            manejarTipoSector();
        }

        /*
         * Comportamiento exclusivo de un RP persistido.
         */
        if (config.initial.reclamoExiste) {
            jQuery(selector('idreclamoprestacion')).val(
                config.initial.idReclamo
            );

            jQuery(selector('botonsavereclamo')).hide();

            if (config.initial.tipoGestionCierre !== null
                && config.initial.tipoGestionCierre !== undefined) {

                seleccionarValor(
                    'tipo_gestion_cierre_reclamo',
                    config.initial.tipoGestionCierre
                );
            }

            if (config.initial.idObservacionMedica !== null
                && config.initial.idObservacionMedica !== undefined) {

                seleccionarValor(
                    'observacion_medica',
                    config.initial.idObservacionMedica
                );
            }

            if (Number(config.initial.estadoReclamo) === 3) {
                jQuery(selector('Cierre_Reclamo_Div')).show();
                jQuery(selector('botonrevision')).hide();
            }

            if (config.initial.resolucionActiva) {
                jQuery(selector('botonrevision')).hide();

                jQuery(
                    selector('mensajerevisionefectuada')
                ).html(
                    'Revision Efectuada, '
                    + 'el Sistema soporta solo una revision activa '
                    + '(No de baja).'
                );
            }
        }

        /*
         * Comportamiento exclusivo del borrador originado en Compras.
         */
        if (config.initial.borradorDesdeCompras
            && !config.initial.reclamoExiste) {

            jQuery(selector('idreclamoprestacion')).val('0');

            /*
             * El botón existe porque cmd=ADD.
             * Se fuerza visible por si una ejecución anterior del JS
             * lo hubiera ocultado.
             */
            jQuery(selector('botonsavereclamo')).show();
        }

        if (!config.initial.esEdicion) {
            var observacionCierre =
                elemento('reclamo_observacion_cierre');

            if (observacionCierre) {
                observacionCierre.disabled =
                    true;
            }

            jQuery(selector('botonrevision')).hide();
            jQuery(selector('buttonaddprestacion')).hide();
        }

        if (valor('tipopedido') === 'EXCEPCION') {
            traerDescripcion();
        }

        tipoGestionCierreReclamo();
        filtrarLetraComprobante();
        integracionReclamo();
        aplicaEstiloBordeRojoDatosObligatorio();

        if (config.initial.buscarCieInicial) {
            var buscarCie =
                window[
                namespace + 'buscarCieCodigo'
                    ];

            if (typeof buscarCie === 'function') {
                buscarCie();
            }
        }
    }

    function vincularEventos() {
        jQuery(selector('sector'))
            .off('change.viewReclamo')
            .on('change.viewReclamo', function () {
                try {
                    var cantidad = Number(valor('cantprestacioneslista') || 0);
                    if (cantidad >= 1 && load === true) {
                        var confirmar = window.confirm(
                            'Se eliminaran los ítems por no pertenecer al tipo correspondiente\nDesea hacerlo?'
                        );

                        if (confirmar) {
                            var parametros = {};
                            parametros[config.commands.actionParam] = config.commands.reclamoPrestacionalSeccional;
                            jQuery(selector('lista_prestaciones_reclamos')).load(
                                agregarParametros(url('borrarReclamosPrestacionesTodos'), parametros)
                            );
                        } else {
                            seleccionarValor('sector', sectorIni);
                        }
                    }
                } catch (error) {
                    window.alert('error manejarTipoSector ');
                }
            });

        jQuery(selector('integracion'))
            .off('change.viewReclamo')
            .on('change.viewReclamo', function () {
                try {
                    traerDescripcion();
                } catch (error) {
                    window.alert('error integracion ');
                }
            });

        jQuery(selector('estado'))
            .off('change.viewReclamo')
            .on('change.viewReclamo', function () {
                try {
                    var estado = valor('estado');
                    var chkAmparo = estaSeleccionado('chk_amparo');
                    if (String(estado) === '4' && !chkAmparo) {
                        window.alert('Debe seleccionar la marca de Amparo ');
                        seleccionarValor('estado', 1);
                    }
                } catch (error) {
                    window.alert('error estado ');
                }
            });

        jQuery(selector('tipopedido'))
            .off('change.viewReclamo')
            .on('change.viewReclamo', function () {
                try {
                    filtrarLetraComprobante();
                    integracionReclamo();
                } catch (error) {
                    window.alert('error tipopedido ');
                }
            });

        jQuery(selector('chk_amparo'))
            .off('change.viewReclamo')
            .on('change.viewReclamo', function () {
                try {
                    var estado = valor('estado');
                    var chkAmparo = estaSeleccionado('chk_amparo');
                    if (String(estado) === '4' && !chkAmparo) {
                        window.alert('No puede sacar la marca de amparo si el estado es Incompleto ');
                        jQuery(selector('chk_amparo')).prop('checked', true);
                    }
                } catch (error) {
                    window.alert('error chk_amparo ');
                }
            });

        jQuery(selector('tipo_gestion_cierre_reclamo'))
            .off('change.viewReclamo')
            .on('change.viewReclamo', tipoGestionCierreReclamo);

        jQuery(selector('observacion_medica'))
            .off('change.viewReclamo')
            .on('change.viewReclamo', function () {
                try {
                    jQuery(selector('reclamo_observacion_cierre')).text('');
                } catch (error) {
                    window.alert('error observacion_medica text');
                }
            });
    }

    function tipoGestionCierreReclamo() {
        try {
            var tipoResolucion = valor('tipo_gestion_cierre_reclamo');
            if (String(tipoResolucion) === '5') {
                jQuery(selector('observacion_medica_tr')).show();
            } else {
                jQuery(selector('observacion_medica_tr')).hide();
            }
        } catch (error) {
            window.alert('error observacion_medica ');
        }
    }

    function integracionReclamo() {
        try {
            if (valor('tipopedido') === 'EXCEPCION') {
                jQuery('#integracion_label').show();
                jQuery(selector('integracion')).show();
                jQuery('#integracion_desc').show();
                jQuery('#integracion_div').show();
            } else {
                jQuery('#integracion_label').hide();
                jQuery(selector('integracion')).hide();
                jQuery('#integracion_desc').show();
                jQuery('#integracion_div').hide();
            }
        } catch (error) {
            window.alert('error integracion ');
        }
    }

    function filtrarLetraComprobante() {
        var tipoPedido = valor('tipopedido');
        var requestUrl = agregarParametros(url('filtrarLetraComprobante'), {
            tipo_pedido: tipoPedido
        });
        var comprobanteLetra = elemento('comprobante_letra');

        jQuery(selector('comprobante_letra')).prop('disabled', true);
        jQuery.ajax({
            url: requestUrl,
            async: false,
            success: function (data) {
                if (comprobanteLetra) {
                    comprobanteLetra.length = 0;
                }
                jQuery(selector('comprobante_letra')).prop('disabled', false).html(data).fadeIn();
            }
        });
    }

    function buscarNomencladorAutocompletar() {
        var nombreNomenclador = valor('descripcionSeguimiento_filtro') || '';
        var codigoNomenclador = valor('codigoSeguimiento_filtro') || '';
        var tipoNomenclador = valor('tipoNomencladorSeguimiento_filtro');
        var marcaReinliq = null;

        if (nombreNomenclador.length === 0 && codigoNomenclador.length === 0) {
            window.alert(mensaje('ingreseParametrosBusqueda', 'Ingrese parámetros de búsqueda'));
            return;
        }

        if (popupMD === null) {
            popupMD = Liferay.Popup({
                title: 'Búsqueda Nomenclador',
                modal: true,
                width: 700,
                onClose: function () {
                    popupMD = null;
                }
            });
        }

        if (String(tipoNomenclador) === '8') {
            marcaReinliq = 6;
        }

        var esPrestMed = valor('sector') === 'PRESTACIONES MEDICAS' ? 1 : 0;
        var requestUrl = agregarParametros(url('buscarNomenclador'), {
            descripcionnomenclador: nombreNomenclador,
            tiponomenclador: tipoNomenclador,
            codigonomenclador: codigoNomenclador,
            soloActivos: true,
            marcareinliq: marcaReinliq,
            esPrestMed: esPrestMed
        });

        jQuery(popupMD).load(requestUrl);
    }

    function buscarNomencladorAutocompletarEdit() {
        var nombreNomenclador = valor('descripcionSeguimiento_filtro_edit') || '';
        var codigoNomenclador = valor('codigoSeguimiento_filtro_edit') || '';

        if (nombreNomenclador.length === 0 && codigoNomenclador.length === 0) {
            window.alert(mensaje('ingreseParametrosBusqueda', 'Ingrese parámetros de búsqueda'));
            return;
        }

        if (popupMD === null) {
            popupMD = Liferay.Popup({
                title: 'Búsqueda Nomenclador',
                modal: true,
                width: 700,
                onClose: function () {
                    popupMD = null;
                }
            });
        }

        var requestUrl = agregarParametros(url('buscarNomenclador'), {
            descripcionnomenclador: nombreNomenclador,
            tiponomenclador: '0',
            codigonomenclador: codigoNomenclador,
            soloActivos: true
        });
        jQuery(popupMD).load(requestUrl);
    }

    function limpiarNomencladorAutocompletar() {
        jQuery(selector('descripcionSeguimiento_filtro')).val('');
        jQuery(selector('codigoSeguimiento_filtro')).val('');
        jQuery(selector('descripcionSeguimiento_filtro_edit')).val('');
        jQuery(selector('codigoSeguimiento_filtro_edit')).val('');
    }

    function seleccionaCamposNm(tipoNomenclador, codigo, descripcion) {
        jQuery(selector('codigoSeguimiento_filtro')).val(codigo);
        jQuery(selector('descripcionSeguimiento_filtro')).val(descripcion);
        jQuery(selector('nom_seleccionado')).val('1');
        jQuery(selector('tipoNomenclador')).val(tipoNomenclador);

        jQuery(selector('codigoSeguimiento_filtro_edit')).val(codigo);
        jQuery(selector('descripcionSeguimiento_filtro_edit')).val(descripcion);
        jQuery(selector('nom_seleccionado_edit')).val('1');
        jQuery(selector('tipoNomenclador_edit')).val(tipoNomenclador);

        Liferay.Popup.close(popupMD);
    }

    function pasarParametrosAParentNm(tipoNomenclador, codigo, descripcion) {
        seleccionaCamposNm(tipoNomenclador, codigo, descripcion);
        cerrarNm();
    }

    function cerrarDivNm() {
        jQuery('#divSeguimientoSur').hide('slow');
    }

    function cerrarNm() {
        cerrarDivNm();
        if (popupMD) {
            Liferay.Popup.close(popupMD);
        }
    }

    function DatosRevisionOk() {
        var diaRevision = valor('fecharevisionDia');
        var mesRevision = valor('fecharevisionMes');
        var anioRevision = valor('fecharevisionAnio');

        var diaInvalido = isNaN(parseInt(diaRevision, 10));
        var mesInvalido = isNaN(parseInt(mesRevision, 10));
        var anioInvalido = isNaN(parseInt(anioRevision, 10));

        if (diaInvalido || mesInvalido || anioInvalido) {
            window.alert('Debe ingresar la fecha de Revisión');
            return false;
        }

        if (valor('resolucion') === '') {
            window.alert('Debe ingresar la resolución');
            return false;
        }

        var resolucion = elemento('resolucion');
        if (!resolucion || resolucion.selectedIndex === 0) {
            window.alert('Debe seleccionar el tipo de resolucion de la lista.');
            return false;
        }

        var diaOspim = parseInt(valor('fechaospimDia'), 10);
        var mesOspim = parseInt(valor('fechaospimMes'), 10);
        var anioOspim = parseInt(valor('fechaospimAnio'), 10);

        var fechaOspim = new Date(anioOspim, mesOspim, diaOspim);
        var fechaRevision = new Date(
            parseInt(anioRevision, 10),
            parseInt(mesRevision, 10),
            parseInt(diaRevision, 10)
        );
        var hoy = new Date();

        if ((fechaRevision - fechaOspim) / 86400000 < 0) {
            window.alert('La fecha de revision no puede ser inferior a la fecha de Ingreso del Reclamo (Fecha Ospim).');
            return false;
        }

        if ((hoy - fechaRevision) / 86400000 < 0) {
            window.alert('La fecha de revision no puede ser superior a la fecha de hoy.');
            return false;
        }

        return true;
    }

    function ValidarDatosObligatorios(edicion) {
        var cantidadPrestaciones = Number(valor('cantprestacioneslista') || 0);

        var dia = isNaN(parseInt(valor('fechaospimDia'), 10));
        var mes = isNaN(parseInt(valor('fechaospimMes'), 10));
        var anio = isNaN(parseInt(valor('fechaospimAnio'), 10));

        var diaSeccional = isNaN(parseInt(valor('fechaseccionalDia'), 10));
        var mesSeccional = isNaN(parseInt(valor('fechaseccionalMes'), 10));
        var anioSeccional = isNaN(parseInt(valor('fechaseccionalAnio'), 10));

        var diaCierre = isNaN(parseInt(valor('fechacierreDia'), 10));
        var mesCierre = isNaN(parseInt(valor('fechacierreMes'), 10));
        var anioCierre = isNaN(parseInt(valor('fechacierreAnio'), 10));

        var mensajes = [
            'Error en la fecha Ospim.',
            'Debe seleccionar el sector que inicia el reclamo.',
            'Debe seleccionar el estado del reclamo.',
            'Debe seleccionar al Afiliado asociado al reclamo.',
            'Complete la Fecha Seccional o dejela en blanco',
            'Debe seleccionar el tipo de Pedido'
        ];

        var tipoSelectSector = elemento('sector');
        var tipoSelectEstado = elemento('estado');
        var tipoSelectPedido = elemento('tipopedido');
        var cuil = valor('cuil');
        var inte = valor('inte');

        var controles = [
            elemento('fechaospimDia'),
            tipoSelectSector,
            tipoSelectEstado,
            elemento('cuil'),
            elemento('fechaseccionalDia'),
            tipoSelectPedido
        ];

        var condiciones = [
            dia || mes || anio,
            !tipoSelectSector || tipoSelectSector.selectedIndex === 0,
            !tipoSelectEstado || tipoSelectEstado.selectedIndex === 0,
            cuil === '' || inte === '',
            (diaSeccional || mesSeccional || anioSeccional) &&
                (!diaSeccional || !mesSeccional || !anioSeccional),
            !tipoSelectPedido || tipoSelectPedido.selectedIndex === 0
        ];

        var respuesta = true;
        for (var i = 0; i < condiciones.length; i++) {
            if (condiciones[i] && respuesta) {
                respuesta = false;
                window.alert(mensajes[i]);
                if (controles[i]) {
                    controles[i].focus();
                }
            }
        }

        var idGestion = Number(valor('tipo_gestion_cierre_reclamo') || 0);
        var justificacion = valor('justificacionmedcica_reclamo') || '';
        var estadoTexto = jQuery(selector('estado') + ' option:selected').text().trim();

        if (idGestion === 0 && estadoTexto === 'CERRADO') {
            window.alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
            if (elemento('tipo_gestion_cierre_reclamo')) {
                elemento('tipo_gestion_cierre_reclamo').focus();
            }
            return false;
        }

        if (idGestion === 5) {
            if (!window.confirm(
                'Al seleccionar la opción RECHAZADO el sistema rechazará todas las prestaciones del caso, no podrá asociarlas a reintegros. Está seguro ?'
            )) {
                return false;
            }
        }

        if (valor('auditoriaadministrativa') !== 'Ok' && justificacion.length === 0 && respuesta) {
            window.alert('Tiene que ingresar la justificación médica del Caso para efectuar el Cierre del Caso.');
            jQuery(selector('justificacionmedcica_reclamo')).focus();
            respuesta = false;
        }

        if (idGestion < 1 && respuesta && estadoTexto === 'CERRADO') {
            window.alert('Debe ingresar el tipo de gestión del Reclamo ( Sección Cierre de Reclamo) ');
            if (elemento('tipo_gestion_cierre_reclamo')) {
                elemento('tipo_gestion_cierre_reclamo').focus();
            }
            respuesta = false;
        }

        if ((diaCierre || mesCierre || anioCierre) && respuesta) {
            window.alert('Debe ingresar la fecha de Cierre del Reclamo');
            if (elemento('fechacierreDia')) {
                elemento('fechacierreDia').focus();
            }
            respuesta = false;
        }

        if (estadoTexto === 'CERRADO' && Number(valor('cantrevisionesactivas') || 0) < 1 && respuesta) {
            window.alert('Recuerde, debe tener registrada por lo menos una revisión activa para el cierre del caso!!!!.');
            respuesta = false;
        }

        if (edicion && addprestacion && cantidadPrestaciones < 1 && respuesta) {
            window.alert('Debe tener ingresada por lo menos una prestación');
            respuesta = false;
        }

        if (valor('tipopedido') === 'EXCEPCION' && String(valor('integracion')) === '0') {
            window.alert('Debe seleccionar un tipo de integración ');
            respuesta = false;
        }

        if (edicion && respuesta && idGestion !== 0 && idGestion !== 5 && cantidadPrestaciones < 1) {
            window.alert('Debe tener ingresada por lo menos una prestación para poder cerrar el reclamo.');
            respuesta = false;
        }

        var codigoError = '';
        var requestUrl = agregarParametros(url('validarReclamoAfiliadoPrestaciones'), {
            baja: valor('baja_fecha')
        });

        jQuery.ajax({
            url: requestUrl,
            async: false,
            success: function (data) {
                codigoError = jQuery.parseJSON(data).codError;
            }
        });

        if (String(codigoError) === '6') {
            window.alert('La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado');
            respuesta = false;
        }

        return respuesta;
    }

    function saveReclamo() {
        if (!ValidarDatosObligatorios(false)) {
            return;
        }

        jQuery(selector('tipogestion')).val(valor('tipo_gestion_cierre_reclamo'));
        var form = formulario('reclamo_fm');
        establecerComando(form, config.commands.save);
        form.method = 'post';
        submitForm(form, agregarParametros(url('editarReclamoAction'), { esDatosTab: true }));
    }

    function volverEstadoObservado() {
        var idReclamo = valor('id_reclamosel');
        var confirmar = window.confirm(
            'Estas observando la precarga, la misma será devuelta a la seccional. \nEstas seguro?'
        );

        if (confirmar) {
            var popup = Liferay.Popup({
                title: mensaje('observacionInterna', 'Observación interna'),
                modal: true,
                width: 700
            });
            jQuery(popup).load(agregarParametros(url('observar'), { idReclamo: idReclamo }));
        }
    }

    function editaReclamo(fromAutoriza) {
        if (fromAutoriza) {
            abreAutorizacion();
        }

        if (!ValidarDatosObligatorios(true)) {
            return;
        }

        jQuery(selector('tipogestion')).val(valor('tipo_gestion_cierre_reclamo'));
        var form = formulario('reclamo_fm');
        establecerComando(form, config.commands.update);
        form.method = 'post';
        submitForm(form, agregarParametros(url('editarReclamoAction'), { esDatosTab: true }));
    }

    function reabrirReclamo(fromAutoriza) {
        if (fromAutoriza) {
            abreAutorizacion();
        }

        var form = formulario('reclamo_fm');
        var accionEnCurso = obtenerComando(form);
        establecerComando(form, config.commands.restore);
        form.method = 'post';
        submitForm(form, agregarParametros(url('editarReclamoAction'), {
            accionEnCurso: accionEnCurso,
            moverATab: 'plan_prest',
            esDatosTab: false
        }));
    }

    function manejarTipoGestion() {
        var idGestion = valor('tipo_gestion_cierre_reclamo');
        var sector = valor('sector');
        var nroLote = valor('nroLote');
        jQuery(selector('tipogestion')).val(idGestion);

        if (String(idGestion) === '1' && sector === 'PRESTACIONES MEDICAS' &&
            (nroLote === null || nroLote === '' || String(nroLote) === '0')) {
            jQuery.ajax({
                url: url('proponeLoteReclamoPrestacional'),
                success: function (data) {
                    jQuery(selector('nroLote')).val(jQuery.parseJSON(data).lote);
                }
            });
        }

        if (String(idGestion) !== '1' || sector !== 'PRESTACIONES MEDICAS') {
            jQuery(selector('nroLote')).val('');
        }
    }

    function manejarListaPresentes() {
        var lista = elemento('presenteslista');
        if (lista) {
            jQuery(selector('presentes')).val(lista.value);
        }
    }

    function cambioResolucion() {
        try {
            var resolucion = elemento('resolucion');
            var justificacion = valor('justificacionmedcica_reclamo') || '';
            var respuestaResolucion = elemento('respresolucion');

            if (resolucion && respuestaResolucion && resolucion.selectedIndex > 0 &&
                justificacion.length === 0 && respuestaResolucion.selectedIndex !== 1) {
                jQuery(selector('justificacionmedcica_reclamo')).focus();
                resolucion.selectedIndex = 0;
                window.alert('Tiene que ingresar la Justificacion Medica del Caso para ingresar la revision.');
            }
        } catch (error) {
            // Se conserva el comportamiento silencioso original.
        }
    }

    function manejarTipoPedido() {
        var tipoPedido = elemento('tipopedido');
        if (tipoPedido && tipoPedido.selectedIndex === 0) {
            window.alert('El tipo de pedido es obligatorio');
            tipoPedido.focus();
        }
    }

    function cambioTipoPedido() {
        var tipoSector = elemento('sector');
        if (tipoSector && tipoSector.selectedIndex !== 0) {
            manejarTipoSector();
        }
    }

    function manejarTipoPedidoCierre() {
        var tipoPedido = elemento('tipopedido');
        var gestion = jQuery(selector('tipo_gestion_cierre_reclamo'));
        gestion.empty();
        gestion.append(new Option('SELECCIONE UNA OPCION', '0'));
        seleccionarValor('tipo_gestion_cierre_reclamo', '0');

        if (!tipoPedido) {
            return;
        }

        if (tipoPedido.value === 'EXCEPCION') {
            gestion.append(new Option('FACTURACION DIRECTA', '3'));
            gestion.append(new Option('PAGADO POR MECANISMO INTEGRACION', '6'));
        }
        if (tipoPedido.value === 'REINTEGRO') {
            gestion.append(new Option('REINTEGRO', '4'));
        }
        if (tipoPedido.value === 'EXTRACAPITA') {
            gestion.append(new Option('EXTRACAPITA', '1'));
        }
        gestion.append(new Option('RECHAZADO', '5'));
    }

    function manejarTipoSector() {
        var tipoSector = elemento('sector');
        var tipoPedido = elemento('tipopedido');

        try {
            jQuery(selector('busqueda_prestaciones')).show();
            jQuery(selector('busqueda_farmacia')).hide();
            jQuery(selector('nom_seleccionado')).val('1');
            jQuery(selector('troquel')).val('');
            jQuery(selector('codigoSeguimiento_filtro')).val('');
            jQuery(selector('tipoNomencladorSeguimiento_filtro')).val('');

            if (!tipoSector || !tipoPedido) {
                return;
            }

            if (tipoSector.selectedIndex === 3) {
                if (tipoPedido.selectedIndex !== 1) {
                    if (tipoPedido.selectedIndex === 2) {
                        jQuery(selector('busqueda_farmacia')).show();
                        jQuery(selector('busqueda_prestaciones')).hide();
                    }
                    jQuery(selector('nom_seleccionado')).val('2');
                } else {
                    jQuery(selector('tipoNomencladorSeguimiento_filtro')).val(9);
                }
            }

            if (tipoSector.selectedIndex === 1) {
                jQuery(selector('tipoNomencladorSeguimiento_filtro')).val(8);
            } else if (tipoSector.selectedIndex === 6) {
                jQuery(selector('tipoNomencladorSeguimiento_filtro')).val(1);
            }
        } catch (error) {
            window.alert('error manejarTipoSector() ');
        }
    }

    function agregarRevision() {
        var revisionConCierre = false;
        if (!DatosRevisionOk()) {
            return;
        }

        var resolucion = valor('resolucion');
        var presentes = valor('presentes');
        var respuestaResolucion = valor('respresolucion');
        var observacionMedica = valor('observacion_medica');

        var resolucionControl = elemento('resolucion');
        var presentesControl = elemento('presentes');
        var respuestaControl = elemento('respresolucion');

        if (resolucionControl && resolucionControl.selectedIndex === 0) {
            resolucion = '';
        }
        if (presentesControl && typeof presentesControl.selectedIndex !== 'undefined' && presentesControl.selectedIndex === 0) {
            presentes = '';
        }
        if (respuestaControl && respuestaControl.selectedIndex === 0) {
            respuestaResolucion = '';
        }

        jQuery(selector('auditoriaadministrativa')).val('');
        if (respuestaControl && respuestaControl.selectedIndex === 1) {
            jQuery(selector('auditoriaadministrativa')).val('Ok');
        }

        var parametros = {
            resolucion: resolucion,
            presentes: presentes,
            respresolucion: respuestaResolucion,
            revisionFechaVtoDia: valor('fecharevisionDia'),
            revisionFechaVtoMes: valor('fecharevisionMes'),
            revisionFechaVtoAnio: valor('fecharevisionAnio'),
            reclamoobservacion: valor('observacion_revision'),
            observacionMedica: observacionMedica
        };

        if (String(resolucion).toUpperCase() !== 'AUTORIZADO') {
            if (!window.confirm('Confirma el Cierre del Caso con el Rechazo en la revision ?')) {
                return false;
            }

            seleccionarValor('estado', '3');
            controlarEstadoCerrado();

            var gestion = elemento('tipo_gestion_cierre_reclamo');
            if (gestion) {
                gestion.disabled = false;
            }
            seteaControlesFacturacionDirecta(true);
            seleccionarValor('tipo_gestion_cierre_reclamo', '5');
            jQuery(selector('tipogestion')).val(valor('tipo_gestion_cierre_reclamo'));
            jQuery(selector('reclamo_observacion_cierre')).val('RECHAZO DE LA PRESTACION EN LA REVISION.');
            revisionConCierre = true;
            jQuery(selector('cantrevisionesactivas')).val(1);
            desactivaCheckCierre();
        }

        jQuery(selector('botonrevision')).hide();
        jQuery(selector('mensajerevisionefectuada')).html(
            'Revisión Efectuada, el Sistema soporta solo una revisión activa (No de baja).'
        );

        jQuery(selector('lista_revisiones')).load(url('listaRevisionesReclamo'), parametros, function () {
            jQuery(selector('buscando')).hide();
        });

        jQuery(selector('resolucion')).val('');
        jQuery(selector('presentes')).val('');
        jQuery(selector('respresolucion')).val('');
        if (elemento('fecharevisionDia')) elemento('fecharevisionDia').selectedIndex = 0;
        if (elemento('fecharevisionMes')) elemento('fecharevisionMes').selectedIndex = 0;
        if (elemento('fecharevisionAnio')) elemento('fecharevisionAnio').selectedIndex = 0;
        jQuery(selector('observacion_revision')).val('');

        if (revisionConCierre) {
            if (config.initial.reclamoExiste) {
                editaReclamo(false);
            } else {
                saveReclamo();
            }
        }
    }

    function verPrestacionesAsociadas() {
        var boton = elemento('botonprestacionesasociadas');
        if (!boton) {
            return;
        }

        if (boton.value === 'Ver Prestaciones del Caso Asociado.') {
            jQuery(selector('lista_prestaciones_asociadas')).show();
            boton.value = 'Ocultar Prestaciones del Caso Asociado.';
        } else {
            jQuery(selector('lista_prestaciones_asociadas')).hide();
            boton.value = 'Ver Prestaciones del Caso Asociado.';
        }
    }

    function ocultaContactosDelReclamo() {
        jQuery(selector('lista_contactos_reclamo')).hide();
        jQuery(selector('botoncontactosreclamo')).show();
        var boton = elemento('botoncontactosreclamo');
        if (boton) {
            boton.value = 'Ver Contactos Asociados al Caso.';
        }
    }

    function verContactosDelReclamo() {
        var cuil = valor('cuil');
        var inte = valor('inte');
        var idReclamoPrestacion = valor('idreclamoprestacion');
        var modoConsulta = valor('consultareclamo');
        var boton = elemento('botoncontactosreclamo');

        if (cuil === '' || inte === '') {
            window.alert('Debe seleccionar al Afiliado para ver sus contactos.');
            if (elemento('cuil')) elemento('cuil').focus();
            return false;
        }

        if (!boton || boton.value !== 'Ver Contactos Asociados al Caso.') {
            return;
        }

        jQuery(selector('lista_contactos_reclamo')).show();
        jQuery(selector('botoncontactosreclamo')).hide();
        jQuery(selector('justificacion_medica_reclamo')).hide();

        if (Number(idReclamoPrestacion) < 1 &&
            cuil === valor('cuiltitular') && inte === valor('intetitular')) {
            return false;
        }

        jQuery(selector('cuiltitular')).val(cuil);
        jQuery(selector('intetitular')).val(inte);

        jQuery(selector('lista_contactos_reclamo')).load(url('listaContactosReclamo'), {
            cuil_contacto: cuil,
            inte_contacto: inte,
            idreclamoprestacion: idReclamoPrestacion,
            modoconsulta: modoConsulta
        }, function () {
            jQuery(selector('buscando')).hide();
        });
    }

    function editarPrestacionSeleccionada(tipoAccion) {
        var frecuencia = valor('frecuenciaEdicion');
        var cantidad = valor('cantidadEdicion');
        var importe = valor('importeEdicion');
        var cargoOspim = valor('cargoospimEdicion');
        var cargoPs = valor('cargopsEdicion');
        var cargoImesa = valor('cargoimesaEdicion');
        var reconocidoSSS = valor('reconocidoSSSEdicion');
        var observaciones = valor('observacion_prestacionEdicion');
        var idPrestacion = valor('codigoprestacion');
        var idRegistro = valor('idRegistro');
        var recuperableSur = valor('recuperable_surEdicion');
        var cpbteTipo = valor('comprobante_tipo_edicion');
        var cpbteNro = valor('comprobante_nro_edicion');
        var cpbteDia = valor('fechaComprobanteDiaEdicion');
        var cpbteMes = valor('fechaComprobanteMesEdicion');
        var cpbteAnio = valor('fechaComprobanteAnioEdicion');
        var cpbteCantidad = valor('cantidadFC_edicion');
        var cpbteImporte = valor('importeUnitarioFC_edicion');
        var importeFC = valor('importeFC_edicion');
        var cpbteCuit = valor('cuit_entidad_edicion');
        var cpbteSucursal = valor('comprobante_suc_edicion');
        var cpbteCuitSucursal = valor('sucursal_entidad_edicion');
        var cpbteLetra = valor('comprobante_letra_edicion');
        var flagAmparo = String(valor('estado')) === '4' && estaSeleccionado('chk_amparo');

        var tieneDatosAreaMedica = tieneValorNumerico(importe) || tieneValorNumerico(cargoOspim) ||
            tieneValorNumerico(cargoPs) || tieneValorNumerico(cargoImesa) || tieneValorNumerico(reconocidoSSS);

        if (tieneDatosAreaMedica) {
            if (String(recuperableSur) === '0') {
                window.alert('Debe seleccionar el campo Recuperable');
                return false;
            }
            if (!validaMontosEdicion()) {
                return false;
            }
        }

        if (frecuencia === 'SELECCIONE') {
            frecuencia = '';
        }

        var fechaPrestacionDia = valor('fechaPrestacionDiaEdicion');
        var fechaPrestacionMes = valor('fechaPrestacionMesEdicion');
        var fechaPrestacionAnio = valor('fechaPrestacionAnioEdicion');
        var idMedicamentoEdit = valor('troquel_edit');
        var nombreMedicamentoEdit = valor('nombre_medicamento_edit');

        if (!flagAmparo && (frecuencia === null || frecuencia === '')) {
            window.alert('Debe seleccionar la frecuencia correspondiente.');
            return false;
        }
        if (!flagAmparo && cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && cpbteLetra === '') {
            window.alert('Debe seleccionar la letra del comprobante');
            return false;
        }
        if (!flagAmparo && esVacioOCero(importeFC)) {
            window.alert('Debe ingresar el importe de la Factura.');
            return false;
        }
        if (!flagAmparo && esVacio(cpbteCuit)) {
            window.alert('Debe ingresar el CUIT del Comprobante');
            return false;
        }
        if (!flagAmparo && esVacio(cpbteCuitSucursal)) {
            window.alert('Debe ingresar la sucursal del CUIT del Comprobante');
            return false;
        }
        if (!flagAmparo && cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && esVacio(cpbteSucursal)) {
            window.alert('Debe ingresar la Sucursal del Comprobante');
            return false;
        }
        if (!flagAmparo && cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && esVacio(cpbteNro)) {
            window.alert('Debe ingresar el Nro del Comprobante');
            return false;
        }
        if (!flagAmparo && fechaIncompleta(cpbteDia, cpbteMes, cpbteAnio)) {
            window.alert('Debe ingresar la fecha del Comprobante');
            return false;
        }
        if (!flagAmparo && esVacioOCero(cpbteCantidad)) {
            window.alert('Debe ingresar la cantidad del Comprobante');
            return false;
        }
        if (!flagAmparo && esVacioOCero(cpbteImporte)) {
            window.alert('Debe ingresar importe unitario del Comprobante');
            return false;
        }

        var codigoEdit = valor('codigoSeguimiento_filtro_edit');
        var descripcionEdit = valor('descripcionSeguimiento_filtro_edit');
        var nomencladorSeleccionadoEdit = valor('nom_seleccionado_edit') || valor('nom_seleccionado');
        var tipoNomencladorEdit = valor('tipoNomenclador_edit') || valor('tipoNomenclador');

        if (String(nomencladorSeleccionadoEdit) === '1') {
            if (Number(codigoEdit) < 1 || esVacio(descripcionEdit)) {
                window.alert('Debe seleccionar la prestación');
                return false;
            }
        } else {
            if (Number(idMedicamentoEdit) < 1 || esVacio(nombreMedicamentoEdit)) {
                window.alert('Debe seleccionar el medicamento');
                return false;
            }
        }

        if (fechaIncompleta(fechaPrestacionDia, fechaPrestacionMes, fechaPrestacionAnio)) {
            window.alert('Debe ingresar la fecha de la Prestación');
            return false;
        }

        if (!ValidaDatosReclamoEditar()) {
            return false;
        }

        var parametros = {
            frecuencia: frecuencia,
            importe: importe,
            cargoospim: cargoOspim,
            cargops: cargoPs,
            cargoimesa: cargoImesa,
            prestacion: 'Graba Edicion',
            idprestacion: idPrestacion,
            idRegistro: idRegistro,
            grabaedicion: true,
            estadoAprobacion: tipoAccion,
            recuperableSur: recuperableSur,
            cantidad: cantidad,
            observaciones: observaciones,
            cpbte_tipo: cpbteTipo,
            cpbte_nro: cpbteNro,
            cpbte_dia: cpbteDia,
            cpbte_mes: cpbteMes,
            cpbte_anio: cpbteAnio,
            cpbte_cantidad: cpbteCantidad,
            cpbte_importe: cpbteImporte,
            cpbte_cuit: cpbteCuit,
            cpbte_sucursal: cpbteSucursal,
            importeFC: importeFC,
            cpbte_cuit_sucursal: cpbteCuitSucursal,
            cpbte_letra: cpbteLetra,
            fecha_prestacion_dia: fechaPrestacionDia,
            fecha_prestacion_mes: fechaPrestacionMes,
            fecha_prestacion_anio: fechaPrestacionAnio,
            id_medicamento_edit: idMedicamentoEdit,
            nombre_medicamento_edit: nombreMedicamentoEdit,
            codigoSeguimiento_filtro_edit: codigoEdit,
            descripcionSeguimiento_filtro_edit: descripcionEdit,
            nom_seleccionado_edit: nomencladorSeleccionadoEdit,
            tipoNomenclador_edit: tipoNomencladorEdit,
            reconocidoSSS: reconocidoSSS,
            cuil: valor('cuil'),
            inte: valor('inte'),
            id_tercerizadora: valor('id_tercerizadora')
        };

        if (cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && !validarExisteComprobante(parametros)) {
            return false;
        }

        jQuery(selector('lista_prestaciones_reclamos')).load(url('editarReclamosPrestaciones'), parametros, function () {
            jQuery(selector('buscando')).hide();
        });

        limpiarFormularioPrestacionEdicion();
        addprestacion = false;
        cancelaEdicionPrestacion();
    }

    function cancelaEdicionPrestacion() {
        jQuery(selector('datos_edicion_prestacion')).hide();
        manejarTipoSector();
        jQuery(selector('datos_prestacion_ingreso')).show();
        limpiarNomencladorAutocompletar();

        if (typeof window.onOffcombosestadosprestaciones === 'function') {
            window.onOffcombosestadosprestaciones(true);
        }

        var controlAccion = elemento('tipoaccionprestacion');
        if (!controlAccion || !controlAccion.value) {
            return;
        }

        var partes = controlAccion.value.split('-');
        var combo = document.getElementById('comboestadosreclamo' + partes[1]);
        if (combo) {
            combo.selectedIndex = 0;
        }
        controlAccion.value = '';
    }

    function agregarPrestacion() {
        var frecuencia = valor('frecuencia');
        var importe = valor('importe');
        var cantidad = valor('cantidad');
        var cargoOspim = valor('cargoospim');
        var cargoPs = valor('cargops');
        var cargoImesa = valor('cargoimesa');
        var reconocidoSSS = valor('reconocidoSSS');
        var observaciones = valor('observacion_prestacion');
        var troquel = valor('troquel');
        var prestacion = valor('codigoSeguimiento_filtro');
        var tipoNomenclador = valor('nom_seleccionado');
        var nombreMedicamento = valor('nombre_medicamento');
        var nombrePrestacion = valor('descripcionSeguimiento_filtro');
        var tipoNomencladorPrestacion = valor('tipoNomenclador');
        var recuperableSur = valor('recuperable_sur');
        var cpbteTipo = valor('comprobante_tipo');
        var cpbteNro = valor('comprobante_nro');
        var cpbteDia = valor('fechaComprobanteDia');
        var cpbteMes = valor('fechaComprobanteMes');
        var cpbteAnio = valor('fechaComprobanteAnio');
        var cpbteCantidad = valor('cantidadFC');
        var cpbteImporte = valor('importeUnitarioFC');
        var importeFC = valor('importeFC');
        var cpbteCuit = valor('cuit_entidad');
        var cpbteCuitSucursal = valor('sucursal_entidad');
        var cpbteSucursal = valor('comprobante_suc');
        var cpbteLetra = valor('comprobante_letra');
        var flagAmparo = String(valor('estado')) === '4' && estaSeleccionado('chk_amparo');

        var tieneDatosAreaMedica = tieneValorNumerico(importe) || tieneValorNumerico(cargoOspim) ||
            tieneValorNumerico(cargoPs) || tieneValorNumerico(cargoImesa) || tieneValorNumerico(reconocidoSSS);

        if (tieneDatosAreaMedica) {
            if (String(recuperableSur) === '0') {
                window.alert('Debe seleccionar el campo Recuperable');
                return false;
            }
            if (!ValidaMontos()) {
                return false;
            }
        }

        if (esVacio(tipoNomenclador)) {
            window.alert('Debe seleccionar el sector');
            return false;
        }

        if (String(tipoNomenclador) === '1') {
            if (Number(prestacion) < 1 || esVacio(nombrePrestacion)) {
                window.alert('Debe seleccionar la prestación');
                return false;
            }
        } else if (Number(troquel) < 1 || esVacio(nombreMedicamento)) {
            window.alert('Debe seleccionar el medicamento');
            return false;
        }

        var fechaPrestacionDia = valor('fechaPrestacionDia');
        var fechaPrestacionMes;
        var fechaPrestacionAnio;

        if (esVacioOCero(fechaPrestacionDia)) {
            fechaPrestacionDia = valor('fechaPrestacionDiaFarmacia');
            fechaPrestacionMes = valor('fechaPrestacionMesFarmacia');
            fechaPrestacionAnio = valor('fechaPrestacionAnioFarmacia');
        } else {
            fechaPrestacionMes = valor('fechaPrestacionMes');
            fechaPrestacionAnio = valor('fechaPrestacionAnio');
        }

        if (frecuencia === 'SELECCIONE') {
            frecuencia = '';
        }

        var frecuenciaControl = elemento('frecuencia');
        if (!flagAmparo && frecuenciaControl && frecuenciaControl.selectedIndex === 0) {
            window.alert('Debe seleccionar la frecuencia correspondiente.');
            return false;
        }
        if (!flagAmparo && cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && cpbteLetra === '') {
            window.alert('Debe seleccionar la letra del comprobante');
            return false;
        }
        if (!flagAmparo && esVacioOCero(importeFC)) {
            window.alert('Debe ingresar el importe de la Factura.');
            return false;
        }
        if (!flagAmparo && esVacio(cpbteCuit)) {
            window.alert('Debe ingresar el CUIT del Comprobante');
            return false;
        }
        if (!flagAmparo && esVacio(cpbteCuitSucursal)) {
            window.alert('Debe ingresar la sucursal del CUIT del Comprobante');
            return false;
        }
        if (!flagAmparo && cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && esVacio(cpbteSucursal)) {
            window.alert('Debe ingresar la Sucursal del Comprobante');
            return false;
        }
        if (!flagAmparo && cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && esVacio(cpbteNro)) {
            window.alert('Debe ingresar el Nro del Comprobante');
            return false;
        }
        if (!flagAmparo && cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && fechaIncompleta(cpbteDia, cpbteMes, cpbteAnio)) {
            window.alert('Debe ingresar la fecha del Comprobante');
            return false;
        }
        if (fechaIncompleta(fechaPrestacionDia, fechaPrestacionMes, fechaPrestacionAnio)) {
            window.alert('Debe ingresar la fecha de la Prestación');
            return false;
        }
        if (!flagAmparo && esVacioOCero(cpbteCantidad)) {
            window.alert('Debe ingresar la cantidad del Comprobante');
            return false;
        }
        if (!flagAmparo && esVacioOCero(cpbteImporte)) {
            window.alert('Debe ingresar importe unitario del Comprobante');
            return false;
        }

        var tipoPedidoControl = elemento('tipopedido');
        if (tipoPedidoControl && tipoPedidoControl.selectedIndex === 0) {
            window.alert('Debe seleccionar el Tipo de Pedido.');
            return false;
        }

        if (!ValidaDatosReclamo()) {
            return false;
        }

        var parametros = {
            frecuencia: frecuencia,
            importe: importe,
            cargoospim: cargoOspim,
            cargops: cargoPs,
            cargoimesa: cargoImesa,
            troquel: troquel,
            prestacion: prestacion,
            tiponomenclador: tipoNomenclador,
            nombre_medicamento: nombreMedicamento,
            nombre_prestacion: nombrePrestacion,
            tiponomnecladorprestacion: tipoNomencladorPrestacion,
            recuperableSur: recuperableSur,
            cantidad: cantidad,
            observaciones: observaciones,
            cpbte_tipo: cpbteTipo,
            cpbte_nro: cpbteNro,
            cpbte_dia: cpbteDia,
            cpbte_mes: cpbteMes,
            cpbte_anio: cpbteAnio,
            cpbte_cantidad: cpbteCantidad,
            cpbte_importe: cpbteImporte,
            cpbte_cuit: cpbteCuit,
            cpbte_sucursal: cpbteSucursal,
            importeFC: importeFC,
            cpbte_cuit_sucursal: cpbteCuitSucursal,
            cpbte_letra: cpbteLetra,
            fecha_prestacion_dia: fechaPrestacionDia,
            fecha_prestacion_mes: fechaPrestacionMes,
            fecha_prestacion_anio: fechaPrestacionAnio,
            reconocidoSSS: reconocidoSSS,
            cuil: valor('cuil'),
            inte: valor('inte')
        };

        if (cpbteTipo !== 'OTR' && cpbteTipo !== 'AUT' && !validarExisteComprobante(parametros)) {
            return false;
        }

        jQuery(selector('lista_prestaciones_reclamos')).load(url('listaPrestacionesReclamos'), parametros, function () {
            jQuery(selector('buscando')).hide();
        });

        limpiarFormularioPrestacionAlta();
        addprestacion = true;

        if (String(valor('estado')) === '3') {
            jQuery(selector('montoPsPrestaciones')).val(cargoPs);
        }
    }

    function controlarEstadoCerrado() {
        if (String(valor('estado')) === '3') {
            if (Number(config.initial.cantRevisiones) > 0) {
                jQuery(selector('Cierre_Reclamo_Div')).show();
                if (config.initial.debitoTercerizadora && jQuery(selector('debitoprestadora')).length) {
                    jQuery(selector('debitoprestadora')).prop('checked', true);
                }
            } else {
                window.alert('Debe agregar una Revisión');
                seleccionarValor('estado', estadoIni);
            }
        } else {
            jQuery(selector('Cierre_Reclamo_Div')).hide();
            jQuery(selector('nroLote')).val('');
        }
    }

    function imprimirReclamo() {
        window.location.href = agregarParametros(config.pdfServletUrl, {
            accion: 'reclamoprestacional',
            idreclamo: config.initial.idReclamo || 0
        });
    }

    function ValidaDatosReclamo() {
        var sector = valor('sector');
        var fechaPrestacion;

        if (sector === 'FARMACIA') {
            fechaPrestacion = {
                dia: valor('fechaPrestacionDiaFarmacia'),
                mes: valor('fechaPrestacionMesFarmacia'),
                anio: valor('fechaPrestacionAnioFarmacia')
            };
        } else {
            fechaPrestacion = {
                dia: valor('fechaPrestacionDia'),
                mes: valor('fechaPrestacionMes'),
                anio: valor('fechaPrestacionAnio')
            };
        }

        var parametros = {
            cpbte_dia: valor('fechaComprobanteDia'),
            cpbte_mes: valor('fechaComprobanteMes'),
            cpbte_anio: valor('fechaComprobanteAnio'),
            fecha_prestacion_dia: fechaPrestacion.dia,
            fecha_prestacion_mes: fechaPrestacion.mes,
            fecha_prestacion_anio: fechaPrestacion.anio,
            cpbteCuit: valor('cuit_entidad'),
            tipopedido: valor('tipopedido'),
            troquel: valor('troquel'),
            prestacion: valor('codigoSeguimiento_filtro'),
            tiponomenclador: valor('nom_seleccionado'),
            tiponomencladorprestacion: valor('tiponomenclador'),
            baja: valor('baja_fecha')
        };

        return procesarValidacionReclamo(agregarParametros(url('validarReclamo'), parametros), parametros.cpbteCuit);
    }

    function ValidaDatosReclamoEditar() {
        var parametros = {
            cpbte_dia: valor('fechaComprobanteDiaEdicion'),
            cpbte_mes: valor('fechaComprobanteMesEdicion'),
            cpbte_anio: valor('fechaComprobanteAnioEdicion'),
            fecha_prestacion_dia: valor('fechaPrestacionDiaEdicion'),
            fecha_prestacion_mes: valor('fechaPrestacionMesEdicion'),
            fecha_prestacion_anio: valor('fechaPrestacionAnioEdicion'),
            cpbteCuit: valor('cuit_entidad_edicion'),
            tipopedido: valor('tipopedido'),
            troquel: valor('troquel_edit'),
            prestacion: valor('codigoSeguimiento_filtro_edit'),
            tiponomenclador: valor('nom_seleccionado_edit') || valor('nom_seleccionado'),
            tiponomencladorprestacion: valor('tipoNomenclador_edit') || valor('tiponomenclador'),
            baja: valor('baja_fecha')
        };

        return procesarValidacionReclamo(agregarParametros(url('validarReclamo'), parametros), parametros.cpbteCuit);
    }

    function procesarValidacionReclamo(requestUrl, cpbteCuit) {
        var codigoError = '';
        jQuery.ajax({
            url: requestUrl,
            async: false,
            success: function (data) {
                codigoError = jQuery.parseJSON(data).codError;
            }
        });

        var errores = {
            '1': 'La fecha de la prestación no puede ser posterior',
            '2': 'La fecha del comprobante no puede ser posterior',
            '3': 'Prestador CUIT ' + cpbteCuit + ' no se encuentra cargado para poder liquidar',
            '4': 'No existe Prestación en el nomenclador',
            '5': 'No existe medicamento en el nomenclador',
            '6': 'La fecha de la prestación no puede ser posterior a la fecha de baja del afiliado'
        };

        if (errores[String(codigoError)]) {
            window.alert(errores[String(codigoError)]);
            return false;
        }
        return true;
    }

    function validarExisteComprobante(parametros) {
        var requestUrl = agregarParametros(url('validarExisteComprobante'), {
            frecuencia: parametros.frecuencia,
            troquel: parametros.troquel,
            prestacion: parametros.prestacion,
            cpbte_tipo: parametros.cpbte_tipo,
            cpbte_nro: parametros.cpbte_nro,
            cpbte_dia: parametros.cpbte_dia,
            cpbte_mes: parametros.cpbte_mes,
            cpbte_anio: parametros.cpbte_anio,
            cpbte_cuit: parametros.cpbte_cuit,
            cpbte_sucursal: parametros.cpbte_sucursal,
            cpbte_cuit_sucursal: parametros.cpbte_cuit_sucursal,
            cpbte_letra: parametros.cpbte_letra,
            fecha_prestacion_dia: parametros.fecha_prestacion_dia,
            fecha_prestacion_mes: parametros.fecha_prestacion_mes,
            fecha_prestacion_anio: parametros.fecha_prestacion_anio,
            tiponomnecladorprestacion: parametros.tiponomnecladorprestacion,
            tiponomenclador: parametros.tiponomenclador,
            idRegistro: parametros.idRegistro,
            id_medicamento_edit: parametros.id_medicamento_edit,
            nombre_medicamento_edit: parametros.nombre_medicamento_edit,
            codigoSeguimiento_filtro_edit: parametros.codigoSeguimiento_filtro_edit,
            descripcionSeguimiento_filtro_edit: parametros.descripcionSeguimiento_filtro_edit,
            nom_seleccionado_edit: parametros.nom_seleccionado_edit,
            tipoNomenclador_edit: parametros.tipoNomenclador_edit,
            cuil: parametros.cuil,
            inte: parametros.inte
        });

        var existe = false;
        var mensajeError = '';
        jQuery.ajax({
            url: requestUrl,
            async: false,
            success: function (data) {
                var respuesta = jQuery.parseJSON(data);
                existe = respuesta.existe === true || respuesta.existe === 'true';
                mensajeError = respuesta.mensajeError || '';
            }
        });

        if (existe) {
            window.alert('Ya existe una prestación en esa fecha para el mismo comprobante');
            return false;
        }
        if (mensajeError !== '') {
            window.alert(mensajeError);
            return false;
        }
        return true;
    }

    function evaluarOnSectorListaEnCero() {
        jQuery(selector('cantprestacioneslista')).val('0');
        if (elemento('tipo_gestion_cierre_reclamo')) {
            elemento('tipo_gestion_cierre_reclamo').selectedIndex = 0;
        }
        seteaControlesFacturacionDirecta(false);
    }

    function validarSiNumero(numero) {
        return /^([0-9])*$/.test(numero);
    }

    function validaMonto(evento) {
        var tecla = document.all ? evento.keyCode : evento.which;
        var caracter = String.fromCharCode(tecla);
        if (tecla === 8 || tecla === 46 || tecla === 0) {
            return true;
        }
        return validarSiNumero(caracter);
    }

    function verCrmContacto(idContSerial) {
        var parametros = {};
        parametros[config.commands.cmdParam] = config.commands.view;
        parametros.idContactoSerial = idContSerial;

        popupCRM = new Liferay.Popup({
            title: mensaje('detalleContacto', 'Detalle contacto'),
            modal: true,
            width: 880,
            position: ['center', 30]
        });

        var requestUrl = config.initial.esPortletCai
            ? url('editarContactoCai')
            : url('editarContactoAfiliados');

        jQuery(popupCRM).load(agregarParametros(requestUrl, parametros));
    }

    function validaMontosEdicion() {
        var importeTotal = numero(valor('totalEdicion'));
        var cargoOspim = numero(valor('cargoospimEdicion'));
        var cargoPs = numero(valor('cargopsEdicion'));
        var cargoImesa = numero(valor('cargoimesaEdicion'));
        var reconocidoSSS = numero(valor('reconocidoSSSEdicion'));
        var importeFactura = numero(valor('importeFC_edicion'));
        var estado = String(valor('estado'));
        var total = redondear(cargoOspim + cargoPs + cargoImesa + reconocidoSSS);
        var importeAreaMedica = redondear(importeTotal);
        importeFactura = redondear(importeFactura);

        if (importeAreaMedica - importeFactura > 0.01) {
            window.alert('El importe autorizado por el Area Médica no puede superar el Importe de la Factura. Area Medica: ' +
                importeAreaMedica + ' - Comprobante: ' + importeFactura);
            return false;
        }
        if (total === 0 && importeFactura > 0 && estado === '3') {
            window.alert('Debe ingresar los importes en el Área Médica');
            return false;
        }
        if (total > importeTotal) {
            window.alert('La suma de los importes ( OSPIM, tercerizadora ) no puede superar el monto en el importe ingresado.');
            return false;
        }
        if (total !== importeTotal) {
            window.alert('La suma de los importes ( OSPIM y tercerizadora) no puede diferir del monto en el total ingresado.');
            return false;
        }

        var tipoPedido = elemento('tipopedido');
        if (tipoPedido && tipoPedido.selectedIndex === 1 && total !== importeTotal && estado === '3') {
            window.alert('El importe total de la prestación debe coincidir con la suma de cargo Ospim y cargo tercerizadora');
            return false;
        }

        var recuperable = String(valor('recuperable_surEdicion'));
        if (recuperable === '2') {
            if (reconocidoSSS > 0) {
                window.alert('El importe reconocido debe estar vacío');
                jQuery(selector('reconocidoSSSEdicion')).val('');
                return false;
            }
        } else {
            if (reconocidoSSS === 0) {
                window.alert('El importe reconocido debe ser mayor a cero');
                return false;
            }
            if (reconocidoSSS > importeTotal) {
                window.alert('El importe Reconocido no puede superar el monto en el importe ingresado.');
                return false;
            }
        }
        return true;
    }

    function ValidaMontos() {
        var importeFactura = numero(valor('importeFC'));
        var importeTotal = numero(valor('total'));
        var cargoOspim = numero(valor('cargoospim'));
        var cargoPs = numero(valor('cargops'));
        var cargoImesa = numero(valor('cargoimesa'));
        var reconocidoSSS = numero(valor('reconocidoSSS'));
        var estado = String(valor('estado'));
        var totalCargos = redondear(cargoOspim + cargoPs + cargoImesa + reconocidoSSS);
        var importeAreaMedica = redondear(importeTotal);
        importeFactura = redondear(importeFactura);

        if (importeAreaMedica - importeFactura > 0.01) {
            window.alert('El importe autorizado por el Area Médica no puede superar el Importe de la Factura. Area Medica: ' +
                importeAreaMedica + ' - Comprobante: ' + importeFactura);
            return false;
        }
        if (totalCargos > importeFactura && estado === '3') {
            window.alert('La suma de los importes ( OSPIM y Tercerizadora) no puede superar el Importe de la Factura.');
            return false;
        }
        if (totalCargos !== importeTotal && estado === '3') {
            window.alert('La suma de los importes ( OSPIM y Tercerizadora ) no puede diferir del monto en el total ingresado.');
            return false;
        }

        var tipoPedido = elemento('tipopedido');
        if (tipoPedido && tipoPedido.selectedIndex === 1 && totalCargos !== importeTotal && estado === '3') {
            window.alert('El importe total de la prestación debe coincidir con la suma de Cargo Ospim más Cargo Tercerizadora.');
            return false;
        }
        if (tipoPedido && tipoPedido.selectedIndex === 2) {
            if (importeTotal < totalCargos && estado === '3') {
                window.alert('El importe total de prestación debe coincidir con la suma de a Cargo Ospim más Cargo Tercerizadora');
                return false;
            }
            if (totalCargos === 0 && estado === '3') {
                window.alert(' la suma de a Cargo Ospim más a Cargo Tercerizadora debe ser mayor que cero.');
                return false;
            }
        }

        var recuperable = String(valor('recuperable_sur'));
        if (recuperable === '2') {
            if (reconocidoSSS > 0) {
                window.alert('El importe reconocido debe estar vacio');
                jQuery(selector('reconocidoSSS')).val('');
                return false;
            }
        } else {
            if (reconocidoSSS === 0) {
                window.alert('El importe reconocido debe ser mayor a cero');
                return false;
            }
            if (reconocidoSSS > importeTotal) {
                window.alert('El importe Reconocido no puede superar el monto en el importe ingresado.');
                return false;
            }
        }
        return true;
    }

    function validaRevision() {
        if (Number(valor('cantrevisionesactivas') || 0) < 1) {
            window.alert('Debe tener registrada por lo menos una revision activa.');
            return false;
        }
        return true;
    }

    function convertToUppercase(elementoInput) {
        if (elementoInput && elementoInput.value) {
            elementoInput.value = elementoInput.value.toUpperCase();
        }
    }

    function myXOR(a, b) {
        return a > 0 && b > 0;
    }

    function enterTecla(evento) {
        var tecla = document.all ? evento.keyCode : evento.which;
        if (tecla === 13 && typeof window.crit_busqueda === 'function') {
            window.crit_busqueda();
        } else {
            jQuery(selector('posforcie10')).val(0);
        }
    }

    function aplicaEstiloBordeRojoDatosObligatorio() {
        var color = '#ff9999';
        var campos = [
            'fechaospimMes', 'fechaospimAnio', 'fechaospimDia', 'estado', 'sector',
            'tipopedido', 'fecharevisionMes', 'fecharevisionAnio', 'fecharevisionDia',
            'resolucion', 'justificacionmedica', 'frecuencia', 'importe', 'mensajerevisionefectuada'
        ];
        jQuery.each(campos, function (_, campo) {
            jQuery(selector(campo)).css('borderColor', color);
        });
    }

    function calculaTotal() {
        var total = numero(valor('importe')) * numero(valor('cantidad'));
        jQuery(selector('total')).val(redondear(total));
    }

    function seleccionaCamposCieDiez(codigo, descripcion) {
        jQuery(selector('codigoCie')).val(codigo);
        jQuery(selector('detalleCie')).val(descripcion);
        jQuery(selector('codigoCie10')).val(codigo);
    }

    function limpiaCamposBusquedaCieDiez() {
        jQuery(selector('codigoCie10')).val('');
    }

    function seteaControlesFacturacionDirecta(estado) {
        jQuery(selector('incluido_convenio_gerenciadora')).prop('checked', estado);
        jQuery(selector('debitoprestadora')).prop('checked', estado);
    }

    function desactivaCheckCierre() {
        seteaControlesFacturacionDirecta(false);
        jQuery(selector('dosporciento')).prop('checked', false).prop('disabled', true);
    }

    function abreAutorizacion() {
        window.open(
            url('autorizacionesPrestacionales'),
            'Autorizaciones',
            'height=800, menubar=no, resizable=yes,scrollbars=yes, status=no, toolbar=no, width=1200'
        );
    }

    function calculaTotalFC() {
        var total = numero(valor('importeUnitarioFC')) * numero(valor('cantidadFC'));
        jQuery(selector('importeFC')).val(redondear(total));
    }

    function traerDescripcion() {
        jQuery.ajax({
            url: agregarParametros(url('getIntegracionDetalle'), {
                id_integracion: valor('integracion')
            }),
            success: function (data) {
                var descripcionLarga = jQuery.parseJSON(data).DescripcionLarga;
                jQuery('#integracion_desc').attr({
                    alt: descripcionLarga,
                    title: descripcionLarga
                });
            }
        });
    }

    function cambioRecuperable() {
        try {
            var recuperable = String(valor('recuperable_sur'));
            if (recuperable === '3' || recuperable === '1') {
                jQuery(selector('reconocidoSSS')).prop('readonly', false);
            } else {
                jQuery(selector('reconocidoSSS')).val(0).prop('readonly', true);
            }
        } catch (error) {
            // Se conserva el comportamiento silencioso original.
        }
    }

    function validarEmail() {
        var email = valor('email') || '';
        if (jQuery.trim(email).length === 0) {
            return true;
        }

        var expresion = /^([a-zA-Z0-9_.\-])+@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
        if (!expresion.test(email)) {
            window.alert('Error: La dirección de correo ' + email + ' es incorrecta.');
            jQuery(selector('email')).focus();
            return false;
        }
        return true;
    }

    function confirmaActualizacionDomicilioAfiliado() {
        var datos = {
            id_domicilio: valor('id_domicilio'),
            id_provincia: valor('provincia'),
            id_localidad: valor('localidad'),
            calle: valor('calle'),
            numero: valor('numero'),
            piso: valor('piso'),
            departamento: valor('dpto'),
            codigo_postal: valor('cod_postal'),
            barrio: valor('barrio'),
            cod_area_telefono: valor('cod_area_telefono'),
            telefono: valor('telefono'),
            cod_area_celular: valor('cod_area_celular'),
            celular: valor('celular'),
            email: valor('email'),
            email_original: valor('email_original')
        };

        var integrante = valor('inte');
        var idParentesco = String(valor('idPar'));
        var parentescos = config.parentescos;
        if (idParentesco !== String(parentescos.defaultId) &&
            idParentesco !== String(parentescos.conyugeId) &&
            idParentesco !== String(parentescos.concubinoId)) {
            integrante = 0;
        }

        if (jQuery.trim(datos.calle || '').length === 0) {
            window.alert('Ingrese la calle del domicilio');
            jQuery(selector('calle')).focus();
            return false;
        }

        if (!validarTelefono(datos.cod_area_telefono, datos.telefono, 'teléfono', 'cod_area_telefono', 'telefono')) {
            return false;
        }
        if (!validarCelular(datos.cod_area_celular, datos.celular)) {
            return false;
        }
        if (!validarEmail()) {
            return false;
        }

        datos.cuil_titular = valor('cuil');
        datos.inte = integrante;
        datos.cmd = 'save';

        jQuery.post(
            agregarParametros(url('actualizaDomicilio'), { id_parentesco: idParentesco }),
            datos,
            function () {
                if (popupDomicilio !== null) {
                    jQuery(selector('divResultadoActualizarOK')).show();
                    jQuery(selector('divBotonActualizar')).hide();
                    Liferay.Popup.close(popupDomicilio);
                }
            }
        );
    }

    function mostrarDomicilioAfiliado() {
        var cuilTitular = valor('cuil');
        var integrante = valor('inte');
        var email = '';

        jQuery.ajax({
            url: agregarParametros(url('buscarAfiliadoDatos'), {
                cuil_titular: cuilTitular,
                inte: integrante
            }),
            async: false,
            success: function (data) {
                email = jQuery.parseJSON(data).email;
            }
        });

        popupDomicilio = Liferay.Popup({
            title: mensaje('detalleDomicilio', 'Detalle domicilio'),
            modal: true,
            width: 950,
            height: 330,
            fixedcenter: true
        });

        jQuery(popupDomicilio).load(agregarParametros(url('actualizaDomicilio'), {
            cuil_titular: cuilTitular,
            inte: integrante,
            cmd: 'view',
            email: email
        }));
    }

    function validarTelefono(codigoArea, telefono, descripcion, campoCodigo, campoNumero) {
        codigoArea = jQuery.trim(codigoArea || '');
        telefono = jQuery.trim(telefono || '');

        if ((codigoArea === '' && telefono !== '') || (codigoArea !== '' && telefono === '')) {
            window.alert('El ' + descripcion + ' debe necesariamente tener el código de area y el número');
            jQuery(selector(campoNumero)).focus();
            return false;
        }
        if (codigoArea.indexOf('0') === 0) {
            window.alert('El código de area del ' + descripcion + ' no debe iniciar con cero');
            jQuery(selector(campoCodigo)).focus();
            return false;
        }
        if (telefono.indexOf('0') === 0) {
            window.alert('El número del ' + descripcion + ' no debe iniciar con cero');
            jQuery(selector(campoNumero)).focus();
            return false;
        }
        if ((codigoArea.length > 0 || telefono.length > 0) && codigoArea.length + telefono.length !== 10) {
            window.alert('La longitud del código de área + ' + descripcion + ' debe de ser de 10 caracteres');
            jQuery(selector(campoCodigo)).focus();
            return false;
        }
        return true;
    }

    function validarCelular(codigoArea, celular) {
        codigoArea = jQuery.trim(codigoArea || '');
        celular = jQuery.trim(celular || '');

        if (codigoArea.indexOf('0') === 0) {
            window.alert('El código de area del celular no debe iniciar con cero');
            jQuery(selector('cod_area_celular')).focus();
            return false;
        }
        if (celular.indexOf('0') === 0) {
            window.alert('El número del celular no debe iniciar con cero');
            jQuery(selector('celular')).focus();
            return false;
        }
        if ((codigoArea.length > 0 || celular.length > 0) && codigoArea.length + celular.length !== 10) {
            window.alert('La longitud del código de área + celular debe de ser de 10 caracteres');
            jQuery(selector('cod_area_celular')).focus();
            return false;
        }
        return true;
    }

    function limpiarFormularioPrestacionEdicion() {
        var valores = {
            cantidadEdicion: '1', importeEdicion: '', totalEdicion: '', cargoospimEdicion: '',
            cargopsEdicion: '', cargoimesaEdicion: '', reconocidoSSSEdicion: '',
            observacion_prestacionEdicion: '', troquel: '', codigoSeguimiento_filtro: '',
            comprobante_tipo_edicion: 'FCP', comprobante_letra_edicion: '', comprobante_nro_edicion: '',
            comprobante_suc_edicion: '', fechaComprobanteDiaEdicion: '', fechaComprobanteMesEdicion: '',
            fechaComprobanteAnioEdicion: '', cantidadFC_edicion: '', importeUnitarioFC_edicion: '',
            importeFC_edicion: '', cuit_entidad_edicion: '', sucursal_entidad_edicion: '', entidad_edicion: '',
            fechaPrestacionDiaFarmacia: '', fechaPrestacionMesFarmacia: '', fechaPrestacionAnioFarmacia: '',
            fechaPrestacionDia: '', fechaPrestacionMes: '', fechaPrestacionAnio: '',
            fechaPrestacionDiaEdicion: '', fechaPrestacionMesEdicion: '', fechaPrestacionAnioEdicion: '',
            nombre_medicamento_edit: ''
        };
        establecerValores(valores);

        if (elemento('frecuenciaEdicion')) elemento('frecuenciaEdicion').selectedIndex = 0;
        if (elemento('recuperable_sur')) elemento('recuperable_sur').selectedIndex = 0;
        jQuery(selector('divBtnBuscaMedicamento_edit')).show();
        limpiarNomencladorAutocompletar();
    }

    function limpiarFormularioPrestacionAlta() {
        var valores = {
            importe: '', total: '', cantidad: '1', cargoospim: '', cargops: '', cargoimesa: '',
            reconocidoSSS: '', observacion_prestacion: '', troquel: '', codigoSeguimiento_filtro: '',
            comprobante_tipo: 'FCP', comprobante_nro: '', fechaComprobanteDia: '',
            fechaComprobanteMes: '', fechaComprobanteAnio: '', cantidadFC: '', importeUnitarioFC: '',
            importeFC: '', cuit_entidad: '', sucursal_entidad: '', entidad_: '', comprobante_suc: '',
            nombre_medicamento: '', fechaPrestacionDiaFarmacia: '', fechaPrestacionMesFarmacia: '',
            fechaPrestacionAnioFarmacia: '', fechaPrestacionDia: '', fechaPrestacionMes: '', fechaPrestacionAnio: ''
        };
        establecerValores(valores);

        if (elemento('frecuencia')) elemento('frecuencia').selectedIndex = 0;
        if (elemento('recuperable_sur')) elemento('recuperable_sur').selectedIndex = 0;
        jQuery(selector('divBtnBuscaEntidad')).show();
        jQuery(selector('divBtnBuscaMedicamento')).show();
        limpiarNomencladorAutocompletar();
    }

    function establecerValores(valores) {
        jQuery.each(valores, function (nombre, nuevoValor) {
            jQuery(selector(nombre)).val(nuevoValor);
        });
    }

    function tieneValorNumerico(dato) {
        return dato !== null && dato !== '' && numero(dato) !== 0;
    }

    function numero(dato) {
        if (dato === null || dato === undefined || dato === '') {
            return 0;
        }
        var resultado = parseFloat(String(dato).replace(',', '.'));
        return isNaN(resultado) ? 0 : resultado;
    }

    function redondear(dato) {
        return Math.round(dato * 100) / 100;
    }

    function esVacio(dato) {
        return dato === null || dato === undefined || String(dato) === '';
    }

    function esVacioOCero(dato) {
        return esVacio(dato) || numero(dato) === 0;
    }

    function fechaIncompleta(dia, mes, anio) {
        return esVacioOCero(dia) || esVacio(mes) || Number(mes) === -1 || esVacioOCero(anio);
    }

    window.ViewReclamo = {
        init: function (opciones) {
            config = opciones;
            namespace = opciones.namespace || '';
            exponerFuncionesPublicas();
            jQuery(function () {
                vincularEventos();
                inicializarPantalla();
            });
        }
    };
}(window, document, window.jQuery));
