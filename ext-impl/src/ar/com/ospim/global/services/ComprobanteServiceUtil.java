package ar.com.ospim.global.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposOPExcel.ItemAnticipoOP;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;

public class ComprobanteServiceUtil {
	private static Log _log = LogFactoryUtil
			.getLog(ComprobanteServiceUtil.class);

	private static ComprobanteServiceImpl instance = null;
	
	private static FacturacionServiceUtil facturacionServiceUtil = new FacturacionServiceUtil();
	
	public static ComprobanteServiceImpl getInstance() {
		if (null == instance) {
			instance = new ComprobanteServiceImpl();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static List<Comprobante> getComprobantes(
			PortletRequest portletRequest) throws Exception {
		List<Comprobante> comprobantes = (List<Comprobante>) portletRequest
				.getPortletSession().getAttribute(
						WebKeysGlobal.COMPROBANTES_EN_SESSION,
						PortletSession.APPLICATION_SCOPE);

		return comprobantes;
	}

	public static int getIdComprobanteLiquidacion(Comprobante comp)
			throws SystemException {
		int id_liquidacion = getInstance().getIdComprobanteLiquidacion(comp);
		return id_liquidacion;
	}

	public static void update(Comprobante comp, User user, int entidad)
			throws SystemException, SQLException {
		_log.debug("obteniendo conexion");
		Connection con = ConnectionHelper.getConnection();
		try {
			con.setAutoCommit(false);
			getInstance().update(comp, user.getScreenName(), con, entidad);

			if (comp.getConceptos() != null) {
				for (ComprobanteConcepto cc : comp.getConceptos()) {
					if (cc.isBorradoLogicamente()) {
						getInstance().borrarConcepto(comp, cc,
								user.getScreenName(), con, entidad);
					}else {
						if (cc.getAlta_fecha() == null) {
							getInstance().saveConcepto(comp, cc,
									user.getScreenName(), con, entidad);
						} else {
							getInstance().updateConcepto(comp, cc,
									user.getScreenName(), con, entidad);
						}
					}
					
				}
			}
			con.commit();
		} catch (SystemException e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);
		}
	}

	public static void deleteComprobanteLiquidacion(int id, User user)
			throws SystemException {
		getInstance().deleteComprobanteLiquidacion(id, user.getScreenName());
	}

	/**
	 * Obtiene un comprobante si es que existe el comprobante en la base por la
	 * clav primaria, si no existe en la base (lo cual puede darse) retorna null
	 * 
	 * @throws Exception
	 */
	public static Comprobante getComprobante(Comprobante comp, int entidad)
			throws Exception {

		Comprobante comprobante = getInstance().getComprobante(comp, entidad);
		if (comprobante != null) {
			if (comprobante.getAcreedorEmpresa() != null
					&& comprobante.getAcreedorEmpresa().getCuit() != null
					&& comprobante.getAcreedorEmpresa().getSucursal() != null) {
				Empresa emp = EmpresaServiceUtil.getEmpleadorCompleto(
						comprobante.getAcreedorEmpresa().getCuit(), comprobante
								.getAcreedorEmpresa().getSucursal().trim());
				comprobante.setAcreedorEmpresa(emp);
			}
			if (comprobante.getSeccional() != null) {
				List<Seccional> s = TraeListasServiceUtil
						.getSeccionales(comprobante.getSeccional()
								.getIdSeccional(), null, null);
				if (s != null && s.size() == 1) {
					Seccional acreedorSeccional = s.get(0);
					acreedorSeccional.setCuitEntidad(comprobante
							.getAcreedorEmpresa().getCuit());
					comprobante.setSeccional(acreedorSeccional);
					comprobante.getAcreedorEmpresa().setRazon_soc(
							acreedorSeccional.getDescripcion());
				}
			}
			if (comprobante != null && comp != null) {
				comprobante.setConceptos(getInstance().getConceptos(comp,
						entidad));
			}
		}
		return comprobante;
	}

	public static Comprobante getComprobanteAnticipo(Comprobante comp,
			int entidad, int idOP) throws Exception {
		Comprobante comprobante = getInstance().getComprobanteAnticipo(comp,
				entidad, idOP);
		if (comprobante != null) {
			if (comprobante.getAcreedorEmpresa() != null
					&& comprobante.getAcreedorEmpresa().getCuit() != null
					&& comprobante.getAcreedorEmpresa().getSucursal() != null) {
				Empresa emp = EmpresaServiceUtil.getEmpleadorCompleto(
						comprobante.getAcreedorEmpresa().getCuit(), comprobante
								.getAcreedorEmpresa().getSucursal().trim());
				comprobante.setAcreedorEmpresa(emp);
			}
			if (comprobante.getSeccional() != null) {
				List<Seccional> s = TraeListasServiceUtil
						.getSeccionales(comprobante.getSeccional()
								.getIdSeccional(), null, null);
				if (s != null && s.size() == 1) {
					Seccional acreedorSeccional = s.get(0);
					acreedorSeccional.setCuitEntidad(comprobante
							.getAcreedorEmpresa().getCuit());
					comprobante.setSeccional(acreedorSeccional);
					comprobante.getAcreedorEmpresa().setRazon_soc(
							acreedorSeccional.getDescripcion());
				}
			}
			if (comprobante != null && comp != null) {
				comprobante.setConceptos(getInstance().getConceptosAnticipo(
						comp, entidad, idOP));
			}
		}
		return comprobante;
	}

	public static List<ComprobanteConcepto> getConceptos(
			Comprobante comprobante, int entidad) throws SystemException {
		return getInstance().getConceptos(comprobante, entidad);
	}

	public static Comprobante.ComprobanteConcepto getConceptoConveniosGlobales(
			Comprobante comprobante, int entidad) throws SystemException {
		List<ComprobanteConcepto> comprobantes = getInstance().getConceptos(
				comprobante, entidad);
		for (ComprobanteConcepto comprobanteConcepto : comprobantes) {
			if (comprobanteConcepto.getConceptoComprobante().getId() == ConceptoServiceUtil
					.getIdConveniosGlobales(comprobante.getFechaRecepcion())) {
				return comprobanteConcepto;
			}
		}
		return null;
	}

	public static List<OrdenPagoOspim> getComprobantesOP(Date fechaIni,
			Date fechaFin) throws Exception {
		List<OrdenPagoOspim> ops = getInstance().getComprobantesOP(fechaIni,
				fechaFin);

		List<Comprobante> comps = getInstance().getConceptosOP(fechaIni,
				fechaFin);

		for (OrdenPagoOspim op : ops) {
			if (op.getComprobantes() != null) {
				for (Comprobante c : op.getComprobantes()) {
					if (comps.contains(c)) {
						int indexOf = comps.indexOf(c);
						c.setConceptos(comps.get(indexOf).getConceptos());
					}
				}
			}
		}
		return ops;

	}

	public static List<OrdenPagoOspim> getComprobantesOP(Date fechaIni,
			Date fechaFin, int id_prestador, String cuit, String sucur,
			String compro_tipo, String compro_nro, int compro_sucur,
			String compro_letra, Connection connection) throws Exception {
		List<OrdenPagoOspim> ops = null;
		List<Comprobante> comps = null;
		Connection con = null;
		try {
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			ops = getInstance().getComprobantesOP(fechaIni, fechaFin,
					id_prestador, cuit, sucur, compro_tipo, compro_nro,
					compro_sucur, compro_letra, connection);
			comps = getInstance().getConceptosOPSubdiario(fechaIni, fechaFin,
					connection);
			// .getConceptosOP(fechaIni,fechaFin);
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(con);
			}
		}
		for (OrdenPagoOspim op : ops) {
			if (op.getComprobantes() != null) {
				for (Comprobante c : op.getComprobantes()) {
					if (comps.contains(c)) {
						int indexOf = comps.indexOf(c);
						c.setConceptos(comps.get(indexOf).getConceptos());
					}
				}
			}
		}
		return ops;

	}

