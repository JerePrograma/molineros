/**
 */

package ar.com.ospim.novedades.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.novedades.beans.NovedadEmpleadorTotal;
import ar.com.ospim.novedades.beans.NovedadTotal;
import ar.com.ospim.novedades.service.NovedadesInconsistenciaServiceUtil;
import ar.com.ospim.novedades.service.NovedadesServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * Realiza la búsqueda de afiliados según parámetros de entrada
 * 
 * @author SVA
 * 
 */
public class BuscarNovedadesAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BuscarNovedadesAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.novedades.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {			

			String cuil_titu = null;
			String cuil = null;
			String tipoDoc = null;
			String nroDoc = null;
			String apellido = null;
			String nombre = null;
			String tipoNov = null;
			String tipoOri = null;
			String fechaProc = null;
			Integer mesHasta = null;
			Integer anioHasta = null;
			String tipoNoveEmpl = null;
			String idInconsistencia =  null;
			String idProceso =  null;
			String accInconsistencia =  null;

			int cantResultados = 0;
			/*Filtro Novedades SSS */
			if (null != renderRequest.getParameter("cuil_titular")) {
				cuil_titu = renderRequest.getParameter("cuil_titular").trim().length() > 0 ? renderRequest
						.getParameter("cuil_titular") : null;
			}
			if (null != renderRequest.getParameter("cuil")) {
				cuil = renderRequest.getParameter("cuil").trim().length() > 0 ? renderRequest
						.getParameter("cuil") : null;
			}
			if (null != renderRequest.getParameter("tipoDoc")) {
				tipoDoc = renderRequest.getParameter("tipoDoc").trim().length() > 0 ? renderRequest
						.getParameter("tipoDoc") : null;
			}
			if (null != renderRequest.getParameter("nroDoc")) {
				nroDoc = renderRequest.getParameter("nroDoc").trim().length() > 0 ? renderRequest
						.getParameter("nroDoc") : null;
			}	
			
			if (null != renderRequest.getParameter("apellido")) {
				apellido = renderRequest.getParameter("apellido").trim()
						.length() > 0 ? renderRequest.getParameter("apellido")
						: null;
			}
			if (null != renderRequest.getParameter("nombre")) {
				nombre = renderRequest.getParameter("nombre").trim().length() > 0 ? renderRequest
						.getParameter("nombre") : null;
			}
			
			if (null != renderRequest.getParameter("tipoNov")) {
				tipoNov = renderRequest.getParameter("tipoNov").trim().length() > 0 ? renderRequest
						.getParameter("tipoNov") : null;
			}
			
			if (null != renderRequest.getParameter("tipoOri")) {
				tipoOri= renderRequest.getParameter("tipoOri").trim().length() > 0 ? renderRequest
						.getParameter("tipoOri") : null;
			}

			if (null != renderRequest.getParameter("fechaProc")) {
				fechaProc = renderRequest.getParameter("fechaProc").trim().length() > 0 ? renderRequest
						.getParameter("fechaProc") : null;
			}
			/*Filtro Novedades Empleadores */
			String periodoAux="";
			if (null != renderRequest.getParameter("mesHasta")) {
				periodoAux = renderRequest.getParameter("mesHasta"); 
				mesHasta = periodoAux != null ? Integer.parseInt(periodoAux) : null;
			}
			if (null != renderRequest.getParameter("anioHasta")) {
				periodoAux = renderRequest.getParameter("anioHasta"); 				
				anioHasta = periodoAux != null ? Integer.parseInt(periodoAux) : null;
			}
			if (null != renderRequest.getParameter("tipoNoveEmpl")) {
				tipoNoveEmpl = renderRequest.getParameter("tipoNoveEmpl").trim().length() > 0 ? renderRequest
						.getParameter("tipoNoveEmpl") : null;
			}
			
			if (null != renderRequest.getParameter("idInconsistencia")) {
				idInconsistencia = renderRequest.getParameter("idInconsistencia"); 				
			}
			
			if (null != renderRequest.getParameter("idProceso")) {
				idProceso = renderRequest.getParameter("idProceso"); 				
			}
			
			if (null != renderRequest.getParameter("accInconsistencia")) {
				accInconsistencia = renderRequest.getParameter("accInconsistencia"); 				
			}
			
			
			
			
			List<NovedadTotal> busqueda=null;
			List<NovedadEmpleadorTotal> busquedaEmp=null;

			
			int pagina_sel = ParamUtil.getInteger(renderRequest, "pagina", 1);
			pagina_sel--;

			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

