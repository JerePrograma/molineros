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

<script type="text/javascript">
(function(window, jQuery) {
    var namespace = window.ReclamoPrestacionalNamespace || "";

    if (<%= esBorradorCompras %>) {
        return;
    }

    var sector = jQuery("#" + namespace + "sector").val();
    var tipoPedido = jQuery("#" + namespace + "tipopedido").val();
    var usaBuscadorFarmacia =
            sector === "FARMACIA" && tipoPedido !== "EXCEPCION";

    if (usaBuscadorFarmacia) {
        jQuery("#" + namespace + "busqueda_farmacia").show();
        jQuery("#" + namespace + "busqueda_prestaciones").hide();
    } else {
        jQuery("#" + namespace + "busqueda_prestaciones").show();
        jQuery("#" + namespace + "busqueda_farmacia").hide();
    }

    jQuery("#" + namespace + "nom_seleccionado").val(
            usaBuscadorFarmacia ? "2" : "1"
    );
})(window, jQuery);
</script>

<script type="text/javascript">
(function(window, jQuery) {
    var config = window.ReclamoPrestacionalViewConfig || {};
    var namespace = config.namespace || "";

    function valor(sufijo) {
        var control = jQuery("#" + namespace + sufijo);
        return control.length ? control.val() : "";
    }

    window.ReclamoPrestacionalBootstrapSnapshot = {
        troquel: valor("troquel"),
        codigo: valor("codigoSeguimiento_filtro"),
        descripcion: valor("descripcionSeguimiento_filtro"),
        tipoNomenclador: valor("tipoNomencladorSeguimiento_filtro"),
        tipoNomencladorSeleccionado: valor("tipoNomenclador"),
        nomSeleccionado: valor("nom_seleccionado"),
        sector: valor("sector"),
        tipoPedido: valor("tipopedido")
    };
})(window, jQuery);
</script>

<script type="text/javascript"
	src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_initial_state.js?v=20260717-initial-state-3"
	onerror="window.ReclamoPrestacionalAssetError('view_reclamo_initial_state.js');"></script>
<script type="text/javascript"
	src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js?v=20260717-initial-state-1"
	onerror="window.ReclamoPrestacionalAssetError('view_reclamo.js');"></script>
<script type="text/javascript">
if (!window.ReclamoPrestacionalInitialStateOk) {
    window.ReclamoPrestacionalAssetError('view_reclamo_initial_state.js/bootstrap');
}
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_tab_guard.js?v=20260717-initial-state-1"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js?v=20260717-initial-state-1"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js?v=20260717-initial-state-1"></script>

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

    jQuery(document).on(
            "submit",
            "#" + namespace + "reclamo_fm",
            normalizarFechasOpcionales
    );
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

jQuery(document).on("change", "#<portlet:namespace />fechaPrestacionDiaEdicion", function(){
	<portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion();
});

jQuery(document).on("change", "#<portlet:namespace />fechaPrestacionMesEdicion", function(){
	<portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion();
});

jQuery(document).on("change", "#<portlet:namespace />fechaPrestacionAnioEdicion", function(){
	<portlet:namespace />actualizarAfiliadoPorFechaPrestacionEdicion();
});
</script>
