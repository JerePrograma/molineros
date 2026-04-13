package ar.com.ospim.farmacia.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem;
import ar.com.ospim.farmacia.beans.ReporteOrdenPagoReintegrosFarmacia;
import ar.com.ospim.liquidaciones.AfiliadoSinPlanException;
import ar.com.ospim.liquidaciones.DuplicateReintegroPrestacionIdException;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;

import com.liferay.portal.SystemException;

public class ReintegroFarmaciaServiceUtil {

	private static ReintegroFarmaciaServiceImpl instance = null;

	public static ReintegroFarmaciaServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReintegroFarmaciaServiceImpl();
		}
		return instance;
	}

	/**
	 * Obtiene el reintegro por su clave primaria
	 * 
	 * @param id_reintegro
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public static ReintegroMedicamento getReintegroEntry(int id_reintegro)
			throws SystemException, NoSuchReintegroEntryException {
		ReintegroMedicamento reintegro = getInstance().getReintegroEntry(
				id_reintegro);
		reintegro.setMedicamentos(getInstance().getMedicamentosReintegroEntry(
				id_reintegro));
		return reintegro;
	}

	public static List<ReintegroMedicamento> buscarReintegros(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, int pagos,
			String alta_usr, int id_medicamento, int receta) throws Exception {

		return getInstance().buscarReintegros(entidad, fechaDesde, fechaHasta,
				periodoDesde, periodoHasta, codPrestad, nroAfi, inte,
				cuil_titular, seccional, numero, pagos, alta_usr,
				id_medicamento, receta);
	}

	public static int cargaReintegroFarmaciaEntry(Date fecha, Date periodo,
			String cuil_titular, int inte, int seccional,
			ArrayList<ReintegroMedicamentoItem> medicamentos, String userName, 
			String cbu, String cuilCuenta,String emailCuenta , 
			String apellidoCuenta, String nombreCuenta)
			throws SystemException {
		int id_reintegro = getInstance().cargaReintegroFarmaciaEntry(fecha,
				periodo, cuil_titular, inte, seccional, medicamentos, userName
				,cbu,cuilCuenta,emailCuenta,apellidoCuenta,nombreCuenta);
		return id_reintegro;
	}

	public static int actualizaReintegroFarmaciaEntry(int id_reintegro,
			Date fecha, Date periodo, String cuil_titular, int inte,
			int seccional, ArrayList<ReintegroMedicamentoItem> medicamentos,
			String userName) throws SystemException,
			NoSuchReintegroEntryException,
			DuplicateReintegroPrestacionIdException, AfiliadoSinPlanException {
		getInstance().actualizaReintegroFarmaciaEntry(id_reintegro, fecha,
				periodo, cuil_titular, inte, seccional, medicamentos, userName);
		getInstance().actualizaMedicamentoReintegroPrestacionEntry(
				id_reintegro, medicamentos, userName);
		return id_reintegro;
	}

	/**
	 * borra un reintegro
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public static void borraReintegroEntry(int id_reintegro, String userName)
			throws NoSuchReintegroEntryException, SystemException {
		getInstance().borraReintegroEntry(id_reintegro, userName);
	}

	/**
	 * Obtene el reintegro por numero de receta, si el reintegro no existe lo
	 * retorna vacío
	 * 
	 */
	public static ReintegroMedicamento getReintegroPorNumeroReceta(
			int num_receta) throws SystemException,
			NoSuchReintegroEntryException {
		ReintegroMedicamento reintegro = new ReintegroMedicamento();
		List<ReintegroMedicamentoItem> medicamentoItems = getInstance()
				.getMedicamentosReintegroEntryPorNumeroReceta(num_receta);
		if (medicamentoItems.size() > 0) {
			reintegro = getInstance().getReintegroEntry(
					medicamentoItems.get(0).getId_reintegro());
			reintegro.setMedicamentos(medicamentoItems);
		} else {
			return null;
		}
		return reintegro;
	}

	/**
	 *Get reintegros por id de la lista
	 */
	public static List<ReporteOrdenPagoReintegrosFarmacia> getReintegros(
			int listaId) throws SystemException, NoSuchReintegroEntryException {
		ArrayList<ReporteOrdenPagoReintegrosFarmacia> list = new ArrayList<ReporteOrdenPagoReintegrosFarmacia>();

		getInstance().getReintegros(listaId, list);

		Collections.sort(list,
				new Comparator<ReporteOrdenPagoReintegrosFarmacia>() {
					public int compare(ReporteOrdenPagoReintegrosFarmacia o1,
							ReporteOrdenPagoReintegrosFarmacia o2) {
						if (o1.getReintegro().getId_reintegro() == o2
								.getReintegro().getId_reintegro()) {
							return 0;
						} else if (o1.getReintegro().getId_reintegro() < o2
								.getReintegro().getId_reintegro()) {
							return -1;
						} else {
							return 1;
						}
					}
				});
		return list;
	}
	
	
	

	public static String validarComprobantesDuplicados(String cuilTitular, int inte ,  ReintegroMedicamentoItem  medicamento) throws Exception {

		return getInstance().validarComprobantesDuplicados(cuilTitular , inte, medicamento);
	}
	
	
}
