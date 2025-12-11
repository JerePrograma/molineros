package ar.com.ospim.farmacia.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.model.User;
import com.sun.star.sdbc.SQLException;

import java.util.Date;

//import com.sun.star.bridge.oleautomation.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.beans.SituacionMedica;
import ar.com.ospim.autorizaciones.exceptions.ImposibleBorrarReclamoPrestacionalException;
import ar.com.ospim.autorizaciones.exceptions.ImposibleBorrarSituacionMedicaException;

import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmaciaOspim.beans.ItemMedicacionTotal;
import ar.com.ospim.farmaciaOspim.beans.MedicacionOspimExcel;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleBorrarMedicamentoOspimException;
import ar.com.ospim.farmaciaOspim.reportes.beans.BusquedaReporteMedicamentosFiltro;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;

/**
 * <a href="BusquedaMedicamentoServiceUtil.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.BusquedaMedicamentoServiceImpl</code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Federico Brachi
 * 
 * @see ar.com.ospim.afiliados.services.BusquedaMedicametoServiceImpl
 * 
 */
public class BusquedaMedicamentoServiceUtil {

	private static BusquedaMedicamentoServiceImpl instance = null;

	public static BusquedaMedicamentoServiceImpl getInstance() {
		if (null == instance) {
			instance = new BusquedaMedicamentoServiceImpl();
		}
		return instance;
	}

	public static List<Medicamento> getBusquedaMedicamentos(int troquel,
			int registro, String nombre, String presentacion,
			String laboratorio, String cod_barras) {
		List<Medicamento> medicamentos = getInstance().getBusquedaMedicamentos(
				troquel, registro, nombre, presentacion, laboratorio,
				cod_barras);
		return medicamentos;
	}
	
	public static List<Medicamento> getBusquedaMedicamentosOspim (int troquel,
			int registro, String nombre, String presentacion,
			String laboratorio, String cod_barras, Date periodoFecha ,String drogaMedicacion, boolean manualDat  ) {
		List<Medicamento> medicamentos = getInstance().getBusquedaMedicamentosOspim (
				troquel, registro, nombre, presentacion, laboratorio,
				cod_barras,periodoFecha , drogaMedicacion , manualDat );
		return medicamentos;
	}
	
	public static List<ItemMedicacionTotal> getBusquedaMedicamentosOspimTotal (int troquel,
			int registro, String nombre, String presentacion,
			String laboratorio, String cod_barras, Date periodoFecha ,String drogaMedicacion, boolean manualDat , int pagina ,boolean incluyeBajas ) {
		List<ItemMedicacionTotal> medicamentos = getInstance().getBusquedaMedicamentosOspimTotal (
				troquel, registro, nombre, presentacion, laboratorio,
				cod_barras,periodoFecha , drogaMedicacion , manualDat , pagina , incluyeBajas);
		return medicamentos;
	}
	

	public static List<MedicacionOspimExcel> getReporteMedicamentosOspimFiltro (BusquedaReporteMedicamentosFiltro filtro ) {
		List<MedicacionOspimExcel> medicamentos = getInstance().getReporteMedicamentosOspimFiltro(filtro );
		return medicamentos;
	}

	
	public static List<Medicamento> getBusquedaMedicamentos(int troquel,
			String nombre) {
		List<Medicamento> medicamentos = getInstance().getBusquedaMedicamentos(
				troquel, 0, nombre, "", "", "");
		return medicamentos;
	}

	public static List<Medicamento> getBusquedaMedicamentos(int troquel,
			int registro, String nombre, String presentacion,
			String laboratorio, int id_plan, String cod_barras,
			List<Plan> planes, BigDecimal precioNuevo) throws Exception {

		List<Medicamento> medicamentos = getInstance().getBusquedaMedicamentos(
				troquel, registro, nombre, presentacion, laboratorio,
				cod_barras);
		if (null != planes) {
			List<Medicamento> nuevaListaMeds = new ArrayList<Medicamento>();
			if (id_plan != 0) {
				for (Medicamento med : medicamentos) {
					if (precioNuevo != null) {
						med.setPrecio(precioNuevo);
					}
					calcularMontosMedicamentos(med, id_plan, planes);
					if (id_plan == 1 || id_plan == 2 || id_plan == 3
							|| id_plan == 19) {
						nuevaListaMeds.add(med);
					}
				}
			}
		}
		return medicamentos;
	}

	
	
	public static List<Medicamento> getBusquedaMedicamentosxRegistrooxTroquel(int registro ,int troquel) throws Exception {
		List<Medicamento> medicamentos = getInstance().getBusquedaMedicamentosxRegistrooxTroquel (registro ,troquel);		
		return medicamentos;
	}
	 
	
	
