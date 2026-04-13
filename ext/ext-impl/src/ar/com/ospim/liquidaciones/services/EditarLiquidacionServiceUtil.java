package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ar.com.ospim.afiliados.AfliadoYaTieneConyugeException;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteHospital;
import ar.com.ospim.global.ComprobanteExistenteException;
import ar.com.ospim.global.ComprobanteInexistenteException;
import ar.com.ospim.global.EmpresaNoExisteConTalCuitException;
import ar.com.ospim.global.PrestacionComprobanteExistenteException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.ComprobanteItem;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.ComprobanteItemServiceUtil;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateReintegroPrestacionIdException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionEntryException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionPrestacionEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.action.LiquidacionActionUtil;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacionAjuste;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

/**
 * Mascara del servicio que da acceso a los datos de la aplicación (BD).
 */
public class EditarLiquidacionServiceUtil {

	private static EditarLiquidacionServiceImpl instance = null;

	public static EditarLiquidacionServiceImpl getInstance() {
		if (null == instance) {
			instance = new EditarLiquidacionServiceImpl();
		}
		return instance;
	}

	/**
	 * Obtiene el afiliado por su clave primaria
	 * 
	 * @param id_liquidacion
	 * @throws SystemException
	 * @throws NoSuchLiquidacionEntryException
	 */
	public static Liquidacion getLiquidacionEntry(int id_liquidacion)
			throws SystemException, NoSuchLiquidacionEntryException {
		Liquidacion liquidacion = getInstance().getLiquidacionEntry(
				id_liquidacion);
		liquidacion.setLiquidacionPrestacion(getInstance()
				.getPrestacionesLiquidacionEntry(id_liquidacion));
		liquidacion = getInstance().traeResumenOP(liquidacion, id_liquidacion);
		Comprobante comprobante = ComprobanteServiceUtil.getComprobanteLiquidacionPorId(id_liquidacion);
		if (comprobante != null) {
			//comprobante tienen todos los activos, los dados de baja no tienen	
			comprobante.setConceptos(ComprobanteServiceUtil.getConceptos(comprobante, WebKeysGlobal.OSPIM));
			liquidacion.setComprobante(comprobante);
		}
		try {
			liquidacion.setDebitos(DebitoServiceUtil.buscaDebitos(id_liquidacion));
		}
		catch (Exception e) {
			liquidacion.setDebitos(new ArrayList<ComprobanteItem>());
		}
		return liquidacion;
	}

	/**
	 * carga un nuevo afiliado
	 * @throws Exception 
	 */
	public static int cargaLiquidacionEntry(Date fecha, Date fechaE,
			Date fechaR, Date fechaV, Date periodo, String entidad_liquidacion,
			int id_prestador, int id_domicilio, String compro_a_debitar_tipo,
			String compro_a_debitar_letra, int sucu,
			String compro_a_debitar_numero, String entidad,
			String cuil_titular, int inte, int seccional, Date prestacionFecha,
			int id_prestacion, String tipo_liquidacion, int estado,
			User user, String cantidad, String importe, String tercerizado,
			String solicitado, String debitado, String resultado, String servicio, 
			String importe_total, String debitado_total, String nroOC,
			String observaciones, String tercerizado_cab, String cuit_prestador, 
			int id_concepto, String importe_concepto, Date periodoPrestacion, 
			int motivoAltaDiscapacidad,int idReclamo , int idPrestacionReclamo , 
			BigDecimal cargoOspim, BigDecimal cargoPrestadora , BigDecimal cargoOmint,
			BigDecimal cargoEnSalud,BigDecimal cargoCemic,BigDecimal cargoImesa) throws Exception {
		
		Empresa empresa = EmpresaServiceUtil.getEmpleadorCompleto(cuit_prestador, "000");
		if (empresa == null) {
			
			// SE PIDE QUE SE PUEDA LIQUIDAR SIN QUE ESTE EN LA TABLA EMPRESA.
			//throw new EmpresaNoExisteConTalCuitException();
			empresa=new Empresa(cuit_prestador, "000");
			EmpresaServiceUtil.save(empresa, user.getScreenName());
		}
		
		List<Empresa> busqueda = EmpresaServiceUtil
			.getEmpleadores(cuit_prestador, null, String.valueOf(id_prestador),0);

		String sucu_prestador = busqueda.size() == 1 ? String.valueOf(id_prestador) : "000";
		
		Comprobante comprobante = new Comprobante(sucu, compro_a_debitar_tipo,
				compro_a_debitar_numero, cuit_prestador, fechaE, fechaR,
				new BigDecimal(importe_total), compro_a_debitar_letra, sucu, fechaV, new Empresa(cuit_prestador, sucu_prestador, ""), periodo);
		
		int id_liquidacion=0;

		validaPrestacionLiquidacion(id_liquidacion,id_prestacion,cuil_titular,inte,id_prestador,compro_a_debitar_tipo,
				compro_a_debitar_letra,compro_a_debitar_numero,prestacionFecha,null);	
		
		Comprobante comprobanteEnBase = ComprobanteServiceUtil
				.getComprobante(comprobante, WebKeysGlobal.OSPIM);
		
		if (comprobanteEnBase != null) {
			throw new ComprobanteExistenteException();
		}
		
		id_liquidacion = getInstance().cargaLiquidacionEntry(fecha, fechaE,
				fechaR, fechaV, periodo, entidad_liquidacion, id_prestador,
				id_domicilio, compro_a_debitar_tipo, compro_a_debitar_letra,
				sucu, comprobante.getNroComprobante(), tipo_liquidacion, estado, user.getScreenName(), 
				null, null, new BigDecimal(importe_total), new BigDecimal(debitado_total), nroOC, 
				observaciones, tercerizado_cab, cuit_prestador , cargoOspim, cargoPrestadora, cargoOmint,cargoEnSalud,cargoCemic,cargoImesa);
		
/*		
		validaPrestacionLiquidacion(id_liquidacion,id_prestacion,cuil_titular,inte,id_prestador,compro_a_debitar_tipo,
				compro_a_debitar_letra,compro_a_debitar_numero,prestacionFecha,null);	
		
		Comprobante comprobanteEnBase = ComprobanteServiceUtil
				.getComprobante(comprobante, WebKeysGlobal.OSPIM);
		
		if (comprobanteEnBase != null) {
			throw new ComprobanteExistenteException();
		}
*/		
		//graba concepto del request en la liquidacion
		if (importe_concepto != null && (!importe_concepto.equalsIgnoreCase("") && !importe_concepto.equalsIgnoreCase("0"))) {
			List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
			ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
					new Concepto(id_concepto),
					new BigDecimal(importe_concepto));
			conceptos.add(comprobanteConcepto);
			comprobante.setConceptos(conceptos);
		}
		
