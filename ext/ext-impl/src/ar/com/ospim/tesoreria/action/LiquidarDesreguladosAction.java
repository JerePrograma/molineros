package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.beans.ProcesoSQL;
import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class LiquidarDesreguladosAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(LiquidarDesreguladosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		/*
		PortletSession session = renderRequest.getPortletSession();
		int cancela=ParamUtil.getInteger(renderRequest, "cancela");
		 
		try {
			if(cancela==0){
				ProcessBuilder pb = new ProcessBuilder("./ejecuta_desreg_NOBORRAR.sh");			
				//File directory = new File("/home/usuario/");
				//pb.directory(directory);
				pb.start();
				
				ProcesoSQL result=LiquidaDesreguladosServiceUtil.isRunningProcess();
				int cont=0;
				//EL PROCESO TARDA EN COMENZAR A CORRER.
				while(result.getProcid()==0 && cont<10000){				
					result=LiquidaDesreguladosServiceUtil.isRunningProcess();
					cont++;
				}
				//NO CORRIO EL PROCESO...
				if(cont>=10000){
					SessionErrors.add(renderRequest, new Exception().getClass().getName());
				}
				//LiquidaDesreguladosServiceUtil.isRunningProcess().getProcid()>0);
				session.setAttribute("procesoSQL", result);
			}else{
				LiquidaDesreguladosServiceUtil.cancelaProceso(cancela);
				ProcesoSQL result=LiquidaDesreguladosServiceUtil.isRunningProcess();
				int cont=0;
				while(result.getProcid()!=0 && cont<10000){				
					result=LiquidaDesreguladosServiceUtil.isRunningProcess();
					cont++;
				}
				//NO CORRIO EL PROCESO...
				if(cont>=10000){
					SessionErrors.add(renderRequest, new Exception().getClass().getName());
				}
				session.setAttribute("procesoSQL", new ProcesoSQL());
			}
		} catch (Exception e) {
			logger.error(e);
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		
		*/
		
        Integer idJob =Integer.parseInt(TraeListasServiceUtil.getSystemConfig("reporte.liquidar_desregulados"));
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		
		String cuit = ParamUtil.getString(renderRequest, "cuil",null);
		List<String>parameters = new ArrayList<String>();
		
		try {
			parameters.add(cuit);
		}catch(Exception e){}
		
		SchedulerServiceUtil.addParameters("reporte.liquidar_desregulados", idJob, parameters);
		
		SchedulerServiceUtil.run(idJob);
		
		return mapping
				.findForward("portlet.tesoreria.liquidar.desregulados.result");
	}

}
