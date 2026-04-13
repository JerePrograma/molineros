import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa;
import ar.com.ospim.afip.service.AfipServiceImpl;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.InteresAfip;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.SystemException;

public class AgregarActas {
	public static void main(String... aArgs) throws IOException, SQLException,
			ParseException, SystemException {
		List<Acta> actas = obtenerActasCompletas();
		for (Acta acta : actas) {
			if (acta.getNumero().equals("4300")) {
				// System.out.println("asd");
			}
			List<ActaPeriodoDeudaEmpresa> perisOriginales = acta.getPeriodos();
			List<ActaPeriodoDeudaEmpresa> peris = null;

			// if (peris == null) {
			peris = new ArrayList<ActaPeriodoDeudaEmpresa>();
			// }

			List<ReporteDeudaNominaEmpresa> list = AfipServiceUtil
					.getDeudaNominaEmpresa(acta.getEmpresa().getCuit(),
							acta.getPeriodoInicial(), acta.getPeriodoFinal());
			for (ReporteDeudaNominaEmpresa deuda : list) {
				ActaPeriodoDeudaEmpresa actaPeri = new ActaPeriodoDeudaEmpresa(
						deuda);
				if (!peris.contains(actaPeri)) {
					peris.add(actaPeri);
				}
			}
			acta.setPeriodos(peris);

			List<InteresAfip> intereses = AfipServiceImpl.getInstance()
					.getIntereses();
			Iterator<ActaPeriodoDeudaEmpresa> it = acta.getPeriodos()
					.iterator();
			while (it.hasNext()) {
				ActaPeriodoDeudaEmpresa peri = it.next();
				if (!listaContiene(perisOriginales, peri.getPeriodo())) {
					it.remove();
				} else {
					Date vencimientoOriginal = AfipServiceUtil
							.getVencimientoOriginalAFIP(acta.getEmpresa()
									.getCuit(), peri.getPeriodo());
					peri.calcularSaldoConInteres(vencimientoOriginal,
							intereses, acta.getFechaPago());
				}
			}

			setearCapitalEInteres(acta);
			// acta.setPagos(pagos)
			System.out.println(acta.getNumero() + " "
					+ acta.getCapital().toString() + " "
					+ acta.getInteres().toString());
		}
	}

	private static void setearCapitalEInteres(Acta acta) {
		BigDecimal subtotal = new BigDecimal(0);
		BigDecimal interes = new BigDecimal(0);
		for (ActaPeriodoDeudaEmpresa p : acta.getPeriodos()) {
			for (ActaPeriodoDeudaEmpresa.Detalle det : p.getDetalle()) {
				subtotal = subtotal.add(det.getCapital());
				interes = interes.add(det.getInteres());
			}
		}
		acta.setCapital(subtotal);
		acta.setInteres(interes);
	}

	@SuppressWarnings("deprecation")
	private static boolean listaContiene(
			List<ActaPeriodoDeudaEmpresa> perisOriginales, Date periodo) {
		for (ActaPeriodoDeudaEmpresa p : perisOriginales) {
			if (p.getPeriodo().getMonth() == periodo.getMonth()
					&& p.getPeriodo().getYear() == periodo.getYear()) {
				return true;
			}
		}
		return false;
	}

	private static List<Acta> obtenerActasCompletas() throws SQLException,
			ParseException {
		List<Acta> actas = getActasFromDB();
		for (Acta acta : actas) {
			Collections.sort(acta.getPeriodos(),
					new Comparator<ActaPeriodoDeudaEmpresa>() {
						public int compare(ActaPeriodoDeudaEmpresa o1,
								ActaPeriodoDeudaEmpresa o2) {
							return o1.getPeriodo().compareTo(o2.getPeriodo());
						}
					});
			Date periodoIni = acta.getPeriodos().get(0).getPeriodo();
			acta.setPeriodoInicial(periodoIni);
			Date periodoFin = acta.getPeriodos()
					.get(acta.getPeriodos().size() - 1).getPeriodo();
			acta.setPeriodoFinal(periodoFin);
			Calendar ini = Calendar.getInstance();
			ini.setTime(periodoIni);
			Calendar fin = Calendar.getInstance();
			fin.setTime(periodoFin);
		}
		return actas;
	}

	private static List<Acta> getActasFromDB() throws SQLException,
			ParseException {
		List<Acta> actas = new ArrayList<Acta>();
		Connection con = getConnection();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		PreparedStatement stmt = con
				.prepareStatement("select  numero, cuit, empresa,cuil, periodo, remune, omint,  afiliado, nuevo, fecha_obligacion, fecha_liq, periodo_d, omint_d  from liquidacion_actas where acta_convenio = 'A'"
						+ "and numero not in (select numero from liquidacion_actas where nuevo = false)"
						+ "and cast (numero as integer) not in (select cast (numero as integer) from acta where cierre_fecha is not null)");
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			String numero = rs.getString("numero");
			List<ActaPeriodoDeudaEmpresa> peris = null;
			Acta acta = obtenerActa(actas, numero);
			if (acta == null) {
				acta = new Acta();
				actas.add(acta);
			}
			peris = acta.getPeriodos();
			if (peris == null) {
				peris = new ArrayList<ActaPeriodoDeudaEmpresa>();
				acta.setPeriodos(peris);
			}
			String cuit = rs.getString("cuit");
			String empresa = rs.getString("empresa");
			String cuil = rs.getString("cuil");
			String remune = rs.getString("remune");
			String afiliado = rs.getString("afiliado");
			String fechaOblig = rs.getString("fecha_obligacion");
			Date peri = rs.getDate("periodo_d");
			// boolean nuevo = rs.getBoolean("nuevo");
			// Date fechaLiq = rs.getDate("fecha_liq");
			// BigDecimal omint = rs.getBigDecimal("omint_d");

			acta.setNumero(numero);
			acta.setEmpresa(new Empresa(cuit, "0", empresa));
			acta.setFechaPago(formatter.parse(fechaOblig));
			acta.setFechaInicio(new Date());

			ActaPeriodoDeudaEmpresa ap = new ActaPeriodoDeudaEmpresa();
			ap.setCuil(cuil);
			ap.setPeriodo(peri);
			try {
				ap.setRemuneracionDeclarada(new BigDecimal(remune.trim()
						.replaceAll(",", ".")));
			} catch (Exception e) {
				System.out.println(e);
			}
			ap.setApellido(afiliado);
			peris.add(ap);
		}
		return actas;
	}

	private static Acta obtenerActa(List<Acta> actas, String numero) {
		for (Acta acta : actas) {
			if (acta.getNumero().equals(numero)) {
				return acta;
			}
		}
		return null;
	}

	private static Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://10.1.1.28:5432/devmolineros",
					"postgres", "barracud4");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
