package ar.com.ospim.afiliados.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.global.beans.FechaPresentacionSSS;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.util.DateUtils;


public class FechaOpcionSSSUtil {

	private static Log _log = LogFactoryUtil.getLog(FechaOpcionSSSUtil.class);
	
	private static List<Feriado> feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
	
	private static FechaOpcionSSSServiceImpl instance = null;

	public static FechaOpcionSSSServiceImpl getInstance() {
		if (null == instance) {
			instance = new FechaOpcionSSSServiceImpl();
		}
		return instance;
	}

	public List<FechaPresentacionSSS> traerFechasPresentacionSSS() throws Exception {
		return getInstance().traerFechasPresentacionSSS();
	}
	
	
	public void insetarProximaFechaOpcionSSS(Date fechaPress, String usr) throws Exception {
		 getInstance().insetarProximaFechaOpcionSSS(fechaPress, usr);
	}
	
	private static Date  obtenerUltimaFechaOpcionCargada() throws Exception {
		 return getInstance().obtenerUltimaFechaOpcionCargada();
	}
	
	public  Date obtenerProximaFechaOpcionPresentar() throws Exception {
		 return getInstance().obtenerProximaFechaOpcionPresentar();
	}
	
	public  Date obtenerUltimaFechaPresentadaSSS() throws Exception {
		 return getInstance().obtenerUltimaFechaPresentadaSSS();
	}
		
	
	public static Date buscarProximaFechaPressSuper() {
		Date endDate =  null;
		Date startDate = null;
		Date fechaElec =  null;
		String diaSemana;
		startDate = new Date();
		try {
			startDate = obtenerUltimaFechaOpcionCargada();
		
			if (startDate == null) {
				startDate = new Date();
			}
			
			endDate = DateUtils.anyadeDias(startDate, 10);
						
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);
			Calendar end = Calendar.getInstance();
			end.setTime(endDate);
			
			for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
			   		    
			    diaSemana = DateUtils.getNombreDiaSemana(DateUtils.toCalendar(date)); 
			    if ("Martes".equals(diaSemana)) {
			    	fechaElec = date;
			    	_log.debug("Fecha próxima Press SSS " +  fechaElec);
			    	if (esFeriado(fechaElec)==true) {
			    		fechaElec = buscarProximoDiahabil(fechaElec);
			    	}		    	
			    	break;
			    }
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
		}
		return fechaElec;
	}
	
	
	
   private static boolean esFeriado(Date fechaElec) {
	   boolean flag = false;
	   for (Feriado feriado : feriados) {
		   if (fechaElec.equals(feriado.getFecha())) {
			   flag = true;
		   }
	   }
	   return flag;	
   }
   
   private static Date buscarProximoDiahabil(Date fechaElec) {
	  // Date fechaElecOut =  null;
	   fechaElec = DateUtils.anyadeDias(fechaElec, 1);
	   
	   if (DateUtils.esFeriadoOFinde(fechaElec, feriados)) {
		   buscarProximoDiahabil(fechaElec);
		   fechaElec = DateUtils.anyadeDias(fechaElec, 1);
	   }
	     
	   return fechaElec;
   }
   
}
