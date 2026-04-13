package ar.com.ospim.liquidaciones.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ConceptoUtilizadoException;
import ar.com.ospim.tesoreria.beans.ConceptoAfip;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class ConceptoServiceUtil {
	// esto esta en la tabla nomenclador_concepto_tipo
	private static final int TIPO_HONORARIOS_AMBULATORIO = 1;
	private static final int TIPO_HONORARIOS_INTERNACION = 2;
	private static final int TIPO_GASTOS_AMBULATORIO = 3;
	private static final int TIPO_GASTOS_INTERNACION = 4;

	private static int idConceptoAjuste = 0;
	private static ConceptoServiceImpl instance = null;

	private static List<ParametroConcepto> parametrosConceptos = null;
	private static List<ParametroCuenta> parametrosCuentas = null;
	private static Log logger = LogFactoryUtil
			.getLog(ConceptoServiceUtil.class);

	private static ConceptoServiceImpl getInstance() {
		if (null == instance) {
			instance = new ConceptoServiceImpl();
		}
		return instance;
	}

	private static int getConceptoIdFromParametrosConceptos(String parametro,
			Date date) {
		for (ParametroConcepto p : parametrosConceptos) {
			if (p.getParametro().equals(parametro)
					&& date.compareTo(p.getValidoDesde()) >= 0
					&& date.compareTo(p.getValidoHasta()) <= 0) {
				return p.getConceptoId();
			}
		}
		return -1;
	}

	private static ParametroCuenta getParametroCuenta(String parametro,
			Date date) {
		for (ParametroCuenta p : parametrosCuentas) {
			if (p.getParametro().equals(parametro)
					&& date.compareTo(p.getValidoDesde()) >= 0
					&& date.compareTo(p.getValidoHasta()) <= 0) {
				return p;
			}
		}
		return null;
	}

	public static int getIdAjuste() throws Exception {
		if (idConceptoAjuste == 0) {
			idConceptoAjuste = getInstance().getIdConceptoAjuste();
		}
		return idConceptoAjuste;
	}

	public static int getIdConveniosGlobalesNoLiquidacion(Date fecha) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(WebKeysGlobal.OSPIM);
		}
		return getConceptoIdFromParametrosConceptos(
				"convenios_globales_no_liquidaciones", fecha);
	}

	public static int getIdConveniosGlobales(Date fecha) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(WebKeysGlobal.OSPIM);
		}
		return getConceptoIdFromParametrosConceptos("convenios_globales", fecha);
	}

	public static int getIdCanjeCheque(Date fecha, int entidad) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(entidad);
		}
		return getConceptoIdFromParametrosConceptos("canje_cheque", fecha);
	}

	public static int getIdReintegros(Date fecha) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(WebKeysGlobal.OSPIM);
		}
		return getConceptoIdFromParametrosConceptos("reintegros", fecha);
	}

	public static int getIdPrestacionesMedicas(Date fecha) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(WebKeysGlobal.OSPIM);
		}
		return getConceptoIdFromParametrosConceptos("prestaciones_medicas",
				fecha);
	}

	public static int getIdSueldosSedeCentral(Date fecha) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(WebKeysGlobal.OSPIM);
		}
		return getConceptoIdFromParametrosConceptos("sueldos1", fecha);
	}

	public static int getIdSueldosSeccionales(Date fecha) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(WebKeysGlobal.OSPIM);
		}
		return getConceptoIdFromParametrosConceptos("sueldos2", fecha);
	}

	public static List<ParametroConcepto> getParametrosConceptos(
			Date validoDesde, Date validoHasta, int entidad) {
		return getInstance().getParametrosConceptos(validoDesde, validoHasta,
				entidad);
	}
	
	public static Concepto getConceptoCuitComproTipo(String cuit, String comproTipo, int entidad){
		return getInstance().getConceptoCuitComproTipo(cuit, comproTipo,
				entidad);
	}

	public static PlanCuentas getCuentaRetencionGanancias(Date fecha,
			int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("retencion_ganancias", fecha)
				.getPlanCuentas();
	}

	
	public static PlanCuentas getCuentaRetencionIIBB(Date fecha,
			int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("retencion_iibb", fecha)
				.getPlanCuentas();
	}
	
	public static PlanCuentas getCuentaRetencionIVA(Date fecha,
			int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("retencion_iva", fecha)
				.getPlanCuentas();
	}
	
	public static PlanCuentas getCuentaPagoSinSalidaDeFondos(Date fecha,
			int entidad,int tipo) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("pago_sin_salida_de_fondos_"+String.valueOf(tipo), fecha)
				.getPlanCuentas();
	}
	
	
	public static List<ParametroCuenta> getParametrosCuentas(int entidad) {
		return getInstance().getParametrosCuentas(entidad);
	}

	public static List<ParametroCuenta> getParametrosCuentas(Date fechaIni,
			Date fechaFin, int entidad) {
		return getInstance().getParametrosCuentas(fechaIni, fechaFin, entidad);
	}
	
	
	public static ParametroCuenta getParametroCuenta(String parametro,
			Date date,int entidad) {
		for (ParametroCuenta p : getInstance().getParametrosCuentas(entidad)) {
			if (p.getParametro().equals(parametro)
					&& date.compareTo(p.getValidoDesde()) >= 0
					&& date.compareTo(p.getValidoHasta()) <= 0) {
				return p;
			}
		}
		return null;
	}
	

	public static class ParametroConcepto {
		private String parametro;
		private Date validoDesde;
		private Date validoHasta;
		private Integer conceptoId;
		private String observaciones;

		public ParametroConcepto(String param) {
			this.parametro = param;
		}

		public ParametroConcepto() {
		}

		public String getParametro() {
			return parametro;
		}

		public void setParametro(String parametro) {
			this.parametro = parametro;
		}

		public Date getValidoDesde() {
			return validoDesde;
		}

		public void setValidoDesde(Date validoDesde) {
			this.validoDesde = validoDesde;
		}

		public Date getValidoHasta() {
			return validoHasta;
		}

		public void setValidoHasta(Date validoHasta) {
			this.validoHasta = validoHasta;
		}

		public String getValidoDesdeString() {
			if (validoDesde == null) {
				return "";
			}
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.format(validoDesde);
		}

		public String getValidoHastaString() {
			if (validoHasta == null) {
				return "";
			}
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.format(validoHasta);
		}

		public static ParametroConcepto getMapping(ResultSet rs)
				throws SQLException {
			ParametroConcepto pc = new ParametroConcepto();
			pc.setParametro(rs.getString("parametro"));
			pc.setValidoDesde(rs.getDate("valido_desde"));
			pc.setValidoHasta(rs.getDate("valido_hasta"));
			pc.setConceptoId(rs.getInt("id_concepto"));
			try {
				pc.setObservaciones(rs.getString("observaciones"));
			} catch (Exception e) {

			}
			return pc;
		}

		public Integer getConceptoId() {
			return conceptoId;
		}

		public void setConceptoId(Integer conceptoId) {
			this.conceptoId = conceptoId;
		}

		public String getObservaciones() {
			return observaciones;
		}

		public void setObservaciones(String observaciones) {
			this.observaciones = observaciones;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((parametro == null) ? 0 : parametro.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ParametroConcepto other = (ParametroConcepto) obj;
			if (parametro == null) {
				if (other.parametro != null)
					return false;
			} else if (!parametro.equals(other.parametro))
				return false;
			return true;
		}

	}

	public static class ParametroCuenta {
		private String parametro;
		private Date validoDesde;
		private Date validoHasta;
		private PlanCuentas planCuentas;
		private String observaciones;

		public ParametroCuenta(String param) {
			this.parametro = param;
		}

		public ParametroCuenta() {
		}

		public String getParametro() {
			return parametro;
		}

		public void setParametro(String parametro) {
			this.parametro = parametro;
		}

		public Date getValidoDesde() {
			return validoDesde;
		}

		public void setValidoDesde(Date validoDesde) {
			this.validoDesde = validoDesde;
		}

		public Date getValidoHasta() {
			return validoHasta;
		}

		public void setValidoHasta(Date validoHasta) {
			this.validoHasta = validoHasta;
		}

		public String getValidoDesdeString() {
			if (validoDesde == null) {
				return "";
			}
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.format(validoDesde);
		}

		public String getValidoHastaString() {
			if (validoHasta == null) {
				return "";
			}
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.format(validoHasta);
		}

		public static ParametroCuenta getMapping(ResultSet rs)
				throws SQLException {
			ParametroCuenta pc = new ParametroCuenta();
			pc.setParametro(rs.getString("parametro"));
			pc.setValidoDesde(rs.getDate("valido_desde"));
			pc.setValidoHasta(rs.getDate("valido_hasta"));
			PlanCuentas pCuenta = new PlanCuentas();
			pCuenta.setId(rs.getInt("id_cuenta"));
			pCuenta.setNumero(rs.getString("numero"));
			pCuenta.setCuenta(rs.getString("cuenta"));
			pc.setPlanCuentas(pCuenta);
			try {
				pc.setObservaciones(rs.getString("observaciones"));
			} catch (Exception e) {

			}
			return pc;
		}

		public String getObservaciones() {
			return observaciones;
		}

		public void setObservaciones(String observaciones) {
			this.observaciones = observaciones;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((parametro == null) ? 0 : parametro.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ParametroCuenta other = (ParametroCuenta) obj;
			if (parametro == null) {
				if (other.parametro != null)
					return false;
			} else if (!parametro.equals(other.parametro))
				return false;
			return true;
		}

		public PlanCuentas getPlanCuentas() {
			return planCuentas;
		}

		public void setPlanCuentas(PlanCuentas planCuentas) {
			this.planCuentas = planCuentas;
		}

	}
	
	public static void update(Concepto concepto, User user, Date desdeOriginal, int entidad)
			throws Exception {
		List<Concepto> conceptos = TraeListasServiceUtil
				.getConceptosValidosDentroDe(concepto.getValidoDesde(),
						concepto.getValidoHasta(), entidad,null).getConceptos();
		Concepto conceptoEnBase = null;
		for (Concepto con : conceptos) {
			if (concepto.getId() == con.getId()
					&& DateUtils.compararFechasTruncarEnDia(desdeOriginal,
							con.getValidoDesde()) == 0) {
				conceptoEnBase = con;
				break;
			}
		}
		if (DateUtils.compararFechasTruncarEnDia(concepto.getValidoDesde(),
				conceptoEnBase.getValidoDesde()) == 0
				&& DateUtils.compararFechasTruncarEnDia(
						concepto.getValidoHasta(),
						conceptoEnBase.getValidoHasta()) == 0) {
			getInstance().update(concepto, user, entidad);
		} else {
			getInstance().reemplazar(conceptoEnBase, concepto, user, entidad);
		}
	}

	public static void update(Concepto concepto, User user, Date desdeOriginal,
			int entidad,int id_secuencial) throws Exception {
		List<Concepto> conceptos = TraeListasServiceUtil
				.getConceptosValidosDentroDe(concepto.getValidoDesde(),
						concepto.getValidoHasta(), entidad,null).getConceptos();
		Concepto conceptoEnBase = null;
		for (Concepto con : conceptos) {
			
				if (con.getIdSecuencial()==id_secuencial) {
					conceptoEnBase = con;
					break;
				}
			
		}
		conceptoEnBase.setIdSeccional(concepto.getIdSeccional());
		if (DateUtils.compararFechasTruncarEnDia(concepto.getValidoDesde(),
				conceptoEnBase.getValidoDesde()) == 0
				&& DateUtils.compararFechasTruncarEnDia(
						concepto.getValidoHasta(),
						conceptoEnBase.getValidoHasta()) == 0) {
			concepto.setIdSecuencial(id_secuencial);
			getInstance().update(concepto, user, entidad);
		} else {
			getInstance().reemplazar(conceptoEnBase, concepto, user, entidad);
		}
	}

	public static List<PrestacionConcepto> getPrestacionesConceptos(
			Calendar desdeEjercicio, Calendar hastaEjercicio) {
		return getInstance().getPrestacionesConceptos(desdeEjercicio,
				hastaEjercicio);
	}

	public static PrestacionConcepto getPrestacionesConceptos(int id,
			Date desdeEjercicio, Date hastaEjercicio) {
		return getInstance().getPrestacionesConceptosActualPorIdPrestacion(id,
				desdeEjercicio, hastaEjercicio);
	}

	public static void update(PrestacionConcepto pConcepto, User user,
			Date ddOriginal) throws Exception {
		Connection con = ConnectionHelper.getConnectionForTransaction();
		try {

			PrestacionConcepto pConceptosEnBase = getInstance()
					.getPrestacionesConceptosActualPorIdPrestacion(
							pConcepto.getPrestacion().getId(), ddOriginal,
							pConcepto.getValidoHastaGastosAmbulatorio());

			actualizarNomencladorConcepto(pConcepto, con,
					pConcepto.getValidoDesdeHonorariosAmbulatorio(),
					pConceptosEnBase.getValidoDesdeHonorariosAmbulatorio(),
					pConcepto.getValidoHastaHonorariosAmbulatorio(),
					pConceptosEnBase.getValidoHastaHonorariosAmbulatorio(),
					TIPO_HONORARIOS_AMBULATORIO,
					pConcepto.getIdHonorariosAmbulatorio(),
					pConcepto.getHonorariosAmbulatorio(), user);

			actualizarNomencladorConcepto(pConcepto, con,
					pConcepto.getValidoDesdeHonorariosInternacion(),
					pConceptosEnBase.getValidoDesdeHonorariosInternacion(),
					pConcepto.getValidoHastaHonorariosInternacion(),
					pConceptosEnBase.getValidoHastaHonorariosInternacion(),
					TIPO_HONORARIOS_INTERNACION,
					pConcepto.getIdHonorariosInternacion(),
					pConcepto.getHonorariosInternacion(), user);

			actualizarNomencladorConcepto(pConcepto, con,
					pConcepto.getValidoDesdeGastosAmbulatorio(),
					pConceptosEnBase.getValidoDesdeGastosAmbulatorio(),
					pConcepto.getValidoHastaGastosAmbulatorio(),
					pConceptosEnBase.getValidoHastaGastosAmbulatorio(),
					TIPO_GASTOS_AMBULATORIO,
					pConcepto.getIdGastosAmbulatorio(),
					pConcepto.getGastosAmbulatorio(), user);

			actualizarNomencladorConcepto(pConcepto, con,
					pConcepto.getValidoDesdeGastosInternacion(),
					pConceptosEnBase.getValidoDesdeGastosInternacion(),
					pConcepto.getValidoHastaGastosInternacion(),
					pConceptosEnBase.getValidoHastaGastosInternacion(),
					TIPO_GASTOS_INTERNACION,
					pConcepto.getIdGastosInternacion(),
					pConcepto.getGastosInternacion(), user);

			getInstance().update(con, pConcepto.getPrestacion(),
					pConcepto.getCoeficienteHonorarios(),
					pConcepto.getCoeficienteGastos(), user);

			con.commit();
		} catch (Exception e) {
			logger.error("error", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);
		}
	}

	private static void actualizarNomencladorConcepto(
			PrestacionConcepto prestacionConcepto, Connection connectionParam,
			Date desdeNuevo, Date desdeOriginal, Date hastaNuevo,
			Date hastaOriginal, int tipo, int idNomencladorConcepto,
			Concepto conceptoNuevo, User user) throws Exception {

		if (desdeOriginal != null
				&& DateUtils.compararFechasTruncarEnDia(desdeNuevo,
						desdeOriginal) == 0
				&& DateUtils.compararFechasTruncarEnDia(hastaNuevo,
						hastaOriginal) == 0) {
			getInstance().updateNomencladorConcepto(connectionParam,
					prestacionConcepto.getPrestacion().getId_prestacion(),
					idNomencladorConcepto, conceptoNuevo.getId(), desdeNuevo,
					hastaNuevo, tipo, user);
		} else {
			getInstance().reemplazarNomencladorConcepto(
					connectionParam,
					prestacionConcepto.getPrestacion().getId_prestacion(),
					desdeNuevo,
					desdeOriginal != null ? desdeOriginal : DateUtils
							.getDesdeInfinito().getTime(),
					hastaNuevo,
					hastaOriginal != null ? hastaOriginal : DateUtils
							.getHastaInfinito().getTime(),
					idNomencladorConcepto, conceptoNuevo.getId(),
					DateUtils.getDesdeEjercicioActual(),
					DateUtils.getInfinito(), user, tipo);
		}
	}

	public static void guardar(Concepto concepto, User user, int entidad) {
		getInstance().guardar(concepto, user, entidad);
	}

	public static void eliminar(Concepto concepto, Date desde, Date hasta,
			User user, int entidad) throws Exception {
		if (getInstance().estaUtilizado(concepto, desde, hasta, entidad)) {
			throw new ConceptoUtilizadoException();
		}
		getInstance().eliminar(concepto, desde, hasta, user, entidad);
	}

	public static void guardar(PrestacionConcepto prestacionConcepto,
			User user, Date validoDesde, Date validoHasta) throws Exception {
		getInstance().guardar(prestacionConcepto, user, validoDesde,
				validoHasta);
	}

	public static void eliminar(PrestacionConcepto prestacionConcepto, User user)
			throws Exception {
		getInstance().eliminar(prestacionConcepto, user);

	}

	public static int getIdReintegrosAmtima(Date fecha) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(WebKeysGlobal.OSPIM);
		}
		return getConceptoIdFromParametrosConceptos("reintegros_amtima", fecha);
	}

	public static int getIdReintegrosFarmaciaOSPIM(Date fecha) {
		if (parametrosConceptos == null) {
			parametrosConceptos = getInstance().getParametrosConceptos(WebKeysGlobal.OSPIM);
		}
		return getConceptoIdFromParametrosConceptos("reintegros_farmacia",
				fecha);
	}

	public static void update(ParametroConcepto pc, Date desdeOriginal,
			User user, int entidad) {
		List<ParametroConcepto> parametrosConceptos = getParametrosConceptos(
				pc.getValidoDesde(), pc.getValidoHasta(), entidad);
		ParametroConcepto parametroConceptoOriginal = null;
		for (ParametroConcepto pcon : parametrosConceptos) {
			if (pcon.getParametro().equals(pc.getParametro())
					&& DateUtils.compararFechasTruncarEnDia(
							pcon.getValidoDesde(), desdeOriginal) == 0) {
				parametroConceptoOriginal = pcon;
				break;
			}
		}

		getInstance().reemplazarParametroConcepto(parametroConceptoOriginal,
				pc, user, entidad);
	}

	public static void update(ParametroCuenta pc, Date desdeOriginal,
			User user, int entidad) {
		parametrosCuentas = null;
		List<ParametroCuenta> parametrosCuentas = getParametrosCuentas(
				pc.getValidoDesde(), pc.getValidoHasta(), entidad);
		ParametroCuenta parametroCuentaOriginal = null;
		for (ParametroCuenta pcon : parametrosCuentas) {
			if (pcon.getParametro().equals(pc.getParametro())
					&& DateUtils.compararFechasTruncarEnDia(
							pcon.getValidoDesde(), desdeOriginal) == 0) {
				parametroCuentaOriginal = pcon;
				break;
			}
		}

		getInstance().reemplazarParametroCuenta(parametroCuentaOriginal, pc,
				user, entidad);

	}

	public static boolean verificarEquivalenciasConceptosCompleto(Date desde,
			Date hasta, int entidad) {
		return getInstance().verificarEquivalenciasConceptosCompleto(desde,
				hasta, entidad);
	}

	public static boolean verificarEquivalenciasPrestacionesCompleto(
			Date desde, Date hasta, int entidad) {
		return getInstance().verificarEquivalenciasPrestacionesCompleto(desde,
				hasta, entidad);
	}

	public static void update(TipoMovBcrio tipo, Date desdeOriginal, User user,
			int entidad) throws Exception {
		List<TipoMovBcrio> tipoMovBcrio = TraeListasServiceUtil
				.getTipoMovBcrio(tipo.getValidoDesde(), tipo.getValidoHasta(),
						entidad);
		TipoMovBcrio tipoEnBase = null;
		for (TipoMovBcrio tipoOrig : tipoMovBcrio) {
			if (tipoOrig.getId_tipo_mov() == tipo.getId_tipo_mov()
					&& DateUtils.compararFechasTruncarEnDia(
							tipoOrig.getValidoDesde(), desdeOriginal) == 0) {
				tipoEnBase = tipoOrig;
				break;
			}
		}
		if (DateUtils.compararFechasTruncarEnDia(tipo.getValidoDesde(),
				tipoEnBase.getValidoDesde()) == 0
				&& DateUtils.compararFechasTruncarEnDia(tipo.getValidoHasta(),
						tipoEnBase.getValidoHasta()) == 0) {
			getInstance().update(tipo, user, entidad);
		} else {
			getInstance().reemplazar(tipoEnBase, tipo, user, entidad);
		}

	}

	public static void guardar(TipoMovBcrio tipo, User user, int entidad) {
		getInstance().guardar(tipo, user, entidad);
	}

	public static void eliminar(TipoMovBcrio tipoMovBcrio, User user,
			int entidad) {
		getInstance().eliminar(tipoMovBcrio, user, entidad);
	}

	public static List<ConceptoAfip> getConceptosAfip(Date desdeEjercicio,
			Date hastaEjercicio) {
		return getInstance().getConceptosAfip(desdeEjercicio, hastaEjercicio);
	}

	public static void guardar(ConceptoAfip cAfip, User user) {
		getInstance().guardar(cAfip, user);
	}

	public static void update(ConceptoAfip cAfip, User user) throws Exception {
		List<ConceptoAfip> conceptosAfip = ConceptoServiceUtil
				.getConceptosAfip(cAfip.getValidoDesde(),
						cAfip.getValidoHasta());
		int indexOf = conceptosAfip.indexOf(cAfip);
		ConceptoAfip cAfipEnBase = conceptosAfip.get(indexOf);
		if (DateUtils.compararFechasTruncarEnDia(cAfip.getValidoDesde(),
				cAfipEnBase.getValidoDesde()) == 0
				&& DateUtils.compararFechasTruncarEnDia(cAfip.getValidoHasta(),
						cAfipEnBase.getValidoHasta()) == 0) {
			getInstance().update(cAfip, user);
		} else {
			getInstance().reemplazar(cAfipEnBase, cAfip, user);
		}
	}

	public static PlanCuentas getCuentaDeudoresActasYConvenios(Date fecha,
			int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("deudores_actas_y_convenios", fecha)
				.getPlanCuentas();
	}
	
	public static PlanCuentas getCuentaSocialUsufructo(Date fecha,
			int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("cuenta_social_usufructo", fecha)
				.getPlanCuentas();
	}
	
	public static PlanCuentas getCuentaArt46(Date fecha,
			int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("cuenta_art46", fecha)
				.getPlanCuentas();
	}
	
	public static PlanCuentas getCuentaSolidario(Date fecha,
			int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("cuenta_solidario", fecha)
				.getPlanCuentas();
	}
		

	public static PlanCuentas getCuentaInteresesPorAportesYContrib(Date fecha,
			int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("intereses_aportes_contribuciones", fecha)
				.getPlanCuentas();
	}

	public static PlanCuentas getCuentaActasYConvenios(Date fecha, int entidad) {
		
		parametrosCuentas = getInstance().getParametrosCuentas(entidad);
		
		return getParametroCuenta("actas_y_convenios", fecha).getPlanCuentas();
	}

}
