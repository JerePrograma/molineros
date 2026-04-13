package ar.com.ospim.farmaciaOspim.services; 

import java.math.BigDecimal; 
import java.util.Date; 
import java.util.List;

import com.liferay.portal.SystemException;
import com.sun.star.sdbc.SQLException;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ColegioFarmacia; 


public class BusquedaColegioFarmaciaServiceUtil { 

	private static BusquedaColegioFarmaciaServiceImpl instance = null; 

	public static BusquedaColegioFarmaciaServiceImpl getInstance() { 
		if (null == instance) { 
			instance = new BusquedaColegioFarmaciaServiceImpl(); 
		} 
		return instance; 
	} 

	 
	public static List<ColegioFarmacia> getBusquedaColegio(String codigoColegio, String detalleColegio) throws Exception { 
		return getInstance().getBusquedaColegio(codigoColegio, detalleColegio); 
	}

	 
	public static Boolean existeColegioFarmacia(String descripcion) throws SystemException{
		return getInstance().existeColegioFarmacia(descripcion);
	}

    public static Long insertaColegioFarmacia(String descripcion, String screenName) throws SystemException, SQLException, java.sql.SQLException{
		return getInstance().insertaColegioFarmacia(descripcion, screenName, null);
	}

	 
	
	 
} 