	public static List<OrdenPago> getComprobantesOP(Date fechaIni,
			Date fechaFin, int id_prestador, String cuit, String sucur,
			String compro_tipo, String compro_nro, int compro_sucur,
			String compro_letra, int entidad, Connection connection)
			throws Exception {
		List<OrdenPago> ops = null;
		List<Comprobante> comps = null;
		Connection con = null;
		try {
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			ops = getInstance().getComprobantesOP(fechaIni, fechaFin,
					id_prestador, cuit, sucur, compro_tipo, compro_nro,
					compro_sucur, compro_letra, entidad, connection);
			comps = getInstance().getConceptosOP(fechaIni, fechaFin, entidad,
					connection);
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(con);
			}
		}

		for (OrdenPago op : ops) {
			if (op.getComprobantes() != null) {
				for (Comprobante c : op.getComprobantes()) {
					if (comps.contains(c)) {
						int indexOf = comps.indexOf(c);
						c.setConceptos(comps.get(indexOf).getConceptos());
					}
				}
			}
		}
		return ops;

	}

	public static Comprobante getComprobanteLiquidacionPorId(int id_liquidacion)
			throws SystemException {
		return getInstance().getComprobanteLiquidacionPorId(id_liquidacion);
	}

	public static Comprobante getComprobanteDebitoLiquidacionPorId(
			int id_liquidacion) throws SystemException {
		return getInstance().getComprobanteDebitoLiquidacionPorId(
				id_liquidacion);
	}

	public static List<Comprobante> getComprobantesLikeNro(Comprobante comp,
			boolean isOspim) throws SystemException {
		return getInstance().getComprobantesLikeNro(comp, isOspim);
	}

	public static String getUltimoNroDebito(int entidad) throws SystemException {
		return getInstance().getUltimoNroDebito("NDB", entidad);
	}

	public static List<Comprobante> getComprobantes(Comprobante comprobante,
			int entidad) throws Exception {
		return getInstance().getComprobantes(comprobante, entidad);
	}

	public static List<Comprobante> getComprobantes(Comprobante comprobante,
			int entidad, int pagado) throws Exception {
		List<Comprobante> comprobantes = getInstance().getComprobantes(
				comprobante, entidad);

		if (pagado == 2) {
			List<Comprobante> comprobantesNuevos = new ArrayList<Comprobante>();
			for (Comprobante c : comprobantes) {
				if (c.isPagado()) {
					comprobantesNuevos.add(c);
				}
			}
			return comprobantesNuevos;

		}
		if (pagado == 1) {
			List<Comprobante> comprobantesNuevos = new ArrayList<Comprobante>();
			for (Comprobante c : comprobantes) {
				if (!c.isPagado()) {
					comprobantesNuevos.add(c);
				}
			}
			return comprobantesNuevos;
		}

		return comprobantes;
	}

	public static void save(Comprobante comprobante, User user, int entidad)
			throws Exception {
		save(comprobante, user, null, entidad);
	}

	public static void save(Comprobante comprobante, User user,
			Connection connectionParameter, int entidad,
			boolean generarNuevoNroNDB) throws Exception {
		Connection con = null;
		try {
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}

			if (comprobante.getAfiliado() != null
					&& comprobante.getAfiliado().getCuil_titular() != null) {
				try {
					EmpresaServiceUtil.getInstance().saveAfiliadoComoEmpresa(
							comprobante.getAfiliado().getCuil_titular(),
							user.getScreenName());
				} catch (Exception e) {
					_log.error(e);
				}

			}

			if (generarNuevoNroNDB
					&& comprobante.getTipoComprobante().equals("NDB") && "30629138567".equals(comprobante.getCuit())) {
				String compro_numero = getInstance().generarSiguienteDebito(
						con, comprobante.getTipoComprobante(), entidad);
				comprobante.setNroComprobante(compro_numero);
			}

			// EL COMPROBANTE ESTA PAGADO PARCIALMENTE
			if (null != comprobante.getImporteComprobanteOriginal()
					&& comprobante.getImporteComprobante().compareTo(
							comprobante.getImporteComprobanteOriginal()) < 0) {
				// COMPROBANTE POR LA DIFERENCIA...
				Comprobante comprobanteNuevo = crearNuevoComprobanteParcial(comprobante);
				getInstance().update(comprobanteNuevo, user.getScreenName(),
						connectionParameter, entidad);
				// DEBEMOS ACTUALIZAR EL COMPRO ORIGINAL
				if (comprobanteNuevo.getConceptos() != null) {
					for (ComprobanteConcepto cc : comprobanteNuevo
							.getConceptos()) {
						getInstance().updateConcepto(comprobanteNuevo, cc,
								user.getScreenName(), con, entidad);
					}
				}
				// EL COMPROBANTE A GRABAR DEBE TENER OTRO NOMBRE
				cambiarNombreComprobante(comprobante, connectionParameter,
						entidad);
			}

			getInstance().save(comprobante, user.getScreenName(), con, entidad);

			if (comprobante.getConceptos() != null) {
				for (ComprobanteConcepto cc : comprobante.getConceptos()) {
					getInstance().saveConcepto(comprobante, cc,
							user.getScreenName(), con, entidad);
				}
			}
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}
	}

	public static void save(Comprobante comprobante, User user,
			Connection connectionParameter, int entidad) throws Exception {
		save(comprobante, user, connectionParameter, entidad, true);
	}

	public static void saveSinGenerarNuevoNroNDB(Comprobante comprobante,
			User user, Connection connectionParameter, int entidad)
			throws Exception {
		save(comprobante, user, connectionParameter, entidad, false);
	}

	public static String getUltimoNumeroComprobante(String tipo, String cuit,
			String sucu, int entidad) throws SystemException {

		String nro = null;
		if (tipo.equals("NDB")) {
			nro = getUltimoNroDebito(entidad);
		} else {
			nro = getInstance().getUltimoNumeroComprobante(tipo, cuit, sucu,
					entidad);
			if (nro != null) {
				nro = nro.replaceAll(cuit + "-" + sucu + "/", "");
			}
		}
		return nro;
	}

	public static List<Comprobante> getComprobantesImpagosConConceptos(
			Comprobante comp, int entidad) throws SystemException {
		List<Comprobante> comprobantes = getInstance()
				.getComprobantesImpagosNoLiquidaciones(comp, entidad);

		if (comprobantes != null) {
			for (Comprobante c : comprobantes) {
				c.setConceptos(getInstance().getConceptos(c, entidad));
			}
		}

		return comprobantes;
	}

	private static Comprobante crearNuevoComprobanteParcial(
			Comprobante comprobante) {
		Comprobante nuevoComprobante = comprobante.clone();

		List<ComprobanteConcepto> nuevosConceptos = new ArrayList<ComprobanteConcepto>();
		BigDecimal importeCompro = BigDecimal.ZERO;
		for (ComprobanteConcepto cc : comprobante.getConceptos()) {
			ComprobanteConcepto nuevoConcepto = new ComprobanteConcepto(
					cc.getConceptoComprobante(), cc.getImporteOriginal()
							.subtract(cc.getImporte()));
			nuevosConceptos.add(nuevoConcepto);
			importeCompro = importeCompro.add(cc.getImporteOriginal().subtract(
					cc.getImporte()));
		}
		nuevoComprobante.setConceptos(nuevosConceptos);
		nuevoComprobante.setImporteComprobante(importeCompro);
		return nuevoComprobante;
	}

	private static void cambiarNombreComprobante(Comprobante comprobante,
			Connection connectionParameter, int entidad) throws SystemException {
		String comproNro = comprobante.getNroComprobante();
		String comproNroNuevo = new String();
		int cont = getInstance().getContadorPagoParcial(comprobante,
				connectionParameter, entidad);
		cont++;
		comproNroNuevo = comproNro.concat("&").concat(String.valueOf(cont));
		comprobante.setNroComprobante(comproNroNuevo);
	}

	public static List<OrdenPago.FormaPago> getAnticipoARendir(
			String cuitEntidad, String sucuEntidad, String idSeccional,
			int entidad) throws SystemException {

		String sucu = sucuEntidad;
		Empresa empresa = null;
		Seccional seccional = null;

		if (StringUtils.checkNotEmpty(idSeccional)
				&& Integer.parseInt(idSeccional) != 0) {
			seccional = new Seccional(Integer.parseInt(idSeccional), null,
					cuitEntidad);
			sucu = "000";
		}

		if (StringUtils.checkNotEmpty(cuitEntidad)) {
			empresa = new Empresa(cuitEntidad, sucu, null);
		}

		List<Anticipo> anticipos = getInstance().getAnticiposARendir(empresa,
				seccional != null ? seccional.getId() : 0, entidad);

		List<OrdenPago.FormaPago> pagos = new ArrayList<OrdenPago.FormaPago>();
		if (anticipos != null) {
			for (Anticipo ant : anticipos) {
				pagos.add(new OrdenPago.FormaPago(ant));
				List<ComprobanteConcepto> conc = ComprobanteServiceUtil
						.getConceptos(ant.getAnticipo(), entidad);
				ant.getAnticipo().setConceptos(conc);
			}
		}
		return pagos;
	}

	public static void saveAsociacionLiquidacion(int idLiquidacion,
			Comprobante compLiquidacion, String user) throws SystemException {
		getInstance().save(idLiquidacion, compLiquidacion, user);
	}

	public static void saveAsociacionOp(Integer id, Comprobante comp,
			String user, Connection con, int entidad) throws SystemException {
		getInstance().save(id, comp, user, con, entidad);
	}

	public static void anular(Comprobante comp, User user, int entidad,
			boolean borrarTotal) throws Exception {
		Connection connectionForTransaction = ConnectionHelper
				.getConnectionForTransaction();
		Comprobante comprobante = getComprobante(comp, entidad);
		try {
			// if (!borrarTotal) {
			// Comprobante comprobanteNDB = new Comprobante(comprobante);
			// comprobanteNDB
			// .setTipoComprobante(WebKeysGlobal.COMPROBANTE_NOTA_DEBITO);
			// comprobanteNDB.setFechaRecepcion(new Date());
			// if (isOspim) {
			// comprobanteNDB.setCuit(WebKeysGlobal.CUIT_OSPIM);
			// } else {
			// comprobanteNDB.setCuit(WebKeysGlobal.CUIT_AMTIMA);
			// }
			// saveSinGenerarNuevoNroNDB(comprobanteNDB, user,
			// connectionForTransaction, isOspim);
			// getInstance().anular(comprobanteNDB, user.getScreenName(),
			// isOspim, borrarTotal, connectionForTransaction);
			// }
			getInstance().anular(comprobante, user.getScreenName(), entidad,
					borrarTotal, connectionForTransaction);
			connectionForTransaction.commit();
		} catch (SystemException e) {
			ConnectionHelper.rollback(connectionForTransaction);
			throw e;
		} finally {
			ConnectionHelper.cerrar(connectionForTransaction);
		}
	}

	public static List<Comprobante> getAnticiposARendir(Comprobante comp,
			int entidad) throws SystemException {

		List<Comprobante> anticiposARendir = getInstance().getAnticiposARendir(
				comp);

		if (anticiposARendir != null) {
			for (Comprobante c : anticiposARendir) {
				List<ComprobanteConcepto> conc = ComprobanteServiceUtil
						.getConceptos(c, entidad);
				c.setConceptos(conc);
			}
		}
		return anticiposARendir;
	}

	public static List<OrdenPagoOspim> getComprobantesOPSubdiario(
			Date fechaIni, Date fechaFin, Connection connection)
			throws SystemException {
		List<OrdenPagoOspim> ops = null;
		List<Comprobante> comps = null;
		Connection con = null;
		try {
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			ops = getInstance().getComprobantesOP(fechaIni, fechaFin);
			comps = getInstance().getConceptosOPSubdiario(fechaIni, fechaFin,
					connection);
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(con);
			}
		}
		for (OrdenPago op : ops) {
			if (op.getComprobantes() != null) {
				for (Comprobante c : op.getComprobantes()) {
					if (comps.contains(c)) {
						int indexOf = comps.indexOf(c);
						if (c.getIdOp() == op.getId()) {
							c.setConceptos(comps.get(indexOf).getConceptos());
						}
					}
				}
			}
		}
		return ops;

	}

	public static List<OrdenPago> getComprobantesOPSubdiario(Date fechaIni,
			Date fechaFin, int entidad, Connection connection)
			throws SystemException {
		Connection con = null;
		List<OrdenPago> ops = null;
		List<Comprobante> comps = null;
		try {
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			ops = getInstance().getComprobantesOP(fechaIni, fechaFin, entidad,
					con);
			comps = getInstance().getConceptosOPSubdiario(fechaIni, fechaFin,
					entidad, con);
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(con);
			}
		}

		for (OrdenPago op : ops) {
			if (op.getComprobantes() != null) {
				for (Comprobante c : op.getComprobantes()) {
					if (comps.contains(c)) {
						int indexOf = comps.indexOf(c);
						if (c.getIdOp() == op.getId()) {
							c.setConceptos(comps.get(indexOf).getConceptos());
						}
					}
				}
			}
		}
		return ops;

	}

	public static void getComprobantesConConceptos(OrdenPago ordenPago,
			int entidad) throws SystemException {
		List<Comprobante> comprobantes = getInstance().getComprobantes(
				ordenPago, entidad);
		List<Comprobante> conceptos = getInstance().getConceptos(ordenPago,
				entidad);

		for (Comprobante c : comprobantes) {
			if (conceptos.contains(c)) {
				int indexOf = conceptos.indexOf(c);
				c.setConceptos(conceptos.get(indexOf).getConceptos());
			}
		}
		ordenPago.setComprobantes(comprobantes);
	}

	public static int actualizaSaldoAnticipoRecibo(int reciboId,
			Comprobante comp, String user, int entidad) throws SystemException {
		return getInstance().actualizaSaldoAnticipoRecibo(reciboId, comp, user,
				entidad);
	}

	public static List<ItemAnticipoOP> listadoAnticiposPagos(Date fechaIni,
			Date fechaFin, Date fechaUtil, String cuit, String sucursal,
			int idSeccional, int entidad) throws SystemException {
		List<ItemAnticipoOP> conceptos = getInstance().listadoAnticiposPagos(
				fechaIni, fechaFin, fechaUtil, cuit, sucursal, idSeccional,
				entidad);
		return conceptos;
	}

	public static Comprobante getUltimoComprobanteAmtimaAutomatico()
			throws SystemException {
		return getInstance().getUltimoComprobanteAmtimaAutomatico();
	}

	public static Comprobante getUltimoComprobanteOspimAutomatico()
			throws SystemException {
		return getInstance().getUltimoComprobanteOspimAutomatico();
	}

	public static Comprobante getUltimoComprobanteNDFOspimAutomatico()
			throws SystemException {
		return getInstance().getUltimoComprobanteNDFOspimAutomatico();
	}
	
	
	//public static List<OrdenPago> getComprobantesOPSubdiario(Date fechaIni,
	
	public static List<OrdenPago> getPerceptionesIIBB_by_OP(Date fechaIni,
			Date fechaFin,int pConcepto,int entidad, Connection connection)
			throws SystemException {
		Connection con = null;
		List<OrdenPago> ops = new ArrayList<OrdenPago>();
		List<OrdenPago> opsSalida = new ArrayList<OrdenPago>();
		List<Localidad>percepciones=TraeListasServiceUtil.getPercepcionesIIBB(entidad);
		try {
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			ops = getComprobantesOPSubdiario(fechaIni,fechaFin,entidad,connection);
					
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(con);
			}
		}
		boolean esPercepcion=false;
		for (OrdenPago op : ops) {
			if (op.getComprobantes() != null) {
				for (Comprobante c : op.getComprobantes()) {
					for(ComprobanteConcepto cc:c.getConceptos()) {
						esPercepcion=false;
						for(Localidad l:percepciones) {
							if(l.getId()==cc.getConceptoComprobante().getId()  && ( pConcepto==0 || pConcepto==l.getId()) ) {
							   ComprobanteConcepto cc1=new ComprobanteConcepto();
							   Concepto concepto = new Concepto(l.getId_provincia(),cc.getDescripcionPAraSubdiario());
							   cc1.setConceptoComprobante(concepto);
							   cc1.setImporte(cc.getImporte());
							   
							   Comprobante comp1 =new Comprobante();
							   comp1.setConceptos(new ArrayList<ComprobanteConcepto>());
							   comp1.getConceptos().add(cc1);
							   comp1.setCuit(c.getCuit());
							   comp1.setFechaEmision(c.getFechaEmision());
							   comp1.setAcreedorEmpresa(c.getAcreedorEmpresa());
							   comp1.setNroComprobante(c.getNroComprobante());
							   comp1.setSucuComprobante(c.getSucuComprobante());
							   comp1.setTipoComprobante(c.getTipoComprobante());
							   comp1.setLetraComprobante(c.getLetraComprobante());
							   
							   OrdenPagoOspim op1 = new OrdenPagoOspim();
							   //op1.setAcreedor(op.getAcreedor());
							   op1.setComprobantes(new ArrayList<Comprobante>());
							   op1.getComprobantes().add(comp1);
							   
							   opsSalida.add(op1);
		                       esPercepcion=true;
		                       break;
							}
						}
					}
				}
			}
		}
		return opsSalida;
	}

	
	public static List<Comprobante> getLibroIVA(Date fechaIni,
			Date fechaFin, String libro, int entidad, Connection connection)
			throws SystemException {
		Connection con = null;
		
		List<Factura> facturas = new ArrayList<Factura>();
		List<Comprobante> comprobLibro = new ArrayList<Comprobante>();
//		Map<String,List<Integer>> librocfg = TraeListasServiceUtil.getLibroIVACompras(entidad);
		try {
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			
			if("COMPRAS".equalsIgnoreCase(libro)) {
			   comprobLibro= getInstance().getComprobantesLibroIVACompras(fechaIni, fechaFin, entidad, connection);
			
			} else {
				
				facturas = facturacionServiceUtil.getFacturasPeriodo(fechaIni, fechaFin);
			}
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(con);
			}
		}
		
		if("COMPRAS".equalsIgnoreCase(libro)) {
			
			for(Comprobante c:comprobLibro) {
				String df="";
				try {
					df =EmpresaServiceUtil.traerPrestadorDomicilioFiscal(c.getCuit());
					String[]vdf =df.split("Provincia de");
					if(vdf.length>1) {
					  df=vdf[1];
					}else {
					  df="";	
					}
				} catch (SQLException e) {
					df=""; 
				}
				c.getAcreedorEmpresa().setDomicilioAfip(df);
			}
		
		}else { //VENTAS
			
			String cuit ="";
			String razonSocial="";
			String tipoDoc="";
			BigDecimal iva21=BigDecimal.ZERO;
			HashMap domSucu = new HashMap<String,String>();
			String domicilio= "";
			for(Factura f:facturas) {
				Comprobante comp1 =new Comprobante();
				iva21=BigDecimal.ZERO;
				if(f.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.EMPRESA) 
						&& StringUtils.checkNotEmpty( f.getCliente().getCuit() ) ) {
				  cuit=	f.getCliente().getCuit();
				  razonSocial=f.getCliente().getRazonSocial();
				  tipoDoc="80";
				}else if(f.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.AFILIADO) 
						&& StringUtils.checkNotEmpty(f.getCliente().getCuilTitular())) {
					cuit= f.getCliente().getCuilTitular();
					razonSocial = (f.getCliente().getApellido()!=null?f.getCliente().getApellido():"") + " " +
							      (f.getCliente().getNombre()!=null?f.getCliente().getNombre():"");
					tipoDoc="86";
//				}else if (f.getCliente().getCuil()!=null) {
//					cuit= f.getCliente().getCuil();
//					razonSocial = (f.getCliente().getApellido()!=null?f.getCliente().getApellido():"") + " " +
//							      (f.getCliente().getNombre()!=null?f.getCliente().getNombre():"");
//					tipoDoc="86";
				}else if (f.getCliente().getTipo().equals(Cliente.TIPOS_CLIENTE.VISITA) 
				&& StringUtils.checkNotEmpty(f.getCliente().getDocumentoNro())) {
					cuit= f.getCliente().getDocumentoNro();
					razonSocial = (f.getCliente().getApellido()!=null?f.getCliente().getApellido():"") + " " +
							      (f.getCliente().getNombre()!=null?f.getCliente().getNombre():"");
					tipoDoc="86";	
				}
				comp1.setCuit(cuit);
				comp1.setFechaEmision(f.getFecha() );
				Empresa empresa=new Empresa();
				
				empresa.setCuit(cuit);
				empresa.setRazon_soc(razonSocial);
				empresa.setImpIva(f.getCliente().getCategoriaIVA());
				empresa.setCartaDoc(tipoDoc);
				comp1.setAcreedorEmpresa(empresa);
