package ar.com.ospim.afip.service;

import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.global.services.TraeListasServiceUtil;

public class FeriadosServiceUtil {

	public Calendar obtenerSiguienteDiaHabil(Calendar dia, PortletRequest portletRequest) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(dia.getTime());
		aux.set(Calendar.MILLISECOND, 0);
		aux.set(Calendar.SECOND, 0);
		aux.set(Calendar.MINUTE, 0);
		aux.set(Calendar.HOUR, 0);		
		while (esFeriadoOFinde(aux, TraeListasServiceUtil.getFeriados(portletRequest))) {
			aux.add(Calendar.DATE, 1);
		}
		return aux;
	}
	
	public Calendar obtenerSiguienteDiaHabil(Calendar dia, HttpServletRequest portletRequest) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(dia.getTime());
		aux.set(Calendar.MILLISECOND, 0);
		aux.set(Calendar.SECOND, 0);
		aux.set(Calendar.MINUTE, 0);
		aux.set(Calendar.HOUR, 0);		
		while (esFeriadoOFinde(aux, TraeListasServiceUtil.getFeriados(portletRequest))) {
			aux.add(Calendar.DATE, 1);
		}
		return aux;
	}
	
	public Calendar obtenerSiguienteDiaHabil(Calendar dia) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(dia.getTime());
		aux.set(Calendar.MILLISECOND, 0);
		aux.set(Calendar.SECOND, 0);
		aux.set(Calendar.MINUTE, 0);
		aux.set(Calendar.HOUR_OF_DAY, 0);
		List<Feriado> feriados = FeriadosServiceImpl.getInstance()
				.findAllFeriados();
		while (esFeriadoOFinde(aux, feriados)) {
			aux.add(Calendar.DATE, 1);
		}
		return aux;
	}

	protected boolean esFeriadoOFinde(Calendar dia, List<Feriado> feriados) {
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
}
