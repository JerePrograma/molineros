<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto" %>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include
	file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<%
/*
 * Normaliza exclusivamente el handoff valido Compras -> Reclamo
 * Prestacional antes de que el ensamblado legacy resuelva el modo.
 *
 * En una navegacion cross-portlet, el parametro cmd=add puede convivir con
 * un atributo Constants.CMD distinto. El contexto validado por nonce,
 * usuario y vigencia es la evidencia necesaria para mantener este flujo en
 * alta y permitir que la botonera renderice Grabar.
 */
String cmdParametroCompras = ParamUtil.getString(
        request,
        Constants.CMD,
        ""
);
String nonceReclamoCompras = ParamUtil.getString(
        request,
        WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
        ""
);
Object contextoReclamoComprasObj = request.getSession().getAttribute(
        WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
);
ReclamoPrestacionalCompraContexto contextoReclamoCompras =
        contextoReclamoComprasObj instanceof ReclamoPrestacionalCompraContexto
        ? (ReclamoPrestacionalCompraContexto) contextoReclamoComprasObj
        : null;

boolean handoffReclamoComprasValido =
        Constants.ADD.equalsIgnoreCase(cmdParametroCompras)
        && contextoReclamoCompras != null
        && Validator.isNotNull(nonceReclamoCompras)
        && contextoReclamoCompras.coincideNonce(nonceReclamoCompras)
        && contextoReclamoCompras.perteneceAUsuario(
                user != null ? user.getScreenName() : ""
        )
        && contextoReclamoCompras.estaVigente(
                System.currentTimeMillis()
        );

if (handoffReclamoComprasValido) {
    request.setAttribute(
            Constants.CMD,
            Constants.ADD
    );
}

/*
 * En algunos wrappers legacy request.getContextPath() devuelve "/" para el
 * contexto raiz. Concatenar otro slash genera //html/... y el navegador
 * interpreta "html" como hostname. Se normaliza una sola vez para todos los
 * assets del ensamblado segmentado.
 */
String reclamoPrestacionalContextPath = request.getContextPath();
if (Validator.isNull(reclamoPrestacionalContextPath)
        || "/".equals(reclamoPrestacionalContextPath)) {
    reclamoPrestacionalContextPath = "";
}
String reclamoPrestacionalAssetBase =
        reclamoPrestacionalContextPath
        + "/html/portlet/autorizaciones/reclamos_prestacionales/";
