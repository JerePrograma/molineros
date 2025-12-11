package ar.com.ospim.afiliados.services;

import java.util.List;

import ar.com.ospim.afiliados.beans.SubidaFTPPadronPF;

public class SubidaPadronPFServiceUtil {

	private static SubidaPadronPFServiceImpl instance = null;

	private static SubidaPadronPFServiceImpl getInstance() {
		if (null == instance) {
			instance = new SubidaPadronPFServiceImpl();
		}
		return instance;
	}

	public static void generarPadronPagoFacil() throws Exception {
		getInstance().generarPadronPagoFacil();
	}
	
	public static List<SubidaFTPPadronPF> generarArchivo() throws Exception {
		return getInstance().generarArchivo();
	}


	public static String generarReporte() throws Exception {
		return getInstance().generarReporte();
	}
	
	
}
