(function(window, jQuery) {
"use strict";

var configDisponible = !!window.ReclamoPrestacionalViewConfig;
var config = window.ReclamoPrestacionalViewConfig || {};
config.values = jQuery.extend({
    cantPrestaciones: 0,
    casoVinculado: "0",
    hasReclamo: false,
    reclamoCerrado: false,
    tipoGestionCierre: 0,
    idObservacionMedica: 0,
    tieneResolucion: false,
    esEdicion: false,
    esAlta: false,
    esBorradorCompras: false,
    idReclamo: 0,
    cantRevisiones: 0,
    debitoTercerizadora: false,
    codigoCie10Presente: false,
    caiNamespace: false
}, config.values || {});
config.urls = config.urls || {};
config.messages = config.messages || {};
config.namespace = config.namespace || window.ReclamoPrestacionalNamespace || "";
window.ReclamoPrestacionalViewConfig = config;

var namespace = config.namespace;

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function ocultarBuscadoresPrestacion() {
    campo("busqueda_prestaciones").hide();
    campo("busqueda_farmacia").hide();
    campo("nom_seleccionado").val("0");
}

function mostrarBuscadorSegunSeleccion() {
    if (config.values.esBorradorCompras) {
        ocultarBuscadoresPrestacion();
        return;
    }

    var sector = campo("sector").val();
    var tipoPedido = campo("tipopedido").val();
    var usaBuscadorFarmacia =
            sector === "FARMACIA" && tipoPedido !== "EXCEPCION";

    if (usaBuscadorFarmacia) {
        campo("busqueda_farmacia").show();
        campo("busqueda_prestaciones").hide();
        campo("nom_seleccionado").val("2");
        return;
    }

    /*
     * Comportamiento legacy: Nuevo comienza con Código Presentado visible,
     * incluso mientras el sector permanece en -- SELECCIONAR --.
     */
    campo("busqueda_prestaciones").show();
    campo("busqueda_farmacia").hide();
    campo("nom_seleccionado").val("1");
}

function aplicarEstadoInicial() {
    campo("divResultadoActualizarOK").hide();
    campo("lista_prestaciones_asociadas").hide();
    campo("lista_contactos_reclamo").hide();

    if (config.values.esBorradorCompras) {
        campo("datos_prestacion_ingreso").hide();
        campo("datos_edicion_prestacion").show();
    } else {
        campo("datos_edicion_prestacion").hide();
        campo("datos_prestacion_ingreso").show();
    }

    campo("Cierre_Reclamo_Div").toggle(
            !!config.values.reclamoCerrado
    );
    mostrarBuscadorSegunSeleccion();
}

if (!configDisponible && window.console && window.console.error) {
    window.console.error("RECLAMO_PRESTACIONAL_CONFIG_AUSENTE");
}

aplicarEstadoInicial();

jQuery(function() {
    /* Se ejecuta después de los ready handlers legacy. */
    window.setTimeout(aplicarEstadoInicial, 0);
});

function actualizarBuscadorPrestacion() {
    mostrarBuscadorSegunSeleccion();
}

/* Compatible con el jQuery legacy incluido por Liferay 5.2. */
campo("sector").change(actualizarBuscadorPrestacion);
campo("tipopedido").change(actualizarBuscadorPrestacion);
window[namespace + "actualizarBuscadorPrestacion"] =
        actualizarBuscadorPrestacion;

window.ReclamoPrestacionalInitialStateOk = true;
})(window, jQuery);
