<%@ include file="/html/portlet/init.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.util.StringUtils" %>
<%@ page import="java.util.Comparator" %>

<%@ page import="ar.com.ospim.prestadores.exception.ProfesionEspecialidadSubEspecPrestadorException"%>
<%@ page import="ar.com.ospim.prestadores.exception.MatriculaNacionalPrestadorException"%>
<%@ page import="ar.com.ospim.prestadores.exception.MatriculaProvincialPrestadorException"%>
<%@ page import="ar.com.ospim.prestadores.exception.PlanPrestadorDuplicadoException"%>
<%@ page import="ar.com.ospim.prestadores.exception.LugarAtencionPrestadorException"%>
<%@ page import="ar.com.ospim.global.beans.Telefono" %>
<%@ page import="ar.com.ospim.global.beans.TipoPago" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.PrestadorPlan" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TelefonoPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador" %>

<%@ page import="ar.com.ospim.prestadores.beans.ConvenioPrestacional.EstadosConvPrest" %>

<%@ page import="ar.com.ospim.prestadores.WebKeysPrestadores" %>
<%@ page import="ar.com.ospim.liquidaciones.WebKeysLiquidaciones" %>

<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.global.beans.Provincia" %>
<%@ page import="ar.com.ospim.global.beans.Localidad" %>
<%@ page import="ar.com.ospim.global.services.TraeListasServiceUtil" %>
<%@ page import="ar.com.ospim.global.beans.Domicilio" %>

<%@ page import="ar.com.ospim.liquidaciones.beans.EspecialidadPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.ProfesionPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.SubEspecialidadPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.Prestador.TipoPrestador" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.MatriculaPrestador" %>
<%@ page import="ar.com.ospim.prestadores.beans.BusquedaCartillaConvenioFiltro" %>
<%@ page import="ar.com.ospim.prestadores.beans.ConvenioPrestacional" %>
<%@ page import="ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle" %>
<%@ page import="ar.com.ospim.prestadores.beans.CartillaConvenioRow" %>
<%@ page import="ar.com.ospim.prestadores.beans.BusquedaConvenioPrestacionalFiltro" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.DocumentComparator" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.portlet.PortletBag" %>
<%@ page import="com.liferay.portal.kernel.portlet.PortletBagPool"%>

<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.plugin.PluginPackageUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.ImageSizeException" %>

<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portal.service.OrganizationLocalServiceUtil" %>
<%@ page import="com.liferay.portal.service.GroupLocalServiceUtil" %>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil" %>
<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.model.Organization" %>
<%@ page import="com.liferay.portal.model.Group" %>

<%@ page import="ar.com.ospim.afiliados.action.ActionUtil" %>
<%@ page import="ar.com.ospim.afiliados.beans.CategoriaLaboral" %>
<%@ page import="ar.com.ospim.afiliados.beans.SituacionRevista" %>
<%@ page import="ar.com.ospim.afiliados.beans.AporteAfiliado" %>
<%@ page import="ar.com.ospim.afiliados.beans.MotivoBaja" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiAporteList" %>
<%@ page import="ar.com.ospim.afiliados.beans.Afiliado" %>

<%@ page import="ar.com.ospim.afiliados.beans.Documento" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiDocumentacion" %>
<%@ page import="ar.com.ospim.afiliados.beans.SituacionLaboral" %>
<%@ page import="ar.com.ospim.afiliados.beans.TercerizadoraServicio" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio" %>
<%@ page import="ar.com.ospim.afiliados.beans.TipoAporte" %>
<%@ page import="ar.com.ospim.afiliados.beans.Direccion" %>
<%@ page import="ar.com.ospim.afiliados.beans.AfiPlan" %>

<%@ page import="ar.com.ospim.afiliados.WebKeysAfiliados" %>
<%@ page import="ar.com.ospim.afiliados.empleadores.WebKeysEmpleadores" %>

<%@ page import="ar.com.ospim.global.beans.Nacionalidad" %>
<%@ page import="ar.com.ospim.global.beans.ObraSocialCampo" %>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
<%@ page import="ar.com.ospim.global.beans.Delegacion" %>
<%@ page import="ar.com.ospim.global.beans.Empresa" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>

<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="ar.com.ospim.util.CuilUtils" %>
<%@ page import="ar.com.ospim.util.ListUtils" %>

<%@ page import="javax.servlet.ServletContext" %>

