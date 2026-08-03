(function(window, jQuery) {
"use strict";

if (!jQuery) {
    return;
}

var config = window.ReclamoPrestacionalViewConfig || {};
var namespace = config.namespace || "";
var urls = config.urls || {};
var MARCA_ENVUELTA = "__rpRecuperableNeutralV2";
var normalizacionInicialEjecutada = false;
var guardadoEnCurso = false;

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function diagnosticar(codigo, detalle) {
    if (window.console && window.console.warn) {
        window.console.warn(codigo + (detalle ? ": " + detalle : ""));
    }
}

function valorBooleano(valor) {
    return valor === true || String(valor || "").toLowerCase() === "true";
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
            selects[i].val("0");
            selects[i].attr("disabled", "disabled");
            selects[i].attr("aria-disabled", "true");

            var primeraOpcion = selects[i].find("option").eq(0);
            if (primeraOpcion.length) {
                primeraOpcion.attr("selected", "selected");
                primeraOpcion.text("SELECCIONE");
            }
        }
    }

    for (i = 0; i < reconocidos.length; i++) {
        if (reconocidos[i].length) {
            reconocidos[i].val("0");
            reconocidos[i].attr("readonly", "readonly");
        }
    }
}

function prepararValidacionLegacy() {
    var recuperableAlta = campo("recuperable_sur");
    var recuperableEdicion = campo("recuperable_surEdicion");

    if (recuperableAlta.length) {
        recuperableAlta.removeAttr("disabled").val("2");
    }
    if (recuperableEdicion.length) {
        recuperableEdicion.removeAttr("disabled").val("2");
    }

    campo("reconocidoSSS").val("0").attr("readonly", "readonly");
    campo("reconocidoSSSEdicion").val("0").attr("readonly", "readonly");
}

function esCargaPrestacion(url) {
    var texto = String(url || "");
    return texto.indexOf("editar_reclamosprestaciones") >= 0 ||
            texto.indexOf("lista_prestaciones_reclamos") >= 0;
}

function esGuardadoPrestacion(url, datos) {
    return String(url || "").indexOf("editar_reclamosprestaciones") >= 0 &&
            datos && typeof datos === "object" &&
            valorBooleano(datos.grabaedicion);
}

function asegurarMarcaEdicion(tipoAccion) {
    var marca = campo("tipoaccionprestacion");
    var idRegistro = campo("idRegistro").val();
    var formulario = campo("reclamo_fm");
    var destino;

    if (!marca.length) {
        marca = jQuery(document.createElement("input"));
        marca.attr("type", "hidden");
        marca.attr(
                "id",
                namespace + "tipoaccionprestacion"
        );
        marca.attr(
                "name",
                namespace + "tipoaccionprestacion"
        );

        destino = formulario.length ? formulario : campo("datos_edicion_prestacion");
        destino.append(marca);
    }

    marca.val(String(tipoAccion == null ? 0 : tipoAccion) + "-" +
            String(idRegistro || ""));

    return marca;
}

function normalizarListadoVisual() {
    var contenedor = campo("lista_prestaciones_reclamos");

    contenedor.find("table").each(function() {
        var tabla = jQuery(this);
        var indice = -1;

        tabla.find("th").each(function(posicion) {
            var texto = String(jQuery(this).text() || "")
                    .replace(/^\s+|\s+$/g, "")
                    .toUpperCase();
            if (texto === "RECUPERABLE") {
                indice = posicion;
            }
        });

        if (indice < 0) {
            return;
        }

        tabla.find("tbody tr").each(function() {
            var celdas = jQuery(this).children("td");
            if (celdas.length > indice) {
                celdas.eq(indice).html("");
            }
        });
    });
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

    guardadoEnCurso = false;
    forzarRecuperableSeleccione();
    normalizarListadoVisual();
    return false;
}

