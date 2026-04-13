package ar.com.uoma.paritarias.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

import ar.com.ospim.util.DateUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.beans.Paritaria;
import ar.com.uoma.paritarias.services.ParitariaServiceUtil;

public class AltaVerAumentosParitariasAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(AltaVerAumentosParitariasAction.class);
	Date periodoDate = null;

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		renderRequest.removeAttribute(WebKeysUOMA.SUELDOS_BASICOS );
		renderRequest.removeAttribute(WebKeysUOMA.JORNALES_BASICOS );
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		if (Constants.VIEW.equalsIgnoreCase(cmd)) {
		    String[] fecha =  null; 
			String camara = ParamUtil.getString(renderRequest, "camara");	 
		    String fechaParitaria = ParamUtil.getString(renderRequest, "fecha_paritaria"); 
		    fecha = fechaParitaria.split("-");
			boolean simular = false;
			this.detalleParitarias(camara,simular,fecha[1], fecha[0],renderRequest);
			renderRequest.setAttribute("modoConsulta","true" ); 
			Paritaria par =  new Paritaria();
			par.setCamara(camara);
			par.setFechaAltaParitaria(DateUtils.getDatetimeFromString("yyyy-MM-dd", fechaParitaria));
			renderRequest.removeAttribute(WebKeysUOMA.ALTA_PARITARIAS );
			renderRequest.setAttribute(WebKeysUOMA.ALTA_PARITARIAS ,par);	
		}else {
			boolean simular = false;
			String nombreCamara = ParamUtil.getString(renderRequest, "nombre_camara");	
			simular = Boolean.parseBoolean(ParamUtil.getString(renderRequest, "simular"));	
			String mes = ParamUtil.getString(renderRequest, "fechaDesdeMes");
			String anno = ParamUtil.getString(renderRequest, "fechaDesdeAnio");
			this.detalleParitarias(nombreCamara,simular,mes, anno,renderRequest);
			
			
		}
		return mapping.findForward(getForward(renderRequest,
				"portlet.uoma.paritarias.alta_ver_paritaria"));
		
	}
	
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {
		    
	    	String cmd = ParamUtil.getString(req, Constants.CMD);
		    if(cmd.equals(Constants.RESET)){
		    	req.removeAttribute(WebKeysUOMA.ALTA_PARITARIAS );
				req.removeAttribute(WebKeysUOMA.SUELDOS_BASICOS );
				req.removeAttribute(WebKeysUOMA.JORNALES_BASICOS );
				req.removeAttribute(Constants.SAVE);
		    }else {
		    	boolean simular = false;
			    simular = Boolean.parseBoolean(ParamUtil.getString(req, "simular"));
				Paritaria par =  new Paritaria();
				par = this.ParserAltaParitaria(req, par);
				ParitariaServiceUtil.altaParitaria(par, simular);
				req.removeAttribute(WebKeysUOMA.ALTA_PARITARIAS );
				req.setAttribute(WebKeysUOMA.ALTA_PARITARIAS ,par);
			
				if(simular == false) {
					String msg = LanguageUtil.get(defaultLocale, "insert-paritarias");
					SessionMessages.add(req, "insertCabOk");
					req.setAttribute("msgCabOk", msg);
					req.setAttribute(Constants.SAVE, Constants.SAVE);
				}
		    }
		
				
		
	}
	
	private Paritaria ParserAltaParitaria(ActionRequest req,Paritaria par) {
	    		
		par.setCatA(ParamUtil.getString(req, "importe_cat_a"));
		par.setCatB(ParamUtil.getString(req, "importe_cat_b"));
		par.setCatC(ParamUtil.getString(req, "importe_cat_c"));
		par.setCatD(ParamUtil.getString(req, "importe_cat_d"));
		par.setCatE(ParamUtil.getString(req, "importe_cat_e"));
		par.setCatJornalesA(ParamUtil.getString(req, "importe_cat_jornal_a"));
		par.setCatJornalesB(ParamUtil.getString(req, "importe_cat_jornal_b"));
		par.setCatJornalesC(ParamUtil.getString(req, "importe_cat_jornal_c"));
		par.setCatJornalesD(ParamUtil.getString(req, "importe_cat_jornal_d"));
		par.setCatJornalesE(ParamUtil.getString(req, "importe_cat_jornal_e"));
		par.setCamara(ParamUtil.getString(req, "nombre_camara"));
		String mes = ParamUtil.getString(req, "fechaDesdeMes");
		String anno = ParamUtil.getString(req, "fechaDesdeAnio");
		
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaParitaria= null;
		par.setFechaAltaParitaria(null);
		try {
			fechaParitaria = formatoDeFecha.parse("01" + "/"
					+ (Integer.parseInt(mes) ) + "/" + anno);
		} catch (Exception e) {
			fechaParitaria = null;
		}
			
		if (fechaParitaria != null) {			
			calendar.add(Calendar.MONTH, -1);
			periodoDate =DateUtils.getLastDateOfMonth(fechaParitaria, false);
			par.setFechaAltaParitaria(periodoDate);
		}
		
			
		return par;
	}
	
	private void detalleParitarias(String nombreCamara ,boolean simular, String mes , String anno,RenderRequest renderRequest) throws Exception{
	
		renderRequest.removeAttribute(WebKeysUOMA.SUELDOS_BASICOS );
		renderRequest.removeAttribute(WebKeysUOMA.JORNALES_BASICOS );
	
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	
		Date fechaParitaria= null;
		try {
			fechaParitaria = formatoDeFecha.parse("01" + "/"
					+ (Integer.parseInt(mes) ) + "/" + anno);
		} catch (Exception e) {
			fechaParitaria = null;
		}
			
		if (fechaParitaria != null) {			
			calendar.add(Calendar.MONTH, -1);
			periodoDate =DateUtils.getLastDateOfMonth(fechaParitaria, false);
		}		
		renderRequest.setAttribute(WebKeysUOMA.SUELDOS_BASICOS ,ParitariaServiceUtil.buscarHonorParitarias(nombreCamara, periodoDate, simular));
		renderRequest.setAttribute(WebKeysUOMA.JORNALES_BASICOS ,ParitariaServiceUtil.buscarHonorParitariasJornales(nombreCamara, periodoDate, simular));		
	}
	
	

}
