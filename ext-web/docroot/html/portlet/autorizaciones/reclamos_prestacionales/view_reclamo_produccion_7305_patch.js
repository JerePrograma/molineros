(function(document) {
    var s = document.getElementsByTagName("script");
    var actual = s.length ? s[s.length - 1].src : "";
    var legacy = actual.replace(
        /view_reclamo_produccion_7305_patch\.js(?:\?.*)?$/,
        "view_reclamo_produccion_7305_legacy_patch.js?v=20260728-visual-2"
    );
    if (legacy && legacy !== actual) {
        document.write('<script type="text/javascript" src="' +
            legacy + '"><\/script>');
    }
})(document);

(function(window, jQuery) {
"use strict";
if (!jQuery) {
    return;
}

var cfg = window.ReclamoPrestacionalViewConfig || {};
var ns = cfg.namespace || "";

function c(id) {
    return jQuery("#" + ns + id);
}

function limpio(valor) {
    return String(valor == null ? "" : valor).replace(/^\s+|\s+$/g, "");
}

function esCompras() {
    var v = cfg.values || {};
    var editor = c("datos_edicion_prestacion");
    var texto = editor.length ? editor.text().toLowerCase() : "";

    if (texto.indexOf("compras no es una factura") < 0 &&
            texto.indexOf("cotización de compras") < 0) {
        texto = c("global").text().toLowerCase();
    }

    return String(v.esBorradorCompras) === "true" ||
        String(v.esReclamoCompras) === "true" ||
        String(v.desdeCompras) === "true" ||
        texto.indexOf("compras no es una factura") >= 0 ||
        texto.indexOf("cotización de compras") >= 0;
}

function ocultarComprobanteCompras() {
    var ids;
    var i;
    var control;
    var tabla;

    if (!esCompras()) {
        return;
    }

    c("datos_comprobante").hide().attr("aria-hidden", "true");
    ids = [
        "cuit_entidad_edicion",
        "sucursal_entidad_edicion",
        "entidad_edicion",
        "comprobante_suc_edicion",
        "comprobante_nro_edicion",
        "cantidadFC_edicion",
        "importeUnitarioFC_edicion",
        "importeFC_edicion"
    ];

    for (i = 0; i < ids.length; i++) {
        control = c(ids[i]);
        if (control.length) {
            tabla = control.closest("table");
            if (tabla.closest("#" + ns + "datos_comprobante").length) {
                tabla.hide().attr("aria-hidden", "true");
            } else {
                control.closest("td").hide().attr("aria-hidden", "true");
            }
        }
    }

    jQuery("#divEntidad_edicion").hide();
    c("divBtnBuscaEntidad_edicion").hide();
}

function textoBoton(id) {
    if (id === ns + "btnedita_prestacion") {
        return "Editar Prestación";
    }
    if (id === ns + "btnautoriza_prestacion") {
        return "Autoriza Prestación";
    }
    if (id === ns + "btnrechaza_prestacion") {
        return "Rechaza Prestación";
    }
    if (id === ns + "btncancelar_prestacion") {
        if (c("btnautoriza_prestacion").length) {
            return "Cancelar Autorización de la Prestación";
        }
        if (c("btnrechaza_prestacion").length) {
            return "Cancelar Rechazo de la Prestación";
        }
        return "Cancelar Edición de la Prestación";
    }
    return "";
}

function repararEditor() {
    var editor = c("datos_edicion_prestacion");
    var etiqueta = c("observacion_prestacionEdicion_label");
    var textarea = c("observacion_prestacionEdicion");
    var botones = c("botones_edicion_prestacion");
    var texto = "Observación de edición:";
    var fila;

    if (c("btnautoriza_prestacion").length) {
        texto = "Observación de autorización:";
    } else if (c("btnrechaza_prestacion").length) {
        texto = "Observación de rechazo:";
    }

    etiqueta.text(texto);
    botones.find("input[type='text']").filter(function() {
        return limpio(jQuery(this).val()) === "";
    }).remove();

    botones.find("input[type='button'],input[type='submit']").each(function() {
        var control = jQuery(this);
        var valor = textoBoton(String(this.id || ""));
        if (valor) {
            control.attr("value", valor).val(valor);
        }
        control.css({
            display: "inline-block",
            width: "auto",
            minWidth: "145px",
            margin: "2px 4px 2px 0"
        });
    }).end().css({
        whiteSpace: "normal",
        minWidth: "155px"
    });

    if (textarea.length) {
        textarea.css({
            width: "100%",
            maxWidth: "760px",
            boxSizing: "border-box"
        });
        fila = textarea.closest("tr");
        fila.children("td").eq(0).css({
            width: "180px",
            whiteSpace: "nowrap",
            verticalAlign: "top"
        });
        fila.children("td").eq(1).css("verticalAlign", "top");
        fila.children("td").eq(2).css({
            width: "190px",
            verticalAlign: "top"
        });
    }

    editor.css({
        width: "100%",
        maxWidth: "100%",
        boxSizing: "border-box"
    });
    ocultarComprobanteCompras();
}

function repararCabecera() {
    jQuery("fieldset.cabeceraCaso").each(function() {
        var fieldset = jQuery(this);
        var tabla = fieldset.children("table.lfr-table").eq(0);
        var fila = tabla.find("tr:first");
        var resumen = fila.children("td:last");
        var nuevaFila;

        fieldset.add(tabla).css({
            width: "100%",
            maxWidth: "100%",
            boxSizing: "border-box"
        });

        if (resumen.find(".divheaderNroReclamo,.divheaderNroOP").length &&
                !resumen.data("rpResumenReubicado")) {
            resumen.data("rpResumenReubicado", true);
            if (!limpio(resumen.text())) {
                resumen.hide();
            } else {
                nuevaFila = jQuery("<tr />");
                resumen.attr("colspan", fila.children("td").length).css({
                    width: "auto",
                    textAlign: "right",
                    whiteSpace: "normal"
                });
                nuevaFila.append(resumen);
                fila.after(nuevaFila);
            }
        }
    });
}

function repararListas() {
    c("global").css({
        width: "100%",
        maxWidth: "100%",
        boxSizing: "border-box"
    });

    c("lista_prestaciones_reclamos")
        .add(c("lista_prestaciones_asociadas"))
        .each(function() {
            var lista = jQuery(this);
            lista.css({
                width: "100%",
                maxWidth: "100%",
                overflowX: "auto",
                boxSizing: "border-box"
            });
            lista.find("table").first().css({
                minWidth: "1380px",
                tableLayout: "auto"
            });
        });
}

function normalizarVisual() {
    repararCabecera();
    repararListas();
    repararEditor();
}

window.ReclamoPrestacionalVisualPatch = {
    esCompras: esCompras,
    ocultarComprobanteCompras: ocultarComprobanteCompras,
    normalizarVisual: normalizarVisual
};

jQuery(document).ready(function() {
    normalizarVisual();
    window.setTimeout(normalizarVisual, 0);
    window.setTimeout(normalizarVisual, 100);
});
jQuery(document).ajaxComplete(function() {
    window.setTimeout(normalizarVisual, 0);
});

})(window, jQuery);