		DebitoServiceUtil.grabarComprobanteAsociarLiquidacion(id_liquidacion,
				comprobante, user);
		
		if(comprobante.getNroComprobante().contains("/") && comprobante.getTipoComprobante().equalsIgnoreCase(WebKeysGlobal.COMPROBANTE_FACTURA)) {
			enviarNotificacionComprobanteDuplicado(comprobante.toString(), user.getScreenName());
		}
		
		//si es liquidación tercerizada debe guardar también la prestación
		if (tercerizado_cab.equals("0")) {
			BigDecimal cant = new BigDecimal(cantidad);			
			int[] idPrestacion = new int[1]; 
			int orden = getInstance().cargaLiquidacionPrestacionEntry(id_liquidacion, cuil_titular, inte, id_prestacion, prestacionFecha, new BigDecimal(cantidad),
					new BigDecimal(importe), servicio, new BigDecimal(solicitado), new BigDecimal(debitado), new BigDecimal(resultado), tercerizado, user.getScreenName(), periodoPrestacion, 
					motivoAltaDiscapacidad,idPrestacion , cargoOspim,  cargoPrestadora.add(!cargoEnSalud.equals(BigDecimal.ZERO)?cargoEnSalud:BigDecimal.ZERO),cargoImesa);
			
			// graba los datos del reclamo prestacional asociado 
			if (idReclamo!=0 && idPrestacionReclamo!=0){
				getInstance().grabaDatosDelReclamoPrestacionaldelaLiquidacion(id_liquidacion, idPrestacion[0] ,  idReclamo , idPrestacionReclamo, user.getScreenName());
			}

			
			ComprobanteItem comprobanteItem = new ComprobanteItem(sucu, compro_a_debitar_tipo, comprobante.getNroComprobante(), cuit_prestador, compro_a_debitar_letra, 
					sucu, orden, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), cant.multiply(new BigDecimal(importe)), "", 0);
			ComprobanteItemServiceUtil.save(comprobanteItem, user);
		}	
		
		return id_liquidacion;
	}

	/**
	 * actualiza un afiliado existente
	 * 
	 * @throws NoSuchLiquidacionEntryException 
	 * @throws Exception 
	 * @throws AfliadoYaTieneConyugeException
	 */
	public static void actualizaLiquidacionEntry(int id_liquidacion,
			Date fecha, Date fechaE, Date fechaR, Date fechaV, Date periodo,
			String cuil_titular, int inte, int id_prestador, int id_domicilio, 
			int id_prestacion, Date prestacionFecha, String cantidad, String importe, 
			String compro_a_debitar_tipo, String letra_compro, int sucu, 
			String compro_a_debitar_numero, String tercerizado, User user, 
			String servicio, String solicitado, String debitado, String resultado, 			
			String importe_total, String debitado_total, String nroOC, String observaciones, 
			String tercerizado_cab, String cuit_prestador, int id_concepto, 
			String importe_concepto, ActionRequest actionRequest, Date periodoPrestacion,
			int motivoAltaDiscapacidad, int idReclamo , int idPrestacionReclamo , BigDecimal cargoOspim, 
			BigDecimal cargoPrestadora , BigDecimal cargoOmin, BigDecimal cargoEnSalud , BigDecimal cargoCemic,BigDecimal cargoImesa) throws Exception {
		
		List<Empresa> busqueda = EmpresaServiceUtil
		.getEmpleadores(cuit_prestador, null, String.valueOf(id_prestador),0);

		String sucu_prestador = busqueda.size() == 1 ? String.valueOf(id_prestador) : "000";
		
		Comprobante comprobante = new Comprobante(sucu, compro_a_debitar_tipo,
				compro_a_debitar_numero, cuit_prestador, fechaE, fechaR,
				new BigDecimal(importe_total), letra_compro, sucu, fechaV, new Empresa(cuit_prestador, sucu_prestador, ""), periodo );
		
		validaPrestacionLiquidacion(id_liquidacion,id_prestacion,cuil_titular,inte,id_prestador,compro_a_debitar_tipo,
				letra_compro,compro_a_debitar_numero,prestacionFecha,null);
		
		
		Comprobante comprobanteEnBase = ComprobanteServiceUtil
			.getComprobante(comprobante, WebKeysGlobal.OSPIM);
		int id_liq_comprobante = ComprobanteServiceUtil.getIdComprobanteLiquidacion(comprobante);
		
		//Valida si ya existe el comprobante
		if (comprobanteEnBase != null && id_liquidacion != id_liq_comprobante) {
			throw new ComprobanteExistenteException();
		}
		Comprobante compLiquidacion = ComprobanteServiceUtil.getComprobanteLiquidacionPorId(id_liquidacion);
		if (compLiquidacion == null) {
			throw new ComprobanteInexistenteException();
		}
		//Actualiza liquidacion
		getInstance().actualizaLiquidacionEntry(id_liquidacion, fecha, fechaE, fechaR, fechaV, periodo, 
				id_prestador, id_domicilio, compro_a_debitar_tipo, letra_compro, sucu, compro_a_debitar_numero, 
				new BigDecimal(importe_total), new BigDecimal(debitado_total), nroOC, observaciones, 
				tercerizado_cab, user.getScreenName(), cuit_prestador, cargoOspim, cargoPrestadora, cargoOmin, cargoEnSalud,cargoCemic,cargoImesa);
		
		getInstance().cambiarEstadoLiquidacionEntry(id_liquidacion, WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO, user.getScreenName());
		//Actualiza comprobante y comprobante de liquidacion
		if (comprobanteEnBase != null && comprobanteEnBase.equals(compLiquidacion)) {

			//actualiza conceptos asociados al comprobante que se está actualizando.
			List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
			ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
					new Concepto(id_concepto),
					comprobante.getImporteComprobante());
			comprobanteConcepto.setBorradoLogicamente(true);
			comprobanteConcepto.setAlta_fecha(new Date());
			conceptos.add(comprobanteConcepto);
			
			if (importe_concepto != null && (!importe_concepto.equalsIgnoreCase("") && !importe_concepto.equalsIgnoreCase("0"))) {
				ComprobanteConcepto comprobanteConceptoNuevo = new ComprobanteConcepto(
						new Concepto(id_concepto),
						new BigDecimal(importe_concepto));
				conceptos.add(comprobanteConceptoNuevo);
			}
			comprobante.setConceptos(conceptos);
			// actualiza el comprobante con el nuevo importe
			ComprobanteServiceUtil.update(comprobante, user, WebKeysGlobal.OSPIM);
		} else {
			
			//crea conceptos asociados a la nota débito que se está insertando
			if (importe_concepto != null && (!importe_concepto.equalsIgnoreCase("") && !importe_concepto.equalsIgnoreCase("0"))) {
				List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
				ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
					new Concepto(id_concepto),
					new BigDecimal(importe_concepto));
				conceptos.add(comprobanteConcepto);
				comprobante.setConceptos(conceptos);
			}
			
			DebitoServiceUtil.grabarComprobanteAsociarLiquidacion(
					id_liquidacion, comprobante, user);
		}
		
		if (tercerizado_cab.equals("0")) {
			//si fue modificado el detalle
			if (cuil_titular.length() > 0 && id_prestacion != 0 && (cantidad != null && Double.valueOf(cantidad) > 0) && importe.length() > 0) {
				
				int[] idPrestacion = new int[1];
				//Si es tercerizado carga la nueva prestación				
				int orden = getInstance().cargaLiquidacionPrestacionEntry(id_liquidacion, cuil_titular, inte, id_prestacion, prestacionFecha, new BigDecimal(cantidad),
					new BigDecimal(importe),servicio, new BigDecimal(solicitado), new BigDecimal(debitado), new BigDecimal(resultado), tercerizado, user.getScreenName(),
					periodoPrestacion, motivoAltaDiscapacidad,idPrestacion , cargoOspim,  cargoPrestadora.add(!cargoEnSalud.equals(BigDecimal.ZERO)?cargoEnSalud:BigDecimal.ZERO),cargoImesa);
				BigDecimal cant = new BigDecimal(cantidad);
				
				// graba los datos del reclamo prestacional asociado 
				if (idReclamo!=0 && idPrestacionReclamo!=0){
					getInstance().grabaDatosDelReclamoPrestacionaldelaLiquidacion(id_liquidacion, idPrestacion[0] ,  idReclamo , idPrestacionReclamo, user.getScreenName());
				}
				//Carga el nuevo comprobante de la prestación
				//Borro primero posibles daots de la sesión
				//poner o borrar de la sesion el dato del servicio especial para que sea más fácil en t´perminos de la edición
				HttpServletRequest httpServletRequest = PortalUtil
					.getHttpServletRequest(actionRequest);
				HttpSession session = (HttpSession) httpServletRequest.getSession();
				if (EditarLiquidacionServiceUtil.servicioEspecial(servicio)) {
					session.setAttribute("cuil_titular_servicio", cuil_titular);
					session.setAttribute("inte_titular_servicio", inte);
					session.setAttribute("servicio", servicio);
					session.setAttribute("fecha_prestacion_servicio", prestacionFecha);
				} else {
					session.removeAttribute("cuil_titular_servicio");
					session.removeAttribute("inte_servicio");
					session.removeAttribute("servicio");
					session.removeAttribute("fecha_prestacion_servicio");
				}				
				ComprobanteItem comprobanteItem = new ComprobanteItem(sucu, compro_a_debitar_tipo, compro_a_debitar_numero, cuit_prestador, letra_compro, 
						sucu, orden, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), cant.multiply(new BigDecimal(importe)), "", 0);			
				if (comprobanteEnBase == null || !comprobanteEnBase.equals(compLiquidacion)) {
					ComprobanteItemServiceUtil.actualizarItemsLiquidacion(id_liquidacion, comprobanteItem, user);
				}
				ComprobanteItemServiceUtil.save(comprobanteItem, user);
				
			}
		}
		//Borra el comprobante anterior y el comprobante por liquidación
		if (comprobanteEnBase == null || !comprobanteEnBase.equals(compLiquidacion)) {
			ComprobanteServiceUtil.deleteComprobanteLiquidacion(id_liquidacion, user);
		}
	}

	/**
	 * borra un liquidacione
	 * 
	 * @throws SystemException
	 * @throws NoSuchLiquidacionEntryException
	 */
	public static void borraLiquidacionEntry(int id_liquidacion, User user)
			throws NoSuchLiquidacionEntryException, SystemException {
		ComprobanteServiceUtil.deleteComprobanteLiquidacion(id_liquidacion, user);		
		getInstance().borraLiquidacionEntry(id_liquidacion, user.getScreenName());
		
	}

	public static void borraLiquidacionPrestacionEntry(int id_liquidacion,
			int orden, User user  , int id_Reclamo , int id_prestacion_reclamo  ) throws NoSuchLiquidacionPrestacionEntryException, SystemException, ComprobanteInexistenteException, NoSuchLiquidacionEntryException {
		
		if (id_Reclamo !=0 && id_prestacion_reclamo  != 0  ) { 
			ComprobanteItemServiceUtil.deleteLiquidacionReclamo(id_liquidacion, id_Reclamo, id_prestacion_reclamo);
		}
		getInstance().borraLiquidacionPrestacionEntry(id_liquidacion,
				orden, user.getScreenName());		
		Comprobante comp = ComprobanteServiceUtil.getComprobanteLiquidacionPorId(id_liquidacion);
		if (comp == null) {
			throw new ComprobanteInexistenteException();
		}
		ComprobanteItem comprobanteItem = new ComprobanteItem(comp.getSucuComprobante(), comp.getTipoComprobante(), comp.getNroComprobante(),
				comp.getLetraComprobante(), comp.getSucuComprobante(), comp.getCuit(), orden);
		ComprobanteItemServiceUtil.delete(comprobanteItem, user);
			
			
		getInstance().cambiarEstadoLiquidacionEntry(id_liquidacion, WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO, user.getScreenName());
	}
	
	public static void guardarCambiosPrestaciones(ArrayList<LiquidacionPrestacionAjuste> listaPrestacionAjuste, Comprobante c, User user) throws NoSuchLiquidacionPrestacionEntryException, SystemException, NoSuchLiquidacionEntryException, ComprobanteInexistenteException, ComprobanteExistenteException, PrestacionComprobanteExistenteException {
		for (LiquidacionPrestacionAjuste liquidacionPrestacionAjustei : listaPrestacionAjuste) {
			if (liquidacionPrestacionAjustei.getAjuste().equals("ADD")) {
				guardarPrestacion(liquidacionPrestacionAjustei, c, user);				
			} else if (liquidacionPrestacionAjustei.getAjuste().equals("EDIT")){
				editarPrestacion(liquidacionPrestacionAjustei, c, user);				
			} else if (liquidacionPrestacionAjustei.getAjuste().equals("DELETE")){
				borraPrestacion(liquidacionPrestacionAjustei, c, user,0,0);			
			}
		}
	}

	private static void guardarPrestacion(
			LiquidacionPrestacionAjuste p, Comprobante c, User u) throws NoSuchLiquidacionPrestacionEntryException, SystemException {
		
		int [] idPrestacion = new int[1];
		int orden = getInstance().cargaLiquidacionPrestacionEntry(p.getId_liquidacion(), p.getCuil_titular(), p.getInte(), p.getId_prestacion(), p.getFecha_prestacion(), p.getCantidad(),
				p.getImporte(), p.getServicio(), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),  p.getTercerizado(), u.getScreenName(), p.getPeriodo(), p.getMotivoAltaDiscapacidad(),idPrestacion ,
				p.getCargoOspim(),p.getCargoPrestadora(),p.getCargoImesa());
		
		ComprobanteItem comprobanteItem = new ComprobanteItem(c.getSucuComprobante(), c.getTipoComprobante(), c.getNroComprobante(), c.getCuit(), c.getLetraComprobante(), 
				c.getSucuComprobante(), orden, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), p.getCantidad().multiply(p.getImporte()), "", 0);
		ComprobanteItemServiceUtil.save(comprobanteItem, u);
	}

	private static void editarPrestacion(
			LiquidacionPrestacionAjuste p, Comprobante c, User u) throws NoSuchLiquidacionPrestacionEntryException, SystemException, NoSuchLiquidacionEntryException, ComprobanteInexistenteException, ComprobanteExistenteException, PrestacionComprobanteExistenteException {			
		actualizaLiquidacionPrestacionEntry(p.getId_liquidacion(), p.getOrden(), p.getFecha_prestacion(), p.getServicio(), 
				p.getCuil_titular(), p.getInte(), p.getId_prestacion(), p.getCantidad().toPlainString(), p.getImporte().toPlainString(), p.getTercerizado(), u, 
				p.getPeriodo(), p.getMotivoAltaDiscapacidad(),null);
	}

	private static void borraPrestacion(
			LiquidacionPrestacionAjuste p, Comprobante c, User u , int id_reclamo_prestacion, int id_prestacion_reclamo) throws NoSuchLiquidacionPrestacionEntryException, SystemException, NoSuchLiquidacionEntryException, ComprobanteInexistenteException {					
		borraLiquidacionPrestacionEntry(p.getId_liquidacion(), p.getOrden(), u,id_reclamo_prestacion,id_prestacion_reclamo);		
	}
	
	public static void cambiarEstadoLiquidacionEntry(int id_liquidacion, int estado, String userName) throws NoSuchLiquidacionEntryException, SystemException {
		getInstance().cambiarEstadoLiquidacionEntry(id_liquidacion, estado, userName);
	}

	public static boolean validarCierreLiquidacion(int id_liquidacion, ActionRequest action) throws Exception{
		
		Liquidacion liquidacion = getLiquidacionEntry(id_liquidacion);
		List<ComprobanteItem> debitos = DebitoServiceUtil.buscaDebitos(id_liquidacion);
		
		Comprobante comprobante = ComprobanteServiceUtil.getComprobanteLiquidacionPorId(id_liquidacion);		
		
		String tercerizado = liquidacion.getTercerizado();
		BigDecimal total_importe = liquidacion.getImporte() != null ? liquidacion.getImporte() : BigDecimal.ZERO;
		//BigDecimal total_prestaciones = liquidacion.getImporteTotal() != null ? liquidacion.getImporteTotal() : BigDecimal.ZERO;
		BigDecimal total_prestaciones_no_estadisticas = liquidacion.getImporteTotalNoEstadistico() != null ? liquidacion.getImporteTotalNoEstadistico() : BigDecimal.ZERO;
		//debitos cab deja de existir
		//BigDecimal total_debitos_cab = liquidacion.getDebitado() != null ? liquidacion.getDebitado() : BigDecimal.ZERO;
		
		Comprobante.ComprobanteConcepto comprobanteConcepto = ComprobanteServiceUtil.getConceptoConveniosGlobales(comprobante, WebKeysGlobal.OSPIM);
		BigDecimal total_conceptos = comprobanteConcepto != null ? comprobanteConcepto.getImporte() : null;
		if (total_conceptos == null) {
			total_conceptos = BigDecimal.ZERO;
		}						
		
		BigDecimal total_debitos = DebitoServiceUtil.sumaImportesItems(debitos);
		
		//Agregado para redondear importes a 2 decimales 2022-06-23
		 total_importe=total_importe.setScale(2, RoundingMode.HALF_UP );
		 total_debitos=total_debitos.setScale(2, RoundingMode.HALF_UP );
		 total_conceptos=total_conceptos.setScale(2, RoundingMode.HALF_UP );
		 total_prestaciones_no_estadisticas=total_prestaciones_no_estadisticas.setScale(2, RoundingMode.HALF_UP );
		//Fin Agregado redondeo
		
		
		if (tercerizado.equals("1")) {
			//validar importe total == debitos  + conceptos
			if (total_importe.compareTo(total_debitos.add(total_conceptos)) == 0) {
				generarConceptoPrestacionesMedicas(liquidacion, comprobante, action);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			//validar importe total = subtotal + sum debito
			if (total_importe.compareTo(total_debitos.add(total_conceptos).add(total_prestaciones_no_estadisticas)) == 0) {
				generarConceptoPrestacionesMedicas(liquidacion, comprobante, action);
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public static boolean validarCierreLiquidacionPaga(int id_liquidacion, ActionRequest action) throws Exception{				
		int id_concepto = ParamUtil.getInteger(action, "id_concepto_", 0);
		double importe_concepto = ParamUtil.getDouble(action,
				"importe_concepto_", 0d);		
		
		Liquidacion liquidacion = getLiquidacionEntry(id_liquidacion);
		List<ComprobanteItem> debitos = DebitoServiceUtil.buscaDebitos(id_liquidacion);
		Comprobante comprobante = ComprobanteServiceUtil.getComprobanteLiquidacionPorId(id_liquidacion);			
		BigDecimal total_importe = liquidacion.getImporte() != null ? liquidacion.getImporte() : BigDecimal.ZERO;
				
		HttpServletRequest httpServletRequest = PortalUtil
			.getHttpServletRequest(action);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(action);
		
		ArrayList<LiquidacionPrestacionAjuste> listaPrestacionAjuste =
			(ArrayList<LiquidacionPrestacionAjuste>) session.getAttribute("lista_ajustes_prestaciones");
		LiquidacionActionUtil.filtraListaPrestacionesDadosAjustes(liquidacion.getLiquidacionPrestacion(), listaPrestacionAjuste);						
		
		BigDecimal total_prestaciones_no_estadisticas = liquidacion.getImporteTotalNoEstadistico() != null ? liquidacion.getImporteTotalNoEstadistico() : BigDecimal.ZERO;					
		BigDecimal total_conceptos = new BigDecimal (importe_concepto);
		
		BigDecimal total_debitos = DebitoServiceUtil.sumaImportesItems(debitos);

		if (total_importe.compareTo(total_debitos.add(total_conceptos).add(total_prestaciones_no_estadisticas)) == 0) {
			guardarCambiosPrestaciones (listaPrestacionAjuste, comprobante, user);			
			//ACTUALIZA CONCEPTO POR PRESTACIONES MÉDICAS
			generarConceptoPrestacionesMedicas(liquidacion, comprobante, action);
			
			//actualiza conceptos asociados al comprobante que se está actualizando.
			List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
			ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
					new Concepto(id_concepto),
					comprobante.getImporteComprobante());
			comprobanteConcepto.setBorradoLogicamente(true);
			comprobanteConcepto.setAlta_fecha(new Date());
			conceptos.add(comprobanteConcepto);
			
			if (importe_concepto!=0) {
				ComprobanteConcepto comprobanteConceptoNuevo = new ComprobanteConcepto(
						new Concepto(id_concepto),
						new BigDecimal(importe_concepto));
				conceptos.add(comprobanteConceptoNuevo);
			}
			comprobante.setConceptos(conceptos);
			// actualiza el comprobante con el nuevo importe
			ComprobanteServiceUtil.update(comprobante, user, WebKeysGlobal.OSPIM);
			//String tercerizado_cab = ParamUtil.getString(action,
			//		"tercerizado_cab", "0");
			//String observaciones = ParamUtil.getString(action, "observaciones", "");
			session.removeAttribute("lista_ajustes_prestaciones");
			session.setAttribute("lista_ajustes_prestaciones", new ArrayList<LiquidacionPrestacionAjuste>());
			return true;
		} else {
			return false;
		}
	}
	
	public static void generarConceptoPrestacionesMedicas(Liquidacion liquidacion, Comprobante comprobante, ActionRequest actionRequest) throws SystemException, SQLException, PortalException {		
		//actualiza conceptos asociados al comprobante que se está actualizando.
		
		User user = PortalUtil.getUser(actionRequest);
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdPrestacionesMedicas(comprobante.getFechaRecepcion())),
				comprobante.getImporteComprobante());
		comprobanteConcepto.setBorradoLogicamente(true);
		comprobanteConcepto.setAlta_fecha(new Date());
		conceptos.add(comprobanteConcepto);
		
		BigDecimal total_prestaciones = liquidacion.getImporteTotalNoEstadistico() != null ? liquidacion.getImporteTotalNoEstadistico() : BigDecimal.ZERO;
		
		if (total_prestaciones.compareTo(BigDecimal.ZERO) == 1) {
			ComprobanteConcepto comprobanteConceptoNuevo = new ComprobanteConcepto(
					new Concepto(ConceptoServiceUtil.getIdPrestacionesMedicas(comprobante.getFechaRecepcion())),
					total_prestaciones);
			conceptos.add(comprobanteConceptoNuevo);
		}
		comprobante.setConceptos(conceptos);
		// actualiza el comprobante con el nuevo importe
		ComprobanteServiceUtil.update(comprobante, user, WebKeysGlobal.OSPIM);
	}
	
	public static void actualizaLiquidacionPrestacionEntry(int numero, int orden, Date prestacionFecha, String servicio, String cuil_titular, int inte, int id_prestacion,
			String cantidad, String importe, String tercerizado, User user, Date periodoPrestacion, int motivoAltaDiscapacidad,Integer idPrestador) throws NoSuchLiquidacionPrestacionEntryException, SystemException, 
			ComprobanteInexistenteException, NoSuchLiquidacionEntryException, ComprobanteExistenteException, PrestacionComprobanteExistenteException {
		getInstance().actualizaLiquidacionPrestacionEntry(numero, orden, prestacionFecha, servicio, cuil_titular, inte, id_prestacion,
				new BigDecimal(cantidad), new BigDecimal(importe), tercerizado, user.getScreenName(), periodoPrestacion, motivoAltaDiscapacidad);
		//Guarda el nuevo item de comprobante asociado a la prestación
		
		Comprobante comp = ComprobanteServiceUtil.getComprobanteLiquidacionPorId(numero);
		if (comp == null) {
			throw new ComprobanteInexistenteException();
		}
		
//		validaPrestacionLiquidacion(numero,id_prestacion,cuil_titular,inte, idPrestador,comp.getTipoComprobante(),
//				comp.getLetraComprobante(),comp.getNroComprobante(),periodoPrestacion,orden);	
		
		
		BigDecimal cant = new BigDecimal(cantidad);
		ComprobanteItem comprobanteItem = new ComprobanteItem(comp.getSucuComprobante(), comp.getTipoComprobante(), comp.getNroComprobante(), comp.getCuit(), comp.getLetraComprobante(), 
				comp.getSucuComprobante(), orden, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), cant.multiply(new BigDecimal(importe)), "", 0);
		ComprobanteItemServiceUtil.update(comprobanteItem, user);		
		getInstance().cambiarEstadoLiquidacionEntry(numero, WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO, user.getScreenName());
	}
	
	public static boolean servicioEspecial (String servicio) {
		if (servicio.equals("") || servicio.equalsIgnoreCase("AMBULATORIO"))
			return false;
		return true;
	}
	
	private static void validaPrestacionLiquidacion(int idLiquidacion,int idPrestacion, String cuilTitular,int inte,
			Integer idPrestador,String comproTipo,String comproLetra,String comproNro,Date periodo,Integer orden)
			throws SystemException, PrestacionComprobanteExistenteException {
		
		
			List<LiquidacionPrestacion>list=  getInstance().getComprobantesLiquidaciones(idPrestacion, comproTipo,comproLetra,
					                           comproNro, idPrestador, cuilTitular, inte, null);
			for(LiquidacionPrestacion l:list){
				if(l.getId_prestacion()==idPrestacion &&
						l.getLiquidacion().getId_prestador()==idPrestador &&
						l.getLiquidacion().getCompro_a_debitar_tipo().equalsIgnoreCase(comproTipo) &&
						l.getLiquidacion().getCompro_a_debitar_letra().equalsIgnoreCase(comproLetra) &&
						l.getLiquidacion().getCompro_a_debitar_numero().equalsIgnoreCase(comproNro) &&
						l.getFecha_prestacion().getTime()==periodo.getTime() &&
//						l.getPeriodo().getTime()==periodo.getTime() &&
						l.getCuil_titular().equalsIgnoreCase(cuilTitular) &&
						l.getInte()==inte 
						){
					
					if(l.getLiquidacion().getId_liquidacion()!=idLiquidacion) throw new PrestacionComprobanteExistenteException();
					if(orden==null || l.getOrden() !=orden) throw new PrestacionComprobanteExistenteException();
				}
			}
		
	}

	private static void enviarNotificacionComprobanteDuplicado(String comprobante, String usuario){
		
		List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.COMPROBANTE_DUPLICADO);
		
		EnviaEmailsThread.enviarMailDesatendido("Aviso comprobante duplicado (/)", "Comprobante: " + comprobante +" Usuario Carga: "+usuario, destinatarios, 1);
		
	}
	
