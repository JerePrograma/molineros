package ar.com.global.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.services.InterbankingServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.OrdenPago;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarOrdenesPagoAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * 
 * @author Martin Moreyra
 * 
 */
public class InterbankingOrdenesPagoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(InterbankingOrdenesPagoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.interbanking.ordenes_pago.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		try {
			
			int entidad=WebKeysGlobal.OSPIM;
			if(renderResponse.getNamespace().equals("_FAR_1_")){
				entidad=WebKeysGlobal.AMTIMA;
			}else if(renderResponse.getNamespace().equals("_UOM_1_")){
				entidad=WebKeysGlobal.UOMA;
			}

			String cmd = renderRequest.getParameter("cmd");
			
			if("filter".equals(cmd)){
				filtrar(entidad,renderRequest);
			}else if("delete".equals(cmd)){
				delete(entidad,renderRequest);
				return mapping.findForward("portlet.liquidaciones.interbanking_ops");
			}else if("deleteall".equals(cmd)){
				deleteall(entidad,renderRequest);
				return mapping.findForward("portlet.liquidaciones.interbanking_ops");
			}
				
			
		} catch (Exception e) {
			_log.error(e);
		}		
		return mapping.findForward("portlet.interbanking.ordenes_pago.result.search");
	}
	
	
	private void filtrar(int entidad,RenderRequest renderRequest) {
		
		String numerodde = null;
		String numerohta = null;
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();

		if (null != renderRequest.getParameter("numerodde")) {
			numerodde = renderRequest.getParameter("numerodde").trim().length() > 0 ? renderRequest
					.getParameter("numerodde")
					: null;
		}
		
		if (null != renderRequest.getParameter("numerohta")) {
			numerohta = renderRequest.getParameter("numerohta").trim().length() > 0 ? renderRequest
					.getParameter("numerohta")
					: null;
		}
		
		int desdeDia=ParamUtil.getInteger(renderRequest, "desdeDia");
		int desdeMes=ParamUtil.getInteger(renderRequest, "desdeMes");
		int desdeAnio=ParamUtil.getInteger(renderRequest, "desdeAnio");
		Calendar fechaDesde=null;
		if(desdeMes>=0 && desdeAnio>0){
			fechaDesde=Calendar.getInstance();
			fechaDesde.set(Calendar.DAY_OF_MONTH, desdeDia==0?1:desdeDia);
			fechaDesde.set(Calendar.MONTH, desdeMes);
			fechaDesde.set(Calendar.YEAR, desdeAnio);
		}
		
		int hastaDia=ParamUtil.getInteger(renderRequest, "hastaDia");
		int hastaMes=ParamUtil.getInteger(renderRequest, "hastaMes");
		int hastaAnio=ParamUtil.getInteger(renderRequest, "hastaAnio");
		Calendar fechaHasta=null;
		if(hastaMes>=0 && hastaAnio>0){
			fechaHasta=Calendar.getInstance();
			fechaHasta.set(Calendar.DAY_OF_MONTH, hastaDia==0?1:hastaDia);
			fechaHasta.set(Calendar.MONTH, hastaMes);
			fechaHasta.set(Calendar.YEAR, hastaAnio);
		}
		
		
		List<OrdenPago> lista=new ArrayList<OrdenPago>();
		try {
			lista = InterbankingServiceUtil.getOrdenesPago(numerodde!=null?Integer.parseInt(numerodde):null ,
					numerohta!=null?Integer.parseInt(numerohta):null, null!=fechaDesde?fechaDesde.getTime():null,
							null!=fechaHasta?fechaHasta.getTime():null, entidad);
		} catch (Exception e) {}
				
		session.setAttribute(WebKeysGlobal.INTERBANKING_OPS, lista);
		
	}
	
	
    private void delete(int entidad,RenderRequest renderRequest) {
				
		String ordenes=ParamUtil.getString(renderRequest, "ordenes");
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		Boolean ret=true;
		List<OrdenPago>lista =(List<OrdenPago>)session.getAttribute(WebKeysGlobal.INTERBANKING_OPS);
		try {
			ret = InterbankingServiceUtil.deleteOrdenesPago(ordenes, entidad);
			List<OrdenPago>l=new ArrayList<OrdenPago>();
			if(ret) {
				for(OrdenPago op:lista) {
					if(!ordenes.contains(op.getId().toString())) {
						l.add(op);
					}
				}
				lista=l;
			}
		} catch (Exception e) {}
				
		session.setAttribute(WebKeysGlobal.INTERBANKING_OPS, lista);
		
	}

    private void deleteall(int entidad,RenderRequest renderRequest) {
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		Boolean ret=true;
		String ordenes="";
		List<OrdenPago>lista =(List<OrdenPago>)session.getAttribute(WebKeysGlobal.INTERBANKING_OPS);
		if(lista!=null && !lista.isEmpty()) {
		  for(OrdenPago op:lista) {
			ordenes += op.getId().toString() + ",";
		  }
		  try {
			  ret = InterbankingServiceUtil.deleteOrdenesPago(ordenes, entidad);
			  List<OrdenPago>l=new ArrayList<OrdenPago>();
			  if(ret) {
		       lista=new ArrayList<OrdenPago>();		
			  }
		  } catch (Exception e) {}
		  session.setAttribute(WebKeysGlobal.INTERBANKING_OPS, lista);
		}  
	}
	
	
}




