package ar.com.ospim.login.action;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.login.coordenadas.services.CoordenadasServiceUtil;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.ibm.icu.util.Calendar;


public class GeneradorCoordenadas {

	public static void main(String[] args) throws Exception {		
		Connection connection = getConnectionDevmolineros();
		connection.setAutoCommit(false);
		int cont=0;

		String query = "insert into ingreso_externo.tarjetacoordenadas(coordenadas ,  user_id, alta_usr, alta_fecha, modi_usr, modi_fecha, ip_sin_coordenadas)"
				+ " values (? ,?,'admin', current_timestamp,'admin', current_timestamp, '12.1.1.')";

		for (Long userId : getUserIds()) {
			System.out.println(userId.longValue());
			PreparedStatement prepareStatement = connection
					.prepareStatement(query);
			String coordenadasFinal = generarCoordenadas();
			prepareStatement.setString(1, coordenadasFinal);
			prepareStatement.setLong(2, userId.longValue());
			prepareStatement.execute();
			cont++;
		}
		
		connection.commit();
		connection.close();
		System.out.println("CANTIDAD GENERADA= "+cont);
		//insertarFechaVto();

	}

	public static String generarCoordenadas() {
		return CoordenadasServiceUtil.generarCoordenadas();
	}

	private static Connection getConnectionLportal() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://12.1.1.28:5432/lportal", "postgres",
					"barracud4");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static Connection getConnectionLportalQA() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://12.1.1.9:5432/lportal", "postgres",
					"postgres");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static Connection getConnectionDevmolineros() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://12.1.1.28:5432/devmolineros",
					"postgres", "barracud4");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static Connection getConnectionDevmolinerosQA() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://12.1.1.9:5432/devmolineros",
					"postgres", "postgres");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private static List<Long> getUserIds() {
		Connection connection = null;
		PreparedStatement stmt = null;
		List<Long> lista = new ArrayList<Long>();
		try {			

			String sql = "select userid from user_ where userid in (2222)";

			connection = getConnectionLportal();
			stmt = connection.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Long userid = rs.getLong(1);
				lista.add(userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionHelper.cerrar(stmt, connection);
		}
		return lista;
	}
	
	public static void insertarFechaVto() throws Exception{
		
		Calendar fechaInicioCalendar=Calendar.getInstance();
		fechaInicioCalendar.set(Calendar.YEAR, 2005);
		fechaInicioCalendar.set(Calendar.MONTH, 1);
		fechaInicioCalendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar fechaFinCalendar=Calendar.getInstance();
		fechaFinCalendar.set(Calendar.YEAR, 2013);
		fechaFinCalendar.set(Calendar.MONTH, 11);
		fechaFinCalendar.set(Calendar.DAY_OF_MONTH, 1);
		
		
		Date periodoDesde=fechaInicioCalendar.getTime();
		Date periodoHasta=fechaFinCalendar.getTime();
						
		Connection connection = getConnectionDevmolineros();
		connection.setAutoCommit(false);

		String query = "update reporte_estudio_borrar set fecha_vto=? where periodo=?";
		
		while (periodoDesde.before(periodoHasta)){
			System.out.println("PERIODO DESDE: "+periodoDesde);
			PreparedStatement prepareStatement = connection.prepareStatement(query);
			prepareStatement.setDate(1, new java.sql.Date(getVencimientoOriginalAFIP("30685850601", periodoDesde).getTime()));
			prepareStatement.setDate(2, new java.sql.Date(periodoDesde.getTime()));
			prepareStatement.execute();			
			fechaInicioCalendar.add(Calendar.MONTH, 1);
			periodoDesde=fechaInicioCalendar.getTime();
		}

		
		connection.commit();
		connection.close();
		
		
	}
	
	private static Calendar obtenerFechaPeriodoMas1(Date periodo, int dia) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(periodo);
		fecha.add(Calendar.MONTH, 1);
		fecha.set(Calendar.DATE, dia);
		return fecha;
	}

	private static int obtenerCantidadDiasEntreFechaYPrimerDiaHabil(
			Calendar fecha) {
		// Hago esto para poder testear con un feriadosServiceMock en
		// AfipServiceUtilTestCalculoVencimiento
	
		Calendar siguienteDia = obtenerSiguienteDiaHabil(
				fecha);
		int obtenerCantidadDiasCalendario = obtenerCantidadDiasCalendario(
				fecha.getTime(), siguienteDia.getTime());
		return obtenerCantidadDiasCalendario;
	}
	
	private static int obtenerCantidadDiasCalendario(Date ini, Date fin) {
		return (int) Math.ceil((fin.getTime() - ini.getTime()) / (1000 * 60 * 60 * 24D));
	}

	public static Calendar obtenerSiguienteDiaHabil(Calendar dia) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(dia.getTime());
		aux.set(Calendar.MILLISECOND, 0);
		aux.set(Calendar.SECOND, 0);
		aux.set(Calendar.MINUTE, 0);
		aux.set(Calendar.HOUR, 0);
		List<Feriado> feriados = findAllFeriados();
		while (esFeriadoOFinde(aux, feriados)) {
			aux.add(Calendar.DATE, 1);
		}
		return aux;
	}
	
	protected static boolean esFeriadoOFinde(Calendar dia, List<Feriado> feriados) {
		if (dia.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			return true;
		} else if (dia.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return true;
		}

		for (Feriado f : feriados) {
			Calendar aux = Calendar.getInstance();
			aux.setTime(f.getFecha());
			if (aux.get(Calendar.DATE) == dia.get(Calendar.DATE)
					&& aux.get(Calendar.MONTH) == dia.get(Calendar.MONTH)
					&& aux.get(Calendar.YEAR) == dia.get(Calendar.YEAR)) {
				return true;
			}
		}
		return false;
	}
	
	public static Date getVencimientoOriginalAFIP(String cuit, Date periodo) {
		// Calculo los dias de vencimiento, afip dicta que siempre son a partir
		// del 7, por lo tanto a partir del 7 me fijo si cae en finde o feriado
		// y
		// voy corriendo las fechas de acuerdo a esto
		int dia7 = 7;
		Calendar fechaDia7 = obtenerFechaPeriodoMas1(periodo, dia7);
		int sumarDiasAl7 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia7);
		dia7 += sumarDiasAl7;

		int dia8 = dia7 + 1;
		Calendar fechaDia8 = obtenerFechaPeriodoMas1(periodo, dia8);
		int sumarDiasAl8 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia8);
		dia8 += sumarDiasAl8;

		int dia9 = dia8 + 1;
		Calendar fechaDia9 = obtenerFechaPeriodoMas1(periodo, dia9);
		int sumarDiasAl9 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia9);
		dia9 += sumarDiasAl9;

		int dia10 = dia9 + 1;
		Calendar fechaDia10 = obtenerFechaPeriodoMas1(periodo, dia10);
		int sumarDiasAl10 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia10);
		dia10 += sumarDiasAl10;

		int dia11 = dia10 + 1;
		Calendar fechaDia11 = obtenerFechaPeriodoMas1(periodo, dia11);
		int sumarDiasAl11 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia11);
		dia11 += sumarDiasAl11;

		// Ahora segun el digito verificador del cuil/cuit obtengo el
		// vencimiento real
		int dig = Integer.parseInt(cuit.substring(cuit.length() - 1));
		int diaSegunDigito = 0;
		if (dig == 0 || dig == 1) {
			diaSegunDigito = dia7;
		} else if (dig == 2 || dig == 3) {
			diaSegunDigito = dia8;
		} else if (dig == 4 || dig == 5) {
			diaSegunDigito = dia9;
		} else if (dig == 6 || dig == 7) {
			diaSegunDigito = dia10;
		} else {
			diaSegunDigito = dia11;
		}

		return obtenerFechaPeriodoMas1(periodo, diaSegunDigito).getTime();
	}
	
	public static List<Feriado> findAllFeriados() {
		Connection con = getConnectionDevmolineros();
		CallableStatement stmt = null;
		List<Feriado> repo = null;
		try {
			String sql = "{call buscar_feriados()}";
			//con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<Feriado>();
			while (rs.next()) {
				repo.add(new Feriado(rs.getDate("feriado")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return repo;
	}

}
