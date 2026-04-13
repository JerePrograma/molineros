/**
 */

package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurComprobante;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EliminaComprobanteLiquidadoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EliminaComprobanteLiquidadoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.comprobantesliquidados.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		try {
			
			String cuit = ParamUtil.getString(renderRequest, "cuit",null);
			String tipo = ParamUtil.getString(renderRequest, "tipocomprobante",null);
			String letra= ParamUtil.getString(renderRequest,"letracomprobante",null);
			Integer ptoVenta=ParamUtil.getInteger(renderRequest,"ptoventa",0);
			Integer sucu=ParamUtil.getInteger(renderRequest,"sucucomprobante",0);
			String nro=ParamUtil.getString(renderRequest,"nrocomprobante");
			Integer idPrestador = ParamUtil.getInteger(renderRequest,"idprestador",0);
			
			List<SeguimientoSurComprobante> ltd = new ArrayList<SeguimientoSurComprobante>();		
			
			  SeguimientoSur	seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			  
			  
			  for(SeguimientoSurComprobante td:seguimiento.getComprobantes() ){
				  if( !cuit.equalsIgnoreCase(td.getCuit()) ||
					  !tipo.equalsIgnoreCase(td.getTipoComprobante()) ||
					  !letra.equalsIgnoreCase(td.getLetraComprobante()) ||
					  ptoVenta != td.getPtoVenta() ||
					  sucu != td.getSucuComprobante() ||
					  !nro.equalsIgnoreCase(td.getNroComprobante()) ||
					  idPrestador != Integer.parseInt(td.getAcreedorEmpresa().getSucursal())
					){
					  ltd.add(td);
				  }	  
			  }
			  
			  seguimiento.setComprobantes(ltd);
			  session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping
				.findForward("portlet.autorizaciones.comprobantesliquidados.result.search");
		
	}
}