//				if(f.getIva().doubleValue()>0D) {
////					comp1.setGravadoIVA21(f.getImporteNeto());
////					comp1.setGravadoIVA105(BigDecimal.ZERO);
//					comp1.setExento(BigDecimal.ZERO);
//					iva21=f.getIva();
//				}else {
//					
//					if("B".equalsIgnoreCase(f.getLetra()) ) {
//						comp1.setExento(BigDecimal.ZERO);
////						comp1.setGravadoIVA21(f.getTotalNeto().divide(BigDecimal.valueOf(1.21D), 2, RoundingMode.HALF_EVEN));
//						iva21=f.getTotalNeto().subtract(f.getTotalNeto().divide(BigDecimal.valueOf(1.21D), 2, RoundingMode.HALF_EVEN));
//					}else {
////					   comp1.setGravadoIVA21(BigDecimal.ZERO);
////					   comp1.setGravadoIVA105(BigDecimal.ZERO);
//					   comp1.setExento(f.getImporteExento());
//					}
//				}
				comp1.setGravadoIVA105(BigDecimal.ZERO);
				comp1.setGravadoIVA21(f.getImporteNeto() );
				comp1.setExento(f.getImporteExento());
				
				domicilio=(String) domSucu.get(f.getSucursal());
				if(domicilio==null || "".equalsIgnoreCase(domicilio)) {
				   ClaseBase cb = FacturacionServiceUtil.getPtoVtaJurisdiccion(f.getSucursal());
				   domicilio=cb.getDescripcion();
				   domSucu.put(f.getSucursal(), domicilio);
				}   
				comp1.getAcreedorEmpresa().setDomicilioAfip(domicilio);
				
				comp1.setNroComprobante(f.getNumero());
				comp1.setSucuComprobante(Integer.parseInt(f.getSucursal()));
				comp1.setTipoComprobante(f.getTipo());
				comp1.setLetraComprobante(f.getLetra());
				
				comp1.setIva21(f.getIva());
