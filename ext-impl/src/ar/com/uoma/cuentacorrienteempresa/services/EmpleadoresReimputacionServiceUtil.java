package ar.com.uoma.cuentacorrienteempresa.services;

import java.sql.Connection;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.util.ConnectionHelper;


public class EmpleadoresReimputacionServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(EmpleadoresReimputacionServiceUtil.class);
	
	
	
	public static boolean updatePago(
			String cuit, FichaBoletaPortal origen,FichaBoletaPortal destino,FichaBoletaPortal ajuste,User user) throws Exception {
		return EmpleadoresReimputacionServiceImpl.getInstance().updatePago(origen, destino, ajuste,user) ;
	}
	
}
