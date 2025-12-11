package ar.com.ospim.comprobantesPortalProveedores.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class ComprobantesIntegracionVerificacionesJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String carpetaStr = ParamUtil.getString(req, "carpeta");
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		
		Integer carpeta = null;
		boolean existeCarpeta=false;
		boolean existeCarpetaLiquidada=false;
		
		try {
			carpeta =Integer.parseInt( carpetaStr.split("_")[1])*100+(Integer.parseInt(carpetaStr.split("_")[0]) + 1);
			List<IntegracionDetalleDS>detalles = IntegracionServiceUtil.detalleDSByPeriodo(carpeta);
			if(!detalles.isEmpty()) {
				existeCarpeta=true;
				for(IntegracionDetalleDS d:detalles) {
					if(d.getOrdenPago()!=null && d.getOrdenPago()>0) {
						existeCarpetaLiquidada=true;
						break;
					}
				}
			}
		}catch(Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		return "{ \"existeCarpeta\" : \"" + existeCarpeta +
				  "\",\"existeCarpetaLiquidada\" : \"" + existeCarpetaLiquidada +
				"\"}";
		
	}
	
}