function ejecutarConReglaRecuperable(original, contexto, argumentos) {
    var loadAnterior = jQuery.fn.load;
    var solicitudIniciada = false;
    var resultado;

    prepararValidacionLegacy();

    jQuery.fn.load = function(url, datos, callback) {
        if (esCargaPrestacion(url) && datos && typeof datos === "object") {
            datos = jQuery.extend({}, datos);
            datos.recuperableSur = 0;
            datos.reconocidoSSS = 0;

            if (esGuardadoPrestacion(url, datos)) {
                solicitudIniciada = true;

                var loadNativo =
                        window.ReclamoPrestacionalJQueryLoadOriginal;
                if (typeof loadNativo === "function") {
                    var destino = this;
                    var callbackOriginal = callback;
                    var callbackSeguro = function(respuesta, estado, xhr) {
                        try {
                            if (typeof callbackOriginal === "function") {
                                callbackOriginal.call(
                                        destino.length ? destino[0] : destino,
                                        respuesta,
                                        estado,
                                        xhr
                                );
                            }
                        } finally {
                            guardadoEnCurso = false;
                            forzarRecuperableSeleccione();
                            normalizarListadoVisual();
                        }
                    };

                    return loadNativo.call(
                            destino,
                            url,
                            datos,
                            callbackSeguro
                    );
                }
            }
        }

        return loadAnterior.call(this, url, datos, callback);
    };

    try {
        resultado = original.apply(contexto, argumentos);
    } catch (error) {
        if (solicitudIniciada) {
            diagnosticar(
                    "RECLAMO_PRESTACIONAL_CANCELACION_LEGACY_RECUPERADA",
                    error && (error.message || error)
            );
            cancelarEdicionSeguro();
            resultado = false;
        } else {
            guardadoEnCurso = false;
            diagnosticar(
                    "RECLAMO_PRESTACIONAL_ACCION_PRESTACION_ERROR",
                    error && (error.stack || error.message || error)
            );
            alert("No se pudo completar la acción de la prestación.");
            resultado = false;
        }
    } finally {
        jQuery.fn.load = loadAnterior;
        window.setTimeout(function() {
            forzarRecuperableSeleccione();
            normalizarListadoVisual();
        }, 0);
    }

    if (!solicitudIniciada) {
        guardadoEnCurso = false;
    }

    return resultado;
}

function guardarEdicionSeguro(tipoAccion) {
    var original = guardarEdicionSeguro.__rpOriginal;

    if (guardadoEnCurso) {
        return false;
    }

    if (typeof original !== "function") {
        alert("No se encontró la acción de edición de la prestación.");
        return false;
    }

    guardadoEnCurso = true;
    asegurarMarcaEdicion(tipoAccion);

    return ejecutarConReglaRecuperable(
            original,
            window,
            [tipoAccion]
    );
}

function envolverAlta() {
    var nombreCompleto = namespace + "agregarPrestacion";
    var original = window[nombreCompleto];

    if (typeof original !== "function" || original[MARCA_ENVUELTA]) {
        return;
    }

    var envuelta = function() {
        return ejecutarConReglaRecuperable(original, this, arguments);
    };

    envuelta[MARCA_ENVUELTA] = true;
    envuelta.__rpOriginal = original;
    window[nombreCompleto] = envuelta;
}

function detectarTipoAccion() {
    if (campo("btnautoriza_prestacion").length) {
        return 1;
    }
    if (campo("btnrechaza_prestacion").length) {
        return 2;
    }
    return 0;
}

