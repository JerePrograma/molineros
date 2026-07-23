(function(window, jQuery) {
    if (!jQuery) {
        return;
    }

    var config = window.ReclamoPrestacionalViewConfig || {};
    var namespace = config.namespace || "";
    var values = config.values || {};
    var urls = config.urls || {};
    var MARCA_VALIDACION = "__rpComprasValidacionVisual";

    function campo(sufijo) {
        return jQuery("#" + namespace + sufijo);
    }

    function valor(sufijo) {
        var control = campo(sufijo);
        return control.length ? control.val() : "";
    }

    function esVacio(valorActual) {
        return valorActual == null || String(valorActual) === "";
    }

    function esBorradorCompras() {
        return values.esBorradorCompras === true ||
                String(values.esBorradorCompras) === "true";
    }

    /*
     * Liferay 5.2 puede exponer una version de jQuery sin ajaxPrefilter.
     * Se instala una compatibilidad minima antes de que cargue tab_guard.
     */
    function instalarAjaxPrefilterLegacy() {
        if (typeof jQuery.ajaxPrefilter === "function") {
            return;
        }

        var prefiltros = [];
        var ajaxOriginal = jQuery.ajax;

        jQuery.ajaxPrefilter = function(prefiltro) {
            if (typeof prefiltro === "function") {
                prefiltros.push(prefiltro);
            }
        };

        if (typeof ajaxOriginal !== "function" ||
                ajaxOriginal.__rpAjaxPrefilterLegacy) {
            return;
        }

        var ajaxCompatible = function(opciones) {
            var copia;
            var i;

            if (arguments.length === 1 && opciones &&
                    typeof opciones === "object") {
                copia = jQuery.extend({}, opciones);

                for (i = 0; i < prefiltros.length; i++) {
                    prefiltros[i](copia, opciones, null);
                }

                return ajaxOriginal.call(this, copia);
            }

            return ajaxOriginal.apply(this, arguments);
        };

        ajaxCompatible.__rpAjaxPrefilterLegacy = true;
        ajaxCompatible.__rpAjaxPrefilterLegacyOriginal = ajaxOriginal;
        jQuery.ajax = ajaxCompatible;
    }

    instalarAjaxPrefilterLegacy();

    function reemplazarPorHiddenSeguro(sufijo) {
        var controles = campo(sufijo);

        controles.each(function() {
            var actual = this;
            var tipo = String(actual.type || "").toLowerCase();
            var reemplazo;

            if (tipo === "hidden") {
                return;
            }

            reemplazo = document.createElement("input");
            reemplazo.type = "hidden";
            reemplazo.id = actual.id || namespace + sufijo;
            reemplazo.name = actual.name || namespace + sufijo;
            reemplazo.value = actual.value || "";

            if (actual.parentNode) {
                actual.parentNode.replaceChild(reemplazo, actual);
            }
        });
    }

    function normalizarAuxiliares() {
        reemplazarPorHiddenSeguro("reclamoDraftId");
        reemplazarPorHiddenSeguro("tipoaccionprestacion");
        reemplazarPorHiddenSeguro("plan_reclamo_bloqueado");
        reemplazarPorHiddenSeguro("nombre_plan_reclamo_bloqueado");
    }

    function resumenValidacion() {
        return campo("reclamo_validacion_resumen");
    }

    function listaValidacion() {
        return campo("reclamo_validacion_lista");
    }

    function limpiarErroresVisuales() {
        jQuery(".rp-campo-error").removeClass("rp-campo-error");
        jQuery(".rp-campo-error-contenedor")
                .removeClass("rp-campo-error-contenedor");
        listaValidacion().empty();
        resumenValidacion().css("display", "none");
    }

    function marcarControl(control) {
        if (!control || !control.length) {
            return;
        }

        control.addClass("rp-campo-error");

        if (String(control.attr("type") || "").toLowerCase() === "hidden") {
            control.closest("fieldset, td, div")
                    .eq(0)
                    .addClass("rp-campo-error-contenedor");
        }
    }

    function agregarError(errores, mensaje, sufijo) {
        var control = sufijo ? campo(sufijo) : jQuery();
        marcarControl(control);
        errores.push({
            mensaje: mensaje,
            control: control
        });
    }

    function agregarErrorFecha(errores, mensaje, prefijo) {
        var dia = campo(prefijo + "Dia");
        var mes = campo(prefijo + "Mes");
        var anio = campo(prefijo + "Anio");

        marcarControl(dia);
        marcarControl(mes);
        marcarControl(anio);

        errores.push({
            mensaje: mensaje,
            control: dia.length ? dia : (mes.length ? mes : anio)
        });
    }

    function mostrarErrores(errores) {
        var lista = listaValidacion();
        var resumen = resumenValidacion();
        var primero = null;
        var i;
        var item;
        var posicion;

        if (!errores.length) {
            return false;
        }

        lista.empty();

        for (i = 0; i < errores.length; i++) {
            item = jQuery("<li></li>");
            item.text(errores[i].mensaje);
            lista.append(item);

            if (!primero && errores[i].control &&
                    errores[i].control.length) {
                primero = errores[i].control.eq(0);
            }
        }

        resumen.css("display", "block");

        if (primero && primero.length) {
            posicion = primero.offset();
            if (posicion) {
                window.scrollTo(0, Math.max(0, posicion.top - 100));
            }
            try {
                primero.focus();
            } catch (errorFoco) {
                // El resumen sigue visible aunque el control no acepte foco.
            }
        } else {
            posicion = resumen.offset();
            if (posicion) {
                window.scrollTo(0, Math.max(0, posicion.top - 100));
            }
        }

        return true;
    }

    function fechaParcial(prefijo) {
        var dia = valor(prefijo + "Dia");
        var mes = valor(prefijo + "Mes");
        var anio = valor(prefijo + "Anio");
        var diaVacio = esVacio(dia) || String(dia) === "0";
        var mesVacio = esVacio(mes) || String(mes) === "-1";
        var anioVacio = esVacio(anio) || String(anio) === "0";
        var todosVacios = diaVacio && mesVacio && anioVacio;
        var todosCompletos = !diaVacio && !mesVacio && !anioVacio;

        return !todosVacios && !todosCompletos;
    }

    function fechaAusente(prefijo) {
        var dia = valor(prefijo + "Dia");
        var mes = valor(prefijo + "Mes");
        var anio = valor(prefijo + "Anio");

        return esVacio(dia) || String(dia) === "0" ||
                esVacio(mes) || String(mes) === "-1" ||
                esVacio(anio) || String(anio) === "0";
    }

    function valorNoSeleccionado(sufijo) {
        var actual = String(valor(sufijo) || "").toUpperCase();
        return actual === "" || actual === "0" ||
                actual === "SELECCIONAR" || actual === "SELECCIONE";
    }

    function validarCabecera() {
        var errores = [];
        var cantidadPrestaciones = parseInt(
                valor("cantprestacioneslista"),
                10
        );
        var tipoPedido = String(valor("tipopedido") || "").toUpperCase();

        if (valor("plan_reclamo_bloqueado") === "1") {
            agregarError(
                    errores,
                    "El plan del afiliado no permite cargar un reclamo.",
                    "plan"
            );
        }

        if (esVacio(valor("cuil")) || esVacio(valor("inte"))) {
            agregarError(
                    errores,
                    "Debe seleccionar el afiliado asociado al reclamo.",
                    "cuil"
            );
            marcarControl(campo("inte"));
        }

        if (fechaParcial("fechaseccional")) {
            agregarErrorFecha(
                    errores,
                    "Complete todos los componentes de Fecha Seccional o dejelos vacios.",
                    "fechaseccional"
            );
        }

        if (valorNoSeleccionado("sector")) {
            agregarError(
                    errores,
                    "Debe seleccionar el sector del reclamo.",
                    "sector"
            );
        }

        if (tipoPedido === "" || tipoPedido === "SELECCIONAR" ||
                tipoPedido === "SELECCIONE") {
            agregarError(
                    errores,
                    "Debe seleccionar el Tipo de Pedido.",
                    "tipopedido"
            );
        }

        if (isNaN(cantidadPrestaciones) || cantidadPrestaciones < 1) {
            agregarError(
                    errores,
                    "Debe existir al menos una prestacion activa.",
                    "lista_prestaciones_reclamos"
            );
        }

        if (tipoPedido === "EXCEPCION" && campo("integracion").length &&
                valorNoSeleccionado("integracion")) {
            agregarError(
                    errores,
                    "Debe seleccionar un tipo de integracion.",
                    "integracion"
            );
        }

        return errores;
    }

    function controlPorMensaje(mensaje) {
        var texto = String(mensaje || "").toLowerCase();

        if (texto.indexOf("afiliado") >= 0) {
            return campo("cuil");
        }
        if (texto.indexOf("fecha seccional") >= 0) {
            return campo("fechaseccionalDia");
        }
        if (texto.indexOf("tipo de integracion") >= 0) {
            return campo("integracion");
        }
        if (texto.indexOf("tipo de pedido") >= 0) {
            return campo("tipopedido");
        }
        if (texto.indexOf("sector") >= 0) {
            return campo("sector");
        }
        if (texto.indexOf("sucursal del cuit") >= 0) {
            return campo("sucursal_entidad_edicion");
        }
        if (texto.indexOf("cuit del comprobante") >= 0) {
            return campo("cuit_entidad_edicion");
        }
        if (texto.indexOf("fecha del comprobante") >= 0) {
            return campo("fechaComprobanteDiaEdicion");
        }
        if (texto.indexOf("fecha de la prestacion") >= 0) {
            return campo("fechaPrestacionDiaEdicion");
        }
        if (texto.indexOf("frecuencia") >= 0) {
            return campo("frecuenciaEdicion");
        }

        return jQuery();
    }

    function ejecutarConAlertasVisuales(original, contexto, argumentos) {
        var alertOriginal = window.alert;
        var mensajes = [];
        var resultado;
        var errores = [];
        var i;
        var control;

        window.alert = function(mensaje) {
            mensajes.push(String(mensaje || "Error de validacion."));
        };

        try {
            resultado = original.apply(contexto, argumentos);
        } catch (error) {
            mensajes.push(
                    error && error.message ?
                            error.message :
                            "No se pudo completar la operacion."
            );

            if (window.console && window.console.error) {
                window.console.error(
                        "RECLAMO_PRESTACIONAL_VALIDACION_ERROR",
                        error
                );
            }
        } finally {
            window.alert = alertOriginal;
        }

        if (!mensajes.length) {
            return resultado;
        }

        for (i = 0; i < mensajes.length; i++) {
            control = controlPorMensaje(mensajes[i]);
            marcarControl(control);
            errores.push({
                mensaje: mensajes[i],
                control: control
            });
        }

        mostrarErrores(errores);
        return false;
    }

    function envolverGuardado(nombreFuncion) {
        var original = window[nombreFuncion];
        var envuelta;

        if (typeof original !== "function" || original[MARCA_VALIDACION]) {
            return;
        }

        envuelta = function() {
            var errores;

            limpiarErroresVisuales();
            errores = validarCabecera();

            if (mostrarErrores(errores)) {
                return false;
            }

            return ejecutarConAlertasVisuales(
                    original,
                    this,
                    arguments
            );
        };

        envuelta[MARCA_VALIDACION] = true;
        envuelta.__rpComprasValidacionOriginal = original;
        window[nombreFuncion] = envuelta;
    }

    function numero(valorActual) {
        var normalizado = String(valorActual == null ? "" : valorActual)
                .replace(/\s/g, "")
                .replace(",", ".");
        var convertido = parseFloat(normalizado);
        return isNaN(convertido) ? 0 : convertido;
    }

    function validarPrestacionCompras() {
        var errores = [];
        var cantidad = numero(valor("cantidadEdicion"));
        var importe = numero(valor("importeEdicion"));
        var total = Math.round(cantidad * importe * 100) / 100;
        var cargos = Math.round((
                numero(valor("cargoospimEdicion")) +
                numero(valor("cargopsEdicion")) +
                numero(valor("cargoimesaEdicion"))
        ) * 100) / 100;
        var codigo = String(valor("codigoSeguimiento_filtro_edit") || "");
        var descripcion = String(
                valor("descripcionSeguimiento_filtro_edit") || ""
        );
        var medicamento = String(valor("troquel_edit") || "");

        if (valorNoSeleccionado("frecuenciaEdicion")) {
            agregarError(
                    errores,
                    "Debe seleccionar la frecuencia correspondiente.",
                    "frecuenciaEdicion"
            );
        }

        if (fechaAusente("fechaPrestacion" + "Edicion")) {
            agregarErrorFecha(
                    errores,
                    "Debe ingresar la fecha de la Prestacion.",
                    "fechaPrestacionEdicion"
            );
        }

        if (!codigo && !medicamento) {
            agregarError(
                    errores,
                    "Debe confirmar el nomenclador o medicamento de la prestacion.",
                    "codigoSeguimiento_filtro_edit"
            );
        } else if (codigo && !descripcion) {
            agregarError(
                    errores,
                    "Debe confirmar la descripcion de la prestacion.",
                    "descripcionSeguimiento_filtro_edit"
            );
        }

        if (cantidad <= 0) {
            agregarError(
                    errores,
                    "La cantidad de la prestacion debe ser mayor a cero.",
                    "cantidadEdicion"
            );
        }

        if (importe <= 0) {
            agregarError(
                    errores,
                    "El importe de la prestacion debe ser mayor a cero.",
                    "importeEdicion"
            );
        }

        if (Math.abs(total - cargos) > 0.01) {
            agregarError(
                    errores,
                    "La suma de los cargos debe coincidir con el total de la prestacion.",
                    "cargoospimEdicion"
            );
            marcarControl(campo("cargopsEdicion"));
            marcarControl(campo("cargoimesaEdicion"));
        }

        return errores;
    }

    function guardarPrestacionCompras() {
        var errores;
        var destino;
        var nomSeleccionado;
        var tipoNomenclador;
        var recuperableInicial;
        var params;

        limpiarErroresVisuales();
        errores = validarPrestacionCompras();

        if (mostrarErrores(errores)) {
            return false;
        }

        nomSeleccionado = valor("nom_seleccionado_edit") ||
                valor("nom_seleccionado") || "1";
        tipoNomenclador = valor("tipoNomenclador_edit") ||
                valor("tipoNomenclador") || "0";
        recuperableInicial = valor("recuperable_sur_compra_inicial") || "0";

        params = {
            frecuencia: valor("frecuenciaEdicion"),
            importe: valor("importeEdicion"),
            cargoospim: valor("cargoospimEdicion"),
            cargops: valor("cargopsEdicion"),
            cargoimesa: valor("cargoimesaEdicion"),
            prestacion: valor("codigoSeguimiento_filtro_edit"),
            codigoPrestacion: valor("codigoSeguimiento_filtro_edit"),
            idprestacion: valor("codigoprestacion"),
            idRegistro: valor("idRegistro"),
            tipoEdicion: 0,
            grabaedicion: true,
            estadoAprobacion: 0,
            recuperableSur: parseInt(recuperableInicial, 10) || 0,
            cantidad: valor("cantidadEdicion"),
            observaciones: valor("observacion_prestacionEdicion"),
            cpbte_tipo: "OTR",
            cpbte_nro: "",
            cpbte_dia: "",
            cpbte_mes: "",
            cpbte_anio: "",
            cpbte_cantidad: valor("cantidadFC_edicion"),
            cpbte_importe: valor("importeUnitarioFC_edicion"),
            cpbte_cuit: valor("cuit_entidad_edicion"),
            cpbte_sucursal: "",
            importeFC: valor("importeFC_edicion"),
            cpbte_cuit_sucursal: "",
            cpbte_letra: "",
            fecha_prestacion_dia: valor("fechaPrestacionDiaEdicion"),
            fecha_prestacion_mes: valor("fechaPrestacionMesEdicion"),
            fecha_prestacion_anio: valor("fechaPrestacionAnioEdicion"),
            id_medicamento_edit: valor("troquel_edit"),
            nombre_medicamento_edit: valor("nombre_medicamento_edit"),
            codigoSeguimiento_filtro_edit:
                    valor("codigoSeguimiento_filtro_edit"),
            descripcionSeguimiento_filtro_edit:
                    valor("descripcionSeguimiento_filtro_edit"),
            nom_seleccionado_edit: nomSeleccionado,
            tipoNomenclador_edit: tipoNomenclador,
            reconocidoSSS: 0,
            cuil: valor("cuil"),
            inte: valor("inte"),
            id_tercerizadora: valor("id_tercerizadora")
        };

        destino = campo("lista_prestaciones_reclamos");
        campo("buscando").css("display", "block");

        destino.load(
                urls.editarPrestaciones,
                params,
                function(respuesta, estado) {
                    campo("buscando").css("display", "none");

                    if (estado === "error") {
                        mostrarErrores([{
                            mensaje: "No se pudo actualizar la prestacion precargada.",
                            control: destino
                        }]);
                        return;
                    }

                    campo("tipoaccionprestacion").val("0");
                    campo("datos_edicion_prestacion").css("display", "none");
                    campo("datos_prestacion_ingreso").css("display", "block");
                    limpiarErroresVisuales();
                }
        );

        return false;
    }

    function prepararInterfazCompras() {
        var comprobante;
        var aviso;
        var botonSeguro;

        if (!esBorradorCompras()) {
            return;
        }

        comprobante = campo("datos_comprobante");
        comprobante.css("display", "none");
        comprobante.attr("aria-hidden", "true");
        campo("comprobante_tipo_edicion").val("OTR");

        aviso = campo("rp_compras_comprobante_info");
        if (!aviso.length && comprobante.length) {
            aviso = jQuery("<div></div>", {
                id: namespace + "rp_compras_comprobante_info",
                class: "portlet-msg-info"
            });
            aviso.text(
                    "La cotizacion de Compras no es una factura. " +
                    "Los datos de comprobante no se solicitan en esta precarga."
            );
            comprobante.before(aviso);
        }

        window[namespace + "editarPrestacionSeleccionada"] =
                guardarPrestacionCompras;

        botonSeguro = campo("rp_guardar_prestacion_seguro");
        if (botonSeguro.length) {
            botonSeguro[0].onclick = function() {
                return guardarPrestacionCompras();
            };
        }
    }

    function instalarValidacionVisual() {
        normalizarAuxiliares();
        prepararInterfazCompras();
        envolverGuardado(namespace + "saveReclamo");
        envolverGuardado(namespace + "editaReclamo");
    }

    function instalar() {
        var inicial = campo("recuperable_sur_compra_inicial");

        if (!inicial.length ||
                jQuery.fn.__rpComprasSurPrecargaInstalado) {
            instalarValidacionVisual();
            return;
        }

        var valorActual = inicial.val() === "1" ? "1" : "0";

        function normalizarValor(valorEntrada) {
            var numeroValor = parseInt(valorEntrada, 10);

            return numeroValor >= 0 && numeroValor <= 3
                    ? String(numeroValor)
                    : "0";
        }

        function esEndpointPrestacion(url) {
            var texto = String(url || "");

            return texto.indexOf(
                    "struts_action=/autorizaciones/" +
                            "lista_prestaciones_reclamos"
            ) >= 0
                    || texto.indexOf(
                            "struts_action=/autorizaciones/" +
                                    "editar_reclamosprestaciones"
                    ) >= 0;
        }

        function corregirUrl(url) {
            var texto = String(url || "");
            var patron = /([?&])recuperableSur=[^&]*/;

            if (!esEndpointPrestacion(texto)) {
                return url;
            }

            if (patron.test(texto)) {
                return texto.replace(
                        patron,
                        "$1recuperableSur=" + valorActual
                );
            }

            return texto
                    + (texto.indexOf("?") >= 0 ? "&" : "?")
                    + "recuperableSur="
                    + valorActual;
        }

        function corregirDatos(datos) {
            var copia;

            if (datos == null) {
                copia = {};
            } else if (typeof datos === "object") {
                copia = jQuery.extend({}, datos);
            } else {
                return datos;
            }

            copia.recuperableSur = parseInt(valorActual, 10);

            return copia;
        }

        function registrarCambios(selector) {
            selector.each(function() {
                var control = jQuery(this);

                if (control.data("rpComprasSurCambio")) {
                    return;
                }

                control.data("rpComprasSurCambio", true);
                control.change(function() {
                    valorActual = normalizarValor(
                            jQuery(this).val()
                    );
                });
            });
        }

        function aplicarSelector() {
            var alta = campo("recuperable_sur");
            var edicion = campo("recuperable_surEdicion");

            registrarCambios(alta);
            registrarCambios(edicion);

            if (alta.length) {
                alta.removeAttr("disabled").val(valorActual);
            }

            if (edicion.length) {
                edicion.removeAttr("disabled").val(valorActual);
            }

            if (typeof window.cambiorecuperable === "function") {
                window.cambiorecuperable();
            }

            if (typeof window.cambiorecuperableEdicion === "function") {
                window.cambiorecuperableEdicion();
            }
        }

        function envolverCallback(callback) {
            if (typeof callback !== "function") {
                return callback;
            }

            return function() {
                var resultado = callback.apply(this, arguments);

                window.setTimeout(function() {
                    aplicarSelector();
                    instalarValidacionVisual();
                }, 0);

                return resultado;
            };
        }

        function envolverLoad(loadOriginal) {
            if (typeof loadOriginal !== "function" ||
                    loadOriginal.__rpComprasSurPrecarga) {
                return loadOriginal;
            }

            var loadCompras = function(url, datos, callback) {
                var urlCorregida = corregirUrl(url);
                var datosCorregidos = datos;
                var callbackCorregido = callback;

                if (typeof datos === "function") {
                    callbackCorregido = datos;
                    datosCorregidos = undefined;
                }

                if (esEndpointPrestacion(urlCorregida)) {
                    datosCorregidos = corregirDatos(datosCorregidos);
                    callbackCorregido = envolverCallback(
                            callbackCorregido
                    );
                }

                if (datosCorregidos === undefined) {
                    return loadOriginal.call(
                            this,
                            urlCorregida,
                            callbackCorregido
                    );
                }

                return loadOriginal.call(
                        this,
                        urlCorregida,
                        datosCorregidos,
                        callbackCorregido
                );
            };

            loadCompras.__rpComprasSurPrecarga = true;
            loadCompras.__rpComprasSurPrecargaOriginal = loadOriginal;

            return loadCompras;
        }

        var ajaxOriginal = jQuery.ajax;

        if (typeof ajaxOriginal === "function" &&
                !ajaxOriginal.__rpComprasSurPrecarga) {

            var ajaxCompras = function(opciones) {
                var configuracion;

                if (arguments.length === 1 &&
                        opciones &&
                        typeof opciones === "object" &&
                        esEndpointPrestacion(opciones.url)) {

                    configuracion = jQuery.extend({}, opciones);
                    configuracion.url = corregirUrl(
                            configuracion.url
                    );
                    configuracion.data = corregirDatos(
                            configuracion.data
                    );
                    configuracion.complete = envolverCallback(
                            configuracion.complete
                    );

                    return ajaxOriginal.call(
                            this,
                            configuracion
                    );
                }

                return ajaxOriginal.apply(this, arguments);
            };

            ajaxCompras.__rpComprasSurPrecarga = true;
            ajaxCompras.__rpComprasSurPrecargaOriginal = ajaxOriginal;
            jQuery.ajax = ajaxCompras;
        }

        jQuery.fn.load = envolverLoad(jQuery.fn.load);

        if (typeof window.ReclamoPrestacionalJQueryLoadOriginal ===
                "function") {

            window.ReclamoPrestacionalJQueryLoadOriginal =
                    envolverLoad(
                            window.ReclamoPrestacionalJQueryLoadOriginal
                    );
        }

        jQuery.fn.__rpComprasSurPrecargaInstalado = true;

        jQuery(document).ajaxComplete(function() {
            window.setTimeout(function() {
                aplicarSelector();
                instalarValidacionVisual();
            }, 0);
        });

        aplicarSelector();
        instalarValidacionVisual();
        window.setTimeout(function() {
            aplicarSelector();
            instalarValidacionVisual();
        }, 0);
    }

    jQuery(function() {
        window.setTimeout(instalar, 0);
    });
})(window, window.jQuery);