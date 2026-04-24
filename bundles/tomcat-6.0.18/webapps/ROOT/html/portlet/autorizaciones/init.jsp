<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.SystemException" %>
<%@ page import="com.liferay.portal.security.auth.PrincipalException" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>

<%@ page import="ar.com.ospim.afiliados.beans.CieDiez" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinario" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PrestacionesEquipoInterdisciplinario" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PatologiasSituacionMedica" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.BusquedaSituacionMedicaFiltro" %>

<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>


<%@ page import="ar.com.ospim.autorizaciones.exceptions.AfiliadoNoEsBebeException " %>
<%@ page import="ar.com.ospim.autorizaciones.exceptions.ExcedeCantAutoException " %>
<%@ page import="ar.com.ospim.autorizaciones.exceptions.NoEsPlanMolineroException " %>
<%@ page import="ar.com.ospim.autorizaciones.exceptions.PrestacionesReclamosException " %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ReclamosPrestacionalesIntegracion" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ReclamosPrestacionalesRevisionEstado" %>
<%@ page import="ar.com.ospim.autorizaciones.exceptions.PrestacionesEquipoInterdisciplinarioException " %>
<%@ page import="ar.com.ospim.autorizaciones.exceptions.RevisionesReclamosException " %>
<%@ page import="ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones" %>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.AutorizacionesPmi" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.Nomenclador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TipoNomenclador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Especialidad" %>
<%@ page import="ar.com.ospim.autorizaciones.action.EditarAutorizacionPmiEntry"%>
<%@ page import="ar.com.ospim.autorizaciones.beans.NomencladorPlan" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ModalidadAtencion" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSur" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.DrogaPatologia" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ConsultaIGS"%>
<%@ page import="ar.com.ospim.autorizaciones.beans.ConsultaIGSTotal"%>
<%@ page import="ar.com.ospim.autorizaciones.beans.BusquedaConsultasIGSFiltro"%>
<%@ page import="ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.Cartilla" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.MotivoExcepcion" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento" %>
<%@ page import="ar.com.ospim.autorizaciones.constantes.interbaking.ConstantesInterbanking" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.FirmaAutorizante" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.BusquedaReclamoSeccionalFiltro" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.BusquedaReclamoFiltro" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.AfiCuentaBancaria" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalCuenta"%>

<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>

<%@ page import="ar.com.ospim.util.ListUtils" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.util.StringUtils"%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ReclamoPrestacional" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ItemReclamoPrestacionalesTotal" %>


<%@ page import="ar.com.ospim.afiliados.beans.Documento" %>
<%@ page import="ar.com.ospim.afiliados.beans.TercerizadoraServicio" %>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="ar.com.ospim.liquidaciones.beans.Prestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TipoDiscapacidad" %>
<%@ page import="ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil" %>

<%@ page import="ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.EstadosReclamosPrestacionales" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.TiposDeGestionReclamosPrestacionales" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.OpcionesPrestacion" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PrestacionesReclamo" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.RevisionesReclamo" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.TiposDeSituacionesMedicas" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.SituacionMedica" %>

<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@ page import="ar.com.ospim.autorizaciones.beans.SeguimientoSurLoteProcesado" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PreAutorizacion" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.PreAutorizacionLoteProcesado" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDS" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS" %>
<%@ page import="ar.com.ospim.global.beans.ClaseBase" %>

<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>

<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" href="/html/jquery.mb.containerPlus/css/mbContainer.css" />
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/jquery.metadata.js"></script>
<script type="text/javascript" src="/html/jquery.mb.containerPlus/inc/mbContainer.js"></script>

<%
List<Provincia> provincias = (ArrayList<Provincia>) portletSession
.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION, PortletSession.APPLICATION_SCOPE);

if (provincias == null) {
	provincias = TraeListasServiceUtil.getProvincias();
	portletSession.setAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
	provincias,PortletSession.APPLICATION_SCOPE);	
}

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION, PortletSession.APPLICATION_SCOPE);

if (localidades == null || localidades.size()==0) {
	localidades = TraeListasServiceUtil.getLocalidades();
	portletSession.setAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
	localidades,PortletSession.APPLICATION_SCOPE);	
}

//DS - Manejo Localidades por Provincia
//Map<Integer,List<Localidad>> localidadesPorProvincia = TraeListasServiceUtil.getLocalidadesAgrupadasPorProvincia();

 Map<Integer,List<Localidad>> localidadesPorProvincia = (Map<Integer,List<Localidad>>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION_POR_PROVINCIA, PortletSession.APPLICATION_SCOPE);

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
%>

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

function VtnaObs(detalleObservacion,titulo){
	var lineaasteriscos = Array(titulo.length+5).join("*");
	alert( titulo + "\n" + lineaasteriscos + "\n" +  detalleObservacion);
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