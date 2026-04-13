package ar.com.ospim.global.services;

import ar.com.ospim.global.beans.ComprobanteItem;
import java.sql.Connection;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

public class ComprobanteItemServiceUtil {

	private static ComprobanteItemServiceImpl instance = null;

	public static ComprobanteItemServiceImpl getInstance() {
		if (null == instance) {
			instance = new ComprobanteItemServiceImpl();
		}
		return instance;
	}

	public static void save(ComprobanteItem compItem, User user)
			throws SystemException {
		getInstance().save(compItem,
		user.getScreenName());		
	}

	public static void update(ComprobanteItem compItem, User user)
		throws SystemException {
			getInstance().update(compItem,
				user.getScreenName());		
	}
	
	public static void delete(ComprobanteItem compItem, User user)
		throws SystemException {
			getInstance().delete(compItem,
					user.getScreenName());		
	}

	public static void deleteLiquidacionReclamo(int idliquidacion , int idReclamoPrestacional , int idPrestacionReclamo )
			throws SystemException {
				getInstance().deleteLiquidacionReclamo(idliquidacion , idReclamoPrestacional  , idPrestacionReclamo );		
		}
	
	
	public static void actualizarItemsLiquidacion(int id_liquidacion, ComprobanteItem comproItem, User user)
		throws SystemException {
		getInstance().actualizarItemsLiquidacion(id_liquidacion, comproItem,
				user.getScreenName());
	}
}