			User user = PortalUtil.getUser(PortalUtil.getHttpServletRequest(renderRequest));
			
			session.removeAttribute(WebKeysAfiliados.BUSQUEDA_NOVEDADES_EN_SESSION);
			
//			{"AFIP", "ANSES", "EMPLEADORES", "NOVEDADES"};
			if(tipoOri.equalsIgnoreCase(WebKeysAfiliados.TIPOS_ORIGEN[2]) ){
				Calendar fechaHasta = Calendar.getInstance();
				fechaHasta.set(Calendar.YEAR, anioHasta);
				fechaHasta.set(Calendar.MONTH, mesHasta);
				fechaHasta.set(Calendar.DATE, fechaHasta.getActualMaximum(Calendar.DAY_OF_MONTH));
				
				_log.debug("Fecha Hasta: " + fechaHasta );
				
				busquedaEmp = NovedadesServiceUtil.getInstance().getNovedadesEmpleadores(fechaHasta.getTime(), tipoNoveEmpl, pagina_sel);

				if(busquedaEmp != null && busquedaEmp.size()>0){
					cantResultados = busquedaEmp.get(0).getTotal_registros();
				}else{
					cantResultados = 0;
				}

			}else if(tipoOri.equalsIgnoreCase(WebKeysAfiliados.TIPOS_ORIGEN[3]) ){
				if (!StringUtils.checkEmpty(idInconsistencia) && accInconsistencia.equalsIgnoreCase("ALTA")){//Genero una Inconsistencia
					NovedadesInconsistenciaServiceUtil.procesarInconsistencia(Integer.parseInt(idInconsistencia),Integer.parseInt(idProceso) ,user.getScreenName());
				}else if (!StringUtils.checkEmpty(idInconsistencia) && accInconsistencia.equalsIgnoreCase("BAJA") ){
					NovedadesInconsistenciaServiceUtil.bajaInconsistencia(Integer.parseInt(idInconsistencia),Integer.parseInt(idProceso) ,user.getScreenName());
				}
				
				busqueda = NovedadesServiceUtil.getInstance().getNovedades(cuil_titu, cuil, tipoDoc, nroDoc, apellido, nombre, 
						tipoNov, tipoOri, fechaProc!=null?sdf.parse(fechaProc):null, pagina_sel);
				
				if(busqueda != null && busqueda.size()>0){
					cantResultados = busqueda.get(0).getTotal_registros();
				}else{
					cantResultados = 0;
				}
			}

			if((busqueda != null && busqueda.size() > 0) ||
			   (busquedaEmp != null && busquedaEmp.size() > 0)){
				session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_NOVEDADES_TOTAL_REGISTROS, cantResultados);
				session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_NOVEDADES_OFFSET_REG, pagina_sel);
			}else{
				session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_NOVEDADES_TOTAL_REGISTROS,0 );
				session.setAttribute(WebKeysAfiliados.FILTRO_BUSQUEDA_NOVEDADES_OFFSET_REG, 0);
			}
//			{"AFIP", "ANSES", "EMPLEADORES", "NOVEDADES"};
			if(tipoOri.equalsIgnoreCase(WebKeysAfiliados.TIPOS_ORIGEN[2]) ){
				session.setAttribute(WebKeysAfiliados.BUSQUEDA_NOVEDADES_EN_SESSION, busquedaEmp);
				
				return mapping.findForward("portlet.novedades.empleadores.result.search");
				
			}else if(tipoOri.equalsIgnoreCase(WebKeysAfiliados.TIPOS_ORIGEN[3]) ){
				session.setAttribute(WebKeysAfiliados.BUSQUEDA_NOVEDADES_EN_SESSION, busqueda);
			}
			
		} catch (Exception e) {
			_log.error(e);
		}

//		popup = ParamUtil.getString(renderRequest, "popup");
//		// Busqueda con selección de checkbox
//		checkbox = ParamUtil.getString(renderRequest, "checkbox");
//		renderRequest.setAttribute("checkbox", checkbox);

//		if (null != popup && !popup.trim().equals("")) {
////FIXME			return map ping.findForward("portlet.afiliados.result.search.popup");
//		} else{	
//			return mapping.findForward("portlet.novedades.result.search");
//		}
		
		return mapping.findForward("portlet.novedades.result.search");
		
	}

}