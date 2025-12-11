package ar.com.global.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ar.com.global.beans.DetalleEscalaSalarial;
import ar.com.global.beans.TablaEscalaSalarial;
import ar.com.global.beans.TablaEscalaSalarial.Camara;


public class CalculaCapitalCuotaServiceUtil {
	
	   public static int AMTIMA=1;
	   public static int SOCIAL=2;
	   public static int USUFRUCTO=3;
	   public static int ART_46=4;
	   public static int SOLIDARIO=5;
	   
	   
	
	// Calculo Capital
		public static BigDecimal calcularCapitalCuotaUsufructo(BigDecimal remun) {
			BigDecimal dosPorciento = new BigDecimal("0.02");
			return remun.multiply(dosPorciento).setScale(2,
					BigDecimal.ROUND_HALF_UP);
		}
		
		public static BigDecimal calcularCapitalAporteSocialUOMA(BigDecimal remun) {
			BigDecimal dosPorciento = new BigDecimal("0.02");
			return remun.multiply(dosPorciento).setScale(2,
					BigDecimal.ROUND_HALF_UP);
		}

		public static BigDecimal calcularCapitalCuotaSocialUOMA(BigDecimal remun) {
			BigDecimal dosPorciento = new BigDecimal("0.02");
			return remun.multiply(dosPorciento).setScale(2,
					BigDecimal.ROUND_HALF_UP);
		}

		public static BigDecimal calcularCapitalArticulo46(Camara camara, Date periodo,
				int antiguedad,
				Map<Camara, List<DetalleEscalaSalarial>> tablaEscalaSalarialJornales) {
			
			if (camara == null) {
				return null;
			}
			
			TablaEscalaSalarial escala = getEscalaCorrespondiente(camara, periodo,
					0, tablaEscalaSalarialJornales);
			return escala.getCatE().setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		public static BigDecimal calcularCapitalCuotaAMTIMA(boolean isAdherente,
				Camara camara, Date periodo, int antiguedad,
				Map<Camara, List<DetalleEscalaSalarial>> tablaEscalaSalarialSueldos) {

			if (camara == null) {
				return null;
			}
			
			if (isAdherente) {

				Calendar periodoC = Calendar.getInstance();
				periodoC.setTime(periodo);

				Calendar fechaInicial220 = Calendar.getInstance();
				fechaInicial220.setTime(periodo);
				fechaInicial220.set(Calendar.YEAR, 2012);
				fechaInicial220.set(Calendar.MONTH, Calendar.SEPTEMBER);

				Calendar fechaInicial260 = Calendar.getInstance();
				fechaInicial260.setTime(periodo);
				fechaInicial260.set(Calendar.YEAR, 2013);
				fechaInicial260.set(Calendar.MONTH, Calendar.JANUARY);
				
				Calendar fechaInicial300 = Calendar.getInstance();
				fechaInicial300.setTime(periodo);
				fechaInicial300.set(Calendar.YEAR, 2013);
				fechaInicial300.set(Calendar.MONTH, Calendar.JUNE);
				
				Calendar fechaInicial325 = Calendar.getInstance();
				fechaInicial325.setTime(periodo);
				fechaInicial325.set(Calendar.YEAR, 2014);
				fechaInicial325.set(Calendar.MONTH, Calendar.JANUARY);
				
				Calendar fechaInicial420 = Calendar.getInstance();
				fechaInicial420.setTime(periodo);
				fechaInicial420.set(Calendar.YEAR, 2015);
				fechaInicial420.set(Calendar.MONTH, Calendar.JANUARY);
			

				if (periodoC.before(fechaInicial220)) {
					return new BigDecimal(200);
				} else if (periodoC.compareTo(fechaInicial220) >= 0
						&& periodoC.compareTo(fechaInicial260) < 0) {
					return new BigDecimal(220);
				} else if(periodoC.compareTo(fechaInicial260) >=0 && periodoC.compareTo(fechaInicial300)<0){
					return new BigDecimal(260);
				} else if(periodoC.compareTo(fechaInicial300) >=0 && periodoC.compareTo(fechaInicial325)<0){
					return new BigDecimal(300);
				} else if(periodoC.compareTo(fechaInicial420) >=0 && periodoC.compareTo(fechaInicial420)<0){
					return new BigDecimal(325);
				} else{
					return new BigDecimal(420);
				}

			}

			TablaEscalaSalarial escala = getEscalaCorrespondiente(camara, periodo,
					0, tablaEscalaSalarialSueldos);
			return escala.getCatB().multiply(new BigDecimal(0.02D))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		private static TablaEscalaSalarial getEscalaCorrespondiente(Camara camara,
				Date periodo, int antiguedad,
				Map<Camara, List<DetalleEscalaSalarial>> tablaEscalaSalarialJornales) {
			
			if (camara == null) {
				return null;
			}
			
			List<DetalleEscalaSalarial> list = tablaEscalaSalarialJornales
					.get(camara);
			DetalleEscalaSalarial anterior = null;
			for (DetalleEscalaSalarial det : list) {
				if (anterior == null || det.getFechaDesde().before(periodo)
						|| det.getFechaDesde().equals(periodo)) {
					anterior = det;
				} else {
					break;
				}
			}

			TablaEscalaSalarial escala = null;
			TablaEscalaSalarial escalaMayor = null;
			for (TablaEscalaSalarial tabla : anterior.getEscalaSalarial()) {
				if (tabla.getAntiguedadDesde() == antiguedad) {
					escala = tabla;
					break;
				}
				if (escalaMayor == null
						|| escalaMayor.getAntiguedadDesde() < tabla
								.getAntiguedadDesde()) {
					escalaMayor = tabla;
				}
			}

			if (escala == null) {
				escala = escalaMayor;
			}
			return escala;
		}
				
}
