package ar.com.ospim.tesoreria.action;


import java.text.DecimalFormat;

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

public class CajaChicaSugiereNroComprobanteAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		DecimalFormat df=new DecimalFormat("#0");
		
		String tipo=ParamUtil.getString(req, "tipo");
		String cuit=ParamUtil.getString(req, "cuit");
		String sucursal=ParamUtil.getString(req, "sucursal");
		String letra=ParamUtil.getString(req, "letra");
		int entidad=ParamUtil.getInteger(req, "entidad");
		int ptoVta=ParamUtil.getInteger(req, "ptovta");
		String resultado = "";
		String comprobantesANumerar = TraeListasServiceUtil.getSystemConfig("cajaChicaComprobantesANumerar");
		
		if(comprobantesANumerar.indexOf (tipo+letra) != -1){
			Double nro= CajaChicaServiceUtil.getUltimoNroComprobante(cuit, sucursal, tipo, letra, entidad,ptoVta);
//			resultado =(nro+=1).toString();
			resultado = df.format(nro+1);
		}
		
		return "{ \"resultado\" : \"" + resultado +"\"}";
	}

}