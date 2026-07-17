<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto" %>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include
	file="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>
<script type="text/javascript">
window.ReclamoPrestacionalNamespace = '<portlet:namespace />';
window.ReclamoPrestacionalAssetError = function(nombre) {
    if (window.console && window.console.error) {
        window.console.error("RECLAMO_PRESTACIONAL_ASSET_ERROR: " + nombre);
    }
};
</script>
<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf" %>

<script type="text/javascript"
	src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js?v=20260717-legacy-flows-1"
	onerror="window.ReclamoPrestacionalAssetError('view_reclamo.js');"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_tab_guard.js?v=20260717-initial-state-1"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js?v=20260717-initial-state-1"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js?v=20260717-legacy-flows-1"></script>

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
