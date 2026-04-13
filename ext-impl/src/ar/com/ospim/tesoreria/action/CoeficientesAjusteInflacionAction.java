package ar.com.ospim.tesoreria.action;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.procesaArchivos.ProcesaArchivos;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.CoeficienteAjusteInflacion;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class CoeficientesAjusteInflacionAction extends PortletAction {
	private Logger _log = Logger.getLogger(this.getClass());
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		
		String cmd=actionRequest.getParameter("cmd");
		
		if("uploadxls".equalsIgnoreCase(cmd)) {
						
			List<String>errores = new ArrayList<String>();
			
			Integer entidad=WebKeysGlobal.OSPIM;
			if(actionResponse.getNamespace().equals("_FAR_1_")){
//				entidad=WebKeysGlobal.AMTIMA;
			}else if(actionResponse.getNamespace().equals("_UOM_1_")){
				entidad=WebKeysGlobal.UOMA;
		   }
		   entidad=WebKeysGlobal.ENTIDADESUNIFICADAS; //Por cambio de criterio en calculo
		   
		   HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();

		   UploadPortletRequest uploadReq = null;
		   try {
		      uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
		   }catch(Exception xx) {}   
		
		   List<CoeficienteAjusteInflacion> det = new ArrayList<CoeficienteAjusteInflacion>();
		   try {
			if(uploadReq!=null && uploadReq.getFileName("archivo")!=null) {
			   String fileName = uploadReq.getFileName("archivo").toLowerCase();
			   Boolean proceso=false;
			   if (fileName != null) {
				File zip = uploadReq.getFile("archivo");
				if ( fileName.endsWith(".xls")) {
					proceso=true;
					det = new ProcesaArchivos().procesarCoeficientesAjusteInflacionXLS(actionRequest, zip,fileName,entidad);
					for(CoeficienteAjusteInflacion c:det) {
						ContabilidadServiceUtil.updateCoeficienteAjusteInflacion(c);
					}
				}	
			   }
			}
						
    		session.setAttribute(WebKeysTesoreria.COEFICIENTES_AJUSTE_INFLACION_EN_SESSION, det);
			
		  } catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
			if(e.getClass().getName().contains("OldExcelFormatException")) {
			   errores.add("Error en Versión de Excel - Debe ser 97-2003 ");
			}
			if(errores.size()>0) {
			   actionRequest.setAttribute("errores", errores);
			}   
		  }

		  if(det.size()==0) {
			errores.add("No se ha procesado ningún archivo"); 
			actionRequest.setAttribute("errores", errores);
		  }else {
		    if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		    }
		  }  
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		Integer entidadUnif=WebKeysGlobal.ENTIDADESUNIFICADAS; //Por cambio de criterio en calculo
		
		PortletSession portletSession = renderRequest.getPortletSession();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		CoeficienteAjusteInflacion coeficiente=null;
		Integer idPreautorizacion = 0;
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
//			idPreautorizacion = ParamUtil.getInteger(renderRequest,"id_preautorizacion", 0);
			
            if(cmd.equals("list") ){ 
            	
        		Calendar desdeEjercicio = DateUtils.getDesdeEjercicio(renderRequest, entidad);
        		Calendar hastaEjercicio = DateUtils.getHastaEjercicio(renderRequest, entidad);
        		
        		String ejercicio=ParamUtil.getString(renderRequest, "ejercicio");
        		
        		portletSession.removeAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
        		portletSession.setAttribute("ejercicio_seleccionado", ejercicio, PortletSession.PORTLET_SCOPE);

        		renderRequest.setAttribute("ejercicio_desde",
        				format.format(desdeEjercicio.getTime()));
        		renderRequest.setAttribute("ejercicio_hasta",
        				format.format(hastaEjercicio.getTime()));
        		Integer dde = Integer.parseInt(sdf.format(desdeEjercicio.getTime()));
        		Integer hta = Integer.parseInt(sdf.format(hastaEjercicio.getTime()));
        		
        		Map<Integer,BigDecimal>coeficientesMap = ContabilidadServiceUtil.getCoeficientesAjustesInflacion(entidadUnif,dde, hta);
        		List<CoeficienteAjusteInflacion> coeficientes = new ArrayList<CoeficienteAjusteInflacion>();
        		
        		for (Map.Entry<Integer, BigDecimal> entry : coeficientesMap.entrySet()) {
        			CoeficienteAjusteInflacion coef= new CoeficienteAjusteInflacion();
        			coef.setCoeficiente(entry.getValue());
        			coef.setEntidad(entidadUnif);
        			coef.setPeriodo(entry.getKey());
        		    coeficientes.add(coef);
        		}
        		portletSession.removeAttribute(WebKeysTesoreria.COEFICIENTES_AJUSTE_INFLACION_EN_SESSION, PortletSession.APPLICATION_SCOPE);			
        		portletSession.setAttribute(WebKeysTesoreria.COEFICIENTES_AJUSTE_INFLACION_EN_SESSION, coeficientes,PortletSession.APPLICATION_SCOPE);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.coeficientes_ajuste_inflacion_result"));
			}
			
			if(cmd.equals("new") ){ 
				
				coeficiente = new CoeficienteAjusteInflacion();
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysTesoreria.COEFICIENTE_EN_EDICION , coeficiente);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.coeficientes_ajuste_inflacion_edit"));
			}
			
            if(cmd.equals("edit") ){ 
            	Integer periodoMesAnio = ParamUtil.getInteger(renderRequest,
						"periodo");
				coeficiente = new CoeficienteAjusteInflacion();
				String coefStr = ParamUtil.getString(renderRequest,"coeficiente");
				
				BigDecimal coef = new BigDecimal(coefStr);
				coeficiente.setCoeficiente(coef);
				coeficiente.setEntidad(entidadUnif);
				coeficiente.setPeriodo(periodoMesAnio);
				
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysTesoreria.COEFICIENTE_EN_EDICION , coeficiente);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.coeficientes_ajuste_inflacion_edit"));
			}
			
			if(cmd.equals("save") ){ 
				String periodoMesAnio = ParamUtil.getString(renderRequest,
						"periodoMesAnio");
				
				String coefStr = ParamUtil.getString(renderRequest,"coeficiente");
				BigDecimal coef = new BigDecimal(coefStr);

				Integer periodo = null;
				try {
					periodo = Integer.parseInt(periodoMesAnio.split("_")[1])*100 + 
							  Integer.parseInt(periodoMesAnio.split("_")[0]) + 1;
				} catch (Exception e) {
					periodo = null;
				}
				 
				CoeficienteAjusteInflacion c = new CoeficienteAjusteInflacion();
				c.setEntidad(entidadUnif);
				c.setPeriodo(periodo);
				c.setCoeficiente(coef);
				ContabilidadServiceUtil.updateCoeficienteAjusteInflacion(c);
				
				msg = "Se ha actualizado el coeficiente del período ";
				msg = msg + " "+ periodo;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				
				session.setAttribute(WebKeysTesoreria.COEFICIENTE_EN_EDICION , c);
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.coeficientes_ajuste_inflacion_edit"));
			}
			
			if(cmd.equals("delete") ){ 
            	Integer periodoMesAnio = ParamUtil.getInteger(renderRequest,
						"periodo");
				coeficiente = new CoeficienteAjusteInflacion();
				
				coeficiente.setEntidad(entidadUnif);
				coeficiente.setPeriodo(periodoMesAnio);
				
				ContabilidadServiceUtil.deleteCoeficienteAjusteInflacion(coeficiente);
				
				List<CoeficienteAjusteInflacion> coeficientes=(List<CoeficienteAjusteInflacion>) portletSession
						.getAttribute(WebKeysTesoreria.COEFICIENTES_AJUSTE_INFLACION_EN_SESSION,
							PortletSession.APPLICATION_SCOPE);
				boolean elimino =coeficientes.remove(coeficiente);
				portletSession.removeAttribute(WebKeysTesoreria.COEFICIENTES_AJUSTE_INFLACION_EN_SESSION, PortletSession.APPLICATION_SCOPE);			
        		portletSession.setAttribute(WebKeysTesoreria.COEFICIENTES_AJUSTE_INFLACION_EN_SESSION, coeficientes,PortletSession.APPLICATION_SCOPE);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.coeficientes_ajuste_inflacion_result"));
			}
		
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.coeficientes_ajuste_inflacion_list"));
	}

}