//				comp1.setIva105(BigDecimal.ZERO);
				comp1.setPercepcionIVA(BigDecimal.ZERO);
				comp1.setPercepcionIIBB(f.getPercepcion());
				comp1.setRetenciones(BigDecimal.ZERO);
//				comp1.setImporteComprobante(f.getTotalNeto().add(f.getTotalExento()).add(f.getIva()));
				comp1.setImporteComprobante(f.getImporteTotalCalculado());
				comp1.setReintegroIVA(f.getIvaReintegro());
				comprobLibro.add(comp1);
				
			}
		}
		return comprobLibro;
	}
	
	
	public static void saveExtendido(Comprobante comprobante, User user, int entidad)
			throws Exception {
		saveExtendido(comprobante, user, null, entidad);
	}
	
	public static void saveExtendido(Comprobante comprobante, User user,
			Connection connectionParameter, int entidad) throws Exception {
		saveExtendido(comprobante, user, connectionParameter, entidad, true);
	}

	public static void saveExtendido(Comprobante comprobante, User user,
			Connection connectionParameter, int entidad,
			boolean generarNuevoNroNDB) throws Exception {
		Connection con = null;
		try {
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}

			if (comprobante.getAfiliado() != null
					&& comprobante.getAfiliado().getCuil_titular() != null) {
				try {
					EmpresaServiceUtil.getInstance().saveAfiliadoComoEmpresa(
							comprobante.getAfiliado().getCuil_titular(),
							user.getScreenName());
				} catch (Exception e) {
					_log.error(e);
				}

			}

			if (generarNuevoNroNDB
					&& comprobante.getTipoComprobante().equals("NDB")) {
				String compro_numero = getInstance().generarSiguienteDebito(
						con, comprobante.getTipoComprobante(), entidad);
				comprobante.setNroComprobante(compro_numero);
			}

			// EL COMPROBANTE ESTA PAGADO PARCIALMENTE
			if (null != comprobante.getImporteComprobanteOriginal()
					&& comprobante.getImporteComprobante().compareTo(
							comprobante.getImporteComprobanteOriginal()) < 0) {
				// COMPROBANTE POR LA DIFERENCIA...
				Comprobante comprobanteNuevo = crearNuevoComprobanteParcial(comprobante);
				getInstance().update(comprobanteNuevo, user.getScreenName(),
						connectionParameter, entidad);
				// DEBEMOS ACTUALIZAR EL COMPRO ORIGINAL
				if (comprobanteNuevo.getConceptos() != null) {
					for (ComprobanteConcepto cc : comprobanteNuevo
							.getConceptos()) {
						getInstance().updateConceptoExtendido(comprobanteNuevo, cc,
								user.getScreenName(), con, entidad);
					}
				}
				// EL COMPROBANTE A GRABAR DEBE TENER OTRO NOMBRE
				cambiarNombreComprobante(comprobante, connectionParameter,
						entidad);
			}

			getInstance().save(comprobante, user.getScreenName(), con, entidad);

			if (comprobante.getConceptos() != null) {
				for (ComprobanteConcepto cc : comprobante.getConceptos()) {
					getInstance().saveConceptoExtendido(comprobante, cc,
							user.getScreenName(), con, entidad);
				}
			}
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}
	}

	
	public static void updateExtendido(Comprobante comp, User user, int entidad)
			throws SystemException, SQLException {
		_log.debug("obteniendo conexion");
		Connection con = ConnectionHelper.getConnection();
		try {
			con.setAutoCommit(false);
			getInstance().update(comp, user.getScreenName(), con, entidad);

			if (comp.getConceptos() != null) {
				for (ComprobanteConcepto cc : comp.getConceptos()) {
					if (cc.isBorradoLogicamente()) {
						getInstance().borrarConceptoExtendido(comp, cc,
								user.getScreenName(), con, entidad);
					}else {
						if (cc.getAlta_fecha() == null) {
							getInstance().saveConceptoExtendido(comp, cc,
									user.getScreenName(), con, entidad);
						} else {
							getInstance().updateConceptoExtendido(comp, cc,
									user.getScreenName(), con, entidad);
						}
					}
				}
				
				/*
				for (ComprobanteConcepto cc : comp.getConceptos()) {
					if (cc.getAlta_fecha() == null) {
						getInstance().saveConceptoExtendido(comp, cc,
								user.getScreenName(), con, entidad);
					} else {
						getInstance().updateConceptoExtendido(comp, cc,
								user.getScreenName(), con, entidad);
					}
				}
				*/
			}
			con.commit();
		} catch (SystemException e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);
		}
	}
	
	
	public static List<Comprobante> getComprobantesIIBB(Date fechaIni,
			Date fechaFin, int entidad,Integer jurisdiccion ,Connection connection)
			throws SystemException {
		Connection con = null;
		List<Comprobante> comps = null;
		try {
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			comps = getInstance().getComprobantesIIBB(fechaIni, fechaFin,
					entidad,jurisdiccion ,con);
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(con);
			}
		}
		return comps;

	}

	public static List<Comprobante> getComprobantesGlobales(Comprobante comprobante,
			int entidad,Integer offset) throws Exception {
		List<Comprobante> comprobantes =  getInstance().getComprobantesGlobales(
				comprobante, entidad,offset);
		return comprobantes;
	}
		
}