	private static void calcularMontosMedicamentos(Medicamento med,
			int id_plan, List<Plan> planes) {
		if (esCoberturaIntegral(id_plan, planes)) { // TIENE PLAN UOMA Y
			// AMTIMA.
			if (med.isPmo()
					&& null != med.getCober_sssalud()
					&& med.getCober_sssalud().doubleValue() == new BigDecimal(
							40).doubleValue()) {// SI ES PMO Y 40
				// SSSALUD,
				// AGREGO EL 40% de DTO.
				med.setMonto_cober_ospim(med.getPrecio()
						.multiply(new BigDecimal(40))
						.divide(new BigDecimal(100))
						.setScale(2, RoundingMode.DOWN)); // OSPIM CUBRE
				// EL 40 del
				// PVP

				med.setMonto_cober_amtima(med
						.getPrecio()
						.multiply(
								med.getCober_amtima().divide(
										new BigDecimal(100)))
						.setScale(2, RoundingMode.DOWN));
				// PORCENTAJE ES LA SUMA
				med.setTotal_cobertura(med.getCober_ospim().add(
						med.getCober_amtima()));
				// 80%
			} else if (med.isPmo()
					&& null != med.getCober_sssalud()
					&& med.getCober_sssalud().doubleValue() == new BigDecimal(
							70).doubleValue()) { // Si ES PMO y 70

				med.setMonto_cober_ospim(med.getPrecio()
						.multiply(new BigDecimal(70))
						.divide(new BigDecimal(100))
						.setScale(2, RoundingMode.DOWN)); // OSPIM CUBRE
				// EL 70 del
				// PVP

				// cambio que pidió ángeles 02022012
				med.setMonto_cober_amtima(med.getPrecio().subtract(
						med.getMonto_cober_ospim()));
				med.setTotal_cobertura(med.getCober_ospim().add(
						med.getCober_amtima()));
				// 100%
			} else if (!med.isPmo()
					&& med.getCober_amtima() != null
					&& (med.getCober_amtima().doubleValue() == new BigDecimal(
							40).doubleValue())) { // NO ES PMO, SOLO
				// VADEMECUM AMTIMA
				med.setMonto_cober_amtima(med
						.getPrecio()
						.multiply(
								med.getCober_amtima().divide(
										new BigDecimal(100)))
						.setScale(2, RoundingMode.DOWN));

				med.setCober_ospim(new BigDecimal(40)); // ángeles nos
				// pidió cambiar
				// el 0% por 40%
				// de cobertura
				// ospim
				// por esto
				med.setMonto_cober_ospim(med
						.getPrecio()
						.multiply(
								med.getCober_ospim()
										.divide(new BigDecimal(100)))
						.setScale(2, RoundingMode.DOWN));
				med.setTotal_cobertura(med.getCober_ospim().add(
						med.getCober_amtima()));
				// 80%
			}
		} else if (esCoberturaAmtima(id_plan, planes)) { // TIENE PLAN
			// INTEGRAL.
			if (med.isPmo()
					&& null != med.getCober_sssalud()
					&& med.getCober_sssalud().doubleValue() == new BigDecimal(
							40).doubleValue()) {// SI ES PMO Y 40
				// SSSALUD,
				// AGREGO EL 40% de DTO.
				med.setMonto_cober_ospim(med.getPrecio_ospim().setScale(2,
						RoundingMode.DOWN)); // OSPIM CUBRE
				// EL MONTO
				// FIJO
				med.setMonto_cober_amtima(med
						.getPrecio()
						.multiply(
								med.getCober_amtima().divide(
										new BigDecimal(100)))
						.setScale(2, RoundingMode.DOWN));
				// PORCENTAJE ES LA SUMA
				med.setTotal_cobertura(med.getCober_ospim().add(
						med.getCober_amtima()));
				// 80%
			} else if (med.isPmo()
					&& null != med.getCober_sssalud()
					&& med.getCober_sssalud().doubleValue() == new BigDecimal(
							70).doubleValue()) { // Si ES PMO y 70
				// SUPER,
				// SOLO EL MONTO
				// FIJO
				med.setMonto_cober_ospim(med.getPrecio_ospim().setScale(2,
						RoundingMode.DOWN)); // OSPIM CUBRE
				// EL MONTO
				// FIJO
				// cambio que pidió ángeles 02022012
				med.setMonto_cober_amtima(med.getPrecio().subtract(
						med.getMonto_cober_ospim()));
				med.setTotal_cobertura(med.getCober_ospim().add(
						med.getCober_amtima()));
				// 100%
			} else if (!med.isPmo()
					&& med.getCober_amtima() != null
					&& (med.getCober_amtima().doubleValue() == new BigDecimal(
							40).doubleValue())) { // NO ES PMO, SOLO
				// VADEMECUM AMTIMA
				med.setMonto_cober_amtima(med
						.getPrecio()
						.multiply(
								med.getCober_amtima().divide(
										new BigDecimal(100)))
						.setScale(2, RoundingMode.DOWN));

				med.setCober_ospim(new BigDecimal(40)); // ángeles nos
				// pidió cambiar
				// el 0% por 40%
				// de cobertura
				// ospim
				// por esto
				med.setMonto_cober_ospim(med
						.getPrecio()
						.multiply(
								med.getCober_ospim()
										.divide(new BigDecimal(100)))
						.setScale(2, RoundingMode.DOWN));
				med.setTotal_cobertura(med.getCober_ospim().add(
						med.getCober_amtima()));
				// 80%
			}
		} else if (esCobertura(id_plan, planes)) { // SOLO
			// OSPIM (COBERTURA)
			if (med.isPmo()) {// SI ES PMO SOLO MONTO FIJO
				med.setMonto_cober_ospim(med.getPrecio_ospim().setScale(2,
						RoundingMode.DOWN)); // OSPIM CUBRE
				// EL MONTO
				// FIJO
				med.setMonto_cober_amtima(BigDecimal.ZERO);
				med.setTotal_cobertura(med.getCober_sssalud());
				// 70 o 40 %
			} else {
				med.setMonto_cober_amtima(BigDecimal.ZERO); // SI NO ES
				// PMO,
				// NADA
				med.setMonto_cober_ospim(BigDecimal.ZERO);
				med.setTotal_cobertura(BigDecimal.ZERO);
				// 0%
			}
		} else if (esCoberturaUOMA(id_plan, planes)) { // SOLO
			// OSPIM (COBERTURA)
			if (med.isPmo()) {// SI ES PMO SOLO MONTO FIJO
				med.setMonto_cober_ospim(med.getPrecio()
						.multiply(med.getCober_sssalud())
						.divide(new BigDecimal(100))
						.setScale(2, RoundingMode.DOWN)); // OSPIM CUBRE
				// EL MONTO
				// FIJO
				med.setMonto_cober_amtima(BigDecimal.ZERO);
				med.setTotal_cobertura(med.getCober_sssalud());
				// 70 o 40 %
			} else {
				med.setMonto_cober_amtima(BigDecimal.ZERO); // SI NO ES
				// PMO,
				// NADA
				med.setMonto_cober_ospim(BigDecimal.ZERO);
				med.setTotal_cobertura(BigDecimal.ZERO);
				// 0%
			}
		}

	}

