package ar.com.ospim.webservice.service;

import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class AfiliadoServiceUtil {
	
	private static Log _log = LogFactoryUtil.getLog(AfiliadoServiceUtil.class);

//	Para informar al SP que tipo operacion estoy realizando para actualizar datos de response
	private final int operacion_altaGrupoFliar = 0;
	private final int operacion_altaBeneficiario = 1;
	private final int operacion_modifBeneficiario = 2;
	private final int operacion_bajaGrupoFliar = 3;
	private final int operacion_bajaBeneficiario = 4;
	private final int operacion_cambioPlan = 5;
	
	/**
	 * Parametros: 
	 * 				0 el SP busca Afiliados p Alta GrupoFamiliares,
	 * 				1 el SP busca Afiliados p Alta Beneficiarios, 
	 * 				2 el SP busca Afiliados p Modificar Beneficiarios,
	 * 				5 el SP busca Afiliados p Modificar para grupo familiar
	 * 	  			3 el SP busca Afiliados p Baja GrupoFamiliares,
	 * 				4 el SP busca Afiliados p Baja Beneficiarios, 
	 * @return
	 */
//	public List<Afiliado> getAfiliadosxAltaGrupoFliar(){
//		
//		List<Afiliado> afiliados = null;
//		
//		afiliados =  AfiliadoServiceImpl.getAfiliadosxAltas(operacion_altaGrupoFliar);
//		
//		return afiliados;
//		
//	}
//
//	public List<Afiliado> getAfiliadosxAltaBeneficiario(){
//		
//		List<Afiliado> afiliados = null;
//		
//		afiliados =  AfiliadoServiceImpl.getAfiliadosxAltas(operacion_altaBeneficiario);
//		
//		return afiliados;
//		
//	}
//	
//	public List<Afiliado> getAfiliadosxModificaBeneficiario(){
//		
//		List<Afiliado> afiliados = null;
//		
//		afiliados =  AfiliadoServiceImpl.getAfiliadosxModifica(operacion_modifBeneficiario);
//		
//		return afiliados;
//		
//	}
//	
//	public List<Afiliado> getAfiliadosxBajaGrupoFliar(){
//		
//		List<Afiliado> afiliados = null;
//		
//		afiliados =  AfiliadoServiceImpl.getAfiliadosxBajas(operacion_bajaGrupoFliar);
//		
//		return afiliados;
//		
//	}
//
//	public List<Afiliado> getAfiliadosxBajaBeneficiario(){
//		
//		List<Afiliado> afiliados = null;
//		
//		afiliados =  AfiliadoServiceImpl.getAfiliadosxBajas(operacion_bajaBeneficiario);
//		
//		return afiliados;
//		
//	}
//	
//	public List<Afiliado> getAfiliadosxCambioPlanGrupoFliar(){
//		
//		List<Afiliado> afiliados = null;
//		
//		afiliados =  AfiliadoServiceImpl.getAfiliadosxModifica(operacion_cambioPlan);
//		
//		return afiliados;
//		
//	}
	
	public List<AfiliadoOpe> getTodasNovedadesPadron(){
		
		List<AfiliadoOpe> afiliados = null;
		
		afiliados = AfiliadoServiceImpl.getTodasNovedadesPadron();
		
		return afiliados;
		
	}
	
	public void updateNovedadesResponse(Integer tipoOper , String cuil_titular, Integer inte, Integer id_transaction, 
			String message_code, String message_description){
		
		AfiliadoServiceImpl.updateNovedadesResponse(tipoOper, cuil_titular, inte, id_transaction, message_code, message_description);
		
	}
	public void updateNovedadesResponse(Integer tipoOper, String cuil_titular, Integer inte, Integer id_transaction, String message_description){
		
		AfiliadoServiceImpl.updateNovedadesResponse(tipoOper, cuil_titular, inte, id_transaction, message_description);
		
	}

}