<%@ page import="ar.com.ospim.global.beans.ContactoElectronico" %>
<%@ page import="ar.com.ospim.global.beans.RamoEmpresa" %>
<%@ page import="ar.com.ospim.global.beans.EntidadCamaraEmpresa" %>
<%@ page import="ar.com.ospim.global.beans.EntidadPadronUnificado" %>
<%@ page import="ar.com.ospim.global.beans.PosicionIva" %>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.EstadoCivil" %>

<%@ page import="ar.com.ospim.crm.WebKeysCrm" %>

<%@ page import="ar.com.empresas.beans.Contacto" %>

<%@ page import="ar.com.ospim.liquidaciones.beans.Prestador" %>

<%@ page import="ar.com.ospim.correspondencia.WebKeysCorrespondencia" %>
<%@ page import="ar.com.ospim.correspondencia.services.EmpresaLiferay" %>
<%@ page import="ar.com.ospim.correspondencia.services.SectorLiferay" %>
<%@ page import="ar.com.ospim.correspondencia.services.EmpresaSectorUsuarioServiceUtil" %>

<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Date" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante" %>
<%@ page import="ar.com.ospim.global.beans.ClaseBase" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.beans.Sector" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteIntegracion" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteAcompanante" %>
<%@ page import="ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteHospital" %>

<%@ taglib uri="http://liferay.com/tld/ui-custom" prefix="liferay-ui-custom" %>
<portlet:defineObjects />
<%
PortalPreferences portalPrefs = PortletPreferencesFactoryUtil.getPortalPreferences(request);

DateFormat dateFormatDateTime = DateFormats.getDateTime(locale, timeZone);

List<Provincia> provincias = (ArrayList<Provincia>) portletSession
.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (provincias == null) {
	provincias = TraeListasServiceUtil.getProvincias();
	portletSession.setAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
	provincias,PortletSession.APPLICATION_SCOPE);	
}

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
PortletSession.APPLICATION_SCOPE);

if (localidades == null || localidades.size()==0) {
	localidades = TraeListasServiceUtil.getLocalidades();
	portletSession.setAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
	localidades,PortletSession.APPLICATION_SCOPE);	
}

/* List<Seccional> seccionales = (ArrayList<Seccional>) portletSession
.getAttribute(WebKeysAfiliados.SECCIONALES_EN_SESSION,
PortletSession.APPLICATION_SCOPE); */

List<Prestador.TipoPrestador> tiposPrestador = (ArrayList<Prestador.TipoPrestador>) portletSession
.getAttribute(WebKeysLiquidaciones.TIPOSPRESTADOR_EN_SESSION,
PortletSession.APPLICATION_SCOPE);

