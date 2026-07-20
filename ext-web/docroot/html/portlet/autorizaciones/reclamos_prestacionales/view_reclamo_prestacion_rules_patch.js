(function(window, jQuery) {
"use strict";

if (!jQuery) {
    return;
}

var config = window.ReclamoPrestacionalViewConfig || {};
var namespace = config.namespace || "";
var urls = config.urls || {};
var MARCA_ENVUELTA = "__rpRecuperableNeutral";

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function forzarRecuperableSeleccione() {
    var selects = [
        campo("recuperable_sur"),
        campo("recuperable_surEdicion")
    ];
    var reconocidos = [
        campo("reconocidoSSS"),
        campo("reconocidoSSSEdicion")
    ];
    var i;

    for (i = 0; i < selects.length; i++) {
        if (selects[i].length) {
            selects[i]
                    .val("0")
                    .attr("disabled", "disabled")
                    .attr("aria-disabled", "true");
        }
    }

    for (i = 0; i < reconocidos.length; i++) {
        if (reconocidos[i].length) {
            reconocidos[i]
                    .val("0")
                    .attr("readonly", "readonly");
        }
    }
}

function prepararValidacionLegacy() {
    /*
     * El código legacy considera obligatorio Recuperable cuando hay importes
     * del área médica. Se usa temporalmente NO Recuperable únicamente para
     * atravesar esa validación antigua. El request real se normaliza a 0.
     */
    var recuperableAlta = campo("recuperable_sur");
    var recuperableEdicion = campo("recuperable_surEdicion");

    if (recuperableAlta.length) {
        recuperableAlta.val("2");
    }
    if (recuperableEdicion.length) {
        recuperableEdicion.val("2");
    }

    campo("reconocidoSSS").val("0");
    campo("reconocidoSSSEdicion").val("0");
}

function esCargaPrestacion(url) {
    var texto = String(url || "");
    return texto.indexOf("editar_reclamosprestaciones") >= 0 ||
            texto.indexOf("lista_prestaciones_reclamos") >= 0;
}

function ejecutarConReglaRecuperable(original, contexto, argumentos) {
    var loadOriginal = jQuery.fn.load;
    var resultado;

    prepararValidacionLegacy();

    jQuery.fn.load = function(url, datos, callback) {
        if (esCargaPrestacion(url) && datos && typeof datos === "object") {
            datos.recuperableSur = 0;
            datos.reconocidoSSS = 0;
        }
        return loadOriginal.apply(this, arguments);
    };

    try {
        resultado = original.apply(contexto, argumentos);
    } catch (error) {
        var detalle = String(
                error && (error.stack || error.message) ?
                        (error.stack || error.message) : error || ""
        );

        if (detalle.indexOf("cancelaEdicionPrestacion") >= 0 ||
                detalle.indexOf("selectedIndex") >= 0) {
            cancelarEdicionSeguro();
            resultado = false;
        } else {
            throw error;
        }
    } finally {
        jQuery.fn.load = loadOriginal;
        window.setTimeout(forzarRecuperableSeleccione, 0);
    }

    return resultado;
}

function prepararMarcaEdicion() {
    var marca = campo("tipoaccionprestacion");
    var idRegistro = campo("idRegistro").val();
    var valor = String(marca.val() || "");

    if (marca.length && idRegistro && valor.indexOf("-") < 0) {
        marca.val("0-" + idRegistro);
    }
}

function cancelarEdicionSeguro() {
    var marca = campo("tipoaccionprestacion");
    var valor = String(marca.val() || "");
    var partes = valor.split("-");
    var idPrestacion = partes.length > 1 ? partes[1] : "";
    var combo = idPrestacion ?
            document.getElementById("comboestadosreclamo" + idPrestacion) :
            null;

    campo("datos_edicion_prestacion").hide();

    if (typeof window.manejarTipoSector === "function") {
        window.manejarTipoSector();
    }

    campo("datos_prestacion_ingreso").show();

    var limpiar = window[namespace + "limpiarNomencladorAutocompletar"];
    if (typeof limpiar === "function") {
        limpiar();
    }

    if (typeof window.onOffcombosestadosprestaciones === "function") {
        window.onOffcombosestadosprestaciones(true);
    }

    if (combo) {
        combo.selectedIndex = 0;
    }

    if (marca.length) {
        marca.val("0");
    }

    forzarRecuperableSeleccione();
    return false;
}

function envolverAccion(nombre, preparar) {
    var nombreCompleto = namespace + nombre;
    var original = window[nombreCompleto];

    if (typeof original !== "function" || original[MARCA_ENVUELTA]) {
        return;
    }

    var envuelta = function() {
        if (typeof preparar === "function") {
            preparar();
        }
        return ejecutarConReglaRecuperable(original, this, arguments);
    };

    envuelta[MARCA_ENVUELTA] = true;
    envuelta.__rpOriginal = original;
    window[nombreCompleto] = envuelta;
}

function instalarReglas() {
    envolverAccion("editarPrestacionSeleccionada", prepararMarcaEdicion);
    envolverAccion("agregarPrestacion");

    window[namespace + "cancelaEdicionPrestacion"] =
            cancelarEdicionSeguro;

    forzarRecuperableSeleccione();
}

instalarReglas();

jQuery(document).ready(instalarReglas);

jQuery(document).ajaxComplete(function(evento, xhr, opciones) {
    var url = opciones && opciones.url ? String(opciones.url) : "";

    if (esCargaPrestacion(url) ||
            url.indexOf(String(urls.editarPrestaciones || "")) >= 0 ||
            url.indexOf(String(urls.listaPrestaciones || "")) >= 0) {
        instalarReglas();
    }
});

window.ReclamoPrestacionalPrestacionRulesPatch = {
    cancelarEdicionSeguro: cancelarEdicionSeguro,
    forzarRecuperableSeleccione: forzarRecuperableSeleccione
};

})(window, window.jQuery);