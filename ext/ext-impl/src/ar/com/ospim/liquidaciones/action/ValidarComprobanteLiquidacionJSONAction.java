package ar.com.ospim.liquidaciones.action;

import java.util.List;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class ValidarComprobanteLiquidacionJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean comprobanteErroneo=false;
		
		try {
		
			HttpSession session = req.getSession();
			Integer idLiquidacion= ParamUtil.getInteger(req,"id_liquidacion");
			
			Liquidacion m = EditarLiquidacionServiceUtil.getLiquidacionEntry(idLiquidacion);
			
			Integer idPrestador = ParamUtil.getInteger(req,"cuit");
			Integer sucu = ParamUtil.getInteger(req,"comprobante_sucu");
			String comprobanteTipo=ParamUtil.getString(req, "comprobante_tipo");
			String comprobanteLetra=ParamUtil.getString(req, "comprobante_letra");
			
			String comprobanteNro=ParamUtil.getString(req, "comprobante_nro");
			try{
			  comprobanteNro= String.format("%08d",Integer.parseInt(comprobanteNro));
			}catch(Exception e) {
				
			}
			
			String comprobanteGuardado= m.getCompro_a_debitar_numero();
			try{
				comprobanteGuardado=String.format("%08d", Integer.parseInt( m.getCompro_a_debitar_numero()));	  
			}catch(Exception e) {
					
			}
			
			if(!m.getCompro_a_debitar_letra().equalsIgnoreCase(comprobanteLetra) ||
			   !comprobanteGuardado.equalsIgnoreCase(comprobanteNro)	||
			   !m.getCompro_a_debitar_tipo().equalsIgnoreCase(comprobanteTipo) ||
			   m.getSucu()!=sucu ||
			   m.getId_prestador()!=idPrestador
			   ) {
			   comprobanteErroneo=true;	
			}
			
			
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		
		return "{ \"comprobanteErroneo\" : \"" + comprobanteErroneo +
				"\"}";
		
	}
	
}
