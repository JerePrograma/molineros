package ar.com.global.services;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Destinatario;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.webservices.agnitas_webservice.EmmWebService_PortProxy;
import ar.com.global.webservices.agnitas_webservice.StringArrayType;
import ar.com.ospim.global.beans.OrdenPago;

public class InterbankingServiceUtil {
	
	private static InterbankingServiceImpl instance = null;

	public static InterbankingServiceImpl getInstance() {
		if (null == instance) {
			instance = new InterbankingServiceImpl();
		}
		return instance;
	}

	
	public static List<OrdenPago> getOrdenesPago(Integer opDde,
			Integer opHta,  Date fechaDesde,
			Date fechaHasta,int entidad) throws Exception {
		return getInstance().getOrdenesPago(opDde, opHta,fechaDesde, fechaHasta, entidad);
	}
	
	public static Boolean deleteOrdenesPago(String ops,int entidad) throws Exception {
		return getInstance().deleteOrdenesPago(ops,  entidad);
	}
}


