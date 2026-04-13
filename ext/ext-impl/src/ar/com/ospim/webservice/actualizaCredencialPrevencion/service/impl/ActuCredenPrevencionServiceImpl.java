package ar.com.ospim.webservice.actualizaCredencialPrevencion.service.impl;

import org.apache.log4j.Logger;

import ar.com.ospim.afiliados.services.CredencialesServiceUtil;
import ar.com.ospim.webservice.actualizaCredencialPrevencion.ActualizacionCredencialService;
import ar.com.ospim.webservice.actualizaCredencialPrevencion.service.base.ActuCredenPrevencionServiceBaseImpl;
import ar.com.ospim.webservice.beans.MensajeActualizacionCredencial;
import ar.com.ospim.webservice.beans.ResultadoActualizacionCredencial;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;


public class ActuCredenPrevencionServiceImpl
    extends ActuCredenPrevencionServiceBaseImpl {
	
	private static Logger _log = Logger.getLogger(ActuCredenPrevencionServiceImpl.class);

	public ResultadoActualizacionCredencial actualizarCredencialBeneficiario(MensajeActualizacionCredencial mensaje){
		
		String user = "prevencion_ws";
		
		AfiliacionPrevencionDTO cred = null;
		ResultadoActualizacionCredencial resultado = new ResultadoActualizacionCredencial();
		Integer idTransaccion = 0;
//		String descripcionMensaje = null;
		String validaMensaje = "";
		boolean validaOk = false;
		
		if(mensaje != null && mensaje.getCabecera() == null){
			resultado.setIdTransaccion(idTransaccion);
			resultado.setDescripcionError("No se puede agregar una Cabecera");
		}
		if(mensaje != null && mensaje.getDetalle() == null){
			resultado.setIdTransaccion(idTransaccion);
			resultado.setDescripcionError("No se puede agregar un Detalle");
		}
		
		if(mensaje.getDetalle().getAfiliacion() != null){
			cred = mensaje.getDetalle().getAfiliacion();
			
			validaMensaje = cred.validaCredencial();
			
			if(!validaMensaje.equalsIgnoreCase(AfiliacionPrevencionDTO.VALIDA_OK)){
				resultado.setIdTransaccion(idTransaccion);
				resultado.setDescripcionError(validaMensaje);
				
				return resultado;
			}
			
			try{
				validaOk = CredencialesServiceUtil.validarAfiliadoCredencialPrevencion(cred);
			
			}catch (Exception e) {
//				validaOK = false;
				resultado.setIdTransaccion(idTransaccion);
				resultado.setDescripcionError("No existe el Afiliado en el padrón de Ospim");
			}
		}
		if(validaOk){
			
			try {
				idTransaccion = CredencialesServiceUtil.actualizarCredencialPrevencion(cred, user);
				resultado.setIdTransaccion(idTransaccion);
				
			} catch (Exception e) {
				_log.error(e);
				resultado.setDescripcionError(e.getMessage());
			}
			
		}
		
//		Random r = new Random(Calendar.getInstance().getTimeInMillis());
//		idTransaccion = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
//		if(idTransaccion < 0){
//			idTransaccion = (-1)*idTransaccion;
//		}
//		
//		resultado.setIdTransaccion(idTransaccion);
		
		return resultado;
		
	}
	
}