	private static boolean esCobertura(int id_plan, List<Plan> planes) {
		for (Plan p : planes) {
			if (p.isMolinero() && !p.isUoma() && p.isOspim() && !p.isAmtima()
					&& p.getId() == id_plan) {
				return true;
			}
		}
		return false;
	}

	private static boolean esCoberturaAmtima(int id_plan, List<Plan> planes) {
		for (Plan p : planes) {
			if (p.isMolinero() && !p.isUoma() && p.isOspim() && p.isAmtima()
					&& p.getId() == id_plan) {
				return true;
			}
		}
		return false;
	}

	private static boolean esCoberturaIntegral(int id_plan, List<Plan> planes) {

		for (Plan p : planes) {
			if (p.isMolinero() && p.isUoma() && p.isOspim() && p.isAmtima()
					&& p.getId() == id_plan) {
				return true;
			}
		}
		return false;
	}

	private static boolean esCoberturaUOMA(int id_plan, List<Plan> planes) {
		for (Plan p : planes) {
			if (p.isMolinero() && p.isUoma() && p.isOspim() && !p.isAmtima()
					&& p.getId() == id_plan) {
				return true;
			}
		}
		return false;
	}
	
	
	public static Medicamento getMedicamento(int idMedicamento ) throws Exception {		
		return getInstance().getMedicamento(idMedicamento ) ;
	}
	
	public static int insertar(Medicamento medicacion   , User user) throws Exception{
		int idMedicacion = 0;			
		idMedicacion = getInstance().insertar(medicacion , user);
		return idMedicacion ;
	}

	public static void actualizar(Medicamento medicacion , User user) throws Exception{
		getInstance().actualizar(medicacion , user );
		}
	
	
	public static void borrar(int id, User user)
			throws ImposibleBorrarMedicamentoOspimException,
			SQLException, java.sql.SQLException, ImposibleBorrarMedicamentoOspimException{
		    getInstance().borrar(id, user.getScreenName());
	}
	
}
