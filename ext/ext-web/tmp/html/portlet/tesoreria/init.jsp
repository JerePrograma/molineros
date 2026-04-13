<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>

<%@ page import="ar.com.ospim.util.ListUtils" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>

<%@ page import="ar.com.ospim.estudioisidro.WebKeysEstudioIsidro" %>

<%@ page import="ar.com.ospim.liquidaciones.WebKeysLiquidaciones" %>
<%@ page import="ar.com.ospim.liquidaciones.DuplicateNumeroChequeException" %>

<%@ page import="ar.com.ospim.afip.beans.ReporteAporteContribucionesEmpresa" %>
<%@ page import="ar.com.ospim.afip.beans.ReporteDeudaEmpresa" %>
<%@ page import="ar.com.ospim.afip.beans.ArchivoSubidoAfip" %>
<%@ page import="ar.com.ospim.afip.beans.ArchivoSubidoBco" %>

<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.global.ExisteReciboConvenioException" %>
<%@ page import="ar.com.ospim.global.beans.Banco" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.beans.Cheque" %>
<%@ page import="ar.com.ospim.global.beans.Ingreso" %>
<%@ page import="ar.com.ospim.global.beans.Efectivo" %>
<%@ page import="ar.com.ospim.global.beans.Pagare" %>
<%@ page import="ar.com.ospim.global.beans.ConvenioNacion" %>
<%@ page import="ar.com.ospim.global.beans.CuentasNacion" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="ar.com.ospim.global.beans.DepositoBancario"%>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.global.services.ChequeServiceUtil" %>

<%@ page import="ar.com.ospim.tesoreria.ActaPerteneceAOtraEmpresaException" %>
<%@ page import="ar.com.ospim.tesoreria.FaltanCuotasConvenioException" %>
<%@ page import="ar.com.ospim.tesoreria.ConvenioSinActasRelacionadasException" %>
<%@ page import="ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.tesoreria.ConvenioSinPagosException" %>
<%@ page import="ar.com.ospim.tesoreria.DuplicateConvenioIdException" %>
<%@ page import="ar.com.ospim.tesoreria.DuplicateActaIdException" %>
<%@ page import="ar.com.ospim.tesoreria.actas.action.InspectorWrapper" %>
<%@ page import="ar.com.ospim.tesoreria.ActaNoExisteException" %>
<%@ page import="ar.com.ospim.tesoreria.ActaYaRelacionadaException" %>
<%@ page import="ar.com.ospim.tesoreria.ActaSinPagosException" %>
<%@ page import="ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil" %>
<%@ page import="ar.com.ospim.tesoreria.service.ActaServiceUtil"%>
<%@ page import="ar.com.ospim.tesoreria.DuplicateNumeroReciboException"%>
<%@ page import="ar.com.ospim.tesoreria.ChequeYaIngresadoException" %>
<%@ page import="ar.com.ospim.tesoreria.ReciboConceptoSinImporteException"%>
<%@ page import="ar.com.ospim.tesoreria.ImposibleBorrarActaException"%>
<%@ page import="ar.com.ospim.tesoreria.LiquidarActaConvenioException"%>
<%@ page import="ar.com.ospim.tesoreria.beans.Recibo" %>
<%@ page import="ar.com.ospim.tesoreria.beans.ReciboActa"%>
<%@ page import="ar.com.ospim.tesoreria.beans.ReciboConvenio"%>
<%@ page import="ar.com.ospim.tesoreria.beans.ReciboCheque"%>
<%@ page import="ar.com.ospim.tesoreria.beans.ReciboOtroConcepto"%>
<%@ page import="ar.com.ospim.tesoreria.beans.ReciboPrestamo"%>
<%@ page import="ar.com.ospim.tesoreria.beans.ReciboIngreso"%>
<%@ page import="ar.com.ospim.tesoreria.beans.CuentaBancaria"%>
<%@ page import="ar.com.ospim.tesoreria.beans.MovimientoBancoCheque"%>
<%@ page import="ar.com.ospim.tesoreria.beans.MovimientoBancoReciboIngreso"%>
<%@ page import="ar.com.ospim.tesoreria.beans.TipoMovBcrio" %>
<%@ page import="ar.com.ospim.tesoreria.beans.Chequera" %>
<%@ page import="ar.com.ospim.tesoreria.beans.TipoTrxBancaria" %>
<%@ page import="ar.com.ospim.tesoreria.beans.MovimientoBancario" %>
<%@ page import="ar.com.ospim.tesoreria.beans.Acta" %>
<%@ page import="ar.com.ospim.tesoreria.beans.ActaEstadoSeguimiento" %>
<%@ page import="ar.com.ospim.tesoreria.beans.Acta.TotalActaNoOS" %>
<%@ page import="ar.com.ospim.tesoreria.beans.ActaPago" %>
<%@ page import="ar.com.ospim.tesoreria.beans.Inspector" %>
<%@ page import="ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa" %>
<%@ page import="ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones"%>
<%@ page import="ar.com.ospim.tesoreria.beans.CalculoDeudaMasivoCab "%>
<%@ page import="ar.com.ospim.tesoreria.beans.convenio.Convenio" %>
<%@ page import="ar.com.ospim.tesoreria.beans.convenio.ConvenioPago" %>
<%@ page import="ar.com.ospim.tesoreria.beans.convenio.ConvenioEstadoSeguimiento" %>
<%@ page import="ar.com.ospim.tesoreria.services.LiquidaActaConveniosServiceUtil"%>
<%@ page import="ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil"%>


