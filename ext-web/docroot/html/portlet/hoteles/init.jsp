<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>

<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.global.beans.Nacionalidad" %>
<%@ page import="ar.com.ospim.global.beans.Domicilio" %>
<%@ page import="ar.com.ospim.global.beans.Telefono" %>
<%@ page import="ar.com.ospim.global.beans.ContactoElectronico" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.beans.ObraSocialCampo" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="ar.com.ospim.global.beans.Prestacion" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.global.services.OrdenPagoServiceUtil" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>

<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.security.auth.PrincipalException" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.SystemException" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>
<%@ page import="ar.com.ospim.util.StringUtils"%>

<%@ page import="ar.com.ospim.liquidaciones.WebKeysLiquidaciones" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Reintegro" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Liquidacion" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ReintegroPrestacion" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.DetalleCuota" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTercero" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdo" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoProtesis" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.LiquidacionPrestacionOdo" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Prestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.PrestadorExterno" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Prestador.TipoPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.PrestadorPlan" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.PlanPrestacion" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Catastro"%>
<%@ page import="ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad"%>
<%@ page import="ar.com.ospim.liquidaciones.beans.ConvenioPrestacional" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ConvenioPrestacionalDetalle" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ConvenioPrestacional.EstadosConvPrest"%>
<%@ page import="ar.com.ospim.liquidaciones.beans.MotivoAltaDiscapacidad" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ProfesionPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.EspecialidadPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.SubEspecialidadPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.MatriculaPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TelefonoPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TipoDiscapacidad" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.BusquedaConvenioPrestacionalFiltro" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TipoNomenclador" %>

<%@ page import="ar.com.ospim.liquidaciones.services.ReintegroServiceUtil" %>
<%@ page import="ar.com.ospim.liquidaciones.services.DebitoServiceUtil" %>
<%@ page import="ar.com.ospim.liquidaciones.services.CatastroServiceUtil" %>
<%@ page import="ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil" %>
<%@ page import="ar.com.ospim.liquidaciones.services.PrestadorExternoServiceUtil" %>
<%@ page import="ar.com.ospim.liquidaciones.services.ConvenioPrestacionalServiceUtil" %>
<%@ page import="ar.com.ospim.liquidaciones.services.PrestadorServiceUtil" %>

<%@ page import="ar.com.ospim.liquidaciones.action.LiquidacionDebitosActionUtil" %>
<%@ page import="ar.com.ospim.liquidaciones.DuplicateNumeroChequeException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticipoSuperaImporteOPException" %>
<%@ page import="ar.com.ospim.liquidaciones.ListasReintegrosNoEncontradasException"%>
<%@ page import="ar.com.ospim.liquidaciones.NoSuchReintegroEntryException" %>
<%@ page import="ar.com.ospim.liquidaciones.DuplicateReintegroIdException" %>
<%@ page import="ar.com.ospim.liquidaciones.NoSuchReintegroPrestacionEntryException" %>
<%@ page import="ar.com.ospim.liquidaciones.DuplicateReintegroPrestacionIdException" %>
<%@ page import="ar.com.ospim.liquidaciones.administracion.prestadores.exception.MatriculaProvincialPrestadorException"%>
<%@ page import="ar.com.ospim.liquidaciones.administracion.prestadores.exception.MatriculaNacionalPrestadorException"%>
<%@ page import="ar.com.ospim.liquidaciones.administracion.prestadores.exception.ProfesionEspecialidadSubEspecPrestadorException"%>
<%@ page import="ar.com.ospim.liquidaciones.administracion.prestadores.exception.LugarAtencionPrestadorException"%>
<%@ page import="ar.com.ospim.liquidaciones.administracion.prestadores.exception.PlanPrestadorDuplicadoException"%>
<%@ page import="ar.com.ospim.liquidaciones.PrestacionYaHechaAAfiliadoExcepcion"%>


<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.global.beans.PosicionIva" %>
<%@ page import="ar.com.ospim.global.beans.Cheque" %>
<%@ page import="ar.com.ospim.global.beans.Caja" %>
<%@ page import="ar.com.ospim.global.beans.OrdenPagoAmtima" %>
<%@ page import="ar.com.ospim.global.beans.OrdenPagoOspim" %>
<%@ page import="ar.com.ospim.global.beans.OrdenPago" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante" %>
<%@ page import="ar.com.ospim.global.beans.Motivo"%>
<%@ page import="ar.com.ospim.global.beans.ComprobanteItem"%>
<%@ page import="ar.com.ospim.global.beans.RetencionGanancias"%>
<%@ page import="ar.com.ospim.global.beans.Anticipo"%>
<%@ page import="ar.com.ospim.global.beans.PagoBancario"%>
<%@ page import="ar.com.ospim.global.beans.Pago"%>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="ar.com.ospim.global.beans.OrdenPago.FormaPago" %>
<%@ page import="ar.com.ospim.global.beans.TipoPago" %>
<%@ page import="ar.com.ospim.global.ComprobanteExistenteException"%>
<%@ page import="ar.com.ospim.global.ComprobanteInexistenteException"%>

