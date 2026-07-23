(function(window, document, jQuery) {
    if (!jQuery) {
        return;
    }

    var config = window.ReclamoPrestacionalViewConfig || {};
    var namespace = config.namespace || "";
    var values = config.values || {};
    var urls = config.urls || {};
    var MARCA_VALIDACION = "__rpComprasValidacionVisual";
    var MARCA_LOAD = "__rpComprasSurPrecarga";
    var MARCA_AJAX = "__rpComprasSurPrecarga";
    var valorRecuperableActual = "0";

    function campo(sufijo) {
        return jQuery("#" + namespace + sufijo);
    }

    function valor(sufijo) {
        var control = campo(sufijo);
        return control.length ? control.val() : "";
    }

    function esBorradorCompras() {
        return values.esBorradorCompras === true ||
                String(values.esBorradorCompras) === "true";
    }

    function esVacio(valorActual) {
        return valorActual == null || String(valorActual) === "";
    }

    function numero(valorActual) {
        var texto = String(valorActual == null ? "" : valorActual)
                .replace(/\s/g, "")
                .replace(",", ".");
        var resultado = parseFloat(texto);
        return isNaN(resultado) ? 0 : resultado;
    }

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

        if (typeof ajaxOriginal !== "function") {
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

    /* Debe ejecutarse antes de cargar view_reclamo_tab_guard.js. */
    instalarAjaxPrefilterLegacy();

    function reemplazarPorHiddenSeguro(sufijo) {
        campo(sufijo).each(function() {
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

    function limpiarErroresVisuales() {
        jQuery(".rp-campo-error").removeClass("rp-campo-error");
        jQuery(".rp-campo-error-contenedor")
                .removeClass("rp-campo-error-contenedor");
        campo("reclamo_validacion_lista").empty();
        campo("reclamo_validacion_resumen").css("display", "none");
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

    function agregarError(errores, mensaje, control) {
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
        var lista = campo("reclamo_validacion_lista");
        var resumen = campo("reclamo_validacion_resumen");
        var primero = null;
        var posicion;
        var item;
        var i;

        if (!errores.length) {
            return false;
        }

        lista.empty();

        for (i = 0; i < errores.length; i++) {
            item = jQuery(document.createElement("li"));
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
                // El resumen sigue visible si el control no admite foco.
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

    function noSeleccionado(sufijo) {
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
                    campo("plan")
            );
        }

        if (esVacio(valor("cuil")) || esVacio(valor("inte"))) {
            agregarError(
                    errores,
                    "Debe seleccionar el afiliado asociado al reclamo.",
                    campo("cuil")
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

        if (noSeleccionado("sector")) {
            agregarError(
                    errores,
                    "Debe seleccionar el sector del reclamo.",
                    campo("sector")
            );
        }

        if (tipoPedido === "" || tipoPedido === "SELECCIONAR" ||
                tipoPedido === "SELECCIONE") {
            agregarError(
                    errores,
                    "Debe seleccionar el Tipo de Pedido.",
                    campo("tipopedido")
            );
        }

        if (isNaN(cantidadPrestaciones) || cantidadPrestaciones < 1) {
            agregarError(
                    errores,
                    "Debe existir al menos una prestacion activa.",
                    campo("lista_prestaciones_reclamos")
            );
        }

        if (tipoPedido === "EXCEPCION" && campo("integracion").length &&
                noSeleccionado("integracion")) {
            agregarError(
                    errores,
                    "Debe seleccionar un tipo de integracion.",
                    campo("integracion")
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
        var errores = [];
        var resultado;
        var control;
        var i;

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
            agregarError(errores, mensajes[i], control);
        }

        mostrarErrores(errores);
        return false;
    }

    function cadenaContieneMarca(funcion, marca) {
        var actual = funcion;
        var pasos = 0;

        while (typeof actual === "function" && pasos < 12) {
            if (actual[marca]) {
                return true;
            }

            actual = actual.__rpProduccion7305Original ||
                    actual.__rpComprasValidacionOriginal ||
                    actual.__rpOriginal ||
                    null;
            pasos++;
        }

        return false;
    }

    function envolverGuardado(nombreFuncion) {
        var original = window[nombreFuncion];
        var envuelta;

        if (typeof original !== "function" ||
                cadenaContieneMarca(original, MARCA_VALIDACION)) {
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

    function normalizarRecuperable(valorEntrada) {
        var convertido = parseInt(valorEntrada, 10);

        return convertido >= 0 && convertido <= 3 ?
                String(convertido) : "0";
    }

    function leerRecuperableInicial() {
        valorRecuperableActual = normalizarRecuperable(
                valor("recuperable_sur_compra_inicial")
        );
    }

    function esEndpointPrestacion(url) {
        var texto = String(url || "");

        return texto.indexOf(
                "struts_action=/autorizaciones/lista_prestaciones_reclamos"
        ) >= 0 || texto.indexOf(
                "struts_action=/autorizaciones/editar_reclamosprestaciones"
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
                    "$1recuperableSur=" + valorRecuperableActual
            );
        }

        return texto + (texto.indexOf("?") >= 0 ? "&" : "?") +
                "recuperableSur=" + valorRecuperableActual;
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

    /* Alias estable requerido por la correccion historica de Surge. */
    var valorActual = valorRecuperableActual;

    function sincronizarAliasRecuperable() {
        valorActual = valorRecuperableActual;
    }

    function envolverLoadRecuperable(loadOriginal) {
        if (typeof loadOriginal !== "function" || loadOriginal[MARCA_LOAD]) {
            return loadOriginal;
        }

        var envuelta = function(url, datos, callback) {
            var urlCorregida = corregirUrl(url);
            var datosCorregidos = datos;
            var callbackCorregido = callback;

            if (typeof datos === "function") {
                callbackCorregido = datos;
                datosCorregidos = undefined;
            }

            if (esEndpointPrestacion(urlCorregida) &&
                    datosCorregidos !== undefined) {
                sincronizarAliasRecuperable();
                datosCorregidos = corregirDatos(datosCorregidos);
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

        envuelta[MARCA_LOAD] = true;
        envuelta.__rpComprasSurPrecargaOriginal = loadOriginal;
        return envuelta;
    }

    function envolverAjaxRecuperable() {
        var ajaxOriginal = jQuery.ajax;

        if (typeof ajaxOriginal !== "function" || ajaxOriginal[MARCA_AJAX]) {
            return;
        }

        var envuelta = function(opciones) {
            var configuracion;

            if (arguments.length === 1 && opciones &&
                    typeof opciones === "object" &&
                    esEndpointPrestacion(opciones.url)) {

                configuracion = jQuery.extend({}, opciones);
                configuracion.url = corregirUrl(configuracion.url);
                sincronizarAliasRecuperable();
                configuracion.data = corregirDatos(configuracion.data);
                return ajaxOriginal.call(this, configuracion);
            }

            return ajaxOriginal.apply(this, arguments);
        };

        envuelta[MARCA_AJAX] = true;
        envuelta.__rpComprasSurPrecargaOriginal = ajaxOriginal;
        jQuery.ajax = envuelta;
    }

    function registrarCambioRecuperable(control) {
        if (!control.length || control.data("rpComprasSurCambio")) {
            return;
        }

        control.data("rpComprasSurCambio", true);
        control.change(function() {
            valorRecuperableActual = normalizarRecuperable(
                    jQuery(this).val()
            );
            sincronizarAliasRecuperable();
        });
    }

    function aplicarRecuperableCompras() {
        var alta = campo("recuperable_sur");
        var edicion = campo("recuperable_surEdicion");

        registrarCambioRecuperable(alta);
        registrarCambioRecuperable(edicion);

        if (alta.length) {
            alta.removeAttr("disabled").val(valorRecuperableActual);
        }
        if (edicion.length) {
            edicion.removeAttr("disabled").val(valorRecuperableActual);
        }

        if (typeof window.cambiorecuperable === "function") {
            window.cambiorecuperable();
        }
        if (typeof window.cambiorecuperableEdicion === "function") {
            window.cambiorecuperableEdicion();
        }
    }

    function prepararInterfazCompras() {
        var comprobante = campo("datos_comprobante");
        var aviso = campo("rp_compras_comprobante_info");

        comprobante.css("display", "none");
        comprobante.attr("aria-hidden", "true");
        campo("comprobante_tipo_edicion").val("OTR");

        if (!aviso.length && comprobante.length) {
            aviso = jQuery(document.createElement("div"));
            aviso.attr("id", namespace + "rp_compras_comprobante_info");
            aviso.addClass("portlet-msg-info");
            aviso.text(
                    "La cotizacion de Compras no es una factura. " +
                    "Los datos de comprobante no se solicitan en esta precarga."
            );
            comprobante.before(aviso);
        }
    }

    function validarPrestacionCompras() {
        var errores = [];
        var frecuencia = String(valor("frecuenciaEdicion") || "");
        var codigo = String(valor("codigoSeguimiento_filtro_edit") || "");
        var descripcion = String(
                valor("descripcionSeguimiento_filtro_edit") || ""
        );
        var medicamento = String(valor("troquel_edit") || "");
        var cantidad = numero(valor("cantidadEdicion"));
        var importe = numero(valor("importeEdicion"));
        var total = Math.round(cantidad * importe * 100) / 100;
        var cargos = Math.round((
                numero(valor("cargoospimEdicion")) +
                numero(valor("cargopsEdicion")) +
                numero(valor("cargoimesaEdicion"))
        ) * 100) / 100;

        if (!frecuencia || frecuencia === "SELECCIONE") {
            agregarError(
                    errores,
                    "Debe seleccionar la frecuencia correspondiente.",
                    campo("frecuenciaEdicion")
            );
        }

        if (fechaAusente("fechaPrestacion")) {
            agregarErrorFecha(
                    errores,
                    "Debe ingresar la fecha de la Prestacion.",
                    "fechaPrestacion"
            );
        }

        if ((!codigo || codigo.indexOf("ART-") === 0) && !medicamento) {
            agregarError(
                    errores,
                    "Debe confirmar el nomenclador o medicamento de la prestacion.",
                    campo("codigoSeguimiento_filtro_edit")
            );
        } else if (codigo && codigo.indexOf("ART-") !== 0 && !descripcion) {
            agregarError(
                    errores,
                    "Debe confirmar la descripcion de la prestacion.",
                    campo("descripcionSeguimiento_filtro_edit")
            );
        }

        if (cantidad <= 0) {
            agregarError(
                    errores,
                    "La cantidad de la prestacion debe ser mayor a cero.",
                    campo("cantidadEdicion")
            );
        }

        if (importe <= 0) {
            agregarError(
                    errores,
                    "El importe de la prestacion debe ser mayor a cero.",
                    campo("importeEdicion")
            );
        }

        if (Math.abs(total - cargos) > 0.01) {
            agregarError(
                    errores,
                    "La suma de los cargos debe coincidir con el total de la prestacion.",
                    campo("cargoospimEdicion")
            );
            marcarControl(campo("cargopsEdicion"));
            marcarControl(campo("cargoimesaEdicion"));
        }

        return errores;
    }

    function guardarPrestacionCompras() {
        var errores;
        var destino;
        var cargador;
        var completar;
        var nomSeleccionado;
        var tipoNomenclador;
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
            recuperableSur: parseInt(valorRecuperableActual, 10) || 0,
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
        params[namespace + "reclamoDraftId"] = valor("reclamoDraftId");

        destino = campo("lista_prestaciones_reclamos");
        cargador = window.ReclamoPrestacionalJQueryLoadOriginal;
        campo("buscando").css("display", "block");

        completar = function(respuesta, estado) {
            var cancelar;

            campo("buscando").css("display", "none");

            if (estado === "error") {
                mostrarErrores([{
                    mensaje: "No se pudo actualizar la prestacion precargada.",
                    control: destino
                }]);
                return;
            }

            campo("tipoaccionprestacion").val("0");
            cancelar = window[namespace + "cancelaEdicionPrestacion"];

            if (typeof cancelar === "function") {
                cancelar();
            } else {
                campo("datos_edicion_prestacion").css("display", "none");
                campo("datos_prestacion_ingreso").css("display", "block");
            }

            limpiarErroresVisuales();
        };

        if (typeof cargador === "function") {
            cargador.call(destino, urls.editarPrestaciones, params, completar);
        } else {
            destino.load(urls.editarPrestaciones, params, completar);
        }

        return false;
    }

    function instalarGuardadoPrestacionCompras() {
        var boton;

        window[namespace + "editarPrestacionSeleccionada"] =
                guardarPrestacionCompras;

        boton = campo("rp_guardar_prestacion_seguro");
        if (boton.length) {
            boton[0].onclick = function() {
                return guardarPrestacionCompras();
            };
        }
    }

    function instalar() {
        normalizarAuxiliares();
        envolverGuardado(namespace + "saveReclamo");
        envolverGuardado(namespace + "editaReclamo");

        if (!esBorradorCompras()) {
            return;
        }

        leerRecuperableInicial();
        aplicarRecuperableCompras();
        prepararInterfazCompras();
        envolverAjaxRecuperable();
        jQuery.fn.load = envolverLoadRecuperable(jQuery.fn.load);
        instalarGuardadoPrestacionCompras();

        window.ReclamoPrestacionalComprasGuardadoFinal = {
            validar: validarPrestacionCompras,
            guardar: guardarPrestacionCompras
        };
    }

    jQuery(function() {
        window.setTimeout(instalar, 25);
    });

    jQuery(document).ajaxComplete(function() {
        window.setTimeout(instalar, 25);
    });
})(window, document, window.jQuery);
