package ar.com.cgt.ddhh.services;

import java.util.List;

import ar.com.cgt.ddhh.beans.TemasNormasDDHH;
import ar.com.cgt.ddhh.beans.TiposNormasDDHH;


/**
 * Mascara del servicio que da acceso a los datos de la aplicación (BD).
 */
public class TraeListasServiceUtil {

	private static TraeListasServiceImpl instance = null;

	public static TraeListasServiceImpl getInstance() {
		if (null == instance) {
			instance = new TraeListasServiceImpl();
		}
		return instance;
	}
	
	public static List<TiposNormasDDHH> getTiposNormasDDHH(String sistema) {
		return getInstance().getTiposNormasDDHH(sistema) ;
	}
	
	public static List<TemasNormasDDHH> getTemasNormasDDHH() {
		return getInstance().getTemasNormasDDHH() ;
	}
}