<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.PortletBag" %>
<%@ page import="com.liferay.portal.kernel.portlet.PortletBagPool"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>


<%@ page import="ar.com.ospim.afiliados.NoSuchAfiliadoEntryException" %>
<%@ page import="ar.com.ospim.afiliados.DuplicateAfiliadoIdException" %>
<%@ page import="ar.com.ospim.afiliados.AfliadoYaTieneConyugeException" %>
<%@ page import="ar.com.ospim.afiliados.FormOpcionSSSDuplicadoException" %>
<%@ page import="ar.com.ospim.afiliados.FormOpcionSSSNoEnviadoException" %>
<%@ page import="ar.com.ospim.afiliados.empleadores.ImposibleBorrarEmpresaException" %>
<%@ page import="ar.com.ospim.afiliados.action.ActionUtil" %>
<%@ page import="ar.com.ospim.afiliados.beans.CategoriaLaboral" %>
<%@ page import="ar.com.ospim.afiliados.beans.SituacionRevista" %>
<%@ page import="ar.com.ospim.afiliados.beans.AporteAfiliado" %>
<%@ page import="ar.com.ospim.afiliados.beans.MotivoBaja" %>
<%-- <%@ page import="ar.com.ospim.afiliados.beans.AfiAporteList" %> --%>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>
<%@ page import="ar.com.ospim.afiliados.beans.TipoBono" %>
<%@ page import="ar.com.ospim.afiliados.beans.Documento" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiDocumentacion" %>
<%@ page import="ar.com.ospim.afiliados.beans.SituacionLaboral" %>
<%@ page import="ar.com.ospim.afiliados.beans.TercerizadoraServicio" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio" %>
<%@ page import="ar.com.ospim.afiliados.services.SituLaboralServiceUtil" %>
<%@ page import="ar.com.ospim.afiliados.services.TercerizadoraServiceUtil" %>
<%@ page import="ar.com.ospim.afiliados.services.DocumentacionServiceUtil" %>
<%@ page import="ar.com.ospim.afiliados.services.AporteServiceUtil" %>
<%@ page import="ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil" %>
<%@ page import="ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil" %>

<%@ page import="ar.com.ospim.global.beans.Nacionalidad" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.beans.ObraSocialCampo" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="ar.com.ospim.global.beans.Delegacion" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS" %>

<%@ page import="javax.servlet.ServletContext" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="ar.com.ospim.afiliados.beans.TipoAporte" %>
<%@ page import="ar.com.ospim.afiliados.beans.HistoricoMovimientoAfiliado" %>
<%@ page import="ar.com.ospim.afiliados.beans.EnvioBonos" %>
<%@ page import="ar.com.ospim.afiliados.beans.Direccion" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiPlan" %>
<%@ page import="ar.com.ospim.afiliados.SituacionLaboralException" %>
<%@ page import="ar.com.ospim.afiliados.AddTercerizadoraException" %>
<%@ page import="ar.com.ospim.afiliados.exceptions.DuplicateEnvioBonosException" %>
<%@ page import="ar.com.ospim.afiliados.exceptions.EnvioBonosNoExisteEnSeccionalException" %>
<%@ page import="ar.com.ospim.afiliados.exceptions.BonoNoCargadoException" %>
<%@ page import="ar.com.ospim.afiliados.ConyugeNoPuedeSerSolteroException" %>
<%@ page import="ar.com.ospim.afiliados.TitularNoPuedeSerSolteroException" %>
<%@ page import="ar.com.ospim.afiliados.HijoNoPuedeSerCasadoException" %>
<%-- <%@ page import="ar.com.ospim.afiliados.reportes.beans.ReporteAmtimaPMI" %>
<%@ page import="ar.com.ospim.afiliados.reportes.beans.PadronInformado" %> --%>
<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.empleadores.WebKeysEmpleadores" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.CuilUtils" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>

<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%-- <%@ page import="ar.com.ospim.global.beans.ContactoElectronico" %>
<%@ page import="ar.com.ospim.global.beans.RamoEmpresa" %>
<%@ page import="ar.com.ospim.global.beans.EntidadCamaraEmpresa" %>
<%@ page import="ar.com.ospim.global.beans.PosicionIva" %> --%>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.EstadoCivil" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.global.beans.Empresa" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>

