(function(window, document, jQuery) {
"use strict";

if (!jQuery || !jQuery.fn) {
    return;
}

var config = window.ReclamoPrestacionalViewConfig || {};
var namespace = config.namespace || "";
var values = config.values || {};
var MARCA_PROP = "__rpPropLegacyCompatible";
var MARCA_OFFSET = "__rpOffsetLegacySeguro";
var MARCA_TIPO_PEDIDO = "rpIntegracionComprasInstalada";
var MARCA_VALIDACION_AJAX = "__rpValidacionAfiliadoSegura";

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function esBorradorCompras() {
    return values.esBorradorCompras === true ||
            String(values.esBorradorCompras) === "true";
}

function esPropiedadBooleana(nombre) {
    return nombre === "checked" ||
            nombre === "disabled" ||
            nombre === "readonly" ||
            nombre === "selected";
}

function instalarPropLegacy() {
    if (typeof jQuery.fn.prop === "function") {
        return;
    }

    var propCompatible = function(nombre, valorPropiedad) {
        if (arguments.length === 1) {
            if (!this.length || !this[0]) {
                return undefined;
            }
            return this[0][nombre];
        }

        return this.each(function() {
            this[nombre] = valorPropiedad;

            if (esPropiedadBooleana(nombre)) {
                if (valorPropiedad) {
                    jQuery(this).attr(nombre, nombre);
                } else {
                    jQuery(this).removeAttr(nombre);
                }
            }
        });
    };

    propCompatible[MARCA_PROP] = true;
    jQuery.fn.prop = propCompatible;
}

function instalarOffsetSeguro() {
    var offsetOriginal = jQuery.fn.offset;

    if (typeof offsetOriginal !== "function" ||
            offsetOriginal[MARCA_OFFSET]) {
        return;
    }

    var offsetSeguro = function() {
        if (arguments.length === 0 &&
                (!this.length || !this[0] ||
                this[0].nodeType !== 1)) {

            return null;
        }

        return offsetOriginal.apply(this, arguments);
    };

    offsetSeguro[MARCA_OFFSET] = true;
    offsetSeguro.__rpOffsetOriginal = offsetOriginal;
    jQuery.fn.offset = offsetSeguro;
}

function esRespuestaHtml(data) {
    var texto;

    if (typeof data !== "string") {
        return false;
    }

    texto = data.replace(/^\s+/, "");
    return texto.charAt(0) === "<";
}

function instalarValidacionAfiliadoSegura() {
    if (window[MARCA_VALIDACION_AJAX] ||
            typeof jQuery.ajaxPrefilter !== "function") {

        return;
    }

    window[MARCA_VALIDACION_AJAX] = true;

    jQuery.ajaxPrefilter(function(opciones) {
        var url = String(opciones && opciones.url || "");
        var successOriginal;

        if (url.indexOf(
                "validar_reclamo_afiliado_prestaciones"
        ) < 0 || typeof opciones.success !== "function") {

            return;
        }

        successOriginal = opciones.success;
        opciones.success = function(data) {
            if (esRespuestaHtml(data)) {
                throw new Error(
                        "No se pudo validar la fecha de baja del afiliado."
                );
            }

            return successOriginal.apply(this, arguments);
        };
    });
}

function normalizarTexto(valor) {
    return String(valor == null ? "" : valor)
            .toUpperCase()
            .replace(/^\s+|\s+$/g, "")
            .replace(/\s+/g, " ");
}

function seleccionarIntegracionNoRecuperable() {
    var integracion;
    var valorSeleccionado = "";

    if (!esBorradorCompras()) {
        return;
    }

    integracion = campo("integracion");
    if (!integracion.length) {
        return;
    }

    integracion.find("option").each(function() {
        var texto = normalizarTexto(this.text || this.innerText || "");

        if (!valorSeleccionado &&
                (texto === "NO RECUPERABLE" ||
                texto === "NO ES RECUPERABLE")) {

            valorSeleccionado = String(this.value || "");
        }
    });

    if (valorSeleccionado) {
        integracion.val(valorSeleccionado);
    }
}

function ocultarBuscadoresDuplicados() {
    var editor;

    if (!esBorradorCompras()) {
        return;
    }

    editor = campo("datos_edicion_prestacion");
    if (!editor.length || editor.css("display") === "none" ||
            !editor.children().length) {

        return;
    }

    campo("busqueda_prestaciones")
            .css("display", "none")
            .attr("aria-hidden", "true");
    campo("busqueda_farmacia")
            .css("display", "none")
            .attr("aria-hidden", "true");
}

function instalarReaplicacionIntegracion() {
    var tipoPedido = campo("tipopedido");

    if (!tipoPedido.length || tipoPedido.data(MARCA_TIPO_PEDIDO)) {
        return;
    }

    tipoPedido.data(MARCA_TIPO_PEDIDO, true);
    tipoPedido.change(function() {
        window.setTimeout(seleccionarIntegracionNoRecuperable, 0);
    });
}

function estabilizarInterfazCompras() {
    seleccionarIntegracionNoRecuperable();
    ocultarBuscadoresDuplicados();
    instalarReaplicacionIntegracion();
}

instalarPropLegacy();
instalarOffsetSeguro();

jQuery(function() {
    instalarValidacionAfiliadoSegura();
    estabilizarInterfazCompras();
    window.setTimeout(estabilizarInterfazCompras, 50);
    window.setTimeout(estabilizarInterfazCompras, 250);
});

jQuery(document).ajaxComplete(function() {
    window.setTimeout(estabilizarInterfazCompras, 0);
});

window.ReclamoPrestacionalLegacyCompatPatch = {
    estabilizar: estabilizarInterfazCompras,
    seleccionarIntegracionNoRecuperable:
            seleccionarIntegracionNoRecuperable,
    ocultarBuscadoresDuplicados: ocultarBuscadoresDuplicados
};

})(window, document, window.jQuery);
