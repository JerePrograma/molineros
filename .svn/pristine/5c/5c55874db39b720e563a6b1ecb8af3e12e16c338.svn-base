package ar.com.ospim.global.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.liferay.portal.SystemException;
import com.liferay.portal.util.PortalUtil;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.empresas.beans.Actividad;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.CategoriaLaboral;
import ar.com.ospim.afiliados.beans.CieDiez;
import ar.com.ospim.afiliados.beans.Direccion;
import ar.com.ospim.afiliados.beans.Documento;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.SituacionRevista;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.beans.TipoAporte;
import ar.com.ospim.afiliados.beans.TipoBono;
import ar.com.ospim.afiliados.reportes.beans.UltimosProcesosSisOld;
import ar.com.ospim.afip.service.AfipServiceImpl;
import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.autorizaciones.beans.DrogaPatologia;
import ar.com.ospim.autorizaciones.beans.EstadosReclamosPrestacionales;
import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.autorizaciones.beans.MotivoExcepcion;
import ar.com.ospim.autorizaciones.beans.OpcionesPrestacion;
import ar.com.ospim.autorizaciones.beans.ReclamosPrestacionalesIntegracion;
import ar.com.ospim.autorizaciones.beans.ReclamosPrestacionalesRevisionEstado;
import ar.com.ospim.autorizaciones.beans.TiposDeGestionReclamosPrestacionales;
import ar.com.ospim.autorizaciones.beans.TiposDeSituacionesMedicas;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.correspondencia.beans.UsuarioCorrespondencia;
import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.AportesMonotributo;
import ar.com.ospim.global.beans.AportesMonotributoClase;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Estado;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.ConvenioNacion;
import ar.com.ospim.global.beans.CuentasNacion;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.EntidadCamaraEmpresa;
import ar.com.ospim.global.beans.EstadoCivil;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.global.beans.ListaConcepto;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Motivo;
import ar.com.ospim.global.beans.Nacionalidad;
import ar.com.ospim.global.beans.ObraSocialCampo;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.Pais;
import ar.com.ospim.global.beans.Parentesco;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.PlanCuentasSSS;
import ar.com.ospim.global.beans.PosicionIva;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.RamoEmpresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.TipoMovExtractoBancario;
import ar.com.ospim.global.beans.TipoPago;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Especialidad;
import ar.com.ospim.liquidaciones.beans.EspecialidadPrestador;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.beans.ProfesionPrestador;
import ar.com.ospim.liquidaciones.beans.SubEspecialidadPrestador;
import ar.com.ospim.liquidaciones.beans.TipoDiscapacidad;
import ar.com.ospim.liquidaciones.beans.TipoNomenclador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceImpl;
import ar.com.ospim.novedades.beans.ArchivoNovedad;
import ar.com.ospim.novedades.beans.TipoNovedad;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Chequera;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.InteresAfip;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.tesoreria.beans.TipoTrxBancaria;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.CentroCosto;

/**
 * Mascara del servicio que da acceso a los datos de la aplicacion (BD).
 */
public class TraeListasServiceUtil {

	private static TraeListasServiceImpl instance = null;

	public static TraeListasServiceImpl getInstance() {
		if (null == instance) {
			instance = new TraeListasServiceImpl();
		}
		return instance;
	}

	public static List<Direccion> getCodPostales(String calle) {
		return getInstance().getCodPostales(calle);
	}

	public static List<Direccion> getDirecciones(String direccion) {
		return getInstance().getDirecciones(direccion);
	}

	public static List<Nacionalidad> getNacionalidades() {
		return getInstance().getNacionalidades();
	}

	public static List<ObraSocialCampo> getObraSocialesAnterires() {
		return getInstance().getObrasSocialesAnteriores();
	}
	
	public static List<OpcionesPrestacion> getOpcionesPrestacion (String codigoPrestacion ) {
		return getInstance().getOpcionesPrestacion(codigoPrestacion ) ;
	}

	
	public static List<EstadosReclamosPrestacionales> getEstadosReclamos() {
		return getInstance().getEstadosReclamosPrestacionales();
	}

	public static List<TiposDeGestionReclamosPrestacionales> getTiposGestionReclamosPrestacionales() {
		return getInstance().getTipoGestionclamosPrestacionales();
	}	
	
	public static List<ReclamosPrestacionalesRevisionEstado> getReclamosPrestacionalesRevisionEstado() {
		return getInstance().getReclamosPrestacionalesRevisionEstado();
	}	
	
	public static List<ReclamosPrestacionalesIntegracion> getReclamosPrestacionalesIntegracion() {
		return getInstance().getReclamosPrestacionalesIntegracion();
	}	
	
	public static List<TiposDeSituacionesMedicas> getTipoSituacionesMedicas() {
		return getInstance().getTipoSituacionesMedicas();
	}	
	
	@Deprecated
	public static List<Provincia> getProvincias() {
		return getInstance().getProvincias();
	}

	@SuppressWarnings("unchecked")
	public static List<Provincia> getProvincias(PortletRequest portletRequest) {
		List<Provincia> localidades = (List<Provincia>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.PROVINCIAS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (localidades == null) {
			localidades = getInstance().getProvincias();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.PROVINCIAS_EN_SESSION, localidades,
					PortletSession.APPLICATION_SCOPE);
		}
		return localidades;
	}
	
