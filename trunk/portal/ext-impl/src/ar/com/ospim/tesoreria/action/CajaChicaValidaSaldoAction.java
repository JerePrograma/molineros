package ar.com.ospim.tesoreria.action;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class CajaChicaValidaSaldoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		int idCaja=ParamUtil.getInteger(req, "idCaja");
		Double importe=ParamUtil.getDouble(req, "importe");
		int entidad=ParamUtil.getInteger(req, "entidad");
		
		Boolean resultado = true;
		Boolean sugiere =false;
		String msg="";
		
		CajaChica cajaChica = CajaChicaServiceUtil.get(idCaja, entidad);
		Double importeOriginal = cajaChica.getImporteOriginal();
		Double saldo = cajaChica.getSaldo();
		Double porcInhabilitaCarga = Double.parseDouble(TraeListasServiceUtil.getSystemConfig("cajaChicaPorcentajeInhabilitaCargaComprobantes"));
		Double porcSugiereReposicion = Double.parseDouble(TraeListasServiceUtil.getSystemConfig("cajaChicaPorcentajeSugiereReposicion"));
		
		if(saldo<importe){
			resultado=false;
			msg="Saldo Insuficiente para este comprobante";
		}else if(saldo<cajaChica.getImporteOriginal()*porcInhabilitaCarga/100){
			resultado=false;
			msg="Saldo inferior al "+ porcInhabilitaCarga + "% asignado";
		}else  if(saldo-importe<cajaChica.getImporteOriginal()*porcSugiereReposicion/100){
			sugiere=true;
		}
		
		return "{ \"resultado\" : \"" + resultado + 
				"\",\"mensaje\" : \"" + msg + 
				"\",\"sugierereposicion\" : \""+sugiere+"\"}";
	}

}