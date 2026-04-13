package ar.com.ospim.autorizaciones.action;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import ar.com.ospim.autorizaciones.beans.Cartilla;
import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.NomencladorPlan;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.autorizaciones.services.CartillaServiceUtil;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.CabeceraCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarCartillaAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		PortletSession portletSession = renderRequest.getPortletSession();
		User user = PortalUtil.getUser(renderRequest);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		Cartilla cartilla=null;
		int idCartilla = 0;
		String msg = "";
		if (!StringUtils.checkEmpty(cmd)) {
			idCartilla = ParamUtil.getInteger(renderRequest,"id_cartilla", 0);
			
           
            if (cmd.equals("eliminaCartilla")) { 
            	session.setAttribute("esPopUp","N");
            	
            	Integer dia = ParamUtil.getInteger(renderRequest, "dia");
            	Integer mes = ParamUtil.getInteger(renderRequest, "mes")+1;
            	Integer anio = ParamUtil.getInteger(renderRequest, "anio");
            	
            	Calendar date = Calendar.getInstance();
            	date.set(Calendar.YEAR, anio);
           	    date.set(Calendar.MONTH, mes);
           	    date.set(Calendar.DAY_OF_MONTH, dia);
           	    Date fechaBaja = date.getTime();
           	    
           	    CartillaServiceUtil.eliminaCartilla( idCartilla, user.getScreenName(), fechaBaja);
            	
            	List<Cartilla>ln= (List<Cartilla>) session.getAttribute("CartillasLista");
           	    for(Cartilla n:ln){
            		if(n.getId()==idCartilla){
            		   n.setBajaFecha(fechaBaja);
            		}
            	}
           	
            	session.setAttribute("CartillasLista",ln);
            	
				msg = LanguageUtil.get(defaultLocale, "delete-cartilla");
				msg = msg + " " +idCartilla;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id cartilla: " + idCartilla
						);
				return mapping.findForward("portlet.autorizaciones.buscar_cartilla");
			}
  
            
            if(cmd.equals(Constants.DELETE) ){ // Posibilita carga motivo de baja
            	session.setAttribute("esPopUp","N");
            	
            	cartilla = CartillaServiceUtil.getCartillaById((int)idCartilla);
            			
            	session.setAttribute(WebKeysAutorizaciones.CARTILLA_EN_EDICION, cartilla);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id cartilla: " + idCartilla
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.eliminar_cartilla"));
			}

            
            if (cmd.equals(Constants.RESTORE)) { 
            	session.setAttribute("esPopUp","N");
            	CartillaServiceUtil.recuperaCartilla((int)idCartilla, user.getScreenName());
            	
            	List<Cartilla>ln= (List<Cartilla>) session.getAttribute("CartillasLista");
           	    for(Cartilla n:ln){
            		if(n.getId()==idCartilla){
            		   n.setBajaFecha(null);	
           		}
            	}
            	session.setAttribute("CartillasLista",ln);
            	
				msg = LanguageUtil.get(defaultLocale, "restore-cartilla");
				msg = msg +" "+ idCartilla;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id cartilla: " + idCartilla
						);
				return mapping.findForward("portlet.autorizaciones.buscar_cartilla");
			}
			
		}
		
		return mapping.findForward("portlet.autorizaciones.buscar_cartilla");
	}
	
  
}
