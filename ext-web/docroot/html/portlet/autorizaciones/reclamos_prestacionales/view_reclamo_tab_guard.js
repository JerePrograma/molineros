(function(window, document, jQuery) {
"use strict";

var config = window.ReclamoPrestacionalViewConfig || {};
var namespace = config.namespace || "";
var STORAGE_KEY = "molineros.reclamoPrestacional.activeEditor";
var SESSION_KEY = "molineros.reclamoPrestacional.draftId";
var HEARTBEAT_MS = 5000;
var LEASE_MS = 20000;
var draftId = obtenerDraftId();
var esPropietaria = false;
var heartbeat = null;

function ahora() {
    return new Date().getTime();
}

function generarId() {
    return "rp-" + ahora() + "-" +
            Math.floor(Math.random() * 1000000000);
}

function obtenerDraftId() {
    try {
        var existente = window.sessionStorage.getItem(SESSION_KEY);
        if (existente) {
            return existente;
        }
        var nuevo = generarId();
        window.sessionStorage.setItem(SESSION_KEY, nuevo);
        return nuevo;
    } catch (error) {
        return generarId();
    }
}

function leerLease() {
    try {
        var raw = window.localStorage.getItem(STORAGE_KEY);
        if (!raw) {
            return null;
        }
        var lease = JSON.parse(raw);
        if (!lease || !lease.draftId || !lease.timestamp) {
            return null;
        }
        return lease;
    } catch (error) {
        return null;
    }
}

function guardarLease() {
    var lease = {
        draftId: draftId,
        timestamp: ahora(),
        reclamoId: valor("id_reclamosel") || "0"
    };
    try {
        window.localStorage.setItem(STORAGE_KEY, JSON.stringify(lease));
        return true;
    } catch (error) {
        return false;
    }
}

function leaseVigente(lease) {
    return lease && ahora() - parseInt(lease.timestamp, 10) <= LEASE_MS;
}

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function valor(sufijo) {
    var control = campo(sufijo);
    return control.length ? control.val() : "";
}

function formulario() {
    return jQuery("#" + namespace + "reclamo_fm");
}

function asegurarDraftEnFormulario() {
    var form = formulario();
    if (!form.length) {
        return;
    }

    var input = campo("reclamoDraftId");
    if (!input.length) {
        input = jQuery("<input/>", {
            type: "hidden",
            id: namespace + "reclamoDraftId",
            name: namespace + "reclamoDraftId"
        }).appendTo(form);
    }
    input.val(draftId);
}

function banner() {
    var id = namespace + "reclamoTabGuard";
    var existente = jQuery("#" + id);
    if (existente.length) {
        return existente;
    }

    var contenedor = jQuery("<div/>", {
        id: id,
        class: "portlet-msg-error",
        css: { display: "none", marginBottom: "12px" }
    });
    var texto = jQuery("<span/>", {
        text: "Este editor está activo en otra pestaña. " +
                "Para evitar sobrescribir datos, esta pestaña quedó bloqueada."
    });
    var boton = jQuery("<button/>", {
        type: "button",
        text: "Tomar control en esta pestaña",
        css: { marginLeft: "12px" }
    });

    boton.bind("click", function(evento) {
        evento.preventDefault();
        tomarControl();
    });

    contenedor.append(texto).append(boton);
    formulario().before(contenedor);
    return contenedor;
}

function controlesEditables() {
    return formulario().find("input, select, textarea, button").not(
            "[type='hidden']"
    );
}

function bloquearControles() {
    controlesEditables().each(function() {
        var control = jQuery(this);
        if (control.data("rpTabGuardDisabled") === undefined) {
            control.data(
                    "rpTabGuardDisabled",
                    control.prop("disabled") === true
            );
        }
        control.prop("disabled", true);
    });
    banner().show();
}

function restaurarControles() {
    controlesEditables().each(function() {
        var control = jQuery(this);
        var originalmenteDeshabilitado =
                control.data("rpTabGuardDisabled") === true;
        control.prop("disabled", originalmenteDeshabilitado);
        control.removeData("rpTabGuardDisabled");
    });
    banner().hide();
}

function establecerPropiedad(propietaria) {
    esPropietaria = propietaria;
    if (propietaria) {
        restaurarControles();
    } else {
        bloquearControles();
    }
}

function tomarControl() {
    guardarLease();
    establecerPropiedad(true);
    iniciarHeartbeat();
}

function evaluarPropiedad() {
    var lease = leerLease();

    if (!leaseVigente(lease) || lease.draftId === draftId) {
        tomarControl();
        return;
    }

    establecerPropiedad(false);
}

function iniciarHeartbeat() {
    if (heartbeat !== null) {
        return;
    }
    heartbeat = window.setInterval(function() {
        if (!esPropietaria) {
            return;
        }
        guardarLease();
    }, HEARTBEAT_MS);
}

function liberarLease() {
    if (!esPropietaria) {
        return;
    }
    try {
        var lease = leerLease();
        if (lease && lease.draftId === draftId) {
            window.localStorage.removeItem(STORAGE_KEY);
        }
    } catch (error) {
        // La expiración temporal libera el lease aunque no pueda borrarse.
    }
}

function anexarDraftAjax(opciones) {
    if (!opciones || !opciones.url) {
        return;
    }

    var url = String(opciones.url);
    if (url.indexOf("autorizaciones") < 0 &&
            url.indexOf("reclamo") < 0) {
        return;
    }

    var nombre = namespace + "reclamoDraftId";
    if (typeof opciones.data === "string") {
        if (opciones.data.indexOf(nombre + "=") < 0) {
            opciones.data += (opciones.data ? "&" : "") +
                    encodeURIComponent(nombre) + "=" +
                    encodeURIComponent(draftId);
        }
    } else {
        opciones.data = opciones.data || {};
        if (opciones.data[nombre] == null) {
            opciones.data[nombre] = draftId;
        }
    }
}

jQuery.ajaxPrefilter(function(opciones) {
    anexarDraftAjax(opciones);
});

jQuery(document).ready(function() {
    asegurarDraftEnFormulario();
    evaluarPropiedad();
    iniciarHeartbeat();
});

jQuery(document).ajaxComplete(function() {
    asegurarDraftEnFormulario();
    if (!esPropietaria) {
        bloquearControles();
    }
});

if (window.addEventListener) {
    window.addEventListener("storage", function(evento) {
        if (evento.key === STORAGE_KEY) {
            evaluarPropiedad();
        }
    }, false);
    window.addEventListener("beforeunload", liberarLease, false);
} else if (window.attachEvent) {
    window.attachEvent("onbeforeunload", liberarLease);
}

window.ReclamoPrestacionalTabGuard = {
    getDraftId: function() {
        return draftId;
    },
    isOwner: function() {
        return esPropietaria;
    },
    takeControl: tomarControl,
    release: liberarLease
};

})(window, document, jQuery);
