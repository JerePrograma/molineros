package ar.com.ospim.global.services;

import java.util.List;

import ar.com.ospim.global.beans.Pagare.Estado;

/**
 * @author Martin Moreyra
 * 
 */
public class PagareServiceUtil {

	private static PagareServiceImpl instance = null;

	public static PagareServiceImpl getInstance() {
		if (null == instance) {
			instance = new PagareServiceImpl();
		}
		return instance;
	}

	

	public static List<Estado> getEstadosPagare() {
		return getInstance().getPagareEstados();
	}
	
	
}
