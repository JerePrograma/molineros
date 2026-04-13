package ar.com.global.services;

import java.io.Serializable;

import ar.com.global.beans.MensajeXMLBase;

public class MensajeriaXMLServiceUtil implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5875976947283584516L;
	
	public static final String PREAUTORIZACION_SERVICIO_REQ = "PREAUTORIZACION_REQUEST";
	public static final String PREAUTORIZACION_SERVICIO_RES = "PREAUTORIZACION_RESPONSE";
	public static final String PADRON_SERVICIO_REQ = "PADRON_REQUEST";
	public static final String PADRON_SERVICIO_RES = "PADRON_RESPONSE";


	private static MensajeriaXMLServiceImpl instance = null;

	public static MensajeriaXMLServiceImpl getInstance() {
		if (null == instance) {
			instance = new MensajeriaXMLServiceImpl();
		}
		return instance;
	}
	
	public static void guardarMensajeXML(MensajeXMLBase mensaje) throws Exception {

		getInstance().guardarMensajeXML(mensaje);

	}

}