%>
<script type="text/javascript">
window.ReclamoPrestacionalNamespace = '<portlet:namespace />';
window.ReclamoPrestacionalAssetError = function(nombre) {
    if (window.console && window.console.error) {
        window.console.error("RECLAMO_PRESTACIONAL_ASSET_ERROR: " + nombre);
    }
};
</script>
<script type="text/javascript">
(function(window, jQuery) {
    if (!jQuery || typeof jQuery.ajax !== "function"
            || jQuery.ajax.__rpFiltroLetraNoBloqueante) {
        return;
    }

    var ajaxOriginal = jQuery.ajax;
    var ajaxNoBloqueante = function(opciones) {
        if (arguments.length === 1
                && opciones
                && typeof opciones === "object"
                && opciones.async === false
                && String(opciones.url || "")
                        .indexOf("filtrarLetraComprobante") >= 0) {

            opciones = jQuery.extend({}, opciones);
            opciones.async = true;

            return ajaxOriginal.call(this, opciones);
        }

        return ajaxOriginal.apply(this, arguments);
    };

    ajaxNoBloqueante.__rpFiltroLetraNoBloqueante = true;
    ajaxNoBloqueante.__rpAjaxOriginal = ajaxOriginal;
    jQuery.ajax = ajaxNoBloqueante;
})(window, window.jQuery);
</script>
<script type="text/javascript">
(function(window, jQuery) {
    if (!jQuery || typeof jQuery.ajax !== "function") {
        return;
    }

    var TIMEOUT_AFILIADO_MS = 15000;

    function esEndpointAfiliadoNoBloqueante(url) {
        return url.indexOf("evalua_permanencia_afiliado") >= 0
                || url.indexOf("tiene_observaciones_afiliado") >= 0
                || url.indexOf("buscar_afiliado_datos") >= 0;
    }

    function reportarError(codigo, detalle) {
        if (window.console && window.console.error) {
            window.console.error(
                    codigo + (detalle ? ": " + detalle : "")
            );
        }
    }

    if (!jQuery.ajax.__rpAfiliadoNoBloqueante) {
        var ajaxAnterior = jQuery.ajax;
        var ajaxAfiliadoNoBloqueante = function(opciones) {
            if (arguments.length === 1
                    && opciones
                    && typeof opciones === "object"
                    && opciones.async === false) {

                var url = String(opciones.url || "");

                if (esEndpointAfiliadoNoBloqueante(url)) {
                    opciones = jQuery.extend({}, opciones);
                    opciones.async = true;

                    if (opciones.timeout == null) {
                        opciones.timeout = TIMEOUT_AFILIADO_MS;
                    }

                    return ajaxAnterior.call(this, opciones);
                }
            }

            return ajaxAnterior.apply(this, arguments);
        };

        ajaxAfiliadoNoBloqueante.__rpAfiliadoNoBloqueante = true;
        ajaxAfiliadoNoBloqueante.__rpAjaxAnterior = ajaxAnterior;
        jQuery.ajax = ajaxAfiliadoNoBloqueante;
    }

    if (!jQuery.fn
            || typeof jQuery.fn.load !== "function"
            || jQuery.fn.load.__rpAfiliadoTimeout) {
        return;
    }

    var loadOriginal = jQuery.fn.load;
    var xhrBusquedaAfiliado = null;

    var loadAfiliadoSeguro = function(url, parametros, callback) {
        if (typeof url !== "string"
                || url.indexOf(
                        "struts_action=/autorizaciones/buscar_afiliados"
                ) < 0) {

            return loadOriginal.apply(this, arguments);
        }

        var destino = this;
        var datos = parametros;
        var completar = callback;
        var metodo = "GET";

        if (typeof parametros === "function") {
            completar = parametros;
            datos = undefined;
        } else if (parametros && typeof parametros === "object") {
            metodo = "POST";
        }

        if (xhrBusquedaAfiliado
                && xhrBusquedaAfiliado.readyState !== 4) {
            xhrBusquedaAfiliado.abort();
        }

        destino.html(
                '<div class="portlet-msg-info">'
                        + 'Buscando afiliados...'
                        + '</div>'
        );

        xhrBusquedaAfiliado = jQuery.ajax({
            url: url,
            type: metodo,
            data: datos,
            dataType: "html",
            timeout: TIMEOUT_AFILIADO_MS,
            success: function(respuesta, estado, xhr) {
                destino.html(respuesta);

                if (typeof completar === "function") {
                    completar.call(
                            destino.length ? destino[0] : destino,
                            respuesta,
                            estado,
                            xhr
                    );
                }
            },
            error: function(xhr, estado) {
                if (estado === "abort") {
                    return;
                }

                destino.html(
                        '<div class="portlet-msg-error">'
                                + 'No se pudo completar la busqueda de '
                                + 'afiliados. Cierre esta ventana e '
                                + 'intente nuevamente.'
                                + '</div>'
                );

                reportarError(
                        "RECLAMO_PRESTACIONAL_AFILIADO_ERROR",
                        estado || "error"
                );

                if (typeof completar === "function") {
                    completar.call(
                            destino.length ? destino[0] : destino,
                            xhr && xhr.responseText
                                    ? xhr.responseText
                                    : "",
                            estado,
                            xhr
                    );
                }
            }
        });

        return destino;
    };

    loadAfiliadoSeguro.__rpAfiliadoTimeout = true;
    loadAfiliadoSeguro.__rpLoadOriginal = loadOriginal;
    jQuery.fn.load = loadAfiliadoSeguro;

    window.ReclamoPrestacionalAfiliadoSearchPatch = {
        timeoutMs: TIMEOUT_AFILIADO_MS,
        esEndpointNoBloqueante: esEndpointAfiliadoNoBloqueante
    };
})(window, window.jQuery);
</script>
<script type="text/javascript">
window.ReclamoPrestacionalJQueryLoadOriginal =
        window.jQuery && window.jQuery.fn ? window.jQuery.fn.load : null;
</script>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf" %>

<script type="text/javascript"
	src="<%= reclamoPrestacionalAssetBase %>view_reclamo.js?v=20260723-editor-dom-clean-1"
	onerror="window.ReclamoPrestacionalAssetError('view_reclamo.js');"></script>
<script type="text/javascript"
        src="<%= reclamoPrestacionalAssetBase %>view_reclamo_tab_guard.js?v=20260717-initial-state-1"
        onerror="window.ReclamoPrestacionalAssetError('view_reclamo_tab_guard.js');"></script>
<script type="text/javascript"
        src="<%= reclamoPrestacionalAssetBase %>view_reclamo_editor_patch.js?v=20260724-editor-buttons-2"
        onerror="window.ReclamoPrestacionalAssetError('view_reclamo_editor_patch.js');"></script>
<script type="text/javascript"
        src="<%= reclamoPrestacionalAssetBase %>view_reclamo_p0_patch.js?v=20260723-popup-clean-2"
        onerror="window.ReclamoPrestacionalAssetError('view_reclamo_p0_patch.js');"></script>
<script type="text/javascript"
	src="<%= reclamoPrestacionalAssetBase %>view_reclamo_prestacion_rules_patch.js?v=20260720-recuperable-neutro-2"
	onerror="window.ReclamoPrestacionalAssetError('view_reclamo_prestacion_rules_patch.js');"></script>
