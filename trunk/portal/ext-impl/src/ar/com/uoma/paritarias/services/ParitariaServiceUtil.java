package ar.com.uoma.paritarias.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.EscalaSueldosBasicos;
import ar.com.uoma.beans.Paritaria;

public class ParitariaServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(ParitariaServiceUtil.class);

	public static List<Paritaria> getListaParitaria(String nombreCamara, String  fechaDesdeMes, String fechaDesdeAnio) {
		Date periodoDate = null;
		
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	
		Date fechaParitaria= null;
		try {
			fechaParitaria = formatoDeFecha.parse("01" + "/"
					+ ((Integer.parseInt(fechaDesdeMes) ) + 1)  + "/" + fechaDesdeAnio);
		} catch (Exception e) {
			fechaParitaria = null;
		}
			
		if (fechaParitaria != null) {
			calendar.setTime(fechaParitaria);
			calendar.add(Calendar.MONTH, -1);
			fechaParitaria =  calendar.getTime();
			periodoDate =DateUtils.getLastDateOfMonth(fechaParitaria, false);
		}
		
		return ParitariasServiceImpl.getInstance().getParitarias(nombreCamara, periodoDate);
	}
	
	
	public static void  altaParitaria(Paritaria paritaria, boolean simular) {
		ParitariasServiceImpl.getInstance().agregarParitarias(paritaria, simular);
	}
	
	public static int  validarParitariaExistente(Paritaria paritaria) {
		return ParitariasServiceImpl.getInstance().validarParitariaExistente(paritaria);
	}
	
	
	public static List<EscalaSueldosBasicos>  buscarHonorParitarias(String camara, Date periodo , boolean simular) {
		return ParitariasServiceImpl.getInstance().getTraerParitarias(camara, periodo , simular);
	}

	public static List<EscalaSueldosBasicos>  buscarHonorParitariasJornales(String camara, Date periodo , boolean simular) {
		return ParitariasServiceImpl.getInstance().getTraerParitariasJornales(camara, periodo , simular);
	}
	
}
