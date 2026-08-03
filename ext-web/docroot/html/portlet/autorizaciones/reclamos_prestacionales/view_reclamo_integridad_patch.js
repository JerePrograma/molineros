(function(window, document, jQuery) {
"use strict";

if (!jQuery) {
    return;
}

var config = window.ReclamoPrestacionalViewConfig || {};
var namespace = config.namespace || "";
var values = config.values || {};
var urls = config.urls || {};
var ENDPOINT_EDITOR = "editar_reclamosprestaciones";
var revisionEnCurso = false;
var metadatosEditor = null;

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function booleano(valor) {
    return valor === true || String(valor || "").toLowerCase() === "true";
}

function entero(valor) {
    var numero = parseInt(valor, 10);
    return isNaN(numero) ? 0 : numero;
}

function terminaCon(valor, sufijo) {
    var actual = String(valor || "");
    return actual.length >= sufijo.length &&
            actual.substring(actual.length - sufijo.length) === sufijo;
}

function elementoPorSufijo(contenedor, sufijo) {
    var elementos = contenedor.getElementsByTagName("*");
    var i;

    for (i = 0; i < elementos.length; i++) {
        if (terminaCon(elementos[i].id, sufijo) ||
                terminaCon(elementos[i].name, sufijo)) {
            return elementos[i];
        }
    }

    return null;
}

function valorElemento(contenedor, sufijo) {
    var elemento = elementoPorSufijo(contenedor, sufijo);
    return elemento ? String(elemento.value || elemento.getAttribute("value") || "") : "";
}

function extraerMetadatosEditor(respuesta) {
    var html = String(respuesta || "");
    var contenedor = document.createElement("div");
    var letra = html.match(/comprobante_letra_edicion[\s\S]{0,600}?\.val\(\s*["']([^"']*)["']\s*\)/i);

    contenedor.innerHTML = html;

    return {
        tipo: valorElemento(contenedor, "comprobante_tipo_edicion"),
        letra: letra ? letra[1] : "",
        sucursal: valorElemento(contenedor, "comprobante_suc_edicion"),
        numero: valorElemento(contenedor, "comprobante_nro_edicion"),
        cuit: valorElemento(contenedor, "cuit_entidad_edicion"),
        sucursalEntidad: valorElemento(contenedor, "sucursal_entidad_edicion"),
        razonSocial: valorElemento(contenedor, "entidad_edicion"),
        cantidad: valorElemento(contenedor, "cantidadFC_edicion"),
        importeUnitario: valorElemento(contenedor, "importeUnitarioFC_edicion"),
        importeTotal: valorElemento(contenedor, "importeFC_edicion")
    };
}

function instalarCapturaEditor() {
    var ajaxOriginal = jQuery.ajax;
    var ajaxSeguro;

    if (typeof ajaxOriginal !== "function" || ajaxOriginal.__rpIntegridadEditor) {
        return;
    }

    ajaxSeguro = function(opciones) {
        var copia;
        var successOriginal;

        if (arguments.length === 1 && opciones && typeof opciones === "object" &&
                String(opciones.url || "").indexOf(ENDPOINT_EDITOR) >= 0) {
            copia = jQuery.extend({}, opciones);
            successOriginal = copia.success;
            copia.success = function(respuesta, estado, xhr) {
                metadatosEditor = extraerMetadatosEditor(respuesta);
                if (typeof successOriginal === "function") {
                    return successOriginal.call(this, respuesta, estado, xhr);
                }
            };
            return ajaxOriginal.call(this, copia);
        }

        return ajaxOriginal.apply(this, arguments);
    };

    ajaxSeguro.__rpIntegridadEditor = true;
    ajaxSeguro.__rpIntegridadEditorOriginal = ajaxOriginal;
    jQuery.ajax = ajaxSeguro;
}

function restaurarValor(sufijo, valor) {
    var control = campo(sufijo);
    if (control.length && valor != null && String(valor) !== "") {
        control.val(valor);
    }
}

function repararComprobanteEdicion() {
    var editor = campo("datos_edicion_prestacion");
    var comprobante;
    var letra;

    if (!editor.children().length || !campo("idRegistro").length) {
        return;
    }

    comprobante = campo("datos_comprobante");
    comprobante.show().attr("aria-hidden", "false");
    comprobante.find("table,tr,td").show();
    campo("rp_compras_comprobante_info").remove();
    jQuery("#divEntidad_edicion").show();
    campo("divBtnBuscaEntidad_edicion").show();

    if (!metadatosEditor) {
        return;
    }

    restaurarValor("comprobante_tipo_edicion", metadatosEditor.tipo);
    restaurarValor("comprobante_suc_edicion", metadatosEditor.sucursal);
    restaurarValor("comprobante_nro_edicion", metadatosEditor.numero);
    restaurarValor("cuit_entidad_edicion", metadatosEditor.cuit);
    restaurarValor("sucursal_entidad_edicion", metadatosEditor.sucursalEntidad);
    restaurarValor("entidad_edicion", metadatosEditor.razonSocial);
    restaurarValor("cantidadFC_edicion", metadatosEditor.cantidad);
    restaurarValor("importeUnitarioFC_edicion", metadatosEditor.importeUnitario);
    restaurarValor("importeFC_edicion", metadatosEditor.importeTotal);

    letra = campo("comprobante_letra_edicion");
    if (letra.length && letra.children("option").length && metadatosEditor.letra) {
        letra.val(metadatosEditor.letra);
    }
}

function programarReparacionComprobante() {
    window.setTimeout(repararComprobanteEdicion, 0);
    window.setTimeout(repararComprobanteEdicion, 80);
    window.setTimeout(repararComprobanteEdicion, 220);
}

function puedeGestionarRevisiones() {
    return booleano(values.hasReclamo) && booleano(values.esEdicion) &&
            !booleano(values.reclamoCerrado) && !booleano(values.tieneResolucion);
}

function cantidadRevisionesActivas() {
    var control = campo("cantrevisionesactivas");
    var valor = control.length ? String(control.val() || "") : "";

    return valor !== "" ? entero(valor) : entero(values.cantRevisiones);
}

function hayRevisionActivaEnListado() {
    var activa = false;
    campo("lista_revisiones").find("img").each(function() {
        if (String(jQuery(this).attr("onclick") || "").indexOf("borrarRevision") >= 0) {
            activa = true;
        }
    });
    return activa;
}

function actualizarRevision(hayActiva, mensaje) {
    var boton = campo("botonrevision");
    var leyenda = campo("mensajerevisionefectuada");

    if (puedeGestionarRevisiones() && !hayActiva) {
        boton.show();
    } else {
        boton.hide();
    }

    if (mensaje) {
        leyenda.text(mensaje);
    } else if (hayActiva) {
        leyenda.text("Revision efectuada. El sistema admite una sola revision activa.");
    } else {
        leyenda.text("");
    }
}

function fechaValida(anio, mes, dia) {
    var fecha = new Date(anio, mes, dia);
    return fecha.getFullYear() === anio && fecha.getMonth() === mes && fecha.getDate() === dia;
}

function datosRevisionOk() {
    var dia = entero(campo("fecharevisionDia").val());
    var mes = entero(campo("fecharevisionMes").val());
    var anio = entero(campo("fecharevisionAnio").val());
    var diaOspim = entero(campo("fechaospimDia").val());
    var mesOspim = entero(campo("fechaospimMes").val());
    var anioOspim = entero(campo("fechaospimAnio").val());
    var fechaRevision;
    var fechaOspim;
    var hoy;

    if (!dia || anio < 1900 || !fechaValida(anio, mes, dia)) {
        alert("Debe ingresar una fecha de revision valida.");
        return false;
    }
    if (!campo("resolucion").length || campo("resolucion")[0].selectedIndex === 0) {
        alert("Debe seleccionar el tipo de resolucion de la lista.");
        return false;
    }
    if (!diaOspim || anioOspim < 1900 || !fechaValida(anioOspim, mesOspim, diaOspim)) {
        alert("La fecha OSPIM del reclamo no es valida.");
        return false;
    }

    fechaRevision = new Date(anio, mes, dia);
    fechaOspim = new Date(anioOspim, mesOspim, diaOspim);
    hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    if (fechaRevision.getTime() < fechaOspim.getTime()) {
        alert("La fecha de revision no puede ser inferior a la fecha de ingreso del reclamo.");
        return false;
    }
    if (fechaRevision.getTime() > hoy.getTime()) {
        alert("La fecha de revision no puede ser superior a la fecha de hoy.");
        return false;
    }

    return true;
}

function agregarRevisionSeguro() {
    var params;
    var boton;
    var lista;

    if (revisionEnCurso || !puedeGestionarRevisiones() ||
            cantidadRevisionesActivas() > 0 || hayRevisionActivaEnListado()) {
        actualizarRevision(true);
        return false;
    }
    if (!datosRevisionOk()) {
        return false;
    }

    params = {
        resolucion: campo("resolucion").val(),
        presentes: campo("presentes").val(),
        respresolucion: campo("respresolucion").val(),
        revisionFechaVtoDia: campo("fecharevisionDia").val(),
        revisionFechaVtoMes: campo("fecharevisionMes").val(),
        revisionFechaVtoAnio: campo("fecharevisionAnio").val(),
        reclamoobservacion: campo("observacion_revision").val(),
        observacionMedica: campo("observacion_medica").val()
    };

    boton = campo("botonrevision").find("input[type='button']");
    lista = campo("lista_revisiones");
    revisionEnCurso = true;
    boton.attr("disabled", "disabled");

    lista.load(urls.listaRevisiones, params, function(respuesta, estado) {
        var errorVisible = lista.find(".portlet-msg-error").length > 0;
        var activa;

        revisionEnCurso = false;
        boton.removeAttr("disabled");
        campo("buscando").hide();

        if (estado === "error" || errorVisible) {
            actualizarRevision(false, "No se pudo agregar la revision. Revise los datos.");
            return;
        }

        activa = hayRevisionActivaEnListado();
        campo("cantrevisionesactivas").val(activa ? "1" : "0");
        actualizarRevision(activa, activa ? "" : "La revision no fue incorporada.");
    });

    return false;
}

function abrirAyudaComprobantes(evento) {
    var panel = campo("helpComprobantes");
    var y = evento && evento.clientY != null ? evento.clientY : 120;

    if (!panel.length) {
        return false;
    }

    panel.css({top: String(y + (jQuery(document).scrollTop() || 0) + 10) + "px", left: "300px"});
    panel.show().attr("aria-hidden", "false");
    return false;
}

function cerrarAyudaComprobantes() {
    campo("helpComprobantes").hide().attr("aria-hidden", "true");
    return false;
}

function instalar() {
    var helpOriginal = window.help;

    window[namespace + "agregarRevision"] = agregarRevisionSeguro;
    window[namespace + "cerrarAyudaComprobantes"] = cerrarAyudaComprobantes;
    window.help = function(evento, id) {
        if (id === "helpComprobantes") {
            return abrirAyudaComprobantes(evento);
        }
        return typeof helpOriginal === "function" ? helpOriginal.apply(this, arguments) : false;
    };

    actualizarRevision(cantidadRevisionesActivas() > 0 || hayRevisionActivaEnListado());
    programarReparacionComprobante();
}

instalarCapturaEditor();
jQuery(instalar);
jQuery(document).ajaxComplete(function(evento, xhr, opciones) {
    var url = opciones && opciones.url ? String(opciones.url) : "";
    if (url.indexOf(ENDPOINT_EDITOR) >= 0 || url.indexOf("filtrarLetraComprobante") >= 0) {
        programarReparacionComprobante();
    }
    if (url.indexOf("lista_revisiones_reclamo") >= 0 || url.indexOf("borrar_reclamosrevisiones") >= 0) {
        window.setTimeout(function() {
            var activa = hayRevisionActivaEnListado();
            campo("cantrevisionesactivas").val(activa ? "1" : "0");
            actualizarRevision(activa);
        }, 0);
    }
});

window.ReclamoPrestacionalIntegridadPatch = {
    actualizarRevision: actualizarRevision,
    repararComprobanteEdicion: repararComprobanteEdicion,
    abrirAyudaComprobantes: abrirAyudaComprobantes
};

})(window, document, window.jQuery);