<script type="text/javascript"
	src="<%= reclamoPrestacionalAssetBase %>view_reclamo_produccion_7305_patch.js?v=20260728-visual-3"
	onerror="window.ReclamoPrestacionalAssetError('view_reclamo_produccion_7305_patch.js');"></script>

<script type="text/javascript">
(function(window, jQuery) {
    var config = window.ReclamoPrestacionalViewConfig || {};
    var namespace = config.namespace || "";

    function esVacio(valor) {
        return valor == null || valor === "" || valor === "0" || valor === "-1";
    }

    function normalizarFechaOpcional(prefijo) {
        var dia = jQuery("#" + namespace + prefijo + "Dia");
        var mes = jQuery("#" + namespace + prefijo + "Mes");
        var anio = jQuery("#" + namespace + prefijo + "Anio");

        if (!dia.length || !mes.length || !anio.length) {
            return;
        }

        if (esVacio(dia.val()) && esVacio(mes.val()) && esVacio(anio.val())) {
            dia.val("");
            mes.val("");
            anio.val("");
        }
    }

    function normalizarFechasOpcionales() {
        normalizarFechaOpcional("fechaseccional");
        normalizarFechaOpcional("fechacierre");
    }

    var submitFormOriginal = window.submitForm;
    if (typeof submitFormOriginal === "function" && !submitFormOriginal.__rpP0Normalizado) {
        var submitFormNormalizado = function(formulario) {
            if (formulario && formulario.name === namespace + "reclamo_fm") {
                normalizarFechasOpcionales();
            }
            return submitFormOriginal.apply(this, arguments);
        };
        submitFormNormalizado.__rpP0Normalizado = true;
        window.submitForm = submitFormNormalizado;
    }

    jQuery("#" + namespace + "reclamo_fm").submit(normalizarFechasOpcionales);
})(window, jQuery);

function <portlet:namespace />actualizarAfiliadoPorFecha(diaId, mesId, anioId) {
	var diaPrest = jQuery("#<portlet:namespace />" + diaId).val();
	var mesPrest = jQuery("#<portlet:namespace />" + mesId).val();
	var anioPrest = jQuery("#<portlet:namespace />" + anioId).val();

	if (diaPrest == "" || mesPrest == "" || anioPrest == "" || mesPrest == "-1") {
		return;
	}

	var mesReal = parseInt(mesPrest, 10) + 1;
	var fechaPrestacion = diaPrest + "/" + mesReal + "/" + anioPrest;

	jQuery("#<portlet:namespace />fprest").val(fechaPrestacion);

	var cuil = jQuery("#<portlet:namespace />cuil").val();
	var inte = jQuery("#<portlet:namespace />inte").val();

	if (cuil != "" && inte != "") {
		<portlet:namespace />buscarAfiliados_(fechaPrestacion);
	}
}

function <portlet:namespace />actualizarFechaPrestacionAfiliado() {
	<portlet:namespace />actualizarAfiliadoPorFecha(
		"fechaPrestacionDia",
		"fechaPrestacionMes",
		"fechaPrestacionAnio"
	);
}

function <portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado() {
	<portlet:namespace />actualizarAfiliadoPorFecha(
		"fechaPrestacionDiaFarmacia",
		"fechaPrestacionMesFarmacia",
		"fechaPrestacionAnioFarmacia"
	);
}

function <portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion() {
	<portlet:namespace />actualizarAfiliadoPorFecha(
		"fechaPrestacionDiaEdicion",
		"fechaPrestacionMesEdicion",
		"fechaPrestacionAnioEdicion"
	);
}

function <portlet:namespace />vincularFechasPrestacionEdicion() {
	var handler = <portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion;
	var dia = jQuery("#<portlet:namespace />fechaPrestacionDiaEdicion");
	var mes = jQuery("#<portlet:namespace />fechaPrestacionMesEdicion");
	var anio = jQuery("#<portlet:namespace />fechaPrestacionAnioEdicion");

	dia.unbind("change", handler).bind("change", handler);
	mes.unbind("change", handler).bind("change", handler);
	anio.unbind("change", handler).bind("change", handler);
}

jQuery("#<portlet:namespace />fechaPrestacionDia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionMes").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionAnio").change(function(){
	<portlet:namespace />actualizarFechaPrestacionAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionDiaFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionMesFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery("#<portlet:namespace />fechaPrestacionAnioFarmacia").change(function(){
	<portlet:namespace />actualizarFechaPrestacionFarmaciaAfiliado();
});

jQuery(document).ready(function() {
	<portlet:namespace />vincularFechasPrestacionEdicion();
});

jQuery(document).ajaxComplete(function(evento, xhr, opciones) {
	var url = opciones && opciones.url ? String(opciones.url) : "";
	if (url.indexOf("editar_reclamosprestaciones") >= 0) {
		<portlet:namespace />vincularFechasPrestacionEdicion();
	}
});
</script>
