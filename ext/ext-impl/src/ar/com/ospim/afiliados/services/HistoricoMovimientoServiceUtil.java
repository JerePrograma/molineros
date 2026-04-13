package ar.com.ospim.afiliados.services;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.HistoricoMovimientoAfiliado;

public class HistoricoMovimientoServiceUtil {
	private static HistoricoMovimientoServiceImpl instance = null;

	public HistoricoMovimientoServiceUtil() {
	}

	public static HistoricoMovimientoServiceImpl getInstance() {
		if (null == instance)
			instance = new HistoricoMovimientoServiceImpl();
		return instance;
	}

	public static List<HistoricoMovimientoAfiliado> buscarHistorico(
			String cuil_titular, Date fecha_desde, Date fecha_hasta)
			throws Exception {
		return getInstance().buscaHistoricoMovimientoAfiliado(cuil_titular,
				fecha_desde, fecha_hasta);
	}

}
