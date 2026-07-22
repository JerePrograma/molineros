(function(window, jQuery) {
"use strict";

if (!jQuery || !jQuery.fn || typeof jQuery.fn.load !== "function") {
    return;
}

var config = window.ReclamoPrestacionalViewConfig || {};
var namespace = config.namespace || "";
var ENDPOINT_EDITOR = "editar_reclamosprestaciones";
var loadOriginal = jQuery.fn.load;

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

    var inicializacionRota =
            texto.indexOf("prestacionEnEdicion != null") >= 0 &&
            texto.indexOf("datos_edicion_prestacion") >= 0;

    var utilidadesLocalesInseguras =
            texto.indexOf("filtrarLetraComprobanteEdicion") >= 0 &&
            texto.indexOf("calculatotalFCEdicion") >= 0;

    return inicializacionRota || utilidadesLocalesInseguras;
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
        return "Observación de edición:";
    }
    if (campo("btnautoriza_prestacion").length) {
        return "Observación de autorización:";
    }
    if (campo("btnrechaza_prestacion").length) {
        return "Observación de rechazo:";
    }
    return "Observación:";
}

function repararEtiquetaObservacion() {
    var textarea = campo("observacion_prestacionEdicion");
    if (!textarea.length) {
        return;
    }

    var celdaEtiqueta = textarea.closest("td").prev("td");
    if (celdaEtiqueta.length) {
        celdaEtiqueta.text(etiquetaObservacion());
    }
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
            usarDosDecimales ? total.toFixed(2) : Math.round(total * 100) / 100
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
    var recuperable = String(campo("recuperable_surEdicion").val() || "");
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
            letraInicial : metadatos.letraComprobante;
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

    contenedor = contenedor && contenedor.length ?
            contenedor : campo("datos_edicion_prestacion");

    if (!contenedor.length) {
        return;
    }

    contenedor.show();
    contenedor.data("rpEditorMeta", datos);

    if (datos.ocultarAutorizado) {
        campo("Autorizado").hide();
    }

    var codigo = campo("codigoSeguimiento_filtro_edit").val();
    if (codigo) {
        campo("codigoprestacion").val(codigo);
    }

    repararEtiquetaObservacion();
    cambiorecuperableEdicionSeguro();

    filtrarLetraComprobanteEdicionSeguro(
            datos.letraComprobante,
            datos.urlFiltroLetra
    );

    var buscarNomenclador =
            window[namespace + "buscarNomencladorAutocompletar_edit"];
    if (codigo && typeof buscarNomenclador === "function") {
        window.setTimeout(buscarNomenclador, 0);
    }
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
                    funcionCallback.call(this, htmlLimpio, estado, xhr);
                }
            });
        },
        error: function(xhr, estado, error) {
            destino.html(
                    '<div class="portlet-msg-error">' +
                    "No se pudo cargar el editor de la prestación." +
                    "</div>"
            );

            if (typeof funcionCallback === "function") {
                destino.each(function() {
                    funcionCallback.call(
                            this,
                            xhr && xhr.responseText ? xhr.responseText : "",
                            estado,
                            xhr
                    );
                });
            }

            if (window.console && window.console.error) {
                window.console.error(
                        "Error cargando editor de prestación",
                        error || estado
                );
            }
        }
    });

    return destino;
}

if (!loadOriginal.__rpEditorSeguro) {
    var loadSeguro = function(url, parametros, callback) {
        if (typeof url !== "string" || url.indexOf(ENDPOINT_EDITOR) < 0) {
            return loadOriginal.apply(this, arguments);
        }
        return cargarEditorSeguro.call(this, url, parametros, callback);
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
window.ReclamoPrestacionalEditorPatch = {
    limpiarScriptsLegacy: limpiarScriptsLegacy,
    extraerMetadatos: extraerMetadatos,
    repararEditor: repararEditor
};

jQuery(document).ready(function() {
    var editor = campo("datos_edicion_prestacion");
    if (editor.length && editor.children().length) {
        repararEditor(editor, obtenerMetadatosEditor());
    }
});

})(window, jQuery);
