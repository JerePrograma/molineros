package ar.com.ospim.tesoreria.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CalculoDeudaMasivoCab;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;

public class CalculoDeudaMasivoAction extends PortletAction {
	
	public static final String reporte_system_config = "reporte.calculo_deuda_masivo";
	
	private static Log logger = LogFactoryUtil.getLog(CalculoDeudaMasivoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
				
		/*Ponemso los resultados de los procesos de calculo de deuda masivo*/
		List<CalculoDeudaMasivoCab> results = ActaServiceUtil.getProcesosCalculoDeudaMasivo();
		renderRequest.setAttribute(WebKeysTesoreria.CALCULOS_DEUDA_MASIVA_RESULTADOS, results);
		/**/
		User user = PortalUtil.getUser(renderRequest);
		
		Integer idJobRankingDeudaEmpresa =Integer.parseInt(TraeListasServiceUtil.
											getSystemConfig(RankingDeudaEmpresaPeriodoAction.reporte_system_config));
        Integer idJobCalcDeudaMasivo =Integer.parseInt(TraeListasServiceUtil.getSystemConfig(reporte_system_config));

        /*Deuda Nómina*/
        Integer fechaDeuNomDiaDesde = ParamUtil.getInteger (renderRequest, "fechaDeuNomDiaDesde");
		Integer fechaDeuNomMesDesde = ParamUtil.getInteger (renderRequest, "fechaDeuNomMesDesde");
		Integer fechaDeuNomAnioDesde = ParamUtil.getInteger(renderRequest, "fechaDeuNomAnioDesde");

        Integer fechaDeuNomDiaHasta = ParamUtil.getInteger (renderRequest, "fechaDeuNomDiaHasta");
		Integer fechaDeuNomMesHasta = ParamUtil.getInteger (renderRequest, "fechaDeuNomMesHasta");
		Integer fechaDeuNomAnioHasta = ParamUtil.getInteger(renderRequest, "fechaDeuNomAnioHasta");
		
		/*Deuda Período*/
        Integer fechaDeuPeriDiaDesde = ParamUtil.getInteger (renderRequest, "fechaDeuPeriDiaDesde");
		Integer fechaDeuPeriMesDesde = ParamUtil.getInteger (renderRequest, "fechaDeuPeriMesDesde");
		Integer fechaDeuPeriAnioDesde = ParamUtil.getInteger(renderRequest, "fechaDeuPeriAnioDesde");

        Integer fechaDeuPeriDiaHasta = ParamUtil.getInteger (renderRequest, "fechaDeuPeriDiaHasta");
		Integer fechaDeuPeriMesHasta = ParamUtil.getInteger (renderRequest, "fechaDeuPeriMesHasta");
		Integer fechaDeuPeriAnioHasta = ParamUtil.getInteger(renderRequest, "fechaDeuPeriAnioHasta");
		
		/*Fecha Impago*/
		Integer fechaDiaImpago = ParamUtil.getInteger (renderRequest, "fechaDiaImpago");
		Integer fechaMesImpago = ParamUtil.getInteger (renderRequest, "fechaMesImpago");
		Integer fechaAnioImpago = ParamUtil.getInteger(renderRequest, "fechaAnioImpago");
		
		/*Fecha Obligación*/
		Integer fechaDiaObligacion = ParamUtil.getInteger (renderRequest, "fechaDiaObligacion");
		Integer fechaMesObligacion = ParamUtil.getInteger (renderRequest, "fechaMesObligacion");
		Integer fechaAnioObligacion = ParamUtil.getInteger(renderRequest, "fechaAnioObligacion");
		
		
//		String fechaDeuNomDesde = ParamUtil.getString(renderRequest,"fechaDeuNomDesde", null);
//		String fechaDeuNomHasta = ParamUtil.getString(renderRequest,"fechaDeuNomHasta", null);
		Boolean sinCalDeudaNomina = ParamUtil.getBoolean(renderRequest,"sinCalDeudaNomina", false);
//		String fechaDeuPeriDesde = ParamUtil.getString(renderRequest,"fechaDeuPeriDesde", null);
//		String fechaDeuPeriHasta = ParamUtil.getString(renderRequest,"fechaDeuPeriHasta", null);
//		String fechaImpago = ParamUtil.getString(renderRequest,"fechaImpago", null);
//		String fechaObligacion = ParamUtil.getString(renderRequest,"fechaObligacion", null);
		Boolean extender30diasMoli = ParamUtil.getBoolean(renderRequest,"extender30diasMoli", false);
		
//		Date deuNomDesde = null;
//		try {
//			deuNomDesde = sdf.parse(fechaDeuNomDesde);
//		} catch (Exception e) {
//			deuNomDesde = null;
//		}		
		String fechaDeuNomDesde = fechaDeuNomAnioDesde+String.format("%02d", fechaDeuNomMesDesde)+String.format("%02d", fechaDeuNomDiaDesde) ;
		String fechaDeuNomHasta = fechaDeuNomAnioHasta+String.format("%02d", fechaDeuNomMesHasta)+String.format("%02d", fechaDeuNomDiaHasta) ;
		
		String fechaDeuPeriDesde = fechaDeuPeriAnioDesde+String.format("%02d", fechaDeuPeriMesDesde)+String.format("%02d", fechaDeuPeriDiaDesde) ;
		String fechaDeuPeriHasta = fechaDeuPeriAnioHasta+String.format("%02d", fechaDeuPeriMesHasta)+String.format("%02d", fechaDeuPeriDiaHasta) ;

		String fechaImpago = fechaAnioImpago+String.format("%02d", fechaMesImpago)+String.format("%02d", fechaDiaImpago) ;

		String fechaObligacion = fechaAnioObligacion+String.format("%02d", fechaMesObligacion)+String.format("%02d", fechaDiaObligacion) ;

		
		int i = 0;
		
		List<String> parametersStep1 = new ArrayList<String>();
		if(!sinCalDeudaNomina){
			parametersStep1.add(i++,fechaDeuNomDesde);
			parametersStep1.add(i++,fechaDeuNomHasta);
		}else{ // forzando un vacio no corre...
			parametersStep1.add(i++,"null");//"");
			parametersStep1.add(i++,"null");//"");
		}
		i = 0;
		List<String> parametersStep2 = new ArrayList<String>();
//		parametersStep2.add(i++,fechaDeuNomDesde);
//		parametersStep2.add(i++,fechaDeuNomHasta);
		parametersStep2.add(i++,String.valueOf(sinCalDeudaNomina));
		parametersStep2.add(i++,fechaDeuPeriDesde);
		parametersStep2.add(i++,fechaDeuPeriHasta);
		parametersStep2.add(i++,fechaImpago);
		parametersStep2.add(i++,fechaObligacion);
		parametersStep2.add(i++,String.valueOf(extender30diasMoli));
		parametersStep2.add(i++,user.getScreenName());
		
		logger.debug(RankingDeudaEmpresaPeriodoAction.reporte_system_config);
		logger.debug(parametersStep1);
		logger.debug(reporte_system_config);
		logger.debug(parametersStep2);
		
//		if(!sinCalDeudaNomina){
			SchedulerServiceUtil.addParameters(RankingDeudaEmpresaPeriodoAction.reporte_system_config, idJobRankingDeudaEmpresa, parametersStep1);
//		}
		SchedulerServiceUtil.addParameters(reporte_system_config, idJobCalcDeudaMasivo, parametersStep2);
		
		logger.debug("correr: " + reporte_system_config) ; 
		
		SchedulerServiceUtil.run(idJobCalcDeudaMasivo);
		
		return mapping.findForward("portlet.tesoreria.calc_deu_masivo_result");
	}

}
