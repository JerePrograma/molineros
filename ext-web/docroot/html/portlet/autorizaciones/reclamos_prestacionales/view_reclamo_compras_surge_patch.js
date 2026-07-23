(function(window, jQuery) {
    if (!jQuery) {
        return;
    }

    function instalar() {
        var config = window.ReclamoPrestacionalViewConfig || {};
        var namespace = config.namespace || "";
        var inicial = jQuery(
                "#" + namespace + "recuperable_sur_compra_inicial"
        );

        if (!inicial.length
                || jQuery.fn.__rpComprasSurPrecargaInstalado) {
            return;
        }

        var valorActual = inicial.val() === "1" ? "1" : "0";

        function normalizarValor(valor) {
            var numero = parseInt(valor, 10);

            return numero >= 0 && numero <= 3
                    ? String(numero)
                    : "0";
        }

        function esEndpointPrestacion(url) {
            var texto = String(url || "");

            return texto.indexOf(
                    "struts_action=/autorizaciones/"
                            + "lista_prestaciones_reclamos"
            ) >= 0
                    || texto.indexOf(
                            "struts_action=/autorizaciones/"
                                    + "editar_reclamosprestaciones"
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
            var alta = jQuery(
                    "#" + namespace + "recuperable_sur"
            );
            var edicion = jQuery(
                    "#" + namespace + "recuperable_surEdicion"
            );

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

                window.setTimeout(aplicarSelector, 0);

                return resultado;
            };
        }

        function envolverLoad(loadOriginal) {
            if (typeof loadOriginal !== "function"
                    || loadOriginal.__rpComprasSurPrecarga) {
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

        if (typeof ajaxOriginal === "function"
                && !ajaxOriginal.__rpComprasSurPrecarga) {

            var ajaxCompras = function(opciones) {
                var configuracion;

                if (arguments.length === 1
                        && opciones
                        && typeof opciones === "object"
                        && esEndpointPrestacion(opciones.url)) {

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

        if (typeof window.ReclamoPrestacionalJQueryLoadOriginal
                === "function") {

            window.ReclamoPrestacionalJQueryLoadOriginal =
                    envolverLoad(
                            window.ReclamoPrestacionalJQueryLoadOriginal
                    );
        }

        jQuery.fn.__rpComprasSurPrecargaInstalado = true;

        jQuery(document).ajaxComplete(function() {
            window.setTimeout(aplicarSelector, 0);
        });

        aplicarSelector();
        window.setTimeout(aplicarSelector, 0);
    }

    jQuery(function() {
        window.setTimeout(instalar, 0);
    });
})(window, window.jQuery);
