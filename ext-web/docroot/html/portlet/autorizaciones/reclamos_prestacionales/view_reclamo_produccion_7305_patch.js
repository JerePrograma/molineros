(function(window, jQuery) {
"use strict";

if (!jQuery) {
    return;
}

var config = window.ReclamoPrestacionalViewConfig || {};
var namespace = config.namespace || "";
var MARCA_ENVUELTA = "__rpProduccion7305Envuelta";
var ultimoPlanAfiliadoDetectado = null;
var intervaloPlanAfiliado = null;

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function valor(sufijo) {
    var control = campo(sufijo);
    return control.length ? control.val() : "";
}

function asegurarCampoOculto(sufijo, valorInicial) {
    var control = campo(sufijo);
    var formulario;

    if (control.length) {
        return control;
    }

    formulario = campo("reclamo_fm");
    if (!formulario.length) {
        return control;
    }

    control = jQuery("<input />")
            .attr("type", "hidden")
            .attr("id", namespace + sufijo)
            .attr("name", namespace + sufijo)
            .val(valorInicial == null ? "" : valorInicial);

    formulario.append(control);
    return control;
}

function asegurarCamposPlan() {
    asegurarCampoOculto("plan_reclamo_bloqueado", "0");
    asegurarCampoOculto("nombre_plan_reclamo_bloqueado", "");
}

function normalizarNombrePlan(nombrePlan) {
    if (nombrePlan == null) {
        return "";
    }

    return String(nombrePlan)
            .toUpperCase()
            .replace(/^\s+|\s+$/g, "")
            .replace(/\s+/g, " ");
}

function esPlanBloqueadoParaReclamo(nombrePlan) {
    var planNormalizado = normalizarNombrePlan(nombrePlan);

    return planNormalizado === "COBERTURA" ||
            planNormalizado === "COBERTURA TOTAL O" ||
            planNormalizado === "COBERTURA TOTAL M";
}

function validarPlanParaReclamo(nombrePlan, mostrarMensaje) {
    var bloqueado = esPlanBloqueadoParaReclamo(nombrePlan);

    asegurarCamposPlan();
    campo("plan_reclamo_bloqueado").val(bloqueado ? "1" : "0");
    campo("nombre_plan_reclamo_bloqueado").val(nombrePlan || "");

    if (!bloqueado) {
        return true;
    }

    if (mostrarMensaje) {
        window.alert(
                'Afiliado con plan "' + nombrePlan +
                '" no puede cargar un reclamo.'
        );
    }

    return false;
}

function verificarPlanAfiliadoDelReclamo() {
    var campoPlan = campo("plan");
    var nombrePlan;

    if (!campoPlan.length) {
        return;
    }

    nombrePlan = campoPlan.val();
    if (nombrePlan == null) {
        nombrePlan = "";
    }
    nombrePlan = String(nombrePlan);

    if (nombrePlan === ultimoPlanAfiliadoDetectado) {
        return;
    }

    ultimoPlanAfiliadoDetectado = nombrePlan;
    validarPlanParaReclamo(nombrePlan, true);
}

function bloquearPorPlan(mensajeSolapa) {
    var nombrePlan = valor("plan");

    validarPlanParaReclamo(nombrePlan, false);
    if (valor("plan_reclamo_bloqueado") !== "1") {
        return false;
    }

    nombrePlan = valor("nombre_plan_reclamo_bloqueado");
    if (mensajeSolapa) {
        window.alert(
                'No se permite cargar un reclamo para el afiliado con plan "' +
                nombrePlan + '".'
        );
    } else {
        window.alert(
                'Afiliado con plan "' + nombrePlan +
                '" no puede cargar un reclamo.'
        );
    }

    return true;
}

function leerFecha(diaSufijo, mesSufijo, anioSufijo) {
    var diaTexto = valor(diaSufijo);
    var mesTexto = valor(mesSufijo);
    var anioTexto = valor(anioSufijo);
    var dia;
    var mes;
    var anio;

    if (diaTexto == null || diaTexto === "" || diaTexto === "0" ||
            mesTexto == null || mesTexto === "" || mesTexto === "-1" ||
            anioTexto == null || anioTexto === "" || anioTexto === "0") {
        return null;
    }

    dia = parseInt(diaTexto, 10);
    mes = parseInt(mesTexto, 10);
    anio = parseInt(anioTexto, 10);

    if (isNaN(dia) || isNaN(mes) || isNaN(anio)) {
        return null;
    }

    return {
        anio: anio,
        mes: mes,
        dia: dia
    };
}

function fechaPosterior(primera, segunda) {
    if (!primera || !segunda) {
        return false;
    }

    if (primera.anio !== segunda.anio) {
        return primera.anio > segunda.anio;
    }
    if (primera.mes !== segunda.mes) {
        return primera.mes > segunda.mes;
    }
    return primera.dia > segunda.dia;
}

function fechaPrestacionAlta() {
    var fecha = leerFecha(
            "fechaPrestacionDia",
            "fechaPrestacionMes",
            "fechaPrestacionAnio"
    );

    if (fecha) {
        return fecha;
    }

    return leerFecha(
            "fechaPrestacionDiaFarmacia",
            "fechaPrestacionMesFarmacia",
            "fechaPrestacionAnioFarmacia"
    );
}

function validarFechaPrestacionAlta() {
    var fechaPrestacion = fechaPrestacionAlta();
    var fechaEmision = leerFecha(
            "fechaComprobanteDia",
            "fechaComprobanteMes",
            "fechaComprobanteAnio"
    );

    if (!fechaPosterior(fechaPrestacion, fechaEmision)) {
        return true;
    }

    window.alert(
            "La fecha de prestación no puede ser posterior a la fecha de emisión"
    );
    return false;
}

function validarFechaPrestacionEdicion() {
    var fechaPrestacion = leerFecha(
            "fechaPrestacionDiaEdicion",
            "fechaPrestacionMesEdicion",
            "fechaPrestacionAnioEdicion"
    );
    var fechaEmision = leerFecha(
            "fechaComprobanteDiaEdicion",
            "fechaComprobanteMesEdicion",
            "fechaComprobanteAnioEdicion"
    );

    if (!fechaPosterior(fechaPrestacion, fechaEmision)) {
        return true;
    }

    window.alert(
            "La fecha de prestación no puede ser posterior a la fecha de emisión"
    );
    return false;
}

function envolver(nombreFuncion, validacion) {
    var original = window[nombreFuncion];
    var envuelta;

    if (typeof original !== "function" || original[MARCA_ENVUELTA]) {
        return;
    }

    envuelta = function() {
        if (!validacion.apply(this, arguments)) {
            return false;
        }
        return original.apply(this, arguments);
    };
    envuelta[MARCA_ENVUELTA] = true;
    envuelta.__rpProduccion7305Original = original;
    window[nombreFuncion] = envuelta;
}

function instalarGuardas() {
    envolver(namespace + "saveReclamo", function() {
        return !bloquearPorPlan(false);
    });
    envolver(namespace + "editaReclamo", function() {
        return !bloquearPorPlan(false);
    });
    envolver(namespace + "siguienteSolapa", function() {
        return !bloquearPorPlan(true);
    });
    envolver(namespace + "agregarPrestacion", validarFechaPrestacionAlta);
    envolver(
            namespace + "editarPrestacionSeleccionada",
            validarFechaPrestacionEdicion
    );
}

function inicializar() {
    asegurarCamposPlan();
    instalarGuardas();
    verificarPlanAfiliadoDelReclamo();

    if (intervaloPlanAfiliado == null) {
        intervaloPlanAfiliado = window.setInterval(
                verificarPlanAfiliadoDelReclamo,
                500
        );
    }
}

window.normalizarNombrePlan = normalizarNombrePlan;
window.esPlanBloqueadoParaReclamo = esPlanBloqueadoParaReclamo;
window[namespace + "validarPlanParaReclamo"] = validarPlanParaReclamo;

jQuery(document).ready(inicializar);
jQuery(document).ajaxComplete(function() {
    instalarGuardas();
    verificarPlanAfiliadoDelReclamo();
});

})(window, jQuery);