List<ProfesionPrestador> profesionPrestador = (ArrayList<ProfesionPrestador>) portletSession
.getAttribute(WebKeysLiquidaciones.LISTAS_DE_PROFESION_PRESTADOR_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (profesionPrestador == null) {
	profesionPrestador = TraeListasServiceUtil.getProfesionesPrestador();
	portletSession.setAttribute(WebKeysLiquidaciones.LISTAS_DE_PROFESION_PRESTADOR_EN_SESSION ,
	profesionPrestador, PortletSession.APPLICATION_SCOPE);
}

List<EspecialidadPrestador> especialidadPrestador = (ArrayList<EspecialidadPrestador>) portletSession
.getAttribute(WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (especialidadPrestador == null) {
	especialidadPrestador = TraeListasServiceUtil.getEspecialidadesPrestador();
	portletSession.setAttribute(WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
	especialidadPrestador, PortletSession.APPLICATION_SCOPE);
}

List<SubEspecialidadPrestador> subEspecialidadPrestador = (ArrayList<SubEspecialidadPrestador>) portletSession
.getAttribute(WebKeysLiquidaciones.LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (subEspecialidadPrestador == null) {
	subEspecialidadPrestador = TraeListasServiceUtil.getSubEspecialidadesPrestador();
	portletSession.setAttribute(WebKeysLiquidaciones.LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION,
	subEspecialidadPrestador, PortletSession.APPLICATION_SCOPE);
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

//DS


List<Plan> planesPrestadores =
	(List<Plan>) session.getAttribute(WebKeysPrestadores.PLANES_EN_SESSION);

if (planesPrestadores == null) {
	planesPrestadores = TraeListasServiceUtil.getPlanesOspim();
	session.setAttribute(WebKeysPrestadores.PLANES_EN_SESSION, planesPrestadores);
}

List<Provincia> provinciasPrestadores =
	(List<Provincia>) session.getAttribute(WebKeysPrestadores.PROVINCIAS_EN_SESSION);

if (provinciasPrestadores == null) {
	provinciasPrestadores = TraeListasServiceUtil.getProvincias();
	session.setAttribute(WebKeysPrestadores.PROVINCIAS_EN_SESSION, provinciasPrestadores);
}

List<Localidad> localidadesPrestadores =
	(List<Localidad>) session.getAttribute(WebKeysPrestadores.LOCALIDADES_EN_SESSION);

if (localidadesPrestadores == null) {
	localidadesPrestadores = TraeListasServiceUtil.getLocalidades();
	session.setAttribute(WebKeysPrestadores.LOCALIDADES_EN_SESSION, localidadesPrestadores);
}

List<EspecialidadPrestador> especialidadesPrestadores =
	(List<EspecialidadPrestador>) session.getAttribute(WebKeysPrestadores.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION);

if (especialidadesPrestadores == null) {
	especialidadesPrestadores = TraeListasServiceUtil.getEspecialidadesPrestador();
	session.setAttribute(
		WebKeysPrestadores.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
		especialidadesPrestadores
	);
}

List<Nacionalidad> nacionalidades = (ArrayList<Nacionalidad>) portletSession
.getAttribute(WebKeysAfiliados.NACIONALIDADES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (nacionalidades == null) {
	nacionalidades = TraeListasServiceUtil.getNacionalidades();
	portletSession.setAttribute(WebKeysAfiliados.NACIONALIDADES_EN_SESSION,
	nacionalidades,PortletSession.APPLICATION_SCOPE);
}

List<ObraSocialCampo> obrasSocialesAnteriores = (ArrayList<ObraSocialCampo>) portletSession
.getAttribute(WebKeysAfiliados.OBRAS_SOCIALES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (obrasSocialesAnteriores == null) {
	obrasSocialesAnteriores = TraeListasServiceUtil.getObrasSocialesAnteriores();
	portletSession.setAttribute(WebKeysAfiliados.OBRAS_SOCIALES_EN_SESSION,
	obrasSocialesAnteriores, PortletSession.APPLICATION_SCOPE);
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


<script type="text/javascript">

<%
int cont_emp=0;
int cont_sec=0;
int cont_usu=0;

List<EmpresaLiferay> empresasSectoresUsuarios = (ArrayList<EmpresaLiferay>) portletSession
		   .getAttribute(WebKeysCorrespondencia.EMPRESA_SECTOR_USUARIOS_LIFERAY_EN_SESSION,
		   		PortletSession.APPLICATION_SCOPE);

		   if (empresasSectoresUsuarios == null) {
			   empresasSectoresUsuarios = EmpresaSectorUsuarioServiceUtil.getEmpresasSectoresUsuarios();;
		   		portletSession.setAttribute(WebKeysCorrespondencia.EMPRESA_SECTOR_USUARIOS_LIFERAY_EN_SESSION,
		   		empresasSectoresUsuarios,PortletSession.APPLICATION_SCOPE);
		   }


//Empresas
for(EmpresaLiferay empSecUsu : empresasSectoresUsuarios){ %>
	data_<%=cont_emp%> = new Option("<%=empSecUsu.getEmpresa().getName() %>", "<%=empSecUsu.getEmpresa().getOrganizationId()%>");
	<% cont_sec=0;

	// Sectores
	for(SectorLiferay secUsu : empSecUsu.getSectores() ){ %>
		data_<%=cont_emp%>_<%=cont_sec%>=new Option("<%=secUsu.getSector().getName() %>","<%=secUsu.getSector().getUserGroupId()%>");
		<% cont_usu=0;

		// Usuarios
		%>
		// Primer opcion de usuario es 'A todos los usuarios del grupo'
		data_<%=cont_emp%>_<%=cont_sec%>_<%=cont_usu%>=new Option("<%="A todos los usuarios" %>","<%="TODOS"%>");
		<%
		cont_usu++;
		for(User usu : secUsu.getUsuarios() ){
			if(usu.getActive()){%>
				data_<%=cont_emp%>_<%=cont_sec%>_<%=cont_usu%>=new Option("<%=usu.getFullName() %>","<%=usu.getScreenName()%>");
			<%
			cont_usu++;
			}
		}
		cont_sec++;
	}
	cont_emp++;
}

%>
</script>

<script src="/html/js/utils.js" type="text/javascript"></script>
<script src="/html/js/formCheck.js" type="text/javascript"></script>
<script src="/html/js/disableKeys.js" type="text/javascript"></script>
<script src="/html/js/combosDinamicos.js" type="text/javascript"></script>


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