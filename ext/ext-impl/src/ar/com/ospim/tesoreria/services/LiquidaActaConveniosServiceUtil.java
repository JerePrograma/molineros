package ar.com.ospim.tesoreria.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.tesoreria.LiquidarActaConvenioException;
import ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones;
import ar.com.ospim.tesoreria.beans.LiquidacionActaConvenio;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class LiquidaActaConveniosServiceUtil {
	private static Log logger = LogFactoryUtil
			.getLog(LiquidaActaConveniosServiceUtil.class);

	private static LiquidaActaConveniosServiceImpl instance = null;

	public static LiquidaActaConveniosServiceImpl getInstance() {
		if (null == instance) {
			instance = new LiquidaActaConveniosServiceImpl();
		}
		return instance;
	}

	public static List<ConsolidadoLiquidaciones> getConsolidadoLiquidaciones(Date fechaIni)
			throws SystemException {
		Calendar cal=null;
		if(null==fechaIni){			
			cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add( Calendar.DATE, -120 );			
		}else{
			cal = Calendar.getInstance();
			cal.setTime(fechaIni);
		}
		return getInstance().getConsolidadoLiquidaciones(cal.getTime());
	}
	
	public static List<LiquidacionActaConvenio> getLiqActaConvenioFechaLiq(java.util.Date fechaLiq)
			throws SystemException {		
		return getInstance().getLiqActaConvenioFechaLiq(fechaLiq);
	}
	
	public static int liqActaConvenio()
			throws LiquidarActaConvenioException {		
		return getInstance().liqActaConvenio();
	}

}