<%@ page import="ar.com.ospim.novedades.beans.ArchivoNovedad" %>
<%@ page import="ar.com.ospim.novedades.beans.TipoNovedad" %>
<%@ page import="ar.com.ospim.novedades.beans.Novedad" %>
<%@ page import="ar.com.ospim.novedades.beans.NovedadTotal" %>
<%@ page import="ar.com.ospim.novedades.beans.NovedadEmpleadorTotal" %>
<%@ page import="ar.com.ospim.novedades.beans.BusquedaNovedadesSssFiltro" %>
<%@ page import="ar.com.ospim.novedades.beans.BusquedaPreAfiliadosFiltro" %>
<%@ page import="ar.com.ospim.novedades.beans.PreAfiliado" %>
<%@ page import="ar.com.ospim.novedades.beans.PreAfiliadoTotal" %>

<%@ page import="ar.com.ospim.novedades.service.NovedadesServiceUtil"%>
<%@ page import="ar.com.ospim.novedades.service.PreAfiliadoServiceUtil"%>




<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>
<portlet:defineObjects />
<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);

List<Nacionalidad> nacionalidades = (ArrayList<Nacionalidad>) portletSession
.getAttribute(WebKeysAfiliados.NACIONALIDADES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (nacionalidades == null) {
	nacionalidades = TraeListasServiceUtil.getNacionalidades();
	portletSession.setAttribute(WebKeysAfiliados.NACIONALIDADES_EN_SESSION,
	nacionalidades,PortletSession.APPLICATION_SCOPE);	
}

List<Seccional> seccionales = (ArrayList<Seccional>) portletSession
.getAttribute(WebKeysAfiliados.SECCIONALES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (seccionales == null) {
	seccionales = TraeListasServiceUtil.getSeccionales();
	portletSession.setAttribute(WebKeysAfiliados.SECCIONALES_EN_SESSION,
	seccionales,PortletSession.APPLICATION_SCOPE);
}

List<ObraSocialCampo> obrasSocialesAnteriores = (ArrayList<ObraSocialCampo>) portletSession
.getAttribute(WebKeysAfiliados.OBRAS_SOCIALES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (obrasSocialesAnteriores == null) {
	obrasSocialesAnteriores = TraeListasServiceUtil.getObrasSocialesAnteriores();
	portletSession.setAttribute(WebKeysAfiliados.OBRAS_SOCIALES_EN_SESSION,
	obrasSocialesAnteriores, PortletSession.APPLICATION_SCOPE);
}

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
 
List<MotivoBaja> motivos = (ArrayList<MotivoBaja>) portletSession.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,PortletSession.APPLICATION_SCOPE);
if(motivos == null){
	motivos = TraeListasServiceUtil.getMotivosBaja();
	portletSession.setAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,
			motivos,PortletSession.APPLICATION_SCOPE);
}

List<Parentesco> parentescos = (ArrayList<Parentesco>) portletSession
.getAttribute(WebKeysAfiliados.PARENTESCOS_EN_SESSION, PortletSession.APPLICATION_SCOPE);

if (parentescos == null || parentescos.size()==0) {
	parentescos = TraeListasServiceUtil.getParentescos();
	portletSession.setAttribute(WebKeysAfiliados.PARENTESCOS_EN_SESSION,
	parentescos,PortletSession.APPLICATION_SCOPE);	
}
List<EstadoCivil> estados_civil = (ArrayList<EstadoCivil>) portletSession
.getAttribute(WebKeysAfiliados.ESTADOS_CIVIL_EN_SESSION, PortletSession.APPLICATION_SCOPE);

if (estados_civil == null || estados_civil.size()==0) {
	estados_civil = TraeListasServiceUtil.getEstadosCivil();
	portletSession.setAttribute(WebKeysAfiliados.ESTADOS_CIVIL_EN_SESSION,
	estados_civil,PortletSession.APPLICATION_SCOPE);	
}
	String seccionalString=null;
	String seccionalDefecto=user.getExpandoBridge().getAttribute("id_seccional").toString(); 		
	int seccionalFijada=null!=seccionalDefecto&& !seccionalDefecto.trim().equals("")&& !seccionalDefecto.trim().equals("0")?Integer.parseInt(seccionalDefecto):0;
	if(seccionalFijada!=0){
		seccionalString=user.getExpandoBridge().getAttribute("seccional").toString();
	}
%>
<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>

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
</script>