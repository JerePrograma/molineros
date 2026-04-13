package ar.com.ospim.afiliados.services;

import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.afiliados.FormOpcionSSSDuplicadoException;
import ar.com.ospim.afiliados.FormOpcionSSSNoEnviadoException;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


public class AfiOpcionSSUtil {

	private static AfiOpcionSSImpl opcionSSImpl;
	
	private static Log logger = LogFactoryUtil.getLog(AfiOpcionSSUtil.class);
	
	public AfiOpcionSSUtil(){
		super();
		this.opcionSSImpl = new AfiOpcionSSImpl();
	}
	
	public static int insertarOpcionSS(DetalleOpcionesSS det, String user) {
		
		int result = -1;
		
//		como el tomo y libro son fijos por la Delegacion, los busco para insertarlos...
		Delegacion d = opcionSSImpl.getDelegacionPorId(det.getDelegacionId());
		det.setLibro(d.getLibro());
		det.setTomo(d.getTomo());
		try{
			result = opcionSSImpl.insertarOpcionSS(det, user);
			
		}catch (SQLException e) {
			return -1;
		}
		
		return result;
	}
	
	public static int actualizarOpcionSS(DetalleOpcionesSS det, String user) {
		
		int result = -1;
		
//		como el tomo y libro son fijos por la Delegacion, los busco para insertarlos...
		Delegacion d = opcionSSImpl.getDelegacionPorId(det.getDelegacionId());
		det.setLibro(d.getLibro());
		det.setTomo(d.getTomo());
		try{
			result = opcionSSImpl.actualizarOpcionSS(det, user);
			
		}catch (SQLException e) {
			return -1;
		}
		
		return result;
	}

	public static DetalleOpcionesSS buscarOpcionSssPorCuil(String cuilOpcion , String nroFormulario ){
		
		return opcionSSImpl.getOpcionSssPorCuil(cuilOpcion , nroFormulario );
		
	}
	

	public static Delegacion buscarDelegacionPorId(int idDelegacion){
		
		return opcionSSImpl.getDelegacionPorId(idDelegacion);
		
	}
	
	public void validarNroFormDuplicado(int nroFormulario, int idOpcionSSS) throws FormOpcionSSSDuplicadoException, FormOpcionSSSNoEnviadoException{
		
		int resultado =  0;
		
		resultado = opcionSSImpl.validarNroFormDuplicado(nroFormulario,idOpcionSSS);
		
		switch (resultado) {
		case -1:
			throw new FormOpcionSSSNoEnviadoException();
		case 0:
			throw new FormOpcionSSSDuplicadoException();	
		}
		
	}
	
	public static String validarOpcionSSS(String cuilOpcSss, int nroFormulario, String regimen, Date fechaCertificacion, int idOpcionSSS){
		
		return opcionSSImpl.validarOpcionSSS(cuilOpcSss, nroFormulario, regimen, fechaCertificacion, idOpcionSSS);
			
	}
	
	public static void eliminarOpcionSS(String cuil, String nroFormulario, String user) {
		
		try{
			opcionSSImpl.eliminarOpcionSS(cuil, nroFormulario, user);
			
		} catch (SystemException e) {
			logger.error(e);
		}
	}
	
	public static int verificaCantidadFormulariosOpcionExportadosSSS() {
		
		int result = 0;
		try{
			result = opcionSSImpl.verificaCantidadFormulariosOpcionExportadosSSS();
			
		} catch (SystemException e) {
			logger.error(e);
		}
		
		return result;
	}
	
	public static int volverAtrasFormulariosOpcionExportadosSSS() {
		
		int result = 0;
		try{
			result = opcionSSImpl.volverAtrasFormulariosOpcionExportadosSSS();
			
		} catch (SystemException e) {
			logger.error(e);
		}
		
		return result;
	}
	
	public static void recuperarOpcionSS(String cuil, String nroFormulario, String user) {
		
		try{
			opcionSSImpl.recuperarOpcionSS(cuil, nroFormulario, user);
			
		} catch (SystemException e) {
			logger.error(e);
		}
	}
	
}