<%@ page import="ar.com.ospim.farmaciaOspim.services.FarmaciaServiceUtil"%>


<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.beans.TercerizadoraServicio" %>
<%@ page import="ar.com.ospim.afiliados.reportes.beans.UltimosProcesosSisOld" %>

<%@ page import="ar.com.ospim.afip.service.AfipServiceUtil"%>

<%@ page import="ar.com.ospim.farmacia.WebKeysFarmacia"%>

<%@ page import="java.util.Date"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.math.BigDecimal" %>

<link rel="stylesheet" type="text/css" href="/html/jquery.mb.containerPlus/css/mbContainer.css" />
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/jquery.metadata.js"></script>
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/mbContainer.js"></script>

<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>

<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);
%>
<script src="/html/js/utils.js?1234" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>


<link rel="stylesheet" type="text/css" href="/html/jquery.mb.containerPlus/css/mbContainer.css" />
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/jquery.metadata.js"></script>
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/mbContainer.js"></script>


<script type="text/javascript">
function addEvent(obj, evType, fn, useCapture) {
// General function for adding an event listener
	if (obj.addEventListener) {
		obj.addEventListener(evType, fn, useCapture);
		return true;} else if (obj.attachEvent) {
	var r = obj.attachEvent("on" + evType, fn);
		return r;
	} else {
		alert(evType+" handler could not be attached");
	}
}

function addKeyEvent() {
// Specific function for this particular browser
	var e = (document.addEventListener) ? 'keypress' : 'keydown';
	addEvent(document,e,keyEventHandler,false);
}

addKeyEvent();
//To disable the right mouse button
document.oncontextmenu=new Function("return false");

jQuery(document).ready(function() {
	jQuery(".containerPlus").buildContainers({
        containment:"document",
        elementsPath:"/html/jquery.mb.containerPlus/elements/",
        onResize:function(o){},
        onClose:function(o){},
        onCollapse:function(o){},
        onIconize:function(o){},
        onDrag:function(o){},
        onLoad:function(o){}
    });
});

function closeHelps(){
	 var elements = jQuery('.containerPlus');
	 elements.each(function() { jQuery(this).mb_close(); })	
}

function help(event, id){
	closeHelps();
	jQuery("#" + id).mb_open();
	jQuery("#" + id).css("top",event.clientY + jQuery(document).scrollTop());
	jQuery("#" + id).css("left", 300);
}

</script>