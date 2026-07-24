(function(window, jQuery) {
    "use strict";

    if (!jQuery || !jQuery.fn || typeof jQuery.fn.load !== "function") {
        return;
    }

    var config = window.ReclamoPrestacionalViewConfig || {};
    var namespace = config.namespace || "";
    var ENDPOINT_EDITOR = "editar_reclamosprestaciones";
    var loadOriginal = jQuery.fn.load;
    var NOMBRE_BUSQUEDA_EDITOR =
        namespace + "buscarNomencladorAutocompletar_edit";
    var NOMBRE_CIERRE_NOMENCLADOR = namespace + "cerrarNm";
    var busquedaInicialBloqueada = true;

    function campo(sufijo) {
        return jQuery("#" + namespace + sufijo);
    }

    function numeroDecimal(valor) {
        var normalizado = String(valor == null ? "" : valor)
            .replace(/\s/g, "")
            .replace(",", ".");
        var numero = parseFloat(normalizado);
        return isNaN(numero) ? 0 : numero;
    }

    function extraerMetadatos(html) {
        var texto = String(html || "");
        var metadatos = {
            letraComprobante: "",
            urlFiltroLetra: "",
            ocultarAutorizado: false
        };

        var letra = texto.match(
            /comprobante_letra_edicion[^;\n]*\.val\(["']([^"']*)["']\)/i
        );
        if (letra) {
            metadatos.letraComprobante = letra[1];
        }

        var url = texto.match(
            /var\s+url\s*=\s*'([^']*filtrarLetraComprobante[^']*)'\s*\+\s*tipoPedido/i
        );
        if (url) {
            metadatos.urlFiltroLetra = url[1];
        }

        metadatos.ocultarAutorizado =
            /Autorizado[^;\n]*\.hide\(\)/i.test(texto);

        return metadatos;
    }

    function esScriptLegacyDelEditor(cuerpo) {
        var texto = String(cuerpo || "");

        var inicializacionLegacy =
            texto.indexOf("datos_edicion_prestacion") >= 0 &&
            (
                texto.indexOf(
                    "buscarNomencladorAutocompletar_edit"
                ) >= 0 ||
                texto.indexOf("troquel_edit") >= 0
            );

        var utilidadesLocalesInseguras =
            texto.indexOf("filtrarLetraComprobanteEdicion") >= 0 &&
            texto.indexOf("calculatotalFCEdicion") >= 0;

        return inicializacionLegacy || utilidadesLocalesInseguras;
    }

    function limpiarScriptsLegacy(html) {
        return String(html || "").replace(
            /<script\b[^>]*>([\s\S]*?)<\/script>/gi,
            function(scriptCompleto, cuerpo) {
                return esScriptLegacyDelEditor(cuerpo) ? "" : scriptCompleto;
            }
        );
    }

    function etiquetaObservacion() {
        if (campo("btnedita_prestacion").length) {
            return "Observaci\u00f3n de edici\u00f3n:";
        }
        if (campo("btnautoriza_prestacion").length) {
            return "Observaci\u00f3n de autorizaci\u00f3n:";
        }
        if (campo("btnrechaza_prestacion").length) {
            return "Observaci\u00f3n de rechazo:";
        }
        return "Observaci\u00f3n:";
    }

    function repararEtiquetaObservacion() {
        var textarea = campo("observacion_prestacionEdicion");
        if (!textarea.length) {
            return;
        }

        var celdaEtiqueta =
            campo("observacion_prestacionEdicion_label");

        if (celdaEtiqueta.length) {
            celdaEtiqueta.text(etiquetaObservacion());
        }
    }

    function esTextoVacio(valor) {
        return String(valor == null ? "" : valor)
            .replace(/^\s+|\s+$/g, "") === "";
    }

    function textoCancelacionEditor() {
        if (campo("btnautoriza_prestacion").length) {
            return "Cancelar Autorizaci\u00f3n de la Prestaci\u00f3n";
        }

        if (campo("btnrechaza_prestacion").length) {
            return "Cancelar Rechazo de la Prestaci\u00f3n";
        }

        return "Cancelar Edici\u00f3n de la Prestaci\u00f3n";
    }

    function repararBotonesEditor(contenedor) {
        var botones = campo("botones_edicion_prestacion");

        if (!botones.length && contenedor && contenedor.length) {
            botones = contenedor.find(
                "#" + namespace + "botones_edicion_prestacion"
            );
        }

        if (!botones.length) {
            return;
        }

        botones.find("input").each(function() {
            var control = jQuery(this);
            var tipo = String(this.type || "").toLowerCase();
            var id = String(this.id || "");
            var texto = "";

            if (tipo !== "button" && tipo !== "submit") {
                return;
            }

            if (id === namespace + "btnedita_prestacion") {
                texto = "Editar Prestaci\u00f3n";
            } else if (id === namespace + "btnautoriza_prestacion") {
                texto = "Autoriza Prestaci\u00f3n";
            } else if (id === namespace + "btnrechaza_prestacion") {
                texto = "Rechaza Prestaci\u00f3n";
            } else if (id === namespace + "btncancelar_prestacion") {
                texto = textoCancelacionEditor();
            } else if (!this.id && !this.name) {
                /*
                 * Compatibilidad con respuestas antiguas del JSP, donde el
                 * boton Cancelar no tenia id ni name.
                 */
                texto = textoCancelacionEditor();

                control.attr(
                    "id",
                    namespace + "btncancelar_prestacion"
                );
                control.attr(
                    "name",
                    namespace + "btncancelar_prestacion"
                );
            }

            if (texto && esTextoVacio(control.val())) {
                control.val(texto);
            }
        });
    }

    function limpiarResiduosEditor(contenedor) {
        if (!contenedor || !contenedor.length) {
            return;
        }

        contenedor.find("input").each(function() {
            var control = jQuery(this);
            var tipo = String(this.type || "text").toLowerCase();
            var sinIdentidad = !this.id && !this.name;
            var tipoVisualVacio = tipo === "text";

            if (sinIdentidad && tipoVisualVacio &&
                esTextoVacio(control.val())) {
                control.remove();
            }
        });

        contenedor.find("div").each(function() {
            var nodo = jQuery(this);
            var sinIdentidad = !this.id &&
                esTextoVacio(this.className || "");

            if (sinIdentidad && !nodo.children().length &&
                esTextoVacio(nodo.text())) {
                nodo.remove();
            }
        });
    }

    function asignarNomencladorSeguro(tipoNomenclador, codigo, descripcion) {
        campo("codigoSeguimiento_filtro").val(codigo);
        campo("descripcionSeguimiento_filtro").val(descripcion);
        campo("nom_seleccionado").val("1");
        campo("tipoNomenclador").val(tipoNomenclador);

        campo("codigoSeguimiento_filtro_edit").val(codigo);
        campo("descripcionSeguimiento_filtro_edit").val(descripcion);
        campo("nom_seleccionado_edit").val("1");
        campo("tipoNomenclador_edit").val(tipoNomenclador);
        campo("codigoprestacion").val(codigo);
    }

    function instalarCierreNomencladorSeguro() {
        var cerrarOriginal = window[NOMBRE_CIERRE_NOMENCLADOR];
        var cierreEnCurso = false;
        var cerrarSeguro;

        if (typeof cerrarOriginal === "function" &&
            !cerrarOriginal.__rpCierreNomencladorSeguro) {

            cerrarSeguro = function() {
                if (cierreEnCurso) {
                    return false;
                }

                cierreEnCurso = true;
                try {
                    return cerrarOriginal.apply(this, arguments);
                } finally {
                    window.setTimeout(function() {
                        cierreEnCurso = false;
                    }, 0);
                }
            };

            cerrarSeguro.__rpCierreNomencladorSeguro = true;
            cerrarSeguro.__rpCierreNomencladorOriginal = cerrarOriginal;
            window[NOMBRE_CIERRE_NOMENCLADOR] = cerrarSeguro;
        }

        window.seleccionaCamposNm = asignarNomencladorSeguro;
        window.pasarParametrosAParentNm = function(
            tipoNomenclador,
            codigo,
            descripcion) {

            var cerrar;

            asignarNomencladorSeguro(
                tipoNomenclador,
                codigo,
                descripcion
            );

            cerrar = window[NOMBRE_CIERRE_NOMENCLADOR];
            if (typeof cerrar === "function") {
                cerrar();
            }
        };
    }

    function instalarBloqueoBusquedaInicial() {
        var buscarOriginal = window[NOMBRE_BUSQUEDA_EDITOR];
        var buscarSeguro;

        if (typeof buscarOriginal !== "function" ||
            buscarOriginal.__rpBusquedaNomencladorSegura) {
            return;
        }

        buscarSeguro = function() {
            if (busquedaInicialBloqueada) {
                return false;
            }

            return buscarOriginal.apply(this, arguments);
        };

        buscarSeguro.__rpBusquedaNomencladorSegura = true;
        buscarSeguro.__rpBusquedaNomencladorOriginal = buscarOriginal;
        window[NOMBRE_BUSQUEDA_EDITOR] = buscarSeguro;
    }

    function habilitarBusquedaManual() {
        busquedaInicialBloqueada = false;
    }

    function calcularTotalSeguro(
        sufijoImporte,
        sufijoCantidad,
        sufijoTotal,
        usarDosDecimales) {

        var importe = numeroDecimal(campo(sufijoImporte).val());
        var cantidad = numeroDecimal(campo(sufijoCantidad).val());
        var total = importe * cantidad;

        campo(sufijoTotal).val(
            usarDosDecimales ?
                total.toFixed(2) :
                Math.round(total * 100) / 100
        );
    }

    function calculatotalSeguro() {
        calcularTotalSeguro(
            "importeEdicion",
            "cantidadEdicion",
            "totalEdicion",
            true
        );
    }

    function calculatotalFCEdicionSeguro() {
        calcularTotalSeguro(
            "importeUnitarioFC_edicion",
            "cantidadFC_edicion",
            "importeFC_edicion",
            true
        );
    }

    function cambiorecuperableEdicionSeguro() {
        var recuperable = String(
            campo("recuperable_surEdicion").val() || ""
        );
        var reconocido = campo("reconocidoSSSEdicion");

        if (!reconocido.length) {
            return;
        }

        if (recuperable === "1" || recuperable === "3") {
            reconocido.removeAttr("readonly");
        } else {
            reconocido.val("0").attr("readonly", "readonly");
        }
    }

    function completarConCerosSeguro(valor, longitud) {
        var digitos = String(valor || "").replace(/\D/g, "");
        var ceros = "";
        var i;

        if (!digitos) {
            return "";
        }

        for (i = 0; i < longitud; i++) {
            ceros += "0";
        }

        return (ceros + digitos).slice(-longitud);
    }

    function obtenerMetadatosEditor() {
        var contenedor = campo("datos_edicion_prestacion");
        return contenedor.data("rpEditorMeta") || {};
    }

    function filtrarLetraComprobanteEdicionSeguro(
        letraInicial,
        urlFiltroLetra) {

        var select = campo("comprobante_letra_edicion");
        if (!select.length) {
            return;
        }

        var metadatos = obtenerMetadatosEditor();
        var letra = letraInicial != null ?
            letraInicial :
            metadatos.letraComprobante;
        var urlBase = urlFiltroLetra || metadatos.urlFiltroLetra;
        var tipoPedido = campo("tipopedido").val() || "";

        if (!urlBase) {
            select.removeAttr("disabled");
            return;
        }

        select.attr("disabled", "disabled");

        jQuery.ajax({
            url: urlBase + encodeURIComponent(tipoPedido),
            type: "GET",
            dataType: "html",
            cache: false,
            success: function(data) {
                select.html(data);

                if (letra != null && letra !== "") {
                    select.val(letra);
                }
            },
            error: function(xhr, estado, error) {
                select.empty().append(
                    jQuery("<option/>", {
                        value: "",
                        text: "No se pudo cargar"
                    })
                );

                if (window.console && window.console.error) {
                    window.console.error(
                        "No se pudo filtrar la letra del comprobante",
                        error || estado
                    );
                }
            },
            complete: function() {
                select.removeAttr("disabled");
            }
        });
    }

    function repararEditor(contenedor, metadatos) {
        var datos = metadatos || {};
        var codigo;

        contenedor = contenedor && contenedor.length ?
            contenedor :
            campo("datos_edicion_prestacion");

        if (!contenedor.length) {
            return;
        }

        contenedor.show();
        contenedor.attr("aria-hidden", "false");
        contenedor.data("rpEditorMeta", datos);

        if (datos.ocultarAutorizado) {
            campo("Autorizado").hide();
        }

        codigo = campo("codigoSeguimiento_filtro_edit").val();

        if (codigo) {
            campo("codigoprestacion").val(codigo);
        }

        /*
         * Los botones deben repararse antes de eliminar controles anonimos.
         * Las respuestas antiguas traen el boton Cancelar sin id ni name.
         */
        repararBotonesEditor(contenedor);
        limpiarResiduosEditor(contenedor);
        repararEtiquetaObservacion();
        cambiorecuperableEdicionSeguro();

        filtrarLetraComprobanteEdicionSeguro(
            datos.letraComprobante,
            datos.urlFiltroLetra
        );
    }

    function cargarEditorSeguro(url, parametros, callback) {
        var destino = this;
        var datos = parametros;
        var funcionCallback = callback;
        var metodo = "GET";

        if (typeof parametros === "function") {
            funcionCallback = parametros;
            datos = undefined;
        } else if (parametros && typeof parametros === "object") {
            metodo = "POST";
        }

        jQuery.ajax({
            url: url,
            type: metodo,
            data: datos,
            dataType: "html",
            cache: false,
            success: function(respuesta, estado, xhr) {
                var metadatos = extraerMetadatos(respuesta);
                var htmlLimpio = limpiarScriptsLegacy(respuesta);

                destino.each(function() {
                    var elemento = jQuery(this);

                    elemento.html(htmlLimpio);
                    repararEditor(elemento, metadatos);

                    if (typeof funcionCallback === "function") {
                        funcionCallback.call(
                            this,
                            htmlLimpio,
                            estado,
                            xhr
                        );
                    }
                });
            },
            error: function(xhr, estado, error) {
                destino.html(
                    '<div class="portlet-msg-error">' +
                    "No se pudo cargar el editor de la prestaci\u00f3n." +
                    "</div>"
                );

                if (typeof funcionCallback === "function") {
                    destino.each(function() {
                        funcionCallback.call(
                            this,
                            xhr && xhr.responseText ?
                                xhr.responseText :
                                "",
                            estado,
                            xhr
                        );
                    });
                }

                if (window.console && window.console.error) {
                    window.console.error(
                        "Error cargando editor de prestaci\u00f3n",
                        error || estado
                    );
                }
            }
        });

        return destino;
    }

    if (!loadOriginal.__rpEditorSeguro) {
        var loadSeguro = function(url, parametros, callback) {
            if (typeof url !== "string" ||
                url.indexOf(ENDPOINT_EDITOR) < 0) {

                return loadOriginal.apply(this, arguments);
            }

            return cargarEditorSeguro.call(
                this,
                url,
                parametros,
                callback
            );
        };

        loadSeguro.__rpEditorSeguro = true;
        jQuery.fn.load = loadSeguro;
    }

    window.calculatotal = calculatotalSeguro;
    window.calculatotalFCEdicion = calculatotalFCEdicionSeguro;
    window.cambiorecuperableEdicion = cambiorecuperableEdicionSeguro;
    window.completarConCeros = completarConCerosSeguro;
    window.filtrarLetraComprobanteEdicion =
        filtrarLetraComprobanteEdicionSeguro;

    instalarCierreNomencladorSeguro();
    instalarBloqueoBusquedaInicial();

    window.ReclamoPrestacionalEditorPatch = {
        limpiarScriptsLegacy: limpiarScriptsLegacy,
        extraerMetadatos: extraerMetadatos,
        repararBotonesEditor: repararBotonesEditor,
        limpiarResiduosEditor: limpiarResiduosEditor,
        repararEditor: repararEditor,
        asignarNomencladorSeguro: asignarNomencladorSeguro,
        habilitarBusquedaManual: habilitarBusquedaManual
    };

    jQuery(document).ready(function() {
        var editor = campo("datos_edicion_prestacion");

        instalarCierreNomencladorSeguro();
        instalarBloqueoBusquedaInicial();

        if (editor.length && editor.children().length) {
            repararEditor(
                editor,
                obtenerMetadatosEditor()
            );
        }

        window.setTimeout(
            habilitarBusquedaManual,
            0
        );
    });

})(window, jQuery);