package ar.com.ospim.global.actions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.global.beans.AportesMonotributo;
import ar.com.ospim.global.beans.AportesMonotributoClase;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;


public class AporteMonotributoAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	Random random = new Random();
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
				
		setForward(actionRequest, "portlet.afiliado.abm_categorias_monotributo_edit");
	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		session.removeAttribute("esEdicion");
		
		AportesMonotributo aporte=null;
		Integer idAporte = 0;
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idAporte = Integer.valueOf(ParamUtil.getInteger(renderRequest,"id_aporte", 0));
			
			if(cmd.equals(Constants.WRITE) ){ 
				
				aporte = new AportesMonotributo();
				aporte.setClases(new ArrayList<AportesMonotributoClase>());
				aporte.setClasesOriginal(new ArrayList<AportesMonotributoClase>());
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysAfiliados.APORTE_EN_EDICION , aporte);
				renderRequest.setAttribute("view","");
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				return mapping.findForward(getForward(renderRequest,"portlet.afiliado.abm_categorias_monotributo_edit"));
			}
			
			if(cmd.equals(Constants.UPDATE) ){  //FALTA
				aporte = (AportesMonotributo) session.getAttribute(WebKeysAfiliados.APORTE_EN_EDICION);
				if(aporte.getId()==null) {
				   aporte.setId(idAporte);
				}   
				actualizaAporte(aporte,PortalUtil.getHttpServletRequest(renderRequest));
				if(validarAporte(aporte)) {
				   Integer idAporteL=updateAporte(aporte, user.getScreenName());
				   if(aporte.getId()==null || aporte.getId()==0) {
					   aporte.setId(idAporteL);
				   }else {
					   aporte = TraeListasServiceUtil.getAportesMonotributoById(idAporteL);
				   }
					
				   msg = "Se guardó correctamente la carga de la tabla de Aportes de Monotributo ";
				   msg = msg + " "+ idAporteL;
				   SessionMessages.add(renderRequest, "updateCabOk");
				   renderRequest.setAttribute("msgCabOk", msg);
				   _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Monotributo Aporte: " + idAporteL
				    );
				}else {
					SessionErrors.add(renderRequest, "errorAporte");
					msg = aporte.getErrorMsg();
					
					renderRequest.setAttribute("msgError", msg);	
				}
				session.setAttribute(WebKeysAfiliados.APORTE_EN_EDICION, aporte);	
			}
			
			
            if(cmd.equals(Constants.EDIT) ){
            	
           	    aporte = TraeListasServiceUtil.getAportesMonotributoById(idAporte);
            	session.setAttribute(WebKeysAfiliados.APORTE_EN_EDICION , aporte);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.afiliado.abm_categorias_monotributo_edit"));
			}
            
            
            
            if(cmd.equals("agregarClase") ){
            	aporte = (AportesMonotributo) session.getAttribute(WebKeysAfiliados.APORTE_EN_EDICION);
				agregarClase(aporte,PortalUtil.getHttpServletRequest(renderRequest));
          	    
              	session.setAttribute(WebKeysAfiliados.APORTE_EN_EDICION , aporte);
           	
		        return mapping.findForward(getForward(renderRequest,"portlet.afiliado.abm_categorias_monotributo_clases_result"));
			}
            
            if(cmd.equals("eliminarClase") ){
            	aporte = (AportesMonotributo) session.getAttribute(WebKeysAfiliados.APORTE_EN_EDICION);
				eliminarClase(aporte,PortalUtil.getHttpServletRequest(renderRequest));
				session.setAttribute(WebKeysAfiliados.APORTE_EN_EDICION , aporte);
		        return mapping.findForward(getForward(renderRequest,"portlet.afiliado.abm_categorias_monotributo_clases_result"));
			}
            
            if(cmd.equals("propagarFecha") ){
            	aporte = (AportesMonotributo) session.getAttribute(WebKeysAfiliados.APORTE_EN_EDICION);
				propagarFechas(aporte,PortalUtil.getHttpServletRequest(renderRequest));
          	    
              	session.setAttribute(WebKeysAfiliados.APORTE_EN_EDICION , aporte);
           	
		        return mapping.findForward(getForward(renderRequest,"portlet.afiliado.abm_categorias_monotributo_clases_result"));
			}
            
            
			if(cmd.equals(Constants.DELETE) ){  //FALTA
				aporte= new AportesMonotributo();
				aporte.setId(idAporte);
				EditarAfiliadoServiceUtil.deleteAporteMonotributo(aporte);
				return mapping.findForward("portlet.afiliado.abm_categorias_monotributo");
			}
			
			
            
			
		}
		return mapping.findForward("portlet.afiliado.abm_categorias_monotributo_edit");
   }
	

	private void agregarClase(AportesMonotributo aporte,HttpServletRequest renderRequest) throws SystemException{
		   SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		 
		    String clase = ParamUtil.getString(renderRequest, "clase");
		    Double importe = ParamUtil.getDouble(renderRequest,"aporte");
		    String fechaDesdeDia = ParamUtil.getString(renderRequest,"fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
			String fechaDesdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
			
			String fechaHastaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
			
			Date fechadesde = null;
			try {
				fechadesde = formatoDeFecha.parse(fechaDesdeDia + "/"
						+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
						+ fechaDesdeAnio);
			} catch (Exception e) {
				fechadesde = DateUtils.getCalendarGMTMenos3().getTime();
			}
			
			Date fechahasta = null;
			try {
				fechahasta = formatoDeFecha.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechahasta = DateUtils.getCalendarGMTMenos3().getTime();
			}
			
		    
			boolean existe=false;
			for(AportesMonotributoClase c:aporte.getClases()) {
				if(c.getClase().equals(clase) &&
				   c.getDesde().equals(fechadesde)	&&
				   c.getHasta().equals(fechahasta)) {
					c.setAporte(importe);
					existe=true;
					break;
				}
			}
			
			if(!existe) {
				 Integer xi=random.nextInt(1000)*-1;
				 AportesMonotributoClase claseAporte=new AportesMonotributoClase();
	   		     claseAporte.setAporte(importe);
	   		     claseAporte.setClase(clase);
	   		     claseAporte.setDesde(fechadesde);
	   		     claseAporte.setHasta(fechahasta);
	   		     claseAporte.setId(xi);
			     aporte.getClases().add(claseAporte);
			}
	   }
	
	
	private void eliminarClase(AportesMonotributo aporte,HttpServletRequest renderRequest) throws SystemException{
	   Integer id =ParamUtil.getInteger(renderRequest, "id");
	   List<AportesMonotributoClase>clases= new ArrayList<AportesMonotributoClase>();
	   for(AportesMonotributoClase c:aporte.getClases()) {
			if(!c.getId().equals(id)) {
					clases.add(c);
			}
	   }
	   aporte.setClases(clases); 
	}
	
	private boolean validarAporte (AportesMonotributo aporte) throws SystemException{
		boolean ret=true;
		aporte.setErrorMsg("");
		 	
		return ret;
	}
	
	
	private void actualizaAporte(AportesMonotributo aporte,HttpServletRequest renderRequest) throws SystemException{
		   SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		   
		    Integer id = ParamUtil.getInteger(renderRequest, "id_aporte");
		    
		    String fechaDesdeDia = ParamUtil.getString(renderRequest,"fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
			String fechaDesdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
			
			String fechaHastaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
			
			Date fechadesde = null;
			try {
				fechadesde = formatoDeFecha.parse(fechaDesdeDia + "/"
						+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
						+ fechaDesdeAnio);
			} catch (Exception e) {
				fechadesde = DateUtils.getCalendarGMTMenos3().getTime();
			}
			
			Date fechahasta = null;
			try {
				fechahasta = formatoDeFecha.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechahasta = DateUtils.getCalendarGMTMenos3().getTime();
			}
		    
			Double importe = 0D;
			String importeS = ParamUtil.getString(renderRequest,"aporte");
			if(importeS!=null  && !importeS.isEmpty()) {
				importeS=importeS.replace(",", ".");
				importe=Double.valueOf(importeS);
		   	}
			
			Integer categoria = ParamUtil.getInteger(renderRequest, "categoria");
			aporte.setId(id);
			aporte.setAporte(importe);
			aporte.setCategoria(categoria);
			aporte.setDesde(fechadesde);
			aporte.setHasta(fechahasta);
		   	
	   }

	
	   private Integer updateAporte(AportesMonotributo aporte, String user) throws Exception{
		Integer id = 0;
		id = EditarAfiliadoServiceUtil.updateAporteMonotributo(aporte, user);
		return id;
	   }
	
	   
	   private void propagarFechas(AportesMonotributo aporte,HttpServletRequest renderRequest) throws SystemException{
		   SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		 
		    String fechaDesdeDia = ParamUtil.getString(renderRequest,"fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
			String fechaDesdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
			
			String fechaHastaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
			
			Date fechadesde = null;
			try {
				fechadesde = formatoDeFecha.parse(fechaDesdeDia + "/"
						+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
						+ fechaDesdeAnio);
			} catch (Exception e) {
				fechadesde = DateUtils.getCalendarGMTMenos3().getTime();
			}
			
			Date fechahasta = null;
			try {
				fechahasta = formatoDeFecha.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechahasta = DateUtils.getCalendarGMTMenos3().getTime();
			}
			
		   	for(AportesMonotributoClase c:aporte.getClases()) {
				 c.setDesde(fechadesde);
	   		     c.setHasta(fechahasta);
			}
	   }
     
}