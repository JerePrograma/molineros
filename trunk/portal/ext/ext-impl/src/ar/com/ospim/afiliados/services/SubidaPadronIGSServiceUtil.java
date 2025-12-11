package ar.com.ospim.afiliados.services;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.SubidaFTPPadronIGS;
import ar.com.ospim.afiliados.beans.TotalesPadronIGS;

public class SubidaPadronIGSServiceUtil {

	private static SubidaPadronIGSServiceImpl instance = null;

	private static SubidaPadronIGSServiceImpl getInstance() {
		if (null == instance) {
			instance = new SubidaPadronIGSServiceImpl();
		}
		return instance;
	}

	public static void grabarReporte(Date fechaDesde,
			Date fechaHasta) throws Exception {
		getInstance().grabarReporte(fechaDesde, fechaHasta);
	}
	
	public static List<SubidaFTPPadronIGS> generarArchivo(Date fechaDesde) throws Exception {
		return getInstance().generarArchivo(fechaDesde);
	}


	public static List<TotalesPadronIGS> generarTotales(Date fechaDesde) throws Exception {
		return getInstance().generarArchivoTotales(fechaDesde);
	}
	
	
}