<%@ page import="ar.com.ospim.afiliados.action.ActionUtil" %>

<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.beans.Documento" %>
<%@ page import="ar.com.ospim.afiliados.beans.Direccion" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiDocumentacion" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.ospim.afiliados.beans.TipoBono" %>
<%@ page import="ar.com.ospim.afiliados.beans.DetalleDiscapacidad" %>
<%@ page import="ar.com.ospim.afiliados.beans.CieDiez" %>
<%@ page import="ar.com.ospim.afiliados.beans.TercerizadoraServicio" %>
<%@ page import="ar.com.ospim.afiliados.services.DocumentacionServiceUtil" %>
<%@ page import="ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil" %>

<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>

<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.tesoreria.beans.CuentaBancaria"%>
<%@ page import="ar.com.ospim.hoteles.services.HotelesServiceUtil"%>
<%@ page import="ar.com.ospim.hoteles.beans.*"%>
<%@ page import="ar.com.ospim.hoteles.services.WebKeysHoteles" %>

<%@ page import="ar.com.ospim.global.beans.Banco"%>
<%@ page import="ar.com.ospim.tesoreria.beans.CuentaBancaria"%>

<%@ page import="ar.com.uoma.facturacion.Producto"%>
<%@ page import="ar.com.uoma.facturacion.services.FacturacionServiceUtil"%>
<%@ page import="ar.com.uoma.WebKeysUOMA"%>                                     


<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);
%>
<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>

<script type="text/javascript">

<%

List<Provincia> provincias = (ArrayList<Provincia>) portletSession.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION, PortletSession.APPLICATION_SCOPE);

if (provincias == null) {
	provincias = TraeListasServiceUtil.getProvincias();
	portletSession.setAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
	provincias,PortletSession.APPLICATION_SCOPE);	
}

List<Localidad> localidades = (ArrayList<Localidad>) portletSession.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION, PortletSession.APPLICATION_SCOPE);

if (localidades == null || localidades.size()==0) {
	localidades = TraeListasServiceUtil.getLocalidades();
	portletSession.setAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
	localidades,PortletSession.APPLICATION_SCOPE);	
}

Map<Integer,List<Localidad>> localidadesPorProvincia = (Map<Integer,List<Localidad>>) portletSession.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION_POR_PROVINCIA, PortletSession.APPLICATION_SCOPE);

if (localidadesPorProvincia == null || localidadesPorProvincia.size()==0) {
	localidadesPorProvincia = new HashMap<Integer,List<Localidad>>();	 
		
	for(Localidad l:localidades){
		if(l!=null && l.getId_provincia()>0 && l.getDescripcion()!=null && !"".equalsIgnoreCase(l.getDescripcion().trim() )){
			List<Localidad> lst =  new ArrayList<Localidad>();
			try{
			  lst = localidadesPorProvincia.get(l.getId_provincia());
			  if(lst==null) lst =  new ArrayList<Localidad>();
			}catch(Exception e){
			  lst =  new ArrayList<Localidad>();
			}
			lst.add(l);
			localidadesPorProvincia.put(l.getId_provincia(), lst);
	   }
	}
	
	portletSession.setAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION_POR_PROVINCIA,
			localidadesPorProvincia,PortletSession.APPLICATION_SCOPE);

}

List<Banco> entidades = (List<Banco>) portletSession.getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (entidades == null) {
   entidades = TraeListasServiceUtil.getBancos();
   portletSession.setAttribute(	WebKeysTesoreria.BANCOS_EN_SESSION, entidades,
	PortletSession.APPLICATION_SCOPE);
}

List<CuentaBancaria> ctas = (List<CuentaBancaria>) portletSession.getAttribute(	WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (ctas == null) {
    ctas = TraeListasServiceUtil.getCtasBcrias();
    portletSession.setAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION, ctas,	PortletSession.APPLICATION_SCOPE);
}

portletSession.setAttribute(WebKeysUOMA.PRODUCTOS_EN_SESSION, FacturacionServiceUtil.getProductos(),
		PortletSession.APPLICATION_SCOPE);
%>




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
</script>