function instalarBotonesSeguros() {
    var editor = campo("datos_edicion_prestacion");
    var contenedor = campo("rp_botones_prestacion_seguros");
    var tipoAccion;
    var etiqueta;

    if (!editor.length || !editor.children().length) {
        return;
    }

    if (window.ReclamoPrestacionalEditorPatch &&
            typeof window.ReclamoPrestacionalEditorPatch
                    .limpiarResiduosEditor === "function") {
        window.ReclamoPrestacionalEditorPatch
                .limpiarResiduosEditor(editor);
    }

    tipoAccion = detectarTipoAccion();
    etiqueta = tipoAccion === 1 ? "Autorizar Prestación" :
            (tipoAccion === 2 ? "Rechazar Prestación" : "Editar Prestación");

    campo("botones_edicion_prestacion").hide();
    campo("btnedita_prestacion").hide();
    campo("btnautoriza_prestacion").hide();
    campo("btnrechaza_prestacion").hide();

    editor.find("input[type=button]").each(function() {
        var boton = jQuery(this);
        var id = String(boton.attr("id") || "");
        var valor = String(boton.val() || "");

        if (id !== namespace + "rp_cancelar_prestacion_seguro" &&
                valor.indexOf("Cancelar Edición") === 0) {
            boton.hide();
        }
    });

    if (!contenedor.length) {
        contenedor = jQuery(document.createElement("div"));
        contenedor.attr(
                "id",
                namespace + "rp_botones_prestacion_seguros"
        );
        contenedor.css({
            marginTop: "10px",
            marginBottom: "10px"
        });
        editor.append(contenedor);
    }

    contenedor.empty();

    var botonGuardar = jQuery(document.createElement("input"));
    botonGuardar.attr("type", "button");
    botonGuardar.attr(
            "id",
            namespace + "rp_guardar_prestacion_seguro"
    );
    botonGuardar.attr("value", etiqueta);
    botonGuardar.val(etiqueta);

    var botonCancelar = jQuery(document.createElement("input"));
    var textoCancelar =
            "Cancelar Edición de la Prestación";

    botonCancelar.attr("type", "button");
    botonCancelar.attr(
            "id",
            namespace + "rp_cancelar_prestacion_seguro"
    );
    botonCancelar.attr("value", textoCancelar);
    botonCancelar.val(textoCancelar);

    botonGuardar[0].onclick = function() {
        return guardarEdicionSeguro(tipoAccion);
    };
    botonCancelar[0].onclick = function() {
        return cancelarEdicionSeguro();
    };

    contenedor.append(botonGuardar);
    contenedor.append("\u00a0\u00a0");
    contenedor.append(botonCancelar);

    asegurarMarcaEdicion(tipoAccion);
}

function normalizarPrestacionInicialEnSesion(valorRecuperableInicial) {
    var editor = campo("datos_edicion_prestacion");
    var idRegistro = campo("idRegistro").val();
    var codigo = campo("codigoSeguimiento_filtro_edit").val();

    if (normalizacionInicialEjecutada || !editor.is(":visible") || !idRegistro) {
        return;
    }

    if (String(valorRecuperableInicial || "0") === "0") {
        return;
    }

    normalizacionInicialEjecutada = true;

    jQuery.ajax({
        url: urls.editarPrestaciones,
        type: "GET",
        dataType: "html",
        cache: false,
        data: {
            idRegistro: idRegistro,
            tipoEdicion: 0,
            codigoPrestacion: codigo,
            estadoAprobacion: 0
        },
        success: function() {
            diagnosticar(
                    "RECLAMO_PRESTACIONAL_RECUPERABLE_SESION_NEUTRALIZADO",
                    String(idRegistro)
            );
        },
        error: function(xhr, estado, error) {
            diagnosticar(
                    "RECLAMO_PRESTACIONAL_RECUPERABLE_SESION_ERROR",
                    error || estado
            );
        }
    });
}

function instalarReglas() {
    var nombreEditar = namespace + "editarPrestacionSeleccionada";
    var originalEditar = window[nombreEditar];
    var valorRecuperableInicial = campo("recuperable_surEdicion").val();

    if (typeof originalEditar === "function" &&
            originalEditar !== guardarEdicionSeguro &&
            !guardarEdicionSeguro.__rpOriginal) {
        guardarEdicionSeguro.__rpOriginal = originalEditar;
    }

    window[nombreEditar] = guardarEdicionSeguro;
    window[namespace + "cancelaEdicionPrestacion"] =
            cancelarEdicionSeguro;

    envolverAlta();
    normalizarPrestacionInicialEnSesion(valorRecuperableInicial);
    forzarRecuperableSeleccione();
    normalizarListadoVisual();
    instalarBotonesSeguros();
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
    guardarEdicionSeguro: guardarEdicionSeguro,
    forzarRecuperableSeleccione: forzarRecuperableSeleccione,
    normalizarListadoVisual: normalizarListadoVisual,
    instalarBotonesSeguros: instalarBotonesSeguros
};

})(window, window.jQuery);
