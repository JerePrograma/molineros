package ar.com.ospim.rrhh.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.rrhh.beans.RegistroAcceso;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.util.DateUtils;

/**
 * Mascara del servicio que da acceso a los datos de la aplicación (BD).
 */
public class RegistroAccesoServiceUtil {

	private static ProcesaArchivoHorariosServiceImpl instance = null;

	public static ProcesaArchivoHorariosServiceImpl getInstance() {
		if (null == instance) {
			instance = new ProcesaArchivoHorariosServiceImpl();
		}
		return instance;
	}

	public static ArrayList<RegistroAcceso> buscarLecturasAcceso(
			Date fechaDesde, Date fechaHasta, String id_tarjeta_acceso)
			throws Exception {
		return (ArrayList<RegistroAcceso>) getInstance().buscarLecturasAcceso(
				fechaDesde, fechaHasta, id_tarjeta_acceso);
	}

	public static ArrayList<RegistroAcceso> buscarControlAccesoAgrupado(
			Date fechaDesde, Date fechaHasta) throws Exception {
		
		return (ArrayList<RegistroAcceso>) getInstance().buscarControlAcceso(fechaDesde, fechaHasta);
	}
	
	public static List<RegistroAcceso> buscarInformacionUsuario(
			Date fechaDesde, Date fechaHasta, String id_tarjeta_acceso,
			boolean verDetalle) throws Exception {
		ArrayList<RegistroAcceso> listaOriginal = (ArrayList<RegistroAcceso>) getInstance()
				.buscarInformacionUsuario(fechaDesde, fechaHasta,
						id_tarjeta_acceso);
		ArrayList<RegistroAcceso> listaFiltrada = filtrarListaPorEntradasSalidas(listaOriginal);
		List<Feriado> feriados = FeriadosServiceImpl.getInstance()
				.findAllFeriados();
		calcularHorasPermanenciaYLaboralesDia(listaFiltrada, feriados, verDetalle);
		calcularTotalesPeriodo(fechaDesde, fechaHasta, listaFiltrada, feriados);
		ArrayList<RegistroAcceso> listaConOcultos = null;
		if (!verDetalle) {
			listaConOcultos = filtrarPorPrimeraEntradasUltimaSalida(listaFiltrada);
			return listaConOcultos;
		}
		return listaFiltrada;

	}

	public static ArrayList<RegistroAcceso> buscarInformacionUsuarios(
			Date fechaDesde, Date fechaHasta, boolean verDetalle)
			throws Exception {

		ArrayList<RegistroAcceso> listaOriginal = (ArrayList<RegistroAcceso>) getInstance()
				.buscarInformacionUsuarios(fechaDesde, fechaHasta);

		// pasar listaOriginal a listas separadas por persona
		HashMap<String, ArrayList<RegistroAcceso>> listaDeListasPorPersona = new HashMap<String, ArrayList<RegistroAcceso>>();
		separarListasPorPersona(listaOriginal, listaDeListasPorPersona);

		ArrayList<ArrayList<RegistroAcceso>> listaDeListasPorPersonaFiltrada = new ArrayList<ArrayList<RegistroAcceso>>();

		/*
		 * for (ArrayList<RegistroAcceso> list : listaDeListasPorPersona) { if
		 * (list != null) {
		 * listaDeListasPorPersonaFiltrada.add(filtrarListaPorEntradasSalidas
		 * (list)); } }
		 */
		Iterator it = listaDeListasPorPersona.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			listaDeListasPorPersonaFiltrada
					.add(filtrarListaPorEntradasSalidas((ArrayList<RegistroAcceso>) e
							.getValue()));
		}

		List<Feriado> feriados = FeriadosServiceImpl.getInstance()
				.findAllFeriados();

		for (ArrayList<RegistroAcceso> list : listaDeListasPorPersonaFiltrada) {
			calcularHorasPermanenciaYLaboralesDia(list, feriados, verDetalle);
		}
		for (ArrayList<RegistroAcceso> list : listaDeListasPorPersonaFiltrada) {
			calcularTotalesPeriodo(fechaDesde, fechaHasta, list, feriados);
		}

		ArrayList<RegistroAcceso> listaDefinitiva = new ArrayList<RegistroAcceso>();

		for (ArrayList<RegistroAcceso> listExt : listaDeListasPorPersonaFiltrada) {
			for (RegistroAcceso rAcceso : listExt) {
				listaDefinitiva.add(rAcceso);
			}
		}

		ArrayList<RegistroAcceso> listaConOcultos = null;

