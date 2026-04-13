package ar.com.ospim.autorizaciones.services;

import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.autorizaciones.beans.Cartilla;

public class CartillaServiceUtil {

	private static Log _log = LogFactoryUtil
			.getLog(CartillaServiceUtil.class);

	private static CartillaServiceImpl instance = null;

	public static CartillaServiceImpl getInstance() {
		if (null == instance) {
			instance = new CartillaServiceImpl();
		}
		return instance;
	}

	 
	public static List<Cartilla> getListaCartillas(String tipo,String prestador,String plan,String localidad,String provincia, String especialidad, String trabajaen,Boolean incluyeBajas) {
		 return getInstance().getListaCartillas(tipo, prestador, plan, localidad, provincia, especialidad, trabajaen,incluyeBajas);
	}
	
	public static Cartilla getCartillaById(Integer id) {
		 return getInstance().getCartillaById(id);
	}
	
	public static long eliminaCartilla(int idCartilla, String screenName,Date fechaBaja) throws Exception {
	    	
		try {			
			    getInstance().eliminaCartilla(idCartilla, screenName, fechaBaja, null);
	    } catch (Exception e) {
			 	_log.error("Error al Eliminar Cartilla");
			 	_log.error(e);
		}  
		return idCartilla;
	}
	
    public static long recuperaCartilla(int idCartilla, String screenName) throws Exception {    	
		
	  try {			
			getInstance().recuperaCartilla(idCartilla, screenName,null);
	  } catch (Exception e) {
			_log.error("Error al Recuperar Cartilla");
		 	_log.error(e);
	  }    
	  return idCartilla;
	}
}