	@SuppressWarnings("unchecked")
	public static Provincia getProvincia(int id_provincia, PortletRequest portletRequest) {
		List<Provincia> provincias = (List<Provincia>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.PROVINCIAS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (provincias == null) {
			provincias = getInstance().getProvincias();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.PROVINCIAS_EN_SESSION, provincias,
					PortletSession.APPLICATION_SCOPE);
		}
		for(Provincia prov:provincias){
			if(prov.getId()==id_provincia){
				return prov;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Pais> getPaises(PortletRequest portletRequest) {
		List<Pais> paises = (List<Pais>) portletRequest.getPortletSession()
				.getAttribute(WebKeysAfiliados.PAISES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (paises == null) {
			paises = getInstance().getPaises();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.PAISES_EN_SESSION, paises,
					PortletSession.APPLICATION_SCOPE);
		}
		return paises;
	}

	@Deprecated
	public static List<Localidad> getLocalidades() {
		return getInstance().getLocalidades();
	}

	@SuppressWarnings("unchecked")
	
	public static List<Localidad> getLocalidades(PortletRequest portletRequest) {
		List<Localidad> localidades = (List<Localidad>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.LOCALIDADES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (localidades == null) {
			localidades = getInstance().getLocalidades();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.LOCALIDADES_EN_SESSION, localidades,
					PortletSession.APPLICATION_SCOPE);
		}
		return localidades;
	}

	
@SuppressWarnings("unchecked")
	
	public static List<OpcionesPrestacion> getOpcionesPrestacion (PortletRequest portletRequest) {
		List<OpcionesPrestacion> listaOpciones = (List<OpcionesPrestacion>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAutorizaciones.ESTADOS_RECLAMOS_PRESTACIONES_EN_SESION,
						PortletSession.APPLICATION_SCOPE);

		if (listaOpciones == null) {
			listaOpciones = getInstance().getOpcionesPrestacion("23101") ;
			portletRequest.getPortletSession().setAttribute(
					WebKeysAutorizaciones.ESTADOS_RECLAMOS_PRESTACIONES_EN_SESION, listaOpciones ,
					PortletSession.APPLICATION_SCOPE);
		}
		return listaOpciones ;
	}

	
	
	@SuppressWarnings("unchecked")
	
	public static List<EstadosReclamosPrestacionales> getEstadosReclamos(PortletRequest portletRequest) {
		List<EstadosReclamosPrestacionales> listaestados = (List<EstadosReclamosPrestacionales>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAutorizaciones.ESTADOS_RECLAMOS_PRESTACIONES_EN_SESION,
						PortletSession.APPLICATION_SCOPE);

		if (listaestados == null) {
			listaestados = getInstance().getEstadosReclamosPrestacionales();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAutorizaciones.ESTADOS_RECLAMOS_PRESTACIONES_EN_SESION, listaestados ,
					PortletSession.APPLICATION_SCOPE);
		}
		return listaestados ;
	}
	
	@SuppressWarnings("unchecked")
	
	public static List<TiposDeGestionReclamosPrestacionales> getTiposGestionReclamosPrestacionales(PortletRequest portletRequest) {
		List<TiposDeGestionReclamosPrestacionales> listatiposgestionreclamos = (List<TiposDeGestionReclamosPrestacionales>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAutorizaciones.TIPOS_GESTION_RECLAMOS_PRESTACIONES_EN_SESION,
						PortletSession.APPLICATION_SCOPE);

		if (listatiposgestionreclamos== null) {
			listatiposgestionreclamos= getInstance().getTipoGestionclamosPrestacionales();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAutorizaciones.TIPOS_GESTION_RECLAMOS_PRESTACIONES_EN_SESION, listatiposgestionreclamos,
					PortletSession.APPLICATION_SCOPE);
		}
		return listatiposgestionreclamos;
	}

	
	
	public static List<Localidad> getLocalidadesByProvincia(int id_provincia) {
		return getInstance().getLocalidades();
	}

    public static List<Localidad> getLocalidadesPorProvincia(Integer idProvincia, Integer idProvinciaSSS) {
		return getInstance().getLocalidadesPorProvincia(idProvincia, idProvinciaSSS);
	}

	public static List<Localidad> getLocalidadesPorCP(int cod_postal) {
		return getInstance().getLocalidadesPorCodPostal(cod_postal);
	}
	
	@SuppressWarnings("unchecked")
	public static Localidad getLocalidad(int id_localidad, PortletRequest portletRequest) {
		List<Localidad> localidades = (List<Localidad>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.LOCALIDADES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (localidades == null) {
			localidades = getInstance().getLocalidades();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.LOCALIDADES_EN_SESSION, localidades,
					PortletSession.APPLICATION_SCOPE);
		}		
		for(Localidad loc: localidades){		
			if(loc.getId()==id_localidad){
				return loc;
			}
			
		}
		return null;
		
	}
	@SuppressWarnings("unchecked")
	public static List<Feriado> getFeriados(PortletRequest portletRequest){
		List<Feriado> feriados = null;
		if(null!=portletRequest){
			feriados = (List<Feriado>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysGlobal.FERIADOS,
						PortletSession.APPLICATION_SCOPE);
		}

		if (feriados == null) {
			feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
			if(null!=portletRequest){
				portletRequest.getPortletSession().setAttribute(
						WebKeysGlobal.FERIADOS, feriados,
						PortletSession.APPLICATION_SCOPE);
			}
		}
		return feriados;		
	}
	
	@SuppressWarnings("unchecked")
	public static List<Feriado> getFeriados(HttpServletRequest portletRequest){
		List<Feriado> feriados = null;
		if(null!=portletRequest){
			feriados = (List<Feriado>) portletRequest.getSession().getAttribute(
						WebKeysGlobal.FERIADOS);
		}

		if (feriados == null) {
			feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
			if(null!=portletRequest){
				portletRequest.getSession().setAttribute(
						WebKeysGlobal.FERIADOS, feriados);
			}
		}
		return feriados;		
	}
	

	public static List<Seccional> getSeccionales() {
		return getInstance().getSeccionales();
	}
	
	public static List<Actividad> getActividades(PortletRequest portletRequest) {
		@SuppressWarnings("unchecked")
		List<Actividad> actividades = (List<Actividad>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysEmpresas.ACTIVIDADES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (actividades == null) {
			actividades = getInstance().getActividades();
			portletRequest.getPortletSession().setAttribute(
					WebKeysEmpresas.ACTIVIDADES_EN_SESSION, actividades,
					PortletSession.APPLICATION_SCOPE);
		}
		return actividades;
	}

	public static List<Delegacion> getDelegaciones() {
		return getInstance().getDelegaciones();
	}

	public static List<Seccional> getSeccionalesFarmacia() {
		return getInstance().getSeccionalesFarmacia();
	}

	@SuppressWarnings("unchecked")
	public static List<Seccional> getSeccionales(PortletRequest portletRequest) {
		List<Seccional> seccionales = (List<Seccional>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.SECCIONALES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (seccionales == null) {
			seccionales = getInstance().getSeccionales();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.SECCIONALES_EN_SESSION, seccionales,
					PortletSession.APPLICATION_SCOPE);
		}
		return seccionales;
	}

	@SuppressWarnings("unchecked")
	public static List<Delegacion> getDelegaciones(PortletRequest portletRequest) {
		List<Delegacion> delegaciones = (List<Delegacion>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.DELEGACIONES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (delegaciones == null) {
			delegaciones = getInstance().getDelegaciones();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.DELEGACIONES_EN_SESSION, delegaciones,
					PortletSession.APPLICATION_SCOPE);
		}
		return delegaciones;
	}

	@SuppressWarnings("unchecked")
	public static List<Seccional> getSeccionalesFarmacia(
			PortletRequest portletRequest) {
		List<Seccional> seccionales = (List<Seccional>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysFarmacia.SECCIONALES_EN_SESSION_FARMACIA,
						PortletSession.APPLICATION_SCOPE);

		if (seccionales == null) {
			seccionales = getInstance().getSeccionalesFarmacia();
			portletRequest.getPortletSession().setAttribute(
					WebKeysFarmacia.SECCIONALES_EN_SESSION_FARMACIA,
					seccionales, PortletSession.APPLICATION_SCOPE);
		}
		return seccionales;
	}

	public static List<Prestacion> getPrestacionesReintegro() {
		return getInstance().getPrestacionesReintegro();
	}

	public static List<Prestador> getPrestadores() {
		return getInstance().getPrestadores();
	}

	public static List<PrestadorLugarAtencion> getPrestadoresLugarAtencion() {
		return getInstance().getPrestadoresLugarAtencion();
	}

	public static List<Empresa> getEmpleadores(String cuit, String razon) {
		return getInstance().getEmpleadores(cuit, razon);
	}

	public static List<Empresa> getEmpleadores(String cuit, String razon,
			String sucu) {
		return getInstance().getEmpleadores(cuit, razon, sucu);
	}

	public static List<Empresa> getEmpleadoresDeOP(String cuit, String razon,
			String sucu, int id_prestador) {
		return getInstance()
				.getEmpleadoresDeOP(cuit, razon, sucu, id_prestador);
	}

	public static List<Empresa> getEmpleadores(String cuit, String razon,
			int pageStart, int pageEnd) {
		return getInstance().getEmpleadores(cuit, razon, pageStart, pageEnd);
	}
	
	public static List<Empresa> getEmpleadoresAfip(String cuit, String razon,
			String sucu) {
		return getInstance().getEmpleadoresAFIP(cuit, razon, sucu);
	}
	
	public static List<Farmacia> getEmpresasFarmacia(String cuit
			) {
		return getInstance().getEmpleadoresFarmacia(cuit);
	}

	public static List<ObraSocialCampo> getObrasSocialesAnteriores() {
		return getInstance().getObrasSocialesAnteriores();
	}

	@Deprecated
	public static List<TipoAporte> getTiposAporte() {
		return getInstance().getTiposAporte();
	}

	@SuppressWarnings("unchecked")
	public static List<TipoAporte> getTiposAporte(PortletRequest portletRequest) {
		List<TipoAporte> tiposAporte = (List<TipoAporte>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.TIPOS_APORTE_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (tiposAporte == null) {
			tiposAporte = getInstance().getTiposAporte();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.TIPOS_APORTE_EN_SESSION, tiposAporte,
					PortletSession.APPLICATION_SCOPE);
		}
		return tiposAporte;
	}

	@SuppressWarnings("unchecked")
	public static List<TipoPago> getTiposPagoContratos(
			PortletRequest portletRequest) {
		List<TipoPago> tiposPago = (List<TipoPago>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.TIPOS_PAGO_CONVENIOS_PREST_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (tiposPago == null) {
			tiposPago = getInstance().getTiposPagoContratos();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.TIPOS_PAGO_CONVENIOS_PREST_EN_SESSION,
					tiposPago, PortletSession.APPLICATION_SCOPE);
		}
		return tiposPago;
	}

	@Deprecated
	public static List<TercerizadoraServicio> getTercerizadoraServicio() {
		return getInstance().getTercerizadoraServicios();
	}

	public static List<TercerizadoraServicio> getTercerizadoraServicio(
			PortletRequest portletRequest) {
		
		List<TercerizadoraServicio> tercerizadoraServicios = (List<TercerizadoraServicio>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.TERCERIZADORAS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (tercerizadoraServicios == null) {
			tercerizadoraServicios =getInstance().getTercerizadoraServicios();
			
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.TERCERIZADORAS_EN_SESSION, tercerizadoraServicios,
					PortletSession.APPLICATION_SCOPE);
		}

		return tercerizadoraServicios;
	}

	public static List<TercerizadoraServicio> getTercerizadorasPorConvenios() {
		return getInstance().getTercerizadoraServiciosPorConvenios();
	}

	public static List<Documento> getDocumentos() {
		return getInstance().getDocumentos();
	}

	public static List<Documento> getDocumentosActualizanAfiliado() {
		return getInstance().getDocumentosActualizanAfiliado();
	}

	@SuppressWarnings("unchecked")
	public static List<Documento> getDocumentosDiscapacidad(
			PortletRequest portletRequest) {
		List<Documento> documentos = (List<Documento>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysGlobal.DOCUMENTOS_DISCAPACIDAD,
						PortletSession.APPLICATION_SCOPE);
		if (documentos == null) {
			documentos = getInstance().getDocumentosDiscapacidad();
			portletRequest.getPortletSession().setAttribute(
					WebKeysGlobal.DOCUMENTOS_DISCAPACIDAD, documentos,
					PortletSession.APPLICATION_SCOPE);
		}
		return documentos;
	}

	@SuppressWarnings("unchecked")
	public static List<CieDiez> getListadoCieDiez(PortletRequest portletRequest) {
		List<CieDiez> cie = (List<CieDiez>) portletRequest.getPortletSession()
				.getAttribute(WebKeysGlobal.DOCUMENTOS_CIE,
						PortletSession.APPLICATION_SCOPE);
		if (cie == null) {
			cie = getInstance().getTraeListadoCieDiez();
			portletRequest.getPortletSession().setAttribute(
					WebKeysGlobal.DOCUMENTOS_CIE, cie,
					PortletSession.APPLICATION_SCOPE);
		}
		return cie;
	}

	public static List<Documento> getDocumentosDiscapacidad() {
		List<Documento> documentos = getInstance().getDocumentosDiscapacidad();
		return documentos != null ? documentos : new ArrayList<Documento>();
	}

	public static List<CieDiez> getListadoCieDiez() {
		List<CieDiez> cie = getInstance().getTraeListadoCieDiez();
		return cie != null ? cie : new ArrayList<CieDiez>();
	}

	public static List<MotivoBaja> getMotivosBaja() {
		return getInstance().getMotivosBaja();
	}

	@SuppressWarnings("unchecked")
	public static List<MotivoBaja> getMotivosBaja(PortletRequest portletRequest) {
		List<MotivoBaja> motivos = (List<MotivoBaja>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (motivos == null) {
			motivos = getInstance().getMotivosBaja();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION, motivos,
					PortletSession.APPLICATION_SCOPE);
		}
		return motivos;
	}

	@SuppressWarnings("unchecked")
	public static List<Afiliado> getListaBusquedaAfiliadosSession(
			PortletRequest portletRequest) {
		List<Afiliado> afiliados = (List<Afiliado>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (afiliados == null) {
			afiliados = new ArrayList<Afiliado>();
		}
		return afiliados;
	}

	public static List<Motivo> getMotivosDebito() {
		return getInstance().getMotivosDebito();
	}

	@SuppressWarnings("unchecked")
	public static List<Motivo> getMotivosDebito(PortletRequest portletRequest) {
		List<Motivo> motivos = (List<Motivo>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.MOTIVOS_DEBITO_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (motivos == null) {
			motivos = getInstance().getMotivosDebito();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.MOTIVOS_DEBITO_EN_SESSION, motivos,
					PortletSession.APPLICATION_SCOPE);
		}
		return motivos;
	}

//	public static List<CategoriaLaboral> getCategoriasLaborales() {
//		return getInstance().getCategoriasLaborales();
//	}

//	public static List<SituacionRevista> getSituacionRevista() {
//		return getInstance().getSituacionRevista();
//	}

	public static List<SituacionRevista> getSituacionRevista(PortletRequest portletRequest) {
		List<SituacionRevista> situsRevista = (List<SituacionRevista>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.SITUACIONES_REVISTA_EMPRESA_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (situsRevista == null) {
			situsRevista = getInstance().getSituacionRevista();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.SITUACIONES_REVISTA_EMPRESA_EN_SESSION, situsRevista,
					PortletSession.APPLICATION_SCOPE);
		}
		return situsRevista;
	}
	
	public static List<CategoriaLaboral> getCategoriasLaborales(PortletRequest portletRequest) {
		List<CategoriaLaboral> categoriasLab = (List<CategoriaLaboral>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.CATEGORIAS_EMPRESA_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (categoriasLab == null) {
			categoriasLab = getInstance().getCategoriasLaborales();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.CATEGORIAS_EMPRESA_EN_SESSION, categoriasLab,
					PortletSession.APPLICATION_SCOPE);
		}
		return categoriasLab;
	}
	
	@Deprecated
	public static List<Plan> getPlanes() {
		return getInstance().getPlanes();
	}

	public static List<Plan> getPlanes(PortletRequest portletRequest) {
		HttpSession session = PortalUtil.getHttpServletRequest(portletRequest)
				.getSession();
		List<Plan> planes = getPlanes(session);
		return planes;
	}

	@SuppressWarnings("unchecked")
	public static List<Plan> getPlanes(HttpSession session) {
		List<Plan> planes = (List<Plan>) session
				.getAttribute(WebKeysAfiliados.PLANES_EN_SESSION);

		if (planes == null) {
			planes = getInstance().getPlanes();
			session.setAttribute(WebKeysAfiliados.PLANES_EN_SESSION, planes);
		}
		return planes;
	}
	
	public static List<Plan> getPlanesMolineros() {
		return getInstance().getPlanesMolineros();
	}

	public static List<Plan> getPlanesOspim() {
		return getInstance().getPlanesOspim();
	}
	
	public static List<Plan> getPlanesSoloOspim() {
		return getInstance().getPlanesSoloOspim();
	}
	
	@SuppressWarnings("unchecked")
	public static List<CuentaBancaria> getCtasBcrias(
			PortletRequest portletRequest) {
		List<CuentaBancaria> ctas = (List<CuentaBancaria>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (ctas == null) {
			ctas = getInstance().getCtasBcrias();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION, ctas,
					PortletSession.APPLICATION_SCOPE);
		}
		return ctas;
	}
	
	@SuppressWarnings("unchecked")
	public static Banco getBancoPorIdCtaBcria(
			PortletRequest portletRequest,int id_cta) {
		List<CuentaBancaria> ctas = (List<CuentaBancaria>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (ctas == null) {
			ctas = getInstance().getCtasBcrias();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION, ctas,
					PortletSession.APPLICATION_SCOPE);
		}
		Banco banco=null;
		for(CuentaBancaria cta:ctas){
			if(cta.getId_cuenta_bcria()==id_cta){
				banco=cta.getBanco();
			}
		}
		
		
		return banco;
	}

	@Deprecated
	public static List<CuentaBancaria> getCtasBcrias() {
		return getInstance().getCtasBcrias();
	}
	
	@SuppressWarnings("unchecked")
	public static List<TipoTrxBancaria> getTiposTrxBancarias(
			PortletRequest portletRequest, int entidad) {
		List<TipoTrxBancaria> tipos = null;
		if (entidad == WebKeysGlobal.AMTIMA) {
			tipos = (List<TipoTrxBancaria>) portletRequest.getPortletSession()
					.getAttribute(
							WebKeysTesoreria.TIPOS_TRX_BCRIA_AMTIMA_EN_SESSION,
							PortletSession.PORTLET_SCOPE);

		} else if (entidad == WebKeysGlobal.OSPIM) {
			tipos = (List<TipoTrxBancaria>) portletRequest.getPortletSession()
					.getAttribute(WebKeysTesoreria.TIPOS_TRX_BCRIA_EN_SESSION,
							PortletSession.PORTLET_SCOPE);
		} else if (entidad == WebKeysGlobal.UOMA) {
			tipos = (List<TipoTrxBancaria>) portletRequest.getPortletSession()
					.getAttribute(WebKeysTesoreria.TIPOS_TRX_BCRIA_EN_SESSION,
							PortletSession.PORTLET_SCOPE);
		}

		if (tipos == null) {
			tipos = getInstance().getTiposTrxBancarias(entidad);
			if (entidad == WebKeysGlobal.AMTIMA) {
				portletRequest.getPortletSession().setAttribute(
						WebKeysTesoreria.TIPOS_TRX_BCRIA_AMTIMA_EN_SESSION,
						tipos, PortletSession.PORTLET_SCOPE);
			} else {
				portletRequest.getPortletSession().setAttribute(
						WebKeysTesoreria.TIPOS_TRX_BCRIA_EN_SESSION, tipos,
						PortletSession.PORTLET_SCOPE);
			}
		}
		return tipos;
	}

	public static List<TipoMovBcrio> getTipoMovBcrio(
			PortletRequest portletRequest, int entidad) {
		List<TipoMovBcrio> tipos = null;
		tipos = getInstance().getTipoMovBcrio(new Date(), entidad);
		if (entidad == WebKeysGlobal.AMTIMA) {
			portletRequest.setAttribute(
					WebKeysTesoreria.TIPOS_MOV_BCRIO_AMTIMA_EN_REQUEST, tipos);
		} else if (entidad == WebKeysGlobal.OSPIM) {
			portletRequest.setAttribute(
					WebKeysTesoreria.TIPOS_MOV_BCRIO_EN_REQUEST, tipos);
		} else if (entidad == WebKeysGlobal.UOMA) {
			portletRequest.setAttribute(
					WebKeysTesoreria.TIPOS_MOV_BCRIO_EN_REQUEST, tipos);
		}
		return tipos;
	}

	public static List<TipoMovBcrio> getTipoMovBcrio(
			RenderRequest renderRequest, Date fecha, int entidad) {
		if (fecha == null) {
			return getTipoMovBcrio(renderRequest, entidad);
		}
		List<TipoMovBcrio> tipos = getInstance()
				.getTipoMovBcrio(fecha, entidad);
		if (entidad == WebKeysGlobal.AMTIMA) {
			renderRequest.setAttribute(
					WebKeysTesoreria.TIPOS_MOV_BCRIO_AMTIMA_EN_REQUEST, tipos);
		} else {
			renderRequest.setAttribute(
					WebKeysTesoreria.TIPOS_MOV_BCRIO_EN_REQUEST, tipos);
		}
		return tipos;
	}

	public static List<TipoMovBcrio> getTipoMovBcrio(Date fechaDesde,
			Date fechaHasta, int entidad) {
		return getInstance().getTipoMovBcrio(fechaDesde, fechaHasta, entidad);
	}

	@SuppressWarnings("unchecked")
	public static List<Chequera> getChequeras(PortletRequest portletRequest) {
		List<Chequera> chequeras = (List<Chequera>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysTesoreria.CHEQUERAS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (chequeras == null) {
			chequeras = getInstance().getChequeras();
			portletRequest.getPortletSession().setAttribute(
					WebKeysTesoreria.CHEQUERAS_EN_SESSION, chequeras,
					PortletSession.APPLICATION_SCOPE);
		}
		return chequeras;
	}

	@SuppressWarnings("unchecked")
	public static List<RamoEmpresa> getRamosEmpresa(
			PortletRequest portletRequest) {
		List<RamoEmpresa> ramos = (List<RamoEmpresa>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.RAMOS_EMPRESA_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (ramos == null) {
			ramos = getInstance().getRamosEmpresa();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.RAMOS_EMPRESA_EN_SESSION, ramos,
					PortletSession.APPLICATION_SCOPE);
		}
		return ramos;
	}

	@SuppressWarnings("unchecked")
	public static List<PosicionIva> getPosicionesIva(
			PortletRequest portletRequest) throws Exception {
		List<PosicionIva> posicionesIva = (List<PosicionIva>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.POSICIONESIVA_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (posicionesIva == null) {
			posicionesIva = PosicionIvaServiceImpl.getInstance()
					.getPosicionesIva();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.POSICIONESIVA_EN_SESSION, posicionesIva,
					PortletSession.APPLICATION_SCOPE);
		}
		return posicionesIva;
	}

	@SuppressWarnings("unchecked")
	public static List<EntidadCamaraEmpresa> getEntidadesCamaraEmpresa(
			PortletRequest portletRequest) throws Exception {
		List<EntidadCamaraEmpresa> entidades = (List<EntidadCamaraEmpresa>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysAfiliados.ENTIDADESCAMARAEMPRESA_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (entidades == null) {
			entidades = EntidadCamaraEmpresaServiceImpl.getInstance()
					.getEntidadesCamaraEmpresa();
			portletRequest.getPortletSession().setAttribute(
					WebKeysAfiliados.ENTIDADESCAMARAEMPRESA_EN_SESSION,
					entidades, PortletSession.APPLICATION_SCOPE);
		}
		return entidades;
	}

	@SuppressWarnings("unchecked")
	public static List<Prestador.TipoPrestador> getTiposPrestador(
			PortletRequest portletRequest) throws Exception {
		List<Prestador.TipoPrestador> entidades = (List<Prestador.TipoPrestador>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.TIPOSPRESTADOR_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (entidades == null) {
			entidades = PrestadorServiceImpl.getInstance().getTipos();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.TIPOSPRESTADOR_EN_SESSION, entidades,
					PortletSession.APPLICATION_SCOPE);
		}
		return entidades;
	}

	@SuppressWarnings("unchecked")
	public static List<InteresAfip> getInteresesAfip(
			PortletRequest portletRequest) throws Exception {
		List<InteresAfip> entidades = null;
		if (null != portletRequest) {
			entidades = (List<InteresAfip>) portletRequest.getPortletSession()
					.getAttribute(WebKeysTesoreria.INTERESES_AFIP_EN_SESSION,
							PortletSession.APPLICATION_SCOPE);

			if (entidades == null) {
				entidades = AfipServiceImpl.getInstance().getIntereses();
				portletRequest.getPortletSession().setAttribute(
						WebKeysTesoreria.INTERESES_AFIP_EN_SESSION, entidades,
						PortletSession.APPLICATION_SCOPE);
			}
		}else{
			entidades = AfipServiceImpl.getInstance().getIntereses();			
		}
		return entidades;
	}
	
	public static List<InteresAfip> getInteresesAfip() throws Exception {
		List<InteresAfip> entidades = null;
		
		entidades = AfipServiceImpl.getInstance().getIntereses();			
		
		return entidades;
	}

	public static List<String> getUsuariosAltaReintegros() {
		return getInstance().getUsuariosAltaReintegros();
	}

	public static List<String> getUsuariosAltaReintegrosFarmacia() {
		return getInstance().getUsuariosAltaReintegrosFarmacia();
	}

	@SuppressWarnings("unchecked")
	public static List<Banco> getBancos(PortletRequest portletRequest)
			throws Exception {
		List<Banco> entidades = (List<Banco>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysTesoreria.BANCOS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (entidades == null) {
			entidades = getInstance().getBancos();
			portletRequest.getPortletSession().setAttribute(
					WebKeysTesoreria.BANCOS_EN_SESSION, entidades,
					PortletSession.APPLICATION_SCOPE);
		}
		return entidades;
	}

	@Deprecated
	public static Set<CuentasNacion> getCuentasNac() {
		return getInstance().getCuentasNac();
	}

	@SuppressWarnings("unchecked")
	public static Set<CuentasNacion> getCuentasNac(PortletRequest portletRequest) {
		Set<CuentasNacion> cuentasNacion = (Set<CuentasNacion>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysTesoreria.CUENTAS_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (cuentasNacion == null) {
			cuentasNacion = getInstance().getCuentasNac();
			portletRequest.getPortletSession().setAttribute(
					WebKeysTesoreria.CUENTAS_EN_SESSION, cuentasNacion,
					PortletSession.APPLICATION_SCOPE);
		}
		return cuentasNacion;
	}

	@Deprecated
	public static List<ConvenioNacion> getConvenioNac() {
		return getInstance().getConvenioNac();
	}

	@SuppressWarnings("unchecked")
	public static List<ConvenioNacion> getConvenioNac(
			PortletRequest portletRequest) {
		List<ConvenioNacion> convenioNacion = (List<ConvenioNacion>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysTesoreria.CONVENIO_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (convenioNacion == null) {
			convenioNacion = getInstance().getConvenioNac();
			portletRequest.getPortletSession().setAttribute(
					WebKeysTesoreria.CONVENIO_EN_SESSION, convenioNacion,
					PortletSession.APPLICATION_SCOPE);
		}
		return convenioNacion;
	}

	public static List<Banco> getBancos() throws Exception {
		return getInstance().getBancos();
	}

	@SuppressWarnings("unchecked")
	public static List<Cheque.Estado> getEstadosCheque(
			PortletRequest portletRequest) {
		List<Cheque.Estado> entidades = (List<Cheque.Estado>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.ESTADOS_CHEQUE_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (entidades == null) {
			entidades = ChequeServiceUtil.getEstadosCheque();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.ESTADOS_CHEQUE_EN_SESSION, entidades,
					PortletSession.APPLICATION_SCOPE);
		}
		return entidades;
	}

	public static List<Pagare.Estado> getEstadosPagare(
			PortletRequest portletRequest) {
		List<Pagare.Estado> entidades = (List<Pagare.Estado>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.ESTADOS_PAGARE_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (entidades == null) {
			entidades = PagareServiceUtil.getEstadosPagare();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.ESTADOS_PAGARE_EN_SESSION, entidades,
					PortletSession.APPLICATION_SCOPE);
		}
		return entidades;
	}

	public static Cheque.Estado getEstadoChequeEmitido(
			PortletRequest portletRequest) {
		List<Cheque.Estado> estados = TraeListasServiceUtil
				.getEstadosCheque(portletRequest);
		for (Cheque.Estado e : estados) {
			if (e.getId() == Cheque.Estado.EMITIDO) {
				return e;
			}
		}
		return null;
	}
	
	public static Cheque.Estado getEstadoChequeEntregadoTerceros(
			PortletRequest portletRequest) {
		List<Cheque.Estado> estados = TraeListasServiceUtil
				.getEstadosCheque(portletRequest);
		for (Cheque.Estado e : estados) {
			if (e.getId() == Cheque.Estado.ENTREGADO_A_TERCEROS) {
				return e;
			}
		}
		return null;
	}

	public static Estado getEstadoChequeCargado(RenderRequest renderRequest) {
		List<Cheque.Estado> estados = TraeListasServiceUtil
				.getEstadosCheque(renderRequest);
		for (Cheque.Estado e : estados) {
			if (e.getId() == Cheque.Estado.CARGADO) {
				return e;
			}
		}
		return null;
	}

	public static Pagare.Estado getEstadoPagareCargado(
			RenderRequest renderRequest) {
		List<Pagare.Estado> estados = TraeListasServiceUtil
				.getEstadosPagare(renderRequest);
		for (Pagare.Estado e : estados) {
			if (e.getId() == Pagare.Estado.CARGADO) {
				return e;
			}
		}
		return null;
	}

	public static Estado getEstadoChequeRecibido(RenderRequest renderRequest) {
		List<Cheque.Estado> estados = TraeListasServiceUtil
				.getEstadosCheque(renderRequest);
		for (Cheque.Estado e : estados) {
			if (e.getId() == Cheque.Estado.RECIBIDO) {
				return e;
			}
		}
		return null;
	}

	public static Estado getEstadoChequeSustituido(RenderRequest renderRequest) {
		List<Cheque.Estado> estados = TraeListasServiceUtil
				.getEstadosCheque(renderRequest);
		for (Cheque.Estado e : estados) {
			if (e.getId() == Cheque.Estado.SUSTITUIDO) {
				return e;
			}
		}
		return null;
	}

	public static Estado getEstadoChequeRechazado(RenderRequest renderRequest) {
		List<Cheque.Estado> estados = TraeListasServiceUtil
				.getEstadosCheque(renderRequest);
		for (Cheque.Estado e : estados) {
			if (e.getId() == Cheque.Estado.RECHAZADO) {
				return e;
			}
		}
		return null;
	}

	public static Estado getEstadoChequeDepositado(RenderRequest renderRequest) {
		List<Cheque.Estado> estados = TraeListasServiceUtil
				.getEstadosCheque(renderRequest);
		for (Cheque.Estado e : estados) {
			if (e.getId() == Cheque.Estado.DEPOSITADO) {
				return e;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Efectivo.Estado> getEstadosEfectivo(
			PortletRequest portletRequest) {
		List<Efectivo.Estado> entidades = (List<Efectivo.Estado>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.ESTADOS_EFECTIVO_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (entidades == null) {
			entidades = getInstance().getEstadosEfectivo();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.ESTADOS_EFECTIVO_EN_SESSION,
					entidades, PortletSession.APPLICATION_SCOPE);
		}
		return entidades;
	}

	public static Efectivo.Estado getEstadoEfectivoRecibido(
			PortletRequest renderRequest) {
		List<Efectivo.Estado> estados = TraeListasServiceUtil
				.getEstadosEfectivo(renderRequest);
		for (Efectivo.Estado e : estados) {
			if (e.getId() == Efectivo.Estado.RECIBIDO) {
				return e;
			}
		}
		return null;
	}

	public static ar.com.ospim.global.beans.Efectivo.Estado getEstadoEfectivoDepositado(
			RenderRequest renderRequest) {
		List<Efectivo.Estado> estados = TraeListasServiceUtil
				.getEstadosEfectivo(renderRequest);
		for (Efectivo.Estado e : estados) {
			if (e.getId() == Efectivo.Estado.DEPOSITADO) {
				return e;
			}
		}
		return null;
	}

	public static List<TipoMovExtractoBancario> getTiposMovExtractoBancario() {
		List<TipoMovExtractoBancario> ret = getInstance()
				.getTiposMovExtractoBancario();
		return ret;
	}

	public static List<Seccional> getSeccionales(Integer id, String sucursal,
			String cuit) {
		return getInstance().getSeccionales(id, sucursal, cuit);
	}

	public static List<Concepto> getConceptoEgresos(
			PortletRequest renderRequest, Date fecha, int entidad) {
		if (entidad == WebKeysGlobal.AMTIMA) {
			List<Concepto> entidades = (List<Concepto>) renderRequest
					.getPortletSession().getAttribute(
							WebKeysLiquidaciones.CONCEPTOS_EGRESOS_AMTIMA,
							PortletSession.APPLICATION_SCOPE);

			if (entidades == null) {
				entidades = getInstance().getConceptoEgreso(fecha, entidad);
				renderRequest.getPortletSession().setAttribute(
						WebKeysLiquidaciones.CONCEPTOS_EGRESOS_AMTIMA,
						entidades, PortletSession.APPLICATION_SCOPE);
			}
			return entidades;
		} else if (entidad == WebKeysGlobal.UOMA
				|| entidad == WebKeysGlobal.OSPIM) {
			List<Concepto> entidades = (List<Concepto>) renderRequest
					.getPortletSession().getAttribute(
							WebKeysLiquidaciones.CONCEPTOS_EGRESOS,
							PortletSession.PORTLET_SCOPE);

			if (entidades == null) {
				entidades = getInstance().getConceptoEgreso(fecha, entidad);
				renderRequest.getPortletSession().setAttribute(
						WebKeysLiquidaciones.CONCEPTOS_EGRESOS, entidades,
						PortletSession.APPLICATION_SCOPE);
			}
			return entidades;
		} else {
			return getConceptoEgresos(fecha, entidad);
		}
	}

	public static List<Concepto> getConceptoEgresos(Date fecha, int entidad) {
		return getInstance().getConceptoEgreso(fecha, entidad);
	}

	public static List<Concepto> getConceptoEgresos(HttpSession session,
			Date fecha, int entidad) {
		List<Concepto> entidades = getInstance().getConceptoEgreso(fecha,
				entidad);
		session.setAttribute(WebKeysLiquidaciones.CONCEPTOS_EGRESOS, entidades);
		return entidades;
	}

	public static List<Concepto> getConceptoIngreso(
			PortletRequest renderRequest, int entidad) {
		List<Concepto> entidades = getInstance().getConceptoIngreso(new Date(),
				entidad);
		renderRequest.getPortletSession().setAttribute(
				WebKeysLiquidaciones.CONCEPTOS_INGRESO, entidades,
				PortletSession.APPLICATION_SCOPE);
		return entidades;
	}
	
	public static List<Concepto> getConceptoIngreso(
			PortletRequest renderRequest, String cuit, String sucu, int idSeccional, int entidad) {
		List<Concepto> entidades = null;
		if(entidad==WebKeysGlobal.UOMA){
				entidades=getInstance().getConceptoIngreso(new Date(), cuit, sucu, idSeccional,
				entidad);
		}else{
			entidades=getInstance().getConceptoIngreso(new Date(),entidad);
		}
		renderRequest.getPortletSession().setAttribute(
				WebKeysLiquidaciones.CONCEPTOS_INGRESO, entidades,
				PortletSession.APPLICATION_SCOPE);
		return entidades;
	}

	public static List<Concepto> getConceptoIngreso(
			PortletRequest renderRequest, Date fecha, int entidad) {
		List<Concepto> entidades = getInstance().getConceptoIngreso(fecha,
				entidad);
		renderRequest.getPortletSession().setAttribute(
				WebKeysLiquidaciones.CONCEPTOS_INGRESO, entidades,
				PortletSession.APPLICATION_SCOPE);
		return entidades;
	}

	public static List<Concepto> getConceptoIngreso(int entidad) {
		List<Concepto> entidades = getInstance().getConceptoIngreso(new Date(),
				entidad);
		return entidades;
	}

	public static List<Concepto> getConceptoIngreso(Date fecha, int entidad) {
		List<Concepto> entidades = getInstance().getConceptoIngreso(fecha,
				entidad);
		return entidades;
	}

	public static List<Concepto> getConceptoLiquidacion(
			PortletRequest renderRequest) {
		return getConceptoLiquidacion(renderRequest, new Date());
	}

	public static List<Concepto> getConceptoLiquidacion(Date fecha) {
		return getInstance().getConceptoLiquidacion(fecha);
	}

	public static List<Concepto> getConceptoLiquidacion(
			PortletRequest renderRequest, Date fecha) {
		List<Concepto> entidades = getInstance().getConceptoLiquidacion(fecha);
		renderRequest.getPortletSession().setAttribute(
				WebKeysLiquidaciones.CONCEPTOS_LIQUIDACION, entidades,
				PortletSession.APPLICATION_SCOPE);
		return entidades;
	}

	public static List<Empresa> getEmpresasIngreso(String cuit, String entidad,
			String sucursal) {
		return getInstance().getEmpresasIngreso(cuit, entidad, sucursal);
	}

	public static List<PlanCuentas> getPlanCuentas(Date validoEnFecha,
			int entidad) {
		return getInstance().getPlanCuentas(validoEnFecha, entidad);
	}

	@SuppressWarnings("unchecked")
	public static List<Farmacia> getFarmacias(PortletRequest renderRequest) {
		List<Farmacia> entidades = (List<Farmacia>) renderRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.FARMACIAS,
						PortletSession.APPLICATION_SCOPE);

		if (entidades == null || (entidades != null && entidades.size() == 0)) {
			entidades = getInstance().getFarmacias();
			renderRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.FARMACIAS, entidades,
					PortletSession.APPLICATION_SCOPE);
		}
		return entidades;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Farmacia> getFarmaciasLiq(PortletRequest renderRequest) {	
		List<Farmacia> entidades = getInstance().getFarmacias();
			renderRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.FARMACIAS, entidades,
					PortletSession.APPLICATION_SCOPE);
		
		return entidades;
	}

	public static List<Plan> getPlanesOmint(HttpSession session) {
		List<Plan> planes = getInstance().getPlanesOmint();
		return planes;
	}

	public static List<Concepto> getConceptos(Date fecha, int entidad) {
		return getInstance().getConceptos(fecha, entidad);
	}

	public static PlanCuentas getCuentaById(int idInt, Date fecha, int entidad) {
		return getInstance().getCuentaById(idInt, fecha, entidad);
	}

	public static List<Concepto> getConceptosEgresoValidosDentroDe(
			Date fechaDesde, Date fechaFin, int entidad) {
		return getInstance().getConceptosEgresoValidosDentroDe(fechaDesde,
				fechaFin, entidad);
	}

	public static ListaConcepto getConceptosValidosDentroDe(Date fechaDesde,
			Date fechaFin, int entidad, Integer pagina) {
		return getInstance().getConceptosValidosDentroDe(fechaDesde, fechaFin,
				entidad, pagina);
	}

	public static List<PlanCuentas> getPlanCuentasImputables(
			Date validoEnFecha, int entidad) {
		return getInstance().getPlanCuentasImputables(validoEnFecha, entidad);
	}

	public static List<TarjetaAcceso> getTarjetasAccesoVigentes() {
		return getInstance().getTarjetasAccesoVigentes();
	}

	public static List<UsuarioCorrespondencia> getUsuariosCorrespondenciaVigentes() {
		return getInstance().getUsuariosCorrespondenciaVigentes();
	}

	public static ArrayList<String> getListaSemanasHaceUnAnio() {
		Date startDate = DateUtils.getPreceedingYear(DateUtils
				.getMismoDia_00_00hs(new Date()));
		Date currentDate = new Date();
		ArrayList<String> semanas = new ArrayList<String>();
		while (startDate.before(currentDate)) {
			Date primerDia = DateUtils.getFirstDateOfWeek(startDate, true);
			Date ultimoDia = DateUtils.getLastDateOfWeek(startDate, true);

			String primerDiaString = DateUtils.format(primerDia,
					DateUtils.SHORT);
			String ultimoDiaString = DateUtils.format(ultimoDia,
					DateUtils.SHORT);

			semanas.add(new String(primerDiaString + "-" + ultimoDiaString));
			startDate = DateUtils.anyadeDias(startDate, 7);
		}
		return semanas;
	}

	public static List<UltimosProcesosSisOld> getUltimosProcesosSisOld(
			Date fechaArchivo) throws SystemException {
		return getInstance().getUltimosPrcesosSisOld(fechaArchivo);
	}

	public static List<TipoBono> getTiposDeBonos() throws SystemException {
		return getInstance().getTiposDeBonos();
	}
	
	public static List<ProfesionPrestador> getProfesionesPrestador() throws Exception {
		return getInstance().getProfesion();
	}
			
	// //profesion prestador
	@SuppressWarnings("unchecked")
	public static List<ProfesionPrestador> getProfesion(
			PortletRequest portletRequest) throws Exception {
		List<ProfesionPrestador> profesion = (List<ProfesionPrestador>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.LISTAS_DE_PROFESION_PRESTADOR_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (profesion == null) {
			profesion = getInstance().getProfesion();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.LISTAS_DE_PROFESION_PRESTADOR_EN_SESSION ,
					profesion, PortletSession.APPLICATION_SCOPE);
		}
		return profesion;
	}
	
	// //especialidad prestador
	@Deprecated
	public static List<EspecialidadPrestador> getEspecialidadesPrestador() throws Exception{
		return getInstance().getEspecialidades();
	}
	
	@SuppressWarnings("unchecked")
	public static List<EspecialidadPrestador> getEspecialidadPrestador(
			PortletRequest portletRequest) throws Exception {
		List<EspecialidadPrestador> especialidadPrestador = (List<EspecialidadPrestador>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		if (especialidadPrestador == null) {
			especialidadPrestador = getInstance().getEspecialidades();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
					especialidadPrestador, PortletSession.APPLICATION_SCOPE);
		}
		return especialidadPrestador;
	}

	// // sub-especialidad prestador
	@Deprecated
	public static List<SubEspecialidadPrestador> getSubEspecialidadesPrestador() throws Exception{
		return getInstance().getSubEspecialidades();
	}
	
	@SuppressWarnings("unchecked")
	public static List<SubEspecialidadPrestador> getSubEspecialidadPrestador(
			PortletRequest portletRequest) throws Exception {
		List<SubEspecialidadPrestador> subEspecialidadPrestador = (List<SubEspecialidadPrestador>) portletRequest
				.getPortletSession()
				.getAttribute(
						WebKeysLiquidaciones.LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION ,
						PortletSession.APPLICATION_SCOPE);

		if (subEspecialidadPrestador == null) {
			subEspecialidadPrestador = getInstance().getSubEspecialidades();
			portletRequest.getPortletSession().setAttribute(
					WebKeysLiquidaciones.LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION,
					subEspecialidadPrestador, PortletSession.APPLICATION_SCOPE);
		}
		return subEspecialidadPrestador;
	}

	public static List<Parentesco> getParentescos() throws SystemException {
		return getInstance().getParentescos();
	}
	
	public static List<EstadoCivil> getEstadosCivil() throws SystemException {
		return getInstance().getEstadosCivil();
	}
	
	public static List<TipoNovedad> getTiposNovedadSss() throws SystemException {
		return getInstance().getTiposNovedadSss();
	}
	
	public static List<ArchivoNovedad> getFechasArchivosNovedades(String origen) throws SystemException {
		return getInstance().getFechasArchivosNovedades(origen);
	}
	
	public static List<Date> getFechasLiquidacionHistoricaTercerizadoras() throws SystemException {
		return getInstance().getFechasLiquidacionHistoricaTercerizadoras();
	}
	public static List<Prestador.TipoPrestador> getTiposPrestador() throws SystemException {
		return PrestadorServiceImpl.getInstance().getTipos();
	}
	public static List<TipoNomenclador> getTiposNomenclador() {
		return getInstance().getTiposNomenclador();
	}
	
//	public static List<Especialidad> getEspecialidades() {
//		return getInstance().getEspecialidades();
//	}
	
	public static List<ModalidadAtencion> getModalidadAtencion() {
		return getInstance().getModalidadAtencion() ;
	}
	
	public static List<Especialidad> getEspecialidadesNomenclador() {
		return getInstance().getEspecialidadesNomenclador();
	}
	
	public static List<TipoDiscapacidad> getTiposDiscapacidad() {
		
		return getInstance().getTraeTiposDiscapacidad();
	}

	
	public static List<String> getBimestresPorAnio(Date fechaDesde, Date fechaHasta,String clase) {
		return getInstance().getBimestresPorAnio(fechaDesde, fechaHasta,clase);
	}
	
	public static String getBimestresPorId(Integer id) {
		return getInstance().getBimestresPorId(id);
	}
	
	public static List<DrogaPatologia> getDrogasPorPatologia(Integer patologia) {
		return getInstance().getDrogasPorPatologia(patologia);
	}

	public static List<ModalidadAtencion> getEstadosSeguimientoSur() {
		return getInstance().getEstadosSeguimientoSur() ;
	}
	
	public static List<ModalidadAtencion> getEstadosSeguimientoSurPorEstados(String estados) {
		return getInstance().getEstadosSeguimientoSurPorEstados(estados) ;
	}
	
	
	public static List<ModalidadAtencion> getEstadosOspimSeguimientoSur() {
		return getInstance().getEstadosOspimSeguimientoSur() ;
	}
	
	public static List<String> getUsuariosAltaSeguimientoSur() {
		return getInstance().getUsuariosAltaSeguimientoSur();
	}
	
	public static List<ModalidadAtencion> getMotivosEstadoSur(Integer idEstado) {
		return getInstance().getMotivosEstadoSur(idEstado);
	}
	
	public static String getSystemConfig(String id) {
		return getInstance().getSystemConfig(id);
	}
	
	public static List<Concepto> getConceptosConSeccional(Date fecha, int entidad) {
		return getInstance().getConceptosConSeccional(fecha, entidad);
	}
	
	public static List<String> getCartillaTipos() {
		return getInstance().getCartillaTipos();
	}
	
	public static List<String> getCartillaPlan() {
		return getInstance().getCartillaPlan();
	}
	
	public static List<String> getCartillaLocalidad() {
		return getInstance().getCartillaLocalidad();
	}
	
	public static List<String> getCartillaProvincia() {
		return getInstance().getCartillaProvincia();
	}
	
	public static List<String> getCartillaEspecialidad() {
		return getInstance().getCartillaEspecialidad();
	}
	
	public static List<MotivoExcepcion> getMotivoExcepcion() {
		return getInstance().getMotivosExcepcion() ;
	}

	
	public static List<MovimientoBancario> getSaldoCuentasBancariasConformado(Integer idCta) {
		return getInstance().getSaldoCuentasBancariasConformado(idCta) ;
	}
	
	public static Map<Integer,List<Localidad>> getLocalidadesAgrupadasPorProvincia() {
		List<Localidad> localidades =  getInstance().getLocalidades();
		
		Map<Integer,List<Localidad>> localidadesPorProvincia = new HashMap<Integer,List<Localidad>>();
		
		for(Localidad l:localidades){
			if(l!=null && l.getId_provincia()>0){
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
		return localidadesPorProvincia;
	}
	
	public static List<PlanCuentasSSS> getPlanCuentasSSS(int entidad,Integer id,String cuenta,String descripcion,String tipo) {
		return getInstance().getPlanCuentasSSS(entidad,id,cuenta,descripcion,tipo);
	}
	
	public static PlanCuentasSSS getCuentaSSSById(int idInt,  int entidad) throws SystemException {
		List<PlanCuentasSSS> list = getPlanCuentasSSS(entidad,idInt,null,null,null);
		PlanCuentasSSS pc = new PlanCuentasSSS();
		if(list.size()>0){
			pc=list.get(0);
		}
		List<PlanCuentas>lpc = ContabilidadServiceUtil.getCuentasAsociadasSSS(pc.getNumero(), entidad, null);
		pc.setEquivalencias(lpc);
		return pc;
	}

	public static List<Delegacion> getDelegacionesSinSeccional() {
		return getInstance().getDelegacionesSinSeccional();
	}
	
	public static Date getMaximoPeriodoActasPorCuit(String cuit,String entidad) throws SystemException {
		return getInstance().getMaximoPeriodoActasPorCuit(cuit,entidad);
	}
	
	public static List<CentroCosto> getCentrosDeCostosVigentes(int entidad) throws Exception {
		return getInstance().getCentrosDeCostosVigentes(entidad);
	}
	
	public static List<ClaseBase> getTraeDiagnosticos() {
		return getInstance().getTraeDiagnosticos();
	}
	
	public static List<Localidad> getPercepcionesIIBB(int entidad) {
		String perc="";
		if(entidad==1) {
		   perc = getSystemConfig("CONCEPTOS_IIBB_UOMA");
		} else if(entidad==2) {   
		   perc = getSystemConfig("CONCEPTOS_IIBB_OSPIM");
		} else if(entidad==3) {
			perc = getSystemConfig("CONCEPTOS_IIBB_AMTIMA");
		}
		String[] vPerc = perc.split(";");
		List list = new ArrayList<Localidad>();
		for(int i=0;i<vPerc.length;i++) {
			String[] x=vPerc[i].split("-");
			Localidad l = new Localidad();
			l.setId(Integer.parseInt( x[0]));
			l.setDescripcion(x[1]);
			l.setId_provincia(Integer.parseInt( x[2]));
			list.add(l);
		}
		
		return list;
	}
	
	
	public static Map<String,List<Integer>> getLibroIVACompras(int entidad) {
		String perc="";
		if(entidad==1) {
		   perc = getSystemConfig("LIBRO_IVA_COMPRAS_UOMA");
		} else if(entidad==2) {   
		   perc = getSystemConfig("LIBRO_IVA_COMPRAS_OSPIM");
		} else if(entidad==3) {
			perc = getSystemConfig("LIBRO_IVA_COMPRAS_AMTIMA");
		}
		String[] vPerc = perc.split(";");
		Map<String, List<Integer> > map = new HashMap<String,List<Integer>>();
		for(int i=0;i<vPerc.length;i++) {
			String[] x=vPerc[i].split("-");
			
			String[]y =x[1].split(":");
			List list = new ArrayList<Integer>();
			for(int j=0;j<y.length;j++) {
				list.add(Integer.parseInt(y[j]));
			}
			
			map.put(x[0], list);
			
		}
		return map;
	}
	
	public static List<Prestador> getPrestodresInexistentesMedicacionEspecial() {
		return getInstance().getPrestodoresInexistentesMedicacionEspecial();
	}
	
	public static List<ClaseBase> getTarjetasDebitoCreditoEmisores() {
		return getInstance().getTarjetasDebitoCreditoEmisores();
	}
	
	public static List<ClaseBase> getSectoresLiquidaciones() {
		return getInstance().getSectoresLiquidaciones();
	}
	
	public static CuentaBancaria getCtasBcriasById(Integer id) {
		return getInstance().getCtasBcriasById(id);
	}
	
	public static List<AportesMonotributo> getAportesMonotributo(Integer id) {
		return getInstance().getAportesMonotributo(id);
	}
	
	public static AportesMonotributo getAportesMonotributoById(Integer id) {
		AportesMonotributo aporte = getAportesMonotributo(id).get(0);
		List<AportesMonotributoClase> clases = getInstance().getAportesMonotributoClases(id);
		aporte.setClases(clases);
		aporte.setClasesOriginal(clases);
		return aporte;
	}
	
	public static List<AportesMonotributo> getCategoriasMonotributo() {
		return getInstance().getCategoriasMonotributo();
	}
	
	public static List<CentroCosto> getSectoresLiquidacionSueldos(String entidad) {
		return getInstance().getSectoresLiquidacionSueldos(entidad);
	}
}