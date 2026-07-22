(function(window, jQuery) {
"use strict";

var config = window.ReclamoPrestacionalViewConfig || {};
var namespace = config.namespace || "";
var values = config.values || {};
var urls = config.urls || {};

var ESTADO_CERRADO = "3";
var GESTION_RECHAZADO = "5";
var submitEnCurso = false;

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function valor(sufijo) {
    var control = campo(sufijo);
    return control.length ? control.val() : "";
}

function asignar(sufijo, nuevoValor) {
    var control = campo(sufijo);
    if (control.length) {
        control.val(nuevoValor == null ? "" : nuevoValor);
    }
}

function fechaValida(anio, mes, dia) {
    var fecha = new Date(anio, mes, dia);
    return fecha.getFullYear() === anio &&
            fecha.getMonth() === mes &&
            fecha.getDate() === dia;
}

function inicioDelDia(fecha) {
    return new Date(
            fecha.getFullYear(),
            fecha.getMonth(),
            fecha.getDate()
    ).getTime();
}

function datosRevisionOkSeguro() {
    var diaRevision = parseInt(valor("fecharevisionDia"), 10);
    var mesRevision = parseInt(valor("fecharevisionMes"), 10);
    var anioRevision = parseInt(valor("fecharevisionAnio"), 10);
    var resolucion = campo("resolucion");

    if (isNaN(diaRevision) || isNaN(mesRevision) || isNaN(anioRevision)) {
        alert("Debe ingresar la fecha de Revisión");
        return false;
    }

    if (!fechaValida(anioRevision, mesRevision, diaRevision)) {
        alert("Error en la fecha de revisión ingresada.");
        return false;
    }

    if (!resolucion.length || resolucion.prop("selectedIndex") <= 0 || !resolucion.val()) {
        alert("Debe seleccionar el tipo de resolución de la lista.");
        return false;
    }

    var diaOspim = parseInt(valor("fechaospimDia"), 10);
    var mesOspim = parseInt(valor("fechaospimMes"), 10);
    var anioOspim = parseInt(valor("fechaospimAnio"), 10);

    if (isNaN(diaOspim) || isNaN(mesOspim) || isNaN(anioOspim) ||
            !fechaValida(anioOspim, mesOspim, diaOspim)) {
        alert("La fecha OSPIM del reclamo no es válida.");
        return false;
    }

    var fechaRevision = new Date(anioRevision, mesRevision, diaRevision);
    var fechaOspim = new Date(anioOspim, mesOspim, diaOspim);
    var hoy = new Date();

    if (inicioDelDia(fechaRevision) < inicioDelDia(fechaOspim)) {
        alert("La fecha de revisión no puede ser inferior a la fecha de ingreso del reclamo.");
        return false;
    }

    if (inicioDelDia(fechaRevision) > inicioDelDia(hoy)) {
        alert("La fecha de revisión no puede ser superior a la fecha de hoy.");
        return false;
    }

    return true;
}

function esResolucionRechazada(resolucion) {
    var normalizada = String(resolucion || "").toUpperCase();
    return normalizada !== "AUTORIZADO" && normalizada !== "AUTORIZADA";
}

function capturarEstadoCierre() {
    return {
        estado: valor("estado"),
        gestion: valor("tipo_gestion_cierre_reclamo"),
        gestionDisabled: campo("tipo_gestion_cierre_reclamo").prop("disabled"),
        tipogestion: valor("tipogestion"),
        observacionCierre: valor("reclamo_observacion_cierre"),
        revisionesActivas: valor("cantrevisionesactivas"),
        auditoriaAdministrativa: valor("auditoriaadministrativa"),
        chkAmparoDisabled: campo("chk_amparo").prop("disabled"),
        chkSuperintendenciaDisabled: campo("chk_superintendencia").prop("disabled"),
        chkRecuperableDisabled: campo("chk_recuperable").prop("disabled"),
        chkEnTramiteDisabled: campo("chk_entramite").prop("disabled")
    };
}

function restaurarEstadoCierre(anterior) {
    if (!anterior) {
        return;
    }

    asignar("estado", anterior.estado);
    asignar("tipo_gestion_cierre_reclamo", anterior.gestion);
    campo("tipo_gestion_cierre_reclamo").prop(
            "disabled",
            anterior.gestionDisabled
    );
    asignar("tipogestion", anterior.tipogestion);
    asignar("reclamo_observacion_cierre", anterior.observacionCierre);
    asignar("cantrevisionesactivas", anterior.revisionesActivas);
    asignar("auditoriaadministrativa", anterior.auditoriaAdministrativa);
    campo("chk_amparo").prop("disabled", anterior.chkAmparoDisabled);
    campo("chk_superintendencia").prop(
            "disabled",
            anterior.chkSuperintendenciaDisabled
    );
    campo("chk_recuperable").prop(
            "disabled",
            anterior.chkRecuperableDisabled
    );
    campo("chk_entramite").prop("disabled", anterior.chkEnTramiteDisabled);

    if (typeof window.controlarEstadoCerrado === "function") {
        window.controlarEstadoCerrado();
    }
}

function configurarCierreRechazado() {
    var estado = campo("estado");
    var gestion = campo("tipo_gestion_cierre_reclamo");

    estado.val(ESTADO_CERRADO);
    if (estado.val() !== ESTADO_CERRADO) {
        throw new Error("No existe el estado CERRADO (3) en la pantalla.");
    }

    gestion.prop("disabled", false).val(GESTION_RECHAZADO);
    if (gestion.val() !== GESTION_RECHAZADO) {
        throw new Error("No existe la gestión RECHAZADO (5) en la pantalla.");
    }

    asignar("tipogestion", GESTION_RECHAZADO);
    asignar("reclamo_observacion_cierre", "RECHAZO DE LA PRESTACION EN LA REVISION.");
    asignar("cantrevisionesactivas", "1");

    if (typeof window.controlarEstadoCerrado === "function") {
        window.controlarEstadoCerrado();
    }
    if (typeof window.seteaControlesFacturacionDirecta === "function") {
        window.seteaControlesFacturacionDirecta(true);
    }
    if (typeof window.desactivaCheckCierre === "function") {
        window.desactivaCheckCierre();
    }
}

function reiniciarFormularioRevision() {
    campo("resolucion").prop("selectedIndex", 0);
    campo("presentes").prop("selectedIndex", 0);
    campo("respresolucion").prop("selectedIndex", 0);
    campo("fecharevisionDia").prop("selectedIndex", 0);
    campo("fecharevisionMes").prop("selectedIndex", 0);
    campo("fecharevisionAnio").prop("selectedIndex", 0);
    asignar("observacion_revision", "");
}

function invocarGuardadoLuegoDeRevision() {
    var nombreFuncion = namespace + (values.hasReclamo ? "editaReclamo" : "saveReclamo");
    var funcion = window[nombreFuncion];

    if (typeof funcion !== "function") {
        throw new Error("No se encontró la función de guardado del reclamo.");
    }

    var confirmarOriginal = window.confirm;
    window.confirm = function(mensaje) {
        if (String(mensaje || "").indexOf(
                "Al seleccionar la opción RECHAZADO"
        ) === 0) {
            return true;
        }
        return confirmarOriginal.call(window, mensaje);
    };

    try {
        if (values.hasReclamo) {
            return funcion(false);
        }
        return funcion();
    } finally {
        window.confirm = confirmarOriginal;
    }
}

function alternarBotonRevision(deshabilitado) {
    var boton = campo("botonrevision");
    boton.prop("disabled", deshabilitado);
    boton.find("input,button,a").prop("disabled", deshabilitado);
}

function agregarRevisionSeguro() {
    if (!datosRevisionOkSeguro()) {
        return false;
    }

    var resolucion = valor("resolucion");
    var rechazo = esResolucionRechazada(resolucion);
    var estadoAnterior = capturarEstadoCierre();

    if (rechazo && !window.confirm(
            "Confirma el cierre del caso con el rechazo en la revisión?"
    )) {
        return false;
    }

    var params = {
        resolucion: resolucion,
        presentes: campo("presentes").prop("selectedIndex") === 0 ? "" : valor("presentes"),
        respresolucion: campo("respresolucion").prop("selectedIndex") === 0 ? "" : valor("respresolucion"),
        revisionFechaVtoDia: valor("fecharevisionDia"),
        revisionFechaVtoMes: valor("fecharevisionMes"),
        revisionFechaVtoAnio: valor("fecharevisionAnio"),
        reclamoobservacion: valor("observacion_revision"),
        observacionMedica: valor("observacion_medica"),
        chk_amparo: campo("chk_amparo").is(":checked"),
        chk_superintendencia: campo("chk_superintendencia").is(":checked"),
        chk_recuperable: campo("chk_recuperable").is(":checked"),
        chk_entramite: campo("chk_entramite").is(":checked")
    };

    asignar(
            "auditoriaadministrativa",
            campo("respresolucion").prop("selectedIndex") === 1 ? "Ok" : ""
    );

    if (rechazo) {
        try {
            configurarCierreRechazado();
        } catch (errorConfiguracion) {
            restaurarEstadoCierre(estadoAnterior);
            alert(errorConfiguracion.message);
            return false;
        }
    }

    alternarBotonRevision(true);
    campo("buscando").show();

    jQuery.ajax({
        url: urls.listaRevisiones,
        type: "POST",
        data: params,
        dataType: "html",
        cache: false
    }).done(function(html) {
        campo("lista_revisiones").html(html);
        campo("botonrevision").hide();
        campo("mensajerevisionefectuada").html(
                "Revisión efectuada. El sistema admite una sola revisión activa."
        );
        reiniciarFormularioRevision();

        if (rechazo) {
            try {
                var resultadoGuardado = invocarGuardadoLuegoDeRevision();
                if (resultadoGuardado === false) {
                    alert(
                            "La revisión fue registrada, pero el cierre del reclamo no se completó. " +
                            "Revise las validaciones y guarde nuevamente sin volver a crear la revisión."
                    );
                }
            } catch (errorGuardado) {
                alert(
                        "La revisión fue registrada, pero ocurrió un error al guardar el cierre: " +
                        errorGuardado.message
                );
            }
        }
    }).fail(function(xhr, estado, error) {
        restaurarEstadoCierre(estadoAnterior);
        alternarBotonRevision(false);
        alert(
                "No se pudo registrar la revisión. El reclamo no fue guardado ni cerrado. " +
                (error || estado || "Error de comunicación")
        );
    }).always(function() {
        campo("buscando").hide();
    });

    return false;
}

function inicializarEditorPrestacion() {
    var editor = campo("datos_edicion_prestacion");
    if (!editor.length || !editor.is(":visible")) {
        return;
    }

    var codigo = valor("codigoSeguimiento_filtro_edit");
    var buscar = window[namespace + "buscarNomencladorAutocompletar_edit"];

    if (codigo && typeof buscar === "function") {
        window.setTimeout(function() {
            buscar();
        }, 0);
        return;
    }

    var codigoTecnico = valor("codigoprestacion");
    if (codigoTecnico && campo("troquel_edit").length && !valor("troquel_edit")) {
        asignar("troquel_edit", codigoTecnico);
    }
}

function envolverSubmit(nombreFuncion) {
    var original = window[nombreFuncion];
    if (typeof original !== "function" || original.__rpP0Envuelta) {
        return;
    }

    var envuelta = function() {
        if (submitEnCurso) {
            return false;
        }

        submitEnCurso = true;
        try {
            return original.apply(this, arguments);
        } catch (error) {
            submitEnCurso = false;
            throw error;
        } finally {
            window.setTimeout(function() {
                submitEnCurso = false;
            }, 1500);
        }
    };

    envuelta.__rpP0Envuelta = true;
    window[nombreFuncion] = envuelta;
}

window.DatosRevisionOk = datosRevisionOkSeguro;
window[namespace + "agregarRevision"] = agregarRevisionSeguro;

envolverSubmit(namespace + "saveReclamo");
envolverSubmit(namespace + "editaReclamo");

jQuery(document).ready(function() {
    inicializarEditorPrestacion();
});

jQuery(document).ajaxComplete(function(evento, xhr, opciones) {
    var url = opciones && opciones.url ? String(opciones.url) : "";
    if (url.indexOf("editar_reclamosprestaciones") >= 0) {
        inicializarEditorPrestacion();
    }
});

})(window, jQuery);