//--- Generación Liquidación desde Comprobantes de Farmacia(Drogueria)	
	
	public static void actualizaLiquidacionEntry(int id_liquidacion,
			Date fecha, Date fechaE, Date fechaR, Date fechaV, Date periodo,
			String cuil_titular, int inte, int id_prestador, int id_domicilio, 
			int id_prestacion, Date prestacionFecha, String cantidad, String importe, 
			String compro_a_debitar_tipo, String letra_compro, int sucu, 
			String compro_a_debitar_numero, String tercerizado, User user, 
			String servicio, String solicitado, String debitado, String resultado, 			
			String importe_total, String debitado_total, String nroOC, String observaciones, 
			String tercerizado_cab, String cuit_prestador, int id_concepto, 
			String importe_concepto, RenderRequest actionRequest, Date periodoPrestacion,
			int motivoAltaDiscapacidad, int idReclamo , int idPrestacionReclamo , BigDecimal cargoOspim, 
			BigDecimal cargoPrestadora , BigDecimal cargoOmin, BigDecimal cargoEnSalud , BigDecimal cargoCemic,BigDecimal cargoImesa) throws Exception {
		
		List<Empresa> busqueda = EmpresaServiceUtil
		.getEmpleadores(cuit_prestador, null, String.valueOf(id_prestador),0);

		String sucu_prestador = busqueda.size() == 1 ? String.valueOf(id_prestador) : "000";
		
		Comprobante comprobante = new Comprobante(sucu, compro_a_debitar_tipo,
				compro_a_debitar_numero, cuit_prestador, fechaE, fechaR,
				new BigDecimal(importe_total), letra_compro, sucu, fechaV, new Empresa(cuit_prestador, sucu_prestador, ""), periodo );
		
		validaPrestacionLiquidacion(id_liquidacion,id_prestacion,cuil_titular,inte,id_prestador,compro_a_debitar_tipo,
				letra_compro,compro_a_debitar_numero,prestacionFecha,null);
		
		
		Comprobante comprobanteEnBase = ComprobanteServiceUtil
			.getComprobante(comprobante, WebKeysGlobal.OSPIM);
		int id_liq_comprobante = ComprobanteServiceUtil.getIdComprobanteLiquidacion(comprobante);
		
		//Valida si ya existe el comprobante
		if (comprobanteEnBase != null && id_liquidacion != id_liq_comprobante) {
			throw new ComprobanteExistenteException();
		}
		Comprobante compLiquidacion = ComprobanteServiceUtil.getComprobanteLiquidacionPorId(id_liquidacion);
		if (compLiquidacion == null) {
			throw new ComprobanteInexistenteException();
		}
		//Actualiza liquidacion
		getInstance().actualizaLiquidacionEntry(id_liquidacion, fecha, fechaE, fechaR, fechaV, periodo, 
				id_prestador, id_domicilio, compro_a_debitar_tipo, letra_compro, sucu, compro_a_debitar_numero, 
				new BigDecimal(importe_total), new BigDecimal(debitado_total), nroOC, observaciones, 
				tercerizado_cab, user.getScreenName(), cuit_prestador, cargoOspim, cargoPrestadora, cargoOmin, cargoEnSalud,cargoCemic,cargoImesa);
		
		getInstance().cambiarEstadoLiquidacionEntry(id_liquidacion, WebKeysLiquidaciones.LIQUIDACION_ESTADO_CARGADO, user.getScreenName());
		//Actualiza comprobante y comprobante de liquidacion
		if (comprobanteEnBase != null && comprobanteEnBase.equals(compLiquidacion)) {

			//actualiza conceptos asociados al comprobante que se está actualizando.
			List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
			ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
					new Concepto(id_concepto),
					comprobante.getImporteComprobante());
			comprobanteConcepto.setBorradoLogicamente(true);
			comprobanteConcepto.setAlta_fecha(new Date());
			conceptos.add(comprobanteConcepto);
			
			if (importe_concepto != null && (!importe_concepto.equalsIgnoreCase("") && !importe_concepto.equalsIgnoreCase("0"))) {
				ComprobanteConcepto comprobanteConceptoNuevo = new ComprobanteConcepto(
						new Concepto(id_concepto),
						new BigDecimal(importe_concepto));
				conceptos.add(comprobanteConceptoNuevo);
			}
			comprobante.setConceptos(conceptos);
			// actualiza el comprobante con el nuevo importe
			ComprobanteServiceUtil.update(comprobante, user, WebKeysGlobal.OSPIM);
		} else {
			
			//crea conceptos asociados a la nota débito que se está insertando
			if (importe_concepto != null && (!importe_concepto.equalsIgnoreCase("") && !importe_concepto.equalsIgnoreCase("0"))) {
				List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
				ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
					new Concepto(id_concepto),
					new BigDecimal(importe_concepto));
				conceptos.add(comprobanteConcepto);
				comprobante.setConceptos(conceptos);
			}
			
			DebitoServiceUtil.grabarComprobanteAsociarLiquidacion(
					id_liquidacion, comprobante, user);
		}
		
		if (tercerizado_cab.equals("0")) {
			//si fue modificado el detalle
			if (cuil_titular.length() > 0 && id_prestacion != 0 && (cantidad != null && Double.valueOf(cantidad) > 0) && importe.length() > 0) {
				
				int[] idPrestacion = new int[1];
				//Si es tercerizado carga la nueva prestación				
				int orden = getInstance().cargaLiquidacionPrestacionEntry(id_liquidacion, cuil_titular, inte, id_prestacion, prestacionFecha, new BigDecimal(cantidad),
					new BigDecimal(importe),servicio, new BigDecimal(solicitado), new BigDecimal(debitado), new BigDecimal(resultado), tercerizado, user.getScreenName(),
					periodoPrestacion, motivoAltaDiscapacidad,idPrestacion , cargoOspim,  cargoPrestadora.add(!cargoEnSalud.equals(BigDecimal.ZERO)?cargoEnSalud:BigDecimal.ZERO),cargoImesa);
				BigDecimal cant = new BigDecimal(cantidad);
				
				// graba los datos del reclamo prestacional asociado 
				if (idReclamo!=0 && idPrestacionReclamo!=0){
					getInstance().grabaDatosDelReclamoPrestacionaldelaLiquidacion(id_liquidacion, idPrestacion[0] ,  idReclamo , idPrestacionReclamo, user.getScreenName());
				}
				//Carga el nuevo comprobante de la prestación
				//Borro primero posibles daots de la sesión
				//poner o borrar de la sesion el dato del servicio especial para que sea más fácil en t´perminos de la edición
				HttpServletRequest httpServletRequest = PortalUtil
					.getHttpServletRequest(actionRequest);
				HttpSession session = (HttpSession) httpServletRequest.getSession();
				if (EditarLiquidacionServiceUtil.servicioEspecial(servicio)) {
					session.setAttribute("cuil_titular_servicio", cuil_titular);
					session.setAttribute("inte_titular_servicio", inte);
					session.setAttribute("servicio", servicio);
					session.setAttribute("fecha_prestacion_servicio", prestacionFecha);
				} else {
					session.removeAttribute("cuil_titular_servicio");
					session.removeAttribute("inte_servicio");
					session.removeAttribute("servicio");
					session.removeAttribute("fecha_prestacion_servicio");
				}				
				ComprobanteItem comprobanteItem = new ComprobanteItem(sucu, compro_a_debitar_tipo, compro_a_debitar_numero, cuit_prestador, letra_compro, 
						sucu, orden, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), cant.multiply(new BigDecimal(importe)), "", 0);			
				if (comprobanteEnBase == null || !comprobanteEnBase.equals(compLiquidacion)) {
					ComprobanteItemServiceUtil.actualizarItemsLiquidacion(id_liquidacion, comprobanteItem, user);
				}
				ComprobanteItemServiceUtil.save(comprobanteItem, user);
				
			}
		}
		//Borra el comprobante anterior y el comprobante por liquidación
		if (comprobanteEnBase == null || !comprobanteEnBase.equals(compLiquidacion)) {
			ComprobanteServiceUtil.deleteComprobanteLiquidacion(id_liquidacion, user);
		}
	}
	
	
	public static void actualizaCargosTotal(int id_liquidacion,BigDecimal cargoOspim, BigDecimal cargoPrestadora, 
			BigDecimal cargoImesa) throws NoSuchLiquidacionEntryException,
			SystemException {
		
		getInstance().actualizaCargosTotal(id_liquidacion,cargoOspim, cargoPrestadora,cargoImesa);
		
	}

//---- Fin generar liquidación
	
	public static List<DLFileEntryImpl> getImagenes(Liquidacion l){
		List<DLFileEntryImpl> list=null;
		String idFacturaImg="";
		Prestador p;
		try {
			p = PrestadorServiceUtil.getPrestador(l.getId_prestador());
			idFacturaImg = p.getCuit()+"-"+l.getCompro_a_debitar_tipo()+"-"+l.getCompro_a_debitar_letra()+
					String.format("%05d",l.getSucu() )+ l.getCompro_a_debitar_numero();
			list = ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"CPBTE");
		} catch (Exception e) {
			
		}
		return list;
	}
	
}
