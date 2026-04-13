package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.ComprobanteExistenteException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.ComprobanteItem;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.services.ComprobanteItemServiceUtil;
import ar.com.ospim.global.services.ComprobanteServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="DebitoServiceUtil .java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.DebitoServiceUtil </code> bean. The
 * static methods of this class calls the same methods of the bean instance.
 * It's convenient to be able to just write one line to call a method on a bean
 * instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Carlos Rivas
 * 
 * @see ar.com.ospim.liquidaciones.services.DebitoServiceImpl
 * 
 */
public class DebitoServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(DebitoServiceUtil.class);
	private static DebitoServiceImpl instance = null;

	public static DebitoServiceImpl getInstance() {
		if (null == instance) {
			instance = new DebitoServiceImpl();
		}
		return instance;
	}

	public static List<ComprobanteItem> grabaDebitoRetornaLista(
			int id_liquidacion, int motivo_debito, String observaciones_debito,
			String importe_debito, String cuit_prestador, User user,String id_prestador)
			throws Exception {
		_log.debug("grabando");
		// obtener el comprobante debito de la liquidacion
		Comprobante compLiquidacion = ComprobanteServiceUtil
				.getComprobanteDebitoLiquidacionPorId(id_liquidacion);		

		// obtener el comprobante no debito de la liquidacion		
		Comprobante compLiquidacionNoDeb = ComprobanteServiceUtil
				.getComprobanteLiquidacionPorId(id_liquidacion);
		
		// si no existe, creo uno
		if (compLiquidacion == null) {
			compLiquidacion = new Comprobante(
					
					2,					
					WebKeysGlobal.COMPROBANTE_NOTA_DEBITO, 
					null,													
					WebKeysGlobal.CUIT_OSPIM,
					new Date(), 
					null, 
					new BigDecimal(importe_debito), 
					"", 
					2, 
					null, /*
					new Empresa(
									compLiquidacionNoDeb.getCuit(), "000", ""),
                    */
					new Empresa(
							compLiquidacionNoDeb.getCuit(), id_prestador!=null && !"".equals(id_prestador)?id_prestador:"000"  , ""),
					null);
			
			compLiquidacion.setFechaRecepcion(compLiquidacionNoDeb.getFechaRecepcion());

			//crea conceptos asociados a la nota débito que se está insertando
			List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
			ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
					new Concepto(ConceptoServiceUtil.getIdAjuste()),
					compLiquidacion.getImporteComprobante());
			conceptos.add(comprobanteConcepto);
			compLiquidacion.setConceptos(conceptos);

			grabarComprobanteAsociarLiquidacion(id_liquidacion,
					compLiquidacion, user);
		} else {

			//actualiza conceptos asociados a la nota débito que se está insertando.
			List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
			ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
					new Concepto(ConceptoServiceUtil.getIdAjuste()),
					compLiquidacion.getImporteComprobante());
			comprobanteConcepto.setBorradoLogicamente(true);
			comprobanteConcepto.setAlta_fecha(new Date());
			conceptos.add(comprobanteConcepto);
			ComprobanteConcepto comprobanteConceptoNuevo = new ComprobanteConcepto(
					new Concepto(ConceptoServiceUtil.getIdAjuste()),
					compLiquidacion.getImporteComprobante().add(
							new BigDecimal(importe_debito)));
			conceptos.add(comprobanteConceptoNuevo);
			compLiquidacion.setConceptos(conceptos);

			compLiquidacion.setImporteComprobante(compLiquidacion
					.getImporteComprobante()
					.add(new BigDecimal(importe_debito)));
			ComprobanteServiceUtil.update(compLiquidacion, user, WebKeysGlobal.OSPIM);

		}
		// al pasarle el item en 0 se inserta como siguiente item
		ComprobanteItem comprobanteItem = new ComprobanteItem(compLiquidacion
				.getSucuComprobante(), compLiquidacion.getTipoComprobante(),
				compLiquidacion.getNroComprobante(), compLiquidacion.getCuit(),
				compLiquidacion.getLetraComprobante(), compLiquidacion
						.getSucuComprobante(), 0, new BigDecimal(0),
				new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
				new BigDecimal(0), new BigDecimal(importe_debito),
				observaciones_debito, motivo_debito);
		ComprobanteItemServiceUtil.save(comprobanteItem, user);
		
		List<ComprobanteItem> debitos = getInstance().buscaDebitos(id_liquidacion);
		
		//graba concepto ajuste en liquidacion
		List<ComprobanteConcepto> conceptosLiquidacion = new ArrayList<ComprobanteConcepto>();
		
		ComprobanteConcepto comprobanteConceptoLiquidacion = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),
				compLiquidacionNoDeb.getImporteComprobante());
		comprobanteConceptoLiquidacion.setBorradoLogicamente(true);
		comprobanteConceptoLiquidacion.setAlta_fecha(new Date());
		conceptosLiquidacion.add(comprobanteConceptoLiquidacion);
		
		ComprobanteConcepto comprobanteConceptoLiquidacionNuevo = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),sumaImportesItems(debitos));
		
		conceptosLiquidacion.add(comprobanteConceptoLiquidacionNuevo);
		compLiquidacionNoDeb.setConceptos(conceptosLiquidacion);

		compLiquidacion.setImporteComprobante(compLiquidacionNoDeb
				.getImporteComprobante()
				.add(new BigDecimal(importe_debito)));
		ComprobanteServiceUtil.update(compLiquidacionNoDeb, user, WebKeysGlobal.OSPIM);
		
		return debitos;
	}

	public static void grabarComprobanteAsociarLiquidacion(int idLiquidacion,
			Comprobante compLiquidacion, User user) throws Exception {
		try {
			ComprobanteServiceUtil.save(compLiquidacion, user, WebKeysGlobal.OSPIM);
		} catch (ComprobanteExistenteException e) {
			// no me importa
		}
		ComprobanteServiceUtil.saveAsociacionLiquidacion(idLiquidacion,
				compLiquidacion, user.getScreenName());
	}

	public static List<ComprobanteItem> buscaDebitos(int id_liquidacion)
			throws Exception {
		return getInstance().buscaDebitos(id_liquidacion);
	}

	public static List<ComprobanteItem> editaDebitoRetornaLista(
			int id_liquidacion, int item, int motivo_debito,
			String observaciones_debito, String importe_debito,
			String cuit_prestador, User user) throws Exception {
		// obtener el comprobante debito de la liquidacion
		Comprobante compLiquidacion = ComprobanteServiceUtil
				.getComprobanteDebitoLiquidacionPorId(id_liquidacion);

		// obtener el comprobante no debito de la liquidacion
		Comprobante compLiquidacionNoDeb = ComprobanteServiceUtil
				.getComprobanteLiquidacionPorId(id_liquidacion);
		// actualiza						
		ComprobanteItem comprobanteItem = new ComprobanteItem(compLiquidacion
				.getSucuComprobante(), compLiquidacion.getTipoComprobante(),
				compLiquidacion.getNroComprobante(), compLiquidacion.getCuit(),
				compLiquidacion.getLetraComprobante(), compLiquidacion
						.getSucuComprobante(), item, new BigDecimal(0),
				new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
				new BigDecimal(0), new BigDecimal(importe_debito),
				observaciones_debito, motivo_debito);
		ComprobanteItemServiceUtil.update(comprobanteItem, user);
		// calcula el nuevo importe del débito
		List<ComprobanteItem> debitos = getInstance().buscaDebitos(
				id_liquidacion);
		compLiquidacion.setImporteComprobante(sumaImportesItems(debitos));
						
		//actualiza conceptos asociados a la nota débito que se está actualizando.
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),
				compLiquidacion.getImporteComprobante());
		comprobanteConcepto.setBorradoLogicamente(true);
		comprobanteConcepto.setAlta_fecha(new Date());
		conceptos.add(comprobanteConcepto);
		ComprobanteConcepto comprobanteConceptoNuevo = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),
				compLiquidacion.getImporteComprobante());
		conceptos.add(comprobanteConceptoNuevo);
		compLiquidacion.setConceptos(conceptos);
		// actualiza el comprobante con el nuevo importe		
		
		compLiquidacion.setFechaRecepcion(compLiquidacionNoDeb.getFechaRecepcion());
		ComprobanteServiceUtil.update(compLiquidacion, user, WebKeysGlobal.OSPIM);
				
		//graba concepto ajuste en liquidacion
		List<ComprobanteConcepto> conceptosLiquidacion = new ArrayList<ComprobanteConcepto>();
		
		ComprobanteConcepto comprobanteConceptoLiquidacion = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),
				compLiquidacionNoDeb.getImporteComprobante());
		comprobanteConceptoLiquidacion.setBorradoLogicamente(true);
		comprobanteConceptoLiquidacion.setAlta_fecha(new Date());
		conceptosLiquidacion.add(comprobanteConceptoLiquidacion);
		
		ComprobanteConcepto comprobanteConceptoLiquidacionNuevo = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),sumaImportesItems(debitos));
		
		conceptosLiquidacion.add(comprobanteConceptoLiquidacionNuevo);
		compLiquidacionNoDeb.setConceptos(conceptosLiquidacion);

		compLiquidacion.setImporteComprobante(compLiquidacionNoDeb
				.getImporteComprobante()
				.add(new BigDecimal(importe_debito)));
		ComprobanteServiceUtil.update(compLiquidacionNoDeb, user, WebKeysGlobal.OSPIM);
		
		return debitos;
	}

	public static List<ComprobanteItem> borraDebitoRetornaLista(
			int id_liquidacion, int item, User user) throws Exception {
		// obtener el comprobante debito de la liquidacion
		Comprobante compLiquidacion = ComprobanteServiceUtil
				.getComprobanteDebitoLiquidacionPorId(id_liquidacion);
		
		// obtener el comprobante no debito de la liquidacion		
		Comprobante compLiquidacionNoDeb = ComprobanteServiceUtil
				.getComprobanteLiquidacionPorId(id_liquidacion);

		// instancia el item a borrar
		ComprobanteItem comprobanteItem = new ComprobanteItem(compLiquidacion
				.getSucuComprobante(), compLiquidacion.getTipoComprobante(),
				compLiquidacion.getNroComprobante(), compLiquidacion
						.getLetraComprobante(), compLiquidacion
						.getSucuComprobante(), compLiquidacion.getCuit(), item);
		// lo borra
		ComprobanteItemServiceUtil.delete(comprobanteItem, user);

		// calcula el nuevo importe del débito
		List<ComprobanteItem> debitos = getInstance().buscaDebitos(
				id_liquidacion);
		compLiquidacion.setImporteComprobante(sumaImportesItems(debitos));
		// actualiza el comprobante con el nuevo importe
						
		//actualiza conceptos asociados a la nota débito que se está actualizando.
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),
				compLiquidacion.getImporteComprobante());
		comprobanteConcepto.setBorradoLogicamente(true);
		comprobanteConcepto.setAlta_fecha(new Date());
		conceptos.add(comprobanteConcepto);
		ComprobanteConcepto comprobanteConceptoNuevo = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),
				compLiquidacion.getImporteComprobante());
		conceptos.add(comprobanteConceptoNuevo);
		compLiquidacion.setConceptos(conceptos);
		// actualiza el comprobante con el nuevo importe		

		ComprobanteServiceUtil.update(compLiquidacion, user, WebKeysGlobal.OSPIM);		
		
		//graba concepto ajuste en liquidacion
		List<ComprobanteConcepto> conceptosLiquidacion = new ArrayList<ComprobanteConcepto>();
		
		ComprobanteConcepto comprobanteConceptoLiquidacion = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),
				compLiquidacion.getImporteComprobante());
		comprobanteConceptoLiquidacion.setBorradoLogicamente(true);
		comprobanteConceptoLiquidacion.setAlta_fecha(new Date());
		conceptosLiquidacion.add(comprobanteConceptoLiquidacion);
		ComprobanteConcepto comprobanteConceptoLiquidacionNuevo = new ComprobanteConcepto(
				new Concepto(ConceptoServiceUtil.getIdAjuste()),
				compLiquidacion.getImporteComprobante());
		conceptosLiquidacion.add(comprobanteConceptoLiquidacionNuevo);
		compLiquidacionNoDeb.setConceptos(conceptos);
		
		ComprobanteServiceUtil.update(compLiquidacionNoDeb, user, WebKeysGlobal.OSPIM);
		
		return debitos;
	}

	public static BigDecimal sumaImportesItems(List<ComprobanteItem> debitos) {
		BigDecimal result = new BigDecimal(0);
		if (debitos == null) {
			return result;
		}
		for (ComprobanteItem debito : debitos) {
			result = result.add(debito.getSaldo());
		}
		return result;
	}
}