		if (!verDetalle) {
			listaConOcultos = filtrarPorPrimeraEntradasUltimaSalida(listaDefinitiva);
			return listaConOcultos;
		}
		return listaDefinitiva;
	}

	public static ArrayList<RegistroAcceso> filtrarListaPorEntradasSalidas(
			ArrayList<RegistroAcceso> listaOriginal) {
		ArrayList<RegistroAcceso> listaFiltrada = new ArrayList<RegistroAcceso>();
		int p1 = 0;
		int p2 = 1;
		int tamanioLista = listaOriginal.size();
		RegistroAcceso r1 = null;
		RegistroAcceso r2 = null;
		while (p2 < tamanioLista) {
			r1 = listaOriginal.get(p1);
			r2 = listaOriginal.get(p2);
			// caso E/E
			if (r1.getTipo_registro().equalsIgnoreCase("E")
					&& r2.getTipo_registro().equalsIgnoreCase("E")) {
				p1 = p1 + 1;
				p2 = p2 + 1;
			} // caso S/S
			else if (r1.getTipo_registro().equalsIgnoreCase("S")
					&& r2.getTipo_registro().equalsIgnoreCase("S")) {
				p1 = p1 + 2;
				p2 = p2 + 2;
			} // caso S/E
			else if (r1.getTipo_registro().equalsIgnoreCase("S")
					&& r2.getTipo_registro().equalsIgnoreCase("E")) {
				p1 = p1 + 1;
				p2 = p2 + 1;
			} // caso correcto E/S
			else if (r1.getTipo_registro().equalsIgnoreCase("E")
					&& r2.getTipo_registro().equalsIgnoreCase("S")) {
//				if (DateUtils.isMismoDia(r1.getFecha_registro(), r2
//						.getFecha_registro())) {
					listaFiltrada.add(r1);
					listaFiltrada.add(r2);
//				}
				p1 = p1 + 2;
				p2 = p2 + 2;
			}
//			//para el Hotel Los Diques
//			else if (r1.getTipo_registro().equalsIgnoreCase("E")
//					&& r2.getTipo_registro().equalsIgnoreCase("S")) {
//					if (!DateUtils.isMismoDia(r1.getFecha_registro(), r2
//							.getFecha_registro())) {
//						listaFiltrada.add(r1);
//						listaFiltrada.add(r2);
//					}
//					p1 = p1 + 2;
//					p2 = p2 + 2;
//			}
//			//
		}
		return listaFiltrada;
	}

	public static ArrayList<RegistroAcceso> filtrarPorPrimeraEntradasUltimaSalida(
			ArrayList<RegistroAcceso> listaOriginal) {

		int p1 = 0;
		int p2 = 3;
		int tamanioLista = listaOriginal.size();
		RegistroAcceso r1 = null;
		RegistroAcceso r2 = null;

		while (p2 < tamanioLista) {

			r1 = listaOriginal.get(p1);
			r2 = listaOriginal.get(p2);

			if (!DateUtils.isMismoDia(r1.getFecha_registro(), r2
					.getFecha_registro())
					|| !(r1.getId_tarjeta_acceso() == r2.getId_tarjeta_acceso())) {
				p1 = p1 + 2;
				p2 = p1 + 3;
				continue;
			}

			// encontrar próximo p2
			while (p2 < tamanioLista
					&& DateUtils.isMismoDia(r1.getFecha_registro(), r2
							.getFecha_registro())
					&& r1.getId_tarjeta_acceso() == r2.getId_tarjeta_acceso()) {

				p2 = p2 + 2;
				if (p2 >= tamanioLista) {
					break;
				}
				r2 = listaOriginal.get(p2);

			}
			// borrar intermedios
			p1 = p1 + 1;
			while (p1 < p2 - 2) {
				r1 = listaOriginal.get(p1);
				r1.setOcultar(true);
				p1 = p1 + 1;
			}

			p1 = p2 - 1;
			p2 = p1 + 3;

		}
		return listaOriginal;
	}

	private static ArrayList<RegistroAcceso> calcularHorasPermanenciaYLaboralesDia(
			ArrayList<RegistroAcceso> listaFiltrada, List<Feriado> feriados, boolean verDetalle) {
		int tamanioLista = listaFiltrada.size();
		int p1 = 0;
		int p2 = 1;
		RegistroAcceso r1 = null;
		RegistroAcceso r2 = null;
		boolean esFeriadoOFinde = false;
		long permanenciaLectura = 0;
		long permanenciaDia = 0;

		Date fechaRef = null;

		while (p2 < tamanioLista) {

			r1 = listaFiltrada.get(p1);
			r2 = listaFiltrada.get(p2);

			permanenciaLectura = r2.getFecha_registro().getTime()
					- r1.getFecha_registro().getTime();

			// r1.setMilisegundosPermanenciaLectura(permanenciaLectura);
			// debería aparecer solo para r2 si es info detalleda
			
			r2.setMilisegundosPermanenciaLectura(verDetalle ?  permanenciaLectura : 0l); 

			if (fechaRef == null) {
				// el primer día
				fechaRef = r1.getFecha_registro();
				permanenciaDia = permanenciaLectura;
			} else {
				if (!DateUtils.isMismoDia(fechaRef, r1.getFecha_registro())) {

					fechaRef = r1.getFecha_registro();

					esFeriadoOFinde = DateUtils.esFeriadoOFinde(listaFiltrada
							.get(p1 - 1).getFecha_registro(), feriados);

					listaFiltrada.get(p1 - 1).setMilisegundosPermanenciaDia(
							permanenciaDia);

					listaFiltrada
							.get(p1 - 1)
							.setMilisegundosLaboralesDia(
									!esFeriadoOFinde ? (long) ((listaFiltrada
											.get(p1 - 1)).getTarjetaAcceso()
											.getHoras_jornada() * DateUtils.MILISEGUNDOS_HORA)
											: 0);

					listaFiltrada.get(p1 - 1).setDiferenciaMilisegundosDia(
							(listaFiltrada.get(p1 - 1))
									.getMilisegundosLaboralesDia()
									- (listaFiltrada.get(p1 - 1))
											.getMilisegundosPermanenciaDia());

					permanenciaDia = permanenciaLectura;
				} else {
					permanenciaDia = permanenciaDia + permanenciaLectura;
				}
			}

			p1 = p1 + 2;
			p2 = p2 + 2;
		}

		if (tamanioLista > 0) {

			esFeriadoOFinde = DateUtils.esFeriadoOFinde(listaFiltrada.get(
					p1 - 1).getFecha_registro(), feriados);

			listaFiltrada.get(p1 - 1).setMilisegundosPermanenciaDia(
					permanenciaDia);

			listaFiltrada
					.get(p1 - 1)
					.setMilisegundosLaboralesDia(
							!esFeriadoOFinde ? (long) ((listaFiltrada
									.get(p1 - 1)).getTarjetaAcceso()
									.getHoras_jornada() * DateUtils.MILISEGUNDOS_HORA)
									: 0);

			listaFiltrada.get(p1 - 1).setDiferenciaMilisegundosDia(
					(listaFiltrada.get(p1 - 1)).getMilisegundosLaboralesDia()
							- (listaFiltrada.get(p1 - 1))
									.getMilisegundosPermanenciaDia());

		}

		return listaFiltrada;
	}

	private static ArrayList<RegistroAcceso> calcularTotalesPeriodo(
			Date fechaDesde, Date fechaHasta,
			ArrayList<RegistroAcceso> listaFiltrada, List<Feriado> feriados) {

		long totalHorasLaboralesPeriodo = 0;
		long totalHorasPermanenciaPeriodo = 0;
		long horasLaboralesUsuario = 0;

		for (RegistroAcceso registroAcceso : listaFiltrada) {
			horasLaboralesUsuario = (long) (registroAcceso.getTarjetaAcceso()
					.getHoras_jornada() * DateUtils.MILISEGUNDOS_HORA);
			if (registroAcceso.getMilisegundosLaboralesDia() != 0) {
				totalHorasPermanenciaPeriodo = totalHorasPermanenciaPeriodo
						+ registroAcceso.getMilisegundosPermanenciaDia();
			}

		}
		while (fechaDesde.before(DateUtils.getMismoDia_23_59hs(fechaHasta))) {
			if (!DateUtils.esFeriadoOFinde(fechaDesde, feriados)) {
				totalHorasLaboralesPeriodo = totalHorasLaboralesPeriodo
						+ horasLaboralesUsuario;
			}
			fechaDesde = DateUtils.anyadeDias(fechaDesde, 1);
		}

		if (listaFiltrada.size() > 0) {
			listaFiltrada
					.get(listaFiltrada.size() - 1)
					.setMilisegundosLaboralesPeriodo(totalHorasLaboralesPeriodo);
			listaFiltrada.get(listaFiltrada.size() - 1)
					.setMilisegundosPermanenciaPeriodo(
							totalHorasPermanenciaPeriodo);
			listaFiltrada.get(listaFiltrada.size() - 1)
					.setDiferenciaMilisegundosPeriodo(
							totalHorasLaboralesPeriodo
									- totalHorasPermanenciaPeriodo);
		}
		return listaFiltrada;
	}

	private static void separarListasPorPersona(
			ArrayList<RegistroAcceso> listaOriginal,
			HashMap<String, ArrayList<RegistroAcceso>> listaSeparada) {
		long id_tarjeta_acceso = 0;
		for (RegistroAcceso registroAcceso : listaOriginal) {
			id_tarjeta_acceso = registroAcceso.getId_tarjeta_acceso();
			if (listaSeparada.get(String.valueOf(id_tarjeta_acceso)) == null) {
				ArrayList<RegistroAcceso> lista = new ArrayList<RegistroAcceso>();
				lista.add(registroAcceso);
				listaSeparada.put(String.valueOf(id_tarjeta_acceso), lista);
			} else {
				listaSeparada.get(String.valueOf(id_tarjeta_acceso)).add(
						registroAcceso);
			}
		}
	}
	
	public static TreeMap<String,List<TarjetaAcceso>> getAccesoPersonalPorSector() {
		
		return getInstance().getAccesoPersonalPorSector();
		
	}
}
